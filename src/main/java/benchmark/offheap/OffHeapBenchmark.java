package benchmark.offheap;

import com.max.system.UnsafeUtils;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import sun.misc.Unsafe;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Micro benchmark for off-heap access using Unsafe and Directed ByteBuffer.
 *
 * Benchmark                                          Mode  Cnt   Score   Error  Units
 * OffHeapBenchmark.byteBufferSequentialAccess        avgt   10  54.711 ± 3.171  ns/op
 * OffHeapBenchmark.byteBufferIndexAccess             avgt   10  49.396 ± 5.426  ns/op
 * OffHeapBenchmark.byteBufferNativeOrderIndexAccess  avgt   10  47.479 ± 8.832  ns/op
 * OffHeapBenchmark.unsafeAccess                      avgt   10  39.855 ± 3.440  ns/op
 */
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(2)
@State(Scope.Thread)
public class OffHeapBenchmark {

    private static final int BUF_LENGTH = 10;

    private final Unsafe unsafe = UnsafeUtils.getUnsafe();

    ByteBuffer buf;
    ByteBuffer bufNativeOrder;
    long baseAddress;


    private static final Random RAND = ThreadLocalRandom.current();

    private static ByteBuffer create(boolean usaNativeByteOrder) {
        ByteBuffer buf = ByteBuffer.allocateDirect(4 * BUF_LENGTH);
        if (usaNativeByteOrder) {
            buf.order(ByteOrder.nativeOrder());
        }

        for (int i = 0; i < BUF_LENGTH; ++i) {
            buf.putInt(RAND.nextInt(100));
        }
        buf.flip();

        return buf;
    }

    @Setup(Level.Invocation)
    public void setUp() {
        buf = create(false);
        bufNativeOrder = create(true);

        baseAddress = unsafe.allocateMemory(4 * BUF_LENGTH);

        for (int i = 0; i < BUF_LENGTH; ++i) {
            unsafe.putInt(baseAddress + (i * 4), RAND.nextInt(100));
        }
    }

    @TearDown(Level.Invocation)
    public void tearDown() {
        buf.clear();
        buf = null;

        bufNativeOrder.clear();
        bufNativeOrder = null;

        baseAddress = 0L;
    }

    /**
     * Use direct ByteBuffer to access memory by index (with default byte order).
     */
    @Benchmark
    public void byteBufferIndexAccess(Blackhole bh) {
        int sum = 0;

        for (int i = 0; i < BUF_LENGTH; ++i) {
            sum += buf.getInt(i * 4);
        }

        bh.consume(sum);
    }
    /**
     * Use direct ByteBuffer with native byte order to access memory by index.
     */
    @Benchmark
    public void byteBufferNativeOrderIndexAccess(Blackhole bh) {
        int sum = 0;

        for (int i = 0; i < BUF_LENGTH; ++i) {
            sum += bufNativeOrder.getInt(i * 4);
        }

        bh.consume(sum);
    }

    /**
     * Use direct ByteBuffer to access memory sequentially.
     */
    @Benchmark
    public void byteBufferSequentialAccess(Blackhole bh) {
        int sum = 0;

        while (buf.hasRemaining()) {
            sum += buf.getInt();
        }

        bh.consume(sum);
    }

    /**
     * Use Unsafe to access memory directly by index.
     * @param bh
     */
    @Benchmark
    public void unsafeAccess(Blackhole bh) {
        int sum = 0;

        for (int i = 0; i < BUF_LENGTH; ++i) {
            sum += unsafe.getInt(baseAddress + (i * 4));
        }

        bh.consume(sum);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(OffHeapBenchmark.class.getSimpleName())
//                .threads(Runtime.getRuntime().availableProcessors())
                .build();

        new Runner(opt).run();
    }

}
