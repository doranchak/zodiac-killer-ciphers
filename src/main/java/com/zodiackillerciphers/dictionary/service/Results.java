package com.zodiackillerciphers.dictionary.service;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

public class Results {
	/** total number of results (not all results are necessarily included) */
	public int countTotal;
	/** count of only the results that are included here */
	public int count;
	/** list of words */
	public List<Word> words;
	
	public Results() {
		words = new ArrayList<Word>();
	}

	public int getCount() {
		return countTotal;
	}

	public void setCount(int count) {
		this.countTotal = count;
	}
	
	public void addWord(String word, int frequency, int percentile) {
		words.add(new Word(word, frequency, percentile));
	}
	
	public void dump() {
		for (Word word : words) System.out.println(word);
	}
	
	public String json() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}

	public int getCountTotal() {
		return countTotal;
	}

	public void setCountTotal(int countTotal) {
		this.countTotal = countTotal;
	}
	
}
