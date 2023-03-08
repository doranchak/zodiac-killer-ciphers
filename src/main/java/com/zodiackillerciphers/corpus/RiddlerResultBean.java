package com.zodiackillerciphers.corpus;

public class RiddlerResultBean implements Comparable {
	public String plaintext;
	public String plaintextNoSpaces;
	public int numWords;
	public float averageWordLength;
	public float sumWordPercentiles;
	public float averageWordPercentile;
	public float ngramZk;
	public double ngram5;
	public double ngram6;
	public float ioc;
	public boolean proper;
	public boolean homophonic;

	public String toString() {
		String tab = "	";
		return numWords + tab + averageWordLength + tab + sumWordPercentiles + tab + averageWordPercentile + tab
				+ ngramZk + tab + ngram5 + tab + ngram6 + tab + ioc + tab + proper + tab + homophonic + tab + plaintext + tab + html();

	}
	
	public String html() {
		String html = "<span class=\"";
		String cl;
		if (!proper && !homophonic)
			cl = "type1";
		else if (!proper && homophonic) 
			cl = "type2";
		else if (proper && !homophonic)
			cl = "type3";
		else cl = "type4";
		html += cl + "\">" + plaintext + "</span>";
		return html;
	}

	@Override
	public int compareTo(Object o) {
		RiddlerResultBean bean1 = (RiddlerResultBean) o;
		RiddlerResultBean bean2 = this;

		return Double.compare(bean1.sumWordPercentiles, bean2.sumWordPercentiles);
	}
}
