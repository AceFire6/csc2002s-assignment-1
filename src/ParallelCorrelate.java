/**
 * @author Jethro Muller   MLLJET001
 * @version 0.0.1
 * Correlates two data sets in parallel and finds the maximum data point and its location in the
 * resulting correlated data set.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;

public class ParallelCorrelate {
    /**
     * The number you divide nanoseconds by to get milliseconds.
     */
    private final static int NS_TO_MS = 1000000;
    private static ForkJoinPool fjPool = new ForkJoinPool();
    /**
     * The sequential cutoff for the RecursiveTask and RecursiveAction.
     */
    public static final int SEQUENTIAL_CUTOFF = 750;
    /**
     * The number of runs to do. The first two will always be warm-ups.
     */
    private static final int NUM_RUNS = 12;

    /**
     * Gets the data from the file specified.
     * @param filePath Path to the file to be read in from.
     * @return float[] containing the data points read in from the file.
     */
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

    /**
     * Uses the two float[] arrays provided to get the correlation in parallel.
     * Starts a RecursiveAction that keeps making more threads until the sequential
     * threshhold is reached.
     * @param transmittedDataPoints The first data set. Contains the transmitted signal data.
     * @param receivedDataPoints    The second data set. Contains the received signal data.
     * @param crossCorrelatedData   The array that will store the results. The pointer is shared
     *                              to remove the need to return anything.
     */
    private static void correlateData(float[] transmittedDataPoints,
                                         float[] receivedDataPoints, float[] crossCorrelatedData) {
        fjPool.invoke(new CorrelateArray(transmittedDataPoints, receivedDataPoints,
                                                crossCorrelatedData, 0,
                                                transmittedDataPoints.length));
    }

    /**
     * Finds the maximum value and its index in the array in parallel.
     * Starts a RecursiveTask that keeps making more threads until the
     * sequential threshhold is reached.
     * @param crossCorrelatedData    The values of the cross correlation.
     * @return  float[] containing the maximum value's index and the maximum value.
     */
    private static float[] findMaxIndex(float[] crossCorrelatedData) {
        return fjPool.invoke(new FindMax(crossCorrelatedData, 0, crossCorrelatedData.length));
    }

    /**
     * Adds up the times stored in the time array.
     * @param times double[] array of the running times.
     * @return the sum of the given times.
     */
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
        float[] maxIndexValue;
        long startTime;
        double totalTimeTaken;
        double[] timeRecord = new double[10];
        FileWriter fileO = null;
        try {
            fileO = new FileWriter("outputPar.out");
            fileO.append("");
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < NUM_RUNS; i++) {
            startTime = System.nanoTime();
            //Timed Functions
            correlateData(transmittedDataPoints, receivedDataPoints, crossCorrelatedData);
            maxIndexValue= findMaxIndex(crossCorrelatedData);
            totalTimeTaken = (System.nanoTime() - startTime) / NS_TO_MS;

            if (i < 2) {
                System.out.println("\nWarm-up Run #" + (i + 1) + "\n===================");
                System.out.println("Total Time Taken:\t" + totalTimeTaken + "ms");

                try {
                    if (fileO != null) {
                        fileO.append("\n\nWarm-up Run #").append(Integer.toString(i + 1)).append(
                                "\n===================");
                        fileO.append("\nTotal Time Taken:\t").append(Double.toString
                                (totalTimeTaken)).append("ms");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("\nParallel Run #" + (i - 1) + "\n===================");
                System.out.println("Total Time Taken:\t" + totalTimeTaken + "ms");
                System.out.println("Max Value:\t\t" + maxIndexValue[1] +
                        "\nReceived Time:\t\t" + maxIndexValue[0]);
                timeRecord[i - 2] = totalTimeTaken;
                try {
                    if (fileO != null) {
                        fileO.append("\n\nParallel Run #").append(Integer.toString(i - 1)).append(
                                "\n===================");
                        fileO.append("\nTotal Time Taken:\t").append(Double.toString
                                (totalTimeTaken)).append("ms");
                        fileO.append("\nMax Value:\t\t").append(Float.toString(maxIndexValue[1]))
                                .append
                                        ("\nReceived Time:\t\t").append(Float.toString(maxIndexValue[0]));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        double sum = sumTimes(timeRecord);
        System.out.println("\n\nTime taken to do 10 runs: " + sumTimes(timeRecord) + "ms");
        System.out.println("Average Time taken to do a run: " + (sum / 10) + "ms");
        try {
            if (fileO != null) {
                fileO.append("\n\nTime taken to do 10 runs: ").append(Double.toString(sum)).append("ms");
                fileO.append("\nAverage Time taken to do a run: ").append(
                        Double.toString(sum / 10)).append("ms\n");
                fileO.close();
            }
        } catch (Exception ioe) {
            ioe.printStackTrace();
        }
    }
}
