package benchmark.concurrency;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;
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
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * Benchmark for safe publication, comparing immutable objects with volatile reference vs fully synchronized access.
 *
 * Benchmark                                                        Mode  Cnt    Score   Error  Units
 * SafePublicationImmutableAndFullySyncBenchmark.fullySynchronized  avgt   25  160.911 ± 7.500  ns/op
 * SafePublicationImmutableAndFullySyncBenchmark.immutableObject    avgt   25  169.606 ± 5.835  ns/op
 *
 * Benchmark                                                        Mode  Cnt    Score    Error  Units
 * SafePublicationImmutableAndFullySyncBenchmark.fullySynchronized  avgt   10  187.268 ± 24.362  ns/op
 * SafePublicationImmutableAndFullySyncBenchmark.immutableObject    avgt   10  178.437 ± 10.230  ns/op
 *
 */
@Fork(2) // default is 10
@Warmup(iterations = 2, time = 1) // default is 10
@Measurement(iterations = 5, time = 1) // default is 10
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class SafePublicationImmutableAndFullySyncBenchmark {

    private static final Random RAND = new Random();
    private int randValue;
    public ImmutableRes immutableRes;
    public SynchronizedRes synchRes;

    @Setup(Level.Invocation)
    public void setUp() {
        randValue = RAND.nextInt();
        immutableRes = new ImmutableRes();
        synchRes = new SynchronizedRes();
    }

    @TearDown(Level.Invocation)
    public void tearDown() {
        immutableRes = null;
        synchRes = null;
    }

    @Benchmark
    public void immutableObject(Blackhole bh) {
        bh.consume(immutableRes.getResult());
        immutableRes.setResult(randValue, new int[] {randValue});
    }

    @Benchmark
    public void fullySynchronized(Blackhole bh) {
        bh.consume(synchRes.getResult());
        synchRes.setResult(randValue, new int[] {randValue});
    }


    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
            .include(SafePublicationImmutableAndFullySyncBenchmark.class.getSimpleName())
            .threads(Runtime.getRuntime().availableProcessors())
            .build();

        new Runner(opt).run();
    }

    // Synchronized object
    static final class SynchronizedRes {

        /**
         * Guarded by this
         */
        private int value;
        /**
         * Guarded by this
         */
        private int[] factors = new int[] {0};

        public synchronized void setResult(int value, int[] factors) {
            this.value = value;
            this.factors = factors;
        }

        public synchronized ValueAndFactors getResult() {
            return new ValueAndFactors(value, factors);
        }


    }


    // Fully immutable object
    static final class ImmutableRes {
        private volatile ValueAndFactors result = new ValueAndFactors(0, new int[] {0});

        public void setResult(int value, int[] factors) {
            result = new ValueAndFactors(value, Arrays.copyOf(factors, factors.length));
        }

        public ValueAndFactors getResult() {
            return result;
        }

    }

    static final class ValueAndFactors {

        final int value;
        final int[] factors;

        public ValueAndFactors(int value, int[] factors) {
            this.value = value;
            this.factors = factors;
        }

        int value() {
            return value;
        }

        int[] factors() {
            return factors;
        }
    }

}
