package com.zodiackillerciphers.names;

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
	
	public String toString() {
		return name + ", " + frequency + ", " + frequencyCumulative + ", " + rank;
	}
}
