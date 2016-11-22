package com.zodiackillerciphers.ngrams;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.ciphers.generator.TrigramUtils;
import com.zodiackillerciphers.tests.jarlve.JarlveMeasurements;
import com.zodiackillerciphers.transform.CipherTransformations;

public class NGrams {

	public static Map<String, Float> Z340Result;
	
	static boolean DEBUG = false;
	static String wild = "??????"; // quick way to avoid looping in ngrams(int n, String text, int i, boolean gaps) 
	/** return list of ngrams for the given text at the given position.
	 * if gaps is true, include gapped variations
	 */
	static {
		Z340Result = toMap(measure2(Ciphers.cipher[0].cipher, false));
	}
	public static List<String> ngrams(int n, String text, int i, boolean gaps) {
		List<String> list = new ArrayList<String>();
		if (i>=text.length()-n+1) return list;
		//System.out.println("got here");
		
		if (gaps) {
			StringBuffer ngram;
			if (n==2) { // A?B, A??B, A???B
				int pos1; int pos2; // locations of A and B
				for (int a=0; a<4; a++) {
					ngram = new StringBuffer();
					pos1 = i;
					pos2 = i+a+1;
					if (pos2 >= text.length()) break;
					ngram.append(text.charAt(pos1)).append(wild.substring(0, a)).append(text.charAt(pos2));
					if (!TrigramUtils.ignore(ngram.toString())) list.add(ngram.toString());
				}
			} else if (n==3) { // AB?C, AB??C, AB???C etc
				int pos1; int pos2; int pos3; // locations of A, B, and C
				for (int a=0; a<4; a++) {
					for (int b=0; b<4; b++) {
						ngram = new StringBuffer();
						pos1 = i;
						pos2 = i+a+1;
						pos3 = pos2+b+1;
						if (pos3 >= text.length()) break;
						ngram.append(text.charAt(pos1)).append(wild.substring(0, a)).append(text.charAt(pos2)).append(wild.substring(0, b)).append(text.charAt(pos3));
						if (!TrigramUtils.ignore(ngram.toString())) list.add(ngram.toString());
					}
				}
				
			} else if (n==4) { // ABC?D, A?B?C?D, etc
				int pos1; int pos2; int pos3; int pos4; // locations of A, B, C, and D
				for (int a=0; a<4; a++) {
					for (int b=0; b<4; b++) {
						for (int c=0; c<4; c++) {
							ngram = new StringBuffer();
							pos1 = i;
							pos2 = i+a+1;
							pos3 = pos2+b+1;
							pos4 = pos3+c+1;
							if (pos4 >= text.length()) break;
							ngram.append(text.charAt(pos1)).append(wild.substring(0, a)).append(text.charAt(pos2)).append(wild.substring(0, b)).append(text.charAt(pos3)).append(wild.substring(0, c)).append(text.charAt(pos4));
							//System.out.println(ngram);
							if (!TrigramUtils.ignore(ngram.toString())) list.add(ngram.toString());
						}
					}
				}
				
			} else {
				String ng = text.substring(i,i+n);
				if (!TrigramUtils.ignore(ng)) list.add(ng); // standard ngram
			}
		} 
		return list;
		
	}
	public static Map<String, Integer> countNgrams(String text, int n) {
		return countNgrams(text, n, new int[2]);
	}
	public static Map<String, Integer> countNgrams(String text, int n, int[] totals) {
		return countNgrams(text, n, totals, false);
	}
	public static Map<String, Integer> countNgrams(String text, int n, int[] totals, boolean gaps) {
		totals[0] = 0; totals[1] = 0; totals[2] = 0; Set<String> repeats = new HashSet<String>();
		Map<String, Integer> map = new HashMap<String, Integer>();
		
		Set<Character> uniqueLetters = new HashSet<Character>();
		for (int i=0; i<text.length()-n+1; i++) {
			List<String> ngrams = ngrams(n, text, i, gaps);
			for (String ngram : ngrams) {
				Integer count = map.get(ngram);
				if (count == null) count = 1;
				else count++;
				map.put(ngram, count);
				
				if (count > 1) {
					repeats.add(ngram);
					for (int j=0; j<ngram.length(); j++) uniqueLetters.add(ngram.charAt(j));
					if (count == 2) totals[0] += 2; 
					else totals[0]++;
					//System.out.println(ngram + ":" + map.get(ngram));
				}
				
			}
		}
		totals[1] = repeats.size();
		totals[2] = uniqueLetters.size();
		return map;
	}
	
