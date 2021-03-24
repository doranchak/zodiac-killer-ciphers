package com.zodiackillerciphers.ciphers.generator;


/** represents the candidate cipher text */
public class CandidateCiphertext {
	public CandidateCiphertext() {
		
	}
	public CandidateCiphertext(String cipher) {
		super();
		this.cipher = new StringBuffer(cipher);
	}

	/** cipher text */
	public StringBuffer cipher;
	
	/** pairs of positions that must contain the same symbol.
	 * these constraints are imposed by locked pivots. 
	 */
	//List<Position[]> fixedPairs;

	public String toString() {
		return "cipher [" + cipher + "]";
	}
	
	public String toStringFormatted() {
		String sep = System.getProperty("line.separator");
		
		String result = sep;
		for (int i=0; i<cipher.length(); i+=17) {
			result += cipher.substring(i,i+17) + sep;
		}
		return result;
	}
	
	
}
