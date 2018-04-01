package benchmark;

import com.max.algs.util.ArrayUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;


@State(Scope.Group)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(5)
public class MemoryAccessPatternsBenchmark {

    private static final int PAGE_SIZE = 4 * 1024; // 4KB

    private static final int ARR_SIZE = PAGE_SIZE * 300;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(MemoryAccessPatternsBenchmark.class.getSimpleName())
                .threads(Runtime.getRuntime().availableProcessors())
                .build();

        new Runner(opt).run();
    }

    @Benchmark
    @Group("sequentialAccess")
    @GroupThreads(2)
    public void sequentialAccess(ArrPerThread state) {
        byte[] data = state.arr1;

        for (int i = 0; i < data.length; ++i) {
            byte temp = data[i];
        }
    }

    @Benchmark
    @Group("randomWithinPage")
    @GroupThreads(2)
    public void randomWithinPage(ArrPerThread state) {
        byte[] data = state.arr2;

        for (int from = 0; from < data.length; ) {

            for (int index : state.indexesWithinPage) {
                byte temp = data[from + index];
            }

            from += state.indexesWithinPage.length;
        }
    }

    @Benchmark
    @Group("randomAccess")
    @GroupThreads(2)
    public void randomAccess(ArrPerThread state) {
        byte[] data = state.arr3;

        for (int randIndex : state.randomIndexes) {
            byte temp = data[randIndex];
        }
    }

    @State(Scope.Thread)
    public static class ArrPerThread {

        public byte[] arr1;
        public byte[] arr2;
        public byte[] arr3;

        public int[] indexesWithinPage;
        public int[] randomIndexes;

        @Setup(Level.Invocation)
        public void setUp() {
            arr1 = ArrayUtils.generateRandomByteArray(ARR_SIZE);
            arr2 = ArrayUtils.generateRandomByteArray(ARR_SIZE);
            arr3 = ArrayUtils.generateRandomByteArray(ARR_SIZE);

            indexesWithinPage = new int[PAGE_SIZE];

            for (int i = 0; i < indexesWithinPage.length; ++i) {
                indexesWithinPage[i] = i;
            }

            ArrayUtils.shuffle(indexesWithinPage);

            randomIndexes = new int[arr1.length];

            for (int i = 0; i < randomIndexes.length; ++i) {
                randomIndexes[i] = i;
            }

            ArrayUtils.shuffle(randomIndexes);
        }

        @TearDown(Level.Invocation)
        public void tearDown() {
            arr1 = null;
            arr2 = null;
            arr3 = null;
            indexesWithinPage = null;
            randomIndexes = null;
        }
    }

}
