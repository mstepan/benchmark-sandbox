package benchmark.arithmetic;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

/**
 * Benchmark financial calculation. BigDecimal vs long.
 * <p>
 * # Run complete. Total time: 00:00:43
 * <p>
 * Benchmark                                       Mode  Cnt   Score   Error  Units
 * BigDecimalVsLongInMoneyBenchmark.useBigDecimal  avgt   10  81.815 ± 1.646  ns/op
 * BigDecimalVsLongInMoneyBenchmark.useLong        avgt   10   3.697 ± 0.092  ns/op
 */
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(2)
public class BigDecimalVsLongInMoneyBenchmark {

    @Benchmark
    public void useBigDecimal(Blackhole bh) {
        bh.consume(calcualateCandiesCountBigDecimal());
    }

    private static int calcualateCandiesCountBigDecimal() {
        BigDecimal tenCents = new BigDecimal("0.1");

        BigDecimal funds = new BigDecimal("1.0");
        int candiesCount = 0;

        for (BigDecimal candyPrice = tenCents; funds.compareTo(candyPrice) >= 0; candyPrice = candyPrice.add(tenCents)) {
            funds = funds.subtract(candyPrice);
            ++candiesCount;
        }

        return candiesCount;
    }

    @Benchmark
    public void useLong(Blackhole bh) {
        bh.consume(calcualateCandiesCountLong());
    }

    private static int calcualateCandiesCountLong() {

        long funds = 100L;
        int candiesCount = 0;

        for (long candyPrice = 10L; funds >= candyPrice; candyPrice += 10) {
            funds -= candyPrice;
            ++candiesCount;
        }

        return candiesCount;
    }


    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(BigDecimalVsLongInMoneyBenchmark.class.getSimpleName())
//                .threads(Runtime.getRuntime().availableProcessors())
                .build();

        new Runner(opt).run();
    }

}
