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
 * Write-read memory dependency benchmark.
 */
@State(Scope.Group)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(5)
public class WriteReadMemoryDependency2Benchmark {

    private static final int ARR_LENGTH = 10_000;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(WriteReadMemoryDependency2Benchmark.class.getSimpleName())
                .threads(Runtime.getRuntime().availableProcessors())
                .build();

        new Runner(opt).run();
    }

    @Benchmark
    @Group("sum")
    @GroupThreads(2)
    public void sum(ArrPerThread state) {
        final int[] a = state.a1;
        final int[] p = state.p1;
        final int pLength = p.length;

        p[0] = a[0];

        for (int i = 1; i < pLength; ++i) {
            p[i] = p[i - 1] + a[i];
        }
    }

    @Benchmark
    @Group("sumOptimised")
    @GroupThreads(2)
    public void sumOptimised(ArrPerThread state) {
        final int[] a = state.a1;
        final int[] p = state.p1;
        final int pLength = p.length;

        p[0] = a[0];
        int curSum = a[0];

        for (int i = 1; i < pLength; ++i) {
            curSum += a[i];
            p[i] = curSum;
        }
    }

    @State(Scope.Thread)
    public static class ArrPerThread {

        public int[] a1;
        public int[] p1;

        public int[] a2;
        public int[] p2;

        @Setup(Level.Invocation)
        public void setUp() {
            a1 = ArrayUtils.generateRandomArray(ARR_LENGTH);
            a2 = Arrays.copyOf(a1, a1.length);
            p1 = new int[a1.length];
            p2 = new int[a2.length];
        }

        @TearDown(Level.Invocation)
        public void tearDown() {
            a1 = null;
            a2 = null;
            p1 = null;
            p2 = null;
        }
    }

}
