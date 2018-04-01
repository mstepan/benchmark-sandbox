package benchmark;

import com.max.algs.util.ArrayUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * Mod/div benchmark.
 */
@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(5)
public class ModDivBenchmark {

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(ModDivBenchmark.class.getSimpleName())
                .threads(Runtime.getRuntime().availableProcessors())
                .build();

        new Runner(opt).run();
    }

    @Benchmark
    @Group("mod")
    @GroupThreads(4)
    public void mod(DataState state) {
        for (int val : state.arr) {
            int res = val % 10;
        }
    }

    @Benchmark
    @Group("modSub")
    @GroupThreads(4)
    public void modSub(DataState state) {
        for (int val : state.arr) {
            int res = (val < 10) ? val : val - 10;
        }
    }

    @State(Scope.Thread)
    public static class DataState {

        public int[] arr;

        @Setup(Level.Invocation)
        public void setUp() {
            arr = ArrayUtils.generateRandomArray(1000, 20);
        }

        @TearDown(Level.Invocation)
        public void tearDown() {
            arr = null;
        }
    }
}
