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
public class WriteReadMemoryDependency1Benchmark {

    private static final int ARR_LENGTH = 1_000_000;

    private static void copyArray(int[] arr, int src, int dest) {
        for (; src < arr.length && dest < arr.length; ++src, ++dest) {
            arr[dest] = arr[src];
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(WriteReadMemoryDependency1Benchmark.class.getSimpleName())
                .threads(Runtime.getRuntime().availableProcessors())
                .build();

        new Runner(opt).run();
    }

    @Benchmark
    @Group("noDependency")
    @GroupThreads(2)
    public void noDependency(ArrPerThread state) {
        copyArray(state.arr1, 1, 0);
    }

    @Benchmark
    @Group("writeReadDependency")
    @GroupThreads(2)
    public void writeReadDependency(ArrPerThread state) {
        copyArray(state.arr2, 0, 1);
    }

    @Benchmark
    @Group("sameLocation")
    @GroupThreads(2)
    public void sameLocation(ArrPerThread state) {
        copyArray(state.arr3, 0, 0);
    }

    @State(Scope.Thread)
    public static class ArrPerThread {

        public int[] arr1;
        public int[] arr2;
        public int[] arr3;

        @Setup(Level.Invocation)
        public void setUp() {
            arr1 = ArrayUtils.generateRandomArray(ARR_LENGTH);
            arr2 = Arrays.copyOf(arr1, arr1.length);
            arr3 = Arrays.copyOf(arr1, arr1.length);
        }

        @TearDown(Level.Invocation)
        public void tearDown() {
            arr1 = null;
            arr2 = null;
            arr3 = null;
        }
    }

}
