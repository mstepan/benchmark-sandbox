import java.util.concurrent.TimeUnit;

/*
-XX:+UnlockDiagnosticVMOptions -XX:+TraceClassLoading -XX:+LogCompilation -XX:+PrintAssembly
 */
public class Unreached {

    static volatile Object obj;
    static volatile boolean isNull;
    static volatile boolean notNull;

    public static void main(String[] args) throws InterruptedException {

        obj = null;
        for (int i = 0; i < 20_000; ++i) {
            hotMethod();
        }
        TimeUnit.SECONDS.sleep(5);

        obj = new Object();
        for (int i = 0; i < 20_000; ++i) {
            hotMethod();
        }
        TimeUnit.SECONDS.sleep(5);

        obj = null;
        for (int i = 0; i < 20_000; ++i) {
            hotMethod();
        }
        TimeUnit.SECONDS.sleep(5);

        System.exit(0);

    }

    static void hotMethod() {
        if (obj == null) {
            isNull = true;
        }
        else {
            notNull = true;
        }
    }


}
