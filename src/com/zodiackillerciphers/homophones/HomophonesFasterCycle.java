package com.zodiackillerciphers.homophones;

import java.util.Map;


public class HomophonesFasterCycle {
	// the cycle symbols
	public String symbols;
	
	// the full sequence
	public StringBuffer sequence = new StringBuffer();
	
	// points to index of next expected symbol from the cycle
	public int expectedSymbol;
	
	// current unbroken run count
	public int run;
	
	// reference to "max run" map
	public Map<String, Integer> maxRunMap;

	/** add next cipher symbol to sequence */
	public void add(char ch) {
		//if ("2z".equals(symbols)) System.out.println("adding " + ch + " to " + sequence);
		sequence.append(ch);
		if (symbols.charAt(expectedSymbol) == ch) { // matched the expected symbol, so increment run count
			run++;
			//if ("2z".equals(symbols)) System.out.println("matched expected " + symbols.charAt(expectedSymbol) + ", run " + run);
			expectedSymbol = (expectedSymbol+1) % symbols.length(); 
		} else { // otherwise, reset the run count
			Integer val = maxRunMap.get(symbols);
			if (val == null) val = 0;
			val = Math.max(val, run);
			maxRunMap.put(symbols, val);
			run = 1;
			expectedSymbol = 0;
			while (symbols.charAt(expectedSymbol) != ch) 
				expectedSymbol = (expectedSymbol+1) % symbols.length();
			// advance one more to get the next expected symbol
			expectedSymbol = (expectedSymbol+1) % symbols.length();
			// finally, reset the full sequence, to avoid filling up memory 
			sequence = new StringBuffer(ch);
		}
		
	}
	
	public HomophonesFasterCycle(String cycle, Map<String, Integer> maxRunMap) {
		symbols = cycle;
		this.maxRunMap = maxRunMap;
		// since we are adding for the first time, there is already one occurrence of the cycle in the full sequence
		sequence = new StringBuffer(cycle);
		run = cycle.length();
	}
	
	public String toString() {
		return symbols + " " + sequence;
	}
	
}
