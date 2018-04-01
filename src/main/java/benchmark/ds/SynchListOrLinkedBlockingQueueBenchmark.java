package benchmark.ds;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@State(Scope.Group)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 3, time = 3, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 3, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class SynchListOrLinkedBlockingQueueBenchmark {


    private final Random rand = ThreadLocalRandom.current();

    private final List<Integer> list = Collections.synchronizedList(new LinkedList<>());

    private final Queue<Integer> queue = new LinkedBlockingQueue<>();

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(SynchListOrLinkedBlockingQueueBenchmark.class.getSimpleName())
                .threads(Runtime.getRuntime().availableProcessors())
                .build();

        new Runner(opt).run();
    }

    @Benchmark
    @Group("synchronised_list")
    @GroupThreads(4)
    public void addToList() {

        boolean removeElement = rand.nextInt(10) == 0;

        if (removeElement) {
            list.remove(0);
        }
        else {
            list.add(rand.nextInt());
        }
    }

    @Benchmark
    @Group("linked_blocking_queue")
    @GroupThreads(4)
    public void addToQueue() {

        boolean removeElement = rand.nextInt(10) == 0;

        if (removeElement) {
            queue.poll();
        }
        else {
            queue.add(rand.nextInt());
        }

    }

}
