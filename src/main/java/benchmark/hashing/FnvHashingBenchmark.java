package benchmark.hashing;

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
import scala.util.Random;

import java.util.concurrent.TimeUnit;

/**
 * Fowler-Noll-Vo hashing benchmark.
 *
 * Benchmark                        Mode  Cnt     Score   Error  Units
 * FnvHashingBenchmark.fnvHashing   avgt   10  1225.482 ± 6.458  ns/op
 * FnvHashingBenchmark.javaHashing  avgt   10   948.459 ± 7.153  ns/op
 *
 */
@Fork(2) // default is 10
@Warmup(iterations = 2, time = 1) // default is 10
@Measurement(iterations = 5, time = 1) // default is 10
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class FnvHashingBenchmark {

    private static final int LENGTH = 1_000;

    public String str1;
    public String str2;

    @Setup(Level.Invocation)
    public void setUp() {
        str1 = randomAsciiString(LENGTH);
        str2 = randomAsciiString(LENGTH);
    }

    private static final Random RAND = new Random();
    private static final int ASCII_SIZE = 'z' - 'a' + 1;

    private static String randomAsciiString(int expectedLength) {
        StringBuilder buf = new StringBuilder(expectedLength);

        for (int i = 0; i < expectedLength; ++i) {
            char ch = (char) ('a' + RAND.nextInt(ASCII_SIZE));
            buf.append(ch);
        }

        return buf.toString();
    }

    @TearDown(Level.Invocation)
    public void tearDown() {
        str1 = null;
        str2 = null;
    }

    @Benchmark
    public void javaHashing(Blackhole bh) {
        bh.consume(str1.hashCode());
    }

    @Benchmark
    public void fnvHashing(Blackhole bh) {
        bh.consume(fnvHash(str2));
    }

    private static final int INITIAL_VALUE = (int) (2_166_136_261L);

    private static final int PRIME = 16_777_619;

    private static int fnvHash(String value) {

        int res = INITIAL_VALUE;

        for (int i = 0, length = value.length(); i < length; ++i) {
            res = (res ^ value.charAt(i)) * PRIME;
        }

        return res;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(FnvHashingBenchmark.class.getSimpleName())
//                .threads(Runtime.getRuntime().availableProcessors())
                .build();

        new Runner(opt).run();
    }
}
