package com.zodiackillerciphers.ciphers.generator.z340;

/** make Z340-like cipher:
 * 		- transpose the 2 9-line sections 
 *      - add the same shift error in the 2nd section  
 *		- then homophonically encode the result   
 **/
public class Z340Generator {
	public static String generate(String plaintext) {
		if (plaintext == null || plaintext.length() != 340) 
			throw new IllegalArgumentException("Bad plaintext length. Expected 340 but found " + plaintext.length());
		return null;
	}
}
