package com.zodiackillerciphers.ciphers.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.ciphers.Ciphers;

import ec.util.MersenneTwisterFast;

/**
 * represents trigrams that appear in plain text or cipher text. we want to
 * preserve certain trigrams that appear in the plain text, so the resulting
 * cipher's trigrams will resemble the ones from the 340.
 */
public class Trigram {
	
	public static int W = 17;
	
	/** the trigram */
	String trigram;
	/** positions of the above trigram (treating cipher as single stream) */
	public Set<Integer> positions;

	/** positions of the ones we picked randomly to encipher with hard-coded symbols */
	public Set<Integer> positionsEnciphered;
	
	/** if this trigram has repeats in the same column, this contains a list of their positions mapped to the column number
	 *  key is the column number.
	 *  value is the set of raw positions where the repeats occur for the given column.
	 **/
	public Map<Integer, Set<Integer>> columnRepeatPositions;

	/**
	 * true if this trigram intersects with a trigram that repeats in the same
	 * column.
	 */
	boolean intersects;
	
	/** if this is a column repeating trigram, this map tracks the positions of any intersecting trigrams.  */
	//public Map<String, Set<Integer>> intersectingTrigramPositions;
	
	/** track intersecting trigrams. key is position, val is Trigram that intersects there. */
	Map<Integer, Trigram> intersectingTrigrams;
	
	/** if this is an intersecting trigram, track the position of the column-repeating trigram it intersects with */
	public Integer whichColumnRepeaterPosition;

	public Trigram(String trigram) {
		this.trigram = trigram;
	}

	/** returns true if the trigram repeats */
	public boolean repeats() {
		if (positions == null)
			return false;
		return positions.size() > 1;
	}

	public String getTrigram() {
		return trigram;
	}

	public void setTrigram(String trigram) {
		this.trigram = trigram;
	}

	public boolean isSameColumn() {
		return columnRepeatPositions != null && !columnRepeatPositions.isEmpty();
	}

	public boolean isIntersects() {
		return intersects;
	}

	public void setIntersects(boolean intersects) {
		this.intersects = intersects;
	}

	public void addPosition(int pos) {
		if (positions == null)
			positions = new HashSet<Integer>();
		positions.add(pos);
		// track repeats per column
		addColumnRepeatPosition(pos % W, pos);
	}

	/** convert position to row and column, based on given grid width */
	public static int[] rowColFrom(int W, int pos) {
		return new int[] { pos / W, pos % W };
	}

	/** default width: 17 */
	public static int[] rowColFrom(int pos) {
		return rowColFrom(W, pos);
	}

	public String toString() {
		String result = trigram + ": ";
		if (positions != null) {
			result += "[" + positions.size() + "] ";
			for (Integer i : positions)
				result += i + " ";
		}
		if (columnRepeatPositions != null) {
			for (Integer col : columnRepeatPositions.keySet()) {
				Set<Integer> val = columnRepeatPositions.get(col);
				result += "(repeats in column " + col + ": ";
				for (Integer pos : val) result += pos + " ";
				result += ") ";
			}
		}
		if (intersectingTrigrams != null) {
			for (Integer key : intersectingTrigrams.keySet()) {
				result += "(intersects with " +intersectingTrigrams.get(key).trigram + " at " + key + ") "; 
			}
		}
		return result;
	}

	
	
	public void addColumnRepeatPosition(int column, int pos) {
		if (columnRepeatPositions == null) columnRepeatPositions = new HashMap<Integer, Set<Integer>>();
		Set<Integer> val = columnRepeatPositions.get(column);
		if (val == null) val = new HashSet<Integer>();
		val.add(pos);
		columnRepeatPositions.put(column, val);
	}
	
	public void cull() {
		/** remove non-repeats from the column-to-position mappings */
		if (columnRepeatPositions != null) {
			Set<Integer> toRemove = new HashSet<Integer>();
			for (Integer col : columnRepeatPositions.keySet()) {
				Set<Integer> positions = columnRepeatPositions.get(col);
				if (positions == null || positions.size() < 2) {
					toRemove.add(col);
				}
			}
			if (toRemove != null) {
				for (Integer remove : toRemove) columnRepeatPositions.remove(remove);
			}
		}
	}
	
	
	public void addIntersectingTrigram(Trigram tri, int pos, int colRepPos) {
		if (intersectingTrigrams == null) intersectingTrigrams = new HashMap<Integer, Trigram>();
		intersectingTrigrams.put(pos, tri);
		tri.setIntersects(true);
		setIntersects(true);
		tri.whichColumnRepeaterPosition = colRepPos;
	}

	public static void main(String[] args) {
		//Set<TrigramCandidatePair> trigrams = TrigramUtils.trigramsFrom(Ciphers.cipher[0].cipher);
		//Set<TrigramCandidatePair> trigrams = TrigramUtils.trigramsFrom("l2A_j:%-TpcU7dd3E)@_ME/VX1F<8>S5L>S5:1KVt;PVl7-z@HZDdqd(yj  zZ71/8K    ;<R*GOdC4D;++H8F)qpX#F:%<pSCz9F/94)B<Vf7.;3F/BdOfkSVd1zO&:CcMNfGNN|EbVb6JV><9LC>@5-1XCd.1DAREb*WAq-GzV&54N1NV4.cVzRDD6@F:ME)9FBc+lqRdC>6jHER4b.cVN#E9T-|N+EzjtVCLX54)MF)61.<V8tG|H>34K|5FBcV+4/S3CcfOUd|5FJp8SV:<MNc8;<lRfyAqBPVDAX%E/jt>JMU@4b_OfVCVA-3kj|_SMFA;GP9>GzERJ|*>");
		Set<TrigramCandidatePair> trigrams = TrigramUtils.trigramsFrom("9%P/Z/UB%kOR=pX=BWV+eGYF69HP@K!MU^MJY^UIk7qTtNQYD5)S(/9#BPORAU%fRlqEk^LMZJdr\\pFHVWe8Y@+qGD9KI)6qX85zS(RNtIYElO8qGBTQS#BLd/P#B@XqEHMU^RRkcZKqpI)Wq!85LMr9#BPDR+j=6\\N(eEUHkFZcpOVWI5+tL)l^R6HI9DR_TYr\\de/@XJQAXqE8RUt%L)NVEKH=GrI!Jk598LMlNA)Z(PzUpkA9#BVW\\+VTtOP^=SrlfUe67DzG%%IMNk)ScE/9%%ZfAP#BVpeXqWq_F#8c+@9A9B%OT5RUc+_dYq_^SqWVZeGYKE_TYA9%#Lt_H!FBX9zXADd\\7L!=q_ed##6e5PORXQF%GcZ@JTtq_8JI+rBPQW6VEXr9WI6qEHM)=UIk");
		for (TrigramCandidatePair tri : trigrams) System.out.println(tri);
	}

	public boolean equals(Object obj) {
		if (obj == null) return false;
		Trigram tri = (Trigram) obj;
		return tri.trigram.equals(trigram);
	}
	
	public Integer randomPosition(MersenneTwisterFast rand) {
		List<Integer> list = new ArrayList<Integer>(positions);
		return list.get(rand.nextInt(list.size()));
	}
	
	public String highlight() {
		String js = "trigram ";
		for (int pos : positions) {
			for (int i=0; i<3; i++) {
				js += "darkenpos(" + (pos+i) + "); ";
			}
		}
		return js;
	}
}
