package lcp;

import java.util.Arrays;

public final class SuffixArray {
    private SuffixArray() {}

    public static int[] build(String text) {
        if (text == null) throw new IllegalArgumentException("Text must not be null");
        Integer[] positions = new Integer[text.length()];
        for (int i = 0; i < text.length(); i++) positions[i] = i;
        Arrays.sort(positions, (left, right) -> {
            int a = left, b = right;
            while (a < text.length() && b < text.length()) {
                int compared = Character.compare(text.charAt(a), text.charAt(b));
                if (compared != 0) return compared;
                a++;
                b++;
            }
            return Integer.compare(text.length() - left, text.length() - right);
        });
        int[] suffixArray = new int[positions.length];
        for (int i = 0; i < positions.length; i++) suffixArray[i] = positions[i];
        return suffixArray;
    }
}
