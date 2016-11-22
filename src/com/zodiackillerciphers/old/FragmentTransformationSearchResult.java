package com.zodiackillerciphers.old;

public class FragmentTransformationSearchResult {
	public StringBuffer cipher;
	public String name;
	public FragmentScore score;
	
	public String toString() {
		return name + " " + score + " [" + cipher + "]";
	}
}
