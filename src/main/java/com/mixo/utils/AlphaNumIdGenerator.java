package com.mixo.utils;

import java.security.SecureRandom;

public class AlphaNumIdGenerator {

	private static final String ALPHANUM = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

	// SecureRandom instance to ensure randomness
	private static final SecureRandom RANDOM = new SecureRandom();

	public static String generateId(int size) {
		if (size <= 0) {
			throw new IllegalArgumentException("Size must be greater than 0");
		}

		StringBuilder sb = new StringBuilder(size);
		for (int i = 0; i < size; i++) {
			int index = RANDOM.nextInt(ALPHANUM.length());
			sb.append(ALPHANUM.charAt(index));
		}

		return sb.toString();
	}

}
