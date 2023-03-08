package com.zodiackillerciphers.annealing.pollux;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.lucene.NGramsCSRA;
import com.zodiackillerciphers.lucene.Stats;
import com.zodiackillerciphers.morse.Morse;

/** non-annealing, brute force solver for digits-only Pollux cipher */ 
public class PolluxBruteSolver {

	static List<ResultBean> beans = new ArrayList<ResultBean>(); 
	
	//public static String alphabet = "0123456789"; 
	public static void solve(String cipher, String alphabet, String[] morseUnits) {
		NGramsCSRA.OPTIONAL_PREFIX = "/Users/doranchak/projects/zodiac/zkdecrypto/zkdecrypto/language/";
		NGramsCSRA.init("EN");
		solve(cipher, alphabet, morseUnits, new int[alphabet.length()], 0);
		process(beans);
		for (ResultBean bean : beans) {
			print(bean);
		}
	}
	
	static void print(ResultBean bean) {
		if (bean.morseAlphabetSize < 20) return;
		if (bean.morseAlphabetSize > 32) return;
		if (bean.morseIocDiff > 0.2) return;
		System.out.println(bean);
	}
	public static void solve(String cipher, String alphabet, String[] morseUnits, int[] key, int index) {
		// reached the end
		if (index == key.length) {
			ResultBean bean = process(cipher, alphabet, morseUnits, key);
			//String morse = decode(cipher, alphabet, morseUnits, key);
			//System.out.println(toString(key) + "	" + toString(measure(morse)) + "	[" + morse + "]	" + toPlaintext(morse));
			//System.out.println(bean);
			//System.out.println(toString(key));
			beans.add(bean);
			return;
		}
		for (int i=0; i<morseUnits.length; i++) {
			key[index] = i;
			solve(cipher, alphabet, morseUnits, key, index+1);
		}
	}
	
	static ResultBean process(String cipher, String alphabet, String[] morseUnits, int[] key) {
		
		ResultBean bean = new ResultBean();
		bean.key = toString(key);
		bean.morse = decode(cipher, alphabet, morseUnits, key);
		bean.plaintext = toPlaintext(bean.morse);
		
		String[] split = bean.morse.split(" ");
		Map<String, Integer> morseAlphabet = new HashMap<String, Integer>(); // counts per morse unit (checking for custom morse alphabets)  
		// length
		bean.morseLength = bean.morse.length();
		// number of non-empty tokens
		bean.morseTokensCount = 0;
		for (String s : split)
			if (s.length() > 0) {
				bean.morseTokensCount++;
				Integer val = morseAlphabet.get(s);
				if (val == null) val = 0;
				val++;
				morseAlphabet.put(s, val);
			}
		bean.morseAlphabetSize = morseAlphabet.size();
		bean.morseAlphabetIoc = Stats.ioc(morseAlphabet.values());
		bean.morseIocDiff = Math.abs(bean.morseAlphabetIoc - Stats.ENGLISH_IOC); 
		
		// number of valid morse sequences (tokens)
		bean.morseTokensValid = 0;
		for (String s : split)
			if (s.length() > 0)
				if (Morse.morseDecodeTable.containsKey(s))
					bean.morseTokensValid++;
		// number of excess spaces (places where there are more than one space in a row)
		for (int i=1; i<bean.morse.length(); i++)
			if (bean.morse.charAt(i) == ' ' && bean.morse.charAt(i-1) == ' ') 
				bean.morseExcessSpaces++;
		// 4: average morse token length
		bean.morseAverageTokenLength = 0;
		for (String s : split)
			if (s.length() > 0)
				bean.morseAverageTokenLength+=s.length();
		if (bean.morseTokensCount > 0) bean.morseAverageTokenLength /= bean.morseTokensCount;
		bean.morseAverageTokenLengthDistance = Math.abs(ResultBean.averageCodeLength - bean.morseAverageTokenLength);
		// 5: plaintext score
		bean.plaintextScore = NGramsCSRA.zkscore(new StringBuffer(bean.plaintext), "EN", false);
		if (bean.plaintext.length() > 0) bean.ioc = Stats.iocAsFloat(bean.plaintext);
		if (Float.isNaN(bean.ioc)) bean.ioc = 0;
		bean.iocDiff = Math.abs(bean.ioc - Stats.ENGLISH_IOC); 
		return bean;
		
	}
	
