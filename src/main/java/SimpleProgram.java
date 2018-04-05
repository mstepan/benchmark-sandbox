/*
-XX:+PrintCompilation -XX:-BackgroundCompilation -XX:+UnlockDiagnosticVMOptions -XX:+PrintInlining

-XX:+UnlockDiagnosticVMOptions -XX:+TraceClassLoading -XX:+LogCompilation -XX:+PrintAssembly
 */
public class SimpleProgram {

    static final class Point {
        final int x;
        final int y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public static void main(String[] args) throws InterruptedException {

        double totalDistance = 0.0;

        for (int i = 0; i < 20_000; ++i) {
            totalDistance += hotDistance(new Point(i, i));
        }

        System.out.println(totalDistance);
    }

    static double hotDistance(Point p) {

        int x1 = p.x;
        call(x1);
        int x2 = p.x;

        int y1 = p.y;
        call(y1);
        int y2 = p.y;

        return Math.sqrt(x1 * x2 + y1 * y2);
    }

    static int call(int x) {
       return x + 2;
    }


}
