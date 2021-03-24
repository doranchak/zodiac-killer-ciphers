package com.zodiackillerciphers.tests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zodiackillerciphers.dictionary.WordFrequencies;

/**
 * From B:
 * 
 * Doing my latest security homework, and I found the .hushlogin replaced. In of
 * itself this isn't odd, but the text is bizarre. It's probably nothing, but I
 * figure no harm in asking around. Since you've got some experience in bizarre
 * messages, does this second line's text look like anything to you?
 * 
 * root@target:~# cat .hushlogin | less Revp Pbeyrl’f unaqyr vf nyyhfvbag gb
 * jung?
 * 
 * @author doranchak
 * 
 */
public class HushLogin {

	public static final String cipher = "REVP PBEYRL’F UNAQYR VF NYYHFVBAG GB JUNG?";

	public static void search() {
		WordFrequencies.init();
		Map<Character, Character> decoder = new HashMap<Character, Character>();
		for (String word : WordFrequencies.map.keySet()) {
			if (word.length() == 9) {
				if (word.charAt(1) == word.charAt(2)) { // words matching
														// pattern: nyyhfvbag
					decoder = decoderFor("NYYHFVBAG", word);
					String decoded = decode(cipher, decoder);
					String word1 = decoded.split(" ")[1];
					String word2 = decoded.split(" ")[2];
					String word3 = decoded.split(" ")[3];
					String word4 = decoded.split(" ")[5];
					List<String> fit1 = fit(word1);
					List<String> fit2 = fit(word2);
					if (fit1.isEmpty() || fit2.isEmpty())
						continue;
					long score = ((long)1 + maxfreq(fit1)) * (1 + maxfreq(fit2))
							* (1 + WordFrequencies.freq(word))
							* (1 + WordFrequencies.freq(word3))
							* (1 + WordFrequencies.freq(word4));

					System.out.println(score + " " + decoded + ": " + fit1
							+ "; " + fit2);

				}
			}
		}
	}

	public static List<String> fit(String pattern) {
		List<String> list = new ArrayList<String>();
		for (String word : WordFrequencies.map.keySet()) {
			if (pattern.length() != word.length())
				continue;
			boolean match = true;
			for (int i = 0; i < word.length(); i++) {
				char c1 = pattern.charAt(i);
				char c2 = word.charAt(i);
				if (c1 == '_' || c1 == ' ')
					continue;
				if (c1 != c2) {
					match = false;
					break;
				}
			}
			if (match)
				list.add(word);
		}
		return list;
	}

	public static int maxfreq(List<String> words) {
		int max = 0;
		for (String word : words) {
			max = Math.max(max, WordFrequencies.freq(word));
		}
		return max;
	}

	public static String decode(String cipher, Map<Character, Character> decoder) {
		String decoded = "";
		for (int i = 0; i < cipher.length(); i++) {
			if (cipher.charAt(i) == ' ') {
				decoded += " ";
				continue;
			}
			Character p = decoder.get(cipher.charAt(i));
			if (p == null)
				decoded += '_';
			else
				decoded += p;
		}
		return decoded;
	}

	public static Map<Character, Character> decoderFor(String cipher,
			String plaintext) {
		Map<Character, Character> decoder = new HashMap<Character, Character>();
		for (int i = 0; i < cipher.length(); i++) {
			decoder.put(cipher.charAt(i), plaintext.charAt(i));

		}
		return decoder;
	}

	public static void main(String[] args) {
		search();
	}
}
