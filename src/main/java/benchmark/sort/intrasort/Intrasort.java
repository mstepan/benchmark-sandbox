package benchmark.sort.intrasort;

import java.util.ArrayDeque;
import java.util.Deque;

import static com.google.common.base.Preconditions.checkArgument;

final class Intrasort {

    private static final int INSERTION_SORT_THRESHOLD = 32;

    private final InsertionSort insertionSortStrategy;

    public Intrasort(InsertionSort insertionSortStrategy) {
        this.insertionSortStrategy = insertionSortStrategy;
    }

    /**
     * Classic intrasort (similar to STL implementation) that uses quicksort and switch
     * to heapsort as soon as the stack size is greater or equal to log N.
     */
    public void intrasort(int[] arr) {
        checkArgument(arr != null, "null 'arr' parameter detected");

        if (arr.length < 2) {
            return;
        }

        final int fromIndex = 0;
        final int toIndex = arr.length - 1;

        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(fromIndex);
        stack.push(toIndex);

        while (!stack.isEmpty()) {

            final int to = stack.pop();
            final int from = stack.pop();

            int elemsCount = to - from + 1;

            // for a small subarray use insertion sort
            if (elemsCount <= INSERTION_SORT_THRESHOLD) {
                insertionSortStrategy.sort(arr, from, to);
                continue;
            }

            // number of subarrays in stack to be sorted is equals to 'stack.size() / 2', because we store
            // 'to' and 'from' in stack directly
            if (stack.size() / 2 >= log2(arr.length)) {
                HeapSort.sort(arr, from, to);
                continue;
            }

            final int pivotIndex = partition(arr, from, to);

            int leftSize = pivotIndex - from;

            // We can push bigger partition to stack first, in this way we will always guaranteed to
            // have log N stack size, but in this case we won't be able to test our heapsort branch. )))
            if (leftSize > 1) {
                stack.push(from);
                stack.push(pivotIndex - 1);
            }

            int rightSize = to - pivotIndex;
            if (rightSize > 1) {
                stack.push(pivotIndex + 1);
                stack.push(to);
            }
        }
    }

    private static int log2(int n) {
        return (int) (Math.log(n) / Math.log(2)) + 1;
    }

    private static int partition(int[] arr, int from, int to) {
        int pivot = arr[to];

        int boundary = from - 1;

        for (int i = from; i < to; ++i) {
            if (arr[i] <= pivot) {
                swap(arr, boundary + 1, i);
                ++boundary;
            }
        }

        swap(arr, boundary + 1, to);
        return boundary + 1;
    }

    private static void swap(int[] arr, int from, int to) {
        assert arr != null : "null 'arr' passed";
        assert from >= 0 && from < arr.length : "'from' out of bound";
        assert to >= 0 && to < arr.length : "'to' out of bound";

        int temp = arr[from];
        arr[from] = arr[to];
        arr[to] = temp;
    }
}
