package benchmark.graph;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

final class DijkstraShortestPath {

    private DijkstraShortestPath() {
        throw new AssertionError("Can't instantiate utility only class");
    }

    /**
     * Shortest path using Dijkstra algorithm.
     * <p>
     * time: O((V+E)*lgV)
     * space: O(V)
     */
    static int shortestPath(String src, String dest, Map<String, List<EdgeWithWeight>> adjList) {
        // check both vertexes present
        if (!(adjList.containsKey(src) && adjList.containsKey(dest))) {
            return -1;
        }

        MinHeap heap = new MinHeap();
        heap.add(src, 0);

        while (true) {

            assert !heap.isEmpty();

            VertexAndWeight cur = heap.poll();

            for (EdgeWithWeight edge : adjList.get(cur.vertex)) {
                String otherVertex = edge.dest;

                Integer otherWeight = heap.getWeight(otherVertex);

                if (otherWeight == null) {

                    heap.add(otherVertex, Integer.MAX_VALUE);
                    otherWeight = Integer.MAX_VALUE;
                }

                if (cur.weight + edge.weight < otherWeight) {
                    heap.changeWeight(otherVertex, cur.weight + edge.weight);
                }
            }

            if (cur.vertex.equals(dest)) {
                return cur.weight;
            }
        }
    }


    private static class MinHeap {

        private VertexAndWeight[] heap = new VertexAndWeight[8];
        private final Map<String, Integer> vertexLocation = new HashMap<>();

        private int last;

        void add(String vertex, int weight) {
            if (last == heap.length) {
                heap = Arrays.copyOf(heap, heap.length * 2);
            }

            heap[last] = new VertexAndWeight(vertex, weight);
            vertexLocation.put(vertex, last);

            ++last;
            fixUp(last - 1);
        }

        VertexAndWeight poll() {
            VertexAndWeight res = heap[0];

            if( last == 1 ){
                heap[0] = null;
            }
            else {
                heap[0] = heap[last - 1];
            }

            --last;
            vertexLocation.remove(res.vertex);

            if( last != 0 ) {
                vertexLocation.put(heap[0].vertex, 0);
                fixDown(0);
            }

            return res;
        }

        boolean isEmpty() {
            return last == 0;
        }

        Integer getWeight(String vertex) {

            Integer index = vertexLocation.get(vertex);

            if (index == null) {
                return null;
            }

            assert heap[index] != null;

            return heap[index].weight;
        }

        void changeWeight(String vertex, int weight) {

            assert vertexLocation.containsKey(vertex);

            int index = vertexLocation.get(vertex);

            int prevWeight = heap[index].weight;

            if (weight == prevWeight) {
                return;
            }

            heap[index] = new VertexAndWeight(vertex, weight);

            if (weight < prevWeight) {
                fixUp(index);
            }
            else {
                fixDown(index);
            }
        }

        private void fixUp(int index) {

            int cur = index;

            while (cur != 0) {
                int parent = (cur - 1) / 2;

                if (heap[parent].weight > heap[cur].weight) {
                    swap(parent, cur);
                    cur = parent;
                }
                else {
                    break;
                }
            }
        }

        private void fixDown(int index) {

            int cur = index;

            while (true) {

                int minIndex = cur;

                int left = 2 * cur + 1;
                int right = 2 * cur + 2;

                if (left < last && heap[left].weight < heap[minIndex].weight) {
                    minIndex = left;
                }

                if (right < last && heap[right].weight < heap[minIndex].weight) {
                    minIndex = right;
                }

                if (minIndex == cur) {
                    break;
                }

                swap(minIndex, cur);
                cur = minIndex;
            }
        }

        private void swap(int from, int to) {

            VertexAndWeight temp = heap[from];

            heap[from] = heap[to];
            heap[to] = temp;

            vertexLocation.put(heap[from].vertex, from);
            vertexLocation.put(heap[to].vertex, to);
        }

    }

    private static final class VertexAndWeight {

        final String vertex;
        final int weight;

        VertexAndWeight(String vertex, int weight) {
            this.vertex = vertex;
            this.weight = weight;
        }

        @Override
        public String toString() {
            return vertex + " (" + weight + ")";
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            VertexAndWeight that = (VertexAndWeight) obj;
            return weight == that.weight &&
                    Objects.equals(vertex, that.vertex);
        }

        @Override
        public int hashCode() {
            return Objects.hash(vertex, weight);
        }
    }
}
