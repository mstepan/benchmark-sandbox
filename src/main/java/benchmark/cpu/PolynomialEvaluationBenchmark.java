package benchmark.cpu;

import com.max.algs.util.ArrayUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * # Run complete. Total time: 00:01:18
 *
 * Benchmark                                    Mode  Cnt        Score       Error  Units
 * PolynomialEvaluationBenchmark.polyHorners    avgt   10     7767.084 ±   682.126  ns/op
 * PolynomialEvaluationBenchmark.polyOptimized  avgt   10     7304.109 ±   465.330  ns/op
 * PolynomialEvaluationBenchmark.polyQuadratic  avgt   10  1810844.277 ± 65056.565  ns/op
 *
 * Micro benchmark for different polynomial evaluation algorithms.
 */
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(2)
@State(Scope.Thread)
public class PolynomialEvaluationBenchmark {

    private static final Random RAND = ThreadLocalRandom.current();

    public int[] coeff1;
    public int[] coeff2;
    public int[] coeff3;
    public int x;

    @Setup(Level.Invocation)
    public void setUp() {
        coeff1 = ArrayUtils.generateRandomArray(1_000, -100, 100);
        coeff2 = Arrays.copyOf(coeff1, coeff1.length);
        coeff3 = Arrays.copyOf(coeff1, coeff1.length);
        x = RAND.nextInt(20);
    }

    @TearDown(Level.Invocation)
    public void tearDown() {
        coeff1 = null;
        coeff2 = null;
        coeff3 = null;
    }

    /**
     * time: O(N^2)
     */
    @Benchmark
    public void polyQuadratic(Blackhole bh) {
        bh.consume(polyQuadraticEval(coeff1, x));
    }

    private static long polyQuadraticEval(int[] coeff, int x) {
        long res = 0L;

        for (int i = 0; i < coeff.length; ++i) {
            res += (coeff[i] * pow(x, i));
        }

        return res;
    }

    private static long pow(int value, int power) {

        long res = 1L;

        for (int i = 0; i < power; ++i) {
            res *= value;
        }

        return res;
    }


    /**
     * time: O(N), 2N multiplications, N additions
     */
    @Benchmark
    public void polyOptimized(Blackhole bh) {
        bh.consume(polyOptimizedEval(coeff2, x));
    }

    private static long polyOptimizedEval(int[] coeff, int x) {
        long res = coeff[0];
        long xPow = 1L;

        for (int i = 1; i < coeff.length; ++i) {
            xPow *= x;
            res += (coeff[i] * xPow);
        }
        return res;
    }

    /**
     * Using Horner's rule.
     * time: O(N), N multiplications, N additions
     */
    @Benchmark
    public void polyHorners(Blackhole bh) {
        bh.consume(polyHornerEval(coeff3, x));
    }

    private static long polyHornerEval(int[] coeff, int x) {
        long res = 0L;

        for (int i = coeff.length - 1; i >= 0; --i) {
            res = res * x + coeff[i];
        }

        return res;
    }


    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(PolynomialEvaluationBenchmark.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }

}