	public static void testSolved() {
		int[] totals = new int[3];
		
		for (int n=2; n<6; n++) { 
			Map<String, Integer> map = countNgrams("ilikekillingpeoplebecauseitissomuchfunitismorefunthankillingwildgameintheforrestbecausemanisthemostdangertueanamalofalltokillsomethinggivesmethemostthrillingexperenceitisevenbetterthangettingyourrocksoffwithagirlthebestpartofitisthaewheninparadicesndalltheihavekilledwillbecomemyslavesiwillnotgiveyoumynamebecauseyouwilltrytosloidownorstopmycollectingofslavesfoemyafterlifeebeorietemethhpit", n, totals);
			for (String key : map.keySet()) System.out.println("key " + key + " val " + map.get(key));
			System.out.println(n+","+totals[0] + ","+ totals[1] + "," + totals[2]);
		}
	}
	
	public static void testNgrams() {
		String text = "abcdefghijklmnop";
		for (int n=2; n<5; n++) {
			System.out.println("n " + n);
			List<String> ngrams = ngrams(n, text, 0, true);
			for (String ngram : ngrams) System.out.println(" - " + ngram);
		}
	}

	public static void testBeale() {
		String text = Ciphers.cipher[33].cipher;
		Map<String, Integer> map = new HashMap<String, Integer>();
		for (int i=0; i<text.length()-4; i++) { 
			List<String> ngrams = ngrams(4, text, i, true);
			for (String ngram : ngrams) {
				Integer val = map.get(ngram);
				if (val == null) val = 0;
				val++;
				map.put(ngram, val);
				//if (ngram.startsWith("G")) System.out.println(ngram);
				
			}
		}
		for (String key : map.keySet()) if (map.get(key) > 1) System.out.println(map.get(key) + "	" + key + "	" + text.indexOf(key));
	}
	
