import java.io.*;
import java.util.Scanner;

public class Challenge {

    private static long CENTER_X = 50;
    private static long CENTER_Y = 50;
    private static long RADIUS = 50;
    private static double ANGLE;

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(new File("input.txt"));

        BufferedWriter out = new BufferedWriter(new FileWriter("output.txt"));

        int lines = Integer.parseInt(sc.nextLine());

        for(int i = 1; i <= lines; i++) {
            String[] line_tokens = sc.nextLine().split(" ");
            int percentage = Integer.parseInt(line_tokens[0]);
            int x = Integer.parseInt(line_tokens[1]);
            int y = Integer.parseInt(line_tokens[2]);
            ANGLE = 360*(percentage/100.0);

            double r = getR(x, y);
            double angle = getAngle(x, y);
            String color = isInsideCircleRange(r, angle) ? "black" : "white";

            out.write(String.format("Case #%d: %s", i, color));
            out.newLine();
        }

        out.close();
    }

    static boolean isInsideCircleRange(double r, double angle) {
        return r <= RADIUS && angle <= ANGLE;
    }

    static double getR(long x, long y) {
        return Math.pow( Math.pow(x - CENTER_X, 2) + Math.pow(y - CENTER_Y, 2), 0.5);
    }

    /**
     * @return angle, between 0 and 360 degrees
     */
    static double getAngle(long x, long y) {
        double angle = 90 - Math.toDegrees(Math.atan2((y - CENTER_Y), (x - CENTER_X)));
        if(angle >= 0) return angle;
        else return (360+angle);
    }
}
