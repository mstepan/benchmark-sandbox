package benchmark;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.*;
import java.util.concurrent.TimeUnit;

@State(Scope.Group)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(5)
public class CollatzConjectureBenchmark {

    private static final int ELEMS_COUNT = 1_000_000;

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

    @Benchmark
    @Group("collatzHashSet")
    @GroupThreads(2)
    public void collatzHashSet() {
        collatzHashSet(ELEMS_COUNT);
    }

    private boolean collatzBitSet(int n) {

        BitSet solvable = new BitSet(n + 1);
        solvable.set(1, true);
        solvable.set(2, true);

        for (int i = 3; i <= n; ++i) {

            long curValue = i;

            List<Integer> partialSolutions = new ArrayList<>();

            while (true) {

                if (curValue <= n) {
                    partialSolutions.add((int) curValue);

                    if (solvable.get((int) curValue)) {
                        for (int singleSol : partialSolutions) {
                            solvable.set(singleSol, true);
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

}
