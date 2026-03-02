import java.util.*;

public class HashTableFundamentals {

    // Trie Node
    class TrieNode {
        HashMap<Character, TrieNode> children = new HashMap<>();
        boolean isEnd = false;
    }

    private TrieNode root;

    // query -> frequency
    private HashMap<String, Integer> frequencyMap;

    public HashTableFundamentals() {
        root = new TrieNode();
        frequencyMap = new HashMap<>();
    }

    // insert query into trie
    public void insert(String query) {
        TrieNode node = root;

        for (char ch : query.toCharArray()) {
            node.children.putIfAbsent(ch, new TrieNode());
            node = node.children.get(ch);
        }
        node.isEnd = true;

        frequencyMap.put(query, frequencyMap.getOrDefault(query, 0) + 1);
    }

    // update frequency when searched again
    public void updateFrequency(String query) {
        insert(query);
    }

    // search suggestions
    public List<String> search(String prefix) {
        TrieNode node = root;

        for (char ch : prefix.toCharArray()) {
            if (!node.children.containsKey(ch))
                return new ArrayList<>();
            node = node.children.get(ch);
        }

        List<String> results = new ArrayList<>();
        dfs(node, prefix, results);

        // sort by frequency descending
        results.sort((a, b) -> frequencyMap.get(b) - frequencyMap.get(a));

        return results.size() > 10 ? results.subList(0, 10) : results;
    }

    // DFS to collect words
    private void dfs(TrieNode node, String word, List<String> results) {
        if (node.isEnd)
            results.add(word);

        for (char ch : node.children.keySet()) {
            dfs(node.children.get(ch), word + ch, results);
        }
    }

    // demo
    public static void main(String[] args) {

        HashTableFundamentals auto = new HashTableFundamentals();

        auto.insert("java tutorial");
        auto.insert("java tutorial");
        auto.insert("javascript");
        auto.insert("java download");
        auto.insert("java stream api");
        auto.insert("java interview questions");

        auto.updateFrequency("java tutorial");

        System.out.println("Suggestions for 'jav':");
        List<String> suggestions = auto.search("jav");

        for (String s : suggestions) {
            System.out.println(s);
        }
    }
}