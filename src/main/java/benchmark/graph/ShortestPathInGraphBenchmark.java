package benchmark.graph;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Micro benchmark for shortest path in DAG using topological sorting and Dijkstra algorithms.
 */
@State(Scope.Group)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.MILLISECONDS)
@Fork(5)
public class ShortestPathInGraphBenchmark {

    private static final int GRAPH_VERTEXES_COUNT = 100;

    private static final Random RAND = ThreadLocalRandom.current();

    @Benchmark
    @Group("topologicalSorting")
    @GroupThreads(4)
    public void topologicalSorting(RandomDag state) {
        state.graph.shortestPath(state.src, state.dest);
    }

    @Benchmark
    @Group("dijkstra")
    @GroupThreads(4)
    public void dijkstra(RandomDag state) {
        state.graph.shortestPathDijkstra(state.src, state.dest);
    }

    @Benchmark
    @Group("floydWarshall")
    @GroupThreads(4)
    public void floydWarshall(RandomDag state) {
        state.graph.shortestPathAllPairs(state.src, state.dest);
    }

    @State(Scope.Thread)
    public static class RandomDag {

        public DirectAcyclicGraph graph;
        public String src;
        public String dest;

        @Setup(Level.Invocation)
        public void setUp() {
            String[] labels = generateVertexesLabels(GRAPH_VERTEXES_COUNT);
            this.graph = DagGenerator.generate(labels);
            this.src = labels[0];
            this.dest = labels[labels.length - 1];
        }

        @TearDown(Level.Invocation)
        public void tearDown() {
            this.graph = null;
            this.src = null;
            this.dest = null;
        }
    }

    static String[] generateVertexesLabels(int length) {
        String[] labels = new String[length];

        final char firstCh = 'A';
        final char lastCh = 'Z';

        char baseLabel = firstCh;

        for (int i = 0; i < labels.length; ++i) {
            labels[i] = String.valueOf(baseLabel) + "-" + i;
            baseLabel = (baseLabel == lastCh) ? firstCh : (char) (baseLabel + 1);
        }

        randomPermutation(labels);
        return labels;
    }

    static void randomPermutation(String[] arr) {
        for (int i = 0; i < arr.length - 1; ++i) {
            swap(arr, i, i + RAND.nextInt(arr.length - i));
        }
    }

    private static void swap(String[] arr, int from, int to) {
        assert arr != null;
        assert (from >= 0 && from < arr.length) && (to >= 0 && to < arr.length);

        String temp = arr[from];
        arr[from] = arr[to];
        arr[to] = temp;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(ShortestPathInGraphBenchmark.class.getSimpleName())
                .threads(Runtime.getRuntime().availableProcessors())
                .build();

        new Runner(opt).run();
    }


}
