package benchmark.hashing;

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
 * Micro benchmark for hash functions.
 * <p>
 * Benchmark                             Mode  Cnt  Score   Error  Units
 * HashFunctionsBenchmark.hashNormal     avgt   25  0.780 ± 0.006  ms/op
 * HashFunctionsBenchmark.hashOptimized  avgt   25  0.421 ± 0.020  ms/op
 * HashFunctionsBenchmark.hashSuperFast  avgt   25  0.273 ± 0.001  ms/op
 */
@Fork(2) // default is 10
@Warmup(iterations = 2, time = 1) // default is 10
@Measurement(iterations = 5, time = 1) // default is 10
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class HashFunctionsBenchmark {

    private static final Random RAND = new Random();
    private static int BIG_PRIME = 10_000_687;
    private static final int A = 1 + RAND.nextInt(BIG_PRIME - 1);
    private static final int B = RAND.nextInt(BIG_PRIME);
    private static final int M = 512;

    private int[] arr1;
    private int[] arr2;
    private int[] arr3;

    private UniversalHash hashNormal;
    private UniversalHashOptimized hashOptimized;
    private UniversalHashSuperFast hashSuperFast;

    @Setup(Level.Invocation)
    public void setUp() {
        arr1 = randomArray(100_000);
        arr2 = Arrays.copyOf(arr1, arr1.length);
        arr3 = Arrays.copyOf(arr1, arr1.length);
        hashNormal = new UniversalHash(A, B, BIG_PRIME, M);
        hashOptimized = new UniversalHashOptimized(A, B, BIG_PRIME, M);
        hashSuperFast = new UniversalHashSuperFast(M);
    }

    private int[] randomArray(int length) {
        int[] arr = new int[length];
        for (int i = 0; i < arr.length; ++i) {
            arr[i] = RAND.nextInt();
        }
        return arr;
    }

    @TearDown(Level.Invocation)
    public void tearDown() {
        arr1 = null;
        arr2 = null;
        arr3 = null;
        hashNormal = null;
        hashOptimized = null;
    }

    @Benchmark
    public void hashNormal(Blackhole bh) {
        for (int val : arr1) {
            bh.consume(hashNormal.hash(val));
        }
    }

    @Benchmark
    public void hashOptimized(Blackhole bh) {
        for (int val : arr2) {
            bh.consume(hashOptimized.hash(val));
        }
    }

    @Benchmark
    public void hashSuperFast(Blackhole bh) {
        for (int val : arr3) {
            bh.consume(hashSuperFast.hash(val));
        }
    }


    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
            .include(HashFunctionsBenchmark.class.getSimpleName())
//                .threads(Runtime.getRuntime().availableProcessors())
            .build();

        new Runner(opt).run();
    }

    static final class UniversalHash {

        private final int a;
        private final int b;
        private final int prime;
        private final int m;

        public UniversalHash(int a, int b, int prime, int m) {
            this.a = a;
            this.b = b;
            this.prime = prime;
            this.m = m;
        }

        int hash(int value) {
            int hashValue = ((a * value + b) % prime) % m;
            return hashValue >= 0 ? hashValue : -hashValue;
        }

    }

    static final class UniversalHashOptimized {

        private final int a;
        private final int b;
        private final int prime;
        private final int mod;

        public UniversalHashOptimized(int a, int b, int prime, int m) {
            this.a = 1 + RAND.nextInt(BIG_PRIME - 1);
            this.b = RAND.nextInt(BIG_PRIME);
            this.prime = prime;
            this.mod = m - 1;
        }

        int hash(int value) {
            return ((a * value + b) % prime) & mod;
        }

    }

    static final class UniversalHashSuperFast {

        private static final int INT_BITS = Integer.SIZE;
        private final int a;
        private final int mBits;

        public UniversalHashSuperFast(int m) {
            this.a = randomOddNumber();
            this.mBits = log2(m);
        }

        private int randomOddNumber() {
            return RAND.nextInt() | 1;
        }

        int hash(int value) {
            return (a * value) >>> (INT_BITS - mBits);
        }

        int log2(int value) {
            return (int) (Math.log(value) / Math.log(2.0));
        }
    }

}
