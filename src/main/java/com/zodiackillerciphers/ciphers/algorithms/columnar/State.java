package com.zodiackillerciphers.ciphers.algorithms.columnar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** some things generated during the encrypt/decrypt process */
public class State {
	
	Variant variant;
	Boolean regular;
	
	StringBuilder plaintext;
	StringBuilder ciphertext;
	boolean removeSpaces;
	int[] key;

	static char PAD_CHAR = '~';
	
	/** texts with padding characters */
	StringBuilder plaintextWithPadding;
	StringBuilder ciphertextWithPadding;
	/** index of all padding characters in the cipher text */
	int[] pads;
	/** map of ciphertext positions to plaintext positions */
	Map<Integer, Integer> c2p;
	/** map of plaintext positions to ciphertext positions */
	Map<Integer, Integer> p2c;
	
	public State(Variant variant, StringBuilder plaintext, StringBuilder ciphertext, int[] key) {
		this(variant, plaintext, ciphertext, key, false);
	}
	public State(Variant variant, StringBuilder plaintext, StringBuilder ciphertext, int[] key, boolean removeSpaces) {
		this.variant = variant;
		this.plaintext = plaintext;
		this.ciphertext = ciphertext;
		this.key = key;
		this.removeSpaces = removeSpaces;
	}
	
	/** map cipher position to plaintext position */
	void map(int cipherPosition, int plainPosition) {
		if (c2p == null) c2p = new HashMap<Integer, Integer>();
		if (p2c == null) p2c = new HashMap<Integer, Integer>();
		c2p.put(cipherPosition, plainPosition);
		p2c.put(plainPosition, cipherPosition);
	}
	
	void clear() {
		plaintext = null;
		ciphertext = null;
		ciphertextWithPadding = null;
		pads = null;
		c2p = null;
		p2c = null;
	}
	
	public void padPlaintext() {
		plaintextWithPadding = new StringBuilder(plaintext);
		int leftover = plaintext.length() % key.length; // if nonzero, then we have irregular columnar, so just pad it then remove the padded chars later.
		regular = true;
		if (leftover > 0) {
			regular = false;
			char pad = PAD_CHAR; 
			for (int i = 0; i < (key.length - leftover); i++)
				plaintextWithPadding.append(pad); // bring length of plaintext up to next multiple of key width.
		}
	}
	
	/** unpad the ciphertext. track where any pad characters were found.  keep a copy of the padded ciphertext. */
	public void unpadCiphertext() {
		List<Integer> positions = new ArrayList<Integer>();
		ciphertextWithPadding = new StringBuilder(ciphertext);
		ciphertext = new StringBuilder();
		for (int i=0; i<ciphertextWithPadding.length(); i++) {
			char ch = ciphertextWithPadding.charAt(i);
			if (ch == PAD_CHAR) {
				positions.add(i);
			} else ciphertext.append(ch);
		}
		pads = new int[positions.size()];
		for (int i=0; i<positions.size(); i++) pads[i] = positions.get(i);
	}
	
	public void encode() {
		if (removeSpaces) plaintext = new StringBuilder(plaintext.toString().replaceAll(" ", ""));
		padPlaintext();
		ciphertext = new StringBuilder();
		StringBuilder pt = plaintext;
		if (!regular) pt = plaintextWithPadding;
		
		for (int i=0; i<pt.length(); i++) {
			int j = c2p(i, pt, key);
			ciphertext.append(pt.charAt(j));
			map(i,j);
		}
		
		// remove padded characters if this was an irregular columnar
		unpadCiphertext();
	}

	public void decode() {
		if (removeSpaces) ciphertext = new StringBuilder(ciphertext.toString().replaceAll(" ", ""));
		// if this is irregular, figure out where any pad characters might appear
		
		StringBuilder ciphertextCopy = ciphertext;
		
		plaintext = new StringBuilder(ciphertext);
		encode();
		
		ciphertext = ciphertextCopy;
		ciphertextWithPadding = new StringBuilder(ciphertext);
		for (int i=0; i<pads.length; i++) {
			ciphertextWithPadding.insert(pads[i], PAD_CHAR);
		}
		
		plaintextWithPadding = new StringBuilder();
		for (int i=0; i<ciphertextWithPadding.length(); i++) {
			int j = p2c.get(i);
			plaintextWithPadding.append(ciphertextWithPadding.charAt(j));
		}
		
		ciphertext = unpad(ciphertextWithPadding);
		plaintext = unpad(plaintextWithPadding);
	}
	
	public static StringBuilder unpad(StringBuilder text) {
		StringBuilder unpadded = new StringBuilder();
		for (int i=0; i<text.length(); i++) {
			char ch = text.charAt(i);
			if (ch != PAD_CHAR) {
				unpadded.append(ch);
			}
		}
		return unpadded;
	}
	
	/**
	 *  given a position in the cipher text, return the corresponding position in the plaintext 
	 */
	public int c2p(int i, StringBuilder plaintext, int[] key) {
		int L = plaintext.length();
		int w = key.length;
		int rows = L/w;
		int n = i/rows;
		int col = key[n];
		int row = i % rows;
		
		boolean startAtBottom = false;
		if (variant == Variant.BOTTOM_TO_TOP) startAtBottom = true;
		else if (variant == Variant.ALTERNATING_1 && n % 2 == 1) startAtBottom = true;
		else if (variant == Variant.ALTERNATING_2 && n % 2 == 0) startAtBottom = true;
		
		if (startAtBottom) row = rows - 1 - row; // count from the bottom instead
		return row*w + col;
	}

	@Override
	public String toString() {
		return "State [variant=" + variant + ", regular=" + regular + ", plaintext=" + plaintext + ", ciphertext=" + ciphertext + ", key="
				+ Arrays.toString(key) + ", plaintextWithPadding=" + plaintextWithPadding + ", ciphertextWithPadding="
				+ ciphertextWithPadding + ", pads=" + Arrays.toString(pads) + ", c2p=" + c2p + ", p2c=" + p2c + "]";
	}
	
}
