import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.stream.IntStream;

class Table {
	int[][] table;
	int[][] tmp;

	public Table(int x, int y){
		table = new int[x][y];
		tmp = new int[x][y];
		for (int i = 0; i < x; i++){
			for (int j = 0; j < y; j++)
				table[i][j] = new Random().nextInt(2);
		}
	}
	public Table(int[][] table){
		this.tmp = new int[table.length][table[0].length];
		this.table = copyTable(table);
	}
	public void print(){
		System.out.print("\033[H");
		System.out.print("\033[2J");
		for (int[] ints : table) {
			for (int anInt : ints) System.out.printf("%s%s",(anInt == 1) ? "\033[31m" : "\033[34m" ,anInt);
			System.out.println("\033[0m");
		}
	}
	private int evalY(int x, int y){
		int tmp = 0;
		if (x > 0 && table[x -1][y] == 1)
			tmp++;
		if (x < table.length - 1 && table[x + 1][y] == 1)
			tmp++;
		return tmp;
	}
	private int getCell(int x, int y){
		int count = 0;
		count += evalY(x, y);
		if (y > 0){
			count += table[x][y - 1];
			count += evalY(x, y - 1);
		}
		if (y < table[0].length - 1){
			count += table[x][y + 1];
			count += evalY(x, y + 1);
		}
		return (table[x][y] == 1) ? ((count == 2 || count == 3) ? 1 : 0) : ((count == 3) ? 1 : 0);
	}
	private int[][] copyTable(int[][] table)
	{
		int lengthY = table[0].length;
		int[][] cp = new int[table.length][lengthY];
		for (int i = 0 ; i < table.length; ++i)
			System.arraycopy(table[i], 0, cp[i], 0, lengthY);
		return cp;
	}
	public void eval(){
		int lengthX = table.length;
		int lengthY = table[0].length;
		for (int i = 0; i < lengthX; i++)
			for (int j = 0; j < lengthY; j++)
				tmp[i][j] = getCell(i, j);
		table = copyTable(tmp);
	}
}

public class Main{
	public static void main(String[] args) throws IOException {
		Table table;
		List<String> list = new ArrayList<>();
		setParams(args, list);
		table = getTable(list);
		while (true){
			table.print();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			table.eval();
		}
	}
	private static void setParams(String[] args, List<String> list){
		if (args.length == 0 || args.length > 2){
			printDiscribe();
			Scanner scanner = new Scanner(System.in);
			list.addAll(new ArrayList<>(Arrays.asList(scanner.nextLine().trim().split("\\s++"))));
			while (list.size() > 2)
			{
				printDiscribe();
				list.clear();
				list.addAll(Arrays.asList(scanner.nextLine().trim().split("\\s++")));
			}
		}
		else
			list.addAll(Arrays.asList(args));
	}
	private static void printDiscribe(){
		System.out.println("\nenter the size using two non negative integer parameters, example:\n12 2\nor");
		System.out.println("file name containing a matrix of non negative integers, example:\ntest.text");
	}
	private static Table getTable(List<String> list) throws FileNotFoundException {
		int x;
		int y;
		int[][] intArray;
		List<List<Integer>> array = new ArrayList<>();
		if (list.size() == 1){
			File file = new File(list.get(0));
			if (file.isFile()){
				Scanner scanner = new Scanner(file);
				while (scanner.hasNext()){
					try {
						array.add(Arrays.asList(IntStream.of(Arrays.stream(scanner.nextLine().trim().split("\\s++")).mapToInt(Integer::parseInt).toArray()).boxed().toArray(Integer []::new)));
					}catch (Exception e){
						fileContentError();
					}
				}
				if (array.size() == 0)
					fileContentError();
				intArray = new int[array.size()][array.get(0).size()];
				for (int i = 0; i < array.size(); i++){
					if (i > 0 && array.get(i).size() != array.get(i - 1).size())
						fileContentError();
					for (int j = 0; j < array.get(i).size(); j++){
						if ((intArray[i][j] = array.get(i).get(j)) < 0 || intArray[i][j] > 1)
							fileContentError();
					}
				}
				return new Table(intArray);
			}
			System.out.println("bad argument");
			System.exit(0);
		}
		else
		{
			try {
				x = Integer.parseInt(list.get(0));
				y = Integer.parseInt(list.get(1));
				if(x <= 0 || y <= 0){
					System.out.println("bad argument");
					System.exit(0);
				}
				return new Table(x, y);
			}catch (Exception e){
				System.out.println("the incorrect arguments of the matrix");
				System.exit(0);
			}
		}
		return null;
	}
	private static void fileContentError(){
		System.out.println("incorrect file contents");
		System.exit(0);
	}
}

6