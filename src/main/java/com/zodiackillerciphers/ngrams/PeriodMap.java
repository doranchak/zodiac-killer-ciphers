package com.zodiackillerciphers.ngrams;

import java.util.HashMap;
import java.util.Map;

public class PeriodMap {
	/** the rewritten cipher */
	public String rewritten;
	/** maps positions to original positions.  key: index in rewritten cipher.  value: index in original cipher. */
	public Map<Integer, Integer> map;
	public PeriodMap() {
		map = new HashMap<Integer, Integer>();
	}
	
}
