package benchmark.sort;

import org.jetbrains.annotations.NotNull;
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

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Micro benchmark for Comparable vs Comparator usage for sorting array elements.
 *
 * This benchmark inspired from reading Effective java 3rd edition Item 14.
 *
 * "Many programmers prefer the conciseness of this
 * approach, though it does come at a modest performance cost: sorting arrays of
 * PhoneNumber instances is about 10% slower on my machine." (Josh Bloch)
 *
 * <p>
 * Benchmark                                       Mode  Cnt       Score      Error  Units
 * ComparatorsBenchmark.sortComparable             avgt   10  172_220.012 ± 3668.372  ns/op ==> 85%
 * ComparatorsBenchmark.sortDeclarativeComparator  avgt   10  201_702.196 ± 1636.277  ns/op ==> 100%
 *
 * So, on my machine the real performance difference is 15%.
 */
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(2)
@State(Scope.Thread)
public class ComparatorsBenchmark {

    private static final int ARR_LENGTH = 1_000;

    public WebUserComparable[] arr1;
    public WebUserDeclarativeCmp[] arr2;

    @Setup(Level.Invocation)
    public void setUp() {
        arr1 = new WebUserComparable[ARR_LENGTH];
        arr2 = new WebUserDeclarativeCmp[ARR_LENGTH];

        for (int i = 0; i < arr1.length; ++i) {
            String username = generateRandomAsciiString(10);
            String password = generateRandomAsciiString(10);
            int age = 10 + RAND.nextInt(100);

            arr1[i] = new WebUserComparable(username, password, age);
            arr2[i] = new WebUserDeclarativeCmp(username, password, age);
        }
    }

    private static final Random RAND = new Random();

    private static String generateRandomAsciiString(int length) {
        StringBuilder buf = new StringBuilder(length);

        for (int i = 0; i < length; ++i) {
            buf.append((char) ('a' + RAND.nextInt('z' - 'a' + 1)));
        }

        return buf.toString();
    }

    @TearDown(Level.Invocation)
    public void tearDown() {
        arr1 = null;
        arr2 = null;
    }


    @Benchmark
    public void sortComparable(Blackhole bh) {
        Arrays.sort(arr1);
        bh.consume(arr1);
    }

    @Benchmark
    public void sortDeclarativeComparator(Blackhole bh) {
        Arrays.sort(arr2);
        bh.consume(arr2);
    }


    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(ComparatorsBenchmark.class.getSimpleName())
//                .threads(Runtime.getRuntime().availableProcessors())
                .build();

        new Runner(opt).run();
    }

    private static final class WebUserComparable implements Comparable<WebUserComparable> {

        private final String username;
        private final String password;
        private final int age;

        public WebUserComparable(String username, String password, int age) {
            this.username = username;
            this.password = password;
            this.age = age;
        }

        @Override
        public int compareTo(@NotNull WebUserComparable other) {

            int cmpRes = username.compareTo(other.username);

            if (cmpRes != 0) {
                return cmpRes;
            }

            cmpRes = password.compareTo(other.password);

            if (cmpRes != 0) {
                return cmpRes;
            }

            return Integer.compare(age, other.age);
        }
    }

    private static final class WebUserDeclarativeCmp implements Comparable<WebUserDeclarativeCmp> {

        private static final Comparator<WebUserDeclarativeCmp> CMP =
                Comparator.comparing((WebUserDeclarativeCmp user) -> user.username).
                        thenComparing(user -> user.password).
                        thenComparing(user -> user.age);

        private final String username;
        private final String password;
        private final int age;

        public WebUserDeclarativeCmp(String username, String password, int age) {
            this.username = username;
            this.password = password;
            this.age = age;
        }

        @Override
        public int compareTo(@NotNull WebUserDeclarativeCmp other) {
            return CMP.compare(this, other);
        }
    }

}
