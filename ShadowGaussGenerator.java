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
import choco.cp.solver.search.integer.varselector.RandomIntVarSelector;
// import choco.Options;



public class ShadowGaussGenerator
{
    private Model model;  
    private Solver solver;
    private int numOfCrossings;
    private IntegerVariable[] letter; //letter[i] is an letter in the Gauss code with value [0, numOfCrossings - 1]
    private IntegerVariable[] notAllSame;

    public ShadowGaussGenerator(int crossings)
    {
        model = new CPModel();
        solver = new CPSolver();

        numOfCrossings = crossings;

        letter = makeIntVarArray("arc ", (2 * numOfCrossings), 1, numOfCrossings);

        // add the constraint that everything can only appear twice
        for (int k = 1; k <= numOfCrossings; k++)
        {
            // System.out.println("" + k);
            model.addConstraint(occurrence(2, letter, k));
        }

        // add a constraint that means the first thing has to be 1
        model.addConstraint(eq(letter[0], 1));

        for (int i = 0; i < (2 * numOfCrossings); i++)
        {
            //add a constraint that does evenly spaced
            for (int j = i; j < (2 * numOfCrossings); j ++)
            {
                if (i % 2 == 0) // if i is even set that it can't be equal to another even space
                {
                    if (j % 2 == 0 && j != i)
                    {   
                        // System.out.println("saying " + i + " can't equal " + j);
                        model.addConstraint(neq(letter[i], letter[j]));
                    }
                }
                else
                {
                    if (j % 2 == 1 && j != i)
                    {
                        // System.out.println("saying " + i + " can't equal " + j);
                        model.addConstraint(neq(letter[i], letter[j]));
                    }
                }
            }    
        }

        ////////////////////////////// IMPOSE LEXIMIN ORDERING!!! //////////////////////////////

        //each number has to be already given or the least of those that aren't
        // x_n <= max(x_0, ... , x_n-1) + 1

        IntegerVariable[] maxSoFar = makeIntVarArray("maxSoFar", ((2 * numOfCrossings) - 1), 0, numOfCrossings);

        for (int i = 1; i < (2 * numOfCrossings - 1); i ++)
        {
            model.addConstraint(max(Arrays.copyOfRange(letter, 0, i), maxSoFar[i]));
            model.addConstraint(leq(letter[i], plus(maxSoFar[i], constant(1))));
        }

        solver.read(model);

        // solver.setVarIntSelector(new RandomIntVarSelector(solver));
        // RandomIntVarSelector rando = new RandomIntVarSelector(solver);

        solver.setVarIntSelector(new StaticVarOrder(solver, solver.getVar(letter)));

        if (solver.solve().booleanValue())
        {
            do
            {
                for (int i = 0; i < (2*numOfCrossings); i++)
                {
                    System.out.print("" + solver.getVar(letter[i]).getVal() + ", ");
                }
                System.out.print("\n");
            }
            while (solver.nextSolution().booleanValue());
        }

        // for (int i = 0; i < (2*numOfCrossings); i++)
        // {
        //     // System.out.println("i = " + i);
        //     System.out.print("" + solver.getVar(letter[i]).getVal() + ", ");
        // }
        System.out.print("\n");
        // s.printRuntimeStatistics();
        System.out.println("feasible: " +solver.isFeasible());
        System.out.println("nbSol: " + solver.getNbSolutions());
        System.out.println("Nodes: " + solver.getNodeCount() + "   cpu: " +solver.getTimeCount());

    }

    public static void main(String[] args) 
    {
        int crossings = Integer.parseInt(args[0]);
        ShadowGaussGenerator sGG = new ShadowGaussGenerator(crossings);
    }
}

