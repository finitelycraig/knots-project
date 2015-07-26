import java.util.Iterator;
import java.util.NoSuchElementException;

public class TrefoilWalk
{
	private static final int OVER = 0;
	private static final int UNDER = 1;

	public static void main(String[] args)
	{
		Knot trefoil = new AdjSetKnot();
		int knotsVisited = 0;

		//add the crossings
		Knot.Crossing one = trefoil.addCrossing("first");
		Knot.Crossing two = trefoil.addCrossing("second");
		Knot.Crossing three = trefoil.addCrossing("third");

		int size = trefoil.size();

		System.out.println("The trefoil contains " + size + " crossings.");

		Knot.Arc oneToTwoOut = trefoil.addArc(one, two, OVER, UNDER);
		Knot.Arc twoToThreeOut = trefoil.addArc(two, three, UNDER, OVER);
		Knot.Arc threeToOneOut = trefoil.addArc(three, one, OVER, UNDER);
		Knot.Arc oneToThreeIn = trefoil.addArc(one, two, UNDER, OVER);
		Knot.Arc twoToThreeIn = trefoil.addArc(two, three, OVER, UNDER);
		Knot.Arc threeToOneIn = trefoil.addArc(three, one, UNDER, OVER);

		if (trefoil.getFirstCrossing() == null)
		{
			System.out.println("oops");
		}
		else
		{
			System.out.println("This knot has a first crossing");
		}

		Knot.WalkIterator walk = trefoil.walk();
		Knot.Crossing crossing;

		// while (walk.hasNext())
		// {
		// 	knotsVisited++;
		// 	crossing = (Knot.Crossing) walk.next();

		// 	System.out.println("Visited " + knotsVisited + " crossing(s). Currently we're visiting " + crossing.getName() + ", which was the " + crossing.getOrderAdded() + " crossing added to this knot.");

		// }

		Colourist colourist = new Colourist(trefoil, 5);

		if(colourist.isColourable())
		{
			System.out.println("The colouring works");
		}
		else
		{
			System.out.println("The colouring failed");
		}

		// System.out.println("We've visited " + knotsVisited + " crossings in this walk");

	}
}