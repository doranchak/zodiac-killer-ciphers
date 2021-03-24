package com.zodiackillerciphers.names;

import java.util.List;

import com.zodiackillerciphers.dictionary.WordFrequencies;

public class Name {
	public String name;
	public float frequency;
	public float frequencyCumulative;
	public int rank = 0;
	
	public NameType type;
	public boolean suffix;
	public boolean prefix;
	
	public Name() {}
	public Name(String name) { this.name = name;}
	
	public static float score(List<Name> names) {
		if (names == null || names.isEmpty()) return 0;
		float score = 1;
		for (Name name : names) {
			if (name.name.length() == 1) continue;
			score *= (1+name.frequency);
		}
		return score;
	}
	
	public String toString() {
		WordFrequencies.init();
		return name + ", " + frequency + ", " + frequencyCumulative + ", " + rank + ", " + WordFrequencies.scoreLog(name);
	}
}
