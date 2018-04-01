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
 * Memory store performance benchmark, pipelined vs simple.
 */
@State(Scope.Group)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(5)
public class ZeroArrayBenchmark {

    private static final int ARR_LENGTH = 10_000;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(ZeroArrayBenchmark.class.getSimpleName())
                .threads(Runtime.getRuntime().availableProcessors())
                .build();

        new Runner(opt).run();
    }

    @Benchmark
    @Group("simple")
    @GroupThreads(1)
    public void simple(ArrPerThread state) {
        final int[] arr = state.arr1;
        final int arrLength = arr.length;

        for (int i = 0; i < arrLength; ++i) {
            arr[i] = 0;
        }
    }

    @Benchmark
    @Group("pipelined")
    @GroupThreads(1)
    public void pipelined(ArrPerThread state) {
        final int[] arr = state.arr2;
        final int arrLength = arr.length;
        final int k = 4;

        int i = 0;
        for (; i < arrLength - k; i += k) {
            // use pipelined store capability of processor
            arr[i] = 0;
            arr[i + 1] = 0;
            arr[i + 2] = 0;
            arr[i + 3] = 0;
        }

        for (; i < arrLength; ++i) {
            arr[i] = 0;
        }
    }

    @State(Scope.Thread)
    public static class ArrPerThread {

        public int[] arr1;
        public int[] arr2;

        @Setup(Level.Invocation)
        public void setUp() {
            arr1 = ArrayUtils.generateRandomArray(ARR_LENGTH);
            arr2 = Arrays.copyOf(arr1, arr1.length);
        }
    }

}
