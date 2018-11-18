package benchmark.algorithms;

import org.eclipse.collections.impl.set.mutable.primitive.IntHashSet;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.IntUnaryOperator;

/**
 * Micro benchmark for different cycle detection algorithms.
 * <p>
 * # Run complete. Total time: 00:01:06
 * <p>
 * Benchmark                                                     Mode  Cnt  Score   Error  Units
 * FunctionCycleDetectionBenchmark.findCycleWithBrentsInternal   avgt   10  2.221 ± 0.181  ms/op
 * FunctionCycleDetectionBenchmark.findCycleWithFloydsInternal   avgt   10  2.363 ± 0.221  ms/op
 * FunctionCycleDetectionBenchmark.findCycleWithHashingInternal  avgt   10  2.875 ± 0.278  ms/op
 */
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(2)
@State(Scope.Thread)
public class FunctionCycleDetectionBenchmark {

    private static final int BOUNDARY = 1_000_000_000;
    private static final Random RAND = ThreadLocalRandom.current();

    private IntUnaryOperator func;
    private int x0;

    static {
        Class<?> clazz = IntHashSet.class;
        System.out.println(clazz.getCanonicalName() + " loaded");
    }

    @Setup(Level.Invocation)
    public void setUp() {

        int a = RAND.nextInt(BOUNDARY);
        int b = RAND.nextInt(BOUNDARY);
        int m = RAND.nextInt(BOUNDARY);

        func = x -> (((a % m) * (x % m)) % m + (b % m)) % m;

        x0 = RAND.nextInt(BOUNDARY);
    }

    @Benchmark
    public void findCycleWithHashingInternal(Blackhole bh) {
        bh.consume(findCycleWithHashingInternal(func, x0));
    }

    @Benchmark
    public void findCycleWithFloydsInternal(Blackhole bh) {
        bh.consume(findCycleWithFloydsInternal(func, x0));
    }

    @Benchmark
    public void findCycleWithBrentsInternal(Blackhole bh) {
        bh.consume(findCycleWithBrentsInternal(func, x0));
    }

    /**
     * Find function cycle using Brent's cycle detection algorithm.
     */
    private static FunctionCycle findCycleWithBrentsInternal(IntUnaryOperator func, int x0) {

        int power = 1;
        int lam = 1;

        int t = x0;
        int h = func.applyAsInt(x0);

        // 'lam' will be equal to cycle length at the end of iteration
        while (t != h) {
            if (power == lam) {
                t = h;
                power *= 2;
                lam = 0;
            }

            h = func.applyAsInt(h);
            ++lam;
        }

        final int cycleLength = lam;

        t = x0;
        h = x0;

        // find star point for cycle
        for (int i = 0; i < lam; ++i) {
            h = func.applyAsInt(h);
        }

        while (t != h) {
            t = func.applyAsInt(t);
            h = func.applyAsInt(h);
        }

        return new FunctionCycle(t, cycleLength);
    }

    /**
     * Find function cycle using HashSet
     */
    private static FunctionCycle findCycleWithHashingInternal(IntUnaryOperator func, int x0) {

        IntHashSet detectedValues = new IntHashSet();
        int curValue = x0;

        while (!detectedValues.contains(curValue)) {
            detectedValues.add(curValue);
            curValue = func.applyAsInt(curValue);
        }

        final int startPoint = curValue;

        curValue = func.applyAsInt(curValue);
        int length = 1;

        while (curValue != startPoint) {
            curValue = func.applyAsInt(curValue);
            ++length;
        }

        return new FunctionCycle(startPoint, length);
    }

    /**
     * Find function cycle using Floyd's cycle detection algorithm.
     */
    private static FunctionCycle findCycleWithFloydsInternal(IntUnaryOperator func, int x0) {

        int t = func.applyAsInt(x0);
        int h = func.applyAsInt(func.applyAsInt(x0));

        // find point inside the cycle
        while (t != h) {
            t = func.applyAsInt(t);
            h = func.applyAsInt(func.applyAsInt(h));
        }

        h = x0;

        while (h != t) {
            t = func.applyAsInt(t);
            h = func.applyAsInt(h);
        }

        final int startPoint = t;
        int length = 1;
        h = func.applyAsInt(h);

        while (t != h) {
            h = func.applyAsInt(h);
            ++length;
        }

        return new FunctionCycle(startPoint, length);
    }

    private static final class FunctionCycle {
        final int startPoint;
        final int length;

        FunctionCycle(int startPoint, int length) {
            this.startPoint = startPoint;
            this.length = length;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;

            FunctionCycle other = (FunctionCycle) obj;

            return Objects.equals(startPoint, other.startPoint) &&
                    Objects.equals(length, other.length);
        }

        @Override
        public int hashCode() {
            return Objects.hash(startPoint, length);
        }

        @Override
        public String toString() {
            return "startPoint: " + startPoint + ", length: " + length;
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(FunctionCycleDetectionBenchmark.class.getSimpleName())
//                .threads(Runtime.getRuntime().availableProcessors())
                .build();

        new Runner(opt).run();
    }

}
