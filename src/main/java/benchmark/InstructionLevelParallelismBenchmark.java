package benchmark;

import com.max.algs.util.ArrayUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Micro benchmark for instruction level parallelism (ILP).
 */
@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(5)
public class InstructionLevelParallelismBenchmark {

    private static final int ARR_LENGTH = 1 << 20; // 1_048_576

    private static double evaluatePolynom(double[] coefficients, double x) {

        long res = 0L;
        long xPow = 1L;

        for (int i = 0; i < coefficients.length; ++i) {
            res += coefficients[i] * xPow;
            xPow *= x;
        }

        return res;
    }

    private static double evaluatePolynomHorner(double[] coefficients, double x) {

        double res = 0.0;

        for (int i = coefficients.length - 1; i >= 0; --i) {
            res = res * x + coefficients[i];
        }

        return res;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(InstructionLevelParallelismBenchmark.class.getSimpleName())
                .threads(Runtime.getRuntime().availableProcessors())
                .build();

        new Runner(opt).run();
    }

    @Benchmark
    @Group("polynom")
    @GroupThreads(4)
    public void polynom(ArrPerThread state) {
        evaluatePolynom(state.arr1, state.x);
    }

    @Benchmark
    @Group("polynomHorner")
    @GroupThreads(4)
    public void polynomHorner(ArrPerThread state) {
        evaluatePolynomHorner(state.arr2, state.x);
    }

    @State(Scope.Thread)
    public static class ArrPerThread {

        private static final Random RAND = new Random();

        public double[] arr1;
        public double[] arr2;

        public double x;

        @Setup(Level.Invocation)
        public void setUp() {
            arr1 = ArrayUtils.generateDoubleArray(ARR_LENGTH);
            arr2 = Arrays.copyOf(arr1, arr1.length);
            x = RAND.nextDouble();
        }

        @TearDown(Level.Invocation)
        public void tearDown() {
            arr1 = null;
            arr2 = null;
        }
    }

}
