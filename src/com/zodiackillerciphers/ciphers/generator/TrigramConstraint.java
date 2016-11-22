package com.zodiackillerciphers.ciphers.generator;

/** represents a possible trigram constraint to enforce in a candidate plain/cipher text. */
public class TrigramConstraint {

	/** trigram #1 is the one that repeats in the same column */
	public String trigram1;
	/** trigram #2 is the one that is joined to the first trigram */
	public String trigram2;
	
	/** column for the trigram1 */
	public int col1;
	/** row the copy of trigram1 appears in */
	public int row1;
	
	
	
	
}
