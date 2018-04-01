package benchmark.cpu;

import com.max.algs.util.ArrayUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Branch prediction and conditional move for random and predictable data patterns.
 */
@State(Scope.Group)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(5)
public class BranchAndConditionalMoveBenchmark {

    private static final int ARR_LENGTH = 10_000;

    private static final int TEN_PERCENTS = 10;
    private static final int ONE_HUNDRED_PERCENTS = 100;

    private static void minMax(int[] a, int[] b) {
        for (int i = 0; i < a.length; ++i) {
            if (a[i] > b[i]) {
                int temp = a[i];
                a[i] = b[i];
                b[i] = temp;
            }
        }
    }

    private static void minMaxConditionalMove(int[] a, int[] b) {
        for (int i = 0; i < a.length; ++i) {
            int min = a[i] < b[i] ? a[i] : b[i];
            int max = a[i] > b[i] ? a[i] : b[i];

            a[i] = min;
            b[i] = max;
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(BranchAndConditionalMoveBenchmark.class.getSimpleName())
                .threads(Runtime.getRuntime().availableProcessors())
                .build();

        new Runner(opt).run();
    }

    @Benchmark
    @Group("jeRandomArray")
    @GroupThreads(2)
    public void jeRandomArray(ArrPerThread state) {
        minMax(state.arrRandom1, state.arrRandom2);
    }

    @Benchmark
    @Group("jePredictableArray")
    @GroupThreads(2)
    public void jePredictableArray(ArrPerThread state) {
        minMax(state.arrPred1, state.arrPred2);
    }

    @Benchmark
    @Group("cmovRandomArray")
    @GroupThreads(2)
    public void cmovRandomArray(ArrPerThread state) {
        minMaxConditionalMove(state.arrRandom3, state.arrRandom4);
    }

    @Benchmark
    @Group("cmovPredictableArray")
    @GroupThreads(2)
    public void cmovPredictableArray(ArrPerThread state) {
        minMaxConditionalMove(state.arrPred3, state.arrPred4);
    }

    @State(Scope.Thread)
    public static class ArrPerThread {

        public int[] arrRandom1;
        public int[] arrRandom2;
        public int[] arrPred1;
        public int[] arrPred2;

        public int[] arrRandom3;
        public int[] arrRandom4;
        public int[] arrPred3;
        public int[] arrPred4;

        @Setup(Level.Invocation)
        public void setUp() {
            arrRandom1 = ArrayUtils.generateRandomArray(ARR_LENGTH);
            arrRandom2 = ArrayUtils.generateRandomArray(ARR_LENGTH);

            arrRandom3 = Arrays.copyOf(arrRandom1, arrRandom1.length);
            arrRandom4 = Arrays.copyOf(arrRandom2, arrRandom2.length);

            // 10% from upper boundary, which is equals to ARR_LENGTH
            int firstArrayBoundary = (ARR_LENGTH * TEN_PERCENTS) / ONE_HUNDRED_PERCENTS;

            arrPred1 = ArrayUtils.generateRandomArray(ARR_LENGTH, firstArrayBoundary);
            arrPred2 = ArrayUtils.generateRandomArray(ARR_LENGTH, ARR_LENGTH);

            arrPred3 = Arrays.copyOf(arrPred1, arrPred1.length);
            arrPred4 = Arrays.copyOf(arrPred2, arrPred2.length);
        }
    }

}
