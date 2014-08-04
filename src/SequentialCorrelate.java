/**
 * @author Jethro Muller   MLLJET001
 * @version 0.0.1
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class SequentialCorrelate {
    private final static int NS_TO_MS = 1000000;

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

    private static float[] correlateData(float[] transmittedDataPoints, float[] receivedDataPoints) {
        float[] crossCorrelatedData = new float[receivedDataPoints.length];
        for (int i = 0; i < receivedDataPoints.length; ++i) {
            float sum = 0;
            for (int j = 0; j < transmittedDataPoints.length; ++j) {
                if (i + j < receivedDataPoints.length) {
                    sum += receivedDataPoints[i + j] * transmittedDataPoints[j];
                }
            }
            crossCorrelatedData[i] = sum;
        }
        return crossCorrelatedData;
    }

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

        float[] transmittedDataPoints = getDataPoints(transmittedDataPath);
        float[] receivedDataPoints = getDataPoints(receivedDataPath);

        double[] timeRecord = new double[10];
        for (int i = 0; i < 12; i++) {
            long startTime = System.nanoTime();
            //Timed Functions
            float[] crossCorrelatedData = correlateData(transmittedDataPoints, receivedDataPoints);
            float[] maxIndexValue = findMaxIndex(crossCorrelatedData);
            double totalTimeTaken = (System.nanoTime() - startTime) / NS_TO_MS;

            if (i < 2) {
                System.out.println("\nWarm-up Run #" + (i + 1) + "\n===================");
                System.out.println("Total Time Taken:\t" + totalTimeTaken + "ms");
            } else {
                float receivedTime = maxIndexValue[0];
                System.out.println("\nSequential Run #" + (i - 1) + "\n===================");
                System.out.println("Total Time Taken:\t" + totalTimeTaken + "ms");
                System.out.println("Max Value:\t\t" + maxIndexValue[1] +
                        "\nReceived Time:\t\t" + receivedTime);
                timeRecord[i - 2] = totalTimeTaken;
            }
        }
        System.out.println("\n\nTime taken to do 10 runs: " + sumTimes(timeRecord) + "ms");
        System.out.println("Average Time taken to do a run: " + (sumTimes(timeRecord) / 10) + "ms");
    }
}
