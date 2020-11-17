package benchmark.arithmetic;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Micro benchmark for NumberUtils class.
 *
 * Benchmark                                       Mode  Cnt   Score   Error  Units
 * NumberUtilsBenchmark.isPalindromeAsString       avgt   10  52.649 ± 0.373  ns/op <-- 2nd place
 * NumberUtilsBenchmark.isPalindromeClassic        avgt   10  61.039 ± 0.834  ns/op <-- 3rd place
 * NumberUtilsBenchmark.isPalindromeReverseNumber  avgt   10  49.458 ± 0.920  ns/op <-- 1st place
 *
 */
@Fork(2) // default is 10
@Warmup(iterations = 2, time = 1) // default is 10
@Measurement(iterations = 5, time = 1) // default is 10
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class NumberUtilsBenchmark {

    private static final ThreadLocalRandom RAND = ThreadLocalRandom.current();

    public int number;

    @Setup(Level.Invocation)
    public void setUp() {
        number = RAND.nextInt();
    }

    @Benchmark
    public void isPalindromeReverseNumber(Blackhole bh) {
        bh.consume(NumberUtils.isPalindromeReverseNumber(number));
    }

    @Benchmark
    public void isPalindromeAsString(Blackhole bh) {
        bh.consume(NumberUtils.isPalindromeAsString(number));
    }

    @Benchmark
    public void isPalindromeClassic(Blackhole bh) {
        bh.consume(NumberUtils.isPalindromeClassic(number));
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(NumberUtilsBenchmark.class.getSimpleName())
//                .threads(Runtime.getRuntime().availableProcessors())
                .build();

        new Runner(opt).run();
    }

}
