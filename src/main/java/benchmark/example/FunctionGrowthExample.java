package benchmark.example;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Function growth benchmark example.
 */
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 3, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Fork(2)
public class FunctionGrowthExample {

    private static final Random RAND = ThreadLocalRandom.current();

    @State(Scope.Benchmark)
    public static class MyState {

        @Param({"8000", "16000", "32000", "64000"})
        private int length;

        private int[] baseArr;
        private int[] arrToSort;

        @Setup(Level.Trial)
        public void setUp() {
            baseArr = new int[length];
            for (int i = 0; i < baseArr.length; ++i) {
                baseArr[i] = RAND.nextInt();
            }
        }

        @Setup(Level.Invocation)
        public void makeArrayCopy() {
            arrToSort = new int[baseArr.length];
            System.arraycopy(baseArr, 0, arrToSort, 0, baseArr.length);
        }

    }

    @Benchmark
    public void insertionSort(MyState state) {
        insertionSortInPlace(state.arrToSort);
    }

    private static void insertionSortInPlace(int[] arr) {

        for (int i = 1; i < arr.length; ++i) {

            final int temp = arr[i];
            int j = i - 1;

            while (j >= 0 && arr[j] > temp) {
                arr[j + 1] = arr[j];
                --j;
            }

            arr[j + 1] = temp;
        }

    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(FunctionGrowthExample.class.getSimpleName())
                .threads(Runtime.getRuntime().availableProcessors())
                .build();

        new Runner(opt).run();
    }

}
