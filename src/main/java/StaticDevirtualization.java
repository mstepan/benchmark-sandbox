import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/*
-XX:+PrintCompilation -XX:-TieredCompilation -XX:-BackgroundCompilation -XX:+UnlockDiagnosticVMOptions -XX:+PrintInlining

 */
public class StaticDevirtualization {

    static abstract class Comsumer<T> {
        public abstract void accept(T val);
    }


    static class NopConsumer<T> extends Comsumer<T> {
        @Override
        public void accept(T val) {

        }
    }

    static class OtherConsumer<T> extends Comsumer<T> {
        @Override
        public void accept(T val) {

        }
    }

    private static final Random RAND = ThreadLocalRandom.current();

    public static void main(String[] args) throws InterruptedException {

        Comsumer<Integer> consumer1 = new NopConsumer<>();
//        Comsumer<Integer> consumer2 = new OtherConsumer<>();

        for (int i = 0; i < 20_000; ++i) {
            consume((i & 1) == 0 ? consumer1 : consumer1, i);
        }

        TimeUnit.SECONDS.sleep(5);

        System.exit(0);
    }

    static <T> void consume(Comsumer<T> consumer, T value) {
        consumer.accept(value);
    }

}
