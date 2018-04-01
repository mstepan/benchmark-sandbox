package benchmark.graph;

import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;

final class DirectAcyclicGraph {

    private final Map<String, List<EdgeWithWeight>> adjList = new HashMap<>();

    boolean isConnected() {

        Set<String> marked = new HashSet<>();

        Deque<String> stack = new ArrayDeque<>();

        String firstVertex = getSourceVertexes().iterator().next();
        marked.add(firstVertex);

        stack.push(firstVertex);

        while (!stack.isEmpty()) {
            String vertex = stack.pop();

            for (EdgeWithWeight edge : getEdges(vertex)) {
                if (!marked.contains(edge.dest)) {
                    stack.push(edge.dest);
                    marked.add(edge.dest);
                }
            }
        }

        return marked.size() == adjList.size();
    }

    int shortestPathAllPairs(String src, String dest) {
        return AllPairsShortestPath.calculateShortestPath(src, dest, adjList);
    }

    int shortestPathDijkstra(String src, String dest) {
        return DijkstraShortestPath.shortestPath(src, dest, adjList);
    }

    int shortestPath(String src, String dest) {
        return TopologicalSortingShortestPath.shortestPath(src, dest, adjList);
    }

    Set<String> getSourceVertexes() {

        Set<String> candidates = new HashSet<>(adjList.keySet());

        for (List<EdgeWithWeight> allEdges : adjList.values()) {
            for (EdgeWithWeight edge : allEdges) {
                candidates.remove(edge.dest);
            }
        }

        checkArgument(!candidates.isEmpty(), "No source vertex (with 0 in degree) in DAG.");

        return Collections.unmodifiableSet(candidates);
    }

    private List<EdgeWithWeight> getEdges(String vertex) {
        assert adjList.containsKey(vertex);
        return adjList.get(vertex);
    }

    void addVertex(String vertex) {
        assert !adjList.containsKey(vertex);

        adjList.put(vertex, new ArrayList<>());
    }

    void addEdge(String src, String dest, int weight) {
        adjList.get(src).add(new EdgeWithWeight(dest, weight));
    }

}
