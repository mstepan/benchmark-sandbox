package benchmark;

import com.max.algs.util.ArrayUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Micro benchmark for conditional jump.
 */
@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(5)
public class NumOfBitsCalculationBenchmark {

    private static final int ARR_LENGTH = 10_000;

    private static short[] BITS_COUNT_PER_BYTE = new short[256];

    static {
        for (int i = 0; i < BITS_COUNT_PER_BYTE.length; ++i) {
            BITS_COUNT_PER_BYTE[i] = (short) numOfBitsOptimized(i);
        }
    }

    /**
     * Calculate number of bits using conditional jump.
     */
    private static int numOfBitsWithJump(int baseValue) {

        int value = baseValue;
        int bitsCount = 0;

        while (value != 0) {
            if ((value & 1) == 1) {
                ++bitsCount;
            }
            value >>>= 1;
        }

        return bitsCount;
    }

    /**
     * Calculate number of bits WITHOUT conditional jump.
     */
    private static int numOfBits(int baseValue) {

        int value = baseValue;
        int bitsCount = 0;

        while (value != 0) {
            bitsCount += (value & 1);
            value >>>= 1;
        }

        return bitsCount;
    }

    /**
     * Calculate number of bits algorithmically optimised.
     */
    private static int numOfBitsOptimized(int baseValue) {

        int value = baseValue;
        int bitsCount = 0;

        while (value != 0) {
            ++bitsCount;
            value &= (value - 1);
        }

        return bitsCount;
    }

    /**
     * Calculate number of bits converting number to String.
     */
    private static int numOfBitsString(int baseValue) {

        String value = Integer.toBinaryString(baseValue);
        int bitsCount = 0;

        for (int i = 0, length = value.length(); i < length; ++i) {
            if (value.charAt(i) == '1') {
                ++bitsCount;
            }
        }

        return bitsCount;
    }

    /**
     * Calculate number of bits using previously precomputed values for bytes.
     */
    private static int numOfBitsPrecomputed(int baseValue) {
        return BITS_COUNT_PER_BYTE[baseValue & 0xFF] +
                BITS_COUNT_PER_BYTE[(baseValue >>> 8) & 0xFF] +
                BITS_COUNT_PER_BYTE[(baseValue >>> 16) & 0xFF] +
                BITS_COUNT_PER_BYTE[(baseValue >>> 24) & 0xFF];
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(NumOfBitsCalculationBenchmark.class.getSimpleName())
                .threads(Runtime.getRuntime().availableProcessors())
                .build();

        new Runner(opt).run();
    }

    @Benchmark
    @Group("numOfBitsWithJump")
    @GroupThreads(2)
    public void numOfBitsWithJump(ArrPerThread state) {
        for (int value : state.arr1) {
            numOfBitsWithJump(value);
        }
    }

    @Benchmark
    @Group("numOfBits")
    @GroupThreads(2)
    public void numOfBits(ArrPerThread state) {
        for (int value : state.arr2) {
            numOfBits(value);
        }
    }

    @Benchmark
    @Group("numOfBitsOptimized")
    @GroupThreads(2)
    public void numOfBitsOptimized(ArrPerThread state) {
        for (int value : state.arr3) {
            numOfBitsOptimized(value);
        }
    }

    @Benchmark
    @Group("numOfBitsString")
    @GroupThreads(2)
    public void numOfBitsString(ArrPerThread state) {
        for (int value : state.arr4) {
            numOfBitsString(value);
        }
    }

    @Benchmark
    @Group("numOfBitsPrecomputed")
    @GroupThreads(2)
    public void numOfBitsPrecomputed(ArrPerThread state) {
        for (int value : state.arr5) {
            numOfBitsPrecomputed(value);
        }
    }

    @State(Scope.Thread)
    public static class ArrPerThread {

        private int[] arr1;
        private int[] arr2;
        private int[] arr3;
        private int[] arr4;
        private int[] arr5;

        @Setup(Level.Invocation)
        public void setUp() {
            arr1 = ArrayUtils.generateRandomArray(ARR_LENGTH);
            arr2 = Arrays.copyOf(arr1, arr1.length);
            arr3 = Arrays.copyOf(arr1, arr1.length);
            arr4 = Arrays.copyOf(arr1, arr1.length);
            arr5 = Arrays.copyOf(arr1, arr1.length);
        }

        @TearDown(Level.Invocation)
        public void tearDown() {
            arr1 = null;
            arr2 = null;
            arr3 = null;
            arr4 = null;
            arr5 = null;
        }
    }

}
