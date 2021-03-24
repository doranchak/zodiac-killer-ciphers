package com.zodiackillerciphers.corpus;

import java.util.Comparator;

public class FirstLetterSequenceSearchBean implements Comparable<FirstLetterSequenceSearchBean> {
	/** which cipher */
	public int which;
	/** the file that matched */
	public String file;
	/** the matching ngram sequence (including spaces) */
	public StringBuffer ngram;
	/** the matching sequence of letters in the cipher */
	public StringBuffer letters;
	/** first position in the cipher that matches the letter sequence */
	public int pos;
	/** word-based score */
	public double score;
	public int length() {
		return letters.length();
	}
	public String toString() {
		return which + "	" + length() + "	" + score + "	" + letters + "	" + pos + "	" + ngram + "	" + file;
	}
	@Override
	public int compareTo(FirstLetterSequenceSearchBean o2) {
		FirstLetterSequenceSearchBean o1 = this;
		int comp = Integer.compare(o2.length(), o1.length()); 
		if (comp == 0) 
			comp = Double.compare(o2.score, o1.score);
		if (comp == 0) 
			comp = o1.ngram.toString().compareTo(o2.ngram.toString());			
//		System.out.println("o2 [" + o2.length() + "," + o2.score + "] o1 [" + o1.length() + ",:" + o1.score + "]: " + comp);
		return comp;
	}
};
