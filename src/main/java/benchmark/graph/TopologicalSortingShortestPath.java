package benchmark.graph;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class TopologicalSortingShortestPath {

    private TopologicalSortingShortestPath() {
        throw new AssertionError("Can't instantiate utility only class");
    }

    /**
     * Shortest path in DAG using topological sorting and left-to-right order of calculation.
     * <p>
     * time: O(V + E)
     * space: O(V + E)
     */
    static int shortestPath(String src, String dest, Map<String, List<EdgeWithWeight>> adjList) {

        assert src != null && dest != null && !src.equals(dest);

        // check both vertexes present
        if (!(adjList.containsKey(src) && adjList.containsKey(dest))) {
            return -1;
        }

        Map<String, Integer> vertexDegree = calculateVertexDegrees(adjList);

        Deque<String> queue = new ArrayDeque<>();

        for (Map.Entry<String, Integer> entry : vertexDegree.entrySet()) {
            if (entry.getValue() == 0) {
                queue.add(entry.getKey());
            }
        }

        assert !queue.isEmpty();

        Map<String, Integer> shortestPaths = new HashMap<>();
        boolean startPathFinding = false;

        while (true) {

            assert !queue.isEmpty();

            String baseVertex = queue.poll();

            // start path tracking
            if (baseVertex.equals(src)) {
                startPathFinding = true;
                shortestPaths.put(baseVertex, 0);
            }
            // we are done with our shortest path search
            else if (baseVertex.equals(dest)) {
                assert startPathFinding;
                return shortestPaths.get(baseVertex);
            }

            for (EdgeWithWeight edge : adjList.get(baseVertex)) {

                if (startPathFinding) {

                    int curPathWeight = shortestPaths.computeIfAbsent(edge.dest, key -> Integer.MAX_VALUE);
                    int newPathWeight = shortestPaths.get(baseVertex) + edge.weight;

                    if (newPathWeight < curPathWeight) {
                        shortestPaths.put(edge.dest, newPathWeight);
                    }
                }

                int curVertexDegree = vertexDegree.compute(edge.dest, (key, val) -> val - 1);

                if (curVertexDegree == 0) {
                    queue.add(edge.dest);
                }
            }
        }
    }

    private static Map<String, Integer> calculateVertexDegrees(Map<String, List<EdgeWithWeight>> adjList) {
        Map<String, Integer> vertexDegree = new HashMap<>();

        for (Map.Entry<String, List<EdgeWithWeight>> entry : adjList.entrySet()) {

            vertexDegree.putIfAbsent(entry.getKey(), 0);

            for (EdgeWithWeight edge : entry.getValue()) {
                vertexDegree.compute(edge.dest, (key, val) -> val == null ? 1 : val + 1);
            }
        }

        return vertexDegree;
    }


}
