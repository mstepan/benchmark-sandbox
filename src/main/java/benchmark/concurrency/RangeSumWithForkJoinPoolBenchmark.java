package benchmark.concurrency;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;

/**
 * RangeSum with ForkJoin framework benchmark using different threshold sizes for sequential part.
 */
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(2)
@State(Scope.Thread)
public class RangeSumWithForkJoinPoolBenchmark {

    @Param({"10000", "15000", "20000", "50000", "100000", "200000"})
    public int thresholdSize;

    @Benchmark
    public void rangeSum(Blackhole bh) {
        bh.consume(new ForkJoinPool().invoke(new RangeSum(thresholdSize, 1, 1_000_000_000)));
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(RangeSumWithForkJoinPoolBenchmark.class.getSimpleName())
//                .threads(Runtime.getRuntime().availableProcessors())
                .build();

        new Runner(opt).run();
    }

    private static final class RangeSum extends RecursiveTask<Long> {

        final int sequentialThreshold;

        final int from;
        final int to;

        RangeSum(int sequentialThreshold, int from, int to) {
            this.sequentialThreshold = sequentialThreshold;
            this.from = from;
            this.to = to;
        }

        @Override
        protected Long compute() {

            int elemsCnt = to - from + 1;

            if (elemsCnt <= sequentialThreshold) {
                return computeSequentially();
            }

            int middle = from + (to - from) / 2;

            RangeSum left = new RangeSum(sequentialThreshold, from, middle);
            left.fork();

            RangeSum right = new RangeSum(sequentialThreshold, middle + 1, to);

            return right.compute() + left.join();
        }

        private long computeSequentially() {
            long res = 0L;

            for (int i = from; i <= to; ++i) {
                res += i;
            }

            return res;
        }
    }

}
