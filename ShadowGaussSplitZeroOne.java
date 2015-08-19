import java.util.*;
import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.kernel.model.Model;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.cp.solver.CPSolver;
import choco.kernel.solver.Solver;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.cp.solver.search.integer.varselector.StaticVarOrder;

public class ShadowGaussSplitZeroOne
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
    private IntegerVariable[][] splitLetter;
    private IntegerVariable[][] splitLetterTranspose;
    private IntegerVariable[][] flipLetter;
    private IntegerVariable[][] flipLetterTranspose;

    public ShadowGaussSplitZeroOne(int numOfCrossings) 
    {
       	crossings = numOfCrossings;
    	twiceCrossings = crossings * 2;
    	model = new CPModel();
    	solver = new CPSolver();

    	splitLetter = makeIntVarArray("splitLetter", twiceCrossings, twiceCrossings, 0, 1);
    	splitLetterTranspose = new IntegerVariable[twiceCrossings][twiceCrossings];

    	letter = makeIntVarArray("letter", twiceCrossings, crossings, 0, 1);
    	letterTranspose = new IntegerVariable[crossings][twiceCrossings];

    	flatLetter = new IntegerVariable[twiceCrossings * twiceCrossings];

    	oddLetter = new IntegerVariable[crossings][crossings];
    	evenLetter = new IntegerVariable[crossings][crossings];

    	oddLetterTranspose = new IntegerVariable[crossings][crossings];
    	evenLetterTranspose = new IntegerVariable[crossings][crossings];

    	flipLetter = new IntegerVariable[twiceCrossings][crossings];
    	flipLetterTranspose = new IntegerVariable[crossings][twiceCrossings];
 





    	////////////////////////////////////////////////////////////////////////////////////////////////////
    	// set up the auxiliary arrays, used just to help make setting constraints easier
    	int m = 0;
    	int indexInEvens = 0;
    	int indexInOdds = 0;

    	for (int i = 0; i < twiceCrossings; i++)
    	{
    		for (int j = 0; j < twiceCrossings; j++)
    		{
    			splitLetterTranspose[j][i] = splitLetter[i][j];
    			flatLetter[m] = splitLetter[i][j];
    			m++;
    		}
    	}

    	for (int i = 0; i < twiceCrossings; i++)
    	{
    		for (int j = 0; j < crossings; j++)/////////////////////////
    		{
    			model.addConstraint(eq(letter[i][j], sum(splitLetter[i][2*j], splitLetter[i][(2*j)+1])));
    		}
    	}

    	for (int i = 0; i < twiceCrossings; i++)
    	{
    		for (int j = 0; j < crossings; j++)
    		{
    			letterTranspose[j][i] = letter[i][j];
      	// 		flatLetter[m] = splitLetter[i][j];
    			// m++;
    			
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
    		}
    	}

    	////////////////////////////////////////////////////////////////////////////////////////////////////

    	// lex stuff for Cn
    	// for (int i = 1; i < crossings -1; i++)
    	// {
    	// 	model.addConstraint(lex(letterTranspose[i + 1], letterTranspose[i]));
    	// }

    	// add the constraint that a crossing can only take one value
    	// this corresponds to each row in letter summing to 1
    	for (int i = 0; i < twiceCrossings; i++)
    	{
    		model.addConstraint(eq(sum(splitLetter[i]), constant(1)));
    	}

    	// add the constraint that each crossing number appears twice
    	// this corresponds to each column in letter summing to 2
    	// and thus, each row in letterTranspose summing to 2
    	for (int i = 0; i < twiceCrossings; i++)
    	{
    		model.addConstraint(eq(sum(splitLetterTranspose[i]), constant(1)));
    	}

    	// add a constraint that means we start from the first crossing
    	// that is the first crossing takes the value 1
    	// this letter[0][0] = 1;
    	model.addConstraint(eq(splitLetter[0][0], constant(1)));

    	// add a constraint that means that the next letter in the code must be one
    	// already used or the smallest of those not yet used. that is
    	//
    	// x_n <= max(x_0, ..., x_(n-1) ) + 1
    	//
    	// for out 0/1 model this means that letterTranspose[i] < letterTranspose[i + 1] 
    	for (int i = 1; i < crossings -1; i++)
    	{
    		model.addConstraint(lex(letterTranspose[i + 1], letterTranspose[i]));
    		// model.addConstraint(lex(letter[i+1], letter[i]));
    	}

    	// evenly spaced
    	for (int i = 0; i < crossings; i++)
    	{
    		model.addConstraint(eq(sum(evenLetterTranspose[i]), constant(1)));
    		model.addConstraint(eq(sum(oddLetterTranspose[i]), constant(1)));
    	}

    	

    	//evenly spaced
    	// for (int j = 0; j < twiceCrossings; j += 2)
    	// {
    	// 	System.out.println("j = " + j);
    	// 	for (int i = 1; i < twiceCrossings; i++)
    	// 	{
    	// 		for (int k = i; k < twiceCrossings; k++)
    	// 		{
    	// 			if ((i % 2 == 0) && i != k && (k % 2 == 0))
    	// 			{
    	// 				int jPlusOne = (j+1);
    	// 				System.out.println("setting (" + i + "," + j + ") not equal (" + k + "," + jPlusOne + ")");
    	// 				model.addConstraint(implies(eq(splitLetter[i][j], constant(1)), eq(splitLetter[k][j+1], constant(0))));
    	// 				// model.addConstraint(neq(splitLetter[i][j], splitLetter[k][j+1]));
    	// 			}
    	// 			else if ((i % 2 == 1) && i !=k && (k % 2 == 1))
    	// 			{
    	// 				model.addConstraint(implies(eq(splitLetter[i][j], constant(1)), eq(splitLetter[k][j+1], constant(0))));
    	// 			}
    	// 		} 
    	// 	}
    	// }

    	// //maxSoFar[i] is the max value found in 
     //    IntegerVariable[] maxSoFar = makeIntVarArray("maxSoFar", crossings, 0, 1);

     //    for (int i = 1; i < (twiceCrossings - 1); i ++)
     //    {
     //        model.addConstraint(max(Arrays.copyOfRange(letter, 0, i), maxSoFar[i]));
     //        model.addConstraint(leq(letter[i], plus(maxSoFar[i], constant(1))));
     //    }

 	   	// 2 before 2'
    	for (int j = 2; j < twiceCrossings; j += 2)
    	{
    		// System.out.println("j = " + j);
    		// for (int i = 1; i < twiceCrossings -1; i++)
    		// {
    			// model.addConstraint(leq(max(Arrays.copyOfRange(splitLetter[j], 0, i)), splitLetter[i+1][j+1]));
           		// model.addConstraint(max(Arrays.copyOfRange(letter, 0, i), maxSoFar[i]));
   			// }
   			model.addConstraint(lex(splitLetterTranspose[j],splitLetterTranspose[j+1]));
    	}



    	// "let the solver see the model" - Paddy McGuinness, Presenter of Take Me Out
    	solver.read(model);

    	// set flattLetter as the decision variables
        solver.setVarIntSelector(new StaticVarOrder(solver, solver.getVar(flatLetter)));


		if (solver.solve().booleanValue())
        {
            do
            {
				for (int i=0; i < twiceCrossings; i++)
				{
			    	for (int j=0; j < crossings;j++)
			    	{
			    		// System.out.print("" + solver.getVar(splitLetter[i][j]).getVal() + " ");
			    		// System.out.print("" + solver.getVar(letter[i][j]).getVal() + " ");
			    		if (solver.getVar(letter[i][j]).getVal() == 1)
			    		{
			    			int codeLetter = j + 1;
			    			System.out.print(codeLetter + ", ");
			    			j = crossings;
			    		}
			    	}
			    	// System.out.println();
				}

                System.out.print("\n");
            }
            while (solver.nextSolution().booleanValue());
        }
  //       solver.solve(true);

		// for (int i=0; i < twiceCrossings; i++)
		// {
	 //    	for (int j=0; j< twiceCrossings;j++)
	 //    	{
	 //    		System.out.print("" + solver.getVar(splitLetter[i][j]).getVal() + " ");
	 //    		// if (solver.getVar(letter[i][j]).getVal() == 1)
	 //    		// {
	 //    		// 	int codeLetter = j + 1;
	 //    		// 	System.out.print(codeLetter + ", ");
	 //    		// 	j = crossings;
	 //    		// }
	 //    	}
	 //    	    	System.out.println();	

		// }

		System.out.println();


	System.out.println("feasible: " +solver.isFeasible());
    System.out.println("nbSol: " + solver.getNbSolutions());
    // System.out.println("sol after equivalence stuff: " + solutions.size());
    System.out.println("Nodes: " + solver.getNodeCount() + "   cpu: " +solver.getTimeCount());


    }


	public static void main(String[] args) 
	{
		int numOfCrossings = Integer.parseInt(args[0]);
    	ShadowGaussSplitZeroOne sGZOM = new ShadowGaussSplitZeroOne(numOfCrossings);

	}
}
