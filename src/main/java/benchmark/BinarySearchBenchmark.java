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
public class BinarySearchBenchmark {

    private static final int ARR_LENGTH = 10_000;

    private static final Random RAND = ThreadLocalRandom.current();

    private static int binarySearch(int[] arr, int value) {
        checkArgument(arr != null, "null 'arr' argument passed");

        int lo = 0;
        int hi = arr.length - 1;
        int mid;

        while (lo <= hi) {

            mid = (lo + hi) >>> 1;

            if (arr[mid] == value) {
                return mid;
            }

            if (arr[mid] > value) {
                hi = mid - 1;
            }
            // arr[mid] <= lo
            else {
                lo = mid + 1;
            }
        }

        return -1;
    }

    private static int binarySearchOptimized(int[] arr, int value) {
        checkArgument(arr != null, "null 'arr' argument passed");

        int lo = 0;
        int hi = arr.length - 1;
        int mid;

        while (lo != hi) {

            // mid = ceil( (lo + hi)/ 2 )
            mid = ((lo + hi) >>> 1) + ((lo + hi) & 1);

            if (arr[mid] > value) {
                hi = mid - 1;
            }
            // arr[mid] <= lo
            else {
                lo = mid;
            }
        }

        if (arr[lo] == value) {
            return lo;
        }

        return -1;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(BinarySearchBenchmark.class.getSimpleName())
                .threads(Runtime.getRuntime().availableProcessors())
                .build();

        new Runner(opt).run();
    }

    @Benchmark
    @Group("binarySearch")
    @GroupThreads(2)
    public void binarySearch(ArrPerThread state) {
        binarySearch(state.arr1, state.searchElement);
    }

    @Benchmark
    @Group("binarySearchOptimized")
    @GroupThreads(2)
    public void binarySearchOptimized(ArrPerThread state) {
        binarySearchOptimized(state.arr2, state.searchElement);
    }

    @State(Scope.Thread)
    public static class ArrPerThread {

        private int[] arr1;
        private int[] arr2;
        private int searchElement;

        @Setup(Level.Invocation)
        public void setUp() {
            arr1 = ArrayUtils.generateRandomArray(ARR_LENGTH);
            Arrays.sort(arr1);

            arr2 = Arrays.copyOf(arr1, arr1.length);

            // select any element randomly from index [0; 9_999]
            searchElement = arr1[RAND.nextInt(ARR_LENGTH)];
        }

        @TearDown(Level.Invocation)
        public void tearDown() {
            arr1 = null;
            arr2 = null;
        }
    }

}
