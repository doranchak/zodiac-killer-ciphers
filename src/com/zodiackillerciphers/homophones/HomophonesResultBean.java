package com.zodiackillerciphers.homophones;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HomophonesResultBean {
	// the repeating sequence
	String sequence;
	// the number of times it repeats
	int count;
	
	float probability;
	
	HomophoneSequenceBean fullsequence;
	
	// the best "run" (contiguous repeats)
	int run;
	// the repeating sequence associated with the best run (in case it's different than one that repeats the most)
	String runSequence;
	// probability of the best run
	float runProbability;
	
	Set<Integer> matches;
	
	public HomophonesResultBean(String sequence, int count, float probability,
			HomophoneSequenceBean fullsequence) {
		super();
		this.sequence = sequence;
		this.count = count;
		this.probability = probability;
		this.fullsequence = fullsequence;
	}
	// simple probability estimate
	public String getSequence() {
		return sequence;
	}
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public float getProbability() {
		return probability;
	}
	public void setProbability(float probability) {
		this.probability = probability;
	}
	public void computeProbability(int L, Map<Character, Integer> counts) {
		probability = 1;
		if (sequence != null) {
			for (int i=0; i<sequence.length(); i++) {
				char ch = sequence.charAt(i);
				probability *= ((float)counts.get(ch))/L;
			}
			probability = (float) Math.pow(probability, count);
		}
		
		runProbability = 1;
		if (runSequence != null) {
			for (int i=0; i<sequence.length(); i++) {
				char ch = sequence.charAt(i);
				runProbability *= ((float)counts.get(ch))/L;
				//System.out.println(sequence + " " + ch + " " + counts.get(ch) + " " + runProbability);
			}
			runProbability = (float) Math.pow(runProbability, run);
		}
	}
	
	/** compute the run as a percent of the full sequence length */
	public float runPercent() {
		if (runSequence == null) return 0;
		return ((float) runSequence.length() * run) / fullsequence.fullSequence.length();
	}
	
	public float simpleScore() {
		return runPercent() * run;
	}
	public static String sorted(String str) {
		if (str == null) return null;
		char[] chars = str.toCharArray();
        Arrays.sort(chars);
        String sorted = new String(chars);
        return sorted;
	}
	public String toString() {
		//if (showRun)
		String result = "'" + sorted(sequence) + "	'" + mark(true) + "	" + run + "	"
				+ runProbability + "	" + runPercent() + "	" + runPercent()
				* runProbability;
		/*if (fullsequence.isAllEven())
			result += "	(ALL EVEN POSITIONS)";
		else if (fullsequence.isAllOdd())
			result += "	(ALL ODD POSITIONS)";*/
		return result;
		//else
		//	return sequence + "	" + mark(showRun) + "	" + count + "	" + probability;
		
	}
	public HomophoneSequenceBean getFullsequence() {
		return fullsequence;
	}
	public void setFullsequence(HomophoneSequenceBean fullsequence) {
		this.fullsequence = fullsequence;
	}
	public String mark(boolean showRun) {
		String seq = showRun ? runSequence : sequence; 
		String result = "";
		int L = seq.length();
		matches = new HashSet<Integer>();
		int index = fullsequence.fullSequence.indexOf(seq);
		while (index >= 0) {
		    matches.add(index);
		    index = fullsequence.fullSequence.indexOf(seq, index + 1);
		}
		for (int i=0; i<fullsequence.fullSequence.length(); i++) {
			if (matches.contains(i)) result += " [";
			result += fullsequence.fullSequence.charAt(i);
			if (matches.contains(i-L+1)) result += "] ";
		}
		return result;
	}
	
	/**
	 * return two lists of positions. the first are positions covered by the
	 * run. the second are the rest (the positions that do not contribute to the
	 * run of cycles)
	 * 
	 * cipher must be supplied so positions can be calculated
	 */
	public List<List<Integer>> positions(String cipher) {
		List<List<Integer>> list = new ArrayList<List<Integer>>();
		
		List<Integer> posGood = new ArrayList<Integer>();
		List<Integer> posBad = new ArrayList<Integer>();
		if (matches == null) mark(true);
		
		for (int i=0; i<fullsequence.fullSequence.length();) {
			if (matches.contains(i)) {
				for (int j=0; j<sequence.length(); j++) posGood.add(fullsequence.positions.get(i+j));
				i+=sequence.length();
			} else {
				posBad.add(fullsequence.positions.get(i));
				i++;
			}
		}
		list.add(posGood);
		list.add(posBad);
		return list;
		
	}
	
	public int getRun() {
		return run;
	}
	public void setRun(int run) {
		this.run = run;
	}
	public String getRunSequence() {
		return runSequence;
	}
	public void setRunSequence(String runSequence) {
		this.runSequence = runSequence;
	}
	public float getRunProbability() {
		return runProbability;
	}
	public void setRunProbability(float runProbability) {
		this.runProbability = runProbability;
	}
}
