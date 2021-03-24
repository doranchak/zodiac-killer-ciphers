package com.zodiackillerciphers.ngrams;

public class NGramResultBean {
	public NGramResultBean(String sequence, Float probability) {
		super();
		this.sequence = sequence;
		this.probability = probability;
	}
	String sequence;
	Float probability;
	public String getSequence() {
		return sequence;
	}
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}
	public Float getProbability() {
		return probability;
	}
	public void setProbability(Float probability) {
		this.probability = probability;
	}
	public String toString() {
		return probability + " " + sequence;
	}
}
