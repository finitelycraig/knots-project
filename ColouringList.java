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
		int size = knot.size();
		this.list = new Node[size];


		//add all the crossings from the knot to the list
		addCrossings();
	}

	//adds all crossings from the knot into the list, does more work than it should TODOd
	public void addCrossings()
	{
		Knot.WalkIterator walk = knot.walk();

		while (walk.hasNext())
		{
			Knot.Crossing crossing = (Knot.Crossing) walk.next();

			addCrossing(crossing);
		}
	}

	public void addCrossing(Knot.Crossing cross)
	{
		boolean alreadyInList = false;

		Knot.Crossing crossing;

		for(int i = 0; i < size; i++) 
		{
			crossing = list[i].getCrossing();

			if (list[i].getCrossing() == cross)
			{
				alreadyInList = true;
				i = this.knot.size();
			}
		}

		if (!alreadyInList)
		{
			for (int i = 0; i < size; i++)
			{
				crossing = list[i].getCrossing();

				if (crossing == null)
				{
					list[i].setCrossing(cross);
				}
			}
		}
	}

	// where walkPos is the position of the arc in the walk
	public void pushUnder(Knot.Crossing cross, int walkPos)
	{
		Knot.Crossing crossingFromList;

		for (int i = 0; i < size; i++)
		{
			crossingFromList = list[i].getCrossing();
			if (cross == crossingFromList)
			{
				System.out.println("Actually added something");
				list[i].addUnder(walkPos);
				i = knot.size();
			}
		}
	}

	// where walkPos is the position of the arc in the walk
	public void pushOver(Knot.Crossing cross, int walkPos)
	{
		Knot.Crossing crossingFromList;

		for (int i = 0; i < size; i++)
		{
			crossingFromList = list[i].getCrossing();
			if (cross == crossingFromList)
			{
				System.out.println("Actually added something");
				list[i].addOver(walkPos);
				i = knot.size();
			}
		}
	}

	public int popUnder(Knot.Crossing cross)
	{
		Knot.Crossing crossingFromList;
		int walkPos = -1; //set this to a value that doesn't correspond to a crossing orientation

		for (int i = 0; i < size; i++)
		{
			crossingFromList = list[i].getCrossing();
			if (cross == crossingFromList)
			{
				walkPos = list[i].popUnder();
				i = size;
			}
		}

		return walkPos;
	}

	public int popUnder(int i)
	{
		return list[i].popUnder();
	}

	public int popOver(Knot.Crossing cross)
	{
		Knot.Crossing crossingFromList;
		int walkPos = -1; //set this to a value that doesn't correspond to a crossing orientation

		for (int i = 0; i < size; i++)
		{
			crossingFromList = list[i].getCrossing();
			if (cross == crossingFromList)
			{
				walkPos = list[i].popOver();
				i = size;
			}
		}

		return walkPos;
	}

	public int popOver(int i)
	{
		if ((i > 0) || i > size)
		{
			throw new NoSuchElementException();
		}
		return list[i].popOver();
	}

	public Iterator iterator()
    {
    	return new LRIterator();
    }

	private class Node
	{
		private Knot.Crossing crossing;
		private Stack<Integer> unders;
		private Stack<Integer> overs;

		public Node()
		{
			this.crossing = null;
			unders = new Stack<Integer>();
			overs = new Stack<Integer>();
		}

		public void setCrossing(Knot.Crossing cross)
		{
			this.crossing = cross;
		}

		public void addUnder(int walkPos)
		{
			unders.push(walkPos);
			System.out.println(unders.peek() + " ... peeking");
		}

		public void addOver(int walkPos)
		{
			overs.push(walkPos);
			System.out.println(overs.peek() + " ... peeking");
		}

		public int popUnder()
		{
			return unders.pop();
		}

		public int popOver()
		{
			return overs.pop();
		}

		public Knot.Crossing getCrossing()
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
