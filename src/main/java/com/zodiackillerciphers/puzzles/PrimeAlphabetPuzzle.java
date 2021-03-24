package com.zodiackillerciphers.puzzles;

import java.util.HashMap;
import java.util.Map;

import com.zodiackillerciphers.dictionary.WordFrequencies;

/** https://www.facebook.com/groups/ACADiscussionGroup/?multi_permalinks=2521882568085084%2C2521072744832733%2C2520986808174660&notif_id=1572389073624081&notif_t=group_activity&ref=notif */
public class PrimeAlphabetPuzzle {
	/** map letters to prime numbers */
	static Map<Character, Integer> map = new HashMap<Character, Integer>(); 
	static {
		int prime = 2;
		for (char c=65; c<=90; c++) {
			map.put(c, prime);
			prime = nextPrime(prime);
		}
		System.out.println(map);
	}
	/** compute next prime after the given prime */
	static int nextPrime(int prime) {
		while(true) {
			if (isPrime(++prime)) {
				return prime;
			}
		}
	}
	/** naive implementation */
	static boolean isPrime(int num) {
		for (int n=num-1; n>1; n--) {
			if (num % n == 0) return false;
		}
		return true;
	}
	
	/** return "coded" value of the given word, the product of each letter's associated prime number */
	public static long encode(String str) {
		str = str.toUpperCase();
		long product = 1;
		for (int i=0; i<str.length(); i++) {
			product *= map.get(str.charAt(i));
		}
		return product;
	}
	
	/** "Decode" the given value.  Search dictionary for all words that encode to the given number. */
	public static void decode(long val) {
		WordFrequencies.init();
		for (String word : WordFrequencies.map.keySet()) {
			if (val == encode(word)) {
				System.out.println(WordFrequencies.percentile(word) + " " + word);
			}
		}
	}
	
	/** find all words that encode to values within a percentage of the given target */ 
	public static void solve(long target, float p) {
		WordFrequencies.init();
		for (String word : WordFrequencies.map.keySet()) {
			long val = encode(word);
			float pCompare = Math.abs(val-target);
			pCompare = pCompare / target;
			if (pCompare <= p) {
				System.out.println(val + " " + Math.abs(val-target) + " " + WordFrequencies.percentile(word) + " " + word);
			}
		}
	}
	/** find all 2-word combos that encode to values within a percentage of the given target */ 
	public static void solveCombo(long target, float p) {
		WordFrequencies.init();
		for (String word1 : WordFrequencies.map.keySet()) {
			if (WordFrequencies.percentile(word1) < 90) continue;
			for (String word2 : WordFrequencies.map.keySet()) {
				if (WordFrequencies.percentile(word2) < 90) continue;
				long val = encode(word1+word2);
				float pCompare = Math.abs(val-target);
				pCompare = pCompare / target;
				if (pCompare <= p) {
					System.out.println(val + " " + Math.abs(val - target) + " "
							+ WordFrequencies.percentile(word1) * WordFrequencies.percentile(word2) + " " + word1 + " "
							+ word2);
				}
			}
		}
	}
	
	public static void testNextPrime() {
		int prime = 2;
		while(true) {
			System.out.println(prime);
			prime = nextPrime(prime);
		}
	}
	public static void main(String[] args) {
//		testNextPrime();
		System.out.println(encode("WOMBAT"));
//		decode(7411406);
//		solve(1000000, .10f);
//		solveCombo(1000000, .03f);
	}
}
