package com.zodiackillerciphers.tests.samblake;

import java.util.ArrayList;
import java.util.List;

/** 2-step transpositions */
public class AZDecryptResult {
	String name;
	List<String> features;
	int score;
	
	public AZDecryptResult() {
		features = new ArrayList<String>();		
	}
	
	public void add(String feature) {
		features.add(feature);
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void makeFeatures(int which) {
		if (which == 0) 
			makeFeatures0();
		if (which == 1) 
			makeFeatures1();
		if (which == 2) 
			makeFeatures2();
		if (which == 3) 
			makeFeatures3();
		if (which == 4 || which == 5) 
			makeFeatures4();
	}
	
	public void makeFeatures0() {
		int start = 6;
		if (name == null)
			return;
		String[] split = name.split("_");
		for (int i = start; i < split.length; i++) {
			String part = split[i];
			if (i == start) {
				if (!part.equals("tr"))
					add("normal");
			} else if (features.size() == 2 && !part.equals("tr"))
				add("normal");
			if (features.size() == 3 && part.startsWith("decimate")) {
				part += "_" + split[split.length - 1];
			}
			if (features.size() == 4)
				continue;
			add(part);
		}
	}

	public void makeFeatures1() {
		
		// 1st is always cutNN
		// 2nd and 3rd is always blocksizeNN_NN
		// 4th is always a transposition operation
		// 5th+ can be: colwise, rowwise, or tr_colwise
		// so, 5th should be: tr or normal
		// 6th can be colwise or rowwise
		
		int start = 0;
		if (name == null)
			return;
		String[] split = name.split("_");
		for (int i = start; i < split.length; i++) {
			String part = split[i];
			if (i == 4) {
				if (!part.equals("tr")) {
					add("normal");
				}
			}
			if (i==1) {
				part = part + "_" + split[i+1];
				i++;
			}
			add(part);
		}
	}

	public void makeFeatures2() {
		
		int start = 0;
		if (name == null)
			return;
		String[] split = name.split("_");
		for (int i = start; i < split.length; i++) {
			String part = split[i];
			if (part.equals("tr")) {
				add("tr");
				add(split[++i]);
			} else {
				add("normal");
				add(part);
			}
		}
	}

	public void makeFeatures3() {
		
		int start = 0;
		if (name == null)
			return;
		String[] split = name.split("_");
		for (int i = start; i < split.length; i++) {
			String part = split[i];
			add(part);
		}
	}
	public void makeFeatures4() {
		int start = 0;
		if (name == null)
			return;
		String[] split = name.split("_");
		for (int i = start; i < split.length; i++) {
			String part = split[i];
			if (part.equals("tr")) {
				add("tr");
				add(split[++i]);
			} else if (part.matches("[0-9][0-9]")) {
				String prev = features.remove(features.size()-1);
				if (prev.startsWith("decimate")) {
					prev += "_" + part;
				} else if (prev.startsWith("blocksize")) {
					prev += "_" + part;
				} else {
					throw new RuntimeException("unexpected " + prev + ", " + name);
				}
				add(prev);
			} else {
				add("normal");
				add(part);
			}
		}
	}
	
	public String toString() {
		return score + "	" + features + "	" + name;
	}
	
	public static void main(String[] args) {
		System.out.println("12".matches("[0-9][0-9]"));
	}
}