	static void process(List<ResultBean> beans) {
		float min; 
		float max; 
		
		
		min = Float.MAX_VALUE;
		max = Float.MIN_VALUE;
		for (ResultBean bean : beans) {
			min = Math.min(min, bean.morseTokensValid);
			max = Math.max(max, bean.morseTokensValid);
		}
		for (ResultBean bean : beans) 
			bean.normMorseTokensValid = (bean.morseTokensValid - min)/(max-min);
		
		min = Float.MAX_VALUE;
		max = Float.MIN_VALUE;
		for (ResultBean bean : beans) {
			min = Math.min(min, bean.morseExcessSpaces);
			max = Math.max(max, bean.morseExcessSpaces);
		}
		for (ResultBean bean : beans) 
			bean.normMorseExcessSpaces = (bean.morseExcessSpaces - min)/(max-min);
		
		min = Float.MAX_VALUE;
		max = Float.MIN_VALUE;
		for (ResultBean bean : beans) {
			min = Math.min(min, bean.morseAverageTokenLengthDistance);
			max = Math.max(max, bean.morseAverageTokenLengthDistance);
			//System.out.println("SMEG " + bean.morseAverageTokenLengthDistance + " " + min + " " + max);
		}
		//System.out.println("SMEG " + min + " " + max);
		for (ResultBean bean : beans) 
			bean.normMorseAverageTokenLengthDistance = (bean.morseAverageTokenLengthDistance - min)/(max-min);
		
		min = Float.MAX_VALUE;
		max = Float.MIN_VALUE;
		for (ResultBean bean : beans) {
			min = Math.min(min, bean.iocDiff);
			max = Math.max(max, bean.iocDiff);
		}
		for (ResultBean bean : beans) 
			bean.normIocDiff = (bean.iocDiff - min)/(max-min);
		
		min = Float.MAX_VALUE;
		max = Float.MIN_VALUE;
		for (ResultBean bean : beans) {
			min = Math.min(min, bean.plaintextScore);
			max = Math.max(max, bean.plaintextScore);
		}
		for (ResultBean bean : beans) 
			bean.normPlaintextScore = (bean.plaintextScore - min)/(max-min);

		for (ResultBean bean : beans)
			bean.combinedScore = bean.normMorseTokensValid + (1 - bean.normMorseExcessSpaces)
					+ (1 - bean.normMorseAverageTokenLengthDistance) + bean.normPlaintextScore;

		Collections.sort(beans, new Comparator<ResultBean>() {

			@Override
			public int compare(ResultBean o1, ResultBean o2) {
				// TODO Auto-generated method stub
				return Float.compare(o2.combinedScore, o1.combinedScore);
			}
			
		});
	}
	
	public static String toString(int[] key) {
		StringBuffer sb = new StringBuffer();
		for (int val : key) sb.append(val);
		return sb.toString();
	}
	public static String toString(float[] vals) {
		StringBuffer sb = new StringBuffer();
		for (float val : vals) {
			if (sb.length() > 0) sb.append("	");
			sb.append(val);
		}
		return sb.toString();
	}
	
	public static String decode(String cipher, String alphabet, String[] morseUnits, int[] key) {
		
		// map alphabet to morse units
		Map<String, String> map = new HashMap<String, String>();
		for (int i=0; i<alphabet.length(); i++)
			map.put(alphabet.substring(i,i+1), morseUnits[key[i]]);
		
		StringBuffer sb = new StringBuffer();
		for (int i=0; i<cipher.length(); i++)
			sb.append(map.get(cipher.subSequence(i, i+1)));
		return sb.toString();
		
	}
	
	public static String toPlaintext(String morse) {
		String[] split = morse.split(" ");
		StringBuffer sb = new StringBuffer();
		for (String s : split)
			if (s.length() > 0) {
				Character val = Morse.morseDecodeTable.get(s);
				if (val != null) sb.append(val);
				//else sb.append("_");
			}
		return sb.toString();
	}
	
//	public static float[] measure(String morse) {
//		float[] results = new float[6];
//		String[] split = morse.split(" ");
//		// 0: length
//		results[0] = morse.length();
//		// 1: number of non-empty tokens
//		for (String s : split)
//			if (s.length() > 0)
//				results[1]++;
//		// 2: number of valid morse sequences (tokens)
//		for (String s : split)
//			if (s.length() > 0)
//				if (Morse.morseDecodeTable.containsKey(s))
//					results[2]++;
//		// 3: number of excess spaces (places where there are more than one space in a row)
//		for (int i=1; i<morse.length(); i++)
//			if (morse.charAt(i) == ' ' && morse.charAt(i-1) == ' ') 
//				results[3]++;
//		// 4: average morse token length
//		for (String s : split)
//			if (s.length() > 0)
//				results[4]+=s.length();
//		results[4] /= results[1];
//		// 5: plaintext score
//		String pt = toPlaintext(morse);
//		results[5] = NGramsCSRA.zkscore(new StringBuffer(pt), "EN", false);
//		return results;
//		
//	}
	
	public static void main(String[] args) {
//		solve(PolluxSolution.ciphertextCrimo1381, "0123456789", new String[] {"0","1"," "});
		solve(PolluxSolution.ciphertextCrimo, "0123456789", new String[] {"0","1"," "});
//		solve(PolluxSolution.ciphertextZ408, "0123456789", new String[] {"0","1"," "});
//		solve(PolluxSolution.ciphertextToebes, "0123456789", new String[] {"0","1"," "});
	}
}
