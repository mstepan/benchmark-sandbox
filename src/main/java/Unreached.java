import java.util.concurrent.TimeUnit;

public class Unreached {

    static volatile Object obj;
    static volatile boolean output;

    public static void main(String[] args) throws InterruptedException {

        obj = null;
        for (int i = 0; i < 20_000; ++i) {
            hotMethod();
        }
        TimeUnit.SECONDS.sleep(5);

        System.out.println("not null");
        obj = new Object();
        for (int i = 0; i < 20_000; ++i) {
            hotMethod();

        }
        TimeUnit.SECONDS.sleep(5);

        System.out.println("back to null");
        obj = null;
        for (int i = 0; i < 20_000; ++i) {
            hotMethod();
        }
        TimeUnit.SECONDS.sleep(5);
    }

    static void hotMethod() {
        if (obj == null) {
            output = false;
        }
        else {
            output = true;
        }
    }


}
