package com.zodiackillerciphers.suffixtree;

/** models movement through a palindromic pattern */
public class PalindromicState {
	/**
	 * the palindromic pattern. first and last characters can optionally repeat.
	 * 
	 * for example: 1 2 3 3 2 1 or 1 2 3 2 1 1 2 3 2 1
	 * so pattern would be: 123
	 */
	String pattern;
	
	/** the full sequence we've found using the given pattern */
	String sequence;

	/** direction of current movement */
	boolean forward;

	/** current position within the pattern */
	int pos;

	/**
	 * @return the pattern
	 */
	public String getPattern() {
		return pattern;
	}

	/**
	 * @param pattern the pattern to set
	 */
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	/**
	 * @return the forward
	 */
	public boolean isForward() {
		return forward;
	}

	/**
	 * @param forward the forward to set
	 */
	public void setForward(boolean forward) {
		this.forward = forward;
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
	/**
	 * make a move with the given character, but return false if the character
	 * violates the expected one from the pattern
	 */
	public boolean move(char ch) {
		if (sequence == null) sequence = "";
		// the last seen character
//		char last = sequence.charAt(sequence.length()-1);
		// case 1: we are at the end of the pattern
		if (pos == pattern.length()-1) {
			forward = false;
			// special case: the end of the pattern can be used twice
			char end = pattern.charAt(pattern.length()-1);
			if (end == sequence.charAt(sequence.length())) {
				
			}
		} 
		// case 2: we are at the beginning of the pattern
		else if (pos == 0) {
			forward = true;
		}
		pos = pos + (forward ? 1 : -1);
		char expected = pattern.charAt(pos);
		
		System.out.println("ch " + ch + " pos " + pos + " forward " + forward + " expected " + expected);
		
		if (expected != ch) {
			// exception to the rule: the end of the pattern can be used twice
			if (pos == pattern.length()-1 && sequence.charAt(sequence.length()-1) == pattern.charAt(pattern.length()-1)) {
				; // then we're ok
			}
			else return false;
		}
		sequence += ch;
		//System.out.println("move success, sequence " + sequence);
		return true;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PalindromicState [pattern=" + pattern + ", sequence="
				+ sequence + ", forward=" + forward + ", pos=" + pos + "]";
	}
	
	


}
