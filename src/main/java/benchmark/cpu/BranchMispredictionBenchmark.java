package benchmark.cpu;

import com.max.algs.util.ArrayUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

/**
 * Micro benchmark for branch mis-prediction.
 */
@State(Scope.Group)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(5)
public class BranchMispredictionBenchmark {

    private static final int ARR_LENGTH = 1 << 20; // 1_048_576

    private static void mergeSort(int[] arr, MergeMethod mergeType) {

        Queue<ArrChunk> queue = new ArrayDeque<>();

        for (int to = 1; to < arr.length; to += 2) {

            int from = to - 1;

            if (arr[from] > arr[to]) {
                int temp = arr[from];
                arr[from] = arr[to];
                arr[to] = temp;
            }

            queue.add(new ArrChunk(from, to));
        }

        while (queue.size() != 1) {
            ArrChunk leftChunk = queue.poll();
            ArrChunk rightChunk = queue.poll();

            int from = leftChunk.from;
            int mid = leftChunk.to;

            int to = rightChunk.to;

            int[] left = Arrays.copyOfRange(arr, from, mid + 1);
            int[] right = Arrays.copyOfRange(arr, mid + 1, to + 1);

            switch (mergeType) {
                case STANDARD:
                    merge(arr, from, left, right);
                    break;
                case CMOV:
                    mergeWithoutBranchMiss1(arr, from, left, right);
                    break;
                case OPTIMIZED2:
                    mergeWithoutBranchMiss2(arr, from, left, right);
                    break;
                case OPTIMIZED3:
                    mergeWithoutBranchMiss3(arr, from, left, right);
                    break;
            }

            queue.add(new ArrChunk(from, to));
        }
    }

    private static void merge(int[] arr, int from, int[] left, int[] right) {

        int index = from;
        int leftIndex = 0;
        int rightIndex = 0;

        while (leftIndex < left.length && rightIndex < right.length) {
            if (left[leftIndex] <= right[rightIndex]) {
                arr[index++] = left[leftIndex++];
            }
            else {
                arr[index++] = right[rightIndex++];
            }
        }

        while (leftIndex < left.length) {
            arr[index++] = left[leftIndex++];
        }
    }

    private static void mergeWithoutBranchMiss1(int[] arr, int from, int[] left, int[] right) {

        int index = from;
        int leftIndex = 0;
        int rightIndex = 0;

        while (leftIndex < left.length && rightIndex < right.length) {
            arr[index++] = (left[leftIndex] <= right[rightIndex]) ? left[leftIndex++] : right[rightIndex++];
        }

        while (leftIndex < left.length) {
            arr[index++] = left[leftIndex++];
        }
    }

    private static void mergeWithoutBranchMiss2(int[] arr, int from, int[] left, int[] right) {

        int index = from;
        int leftIndex = 0;
        int rightIndex = 0;

        int x, y, sign;

        while (leftIndex < left.length && rightIndex < right.length) {
            x = left[leftIndex];
            y = right[rightIndex];

            sign = (x - y) >> 31;

            arr[index++] = (x & sign) | (y & (~sign));

            leftIndex += (sign & 1);
            rightIndex += (~sign) & 1;
        }

        while (leftIndex < left.length) {
            arr[index++] = left[leftIndex++];
        }
    }

    private static void mergeWithoutBranchMiss3(int[] arr, int from, int[] left, int[] right) {

        int index = from;
        int leftIndex = 0;
        int rightIndex = 0;

        int x, y, sign, signFlipped;

        while (leftIndex < left.length && rightIndex < right.length) {
            x = left[leftIndex];
            y = right[rightIndex];

            sign = (x - y) >>> 31;
            signFlipped = sign ^ 1;

            arr[index++] = (x * sign) | (y * signFlipped);

            leftIndex += sign;
            rightIndex += signFlipped;
        }

        if (leftIndex < left.length) {
            System.arraycopy(left, leftIndex, arr, index, left.length - leftIndex);
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(BranchMispredictionBenchmark.class.getSimpleName())
                .threads(Runtime.getRuntime().availableProcessors())
                .build();

        new Runner(opt).run();
    }

    @Benchmark
    @Group("branchMissed")
    @GroupThreads(4)
    public void branchMissed(ArrPerThread state) {
        mergeSort(state.arr1, MergeMethod.STANDARD);
    }

    /**
     * On x86 will use 'cmovne' instruction.
     */
    @Benchmark
    @Group("branchConditionalMove")
    @GroupThreads(4)
    public void branchConditionalMove(ArrPerThread state) {
        mergeSort(state.arr2, MergeMethod.CMOV);
    }

    @Benchmark
    @Group("branchPredictedOptimized2")
    @GroupThreads(4)
    public void branchPredictedOptimized2(ArrPerThread state) {
        mergeSort(state.arr3, MergeMethod.OPTIMIZED2);
    }

    @Benchmark
    @Group("branchPredictedOptimized3")
    @GroupThreads(4)
    public void branchPredictedOptimized3(ArrPerThread state) {
        mergeSort(state.arr4, MergeMethod.OPTIMIZED3);
    }

    private enum MergeMethod {
        STANDARD,
        CMOV,
        OPTIMIZED2,
        OPTIMIZED3
    }

    @State(Scope.Thread)
    public static class ArrPerThread {

        public int[] arr1;
        public int[] arr2;
        public int[] arr3;
        public int[] arr4;

        @Setup(Level.Invocation)
        public void setUp() {
            arr1 = ArrayUtils.generateRandomArray(ARR_LENGTH);
            arr2 = Arrays.copyOf(arr1, arr1.length);
            arr3 = Arrays.copyOf(arr1, arr1.length);
            arr4 = Arrays.copyOf(arr1, arr1.length);
        }

        @TearDown(Level.Invocation)
        public void tearDown() {
            arr1 = null;
            arr2 = null;
            arr3 = null;
            arr4 = null;
        }
    }

    private static class ArrChunk {
        final int from;
        final int to;

        public ArrChunk(int from, int to) {
            this.from = from;
            this.to = to;
        }
    }

}
