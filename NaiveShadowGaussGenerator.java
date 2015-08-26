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
import choco.cp.solver.search.integer.valselector.RandomIntValSelector;
// import choco.Options;

public class NaiveShadowGaussGenerator
{
    private Model model;  
    private Solver solver;
    private int numOfCrossings;
    private IntegerVariable[] letter; //letter[i] is an letter in the Gauss code with value [0, numOfCrossings - 1]
    private IntegerVariable[] notAllSame;
    private final int RANDOM = 0;
    private final int ALL = 1;
    private final int RANDOM_PRIME = 2;  
    private final int ALL_PRIME = 3;
    private String output = "";
    private Set<List<Integer>> codes;

    // use options as given by the integers above
    // boolean true for verbose solution with cpu time, solution counts ect, false for just solution codes
    public NaiveShadowGaussGenerator(int crossings, int option, boolean verbose)
    {
        model = new CPModel();
        solver = new CPSolver();

        numOfCrossings = crossings;

        letter = makeIntVarArray("arc ", (2 * numOfCrossings), 1, numOfCrossings);

        // add the constraint that each letter can only appear twice
        for (int k = 1; k <= numOfCrossings; k++)
        {
            model.addConstraint(occurrence(2, letter, k));
        }

        // add a constraint that sets the first letter to be 1
        model.addConstraint(eq(letter[0], 1));

        // add a constraint that does evenly spaced, to do this add the constraint that 
        //  
        //      x[i] = k with i odd implies that x[j] != k with j odd, similarly for even
        //
        // with the added constraint that the second 1 can't appear after the nth position in the code
        // any code with a 1 later then the nth position isn't the lexicographically minimum code 
        // representing the equivelence class of codes to which it belongs
        for (int i = 0; i < (2 * numOfCrossings); i++)
        {
            for (int j = i; j < (2 * numOfCrossings); j ++)
            {
                // set up both above constraints for 1
                if (i == 0)
                {
                    if (j % 2 == 0 && j != i && j <= numOfCrossings)
                    {
                        model.addConstraint(neq(letter[i], letter[j]));
                    }
                    else if (j > numOfCrossings)
                    {
                        model.addConstraint(neq(letter[i], letter[j]));
                    }
                }
                else if (i % 2 == 0 && i != 0)
                {
                    if (j % 2 == 0 && j != i)
                    {   
                        model.addConstraint(neq(letter[i], letter[j]));
                    }
                }
                else
                {
                    if (j % 2 == 1 && j != i)
                    {
                        model.addConstraint(neq(letter[i], letter[j]));
                    }
                }
            }    
        }

        ////////////////////////////// IMPOSE LEXIMIN ORDERING!!! //////////////////////////////

        //each number has to be already given or the least of those that aren't
        //
        //      x_n <= max(x_0, ... , x_n-1) + 1
        //

        IntegerVariable[] maxSoFar = makeIntVarArray("maxSoFar", ((2 * numOfCrossings) - 1), 0, numOfCrossings, "cp:no_decision");

        for (int i = 1; i < (2 * numOfCrossings - 1); i ++)
        {
            model.addConstraint(max(Arrays.copyOfRange(letter, 0, i), maxSoFar[i]));
            model.addConstraint(leq(letter[i], plus(maxSoFar[i], constant(1))));
        }

        solver.read(model);

        if (option == RANDOM || option == RANDOM_PRIME)
        {
            System.out.println("Setting random seed");
            solver.setVarIntSelector(new RandomIntVarSelector(solver));
            // Random r = new Random();
            // long number = r.nextLong();

            // RandomIntValSelector rando = new RandomIntValSelector(number);

            // solver.setIntValSelector(rando);
            // solver.set(rando);

        }
        else
        {
            solver.setVarIntSelector(new StaticVarOrder(solver, solver.getVar(letter)));
        }

        // set up a set into which we'll add the lexicographically minimum Gauss codes
        codes = new TreeSet<List<Integer>>(new Comparator<List<Integer>>() 
        {
            public int compare(List<Integer> l1, List<Integer> l2) 
            {
                int sz = l1.size();
                for (int i=0; i<sz; i++) 
                {
                    if (l1.get(i) < l2.get(i)) 
                    {
                        return -1;
                    }
                    if (l1.get(i) > l2.get(i)) 
                    {
                        return 1;
                    }
                }
                return 0;

            }
        });

        // actually solve stuff!

        System.out.println("Just before if");
        // if a first solution exists (it does)
        if (solver.solve().booleanValue())
        {
            do
            {
                System.out.println("Grabbing another solution");
                // Generate Gauss codes
                int[] gaussCode = new int[2*numOfCrossings];

                for (int i = 0; i < (2*numOfCrossings); i++)
                {
                    gaussCode[i] = solver.getVar(letter[i]).getVal();
                }

                // submit the gauss code to the dually paired testing class
                DuallyPairedTest dp = new DuallyPairedTest(gaussCode);

                // only consider the lexmin reordering of each code
                gaussCode = lexRenumber(gaussCode, numOfCrossings);

                if(dp.isDuallyPaired())
                {
                    if (option == RANDOM_PRIME || option == ALL_PRIME)
                    {
                        if (isPrime(gaussCode))
                        {
                            codes.add(lexMinInClass(gaussCode, numOfCrossings));

                            // if we're generating just one random code we want to break after one if found
                            if (option == RANDOM_PRIME)
                            {
                                break;
                            }
                        }
                    }
                    else
                    {
                        codes.add(lexMinInClass(gaussCode, numOfCrossings));
                        if (option == RANDOM)
                        {
                            break;
                        }
                    }
                }
            }
            while (solver.nextSolution().booleanValue());
        }

        // add each Gauss code to the output string, on its own line
        for (List<Integer> c : codes) 
        {
            output = output + codeToString(c) + "\n";
        }

        // command line printing code
        // System.out.println();
        // System.out.println("De-duplicated codes:");
        // for (List<Integer> c : codes) 
        // {
        //     print(c);
        // }
        // System.out.println();

        if (verbose) 
        {
            System.out.println("feasible: " + solver.isFeasible());
            System.out.println("nbSol: " + solver.getNbSolutions());
            System.out.println("nodes: "+ solver.getNodeCount() +"   cpu: "+ solver.getTimeCount());
            System.out.println("Solution count: " + codes.size());
        }
    }

