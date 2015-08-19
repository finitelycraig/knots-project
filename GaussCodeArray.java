import java.util.*;

public class GaussCodeArray// implements Comparable<GaussCodeArray>
{
	private int size;
	private int[] gaussCode;

	public GaussCodeArray()
	{
		// nothing to see here
	}

	public GaussCodeArray(int s)
	{
		gaussCode = new int[s];
		size = s;
	}

	//fix this so it throws an exception if its too full
	public void add(int letter)
	{
		for (int i = 0; i < size; i++)
		{
			if (gaussCode[i] == 0)
			{
				gaussCode[i] = letter;
				i = size;
			}
		}
	}

	public int getLetterAt(int i)
	{
		return gaussCode[i];
	}

	public int getSize()
	{
		return size;
	}

	public void clear()
	{
		for (int i = 0; i < size; i++)
		{
			gaussCode[i] = 0;
		}
	}

	// public int compareTo(GaussCodeArray that)
	// {
	// 	System.out.println("running compareTo");
	// 	int toReturn = 0;

	// 	if (this.size != that.getSize())
	// 	{
	// 		//throw some exception
	// 		System.out.println("this gaussCode isn't the same length as that gaussCode");
	// 	}
	// 	else
	// 	{
	// 		System.out.println("in the else");
	// 		for (int i = 0; i < this.size; i++)
	// 		{
	// 			System.out.println("in the for!");
	// 			if (this.getLetterAt(i) < that.getLetterAt(i))
	// 			{
	// 				toReturn = -1;
	// 				i = size;
	// 			}
	// 			else if (this.getLetterAt(i) > that.getLetterAt(i))
	// 			{
	// 				toReturn = 1;
	// 				i = size;
	// 			}
	// 		}	
	// 	}

	// 	return toReturn;
	// }
	
	public String toString()
	{
		String output = "";

		for (int i = 0; i < size; i++)
		{
			output = output + gaussCode[i] + ", ";
		}

		return output;
	}
}