package com.zodiackillerciphers.dictionary;

import java.util.ArrayList;
import java.util.List;

public class PhraseSearchResult implements Comparable<PhraseSearchResult> {
	
	/** list of words found */
	public List<String> words;
	/** starting position in the input string */
	int position;
	/** score */
	Float score = null;
	
	public PhraseSearchResult(int position) {
		words = new ArrayList<String>();
		this.position = position;
	}
	
	public float computeScore() {
		score = 1f;
		if (words == null) return score;
		for (String word : words) {
			Integer p = WordFrequencies.percentile(word);
			if (p != null && p > 0) score *= p * word.length();
		}
		return score;
	}

	public int compareTo(PhraseSearchResult o) {
		if (o.score == null) o.computeScore();
		if (this.score == null) this.computeScore(); 
		return Float.compare(o.score, this.score);
	}
	
	public void add(String word) {
		words.add(word);
	}
	public void add(PhraseSearchResult phrase) {
		if (phrase == null) return;
		if (phrase.words == null) return;
		words.addAll(phrase.words);
	}
	
	public String toString() {
		if (score == null) computeScore();
		String line = "" + score + ", " + position + ", " + words.size() + ", ";
		for (int i=0; i<words.size(); i++) {
			line += words.get(i);
			if (i < words.size() - 1) line += " ";
		}
		return line;
	}
}
