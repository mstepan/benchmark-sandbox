package benchmark.concurrency;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.StampedLock;

@State(Scope.Group)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(5)
public class StampedVsReadWriteLockBenchmark {

    private static final Random RAND1 = new Random();

    private static final Random RAND2 = new Random();
    private static final TwoValuesReadWrite READ_WRITE_DATA = new TwoValuesReadWrite();
    private static final TwoValuesStamped STAMPED_DATA = new TwoValuesStamped();

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(StampedVsReadWriteLockBenchmark.class.getSimpleName())
                .threads(Runtime.getRuntime().availableProcessors())
                .build();

        new Runner(opt).run();
    }

    @Benchmark
    @Group("stampedLockLockRead")
    @GroupThreads(4)
    public void stampedLockLockRead() {
        STAMPED_DATA.readValues();
    }

    @Benchmark
    @Group("stampedLockWrite")
    @GroupThreads(4)
    public void stampedLockWrite() {
        STAMPED_DATA.writeValues(RAND2.nextInt());
    }

    @Benchmark
    @Group("readWriteLockRead")
    @GroupThreads(4)
    public void readWriteLockRead() {
        READ_WRITE_DATA.readValues();
    }

    @Benchmark
    @Group("readWriteLockWrite")
    @GroupThreads(4)
    public void readWriteLockWrite() {
        READ_WRITE_DATA.writeValues(RAND1.nextInt());
    }

    private static final class TwoValuesStamped {

        private final StampedLock lock = new StampedLock();

        int x;
        int y;

        public void writeValues(int value) {
            long stamp = lock.writeLock();
            try {
                x = value;
                y = value;
            }
            finally {
                lock.unlockWrite(stamp);
            }
        }

        public int[] readValues() {

            long stamp = lock.tryOptimisticRead();

            int curX = x;
            int curY = y;

            if (!lock.validate(stamp)) {
                stamp = lock.readLock();
                try {
                    curX = x;
                    curY = y;
                }
                finally {
                    lock.unlockRead(stamp);
                }
            }

            return new int[]{curX, curY};
        }

    }

    private static final class TwoValuesReadWrite {

        private final ReadWriteLock lock;

        private final Lock readLock;
        private final Lock writeLock;
        int x;
        int y;

        TwoValuesReadWrite() {
            lock = new ReentrantReadWriteLock();
            readLock = lock.readLock();
            writeLock = lock.writeLock();
        }

        public void writeValues(int value) {
            writeLock.lock();
            try {
                x = value;
                y = value;
            }
            finally {
                writeLock.unlock();
            }
        }

        public int[] readValues() {

            int curX, curY;
            readLock.lock();

            try {
                curX = x;
                curY = y;
            }
            finally {
                readLock.unlock();
            }

            return new int[]{curX, curY};
        }

    }


}
