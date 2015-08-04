import java.util.Iterator;
import java.util.NoSuchElementException;

public class AdjSetKnot implements Knot
{
	/* This knot is represented as follows.  
		-	firstCrossing is a link to the first crossing in the knot (which can be chosen arbitarily for all 
			knots) which points to the first node of a DLL of AdjSetKnot.Crossing objects.
		-	each AdjSetKnot.Crossing is linked to an array of outgoing AdjSetKnot.Arc objects.  The outgoing over
			crossing is placed in index 0 of the array and the outgoing under crossing index 1.
		- 	each AdjSetKnot.Arc object contains a link to the arc's source and target crossings, and attributes
			determining the nature of the arc (over/under) at source and target
	*/

	private AdjSetKnot.Crossing firstCrossing;

	private int size;

	public AdjSetKnot()
	{
		//construct a Knot, initially empty
		firstCrossing = null;

		// TODO maybe set size to minus 1, since the trivial knot will have size 0
		// but all knots uwith size 1 or 2 are trivial
		size = 0;
	}

    // public AdjSetKnot(int[] gaussCode)
    // {
        
    // }

    ///////////////////////// Accessors /////////////////////////

    public int size() 
    {
    	// Return the number of crossings in this knot
        return this.size;
    }

    ///////////////////////// Transformers /////////////////////////

    public void clear()
    {
    	firstCrossing = null;
    	size = 0;
    }

    // public void clone()

    public Knot.Crossing addCrossing()
    {
    	//add to this knot a new crossing
    	AdjSetKnot.Crossing crossing = new AdjSetKnot.Crossing();

    	crossing.prevCrossing = null;
    	crossing.nextCrossing = firstCrossing;
    	firstCrossing = crossing;

    	if (size == -1)
    	{
    		System.out.println("Looks like this is your fist crossing");
    		size = 1;
    	}
    	else
    	{
    		size++;
    	}

        return crossing;
    }

    public Knot.Crossing addCrossing(String n)
    {
        //add to this knot a new crossing
        AdjSetKnot.Crossing crossing = new AdjSetKnot.Crossing();

        crossing.prevCrossing = null;
        crossing.nextCrossing = firstCrossing;
        crossing.name = n;
        firstCrossing = crossing;

        if (size == -1)
        {
            System.out.println("Looks like this is your fist crossing");
            size = 1;
        }
        else
        {
            size++;
        }

        return crossing;
    }

    public Knot.Arc addArc(Knot.Crossing source, Knot.Crossing target, int sourceOrient, int targetOrient)
    {
    	//add outgoing arc to the array label on source

        AdjSetKnot.Crossing s = (AdjSetKnot.Crossing) source;
        AdjSetKnot.Crossing t = (AdjSetKnot.Crossing) target;
    	s.addArc(t, sourceOrient, targetOrient);

    	return null;
    }

    public void removeCrossing(Knot.Crossing cross)
    {
        for (AdjSetKnot.Crossing c = firstCrossing; c!=null; c = c.nextCrossing)
        {
            for (int i = 0 ; i < 2 ; i++)
            {

            }
        }
    }

    public void reverse()
    {

    }

    public void checkValid()
    {

    }

    public Knot.Crossing getFirstCrossing()
    {
        return firstCrossing;
    }

    public Knot.Crossing getByOrderAdded(int i)
    {
        Iterator it = this.iterator();
        Knot.Crossing crossing;

        while (it.hasNext())
        {
            crossing = (Knot.Crossing) it.next();
            {
                if (crossing.getOrderAdded() == i)
                {
                    return crossing;
                }
            }
        }

        return null;

    }

    ///////////////////////// Iterators /////////////////////////

    public Knot.WalkIterator walk()
    {
    	return new AdjSetKnot.WalkIterator();
    }

    public Iterator iterator()
    {
        return new AdjSetKnot.OrderAddedIterator();
    }

    ///////////////////////// Inner Classes for Crossings and Arcs - and iterators /////////////////////////


    private class Crossing implements Knot.Crossing 
    {
    	// each Crossing object represents a crossing of the knot
    	// it has a link to the previous and next crossings along the knot
    	// it has an array of links to its outgoing arcs
    	private AdjSetKnot.Crossing prevCrossing;
    	private AdjSetKnot.Crossing nextCrossing;
        private String name;
    	private AdjSetKnot.Arc[] outArcs;
        private int orderAdded = size + 1;

    	private Crossing()
    	{
    		this.prevCrossing = null;
    		this.nextCrossing = null;
    		this.outArcs = new AdjSetKnot.Arc[2]; //there are always two outArcs;
    	}

        private Crossing(String n)
        {
            this.prevCrossing = null;
            this.nextCrossing = null;
            this.name = n;
            this.outArcs = new AdjSetKnot.Arc[2]; //there are always two outArcs;
        }

        public String getName()
        {
            return this.name;
        }

        public AdjSetKnot.Crossing getNextCrossing()
        {
            return this.nextCrossing;
        }

        public AdjSetKnot.Crossing getPrevCrossing()
        {
            return this.prevCrossing;
        }

    	//get the array of outgoing arcs from this crossing 
    	public AdjSetKnot.Arc[] getOutArcs()
    	{
    		return this.outArcs;
    	}

    	//add an arc to this crossing, its position in the array is determined by its source orientation
    	public void addArc(AdjSetKnot.Crossing target, int sourceOrient, int targetOrient)
    	{
    		if (sourceOrient == OVER)
    		{
    			AdjSetKnot.Arc arc = new AdjSetKnot.Arc(this, target, sourceOrient, targetOrient);

    			this.outArcs[OVER] = arc;
    		}
    		else if (sourceOrient == UNDER)
    		{
    			AdjSetKnot.Arc arc = new AdjSetKnot.Arc(this, target, sourceOrient, targetOrient);

    			this.outArcs[UNDER] = arc;
    		}
    		else
    		{
    			System.out.println("Tried adding an arc but the orientation was neither OVER (0) not UNDER (1)");
    		}
    	}

