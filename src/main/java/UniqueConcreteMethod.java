import java.util.concurrent.TimeUnit;

/*

-XX:+PrintCompilation -XX:-TieredCompilation -XX:-BackgroundCompilation -XX:+UnlockDiagnosticVMOptions -XX:+PrintInlining

 */
public class UniqueConcreteMethod {

    static abstract class Func {
        public abstract double accept(double value);
    }

    static class Square extends Func {
        @Override
        public double accept(double val) {
            return val * val;
        }
    }

    static class Sqrt extends Func {
        @Override
        public double accept(double val) {
            return Math.sqrt(val);
        }
    }

    static class SecondSquare extends Square {
    }

    static class ThirdSquare extends Square {
    }

    public static void main(String[] args) throws InterruptedException {

        System.out.println("Using: " + Square.class);

        Func func1 = new Square();
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

        System.out.println("Loading: " + SecondSquare.class);
        Func func2 = new SecondSquare();
        for (int i = 0; i < 20_000; ++i) {
            apply0(func2, i);
            apply1(func2, i);
        }
        TimeUnit.SECONDS.sleep(5);

        System.out.println("Loading: " + ThirdSquare.class);
        Func func3 = new ThirdSquare();
        for (int i = 0; i < 20_000; ++i) {
            apply0(func3, i);
            apply1(func3, i);
        }
        TimeUnit.SECONDS.sleep(5);

        System.out.println("Loading: " + Sqrt.class);

        TimeUnit.SECONDS.sleep(5);

        System.exit(0);
    }


    static double apply0(Func func, double value) {
        return func.accept(value);
    }

    static double apply1(Func func, double value) {
        return func.accept(value);
    }

    static double apply2(Func func, double value) {
        return func.accept(value);
    }

    static double apply3(Func func, double value) {
        return func.accept(value);
    }

    static double apply4(Func func, double value) {
        return func.accept(value);
    }

    static double apply5(Func func, double value) {
        return func.accept(value);
    }

    static double apply6(Func func, double value) {
        return func.accept(value);
    }

    static double apply7(Func func, double value) {
        return func.accept(value);
    }


}
