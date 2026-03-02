import java.util.concurrent.ConcurrentHashMap;

public class HashTableFundamentals {

    // Token bucket for each client
    class TokenBucket {
        int tokens;
        final int maxTokens;
        final int refillRate; // tokens per second
        long lastRefillTime;

        TokenBucket(int maxTokens, int refillRate) {
            this.maxTokens = maxTokens;
            this.refillRate = refillRate;
            this.tokens = maxTokens;
            this.lastRefillTime = System.currentTimeMillis();
        }

        synchronized boolean allowRequest() {
            refill();

            if (tokens > 0) {
                tokens--;
                return true;
            }
            return false;
        }

        private void refill() {
            long now = System.currentTimeMillis();
            long secondsPassed = (now - lastRefillTime) / 1000;

            if (secondsPassed > 0) {
                int tokensToAdd = (int) secondsPassed * refillRate;
                tokens = Math.min(maxTokens, tokens + tokensToAdd);
                lastRefillTime = now;
            }
        }

        int getTokens() {
            refill();
            return tokens;
        }
    }

    private ConcurrentHashMap<String, TokenBucket> clients;

    private final int MAX_REQUESTS = 1000;  // capacity
    private final int REFILL_RATE = 1000;   // tokens per second

    public HashTableFundamentals() {
        clients = new ConcurrentHashMap<>();
    }

    // rate limit check
    public String checkRateLimit(String clientId) {

        clients.putIfAbsent(clientId,
                new TokenBucket(MAX_REQUESTS, REFILL_RATE));

        TokenBucket bucket = clients.get(clientId);

        if (bucket.allowRequest()) {
            return "Allowed (" + bucket.getTokens() + " requests remaining)";
        } else {
            return "Denied — Rate limit exceeded. Try later.";
        }
    }

    // status info
    public String getRateLimitStatus(String clientId) {
        TokenBucket bucket = clients.get(clientId);

        if (bucket == null) return "Client not found";

        return "{used: " + (MAX_REQUESTS - bucket.getTokens()) +
                ", limit: " + MAX_REQUESTS +
                ", remaining: " + bucket.getTokens() + "}";
    }

    // demo
    public static void main(String[] args) {

        HashTableFundamentals limiter = new HashTableFundamentals();

        System.out.println(limiter.checkRateLimit("abc123"));
        System.out.println(limiter.checkRateLimit("abc123"));
        System.out.println(limiter.checkRateLimit("abc123"));

        // simulate many requests
        for (int i = 0; i < 1000; i++) {
            limiter.checkRateLimit("abc123");
        }

        System.out.println(limiter.checkRateLimit("abc123")); // denied
        System.out.println(limiter.getRateLimitStatus("abc123"));
    }
}