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
 * Micro benchmark for branch prediction simple check.
 */
@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(5)
public class CheapBranchBenchmark {

    private static final int ARR_LENGTH = 10_000;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(CheapBranchBenchmark.class.getSimpleName())
                .threads(Runtime.getRuntime().availableProcessors())
                .build();

        new Runner(opt).run();
    }

    @Benchmark
    @Group("loop")
    @GroupThreads(2)
    public void loop(ArrPerThread state) {
        final int[] arr = state.arr1;

        int p = 0;
        for (int i = 0; i < arr.length; ++i) {
            p += arr[i];
        }
    }

    @Benchmark
    @Group("loopWithBoundaryCheck")
    @GroupThreads(2)
    public void loopWithBoundaryCheck(ArrPerThread state) {
        final int[] arr = state.arr2;
        final int arrLength = arr.length;

        int p = 0;
        for (int i = 0; i < arrLength; ++i) {
            if (i >= 0 && i < arrLength) {
                p += arr[i];
            }
        }
    }

    @State(Scope.Thread)
    public static class ArrPerThread {

        public int[] arr1;
        public int[] arr2;

        @Setup(Level.Invocation)
        public void setUp() {
            arr1 = ArrayUtils.generateRandomArray(ARR_LENGTH, 1_000_000);
            arr2 = Arrays.copyOf(arr1, arr1.length);
        }

        @TearDown(Level.Invocation)
        public void tearDown() {
            arr1 = null;
            arr2 = null;
        }
    }

}
