package com.zodiackillerciphers.tests;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.dictionary.WordFrequencies;

/** http://scienceblogs.de/klausis-krypto-kolumne/2020/05/06/a-homophonic-polybius-challenge */
public class KlausHomophonicPolybius {
	public static String cipher = "MG VI VH PG JG KE KB KB VR LU JG ZG JE VN ZD VC KA ME TB VR JS TU PG KA JS VX VB VH PE KE VS KB PB KA MA PF ZX KA MB PA ME PI VE KI VF KB VA PD MG ZC VB KU KS VH PE KA VC YX VE KA TS TC KA VB KI KE VB ME PR MA KH KB VC TI TU PG KA VB VF ZF VH PE KG VN KB PC KA ME PX ZR KA MD PE MG PI VG KU VR KN VD VR MU MG VS KG KH PF PE VU KE JA KN JN VF VH PE JA VB KN PD KG ME PR ZR KG VI KG MG PI VD PG MG ZG KG ZB VR KN VD KU MR VU ZD VF MU VU PE KG KE KI PR PG VU ZD VB ZF VG KG VB VD KG VN KI KE VD VG PD MG ZD KE KD VD KU KB MC KG KB KI KN KA TI KG KA KS PG ZR KN KD VB KG KD MG KB KD MI KU LI VH KG";
	public static String pt = "ATTHEENDOFEVERYSEASONTHENORTHERNLEAGUECHAMPIONPLAYSINTHESUPERSERIESAGAINSTTHESOUTHERNLEAGUECHAMPIONSOFAREIGHTEENNOTHERNLEAGUETEAMSHAVEWONSIXTYOFTHEEIGHTYSUPERSERIESPLAYEDSINCENINETEENHUNDREDANDFIFTEEN";
	
	public static String[] topKey = new String[] {"XENDI", "FASBH", "RGBCU"};
	
	public static void convert() {
		String alpha = Ciphers.alphabet[3];
		int index = 0;
		String converted = "";
		Map<String, Character> digraphToUnigraph = new HashMap<String, Character>();
		int index2 = 0;
		
		Map<Character, Set<Character>> p2c1 = new HashMap<Character, Set<Character>>(); 
		Map<Character, Set<Character>> p2c2 = new HashMap<Character, Set<Character>>(); 
		
		for (String ct : cipher.split(" ")) {
			Character u = digraphToUnigraph.get(ct);
			if (u == null) {
				u = alpha.charAt(index++);
				digraphToUnigraph.put(ct, u);
			}
			converted += u;
			char p = pt.charAt(index2++);
			System.out.println(ct + " " + p);
			
			char c1 = ct.charAt(0);
			char c2 = ct.charAt(1);
			
			Set<Character> set = p2c1.get(p);
			if (set == null) {
				set = new HashSet<Character>();
				p2c1.put(p, set);
			}
			set.add(c1);
			
			set = p2c2.get(p);
			if (set == null) {
				set = new HashSet<Character>();
				p2c2.put(p, set);
			}
			set.add(c2);
		}
		System.out.println(digraphToUnigraph);
		System.out.println(converted);
		
		System.out.println(p2c1);
		System.out.println(p2c2);
	}
	
	static void deriveTopKey() {
		String[] rewritten = new String[] {"", "", ""};
		deriveTopKey(new int[] {0,1,2}, -1, rewritten);
	}
	static void deriveTopKey(int[] positions, int index, String[] rewritten) {
		if (index == topKey[0].length()) { // break condition
			StringBuffer sb = new StringBuffer();
			for (String s : rewritten) sb.append(s);
			int count = 0;
			for (int i=sb.length()-2; i>=0; i--) {
				char c1 = sb.charAt(i);
				char c2 = sb.charAt(i+1);
				if (c1 < c2) {
					count++;
				} else break;
			}
			System.out.println(count + " " + Arrays.toString(rewritten));
			return;
		}
		String[] copy = new String[] {"","",""};
		System.arraycopy(rewritten, 0, copy, 0, rewritten.length);
		if (index > -1)
			for (int i = 0; i < positions.length; i++) {
				copy[i] += topKey[positions[i]].charAt(index); // reorder a column using the given positions
			}
		//System.out.println("pos " + Arrays.toString(positions) + " index " + index + " rewritten " + Arrays.toString(copy));
		// now explore all possible reoderings
		deriveTopKey(new int[] {0,1,2}, index+1, copy);
		deriveTopKey(new int[] {0,2,1}, index+1, copy);
		deriveTopKey(new int[] {1,0,2}, index+1, copy);
		deriveTopKey(new int[] {1,2,0}, index+1, copy);
		deriveTopKey(new int[] {2,0,1}, index+1, copy);
		deriveTopKey(new int[] {2,1,0}, index+1, copy);
	}
	
	public static void findKeyWord() {
		WordFrequencies.init();
		for (String word : WordFrequencies.map.keySet()) {
			String uniq = "";
			for (int i=0; i<word.length(); i++) {
				char c = word.charAt(i);
				if (!uniq.contains(""+c)) uniq += c;
			}
			if (uniq.contains("ENDIX")) {
				System.out.println(uniq + " " + word + " " + WordFrequencies.percentile(word));
			}
			if (uniq.startsWith("J") || uniq.startsWith("K")) {
				if (uniq.substring(1).startsWith("ENDI")) {
					System.out.println(uniq + " " + word + " " + WordFrequencies.percentile(word));
				}
			}
		}
	}
	
	public static void main(String[] args) {
//		convert();
//		deriveTopKey();
		findKeyWord();
	}
}
