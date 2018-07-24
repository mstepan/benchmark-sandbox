package benchmark.algorithms;

import com.max.algs.util.ArrayUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

/**
 * Micro benchmark for longest increased sub sequence algorithms
 * (dynamic version and optimized output-sensitive search).
 * <p>
 * Benchmark                                          Mode  Cnt    Score   Error  Units
 * LongestIncreasedSubsequenceBenchmark.lisDynamic    avgt   10  140.958 ± 0.676  ms/op
 * LongestIncreasedSubsequenceBenchmark.lisOptimized  avgt   10    0.374 ± 0.008  ms/op
 */
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(2)
@State(Scope.Thread)
public class LongestIncreasedSubsequenceBenchmark {

    public int[] arr1;
    public int[] arr2;

    @Setup(Level.Invocation)
    public void setUp() {
        arr1 = ArrayUtils.generateRandomArray(10_000, -1000, 1000);
        arr2 = Arrays.copyOf(arr1, arr1.length);
    }

    @TearDown(Level.Invocation)
    public void tearDown() {
        arr1 = null;
        arr2 = null;
    }

    @Benchmark
    public void lisDynamic(Blackhole bh) {
        int res = longestIncSubseqDynamic(arr1);
        bh.consume(res);
    }

    /**
     * N - array length
     * <p>
     * time: O(N^2)
     * space: O(N)
     */
    private static int longestIncSubseqDynamic(int[] arr) {
        checkArgument(arr != null, "null 'arr' parameter passed");

        if (arr.length == 0) {
            return 0;
        }

        int maxSoFar = 1;
        int[] lis = new int[arr.length];
        lis[0] = 1;

        for (int i = 1; i < arr.length; ++i) {
            int curLongest = 1;

            for (int j = i - 1; j >= 0; --j) {
                if (arr[j] < arr[i]) {
                    curLongest = Math.max(curLongest, 1 + lis[j]);
                    maxSoFar = Math.max(maxSoFar, curLongest);
                }
            }

            lis[i] = curLongest;
        }

        checkState(maxSoFar >= 1);

        return maxSoFar;
    }


    @Benchmark
    public void lisOptimized(Blackhole bh) {
        int res = longestIncSubseqOptimized(arr2);
        bh.consume(res);
    }

    /**
     * N - array length
     * K - longest increased sub sequence length (can be up to N)
     * <p>
     * time: O(N*lgK)
     * space: O(K)
     */
    private static int longestIncSubseqOptimized(int[] arr) {
        checkArgument(arr != null, "null 'arr' parameter passed");

        if (arr.length == 0) {
            return 0;
        }

        int maxSoFar = 1;

        final int[] ends = new int[arr.length];
        int to = 0;
        ends[0] = arr[0];

        for (int i = 1; i < arr.length; ++i) {

            int val = arr[i];

            int index = Arrays.binarySearch(ends, 0, to + 1, val);

            // not found, but returns insertion point
            if (index < 0) {
                int pos = (-index) - 1;
                ends[pos] = val;
                maxSoFar = Math.max(maxSoFar, pos + 1);

                to = Math.max(to, pos);
            }

        }

        return maxSoFar;
    }


    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(LongestIncreasedSubsequenceBenchmark.class.getSimpleName())
//                .threads(Runtime.getRuntime().availableProcessors())
                .build();

        new Runner(opt).run();
    }

}
