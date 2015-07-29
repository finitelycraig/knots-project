import java.util.*;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ColouringList
{
	private ColouringList.Node[] list;
	private Knot knot;
	private int size;

	public ColouringList(Knot k)
	{
		this.knot = k;
		size = knot.size();
		// System.out.println("Size of this knot is " + size);
		this.list = new Node[size];

		for(int i = 0; i < size; i++)
		{
			if (list[i] == null)
			{
				// System.out.println("The " + i + " in this list is null");
			}
		}

		// System.out.println(" *** ColouringList constructor.");
		//add all the crossings from the knot to the list
		addCrossings();
		// System.out.println("Finished adding crossings");

		// System.out.println("Size of the list is " + list.length);
	}

	//adds all crossings from the knot into the list, does more work than it should TODOd
	public void addCrossings()
	{
		Knot.WalkIterator walk = knot.walk();

		while (walk.hasNext())
		{
			Knot.Crossing crossing = (Knot.Crossing) walk.next();


			addCrossing(crossing.getOrderAdded());
		}

		// System.out.println("These crossings are added to the ColouringList: ");

		// System.out.println("Size in addCrossings() " + size);
		for(int i = 0; i < size; i++)
		{
			// System.out.println("this crossing has been added to each... " + list[i].getCrossing() + "");
		}


	}

	public void addCrossing(int cross)
	{
		// System.out.println("Cross " + cross);
		boolean alreadyInList = false;
		boolean success = false;

		// Knot.Crossing crossing;
		int crossing;

		// System.out.println("Size in addcrossing() " + size);


		for(int i = 0; i < size; i++) 
		{
			// System.out.println("i " + i);

			if (list[i] == null)
			{
					list[i] = new Node();
			}

			crossing = list[i].getCrossing();

			if (crossing == cross)
			{
				alreadyInList = true;
				i = size;
			}
		}

		// System.out.println("Cross again " + cross);

		int c = cross;

		// System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>.. c is " + c);

		if (!alreadyInList)
		{
			// System.out.println("!alreadyInList");
			for (int i = 0; i < size; i++)
			{
				// System.out.println("Got in the for loop");
				crossing = list[i].getCrossing();

				// System.out.println("Crossing here was " + crossing);

				if (crossing == -1 && !success)
				{
					// System.out.println("Got in the inner if ");
					list[i].setCrossing(c);

					success = true;
				}
			}
		}
	}

	// where walkPos is the position of the arc in the walk
	public void pushUnder(int cross, int walkPos)
	{
		int crossingFromList;

		for (int i = 0; i < size; i++)
		{
			crossingFromList = list[i].getCrossing();

			if (cross == crossingFromList)
			{
				// System.out.println("Actually added something");
				list[i].addUnder(walkPos);
				i = size;
			}
		}
	}

	// where walkPos is the position of the arc in the walk
	public void pushOver(int cross, int walkPos)
	{
		int crossingFromList;

		for (int i = 0; i < size; i++)
		{
			crossingFromList = list[i].getCrossing();

			if (cross == crossingFromList)
			{
				// System.out.println("Actually added something");
				list[i].addOver(walkPos);
				i = size;
			}
		}
	}

	// public int popUnder(int cross)
	// {
	// 	int crossingFromList;
	// 	int walkPos = -1; //set this to a value that doesn't correspond to a crossing orientation

	// 	for (int i = 0; i < size; i++)
	// 	{
	// 		crossingFromList = list[i].getCrossing();
	// 		if (cross == crossingFromList)
	// 		{
	// 			walkPos = list[i].popUnder();
	// 			i = size;
	// 		}
	// 	}

	// 	return walkPos;
	// }

	public int popUnder(int i)
	{
		if ((i < 0) || i > size)
		{
			throw new NoSuchElementException();
		}

		return list[i].popUnder();
	}

	// public int popOver(Knot.Crossing cross)
	// {
	// 	Knot.Crossing crossingFromList;
	// 	int walkPos = -1; //set this to a value that doesn't correspond to a crossing orientation

	// 	for (int i = 0; i < size; i++)
	// 	{
	// 		crossingFromList = list[i].getCrossing();
	// 		if (cross == crossingFromList)
	// 		{
	// 			walkPos = list[i].popOver();
	// 			i = size;
	// 		}
	// 	}

	// 	return walkPos;
	// }

	public int popOver(int i)
	{
		if ((i < 0) || i > size)
		{
			throw new NoSuchElementException();
		}

		return list[i].popOver();
	}

	public int size()
	{
		return size;
	}

	public Iterator iterator()
    {
    	return new LRIterator();
    }

	private class Node
	{
		private int crossing;
		private Stack<Integer> unders;
		private Stack<Integer> overs;

		public Node()
		{
			this.crossing = -1;
			unders = new Stack<Integer>();
			overs = new Stack<Integer>();
		}

		public void setCrossing(int cross)
		{
			// System.out.println("Cross in node " + cross);
			this.crossing = cross;
		}

		public void addUnder(int walkPos)
		{
			unders.push(walkPos);
			// System.out.println(unders.peek() + " ... peeking");
		}

		public void addOver(int walkPos)
		{
			overs.push(walkPos);
			// System.out.println(overs.peek() + " ... peeking");
		}

		public int popUnder()
		{
			return unders.pop();
		}

		public int popOver()
		{
			return overs.pop();
		}

		public int getCrossing()
		{
			return this.crossing;
		}
	}

	private class LRIterator implements Iterator
	{
		private int position = 0;

		public LRIterator()
		{
			position = 0;
		}

		public boolean hasNext()
		{
			return (position < size);
		}

		public Node next()
		{
			if (position >= size)
			{
				throw new NoSuchElementException();
			}

			return list[position++];
		}

		public void remove()
		{
			// not implemented 
		}
	}
}