    public String solutionToString()
    {
        return output;
    }

    public boolean isPrime(int[] gaussCode)
    {
        //for each even subset, check that it contains more entries than half its length
        //if a subset which contains the same number
        for (int i = 0; i < gaussCode.length; i++)
        {
            for (int j = i; j < gaussCode.length; j++)
            {
                // System.out.println("i = " + i + " j = " + j);
                int gap = j - i;
                // otherwise everything is false
                if (!(gap == gaussCode.length) && (gap % 2 == 0) && (gap > 0))
                {
                    int[] temp = Arrays.copyOfRange(gaussCode, i, j);

                    Set<Integer> tempSet = new TreeSet<Integer>();

                    for (int k = 0; k < temp.length; k++)
                    {
                        tempSet.add(temp[k]);
                    } 

                    if (tempSet.size() == (temp.length / 2))
                    {
                        // System.out.println("I'm returning false now");
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public int[] lexRenumber(int[] gaussCode, int n) 
    {
        int[] retval = new int[n*2];
        int[] newNums = new int[n];
        int maxSoFar = 0;
        
        for (int i=0; i<n*2; i++) 
        {
            if (newNums[gaussCode[i]-1]==0) 
            {
                newNums[gaussCode[i]-1] = ++maxSoFar;
            }
            retval[i] = newNums[gaussCode[i]-1];
        }
        return retval;
    }

    public List<Integer> lexMinInClass(int[] gaussCode, int n) 
    {
        int[] minInClass = gaussCode;
        int[] reversedCode = reversed(gaussCode);
    
        if (lexLt(lexRenumber(reversedCode, n), gaussCode))
        {
            minInClass = lexRenumber(reversedCode, n);
        }

        for (int i=1; i<n*2; i++) 
        {
            int[] rotatedCode = lexRenumber(rotated(gaussCode, i), n); 
            int[] rotatedReversedCode = lexRenumber(rotated(reversedCode, i), n);

            if (lexLt(rotatedCode, minInClass)) 
            {
                minInClass = rotatedCode;
            }

            if (lexLt(rotatedReversedCode, minInClass)) 
            {   
                minInClass = rotatedReversedCode;
            }
        }

        List<Integer> retval = new ArrayList<Integer>();

        for (int i=0; i<n*2; i++) 
        {
            retval.add(minInClass[i]);
        }

        return retval;
    }

    public int[] reversed(int[] arr) 
    {
        int len = arr.length;
        int[] retval = new int[len];
    
        for (int i=0; i<len; i++) 
        {
            retval[len-1-i] = arr[i];
        }
        
        return retval;
    }

    public int[] rotated(int[] arr, int rotateBy) 
    {
        int len = arr.length;
        int[] retval = new int[len];
    
        for (int i=0; i<len; i++) 
        {
            retval[(i+rotateBy)%len] = arr[i];
        }

        return retval;
    }
    
    public boolean lexLt(int[] arr1, int[] arr2) 
    {
        int len = arr1.length;

        for (int i=0; i<len; i++) {
            if (arr1[i] < arr2[i]) 
            {
                return true;
            }

            if (arr1[i] > arr2[i]) 
            {
                return false;
            }
        }

        return false;
    }

    public void print(int[] arr) 
    {
        for (int i=0; i<arr.length; i++) 
        {
            System.out.print(arr[i] + ", ");
        }

        System.out.println();
    }

    // to string method for each Gauss code
    public String codeToString(List<Integer> list)
    {
        String s = "";
        for (Integer i : list) 
        {
            s = s + i + ", ";
        }
        return s;
    }

    public void print(List<Integer> list) 
    {
        for (Integer i : list) 
        {
            System.out.print(i + ", ");
        }

        System.out.println();
    }

    public static void main(String[] args) 
    {
        // the option, choose random, not necissarily prime by default
        int opt = 0;
        int crossings = 0;

        // boolean flag for verbose output
        boolean v = false;

        if (args.length < 2)
        {
            // System.out.println("Input to this program is of the form java NaiveShadowGaussGenerator <number> <option1> <option2> <option3>"
            //     + "\n"
            //         +"  where option1 is 'all' or 'random'"
            //     + "\n"
            //         +"  and option2 can be specified as 'prime', or omitted (to geneate non prime Gauss codes as well as prime)"
            //     + "\n"
            //         +"  and option3 can be specided as 'verbose' to include information about the solver in the output"
            //         );
            System.out.println("Input to this program is of the form ... java NaiveShadowGaussGenerator <number> <option1> <option2>"
                + "\nwhere option2 is given as either:"
                + "\n   0 to generate a random code,"
                + "\n   1 to generate all codes,"
                + "\n   2 to generate a random prime codes,"
                + "\n   3 to generate all prime codes."
                + "\noption2 is given as 'verbose' to include information about the solver, or omitted to leave this information out by defult");
        }
        else
        {
            crossings = Integer.parseInt(args[0]);

            if (args[1].equals("1"))
            {
                opt = 1;
            }
            else if (args[1].equals("2"))
            {
                opt = 2;
            }
            else if (args[1].equals("3"))
            {
                opt = 3;
            }

            if (args.length > 2)
            {
                v = true;
            }


            NaiveShadowGaussGenerator sGG = new NaiveShadowGaussGenerator(crossings, opt, v);
            System.out.println(sGG.solutionToString());
        }



    }

    // input is of the form java NaiveShadowGaussGenerator input.txt numberOfColours
}

