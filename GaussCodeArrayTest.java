public class GaussCodeArrayTest
{
	public static void main(String[] args) 
	{
		GaussCodeArray a = new GaussCodeArray(3);
		GaussCodeArray b = new GaussCodeArray(3);

		a.add(4);
		a.add(1);
		a.add(1);

		b.add(1);
		b.add(1);
		b.add(2);

		System.out.println("size of a " + a.getSize());
		System.out.println("size of b " + b.getSize());

		// System.out.println(" " + a.compareTo(b));
	}
}