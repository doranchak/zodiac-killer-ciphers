package com.zodiackillerciphers.annealing.skydrop;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.zodiackillerciphers.annealing.Solution;
import com.zodiackillerciphers.dictionary.WordFrequencies;
import com.zodiackillerciphers.io.FileUtil;
import com.zodiackillerciphers.lucene.Stats;
import com.zodiackillerciphers.ngrams.AZDecrypt;
import com.zodiackillerciphers.skydrop.Matrix;

/*
 * 
*/

public class SkydropSolution extends Solution {
	public static int N = 12;
	// dictionary
	public static List<String> dictionary;

	// map words to plaintext
	public static Map<String, String> plaintextMap;

	// current list of words
	List<String> words;
	// copy to revert to when needed
	List<String> mutateWords;

	public Random random = new Random();
	public double energyCached;

	public String transposedCached;
	public StringBuffer foundWords;

	@Override
	public void mutateReverse() {
		words.clear();
		for (String word : mutateWords) words.add(word);
	}
	@Override
	public void mutateReverseClear() {
		mutateWords.clear();
	}

	@Override
	public String representation() {
		return energyCached + " " + transposedCached + " " + words + " " + foundWords;
	}

	@Override
	public double energyCached() {
		return energyCached;
	}

	@Override
	public void initialize() {
		this.words = new ArrayList<String>();
		// for (int i=0; i<N; i++) 
		// 	this.words.add(dictionary.get(random.nextInt(dictionary.size())));
		this.words.add("century");
		this.words.add("throw");
		this.words.add("task");
		this.words.add("corn");
		this.words.add("library");
		this.words.add("liar");
		this.words.add("artefact");
		this.words.add("seat");
		this.words.add("general");
		this.words.add("daring");
		this.words.add("reduce");
		this.words.add("unlock");
		this.mutateWords = new ArrayList<String>();
		// String known = "ALWSRMTODRNKYOUROVALTINECANUSEEBASE3ON2NDBIGRINGB3>HEX>ASCIIDONOTFLYTONVBETWEEN2MTNS2MTNEARNVGPSUSE2MTN`NAMESM2ISATRAITORYOUR4SCRTWRDSCRTWORD1-S1M1-MOUNTAIN1JAROFGOLDGPSLATI-S1.M1S2-S3.M2S4-LONCANDANSOLVITMAYBEONLYDANWEARENOTSUREKEENEYEON87KLOVEJASONTOM";
		double score = score();
		System.out.println("Known solution: " + this.words);
		System.out.println("Plaintext: " + transposedCached);
		System.out.println("Score: " + score);
		this.words.clear();
		this.transposedCached = "";
		this.energyCached = 0;
		// StringBuffer sb = new StringBuffer();
		// System.out.println("Score: " + WordFrequencies.score(known, 4, sb));
		// System.out.println("Info: " + sb);
	}
	public SkydropSolution() {
	}
	
	// * swap random word
	// * replace random word
	// * delete random word
	// * insert random word
	@Override
	public boolean mutate() {
		int mutateWhich = random.nextInt(4);
		mutateWords.clear();
		for (String word : words) mutateWords.add(word);
		// swap
		if (mutateWhich == 0 && words.size() > 1) {
			int a = random.nextInt(words.size());
			int b = a;
			while (a==b) b = random.nextInt(words.size());
			String tmp = words.get(a);
			words.set(a, words.get(b));
			words.set(b, tmp);
		// replace random word
		} else if (mutateWhich == 1 && words.size() > 0) {
			words.set(random.nextInt(words.size()), dictionary.get(random.nextInt(dictionary.size())));
		// delete random word
		} else if (words.size() > 0 && mutateWhich == 2) {
			words.remove(random.nextInt(words.size()));
		// insert random word
		} else if (words.size() < N && mutateWhich == 3) {
			words.add(random.nextInt(words.size()+1), dictionary.get(random.nextInt(dictionary.size())));			
		}
		return true;
	}
	public boolean mutate2() {
		int mutateWhich = random.nextInt(100);
		mutateWords.clear();
		for (String word : words) mutateWords.add(word);
		// swap
		if (mutateWhich < 80 && words.size() > 1) { // [0, 79], 80% chance
			int a = random.nextInt(words.size());
			int b = a;
			while (a==b) b = random.nextInt(words.size());
			String tmp = words.get(a);
			words.set(a, words.get(b));
			words.set(b, tmp);
		// insert random word
		} else if (mutateWhich < 90 && words.size() < 12) { // [80, 89], 10% chance
			// [0, len]
			int pos = random.nextInt(words.size()+1);
			words.add(pos, dictionary.get(random.nextInt(dictionary.size())));
		// remove random word
		} else { // [90, 99], 10% chance
			if (words.size() > 0) {
				words.remove(random.nextInt(words.size()));
			}
		}
		return true;
	}

	@Override
	public double energy() {
		energyCached = -score();
		return energyCached;
	}
	@Override
	public Solution clone() {
		SkydropSolution sol = new SkydropSolution();
		sol.words = new ArrayList<String>();
		for (String word : this.words) sol.words.add(word);
		sol.mutateWords = new ArrayList<String>();
		for (String word : this.mutateWords) sol.mutateWords.add(word);
		return sol;
	}
	
	public static String decode(List<String> words) {
		if (words.size() == 0) return "";
		List<String> decoded = new ArrayList<String>();
		for (String word : words) {
			decoded.add(plaintextMap.get(word));
		}
		StringBuffer transposed = new StringBuffer();
		for (int col = 0; col < decoded.get(0).length(); col++) {
			for (int row = 0; row < decoded.size(); row++) {
				transposed.append(decoded.get(row).charAt(col));
			}
		}
		return transposed.toString();
	}
	public double score_dictionary() {
		String decoded = decode(words);
		transposedCached = decoded;
		if (decoded.length() == 0) return 0;
		StringBuffer info = new StringBuffer();
		float score = WordFrequencies.score(decoded, 4, info);
		this.foundWords = info;
		return score;
	}
	// public double score_words_bad() {
	// 	String decoded = decode(words);
	// 	transposedCached = decoded;
	// 	if (decoded.length() == 0) return 0;
	// 	foundWords = "";
	// 	double sum = 0;
	// 	for (int i=0; i<decoded.length()-2; i++) {
	// 		int n=3;
	// 		String prefix = decoded.substring(i, i+n);
	// 		while (WordFrequencies.hasPrefix(prefix)) {
	// 			if (WordFrequencies.hasWord(prefix)) {
	// 				foundWords += prefix + " ";
	// 				double log = Math.log10(WordFrequencies.freq(prefix));
	// 				sum += log;
	// 				foundWords += " " + log + " ";
	// 			}
	// 			n++;
	// 			if (i+n >= decoded.length()) break;
	// 			prefix = decoded.substring(i, i+n);
	// 		}
	// 	}
	// 	return sum * Stats.entropy(decoded);
	// }
	public boolean dupeWord() {
		Set<String> set = new HashSet<String>();
		set.addAll(words);
		return set.size() != words.size();
	}
	public double score() {
		if (dupeWord()) return 0;
		String decoded = decode(words);
		transposedCached = decoded;
		if (decoded.length() == 0) return 0;
		return AZDecrypt.score(FileUtil.convert(decoded).toString(), false, false);
	}
	public double score_ngrams() {
		if (dupeWord()) return 0;
		String decoded = decode(words);
		transposedCached = decoded;
		if (decoded.length() == 0) return 0;
		return Matrix.score(new StringBuffer(decoded), 1);
	}
	
}
