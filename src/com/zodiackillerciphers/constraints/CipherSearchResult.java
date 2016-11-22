package com.zodiackillerciphers.constraints;

public class CipherSearchResult implements Comparable {
	public String substring;
	public String solution;
	int pos;

	@Override
	public int compareTo(Object o) {
		CipherSearchResult c1 = this;
		CipherSearchResult c2 = (CipherSearchResult) o;
		
		return new Integer(c1.substring.length()).compareTo(c2.substring.length());
	}

	public String toString() {
		return pos + ", " + substring.length() + ", " + substring + (solution == null ? "" : ", " + solution);
	}
}
