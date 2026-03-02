import java.util.*;

public class HashTableFundamentals {
    // Stores username -> userId
    private HashMap<String, Integer> users;

    // Tracks how many times usernames were attempted
    private HashMap<String, Integer> attempts;

    private int userIdCounter;

    public HashTableFundamentals() {
        users = new HashMap<>();
        attempts = new HashMap<>();
        userIdCounter = 1;

        // preloaded users (simulate existing database)
        users.put("john_doe", userIdCounter++);
        users.put("admin", userIdCounter++);
        users.put("thor", userIdCounter++);
    }

    // O(1) username check
    public boolean checkAvailability(String username) {
        trackAttempt(username);
        return !users.containsKey(username);
    }

    // register username
    public void registerUser(String username) {
        if (checkAvailability(username)) {
            users.put(username, userIdCounter++);
            System.out.println(username + " registered successfully.");
        } else {
            System.out.println(username + " is already taken.");
        }
    }

    // track popularity
    private void trackAttempt(String username) {
        attempts.put(username, attempts.getOrDefault(username, 0) + 1);
    }

    // suggest alternatives
    public List<String> suggestAlternatives(String username) {
        List<String> suggestions = new ArrayList<>();

        if (!users.containsKey(username)) {
            suggestions.add(username);
            return suggestions;
        }

        suggestions.add(username + "1");
        suggestions.add(username + "2");
        suggestions.add(username + "_official");
        suggestions.add(username.replace("_", "."));

        return suggestions;
    }

    // most attempted username
    public String getMostAttempted() {
        String popular = "";
        int max = 0;

        for (String name : attempts.keySet()) {
            if (attempts.get(name) > max) {
                max = attempts.get(name);
                popular = name;
            }
        }
        return popular + " (" + max + " attempts)";
    }

    // demo
    public static void main(String[] args) {

        HashTableFundamentals system = new HashTableFundamentals();

        System.out.println(system.checkAvailability("john_doe")); // false
        System.out.println(system.checkAvailability("jane_smith")); // true

        System.out.println(system.suggestAlternatives("john_doe"));

        system.checkAvailability("admin");
        system.checkAvailability("admin");
        system.checkAvailability("admin");

        System.out.println(system.getMostAttempted());
    }
}
