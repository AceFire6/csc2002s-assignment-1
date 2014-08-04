/**
 * @author Jethro Muller   MLLJET001
 * @version 0.0.1
 */

import java.util.concurrent.RecursiveAction;

public class CorrelateArray extends RecursiveAction {
    private int endIndex;
    private int startIndex;
    private float[] transmittedArray;
    private float[] receivedArray;
    private float[] correlationArray;

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
            for (int i = startIndex; i < endIndex; ++i) {
                float sum = 0;
                for (int j = 0; j < receivedArray.length; ++j) {
                    if (i + j < receivedArray.length) {
                        sum += receivedArray[i + j] * transmittedArray[j];
                    }
                }
                correlationArray[i] = sum;
            }
        } else {
            CorrelateArray leftBranch = new CorrelateArray(transmittedArray, receivedArray,
                                                           correlationArray, startIndex,
                                                           (startIndex + endIndex)/2);
            CorrelateArray rightBranch = new CorrelateArray(transmittedArray, receivedArray,
                                                            correlationArray,
                                                            (startIndex + endIndex)/2, endIndex);
            leftBranch.fork();
            rightBranch.compute();
            leftBranch.join();
        }
    }
}
