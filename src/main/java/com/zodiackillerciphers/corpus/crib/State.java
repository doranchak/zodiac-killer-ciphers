package com.zodiackillerciphers.corpus.crib;

import java.util.HashMap;
import java.util.Map;

/** current state of the crib search.  maps length of cipher substring to map of IoC to crib match rate.
 * when hit rate gets too high, the length/IoC combination is removed from further consideration.
 * over time, only interesting cribs that fit in cipher substrings are displayed.  
 */
public class State {
	
	public static double MAX_RATE = 0.0001;
	
	/** map length to a map of ioc to rate info */
	Map<Integer, StateIOC> map;
	
	public State() {
		map = new HashMap<Integer, StateIOC>();
	}
	
	/** increment trial for the given length and ioc.
	 *  if rate is too high, mark it so it is ignored.
	 **/
	public boolean increment(int length, double ioc, boolean hit) {
		StateIOC val = map.get(length);
		if (val == null) val = new StateIOC();
		boolean result = val.increment(ioc, hit);
		map.put(length, val);
		return result;
	}
}
