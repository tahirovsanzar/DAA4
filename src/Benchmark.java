import dp.EditDistance;
import dp.LCS;
import lcp.Kasai;
import lcp.SuffixArray;
import matching.KMPSearch;
import matching.MatchResult;
import matching.NaiveSearch;
import matching.RabinKarpSearch;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.function.Supplier;

public final class Benchmark {
    private static final String ALPHABET = "ACGT";
    private static final long SEED = 20261107L;
    private static volatile Object sink;

    private record Measurement<T>(double averageNanoseconds, T result) {}

    private Benchmark() {}

    private static String randomText(int length, Random random) {
        StringBuilder text = new StringBuilder(length);
        for (int i = 0; i < length; i++) text.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        return text.toString();
    }

    private static <T> Measurement<T> measure(Supplier<T> algorithm) {
        for (int i = 0; i < 3; i++) sink = algorithm.get();
        long total = 0;
        T result = null;
        for (int i = 0; i < 5; i++) {
            long start = System.nanoTime();
            result = algorithm.get();
            total += System.nanoTime() - start;
            sink = result;
        }
        return new Measurement<>(total / 5.0, result);
    }

    private static void row(StringBuilder csv, String algorithm, String workload, int size, String variant,
                            Measurement<?> measurement, String operationType, long operations, String result) {
        csv.append(String.format(Locale.ROOT, "%s,%s,%d,%s,%.0f,%s,%d,%s%n",
                algorithm, workload, size, variant, measurement.averageNanoseconds(), operationType, operations, result));
    }

    private static void matchingRows(StringBuilder csv, int size, String variant, String text, String pattern) {
        Measurement<MatchResult> naive = measure(() -> NaiveSearch.search(text, pattern));
        Measurement<MatchResult> kmp = measure(() -> KMPSearch.search(text, pattern));
        Measurement<MatchResult> rabinKarp = measure(() -> RabinKarpSearch.search(text, pattern));
        List<Integer> expected = naive.result().indices();
        if (!expected.equals(kmp.result().indices()) || !expected.equals(rabinKarp.result().indices()))
            throw new AssertionError("Matching algorithms disagree for size=" + size + ", variant=" + variant);
        if (variant.equals("present") && !expected.contains(size / 2))
            throw new AssertionError("Present pattern was not found");
        if (variant.equals("absent") && !expected.isEmpty())
            throw new AssertionError("Absent pattern was found");
        row(csv, "Naive", "matching", size, variant, naive, "character_comparisons",
                naive.result().characterComparisons(), "matches=" + expected.size());
        row(csv, "KMP", "matching", size, variant, kmp, "character_comparisons",
                kmp.result().characterComparisons(), "matches=" + expected.size());
        row(csv, "RabinKarp", "matching", size, variant, rabinKarp, "verification_comparisons",
                rabinKarp.result().characterComparisons(),
                "matches=" + expected.size() + ";hash_hits=" + rabinKarp.result().hashHits());
    }

    public static void main(String[] args) throws IOException {
        Path output = Path.of(args.length == 0 ? "results/tables/benchmark.csv" : args[0]);
        Files.createDirectories(output.toAbsolutePath().getParent());
        StringBuilder csv = new StringBuilder("algorithm,workload,input_size,variant,avg_nanoseconds,operation_type,operations,result\n");

        for (int size : new int[]{100, 500, 1000}) {
            Random random = new Random(SEED + size);
            String first = randomText(size, random);
            String second = randomText(size, random);
            Measurement<Integer> lcs = measure(() -> LCS.length(first, second));
            Measurement<Integer> edit = measure(() -> EditDistance.distance(first, second));
            if (lcs.result() < 0 || lcs.result() > size || edit.result() < 0 || edit.result() > size)
                throw new AssertionError("Invalid dynamic programming result");
            row(csv, "LCS", "dynamic_programming", size, "random", lcs, "dp_cells", (long) size * size,
                    "length=" + lcs.result());
            row(csv, "EditDistance", "dynamic_programming", size, "random", edit, "dp_cells", (long) size * size,
                    "distance=" + edit.result());
        }

        String absentPattern = "T".repeat(24);
        for (int size : new int[]{10000, 50000, 100000}) {
            int attempt = 0;
            String text;
            do {
                text = randomText(size, new Random(SEED + size + attempt++));
            } while (text.contains(absentPattern));
            String presentPattern = text.substring(size / 2, size / 2 + 20);
            matchingRows(csv, size, "present", text, presentPattern);
            matchingRows(csv, size, "absent", text, absentPattern);
        }

        for (int size : new int[]{1000, 5000, 10000}) {
            String text = randomText(size, new Random(SEED + size));
            int[] suffixArray = SuffixArray.build(text);
            Measurement<int[]> kasai = measure(() -> Kasai.lcp(text, suffixArray));
            if (kasai.result().length != size || kasai.result()[0] != 0)
                throw new AssertionError("Invalid LCP array");
            row(csv, "Kasai", "lcp", size, "random", kasai, "suffixes_processed", size,
                    "lcp_length=" + kasai.result().length);
        }

        Files.writeString(output, csv.toString());
        System.out.print(csv);
    }
}
