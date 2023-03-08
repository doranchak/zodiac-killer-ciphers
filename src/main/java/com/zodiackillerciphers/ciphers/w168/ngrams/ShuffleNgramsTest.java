package com.zodiackillerciphers.ciphers.w168.ngrams;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeSet;

import com.zodiackillerciphers.ciphers.w168.EnumerationBean;
import com.zodiackillerciphers.ciphers.w168.StringUtils;
import com.zodiackillerciphers.ciphers.w168.W168;
import com.zodiackillerciphers.lucene.NGramsCSRA;
import com.zodiackillerciphers.stats.StatsWrapper;

/** test the significance of n-grams scores in the W168 */
public class ShuffleNgramsTest {
	
	/** a cache for shuffle stats */
	public static boolean ENABLE_CACHING = true;
	
	public static int MAX_HEAP_SIZE = 100;
	
	// key is {ngram length, string sample length}
	public static Map<String, StatsWrapper> statsCache = new HashMap<String, StatsWrapper>();
	
	public static synchronized void statsCacheAdd(int ngramLength, int stringSampleLength, StatsWrapper stats) {
		String key = ngramLength + " " + stringSampleLength;
		System.out.println("Added shuffle stats for key " + key + " to cache");
		stats.output();
		statsCache.put(key, stats);
	}
	
	static Random random = new Random();
	/** create a shuffle by taking random samples from the given string array */
	public static StringBuilder sample(StringBuilder[] arr, int samples) {
		//System.out.println("# " + samples);
		StringBuilder sample = new StringBuilder();
		for (int i=0; i<samples; i++) {
			sample.append(arr[random.nextInt(arr.length)].charAt(random.nextInt(arr[0].length())));
		}
		//System.out.println(sample.length());
		return sample;
	}
	
	public static void shuffleTestPeriodCombinations(StringBuilder[] cipher, int shuffles, boolean reverse,
			int[] periods, boolean wrap, boolean overlap, TreeSet<ShuffleResult> topResults, boolean zkscore) {
		//System.out.println("n=" + n + ", shuffles=" + shuffles + ", reverse=" + reverse + ", periods=" + Arrays.toString(periods));
		int n = periods.length+1;
		StringBuilder cipherStr = StringUtils.toLine(cipher);
		if (reverse) cipherStr = cipherStr.reverse();
		StringBuilder combos = periodCombinations(cipherStr, periods, wrap);
		double score = score(combos, n, overlap, zkscore);
		double actual = score;
		//System.out.println("combos	" + combos.length() + "	" + actual + "	" + Arrays.toString(periods) + "	" + combos);
		
		
		StatsWrapper stats = null;
		if (ENABLE_CACHING) stats = statsCache.get(n + " " + combos.length());
		if (stats == null) {
			stats = new StatsWrapper(); 
			for (int i=0; i<shuffles; i++) {
				cipherStr = sample(cipher, combos.length());
				//combos = periodCombinations(cipherStr, periods, wrap);
				//score = score(combos, n);
				score = score(cipherStr, n, overlap, zkscore);
				stats.addValue(score);
				//System.out.println(i);
			}
			if (ENABLE_CACHING) statsCacheAdd(n, combos.length(), stats);
		}
		
		double sigma = stats.sigma(actual); 
		
		ShuffleResult result = new ShuffleResult(sigma, actual, n, reverse, periods, combos.length());
		updateHeap(topResults, result);
		//System.out.println(result);
//		if (Math.abs(sigma) >= minSigmaToPrint)
//			System.out.println(sigma + "	" + actual + "	" + n + "	" + shuffles + "	" + reverse + "	"
//					+ Arrays.toString(periods));
	}
	public static void updateHeap(TreeSet<ShuffleResult> heap, ShuffleResult result) {
		// if heap not full, just add it
		if (heap.size() < MAX_HEAP_SIZE) {
			heap.add(result);
			//if (feedback) System.out.println(key + " Added1 " + bean);
			//dump(heap, key);
		} else {
			// tree already has this result?  then ignore;
			if (heap.contains(result)) {
				;
			} else {
				// is this score better than the worst score? 
				ShuffleResult worst = heap.first();
				if (result.sigma > worst.sigma) { // it's better
					if (!heap.remove(worst)) throw new RuntimeException("ERROR!  CANNOT REMOVE WORST!"); // and remove the worst score
					if (!heap.add(result)) {
						//System.out.println(heap);
						throw new RuntimeException("ERROR!  CANNOT ADD RESULT! " + result); // so add to heap
					}
					//if (feedback) System.out.println(key + " Added2 " + bean + " Removed " + worst);
					//dump(heap, key);
				}
			}
		}
	}
	
