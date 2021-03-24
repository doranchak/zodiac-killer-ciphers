package com.zodiackillerciphers.tests.unicity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.zodiackillerciphers.ciphers.algorithms.Vigenere;
import com.zodiackillerciphers.dictionary.WordFrequencies;
import com.zodiackillerciphers.io.FileUtil;

/** find mutually compatible vignere plaintexts */
public class VigenereMutual {
	/** How many Vigenere ciphers can decode to more than one word?
	 *  This is an attempt to estimate a practical lower bound on the unicity distance
	 *  of Vigenere ciphers in English.
	 *  
	 *  n is the Vigenere period.
	 */
	public static void ambiguousCiphers() {
		WordFrequencies.init();
		for (int n=2; n<50; n++) {
			ambiguousCiphers(n);
		}
	}
	public static void ambiguousCiphers(int n) {
		for (int L=1; L<50; L++) {
			ambiguousCiphers(L, n);
		}
	}
	public static void ambiguousCiphers(int L, int n) {
		System.out.println("=== Word length: " + L + ".  Period: " + n);
		/** Compute sum of frequencies so we can compute probability of each word. */
		long sum = 0;
		for (String word : WordFrequencies.map.keySet()) {
			if (word.length() != L) continue;
			sum += WordFrequencies.freq(word);
		}
		
		/** Map letter difference sequences to words that match.
		 * These are used to identify "Vigenere mutual" words.
		 * Two words are "Vigenere mutual" if a Vigenere cipher exists
		 * such that one key produces the first word, and another key produces the second word.  
		 */
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		for (String word : WordFrequencies.map.keySet()) {
			if (word.length() != L)
				continue;
			String key = Arrays.toString(letterDifferences(word, n));
			List<String> list = map.get(key);
			if (list == null) list = new ArrayList<String>();
			list.add(word);
			map.put(key,  list);
		}
		int total = 0;
		for (String key : map.keySet()) {
			List<String> list = map.get(key);
			if (list.size() < 2) continue;
			total += list.size();
			System.out.println(key + "	" + list);
		}
		System.out.println("=== TOTAL: " + total + ".  Word length: " + L + ".  Period: " + n);
		System.out.println("TOTAL: " + total);
	}
	
	
	/** How many Vigenere ciphers can decode to more than one word?
	 *  This is an attempt to estimate a practical lower bound on the unicity distance
	 *  of Vigenere ciphers in English.
	 *  
	 *   d is the key length.
	 *   L is the target word length.
	 *   26^d is the key space.
	 *   When d = 1, the cipher is equivalent to Caesar shift.
	 */
	public static void ambiguousCiphersBRUTE(int d, int L) {
		WordFrequencies.init();
		for (String word : WordFrequencies.map.keySet()) {
			if (word.length() != L) continue;
//			if (WordFrequencies.percentile(word) < minPercentile)
//				continue;
			int[] key = new int[d];
			ambiguousCiphersBRUTE(key, 0, word);
		}
	}
	public static void ambiguousCiphersBRUTE(int[] key, int index, String word) {
		if (index == key.length) {
			boolean allZeroes = true;
			for (int k : key) {
				if (k != 0) {
					allZeroes = false;
					break;
				}
			}
			if (allZeroes) return;
			String keyWord = keyFrom(key);
			String cipher = Vigenere.encrypt(word, keyWord).toUpperCase();
			int p1 = WordFrequencies.percentile(word);
			int p2 = WordFrequencies.percentile(cipher);
			if (p2 == 0)
				return;
			//			if (p2 < minPercentile)
//				return;
			long score = (1+p1)*(1+p2);
			System.out.println(score + " " + p1 + " " + p2 + " " + word + " " + Arrays.toString(key) + " " + keyWord
					+ " " + cipher + " " + Arrays.toString(letterDifferences(word, key.length)) + " "
					+ Arrays.toString(letterDifferences(cipher, key.length)));
			return;
		}
		for (int a=0; a<26; a++) {
			key[index] = a;
			ambiguousCiphersBRUTE(key, index+1, word);
		}
	}
	/** convert an integer key to a keyword */
	public static String keyFrom(int[] key) {
		String word = "";
		for (int i : key) {
			char ch = (char) (i+97);
			word += ch;
		}
		return word;
	}
	

	public static String makeString(int L) {
		String result = "";
		String resultSpaces = "";
		Set<String> seen = new HashSet<String>();
		while (true) {
			String word = WordFrequencies.randomWord();
			if (word.length() < 3) continue;
			if (WordFrequencies.percentile(word) < 80) continue;
			if (seen.contains(word)) continue;
			seen.add(word);
			result += word;
			resultSpaces += word + " ";
			if (result.length() > L) {
				result = "";
				resultSpaces = "";
				seen.clear();
			} else if (result.length() == L) {
				return resultSpaces;
			}
		}
	}
	/** keep forming strings of the given length, using 1 or more words, until a pair of Vigenere mutual 
	 *  strings is discovered. */
	public static void makeVigenereMutual(int L, int d) {
		System.out.println("Restarting search...");
		WordFrequencies.init();

		
		Map<String, String> map = new HashMap<String, String>();
		long trials = 0;
		boolean go = true;
		while (go) {
			trials++;
			if (trials > 50000000) return;
			String str = makeString(L);
			String strNoSpaces = str.replaceAll(" ", "");
			String key = Arrays.toString(letterDifferences(strNoSpaces, d));
			String val = map.get(key);
			if (val != null) {
				if (val.replaceAll(" ", "").equals(strNoSpaces)) continue;
				System.out.println("[" + val + "] [" + str + "] " + key + " " + trials);
				return;
			}
			map.put(key, str);
		}
	}

