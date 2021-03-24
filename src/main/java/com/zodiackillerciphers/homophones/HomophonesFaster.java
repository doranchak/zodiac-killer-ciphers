package com.zodiackillerciphers.homophones;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.zodiackillerciphers.ciphers.Ciphers;

/** an attempt at a faster algorithm for finding largest unbroken runs of homophone sequences.
 * 
 * DIDN'T WORK - it's slower than HomophonesNew.  but it correctly finds sequences.
 *  
 **/ 
public class HomophonesFaster extends Thread {

	char ch;
	Map<Character, List<HomophonesFasterCycle>> symbolToCycle;
	StringBuffer seenSymbols;
	int L;
	int[] indexes;
	int indexPos;
	Map<String, Integer> maxRunMap;
	/** map symbol to cycles */
	static Map<Character, Set<String>> mapSymbolToCycles = new ConcurrentHashMap<Character, Set<String>>();
	/** map cycle to sequence */
	//static Map<String, StringBuffer> mapCycleToSequence = new HashMap<String, StringBuffer>();
	
	public HomophonesFaster(char ch, Map<Character, List<HomophonesFasterCycle>> symbolToCycle, StringBuffer seenSymbols, int L, int[] indexes, int indexPos, Map<String, Integer> maxRunMap) {
		this.ch = ch;
		this.symbolToCycle = symbolToCycle;
		this.seenSymbols = seenSymbols;
		this.L = L;
		this.indexes = indexes;
		this.indexPos = indexPos;
		this.maxRunMap = maxRunMap;
	}
	
	public void run() {
		//System.out.println("running...");
		HomophonesFaster.permute(ch, symbolToCycle, seenSymbols, L, indexes, indexPos, maxRunMap);
		//System.out.println("done...");
	}
	
	public static void search(String cipher, int L) {
		Long startTime = new Date().getTime();
		/** track symbols we've seen so far.  used to generate new keys. */
		Set<Character> seenSymbols = new LinkedHashSet<Character>();
		StringBuffer seenSymbolsSB = new StringBuffer();
		
		/** map individual symbol to all cycles that contain it */
		Map<Character, List<HomophonesFasterCycle>> symbolToCycle = new HashMap<Character, List<HomophonesFasterCycle>>();

		

		/** track best run scores for each cycle */
		Map<String, Integer> maxRunMap = new HashMap<String, Integer>(); 
		
		
		for (int i=0; i<cipher.length(); i++) {
			//System.out.println(i+"..."+seenSymbolsSB);
			char ch = cipher.charAt(i);
			System.out.println(i + " " + ch );
			// if symbol is unseen:
			// - add to end of new cycle, with every combination of seen symbols, and append symbol to sequence
			if (seenSymbols.size() >= L-1 && !seenSymbols.contains(ch)) {
				permute(ch, symbolToCycle, seenSymbolsSB, L, maxRunMap);
			} else {
				// otherwise:
				// - go through every cycle, and append symbol to sequence
				/*List<HomophonesFasterCycle> list = symbolToCycle.get(ch);
				if (list != null) {
					for (HomophonesFasterCycle h : list) {
						h.add(ch);
					}
				}*/
				
				/*
				if (seenSymbols.size() >= L-1) {
					for (String cycle : mapSymbolToCycles.get(ch)) {
						mapCycleToSequence.get(cycle).append(ch);
					}
				}*/
			}
			if (!seenSymbols.contains(ch)) seenSymbolsSB.append(ch);
			seenSymbols.add(ch);
		}
		Set<HomophonesFasterCycle> set = new HashSet<HomophonesFasterCycle>();
		for (List<HomophonesFasterCycle> list : symbolToCycle.values()) 
			set.addAll(list);
		for (HomophonesFasterCycle h : set) {
			Integer val = maxRunMap.get(h.symbols);
			if (val == null) val = 0;
			val = Math.max(val, h.run);
			maxRunMap.put(h.symbols, val); // some runs are still active when we reached the end of the cipher, so track max runs.
			//System.out.println(h);
		}
		System.out.println("elapsed: " + (new Date().getTime() - startTime));
		//for (String symbols : maxRunMap.keySet()) {
		//	System.out.println(maxRunMap.get(symbols) + " max run for " + symbols);
		//}
	}
	
