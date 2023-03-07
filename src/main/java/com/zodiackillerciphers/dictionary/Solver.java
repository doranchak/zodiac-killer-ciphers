package com.zodiackillerciphers.dictionary;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.tests.SpanishCipher;

/** dictionary-based solver for simple substitution ciphers. */ 
public class Solver {
	
	public static long score(List<String> words) {
		long result = 1;
		for (String word : words)
			result *= (1+WordFrequencies.percentile(word));
		return result;
	}
	public static String out(List<String> words) {
		String result = ""+score(words);
		for (String word : words) result += " " + word;
		return result;
	}
	
	
	/** brute force search for all word combinations that fit the given ciphertext words */
	public static void solve(String[] cipherwords, String cipherFull) {
		WordFrequencies.init();
		
		Map<Character, Character> map = null;
		List<String> words = new ArrayList<String>();
		solve(cipherwords, 0, map, words, cipherFull);
	}
	public static void solve(String cipher, String cipherFull) {
		solve(cipher.split(" "), cipherFull);
	}
	
	/** recursively solves cipher split up into words.
	 * 
	 * @param cipherwords cipher text broken up into words
	 * @param which which cipher word currently under consideration
	 * @param map current state of the cipher-plaintext mapping imposed by the search
	 * @param words current word combinations under consideration
	 * @param cipherFull the full ciphertext for context
	 */
	public static void solve(String[] cipherwords, int which, Map<Character, Character> map, List<String> words, String cipherFull) {
		if (which == cipherwords.length) {
			System.out.println(out(words) + " " + Ciphers.isHomophonic(map) + " " + Ciphers.decode(cipherFull, map, false));
			return;
		}
		for (String word : WordFrequencies.map.keySet()) {
			Map<Character, Character> newMap = Ciphers.decoderMapFor(map, cipherwords[which], word);
			if (newMap == null) continue;
			List<String> newWords = new ArrayList<String>(words);
			newWords.add(word);
			solve(cipherwords, which+1, newMap, newWords, cipherFull);
		}
	}
	
	/** find combinations of n cipher words that have the most repeats.  this is to reduce the search space. */
	
	public static void repeats(String[] words, int n) {
		repeats(words, 0, n, "");
	}
	public static void repeats(String[] words, int which, int n, String selected) {
		String indent = "";
		for (int i=0; i<which+1; i++) indent += "	";
		//System.out.println(indent + "w:" + which + " n:" + n + " " + selected);
		if (n == 0) {
			String s = selected.replaceAll(" ", "");
			float u = uniques(s);
			u /= s.length();
			System.out.println(u + " " + selected);
			return;
		}
		//System.out.println(indent + "loop begin");
		for (int i=which; i<words.length-n+1; i++) {
			String newSelected = selected + " " + words[i];
			//System.out.println(indent + "i:" + i + " words[i]:" + words[i]);
			repeats(words, i+1, n-1, newSelected);
		}
//		System.out.println(indent + "loop end");
	}
	
	public static int uniques(String str) {
		Set<Character> set = new HashSet<Character>();
		for (int i=0; i<str.length(); i++)
			set.add(str.charAt(i));
		return set.size();
		
	}
	
	public static void findRepeats(String cipher, int min, int max) {
		String[] split = cipher.split(" ");
		for (int i=min; i<=max; i++) {
			repeats(split, i);
		}
	}
	
	public static void main(String[] args) {
//		String[] words = new String[] {"LSMB", "GRDQ", "HBP", "CPHUMB"};
//		repeats(words, 3);
		//findRepeats("LSMB GRDQ HBP CPHUMB");
		//findRepeats("DRJ MLW CLTV LO EZLO BRWO ELWO GR STIZO LG EYVV OLNY L MZLWMY RW BRTWI VRZY FTS MLQQYD");
		//findRepeats("OPFHNK BYE HBLPGYK ESYI BO HGFYO RIPYGY");
		//solve("HBLPGYK RIPYGY HGFYO", "OPFHNK BYE HBLPGYK ESYI BO HGFYO RIPYGY");
		//findRepeats("XYZRUWZTKNG GUZYKNUP RYZKWUR XYZYKLAF GRUZYWS XRZZQ GUZTY GXTYZYXT XZURCAVE");
		//findRepeats("UYX OW F KBOZB LQY F MOPX LQY XQ JPQM GYWX XEFPJ EQM NYSE LQYVB DQFPD XQ UB NFWWFPD LQY MQPX EOZB PFIQP XQ JFSJ OVQYPA OPLNQVB");
		//findRepeats("DPNFN GX J FNTJFRJKSB LSUXN VJFJSSNS KNDZNNH DPN VFUKSNTX UO DPN VPBXGLGXD JHM DPUXN UO DPN LFBVDUIFJVPNF");
//		solve("THE", "");
//		solve("AOPQRISH HLOPQIc HFOPQ OH",
//				"ABCDE FG HIJK LMNGB AOPQRISH TUPQKV LUNW NRXOYZI Ia baGKH cWGD dRe YRJG fUHCN JOgG NhKF OH XCE Mi jRk YOS Xkb HFOPQ DkTl bR HLOPQIc HSGOm GF D XME YhCni Ra YWRY JPg nQo UNp KOoq");
		//		findRepeats("ABCDE FG HIJK LMNGB AOPQRISH TUPQKV LUNW NRXOYZI Ia baGKH cWGD dRe YRJG fUHCN JOgG NhKF OH XCE Mi jRk YOS Xkb HFOPQ DkTl bR HLOPQIc HSGOm GF D XME YhCni Ra YWRY JPg nQo UNp KOoq");
//		solve("AOPQRISH HLOPQIc", "");
//		solve("CDECDEDE", "");
//		SpanishCipher.solveSpanishHom();
//		solve("ABCDEB ABBC", "");
//		WordFrequencies.init();
//		System.out.println(WordFrequencies.percentile("GREEN"));
//		System.out.println(WordFrequencies.percentile("VIRIDESCENT"));
		//solve("XEQBJOZZO QJXCOCOW LQZJBL EZJ LQXJUDFVL", "LQXJUDFVL QZ FMZLB LQZJBL EZJ XO XEQBJOZZO EZJ UCXL QJXCOCOW");
//		findRepeats("894RPV YIN 24N1 K34ND TIHS N341 VIPNO K3N94KV V93 ZADD 89AO DIS3 YNIE", 4, 4);
//		solve("K3N94KV K34ND N341 V93", "894RPV YIN 24N1 K34ND TIHS N341 VIPNO K3N94KV V93 ZADD N341 89AO DIS3 YNIE");;
//		solve("WQHDNDII KGOVQQF BPQQG","");
//		solve("ABCABCDABEABE", "");
		//solve("AB CDBE EFDCC", "");
		solve("ABCDBE EFDCC", "");
		
	}
}
