/**
 * @author Jethro Muller   MLLJET001
 * @version 0.0.1
 * Does the actual calculating of the correlation of the two data sets. New threads are made
 * until the sequential cutoff is reached after which it does the sequential algorithm.
 */

import java.util.concurrent.RecursiveAction;

/**
 * CorrelateArray extends RecursiveAction because it doesn't need to return anything.
 * It's used to calculate the correlation of the two given data sets. They results are stored in
 * correlationArray. correlationArray's mutability is exploited to remove the need for joining or
 * returning arrays.
 */
public class CorrelateArray extends RecursiveAction {
    /**
     * The end of the indices that have to be covered by a thread.
     */
    private int endIndex;
    /**
     * The start of the indices that have to be covered by a thread.
     */
    private int startIndex;
    /**
     * Transmitted data set.
     */
    private float[] transmittedArray;
    /**
     * Received data set.
     */
    private float[] receivedArray;
    /**
     * The data set that will hold the answers.
     */
    private float[] correlationArray;

    /**
     * Parameterized constructor. Initializes all the necessary variables.
     * @param transmittedDataPoints    Transmitted data set.
     * @param receivedDataPoints       Received data set.
     * @param crossCorrelatedData      The data set that will hold the answers.
     * @param startIndex               The start of the indices that have to be covered by a thread.
     * @param endIndex                 The end of the indices that have to be covered by a thread.
     */
    public CorrelateArray(float[] transmittedDataPoints, float[] receivedDataPoints,
                          float[] crossCorrelatedData, int startIndex, int endIndex) {
        this.endIndex = endIndex;
        this.startIndex = startIndex;
        transmittedArray = transmittedDataPoints;
        receivedArray = receivedDataPoints;
        correlationArray = crossCorrelatedData;
    }


    @Override
    protected void compute() {
        if ((endIndex - startIndex) < ParallelCorrelate.SEQUENTIAL_CUTOFF) {
            //If the sequential cutoff is met, switch to the sequential task.
            for (int i = startIndex; i < endIndex; ++i) {
                float sum = 0;
                for (int j = 0; j < receivedArray.length; ++j) {
                    if (i + j < receivedArray.length) {
                        sum += receivedArray[i + j] * transmittedArray[j];
                    } else {
                        break;
                    }
                }
                correlationArray[i] = sum;
            }
        } else {
            // If the sequential cutoff isn't met, make more threads.
            CorrelateArray leftBranch = new CorrelateArray(transmittedArray, receivedArray,
                                                           correlationArray, startIndex,
                                                           (startIndex + endIndex)/2);
            CorrelateArray rightBranch = new CorrelateArray(transmittedArray, receivedArray,
                                                            correlationArray,
                                                            (startIndex + endIndex)/2, endIndex);
            //Make a new thread.
            leftBranch.fork();
            //Do the work of a new thread in this thread to save time
            rightBranch.compute();
            //Wait for the left branch to finish running before continuing.
            leftBranch.join();
        }
    }
}
