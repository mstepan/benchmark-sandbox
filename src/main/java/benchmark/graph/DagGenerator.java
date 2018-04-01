package benchmark.graph;

import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static com.google.common.base.Preconditions.checkNotNull;

final class DagGenerator {

    private static final Random RAND = ThreadLocalRandom.current();

    private DagGenerator() {
        throw new AssertionError("Utility only class");
    }

    static DirectAcyclicGraph generate(String[] vertexes) {
        checkNotNull(vertexes, "null 'vertexes' passed");

        DirectAcyclicGraph graph = new DirectAcyclicGraph();

        for (String singleVertex : vertexes) {
            graph.addVertex(singleVertex);
        }

        // add each edge with probability 5%
        for (int i = 0; i < vertexes.length - 1; ++i) {
            for (int j = i + 1; j < vertexes.length; ++j) {
                if (RAND.nextInt(100) < 5) {
                    graph.addEdge(vertexes[i], vertexes[j], RAND.nextInt(100) + RAND.nextInt(100));
                }
            }
        }

        Set<String> baseVertexes = graph.getSourceVertexes();

        String prev = vertexes[0];

        for (int i = 1; i < vertexes.length; ++i) {

            String cur = vertexes[i];

            if (baseVertexes.contains(cur)) {
                graph.addEdge(prev, cur, 1 + RAND.nextInt(15));
                prev = cur;
            }
        }
        return graph;
    }

}
