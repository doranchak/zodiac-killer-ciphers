package com.zodiackillerciphers.tests;

import java.util.Arrays;

import com.zodiackillerciphers.lucene.ZKDecrypto;

public class IntersectionBean {
	
	// intersections are marked uppercase letters 
	public String[] cipher;
	// number of intersections
	public int intersections;
	public float zkscore;
	public float zkscoreRev;
	public String letters;
	public String lettersRev;
	public boolean[][] marked;
	public IntersectionBean(String[] cipher, int intersections, boolean[][] marked, String letters, float zkscore) {
		super();
		this.cipher = cipher;
		this.intersections = intersections;
		this.marked = marked;
		this.letters = letters;
		this.zkscore = zkscore;
		this.lettersRev = new StringBuilder(letters).reverse().toString();
		this.zkscoreRev = ZKDecrypto.calcscore(new StringBuffer(this.lettersRev));
	}
	public String toString() {
		return intersections + " " + Arrays.toString(cipher);
	}
	
	public String toHtml() {
		String html = "Count: " + intersections + "<br>";
		html += "Letters: " + letters + ", ZKScore: " + zkscore + "<br>";
		html += "Letters (reversed): " + lettersRev + ", ZKScore: " + zkscoreRev+ "<br>";
		html += "<table>";
		for (int row=0; row<cipher.length; row++) {
			html += "<tr>";
			for (int col=0; col<cipher[0].length(); col++) {
				String cl = "";
				if (row == 0 || row == cipher.length - 1 || col == 0
						|| col == cipher[row].length() - 1) cl = "ring";
				else if (marked[row][col]) cl = "marked";
				
				
				html += "<td class=\"" + cl + "\">";
				html += cipher[row].charAt(col);
				html += "</td>";
			}
			html += "</tr>";
		}
		html += "</table>";
		return html;
	}
	
	
}
