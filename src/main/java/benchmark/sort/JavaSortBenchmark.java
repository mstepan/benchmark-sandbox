package benchmark.sort;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Java array sort benchmark.
 */
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 3, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Fork(2)
public class JavaSortBenchmark {

    private static final Random RAND = ThreadLocalRandom.current();

    @State(Scope.Thread)
    public static class MyState {

        @Param({"8000", "16000", "32000", "64000"})
        private int length;

        private int[] baseArr;
        private int[] arrToSort;

        @Setup(Level.Trial)
        public void setUp() {
            baseArr = new int[length];
            for (int i = 0; i < baseArr.length; ++i) {
                baseArr[i] = RAND.nextInt(10_000);
            }
        }

        @Setup(Level.Invocation)
        public void makeArrayCopy() {
            arrToSort = new int[baseArr.length];
            System.arraycopy(baseArr, 0, arrToSort, 0, baseArr.length);
        }
    }

    @Benchmark
    public void arraySort(MyState state) {
        Arrays.sort(state.arrToSort);
    }


    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JavaSortBenchmark.class.getSimpleName())
                .threads(Runtime.getRuntime().availableProcessors())
                .build();

        new Runner(opt).run();
    }

}
