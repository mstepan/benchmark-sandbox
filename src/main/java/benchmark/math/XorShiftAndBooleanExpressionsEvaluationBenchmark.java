package benchmark.math;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Micro benchmark for xor, shift and boolean expression evaluations.
 */
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(2)
@State(Scope.Thread)
public class XorShiftAndBooleanExpressionsEvaluationBenchmark {

    public int x;
    public int y;

    private static final Random RAND = ThreadLocalRandom.current();

    @Setup(Level.Invocation)
    public void setUp() {
        x = RAND.nextInt();
        y = RAND.nextInt();
    }

    @TearDown(Level.Invocation)
    public void tearDown() {
    }

    @Benchmark
    public void sameSignBruteforceBenchmark(Blackhole bh) {
        bh.consume(sameSignBruteforce(x, y));
    }

    private static boolean sameSignBruteforce(int x, int y) {
        return (x >= 0 && y >= 0) || (x < 0 && y < 0);
    }

    @Benchmark
    public void sameSignOptimizedBenchmark(Blackhole bh) {
        bh.consume(sameSignOptimized(x, y));
    }

    private static boolean sameSignOptimized(int x, int y) {
        return ((x ^ y) >> 31) == 0;
    }

    @Benchmark
    public void sameSign2Benchmark(Blackhole bh) {
        bh.consume(sameSign2(x, y));
    }

    private static boolean sameSign2(int x, int y) {
        return ((x >> 31) ^ (y >> 31)) == 0;
    }


    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(XorShiftAndBooleanExpressionsEvaluationBenchmark.class.getSimpleName())
//                .threads(Runtime.getRuntime().availableProcessors())
                .build();

        new Runner(opt).run();
    }

}
