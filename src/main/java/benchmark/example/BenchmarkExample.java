package benchmark.example;

import com.max.algs.util.ArrayUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * Micro benchmark example.
 */
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(2)
public class BenchmarkExample {

    @Benchmark
    @Group("copyIntrinsics")
    @GroupThreads(4)
    public void copyIntrinsics(ArrPerThread state) {
        final int[] arr = state.arr1;
        final int[] copy = state.copy1;
        System.arraycopy(arr, 0, copy, 0, arr.length);
    }

    @Benchmark
    @Group("loopCopy")
    @GroupThreads(4)
    public void loopCopy(ArrPerThread state) {

        final int[] arr = state.arr1;
        final int[] copy = state.copy1;

        for (int i = 0; i < arr.length; ++i) {
            copy[i] = arr[i];
        }
    }

    @State(Scope.Thread)
    public static class ArrPerThread {

        public int[] arr1;
        public int[] copy1;

        @Setup(Level.Invocation)
        public void setUp() {
            arr1 = ArrayUtils.generateRandomArray(1_000_000);
            copy1 = new int[arr1.length];
        }

        @TearDown(Level.Invocation)
        public void tearDown() {
            arr1 = null;
            copy1 = null;
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(BenchmarkExample.class.getSimpleName())
                .threads(Runtime.getRuntime().availableProcessors())
                .build();

        new Runner(opt).run();
    }

}
