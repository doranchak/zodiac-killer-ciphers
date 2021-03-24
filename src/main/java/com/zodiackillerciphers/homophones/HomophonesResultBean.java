package com.zodiackillerciphers.homophones;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.ciphers.Ciphers;

public class HomophonesResultBean {
	// the repeating sequence
	String sequence;
	// the number of times it repeats
	int count;
	
	float probability;
	
	/** true if we used longest running substring method instead of perfect cycles */
	boolean lrs;
	/** first position of the unbroken repeating sequence (if in lrs mode) */
	int pos;
	
	HomophoneSequenceBean fullsequence;
	
	// the best "run" (contiguous repeats)
	int run;
	// the repeating sequence associated with the best run (in case it's different than one that repeats the most)
	String runSequence;
	// probability of the best run
	float runProbability;
	
	Set<Integer> matches;
	
	// for LRS method, all positions covered by all occurrences of the repeating substring
	//Set<Integer> coverage;
	// probability a result of this kind was found during shuffles.  lower values suggest
	// more significance.
	float shuffleProbability;
	
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
	public void computeProbability(int L, Map<Character, Integer> counts, HomophonesResultBean bean) {
		probability = 1;

		if (bean.isLrs()) {
//			for (int i=0; i<bean.getCount()*bean.getSequence().length(); i++) {
//				char ch = bean.fullsequence.fullSequence.charAt(i+bean.getPos());
//				probability *= 1/((float)counts.get(ch));
//			}
			return;
		}
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
		if (isLrs()) {
//			return score() + "	" + shuffleProbability + "	" + sequence + "	"
//					+ fullsequence.fullSequence + "	" + count + "	" + count
//					* sequence.length() + "	"
//					+ fullsequence.fullSequence.length();
			return "'" + sequence + "	" + sequence.length() + "	" + count + "	'" + fullsequence.fullSequence + "	" +
			fullsequence.fullSequence.length() + "	" + score();
		}
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
		try {
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
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
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
	/**
	 * @return the lrs
	 */
	public boolean isLrs() {
		return lrs;
	}
	/**
	 * @param lrs the lrs to set
	 */
	public void setLrs(boolean lrs) {
		this.lrs = lrs;
	}
//	/**
//	 * @return the coverage
//	 */
//	public Set<Integer> getCoverage() {
//		return coverage;
//	}
//	/**
//	 * @param coverage the coverage to set
//	 */
//	public void setCoverage(Set<Integer> coverage) {
//		this.coverage = coverage;
//	}
	/**
	 * @return the pos
	 */
	public int getPos() {
		return pos;
	}
	/**
	 * @param pos the pos to set
	 */
	public void setPos(int pos) {
		this.pos = pos;
	}
	
	/** return size of sequence alphabet */
	public int alphabetSize() {
		return Ciphers.alphabetAsSet(sequence).size();
	}
	
	public String key() {
		return sequence.length() + " " + count + " " + fullsequence.fullSequence.length(); 
	}
	
	/** LRS method produces a lot of spurious results.  try to estimate a score to rank them */
	public float score() {
		float result = 1;
		// maximize: total length, count, size of sequence alphabet, ratio of pattern to nonpattern
		// minimize: sequence length
		result *= count * sequence.length();
		result *= count;
		result *= alphabetSize();
		result *= ((float)count * sequence.length()) / fullsequence.fullSequence.length();
		result /= sequence.length();
		return result;
	}
	/**
	 * @return the shuffleProbability
	 */
	public float getShuffleProbability() {
		return shuffleProbability;
	}
	/**
	 * @param shuffleProbability the shuffleProbability to set
	 */
	public void setShuffleProbability(float shuffleProbability) {
		this.shuffleProbability = shuffleProbability;
	}
}
