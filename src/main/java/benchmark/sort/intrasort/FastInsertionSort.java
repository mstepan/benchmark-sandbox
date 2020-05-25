package benchmark.sort.intrasort;


import com.max.algs.util.ArrayUtils;

final class FastInsertionSort implements InsertionSort {

    @Override
    public void sort(int[] arr, int from, int to) {
        assert arr != null : "null 'arr' detected";

        final int length = to - from + 1;

        if (length < 2) {
            return;
        }

        if ((length & 1) == 0) {
            heapify(arr, from, to - 1);
            fixUp(arr, from, to);
        }
        else {
            heapify(arr, from, to);
        }

        unguardedInsertionSort(arr, from, to);
    }

    private static void unguardedInsertionSort(int[] arr, int from, int to) {
        assert arr != null : "null 'arr' detected";

        int temp;
        for (int i = from + 1; i <= to; ++i) {

            temp = arr[i];

            int j = i - 1;

            while (arr[j] > temp) {
                arr[j + 1] = arr[j];
                --j;
            }

            arr[j + 1] = temp;
        }
    }

    private static void heapify(int[] arr, int from, int to) {
        assert arr != null : "null 'arr' detected";

        int size = to - from + 1;

        for (int parent = (from + (size / 2)) - 1; parent >= from; --parent) {
            fixDown(arr, parent, from, to);
        }
    }

    private static void fixDown(int[] arr, int index, int from, int to) {
        assert arr != null;

        final int left = from + (2 * (index - from) + 1);
        int minIndex = index;

        // IMPORTANT: min heap here always has even number of children (0 or 2).
        if (left <= to) {

            // check left child
            if (arr[left] < arr[minIndex]) {
                minIndex = left;
            }

            final int right = from + (2 * (index - from) + 2);

            // check right child
            if (arr[right] < arr[minIndex]) {
                minIndex = right;
            }
        }

        if (minIndex != index) {
            ArrayUtils.swap(arr, index, minIndex);
            fixDown(arr, minIndex, from, to);
        }
    }

    private static void fixUp(int[] arr, int from, int index) {
        int curIndex = index;

        while (curIndex >= from) {
            int parent = curIndex / 2;

            if (arr[parent] <= arr[curIndex]) {
                break;
            }

            ArrayUtils.swap(arr, curIndex, parent);
            curIndex = parent;
        }
    }
}
