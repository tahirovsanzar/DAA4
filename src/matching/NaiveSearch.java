package matching;

import java.util.ArrayList;
import java.util.List;

public final class NaiveSearch {
    private NaiveSearch() {}

    public static MatchResult search(String text, String pattern) {
        if (text == null || pattern == null) throw new IllegalArgumentException("Strings must not be null");
        if (pattern.isEmpty()) return MatchResult.emptyPattern(text.length());
        List<Integer> indices = new ArrayList<>();
        long comparisons = 0;
        for (int start = 0; start <= text.length() - pattern.length(); start++) {
            int matched = 0;
            while (matched < pattern.length()) {
                comparisons++;
                if (text.charAt(start + matched) != pattern.charAt(matched)) break;
                matched++;
            }
            if (matched == pattern.length()) indices.add(start);
        }
        return new MatchResult(List.copyOf(indices), comparisons, 0);
    }
}
