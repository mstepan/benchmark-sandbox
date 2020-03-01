package benchmark.ds;

import com.max.algs.ds.tree.BSTree;
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
public class InOrderTraversationBenchmark {

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(InOrderTraversationBenchmark.class.getSimpleName())
                .threads(Runtime.getRuntime().availableProcessors())
                .build();

        new Runner(opt).run();
    }

    @Benchmark
    @Group("inOrder")
    @GroupThreads(4)
    public void inOrder(TreePerThread state) {
        state.tree1.inOrderStr();
    }

    @Benchmark
    @Group("inOrderMorris")
    @GroupThreads(4)
    public void inOrderMorris(TreePerThread state) {
        state.tree2.inOrderMorrisStr();
    }

    @State(Scope.Thread)
    public static class TreePerThread {

        private static final int TREE_SIZE = 100_000;

        BSTree<Integer> tree1;
        BSTree<Integer> tree2;

        @Setup(Level.Invocation)
        public void setUp() {
            tree1 = BSTree.createRandomTree(Integer.MAX_VALUE, TREE_SIZE);
            tree2 = BSTree.createRandomTree(Integer.MAX_VALUE, TREE_SIZE);
        }

        @TearDown(Level.Invocation)
        public void tearDown() {
            tree1 = null;
            tree2 = null;
        }
    }

}
