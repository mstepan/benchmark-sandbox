package benchmark.array;

import com.max.algs.util.ArrayUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Micro benchmark for array rearrange algorithms: Hoar and Lomuto like.
 */
@State(Scope.Group)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(5)
public class ArrayRearrangeBenchmark {

    /**
     * Rearrange array in-place, so all 'even' elements
     * appear first followed by 'odd' elements.
     * <p>
     * time: O(N)
     * space: O(1)
     */
    private static void rearrangeHoar(int[] arr) {

        checkNotNull(arr);

        int even = 0;
        int odd = arr.length - 1;
        int temp;

        while (even < odd) {
            // even value
            if ((arr[even] & 1) == 0) {
                ++even;
            }
            // odd value, swap elements
            else {
                temp = arr[even];
                arr[even] = arr[odd];
                arr[odd] = temp;
                --odd;
            }
        }
    }

    /**
     * Rearrange array using Lomuto's like partition technique.
     * time: O(N)
     * space: O(1)
     */
    private static void rearrangeLomuto(int[] arr) {
        checkNotNull(arr);

        int even = 0;
        int temp, value;

        for (int i = 0, arrLength = arr.length; i < arrLength; ++i) {

            value = arr[i];

            // 'even' value
            if ((value & 1) == 0) {
                temp = arr[even];
                arr[even] = value;
                arr[i] = temp;
                ++even;
            }
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(ArrayRearrangeBenchmark.class.getSimpleName())
                .threads(Runtime.getRuntime().availableProcessors())
                .build();

        new Runner(opt).run();
    }

    @Benchmark
    @Group("rearrangeHoar")
    @GroupThreads(4)
    public void rearrangeHoar(ArrPerThread state) {
        rearrangeHoar(state.arr1);
    }

    @Benchmark
    @Group("rearrangeLomuto")
    @GroupThreads(4)
    public void rearrangeLomuto(ArrPerThread state) {
        rearrangeLomuto(state.arr2);
    }

    @State(Scope.Thread)
    public static class ArrPerThread {

        public int[] arr1;
        public int[] arr2;

        @Setup(Level.Invocation)
        public void setUp() {
            arr1 = ArrayUtils.generateRandomArray(1_000_000);
            arr2 = Arrays.copyOf(arr1, arr1.length);
        }

        @TearDown(Level.Invocation)
        public void tearDown() {
            arr1 = null;
            arr2 = null;
        }
    }

}
