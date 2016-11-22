package com.zodiackillerciphers.tests.jarlve;

public class AZResultBean {
	/** index number (starts at 0) */
	int number;
	/** score */
	float score;
	/** adjuste score */
	float scoreAdjusted;
	/** ioc */
	//int ioc;
	/** M */
	//int m;
	float multiplicity;
	/** plaintext */
	String plaintext;
	/** ciphertext */
	String ciphertext;
	/** ciphertext2 */
	/** operations */
	String operations;
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public float getScore() {
		return score;
	}
	public void setScore(float score) {
		this.score = score;
	}
	/*public int getIoc() {
		return ioc;
	}
	public void setIoc(int ioc) {
		this.ioc = ioc;
	}
	public int getM() {
		return m;
	}
	public void setM(int m) {
		this.m = m;
	}*/
	public String getPlaintext() {
		return plaintext;
	}
	public void setPlaintext(String plaintext) {
		this.plaintext = plaintext;
	}
	public String getCiphertext() {
		return ciphertext;
	}
	public void setCiphertext(String ciphertext) {
		this.ciphertext = ciphertext;
	}
	
	public String toString() {
		//return number + " " + score + " " + ioc + " " + m + " " + plaintext + " " + ciphertext;
		return number + " " + score + " " + scoreAdjusted + " length " + ciphertext.length() + " multiplicity " + multiplicity + " plaintext " + plaintext + " ciphertext " + ciphertext + " operations " + operations;
	}
	public String getOperations() {
		return operations;
	}
	public void setOperations(String operations) {
		this.operations = operations;
	}
	public float getMultiplicity() {
		return multiplicity;
	}
	public void setMultiplicity(float multiplicity) {
		this.multiplicity = multiplicity;
	}
	public float getScoreAdjusted() {
		return scoreAdjusted;
	}
	public void setScoreAdjusted(float scoreAdjusted) {
		this.scoreAdjusted = scoreAdjusted;
	}
	
}
