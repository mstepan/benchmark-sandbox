package benchmark.sort;

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
 * Benchmark for ordinary insertion sort and fast insertion sort that use min heap before sorting.
 * <p>
 * For more info, check this video https://www.youtube.com/watch?v=FJJTYQYB1JQ
 *
 * Benchmark                                                Mode  Cnt       Score     Error  Units
 * InsertionVsFastInsertionSortBenchmark.fastInsertionSort  avgt   10   29953.124 ± 582.978  ns/op
 * InsertionVsFastInsertionSortBenchmark.insertionSort      avgt   10   48102.230 ± 313.428  ns/op
 * InsertionVsFastInsertionSortBenchmark.jdkInsertionSort   avgt   10  103639.169 ± 723.746  ns/op
 */
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(2)
@State(Scope.Thread)
public class InsertionVsFastInsertionSortBenchmark {

    private static final Random RAND = new Random();

    public int[] arr1;
    public int[] arr2;
    public int[] arr3;

    @Setup(Level.Invocation)
    public void setUp() {
        arr1 = generateRandomArray(100 + RAND.nextInt(1_000));
        arr2 = Arrays.copyOf(arr1, arr1.length);
        arr3 = Arrays.copyOf(arr1, arr1.length);
    }

    private static int[] generateRandomArray(int length) {
        assert length >= 0 : "negative arr length";
        return IntStream.range(0, length).
                map(notUsed -> RAND.nextInt()).
                toArray();
    }

    @TearDown(Level.Invocation)
    public void tearDown() {
        arr1 = null;
        arr2 = null;
        arr3 = null;
    }

    @Benchmark
    public void insertionSort(Blackhole bh) {
        insertionSort(arr1);
        bh.consume(arr1);
    }

    @Benchmark
    public void fastInsertionSort(Blackhole bh) {
        fastInsertionSort(arr2);
        bh.consume(arr2);
    }

    @Benchmark
    public void jdkInsertionSort(Blackhole bh) {
        jdkInsertionSort(arr3, 0, arr3.length - 1);
        bh.consume(arr3);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(InsertionVsFastInsertionSortBenchmark.class.getSimpleName())
//                .threads(Runtime.getRuntime().availableProcessors())
                .build();

        new Runner(opt).run();
    }

    /**
     * Copied from jdk, java.util.DualPivotQuicksort class.
     */
    public static void jdkInsertionSort(int[] a, int left, int right) {
        for (int i = left, j = i; i < right; j = ++i) {
            int ai = a[i + 1];
            while (ai < a[j]) {
                a[j + 1] = a[j];
                if (j-- == left) {
                    break;
                }
            }
            a[j + 1] = ai;
        }
    }

    /**
     * Typical insertion sort algorithm with linear scan.
     * <p>
     * time: O(N^2)
     * space: O(1)
     */
    public static void insertionSort(int[] arr) {
        assert arr != null : "null 'arr' detected";

        if (arr.length < 2) {
            return;
        }

        int temp;
        for (int i = 1; i < arr.length; ++i) {

            temp = arr[i];

            int j = i - 1;

            while (j >= 0 && arr[j] > temp) {
                arr[j + 1] = arr[j];
                --j;
            }

            arr[j + 1] = temp;
        }
    }

    /**
     * Modified insertion sort that use min heap before doing un-guarded insertion sort.
     * Time should be the same from big O notation point of view, but the performance should be better.
     * <p>
     * time: O(N^2)
     * space: O(1)
     */
    public static void fastInsertionSort(int[] arr) {
        assert arr != null : "null 'arr' detected";

        if (arr.length < 2) {
            return;
        }

        if ((arr.length & 1) == 0) {
            heapify(arr, arr.length - 1);
            fixUp(arr, arr.length - 1);
        }
        else {
            heapify(arr, arr.length);
        }

        unguardedInsertionSort(arr);
    }

    private static void unguardedInsertionSort(int[] arr) {
        assert arr != null : "null 'arr' detected";

        int temp;
        for (int i = 2; i < arr.length; ++i) {

            temp = arr[i];

            int j = i - 1;

            while (arr[j] > temp) {
                arr[j + 1] = arr[j];
                --j;
            }

            arr[j + 1] = temp;
        }
    }

    private static void heapify(int[] arr, int length) {
        assert arr != null : "null 'arr' detected";

        for (int parent = length / 2 - 1; parent >= 0; --parent) {
            fixDown(arr, length, parent);
        }
    }

    private static void fixDown(int[] arr, int length, int parent) {
        assert arr != null;
        assert parent >= 0 && parent < length : String.format("arr.length: %d, parent: %d", length, parent);

        final int left = 2 * parent + 1;
        int minIndex = parent;

        // IMPORTANT: min heap here always has even number of children (0 or 2).
        if (left < length) {

            // check left child
            if (arr[left] < arr[minIndex]) {
                minIndex = left;
            }
            //minIndex = arr[left] < arr[minIndex] ? left : minIndex;

            final int right = 2 * parent + 2;

            // check right child
            if (arr[right] < arr[minIndex]) {
                minIndex = right;
            }
            //minIndex = arr[right] < arr[minIndex] ? right : minIndex;
        }

        if (minIndex != parent) {
            swap(arr, parent, minIndex);
            fixDown(arr, length, minIndex);
        }
    }

    private static void fixUp(int[] arr, int index) {
        int curIndex = index;

        while (curIndex != 0) {
            int parent = curIndex / 2;

            if (arr[parent] <= arr[curIndex]) {
                break;
            }

            swap(arr, curIndex, parent);
            curIndex = parent;
        }
    }

    private static void swap(int[] arr, int from, int to) {
        assert arr != null : "null 'arr' passed";
        assert from >= 0 && from < arr.length : "'from' out of bound";
        assert to >= 0 && to < arr.length : "'to' out of bound";

        int temp = arr[from];
        arr[from] = arr[to];
        arr[to] = temp;
    }

}
