package benchmark.concurrency;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@State(Scope.Group)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(1)
/*
 * ReduceLockScopeBenchmark.bigScope    avgt    5  0.785 ± 5.471  ms/op
 * ReduceLockScopeBenchmark.smallScope  avgt    5  0.050 ± 0.010  ms/op
 */
public class ReduceLockScopeBenchmark {

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(ReduceLockScopeBenchmark.class.getSimpleName())
                .threads(Runtime.getRuntime().availableProcessors())
                .build();

        new Runner(opt).run();
    }

    @State(Scope.Group)
    public static class MapPerThreadGroup {

        public Map<String, Long> map;
        public Random rand;

        @Setup(Level.Iteration)
        public void setUp() {
            map = new HashMap<>();
            rand = ThreadLocalRandom.current();
        }

        @TearDown(Level.Iteration)
        public void tearDown() {
            map = null;
            rand = null;
        }
    }

    @Benchmark
    @Group("smallScope")
    @GroupThreads(100)
    public void smallScope(MapPerThreadGroup state) {

        Long value = state.rand.nextLong();
        String key = String.valueOf(value) + "-" + value;

        synchronized (state.map) {
            state.map.put(key, value);
        }
    }

    @Benchmark
    @Group("bigScope")
    @GroupThreads(100)
    public void bigScope(MapPerThreadGroup state) {

        synchronized (state.map) {
            Long value = state.rand.nextLong();
            String key = String.valueOf(value) + "-" + value;
            state.map.put(key, value);
        }
    }

}
