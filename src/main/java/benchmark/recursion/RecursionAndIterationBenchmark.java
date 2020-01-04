package benchmark.recursion;

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
 * Micro benchmark for recursion, tail recursion and iteration.
 * <p>
 * Benchmark                                        Mode  Cnt   Score   Error  Units
 * RecursionAndIterationBenchmark.mulIterative      avgt   10  54.382 ± 0.367  ns/op
 * RecursionAndIterationBenchmark.mulRecursion      avgt   10  98.419 ± 0.614  ns/op
 * RecursionAndIterationBenchmark.mulTailRecursion  avgt   10  60.125 ± 4.242  ns/op
 */
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(2)
@State(Scope.Thread)
public class RecursionAndIterationBenchmark {

    private static final Random RAND = ThreadLocalRandom.current();

    private int x;
    private int y;

    @Setup(Level.Invocation)
    public void setUp() {
        x = RAND.nextInt(10_000);
        y = RAND.nextInt(10_000);
    }

    @Benchmark
    public void mulRecursion(Blackhole bh) {
        bh.consume(mulRec(x, y));
    }

    @Benchmark
    public void mulTailRecursion(Blackhole bh) {
        bh.consume(mulTailRecursive(x, y, 0));
    }

    @Benchmark
    public void mulIterative(Blackhole bh) {
        bh.consume(mulIterative(x, y));
    }

    /**
     * Peasant algorithm for numbers multiplication that use pure recursion.
     */
    private static int mulRec(int x, int y) {

        if (x == 0 || y == 0) {
            return 0;
        }

        if (isEven(x)) {
            return mulRec(x / 2, y + y);
        }

        return y + mulRec(x / 2, y + y);
    }

    /**
     * The same Peasant algorithm but use tail optimization.
     */
    private static int mulTailRecursive(int x, int y, int res) {

        if (x == 0 || y == 0) {
            return res;
        }

        return mulTailRecursive(x / 2, y + y, isEven(x) ? res : res + y);
    }

    /**
     * The same Peasant algorithm but in iterative style.
     */
    private static int mulIterative(int initialX, int initialY) {

        if (initialX == 0 || initialY == 0) {
            return 0;
        }

        int res = 0;

        int x = initialX;
        int y = initialY;

        while (x != 0) {

            if (isOdd(x)) {
                res += y;
            }

            x /= 2;
            y = y + y;
        }

        return res;
    }

    private static boolean isOdd(int value) {
        return !isEven(value);
    }

    private static boolean isEven(int value) {
        return (value & 1) == 0;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(RecursionAndIterationBenchmark.class.getSimpleName())
//                .threads(Runtime.getRuntime().availableProcessors())
                .build();

        new Runner(opt).run();
    }

}
