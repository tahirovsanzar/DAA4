import dp.EditDistance;
import dp.LCS;
import lcp.Kasai;
import lcp.SuffixArray;
import matching.KMPSearch;
import matching.MatchResult;
import matching.NaiveSearch;
import matching.RabinKarpSearch;

import java.util.Arrays;
import java.util.List;

public final class Tests {
    private static int checks;

    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) throw new AssertionError(message);
    }

    private static void matchingCase(String text, String pattern, List<Integer> expected) {
        MatchResult naive = NaiveSearch.search(text, pattern);
        MatchResult kmp = KMPSearch.search(text, pattern);
        MatchResult rabinKarp = RabinKarpSearch.search(text, pattern);
        check(naive.indices().equals(expected), "Naive failed for text=" + text + ", pattern=" + pattern);
        check(kmp.indices().equals(expected), "KMP failed for text=" + text + ", pattern=" + pattern);
        check(rabinKarp.indices().equals(expected), "Rabin-Karp failed for text=" + text + ", pattern=" + pattern);
        check(naive.indices().equals(kmp.indices()) && kmp.indices().equals(rabinKarp.indices()),
              "Matching algorithms disagree");
    }

    private static void dynamicProgrammingCases() {
        check(LCS.length("CITY", "CITY") == 4, "LCS identical strings");
        check(LCS.length("AAAA", "BBBB") == 0, "LCS different strings");
        check(LCS.length("", "CITY") == 0, "LCS empty first string");
        check(LCS.length("CITY", "") == 0, "LCS empty second string");
        check(LCS.length("AGGTAB", "GXTXAYB") == 4, "LCS subsequence");

        check(EditDistance.distance("CITY", "CITY") == 0, "Edit identical strings");
        check(EditDistance.distance("", "CITY") == 4, "Edit empty first string");
        check(EditDistance.distance("CITY", "") == 4, "Edit empty second string");
        check(EditDistance.distance("cat", "cut") == 1, "Edit substitution");
        check(EditDistance.distance("cat", "cart") == 1, "Edit insertion");
        check(EditDistance.distance("cart", "cat") == 1, "Edit deletion");
        check(EditDistance.distance("kitten", "sitting") == 3, "Edit mixed operations");
    }

    private static void stringMatchingCases() {
        matchingCase("abcxx", "abc", List.of(0));
        matchingCase("xxabcxx", "abc", List.of(2));
        matchingCase("xxabc", "abc", List.of(2));
        matchingCase("abcabcabc", "abc", List.of(0, 3, 6));
        matchingCase("aaaaa", "aaa", List.of(0, 1, 2));
        matchingCase("abcdef", "xyz", List.of());
        matchingCase("ab", "abcd", List.of());
        matchingCase("abc", "", List.of(0, 1, 2, 3));
        matchingCase("", "", List.of(0));
        matchingCase("", "a", List.of());
        matchingCase("ababababac", "ababac", List.of(4));
    }

    private static void kasaiCases() {
        int[] bananaSuffixes = {5, 3, 1, 0, 4, 2};
        check(Arrays.equals(SuffixArray.build("banana"), bananaSuffixes), "banana suffix array");
        check(Arrays.equals(Kasai.lcp("banana", bananaSuffixes), new int[]{0, 1, 3, 0, 0, 2}),
              "banana LCP array");
        check(Arrays.equals(Kasai.lcp("", new int[0]), new int[0]), "empty LCP array");
        check(Arrays.equals(Kasai.lcp("a", new int[]{0}), new int[]{0}), "single character LCP");
        check(Arrays.equals(Kasai.lcp("aaaa", new int[]{3, 2, 1, 0}), new int[]{0, 1, 2, 3}),
              "repeated characters LCP");
        boolean rejected = false;
        try {
            Kasai.lcp("abc", new int[]{0, 0, 2});
        } catch (IllegalArgumentException expected) {
            rejected = true;
        }
        check(rejected, "invalid suffix array accepted");
    }

    public static void main(String[] args) {
        dynamicProgrammingCases();
        stringMatchingCases();
        kasaiCases();
        System.out.println("PASS: " + checks + " checks");
    }
}
