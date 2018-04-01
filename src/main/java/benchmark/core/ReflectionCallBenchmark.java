package benchmark.core;

import org.apache.log4j.Logger;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Micro benchmark for reflection call.
 */
@State(Scope.Group)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(5)
public class ReflectionCallBenchmark {

    private static final Logger LOG = Logger.getLogger(MethodHandles.lookup().lookupClass());

    @Benchmark
    @Group("reflection")
    @GroupThreads(4)
    public void reflection(PerThreadState state) {
        try {
            state.method.invoke(state.obj);
        }
        catch (ReflectiveOperationException ex) {
            LOG.error("Error during reflective calls", ex);
        }
    }

    @Benchmark
    @Group("directCall")
    @GroupThreads(4)
    public void directCall(PerThreadState state) {
        state.obj.getValue();
    }

    @State(Scope.Thread)
    public static class PerThreadState {

        private static final Random RAND = ThreadLocalRandom.current();

        public MyCustomClass obj;
        public Method method;

        @Setup(Level.Invocation)
        public void setUp() {
            obj = new MyCustomClass(RAND.nextInt());

            try {
                method = MyCustomClass.class.getDeclaredMethod("getValue");
            }
            catch (ReflectiveOperationException ex) {
                LOG.error("Can't obtain method reflectively", ex);
            }

        }

        @TearDown(Level.Invocation)
        public void tearDown() {
            obj = null;
        }
    }

    private static final class MyCustomClass {

        private final int value;

        MyCustomClass(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(ReflectionCallBenchmark.class.getSimpleName())
                .threads(Runtime.getRuntime().availableProcessors())
                .build();

        new Runner(opt).run();
    }

}
