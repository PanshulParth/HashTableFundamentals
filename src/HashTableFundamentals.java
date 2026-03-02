import java.util.*;

public class HashTableFundamentals {

    // page -> total visits
    private HashMap<String, Integer> pageViews;

    // page -> unique visitors
    private HashMap<String, HashSet<String>> uniqueVisitors;

    // traffic source -> count
    private HashMap<String, Integer> trafficSources;

    public HashTableFundamentals() {
        pageViews = new HashMap<>();
        uniqueVisitors = new HashMap<>();
        trafficSources = new HashMap<>();
    }

    // process page view event
    public void processEvent(String pageUrl, String userId, String source) {

        // count page views
        pageViews.put(pageUrl, pageViews.getOrDefault(pageUrl, 0) + 1);

        // track unique visitors
        uniqueVisitors.putIfAbsent(pageUrl, new HashSet<>());
        uniqueVisitors.get(pageUrl).add(userId);

        // track traffic source
        trafficSources.put(source, trafficSources.getOrDefault(source, 0) + 1);
    }

    // get top N pages
    public List<String> getTopPages(int n) {
        PriorityQueue<Map.Entry<String, Integer>> pq =
                new PriorityQueue<>((a, b) -> b.getValue() - a.getValue());

        pq.addAll(pageViews.entrySet());

        List<String> result = new ArrayList<>();

        int rank = 1;
        while (rank <= n && !pq.isEmpty()) {
            Map.Entry<String, Integer> entry = pq.poll();
            String page = entry.getKey();
            int views = entry.getValue();
            int unique = uniqueVisitors.get(page).size();

            result.add(rank + ". " + page + " - " + views +
                    " views (" + unique + " unique)");
            rank++;
        }

        return result;
    }

    // display dashboard
    public void getDashboard() {

        System.out.println("\n=== REAL-TIME DASHBOARD ===");

        System.out.println("\nTop Pages:");
        for (String page : getTopPages(10)) {
            System.out.println(page);
        }

        System.out.println("\nTraffic Sources:");
        for (String src : trafficSources.keySet()) {
            System.out.println(src + " : " + trafficSources.get(src));
        }
    }

    // demo
    public static void main(String[] args) throws InterruptedException {

        HashTableFundamentals dashboard = new HashTableFundamentals();

        dashboard.processEvent("/article/breaking-news", "user123", "google");
        dashboard.processEvent("/article/breaking-news", "user456", "facebook");
        dashboard.processEvent("/sports/championship", "user789", "google");
        dashboard.processEvent("/article/breaking-news", "user123", "direct");
        dashboard.processEvent("/sports/championship", "user111", "google");
        dashboard.processEvent("/tech/ai-future", "user222", "twitter");

        dashboard.getDashboard();
    }
}