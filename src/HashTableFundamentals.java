import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class HashTableFundamentals {

    // productId -> stock count
    private ConcurrentHashMap<String, AtomicInteger> inventory;

    // productId -> waiting list (FIFO)
    private ConcurrentHashMap<String, Queue<Integer>> waitingList;

    public HashTableFundamentals() {
        inventory = new ConcurrentHashMap<>();
        waitingList = new ConcurrentHashMap<>();

        // preload product stock
        inventory.put("IPHONE15_256GB", new AtomicInteger(100));
        waitingList.put("IPHONE15_256GB", new LinkedList<>());
    }

    // instant stock check (O(1))
    public int checkStock(String productId) {
        AtomicInteger stock = inventory.get(productId);
        return stock == null ? 0 : stock.get();
    }

    // purchase item (thread-safe)
    public synchronized String purchaseItem(String productId, int userId) {

        inventory.putIfAbsent(productId, new AtomicInteger(0));
        waitingList.putIfAbsent(productId, new LinkedList<>());

        AtomicInteger stock = inventory.get(productId);

        if (stock.get() > 0) {
            int remaining = stock.decrementAndGet();
            return "Success! User " + userId +
                    " purchased item. Remaining stock: " + remaining;
        } else {
            Queue<Integer> queue = waitingList.get(productId);
            queue.add(userId);
            return "Out of stock. User " + userId +
                    " added to waiting list. Position: " + queue.size();
        }
    }

    // view waiting list
    public void showWaitingList(String productId) {
        Queue<Integer> queue = waitingList.get(productId);
        System.out.println("Waiting List: " + queue);
    }

    // demo simulation
    public static void main(String[] args) {

        HashTableFundamentals system = new HashTableFundamentals();

        System.out.println("Stock: " + system.checkStock("IPHONE15_256GB"));

        System.out.println(system.purchaseItem("IPHONE15_256GB", 12345));
        System.out.println(system.purchaseItem("IPHONE15_256GB", 67890));

        // simulate stock finishing quickly
        for (int i = 1; i <= 100; i++) {
            system.purchaseItem("IPHONE15_256GB", i);
        }

        System.out.println(system.purchaseItem("IPHONE15_256GB", 99999));

        system.showWaitingList("IPHONE15_256GB");
    }
}