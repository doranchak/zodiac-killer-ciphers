package com.zodiackillerciphers.pivots;

public class ShuffleBean {
	/** the shuffled cipher text */
	String shuffle;
	/** its number of repeating bigrams */
	int numRepeats;
	public ShuffleBean(String shuffle, int numRepeats) {
		super();
		this.shuffle = shuffle;
		this.numRepeats = numRepeats;
	}
	/**
	 * @return the shuffle
	 */
	public String getShuffle() {
		return shuffle;
	}
	/**
	 * @param shuffle the shuffle to set
	 */
	public void setShuffle(String shuffle) {
		this.shuffle = shuffle;
	}
	/**
	 * @return the numRepeats
	 */
	public int getNumRepeats() {
		return numRepeats;
	}
	/**
	 * @param numRepeats the numRepeats to set
	 */
	public void setNumRepeats(int numRepeats) {
		this.numRepeats = numRepeats;
	}
	
}
