import java.util.*;
import java.io.*;

public class MathematicaPippingTest
{
	private Scanner sc;
	private LinkedList<Integer> gaussList;
	private PrintWriter writer = null;
	
	public MathematicaPippingTest(String fname) throws IOException 
	{
		sc = new Scanner(new File(fname));

		gaussList = new LinkedList<Integer>();
		String guassString;
		int size;

		// read in the code from file fname
		while (sc.hasNextLine())
		{
			guassString = sc.nextLine();

			for (String s: guassString.split("[, ]+"))
			{
				int num = Integer.parseInt(s);

				gaussList.addLast(num);

     		}
     	}

     	size = gaussList.size();

     	writer = new PrintWriter("temp.txt");

    	String s;

     	for (int i = 0; i < size; i++)
     	{
     		s = "" + gaussList.get(i); 
     		System.out.println(s);
     		writer.write(s + " ");
     	}

     	writer.close();

     	Runtime r = Runtime.getRuntime();
		Process p = r.exec("./arcTest.m temp.txt");

    }

 	public static void main(String[] args) throws IOException
 	{
 		MathematicaPippingTest m = new MathematicaPippingTest(args[0]);
 	}

}