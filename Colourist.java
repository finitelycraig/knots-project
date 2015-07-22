import java.util.*;
import java.io.*;
import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.Model;
import choco.kernel.solver.Solver;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.cp.solver.search.integer.varselector.MinDomain;
import choco.cp.solver.search.integer.varselector.StaticVarOrder;

public class Colourist
{
	private Knot knot; 
	private Model model;  
    private Solver solver;
    private int pColours; //colouring is done mod p
    private int[] solutions;
    private int numOfArcs;
    private int numOfCrossings;
    private Knot.Arc[] arcAtPosition;
    private Deque<Integer>[] colouringPositions; 	
    // an array of deques of integers. the integers are the arc's positions in the walks
	// and they are added to the array depending on their crossing's orderAdded, and to the
	// deques with the rule that outgoing crossings are to the front, incoming crossings
	// to the rear
    private IntegerVariable[] arc; //arc[i] is an integer variable with domain [0, p - 1]
    private IntegerVariable[] negArc;

    public Colourist(Knot k, int colours)
    {
    	this.knot = k;
    	this.numOfCrossings = knot.size();
    	this.numOfArcs = (this.numOfCrossings) * 2;
    	this.pColours = colours;

    	// create an array of arc objects of length numOfCrossings * 2 , each arc is placed in the array
    	// at the index corresponding to its poistion in the walk.  And example for the trefoil is given below
    	// where the walk starts on the overarc at the top left hand crossing
    	//                           
    	//							---
    	//						 0 /   \
    	//                        /  3  \
    	//                    ---| ----------        
    	//                   /    \     /    \
    	//                   |     \5  /1    |
    	//                    \2    \ /     /
    	//					   \	 /	   /4
    	//						\---/ \---/

    	this.arcAtPosition = new Knot.Arc[numOfArcs];

    	arc = makeIntVarArray("arc ", numOfArcs, 0, pColours - 1);

    	solutions = new int[3];
    	solutions[0] = (-1) * pColours;
    	solutions[1] = 0;
    	solutions[2] = pColours;
    }

    public boolean isColourable()
    {
    	Knot.WalkIterator walk = knot.walk();
    	int i = 0; //counter for how many arcs we've seen
    	Knot.Crossing crossing;
    	Knot.Crossing target;
    	int crossingNum;
    	int incomingOrient;
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
    		targetNum = target.getOrderAdded();
    		targetOrient = outArcs[incomingOrient].getTargetOrientation();

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

     	for (int j = 0; j <= numOfCrossings; j++)
     	{
     		int over1, over2;
     		int under1, under2;

     		over1 = colouringPositions[j].removeFirst();
     		over2 = colouringPositions[j].removeFirst();
     		under1 = colouringPositions[j].removeLast();
     		under2 = colouringPositions[j].removeLast();

     		// overarcs at a crossing must take the same colour
     		model.addConstraint(eq(arc[over1], arc[over2]));

     		// labels on arcs have to conform at crossings to the equation
     		//		2x - y - z = 0 mod p
     		//
     		// where x is an over crossing and y and z are the undercrossings
     		// WLOG we can choose either overcrossing
     		// model.addConstraint(mod(minus(minus(mult(arc[over1], constant(2)), arc[under1]), arc[under2]), constant(0), constant(pColours)));

     		Constraint negP = eq(mult(arc[over1], 2), ((-1) * pColours));
     		Constraint zero = eq(mult(arc[over1], 2), 0);
     		Constraint p = eq(mult(arc[over1], 2), pColours);
     		model.addConstraint(or(negP, zero, p));

     		// IntegerVariable lhs = minus(minus(mult(arc[over1], 2), arc[under1]), arc[under2]);
     		// model.addConstraint(mod(lhs, constant(0), pColours));
     	}

    	return true;
    }
}