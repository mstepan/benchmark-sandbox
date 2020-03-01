package benchmark.concurrency;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Group;
import org.openjdk.jmh.annotations.GroupThreads;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@State(Scope.Group)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(5)
public class CollatzConjectureBenchmark {

    private static final int ELEMS_COUNT = 1_000;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(CollatzConjectureBenchmark.class.getSimpleName())
                .threads(Runtime.getRuntime().availableProcessors())
                .build();

        new Runner(opt).run();
    }

    @Benchmark
    @Group("collatzHashNoResizing")
    @GroupThreads(2)
    public void collatzHashNoResizing() {
        collatzHashNoResizing(ELEMS_COUNT);
    }

    private boolean collatzHashNoResizing(int n) {

        Set<Integer> solvable = new HashSet<>((int) (n / 0.75 + 1));
        solvable.add(1);
        solvable.add(2);

        for (int i = 3; i <= n; ++i) {

            long curValue = i;

            List<Integer> partialSolutions = new ArrayList<>();

            while (true) {

                if (curValue <= n) {
                    partialSolutions.add((int) curValue);

                    if (solvable.contains((int) curValue)) {
                        for (int singleSol : partialSolutions) {
                            solvable.add(singleSol);
                        }

                        break;
                    }
                }

                // even case
                if ((curValue & 1) == 0) {
                    curValue = curValue >> 1L;
                }
                else {
                    curValue = ((curValue << 1) | 1) + curValue;
                }
            }
        }

        return true;
    }

    @Benchmark
    @Group("collatzHashSet")
    @GroupThreads(2)
    public void collatzHashSet() {
        collatzHashSet(ELEMS_COUNT);
    }

    private boolean collatzHashSet(int n) {

        Set<Integer> solvable = new HashSet<>();
        solvable.add(1);
        solvable.add(2);

        for (int i = 3; i <= n; ++i) {

            long curValue = i;

            List<Integer> partialSolutions = new ArrayList<>();

            while (true) {

                if (curValue <= n) {
                    partialSolutions.add((int) curValue);

                    if (solvable.contains((int) curValue)) {
                        for (int singleSol : partialSolutions) {
                            solvable.add(singleSol);
                        }

                        break;
                    }
                }

                // even case
                if ((curValue & 1) == 0) {
                    curValue = curValue >> 1L;
                }
                else {
                    curValue = ((curValue << 1) | 1) + curValue;
                }
            }
        }

        return true;
    }

    @Benchmark
    @Group("isCollatzHoldsSequential")
    @GroupThreads(2)
    public void isCollatzHoldsSequential() {
        isCollatzHoldsSequential(ELEMS_COUNT);
    }

    static boolean isCollatzHoldsSequential(int lastValue) {
        for (int i = 3; i <= lastValue; i += 2) {
            if (!isCollatzValidForValue(i, i - 1)) {
                return false;
            }
        }

        return true;
    }


    @Benchmark
    @Group("isCollatzHoldsStream")
    @GroupThreads(2)
    public void isCollatzHoldsStream() {
        isCollatzHoldsStream(ELEMS_COUNT);
    }

    private static boolean isCollatzHoldsStream(int lastValue) {
        return IntStream.rangeClosed(3, lastValue).
                filter(value -> !isCollatzValidForValue(value, value - 1)).count() == 0L;
    }

    @Benchmark
    @Group("isCollatzHoldsParallelStream")
    @GroupThreads(2)
    public void isCollatzHoldsParallelStream() {
        isCollatzHoldsParallelStream(ELEMS_COUNT);
    }

    private static boolean isCollatzHoldsParallelStream(int lastValue) {
        return IntStream.rangeClosed(3, lastValue).parallel().
                filter(value -> !isCollatzValidForValue(value, value - 1)).count() == 0L;
    }


    @Benchmark
    @Group("isCollatzHoldsForkJoinPool")
    @GroupThreads(2)
    public void isCollatzHoldsForkJoinPool() throws Exception {
        isCollatzHoldsForkJoinPool(ELEMS_COUNT);
    }

    private static final class CollatzTask extends RecursiveTask<Boolean> {

        private final int from;
        private final int to;

        public CollatzTask(int from, int to) {
            this.from = from;
            this.to = to;
        }

        @Override
        protected Boolean compute() {

            int elementsCount = to - from + 1;

            if (elementsCount <= 10) {
                for (int i = from; i <= to; ++i) {
                    if (!isCollatzValidForValue(i, i - 1)) {
                        return false;
                    }
                }

                return true;
            }
            int mid = from + (to - from) / 2;

            CollatzTask leftPart = new CollatzTask(from, mid);
            leftPart.fork();

            CollatzTask rightPart = new CollatzTask(mid + 1, to);

            return rightPart.compute() && leftPart.join();
        }
    }

    private static boolean isCollatzHoldsForkJoinPool(int lastValue) throws InterruptedException, ExecutionException {
        ForkJoinPool pool = new ForkJoinPool();

        try {
            Future<Boolean> resultFuture = pool.submit(new CollatzTask(3, lastValue));
            return resultFuture.get();
        }
        finally {
            pool.shutdown();
        }
    }

    @Benchmark
    @Group("isCollatzHoldsPool")
    @GroupThreads(2)
    public void isCollatzHoldsPool() {
        isCollatzHoldsPool(ELEMS_COUNT);
    }

    private static boolean isCollatzHoldsPool(int lastValue) {

        int threadsCount = Runtime.getRuntime().availableProcessors() - 1;

        ExecutorService pool = Executors.newFixedThreadPool(threadsCount);

        Future<Boolean>[] results = new Future[threadsCount];

        try {
            for (int i = 0, offset = threadsCount; i < threadsCount; ++i) {

                final int startElement = i + 3;

                results[i] = pool.submit(() -> {

                    for (int value = startElement; value <= lastValue; value += offset) {

                        if (!isCollatzValidForValue(value, value - 1)) {
                            return false;
                        }
                    }

                    return true;
                });
            }

            for (Future<Boolean> singleRes : results) {
                try {
                    if (!singleRes.get()) {
                        return false;
                    }
                }
                catch (InterruptedException | ExecutionException ex) {
                    return false;
                }
            }

            return true;
        }
        finally {
            pool.shutdown();
        }

    }

    private static final int THRESHOLD = 10_000;

    /**
     * Check if Collatz conjecture holds for specified value.
     *
     * @param initialValue - value to check for Collatz conjecture
     * @param lastChecked  - last checked for Collatz conjecture number so far
     */
    private static boolean isCollatzValidForValue(int initialValue, int lastChecked) {

        Set<Long> curSeq = new HashSet<>();

        long value = initialValue;

        for (int it = 0; it < THRESHOLD && value != 1; ++it) {

            // cycle detected
            if (curSeq.contains(value)) {
                return false;
            }

            if (value <= lastChecked) {
                return true;
            }

            curSeq.add(value);

            // even case
            if ((value & 1) == 0) {
                value = value / 2;
            }
            // odd case
            else {
                value = 3 * value + 1;

                if (value < 0L) {
                    throw new IllegalStateException("Overflow detected: " + value);
                }
            }
        }

        // took more than THRESHOLD iterations
        if (value != 1) {
            return false;
        }

        return true;
    }


}
