import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/*
-XX:+PrintCompilation -XX:-BackgroundCompilation -XX:+UnlockDiagnosticVMOptions -XX:+PrintInlining

-XX:+UnlockDiagnosticVMOptions -XX:+TraceClassLoading -XX:+LogCompilation -XX:+PrintAssembly
 */
public class Optimization {


    public static void main(String[] args) throws InterruptedException {

//        int[] arr = randomNumbers(1_000);


        for (int i = 0; i < 20_000; ++i) {
            factory();
        }


        System.out.println("Waiting for compiler...");
        TimeUnit.SECONDS.sleep(5);

        /*
        * If you stop the VM with SIGQUIT (kill -9) the VM does not combine the logs.
        * Stopping the program with a System.exit() or using a SIGTERM (kill) signal
        * will ensure the logs are combined.
        */
        System.exit(0);
    }

    static Object factory() {
        return new Object();
    }

    static int sumOfSquares(int[] arr) {
        int sum = 0;

        for (int val : arr) {
            sum += square(val);
        }
        return sum;
    }

    static int square(int x) {
        return x * x;
    }


    private static final Random RAND = ThreadLocalRandom.current();

    private static int[] randomNumbers(int length) {
        int[] randArr = new int[length];

        for (int i = 0, baseLength = randArr.length; i < baseLength; ++i) {
            randArr[i] = RAND.nextInt();

        }

        return randArr;
    }
}
