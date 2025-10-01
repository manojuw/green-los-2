package com.mixo.utils;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NameMatch {
	private static final Set<String> SALUTATIONS = new HashSet<>(
			Arrays.asList("mr", "mrs", "ms", "miss", "dr", "shri", "smt"));

//	public static void main(String[] args) {
//		String panName = "Mr. Ramesh Kumar JHa";
//		String bankName = "Ramesh Kumar";
//
//		boolean isMatch = isNameMatching(panName, bankName, 60);
//		System.out.println("Name Match: " + isMatch);
//	}

	public static boolean isNameMatching(String panName, String bankName, int threshold) {
		String cleanPanName = cleanName(panName);
		String cleanBankName = cleanName(bankName);

		int matchPercentage = calculateMatchPercentage(cleanPanName, cleanBankName);

		log.info("Match Percentage: " + matchPercentage + "%");

		return matchPercentage >= threshold;
	}

	private static String cleanName(String name) {
		if (name == null)
			return "";

		name = Normalizer.normalize(name, Normalizer.Form.NFD).replaceAll("[^a-zA-Z ]", "") // Remove special characters
																							// and numbers
				.toLowerCase().trim();

		String[] words = name.split(" ");
		StringBuilder cleanedName = new StringBuilder();

		for (String word : words) {
			if (!word.isEmpty() && !SALUTATIONS.contains(word)) {
				cleanedName.append(word).append(" ");
			}
		}
		return cleanedName.toString().trim();
	}

	private static int calculateMatchPercentage(String str1, String str2) {
		int maxLength = Math.max(str1.length(), str2.length());
		if (maxLength == 0)
			return 100;

		int distance = levenshteinDistance(str1, str2);
		return (int) (((maxLength - distance) / (double) maxLength) * 100);
	}

	private static int levenshteinDistance(String s1, String s2) {
		int[][] dp = new int[s1.length() + 1][s2.length() + 1];

		for (int i = 0; i <= s1.length(); i++) {
			for (int j = 0; j <= s2.length(); j++) {
				if (i == 0) {
					dp[i][j] = j;
				} else if (j == 0) {
					dp[i][j] = i;
				} else {
					dp[i][j] = Math.min(dp[i - 1][j - 1] + (s1.charAt(i - 1) == s2.charAt(j - 1) ? 0 : 1),
							Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1));
				}
			}
		}
		return dp[s1.length()][s2.length()];
	}
}
