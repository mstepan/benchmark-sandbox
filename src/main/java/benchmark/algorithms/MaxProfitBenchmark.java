package benchmark.algorithms;

import com.max.algs.util.ArrayUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Micro benchmark for max profit problem.
 */
@State(Scope.Group)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(5)
public class MaxProfitBenchmark {

    private static final int[] arr1 = ArrayUtils.generateRandomArray(10_000);
    private static final int[] arr2 = Arrays.copyOf(arr1, arr1.length);

    private static final int BRUTEFORCE_THRESHOLD = 7;

//    @Benchmark
//    @Group("bruteforce")
//    @GroupThreads(1)
//    public static int maxProfitBruteforce() {
//
//        int[] prices = arr;
//
//        int maxProfit = Integer.MIN_VALUE;
//
//        for (int i = 0; i < prices.length - 1; i++) {
//            for (int j = i + 1; j < prices.length; j++) {
//
//                maxProfit = Math.max(maxProfit, prices[j] - prices[i]);
//            }
//        }
//
//        return maxProfit;
//    }

    @Benchmark
    @Group("div_and_conquer")
    @GroupThreads(1)
    public static int maxProfitDivAndConquer() {
        int[] prices = arr2;
        return maxProfitDivAndConqRec(prices, 0, prices.length - 1);
    }

    private static int maxProfitBruteforceInternal(int[] prices, int from, int to) {

        int maxProfit = Integer.MIN_VALUE;

        for (int i = from; i < to; i++) {
            for (int j = i + 1; j <= to; j++) {
                maxProfit = Math.max(maxProfit, prices[j] - prices[i]);
            }
        }

        return maxProfit;
    }


    private static int maxProfitDivAndConqRec(int[] prices, int from, int to) {
        int elemsCount = to - from + 1;

        if (elemsCount <= BRUTEFORCE_THRESHOLD) {
            return maxProfitBruteforceInternal(prices, from, to);
        }

        if (elemsCount == 1) {
            return Integer.MIN_VALUE;
        }

        if (elemsCount == 2) {
            if (prices[from] <= prices[to]) {
                return prices[to] - prices[from];
            }

            return Integer.MIN_VALUE;
        }

        int mid = from + ((to - from) >>> 1);

        // check left
        int maxProfit = maxProfitDivAndConqRec(prices, from, mid);

        // check right
        maxProfit = Math.max(maxProfit, maxProfitDivAndConqRec(prices, mid + 1, to));

        // check middle
        int minLeft = prices[mid];
        for (int i = mid - 1; i >= from; i--) {
            minLeft = Math.min(minLeft, prices[i]);
        }

        int maxRight = prices[mid + 1];

        for (int i = mid + 2; i <= to; i++) {
            maxRight = Math.max(maxRight, prices[i]);
        }

        return Math.max(maxProfit, maxRight - minLeft);
    }


    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(MaxProfitBenchmark.class.getSimpleName())
                .threads(Runtime.getRuntime().availableProcessors())
                .build();

        new Runner(opt).run();
    }


}