	public static int matches(String str1, String str2) {
		str1 = str1.replaceAll(" ", "");
		str2 = str2.replaceAll(" ", "");
		int count = 0;
		for (int i=0; i<str1.length(); i++) {
			if (str1.charAt(i) == str2.charAt(i)) {
				count++;
			}
		}
		return count;
	}
	
	public static void process(String path) {
		List<String> lines = FileUtil.loadFrom(path);
		long totalTrials = 0;
		int num = 0;
		for (String line : lines) {
			if (!line.startsWith("[")) continue;
			String part1 = line.substring(1);
			String part2 = part1.substring(part1.indexOf("[")+1); 
			part1 = part1.substring(0, part1.indexOf("]"));
			part2 = part2.substring(0, part2.indexOf("]"));
			String p1ns = part1.replaceAll(" ", "");
			String p2ns = part2.replaceAll(" ", "");
			int matches = matches(p1ns, p2ns);
			if (matches > p1ns.length()/2) {
				continue;
			}
			
			// check for repeating compound words, like FIFTYFIFTY and BADENBADEN.
			if (p1ns.length() % 2 == 0) {
				if (p1ns.substring(0, p1ns.length()/2).equals(p1ns.substring(p1ns.length()/2)))
						continue;
				if (p2ns.substring(0, p2ns.length()/2).equals(p2ns.substring(p2ns.length()/2)))
					continue;
			}
			
			long trials = Long.valueOf(line.substring(line.lastIndexOf(' ')+1));
			totalTrials += trials;
			num++;
			System.out.println(part1 + ", " + part2 + ", " + matches(part1, part2) + ", " + trials);
		}
		System.out.println("Hits: " + num);
		System.out.println("Total trials: " + totalTrials);
		System.out.println("Average: " + totalTrials/num);
	}
	
	/** generate a random cipher of period n for the given mutually compatible plaintexts.
	 * output the keys that decode to both plaintexts. */
	public static void randomCipher(String part1, String part2, int n) {
		part1 = part1.toLowerCase();
		part2 = part2.toLowerCase();
		Random rand = new Random();
		String key = "";
		for (int i=0; i<n; i++) {
			char c = (char)(rand.nextInt(26) + 97);
			key += c;
		}
		String encoded = Vigenere.encrypt(part1, key);
		String key1 = keyFor(encoded, part1).substring(0, n);
		String key2 = keyFor(encoded, part2).substring(0, n);
		System.out.println(encoded + " " + key1 + " " + key2 + " " + Vigenere.decrypt(encoded, key1) + " " + Vigenere.decrypt(encoded, key2));
	}
	
	/** compute differences between each letter in the input.
	 * example: ABDC = [1,2,-1] */
	public static int[] letterDifferences(String str) {
		str = str.toLowerCase();
		int[] result = new int[str.length()-1];
		for (int i=1; i<str.length(); i++) {
			char c1 = str.charAt(i-1);
			char c2 = str.charAt(i);
			int val1 = c1 - 97;
			int val2 = c2 - 97;
			int diff = val2 - val1;
			if (diff < 0) diff += 26;
			
			result[i-1] = diff;
		}
		return result;
	}
	
	/** return key letter needed to decode given cipher letter to the given plaintext letter */
	public static char keyLetterFor(char cipher, char plaintext) {
		char c = (char)(cipher-97);
		char p = (char)(plaintext-97);
		int diff = c-p;
		if (diff < 0) diff += 26;
		char d = (char)(diff+97);
		return d;
	}
	/** return key needed to decode given cipher to the given plaintext */
	public static String keyFor(String cipher, String plaintext) {
		cipher = cipher.toLowerCase();
		plaintext = plaintext.toLowerCase();
		String key = "";
		for (int i=0; i<cipher.length(); i++) {
			key += keyLetterFor(cipher.charAt(i), plaintext.charAt(i));
		}
		return key;
	}
	
	/** compute differences between each letter in the input,
	 * based on the given vigenere key size (d). */
	public static int[] letterDifferences(String str, int d) {
		str = str.toLowerCase();
		int[] result = new int[str.length()];
		for (int i=0; i<result.length; i++) result[i] = -1;
		for (int i=0; i<str.length(); i++) {
			if (i + d >= str.length())
				continue;
			char c1 = str.charAt(i);
			char c2 = str.charAt(i+d);
			int val1 = c1 - 97;
			int val2 = c2 - 97;
			int diff = val2 - val1;
//			System.out.println(str + ", " + c1 + ", " + c2 + ", " + val1 + ", " + val2 + ", " + diff);
			if (diff < 0) diff += 26;
			result[i] = diff;
		}
		return result;
	}
	public static void main(String[] args) {
		System.out.println(Arrays.toString(letterDifferences("PUZZLINGLY", 5)));
	}
}
