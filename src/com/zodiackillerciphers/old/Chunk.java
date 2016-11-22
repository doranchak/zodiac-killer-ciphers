package com.zodiackillerciphers.old;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.zodiackillerciphers.lucene.DictionaryIndexer;

public class Chunk implements Comparable {
	public String chunk;
	public int pos;
	public int num;

	public int compareTo(Object o) {
		Chunk chunk = (Chunk) o;
		if (this.chunk.length() > chunk.chunk.length()) return 1;
		if (this.chunk.length() < chunk.chunk.length()) return -1;
		return 0;
	}

	public Chunk(String chunk, int pos, int num) {
		super();
		this.chunk = chunk;
		this.pos = pos;
		this.num = num;
	}
	
	public String toString() {
		return pos + "," + chunk;
	}

	public static void test() {
		List<Chunk> chunks = new ArrayList<Chunk>();
		
		chunks.add(new Chunk("alsj", 0, 0));
		chunks.add(new Chunk("sadfasdgasdfasdfasdfasdfsdfas", 0, 1));
		chunks.add(new Chunk("aa", 0, 2));
		chunks.add(new Chunk("0983091823", 0, 3));
		chunks.add(new Chunk("1092830918309183091", 0, 4));
		chunks.add(new Chunk("212331", 0, 5));
		
		Collections.sort(chunks);
		
		for (Chunk c : chunks) System.out.println(c.chunk);
	}
	
	public static void main(String[] args) {
		//test();
		System.out.println("["+DictionaryIndexer.patterns("abc")+"]");
		System.out.println(DictionaryIndexer.patterns("abc").split(" ").length);
		System.out.println(DictionaryIndexer.patterns("abc").length());
	}
	
}
