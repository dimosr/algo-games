import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;

public class Challenge {

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(new File("lazyloading.in"));

        BufferedWriter out = new BufferedWriter(new FileWriter("output.txt"));

        int days = Integer.parseInt(sc.nextLine());
        for(int day = 1; day <= days; day++) {
            int items = Integer.parseInt(sc.nextLine());
            Integer[] itemWeightsOrderedAsc = new Integer[items];
            for(int i = 0; i < items; i++) {
                itemWeightsOrderedAsc[i] = Integer.parseInt(sc.nextLine());
            }
            Arrays.sort(itemWeightsOrderedAsc);

            int maxTrips = calculateMaxTrips(new LinkedList(Arrays.asList(itemWeightsOrderedAsc)));

            String output = String.format("Case #%d: %d", day, maxTrips);
            out.write(output);
            out.newLine();
        }

        out.close();
    }

    /**
     * Complexity: O(N), but note that the provided list has to be ordered which corresponds to O(N*logN)
     * Not using generic interface List, but LinkedList to leverage O(1) operations for pollFirst(), pollLast()
     * The algorithm works in the following way:
     * - put the heaviest item in the top
     * - fill with the lightest items
     * @param itemWeights, a list of item Weights sorted in ascending order
     * @return the maximum number of trips, so that each trip is "considered" to weight >= 50 pounds
     */
    private static int calculateMaxTrips(LinkedList<Integer> itemWeights) {
        LinkedList<Trip> trips = new LinkedList<>();
        while(itemWeights.size() > 0) {
            int heaviestItem = itemWeights.peekLast();

            /* If the remaining items cannot compose a trip >= 50 pounds, they are included in the last trip */
            if(heaviestItem * itemWeights.size() < 50) {
                Trip lastTrip = trips.peekLast();
                while(!itemWeights.isEmpty())
                    lastTrip.items.addLast(itemWeights.pollFirst());
            } else { /* otherwise, compose a new trip */
                Trip currentTrip = new Trip();
                int minItemsForTrip = (int) Math.ceil((50.0 / heaviestItem));
                currentTrip.items.addLast(itemWeights.pollLast());
                for(int i = 1; i < minItemsForTrip; i++)
                    currentTrip.items.addLast(itemWeights.pollFirst());
                trips.addLast(currentTrip);
            }
        }
        return trips.size();
    }

    static class Trip {
        LinkedList<Integer> items;

        Trip() {
            items = new LinkedList<>();
        }
    }

}
