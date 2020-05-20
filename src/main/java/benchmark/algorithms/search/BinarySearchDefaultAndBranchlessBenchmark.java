package benchmark.algorithms.search;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * Benchmark for ordinary binary search algorithm and branch-less for array of size 32.
 *
 * Benchmark                                                         Mode  Cnt    Score    Error  Units
 * BinarySearchDefaultAndBranchlessBenchmark.binarySearchBranchLess  avgt   10  403.170 ± 11.758  ns/op
 * BinarySearchDefaultAndBranchlessBenchmark.binarySearchDefault     avgt   10  506.641 ±  5.261  ns/op
 *
 * Summary branch-less binary search on average 20% faster than the ordinary binary search.
 */
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(2)
@State(Scope.Thread)
public class BinarySearchDefaultAndBranchlessBenchmark {

    // this value is fixed for branch-less binary search and should be power of 2
    private static final int ARR_LENGTH = 32;

    private int[] arr1;
    private int[] arr2;

    @Setup(Level.Invocation)
    public void setUp() {
        arr1 = generateRandomArray(ARR_LENGTH);
        arr2 = Arrays.copyOf(arr1, arr1.length);

        // both arrays should be sorted for binary search to work properly
        Arrays.sort(arr1);
        Arrays.sort(arr2);
    }

    @TearDown(Level.Invocation)
    public void tearDown() {
        arr1 = null;
        arr2 = null;
    }

    @Benchmark
    public void binarySearchDefault(Blackhole bh) {
        for (int i = 0; i < arr1.length; ++i) {
            bh.consume(binarySearch(arr1, i));
        }
    }

    /**
     * Ordinary binary search.
     */
    private static int binarySearch(int[] arr, int value) {

        int from = 0;
        int to = arr.length - 1;

        while (from <= to) {
            int mid = from + (to - from) / 2;

            if (arr[mid] == value) {
                return mid;
            }

            if (arr[mid] < value) {
                from = mid + 1;
            }
            else {
                to = mid - 1;
            }
        }

        return -from;
    }

    @Benchmark
    public void binarySearchBranchLess(Blackhole bh) {
        for (int i = 0; i < arr2.length; ++i) {
            bh.consume(binarySearchBranchless(arr2, i));
        }
    }

    /**
     * Branch-less binary search for array of length = 32.
     * for length = 32, we need to make 5 comparisons using offsets 16, 8, 4, 2, 1
     * <p>
     * If element is not found in the array, returns possible insertion position as a negative index.
     */
    private static int binarySearchBranchless(int[] arr, int value) {
        assert arr != null : "null 'arr' parameter detected";
        assert arr.length == 32 : "Incorrect array length detected, expected 32, but found: " + arr.length;

        int base = 0;

        base += (arr[base + 16] <= value) ? 16 : 0;
        base += (arr[base + 8] <= value) ? 8 : 0;
        base += arr[base + 4] <= value ? 4 : 0;
        base += arr[base + 2] <= value ? 2 : 0;
        base += arr[base + 1] <= value ? 1 : 0;

        return arr[base] == value ? base : -(base + 1);
    }

    private static final Random RAND = new Random();

    private static int[] generateRandomArray(int length) {
        assert length >= 0 : "negative arr length";
        return IntStream.range(0, length).
                map(value -> RAND.nextInt()).
                toArray();
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(BinarySearchDefaultAndBranchlessBenchmark.class.getSimpleName())
//                .threads(Runtime.getRuntime().availableProcessors())
                .build();

        new Runner(opt).run();
    }

}
