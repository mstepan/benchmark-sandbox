package benchmark;

import com.max.algs.util.ArrayUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
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
 * Benchmark                                    Mode  Cnt    Score     Error  Units
 * BinarySearchBenchmark.binarySearch           avgt   10  723.307 ± 234.512  ns/op
 * BinarySearchBenchmark.binarySearchOptimized  avgt   10  849.574 ± 476.433  ns/op
 * BinarySearchBenchmark.binarySearchWithJump   avgt   10  867.921 ± 703.975  ns/op
 */
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(2)
@State(Scope.Thread)
public class BinarySearchBenchmark {

    private static final int ARR_LENGTH = 10_000;
    private static final Random RAND = ThreadLocalRandom.current();

    private int[] arr1;
    private int[] arr2;
    private int[] arr3;
    private int searchElement;

    @Setup(Level.Invocation)
    public void setUp() {
        arr1 = ArrayUtils.generateRandomArray(ARR_LENGTH);
        Arrays.sort(arr1);

        arr2 = Arrays.copyOf(arr1, arr1.length);
        arr3 = Arrays.copyOf(arr1, arr1.length);

        // select any element randomly from array
        searchElement = arr1[RAND.nextInt(arr1.length)];
    }

    @TearDown(Level.Invocation)
    public void tearDown() {
        arr1 = null;
        arr2 = null;
        arr3 = null;
    }

    @Benchmark
    public void binarySearch(Blackhole bh) {
        int index = binarySearch(this.arr1, this.searchElement);
        bh.consume(index);
    }

    @Benchmark
    public void binarySearchOptimized(Blackhole bh) {
        int index = binarySearchOptimized(this.arr2, this.searchElement);
        bh.consume(index);
    }

    @Benchmark
    public void binarySearchWithJump(Blackhole bh) {
        int index = binarySearchWithJump(this.arr3, this.searchElement);
        bh.consume(index);
    }

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

    private static int binarySearchWithJump(int[] arr, int value) {

        int offset = 0;

        for (int jump = arr.length / 2; jump >= 1; jump /= 2) {
            while (offset + jump < arr.length && arr[offset + jump] <= value) {
                offset += jump;
            }
        }

        return arr[offset] == value ? offset : -1;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(BinarySearchBenchmark.class.getSimpleName())
                .threads(Runtime.getRuntime().availableProcessors())
                .build();

        new Runner(opt).run();
    }

}