	public static void testDora() {
		String text = Ciphers.cipher[30].cipher;
		Map<String, Integer> map = new HashMap<String, Integer>();
		for (int i=0; i<text.length()-4; i++) { 
			List<String> ngrams = ngrams(4, text, i, true);
			for (String ngram : ngrams) {
				Integer val = map.get(ngram);
				if (val == null) val = 0;
				val++;
				map.put(ngram, val);
				//if (ngram.startsWith("G")) System.out.println(ngram);
				
			}
		}
		for (String key : map.keySet()) if (map.get(key) > 1) System.out.println(map.get(key) + "	" + key + "	" + text.indexOf(key));
	}

	
	public static void testCipher(String text, int n) {
		Map<String, Integer> map = new HashMap<String, Integer>();
		for (int i=0; i<text.length()-n; i++) { 
			List<String> ngrams = ngrams(n, text, i, true);
			for (String ngram : ngrams) {
				Integer val = map.get(ngram);
				if (val == null) val = 0;
				val++;
				map.put(ngram, val);
				//if (ngram.startsWith("G")) System.out.println(ngram);
				
			}
		}
		for (String key : map.keySet()) if (map.get(key) > 1) System.out.println(map.get(key) + "	" + key + "	" + text.indexOf(key));
	}
	public static float[] measure(String ciphertext) {
		return measure(ciphertext, Ciphers.alphabet(ciphertext).length()); // ignore whitespace during ngram search
	}
	/** measure the internal structure of the given cipher text by counting n-gram patterns, including those with gaps, from 
	 * n=2 to n=4. 
	 * 
	 * @param ciphertext cipher text to measure
	 * @param alpha number of symbols in the cipher alphabet
	 * @return score array (see below) 
	 */
	public static float[] measure(String ciphertext, int alpha) {
		//Map<Character, Integer> counts = Ciphers.countMap(ciphertext);
		int[] totals = new int[3];
		
		/* scores array.  elements:
		 * 
		 * # of 2-grams (no gaps)
		 * # of 3-grams (no gaps)
		 * # of 4-grams (no gaps)
		 * combined score (no gaps) - weighted sum of n-gram unique repetition counts.  the formula is: (n2 / 4) + (n3 / 2) + n4
		 * # of 2-grams (gaps)
		 * # of 3-grams (gaps)
		 * # of 4-grams (gaps)
		 * combined score (gaps) - weighted sum of n-gram unique repetition counts.  the formula is: (n2 / 4) + (n3 / 2) + n4
		 * combined score (all) - sum of the two combined scores
		 * combined score (all) divided by cipher length
		 * 
		 */
		float[] scores = new float[10]; 
		
		for (int n=2; n<5; n++) { 
			Map<String, Integer> map = countNgrams(ciphertext, n, totals, true);
			//System.out.println(n+","+totals[0] + ","+ totals[1] + "," + totals[2]);
			
			for (String ngram : map.keySet()) {
				if (map.get(ngram) < 2) continue;
				if (ngram.contains("?")) { // gaps
					//System.out.println("counting " + ngram + " with gaps");
					scores[n+2]++;
				} else { 
					//System.out.println("counting " + ngram + " without gaps");
					scores[n-2]++;
				}

			}
			if (DEBUG) {
			String line = "  - ";
			for (String ngram : map.keySet()) if (map.get(ngram) > 1) line += ngram + ": " + map.get(ngram) + " ";
			System.out.println(line);}
		}
		
		for (int i=0; i<3; i++) scores[3] += scores[i] / Math.pow(2, 2-i); // combined score (no gaps)
		for (int i=0; i<3; i++) scores[7] += scores[i+4] / Math.pow(2, 2-i); // combined score (gaps) 
		scores[8] = scores[3] + scores[7];
		//scores[9] = scores[8] / alpha;
		scores[9] = scores[8] / ciphertext.length();
		//System.out.println("Uniques score: " + score);
		return scores;
	}
	
	public static Map<String, Float> toMap(List<NGramResultBean> beans) {
		if (beans == null) return null;
		Map<String, Float> map = new HashMap<String, Float>();
		for (NGramResultBean bean : beans) {
			if (map.containsKey(bean.sequence)) {
				System.err.println(bean.sequence + " is already in the map.  why?");
				System.exit(-1);
			}
			map.put(bean.sequence, bean.probability);
		}
		return map;
	}
	
	/** generate a list of repeating patterns, and rank them based on probability estimates */
	public static List<NGramResultBean> measure2(String ciphertext, boolean sort) {
		int[] totals = new int[3];
		List<NGramResultBean> probs = new ArrayList<NGramResultBean>(); 
		Map<Character, Integer> counts = Ciphers.countMap(ciphertext);
		for (int n=2; n<5; n++) { 
			Map<String, Integer> map = countNgrams(ciphertext, n, totals, true);
			for (String ngram : map.keySet()) {
				String key = "";
				int val = map.get(ngram);
				if (val < 2) continue;
				for (int i=0; i<val; i++) {
					key += ngram + " ";
				}
				float prob = 1;
				for (int i=0; i<ngram.length();i++) {
					char ch = ngram.charAt(i);
					if (ch == '?') continue;
					if (ch == ' ') continue;
					prob *= ((float)counts.get(ch)/ciphertext.length());
				}
				prob = (float) Math.pow(prob, map.get(ngram));
				probs.add(new NGramResultBean(key, prob));
			}
		}
		if (sort) {
			Collections.sort(probs, new Comparator<NGramResultBean>() {
				@Override
				public int compare(NGramResultBean o1, NGramResultBean o2) {
					return o1.getProbability().compareTo(o2.getProbability());
				}
				
			});
		}
		
		if (DEBUG) {
			float sum = 0;
			for (NGramResultBean bean : probs) {
				System.out.println(bean);
				sum += bean.probability;
			}
			System.out.println("SUM " + sum + " MEAN " + (sum/probs.size()));
		}
		return probs;
	}
	