        public int getOrderAdded()
        {
            return orderAdded;
        }

    }


    //////////////////////////////////////////////////

    private class Arc implements Knot.Arc
    {
    	//each arc has a link to its source and target crossings 
    	//and an attribute to determine its orientation at each crossing
    	private AdjSetKnot.Crossing source;
     	private AdjSetKnot.Crossing target;
    	private int sourceOrient;
    	private int targetOrient;

    	private Arc(AdjSetKnot.Crossing s, AdjSetKnot.Crossing t, int sOrient)
    	{
    		this.source = s;
    		this.target= t; 
    		this.sourceOrient = sOrient;
    	}

    	private Arc(AdjSetKnot.Crossing s, AdjSetKnot.Crossing t, int sOrient, int tOrient)
    	{
    		this.source = s;
    		this.target= t; 
    		this.sourceOrient = sOrient;
    		this.targetOrient = tOrient;
    	}

    	public AdjSetKnot.Crossing getTarget()
    	{
    		return this.target;
    	}

    	public void setTarget(AdjSetKnot.Crossing newTarget)
    	{
    		this.target = newTarget;
    	}

    	public AdjSetKnot.Crossing getSource()
    	{
    		return this.source;
    	}

    	public int getSourceOrientation()
    	{
    		return sourceOrient;
    	}

    	public int getTargetOrientation()
    	{
    		return targetOrient;
    	}

    	public void setTargetOrientation(int orientation)
    	{
    		if (orientation == OVER || orientation == UNDER)
    		{
	    		this.targetOrient = orientation;
    		}
    	}
    }

    //////////////////////////////////////////////////

    private class WalkIterator implements Knot.WalkIterator
    {
    	//the walk iterator walks around the knot from the first crossing, untill it reaches the first crossing again

    	private AdjSetKnot.Crossing currentCrossing;
    	private int incomingArcOrient;
    	private boolean leftOnTheWalk;
        private boolean halfway;
       	private boolean finished;
        
    	private WalkIterator()
    	{
    		currentCrossing = firstCrossing;
    		incomingArcOrient = -1;
    		leftOnTheWalk = false;
            halfway = false;
    		finished = false;
    	}

    	public boolean hasNext()
    	{
            //if we've not left yet then return true
    		if (leftOnTheWalk == false)
    		{
                // System.out.println("just left");
    			return true;
    		}
    		else if (currentCrossing == firstCrossing && halfway == false)
    		{   //if we have gone halfway though
                halfway = true;
                // System.out.println("halfway");
    			return true;
    		}
            else if (currentCrossing != firstCrossing && halfway == true)
            {   // if we've gone over half way though
                // System.out.println("past halfway");
                return true;
            }
            else
            {
                // System.out.println("else");
                return (currentCrossing != firstCrossing);
            }
    	}

    	public Knot.Crossing next()
    	{
    		if (currentCrossing == null)
    		{
                System.out.println("What is going on!");
    			throw new NoSuchElementException();
    		}

    		AdjSetKnot.Crossing nextInWalk;
			AdjSetKnot.Arc[] outArcs = currentCrossing.getOutArcs();
	

    		//if the walk hasn't started choose to leave on the over edge of the first crossing
    		//mark the walk as begun
    		if (leftOnTheWalk == false)
    		{
    			nextInWalk = outArcs[OVER].getTarget();

    			incomingArcOrient = outArcs[OVER].getTargetOrientation();

				leftOnTheWalk = true;
    		}
    		else
    		{
    			// set the next crossing based on the orientation of the arc we arrived at the current crossing on
    			// and the incomingArcOrienat based on the arc we leave on at this next crossing
    			nextInWalk = outArcs[incomingArcOrient].getTarget();
    			incomingArcOrient = outArcs[incomingArcOrient].getTargetOrientation();
    		}

			try
            {
                if (nextInWalk == null)
			    {
				    throw new AdjSetKnot.KnotNotClosedException("Failed to walk around the knot because it was not closed.");
                }
			}
            catch (AdjSetKnot.KnotNotClosedException e)
            {
                System.out.println("Failed to walk around th knot because it was not closed.");
            }

	   		currentCrossing = nextInWalk;

    		return nextInWalk;
    	}

    	public void remove()
    	{
    		//not implemented
    	}

        public int getIncomingArcOrient()
        {
            return this.incomingArcOrient;
        }

    }

    private class OrderAddedIterator implements Iterator
    {
        private AdjSetKnot.Crossing currentCrossing;

        public OrderAddedIterator()
        {
            currentCrossing = firstCrossing;
        }

        public boolean hasNext()
        {
            // boolean success = (currentCrossing != null);

            // System.out.println(" " + success + " currentCrossing " + currentCrossing.getOrderAdded());
            return (currentCrossing != null);
        }

        public Knot.Crossing next()
        {
            // int n = currentCrossing.getOrderAdded();

            // System.out.println("" + n);

            Knot.Crossing result = currentCrossing;

            if (result == null)
            {
                throw new NoSuchElementException();
            }

            currentCrossing = currentCrossing.getNextCrossing();
 
            return result;
        }

        public void remove()
        {
            //not implemented
        }
    }

    ///////////////////////// Custom exeptions /////////////////////////

    private class KnotNotClosedException extends Exception
    {
    	public KnotNotClosedException(String message)
    	{
    		super(message);
    	}

    	public KnotNotClosedException(String message, Throwable throwable)
    	{
    		super(message, throwable);
    	}
    }
}