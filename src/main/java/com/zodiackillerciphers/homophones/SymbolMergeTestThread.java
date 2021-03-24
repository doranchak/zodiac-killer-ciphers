package com.zodiackillerciphers.homophones;

import java.util.HashMap;
import java.util.Map;

import com.zodiackillerciphers.ngrams.NGramsBean;
import com.zodiackillerciphers.ngrams.Periods;

public class SymbolMergeTestThread extends Thread {
	Map<Integer, Map<Integer, Integer>> periodRepeats;
	int n;
	String shuffled;
	
	public SymbolMergeTestThread(
			Map<Integer, Map<Integer, Integer>> periodRepeats, int n,
			String shuffled) {
		super();
		this.periodRepeats = periodRepeats;
		this.n = n;
		this.shuffled = shuffled;
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		periodRepeats.put(n, new HashMap<Integer, Integer>());
		for (int p = 1; p <= 170; p++) {
			String re = SymbolMergeTest.untranspose ? Periods.rewrite3(shuffled, p) : Periods.rewrite3undo(shuffled, p);
			NGramsBean ng = new NGramsBean(n, re);
			periodRepeats.get(n).put(p, ng.numRepeats());
		}
	}

}
