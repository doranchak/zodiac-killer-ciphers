package com.zodiackillerciphers.ciphers.w168;

import java.util.ArrayList;
import java.util.List;

/** one instance of an ngram */ 
public class NGram implements Comparable {
	public String ngram;
	public List<Integer> positions; // all positions 
	public int score; // log frequency score
	public NGram(String ngram, int score) {
		super();
		this.ngram = ngram;
		this.score = score;
		this.positions = new ArrayList<Integer>();
	}
	@Override
	public int compareTo(Object o) {
		NGram ngram1 = this;
		NGram ngram2 = (NGram) o;
		int comp = Integer.compare(ngram1.score, ngram2.score);
		if (comp == 0) 
			comp = Integer.compare(ngram1.positions.get(0), ngram2.positions.get(0));
		return comp;
	}
	
	public String toString() {
		return score + "	" + ngram + "	" + positions;
	}
	
	
}
