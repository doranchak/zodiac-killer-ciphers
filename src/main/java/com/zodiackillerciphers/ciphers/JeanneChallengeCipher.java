package com.zodiackillerciphers.ciphers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.io.FileUtil;

//CONFIDENTIAL
//CONFIDENTIAL
//CONFIDENTIAL
//
// DO NOT DISTRIBUTE
//
public class JeanneChallengeCipher {
	public static String cipher = "&P9LS2FA(=\"K*7#F6UR!:<>PF=A.UPFK86J4\"7*9M!R#/73(P;??!&V*51%L:4FDBPKU/+9<SA6,#*7#F6UKP=)D*59S%#OF!'\"#H6\"@<P;$*IJ(AI@GC>*(=8L48QTA-9F)*,9?P-JU??!GPT<=IAJ9H*&@8F,A9M*=<L=)#P6NUK*09F,AJ;&!$@A9P%=F*;6LN!7@E#*GAOC9ML,=7C/!N:QT*.=(56M";
	public static String[] cipherRows = new String[] { "&P9LS2FA(=\"K*7#F6UR!:<>PF=", "A.UPFK86J4\"7*9M!R#/73(P;??",
			"!&V*51%L:4FDBPKU/+9<SA6,#*", "7#F6UKP=)D*59S%#OF!'\"#H6\"@", "<P;$*IJ(AI@GC>*(=8L48QTA-9",
			"F)*,9?P-JU??!GPT<=IAJ9H*&@", "8F,A9M*=<L=)#P6NUK*09F,AJ;", "&!$@A9P%=F*;6LN!7@E#*GAOC9",
			"ML,=7C/!N:QT*.=(56M" };
	static List<Character> alphabet;
	static int count;
	
	static Map<Character, Character> key;
	static {
		key = new HashMap<Character, Character>();
	}
	
	/** produce the cipher alphabet */
	public static void alphabet() {
		alphabet = new ArrayList<Character>();
		for (int i=0; i<cipher.length(); i++) {
			char ch = cipher.charAt(i);
			if (alphabet.contains(ch)) continue;
			alphabet.add(ch);
		}
//		System.out.println(alphabet);
//		System.out.println(alphabet.size());
	}
	
	public static void testAlphabet() {
		alphabet();
	}
	
	/** compute all possible selections of up to N symbols.  for each selection,
	 * consider them as word breaks.  score them based on average word length and
	 * how many "double spaces" are present (I wouldn't expected two word break symbols to be
	 * adjacent)
	 */
	public static void wordBreakSearch(int N) {
		alphabet();
		count = 0;
		wordBreakSearch(0, new int[N]);
		System.out.println("TOTAL SELECTIONS: " + count);
	}
	public static void wordBreakSearch(int index, int[] breaks) {
		if (index == breaks.length) {
			processWordBreaks(breaks);
			count++;
			return;
		}
		
		int start = 0;
		if (index > 0) {
			start = breaks[index-1]+1;
		}
		
		for (int i=start; i<alphabet.size(); i++) {
			breaks[index] = i;
			wordBreakSearch(index+1, breaks);
		}
	}
	public static void processWordBreaks(int[] breaks) {
		StringBuffer sb = new StringBuffer(cipher);
		int adjacents = 0;
		for (int i=0; i<sb.length(); i++) {
			char c = sb.charAt(i);
			for (int j=0; j<breaks.length; j++) {
				char b = alphabet.get(breaks[j]);
				if (c==b) {
					sb.setCharAt(i, ' ');
					if (i>0 && sb.charAt(i-1) == ' ') adjacents++;
				}
			}
		}
		
		String[] split = sb.toString().replaceAll(" +", " ").split(" "); // remove adjacent spaces, so they don't mess up the word length counts
		float avgWordLength = 0;
		for (String s : split) avgWordLength += s.length();
		avgWordLength /= split.length;
		String bstr = Arrays.toString(breaks);
		double score = Math.abs(4.7-avgWordLength)*(1+adjacents);
		String tab = "	";
		//if (avgWordLength > 15) return;
		System.out.println(score + tab + avgWordLength + tab + adjacents + tab + bstr + tab + toAlpha(breaks));
		
	}
	public static String toAlpha(int[] breaks) {
		String result = "";
		for (int b : breaks) result += alphabet.get(b);
		return result;
	}
	
