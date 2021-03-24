package com.zodiackillerciphers.lucene;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class VigenereOLD {
	public static String vig = "BMGOSOBONWMZNZUBATGUNQFWFLMNQFWFJQOBPWSMEIPLLNSETBAWFBOJQBWSSPVTCFHJUHFHWSSOBOVWSSHFBFWESMTJFQ";
	public static void test() {
		LuceneService.init();
		for (int i=0; i<7;i++ ) {
			Map<Character, Integer> map = new HashMap<Character, Integer>();
			int j=i;
			while (j<vig.length()) {
				char c = vig.charAt(j);
				Integer val = map.get(c);
				if (val == null) val = 0;
				val++;
				map.put(c,val);
				j+=7;
			}
			for (Character c : map.keySet()) {
				System.out.println(i+","+c+","+map.get(c));
			}
		}
		System.out.println('E'-'B');
		
		String tops = "BFWSSOT";
		String alpha = "ETAO";
		
		float best = 0;

		for (int a1 = 0; a1 < alpha.length(); a1++) {
			for (int a2 = 0; a2 < alpha.length(); a2++) {
				for (int a3 = 0; a3 < alpha.length(); a3++) {
					for (int a4 = 0; a4 < alpha.length(); a4++) {
						for (int a5 = 0; a5 < alpha.length(); a5++) {
							for (int a6 = 0; a6 < alpha.length(); a6++) {
								for (int a7 = 0; a7 < alpha.length(); a7++) {
									int i=0; String s = "";
									for (int j=0; j<vig.length(); j++) {
										char toShift = vig.charAt(j);
										int a=0;
										if (i==0) a=a1;
										else if (i==1) a=a2;
										else if (i==2) a=a3;
										else if (i==3) a=a4;
										else if (i==4) a=a5;
										else if (i==5) a=a6;
										else if (i==6) a=a7;
										s+=shift(toShift,tops.charAt(i),alpha.charAt(a));
										i++;
										i%=7;
									}
									Settings.cipdec = new StringBuffer(vig.toLowerCase());
									Settings.cipher = new StringBuffer(vig.toLowerCase());
									BestMatches bm = Scorer.score(new StringBuffer(s.toLowerCase()), false);
									// TODO: this was obsoleted: if (bm.sum > 0.1) {
									//	System.out.println(bm.sum + "," + a1+","+a2+","+a3+","+a4+","+a5+","+a6+","+a7+": "+s);
									//	//best = bm.sum;
									//}
								}
							}
						}
					}
				}
			}
		}
	}
	public static char shift(char toShift, char from, char to) {
		int diff = to-from;
		int n = ((int) toShift)+diff;
		if (n < 65) n+=26;
		if (n > 90) n-=26;
		return (char) n;
	}

	public static int[] mutate(int[] shifts) {
		int[] result = new int[shifts.length];
		for (int i=0; i<shifts.length; i++) result[i] = shifts[i];
		int i=(int) (Math.random()*result.length);
		result[i] = (int) (Math.random()*26);
		return result;
	}
	public static String shift(String v, int[] shifts) {
		int i=0;
		String s = "";
		for (int j=0; j<v.length(); j++) {
			char ch = v.charAt(j);
			ch += shifts[i];
			if (ch > 90) ch -= 26;
			s += ch;
			i++;
			i%=shifts.length;
		}
		return s;
	}
	
	static Map<Character, Float> freqs = new HashMap<Character, Float>();
	static {
		freqs.put('E',.12702f);
		freqs.put('T',.09056f);
		freqs.put('A',.08167f);
		freqs.put('O',.07507f);
		freqs.put('I',.06966f);
		freqs.put('N',.06749f);
		freqs.put('S',.06327f);
		freqs.put('H',.06094f);
		freqs.put('R',.05987f);
		freqs.put('D',.04253f);
		freqs.put('L',.04025f);
		freqs.put('C',.02782f);
		freqs.put('U',.02758f);
		freqs.put('M',.02406f);
		freqs.put('W',.02360f);
		freqs.put('F',.02228f);
		freqs.put('G',.02015f);
		freqs.put('Y',.01974f);
		freqs.put('P',.01929f);
		freqs.put('B',.01492f);
		freqs.put('V',.00978f);
		freqs.put('K',.00772f);
		freqs.put('J',.00153f);
		freqs.put('X',.00150f);
		freqs.put('Q',.00095f);
		freqs.put('Z',.00074f);	}
	
	public static float error(String s) {
		Map<Character, Integer> counts = new HashMap<Character, Integer>();
		for (int i=0; i<s.length();i++) {
			Integer val = counts.get(s.charAt(i));
			if (val == null) val = 0;
			val++;
			counts.put(s.charAt(i), val);
		}
		float error = 0;
		for (Character ch : freqs.keySet()) {
			float f1 = freqs.get(ch);
			Integer count = counts.get(ch);
			if (count == null) count = 0;
			float f2 = ((float)count)/s.length();
			error += Math.abs(f1-f2);
		}
		return error;
	}
	
	static Set<String> grams = new HashSet<String>();
	static {
		grams.add("TH");
		grams.add("HE");
		grams.add("IN");
		grams.add("EN");
		grams.add("NT");

		grams.add("THE");
		grams.add("AND");
		grams.add("THA");
		grams.add("ENT");
		grams.add("ING");

	}
	public static int grams(String s) {
		Map<String, Integer> map = new HashMap<String, Integer>();
		for (int i=0; i<s.length()-1; i++) {
			String n = s.substring(i,i+2);
			Integer val = map.get(n);
			if (val == null) val = 0;
			val++;
			map.put(n,val);
		}
		for (int i=0; i<s.length()-2; i++) {
			String n = s.substring(i,i+3);
			Integer val = map.get(n);
			if (val == null) val = 0;
			val++;
			map.put(n,val);
		}
		int result = 0;
		for (String n : map.keySet()) {
			if (grams.contains(n)) {
				result += map.get(n);
			}
		}
		return result;
	}
	
	public static String dumpShifts(int[] sh) {
		String s = "(";
		for (int i=0; i<sh.length; i++) {
			s+=sh[i]+",";
		}
		s += ")";
		return s;
	}
	
	public static void evolve() {
		int[] shifts = new int[7];
		
		float best = 0;// = Float.MAX_VALUE;
		int gen = 0; int last = 0;
		while (true) {
			//if (gen % 1000 == 0) System.out.println(gen);
			int[] newShifts = mutate(shifts);
			String sh = shift(vig, newShifts);
			float newScore = grams(sh)*(1-error(sh))/20;
			if (newScore > best) {
				System.out.println(newScore + ", " + dumpShifts(newShifts) + ", " + sh);
				best = newScore;
				shifts = newShifts;
				last = gen;
			}
			if (gen-last > 50000) {
				shifts = new int[7];
				last = gen;
				best = 0; //Float.MAX_VALUE;
			}
			gen++;
		}
		
	}
	
	public static void main(String[] args) {
		//evolve();
		//System.out.println(error("ASONECANTELLBYTHISSIMPLEEXAMPLEEVENAVERYSHORTMESSAGEENCIPHEREDUSINGVIGENERECANBEREVEALEDEASILY"));
		
		String w = "THE";
		for (int i=0; i<26; i++) {
			System.out.println(shift(w, new int[] {i,i,i}));
		}
	}
	
}

//BFWSSOT

