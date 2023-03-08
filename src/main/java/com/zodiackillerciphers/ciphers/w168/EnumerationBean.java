package com.zodiackillerciphers.ciphers.w168;

public class EnumerationBean implements Comparable {
	/** score */
	public double score;
	/** enumeration */
	public String enumeration;
	@Override
	public String toString() {
		return score + "	" + enumeration;
	}
	@Override
	public int compareTo(Object o) {
		EnumerationBean bean1 = this;
		EnumerationBean bean2 = (EnumerationBean) o;
		int comp = Double.compare(bean1.score, bean2.score);
		if (comp == 0)
			return bean1.enumeration.compareTo(bean2.enumeration);
		return comp;
	}
}
