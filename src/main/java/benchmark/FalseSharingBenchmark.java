package benchmark;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * Micro benchmark for false sharing for L1 cache.
 */
@State(Scope.Group)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(5)
public class FalseSharingBenchmark {

    /**
     * Cache line size: 64 bytes or 16 ints
     */
    public final int[] array = new int[17];

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(FalseSharingBenchmark.class.getSimpleName())
                .threads(Runtime.getRuntime().availableProcessors())
                .build();

        new Runner(opt).run();
    }

    @Benchmark
    @Group("near")
    public void modifyNearA() {
        array[0]++;
    }

    @Benchmark
    @Group("near")
    public void modifyNearB() {
        array[1]++;
    }

    @Benchmark
    @Group("far")
    public void modifyFarA() {
        array[0]++;
    }

    @Benchmark
    @Group("far")
    public void modifyFarB() {
        array[16]++;
    }

}
