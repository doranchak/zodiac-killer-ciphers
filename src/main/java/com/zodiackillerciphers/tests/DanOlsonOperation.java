package com.zodiackillerciphers.tests;

public class DanOlsonOperation {
	/** choice of line one */
	public int line1;
	/** do we reverse line1 ? */
	public boolean reverse1;
	/** choice of line two */
	public int line2;
	/** do we reverse line2 ? */
	public boolean reverse2;
	
	/** combination operator:  interleave if true, simple splice otherwise */
	public boolean interleave;
	
	public DanOlsonOperation() {
		line1 = 0;
		line2 = 0;
		reverse1 = false;
		reverse2 = false;
		interleave = false;
	}
	public String reverse(String str) {
		return new StringBuffer(str).reverse().toString();
	}
	/** produce the merged line */
	public String produce() {
		String row1 = DanOlson.lineFrom(line1);
		if (reverse1) row1 = reverse(row1);
		String row2 = DanOlson.lineFrom(line2);
		if (reverse2) row2 = reverse(row2);
		
		if (interleave) {
			String result = "";
			for (int i=0; i<row1.length(); i++) {
				result += row1.charAt(i);
				result += row2.charAt(i);
			}
			return result;
		}
		return row1 + row2;
	}
	
	public String toString() {
		return line1 + " " + reverse1 + " " + line2 + " " + reverse2 + " " + interleave;
	}
}
