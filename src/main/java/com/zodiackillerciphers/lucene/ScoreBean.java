package com.zodiackillerciphers.lucene;

/** ngram score bean */
public class ScoreBean implements Comparable {
	public float score;
	public String text;

	@Override
	public int compareTo(Object o) {
		ScoreBean s1 = (ScoreBean) this;
		ScoreBean s2 = (ScoreBean) o;
		int compare = Float.compare(s1.score, s2.score);
		if (compare != 0)
			return compare;
		return s1.text.compareTo(s2.text);
	}

	public ScoreBean(float score, String text) {
		super();
		this.score = score;
		this.text = text;
	}
	
	public String toString() {
		return score + "	" + text;
	}
}
