package benchmark;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLongArray;

/**
 * False sharing of cache lines between threads.
 */
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 10, time = 1)
@Threads(2)
@State(Scope.Benchmark)
@Fork(5)
public class FalseSharingBenchmark {

    // cache line size 64 bytes, aka 8 longs
    // [0...7] - 1st line
    // [8..15] - 2nd line
    private final AtomicLongArray arr = new AtomicLongArray(16);

    @Param({"1", "8"})
    private int otherIndex;

    @State(Scope.Thread)
    public static class ThreadData {

        private static final AtomicInteger THREAD_COUNTER = new AtomicInteger();

        private final int threadId;

        public ThreadData() {
            this.threadId = THREAD_COUNTER.getAndIncrement();
        }
    }

    @Benchmark
    public void benchmark(ThreadData data) {
        if (data.threadId == 0) {
            this.arr.incrementAndGet(0);
        }
        else {
            this.arr.incrementAndGet(this.otherIndex);
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(FalseSharingBenchmark.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }

}
