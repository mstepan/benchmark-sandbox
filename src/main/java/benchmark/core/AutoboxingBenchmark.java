package benchmark.core;

import org.apache.log4j.Logger;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.TimeUnit;

/**
 * Micro benchmark for autoboxing vs primitive values.
 */
@State(Scope.Group)
@OutputTimeUnit(TimeUnit.SECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(5)
public class AutoboxingBenchmark {

    private static final Logger LOG = Logger.getLogger(MethodHandles.lookup().lookupClass());

    @Benchmark
    @Group("sumAutoboxing")
    @GroupThreads(4)
    public void sumAutoboxing() {
        Long res = 0L;

        for (long i = 0; i < 100_000_000; ++i) {
            res += i;
        }
    }

    @Benchmark
    @Group("sumPrimitive")
    @GroupThreads(4)
    public void sumPrimitive() {

        long res = 0L;

        for (long i = 0; i < 100_000_000; ++i) {
            res += i;
        }
    }


    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(AutoboxingBenchmark.class.getSimpleName())
                .threads(Runtime.getRuntime().availableProcessors())
                .build();

        new Runner(opt).run();
    }

}
