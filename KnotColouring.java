import java.util.*;
import java.io.*;

// The following java program runs an experiement to determine which knots, up to 10 crossings
// are colourable with 3, 5 and 7 colours.
// Each knot's Gauss code is read in from a file, where it is in the form
//
// -1, 3, -2, 1, -3, 2
//
// -- this being the trefoil.
//
// The cpu time taken and node count for each colouring for each knot is also given, regardless of success.

public class KnotColouring
{
	private Scanner sc;
	private LinkedList<Integer> gaussList;
	
	public KnotColouring(String fname, int modP) throws IOException 
	{
		sc = new Scanner(new File(fname));

		gaussList = new LinkedList<Integer>();
		String guassString;
		Knot knot;
		int size;

		while (sc.hasNextLine())
		{
			guassString = sc.nextLine();

			for (String s: guassString.split("[, ]+"))
			{
				int num = Integer.parseInt(s);

				// num = zeroBase(num);

				gaussList.addLast(num);

				// System.out.print("" + num + " ");
     		}

     		knot = new AdjSetKnot();
			size = gaussList.size();

     		

     		for (int i = 0; i < size; i++)
     		{
     			int n = gaussList.get(i);

				System.out.print("" + gaussList.get(i) + " ");

			}


				System.out.println("");

     		for (int i = 0; i < (size / 2); i++)
     		{
				knot.addCrossing("" + i);
     		}

     		for (int i = 0 ; i < size; i++ ) 
     		{
     			int n = gaussList.get(i);
     			int m;
     			if (i == (size -1))
     			{
     				m = gaussList.get(0);
     			}
     			else
     			{
     				m = gaussList.get(i + 1);
     			}
     			
     			Knot.Crossing source = knot.getByOrderAdded(Math.abs(n));
     			Knot.Crossing target = knot.getByOrderAdded(Math.abs(m));

     			knot.addArc(source, target, orient(n), orient(m));
     		}

     		Colourist colourist = new Colourist(knot, modP);

     		colourist.isColourable();

			Knot.WalkIterator walk = knot.walk();
			Knot.Crossing crossing;
			int knotsVisited = 0;
			int name = (Integer.parseInt(knot.getFirstCrossing().getName()) + 1);

			while (walk.hasNext())
			{
				knotsVisited++;
				crossing = (Knot.Crossing) walk.next();
			}

     		gaussList.clear();
    	}


	}

	public void skip(int k)
    {
    	for (int i=0;i<k;i++) 
    	{
    		sc.next();
    	}
    }

    public int zeroBase(int n)
    {
    	if (n == 0)
    	{
    		System.out.println("already a zero");
    	}
    	else if (n > 0)
    	{
    		n = n - 1;
    	}
    	else
    	{
    		n = n + 1;
    	}

    	return n;
    }

    public int orient(int n)
    {
    	int orient;

    	if (n < 0)
    	{
    		orient = Knot.UNDER;
    	}
    	else
    	{
    		orient = Knot.OVER;
    	}

    	return orient;
    }

	public static void main(String[] args) throws IOException
	{
		KnotColouring catk = new KnotColouring(args[0], Integer.parseInt(args[1]));
	}
}
