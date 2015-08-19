import java.util.Arrays;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.kernel.model.Model;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.cp.solver.CPSolver;
import choco.kernel.solver.Solver;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.cp.solver.search.integer.varselector.StaticVarOrder;

public class Gauss {
    public static void main(String args[]) {
        int n                   = Integer.parseInt(args[0]);
        boolean justCodes = args.length > 1;
        Model model             = new CPModel();
        IntegerVariable[] first = makeIntVarArray("first", n, 0, 2*n-1);
        IntegerVariable[] second = makeIntVarArray("second", n, 1, 2*n-1);
        IntegerVariable[] halfGap = makeIntVarArray("halfGap", n, 0, (n-1)/2);
        
        // code is elements of first followed by elements of second
        IntegerVariable[] code = new IntegerVariable[2*n];

        for (int i=0; i<n; i++) {
            code[i] = first[i];
            code[n+i] = second[i];
        }

        // first[0] equals 0 (symmetry breaking)
        model.addConstraint(eq(first[0], 0));

        // All 2n positions have to be used
        model.addConstraint(choco.Options.C_ALLDIFFERENT_CLIQUE, allDifferent(code));

        // Gaps in nondecreasing order, with ties broken using first in asc. order
        for (int i=0; i<n-1; i++) {
            model.addConstraint(leq(halfGap[i], halfGap[i+1]));
            model.addConstraint(implies(eq(halfGap[i], halfGap[i+1]), lt(first[i], first[i+1])));
        }

        // second is first plus gap plus 1, possibly wrapped around
        for (int i=0; i<n; i++) {
            // TODO: 3*n is an unnecessarily big range for firstPlusGapPlusOne
            IntegerVariable firstPlusGapPlusOne = makeIntVar("f+g+1", 0, 3*n-1, "cp:no_decision");
            model.addConstraint(eq(firstPlusGapPlusOne, plus(plus(first[i], mult(halfGap[i], 2)), 1)));
            model.addConstraint(eq(mod(firstPlusGapPlusOne, 2*n), second[i]));
        }

        // In ambiguous case where gap=n-1, enforce that first is in first
        // half of positions
        if (n%2==1) {
            for (int i=1; i<n; i++) {
                model.addConstraint(
                    implies(eq(halfGap[i], (n-1)/2), lt(first[i], n))
                );
            }
        }

        Solver sol = new CPSolver();
        sol.read(model);
        
        Set<List<Integer>> codes = new TreeSet<List<Integer>>(new Comparator<List<Integer>>() {
            public int compare(List<Integer> l1, List<Integer> l2) {
                int sz = l1.size();
                for (int i=0; i<sz; i++) {
                    if (l1.get(i) < l2.get(i)) return -1;
                    if (l1.get(i) > l2.get(i)) return 1;
                }
                return 0;

            }
        });

        if (sol.solve().booleanValue()) {
            do {

                // Generate Gauss codes
                int[] gaussCode = new int[n*2];
                for (int i=0; i<n; i++) {
                    gaussCode[sol.getVar(first[i]).getVal()] = i+1;
                    gaussCode[sol.getVar(second[i]).getVal()] = i+1;
                }

                // Make Gauss codes lex-smallest (as integer)
                gaussCode = lexRenumber(gaussCode, n);

                //print(gaussCode);
                if (!justCodes) {
                    print(gaussCode);
                    System.out.println("first   gap second");
                    for (int i=0; i<n; i++) {
                        System.out.format("%5d %5d %5d%n",
                                sol.getVar(first[i]).getVal(),
                                sol.getVar(halfGap[i]).getVal()*2,
                                sol.getVar(second[i]).getVal());
                    }
                    System.out.println();
                }

                codes.add(lexMinInClass(gaussCode, n));

                List<Integer> l = new ArrayList<Integer>();
                int[] xs = new int[] {1, 1, 2, 2, 3, 4, 5, 3, 4, 6, 6, 5};
                for (int i=0; i<xs.length; i++)
                    l.add(xs[i]);

                if (lexMinInClass(gaussCode, n).equals(l)) {
                    System.out.print("       ");
                    print(gaussCode);
                }
                    

            } while (sol.nextSolution().booleanValue());
        }

        System.out.println();
        System.out.println("De-duplicated codes:");
        for (List<Integer> c : codes) {
            print(c);
        }
        System.out.println();

        if (true || !justCodes) {
            System.out.println("feasible: " + sol.isFeasible());
            System.out.println("nbSol: " + sol.getNbSolutions());
            System.out.println("nodes: "+ sol.getNodeCount() +"   cpu: "+ sol.getTimeCount());
            System.out.println("Solution count: " + codes.size());
        }
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

    static List<Integer> lexMinInClass(int[] gaussCode, int n) {
        int[] minInClass = gaussCode;
        int[] reversedCode = reversed(gaussCode);
        if (lexLt(lexRenumber(reversedCode, n), gaussCode))
            minInClass = lexRenumber(reversedCode, n);
        for (int i=1; i<n*2; i++) {
            int[] rotatedCode = lexRenumber(rotated(gaussCode, i), n); 
            int[] rotatedReversedCode = lexRenumber(rotated(reversedCode, i), n);
            if (lexLt(rotatedCode, minInClass)) minInClass = rotatedCode;
            if (lexLt(rotatedReversedCode, minInClass)) minInClass = rotatedReversedCode;
        }
        List<Integer> retval = new ArrayList<Integer>();
        for (int i=0; i<n*2; i++) {
            retval.add(minInClass[i]);
        }
        return retval;
    }

    static int[] reversed(int[] arr) {
        int len = arr.length;
        int[] retval = new int[len];
        for (int i=0; i<len; i++) {
            retval[len-1-i] = arr[i];
        }
        return retval;
    }

    static int[] rotated(int[] arr, int rotateBy) {
        int len = arr.length;
        int[] retval = new int[len];
        for (int i=0; i<len; i++) {
            retval[(i+rotateBy)%len] = arr[i];
        }
        return retval;
    }
    
    static boolean lexLt(int[] arr1, int[] arr2) {
        int len = arr1.length;
        for (int i=0; i<len; i++) {
            if (arr1[i] < arr2[i]) return true;
            if (arr1[i] > arr2[i]) return false;
        }
        return false;
    }

    static void print(int[] arr) {
        for (int i=0; i<arr.length; i++) {
            System.out.print(arr[i] + ", ");
        }
        System.out.println();
    }

    static void print(List<Integer> list) {
        for (Integer i : list) {
            System.out.print(i + ", ");
        }
        System.out.println();
    }
}


