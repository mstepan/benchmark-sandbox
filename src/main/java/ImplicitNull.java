import java.util.concurrent.TimeUnit;

public class ImplicitNull {

    public static void main(String[] args) throws InterruptedException {

        for (int i = 0; i < 20_000; ++i) {
            hotMethod("foo");
        }

        TimeUnit.SECONDS.sleep(5);

        for (int i = 0; i < 20_000; ++i) {
            try {
                hotMethod(null);
            }
            catch (NullPointerException ex) {
//                System.out.println("Tempting fate");
            }
        }

    }

    static int hotMethod(String str) {
        return str.hashCode();
    }


}
