import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Hint: A naive solution is to count in reverse order until you find the last tidy number
 * But, instead of decreasing by 1 digit each time, couldn't you go faster ?
 * This solution starts from the smallest digits, finding each digit that breaks the necessary
 * "tidiness" condition, sets this number to 9 and decreases the previous numbers (to stay under the initial number)
 */
public class Challenge {

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(new File("input.txt"));

        BufferedWriter out = new BufferedWriter(new FileWriter("output.txt"));

        int testCasesNo = Integer.parseInt(sc.nextLine());
        for(int test = 0; test < testCasesNo; test++) {
            long number = Long.parseLong(sc.nextLine());
            int[] digits = Long.toString(number).chars().map(a->a-'0').toArray();

            for(int i = digits.length-1; i > 0; i--) {
                int currentDigit = digits[i];
                int previousDigit = digits[i-1];

                if(!(previousDigit <= currentDigit)) {
                    digits[i-1] = decreaseWithOverflow(previousDigit);
                    for(int j = i; j < digits.length; j++) {
                        digits[j] = 9;
                    }
                }
            }

            StringBuilder builder = new StringBuilder();
            for(int i = 0; i < digits.length; i++) {
                builder.append(digits[i]);
            }
            long lastTidyNumber = Long.parseLong(builder.toString());

            out.write(String.format("Case #%d: %d", test+1, lastTidyNumber));
            out.newLine();
        }

        out.close();
    }

    private static int decreaseWithOverflow(int digit) {
        if(digit == 0) {
            return 9;
        } else {
            return digit-1;
        }
    }

}
