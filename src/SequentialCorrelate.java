/**
 * @author Jethro Muller   MLLJET001
 * @version 0.0.1
 * Correlates two data sets sequentially and finds the maximum data point and its location in the
 * resulting correlated data set.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class SequentialCorrelate {
    private final static int NS_TO_MS = 1000000;

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
     * Uses the two float[] arrays provided to get the correlation.
     * @param transmittedDataPoints Array of points from transmit.txt.
     * @param receivedDataPoints    Array of points from receive.txt.
     * @return float[] array containing the results of the correlation.
     */
    private static float[] correlateData(float[] transmittedDataPoints, float[] receivedDataPoints) {
        float[] crossCorrelatedData = new float[receivedDataPoints.length];
        for (int i = 0; i < receivedDataPoints.length; ++i) {
            float sum = 0;
            for (int j = 0; j < transmittedDataPoints.length; ++j) {
                if (i + j < receivedDataPoints.length) {
                    sum += receivedDataPoints[i + j] * transmittedDataPoints[j];
                } else {
                    break;
                }
            }
            crossCorrelatedData[i] = sum;
        }
        return crossCorrelatedData;
    }

    /**
     * Finds the maximum value and its index in the array.
     * @param crossCorrelatedData    The values of the cross correlation.
     * @return  float[] containing the maximum value's index and the maximum value.
     */
    private static float[] findMaxIndex(float[] crossCorrelatedData) {
        float max = 0;
        float maxIndex = 0;
        for (int i = 0; i < crossCorrelatedData.length; i++) {
            if (crossCorrelatedData[i] > max) {
                max = crossCorrelatedData[i];
                maxIndex = i;
            }
        }
        return new float[] {maxIndex, max};
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
            System.out.println("You need to specify the sent-data file and received-data file, or use make run-seq" +
                               " or run-parallel.");
            System.exit(0);
        }
        String transmittedDataPath = args[0];
        String receivedDataPath = args[1];

        //Make all the variable beforehand.
        float[] transmittedDataPoints = getDataPoints(transmittedDataPath);
        float[] receivedDataPoints = getDataPoints(receivedDataPath);
        float[] crossCorrelatedData;
        float[] maxIndexValue;
        long startTime;
        double totalTimeTaken;
        double[] timeRecord = new double[10];
        FileWriter fileO = null;
        try {
            fileO = new FileWriter("outputSeq.out");
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < 12; i++) {
            startTime = System.nanoTime();
            //Timed Functions
            crossCorrelatedData = correlateData(transmittedDataPoints, receivedDataPoints);
            maxIndexValue = findMaxIndex(crossCorrelatedData);
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
                System.out.println("\nSequential Run #" + (i - 1) + "\n===================");
                System.out.println("Total Time Taken:\t" + totalTimeTaken + "ms");
                System.out.println("Max Value:\t\t" + maxIndexValue[1] +
                                   "\nReceived Time:\t\t" + maxIndexValue[0]);
                timeRecord[i - 2] = totalTimeTaken;
                try {
                    if (fileO != null) {
                        fileO.append("\n\nSequential Run #").append(Integer.toString(i - 1)).append(
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
        System.out.println("\nAverage Time taken to do a run: " + (sum / 10) + "ms");
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
