import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class HotNull {

    private static final int ARR_LENGTH = 100_000;

    public static void main(String[] args) throws InterruptedException {
        Object[] arr1 = randomObjects(10, ARR_LENGTH);
        System.out.println(countNpes1(arr1));

        Object[] arr2 = randomObjects(50, ARR_LENGTH);
        System.out.println(countNpes2(arr2));

        Object[] arr3 = randomObjects(90, ARR_LENGTH);
        System.out.println(countNpes3(arr3));
    }

    static Result countNpes1(Object[] arr) {

        int exCnt = 0;

        Set<NullPointerException> set = new HashSet<>();

        for (int i = 0; i < arr.length; ++i) {
            try {
                arr[i].hashCode();
            }
            catch (NullPointerException ex) {
                if( ! set.add(ex) ){
                    ex.printStackTrace();
                }
                ++exCnt;
            }
        }

        return new Result(exCnt, set.size());

    }

    static Result countNpes2(Object[] arr) {

        int exCnt = 0;

        Set<NullPointerException> set = new HashSet<>();

        for (int i = 0; i < arr.length; ++i) {
            try {
                arr[i].hashCode();
            }
            catch (NullPointerException ex) {
                set.add(ex);
                ++exCnt;
            }
        }

        return new Result(exCnt, set.size());

    }

    static Result countNpes3(Object[] arr) {

        int exCnt = 0;

        Set<NullPointerException> set = new HashSet<>();

        for (int i = 0; i < arr.length; ++i) {
            try {
                arr[i].hashCode();
            }
            catch (NullPointerException ex) {
                set.add(ex);
                ++exCnt;
            }
        }

        return new Result(exCnt, set.size());

    }

    static class Result {
        final int exceptionsCount;
        final int uniqueCount;

        Result(int exceptionsCount, int uniqueCount) {
            this.exceptionsCount = exceptionsCount;
            this.uniqueCount = uniqueCount;
        }

        @Override
        public String toString() {
            return "exceptionsCount: " + exceptionsCount + ", uniqueCount: " + uniqueCount;
        }
    }

    private static final Random RAND = ThreadLocalRandom.current();

    static Object[] randomObjects(int nullProbability, int length) {
        Object[] arr = new Object[length];

        for (int i = 0; i < arr.length; ++i) {

            int randVal = RAND.nextInt(100);

            arr[i] = randVal < nullProbability ? null : randVal;
        }

        return arr;
    }


}
