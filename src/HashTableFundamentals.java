import java.util.*;

public class HashTableFundamentals {

    static class Transaction {
        int id;
        int amount;
        String merchant;
        String account;
        long time; // minutes since start of day

        Transaction(int id, int amount, String merchant, String account, long time) {
            this.id = id;
            this.amount = amount;
            this.merchant = merchant;
            this.account = account;
            this.time = time;
        }
    }

    private List<Transaction> transactions = new ArrayList<>();

    public void addTransaction(Transaction t) {
        transactions.add(t);
    }

    // Classic Two Sum
    public void findTwoSum(int target) {
        HashMap<Integer, Transaction> map = new HashMap<>();

        for (Transaction t : transactions) {
            int complement = target - t.amount;

            if (map.containsKey(complement)) {
                System.out.println("Two-Sum Found: " +
                        map.get(complement).id + " & " + t.id);
            }
            map.put(t.amount, t);
        }
    }

    // Two Sum within 1 hour window
    public void findTwoSumWithinHour(int target) {
        HashMap<Integer, Transaction> map = new HashMap<>();

        for (Transaction t : transactions) {
            int complement = target - t.amount;

            if (map.containsKey(complement)) {
                Transaction prev = map.get(complement);

                if (Math.abs(t.time - prev.time) <= 60) {
                    System.out.println("Within 1hr: " + prev.id + " & " + t.id);
                }
            }
            map.put(t.amount, t);
        }
    }

    // Detect duplicate payments (same amount + merchant, diff accounts)
    public void detectDuplicates() {
        HashMap<String, Set<String>> map = new HashMap<>();

        for (Transaction t : transactions) {
            String key = t.amount + "-" + t.merchant;

            map.putIfAbsent(key, new HashSet<>());
            map.get(key).add(t.account);
        }

        for (String key : map.keySet()) {
            if (map.get(key).size() > 1) {
                System.out.println("Duplicate payment pattern: " + key +
                        " accounts: " + map.get(key));
            }
        }
    }

    // K-Sum (find any K transactions = target)
    public void findKSum(int k, int target) {
        List<Integer> amounts = new ArrayList<>();
        for (Transaction t : transactions) amounts.add(t.amount);

        List<Integer> result = new ArrayList<>();
        kSumHelper(amounts, k, target, 0, result);
    }

    private void kSumHelper(List<Integer> nums, int k, int target,
                            int start, List<Integer> result) {

        if (k == 0 && target == 0) {
            System.out.println("K-Sum Found: " + result);
            return;
        }

        if (k == 0 || target < 0) return;

        for (int i = start; i < nums.size(); i++) {
            result.add(nums.get(i));
            kSumHelper(nums, k - 1, target - nums.get(i), i + 1, result);
            result.remove(result.size() - 1);
        }
    }

    // demo
    public static void main(String[] args) {

        HashTableFundamentals system = new HashTableFundamentals();

        system.addTransaction(new Transaction(1, 500, "StoreA", "acc1", 600));
        system.addTransaction(new Transaction(2, 300, "StoreB", "acc2", 615));
        system.addTransaction(new Transaction(3, 200, "StoreC", "acc3", 630));
        system.addTransaction(new Transaction(4, 500, "StoreA", "acc4", 640));

        system.findTwoSum(500);
        system.findTwoSumWithinHour(500);
        system.detectDuplicates();
        system.findKSum(3, 1000);
    }
}