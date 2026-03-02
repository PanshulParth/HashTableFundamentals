import java.util.*;

public class HashTableFundamentals {

    // L1 cache (fastest) — LRU using LinkedHashMap
    private LinkedHashMap<String, String> L1Cache;

    // L2 cache (SSD level)
    private HashMap<String, String> L2Cache;

    // L3 database (simulated)
    private HashMap<String, String> database;

    private HashMap<String, Integer> accessCount;

    private final int L1_CAPACITY = 3;
    private final int PROMOTION_THRESHOLD = 2;

    private int l1Hits = 0, l2Hits = 0, l3Hits = 0;

    public HashTableFundamentals() {

        L1Cache = new LinkedHashMap<String, String>(L1_CAPACITY, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
                return size() > L1_CAPACITY;
            }
        };

        L2Cache = new HashMap<>();
        database = new HashMap<>();
        accessCount = new HashMap<>();

        // preload database
        database.put("video_123", "Video Data A");
        database.put("video_456", "Video Data B");
        database.put("video_999", "Video Data C");
    }

    public String getVideo(String videoId) {

        // L1 check
        if (L1Cache.containsKey(videoId)) {
            l1Hits++;
            return "L1 Cache HIT (0.5ms)";
        }

        // L2 check
        if (L2Cache.containsKey(videoId)) {
            l2Hits++;
            promoteToL1(videoId);
            return "L2 Cache HIT (5ms) → Promoted to L1";
        }

        // L3 database
        if (database.containsKey(videoId)) {
            l3Hits++;
            L2Cache.put(videoId, database.get(videoId));
            updateAccess(videoId);
            return "L3 Database HIT (150ms) → Added to L2";
        }

        return "Video not found";
    }

    private void promoteToL1(String videoId) {
        updateAccess(videoId);

        if (accessCount.get(videoId) >= PROMOTION_THRESHOLD) {
            L1Cache.put(videoId, L2Cache.get(videoId));
        }
    }

    private void updateAccess(String videoId) {
        accessCount.put(videoId, accessCount.getOrDefault(videoId, 0) + 1);
    }

    public void getStatistics() {
        int total = l1Hits + l2Hits + l3Hits;

        System.out.println("\n=== CACHE STATS ===");

        if (total == 0) return;

        System.out.println("L1 Hit Rate: " + (l1Hits * 100 / total) + "%");
        System.out.println("L2 Hit Rate: " + (l2Hits * 100 / total) + "%");
        System.out.println("L3 Hit Rate: " + (l3Hits * 100 / total) + "%");

        double avgTime =
                (l1Hits * 0.5 + l2Hits * 5 + l3Hits * 150) / total;

        System.out.println("Overall Avg Time: " + avgTime + " ms");
    }

    // demo
    public static void main(String[] args) {

        HashTableFundamentals cache = new HashTableFundamentals();

        System.out.println(cache.getVideo("video_123")); // L3 → L2
        System.out.println(cache.getVideo("video_123")); // L2 → L1
        System.out.println(cache.getVideo("video_123")); // L1 hit

        System.out.println(cache.getVideo("video_999"));
        System.out.println(cache.getVideo("video_999"));

        cache.getStatistics();
    }
}