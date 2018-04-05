package benchmark.example;

import com.max.algs.util.ArrayUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * Micro benchmark example.
 */
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(2)
@State(Scope.Thread)
public class BenchmarkExample {

    public int[] arr;
    public int[] copy;

    @Setup(Level.Invocation)
    public void setUp() {
        arr = ArrayUtils.generateRandomArray(1_000);
        copy = new int[arr.length];
    }

    @TearDown(Level.Invocation)
    public void tearDown() {
        arr = null;
        copy = null;
    }

    @Benchmark
    public void copyIntrinsics() {
        System.arraycopy(arr, 0, copy, 0, arr.length);
    }

    @Benchmark
    public void loopCopy() {
        for (int i = 0; i < arr.length; ++i) {
            copy[i] = arr[i];
        }
    }


    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(BenchmarkExample.class.getSimpleName())
//                .threads(Runtime.getRuntime().availableProcessors())
                .build();

        new Runner(opt).run();
    }

}
