import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Hint
 * A naive solution could just perform the simulation of the rules mentioned
 * For an optimised solution, notice that what matters only is the number of 
 * consecutive stalls of each piece
 * For an ultimate optimisation (not done here), notice that we could only 
 * store the int corresponding to the number of consecutive stalls 
 * (not the actual row of stalls) parts with the. Then, after observing that the 
 * the same number of consecutive rows are repeated, we could calculate their result only once
 */
public class Challenge {

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(new File("input.txt"));

        BufferedWriter out = new BufferedWriter(new FileWriter("output.txt"));

        int testCasesNo = Integer.parseInt(sc.nextLine());
        for(int test = 0; test < testCasesNo; test++) {
            String[] tokens = sc.nextLine().split(" ");
            int stallsNumber = Integer.parseInt(tokens[0]);
            int people = Integer.parseInt(tokens[1]);

            Result result = calculateOptimised(stallsNumber, people);
            out.write(String.format("Case #%d: %d %d", test+1, result.maxVal, result.minVal));
            out.newLine();
        }

        out.close();
    }

    private static class Result{
        public int maxVal;
        public int minVal;

        public Result(int maxVal, int minVal) {
            this.maxVal = maxVal;
            this.minVal = minVal;
        }
    }

    /**
     * Complexity: O(N*K)
     */
    private static Result calculateWithSimulation(int stallsNumber, int people) {
        Result result = null;
        int[] stalls = new int[stallsNumber];  //0 considered as empty, 1 considered as reserved
        for(int person = 0; person < people; person++) {
            int[] left_empty = new int[stallsNumber];
            int[] right_empty = new int[stallsNumber];


            int lastEmptyFromLeft = -1;
            for(int j = 0; j < stalls.length; j++) {
                left_empty[j] = Math.abs((j-lastEmptyFromLeft-1));
                if(stalls[j] == 1) {
                    lastEmptyFromLeft = j;
                }
            }
            int lastEmptyFromRight = stalls.length;
            for(int j = stalls.length-1; j >= 0; j--) {
                right_empty[j] = Math.abs((lastEmptyFromRight-j-1));
                if(stalls[j] == 1) {
                    lastEmptyFromRight = j;
                }
            }

            int[] min_LeftAndRight = new int[stallsNumber];
            for(int j = 0; j < stalls.length; j++) {
                min_LeftAndRight[j] = Math.min(left_empty[j], right_empty[j]);
            }
            int[] max_LeftAndRight = new int[stallsNumber];
            for(int j = 0; j < stalls.length; j++) {
                max_LeftAndRight[j] = Math.max(left_empty[j], right_empty[j]);
            }

            List<Integer> firstSelection = new LinkedList<>();
            int selectedStall = -1, biggestMin = -1;
            for(int j = 0; j < stalls.length; j++) {
                if(stalls[j] == 0) {    //if stall is empty
                    if(min_LeftAndRight[j] > biggestMin) {
                        biggestMin = min_LeftAndRight[j];
                    }

                }
            }
            for(int j = 0; j < stalls.length; j++) {
                if(stalls[j] == 0) {
                    if(min_LeftAndRight[j] == biggestMin) {
                        firstSelection.add(j);
                    }
                }
            }
            if(firstSelection.size() == 1) {
                selectedStall = firstSelection.get(0);
            } else {
                List<Integer> secondSelection = new LinkedList<>();
                int biggestMax = -1;
                for(Integer stall: firstSelection) {
                    if(max_LeftAndRight[stall] > biggestMax) {
                        biggestMax = max_LeftAndRight[stall];
                    }
                }
                for(Integer stall: firstSelection) {
                    if(max_LeftAndRight[stall] == biggestMax) {
                        secondSelection.add(stall);
                    }
                }
                selectedStall = secondSelection.get(0);
            }
            stalls[selectedStall] = 1;  //reserve stall

            /* Last person - return pre-selection numbers as solution */
            if(person == people - 1) {
                result = new Result(max_LeftAndRight[selectedStall], min_LeftAndRight[selectedStall]);
            }
            /* ------------------------------------------------------ */
        }
        return result;
    }

    private static class Row implements Comparable<Row> {
        int start;
        int end;

        public Row(int start, int end) {
            this.start = start;
            this.end = end;
        }

        public int size() {
            return end-start+1;
        }

        /**
         * Returns the middle, if there is only one
         * or the left of the 2 middles, if there are two of them
         */
        public int getMiddle() {
            return start + ((end-start)/2);
        }

        @Override
        public int compareTo(Row o) {
            int sizeComparison = new Integer(o.size()).compareTo(this.size());

            if(sizeComparison == 0) {   //break ties
                if(this == o) {
                    return 0;       //maintain consistency with equals()
                } else {
                    return -1;
                }
            }

            return sizeComparison;
        }
    }

    /**
     * Finds the largest sub row of consecutive empty free stalls and reserves the cetral one
     * Complexity: O(K*logK)
     */
    private static Result calculateOptimised(int stallsNumber, int people) {
        Result result = null;

        TreeSet<Row> heap = new TreeSet<>();
        heap.add(new Row(0, stallsNumber-1));

        for(int person = 0; person < people; person++) {
            Row row = heap.pollFirst();
            int middle = row.getMiddle();

            Row leftSubRow = new Row(row.start, middle-1);
            Row rightSubRow = new Row(middle+1, row.end);
            if(leftSubRow.size() > 0) {
                heap.add(leftSubRow);
            }
            if(rightSubRow.size() > 0) {
                heap.add(rightSubRow);
            }

            /* Last person - return pre-selection numbers as solution */
            if(person == people - 1) {
                result = new Result(rightSubRow.size(), leftSubRow.size());
            }
            /* ------------------------------------------------------ */
        }
        return result;
    }

}
