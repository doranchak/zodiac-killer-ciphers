package com.zodiackillerciphers.tests.cribbing;

import java.util.Random;

public class Result extends Base implements Comparable {

	/** multiplicity of the cipher text portion */
	float multiplicity;
	/** length of the cipher text portion */
	int length;
	/** ioc of plaintext */
	float ioc;
	/** composite score */
	float score;

	/** the cipher text portion */
	String ciphertext;
	/** the plaintext portion (converted stream) */
	String plaintext;
	/** original plaintext portion plus some context */
	String plaintextOriginal;

	/** filename of plaintext source */
	String filename;

	/** start position of the cipher text portion */
	int posCipherStart;
	/** start position of the plain text portion */
	int posPlainStart;

	static Random rnd = new Random();

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Object o) {
		Result r1 = this;
		Result r2 = (Result) o;
		int result = Float.compare(r2.getScore(), r1.getScore());
		if (result == 0) {
			// special case: scores are the same but plaintext might not be
			if (r1.getPlaintext().equals(r2.getPlaintext())) {
				result = 0;
			}
			// no preference if strings are different but have same score
			else
				result = r1.getPlaintext().compareTo(r2.getPlaintext());
		}
		// System.out.println("compareTo " + result + " score " + r1.score + " "
		// + r2.score + " pt " + r1.plaintext + " " + r2.plaintext);
		return result;
	}

	public String toString() {
		return "RESULT:	" + score + "	" + length + "	" + multiplicity + "	"
				+ ioc + "	" + (float) Math.abs(0.0667 - ioc) + "	" + ciphertext + "	"
				+ plaintext + "	" + plaintextOriginal + "	" + filename + "	"
				+ posCipherStart + "	" + posPlainStart;
	}

	/**
	 * @return the multiplicity
	 */
	public float getMultiplicity() {
		return multiplicity;
	}

	/**
	 * @param multiplicity
	 *            the multiplicity to set
	 */
	public void setMultiplicity(float multiplicity) {
		this.multiplicity = multiplicity;
	}

	/**
	 * @return the length
	 */
	public int getLength() {
		return length;
	}

	/**
	 * @param length
	 *            the length to set
	 */
	public void setLength(int length) {
		this.length = length;
	}

	/**
	 * @return the score
	 */
	public float getScore() {
		return score;
	}

	/**
	 * @param score
	 *            the score to set
	 */
	public void setScore(float score) {
		this.score = score;
	}

	/**
	 * @return the ciphertext
	 */
	public String getCiphertext() {
		return ciphertext;
	}

	/**
	 * @param ciphertext
	 *            the ciphertext to set
	 */
	public void setCiphertext(String ciphertext) {
		this.ciphertext = ciphertext;
	}

	/**
	 * @return the plaintext
	 */
	public String getPlaintext() {
		return plaintext;
	}

	/**
	 * @param plaintext
	 *            the plaintext to set
	 */
	public void setPlaintext(String plaintext) {
		this.plaintext = plaintext;
	}

	/**
	 * @return the plaintextOriginal
	 */
	public String getPlaintextOriginal() {
		return plaintextOriginal;
	}

	/**
	 * @param plaintextOriginal
	 *            the plaintextOriginal to set
	 */
	public void setPlaintextOriginal(String plaintextOriginal) {
		this.plaintextOriginal = plaintextOriginal;
	}

	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * @param filename
	 *            the filename to set
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}

	/**
	 * @return the posCipherStart
	 */
	public int getPosCipherStart() {
		return posCipherStart;
	}

	/**
	 * @param posCipherStart
	 *            the posCipherStart to set
	 */
	public void setPosCipherStart(int posCipherStart) {
		this.posCipherStart = posCipherStart;
	}

	/**
	 * @return the posPlainStart
	 */
	public int getPosPlainStart() {
		return posPlainStart;
	}

	/**
	 * @param posPlainStart
	 *            the posPlainStart to set
	 */
	public void setPosPlainStart(int posPlainStart) {
		this.posPlainStart = posPlainStart;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int hashCode = 1;
		hashCode = 31 * hashCode + new Float(score).hashCode();
		hashCode = 31 * hashCode + new Float(ioc).hashCode();
		hashCode = 31 * hashCode + plaintext.hashCode();
		return hashCode;
	}

	/**
	 * @return the ioc
	 */
	public float getIoc() {
		return ioc;
	}

	/**
	 * @param ioc
	 *            the ioc to set
	 */
	public void setIoc(float ioc) {
		this.ioc = ioc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	// @Override
	// public boolean equals(Object obj) {
	// System.out.println("CALLED EQUALS");
	// if (obj == null) return false;
	// Result r1 = this;
	// Result r2 = (Result) obj;
	// if (r1.getScore() != r2.getScore()) return false;
	// System.out.println("equals " + r1.getPlaintext() + " " +
	// r2.getPlaintext());
	// return r1.getPlaintext().equals(r2.getPlaintext());
	// }

}
