package benchmark.concurrency;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Micro benchmark for CountDownLatch from java and spinning busy wait latch.
 */
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(2)
@State(Scope.Thread)
public class BusyWaitingCountLatchBenchmark {

    private static final int THREADS_CNT = 100;

    private CountDownLatch latch;
    private ExecutorService latchPool;

    private SpinningCountDownLatch spinningLatch;
    private ExecutorService spinningLatchPool;

    private static final class SpinningCountDownLatch {

        private int count;

        SpinningCountDownLatch(int count) {
            this.count = count;
        }

        void countDown() {
            synchronized (this) {
                if (count != 0) {
                    --count;
                }
            }
        }

        void await() {
            while (true) {
                synchronized (this) {
                    if (count == 0) {
                        break;
                    }
                }
            }
        }

    }

    @Setup(Level.Invocation)
    public void setUp() {
        latch = new CountDownLatch(1);
        latchPool = Executors.newFixedThreadPool(THREADS_CNT);

        spinningLatch = new SpinningCountDownLatch(1);
        spinningLatchPool = Executors.newFixedThreadPool(THREADS_CNT);
    }

    @TearDown(Level.Invocation)
    public void tearDown() {

        latchPool.shutdown();
        latchPool = null;
        latch = null;

        spinningLatchPool.shutdown();
        spinningLatchPool = null;
        spinningLatch = null;
    }

    @Benchmark
    public void javaLatch(Blackhole bh) {

        for (int i = 0; i < THREADS_CNT; ++i) {
            latchPool.execute(() -> {
                try {
                    latch.await();
                }
                catch (InterruptedException interEx) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        latch.countDown();
    }

    @Benchmark
    public void busyWaitLatch(Blackhole bh) {
        for (int i = 0; i < THREADS_CNT; ++i) {
            spinningLatchPool.execute(() -> {
                spinningLatch.await();
            });
        }

        spinningLatch.countDown();
    }


    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(BusyWaitingCountLatchBenchmark.class.getSimpleName())
                .threads(Runtime.getRuntime().availableProcessors())
                .build();

        new Runner(opt).run();
    }

}
