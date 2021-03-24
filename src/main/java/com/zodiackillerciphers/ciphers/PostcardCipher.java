package com.zodiackillerciphers.ciphers;

import java.util.Map;

import com.zodiackillerciphers.dictionary.Solver;
import com.zodiackillerciphers.dictionary.WordFrequencies;
import com.zodiackillerciphers.io.FileUtil;

//http://scienceblogs.de/klausis-krypto-kolumne/2018/05/11/an-encrypted-postcard-with-only-17-letters/#comment-981951

// LSMB GRDQ HBP CPHUMB
public class PostcardCipher {
	public static void postProcess() {
		WordFrequencies.init();
		for (String line : FileUtil.loadFrom("/Users/doranchak/projects/zodiac/postcard-cipher-results-sorted.txt")) {
			String[] split = line.split(" ");
			long score = Long.valueOf(split[0]);
			for (String word : WordFrequencies.map.keySet()) {
				if (word.length() == 4) {
					if (word.substring(2).equals(split[2].substring(4))) {
						long score2 = score * (1+WordFrequencies.percentile(word));
						System.out.println(score2 + " " + word + " ____ " + split[1] + " " + split[2]);
					}
				}
			}

		}
	}
	public static void postProcess2() {
		WordFrequencies.init();
		String cipher = "CPHUMB LSMB HBP";
		for (String line : FileUtil.loadFrom("/Users/doranchak/projects/zodiac/postcard-cipher-results-2-sorted.txt")) {
			String[] split = line.split(" ");
			long score = Long.valueOf(split[0]);
			Map<Character, Character> map = Ciphers.decoderMapFor(null, cipher, split[1] + " " + split[2] + " " + split[3]);
			if (map == null) throw new RuntimeException("why null??? " + line);
			System.out.println(score + " " + Ciphers.decode("LSMB GRDQ HBP CPHUMB", map) + " " + Ciphers.isHomophonic(map));
		}
	}

	public static void search() {
		WordFrequencies.init();
		for (String word : WordFrequencies.map.keySet()) {
			if (word.length() == 6) {
				for (String word2 : WordFrequencies.map.keySet()) {
					if (word2.length() == 3) {
						// CPHUMB HBP
						// 012345 012
						if (word.charAt(1) == word2.charAt(2) && word.charAt(2) == word2.charAt(0)
								&& word.charAt(5) == word2.charAt(1)) {
							long score = (1+WordFrequencies.percentile(word)) * (1+WordFrequencies.percentile(word2));
							System.out.println(score + " " + word2 + " " + word);
						}
					}
				}

			}
		}
	}
	public static void search2() {
		// CPHUMB HBP
		Solver.solve(new String[] {"CPHUMB", "LSMB", "GRDQHBP"}, null);
	}

	public static void main(String[] args) {
		search2();
		//postProcess2();
	}
}
