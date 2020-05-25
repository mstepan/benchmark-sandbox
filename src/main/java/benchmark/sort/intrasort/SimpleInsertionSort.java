package benchmark.sort.intrasort;

final class SimpleInsertionSort implements InsertionSort {

    @Override
    public void sort(int[] arr, int from, int to) {
        assert arr != null : "null 'arr' detected";

        int temp;
        for (int i = from + 1; i <= to; ++i) {

            temp = arr[i];

            int j = i - 1;

            while (j >= 0 && arr[j] > temp) {
                arr[j + 1] = arr[j];
                --j;
            }

            arr[j + 1] = temp;
        }
    }
}
