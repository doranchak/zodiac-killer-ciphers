package com.zodiackillerciphers.ciphers.w168;

public class StringUtils {

	public static StringBuilder[] toStringBuilder(String[] arr) {
		StringBuilder[] sb = new StringBuilder[arr.length];
		int i=0;
		for (String s : arr) sb[i++] = new StringBuilder(s);
		return sb;
	}

	public static StringBuilder[] toRows(StringBuilder sb) {
		StringBuilder[] arr = new StringBuilder[6];
		for (int i=0; i<6; i++) {
			arr[i] = new StringBuilder(sb.substring(i*28,i*28+28));
		}
		return arr;
	}

	public static StringBuilder toLine(StringBuilder[] rows) {
		StringBuilder sb = new StringBuilder();
		for (StringBuilder row : rows) sb.append(row);
		return sb;
	}

	public static StringBuilder toLine(String[] rows) {
		StringBuilder sb = new StringBuilder();
		for (String row : rows) sb.append(row);
		return sb;
	}
	
	/** produce html table representing this StringBuilder array */ 
	public static String html(StringBuilder[] sb, String className) {
		String html = "<table class=\"" + className + "\">";
		for (StringBuilder s : sb) {
			html += "<tr>";
			for (int i=0; i<s.length(); i++) {
				html += "<td>" + s.charAt(i) + "</td>";
			}
			html += "</tr>";
		}
		html += "</table>";
		return html;
	}

}
