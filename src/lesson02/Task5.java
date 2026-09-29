package lesson02;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Task5 {
    public static void main(String[] args) {
        String text = "Java удобна для учёбы, а практика помогает изучать Java быстрее. "
                + "Практика делает код лучше.";

        String[] words = text.toLowerCase()
                .replaceAll("[^а-яёa-z\\s]", "")
                .split("\\s+");

        Map<String, Integer> wordFrequency = new HashMap<>();

        for (String word : words) {
            wordFrequency.put(word, wordFrequency.getOrDefault(word, 0) + 1);
        }

        List<Map.Entry<String, Integer>> sortedWords = new ArrayList<>(wordFrequency.entrySet());
        sortedWords.sort(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder())
                .thenComparing(Map.Entry.comparingByKey()));

        for (Map.Entry<String, Integer> entry : sortedWords) {
            System.out.printf("%s: %d%n", entry.getKey(), entry.getValue());
        }
    }
}
