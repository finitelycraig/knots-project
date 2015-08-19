public class DistanceGame
{
	private int[] testArray;

	public DistanceGame()
	{
		testArray = new int[10];

		testArray[0] = 1;
		testArray[1] = 2;
		testArray[2] = 3;
		testArray[3] = 1; 
		testArray[4] = 2;
		testArray[5] = 4;
		testArray[6] = 4;
		testArray[7] = 5;
		testArray[8] = 5;
		testArray[9] = 3;
	}

	public int[] distances()
	{
		int[] dist = new int[5];

		for (int i = 0; i < 10; i++)
		{
			int count = -1;

			for (int j = i + 1; j < 10; j++)
			{
				count++;

				if (testArray[i] == testArray[j])
				{
					// System.out.println(" " + (testArray[i] == testArray[j]));
					j = 10;
					dist[testArray[i] - 1] = count;
					System.out.println("" + count);
				}
			}
		}

		return dist;
	}

	public static void main(String[] args) 
	{
		DistanceGame dG = new DistanceGame();

		int[] dist = new int[5];
		dist = dG.distances();

		for (int i = 0; i < 5; i++)
		{
			int actualNumber = i + 1;
			System.out.println("Distance between the " + actualNumber + "'s is " + dist[i]);
		}
	}
}