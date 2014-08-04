/**
 * @author Jethro Muller   MLLJET001
 * @version 0.0.1
 */

import java.util.concurrent.RecursiveTask;

public class FindMax extends RecursiveTask<float[]> {
    private int startIndex;
    private int endIndex;
    private float[] correlatedData;

    public FindMax(float[] correlationArray, int startIndex, int endIndex) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        correlatedData = correlationArray.clone();
    }

    public static float[] compare(float[] floatOne, float[] floatTwo) {
        if (floatOne[1] > floatTwo[1]) {
            return floatOne;
        }
        return floatTwo;
    }

    @Override
    protected float[] compute() {
        if ((endIndex - startIndex) < ParallelCorrelate.SEQUENTIAL_CUTOFF) {
            float max = 0;
            float maxIndex = -1;
            for (int i = startIndex; i < endIndex; i++) {
                if (correlatedData[i] > max) {
                    max = correlatedData[i];
                    maxIndex = i;
                }
            }
            return new float[] {maxIndex, max};
        } else {
            FindMax leftBranch = new FindMax(correlatedData, startIndex, (endIndex + startIndex)/2);
            FindMax rightBranch = new FindMax(correlatedData, (endIndex + startIndex)/2, endIndex);

            leftBranch.fork();
            float[] rightBranchAnswer = rightBranch.compute();
            float[] leftBranchAnswer = leftBranch.join();
            return compare(leftBranchAnswer, rightBranchAnswer);
        }
    }
}
