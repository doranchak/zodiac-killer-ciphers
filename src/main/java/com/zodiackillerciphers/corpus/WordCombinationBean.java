package com.zodiackillerciphers.corpus;

public class WordCombinationBean {
	String combination;
	double score;

	public String getCombination() {
		return combination;
	}

	public void setCombination(String combination) {
		this.combination = combination;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}
	
	public int count() {
		return combination.split(" ").length;
	}

	public String toString() {
		return count() + "	" + score + "	" + combination;
	}
}
