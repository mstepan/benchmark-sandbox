import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

final class BenchmarkMain {

    private BenchmarkMain() throws Exception {

        Map<Integer, String> data = new TreeMap<>();

        for (int i = 0; i < 100; ++i) {
            data.put(i, "value-" + i);
        }

        List<Integer> filteredData =
                data.keySet().stream().
                        filter(key -> key > 10).
                        collect(Collectors.toList());

        System.out.println(filteredData);

        System.out.println("java: " + System.getProperty("java.version"));
    }

    public static void main(String[] args) {
        try {
            new BenchmarkMain();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