	public static void makeBatch() {
		List<String> list = FileUtil.loadFrom("/Users/doranchak/projects/zodiac/com.zodiackillerciphers.ciphers.JeanneChallengeCipher.wordBreakSearch.sorted.txt");
		int count = 0;
		for (String line : list) {
			if (line.startsWith("TOTAL")) continue;
			String[] split = line.split("	");
			String breaks = split[4];
			System.out.println("cipher_information=" + line);
			System.out.println(removeFromCipher(breaks));
			System.out.println();
			count++;
			if (count == 500000) break;
		}
	}
	static String removeFromCipher(String breaks) {
		Set<Character> set = new HashSet<Character>();
		for (int i=0; i<breaks.length(); i++) set.add(breaks.charAt(i));
		StringBuffer sb = new StringBuffer();
		for (int i=0; i<cipher.length(); i++) {
			char ch = cipher.charAt(i);
			if (set.contains(ch)) continue;
			sb.append(ch);
		}
		return sb.toString();
	}
	
	/** generate key from the given plaintext and symbols counted as word breaks.
	 */
	public static Map<Character, Character> keyFrom(String plaintext, String breaks) {
		Map<Character, Character> key = new HashMap<Character, Character>();
		plaintext = plaintext.replaceAll(" ", "");
		String cipher = removeFromCipher(breaks);
		if (plaintext.length() != cipher.length()) throw new RuntimeException("Length mismatch " + plaintext.length() + " " + cipher.length());
		for (int i=0; i<cipher.length(); i++) {
			char c= cipher.charAt(i);
			char p = plaintext.charAt(i);
			Character val = key.get(c);
			if (val == null) val = p;
			if (p != val) {
				throw new RuntimeException("Invalid plaintext! " + cipher + " " + plaintext);
			}
			key.put(c,p);
		}
		return key;
	}
	public static void testDecode() {
		Map<Character, Character> key = keyFrom(
				"MSIGETYOURLETTERANDSTOBESTRUTHFULISREALLYSIMPSONAFTERSREADINGTHELETTERSOHESIGNEATQUESTUONSISWHYWORLDYOUFUCKWITHHISWHERSKNOWHISMOUTHISONOHESTHERWITHHIMSOISNOTITHLOVERALISHOLLAHACKBOYSTS",
				"LA*!?");
		key.put('L', ' ');
		key.put('A', ' ');
		key.put('*', ' ');
		key.put('!', ' ');
		key.put('?', ' ');
//		print(key);
		key.clear();
		key.put('!',' ');
		key.put('"','U');
		key.put('#','E');
		key.put('(','Y');
		key.put(')','H');
		key.put('*',' ');
		key.put('+','D');
		key.put(',','H');
		key.put('-','W');
		key.put('.','B');
		key.put('/','A');
		key.put('2','O');
		key.put('4','F');
		key.put('6','T');
		key.put('7','L');
		key.put('8','U');
		key.put('9','I');
		key.put(':','A');
		key.put('<','N');
		key.put('=','O');
		key.put('>','D');
		key.put('?',' ');
		key.put('@','O');
		key.put('A',' ');
		key.put('B','R');
		key.put('C','L');
		key.put('D','E');
		key.put('F','T');
		key.put('G','U');
		key.put('I','W');
		key.put('J','H');
		key.put('K','R');
		key.put('L',' ');
		key.put('N','B');
		key.put('P', ' ');
		key.put('Q','C');
		key.put('R','R');
		key.put('S','G');
		key.put('T','K');
		key.put('U','E');
		print(key);
	}
	public static void print(Map<Character, Character> key) {
		System.out.println(key);
		System.out.println();
		for (String line : cipherRows) {
			System.out.println(line);
			for (int i=0; i<line.length(); i++) {
				char c = line.charAt(i);
				Character p = key.get(c);
				if (p == null) p = '_';
				System.out.print(p);
			}
			System.out.println();
			System.out.println();
		}
	}
	public static void main(String[] args) {
//		testAlphabet();
//		for (int i=1; i<6; i++) {
//			wordBreakSearch(i);
//		}
//		String test = "a    bcd  e ff";
//		System.out.println(test.replaceAll(" +", " "));
//		makeBatch();
		testDecode();
		
	}
}
