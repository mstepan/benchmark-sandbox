package benchmark;

import com.max.algs.util.MatrixUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;


@State(Scope.Group)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(5)
public class MatrixMultiplicationCacheMissBenchmark {

    private static final int ARR_SIZE = 100;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(MatrixMultiplicationCacheMissBenchmark.class.getSimpleName())
                .threads(Runtime.getRuntime().availableProcessors())
                .build();

        new Runner(opt).run();
    }

    @Benchmark
    @Group("standard")
    @GroupThreads(2)
    public void standard(ArrPerThread state) {
        int[][] a = state.a1;
        int[][] b = state.b1;

        int[][] c = new int[ARR_SIZE][ARR_SIZE];

        final int iLength = a.length;
        final int jLength = b[0].length;
        final int kLength = b.length;

        for (int i = 0; i < iLength; ++i) {
            for (int j = 0; j < jLength; ++j) {
                for (int k = 0; k < kLength; ++k) {
                    c[i][j] += a[i][k] + b[k][j];
                }
            }
        }
    }

    @Benchmark
    @Group("optimized")
    @GroupThreads(2)
    public void optimized(ArrPerThread state) {
        int[][] a = state.a2;
        int[][] b = state.b2;

        int[][] c = new int[ARR_SIZE][ARR_SIZE];

        final int iLength = a.length;
        final int jLength = b[0].length;
        final int kLength = b.length;

        for (int i = 0; i < iLength; ++i) {
            for (int k = 0; k < kLength; ++k) {
                for (int j = 0; j < jLength; ++j) {
                    c[i][j] += a[i][k] + b[k][j];
                }
            }
        }

    }

    @State(Scope.Thread)
    public static class ArrPerThread {

        public int[][] a1;
        public int[][] b1;

        public int[][] a2;
        public int[][] b2;

        @Setup(Level.Invocation)
        public void setUp() {
            a1 = MatrixUtils.generateRandomMatrix(ARR_SIZE);
            b1 = MatrixUtils.generateRandomMatrix(ARR_SIZE);

            a2 = MatrixUtils.deepCopy(a1);
            b2 = MatrixUtils.deepCopy(b1);
        }

        @TearDown(Level.Invocation)
        public void tearDown() {
            a1 = null;
            b1 = null;

            a2 = null;
            b2 = null;
        }
    }

}
