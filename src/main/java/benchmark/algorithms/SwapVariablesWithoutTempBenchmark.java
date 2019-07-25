package benchmark.algorithms;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Micro benchmark for swapping two variables without temp storage.
 *
 * # Run complete. Total time: 00:00:44
 *
 * Benchmark                                        Mode  Cnt    Score    Error  Units
 * SwapVariablesWithoutTempBenchmark.swap           avgt   10  256.854 ± 12.248  ns/op
 * SwapVariablesWithoutTempBenchmark.swapOptimized  avgt   10  263.500 ±  4.856  ns/op
 */
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(2)
@State(Scope.Thread)
public class SwapVariablesWithoutTempBenchmark {

    private static final Random RAND = ThreadLocalRandom.current();

    public int x;
    public int y;

    @Setup(Level.Invocation)
    public void setUp() {
        x = RAND.nextInt();
        y = RAND.nextInt();
    }


    @Benchmark
    public void swap(Blackhole bh) {

        int temp = x;
        x = y;
        y = temp;

        bh.consume(x);
        bh.consume(y);
    }

    @Benchmark
    public void swapOptimized(Blackhole bh) {

        x ^= y;
        y ^= x;
        x ^= y;

        bh.consume(x);
        bh.consume(y);
    }


    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(SwapVariablesWithoutTempBenchmark.class.getSimpleName())
                .threads(Runtime.getRuntime().availableProcessors())
                .build();

        new Runner(opt).run();
    }
}
