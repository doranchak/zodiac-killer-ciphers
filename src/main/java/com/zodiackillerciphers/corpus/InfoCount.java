package com.zodiackillerciphers.corpus;

import java.util.HashSet;
import java.util.Set;


public class InfoCount implements Comparable {
	public int count = 0; // number of times this substring was found
	public String substring; // the found substring
	public Set<String> fileNames; // filenames in which the substring was found
	public InfoCount() {
		fileNames = new HashSet<String>();
	}
	@Override
	public int compareTo(Object o) {
		InfoCount a = this;
		InfoCount b = (InfoCount) o;
		
		if (a.substring.length() < b.substring.length()) return 1;
		if (a.substring.length() > b.substring.length()) return -1;
		
		if (a.count < b.count) return 1;
		if (a.count > b.count) return -1;
		return 0;
	}
	
	public String toString() {
		return "Length: '''" + substring.length() + "'''  Substring: '''" + substring + "'''  Matches: '''" + count + "'''";
	}
}
