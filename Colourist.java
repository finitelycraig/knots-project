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
        // int numOfTimeSlots;
        // Integer[][] participation; //participation[i][j] = 1 iff agent i attends meeting j, 0 otherwise
        // Integer[][] distance; //distance[i][j] = the distance between meeting i and meeting j
    private IntegerVariable[] arc; //arc[i] is an integer variable with domain [0, p - 1]

    public Colourist(Knot k, int colours)
    {
    	this.knot = k;
    	this.numOfCrossings = knot.size();
    	this.numOfArcs = (this.numOfCrossings) * 2;
    	this.pColours = colours;

    	arc = makeIntVarArray("arc ", numOfArcs, 0, pColours - 1);
    }

    public boolean isColourable()
    {
    	Iterator walk = knot.walk();
    	int i = 0; //counter for how many knots we've seen

    	while(walk.hasNext())
    	{
    		Knot.Crossing crossing = (Knot.Crossing) walk.next();

    		if (walk.getincomingArcOrient() < 0)
    		{
    			//walk around and get the arcs, also make the arcs that are over to over at crossings have to be equal 

    			// then 2x - y - z = 0 (mod p), where x is the number on the over crossings and y and z are the unders
     		}
    	}

    	return true;
    }
}