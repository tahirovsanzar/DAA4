package matching;

import java.util.ArrayList;
import java.util.List;

public record MatchResult(List<Integer> indices, long characterComparisons, long hashHits) {
    public static MatchResult emptyPattern(int textLength) {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i <= textLength; i++) indices.add(i);
        return new MatchResult(List.copyOf(indices), 0, 0);
    }
}
