import java.util.*;

public class HashTableFundamentals {

    // DNS Entry class
    class DNSEntry {
        String ipAddress;
        long expiryTime;

        DNSEntry(String ipAddress, long ttlSeconds) {
            this.ipAddress = ipAddress;
            this.expiryTime = System.currentTimeMillis() + ttlSeconds * 1000;
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
    }

    // LRU Cache using LinkedHashMap
    private LinkedHashMap<String, DNSEntry> cache;
    private int capacity;

    private int hits = 0;
    private int misses = 0;

    public HashTableFundamentals(int capacity) {
        this.capacity = capacity;

        cache = new LinkedHashMap<String, DNSEntry>(capacity, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<String, DNSEntry> eldest) {
                return size() > HashTableFundamentals.this.capacity;
            }
        };

        startCleanupThread();
    }

    // resolve domain
    public synchronized String resolve(String domain) {

        if (cache.containsKey(domain)) {
            DNSEntry entry = cache.get(domain);

            if (!entry.isExpired()) {
                hits++;
                return "Cache HIT → " + entry.ipAddress;
            } else {
                cache.remove(domain);
            }
        }

        misses++;

        // simulate upstream DNS query
        String ip = queryUpstreamDNS(domain);

        // store with TTL = 5 seconds (demo)
        cache.put(domain, new DNSEntry(ip, 5));

        return "Cache MISS → Fetched IP: " + ip;
    }

    // simulate upstream DNS
    private String queryUpstreamDNS(String domain) {
        Random rand = new Random();
        return "172.217.14." + rand.nextInt(255);
    }

    // background thread to clean expired entries
    private void startCleanupThread() {
        Thread cleaner = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(3000);
                    cleanExpiredEntries();
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        cleaner.setDaemon(true);
        cleaner.start();
    }

    private synchronized void cleanExpiredEntries() {
        Iterator<Map.Entry<String, DNSEntry>> it = cache.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, DNSEntry> entry = it.next();
            if (entry.getValue().isExpired()) {
                it.remove();
            }
        }
    }

    public void getCacheStats() {
        int total = hits + misses;
        double hitRate = total == 0 ? 0 : (hits * 100.0) / total;

        System.out.println("Hits: " + hits);
        System.out.println("Misses: " + misses);
        System.out.printf("Hit Rate: %.2f%%\n", hitRate);
    }

    // demo
    public static void main(String[] args) throws InterruptedException {

        HashTableFundamentals dnsCache = new HashTableFundamentals(3);

        System.out.println(dnsCache.resolve("google.com"));
        System.out.println(dnsCache.resolve("google.com")); // hit

        Thread.sleep(6000); // wait for TTL expiry

        System.out.println(dnsCache.resolve("google.com")); // expired

        dnsCache.resolve("facebook.com");
        dnsCache.resolve("amazon.com");
        dnsCache.resolve("openai.com"); // triggers LRU eviction

        dnsCache.getCacheStats();
    }
}