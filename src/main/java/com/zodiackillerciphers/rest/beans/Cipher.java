package com.zodiackillerciphers.rest.beans;

import java.util.HashMap;
import java.util.Map;

public class Cipher {
	
	String name;
	String description;
	
	// raw unprocessed ciphertext
	String ciphertextRaw;
	
	// list of cipher units
	String[] cipherUnits;
	
	// optional word delimiter
	String wordDelimiter;

	// optional cipher unit delimiter
	String cipherUnitDelimiter;
	
	// cipher stream as an integer sequence (generated)
	int[] cipherNumeric;

	/** simplest case: each character is a cipher unit.  no word delimiters. */
	public Cipher(String ciphertextRaw, String name, String description) {
		this.ciphertextRaw = ciphertextRaw;
		this.name = name;
		this.description = description;
		makeCipherUnits(); 
		makeCipherStream();
	}
	
	/** convert raw cipher into array of cipher units */
	public void makeCipherUnits() {
		cipherUnits = new String[ciphertextRaw.length()];
		for (int i=0; i<ciphertextRaw.length(); i++)
			cipherUnits[i] = ""+ciphertextRaw.charAt(i);
	}
	
	/** convert stream of cipher units into stream of integers */
	public void makeCipherStream() {
		Map<String, Integer> decoderMapNumeric = new HashMap<String, Integer>();
		cipherNumeric = new int[cipherUnits.length];
		int current = 1;
		
		for (int i=0; i<cipherUnits.length; i++) {
			String unit = cipherUnits[i];
			Integer val = decoderMapNumeric.get(unit);
			if (val == null) {
				val = current++;
				decoderMapNumeric.put(unit, val);
			}
			cipherNumeric[i] = val;
		}
	}

	public String getCiphertextRaw() {
		return ciphertextRaw;
	}

	public void setCiphertextRaw(String ciphertextRaw) {
		this.ciphertextRaw = ciphertextRaw;
	}

	public String[] getCipherUnits() {
		return cipherUnits;
	}

	public void setCipherUnits(String[] cipherUnits) {
		this.cipherUnits = cipherUnits;
	}

	public String getWordDelimiter() {
		return wordDelimiter;
	}

	public void setWordDelimiter(String wordDelimiter) {
		this.wordDelimiter = wordDelimiter;
	}

	public String getCipherUnitDelimiter() {
		return cipherUnitDelimiter;
	}

	public void setCipherUnitDelimiter(String cipherUnitDelimiter) {
		this.cipherUnitDelimiter = cipherUnitDelimiter;
	}

	public int[] getCipherNumeric() {
		return cipherNumeric;
	}

	public void setCipherNumeric(int[] cipherNumeric) {
		this.cipherNumeric = cipherNumeric;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
