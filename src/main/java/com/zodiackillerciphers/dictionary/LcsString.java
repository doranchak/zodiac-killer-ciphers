package com.zodiackillerciphers.dictionary;

import java.util.List;

public class LcsString extends LongestCommonSubsequence<Character> {
	private String x;
	private String y;

	public LcsString(String from, String to) {
		this.x = from;
		this.y = to;
	}

	protected int lengthOfY() {
		return y.length();
	}

	protected int lengthOfX() {
		return x.length();
	}

	protected Character valueOfX(int index) {
		return x.charAt(index);
	}

	protected Character valueOfY(int index) {
		return y.charAt(index);
	}

	public String getHtmlDiff() {
		DiffType type = null;
		List<DiffEntry<Character>> diffs = diff();
		StringBuffer buf = new StringBuffer();

		for (DiffEntry<Character> entry : diffs) {
			if (type != entry.getType()) {
				if (type != null) {
					buf.append("</span>");
				}
				buf.append("<span class=\"" + entry.getType().getName() + "\">");
				type = entry.getType();
			}
			buf.append(escapeHtml(entry.getValue()));
		}
		buf.append("</span>");
		return buf.toString();
	}

	public boolean onlyVowelsAdded() {
		DiffType type = null;
		List<DiffEntry<Character>> diffs = diff();

		for (DiffEntry<Character> entry : diffs) {

			if (type != entry.getType()) {
				if (entry.getType().getName().equals("add")) {
					if (entry.getValue() != 'A' && entry.getValue() != 'E'
							&& entry.getValue() != 'I'
							&& entry.getValue() != 'O'
							&& entry.getValue() != 'U')
						return false;
				}
			}
		}
		return true;
	}

	private String escapeHtml(Character ch) {
		switch (ch) {
		case '<':
			return "&lt;";
		case '>':
			return "&gt;";
		case '"':
			return "\\&quot;";
		default:
			return ch.toString();
		}
	}

	// EXAMPLE. Here's how you use it.
	public static void main(String[] args) {
		// LcsString seq = new
		// LcsString("<p>the quick brown fox</p>","<p>the <b>Fast</b> brown dog</p>");
		LcsString seq = new LcsString("HRDN", "HARDEN");
		System.out.println("LCS: " + seq.getLcsLength());
		System.out.println("Edit Dist: " + seq.getMinEditDistance());
		System.out.println("Backtrack: " + seq.backtrack());
		System.out.println("HTML Diff: " + seq.getHtmlDiff());
	}

}