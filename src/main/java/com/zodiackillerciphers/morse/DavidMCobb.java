package com.zodiackillerciphers.morse;

import java.util.ArrayList;
import java.util.List;

import com.zodiackillerciphers.ciphers.Ciphers;

/** David M Cobb's ideas for Z32 (via Ryan Garlick)
 * https://www.evernote.com/l/AAGotDdUuk1BYL1A21mw2kU5E3iWaoguuzQ
 */
public class DavidMCobb {
	
	/** how many symbol sequences contain "121" encoded as digits using David M. Cobb's scheme? */
	public static void search() {
		for (char ch : new char[] { '0', '1' }) {
			for (String search : new String[] {"121", "122"}) {
				search(search, "", "", "", ch, 2);
			}
		}
	}
	
	/**
	 * output all character sequences that produce the given encoded number.
	 * 
	 * @search The number string we are looking for
	 * @morse The morse string we are considering
	 * @sequence The symbol sequence that produces the given morse sequence
	 * @morseFormatted Formatted version of Morse sequence
	 * @delimiter The delimiter between each encoded digit. can be '0' or '1'.
	 * @maxDelimiters The max number of times we allow a delimiter to appear between
	 *                two digits
	 * 
	 */
	public static void search(String search, String morse, String sequence, String morseFormatted, char delimiter, int maxDelimiter) {
		int count = valid(search, morse, delimiter, maxDelimiter);
//		System.out.println("searching: " + morse + " " + sequence + " " + count);
		if (count == -1) return;
		if (count == search.length()) {
			System.out.println(sequence.length() + "	" + sequence + "	" + "forward	" + Morse.toDotsDashes(""+delimiter) + "	" + Morse.toDotsDashes(morseFormatted) + "	" + search);
			System.out.println(sequence.length() + "	" + reverse(sequence) + "	" + "reverse	" + Morse.toDotsDashes(""+delimiter) + "	" + Morse.toDotsDashes(reverse(morseFormatted)) + "	" + search);
			if (Ciphers.cipher[4].cipher.toUpperCase().contains(sequence) || Ciphers.cipher[4].cipher.toUpperCase().contains(reverse(sequence))) {
				System.out.println("Match: " + sequence);
			}
			return;
		}
		for (Character key : Morse.morseTable.keySet()) {
			String val = Morse.morseTable.get(key);
			search(search, morse+val, sequence+key, morseFormatted + " " + val, delimiter, maxDelimiter);
		}
	}
	
	public static String reverse(String hi) {
		return new StringBuilder(hi).reverse().toString();
	}
	
	public static int valid(String search, String morse, char delimiter, int maxDelimiter) {
//		System.out.println("valid " + search + " " + morse);
		String[] dels = morse.split(delimiter == '0' ? "1" : "0");
		for (String del : dels) if (del.length() > maxDelimiter) return -1;
		String[] split = morse.split(""+delimiter);
		List<String> tocheck = new ArrayList<String>();
		for (String s : split) {
			if (s.length() == 0) continue;
			tocheck.add(s);
		}
		
		int i=0; int result = 0;
		for (int k=0; k<tocheck.size(); k++) {
			String s = tocheck.get(k);
//			System.out.println(k + " " + tocheck + " " + s);
			if (s.length() == 0) continue;
//			System.out.println("split: " + morse + " " + s);
//						System.out.println(s + " " + s.length() + " " + Integer.valueOf(""+search.charAt(i)));
			int comp = Integer.valueOf(""+search.charAt(k));
			if (k == tocheck.size()-1) {
				if (s.length() > comp) return -1; // too big
				if (s.length() == comp) result++; // exact match
				// otherwise, it could form substring of correct match in next iteration
			} else { 
				if (s.length() != comp) return -1;
				result++;
			}
			i++;
			if (result == search.length()) break;
		}
		return result;
	}
	
	public static void main(String[] args) {
//		String test = "000101001110101000";
//		String[] split = test.split("0");
		search();
//		System.out.println(valid("1", "01", '0', 3));
		
	}
	
}
