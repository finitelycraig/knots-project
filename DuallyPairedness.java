import java.util.*;

public class DuallyPairedness
{
	public static void main(String[] args) 
	{
		// james' test case that is paired
		// int[] thing = new int[] {1, 1, 2, 2, 3, 4, 5, 3, 4, 6, 6, 5};
		//something that isn't paired
		// int[] thing = new int[] {1, 3, 4, 2, 1, 5, 2, 3, 5, 4};
		int[] thing = new int[] {1, 2, 3, 1, 4, 3, 2, 4};
		int[] wStar = gcStar(thing);
		if (isDuallyPaired(wStar))
		{
			System.out.println("dually paired");
		}
		else
		{
			System.out.println("not dualy paired");
		}
	}

	public static int[] gcStar(int[] gc)
	{
		int[] star = gc;

		// for each letter in gc, change the order of all other letters inbetween its occurences
		// for example, 1, 2, 3, 1, 4, 3, 2, 4 becomes 1, 3, 2, 1, 4, 3, 2, 4 becomes 
		// 1, 3, 2, 3, 4, 1, 2, 4 becomes 1, 3, 4, 1, 2, 3, 2, 4 becomes 1, 3, 4, 2, 3, 2, 1, 4

		int first = -1;
		int second = -1;
		Stack<Integer> stack = new Stack<Integer>();
		// for each letter
		for (int i = 1; i < star.length/2 + 1 ; i++)
		{	
			// System.out.println("i = " + i);
			//find the first index
			first = -1;
			second = -1;
			for (int j = 0; j < star.length; j++)
			{
				if (star[j] == i && first == -1)
				{
					// System.out.println("got in if");
					first = j;
					// System.out.println("first = " + first);
				}
				else if (star[j] == i && first != -1) //star[j] = i after we've set first
				{
					// System.out.println("got in else");
					second = j;
					// System.out.println("second = " + second);

				}
			}

			// int blah = first + 1;
			// System.out.println("BLAH = " + blah);
			// pass over the array pushing everything between first and second onto the stack
			
			if (second - first == 1)
			{
				// do nothing
			}
			else
			{
				int start = first + 1;
				for (int j = start; j < second; j++)
				{
					// System.out.println("first for j = " + j);
					// System.out.println("filling stack with " + star[j]);
					stack.push(star[j]);
				}

				// System.out.println("stack size = " + stack.size() + stack.peek());
				// pass over the array again, poping entries from the stack into the array
				for (int j = start; j < second; j++)
				{
					// System.out.println("second " + second);
					// System.out.println("j = " + j);
					star[j] = stack.pop();
				}

				for (int j = 0; j < star.length; j++)
				{
				System.out.print(star[j] + ", ");
				}
				System.out.println();
			}

			stack.clear();
		}
		return star;
	}

	// 
	public static boolean isDuallyPaired(int[] star)
	{
		// set up the adj matrix
		int size = star.length/2;
		// if there's an arc between nodes i and j then graph[i][j] = 1, 0 otherwise
		int[][] graph = new int[size][size];

		int PINK = 1;
		int BLUE = 0;
		// colour[i] = 0 means blue, colour[i] = 1 means pink
		int[] colour = new int[size];

		//add the nodes of the graph to a list of not yet visited nodes, to be popped when visited
		ArrayDeque<Integer> notVisited = new ArrayDeque<Integer>(size);
		for (int i = 0 ; i < size; i++ ) 
		{
			//add i to the ith index, since we can't remove an int from an array list because java thinks we're giving it an index 
			notVisited.push(i);
		}

		System.out.println("Initial list size = " + notVisited.size());

		int first;
		int second;
		// clear all the colours
		for (int i = 0; i < size; i++)
		{
			colour[i] = -1;
		}

		for (int i = 1; i < size + 1 ; i++)
		{	
			System.out.print("i = " + i + " ... ");

			first = -1;
			second = -1;
			for (int j = 0; j < size*2; j++)
			{
				if (star[j] == i && first == -1)
				{
					// System.out.println("got in if");
					first = j;
					// System.out.println("first = " + first);
				}
				else if (star[j] == i && first != -1) //star[j] = i after we've set first
				{
					// System.out.println("got in else");
					second = j;
					// System.out.println("second = " + second);

				}
			}

			// int blah = first + 1;
			// System.out.println("BLAH = " + blah);
			// pass over the array pushing everything between first and second onto the stack
			
			if (second - first == 1)
			{
				System.out.println("do nothing");
			}
			else
			{
				int start = first + 1;
				int appearancesBetweenLetter[] = new int[size];
				// System.out.println("start " + start + " second "+ second);
				for (int j = start; j < second; j++)
				{
					// System.out.println(j +"");
					// System.out.println(star[j] + " is between " + j);
					appearancesBetweenLetter[star[j] -1]++;
				}

				for (int j = 0; j < size; j++)
				{
					System.out.print(appearancesBetweenLetter[j] + " ");
				}

				// finally we get round to setting up the adj matrix
				for (int j = 0; j < size; j++)
				{
					if (appearancesBetweenLetter[j] == 1)
					{
						graph[i-1][j] = 1;
						graph[j][i-1] = 1;
					}
				}
				
			}

			System.out.println();
		}
		for (int i = 0; i < size; i++)
		{
			for (int j = 0; j < size; j++)
			{
				System.out.print(graph[i][j] + " ");
			}
			System.out.println();
		}

		// just start from zero
		colour[0] = 0;

		ArrayDeque<Integer> queue = new ArrayDeque<Integer>();

		queue.add(0);

		for (int i = 0; i < size ; i++)
		{
			System.out.print(" " + colour[i]);
		}

		System.out.println();
		int index = 0;
		while (queue.peek() != null || !notVisited.isEmpty())
		{
			//take things from notVisited if the queue becomes empty before all nodes have been visited
			if (queue.peek() == null)
			{
				queue.add(notVisited.removeFirst());
			}
			int u = queue.removeFirst();

			notVisited.remove(u);
			System.out.println("u = " + u);


			for (int j = 0; j < size; j++)
			{
				if (graph[u][j] == 1 && colour[j] == -1)
				{
					// System.out.println("got in big if");
					// System.out.println("colour u = " + colour[u]);
					if (colour[u] == 0)
					{
						colour[j] = 1;
					}
					else if (colour[u] == 1)
					{
						colour[j] = 0;
						queue.add(j);
					}
					else //we get in here if the opening choice isn't connected
					{
						colour[j] = 0;
						// System.out.println("Something has gone terribly wrong");
					}
				}
				else if (graph[u][j] == 1 && colour[j] == colour[u])
				{
					System.out.println(" j = " + j + " colour " + colour[j]);
					return false;
				}
			}
		}

		// if we got through all that then things are cooooooel
		return true;
	}
}