import java.util.*;

public class GaussCodeArrayComparator implements Comparator<GaussCodeArray>
	{
		public int compare(GaussCodeArray g1, GaussCodeArray g2)
	{
		int toReturn = 0;
		int size = g1.getSize();

		if (size != g2.getSize())
		{
			//throw some exception
			System.out.println("this gaussCode isn't the same length as that gaussCode");
		}
		else
		{
			for (int i = 0; i < size; i++)
			{
				if (g1.getLetterAt(i) < g2.getLetterAt(i))
				{
					// System.out.println("g1<g2");
					toReturn = -1;
					i = size;
				}
				else if (g1.getLetterAt(i) > g2.getLetterAt(i))
				{
					// System.out.println("g1>g2");
					toReturn = 1;
					i = size;
				}
			}	
		}

		// System.out.print("" + toReturn + "\n");

		return toReturn;
	}
	}