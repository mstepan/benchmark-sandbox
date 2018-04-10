import java.util.concurrent.TimeUnit;

/*
-XX:+PrintCompilation -XX:-TieredCompilation -XX:-BackgroundCompilation -XX:+UnlockDiagnosticVMOptions -XX:+PrintInlining

 */
public class InterfaceDevirtualization {

    static abstract class AbstractFunc {
        public abstract double accept(double value);
    }


    static class Square extends AbstractFunc {
        @Override
        public double accept(double val) {
            return val * val;
        }
    }

    static class AnotherSquare extends Square {
        @Override
        public double accept(double val) {
            return val * val;
        }
    }

    static class ThirdSquare extends AnotherSquare {
        @Override
        public double accept(double val) {
            return val * val;
        }
    }

    public static void main(String[] args) throws InterruptedException {

        System.out.println("Using: " + Square.class);

        AbstractFunc func1 = new Square();
        for (int i = 0; i < 20_000; ++i) {
            apply0(func1, i);
            apply1(func1, i);
            apply2(func1, i);
            apply3(func1, i);
            apply4(func1, i);
            apply5(func1, i);
            apply6(func1, i);
            apply7(func1, i);
        }
        TimeUnit.SECONDS.sleep(5);

        System.out.println("Loading: " + AnotherSquare.class);
        AbstractFunc func2 = new AnotherSquare();
        for (int i = 0; i < 20_000; ++i) {
            apply0(func2, i);
            apply1(func2, i);
        }
        TimeUnit.SECONDS.sleep(5);

        System.out.println("Loading: " + ThirdSquare.class);
        AbstractFunc func3 = new ThirdSquare();
        for (int i = 0; i < 20_000; ++i) {
            apply0(func3, i);
            apply1(func3, i);
        }
        TimeUnit.SECONDS.sleep(5);

        System.exit(0);
    }


    static double apply0(AbstractFunc func, double value) {
        return func.accept(value);
    }

    static double apply1(AbstractFunc func, double value) {
        return func.accept(value);
    }

    static double apply2(AbstractFunc func, double value) {
        return func.accept(value);
    }

    static double apply3(AbstractFunc func, double value) {
        return func.accept(value);
    }

    static double apply4(AbstractFunc func, double value) {
        return func.accept(value);
    }

    static double apply5(AbstractFunc func, double value) {
        return func.accept(value);
    }

    static double apply6(AbstractFunc func, double value) {
        return func.accept(value);
    }

    static double apply7(AbstractFunc func, double value) {
        return func.accept(value);
    }


}
