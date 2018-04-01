package benchmark.ds;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@State(Scope.Group)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(3)
public class HashSetAddBenchmark {

    private static final float LOAD_FACTOR = 0.75F;
    private static final int ELEMS_COUNT = 1_000_000;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(HashSetAddBenchmark.class.getSimpleName())
                .threads(Runtime.getRuntime().availableProcessors())
                .build();

        new Runner(opt).run();
    }

    @Benchmark
    @Group("addHashSet")
    @GroupThreads(4)
    public void addHashSet() {
        Set<Integer> data = new HashSet<>();

        for (int i = 0; i < ELEMS_COUNT; ++i) {
            data.add(i);
        }
    }

    @Benchmark
    @Group("addHashSetNoResizing")
    @GroupThreads(4)
    public void addHashSetNoResizing() {
        Set<Integer> data = new HashSet<>((int) ((ELEMS_COUNT / 0.75) + 1));

        for (int i = 0; i < ELEMS_COUNT; ++i) {
            data.add(i);
        }
    }

}
