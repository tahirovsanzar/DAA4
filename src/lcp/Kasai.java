package lcp;

public final class Kasai {
    private Kasai() {}

    public static int[] lcp(String text, int[] suffixArray) {
        if (text == null || suffixArray == null || suffixArray.length != text.length())
            throw new IllegalArgumentException("Suffix array length must match text length");
        int n = text.length();
        int[] rank = new int[n];
        int[] lcp = new int[n];
        boolean[] seen = new boolean[n];
        for (int i = 0; i < n; i++) {
            int suffix = suffixArray[i];
            if (suffix < 0 || suffix >= n || seen[suffix])
                throw new IllegalArgumentException("Invalid suffix array");
            seen[suffix] = true;
            rank[suffix] = i;
        }
        int common = 0;
        for (int i = 0; i < n; i++) {
            int position = rank[i];
            if (position == 0) {
                common = 0;
                continue;
            }
            int previousSuffix = suffixArray[position - 1];
            while (i + common < n && previousSuffix + common < n &&
                   text.charAt(i + common) == text.charAt(previousSuffix + common)) {
                common++;
            }
            lcp[position] = common;
            if (common > 0) common--;
        }
        return lcp;
    }
}
