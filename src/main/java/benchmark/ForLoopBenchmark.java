package benchmark;

import com.max.algs.util.ArrayUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * Micro benchmark.
 */
@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(5)
public class ForLoopBenchmark {

    static int sum = 0;
    private final int[] elementData = ArrayUtils.generateRandomArray(1000);

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
//    @CompilerControl(CompilerControl.Mode.INLINE)
    static void consume(int value) {
        sum += value;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(ForLoopBenchmark.class.getSimpleName())
                .threads(Runtime.getRuntime().availableProcessors())
                .build();

        new Runner(opt).run();
    }

    @Benchmark
    public void cstyle() {
        for (int i = 0; i < this.elementData.length; ++i) {
            consume(this.elementData[i]);
        }
    }

    @Benchmark
    public void hoisted() {
        int[] values = this.elementData;
        for (int i = 0; i < values.length; ++i) {
            consume(values[i]);
        }
    }

    @Benchmark
    public void foreach() {
        for (int value : this.elementData) {
            consume(value);
        }
    }

    /*
    INLINE
        Benchmark                 Mode  Cnt    Score   Error  Units
        ForLoopBenchmark.cstyle   avgt   25  540.751 ± 8.546  ns/op
        ForLoopBenchmark.foreach  avgt   25  544.386 ± 6.614  ns/op
        ForLoopBenchmark.hoisted  avgt   25  539.327 ± 6.489  ns/op

    DONT_INLINE
        Benchmark                 Mode  Cnt      Score      Error  Units
        ForLoopBenchmark.cstyle   avgt   25  67490.884 ± 3061.769  ns/op
        ForLoopBenchmark.foreach  avgt   25  53762.968 ± 2413.216  ns/op
        ForLoopBenchmark.hoisted  avgt   25  53822.566 ± 1933.995  ns/op

     */

}
