package benchmark.algorithms;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import com.max.algs.util.ArrayUtils;
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
 * Micro benchmark for sets intersection count algorithms.
 *
 * Run complete. Total time: 00:05:50
 *
 * Benchmark                                            Mode  Cnt     Score      Error  Units
 * SetsIntersectionAlgorithmsBenchmark.bsTree           avgt   10  9145.049 ±  837.922  ms/op
 * SetsIntersectionAlgorithmsBenchmark.hashTable        avgt   10  1806.438 ± 1264.949  ms/op
 * SetsIntersectionAlgorithmsBenchmark.sortAndTraverse  avgt   10   851.247 ±   25.167  ms/op
 *
 */
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(2)
@State(Scope.Thread)
public class SetsIntersectionAlgorithmsBenchmark {

    public int[] arr1;
    public int[] arr2;

    public int[] arr3;
    public int[] arr4;

    public int[] arr5;
    public int[] arr6;

    @Setup(Level.Invocation)
    public void setUp() {
        arr1 = ArrayUtils.generateRandomArray(5_000_000);
        arr2 = ArrayUtils.generateRandomArray(5_000_000);

        arr3 = Arrays.copyOf(arr1, arr1.length);
        arr4 = Arrays.copyOf(arr2, arr2.length);

        arr5 = Arrays.copyOf(arr1, arr1.length);
        arr6 = Arrays.copyOf(arr2, arr2.length);
    }

    @TearDown(Level.Invocation)
    public void tearDown() {
        arr1 = null;
        arr2 = null;

        arr3 = null;
        arr4 = null;

        arr5 = null;
        arr6 = null;
    }

    @Benchmark
    public void bsTree(Blackhole bh) {

        Set<Integer> set = new TreeSet<>();

        for (int value : arr1) {
            set.add(value);
        }

        int cnt = 0;

        for (int value : arr2) {
            if (set.contains(value)) {
                ++cnt;
            }
        }

        bh.consume(cnt);
    }

    @Benchmark
    public void hashTable(Blackhole bh) {
        Set<Integer> set = new HashSet<>();

        for (int value : arr3) {
            set.add(value);
        }

        int cnt = 0;

        for (int value : arr4) {
            if (set.contains(value)) {
                ++cnt;
            }
        }

        bh.consume(cnt);
    }

    @Benchmark
    public void sortAndTraverse(Blackhole bh) {

        Arrays.sort(arr5);
        Arrays.sort(arr6);
        int i = 0;
        int j = 0;
        int cnt = 0;

        while (i < arr5.length && j < arr6.length) {

            if (arr5[i] == arr6[j]) {
                ++i;
                ++j;
                ++cnt;
            }
            else if (arr5[i] < arr6[j]) {
                ++i;
            }
            else {
                ++j;
            }
        }

        bh.consume(cnt);

    }


    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(SetsIntersectionAlgorithmsBenchmark.class.getSimpleName())
//                .threads(Runtime.getRuntime().availableProcessors())
                .build();

        new Runner(opt).run();
    }

}
