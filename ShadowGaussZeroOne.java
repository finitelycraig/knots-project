import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.kernel.model.Model;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.cp.solver.CPSolver;
import choco.kernel.solver.Solver;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.cp.solver.search.integer.varselector.StaticVarOrder;

public class ShadowGaussZeroOne
{
	private Model model;  
    private Solver solver;
    private int crossings;
    private int twiceCrossings;

    // letter[i][j] is the ith letter in the Gauss code with value j, if letter[i][j] = 1
    private IntegerVariable[][] letter;
    private IntegerVariable[][] letterTranspose;
    private IntegerVariable[] flatLetter;
    private IntegerVariable[][] oddLetter;
    private IntegerVariable[][] evenLetter;
    private IntegerVariable[][] oddLetterTranspose;
    private IntegerVariable[][] evenLetterTranspose;

    public ShadowGaussZeroOne(int numOfCrossings)
    {
    	crossings = numOfCrossings;
    	twiceCrossings = crossings * 2;
    	model = new CPModel();
    	solver = new CPSolver();

    	letter = makeIntVarArray("letter", twiceCrossings, crossings, 0, 1);
    	letterTranspose = new IntegerVariable[crossings][twiceCrossings];
    	flatLetter = new IntegerVariable[crossings * twiceCrossings];

    	oddLetter = new IntegerVariable[crossings][crossings];
    	evenLetter = new IntegerVariable[crossings][crossings];

    	oddLetterTranspose = new IntegerVariable[crossings][crossings];
    	evenLetterTranspose = new IntegerVariable[crossings][crossings];

    	// set up the auxiliary arrays, used just to help make setting constraints easier
    	int k = 0;
    	int indexInEvens = 0;
    	int indexInOdds = 0;

    	for (int i = 0; i < twiceCrossings; i++)
    	{
    		for (int j = 0; j < crossings; j++)
    		{
    			letterTranspose[j][i] = letter[i][j];
    			
    			if (i % 2 == 0)
    			{
    				indexInEvens = i / 2;
    				evenLetter[indexInEvens][j] = letter[i][j];
    				evenLetterTranspose[j][indexInEvens] = letter[i][j];
    			}
    			else
    			{
    				indexInOdds = i / 2;
    				oddLetter[indexInOdds][j] = letter[i][j];
    				oddLetterTranspose[j][indexInOdds] = letter[i][j]; 
    			}

    			flatLetter[k] = letter[i][j];
    			k++;
    		}
    	}

    	// add the constraint that a crossing can only take one value
    	// this corresponds to each row in letter summing to 1
    	for (int i = 0; i < twiceCrossings; i++)
    	{
    		model.addConstraint(eq(sum(letter[i]), constant(1)));
    	}

    	// add the constraint that each crossing number appears twice
    	// this corresponds to each column in letter summing to 2
    	// and thus, each row in letterTranspose summing to 2
    	for (int i = 0; i < crossings; i++)
    	{
    		model.addConstraint(eq(sum(letterTranspose[i]), constant(2)));
    	}

    	// add a constraint that means we start from the first crossing
    	// that is the first crossing takes the value 1
    	// this letter[0][0] = 1;
    	model.addConstraint(eq(letter[0][0], constant(1)));

    	// evenly spaced
    	for (int i = 0; i < crossings; i++)
    	{
    		model.addConstraint(eq(sum(evenLetterTranspose[i]), constant(1)));
    		model.addConstraint(eq(sum(oddLetterTranspose[i]), constant(1)));
    	}

       	// add a constraint that means that the next letter in the code must be one
    	// already used or the smallest of those not yet used. that is
    	//
    	// x_n <= max(x_0, ..., x_(n-1) ) + 1
    	//
    	// for our 0/1 model this means that letterTranspose[i] < letterTranspose[i + 1] 
    	for (int i = 0; i < crossings -1; i++)
    	{
    		model.addConstraint(lex(letterTranspose[i + 1], letterTranspose[i]));
    	}

    	// "let the solver see the model" - Paddy McGuinness, Presenter of Take Me Out
    	solver.read(model);

    	// set flattLetter as the decision variables
        solver.setVarIntSelector(new StaticVarOrder(solver, solver.getVar(flatLetter)));

        // print all solutions
		if (solver.solve().booleanValue())
        {
            do
            {
				for (int i=0; i < twiceCrossings; i++)
				{
			    	for (int j=0; j< crossings;j++)
			    	{
			    		// System.out.print("" + solver.getVar(letter[i][j]).getVal() + " ");

			    		// System.out.print("" + solver.getVar(evenLetter[i][j]).getVal() + " ");

			    		if (solver.getVar(letter[i][j]).getVal() == 1)
			    		{
			    			int codeLetter = j + 1;
			    			System.out.print(codeLetter + ", ");
			    			j = crossings;
			    		}
			    	}
			    	// System.out.println();
			    	// for (int j=0; j< crossings;j++)
			    	// {
			    	// 	// System.out.print("" + solver.getVar(letter[i][j]).getVal() + " ");

			    	// 	System.out.print("" + solver.getVar(oddLetter[i][j]).getVal() + " ");

			    	// 	// if (solver.getVar(letter[i][j]).getVal() == 1)
			    	// 	// {
			    	// 	// 	int codeLetter = j + 1;
			    	// 	// 	System.out.print(codeLetter + ", ");
			    	// 	// 	j = crossings;
			    	// 	// }
			    	// }
			    	// System.out.println();
				}

                System.out.print("\n");
            }
            while (solver.nextSolution().booleanValue());
        }

		// solver.solve(true);

		// for (int i=0; i < twiceCrossings; i++)
		// {
	 //    	for (int j=0; j< crossings;j++)
	 //    	{
	 //    		// System.out.print("" + solver.getVar(letter[i][j]).getVal() + " ");
	 //    		if (solver.getVar(letter[i][j]).getVal() == 1)
	 //    		{
	 //    			int codeLetter = j + 1;
	 //    			System.out.print(codeLetter + ", ");
	 //    			j = crossings;
	 //    		}
	 //    	}
		// }
    	// System.out.println();	


	System.out.println("feasible: " +solver.isFeasible());
    System.out.println("nbSol: " + solver.getNbSolutions());
    // System.out.println("sol after equivalence stuff: " + solutions.size());
    System.out.println("Nodes: " + solver.getNodeCount() + "   cpu: " +solver.getTimeCount());


    }

    public static void main(String[] args) 
    {
    	int numOfCrossings = Integer.parseInt(args[0]);
    	ShadowGaussZeroOne sGZOM = new ShadowGaussZeroOne(numOfCrossings);
    		
    }
}