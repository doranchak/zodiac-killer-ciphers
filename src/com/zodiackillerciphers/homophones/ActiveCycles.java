package com.zodiackillerciphers.homophones;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zodiackillerciphers.ciphers.generator.CandidateKey;

public class ActiveCycles {

	/** track all active cycles */
	public List<ActiveCycle> activeCycles;
	
	/** put all deactivated cycles here */
	public List<ActiveCycle> deactivatedCycles;
	
	/** map symbols to their corresponding active cycles */
	public Map<Character, List<ActiveCycle>> activeCyclesMap;
	
	/** track first occurrences of symbols, also count them */
	public Map<Character, Integer> counts;

	/** all symbols from the cipher's alphabet */
	public String alphabet;
	
	/** top N scores */
	public static int N = 20;
	public float[] scores;
	
	
	public static float sum(float[] scores) {
		float result = 0;
		for (float score : scores) result += score;
		return result;
	}
	public ActiveCycles(String alphabet) {
		this.alphabet = alphabet;
		activeCycles = new ArrayList<ActiveCycle>();
		deactivatedCycles = new ArrayList<ActiveCycle>();
		activeCyclesMap = new HashMap<Character, List<ActiveCycle>>();
		counts = new HashMap<Character, Integer>();
	}
	
	public void add(ActiveCycle ac) {
		activeCycles.add(ac);
		//System.out.println("Added " + ac);
		for (int i=0; i<ac.cycle.length(); i++) {
			char c = ac.cycle.charAt(i);
			List<ActiveCycle> val = activeCyclesMap.get(c);
			if (val == null) val = new ArrayList<ActiveCycle>();
			val.add(ac);
			activeCyclesMap.put(c, val);
		}
	}

	/** returns true if this is the first time we've seen this symbol */
	public boolean isFirstOccurrence(char c) {
		return !counts.containsKey(c);
	}
	
	/** init list of cycles that might start with the given symbol.
	 * all symbols we haven't yet seen are paired with this one.
	 **/
	public void init(char c) {
		for (int i=0; i<alphabet.length(); i++) {
			char a = alphabet.charAt(i);
			if (counts.containsKey(a)) continue;
			if (c == a) continue;
			add(new ActiveCycle(c, a, counts));
		}
	}
	
	/** process the next symbol in the cipher text */
	public void processNext(char c) {
		
		// check if this is the first occurrence
		if (isFirstOccurrence(c)) {
			init(c);
		}
		
		Integer val = counts.get(c);
		if (val == null) val = 0;
		val++;
		counts.put(c, val);
		//System.out.println("count " + c + " = " + counts.get(c));
		
		// for each active cycle involving this symbol:
		// if the symbol is the expected next symbol in the sequence, append it.
		List<ActiveCycle> list = activeCyclesMap.get(c);
		for (int i=list.size()-1; i>=0; i--) {
			ActiveCycle cycle = list.get(i);
			if (cycle.isNextSymbol(c)) {
				cycle.add(c);
				//System.out.println(c + " is next for " + cycle.cycle + ", sequence now " + cycle.sequence);
			} else {
				// otherwise, remove the active cycle from the list because we no longer care about it.
				//System.out.println(c + " is not next for " + cycle.cycle + ", sequence was " + cycle.sequence + ", removing");
				deactivatedCycles.add(list.remove(i));
				activeCycles.remove(cycle);
				activeCyclesMap.get(cycle.nextSymbol(c)).remove(cycle); // remove map entry for other symbol in cycle
				
			}
		}
	}
	
	/** process a cipher text */
	public void process(String cipher) {
		for (int i=0; i<cipher.length(); i++) {
			processNext(cipher.charAt(i));
		}
		
		// might have some left in active list
		if (!activeCycles.isEmpty()) deactivatedCycles.addAll(activeCycles);

		// sort by score
		Collections.sort(deactivatedCycles, new Comparator() {
			public int compare(Object o1, Object o2) {
				ActiveCycle ac1 = (ActiveCycle) o1; 
				ActiveCycle ac2 = (ActiveCycle) o2;
				return Float.compare(ac2.score(), ac1.score());
			}
		});
		
		scores = new float[N];
		for (int i=0; i<N; i++) {
			if (i == deactivatedCycles.size()) break;
			scores[i] = deactivatedCycles.get(i).score();
		}
		
	}
	
	public void dump(boolean summaryOnly) {
		if (!summaryOnly)
			for (ActiveCycle cycle : deactivatedCycles) {
				System.out.println(cycle);
			}
		System.out.println("Cycle sum: " + sum(scores) + " Scores: " + Arrays.toString(scores));
	}
	
	
}
