package com.zodiackillerciphers.tests.samblake;

import java.util.List;

public class Cipher {

	public static String tab = "	";
	public String name; // ciphertext file (excluding suffix)
	public String cipherText; // ciphertext

	public int zkScore; // score from zkdecrypto
	public String zkPlaintext; // plaintext from zkdecrypto
	public String zkPlaintextWithBreaks; // plaintext with guessed word breaks
	public List<String> zkPlaintextWords; // plaintext with only recognized words zodiac actually used in letters
	public float zkZodiacWordScore; // sum of relative Zodiac word scores based on English frequencies
	public float zkZodiacWordScoreAscending; // sum of relative word scores based on English INfrequencies

	public int azScore;
	public String azPlaintext;
	public String azPlaintextWithBreaks;
	public List<String> azPlaintextWords; //  plaintext with only recognized words zodiac actually used in letters
	public float azZodiacWordScore; // sum of relative word scores based on English frequencies
	public float azZodiacWordScoreAscending; // sum of relative word scores based on English INfrequencies

	public int n2reps;
	public int n3reps;
	public int n4reps;
	public double pcs2;
	
	float zkScore() {
		if (zkPlaintext == null || zkPlaintext.isEmpty()) return 0;
		float result = zkScore;
		result /= zkPlaintext.length();
		return result;
	}
	float zkZodiacWordScore() {
		if (zkPlaintext == null || zkPlaintext.isEmpty()) return 0;
		float result = zkZodiacWordScore;
		result /= zkPlaintext.length();
		return result;
	}
	float zkZodiacWordScoreAscending() {
		if (zkPlaintext == null || zkPlaintext.isEmpty()) return 0;
		float result = zkZodiacWordScoreAscending;
		result /= zkPlaintext.length();
		return result;
	}
	
	float azScore() {
		if (azPlaintext == null || azPlaintext.isEmpty()) return 0;
		float result = azScore;
		result /= azPlaintext.length();
		return result;
	}
	float azZodiacWordScore() {
		if (azPlaintext == null || azPlaintext.isEmpty()) return 0;
		float result = azZodiacWordScore;
		result /= azPlaintext.length();
		return result;
	}
	float azZodiacWordScoreAscending() {
		if (azPlaintext == null || azPlaintext.isEmpty()) return 0;
		float result = azZodiacWordScoreAscending;
		result /= azPlaintext.length();
		return result;
	}

	public String toString() {
		return name + tab + "=(\"" + cipherText + "\")" + tab + zkScore() + tab + zkPlaintext + tab + zkPlaintextWithBreaks + tab
				+ zkPlaintextWords + tab + zkZodiacWordScore() + tab + zkZodiacWordScoreAscending() + tab + azScore() + tab
				+ azPlaintext + tab + azPlaintextWithBreaks + tab + azPlaintextWords + tab + azZodiacWordScore() + tab
				+ azZodiacWordScoreAscending() + tab + n2reps + tab + n3reps + tab + n4reps + tab + pcs2;
	}
}
