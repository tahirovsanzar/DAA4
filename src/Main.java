import dp.EditDistance;
import dp.LCS;
import lcp.Kasai;
import lcp.SuffixArray;
import matching.KMPSearch;
import matching.NaiveSearch;
import matching.RabinKarpSearch;

import java.util.Arrays;

public final class Main {
    public static void main(String[] args) {
        String first = "CITY";
        String second = "CITIES";
        System.out.println("LCS length: " + LCS.length(first, second));
        System.out.println("Edit distance: " + EditDistance.distance(first, second));

        String text = "CITY CITY SMART CITY";
        String pattern = "CITY";
        System.out.println("Naive matches: " + NaiveSearch.search(text, pattern).indices());
        System.out.println("KMP matches: " + KMPSearch.search(text, pattern).indices());
        System.out.println("Rabin-Karp matches: " + RabinKarpSearch.search(text, pattern).indices());

        String sample = "banana";
        int[] suffixArray = SuffixArray.build(sample);
        System.out.println("Suffix array for banana: " + Arrays.toString(suffixArray));
        System.out.println("LCP array for banana: " + Arrays.toString(Kasai.lcp(sample, suffixArray)));
    }
}
