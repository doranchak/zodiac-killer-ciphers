package com.zodiackillerciphers.suffixtree;

public class RepeatsResultBean {
	/** the substring that repeats */
	String sequence;
	/** how many times it repeats */
	int count;
	/** where the repeats begin */
	int pos;
	
	public RepeatsResultBean() {
		
	}
	public RepeatsResultBean(String sequence, int count, int pos) {
		super();
		this.sequence = sequence;
		this.count = count;
		this.pos = pos;
	}


	/** total length of repeating patterns */
	public int length() {
		if (sequence == null) return 0;
		return sequence.length() * count;
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
	 * @return the count
	 */
	public int getCount() {
		return count;
	}


	/**
	 * @param count the count to set
	 */
	public void setCount(int count) {
		this.count = count;
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
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RepeatsResultBean [sequence=" + sequence + ", count=" + count
				+ ", pos=" + pos + ", length=" + length() +"]";
	}
	
	

}
