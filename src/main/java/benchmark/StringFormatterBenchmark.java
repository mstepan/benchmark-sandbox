package benchmark;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Micro benchmark example.
 * <p>
 * Benchmark                        Mode  Cnt     Score   Error  Units
 * StringFormatterBenchmark.concat  avgt   10    91.212 ± 1.026  ns/op
 * StringFormatterBenchmark.format  avgt   10  1243.393 ± 9.485  ns/op
 */
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(2)
@State(Scope.Thread)
public class StringFormatterBenchmark {

    private static final Random RAND = ThreadLocalRandom.current();

    private static final String SYNAPSE_LITE_URL = "http://dev-synapse.va.opower.it:8999";

    private static final String[] TENANTS = {"pge", "cec", "bgec", "peco"};

    private String tenant;
    private int accountId;

    @Setup(Level.Invocation)
    public void setUp() {
        tenant = TENANTS[RAND.nextInt(TENANTS.length)];
        accountId = RAND.nextInt(Integer.MAX_VALUE);
    }

    @TearDown(Level.Invocation)
    public void tearDown() {
        tenant = null;
    }

    @Benchmark
    public void concat(Blackhole bh) {
        String v2 = SYNAPSE_LITE_URL + "/oucss/" + tenant + "/sessions/" + accountId + "/customers";
        bh.consume(v2);
    }

    @Benchmark
    public void format(Blackhole bh) {
        String v1 = String.format("%s/oucss/%s/sessions/%s/customers", SYNAPSE_LITE_URL, tenant, accountId);
        bh.consume(v1);
    }


    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(StringFormatterBenchmark.class.getSimpleName())
//                .threads(Runtime.getRuntime().availableProcessors())
                .build();

        new Runner(opt).run();
    }

}
