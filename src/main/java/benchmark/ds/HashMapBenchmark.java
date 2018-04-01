package benchmark.ds;

import com.max.algs.hashing.open.OpenHashMap;
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

/**
 * Micro benchmark for adding value to beginning of collection.
 */
@State(Scope.Group)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(2)
public class HashMapBenchmark {

    private static final Random rand = ThreadLocalRandom.current();

    private static final String DUMMY_VALUE = "dome value";

    private static final Map<Integer, String> jdkMap = new HashMap<>();
    private static final OpenHashMap<Integer, String> openMap = new OpenHashMap<>(0.5);

    static {
        for (int i = 0; i < 10_000_000; i++) {

            int value = rand.nextInt();

            jdkMap.put(value, DUMMY_VALUE);
            openMap.put(value, DUMMY_VALUE);
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(HashMapBenchmark.class.getSimpleName())
                .threads(Runtime.getRuntime().availableProcessors())
                .build();

        new Runner(opt).run();
    }

    @Benchmark
    @Group("jdkHashMap")
    @GroupThreads(2)
    public void getJdkHashMap() {
        for (int i = 0; i < 1_000_000; ++i) {
            jdkMap.get(rand.nextInt());
        }
    }

    @Benchmark
    @Group("openAddressing")
    @GroupThreads(2)
    public void getOpenHashMap() {
        for (int i = 0; i < 1_000_000; i++) {
            openMap.get(rand.nextInt());
        }
    }

}
