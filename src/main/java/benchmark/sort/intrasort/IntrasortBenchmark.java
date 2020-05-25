package benchmark.sort.intrasort;

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

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Micro benchmark for intrasort algorithm with different insertion sort techniques for small subarrays.
 *
 * Benchmark                  Mode  Cnt      Score      Error  Units
 * IntrasortBenchmark.fast    avgt   10  78_958.890 ± 1561.604  ns/op
 * IntrasortBenchmark.simple  avgt   10  67_209.767 ± 1154.711  ns/op
 */
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(2)
@State(Scope.Thread)
public class IntrasortBenchmark {

    private static final Random RAND = new Random();

    private static final Intrasort SIMPLE = new Intrasort(new SimpleInsertionSort());
    private static final Intrasort FAST = new Intrasort(new FastInsertionSort());

    public int[] arr1;
    public int[] arr2;

    @Setup(Level.Invocation)
    public void setUp() {
        arr1 = createRandomArray(1000 + RAND.nextInt(1000));
        arr2 = Arrays.copyOf(arr1, arr1.length);
    }

    private static int[] createRandomArray(int length) {
        return Arrays.stream(new int[length]).
                map(notUsed -> RAND.nextInt()).
                toArray();
    }

    @TearDown(Level.Invocation)
    public void tearDown() {
        arr1 = null;
        arr2 = null;
    }

    @Benchmark
    public void simple(Blackhole bh) {
        SIMPLE.intrasort(arr1);
        bh.consume(arr1);
    }

    @Benchmark
    public void fast(Blackhole bh) {
        FAST.intrasort(arr2);
        bh.consume(arr2);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(IntrasortBenchmark.class.getSimpleName())
//                .threads(Runtime.getRuntime().availableProcessors())
                .build();

        new Runner(opt).run();
    }
}
