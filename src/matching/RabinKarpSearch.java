package matching;

import java.util.ArrayList;
import java.util.List;

public final class RabinKarpSearch {
    private static final long BASE = 257;
    private static final long MOD = 1_000_000_007;

    private RabinKarpSearch() {}

    public static MatchResult search(String text, String pattern) {
        if (text == null || pattern == null) throw new IllegalArgumentException("Strings must not be null");
        if (pattern.isEmpty()) return MatchResult.emptyPattern(text.length());
        int n = text.length(), m = pattern.length();
        if (m > n) return new MatchResult(List.of(), 0, 0);

        long patternHash = 0, windowHash = 0, highestPower = 1;
        for (int i = 0; i < m; i++) {
            patternHash = (patternHash * BASE + pattern.charAt(i)) % MOD;
            windowHash = (windowHash * BASE + text.charAt(i)) % MOD;
            if (i < m - 1) highestPower = (highestPower * BASE) % MOD;
        }

        List<Integer> indices = new ArrayList<>();
        long comparisons = 0, hashHits = 0;
        for (int start = 0; start <= n - m; start++) {
            if (patternHash == windowHash) {
                hashHits++;
                int matched = 0;
                while (matched < m) {
                    comparisons++;
                    if (text.charAt(start + matched) != pattern.charAt(matched)) break;
                    matched++;
                }
                if (matched == m) indices.add(start);
            }
            if (start < n - m) {
                long outgoing = text.charAt(start) * highestPower % MOD;
                windowHash = (windowHash - outgoing + MOD) % MOD;
                windowHash = (windowHash * BASE + text.charAt(start + m)) % MOD;
            }
        }
        return new MatchResult(List.copyOf(indices), comparisons, hashHits);
    }
}
