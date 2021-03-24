package com.zodiackillerciphers.ciphers.algorithms;

public class ResultBean {
	public float score = Float.MIN_VALUE;
	public String words;
	@Override
	public String toString() {
		return score + "	" + words;
	}
	
	public void check(float score, String words) {
//		System.out.println(score + " " + words);
		if (score > this.score) {
			this.score = score;
			this.words = words;
		}
	}
}
