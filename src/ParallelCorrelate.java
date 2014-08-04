/**
 * @author Jethro Muller   MLLJET001
 * @version 0.0.1
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;

public class ParallelCorrelate {
    private final static int NS_TO_MS = 1000000;
    private static ForkJoinPool fjPool = new ForkJoinPool();
    public static final int SEQUENTIAL_CUTOFF = 500;
    private static final int NUM_RUNS = 12;

    private static float[] getDataPoints(String filePath) {
        Scanner fileScanner = null;
        // Opens the file
        try {
            fileScanner = new Scanner(new File(filePath));
        } catch (FileNotFoundException fileError) {
            System.out.println("Files not found. Make sure you provided the correct paths.");
            System.exit(0);
        }

        int numLines = fileScanner.nextInt();
        float[] dataPoints = new float[numLines];
        int dataPointIndex = 0;
        while (fileScanner.hasNextFloat()) {
            dataPoints[dataPointIndex] = fileScanner.nextFloat();
            dataPointIndex++;
        }

        fileScanner.close();
        return dataPoints;
    }

    private static void correlateData(float[] transmittedDataPoints,
                                         float[] receivedDataPoints, float[] crossCorrelatedData) {
        fjPool.invoke(new CorrelateArray(transmittedDataPoints, receivedDataPoints,
                                                crossCorrelatedData, 0,
                                                transmittedDataPoints.length));
    }

    private static float[] findMaxIndex(float[] crossCorrelatedData) {
        return fjPool.invoke(new FindMax(crossCorrelatedData, 0, crossCorrelatedData.length));
    }

    private static double sumTimes(double[] times) {
        double total = 0.0;
        for (double time: times) {
            total += time;
        }
        return total;
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("You need to specify the sent-data file and received-data file, " +
                    "alternatively, use make run-seq or run-parallel.");
            System.exit(0);
        }
        String transmittedDataPath = args[0];
        String receivedDataPath = args[1];

        float[] transmittedDataPoints = getDataPoints(transmittedDataPath);
        float[] receivedDataPoints = getDataPoints(receivedDataPath);
        float[] crossCorrelatedData = new float[transmittedDataPoints.length];

        double[] timeRecord = new double[10];
        for (int i = 0; i < NUM_RUNS; i++) {
            long startTime = System.nanoTime();
            //Timed Functions
            correlateData(transmittedDataPoints, receivedDataPoints, crossCorrelatedData);
            float[] maxIndexValue = findMaxIndex(crossCorrelatedData);
            double totalTimeTaken = (System.nanoTime() - startTime) / NS_TO_MS;

            if (i < 2) {
                System.out.println("\nWarm-up Run #" + (i + 1) + "\n===================");
                System.out.println("Total Time Taken:\t" + totalTimeTaken + "ms");
            } else {
                System.out.println("\nParallel Run #" + (i - 1) + "\n===================");
                System.out.println("Total Time Taken:\t" + totalTimeTaken + "ms");
                System.out.println("Max Value:\t\t" + maxIndexValue[1] +
                        "\nReceived Time:\t\t" + maxIndexValue[0]);
                timeRecord[i - 2] = totalTimeTaken;
            }
        }
        System.out.println("\n\nTime taken to do 10 runs: " + sumTimes(timeRecord) + "ms");
        System.out.println("Average Time taken to do a run: " + (sumTimes(timeRecord) / 10) + "ms");
    }
}
