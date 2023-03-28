package benchmark.array;

import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Group;
import org.openjdk.jmh.annotations.GroupThreads;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

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
    public void forLoop(Blackhole bh) {
        int sum = 0;

        for (int i = 0; i < ARR.length; i++) {
            sum += ARR[i];
        }
        bh.consume(sum);
    }

    @Benchmark
    @Group("forOptimisedLoop")
    @GroupThreads(2)
    public void forOptimisedLoop(Blackhole bh) {

        int sum = 0;

        for (int i = 0, arrLength = ARR.length; i < arrLength; ++i) {
            sum += ARR[i];
        }
        bh.consume(sum);
    }

    @Benchmark
    @Group("foreachLoop")
    @GroupThreads(2)
    public void foreachLoop(Blackhole bh) {
        int sum = 0;

        for (int value : ARR) {
            sum += value;
        }
        bh.consume(sum);
    }

}
