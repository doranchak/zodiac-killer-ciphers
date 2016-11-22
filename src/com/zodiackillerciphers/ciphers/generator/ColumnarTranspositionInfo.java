package com.zodiackillerciphers.ciphers.generator;

public class ColumnarTranspositionInfo {
	// selection of columns
	public int[] columns;
	// string prior to encoding
	public String decoded;
	
	public int length() {
		return columns.length;
	}
	
	public boolean regular() {
		return decoded.length() % length() == 0;
	}
	
	public String toString() {
		String c = "";
		for (int col : columns) {
			if (c.length() > 0) c += ",";
			c += "" + col;
		}
		return "columns " + c + " key length " + length() + " regular " + regular(); 
	}
}
