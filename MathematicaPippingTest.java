import java.util.*;
import java.io.*;

public class MathematicaPippingTest
{
	private Scanner sc;
	private LinkedList<Integer> gaussList;
	private PrintWriter writer = null;
	
	public MathematicaPippingTest(String fname) throws IOException, InterruptedException
	{
		sc = new Scanner(new File(fname));

		gaussList = new LinkedList<Integer>();
		String gaussString;
		int size;

		MathematicaAdapter ma = new MathematicaAdapter();

		gaussString = sc.nextLine();



		// gaussString = "-1, -3, -4, 1, -2, 3, 4, 2";
		// gaussString = "-1, -2, -3, -4, -5, 1, 2, 3, 4, 5";

		ma.drawArcPresentation(gaussString);
		ma.drawPlanarDiagram(gaussString);


		// read in the code from file fname
		// while (sc.hasNextLine())
		// {
		// 	gaussString = sc.nextLine();

		// 	for (String s: gaussString.split("[, ]+"))
		// 	{
		// 		int num = Integer.parseInt(s);

		// 		gaussList.addLast(num);

  //    		}
  //    	}

  //    	size = gaussList.size();

  //    	writer = new PrintWriter("temp.txt");

  //   	String s;

  //    	for (int i = 0; i < size; i++)
  //    	{
  //    		s = "" + gaussList.get(i); 
  //    		System.out.println(s);
  //    		writer.write(s + " ");
  //    	}

  //    	writer.close();

  //    	Runtime r = Runtime.getRuntime();
		// Process p = r.exec("./arcTest.m temp.txt");

		// gaussList.clear();

    }

 	public static void main(String[] args) throws IOException, InterruptedException
 	{
 		MathematicaPippingTest m = new MathematicaPippingTest(args[0]);
 	}

}