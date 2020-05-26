package benchmark.example;

import java.util.concurrent.TimeUnit;

import com.max.algs.util.ArrayUtils;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * Micro benchmark example.
 */
@Fork(2) // default is 10
@Warmup(iterations = 2, time = 1) // default is 10
@Measurement(iterations = 5, time = 1) // default is 10
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class ExampleBenchmark {

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
    public void copyIntrinsics(Blackhole bh) {
        System.arraycopy(arr, 0, copy, 0, arr.length);
        bh.consume(copy);
    }

    @Benchmark
    public void loopCopy(Blackhole bh) {
        for (int i = 0; i < arr.length; ++i) {
            copy[i] = arr[i];
        }
        bh.consume(copy);
    }


    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(ExampleBenchmark.class.getSimpleName())
//                .threads(Runtime.getRuntime().availableProcessors())
                .build();

        new Runner(opt).run();
    }

}
