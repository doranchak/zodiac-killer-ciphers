package com.zodiackillerciphers.corpus.crib;

import java.util.HashMap;
import java.util.Map;

public class StateIOC {
	/** map cipher substring IoC to rate information */ 
	public Map<Double, StateIOCRate> map;
	
	public StateIOC() {
		map = new HashMap<Double, StateIOCRate>();
	}
	
	/** returns false if we didn't increment due to ignoring */
	public boolean increment(double ioc, boolean hit) {
		StateIOCRate val = map.get(ioc);
		if (val == null) val = new StateIOCRate();
		boolean result = val.increment(hit);
		map.put(ioc, val);
		return result;
	}
}
