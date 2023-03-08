package com.zodiackillerciphers.tests;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.dictionary.WordFrequencies;
import com.zodiackillerciphers.ngrams.NGramsBean;

/** https://rubyhunt.us/clues/ */
public class RubyHuntCipher {

	static String cipher = "7NVL J7FAN 2 OU{> UJF>7 }N3 RCA{3 {{C4N U7 C>}72U{ }FJ2}>V >N2UV^{7O JA37 N{V3^}7 V3U3O{O7> 7N U>}^V JJU^{>A AN OcA{2 U7VL{ 7N ^A7OUUO <2J>A>} 2>}} 2 F>2J O7OU^3O 7>A^}F{U CC{>UJ} }>JU{F 2>J4O>A AJ> O2O >J}L> U<>OV{ }>N5 N34 >cA{2 UA L}A}NV72J 7JF>{U 2VN} J>A 2{";

	/** find N-letter words with matching (N-1)-letter prefixes (suffixes) */
	public static void findWords(int N, boolean prefix) {
		WordFrequencies.init();
		Map<String, Set<String>> chunks = new HashMap<String, Set<String>>();
		for (String word : WordFrequencies.map.keySet()) {
			if (word.length() == N) {
				String chunk = prefix ? word.substring(0, N - 1) : word.substring(1, N);
				Set<String> set = chunks.get(chunk);
				if (set == null) {
					set = new HashSet<String>();
					chunks.put(chunk, set);
				}
				set.add(word);
			}
		}
		for (String chunk : chunks.keySet()) {
			Set<String> words = chunks.get(chunk);
			if (words.size() < 2)
				continue;
			double score = 0;
			for (String word : words)
				score += Math.log10(WordFrequencies.freq(word));
			System.out.println(score + "	" + chunk + ": " + words);
		}
	}
	
	/** generate Ngram counts but ignore directions */
	public static void findNgramsIgnoreDirection() {
		int n=2;
		while (true) {
			System.out.println("==== " + n + "-grams:");
			Map<String, Integer> counts = new HashMap<String, Integer>(); 
			boolean found = false;
			NGramsBean ng = new NGramsBean(n, cipher, false);
			for (String ngram1 : ng.counts.keySet()) {
				String ngram2 = new StringBuffer(ngram1).reverse().toString();
				if (ngram1.equals(ngram2)) continue;
				String key = keyFor(ngram1, ngram2);
				//System.out.println(ngram1 + " " + ngram2 + " " + key + " " + counts.get(key));
				Integer val = counts.get(key);
				if (val == null) {
					val = 0;
				}
				
				Integer count1 = ng.counts.get(ngram1);
				Integer count2 = ng.counts.get(ngram2);
				if (count1 != null) val += count1;
				if (count2 != null) val += count2;
				
				counts.put(key, val);
				if (val > 1) found = true;
			}
			//System.out.println(counts);
			
			for (String key : counts.keySet()) {
				Integer count = counts.get(key);
				if (count > 1) {
					System.out.println(count + "	" + key);
				}
			}
			
			n++;
			if (!found) break;
		}
	}
	/** find repeating Ngrams */
	public static void findNgrams(String cipher) {
		int n=2;
		while (true) {
			System.out.println("==== " + n + "-grams:");
			NGramsBean ng = new NGramsBean(n, cipher, false);
			if (ng.repeats.size() == 0) break;
			for (String ngram : ng.repeats)
				System.out.println(ng.counts.get(ngram) + " " + ngram);
			n++;
		}
	}
	public static String keyFor(String ng1, String ng2) {
		if (ng1.compareTo(ng2) == -1) return ng1;
		return ng2;
	}

	public static void main(String[] args) {
//		findWords(5, true);
		findNgramsIgnoreDirection();
//		findNgrams(cipher);
	}
}
