import java.util.Iterator;

public interface Knot
{
	// Each Knot object is an oriented knot, represented as a digraph.
	// Each vertex of the graph corresponds to a corssing of the knot.
	// Each crossing has associated with it four arcs (edges of the graph).
	// Each arc has source and target crossings, and source and target labels to determine if the arc is an over/under
	// crossing at source and at target.

	//over crossings have integer value 0
	public static final int OVER = 0;

	//under crossings have interger value 1
	public static final int UNDER = 1;

	////////// Accessors //////////

	public int size();
	// return the number of crossings in this knot

	////////// Transformers //////////

	public void clear();
	// makes this knot 

	public Knot.Crossing getFirstCrossing();

	// public Knot clone();
	// Returns a clone of this knot

	public Knot.Crossing addCrossing();
	// Add to this knot a new crossing, with no connected arcs and return the new crossing
	public Knot.Crossing addCrossing(String name);

	// public void addArc(Knot.Crossing a, Knot.Crossing b);
	// // Add to this knot a new arc connecting crossings a and b.  the arc's source is a and its target is b

	public Knot.Arc addArc(Knot.Crossing a, Knot.Crossing b, int aOrientation, int bOrientation);
	// Add to this know a new arc connecting crossings a and b.  The arc's source is a, and its over or under
	// orientation at a is recorded.  That arc's target is b, and its over or under orientation at b is recorded

	public void removeCrossing(Knot.Crossing a);
	// Remove crossing a from the knot. Do not remove edges

	// public void removeArc(Knot.Arc x);
	// Remove arc x from the knot

    public Knot.Crossing getByOrderAdded(int i);


	////////// Iterators //////////

	public Knot.WalkIterator walk();
	// walk along the knot from a in the direction of the over crossing arc (with source orientation OVER)

	////////// Inner interfaces //////////

	public interface Crossing
	{
		// Each Knot.Crossing object is a directed graph node
		public String getName();

		public Knot.Arc[] getOutArcs();

		public int getOrderAdded();
	}

	public interface Arc
	{
		// Each Knot.Arc object is a directed graph edge, with source and target labels regarding orientation 
		// at source and target crossings 

		public Knot.Crossing getTarget();

		public int getSourceOrientation();
		// Return the over/under orientation at the arc's source crossing 

		public int getTargetOrientation();
		// Return the over/under orientation at the arc's target crossing 

		// public void setSourceOrientation(int orientation);
		// // Set the over/under orientation at the arc's source crossing 

		// public void setTargetOrientation(int orientation);
		// // Set the over/under orientation at the arc's target crossing 
	}

	public interface WalkIterator extends Iterator
	{
		public int getIncomingArcOrient();
	}
}