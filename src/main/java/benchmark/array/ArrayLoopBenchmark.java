package benchmark.array;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Hoisted, c-style and foreach loops.
 */
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 3, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Fork(2)
@State(Scope.Benchmark)
public class ArrayLoopBenchmark {

    private static final Random RAND = ThreadLocalRandom.current();

    private int[] arr;

    @Setup(Level.Trial)
    public void setUp() {
        arr = new int[10_000];

        for (int i = 0; i < arr.length; ++i) {
            arr[i] = RAND.nextInt();
        }
    }

    @Benchmark
    public void cstyle(Blackhole bh) {
        for (int i = 0; i < this.arr.length; ++i) {
            consume(this.arr[i]);
        }
    }

    @Benchmark
    public void hoisted(Blackhole bh) {
        for (int i = 0, length = this.arr.length; i < length; ++i) {
            consume(this.arr[i]);
        }
    }

    @Benchmark
    public void foreach(Blackhole bh) {
        for (int val : this.arr) {
            consume(val);
        }
    }

    @Benchmark
    public void fullyHoisted(Blackhole bh) {

        final int[] arr1 = this.arr;

        for (int i = 0; i < arr1.length; ++i) {
            consume(arr1[i]);
        }
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    static void consume(int value) {
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(ArrayLoopBenchmark.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }

}
