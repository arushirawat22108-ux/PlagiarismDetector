package com.plagcoders.preprocessing;

import java.util.*;

/**
 * TextPreprocessor – Lowercase → punctuation removal → tokenise → stopword removal → n-grams
 * Team: PlagCoders | TCS-408 | JAVA-IV-T167
 */
public class TextPreprocessor {

    private static final Set<String> STOP = new HashSet<>(Arrays.asList(
        "a","an","the","and","or","but","is","are","was","were","be","been","being",
        "have","has","had","do","does","did","will","would","could","should","may",
        "might","shall","must","can","to","of","in","for","on","with","at","by",
        "from","as","into","through","before","after","above","below","between",
        "out","off","over","under","then","once","here","there","when","where",
        "why","how","all","both","each","few","more","most","other","some","such",
        "no","not","only","same","so","than","too","very","just","if","while",
        "this","that","these","those","i","me","my","we","our","you","your","he",
        "him","his","she","her","it","its","they","their","what","which","who",
        "about","any","also","used","using","based","new","one","two","three",
        "well","way","use","make","like","time","see","get","good","also","many"
    ));

    public List<String> preprocess(String text) {
        if (text == null || text.isBlank()) return Collections.emptyList();
        String cleaned = text.toLowerCase()
                             .replaceAll("[^a-z\\s]", " ")
                             .replaceAll("\\s+", " ").trim();
        List<String> tokens = new ArrayList<>();
        for (String t : cleaned.split(" "))
            if (t.length() > 2 && !STOP.contains(t)) tokens.add(t);
        return tokens;
    }

    public List<String> generateNgrams(List<String> tokens, int n) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i <= tokens.size() - n; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < n; j++) { if (j > 0) sb.append(' '); sb.append(tokens.get(i+j)); }
            result.add(sb.toString());
        }
        return result;
    }

    public Map<String, Double> buildTfVector(List<String> tokens) {
        Map<String, Integer> freq = new LinkedHashMap<>();
        for (String t : tokens) freq.merge(t, 1, Integer::sum);
        double total = tokens.isEmpty() ? 1 : tokens.size();
        Map<String, Double> tf = new LinkedHashMap<>();
        freq.forEach((k, v) -> tf.put(k, v / total));
        return tf;
    }

    public Map<String, Integer> buildFreqMap(List<String> tokens) {
        Map<String, Integer> freq = new LinkedHashMap<>();
        for (String t : tokens) freq.merge(t, 1, Integer::sum);
        return freq;
    }
}
