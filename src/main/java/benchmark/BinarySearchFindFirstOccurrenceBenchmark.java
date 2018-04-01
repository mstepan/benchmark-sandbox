package benchmark;

import com.max.algs.util.ArrayUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Micro benchmark for binary search.
 */
@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(5)
public class BinarySearchFindFirstOccurrenceBenchmark {

    private static final int ARR_LENGTH = 10_000;

    private static final Random RAND = ThreadLocalRandom.current();

    private static int findFirstRec(int[] arr, int lo, int hi, int value) {

        if (lo > hi) {
            return -1;
        }
        int mid = (lo + hi) >>> 1;

        if (arr[mid] == value) {
            int leftIndex = findFirstRec(arr, lo, mid - 1, value);
            return leftIndex != -1 ? leftIndex : mid;
        }

        if (arr[mid] > value) {
            return findFirstRec(arr, lo, mid - 1, value);
        }

        return findFirstRec(arr, mid + 1, hi, value);

    }

    private static int findFirst(int[] arr, int value) {
        checkArgument(arr != null, "null 'arr' argument passed");

        int lo = 0;
        int hi = arr.length - 1;
        int index = -1;

        int mid;
        while (lo <= hi) {

            mid = (lo + hi) >>> 1;

            if (arr[mid] == value) {
                index = mid;
                hi = mid - 1;
            }
            else if (arr[mid] > value) {
                hi = mid - 1;
            }
            else {
                lo = mid + 1;
            }
        }

        return index;
    }

    private static int findFirstCmove(int[] arr, int value) {
        checkArgument(arr != null, "null 'arr' argument passed");

        int lo = 0;
        int hi = arr.length - 1;
        int index = -1;
        int mid;

        while (lo <= hi) {

            mid = (lo + hi) >>> 1;

            // use conditional move instead of typical conditional jumps
            index = (arr[mid] == value) ? mid : index;
            hi = (arr[mid] >= value) ? mid - 1 : hi;
            lo = (arr[mid] < value) ? mid + 1 : lo;
        }

        return index;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(BinarySearchFindFirstOccurrenceBenchmark.class.getSimpleName())
                .threads(Runtime.getRuntime().availableProcessors())
                .build();

        new Runner(opt).run();
    }

    @Benchmark
    @Group("findFirstRec")
    @GroupThreads(2)
    public void findFirstRec(ArrPerThread state) {
        findFirstRec(state.arr1, 0, state.arr1.length - 1, state.searchElement);
    }

    @Benchmark
    @Group("findFirst")
    @GroupThreads(2)
    public void findFirst(ArrPerThread state) {
        findFirst(state.arr1, state.searchElement);
    }

    @Benchmark
    @Group("findFirstCmove")
    @GroupThreads(2)
    public void findFirstCmove(ArrPerThread state) {
        findFirstCmove(state.arr1, state.searchElement);
    }

    @State(Scope.Thread)
    public static class ArrPerThread {

        private int[] arr1;
        private int searchElement;

        @Setup(Level.Invocation)
        public void setUp() {
            arr1 = ArrayUtils.generateRandomArray(ARR_LENGTH);
            Arrays.sort(arr1);
            searchElement = arr1[RAND.nextInt(1000)];
        }

        @TearDown(Level.Invocation)
        public void tearDown() {
            arr1 = null;
        }
    }

}
