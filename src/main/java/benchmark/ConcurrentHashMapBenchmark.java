package benchmark;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * Micro benchmark for ConcurrentHashMap.
 * <p>
 * # Run complete. Total time: 00:00:57
 * <p>
 * Benchmark                                   Mode  Cnt    Score     Error  Units
 * ConcurrentHashMapBenchmark.intern           avgt   10  996.578 ± 119.312  ms/op
 * ConcurrentHashMapBenchmark.internOptimized  avgt   10  618.757 ±  16.491  ms/op
 */
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(2)
@State(Scope.Thread)
public class ConcurrentHashMapBenchmark {

    private static final char[] ALPHABET = "ABCDE".toCharArray();

    public StringCache cacheNormal;
    public StringCache cacheOptimized;

    private static final int ITERATIONS = 100;

    private static final List<String> ALL_DATA = new ArrayList<>();

    static {
        for (int it = 0; it < 100; ++it) {
            for (char ch1 : ALPHABET) {
                for (char ch2 : ALPHABET) {
                    for (char ch3 : ALPHABET) {
                        for (char ch4 : ALPHABET) {
                            for (char ch5 : ALPHABET) {
                                ALL_DATA.add(Character.toString(ch1) + ch2 + ch3 + ch4 + ch5);
                            }
                        }
                    }
                }
            }
        }
    }

    @Setup(Level.Invocation)
    public void setUp() {
        cacheNormal = new StringCache();
        cacheOptimized = new StringCache();
    }

    @TearDown(Level.Invocation)
    public void tearDown() {
        cacheNormal = null;
        cacheOptimized = null;
    }

    @Benchmark
    public void intern(Blackhole bh) {
        for (int it = 0; it < ITERATIONS; ++it) {
            for (String str : ALL_DATA) {
                bh.consume(cacheNormal.intern(str));
            }
        }
    }

    @Benchmark
    public void internOptimized(Blackhole bh) {
        for (int it = 0; it < ITERATIONS; ++it) {
            for (String str : ALL_DATA) {
                bh.consume(cacheOptimized.internOptimized(str));
            }
        }
    }


    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(ConcurrentHashMapBenchmark.class.getSimpleName())
//                .threads(Runtime.getRuntime().availableProcessors())
                .build();

        new Runner(opt).run();
    }

    private static final class StringCache {

        private final ConcurrentMap<String, String> cache = new ConcurrentHashMap<>();

        /**
         * Check and update in one atomic action.
         */
        private String intern(String str) {
            String res = cache.putIfAbsent(str, str);
            return res == null ? str : res;
        }

        /**
         * Check first, because ConcurrentHashMap is faster on read and then
         * check and update in one atomic action if needed.
         */
        private String internOptimized(String str) {

            String res = cache.get(str);

            if (res != null) {
                return res;
            }

            res = cache.putIfAbsent(str, str);
            return res == null ? str : res;
        }

    }

}
