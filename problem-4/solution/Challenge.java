import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Challenge {

    static int totalDays;
    static int totalPies;

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(new File("input.txt"));

        BufferedWriter out = new BufferedWriter(new FileWriter("output.txt"));

        int eatingSprees = Integer.parseInt(sc.nextLine());
        for(int spree = 1; spree <= eatingSprees; spree++) {
            String[] tokens = sc.nextLine().split(" ");
            totalDays = Integer.parseInt(tokens[0]);
            totalPies = Integer.parseInt(tokens[1]);

            List<Integer>[] dayPies = new List[totalDays];
            for(int day = 0; day < totalDays; day++) {
                String[] piesTokens = sc.nextLine().split(" ");
                List<Integer> piesOfDay = new ArrayList<>();
                for(int i = 0; i < piesTokens.length; i++) {
                    int pie = Integer.parseInt(piesTokens[i]);
                    piesOfDay.add(pie);
                }
                Collections.sort(piesOfDay);
                dayPies[day] = piesOfDay;
            }

            long minCost = calculateOptimalBill(dayPies);
            out.write(String.format("Case #%d: %d", spree, minCost));
            out.newLine();
        }

        out.close();
    }

    /**
     * Greedy algorithm:
     * - Calculates the additional tax per pie and adds it
     * - Maintains all the pies in a minimum heap (priority queue), making sure that pies are added after the available day
     * - Each day, retrieves one pie (this might be from previous day, since all pies are maintained in the queue)
     * @return the optimal cost
     */
    private static long calculateOptimalBill(List<Integer>[] dayPies) {
        int totalBill = 0;

        PriorityQueue<Integer> sortedPies = new PriorityQueue<>();
        for(int day = 0; day < dayPies.length; day++) {
            List<Integer> pies = dayPies[day];
            int previousTaxSum = 0;
            for(int i = 0; i < pies.size(); i++) {
                int pie = pies.get(i);
                int tax = (int)Math.pow(i+1, 2) - previousTaxSum;
                previousTaxSum += tax;
                int totalPieCost = pie + tax;
                sortedPies.add(totalPieCost);
            }
            totalBill += sortedPies.poll();
        }
        return totalBill;
    }

}
