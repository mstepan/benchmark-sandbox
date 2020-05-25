package benchmark.sort.intrasort;


import com.max.algs.util.ArrayUtils;

final class HeapSort {

    private HeapSort() {
        throw new AssertionError("Can't instantiate utility only class");
    }

    static void sort(int[] arr, int from, int to) {
        assert arr != null : "null 'arr' detected";

        if (to - from + 1 < 2) {
            return;
        }

        convertToMaxHeap(arr, from, to);

        int last = to;

        while (last != from) {

            int maxValue = arr[from];

            arr[from] = arr[last];
            fixDown(arr, from, from, last - 1);

            arr[last] = maxValue;
            --last;
        }
    }

    /**
     * Floyd's classic heapify algorithm.
     * <p>
     * time: O(N)
     * space: O(1)
     */
    private static void convertToMaxHeap(int[] arr, int from, int to) {

        int size = to - from + 1;

        for (int parent = (from + (size / 2)) - 1; parent >= from; --parent) {
            fixDown(arr, parent, from, to);
        }
    }

    private static void fixDown(int[] arr, int index, int from, int to) {

        int maxIndex = index;

        int left = from + leftIndex(index - from);

        if (left <= to && arr[left] > arr[maxIndex]) {
            maxIndex = left;
        }

        int right = from + rightIndex(index - from);

        if (right <= to && arr[right] > arr[maxIndex]) {
            maxIndex = right;
        }

        if (maxIndex != index) {
            ArrayUtils.swap(arr, index, maxIndex);
            fixDown(arr, maxIndex, from, to);
        }
    }


    /*
     * left child index = 2 * parent + 1
     *
     * simplified: 2x + 1 = (x << 1) | 1
     */
    private static int leftIndex(int parentIndex) {
        return (parentIndex << 1) | 1;
    }

    /**
     * right child index = 2 * parent + 2
     * <p>
     * simplified: 2x + 2 = 2 * (x + 1) = (x + 1) << 1
     */
    private static int rightIndex(int parentIndex) {
        return (parentIndex + 1) << 1;
    }


}
