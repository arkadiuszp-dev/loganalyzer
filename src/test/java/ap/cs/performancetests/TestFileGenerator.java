package ap.cs.performancetests;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Large input test file generator
 */
public class TestFileGenerator {

    private static int NUMBER_OF_EVENTS = 10000000;
    private static String FILE_NAME = "logs.txt";
    private static int DISTANCE_BASIS = 1000000;

    public static void main(String[] args) {

        List<Integer> toClose = new LinkedList<>();
        Random rand = new Random();

        try (FileWriter fw = new FileWriter(FILE_NAME);
             PrintWriter p = new PrintWriter(fw)){
            for (int i = 0; i < NUMBER_OF_EVENTS; i++) {
                p.println("{\"id\":\"" + i + "\", \"state\":\"STARTED\", \"timestamp\":" + i + "}");
                toClose.add(i);
                if (i >= DISTANCE_BASIS) {
                    int close = toClose.remove(rand.nextInt(toClose.size()));
                    p.println("{\"id\":\"" + close + "\", \"state\":\"FINISHED\", \"timestamp\":" + i + "}");
                }
            }
            for (int i = NUMBER_OF_EVENTS; i < NUMBER_OF_EVENTS + DISTANCE_BASIS; i++) {
                p.println("{\"id\":\"" + toClose.remove(0) + "\", \"state\":\"FINISHED\", \"timestamp\":" + i + "}");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