	static void permute(char ch, Map<Character, List<HomophonesFasterCycle>> symbolToCycle, StringBuffer seenSymbols, int L, Map<String, Integer> maxRunMap) {
		int[] indexes = new int[L-1];
		int indexPos = 0;
		permute(ch, symbolToCycle, seenSymbols, L, indexes, indexPos, maxRunMap);
		
	}
	static void permute(char ch, Map<Character, List<HomophonesFasterCycle>> symbolToCycle, StringBuffer seenSymbols, int L, int[] indexes, int indexPos, Map<String, Integer> maxRunMap) {
		if (indexPos >= indexes.length) {
			// stop condition met - a full cycle
			String cycle = "";
			for (int j=0; j<indexes.length; j++) {
				char c = seenSymbols.charAt(indexes[j]);
				cycle += c;
			}
			cycle += ch;
			//System.out.println(" - indexPos " + indexPos + " indexes " + Arrays.toString(indexes) + " cycle " + cycle);
			System.out.println(" cycle " + cycle);
			
			/*HomophonesFasterCycle newCycle = new HomophonesFasterCycle(cycle, maxRunMap);*/
			
			for (int j=0; j<cycle.length(); j++) {
				char c = cycle.charAt(j);
				Set<String> cycles = mapSymbolToCycles.get(c);
				if (cycles == null) cycles = new HashSet<String>();
				cycles.add(cycle);
				mapSymbolToCycles.put(c, cycles);
				
				/*List<HomophonesFasterCycle> list = symbolToCycle.get(c);
				if (list == null) list = new ArrayList<HomophonesFasterCycle>();
				list.add(newCycle);
				symbolToCycle.put(c, list);*/
			}
			
			//mapCycleToSequence.put(cycle, new StringBuffer(cycle));
			
			return;
		}
		
		int startPos = 0;
		if (indexPos > 0) startPos = indexes[indexPos-1]+1;
		
		List<Thread> threads = null;
		if (indexPos == 0) {
			threads = new ArrayList<Thread>(); 
		}
		
		for (int i=startPos; i<seenSymbols.length(); i++) {
			indexes[indexPos] = i;
			//System.out.println(Thread.currentThread().hashCode() + " indexPos " + indexPos + " i " + i + " indexes " + Arrays.toString(indexes));
			//if (indexPos == 0) {
				// divide into threads at the topmost level of recursion
				//threads.add(new HomophonesFaster(ch, symbolToCycle, seenSymbols, L, indexes, indexPos+1, maxRunMap));
			//} else // otherwise, single thread
			
				permute(ch, symbolToCycle, seenSymbols, L, indexes, indexPos+1, maxRunMap);
			//indexes[indexPos] = 0;
		}
		
		if (threads != null) {
			for (Thread thread : threads) thread.start();
			for (Thread thread : threads) {
				try {
					thread.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	
	public static void testPermute() {
		//permute('x',null, new StringBuffer("abcdefghijklmn"), 4);
		//search(Ciphers.cipher[0].cipher, 4);
		search("ABCDEFGHIJKLMNOPQRSTUFVEWXVYZabcdefghXijklmnopqrsPtuJJJUEvWwxkBpJXXaplRcyzRM01Of0XSspL2KCQNDMZJDgo2bJKILTTJ3LGzJMy4qW5i6FRmfdh7XN8QMoAup8yrcMavPJfdJ0wVxWszSFJMtRL8MXGrZXnNpRefgPXJAJUb1JCBHJKdaqcki3liMuJJEGfYRUWakrqORwlEsJ3SDNS51ugwxkEzgF6XOV7JtRJWEIkEBli2JGm8UpXhYWPXkcWJRflH0ZRWvvlsQ4MXqAJkl0zCMJaGPYJzukD6MXWJ0Czk9X8Cxl7ZtFLzRcJEXG1Oa6PMU", 3);
		//search("ABBABA", 2);
	}
	
	public static void main(String[] args) {
		testPermute();
	}
	
}
