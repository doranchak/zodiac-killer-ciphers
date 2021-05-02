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
	
	// if the cipher is in a grid layout, this is the grid width.  0 for non-grid ciphers.
	int width = 0;
	
	// split up grid cipher into rows
	String[] rows; 

	/** simplest case: each character is a cipher unit.  no word delimiters. */
	public Cipher(String ciphertextRaw, String name, String description, int width) {
		this.ciphertextRaw = ciphertextRaw;
		this.name = name;
		this.description = description;
		this.width = width;
		makeCipherUnits(); 
		makeCipherStream();
		makeCipherRows();
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
	
	/** convert cipher text into rows */
	public void makeCipherRows() {
		if (width == 0) {
			// this cipher is not in a grid layout, so just make it one row.
			rows = new String[] { ciphertextRaw };
			return;
		}
		int height = ciphertextRaw.length() / width + (ciphertextRaw.length() % width == 0 ? 0 : 1);
		rows = new String[height];
		for (int i=0; i<height; i++) {
			rows[i] = ciphertextRaw.substring(i*width, i*width + width);
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

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public String[] getRows() {
		return rows;
	}

	public void setRows(String[] rows) {
		this.rows = rows;
	}
}
