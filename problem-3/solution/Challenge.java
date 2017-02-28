import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Challenge {

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(new File("fightingthezombie.in"));

        BufferedWriter out = new BufferedWriter(new FileWriter("output.txt"));

        int zombies = Integer.parseInt(sc.nextLine());
        for(int i = 1; i <= zombies; i++) {
            String[] gameTokens = sc.nextLine().split(" ");
            int zombieHealth = Integer.parseInt(gameTokens[0]);

            String[] spellTokens = sc.nextLine().split(" ");
            List<Spell> spells = new LinkedList<>();
            for(String token: spellTokens) {
                spells.add(new Spell(token));
            }
            double maxPossibility = getPossibilityOfOptimalSpell(spells, zombieHealth);

            String output = String.format("Case #%d: %f", i, maxPossibility);
            out.write(output);
            out.newLine();
        }

        out.close();
    }

    private static double getPossibilityOfOptimalSpell(Collection<Spell> spells, int zombieHealth) throws IOException {
        if(spells == null || (spells.size() == 0))
            return 0;

        double maximumPossibility = 0;
        for(Spell spell: spells) {
            double possibility = spell.calculateKillPossibilityOptimised(zombieHealth);
            maximumPossibility = Math.max(maximumPossibility, possibility);
        }
        return maximumPossibility;
    }

    static class Spell {
        int diceSides;
        int diceRolls;
        int offset;
        double possibilityOfEachSide;

        private Map<Integer, Map<Integer, Double>> memoryByDepthAndDamage;

        Spell(String format) {
            String[] tokens = format.split("(d)|(\\+)|(-)");
            diceRolls = Integer.parseInt(tokens[0]);
            diceSides = Integer.parseInt(tokens[1]);
            possibilityOfEachSide = 1.0 / diceSides;
            if(format.contains("+"))
                offset = Integer.parseInt(tokens[2]);
            else if(format.contains("-"))
                offset = -Integer.parseInt(tokens[2]);

            memoryByDepthAndDamage = new HashMap<>();
            for(int depth = 0; depth <= diceRolls; depth++) memoryByDepthAndDamage.put(depth, new HashMap<>());
        }

        /**
         * Complexity: O(diceSides^diceRolls) worst-case, when all states have to be explored
         * @param health, the health of a zombie, aka the minimum danger required to kill it
         * @return the possibility that the spell  will kill the zombie
         */
        double calculateKillPossibility(int health) throws IOException {
            return performDFS(health, offset, 0);
        }

        /**
         * Algorithm used:
         * - Perform DFS to the state space of all the dice rolls (using values from smallest to biggest)
         * - Stop when the current state >= health, since all subsequent states will have damage >= health
         *
         * @param health, the health of a zombie, aka the minimum danger required to kill it
         * @param currentDamage, the damage produced in the current state
         * @param diceRoll, the number of this dice roll
         * @return the possibility that the current state (with all the past dice rolls and
         * any possible future combinations of dice rolls left) will have >= health
         */
        double performDFS(int health, int currentDamage, int diceRoll) throws IOException {
            /* Irregardless of next dice rolls, any combination will have damage >= health */
            if(currentDamage >= health)
                return 1;
            /* If this was the last dice roll, there is no possibility to have damage >= health */
            if(diceRoll == diceRolls)
                return 0;

            double possibility = 0;
            for(int rolledSide = 1; rolledSide <= diceSides; rolledSide++) {
                possibility += possibilityOfEachSide*performDFS(health, currentDamage + rolledSide, diceRoll+1);
            }
            return possibility;
        }

        /**
         * Complexity: O((diceSides^2)*(diceRolls^2)) upper-bound in worst case
         * @param health, the health of a zombie, aka the minimum danger required to kill it
         * @return the possibility that the spell  will kill the zombie
         */
        double calculateKillPossibilityOptimised(int health) throws IOException {
            return performDFSWithMemoization(health, offset, 0);
        }

        /**
         * Algorithm used:
         * - same with the above algorithm, but memoizes calculated values for each depth as optimisation
         *
         * @param health, the health of a zombie, aka the minimum danger required to kill it
         * @param currentDamage, the damage produced in the current state
         * @param diceRoll, the number of this dice roll
         * @return the possibility that the current state (with all the past dice rolls and
         * any possible future combinations of dice rolls left) will have >= health
         */
        double performDFSWithMemoization(int health, int currentDamage, int diceRoll) throws IOException {
            if(memoryByDepthAndDamage.get(diceRoll).containsKey(currentDamage)) {
                return memoryByDepthAndDamage.get(diceRoll).get(currentDamage);
            }

            double possibility;
            if(currentDamage >= health) { /* Irregardless of next dice rolls, any combination will have damage >= health */
                possibility = 1;
            } else if(diceRoll == diceRolls) /* If this was the last dice roll, there is no possibility to have damage >= health */
                possibility = 0;
            else {
                possibility = 0;
                for (int rolledSide = 1; rolledSide <= diceSides; rolledSide++) {
                    possibility += possibilityOfEachSide * performDFSWithMemoization(health, currentDamage + rolledSide, diceRoll + 1);
                }
            }


            memoryByDepthAndDamage.get(diceRoll).put(currentDamage, possibility);
            return possibility;
        }


    }
}
