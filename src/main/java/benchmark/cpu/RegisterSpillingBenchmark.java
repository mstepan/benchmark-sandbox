package benchmark.cpu;

import com.max.algs.util.ArrayUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Micro benchmark for instruction level parallelism (ILP) for parallel product.
 */
@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(5)
public class RegisterSpillingBenchmark {

    private static final int ARR_LENGTH = 10_000;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(RegisterSpillingBenchmark.class.getSimpleName())
                .threads(Runtime.getRuntime().availableProcessors())
                .build();

        new Runner(opt).run();
    }

    @Benchmark
    @Group("prefixSum0")
    @GroupThreads(1)
    public void prefixSum0(ArrPerThread state) {
        final int[] arr = state.arr0;

        int p = 1;
        for (int i = 0; i < arr.length; ++i) {
            p += arr[i];
        }
    }

    @Benchmark
    @Group("prefixSum00")
    @GroupThreads(1)
    public void prefixSum00(ArrPerThread state) {
        final int[] arr = state.arr00;
        final int k = 4;

        int p = 1;
        int i = 0;
        for (; i < arr.length - k; i += k) {
            p = p + ((arr[i] + arr[i + 1]) + (arr[i + 2] + arr[i + 3]));
        }

        for (; i < arr.length; ++i) {
            p += arr[i];
        }
    }

    @Benchmark
    @Group("prefixSum4")
    @GroupThreads(1)
    public void prefixSum4(ArrPerThread state) {
        final int[] arr = state.arr4;

        final int k = 4;
        int p1 = 1;
        int p2 = 1;
        int p3 = 1;
        int p4 = 1;

        int i = 0;
        for (; i < arr.length - k; i += k) {
            p1 += arr[i];
            p2 += arr[i + 1];
            p3 += arr[i + 2];
            p4 += arr[i + 3];
        }

        int p = p1 + p2 + p3 + p4;

        for (; i < arr.length; ++i) {
            p += arr[i];
        }
    }

    @Benchmark
    @Group("prefixSum10")
    @GroupThreads(1)
    public void prefixSum10(ArrPerThread state) {

        final int[] arr = state.arr10;

        final int k = 10;
        int p1 = 1;
        int p2 = 1;
        int p3 = 1;
        int p4 = 1;
        int p5 = 1;
        int p6 = 1;
        int p7 = 1;
        int p8 = 1;
        int p9 = 1;
        int p10 = 1;

        int i = 0;
        for (; i < arr.length - k; i += k) {
            p1 += arr[i];
            p2 += arr[i + 1];
            p3 += arr[i + 2];
            p4 += arr[i + 3];
            p5 += arr[i + 4];
            p6 += arr[i + 5];
            p7 += arr[i + 6];
            p8 += arr[i + 7];
            p9 += arr[i + 8];
            p10 += arr[i + 9];
        }

        int p = p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9 + p10;

        for (; i < arr.length; ++i) {
            p += arr[i];
        }
    }

    @State(Scope.Thread)
    public static class ArrPerThread {

        int[] arr0;
        int[] arr00;
        int[] arr4;
        int[] arr10;

        @Setup(Level.Invocation)
        public void setUp() {
            arr0 = ArrayUtils.generateRandomArray(ARR_LENGTH);
            arr00 = Arrays.copyOf(arr0, arr0.length);
            arr4 = Arrays.copyOf(arr0, arr0.length);
            arr10 = Arrays.copyOf(arr0, arr0.length);
        }

        @TearDown(Level.Invocation)
        public void tearDown() {
            arr0 = null;
            arr00 = null;
            arr4 = null;
            arr10 = null;
        }
    }

}
