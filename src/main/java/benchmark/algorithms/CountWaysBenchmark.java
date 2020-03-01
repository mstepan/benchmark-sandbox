package benchmark.algorithms;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;


@State(Scope.Group)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(5)
public class CountWaysBenchmark {

    private static final Random RAND = ThreadLocalRandom.current();

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(CountWaysBenchmark.class.getSimpleName())
                .threads(Runtime.getRuntime().availableProcessors())
                .build();

        new Runner(opt).run();
    }

    @Benchmark
    @Group("countWays1")
    public int countWays1() {

        int value = RAND.nextInt(Integer.MAX_VALUE);

        if (value < 2) {
            return 0;
        }

        int boundary = ((int) Math.sqrt(value)) + 1;
        int cnt = 0;

        for (int first = 0; first <= boundary; first++) {

            int other = (int) Math.sqrt((double) value - (first * first));

            if (first <= other && value == (first * first + other * other)) {
                ++cnt;
            }
        }

        return cnt;
    }

    @Benchmark
    @Group("countWays2")
    public int countWays2() {

        int value = RAND.nextInt(Integer.MAX_VALUE);

        if (value < 2) {
            return 0;
        }

        int cnt = 0;

        int left = 0;
        int right = ((int) Math.sqrt(value)) + 1;

        while (left <= right) {

            int curValue = left * left + right * right;

            if (curValue == value) {
                ++left;
                --right;
                ++cnt;
            }
            else if (curValue < value) {
                ++left;
            }
            else {
                --right;
            }
        }

        return cnt;
    }
}
