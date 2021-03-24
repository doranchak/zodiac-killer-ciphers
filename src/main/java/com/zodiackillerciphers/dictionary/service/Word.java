package com.zodiackillerciphers.dictionary.service;

public class Word {
	String word;
	int frequency;
	int percentile;

	public Word(String word, int frequency, int percentile) {
		super();
		this.word = word;
		this.frequency = frequency;
		this.percentile = percentile;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public int getPercentile() {
		return percentile;
	}

	public void setPercentile(int percentile) {
		this.percentile = percentile;
	}

	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	@Override
	public String toString() {
		return word + " " + frequency + " " + percentile;
	}
	
}
