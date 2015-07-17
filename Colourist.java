import java.util.*;
import java.io.*;
import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.Model;
import choco.kernel.solver.Solver;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.cp.solver.search.integer.varselector.MinDomain;
import choco.cp.solver.search.integer.varselector.StaticVarOrder;

public class Colourist
{
	private Knot knot; 
	private Model model;  
    private Solver solver;
    private int pColours; //colouring is done mod p
    private int numOfArcs;
    private int numOfCrossings;
    private Knot.Arc[] arcAtPosition;
    private Deque<Integer>[] colouringPositions; 	
    // an array of deques of integers. the integers are the arc's positions in the walks
	// and they are added to the array depending on their crossing's orderAdded, and to the
	// deques with the rule that outgoing crossings are to the front, incoming crossings
	// to the rear
    private IntegerVariable[] arc; //arc[i] is an integer variable with domain [0, p - 1]

    public Colourist(Knot k, int colours)
    {
    	this.knot = k;
    	this.numOfCrossings = knot.size();
    	this.numOfArcs = (this.numOfCrossings) * 2;
    	this.pColours = colours;

    	// create an array of arc objects of length numOfCrossings * 2 , each arc ois placed in the array
    	// at the index corresponding to its poistion in the walk.  And example for the trefoil is given below
    	// where the walk starts on the overarc at the top left hand crossing
    	//                           
    	//							---
    	//						 1 /   \
    	//                        /  4  \
    	//                    ---| ----------        
    	//                   /    \     /    \
    	//                   |     \6  /2    |
    	//                    \3    \ /     /
    	//					   \	 /	   /5
    	//						\---/ \---/

    	this.arcAtPosition = new Knot.Arc[numOfArcs];

    	arc = makeIntVarArray("arc ", numOfArcs, 0, pColours - 1);
    }

    public boolean isColourable()
    {
    	Knot.WalkIterator walk = knot.walk();
    	int i = 0; //counter for how many arcs we've seen
    	Knot.Crossing crossing;
    	Knot.Crossing target;
    	int crossingNum;
    	int targetNum;
    	int targetOrient;
    	Knot.Arc[] outArcs = new Knot.Arc[2]; 

    	//get the positions of the arcs in the walk associated with the crossings

    	while(walk.hasNext())
    	{
    		crossing = (Knot.Crossing) walk.next();
    		outArcs = crossing.getOutArcs();
    		crossingNum = crossing.getOrderAdded();
    		incomingOrient = walk.getIncomingArcOrient();

    		if (incomingOrient == Knot.OVER) //add to the front of the dq for the crossingNum
    		{
    			colouringPositions[crossingNum].addFirst(i);
    		}
    		else
    		{
    			colouringPositions[crossingNum].addLast(i);
    		}

    		target = outArcs[incomingOrient].getTarget();
    		targetNum = taget.getOrderAdded();
    		targetOrient = outArcs[incomingOrient].getTargetOrienation();

    		if (targetOrient == Knot.OVER)
    		{
    			colouringPositions[targetNum].addFirst(i);
    		}
    		else
    		{
    			colouringPositions[targetNum].addLast(i);
    		}

    		i++;

	    	// then 2x - y - z = 0 (mod p), where x is the number on the over crossings and y and z are the unders
     	}

    	return true;
    }
}