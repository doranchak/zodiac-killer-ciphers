package com.zodiackillerciphers.suffixtree;

import java.util.Map;

import com.zodiackillerciphers.homophones.HomophonesResultBean;

public class RepeatsPalindromicBean {
	/** the palindromic sequence */
	String sequence;
	/** where it begins */
	int pos;
	/** full sequence length */
	int fullLength;
	/** result of search from homophonic cycles */
	HomophonesResultBean hom;
	
	public RepeatsPalindromicBean() {
		
	}
	public RepeatsPalindromicBean(String sequence, int pos) {
		super();
		this.sequence = sequence;
		this.pos = pos;
	}
	/**
	 * @return the sequence
	 */
	public String getSequence() {
		return sequence;
	}
	/**
	 * @param sequence the sequence to set
	 */
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}
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

	public int length() {
		if (sequence == null) return 0;
		return sequence.length();
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return score() + "	" + sequence + "	" + "	" + length() + "	" + fullLength;
	}
	public String toString(Map<Character, Integer> map) {
		
		String num = "";
		for (int i=0; i<sequence.length(); i++)
			num += map.get(sequence.charAt(i)) + " ";
		
		
		return score() + " [sequence=" + sequence + " " + num + ", pos=" + pos + ", length=" + length() + " of " + fullLength
				+ "]";
	}
	/**
	 * @return the fullLength
	 */
	public int getFullLength() {
		return fullLength;
	}
	/**
	 * @param fullLength the fullLength to set
	 */
	public void setFullLength(int fullLength) {
		this.fullLength = fullLength;
	}
	
	public float score() {
		// maximize: sequence length, coverage ratio
		//return ((float)sequence.length()) * sequence.length() / fullLength;
		return getHomScore();
	}
	/**
	 * @return the hom
	 */
	public HomophonesResultBean getHom() {
		return hom;
	}
	/**
	 * @param hom the hom to set
	 */
	public void setHom(HomophonesResultBean hom) {
		this.hom = hom;
	}
	
	
	public float getHomScore() {
		if (hom == null) return 0;
		return hom.score();
	}
}
