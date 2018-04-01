package benchmark;

import com.max.algs.hashing.HashUtils;
import com.max.algs.string.StringUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * Micro benchmark for a String hashing algorithms.
 * <p>
 * Benchmark                            Mode  Cnt        Score        Error  Units
 * StringHashingBenchmark.fnvHash       avgt   25  4007097.524 ± 540003.555  ns/op
 * StringHashingBenchmark.jenkinsHash   avgt   25  5349687.158 ± 508751.838  ns/op
 * StringHashingBenchmark.murmur3Hash   avgt   25  7538962.440 ± 513778.401  ns/op
 * StringHashingBenchmark.standardHash  avgt   25  5099322.812 ± 155859.164  ns/op
 */
@State(Scope.Group)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(5)
public class StringHashingBenchmark {

    private static final int WORDS_COUNT = 100_000;
    private static final int AVG_WORD_LENGTH = 20;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(StringHashingBenchmark.class.getSimpleName())
                .threads(Runtime.getRuntime().availableProcessors())
                .build();

        new Runner(opt).run();
    }

    @Benchmark
    @Group("standardHash")
    @GroupThreads(2)
    public void standardHash(ArrPerThread state) {
        for (String str : state.arr) {
            HashUtils.standardHash(str);
        }
    }

    @Benchmark
    @Group("fnvHash")
    @GroupThreads(2)
    public void fnvHash(ArrPerThread state) {
        for (String str : state.arr) {
            HashUtils.fnvHash(str);
        }
    }

    @Benchmark
    @Group("jenkinsHash")
    @GroupThreads(2)
    public void jenkinsHash(ArrPerThread state) {
        for (String str : state.arr) {
            HashUtils.jenkinsHash(str);
        }
    }

    @Benchmark
    @Group("murmur3Hash")
    @GroupThreads(2)
    public void murmur3Hash(ArrPerThread state) {
        for (String str : state.arr) {
            HashUtils.murmur3Hash(str);
        }
    }

    @State(Scope.Thread)
    public static class ArrPerThread {

        public String[] arr;

        @Setup(Level.Invocation)
        public void setUp() {
            arr = new String[WORDS_COUNT];

            for (int i = 0, length = arr.length; i < length; ++i) {
                arr[i] = StringUtils.randomLowerCase(AVG_WORD_LENGTH);
            }
        }

        @TearDown(Level.Invocation)
        public void tearDown() {
            arr = null;
        }
    }

}
