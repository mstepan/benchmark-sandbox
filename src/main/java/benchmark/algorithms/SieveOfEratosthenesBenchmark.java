package benchmark.algorithms;

import com.max.algs.primes.PrimeUtilities;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * Micro benchmark for Sieve of Eratosthenes (normal and segmented implementations).
 */
@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(5)
public class SieveOfEratosthenesBenchmark {

    private static final int N = 100_000_000;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(SieveOfEratosthenesBenchmark.class.getSimpleName())
                .threads(Runtime.getRuntime().availableProcessors())
                .build();

        new Runner(opt).run();
    }

    @Benchmark
    @Group("countPrimesSegmentedWithBoolean")
    @GroupThreads(2)
    public void countPrimesSegmentedWithBoolean() {
        PrimeUtilities.countPrimesSegmentedWithBoolean(N);
    }

//    @Benchmark
//    @Group("countPrimes")
//    @GroupThreads(2)
//    public void countPrimes() {
//        PrimeUtilities.countPrimes(N);
//    }

    @Benchmark
    @Group("countPrimesSegmented")
    @GroupThreads(2)
    public void countPrimesSegmented() {
        PrimeUtilities.countPrimesSegmented(N);
    }

}
