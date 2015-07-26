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
public static final String ANSI_RESET = "\u001B[0m";
public static final String ANSI_BLACK = "\u001B[30m";
public static final String ANSI_RED = "\u001B[31m";
public static final String ANSI_GREEN = "\u001B[32m";
public static final String ANSI_YELLOW = "\u001B[33m";
public static final String ANSI_BLUE = "\u001B[34m";
public static final String ANSI_PURPLE = "\u001B[35m";
public static final String ANSI_CYAN = "\u001B[36m";
public static final String ANSI_WHITE = "\u001B[37m";

	private Knot knot; 
	private Model model;  
    private Solver solver;
    private int pColours; //colouring is done mod p
    private int[] solutions;
    private int numOfArcs;
    private int numOfCrossings;
    private Knot.Arc[] arcAtPosition;
    private ColouringList colouringPositions; 	
    // an array of deques of integers. the integers are the arc's positions in the walks
	// and they are added to the array depending on their crossing's orderAdded, and to the
	// deques with the rule that outgoing crossings are to the front, incoming crossings
	// to the rear
    private IntegerVariable[] arc; //arc[i] is an integer variable with domain [0, p - 1]
    private IntegerVariable[] notAllSame;

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
    	notAllSame = makeIntVarArray("notAllSame", numOfArcs - 1, 0, 1);

    	solutions = new int[3];
    	solutions[0] = (-1) * pColours;
    	solutions[1] = 0;
    	solutions[2] = pColours;

    	model           = new CPModel();
        solver          = new CPSolver();

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
    	colouringPositions = new ColouringList(knot);


    	//get the positions of the arcs in the walk associated with the crossings

    	// while(walk.hasNext())
    	// {
    	// 	crossing = (Knot.Crossing) walk.next();
    	// 	outArcs = crossing.getOutArcs();
    	// 	crossingNum = crossing.getOrderAdded();
    	// 	incomingOrient = walk.getIncomingArcOrient();

    	// 	if (incomingOrient == Knot.OVER) //add to the front of the dq for the crossingNum
    	// 	{
    	// 		colouringPositions[crossingNum].addFirst(i);
    	// 	}
    	// 	else
    	// 	{
    	// 		colouringPositions[crossingNum].addLast(i);
    	// 	}

    	// 	target = outArcs[incomingOrient].getTarget();
    	// 	targetNum = target.getOrderAdded();
    	// 	targetOrient = outArcs[incomingOrient].getTargetOrientation();

    	// 	if (targetOrient == Knot.OVER)
    	// 	{
    	// 		colouringPositions[targetNum].addFirst(i);
    	// 	}
    	// 	else
    	// 	{
    	// 		colouringPositions[targetNum].addLast(i);
    	// 	}

    	// 	i++;

	    // 	// then 2x - y - z = 0 (mod p), where x is the number on the over crossings and y and z are the unders
     // 	}

    	while(walk.hasNext())
    	{
    		crossing = (Knot.Crossing) walk.next();
    		outArcs = crossing.getOutArcs();
    		crossingNum = crossing.getOrderAdded();
    		incomingOrient = walk.getIncomingArcOrient();

    		// System.out.println("Incoming orientation in Colourist: " + incomingOrient);

    		if (incomingOrient == Knot.OVER) //add to the front of the dq for the crossingNum
    		{
    			// System.out.println("Colourist pushing over.");
    			// System.out.println("pushed over " + i + " to crossing " + crossingNum);
    			colouringPositions.pushOver(crossingNum, i);
    		}
    		else
    		{
    			// System.out.println("pushed under " + i + " to crossing " + crossingNum);
    			colouringPositions.pushUnder(crossingNum, i);
    		}

    		target = outArcs[incomingOrient].getTarget();
    		targetNum = target.getOrderAdded();
    		targetOrient = outArcs[incomingOrient].getTargetOrientation();

    		if (targetOrient == Knot.OVER) /// i is the wrong number
    		{
    			// System.out.println("pushed over " + i + " to crossing " + crossingNum + " ... target");
    			colouringPositions.pushOver(targetNum, i);
    		}
    		else
    		{
    			// System.out.println("pushed under " + i + " to crossing " + crossingNum + " ... target");
    			colouringPositions.pushUnder(targetNum, i);
    		}

    		i++;

	    	// then 2x - y - z = 0 (mod p), where x is the number on the over crossings and y and z are the unders
     	}


     	//	for each crossing set the constraint that 
     	//		2x - y - z = 0 (mod p)
     	//
     	// where x is the number on the over crossings and y and z are the unders
     	// for (int j = 0; j <= numOfCrossings; j++)
     	// {
     	// 	int over1, over2;
     	// 	int under1, under2;

     	// 	over1 = colouringPositions.removeFirst();
     	// 	over2 = colouringPositions[j].removeFirst();
     	// 	under1 = colouringPositions[j].removeLast();
     	// 	under2 = colouringPositions[j].removeLast();

     	// 	// overarcs at a crossing must take the same colour
     	// 	model.addConstraint(eq(arc[over1], arc[over2]));

     	// 	// labels on arcs have to conform at crossings to the equation
     	// 	//		2x - y - z = 0 mod p
     	// 	//
     	// 	// where x is an over crossing and y and z are the undercrossings
     	// 	// WLOG we can choose either overcrossing
     	// 	// model.addConstraint(mod(minus(minus(mult(arc[over1], constant(2)), arc[under1]), arc[under2])
     	// 	//, constant(0), constant(pColours)));

     	// 	Constraint negP = eq(minus(minus(mult(arc[over1], 2), arc[under1]), arc[under2]), ((-1) * pColours));
     	// 	Constraint zero = eq(minus(minus(mult(arc[over1], 2), arc[under1]), arc[under2]), 0);
     	// 	Constraint p = eq(minus(minus(mult(arc[over1], 2), arc[under1]), arc[under2]), pColours);
     	// 	model.addConstraint(or(negP, zero, p));
     	// 



     	////////////////////////////////////////////


     	for (int j = 0; j < 3; j++)
     	{
     		// System.out.println("                                                     j = " + j);
     		int over1, over2;
     		int under1, under2;

     		over1 = colouringPositions.popOver(j);
     		over2 = colouringPositions.popOver(j);
     		under1 = colouringPositions.popUnder(j);
     		under2 = colouringPositions.popUnder(j);

     		// System.out.println("over1 = " + over1 + "  |  over2 = " + over2);
     		// System.out.println("under1 = " + under1 + "  |  under2 = " + under2);


     		// overarcs at a crossing must take the same colour

     		// System.out.println("arc length" + arc.length);

     		// System.out.println("arc[over1] " + arc[over1]);
     		// System.out.println("arc[over2] " + arc[over2]);

     		// System.out.println(" ** making these equal " + arc[over1] + "  |  " + arc[over2]);
     		model.addConstraint(eq(arc[over1], arc[over2]));

     		// labels on arcs have to conform at crossings to the equation
     		//		2x - y - z = 0 mod p
     		//
     		// where x is an over crossing and y and z are the undercrossings
     		// WLOG we can choose either overcrossing
     		// model.addConstraint(mod(minus(minus(mult(arc[over1], constant(2)), arc[under1]), arc[under2])
     		//, constant(0), constant(pColours)));

     		// System.out.println("EQUATION ---------> 2*" + over1 + " - " + under1 + " - " + under2 + " = 0");

     		Constraint negP = eq(minus(minus(mult(arc[over1], 2), arc[under1]), arc[under2]), ((-1) * pColours));
     		Constraint zero = eq(minus(minus(mult(arc[over1], 2), arc[under1]), arc[under2]), 0);
     		Constraint p = eq(minus(minus(mult(arc[over1], 2), arc[under1]), arc[under2]), pColours);
     		model.addConstraint(or(negP, zero, p));
     	}


     	// we must also set the constraint that some arc value is not the same as the rest
     	for (int k = 0; k < numOfArcs - 1; k++)
     	{
     		model.addConstraint(ifOnlyIf(eq(arc[k], arc[k + 1]), eq(notAllSame[k], 0)));
     	}

     	model.addConstraint(geq(sum(notAllSame), 1));

     	solver.read(model);

    	boolean success = solver.solve();

    	int solution = -1;
    	String colour;

    	// System.out.println(ANSI_GREEN);

    	if(success)
    	{
	    	for (int k = 0; k < numOfArcs; k++)
	    	{
	    		solution = solver.getVar(arc[k]).getVal();

	    		switch (solution)
	    		{
	    			case 0: colour = ANSI_RED;
	    					break;
	    			case 1: colour = ANSI_GREEN;
	    					break;
	    			case 2: colour = ANSI_BLUE;
	    					break;
	    			case 3: colour = ANSI_YELLOW;
	    					break;
	    			case 4: colour = ANSI_CYAN;
	    					break;
	    			default: colour = ANSI_WHITE;
	    					break;
	    		}

				System.out.println(colour + "arc " + k + " colour " + solution + ANSI_RESET);

	    	}
    	}

    	System.out.println("nodes: "+ solver.getNodeCount() +"   cpu: "+ solver.getTimeCount());

    	return success;
    }
}