	/** apply multiple periods at the same time. */ 
	public static StringBuilder periodCombinations(StringBuilder str, int[] periods, boolean wrap) {
		StringBuilder result = new StringBuilder();
		for (int i=0; i<str.length(); i++) {
			int[] positions = new int[periods.length+1];
			positions[0] = i;
			boolean inBounds = true;
			for (int p=0; p<periods.length; p++) {
				positions[p+1] = positions[p] + periods[p];
				if (positions[p+1] < 0 || positions[p+1] >= str.length())
					inBounds = false;
			}
			if (!inBounds && !wrap) continue;
			for (int p=0; p<positions.length; p++) {
				int pos = positions[p];
				while (pos < 0) pos += str.length();
				pos = pos % str.length();
				result.append(str.charAt(pos));
			}
		}
		return result;
	}
	
	public static void testPeriodCombinations() {
		System.out.println(periodCombinations(StringUtils.toLine(W168.Z408_1), new int[] {29, -1}, false));
		System.out.println(periodCombinations(StringUtils.toLine(W168.Z408_1), new int[] {0, 0}, false));
	}
	
	// without period
	public static double score(StringBuilder str, int n, boolean overlap, boolean zkscore) {
		if (zkscore) return NGramsCSRA.zkscore(new StringBuffer(str.toString()), "EN", true);
		double score = 0;
		int increment = 1;
		if (!overlap) increment = n;
		for (int i=0; i<str.length()-n+1; i+=increment) {
			String ngram = str.substring(i,i+n);
			score += NGramsCSRA.valueFor(ngram, "EN", true);
		}
		return score;
	}
	// with period
	static double score(String str, int n, int period) {
		double score = 0;
		boolean go = true;
		int i=0;
		while (go) {
			String ngram = "";
			for (int j=0; j<period*n; j+=period) {
				if (i+j >= str.length()) {
					go = false; 
					break;
				}
				ngram += str.charAt(i+j);
			}
			i++;
			if (go) {
				//System.out.println("[" + ngram + "]");
				score += NGramsCSRA.valueFor(ngram, "EN", true);
			}
		}
		return score;
	}
	static double scoreZK(String str, int period) {
		double score = 0;
		boolean go = true;
		int i=0;
		int n =6;  // zkscore the entire 6-gram
		while (go) {
			String ngram = "";
			for (int j=0; j<period*n; j+=period) {
				if (i+j >= str.length()) {
					go = false; 
					break;
				}
				ngram += str.charAt(i+j);
			}
			i++;
			if (go) {
				//System.out.println("[" + ngram + "]");
				score += NGramsCSRA.zkscore(new StringBuffer(ngram), "EN", true);
			}
		}
		return score;
	}
	public static void testScore() {
		NGramsCSRA.OPTIONAL_PREFIX = "/Users/doranchak/projects/zodiac/zkdecrypto/zkdecrypto/language/";
		NGramsCSRA.init("EN");		
		score("REDACTED", 2, 7);
	}
	public static String toString(StringBuilder[] arr, boolean reverse) {
		String result = "";
		for (StringBuilder sb : arr) result += sb;
		if (reverse) result = new StringBuffer(result).reverse().toString();
		return result;
	}

