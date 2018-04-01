package benchmark.memory;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * Micro benchmark for memory touching every item vs touching every element on a cache line.
 * <p>
 * Cache line: 64 bytes. Use: `sysctl -a | grep machdep.cpu.cache.linesize`
 */
@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(5)
public class TouchEveryItemAndEveryCacheLineBenchmark {

    private static final int ARR_LENGTH = 10 * 1024 * 1024; // 10 MB

    /*
     * Memory transfer speed = (10 MB / 2.5 ms) = 4 GB/sec
     *
     * theoretical maximum write speed of 8-12GB/sec according to 2015
     */

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(TouchEveryItemAndEveryCacheLineBenchmark.class.getSimpleName())
                .threads(Runtime.getRuntime().availableProcessors())
                .build();

        new Runner(opt).run();
    }

    @Benchmark
    @Group("touchEachElement")
    @GroupThreads(4)
    public void touchEachElement(ArrPerThread state) {
        byte[] arr = state.arr1;
        for (int i = 0; i < arr.length; ++i) {
            ++arr[i];
        }
    }

    @Benchmark
    @Group("touchEachCacheLine")
    @GroupThreads(4)
    public void touchEachCacheLine(ArrPerThread state) {
        byte[] arr = state.arr2;

        // jump over 64 bytes(aka one cache line)
        for (int i = 0; i < arr.length; i += 64) {
            ++arr[i];
        }
    }

    @State(Scope.Thread)
    public static class ArrPerThread {

        public byte[] arr1;
        public byte[] arr2;

        public double x;

        @Setup(Level.Invocation)
        public void setUp() {
            arr1 = new byte[ARR_LENGTH];
            arr2 = new byte[ARR_LENGTH];
        }

        @TearDown(Level.Invocation)
        public void tearDown() {
            arr1 = null;
            arr2 = null;
        }
    }

}
