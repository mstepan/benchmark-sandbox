package benchmark.ds;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * ArrayList vs LinkedList traversal time.
 */
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@State(Scope.Benchmark)
@Fork(2)
public class ListFaceOffBenchmark {

    @Param({"1", "2", "4", "8", "16", "32", "64", "128", "256", "512", "1024", "2048", "4096", "8192",
            "16384", "32768", "65536", "131072", "262144", "524288", "1048576"})
    private int size;

    private List<String> arrayList;
    private List<String> linkedList;

    @Setup(Level.Trial)
    public void setUp() {
        this.arrayList = new ArrayList<>(this.size);
        this.linkedList = new LinkedList<>();

        ThreadLocalRandom rand = ThreadLocalRandom.current();

        for (int i = 0; i < this.size; ++i) {
            String value = String.valueOf(rand.nextInt());

            // pre compute hashCode for String
            value.hashCode();

            this.arrayList.add(value);
            this.linkedList.add(value);
        }
    }


    @Benchmark
    public int arrayList() {

        int hash = 0;

        for (String val : this.arrayList) {
            hash |= val.hashCode();
        }
        return hash;
    }

    @Benchmark
    public int linkedList() {
        int hash = 0;

        for (String val : this.linkedList) {
            hash |= val.hashCode();
        }
        return hash;
    }


    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(ListFaceOffBenchmark.class.getSimpleName())
                .threads(Runtime.getRuntime().availableProcessors())
                .build();

        new Runner(opt).run();
    }

}
