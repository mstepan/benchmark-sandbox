package benchmark.intrinsics;

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
 * Micro benchmark Math.max(...).
 */
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(2)
public class MaxBenchmark {

    @Benchmark
    @Group("maxFromUtils")
    @GroupThreads(2)
    public void maxFromUtils(ArrPerThread state, Blackhole bh) {
        bh.consume(Math.max(state.x, state.y));
    }

    @Benchmark
    @Group("myMax")
    @GroupThreads(2)
    public void myMax(ArrPerThread state, Blackhole bh) {
        bh.consume(myMaxCall(state.x, state.y));
    }

    private static int myMaxCall(int x, int y) {
        if (x >= y) {
            return x;
        }
        return y;
    }

    private static final Random RAND = ThreadLocalRandom.current();

    @State(Scope.Thread)
    public static class ArrPerThread {

        public int x;
        public int y;

        @Setup(Level.Invocation)
        public void setUp() {
            x = RAND.nextInt();
            y = RAND.nextInt();
        }

        @TearDown(Level.Invocation)
        public void tearDown() {

        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(MaxBenchmark.class.getSimpleName())
                .threads(Runtime.getRuntime().availableProcessors())
                .build();

        new Runner(opt).run();
    }

}
