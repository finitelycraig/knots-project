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
    private int twiceCrossings;
    private IntegerVariable[] letter; //letter[i] is an letter in the Gauss code with value [0, numOfCrossings - 1]

    public ShadowGaussGenerator(int crossings)
    {
        model = new CPModel();
        solver = new CPSolver();

        numOfCrossings = crossings;
        twiceCrossings = 2 * crossings;

        letter = makeIntVarArray("arc ", (twiceCrossings), 1, numOfCrossings);

        // add the constraint that everything can only appear twice
        for (int k = 1; k <= numOfCrossings; k++)
        {
            // System.out.println("" + k);
            model.addConstraint(occurrence(2, letter, k));
        }

        // add a constraint that means the first thing has to be 1
        model.addConstraint(eq(letter[0], 1));

        //add a constraint that does evenly spaced
        for (int i = 0; i < twiceCrossings; i++)
        {
            for (int j = i; j < twiceCrossings; j ++)
            {
                // if (i == 0)
                // {
                //     if (j % 2 == 0 && j != i && j <= numOfCrossings)
                //     {
                //         model.addConstraint(neq(letter[i], letter[j]));
                //     }
                //     else if (j > numOfCrossings)
                //     {
                //         model.addConstraint(neq(letter[i], letter[j]));
                //     }
                // }
                if (i % 2 == 0) // && i != 0) // if i is even set that letter[i] can't be equal to letter[j] with j even
                {
                    if (j % 2 == 0 && j != i) // don't set that letter[i] != letter[i]
                    {   
                        // System.out.println("saying " + i + " can't equal " + j);
                        model.addConstraint(neq(letter[i], letter[j]));
                    }
                }
                else // if i is odd set that letter[i] can't be equal to letter[j] with j odd
                {
                    if (j % 2 == 1 && j != i) //don't set that letter[i] != letter[i]
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

        IntegerVariable[] maxSoFar = makeIntVarArray("maxSoFar", (twiceCrossings - 1), 0, numOfCrossings);

        for (int i = 1; i < (twiceCrossings - 1); i ++)
        {
            model.addConstraint(max(Arrays.copyOfRange(letter, 0, i), maxSoFar[i]));
            model.addConstraint(leq(letter[i], plus(maxSoFar[i], constant(1))));
        }

        // IntegerVariable[] distances = makeIntVarArray("distances", (numOfCrossings) - 1, 0, (twiceCrossings));

        // for (int i = 0; i < (twiceCrossings); i++)
        // {
        //     int count = -1;
    
        //     for (int j = i + 1; j < (2 * ))
        //     {
        //         count ++;

        //         if (letter[i].getVar().getVal() == letter[j].getVar().getVal())
        //         {

        //         }
        //     }

        solver.read(model);

        // solver.setVarIntSelector(new RandomIntVarSelector(solver));
        // RandomIntVarSelector rando = new RandomIntVarSelector(solver);

        solver.setVarIntSelector(new StaticVarOrder(solver, solver.getVar(letter)));

        // GaussCodeArray temp = new GaussCodeArray(twiceCrossings);
        int[] dist = new int[crossings];

        TreeSet<GaussCodeArray> solutions = new TreeSet<GaussCodeArray>(new GaussCodeArrayComparator());
        TreeSet<Integer> blah = new TreeSet<Integer>();
        boolean addSolnToSet = true;

        
        if (solver.solve().booleanValue())
        {
            do
            {
                GaussCodeArray temp = new GaussCodeArray(twiceCrossings);
                addSolnToSet = true;
                for (int i = 0; i < (2*numOfCrossings); i++)
                {
                    // System.out.println("I'm in here!");
                    int nextLetter = solver.getVar(letter[i]).getVal();
                    System.out.print("" + nextLetter + ", ");
                    temp.add(nextLetter);
                }

                for (int i = 0; i < twiceCrossings; i++)
                {
                    int count = -1;

                    for (int j = i + 1; j < twiceCrossings; j++)
                    {
                        count++;

                        if (temp.getLetterAt(i) == temp.getLetterAt(j))
                        {
                            j = twiceCrossings;
                            dist[temp.getLetterAt(i) - 1] = count;
                        }
                    }
                }

                for (int i = 0; i < numOfCrossings; i++)
                {
                    if (dist[i] < dist[0])
                    {
                        addSolnToSet = false;
                    }
                }

                if (addSolnToSet)
                {
                    // System.out.println("adding " + temp.toString());
                    solutions.add(temp);
                }

                // temp.clear();

                System.out.print("\n");
            }
            while (solver.nextSolution().booleanValue());
        }

        

        // for (int i = 0; i < (2*numOfCrossings); i++)
        // {
        //     // System.out.println("i = " + i);
        //     System.out.print("" + solver.getVar(letter[i]).getVal() + ", ");
        // }

        System.out.println("solutions after equivalence");

        for(GaussCodeArray g : solutions)
        {
            System.out.println(g.toString());
        }

        System.out.print("\n");
        // s.printRuntimeStatistics();
        System.out.println("feasible: " +solver.isFeasible());
        System.out.println("nbSol: " + solver.getNbSolutions());
        System.out.println("sol after equivalence stuff: " + solutions.size());
        System.out.println("Nodes: " + solver.getNodeCount() + "   cpu: " +solver.getTimeCount());



    }

    public static void main(String[] args) 
    {
        int crossings = Integer.parseInt(args[0]);
        ShadowGaussGenerator sGG = new ShadowGaussGenerator(crossings);
    }
}