	public static void dump(TreeSet<ShuffleResult> treeSet) {
		for (ShuffleResult result : treeSet)
			System.out.println(result);
	}
	// test single period + caching
	public static void goSingle(StringBuilder[] cipher, boolean wrap, boolean overlap, boolean zkscore) {
		NGramsCSRA.OPTIONAL_PREFIX = "/Users/doranchak/projects/zodiac/zkdecrypto/zkdecrypto/language/";
		NGramsCSRA.init("EN");
		int shuffles = 10000;

//		StringBuilder[] cipher = W168.toStringBuilder(W168.Z408_1);
//		StringBuilder[] cipher = W168.toStringBuilder(W168.J168_2);
//		StringBuilder[] cipher = StringUtils.toStringBuilder(W168.SAM184_1);
//		StringBuilder[] cipher = StringUtils.toStringBuilder(new String[] { "IN A POPULATION OF AND A HAL",
//				"F BILLION THERES BOUND TO BE", " SOME TOTAL PIECES OF SHIT. ", "IDK I SEE WHERE YOU ARE COMI",
//				"NG FROM BUT I THINK THAT TYW", "IN ACCOUNTS FOR THAT AS WELL" });
		//StringBuilder[] cipher = W168.cipherBuilder;
		
		TreeSet<ShuffleResult> topResults = new TreeSet<ShuffleResult>();
		
		for (int p1 = -168; p1 <= 168; p1++) {
			System.out.println("p1 " + p1 + "...");
			shuffleTestPeriodCombinations(cipher, shuffles, false, new int[] { p1 }, wrap, overlap, topResults, zkscore);
		}
		System.out.println("=== RESULTS 1 ===");
		dump(topResults);
	}	
	// test period pair + caching
	public static void goPair(StringBuilder[] cipher, boolean wrap, boolean overlap, boolean zkscore) {
		NGramsCSRA.OPTIONAL_PREFIX = "/Users/doranchak/projects/zodiac/zkdecrypto/zkdecrypto/language/";
		NGramsCSRA.init("EN");
		int shuffles = 10000;

//		StringBuilder[] cipher = W168.toStringBuilder(W168.Z408_1);
//		StringBuilder[] cipher = W168.toStringBuilder(W168.J168_2);
		//StringBuilder[] cipher = W168.cipherBuilder;
		TreeSet<ShuffleResult> topResults = new TreeSet<ShuffleResult>();
		for (int p1 = -168; p1 <= 168; p1++) {
			System.out.println("p1 " + p1 + "...");
			for (int p2 = -168; p2 <= 168; p2++) {
				shuffleTestPeriodCombinations(cipher, shuffles, false, new int[] { p1, p2 }, wrap, overlap, topResults, zkscore);
			}
		}
		System.out.println("=== RESULTS 2 ===");
		dump(topResults);
	}	
	// test period triplet + caching
	public static void goTriplet(StringBuilder[] cipher, boolean wrap, boolean overlap, boolean zkscore) {
		NGramsCSRA.OPTIONAL_PREFIX = "/Users/doranchak/projects/zodiac/zkdecrypto/zkdecrypto/language/";
		NGramsCSRA.init("EN");
		int shuffles = 10000;

		//StringBuilder[] cipher = W168.toStringBuilder(W168.Z408_1);
		//StringBuilder[] cipher = W168.toStringBuilder(W168.J168_2);
		//StringBuilder[] cipher = W168.cipherBuilder;
		//StringBuilder[] cipher = StringUtils.toStringBuilder(W168.SAM184_2);
		//for (boolean reverse : new boolean[] { false, true }) {
		TreeSet<ShuffleResult> topResults = new TreeSet<ShuffleResult>();
			for (int p1 = -84; p1 <= 84; p1++) {
				System.out.println("p1 " + p1 + "...");
				for (int p2 = -84; p2 <= 84; p2++) {
					for (int p3 = -84; p3 <= 84; p3++) {
						shuffleTestPeriodCombinations(cipher, shuffles, false, new int[] { p1, p2, p3 }, wrap, overlap, topResults, zkscore);
					}
				}
			}
		//}
			System.out.println("=== RESULTS 3 ===");
			dump(topResults);
	}	
	
	public static void main(String[] args) {
//		StringBuilder[] cipher = StringUtils.toStringBuilder(new String[] { " UNPNNOAALHIT  S  DETP   ISH",
//				"KEDEEAR MROFTIUHAIHWOFC AWH ", "PTIOFAI  O BRUNE MNBACEL  ES", " HISO EUG C   BTTATYT CSALTS",
//				"AL A DO LLFIHBEOTSOOOITEOTF. ", "  IW RYEIONM NIKTN  UONRTE L" });
		StringBuilder[] cipher = W168.cipherBuilder;
		//go3();
		//testScore();
		//go4();
//		testPeriodCombinations();
		//go7();
//		goSingle(cipher, false, false, false);
//		goSingle(cipher, false, false, true);
//		goSingle(cipher, false, true, false);
//		goSingle(cipher, false, true, true);
//		goSingle(cipher, true, false, false);
//		goSingle(cipher, true, false, true);
//		goSingle(cipher, true, true, false);
//		goSingle(cipher, true, true, true);
		
		goPair(cipher, true, false, false);
		statsCache = new HashMap<String, StatsWrapper>();
		goPair(cipher, true, false, true);
		statsCache = new HashMap<String, StatsWrapper>();
		goPair(cipher, true, true, false);
		statsCache = new HashMap<String, StatsWrapper>();
		goPair(cipher, true, true, true);
		
		
//		goPair(cipher);
		//goTriplet(cipher);
	}
}
