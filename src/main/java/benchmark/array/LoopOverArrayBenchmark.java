package benchmark.array;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * Micro benchmark for iterating over array of ints using for and foreach loops.
 */
@State(Scope.Group)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(5)
public class LoopOverArrayBenchmark {

    private static final int ARRAY_SIZE = 100_000_000;

    private static final int[] ARR = new int[ARRAY_SIZE];

    static {
        for (int i = 0, arrLength = ARR.length; i < arrLength; ++i) {
            ARR[i] = i;
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(LoopOverArrayBenchmark.class.getSimpleName())
                .threads(Runtime.getRuntime().availableProcessors())
                .build();

        new Runner(opt).run();
    }

    @Benchmark
    @Group("forLoop")
    @GroupThreads(2)
    public void forLoop() {
        int sum = 0;

        for (int i = 0; i < ARR.length; i++) {
            sum += ARR[i];
        }
    }

    @Benchmark
    @Group("forOptimisedLoop")
    @GroupThreads(2)
    public void forOptimisedLoop() {

        int sum = 0;

        for (int i = 0, arrLength = ARR.length; i < arrLength; ++i) {
            sum += ARR[i];
        }
    }

    @Benchmark
    @Group("foreachLoop")
    @GroupThreads(2)
    public void foreachLoop() {
        int sum = 0;

        for (int value : ARR) {
            sum += value;
        }
    }

}
