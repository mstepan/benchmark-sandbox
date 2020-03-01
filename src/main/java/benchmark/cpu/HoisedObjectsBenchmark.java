package benchmark.cpu;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Hoisted foreach for a list.
 */
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(2)
@State(Scope.Benchmark)
public class HoisedObjectsBenchmark {

    private static final Random RAND = ThreadLocalRandom.current();

    private MyList list;

    @Setup(Level.Trial)
    public void setUp() {
        int[] arr = new int[1_000_000];

        for (int i = 0; i < arr.length; ++i) {
            arr[i] = RAND.nextInt();
        }

        list = new MyList(arr);
    }

    @Benchmark
    public void forEachOptimized(Blackhole bh) {
        list.forEachOptimized(bh::consume);
    }

    @Benchmark
    public void forEach(Blackhole bh) {
        list.forEach(bh::consume);
    }

    private static final class MyList {

        private final int[] arr;

        MyList(int[] arr) {
            this.arr = arr;
        }

        void forEachOptimized(Consumer<Integer> action) {

            final int[] elementData = this.arr;
            final int size = this.arr.length;

            for (int i = 0; i < size; i++) {
                action.accept(elementData[i]);
            }
        }

        void forEach(Consumer<Integer> action) {
            for (int i = 0; i < this.arr.length; i++) {
                action.accept(this.arr[i]);
            }
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(HoisedObjectsBenchmark.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }

}
