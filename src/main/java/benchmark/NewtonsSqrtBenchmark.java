package benchmark;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * Micro benchmark for a shell sort vs jdk-sort algorithm.
 */
@State(Scope.Group)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(5)
public class NewtonsSqrtBenchmark {

    private static final int ITERATIONS = 1_000;
    private static final double PRECISION = 0.00001;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(NewtonsSqrtBenchmark.class.getSimpleName())
                .threads(Runtime.getRuntime().availableProcessors())
                .build();

        new Runner(opt).run();
    }

    /**
     * Newton-Rhapsody method to find square root function.
     */
    private static double sqrt2(double value, double initialGuess) {
        double guess = initialGuess;

        double diff;

        while (true) {

            diff = Math.abs(guess * guess - value);

            if (Double.compare(diff, PRECISION) < 0) {
                return guess;
            }

            guess = guess - (guess * guess - value) / (2.0 * guess);
        }
    }

    @Benchmark
    @Group("sqrt1")
    @GroupThreads(2)
    public void sqrt1() {
        for (int i = 0; i < ITERATIONS; ++i) {
            sqrt2(i, 1.0);
        }
    }

    @Benchmark
    @Group("sqrt2")
    @GroupThreads(2)
    public void sqrt2() {
        for (int i = 0; i < ITERATIONS; ++i) {
            sqrt2(i, i / 2.0);
        }
    }

    @Benchmark
    @Group("jdkSqrt")
    @GroupThreads(2)
    public void jdkSqrt() {
        for (int i = 0; i < ITERATIONS; ++i) {
            Math.sqrt(i);
        }
    }

}
