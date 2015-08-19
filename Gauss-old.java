import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.kernel.model.Model;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.cp.solver.CPSolver;
import choco.kernel.solver.Solver;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.cp.solver.search.integer.varselector.StaticVarOrder;

public class Gauss-old {
    public static void main(String args[]) {
        int n                   = Integer.parseInt(args[0]);
        boolean justCodes = args.length > 1;
        Model model             = new CPModel();
        IntegerVariable[] first = makeIntVarArray("first", n, 0, 2*n-1);
        IntegerVariable[] second = makeIntVarArray("second", n, 0, 2*n-1);
        IntegerVariable[] gap = makeIntVarArray("gap", n, 0, n-1);
        // IntegerVariable[] secondOrderGap = makeIntVarArray("secondOrderGap")
        
        // for implementing nondecreasing gap tie-broken using first
        // with lex
        IntegerVariable[][] gapFirst = new IntegerVariable[n][2];
        
        // code is elements of first followed by elements of second
        IntegerVariable[] code = new IntegerVariable[2*n];

        for (int i=0; i<n; i++) {
            code[i] = first[i];
            code[n+i] = second[i];
            gapFirst[i][0] = gap[i];
            gapFirst[i][1] = first[i];
        }

        // first[0] equals 0 (symmetry breaking)
        model.addConstraint(eq(first[0], 0));

        // All 2n positions have to be used
        model.addConstraint(allDifferent(code));

        // All gaps are even
        for (int i=0; i<n; i++) {
            model.addConstraint(eq(mod(gap[i], 2), 0));
        }

        // Gaps in ascending order, with ties broken using first in asc. order
        // TODO: could this be done more simply using lexChainEq?
        for (int i=0; i<n-1; i++) {
            model.addConstraint(lexEq(gapFirst[i], gapFirst[i+1]));
        }

        // second is first plus gap plus 1, possibly wrapped around
        for (int i=0; i<n; i++) {
            // TODO: 4*n is an unnecessarily big range for firstPlusGapPlusOne
            IntegerVariable firstPlusGapPlusOne = makeIntVar("f+g+1", 0, (2*n)+2);
            model.addConstraint(eq(firstPlusGapPlusOne, plus(plus(first[i], gap[i]), 1)));
            model.addConstraint(eq(mod(firstPlusGapPlusOne, 2*n), second[i]));
        }

        // In ambiguous case where gap=n-1, enforce that first is in first
        // half of positions
        for (int i=0; i<n; i++) {
            model.addConstraint(
                    implies(
                            eq(gap[i], n-1), lt(first[i], second[i])
                    )
            );
        }

        for (int i = 2; i < n -1; i++)
        {
            model.addConstraint(lt(first[i], first[i+1]));
        }

        Solver sol = new CPSolver();
        sol.read(model);
        //IntDomainVar [] v = sol.getVar(flatZ);
        sol.setVarIntSelector(new StaticVarOrder(sol, sol.getVar(code)));
        //System.out.println("solved: " + sol.solve());
        //for (int i=0;i<n;i++){
        //    for (int j=0;j<n;j++)
        //	System.out.print("(" + sol.getVar(X[i][j]).getVal()
        //			 +","+ sol.getVar(Y[i][j]).getVal() +")");
        //   System.out.println();
        //}

        if (sol.solve().booleanValue()) {
            do {
                if (!justCodes) System.out.println("first gap second");

                // Generate Gauss codes
                int[] gaussCode = new int[n*2];
                for (int i=0; i<n; i++) {
                    gaussCode[sol.getVar(first[i]).getVal()] = i+1;
                    gaussCode[sol.getVar(second[i]).getVal()] = i+1;
                }

                // Make Gauss codes lex-smallest (as integer)
        //        gaussCode = lexRenumber(gaussCode, n);

                // Print Gauss codes
                for (int i=0; i<2*n; i++) {
                    System.out.print(gaussCode[i] + ", ");
                }
                System.out.println();
                if (!justCodes) {
                    for (int i=0; i<n; i++) {
                        System.out.format("%d     %d     %d%n",
                                sol.getVar(first[i]).getVal(),
                                sol.getVar(gap[i]).getVal(),
                                sol.getVar(second[i]).getVal());
                    }
                    System.out.println();
                }
            } while (sol.nextSolution().booleanValue());
        }
        if (!justCodes) {
            System.out.println("feasible: " + sol.isFeasible());
            System.out.println("nbSol: " + sol.getNbSolutions());
            System.out.println("nodes: "+ sol.getNodeCount() +"   cpu: "+ sol.getTimeCount());
        }
        
        // another view
        //for (int i=0;i<n;i++){
        //    for (int j=0;j<n;j++)
        //	System.out.print(sol.getVar(Z[i][j]).getVal() +" ");
        //    System.out.println();
        //}
    }

    static int[] lexRenumber(int[] gaussCode, int n) {
        int[] retval = new int[n*2];
        int[] newNums = new int[n];
        int maxSoFar = 0;
        
        for (int i=0; i<n*2; i++) {
            if (newNums[gaussCode[i]-1]==0) {
                newNums[gaussCode[i]-1] = ++maxSoFar;
            }
            retval[i] = newNums[gaussCode[i]-1];
        }
        return retval;
    }
}
