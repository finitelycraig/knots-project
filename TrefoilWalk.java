public class TrefoilWalk
{
	private static final int OVER = 0;
	private static final int UNDER = 1;

	public static void main(String[] args)
	{
		Knot trefoil = new AdjSetKnot();


		//add the crossings
		Knot.Crossing one = trefoil.addCrossing();
		Knot.Crossing two = trefoil.addCrossing();
		Knot.Crossing three = trefoil.addCrossing();

		int size = trefoil.size();

		System.out.println("The trefoil contains " + size);

		Knot.Arc oneToTwoOut = trefoil.addArc(one, two, OVER, UNDER);
		Knot.Arc twoToThreeOut = trefoil.addArc(two, three, UNDER, OVER);
		Knot.Arc threeToOneOut = trefoil.addArc(three, one, OVER, UNDER);
		Knot.Arc oneToThreeIn = trefoil.addArc(one, two, UNDER, OVER);
		Knot.Arc twoToThreeIn = trefoil.addArc(two, three, OVER, UNDER);
		Knot.Arc threeToOneIn = trefoil.addArc(three, one, UNDER, OVER);


	}
}