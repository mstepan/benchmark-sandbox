package benchmark.arithmetic;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkArgument;

@State(Scope.Group)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class SumOfDigitsBenchmark {

    private final Random rand = ThreadLocalRandom.current();

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(SumOfDigitsBenchmark.class.getSimpleName())
                .threads(Runtime.getRuntime().availableProcessors())
                .build();

        new Runner(opt).run();
    }

    @Benchmark
    @Group("bruteForce")
    @GroupThreads(1)
    public void countBruteForce() {
        int digits = rand.nextInt(10);
        int sum = rand.nextInt(82);
        countBruteForce(digits, sum);
    }

    private int countBruteForce(int digits, int sum) {

        checkArgument(digits < 10);

        int first = (int) Math.pow(10.0, digits - 1.0);
        int last = (int) Math.pow(10.0, digits) - 1;

        if (sumOfDigits(last) < sum) {
            return 0;
        }

        int count = 0;

        for (int i = first; i <= last; i++) {

            if (sumOfDigits(i) == sum) {
                ++count;
            }
        }

        return count;

    }

    private int sumOfDigits(int baseValue) {
        int value = baseValue;

        int sum = 0;

        while (value != 0) {
            sum += value % 10;
            value /= 10;
        }

        return sum;
    }

    @Benchmark
    @Group("dynamic")
    @GroupThreads(1)
    public void countWithSumOfDigitsDynamic() {
        int digits = rand.nextInt(10);
        int sum = rand.nextInt(82);
        countWithSumOfDigits(digits, sum);
    }

    /**
     * time: O(digits*sum)
     * space: O(digits*sum)
     */
    private int countWithSumOfDigits(int digits, int sum) {

        checkArgument(digits < 10);

        int last = (int) Math.pow(10.0, digits) - 1;

        if (sumOfDigits(last) < sum) {
            return 0;
        }

        int[][] solution = new int[digits + 1][sum + 1];

        solution[0][0] = 1;

        for (int row = 1; row < solution.length; row++) {
            for (int col = 1; col < solution[row].length; col++) {

                int curCount = 0;

                for (int offset = 0; offset <= Math.min(col, 9); offset++) {
                    curCount += solution[row - 1][col - offset];
                }

                solution[row][col] = curCount;
            }
        }

        int lastRow = solution.length - 1;
        int lastCol = solution[lastRow].length - 1;

        return solution[lastRow][lastCol];

    }

}
