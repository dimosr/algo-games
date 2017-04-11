import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * --- Hints ---
 * For the small dataset: a BFS search is efficient enough
 * For the large dataset: notice that the state of the first 
 * pancake completely determines what we have to do with the 
 * first 3 pancakes and extend that thought to the whole table
*/
public class Challenge {

    static Deque<State> states;
    static Set<Table> alreadySeen;

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(new File("input.txt"));

        BufferedWriter out = new BufferedWriter(new FileWriter("output.txt"));

        int testCasesNo = Integer.parseInt(sc.nextLine());
        for(int test = 0; test < testCasesNo; test++) {
            String[] tokens = sc.nextLine().split(" ");
            String tableString = tokens[0];

            List<Pancake> pancakes = convertStringToPancakes(tableString);
            int flipperSize = Integer.parseInt(tokens[1]);

            states = new ArrayDeque<>();
            Table initialTable = new Table(pancakes);
            states.addFirst(new State(initialTable, 0));
            alreadySeen = new HashSet<>();
            alreadySeen.add(initialTable);


            int flipsRequired = fixTableGreedy(flipperSize);

            if(flipsRequired < 0) {
                out.write(String.format("Case #%d: IMPOSSIBLE", (test+1)));
            } else {
                out.write(String.format("Case #%d: %d", test+1, flipsRequired));
            }
            out.newLine();
        }

        out.close();
    }

    private enum Pancake {
        UPSIDE, DOWNSIDE;

        public Pancake flip() {
            if(this == UPSIDE) {
                return DOWNSIDE;
            } else {
                return UPSIDE;
            }
        }
    }

    private static class Table {
        private List<Pancake> pancakes;
        private int numberOfUpsidePancakes = 0;

        public Table(List<Pancake> pancakes) {
            this.pancakes = new ArrayList<>(pancakes);
            for(Pancake pancake: this.pancakes) {
                if(pancake == Pancake.UPSIDE) {
                    numberOfUpsidePancakes++;
                }
            }
        }

        public boolean mayImproveByFlip(int flipStart, int flipEnd) {
            for(int i = flipStart; i <= flipEnd; i++) {
                if(pancakes.get(i) == Pancake.DOWNSIDE) {
                    return true;
                }
            }

            return false;
        }

        private Table flipTable(int flipStart, int flipEnd) {
            List<Pancake> flippedTable = new ArrayList<>(pancakes);
            for(int i = flipStart; i <= flipEnd; i++) {
                Pancake currentPancake = pancakes.get(i);
                flippedTable.set(i, currentPancake.flip());
            }
            return new Table(flippedTable);
        }

        private boolean isReady() {
            return numberOfUpsidePancakes == this.pancakes.size();
        }

        public int size() {
            return pancakes.size();
        }

        public List<Pancake> getPancakes() {
            return pancakes;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Table table = (Table) o;
            return Objects.equals(pancakes, table.pancakes);
        }

        @Override
        public int hashCode() {
            return Objects.hash(pancakes);
        }
    }

    private static class State {
        public Table tableState;
        public int currentFlips;

        public State(Table table, int currentFlips) {
            this.tableState = table;
            this.currentFlips = currentFlips;
        }
    }

    private static List<Pancake> convertStringToPancakes(String tableString) {
        List<Pancake> pancakes = new ArrayList<>(tableString.length());
        for(int i = 0; i < tableString.length(); i++) {
            char symbol = tableString.charAt(i);
            if (symbol == '+') {
                pancakes.add(Pancake.UPSIDE);
            } else if (symbol == '-') {
                pancakes.add(Pancake.DOWNSIDE);
            }
        }
        return pancakes;
    }

    /**
     * Executes Depth-First-Search to find the solution
     * Complexity: O(V+E) = O(2^(N-K+1))
     */
    private static int fixTable(int flipperSize) {
        while(states.size() > 0) {
            State currentState = states.pollLast();
            if(currentState.tableState.isReady()) {
                return currentState.currentFlips;
            }

            int flipperStart = 0, flipperEnd = flipperSize - 1;
            Table currentTable = currentState.tableState;
            while(flipperEnd < currentTable.size()) {
                if(currentTable.mayImproveByFlip(flipperStart, flipperEnd)) {
                    Table flippedTable = currentTable.flipTable(flipperStart, flipperEnd);
                    if(!alreadySeen.contains(flippedTable)) {
                        states.addFirst(new State(flippedTable, currentState.currentFlips + 1));
                        alreadySeen.add(flippedTable);
                    }
                }

                flipperStart++;
                flipperEnd++;
            }
        }

        return -1;
    }

    /**
     * Uses greedy approach to find the required number of flips
     * Complexity: O(N)
     */
    private static int fixTableGreedy(int flipperSize) {
        List<Pancake> pancakes = states.poll().tableState.getPancakes();
        int flipsDoneInTotal = 0;
        int flipsForCurrentPancake = 0;
        int[] flipsToUndo = new int[pancakes.size()];

        for(int i = 0; i < (pancakes.size() - flipperSize + 1); i++) {
            flipsForCurrentPancake -= flipsToUndo[i];

            Pancake flippedPancake;
            if(flipsForCurrentPancake % 2 == 1) {     //odd number of flips - have to take into account them
                flippedPancake = pancakes.get(i).flip();
            } else {
                flippedPancake = pancakes.get(i);
            }

            if(flippedPancake == Pancake.DOWNSIDE) {
                flipsDoneInTotal++;
                flipsForCurrentPancake++;
                if((i+flipperSize) < flipsToUndo.length) {
                    flipsToUndo[i+flipperSize]++;
                }
                pancakes.set(i, Pancake.UPSIDE);
            }
        }

        for(int i = (pancakes.size() - flipperSize + 1); i < pancakes.size(); i++) {
            flipsForCurrentPancake -= flipsToUndo[i];

            Pancake flippedPancake;
            if(flipsForCurrentPancake % 2 == 1) {     //odd number of flips - have to take into account them
                flippedPancake = pancakes.get(i).flip();
            } else {
                flippedPancake = pancakes.get(i);
            }

            if(flippedPancake == Pancake.DOWNSIDE) {
                return -1;
            }
        }

        return flipsDoneInTotal;
    }

}
