package matching;

import java.util.ArrayList;
import java.util.List;

public final class KMPSearch {
    private KMPSearch() {}

    public static MatchResult search(String text, String pattern) {
        if (text == null || pattern == null) throw new IllegalArgumentException("Strings must not be null");
        if (pattern.isEmpty()) return MatchResult.emptyPattern(text.length());
        int[] lps = new int[pattern.length()];
        long comparisons = 0;
        int prefix = 0;
        for (int i = 1; i < pattern.length();) {
            comparisons++;
            if (pattern.charAt(i) == pattern.charAt(prefix)) {
                lps[i++] = ++prefix;
            } else if (prefix > 0) {
                prefix = lps[prefix - 1];
            } else {
                lps[i++] = 0;
            }
        }

        List<Integer> indices = new ArrayList<>();
        int textIndex = 0, patternIndex = 0;
        while (textIndex < text.length()) {
            comparisons++;
            if (text.charAt(textIndex) == pattern.charAt(patternIndex)) {
                textIndex++;
                patternIndex++;
                if (patternIndex == pattern.length()) {
                    indices.add(textIndex - patternIndex);
                    patternIndex = lps[patternIndex - 1];
                }
            } else if (patternIndex > 0) {
                patternIndex = lps[patternIndex - 1];
            } else {
                textIndex++;
            }
        }
        return new MatchResult(List.copyOf(indices), comparisons, 0);
    }
}
