package benchmark;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
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
 * Traverse linked list with good and bad data localities.
 *
 * # Run complete. Total time: 00:00:46
 *
 * Benchmark                                           Mode  Cnt   Score   Error  Units
 * TraverseLinkedListsPackedAndNot.traverseBadPacked   avgt   10  27.972 ± 2.006  ms/op
 * TraverseLinkedListsPackedAndNot.traverseGoodPacked  avgt   10   6.930 ± 0.636  ms/op
 *
 */
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(2)
@State(Scope.Thread)
public class TraverseLinkedListsPackedAndNot {

    List<Integer> goodPacked;
    List<Integer> badPacked;

    private static final int LIST_LENGTH = 1_000_000;
    private static final Random RAND = ThreadLocalRandom.current();

    @Setup(Level.Invocation)
    public void setUp() {

        goodPacked = new LinkedList<>();
        badPacked = new LinkedList<>();

        List<Integer> tempArr = new ArrayList<>();
        for (int i = 0; i < LIST_LENGTH; ++i) {
            goodPacked.add(RAND.nextInt());
            tempArr.add(RAND.nextInt());
        }

        for (int i = 0; i < tempArr.size(); ++i) {
            badPacked.add(tempArr.get(RAND.nextInt(tempArr.size())));
        }
    }

    @TearDown(Level.Invocation)
    public void tearDown() {
        goodPacked = null;
        badPacked = null;
    }

    @Benchmark
    public void traverseGoodPacked(Blackhole bh) {
        for (Integer value : goodPacked) {
            bh.consume(value);
        }
    }

    @Benchmark
    public void traverseBadPacked(Blackhole bh) {
        for (Integer value : badPacked) {
            bh.consume(value);
        }
    }


    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(TraverseLinkedListsPackedAndNot.class.getSimpleName())
//                .threads(Runtime.getRuntime().availableProcessors())
                .build();

        new Runner(opt).run();
    }

}
