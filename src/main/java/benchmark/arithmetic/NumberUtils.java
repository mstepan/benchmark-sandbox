package benchmark.arithmetic;

public final class NumberUtils {

    private NumberUtils() {
        throw new AssertionError("Can't instantiate utility-only class");
    }

    /**
     * Convert 'initialValue' from int to String and check if string is palindrome or not.
     * <p>
     * K = decimal digits in 'value'
     * time: O(k)
     * space: O(K)
     */
    public static boolean isPalindromeAsString(int initialValue) {
        if (initialValue < 0) {
            return false;
        }

        char[] arr = String.valueOf(initialValue).toCharArray();

        int left = 0;
        int right = arr.length - 1;

        while (left < right) {
            if (arr[left] != arr[right]) {
                return false;
            }

            ++left;
            --right;
        }

        return true;
    }

    /**
     * K = decimal digits in 'value'
     * <p>
     * time: O(k)
     * space: O(1)
     * <p>
     * Check if value is palindrome by comparing digits of int value.
     */
    public static boolean isPalindromeClassic(int initialValue) {
        if (initialValue < 0) {
            return false;
        }

        int value = Math.abs(initialValue);

        int digitsCount = countDecimalDigits(value);
        int upperBoundary = (int) Math.pow(10.0, digitsCount - 1);

        while (value >= 10) {

            int leftDigit = value / upperBoundary;
            int rightDigit = value % 10;

            if (leftDigit != rightDigit) {
                return false;
            }

            value = value % upperBoundary;
            value /= 10;

            upperBoundary /= 100;
        }

        return true;
    }

    private static int countDecimalDigits(int value) {
        return ((int) Math.log10(value)) + 1;
    }

    /**
     * Check is int value is palidrome by reverting decimal number and checking against initial value.
     * K = decimal digits in 'value'
     * <p>
     * time: O(K)
     * space: O(1)
     */
    public static boolean isPalindromeReverseNumber(int value) {
        if (value < 0) {
            return false;
        }

        long reverse = reverseDecimalDigits(value);

        return reverse == value;
    }

    private static long reverseDecimalDigits(int initialValue) {
        if (initialValue == Integer.MIN_VALUE) {
            return -8463847412L;
        }

        long res = 0L;

        int value = Math.abs(initialValue);

        while (value != 0) {
            res = 10L * res + (value % 10);
            value /= 10;
        }

        return initialValue >= 0 ? res : -res;
    }
}
