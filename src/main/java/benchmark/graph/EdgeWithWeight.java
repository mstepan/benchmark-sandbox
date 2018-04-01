package benchmark.graph;

import java.util.Objects;

final class EdgeWithWeight {

    final String dest;
    final int weight;

    EdgeWithWeight(String dest, int weight) {
        this.dest = dest;
        this.weight = weight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EdgeWithWeight that = (EdgeWithWeight) o;
        return weight == that.weight &&
                Objects.equals(dest, that.dest);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dest, weight);
    }

    @Override
    public String toString() {
        return dest + ": " + weight;
    }
}