	public static void testZ() {
		//String cipher = Ciphers.cipher[0].cipher;
		DEBUG = true;
		String cipher = Ciphers.cipher[0].cipher;
		//String cipher= "c*l)OATOB5FBcDp.#/2Z^YNp2Ez<8fUR(O^TO|5FBcNl2Z/(d5+|<(@MU^bY2GZ><p27W9KR*l)4&SB+GB9C+>tc<pFOLPS(54FXcJ++Bfc+:P+RlklE1C+HV+G;U+GV4:)T+NlFp83MFLYM(pA16+_+R-zz-S*LdWK/4_+DM-G_b;Bj.9>BcDMFXFO^TO|5FKypkfBLk%#jd|^4Wy#Fq|H)KRpUNYl2VzRVf6*-O^LOt5.V5#k>B+GtETSJ1O9)p(+K8t+6zzRpyd4C;qkKb8Zz-*BJHC+dK<M2T.z6DW|+|7yp2zW7.Pc<M2#F&*|JRM(LHC+Vc.byUNW|+4Bc";
		System.out.println("Z340: " + JarlveMeasurements.nonrepeat(cipher));
		for (int i=0; i<100; i++) {
			cipher = CipherTransformations.shuffle(cipher);
			//String cipher= "c*l)OATOB5FBcDp.#/2Z^YNp2Ez<8fUR(O^TO|5FBcNl2Z/(d5+|<(@MU^bY2GZ><p27W9KR*l)4&SB+GB9C+>tc<pFOLPS(54FXcJ++Bfc+:P+RlklE1C+HV+G;U+GV4:)T+NlFp83MFLYM(pA16+_+R-zz-S*LdWK/4_+DM-G_b;Bj.9>BcDMFXFO^TO|5FKypkfBLk%#jd|^4Wy#Fq|H)KRpUNYl2VzRVf6*-O^LOt5.V5#k>B+GtETSJ1O9)p(+K8t+6zzRpyd4C;qkKb8Zz-*BJHC+dK<M2T.z6DW|+|7yp2zW7.Pc<M2#F&*|JRM(LHC+Vc.byUNW|+4Bc";
			//measure2(cipher, true);
			System.out.println(JarlveMeasurements.nonrepeat(cipher));
				
			//System.out.println(measureRepeatingFragmentDistance(cipher));
		}
	}
	
	public static void dump(float[] scores) {
		String line = "";
		for (int i=0; i<scores.length; i++) line += scores[i] + "	";
		System.out.println(line);
	}

	/** measure similarity to Z340's repeating fragments (including ngrams) */
	public static float measureRepeatingFragmentDistance(String cipher) {
		float result = 0;
		Map<String, Float> map = toMap(measure2(cipher, false));
		
		for (String key : map.keySet()) {
			Float val1 = map.get(key);
			if (val1 != 0) val1 = 1/val1; // exaggerate the distance when probabilities are low
			Float val2 = Z340Result.get(key);
			if (val2 == null) val2 = 0f;
			if (val2 != 0) val2 = 1/val2; // exaggerate the distance when probabilities are low
			result += (val1-val2)*(val1-val2);
		}
		
		// then, include any from Z340 that aren't in this cipher
		for (String key : Z340Result.keySet()) {
			if (map.containsKey(key)) continue; // already looked at this one
			
			// now we have a repeated pattern in Z340 that isn't in this cipher 
			
			Float val = Z340Result.get(key);
			if (val != 0) val = 1/val; // exaggerate the distance when probabilities are low
			result += val*val;
		}
		
		result = (float)Math.sqrt(result);
		return result;
	}
	public static void main(String[] args) {
		//testSolved();
		//dump(measure(Ciphers.cipher[0].cipher));
		//dump(measure(Ciphers.cipher[1].cipher));
		//testZ();
		testCipher(Ciphers.cipher[0].cipher, 2);
		
		/*
		System.out.println("zodiac");
		testCipher(Ciphers.cipherByDescription("Z340: Zodiac killer's unsolved 340 cipher").cipher, 4);
		System.out.println("pi");
		testCipher(Ciphers.cipherByDescription("Test cipher by pi").cipher, 4);
		*/
		
		
		//testBeale();
		//testDora();
		//testSolved();
	}
}
