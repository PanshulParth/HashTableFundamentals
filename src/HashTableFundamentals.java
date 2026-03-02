import java.util.*;

public class HashTableFundamentals {

    // n-gram -> set of document names
    private HashMap<String, Set<String>> index;

    private int N = 5; // 5-gram (recommended)

    public HashTableFundamentals() {
        index = new HashMap<>();
    }

    // create n-grams from document text
    private List<String> generateNGrams(String text) {
        List<String> ngrams = new ArrayList<>();
        String[] words = text.toLowerCase().replaceAll("[^a-z0-9 ]", "").split("\\s+");

        for (int i = 0; i <= words.length - N; i++) {
            StringBuilder gram = new StringBuilder();
            for (int j = 0; j < N; j++) {
                gram.append(words[i + j]).append(" ");
            }
            ngrams.add(gram.toString().trim());
        }
        return ngrams;
    }

    // add document to database
    public void addDocument(String docName, String text) {
        List<String> grams = generateNGrams(text);

        for (String gram : grams) {
            index.putIfAbsent(gram, new HashSet<>());
            index.get(gram).add(docName);
        }
    }

    // analyze document similarity
    public void analyzeDocument(String docName, String text) {

        List<String> grams = generateNGrams(text);
        Map<String, Integer> matchCount = new HashMap<>();

        for (String gram : grams) {
            if (index.containsKey(gram)) {
                for (String existingDoc : index.get(gram)) {
                    if (!existingDoc.equals(docName)) {
                        matchCount.put(existingDoc,
                                matchCount.getOrDefault(existingDoc, 0) + 1);
                    }
                }
            }
        }

        System.out.println("Extracted " + grams.size() + " n-grams");

        for (String doc : matchCount.keySet()) {
            int matches = matchCount.get(doc);
            double similarity = (matches * 100.0) / grams.size();

            System.out.println("Found " + matches +
                    " matching n-grams with \"" + doc + "\"");

            System.out.printf("Similarity: %.1f%%", similarity);

            if (similarity > 30)
                System.out.println("  → PLAGIARISM DETECTED");
            else if (similarity > 10)
                System.out.println("  → suspicious");
            else
                System.out.println("  → safe");
        }
    }

    // demo
    public static void main(String[] args) {

        HashTableFundamentals detector = new HashTableFundamentals();

        String doc1 = "Data structures and algorithms are important for computer science students.";
        String doc2 = "Algorithms and data structures are essential topics in computer science.";
        String doc3 = "Football is a popular sport played worldwide.";

        detector.addDocument("essay_089.txt", doc1);
        detector.addDocument("essay_092.txt", doc2);
        detector.addDocument("essay_200.txt", doc3);

        String newDoc = "Data structures and algorithms are essential for computer science.";

        detector.analyzeDocument("essay_123.txt", newDoc);
    }
}