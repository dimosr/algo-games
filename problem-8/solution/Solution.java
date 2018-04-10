import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Solution {

    private static int INITIAL_POWER = 1;

    /**
     * Time Complexity: O(N*logN)
     * Space Complexity: O(N)
     *
     * N: number of program moves
     */
    public static void main(String[] args) {
        Scanner in = new Scanner(new BufferedReader(new InputStreamReader(System.in)));
        int testCasesNum = Integer.parseInt(in.nextLine());
        for (int i = 0; i < testCasesNum; i++) {
            String[] lineTokens = in.nextLine().split(" ");
            int defense = Integer.parseInt(lineTokens[0]);
            List<Move> programMoves = parseProgramMoves(lineTokens[1]);
            Queue<Hack> hacksMinHeap = calculateInitialHeap(programMoves);
            int totalDamage = calculateDamage(programMoves);
            Optional<Integer> minNeededHacks = calculateMinNeededHacks(programMoves, hacksMinHeap, totalDamage, defense);

            if(minNeededHacks.isPresent()) {
                System.out.println(String.format("Case #%d: %d", i, minNeededHacks.get()));
            } else {
                System.out.println(String.format("Case #%d: IMPOSSIBLE", i) );
            }
        }
    }

    private static int calculateDamage(List<Move> programMoves) {
        int damage = 0;
        int currentPower = INITIAL_POWER;
        for(int i = 0; i < programMoves.size(); i++){
            Move move = programMoves.get(i);
            if(move == Move.CHARGE) {
                currentPower *= 2;
            } else if(move == Move.SHOOT) {
                damage += currentPower;
            }
        }

        return damage;
    }

    private static Optional<Integer> calculateMinNeededHacks(final List<Move> programMoves, final Queue<Hack> hacksMaxHeap, final int damage, final int defense) {
        int currentDamage = damage;
        int hacksUsed = 0;

        while(currentDamage > defense && !hacksMaxHeap.isEmpty()) {
            Hack usedHack = hacksMaxHeap.poll();
            hacksUsed++;
            currentDamage -= usedHack.gain;

            /* Check for newly appeared hacks, after the swap */
            Collections.swap(programMoves, usedHack.position, usedHack.position+1);
            if((usedHack.position-1 > 0) && programMoves.get(usedHack.position-1) == Move.CHARGE) {
                hacksMaxHeap.add(new Hack(usedHack.position-1, usedHack.gain/2));
            }
            if((usedHack.position+2 < programMoves.size()) && programMoves.get(usedHack.position+2) == Move.SHOOT) {
                hacksMaxHeap.add(new Hack(usedHack.position+1, usedHack.gain));
            }
        }

        if(currentDamage > defense) {
            return Optional.empty();
        } else {
            return Optional.ofNullable(hacksUsed);
        }
    }

    private static List<Move> parseProgramMoves(final String program) {
        return program.chars()
                .mapToObj(i -> (char) i)
                .map(Move::fromSymbol)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private static PriorityQueue<Hack> calculateInitialHeap(final List<Move> programMoves) {
        PriorityQueue<Hack> hacksHeap = new PriorityQueue<>(Hack.getPositionComparator().reversed());
        int currentPower = 1;

        for(int i = 0; i < programMoves.size()-1; i++) {
            if(programMoves.get(i) == Move.CHARGE) {
                if(programMoves.get(i+1) == Move.SHOOT) {
                    hacksHeap.add(new Hack(i, currentPower)); //by swapping, shoot will be performed before charging, damaging currentPower instead of currentPower*2
                    i++; //skipping the shoot
                }

                currentPower *= 2; //charging multiplies power by 2
            }
        }

        return hacksHeap;
    }

    private enum Move {
        SHOOT('S'),
        CHARGE('C');

        private char symbol;

        Move(final char symbol) {
            this.symbol = symbol;
        }

        public static Move fromSymbol(final char symbol) {
            switch( symbol ) {
                case 'S': return SHOOT;
                case 'C': return CHARGE;
                default: throw new IllegalArgumentException("Invalid symbol");
            }
        }
    }

    /**
     * Corresponds to a CS pattern, which could be swapped to SC
     * - position: the starting point of the pattern (aka position of C)
     * - gain: the gain from the swap
     */
    static private class Hack {
        public int position;
        public int gain;

        Hack(final int position, final int gain) {
            this.position = position;
            this.gain = gain;
        }

        static Comparator<Hack> getPositionComparator() {
            return Comparator.comparing(hack -> hack.gain);
        }
    }
}
