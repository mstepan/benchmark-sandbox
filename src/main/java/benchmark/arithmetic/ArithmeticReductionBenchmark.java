package benchmark.arithmetic;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Micro benchmark division reduction to shift.
 */
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(2)
public class ArithmeticReductionBenchmark {

    @State(Scope.Benchmark)
    public static class Data {

        private static final Random RAND = new Random(532);

        public int x;

        @Setup(Level.Trial)
        public void setUp() {
            x = RAND.nextInt();
        }

    }

    @Benchmark
    public int div4(Data state) {
        return state.x / 4;
    }

    @Benchmark
    public int div5(Data state) {
        return state.x / 5;
    }


    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(ArithmeticReductionBenchmark.class.getSimpleName())
//                .threads(Runtime.getRuntime().availableProcessors())
                .build();

        new Runner(opt).run();
    }

}
