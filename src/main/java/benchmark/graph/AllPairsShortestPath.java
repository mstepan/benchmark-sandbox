package benchmark.graph;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class AllPairsShortestPath {

    private AllPairsShortestPath() {
        throw new AssertionError("Can't instantiate utility only class");
    }

    /**
     * All pairs shortest path using Floyd-Warshall.
     * <p>
     * time: O(V^3)
     * space: O(V^2)
     */
    static int calculateShortestPath(String src, String dest, Map<String, List<EdgeWithWeight>> adjList) {

        final int vertexesCount = adjList.size();

        Map<String, Integer> vertexToIndexMap = createVertexToIndexMap(adjList);

        int[][] allPaths = initialPathsArray(adjList, vertexToIndexMap);

        for (int m = 0; m < vertexesCount; ++m) {

            for (int i = 0; i < vertexesCount; ++i) {
                for (int j = 0; j < vertexesCount; ++j) {

                    if (allPaths[i][m] != Integer.MAX_VALUE && allPaths[m][j] != Integer.MAX_VALUE) {
                        int newPath = allPaths[i][m] + allPaths[m][j];

                        if (newPath < allPaths[i][j]) {
                            allPaths[i][j] = newPath;
                        }
                    }
                }
            }
        }

        return allPaths[vertexToIndexMap.get(src)][vertexToIndexMap.get(dest)];
    }

    private static int[][] initialPathsArray(Map<String, List<EdgeWithWeight>> adjList,
                                             Map<String, Integer> vertexToIndexMap) {
        int[][] allPaths = new int[vertexToIndexMap.size()][vertexToIndexMap.size()];

        for (int i = 0; i < allPaths.length; ++i) {
            Arrays.fill(allPaths[i], Integer.MAX_VALUE);
            allPaths[i][i] = 0;
        }

        for (Map.Entry<String, List<EdgeWithWeight>> entry : adjList.entrySet()) {
            for (EdgeWithWeight singleEdge : entry.getValue()) {

                int srcIndex = vertexToIndexMap.get(entry.getKey());
                int destIndex = vertexToIndexMap.get(singleEdge.dest);

                allPaths[srcIndex][destIndex] = singleEdge.weight;
            }
        }


        return allPaths;
    }

    private static Map<String, Integer> createVertexToIndexMap(Map<String, List<EdgeWithWeight>> adjList) {

        Map<String, Integer> vertexToIndexMap = new HashMap<>();

        int index = 0;

        for (String ver : adjList.keySet()) {
            vertexToIndexMap.put(ver, index);
            ++index;
        }

        return vertexToIndexMap;
    }

}
