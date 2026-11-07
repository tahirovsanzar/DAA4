package dp;

public final class EditDistance {
    private EditDistance() {}

    public static int distance(String first, String second) {
        if (first == null || second == null) throw new IllegalArgumentException("Strings must not be null");
        int[][] dp = new int[first.length() + 1][second.length() + 1];
        for (int i = 0; i <= first.length(); i++) dp[i][0] = i;
        for (int j = 0; j <= second.length(); j++) dp[0][j] = j;
        for (int i = 1; i <= first.length(); i++) {
            for (int j = 1; j <= second.length(); j++) {
                if (first.charAt(i - 1) == second.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    int deletion = dp[i - 1][j];
                    int insertion = dp[i][j - 1];
                    int substitution = dp[i - 1][j - 1];
                    dp[i][j] = 1 + Math.min(deletion, Math.min(insertion, substitution));
                }
            }
        }
        return dp[first.length()][second.length()];
    }
}
