package com.zodiackillerciphers.ngrams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.io.FileUtil;
import com.zodiackillerciphers.stats.StatsWrapper;
import com.zodiackillerciphers.transform.CipherTransformations;

/** an attempt to find repeating fragments in a faster way */
public class RepeatingFragmentsFaster {

	static Map<Integer, String[]> wildcards;
	static {
		wildcards = new HashMap<Integer, String[]>();
		wildcards.put(4, new String[] { "A?CD", "AB?D" });
		wildcards.put(5, new String[] { "A?CDE", "AB?DE", "ABC?E", "A?C?E" });
		wildcards.put(6, new String[] { "A?CDEF", "AB?DEF", "ABC?EF", "ABCD?F",
				"A??DEF", "A?C?EF", "A?CD?F", "AB??EF", "AB?D?F", "ABC??F",
				"A???EF", "A??D?F", "A?C??F", "AB???F" });
	}

	/** stats on patterns found during random shuffles of z340/z408 */ 
	public static double z340mean = 1.256e-06;
	public static double z340median = 2.694e-07;
	public static double z340sigma = 3.155214e-06;

	public static double z408mean = 3.009e-07;
	public static double z408median = 2.140e-07;
	public static double z408sigma = 3.164215e-07;
	
	public static double z340iocMean = 9.221e-07;
	public static double z340iocStdDev = 1.812715e-07;
	public static double z408iocMean = 8.043e-07;
	public static double z408iocStdDev = 1.230456e-07;
	
	public static Map<Integer, RepeatingFragmentsFasterBean> reference;
	public static float[] referenceProbs;
	static {
		reference = score(Ciphers.cipher[0].cipher, false, false);
		//reference = null;
		referenceProbs = probs(reference);
		//referenceProbs = null;
	}
	public static Map<Integer, RepeatingFragmentsFasterBean> referenceExcludeNormalBigrams;
	public static float[] referenceProbsExcludeNormalBigrams;
	static {
		referenceExcludeNormalBigrams = score(Ciphers.cipher[0].cipher, true, false);
		referenceProbsExcludeNormalBigrams = probs(referenceExcludeNormalBigrams);
	}
	
	static String cipherName;

	/** 
	/** measure how much improvement the given prob-per-pos map has over the reference map */
	public static float improvement(Map<Integer, RepeatingFragmentsFasterBean> map, boolean excludeNormalBigrams, boolean showSteps) {
		float score = 0;
		float[] probs = probs(map);
		
		Map<Integer, RepeatingFragmentsFasterBean> refMap = excludeNormalBigrams ? referenceExcludeNormalBigrams : reference; 
		float[] refProbs = excludeNormalBigrams ? referenceProbsExcludeNormalBigrams : referenceProbs;
		if (showSteps) {
			System.out.println("reference: " + Arrays.toString(refProbs));
			System.out.println("    given: " + Arrays.toString(probs));
		}
			
		for (int i=0; i<340; i++) {
			RepeatingFragmentsFasterBean bean1 = map.get(i);
			RepeatingFragmentsFasterBean bean2 = refMap.get(i);
			
			
			
			float diff = 0;
			float p1 = Math.max(probs[i], 1);
			float p2 = Math.max(refProbs[i], 1);
			if (p1 == 0 && p2 == 0) {
				// nothing
			} else if (p1 > 0 && p2 == 0) {
				// perfect improvement, 
			}
			
			diff = probs[i]-refProbs[i];
			float squashed = 1 - (1/(1 + Math.abs(diff)/100000000)); 
			if (diff > 0) score += squashed;
			else score -= squashed;
			//if (score > 400) System.out.println("fuck " + diff + " " + score + " " + squashed);
			
			if (showSteps) {
				if (diff > 0)
					System.out.println(i + " " + diff + " " + squashed);
			}
			
		}
		return score;
	}
	
	/** return sorted inverse probability array */
	public static float[] probs(Map<Integer, RepeatingFragmentsFasterBean> map) {
		//if (1==1) return null;
		float[] probs = new float[340];
		for (int i=0; i<probs.length; i++) {
			RepeatingFragmentsFasterBean bean = map.get(i);
			if (bean != null) probs[i] = ((float)1)/bean.probability();
		}
		Arrays.sort(probs);
		return probs;
	}
	
	
	public static Map<Integer, RepeatingFragmentsFasterBean> score(String cipher, boolean excludeNormalBigrams, boolean showSteps) {
		return score(cipher, excludeNormalBigrams, showSteps, false);
		//return null;
	}
	/** generate "best probability per position" map of repeating ngrams/fragments */
	public static Map<Integer, RepeatingFragmentsFasterBean> score(String cipher, boolean excludeNormalBigrams, boolean showSteps, boolean showHtml) {
		Map<String, Integer> counts = new HashMap<String, Integer>();
		Map<Character, Integer> symbolCounts = Ciphers.countMap(cipher);
		Map<String, Set<Integer>> positions = new HashMap<String, Set<Integer>>();
		int MAX = 6;
		
 		for (int n = 2; n <= MAX; n++) {
			for (int i = 0; i < cipher.length()-n+1; i++) {
				// ngram as-is
				String ngram = cipher.substring(i,i+n);  
				if (ngram.contains(" ")) continue; // skip spaces
				if (excludeNormalBigrams && n == 2) {
					
				} else {
					count(counts, ngram);
					track(positions, ngram, i);
				}
				// with wildcards
				if (n>3) {
					for (String pattern : wildcards.get(n)) {
						String ngramWithWildcards = "";
						for (int j=0; j<pattern.length(); j++) {
							if (pattern.charAt(j) == '?')
								ngramWithWildcards += '?';
							else
								ngramWithWildcards += ngram.charAt(j);
								
						}
						count(counts, ngramWithWildcards);
						track(positions, ngramWithWildcards, i);
					}
				}
			}
		}

 		// avoid overcounting substrings
 		// for each cipher position, track best pattern that occurs there.
 		
 		/** ngrams that repeat */  
 		//Set<String> reps = new HashSet<String>();
 		Map<Integer, RepeatingFragmentsFasterBean> bestBeansByPosition = new HashMap<Integer, RepeatingFragmentsFasterBean>(); 
 		Map<String, RepeatingFragmentsFasterBean> beans = new HashMap<String, RepeatingFragmentsFasterBean>(); 
 		for (String key : counts.keySet()) {
 			Integer val = counts.get(key);
 			if (val < 2) continue;
 			//reps.add(key);
 			RepeatingFragmentsFasterBean bean = new RepeatingFragmentsFasterBean();
 			bean.ngram = key;
 			bean.cipherLength = cipher.length();
 			bean.positions = positions.get(key);
 			bean.symbolCounts = symbolCounts;
 			updateScores(bestBeansByPosition, bean);
 			
 			beans.put(key, bean);
 			//System.out.println("made bean " + bean + " for key [" + key + "]");
 			
 			
 			//float factor = MAX-key.length()+1;
 			//factor = (float) Math.pow(factor, weight);
 			//System.out.println(key.length() + " " + key + " " + val + " " + factor + " " + ((val-1)/factor));
 			//updateScores(bestScoreByPosition, positions, key, (val-1)/factor);
 		}
 		if (showHtml) {
 			String html = "new Cipher(\"\", \"" + cipher + "\", [";
 			List<RepeatingFragmentsFasterBean> list = new ArrayList<RepeatingFragmentsFasterBean>(beans.values());
 			Collections.sort(list);
 			Set<RepeatingFragmentsFasterBean> set = new HashSet<RepeatingFragmentsFasterBean>();
 			boolean first = true;
 			for (RepeatingFragmentsFasterBean bean : list) {
 				if (set.contains(bean)) continue;
 				set.add(bean);
 				if (!first) html += ", ";
 				html += bean.js();
 				first = false;
 			}
 			html += "], 0, 0)";
 			System.out.println(html);
 		}
 		
 		return bestBeansByPosition;
 		
	}

	/** locate all repeating fragments and then compute their index of coincidence */
	/*public static double fragmentIOC_OLD_VERSION(String cipher) {
		Map<String, Integer> counts = new HashMap<String, Integer>();
		Map<Character, Integer> symbolCounts = Ciphers.countMap(cipher);
		Map<String, Set<Integer>> positions = new HashMap<String, Set<Integer>>();
		int MAX = 6;
		
		// this is the total used by calculation of fragment IOC
		long N = 0;
 		for (int n = 2; n <= MAX; n++) {
			for (int i = 0; i < cipher.length()-n+1; i++) {
				// ngram as-is
				String ngram = cipher.substring(i,i+n);  
				if (ngram.contains(" ")) continue; // skip spaces
				N++;
				count(counts, ngram);
				track(positions, ngram, i);
				// with wildcards
				if (n>3) {
					for (String pattern : wildcards.get(n)) {
						N++; 
						String ngramWithWildcards = "";
						for (int j=0; j<pattern.length(); j++) {
							if (pattern.charAt(j) == '?')
								ngramWithWildcards += '?';
							else
								ngramWithWildcards += ngram.charAt(j);
								
						}
						count(counts, ngramWithWildcards);
						track(positions, ngramWithWildcards, i);
					}
				}
			}
		}

 		Map<String, RepeatingFragmentsFasterBean> beans = new HashMap<String, RepeatingFragmentsFasterBean>(); 
 		for (String key : counts.keySet()) {
 			Integer val = counts.get(key);
 			if (val < 2) continue;
 			//reps.add(key);
 			RepeatingFragmentsFasterBean bean = new RepeatingFragmentsFasterBean();
 			bean.ngram = key;
 			bean.cipherLength = cipher.length();
 			bean.positions = positions.get(key);
 			bean.symbolCounts = symbolCounts;
 			beans.put(key, bean);
 		}
 		
 		List<RepeatingFragmentsFasterBean> list = new ArrayList<RepeatingFragmentsFasterBean>(beans.values()); 
 		List<RepeatingFragmentsFasterBean> nondominated = new ArrayList<RepeatingFragmentsFasterBean>(); 
 		for (int i=0; i<list.size(); i++) {
			RepeatingFragmentsFasterBean bean1 = list.get(i);
 			boolean dominated = false;
 			for (int j=0; j<list.size(); j++) {
 				if (i==j) continue;
 				RepeatingFragmentsFasterBean bean2 = list.get(j);
 				if (bean2.dominates(bean1)) {
 					//System.out.println(bean2 + " dominates " + bean1);
 					dominated = true;
 					break;
 				}
 			}
 			if (!dominated) nondominated.add(bean1);
 		}
 		
 		
 		double sum = 0;
		for (RepeatingFragmentsFasterBean bean : nondominated) {
			sum += (bean.count() * (bean.count()-1));
		}
		double ioc = sum/(N*(N-1));
 		return ioc;
	}*/

	/**
	 * locate all repeating fragments and then compute their index of
	 * coincidence new version with more wildcard support
	 * 
	 * pass an empty beanslist if you want the method to return all the beans  
	 * 
	 * @param cipher
	 *            the cipher
	 * @param MAX
	 *            max n-gram length
	 * @return
	 **/
	public static double fragmentIOC(String cipher, int MAX, boolean showHtml, List<RepeatingFragmentsFasterBean> beansList) {
		return fragmentIOC(cipher, MAX, showHtml, false, beansList);
	}
	public static double fragmentIOC(String cipher, int MAX, boolean showHtml) {
		return fragmentIOC(cipher, MAX, showHtml, false, new ArrayList<RepeatingFragmentsFasterBean>());
	}
	public static double fragmentIOC(String cipher, int MAX, boolean showHtml, boolean showSteps) {
		return fragmentIOC(cipher, MAX, showHtml, showSteps, new ArrayList<RepeatingFragmentsFasterBean>());
	}
	/** pass an empty beanslist if you want the method to return all the beans */ 
	public static double fragmentIOC(String cipher, int MAX, boolean showHtml, boolean showSteps, List<RepeatingFragmentsFasterBean> beansList) {
		//Map<String, Integer> counts = new HashMap<String, Integer>();
		Map<Character, Integer> symbolCounts = Ciphers.countMap(cipher);
		Map<String, Positions> positions = new HashMap<String, Positions>();
		// this is the total used by calculation of fragment IOC
		long N = 0;
 		for (int n = 2; n <= MAX; n++) {
			for (int i = 0; i < cipher.length()-n+1; i++) {
				// ngram as-is
				String ngram = cipher.substring(i,i+n);  
				if (ngram.contains(" ")) continue; // skip spaces
				N++;
				//count(counts, ngram);
				Positions pos = positions.get(ngram);
				if (pos == null) {
					pos = new Positions(ngram);
					positions.put(ngram, pos);
				}
				pos.add(i);
				if (showSteps) System.out.println(pos);
				// with wildcards
				if (n>3) {
					Set<String> wildcards = new HashSet<String>();
					wildcards(wildcards, new StringBuffer(ngram), 0);
					if (showSteps) System.out.println("wildcards " + wildcards);
					for (String wildcard : wildcards) {
						//System.out.println("wild " + wildcard);
						//if (!wildcard.contains("?")) continue; // we already dealt with non-wildcard ngrams
						N++; 
						String ngramWithWildcards = "";
						for (int j=0; j<wildcard.length(); j++) {
							if (wildcard.charAt(j) == '?')
								ngramWithWildcards += '?';
							else
								ngramWithWildcards += ngram.charAt(j);
								
						}
						
						pos = positions.get(ngramWithWildcards);
						if (pos == null) {
							pos = new Positions(ngramWithWildcards);
							positions.put(ngramWithWildcards, pos);
						}
						pos.add(i);
						if (showSteps) System.out.println(pos);
						
					}

					/*
					for (String pattern : wildcards.get(n)) {
						N++; 
						String ngramWithWildcards = "";
						for (int j=0; j<pattern.length(); j++) {
							if (pattern.charAt(j) == '?')
								ngramWithWildcards += '?';
							else
								ngramWithWildcards += ngram.charAt(j);
								
						}
						count(counts, ngramWithWildcards);
						track(positions, ngramWithWildcards, i);
					}*/
				}
			}
		}
 		
 		
 		Map<String, RepeatingFragmentsFasterBean> beans = new HashMap<String, RepeatingFragmentsFasterBean>(); 
 		for (String key : positions.keySet()) {
 			Integer val = positions.get(key).count();
 			if (val < 2) continue;
 			
 			//System.out.println("Positions " + positions.get(key));
 			//reps.add(key);
 			RepeatingFragmentsFasterBean bean = new RepeatingFragmentsFasterBean();
 			bean.ngram = key;
 			bean.cipherLength = cipher.length();
 			bean.positions = positions.get(key).startPositions;
 			bean.symbolCounts = symbolCounts;
 			beans.put(key, bean);
 			if (showSteps) System.out.println("made bean " + bean);
 		}
 		
 		/*List<RepeatingFragmentsFasterBean> list = new ArrayList<RepeatingFragmentsFasterBean>(beans.values()); 
 		List<RepeatingFragmentsFasterBean> nondominated = new ArrayList<RepeatingFragmentsFasterBean>(); 
 		for (int i=0; i<list.size(); i++) {
			RepeatingFragmentsFasterBean bean1 = list.get(i);
 			boolean dominated = false;
 			for (int j=0; j<list.size(); j++) {
 				if (i==j) continue;
 				RepeatingFragmentsFasterBean bean2 = list.get(j);
 				if (bean2.dominates(bean1)) {
 					//System.out.println(bean2 + " dominates " + bean1);
 					dominated = true;
 					break;
 				}
 			}
 			if (!dominated) nondominated.add(bean1);
 		}*/
 		
 		beansList.addAll(beans.values());
 		String html = null;
 		if (showHtml) {
 			html = "new Cipher(\"" + cipherName + "\", \"" + cipher.replace("\\", "\\\\") + "\", [";
 			Collections.sort(beansList);
 			boolean first = true;
 			for (RepeatingFragmentsFasterBean bean : beansList) {
 				if (!first) html += ", ";
 				html += bean.js();
 				first = false;
 			}
 		}

 		double sum = 0; double contribution = 0;
 		
 		Set<Integer> seen = new HashSet<Integer>();
 		
		for (RepeatingFragmentsFasterBean bean : beansList) {
			//if (bean.probability() > cutoff) continue;
			contribution = bean.count() * (bean.count()-1);
			
			// garnish the contribution based on how many positions we've already seen
			// TODO: this is sensitive to sort order of RepeatingFragmentsFasterBean list.
			// so maybe a better position sharing scheme is needed 
			Positions p = positions.get(bean.ngram);
			contribution *= p.unseenRatio(seen);
			sum += contribution;
			if (showSteps) System.out.println("unseen " + p.unseenRatio(seen) + " bean " + bean + " positions " + p);
			seen.addAll(p.coveredPositions);
			if (showSteps) bean.dumpPositions();
			//System.out.println("bean " + bean);
			
			
		}
		double ioc = sum/(N*(N-1));

		if (showHtml) {
			html += "], 0, 0, " + ioc + "),";
			System.out.println(html);
		}
		
		
 		return ioc;
	}
	
	/** generate all possible wildcard variations for the given ngram
	 * TODO: would it be more efficient to simply generate all binary values in a range? 
	 */
	public static void wildcards(Set<String> wildcards, StringBuffer ngram, int pos) {
		if (pos == ngram.length()-1) return;
		if (pos == 0) {
			wildcards(wildcards, ngram, 1);
			return;
		}

		// two possibilities with current character:  
		//   1) keep it
		if (ngram.indexOf("?") > -1)
			wildcards.add(ngram.toString());
		wildcards(wildcards, ngram, pos+1);
		//   2) replace it with wildcard 
		StringBuffer ngramCopy = new StringBuffer(ngram);
		ngramCopy.setCharAt(pos, '?');
		wildcards.add(ngramCopy.toString());
		wildcards(wildcards, ngramCopy, pos+1);
	}		

	
	static void track(Map<String, Set<Integer>> positions, String ngram, int position) {
		Set<Integer> val = positions.get(ngram);
		if (val == null) val = new HashSet<Integer>();
		else {
			// ignore this ngram if any of its non wildcard positions fall on another ngram's non wildcard position
			for (Integer existingPosition : val) {
				for (int i=0; i<ngram.length(); i++) {
					
					
					
					if (ngram.charAt(i) == '?') continue;
					if (position + i == existingPosition + i) {
						System.out.println("ngram " + ngram + " position " + position + " existing " + existingPosition);
						return;
					}
				}
			}
		}
		val.add(position);
		positions.put(ngram, val);
	}
	static void updateScores(Map<Integer, RepeatingFragmentsFasterBean> bestBeansByPosition, RepeatingFragmentsFasterBean bean) {
		for (int pos : bean.positions) {
			for (int i=pos; i<pos+bean.ngram.length(); i++) {
				if (bean.ngram.charAt(i-pos) == '?') continue; // don't overcount positions covered by wildcards
				RepeatingFragmentsFasterBean existingBean = bestBeansByPosition.get(i);
				if (existingBean == null) existingBean = bean;
				else {
					if (bean.probability() > 0 && bean.probability() < existingBean.probability()) {
						existingBean = bean;
					}
				}
				bestBeansByPosition.put(i, existingBean);
			}
		}
	}
	
	static void count(Map<String, Integer> counts, String ngram) {
		Integer val = counts.get(ngram);
		if (val == null) val = 0;
		val ++;
		counts.put(ngram, val);
	}

	static void updateScoresOLD(Map<Integer, Float> bestScoreByPosition,
			Map<String, Set<Integer>> positions, String ngram, float incomingScore) {
		Set<Integer> pos = positions.get(ngram);
		
		int n = ngram.length();
		for (Integer p : pos) {
			for (int i=p; i<p+n; i++) {
				Float score = bestScoreByPosition.get(i);
				if (score == null) score = 0f;
				score = Math.max(score, incomingScore);
				bestScoreByPosition.put(i, score);
			}
		}
	}
	
	/** return per-position improvement score of repeating ngrams an fragments*/
	public static float measure(String cipher) {
		return measure(cipher, false, false);
	}
	public static float measure(String cipher, boolean excludeNormalBigrams, boolean showSteps) {
		return improvement(score(cipher, excludeNormalBigrams, showSteps), excludeNormalBigrams, showSteps);
	}
	
	/** returns ratio of how much of the cipher is covered by the repeating patterns */
	public static float coverage(String cipher) {
		Map<Integer, RepeatingFragmentsFasterBean> map = score(cipher, false, false);
		Set<Integer> seen = new HashSet<Integer>();
		for (Integer i : map.keySet()) {
			RepeatingFragmentsFasterBean bean = map.get(i);
			for (int j=0; j<bean.ngram.length(); j++) {
				if (bean.ngram.charAt(j) == '?' || bean.ngram.charAt(j) == ' ') continue;
				for (Integer pos : bean.positions) {
					seen.add(pos + j);
				}
			}
		}
		return ((float)seen.size()/cipher.length());
	}

	public static int total(String cipher) {
		Map<Integer, RepeatingFragmentsFasterBean> map = score(cipher, false, false);
		Set<RepeatingFragmentsFasterBean> seenBeans = new HashSet<RepeatingFragmentsFasterBean>();
		
		int total = 0;
		for (RepeatingFragmentsFasterBean bean : map.values()) {
			if (seenBeans.contains(bean)) continue;
			seenBeans.add(bean);
			total += bean.count();
		}
		
		return total;
	}
	
	public static void compare(String cipher1, String cipher2) {
		Map<Integer, RepeatingFragmentsFasterBean> map1 = score(cipher1, false, false);
		Map<Integer, RepeatingFragmentsFasterBean> map2 = score(cipher2, false, false);
		
		List<RepeatingFragmentsFasterBean> list1 = new ArrayList<RepeatingFragmentsFasterBean>(map1.values());
		List<RepeatingFragmentsFasterBean> list2 = new ArrayList<RepeatingFragmentsFasterBean>(map2.values());
		
		Collections.sort(list1);
		Collections.sort(list2);
		
		int size = Math.max(list1.size(), list2.size());
		for (int i=0; i<size; i++) {
			String line = "";
			if (i < list1.size()) line += list1.get(i) + "	";
			else line += "(end of list1)	";
			if (i < list2.size()) line += list2.get(i) + "	";
			else line += "(end of list2)";
			System.out.println(line);
		}
		
	}
	
	public static void test2() {
		compare(Ciphers.cipher[0].cipher, "L16C<+FlWB|K-RcT++^J+Op7<FBy-UzlUVtE|DYBpOB*-Cc+R/5NpkSzZO8A|K;+>MDHpl^VPk|1LTG2dHER>(#O%DWY.<*Kf)Np+BM+UZGW()L#zHJBy:c^l8*V3pO++RK2Spp7ztjd|5FP+&4k/_9M+FlO-*dCkF>2D(p8R^q%;2UcXGV.zL|#5+Kfj#O+_NYz+@L9(G2Jb+ZR2FBcyA64)d<M+WCzWcPOSHT/()L++)dW<7tB_YbTMKOp|FkRJ|*5T4M.+&BF2<cly#+N|5FBc(;8Rz69S^f524b.cV4t++lGFN*:49CE>VUZ5-+yBX1zBK(Op^.fMqG2|c.3");
	}
	public static void test() {
		System.out.println("======== test");
		String[] ciphers = new String[] {
				Ciphers.cipher[0].cipher,
				//"L16C<+FlWB|K-RcT++^J+Op7<FBy-UzlUVtE|DYBpOB*-Cc+R/5NpkSzZO8A|K;+>MDHpl^VPk|1LTG2dHER>(#O%DWY.<*Kf)Np+BM+UZGW()L#zHJBy:c^l8*V3pO++RK2Spp7ztjd|5FP+&4k/_9M+FlO-*dCkF>2D(p8R^q%;2UcXGV.zL|#5+Kfj#O+_NYz+@L9(G2Jb+ZR2FBcyA64)d<M+WCzWcPOSHT/()L++)dW<7tB_YbTMKOp|FkRJ|*5T4M.+&BF2<cly#+N|5FBc(;8Rz69S^f524b.cV4t++lGFN*:49CE>VUZ5-+yBX1zBK(Op^.fMqG2|c.3"
				Ciphers.cipher[1].cipher
		};
		for (String cipher : ciphers) { 
			System.out.println("======== score");
			Map<Integer, RepeatingFragmentsFasterBean> map = score(cipher, false, true);
			System.out.println("======== improvement");
			float[] probs = probs(map);
			System.out.println(Arrays.toString(probs));
			System.out.println(improvement(map, false, true));
		}
	}

	/** period 28 seems to have the best improvement over Z340 */
	public static void test3() {
		String cipher = Ciphers.cipher[0].cipher;
		String re = Periods.rewrite3(cipher, 1);
		measure(re, false, true);
	}
	
	public static void testForTestCiphers() {
		String[] ciphers = new String[] {
				"HER>pl^VPk|1LTG2dNp+B(#O%DWY.<*Kf)By:cM+UZGW()L#zHJSpp7^l8*V3pO++RK2_9M+ztjd|5FP+&4k/p8R^FlO-*dCkF>2D(#5+Kq%;2UcXGV.zL|(G2Jfj#O+_NYz+@L9d<M+b+ZR2FBcyA64K-zlUV+^J+Op7<FBy-U+R/5tE|DYBpbTMKO2<clRJ|*5T4M.+&BFz69Sy#+N|5FBc(;8RlGFN^f524b.cV4t++yBX1*:49CE>VUZ5-+|c.3zBK(Op^.fMqG2RcT+L16C<+FlWB|)L++)WCzWcPOSHT/()p|FkdW<7tB_YOB*-Cc>MDHNpkSzZO8A|K;+",
				"zkLBlp^tW)FP2OX+<;5q>KG7DR&V%jYtk+dO(|5FWKLclp-42#lMLC<5BNtG3DX*|JRf+DzRO8#pcS^+|JH(TVK(ypM*Vc.b#JdB>&FBc|5FBc4+>LRBV+OY-*4+yp.#/K^B+4GWR2|cqzUb6;+Ed+cT#-Z3M|y582_1:fp49-YCFP*-O2_Rb+)SkB>8HUGBM+Z).VEj6T^9*|fHERO+G2KS/klF)D8.z(A5Tl|N:VUzWO++k2d+9F^LcC(zKf4+YNO<p7UWz<p;(1zZ%7|Ak(p2.LF++FMCcB|H<pdUC2JT+GcWlZ5M+y^SM+_NP)/tBFO^+1l*N+yK@O9.<R6B",
				"BT;+cqtp/+2F4bk*|5FBcK<O#y)HER6SW89+JlFM(2|H45R.zZ)d9|k2C5pWK&+zc9+LG;+tLf>W^zjBd>-<c4N#OVDA+zSYCU1NB.F^pNY+OT+K%pk69+_*L(MdVc.bUXR@l2#BO+5Rlc7t8B4|MpD5T-NBJ+.2^+|ylCp1<-O+UpMbdSY2#j<Ff)zVZ2TEUFW)>(&zN+:cB+kCyl3MO(@W*|JRHzLq7pl+4GlCX|%.*-OM#>7cp/GD6BJ4^|T5(HtKP+)GGOdR(^8BAKWVL.;+FBcSPFDy62B/pKP|MZY1f+UcF+<+8p:-*fL/Ez+_3^|5F2O<(V_+yOG*KzRk",
				"G#d>pXCOfcVTFRy)+d2JzB-M61lWH+Z9L+#VAWGk2M)>^+B7NpUK;<4.jP2zZ_9;^C:DUK41Wy#+Y+(.+DtC+TMZVlk5XKzU#FKkpM.(zOcHERq8l>5_FZP/O+YKGTJRL|pyLfBb2^TFBct|5F-zVk*6+(|+DRJ|*%yl1+5SdB-S&K<k|URLpV(5cO-84@l+2/R*WNGBq<MO>7F9(ANCJ)c+DO/%MW2Yp|p)4O3EG6OF2+tN8-*cYlGNMB(bpj3zfK|4+RPOF):B.8l4*-f2;py|<^Cp/cEL&|+OHzc<*B+b.cV<#27^BL_1CWS+R9SU+t%+zO(FB+|5FBc;.5+z",
				"RT;%cqtp/+2F4Wk*|5FBcK9O#y)HER6SW8<+JlFM(2/k45R.zZ)d9|k2C52WT&+zc9|LGG+tLf>W^zjBd>6<c4N#OVDA+zSYCZ1NB.F^pNY+OT+@OpA69+7*L(MdVc.bUXRKl2#BO+5RlcHt8B4|MpD5T-NBJ+.2^+|ylCp2<-O+UpMbdSY1#j<Ff)zpZ>KEUFL)V(&zN+:cB+k6yl3MO(Cb*|JRHzLq_pl+4GlCX|3.*-OM#>_c2H;D6BJ4^|T5(ktKP+)GG%dR(^8B-KWVL.;+FBcSPFDy62B/pKP|MZY1f+UcF+<+8p:-*fB/Ez+73^|5FpO<(V7+yOG*KzR_",
				"ABCDEDFGHIJKLMNLBOEPQRSTUAVCWXYZSaQbScFPIdefghbSijklEEAGHCJKmEBPPnZoIcpPqbrstMTuPOvwSExeyiAXVzUZ0d12R3KhPNSTlJCenGYbwHBjfDFGHWrPop4IPKKA5qXPMPkOZ0QV16gCBGFiKxPLUPhEaoIQq5MP7OjpxtuzQEKUV1AiK8NS9gYvDWfbPmQj4RKFsHPkh7oQuLltVrbI1Adj6npmzq3CPPBQ7M9O70gJIcLwsyPAaxRiQlHBE4UCkn5hDFGPo2mIBP7qvQeMZPPHw5OWAmCBG3YVKE5x8fSe8JQZU7QaRSXo8rSmEHB1t8j0dGNP",
				"ABCDEDFGHIJKLMNLBOEPQRSTUAVCWXYZSabcSdFPIefghicSjklmEEAGHCJKnEBPPoZpIdqPrcstuMTvPOQwSExfyjAXVzUZ0e12R3KiPNSTmJCfoGYcwHBkgDFGHWsPpqbIPKKA4rXPMPlOZ0eV15hCBGFjKxPLUPiEapITr4MP6OkqxuvzyEKUV1AjK7NS8hYQDWgcPnCkbRKFtHPli6pXvLmuVscI1Aek5oqnzr3CPPBG6M8O60hJIdLwtyPAaxRj9mHBEbUClo4iDFGPp2nIBP6rQNfMZPPHw4OWAnCBG3YVKE4x7gSf7JyZU6iaRSXp7sSnEHB1u7k0eGNP",
				"ABCDEFGHIJKLMNOPQCRSTIUVCWXQYZabBcORdefghUMCijJkAAHNlDCIminFKQTHoZCPbUpjCaCqnAhBGIrefQTJsRdWKtWEuvSFwCxOavdyWz0CNliblCF12OQuCbma3l1nX4Z5YC5HhCPWUKgRyICJeGLDZpSCuVQOCXkCo3HbmqZFPCTneairYgINfEMZlIxKR6JDmaPom73cwZfKSQpYbUFhZpJ3sINaComkZfGUn2WrjbVlCuHhURPIJp0NXmtgOEs4xc66zZltNayINCWRCuCTabJPOzNfHS2RlGmyhCigopRvwBeGomCfnDKXCQ1UarvQdyj6rl1XEFGv",
				"B+LR^1.4pK()kp+J6q8IyM(_pYfOB)zV%;+*4G^DyWBS+XKYEzy2NFfLA:pO<c5DIH(dBD^*.4+ZRCIOLTEFHIJf>z2LpKp+<+_++WNBlOU+j)2TRIb(.+IJIWZcWL5kWc&P#>Wd.9BGCWCFT.*BB+k1ONzzU(DK+EAcOp2H2O*cH-+/6M8Iy):jWTRd7GBKBc729OMFN>ZpGt4P_^2SkMGpc9RG;lY(W*V-V+4<PM^#S16I2#BB5%czFzCtU8+;lzIyO+q(+N#/<F/2OtSCRlFbXJZUW95ccp3^7dL&3MUV><@p++lkV+.l--+*#M8Ft<VFlzYWF)Tb-dfRR+K+4",
				",\")0/AG-EY(8[O%*DU2!X=Q;-H\\3199!O.430/9HF3&%IG\\+B]M>$=!1#:[L<E3Q)/3J7VY-@_T0^3:GH2*9%I'=Z\\;.3][351QPIFZ]A31%I4U.4=,DK5X7)4,]X3G3AQOM6(EU34M)[YQJ9=QXR43$FA,13%C1WB=-@/^EE?QU1M3#50H+%T1F!3<4YG;2S3=E[3YVI')]O-1I9D]X]/\"%3#?C4;7A@J.FYU4\\%M]]1,=)TQ31]8WA]+G(3I]\\U1<<I]6%4[Y103=I@QQ-H\\3)D_+]##J\"-TT1*BW%_Y6UN9\\,=]JRZI>R&%N0#<9SH\"U\\.-341)E4OAF(3G0U",
				"W#(>54D)W427!BUA\"3/L3E:!+@))>&HFF^DU.E4!<$P@03S?2QIW3NT._'3^&-3)-Z4(.+BB[]&=;]ZU^%P1>7\"!NJ9@]QD3ZWA2#AC;KT$=3XYB\\U4X:^143<3P\\->65B2.7QTE3G%0Z((>!3:GDBXSI3U,E\\34K3\"TDWO3>JF02YGQ%KI+]D<)4QB1U)ZJP](/3L4BWUC9MV_K3R)43^8T.BKU=%X)3!BD>45WQQ;O'UN:63@V<Y)APGU,SF'!RU4:EK)%LC^X0VJY>43ZQP(.GW\"&YFT3Z%UMU32)HB#YU>A!B4@K6+Q3BI=4T3D-:,Q;><P_]Q9.57UW33\\O",
				".pEk-lT<PF*9C+B^At^qUG|yVc+BKZl(RTH-*V<NF#pcC2L5WKBG@|tN)Oj|2J#+SBcK>4kpbM+<Mz9dVp*N2O1X6Y#5pOCLOdc)K+<_&*FBc5ORNF(UU%ZpBM+^^L>>;6ctYL2+OTyzZ)8A|K_H:WFB4YfC5Ob.cV^WlM2>9+-tyS;F/Bc<O#HERp-LlVGfUp(.fz4;W%8dz)kRKE7+b|5FS41P(O#|+T3GR&_/<.l9UCWzLHO+BdpD++J+++B+f++p8^%*.7RJ|*T2+b4qz|YjWG5D(F|)NzKMF+*-k.c+(P8JM+24l.zV6/y|5FBcRyd1NpR(:lk73XSGH+BM",
				"82(1UyKbjJ.#5BR+3+29@TSp1l-^NBtHER+B|JLY9OzFR(4>bl*VLk+FU2)^RJ/c5.DOzB(WH9MNR+|c+.cO6|5FU+<+RJ|*b.cVOL|5FBc)T(ZU+7XzR+k>+lpyV)D|(#kcNz):69Vp%CK-*<WqC2#pc-F_2B8+>;ZlCPTBU-7tLRd|D5.p8O)*ZM6Bctz:&yVOp%<K+>^CFqNLPp*-WfzZ2d7;kl<S^+/|dT8f4YK+WGj4EyM+WAlH#+VB+L<z|4&+OkNpB1V2Ff/)z+Mp_*(;KSp2(^GO+FBcMSEG3dWKc.4fG5pDCE4GyTY+_BAdPzp|+tFMPHYGK+F6pX^2",
				"ABCDECFGHIJHKLMNOPQRSTUVWXYQZVaGSNbcdeLfYghQijklmgJnoMGpdTqrAstSOBuSVvwSYQSIEHKejuhxSFsDMuykraCfwMJiRZlUzSvNnGvPLDLP0VK1iuSUqghobHwvAkFSBXpKWGjS2eUrSIg3qFiTLPOhazYjt4ZuNSnHSmSvfS5SKR6vOSBLQZQsMqedJ7EG0dGItPgjxRhFHTSxlJsMhQ1y6Dfy0QoBVnw5gHNYkrSevQXhKozIZZDNfKVKv1TcufOXDUTQPUBkSIhM3QoISr6GSgd1lPEVSWB3qzIpPZNOtCSD1JPZSkRhvxLBJiIrvW4n37YUMzZM",
				"+D!>A(Z0%G%ETQ7.2)5M>-4PT#34J^:,4U\\BYX>=$3K;*3#2AWZ3+Q]'9JN!\"7T4P%^)GBUA;3[LK,!P+'BU>&HFY3D).EY!<$:@03'?2QIW3NTG_'3KR43)4ZJA.#(%5-DBW127!4;=&][BUW:.+BXSI3),E\\34K^\"TDWO33JY03YG%>2I+]3#U4Q)1U)ZJP](/KL\\)+BC96V_^WF',BGPAU-#*@36!7B'O;TQW54>DB!%)X3$UKBZT8^WJB?'!RU4:EK>U4<BHB23UMB%.33F-&\"WGG(PQZ3Y>Y1V0X^CL>)A!UJ@K6+Q^BI=4T3D-!,%;><P_]Q9.57UW33\\O",
				"K)IM3$Um8lnJ#X5-*-MTCgN(96AcPgna$HaRY6cWb0*dN4LCAORc4_RejkHFDE3#KpnAPIH!M(TUS7*50*mHaMbQd*RK4iXHVAL$AGRQXCSa#k-&LK02aiIW1TK7!QJpUmMbH9nH!T*_(OfRH@7lQbHBkSC4K67HSR-gQ3Rc561-ZN(RDd9A%X_VSUhJaRg7&XW4Mn5hRT_m*3D$IHgo!X54b*8T6cpYEHml%Cabk*I@0U9oA#$4G0QX$SW_9E3A(FUCjYnhHdKS0H4m6*CBn-IQMkg-A6(*nH!aLm4GX5JU01CPDZMb4AXH8k*n%d_Vb*F_)mX9#$B6*&%D$TF)",
				"GJ$[P8E0%88C#R,466&5H);\"WIS$>@UFLB-+;JEAQ82'Y?NL88*V8=G:)88\\Z<09CZF=Q:<(P>,BTG\"J3@HI;%52V!G@S,WFUWA_]G\\.Y8L\"I5$2VF,N]8:/UQE)0>'F=+*Q8C\\GX*@278(!F\"^SA-B$%TE8CAO#W2.8R^Q/GHPK7+&!<R5YS;I8\\0A/$-CDU/BT'=[>,6N1E6(V\\GY:JE.3=<S4])AU+7/$^-*IJ5(P8'9Q7[1$)V&!T<3'Y/E8#^B]_GRWI!9V%7S-.>*]XE?$:PP_ZQ0%&WH,28FM\"QIK5?Q1G6)8U7C$X!D'LEM8V..3=$];@PD,L]E0%ZTM",
				"lo!ZA)]B<BPs*gzfbk$&hdhLDvG!<8`_?lV4l[SiFtlu=?lylCA-=&wkl>o:HTuP6riN8s5ML\"ZzbkC_?0Ahd)Ilu=DG@`?>PBtTs!eI&p],hPyh[bH^@,wS6f>GxM)0_ysFoTEuAilV)lldfu5>$&H0H)I!l=Owl\"Vo@uPDrt<0b?T>leMVjN_4E,$4lCAx^GV[hg0POpB=yl500\"8Bh-lNo0k`>IOlills0vt[P?lVgVy`6hV&Ze0SHlb,w6:s]*],^dxpMM]5hu_dV-rA)Ee$D)IL$ON=Gv_e,z<@fCDT[V\"L8o0thV&!O08dlBx`!Pilp$?HIZlV]yF4[S-u",
				"(Gkj|qC5MY>Ey4pP*tT3FLK+Hp2)Bz.c_GMJ#6fPTNp+&^CFUqZ+D++O++U/c+|5BX<Tt+.E6RlKS#@d|5FBc+p+YByzOtFHERtNW)#zZ(9|7cCBO2)81B+_7SL4.ylB(p<9Mzfj^A4;#Y*2|kH+Z<W+8-+HO2/9B*-+^l8(/-<pODG(K*zLf2WcF|+N#cRFJpyLXlVbN;O4z>Ck85(-*P+BW2c3|^LMWT+GN|lkMp%^V5O4-1R>K6G5DKz+Ud;OVpARJ|*FlVVkb.cV++Gp2RL|S7T:|5FBc&W+1.)zJ>^FpY(dC.cKB)2OURB:+_fSZ4bylUDp29MGO<<K%zdM",
				"+#(%5-DBW127!BUA;3[LK,!P+'BU>&HFY3D).EY!<$:@03'?2QIW3NTG_'3KR43)4ZJA.WUB[]&=;4G)^%P4T7\"!NJ9']Q+3ZWA2#3*;K3$=>XYB\\U4,:^J43#TP4->M5)2.7QTE%G%0Z(A>!D:.+BXSI3),E\\34K^\"TDWO33JY03YG%>2I+]3#U4Q)1U)ZJP](/KL\\)+BC96V_^W?BJW^8TZBKU$3X)%!BD>45WQT;O'B7!63@*#-UAPGB,'F'!RU4:EK)>LC^X0V1Y>Y3ZQP(GGW\"&-F33.%BMU32BHB<4U>A!UJ@K6+Q^BI=4T3D-!,%;><P_]Q9.57UW33\\O",
				".53lW+1p2VzZ_d/^C4p+O1^*;2UfL|5FBc+ZP-pC<)NKD#Rb8lH9k6z<NLcFyCW5+%p4S2.FXNzfR+V%+dJc+dMctdB+V4+kSRJ|*|LFpb.cV5_KzBMMT|+R4HPk2cMCH4GOyGJ7z+2Ft#.+*9cB^|)RT-SVz<Mb2LHER8+ZA-;p&(.+q#/:kjF^W1R+cK_*-z#YB+U5BU.yp8LO|92|^>tM(N7TWKS+B8R-*l#pk;_@D:TGOd2(7UHB+>69(p+|GZ&2/FENX)+OY>Kfc^B*jB5<LUD)|5FBc+WJFK+(OBlqT6YfAWz>3EFM(|*Kp+(D)+ptGY<COO4y<llOPGOl",
				"ABCDEDFGHIJKLMNLBOPQRSTUVAWCXYZaTbcdTeFQIfghijdTklmnoDAGHCJKpFBQQqarIesQEdtuvMUwQORxTXygzkAYW0Va1f23SoKjQNTUnJCgqGZdxHBlhDFGHXtQrscIQKKA4EYQMQmOa1fW25iCBGFkKy6LVQjobrIUE4MQPOlsyvw0zeKVW2AkK7NT8iZRDXhdQpClcSKFuHQmjPrYwLnvWtdI2Afl5qsp0EoCQQBGPM8OP1iJIeLxuz9AbySk!nHBwcVCmq4jDFGQr3pIBQPERNgMaQQHx4OXApCBGoZWKF4y7hTg7JzaVPjbSTYr7tTpIHB2v7l1fGNQ",
				"(;+Hc^BE&pPN5FB*UcYW+T2b4FD<OK+RyT+I+JjR8I1C3#LBHlB*F/^2W.zGO7LBV4>KO<X+;y31(d)MFzW/M_Sj+lpfUC)l.DF+TIzOJ*zHVI6zP.2A+U*GZ>+(&1#cJkNpvB2VBFOORIw+Z>Bz68@Y9pTB2C+#lcq5M*RZMIp<b+EUckC+l.zFC5Ik#cG25dp_-^Rct+z7J8M2(#Gc^pMpFHOR-+D2K94bBI)L^5tLpdWpAFpYlOk+NRyKYBR9k4N.^(D+%dOZ<;*-K6<(t+z7S+GFcOL5.SV_Lqly%G:y)f2+T-(f+I-4XBKd5<9:)t+S8fN+>WI4MEPcUV/K",
				"ABCDEDFGHIJKLMNOPCQRSTUVCWXPYZabcdeQfghijULCklImnAoMpqCHUkrDJRSGTICObsNlCaCtrnjBFHughPqIvQfwJxWcyzRD0C1eazfiWKAoUpkTNCS23ePdCbUh4e2rmXZ5tu5Gj1OWMJCQoHsIgCcqMeRCdVPeCXmCT4GbUtZSoCSrgaluYijsaBLCpT1JQ6IqMaoTU7Cd0MhJRPptbsDjCeIrvjMaCTUwZaFIr3WunbVpQdGjMQOHINAMXMvieKvmud66KUexsJiTMCwRCdCSabMCNcMJGQ3QeFUOHEkojpRzzCgFTZCarDamPP2shuzQfol61e2XKDFz",
				"PH3lW+(.GVzZOd;^C4p+_/^*EMUfL|5FBc+Z&p.C<)NKD#2bWlf9k6z<NLcFy%85+Cp4S2.F1NzfR+VyBdJc+dVcRdB+:t+kDRJ|*|LFpb.cV5_KzBMRT|+RtHpk2cG%H4GOdMJ7z+2F4#.+*9+B^|)RT-SVz<Mb2LHER8+ZA-:p3(.+q#;/k^Oj8X2+cK_*-z#(P+U5BUpyp8L-|9O|^>tMTN7(WKS+B2R-*l#pkX_@D/TGOyMY>UHB+Z69bp+|G>12;FEN1)+O(7KSc^BtjB*<LUD)|5FBc+WYFK+YOBlqT6(fAWz>5EFMY|*KP+pH)+;42(<COO4y<llF&GOl",
				"ABCDEFGHIJKLMNOPQRESTUVWXYUZabcdefTEghFSijOkkfSVlAmnEopGqrMHsEWScCdPtuqSlvwQKxyISz0J1ESCGyFW2MQ3JyDPYUVxrd4X5Pih6OHalcKUOPmSwVWSTRZl78cuQbqS9ejCPytho!S0d2lFiHSKmSWEpbqTo2iSC1xvBGYZTE9NydWPbhFCmKM0NxqaSzTyl7unofSRKxyThU5SCFOyRGexP09ahH0vSroT6LMg0u3BDHijO2SKhaslTdUWEGaeq4xPChNScLP3bSyFkTKfcSSfk3lvhIWnAN1UfEKYJQkbpkTtZWTd23hDqyAREJnljWr!KM5S",
				"!\"#$%&'()*+,-.-/0123456$$789:%;<2$=>?@(<AB&!CBDE4F;G#$H5$-*I$JK$LM,9!NO,P'QR\"ST!!'CC4&U6-V*<%WX(&E28Y8Z$[D!E(!0);UG)HS,\\4.K81]^5A8<5L_BN*7;D0OEI]>,US%J#:$Q\"3*24Z6P@=F%'V[8(0.8!$<'HBS&$TD5EKM$X1-@$RWX,U<`142NYB(\\8)_$&L:I^G7;S0E>=$B!$,F4[;aDO*!EMS$*VR%-(%$W\\Y^,]5/();P7=>4$U8!BF8ZD92&0AT-62['!;J+E$QE5\"#',.a<U$P?B04S!a$<,M_I`\\$@-54&%^S;&9R>83",
				"ABCDEFGHIJKLMNOPPQRSTUVQDWXYZaRbZcdeLfFgLhNGWFiKMVjkPTEAGVlJIRmcLnXoFpOqrGWKUAplsFtUuKvIHlgwnxAgTaNAodeYDWSLyIGlTEKhfzUJM01BlDF1OtACkKBGXTxlPI2BFQguMjRm3qDEK3ZlzORrSnbO4IdN4plDTQ5VWhcbO63RyaPqaHDxKYfEltJezFgGWSbWKpa1l5udH0L0ORU75FITuKODgieGGEKtFA5KDWplFvxQnE2Fzhq4TMTWlbj1FHfIDwYOBcRXqKauGxlLFWKVtlgTCFLv3DWAuSKqrnIBBlr8BO9ARr6QCkLxc!IqmGEh",
				"ABCDEFGHIJKLMNOPQRSTUVWXBIYZaKbcdICefQgACRFJHODIUfhiMgNjSEkGAVlbmTZjEeJGWLnZopqSPVDIUcXdrmjBbehsGkEJtTrYuviGOfHcwSPnOgltApLGDVIMmPuFdIUxyObiaPZKxNkGLeJTz0fgqGGAzCyOSyRVm0bqWcejEQBsDozLGPdOJ0PkUfgySKYAGqGZapHjTruM1EGChRWQVZXGDctiCbdUFBfeGKJjgSREkVGNlpWGBKrQZsGtzRnuAjObGW0TBPeOPmGscoqdXxYkhGiJLHTISIODUfgVacvbyeyJAnmlMIKdRNk1FpzDUvL1fsSVvW0s",
				"kpP+tVH7Vz(;btYtWMJGLX.UtN%:JM/6Z3)Y!b5#TFB!_tVH#K%S8fCd7Et98>AR1QY2fQN1lLdEDW63X5Tp.GJlP)-Ctf1zdUOV_/4JZ(K;)Y+pl5TB/EXtKLESHV6!B!PptY%^3M(V&zk%RbF_H>CX93lW#LN+t<RLD:dCq.;bz1AH#GcU57f_DpR4HD-MZ/W6V!QN3_lGcZ;7)XtJM&EX%:U-<RdQYP+19S5C:tdGftJ7Mb;)NY(WE1Y3Z9YF8(./K&Tp)1S>Bdl+ttRttG;CK;#V)%:JZ(M88k;NY5W:FEZFCX(1q+PtUj#-V>XZJM^Ftt+3HMXFASXpK^+1",
				"ABCDEFGHIJKLMNOPQCQRSITUCVWXYZabcdeQfghijkMCllJmnAHNODCITopFKRSHqZCPbkrlCaCYsnjBGItghXDJuQfvKwVExyRFzC0eayf1WLA1NeobrpS23eXdCbJhpO2smvZ4Yt4Hj0iVkKCQ1ICJgCcDNrRCxUXeCWmCqpPbC5CFPLSsgKltYijkhBMNOI0KQ6JDNaPjk7sxzZhaRCO5bkFjCOJpwIJaCqTvZhGks3VtAqUOXxHjTQPIJrANWTwieEumCd66LZOwkh1qTGvRRxCSabkCOBNhHX3QeGTibBoibeCzzcgCqZpKsDaVQX2kKtyQf1l60r2WCFGz",
				"KKxbh/K@.KKrSs>cH>GIlJKXluI^L2lJmK.@Ql?Ln0j=,jIE@fbN?'u)(0,iQXd+YH=]')o\\K3YWeza\\jUKL2T:f\\'ICKeLS0>V(i+.ijrn?KEzUCGQtFKGKKJ:mR+E]B6BKXmJj,MugYaz)H)V?R2Ec.@sd3`sICTMg]hRTGSG^K!GUr.Yx!\\iJg]K,?i:W\\MJ:M6C,K^6rTx(F`UK(o@.3)tH(JjLSmF>)zXY=j+.S'lC2,ja]'n.EL/,rna2l0>?Mdih]hcd/HWH0Vj2@s0W=ulY2`jEfB2+.@DKzUNr:tK\\!aSmKLWuKLtsUbmKV`/xKN(3fhJVoF>BaQMuX",
				"ABCDEFGHAIJKLMNOOPQRSTUVDWXYZabcdefgDhiKcLMjWEkFlmnSOGioGUJIAQpPDIXqEHNQpDrETosFtiuTvimAEEKwdENcjaMoqfgiGiRDxNSFDiJLhyTIlz0SijE0NSNBCFCjXSrJON1BiVKvagb2eQDWEPZFSNh2Rdco3Afv3HJjGRYUEaeDN4PbplOQX5SiishrEudg6FKDWecEJEa0iivli7DzAbT8HEAjOJNGKkgSDiJYio5EjrHFJUWVZE1iCLQ9GLGWEcn0FuQADwSN6ebLQEaOjrJDFWFUiiKGyiDmPSEoMPEQ2IASBFx3CNzoQp4VCyDie7AQpDrf",
				"HER>pl^VPk|1LTG2dN+B(#O%DRdLW>Y.dV<*W+Kf)y.:T#cd^MkpUWZ>YdzJS#7ky^831_k3+9c|tj:YD5>F&FH.d4OM/#P-G2(^WCq)#TRz;Y98>^+VX1d|S7HdyW:(lcNB/*k%dt>ZMOK<@Y;dzdj+9-kYX^7)RbW#.|qdJT+MRW&d<ydlB%k>Ff8GzNk2:HAKSd6Z9ctYy.W_(;)&J1L27/^dES85G#Uk:qY6D+H4|tcb>.N(dOzW8M)jY;BSV^%FXH-#&1y9AlK*2EW<(:ddyc.|Odk4dFbGT*7YZ++1#J)>_^zOkAtjc:#ACd>XbM^8Pl6LdkF-Wq4Yd/NS",
				"HER>pl^VPk|1LTG2dN+B(#O%DRdLW>Y.dV<*W+Kf)y.:T#cd^MkpUWZ>YdzJS#7ky^831_k3+9c|tj:YD5>F&FH.d4OM/#P-G2(^WCq)#TRz;Y98>^+VX1d|S7HdyW:(lcNB/*k%dt>ZMOK<@Y;dzdj+9-kYX^7)RbW#.|qdJT+MRW&d<ydlB%k>Ff8GzNk2:HAKSd6Z9ctYy.W_(;)&J1L27/^dES85G#Uk:qY6D+H4|tcb>.N(dOzW8M)jY;BSV^%FXH-#&1y9AlK*2EW<(:ddyc.|Odk4dFbGT*7YZ++1#J)>_^zOkAtjc:#ACd>XbM^8Pl6LdkF-Wq4Yd/NS",
				"9%(1UyKbjJ.#5BR+3+28@TSp1l-^NBtHER+B|JLY8OzFR(4>bl*VLk+FU2)^RJ/c5.DOzB(WH8MNR+|c+.cO6|5FU+<+RJ|*b.cVOL|5FBc)T(ZU+7XzR+k>+lpyV)D|(#kcNz):68Vp%CK-*<WqC2#pc-Ff2B9+>;ZlCP^BU-7tLRd|D5.p9O)*ZM6Bctz:&yVOp%<K+>^CFqNLPp*-WfzZ2d7;kl<S^+/|dT9f4YK+WGj4EyM+WAlH#+VB+L<z|4&+OkNpB1V2Ff/)z+Mp_*(;KSp2(TGO+FBcMSEG3dWKc.4_G5pDCE4GyTY+_BAdP2p|+tFMPHYGK+F6pX^2",
				"O9PP5o97W`flWZ8OmAQ+@@k\\Ah8OEW9Mg0n\\Kh8+D7NRVe=anVD`SLia:C7oX0@++ZDi`fR+b0X?dKk?+E8LV7YMgcSNhZaE5o6EYZMo+\\KM1ocgbm2[BZ8dE1LZB`6+aCGU+f;KjQ7+g+5]M?Dl;e1gfFX@lO+4A+iCG`hj67D+QAEgal2[jgAMLHWghCNB74hWIBoX+;C1Z5RIB7+fIcgk=XAPHOeENe7WBd@SRga]++D9@bJ?\\0a1`a9ih2+8ga+WZoe`+L:mL?ea++TIhWZBSoSY4=5NXKh3[CH0?E9^de=oCCMik2SYZ7gBL[BPXB@4\\eh3_=[R[ELYPVb+",
				"041D,%(!9=:AN).-8\"1J@Q,T3KA8G?MO'2E/X3FU\\-GL_R!-%:#3W.&HBO8\"4G]<CX[+.E6IV8Z;H2=-K6()NU@+MFRS?\\Z+8EQ1E,_LJ9%TQN!EYHS$<^O3EX/IWE]-#C-E.>40P1$Y6B:-@VJD08^1MOEWQ=18IETGK5U1]3)FGS!3'@1,DHG:R0\\AQQX-_L%SD-=VKS0-UC(.PY?8[JLD4Q0N_VT9E!,AMB-E=8KZ<EOQ+X[6W&]G2$:8C1.)>(38PH5E'YG-\\\"N^I@B>\",/6MS'-E-WX<]F)3QCQPS1YI^[*4R3G?_%.$/&3@-ELKAD-UE\\JV0HEDNS(TE%7",
				"K[/HE@[B_G\\PSOQ2KI&?$4TMP0\\FZLB[1;\"WD0I!<STN_O=54XWDP'K1RFH4S/XUZ1-_!T33>\"QP<]*2YT&$X`WN8?\"*/F;&BAHK>\\O\"T13^\\_$W/+^JD.$S\\T4]GM'K@LEWB0=`QYZ?T2H$I]U&F_<;!D<I\"!+3SRX@+Z-K=FWO1;R3I4<STJ`00L@BN5/1D8P!*2;4_>VO_5Z\\Y`MW^QD[/K\\'\"<1!!OU/XS/J>/YENTR-\\3PK!1O!IY5/'A&O!<S.W\"\\G4XS___!?\"K_/]KAVW_`$<AT*T^;JN$O[L.\\2?!U&OMB!EJYUD&VF`=WZG\"]*I^KTVF&T/<5<SJS\\",
				"ABCDEFGHIJKLMNOPQRSMTUVWXYZabcdefgShijklMMmZTUnoIKBpOYqZPrRHsITWEAPmtuGKjvwWYxyOkz0R1k2V3YkbhjxRIguTWQr4PNcG5ZCDB67vAeUIC4tTxjyM2bvLQUzkG48eZwcShk1aCmj9rxcsbUY32MWmI9PWgOqkY30zPHdbljWIxGZoPZRGkjV4yXIgTkIu5ZOXNeWlFMRZe5jxuSur0TxfOZajvGWm17xpYDAIkwASm1YxyVE4GWmOXLV5ZEtBKsCtgr4tACu7zEMWRrxLsOT4QHjyDzVKID19G2jkCgVwnRZyA578m5yUnTl!1yCVnUfooSZZ",
				"D(_SrTTHW;E^DX+[Cn#)OQLDYymy$9ABfrj4r7#5GZyaST+NCFjMEPJW1%XV29YrD>AnzT-+QKC7#pNU];[\\DljPPCk%3)+_ZM<HTD8]f^BH%TEfmDLSPOXk$4Gn;N2Br]YDCD>AaQFE[(j7KrS#pNa%)M-_y3EHA]9L+DmZ#$MzCn#J%TUPTG2jQ#1EfD;[_5Yy]NFrA\\EV+)DrZ7M%SzC3f>TLW;<p[jX$HFrZGl)(mM+PC^jVrkz]>3fE4%9l25T81DOn_4EYEFPpQ^AH@LZa1$yWJUP8PrT;(GQ+B8%3V#2[mNTOf][DFEmnE)-iQ#CDm7M_Vy%N3rY\\El#S",
				"S3)L*(1=!M+E.T4,UFN&5>926V8A1W3-9BS)L:GMH/U4*%?E'<5FXO+P6G[,(@-UC)\\HNQVE0@^\"IJL*4+CR;FAOM,#=9_-(W5)K8N$S*P[+L,9BIGQR-OP:HBC.T3C3FYQ*!A(RUGM+OV4,N-\\\"OW5)&#Q<H/'E2B\"'S6*C-\\,M-T3(JFC<!E)%D*\")!R%+0GN,$B)\\4$US-C.H>);B'N(\"Q*)C^5ALMFCEGW!P91BH0$E^KL8OAB+M9&FX?)GE>^]Y)1-S6!T\">?3\")I+N:R4()DB)@C5*!LUO@HV*@,PAN-=^C3\"S;3$)EFT%(+BU4+N,VGJ-M;P5\\8WDS3(@",
				"jfHQCHbc[GS`DlnMz[HKgaPH:VIbA2bG@Tz[HReWQU>H5k6GzY8QCH0HRi[BechjH^LmH9nS;]aHl=3KZHRDMHIJA1<:E@f4U]Y8F2g0cNmSVaXdbT<BOWQI;J>5i^HHA46kjGzh[9RLCH@=nlKGQM`5W1H3]8ifH0d^RkJ:Q2HHjmDW`UJlL0H^g`@4i6XeBGI5=Q:[fL3HKN;]YG^bMd=Q17|Ie8VQTmQ]Hcb[a9hSJSzb1i>Q1N|CmQ:E3<2GHR5@[F9XRz:7dcVWdIUe84RBNd<RkD]0e8<RKE09NOQl^[O=;4MnSTd1W7fzHChm9|R3jHSU1naA_3gBQBV0",
				"ABCDECFGHIJKLMNOPHCQRSTCUVWFXYFIZaPHCbcdDefCghiIPjkDEClCbmHncGoACpqrCsNJtuSCMvwQxCbLOCWyXz0U1ZB2eujk3YRlG4rJVS56Fa0n7dDWtyfgmpCCX2ihAIPoHsbqECZvNMQIDOKgdzCwukmBCl6pbhyUDYCCArLdKeyMqlCpRKZ2mi5cnIWgvDUHBqwCQ4tujIpFO6vDz89WckVDarDuCGFHSsoJyJPFzmfDz49ErDU1w0YICbgZH3s5bPU86GVd6Weck2bn460bhLulck0bQ1ls47DMpH7vt2ONJa6zd8BPCEors9bwACJezNSX!wRnDnVl",
				"ePCC0Og1Z]T+M]LebB`oSk+PiTLj+V\\;P0*\\iT;h^17*eB3SEH^]+MekOK196IdJWQD_hFE8bI6Jjab]B+LZj1Z;Pk+pTfkm078+R`L=*:-LXOkg++2fI@;e+2V]IJ8QdKjc`Fp-EJ1^PH5@LJ^+OBVPTN6dmjT4A+jKefF*G1DH]A+:dmX4EgBLX=XPTK7I1]TXh0p6+9KZ]0E[01fFJog+36iC=_a+Oi120_Om*:S4U[^:o+G4:5oVfkgeFXQ;\\kmZJOiQLV=+X@idX7U[T240+7+Xf30O6BTW4K=54+PYea39KK;H+R+V`1:02f0C6IS]\\iF:^DbVhW:3o[NY",
				".5)G%&+=KS[(!H\":#U@2-?HLAJV_$55[[\"8_*1')5CG\\(F&3?SWT!:ZPEB4%LQZ2OJ1>T=XE*]Y6)N;#![@KQ1H<,U\\^:+V(\"@6WE?3H.4]_LCI$-GJA,VPT87;%QG)S[FB=Y*!9^GKU.#(\\36O9S:(X1<QG9$S[CR<L04N@_5ZJM%9T!WG[-5<(+&=<:,$7!2PA_@TY54IT.*B83Q.IG1C(Z(8:,S6[;:\\EPR3)TY'<_45\\6GHC5Q9O[L\"K_6117HK\";C/)KQ[)G\"$4C!3!\"FAJ?S[(\\T*&!?\"LIVV2A:_;:T_7?OP1YAGE^.@NQW[^<E)%$P-!+@#=F[:E_K<5",
				"ABCDAEFGHIFJKLMNOPQRSETUVHWXYZaTFbcdUeIQOfCTbATghiGjPklmnUdHopqIKrsUtVZASuiJCCvHbHaEFefSUwxQySGrzEBgBmD0Xi1M23bcq0hIIhVFUebHcSfd4Ck5bSO6NIMwiGgv7zKFKeYI0UsWORDZaTScutjIn82pmLsAq3GAoQtLbzONSUIrKkMV0u6vUJePkLhiIuLCHO3vgczglxXRSEKNXFyBDM3s0uVm9TXLAvqeQGOSCJHbnI62aMZcEoDu6z7NCvAKFqeUdrt9HcJQhxyOSzLAsECGgPFLua2eUKAukO0J7b23VUSHiEFpvQJePNmcxcnZ",
				"[VK]EXk;,nHRix(^m\\B^\"wpvS}sH/�YFDq$bo|=�gblb&_-uwRGx9Ma^'b1*:Q~.;4,\\p2|X([^}\\\"TunbUMbFVQi]@vqDY�[-K2EoYbU]^g.t$l:&b/Y,n_Vk'm=p9b*v(;RYF\\4+|XuGb\\M}ql-Q.\\,2po_�Si|Y:�b'xYKgs\\u1*(bFVM\"/bt9wQ~DY.[q-,@=EypkBm$Xnlb}|Sob_:u;Rg^9MXsx}yHi\"U�\\&^/4D'$v*VQYK\\\\b1(-.�:E]koFba=RgB+m\\S9Y,qblVx[Y-�bnKY_\"XyEv2bwp;k'bi�mUb}B|S/*:&D=ut�(M;KosE$i[Vk]mgn^2Ub9F",
				"3:aON;653<!%C>.\\7#8Z<L6O\\Y|U:AO#7Y=(UB8,^aP+7/a>S#IOY5W;AOGYQ2C_Q3OF[+7PGU]N74()1\\6'\\*@M153\\86O&HRIL;3]4X56NE+*M187!Y<N[7\"\\;YMK\\3\\]6<&HR#Y.+X(>Y#]N\"(N7O3_9O'#P(M12Y+Y^>Q77>KCa4K8$3/,=(UB/,$;I]S-CO0;ND+YEQX;-BQ5\"<;Y(50>+.^18%<SO<Y\"Y3\\<,7<6*]V,/YL]B8#CF[,7_\\M:Q7`+I1DZ:\"7KO1>P5]Y<`[3O8,F*W5'J5\\)`'VZ<X*G05.BP<!(T8*\\^-+XC18%OVU\\,MG:DF(NI/V.Oa6",
				"<;?-(_70MBE>3\"83\".X-).'N=LZ<(YQ^JU#$'E?/M@%WD^+JET+AE-7I3LEO5<;!>&1H?EK#!BS6\\=NZ:R)%'(]^[E7Y/+C%F@TP+5OPD8JNAEL1.0L$AM\"KA_<I;(Q^GZ#?E>O73HY%E/+!1W@!TM6L#043>=F3R\\]EUC<K?NP9GEXHZ/!DT#&EO=KT7EF*S'6\\8)-(QB$2LZ+&><DM/#R*;YT3[6KW8?Z7@/+.T.#]EL4XC<5?\"I:P\\$XSVE6E3AD&V7UP[E7A;0KUGL+.2H8<)M!42?\"I:=)8^R%,F;A9]K74L0MEQZO[9C*WJS4E&1PXGU['NH,SJ0<\"I/6*",
				"8v\"dc%<O9Su^sOl)/-[yUbFf'D>6QZ7pB?/05KNv/J_;c>`AFP.%8ue=st4l)q:3f//UNY7#?x-aZP^[b6i/D=K/5'FVD<c/A_t;pfQ.qv96t`d'0JK\"/u=%SUfYFesNl)/^:<xZKDdfw5iYA;[NQ?pb'9/F#\"OZby\\_v^%uc//pZu[;e65_B/.^ArY:/h#;>PD8`T<xJis-=NtYU.Z[a?9'5OqAbPQ%Yivp3ZAc5N/F;6U_cB4).a^?h`u/ls#i=Nb/v\"=4Vtd6;yO/t5Bq[Sl3VpwU)ds%_Kxq\"ugh'bf^?6cD-rA<pT)w=gKve/Qf[/7tyg`auYl%F0d>#T_3",
				"O@E<9*P3FXQ+4!RS5,%LG[AM-&=YO.\">PH<BI\\C#Q?E]FR'*=+ON,&>XP$T6-QJ8(.L3!R\"Z*DG@HYO%9U+1MI[PVE%,W\\-.AQ:]SFT[*BU^@;?=9@+V,NR#C)>8Z-O0GLHD.WIS\\E^*$MPT4+]PN,(9KR]'!AL\"C2-0MF@?:15VVG)#N<%LH[C%XWS6.T3IM$]!D(\"%)E@*A@#^BESC+&,OP!N;=^%-V4.Q!@*'F>HL@\"N<9D2RIAS6+(ENQ#?P8F_)0S5-T3G]_L>HI<Q>;<.!Z:E?8A\"D(YD=9<*FU4+MRJ:),NOW4-L.#M+GB>^P9]+^,Q:BRW*$(H0.;16W",
				"ABCDEFGHIJKLMNOPQRSTUVWXYZabGcBdWeACDfgJhiQOEjkLlmURnoKpYgqPFrdQsCthSuZLvrwxyzDEOKs01RboJP2HW3dFcUC4aS5AEpqKDPWeygu0dopfhesMNBsBR6uEIbF0QgJKoZOPSdtxocUCT2umhilLXexlAYEsdtPJdNBFzRsmILCj7ExCI0jKvgSP5eCtO5QAdsMhVC1elSFxuECswUbDJRsLgcIpWGehv5Lw4DaobeKJWTRnkCgLVw86CGdAYINxVkBxCyKSf0OFC7eCrsUEIDQorhZErPpbSdHwsBxA1B5CLRNjFKeQOKSPZgzdJ1pUtac7ABFr",
				">Jd*';KP!>n(pA4E<'=A^0O`'K@d\"_C[$I+r3Y>fd_my8Ao$gu%xY*dPJ>n:6K?&Fez'KgR\\Ar427'\"9HAIcO\"AmeY>_@+'[C`V-0n('8y3`xgdzc!4o0<?uy;dPJ_C[Af%>n:FKt'\"mAR\\'z4`9V+2K!<Kg('apA7'Y^Ad@>_AQ'n=Afu'8R%eYFA9V>g:W4$72yI*\"CorZd@\\x(K8?f+'HA`uEc2@CrKVd_9\\0R6%'n>&>AdZKJP='7AHy8Y>np02c&d^'xg-e<!f+AK%et';>?JA-6dP!='I4mA[#`?xQ'\">ZK<JYCuFdtAWyOz#gc['H'^0$_A#436K!PR7W",
				"R$[+4^G?DD?&-',78(-5#/=\\aK>Y)9FO#*L$IEBLS`H>T&(\"44K1SU>^HJR[XE+7]::ZN=L'A?#W,NY98T\\J<H5#FIES1-B/>4aSU2OY9?X>*8(`RJ&-T:)N\\K/4ID^ZSG[A`Oa=W*[41:JU#>7WA$;UL:=*L^N1AFDE-X(O\\K>'G?YIaXS-W+H85)B*\"F:NA*/,_WWR1V9OGTGEB>[Y;\\\"^(G&a`a9EU51&-TRI7_8EU;D\\:Z(IF2+'*$XX)?(D,TLAK\"WBS5#\"81$GXD&[DKIT1E&TNaA\\F`;>F/\\*'Y-9/?:J)T&aH+BK,/=5R0U^_7?HJLDU#>1*NF+[$\\IW",
				"ABCDEFGHIJKLMNOPQRSTUVWRXYZabZcdJeMfLgFLLhiDjKkFhlmnPUopUWKJqSrTdJMsFaActXuKvIaFsKHVwKlAHKLfbxpdDZwpshmHGERLtAUFXYKMgyvJZz0nFDK1IaOCnFyGhUjKQq2BFediZfc3TgXxKRbFCpgreJLA4IhP5aKUGTaluhRdO6RS7MNcZHUxFagEKHbmnFdXYedjKah1KHwMa8LzqSV4HKqDQFAGdkfUXoKaFIHKDuaFKWxRJE2Fyhc5GhUYKLm1FHgOXfaqBecMcKZiDjFLKEF9aKLDCFdleUupPeKg!bAnCFt4BI8OS36eBnLETzqcrXEh",
				"5-WKdiKZVR@\"h&>Y1$HIA0)LegD4'3WfQ7)_?N8h[@Z!eH(M-0&<.!/GDQXCdc(@57U8_ib[@V.<gLcd3*NCK_Fi&/Q5AZ4V\\]'a85gR`[6LD.LW()&#D-880<?dXe7U_\"(>0IW/fhH@&K'cMC.^-AQL7bi/1*,)6?48e#hVH5\"$XK;0[?N<a23?AZ$W>!@)`LL4fLXfg@M^)20Q7\\hWDdiL(80H[*(!'@_.Lg?CdKZD\"i8)/([gP_8.h3Hf`-<L830c#Abe[7VH@diM_ag!]L543-0ZN8I&.[D7?hVY\"<d_@/I$;cQiLURC;a7-XV8KeP-g[fA./T@LI4Q,)(<1",
				"-Lw$De;aG)5#=JAw\"@S<ojs.,K#$@(lTLgN@tY(O/RgaiP-zt0Fpu[0Gg^y/#C)D,Yna(<qb=W$A5e05PvTo<@`g)\"j[8lP4TY,`RwXC<];*oiLoaF,I`XZ(#w^[*bG7uO#/z84)Dy-k@tg#$)U4SR<7jeCA-5*0tJNn8Y\",ajev\"P`^tX]kp)5G4*KGgZuXIRN)sW7Tq]YiO-U7vJlao&t)L7e(4C*gP-t[v;#YiOgkWkyl<sN<wX7(j-Fq0j/,S=K*IRUb]bKXoau[Np)D@^*K#G8=;Xa5<S\"qq$eCAZR`)NJ.(Lvjok,A*7(RtYqlwTzgbKn[`w-NKy/GY(&a",
				"ABCDEFGHIJKLMNOPQRSDGTLUAVGWXKYZaHbBEbcIdeJfgANShiLJFOjGkHUbblmASKJbGLQnoYpqETrsbBRXIObtuvwGbjQENfLScWThxdNGeCBguKOULybJDIYJzAQ0HBAJSEoHJlOiIYQETbOGXJASmcsdDY1ZHbKEjLCbnSDXbrGcIv2OrkYNSAJKbRC3LTpG4SUfNE5QWgbYTjH6LX7ENbDOUG3XABSLlMTGFIqFzKdeDAQhuGjFYyaNCos8BExHISFLG9X2JwAnRjbSbbWzYTE7bQGgOBJYcSPZLvbd3GlDCUjUH6bbJ5IQGjcVLNf4E!kYTSdZUGJPBILj",
				"=`Y>[Ta1Tj3ZC@RykoqB7yRLu7-F?(ONo5n:dx+VSK7%IgaU4PW0tX!kFBApu;H3Ed(E:wuj=74+v7w#TSdC777?u7PV.z%qeUgSOb`RZSa7`A>dF7][73nLiwKyGvV#5HT=[pXeB%gUk(Hx?]7HFbBVg-ak7IO;d`1w%:AEj+gqo.Su7PeCFVLVdi@0p3dUc+Kt50yN=7cbXKFv4:a]xTEkG>%NHz71`u51Bj(-d!TX77`VRk?T5b7IcCOoAw77v%#:(XO+ZTFSEL0P@kP>g7BNi%WpHYun-.%kdjIbCzd:?7wgt;qdS#PO5GFLzceudPoTwik[ykUXn>VEp%Bv",
				"9ZTqcf#8LdLeL^SSYW%e5WEUBL)e)EMR#(MVPGRq/eVMN/lUrRBSJ+/YU_96WlV@^H9kk(YeZKcF%7\\NEXRk#@^/^G/E+#%D+AKJ6FR+L9UYU__8ZzV)HQHLNqM#IFBTdcfGTZ=AcdtGZBk6%YY+A%t(GBZ/IDJP79kAq@P%OPZPKPY9dOqHO9_9#IPIcDq#EKrRTPR%^ABMP9pRpBlI\\At@=#S9Vr5DO+I@O)pUNKpLqBzIMRVj)X86F%Q!XtW^U!8_W=WqqqHfYq=_N=pJRTI6qEGXVRDYBpkSkkUY5\\!HB8Wl5e%e)rA5tr+N8MT5eq)VOXSl99%\\t(5UQz8E",
				"ABCDEFGHGIJKLMNOPQRSTTUVWXRFRYLOZabGOcTRVdQTefETAghiTjkJKlHSmbZUTnchGjlAFEYLoDCKFpLVHYqkSWrUTZCEbGKQlRhstdukTobcjUVQqNOrvHlJwNxlVyFXkdluHZXlYWLJz0QFisxofhbXpGrTqXmPxncZjYoYZE1Ak2KDxlc33hRbgcUL4Go5PqUc4EhRzWbjdNTXSkUWQIGOHzAOkrYVRGobcqiH4XnLksJZ2KgT1NWroAM1ej2WOYEOEHVNDqebWlZU4iNUvcxAdQKHZ6eYr7E2TB0VblMX4wNGJPeuXRfOWAI0bVLGc4qEU3JdHZnToXAh",
				"ABCDEFGHIJKLMNOPQRSTUVWXYZabAcdeGfDghijkKlCmIOnFaLAoRZeJGpqrYKstucTHNOdvFwUBfbASExLyXhVGzCSR0iYcWK1mPIqVFgx2B3laEBLzRoOkj4etvYA5UTfwc0hPiC2FpXGqMLmGoRuE6OmnNWTdj7Y5XIBl1yQzzU4koDSTfVjSJ0PrcqHhXpmNwudS4CBFWBk2gCPjLZRAGNo3a2SYzMcKNBFnIefTBdoDEw7OhWPrLuCoKklGtI845PQYqHUm8TefhDKe3DcNv1CltWdwubwaEDFIxMLXOs14RoA0MYTckXLUge2GEmL2RK1gO0Fpuf5c3yr0",
				"zL6ZQi1~Vf0'7?#KoE]{+BpYSwnE=^G\\lN&fIQ%G�gMk6@l$J1w\\G#)c=(2%=/0R97T=�D.?^K5vl+:ownr3|pNzYLGZ{]IMV~(2=Rc6:l/SC&1im0'^�.3gkG7!KzQr+p=J=$@?#ND]oCS=|nl�)9QvVM&RT6@f1\\%G$g.=i0r:=37w~=#Dz=\\%=�n]LKY{?w+2\\=ZpfN='V=$=lkoB=|G&RS/i=:lZ.M/%�rGQ#YIlZc('V/?k6~&E=391|^G)Tzf0w\\L=m7]{ovgIR=.r�iK@Yc+�9p%NB:TM'LlV{Sv&63I1k0=zcG.D7=]9lRGrQ$K?E:fo+!lnp#3w~@NV",
				"RMKR!YDAI]L*?S@,NW7'9X+WS2,Z8P0TMB=-4\\0GL/R[<^_V(66D-NEP1D:X/@&:F>7@TL!/+YG=)%,3-4*>1R([QKO;0(A78B9N5S)ODD(&2H'LJ8P//0NT)@H.46=O#,*5$ZUIY\\Q_YVRN!FM'*DW\\6@V2,.3I3\"/W?DBX1K#/IT(M$=)PXDN80W:]?DCX/6+_3BQ^AWL)%F!2ZN6ZLQN*5\\^P!B=<A:BR$@8R2SL0H(BX-W>F;\\L^6B*O$FC2*O=_/NQPS!LM$':@1-D)&2-X0\\7-+$*A6@,WUL-2/RL!Q2WK+0,W?8BXM)QM/XSML;WO4-5V&*YR3@/BFE\\]",
				"MT4F`RQCUG'hE5-_d0T+8<,3V1;?<cVVD1S><WAX\\H*&7iVO#C'1.`X?Oh$_M[5U&'G<96F1R*_]E)A<\"8Qc&\\I3#(6S;Y=hVVIDUK75'>Be.$]WMVFi;gVC?1-R_A&`[JUJG0\\*7'V9Y.V_&CMQEN8a+'\",d_#IHDFc3B/hVN?RA>(W/K\\0J<$7.MF+*41V9YX],JRiIY&HOKQ)gIV=#$<'`1<aA\\0G+J;76C1U4Y?VIT*eVd]E8N,D;_>VWhUi`)H&G<(V1KC=S.E-M8!'[_5\"/BFQ&6X#<;?0'6_ec$D]RgU-+S1&Q4,5>W*i'THKA`aG3hc<VVE[\"OdBC0+,",
				"ABCDEFGHIJKLMMNBJOPJQRSTUVWFXYZabcdeEfgCIhFijkLlbmJnDELopqYAGBdUErJIRTUWMsHtGsQquIKivEwxZNNyJiivNAzcTVeDUHkYUtY01aKaB2LFmoWPAEX3Ab4SfUIZ5RxxQhWTqmA2SjwoGUxWJr5F5DEnoh63PC7GQeOFP8MqJNuI5uBJohqSHMfCVyu1vUUqYGy78gxfjpoLAkMZvQOX567SNFRVscTwEuCYUB2WhrnGdKwJMhMiBxbIaDzPJ49tpH0v5rsT7E6KV8qjZofbUDHSMKIQGdc85hYTirENp6gbBJzIToGU8D9Hwde3vIuatFD0pOUM",
				"dIlD0;Si2<8pJ'[$cj.#PTP*yYElN9FWK73;>?9L:=&/xoOQd+0e8BG27AIHp`Zc#RKiFTUXJqD[Wj+xL,0PyN`><8E=`9omc?B`p$U`#vbUPWQP/xTw`UGFylAEUX;,0I=:K`mZ8L&z2OdBDRUASp#,Tj`[7cU+>'3o`iWy<EN,xQ`m&Uvzg?0;AU.2OG8Uw=3/Pq,cUXZWIdU,,'9RPe7iK,jFm`U>L&OB,Yp<xodzq3Q9#PzT$U,Fy70U+EH=b*SUwBUvXv.UP?8p3g/cNAUY#;`JbUZWTSxUUl2`DGy`Rz'*9I,EP3=[U,FB>iU9$0K&X.Lp`lOzYo:j<Fe?",
				"/6.(!H$0%5&Y73M)@2N]\\5?U.#DI>Z/<J-4#LT!I^Z96%H[NA2Y/@W_)3]4$&U.CM'6\\<N^HY3%@OUVQ,D^.W!JGC9]4'HY3I%VAZ@Z.HU^^>/;&#>@AJW!Y:6)#<L7/?ZUS^1.%>[N_V]@+W.HUO$IZ7^/(%C5_\\#\\>)5F!@AA)QJ)IU4Y#I_T6NW2L]]8SX]&M%'/-N]<%D=@TV(IZ%W4!3F.J5V?]^>FU@#[/5CA>UM2IU\\OLTB5DTE&>%@UM^%9\\N]!XI7HF<4/J]I5KVYOT)NFP=UUACG!S&]7^D\"]Z%@^MAR4^J[TNV%H-*@./?2W&0U\\](^AT4%I&!H@G",
				"N;+2.lczX)k;KRV6+p<T%CzcERB4-U.%LVN>Ff+|S)Rc(7ppzlk|.*^z)EOZVW*O^_K+5&fBLXK95fBVBz+(@Bp_56GH4HbH-ZWYjT+K|t#WpdT|+JJly|4p*F4SU>l(c)2GB^OtY.*|lOpMp>Ry(T|<BLB/+FZ)82B9+/2MMt*.^V.OdR_A^WZcJ5P8++B2FcBdU<5^d9K#-+k+1>E|J+#9+tDFG#5+lN3cD84T-W<YF+ONAR5P2G*zLFOOHLU<637yf+|RYjpPC+-FVNyBC#&dl8ycGqSO:24cFMDkM+(7SOM2K(+|/Fp<CbU2zD1c+R+1(CqzWMkbK;:p+GLz",
				"ABCDEFGHIJKLMNOPOQRDBSTUVWXBYZabBcdFefghibfTjGeeKkDNlmnoABIpqarCMlGRfUstWnKPuYYItnZKafVivwhtodAmLXSFJOpjsDCMhXPlBSDnNTxRILrVnmEUtnQsogkAkyGyjcJZFNaOYCgbeGhyzZwvfpeiRSMKUljVZY0wWFEmqpTkosuNVqLHgAzRR1GYuItXyyATJOaPgbQkhmcvujwGCMldUsVYJLISYopfZVtynhSqKgygXJzP1YecDQOhWwFkNuaAvOC0MibRfUlZTwLEdnFeNVKTDiHBj2aGpI1WFRbXsEzoUscItuZHfAeaPFcnvKQXyEHE",
				"OiL6Mua5A1=-d;oh[8:1z_1K0dMAU4&\\H;yIZ@kq>o7uh8zUl10dHGb']Ooue8Q/U1k3zSf!=^-d_rqHeu7s<n:Uag[foLYc510Z48;'-lHGt`z1doh8%MS1ku]qi=@&\\7KaQUg4Z:z<>H1^'S%1/dy1CoQ3u;Uc1=h80`Hlt9r1IZkizfuG&\\U;>d1Lq<'.]57/H1+uKaSy4%10r-koQ6AL!Ulg38b=GqH]nz<+_5suatK4%9:U7Hl&GZ0d1^'f`yMcr]iS@-k\\oa.84qn617Lu:10=>U11k@Q!9/zfd3o-8h5Hlz<^Z:q.'9doY1.u7`8A1K[fUGgH]tn+iu1c",
				"/NaAhDMBC*O[)Y07h!]\"_<h-.P,'g$*#@LaK64(!FKSWeJ)N1D/^I&0<XKNd+;$7)U\\e<4a?afKD=ah9C13#4h/h?G9a>,E%L+ghh[F0AOOM\"(dTHYR-O9hhh@2W.P2h$&!*a)h<h^0hDCfGaR1eXB_;6h,7$27g'YH>F(!1h4KE%#S/)UEA^?XHaTD=>C[3<aL2=Y$@J1*d*\"_#'KFfG]0[L-.,PhAgd+eIhh&04h++(WM=\"H6(aO7h>fh!\"^9?]=?S>hXG)e-9%hOR12ETA+.NhUP/&20;7<YBdK[@^IL\"e1/CFdJfhM_>-ID'C6S%>,@e<dg.]9=eBJP'h&$N",
				"aLAljhKkY(Tu+LUo1cM6BrZrn3^i_'\\&AyV0K_Y9:dOeN)WcS2QT\"lkp;19:.+(#YMAJB%+u-LRajpy:VaEI&o0b\\Ni3_gfW#e.O\"'2+b6zr%SaYIMegX%%T^B3;-%PKAV9i.:;_MdWksc)LO%Nhf+\"Pl3jb(1p%U0SQ#gBaYyn\\E.Wd;z%@seTfK%'(2)%ViXNQRrlP-%#y;%cPE&RIY1J:TkE_AQ0%dWO)6PN\"S(scU.9+&sroME%B_\\1p%3A\"Zh%nOXz%z-%6YIaiM#@K\\%f+hbynuY%eQ:%V^%g'2L.BsTZR&6WrM3Q%j_E0i%No&lP#h-OS^bQ)AnK9_#Jd",
				"*QFE8{<IC~8E9FQG>+#H[!}!Iy4\\D*I/&\\|3N4I:E=B@EA-D$$I::XJMVXB#\\F$I\\H$Z\\D!XMB=:I%AIVJK+Q4^*7-5W2J2DCA=\"EBIKGYABM|]0JK;I.&AY%FNYA=:I&9KK\\D$VB5GE-6A7>ECI\\KU;E\\XW\"DB<[CL$T|,NZH&EBVI[N^G{I>39@|35CYI\"#0+%U=Z1.D:u1>W/6YWB;EB/E%C\\E4!0A8TFB~\\E-EC=24\\IEA0=_KUZIB7NM4QVI{,1.*C@|6>$I>2FPQVHB;9H$IG$YE%6E;!33D#GVI;6EMVI*Z4U7L1\"$I^DEB=./F[S~>38SS",
				"778;+<98?6D1L\"HM@#%$'VZN+3V4SPIA[(3/Z>9S:QBFN\"0[FT4WG;77>4D2]5?)1XC#:>Y-./KIP!RR&\"'YZO-A!?7PW..BHQX*\"@2!QE[N#=8C$:J0!7>UK<99@OHA,R36E123\\#TB.S4!CEUTY-N4=89?1\"^L-I.@(!:R7N,\\-=%$WT+JY.\">2\"VS8?1_JZQHK';OH/0]9T3X26O9Y,-&.PVM$JWE/3X4UU5VV]6!G5)6,3]4@7%#Q$&FN=9>LWOX*6(!$?)V>9W+,34W\\#<4@7.*]5>9%!'GA\"B_R:#\\-O:]77>@HT14\\$_E[C_=$A+&,(!ZT#_F[]99@UP_",
				",CurN[|dISMT^yhvosdWSPFYwptc+gbfdBzOR`yA+0ZIG-VYa6+_C-UroLZD60Zfu4Jbsdq4{pikGFBJtTc:zS+:OS]mNjxlIJJWgu9vP[C,4Yy7UXr`hJsFclXfN@wtVRKTa8-NB_oDxZrZsUdliqXj4dIWn@JSdl9p,XT`kTU-UlFa[-zX@JhkYq+b@26gGRdfOyDpcA+Vi20ZBu,dE_oZbT^vjIGtT`C2ULZn*YW_PzufSU9rUo[sbZFTUWhw6BGRI_taD-qVrNisp+nmoWxFVjVtu9T`OE_,1n^oW[gYJrTy{CsHhaL]|cR_o-V+ZlDibTUdXjq-SgFZfzZl",
				"ABCDCEFGHIJKLMNOPQRSTSUIVWXYZaAbcKUNdefNgDhijeklmnKMBYoIcNpREYGXqrstuavdmXwlxjekIrylvxhePoZgQqyclrXlz0efegUUrgQum1DCl2whnys1LxrhPvfF3drcRpWCPdi3Ph0MEXQusb2yYOcOct1pxpMIX02nbUi3L4cQsbHVfo5ynwnB6RYQWYvEr7JZJqVQR8IiXJxeIPvbxeqmJx2u4z32hSrZa0u0o4CWDojynXrja2Q92nbgZo0fs1VvBcJR!C931BijpXWpYqJ5MRgv4cJMC!93uefnn7QW3S4wdofLLRFXfgsdsv3f22uSBcAoYeJo",
				"kuuo,er1F.73[g%tV^J^R`v_>,w!<\\OXpP@E3JTX3[g'N767wvEutGI-(vh.+JzPX<@QTL:OzoNi`-;7Z71`tTvULh3Ep,kXr\\VFQ@E!RNP3z+>T'Ie[Jl`.@yquTGhd%L+n#iu(woIig:Cle^3t\\EJz_OrNTP<7#-Jz^pXk/-@VY;7R>_1vN[P3:=PJzwgO\\!'Nh;J,eL.Enh3GN^XNT1@qh;t[;?3y+J`/1iU(VikoXCErRIi;7F!h>,LhevN_<N67nwOY'@GIe3EdT;J%(E1+uPO\\JT@go,73iN7v!e3ruhXv^RI:tPeOzp-#nTz/uU?kJzm_#l3,_yFOz>N[",
				"kuuo,er1F.73[g%tV^J^R`v_>,w!<\\OXpP@E3JTX3[g'N767wvEutGI-(vh.+JzPX<@QTL:OzoNi`-;7Z71`tTvULh3Ep,kXr\\VFQ@E!RNP3z+>T'Ie[Jl`.@yquTGhd%L+n#iu(woIig:Cle^3t\\EJz_OrNTP<7#-Jz^pXk/-@VY;7R>_1vN[P3:=PJzwgO\\!'Nh;J,eL.Enh3GN^XNT1@qh;t[;?3y+J`/1iU(VikoXCErRIi;7F!h>,LhevN_<N67nwOY'@GIe3EdT;J%(E1+uPO\\JT@go,73iN7v!e3ruhXv^RI:tPeOzp-#nTz/uU?kJzm_#l3,_yFOz>N[",
				"ABCDEDFGHIJKLMNLBOEPQRSTUAVCWXYZSabcSdFeIfghijcSklmnEEAGHCJKoEBpKqZrIdstucvwxMTyzOQ0SEPg1kAXV2UZ3fe4R5Kj6NSTnJCgqGYc0HBlhDFGHWvZrsbIdKKA7uXgMymOZ3fVetiCBGFkKP8LUwjEarITu7MJzOlsPxy21EKUVeAkK9NS6iYQDWhccoClbRKFwHsmjzrXyLnxVvcIeAfltqso2u5CoFBGzM6Oz3iJIdL0w1!AaPRkpnHBEbUCmq7jDFGHr4oIBGzuQNgMZ9TH07OWAoCBG5YVKE7P9hSg9J1ZUzjaRSXr9vSoEHBex9l3fGNo",
				"2)/%;+ISZ0\"?5^>E!K$-'1(@D=LQ+38.*H4C,MAV%YRU]<[!J:\")OT#N8$>(W;=:'J\"_-H?2E$0<I:QSL@+M7Z\\RGY[$V&]B-&T^U3[:(_'.FP@!45/8A2=&JCMEV<$N'H:,\"WQ3<;&$:RY\\-R$@BI8X]1?MHV,:T$)=S>K-9LDZ:UR_!8+'^#P=7JU3)&'$G9X:?N>/H4#27TO0L$KA1%-'9CB&(RDPGUE![,?;=8:<X4SQZHY1&P$O^L.]J0M[_':C6><$N-\"TR2ME\\K(P:@QN$M3V!4Y-XA&7,@'U8[G%N*D<(MHO:;D!).0&Q_1J#2/!L&XE\\.$&>C,8V:$I",
				"XH'2MIA2)Z3P?K[#4\\R8/V5W32U\"<9F&N-L!QP#4?X87IB\"IM9D%2>HZ/Z=&5X$[KXLA3QN-I\"XM.\\DR52+,<)JGIK%LP4/?WQ#=4F'MFVZ[\"78'\\89HUAR2G59!3?+7$J<#=K,'LPN)W[U.=F&34$A\\<8>!GQJ$&B5RM'>WU2VIG!BDA.%<-NJSVPK96?HDB4S)<,W8#V%[7&[L'9:Q>H-6+U=!MB5X/\\GX2K\"D7&LIV\\Q<MBP4)AV,%H.N8SV)\"9>A3R#\"M2S:-5NG%:7+%DK6=\\?J&FR-6LLI[9>$QWM'<#UP#23AF$/NS4!TJ/-G=KZN86L',W?&UZ::<S.!",
				"j_CN<!J4e7ilK0=B4ah0Vl8C7Yk7igO4f:D`^=^NeAbKl8F7AlX:3d00V_AahZQ2m5dQ<mH1JTmml7Wol1ChOehZQD6d@QI3`KF<8ZcB76@nWbF!TTZn=P=7aebYIgmZJFdYea3Y86HRfRDGKMV41VEIF2pJfKL6:H`hAiC0O7Zo7QAnD65_MQh<QBIaRT0FdO^6Na=:HnDf>I@k7Mc=4NGlmmW9YHLTiS!J:fFIPED0jfKDCIYciKRDfi;o39C09<Qd<`N^pZm|hUWKFe4IG@N2B3N2kUNG^g3<NMcXDi_L07Zo>hEgNWcIEPm^_04n2:f|hEE0hWJ|!e4pFdgT",
				"HEER>pl^VPk|1LTG2dNd+B(#O>%DWY.<*Kf)|Ny<|1L:ckMk%()EGUZzJ(SP7N8K<Wf3y_9.8RctBzjk5k^BGy(F_S|)*>H<lY2V3f)D+cK|87Oy:Zp1N&BPf4/EyUS-T_7CqtEJ%RZtL9;&pd|GY)N8#.lcyKWkqzN8d*<HXzf2@jk+O#^(c1K|9bKN8%L.YD:cSjN>p_P)CS|Ucd<cy^f/SjG1jA|47NBX^tFJ2tHR<;)l+ZtjkVDSO>_Sp(c#WcMkC%.@:fUZp|)-yjNTJ)^7EK.YNyfLR>k|tck(Dp|lES<(d+Z9GKp.8*zqCy8XEFAHN86#q&|>#4V.8Oc1",
				"16AO-1=+Y<T=&P>HT(yVG<=@vlZ>^T6Z1{,\\H<%F^%<G>0<O/w}J.O@Y?S5DGJ\\;BB44FO=FY<G_&&ZI]{lo\\}OTf>hMJ--BD&+JHw'HVZ,I,n1}~ZCYVKQnO'YT?H'(M%f>4ARSh1IQIwZw^.}J=S,?yNh*-QKSP+%%F5\\GQvP~\"Dw45BkOhJIRH_B6B<h?f'}~0(M1,+AZK@_.VG\"6]Ek<5E-C<O\"YNQv0RPnZ}f*K0A{yvY6A%\"~]yRC\"A,E0I@,5,^N.f@j+<,RN5S_4VBM=_M\",6HGl''O)n<yv_R-,kBVDw+TB>H?%FO\\y,w6VEDYT4EkC5KD0I~f\"QWJB",
				"U9X%P*J$NK>)H*R!1>7F@WY%^8HNa@M0[,C8I$RPDN-6N9H#W\\1:EZN2U7Nd(h=K@g>`8H]fabTY^$RRNIA3*J_P3,M$BRc%[-?WKEN\"N$PN>RidJ'Yg(DAB\\!=P/B?,`>&NZ[9(B$I%\"bF*gNJN%)-@/'0(R1KLRW>N#Y*$\\)AREB/S\"6Z72N?WN^,N$U[-,RW\\b9NN1&\"8$F8AR#(',@%W8%YJZ!^XNP^7C3?cc1N*\"NZ-^9:N'!g#b)QfHF/\\9U\"aTKY$@d>RBHZ3W%HC6LN[_^-KWa,B(@,BNgb$*8)/IDEC\"Kd9A>SU\"HRQ`(,IP>>@618WJ^Nb!&N$gFR6",
				"ABCDEFGHEEDCIEJKLJMNOPQIRSHNTUVWXYZaObSANAMcUdaYbGefghiTjEDklmnopqrhaHIWgsVMtuvbBVYeQBwiedVHLWSsVUZxOKupFayzErqHGr0Um1rvsjmTCFan2gLdKNVp3AhpZpfSSVvZFinjmDt2gLQGMtB45BgHxniB6ptMsSBRGPfShaAsgIFcOU7NkXvc0bU2gD8tHFPfgESVpRBscP40KSHNHSyWsipf8HIHzAjmDoMRGuKwIuinIXAlytP4lir1EeMWgp3PfTzMEmnk1mHoBdvQBw9qxB3yjxDj1HogKaL6ThOK77ZBqPipxYeXLRYK7j1T89cs",
				"Jl'<GB1sODU?(gq2t^i<,<5y@<p[VbwI34vM]/;<h`h&bSTE>\\(O0IL+.^#<NyrPCRr=DZJl'8u,Ut?G1<4vi<@QV`]c[N>;SLs5bm.\\hwM<RChE&<(2lubbQ#=Dtb1J'B?Gg+viU\\;rqC4I].^5<@Z>w<8[<hc`LRTPr<<Eh=Dl#<J?MUsv'trq\\4];C&^GB.3hi<5hu>LlS2Vr=1JU?w4(yEI]@bb+#'yGh)r`>i/[Lr<MbQr=8v5OJ\\w<&VQSUuT4r1bb,]RDp@B[>PCtrL;m(=c8MIsJT.&UoRP0E4ughbDtp#h1]`>@3hLl'/?r<<SGh;O[b(Ti2=M<J5hU",
				"I&9`3'O7:GqPy:ErGVU;a`Y.x`[)$]=Deu*QCeG-kWkQ]yjv1*D:+W<_G@YFTsm2iGmLGw4&J'QT<GP9\\>I*3;Qllyq7?TuGDCrv]RG*kUO`Gik=x-D_&)]]6v1GG]?1YXPJe7PJL*Gmoi4W<GV9F\\RI3>'Q;k_WqGj2m`-UkuG&=FCP?Cr*vGmE&LuGiO@UXGekY>Jkxq1&y_$mL)4<*9IWs3yq\\]]rU=.vkNmyuY:QCm;?]6mL'PJ:1i9-Ol$Wu\\jLm?]]T<GG[x')42PGm<GwDI7X\\W_qjGQutG2+3C?ek]GG+Jk?4yCO:kC*Ueim`-D=kG:x]Wjvr1xFLYku",
				"fTQH&Hb08h4zo12o-PDx@.k\\,s%;ZW<cI@YikRjwf\\+p\"&!IRJtm=HQyub4*lhU^z.cMsRwY&i2K:1Z'DPqLkWx+.=;\\%t&+<\\wvm=*,\"pIZL4jc.-2!mT0J<HfT8Wp+M'YQRz*bG&\\c1'tP+2Z'%YKh-ys=zx?o,:M4@&;w;W1GPR[xwJ,\"JtL=zM'%j4*C<kK:pqHZ2i!#fwY.zQ:uJY&D1\\'om\"%<ibwhZwtLJlYMRs^;Pj#fUT[xK,Dp:4Q=o.Km^bqML4v.08't&htmG&Hs-y1^l;uU[P@2cx+CWTLG,\\j#fT0R<%*QGMepIcC4.+&D1qmIZPe2klb8-w\"C",
				"CV@pIpAuvik+9jTZn0_5bLdB]wH6ea^SgbGC4wc7PIfaDx1Ep@uvhlAUYi2e+vxZg8MN0xnd_3bJIc5Fwx+e91<W2wy[aZ3G5c1V6f2uzpBuK7e0xvhiCozcdxIe83]xO@uvh+rkyZD^0A4wdoIEn+<[yx5wZ]4vim9bPM6_pL7gflBx1hCNTx0ndxj2Ozg@<A3x5Il>FBXC+@lAunyZn8PGvk1M2YB^zDW3ux0CQ5odp@uIXlAun+_9cZemHU1ozBlCuE6[i@o0m7acmF2ednI^3bJ+m8alAu]NmzKV9BY6lG7Ox8jcm_9uZ^4vh6fLT5ozg7Pem1f4yT@f+ZDK",
				"I4X91iZg;`GJ=b=7[2j2fKG8OPEUH6B:dXFXLfXmaJJ0QdWATXPkKZUHkl@ZHnCK4[TJA0Qnnd[gnOSK6CM\\7[U0VL32G415Y4Y9[7KT[J=|dM9;`O5=kM8bK4_CfDT=m5G5IkY4VDFcWZB`TEFcXb67fKTJ9IQ25iVJ0|diZQ]hDLQ;A^ToMkK9[]XQgHX4THmJ]D1a4_K8J;10V_3Ua45onZG=5d^IVgW5iAhBACdWL;LD]GZQAa42GcPY[URpH:;AF[72dWBD2|D765i8KAO^K1cECdM6JeH|nI4JPD1jLbO0V^KN^kWeU0XVB:gdK0VLDgR=|fK92jN7kYAU",
				",X;,(:\"?T%*YZR9:&B1J7G=)UK3)@\"O2A\\C;>,%BN-(26FZWEKP[4N)T&WJOG<A!Y@+8(T=Y58X=\\Z:-*<%W>7K6P13C;R!N@UFME?+381MA*,O9FGU[2(BP7\\)9C[&N;?@J+6Y5HB?6=G5TT$>1E7J@Y*RRW!:P\\CA<\"Z,KM2K(5:\\GU=X3+N<M14R>[H6E);.W*7%TN!><Z<KUCN\\4+!17M@F(5EC!3X18A?EB,\";&5>-\"M;RMX84->&9:,CXTUZ8K-BF[2A(W<J\\67:THEP4?HY24)=XJ9JUZ*-7F\\A,R.&(B9K2KO43F.&G@85H.T..[OW:PB!%69Z\\Y>R*X",
				"9!q(/ZP!,eVpb)2%FDu\"4axX\"qceXX2mE6NoxSLcP5H#ktoxS^J1SZq==eVpBP=lpMH/LVdNQo#kkDX^h)4ax5/mMSe5cJ)H#y*vaSp)k#xyMVeYM32oa=Fd#(q!3>#H\"dJPVppq0DXY)dJ/H#XcdJke!=PSpt?%Dj/S4\"Pd9X\"0)Vh/c^tk*I1SpQdc9VpO2ECj#4Zy2no]L^JMpqk-*NDu\"5d%aCc#nP^e>*I1dBJ)S9vL/q]P!=htjQu2kVeSbMCal96D1VvMFGcN\"LIa7)(q,3/lBP-!ht4#YQmOX=10Dye]9FGS2^pL7\"O#xHOVMY)u/6aE5tO2xBq,3*jO",
				"Q5A#-2.O=E)@V%L8#7)P,$GUB3\\R@W(5YY49D3SO5)CMG\\:K[:.X7YBP<%-@V\"I'NU2IB\\=>(L\"Q0'OSAO>+.M9,<EAX\\'34RXXQKA;#7GI$NK58+:D*RUCYR7G%E=SP'@[@U\".)Y>#I<C>NM4QRO\"=W0V$8A9GCR[4P@#C(4-5>+27)E5[KRM<B=CB'QV*0;,$@[L93I+.ENW-+2,P8X@=W\"RJG5'-\\)KL;(Q*SO2:0A>MVD%Q$,.R=PYC@\\S(U:4W<S#@%7;NN#.Q\"IB8%+;[P9;LR-U723,GK@*W>NMQUB\\A$58R%XC-=>5*9('W.I>,AQ7YDEM0\"LO-)$G[0",
				"ABCDEFGHIJKLMNOPQRSTUCRVWXYSFKZTabIcMOdBANefghijaklKmnLoQpSDjbUHdjqrstuVvKsXQwaxCalydIzcGD0FhjbMfOwRVNGiC1KmxiQ2BThZP3hLSNypwduqmuh4LRbV05mszNYZCjfXXuc3EiaKylKtvFINO6S1bOIo7djeKoyxon8KpDVLdoUsKIEiktaLwnoHFMA0bS8WoS9sismESzWRNtGMOlhVwkTCBzSXtgr2YGKcILHnN18AqCQP5Q9HS9YxkM9ezdfIN3jXH4D0nKUbDOXwCuWSt8qcFQkmMwsCvDyNOxPbdjHim9K3Q1VUwyroU7SXcCz4",
				"XF.V!O1NGE4;TPZ4FKFJE:JDU@&I$,=-T:P!@HY5%JB>VCU5+N'$XT5I5;[QV3,G4>E:DWO@YB'[!.%$ZT1PQX)D+9W,&M\\;U=)!G#VP4!S3O[1CY-%H&SUNI:ZV>X'CZ2G2E<VIOQ=DMY->4B%+!ATAJ>3KF'[)#TX,DS9;UABVO!9H9JX<2$1Y%XVJB.:=DM5+K2YC)MQ#5<[.S)-\\11@4E$:AVOKTJ2&YWN@G.MIU)FBZ=F[!CAK!&>H-C;&ET.#'!$9U$<N.P%H3XCRQZ4,39SV+>W5[:GIJ'WQZP[E1OS&ZK,:41\\<PT!BH>F#<YCAHD;,@==E335FSNJK#",
				"ABCDEDFGHIJKLMNOBPQRSTUVWXYAZabcdefgUhCiFVcjklgdmnopqDIGHXJrsABtKucvChwxygz01EZ2QMS3UaPcTmFVY4Rc5Zi6pqrWkNdauJIc3GbgTHBnjDXGHVzclwfAhKrC7vZcy2oEc5aYix0FBGImKM8LP1RqeWXVl7vJQynwEk24phrMYiAmK9NU01bSDZjgg!CnfurFkHwoPQRa2O30YzgIiXVnxTws4WqA!CBGQl1vQ5kJFhLp0utIey3m6THB2fEXop7MDAGHPtsCBGQRSNcWc9ZHu7laF!IBGqbYKX7v9jdc9J3cyQEeTUVM9zdsAHBi19n5ZGN!",
				"XY(#3dC&%fGSl>up*'i#47<2!bn\"Dm0+WvV:c%KbFZPIra9es$Z>Ol_.@'(#42,JYf1j*6/S35\"UsKViC7)$<b!gD+X&\"4G@avp(r6KYF0\"#fSPe:7Z.$Imrg(c*KrC_35ViW&$<vYf,uSjZ/K'0b!6se#d\"7Fpl)@991b#ePXfV(7)$:GpY3f,uSvc*VI'i5K%F<70FC_jS+.D1/Cs)Se/a2iZX!mr&(32iPR,lG<W\"v1b:rg,cdV0>_$e#IDDljCJ/1!mrU/@Kn\"5\"c9Y@,sf6a)&5:+pXJ*IGzK9n(vC>Pm@KO3F!ca_\"WPjS0>V17bZ3Pf%:rlJi./I#s<F)",
				">J&9*+%F8$WT13+!#4*J{;.RTX*5XC;E1&<9=5^<9*;=UC]HM[,6[M3=DE}.6K#3]CQDL]X-HK-_Q*8B524T-Z&<4(7)&MKADKV<K-@KMG4@#IW0:N+=L32PJ1.Z;@6G#8I;&=5^67*-4.;KT$9!ML46C;M3V!P+H-Z#7@93SK#PBP,W:*Q*1Y,}O%]L;,;X:@A#&<GZI6ABW4V3OVU17!DZJT7IQKCJH<%$G,P\\WVF<{*{=8^G#A*=AH{S3|5+'%O!MN':OQAV$UECFZOR5FCO(5J8&\"{Q4BGSNP_Z_RTW..NVACR<@UM{C+M*N4\\PMEWZ#8_!,<[]4,(WI;XS9",
				"24-\\D7K\\TLX*;*F$\\\"C>77C@=\"'NS0>>G\\''V-08*5S,/H8Y7&VE=Q5RV8W$G.%B.U0\"05L3$%7\"=AFYT2W>;#>[L8-GZ5#8Q\"=NF;Y5==\\XUV0C$%.QB%B\\4\"TKX'E>4MRF?&ZAT9D93REZ\"&&H.LN*J&UNZ8K4#L,QJ,GF0,-%ULWU,B92.&AMG2KM4T2GSKJJ[#ZFJ3'Y.Z0H\"WS?=;;E50MVHHR@2[=L#\"-M\\F/[>CX-MW'JSQVD1ACH4=U[K./D%EXB9R/??-B/RR?ENDQVYSD[M9R;/-A.%E;UW,NU*$@@73&N7#C'3AZ@@73/3CNVHG[A@*2*Q,X25J-H",
				"8OA68Z@FW,,XTIT&5C>UYU97S\\-%5$&8%>V#[N/\"/AFJ!.2R!O:$SE1'V6X\"S@AP<2/N,3?4>89TOUSN.ZWC87-.B8AH'<#2?,65\\EOL\"B[CY.P!KZR<XV%W[JK>'3S79?\\6\"VO6E'P.A:IZ#W\\UN!R@F2-,7'$%(X>69&?N8\\34/N3CBFP:OV2\"[<#Y5!RKVX&2%\\-YFE/USP,>.HS4'X5(9U>CK#8<Z&H>26,CPY5Y\"$&W77%-1@<ET5P!R2Y/AXJVZ@.:>&O19KIW%L7?E-F24J[H#J,$P9Z:JV'2C%5IC4@<A5\"F4H(VJ%-[4:!VNB/5K!RWXU\\>46H@AJS>",
				"ABCDEDFGHIJKLMNLBOPQRSTUVAWCXYZaTbcdTeFfIghijkdTlmnopDAGHCJKqFBrKsatIeuvEdwxyMUzPOR0TXQh1lAYW2Va3gf4SpKk5NTUoJChsGZd0HBmiDFGHXwatucIeKKA6EYhMznOa3gWfvjCBGFlKQ7LVxkpbtIUE6MJPOmuQyz21eKVWfAlK8NT5jZRDXiddqCmcSKFxHunkPtYzLoyWwdIfAgmvsuq2EpCqFBGPM5OP3jJIeL0x19AbQSlroHBzcVCns6kDFGHt4qIBGPERNhMa8UH06OXAqCBGpZWKF6Q8iTh8J1aVPkbSTYt8wTqIHBfy8m3gGNq",
				"ABCDEDFGHIJKLMNLBOPQRSTUVAWCXYZaTbcdTeFfIghijkdTlmnopDAGHCJKqFBrKsatIeuvEdwxyMUzPOR0TXQh1lAYW2Va3gf4SpKk5NTUoJChsGZd0HBmiDFGHXwatucIeKKA6EYhMznOa3gWfvjCBGFlKQ7LVxkpbtIUE6MJPOmuQyz21eKVWfAlK8NT5jZRDXiddqCmcSKFxHunkPtYzLoyWwdIfAgmvsuq2EpCqFBGPM5OP3jJIeL0x19AbQSlroHBzcVCns6kDFGHt4qIBGPERNhMa8UH06OXAqCBGpZWKF6Q8iTh8J1aVPkbSTYt8wTqIHBfy8m3gGNq",
				"ABCDEDFGHIJKLMNLBOPQRRSTUAVCWXYZSabcSdFeIfghijcSklmnoDARRCJKpFBRRqZrIdstEcuvwMTxPOyzSWQg0kAXV1UZ2fe34oKjRRSTnJCgqGYczHBlhDFGHWuZrsbIdKKA5EXgMxmOZ2fRRtiCBGFkKQ6LUvjoarITE5MJPOlsQwx10dKUVeAkK7NS8iYRRWhccpClbRRFvHsmjPrXxLnwVucIeAfltqsp1EoCpFBGPM8OP2iJIRRzv09AaQ4k!nHBxbUCmq5jDFGHr3pIBGPERRgMZ7TRR5OWApCBGoYVKF5Q7hSg7J0ZUPja4SXr7uSpIHBew7l2fGNp",
				"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdeUfghiFjdklDEmHPQnAoNhIMjCpKeqgLTBWGfJiFZnSkrLsACmQtXjqVdYnpDMlReuBvWGbkOmQUrIJgEXPYZThDicvBAGEWfkHFsRKJutmOorMXVYbegKPsBQiaVNdqoGWRkTZwmSfovDJZAElacNQMuFIeWLPdkrRfbgmrXSiCaAROcKucMtYHQqBNTuFegdItxfDVGJhEUFOUiIbXKVqWHlSrYAMbakLhsmKlBpejhRtodgclQfhruvPilFVnOUsAvIbKGTwDhOLMNSJRleELdqgfaXWYPVtFvdNCBfkbLmKQGTiIcA",
				"HER>p>l^EVPk|1L|^TG2dN+B(H#RO%DW+dY.+<l2V*WKf)y+:cMUZ>Hz^RPkJlE22SW7V<82p.3_91B#2Tdt+O2WN:H%jM(WLtc5UZk)2j+7SPtWN^KyUz^83>Rz^OL27#Yl2kkVFp%212MTWDtc8Y_Hz^R:k22|(2)Zd7l#VBp21PGTjc2f8MS<k(#jH:k&K+_93d>O2.yJRcYtkl2E8M)G7%#|N_jD.VcHt8YS)JMp225l1VJHz^GT92GKfP2<|U_S4ld(*:5NEEjY)VM2F7>HE2p4JRz^G22LWTW&BztF2OHJH^EPKcklF2&3+W&<UWTGpdN+%7&K+JHEz8f2",
				"#&@+02)59,<3DTJF*]%=O0&7.144?P,.F$-K5/L-,NK@/A55E::!8<GBABTT*AMLLJF2G;Z=HK^/7Q1*OT)HI6;CM8/<P2E/3H+0UK)NU<42MVD=OIMCLK?>NDMQ,9OF[JXB:5,AI-\"PXP=CBWVE@+->R%AW(*\\864L:N9RTU+U??[IS@$GVCT-R=]+C,4@J&>O)8?HBK&+V@AS/J3;L+T>Q<BA3:WAM7OD5#.P#XB!V<J1\\(?F=82LF6)N0FZR%H6'4M5EG>,PB0,\"QY2U;=KLQW:R-ET@Z;R=NGS8)-,310._7I^G2Z<0.=4>HK]1OY+99%14<@QC+?H)E6T3*",
				":S?Y*4B1#AG)R=/ZWMU$,3'L+)9NG66@-FG=1K>VQ-&YXMVUC!*4#/BZ\\D+3K*':JNR8,T6.5?I4,W!)Y9>JQCAOLD1=8WO#/-6U$Q[3@*\"&:T1[\\?AFUR-!\"$SVJ9S4#OI.C/B[35DZ*ML649TQ,.'8N)\\,=?F\"@-A&L+:W[T#\"BCY[I1F?RUQM-\"$/@D'93NKC[&!*YD>M:4RUA+Z'IJK9)$>18N#C@\\Y=Z5.+M&W!/LJ'3K\"G)O5:N8R\\TY1>?MDB*A4$@V=+#W!&:Z/QUBOS3FJI8R*.L1T49?GVGWC$SK)#S/F3'*@!&=S:5B[OB4\\QJ8RV\"$>Z#NO@L+DU",
				"/#SO9.BG1TH<'6<HF96AWV#GM-%Z07RB@J)[/@'\"-R\\WN3*M&)U/9N,<BPZ7\\0V:.*K*'VW&XH>-Q\"UU\"59DFTMB1*[\"@T6Q3=OA-0:#GA0SW>F;.\",+/P+P-AU;;=XOHDRZ3[)K)K6!#;WF/J&N+VQ+!Z=A9%QD:K0OT\\@H%'.7T06<5/MP&3*.UB,K=FU,K1JZSX+G:5>6DT\"&)'!-:6X>N#VWR/G+*O@D9H!3A5J%=;W\\KD=>,'FPRQ)N#:WQ=#/X+&<<U6VMZ;A0G-[0)S7@TP<.1%3[1FRM[>K',&BUN:*Z%7G5\\+F0T-.J7/=AW92*BV&U,OPZ!S[S3XH@",
				"6>7<*=8?@9C0K+GL>QGOKC3.P[0[/.%_4L3.YD8W9UBIO+/ZEX4D!][^^[\"W:[^MXE_O9DV5-.JPQ*RW%+&!Y/P_C![.2ZQ_$/0ND\"0*OHZR!C7B\"?I/#@>1%][^^/$_P2Y[!WX[[,TB-V5*AHUWX3P7>^[\"VQ<&O+P#LQ[0[.+[,C$-WX*OV4!D1+W2[#X>$3-*%K]/$RS;8V5\"09O@W3,%-TXL#,0%T[1[/2YC0:ZP![(9+6\\7>?$,Q-%IOC8#&D+EN[LQF!MC^^1Y+64#[,=7?@-)\\8>?$OK%_P_?.^E9Q/[;[^?FGX17[-^HZB^C\"A*JQLC5.O?$3:[^^0,@",
				"ABCDEFGHIJKLMNBOLPQIRSTJUAVWXYTZMaDbcdeMfCFghijbTkYlbmJnKOGoQFpcUScVqaAlmkrsOWGghZdEtTkbGDrguLHKNvrMCWwlJiXYAQUAnFslPGflKtQdOjkmZfTbqqhQEkZofHDvJICBUCupmSIBDEhNARPJAlYjurTlwwpWcpUPgesmVatxKQnXmMbOGWPjdSFwMLoskRCHBEptVHUSaqSgJSEXZiAAIYfmdJnMMwWsPgZtMNhqJLrOutlYFGovnuSkKRAfvNHQJwFijUVCaRXjWAMmOSaclsoigKhftBdKeQgTYePXeLeTZvJLwIierNqksNuhVOnL",
				"9QT3H+.$X4,FCK2P5ITJD\\OH</L@W+R-!\"V7P&DH+36NG2E':XM/QI8.0&\"30KEYY,F)Y>,$F88PNJ46%:93D\"R.\\D!ELC72'T@1WA+V4LWYC1A$K)<XG5IF\\O&5PA6OH<Q?9!7>NQ3.9J8R1-,%'TOE:).PXL6EI/0\"2E4VRC,FGQ-W@0$<OJV&!+:\\\"O26>9)!NK15'>7A1Y4V:%ELG@AHPID<CJKI,!%%\"FY2C'%.?\\KL+G?@<9TN/&W3?$Q/)C8X2F5'A.P74:6\\)O7<D9W$?TV!HX1\",W\"$NPA6/J2'O:IRVQPIH0693+X7>,1F@?4,--N/T&.K8)J8G:EF",
				"K*10S8$QE:4JC>V4*VK@Q&M-)D#7Z/\"T?J!PW@Y-A\"0<I2H.H.KZ[.MC:,7$.*FK0K@;D(12\"U!P*:4[RO5*I*JE>O1S@TC8T7:/$(Z<VYC\"MR4HQ,&7Y)<T24FS8O.NHU;GI!![(QPF;ON&?[I/@$54!K@)[CTJ7@A:DE>R5VJ/;DPNYIE?>1ZV#(W7@E$>:2?,AF#(U.\"M0S[Y@G-Z\"82.RMD5#P#1?CVU-YA$G)O<TSY%2.JT&Z<RID/G:M7(U\"W0:SHA$KV)Q-W;-2.,@M8ZGC7G5PF#?Y#[;\"WENAI/R0S>1D4,NQPTM7($JU><C!H!;21I!G;[8@N5F>.R",
				"ABCDEFGFHIJKLMNOPQJRSTUPVAFWXYZaJbcdeOWfLghbiaANjFkPjlQHWmnmoIKHOTdCMpqRmYhAoZJrFLFsQKCRCMdrRtVCNQhurpDgAHRRIpQuOvCwpxdvoTCqbCZMFmvmQXchKDQLSPamnZrRHPrihPLHZiMLygbzazWxbYIRsLSwyQdgyIAO0aALDtjJBgWgBSCVobxbizjGXyGbd1DHKHOrftuW1YWgJNymm0kvvTtYzbSNiZvskrXHMH0kfXkONKiMXjFit1lLIAICwpfcFossEvMCMFiONTtRRHIbGszPQzRGsyP0NltSOeaavgBZWelSSHOWBufHrmMivvsZieRdLQacdRTvB",
				"9%P/Z/UB%kOR=pX=BWV+eGYF69HP@K!qYeMJY^UIk7qTtNQYD5)S(/9#BPORAU%fRlqEk^LMZJdr\\pFHVWe8Y@+qGD9KI)6qX85zS(RNtIYElO8qGBTQS#BLd/P#B@XqEHMU^RRkcZKqpI)Wq!85LMr9#BPDR+j=6\\N(eEUHkFZcpOVWI5+tL)l^R6HI9DR_TYr\\de/@XJQAP5M8RUt%L)NVEKH=GrI!Jk598LMlNA)Z(PzUpkA9#BVW\\+VTtOP^=SrlfUe67DzG%%IMNk)ScE/9%%ZfAP#BVpeXqWq_F#8c+@9A9B%OT5RUc+_dYq_^SqWVZeGYKE_TYA9%#Lt_",
				"2!*gi5-$.&\"#RQ6jd4y7`VX\\:zsmk@,WX0>?X\"29*1WD=NqX\"EMt\"g-3B&\"#FzlH#<Wum\"Sfi?6I=Q\\Je4`VXk7Wt\"2@s>:W,19_<\"#NIDX\\V\"*Wt!6q<$.E,5-d3kDWuSM&\"##zKi@WQJf4W61s9>=mBl2\"#7ZR:IN\"0u*E-\\iOQ\"y4SJ7=sMV\"#:9E&\"#',XI=D`gk6?q[zSft#mI!J>Neu@sj<=9,?2E*1SMVJFfi\"-H&Qz[m$.y4I7eD=\"2\"RtI<_*0:V\"Htd3s>N-M<Ku5&Bli_Fz!$yQ`6W4WP\\.VO7km[2d3\",9#*K:'DXWP\"tWNeu0<X@i'6XF-BlE=P",
				"#$3MWKV69!KR\"\"A7ER91@@?%7AN+=?%OGDY0&X(;>,W6D#:HKA<MF\"5B.EYM2G[N!@H$OS);>19<2)0&7\"%-$Q0CW83-F1QV.SU%:7G8A'?H!'DY)E5O0X8&B>(W=;\"Y!F$M5#<N[VM9.UQC7S-;6C2QFD8X!KQE1U.3%$N78:YAO=>0#,!Q-G&KO6NCWC%S@(=EH,>[3,1G#D!3VK(9B56N:<20;G=Y@8R()5A=H-VF#1,SKOX0.&C3?(6W<2:A[D$$X)'YEGUH-0B;1S&OFR?R<>C'@[W'DEYN032:9'A5X8)X&V%2G-?8C,(W=)3;6!$:'.#DYX10.R[A9W(H",
				"!Y('RVWH%M=.!+@Y,\"7QO?,D41JC(F%$L\\#'W.H9(>9GMV5*E%NF>#+:FH@[!#?D0<&,S2)=$JY*R8Q%)O\"5'[WH4NHM7L&<.1?!93R:@QD8ECJ7V[(G=GEYN\"'$FJ01\\:<*7V4[,&LQ8)3$@=\"N.S?+RJ7>%[E20HY*VC525+D%1<W&:4LQ.,ML2!S?D@+OYQ9\\>5'W,4GL)8J973%1M$O\\R\"#/2:[=>@P.C!Y?'NWVD754<18>L.G/FHM*E(?6S0GD<3!2#)F&RE+$4G=*CL\"QR(<)4$J:YCN6,O\\/503>S.'6W@%*G?&O8,D=NRO+6\\47M&L.V[!/Y6)V/'1/",
				"8189L,#D\\?NLI#O$JK3T*R+BW(H)YE[2.;DJBW=6XV7U9C\\T6C'?'!UI!O)(*-\"2,C>)WJG.YT$'<X91\\286@LU-8:P?7K;EV*+RI\\!,B3%CO1J-[H(@>=E+DLTWBKCGE8I:V\";.%<K+2R)GH@N6[X$7$YV:BOG,*1'=\"LUH3.W<9XD6;IK1%\"=>\\3$7,!2JVO*[\\E.CXT-?7(G;OP>@P.+LNW,#):J'TF%KUH-I6-3R8DX@D,!>7E=:).B'OHYU2WV3\"XON-719#R?##'+8!;[%#>L,<Y(9)?=.\"6P(J@EIT*#\\YB9C+:D7U13!$-X(?ON2N*HRK,D;J\\TP.<2Y",
				"G)$[P?E0%Q8C#7,46!&5HI;\"WGS$>@UFLR-+;8EAQ^2'Y.NL8/*V8[G:)$8\\ZE09C]F=Q8<(P+,BT7\"J3!HI;>52V8G@S-WFU^A_]8\\.Y'L\"I8$2V%,N]6:/U?E)0>'F=<*Q8C\\GXP@27J(!F,^SA-B$%6E8C5O#WT.8R=Q/G\"PK78&!<J5YS*I8\\WA/$8CDU;BT'H[>,+N1E<(V\\QY:J-.3=@S4]BAU+G/$^<*IJZ(P8E9Q7G1$)0&!T53'Y8E8#VB]_QRWI89V%6S-.G*]X=?$:)P_ZE0%&7H,2!FM\"6IK5>Q1G:)8UAC$XWD'L2M8VF.3=R];@PD,LZE0%/TM",
				"721QJB&0RT9>XF5.\"S(=J%@$*7,43L#=:V<&0G'@BX26$U[Z,#9HNRS>*-KJUP%#:1FV\\C.'754B=+#6\"TK3<QXC(>\"$>+I,TL&Z\\R192.W\"%H66-G:0[FC@*<+VZ2PXNBQ)J59'>(LGHK-[\\1.P$S*'74@UN13G@[QN%<\"(&RISLT:[ZVKH-9N7C&2K$5,9<90F0KR9.#-I0+PFT54\\CX+.=%J[3R0WI)WJ<S#FT7Z;Q'5946(G3L$0&HBJR-P\"I,F>*)QUW2:51XK4@,VU$#3N*G%0(JW<@[WCBL:.\\HX1'KS\"#VB-P\\QF)5Z94=32:0(.$F+,%)7L<5N6VH-&",
				"oNY*Pw)Ah5(F@5IZ\\cSTk`ulh2?\\<b,4a_&<\"mf*HMJvSD(C9frberBL;c0@VWf84?U=-j.sbL]v$r<\")Z,FK5DluI]skb_AppoPfT\\2W*vCM,Jw=9S`e2sK-=LIFaZ8kVL\"NC)rrv]@TSW.$\"HoU?;lYmP\\2D8jJw<@&_BfU9Wv)0u8M\"AeY\\<Lsr5FN;J(vZ$J-`I?2-lKK,f45H=\"M*Kp0uDT.04&K9UC8:k(=S*L\"\\h;,KWp&aS_2ka$jW0b0IwC,$NkTc&mDso.u5=`]mB;f$*)ePrjUHBeCFF,c8-w\"QVcJoWs@ZJf&<.90;Tl(h$ACL_B\"9J\\S5rVD*`I",
				"ABCDEFGHIJKLMNOPQRSTUVEWXYLQZabcdefghijklPmnopHPFKqXrOstVcQRBZuvwhixOyz01Q23teJPYzGNMkUxbjpmal2xQAB4DEonTIFWLMHC5tVfv6cXxhg0rsuPqwPNO7BAySf5zVKPU1TDAQ64bcPreJCQ0GWZY8kSuiNjmaHXdU4EDtZKpAlLMghPonFEDPJ1YLAPkwGOy5aQlTnDBCAMo1WIFHELbVPQJQY2v6cixhgzrsumefKQwSON7GXQyt8kd5ZPlRM60UV7REgzbcdPQPrhvujNimwPy4C506S9BpXZaoFOfgsiUPJnYLDPkQlT1At4DMoGWIF!",
				"YacUyF'pSwCE-kV\"cmo\"1Z8IyJNb='nMs_4eu*LqFeze\\!:S6E0kJ8&\"3e.V>*AS;28m*,SuMv\"Fm1/8YeW*e_+SQ5rwzsnIq@cUyJne,W\"u8)4!O\\epn*v3a'Vc-SFeMY_=Enzm2^8J*0emSu!3:8*mS58FVwyL*n>'eMkncJZmS._ze!+81;eRuN*AsnSI3@8rQyB*'oc4FqVeJSyueMO8pEF\"J*u6kFBC-1U'm\\\"=2s_4vzaSncmme.!:8Y>y,'J3e&LEuo^cmyFn*VeM+kwn@'eIcn_1JByqWeZS;'zeQvc5euo8yp!O\\s-*)'3S=cFNy4LYa'UcJw\",WeuV",
				"HER>pl^VPk|1LTG2dN+BB+(#OP%DWY+.<*Kf)y:RDKfHEc*pMUZzkJSRc:O783Z|_E9K.St9)jU5N:>(pFH&Pc54/-CF^N%G:LflRq(Lj)KtZ_.;j;qX)@DlkVbd9czAlF+ScCB+Sc86A>L4p345OBU#ET+NT|9BdtXUN8>P71t9|2*.J_G@_8CMRG7y1H/pKD(-H7T#N4T_^8DOOjj^LN3dzB<O>OC;%(:6EC>(WMCL|U9KNTB3btZk5SkA4t8SG9U&lcJ*cXqV/+T1XzPBMG#2:2H@R>d|Sq(-^.p+f;qE+p8y*4JMJ8kCtDOHX&j_b)Kj4BMddT76G^U#<J5Y",
				"[)H'S&%.7$9RGC=XAN)#KJ4UZ*,F@P:\"+7C1JSDQYN./[$ZQ5AF*'GQ.QR6T&WP,9XK@U<DJYA/%+O[*=15C:'(U6M<P72HR\"Z(S,O&CT$3WD%5GY:[K73F.A@='9&X+WI,I1#DY[/\"U2'ZT9F&6SL$LGX=4)/%(NKDPU3MR:.AY[+M1M#'4IJ5&DYN#FH*\"U2Q64I[S(2TZQN%O3(:H56@9$J*LW'#G4I7&<.@,O2A\"()F=Z)%K+LN17XS:$R\"GKH#/+JMZ*4.OCD1)YS>TW9P=M3[5X<Q6:,AN/<TWC%$5'37=#U@96H4PGKF+X)N#&1LSURCJ\"Z$W=Q)3.4NM",
				"[)H'S&%.7$9RGC=XAN)#KJ4UZ*,F@P:\"+7C1JSDQYN./[$ZQ5AF*'GQ.QR6T&WP,9XK@U<DJYA/%+O[*=15C:'(U6M<P72HR\"Z(S,O&CT$3WD%5GY:[K73F.A@='9&X+WI,I1#DY[/\"U2'ZT9F&6SL$LGX=4)/%(NKDPU3MR:.AY[+M1M#'4IJ5&DYN#FH*\"U2Q64I[S(2TZQN%O3(:H56@9$J*LW'#G4I7&<.@,O2A\"()F=Z)%K+LN17XS:$R\"GKH#/+JMZ*4.OCD1)YS>TW9P=M3[5X<Q6:,AN/<TWC%$5'37=#U@96H4PGKF+X)N#&1LSURCJ\"Z$W=Q)3.4NM",
				",/\"\\=(YXHC?9ZG-OB'AIDX5>4N6U!@&8TTE)9H%='12Z(-ELYM\"CIP*<D7,/4GVH.PA?!&BU1D\\(2OKTIX:<@,56M.8'4:PG&9ZE<IL-Q75/>\"OMA!.)CB'34@V?X,9N\\=GN=6:*/%YM)ZA8N$!>U2%1L-POI(@V<7\"Y,\\TC>.K56)2I7VGA!XP<:9%DYZ@-?BK2\\7$&'6=O$8*2\"4.U)X>9V,H/\\%KGCZ&B-675)H':4AO1NXTVH9L*5\\!:TMDY/8$@\"Z5,&G-U>L1PADEOC!=B8@U?2,6IG%<HN=$'.3L\"X41A9KCZ(!D@3,Y-PGA:*7L23)BK3EL&3VK8H*U1",
				"bfWKU2:8hDz<>=H>aC0YIT&[ujgsG[vQr5m_Axb#WGX4,]e&Jk'NRK:SfDzFnj8@<6QUsx?;=_Hi,C[goYI/rGuXTJb[#m]QvGkZNRFUi4A[6zWX/hHeTaS?v2:f8G4Q=g'Dx<Fj*C[XY#;uQHGk?m,shabJ<]t>Ui=R5CWg:[=^Ux0]?#Ui#'6J<Cg?jJ<LHri,4I2G4_enb?;6<ji8#;]ou[g>T,kHeD#sGg'/gn;=Rj@sCsnshho=iYoHix:R>T,/@:IuNz@/Shk;C:;6^]KjS8UZnWSa0U5vQUQLGaT^C[DnDa8z4?F:*=LHAQLR/XY0]5T&G=wH&nj8a?iL",
				"CJPB-)\"9WY:DRZ0#;!EC7119'\\P+2>(/VIABQ[8K0L'3CIK?,:-&G4>M)0Y+R?HO\"DZ(,8J+\\'PQG/BN<=&1W[I781CYRSMJ-:H2S#VC3IE<WO@:#4)AZ7(2@!B\\=NSYR;[QK;VA8>G0&/I<HZ1O'(\"\\=3W@M4?2)#C7AE<P/O-V@C>8I3WN:EA0[D@J',H>BQ4A\\+0Y['H)#G:?7\"\\,MN>P+<8VRS2ZJQ30?&=-\"'O4[():>W27G0V&SP9@,B'Y3MJR+-K4)Z#(!H><EO=B7QNGEY;SDI8R\"AZ;(P?;V\\M9K9W[B#2OY!S@D&3;-4J)R0ZHI\\,27S'(\"+VAK3QG",
				"ABCDEDFGHIJKLMNLBOPQRSTUVAWCXYZaTbcdTeFfIghijkdTlmnopqAGHCJKrsBtuvawIexyzd012MU34OR5T67h8lAYW9Va!g#$S%Kk&NTUoJChvGZd5HBmiDFGHX0(wxcI)KKA*zY+M,nOa!gW#-jCBGFlK7.LV/k:bwIUz*M;<Omx72398=KVW#AlK>NT@jZRDXid[rCmcSKF1H\\nk<wY3Lo2W0dI#Agm-vxr9z%C]^BG<M@O<!jJIeL518_Ab7Sl`oHB{cVCnv*kDFG|w$rIB}<zRNhMa~�H5*OXArCBG%ZWK�*7>iTh>J8aV<kbSTYw>0Tr�HB#2>m!gGN�",
				"KN5%J?1.!$#730'#N[NBS=<V@\\!M=P:@*+0$=J?E6B.C&$:E19#\\%SE9E7I;?UP!C;3+V)K+%.YIS46=U*XP#%GV1O)0LQ57R:QJ!>KP;J/U?IX3&@?$L/R99+U?;KC3UF!FS[?.%C:VG6R#YM&1SA*A<#'BN#XQBS?0V/O7@A.?K*O$OBK[F\\I%6&&>M5+RVGE1[F?JQG;BE<X4/GR5XI=C3\\+A??>$BFLK)9=!4Q9:QN9U:NX3SABJ!Y3R37!$S5>#*\\O@\\[M4P&J'%*DYUY0'O/6XC)EI+L.<;)C'PI$1&/!UBP=;X4>033.*YNBB?SAJV7P+@R*'UEN/9[B>",
				"0159>?FJM23P41RN<=CU:\\JO]7JGHa@80VcS^AdIW9>2Q93EJXB_]:6PZM[CNbTFefKRFS7Jh`Y^icDE1K;a_j785Fd?Sfi=U4Vdb9@L>HA]^gWKBkjk[JC6ch_OWDXMAQY]Ii:EY\\d1QeJUNZ6LKj@RO[`^21M?;f@V;WABg3caX>4lC`T<YNd[<YD9\\E=5RLUb>1=23`1l5Qa_fHSZb[0em<jS?HGgF41\\f=Ig8]UP1X7ZJO^V_k@>Pa[8A`mSW9<X\\IYj?kAVGL:MN;[KUBVC6VdFneL=0mHWX@j[aDlE62f34Kk700OXZ8_MN?9MI\\:@SgeA;OdYKB>U[7jS",
				"?'u0mF*W<+CYPq3tfq8A/2e@ARjB>cIae\"[:ehkS?@Jz-yGelj[2`0uUf*7Y]+'(Y2aKRC4[w:D)9q>js;/2ecmJ2hB@S[qa3>4N2lYA-Iec2`kJ2,zG2W<jDF?fU@3ayj[u`YY*nKcJwS[;JI>4j[)+',R7Ym.Pq9AC/yBjkcKZwh8yS4m-j[2hY;S4?lY&ze))D\"0@3:Gbuj[2Y*9WS[msq@4t2-jI:+SR>4[2j][A`B(ky?bu<,8K)wsz97*7t2-2N+/;2C(2fUS[mR[2nq0B',AN]kW<8y/zaKJ!>f2Zwc?buU'hD4Y*n;&3ea!l2Jmsq\"2e@A&Ie]+,Wj)!",
				"016;<AFHM43Q14TM<=EW8\\LM^;KFKbE:0VcT_AdKV7>2Q;3ELWE^^95PZO\\BOaRFegLSGT;KhaV]ic?B1J9b_j::6Fd@Rfi>X4Ud`8?H<LE^_gXHBjkjZH?5ch_NYBUM?QU^Ki;?X\\d1PfIYO\\6LLk?TOZb_42NC8gCU;V@Ce2c`V>2lB`T>WMdZ>UB7ZA=5TLYa<4>21a4l6P`]fHS\\b\\0em>kR@JGgG21Zf<If;]YP3U;\\LN]V]kC<QbZ:DamRV7<W[IWj@jAYFH8OO8[IUEYB6YdFneJ>0mKXU?jZ`Al?63g31Kk;00OX[:^MMC;NH[7@Tee@:NdYKC=X\\9kT",
				"ABCDEFGHBIJKLMFNKOOLPJQRSTUVWXAYZabJKMcSLdeDFfUOLaScghTDRiIjkRlmFNnDODoGgpqEgDrZsgthNZIuEBuvNCwxgyqzcVdzndKY0sveT1wewSTx2GrXPjoMTD3lZsXxCxDnkkjQA4CsjE45Lg6Za7q8wNm9Ia8a!EWFRv6aklqbHmxkZUbDZwmtrKA8DS4AclQwYnonrNWgpTVhZRgKh!u0NXb4ENXEiIuYKuuLMTkq87j2IxeraqhWCbfXRbxWJWcnIQazS8wkQzkWoev!xueJcvoQ3WXdK732WwK1jE8FZJghQxzjveItJgsZoBJn7sSKYFR0radxZirLRAQzHk9tK7IEh",
				"g(ihIOUj?Z';Q1m\"AnLf=;9&NiJ*cL)Y>piA6<g-VE@/b5nJ?(ahY=;C%.fIW7'TGRL2c)D+UeZ4=bjQm\"5466_N9aK;:@IBRLEjm&.X>7JGC9AapO'N%21DT_cRb:.=IEeX;mLgC4(7-NVW+=9i\\'<@h;nULVZI=*/O;BfbLK)=\\;Tp&L=NGaA?e@W+RB.nm691a%Q'R;L2Dm:f=39*)\"K6AnV>gX_(W;LC/ahcf)IpY0Ni%5<IUZ=Qb\"-;R+LN=E70Jj>T@i;SI_\\LSg=Y.N@?c;<'EILD2AS7&m(NbSnf)0VC_A=h91T\\;.n:fL'bI)*aU.=;OGZ0QSc-0\"R0",
				"5*bD0<3F17+Bh*cabY0.UZfBS%,;-CQ+(1'JDMRTL\\?X]*9!E(%UTiQ\\?%;E,[ZD3Y7G-']Ua/ebA<cS7A0Q+R9VgJXiW)[.\\Zf0=!1hT&D&S?7OE*CF)+fBce).]GUW:&9SAIW\\B;X3TL0<?]iM(;ce9LQY7SICgEO+TIWUf@\\6?&IRBE%J,'-Fb@!5afD7]9[<TMiQ#Jcgh?e`!'XCGM(PS;IWUOgffD%5*1\\O\"YhZfBEiM!e\"AOX30R%-UiQ[1ge+(5#]bZMY;9<DFfi0Q[*T?5%7O\\bCGeP+W:0,.]=!-h9acES%aD7SFQ#TIWR1'YKCG7I-gM!U1i?F\\Y+X",
				",/\"\\=(YXHC?9ZG-OBI'AIDX5>4N6U!@&8TT*E)9H%='12Z(-ELYM\"BCIP*<D7,/4GVH.PA?Y!&BU1D\\(2OKTIX:<@Y,56M.8'4:PG&9ZE<I4L-Q75/>\"OMA!.)CB'534@V?X,9N\\=GN=6:*</%YM)ZA8N$!>U2%1L'-POI(@V<7\"Y,\\TC>.MK56)2I7VGA!XP<:9%&DYZ@-?BK2\\7$&'6=O/$8*2\"4.U)X>9V,H/\\O%KGCZ&B-675)H':4ADO1NXTVH9L*5\\!:TMD/Y/8$@\"Z5,&G-U>L1PXADEOC!=B8@U?2,6IGT%<HN=$'.3L\"X41A9K&CZ(!D@3,Y-PGA:*7L923)BK3EL&3VK8H*U16",
				"0157<?FHM23P41RN=>@U8ZIO]9JGK`A:0VcS^BdLW;<2Q73CHXD_]86P[M\\ENaTFefIRGS9JhbY^ic?@4K:`_j;75FdATgi=U1Vda8BL>HC]^eWIDkjkZJE6ch_OX?YM@QU]Ki9AV[d2PfLWN\\5HIjBROZb^34MC:gDX;YE?e1c`U<2l@aS=VNd[>WA7\\B<6TJXb=3>41`2l5Qa_fKRZb[0gm<kSCLGeF34\\f=Hg8]YP1U9ZIO^V_jD>Q`[:EamTW;<X\\JYk?j@UGK7MN8ZLVAWB6XdFneH=0mIYUCk[bDlE52f34Jj900OV\\:]MN?;OKZ7@RgeA8MdWLB>X[9kS",
				"HER>pl^VPpk|1LTG2dN+B(#OP%DWlHY.<*>dKf)yk(:NcO^GMUZ|WzJSRp78>K3Y.LB#T_D*^|+)<Uk%WVcy9(SfPK8OMtZpSL>PjVp5JNRP#Tf^.Bk#(NDpH|Z3*WOSVP17>^%29)_Htc.K+MFf&|ZJ5VRdyW1LBD2YNT*z%kyfK8).LTcMd%Z|7(#jNOJ9Hpl>:UYP^R.&NBDdykpj12zVL+PHWf1pt8(KOZ.TVP#N*LT>pFkf|7(^U)OcY%>PM4p#J42WZHPK+k|t(yL31WzOp^T&VPR%Ky_B>Sfp5|2kUHdLDY",
				"HER>p>l^EVPk|1L|^TG2dN+BH(R#O%DN2WYN.lV<D*Kf)Ny:cMU>HZ^RPkzlEJDSV.7pY83_1+(T29N#DdyHOtcBDL9:jMUkftNSJP9Dd^*)MZ^78>RZ^#LS(WlkkV5pO1cTD%9:7W3HZ^Ryk|BfU2Sl(V+p1PGTt:K7cJ.kB(tHykF*N3_82>#Y)zR:W9klE7cfGSO(|d3t%YV:H97WJfzcpjl1VzHZ^GT_G*KP.|M3J&l2B<yjdEEtWfVc5S>HEp&zRZ^GLDTDF+Z95#HzH^EP*:kl5F8NDF.MDTGp2dNOSF*NzHEZ7K",
				"071<=<2893EIK>LK:?O@QSWYA4\\5Z[MaXRcfWF6]0YbNgBfXG^dTH<1;72EJj38kIUaC4F_eDfLhi=Z`P>QVW[?bSG5Y\\c@aMZ]kTHJAgNX[UE6bV9LfS:;^M<078YNaB_d1FIJ2lCZbD`e=aL[\\]ch39:4GI>mK?i@HRA5^6YBlCEOD_`=g\\dTFJ>]^0GInMWhiNQ<ZLffj1_eUJ2g;`c?P@[\\KVh]Mf3^4Y_dS`jeAH5k6B0j178OCiDPNgE2FKThUk3R=VGkS9:\\c>4dTl?<5;7@kj689OAQLbBanZ:UlC[0j1;7HM]I2lDnNXbnEVa=P>RSWY?nLXj389^in",
				"RzG_YW8#7FZZ5j+4MyB6VGTSQA2%tq1K)LXWCJY8>_ZT8#RxA/z2KI31yb=M@l/cYk7zYZWT_GAj#N3B*2=EK1zSZQ4+5Mjb_kRVGQJY#3xZK+%MVCJTW89_)tcnB>pN9#yAW6VG@)SEq*2kRKX7y+=5_DRk+3VGl4)kLbjCQ1yYF#c3%VFZKTxA)yNM8J26VB1)I@SWE=>Rzk_YR+P5MZ#F7yDTjA/qM24X1V3)GC*YDbkW%y+ZRk+ZV6)G8QTJA7KBnzS@W4q2lWC>1WMR/zDYFc3ZTWf8W33)klAtLIP2FB_1zZS#6fPY+N=M@/z%yktE9l5V/Wq*ZRKCQ3_L8jb#=KTA+cWG6x2IBJ13>n)GY_jN#xZyGTRVFA",
				"6d#]8*=!W`1K0grn_YLXeOIp4=7#*Hs)v\\(WT2H/.?xuZi6d\\[8b1$cYTPv%K^!_X`/2sOS90-]r)*[ZiQ8e4WFxu17?lHdG_!$^KnNFXRL5I)ve`ZO:lzcs4#P7S9YA8/?.i^G21d6<*\\T$]uNP=KXQOWFrx_5[6g(vl!)4`7YAZ/^G\\zR<E28*PSLWTc1N:?(uI-Q_59!)ixzAQgH`eb62dAYsGFS\\vTx$Q=KuZ/6<-(iHXI<OnNAs4\\85[7%?Lp=z:$SR9RLNe!1K(E`_*P5=XWl0Lz2)O=ZSN#Y^]c4Fu<gpHdQ7I(?r5As$T!zHn8vx9L/Kl#6<=i.*`sb2",
				"ABCDEDFGHIJKLMNLBOPQRSTUVAWCXYZaTbcdTeFfIghijkdTlmnopqAGHCJKrsBtuvawIexyzd012MU34O56T78h9lAYW!Va#g$%S&Kk(NTUoJChvGZd6HBmiDFGHX0)wx*I+KKA,zY-M.nOa#/W$:jCBGFlK8;LV<k=bwI>z,M@[Omx823!\\]KVW$AlK^NT_jZ5DXid`r{m*SKF1H|nk[w}3Lo2W0dI$Agm:vxr!z&C~�B�[M_O[#jJIeL619�Ab8Sl�oHB�*VCnv,kDFG�w%rIB�[z5�hMa��H6,OXArCBG&ZWK�,8^iTh^J�aV[�bSTYw^0Tr�HB$2^m#gGN�",
				"4vMP]9[RK,st6^>6y;Y0ITAq@b\\_?q$UmLCfA(4NM?Wr1wHmc/OlsP[Sv,(o\"bREtZU!_cV8]f><*^q\\%;I7A?0WTs4qNC@U$?/+l(ow1rmqZcMW7K>HTySV$9[vR?rU!\\O,stobB]qW^N8;U>?/VC<_Ky4(t0j6@*wcL!M\\[q]B^sY;N/01VOl(o@\\N,ct`$A<*rIP?>fHub/8Zo_1SVCw%!q\\67<N$f4/M?VOT\\\"8]s[E,^bu_vRY;*0%r1(4c6l<Z+ML@7sETKyNCw[OlB!9,Sv]+\"bRKY^I>W;UpqyZB0?_u4Sv($/tMB@`rmWpc7Uw%!LTAq]`>m\"[RKV*p",
				"TU6JPJ(WIZ@:9%N9*C'0G3S5MQY_O[DK->\\?S=)LE#&7+2?-A8F1,JTUW6@^!(I\":<KVZ=X\\H?$.BP5Y;%GRSOC&3AQ[LF0KN#8\"1,^M.D-5<@_&R*7?3UWX$J)I*ONK2Y\\E=:^T/V[&HLFPKD#8X\\+6UW(A:%49C.0,>MZYQ52/V@'HL8PBXF1=^%YL_A:]7S.+$GJON??!)8\\<^E.IXFC;0[Y9RBLD?T86#X\\3Y!FM,(\"Z2Q!_*U'V.H;7+@)=91.<\"E>PRA\"3WIL\\%TF1/CJ6*U0\"!(WI'MG$&2K]5*</VOZ!QUW,N8:_/H]D-&]@RKP;%>3S[C]7-!)I*XB]",
				"3Gkvt\\V+<8YWmgO2.&oUwpn!,)easJ=%:>i@L<MvE\"lB^mq1fZ%g6\"FS$&kU;!*-G#I]85'WtNVw3.io,pYZn)aDsm>+@;LM%f2=J5$GE1Bv#WlkVU\"Si,^JDtF8.^a]o\\Zn:+G='WM*Oi3mY$&1p@5>k)NBvE2%L#q-IUptlf8Zo)FGV]SWn.*Oi'3MZ,&=\\$<E1vklaY>G\"+sIL@fFWt]m!o%'BJ^2n=!1E(*\"3kgVYIU,JD*>Nit:LZopasDmf@qFIB^Jw]#8eV\\,'-G.*3M5%YSNa\"+>q$@LP#-6nfB<l^8.e=EVFm],gl'W1:iI)v%kEM<aJ\"qt23@UYol>",
				"@7DWX'Q>5/?[=Fg;%,\\f!$Z6:#c\"I*OV/iLJ`KT.[CF+<-a]=ZJ_;WGPQZhI@09,V/c)8N5\\Y!4e$gA')?>$%TZP\"-`JIX@.:*F5)LJN+#f[9gW]Q<hCO($/=dS78!eSPA@D_'(:c+G?;5a7T6V%*JKYi0W4-,I]Dg\\9f/`PR@LFR>hQ!6\"ZXCi.53,OY=;KcN:#8A0<e>IJ_'[+GfV4?\"*STN%LAd`dg\\$R>-PWF]@Q.aJ!:Xh\"85Ne+#A'?Z<6/GaT(CORW=Q4-[JS]>Kg!J\"P7i0c\\h*;:X8Ve#'ZN?`+DT.Z,W<@%f-O9I5_(]YR7gdPK936D_[Gid@0YQ4L",
				"9V[J%:;*S#K&_/$T'_A(]#4$B)$%a0M$+USP<$`#W,Q5L1a#Y-$&E2$$F#G39$.X:*N/#$_+VW#6J#=;^C,T0ZD$^#HI`#R7K#>Y?-P8L^A.^*QEJ4#a#@`#U1<9#K^B+^,RFL5#a#=`#S2:;#J$C#>#3<$D#[#9$PG#6$Q'H7RO#8M#:^A-^B?/$#X.@(P$*Z=)]C;%IE[#+]F#^D,A&a#VG'``_B(4K<)5L0>?1$%$26YH&QZC9'IEND3FJ@#7^A-(=#R[/8K)$#:%4L0T]G.>#_*P^1$B2U[H&$$'IE]#+[F#^C,(?#;D)b#35YA%6J<9B&b#C'7K:G/b#$D($)8L%HI",
				"HER>>plE^VPk|1klLTG2dHNR+B(#GO%GDp^W#Y.<*GKf)yH:lRVPcEM#U^DZz%JS7|NLO8G3#THB_)d#1f9ytP<_GUMV8#TlY*y:lZJ>R:l+1UNjpPP^5zB|)L#fZjSH:lRKP3kd<OU^2z|VFL_f3.MDPdN_HKP&YGS7JO>+%Rfj8PpEZ)UBNkTS_%^fH8ZjM<c)z9|^cH:lFL73FY.VDkySMpOdK9TEE_j<^)5U>HE4c:lF1#L#&2:853+HcHEYfPp53JG#Dy#LFzOTGU&YGcHE:Z.",
				"ACMQXOhT[oNi\\jPSBkR*tEeFI+vxlo]BXYsUE0CkZoO|ATG3J\\4iKStMjbNpvx|2QXBr*R\\T/USfI5|4Gc\\/ziKSICq0hvTp|[A+Nx\\ymSEkRQtoPF|XL\\jAkYeKGZbO*tM/JyuEiSUvp|x0qo[\\TxSl|AkZhFOEtLsoi\\ACXBjS-UQ+|AEvIjgGUT3/YoV4NiABk\\Ijz*JvSUEtyn|\\0TKSqCD+QLRoibABv|Tp\\F*whCYuQixSZ4GXNEJvRokyq|BW\\/rSjt*6KTip|UCeQXs*FJMIqF\\[vGkTxSm/ivI24CfOJ=0G+A/jNHZItG-gc/RT1Ei|UoXLq\\vTKSA|4i\\5BOIjEvSGz-",
				"9%P/Z/UB%kOR=pX=BB#SQTBGq8OlEYItNRWV+eGYF69HP@K!qYekRR^UMHEqX@B#P/dLMJY^UIk7qTtNQYD5)#9rML58!qW)IpqKZcS(/9#BPORAU%fRlqEFkHUEe(N\\6=j+RDPBk^LMZJdr\\pFHVWe8YH6R^l)Lt+5IWVOpcZ@+qGD9KI)6qX85zS(AQJX@/ed\\rYT_RD9IpeXqWq_F#8c+@9A9BkIU=)MHEq6IW9rXEVNk)ScE/9%%ZfAP#BV6WQPBr+IJ8_qtTJ@Z^=SrlfUe67DzG%%IMcG%FQXROP5e6##de_zUpkA9#BVW\\+VTtOPq=!L7\\dDAXz9XBF!HrI!Jk598LMlNA)Z(P_tL#%9AYT_EKYGeZVP5M8RUt%L)NVEKH=GWqS^_qYd_+cUR5TO%",
				"041D,%(!9=:AN).-8\"1J@B,T3KA8G?MO'2$/X3FU\\-GL_R!-%:#3W.&HBO8\"4G]<CX3+.P6IV8Z;H2=-K6()NU@+MFRG?\\Z+8041D,_LJ9%TAN!1YHB$<^O3+X/IW&]-#C-).>40P1$Y6B:-@VJD08^1MO-W2=18I(TGK5U1]3)FG?!3'@1,DHG:R0\\AN/X-_L%,D-=VKA0-UC(.PY?8\\JLD410N_VT9%!,AMB-8=8KZ<^O3+X/6W&]G2$:8C1.)>(38PH5U'YG-\\\"N^I@B>\",/6MO'-8-WX<]F)3GC-P11YI^1*4R3G?_%.$/&3@-=LKAD-U8\\JV0H1DN_(T9%7",
				"Z@JTtq_8JI+rBPQW6VB#PAfZ%%9/EcS)kNMI%%GzD76eUflrS=^@+qGD9KI)6qX85zS(B9A9@+c8#F_qWqXepH6R^l)Lt+5IWVOpcZVEXr9WI6qEHM)=UIk#9rML58!qW)IpqKZcFkHUEe(N\\6=j+RDPBAQJX@/ed\\rYT_RD9IP5M8RUt%L)NVEKH=GWV+eGYF69HP@K!qYe%OT5RUc+_dYq_^SqWPOtTV+\\WVB#9AkpUzY8eWVHFp\\rdJZML^k)5DYQNtTq7kIU^YJMq=!L7\\dDAXz9XBF!HVZeGYKE_TYA9%#Lt_RNtIYElO8qGBTQS#BLd/P#B@XqEHMU^RRkEqlRf%UAROPB#9/(ScG%FQXROP5e6##de_P(Z)ANlML895kJ!IrB=Xp=ROk%BU/Z/P%9",
				"q%2@FMGH&WK5Qh/%+aWL4lc2JbH&#sTWT~XoB9zxQ0!H<6dlEqRFJ5oUYw4p6KUbam@7kmOsxnHAaedd4@QW9*2poTln%+NQXFcLbUEO/FX+~qH&%Q�5AOspDB>*W<K4eH9!^2CWcR^QX</Fqr2d6Ub#D=+7*U^n9z@WABp+X9S&!hU#<bQ�Ob&GDw7xa0n4ClE/MU+9~oqQMPWpp5TOoUYbAnwHDGTr^E/WlQJq%mN<#O�EzTG�dOxU0FwQ065bTk&Y*o2O+K4noxEYhUQp@ao>c4R+%9H7sxhQ^SMPEUKDl5Id<qW%9O4JJA#T@!<zk&O7/~21GH2zqoMH7#I�^FAGe*TX�hRcUrnEaxDlewa6W0X<zSW^P+KS25OT&Ms&L�EOJQrK%0>T5c%s�%2+FoHY",
				"VB#PAfZ%%9/EcS)kNMI%%GzD76eUflrS=^POtTV+\\WVB#9AkpUzP(Z)ANlML895kJ!IrG=HKEVN)L%tUR8M5PAQJX@/ed\\rYT_RD9IH6R^l)Lt+5IWVOpcZFkHUEe(N\\6=j+RDPB#9rML58!qW)IpqKZckRR^UMHEqX@B#P/dLB#SQTBGq8OlEYItNR(Sz58Xq6)IK9DGq+@Y8eWVHFp\\rdJZML^kEqlRf%UAROPB#9/(S)5DYQNtTq7kIU^YJMeYq!K@PH96FYGe+VWB=Xp=ROk%BU/Z/P%9kIU=)MHEIODYd86WQPBr+qJPAT_#cG%FQXR685X_+Fq=!L7\\dI_ezEc__tL#%9AWq69KUqWqS^_qY9t#XYRWB9A9@+crT#BG5qXJdFeTXE@e!ZOeVZ_HV%p",
				"TJBRQAHGHJUQZBZKHYDSCTQOWFUWBDLZZKKSPHXNECTMQZOALMRXWLBUTHZTXHDTTCKBLDIVSOSLBQORLBQGVCRPOSLSTVTAIZUMGZKNMHECSJMNXJXZEJWHQLJPOWWESFWFWXCOKJJAYCWLNDHHENKAXXUOJCVSPMJEJWSOVYOHAKCKACPNIJQEVRJEVCUYYVIOIFZLBWLFCRGHNNANZUSQPJCLGUWGZAHHTIWFLLODAGCMAXHSHJFRDIQFMNMQTQYXUHQUNADBKJOBDDDOZKOBFDIBMQGMWTQBJXYSEGZMPJHZXXDMYWFVOAEAMCLXGVQHEXSOECUBRHIDDTWJ",
				"9%P/Z/UB%kOR=pX=BWV+eGYF69HP@K!qYeMJY^UIk7qTrNQYD5)S(/9#BPORAU%fRlqEk^LMZJdrrpFHVWe8Y@+qGD9KI)6qX85zS(RNrIYElO8qGBTQS#BLd/P#B@XqEHMU^RRkcZKqpI)Wq!85LMr9#BPDR+j=6rN(eEUHkFZcpOVWI5+rL)l^R6HI9DR_TYrrde/@XJQAP5M8RUr%L)NVEKH=GrI!Jk598LMlNA)Z(PzUpkA9#BVWr+VTrOP^=SrlfUe67DzG%%IMNk)ScE/9%%ZfAP#BVpeXqWq_F#8c+@9A9B%OT5RUc+_dYq_^SqWVZeGYKE_TYA9%#Lr_H!FBX9zXADdr7L!=q_ed##6e5PORXQF%GcZ@JTrq_8JI+rBPQW6VEXr9WI6qEHM)=UIk",
				"9%P/Z/UB%kOR=pX=BWV+eGYF69HP@K!qYeMJY^UIk7qTtNQYD5)S(/9#BPORAU%fRlqEk^LMZJdr\\pFHVWe8Y@+qGD9KI)6qX85zS(RNtIYElO8qGBTQS#BLd/P#B@XqEHMU^RRkcZKqpI)Wq!85LMr9#BPDR+j=6\\N(eEUHkFZcpOVWI5+tL)l^R6HI9DR_TYr\\de/@XJQAP5M8RUt%L)NVEKH=GrI!Jk598LMlNA)Z(PzUpkA9#BVW\\+VTtOP^=SrlfUe67DzG%%IMNk)ScE/9%%ZfAP#BVpeXqWq_F#8c+@9A9B%OT5RUc+_dYq_^SqWVZeGYKE_TYA9%#Lt_H!FBX9zXADd\\7L!=q_ed##6e5PORXQF%GcZ@JTtq_8JI+rBPQW6VEXr9WI6qEHM)=UIk",
				"9%P/Z/UB%kOR=pX=BWV+eGYF69HP@K!qYeMJY^UIk7qTtNQYD5)S(/9#BPORAU%fRlqEk^LMZJdr\\pFHVWe8Y@+qGD9KI)6qX85fS(RNtIYElO8qGBTQS#BLd/P#B@XqEHMU^RRkcZKqpI)Wq!85LMr9#BPDR+j=6\\N(eEUHkFZcpOVWI5+tL)l^R6HI9DR_TYr\\de/@XJQAP5M8RUt%L)NVEKH=GrI!Jk598LMlNA)Z(PfUpkA9#BVW\\+VTtOP^=SrlfUe67DfG%%IMNk)ScE/9%%ZfAP#BVpeXqWq_F#8c+@9A9B%OT5RUc+_dYq_^SqWVZeGYKE_TYA9%#Lt_H!FBX9fXADd\\7L!=q_ed##6e5PORXQF%GcZ@JTtq_8JI+rBPQW6VEXr9WI6qEHM)=UIk",
				"9%P/Z/UB%kOR=pX=BWV+eGYF69HPFF!qYeMJY^UIkFqTtNQYD5)S(/9#BPORAU%fRlqEk^LMZJdr\\pFHVWe8YF+qGD9FI)6qX85zS(RNtIYElO8qGBTQS#BLd/P#BFXqEHMU^RRkcZFqpI)Wq!85LMr9#BPDR+j=6\\N(eEUHkFZcpOVWI5+tL)l^R6HI9DR_TYr\\de/FXJQAP5M8RUt%L)NVEFH=GrI!Jk598LMlNA)Z(PzUpkA9#BVW\\+VTtOP^=SrlfUe6FDzG%%IMNk)ScE/9%%ZfAP#BVpeXqWq_F#8c+F9A9B%OT5RUc+_dYq_^SqWVZeGYFE_TYA9%#Lt_H!FBX9zXADd\\FL!=q_ed##6e5PORXQF%GcZFJTtq_8JI+rBPQW6VEXr9WI6qEHM)=UIk",
				"9%P/Z/UB%kOR=pX=BWV+eGYF69HP@K!qYeMJY^UIk7qTtNJYD5)S(/9#BPORAU%fRlqEk^LMZJdr\\pFHVWe8Y@+qGD9KI)6qX85zS(RNtIYElO8qGBTJS#BLd/P#B@XqEHMU^RRkcZKqpI)Wq!85LMr9#BPDR+j=6\\N(eEUHkFZcpOVWI5+tL)l^R6HI9DR_TYr\\de/@XJJAP5M8RUt%L)NVEKH=GrI!Jk598LMlNA)Z(PzUpkA9#BVW\\+VTtOP^=SrlfUe67DzG%%IMNk)ScE/9%%ZfAP#BVpeXqWq_F#8c+@9A9B%OT5RUc+_dYq_^SqWVZeGYKE_TYA9%#Lt_H!FBX9zXADd\\7L!=q_ed##6e5PORXJF%GcZ@JTtq_8JI+rBPJW6VEXr9WI6qEHM)=UIk",
				"9%P/Z/UB%kOR=pT=BWV+eGYF69HP@KTqYeMJY^UIk7qTtNQYD5)S(/9#BPORAU%fRlqEk^LMZJTr\\pFHVWe8Y@+qGD9KI)6qT85zS(RNtIYElO8qGBTQS#BLT/P#B@TqEHMU^RRkcZKqpI)WqT85LMr9#BPDR+j=6\\N(eEUHkFZcpOVWI5+tL)l^R6HI9DR_TYr\\Te/@TJQAP5M8RUt%L)NVEKH=GrITJk598LMlNA)Z(PzUpkA9#BVW\\+VTtOP^=SrlfUe67DzG%%IMNk)ScE/9%%ZfAP#BVpeTqWq_F#8c+@9A9B%OT5RUc+_TYq_^SqWVZeGYKE_TYA9%#Lt_HTFBT9zTADT\\7LT=q_eT##6e5PORTQF%GcZ@JTtq_8JI+rBPQW6VETr9WI6qEHM)=UIk",
				"9%P/Z/UB%kOR=pX=BWV+eGYF69HP@K!qYeMJY^UIk7qTtNQYD5MS(/9#BPORAU%fRlqEk^LMZJdr\\pFHVWe8Y@+qGD9KIM6qX85zS(RNtIYElO8qGBTQS#BLd/P#B@XqEHMU^RRkcZKqpIMWq!85LMr9#BPDR+j=6\\N(eEUHkFZcpOVWI5+tLMl^R6HI9DR_TYr\\de/@XJQAP5M8RUt%LMNVEKH=GrI!Jk598LMlNAMZ(PzUpkA9#BVW\\+VTtOP^=SrlfUe67DzG%%IMNkMScE/9%%ZfAP#BVpeXqWq_F#8c+@9A9B%OT5RUc+_dYq_^SqWVZeGYKE_TYA9%#Lt_H!FBX9zXADd\\7L!=q_ed##6e5PORXQF%GcZ@JTtq_8JI+rBPQW6VEXr9WI6qEHMM=UIk",
				"9%P/Z/UB%kOR=pX=BWV+eGYF69HP@K!qYeMJY^UIk7qTtNQYD5)G(/9#BPORAU%fRGqEk^LMZJdr\\pFHVWeGY@+qGD9KI)6qXG5zG(RNtIYEGOGqGBTQG#BLd/P#B@XqEHMU^RRkcZKqpI)Wq!G5LMr9#BPDR+j=6\\N(eEUHkFZcpOVWI5+tL)G^R6HI9DR_TYr\\de/@XJQAP5MGRUt%L)NVEKH=GrI!Jk59GLMGNA)Z(PzUpkA9#BVW\\+VTtOP^=GrGfUe67DzG%%IMNk)GcE/9%%ZfAP#BVpeXqWq_F#Gc+@9A9B%OT5RUc+_dYq_^GqWVZeGYKE_TYA9%#Lt_H!FBX9zXADd\\7L!=q_ed##6e5PORXQF%GcZ@JTtq_GJI+rBPQW6VEXr9WI6qEHM)=UIk",
				"9%P/Z/UB%kOR=pX=BWV+eGYF69HP@K!qYeMJY^UHk7qTtNQYDH)S(/9#BPORAU%fRlqEk^HMZJdr\\pFHVWe8Y@+qGD9KH)6qX8HzS(RNtHYElO8qGBTQS#BHd/P#B@XqEHMU^RRkcZKqpH)Wq!8HHMr9#BPDR+j=6\\N(eEUHkFZcpOVWHH+tH)l^R6HH9DR_TYr\\de/@XJQAPHM8RUt%H)NVEKH=GrH!JkH98HMlNA)Z(PzUpkA9#BVW\\+VTtOP^=SrlfUe67DzG%%HMNk)ScE/9%%ZfAP#BVpeXqWq_F#8c+@9A9B%OTHRUc+_dYq_^SqWVZeGYKE_TYA9%#Ht_H!FBX9zXADd\\7H!=q_ed##6eHPORXQF%GcZ@JTtq_8JH+rBPQW6VEXr9WH6qEHM)=UHk",
				"ABCDEFGHIJKLMNOPQRSDGTLUAVGWXKYZaHJBESbIcdJefANSghLJDOFGiHUFLjkASKJDGLQlmYnoETpqCBRXIOYrstuGUFQENeLSbWTgvcNGdCBfsKOULwHJDIYSKAQxHBAJSEmHJjOhIYQETSOGXJHSkbqcDYyZHJKEFLCBlSDXJzGbIt0OpiYNSAJKcRC1GTnG2SUeNE3QWfOYTFH4LX5ENJDOUG1bABSLjMTGLIoHJKcdDAQgsGFUYwaNCmq6BEvHISKLG7c0JuAlRFLSDGWOYTE1LQGfOBJYbSPyLtKc1GjDCUFUH4LXJ3IQGUbVLNe2EuiOTScZUGJPBILF",
				"MZtp9F_XYrG@KPROqW^HJ!\\I)U8+j=6tkqND5Ar9LES(ZF@l_8VTYH_dY\\XAO=ptK!^G%=MPBT8d=)_Wj=%SUDMXA_!YrleIkc95P+F\\6B8LNHT_dYtVE%UZQ@_XYrpK8G_F)SB%e!(_kKITJ5M\\W+=ltL8A)9eM8r6H)NPOI\\dfYe5UX^LMEV!z_GDfH)ZeT(eBYFkdOIMp9^5tXzYelP!DqY@He%WSrB_KIl5+_TY\\L)68UFHMNVdf_qY@IEj=%8k(_XYt5)ZK98POq!rpzWLGUBV+f6Fe\\k=H9cNI)EeT^e%Y@PdDqYK5tZ8LSHp_XYrIMWFU@k(z9QJ+\\6O5A!tfKe)Ne/QTrR\\lqq8LPeGBS^z8=E%BUDRZtrd\\FqL/p@YtWHXk(e%Yf+_!YrO8q6q/",
				"9%P/Z/UB%kDR=pX=BWV+eGYF69HP@K!qYeMJYDUIk7qTtNQYD5)SD/9#BPDRAU%fRlqEkDLMZJdr\\pFHVWe8Y@+qGD9KI)6qX85zSDRNtIYElD8qGBTQS#BLd/P#B@XqEHMUDRRkcZKqpI)Wq!85LMr9#BPDR+j=6\\NDeEUHkFZcpDVWI5+tL)lDR6HI9DR_TYr\\de/@XJQAP5M8RUt%L)NVEKH=GrI!Jk598LMlNA)ZDPzUpkA9#BVW\\+VTtDPD=SrlfUe67DzG%%IMNk)ScE/9%%ZfAP#BVpeXqWq_F#8c+@9A9B%DT5RUc+_dYq_DSqWVZeGYKE_TYA9%#Lt_H!FBX9zXADd\\7L!=q_ed##6e5PDRXQF%GcZ@JTtq_8JI+rBPQW6VEXr9WI6qEHM)=UIk",
				"9BP/Z/UBBkOR=pX=BWV+eGYF69HP@K!qYeMJY^UIk7qTtNQYD5)S(/9BBPORAUBfRlqEk^LMZJdr\\pFHVWe8Y@+qGD9KI)6qX85zS(RNtIYElO8qGBTQSBBLd/PBB@XqEHMU^RRkcZKqpI)Wq!85LMr9BBPDR+j=6\\N(eEUHkFZcpOVWI5+tL)l^R6HI9DR_TYr\\de/@XJQAP5M8RUtBL)NVEKH=GrI!Jk598LMlNA)Z(PzUpkA9BBVW\\+VTtOP^=SrlfUe67DzGBBIMNk)ScE/9BBZfAPBBVpeXqWq_FB8c+@9A9BBOT5RUc+_dYq_^SqWVZeGYKE_TYA9BBLt_H!FBX9zXADd\\7L!=q_edBB6e5PORXQFBGcZ@JTtq_8JI+rBPQW6VEXr9WI6qEHM)=UIk",
				"T[jaIfd+@rg=Dhj2moK0Vs.uJ*v;w*AEGbX>^GH5Uk(:?<Tu_tF!#jx�i~R]7-z{}pNg$3lM9fS*@I-�Z2�|ks/J>4Tq?PAsWH61yt,G$mQ\\a�!iB=d09+vr{ezUWd%6[w&\\L#hJ>Ri5-yXp{m\\�:_3Zs0v8!<jw4g^`j(GM1OCTxQ�#-17n,};fW9@/[a&2jm�{|E0CuvrX=V!KDhcZJ~l,.53)|?_osw-y$41]b�^Fe(*x�RCUSyq/:\\<&8�z`?�7W#�6/�-O}Q>au=�0vUF.W!:IwdE^zG*(d-�Xx<R#Z4A)7u/9k1ay=�{Hl}YfJ.I:t~&\\DQ�@r8<V3X]1S*m`icOe0B6p%v5NM|Ph#Z>w*^do�4(/9xI+7bga1[�}Gq�KLfT=d.Ak&!@uUFHtBz",
				"vKeO<]r4]GdgRq<aa@tHZ_LWAo1MiXBI[2r3+gCYNSDnE^T56P+JUF_K+7Xf+bV8cGm9+ZoAkde@a1q2+gBl3+CnQ5r6+Rm7[Dp8+O]4<EYLWFo9Mi^GIhNq@gO+hHSZnJ+1P+KTA_d+2Xf+bU3cBk5+CoDlHe64iYEgF^LVGn7Mi_ZQm8RAa<s9p@WSo1[P<BXNTCn2OiYDIE^4UFoG_V35ghJWZn6LiXAQ+KSBYd+7^f+bT8cCk9+DoElHeF_MUG@1+ghNhZnhJfVRm2[AP<+hW<+]OQ]KBX4SCoR3r5+IhdTDn[6q7+g+L+8E9@+P+HUFYJ+1^f+bV2cGk3+ZoAlKeBm5+CnQ6r7+gDk8+EohMq9Rl@[Z0dI4<]",
				"i76O<d6P01ZSAj6A[i8GH2I6QK=^gR6OAL634B]>CJ65TU96A?DG6P06k1Vn26M:WA6A@E;Sr<XA7Z[=FH63TU86A>BIjV_J?iQA`]R^GkOAZPAC64Np5[6Hm06k12DI]@KjA<9EYJ:hA66;LA_G6k3Sn4HAgFI=er>WA7Z8M5fANJ69KA0GH1[?BX6:`L^I6QAMYh2aACAd@_N6R];DZ3KeAf6OT4<JAq6PnALg=G5n0E6QUAmM71AhR6OH[>NiA?8WXA6A@F9V<jPA`]Q^IBAC6RJ:gA66;KA_G62D6OZ7iLA[=ME3S4n5F6P8s`9NA^H6k0Td6QjAI]1Jr>YA:Z",
				"WURVFXGJYTHEIZXSQXOBGSVRUDOOJXATBKTARVIXPYTMYABMVUFXPXKUJVPLSDVTGNGOSIGLWURPKFCVGELLRNNGLPYTFVTPXAJOSCWRODORWNWSICLFKEMOTGJYCRRAOJVNTODVMNSQIVICRBICRUDCSKXYPDMDROJUZICRVFWXIFPXIVVIEPYTDOIAVRBOOXWRAKPSZXTZKVROSWCRCFVEESOLWKTOBXAUXVB",
				"ABACDCABBAEFGHIGBJKLMNOPQARAPPSTOUVWOEAXAPTYZaWOEbcdefABBAEFghBijkTlAEmnoWpZZHPqrJUsOtuTvEAPRcQTwPxyNEFazIOPdEATkBSWsBBbYCABBPp0lmcA1FFA2oP3H4cJTw5RxcZABBAEFu6GQ7a8UlA9o2H!#JbmuZqc$%FQRxAEF&IOZZSUCPYW(g)bcNFAZB*ca#l+qGdZRpWAxAPbckmgcoEA,-B.#HZJ#wZEAEGsZv/AUuNE:dBB;cQAck2aCAB<lygAB=#oU>THT@[Bs2JPAgABBESRFu&YOT&E]TQ#^UNOPl&pOg_BBxZ&bwPBI`",
				"LZhiCM+ks8t9uNuVFab0mvklDWkGLMw4xfVEjIyW1JoCkxgz2fAc3HdksN5yVZhksXyFbiODm+ksPmGLaBTlZ6h0EiU1l2RH!ksjC@l#lT3$M+0z1g8c2NDz3kj%UeEQY+ks$LwWCjKzFMGNDI0J9L^ra%HlOFZbXE!wGCKla7IDjMZhiEN1zAuTC$dh2DiU3j%THfJEth+vitLMatyVN4yWZhz0tj%Ul#5FihxgiCx6j^tLw7xfVDjKj%Tl1$EWGMBVmyug2sNyW3mvklCVkj%Ul+eDPY0W$L4yVah$M12z3f8c+yWHdksNwVEjIksj%TS9m#UCLMzFNGLyDzvJAM^rZ%HlQFabElNvm0RCjz&jD1rZ%GlOHB8tKE52raFm9T3@yWksXy6g&s$e+GzvCdz0cjHzsuLFbZU7y1sMYDEPVxA42WBX5GCI",
				"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdFeEfghijkWlmnLoYMpqcrstuvwxyAzE0A1G2e3456Jx7O89j!#NYH$h%&X(e)*a1+,i#,x1I-K(.e/2:;<M!f*i=>@$[vlst\\]>^_`qC{|}~0_~U���/�dO�s,��Q^w��b��}[�ui�no�KR4��R���{8���;T���%��Ru[T����}w�$����V3�����=Z�Ndn��4Px�NY�����snd��;�K�DY�B�VTo�}36�[6�j��EV�,�u�:�|WX5��r��x@�vW�uy郂g��}�A`W4O�xfVS�",
				"P%P/Z/PB%POR=pX=BWV+eGYF6PHP@K!qYeMJY^PIP7qTtNQYD5)S(/P#BPORAP%fRlqEP^LMZJdr\\pFHVWe8Y@+qGDPKI)6qX85zS(RNtIYElO8qGBTQS#BLd/P#B@XqEHMP^RRPcZKqpI)Wq!85LMrP#BPDR+j=6\\N(eEPHPFZcpOVWI5+tL)l^R6HIPDR_TYr\\de/@XJQAP5M8RPt%L)NVEKH=GrI!JP5P8LMlNA)Z(PzPpPAP#BVW\\+VTtOP^=SrlfPe67DzG%%IMNP)ScE/P%%ZfAP#BVpeXqWq_F#8c+@PAPB%OT5RPc+_dYq_^SqWVZeGYKE_TYAP%#Lt_H!FBXPzXADd\\7L!=q_ed##6e5PORXQF%GcZ@JTtq_8JI+rBPQW6VEXrPWI6qEHM)=PIP",
				"ABCDEFGHIJKLEMNOPQRSDETUVBCHAJWXIDEYHGCUXZAVDaXBbWXcEFGKZdPMbaSKOePTWcVbFBKFRdNILcPfdYMHZQJbLLOLcFSaRLWGZXIgDVgBEdacGKWXTZJffhMPSHOFDRTaILMiCJfbVeeOSHRIKUTUMLdPJUDOSWFcNdfeTHZREacbUeXIWXMDZXJaKSWdBPjH",
				"ABCDEFGHIJKLEMNOPQRSDETUVBCHAJWXIDEYHGCUXZAVD0XB1WX2EFGKZ3PM10SKO4PTW2V1FBKFR3NIL2P53YMHZQJ1LLOL2FS0RLWGZXI6DV6BE302GKWXTZJ557MPSHOFDRT0ILM8CJ51V44OSHRIKUTUML3PJUDOSWF2N354THZRE021U4XIWXMDZXJ0KSW3BPGH",
				"bkmpowllgupyqgktjqcnxqpggmjtbyxskeklluorxzbukhumpxxhbrjmoigthvvrntpabrolllkmpentmaprtntjrxhmgugkhkgkkzbbstakgyhapnzplkntnzmvzmvqzvkvdrzalbbuezerallbbkqwtbxxaprukpovtghthtlvtikwjqcnxyigzapacububvchbtqrbrmguflvgohuqqugvbpuesmpzlsisklhvqoduwjnhdbbvkvxrxefackesxnxtkqpkbdwhrwhtfuepsrzhtmazbvvvgfocamkfqasrhngkksqsklsiikldqcrfhsrsxmzryaziajppkukl",
				")+9*%*=;C)-'/8./+:\">#!31%92=B1A,3#(&3@)<9B,.08&3-2(?@*=;C)-'59+$'!,:=@<(>&A00:12\"8#?3B:,!-)1<(>,.B2$?@'%0A31!-9,?;.&!C+<A*=;CB.,82()@''94:1,><(%,AB2<(0=+;)-'86/:0>@#%92=184:-\"><2%0<(?@'82<)-'7.300A#*B.&&592(!'=0C<(:\">12/?0<A&)29B<(!25(%@=$)895=+;\":0>\".0-)@/?0!$9#%?-$!C+<(8=(?4:*);C>$59+;\"%#A,8,71C!4:B=5)+;@.2'94>7A3,7-?,%\"8#!31:7.35=C+<07",
				"XUKEXWSLZJUAXUNKIGWFSOZRAWURORKXAOSLHROBXBTKCMUWDVPTFBLMKEFVWMUXTVTWUIDDJVZKBRMCWOIWYDXMLUFPVSHAGSVWUFWORCWUIDUJCNVTTBERTUNOJUZHVTWKORSVRZSVVFSQXOCMUWPYTRLGBMCYPOJCLRIYTVFCCMUWUFPOXCNMCIWMSKPXEDLYIQKDJWIWCJUMVRCJUMVRKXWURKPSEEIWZVXULEIOETOOFWKBIUXPXUGOWLFPWUSCH",
				"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz012345em67894!#N$%&()*+,-./:;<&=j>@[\\]^_`{|:}~���A.��֚�J�_������a����7�Z0����/�F�����������i��I��UR��������EO�dTShM�L��B�Wg�Z*�xk���LJNDQ+|1p�crSyf�En�q=\\�G�_�eC�:20#K{XHVl9C��,�8�v^i�$%�c)��/�4<u>�!�P��Yf��ms���(�o5;@�v�Aj[t�3`�,��F|uw�&�6bn]�{.z~,�~��d�B3-W�R��{�X&��~*+}���:�7�",
				"au=�0vUF.W!:IwdE^zG*(d-�Xx<R#Z4A)7u/9k1ay=�{Hl}YfJ.I:t~&\\DQ�@r8<V3X]1S*m`icOe0B6p%v5NM|Ph#Z>w*^do�4(/9xI+7bga1[�}Gq�KLfT=d.Ak&!@uUFHtBzJ>Ri5-yXp{m\\�:_3Zs0v8!<jw4g^`j(GM1OCTxQ�#-17n,};fW9@/[a&2jm�{|E0CuvrX=V!KDhcZJ~l,.53)|?_osw-y$41]b�^Fe(*x�RCUSyq/:\\<&8�z`?�7W#�6/�-O}Q>T[jaIfd+@rg=Dhj2moK0Vs.uJ*v;w*AEGbX>^GH5Uk(:?<Tu_tF!#jx�i~R]7-z{}pNg$3lM9fS*@I-�Z2�|ks/J>4Tq?PAsWH61yt,G$mQ\\a�!iB=d09+vr{ezUWd%6[w&\\L#h",
				"9%P/Z/UB%kOR=ZX=BZVZeGYFZ9HP@K!qYeMJY^UIk7qTtZQYD5)S(/9#BPORAU%fRlqZk^LMZJdr\\ZFHVZe8Y@ZqGD9KI)ZqX85zS(RZtIYZlO8qGBTQS#BLd/P#B@XqZHMU^RRkcZKqZI)Zq!85LMr9#BPDRZj=Z\\Z(eZUHkFZcZOVZI5ZtL)l^RZHI9DR_TYr\\de/@XJQAP5M8RUt%L)ZVZKH=GrI!Jk598LMlZA)Z(PzUZkA9#BVZ\\ZVTtOP^=SrlfUeZ7DzG%%IMZk)ScZ/9%%ZfAP#BVZeXqZq_F#8cZ@9A9B%OT5RUcZ_dYq_^SqZVZeGYKZ_TYA9%#Lt_H!FBX9zXADd\\7L!=q_ed##Ze5PORXQF%GcZ@JTtq_8JIZrBPQZZVZXr9ZIZqZHM)=UIk",
				"���V�ȼ^VI^X�+j+ԑ�^ȸ��ԅ��JX^�A�m�+XJ���+�^Ƒ��^/��IXyԅ+nJ�������M��˅�m+�Ѽ^+��7�ƒ�IXy�԰�ƔHX^��+A�+�mXJ%Լ���IXy��^�԰H�XVn+�m��7X�^���^���^˰��m7�+��V��mIXy�M�ԀXX/M�^������+��^��^�n+���ԑX�m�^ƒ�/�XA����%�y�ƣ�HH�^A�+^��+ImA�nnXA���A�X�+Ƃ^�n+����ԅ+nJV�V��^ƑX�7���m��XH�+IXy%�^���M����ԅ+nJJԂљ^y^m�+^�^+Ѹ",
				"cZKqpI)Wq!85LMr9#BPDR+j=6\\N(eEUHkFZcpOVWI5+tL)l^R6HI9DR_TYr\\de/@XJQAP5M8RUt%L)NVEKH=GrI!Jk598LMlNA)Z(PzUpkA9#BVW\\+VTtOP^=SrlfUe67DzG%%IM",
				"9%P/Z/UB%kOR=pX=BWV+eGYF69HP@K!qYeMJY^UIk7qTtNQYD5)S(/9#BPORAU%fRlqEk^LMZJdr\\pFHVWe8Y@+qGD9KI)6qX85zS(RNtIYElO8qGBTQS#BLd/P#B@XqEHMU^RRk",
				"Nk)ScE/9%%ZfAP#BVpeXqWq_F#8c+@9A9B%OT5RUc+_dYq_^SqWVZeGYKE_TYA9%#Lt_H!FBX9zXADd\\7L!=q_ed##6e5PORXQF%GcZ@JTtq_8JI+rBPQW6VEXr9WI6qEHM)=UIk",
				"VeXExA4WAuO<X{xEAeV[EMAV3[A[Vx!3EMMA2VMAX{x3E[AX]ApxVWAUVX<2O3pAX2x]{p2AUO3[]UMAW]{xEAU]3[ExO3pAOeAO4A]!VWAME<xEXMAMX]^E3Aex]4A[EEuAO3MO[EAX2EA[x{4AnEVXMA]{XA]eAXO4EGAMe",
				"TAKFQMOTHQSTOCNHTATSCTKONXTOTFNJOKGTAKCWGHEHTVVGEECGOTNITEOKNKCNMPTAKOHGKOFFGAOTEGIZGWGTAKCQOIFTOJNAWMHVJNEAKNWTVGECGTHQKGCYCNOCNOHGTZKQBQMAKFFGAOAONHQMFICNFGKHMJJNAKQMZKHQACQENJGTAKHGWIMOKCTJTFTTAKYSTHWIMHTKKGJECGTAKAOQHSIMOEFHMW",
				"ABCADEFGHBIJCKEIJELBMNODPJIMAPGQCAGKREBJIMACAGKLBSSBMNCAGKSBCAEMBMNAKDGMPCBTADBCBCMGAGMADETUGMVKINEBMIOEELBOBSSCLBMWSBAASELBHCIMHRILEICQBATUGRADECLBM",
				"h,>05W<3haWIRghfDK\\P0\"?b<5R39RYIS(RGIdWIIR@05W+WRM1NA<5h0VW=h;T)ILR[W3h.]<5WQh3W[W3\"gW_h3WI&a5hI0Rfh[Ri3<5`I2h0W<5WR8JQ9RE*6W0WBh3hU,W><R05WDhXNWKI@R/\\!PY<5WbSWJhdC(=Ih:hRAO3W0<)3c5R`Ih^K;TRg.R305W385W3LG6WI<5PI5W3R*W3fR3HIEh\\\":3Wh0DWh<I+10<5W3Wh3W053WW?5ba5XWURYO<R05WI&BW3dh<i3h,(;R.WR@05WIW5WM3WI<>WIV)05hA`W8]9hNJW2:6hHh\\!^KU,I5PEhYC<5R",
				"zRO1+ dI^2Y3lp IZY+FBI>c+ O<YL>4F, B5 V6 W73 Y89M M!@# Md z$Mp Iq",
				"B1Ot+km^pB12^IB1rB2/Q^AXtB13pJ2^BOtHt+BrB2X^A1+^B1+tO2/^XrBB+qHBBpJQ4JNN+jHO7BrB2X^B1+tO2/^BrNAr\\/rt+M+4rB2X^k^A1~B\\XYJ2^fVYB2B/~4Ar3/AXtB1B1+,XmBXJ^XB2I^Xtk^IB1+m2I^/A1+^rf2NOqq~2mQ^7pM+t+f\\XYqQ/BBr|O~7B2p^r^f^rBt+/BY^B243pQt+mXNMO2BBX/~BkmJr,B2p^I2MOB1+fOBrkN/B1+rBBO^B2X^B1+3fOq~^fVO7rYm+A1+^3pQ~^r43zO\\XYQ^fOt/B~^f",
				"WRGOABABDWTBIMPANETPMLIABOAIAQCITTMTSAMSTGAB",
				"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwKxyz0T123456789!#$%&()*cp+,F-h./:;<&=>@[P\\]^f_Y`{xlHK|IOG}~S�RyZUWy����xmbAt�6e[��Nrde3$25kn�]87&E��9z`(#�u-a�3�5X=|3H[lo�pQ;�W.��FDO�vAXg{�$n><C�1j��0wI\\QjJgG|B8-_kh�@n^oPMU�+�_�9HAVd}svw�`�#r�NuW&CQ)Ff�Em���9*`xlbKjIl*q�pu�p�[(��`+_�s�~�O,yz�I�=�/�Pn@��|V$�aEd�rjZ3T8D�oJ�RK�f15e{�hB��Sc#m%�|��XN�y0A_-H\\!fV��t|+�#_*NMJHu6��S@}{;�sn�WbFD���|O��mG:�YR>qlUI�a���pmz%�j+V��1Z0k��*>xhMP*43$�2�rw8^@7,9H�e�(o#S<YdbA[P�lB=g�qpp�x�-.J|]fzt~EUVWX�&caNx��y��c1K*[�Qk3�,5�8^�h��9$v)yM�-#�OjH�6{<�sno�p7YV>�d�B,5(LSDrP%\\WbGga�3��q}_)Y�=B�d#[�I0Ej@J��wN���O;$h|z:)R1Z8K=��X��oec6UH-�~�,�mv��^0�~�Wr�*xy�>{B%I54��+��Vr`|�OD}b*�",
				"C9J|#OktAMf8oORTGX6FDVj%HCELzPW9",
				"ePTWYPNWA[1WkSNZf22Q32ZZWkW[[",
				"AENz8K8M8tNAM",
				"MEOTAIHSIBRTEWDGLGKNLANEAINOEEPEYSTNPEUOOEHRONLTIROSDHEOTNPHGAAETOHSZOTTENTKEPADLYPHEODOWCFORRRNLCUEEEEOPGMRLHNNDFTOENEALKEHHEATTHNMESCNSHIRAETDAHLHEMTETRFSWEDOEOENEGFHETAEDGHRLNNGOAAEOCMTURRSLTDIDOREHNHEHNAYVTIERHEENECTRNVIOUOEHOTRNWSAYIFSNSHOEMRTRREUAUUHOHOOHCDCHTEEISEVRLSKLIHIIAPCHRHSIHPSNWTOIISISHHNWEMTIEYAFELNRENLEERYIPHBEROTEVPHNTYATIERTIHEEAWTWVHTASETHHSDNGEIEAYNHHHNNHTW",
				"PBP/N/PBBPDR=NT=BNVNeGYFNPHPFFTqYeMJYDPHPFqTtNJYDHMGD/PBBPDRAPBzRGqNPDHMNJTttNFHVNeGYFNqGDPFHMNqTGHzGDRNtHYNGDGqGBTJGBBHT/PBBFTqNHMPDRRPcNFqNHMNqTGHHMtPBBPDRNj=NtNDeNPHPFNcNDVNHHNtHMGDRNHHPDR_TYttTe/FTJJAPHMGRPtBHMNVNFH=GtHTJPHPGHMGNAMNDPzPNPAPBBVNtNVTtDPD=GtGzPeNFDzGBBHMNPMGcN/PBBNzAPBBVNeTqNq_FBGcNFPAPBBDTHRPcN_TYq_DGqNVNeGYFN_TYAPBBHt_HTFBTPzTADTtFHT=q_eTBBNeHPDRTJFBGcNFJTtq_GJHNtBPJNNVNTtPNHNqNHMM=PHP",
				"zkLBlp^tW)FP2OX+<;5q>KG7DR&V%jYtk+dO(|5FWKLclp-42#lMLC<5BNtG3DX*|JRf+DzRO8#pcS^+|JH(TVK(ypM*Vc.b#JdB>&FBc|5FBc4+>LRBV+OY-*4+yp.#/K^B+4GWR2|cqzUb6;+Ed+cT#-Z3M|y582_1:fp49-YCFP*-O2_Rb+)SkB>8HUGBM+Z).VEj6T^9*|fHERO+G2KS/klF)D8.z(A5Tl|N:VUzWO++k2d+9F^LcC(zKf4+YNO<p7UWz<p;(1zZ%7|Ak(p2.LF++FMCcB|H<pdUC2JT+GcWlZ5M+y^SM+_NP)/tBFO^+1l*N+yK@O9.<R6B",
				"BAUdS+)k7dKFEj.%|5FBc%WF<#j.JBN;qX:df9FGtK-1O&B/bPR&5d7k*(>R54dENldBUUd1;2DZ&OzZ(D;^cbVT81V)df%WdPYVZ1FGk1l|G5dRNC8|:d1%RcG5Vc.bPl;%9K<Zp|AB9c1zXj_d)>T5(-VBbd.kGdK#9%>%:-2dPk^b(Hl>TzLZS><q/B3RWYBjXkL>Wd_y@|8%#9yp)tN6*|JR7EB7.k@dHM9H8|y.*-p)<D:yC/MT*ZJE8|55c1z*:d6MMd(RyEX688RVB1^dFBc%lF<#%kZ8K*L|GPN>&dPSFdl|3K_-*(R+Jbd2Sp|5FkpLtV5d#FMTHER+",
				"b&/LjOUWG^;yqRU|DN/fEcRN5V7tO)FDOOHODllOP+;y7+zc4_+FyX|5FC:TjN2dY(#N3Tj*-BY7MJy7*|JRB#_L):X2dcp:+|-:pA&_<|/G|Vc.b^JjBj2FBc|5FBc6+HERkS+VM-*P+|5.zly_B+EKty;|cOMGb-j+6)+C.z-AJ.3U#9;%RU2JP;-MBc2^#Z(%jk*++pyBHXB1Kb@+UO.8686@&|2y6R^+Mq;fpl:NRO_<@tC(W3NzZz8A/C9++:(c+(cYTRk<Cy2Z+M2V*d@tLUJ)XRLZ;Z^S_<4FGTF++FG4DB^f|#).B(dG+MjLN>53+3fp.+%9+Ol7BDWy",
				"2%^CRW&TG#<7PR&|Dd^XEcRPMV)^WjFDWWHWDOOWNt<7)t(cb>tF79|5FC1qjd2p%_Md3qj*-B%)kJ7)*|JRBM7CL192pLy1t|-1yAl>+|/G|Vc.b#JjBL2FBc|5FBc4tHER(ltV:-*4t|5.zO7>BtEk;D<|cW:Gb-jt6LtC.z-&J.3&MN<KR&254_-:Bc2fMZ_Kjb*tty7BH9BS:(Ut&W.8686Ul|b76R#t:d<XyO1djW>+U;C<T3dzZz8A/C6tt1_ct_cAqR(+;>2Zt:2@*pU;^fJc9R^Z<Z#Y>+TFGqFttFG@DB#X|ML.B_pGt:j^df53t3Xy.tKNtWO)B7T7",
				"k#tCF_-:Gy6&TRy*+M^&fcFM5UK/_l37q+l+37yq(<6pK<Wc3#<FP;|5FC&8(Tk:Xq#T4E(*-BPK>jYK*|JRk#U/+pL*:cNY<|:pNO7U;|:G|Vc.b%JHB(kFBc|5FBcV<HER17<V>-*X<#5.z)YpB<(>tRq|c_SGb_(<d+<R.z-z5T/##962D*kfXq->Bckz5%q2F1.<<NYBH;BASBO<W_.7dO_t4|k&dRV<>M6PN)pMF_U;O^Cqj7MzZz9A/C@<<Pqc<qcP8R1;CYkZ<SkVy;ACCW#+L1^ZqZ|@&;#FO8+<<FG1DBJp|#(lBq:G<S(^My54<*&N.<29<_)KB+:Y",
				"Gj|U|PMWXjB|MF*pVz&:Tc)y8l9BRM2&@AP-*J(YFfjcMOfOt(SYq|VY571UO-G4DSc(L-)>:f9Rfz7GTjBl<_+ACNG<#7Md_fl5(X@CNBFBcO-Wc>&T6+AXBfESpkLLfflzNfOF#MyV/5^z^(C2UtjSUN6A<_b.cVpK2fOLfF-VySHERbcR<ZP^@>-k%ycTT1J.6V^fK+bfV<|@W)Ojb|5FSJ+1-#fOjMtcWNC@(f%U6AKzZ%d9;kjDd*dDZ4L*z4DEB|+SGKRJ|*/5XB|:z|3pOY<_|q|5cO7f9j*-#fYj&9BJG>z)2fzcCR8|5FBcRyG+YF@U&2)tKABl2XSf",
				"Gj|U|PMWXjB|MF*pVz&:Tc)y8l9BRM2&@AP-*J(YFfjcMOfOt(SYq|VY571UO-G4DSc(L-)>:f9Rfz7GTjBl<_+ACNG<E7Md_fl5(X@CNBFBcO-Wc>&T6+AXBfESpkLLfflzNfOF#MyV/5^z^(C2UtjSUN6A<_b.cVpK2fOLfF-VySHERbcR<ZP^@>-k%ycTT1J.6V^fK+bfV<|@W)Ojb|5FSJ+1-#fOjMtcWNC@(f%U6AKzZ%d9;kjDd*dDZ4L*z4DEB|+SGKRJ|*/5XB|:z|3pOY<_|q|5cO7f9j*-#fYj&9BJG>z)2fzcCR8|5FBcRyG+YF@U&2)tKABl2XSf",
				"zl3WVc.b<+2Y29<MU#S&lc>/*dA9c:M57V3|5F.HERqflOG-c(Vk<k#bH)3p#&%q+;yP@qjkKp+qq116/WX++2|5FBcqYqV6/W@q_k8tjy5:qGRqq:dO1@d1zqK;Rq>NN3E8HzZ27A|k%:24SBW<RcqVXqZqtXqC7q7:qRd5T^-y*;FBcfX5L|*-5H-*_qNtM33kP-*|ZBB;Y2<.DzAHqljd>*|JR>N4qD%SCTq;f)|W(YCY/^kblR)L@cdJclHT:qTGqyJELZ:MRBOfB3BylV98GzdbKJ:dZfqPdV@>q*K52cfLARCzBOdGj)@JzBd3P/W+21c@2Tq:>NBZLG6N",
				")/@VbJ|ldGNS8XKTqyFk*-pNY_(9K2Bp6NdLU|M:)N%zZ1D;^cNNAE4p#Rtp+#f7X|z:lA<&z>dWFRp34-*N+#LcZ#9JGJ^HJ.*D5V#;A|zN#q/MET(_NtJCcydz>ZyFBcbOPOG6dlA_Dp#RpPO.d|5F_-<ky(KLJ7/NcYfOP-8|BUE1)|+N@Vc.bfz^-9-:%O*|JRMNyF4p<1CWzS8Ntp+_>7X:AKX8@LzTkN/3Rdq@l)M_&U9z7:Cl(SANtXcPNU#S)3NYXMS7RkpT(34b#F^(KK6KA|/c#NSZ@VF>)yN|PqlLF97|8<NzL2k%6%Y|5FBcLHERNqA|CpN3ZjjM",
				")/@VbJ|ldGNj8XKW(yFS*-pNC_q9K2Bp6NdLO|M:)N#zZ1D;^cNNAE4p%Rtp+%f7X|z:lA<&z>dTFRp34-*N+%LcZ%9JGJ^HJ.*D5V%;A|zN%(/MEWq_NtJkcydz>ZyFBcbYPYG6dlA_Dp%RpPY.d|5F_-<SyqKLJ7/NcCfYP-8|BOE1)|+N@Vc.bfz^-9-:#Y*|JRMNyF4p<1kTzj8Ntp+_>7X:AKX8@LzWSN/3Rd(@l)M_&O9z7:klqjANtXcPNO%j)3NCXMj7RSpWq34b%F^qKK6KA|/c%NjZ@VF>)yN|P(lLF97|8<NzL2S#6#C|5FBcLHERN(A|kpN3ZUUM",
				"kF|j|2^t(_B|YF*MVz%;XcyNP)FBRY%3T(2-*jA7F.^)YOHb&A*7S|V751WjO-G#DLcA4-yl;C9RCzMGX^B)OU+2>tGKf1YCUC)5A_T><BFBcO-tcl%X/+(_BCEL8k44CC)z<COFfYPV651z8A>3j&^Lj</(KUb.cVMp3CO4CF-VPLHERbcRKZ(8Tl-C:P7X/WJ./V8dp+bCVKJTtyp^b|5FL|+W-f_O^Y&ct<>TACqj/(pzZ@d9:k)qq*dqZZ4*qdZEBb+LGpRJ|*65_B|;z|#MO7KU|S|5cO1C#^*-fC7^%lBJG9zy3Czc>RP|5FBcRNG+7FTj%3y&p(B)3_LC",
				"kF|j|2^t(_B|YF*MVz%;XcyNP)FBRY%3T(2-*jA7F.^)YOHb&A*7S|V751WjO-G#DLcA4-yl;C9RCzMGX^B)OU+(>tGKf1YCUC)5A_T><BFBcO-tcl%X/+(_BCEL8k44CC)z<COFfYPV651z8A>3j&^Lj</(KUb.cVMp3CO4CF-VPLHERbcRKZ28Tl-C:P7X/WJ./V8dp+bCVKJTtyp^b|5FL|+W-f_O^Y&ct<>TACqj/(pzZ@d9:k)qq*dqZZ4*qdZEBb+LGpRJ|*65_B|;z|#MO7KU|S|5cO1C#^*-fC7^%lBJG9zy3Czc>RP|5FBcRNG+7FTj%3y&p(B)3_LC",
				"R7X|@4YN%D_FJZ4p|5FBcp>F&KR8)6Hp6l>DJMF^WA-.((BO9kB1(DO_*1_B5PD+qSDRXXDY;1dBG)zZ(d;^cEVU344ODE*(DkACB:F^#cT|G1DRHAOp-D5pRW31Vc.bk^Z@MN&ZtD5BMcO(2Z(DG_&5/-VRbD.A^DdKMp_pS-(Dk#Gb/pSA&Y/()T)LL6L+)L+6TftyY|jCfD8pKMcG3@>;*|JR%9L4%#MDf3MHy|WO*-G^&dqW4.<Uq6JfG|/7c4z*SD;X<D7RWJlB:.RVB.VDFBcpSF&Kp_B:N*>|GkSN5Dk@FDyD2#j-*4B8+f|1c3|5F_3TcV/DKF<&HER4",
				"<#/CF@-jGPM:ORP*WKt:ScFK5Yd/@S4&yWpW7&Ey)+M:d+Nc4#+FY2|5FCUS)O<l^y#O3j)*-BYd(lLd*|JR<#_>WUX*lckL+|lUkT&_2|lG|Vc.b9JHB)<FBc|5FBcV+HERf&+V(-*^+|5.z8LUB+);tRy|c@;Gb@)+6W+R.z-_5O/##^M%Dz<q3y-(Bc<z59y%Ff.++kLBH2BA;BT+N@.&6T@t7|<U6RV+(KMbk8UKF@_2T>Cyq&KzZz7A/C1++Yyc+ycYSRf2CL<Z+(<VP2AC>N#WXf>ZyZ#p:2#FTSW++FG4DBP:|#)1BylG+;)CKP53+*:k.+%9+@8dBWlL",
				"GDU||WN+UEb7NU*|Vzyp2clj6M9BRSA^(SW-*J+qF.DcS4G4XfOqL|Vq5_>y4-GFbPcf)-l<PK9RKz7G2DBM&1TSC;G&_7Nd1KM5fU(C;BFBc4-+cE^22TSUBK%P3k))KKMz;K4F_NjVt5#zyfC%yXDPy;YS&1b.cV3/AK4)KF-Vj:HERBcR&ZWy(<-k%jM22>|.YzJd/T*Kz&|f+#4Db|5FPJT>|_K4DNXM+;C(fKAyYS/zZ%d9:kDOZ*8<Z8)Zz8OEB|TPG/RJ|*t5FB|pz|@34q&1|L|5c4_K9D*-_KqD^9BJG<VlAKzcCR6|5FBcRjGTqF(y8AlX/SBMAUPK",
				".Fj)pP/(XFS|/FSpVz&:TcF88l9BRM2-RAP-*-RYFfjcA<fOtESYq|VY5_1UO-G4DSc(L-)9:f9Rfz7GTjBl<_+ACNG<j#/k_fl5(X@CNBFBcO-Wc>&T6+AXBfESpkLLfflzNfOF#MyV/57z^(C2UtjS&N6A<_b.cVpK2fOLfF-V8SHER;cR<ZPU@>-k%&c6T1J.TV-fK+bfV<^WW)Ofb|5FSJ+1-#fOjMtcWNC@(f%UTAKzZ%d9;kj*t*L494LtW4DEB|+S.KRJ|*/5XB|:z|3pOY<_|q|5cO7f9j*-#fYj&9BJGjz)2fzcCR8|5FBcRyG+YF@U&2)OKABl2XSf",
				".Fj)pP/(XFS|/FSpVz&:TcF88l9BRM2-RAP-*-RYFfjlA<fOtESYq|VY5_1UO-G4DSc(L-)9:f9Rfz7GTjBl<_+ACNG<j#/k_fl5(X@CNBFBcO-Wc>&T6+AXBfESpkLLfflzNfOF#MyV/57z^(C2UtjS&N6A<_b.cVpK2fOLfF-z8SHER;cR<ZPU@>-k%&c6T1J.TV-fK+bfV<^WW)Ofb|5FSJ+1-#fOjMtcWNC@(f%UTAKzZ%d9;kj*t*L494LtW4DEB|+S.KRJ|*/5XB|:z|3pOY<_|q|5cO7f9j*-#fYj&9BJGjz)2fzcCR8|5FBcRyG+YF@U&2)OKABl2XSf",
				"d@(YVc.blfDy6<dqpYW&dc4>GL8_cPMN(;K|5F.HERMRBUG)/At-*-#bHMU:#%9MfcNHVMl)_:SqM@|6(yqfW+|5FBcMyM;67YtM>-.6j5NFM8RMXEdKj;B|AM<&PM1CCAW+^zZ47A|k9R.|SB#jRcMt8MLMSOMkkMkPMRd_TG-5b&FBcRq5D|*-Nz-b>XCGqKc()-*|Zd<%y1j.Sz6)MBj2.*|JR+C@Mf9S.TMpPM|YVy1yKt7^2RXzVJ2JcBHTPMT<MN/PzpFMRB_PBU25d66+GOdG_JPBpRqHLV;+M8@N+%P+bP1CdKL8jXVcFLBpHk#SU|Jt+TXF.CD/2G6C",
				"GD|y|WN+UDB|NF*3Vz^p2clj6M9BRNA^(SW-*JfqFKDcN4K4XfPqL|Vq57>y4-G8OPcf)-l<pK9RKz7G2DBM&1TSC;G&_7Nd1KM5fU(C;BFBc4-+c<^2YTSUBK%P3k))KKMz;K4F_NjVt5#z^fCAyXDPy;YS&1b.cV3/AK4)KF-VjPHERbcR&ZW#(<-k%jc22>J.YV#d/TbKV&|(+l4Db|5FPJT>-_K4DNXc+;C(fKEyYS/zZ%d9:kDOd*dOZ8)*z8OEB|TPG/RJ|*t5UB|pz|@34q&1|L|5c47K9D*-_KqD^9BJG<zlAKzcCR6|5FBcRjGTqF(y^AlX/SBMAUPK",
				"pS7OjMzBjVqz%>Ajdk6(zCTD&E|K|OR|#BjPUWTt&j-*LFf|5FBcl|FN6UKkt*/kPBjG2D7P&MzZ2D;^KWVpS;j<3GlP;@lLpy*-p^&>F_718lLV>SB|5FBc6CjHBp:q/%z:O(;pV/CpF263>pHER*A3M-<:UT4zp_6t)WkX7->:UHB))3+l3Bt4p+B8SfUlzpSTb.cV;p<66q)pYJ:_@zOOc7Z3pp.K(ppRJ|*_p.%X1C_pkO#pp-7|#Lbp3:#3A47pt399JzHdj-yyS36OCFpy2RKY^Mz&7jZj3@DOyS<y<@WHDlXJjYJO%)L^&y*<N>(+7^7A(AN(NNy3MK&z",
				"p_L-YXH2N2O;kWCG6d&M#@3OX>LzZ)7^|kt)j1|5FBc;fU%%Z-LXUAk4_V<c5FETXB|ER9y%23z8>3l31l#bDq*|JR(<ZFY#WD.y3|b|33XN22TXHER9c%3yJ3FB%1yp63jkN73czRSz|5F3>LHj4p8:l-p#3EV6GK:T3Z)3^-^Fb&3#@Zyp_E/+S#FG#3%zC-*D6AP4kp+%FBc3&-.|d1((8VC43E6)C1(zZGR5VVc.b6Y_#5A&.H35y.cdV|>3zZ)dE|qZ&l6.-%*-XDl6bc1CUY7cbO3T3-2yFJL_6ARGFZc%DP67j%Z4j+zZ);*;W8;ZptfW;9fM8tG#3G/6",
				"pJLXYXH2N2y;kWCG6d&M#@3OX>LzZ)7^|kt)j1|5FBc;TU%%Z-LXUAk4_V<c5FEfXB|ER9y%23z8>3l31l#bDq*|JR(<ZFY#WD.y3|b|33XN22TXHER9c%3yJ3FB%qyp63jkN73czRSz|5F3>LHj4pZ:l-p#3/V6GK:T3Z)3^-^YbH3#@Zyp_E/NS6FG#3%zC-*D6AP4%p+%FBc3&-.|d1((XV@N3E6)H1(z8GR5VVc.b6Y_#5A&.H35y.cdV|>3zZ)dE|qZ&l6.-%*-XDl6bc1CUY7cbO3T3-2yFJL_6ARGFZc%DP67j%Z4j+zZ);*;W8tZpMPWWtfM8tG#3G/6",
				"VJ_*qZbV|MWCV2^.z._/D@;_YUt<cJ|&1c(8)9V5y764V9-*PKVGOdfYFEV|AVc.bLyX.c63<d.RYScN@FNtGVU9FVS*V1V.|5FBcMjS5c9VNVR7bVRf^XCDAHRE3G3H|@N(HF<3A>SHXCK|T7|kU6:N+-*|JR+V#Rp9|5FBc-d|#bGzZOD;9KR:HbVCJM4VJ7W%X.z6ZkE@_RAcKb7_MLBCJFCf>Rc9_G9FDHERL7/dO3EcJMcc._WGH)GJ)JGYJJUC.S*:V.fY9Vk/ARcDA*-*PVkjVydZVHZ&49|j.klRY(>fG;VkY:EA(l@Np8OlVM6ZfqBV#R5OzcRY%dNU",
				"V9bHyJ|^|f+UVSCLDZ|AWNV|Azk5C.|T3M36CZ/5|qyDDk-*@L7GOD:XREVRqVc.b+#j.c8P5dJ|X:c&NR&PHVUkRV%HVPV.|5FBcYyV5cKR&VRqbNR:H;cWq*b^(DPHbN&3GR5OtTS*dUJR@6RfcAp&l-*|JRl34R>J|5FBc-C|4|*zZ(8;9K|pG%VCJYDzK7lVjLcyJ1E27RGM.|t|1BBD3#U%T7zZ/*ZRWHERl6ycV(^DLfzc.|bb).)b.JM9M+ccK#*@Vk-XkVYy6RM8C*-*dVf)VScKVH.TUk|).1_bXPT-H@NYXpV<P_N&Ec3_VfAZ-)+34|53qDRX:D&c",
				"Gj|U|PMWXjB|MF*pzz&:Tc)y8l9BRM2&@AP-*J(YFfjcMOfOt(SYq|VY571UO-G4DSc(L-)>:f9Rfz7GTjBl<_+ACNG<#7Md_fl5(X@CNBFBcO-Wc>&T6+AXBfESpkLLfflzNfOF#MyV/5^z^(C2UtjSUN6A<_b.cVpK2fOLfF-VySHERbcR<ZP^@>-k%ycTT1J.6V^fK+bfVO|@W)Ojb|5FSJ+1-#fOjMtcWNC@(f%U6AKzZ%d9;kjDd*dDZ4L*z4DEB|+SGKRJ|*/5XB|:z|3pOY<_|q|5cO7f9j*-#fYj&9BJG>z)2fzcCR8|5FBcRyG+YF@U&2)tKABl2XSf",
				"XL+6TqzBTV1z%y;TPk6Sz8&D(&|AWOA|#BTd:Rf)7T-*CFf|5FBc7OFN6:Kk)*UkdBTG4D+d(qzZ4D;^KAVXLMT<<G7dMY7CXp*-X^(yF_+f27CVyLB|5FBc6lTHBX@1U%z@OS;XVU8XF463yXHER*;3q-<@:E/zX_O)tAk9+-t>:HBtt3j73B)/XjB2Lf:NzXL&b.cV;X<661tXWJ@_YzOOc+Z_XX.1SXXRJ|*yX.%9PZ3XkO#XX-+|#CbX3>#3M/+X)3>@JzHPT-ppL36O8FXp4RK|Nqz(+TZT3YD|pL<p<YAHD79JTWJ6%tC^(p*3RNS+j^++SKN+N^p<qA(z",
				"Op_&H9b<FB:37cS.yD._GOL*t/):4U|yH(*|JR%E8HpN2_W4j^7|kppPD|7k&LF-B&.jJ2b.^f-*J|#fJFzHERA;bl|y|t)|fVKfHWNkUUl@p5.4*-lS|z|5F42@@.<_/HP|bPCjJpb/f4GRG2d@RAcDF-#F<FL4kyXjUOqEK->T34;&Etfc8b561Z#15f6MXYz7Pld|GqTk@Cj>5/9#&Vc.bq76|5FBc9b@FBc8FMd_FkSBUDfXqRd.B2W&HE+Fc)bq@O#Ub)_FS|yzZ397|c21.<tFXfTq#fHHcGFEXGqBZNqHzO|f42<b^c*JL|Z.WL2y*SYWGz/|#GFFJplS",
				"Op_&H9b<FB:37cS.yD)_GOL*t/):4U|yH(*|JR%E8HpN2_W4j^7|kppPD|7k&LF-B&.jJ2b.^f-*J|#fJFzHERA;bl|y|t)|fVKfHWNkUUl@p5.4*-lS|z|5F42@@.<_/HP|bPCjJpb/f4GRG2d@RAcDF-#F<FL4kyXjUOqEK->T34;&Etfc8b561Z#15f6MXYz7Pld|GqTk@Cj>5/9#&Vc.bq76|5FBc9b@FBc8FMd_FkSBUDfXqRd.B2W&HE+Fc)bq@O#Ub)_FS|yzZ397|c21.<tFXfTq#fHHcGFEXGqBZNqHzO|f42<b^c*JL|Z.WL2y*SYWGz/|#GFFJplS",
				")3(VbZ|lFGj7^qTLCOFy*-Kjp<@9TABK6jd&f|>:)jtzZ1D/8cBj2E4K#RXK;#WMq|z:l2<UzNd&FRK%4-*j;_ScB/9JGJ88J.*85V#/2|2j#C3>EL@_jXzYcOdzNZOFBcbkPFG6dl2_4K#RKPk.3|5F4-<yO@TSJM<jcpWkP-^|Bf<1)|;j(Vc.bWzO-9y:tk*|JR>jOk4KORYlzd^jXK;_NMq:2Tq^(SzLyjK%RdCdl)>_Uf9zM:Yl@72jXqcKjf#7)%_pq>7MRyKL@%4b#FO@TT6T2j3c#j7Z(VFN)Oj|PC>SF9M|^<jzSAyt6tp|5FBcSHERjC2|+Kj%Z+Y;",
				"kF|L|K^t(lB|+F*jVz&:4c/pf)FBR+Y&T^K-*LPAF.l)+OHbSP*Aq|VA5X6LO-G@D_cP>-/N:W9RWzjG4lB)O2U^CtG8#X+W2W)5P(TCyBFBcO-tcN&41U^(BWE_Mk>>WW)zyWOF#+pV75XzMPCYLSl_Ly1^82b.cVj<YWO>WF-Vp_HERbcR8ZKMTN-W%pA416J.1VMd<UbWV8JTt/<lb|5F_|U6-#(Ol+SctyCTPW%L1^<zZ%d9;k)Dd*d3Z3>*3dZEBbU_G<RJ|*75(B|:z|@jOA82|q|5cOXW@l*-#WAl&9BJGNz/YWzcCRp|5FBcRfGUAFTL&Y/S<^B)Y(_W",
				"24(VbZ|pFG>7^STXCOF+*-K>3<@9TABK6>d&N|l:2>tzZ1D/8cB>yEjK#R;KL#WMS|z:py<Uzfd&FRK%j-*>L_)cB/9JGJ/8J.*85V#Dy|y>#C4lEX@_>;zYcOdzfZOFBcbkPFG6dpy_jK#RKPk.4|5Fj-<+O@T)JM<>c3WkP-^|BN<12|L>(Vc.bWzO-9+:tk*|JRl>OkjKORYpzd^>;KL_fMS:yTS^()zX+>K%RdCdp2l_UN9zM:Yp@7y>;ScK>N#72%_3Sl7MR+KX@%jb#FO@TT6Ty>4c#>7Z(VFf2O>|PCl)F9M|^<>z)A+t6t3|5FBc)HER>Cy|qK>%ZYqL",
				"<U%zNZ_%3f%M:88CWT3(.J)Z#;YJ3p%*S+P8HERtN/8<_(U)Jl*_.S8AkF8:#Vc.b43.M|T8;z#AtYc9Y282fF*-AW)z%XO.H99kjX)9FBc>G_dqb#d3YU%|8/6fb1;ZWOCB;|>>FYW5XOp<Oy:1X7zz<-K|2#8S^c9O88|<z-COM|EBc9z7N>|5F*-X)GF_YqpPJF^Uk&.C218B6BADKALLK+zzKB|E82zZ)dA:CMSLt4OKG*|JRZ#YOpCc^N9tY>|8C8R4Rf:|5FBc9L2J/5(cO#&Y39c8.OC.RAU8U8(39-&^7(-*t|>3MlYOK|@tFM/K|CGz/G.-<U+Mc8B3",
				"dM#;Vc.b^<4y&G9N@l6Zzc&kODGU3p+)LS:|5F.HER+FB_O7c#Pk*7lb>+UYl%8+<%5>S+(L_Y64NM|jkl+62&|5FBc+y+VjL_<+:7tO(5)F+OR+d-f_KSfM#N:qp+.WW#2t>zZ17A|k8F.|<B;KRc+PX*tNX>UpDt>|+pf_Tb-)G3FBcp+zB|*-)>-AU/WO+#%7H-*|cBACyt^12fO>+BK88*|JR.WM+292&TN%p+|lSy.ykV-8dR+zPcBJcBETF+TOHOqpzJp+RB:pB:z)dS*&G(Db_ZpfCFX>fVP&+%U5t3R1AF4Wz:1OK/PcRfBZ>5l2:F%S%T+p&Wz3zOjW",
				"WUPYj7cTq1TBk88FOE3(.8)77#l4U6PB#5P8HER>jR8W+(3)8cB_Bt8fkF8k7Vc.b;tMM|%8>z7fSlc9lD8D5F*-f^)YT<O.399kp<)9FBc4G_dqb793XUTL8N^5b6>Z^OCB>|44FK^5<O2WOyL1<UzzW-Yk+78tK_9O88|Wz-FOML%BR9z3j8|5FB-<)GF>Kt2P4Gl3L<MFR68VV*/fVAVA*JA:AM|E8+zZ)dA:CBtKS;HzG*|JRZ7KOpCcXj9Sl4|8C8D;R1L|5FBc9l+JN5(cO7&K39c8.^GBRfU8U8(#9D&l#(-*SL83M/KOY|@_F.Nz|CGzNG.-W#1Mc8B3",
				"-9bHyJ|^|f+UVSCLDZ|AWNV|Azk5C.|T3z36CZ/5|qyDDk-*@L7GOD:XREVRqVc.b+#j.c8P5dJ|X:c&NR&PHVUkRV%HVPV.|5FBcYyV5cKR&VRqbNR:H;cWq*b^(DPHbN&3GR5OtTS*dUJR@6RfcAp&l-*|JRl34R>J|5FBc-C|4|HzZ(8;9K|pH|VCJYDzK7lVjLcyJ1EN7RGM.|t|1BBD3#U%T7zZ/*ZRWHERl6ycV(^DLfzc.|bb).)b.JM9M+ccK#*@Vk-XkVYy6RM8C*-*dVf)VScKVH.TUk|).1_bXPT-H@NYXpV<P_N&Ec3_VfAZ-)+34|53qDRX:D&c",
				"GKEKU+zTKPBA+&&zK3XPCF|Hb.FP|PFFzkK3>7P&XZLzc%75lX*y<PjG9KOt@f76G#D6PPUPqj29Kl:P>Bf29^_VG6P>f7*-c/C2R#XOVbdS|Wj|NN:7R#_p+LB@;M#cjY3P(1dSWyLjR>z15)zzZ278;k2%&4#H|b/SpMj5Pz-)jHER-*.NzfBPdM-DzPKRyCtc|5F6Pclk^PEL#d@BVc.bRSSR4zR@BfFOp5ZYd4P5Y(PJFYTyVf)4FBc%z+q#|CV)8/&AD)Gz;NR2K*|JR/B6FK/YzVdpW:B8X+MJPLbczP4GZqP&(tc))KB|5FBcjS8+1AWfPJNPk_K<|B^+",
				"dM#fVc.b(Ptyq*9N@TYJzc4kODA_3R+)LW>|5F.HER+FB%O7c#Wk*7Tbl+%:T^8+P^5lU+jL%:Y1NM|6kT+YSq|5FBc+y+W6L_V+>7tOjC)F+OR+8-;>jV8/#N_&|+.<<8P.lzZ47A|k9F.|YBfKRc+U(tGMHlGp/^Hp+F;>2b-5b&FBcp+zB|*-1l-A_X<G+#^kE-*|cBAJy69qP;Ol+zK84*|JR.<M+S8Sq2N^p+|TVyqykV78dR+zWcBJcBl2p+dONO&pzZF+RB_pB>z)dU%qG(DO%Zp;CpXl;UWq+*#5t3ptAF#<z>dO(XVcR;BZlkTS_F^Ut2+p.<z3BO6<",
				":cLE1L)O5E*9k_>z;)l|6HcD%D9BWBNDF(57p8C>lFSy#Fd53lcN|jO(dTF;_Udcy&ycC.1F2MSd;7qF2BZ289Y&>H.pZ8*-ckB2RyljKbR@F%U|T5C8R5YD3pB>CX-cA42C;bG@Df2UPSFbtR4zZ#dA/K;F|pK#|bL+DXU5|)-PZHER-*.JqUV^PX-&cFtGfB;c|5Fc/c3<AFjp:P>BVc.bP+@R24R_<F.TD5MqP2.5);/JCz&k(UR2FBcE;L2:|B(R&3_FKP(#E5Ppy*|JRk5HF;NFW5RDDC69lNX5.Sbcz|2&M2F>;;tPPTB|5FBcZ+9LbFDZF5J|&Y5pCB9k",
				")3@VbZ|MkG_78W9SC2Fk*-X_L4y+9A_X6_d(:|Uf)_3zZ1D;^c__KE<X#RjXY#TOW|zfMK4lzTd(FRX%<-*_Y#PcB;+JGJ^HHV*D5V#;K|K_#C/UESy&_jzNc2dzqZ2FBcb>pFG6dMK&<X#R|pF./|5F<-4k2y9PJO4_cLq>p-&|B:418|YB3Vc.bqz2-+kf7>*|JRU_2><X^RNVz|8_jXY&TOWfK9WU@PzSk_X%RdCdM)U&l:+zOftMy7K_jWcX_:#7)%_LWU7ORkXSy#<b#F2y9969K_/c#_7Z@VFT)2_|pCUPF+O|84_zPAk363L|5FBcPHER_CK|tX_%ZtNY",
				"VLRHy9|^|1+UVSzKDZ|AWN3|Ack5M.|TPzO6c975#q)DCk-*@L7GOD:XREVDqVc.b+#j.c8P5dJ|X:c&NR&PH^UkbV%HVPV.|5FBcfyV5cKR&VRqbNR:H;cWq*b^(HPHb2&3GR5V<T##OUJR@6Rfc/p&l-*|JRl34R>J|5FBc-z|>|*zZ(8;9KFpG|VcJfDzKFl:jtcAKYE27R*M.|<|YllDKVD%T7z.K*ZRWHERB6ycVO^DtfzM.|>S+.y+M9++MRMcK#*@Vk-X.V1y6bc8C*-*dVf)VSMKVH.TUL|)L1_bXPT-H@NYXpV<P_N&Eq3_V1/Z-)+34R5(qdRX:D&c",
				"GKEK6Mz1KPB^M&&zK4XP)F|Hb.FP|PFFzkK4>7P&XZ:zcU75%X*y<P#G9KO(jf7CG3DCPP6Pq#29K%/P>Bf29^TVGCP>f7*-cN)2R3XOVbd@|W#|LL/7R3TYM:Bj;p3c#l4P_td@Wy:#R>zt5+zzZ278;k2U&S3H|bN@Yp#5Pz-+#HER-*.LzfBPdp-DzPKRy)(c|5FCPc%k^PE:3djBVc.bR@@RSzRjBfFOW5ZldSP5l_PJFlVyVf+SFBcUzMq3|)1+8N&AD+Gz;LR:K*|JRNBCFKNlzVdYW/B8XMpJP2bczPSGZqP&_(c++KB|5FBc#@8MtAYfPJLPkTK<|BAM",
				"<UPKN7c%q1%Bk88FOE3(.8)77#j>UfPB#5P8HER;NR8<2(3)8cB_BS86kF8k7Vc.b4SMM|T8;z76tjc9jD8D5F*-6W)K%XO.399kpX)9FBc>G_dqb793^U%L8/W5bf;ZWOCB;|>>FYW5XO+<OyL1XUzz<-Kk278SY_9O88|<z-FOMLfBR9z3N8|5FB-X)GF;YS+P>Gj3:XMFRP8VV*:6*AVA*JA:AM|E82zZ)dA:CBSYt4HzG*|JRZ7YOpCc^N9tj>|8C8D4R1L|5FBc9j2J/5(cO7&Y39c8.WGBR6U8U8(#9D&j#(-*tL83MlYOK|@_F./z|CGz/G.-<#1Mc8B3",
				"L/3>Vc.bFY+y.*lMPWf%dc3kGzO>/RM5)t>|5F.HERMFBKG)c3;-*7#bHM_:#Z9MfZ5HtMl)K:fqM@|6k#MfS2|5FBcMyMV6)WVM<).6j5NFM8RMXEd_j;B@AM_&FM1CC392HzZ47A|k9F.|fB#jRcMtYM(M&YM:EMYpMFDKTG-5O%FBc2MND|*-NH-A<MCbq_U)H-*|cLGUy3d1Sz87MBlz4*|JR+C@MS9S.TMUpM|W;y+ykt77LRXz;cBJcB^TpMT<M5/pzPFMRBKpB>dNd;*+G(dOKZpB/1M^L4;4M8_N+%p+bp+Cz_LDjMVcRLBJ^-#S%|Z3+Tqp.CO%dO6C",
				"L3WYp7_T31T*k4JCHE@(.8)ZZ-X4#2WMt6T8HERSp/8L+(#)8c*cB#8fkF8k7Vc.b;@MB|%8>zZASlc9KD8D5F*-AO)YW<O.H9dk5<)9FBc4G_d#bZ9tlUTP8N^6O6>Z^OCBN|44Fl^5&O2LbyP1<3YzL-5k+Z8tXR9O88|Lz-G^BP%M+9zUp8|5FMN&)CGDlUjT8FK3:&BFD18V/>V>WtbRb2b2RM|E8+zZ)dA:CB#XS;HzG*|JR7ZXOjCcXp9SK4|8C8D;_1P|5FBc9X+JN5(cHZ<K#9c8.^GBRA@8U8(39D&l#(-*Sk8tMclOY|q_F.Nz|CGzNG.-L#1Mc8B3",
				"PMNVbZ|fkG#78q9SyOFk*-X#L4C29A#X6#d(:|lTP#MzZ1D;^c##KE<X_RYXU_3>q|zTfK4jz3d(FRX%<-*#U_&cB;2JGJ^HHV*D5V_;K|K#_y/lESC)#YztcOdzWZOFBcb+@FG6dfK)<X_R|@F./|5F<-4kOC9&J>4#cLW+@-)|B:418|U#MVc.bWzO-2kT7+*|JRl#O+<X^RpVz|8#YXU)3>qTK9qlN&zSk#X%RdydfPl)j:2z>TtfC7K#YqcX#:_7P%#Lql7>RkXSC_<b_FOC9969K#/c_#7ZNVF3PO#|@yl&F2>|84#zOAkM6ML|5FBc&HER#yK|pX#%ZtpU",
				"K<P)9p/)J36AHXXz:fk|B.cR#ycBFE#B2t5H;dFXkl7)c&M5qkcfj&ZtMT|:XSdc5->c<<9F;Z7d:q/F>BS7M%y>K*F4Zd*-CqB;R)k.(3#P|DS|J5|MGKyDq;+XOUtLS/4|W3G1Df4lG4F3T#/zZ4dA|K:F&;(L|bfPDUST._-RSHER-*.T2lB&GU-F^F5RNBYc|5FC<cHB%F84OGX(Vc.bRP1#7/RX+F|TD5L/G;85_:&J|_(p>ZR;FBcF:N;V|BK#tqXFO#(9&5#;t*|JRf6)FWqz2FRD(F+AkfU5E4bc_|4>S;<XYWcGGJB|5FBclP%p3FDFFJJFKyJ;<BAq",
				"4Yp3*-Z)WJMyk&_T:HL>4j783_(ycfSk9:|A4+WD#*6%5CV:J1F@PVJF2+k4;5Nz2*Yzk/ppH_R/@/pWq*-FBX)BZREff%(-pk|:EUc2N|5FBc2q|%B+ppz2Z^M|:/N6dUK_>H(RU3z-*K.#U62/+|)&RJ7#_+DdzUAYy<>9%+-TYC*RcESW5_O4fH-LC%MB+K<lzZ5d;9k4qtOU>^3Vc.bHX(BzG(DYt%fpcP+-2TlU)#*|JR9W;.Y4Lf@&8D)|54U+-8bp+LYXfD3zJkBLZyDBNf;TM_lHERHDX1^|p@UOO/BTWcXzH8CFBcUAXyRG*D.:/@|5FYB3+c.bHA+b",
				"EWRL3EPzSF<VFBcHpG*D^_@MEWB%6k%+R|lF|45JA1X.9Z.lt-f5kNqY;X9|9)U(OkEYLG)C&<6(>fXzZO7A|KO2U/@DCJHEcZBD/c-NMZG6tFtBFN;66BF|W2|C+RC2Vz-J#kX(2)*|JRAF2>||JTNfld-|Vc.bF@-*Kq|@8B-_Jc-/&S*-Kqy8LA--WR.&L2#|CFF>;1Pd23FbT#cjclE(7YX78*)Xf#96E)&X/FU.;<Pd(2yBP/XJtf;|5FBc5WHN:UFp|>H^|5FZ5%&X8lOKc+S9@ZMZ_*Wq/7@H2C%&@&z8(cRHERqdFCPP&p.pzqjR+OFJFB7dDXG9S/MO",
				".FjU&%MWXXb&A4;)zVp:TY)8ycPBRA2&@A%-*|(YXd/l>O.<tWbYq|VY576UO)G4DSc(L-)/;fPRfV-GT/Sc<_+ACNGOX7Md#fc5(X@CNBFBc<3Wc/&TT+AXBfXSpkLLfflVNfOE#AyV>5_z-(C2UKjSUNTM5_b.cVJK2fOLH/-VySHERDc@Ok%URj-f%8cTT6p.1V)GK+bfzOJ@@Jt9b|5FSp+6)3GOjMtcWNC@(f%UTAKzZ2d9;kjCOl99|^CO|OpEB|+SGKRJ|*>5XD&:z|NptY<_&q|OcO7fjj*-#dY4&FBJG/V)2fzcC@8|5FBcRyG+Y/@U&2-tKABl2XSk",
				"_VUk)Vc.b<k4|H72bV|5FBc9C;cYS*-M6S5Wf(O.EBBK@jF*PKy36Z)Hb>BR:yJH4pkC:j7H:_t*|JRM+t-+FVDz|+Oy|1c55_+WTY@|5F-*LJ+yF9W-NzFBc)lP|jRzWS;Y)GE:L+KRq:+%qNt5E+fFL-R4F/S4Z+RY.+8b;-PdRcSzFVl+L>V^7Bd9q2G5dBB1FJ#F|B|cBpB:t%UFCTX8HAZ<CMj:SGlyDNXcDF5*X)z8*pVCkB>_*3SRF5ZjDNFWkY/p2BzMG@>^PF6+L5cycb|<)X94:kO48(+ZVVWHER5:;5)|S>4f.TzZ57/8kRKBFD&*2c5POYNj3WXC",
				"VKRH/.|ELA+cVVcJ2.R6D@;|U84Oz.|c3C^14Z|5fkqVcJ-*TZ|G(d%UFVVbkVc.b+y:.cjC5d9RUScN@|N^*VYJFV2*E3V.|5FBcMj25CJRNVR1bWRfGPYDl*REtG3*|WN(*bO(k&%*X4K|T1|Md6pN>-*|JR>3#REZ|5FBc-C|#R*zZ(D:9K|p*%V5JV<dZ|+SXZYjZMVW|R*49FlF7Ll<JbdS&RC9|*.LDHER>16ct^VV9McC.bUJJU.b8b8^.bc4.S*:VJfUJVAq1RYDk*-HPVAjVyz9)*J&cJ|/J7_RUt&-GT@7UpVk3_@NEk(_V76J*qBt#|531cRUVYNc",
				"L/3>Vc.bFY+y.*lMPW:%dc3kGzO>/RM5)_>|5F.HERMFB@G)c3;-*7XbHM@fXZ9M:Z5H_Ml)Kf:#Mt|6kXMSS2|5FBcMyMV6)WVM<).6j5NFM8RMpEd@j;BtAM@&FM1CC392HzZ47A|k9F.|:BXjRcM_YM(M&YMfEMYUMFDKTG-5O%FBc2M5D|*-NH-A<MCb#Kq)H-*|cLGqy3d1Sz87MBlz4*|JR+CtMS9S.TMqUM|W;y+yk_77LRpz;cBJcB^TUMT<MN/UzPFMRBKUB>dNd;*+G(dOKZUB/1M^L4;4M8@N+%U+bU+Cz@LDjMVcRLBJ^-X:%|Z3+T#U.CO%dO6C",
				"EY(L517pSF<VFBcU)G*D)_@;AVBq6<q&M|Z#y<5J_F8.jfGtF|45VNckbXH*j|39_W6+VG:qH4dD>4/zZ_87|k_;b2@PdY+ActBD8q-N;)8AlFMB>.R5BFf|Wz|2&T%5WT-JqV/9B:*|JROF1>|yYTN4ZE-|Vc.bF@-*Wc|@DB--JcNKkS*-YK*9fO-|KR.Hf5q|6FF>3#BdT^FbK/c(CtEPz+/KDy:/4Tjdd:H2/F&.&4BAP/-BH/XWF43|5FBc5YH^z9FVy>+Z|5F)5ck/DL_Yc3S+@ZPLO*YcXX@HKd%H@Uc/PcTHERp@Fd47UZG)qT(c&_FYF7cdp1.+S8;_",
				"VKR#)98V2@tzV-cJU.byWN^|1735UK|TPU3+zkR5-CA7Uk-*;.RG(D:1|EVRCVc.btSdKcAP5MZ|1:c&Nb&P*OU9|V#H<4V.|5FBcYAS5cKR&VRCbNR:*fcWq*R<(*PHFN&PGR5P+T#*dD9Ff+RYz/p&t-*|JRl34R>9|5FBc-M|4|HzZ(7;9kFpGRV7J@zUJbl:fZUAkYOX|RLDkbCR@BlzJ2D-TRzK2*kRWHERBq)c(3<cZ@UUZ2__..__J.b_._cc9%*dV9#1kVYyCRc8C*-*;V@)VSc9-*kTMK||k@_|1jT-GfNY1p<qP_N&>63_V@)9%At(4R5(+c|1:D&c",
				"VKFH/.|ELA+cVVcJ2.R6D@;|U84Oz.|c3C^14Z|5fkqVcJ-*TZ|G(d%UFVVbkVc.b+y:.cjC5d9RUScN@|N^*VYJFV2*E3V.|5FBcMj25CJRNVR1bWRfGPYDl*REtG3*|WN(*bO(k&%*X4K|T1|Md6pN>-*|JR>3#REZ|5FBc-C|#R*zZ(D:9K|p*%V5JV<dZ|+SXZYjZMVW|R*49FlF7Ll<JbdS&RC9|*.LDHER>16ct^VV9McC.bUJJU.b8b8^.bc4.S*:VJfUJVAq1RYDk*-HPVAjVyz9)*J&cJ|/J7_RUt&-GT@7UpVk3_@NEk(_V76J*qBt#|531cRUVYNc",
				"VKRH/.|ELA+cVVcJ2.R6D@;|U84Oz.|c3C^14J|5fkqVcJ-*TZ|G(d%UFVVbkVc.b+y:.cjC5d9RUScN@|N^*VYJFV2*E3V.|5FBcMj25CJRNVR1bWRfG:YDl*REtG3*|WN(*bO(k&%*X4K|T1|Md6pN>-*|JR>3#REZ|5FBc-C|#R*zZ(D:9K|p*%V5JV<dZ|+SXZYjZMVW|R*49FlF7Ll<JbdS&RC9|*.LDHER>16ct^VV9McC.bUJJU.b8b8^.bc4.S*:VJfUJVAq1RYDk*-HPVAjVyz9)*J&cJ|/J7_RUt&-GT@7UpVk3_@NEk(_V76J*qBt#|531cRUVYNc",
				"&LRHT^@E71_zV%#<c.bT9pON>z45#A|/3c6O#^F5YWyCz+-*;L@G3dY:bUpbSVc.bB-:L#T45CZR>-c_&kj6HU#J|8YHD68.|5FBcPT-5)Lk_8bWb&k-(;#9W_kE3G3GR&j3GN54S/YH;dK<:WNfzt__B-*|JRA3qRE.|5FBc-C|qbGzZ69:^Kk_(78cJ1zC<RB%;K)t^PE&7R(p<VSN1Bl#^|d%/Vc.XH.R9HER_^Tz64Uc.1##LRd7WG2dG7GWdGdCZYG;pZ->.8PTSNd2S*-G;8PtpYCL-G+/dJ|yJPMR>4/-_:cP>_U)3M&jDW3Mp1y.pTB4qV536dX>Ycjz",
				"P(.6dVc.b>>p|G_DbV|5FBcK1L)jR*-D5A/_1lc.TBW#*YJ*O#@;^J:Zbc&:(@FGpF6EbYEG(Pf*|JR<+fEB|V;zF+)@|7c+cBBEK5-|5F-*MJB/F9ET5CFBcdD1|SRp9AL5dkE<Mtc5p<WfpJf54BOFY-dp8%Kz|BR5%&|bL-3l:#KqFV<tMcVU4^l5pBkc7+FccS|F||ctFFS(*f%Fy:N|HUZGyDMDdH<2;5Nc;FW^N:CF.|V#6WcX5_AR|*BM;2FEkj%8DWq<6_c<O|-&Y+c@)SJG:NAqDk>CJl+8VVTHER.(Lj:FK)CZ.RzZ%9/8k:c+Z;J%k)&1cj5Y;TNE",
				"VL|Hy9|^|Y+UVSzKDZ|AWN3|Azk5M.|TPzO6c975#q)DCk-*@L7GOD:XREVDqVc.b+#j.c8P5dJ|X:c&NR&PH^UkbV%HVPV.|5FBcfyV5cKR&VRqbNR:H;cWq*b^(HPHb2&3GR5V<T##OUJR@6Rfc/p&l-*|JRl34R>J|5FBc-z|>|*zZ(8;9KFpG|VcJfDzKFl:jtcAKYE27R*M.|<|YllDKVD%T7z.K*ZRWHERB6ycVO^DtfzM.|>S+.M+M9++MRMcK#*@Vk-X.V1y6bc8C*-*dVf)VSMKVH.TUL|)L1_bXPT-H@NYXpV<P_N&Eq3_V1/Z-)+34R5(qdRX:D&c",
				"XJRNy.5E%SbtlPq.c.Vyd#^_21L5;.|7_zL<q.R(6Wp11@-*3^%GOD-2OEl|/Vc.bN4:.;yO)/.R2-cU#|&LMEDZbl-HE)X.|5FBcSp-(D%b&l|Eb#_-9:zdWHRFLHLCR#U)CV5LW769:q@R:<_+Dy>&B-*|JRN)/|/.|5FBc-c|/RMzZOd:AKF>Ckl1J+DzK_TP3.Dy.YE#|R9c._<FYBTD.R;-8V;.bC@bdHERNWyDLOEtA+qq@%.2.Lzz...T.G;;@/*:lZ62jXFyW|;d<*-H:lSylPcA-HK7c.|p.+j|2L7AH3#+2>E<Oj#UFWLjX+p.6yBO4RfL<qF26DUz",
				"Vkb*yK|^|fMzVSCLDZ|AWNV|AzV5D.|TPzO6UZ/5|qycDk-*@L7GOD:XREVRqVc.b+#j.c8P5dJ|X:c&NR&PHVUkRV%HVPV.|5FBcYyV5cKR&VRqbNR:H;cWq*b^(HPHbN&PGR5OtTS*dUJR@6Rfc)p&l-*|JRl34R>J|5FBc-C|4|HzZ(8;9KbpG|VcJYUzKblVjKcyJ1E27R^c.|t|f+BD3#U%T7zZK*ZRWHERl6ycVO^DLfzc.|bb)..b.JMJM+ccK#*@Vk-XkVYy6Rc86*-*dVf)VScKVH.TUk|).1_bXPT-H@NYXpV<P_N&Ec3_VfAZ-AB34|5(qDRX:D&c",
				"Vkb*yK|^|fMzVSCLDZ|AWNV|AzV5D.|TPzO6UZ/5|qycDk-*@L7GOD:XREVRqVc.b+#j.c8P5dJ|X:c&NR&PHVUkRV%HVPV.|5FBcYyV5cKR&VRqbNR:H;cWq*b^(HPHbN&PGR5OtTS*dUJR@6Rfc)p&l-*|JRl34R>J|5FBc-C|4|HzZ(8;9KbpG|VcJYDzKblVjKcyJ1E27R^c.|t|fBBD3#U%T7zZK*ZRWHERl6ycVO^DLfzc.|bb)..(.JM9M+ccK#*@Vk-XkVYy6Rc8C*-*dVf)VScKVH.TUk|).1_bXPT-H@NYXpV<P_N&Ec3_VfAZ-AB34|5(qDRX:D&c",
				"O:<BcD)b.8*ljK-dUlV<53SkG;.qDPcUH+*|JR7<jHtC3T%D&fE|*tt3b6T3pcqbW:F&J()F7z-*VX|MO.zHER9c)pkU|5q|N-qMH%C(PP&JtG_D*-p-|j|5FD3JJ<47>H(>43K:-tlzM25.GfS-F9&lR-kF)@S2(U/:P)q#F-6Dl2_&7LNK4XOJHW^9HX48/^z<#pW15R2(JKp6GMZX&Vc.bF<1|5FBcdlVFBczqWS7q3-8Pcz/qR8.^3%&HEL@Kq4qJN|Pb@fqV6UzZ_d7|k3yRkLF/M3FXjHHXGqE/5_WZ+.H;bcj2j))jK3A^1ZF%8#U3->T5jjcXO8qVt&-",
				"-;F%tqT)&Yy8jWNG3_lM3H_fB&+zZ4d^|k%>Vj|5FBc_@HSSMCtM1A:LJD<c5FE@M1c^R#lS*_z%L_XYjX6bTS*|JRU@ZFt6WC.l_|.|__%P))@%HER#N:_1JAFB3S/p68D:L__yzR7z|5F_&+YV&p29E-O3_EV6GK9@(ZV_f-<fb1_*lZYpJXX>73FG6_:Lc-*y3_P>kp(SFBc_/-.|8:UzZD1&_E6b/:UzZGRADVc.b6tJ3AATbY_Ay.cdV|4_zZ4_|cjZTX6.-S*-B1E6b|p*1tZ-b/_Ed-)ltJ+;65RGtBcScp38DSZ>V>zZ&_35WkqZk;k2q92HKWG3dG^6",
				"+HcbL&%*bzZ2D8|CM|tz>Sqc+)3l_%XZzA8^lW8Dp_FWdpp/Eb6kdj*t.PP/GV/P@VG/HDU:W.EO)9>dK71:>O^FAD_pRy&|REUjPdRqAN*q4-J<HRy(51ytj#TRG/Ab.RN2&-X.G%)bfRk5^RJqyRpJ.6JzH;fcJLb||5FBcR-cKOj(l*9C*|JR**-3tW_9.U(j|<38NJqE|b&Tbj.k5G6#HyR|5R.52qfcJ)-*|5FBcRSc:djUMl*S|*-PbzJKVc.bXb*:8kW|JHERK#X+;cJ)Cl4MDWp@OWDp@HDdl.ySRfcJ1CG*DREN147(K>N^>tM|AA|@qWdMRkbRfc5L",
				"GA/+@tbq2L-DOcS3)DF^GKLcG/F<TU|)H@*|JR9EzHp@P_W4jE7|Kpp6BC^k&M;YL&.lJ6b.^(-*V|>fS.|HERA;bjc)|GF|//<(H_@zUU&Np:<4*-lSc(|5F4kNN;8^/H6YqPyjJpqfz4G2G6dV;AcqF-cFq;LTK)XjUq<%;-c4>4F&E5fyR#7P++Wd@DpLXM/_klY>G;T*Ny8C:fZ6jVc.b27<|5FBc9%-FBc(FMB_;kSBUD/X<Rd.YK^&HE:.y.q.NfcUb27FS#)zZ39^|c61.>GFXfTC#fHHcGF%X5;M9@RH/D|/4(qb/cPJKOZ.EL6)*Sd_Gzz#>:F.JpjV",
				"M@z3*O+^9JTtkH_PfLyX4jY83_%9KlEOzf7:y6WDS(Lj1|VfJdFN;VJF2+kyYR>z24Xzk/pp7T5/>/pl^*-.B&XBVR)WW5qZpO|fEUc2>|5FBc23|1B+ppz2Z89|f/>NdUKyXH%RU^|-*K_SU1M/+p4LHJYSO+Dd#U:X94@|R+-yXC(17)EW5MO4tH-TCJ_B</#qzZ5d;7k4^NRUX83Vc.bH&(Bz2(DX75t<cP+KbTqU#S*|JRNlY.X4yt>H7D#|14B+-7b<+yC:WD^zJOByZlDB>tYTM_1HERHK&c@4P>UOO/BMWc:zN>2FBcU&:lRG*D.f/A|5FXB3+c.bz:+b",
				"LVR^5KBzSF<VFBcPZG*D9A@MLWB#LJ%+R|Zt|45JAt2.YZ.3F|f5kNqY;XY|9)|(OkdHLG)C&<6(>fXzZO7A|KO8U/@DCJYEcZBD/q-NMZN^6FtBFNFGGBB|W2|C+RC2Vz-J#kX(B)*|JR_F2>||JTNfld-|Vc.bF@-*kq|@8B-_JcN/HS*-Kqy85_--kR.Hl5c|LFF>;1BdOpFbT7cjclE(7&X28*)Xfq96C)9X/FU.;<Pd(2yBP/XJFf;|5FBc5WHN:UFp|>H^|5FZ5%YX3l_Kc+S9@ZMZA*Wq/7@H2C%H@Yz8(cRHERqdFCPB&l.pzqjR+OFkFB7dlXG9S/M_",
				"-;F%fqT)&Yy8jWNG3_lM3H_fB&+zZ4d^|k%>Vj|5FBc_@HSSMCtM1A:LJD<c5FE@M1c^R#lS*_z%L_XYjX6bTS*|JRU@ZFt6WC.l_|.|__%P))@%HER#N:_1JAFB3S/p68D:L__yzR7z|5F_&+YV&p29E-O3_EV6GK9@(ZV_f-<Fb1_*lZYpJXX>73FG6_:Lc-*y3_P>kp(SFBc_/-.|8:UzZD1&_E6b/:UzZGRADVc.b6tJ3AATbY_Ay.cdV|4_zZ4_|cjZTX6.-S*-B1E6b|p*1tZ-b/_Ed-)ltJ+;65RGFBcScp38DSZ>V>zZ&_35WkqZk;k2q92HKWG3dG^6",
				"8%lH/7|EbMLcVVc.d%1jD@;lU4t5CZ|&3)<Ac.bOf1/4CX-*PKlG(d-UFEVRkVc.b+y:.zqt5d9RUScN@|N<HVc.FVyGV3V.|5FBc7/2Oc.RNVRkb@RfGPcD1CREtGt^lWNV^FO(k&-8PcK|T1|MzqpNL-*|JRB3#RE%|5FBc-C|#>*zZ(D:9KFp^lVcJMCYX>B%TZ)j9M^W>RGYXb1>ML+c.bd2&R49|G%RDHER61/ct<E4.7cY.l&&.J.6..W@..zz.SH:VZfUXVMq1RcDO*-HPV7jVyV.fGK&z.|/9M_>UtV2GTWMUpEk3)@NVk(_VM/.*jL9#|O31YRU2YNd",
				"8KbH/.|EbALCV|Y.Y.R6D@;|U44OV.|c34tl<J|5fkq)zJ-*TJbG(d%UFVVbkVc.b+y:Zcjt5d9RUScN@|N^*VYJFV2*E3V.|5FBcMj25CJRNVR1bWRfGPcDl*REt*3*|WN(*bO(k&%*XcK|T1|Md6pN>-*|JR>3#REZ|5FBc-C|#F*zZ(D:9KRpV%VcJV<d.|+SX9<jKMEWbR*C9F1FMLl<KRd2&RC9|*JRDHER>16ct^VVJMcC.bUJ8bJbKbbJ.Mc49S*:VJfU.VAq1RYDk*-HPVMjVLz9)HJ&cJ|/JM_RUtp-GT@7UpVk3_@NEk(_VM6J*qB9#|531cRUVYNc",
				"VJbH/.|EbALCVSY.Y.R6D@;|U44Oc.|c34tl<J|5fkqVzJ-*TJbG(d%UFVVbkVc.b+y:Zcjt5d9RUScN@|N^*VYJFV2*E3V.|5FBcMj25CJRNVR1bWRfGPcDl*REt*3*|WN(*bO(k&%*XcK|T1|Md6pN>-*|JR>3#REZ|5FBc-C|#F*zZ(D:9KRpV%VcJV<d.|+SX9<jZMEWbR*C9F1FMLl<KRd2&RC9|*JRDHER>16ct^VVJMcC.bUJ#bJbKbbJ.Mc49S*:VJfU.VAq1RYDk*-HPVMjVLzK)HJ&cJ|/JM_RUtp-GT@7UpVk3_@NEk(_VM6J*qB9#|531cRUVYNc",
				"TzZ399:ktf&U2d^_yK/-HTB4ED5K8F)N7(Xp/l%c@G&J89LqD/V>tECF+N^<zS_p+F*_SBOj)TJ.E&C#qPK;A|VJF7zyKGWHFfSOG12H.K3OJ+J+2yFGp_>YE_5Jc-(EyPEpkNf_p8XAAyB/9D5N;;N>3UtWZJY/Z@PMTZ&UWL-CV.+||PKGGpF|#5-J8-.UOl%+J+^>t|:8AyL^V@21OVy*BcM^OV1;|%CEJ_7%UOTdSG2dCMfWTZPUWLXC9Kz;;GXK8|26WlL*-Kcb.+lRK-*8D%cG|KP|.+VyJb&PkWG6FBc|2clLB|*P|5F(bK+b.cVRJ|*KP|5FBc|HERGM",
				"TzZ399:k9f&>2d*_y@O2HT@4EPZB+#)NR(X</V%PKG&@czRq_/V>tECM+N^<zS_p8F*_SBOb)TJtE&C#qPKPA|VJF7zyKGWHFfSOG12H.KCOJ+J+2yFGU_>YE_5Jc-(EyPEpkNf_p+XAAyK/9DCN::N<3UtWZJ6/Z@PMTZ&UWL-CVF.#pbbbpE#bV5-J8M2U/V%8K8D>t3:2ARL^lJ.1Oly*B+M^OV1;#%CEK_7%UOTdSG2dCMfWTZPUWLXC9Kz;;GXK8|2YWlL*-Kcb4+lRK-*8D%cG|KPb.+VyJb&PkWG6FBc|2clLB|*P|5F(bK+b.cVRJ|*KP|5FBc|HERGM",
				"VJbH/.|EbALCV|Y.Y.R6D@;|U44Oc.|c34tl<J|5fkqVzJ-*TJbG(d%UFVVRkVc.b+y:Zcjt5d9RUScN@|N^*VYJFV2*E3V.|5FBcMj25CJRNVR1bWRfGPcDl*REt*3*|WN(*bO(k&%*XcK|T1|Md6pN>-*|JR>3#REZ|5FBc-C|#F*zZ(D:9KRpV%VcJV<d.|+SX9<jZMEWbR*C9F1FMLl<KRd2&RC9|*JRDHER>16ct^VVJMcC.bUJ8bJbKbbJ.Mc49S*:VJfU.VAq1RYDk*-HPVMjVLzK)HJ&cJ|/JM_RUtp-GT@7UpVk3_@NEk(_VM6J*qB9#|531cRUVYNc",
				"TzZ399:kEf&U2d^_yK/-HTJ4ED5K8F)N7(Xp/l%c@G&J89LqD/V>tECF+N^<zS_p+F*_SBOj)TJ.E&C#qPK;A|VJF7zyKGWHFfSOG12H.K3OJ+J+2yFGp_>YE_5Jc-(EyPEpkNf_p8XAAyB/9D5N;;N>3UtWZJY/Z@PMTZ&UWL-Cl.+||PKGGpd|#5-J8-.UOl%+J+^>t|:8AyL^V@21OVy:BcM^OV1;|%CEJ_7%UOTdSG2dCMfWTZPUWLXC9Kz;;GXK8|26WlL*-Kcb.+lRK-*8D%cG|KP|.+VyJb&PkWG6FBc|2clLB|*P|5F(bK+b.cVRJ|*KP|5FBc|HERGM",
				"TzZ399:ktf&U2d^_yK/-HTJ4ED5K8F)N7(Xp/l%c@G&J89LqD/V>tECF+N^<zS_p+F*_SBOj)TJ.E&C#qPK;A|VJF7zyKGWHFfSOG12H.K3OJ+J+2yFGp_>YE_5Jc-(EyPEpkNf_p8XAAyB/9D5N;;N>3UtWZJY/Z@PMTZ&UWL-Cl.+||PKGGpd|#5-J8-.UOl%+J+^>t|:8AyL^V@21OVy*BcM^OV1;|%CEJ_7%UOTdSG2dCMfWTZPUWLXC9Kz;;GXK8|26WlL*-Kcb.+lRK-*8D%cG|KP|.+VyJb&PkWG6FBc|2clLB|*P|5F(bK+b.cVRJ|*KP|5FBc|HERGM",
				"TzZ399:k9f&>2d*DyKOPHT@4EPZB8#)NR(X</V%PKG&@czRq_/V>tECM+N^<zS_p8F*_SBOb)TJtE&C#qPKP_|VJF7zyKGWHFfSOG12H.KCOJ+J+2yFGU_>YE_ZJc-(EyPEpkNf_p+XAAyK/9D5N::N<3UtWZJ6/Z@PMTZ&UWL-CNp#NBbpb)lNb#5-J8M2U/V%8J8D>t3:2ARL^lK21OVy*B+M^OV1;#%CEK_7%UOTdSG2dCMfWTZPUWLXC9KN;;GXK8|2YWlL*-Kcb4+lRK-*8D%cG|KPb.+VyJb&PkWG6FBc|2clLB|*P|5F(bK+b.cVRJ|*KP|5FBc|HERGM",
				"VJbH/.|EbALCV|Y.Y.R6D@;|U44Oc.|c34tl<J|5fkqVzJ-*TJbG(d%UFVVbkVc.b+y:Zcjt5d9RUScN@|N^*VYJFV2*E3V.|5FBcMj25CJRNVR1bWRfGPcDl*REt*3*|WN(*bO(k&%*XcK|T1|Md6pN>-*|JR>3#REZ|5FBc-C|#F*zZ(D:9KRpV%VcJV<d.|+SX9<jZMEWbR*C9F1FMLl<KRd2&RC9|*JRDHER>16ct^VVJMcC.bUJ#bJbKbbJ.Mc49S*:VJfU.VAq1RYDk*-HPVMjVLzK)HJ&cJ|/JM_RUtp-GT@7UpVk3_@NEk(_VM6J*qB9#|531cRUVYNc",
				">(f18&DYtd&@4TfPEY25@U7Dl||+O|R+-A84/5S3)P-*-MS|5FBcqBME2/|S3*zS4A8G)Dq44pzZ)D^;K|*>Ff8dd&EHf&E->j*->J4_Fd)-3EC#-FB|5FBc)Ut)AbL&D*DL+*fb*@UbFE1%->HER#^d&-d-/A-Dbd-4--7cW--|/NACC|194Y3<b1S3XS/9zb(.b.cV>>%11&Tb:qLdpDH#cJZ6|bAR*bbRJ|*-b.#c9U6bYOVbbOW|*CbbdL-6^#9b3dLLJDHW8Tyy(d_#UMbyERK:9&@l)8U8d&@HyMd1k&OHDEcJ8:E1*%CENjF6z/MUM/9P99MM/Py6&+lD",
				"TzZ399:k9f&>2d*DyKO2HT@4EPZB8#)NR(X</V%PKG&@czRq_/V>tECM+N^<zS_p8F*_SBOb)TJtE&C#qPKP_|VJF7zyKGWHFfSOG12H.KCOJ+J+2yFGU_>YE_ZJc-(EyPEpkNf_p+XAAyK/9D5N::N<3UtWZJ6/Z@PMTZ&UWL-CNp#NBbpbblNb#5-J8M2U/V%8J8D>t3:2ARL^lK21OVy*B+M^OV1;#%CEK_7%UOTdSG2dCMfWTZPUWLXC9Kz;;GXK8|2YWlL*-Kcb4+lRK-*8D%cG|KPb.+VyJb&PkWG6FBc|2clLB|*P|5F(bK+b.cVRJ|*KP|5FBc|HERGM",
				"tzZ@78:kNLDUPF:&fB/bHTp.EDCSP^)ERG-U/V%PKPjpc>_W&/+UEE|MLz:k8_&UL<(jyJq@HTB9F&C|WjJ;j-VJMREyJPqHMy*/6X9).K#Op7pL@_fG|&UYNj#JcFH5yc-Ukzy&UPF*jyK<NjZ>;;E|dUtqZpYqZJPMTCjUO_-C.9b9P7b39c9cc6-J7|9Uq+%_P7jUt|P;Dy_(VS9XO+_AJPM:q+X;d%Z>JjR%U/Td_G@dCMyqTZ&U/RbC2BE;;G<B^|9Yq1R*-Kcb9l+RS-*cj%4G|KlM.lOfJ-:Pkq6GFBc@9c+yB|*P|5F6MJ4b.cVRJ|*J4|5FBcdHER6M",
				"VLRHy9|^|Y+UVSzKcK|AWNV|Azk5M.|TPzO6c975#q)DCk-*@L7GOD:XREVDqVc.b+#j.c8P5dJ|X:c&NR&PH^UkbV%HVPV.|5FBcfyV5cKR&VRqbNR:H;cWq*b^(HPHb2&3GR5V<TS*jUJR@6Rfc/p&l-*|JRl34R>J|5FBc-z|4R*zZ(8;9KFpG|VcJfczK7l:jtcAKYE27R*M.|<|YlBDKVz%T7z.K*ZRWHERB6ycVO^DLfzM.|>S+.M.M9<+MRMcK#*@VZ-X.V1y6bc8C*-*dVf)VS3KVH.TUL|)L1_bXPT-H@NYXpV<P_N&Eq3_V#OZ-)+34R5(qD|X:D&c",
				"WK|*JK|||1UfWyfJf9|jD@l|8z>4z9|l>z(7z935-76zY9-*t9bGOdy8FEV|7Vc.bpy;qcj<4YJR8yc^@F^>GEC.|W&*/>W.|5FBc16y5zZ3PV|)b@V#HXzDk*RE>G>HM@PO1M5>7LyHXzKVt)FAc6:^B-*|JRS<%VEZ|5FBc-C|%MHzZOD:9C|:HMWdJAdY.VS#X9Y6q9V@|RHdK|)||SpYK|d&LRzZFHqMDHERp)jz>><d.Azc9MBK)2f))LMf)Rzc.-*;V.yNqWA/kRYD)*-OXWA6W#z.#GKlc+||9A_F8TLyGt@1N:EkO_@PE7>_V1j+*6UO%35>)c#N4d^z",
				"TzZ399:k9f&>2d*_y@O2HT@4EPZB+#)NR(X</l%PKG&@czRq_/V>tECM+N^<zS_p8F*_SBOb)TJtE&C#qPKPA|VJF7zyKGWHFfSOG12H.KCOJ+J+2yFGU_>YE_ZJc-(EyPEpkNf_p+XAAyK/9DCN::N<3UtWZJ6/Z@PMTZ&UWL-CVF.#pbbbpE#bV5-J8M2U/V%8K8D>t3:2ARL^lJ21Oly*B+M^OV1;#%CEK_7%UOTdSG2dCMfWTZPUWLXC9Kz;;GXK8|2YWlL*-Kcb4+lRK-*8D%cG|KPb.+VyJb&PkWG6FBc|2clLB|*P|5F(bK+b.cVRJ|*KP|5FBc|HERGM",
				"TzZ399:k9f&>2d*_y@O2HT@4EPZB+#)NR(X</l%PKG&@czRq_/V>tECM+N^<zS_p8F*_SBOb)TJtE&C#qPKPA|VJF7zyKGWHFfSOG12H.KCOJ+J+2yFGU_>YE_ZJc-(EyPEpkNf_p+XAAyK/9DCN::N<3UtWZJ6/Z@PMTZ&UWL-CVF.#pbbbpE#bV5-J8M2U/V%8K8D>t3:2ARL^lJ21Oly*B+M*OV1;#%CEK_7%UOTdSG2dCMfWTZPUWLXC9Kz;;GXK8|2YWlL*-Kcb4+lRK-*8D%cG|KPb.+VyJb&PkWG6FBc|2clLB|*P|5F(bK+b.cVRJ|*KP|5FBc|HERGM",
				"j*15:DzB8<DdHL;8yB3%dZ6zlB|5W*fK*B:lN5(SJ+-*LFk|5FBcJpFztN/BS%(klk:G2d>##DzZ1d;^K5VpF;:__GUl;D>YpP*-p&l-F72-S&@V-Fk|5FBcOZ8zkz|DzVdz|V)pVdZpM>87-pHERV)_D-_zNBVdp_3S-Rkc2-Y2NHkLL>|UH.SVb9BSF^N>(jMBb.cV;b_t9DYpW1/_DzAVcEZ7ppk|*ppRJ|*@p.%c1Z_pkf%pqfy|V-bp7z-7;%1pS7XXJd#y:YPP47t%Z4pP1R|W^DdL>+Z:_D(HO4_O_DfH(&CJ+W23%L-<APF_HH4R46SHSSSVRRP_D5#d",
				"VJbH/.|E|ALCV|Y.Y.R6D@;|Uc4OV.|c34tl<J|5fkqVzJ-*T.bG(d%UFVVbkVc.b+y:Zcjt5d9RUScN@|N^*VYJFV2*E3V.|5FBcMj25CJRNVR1bWRfGPcDl*REt*3*|WN(*bO<k&%*XcK|T1|Md6pN>-*|JR>3#REZ|5FBc-C|#F*zZ(D:9KRpV%VcJV<d.|+SX9<jKMEWbR*C9F1F7Ll<KRd2&RC9|*JRDHER>16ct^VVJMcC.bUJ8bJbKbbJ.Mc49S*:V.fU.VAq1RYDk*-HPVMjVSzJ)HJ&cJ|/J7_RUtp-GT@7UpVk3_@NEk(_V76J*qB9#|531cRUVYNc",
				"5ZfGA.WE;j&TKYdX7JbA/^OV4735D8|1<M<y%K+5Lp^T7X-*l)WGOdY4+EK+yVc.b&Yl)SCk5cXW4Lc_^b_2HESJ;#L*E<K.|5FBcjAK5SZt_#+yb^FYHlM9yHqE3*O(W^_k(b5Oy1L*lSZ+:y+jcCP_N-*|JRB<@;E)|5FBc-T|@RHzZ29:8K+PGt#KJ6Sz8FBYl.c>TjE^VR(c8Fyqj&BSD|Tk12_X|U)b9HERNpAS22EM.67SXW|VFFRccG||LRMd)YG:K)K4ZK6CyFS/p*-(:#jC#Y%ZY*Z1S.|AJjfW4O1k(l^64PEykf^_Ey2f16A.YAB3@+5Kytq4LM_K",
				"6C%P6jSq<y>tYC:J_@OH_^A*-kEK7TfA@^yX-.b%|^R;@9d/W_qZ8423SjCpdGAT%#;Nq7fdPCy*zZ*DA:CHb>EMXVlWP3+8Y&WlZlY4GGyKjSW7dVc.b>5C<A|5FBcC^_c3;N6+MLb(|W&%AK7.f;:8T-1R81_)1WPHb7WF(-E#2tG#l7HERB7^p9Vp%_|5FBc)(*|JR>NMXb&jpZ+Ep(|Of@Dk.UV<V@OD(jOJfAM9ZCJSK-:Hp:blR3#>ZB5pBc:_C<|H/G)dP;TyG9HZW8-*7OydS3N9q.b;:_CLJ2JVdp67E(2_l_(1#c/T1;9/8j<5>)5*ylZT9|fP9E1A",
				"TzZ399:k9f&>+d*DyKO2HT@4EPZB8#)NR(X</V%PKG&@czRq_/V>tECM+N^<zS_p8F*_SBOb)TJtE&C#qPKPA|VJF7zyKGWHFfSOG12H.KCOJ+J+2yFGU_>YE_ZJc-(EyPEpkNf_p+XAAyK/9D5N::N<3UtWZJ6/Z@PMTZ&UWL-CNp|NBb>bplNb#5-J8M2U/V%8J8DUt3:2ARL^lK21Oly*B+M^OV1;#%CEK_7%UOTdSG#dCMfWTZPUWLXC9Kz;;GXK8|2YWlL*-Kcb4+lRK-*8D%cG|KPb.+VyJb&PkWG6FBc|2clLB|*P|5F(bK+b.cVRJ|*KP|5FBc|HERGM",
				">zZ379:kzfDUPd*SyJOXHT@4EDZJ+F)NR(#p/V%PKG&@PzRqS/lptE5M+N^kz_SU8F*S_KOM)TJ.E&CFqPK:A|VJ|7zyKGWH-f_OG12H.KCOJ+J32yFGUD>YES5Jc-(EyPEUkNfSp8XAAyJ/9DCN::N<3UtWZJY/Z@PMTZ&UWL-CVBl|-cbF+U2l#5-@8-2UOV%+J+D<t|:8ARL^VJ21/Vy^J+M^/V1;-%5EJA7%UOTd_G2dCMfWTZAUWLXC9Kz;;GX^8|2YWlL*-Kcb4+lRK-*8D%cG|KPb.+VyJb&PkOG6FBc|2clLB|*P|5F(bK+b.cVRJ|*KP|5FBc;HERGM",
				"G5%%cZ)lKB@4O9XFU)<EL#9k5Mq(pPcUH6*|JREEjH_+#%7D&ET|*#_#46%3&BK8W2(>J@lNTJ-*Xk6O-.MHERfK)&6U|GF|M-KMH%C*PP&X_G(D*-&J|O|5Fp3VJ<4%zH@B)*92-_ljMp5qG38AFf&l.-kR))^D3U2>PlR%K-cDbp(27Gj9SW(Xq(RX.BtB/^:7*>W5GNp#A92c;:d62Vc.b(E6|5FBcdl-FBcjq8W7<3X8PlO/K18.W3%2HjL.9FlNJ3cPbpERA6UzZ_d7|c#yKkLF/MpR|jHHk5O7/GqBZ+.H:45jpj))M937^kZKEW*U#XSTLMzkk;NRX#&-",
				"VJbH/.|E|ABCV|Y.Y.R6D@;|Uc4OV.|c34tl<J|5fkqVzJ-*T.bG(d%UFVVbkVc.b+y:Zcjt5d9RUScN@|N^*VYJFV2*E3V.|5FBcMj25CJRNVR1bWRfGPcDl*REt*3*|WN(*bO<k&%*XcK|T1|Md6pN>-*|JR>3#REZ|5FBc-C|#F*zZ(D:9KRpV%VcJV<d.|+SX9<jKMEWbR*C9F1F7Ll<KRd2&RC9|*JRDHER>16ct^VVJMcC.bUJ8bJbKbbJ.Mc49S*:VJfU.VAq1RYDk*-HPVMjVSzJ)HJ&cJ|/J7_RUtp-GT@7UpVk3_@NEk(_V76J*qB9#|531cRUVYNc",
				"VKRH/.|ELA+cVVcJ2KR6D@;|U84Oz.|c3C^14J|5fkqVcJ-*TZ|G(d%UFVVbkVc.b+y:.<jC5d9RUScN@|N^*EYJFV2*E3V.|5FBcMj25CJRNVR1bWRfGPYDl*RVtG3*|WN(*bO(k&%*X4K|T1|Md6pN>-*|JR>3#REZ|5FBc-C|#R*zZ(D:9K|p*%VcJV<dZ|+SXZYjZMEW|R*49FlF7LlOJbdS&RC9|*.LDHER>16ct^VV9McC.bUJJU.b8b8^.bc4KS*:VJfUJVAq1RYDk*-HPVAjVyz9)*J&cJ|/J7_RUt&-GT@7UpVk3_@NEk(_V76J*qBt#|531cRUVYNc",
				"5q)_+l4D9SS>q3U.3Vq&pHERJNPG2DLCq(VPdXqPVV<8yq/24@2VNCD4fFNYy&.byG-*.CF|3C.pbSX4.F.H|dyFJbyDbyVc.bV4Rt&qH98|5FBcX-d3c9qV8qXXDpFy.JCF+qG3CqRbGVqR1byf5Vf-L/Vq|p^FtJGFBc.bGA-qYK8|5F;f--5q#f-^p:WB+:>5VC:cfb11Ct5k::6O.A6fccBffdyc7c2ddc*|JR6cDt+z<1qUYO^|+Wq.Bt#*byq>/BY-Jt.+-*<4jK#LGq/C8RqzE8tpMkk-A1UFc|dOd|WR*>LzZ_8|AK*-:TB;Ut<%%TRE1q7GO/Eq#R;B",
				"#/8E%K7WOXXG/3H.3Zf9pHERcBbG6DVz/Ay-dSf-*Vt8^43Y>&Y*qzD>lF:#ZW.-1G-*.25b3MBpPXX>;2.H|d1_5PZDP1Vc.b*>J)W4zE8|5FBcXWdBjKFL8/SXDpFZ.c#2%4Y3z4cPG*4cqby5DV5WV31/Dp*_)RGFBczbYA-/UK8|5F(@)DYfY5-Lp21:%FGf3z:j#Pq:zW5X_F7K;(>>>f+pLRZpRpZ>11*|JR7cD9%UE:4UHE8|%1/.:)YV|Z4Y3BM-JW.%:VO>VEG^5/3C.R4#E8dp4XX|VBU2R|dKdD1ctG*zZO8|AK*-FPBBH)O++PRE(/2GKNEfYj;q",
				">zZ379:kzfDUPd*SyJOXHT@4EDZJ+F)NR(#p/V%PKG&@PzRqS/lptE5M+N^kz_SU8F*S_KOM)TJ.E&CFqPK:A|VJ|7zyKGWH-f_OG12H.KCOJ+J32yFGUD>YES5Jc-(EyPEUkNfSp8XAAyJ/9DCN::N<3UtWZJY/Z@PMTZ&UWL-CVBl|-cbF+U2l#5-@8-2UOV%+J+D<t|:8ARL^VJ21/Vy^J+M^/V1;-%5EJA7%UOTd_G2dCMfWTZAUWLXC9Kz;;GX^8|2YWlL*-Kcb4+lRK-*8D%cG|KPb.+VyJb&PkOG6FBc|2clLK|*P|5F(bK+b.cVRJ|*KP|5FBc;HERGM",
				"tzZ@78:k>LjUPF&DfB/dHTJ3>jC2P^)NRGbU4+%^KPSpc>RW6qVUtEZMcz:k8_DUP@(DyJO-1TB.FSC|WjE;j-+JMREyJ6qHMy_/2X3)3K#/JcJ7@_fGU&UYNj#Jc|H>yc-UkzySUPF*PyK/NjZ>;PEUdUtqZpYqCJPFTCjUq_-519DVc+c999+BV2-Bl-9UOV%lJ|DUt-P;P_yAVJ9X/VL&BcM*q+X;d%Z>Jjy%UqTd_G@dCMyqTZjU/Rb<>BE;;G<p^|.Yq+R*-Kcb3l+RK-*cD%4G|KpM.l+RJb:Pkq6GFBc@3c+yB|*P|5F6MB^b.cVRJ|*J7|5FBcdHER6M",
				"CHc41fl*4zZ2D8|CF|-X;bqc+L|GPb2Zz98FOD8W6@FWW__.%bRkdjPt.GU5:7VV5VVVqDtMW.Et)|;dK7t*7(G9AD66Ry|CREUjYdRqWNlqJAJ<H.ydJL%dp|#RM/Wb.RN23C<.l|1b>REJ|.JqyR65.TJzH>>cJLS||5FBcT-clNpt;|P+*|JRO|-|U9@|T#(jCX|8dJqE|b3&SpTkJ*R5Hy/_5R.52q^cJL-*|5FBcRCcl&jd:|Yb|*-|bX5GVc.bXb#M8kD6JHERlJz+^cJLCMlFDA66K9W_6HWp:.ySR>c5)bG4D.ENLO;d|;d|7tOFWW|6HWdMR%bt|cJ1",
				"tzZ@78:k>LjUPF&DfB/dHTJ3>jC2P^)NRGbU4+%^KPSpc>RW6qVUtEZMcz:k8_DUP@(DyJO-1TB.FSC|WjE;j-+JMREyJ6qHMy_/2X3)3K#/JcJ7@_fGU&UYNj#Jc|H>yc-UkzySUPF*PyK/NjZ>;PEUdUtqZpYqCJPFTCjUq_-519DVc+c999+B.2-Bl-9UOV%lJ|DUt-P;P_yAVJ9X/VL&BcM*q+X;d%Z>Jjy%UqTd_G@dCMyqTZjU/Rb<>BE;;G<p^|.Yq+R*-Kcb3l+RK-*cD%4G|KpM.l+RJb:Pkq6GFBc@3c+yB|*P|5F6MB^b.cVRJ|*J7|5FBcdHER6M",
				"tzZ@78:k>LjUPF&DfB/dHTJ3>jC2P^)NyGbU4+%^KPSpc>RW6qVUtEZMcz:k8_DUP@(DyJO-1TB.FSC|WjE;j-+JMREyJ6qHMy_/2X3)3K#/JcJ7@_fGU&UYNj#Jc|H>yc-UkzySUPF*PyO/NjZ>;PEUdUtqZpYqCJPFTCjUq_-519DVc+c999+B.2-Bl-9UOV%lJ|DUt-P;P_yAVJ9X/VL&BcM*q+X;d%Z>Jjy%UqTd_G@dCMyqTZjU/Rb<>BE;;G<p^|.Yq+R*-Kcb3l+RK-*cD%4G|KpM.l+RJb:Pkq6GFBc@3c+yB|*P|5F6MB^b.cVRJ|*J7|5FBcdHER6M",
				"tzZ@78:k>LjUPF&DfB/dHTJ3>jC2P^)NyGbU4+%^KPSpc>RW6qVUtEZMcz:k8_DUP@(DyJO-1TB.FSC|WjE;j-+JMREyJ6qHMy_/2X3)3K#/JcJ7@_fGU&UYNj#Jc|H>yc-UkzySUPF*PyK/NjZ>;PEUdUtqZpYqCJPFTCjUq_-519DVc+c999+B.2-Bl-9UOV%lJ|DUt-P;P_yAVJ9X/VL&BcM*q+X;d%Z>Jjy%UqTd_G@dCMyqTZjU/Rb<>BE;;G<p^|.Yq+R*-Kcb3l+RK-*cD%4G|KpM.l+RJb:Pkq6GFBc@3c+yB|*P|5F6MB^b.cVRJ|*J7|5FBcdHER6M",
				"VLRHy9|^|YbUVSzKcK|AWNV|Azk5M.|TPzO6c975#q)DCk-*@L7GOD:XREVDqVc.b+#j.c8P5dJ|X:c&NR&PH^UkbV%HVPV.|5FBcfyV5cKR&VRqbNR:H;cWq*b^(HPHb2&3GR5V<TS*jUJR@6Rfc/p&l-*|JRl34R>J|5FBc-z|4R*zZ(8;9KFpG|VcJfczK7l:jtcAKYE27R*M.|<|YlBDKVc%T7z.K*ZRWHERB6ycVO^DLfzM.|>S+.M.MR<+MRMcK#*@VZ-X.V1y6bc8<*-*dVf)VS3KVH.TUL|)L1_bXPT-H@NYXpV<P_N&Eq3_V#OZ-)+34R5(qD|X:D&c",
				">zZ379:kzfDUPd*S7JOXHT@4EDZJ+F)NR(#p/V%PKG&@PzRqS/lptE5M+N^kz_SU8F*S_KOM)TJ.E&CFqPK:A|VJ|7zyKGWH-f_OG12H.KCOJ+J32yFGUD>YES5Jc-(EyPEUkNfSp8XAAyJ/9DCN::N<3UtWZJY/Z@PMTZ&UWL-CVBl|BcbF+U2l#5-@862UOV%+J+D<t|:8ARL^V@21/Vy^J+M^/V1;-%5EJA7%UOTd_G2dCMfWTZAUWLXC9Kz;;GX^8|2YWlL*-Kcb4+lRK-*8D%cG|KPb.+VyJb&PkOG6FBc|2clLB|*P|5F(bK+b.cVRJ|*KP|5FBc;HERGM",
				">zZ379:kzfDUPd*S7JOXHT@4EDZJ+F)NR(#p/V%PKG&@PzRqS/lptE5M+N^kz_SU8F*S_KOM)TJ.E&CFqPK:A|VJ|7zyKGWH-f_OG12H.KCOJ+J32yFGUD>YES5Jc-(EyPEUkNfSp8XAAyJ/9DCN::N<3UtWZJY/Z@PMTZ&UWL-CVBl|BcbF+U2l#5-@8-2UOV%+J+D<t|:8ARL^V@21/Vy^J+M^/V1;-%5EJA7%UOTd_G2dCMfWTZAUWLXC9Kz;;GX^8|2YWlL*-Kcb4+lRK-*8D%cG|KPb.+VyJb&PkOG6FBc|2clLB|*P|5F(bK+b.cVRJ|*KP|5FBc;HERGM",
				"tzZ378:kHR&UpF&DfB/dHTp9>jC2P^)NR6bU4+%^KPjpPN_WPq+UtECMcz:k8_DUP3(DyJO-1TB.F&C|WjB;j-+JMR>yJPq1My_/2X@1.K#/JcJ73_bGU&UYNj#Jc|H>yc8UkzySUPF*DyK<NjZ>;PEUdUtqZpYqCJP<TCjUq_-#..P9cS@9k9V.7G-Bl-.Uq+%45lDUt<P;P_y:+J9X/+LAJ^M*O+X;d%Z>Jjf%UqTdLG3dCM_qTZjU/Rb#>BE;;G<p^|96q+R*-KcbEl+RK-*cD%4G|KpM.l+RJ-:PkqGGFBc39c+yB|*P|5F6MB^b.cVRJ|*J4|5FBcdHERGF",
				"tzZ378:kHR&UpF&DfB/dHTp9>jC2P^)NR6bU4+%^KPjpPN_WPq+UtECMcz:k8_DUP3(DyJO-1TB.F&C|WjB;j-+JMR>yJPq1My_/2X@1.K#/JcJ73_bGU&UYNj#Jc|H>yc8UkzySUPF*DyK<NjZ>;PEUdUtqZpYqCJP<TCjUq_-#..P9cS@9k.V.7G-Bl-.Uq+%45lDUt<P;P_y:+J9X/+LAJ^M*O+X;d%Z>Jjf%UqTdLG3dCM_qTZjU/Rb#>BE;;G<p^|96q+R*-KcbEl+RK-*cD%4G|KpM.l+RJ-:PkqGGFBc39c+yB|*P|5F6MB^b.cVRJ|*J4|5FBcdHERGF",
				"TzZ399:k9f&>2d*Dy@O2HT@4EPZB+#)NR(X</V%PKG&@czRq_/V>tECM+N^<zS_p8F*_SBOb)TJtE&C#qPKPA|VJF7zyKGWHFfSOG12H.KCOJ+J+2yFGU_>YE_ZJc-(EyPEpkNf_p+XAAyK/9D5N::N<3UtWZJ6/Z@PMTZ&UWL-CVF.|BbbNpR.l#5-J8M2U/V%8K8D>t3:2ARL^l@21Oly*B+M^OV1;#%CEK_7%UOTdSG2dCMfWTZPUWLXC9Kz;;GXK8|2YWlL*-Kcb4+lRK-*8D%cG|KPb.+VyJb&PkWG6FBc|2clLB|*P|5F(bK+b.cVRJ|*KP|5FBc|HERGM",
				"VKRH/.|ELA+cVVcJ2KR6D@;|U84Oz.|c3C^14J|5fkqVcJ-*TZ|G(d%UFVVbkVc.b+y:.<jC5d9RUScN@|N^*EYJFV2*E3V.|5FBcMj25CJRNVR1bWRfGPYDl*RVtG3*|WN(*bO(k&%*X4K|T1|Md6pN>-*|JR>3#REZ|5FBc-C|#R*zZ(D:9K|p*%VcJV<dZ|+SXZYjZMEW|R*49FlF7LlOJbdS&RC9|*.LDHER>16ct^VV9McC.bUJJU.b8b8^8bc4KS*:VJfUJVAq1RYDk*-HPVAjVyz9)*J&cJ|/J7_RUt&-GT@7UpVk3_@NEk(_V76J*qBt#|531cRUVYNc",
				"CHc41fl*-zZ2D8|CF|-X;bqc+L|GPb2Zz98FOD8W6@FWW__.%bRkdjPt.GU5:7VV5VVVqDtMW.Et)|;dK7t*7(G9AD66Ry|CREUjYdRqWNlqJAJ<H.ydJL%dp|#RM/Wb.RN23C<.l|1b>REJ|.JqyR65.TJzH>>cJLS||5FBcT-clNpt;|P+*|JRO|-|U9||T#(jCX|8d5qE|b3&SpTkJ*R5Hy/_5R.52q^cJL-*|5FBcRCcl&jd:|Yb|*-|bX5GVc.bXb#M8kD6JHERlJz+^cJLCMlFDD66K9W_6HWp:.ySR>c5)bG4D.ENLF7d|;d|7tOFWW|6HWdMR%bt|cJ1",
				"TzZ399:k9f&>4d*_LKO2HT@4ED5BPF)NS(XpOV%PKG&JczRqD/l>tECM+N^<zS_p8F*_SBOM)TJtE&C#qPKPA|VJF7zyKGWHFfSOG12H.KCOJ+J+2yFGU_>YE_5Jc-HEyPEpkNf_p+XAAyB/zD5N::N<3UtWZJ6/Z@PMTZ&UWL-ClFc|BPb|GR.l#5-J8M2pOV%8K8DptF:8ARL^l_21OVy*B+M^OV1;#%CEJ_7%UOTdSG2dCMfWTZPUWLXC9Kz;;GXK8|2YWlL*-Kcb4+lRK-*8D%cG|KPb.+VyJb&PkWG6FBc#2clLB|*P|5F(bK+b.cVRJ|*KP|5FBc|HERGM",
				"55)@+l4D9SS5q3H.3Vq&pHERJNPG2DLCq(VPdXqDV^<8yq/249GVNCD4fFNYy&.byG-*.CF|3C.pbSX4.F.H|dyFJbyDbyVc.bV4Rt&qH9^|5FBcX-d3c9qV8qXXDp:y.JCF+qG3CqRbGVqR1byf5Lf-LVVq-p^FtJGFBc.bGA-qYK8|5F;fP-5qGf-^p:WB+:>5VY:cfb11YtGk::6O.A6fBdBcf7ff7cdd7f*|JR6cDt+z<1qUYO8|+Wq.Bt#*byM>/BY-Jt.+F^<4jK#*Gq/C8RqzE8tpMkk-^1U:c|dOd|Wc*>LzZ_8|AK*-:TB;Ut<%%TRE1M7GO/Eq#R;B",
				">zZ#79:kzfDUPd*SyJOXHT@4EDZJ+F)NR(#p/V%PKG&@PzRqS/lptE5M+N9kz_SU8F*S_KOM)TJ.E&CFqPK:A|VJ|7zyKGWH-f_OG12H.KCOJ+J32yFGUD>YES5Jc-(EyPEUkNfSp8XAAyJ/9DCN::N<3UtWZJY/Z@PMTZ&UWL-CVBl|-cbF+U2l#5-@8-2UOV%+J+D<t|:8ARL^V@21/Vy^J+M^/V1;-%5EJA7%UOTd_G2dCMfWTZAUWLXC9Kz;;GX^8|2YWlL*-Kcb4+lRK-*8D%cG|KPb.+VyJb&PkOG6FBc|2clLB|*P|5F(bK+b.cVRJ|*KP|5FBc;HERGM",
				">zZ379:kzfDUPd*SyJOXHT@4EDZJ+F)NR(#p/V%PKG&@PzRqS/lptE5M+N9kz_SU8F*S_KOM)TJ.E&CFqPK:A|VJ|7zyKGWH-f_OG12H.KCOJ+J32yFGUD>YES5Jc-(EyPEUkNfSp8XAAyJ/9DCN::N<3UtWZJY/Z@PMTZ&UWL-CVBl|-cbF+U2l#5-@8-2UOV%+J+D<t|:8ARL^V@21/Vy^J+M^/V1;-%5EJA7%UOTd_G2dCMfWTZAUWLXC9Kz;;GX^8|2YWlL*-Kcb4+lRK-*8D%cG|KPb.+VyJb&PkOG6FBc|2clLB|*P|5F(bK+b.cVRJ|*KP|5FBc;HERGM",
				"+Hc-L&M9bzZ2D8|ClPtz>-qc+)&GP%zZXD8^lA8D__FWWpp/Eb6kdj*t.P@/GV/VVGG/HAU:W.kO)4>dK71:>O^FWd_pRy&|REUjPdRqDN*q4-#<H^y(51ytj|TRG/Ab.RN2&-X.G%)bfRk5^RJqyRpJ.6JzH;fcJLb||5FBcR-cKOjTl*9S*|JR^9-3tD_9.U(j|<38N#qE|b3dbj.k546#Hy.|5R.52qfcJ)-*|5FBcRSc:djUMl9S|*-PbzJKVc.bXb*:8kW|JHER%#2+;cJOCl4MDWp@OWDppHWdl.TSRfcJ1CG*DREN14>(K>N*>tM|AA|@qWdMRkbRfc5)",
				"VZF*/J)ERALzVzVKYZb6D1;)UYtO29|cY(3WYZ|5fkjc49-*T.bG(d%UFEVbkVc.b+M:.C6tOd9b8Scl@|^<HVY.FVMG73V.|5FBc7j-5CJFVVRWb@Rf*P2DW*|EtHVHR1^VGL5(k&%GXcK)PW|7z6p^>-*|JR>3#REZ|5FBc-z|#bGzZ(D:9KLpGEV3J_cCJ|+%XKCjkA^1|RHCJ|k|7Bly9bdM&R4J|*JRDHER>W62tOEc97ycZR@TJ.bbJb@2.bc4ZS*:V.f8KVAjWRcDW*-HPVAjVMz9f*J&CJ|/.7yR8t&%GX@7UpEk3N@^EW(NV76.M6Bt#|O3WzR8Vd^Y",
				"TzZ399:ktf&U2d^_RK/-HTB4ED5J8F)N7(Xp/l%c@G&J89LqD/V>tECF+N^<zS_p+F*_SBOj)TJ.E&C#qPK;A|VJF7zyKGWHFfSOG12H2K3OJ+J+2yFG<_>YE_5Jc-(EyPEpkNf_p8X^AyB/9D5N;-N<3UtWZJY/Z@PMTZ&UWL-CV.+|RPKFGp|b#5-J8-2U/l%+J+;>t|:8AyL*lJ21OVy*BcM:OV1;#%CEJ_7%UOTdSG2dCMfWTZPUWLXC9Kz;;GXK8|26WlL*-Kcb.+lRK-*8D%cG|KP|.+VyJb&PkWG6FBc|2clLB|*P|5F(bK+b.cVRJ|*KP|5FBc|HERGM",
				"VJbH/.|E|ALCV|Y.c.R6D@;|Uc4OV.|c34tl<J|5fkqVzJ-*T.bG(d%UFVVbkVc.b+y:Zcjt5d9RUScN@|N^*VYJFV2*E3V.|5FBcMj25CJRNVR1bWRfGPcDl*REt*3*|WN(*bO(k&%*XcK|T1|Md6pN>-*|JR>3#REZ|5FBc-4|#F*zZ(D:9KRpV%VcJV<V.|+SX9<jKMEWbR*C9F1F7Ll<KRd2&RC9|*JRDHER>16ct^VVJMcC.bUJ8bJbKbbJ.Mc49S*:VJfU.VAq1RYDk*-HPVMjVSzJ)HJ&cJ|/J7_RUtp-GT@7UpVk3_@NEk(_V76J*qB9#|531cRUVYNc",
				"5V7%cd)l(8@4O9AFUl<%L3B6LMq(2PcUH6*|JRTEMH_+#%TD&ET|*#_#46%3&BK8Wp(>J3lNTJ-*Xk6O-.MHER1K)&6U|GF|M-<MH%C*PP&X_G(D*-&J|O|5F2@VJ<4%zH@B4*9&-_lOM25qG3SAF1&l.-kRb)^D3Up>PlR%K-cDl2(p7;j9SW(XK(FX(RtB/^O7*>85GN2#A9&k;:d6pVc.b(%c|5FBcdl-FBcjq8W7<3X8Pl:/KR8.S3%pHTL.9FlNJ3cPb2ERA65zZ_d7|c#yKkLF/M2R|jHHkLO7/GqBZ+.H:45j2j))M937^kZKEW*U#XSTLMzkk;NRX#&-",
				"tzZ@78:k>LjUPF&DfB/dHTJ3>jC2P^)NRGbU4+%^KPSpc>_W6qVUtEZMcz:k8_DUP@(DyJO-1TB.FSC|WjE;j-+JMREyJ6qHMy_/2X3)3K#/JcJ7@_fGU&UYNj#Jc|H>yc-UkzySUPF*PyK/NjZ>;PEUdUtqZpYqCJPFTCjUq_-519DVc+c9N9+B.2-Bl-9UOV%PJ|DUt-P;P_yAVJ9X/VL&BcM*q+X;d%Z>Jjy%UqTd_G@dCMyqTZjU/Rb<>BE;;G<p^|.Yq+R*-Kcb3l+RK-*cD%4G|KpM.l+RJb:Pkq6GFBc@3c+yB|*P|5F6MB^b.cVRJ|*J7|5FBcdHER6M",
				"TzZ399:k^f&>4d*_SKO2HT@4EDZBP#)NS(XpOV%PKG&JczRqD/l>tECM+N^<zS_p8F*_SBOM)TJtE&C#qPKPA|VJF7zyKGWHFfSOG12H.KCOJ+J+2yFGU_>YE_5Jc-HEyPEpkNf_p+XAAyB/zD5N::N<3UtWZJ6/Z@PMTZ&UWL-ClFc|BPb|GR.l#5-J8M.>OV%8K8DptF:8ARL^lK21OVy*B+M^OV1;#%CEJ_7%UOTdSG2dCMfWTZPUWLXC9Kz;;GXK8|2YWlL*-Kcb4+lRK-*8D%cG|KPb.+VyJb&PkWG6FBc#2clLB|*P|5F(bK+b.cVRJ|*KP|5FBcMHERGM",
				"tzZ378:kHR&UpF&DfB/dHTp9>jC2P^)NR6bU4+%^KPjpPN_WPq+Ut8CMcz:k8_DUP3(DyJO-1TB.F&C|WjB;j-+JMR>yJPq1My_/2X@1.K#/JcJ73_bGU&UYNj#Jc|H>yc8UkzySUPF*DyK<NjZ>;PEUdUtqZpYqCJP<TCjUq_-#..P9cS@.k9V.7G-Bl-.Uq+%P5lDUt<P;P_y:+J9X/+LAJ^M*O+X;d%Z>Jjf%UqTdLG3dCM_qTZjU/Rb#>BE;;G<p^|96q+R*-KcbEl+RK-*cD%4G|KpM.l+RJ-:PkqGGFBc39c+yB|*P|5F6MB^b.cVRJ|*J4|5FBcdHERGF",
				"tzZ@78:k>LjUPF&DfB/dHTJ3>jC2P^)NRGbU4+%^KPSpc>_W6qVUtEZMcz:k8_DUP@(DyJO-1TB.FSC|WjE;j-+JMREyJ6qHMy_/2X3)3K#/JcJ7@_fGU&UYNj#Jc|H>yc-UkzySUPF*PyK/NjZ>;PEUdUtqZpYqCJPFTCjUq_-519DVc+c3+9+B.2-Bl-9UOV%PJ|DUt-P;P_yAVJ9X/VL&BcM*q+X;d%Z>Jjy%UqTd_G@dCMyqTZjU/Rb<>BE;;G<p^|.Yq+R*-Kcb3l+RK-*cD%4G|KpM.l+RJb:Pkq6GFBc@3c+yB|*P|5F6MB^b.cVRJ|*J7|5FBcdHER6M",
				"tzZ@78:k>LjUPF&DfB/dHTJ3>jC2P^)NRGbU4+%^KPSpc>RW6qVUtEZMcz:k8_DUP@(DyJO-1TB.FSC|WjE;j-+JMREyJ6qHMy_/2X3)3K#/JcJ7@_fGU&UYNj#Jc|H>yc-UkzySUPF*PyK/NjZ>;PEUdUtqZpYqCJPFTCjUq_-519SVc+c999+B.2-Bl-9UOV%PJ|DUt-P;P_yAVJ9X/VL&BcM*q+X;d%Z>Jjy%UqTd_G@dCMyqTZjU/Rb<>BE;;G<p^|.Yq+R*-Kcb3l+RK-*cD%4G|KpM.l+RJb:Pkq6GFBc@3c+yB|*P|5F6MB^b.cVRJ|*J7|5FBcdHER6M",
				"TzZ399:k9f&>2d*_y@O2HT@4EPZB+#)NR(XU/V%PKG&@czRq_/V>tECM+N^<zS_p8F*_SBOb)TJtE&C#qPKPA|VJF7zyKGWHFfSOG12H.KCOJ+J+2yFGU_>YE_ZJc-(EyPEpkNf_p+XA&yK/9D5N::N<3UtWZJ6/Z@PMTZ&UWL-CVF.|BbbEpR.l#5-J8M2U/V%8K8D>t3:2ARL^lJ21Oly*B+M^OV1;#%CEK_7%UOTdSG2dCMfWTZPUWLXC9Kz;;GXK8|2YWlL*-Kcb4+lRK-*8D%cG|KPb.+VyJb&PkWG6FBc|2clLB|*P|5F(bK+b.cVRJ|*KP|5FBc|HERGM",
				"TzZ399:ktf&U2d^_RK/-HTB4ED5J8F)N7(Xp/l%c@G&J89LqD/V>tECF+N^<zS_p+F*_SBOj)TJ.E&C#qPK;A|VJF7zyKGWHFfSOG12H2K3OJ+J+2yFG<_>YE_5Jc-(EyPEpkNf_p8X^AyB/9D5N;-N<3UtWZJY/Z@PMTZ&UWL-Cl.+|RPKFGp|b#5-J8-2U/l%+J+;>t|:8AyL*lJ21OVy*BcM:OV1;#%CEJ_7%UOTdSG2dCMfWTZPUWLXC9Kz;;GXK8|26WlL*-Kcb.+lRK-*8D%cG|KP|.+VyJb&PkWG6FBc|2clLB|*P|5F(bK+b.cVRJ|*KP|5FBc|HERGM",
				"tzZ@78:k>LjUPF&DfB/dHTJ3>jC2P^)NRGbU4+%^KPSpc>RW6qVUtEZMcz:k8_DUP@(DyJO-1TB.FSC|WjE;j-+JMREyJ5qHMy_/2X3)3K#/JcJ7@_fGU&UYNj#Jc|H>yc-UkzySUPF*PyK/NjZ>;PEUdUtqZpYqCJPFTCjUq_-519DVc+c999+B.2-Bl-9UOV%PJ|DUt-P;P_yAVJ9X/VL&BcM*q+X;d%Z>Jjy%UqTd_G@dCMyqTZjU/Rb<>BE;;G<p^|.Yq+R*-Kcb3l+RK-*cD%4G|KpM.l+RJb:Pkq6GFBc@3c+yB|*P|5F6MB^b.cVRJ|*J7|5FBcdHER6M",
				"TzZ399:k9f&>4d*_SKO2HT@4EPZBP#)NS(XpOl%P@G&JczRqD/l>tECM+N^<zS_p8F*_SBOM)TJtE&C#qPKPA|VJF7zyKGWHFfSOG12H.KCOJ+J82yFGU_>YE_5Jc-HEyPEpkNf_p+XAAyB/zD5N::N<3UtWZJ6/Z@PMTZ&UWL-ClFckBPbdGR.l#5-J8M2pOV%8K8DptF:8_RL^lK21OVy*B+M^OV1;#%CEJ_7%UOTdSG2dCMfWTZPUWLXC9Kz;;GXK8|2YWlL*-Kcb4+lRK-*8D%cG|KPb.+VyJb&PkWG6FBc#2clLB|*P|5F(bK+b.cVRJ|*KP|5FBc|HERGM",
				"tzZ378:kHR&UpF&DfB/dHTp9>jC2P^)NR6bU4+%^KPjpPN_WPq+UtECMcz:k8_DUP3(DyJO-1TB.F&C|WjB;j-+JMR>yJPq1My_/2X@1.K#/JcJ73_bGU&UYNj#Jc|H>yc8UkzySUPF*DyK<NjZ>;PEUdUtqZpYqCJP<TCjUq_-#..P9cS@.k99.7G-Bl-.Uq+%P5lDUt<P;P_y:+J9X/+LAJ^M*O+X;d%Z>Jjf%UqTdLG3dCM_qTZjU/Rb#>BE;;G<p^|96q+R*-KcbEl+RK-*cD%4G|KpM.l+RJ-:PkqGGFBc39c+yB|*P|5F6MB^b.cVRJ|*J4|5FBcdHERGF",
				"TzZ399:ktf&U2d^_RK/dHTB4ED5J8F)N7(Xp/l%c@G&J89LqD/V>tECF+N^<zS_p+F*_SBOj)TJ.E&C#qPK;A|VJF7zyKGWHFfSOG12H2K3OJ+J+2yFG<_>YE_5Jc-(EyPEpkNf_p8X^AyB/9D5N;-N<3UtWZJY/Z@PMTZ&UWL-Cl.+|RPKFGp|b#5-J8-2U/l%+J+;>t|:8AyL*lJ21OVy*BcM:OV1;#%CEJ_7%UOTdSG2dCMfWTZPUWLXC9Kz;;GXK8|26WlL*-Kcb.+lRK-*8D%cG|KP|.+VyJb&PkWG6FBc|2clLB|*P|5F(bK+b.cVRJ|*KP|5FBc|HERGM",
				"TzZ399:k9f&>2d*_y@O2HT@4EPZB+#)NR(XU/V%PKG&@czRq_/V>tECM+N^<zS_p8F*_SBOb)TJtE&C#qPKPA|VJF7zyKGWHFfSOG12H.KCOJ+J+2yFGU_>YE_ZJc-(EyPEpkNf_p+XAAyK/9D5N::N<3UtWZJ6/Z@PMTZ&UWL-CVF.|BbbElR.l#5-J8M2U/V%8K8D>t3:2ARL^lJ21Oly*B+M^OV1;#%CEK_7%UOTdSG2dCMfWTZPUWLXC9Kz;;GXK8|2YWlL*-Kcb4+lRK-*8D%cG|KPb.+VyJb&PkWG6FBc|2clLB|*P|5F(bK+b.cVRJ|*KP|5FBc|HERGM",
				"TzZ399:ktf&U2d^_RK/-HTB4ED5J8F)N7(Xp/l%c@G&J89LqD/V>tECF+N^<zS_p+F*_SBOj)TJ.E&C#qPK;A|VJF7zyKGWHFfSOG12H2K3OJ+J+2yFG<_>YE_5Jc-(EyPEpkNf_p8X^AyB/9D5N;-N<3UtWZJY/Z@PMTZ&UWL-Cl.+|RPKFGp|b#5-J8-2UOl%+J+^>t|:8AyL*VJ21OVy^BcM:OV1;#%CEJ_7%UOTdSG2dCMfWTZPUWLXC9Kz;;GXK8|26WlL*-Kcb.+lRK-*8D%cG|KP|.+VyJb&PkWG6FBc|2clLB|*P|5F(bK+b.cVRJ|*KP|5FBc|HERGM",
				"TzZ399:ktf&U2d^_RK/-HTB4ED5J8F)N7(Xp/l%c@G&J89LqD/V>tECF+N^<zS_p+F*_SBOj)TJ.E&C#qPK;A|VJF7zyKGWHFfSOG12H2K3OJ+J+2yFG<_>YE_5Jcj(EyPEpkNf_p8X^AyB/9D5N;-N<3UtWZJY/Z@PMTZ&UWL-Cl.+|RPKFGp|b#5-J8-2UOl%+J+^>t|:8AyL*VJ21OVy^BcM:OV1;#%CEJ_7%UOTdSG2dCMfWTZPUWLXC9Kz;;GXK8|26WlL*-Kcb.+lRK-*8D%cG|KP|.+VyJb&PkWG6FBc|2clLB|*P|5F(bK+b.cVRJ|*KP|5FBc|HERGM",
				"TzZ399:ktf&U2d^_RK/-HTB4ED5J+F)N7(Xp/l%c@G&J89LqD/V>tECF+N^<zS_p+F*_SBOj)TJ.E&C#qPK;A|VJF7zyKGWHFfSOG12H2K3OJ+J+2yFG<_>YE_5Jc-(EyPEpkNf_p8X^AyB/9D5N;-N<3UtWZJY/Z@PMTZ&UWL-Cl.+|RPKFGp|b#5-J8-2UOl%+J+^>t|:8AyL*VJ21OVy^BcM:OV1;#%CEJ_7%UOTdSG2dCMfWTZPUWLXC9Kz;;GXK8|26WlL*-Kcb.+lRK-*8D%cG|KP|.+VyJb&PkWG6FBc|2clLB|*P|5F(bK+b.cVRJ|*KP|5FBc|HERGM",
				"tzZ@78:k>LjUPF&DfB/dHTJ3>jC2P^)NRGbU4+%^KPSpc>RW6qVUtEZMcz:k8_DUP@(DyJO-1TB.FSC|WjE;j-+JMREyJ6qHMy_/2X3)3K#/JcJ7@_fGU&UYNj#Jc|H>yc-UkzySUPF*PyK/NjZ>;PEUdUtqZpYqCJPFTCjUq_-519DVc+c9+9+B.2-Bl-9UOV%PJ|DUt-P;P_yAVJ.X/VL&BcM*q+X;d%Z>Jjy%UqTd_G@dCMyqTZjU/Rb<>BE;;G<p^|.Yq+R*-Kcb3l+RK-*cD%4G|KpM.l+RJb:Pkq6GFBc@3c+yB|*P|5F6MB^b.cVRJ|*J7|5FBcdHER6M",
				"TzZ399:ktf&U2d^_RK/-HTB4ED5J8F)N7(Xp/l%c@G&J89LqD/V>tECF+N^kzS_p+F*_SBOj)TJ.E&C#qPK;A|VJF7zyKGWHFfSOG12H2K3OJ+J+2yFG<_>YE_5Jc-(EyPEpkNf_p8X^AyB/9D5N;-N<3UtWZJY/Z@PMTZ&UWL-Cl.+|RPKFGp|b#5-J8-2UOl%+J+^>t|:8AyL*VJ21OVy*BcM:OV1;#%CEJ_7%UOTdSG2dCMfWTZPUWLXC9Kz;;GXK8|26WlL*-Kcb.+lRK-*8D%cG|KP|.+VyJb&PkWG6FBc|2clLB|*P|5F(bK+b.cVRJ|*KP|5FBc|HERGM",
				"+Hc-L&M9bzZ2D8|ClPtz>-qc+)&G#%zZXD8^lA8D__FWWpp/Eb6kdj*t.P@/GV/VVGG/HAU:W.kO)4>dK71:>O^FWd_pRy&|REUjPdRqDN*q4-#<H^y(51ytj|TRG/Ab.RN2&-X.G%)bfRk5^RJqyRpJ.6JzH;fcJLb||5FBcR-cKOjTl*9S*|JR^9-3tD_9.U(j|<38N#qE|b3dbj.k546|HyR|5R.52qfcJ)-*|5FBcRSc:djUMl9S|*-PbzJKVc.bXb*:8kW|JHER%#2+;cJOCl4MDWp@OWDppHWdl.TSRfcJ1CG*DREN14>(K>N*>tM|AA|@qWdMRkbRfc5)",
				"3Hc-8fqL-zZ_97|CFLC_P2&cb8J+Xb_ZDM7kF%7%)t|@;t).EO#ESpN|Gl##V>VlHlVHW@TU;.y6|UP6F8S(A6|*%M)).Ej2.y>pLSd&;Tk4J*JDW.E6J|ET^LkG*d9bRR:_fb_R(b/b3GEJURJHERt5.#J_W33cj/C||5FBcR-cz6p:F+Lb*|JR+N-f:9l(dST^2_f745<YFbfKOpGyj*GA4Y#l5d.JD41cJ8-*|5FBc#bcU:^S|(NY|*-XbzJkVc.b_b&+7E;)JPE.F52b1cJ8C(UU;%)l|@MKtH%Kq.ECR3cJ8bkF;dE>/+AT(A|(A>||9*t)W^6k.EbR1cj/",
				"3Hc-8fqL-zZ_97|CFLC_P2&cb8J+Xb_ZDM7kF%7%)t|@;t).EO#ESpN|Gl##V>VlHlVHW@TU;.y6|UP6F8S(A6|*%M)).Ej2.y>pLSd&;Tk4J*JDW.E6J|ET^LkG*d9bRR:_fb_R(b/b3GEJURJHERt5.#J_W33cj/C||5FBcR-cz6p:F+Lb*|JR+N-f:9l(dST^2_f745<YFbfKOpGyj*GA4Y#l5d.JD41cJ8-*|5FBcGbcU:^S|(NY|*-XbzJkVc.b_b&+7E;)JPE.F52b1cJ8C(UU;%)l|@MKtH%Kq.ECR3cJ8bkF;dE>/+AT(A5(A>||9*t)W^6k.EbR1cj/",
				"VZF*/J)ERALzVVVKYZb6D1;)UYtO29|cY(3WYZ|5fkjc49-*T.bG(d%UFEVbkVc.b+M:.C6tOd9b8Scl@|^<HVY.FVMG73V.|5FBc7j-5CJFVVRWb@Rf*P2DW*|EtHVHR1^VGL5(k&%GXcK)PW|7z6p^>-*|JR>3#REZ|5FBc-z|#bGzZ(D:9K|pGEV3J_cCJ|+%XKCjkA^1|RHCJ|k|7Bly9bdM&R4J|*JRDHER>W62tOEc97ycZR@TJ.bbJb@2.bc4ZS*:V.f8KVAjWRcDW*-HPVAjVMz9f*J&CJ|/.7yR8t&%GX@7UpEk3N@^EW(NV76.M6Bt#|O3WzR8Vd^Y",
				"TzZ399:k9f&>4d*_SKO2HT@4ED5BP#)NS(XpOV%PKG&JczRqD/l>tECM+N^<zS_p8F*_SBOM)TJtE&C#qPKPA|VJF7zyKGWHFfSOG12H.KCOJ+J+2yF1U_>YE_5Jc-HEyPEpkNf_p+XAAyB/zD5N::N<3UtWZJ6/Z@PMTZ&UWL-ClFc|BPb|GR.l#5-J8M2pOV%8J8DptF:8ARL^lK21/Vy*B+M^OV1;#%CEJ_7%UOTdSG2dCMfWTZPUWLXC9Kz;;GXK8|2YWlL*-Kcb4+lRK-*8D%cG|KPb.+VyJb&PkWG6FBc#2clLB|*P|5F(bK+b.cVRJ|*KP|5FBc|HERGM",
				"tzZ@78:k>LjUPF&DfB/dHTJ3>jC2P^)NRGbU4+%^KPSpc>_W6qVUtEZMcz:k8_DUP^(DyJO-1TB.FSC|WjE;j-+JMREyJ6qHMy_/2X3)3K#/JcJ7@_fGU&UYNj#Jc|H>yc-UkzySUPF*PyK/NjZ>;PEUdUtqZpYqCJPFTCjUq_-519DVc+c9+9+B.2-Bl-9UOV%PJ|DUt-P;P_yAVJ9X/VL&BcM*q+X;d%Z>Jjy%UqTd_G@dCMyqTZjU/Rb<>BE;;G<p^|.Yq+R*-Kcb3l+RK-*cD%4G|KpM.l+RJb:Pkq6GFBc@3c+yB|*P|5F6MB^b.cVRJ|*J7|5FBcdHER6M",
				"tzZ399:k9R&U3F*_fj/#HT@4N&C@8X(9R6-<Ol%PKGWB+97qDOl<tzCM+N^<NS_p8b^_RB/M)TJ.E8C#q_JPW|VJF7Ny(GAHFfSAG12H2KCAJ+@PbyFGp_>Y9_ZJcF(zR+EpkNf_p8#^WSKON&59:;E>d<tO-JY/Z@8MTZ&UAL-Zl.|FJ+bFE>N.>5-24M.pAV%8jP&Utd;PWRL^lJ21Aly^jcb^Ol1PF%CEK_7%U/TdSG2dCMf/TZPUALXCzKN:;GXKP|26O2L*-Jcb.+lRK-*8D%cG|JPb.+VyJF^PkAG6FBc|2clLB|*P|5FjbB+b.cVRJ|*2P|5FBc|HER6M",
				">zZ379:k9fD>Pd*_yJOXHT@4EAZJ)F)N76#p/V%PKG&@czRq_/lptE5M+N^kzS_U8F*_SKOM)TJ.E&C#qPK:P|VJ|7zy4GWHOfSOG12H.KCOJ+J32yFGUD>YE_5Jc-(EyPzUkNf_p8XAAyJ/9DCN::N<3UtWZJ6/c@PMTZ&UWL-CVBl|@-b-GUVl#5-J8-2UOl%+J+D<t|:8A7L^V@21/V7^J+M^/V1;-%CEJA7%UOTdSG2dCMfWTZAUWLXC9Kz;;GX^8|2YWlL*-Kcb4+lRK-*8D%cG|KPb.+VyJb&PkWG6FBc|2clLB|*P|5F(bK+b.cVRJ|*KP|5FBc|HERGM",
				"TzZ399:k9f&>4d*_SKO2HT@4ED5BP#)NS(XpOV%PKG&JczfqD/l>tECM+N^<zS_p8F*_SBOM)TJtE&C#qPKPA|VJF7zyKGWHFfSOG12H.KCOJ+J+2yF1U_>YE_5Jc-HEyPEpkNf_p+XAAyB/zD5N::N<3UtWZJ6/Z@PMTZ&UWL-ClFc|BPb|GR.l#5-J8M2pOV%8J8DptF:8ARL^lK21/Vy*B+M^OV1;#%CEJ_7%UOTdSG2dCMfWTZPUWLXC9Kz;;GXK8|2YWlL*-Kcb4+lRK-*8D%cG|KPb.+VyJb&PkWG6FBc#2clLB|*P|5F(bK+b.cVRJ|*KP|5FBc|HERGM",
				"tzZ@78:k>LjUPF&DfB/dHTJ3>jC2P^)NRGbU4+%^KPSpc>RW6qVUtEZMcz:k8_DUP@(DyJO-1TB.FSC|WjE;j-+JMREyJ6qHMy_/2X3)3K#/JcJ7@_fGU&UYNj#Jc|H>yc-UkzySUPF*PyK/NjZ>;PEUdUtqZpYqCJPFTCjUq_-519DVc+c9+9+B.2-Bl-9UOV%PJ|DUt-P;P_yAVJ.X/VR&BcM*q+X;d%Z>Jjy%UqTd_G@dCMyqTZjU/Rb<>BE;;G<p^|.Yq+R*-Kcb3l+RK-*cD%4G|KpM.l+RJb:Pkq6GFBc@3c+yB|*P|5F6MB^b.cVRJ|*J7|5FBcdHER6M",
				"tzZ399:k9R&U3-*_fj/#HT@4NDCJ8X(9RGF<Ol%PjGWB897qDAl>tzCM+N^<NS_p8b^_RB/M)TJ.E8C#q_JPW|VJF7NyKGAHFfSAG12H2KCAJ+@PbyFGp_<Y9_ZJcF(zR+EpkNf_>8#^WSKON&59:;9>d<tO-JY/Z@8MTZ&UAL-Zl.|FJ+bFEpE.>5-24d4pAV%+jP&>td;PWRL^lJ21Aly^KcM^Ol1PF%CEJ_7%U/TdSG2dCMf/TZPUALXCzKN:;GXKP|26O2L*-Jcb.+lRK-*8D%cG|JPb.+VyJP^PkAG6FBc|2clLB|*P|5FjbK+b.cVRJ|*2P|5FBc3HER6M",
				"tzZ399:k9R&U3-*_fj/#HT@4NDCJ8X(9RGF<Ol%PjGWB897qDAl>tzCM+N^<NS_p8b^_RB/M)TJ.E8C#q_JPW|VJF7NyKGAHFfSAG12H2KCAJ+@PbyFGp_<YE_ZJcF(zR+EpkNf_>8#^WSKON&59:;9>d<tO-JY/Z@8MTZ&UAL-Zl.|FJ+bFEpE.>5-24d4pAV%+jP&>td;PWRL^lJ21Aly^KcM^Ol1PF%CEJ_7%U/TdSG2dCMf/TZPUALXCzKN:;GXKP|26O2L*-Jcb.+lRK-*8D%cG|JPb.+VyJP^PkAG6FBc|2clLB|*P|5FjbK+b.cVRJ|*2P|5FBc3HER6M",
				"TzZ399:ktf&U2d^_RK/-HTB4ED5J8F)N7(Xp/l%c@G&J89LqD/V>tECF+N^<zS_p+F*_SBOj)TJ.E&C#qPK;A|VJF7zyKGWHFfSOG12H2K3OJ+J+2yFG<_>YE_5Jc-(EyPEpkNf_p8X^AyB/9D5N;-N<3UtWZJY/Z@PMTZ&UWL-Cl.+|RPKFGp|b#5-J8-2U/l%+J+*>t|:8AyL*lJ21OVy*BcM:OV1;#%CEJ_7%UOTdSG2dCMfWTZPUWLXC9Kz;;GXK8|26WlL*-Kcb.+lRK-*8D%cG|KP|.+VyJb&PkWGGFBc|2clLB|*P|5F(bK+b.cVRJ|*KP|5FBc|HERGM",
				"TzZ399:ktf&U2d^_yK/-HTB4ED5J8F)N7(Xp/l%c@G_J89LqD/V>tECF+N^<zS_p+F*_SBOj)TJ.E&C#qPK;A|VJF7zyKGWHFfSOG12H2K3OJ+J+2yFG<_>YE_5Jc-(EyPEpkNf_p8X^AyB/9D5N;-N<3UtWZJY/Z@PMTZ&UWL-Cl.+|RPKFGp|b#5-J8-2UOl%+J+^>t|:8AyL^VJ21OVy*BcM:OV1;#%CEJ_7%UOTdSG2dCMfWTZPUWLXC9Kz;;GXK8|26WlL*-Kcb.+lRK-*8D%cG|KP|.+VyJb&PkWG6FBc|2clLB|*P|5F(bK+b.cVRJ|*KP|5FBcdHERGM",
				"TzZ399:ktf&U2d^_LK/-HTB4ED5J8F)N7(Xp/l%c@G&J89RqD/l>tECF+N^kzS_p+F*_SBOM)TJ.E&C3qPK;A|VJF7zyKGWHFfSOG12H.K3OJ+J+2yFGp_>YE_5Jc-(EyPEpkNf_p8XAAyB/9D5N;;N<#UtWZJY/Z@PMTZ&UWL-Cl.+>RPK#Gp|b#5-J8-2UOl%+J+^>t|:8AyL^VJ21OVy*BcM^OV1;#%CEJ_7%UOTdSG2dCMfWTZPUWLXC9Kz;;GXK8|26WlL*-Kcb.+lRK-*8D%cG|KP|.+VyJb*PkWG6FBc|2clLB|*P|5F(bK+b.cVRJ|*KP|5FBc|HERGM",
				"tzZ399:k9R&U3-*_fj/#HT@4NDCJ8X(9RG-<Ol%PjGWB897q_Al>tzCM+N^<NS_p8b^_RB/M)TJ.E8C#q_JPW|VJF7NyKGAHFfSAG12H2KCAJ+@PbyFGp_<Y9_ZJcF(zR+EpkNf_>8#^WSKON&59:;9>d<tO-JY/Z@8MTZ&UAL-Zl.|FJ+bFEpE.>5-24d4pAV%+jP&>td;PWRL^lJ21Aly^KcM^Ol1PF%CEJ_7%U/TdSG2dCMf/TZPUALXCzKN:;GXKP|26O2L*-Jcb.+lRK-*8D%cG|JPb.+VyJP^PkAG6FBc|2clLB|*P|5FjbK+b.cVRJ|*2P|5FBc3HER6M",
				"VZR*/J)ERALzVVVKcZb6D1;)UYtO29|cYd3WYZ|5fkjc49-*T.RG(d%UFEVbkVc.b+M:.C6tOd9b8Scl@|^<HVY.FVMG73V.|5FBc7j-5CJFVVRWb@Rf*P2DW*|EtHVHR1^VGL5(k&%GXcK)PW|7z6p^>-*|JR>3#REZ|5FBc-z|#bGzZ(D:9K|pGEV3J_cCJ|+%X.CjkAV1|RHCJ|kF7Bly9bdM&R4J|*JRDHER>W64tOEc97yc.R@TJ.bbJb@2.bc4ZS*:V.f8KVAjWRcDW*-HPVAjVMz9f*J&CJ|/.7yR8t&%GX@7UpEk3N@^EW(NV76.M6Bt#|O3WzR8VdVY",
				"TzZ399:k^f&>4d*_SKO2HT@4EDZBP#)NS(XpOV%PKG&JczRqD/l>tECM+N^<zS_p8F*_SBOM)TJtE&C#qPKPA|VJF7zyKGWHFfSOG12H.KCOJ+J+2yFGU_>YE_5Jc-HEyPEpkNf_p+XAAyB/zD5N::N<3UtWZJ6/Z@PMTZ&UWL-ClFc|BPb|GR.l#5-J8M2>OV%8K8DptF:8ARL^lK21OVy*B+M^OV1;#%CEJ_7%UOTdSG2dCMfWTZPUWLXC9Kz;;GXK8|jYWlL*-Kcb4+lRK-*8D%cG|KPb.+VyJb&PkWG6FBc#2clLB|*P|5F(bK+b.cVRJ|*KP|5FBcMHERGM",
				"TzZ399:k9f&>4d*_SKO2HT@4EP5BP#)NS(XpOV%P@G&JczRqD/l>tECM+N^<zS_p8F*_SBOM)TJtE&C#qPKPA|VJF7zyKGWHFfSOG12H.KCOJ+J82yFGU_>YE_5Jc-HEyPEpkNf_p+XAAyB/zD5N::N<3UtWZJ6/Z@PMTZ&UWL-ClFc|BPb|GR.l|5-J8M2pOV%8K8DptF:8ARL^lK21OVy*B+M^OV1;#%CEJ_7%UOTdSG2dCMfWTZPUWLXCzKz;;GXK8|2YWlL*-Kcb4+lRK-*8D%cG|KPb.+VyJb&PkWG6FBc#2clLB|*P|5F(bK+b.cVRJ|*KP|5FBc|HERGM",
				"tzZ378:kHR&UpF&DfB/dHTp9>jC2P^)NR6bU4+%^KPSpPN_WPq+UtECMcz:k8_DUP3(DyJO-1TB.F&C|WjB;j-+JMR>yJPq1My_/2X@1.K#/JcJ73_bGU&UYNj#Jc|H>yc8UkzySUPF*DyK<NjZ>;PEUdUtqZpYqCJP<TCjUq_-#..P9cS@9k9V.7G-Bl-.Uq+%P5lDUt<P;P_y:+J9Xq+LAJ^M*O+X;d%Z>Jjf%UqTdLG3dCM_qTZjU/Rb#>BE;;G<p^|96q+R*-KcbEl+RK-*cD%4G|KpM.l+fJ-:PkqGGFBc39c+yB|*P|5F6MB^b.cVRJ|*Jl|5FBcdHERGF",
				"1^35/F8+Zf&9U7<YA(8Z^.L<B48qFtOdfBS#V:EpZOA8N2DSSL_^.q9cOPCy%%*UDPP(q<YU)lOPWb<jU9FOP+8qZ+Zp#O.NOF%RL.5M5Lp#O|8c2MSBk.K.y+#FP+UFVc.bC#OPD>U13*+H&BFjbRCT+-VNbP|5FBcjqR+D+-PfqP^|5FBcYqJCWT@FHERWT6CMkR3|X+;kl|*3<SYF4-R)*|JRE6F5+4EG-BBTC)*:L*kXc*+RR+kCDDzD_k+2k2:L6q;B:q*-lR7GNlC>MG(DER>|tCH7*>|t:DLl.UHG8H&/7C-*+AZPy1c2d+-+tkzcPTlCU#P1zZ2d9;C@",
				"tzZ399:k9R&U3F*_fj/#HT@4N&CJ8X(9R6-<Ol%PKGWB+97qDOl<tzCM+N^<NS_p8b^_RB/M)TJ.E8C#q_JPW|VJF7Ny(GAHFfSAG12H2KCAJ+@PbyFGp_>Y9_ZJcF(zR+EpkNf_p8#^WSKON&59:;E>d<tO-JY/Z@83TZ&UAL-Zl.|FJ+bFE>N.>5-24M.pAV%8jP&Utd;PWRL^lJ21Aly^jcb^Ol1PF%CEK_7%U/TdSG2dCMf/TZPUALXCzKN:;GXKP|26O2L*-Jcb.+lRK-*8D%cG|JPb.+VyJF^PkAG6FBc|2clLB|*P|5FjbB+b.cVRJ|*2P|5FBc|HER6M",
				"tzZ378:kHR&UpF&DfB/dHTp9>jC2P^)NR6bU4+%^KPjpPN_WPq+Ut8CMcz:k8_DUP3(DyJO-1TB.F&C|WjB;j-+JMR>yJPq1My_/2X@1.K#/JcJ73_bGU&UYNj#Jc|H>yc8UkzySUPF*DyK<NjZ>;PEUdUtqZpYqCJP<TCjUq_-#..P9cS@.k9V.7G-Bc-.Uq+%P5lDUt<P;P_y:+J9X/+LAJ^M*O+X;d%Z>Jjf%UqTdLG3dCM_qTZjU/Rb#>BE;;G<p^|96q+R*-KcbEl+RK-*cD%4G|KpM.l+RJ-:PkqGGFBc39c+yB|*P|5F6MB^b.cVRJ|*J4|5FBcdHERGF",
				"P(Xl6Vc.bllU|G4DMV|5FBcA3L#dR*-(5R&4O>#.-WW#/Y|XO#@;^F:Zb#W:(@FGpFjEbY-G(&f*|JR<Wf4+|V;zF+#@|+#WcBB4:5-|5F-*MJB/F5-T5zFBcRDO|CRzEAL5:KEDM7c5p*WfpJf54BOFC-:pF*:z|+kd*t|CL-O>:c:zFV<7C)VS-+>:pBHcJJJcc<c7CC7czC7<ffXZEkNJKzJjTDCDAH<2;^Nc;JW^NkqZ.JV#jW)&2_AkZ%JM;2ZEKd%ZD7q<j_c<1BytC7c@)CJGkNAqDKlSJ>tEVVyHER.(LdkFk)zZ1AzZ*9/8K:c7Z;J%K)81cd^Y;TNE",
				"tzZ399:k9R&U3F*_fj/#HT@4N&CJ8X(9R6-<Ol%PKGWB+97qDOl<tzCM+N^<NS_p8b^_RB/M)TJ.E8C#q_JPW|VJF7Ny(GAHFfSAG12H2KCAJ+@Pby3Gp_>Y9_ZJcF(zR+EpkNf_p8#^WSKON&59:;E>d<tO-JY/Z@83TZ&UAL-Zl.|FJ+bFE>N.>5-24M.pAV%8jP&Utd;PWRL^lJ21Aly^jcM^Ol1PF%CEJ_7%U/TdSG2dCMf/TZPUALXCzKN:;GXKP|26O2L*-Jcb.+lRK-*8D%cG|JPb.+VyJF^PkAG6FBc|2clLB|*P|5FjbB+b.cVRJ|*2P|5FBc|HER6M",
				"TzZ399:ktf&U2d^_RK/-HTB4ED5J8F)N7(Xp/l%c@G&J89LqD/V>tECF+N^<zS_p+F*_SBOj)TJ.E&C#qPK;A|VJF7zyKGWHFfSOG12H2K3OJ+J+2yFG<_>YE_5Jc-(EyPEpkNf_p8X^AyB/9D5N;-N<3UtWZJY/Z@PMTZ&UWL-Cl.+|RPKFGp|b#5-J8-2U/l%+J+*>t|:8AyL*lJ21OVy*BcM:OV1;#%CEJ_7%UOTdSG2dCMfWTZPUWLXC9Kz;;GXKP|26WlL*-Kcb.+lRK-*8D%cG|KP|.+VyJb&PkWG6FBc|2clLB|*P|5F(bK+b.cVRJ|*KP|5FBc|HERGM",
				"tzZ378:kHR&UpF&DfB/dHTp9>jC2P^)NR6bU4+%^KPSpPN_WPq+UtECMcz:k8_DUP3(DyJO-1TB.F&C|WjB;j-+JMR>yJPq1My_/2X@1.K#/JcJ73_bGU&UYNj#Jc|H>yc8UkzySUPF*DyK<NjZ>;PEUdUtqZpYqCJP<TCjUq_-#..P9cS@9k9V.7G-Bl-.Uq+%P5lDUt<P;P_y*+J9Xq+LAJ^M*O+X;d%Z>Jjf%UqTdLG3dCM_qTZjU/Rb#>BE;;G<p^|96q+R*-KcbEl+RK-*cD%4G|KpM.l+fJ-:PkqGGFBc39c+yB|*P|5F6MB^b.cVRJ|*J4|5FBcdHERGF",
				"tzZ399:kzR&U3F*_fj/#HT@4N&CJ8X(9R6-<Ol%PKGWB+97qDOl<tzCM+N^<NS_p8b^_RB/M)TJ.E8C#q_JPW|VJF7Ny(GAHFfSAG12H2KCAJ+@Pby3Gp_>Y9_ZJcF(zR+EpkNf_p8#^WSKON&59:;E>d<tO-JY/Z@83TZ&UAL-ZV.|FJ+bFE>N.>5-24M.pAV%8jP&Utd;PWRL^lJ21Aly^jcM^Ol1PF%CEJ_7%U/TdSG2dCMf/TZPUALXCzKN:;GXKP|26O2L*-Jcb.+lRK-*8D%cG|JPb.+VyJF^PkAG6FBc|2clLB|*P|5FjbB+b.cVRJ|*2P|5FBc|HER6M",
				"tzZ378:kHR&UpF&DfB/dHTp9>jC2P^)NR6bU4+%^KPS2PN_WPq+UtECMcz:k8_DUP3(DyJO-1TB.F&C|WjB;j-+JMR>yJPq1My_/2X@1.K#/JcJ73_bGU&UYNj#Jc|H>yc8UkzySUPF*DyK<NjZ>;PEUdUtqZpYqCJP<TCjUq_-#..P9cS@9k9V.7G-Bl-.Uq+%P5lDUt<P;P_y*+J9Xq+LAJ^M*O+X;d%Z>Jjf%UqTdLG3dCM_qTZjU/Rb#>BE;;G<p^|96q+R*-KcbEl+RK-*cD%4G|KpM.l+fJ-:PkqGGFBc39c+yB|*P|5F6MB^b.cVRJ|*J4|5FBcdHERGF",
				"TzZ399:ktf&U2d^_yKO-HTK4ED5J8F)Nf(XU/l%c@G&J89RqD/V>tECF+N*<zS_p+F*_SBOM)TJ.E&C#qPK;A|VJF7zyKGWHFfSOG12H.K3OJ+J+2yFGp_>YE_5Jc-(EyPEpkNf_p8X*AyB/9D5N;;N<3UtWZJY/Z@PMTZ&UWL-ClJ+|RPJFGp|R#5-J;d2UOl%+J+^>t|:8AyL*VJ21Oly^BcM^OV1;#%CEJ_7%UOTdSG2dCMfWTZPUWLXC9Kz;;GXK8|26WlL*-Kcb.+lRK-*8D%cG|KP|.+VyJb&PkWG6FBc|2clLB|*P|5F(bK+b.cVRJ|*KP|5FBc-HERGM",
				"tzZ@78:kNLDUPF:&fB/bHTp.EDCSP^)ERG-U/V%PKPDpc>_W&/+UEE|MLz:k8_&UL<(jyJq@HTB9F&C|WjJ;j-VJMREyJPqHMy*/6X.).K#Op7pL@_fG|&UYNj#JcFH5yl-Ukzy&UPF*jyK<NjZ>;2E|dUtqZpYqZJPMTCjUO_-C.9b9PSb33c9cc6-J^|9Uq+%_P7jUt|P;Dy_(+B.XO1_AJ^M(q+X;d%Z>JjR%U/Td_G@dCMyqTZ&U/RbC2BE;;GbB^|9Yq1R*-Kcb9l+RS-*cj%4G|KlM.l+fJ-:Pkq6GFBc@.c+yB|*P|5F6MJ4b.cVRJ|*J4|5FBcdHER6M",
				"TzZ399:ktf&U2d^_yKO-HTK4ED5J8F)Nf(XU/l%c@G&J89RqD/V>tECF+N*<zS_p+F*_SBOM)TJ.E&C#qPK;A|VJF7zyKGWHFfSOG12H.K3OJ+J+2yFGp_>YE_5Jc-(EyPEpkNf_p8X*AyB/9D5N;;N<3UtWZJY/Z@PMTZ&UWL-ClJ+|RPJFGp|R#5-J;-2UOl%+J+^>t|:8AyL*VJ21Oly^BcM^OV1;#%CEJ_7%UOTdSG2dCMfWTZPUWLXC9Kz;;GXK8|26WlL*-Kcb.+lRK-*8D%cG|KP|.+VyJb&PkWG6FBc|2clLB|*P|5F(bK+b.cVRJ|*KP|5FBc-HERGM",
				"TzZ399:ktf&U2d^_yK/-HTB4ED5J8F)N7(Xp/l%c@G_J89LqD/V>tECF+N^<zS_p+F*_SBOj)TJ.E&C#qPK;A|VJF7zyKGWHFfSOG12H2K3OJ+J+2yFG<_>YE_5Jc-(EyPEpkNf_p8X^AyB/9D5N;-N<3UtWZJY/Z@PMTZ&UWL-Cl.+|RPKFGp|b#5-J8M2UOl%+J+^>t|;8AyL^VJ21OVy*BcM:OV1;#%CEJ_7%UOTdSG2dCMfWTZPUWLXC9Kz;;GXK8|26WlL*-Kcb.+lRK-*8D%cG|KP|.+VyJb&PkWG6FBc|2clLB|*P|5F(bK+b.cVRJ|*KP|5FBc|HERGM",
				"TzZ399:ktf&U2d^_yK/-HTB4ED5J8F)N7(Xp/l%c@GAJ89LqD/V>tECF+N^<zS_p+F*_SBOj)TJ.E&C#qPK;A|VJF7zyKGWHFfSOG12H2K3OJ+J+2yFG<_>YE_5Jc-(EyPEpkNf_p8X^AyB/9D5N;-N<3UtWZJY/Z@PMTZ&UWL-Cl.+|RPKFGp|b#5-J8-2UOl%+J+^>t|;8AyL^VJ21OVy*BcM:OV1;#%CEJ_7%UOTdSG2dCMfWTZPUWLXC9Kz;;GXK8|26WlL*-Kcb.+lRK-*8D%cG|KP|.+VyJb&PkWG6FBc|2clLB|*P|5F(bK+b.cVRJ|*KP|5FBc|HERGM",
				"XYf;1%*pLzZ297|CqAXDjL^C-K%Tp;DZKt76*#99)(*#t((G4;@yW:AM<S66S>+>++H+&#lT#RE81+j1+jU+j1+*##>P.4%;7yK:AW<Y91T^JSJDH.y85KK/:pWdT.#XdR/2%LD<q;KcN.4JTRJHER(5KG_D&NNc5)b||5FBc<-C*M:1F|AL*|JR*p-%8#)6dWU:-k%7/_HK+b%Mb:GEJ|G5&yd>Jd.Jz^OcJK-*|5FBc.Xcl8:1F+AX|*-pbDJ+Vc.bDXq+7y9PJHyG*J2XNCJ1bTF|fFPPLt#3>H#MTRy;<Nc_KX|+9.ylO|j1+jK6jU+6#t>3^fWF.4XROcJ1",
				"UzZ@78:kN7&/PF:PfB/dHTp3>jCB4^)ERG-t/1%LK6j2^>_WDO+tUE|MLz:k8_Dt4<(jyJq-HTB9F&C|WDJ;D-VJMREyJ6qHby_O6X.).K#42PpL@_fGl&tYNjZJ4|H5yc1tkzyjtcF*jyK/6jZ>;PE|dtUqZpY/ZJPFTCjt<_-CRcb.cYY3cjMSc6-B7d9tqV%c27jtL|P;&Ry*1J.XqVRAJLM(q+X;d%Z>Jj_%tOTd_G@dCMy/TZ&t/RbC2BE;;G<Bc|9Yq+R*-Kcb9l+RS-*cj%4G|KcM.l+RJ-#Pk/6GFBc@9c+yB|*P|5F6MJ4b.cVRJ|*J4|5FBcdHER6M",
				"TzZ399:k9f&>4d*_SKO2HT@4EPZBP#)NS(XpOV%PKG&JczRqD/l>tECM+N^<zS_p8F*_SBOM)TJtE&C#qPKPA|VJF7zyKGWHFfSOG12H.KCOJ+J82yFGU_>YE_5Jc-HEyPEpkNf_p+XAAyB/zD5N::N<3UtWZJ6/Z@PMTZ&UWL-ClFc|BPb|GR.l#5-J8M2pOV%8K8DptF:8ARL^lK21OVy*K+M^OV1;#%CEJ_7%UOTdSG2dCMfWTZPUWLXC9Kz;;GXK8|2YWlL*-Kcb4+lRK-*8D%cG|KPb.+VyJb&PkWG6FBc#2clLB|*P|5F(bK+b.cVRJ|*KP|5FBc|HERGM",
				"TzZ399:k^f&>4d*_SKO2HT@4EDZBP#)NS(XpOV%PKG&JczRqD/l>tECM+N^<zS_p8F*_SBOM)TJtE&C#qPKPA|VJF7zyKGWHFfSOG12H.KCOJ+J+2yFGU_>YE_5Jc-HEyPEpkNf_p+XAAyB/zD5N::N<3UtWZJ6/Z@PMTZ&UWL-ClFc|BPb|GR.l#5-J8M2>OV%8K8DptF:8ARL^lK21OVy*B+M^OV1;#%CEJ_7%UOTdSG2dCMfWTZPUWLXC9Kz;;GXK8|jYWlL*-Kcb4+lRK-*8D%cG|KPb.+VyJb&PkWG6FBc#2clLJ|*P|5F(bK+b.cVRJ|*KP|5FBcMHERGM",
				"TzZ399:k9f&>4d*_SKO2HT@4EPZBP#)NS(XpOV%PKG&JczRqD/l>tECM+N^<zS_p8F*_SBOM)TJtE&C#qPKPA|VJF7zyKGWHFfSOG12H.KCOJ+J82yFGU_>YE_5Jc-HEyPEpkNf_p+XAAyB/zD5N::N<3UtWZJ6/Z@PMTZ&UWL-ClFc|BPb|GR.l#5-J8M2pOV%8K8DptF:8ARL^lK21OVy*B+M^OV1;#%CEJ_7%UOTdSG2dCMfWTZPUWLXC9Kz;;GXK8|2YWlL*-Kcb4+lRK-*8D%cG|KPb.+VyJb&PkWG6FBc#2clLB|*P|5F(bK+b.cVRJ|*KP|5FBc|HERGM",
				"VZF*/J)ERALzVVVKcZb6D1;)UYtO29|cY(3WYZ|5fkjc49-*T.bG(d%UFEVbkVc.b+M:.C6tOd9b8Scl@|^<HVY.FVMG73V.|5FBc7j-5CJFVVRWb@Rf*P2DW*|EtHVHR1^VGL5(k&%GXcK)PW|7z6p^>-*|JR>3#REZ|5FBc-z|#bGzZ(D:9K|pGEV3J_cCJ|+%XKcjkA^1|RHCJ|k|7Bly9bdM&R4J|*JRDHER>W62tOEc97yc.R@.J.bbJb@2.bc4ZS*:V.f8KVAjWRcDW*-HPVAjVMz9f*J&CJ|/.7yR8t&%GX@7UpEW3N@^EW(NV76.M6Bt#|O3WzR8Vd^Y",
				"tzZ@78:k>LjUPF&DfB/dHTJ3>jC2P^)NRGbU4V%^KPSpc>RW6qVUtEZMcz:k8_DUP@(DyJO-1TB.FSC|WjE;j-+JMREyJ6qHMy_/2X3)3K#/JcJ7@_fGU&UYNj#Jc|H>yc-UkzySUPF*PyK/NjZ>;PEUdUtqZpYqCJPFTCjUq_-519D.cWc999+2.2-Bl-9UOV%PJ|DUt-P;P_yAVJ9X/VL&BcM*q+X;d%Z>Jjy%UqTd_G@dCMyqTZjU/Rb<>BE;;G<p^|.Yq+R*-Kcb3l+RK-*cD%4G|KpM.l+RJb:Pkq6GFBc@3c+yB|*P|5F6MB^b.cVRJ|*J7|5FBcdHER6M",
				"2Hcb/f|L-zZO97|C(LCOAb4cb8J+X_OZDM7q(@7%lt|@;))dE_#ESpN|GV.>.#V<<#.>W@TU;.y6|UP6F8S(AK|*%9)).Ej2.y>pLSd&;T|4J*JDW.E6J|ET^LkG*d9bRR:OfbOR(b/b3GEJURJHERl5.#JOW33cj/C||5FBcR-cz>p:F(L-*|JR+N-f:@lAdST^2Of7:5<YkbfK_pGyj*.j&yRt5d.JD41cJ8-*|5FBc#bcU:^t|(NC|*-XbzJkVc.bOb&+7E;)JPE.F52b1cJ8C(UU;%)l|@MKtH%Yq.ECR3cJ8b*F;dE>/+AT(A5(A>||9*t)W*6k.EbR1cJ/",
				"tzZ@78:kNL&UPF:&fB/bHTp.EjCSP^)2RG-U/V%PKP&pc>RW&/+UEECMLz:k8_jUL<(jyJq@HTB9F&C|WjJ;j-VJMREyJPqHMy*/6X9).K#Op7pL@_fGU&|YNj#JcFH5yc-Ukzy&UP<*jyK<NjZ>;2E|dUtqZpYqZJPM3C&UO_-C.9b.G7bb9c.cc6-J^F9Uq+%PP7jUt|P;DyR(+J.XO1_AJPM:q+X;d%Z>JDR%U/Td_G@dCMyqTZ&U/R-C2BE;;G<B^|9Yq1R*-Kcb9l+RS-*cj%4G|KlM.l+fJ-:Pkq6GFBc@9c+yB|*P|5F6dJ4b.cVRJ|*J4|5FBcMHER6M",
				"bNc2/fU_-zZz97|CqLCNAK4c28J|X-NZNM7k+^7^ltq;;tl.E2GESp_|.q>>VVVVV(VVW@TU;.y6|*P6F8SFAK|*^9ll.Ejb.yYpXSd&;T*45*JDW.E6J8E6%LkG*d9b#R:DfbzRk2/O1GEJURJHER)5..JNW33c5/C||5FBcR-cFKpK|+LC*|JRFX-fK@)qdSTpODj7:&<|kbfTO%dyJq#5WYRt5d.JD41cJ8-*|5FBc#bcU:%S||LC|*-LbDJkVc.bDb*+7E;)JPE.FJ/b1cJ/bkU*;^)l|@M)tH^YF.EbR1c&|bkz;dE>/+A2*A<+P>|*9*l)W*6(.EbR3cj/",
				"XYf;1%*p-zZ297|CqAXkjL^C-K%Tp;DZKt76*#99)(*9t((G4;@yW:AM<S66S>+>++H+&#lT#RE81+j1+jU+j1+*##>P.4%;7yK:AW<Yf1T^JSJDH.y85KK/:pWdT.#XdR/2%LD<q;KcN.4JTRJHER(5KG_D&NNc5)b||5FBc<-C*M:1F|AL*|JR*p-%8#)6dWU:-k%7/_HK+b%Mb:GEJ*G5&yd>5d.Jz^Oc5K-*|5FBc.Xcl8:1T+AX|*-pbDJ+Vc.bDXqq7y9PJHyG*JzXNCJ1bTF|fFPPLt#3>H#MTRy;<Nc_KX|+9.ylO|j1+jK6jU+6#t>3^tWF.4XROcJ1",
				"2Hcb/f|L-zZO97|C(LCOAb4cb8J+XbOZDM7q(@7%lt|@;))GE_#ESpN|GV#>.>VM..OVW@TU;.y6|UP6F8S(AK|*%9)).Ej2.y>pLSd&;T|4J*JDW.E6J|ET^LkG*d9bRR:OfbOR(b/b3GEJURJHERl5.#JOW33cj/C||5FBcR-cz>p:F(Lb*|JR+N-f:MlAdST^2Of7:5<YkbfK_pGyj*.j&YR)5d.JD41cJ8-*|5FBc#bcU:^t|(NC|*-XbzJkVc.bOb&+7E;)JPE.F52b1cJ8C(UU;%)l|@MKtH%Yq.ECR3cJ8b*F;dE>/+AT(A5(A>||9*t)W*6k.EbR1cj%",
				"TzZ399:ktf&U2d^_y@O-HTB4ED5J8F)Nf(Xp/l%c@G&K89RqDWV>tECF+N^<zS_p+F*_SBOM)TJ.E&C#qPK;A|VJF7zyKGWHFfSOG12H.K3OJ+J+2yFGp_>YE_5Jc-(EyPEpkNf_p8XAAyB/9D5N;;N<3UtWZJY/Z@PMTZ&UWL-CV.+|RPKFGp|b#5-J8-2UOl%+J+^>t|:8AyL*lJ21Oly^BcM^OV1;#%CEJ_7%UOTdSG2dCMfWTZPUWLXC9Kz;;GXK8|26WlL*-Kcb.+lRK-*8D%cG|KP|.+VyJb&PkWG6FBc|2clLB|*P|5F(bK+b.cVRJ|*KP|5FBc|HERGM",
				"tzZ@78:kNLDUPF:&fB/bHTp.EDCSP^)ERG-U/1%PKPjpc>_W&/+UEE|MLz:k8_&UL<(jyJq@HTB9F&C|WjJ;j-VJMREyJPqHMy*/6X9).K#Op7pL@_fG|&UYNj#JcFH5yc-Ukzy&UPF*jyK<NjZ>;;E|dUtqZpYqZJPMTCjUO_-C..b9P7b3979cc6-J^|9Uq+%_P7jUt#P;Dy_(VJ9XO+fAJPM*q+X;d%Z>JjR%U/Td_G@dCMyqTZ&U/RbC2BE;;G<B^|9Yq1R*-Kcb9l+RS-*cj%4G|KlM.l+RJ-:Pkq6GFBc@9c+yB|*P|5F6MJ4b.cVRJ|*J4|5FBcdHER6M",
				"TzZ399:ktf&U2d^_y@O-HTB4ED5J8F)Nf(Xp/l%c@G&K89RqDWV>tECF+N^<zS_p+F*_SBOM)TJ.E&C#qPK;A|VJF7zyKGWHFfSOG12H.K3OJ+J+2yFGp_>YE_5Jc-(EyPEpkNf_p8XAAyB/9D5N;;N<3UtWZJY/Z@PMTZ&UWL-Cl.+|RPKFGp|b#5-J8-2UOl%+J+^>t|:8AyL*lJ21Oly^BcM^OV1;#%CEJ_7%UOTdSG2dCMfWTZPUWLXC9Kz;;GXK8|26WlL*-Kcb.+lRK-*8D%cG|KP|.+VyJb&PkWG6FBc|2clLB|*P|5F(bK+b.cVRJ|*KP|5FBc|HERGM",
				"8%lH/7FEbMLcVVc.d%16D@;lU4t54Z|&3)<Ac.bOf1q4zX-*PZlG(d-UFE8RkVc.b+y:.c6t5d9RUScN@|N<HVc.FVyGV3V.|5FBc7j2Oc.RNVRkb@RfGPcD1CREtGt^lWNV^FO(1&-GPcK|T1|MzqpNL-*|JRB3#RE%|5FBc-C|#>*zZ(D:9KFp^lVcJMCC.>B%TZ)j97^@>RGYXb1>MB+c.bY2&R49|_5RDHER+16ct<E4.7cY.l&&.J....W...zz.SH:VZfUXVMq1RcDO*-HPV7jVyV.fGK&z.|/9M_>UtV2GT@MUpEk3)@NVk()8M6.*qL9#|O31YRU2YNd",
				"tzZ@78:kNLDUPF:&fB/bHTp.EDCSP^)ERG-U/V%PKPDpc>_W&/+UEE|MLz:k8_&UL<(jyJq@HTB9F&C|WjJ;j-VJMREyJPqHMy*/6X.).K#Op7pL@_fG|&UYNj#JcFH5yl-Ukzy&UPF*jyK<NjZ>;;E|dUtqZpYqZJPMTCjUO_-C.9b9PSb39c9cc6-J^|9Uq+%_P7jUt|P;Dy_(+B.XO1_AJPM(q+X;d%Z>JjR%U/Td_G@dCMyqTZ&U/RbC2BE;;GbB^|9Yq1R*-Kcb9l+RS-*cj%4G|KlM.l+fJ-:Pkq6GFBc@.c+yB|*P|5F6MJ4b.cVRJ|*J4|5FBcdHER6M",
				"tzZ@78:kNL&UPF:&fk/bHTp.EjCSP^)2RG-U/V%PKP&pc>RW&/+UEECMLz:k8_jUL<(jyJq@HTB9F&C|WjJ;j-VJMREyJPqHMy*/6X9).K#Op7pL@_fGU&|YNj#JcFH5yc-Ukzy&UPF*jyK<NjZ>;2E|dUtqZpYqZJPM3C&UO_-C.9b9G7b39c.cc6-J^|9Uq+%PP7jUt|P;DyR(+J.XK1_ABPM:q+X;d%Z>JDR%U/Td_G@dCMyqTZ&U/R-C2BE;;G<B^|9Yq1R*-Kcb9l+RS-*cj%4G|KlM.l+fJ-:Pkq6GFBc@9c+yB|*P|5F6dJ4b.cVRJ|*J4|5FBcMHER6M",
				"bHcb1fl*bzZ2D8|CF|S<;-Hc+L|GPb2Zz98FO98W||*A9_@.%-R>djPt.VVVV77VVVO+qDtMW.Et)|;dK7t*7(G*AD66Ry|CREUjYdRqDNlqJAJ<q.ydJL%dp|#RM/Wb.RN23C<.l|1bkREJ|.JHyR6J.TJzqkkcJLS||5FBcT-cMNjt;|P+*|JRO|-|UW6KTN(jCX|8dJqE|b3&Sp.>JM.5Hy/_JR.52q^cJL-*|5FBcRCcl&jd:|Yb|*-|bX5GVc.bXb#M8>D@JHERlJz+^cJLCMlFDW6_K9W6|HWp:.ySRkc5)bG4D.ENLO;d*7d|7tKFWW||qWdlR%bR|cJ1",
				"2Hcb/f|L-zZO97|C(LCOAb4cb8J+XbOZDM7q(@7%l)|@;))GE_#ESpN|GV#>.>VM..OVW@TU;.y6|UP6F8S(AK|*%9)).Ej2.y>pLSd&;T|4j*JDW.E6J|ET^LkG*d9bRR:OfbOR(b/b3GEJURJHERl5.#JOW33cj/C||5FBcR-cz>p:F(Lb*|JR+N-f:MlAdST^2Of7:5<YkbfK_pGyj*.j&YR)5d.JD41cJ8-*|5FBc#bcU:^t|(NC|*-XbzJkVc.bOb&+7E;)JPE.F52b1cJ8C(UU;%)l|@MltH%Yq.ECR3cJ8b*F;dE>/+AT(A5(A>||9*t)W*6k.EbR1cj%",
				"TzZ399:ktf&U2#^_yKO-HTB4ED5J8b)Nf(X</l%c@G&K89RqDWV>tECF+N^<zS_<+F*_SBOM)TJ.E&C#qPK;A|VJF7zyKGWHFfSOG12H.K3OJ+J+2yFGp_<YE_5Jc-(EyPEpkNf_<8XAAyB/9D5N;;N<3UtWZJY/Z@PMTZ&>WL-Cl.+|RPKFGp|b#5-J8-.UOl%+J+^>t|:8ASL^lJ21Oly*BcM^OV1;#%CEJ_7%UOTdSG2dCMfWTZPUWLXC9Kz;;GXK8|26WlL*-Kcb.+lRK-*8D%cG|KP|.+VyJb&PkWG6FBc|2clLB|*P|5F(bK+b.cVRJ|*KP|5FBc|HERGM",
				"2Hcb/f|L-zZO97|C(LCOA24cb8J+XbOZDM7q(@7%lt|@;))GE_#ESpN|GV#>.>VM..OVW@TU;.y6|UA6F8S(AK|*%9)).Ej2.y>pLSd&;T|4J*JDW.E6J|ET^LkG*d9bRR:OfbOR(b/b3GEJURJHERl5.#JOW33cj/C||5FBcR-c|>p:F(L-*|JR+N-f:MlAdST^2Of7:5<YkbfK_pGyj*.j&Y.)5d.JD41cJ8-*|5FBc#bcU:^t|(NC|*-XbzJFVc.bOb&+7E;)JPE.F52b1cJ8C(UU;%)l|@MKtH%Yq.ECR3cJ8b*F;dE>/+AT(A5(A>||9*t)W*6k.EbR1cj%",
				"2Hcb/f|L-zZO97|C(LCOA24cb8J+XbOZDM7q(@7%lt|@;))GE_#ESpN|GV#>.>VM..OVW@TU;.y6|UA6F8S(AK|*%9)).Ej2.y>pLSd&;T|4J*JDW.E6J|ET^LkG*d9bRR:OfbOR(b/b3GEJURJHERl5.#JOW33cj/C||5FBcR-cz>p:F(L-*|JR+N-f:MlAdST^2Of7:5<YkbfK_pGyj*.j&Y.)5d.JD41cJ8-*|5FBc#bcU:^t|(NC|*-XbzJkVc.bOb&+7E;)JPE.F52b1cJ8C(UU;%)l|@MKtH%Yq.ECR3cJ8b*F;dE>/+AT(A5(A>||9*t)W*6k.EbR1cj%",
				"tzZ@78:kNLDUPF:&fB/bHTp.EDCSP^)ERG-U/1%PKPjpc>_W&/+UEE|MLz:k8_&UL<(jyJq@HTB9F&C|WjJ;j-VJMREyJPqHMy*/6X9).K#Op7pL@_fG|&UYNj#JcFH5yc-Ukzy&UPF*jyK<NjZ>;;E|dUtqZpYqZJPMTCjUO_-C..b9P7b39c9cc6-J^|9Uq+%_P7jUt#P;Dy_(VJ9XO+fAJPM*q+X;d%Z>JjR%U/Td_G@dCMyqTZ&U/RbC2BE;;G<B^|9Yq1R*-Kcb9l+RS-*cj%4G|KlM.l+RJ-:Pkq6GFBc@9c+yB|*P|5F6MJ4b.cVRJ|*J4|5FBcdHER6M",
				"VKcH3JbEbMLcVVc.c%R6DW;lUYtOY9|&3)4kc.bOfAq)CJ-*PZbG(d-UFEVRkVc.b+y:.c6t5d9RUScN@|N<HVc.FVyGV3V.|5FBc7j25c.RV8RkbWRfGPcD1CREtGt^lWNV^FO(1&-GPcK|T1|MzqpNL-*|JRB3#RE%|5FBc-Y|#b*zZ(D:9KFpGl)dJ7CYX>B%PZCj%7VWbRGYXbk>ML+c.bC2&R49|G.RDHER+16cT<E(.7cc.lSJ.J...yJ...czXSH:VZfUXVMq1RcDO*-HPV7jVyY%fGK&z.|/9M_>UtV2GPWMUpEA3VWNVk(_VM6.*qLt#|O3kYRU24Nd",
				"tzZ399:k9R&U3F*_fj/#HT@4N&CJ8X(9R6-<Ol%PKGWB+97qDOl<tzCM+N^<NS_p8b^_RB/M)TJ.E8C#q_JPW|VJF7Ny(GAHFfSAG12H2KCAJ+@PbyFGp_>Y9_ZJcF(zR+EpkNf_p8#^WSKON&59:;E>d<tO-JY/Z@83TZ&UAL-Zl.|FJ+bFE>N.>5-2cM.pAV%8jP&Utd;PWRL^lJ21Aly^jcb^Ol1PF%CEK_7%U/TdSG2dCMf/TZPUALXCzKN:;GXKP|26O2L*-Jcb.+lRK-*8D%cG|JPb.+VyJF^PkAG6FBc|2clLB|*P|5FjbB+b.cVRJ|*2P|5FBc|HER6M",
				"TzZ399:ktf&U2d^_y@O-HTB4ED5J8F)Nf(Xp/l%c@G&K89RqDWV>tECb+N^<zS_p+F*_SBOM)TJ.E&C#qPK;A|VJF7zyKGWHFfSOG12H.K3OJ+J+2yFGp_>YE_5Jc-(EyPEpkNf_p8XAAyB/9D5N;;N<3UtWZJY/Z@PMTZ&UWL-Cl.+|RPKFGp|b#5-J8-2UOl%+J+^>t|:8AyL*lJ21Oly*BcM^OV1;#%CEJ_7%UOTdSG2dCMfWTZPUWLXC9Kz;;GXK8|26WlL*-Kcb.+lRK-*8D%cG|KP|.+VyJb&PkWG6FBc|2clLB|*P|5F(bK+b.cVRJ|*KP|5FBc|HERGM",
				"TzZ399:k9f&U2d*_y@/bHT@4EPZBP#HNR(X>/V%+KG&@czSqD/V>tECM+N^<zS_p8d*_SBOb)TJtE&C#qPKPA|VJF7zyKGWHFfSOG12H.KCOJ+J+2yFGU_>YE_5Jc-(EyPEpkNf_p+XAAyK/9D5N::Np3UtWZJ6/Z@PMTZ&>WL-ClF.|B+bNpR.lp5-J8M2U/V%8J2D>t3:8ARL^VK21Oly*BPM^OV1;#%CEK_7%pOTdSG2dCMfWTZPUWLXCzKz;;GXK8|2YWVL*-Kcb4+lRK-*cD%cG|Kcb.+lyJb&PkWG6FBc|2clLB|*P|5F(bKcb.cVRJ|*KP|5FBc|HERGM",
				"UfAO%t79ECXGM3/.NZfWpHERjBbG6DL#f(yWdXfW*Vt8Z>Nq4&qLY#D4lF.#yW.-1G-*.25b3/BpPXC4;2.H|d1_5PZDb1Vc.b^4R)W>zE8|5FBcSWdBj<F^8MSSDpFZ.c/2%>q3U>JPG*>V:by5DV5W^31MDp^_)RGFBczbqA-M#KT|5F:@)DqMql-^p_1:%F6>3z5j#PY:/D5S2_7K;;444fMpLfZpRccMR4*|JR7cD9%UE:>UHE8|%1M.:)qL|Z>q3B/-JW6%:VO4VEq^5M3zbRf#E89p>CX|VBU2R|dKdD1c@G*zZO8|AK*-FPBBH)<++PRE(M2GONEf5j;Y",
				"tzZ@78:kNfD|PF:&yB/|HTp.>DCSl^)ERG-U/V%cKPDpL>_W&/+U4E|MLz:k8_&Uc<AjyJ6@HTB9F&C|WjJ;j-4JMREyJP6HMy*/qX.).K#Op7pL@_fGl&UYNj#JcFH5yc-Ukzy&UP<*jyK<NjZ>;2E|dUt6ZpY61JP-TCjUO_-C.L(9cLb3.c9:cq-J^|.U6V%LPLDUtMP;jy_(1J9XOVfAJPM(O+X;d%Z>Jjf%U/Td_G@dCMy6TZ&U/RbC2BE;;G<B^|9Y6+R*-Kcb9l+RS-*cj%LG|K7B.l+fJ-:Pk6qGFBc@9c+yB|*P|5FqMJlb.cVRJ|*Jc|5FBcdHERqM",
				">F;k8pLB8PpD4%fd^S-VDZWzVB|_5*C@V6d4/+6&)8-*_F6|5FBc3bFE-/|S&*DS4WdG3D344pzZ)D^;C+*>Ff8PPG)4fpJ_>T*->EH_|P)-&E-#%FB|5FBc)Ud)W>-qD*D-C*f>#DU*F9-P->HER#fPq-PN/W-DbP2&--WcE---/4W--P23:W&<b46&FW/9DbtWb.cV>bPkkq-byJ7YqzH#cJUY>bWONbbRJ|*-b.NcAUYb6O#bbOA|NKbbY7-Yf#Jb&P77Jz:9d-jjt<2NUtbjHRCy)qzHJdU8YqzHjtYj<qO:z9cJ8yE2#K%ANjt<&((k&.1R1&:(k1lYM+HX",
				"2Hcb/f|L-zZO97|C(LCOAb4cb8J+XbOZDM7q(@7%lt|@;))GE_#ESpN|GV#>.>VM..OVW@TU;.y6|UP6F8S(AK|*%9)).Ej2.y>pLSd&;T|4j*JDW.E6J|ET^LkG*d9bRR:OfbOR(b/b3GEJURJHERl5.#JKW33cj/C||5FBcR-cz>p:F(Lb*|JR+N-f:MlAdST^2Of7:5<YkbfK_pGyj*.j&YR)5d.JD41cJ8-*|5FBc#bcU:^t|(NC|*-XbzJkVc.bOb&+7E;)JPE.F52b1cJ8C(UU;%)l|@MltH%Yq.ECR3cJ8b*F;dE>/+AT(A5(A>||9*t)W*6k.EbR1cj%",
				"TzZ399:ktf&U2d^_y@O-HTB4ED5J8F)Nf(Xp/l%c@G&K89RqDWV>tECF+N^<zS_p+F*_SBOM)TJ.E&C#qPK;A|VJF7zyKGWHFfSOG12H.K3OJ+J+2yFGp_>YE_5Jc-(EyPEpkNf_p8XAAyB/9D5N;;N<3UtWZJY/Z@PMTZ&UWL-Cl.+|RPKFGp|b#5-J8-2UOl%+J+^>t|:8AyL*lJ21Oly*BcM^OV1;#%CEJ_7%UOTdSG2dCMfWTZPUWLXC9Kz;;GXK8|26WlL*-Kcb.+lRK-*8D%cG|KP|.+VyJb&PkWG6FBc|2clLB|*P|5F(bK+b.cVRJ|*KP|5FBc|HERGM",
				"tzZ@78:kNL&UPF:&fB/bHTp.EjCSP^)2RG-U/V%PKP&pc>RW&/+UEECMLz:k8_jU7<(jyJq@HTB9F&C|WjJ;j-VJMREyJPqHMy*/6X9).K#Op7pL@_fGU&|YNj#JcFH5yc-Ukzy&UPF*jyK<NjZ>;2E|dUtqZpYqZJPM3C&UO_-C.9b.G7b39c.cc6-J^|9Uq+%PP7jUt|P;DyR(+J.XO1_AJPM:q+X;d%Z>JDR%U/Td_G@dCMyqTZ&U/R-C2BE;;G<B^|9Yq1R*-Kcb9l+RS-*cj%4G|KlM.l+fJ-:Pkq6GFBc@9c+yB|*P|5F6dJ4b.cVRJ|*J4|5FBcMHER6M",
				"tzZ@78:kNL&UPF:&fB/bHTp.EjCSP^)2RG-U/V%PKP&pc>RW&/+UEECMLz:k8_jUL<(jyJq@HTB9F&C|WjJ;j-VJMREyJPqHMy*/6X9).K#Op7pL@_fGU&|YNj#JcFH5yc-Ukzy&UP<*jyK<NjZ>;2E|dUtqZpYqZJPM3C&UO_-C.9b.G7bb9c.cc6-J^|9Uq+%PP7jUt|P;DyR(+J.XO1_AJPM:q+X;d%Z>JDR%U/Td_G@dCMyqTZ&U/R-C2BE;;G<B^|9Yq1R*-Kcb9l+RS-*cj%4G|KlM.l+fJ-:Pkq6GFBc@9c+yB|*P|5F6dJ4b.cVRJ|*J4|5FBcMHER6M",
				"tzZ@78:kNLDUPF:&fB/bHTp.EDCSP^)ERG-U/1%PKPjpc>_W&/+UEE|MLz:k8_&UL<(jyJq@HTB9F&C|WjJ;j-VJMREyJPqHMy*/6X9).K#Op7pL@_fG|&UYNj#JcFH5yc-Ukzy&UPF*jyK<NjZ>;;E|dUtqZpYqZJPMTCjUO_-C.9b9P7b39c9cc6-J^|9Uq+%_P7jUt#p;Dy_(VJ9XO+_AJPM:/+X;d%Z>JjR%U/Td_G@dCMyqTZ&U/RbC2BE;;G<B^|9Yq1R*-Kcb9l+RS-*cj%4G|KlM.l+RJ-:Pkq6GFBc@9c+yB|*P|5F6MJ4b.cVRJ|*J4|5FBcdHER6M",
				">zZ379:kzfP>Pd*_yJO#HTK4EDCJcF)NR(Xp/V%8KG&@czRqD/V>tEZM+N^<zS_p8F*_SKOM)TJ.E&CFqPK:D|VJ|7zyK6WHFfSOG1.H.KCOJ+J32yFGU_>YE_5Jc-(EyPEpkNf_p8XADyJ/9DCN::N<3UtWZJ6/Z@P#TZ&pWL-ClBV|BcbFGU2l#5-J842<Ol%)J+D>t|:8AfL^lJ21Oly*B+M^/V1;-%CEJ_7%UOTdSG2dCMfWTZPUWLXC9Kz;;GXJ8|2YWlL*-Kcb4+lRK-*8D%cG|KPb.+VyJb&PkW(6FBc|2clLB|*P|5F(bB+b.cVRJ|*KP|5FBc|HERGM",
				">zZ379:kzfP>Pd*pyJOXHTK49DCJcF)NR(Xp/V%8KG&@czRqD/l>tEZM+N^<zS_pcF*_SKOM)TJ.E&CFqPK:D|VJ|7zyK6WHFfSOG1.H.KCOJ+J32yFGU_>YE_5Jc-(EyPEpkNf_p8XAAyJ/9DCN::N<3UtWZJ6/Z@PMTZ&pWL-ClBV|BcbFGU2l#5-J842UOl%+J+D>t|:8AfL^lB21Oly*B+M^WV1;-%CEJ_7%UOTdSG2dCMfWTZPUWLXC9Kz;;GXJ8|2YWlL*-Kcb4+lRK-*8D%cG|KPb.+VyJb&PkWG6FBc|2clLB|*P|5F(bB+b.cVRJ|*KP|5FBc|HERGM",
				">zZ379:kzfP>Pd*_yJOXHTK4EDCJcF)NR(Xp/V%8KG&@czRqD/l>tEZM+N^<zS_pcF*_SKOM)TJ.E&CFqPK:D|VJ|7zyK6WHFfSOG1.H.KCOJ+J32yFGU_>YE_5Jc-(EyPEpkNf_p8XAAyJ/9DCN::N<3UtWZJ6/Z@PMTZ&pWL-ClBV|BcbFGUBl#5-J842UOl%+J+D>t|:8AfL^lB^1Oly^B+M^/V1;-%CEJ_7%UOTdSG2dCMfWTZPUWLXC9Kz;;GXJ8|2YWlL*-Kcb4+lRK-*8D%cG|KPb.+VyJb&PkWG6FBc|2clLB|*P|5F(bB+b.cVRJ|*KP|5FBc|HERGM",
				"tzZ@78:kNLDUPF:&fB/bHTp.EDCSP^)ERG-U/1%PKPjpc>_W&/+UEE|MLz:k8_&UL<(jyJq@HTB9F&C|WjJ;j-VJMREyJPqHMy*/6X9).K#Op7pL@_fG|&UYNj#JcFH5yc-Ukzy&UPF*jyK<NjZ>;;E|dUtqZpYqZJPMTCjUO_-C.9b.P7b39c9cc6-J^|9Uq+%_P7jUt#p;Dy_(VJ9XO+_AJPM:/+X;d%Z>JjR%U/Td_G@dCMyqTZ&U/RbC2BE;;G<B^|9Yq1R*-Kcb9l+RS-*cj%4G|KlM.l+RJ-:Pkq6GFBc@9c+yB|*P|5F6MJ4b.cVRJ|*J4|5FBcdHER6M",
				">zZ379:kzfP>Pd*_yJOXHTK4EDCJcF)NR(Xp/V%8@G&@czRqD/l>tEZM+N^<zS_pcF*_SKOM)TJ.E&CFqPK:D|VJ|7zyK6WHFfSOG1.H.KCOJ+J32yFGU_>YE_5Jc-(EyPEpkNf_p8XAAyJ/9DCN::N<3UtWZJ6/Z@PMTZ&pWL-ClBV|BcbFGU2l#5-J842UOl%+J+D>t|:8AfL^lB^1Oly^B+M^WV1;-%CEJ_7%UOTdSG2dCMfWTZPUWLXC9Kz;;GXJ8|2YWlL*-Kcb4+lRK-*8D%cG|KPb.+VyJb&PkWG6FBc|2clLB|*P|5F(bB+b.cVRJ|*KP|5FBc|HERGM",
				"tzZ@78:kNRDUPF:&fB>bHTp.EDCSP^)ERG-U>V%PKPDpc/_Wj>+UEE|MLz:k8_&UL<(jRJq@HTB9F&C|WjJ;j-VJMREyJPqHMy*>6X.).K#Op7pL@_fG|&UYNj#JcFH5yl-Ukzy&UPF*jyK<NjZ/;2E|dUtqZpYqZJPMTCjUO_-#..b9PSb3.c.cc6-J^|9Uq+%_P7jUt|P;Dy_(+P.XO+_AJPM(q+X;d%Z/JjR%U>Td_G@dCMyqTZ&U>RbC2BE;;G<B^|9Yq1R*-Kcb9l+RS-*cj%4G|KlM.l+fJ-:Pkq6GFBc@9c+yB|*P|5F6MJ4b.cVRJ|*J4|5FBcdHER6M",
				"tzZ@78:kNL&UPF:&fB/bHTp.EjCSP^)2RG-U/V%PKP&pc>RW&/+UEECMLz:k8_jUL<(jyJq@HTB9F&C|WjJ;j-VJMREyJPqHMy*/6X9).K#Op7pL@_fGU&|YNj#JcFH5yc-Ukzy&UPF*jyK<NjZ>;2E|dUtqZpYqZJPM3C&UO_-C.9b.G7b39c93c6-J^|9Uq+%PP7jUt|P;DyR(+J.XO1_AJPM:q+X;d%Z>JDR%U/Td_G@dCMyqTZ&U/R-C2BE;;G<B^|9Yq1R*-Kcb9l+RS-*cj%4G|KlM.l+fJ-:Pkq6GFBc@9c+yB|*P|5F6dJ4b.cVRJ|*J4|5FBcMHER6M",
				"tzZ@78:kNRDUPF:&fB>bHTp.EDCSP^)EyG-U>V%PKPDpc/_Wj>+UEE|MLz:k8_&UL<(jRJq@HTB9F&C|WjJ;j-VJMREyJPqHMy*>6X.).K#Op7pL@_fG|&UYNj#JcFH5yl-Ukzy&UPF*jyK<NjZ/;2E|dUtqZpYqZJPMTCjUO_-#..b9PSb39c.cc6-J^|9Uq+%_P7jUt|P;Dy_(+P.XO+_AJPM(q+X;d%Z/JjR%U>Td_G@dCMyqTZ&U>RbC2BE;;G<B^|9Yq1R*-Kcb9l+RB-*cj%4G|KlM.l+fJ-:Pkq6GFBc@9c+yB|*P|5F6MJ4b.cVRJ|*J4|5FBcdHER6M",
				"XYf;1%*p-zZ297|CqAXkjL^C-K%Tp;DZKt76*#99)(*9t((G4;@yW:AM<S66S>>>++H+&#lT#RE81+j1+jU+j1+*##>P.4%;7yK:AW<Y91T^JSJDH.y85KK/:pWdT.#XdR/2%LD<q;KcN.4JTRJHER(5KG_D&NNc5)b||5FBc<-C*M:1F|AL*|JR*p-%8#)6dWU:-k%7/_HK+b%Mb:GEJ*G5&yd>5d.Jz^Oc5K-*|5FBc.Xcl8:1F+AX|*-pbDJ+Vc.bDXqq7y9PJHyG*JzXNCJ1bTF|fFPPLt#3>H#MTRy;<Nc_KX|+9.ylO|j1+jK6jU+6#t>3^fWF.4XROcJ1",
				"tzZ@78:kNLDUPF:&fB/bHTp.ED|SP^)ERG-UO1%PKPjpc>_W&/+UEE|MLz:k8_&UL<(jyJq@HTB9F&C|WjJ;j-VJMREyJPqHMy*/6X9).K#Op7pL@RfG|&UYNj#JcFH5yc-Ukzy&UPF*jyK<NjZ>;;E|dUtqZpYqZJPMTCjUO_-C.9b.Pcb39c9cc6-J^|9Uq+%_P7jUt|P;Dy_(VJ9XO+_AJPM:/+X;d%Z>JjR%U/Td_G@dCMyqTZ&U/RbC2BE;;G<B^|9Yq1R*-Kcb9l+RS-*cj%4G|KlM.l+RJ-:Pkq6GFBc@9c+yB|*P|5F6MJ4b.cVRJ|*J4|5FBcdHER6M",
				"tzZ@78:kNRDUPF:&fB>bHTp.EDCSP^)ERG-U>V%PKPDpc/_Wj>+UEE|MLz:k8_&UL<(jRJq@HTB9F&C|WjJ;j-VJMREyJPqHMy*>6X.).K#Op7pL@_fG|&UYNj#JcFH5yl-Ukzy&UPF*jyK<NjZ/;2E|dUtqZpYqZJPMTCjUO_-#..b9PSb39c.cc6-J^|9Uq+%_P7jUt|P;Dy_(+P.XO+_AJPM(q+X;d%Z/JjR%U>Td_G@dCMyqTZ&U>RbC2BE;;G<B^|9Yq1R*-Kcb9l+RS-*cj%4G|KlM.l+fJ-:Pkq6GFBc@9c+yB|*P|5F6MJ4b.cVRJ|*J4|5FBcdHER6M",
				"TzZ399:ktf&U2d^_y@O-HTB4ED5J8F)Nf(Xp/l%c@G&K89RqDWV>tECF+N^<zS_p+F*_SBOM)TJ.E&C#qPK;A|VJF7zyKGWHFfSOG12H.K3OJ+J+2yFGp_>YE_5Jc-(EyPEpkNf_p8XAAyB/9D5N;;N<3UtWZJY/Z@PMTZ&UWL-Cl.+|RPKFGp|b#5-J+-2UOl%+J+^>t|:8AyL*lJ21Oly*BcM^OV1;#%CEJ_7%UOTdSG2dCMfWTZPUWLXC9Kz;;GXK8|26WlL*-Kcb.+lRK-*8D%cG|KP|.+VyJb&PkWG6FBc|2clLB|*P|5F(bK+b.cVRJ|*KP|5FBc|HERGM",
				"2Hcb/f|L-zZO97|C(LCOAb4cb8J+XbOZDM7q(@7%lt|@;))dE_#ESpN|GV.>.#V<<#.>W@TU;.y6|UP6F8S(AK|*%9)).Ej2.y>pLSd&;T|4J*JDW.E6J|ET^LkG*d9bRR:OfbOR(b/b3GEJURJHERl5.#JOW33cj/C||5FBcR-cz>p:F(L_*|JR+N-f:@lAdST^2Of7:5<YkbfK_pGyj*.j&yRt5d.JD41cJ8-*|5FBc#bcU:^t|(NC|*-XbzJkVc.bOb&+7E;)JPE.F52b1cJ8C(UU;%)l|@MKtH%Yq.ECR3cJ8b*F;dE>/+AT(A5(A>||9*t)W*6k.EbR1cJ/",
				"bHcb1fl*bzZ2D8|CF|S<;bHc+L|GPb2ZzA8FO98W||*A9_@.%-R>djPt.VVVV77VVVO+qDtMW.Et)|;dK7t*7(G*AD66Ry|CREUjYdRqDNlqJAJ<H.ydJL%dp|#RM/Wb.RN23C<.l|1bkREJ|.JqyR6J.TJzHkkcJLS||5FBcT-cMNjt;|P+*|JRO|-|UW6KTN(jCX|8dJqE|b3&Sp.>JM.5Hy/_JR.52q^cJL-*|5FBcRCcl&jd:|Yb|*-|bX5GVc.bXb#M8>D@JHERlJz+^cJLCMlFDW6_K9W6|HWp:.ySRkc5)bG4D.ENLO;d*7d|7tKFWW||HWdlR%bR|cJ1",
				">zZ379:kzfP>Pd*pyJOXHTK49DCJcF)NR(Xp/l%8KG&@czRqD/l>tEZM+N^<zS_pcF*_SKOM)TJ.E&CFqPK:D|VJ|7zyK6WHFfSOG1.H.KCOJ+J32yFGU_>YE_5Jc-(EyPEpkNf_p8XAAyJ/9DCN::N<3UtWZJ6/Z@PMTZ&pWL-ClBV|BcbFGU2l#5-J842UOl%+J+D>t|:8AfL^lB21Oly*B+M^WV1;-%CEJ_7%UOTdSG2dCMfWTZPUWLXC9Kz;;GXJ8|2YWlL*-Kcb4+lRK-*8D%cG|KPb.+VyJb&PkWG6FBc|2clLB|*P|5F(bB+b.cVRJ|*KP|5FBc|HERGM",
				"TzZ399:k9f&U2d*_y@/bHT@4EPZBP#HNR(X>/V%+KG&@czSqD/V>tECM+N^<zS_p8d*_SBOb)TJtE&C#qPKPA|VJF7zyKGWHFfSOG12H2KCOJ+J+2yFGU_>YE_5Jc-(EyPEpkNf_p+XAAyK/9D5N::Np3UtWZJ6/Z@PMTZ&>WL-ClF.|J+bNpR.l#5-J8M2U/V%8J2D>t3:8ARL^VK21Oly*BPM^OV1;#%CEK_7%pOTdSG2dCMfWTZPUWLXCzKz;;GXK8|2YWVL*-Kcb4+lRK-*cD%cG|Kcb.+lyJb&PkWG6FBc|2clLB|*P|5F(bKcb.cVRJ|*KP|5FBc|HERGM",
				"TzZ399:ktfPU2d^_yK/-HTB4ED5J8F)N7(Xp/l%c@G&J89LqD/V>tECF+N^<zS_p+F*_SBOj)TJ.E&C#qPK;A|VJF7zyKGWHFfSOG12H2K3OJ+J+2yFG<_>YE_5Jc-(EyPEpkNf_p8X^AyB/ED5N;;N<3UtWZJY/Z@PMTZ&UWL-Cl.+|RPKFGp|b#5-J8-2UOl%+K+&>t|:8AyL*VJ21OVy*BcM*OV1;M%CEJ_7%UOTdSG2dCMfWTZPUWLXC9Kz;;GXK8|26WlL*-Kcb.+lRK-*8D%cG|KP|.+VyJb&PkWG6FBc|2clLB|*P|5F(bK+b.cVRJ|*KP|5FBc|HERGM",
				"VJVG/ZlEVALcV#4KcZ|6D@;b8Ct54.|&3cO7zJRUfkqzc9-*TZ|G3d^>FEVRkVc.bLyXKcjt5d.R>#cN@bNtGVYJFVy*EtV.|5FBc1j2U49RVVR7b@RfHXCD7GREt*V*b@N3*b5Vk&%*XCKRT7bAz/:N+-*|JRL3SREZ|5FBc-c|S|*zZ(D;9Kb:HbVYJACzJ|+^X.4j.MV@bRHpJF)F1+BC9Rc%VRC9|GJbDHERL76p<3Vc.MccJlJdbb..Jbbb.Jcc.#H:V.f>JVA/7RzDk*-*XVAjVyz.V*.&VJ||.A_R83VfG;@A>:Ok(_@NVc3_VA/J*qB<S|5O7pR>#dNc",
				"6&>TN;pKOl7ykF:J_/HHfMA*-9SO2+31/MlW_.j%|^R</zdf@3;ZK^2>U&CydGA+%B<12;3dTCl*zZ*DA:CHb71YWV+SE>@KkUSLZLk#GG3N&U@2dVc.b75CM1|5FBcC^%c><8t@YqbP|SD%AN2.3<:_+-4RK4#P4ST8b;SFP-q#CXGjLzHERB;jytRp%_|5FBc8%*|JR78Y5b((UZ6TFP|MfV/9DD9VM/k.9kDJlAYtZCJUN-:HyBlLR>j7ZB5y_c:#z^|HfG1dT<+lG)HFSK-*2Oldp>8)^.V<:_zqJ&JVdyN&@Pz#L#P4jcfD4<tfK&^5715*lLy+t|3TNA4A",
				"tzZ@78:kNL&UPF:&fB/bHTp.EjCSP^)2RG-U/V%PKP&pc>RW&/+UEECMLz:k8_jUL<(jyJq@HTB9F&C|WjJ;j-VJMREyJPqHMy*/6X9).K#Op7pL@_fGU&|YNj#JcFH5yc-Ukzy&UPF*jyK<NjZ>;2E|dUtqZpYqZJPM3C&UO_-C.9b.G7b39c9cc6-J^|9Uq+%PP7jUt|P;DyR(+J.XO1_ASPM:q+X;d%Z>JDR%U/Td_G@dCMyqTZ&U/R-C2BE;;G<B^|9Yq1R*-Kcb9l+RS-*cj%4G|KlM.l+fJ-:Pkq6GFBc@9c+yB|*P|5F6dJ4b.cVRJ|*J4|5FBcMHER6M",
				"TzZ399:k9f&U2d*_y@/bHT@4EPZBP#HNR(X>/V%+KG&@czSqD/V>tECM+N^<zS_p8d*_SBOb)TJtE&C#qPKPA|VJF7zyKGWHFfSOG12H2KCOJ+J+2yFGU_>YE_5Jc-(EyPEpkNf_p+XAAyK/9D5N::Np3UtWZJ6/Z@PMTZ&>WL-ClF.|B+bNpR.l#5-J2M2U/V%8J2D>t3:8ARL^VK21Oly*BPM^OV1;#%CEK_7%pOTdSG2dCMfWTZPUWLXCzKz;;GXK8|2YWVL*-Kcb4+lRK-*cD%cG|Kcb.+lyJb&PkWG6FBc|2clLB|*P|5F(bKcb.cVRJ|*KP|5FBc|HERGM",
				"tzZ@78:kNRDUPF*&fB>bHTp.EDCSP^)ERG-U>V%PKPDpc/_Wj>+UEE|MLz:k8_&UL<(jRJq@HTB9F&C|WjJ;j-VJMREyJPqHMy*>6X.).K#Op7pL@_fG|&UYNj#JcFH5yl-Ukzy&UPF*jyK<NjZ/;2E|dUtqZpYqZJPMTCjUO_-#..b9PSb3.c.cc6-J^|9Uq+%_P7jUt|P;Dy_(+P.XO+_AJPM(q+X;d%Z/JjR%U>Td_G@dCMyqTZ&U>RbC2BE;;G<B^|9Yq1R*-Kcb9l+RS-*cj%4G|KlM.l+fJ-:Pkq6GFBc@9c+yB|*P|5F6MJ4b.cVRJ|*J4|5FBcdHER6M",
				"1^f;1%*pXzZ297|CqpXkj;^c-K%TA;DZK976*#9t)(*ft(>R4;@yW:pM<S66S>+>++++&#lT#RE8K+j1+jU+j1+*##>P.4%;7yK:pW<Y91T^JSJDH.y85KK/:pWd+.9XdR/2%LD<q;KcN.4JTRJHER(5KG_DHNNc5)b||5FBc<-C|U:1F|pL*|JR*A-%8#(qdWU:-2%7/_HK+b%Mb:GEJ*G5&yd>5d.JD^NcJ)-*|5FBc.Xcl8:1F+AX|*-pbDJ+Vc.bDXq+7y9PJHyG*JzXNCJ1bTF|#FPPLt#3>H#MT.y;<Nc_KX|+9.ylO|j1+jK6jU+6##33^fWF.4XROcJ1",
				"V9RG6.|Eb)BzVydKN9R6D@C|8dtUcJ|L(kfYzK>5-7/2d.-*TWFG(d^lbEVF7Vc.bByT#z/(5zJ>l%c4@F4(HjkJFVy*jtV.|5FBcA/-52WR4VRYb@R%*XdDY*REt*f*|@4tH>5tYLy*;29FX7|Az6:4&-*|JRBtSRjZ|5FBc-N|S|HzZ3D;9K>:H|V2J)N29R+^XZN/#Aj@bR*2W>YFAB1k9Fk%LF2M|*KRDHER1Y/ztt<2.ACzW>OPPPPppPPpPpzd#%*TVM%lJV)q7RzDY*-*TVAqVyC.yO.LkJ|6W)_R8tLy*T@Al:E7t_@4E7t_V)q.^6BtS|5fY2F8^k4c",
				"TzZ399:k9f&U2d*_S@/bHT@4EPZBP#HNR(X>/V%+KG&@czSqD/V>tECM+N^<zS_p8d*_SBOb)TJtE&C#qPKPA|VJF7zyKGWHFfSOG12H.KCOJ+J+2yFGU_>YE_5Jc-(EyPEpkNf_p+XAAyK/9D5N::Np3UtWZJ6/Z@PMTZ&>WL-ClF.|B+bNpR.lp5-J8M2U/V%8J2D>t3:8ARL^VK21Oly*BPM^OV1;#%CEK_7%pOTdSG2dCMfWTZPUWLXCzKz;;GXK8|2YWVL*-Kcb4+lRK-*cD%cG|Kcb.+lyJb&PkWG6FBc|2clLB|*P|5F(bKcb.cVRJ|*KP|5FBc|HERGM",
				"tzZ@78:k>LjUPF&DfB/dHTJ3>jC2P^)NRGbU4+%^KPSp7>RWDqVUtEZMcz:k8_DUP@(DyJO-1TB.FSC|WjE;j-+JMRzyJ6qHMy_/2X3)3K#/JcJ7@_fGU&UYNj#Jc|H>yc-UkzySUPF*PyK/NjZ>;PEUdUtqZKYqCJPFTCjUq_-519PVc+c999+2.6-Kl<9UO+%PJ|DUt-P;P_yA+J9X/+f&BcM*O+X;d%Z>Jjf%UqTd_G@dCMyqTZjU/Rb#<BE;;G<p^|.Yq+R*-Kcb9l+RK-*cD%4G|KpM.l+RJb:Pkq6GFBc@3c+yB|*P|5F6MB^b.cVRJ|*J4|5FBcdHER6M",
				"tzZ@78:k>LjUPF&DfB/dHTJ3>jC2P^)NRGbU4+%^KPSp7>RWDqVUtEZMcz:k8_DUP@(DyJOb1TB.FSC|WjE;j-+JMRzyJ6qHMy_/2X3)3K#/JcJ7@_fGU&UYNj#Jc|H>yc-UkzySUPF*PyK/NjZ>;PEUdUtqZKYqCJPFTCjUq_-519PVc+c999+G.6-Kl<9UO+%PJ|DUt-P;P_yA+J9X/+f&BcM*O+X;d%Z>Jjf%UqTd_G@dCMyqTZjU/Rb#<BE;;G<p^|.Yq+R*-Kcb3l+RK-*cD%4G|KpM.l+RJb:Pkq6GFBc@3c+yB|*P|5F6MB^b.cVRJ|*J4|5FBcdHER6M",
				"tzZ@78:k>LjUPF&DfB/dHTJ3>jC2P^)NLGbU4+%^KPSp7>RWDqVUtEZMcz:k8_DUP@(DyJOb1TB.FSC|WjE;j-+JMRzyJ6qHMy_/2X3)3K#/JcJ7@_fGU&UYNj#Jc|H>yc-UkzySUPF*PyK/NjZ>;PEUdUtqZKYqCJPFTCjUq_-519PVc+c999+G.6-Kl<9UO+%PJ|DUt-P;P_yA+J9X/+f&BcM*O+X;d%Z>Jjf%UqTd_G@dCMyqTZjU/Rb#<BE;;G<p^|.Yq+R*-Kcb3l+RK-*cD%4G|KpM.l+RJb:Pkq6GFBc@3c+yB|*P|5F6MB^b.cVRJ|*J4|5FBcdHER6M",
				"XHf;1%*pXzZ297|CqpXkj;^C-K%TA;DZK976*#9t)(*ft((G4;@yW:pM<S66SS+>++++&#lT#RE81+j1+jU+j1+*##>P.4%;7yK:pW<Y91T^JSJDH.y85KK/:pWd+.9XdR/2%LD<q;KcN.4JTRJHER(5KG_DHNNc5)b||5FBc<-C|U:1F|pL*|JR*A-%8#(6dWU:-2%7/_HK+b%Mb:GE5*G5&yd>5d.JD^NcJ)-*|5FBc.Xcl8:1F+AX|*-pbDJ*Vc.bDXq+7y9PJHyG*JzXNCJ1bTF|#FPPLt#3>H#MT.y;<Nc_KX|+9.yMO|j1+jK6jU+6#933^fWF.4XROcJ1",
				"tzZ@78:kNL&UPF:&fB/bHTp.EjCSP^)2RG-U/V%PKP&pc>RW&/+UEECMLz:k8_jU7<(jyJq@HTB9F&C|WjJ;j-VJMREyJPqHMy*/6X9).K#Op7pL@_fGU&|YNj#JcFH5yc-Ukzy&UPF*jyK<NjZ>;2E|dUtqZpYqZJPM3C&UO_-Cc9b.GbG39b.cc6-J^<9Uq+%PP7jUt|P;DyR(+J.XO1_AJPM*q+X;d%Z>JDR%U/Td_G@dCMyqTZ&U/R-C2BE;;G<B^|9Yq1R*-Kcb9l+RS-*cj%4G|KlM.l+fJ-:Pkq6GFBc@9c+yB|*P|5F6dJ4b.cVRJ|*J4|5FBcMHER6M",
				"tzZ@78:kNLDUPF:&fB/bHTp.EDCSP^)ERG-U/1%PKPjpc>_W&/+UEE|MLz:k8_&UL<(jyJq@HTB9F&C|WjJ;j-VJMREyJPqHMy*/6X9).K#Op7pL@_fG|&UYNj#JcFH5yc-Ukzy&UPF*jyK<NjZ>;;E|dUtqZpYqZJPMTCjUO_-C..b.P7b39c9cc6-J^|9Uq+%_P7jUt#P;Dy_(VJ9XO+fAJPM*/+X;d%Z>JjR%U/Td_G@dCMyqTZ&U/RbC2BE;;G<B^|9Yq1R*-Kcb9l+RS-*cj%4G|KlM.l+RJ-:Pkq6GFBc@9c+yB|*P|5F6MJ4b.cVRJ|*J4|5FBcdHER6M",
				"tzZ@78:kNRDUPF:&fB>bHTp.EDCSP^)ERG-U>V%PKPDpc/_Wj>+UEE|MLz:k8_&UL<(jRJq@HTB9F&C|WjJ;j-VJMREyJPqHMy*>6X.).K#Op7pL@_fG|&UYNj#JcFH5yl-Ukzy&UPF(jyK<NjZ/;2E|dUtqZpYqZJPMTCjUO_-#..b9PSb3.c.cc6-J^|9Uq+%_P7jUt|P;Dy_(+P.XO+_AJPM(q+X;d%Z/JjR%U>Td_G@dCMyqTZ&U>RbC2BE;;G<B^|9Yq1R*-Kcb9l+RS-*cj%4G|KlM.l+fJ-:Pkq6GFBc@9c+yB|*P|5F6MJ4b.cVRJ|*J4|5FBcdHER6M",
				"Uj3/K7&8Ol>pYF:J_#HHP<A*-kWO2Sf)#<lX_.@%|^R;#zdP+f7Z8^23TjCpdGAS%B;)27fd/Cl*zZ*DA:CHb>)MXVSWE3+8YTWyZyY4GGfKjT+2dVc.b>5C<)|5FBcC^%c3;N9+MLb(|WD%AK2.f;:_S-1R814(1W/Nb7WF(-L4CtG@yzHERB7@p9Rt%_|5FBcN%*|JR>NM5b&qTZ6/F(|<PY#kDDk<V#V.UYDJlAM9ZCJTK-:HpBbyR3@>ZB5p_c:4z^|HPG)d/;SlGUHFW8-*2Old&3N6^.V;:_zLJjJVdpKj+(z4y4(1@cPD1;9P8j^5>)5*lypS9|f/KA1A",
				"XHf;1%*pXzZ297|CqpXkj;^C-K%TA;DZK976*#9t)(*ft((G4;@yW:pM<S66S>+>++++&#lT#RE81+j1+jU+j1+*##>P.4%;7yK:pW<Y91T^JSJDH.y85KK/:pWd+.9XdR/2%LD<q;KcN.4JTRJHER(5KG_DHNNc5)b||5FBc<-C|U:1F|pL*|JR*A-%8#(6dWU:-2%7/_HK+b%Mb:GE5*G5&yd>5d.JD^NcJ)-*|5FBc.Xcl8:1F+AX|*-pbDJ*Vc.bDXq+7y9PJHyG*JzXNCJ1bTF|#FPPLt#3>H#MT.y;<Nc_KX|+9.yMO|j1+jK6jU+6#933^fWF.4XROcJ1",
				"tzZ@78:kNL&UPF:&fB/bHTp.EjCSl^)2RG-U/V%PKP&pc>RW&/+UEECMLz:k8_jUL<(jyJq@HTB9F&C|WjJ;j-VJMREyJGqHMy*/6X9).K#Op7pL@_fGU&|YNj#JcFH5yL-Ukzy&UPF*jyK<NjZ>;2E|dUtqZpYqZJPM3C&UO_-C.9b.G7b39c9cc6-J^|9Uq+%PP7jUt|P;DyR(+J.XO1_AJPM:q+X;d%Z>JDR%U/Td_G@dCMyqTZ&U/R-C2BE;;G<B^|9Yq1R*-Kcb9l+RS-*cj%4G|KlM.l+fJ-:Pkq6GFBc@9c+yB|*P|5F6dJ4b.cVRJ|*J4|5FBcMHER6M",
				"4HcS1fl*-zZ2D8|CF|-X;bqc+L|GPbzZz98FOD8W6@FDW__.%bRkUjP(.GU5:7VV5VVVqDtMW.EU)|;dK7t*7(*KAD66.y|CREUjYURqWNlqJAJ<H.ydJL%(p|#RM/Wb.RN23C<.l|1b>REJ|.JqyR65.TJzH>>cJLS||5FBcT-cl#pN;@P+*|JRO|-|#9@|T#(jCX|8tJqE|b3&Sp.kJ*R5Hy/_JR.52q^cJL-*|5FBcRCcltj#:|Yb|*-|bX5GVc.bXb#M8kD6JHERlJz+^cJLCMlFDA66K9W_6HW(:.ySR>c5)bG4D.ENLO;d|;d|7t*FWW|6HWdMR%b.|cJ1",
				"b<c2/fU_bzZz97|CFXbNA24cb8J|X-NZDM7k|;7^ttq9*)l.E2GESp_|.q>>q>>VV(VVW@TU;.y6|*P6F8SFAK|*^9l).Ejb.yYpXSd&;T*45*JDW.E6J8E6%LkG*d9bRR:DfbzRk2/O1GEJURJHER)5..JNW33c5/C||5FBcR-c|KpS|+L-*|JRFL-fK@lkdSTpOzj7:J<|qbf>2%REJq#5W|RtJd.JD41cJ8-*|5FBc#bcU:%S||LC|*-LbDJ+Vc.bDb*+7E;)JPE.FJ/b1cJ/bkU*;^)l|@M)tH^YF.EbR1c&|b*z;dE>/+P2*A5+P>|*9*l)W*6(.EbR3cj/",
				"Y@SW6Z<SU6S*:DD^8@U(.&4ZZNXl%5S.35S8HERcL7JY#(84l_._*3DA|FJ+ZVc.b;3.B|E&NWZA-XcdKRO-jF*-AH)WSP^.bdd+654dFBcJG_dUbp1/XUS:O_H6bj>pH9F._|JDCK8j4b5YHy95P3WWY-W+TZl4KR1bOl|tW-Fb*2@<7dz3_O|5F./8)CCN8qjS8CXU2PMGNjOBTlpZb#bTAp()-.k@8czZ)dA:CM3KR;bzC*|JRZpXHjCcXL1c8l|O4lT;>jk|5FBcdXNJ>j(c9Z8K3d_DBH^*RA3OUJz/dcPKU(-*R283*#XHW2fR7*RW|FFWRC*TY3jBT&Bq",
				"-Hcb1f:*bzZ2D8|CO|-2;bHc+L3GPb2Zz98KOD8W||FAW6_.%bRkdjPt.UVVV7VVU1VVqDtMW.Et)l7dK7t*7(GMAD66Ry3CREUjYdRqWN|qJAJ<H/ydJL%dp|#RM/Wb.RN23C<.l|1b>REJ|.JqyR6J.TJzH>>cJLS||5FBcT-clNpt;|P+*|JRO|-3UW@|R#(jCX38dJqE|b3&SpTk5*R5Hy/@5R.52H>cJL-*|5FBcRCcl&jd:|Yb|*-|bX5GVc.bXb#M8kD@JHERlJz+^cJLCMlFDW6_K9W6|HWp:.ySR>c5)bG4D.ENLO;d|;d|;t*FWW||HWdlR%b&>cJ1",
				"TzZ399:ktf&U2d^_yK/-HTB4ED5J8F)N7(XpWl%c@G&J89RqD/V>tECF+N^kzS_p+F*_SBOj)TJ.E&C#qPK;A|VJF7zyKGWHFfSOG12H.K3OJ+J+2yFGp_>YE_5Jc-HEyPEpkNf_p8XAAyB/9D5N;;N<3UtOZJY/Z@PMTZ&UWL-Cl.+|RPKFGp||#5-J8-2UWl%PJc^>t|:8AyL^VJ21WVy*BcM^OV1;#%CEJ_7%UOTdSG2dCMfWTZPUWLXCEKz;;GXK8|26WVL*-Kcb.+lRK-*8D%cG|KP|.+VyJb&PkWG6FBc|2clLB|*P|5F(bK+b.cVRJ|*KP|5FBc|HERGM",
				"UzZ@78:kN7&/PF:PfB/dHTp3>jCB4^)ERG-t/1%c26j2^z_WDO+tUE|MLz:k8_Dt4<(jyJq-HTB9F&C|WDJ;D-VJMREyJ6qHby_O6X.).K#42PpL@_fGl&tYNjZJ4|H5yc1tkzyjtcF*jyK/6jZ>;PE|dtUqZpY/ZJPFTCjt<_-CRcb.cYYScjMSc6-B^d9tqV%c2^jtU|P;&Ry*1J.XqVRAJLM(q+X;d%Z>Jj_%tOTd_G@dCMy/TZ&t/RbC2BE;;G<Bc|9Yq+R*-Kcb9l+RS-*cj%4G|KcM.l+RJ-#Pk/6GFBc@9c+yB|*P|5F6FJ4b.cVRJ|*J4|5FBcdHER6M",
				"tzZ@78:k>LPUPF&DyB/dHTE.>jC2P^)NRGbUq+%^KGSp^N_W6/VUtEZMcz:k8_DUP@(DyJq-)TB.FSC|WjE;j-VJMREyJ6/HMy_/2X3).K#/JcJ7@_<GU&UYNj#Jc|H>yc-UkzySUPF*SyK/NjZ>;PEUdUtqZpY/CJPFTCjUq_-519P+cWc9K9+K95-4l-3UqV%l4|DUt|P;P_yA+J9XqVL&2cM*/+X;d%Z>Jjf%UqTd_G@dCMyqTZjU/RbO>BE;;G<p^|.Yq+R*-Kcb3l+RK-*cD%4G|KpM.l+RJ-:Pkq6GFBc@3c+yB|*P|5F6ME^b.cVRJ|*Jl|5FBcdHER6M",
				"tzZ@78:kNf&|PF:&yB/|HTp.>DCSl^)ERG-U/V%cKPDpL>_W&/+U4E|MLz:k8_&Uc<AjyJ6@HTB9F&C|WjJ;j-4JMREyJP6HMy*/qX.).K#Op7pL@_fGl&UYNj#JcF)5yc-Ukzy&UP<*jyK<NjZ>;2E|dUt6ZpY61JP-TCjUO_-C.9(9cLb3.c9(cq-J^|.U6V%LPLDUtMP;jy_*1J9XOVfAJPM(O+X;d%Z>Jjf%U/Td_G@dCMy6TZ&U/RbCzBE;;G<B^|9Y6+R*-Kcb9l+RS-*cj%LG|K7B.l+fJ-:Pk6qGFBc@9c+yB|*P|5FqMJlb.cVRJ|*Jc|5FBcdHERqM",
				"TzZ399:ktf&U2d^_yK/-HTB4ED5J8F)N7(XpWl%c@G&J89RqD/V>tECF+N^kzS_p+F*_SBOj)TJ.E&C#qPK;A|VJF7zyKGWHFfSOG12H.K3OJ+J+2yFGp_>YE_5Jc-HEyPEpkNf_p8XAAyB/9D5N;;N<3UtOZJY/Z@PMTZ&UWL-Cl.+|RPKFGp||#5-J8-2UWl%+Jc^>t|:8AyL^VJ21WVy*BcM^OV1;#%CEJ_7%UOTdSG2dCMfWTZPUWLXCEKz;;GXK8|26WVL*-Kcb.+lRK-*8D%cG|KP|.+VyJb&PkWG6FBc|2clLB|*P|5F(bK+b.cVRJ|*KP|5FBc|HERGM",
				"VZVVp.F8VANcV7dKc.|<DY;F1dP5U.|S3cO6z.b5-kpczJ-*@9|GOd^1FEVRkVc.bNy;Jc<P5d.R1Lc&Yb&PG8%JFVy*E3V.|5FBcl<2qc9R&VR6bYR#*XCD6*RVP*V*FY&3Vb53kS7(XCKV@6|Azp:&+-*|JRN34R8Z|5FBc-c|4|*zZ)D:9Kb:*|VCJMUdJ|+^X.z/.MEYVRHcJfjbl+BdKV%7VRC9|*JbDHERN6WcPPT%^Mcc9fbb.YJ.bJbY..cc.LH@V.#1JVA/6RcDk*-*XVApVyz.V*.SVJ|p.A_R>PV#G;YA1:8k3_Y&8c3_VA/J*/BP4|5O6cR12d&c",
				"TzZ399:kzf_U2d^_yKO-HTK4ED5J8F)Nf(XU/l%c@G&J89RqD/V>tECF+N*<zS_p+F*_SBOM)TJ.E&C#qPK;A|VJF7zyKGWHFfSOG12H.K3/J+J+2yFGp_>YE_5Jc-(EyPEpkNf_p8XAAyB/9D5N;;NU3UtWZJY/Z@PMTZ&UOL-Cl.+|RPJFGp|b#5-J8-2UOl%+J+^>t|:8AyL*VJ21Wly^BcM^OV1;#%CEJ_7%UOTdSG2dCMfWTZPUWLXC9Kz;;GXK8|26WlL*-Kcb.+lRK-*8D%cG|KP|.+VyJb&PkWG6FBc|2clLB|*P|5F(bK+b.cVRJ|*KP|5FBc-HERGM",
				"tzZ@78:kNf&|PF:&yB/|HTp.>DCSl^)ERG-U/V%cKPDpL>_W&/+U4E|MLz:k8_&Uc<AjyJ6@HTB9F&C|WjJ;j-4JMREyJP6HMy*/qX.).K#Op7pL@_fGU&UYNj#JcF)5yc-Ukzy&UP<*jyK<NjZ>;2E|dUt6ZpY61JP-TCjUO_-C.9(9cLb3.c9(cq-J^|.U6V%LPLDUtMP;jy_*1J9XOVfAJPM(O+X;d%Z>Jjf%U/Td_G@dCMy6TZ&U/RbCzBE;;G<B^|9Y6+R*-Kcb9l+RS-*cj%LG|K7B.l+fJ-:Pk6qGFBc@9c+yB|*P|5FqMJlb.cVRJ|*Jc|5FBcdHERqM",
				"U2V/K7&8Ol>pYF:J_#HHP<A*-kWO2Sf)#.lX_.@%|^R;#zdP+f7Z8^23TjCpdGAS%B;)27fd/Cl*zZ*DA:CHb>)MXVSWE3+8YTWyZyY4GGfKjT+2dVc.b>5C<)|5FBcC^%c3;N9+MLb(|WD%AK2.f;:_S-1R814(1W/Jb7WF(-L4CtG@yzHERB7@p9Rt%_|5FBcN%*|JR>NM5b&qTZ6/F(|<PY#kDDk<V#V.UYDJlAM9ZCJTK-:HpBbyR3@>ZB5p_c:4z^|HPG)d/;SlGUHFW8-*2Old&3N6^.V;:_zLJjJVdpKj+(z4y4(1@cPD1;9P8j^5>)5*lypS9|f/KA1A",
				"4HcS1fl*-zZ2D8|CF|-X;bqc+L|GPbzZz98FOD8W6@FWW__.%bRkUjP(.GU5:7VV5VVVqDtMW.EU)|;dK7t*7(*KAD66.y|CREUjYURqWdlqJAJ<H.ydJL%(p|#RM/Wb.RN23C<.l|1b>REJ|.JqyR65.TJzH>>cJLS||5FBcT-cl#pN;@P+*|JRO|-|#9@|T#(jCX|8tJqE|b3&Sp.kJ*R5Hy/_JR.52q^cJL-*|5FBcRCcltj#:|Yb|*-|bX5GVc.bXb#M8kD6JHERlJz+^cJLCMlFDA66K9W_6HW(:.ySR>c5)bG4D.ENLO;d|;d|7t*FWW|6HWdMR%b.|cJ1",
				"tzZ@78:k>LjUPF&DfB/dHTJ3>jC2P^)NRGbU4V%^KPSpc>RWDqVUtEZMcz:k8_DUP@(DyJO-1TB.FSC|WjE;j-+JMREyJ6qHMy_/2X3)3K#/JcJ7@_fGUPUYNj#Jc|H>yc-UkzySUPF*PyK/NjZ>;PEUdUtqZKYqCJPFTCjUq_-513DVc+c993+2.6-Bl<3UO+%PJ|DUt-P;P_yA+59Xq+L&BcM*O+X;d%Z>Jjf%UqTd_G@dCMyqTZjU/RbO>BE;;GMp^|.Yq+R*-Kcb3l+RK-*cD%4G|KpM.l+RJb:Pkq6GFBc@3c+yB|*P|5F6MB^b.cVRJ|*J4|5FBcdHER6M",
				"tzZ@78:kNLDUPF:&fB/bHTp.EDCBP^)ERG-U/V%lKPDpc>_W&/+UEE|MLz:k8_&UL<(jyJq@HTB9F&C|WjJ;j-VJMREyJPqHMy*/6X.).K#Op7pL@_fG|&UYNj#JcFH5yl-Ukzy&UPF*jyK<NjZ>;2E|dUtqZpYqZJPMTCjUO_-C.3b.PSb39c9cc6-J^|9Uq+%_P7jUt|P;Dy_(+B.XO+_AJPM(q+X;d%Z>JjR%U/Td_G@dCMyqTZ&U/RbC2BE;;G<B^|9Yq1R*-Kcb9l+RS-*cj%4G|KlM.l+fJ-:Pkq6GFBc@9c+yB|*P|5F6MJ4b.cVRJ|*J4|5FBcdHER6M",
				"tzZ@78:k>LjUPF&DfB/dHTJ3>jC2P^)NRGbU4V%^KPSpc>RWDqVUtEZMcz:k8_DUP@(DyJO-1TB.FSC|WjE;j-+JMREyJ6qHMy_/2X3)3K#/JcJ7@_fGUPUYNj#Jc|H>yc-UkzySUPF*PyK/NjZ>;PEUdUtqZKYqCJPFTCjUq_-513DVc+c993+D.6-Bl<3UO+%PJ|DUt-P;P_yA+59Xq+L&BcM*O+X;d%Z>Jjf%UqTd_G@dCMyqTZjU/RbO>BE;;GMp^|.Yq+R*-Kcb3l+RK-*cD%4G|KpM.l+RJb:Pkq6GFBc@3c+yB|*P|5F6MB^b.cVRJ|*J4|5FBcdHER6M",
				"tzZ@78:kNLDUPF:&fB/bHTp.EDCBP^)ERG-U/V%lKPDpc>_W&/+UEE|MLz:k8_&UL<(jyJq@HTB9F&C|WjJ;j-VJMREyJPqHMy*/6X.).K#Op7pL@_fG|&UYNj#JcFH5yl-Ukzy&UPF*jyK<NjZ>;2E|dUtqZpYqZJPMTCjUO_-C.3b.PSb39c9cc6-J^|9Uq+%_P7jUt|P;Dy_(+B.XO+yAJPM(q+X;d%Z>JjR%U/Td_G@dCMyqTZ&U/RbC2BE;;G<B^|9Yq1R*-Kcb9l+RS-*cj%4G|KlM.l+fJ-:Pkq6GFBc@9c+yB|*P|5F6MJ4b.cVRJ|*J4|5FBcdHER6M",
				"bHcO13l*CzZ2D8|Cl|-<;bqc+1|GPb2ZXA8FO98A||4AW6@.%bR>djPt.97VV77VVVVVqDtMW.Et)|;dK7t*7(G*AD66RyfCREUjYdRqWNlqJAJ<H.ydJL%dp|#RM/Wb.RN23C<.l|1bkREJ|.JqyR6J.TJ<HkkcJLS||5FBcT-cMNpt;|PS*|JRO|-|UW6|T#(jCX|8dJqE|b3&Sp/>JM.5Hy/_5R.52q^cJL-*|5FBcRCcl&jd:|Yb|*-|bX5GVc.b<b#M8>D@JHERlJz+^cJLCMlFDW6_K9W6|HWp:.ySRkc5)bG4D.ENLO;dF;d|;t*FWW||HWdlR%bR|cJ1",
				"tzZ@78:kNL&UPF:&fB/bHTp.EjCSP^)2RG-U/V%PKP&pc>RW&/+UEECMLz:k8_jU7<(jyJq@HTB9F&C|WjJ;j-VJMREyJPqHMy*/6X9).K#Op7pL@_fGU&|YNj#JcFH5yc-Ukzy&UPF*jyK<NjZ>;2E|dUtqZpYqZJPM3C&UO_-Cc9b.GbG39b.cc6-J^|9Uq+%PP7jUt|P;DyR(+J.XO1_AJPM*q+X;d%Z>JDR%U/Td_G@dCMyqTZ&U/R-C2BE;;G<B^|9Yq1R*-Kcb9l+RS-*cj%4G|KlM.l+fJ-:Pkq6GFBc@9c+yB|*P|5F6dJ4b.cVRJ|*J4|5FBcMHER6M",
				"zO35/F87ZfP9U+<YAA8ZO.L/B48qFpS/fB^#V:EtZS(8N2D^9L.K.q/cSPCy%F*UDPPBq<YA)8SPWb<jU9FSP78qZ7Zt#S.NSF%RL&5M5Lt#S|8c2M^Bk.O.y7#FP7UFVc.bC#SPD>U13*7H_B%jbRCT7-VNb_|5FBcYqR7D7-PfqPO|5FBcjqJCWT6FHERWT6CMkR3|X7;kl|*3<^YF4-R)*|JREXF574EG-(BTC)*:L*kpc*7RR7&CD.VDDG72K2:L@q;B:q*-lR+XNlC>MG(DER>|pCH+*>|p:DLl.UH@8H_<+C-*7AZPy1c2d7-7@kzcPTlCU#P1zZ2d9;C6",
				"tzZ@78:k>LjUPF&DfB/dHTJ.>jC2P^)NRGbU4+%^KPSpc>RWDq+UtEZMcz:k8_DUP@(DyJO-1TB.FSC|WjE;j-+JMRzyJ6qHMy_/2X3)3K#/JcJ7@_fGU&UYNj#Jc|H>yc-UkzySUPF*PyK/NjZ>;PEUdUtqZKYqCJPFTCjUq_-519PVc+c999+P.2-Kl<3UO+%PJ|DUt-P;P_yA+J9X/+L&6cM*O+X;d%Z>Jjf%UqTd_G@dCMyqTZjU/RbO>BE;;G<p^|.Yq+R*-Kcb3l+RK-*cD%4G|KpM.l+RJb:Pkq6GFBc@3c+yB|*P|5F6MB^b.cVRJ|*J4|5FBcdHER6M",
				"tzZ@78:kNL&UPF:&fB/bHTp.EjCSP^)2RG-U/V%PKP&pc>RW&/+UEECMLz:k8_jUL<(jyJq@HTB9F&C|WjJ;j-VJMREyJPqHMy*/6X9).K#Op7pL@_fGU&|YNj#JcFH5yc-Ukzy&UPF*jyK<NjZ>;2E|dUtqZpYqZJPM3C&UO_-C.9b.Gcb39c93c6-J^|9Uq+%PP7jUt|P;DyR(+J.XO1_AJPM:q+X;d%Z>JDR%U/Td_G@dCMyqTZ&U/R-C2BE;;G<B^|9Yq1R*-Kcb9l+RS-*cj%4G|KlM.l+fJ-:Pkq6GFBc@9c+yB|*P|5F6dJ4b.cVRJ|*J4|5FBcMHER6M",
				"TzZ399:kNf&U2d*_y@/bHT@4EPZBP#HNf(X>/V%+KG&@czSqD/V>tECM+N^<zS_p8d*_SBOb)TJtE&C#qPKPA|VJF7zyKGWHFfSOG12H.KCOJ+J+2yFGU_>YE_5Jc-(EyPEpkNf_p+XAAyK/9D5N::Np3UtWZJ6/Z@PMTZ&>WL-ClF.|O+MNpR.l#5-J8M2U/V%8J2D>t3:8ARL^VK21Oly*BPM^OV1;#%CEK_7%pOTdSG2dCMfWTZPUWLXCzKz;;GXK8|2YWlL*-Kcb4+lRK-*cD%cG|Kcb.+lyJb&PkWG6FBc|4clLB|*P|5F(bKcb.cVRJ|*KP|5FBc|HERGM",
				"V.|G/Z|EVALcV#dJcZ|6D@;b8ct54.|&3cO7zZRUfkqcc9-*TZ|G3d^>FEVRkVc.bLyXKYjt5d.R>#cN@bNtGVYJFVy*EtV.|5FBc1j2Uc9RVVR7b@Rf*XCD7*REt*V*|@N3*b5Vk&%*XCKRT7bAz/:N+-*|JRL3SREZ|5FBc-c|S|*zZ(D;9Kb:HlVYJACdK|L^X.4j.MV@bRHp.F)P1+Bz.Rc%&RC9|GJbDHERL76p<3Vc.MccJl_.bb..Jbbb.6cc.#H:V.f>JVA/7RcDk*-GXVAjVy2.V*.&VJ||.A_R83VfG;@A>:Ok(_@NVc3_VA/J*qBOS|537pR8#dNc",
				"bHcO13l*CzZ2D8|Cl|-<;bqc+1|GPb2ZX98FO98A||4AW6@.%bR>djPt.97VV77VVVVVqDtMW.Et)|;dK7t*7(G*AD66RyfCREUjYdRqWNlqJAJ<H.ydJL%dp|#RM/Wb.RN23C<.l|1bkREJ|.JqyR6J.TJ<HkkcJLS||5FBcT-cMNpt;|PS*|JRO|-|UW6|T#(jCX|8dJqE|b3&Sp/>JM.5Hy/_5R.52q^cJL-*|5FBcRCcl&jd:|Yb|*-|bX5GVc.b<b#M8>D@JHERlJz+^cJLCMlFDW6_K9W6|HWp:.ySRkc5)bG4D.ENLO;dF;d|;t*FWW||HWdlR%bR|cJ1",
				"tzZ@78:kNRDUPF:&fB>bHTp9/DCSP^)ERG-U>V%PKPDpc/_W&>+UEE|MLz:k8_&UL<(jRJq@HTB9F&C|WjJ;j-VJMREyJPqHMy*>6X.).K#Op7pL@_fG|&UYNj#JcFH5yl-Ukzy&UPF*jyK<NjZ/;2E|dUtqZpYqZJPMTCjUO_-#.9b9PSb3.c.cc6-J^|9Uq+%_P7jUt|P;&y_(+B.XO+_AJPM(q+X;d%Z/JjR%UqTd_G@dCMRqTZ&U>RbC2BE;;G<B^|9Yq1R*-Kcb9l+RS-*cj%4G|KlM.l+fJ-:Pkq6GFBc@9c+yB|*P|5F6MJ4b.cVRJ|*J4|5FBcdHER6M",
				"-Hcb1f:*bzZ2D8|CO|-X;bHc+L|GPb2Zz98KOD8W||FAW6_.%bRkdjPt.VVVV7VVU1VVqDtMW.Et)l7dK7t*7(GMAD66Ry|CREUjYdRqWN|qJAJ<H/ydJL%dp|#RM/Wb.RN23C<.l|1b>REJ|.JqyR6J.TJzH>>cJLS||5FBcT-clNpt;|P+*|JRO|-3UW@|T#(jCX|8d5qE|b3&SpTk5*R5Hy/@5R.52q>cJL-*|5FBcRCcl&jd:|Yb|*-|bX5GVc.bXb#M8kD@JHERlJz+^cJLCMlFDW6_K9W6|HWp:.ySR>c5)bG4D.ENLO;d|;dK;t*FWW||HWdlR%b&>cJ1",
				"b*c-/fFLbzZz97|CFXbNA24cb8J|XbDZzM7k|;7^ttq;*)l.E2GESp_|.q>>qVVVV(VVW@TU;.y6|*P6F8SFAK(*^9l).Ejb.yYpXSd&;T*45*JDW.E6J8E6%LkG*d9bRR:DfbzRk2/O1GEJURJHER)5..JNW33c5/C||5FBcR-c|KpS|+L-*|JRU_-fK@lkdSTpONj7:J<|*bf>2%REJq#5WYRtJd.JD41cJ8-*|5FBc#bcU:%S||LC|*-LbDJ+Vc.bDb*+7E;)JPE.FJ/b1cJ/bkU*;^)l|@M)tH*YF.EbR1c&|b|F;dE>/+P2*A5+P>|*9*l)W*6(.EbR3cj/",
				"-Hcb1f:*bzZ2D8|CO|-2;bHc+L|GPb2Zz98KOD8W||FAW6_.%bRkdjPt.UVVV7VVU1VVqDtMW.Et)l7dK7t*7(GMAD66Ry|CREUjYdRqWN|qJAJ<H/ydJL%dp|#RM/Wb.RN23C<.l|1b>REJ|.JqyR6J.TJzH>>cJLS||5FBcT-clNpt;|P+*|JRO|-3UW@|R#(jCX|8dJqE|b3&SpTk5*R5Hy/@5R.52H>cJL-*|5FBcRCcl&jd:|Yb|*-|bX5GVc.bXb#M8kD@JHERlJz+^cJLCMlFDW6_K9W6|HWp:.ySR>c5)bG4D.ENLO;d|;d|;t*FWW||HWdlR%b&>cJ1",
				"bHcO13l*CzZ2D8|Cl|-<;bqc+1|GPb2ZX98FO98W||4AW6@.%bR>djPt.97VV77VVVVVqDtMW.Et)|;dK7t*7(G*AD66RyfCREUjYdRqWNlqJAJ<H.ydJL%dp|#RM/Wb.RN23C<.l|1bkREJ|.JqyR6J.TJ<HkkcJLS||5FBcT-cMNpt;|PS*|JRO|-|UW6|T#(jCX|8dJqE|b3&Sp/>5:.5Hy/_5R.52q^cJL-*|5FBcRCcl&jd:|Yb|*-|bX5GVc.b<b#M8>D@JHERlJz+^cJLCMlFDW6_K9W6|HWp:.ySRkc5)bG4D.ENLO;dF;d|;t*FWW||HWdlR%bR|cJ1",
				"VZRH)K|4LfkcVSM.V9|AWNV|Xc35KJ|TPD(cc9F5#q/Uc.-*@kbGOD:XbEVRCVc.b+SdZcAP5cJ|X7c&NR&PGVUK|V%H4>V.|5FBcYAV5cJL&VRVbN|-*CcWqG|4(*PHFN&3*R5PtTSGdcKR@6Rfcyp&l-*|JRl3>RE.|5FBc-V|>b*zZ(8;9k|pG|VcJ1ccJb^%j.cAKYVN|RGMZFt|1l+DZ|c%TRzKRGJFWHEROqycV3Ec.fUM.|Jbb9RyJ*bR*9ccJ#*@VJ-X9Vf)VbcWq*-GdVf)VSDJV*KTUK|A.Y_RXPT-H@NYXpVqP_N&4t3_Vf/Z-ABP>R5(VcRX:U&c",
				"TzZ399:kNf&U2d*_y@/bHT@4EPZBP#HNR(X>/V%+KG&@czSqD/V>tECM+N^<zS_p8d*_SBOb)TJtE&C#qPKPA|VJF7zyKGWHFfSOG12H.KCOJ+J+2yFGU_>YE_5Jc-(EyPEpkNf_p+XAAyK/9D5N::Np3UtWZJ6/Z@PMTZ&>WL-ClF.|O+MNpR.l#5-J8M2U/V%8J2D>t3:8ARL^VK21Oly*BPM^OV1;#%CEK_7%pOTdSG2dCMfWTZPUWLXCzKz;;GXK8|2YWlL*-Kcb4+lRK-*cD%cG|Kcb.+lyJb&PkWG6FBc|4clLB|*P|5F(bKcb.cVRJ|*KP|5FBc|HERGM",
				"LHC-1>l^;zZ297|CFAXkjLYcb1%MpLzZK#7l*t#9((Tf%P1<:XR:7yp+<USUFMM;5MlFNt1Tf.:+KMjUMjAljUMltAP).:>;<43yA+<&t8l&JSJDY@:861:Uyp8dT.fLdRW2>LDdl;KcO.4JT.JHER3H<G_DNOOCJ1b||5FBcR-CqWyUl-A;*|JRTp->8t3|@8Ay;D>7+6N4lb>8;yGE_M.6&4d(5d.6DNOcJ1-*|5FBc.Xcl8y21MpX|*-pbDJTVc.bDXql7:tPJ&:.*JzXOcJ(bTl|f9P)1t#PPY#+q@:;GOc_1Xqq9.68KOjUFj+ljWM*#9PP&fWF.EX<OcJ1",
				"VZRH)K|4LfkcVSM.V9|AWNV|Xc35KJ|TPD(cc9F5#q/Uc.-*@kbGOD:XbEVRCVc.b+SdZcAP5cJ|X7c&NR&PGVUK|V%H4>V.|5FBcYAV5cJL&VRVbN|-*CcWqG|4(*PHFN&3*R5PtTSGdcKR@6Rfcyp&l-*|JRl3>RE.|5FBc-V|>b*zZ(8;9k|pG|VcJ1ccJb^%j.cAKYVN|RGMZFt|1l+DZ|c%TRzKRGJFWHEROqycV3Ec.fUM.|Jbb9RyJ*bR*9ccJ#*@VJ-X9Vf)VbcWq*-GdVf)VSdJV*KTUK|A.Y_RXPT-H@NYXpVqP_N&4t3_Vf/Z-ABP>R5(VcRX:U&c",
				"VK|G/Z|EVALcV#dJcZ|6D@;b8ct54.|&3cO7zZRUfkqcc.-*TZ|G3d^>FEVRkVc.bLyXKYjt5d.R>#cN@bNtGVYJFVy*EtV.|5FBc1j2Uc9RVVR7b@Rf*XCD7*REt*V*|@N3*b5Vk&%*XCKRT7bAz/:N+-*|JRL3SREZ|5FBc-c|S|*zZ(D;9Kb:HlVYJACdK|L^X.4j.MV@bRHp.F)P1+Bz.Rc%VRC9|GJbDHERL76p<3Vc.MccJl_.b3..Jbbb.6cc.#H:V.f>JVA/7RcDk*-GXVAjVy2.V*.&VJ||.A_R83VfG;@A>:Ok(_@NVc3_VA/J*qBOS|537pR8#dNc",
				"6&fEN;U8JV7pkF:J_tH^-OA*-9S6K+31t3lW:.3%|^3<tzdf@E;ZK^2>U&CydGA+%B<1z;3dTCl*zZ(DA:CHb71YWV+ST>@/kUSLZLk#GG3N&U@2dVc.b75CO1|5FBcC^%c><8/@WqbM|@D%AN2.3<:_+-4RK4#M4STPb;SFM-q#CXGjLzHERB;jpNRUM_|5FBc8%*|JR78Y5bF(yZ@TFM|P-9VVtkt.ktk.kVkJlAY/ZCJUN-:HyBllR>R7ZB5y:c:#;^|HfG1dT<_lG)HFSK-*2Oldp>1)K.V<:_(qJ&JVdyN#@MC#L#M4fcfD4</fK&^5715*lLy+/|3TNA4A",
				"TzZ399:kNf&U2d*_y@/bHT@4EPZBP#HNR(X>/V%+KG&@czSqD/V>tECM+N^<zS_p8d*_SBOb)TJtE&C#qPKPA|VJF7zyKGWHFfSOG12H.KCOJ+J+2yFGU_>YE_5Jc-(EyPEpkNf_p+XAAyK/9D5N::Np3UtWZJ6/Z@PMTZ&>WL-ClF.|O+bNpR.l#5-J8M2UWV%8J2D>t3:8ARL^VK21Oly*BPM^OV1;#%CEK_7%pOTdSG2dCMfWTZPUWLXCzKz;;GXK8|2YWlL*-Kcb4+lRK-*cD%cG|Kcb.+lyJb&PkWG6FBc|4clLB|*P|5F(bKcb.cVRJ|*KP|5FBc|HERGM",
				"VKbG/J|EVALcV#d.C.|6D@;F>ct54.|&3c<7zKRUfkqVPK-*TZ|G3d^>FEVRkVc.bLyXKcjt5d.R8#cN@bNtGVYJFVy*EtV.|5FBc1j2U49RVVR7b@Rf*XCD7HREt*V*b@N3*b5Vk&%*TCKRT7bAz/:N+-*|JRL3SROZ|5FBc-c|Sb*zZ(D;9Kb:HbVYJMzcJ|+^X.4jJMV@bR*YJbkF1+BCJbc%VRC9|GJbDHERL76p<3Vc.AccJlJ.bb..Jb)@..cc.#H:V.f8JVA/7RCDk*-*XVAjVyz.V*.&VJ||.A_R83VfG;@A8:Ok(_@NVc3_VA/J*qB(S|5O74R>#dNc",
				"UzZ@78:kNLj|cFAPfB/dHTp9>jCB4^)ERG-t/1%LK6D2cN_WDO1tUE|MLz:k8_Dt4<(jyJq-HTB9F&C-WDJ;j-VJMREyJ6qH*y_O6X.).K#42LpLd_fGl&tYNjZJ4|H5yc-tkzyjtcF*jyK/NjZ>;PE|dtUqZ@Y/ZJPFTCjt<_-C+cb.HYY3cjbSc6-2^|9|qV%c2^jtU|P;&yy*1J.XqV_A2LM(q+X;d%Z>Jj_%tOTd_G@dCMy/TZ&t/RbC2BE;;G<Bc|9Yq+R*-Kcb9l+RS-*cj%4G|KcM.l+RJ-:Pk/6GFBc@9c+yB|*P|5F6MJ4b.cVRJ|*J4|5FBcdHER6_",
				"156t7M(Ef8MzlY;f2qP*(zk(CB|KjV|K^EfHNKESU_-*2Fk|5FBc^pFFtN|BS*dklk7G6d2##MzZ6d;^KKVp3;78AGUl;MJ-pO*-p2C-Fz&-SJL*-Fk|5FBcpZ7&kpFDzV(/|V;pVzzpFFt8YpHERV)8D-8FNBVdp+tS-|8cJ-VFNCk--+PJ#BSVpPkSFEN>dpFkb.cV)p8PPD@pjy/8DdHVc&Z8<pFAV<pRJ|*Lp.%cyZ8pkAV<<A&|4Lb<8/L+)%y<S+//JdC&7LOO3>P%Z4<OyRAj&Xdly:Z:8DT#O38W+DKldJcJfj&P%L@yCWF+XT.Rj.XbZTTbXXW>9/#d",
				"bHcb1fl*CzZ2D8|CM|-<;bqc+L|GPb2ZX98FO98W||*AW6@R%bR>djPt.V7VV77VVVVVqDtMW.Et)|;dK7t*7(G*AD66Ry|CREUjYdRqWNlqJAJ<H.ydJL%dp|#RM/Wb.RN23C<.l|1bkREJ|.JqyR6J.TJ<HkkcJLS||5FBcT-cMNpt;|PS*|JRO|-|UW6|T#(jCX|8dJqE|b3&Sp.>JM.5Hy/_5R.52q^cJL-*|5FBcRCcl&jd:|Yb|*-|bX5GVc.b<b#M8>D@JHERlJz+^cJLCMlFDW6_K9W6|HWp:.ySRkc5)bG4D.ENLO;dF7d|;t*FWW||HWdlR%bR|cJ1",
				"9O35/F87Nf_9U+<YU(UZO&L<^WA)Np5/#^^ZV(LtZS78N2DB3L&O&qFcSP91%F#8DPPBq<Y8)8SP3b<jU(ZSP78qZ7Zt(S.5SF%RL&5M*Lt#S|Uc2M^Bk.O&17#FP78FVc.bC#SPD>U)4*7HZBFjbRCT7-VNbP|5FBcYqR7D7-Pc1PO|5FBcF1J74T67HER4|pCDkR4|p7M+l|*4<^jc4-Rq*|JRE6Fl74Ep-BBTC1*:l*kpc*7DRD.RDJ.J2..DpJ:L6qBB:q*-lR+XNlC>MG(DEK>|pCH+*>|6:Dkl.UH@UHVd+C-*78cP1zc2d7M7@kzcPT1C85PyzZ2d9;C6",
				"VZ|H)JbEbf+cV%cK49|yWN9|XD35DK|TPDO6MJ|5#d/UcL-*@KbGOD:X|EVRdVc.b+SqZcAP5cJRX7cVNR&P*1U.bV%*E(V.|5FBcYAV5cJb&VRVbNR%GCcWC*R4(*<*FN&33R5VtTS*qcJR@6Rfcyp&+-*|JRl31R4.|5FBc-c|1|GzZ(8;9kRpGRVcJ>VDkF^7j.zA8YV2|RGzkFt|>l+MJ|c:TRz.RGJFWHERK6ycV3VcZfUMJ|JSbSWJJJJ.*JccJ#*@V8-X.VYyVbcW6*-*qVf)VScJVGZTU.||LY_RXPT-H@NYXpEdP_N&Ec3_Vf/Z-A+P1R5(dcRX:D&c",
				"tzZ@78:k>LjUPF&DfB/dHTJ3>jC2P^)NRGbU4+%^KPSpc>RWDqVUtEZMcz:k8_DUP@(DyJO-1TB.FSC|WjE;j-+JMREyJ6qHMy_/2X3)3K#/JcJ7@_fGUPUYNj#Jc|H>yc-UkzySUPF*PyK/NjZ>;PEUdUtqZKYqCJPFTCjUq_-513DVc+c993+2.6-Bl<3UO+%PJ|DUt-P;P_yA+59Xq+L&BcM*O+X;d%Z>Jjf%UqTd_G@dCMyqTZjU/RbO>BE;;GMp^|.Yq+R*-Kcb3l+RK-*cD%4G|KpM.l+RJb:Pkq6GFBc@3c+yB|*P|5F6MB^b.cVRJ|*J4|5FBcdHER6M",
				"V.bG/J|EVALcV#d.C.|6D@;F>ct54.|&3c<7zKRUfkqVPK-*TZ|G3d^>FEVRkVc.bLyXKcjt5d.R8#cN@bNtGVYJFVy*EtV.|5FBc1j2U49RVVR7b@Rf*XCD7HREt*V*b@N3*b5Vk&%*TCKRT7bAz/:N+-*|JRL3SROZ|5FBc-c|Sb*zZ(D;9Kb:HbVYJMzcJ|+^X.4jJMV@bR*YJb)F1+BCJbc%VRC9|GJbDHERL76p<3Vc.1ccJlJ.bb..Jb)@..cc.#H:V.f8JVA/7RCDk*-*XVAjVyz.V*.&VJ||.A_R83VfG;@A8:Ok(_@NVc3_VA/J*qB(S|5O74R>#dNc",
				"V9|G/Z|EVALcV#dJcZ|6D@;b8ct54.|&3cO7zKRUfkqcc9-*TZ|G3d^>FEVRkVc.bLyXKYWt5d.R>#cN@bNtGVYJFVy*EtV.|5FBc1j2Uc9RVVR7b@Rf*XCD7*REt*V*|@N3*b5Vk&%*XCKRT7bAz/:N+-*|JRL3SREZ|5FBc-c|S|*zZ(D;9Kb:HbVYJACdK|L^X.4j.MV@bRHp.F)F1+Bz.Rc%VRC9|GJbDHERL76p<3Vc.MccJlJ.bb..Jbbb.6cc.#H:V.f>JVA/7RcDk*-GXVAjVyd.V*.&VJ||.A_R83VfG;@A>:Ok(_@NVc3_VA/K*qB<S|537pR8#dNc",
				"tzZ@78:kNLjUPF:&fB/bHTp.EDCSP^HERG-U/V%PKPD)c>_W&/+UEE|MLz:k8_&UL<(jyJq@HTB9F&C|WjJ;j-VJMREyJPqHMy*/6X.H.K#Op7pL@_fG|&UYNj#J4FH5yl-Ukzy&UPF*jyK<NjZ>;2E|dUtqZpYqZJPMTCjUO_-C.9b9PSb39c9c46-J^|9Uq+%4P7jUt|P;Dy_(+B.XO+_AJPM(q+X;d%Z>JjR%U/Td_G@dCMyqTZ&U/RbC2BE;;G<B^|9Yq1R*-Kcb9l+RS-*cj%4G|KlM.l+fJ-:Pkq6GFBc@9c+yB|*P|5F6MJ4b.cVRJ|*J4|5FBcdHER6M",
				"tzZ@78:kNLjUPF:&fB/bHTp.EDCSP^HERG-U/V%PKPD)c>_W&/+UEE|MLz:k8_&UL<(jyJq@HTB9F&C|WjJ;j-VJMREyJPqHMy*/6X.H.K#Op7pL@_fG|&UYNj#J4FH5yl-Ukzy&UPF*jyK<NjZ>;2E|dUtqZpYqZJPMTCjUO_-C.9b9PSb39c9cc6-J^|9Uq+%4P7jUt|P;Dy_(+B.XO+_AJPM(q+X;d%Z>JjR%U/Td_G@dCMyqTZ&U/RbC2BE;;G<B^|9Yq1R*-Kcb9l+RS-*cj%4G|KlM.l+fJ-:Pkq6GFBc@9c+yB|*P|5F6MJ4b.cVRJ|*J4|5FBcdHER6M",
				"b*c-/fFLbzZz97|CFXbNA24cb8J|XbDZzM7k|;7^ttq;*)l.E2GESp_|.q>>VVVVV(VVW@TU;.y6|*P6F8SFAK(*^9l).Efb.yYpXSd&;T*45*JDW.E6J8E6%LkG*d9bRR:DfbzRk2/O1GEJURJHER)5..JNW33c5/C||5FBcR-c|KpS|+L-*|JRU_-fK@lkdSTpONj7:J<|*bf>2%REJq#5WYRtJd.JD41cJ8-*|5FBc#bcU:%S||LC|*-LbDJ+Vc.bDb*+7E;)JPE.FJ/b1cJ/bkU*;^)l|@M)tH*YF.EbR1c&|b|F;dE>/+P2*A5+P>|*9*l)W*6(.EbR3cj/",
				"^zZ*8^:kV*;*8d:;K*2THM*TN)5*cTHNR)|>l&_+A>;A+>Kj;l&y^/X|8N:kzR;48q*(%*C3HMS.NDLbjDSpDFV@-%NYS>lH/%ROG1.H.BL2J8A9|KFG4;y6E;5B8dHzYcNy2N%Uy+t:;KACND5NWfNy|y#CXJ6l5Jc-MZ(yC%-XPP9X9P9XWPXPP>-J83.y@V_8J8U4#3ff<%%*&B.1O&%*B8B*O&Of*_Z7S(Y_4CM3%G3dLTRlM5DycK3ZzAzppG-S+|.6lVY*-J8b.+&RS-*8;_9G|J8-.8&%Jb:fkCGGFBcq.cV%A|*+|5F)dS8b.cVRJ|*A8|5FBc|HERG3",
				"UzZ@78:k(fj|cF:PfB/dHTp9>jCB4^)ERG-t/+%LK6j2cNfWDO1tUE|MLz:k8_&t4<(jyJq-HTB9F&C|WDJ;j-VJMREyJ6qH*y_O6X.).K#42LpL@_fGljtYNjZJ4|H5yc-tkzyjtcF*jyK/NjZ>;PE|dtUqZ@Y/ZJPFTCjt<_-Czcb.cYY3cDbp%6-2^|9|qV%cS^jtU|P;&yy*1J.XqV_A2LM(q+X;d%Z>Jj_%tOTd_G@dCMy/TZ&t/RbC2BE;;G<Bc|9Yq+R*-Kcb9l+RS-*cj%4G|KcM.l+RJ-:Pk/6GFBc@|c+yB|*P|5F6MJ4b.cVRJ|*J4|5FBcdHER6M",
				"tzZ@78:k>LjUPF&DfB/dHTJ3>jC2P^)NLGbU4+%^KPSp7>RWDqVUtEZMcz:k8_DUP@(DyJOb1TB.FSC|WjE;j-+JMRzyJ6qHMy_/2X3)3K#/JcJ7@_fGU&UYNj#Jc|H>yc-UkzySUPF*PyK/NjZ>;PEUdUtqZKYqCJPFTCjUq_-519P+c+c999+G.6-Kl<9UO+%PJ|DUt-P;P_yA+J9X/+f&BcM*O+X;d%Z>Jjf%UqTd_G@dCMyqTZjU/Rb#<BE;;G<p^|.Yq+R*-Kcb3l+RJ-*cD%4G|KpM.l+RJb:Pkq6GFBc@3c+yB|*P|5F6MB^b.cVRJ|*J4|5FBcdHER6M",
				"UzZ@78:k(fj|cF:PfB/dHTp9>jCB4^)ERG|t/+%LK6j2cNfWDO1tUE|MLz:k8_&t4<(jyJq-HTB9F&C|WDJ;j-VJMREyJ6qH*y_O6X.).K#42LpL@_fGljtYNjZJ4|H5yc-tkzyjtcF*jyK/NjZ>;PE|dtUqZ@Y/ZJPFTCjt<_-C8cb.cYY3cjbS%6-2^|9|qV%cS^jtU|P;&yy*1J.XqV_A2LM(q+X;d%Z>Jj_%tOTd_G@dCMy/TZ&t/RbC2BE;;G<Bc|9Yq+R*-Kcb9l+RS-*cj%4G|KcM.l+RJ-:Pk/6GFBc@|c+yB|*P|5F6MJ4b.cVRJ|*J4|5FBcdHER6M",
				"tzZ@78:k>LSUPF&DyB/dHTE9>jCKP^)NRGbUq+%^KGSp^>yWS/VUtEZMcz:k8_DUP@(DyJqd)TB.FSC|WjE;j-VJMREyJ6/HMy_/2X3).K#/JcJ7@_fGU&UYNj#Jc|H>yc-UkzySUPF*SyK/NjZ>;PEUdUtqZpY/CJ<FTCjUq_-O19P+cVc9*9cK95-4l-3UqV%P4|DUt-P;P_yAVJ9X/VL&2lM*/+X;d%Z>Jjf%UqTd_G@dCMyqTZjU/RbO>BE;;G<p^|.Yq+R*-Kcb3l+RK-*cD%4G|KpM.l+RJ-:Pkq6GFBc@3c+yB|*P|5F6ME^b.cVRJ|*Jl|5FBcdHER6M",
				"bHcb1fl*CzZ2D8|Cl|-<;bHc+1|GPb2ZX98FO98W||*AW6@.%bR>djPt.97VV77VVVVVqDtMW.Et)|;dK7t*7(G*AD66Ry|CREUjYdRqWNlqJAJ<H.ydJL%dp|#RM/Wb.RN23C<.l|1bkREJ|.JqyR6J.TJ<HkkcJLS||5FBcT-c:Npt;|PS*|JRO|-|UW6|T#(j-X|8dJqE|b3&Sp.>JM.5Hy/_5R.52q^cJL-*|5FBcRCcl&jd:|Yb|*-|bX5GVc.b<b#M8>D@JHERlJz+^cJLCMlFDW6_K9W6|HWp:.ySRkc5)bG4D.ENLO;dF;d|;t*FWW||HWdlR%bR|cJ1",
				"tzZ@78:k>LSUPF&DyB/dHTE9>jC2P^)NRGbUq+%^KGSp^>yWS/VUtEZMcz:k8_DUP@(DyJqd)TB.FSC|WjE;j-VJMREyJ6/HMy_/2X3).K#/JcJ7@_fGU&UYNj#Jc|H>yc-UkzySUPF*SyK/NjZ>;PEUdUtqZpY/CJ<FTCjUq_-O19P+cVc9*9cK95-4l-3UqV%P4|DUt-P;P_yAVJ9X/VL&2lM*/+X;d%Z>Jjf%UqTd_G@dCMyqTZjU/RbO>BE;;G<p^|.Yq+R*-Kcb3l+RK-*cD%4G|KpM.l+RJ-:Pkq6GFBc@3c+yB|*P|5F6ME^b.cVRJ|*Jl|5FBcdHER6M",
				"tzZ@78:k>LSUPF&DyB/dHTE9>jC2P^)NRGbUq+%^KGSp^>yWS/VUtEZMcz:k8_DUP@(DyJqd)TB.FSC|WjE;j-VJMREyJ6/HMy_/2X3).K#/JcJ7@_fGU&UYNj#Jc|H>yc-UkzySUPF*SyK/NjZ>;PEUdUtqZpY/CJPFTCjUq_-O19P+cVc9*9cK95-4l-3UqV%P4|DUt-P;P_yAVJ9X/VL&2lM*/+X;d%Z>Jjf%UqTd_G@dCMyqTZjU/RbO>BE;;G<p^|.Yq+R*-Kcb3l+RK-*cD%4G|KpM.l+RJ-:Pkq6GFBc@3c+yB|*P|5F6ME^b.cVRJ|*Jl|5FBcdHER6M",
				"b*c2/fULbzZz97|CFXbNA24cb8J|X-NZDM7k|;7^ttq9*)l.y2GETp_|.q>>VVVVV(VqW@TU;.E6|kP6F8SFAK|*^9l).Ejb.EYpXSd&;T(45*JDW.E6J8E6%LkG*d9bRR:DfbzRk2/O1GEJURJHER)5..JNW33c5/C||5FBcR-c|K%S|+L-*|JRFL-fK@lkd|TpOzj7:J<Eqbf>-%REJq#5WYRtJd.JD41cJ8-*|5FBc#bcU:%S||LC|*-LbDJ+Vc.bDb*+7E;)JPE.FJ/b1cJ/bkU*;^)l|@M)tH^YF.EbR1c&|b*z;dE>/+P2*A5+P>|*9*l)W*6(.EbR3cj/",
				"V.|H)JbERf+UV%cK49|yWN9|Xz35DK|TPcO6MJ|5#d/Uc8-*@KbGOD:X|EVRdVc.b+SqZcAP5cJRX7cVNR&P*1U.bV%*E(V.|5FBcYAV5cJb&VRVbNR%GCcWCHR4(*<*FN&3*R5VtTS*qcJR@6Rfcyp&+-*|JRl31R4.|5FBc-c|1bGzZ(8;9kRp*FVUJ>VDkR^7j.zAJYV2|RGzkFt|Yl+MJ|c:TRz.RGJFWHERK6ycV3VcZfUMJ|JJbbSbJSb.*JccJ#*@V8-X.VYyVbcW6*-*qVf)VScJVGZTU.||LY_RXPT-H@NYXpVdP_N&Ec3_Vf/Z-A+P1R5(dcRX:D&c",
				"V.|H)kbERf+UV%cK49|yWN9|Xz35DK|TPcO6MJ|5#d/Uc8-*@KbGOD:X|EVRdVc.b+SqZcAP5cJRX7cVNR&P*1U.bV%*E(V.|5FBcYAV5cJb&VRVbNR%GCcWC*R4(*<*FN&3*R5VtTS*qcJR@6Rfcyp&+-*|JRl31R4.|5FBc-c|1|GzZ(8;9kRpHFVUJ>VDkR^7j.zA9YV2|RGzkFt|>l+MJ|c:TRz.RGJFWHERK6ycV3VcZfUMJ|JJbbSbJSb.*JccJ#*@V8-X.VYyVbcW6*-*qVf)VScJVGZTU.||LY_RXPT-H@NYXpVdP_N&Ec3_Vf/Z-A+P1R5(dcRX:D&c",
				"V.|H)JbEFf+UV%cK49|yWN9|Xz35DK|TPcO6MJ|5#d/Uc8-*@KbGOD:X|EVRdVc.b+SqZcAP5cJRX7cVNR&P*1U.bV%*E(V.|5FBcYAV5cJb&VRVbNR%GCcWCHR4(*<*FN&3*R5VtTS*qcJR@6Rfcyp&+-*|JRl31R4.|5FBc-c|1bGzZ(8;9kRp*FVUJ>VDkR^7j.zAJYV2|RGzkFt|Yl+MJ|c:TRz.RGJFWHERK6ycV3VcZfUMJ|JJbbSbJSb.*JccJ#*@V8-X.VYyVbcW6*-*qVf)VScJVGZTU.||LY_RXPT-H@NYXpVdP_N&Ec3_Vf/Z-A+P1R5(dcRX:D&c",
				"-4CBK>6AXzZ297|ClALkjLHc;O%*p-zZKf7TF#9q((69^P3@EL@yW:AM<+GLUUJU+YJJ4>Mt#.E81+j1+jU+j1+*##PP.E%;7yW:AW<Y^MlHJSJDH.EWJ1E/:pWd|.#XdRWD>LD<tXKcN.EJTRJHER35RG_DYNNCJO:||5FBc<-Ct/:MF6&b*|JRF1->W#P+RU):;D%7/_pylb%8X:GyJF.5YpdP_d._DNNCJ1-*|5FBc.Xcl8:/F+pX|*-AbDJlVc.bDXtt7pqPJHpG*_2XNCJ1;Tl|f#PP+q#3PH#MTGy-<Nc_KXt+#.EM1*j8_j/ljW51#9PP^fWF.EXRNCJ1",
				"tzZ389:k9f8><d;8f*2#HTP.Ny5J^4HE7GbA/V%PJM&B+9Rq8Wl>t|C-+N:kzj&>+b*8L@/FHTp.E8Z#qy@PDdlBXONLBG/HbRjWG1.H.JCWJ+@+bf(G>D>YE_C@P4HNL^N>k9R&UP#*_R@WA&CN<:9AXUtW<JY2Z@<1TZ_PWf-CDJ6yDFJKJ_6S_M-@+|4U/V%VJc_>tXP<_jf;VB.1/Vf:K^(P/V1<-%CE@Dj%>/TbjG3dC|RWTZ&>WjbC9BE<<GXBc|.Y/lR*-Bcb.+lRJ-*+&%3G|B<b.+lfJb*<kWGGFBc3.cljB|#<|5FGbK^b.cVRJ|*@+|5FBc(HERGd",
				"tzZ@78:k>LPUPF&DyB/dHTE.>jC2P^HNRGbU/+%^4GSp^>_W6/VUtEZMcz:k8_DUP@(DyJq-)TB.FSC|WjE;j-VJMREyJ6/HMy_/2X3).K#/JcJ7@_fGU&UYNj#Jc|H>yc-UkzySUPF*SyK/NjZ>;PEUdUtqZpY/CJPFTCjUq_-519P+cWc9K9+K95-Bc-3UqV%P4|DUt|P;P_yA+J9X/VL&2cM*/+X;d%Z>Jjf%UqTd_G@dCMyqTZjU/RbO>BE;;G<p^|.Yq+R*-Kcb3l+RK-*cD%4G|KpM.l+RJ-:Pkq6GFBc@3c+yB|*P|5F6ME^b.cVRJ|*Jl|5FBcdHER6M",
				"-4CBK>6AXzZ297|ClALkjLHc;O%*p-zZKf7TF#9q((69^P3GEL@yW:AM<+GLUUJU+YJJ4>Mt#.E81+j1+jU+j1+*##PP.E%;7yW:AW<Y^MlHJSJDH.EWJ1E/:pWd|.#XdRWD>LD<tX1cN.EJTRJHER35RG_DYNNCJO:||5FBc<-Ct/:MF6&b*|JRF1->W#P+RU):;D%7/_pylb%8X:GyJF.5YpdP_d._DNNCJ1-*|5FBc.Xcl8:/F+pX|*-AbDJlVc.bDXtt7pqPJHpG*_2XNCJ1;Tl|f#PP+q#3PH#MTGy-<Nc_KXt+#.EM1*j8_j/ljW51#fPP^fWF.EXRNCJ1",
				"bSc-ft)+bzZzd8|CL<Kz38@164c)OCzZzkN%Ll^d//YyAMMDpb.p:1+:U<LL.LS.:4L._d:(d7Ej*L3:q3:Y3jq(yy//7ptBGEj^OjG_d:(H595zHUE:5#&j1+:7YUdKDR:WtCWG(C4K>Gp52DJ@pR/XGRJWH>>&5fC||5FBc.-bq:1:(#;6*|JR*+-TjdM2.::16zt^:JHEPbc:c47p6%G5HERM5D.XzH>c54-*|5FBcUKcF:1:qL<c|*-+6WXLVc.bzKq%Vpy/5@EDq5W6>c5f8Y*|ly|/FAk//_AjY.p-G>&5f-%LAGE:_|3:E3:*3jPqAd//5d:(.pbU>c5#",
				"-4CBK>6AXzZ297|ClALkjLHc;O%*p-zZKf7TF#9q((69^P3GEL@yW:AM<+GLUUJU+YJJ4>Mt#.E81+j1+jU+j1+*##PP.E%;7yW:AW<Y^MlHJSJDH.EWJ1E/:pWd|.#XdRWD>LD<tX1cN.EJTRJHER35RG_DYNNCJO:||5FBc<-Ct/:MF6&b*|JRF1->W#P+RU):;D%7/_pylb%8X:GyJF.5YpdP_d._DNNCJ1-*|5FBc.Xcl8:/F+pX|*-AbDJlVc.bDXtt7pqPJHpG*_2XNCJ1;Tl|f#PP+q#3PH#MTGy-<Nc_KXt+#.EM1*j8_j/ljW51#9PP^fWF.EXRNCJ1",
				"bS&-fT)ObzZzd8|CL<Kz38@16f6)OCzZWlN%Ly^A//YyAMMDpkDp:1+:U.LL.:S.:<;L_d:(d7E:4Y3:q3:Y3:)fyy//7ptBGE:4OjG_d:(@595zHUE:5#&:1+J7YUdKDR:WtCWG(C4K>Gp52DJ@pR/XGRJWH>>&5fC||5FBc.-&*:1:2%O6*|JRY+-T:yMq.::^pztN:J_EPbc:c17pJzG5HERMJD.XWH>c54-*|5FBcUKcF:^:PLO2|*-+6WXLVc.bWKq%Vpy/5@EDq5W6>c5#8Y*|lyM/FAkM/_AjY.p-G>&5#-%LAGE:#|3jE3:|3:PYAd//_d%(RpkU>c5#",
				"bS&-fT)ObzZzd8|CL<Kz38@16f6)OCzZWlN%Ly^A//YyAMMDpkDp:1+:U.LL.:C.:<;L_d:(d7E:4Y3:q3:Y3:)fyy//7ptBGE:4OjG_d:(@595zHUE:5#&:1+J7YUdKDR:WtCWG(C4K>Gp52DJ@pR/XGRJWH>>&5fC||5FBc.-&*:1:2%O6*|JRY+-T:yMq.::^pztN:J_EPbc:c17pJzG5HERMJD.XWH>c54-*|5FBcUKcF:^:PLO2|*-+6WXLVc.bWKq%Vpy/5@EDq5W6>c5#8Y*|lyM/FAkM/_AjY.p-G>&5#-%LAGE:#|3jE3:|3:PYAd//_d%(RpkU>c5#",
				"BHcb(fK*bzZ298|C:4bX;CHc+L3GP-<Z298POW8W|_FDA@;.ybRk&jYt.2VVV27VT2V7qDtMW.E1T|;tK7#*;dG*AD66Ry3C.Etp&1RqWdlqJAJ<H.y1JL%dp|tRM/W+.Rt23C<(l|(b>REJ|.JqyR6J))JzH>>cJLS||5FBc)-cl1jt;4YS*|JR|Y-fUW@|)1&pbX39U5qb|b3t+j/k5M/5H%.@5R.5zq>cJL-*|5FBcRCcltjd:|Pb|*-YbX5GVc.bXC#K8kD@JHERlJz+^cJLCMl*DW@_O9W66H8d:.ySK>c5TbG4D.EULO;t*;dk;d|FWW|6HWdMR%bR|cJL",
				"VKcH/ZbEbMLcVVc.C%R6D@;lUdtOC9|&3)4Ac.bOfAq)dX-*PZbG(d-UFEVRkVc.b+y:.c6tOd9RUScN@|N<HVc.FVyGV3V.|5FBc7j2Oc.RV8RkbWlfGPcD1CREtGt^lWNV^FO(k&-GPcK|T1|MzqpNL-*|JRB3#RE%|5FBc-Y|#>9zZ(D:9KFpHl)dJMCYX>B%TZ)j%7^WbRGYXbk>ML+c.bC2&R49|G5RDHER+16cT<E(.7cc.l.J.J....J...czXSH:VZfUXVMq1RdDO*-HPV7jVyY%fGK&z.|/9M_>UtV2GT@MUpEk3VWNEk(_VM6.*qLt#|O31YRU24Nd",
				"-tCBK>6AXzZ297|ClALkjbHc;O%*p-zZKf7TF#9q((69#P3GEb@yW:AM<+GLGUJ++YJJ4#Mt#.E81+j1+jU+j1+*##PP.E%;7yW:AW<Y^MlHJSJDH.EWJ1E/:pWd|.#XdRWD>LD<tX1cN.EJTRJHER35RG_DYNNCJO:||5FBc<-Ct/:MF6&b*|JRF1->W#P+RU):;D%7/_pylb%8X:Gy5@.5YEdP_d._DNNCJ1-*|5FBc.Xcl8:/F+pX|*-AbDJlVc.bDXtt7pqPJHpG*_2XNCJ1;Tl|f#PP+q#3PH#MTGy-<Nc_KXt+#.EM1*j8_j/ljW51#9PP^fWF.EXRNCJ1",
				"V.|G/Z|EVALcV#dJcZ|6D@;b8ct54.|&3cO7zZRUfkqcc.-*TZ|G3d^>FEVRkVc.bLyXKYjt5d.R>#cN@bNtGVYJFVy*EtV.|5FBc1j2Uc9RVVR7b@Rf*XCD7*REt*V*|@N3*b5Vk&%*XCKRT7bAz/:N+-*|JRL3SREZ|5FBc-c|S|*zZ(D;9Kb:HlVYJACdK|L^X.4j.MV@bRHp.F)P1+Bz.Rc%VRC9|GJbDHERL76p<3Vc.MccJl_.bb..Jbbb.6cc.#H:V.f>JVA/7RcDk*-GXVAjVy2.V*.&VJ||.A_R83VfG;@A>:Ok(_@NVc3_VA/J*qBOS|537pR8#dNc",
				"bHcb1f:*bzZ2D8|CO|-X;bHc+L|GPb2Zz98K|D8W||FAW6_.%bRkdjPt.VVVV7VVU1VVqDtMW.Et)l7dK7t*7(GMAD66Ry|CREUjYdRqWN|qJAJ<H/ydJL%dp|#RM/Wb.RN23C<.l|1b>REJ|.JqyR6J.TJzH>>cJLS||5FBcT-clNpt;|P+*|JRO|-|UW@|R#(jCX|8dJqE|b3&SpTk5*R5Hy/@JR.52q>cJL-*|5FBcRCcl&jd:|Yb|*-|bX5GVc.bXb#M8kD@JHERlJz+^cJLCMlFDW6_K9W6|HWp:.ySR>c5)bG4D.ENLO;d|;d|;t*FWW||HWdlR%b&>cJ1",
				"tzZ@78:k>LSUPF&DyB/dHTE9>jC2P^)NRGbUqV%^KGSp^>yWS/VUtEZMcz:k8_DUP@(DyJqd)TB.FSC|WjE;j-VJMREyJ6/HMy_/2X3).K#/JcJ7@_fGU&UYNj#Jc|H>yc-UkzySUPF*SyK/NjZ>;PEUdUtqZpY/CJPFTCjUq_-O19P+cVc9*9cK95-4l-3UqV%P4|DUt-P;P_yAVJ9X/VL&2lM*/+X;d%Z>Jjf%UqTd_G@dCMyqTZjU/RbO>BE;;G<p^|.Yq+R*-Kcb3l+RK-*cD%4G|KpM.l+RJ-:Pkq6GFBc@3c+yB|*P|5F6ME^b.cVRJ|*Jl|5FBcdHER6M",
				"tzZ@78:k>LSUPF&DyB/dHTE9>jC2P^)NRGbUqV%^KGSp^>yWS/VUtEZMcz:k8_DUP@(DyJqd)TB.FSC|WjE;j-VJMREyJ6/HMy_/2X3).K#/JcJ7@_fGU&UYNj#Jc|H>yc-UkzySUPF*SyK/NjZ>;PEUdUtqZpY/CJPFTCjUq_-O19P+cVc9*9cK95-4l-3UqV%P4|DUt-P;P_yAVJ9X/VL&2lM*/+X;d%P>Jjf%UqTd_G@dCMyqTZjU/RbO>BE;;G<p^|.Yq+R*-Kcb3l+RK-*cD%4G|KpM.l+RJ-:Pkq6GFBc@3c+yB|*P|5F6ME^b.cVRJ|*Jl|5FBcdHER6M",
				"tzZ@78:k>LSUPF&DyB/dHTE9>jC2P^)NRGbUqV%^EGSp^>yWS/VUtEZMcz:k8_DUP@(DyJqd)TB.FSC|WjE;j-VJMREyJ6/HMy_/2X3).K#/JcJ7@_fGU&UYNj#Jc|H>yc-UkzySUPF*SyK/NjZ>;PEUdUtqZpY/CJPFTCjUq_-O19P+cVc9*9cK95-4l-3UqV%P4|DUt-P;P_yAVJ9X/VL&2lM*/+X;d%Z>Jjf%UqTd_G@dCMyqTZjU/RbO>BE;;G<p^|.Yq+R*-Kcb3l+RK-*cD%4G|KpM.l+RJ-:Pkq6GFBc@3c+yB|*P|5F6ME^b.cVRJ|*Jl|5FBcdHER6M",
				"V.|G/Z|EVALc3#dJcZ|6D@;b8ct54.|&3cO7zZRUfkqcc9-*TZ|G3d^>FEVRkVc.bLyXKYjt5d.R>#cN@bNtGVYJFVy*EtV.|5FBc1j2Uc9RVVR7b@Rf*XCD7*REtHV*|@N3*b5Vk&%*XCKRT7bAz/:N+-*|JRL3SREZ|5FBc-c|S|*zZ(D;9Kb:HbVYJACdK|L^X.4j.MV@bRHp.F)F1+BzKRc%&RC9|GJbDHERL76p<3Vc.MccJlJ.bb..Jbbb.Jcc.#H:V.f>JVA/7RcDk*-*XVAjVy2.V*.&cJ||.A_R83VfG;@A>:Ok(_@NVc3_VA/J*qB<S|5t7pR8#dNc",
				"VZ|H)JbEbf+cV%cK49|yWN9|XD35DK|TPDO6MJ|5#d/UcL-*@KbGOD:X|EVRdVc.b+SqZcAP5cJRX7cVNR&P*1U.bV%*E(V.|5FBcYAV5cJb&VRVbNR%GCcWC*R4(*<*FN&33R5VtTS*qcJR@6Rfcyp&+-*|JRl31R4.|5FBc-c|1|GzZ(8;9kRpGRVcJ>VDkF^7j.zA8YV2|RGzkFt|>l+MJ|c:TRz.RGJFWHERK6ycV3VcZfUMJ|JJbSWJJJJ.*JccJ#*@V8-X.VYyVbcW6*-*qVf)VScJVGZTU.||LY_RXPT-H@NYXpEdP_N&Ec3_Vf/Z-A+P1R5(dcRX:D&c",
				"UzZ@78:kNLj|cF:PfBOdHTp9>jCB4^)ERG-t/1%LK6j2^N_WDO+tUE|MLz:k8_Dt4<(jyJq-HTB9F&C|WDJ;j-VJMRE_J6qH*y_O6X.).K#42LpL@_fGl&tYNjZJ4|H5yc-tkzyjtcF*jyK/NjZ>;PE|dtUqZ@Y/ZJPFTCjt<_-Czcb.cYY3cjbSc6-2^|9|qV%c2PjtU|;;&Ry*1J.Xq+_A2LM(q+X;d%Z>Jj_%tOTd_G@dCMy/TZ&t/RbC2BE;;G<Bc|9Yq+R*-Kcb9l+RS-*cj%4G|KcM.l+RJ-:Pk/6GFBc@|c+yB|*P|5F6MJ4b.cVRJ|*J4|5FBcdHER6M",
				"bS&-fT;ObzZzd8|CL<Kz3-@16f6)OCzZWlN%Ly^A/MYAAMMDpkDp:1+:U.Lq.:S.:<;L_d:(d7E:4Y3:L3:Y3:)fyy//7ptBGE:4OjG_d:(S595zHUE:5#&:1+:7YUdKDR:WtCWG(C4K>Gp52DJ@pR/XGRJWH>>&5fC||5FBc.-&*:1:2%<6*|JRY+-T:yMq.::4pztN:J_EPbc:c17pJzG5HERMJD.XWH>c54-*|5FBcUKcF:^:qlO2|*-+6WXLVc.bWKq%Vpy/5@EDq5W6>c5#8Y*|ly|/FAk//_AjY.p-G>&5#-%LAGE:#|3jE3:|3:PYAd//_d%(RpkU>c5#",
				"tzZ@78:k>LSUPF&DyB/dHTE9>jC2P^)NRGbUqV%^KGSp^>yWS/VUtEZMcz:k8_DUP@(DyJqd)TB.FSC|WjE;j-VJMREyJ6/HMy_/2X3).K#/JcJ7@_fGU&UYNj#Jc|H>yc-UkzySUPF*SyK/NjZ>;PEUdUtqZpY/CJPFTCjUq_-O19P+cVc9*9cK95-4l-3UqV%P4|DUt-P;P_yAVJ9X/VR&2lM*/+X;d%Z>Jjf%UqTd_G@dCMyqTZjU/RbO>BE;;G<p^|.Yq+R*-Kcb3l+RK-*cD%4G|KpM.l+RJ-:Pkq6GFBc@3c+yB|*P|5F6ME^b.cVRJ|*Jl|5FBcdHER6M",
				"UzZ@78:kNLj|cF:PfBOdHTp9>jCB4^)ERG-t/1%LK6j2^N_WDO+tUE|MLz:k8_Dt4<(jyJq-HTB9F&C|WDJ;j-VJMRE_J6qH*y_O6X.).K#42LpL@_fGl&tYNjZJ4|H5yc-tkzyjtcF*jyK/NjZ>;PE|dtUqZ@Y/ZJPFTCjt<_-Czcb.cYY3cjbSc6-2^|9|qV%c2PjtU|;;&yy*1J.Xq+_A2LM(q+X;d%Z>Jj_%tOTd_G@dCMy/TZ&t/RbC2BE;;G<Bc|9Yq+R*-Kcb9l+RS-*cj%4G|KcM.l+RJ-:Pk/6GFBc@|c+yB|*P|5F6MJ4b.cVRJ|*J4|5FBcdHER6M",
				"+Hc41fl*-zZ2D8|CF|bX;bHc+L||Pb2Zz98FOD8W6@FAW__.%bRkdjPt.GG5:7VVVVVVHDtMW.Et)|;dK7t*;(GGAD66Ry|CREUjYdRqWNlHJAJ<H.ydJL%dp|#RM/Wb.RN23C<.l|1b>REJ|.JHyR62.TJzH>|cJLS||5FBcT-c4Npt;|PC*|JRO|-|UA@|T#(jCX|8dJHE|b3&SjTk5*R5qy/_5R.52q^cJL-*|5FBcRCcl&jd:|Yb|*-|bX5GVc.bXb#M8kD6JHERlJz+^cJLCMlFDW66K9W_6HWp:.ySR>c5)bG4D.ENLO;d|;d|;t*FWW|6HWdlR%bt|cJ1",
				"bHcb1f:*bzZ2D8|CO|-X;bHc+L|GPb2Zz98KOD8W||FAW6_.%bRkdjPt.VVV77VV71VVqDtMW.Et)l;dK7t*7(GMAD66Ry|CREUjYdRqWN|qJAJ<H/ydJL%dp|#RM/Wb.RN23C<.l|1b>REJ|.JqyR6J.TJzH>>cJLS||5FBcT-clNpt;|P+*|JRO|-|UW@|R#(jCX|8dJqE|b3&SpTk5*R5Hy/@5R.52q^cJL-*|5FBcRCcl&jd:|Yb|*-|bX5GVc.bXb#M8kD@JHERlJz+>cJLCMlFDW6_K9W6|HWp:.ySR>c5)bG4D.ENLO;d|;d|;t*FWW||HWdlR%bR|cJ1",
				"V.|H)JbERf+cV%cK49|yWN9|XD35DK|TPcO6Mk|5#d/Uc8-*@KbGOD:X|EVRdVc.b+SqZcAP5cJRX7cVNR&P*1U.bV%*E(V.|5FBcYAV5cJb&VRVbNR%GCcWC*R4(*<*FN&3*R5VtTS*qcJR@6Rfcyp&+-*|JRl31R4.|5FBc-c|1|GzZ(8;9kRp*RVcJ>VDkR^7j.zAJYV2|RGMkFt|>l+M.|c:TRz.RGJFWHERK6ycV3VcZfUMJ|JJbSJbJJJ..JccJ#*@V8-X.VYyVbcW6*-*qVf)VScJVGZTU.||LY_RXPT-H@NYXpVdP_N&Ec3_Vf/Z-A+P1R5(dcRX:D&c",
				"VKcH3JbEbMLcVVc.c%R6DW;lUYtOY9|&3)4kc.bOfAq)CJ-*PZbG(d-UFEVRkVc.b+y:.c6t5d9RUScN@|N<HVc.FVyGV3V.|5FBc7j25c.RV8RkbWRfGPcD1CREtGt^lWNV^FO(1&-GPcK|T1|MzqpNL-*|JRB3#RE%|5FBc-Y|#b*zZ(D:9KFpGl)dJ7C)X>B%PZCj%7VWbRGYXbk>ML+c.bC2&R49|G.RDHER+16cT<E(.7cc.l.J.J....J...czXSH:VZfUXVMq1RcDO*-HPV7jVyY%fGK&z.|/9M_>UtV2GPWMUpEA3VWNVk(_VM6.*qLt#|O3kYRU24Nd",
				"bSc-ft)+bzZzd8|CL<Kz38@164c)OCzZzkN%Ll^d//YyAMMDpb.p:1+:U<LL.LS.:4L._d:(d7E:*L3jq3:Y3jq(yy//7ptBGE:^OjG_d:(H595zHUE:5#&:1+:7YUdKDR:WtCWG(C4K>Gp52DJ@pR/XGRJWH>>&5fC||5FBc.-bq:1:(#;6*|JR*+-Tjd/2.::16zt^:JHEPbc:c47p6%G5HERM5D.XzH>c54-*|5FBcUKcFj1:qL<c|*-+6WXLVc.bzKq%Vpy/5@EDq5W6>c5f8Y*|ly|/FAk//_AjY.p-G>&5f-%LAGEj_|3jE3:*3jPqAd//5d:(.pbU>c5#",
				"+Hc41fl*-zZ2D8|CF|bX;bHc+L||Pb2Zz98FOD8W6@FAW__.%bRkdjPt.GG5:7VVVVVVHDtMW.Et)|;dK7t*;(GGAD66Ry|CREUjYdRqWNlHJAJ<H.ydJL%dp|#RM/Wb.RN23C<.l|1b>REJ|.JHyR62.TJzH>|cJLS||5FBcT-c4Npt;|PC*|JRO|-|UA@|T#(jbX|8dJHE|b3&SjTk5*R5qy/_5R.52q^cJL-*|5FBcRCcl&jd:|Yb|*-|bX5GVc.bXb#M8kD6JHERlJz+^cJLCMlFDW66K9W_6HWp:.ySR>c5)bG4D.ENLO;d|;d|;t*FWW|6HWdlR%bt|cJ1",
				"W.VG/J|EVALCV#dJcZ|6D@;|_ct54.|&3cO7z.RUfkqcz.-*TZ|G3d^>FEVRkVc.bLyXKcjt5d.R>#cN@bNtG<YJFVy*EtV.|5FBc1j2Uc9RVVR7b@Rf*XCD7*REt*O*b@N3*b53k&%*XCKRT7bAz/:N+-*|JRL3SREZ|5FBc-c|S|*zZOD;9Kb:GbVpJAccJ|+^X.CjZMV@bRHYJb)F1+BcJFc%VRC9|GJbDHERL76p<3Vc.MccJlJ._b..JJb@4.cc.#H:V.f>JVA/7RcDk*-*XVAjVyz.V*.&VJ||.A8R_3VfG;@A>:Ok(8@NVc38VA/J*qBOS|5O7cR>#dNc",
				"bS&-fT)ObzZzd8|CL<Kz38@16f6)OCzZWlN%Ly^A//YyAMMDpkDp:1+jU.LL.:S.:<;L_d:(d7E:4Y3:q3:Y3:)fyy//7ptBGE:4OjG_d:(@595zHUE:5#&j1+J7YUdKDR:WtCWG(C4K>Gp52DJ@pR/XGRJWH>>&5fC||5FBc.-&*:1:2%O6*|JRY+-T:yMq.::^pztN:J_EPbc:c17pJzG5HERMJD.XWH>c54-*|5FBcUKcF:^:6LO2|*-+6WXLVc.bWKq%Vpy/5@EDq5W6>c5#8Y*|lyM/FAkM/_AjY.p-G>&5#-%LAGE:#|3jE3:|3:PYAd//_d:(RpkU>c5#",
				"V.|H)JbERf+cV%cK49|yWN9|XD35DK|TPcO6Mk|5#d/Uc8-*@KbGOD:X|EVRdVc.b+SqZcAP5cJRX7cVNR&P*1U.bV%*E(V.|5FBcYAV5cJb&VRVbNR%GCcWC*R4(*<*FN&3*R5VtTS*qcJR@6Rfcyp&+-*|JRl31R4.|5FBc-c|1|GzZ(8;9kRp*RVcJ>VDkR^7j.zAJYV2|RGMkFt|>l+MJ|c:TRz.RGJFWHERK6ycV3VcZfUMJ|JJbSJbJJJ..JccJ#*@V8-X.VYyVbcW6*-*qVf)VScJVGZTU.||LY_RXPT-H@NYXpVdP_N&Ec3_Vf/Z-A+P1R5(dcRX:D&c",
				"bHcb1fl*bzZ2D8|CF|-<;-qc+L|GPb2Zz98FOW89||*AW6@.%bR>djPt.VVVJ7VVVVVVqD&MW.Et)|;dK7t*7(G*DD66Ry|CREUjYdRqWNlqJAJ<H.ydJL%&p|#RM/Wb.Rt23C<.l|1bkREJ|.JqyR6J.TJzHkkcJLS||5FBcT-cMNpt;l|+*|JRO:-3(D@lT#(jCX|8&JqE|b3&Sp.>JM.5Hy/_5R.52q^cJL-*|5FBcRCcl(jdK|Yb|*-|bX5GVc.bXb#M8>D@JHERlJz+^cJLCMlFDW6_K9W6|HWp:.ySRkc5)bG4D.ENLO;U*7U|;t*FWW||HWUlR%bR|cJ1",
				"W.VG/J|EVALCV#dJcZ|6D@;|_ct54.|&3cO7z.RUfkqcz.-*TZ|G3d^>FEVRkVc.bLyXKcjt5d.R>#cN@bNtG<YJFVy*EtV.|5FBc1j2Uc9RVVR7b@Rf^XCD7*REt*O*b@N3*b53k&%*XCKRT7bAz/:N+-*|JRL3SREZ|5FBc-c|S|*zZOD;9Kb:GbVpJAccJ|+^X.CjZMV@bRHYJb)F1+BcJFc%VRC9|GJbDHERL76p<3Vc.MccJlJ.bb..JJb@..cc.#H:V.f>JVA/7RcDk*-*XVAjVyz.V*.&VJ||.A8R_3VfG;@A>:Ok(8@NVc38VA/J*qBOS|5O7cR>#dNc",
				"W.VG/J|EVALCV#dJcZ|6D@;|_ct54.|&3cO7z.RUfkqcz.-*TZ|G3d^>FEVRkVc.bLyXKcjt5d.R>#cN@bNtG<YJFVy*EtV.|5FBc1j2Uc9RVVR7b@Rf*XCD7*REt*O*b@N3*b53k&%*XCKRT7bAz/:N+-*|JRL3SREZ|5FBc-c|S|*zZOD;9Kb:GbVpJAccJ|+^X.CjZMV@bRHYJb)F1+BcJFc%VRC9|GJbDHERL76p<3Vc.MccJlJ.bb..JJb@4.cc.#H:V.f>JVA/7RcDk*-*XVAjVyz.V*.&VJ||.A8R_3VfG;@A>:Ok(8@NVc38VA/J*qBOS|5O7cR>#dNc",
				"V.|H)JbERf+cV%cK49|yWN9|XD35DK|TPcO6MJ|5#d/Uc8-*@KbGOD:X|EVRdVc.b+SqZcAP5cJRXlcVNR&P*1U.bV%*E(V.|5FBcYAV5cJb&VRVbNR%GCcWC*R4(*<*FN&3*R5VtTS*qcJR@6Rfcyp&+-*|JR731R4.|5FBc-U|1FGzZ(8;9kRp*RVcJ>zDkR^lj9VAJYV2|RGMkFt|>7+MJ|c:TRz.RGJFWHERK6ycV3VcZfUMJ|JJbb.bJSb.*JccJ#*@V8-X.VYyVbcW6*-*qVf)V:cJVGZTU.||LY_RXPT-H@NYXpVdP_N&Ec3_Vf/Z-A+P1R5(dcRX:D&c",
				"48<lDy8<FO&:^7&yU^UF8f7kSFUq#GFk#SS#&+7P#AMU#_H337282qC#O&CbN#*UD&&SqdyUlOO&XbdyU+ZO&+ULZ+ZPWO.WOcNK7.KHJ7PcOCUc_HSS7.8.b+cAV+UWVc.bVWA&H>ALX*(HWSWyq*CT(-&c-&|5FBcyYRlH+-pcYp8|5FBcyJJCXDGCHERFTGlDEKf|@;;EJD:fdSy6f;*b*|JRE@#K((7@bSST<bjTR*7G#Jl)B)j())E(/E/3jBC7@q3BlL*-RR7G#KC>;G(D7K>|GAHtK>|GC-tR2UHGUH&dtC-*CU6&1zc_d+T+G7zcpT*AAZp1zZ_d9;CG",
				"V9RH2K|4LYBcVSMZV9|AWNV|Xc35cJ|TPc(cc9F5#q/Uc9-*@.LGOD:XbEVRCVc.b+SdZcAP5cJ|X7c&NR&PGVUK|V%H4>V.|5FBcYA75c9L&VRVbNR-*^cWqHR4(*PHFN&3*R5VtTSGdcJR@6Rfcyp&+-*|JRl3>RE.|5FBc-V|>b*zZ(8;9k|p*|VDJfcc.bC7j.zAkYVN|RGM.Rt|1lBDkLc%TRzKRGJFWHERVqycV3Vc.fUM.|JLb.HyJ+/RUZccJ#*@VJ-X9Vf)VLcWq*-*dVf)VSDJV*.TU.||KY_RXPT-H@NYXpVqP_N&4t3_Of/Z-ABP>R5(VcRX:U&c",
				"VZ|H)JbERf+cV%cK49|yWN9|XD35DK|TPcO6MJ|5#d/Uc8-*@KbGOD:X|EVRdVc.b+SqZcAP5cJRX7cVNR&P*1U.bV%*E(V.|5FBcYAV5cJb&VRVbNR%GCcWC*R4(*<*FN&33R5VtTS*qcJR@6Rfcyp&+-*|JRlO1R4L|5FBc-c|1|GzZ(8;9kRpGRVcJ>VDkF^7j.zA8YV2|RGzkFt|>l+MJ|c:TRz.RGJFWHERK6ycV3VcZfUMJ|JJbSJJJJJ.*JccJ#*@V8-X.VYyVbcW6*-*qVf)VScJVGZTU.||LY_RXPT-H@NYXpEdP_N&Ec3_Vf/Z-A+P1R5(dcRX:D&c",
				"bHcb1fl*bzZ2D8|CF|-<;-qc+L|GPb2ZX98FOW89||*AW6@T%bR>djPt.VVVJ7VVVVVVqD&MW.Et)|;dK7t*7(G*AD6_Ry|CREUjYdRqWNlqJAJ<H.ydJL%&p|#RM/Wb.Rt23C<.l|1bkREJ|.JqyR6J.TJzHkkcJLS||5FBcT-cMNpt;l|+*|JRO:-3(D@lT#(jCX|8&JqE|b3&Sp.>JM.5Hy/_5R.52q^cJL-*|5FBcRCcl(jdK|Yb|*-|bX5GVc.bXb#M8>D@JHERlJz+^cJLCMlFDW6_K9W6|HWp:.ySRkc5)bG4D.ENLO;U*7U|;t*FWW||HWUlR%bR|cJ1",
				"bHcb1fl*bzZ2D8|CF|-<;-qc+L|GPb2Zz98FOW89||*AW6@T%bR>djPt.VVVJ7VVVVVVqD&MW.Et)|;dK7t*7(G*AD6_Ry|CREUjYdRqWNlqJAJ<H.ydJL%&p|#RM/Wb.Rt23C<.l|1bkREJ|.JqyR6J.TJzHkkcJLS||5FBcT-cMNpt;l|+*|JRO:-3(D@lT#(jCX|8&JqE|b3&Sp.>JM.5Hy/_5R.52q^cJL-*|5FBcRCcl(jdK|Yb|*-|bX5GVc.bXb#M8>D@JHERlJz+^cJLCMlFDW6_K9W6|HWp:.ySRkc5)bG4D.ENLO;U*7U|;t*FWW||HWUlR%bR|cJ1",
				"bHc41fl*-zZ2D8|CF|bX7bHc+L||Pb2Zz98KOD8W6@:AW__.%bRkdjPt.GG5:7VVVVVVHDtMW.Et)|;dK7t*7(lGAD66Ry3CREUjYdRqWNGHJAJ<H.ydJL%dp|#RM/Wb.RN23C<.l|1b>REJ|.JHyR62.TJzH>>cJLS||5FBcT-c|Np9;|P+*|JRO|-|UW@|T#(jCX|8dJHE|b3&SjTk5*/JHy/_5R.52q^cJL-*|5FBcRCcM&jd:|Yb|*-|bX5GVc.bXb#M8kD6JHERlJz+^cJLCMlFDW66K9W_6HWp*.ySR>c5)bG4D.ENLO;d|;d|;t*FWW|6HWdlR%bt|cJ1",
				"XpcL1%q&XzZ299|Cl^LkjbHcb1A61-DZKf9+q#7#(>Tf#3PRFbGyW:>M<6T+W++;++U6Y#Mq#<E81+jM+jUlj1++#tPP.y%;._M:>1<Yt1l&JSJDH@y85Oy/:1WdT.#LdRWDA;D<6X1XN.yJ|.JHER3JRG_DYONcJ1b||5FBc<-C|/:)F*&L*|JR|^-%Wt3F@U):;zA7M_py+bA8b:GE*2.5YEd3_d._DHNcJO-*|5FBc.Xcl8:/F+>X|*->bzJ+Vc.bDXqq7E#P_HyGFJzXNCJOb2q|ffkP+t#(1Y#/|Gy;KOc_1Xq+#GEM1+j8+jU+j/+c##PP^fWF.yXGNCJ1",
				"-4C;KH6AXzZ297|ClALkjbHcXO%6A-zZKf7T*#9q(PT9#P3@Eb@yW:AM<+MLUJJU++JJ4#Ut#.E81+j1+jU+j1+*##PP.E%;7yU:AW<Y#MlHJSJDH.EWJ1E/:pWd|.#XdRWD>LD<tX1cN.EJTRJHER35RG_DYNNcJK;||5FBc<-Ct/:Ml6&b*|JRF1->W#P+RUM:;D%7/JpE6b%8X:Gy*|.5YEdP_d._DNNCJ1-*|5FBc.Xcl8:/F+pX|*-AbDJlVc.bDXtt7pqPJHpG*_2XOCJ1;Tl|f#PP+q#3PY#MTGy-<Oc_KXt+#.EM1*j1Tj/ljW51#9PP^fWF.EXRNCJ1",
				"z8Fy-y:zc:&LAk&yAL:Z8.E6B%U:pG:9>3B#VCEPF:K/#<-FSE484)CZ:&(bNZ2UD&&Fb6yU:c:&@bPyUKp:&(AjZKZPp:.p:cN2k42|RkPc:CUc_|3Sk.8.XLZ:V(/pVc.bLp:&|W:X%R8TpBpyX*|-L-&pj&|5FBcyYRK-K-&F)q;|5FBcybJLf|MLHERd^GlTk2f|M;TER^7@tSycd^*Y*|JREMp7((kGbSST(bRTR*kG#RLJc+1cccJH%C%+cclkG)33Cj*-2ROG>7(WDG(DOJW|GKDE7W|GK|E2.U-GUTV9kK-*KU>&jz><9KTKG4zp&T2::p&bzZ_9^|KM",
				"V.|H)JFER>^cV%c8VJ|yWN9|Xc35UK|TPcO6MJ|5#d/Uc8-*@KbG(D:X|EVRdVc.b^SqZcyP5cJRX7c&NR&P*1U.bV%*4(V.|5FBcYAV5cJb&VRVbNR%*CcWC*R4(*<HbN&3/L5VtTS*qcKR@6Rfcyp&+-*|JRl31R4.|5FBc-c|1|*zZ(8;9kFp*RVcJ>VDkR^7j.zA9YV2RRGMKFt|fl+MJ|D:TRz.RGJFWHERO6ycV3Vc.fUMJ|JJbt.bJJb.*JccJ#*@V8-X.VYyVbcW6*-*qVf)VScJA*ZTU.||.Y_RXPT-H@NYXpVdP_N&Ec3_Vf/Z-A+P1R5(dcRX:D&c",
				"W.VG/J|EVALCV#dJcZ|6D@;|_ct54.|&3cO7z.RUfkqcz.-*TZ|G3d^>FEVRkVc.bLyXKcjt5d.R>#cN@bNtG<YJFVy*EtV.|5FBc1j2Uc9RVVR7b@Rf*XCD7*REt*O*b@N3*b53k&%*XCKRT7bAz/:N+-*|JRL3SREZ|5FBc-c|S|*zZOD;9Kb:GbVpJAccJ|+^X.CjZMV@bRHYJb)F1+BcJFc%VRC9|GJbDHERL76p<3Vc.MccJlJ.bb..JJb@..cc.#H:V.f>JVA/7RcDk*-*XVAjVyz.V*.&VJ||.A8R_3VfG;@A>:Ok(8@NVc38VA/J*qBOS|5O7cR>#dNc",
				"V9RH2K|4LfBcVSMZV9|AWNV|Xc35cJ|TPc(cc9F5#q/Uc9-*@.LGOD:XbEVRCVc.b+SdZcAP5cJ|X7c&NR&PGVUK|V%H4>V.|5FBcYA75c9L&VRVbNR-*^cWqHR4(*PHFN&3*R5VtTSGdcJR@6Rfcyp&+-*|JRl3>RE.|5FBc-V|>b*zZ(8;9k|p*|VDJfcc.bC7j.zAZYVN|RGM.Rt|1lBDkLc%TRzKRGJFWHERVqycV3Vc.fUM.|JLb.HyJ+URUZccJ#*@VJ-X9Vf)VbcWq*-*dVf)VSDJV*.TU.||KY_RXPT-H@NYXpVqP_N&4t3_Of/Z-ABP>R5(VcRX:U&c",
				"48<4Uy8<#FV:^7&y^^>#8.7U3F>bcGFU#S3WV+7P#FM>F_-337F82qC#O&CbN#*>D&&Sqdy>lOO&Xbdy>+ZO&+>Lc+ZPWO.WOcNK7.KHJ7PcOC>c_HSS7.8.b+cAV+>WVc.blWO&Hk>qX*(HWSWyq*CTA-&c-&|5FBcyYR(H+-pcY&8|5FBcyJJCXDGCHERATGCDEKf|@|-7JD5fdSy6f;*L*|JRt@#K(<7@bSST(YjTR*EG#JlBBBjf)*E(E((3EBC7@q3S/L*-RR7G5KCk;G(D7Kk|GAHtKk|GC-t5%>H)>H&dEC-*C>6&1zc_d+T+@7zcpT*AAZp1zZ_d9;CG",
				"dYc;1%_p-zZ297|C6^LkjbHcbO>6&CDZKf9+qf7f(k*t#(PREC@E8:pX<UU+W++U++UdY#Xq#.EX1+jX+jU+j1++##PP.y%;RyX:A8pHtXlYJSJDH.E85KyX:pXMF.#LMR8D%BD<2d1dN.yJ|RJHER3JRG_DYONcJ1b||5FBc/-cqX:)TF&L*|JR+b-%Wt(q<1):;k%9UJHy+b>UL:GEO+.5YEM35M._DHNcJ1-*|5FBc.;cl8:XF+^d|*-AbDJ+Vc.bDdqq7EtPJYEG+JzdOCJOb2F|f#PP+t#(1Y#XTGylONc_1dq+#.FX1+j1TjU+jX++t#PPHfX7.Ed<OcJK",
				"49FF@yl)FF&(^t&y^^UZ8.Ek3ffq#GfdFSSW&CtPWfff#/D337282qC#O&CbN#*UH&&SbdyfCOO&XbkyU+ZO&+f1Z+ZPWO.WOZNM7.JHJ7PcOCUc/HSSE.8.b+cfV+fcVc.bV1OVH>U1X*(HWSWyq*CT(-&c-&|5FBcyYR+H+-pjYp9|5FBcy5JCFH%CHERXT%CTtMA|%A;tJ;JAkBy6A;*b*|JR7@#R(<7@bSST<b*TM*EG#R(llKlKLL:::8K::2Ct%qSS<Y*-RR7G#RC>;G)D7M>|GC|tJ>|GC|7R.UHGUHpdtC-*CUj&Y4c_k+T+G74jpT*fUZp1zZ_d9;C@",
				"bSc-ft)+bzZzd8|CL<Kz38@164c)OCWZzAN2Ll^d//YyAMMDpb.p:1+jU<LL.LS.:4L._d:(d7E:*Y3:q3:Y3:q(yy//7ptBGE:^OjG_d:(H595zHUE:5#&:1+:7YUdKDR:WtCWG(C4K>Gp52DJ@pR/XGRJWH>>&5fC||5FBc.-bq:1:(#;6*|JR)+-Tjd/q.::16zt^:JHEPbc:c47p6%G5HERM5D.XzH>c54-*|5FBcUKcF:1:qLOc|*-+6WXLVc.bzKq%Vpy/5@EDq5W6>c5f8Y*|ly|/FAk//_AjY.p-G>&5f-%LAGE:_|3jE3:|3:PqAd//5d:(.pbU>c54",
				"bSc-ft)+bzZzd8|CL<Kz38@164c)OCWZzAN%Ll^d//YyAMMDpb.p:1+jU<LL.LS.:4L._d:(d7E:*Y3:q3:Y3:q(yy//7ptBGE:^OjG_d:(H595zHUE:5#&:1+:7YUdKDR:WtCWG(C4K>Gp52DJ@pR/XGRJWH>>&5fC||5FBc.-bq:1:(#;6*|JR)+-Tjd/q.::16zt^:JHEPbc:c47p6%G5HERM5D.XzH>c54-*|5FBcUKcF:1:qLOc|*-+8WXLVc.bzKq%Vpy/5@EDq5W6>c5f8Y*|ly|/FAk//_AjY.p-G>&5f-%LAGE:_|3jE3:|3:PqAd//5d:(.pbU>c54",
				"V.|H)J|EFf5cVSz9VJbyWNVbXc35cZ|TPcO6ck|5#q/UcK-*@kFGOD:X|EVRqVc.b+SdZcAP5DJ|X7c&NF&P*VU.bV%G41V.|5FBcYAk5cJb&VRVbNR%*@cWC*R4(*<*bN&3*R5VtTS*dckR@6Rfcyp&l-*|JRl31R4.|5FBc-(|1|*zZ38;9k|p*bVMJfcDJb^7j.zAJY42|RGVK|t|>^^cJ|V%TRzJRGkFWHERO6ycV3VU.fUM.|JJb.AbA*b-*bccJ#*@VJ-X.VYyVbc86*-*dVf)VScJV*.TU9||.Y_RXPT-H@NYXp4qP_N&Ec3_Vf/Z-A+P1R5(qzRX:D&z",
				"bHcb1fK*bzZ2D8|CF|b<;bqc+L|GPb2Zz98FOA89||*AW6@.%bR>djPt.VVVy7VVVVVVqDtMW.Et)|;dK7t*7(G*AD66RE|CREUjYdRqWNlqJAJ<H.ydJL%dp|#R|/Wb.RN23C<.l|1bkREJ|.JqyR6J.TJzHkkcJLS||5FBcT-cMNpt;lP+*|JRO:-|UA6|T#(jCX|8dJqE|b3&SpR>JM.5Hy/_5R.52q^cJL-*|5FBcRCcl&jdK|Yb|*-|bX5GVc.bXb#M8>D@JHERlJz+^cJLCMlFDW6_K9W6|HWp:.ySRkc5)bG4D.ENLO;d*7d|;t*FWW||HWdMR%bR|cJ1",
				"V.|G/.|EVALcV#d.cZV6D@;b8ct54.|&3cO7zZRUfkqcc9-*T.bG3d^>FEVRkVc.bLyXKcjt5d.R>ycN@bNtGVYJFVy*EtV.|5FBc1j2Uc9RVVR7b@Rf*XCD7HREt*V*|@N3*b5Vk&%*XCKRT7bAz/:N+-*|JRL3SREZ|5FBc-c|S|*zZ(D;9Kb:HbVYJACd.|L^X.4j.MV@bRHp.FkF1+BzJRc%VRC9|GJRDHERLk6p<3Vc.MccJlY.b...Jbbb.Jcc.#H:V.f>JVA/7RcDk*-*XVAjVyz.V*.&VZ||.A_R83VfG;@A>:O7(_@NVk3_VA/J*qBOS|537pR8#dNc",
				"VZ|H)J|EFf5cVSD9V.byWNVbXc35cZ|TPcO6cJb5#q/Uc.-*@kFGOD:X|EVRqVc.b+SdZcyP5cJ|X7c&NF&P*VU.bV%G41V.|5FBcYA#5cJb&VRVbNR%*CcWC*R4(*<*bN&3*R5VtTS*dcJR@6Rfcyp&l-*|JRl31R4.|5FBc-(|1|*zZ(8;9kRp*bVMJfVDJb^7j.UAKY4N|RGVKbt|>l+cJ|V%TRz.RGkFWHERO6ycV3VU.fUM.|JJb.AbJ*b-*bccJ#*@VJ-X.VYyVbc86*-*dVf)VScJV*.TU9||.Y_RXPT-H@NYXp4qP_N&Ec3_Vf/Z-A+P1R5(qzRX:D&z",
				"VZ|H)JbERf+cV%cK49|yWN9|XD35DK|TPcO6MJ|5#d/Uc8-*@KbGOD:X|EVRdVc.b+SqZcAP5cJRX7cVNR&P*1U.bV%*E(V.|5FBcYAV5cJb&VRVbNR%GCcWC*R4(*<*FN&33R5VtTS*qcJR@6Rfcyp&+-*|JRlO1R4.|5FBc-c|1|GzZ(8;9kRpGRVcJ>VDkF^7j.zA8YV2|RGzkFt|>l+MJ|c:TRz.RGJFWHERK6ycV3VcZfUMJ|JJbSJJJJJ.*JccJ#*@V8-X.VYyVbcW6*-*qVf)VScJVGZTU.||LY_RXPT-H@NYXpEdP_N&Ec3_Vf/Z-A+P1R5(dcRX:D&c",
				"VJbG/.lEVMLcV#dJCK|6D@;b>c)54.|&3cO7c.RUfkqccJ-*T.RG3d^>FEVRkVc.bLyXJCj(Ud.R>#cN@bN)G<YJFVy*E)V.|5FBc1j2Ud9|VVR7b@RfHXCD7*RE)*V*b@N3*b5Vk&%*XCKRT7|Az/:N+-*|JRL3SR<Z|5FBc-c|S|*zZ(D;9Kb:HlVCJAcYJ|+^X.cjJPV@bR*cJbt|1BBC.Fc%(RC9|*JFDHERL76c)3VcJPccJ|J.bb...bbJJbcc.yH:V.f>9V1/7RcDk*-*XV1jVyz.V*.&VJ||.1_R83VfG;@M>:Ok(_@NVc3_VM/J*qBzS|5(7cR>%dNc",
				"V.|G/.|EVALcV#d.cZV6D@;b8ct54.|&3cO7zZRUfkqcc9-*T.bG3d^>FEVRkVc.bLyXKcjt5d.R>ycN@bNtGVYJFVy*EtV.|5FBc1j2Uc9RVVR7b@Rf*XCD7HREt*V*|@N3*b5Vk&%*XCKRT7bAz/:N+-*|JRL3SREZ|5FBc-c|S|*zZ(D;9Kb:HbVYJACd.|L^X.4j.MV@bRHp.F)F1+BzJRc%VRC9|GJRDHERL76p<3Vc.MccJlY.b...Jbbb.Jcc.#H:V.f>JVA/7RcDk*-*XVAjVyz.V*.&VZ||.A_R83VfG;@A>:Ok(_@NVc3_VA/J*qBOS|537pR8#dNc",
				"V.|H)JbERfLcV%c8VJ|yWN9|Xc35UK|TPcO6cJ|5#d/Uc8-*@ZbG(D:X|EVRdVc.b+SqZcyP5cJRX7cVNR&P*1U.bV%H4(V.|5FBcYAV5cJb&VRVbNR%*CcWC*R4(*<HbN&3*L5VtTS*qcKR@6Rfcyp&+-*|JRl31R4.|5FBc-z|1|*zZ(8;9kFp*RVcJ>VDkR^7j.zAkYV2RRGM.Ft|fl+MJ|z:TRz.RGJFWHERL6ycV3Vc.fUMJ|^Jb..bJJb.*JccJ#*@V8-X.VYyVbcW6*-*qVf)VScJA*ZTU.||JY_RXPT-H@NYXpVdP_N&Ec3_Vf/Z-A+P1R5(dcRX:D&c",
				"V.VG/J|E%ALcV@dJc.|8D6;b>ct54.|&3cO7z.l5fkqcCJ-*X.|GOd^>FEVRkVc.bLyTJcjt5d.R>#cN6bNtG<YJFVy*E3V.|5FBc1j2Uc9RNVR7b6Rf^TCD7*REt*V*b6N3*b53k&%*TCKVX7|Az/:N+-*|JRL3SR<Z|5FBc-c|S|*zZ(D;9Kb:HbVCJMCYJ|+^T.4jJMV6bRHcJb)|1+Bd.|c%VRC9|*JbDHERL78cttVc.MccJlJbb...Jbb6..cc.#H:V.f@JVA/7RcDk*-*TVAjVyz.V*.&VJ||.A_R@tVfG;6A>:Ok3_6NVc3_VA/J*qBtS|5O7cR>2dNc",
				"V.bG/J|EFALcV#d.c.|6D@;b8ct5V.|&3c<7z.RUfkqVcZ-*TZ|G3d^>FEVRkVc.bLyXKcjt5d.R>#cN@bNtGVYJFVy*EtV.|5FBc1j2Uz9RVVR7b@Rf*XCD7*REtGV*b@N3*b5Vk&%*TCKRT7bAz/:N+-*|JRL3SROZ|5FBc-c|Sb*zZ(D;9KR:HbVYJ1ccJ|+^X.4jJMO@|RHYKb)F1+BCJFc%VRC9|GJbDHERL76p<3Vc.MccJlJ.bb..Jbb@..cz.#H:V.f>JVA/7RcDk*-*XVAjVyz.V*.&VJ||.A_R83VfG;@A>:Ok(_@NVc3_VA/.*qB(S|5<7cR>#dNc",
				"V.|G/.|EVALcV#d.cZV6D@;b8ct54.|&3cO7zZRUfkqcc9-*T.bG3d^>FEVRkVc.bLyXKcjt5d.R>ycN@bNtGVYJFVy*EtV.|5FBc1j2Uc9RVVR7b@Rf*XCD7HREt*V*|@N3*b5Vk&%*XCKRT7bAz/:N+-*|JRL3SREZ|5FBc-c|S|GzZ(D;9Kb:HbVYJACd.|L^X.4j.MV@bRHp.F)F1+BzJRc%VRC9|GJRDHERL76p<3Vc.MccJlY.b...Jbbb.Jcc.#H:V.f>JVA/7RcDk*-*XVAjVyz.V*.&VZ||.A_R83VfG;@A>:Ok(_@NVc3_VA/J*qBOS|537pR8#dNc",
				"bHcb1fl*bzZ2D8|ClPb<;bqc+L|GPb2Zz98FOA89||*AW6@.%bR>djPt.VVVV7VVVVVVqDtMW.Et)|;dK7t*7(G*AD66Ry|CREUjYdRqWNlqJAJ<H.ydJL%dp|#R|/Wb.RN23C<.l|1bkREJ|.JqyR6J.TJzHkkcJLS||5FBcT-cMNpt;lP+*|JRO:-|UA6|T#(jCX|8dJqE|b3&Sp.>JM.5Hy/_5R.52q^cJL-*|5FBcRCcf&jdK|Yb|*-|bX5GVc.bXb#M8>D@JHERlJz+^cJLCMlFDW6_K9W6|HWp:.ySRkc5)bG4D.ENLO;d*7d|;t*FWW||HWdlR%bR|cJ1",
				"V.|H)JbEbf+cV%cK4J|yWN9|XD35DK|TPcO6MJ|5#d/Uc8-*@KbGOD:X|EVRdVc.b+SqZcAP5cJRX7cVNR&P*1U.bV%*E(V.|5FBcYAV5cJb&VRVbNR%GCcWC*R4(*<*FN&3*R5VtTS*qcJR@6Rfcyp&+-*|JRl31R4.|5FBc-c|1|GzZ(8;9kRp*RVcJ>zDkR^7j.VAJYV2|RGMkFt|>l+MJ|c:TRz.RGJFWHERK6ycV3VcZfUMJ|JJbb.bJSbA*JccJ#*@V8-X.VYyVbcW6*-*qVf)VScJVGZTU.||LY_RXPT-H@NYXpVdP_N&Ec3_Vf/Z-A+P1R5(dcRX:D&c",
				"V.R*/ZREbM+VVVVJc.R6D@;VUc(5c.|c3cVkc9RONkqcd)-*P)|G(d^8REVRkVc.bLy:.c6t5d9R8Scf@|f<HVYJFVyGV3V.|5FBcMj25cZRVV|AbWlN>PcD1*REt*V>lWf(CRO(k&%GXcKRT1RAz/pfL-*|JRB3#REZ|5FBc-c|#RCzZ(D:9KFp*RVcJA4YK|ByX9cjJ7VWRR*d.l1|M+LcJbc2&RC9|*.RDHERL16ct<Ec.McY.l...R.R..RGR.cc.S*:V.%U.VMq1RcDk*-HPVMjV^z9N>9VVj|/.A^R8t&%GT@AUpEk3_WfVk(_V76.*q+t#|O31cR82dfc",
				"V.|H)JbERf+UV%cK49|yWN9|Xz35Dk|TPcO6MJ|5#d/Uc8-*@KbGOD:X|EVRdVc.b+SqZcAP5cJRX7cVNR&P*1U.bV%*E(V.|5FBcYAV5cJb&VRVbNR%GCcWC*R4(*<*FN&3*R5VtTS*qcJR@6Rfcyp&+-*|JRl31R4.|5FBc-c|1bGzZ(8;9kRp*FVUJ>VDk|^7j.zAJ>V2|RGzkFt|Yl+MJ|c:TRz.RGJFWHERK6ycV3VcZfUMJ|JJbb.bJSb.*JccJ#*@V8-X.VYyVbcW6*-*qVf)VScJVGZTU.||LY_RXPT-H@NYXpVdP_N&Ec3_Vf/Z-A+P1R5(dcRX:D&c",
				"48<lDy8<FO&:^7&yU^UF8.tkSFUq#GFk#SS#&+7P#AMUF_HS37282qC#O&CbN#*UD&&SqdyUlOO&XbdyU+ZO&+ULZ+ZPWO.WOcNK7.KHJ7PcOCUc_HSS7.8.b+cAp+UWVc.bVWA&H>ALX*(HWSWyq9CT(-&c-&|5FBcyYRlH+-pcYp8|5FBcyJJCXDGCHERFTG^DEKf|@;;E*DJFdSy6f;*b*|JRE@#K((7@bSST<bjTR*7G#Jl)B)j())E(/E/3jBC7@qSBlL*-RR7G#KC>;G(D7K>|GAHtK>|GCHtR2UHGUH&dtC-*CU6&1zc_d+T+G7zcpT*AAZp1zZ_d9;CG",
				"V.R*/ZREbM+VVVVJc.R6D@;VUc(5c.|c3cVkc9RONkqcd)-*P)|G(d^8REVRkVc.bLy:.c6t5d9R8Scf@|f<HVYJFVyGV3V.|5FBcMj25cZRVV|AbWlN>PcD1*REt*V>lWf(CRO(k&%GXcKRT1RAz/pfL-*|JRB3#REZ|5FBc-c|#RCzZ(D:9KFp*RVcJA4YK|ByX9cjJ7VWRR*d.l1|M+LcJbc2&RC9|*.RDHERL16ct<Ec.McY.l...R.R..RGR.cc.S*:V.%U.VMq1RcDk*-HPVMjV^z9N>9VVj|/.M^R8t&%GT@AUpEk3_WfVk(_V76.*q+t#|O31cR82dfc",
				"bHcb1fl*bzZ2D8|CF|b<;bqc+L|GPb2Zz98:OA89||*AW6@.%bR>djPt.VVVV7VVVVVVqDtMW.Et)|;dX7t*7(G*AD66Ry|CREUjYdRqWNlqJAJ<H.ydJL%dp|#R|/Wb.RN23C<.l|1bkREJ|.JqyR6J.TJzHkkcJLS||5FBcT-cMNpt;lP+*|JRO:-|UD6|T#(jCK|8dJqE|b3&Sp.>JM.5Hy/_5R.52q^cJL-*|5FBcRCcl&jdX|Yb|*-|bK5GVc.bKb#M8>D@JHERlJz+^cJLCMlFDW6_X9W6|HWp:.ySRkc5)bG4D.ENLO;d*7d|;t*FWW||HWdMR%bR|cJ1",
				"V.|H)JbERfLcV%c8VJ|yWN9|Xc35UK|TPcO6cJ|5#d/Uc8-*@ZbG(D:X|EVRdVc.b+SqZcyP5cJRX7cVNR&P*1U.bV%H4(V.|5FBcYAV5cJb&VRVbNR%*CcWC*R4(*<HbN&3*L5VtTS*qcKR@6Rfcyp&+-*|JRl31R4.|5FBc-c|1|*zZ(8;9kFp*RVcJ>VDkR^7j.zAkYV2RRGM.Ft|fl+MJ|z:TRz.RGJFWHERL6ycV3Vc.fUMJ|^Jb..bJJb.*JccJ#*@V8-X.VYyVbcW6*-*qVf)VScJA*ZTU.||JY_RXPT-H@NYXpVdP_N&Ec3_Vf/Z-A+P1R5(dcRX:D&c",
				"V.VG/J|EVALcV@dJc.|8D6;b>ct54.|&3cO7zZR5fkqcCJ-*X.|GOd^>FEVRkVc.bLyTJcjt5d.R>#cN6bNtG<YJFVy*E3V.|5FBc1j2Uc9RNVR7b6Rf*TCD7*REt*V*b6N3*b53k&%*TCKVX7|Az/:N+-*|JRL3SR<Z|5FBc-c|S|*zZ(D;9Kb:HbVCJM4YJ|+^T.4jJMV6FRHcJb)|1+Bd.|c%VRC9|*JbDHERL78cttVc.MccJlJbb...Jbb6+&ccK#H:V.f@JVA/7RcDk*-*TVAjVyz.V*.&VJ||.A_R@tVfG;6A>:Ok3_6NVc3_VA/J*qBtS|5O7cR>2dNc",
				"V.bG/J|EVALcV#d.c.|6D@;b>ct54.|&3p<7zKRUfkqVP.-*TZ|G3d^>FEVRkVc.bLyXKcjt5d.R8#cN@bNtGVYJFVy*EtV.|5FBc1j2U49RVVR7b@Rf*XCD7*REt*V*b@N3*b5Vk&%*TCKRT7bAz/:N+-*|JRL3SROZ|5FBc-d|Sb*zZ(D;9Kb:HbVYJMccJR+^X.4jJMV@bR*YJb)F1+BCJbc%VRC9|GJbDHERL76p<3Vc.AccJlJ.bb..Jbb@..cc.#H:V.f8JVA/7RCDk*-*XVAjVyz.V*.&VJ||.A_R83VfG;@A8:Ok(_@NVc3_VA/J*qB(S|5O74R>#dNc",
				"V.bG/J|EVALcV#d.c.|6D@;b8ct5V.|&3c<7z.RUfkqVcZ-*TZ|G3d^>FEVRkVc.bLyXKcjt5d.R>#cN@bNtGVYJFVy*EtV.|5FBc1j2Uz9RVVR7b@Rf*XCD7*REtGV*b@N3*b5Vk&%*TCKRT7bAz/:N+-*|JRL3SROZ|5FBc-c|Sb*zZ(D;9KR:HbVYJ1ccJ|+^X.4jJMO@|RHYKb)F1+BCJFc%VRC9|GJbDHERL76p<3Vc.MccJlJ.bb..Jbb@..cz.#H:V.f>JVA/7RcDk*-*XVAjVyz.V*.&VJ||.A_R83VfG;@A>:Ok(_@NVc3_VA/.*qB(S|5<7cR>#dNc",
				"VJ|H)JbE|flcV%c.zJVyWN9bXM35DZ|TPcO6zJ|5#d/Uc8-*@9bGOD:X|EVRdVc.b+SqZcAP5cJ|X7cVNR&P*VU.bV%*E(V.|5FBcYAV5cJb&VRVbNR%*CcWC*R4(*<*bN&3*R5VtTS*qcJR@6Rfcyp&+-*|JRl31R4.|5FBc-V|1|*zZ(8;9kbp*FVcJ>UD.b^7j9zAJYV2|RGVKFt|>l+MKbc:TRz.RG9FWHERK6ycV3Vc.fUM.|.Jbb.bJ*bb*bccJ#*@VJ-X.VYytbc86*-*qVf)VSMJV*kTU.||9Y_RXPT-H@NYXpVdP_N&Ec3_Vf/Z-A+O1R5(dcRX:D&c",
				"V.|H)JbERf+cV%cK4L|yWN9|XD35DK|TPcO6Mk|5#d/Uc8-*@KbGOD:X|EVRdVc.b+SqZcAP5cJRX7cVNR&P*1U.bV%*E(V.|5FBcYAV5cJb&VRVbNR%GCcWC*R4(*<*FN&3*R5VtTS*qcJR@6Rfcyp&+-*|JRl31R4.|5FBc-c|1|GzZ(8;9kRp*RVcJ>VDkR^7j.zAJYV2|RGMkFt|>B+MJ|c:TRz.RGJFWHERK6ycV3VcZfUMJ|JJbSJJJJJ..JccJ#*@V8-X.VYyVbcW6*-*qVf)VScJVGZTU.||LY_RXPT-H@NYXpVdP_N&Ec3_Vf/Z-A+P1R5(dcRX:D&c",
				"V.VG/J|E%ALcV@dJc.|8D6;b>ct54.|&3cO7z.R5fkqcCJ-*X.|GOd^>FEVRkVc.bLyTJcjt5d.R>#cN6bNtG<YJFVy*E3V.|5FBc1j2Uc9RNVR7b6Rf*TCD7*REt*V*b6N3*b53k&%*TCKVX7|Az/:N+-*|JRL3SR<Z|5FBc-c|S|*zZ(D;9Kl:HVVzJA4Y.|+^T.4jJMV6bRHcJb)|1+Bd.|Y%VRC9|*JbDHERL78cttVc.MccJlJbbb..Jbb6H.cc.#H:V.f@KVA/7RcDk*-*TVAjVyz.V*.&VJ||.A_R@tVfG;6A>:Ok3_6NVc3_VA/J*qBtS|5O7cR>2dNc",
				"bSc-fc)+bzZzd8|CL<KW38@1k4cqOCzZWAN%Ll^A//Yyd/MD&b.p:1+jU<LL.LS.:4;._d:(d7E:*Y3:q3:Y3:qfyy//7ptBGE:^OjG_d:(S595zHUE:5#&:1+:7YUdKDR:WtCWG(C4K>Gp52DJ@pR/XGRJWH>>&5fC||5FBc.-bq:1:P%<6*|JR*<-Tjk/2.::16zt^jJ_EPbc:c47pJPG5HERM5D.XWH>c54-*|5FBcUKcF:1:qLOc|*-+6WXLVc.bzKq%Vpy/5@EDq5W6>c5f8Y)|ly|/FAk//_AjY.;-G>c5#-%LAGp:_P3jE3:|3:|qAd//_d:(.pKU>c5#",
				"V.bG/J|EVALcV#d.c.|6D@;b8ct5V.|&3c<7z.RUfkqVcZ-*TZ|G3d^>FEVRkVc.bLyXKcjt5d.R>#cN@bNtGVYJFVy*EtV.|5FBc1j2Uz9RVVR7b@Rf*XCD7*REtGV*b@N3*b5Vk&%*TCKRT7bAz/:N+-*|JRL3SROZ|5FBc-c|Sb*zZ(D;9KR:HbVYJ1ccJ|+^X.4jJMV@|RHYKb)F1+BCJFc%VRC9|GJbDHERL76p<3Vc.MccJlJ.bb..Jbb@..cz.#H:V.f>JVA/7RcDk*-*XVAjVyz.V*.&VJ||.A_R83VfG;@A>:Ok(_@NVc3_VA/.*qB(S|5<7cR>#dNc",
				"#.VG/J|E7ANcV>dJc.|WDY;b1cP5U.|S3cO6U.R5#kpcCJ-*@.|GOd^1FEVRkVc.bNyXJc<P5d.R1Lc&Yb&PG8%JFVy*E3V.|5FBcl<2qc9R&VR6bYR#*XCD6*REP*V*bY&3*b53kS7*XCKV@6|Az/:&+-*|JRN34R8Z|5FBc-c|4|*zZ)D;9Kb:HbVCJMU%J|+^X.U<JMVYbRHcJbj|l+Bd.|%7VRC9|*JbDHERN6WcPPVc.MccJfJbbb..JbbY..cc.LH:V.#>JVA/6RcDk*-*XVA<Vyz.V*.SVJ||.A_R>PV#G;YA1:Ok3_Y&Vc3_VA/J*pBP4|5O6cR12d&c",
				"V.VG/J|EVALcV@dJc.|8D6;b>ct54.|&3cO7z.R5fkqcCJ-*X.|GOd^>FEVRkVc.bLyTJcjt5d.R>#cN6bNtG<YJFVy*E3V.|5FBc1j2Uc9RNVR7b6Rf*TCD7*REt*V*b6N3*b53k&%*TCKVX7|Az/:N+-*|JRL3SR<Z|5FBc-c|S|*zZ(D;9Kb:HbVCJM4YJ|+^T.4jJMV6FRHcJb)|1+Bd.|c%VRC9|*JbDHERL78cttVc.MccJlJbb...Jbb6+&ccK#H:V.f@JVA/7RcDk*-*TVAjVyz.V*.&VJ||.A_R@tVfG;6A>:Ok3_6NVc3_VA/J*qBtS|5O7cR>2dNc",
				"V.|H)JbERfLcV%c8VJ|yWN9|Xc35UK|TPcO6cJ|5#d/Uc8-*@ZbG(D:X|EVRdVc.b+SqZcyP5cJRX7cVNR&P*1U.bV%*4(V.|5FBcYAV5cJb&VRVbNR%*CcWC*R4(*<HbN&3*L5VtTS*qcKR@6Rfcyp&+-*|JRl31R4.|5FBc-c|1|*zZ(8;9kFp*RVcJ>VDkR^7j.zAkYV2RRGM.Ft|fl+MJ|z:TRz.RGJFWHERL6ycV3Vc.fUMJ|^Jb..bJJb.*JccJ#*@V8-X.VYyVbcW6*-*qVf)VScJA*ZTU.||JY_RXPT-H@NYXpVdP_N&Ec3_Vf/Z-A+P1R5(dcRX:D&c",
				"bSc-ft)ObzZzd8|CL<Kz38@164c)OCzZWAN%|lVA//YyA/MDpb.p:1+jU<LL.LSSLLL._d:(d7E:4Y;:q3:Y3:qfyy//7ptBGE:^OjG_d:(S595zHUE:5#&:1+:7YUdKDR:WtCzG(C4K>Gp52DJ@pR/XGKJzH>>&5fC||5FBc.-bqj4:2PO6*|JR*+-TjA/F7:jp6zt^:J_EPbc:c17pJPG5HERMXD.XWH>c5f-*|5FBcU6cF:1:qLOc|*-+6WXLVc.bzKq%Vpy/5@EDq5z6>c5f8Y*|ly|/FAk//_AjY.p-G>&5f-%LAGE:4|3jE3:|3:PqAd/K_d:(.pkU>c5f",
				"V.|H)J|EFf5cVSz9VJbyWNVbXc35cZ|TPcO6cJ|5#q/UcK-*@kFGOD:X|EVRqVc.b+SdZcAP5c<|X7c&NF&P*VU.bV%G41V.|5FBcYA-5cJb&VRVbNR%*@cWC*R4(*<*bN&3*R5VtTS*dckR@6Rfcyp&l-*|JRl31R4.|5FBc-(|1|*zZ38;9k|p*bVMJfcDJbB7j.zA.Y42|RGVKbt|>^^cJ|V%TRzJRGkFWHERO6ycV3VU.fUM.|JJb.AbA*b-*bccJ#*@VJ-X.VYyVbc86*-*dVf)VScJV*.TU9||.Y_RXPT-H@NYXp4qP_N&Ec3_Vf/Z-A+P1R5(qzRX:D&z",
				"V.VG/J|E%ALcV@dJc.|8D6;b>ct54.|&3cO7z.l5fkqcCJ-*X.|GOd^>FEVRkVc.bLyTJcjt5d.R>#cN6bNtG<YJFVy*E3V.|5FBc1j2Uc9RNVR7b6Rf*TCD7*REt*V*b6N3*b53k&%*TCKVX7|Az/:N+-*|JRL3SR<Z|5FBc-c|S|*zZ(D;9Kb:HbVCJMCYJ|+^T.4jJMV6bRHcJb)|1+Bd.|c%VRC9|*JbDHERL78cttVc.MccJlJbb...Jbb6..cc.#H:V.f@JVA/7RcDk*-*TVAjVyz.V*.&VJ||.A_R@tVfG;6A>:Ok3_6NVc3_VA/J*qBtS|5O7cR>2dNc",
				"bSc-ft)ObzZzd8|CL<Kz38@164c)OCzZWAN%|lVA//YyA/MDpb.p:1+jU<LL.LSSLLL._d:(d7E:4Y;:q3:Y3:qfyy//7ptBGE:^OjG_d:(S595zHUE:5#&:1+:7YUdKDR:WtCzG(C4K>Gp52DJ@pR/XGRJzH>>&5fC||5FBc.-bqj4:2PO6*|JR*+-TjA/F7:jp6zt^:J_EPbc:c17pJPG5HERMXD.XWH>c5f-*|5FBcU6cF:1:qLOc|*-+6WXLVc.bzKq%Vpy/5@EDq5z6>c5f8Y*|ly|/FAk//_AjY.p-G>&5f-%LAGE:4|3jE3:|3:PqAd/K_d:(.pkU>c5f",
				"V.R*/.|EbM+VVVVJc.R6D@;VUc(5c.|c3cV1c9RONkqcc)-*P)|G(d^8REVRkVc.bLy:.c6t5d9R8Scf@|f<HVYJFVyGV3V.|5FBcMj25cZRVV|AbWlN>PcD1*REt*V>lWf(CRO(kV%GXcKRT1RMz/pfL-*|JRB3#REZ|5FBc-c|#RCzZ(D:9KFp*RVcJA4YK|ByX9cjJ7VWRR*d.l1lM+LcJbc2&RC9|*.RDHERL16ct<Ec.McY.l...RHR..R.R.cc.S*:V.%U.VMq1RcDk*-HPVMjV^z9N>9VVJ|/.7^R8t&%GT@AUpEk3_WfVk(_V76.*q+t#|O31cR82dfc",
				"b^cbf^FNbzZzd^|k*N8WCO%^Of^(^8WZW^7##^7^TT*^^T/R>MRX:4+:RR8.*.#*.R*R_d:|d&>:f#C:FC:#C:14ddT/&Xj-&X:4N:G_d:qHAFL2H@X:5fk:4+:.|@^bRRSWtb2@YKfb<&X5|.JHX@TAGULW^<<^Lf)||5FBc@-^|:4:E|Np*|JRPN-^:^/*&S:4lW^7:56>Fbj:)4UEL|GAHER;DG.AWH39Lf-*|5FBc@Ocq:4:qq+M|*-+b2L#Vc.bW8#*%>dTL_XG#5z8^7Lf8q(#^dTTqd^TTH^:#.XK@^yAf2|#^@X:fqC:*CS*C:||ddTT6dS*&k8R<y5f",
				"V.|H)J|EFf5cVSz9VJbyWNVbXc35DZ|TPcO6cJ|5#q/UcK-*@kFGOD:X|EVRqVc.b+SdZcAP5cJ|X7c&NF&P*VU.bV%G41V.|5FBcYA-5cJb&VRVbNR%*@cWC*R4(*<*bN&3*R5VtTS*dckR@6Rfcyp&l-*|JRl31R4.|5FBc-c|1|*zZ38;9k|p*bVMJfcDJbB7j.zA.Y42|RGVKbt|>^^MJ|V%TRzJRGkFWHERO6ycV3VU.fUM.|JJb.AbA*b-*bccJ#*@VJ-X.VYyVbc86*-*dVf)VScJV*.TU9||.Y_RXPT-H@NYXp4qP_N&Ec3_Vf/Z-A+P1R5(qzRX:D&z",
				"V.VG/J|EVALcV@dJc.|8D6;b>ct54.|&3cO7z.R5fkqcCJ-*X.|GOd^>FEVRkVc.bLyTJcjt5d.R>#cN6bNtG<YJFVy*E3V.|5FBc1j2Uc9RNVR7b6Rf*TCD7*REt*V*b6N3*b53k&%*TCKVX7|Az/:N+-*|JRL3SR<Z|5FBc-c|S|*zZ(D;9Kb:HbVCJM4YJ|+^T.4jJMV6bRHcJb)|1+Bd.|c%VRC9|*JbDHERL78cttVc.MccJlJbb...Jbb6+&ccK#H:V.f@JVA/7RcDk*-*TVAjVyz.V*.&VJ||.A_R@tVfG;6A>:Ok3_6NVc3_VA/J*qBtS|5O7cR>2dNc",
				"V.|H)J|EFf5cV:z9VJbyWNVbXc35cZ|TPcO6cJ|5#q/UDK-*@kFGOD:X|EVRqVc.b+SdZcAP5cJ|X7c&NF&P*VU.bV%G41V.|5FBcYAk5cJb&VRVbNR%*@cWCCR4(*<*bN&3*R5VtTS*dckR@6Rfcyp&l-*|JRl31R4.|5FBc-(|1|*zZ38;9k|p*bVMJfcDJb^7jkzA.Y42|RGVKbt|>^+cJ|V%TRzJRGkFWHERO6ycV3VU.fUM.|JJb.AbA*b-*bccJ#*@VJ-X.VYyVbc86*-*dVf)VScJV*.TU9||.Y_RXPT-H@NYXp4qP_N&Ec3_Vf/Z-A+P1R5<qzRX:D&z",
				"pKbH/.lEVMLcVfdJCJ|6D@;b>c)54.|&34O7zZRUfkqccJ-*T.|G3d^>FEVRkVc.bLyXJcj(#d.R>#cN@|N)G<YJFVy*E)V.|5FBc1j2Ud9|VVR7b@Rf*XCD7*RE)*V*b@N3*b5Vk&%*XCKRT7|Az/:N+-*|JRL3SR<Z|5FBc-c|S|*zZ(D;9Kb:HbVcJAcYJ|B^X.4jJP<@bR*cJbt|1+BC.bd%VRC9b*JbDHERL76C)3Vc.PccJ|J.bb...bbJJbcW.y*:V.f>JV1/7RcD^*-*XV1jVyz.V*.&VJ||.1_R83VfG;@M>:Ok(_@NVc3_VM/J*qBzS|5O7cR>%dNc",
				"LyCL1>*pXzZ297|ClAXkjLHc;O%|pLzZKf7Tk#7t(PF##PP1EX@EW:AM<U#+U++U++UF4#Mq#.E8O|j1+jU+j1+*##PP.E%;.yU:AW<Y^5l4JS5DH.E8JKE/:pWd|.#XdRWD>LD<qX1cN.EJT>JHER3JRG_DYNNcJ1;||5FBc<-Cq/:)F6&-*|JRk1->W#(+1U):;D%7M5py*b>8X:Gy5*.5YEd3_d.5DHNCJ1-*|5FBc.Xcl8:/F+pX|*-pbDJ+Vc.bDXqq7E9PJHyG*_zXNC51;Tl|f#PP+9#(PH#MT.E-<Oc5KXq+#.pM1+j1/jU+jU+*9#PP^fWF.EXRNCJ1",
				"V.|H)JbE|flcV%zJzJVyWN9bXc35DK|TPcO6c.|5#d/Uc8-*@9VGOD:X|EVRdVc.b+SqZcAP5cJ|X7cVNR&P*VU.bV%*4(V.|5FBcYAV5cJb&VRVbNR%*CcWC*R4(*<*bN&3*R5VtTS*qcJR@6Rfcyp&+-*|JRl31R4.|5FBc-c|1|*zZ(8;9kRpGFVcJ>UD.F^7j9zAJYV2|RGVKFt|Yl+MJbc:TRz.RGKFWHER36ycV3VM.fUM.|.Jb..bJ*bb-bcc9#*@VJ-X.VYytbc86*-*qVf)VSMJV*.TU.||JY_RXPT-H@NYXpVdP_N&Ec3_Vf/Z-A+O1R5(dcRX:D&c",
				"VJbG/.lEVMLcV#dJCK|6D@;b>c)54.|&3cO7c.RUfkqccJ-*T.RG3d^>FEVRkVc.bLyXJcj(Ud.R>#cN@bN)G<YJFVy*E)V.|5FBc1j2Ud9|VVR7b@RfHXCD7*RE)*V*b@N3*b5Vk&%*XCKRT7|Az/:N+-*|JRL3SR<Z|5FBc-c|S|*zZ(D;9Kb:HlVCJAcYJ|+^X.cjJP)@bR*cJbt|1LBC.Fc%VRC9|*JbDHERL76c)3VcJPccJ|J.bb...bbJJbcc.yH:V.f>9V1/7RcDk*-*XV1jVyz.V*.&VJ||.1_R83VfG;@M>:Ok(_@NVc3_VM/J*qBzS|5(7cR>%dNc",
				"VK|*/.lEVMWcVfdJCJ|6D@;b>c)54.|&3cO7z.RUfkqccJ-*T.|G3d^>FEVRkVc.bLyX9cj(#d.R>#cN@bN)G<YJFVy*E)V.|5FBc1j2Ud9lVVR7b@Rf*XCD7*RE)*V*b@N3*b5Vk&%*XCKRT7|Az/:N+-*|JRL3SR<Z|5FBc-c|S|*zZ(D;9Kb:HbVYJAcYJ|+^X.4jJPV@bR*dJRt|1+BC.bY%VRC9b*JbDHERL76c)3Vc.PccJ|J.bb...b.JJbcc.y*:V.f>JV1/7RcDk*-*XV1jVyz.V*.&VJ||Z1_R83VfG;@M>:Ok(_@NVc3_VM/J*qBzS|5O7cR>%dNc",
				"pKbH/.lEVMLcVfdJCJ|6D@;b>c)54.|&34O7zZRUfkqccJ-*T.|G3d^>FEVRkVc.bLyXJcj(#d.R>#cN@|N)G<YJFVy*E)V.|5FBc1j2Ud9|VVR7b@Rf*XCD7*RE)*V*b@N3*b5Vk&%*XCKRT7|Az/:N+-*|JRL3SR<Z|5FBc-c|S|*zZ(D;9Kb:HbVcJAcYJ|B^X.4jJP<@bR*cJbt|1+BC.bd%VRC9b*JbDHERL76c)3Vc.PccJ|J.bb...bbFlbcW.y*:V.f>JV1/7RcDk*-*XV1jVyz.V*.&VJ||.1_R83VfG;@M>:Ok(_@NVc3_VM/J*qBzS|5O7cR>%dNc",
				"bSc-ft)ObzZzd8|CL<Kz38@164c)OCzZWAN%|lVA//YyA/MDpb.p:1+jU<LL.LS.:4;._d:(d7E:4Y3:q3:Y3:qfyy//7ptBGE:^OjG_d:(S595zHUE:5#&:1+:7YUdKDR:WtCWG(C4K>Gp52DJ@pR/XGRJWH>>&5fC||5FBc.-bqj4:2PO6*|JR*+-Tjd/F7:j(6zt^:J_EPbc:c47pJPG5HERMXD.XWH>c5f-*|5FBcU6cF:1:qLO-|*-+6WXLVc.bzKq%Vpy/5@EDq5W6>c5f8Y*|ly|/FAk//_AjY.p-G>&5f-%LAGE:4|3jE3:|3:PqAd/K_d:(.pkU>c5f",
				"VK|*/.lEbMLcVfdJCZ|6D@;b>5)54.|&3cO7z9RUfkqccJ-*T.|G3d^>FEVRkVc.bLyXJcj(#d.R>ycN@bN)G<YJFVy*E)V.|5FBc1j2Ud9lVVR7b@Rf*XCD7*RE)*V*b@N3*b5Vk&%*XCKRT7|Az/:N+-*|JRL3SR<Z|5FBc-c|S|*zZ(D;9Kb:HbVYJAcYJ|B^X.4jJPV@bR*cJRt|1+BC.bC%VRC9b*JbDHERL76c)3Vc.PccJ|J.bb...b.JJbcc.y*:V.f>JV1/7RcDk*-*XV1jVyz.V*.&VJ||.1_R83VfG;@M>:Ok(_@NVc3_VM/J*qBzS|5O7cR>%dNc",
				"V.bG/J|EVALcV#d.c.l6D@;b8ct54.|&3c<7z.RUfkqVcK-*TZ|G3d^>FEVRkVc.bLyXKcjt5d.R>#cN@bNtGVYJFVy*EtV.|5FBc1j2U49RVVR7b@Rf*XCD7*REt*V*b@N3*b5Vk&%*TCKRT7bAz/:N+-*|JRL3SROZ|5FBc-c|Sb*zZ(D;9Kb:HbVYJMccJ|+^X.4jJAV@bRHYJb)F1+BCKbc%VRC9|GJbDHERL76p<3Vc.MccJlJ.bb..Jbb@..cc.#H:V.f>JVA/7RCDk*-*XVAjVyz.4*.&VJ||.A_R83VfG;@A>:Ok(_@NVc3_VA/J*qB(S|5O7cR>#dNc",
				")>:ytyAL6:&^AktyA(>Z>.k9SF:+#G:9FFSF&:1P#:CqF_-//k.>.dCF:&CbN#WqH&&FbUyq:OO&MbPyqK4:&KqjZKZP4:.4:4NWk.W%RkPc:;qc_%SSk.>.XLZ:<LqZVc.b(4:V-pVXMR(-4B4yXR^^^-&Zb&|5FBcyYRY-K-<FY<>|5FBcyJJ;FT@K%83f%@lHERM|@KHE37JM&Fy4M|*b*|JRkG43((kGbSBHLbRHR*kG#RLUL|L||L11UUUdd171Gd//Ld*-RR8G6RCpDG(DkWp|GKDk3p|GK|kW.qHGqD&9kK-*Lq6&dz6_9lHlGkz4&HWzAZ&+zZ_9^|KG",
				"V.VG/J|E%ALcV@dJc.|8D6;b>ct54.|&3cO7z.R5fkqcCJ-*X.|GOd^>FEVRkVc.bLyTJcjt5d.R>#cN6bNtG<YJFVy*E3V.|5FBc1j2Uc9RNVR7b6Rf*TCD7*REt*V*b6N3*b53k&%*TCKVX7|Az/:N+-*|JRL3SR<Z|5FBc-c|S|*zZ(D;9Kb:HbVCJM4YJ|+^T.4jKMV6bRHcJb)|1+Bd.|Y%VRC9|*JbDHERL78cttVc.MccJlJbbb..Jbb6..cc.#H:V.f@JVA/7RcDk*-*TVAjVyz.V*.&VJ||.A_R@tVfG;6A>:Ok3_6NVc3_VA/J*qBtS|5O7cR>2dNc",
				"V.|H)J|EFf5cV:z9VJbyWNVbXc35cZ|TPcO6cJ|5#q/UzK-*@kFGOD:X|EVRqVc.b+SdZcAP5cJ|X7c&NF&P*VU.bV%G41V.|5FBcYAk5cJb&VRVbNR%*@cWC*R4(*<*bN&3*R5VtTS*dckR@6Rfcyp&l-*|JRl31R4.|5FBc-(|1|*zZ38;9k|p*bVMJfcDJb^7jkzA.Y42|RGVKbt|>^+cJ|V%TRzJRGkFWHERO6ycV3VU.fUM.|JJb.AbA*b-*bccJ#*@VJ-X.VYyVbc86*-*dVf)VScJV*.TU9||.Y_RXPT-H@NYXp4qP_N&Ec3_Vf/Z-A+P1R5<qzRX:D&z",
				"V.|H)JbERf+cV%cK49|yWN9|XD35DO|TPcO6MJb5#d/Uc8-*@KbGOD:X|EVRdVc.b+SqZcAP5cJRX7cVNR&P*1U.bV%*E(V.|5FBcYAV5cJR&VRVbNR%GCcWC*R4(*<*FN&3*R5VtTS*qcJR@6Rfcyp&+-*|JRl31R4.|5FBc-M|1|GzZ(8;9kRp*RVcJ>zDkR^7j.zAJYV2|RGMkFt|>l+UJ|c:TRz.RGJFWHERK6ycV3VcZfUMJ|JJJSJJJJJb*JccJ#*@V8-X.VYyVbcW6*-*qVf)VScJVGZTU.||LY_RXPT-H@NYXpVdP_N&Ec3_Vf/Z-A+P1R5(dcRX:D&c",
				"VJ|H)JbE|flcV%c.zJVyWN9bXM35DZ|TPcO6zJ|5#d/Uc8-*@9bGOD:X|EVRdVc.b+SqZcAP5cJ|X7cVNR&P*VU.bV%*E(V.|5FBcYAV5cJb&VRVbNR%*CcWC*R4(*<*bN&3*R5VtTS*qcJR@6Rfcyp&+-*|JRl31R4.|5FBc-V|1|*zZ(8;9kbp*FVcJ>UD.b^7j9zAJYV2|RGVKFt|>l+MKbc:TRz.RG9FWHERK6ycV3Vc.fUM.|.Jbb.bJ*bb*bccJ#*@VJ-X.VYytbc86*-*qVf)VSMJV*.TU.||9Y_RXPT-H@NYXpVdP_N&Ec3_Vf/Z-A+O1R5(dcRX:D&c",
				"bSc-ft)ObzZzd8|CL<Kz38@164c)OCzZWAN%|lVA//YyA/MDpb.p:1+jU<LL.LS.:4;._d:(d7E:4Y3:q3:Y3:qfyy//7ptBGE:^OjG_d:(S595zHUE:5#&:1+:7YUdKDR:WtCWG(C4K>Gp52DJ@pR/XGRJWH>>&5fC||5FBc.-bqj4:2PO6*|JR*+-Tjd/F7:j(6zt^:J_EPbc:c47pJPG5HERMXD.XWH>c5f-*|5FBcU6cF:1:qLOc|*-+6WXLVc.bzKq%Vpy/5@EDq5W6>c5f8Y*|ly|/FAA//_AjY.p-G>&5f-%LAGE:4|3jE3:|3:PqAd/K_d:(.pkU>c5f",
				"V.|H)JbERfLcV%c8VJ|yWN9|Xc35UK|TPcO6cJ|5#d/Uc8-*@ZbG(D:X|EVRdVc.b+SqZcyP5cJRX7cVNR&P*1U.bV%*4(V.|5FBcYAV5cJb&VRVbNR%*CcWC*R4(*<HbN&3*L5VtTS*qcKR@6Rfcyp&+-*|JRl31R4.|5FBc-c|1|*zZ(8;9kFp*RVcJ>VDkR^7j.zA9YV2RRGM.Ft|fl+MJ|z:TRz.RGJFWHERL6ycV3Vc.fUMJ|JJb..bJJb.*JccJ#*@V8-X.VYyVbcW6*-*qVf)VScJA*ZTU.||JY_RXPT-H@NYXpVdP_N&Ec3_Vf/Z-A+P1R5(dcRX:D&c",
				"b^cbf^qNbzZzd^|k*N8WCO%^Of^(^8WZW^7##^7^TT*^^TTR>MRX:4+:RR8.*.#*.RtR_d:|d&>:f#C:FC:#C:14dd//&Xj-&X:4N:G_d:qHAFL2H@X:5fk:4+:.|@^bRRSWtb2@YKfb<&X5|.JHX@;AGULW^<<^Lf)||5FBc@-^|:4:E|Np*|JRPN-^:^/*&SS4lz^7S56>Fbj:)4UEL|GAHER;DG.AWH39Lf-*|5FBc@Ocq:4:qq+M|*-+b2L#Vc.bW8#*%>dTL_XG#5z8^7Lf8q(#^dTTqd^TTH^:#@XK@^yAf2|#^@X:fqC:*CS*C:||dd//6dS*&k8R<y5f",
				"pKbH/.lEVMLcVfdJCJ|6D@;b>c)54.|&34O7zZRUfkqccJ-*T.|G3d^>FEVRkVc.bLyXJcj(#d.R>#cN@|N)G<YJFVy*E)V.|5FBc1j2Ud9|VVR7b@Rf*XCD7*RE)*V*b@N3*b5Vk&%*XCKRT7|Az/:N+-*|JRL3SR<Z|5FBc-c|S|*zZ(D;9Kb:HbVcJAcYJ|B^X.4jJP<@bR*cJbt|1+BC.bd%VRC9b*JbDHERL76c)3Vc.PccJ|J.bb...bbJJbcW.y*:V.f>JV1/7RcDk*-*XV1jVyz.V*.&VJ||.1_R83VfG;@M>:Ok(_@NVc3_VM/J*qBzS|5O7cR>%dNc",
				"VK|*/.lEVMWcVfdJCJ|6D@;b>c)54.|&3cO7z.RUfkqccJ-*T.|G3d^>FEVRkVc.bLyX9cj(#d.R>#cN@bN)G<YJFVy*E)V.|5FBc1j2Ud9lVVR7b@Rf*XCD7*RE)*V*b@N3*b5Vk&%*XCKRT7|Az/:N+-*|JRL3SR<Z|5FBc-c|S|*zZ(D;9Kb:HbVYJAcYJ|+^X.4jJPV@bR*dJRt|1+BC.bY%VRC9b*JbDHERL76c)3Vc.PccJ|J.bb...b.JJbcc.y*:V.f>JV1/7RcDk*-*XV1jVyz.V*.&VJ||.1_R83VfG;@M>:Ok(_@NVc3_VM/J*qBzS|5O7cR>%dNc",
				"V.|H)JbE|flcV%c.zJVyWN9bXM35DK|TPcO6zJ|5#d/Uc8-*@9bGOD:X|EVRdVc.b+SqZcAP5cJ|X7cVNR&P*VU.bV%*4(V.|5FBcYAV5cJb&VRVbNR%*CcWC*R4(*<GbN&3*R5VtTS*qcJR@6Rfcyp&+-*|JRl31R4.|5FBc-V|1|*zZ(8;9kbp*FVcJ>UD.b^7j9zAJYV2|RGVKFt|>l+MJbc:TRz.RG9FWHERK6ycV3Vc.fUM.|.Jbf.bJ*bb*bccJ#*@VJ-X.VYytbc86*-*qVf)VSMJV*.TU.||9Y_RXPT-H@NYXpVdP_N&Ec3_Vf/Z-A+O1R5(dcRX:D&c",
				"V.VG/J|E7ANcV1dJc.|WDY;b>cP5U.|S3cO6z.R5#kpcCJ-*@.|GOd^>FEVRkVc.bNyXJc<P5d.R>Lc&Yb&PG8%JFVy*E3V.|5FBcl<2qc9R&VR6bYR#*XCD6*REP*V*bY&3*b53kS7*XCKV@6|Az/:&+-*|JRN34R8Z|5FBc-c|4|*zZ)D;9Kb:HbVCJMU%J|+^X.U<JMVYbRHcJbj|l+Bd.|%7VRC9|*JbDHERN6WcPPVc.MccJfJbbb..JbbY..cc.LH:V.#1JVA/6RcDk*-*XVA<Vyz.V*.SVJ||.A_R1PV#G;YA>:Ok3_Y&Vc3_VA/J*pBP4|5O6cR>2d&c",
				"V.VG/J|E%ALcV@dJc.|8D6;b>ct54.|&3cO7z.R5fkqcCJ-*X.|GOd^>FEVRkVc.bLyTJcjt5d.R>#cN6bNtG<YJFVy*E3V.|5FBc1j2Uc9RNVR7b6Rf*TCD7*REt*V*b6N3*b53k&%*TCKVX7|Az/:N+-*|JRL3SR<Z|5FBc-c|S|*zZ(D;9Kb:HbVCJM4YJ|+^T.4jJMV6bRHcJb)|1+Bd.|Y%VRC9|*JbDHERL78cttVc.MccJlJbbb..Jbb6..cc.#H:V.f@JVA/7RcDk*-*TVAjVyz.V*.&VJ||.A_R@tVfG;6A>:Ok3_6NVc3_VA/J*qBtS|5O7cR>2dNc",
				"V.|H)JbERf+cV%cK49|yWN9|XD35DO|TPcO6MJb5#d/Uc8-*@KbGOD:X|EVRdVc.b+SqZcAP5cJRX7c&NR&PG1U.bV%*E(V.|5FBcYAV5cJR&VRVbNR%GCcWC*R4(*<*FN&3*R5VtTS*qcJR@6Rfcyp&+-*|JRl31R4.|5FBc-M|1|GzZ(8;9kRp*RVcJ>zDkR^7j.zAJYV2|RGMkFt|>l+UJ|c:TRz.RGJFWHERK6ycV3VcZfUMJ|JJJSJJJJJb*JccJ#*@V8-X.VYyVbcW6*-*qVf)VScJVGZTU.||LY_RXPT-H@NYXpVdP_N&Ec3_Vf/Z-A+P1R5(dcRX:D&c",
				"V.bG/J|EVALcV#d.c.l6D@;b8ct54.|&3c<7z.RUfkqVcK-*TZ|G3d^>FEVRkVc.bLyXKcjt5d.R>#cN@bNtGVYJFVy*EtV.|5FBc1j2U49RVVR7b@Rf*XCD7*REt*V*b@N3*b5Vk&%*TCKRT7bAz/:N+-*|JRL3SROZ|5FBc-c|Sb*zZ(D;9Kb:HbVYJMccJ|+^X.4jJAV@bRHYJb)F1+BCJbc%VRC9|GJbDHERL76p<3Vc.MccJlJ.bb..Jbb@..cc.#H:V.f>JVA/7RCDk*-*XVAjVyz.4*.&VJ||.A_R83VfG;@A>:Ok(_@NVc3_VA/J*qB(S|5O7cR>#dNc",
				"LyCL1>*pXzZ297|ClAXkjLHc;O%|pLzZKf7Tk#7t(PF##PP1yX@EW:AM<U#+U++U++UF4#Mq#.E8O|j1+jU+j1+*##PP.E%;.yU:AW<Y^5l4JSJDH.E8J1E/:pWd|.#XdRWD>LD<qX1cN.EJT>JHER3JRG_DYNNcJ1;||5FBc<-Cq/:)F6&-*|JRk1->W#(+1U):;D%7MJpy*b>8X:Gy**.JYEd3_d._DHNCJ1-*|5FBc.Xcl8:/F+pX|*-pbDJ+Vc.bDXqq7E9PJHyG*_zXNC51;Tl|f#PP+9#(PH#MT.E-<Oc_KXq+#.pM1+j1/jU+jU+*9#PP^fWF.EXRNCJ1",
				"bSc-ft)ObzZzd8|CL<Kz38@164c)OCzZWAN%|lVA//YyA/MDpb.p:1+jU<LL.LS.:4;._d:(d7E:4Y3:q3:Y3:qfyy//7ptBGE:^OjG_d:(S595zHUE:5#&:4+:7YUdKDR:WtCWG(C4K>Gp52DJ@pR/XGRJWH>>&5fC||5FBc.-bq:4:2)O6*|JR*+-Tjd/F7:jp6zt^:J_EPbc:847pJPG5HERMXD.XWH>c5f-*|5FBcU6cF:1:qLOc|*-+6WXLVc.bzKq%Vpy/5@EDq5W6>c5f8Y*|ly|/FAk//_AjY.p-G>&5f-%LAGE:4|3jE3:|3:PqAd/K_d:(.pkU>c5f",
				"b>cL1%*pXzZ297|ClpbDjLYcbO%/&bkZKf9+T##t(1+##P3.EX@yW:AM<UH++++(++U+Y*Mq#.y81+j1+jU+j1+k##PP.y%;.yM:AW<Yf6l^JSJDH.y851y6:pWdT.#LdRWD%LD<q;1cO.EJTRJHER3JRG_DYONc*1L||5FBc<-cT/:8F6pL*|JR*A->U#PqdW):;D%9M_pyqb>6b:Gy*+.5Yyd3_d._DHNC71-*|5FBc.Xcl8:MF+pX|*-AbDJ+Vc.bDXqq7y*PJHyG*JzXNCJ1bTl|f#PP+t#((H#MTGy;<Oc_KXq+#<yMK+j1/jU+4U+|##PPHfWl.yXROcJ1",
				"V.|H)J|EFf5cVSz9VJbyWNVbXc35DZ|TPcO6cJ|5#q/UcK-*@kFGOD:X|EVRqVc.b+SdZcAP5cJ|X7c&NF&P*VU.bV%G41V.|5FBcYA-5cJb&VRVbNR%*@cWC*R4(*<*bN&3*R5VtTS*dckR@6Rfcyp&l-*|JRl31R4.|5FBc-c|1|*zZ38;9k|p*bVMJfcDJbB7j.zA.Y42|RGVKbt|>^^cJ|V%TRzJRGkFWHERO6ycV3VU.fUM.|JJb.AbA*b-*bccJ#*@VJ-X.VYyVbc86*-*dVf)VScJV*.TU9||.Y_RXPT-H@NYXp4qP_N&Ec3_Vf/Z-A+P1R5(qzRX:D&z",
				"LyCL1>*pXzZ297|ClAXkjLHc;O>|pLzZKf7Tk#7t(PF##PP1yX@EW:AM<U#+U++U++UF4#Mq#.E8O|j1+jU+j1+*##PP.E%;.yU:AW<Y^5l4JSJDH.E8J1E/:pWd|.#XdRWD>LD<qX1cN.EJT>JHER3JRG_DYNNcJ1;||5FBc<-Cq/:)F6&-*|JRk1->W#(+1U):;D%7MJpy*b>8X:Gy**.JYEd3_d._DHNCJ1-*|5FBc.Xcl8:/F+pX|*-pbDJ+Vc.bDXqq7E9PJHyG*_zXNC51;Tl|f#PP+9#(PH#MT.E-<Oc_KXq+#.pM1+j1/jU+jU+*9#PP^fWF.EXRNCJ1",
				"H.RN)JbE|flcV%ckzJbyWN9bXc35DZ|TPcO6cJ|5#d/Uz8-*@9RGOD:X|EVRdVc.b+SqZcAP5cJ|X7cVNR&P*VU.bV%*4(V.|5FBcYAV5cZb&VRVbNR%*CcWC*R4(*<*bN&3*R5VtTS*qcJR@6Rfcyp&^-*|JRl31R4.|5FBc-c|1|*zZ(8;9kbp*FVMJ>UD9|^7j9zA.YV2|RGV.Ft|>K+MJbc:TRz.RGJFWHER^6ycV3Vc.fUM.|.Jb..b.*bb*bccJ#*@VJ-X.VYyVbc86*-*qVf)VScJV*.TU.||JY_RXPT-H@NYXpVdP_N&Ec3_Vf/Z-A+O1R5(dcRX:D&c",
				"V.|H)JbE|flcV%zJzJVyWN9bXc35DK|TPcO6c.|5#d/Uc8-*@9VGOD:X|EVRdVc.b+SqZcAP5cJ|X7cVNR&P*VU.bV%*4(V.|5FBcYAV5cJb&VRVbNR%*CcWC*R4(*<*bN&3*R5VtTS*qcJR@6Rfcyp&+-*|JRl31R4.|5FBc-c|1|GzZ(8;9kRp*FVcJ>UD.b^7j9zAJYV2|RGVKRt|>l+MJbc:TRz.RGKFWHER36ycV3VM.fUM.|.Jb..bJ*bb-bcc9#*@VJ-X.VYytbc86*-*qVf)VSMJV*.TU.||JY_RXPT-H@NYXpVdP_N&Ec3_Vf/Z-A+O1R5(dcRX:D&c",
				"VK|*/.lEVMLcVfdJCJ|6D@;b>c)54.|&3cO7z.RUfkqccJ-*T.|G3d^>FEVRkVc.bLyXJcj(#d.R>#cN@bN)GEYJFVy*E)V.|5FBc1j2Ud9lVVR7b@Rf*XCD7*RE)*V*b@N3*b5Vk&%*XCKRT7|Az/:N+-*|JRL3SR<Z|5FBc-c|S|*zZ(D;9Kb:HbVYJAcYJF+^X.4jJPV@bR*c.Rt|1+BC.bC%VRC9b*JbDHERL76c)3Vc.PccJ|J.bb...b.JJbcc.y*:V.f>JV1/7RcDk*-*XV1jVyz.V*.&VZ||.1_R83VfG;@M>:Vk(_@NVc3_VM/J*qBzS|5O7cR>%dNc",
				"Olc)f*(+|zZ2d9|C(;3Wj-q^)f8F;@zZ2y%*|O%dTTE@YK/.XcD>:U+A4Lz:::::29<:qy:<yD>6f#1:|1:|j:<*@@K/D>@7.>:U+:G@d:Pt5F5zqG@MJ3>:U+:DE4dbRR_WcczG|Sfbp4XJ|4JH>GT5G4k2NppckfS||5FBcG-C*MU:FP;7*|JR<;-c6dK^4A_US28965NX#b8A)UD>&(G&HER/k..&Wqpc@f-*|5FBc4bc(AUM(|+L|*-;SzJ(Vc.b2S((9>OKpqX4P52)pc&f-<PP@yK/PYYK/ly:|.X3.pcJf7<|d.>:f<1:|j:(j:P#YyKYqY:|4>bDpcJf",
				"V.|H)JbE|flcV%c.zJVyWN9bXM35DK|TPcO6zJ|5#d/Uc8-*@9bGOD:X|EVRdVc.b+SqZcAP5cJ|X7cVNR&P*VU.bV%*4(V.|5FBcYAV5cJb&VRVbNR%*CcWC*R4(*<GbN&3*R5VtTS*qcJR@6Rfcyp&+-*|JRl31R4.|5FBc-V|1|*zZ(8;9kbp*FVcJ>UD.b^7j9zAJYV2|RGVKFt|>l+MJbc:TRz.RG9FWHERK6ycV3Vc.fUM.|.Jbb.bJ*bb*bccJ#*@VJ-X.VYytbc86*-*qVf)VSMJV*.TU.||9Y_RXPT-H@NYXpVdP_N&Ec3_Vf/Z-A+O1R5(dcRX:D&c",
				"VK|*/.lEVMLcVfdJCJ|6D@;b>c)54.|&34O7z.RUfkqc4J-*T.|G3d^>FEVRkVc.bLyXJcj(#d.R>#cN@bN)GEYJFVy*E)V.|5FBc1j2Ud9lVVR7b@Rf*XCD7*RE)*V*b@N3*b5Vk&%*XCKRT7|Az/:N+-*|JRL3SR<Z|5FBc-c|S|*zZ(D;9Kb:HbVYJAcYJ|B^X.4jJPV@bR*cJRt|1+BC.bC%VRC9b*JbDHERL76c)3Vc.PccJ|J.bb.b.b.JJbcc.y*:V.f>JV1/7RcDk*-*XV1jVyz.V*.&VJ||.1_R83VfG;@M>:Vk(_@NVc3_VM/J*qBzS|5O7cR>%dNc",
				"V.|H)JbERYBcV%c8VJ|yWN9|Xc35DK|TPcO6MJ|5#d/Uc8-*@KbG(D:X|EVRdVc.b+SqZcAP5cJRX7cVNR&P*1U.bV%*4(V.|5FBcYAV5cJb&VRVbNR%*CcWC*R4(*<HbN&3*R5VtTS*qcJR@6Rfcyp&+-*|JRl31R4.|5FBc-c|1|*zZ(8;9kRp*RVcJ>VDkR^7j.zA8YV2RRGD.Ft|fl+MK|z:TRz.RGJFWHERO6ycV3Vc.fUMJ|JJb..bJJb.*JccJ#*@VZ-X.VYyVbcW6*-*qVf)VScJA*ZTU.||JY_RXPT-H@NYXpVdP_N&Ec3_Vf/Z-A+P1R5(dcRX:D&c",
				"VJR*/9REbMLVVVV.c.R6D@;VUc(5c.|c3c)1c.RONkqcc9-*P)|G(d%8REVRkVc.bLyT.c6t5d9R8Scf@|f<*VYJFVyGV3V.|5FBcMj25cZRVV|AbWlN>PcD1*REt*V>lWfVCR5(k&%GXcKRT1RMz/pfL-*|JR+3#REZ|5FBc-c|#|CzZ(D:9KFp*lVcJA4YK|ByX.dq.7VWRRCd.l1R7+Ld)bc2&RC9|C.RDHERL1jct<Ec.McY.l...R+R..R...cc.S*:V.%U.VMq1RcDk*-*PVMjV^z9N>9VV.|j.7^R8t&%GT@AUpEk3_WfVk(_V7q.*q+t#|O31cR82dfc",
				"V.|H)JbE|flcV%zJzJVyWN9bXc35DK|TPcO6c.|5#d/Uc8-*@9VGOD:X|EVRdVc.b+SqZcAP5cJ|X7cVNR&P*VU.bV%*4(V.|5FBcYAV5cJb&VRVbNR%*CcWC*R4(*<*bN&3*R5VtTS*qcJR@6Rfcyp&+-*|JRl31R4.|5FBc-c|1|*zZ(8;9kRp*FVcJ>UD.b^7jKzAJYV2|RGVKRt|>l+MJbc:TRz.RGKFWHER36ycV3VM.fUM.|.Jb..bJ*bb-bcc9#*@VJ-X.VYytbc86*-*qVf)VSMJV*.TU.||JY_RXPT-H@NYXpVdP_N&Ec3_Vf/Z-A+O1R5(dcRX:D&c",
				"V.|H)JbE|flcV%zJzJVyWN9bXc35DK|TPcO6c.|5#d/Uc8-*@9VGOD:X|EVRdVc.b+SqZcAP5cJ|X7cVNR&P*VU.bV%*4(V.|5FBcYAV5cJb&VRVbNR%*CcWC*R4(*<*bN&3*R5VtTS*qcJR@6Rfcyp&+-*|JRl31R4.|5FBc-c|1|*zZ(8;9kRp*FVcJ>UD.b^7j9zAJYV2|RGVKRt|>l+MJbc:TRz.RGKFWHER36ycV3VM.fUM.|.Jb..bJ*bb-bcc9#*@VJ-X.VYytbc86*-*qVf)VSMJV*.TU.||JY_RXPT-H@NYXpVdP_N&Ec3_Vf/Z-A+O1R5(dcRX:D&c",
				"V.|H)J|EFf5cVSz9VJbyWNVbXc35cZ|TPcO6cJ|5#q/UcK-*@kFGOD:X|EVRqVc.b+SdZcAP5cJ|X7c&NF&P*VU.bV%G41V.|5FBcYA-5cJb&VRVbNR%*@cWC*R4(*<*bN&3*R5VtTS*dckR@6Rfcyp&l-*|JRl31R4.|5FBc-c|1|*zZ38;9k|p*bVMJfcDJbB7j.zA.Y42|RGVKbt|>^^cJ|V%TRzJRGkFWHERO6ycV3VU.fUM.|JJb.AbA*b-*bccJ#*@VJ-X.VYyVbc86*-*dVf)VScJV*.TU9||.Y_RXPT-H@NYXp4qP_N&Ec3_Vf/Z-A+P1R5(qzRX:D&z",
				"H.|N)JbE|flcV%ckzJbyWN9bXc35DZ|TPcO6ck|5#d/Uz8-*@9RGOD:X|EVRdVc.b+SqZcAP5cJ|X7cVNR&P*VU.bV%*4(V.|5FBcYAV5cJb&VRVbNR%*CcWC*R4(*<*bN&3*R5VtTS*qcJR@6Rfcyp&^-*|JRl31R4.|5FBc-c|1|*zZ(8;9kbp*FVMJ>UD9|^7j9zA.YV2|RGV.Ft|>K+MJbc:TRz.RGJFWHER^6ycV3Vc.fUM.|.Jb..b.*bb*bccJ#*@VJ-X.VYyVbc86*-*qVf)VScJV*.TU.||JY_RXPT-H@NYXpVdP_N&Ec3_Vf/Z-A+O1R5(dcRX:D&c",
				"VJR*/9REbMLVVVV.c.R6D@;VUc(5c.|c3c)1c.RONkqcc9-*P)|G(d%8REVRkVc.bLyT.c6t5d9R8Scf@|f<*VYJFVyGV3V.|5FBcMj25cZRVV|AbWlN>PcD1*REt*V>lWfVCR5(k&%GXcKRT1RMz/pfL-*|JRB3#REZ|5FBc-c|#|CzZ(D:9KFp*lVcJA4YK|ByX.dq.7VWRRCd.l1R7+Ld)bc2&RC9|C.RDHERL1jct<Ec.McY.l...R+R..R...cc.S*:V.%U.VMq1RcDk*-*PVMjV^z9N>9VV.|j.7^R8t&%GT@AUpEk3_WfVk(_V7q.*q+t#|O31cR82dfc",
				"VK|*/.lEVMLcVfdJCJ|6D@;b>c)54.|&3cO7z.RUfkqccJ-*T.|G3d^>FEVRkVc.bLyXJcj(#d.R>#cN@bN)GEYJFVy*E)V.|5FBc1j2Ud9lVVR7b@Rf*XCD7*RE)*V*b@N3*b5Vk&%*XCKRT7|Az/:N+-*|JRL3SR<Z|5FBc-c|S|*zZ(D;9Kb:HbVYJAcYJ|+^X.4jZPV@bR*c.Rt|1+BC.bC%VRC9b*JbDHERL76c)3Vc.PccJ|J.bb...b.JJbcc.y*:V.f>JV1/7RcDk*-*XV1jVyz.V*.&VJ||.1_R83VfG;@M>:Vk(_@NVc3_VM/J*qBzS|5O7cR>%dNc",
				"VK|*/.lEVMLcVfdJCJ|6D@;b>c)54.|&34O7z.RUfkqccJ-*T.|G3d^>FEVRkVc.bLyXJcj(#d.R>#cN@bN)GEYJFVy*E)V.|5FBc1j2Ud9lVVR7b@Rf*XCD7*RE)*V*b@N3*b5Vk&%*XCKRT7|Az/:N+-*|JRL3SR<Z|5FBc-c|S|*zZ(D;9Kb:HbVYJAcYJ|B^X.4jJPV@bR*cJRt|1+BC.bC%VRC9b*JbDHERL76c)3Vc.PccJ|J.bJ.b.b.JJbcc.y*:V.f>JV1/7RcDk*-*XV1jVyz.V*.&VJ||.1_R83VfG;@M>:Vk(_@NVc3_VM/J*qBzS|5O7cR>%dNc",
				"VK|*/.lEVMLcVfdJCJ|6D@;b>c)54.|&34O7z.RUfkqccJ-*T.|G3d^>FEVRkVc.bLyXJcj(#d.R>#cN@bN)GEYJFVy*E)V.|5FBc1j2Ud9lVVR7b@Rf*XCD7*RE)*V*b@N3*b5Vk&%*XCKRT7|Az/:N+-*|JRL3SR<Z|5FBc-c|S|*zZ(D;9Kb:HbVYJAcYJ|B^X.4jJPV@bR*cJRt|1+BC.bC%VRC9b*JbDHERL76c)3Vc.PccJ|J.bb.b.b.JJbcc.y*:V.f>JV1/7RcDk*-*XV1jVyz.V*.&VJ||.1_R83VfG;@M>:Vk(_@NVc3_VM/J*qBzS|5O7cR>%dNc",
				"H.RN)JbE|flcV%ckzJbyWN9bXc35DZ|TPcO6cJ|5#d/Uz8-*@9RGOD:X|EVRdVc.b+SqZcAP5cJ|X7cVNR&P*VU.bV%*4(V.|5FBcYAV5cJb&VRVbNR%*CcWC*R4(*<*bN&3*R5VtTS*qcJR@6Rfcyp&^-*|JRl31R4.|5FBc-c|1|*zZ(8;9kbp*FVMJ>UD9|^7j9zA.YV2|RGV.Ft|>K+MJbc:TRz.RGJFWHER^6ycV3Vc.fUM.|.Jb..b.*bb*bccJ#*@VJ-X.VYyVbc86*-*qVf)VScJV*.TU.||JY_RXPT-H@NYXpVdP_N&Ec3_Vf/Z-A+O1R5(dcRX:D&c",
				"HNcLf*|;-zZ2d9|CF+-zjcqc)f8P;HWZzY%(|Y9@/N*@@//GC7.>:U+:4p::::OO::O:l@:^@DC:f^jM|1_|j:p|@YK/y>@7.O:U+:G@d:Pt53J2qy@MJfC_U+:DE4YbRR62@7WG|Sfb<4X5|4JtXGT5G432N<<cJf5||5FBcG-c#_UM^P;c*|JRF;-c:YK*4M6USz8963lEpb@:)UD>3pG&HER/5..&Wq<cJf-*|5FBc4bc(AU:F|+L|*-;S2J(Vc.b2S(P%XYKFqC4P5z)<c&f^zP^@YK/P@YK/qd:|.Xk.<cJf7p|d.C:f^1M|jM|j:P#YyKYlY:|4>bD<cJf",
				"VK|*/.lEVMLcVfdJCJ|6D@;b>c)54.|&3cO7z.RUfkqccJ-*T.|G3d^>FEVRkVc.bLyXJcj(#d.R>%cN@bN)GEYJFVy*E)V.|5FBc1j2Ud9lVVR7b@Rf*XCD7*RE)*V*b@N3*b5Vk&%*XCKRT7|Az/:N+-*|JRL3SR<Z|5FBc-c|S|*zZ(D;9Kb:HbVYJAcYJ|+^X.4jJPV@bR*cJRt|1+BC.bC%VRC9b*JbDHERL76c)3Vc.PccJ|J.bb...b.JJbcc.y*:V.f>JV1/7RcDk*-*XV1jVyz.V*.&VJ||.1_R83VfG;@M>:Vk(_@NVc3_VM/J*qBzS|5O7cR>%dNc",
				"VK|*/.lEVMLcVfdJCJ|6D@;b>c)54.|&3cO7z.RUfkqccJ-*T.|G3d^>FEVRkVc.bLyXJcj(#d.R>#cN@bN)GEYJFVy*E)V.|5FBc1j2Ud9lVVR7b@Rf*XCD7*RE)*V*b@N3*b5Vk&%*XCKRT7|Az/:N+-*|JRL3SR<Z|5FBc-c|S|*zZ(D;9Kb:HbVYJAcYJ|+^X.4jJPV@bR*c.Rt|1+BC.bC%VRC9b*JbDHERL76c)3Vc.PccJ|J.bb...b.JJbcc.y*:V.f>JV1/7RcDk*-*XV1jVyz.V*.&VJ||.1_R83VfG;@M>:Vk(_@NVc3_VM/J*qBzS|5O7cR>%dNc",
				"V)|*/.RElM+%VVV.c.R6D@;VUc(5c.|c3cV1c.RON1qcC)-*PJ|G(d^8REV|kVc.bLy:.c6t5d9R8Scf@|f<HVYJFVyGV3V.|5FBcMj25cZRVV|1bWlN>PcD1*REt*V>lWfVHRO(k&%GXcKRTkRMz/pfL-*|JRB3#REZ|5FBc-c|#RCzZ(D:9KFp*RVcJA4YKRByX.cq.7VWRROdKlkRM+LcJbc2&RC9|*.RDHERL1jct<Ec.McY.l...&.R.RR...cc.S*:V.%UJVMqkRcDk*-HPVMjVyz9N>9VV.|j.7^R8t&%GT@7UpE13_WfVk(_V7q.*q+t#RO31cR82dfc",
				"HNcLf*|;-zZ2d9|CF+-zjcqc)f8P;HWZzY%(|Y9@/K*@@//GO7.>:U+:4p::::OO::O:l@:^@DC:f^jM|1_|j:p|@YK/y>@7.O:U+:G@d:Pt53J2qy@MJfC_U+:DE4YbRR62@7WG|Sfb<4X5|4JtXGT5G432N<<cJf5||5FBcG-c#_UM^P;c*|JRF;-c:YK*4M6USz8963lEpb@:)UD>3^G&HER/5..&Wq<cJf-*|5FBc4bc(AU:F|+L|*-;S2J(Vc.b2S(P%XYKFqC4P5z)<c&f^zP^@YK/P@YK/qd:|.Xk.<cJf7p|d.C:f^1M|jM|j:P#YyKYlY:|4>bD<cJf",
				"V.|H)JbE|flcV%c.zJVyWN9|Xc35DK|TPcO6z.|5#d/Uc8-*@9bGOD:X|EVRdVc.b+SqZcAP5cJ|X7cVNR&P*VU.bV%*4(V.|5FBcYAV5cJb&VRVbNR%*CcWC*R4(*<*bN&3*R5VtTS*qcJR@6Rfcyp&+-*|JRl31R4.|5FBc-c|1|*zZ(8;9kbp*FVcJ>UD.b^7j9zAJYV2|RGVKFt|>l+MJbc:TRz.RGKFWHERK6ycV3Vc.fUM.|.Jb..bJ*bb%b^cJ#*@VJ-X.VYytbc86*-*qVf)VSMJV*.TU.||JY_RXPT-H@NYXpVdP_N&Ec3_Vf/Z-A+O1R5(dcRX:D&c",
				"V.|H)JbE|flcV%cZzJbyWN9bXc35DZ|TPcO6cJ|5#d/UV8-*@JRGOD:X|EVRdVc.b+SqZcAP5cJ|X7cVNR&P*VU.bV%*4(V.|5FBcYAV5cJb&VRVbNR%*CcWC*R4(*<*bN&3*R5VtTS*qcJR@6Rfcyp&^-*|JRl31R4.|5FBc-c|1|*zZ(8;9kbp*FVMJfUD.|^7j9zAJYV2|RGV.Ft/>K+MJbc:TRz.RGJFWHER^6ycV3Vz.fUM.|.Jf..M.*bb*bccJ#*@VJ-X.VYyVbc86*-*qVf)VScJV*.TU.||JY_RXPT-H@NYXpVdP_N&Ec3_Vf/Z-A+O1R5(dcRX:D&c",
				"V.FGA.LkRfdcVTc.cZRAW2yR<c35cV|SPc(CV.V5M6)%j.-*XVbG1Dl<bEVbqVc.b+TU.%@P5D.R<^c&NR&P*VjKLV#*E7V.|5FBc>)M5z.L&VR6b2R:*Oj8q*Rk(*7HbN&7Gb52tST*U%ZR;CRfc@1&+-*|JRB3VRVK|5FBc-c|VRHzZ(8;9KR1*RVVJYcc.FB:UKD/.>9NRR*c.VtRp++c.Vc#SRcZR*KRWHERdt/cPP^c.fD%.R....k&D.d...)c.-*XVZ-<.Vp/ObcW6*-*XVf)VTcJ-*.Sj.|).Y_R<PS#G;2Y<1Vq7_2&^C3_V>@ZlA+P4R5(qcR<:z&z",
				"V)|*/.RElM+%VVV.c.R6D@;VUc(5c.|c3cV1c.RON1qcC)-*PJ|G(d^8REV|kVc.bLy:.c6t5d9R8Scf@|f<HVYJFVyGV3V.|5FBcMj25cZRVV|1bWlN>PcD1*REt*V>lWfVHRO(k&%GXcKRTkRMz/pfL-*|JRB3#REZ|5FBc-c|#RCzZ(D:9KFp*RVcJA4YKRByX.cq.7VWRROd.lkRM+LcJbc2&RC9|*.RDHERL1jct<Ec.McY.l...&.R.RR...cc.S*:V.%UJVMqkRcDk*-HPVMjVyz9N>9VV.|j.7^R8t&%GT@7UpE13_WfVk(_V7q.*q+t#RO31cR82dfc",
				"V.|H)JbE|flcV%c.zJbyWN9|Xc35DZ|TPMO6MJ|5#d/UV8-*@JRGOD:X|EVRdVc.b+SqZcAP5cJ|X7cVNR&P*VU.bV%*4(V.|5FBcYAV5cJb&VRVbNR%*CcWC*R4(*<*bN&3*R5VtTS*qcJR@6Rfcyp&^-*|JRl31R4.|5FBc-c|1|*zZ(8;9kbp*FVMJ>UD.|^7j9zAJYV2|RGV.Ft/>K+MJbc:TRz.RGJFWHER^6ycV3Vz.fUM.|.Jb..b.2bb*bccJ#*@VJ-X.VYyVbc86*-*qVf)VScJV*.TU.||JY_RXPT-H@NYXpVdP_N&Ec3_Vf/Z-A+O1R5(dcRX:D&c",
				"C#cA7@/YlzZ6d9|kO+NzPKSANf@*+kWZz<9*1y9<4Dj<MTD.8ltX_q+:R%%:z:2#Rz:6S<5|dG>:f4C:1P:|P:O1y<DT&X@bLX_qY:G#d:jHJF)zS&l2JK>5qU_Gj.<bRR_z@-W.jbfL|.8)/.JHX.t)GR5zHppA)fN||5FBc.-cE:q:/49L*|JRF^-T5<t*.2:q(639_)SXEb@2KqG>)|&5HERD5..)W#|A)f-*|5FBcGKc*:q:E4Yc|*-+(6)4Vc.bzNEj9>d|)H>./)zKpAJf(411ddtT4<<|tSM21.>8G;A)f(/EdR8:f4P:*P:|C:j%yyttHM_j.>N.;A)7",
				"byC;1>*pbzZ297|ClAXkjLHc;O>*pXzZKt9TFt7t(P|#9P(1EXkyW:AM<bU+Ub+U+b4U4#Mq#.E81+j1+jU+j1+*##PP.E%;.y8:AW<Y^Ml^JSJDH.E851E/:AWd|.#XpRWD>LD<qX1cN.EJTRJHER3JRG_DYNNCJ1-||5FBc<-cq/:)*6&b*|JR|1-%Wf(+1U8:;D>7M_py*b%WX:Gy6F.5YEd3_d._DHNcJ1-*|5FBc.Xcl8:/F|AX|*-AbDJ+Vc.bDXqq7EtPJHpG*_2XNCJ1;Tl|f#PPF9#(PH#MTYy-<Nc_OXq+#.1M1+j1LjU|jU+*##PPY>8F.EXRNCJ1",
				"V.R*/.REbM+VVVVJc.R6D@;VUc(5c.|c3cVkc9RONkqcd)-*P)|G(d^8REVRkVc.bLy:.c6t5d9R8Scf@|f<HVYJFVyGV3V.|5FBcMj25cZ|VV|AbWlN>PcD1*REt*V>lWf(CRO(k&%GXcKRT1RAz/pfL-*|JRB3#REZ|5FBc-c|#RCzZ(D:9KFp*RVcJA4YK|ByX9cjJ7VWRR*d.l1|M+LcJbc2&RC9|*.RDHERL16ct<Ec.McY.l...R.R..R.R.cc.S*:V.%U.VMq1RcDk*-HPVMjV^z9N>9VVj|/.A^R8t&%GT@AUpEk3_WfVk(_V76.*q+t#|O31cR82dfc",
				"V.F*A.LkRfdcVTc.c.RAW2yR<c35cV|SPc4CV.V5M6)%j.-*XVbG1Dl<bEVbqVc.b+TU.%@P5D.R<^c&NR&P*VjKLV#*E7V.|5FBc>)M5z.L&VR6b2R:*Oj8q*Rk(*7HbN&7Gb52tST*U%ZR;CRfc@1&+-*|JRB3VRVK|5FBc-c|VRHzZ(8;9KR1*RVVJYcc.FB:U.D/.>9NRR*c.VtRp++c.Vc#SRcZR*KRWHERdt/cPP^c.fD%.R....k&D.d...)c.-*XVZ-<.Vp/ObcW6*-*XVf)VTcJ-*.Sj.|).Y_R<PS#G;2Y<1Vq7_2&^C3_V>@ZlA+P4R5(qcR<:z&z",
				"V.|H)JbE|flcV%cZzJbyWN9bXc35DZ|TPcO6cJ|5#d/UV8-*@JRGOD:X|EVRdVc.b+SqZcAP5cJ|X7cVNR&P*VU.bV%*4(V.|5FBcYAV5cJb&VRVbNR%*CcWC*R4(*<*bN&3*R5VtTS*qcJR@6Rfcyp&^-*|JRl31R4.|5FBc-c|1|*zZ(8;9kbp*FVMJfUD.|^7j9zAJYV2|RGV.Ft/>K+MJbc:TRz.RGJFWHER^6ycV3Vz.fUM.|.Jf..b.*bb*bccJ#*@VJ-X.VYyVbc86*-*qVf)VScJV*.TU.||JY_RXPT-H@NYXpVdP_N&Ec3_Vf/Z-A+O1R5(dcRX:D&c",
				"byC;1>+pbzZ297|ClAXkjLHc;O>*pXzZKt9TTt7t(P|#9P(1EXkyW:AM<bU+Ub+U+b4U4#Mq#.E81+j1+jU+j1+*##PP.E%;.y8:AW<Y^Ml^JSJDH.E851E/:AWd|.#XpRWD>LD<qX1cN.EJTRJHER3JRG_DYNNCJ1-||5FBc<-cq/:)*6&b*|JR|1-%Wf(+1U8:;D>7M_py*b%WX:Gy6F.5YEd3_d._DHNcJ1-*|5FBc.Xcl8:/F|AX|*-AbDJ+Vc.bDXqq7EtPJHpG*_2XNCJ1;Tl|f#PPF9#(PH#MTYy-<Nc_OXq+#.EM1+j1LjU|jU+*##PPY>8F.EXKNCJ1",
				"V.BN)JbE|flcV%c.MJbyWN9bXc35DZ|TPcO6cJ|5#d/Uc8-*@9RGOD:X|EVRdVc.b+SqZcAP5cJ|X7cVNR&P*VU.bV%*4(V.|5FBcYAV5cJb&VRVbNR%*CcWC*R4(*<*bN&3*R5VtTS*qcJR@6Rfcyp&^-*|JRl31b4.|5FBc-c|1|*zZ(8;9kbp*FVMJ>UD.|^7j9zAJYV2|RGV.Ft|>K+Mkbc:TRz.RGJFWHER^6ycV3Vc.fUM.|.Jb..b.*bb*bccJ#*@VJ-X.VYyVbc86*-*qVf)VScJV*.TU.||JY_RXPT-H@NYXpVdP_N&Ec3_Vf/Z-A+O1R5(dcRX:D&c",
				"b>cL1%*pXzZ297|ClpbDjLYcbO%/&bkZKf9+T##t(1+##P3.EX@yW:AM<UH++++(++U+Y*Mq#.y81+j1+jU+j1+k##PP.y%;.yM:AW<Yf6l^JSJDH.y851y6:pWdT.#LdRW2%LD<q;1cO.EJTRJHER3JRG_DYONc*1L||5FBc<-cT/:8F6pL*|JR+A->U#PqdW):;D%9M_pyqb>6b:Gy*+.5Yyd3_d._DHNC71-*|5FBc.Xcl8:MF+pX|*-AbDJ+Vc.bDXqq7y*PJHyG*JzXNCJ1bTl|f#PP+t#((H#MTGy;<Oc_KXq+#<yMK+j1+jU+4U+|##PPHfWl.yXROcJ1",
				";S7-ftLObzZzd8|C*+Kz6c_4-@TtObWZzyNY9k^k//qyk//D&;Up:1+jU....A:.:A.:@d:Ld9E:%P6:|6A|3:qFky//7)T;Gp:4OjG_d:L_5CXz@UpAXf):1<:UqUd;%R:WTbWG(bfl>Rp52DJSpR/59RJWS>>cX4M||5FBc.-cq:4j&POK*|JRT<-4:k/F.Aj1l1t^j5#)Lbc:84Dp52GXHER/J7.XW#>c5f-*|5FBcUCcF:4:qLKl|*-+lWJLVc.bW-qYNpy/X@EDq5zM>c5flP|2kk//&kk//_k:P.E-G>cXf6|_kGpj%|3:|3:|3:PPkk//4d:L.pkU>c5f",
				"7lc3f*);|zZ2d9|C|+-2j-qOSfc(+)2Z2y%P|@^@TK(@YK/GXc.>:U+:4<::>T++O:O<lY:<YD>:f#1:|1:(j:<|@YK/D>@7.>:U+MG@d:Pt5F52qy>MJf>:U+:DE4dbRR_Wc#zG|Sfbp4XJ#4JH@GT5G4k2NppckfS||5FBcG-cF_UM(P+7*|JR|+-c6YNP4A:U-z@9:&@X#b8M-UD>k<G5HERN5..&WqpcJf-*|5FBc4bc(AU_<|+L|*-;SzJ(Vc.b2S((9>dKpqX4P5z)pc&f-FEF@YK/P@YK/l@:|.X3.pOJf7|Fd.>:f<c:|j:|j:P#YyKYqY:|4>b.pcJf",
				"Xyc;1>_&XzZ297|ClAXkjLHc;O>+p;zZk#9TT#7tPPf##PP1yX@yW:AM<U/+U++U++Z+4#Mq#.E81+j3+jU+j1+*##PP.E%;.y8:AWdY^Ml^JSJDH.E85OE/:AWd|.#XpRWD>LD<qX1cN.EJTRJHER3JRG_DYNNCJ1-||5FBc<-cq/:)*6&;*|JRFp-%Wf(l1U8:;D>7MJpy6b%1X:Gy_+.5YEd3_d._DHNCJ1-*|5FBc.Xcl8:/F+pX|*-AbDJ+Vc.bKXqq7EtPJHpG|_2XNCJ1;Tl|##PP+9#(PH#MTYE-<Nc_KXq+#.EM1+j1FjU+j/E*##PPY>8F.EXRNcJ1",
				";S7-ftLObzZzd8|C*+Kz6c_4-@TtObWZzkNY9k^k//qyk//D&;.p:4+jU....A:.:A.:@d:Ld9E:3P6:|6A|3:qFky//7)T;Gp:4OjG_d:L_5CXz@UpAXf):1<:UqUd;%R:WTbWG(bfl>Rp52DJSpR/59RJWS>>cX4M||5FBc.-cq:4j&POb*|JRT<-4:k/F.Aj1lzt^j5#)Pbc:M4Dp52GXHER/J7.XW#>c5f-*|5FBcUCcF:4:qLKl|*-+lWJLVc.bW-qYNpy/X@E*q5zM>c5flP|2kk//&kk//_k:P.E-G>cXf-|_kGpj%|3:|3:|3:PPkk//4d:L.pkU>ccf",
				";S7-ftLObzZzd8|C*+Kz6c_4-@TtObWZzkNY9k^k//qyk//D&;.p:4+jU....A:.:A.:@d:Ld9E:3P6:|6A|3:qFky//7)T;Gp:4OjG_d:L_5CXz@UpAXf):1<:UqUd;%R:WTbWG(bfl>Rp52DJSpR/59RJWS>>cX4M||5FBc.-cq:4j&POb*|JRT<-4:k/F.Aj1lzt^j5#)Lbc:M4Dp52GXHER/J7.XW#>c5f-*|5FBcUCcF:4:qLKl|*-+lWJLVc.bW-qYNpy/X@E*q5zM>c5flP|2kk//&kk//_k:P.E-G>cXf-|_kGpj%|3:|3:|3:PPkk//4d:L.pkU>ccf",
				"V9FH/.LkRpdVVTcJC.RAW2yR<c35cV|SPc4Cj.V5M6)%%.-*XVbG1Dl<bEVbqVc.b+TU.%@P5D.R<Tc&NR&P*VjKLV#*E7V.|5FBcf)M5z.L&VRVb2R:*Oj8q*Rk(*PHbN&7Gb5VtST*U%ZR;CRfc@1&+-*|JRB34RVK|5FBc-c|VRHzZ(8;9KR1*RVVJYcc.FB:U.D).>9NRRGc.VtRfd+c.Rc#SRcZR*KRWHERdt/jPP^c.fD%.R.#._..V.....cc.-*XVZ-<.Vf/VbcW6*-*XVf)VTcJ#*.Sj.|).Y_R<PS#G;2Y<1Vq7_2&kc3_Vp@ZlA+P4R5(6cR<:z&z",
				"7lc3f*);|zZ2d9|CF+-Wj-qcSf8(+)2ZWd%P|Y^yKN<@YK/GX).>:U+:4<:O::::OGOOlY:<YD>:f#O:|1:(1:<|@YK/D>@7.>:U+_G@d:Pt5F52qy@MJf>:U+MDE4YbRR_WcczG|Sfbp4XJ#4JH@GT5G4k2NppckfS||5FBcG-cF_U6(Pc7*|JR|+-c6YNP4A:U)z@9_&@X#b8MSUD>k<.&HER/5..&WqpcJf-*|5FBc4bc(AU_<|+L|*-;SzJ(Vc.b2S((9>dKpqX4P5z)pc&f-F^F@YK/P@YK/l@:|.X3.pcJf7<Fd.>:f<1:|j:|j:P#YyKYqY:|4>b.pcJf",
				"C#cA7@/YlzZ6d9|kO+N6PKSALf@*+kWZz<9*1y9MtDj<MTD.8ltX_q+:R%%:z:2#Rz:6S<5|dG>:f4C:1P:|P:O1y<DT&X@bLX_qY:G#d:jHJF)zS&l2JK>5qU_Gj.<bRR_z@-W.jbfL|.8)/.JHX.t)GR5zHppA)fN||5FBc.-cE:q:/4UL*|JRF^-T5<t*.2:q(639_)SXEb@2KqG>)|&5HERD5..)W#|A)f-*|5FBcGKc*:q:E4Yc|*-+(6)4Vc.bzNEj9>d|)H>./)zKpAJf(411ddtT4<<|tSM21.>8G;A)f(/EdR8:f4P:*P:|C:j%yyttHM_j.>N.;A)7",
				"V.|H)JbE|flcV%cZzJbyWN9bXc35DZ|TPcO6cJ|5#d/UV8-*@JRGOD:X|EVRdVc.b+SqZcAP5cJ|X7cVNR&P*VU.bV%*4(V.|5FBcYAV5cJb&VRVbNR%*CcWC*R4(*<*bN&3*R5VtTS*qcJR@6Rfcyp&^-*|JRl31R4.|5FBc-c|1|*zZ(8;9kbp*FVMJ>UD.|^7j9zAJYV2|RGV.Ft/>K+MJbc:TRz.RGJFWHER^6ycV3Vz.fUM.|.Jb..b.*bb*bccJ#*@VJ-X.VYyVbc86*-*qVf)VScJV*.TU.||JY_RXPT-H@NYXpVdP_N&Ec3_Vf/Z-/+O1R5(dcRX:D&c",
				"-yC;1>+pXzZ297|ClAXkjLHc;O%*pXzZKt9T*#7t(P|#9P(pEXkyW:AM<UU+Ub+U+b4U4#Mq#.E81+j1+jU+j16*##PP.E%;.y8:AW<^^Ml^JSJDH.E851E/:AWd|.#XpRWD>LD<qX1cN.EJTRJHER3JRG_DYNNCJ1-||5FBc<-cq/:)*6&b*|JR*1-%8f(+1U8:;D%7MJpy*b%WX:Gy6F.5YEd3_d._DHNCJ1-*|5FBc.Xcl8:/F|pX|*-AbDJ+Vc.bDXqq7EtPJHpG*_2XNCJ1;Tl|f#PPF9#(PH#MTYy-<Nc_OXq+#.EM1+j1LjU|KU+*##PPY>8F.EXRNCJ1",
				";S7-ftLObzZzd8|C*+Kz6c_4-@T*ObWZzyNY9k^k//qyk//D&;Up:4+jU....A:.:A.:@d:Ld9E:%P6:|6A|3:qFky//7)T;Gp:4OjG_d:L_5CXz@UpAXf):1<:UqUd;%R:WTbWG(bfl>Rp52DJSpR/59RJWS>>cX4M||5FBc.-cq:4j&POK*|JRT<-4:k/F.Aj1l1t^j5#)Lbc:84Dp52GXHER/J7.XW#>c5f-*|5FBcUCcF:4:qLKl|*-+lWJLVc.bW-qYNpy/X@EDq5zM>c5flP|2kk//&kk//_k:P.E-G>cXf6|_kGpj%|3:|3:|3:PPkk//4d:L.pkU>c5f",
				"V.|H)JbE|flcV%c.zJbyWN9|Xc35DZ|TPcO6cJ|5#d/UV8-*@JRGOD:X|EVRdVc.b+SqZcAP5cJ|X7cVNR&P*VU.bV%*4(V.|5FBcYAV5cJb&VRVbNR%*qcWC*R4(*<*bN&3*R5VtTS*qcJR@6Rfcyp&^-*|JRl31R4.|5FBc-c|1|*zZ(8;9kbp*FVMJ>UD.|^7j9zAJYV2|RGV.Ft/>K+MJbc:TRz.RGJFWHER^6ycV3Vz.fUM.|.Jb..b.2bb*bccJ#*@VJ-X.VYyVbc86*-*qVf)VScJV*.TU.||JY_RXPT-H@NYXpVdP_N&Ec3_Vf/Z-A+O1R5(dcRX:D&c",
				"V.R*/.REbM+VVVVJc.R6D@;VUc(5c.|c3cV1c9RONkqcc)-*P)|G(d^8REVRkVc.bLy:.c6t5d9R8Scf@|f<HVYJFVyGV3V.|5FBcMj25cZRVV|AbWlN>PcD1*REt*V>lWf(CRO(kV%GXcKRT1RMz/pfL-*|JRB3#REZ|5FBc-c|#RCzZ(D:9KFp*RVcJA4Y9|ByXJcjJ7VWRR*d.l1|M+LcJbc2&RC9|*.RDHERL16ct<Ec.McY.l...R.R..R.R.cc.S*:V.%U.VMq1RcDk*-HPVMjV^z9N>9VcJ|/.7^R8t&%GT@AUpEk3_WfVk(_V76.*q+t#|O31cR82dfc",
				"V.|*/.|EbM+VVVV.c.R6DW;VUc(5cZ|c3cV1c.RONkqcc)-*P)|G(d^8REVRkVc.bLy:.c6t5d9R8Scf@|f<HVYJFVyGV3V.|5FBcMj25cZRVV|AbWlN>PcD1*REt*V>lWfVCRO(k&%GXcKRT1RMz/pfL-*|JRB3#REZ|5FBc-c|#RCzZ(D:9KFp*RVcJA4YKRByX.cq.7VWRR*d.l1F7+LYJbc2&RC9|*.RDHERL1jct<Ec.McY.l...R.R..R...cc.S*:V.%U.VMq1RcDk*-HPVMjV^z9y>9VV.|j.7^R8t&%GT@AUpEk3_WfVk(_V7q.*q+t#|O31cR82dfc",
				"7@c3f*O;|zZ2d^|C|+-Wj-qcSf8(+)2Zzy%P|d^@/N|YYT/GXc.>:U+:4<:9:O+:O(:<lY:#YD>:f#jM|1:|j:<|@YK/D>@7.>_U+:G@d:Pt5F52qy@:Jf>:U+:DE4YbRR_WcczG|Sfbp4XJ#4JH@GT5G4k2NppckfS||5FBcG-cF:UA(P;L*|JR|+-@6yNPDM:U-z89M5@X#b8M-UH>k|G&HER/5..&WqpcJf-*|5FBc4bc(AU:F|+L|*-;SzJ(Vc.b2S((9>YKpqX4P5z)pc&f-F^F@YK/P@YK/ly:|.X3.pcJf7<Fd.>:f<1:|j:|j:(#Y@KYqY:|4>b.pcJf",
				"V.|*/.RElM+%VVV.c.R6D@;VUc(5c.|c3cV1c.RON1qcc)-*PJ|G(d^8REV|kVc.bLy:.c6t5d9R8Scf@|f<HVYJFVyGV3V.|5FBcMj25cZRVV|1bWlN>PcD1*REt*V>lWfVCRO(k&%GXcKRTkRMz/pfL-*|JRB3#REZ|5FBc-c|#RCzZ(D:9KFp*RVcJA4YZRByX.cq.7VWRROd.lkRM+LcJbc2&RC9|*.RDHERL1jct<Ec.McY.l...&.R.RR...cc.S*:V.%U.VMqkRcDk*-HPVMjVyz9N>9VV.|j.7^R8t&%GT@7UpE13_WfVk(_V7q.*q+t#RO31cR82dfc",
				"-yC;1>+pbzZ297|ClA;kjLHc;O>FpXzZKt9TTt7tPP|#9P(1EXkyW:AM<UU+Ub+U+U4U4#lq#.E81+j1+jU+j1+*##PP.E%;.y8:AW<Y^Ml^JSJDH.E851E/:AWd|.#XpRWD>LD<qX1cN.EJTRJHER3JRG_DYNNCJ1-||5FBc<-cq/:)F6&b*|JR|1-%Wf3+1U8:;D>7M_py*b%WX:Gy6F.5YEd3_d._DHNcJ1-*|5FBc.Xcl8:/F|pX|*-AbDJ+Vc.bDXqq7EtPJHpG*_2XNCJ1;Tl|f#PPF9#(PH#MTYy-<Nc_OXq+#.EM1+j1LjU|jU+*##PPY>8F.EXRNCJ1",
				"V.BN)JbE|flcV%c.MJbyWN9bXc35DZ|TPcO6cJ|5#d/Uc8-*@9RGOD:X|EVRdVc.b+SqZcAP5cJ|X7cVNR&P*VU.bV%*4(V.|5FBcYAV5cJb&VRVbNR%*CcWC*R4(*<*bN&3*R5VtTS*qcJR@6Rfcyp&^-*|JRl31b4.|5FBc-c|1|*zZ(8;9kbp*FVMJ>UD.|^7j9zAJYV2|RGV.Ft|>K+M.bc:TRz.RGJFWHER^6ycV3Vc.fUM.|.Jb..b.*bb*bccJ#*@VJ-X.VYyVbc86*-*qVf)VScJV*.TU.||JY_RXPT-H@NYXpVdP_N&Ec3_Vf/Z-A+O1R5(dcRX:D&c",
				"V.R*/.|E|MLVVVc.c.|6DW;VUct5V.|c3cVkc.bO-kqcc9-*P.FG(d^UREVRkVc.bLy:9c6t5c9R8ScNW|N<HVYJFVyGV3V.|5FBcMj25cZRVV|AbWlf>PcD1*REt*V>l@NVCRO(k&%GXcKRTkRMz/pNL-*|JRB3#REZ|5FBc-c|#R>zZ(D:9KFp*lVcJA4YKRByX.cj)7V@RR*d.R1FM+Lc.bz2&RC9|*ZRDHERL16ct<EcJMcY.l.....R.R.R..cc.S*:V.%U.VMq1RcDk*-HPVMjVyz9f>9VVJ|/.7^b8t&%GTWMUpEk3_@NVk(_V76.*q+t#|O31cR82dNc",
				"V.|H)JbE|flcV%c.zJbyWN9bXc35DZ|TPcO6cJ|5#d/UV8-*@JRGOD:X|EVRdVc.b+SqZcAP5cJ|X7cVNR&P*VU.bV%*4(V.|5FBcYAV5cJb&VRVbNR%*CcWC*R4(*<*bN&3*R5VtTS*qcJR@6Rfcyp&^-*|JRl31R4.|5FBc-c|1|*zZ(8;9kbp*FVMJ>UD.|^7j9zAJYV2|RGV.Ft/>K+MJbc:TRz.RGJFWHER^6ycV3Vz.fUM.|.Jb..b.*bb*bccJ#*@VJ-X.VYyVbc86*-*qVf)VScJV*.TU.||JY_RXPT-H@NYXpVdP_N&Ec3_Vf/Z-A+O1R5(dcRX:D&c",
				"V.R*/.REbM+VVVV.z.R6D@;VUc(5cJ|c3cVkc.RONkqcc)-*P)|G(d^8REVRkVc.bLy:.c6t5d9R8Scf@|f<HVYJFVyGV3V.|5FBcMj25cZRVV|AbWlN>PcD1*REtCV>lWfVCRO(k&%GXcKRT1RMz/pfL-*|JRB3#REZ|5FBc-c|#RCzZ(D:9KFp*RVcJA4YKRByX.cq.7VWRR*dKl1R7+LcJRc2&RC9|*.RDHERL1jct<Ec.McY.l...R.R..R...cc.S*:V.%U.VMq1RcDk*-HPVMjV^z9y>9VV.|j.7^R8t&%GT@AUpEk3_WfVk(_V7q.*q+t#|O31cR82dfc",
				"-yC;1>+pXzZ297|ClAXkjLHc;O%*pXzZKt9T*#7t(P|#9P(pEXkyW:AM<UU+Ub+U+b4U4#Mq#.E81+j1+jU+j1+*##PP.E%;.y8:AW<^^Ml^JSJDH.E851E/:AWd|.#XpRWD>LD<qX1cN.EJTRJHER3JRG_DYNNCJ1-||5FBc<-cq/:)*6&b*|JR*1-%8f(+1U8:;D%7MJpy*b%WX:Gy6F.5YEd3_d._DHNCJ1-*|5FBc.Xcl8:/F|pX|*-AbDJ+Vc.bDXqq7EtPJHpG*_2XNCJ1;Tl|f#PPF9#(PH#MTYy-<Nc_OXq+#.EM1+j1LjU|KU+*##PPY>8F.EXRNCJ1",
				"C4*;fT&+-zZzd8|C|OMz3c@_8fcY<KWZWyNPFy^A//FAk/tDflUp:4<:U.:.:::::..:Hd:&49E:^Y3:|3:|3:qFkk//7pcBUpL4O:G@d:&@5CXz#Up:5%):4+:7qUd;DRjWTCWG(bfl>Rp5#cJ_pR/5GR5WS>>cXK8||5FBcD-cFL4:q26;*|JR2<-tjkt|9jj1MzTNLX_)Yb7:;17pX2G5HER/J9.XWH>c5%-*|5FBcUMcFj4jq&Ol|*-+MWJ&Vc.bWKqPNpy/XSEDq5Wl>c5flY|(yk/1*yd//_ALY.E-9>cXf-|*kDp:%|3:|3:|3:YYkk//HkL&.pkU>c3f",
				"V.BN)JbE|flcV%c.cJbyWN9bXc35DZ|TPcO6cJ|5#d/UD8-*@9RGOD:X|EVRdVc.b+SqZcAP5cJ|X7cVNR&P*VU.bV%*4(V.|5FBcYAV5cJb&VRVbNR%*CcWC*R4(*<*bN&3*R5VtTS*qcJR@6Rfcyp&^-*|JRl31b4.|5FBc-c|1|*zZ(8;9kbp*FVMJ>UD.|^7j9zAJYV2|RGV.Ft|>K+M.bc:TRz.RGJFWHER^6ycV3Vc.fUM.|.Jb..b.*bb*bccJ#*@VJ-X.VYyVbc86*-*qVf)VScJV*.TU.||JY_RXPT-H@NYXpVdP_N&Ec3_Vf/Z-A+O1R5(dcRX:D&c",
				"LyCL1>*pXzZ297|ClAlkjLHc;O%lpXzZKf7T*^7t((F#fPP1EX@EW:AM<+#+U++U#+UF4#Mq#.E8O|j1+jU+jU+*##PP.E%;.yU:AW<Y^5l4JSJDH.E8J1E/:pWd|.#XdRWD>LD<qX1cN.EJT>JHER3JRG_DYNNcJ1;||5FBc<-Cq/:)l6&-*|JRk1->W#(+1U):;D>7MJpE|b%8X:Gy**.JYEd3_d._DHNCJ1-*|5FBc<Xcl8:/F+&X|*-pbDJ+Vc.bDXqq7E9PJHpG*_zXNC51;Tl|f#PP+9#(PH#MT.y-<Oc_KXq+#.pM1+j1/jU+jU+*##PP^fWF.EXRNCJ1",
				"Xyc;1>_&XzZ297|ClAXkjLHc;O>+p;zZk#9TT#7tPPf##PP1yX@yW:AM<U/+U++U++Z+4#Mq#.E81+j1+jU+j1+*##PP.E%;.y8:AWdY^Ml^JSJDH.E85OE/:AWd|.#XpRWD>LD<qX1cN.EJTRJHER3JRG_DYNNCJ1-||5FBc<-cq/:)*6&;*|JRFp-%Wf(l1U8:;D>7M_py6b%1X:Gy_+.5YEd3_d._DHNCJ1-*|5FBc.Xcl8:/F+pX|*-AbDJ+Vc.bKXqq7EtPJHpG|_2XNCJ1;Tl|##PP+9#(PH#MTYE-<Nc_KXq+#.EM1+j1FjU+j/E*##PPY>8F.EXRNcJ1",
				"C4*bfT(+-zZzd8|C|OMz3c@48@cYO8WZWyNPFy^A//YAk/1DflUpj4<:U.:E:::l:..:Hd:&49E:%Y3:/3:|3:qFkk//7pcbUp:4O:G_d:&@5CXz#Up:5%):4+:7qUd;DRjWTCWG(b&l>Rp5#cJ_pR/5GR5WS>>cX^8||5FBcD-c|L4Lqq6;*|JR2<-tjy1|.::4lzTNjX_)Ybc:;17p5&G5HER/J9.XWH>c5f-*|5FBcUMcF:4jq&Ol|*-+MWJ&Vc.bWKqPNpy/X#EDq5WM>c5flY|(kk/1*kk//_ALYU)-9>cXf-|*kGp:%|3:|3:|3:YYkk//SdL&.pkU>c5f",
				"V.R*/.REbM+VVVV.z.R6D@;VUc(5c.|c3cVkc.RONkqcc)-*P)|G(d^8REVRkVc.bLy:.c6t5d9R8Scf@|f<HVYJFVyGV3V.|5FBcMj25cZRVV|AbWlN>PcD1*REtCV>lWfVCRO(k&%GXcKRT1RMz/pfL-*|JRB3#REZ|5FBc-c|#RCzZ(D:9KFp*RVcJA4YKRByX.cq.7VWRR*dKl1R7+LcJRc2&RC9|*.RDHERL1jct<Ec.McY.l...R.R..R...cc.S*:V.%U.VMq1RcDk*-HPVMjV^z9y>9VV.|j.7^R8t&%GT@AUpEk3_WfVk(_V7q.*q+t#|O31cR82dfc",
				"V.BN)JbE|flcV%c.cJbyWN9bXc35DZ|TPcO6cJ|5#d/Uc8-*@9RGOD:X|EVRdVc.b+SqZcAP5cJ|X7cVNR&P*VU.bV%*4(V.|5FBcYAV5cJb&VRVbNR%*CcWC*R4(*<*bN&3*R5VtTS*qcJR@6Rfcyp&^-*|JRl31b4.|5FBc-c|1|*zZ(8;9kbp*FVMJ>UD.|^7j9zAJYV2|RGV.Ft|>K+M.bc:TRz.RGJFWHER^6ycV3Vc.fUM.|.Jb..b.*bb*bccJ#*@VJ-X.VYyVbc86*-*qVf)VScJV*.TU.||JY_RXPT-H@NYXpVdP_N&Ec3_Vf/Z-A+O1R5(dcRX:D&c",
				"V.F*/.LkFfdcVTc.c.RAW2yR<c35cV|SPc4CV.>5M6)%j.-*XVbG1Dl<bEVbqVc.b+TU.%@P5D.R<^c&NR&P*VjKLV#*E7V.|5FBc>)M5z.L&VRVb2R:*Oj8q*Rk(*7HbN&7Gb5VtST*U%ZR;CRfc@1&+-*|JRB34RVK|5FBc-c|VLHzZ(8;9KR1HRVVJYcc.Rd:U.D/.>9NRR*c.VtRp+Bc.Rc#SRcZR*KRWHERdt/cPP^c.fD%.R....q.....l.cc.-*XVZ-<.Vp/VbcW6*-*XVf)VTcJV*.Sj.|).Y_R<PS#G;2Y<1Vq7_N&kc3_V>@ZlA+P4R5(6cR<:z&z",
				"V.|H)JbE|flcV%c.zJbAWN9bXc35DZ|TPcO6cJ|5#d/UV8-*@JRGOD:X|EVRdVc.b+SqZcAP5cJ|X7cVNR&P*VU.bV%*4(V.|5FBcYAV5cJb&VRVbNR%*CcWC*R4(*<HbN&3*R5VtTS*qcJR@6Rfcyp&^-*|JRl31R4.|5FBc-c|1|*zZ(8;9kbp*FVcJ>UD.|^7j9VAJYV2|RGV.Rt|>K+MJbc:TRz9RGJFWHER^6ycV3Vc.fUM.|.Jb..b.*bb*(ccJ#*@VJ-X.VYyVbc86*-*qVf)VScJV*.TU.||JY_RXPT-H@NYXpVdP_N&Ec3_Vf/Z-A+O1R5(dcRX:D&c",
				"-yc-1%qp-zZ297|ClAXkjLHc;O>+pXzZ2f9T*#7tPPf##PPdEX@yW:AM<UX+jjUUUUU+4#Mq#.E81+j1+jU|j1**##PP.E>;.y8:AW<Y^Ml^JSJDH.E85OE/:AWd|.#XpRWD>LD<qX1cN.EJTRJHER3JRG_DYNNCJ1-||5FBc<-cq/:)*6&;*|JRFp-%Wf(+1U8:;D>7MJ^y*b>1X:Gy*T.5YEd3_d._DHNCJ1-*|5FBc.Xcl8:/F+pX|*-AbDJ+Vc.bDXqq7EtPJHpG*_2XNc11;2l|f#PP+9#(PH#MTYy-<Oc_KXq+#.PM1+j1+jU+jU+*##PPY>WF.EXRNCJ1",
				"V.|H)JbE|flcV%c.zJbyWN9bXc35DZ|TPcO6cJ|5#d/UV8-*@JRGOD:X|EVRdVc.b+SqZcAP5cJ|X7cVNR&P*VU.bV%*4(V.|5FBcYAV5cJb&VRVbNR%*CcWC*R4(*<*bN&3*R5VtTS*qcJR@6Rfcyp&^-*|JRl31R4.|5FBc-D|1|*zZ(8;9kbp*FVcJ>UD.|^7j9VAJYV2|RGV.Ft|>K+MJbc:TRz9RGJFWHER^6ycV3Vc.fUM.|.Jb..b.*bb*bccJ#*@VJ%X.VYyVbc86*-*qVf)VScJV*.TU.||JY_RXPT-H@NYXpVdP_N&Ec3_Vf/Z-A+O1R5(dcRX:D&c",
				"V.|H).bE|flcV%c.zJ|yWN9bXc35DZ|TPcO6cJ|5#d/UV8-*@JRGOD:X|EVRdVc.b+SqZc/P5cJ|X7cVNR&P*VU.bV%*4(V.|5FBcYAV5cJb&VRVbNR%*CcWC*R4(*<*RN&3*R5VtTS*qcJR@6Rfcyp&^-*|JRl31R4.|5FBc-c|1|*zZ(8;9kbp*FVcJ>UD.|^7j9VAJYV2|RGV.Ft|>K+MJbc:TRz.RGJFWHER^6ycV3Vc.fUM.|.Jb..b.*fb*bccJ#*@VJ-X.VYyVbc86*-*qVf)VScJV*.TU.||JY_RXPT-H@NYXpVdP_N&Ec3_Vf/Z-A+O1R5(dcRX:D&c",
				"C4*;fT&+-zZzd8|C|OMz3c@48fcYOKWZWdNPFy^A1/Fdk/1DflUp:4<:U.:+:R:::..:Hd:&49E:^Y3:|3:|3:qFkk//7pcBUpL4O:G@d:&@5CXz#Up:5%):46:7qUd;DRjWTCWG(bfl>Rp5#cJ_pR/5GR5WS>>cXKc||5FBcD-cFL4:q26;*|JR2<-tjy/|9:j4MWTN:X_)Ybc:;17pX2G5HER/J9.XWH>c5%-*|5FBcUMcFj4jq&Ol|*-+MWJ&Vc.bWKqPNpy/X#EDq5WM>c5flY|(kk/1*kk//_ALY.E-9>cXf-|*kGp:%|3:|3:|3:YYkk//4dL&.pkU>c5%",
				"7lc3f*F;|zZ2d^|C|+-Wj-qcSf8(;)2Zzy%P|d^@TN<YYK/.XcG>:U+:4<::::::O::<lY:#@D>:f#jM|1:|j:<|@YK/D>@7.>:U+:G@d:Pt5F52qy@:Jf>6U+:DE4YbRR_WcczG|Sfbp4XJ#4JH@GT5G4k2NppckfS||5FBcG-cF:UA(P;L*|JR|;-@6dNPDM:U-z89A&@X#b8M-UH>k|G&HER/5..&WqpcJf-*|5FBc4bc(AU:F|+L|*-;S2J(Vc.b2S((9>YKpqX4P5z-pc&f-F^F@YK/P@YK/ly:|.X3.pcJf7<Fd.>:f<1:|j:|j:(#Y@KYqY:|4>b.pcJf",
				"V.|H)JFE|flcV%c.zJbyWN9bXc35DZ|TPcO6cJ|5#d/UV8-*@JRGOD:X|EVRdVc.b+SqZcAP5cJ|X7cVNR&P*VU.bV%*4(V.|5FBcYAV5cJb&VRVbNR%*CcWC*R4(*<*bN&3*R5VtTS*qcJR@6Rfcyp&^-*|JRl31R4.|5FBc-c|1|*zZ(8;9kbp*FVcJ>UD.|^7j9VAJYV2bRGV.Ft|>K+MJbc:TRz9RGJFWHER^6ycV3Vc.fUM.|.Jb..b.*bb*bccJ#*@VJ-X.VYyVbc86*-*qVf)VScJV*.TU.||JY_RXPT-H@NYXpVdP_N&Ec3_Vf/Z-A+O1R5(dcRX:D&c",
				"-yc-1>qpXzZ297|ClAXkjLHc;O>+pXkZ2f9T*#7t3Pf##PPdEX@yW:AM<UX+jjUUUUU+4#Mq#.E81+j1+jU+j1+*##PP.E%;.y8:AW<Y^Ml^JSJDH.E85OE/:AWd|.#XpRWD>LD<qX1cN.EJTRJHER3JRG_DYNNCJ1-||5FBc<-cq/:)*6&;*|JRFp-%Wf(+1U8:;D>7MJ^ylb>1X:Gy*T.5YEd3_d._DHNCJ1-*|5FBc.Xcl8:/F+pX|*-AbDJ+Vc.bDXqq7EtPJHpG*_zXNCJ1;Tl|f#PP+9#(PH#MTYp-<Oc_KXq+#.PM1+j14jU+jU+*##PPY>WF.EXRNCJ1",
				"7lc3f*F;|zZ2d^|C|+-Wj-qcSf8(;)2Zzy%P|d^@TN<YYK/.XcG>:U+:4<::::::O::<lY:#YD>:f#jM|1:|j:<|@YK/D>@7.>:U+:G@d:Pt5F52qy@:Jf>6U+:DE4YbRR_WcczG|Sfbp4XJ#4JH@GT5G4k2NppckfS||5FBcG-cF:UA(P;L*|JR|;-@6dNPDM:U-z89A&@X#b8M-UH>k|G&HER/5..&WqpcJf-*|5FBc4bc(AU:F|+L|*-;S2J(Vc.b2S((9>YKpqX4P5z-pc&f-F^F@YK/P@YK/ly:|.X3.pcJf7<Fd.>:f<1:|j:|j:(#Y@KYqY:|4>b.pcJf",
				"V.R*/.REbM+VVVz.c.R6D@;VUc(5c.|c3cVkc.RONk/cc)-*P)|G(d^8REVRkVc.bLy:.c6t5d9R8Scf@|f<HVYJFVyGV3V.|5FBcMj25cZRVV|AbWlN>PcD1*REt*V>lWfVCRO(k&%GXcKRT1RMz/pfL-*|JRB3#REZ|5FBc-c|#RCzZ(D:9KFp*RVcJA4YKRByX.cq.7VWRR*d.l1R7+LcJRc2&RC9|*.RDHERL1jct<Ec.McY.l...R.R..R...cc.S*:V.%U.VMq1RcDk*-HPVMjV^z9y>9VV.|j.7^R8t&%GT@AUpEk3_WfVk(_V7q.*q+t#|O31cR82dfc",
				"V.|*/.REbM+VVVV.c.R6DW;VUc(5cZ|c3cV1c.RONkqcc)-*P)RG(d^8REVRkVc.bLy:.c6t5d9R8Scf@|f<HVYJFVyGV3V.|5FBcMj25cZRVV|AbWlN>PcD1*REt*V>lWfVCRO(k&%GXcKRT1RMzjpfL-*|JRB3#REZ|5FBc-c|#RCzZ(D:9KFp*RVcJA4YKRByX.cq.7VWRR*d.l1R7+LcJbc2&RC9|*.RDHERL1jct<Ec.McY.l...R.R..R...cc.S*:V.%U.VMq1RcDk*-HPVMjV^z9y>9VV.|j.7^R8t&%GT@MUpEk3_WfVk(_V7q.*q+t#|531cR82dfc",
				";XcC4TTP3zZ297/CpPbzS;qcb4p|P-DZD97*pp7W((FlWNA.OLGOYMP>16WN56N++WNKHWY&l.%Y|*@Y&S>/SyTtWW(A4%)LGOYMPYRX@YFq5^5zX.O%54OY:PYRt@p-dRY2)3zGf84CkGO_/.JHERA5.G5DXk1cK4C||5FBcd-c96:>^&P8*|JRfP-@Y<(FG6YM;2p7y5HE|b*Y+:dUjtRJHOR)J..5zXkcKj-*|5FBc.;c/Y:>pfPC|*-P3D_/Vc.bz8tp7OWNpq#.&5D8kcK4C*|pW9((FWlAAqWytROLGkcK4-@&ld#Yj/@Y&@>*SyT@WW(AX<yTGO;.kcKj",
				"V.R*/.REbM+VVVV.c.R6D@;VUc(5c.|c3cVkc.RONkqcc)-*P)|G(d^8REVRkVc.bLy:.c6t5d9R8Scf@|f<HVYJFVyGV3V.|5FBcMj%5cZRVV|AbWlN>PcD1*REt*V>lWfVCRO(k&%GXcKRT1RMz/pfL-*|JRB3#REZ|5FBc-c|#RCzZ(D:9KFp*RVcJA4YKRByX.cq.7VWRR*d.l1R7BLcJRc2&RC9|*.RDHERL1jct<Ec.McY.l...R.R..R...cc.S*:V.%U.VMq1RcDk*-HPVMjV^z9y>9VV.|j.7^R8t&%GT@AUpEk3_WfVk(_V7q.*q+t#|O31cR82dfc",
				"-yc-1#qp-zZ297|ClAXkjLHc;O>+pXzZ2f9T*#7tPPft#PPdEX@yW:AM<UX+jjUdUUU+4#Mq#.E81+j1+jU+j1**##PP.E%;.y8:AW<Y^Ml^JSJDH.E85OE/:AWdt.#XpRWD>LD<qX1cN.EJTRJHER3JRG_DYNNCJ1-||5FBc<-cq/:)*6&;*|JRFp-%Wf(+188:;D>7MJ^y*b>1X:Gy*T.5YEd3_d._DHNCJ1-*|5FBc.Xcl8:/F+pX|*-AbDJ+Vc.bDXqq7EtPJHpG*_2XNcJ1;Tl|f#PP+9#(PH#MTYy-<Oc_KXq+#.PM1+j1+jU+jU+*##PPY>WF.EXRNCJ1",
				"*zZ38^:kCYT6+d2DKB/|H7S.&PZp|;A8K@-yOl_cN@DJcF^XPOW>*zZ;+&:k&YP>+;2P5p/dA7N.z^ZdXPSjq;W*-%z%E@OA9%^<G4.A.BZ/Jc*+-YUG6PytCPLp89AEk+&6k&%P68d2PKSO&DZ&jjEy;6#)5Bt)5Jc;7ZPy<6-LT11PT11RTT(1RG-N(|.yOlf8N8D>#Fjjq^Y*Wp.4OWK*pc92Ol4jbfZ&JD%f>OM9%G3d5UR/7ZP><R-Z&B^jjG;S+|.tOWY*-pcb.+lR*-*+D_cG|N+2.8V%J|2jkK@GFBc|.clRH|:+|5FGdJ+b.cVRJ|*p+|5FBc|HER@;",
				"V.|H)JbE|flcV%c.zJ|yWN9bXc35DZ|TPcO6cJ|5#d/UV8-*@JRGOD:X|EVRdVc.b+SqZc/P5cJ|X7cVNR&P*VU.bV%*4(V.|5FBcYAV5cJb&VRVbNR%*CcWC*R4(*<*RN&3*R5VtTS*qcJR@6Rfcyp&^-*|JRl31R4.|5FBc-c|1|*zZ(8;9kbpHFVcJ>UD.b^7j9VAJYV2|RGV.Ft|>K+MJbc:TRz.RGJFWHER^6ycV3Vc.fUM.|.Jb..b.*bb*bccJ#*@VJ-X.VYyVbc86*-*qVf)VScJV*.TU.||JY_RXPT-H@NYXpVdP_N&Ec3_Vf/Z-A+O1R5(dcRX:D&c",
				"C4*bfT(+-zZzd8|C|OMz3c@48@cYO8WZWyNPFy^A//YAk/1DflUpj4<:U.:E:::l:..:Hd:&49E:%Y3:|3:|3:qFkk//7pcbUp:4O:G_d:&@5CXz#Up:5%):4+:7qUd;DRjWTCWG(b&l>Rp5#cJ_pR/5GR5WS>>cX^8||5FBcD-c|L4Lqq6;*|JR2<-tjy1|.::4lzTNjX_)Ybc:;17p5&G5HER/J9.XWH>c5f-*|5FBcUMcF:4jq&Ol|*-+MWJ&Vc.bWKqPNpy/X#EDq5WM>c5flY|(kk/1*kk//_ALYU)-9>cXf-|*kGp:%|3:|3:|3:YYkk//SdL&.pkU>c5f",
				"V.|H)JbE|flcV%c.zJ|yWN9bXc35DZ|TPcO6cJ|5#d/UV8-*@JRGOD:X|EVRdVc.b+SqZc/P5cJ|X7cVNR&P*VU.bV%*4(V.|5FBcYAV5cJb&VRVbNR%*CcWC*R4(*<*RN&3*R5VtTS*qcJR@6Rfcyp&^-*|JRl31R4.|5FBc-c|1|*zZ(8;9kbp*FVcJ>UD.|^7j9VAJYV2|RGV.Ft|>K+MJbc:TRz.RGJFWHER^6ycV3Vc.fUM.|.Rb..b.*bb*bccJ#*@VJ-X.VYyVbc86*-*qVf)VScJV*.TU.||JY_RXPT-H@NYXpVdP_N&Ec3_Vf/Z-A+O1R5(dcRX:D&c",
				"LyCL1>*pXzZ297|ClAlkjLHc-O%lpXzZKf7T*^7t((F##PP1EX@EW:AM<+#+U++U++UF4#Mq#.E8O|j1+jU+jU+*##PP.E%;.yU:AW<Y^5l4JSJDH.E8J1E/:pWd|.#XdRWD>LD<qX1cN.EJT>JHER3JRG_DYNNcJ1;||5FBc<-Cq/:)l6&-*|JRk1->W#(+1U):;D>7MJpE|b%8X:Gy**.JYEd3_d._DHNCJ1-*|5FBc<Xcl8:/F+pX|*-pbDJ+Vc.bDXqq7E9PJHpG*_zXNC51;Tl|f#PP+9#(PH#MT.y-<Oc_KXq+#.pM1+j1/jU+jU+*##PP^fWF.EXRNCJ1",
				"-yc-1>+pXzZ297|ClAXkjLHc;O>+pXkZ2f9T*#7t3Pf##PPdEX@yW:AM<UX+jjUUUUU+4#Mq#.E81+j1+jU+j1+*##PP.E%;.y8:AW<Y^Ml^JSJDH.E85OE/:AWd|.#XpRWD>LD<qX1cN.EJTRJHER3JRG_DYNNCJ1-||5FBc<-cq/:)*6&;*|JRFp-%Wf(+1U8:;D>7MJ^ylb>1X:Gy*T.5YEd3_d._DHNCJ1-*|5FBc.Xcl8:/F+pX|*-AbDJ+Vc.bDXqq7EtPJHpG*_zXNCJ1;Tl|f#PP+9#(PH#MTYp-<Oc_KXq+#.PM1+j14jU+jU+*##PPY>WF.EXRNCJ1",
				"V.|H)JRE|flcV%c.zJ|yWN9bXc35DZ|TPcO6cJ|5#d/UV8-*@JRGOD:X|EVRdVc.b+SqZc/P5cJ|X7cVNR&P*VU.bV%*4(V.|5FBcYAV5cJb&VRVbNR%*CcWC*R4(*<*RN&3*R5VtTS*qcJR@6Rfcyp&^-*|JRl31R4.|5FBc-c|1|*zZ(8;9kbp*FVcJ>UD.|^7j9VAJYV2|RGV.Ft|>K+MJbc:TRz.RGJFWHER^6ycV3Vc.fUM.|.Rb..b.*bb*bccJ#*@VJ-X.VYyVbc86*-*qVf)VScJV*.TU.||JY_RXPT-H@NYXpVdP_N&Ec3_Vf/Z-A+O1R5(dcRX:D&c",
				"V.R*/.REbM+VVVV.c.R6D@;VUc(5c.|c3cVkc.RONk/cc)-*P)|G(d^8REVRkVc.bLy:.c6t5d9R8Scf@|f<HVYJFVyGV3V.|5FBcMj25cZRVV|AbWlN>PcD1*REt*V>lWfVCRO(k&%GXcKRT1RMz/pfL-*|JRB3#REZ|5FBc-c|#RCzZ(D:9KFp*RVcJA4YKRByX.cq.7VWRR*d.l1R7+LcJRc2&RC9|*.RDHERL1jct<Ec.McY.l...R.R..R...cc.S*:V.%U.VMq1RcDk*-HPVMjV^z9y>9VV.|j.7^R8t&%GT@AUpEk3_WfVk(_V7q.*q+t#|O31cR82dfc",
				"-yc-1#qp-zZ297|ClAXkjLHc;O>*pXzZ2f9T*#7tPPft#PPdEX@yW:AM<UX+jjUUUUU+4#Mq#.E81+j1+jU+j1**##PP.E%;.y8:AW<Y^Ml^JSJDH.E85OE/:AWdt.#XpRWD>LD<qX1cN.EJTRJHER3JRG_DYNNCJ1-||5FBc<-cq/:)*6&;*|JRFp-%Wf(+188:;D>7MJ^y*b>1X:Gy*T.5YEd3_d._DHNCJ1-*|5FBc.Xcl8:/F+pX|*-AbDJ+Vc.bDXqq7EtPJHpG*_2XNcJ1;Tl|f#PP+9#(PH#MTYy-<Oc_KXq+#.PM1+j1+jU+jU+*##PPY>WF.EXRNCJ1",
				"-yc-1>+pXzZ297|ClAXkjLHc;O>+pXzZ2f7T|#7tPPT##PPdEX@yW:AM<UX+U8+U++3+4#Mq#.E81+j1+jU+j1+*##PP.E%;.y8:AW<Y^Ml^JSJDH.E45OE/:AWd|.#XpRW&>LD<qXKcN.EJTRJHER3JRG_DYNNCJ1-||5FBc<-cq/:)*6&;*|JRFp-%Wf(+1U8:;D#7MJ^y*b%1X:Gy*T.5YEd3_d._DHNcJ1-*|5FBc.Xcl8:/F+pX|*-AbDJ+Vc.bDXqq7EtPJHpG*_2XNCJ1;Tl|f#PP+9#(PH#MTYy-<Oc_KXq+#.PM1+j1+j-+jU+*##PPH>WF.EXRNCJ1",
				"cqc3f*7+(zZ2d9|C|+-W:SqcSf8(+-zZWy^t|O^@KK<@YK/GXc.>jU+j4<jj<jjjOj<OlYj<YD>jf#1j|1j|:j<|@dK/D>@7.>jU+jG@djtP5F52qy@MJf>jU+jDE4YbRR_WcczG|Sfbp4XJ#4JH>GT5G4k2NppckfS||5FBcG-cF_UjFt+7*|JR<+-c6YNt4AjUSz@^jJ@X#b8M)UD>k<G&HER/5..&WqpcJf-*|5FBc4bc(AUj<|+L|*-+3zJ(Vc.b2S(|9>YKpqX4t5z7pc&f-<^F@YK/t@YK/lOj|.X3.pcJf7<|d.>jf<1j;:j|:jt#YyKYqOj|4>b.pcJf",
				"Xyc;1>+AXzZ297|ClAXkjLHc;O>+p;zZz#7TT#7>PPf##PP1yX@y_:AM<U/+U++U++D+4#Mq#.E81+j1+jU+j1f*##PP.E%;.y8:AW<Y^Ml^JSJDH.E851E/:AWd|.#XpRWD>LD<qX1cN.EJTRJHER3JRG_DYNNCJ1-||5FBc<-cq/:)*6&;*|JRFp-%Wf(l1U8:;D>7MJpy*b%1X:Gy*+.5YEd3Jd._DHNCJ1-*|5FBc.Xcl8:/F+pX|*-&bDJ+Vc.bKXqq7EtPJHpGOJ2XNCJ1;Tl|##PP+9#(PH#MTYy-<Nc_KXq+#.EM1+j1*jU+jU+*##PPYt8F.EXRNCJ1",
				"7)c3f_C+-zZzd9|CC;t217Pcbf8^+-zZWd9F#y4YK>(d@KK.XS./Aj+U:qXYqXUUqYUUHYU%dDXUf(^U(<A|1UqLdYO>DE@<./6j;UG)dUqP5F5zPR/6kf/Mj+UD*:dbDR6Wcb2G%3f3p:-J*:JHEcT5G.kzPppckfS||5FBcG-cLAjUlE;t*|JRC+-@MyN*:6Ajc284_&)/Cb@MSjD|5^G&HERTk..kWPp@Jf-*|5FBcROcLAjULq+-|*-;b2k%Vc.b2S(^4/yN7P/:#kW8pLJf3F(#@YK>#dYK>lYU*.XS.pcJf-|%d./Ufq1U|<_C1Uq#ddK4lY6*:XbDpcJf",
				"V.|H)JbE|flcV%c.zJ|yWN9bXc35DZ|TPcO6cJ|5#d/UV8-*@JRGOD:X|EVRdVc.b+SqZc/P5cJ|X7cVNR&P*VU.bV%*4(V.|5FBcYAV5cJb&VRVbNR%*CcWC*R4(*<*RN&3*R5VtTS*qcJR@6Rfcyp&^-*|JRl31R4.|5FBc-c|1|*zZ(8;9kbp*FVcJ>UD.b^7j9VAJYV2|RGV.Ft|>K+MJbc:TRz.RGJFWHER^6ycV3Vc.fUM.|.Jb..b.*bb*bccJ#*@VJ-X.VYyVbc86*-*qVf)VScJV*.TU.||JY_RXPT-H@NYXpVdP_N&Ec3_Vf/Z-A+O1R5(dcRX:D&c",
				"V.|H)JbE|flcV%c.zJbyWN9bXc35DZ|TPcO6cJ|5#d/UV8-*@JRGOD:X|EVRdVc.b+SqZcAP5cJ|X7cVNR&P*VU.bV%*4(V.|5FBcYAV5cJb&VRVbNR%*CcWC*R4(*<*bN&3*R5VtTS*qcJR@6Rfcyp&^-*|JRl31R4.|5FBc-c|1|*zZ(8;9kbp*FVcJ>UD.|^7j9VAJYV2|RGV.Rt|>K+MJbc:TRz9RGJFWHER^6ycV3Vc.fUM.|.Jb..b.*bb*bccJ#*@VJ-X.VYyVbc86*-*qVf)VScJV*.TU.||JY_RXPT-H@NYXpVdP_N&Ec3_Vf/Z-A+O1R5(dcRX:D&c",
				"-ycL1>TpXzZ297|ClAXkjLHC;O>+pXzZKf7T*#9t(P+##PP1EX@yW:AM<U+AU++U++U+4#Mq#.E81+j1+jM+j1++##PPGE%;.yU:AW<Y^Ml^JSJDH.E851E/:AWd|.#XpRWD>LD<qX1cN.EJTRJHER3JRG_DYNNCJO-||5FBc<-Cq/:)*6&b*|JRFp-%W9(+1U8:-D>7MJpp*b>1X:GE*F.54Ed3_d._DHNCJ1-*|5FBc.Xcl8:/F+pX|*-AbDJ+Vc.bDXqq7EtPJHpGF_2XNCJ1;Tl|f#PP+9#(PH#MTYy-<Oc_KXq+#.EM1+j1+jU+jU+*##PPY>8F.EXRNCJ1",
				"C4*bfT(+-zZzd8|C|OMz3c@48@cYO8W6WyNPFy^A//Ykk/1DflUp:4<:U.:E:::l:..:Hd:&49E:^Y3:|3:|3:qFkk//7pcbUp:4O:G_d:&@5CXW#Up:5%):4+:7qUd;DRjWTCzG(b&l>Rp5#cJ_pR/5GR5WS>>cX^8||5FBcD-t|L4Lqq6;*|JR2<-cjy/|.::4KzTNjX_)Ybc:;17pX&G5HER1J9.XWH>c5f-*|5FBcUMcF:4jq&Ol|*-+KWJ&Vc.bWKqPNpy/X#EDq5WM>c5flY|(kk/1*kk//_ALYU)-9>cXf-|*kGp:%|3:|3:|3:YYkk//SdL&.pkU>c5f",
				"C4*bfT(+-zZzd8|C|OMz3c@48@cYO8WZWyNPFy^A//Ykk/1DflUp:4<:U.:E:::l:..:Hd:&49E:^Y3:|3:|3:qFkk//7pcbUp:4O:G_d:&@5CXW#Up:5%):4+:7qUd;DRjWTCzG(b&l>Rp5#cJ_pR/5GR5WS>>cX^8||5FBcD-t|L4Lqq6;*|JR2<-tjy/|.::4lzTNjX_)Ybc:;17pX&G5HER/J9.XWH>c5f-*|5FBcUMcF:4jq&Ol|*-+MWJ&Vc.bWKqPNpy/X#EDq5WM>c5flY|(kk/1*kk//_ALYU)-9>cXf-|*kGp:%|3:|3:|3:YYkk//SdL&.pkU>c5f",
				"7lc3f*);|zZ2d^|C|+-Wj-qcSf8(;)2Z2y%P|d^@/N<YYT/GXc.>:U+-4<::::::O::<l@:#YD>:f#jM|1:|j:E|@YK/D>@7.>:U+:G@d:Pt5F52qy@:Jf>:U+:D(4YbRR_WcczG|Sfbp4XJ#4JH@GT5G4k2NppckfS||5FBcG-cF:UA(P;)*|JR|;-@6dNPDM:U-z89M&qX#b8M-UH>k|G&HERN5..&WqpcJf-*|5FBc4bc(AU:F|+L|*-;SzJ(Vc.b2S((9>YOpqX4P5z)pc&f-|^F@YK/P@YK/ly:|.X3.pcJf7<Fd.>:f<1:|j:|j:(#Y@KTqY:|4>b.pcJf",
				"7lc3f*F;|zZ2d^|Cf+-Wj-qcSf8(;)2Zzy%P|d^@/N<YYK/.XcG>:U+:4<::::::O::<lY:#YD>:f#jM|1:|j:<|@YK/D>@7.>:U+:G@d:Pt5F52qy@:Jf>:U+:DE4YbRR_WcczG|Sfbp4XJ#4JH@GT5G4k2NppckfS||5FBcG-cF:UA(P;L*|JR|;-@6dNPDM:U-z89A&@X#b8M-UH>k|G&HER/5..&WqpcJf-*|5FBc4bc(AU:F|+L|*-;SzJ(Vc.b2S((9>YKpqX4P5z-pc&f-F^F@YK/P@YK/ly:|.X3.pcJf7<Fd.>:f<1:|j:|j:(#Y@KYqY:|4>b.pcJf",
				"-yc;1>+pXzZ297|ClAXkjLHC;O>qpXzZKf7T*#9t(P|##PP1EX@yW:AM<U++UA+U++U+4#Mq#.E81+j1+jU+j1++##PPGE%;.yU:AW<Y^Ml^JSJDH.E851E/:AWd|.#XpRWD>LD<qX1cN.EJTRJHER3JRG_DYNNCJO-||5FBc<-Cq/:)*6&b*|JRFp-%Wf(+1U8:-D>7MJpp*b>1X:GE*l.54Ed3_d._DHNCJ1-*|5FBc.Xcl8:/F+pX|*-AbDJ+Vc.bDXqq7EtPJHpGF_2XNCJ1;Tl|f#PP+f#(PH#MTYy-<Oc_KXqT#.EMK+j1+jU+jU+*##PPY>8F.EXRNCJ1",
				"-yC-1>+pXzZ297|ClAXkjLHc;O>+pXzZzf9T*#7tPPT##PPdEX@yW:AM<UX+U++U++;+4#Mq#.E81+j1+jU+j1F*##PP.E%;.y8:AW<Y^Ml^JSJDH.E85OE/:AWd|.#XpRW&>LD<qX1cN.EJTRJHER3JRG_DYNNCJ1-||5FBc<-cq/:)*6&;*|JRFp-%Wf(+1U8:;D#7MJ^y*b>1X:Gy*T.5YEd3_d._DHNcJ1-*|5FBc.Xcl8:/F+pX|*-AbDJ+Vc.bDXqq7EtPJHpG*_2XNCJ1;Tl|f#PP+9#(PH#MTYy-<Oc_KXq+#.PM1+j1+jU+jUL*##PPH>WF.EXRNCJ1",
				"C4cbfT(+-zZzd8|C|OMz3c@48@cYO8W6WyNPFy^A//Ykk/1DflUp:4<:U.:E:::l:..:Hd:&49E:^Y3:|3:|3:qFkk//7pcbUp:4O:G_d:&@5CXW#Up:5%):4+:7qUd;DRjWTCzG(b&l>Rp5#cJ_pR/5GR5WS>>cX^8||5FBcD-t|L4Lqq6;*|JR2<-cjy/|.::4lzTNjX_)Ybc:;17pX&G5HER/J9.XWH>c5f-*|5FBcUMcF:4jq&Ol|*-+KWJ&Vc.bWKqPNpy/X#EDq5WM>c5flY|(kk/1*kk//_ALYU)-9>cXf-|*kGp:%|3:|3:|3:YYkk//SdL&.pkU>c5f",
				"7lc3f*F;|zZ2d^|C|+-Wj-qcSf8(;)2Zzy%P|d^@/N<YYK/.XcG>:U+:4<::::::O::<lY:#YD>:f#jM|1:|j:<|@YK/D>@7.>:U+:G@d:Pt5F52qy@:Jf>:U+:DE4YbRR_WcczG|Sfbp4XJ#4JH@GT5G4k2NppckfS||5FBcG-cF:UA(P;L*|JR|;-@6dNPDM:U-z89A&@X#b8M-UH>k|G&HER/5..&WqpcJf-*|5FBc4bc(AU:F|+L|*-;SzJ(Vc.b2S((9>YKpqX4P5z-pc&f-F^F@YK/P@YK/ly:|.X3.pcJf7<Fd.>:f<1:|j:|j:(#Y@KYqY:|4>b.pcJf",
				"7)c3f_C+-zZzd9|CC;t217Pcbf8^+-zZWd9F#y4YK>(d@KK.XS./Aj+U:qXUqXUUqYUUHYU%dDXUf(^U(<A|1UqLdYO>DE@<./6j;UG)dUqP5F5zPR/6kf/Mj+UD*:dbDR6Wcb2G%3f3p:-J*:JHEcT5G.kzPppckfS||5FBcG-cLAjUlE;t*|JRC+-@MyN*:6Ajc284_&)/Cb@MSjD|5^G&HERTk..kWPp@Jf-*|5FBcROcLAjULq+-|*-;b2k%Vc.b2S(^4/yN7P/:#kW8pLJf3F(#@YK>#dYK>lYU*.XS.pcJf-|%d./Ufq1U|<_C1Uq#ddK4lY6*:XbDpcJf",
				"-yc-1#lp-zZ297|ClAXkjLHc;O>+pXzZ2f9T*#7tPPft#PPdEX@yW:AM<UX+jjUUUUU+4#Mq#.E81+j1+jU+j1**##PP.E%;.y8:AW<Y^Ml^JSJDH.E85OE/:AW<t.#XpRWD>LDGqX1cN.EJTRJHER3JRG_DYNNCJ1-||5FBc<-cq/:)*6&;*|JRFp-%Wf(+188:;D>7MJ^y*b>1X:Gy*T.5YEd3_d._DHNCJ1-*|5FBc.Xcl8:/F+pX|*-AbDJ+Vc.bDXqq7EtPJHpG*_zXNCJ1;Tl|f#PP+9#(PH#MTYy-<Oc_KXq+#.PM1+j1+jU+jU+*##PPY>WF<EXRNCJ1",
				"cqc3f*7+(zZ2d9|C|+-W:SqcSf8(+-zZWy^t|O^@KK<@YK/GXc.>jU+j4<jj<jjjOj<OlYj<YD>jf#1j|1j|:j<|@YK/D>@7.>jU+jG@djtP5F52qy@MJf>jU+jDE4YbRR_WcczG|Sfbp4XJ#4JH>GT5G4k2NppckfS||5FBcG-cF_UjFt+7*|JR<+-c6YNt4AjUSz@^jJ@X#b8M)UD>k<G&HER/5..&WqpcJf-*|5FBc4bc(AUj<|+L|*-+3zJ(Vc.b2S(|9>YKpqX4t5z7pc&f-<^F@YK/t@YK/lOj|.X3.pcJf7<|d.>jf<1j;:j|:jt#YyKYqOj|4>b.pcJf",
				"7lc3f*-;|zZ2d9|C|;-2j-qcSf8(+)2Zzy%P|Y^@KKP@YK/GXc.>:U+:4<::::::O:O<lY:<YD>:f#1:|1:|j:<|@YK/D>@7.>:U;MG@d:Pt5F52qy@MJf>:U+:DE4YbRR_WcczG|Sfbp4XJ#4JH@GT5G4k2NppckfS||5FBcG-cF_UM(P+C*|JR|+-c6YNP4A:U-z@9:&@X#b8M-UD>k<G5HER/5..&WqpcJf-*|5FBc4bc(AU_<|+L|*-;SzJ(Vc.b2S((9>dKpqX4P5z)pc&f-F^F@YK/P@YK/l@:|.X3.pcJf7|Fd.>:f<1:|j:|j:P#YyKYqY:|4>b.pcJf",
				"-yc-1#qp-zZ297|ClAXkjLHc;O>+pXzZ2f9T*#7tPPft#PPdEX@yW:AM<UX+jjUUUUU+4#Mq#.E81+j1+jU+j1**##PP.E%;.y8:AW<Y^Ml^JSJDH.E85OE/:AWdt.#XpRWD>LD<qX1cN.EJTRJHER3JRG_DYNNCJ1-||5FBc<-cq/:)*6&;*|JRFp-%Wf(+188:;D>7MJ^y*b>1X:Gy*T.5YEd3_d._DHNCJ1-*|5FBc.Xcl8:/F+pX|*-AbDJ+Vc.bDXqq7EtPJHpG*_2XNCJ1;Tl|f#PP+9#(PH#MTYy-<Oc_KXq+#.PM1+j1+jU+jU+*##PPY>WF.EXRNCJ1",
				"-yc-1>*pXzZ297|ClAXkjLHc;O>+pXzZ2f9Tq#7tPPT##PPdyX@yW:AM<UX+U++U++X+4#Mq#.E81+j1+jU+j1F*##PP.E%;.y8:AW<Y^Ml^JSJDH.E85OE/:AWd|.#XpRW&>LD<qX1cN.EJTRJHER3JRG_DYNNCJ1-||5FBc<-cq/:)*6&;*|JRFp-%Wf(+1U8:;D#7MJ^y*b>1X:Gy*T.5YEd3_d._DHNcJ1-*|5FBc.Xcl8:/F+pX|*-AbDJ+Vc.bDXqq7EtPJHpG*_2XNCJ1;Tl|f#PP+9#(PH#MTYy-<Oc_KXq+#.PM1+j1+jU+jU+*##PPH>WF.EXRNCJ1",
				"-ycL1>TpXzZ297|ClAXkjLHC;O>+pXzZKf7T*#9t(P+##PP1EX@yW:AM<U++U++U++U+4#Mq#.E81+j1+jM+j1++##PPGE%;.yU:AW<Y^Ml^JSJDH.E851E/:AWd|.#XpRWD>LD<qX1cN.EJTRJHER3JRG_DYNNCJO-||5FBc<-Cq/:)*6&b*|JRFp-%W9(+1U8:-D>7MJpp*b>1X:GE*F.54Ed3_d._DHNCJ1-*|5FBc.Xcl8:/F+pX|*-AbDJ+Vc.bDXqq7EtPJHpGF_2XNCJ1;Tl|f#PP+9#(PH#MTYy-<Oc_KXq+#.EM1+j1|jU+jU+*##PPY>8F.EXRNCJ1",
				"-yc-1>+pXzZ297|ClAXkjLHc;O>+pXzZ2f9Tq#7tPPT##PPdyX@yW:AM<UX+U++U++X+4#Mq#.E81+j1+jU+j1F*##PP.E%;.y8:AW<Y^Ml^JSJDH.E85OE/:AWd|.#XpRW&>LD<qX1cN.EJTRJHER3JRG_DYNNCJ1-||5FBc<-cq/:)*6&;*|JRFp-%Wf(+1U8:;D#7MJ^y*b>1X:Gy*T.5YEd3_d._DHNcJ1-*|5FBc.Xcl8:/F+pX|*-AbDJ+Vc.bDXqq7EtPJHpG*_2XNCJ1;Tl|f#PP+9#(PH#MTYy-<Oc_KXq+#.PM1+j1+jU+jU+*##PPH>WF.EXRNCJ1",
				"7lc3f*-;|zZ2d9|C|;-2j-qcSf8(+)2Zzy%P|Y^@KKP@YK/GXc.>:U+:4<::::::O:O<lY:<YD>:f#1:|1:|j:<|@YK/D>@7.>:U+MG@d:Pt5F52qy@MJf>:U+:DE4YbRR_WcczG|Sfbp4XJ#4JH@GT5G4k2NppckfS||5FBcG-cF_UM(P+C*|JR|+-c6YNP4A:U-z@9:&@X#b8M-UD>k<G5HER/5..&WqpcJf-*|5FBc4bc(AU_<|+L|*-;SzJ(Vc.b2S((9>dKpqX4P5z)pc&f-F^F@YK/P@YK/l@:|.X3.pcJf7|Fd.>:f<1:|j:|j:P#YyKYqY:|4>b.pcJf",
				";Xcb4TTP3zZ297/CpPbzS;qcb4p|P-2ZD97tpp7WN(FlWNA.OLGOYMP>16WNZ6NN+W+&HWY&l.%Y|*@Y&S>/SyTt<W(A4O)LGOYMPYRX@YFq#^5zX.E%K4OY:PYRt@p-dRY2)3zGf84CkGU_/.JHERA5.G5DXk1cK4C||5FBcd-c96:>^&P+*|JRfP-@Y<(FG6YM82p7yJHE|b*Y+:dUjtRJHER)J..5zXkcKj-*|5FBc.;c/Y:>^fPC|*-P3D_/Vc.bz8tp7OWNpq#.&5D81cK4C*|pW9((FWlAAqWytROLGkcK4-@&ld%Yj/@Y&@>^SyT^Wl(AX<yTGO;.kcKj",
				"-yc;1>TpXzZ297|ClAXkjLHC;O>+pXDZKf7T*#9t(P+##PP1EX@yW:AM<U)+U++U#+U+4#Mq#.E81+j1+jU+j1++##PPGE%;.yU:AW<Y^Ml^JSJDH.E851E/:AWd|.#XdRWD>LD<qX1cN.EJTRJHER3JRG_DYNNCJO-||5FBc<-Cq/:)*6&b*|JRFp-%Wt(+1U8:-2>7MJpp*b>1X:Gy*|.54Ed3_d._DHNCJ1-*|5FBc.Xcl8:/F+pX|*-AbDJ+Vc.bDXqq7EtPJHpGF_zXNCJ1;Tl|f#PP+9#(PH#MTYy-<Oc_KXq+#.EM1+j1+jU+jU+*##PPY>8F.EXRNCJ1",
				"+zZ18^:kzyP%O*_AtYf^H7X.*PZB<|KERG)%flUOYLABcpt9jfl%**Lb<z_kzyP%<*_PRYfFK7X.*PZF-AJWjbl/bYERJGqH&Ryq62.H.YlqJ4J4byb6%P%C*P*B4bKzy<^%k9yA%O(_PRYf^PZ8WNE%*%*f5/_qZJ4&75P%qy-54(#cSj4POcG4PL-J4F.%qlU8Y<A%*dNWjRt_VX.2fl#*BcbMfl2NFDZpJART%q7byG)d5byf7ZP%qydZpJ9WWGFY4|.Cqly*-Y4b.*lRJ-*4PTcG|Y4F.OlRJbMWkfGLFBcF.clyY|M4|5FG&J4b.cVRJ|*Y4|5FBcbHERGb",
				"-yc-1>+pXzZ297|ClAXkjLHc;O>+pXkZ2f9T*#7t3Pf##PPdpX@yW:AM<UX+jjUUUUU+4#Mq#.E81+j1+jU+j1+*##PP.E%;.y8:AW<Y^Ml^JSJDH.E85OE/:AWd|.#XpRWD>LD<qX1cN.EJTRJHER3JRG_DYNNCJ1-||5FBc<-cq/:)*6&;*|JRFp-%Wf(+1U8:;D>7MJ^ylb>1X:Gy*T.5YEd(_d._DHNCJ1-*|5FBc.Xcl8:/F+pX|*-AbDJ+Vc.bDXqq7EtPJHpG*_zXNCJ1;Tl|f#PP+9#(PH#MTYp-<Oc_KXq+#.PM1+j1+jU+jU+*##PPY>WF.EXRNCJO",
				"-yc-1>#pXzZ297|ClAXkjLHc;O>+pXkZ2f9T*#7t3Pf##PPdEX@yW:AM<UX+jjUU+UU+4#Mq#.E81+j1+jU+j1+*##PP.E%;.y8:AW<Y^Ml^JSJDH.E85OE/:AWd|.#XpRWD>LD<qX1cN.EJTRJHER3JRG_DYNNCJ1-||5FBc<-cq/:)*6&;*|JRFp-%Wf(+1U8:;D>7MJ^ylb>1X:Gy*T.5YEd3_d._DHNCJ1-*|5FBc.Xcl8:/F+pX|*-AbDJ+Vc.bDXqq7EtPJHpG*_zXNCJ1;Tl|f#PP+9#(PH#MTYp-<Oc_KXq+#.PM1+j1+jU+jU+*##PPY>WF.EXRNCJ1",
				"-yc-1>+pXzZ297|ClAXkjLHc;O>+pXzZ2f9+*#7tPPT##PPdEX@yW:AM<UX++++U+G2+4#Mq#.E81+j1+jU+j1+*##PP.E%;.y8:AW<Y^Ml^JSJDH.E85OE/:AWd|.#XpRW&>LD<qX1cN.EJTRJHER3JRG_DYNNCJ1-||5FBc<-cq/:)*6&;*|JRFp-%WfP+1U8:;D#7MJ^y*b>1X:Gy*T.5YEd3_d._DHNcJ1-*|5FBc.Xcl8:/F+pX|*-AbDJ+Vc.bDXqq7EtPJHpG*_2XNCJ1;Tl|f#PP+t#(PH#MTYy-<Oc_KXq+#.PM1+j1+jU+jU+*##PPH>WF.EXRNCJ1",
				"#JRH&XRW%1_zAYCLdj|y9&pR>D35z<|/2D6SzK|5:Utc#J-*:JNG6dY>bf^b)Vc.bB-:Lcy45CZR>+c_&k_2GW#Z|8+HE3p.|5FBcPt-5dLk_^b)b&k-*:T_)*|W2G6*Rp_6*N52S/-H;pKk:ON1@t__l-*|JRl3qRW.|5FBc-C|qNGzZ39;^Kk_(%p#J1TdKkB+:LDyXPf&kRHcj%ON1@lD.bd-/Vcj%H.89HEREUtc34WzZPTC<RfffffF7ff/7fTC<-G;p<->.8PtONd4S*-G;pPtAYCL-*X/dj|yL1MR>2/-G;&P>_W)2Mp_D)3Mp1T.YtB67R536dR>Yc_z",
				"-yc-1>#pXzZz97|ClAXkjLHc;O>+pXkZ2f9T*#7t3Pf##PPdEX@yW:AM<UX+UjUUUUU+4#Mq#.E81+j1+jU+j1+*##PP.E%;.y8:AW<Y^Ml^JSJDH.E85OE/:AWd|.#XpRWD>LD<qX1cN.EJTRJHER3JRG_DYNNCJ1-||5FBc<-cq/:)*6&;*|JRFp-%Wf(+1U8:;D>7MJ^ylb>1X:Gy*T.5YEd3_d._DHNCJ1-*|5FBc.Xcl8:/F+pX|*-AbDJ+Vc.bDXqq7EtPJHpG*_zXNCJ1;Tl|f#PP+9#(PH#MTYp-<Oc_KXq+#.PM1+j1+jU+jU+*##PPY>WF.EXRNCJ1",
				"-ycL1>TpXzZ297|ClAXkjLHC;O>+pXzZKf7T*#9t(P+##PP1EX@yW:AM<U++U++U++U+4#Mq#.E81+j1+jM+j1++##PPGE%;.yU:AW<Y^Ml^JSJDH.E851E/:AWd|.#XpRWD>LD<qX1cN.EJTRJHER3JRG_DYNNCJO-||5FBc<-Cq/:)*6&b*|JRFp-%W9(+1U8:-D>7MJpplb>1X:GE*F.54Ed3_d._DHNCJ1-*|5FBc.Xcl8:/F+pX|*-AbDJ+Vc.bDXqq7EtPJHpGF_2XNCJ1;Tl|f#PP+9#(PH#MTYy-<Oc_KXq+#.EM1+j1+jU+jU+*##PPY>8F.EXRNCJ1",
				"Xyc;1>+pXzZ297|ClAXkjLHc;O>+p;KZK#7Tl#7tPPf##PP1yX@y_:AM<+/+U++U++++4#Mq#.E81+j1+jW+j1+*##PP.E%;.y8:AW<Y^Ml^JSJDH.E851E/:AWd|.#XpRWD>LD<qX1cN.EJTRJHER3JRG_DYNNCJ1-||5FBc<-cq/:)*6&;*|JRFp-%Wf(l1U8:;D>7MJpy*b%1X:Gy*+.5YEd3_d._DHNCJ1-*|5FBc.Xcl8:/F+pX|*-AbDJ+Vc.bKXqq7EtPJHpG*J2XNCJ1;Tl|##PP+9#(PH#MTYp-<Nc_KXq+#.EM1+j1+jU+jU+*##PPY>8F.EXRNCJ1",
				"V.F*/.LkFfdcVTc.c.RAW2yR<c35cV|SPc4CV.V5M6)%j.-*XVbG1Dl<bEVbqVc.b+TU.%@P5D.R<^c&NR&P*VjKLV#*E7V.|5FBc>)M5z.L&VRVb2R:*Oj8q*Rk(*7HbN&7Gb5VtST*U%ZR;CRfc@1&+-*|JRB34RVK|5FBc-c|VRHzZ(8;9KR1*FVVJYcc.R+:U.D/.>9NRR*c.VtRp+Bc.Rc#SRcZR*KRWHERdt/cPP^c.fD%.R............cc.-*XVZ-<.Vp/qbcW6*-*XVf)VTcJV*.Sj.|).Y_R<PS#G;2Y<1Vq7_N&kc3_V>@ZlA+P4R5(6cR<:z&z",
				"-yc-1>+pXzZ297|ClAXkjLHc;O>+pXkZ2f9T*#7t3Pf##PPdEX@yW:AM<UX+jjUUUUU+4#Mq#.E81+j1+jU+j1+*##PP.E%;.y8:AW<Y^Ml^JSJDH.E8JOE/:AWd|.#XpRWD>LD<qX1cN.EJTRJHER3JRG_DYNNCJ1-||5FBc<-cq/:)*6&;*|JRFp-%Wf(+1U8:;D>7MJ^ylb>1X:Gy*T.5YEd3_d.JDHNCJ1-*|5FBc.Xcl8:/F+pX|*-AbDJ+Vc.bDXqq7EtPJHpG*_zXNCJ1;Tl|f#PP+9#(PH#MTYp-<Oc_KXq+#.PM1+j1+jU+jU+*##PPY>WF.EXRNCJ1",
				"4qc3f*7+(zZ2d9|C|+Lzj)qcSf8(+-zZWy^t|O^@KK<@YK/4Xc.>:U+:4<:q<:O:O:<<lY:<YD>:f#1:|1:|j:<|yYK/D>@C.>:U+:G@d:tP5F52qy@MJf>:U+:DE4YbRR_WcczG|Sfbp4XJ#4JH>GT5G4J2NppckfS||5FBcG-cF_U:Ft+7*|JRF+-c6YNt4A:USz@9:J@X#b8M)UD>k<G&HER/k..JWqpcJf-*|5FBc4bc(AU:<|+L|*-+3zJ(Vc.b2S(|9>YKpqX4t5z7pcJf-<^F@YK/t@YK/lO:|.X3.pcJf7<|d.>:f<1:|j:|j:t#YyKYqO:|4>b.pcJf",
				";Xcb4TTP3zZ297/CpPbzS;qcb4p|P-2ZD97tpp7WN(FlWNA.OLGOYMP>16WNZ6NN+WNKHWY&l.%Y|*@Y&S>/SyTt<W(A4O)LGOYMPYRX@YFq#^5zX.E%K4OY:PYRt@p-dRY2)3zGf84CkGU_/.JHERA5.G5DXk1cK4C||5FBcd-c96:>^&P+*|JRfP-@Y<(FG6YM82p7yJHE|b*Y+:dUjtRJHER)J..5zXkcKj-*|5FBc.;c/Y:>^fPC|*-P3D_/Vc.bz8tp7OWNpq#.&5D81cK4C*|pW9((FWlAAqWytROLGkcK4-@&ld%Yj/@Y&@>^SyT*Wl(AX<yTGO;.kcKj",
				"V.F*/.LkFfdcVTc.c.RAW2yR<c35cV|SPc4CV.V5M6)%j.-*XVbG1Dl<bEVbqVc.b+TU.%@P5D.R<^c&NR&P*VjKLV#*E7V.|5FBc>)M5z.L&VRVb2R:*Oj8q*Rk(*7HbN&7Gb5VtST*U%ZR;CRfc@1&+-*|JRB34RVK|5FBc-c|VRHzZ(8;9KR1*FVVJYcc.L+:U.D/.>9NRR*c.VtRp+Bc.Rc#SRcZR*KRWHERdt/cPP^c.fD%.R............cc.-*XVZ-<.Vp/VbcW6*-*XVf)VTcJV*.Sj.|).Y_R<PS#G;2Y<1Vq7_N&kc3_V>@ZlA+P4R5(6cR<:z&z",
				"pCbH<Z4fbT_d7-)AcAq<^&MF)D:Y(#|t6X6>/.R5L></(l-*;JRG1D-)SN8q@Vc.b%-;lc<3YzlV)Uc_&S_3HfzZV8UHEO8.|5FBcP<UY/KS_8F@bk4E*;d_>*|f6j6*|k_1*|56>t-G;ClS;@4TXy2p%-*|JR%1WOfJ|5FBc-(|WRGzZO9;^KS2jS7XJTXCJb%U;#(<JPNp4RHCZ4>/T_BdA4D-tVc.qH^89HER%><c33fzZTXCJR|+Lc3+L4BLWl/(l-G;7l-).8P<@bd6>*-H;8P<7Ld.-DAt(#|<KPpF)6tU*;kPy2N@:p&pN@:p&P<*LYB1WR5:>/R)UX_z",
				"-ycL1>TpXzZ297|ClAXkjLHC;O>+pXzZKf7T*#9t(P+##PP1EX@yW:AM<U++U++U++U+4#Mq#.E8K+j1+jM+j1++##PPGE%;.yU:AW<Y^Ml^JSJDH.E851E/:AWd|.#XpRWD>LD<qX1cN.EJTRJHER3JRG_DYNNCJO-||5FBc<-Cq/:)*6&b*|JRFp-%W9(+1U8:-D>7MJpplb>1X:GE*F.54Ed3_d._DHNCJ1-*|5FBc.Xcl8:/F+pX|*-AbDJ+Vc.bDXqq7EtPJHpGF_2XNCJ1;Tl|f#PP+##(PH#MTYy-<Oc_KXq+#.EM1+j1+jU+jU+*##PPY>8F.EXRNCJ1",
				"C4*bfT(+-zZzd8|C|<Mz3c@48@cYObWZWyNPFy^A//(kk/1DflUp:4<:U.:E:::::.::Hd:&49E:^Y3:|3:|3:qFkk//7pcBUp:4O:G_d:&@5CXz#Up:5%):4+:7qUd;KRjWTCWG(bfl>Rp5#GJ_pR/5GR5WS>>cX^8||5FBcD-c|L4Lqq6;*|JR2<-tjy1|.:j4MzTNLX_)Ybc:;17pX&G5HER/J9.XWH>c5f-*|5FBcUMcF:4jq&<l|*-+KWJ&Vc.bWKqPNpy/X#EDq5WM>c5flY|(kk/1qkk//_ALY.E-9>cXf-|*yGp:%|3:|3:|3:YYkk//4dL&UpkU>c5f",
				"-yc;1>+pXzZ297|ClAXkjLHC;O>qpXzZKf7T*#9t(P|##PP1yX@yW:AM<U++U++U++U+4#Mq#.E81+j1+jU+j1++##PPGE%;.yU:AW<Y^Ml^JSJDH.E851E/:AWd|.#XpRWD>LD<qX1cN.EJTRJHER3JRG_DYNNCJO-||5FBc<-Cq/:)*6&b*|JRFp-%Wf(+1U8:-D>7MJpp*b>1X:Gy*l.54EdP_d._DHNCJ1-*|5FBc.Xcl8:/F+pX|*-AbDJ+Vc.bDXqq7EtPJHpGF_2XNCJ1;Tl|f#PP+9#(PH#MTYy-<Oc_KXqT#.EMK+j1+jU+jU+*##PPY>8F.EXRNCJ1",
				"-yc;1>+pXzZ297|ClAXkjLHC;O>qpXzZKf7T*#9t(P|##PP1yX@yW:AM<U++U++U++U+4#Mq#.E81+j1+jU+j1++##PPGE%;.yU:AW<Y^Ml^JSJDH.E851E/:AWd|.#XpRWD>LD<qX1cN.EJTRJHER3JRG_DYNNCJO-||5FBc<-Cq/:)*6&b*|JRFp-%Wf(+1U8:-D>7MJpp*b>1X:Gy*l.54Ed3_d._DHNCJ1-*|5FBc.Xcl8:/F+pX|*-AbDJ+Vc.bDXqq7EtPJHpGF_2XNCJ1;Tl|f#PP+9#(PH#MTYy-<Oc_KXqT#.EMK+j1+jU+jU+*##PPY>8F.EXRNCJ1",
				"4qc3f*C+(zZ2d9|C|+Lzj)qc7f8(+-zZWy^t|O^@KK<@YK/4X3.>:U+:4<:q<:O:O:<<lY:<YD>:f#1:|1:|j:<|yYK/D>@C.>:U+:G@d:tP5F52qy@MJf>:U+:DE4YbRR_WcczG|Sfbp4XJ#4JH>GT5G4J2NppckfS||5FBcG-cF_UAFt+7*|JRF+-c6yNt4A:USz@9:J@X#b8M)UD>&<G&HER/k..JWqpcJf-*|5FBc4bc(AU:<|+L|*-+3zJ(Vc.b2S(|9>YKpqX4t5z7pcJf-<^F@YK/t@YK/lO:|.X3.pcJf7<|d.>:f<1:|j:|j:t#YyKYqO:|4>b.pcJf",
				";Xcb4TTP3zZ297/CpPbzS;qcb4p|P-2ZD97tpp7WN(FlWNA.OLGOYMP>16WNZ6NN+WNKHWY&l.%Y|*@Y&S>/SyTt<W(A4O)LGOYMPYRX@YFq#^5zX.E%K4OY:PYRt@p-dRY2)3zGf84CkGU_/.JHERA5.G5DXk1cK4C||5FBcd-c96:>^&P+*|JRfP-@Y<(FG6YM82p7yJHE|b*Y+:dUjtRJHER)J..5zXkcK4-*|5FBc.;c/Y:>^fPC|*-P3D_/Vc.bz8tp7OWNpq#.&5D81cK4C*|pW9((FWlAAqWytROLGkcK4-@&ld%Yj/@Y&@>^SyT*Wl(AX<yTGO;.kcKj",
				"CH*bfT(+-zZzd8|C|<Mz3c@48@cYObWZW4NPFy^A/1(kk//RflUp:4<:U.:E:::::..:Hd:&49E:^Y3:|3:|3:qFkk//7pcBUp:4O:G_d:&@5CXz#Up:5%):4+:7qUd;KRjWTCWG(bfl>Rp5#GJ_pR/5GR5WS>>cX^8||5FBcD-c|L4Lqq6;*|JR2<-tjy1|.:j4MzTNL5_)Ybc:;17pX&G5HER/J9.XWH>c5f-*|5FBcUMcF:4jq&<l|*-+MzJ&Vc.bWKqPNpy/X#EDq5zl>c5flY|(kk/1qkk//_ALY.E-9>cXf-|*yGp:%|3:|3:|3:YYkk//4dL&UpkU>c5f",
				"-yc-1>+pXzZ297|ClAXkjLHc;O>+pXzZ2f9+*#7tPPT##PPdEX@yW:AM<UX+U++U++++4#Mq#.E81+j1+jU+j1+*##PP.E>;.y8:AW<Y^Ml^JSJDH.E85OE/:AWd|.#XpRW&%LD<qX1XN.EJTRJHER3JRG_DYNNCJ1-||5FBc<-cq/:)*6&;*|JRFp-%WfP+1U8:;D%7MJ^y*b>1X:Gy*T.5YEd3_d._DHNcJ1-*|5FBc.Xcl8:/F+pX|*-AbDJ+Vc.bDXqq7EtPJHpG*_2XNCJ1;Tl|f#PP+t#(PH#MTYy-<Oc_KXq+#.PM1+j1+jU+jU+*##PPH>WF.EXRNCJ1",
				"V.F*/.LkFfdcVTc.c.RAW2yR<c35cV|SPc4CV.V5M6)%j.-*XVbG1Dl<bEVbqVc.b+TU.%@P5D.R<^c&NR&P*VjKLV#*E7V.|5FBc>)M5z.L&VRVb2R:*Oj8q*Rk(*7HbN&7Gb5VtST*U%ZR;CRfc@1&+-*|JRB34RVK|5FBc-c|VRHzZ(8;9KR1*FVVJYcc.R+:U.D/.>9NRR*c.VtRp+Bc.Rc#SRcZR*KRWHERdt/cPP^c.fD%.R............cc.-*XVZ-<.Vp/VbcW6*-*XVf)VTcJV*.Sj.|).Y_R<PS#G;2Y<1Vq7_N&kc3_V>@ZlA+P4R5(6cR<:z&z",
				"4qc3f*7+(zZ2d9|C|+Lzj)NcSf8(+-zZWy^t|O^@KK#@YK/4Xc.>:U+:4O:qO:O:O:<<lY:<YD>:f#1:|1:|j:<|yYK/D>@C.>:U+:G@d:tP5F52qy@MJf>:U+:DE4YbRR_WcczG|Sfbp4XJ#4JH>GT5G4J2NppckfS||5FBcG-cF_U:Ft+7*|JR*+-c6YNt4A:USz@9:J@X#b8M)UD>k<G&HER/k..JWqpcJf-*|5FBc4bc(AU:<|+L|*-+3zJ(Vc.b2S(|9>YKpqX4t5z7pcJf-F^F@YK/t@YK/lO:|.X3.pcJf7<|d.>:f<1:|j:|j:t#YyKYqO:|4>b.pcJf",
				"CH*bfT(+-zZzd8|C|<Mz3c@48@cYObWZW4NPFy^A/1(kk//RflUp:4<:U.:.:::::..:Hd:&49E:^Y3:|3:|3:qFkk//7pcBUp:4O:G_d:&@5CXz#Up:5%):4+:7qUd;KRjWTCWG(bfl>Rp5#GJ_pR/5GR5WS>>cX^8||5FBcD-c|L4Lqq6;*|JR2<-tjy1|.:j4MzTNL5_)Ybc:;17pX&G5HER/J9.XWH>c5f-*|5FBcUMcF:4jq&<l|*-+MzJ&Vc.bWKqPNpy/X#EDq5zl>c5flY|(kk/1qkk//_ALY.E-9>cXf-|*yGp:%|3:|3:|3:YYkk//4dL&UpkU>c5f",
				"Xyc;1%+pXzZ297|ClAXkjLHc;O>+p;KZO#7Tl97tPPf#tPP1yX@y_:AM<U/+U++U++++4#Mq#.E81+j1+jU+j1+*##PP.E%;.y8:AW<Y^Ml^JSJDH.E851E/:AWd|.#XpRWD>LD<qX1cN.EJTRJHER3JRG_DYNNCJ1-||5FBc<-cq/:)*6&;*|JRFp-%Wf(l1U8:;D>7MJpy*b%1X:Gy*+.5YEd3_d._DHNCJ1-*|5FBc.Xcl8:/F+pX|*-AbDJ+Vc.bKXqq7EtPJHpG*J2XNCJ1;Tl|##PP+9#(PH#MTYy-<Nc_KXq+#.EM1+j1+jU+jU+*##PPY>8F.EXRNCJ1",
				"Xyc;1%+pXzZ297|ClAXkjLHc;O>+p;KZK#7Tl97tPPf#tPP1yX@y_:AM<U/+U++U++++4#Mq#.E81+j1+jU+j1+*##PP.E%;.y8:AW<Y^Ml^JSJDH.E851E/:AWd|.#XpRWD>LD<qX1cN.EJTRJHER3JRG_DYNNCJ1-||5FBc<-cq/:)*6&;*|JRFp-%Wf(l1U8:;D>7MJpy*b%1X:Gy*+.5YEd3_d._DHNCJ1-*|5FBc.Xcl8:/F+pX|*-AbDJ+Vc.bKXqq7EtPJHpG*J2XNCJ1;Tl|##PP+9#(PH#MTYy-<Nc_KXq+#.EM1+j1+jU+jU+*##PPY>8F.EXRNCJ1",
				"V.F*/.LkRfBcVTc.c.RAW2yR<c35cV|SPc4CV.V5M6)%j.-*XVbG1Dl<bEVbqVc.b+TU.%@P5D.R<^c&NR&P*VjKLV#*E7V.|5FBc>)M5z.L&VRVb2R:*Oj8q*Rk(*7HbN&7Gb5VtST*U%ZR;CRfc@1&+-*|JRB34RVK|5FBc-c|VRHzZ(8;9KR1*RVVJYcc.Rd:U.D/.>9NRR*c.VtRp+Bc.bc#SRcZR*KRWHERdt/cPP^c.fD%.R............cc.-*XVZ-<.Vp/VbcW6*-*XVf)VTcJV*.Sj.|).Y_R<PS#G;2Y<1Vq7_N&kc3_V>@ZlA+P4R5(6cR<:z&z",
				"C4*bfT(+bzZzd8|C|<Mz3c@c8@cYObWZWyNPFy^A//(kk/1DflUp:4<:U.:.::.::.:.Hd:&49E:^Y3:|3:|3:qFkk//7pcBUp:4O:G_d:&@5CXz#Up:5%):4+:7qUd;KRjWTCWG(bfl>Rp5#GJ_pR/5GR5WS>>cX^8||5FBcD-c|L4Lqq6;*|JR2<-tjy1|.:j4MzTNLX_)Ybc:;17pX&G5HER/J9.XWH>c5f-*|5FBcUMcF:4jq&<l|*-+KWJ&Vc.bWKqPNpy/X#EDq5WM>c5flY|(kk/1qkk//_ALY.E-9>cXf-|*yGp:%|3:|3:|3:YYkk&/4dL&UpkU>c5f",
				"-yc;1>+pXzZ297|ClAXkjLHC;O>+pXzZKf7TF#9t(P+##PP1EX@yW:AM<Uj+U++U++U+4#Mq#.E81+j1+jU+j1++##PPGE%;.yU:AW<Y^Ml^JSJDH.E851E/:AWd|.#XpRWD>LD<qX1cN.EJTRJHER3JRG_DYNNCJO-||5FBc<-Cq/:)*6&b*|JRFp-%Wf(+1U8:-D>7MJpp*b>1X:Gy*F.54Ed3_d._DHNCJ1-*|5FBc.Xcl8:/F+pX|*-AbDJ+Vc.bDXqq7EtPJHpGF_2XNCJ1;Tl|f#PP+9#(PH#MTYy-<Oc_KXq+#.EM1+j1+jU+jU+*##PPH>8F.EXRNCJ1",
				"Xyc;1>-pXzZ297|ClAXkjLHc;O>+p;KZK#7Tl#7tPPf##PP1yX@y_:AM<+/+U++U++++4#Mq#.E81+j1+jU+j1+*##PP.E%;.y8:AW<Y^Ml^JSJDH.E851E/:AWd|.#XpRWD>LD<qX1cN.EJTRJHER3JRG_DYNNCJ1-||5FBc<-cq/:)*6&;*|JRFp-%Wf(l1U8:;D>7MJpy*b%1X:Gy*+.5YEd3_d._DHNCJ1-*|5FBc.Xcl8:/F+pX|*-AbDJ+Vc.bKXqq7EtPJHpG*J2XNCJ1;Tl|##PP+9#(PH#MTYy-<Nc_KXq+#.EM1+j1+jU+jU+*##PPY>8F.EXRNCJ1",
				"C4*bfT(+-zZzd8|C|<Mz3c@48@cYObWZWyNPFy^A//(kk/1DflUp:4<:U.:.::.::.:.Hd:&49E:^Y3:|3:|3:qFkk//7pcBUp:4O:G_d:&@5CXz#Up:5%):4+:7qUd;KRjWTCWG(bfl>Rp5#GJ_pR/5GR5WS>>cX^K||5FBcD-c|L4Lqq6;*|JR2<-tjy1|.:j4MzTNLX_)Ybc:;17pX&G5HER/J9.XWH>c5f-*|5FBcUMcF:4jq&Ol|*-+KWJ&Vc.bWKqPNpy/X#EDq5WM>c5flY|(kk/1qkk//_ALY.E-9>cXf-|*yGp:%|3:|3:|3:YYkk//4dL&UpkU>c5f",
				"K4*bfT(+bzZzd8|C|<Mz3c@c8@cYObWZWyNPFy^A//(kk/1DflUp:4<:U.:.::.::.:.Hd:&A9E:^Y3:|3:|3:qFkk//7pcBUp:4O:G_d:&@5CXz#Up:5%):4+:7qUd;KRjWTCWG(bfl>Rp5#GJ_pR/5GR5WS>>cX^8||5FBcD-c|L4Lqq6;*|JR2<-tjy1|.:j4MzTNLX_)Ybc:;17pX&G5HER/J9.XWH>c5f-*|5FBcUMcF:4jq&<l|*-+KWJ&Vc.bWKqPNpy/X#EDq5WM>c5flY|_kk/1qkk//_ALY.E-9>cXf-|*yGp:%|3:|3:|3:YYkk//4dL&UpkU>c5f",
				"Xyc;1>+pXzZ297|ClAXkjLHc;O>+p;KZK#7Tl#7tPPf##PP1yX@y_:AM<+/+U++U++++4#Mq#.E81+j1+j1+j1+*##PP.E%;.y8:AW<Y^Ml^JSJDH.E851E/:AWd|.#XpRWD>LD<qX1cN.EJTRJHER3JRG_DYNNCJ1-||5FBc<-cq/:)*6&;*|JRFp-%Wf(l1U8:;D>7MJpy*b%1X:Gy*+.5YEd3_d._DHNCJ1-*|5FBc.Xcl8:/F+pX|*-AbDJ+Vc.bKXqq7EtPJHpG*JzXNCJ1;Tl|##PP+9#(PH#MTYy-<Nc_KXq+#.EM1+j1+jU+jU+*##PPY>8F.EXRNCJ1",
				"Xyc;1>+pXzZ297|ClAXkjLHc;O>+p;KZK#7Tl#7tPPf##PP1yX@y_:AM<+/+U++U++++4#Mq#.E81+j1+jU+j1+*##PP.E%;.y8:AW<Y^Ml^JSJDH.E851E/:AWd|.#XpRWD>LD<qX1cN.EJTRJHER3JRG_DYNNCJ1-||5FBc<-cq/:)*6&;*|JRFp-%Wf(l1U8:;D>7MJpy*b%1X:Gy*+.5YEd3_d._DHNCJ1-*|5FBc.Xcl8:/F+pX|*-AbDJ+Vc.bKXqq7EtPJHpG*J2XNCJ1;Tl|##PP+9#(PH#MTYp-<Nc_KXq+#.EM1+j1+jU+jU+*##PPY>8F.EXRNCJ1",
				"-ycL1>TpXzZ297|ClAXkjLHC;O>+pXzZKf7T*#9t(P+##PP1EX@yW:AM<U++U++U++U+4#Mq#.E81+j1+jU+j1++##PPGE%;.yU:AW<Y^Ml^JSJDH.E851E/:AWd|.#XpRWD>LD<qX1cN.EJTRJHER3JRG_DYNNCJO-||5FBc<-Cq/:)*6&b*|JRFp-%W9(+1U8:-D>7MJpp*b>1X:GE*F.54Ed3_d._DHNCJ1-*|5FBc.Xcl8:/F+pX|*-AbDJ+Vc.bDXqq7EtPJHpGF_2XNCJ1;Tl|f#PP+9#(PH#MTYy-<Oc_KXq+#.EM1+j1+jU+jU+*##PPY>8F.EXRNCJ1",
				"V.F*/.LkRfdcVTc.c.RAW2yR<c35cV|SPc4CV.V5M6)%j.-*XVbG1Dl<bEVbqVc.b+TU.%@P5D.R<^c&NR&P*VjKLV#*E7V.|5FBc>)M5z.L&VRVb2R:*Oj8q*Rk(*7HbN&7Gb5VtST*U%ZR;CRfc@1&+-*|JRB34RVK|5FBc-c|VRHzZ(8;9KR1*RVVJYcc.Rd:U.D/.>9NRR*c.VtRp+Bc.Rc#SRcZR*KRWHERdt/cPP^c.fD%.R............cc.-*XVZ-<.Vp/VbcW6*-*XVf)VTcJV*.Sj.|).Y_R<PS#G;2Y<1Vq7_N&kc3_V>@ZlA+P4R5(6cR<:z&z",
				"cqc3f*7+(zZ2d9|C|+-z1SqcSf8(+-zZWy^t|O^@KK<@YK/GXc.>jU+j4<jj<jjjOj<OlYj<YD>jf#1j|1j|1j<|@YK/D>@7.>jU+jG@djtP5F52qy@MJf>jU+jDE4YbRR_WcczG|Sfbp4XJ#4JH>GT5G4k2NppckfS||5FBcG-cF_UjFt+7*|JR<+-c6YNt4AjUSz@^jJ@X#b8M)UD>k<G&HER/5..&WqpcJf-*|5FBc4bc(AUj<|+L|*-+3zJ(Vc.b2S(|9>YKpqX4t5z7pc&f-<^F@YK/t@YK/lOj|.X3.pcJf7<|d.>jf<1j|1j|1jt#YyKYqOj|4>b.pcJf",
				"*zZ*8^:kpYf%+F*fYCq||7C.*f5C+;HEYGd%qlU+C)fB+*YXfql4**DFcp*k*Yf%+dyf6Cq|A7C.*fZ3XfJWf|lC-Y*6J)qH;RKqG2.H.BlqJ+J+dYbG%f%&pfZJ+|H*Y+p*kz6f*+byf6Cq^fZ*/WE6*%*q5B&%ZJ+F7Zf%q6-L+f+f+fff++f+f)-Sc;.%qlU+J+f%*|WWfY6ylC.2qlR*B8byql2WFUZ^CfYU4q7|YG3d5dYq7Zf%qkdZpJ*WW)FC+|.&qlY*-C+b.+lRB-*cfU+)|C+d.+lYJ-yWkq))FBcF.clYC|:+|5F)*J+b.cVRJ|*C+|5FBc|HERG;",
				"7qc3f*7+(zZ2d9|C|+-z1SqcSf8(+-zZWy^t|O^@KK<@YK/GXc.>jU+j4<jj<jjjOj<OlYj<YD>jf#1j|1j|1j<|@YK/D>@7.>jU+jG@djtP5F52qy@MJf>jU+jDE4YbRR_WcczG|Sfbp4XJ#4JH>GT5G4k2NppckfS||5FBcG-cF_UjFt+7*|JR<+-c6YNt4AjUSz@^jJ@X#b8M)UD>k<G&HER/5..&WqpcJf-*|5FBc4bc(AUj<|+L|*-+3zJ(Vc.b2S(|9>YKpqX4t5z7pc&f-<^F@YK/t@YK/lOj|.X3.pcJf7<|d.>jf<1j|1j|1jt#YyKYqOj|4>b.pcJf",
				"*zZ*8^:kpYf%+Fyf6Cq||7C.*f5C+;AEYGd%qlU+C)fB+*YXfql4**DFcp*k*Yf%+dyf6Cq|A7C.*fZ3XfJWf|lC-Y*6J)qH;RKqG2.A.BlqJ+J8dYbG%f%&pfZJ+|A*Y+p*kz6f*+byf6Cq^fZ*/WE6*%*q5B&%ZJ+F7Zf%q6-Lf++fffff+++++)-Sc|.%qlU+J+f%*|/WfY6ylC.2qlR**+byql2WFUZ^CfYU%q7|YG3d5dYq7Zf%qkdZpJ*WW)FC+|.&qlY*-C+b.+lRB-*cfU8)|C+d.+lYJ-yWkq))FBcF.clYC|:+|5F)*J+b.cVRJ|*C+|5FBc|HERG;",
				"*zZ*8^:kpYf%c3yf6COb|7C.*f5C+;AE6Gd%OlU+C)fB+*YXfOl4**#Fcp*k*Yf%+dyf6CObH7C.*fZdXfJWfblC-Y*6C)OH<RKOG2.H.BlOC+J8dYbG%f4&pfZJ+|A*Y+p*kz6f*+byf6CO^fZ*WWE6*4*O5B&OZJ+F7Zf%O6-LU++fffffU++++)-Cc*.%OlU+C+f4*FWWfR6ylC.2OlM*J+byOl2WFUZ^CfYU%O7|YG3d536O7Zf%OkdZpJ*WW)FC+|.&%lY*-C+b.clRC-*8fU+)|J+d.+lYJ-yWkO))FBcF.clYC|:+|5F)*J+b.cVRJ|*C+|5FBcbHERT;",
				"*zZ)8^:k*_W%4b:W_JqFK7J.*WZJ4|**RGb%qlU4JGWB4pR+Wql%**Lbc^yk*_W%4*yWRYf*@7Y.*WZb-WJPWblJbR*RJGq%&R_qG2.K.YlqJ4J4b_bG%W%C*W*j4bHz_4*%k*_W%4byWRJq^WZ8PPE%A%*q5BCqZJ4F75W%q_-5W4WW4W4444W44G-J4b.%qlU4J4W**FPPW__MlJ.2fl_*Ycdyfl2PFUZpYWRU%q7b_G)d5b_q7ZW%q_dZ*J*PPGFJ4|.Cql_*-J4b.clRJ-*cWUcG|Y4F.cl_JbyPkqGLFBcF.cl_Y|M4|5FG*J4b.cVRJ|*Y4|5FBcbHERGb",
				"*zZ)8^:k*_W%cbyWRYqFK7J.*WZY4|**_Gb%qlU4JGWB4p_+Wql%**Lbc^yk*_W%4*yWRYf*@7J.*WZb-WJPWblJ*R*RJGq%&R_qG2.O.YljJ4B4b_bG%W%C*W*Y4FHz_4*%k*_W%4byWRbq^WZ8PPE%*%*q5BCqZJ4F75W%q_-5W44W4W4444W44G-J4|.%qlUcJ4W%*FPPW__MlJ.2ql_*Ycdyfl2PFUZpYWRU%q7b_G)d5b_q7ZW%q_bZ*Y*PPGFB4|.Cql_*-B4b.clRY-*cWUcG|Y4F.cl_JbyPkqGGFBcF.cl_B|M4|5FG*J4b.cVRJ|*Y4|5FBcbHERGd",
				"*zZ)8^:k*_W%4byW_JqFK7J.*WZJ4|**_Gb%qlU4JGWB4pR+Wql%**Lbc^yk*_W%4*yWRYf*@7J.*WZb-WJPWblJbR*RJGq%&R_qG2.K.YlqJ4J4b_bG%W%C*W*j4bHz_4*%k*_W%4byWRJq^WZ8PPE%*%*q5BCqZJ4F75W%q_-5W4WW4W4444W44G-J4b.%qlU4J4W**FPPW__MlJ.2ql_*Ycdyfl2PFUZpYWRU%q7b_G)d5b_q7ZW%q_dZ*J*PPGFJ4|.Cql_*-J4b.clRJ-*cWUcG|Y4F.cl_JbyPkqGLFBcF.cl_Y|M4|5FG*J4b.cVRJ|*Y4|5FBcbHERGb",
				"*zZ18^:k*yS%4*_SRYfFK7J.*SZB4|KERG)%qlU4JGSJ4*y+Sfl%**Lbc^_k*yS%45_SRYf*K7J.*SZF-SJWSblBbR^RJGq*&RyqG2.K.YlqJ4J4bybG%S%C*SZY4b*zR4*%k*yS%4b_SRYf^SZEWWE%*%*f5BCqZJ8F7ZS%qy-54S44Sc444S44SG-J4b.%flU4J4S%*dWWSRy1lY.2qlR*JczMql2WFUZOJSRU%q7byG)dZbyf7ZS%qydZ*J*WWGFY4|.Cqly*-Y4b.clRJ-*cSUcG|Y4F.clRJb_WkfGLFBcF.clyY|M4|5FG*J4b.cVRJ|*Y4|5FBcbHERGb",
				"*zZ18^:k*yS%4*_SRYfFK7J.*SZB4|KERG)%qlU4JGSJ4*y+Sfl%**Lbc^_k*yS%45_SRYf*K7J.*SZF-SJWSblBbR^RJGq*&RyqG2.K.YlqJ4J4bybG%S%C*SZY4b*zR4*%k*yS%4b_SRYf^SZEWWE%*%*f5BCqZJ8F7ZS%qy-54S44S4444S44SG-J4b.%flU4J4S%*dWWSRy1lY.2qlR*JczMql2WFUZOJSRU%q7byG)dZbyf7ZS%qydZ*J*WWGFY4|.Cqly*-Y4b.clRJ-*cSUcG|Y4F.clRJb_WkfGLFBcF.clyY|M4|5FG*J4b.cVRJ|*Y4|5FBcbHERGb"
		};
		for (int i=0; i<ciphers.length; i++) {
			String cipher = ciphers[i];
			boolean is340 = cipher.length() != 408;
			//double ioc = RepeatingFragmentsFaster.fragmentIOC(cipher, 6, is340 ? z340median : z408median, false);
			double ioc = RepeatingFragmentsFaster.fragmentIOC(cipher, 6, false);
			double mean = is340 ? z340iocMean : z408iocMean;
			double sd = is340 ? z340iocStdDev : z408iocStdDev;
			double sigma = (ioc-mean)/sd;
			System.out.println(ioc + "	" + sigma);
		}
	}
	public static void testForFragExplorer() {
		
		String[] ciphers = new String[] {
				"Z340 (original)",
				"HER>pl^VPk|1LTG2dNp+B(#O%DWY.<*Kf)By:cM+UZGW()L#zHJSpp7^l8*V3pO++RK2_9M+ztjd|5FP+&4k/p8R^FlO-*dCkF>2D(#5+Kq%;2UcXGV.zL|(G2Jfj#O+_NYz+@L9d<M+b+ZR2FBcyA64K-zlUV+^J+Op7<FBy-U+R/5tE|DYBpbTMKO2<clRJ|*5T4M.+&BFz69Sy#+N|5FBc(;8RlGFN^f524b.cV4t++yBX1*:49CE>VUZ5-+|c.3zBK(Op^.fMqG2RcT+L16C<+FlWB|)L++)WCzWcPOSHT/()p|FkdW<7tB_YOB*-Cc>MDHNpkSzZO8A|K;+",
				"Z340 (flipped horizontally)",
				"d2GTL1|kPV^lp>REH)fK*<.YWD%O#(B+pNJHz#L)(WGZU+Mc:yB2KR++Op3V*8l^7ppS/k4&+PF5|djtz+M9_(D2>FkCd*-OlF^R8p|Lz.VGXcU2;%qK+5#9L@+zYN_+O#jfJ2G(K46AycBF2RZ+b+M<d-yBF<7pO+J^+VUlz-OKMTbpBYD|Et5/R+UFB&+.M4T5*|JRlc<2R8;(cBF5|N+#yS96z++t4Vc.b425f^NFGl+-5ZUV>EC94:*1XBy2GqMf.^pO(KBz3.c|L)|BWlF+<C61L+TcRp)(/THSOPcWzCW)++cC-*BOY_Bt7<WdkF|+;K|A8OZzSkpNHDM>",
				"Z340 (untransposed period 19)",
				"H+M8|CV@K+l#2E.B)>EB+*5k.L-RR+4>f|pMR(UVFFz9z/JNbVM)|D>#Z3P>Ldl5||.UqLFHpOGp+2|<Ut*5cZG+kNl%WO&D(MVE5FV52+dp^D(+4(G++|TB4-R)WkVW)+k#2b^D4ct+cW<SPYLR/5J+JYM(+|TC7zk.#Kp+fZ+B.;+c+ztZ|<z28KjROp+8y.LWBO1*H_Rq#2pb&RB31c_8LKJ9^%OF7TBlXz6PYATfSMF;+B<MFG1BCOO|G)p+l2_cFKzF*K<SBK2BpzOUNyBO6N:(+H*;dy7t-cYAy29^4OFT-+N:^j*Xz6-<Sf9pl/CpclddG+4Ucy5C^W(c",
				"Z340 (flipped horizontally then untransposed period 15)",
				"dEB+*5k.L(MVE5FV52c+ztZ2H+M8|CV@K<Ut*5cZG|TC7zG)pclddG+4dl5||.UqLcW<STfN:^j*Xz6-z/JNbVM)R)WkLKJy7t-cYAy-RR+4>f|p+dp1*HBpzOUNyBO+l#2E.B)+kN|<z2p+l2_cFKUcy5C^W(cFHk.#KSMF;+B<MF<Sf9pl/C|DPYLR/9^%OF7TB29^4OFT-+MVW)+k_Rq#2pb&R6N:(+H*;>^D(+4(8KjROp+8zF*K<SBKl%WO&Dp+fZ+B.;+G1BCOO|pOGp+2|5J+JYM(+lXz6PYA>#Z3P>L#2b^D4ct+B31c_8R(UVFFz9G++|TB4-y.LWBO",
				"Z340 (untransposed column period 2 then linear period 18)",
				"H+M8|CV@Kz/JNbVM)|DR(UVFFz9<Ut*5cZG+kNpOGp+2|G++|TB4-R)Wk^D(+4(5J+JYM(+|TC7zPYLR/8KjROp+8y.LWBO|<z29^%OF7TBlXz6PYALKJp+l2_cFKzF*K<SBKG)y7t-cYAy29^4OFT-+dpclddG+4Ucy5C^W(cMEB+*5k.L-RR+4>f|pFH>#Z3P>Ldl5||.UqL+dpl%WO&D(MVE5FV52cW<SVW)+k#2b^D4ct+c+ztZk.#Kp+fZ+B.;+B31c_81*H_Rq#2pb&RG1BCOO|TfSMF;+B<MF6N:(+H*;2BpzOUNyBO<Sf9pl/CN:^j*Xz6-+l#2E.B)>",
				"Z408 (original)",
				"9%P/Z/UB%kOR=pX=BWV+eGYF69HP@K!qYeMJY^UIk7qTtNQYD5)S(/9#BPORAU%fRlqEk^LMZJdr\\pFHVWe8Y@+qGD9KI)6qX85zS(RNtIYElO8qGBTQS#BLd/P#B@XqEHMU^RRkcZKqpI)Wq!85LMr9#BPDR+j=6\\N(eEUHkFZcpOVWI5+tL)l^R6HI9DR_TYr\\de/@XJQAP5M8RUt%L)NVEKH=GrI!Jk598LMlNA)Z(PzUpkA9#BVW\\+VTtOP^=SrlfUe67DzG%%IMNk)ScE/9%%ZfAP#BVpeXqWq_F#8c+@9A9B%OT5RUc+_dYq_^SqWVZeGYKE_TYA9%#Lt_H!FBX9zXADd\\7L!=q_ed##6e5PORXQF%GcZ@JTtq_8JI+rBPQW6VEXr9WI6qEHM)=UIk",
				"Z408 Swap(178, 36, 14, 2) Swap(23, 313, 5, 6) Shift(0, 163)",
				"VZeGYKEk7qTtN#Lt_H!FBX9zPORAU%L!=q_ed##6edr\\pFHF%GcZ@JTtq_KI)6qXPQW6VEXr9WI6SrHM)=UIk9%P/Z/UB%kOR=pX=BWV+eGY+pOYq_K!qYeMJ+tUI_DRA9%QYD5)S(\\d#BXM8d\\7fRlqEk^L)ZJ5!JRXQVWe8Y@+LMD98pk+rB85zS(RNVWYElO8qGBTQS#BLd67#B@XqEHMU^RRkcZ%%pI)Wq!85LMr9#BP#8+j=6\\N(eEUHkFZc_dVWI5Y^L)l^R6HI9TY_TYr/9e/@XJQAP5ADRUt%LMNVEKH=GrIPOk598qGlNA)Z(PzUJIA9#BtI\\+VTtOP^=qElfUe/PDzG%%IMNk)ScE/9KqZfAP#BVpeXqWq_FDRc+@9A9B%OT5RUcF69HP@^SqW",
				"Z408 Swap(212, 19, 10, 2) Swap(23, 313, 5, 6) SwapLinear(46, 204, 151)",
				"9%P/Z/UB%kOR=pX=BWVL)GY+/PYq_K!qYeMJLMUI_KqA9%P5M8RUt%+eNVEKH=GrI!Jk598Y^lNA)Z(PzUpkA9#B/9\\+VTtOP^=SrlfUeLMDzG%%IMNk)ScE/9qGZfAP#BVpeXqWq_FtIc+@9A9B%OT5RUcF69HP@^SqWVZeGYKEk7qTtN#Lt_H!FBX9zPORAU%L!e/@XJQAQYD5)S(VW#BXDRd\\7fRlqEk^67ZJ5pORXQVWe8Y@+%%D98JI+rB85zS(RN#8YElO8qGBTQS#BLd_d#B@XqEHMU^RRkcZTYpI)Wq!85LMr9#BPAD+j=6\\N(eEUHkFZcPOVWI5+tL)l^R6HI9DR_TYr\\d=q_ed##6edr\\pFHF%GcZ@JTtq_KI)6qXPQW6VEXr9WI6qEHM)=UIk",
				"best ioc",
				"lz2Jf+KqBy:Np+HER4T5*F<pk/pK2_HJSz#ySc.b4t++5Vb/U+McG6<lz2RFOlBF5|N+&M(+TVc.b4t++^NF9G21*:49CE>VUZ5-+;WB|)L3zBK(Op^.fMqOSHT/()p+L16C<+Fl7tB_YOB*-CcWCzWcPHNpkSzZO8A|K;+dW<c|XByKB8f52RJ|Z^EpBc7DM>kF|)++TcR.A64K^R8RJ|2+DFOYBF5|djtz+M9By-U+Ry++Op3V*8l^7pp4&+P#jz#L)(WCd*-OlF-RcU2;%+@L9d<MYN_+OFkGZU+McG(|Lz.VGX*<.YWD%O#(B5#(D2>d2GTL1|kPV^lp>)fK",
				"LinearSelection(49, 28) FlipVertical() PeriodRow(2) LinearSelection(83, 0)",
				"EHG(ByR2X>J1pf*lj:^#4VO9P+Ck_E|N>1YVLzUT+ZG@52L-d9+Npd<|cBy-zRcSpU+++_92<|Fp8z6>M#5lGFN^f524b.cV4t+++Kq%;2UcXGV.zL|DHNpkSzZO8A|K;+9Sy#+N|5FBc(;8RR^FlO-*dCkF>2D(kdW<7tB_YOB*-CcclRJ|*5T4M.+&BFM+ztjd|5FP+&4k/)WCzWcPOSHT/()pR/5tE|DYBpbTMKOp7^l8*V3pO++RK2T+L16C<+FlWB|)LlUV+^J+Op7<FBy-:cM+UZGW()L#zHJ.3zBK(Op^.fMqG2M+b+ZR2FBcyA64K+B(#O%DWY.<*Kf)",
				"LinearSelection(87, 32) Diagonal(1) PeriodRow(3) Swap(4, 139, 8, 1) Swap(114, 19, 6, 2)",
				"HER>+l^VPk|1LTG2dSpB*Jl8*V3pO++RK2Cc<*/G>V2.Dz(L#|5-z+&N+^J+Op7<FBy-z6z++#+N|5FBc(;8R|cbTHBK(Op^.fMqG2|FV4c<7tB_YOp7-CcNp+B^#O%DWY.kXKf)_9Mpztjd|5FPlU4k/(G2^fj#O+_NY9S@L9U+RF5tE|DYBp.3MKOlGFV^f524b.ckdt++RcTyL16C<+FlWB|)L>MDzNpkSzZO8A|K;+By:WM+UZGW()L#zHJp8R(+FKlqO%-;*2dUd<M+b+ZR2FBcyA64K2<clRJ|*5T4M.+&BFyBX1*:49CE>VUZ5-+++)WCzWcPOSHT/()p",
				"Quadrants(13, 4, 1, 0, 1286 [0, 2, 2, 0, 0, 0, 1, 1, 0]) SwapLinear(96, 41, 2)",
				"HER>Np+BBy:cSpp7_9M+p8R^#5+K(G2Jd<M+-zlUUOp/2<clz69Sy#+N|5FBc(;8RRJ|*5T4M.+&BF5tE|DYBpbTMKOV+^J++R7<FBy-b+ZR2FBcyA64Kfj#O+_NYz+@L9q%;2UcXGV.zL|FlO-*dCkF>2D(ztjd|5FP+&4k/^l8*V3pO++RK2M+UZGW()L#zHJ(#O%DWY.<*Kf)pl^VPk|1LTG2dNpkSzZO8A|K;+W<7tB_YOB*-CcCzWcPOSHT/()pL16C<+FlWB|)LzBK(Op^.fMqG2*:49CE>VUZ5-+^f524b.cV4t++lGFNyBX1|c.3RcT+++)W|Fkd>MDH",
				"LinearSelection(131, 103) Quadrants(3, 4, 1, 1, 1647 [0, 3, 0, 3, 0, 1, 1, 1, 1]) LinearSelection(90, 30) Diagonal(1) SwapLinear(117, 40, 26) FlipVertical() PeriodRow(4)",
				">MDHNpkSzZO8A|K;+|c.3zBK(Op^.fMqG2|JRlc<2O8BKBF5|N+GW()L#zHJSpp7^l8*_NYL@+zp7c46AyBK2|FkdW<7tB_YOB*-CcyBX1*:49CE>VUZ5-+RlM+z<U-d-K9M4T5*XFG>V2.Dz(L#|5(UZBy:cM++KqG2Jfj#O+++)WCzWcPOSHT/()p^NFGlR(+TVc.b4t+++2|JRE^Zt++5Vb/U+p8R^FlO%-;*2dUCckNp+B(#O%DWY.<*Kf)RcT+L16C<+FlWB|)L#yS96zF;&Mc.b425fV3pO++RpByBF<YOFD_9M+ztjd|5FP+&4k/HER>pl^VPk|1LTG2d",
				"PeriodColumn(2) Period(18) Swap(5, 148, 6, 2) SwapLinear(36, 95, 25)",
				"H+M8|FT@Kz/JNbVM)|DR(UC^Fz9<Ut*5cZG+|<z29^%OF7TBlXz6PYALKJp+l4(5J+JYM(+|TMVzPYLR/8KjROp+8y#2WBOkNp+4p+2|G++|TB4-R)W5|D(+2_cFKzF*K<SBKG)y7t-cYAy29^4OCV-+dpclddG+4Ucy5VFW(cMEB+*5k.L-RROG>f|pFH>#Z3P>Ldlk^|.UqL+dpl%WO&D(C7E5FV52cW<SVW)+k.Lb^D4ct+c+ztZk.#Kp+fZ+B.;+B31c_81*H_Rq#2pb&RG1BCOO|TfSMF;+B<MF6N:(+H*;2BpzOUNyBO<Sf9pl/CN:^j*Xz6-+l#2E.B)>",
				"Shift(3, 18) Period(19) FlipHorizontal()",
				"KBS<K*FzKFc_2l+pJ*H+(:N6FByNUOzpB)TFO4^92yAYc-t7yN;lp9fS<O6zX*j^:pd-^C5ycU4+Gddlc+HC/E2#l+-@VC|8MBE+(W4+RR-L.k5*+(R>)B.NJ/zKzFFVU#>Mc|f>|5ldL>P3ZOpD|)MVbtU<92+pG%lHFpqU.|VM(D&OWD^Nk+GZc5*+G|4+(WVpd+L5VF5E2#k+)YPkW)R-4BT|+5(RL.kS<Wc2tc4D^bpK#<|z7CT|+(MYJ+J/z*1Ztz+c+;.B+Zf+HKLOBWL.y8+pORjK8fT8_c13B+&bp2#qR_GAYP6zXlBT7FO%^922|OOCB1GRM<B+;FMS",
				"Quadrants(13, 4, 1, 0, 1287 [0, 2, 2, 0, 0, 0, 1, 1, 1]) Swap(96, 41, 1, 3)",
				"HER>Np+BBy:cSpp7_9M+p8R^#5+K(G2Jd<M+-zlUUOp72<clz69Sy#+N|5FBc(;8RRJ|*5T4M.+&BF5tE|DYBpbTMKOV+^J++R/<FBy-b+ZR2FBcyA64Kfj#O+_NYz+@L9q%;2UcXGV.zL|FlO-*dCkF>2D(ztjd|5FP+&4k/^l8*V3pO++RK2M+UZGW()L#zHJ(#O%DWY.<*Kf)pl^VPk|1LTG2dNpkSzZO8A|K;+W<7tB_YOB*-CcCzWcPOSHT/()pL16C<+FlWB|)LzBK(Op^.fMqG2*:49CE>VUZ5-+^f524b.cV4t++NFGl1XBy3.c|+TcRW)++dkF|HDM>",
				"PeriodColumn(2) Period(18) Swap(5, 148, 6, 2)",
				"H+M8|FT@Kz/JNbVM)|DR(UC^Fz9<Ut*5cZG+kNp+4p+2|G++|TB4-R)W5|D(+4(5J+JYM(+|TMVzPYLR/8KjROp+8y#2WBO|<z29^%OF7TBlXz6PYALKJp+l2_cFKzF*K<SBKG)y7t-cYAy29^4OCV-+dpclddG+4Ucy5VFW(cMEB+*5k.L-RROG>f|pFH>#Z3P>Ldlk^|.UqL+dpl%WO&D(C7E5FV52cW<SVW)+k.Lb^D4ct+c+ztZk.#Kp+fZ+B.;+B31c_81*H_Rq#2pb&RG1BCOO|TfSMF;+B<MF6N:(+H*;2BpzOUNyBO<Sf9pl/CN:^j*Xz6-+l#2E.B)>",
				"LinearSelection(49, 28) FlipVertical() PeriodRow(2) LinearSelection(83, 0)",
				"HER>pl^VPk|1LTG2dBy:cM+UZGW()L#zK28*V3pO++R5FP+&4k/#5+Kq%;2UcXGV.zL|d<M+b+ZR2FBcyA64KU+R/5tE|DYBpbTMKOz69Sy#+N|5FBc(;8RyBX1*:49CE>VUZ5-+RcT+L16C<+FlWB|)L|FkdW<7tB_YOB*-CcNp+B(#O%DWY.<*Kf)_9M+ztjd|HJSpp7^lp8R^FlO-*dCkF>2D((G2Jfj#O+_NYz+@L9-zlUV+^J+Op7<FBy-2<clRJ|*5T4M.+&BFlGFN^f524b.cV4t++|c.3zBK(Op^.fMqG2++)WCzWcPOSHT/()p>MDHNpkSzZO8A|K;+",
				"smokie27d",
				"ABCDEFGHIJKLMNOPQCRSTIUVCWXQYZabBcORdefghUMCijJkAAHNlDCIminFKQTHoZCPbUpjCaCqnAhBGIrefQTJsRdWKtWEuvSFwCxOavdyWz0CNliblCF12OQuCbma3l1nX4Z5YC5HhCPWUKgRyICJeGLDZpSCuVQOCXkCo3HbmqZFPCTneairYgINfEMZlIxKR6JDmaPom73cwZfKSQpYbUFhZpJ3sINaComkZfGUn2WrjbVlCuHhURPIJp0NXmtgOEs4xc66zZltNayINCWRCuCTabJPOzNfHS2RlGmyhCigopRvwBeGomCfnDKXCQ1UarvQdyj6rl1XEFGv",
				"smokie27d best ioc",
				"xQfXJumgeZIgTHENImBWSnfsbbUbFJbHsCgCCCCCCGRa1ppPUbk40oLBDViPdTnQRFjxzpGEUj2ECXaIhozWRJaDJWWJ4rHXZ6lGeemsrtPCvGQH6iejna3lKOYZFYAUbByTPJVWz5DiZAbZwdJopCu2Z6rckjl2SKcNuvS5rY3IUt1oLBlqSHHTg7NgNFmmbDQFfhtImaCCCCCMCHONClQOCaNOfuEwhxR11PRFCIQXVfZZClKqOdnQyuKRNfkEGUnNeivaCDJCKmXaiQ3vCCCdhoOyMQrRUKIShnPTlyNW3RGRamFCfUoZYGhlpWAIAy06CKZpclvOMaIXwIPm",
				"smokie27d LinearSelection(20, 82) Period(25) LinearSelection(74, 7)",
				"ABCDEFGHIJKLMNOPQCRSTjpEIJjuUkCvVAaSCACFWHqwXNnCQlAYDhZCBaIGbmIBircneOFfRKQdQTeTJfHsgoRhZdUCWMPKCbtiUWxOavdyWz0CNliblCF12OQuCbma3l1nX4Z5YC5HhCPWUKgRyICJeGLDZpSCuVQOCXkCo3HbmqZFPCTneairYgINfEMZlIxKR6JDmaPom73cwZfKSQpYbUFhZpJ3sINaComkZfGUn2WrjbVlCuHhURPIJp0NXmtgOEs4xc66zZltNayINCWRCuCTabJPOzNfHS2RlGmyhCigopRvwBeGomCfnDKXCQ1UarvQdyj6rl1XEFGv",
				"smokie27d LinearSelection(31, 35) Diagonal(0) RectangularSelection(1, 12, 14, 4) Diagonal(0) LinearSelection(40, 85) Period(22)",
				"ABCDEFGHIJKLMNOPQCRSTIUVCWXQYZfaHBZAcHONRJfdlgQydATWDvJzeIs0CPRCIbdNbUWlGpKFkjtehCWyiaEXUCuFnqr1MnC2FAnOChbQKBCuioxCQmOjiaTSvbma3l1lwXI5YC5HhCPWUKgR4CikeGLDZpSCuVQOCCZDr3HbmqZFPCTneaCJbmINfEMZlIxKR6JYofUom73cwZfKSQpYagPGZpJ3sINaComkZFPIU2WrjbVlCuHhURhJnp0NXmtgOEs4xc66zZltNayINCWRCuCTabJPOzNfHS2RlGmyhCigopRvwBeGomCfnDKXCQ1UarvQdyj6rl1XEFGv"
				
		};
		
		for (int i=0; i<ciphers.length; i+=2) {
			cipherName = ciphers[i];
			String cipher = ciphers[i+1];
			double ioc = RepeatingFragmentsFaster.fragmentIOC(cipher, 6, true);
			System.out.println(ioc);
		}
	}
	
	/** test fragment IOC of all periods of the given cipher */
	public static void fragmentIOCAllPeriods(String cipher, int MAX) {
		for (int period=1; period<cipher.length()/2;period++) {
			String re = Periods.rewrite3(cipher, period);
			System.out.println(RepeatingFragmentsFaster.fragmentIOC(re, MAX, false, false) + " " + period + " " + re);
		}
		
		//System.out.println(RepeatingFragmentsFaster.fragmentIOC(, 6, false) + " " + period);
		
	}
	
	/** shuffle test */
	public static void testShuffles(String cipher, int N, int MAX) {
		StatsWrapper stats = new StatsWrapper();
		stats.actual = fragmentIOC(cipher, MAX, false, false);
		for (int i=0; i<N; i++) {
			cipher = CipherTransformations.shuffle(cipher);
			stats.addValue(fragmentIOC(cipher, MAX, false, false));
		}
		System.out.println(stats.header());
		stats.output();
	}
	
	static void test4() {
		String cipher = Periods.rewrite3(Ciphers.Z340, 19);
		for (int L=288; L<=340; L++) { 
			String sub = cipher.substring(0, L);
			for (int n=2; n<6; n++)
			//testShuffles(cipher, 10000, n);
			System.out.println(L + " " + n + " " + fragmentIOC(sub, n, false, false));
		}
	}
	public static void testFragmentIOC() {
		//String cipher = "YWXHZVFKTLJIPQPUHUSIEUNPXWTXHASLAADEOVXNDZJYRPUBKUGHSPXMOEPJTZITGQQRLXXERRJCJQPXICKJECPDZDMLUIDAHZRDFWUZPMXLSHUNJIXMDUENTZIIDSESYHBVEXSIUMPTZIJPDJNISEDERHHTYAQHEMTIRAICENTZMJXNDNVDBZLXTTLITCCDOOJSJESIIWPXJJNDUNPBBSLPSIAFRCRPAFIXRTKLQIRBIXSIWSRBEDANUUXAIDCTCEHYZOZARZRUXUHMCISUWEABIPDEJOZLERYTAERATETLZRZXAAOZKXIUWNPSYDZSIGPZJBZRKQLXSTSITYKKXHGXDWIXPRXOVRQTQFXXIVJOSBZDNOPWLFLNIOP";
		//String cipher = "dG2)LTKfJ|1<*zH2PkY.L#RK/^VDW()++4k(plO%GWpO+&2D|R>(#UZV3FPF>zL9HE+BM+8*|5CkV.@LKNp:c^ljd*dXGz+64-Byp7ztO-UcNYyAByOSpM+Fl;2+_Bc<FMKF_9R^q%#O2Fp7bT&BRp8+KfjZR+OBp.+;8+#52Jb+^JDY4Mc(t++(GM+V+E|5TFBV45-2d<lU5t|*|5.cUZqGL-zR/RJ+N4b>VfM|)pU+cly#52CE^.WB()c2<9S^f49OpFlT/-C+z6FN*:K(<+SHB*K;lGX1zB6CPOYOA|yB.3L1WcB_O8|cT+Cz7tzZRc)WW<kS++kdNp|FDH>M";
		String cipher = Ciphers.Z408;
//		String cipher = "WEOZQXZEDWDCEGDOD+RMQImJWPWSOYXRZLzQJRFPERsSJVEULDROXLMVJRFLRTLITITQVLYVHOtGIBLAHJYRNZJXPOLPGYMFFmDEDWLGLESURELMACRCACTNOLWCAnIzRTAOTHSRNUKAFOVIITFLPXXAKLRZFMGJSFULPtQWHIZVASTr181l861r3MERGUITAVOY625ISR50";
		cipher = "HH*1W%lcpdz<|GOp:N2#.kZ#>y)GLYPU(RBfT)WV+BEJKL(D^M+S4+Gddl+G|2+p2#qR_KAYc-t7<9zFFVZf+pk+c_2l+pK@VC|8b2#D&OFO%^926zX*j^M(L>P3RjK8/RyNUOzpdL.k5*+J5(4+B+;FM-)B.E2#l+-(W^C5ycUy/lp9fS<OBTFO4^92KFH+(:N6FM<S<K*FzBT7OCB1GR&bpP6zXl8+pOc13B+;.B+WL.y+(MYJz+c+tc4D^CT|-4BT|+Wc25VF5EV)RGZc5*tU+LqU.||5l+)MVbNJ/zp|f>4+RR|;*8_S<HF+-AYz7Nk>C|OZtpdMcKBOBkWD"; 
		double ioc = fragmentIOC(cipher, 6, false, true);
		System.out.println("ioc: " + ioc);
	}
	public static void testFragmentIOCBatch() {
		//String cipher = "kAMS|DzKHZ;NO+p8>|+R|ylz2U-d(#p_SBNH7BFt*kB-d_CWYc<OHl.VcBMp7cYGkPO).1WT+c/)P(WO)CSpz1B:f#Jt++j%ltl+#l6WcCBT<|++)LFL2++RFO-K9|(/2J)dKfc(M.Oq3pGz^>.F4BpBNXCFp(Y|4UB9ZXC51E-*^yR5VbfqFz^M(p5VG24F4tNb+8BKy4LLDkKHf2+c6N(9|;S5TYOF_cd53WWk|.<*+c5&l/U+JK^+7cB>Eb+|TRDMB6@z24RzKG^<zJFl+2+U*|VGDPZy<RAM2+RMp:+R#zGO+.>&+#*T;V52-d*Z%VOF89pypEj++L<L8UO^";
//		String cipher = Ciphers.Z340;
		
		String[] names = new String[] { "z408 first 340 shuffled", "z408 first 340", "z340 shuffled", "z340", "z340 p19", "z340_ngrams_28_05_01_col14_blocksize04_02_hvrlbt4_rowwise.txt",
				"z340_ngrams_23_02_00_col08_blocksize04_02_hvlrbt_rowwise.txt",
				"z340_ngrams_28_05_01_col14_blocksize04_02_hvrlbt4_rowwise.txt",
				"z340_ngrams_23_02_00_col08_blocksize04_02_hvlrbt_rowwise.txt",
				"z340_ngrams_26_02_01_col07_blocksize04_04_rllratb_tr_colwise.txt",
				"z340_ngrams_26_02_01_col06_blocksize04_04_rllratb_tr_colwise.txt",
				"z340_ngrams_24_02_01_col09_blocksize04_04_rllratb_tr_colwise.txt",
				"z340_ngrams_24_02_01_col08_blocksize04_04_rllratb_tr_colwise.txt",
				"z340_ngrams_27_03_01_col05_blocksize04_04_rllratb_tr_colwise.txt",
				"2s_z340_ngrams_32_04_00_del10_decimate01_02.txt", "2s_z340_ngrams_32_04_00_del10_decimate18_15.txt",
				"z340_ngrams_28_05_01_col14_blocksize04_02_hvrlbt4.txt",
				"z340_ngrams_23_02_00_col08_blocksize04_02_hvlrbt.txt",
				"z340_ngrams_30_03_00_reshape19_del09_tr_rlrllrlrabt.txt",
				"z340_ngrams_32_04_01_del03_del09_decimate01_02.txt",
				"z340_ngrams_35_04_00_reshape19_del09_tr_rltb.txt",
				"z340_ngrams_29_03_02_reshape23_del09_decimate01_19.txt",
				"z340_ngrams_32_05_00_del01_del09_decimate01_02.txt",
				"z340_ngrams_31_03_00_reshape19_del09_tr_lrrltb.txt",
				"z340_ngrams_32_04_00_decimate19_del12_decimate18_02.txt",
				"z340_ngrams_30_03_00_del10_del18_decimate17_15.txt",
				"z340_ngrams_38_04_00_reshape19_del09_tr_rlatb.txt",
				"z340_ngrams_33_05_01_del01_del02_decimate17_15.txt",
				"z340_ngrams_36_04_00_reshape19_del09_tr_rlabt.txt",
				"z340_ngrams_29_03_02_reshape23_del09_decimate12_04.txt",
				"z340_ngrams_31_03_00_reshape19_del09_tr_rlrllrlratb.txt",
				"z340_ngrams_36_04_00_reshape19_del09_tr_lrtb.txt",
				"z340_ngrams_33_05_01_del01_del02_decimate01_02.txt",
				"z340_ngrams_32_04_00_per09_del02_decimate02_02.txt",
				"z340_ngrams_30_03_00_reshape19_del09_tr_lrrlbt.txt",
				"z340_ngrams_30_03_00_del10_del18_decimate01_02.txt",
				"z340_ngrams_33_04_01_per05_del18_decimate15_15.txt",
				"z340_ngrams_38_04_01_per09_del07_decimate02_02.txt",
				"z340_ngrams_32_04_01_del03_del09_decimate17_15.txt",
				"z340_ngrams_31_05_00_del01_del09_decimate17_15.txt",
				"z340_ngrams_32_04_00_per09_del02_decimate17_15.txt",
				"z340_ngrams_36_04_00_reshape19_del09_tr_rlbt.txt",
				"z340_ngrams_31_03_00_reshape19_del09_tr_lrlrrlrlabt.txt",
				"z340_ngrams_31_03_00_reshape19_del09_tr_rllrbt.txt",
				"z340_ngrams_37_04_00_reshape19_del09_tr_lratb.txt",
				"z340_ngrams_38_04_01_per09_del07_decimate17_15.txt",
				"z340_ngrams_31_02_01_del03_del08_decimate17_15.txt",
				"z340_ngrams_31_02_01_del03_del08_decimate01_02.txt",
				"z340_ngrams_35_04_00_reshape19_del09_tr_lrbt.txt",
				"z340_ngrams_30_03_00_reshape19_del09_tr_rllrtb.txt",
				"z340_ngrams_30_03_00_reshape19_del09_tr_lrlrrlrlatb.txt",
				"z340_ngrams_33_04_01_per05_del18_decimate04_02.txt",
				"z340_ngrams_38_04_00_reshape19_del09_tr_lrabt.txt",
				"z340_ngrams_31_05_02_reshape31_reshape05_hvrltb.txt",
				"z340_ngrams_32_04_00_decimate19_del12_decimate01_15.txt",
				"z340_ngrams_32_04_00_del10_decimate01_02.txt", "z340_ngrams_22_01_00_tr_reshape11_decimate11_07.txt",
				"z340_ngrams_22_01_00_tr_reshape11_decimate19_04.txt", "z340_ngrams_37_04_01_del03_decimate18_15.txt",
				"z340_ngrams_37_04_01_del03_decimate01_02.txt", "z340_ngrams_32_04_00_del10_decimate18_15.txt" };
		String[] ciphers = new String[] {
				CipherTransformations.shuffle(Ciphers.Z408).substring(0, 340), Ciphers.Z408.substring(0, 340),
				CipherTransformations.shuffle(Ciphers.Z340), Ciphers.Z340, Periods.rewrite3(Ciphers.Z340, 19),
				"SpypEBNHp7cB>:+R^l+#lM(p8*Z%VUO^V3WWkGDPpO).1(Y|T*#++RzKGL<LK2J)dHf2(G589#p_2JK^++RMfj%ltqFz#O2-d;Oj+_cd5U*|NYGkPXCF&>.+z@z24VF+L9|(/LDk2<+z<U-dcl/U+RlMRJt++5Vb|*|JRE^Z5TYOFD+24Mp7cBpBAFT+.&MB6b<yBFO-KKy4|cBG6ylz.31NSXF9zB:f#*^yK(92N45+OpEb5C4|^.VcB>.F(4ZMfq5t;UVcG2++R-+8>MF+c|+RDHdW+k)TNp<z1WCLkStcC7W6zZ_O+BP<O8OHlYSFB/*|AK-(|BTW;+cpLC))",
				"SpBNHypEp7:+RcB>^lM(p+#lV%Z*8VUO^GDP3pWWk(Y|O+).1L<L+R#*TzKGK2Hf2J)d(G#p_5892J+RMK^+fjqFz%ltd-2O#+;OjU*|_Ncd5XCFYzGkPVF++@.>&z24L9LDk|(/2<U-d+z<clRlM/U+RJ5Vbt++RJ|*|5E^ZD+2T4YOFBpBM.p7cb<y+&TFAMB6BFKy4O-K|cylzBG6.3XF91NSzB*^y:f#N29(KO45+C4|p^Eb5>.F.fVcBUVcMqZ4(5t;G2-+8++R>M|+RF+cDHk)TdW+NpWCL<z1CctSkz7W6BP<ZO_O+YSF8AOHlBTW|K*/B-(|;+C))cpL",
				"SpypEBNHp7cB>:+R^l+#lM(p8*Z%VUO^V3WWkGDPpO).1(Y|T*#++RzKGL<LK2J)dHf2(G589#p_2JK^++RMfj%ltqFz#O2-d;Oj+_cd5U*|NYGkPXCF&>.+z@z24VF+L9|(/LDk2<+z<U-dcl/U+RlMRJt++5Vb|*|JRE^Z5TYOFD+24Mp7cBpBAFT+.&MB6b<yBFO-KKy4|cBG6ylz.31NSXF9zB:f#*^yK(92N45+OpEb5C4|^.VcB>.F(4ZMfq5t;UVcG2++R-+8>MF+c|+RDHdW+k)TNp<z1WCLkStcC7W6zZ_O+BP<O8OHlYSFB/*|AK-(|BTW;+cpLC))",
				"SpBNHypEp7:+RcB>^lM(p+#lV%Z*8VUO^GDP3pWWk(Y|O+).1L<L+R#*TzKGK2Hf2J)d(G#p_5892J+RMK^+fjqFz%ltd-2O#+;OjU*|_Ncd5XCFYzGkPVF++@.>&z24L9LDk|(/2<U-d+z<clRlM/U+RJ5Vbt++RJ|*|5E^ZD+2T4YOFBpBM.p7cb<y+&TFAMB6BFKy4O-K|cylzBG6.3XF91NSzB*^y:f#N29(KO45+C4|p^Eb5>.F.fVcBUVcMqZ4(5t;G2-+8++R>M|+RF+cDHk)TdW+NpWCL<z1CctSkz7W6BP<ZO_O+YSF8AOHlBTW|K*/B-(|;+C))cpL",
				"SBNH>Bc7pypER+:p(#p_+^KJG589MR+22U-d+U/l<+z<MlRc|ylzSN13cBG69FX.>|+R+WdHMF+cT)kD^OU8jO;#Z^E|+54K6W7k^M(pPDGVl+#lV%Z*fqFz|*U+j%ltd-2OR5Vb2+D5Jt++RJ|*z*^y|4COB:f#N29(NWCL<PBzp<z1CctS3WWkL<L+p(Y|1.)O_cd5+FVzNXCFPkGYTYOFy<b.4BpBc7pMpEb5cVUf^>.FBcV.Z_O+WTBAOYSFlHO8+#*Td)J2RzKG2fHK+.>&/(|9@z24kDLL+TFAK-OF&MB64yKBMZ4(R++2q5t;8+-G|*/BLpc+K-(|))C;",
				"SBNH>Bc7pypER+:p(#p_+^KJG589MR+22U-d+U/l<+z<MlRc|ylzSN13cBG69FX.>|+R+WdHMF+cT)kDl#+ltl%j++tJ#f:B1z<p^M(pPDGV8UO^V%Z*fqFz|*U+#;Ojd-2OR5Vb2+D5|E^ZRJ|*z*^y|4COK45+N29(NWCL<PBzk7W6CctS3WWkL<L+p(Y|1.)O_cd5+FVzNXCFPkGYTYOFy<b.4BpBc7pMpEb5cVUf^>.FBcV.Z_O+WTBAOYSFlHO8+#*Td)J2RzKG2fHK+.>&/(|9@z24kDLL+TFAK-OF&MB64yKBMZ4(R++2q5t;8+-G|*/BLpc+K-(|))C;",
				"SBNH>Bc7pypER+:p(#p_+^KJG589MR+22U-d+U/l<+z<MlRc|ylzSN13cBG69FX.>|+R+WdHMF+cT)kD^M(pV%Z*l+#l^OU8fqFzd-2Oj%ltjO;#R5VbRJ|*Jt++Z^E|z*^yN29(B:f#+54KNWCLCctSp<z16W7kPDGV|*U+2+D5|4CO<PBz3WWkL<L+p(Y|1.)O_cd5+FVzNXCFPkGYTYOFy<b.4BpBc7pMpEb5cVUf^>.FBcV.Z_O+WTBAOYSFlHO8+#*Td)J2RzKG2fHK+.>&/(|9@z24kDLL+TFAK-OF&MB64yKBMZ4(R++2q5t;8+-G|*/BLpc+K-(|))C;",
				"SBNH>Bc7pypER+:p(#p_+^KJG589MR+22U-d+U/l<+z<MlRc|ylzSN13cBG69FX.>|+R+WdHMF+cT)kDV%Z*d-2ORJ|*N29(CctS^M(pPDGVl+#l^OU8fqFz|*U+j%ltjO;#R5Vb2+D5Jt++Z^E|z*^y|4COB:f#+54KNWCL<PBzp<z16W7k3WWkL<L+p(Y|1.)O_cd5+FVzNXCFPkGYTYOFy<b.4BpBc7pMpEb5cVUf^>.FBcV.Z_O+WTBAOYSFlHO8+#*Td)J2RzKG2fHK+.>&/(|9@z24kDLL+TFAK-OF&MB64yKBMZ4(R++2q5t;8+-G|*/BLpc+K-(|))C;",
				"SBNH>Bc7pypER+:p(#p_+^KJG589MR+22U-d+U/l<+z<MlRc|ylzSN13cBG69FX.>|+R+WdHMF+cT)kDp(M^zFqfbV5Ry^*zLCWNl+#lPDGV8UO^V%Z*j%lt|*U+#;Ojd-2OJt++2+D5|E^ZRJ|*B:f#|4COK45+N29(p<z1<PBzk7W6CctS3WWkL<L+p(Y|1.)O_cd5+FVzNXCFPkGYTYOFy<b.4BpBc7pMpEb5cVUf^>.FBcV.Z_O+WTBAOYSFlHO8+#*Td)J2RzKG2fHK+.>&/(|9@z24kDLL+TFAK-OF&MB64yKBMZ4(R++2q5t;8+-G|*/BLpc+K-(|))C;",
				"H+M8|CV@K+l#2E.B)|DpOGp+2|G+t*5cZGR)WkPYLR/8KjRYM(+y.LWBOLKJp+l2_cTBzF*K<SBKdpclddG+4Ucy5C^W(cM>#Z3P>L(M5||.UqL+dpVW)+kp+fZD4ct+c+ztZ1*HSMF;+Bb&RG1BCOO|2N:^j*Xz6O<Sf9pl/C>R(UVFFz9</JNbVM)+kN^D(+4(5J+|TB4-|TC7z|<z29^%OFp+8lXz6PYAG)y7t-cYAK29^4OFT-+EB+*5k.LdRR+4>f|pFHl%WO&D#2bE5FV52cW<Sk.#K_Rq#2B.;+B31c_8TfBpzOUNyMF6N:(+H*;",
				"H;*H+(:N6FMyNUOzpBfT8_c13B+;.B2#qR_K#.kS<Wc25VF5Eb2#D&OW%lHFp|f>4+RRdL.k5*+BE+-TFO4^92KAYc-t7y)GAYP6zXl8+pFO%^92z<|z7CT|-4BT|+J5(4+(D^Nk+)MVbNJ/<9zFFVU(R>C/lp9fS<O6zX*j^:N2|OOCB1GR&bB+;FMSH*1Ztz+c+tc4DZf+pk+)WVpd+LqU.||5M(L>P3Z#>Mc(W^C5ycU4+GddlcpdKBS<K*FzBTc_2l+pJKLOBWL.y+(MYRjK8/RLYPkW)RGZc5*t+G|2+pGOpD|)B.E2#l+K@VC|8M+",
				"SpypEBNHp7cB>:+R^l+#lM(p8*Z%VUO^V3WWkGDPpO).1(Y|T*#++RzKGL<LK2J)dHf2(G589#p_2JK^++RMfj%ltqFz#O2-d;Oj+_cd5U*|NYGkPXCF&>.+z@z24VF+L9|(/LDk2<+z<U-dcl/U+RlMRJt++5Vb|*|JRE^Z5TYOFD+24Mp7cBpBAFT+.&MB6b<yBFO-KKy4|cBG6ylz.31NSXF9zB:f#*^yK(92N45+OpEb5C4|^.VcB>.F(4ZMfq5t;UVcG2++R-+8>MF+c|+RDHdW+k)TNp<z1WCLkStcC7W6zZ_O+BP<O8OHlYSFB/*|AK-(|BTW;+cpLC))",
				"SpBNHypEp7:+RcB>^lM(p+#lV%Z*8VUO^GDP3pWWk(Y|O+).1L<L+R#*TzKGK2Hf2J)d(G#p_5892J+RMK^+fjqFz%ltd-2O#+;OjU*|_Ncd5XCFYzGkPVF++@.>&z24L9LDk|(/2<U-d+z<clRlM/U+RJ5Vbt++RJ|*|5E^ZD+2T4YOFBpBM.p7cb<y+&TFAMB6BFKy4O-K|cylzBG6.3XF91NSzB*^y:f#N29(KO45+C4|p^Eb5>.F.fVcBUVcMqZ4(5t;G2-+8++R>M|+RF+cDHk)TdW+NpWCL<z1CctSkz7W6BP<ZO_O+YSF8AOHlBTW|K*/B-(|;+C))cpL",
				"c(W^C5yc4+Gddlcp)B.E2#l+@VC|8M+HN:^j*Xz6<Sf9pl/CEB+*5k.LRR+4>f|p-TFO4^92AYc-t7yd|)MVbNJ/9zFFVU(R2BpzOUNyO6N:(+H*>#Z3P>Ld5||.UqLFBS<K*FzKc_2l+p)Gk+GZc5*t<|2+pGOpTfSMF;+BMFG1BCOOl%WO&D(ME5FV52+dYP6zXlBTFO%^9JKLW)R-4BT|+G(4+(D^1*H_Rq#2b&RB31c_VW)+k#2bD4ct+cW<BWL.y8+pRjK82z<|7CT|+(MY+J5/RLYPk.#Kp+fZB.;+c+zt",
				"H+^j*Xz6O<Sf9pl/C>R(8|CV@K+l#2E.B)|DpOVFFz9</JNbVM)+kN^Dp+2|G+t*5cZGR)WkPY+4(5J+|TB4-|TC7z|<R/8KjRYM(+y.LWBOLK29^%OFp+8lXz6PYAG)p+l2_cTBzF*K<SBKdp7t-cYAK29^4OFT-+EBlddG+4Ucy5C^W(cM>#*5k.LdRR+4>f|pFHl%3P>L(M5||.UqL+dpVWO&D#2bE5FV52cW<Sk.+kp+fZD4ct+c+ztZ1*K_Rq#2B.;+B31c_8TfSMF;+Bb&RG1BCOO|2NpzOUNyMF6N:(+H*;",
				")B.E2#l+@VC|8M+Hp|f>4+RRL.k5*+BE|)MVbNJ/9zFFVU(RFLqU.||5dL>P3Z#>k+GZc5*t<|2+pGOpd+25VF5EM(D&OW%lW)R-4BT|+G(4+(D^<Wc+tc4Db2#k+)WV7CT|+(MY+J5/RLYPtz+c+;.BZf+pK#.kBWL.y8+pRjK82z<|_c13BR&b2#qR_H*1YP6zXlBTFO%^9JKLOOCB1GFMB+;FMSfTBS<K*FzKc_2l+p)G*H+(:N6OyNUOzpB2-TFO4^92AYc-t7ydC/lp9fS<6zX*j^:Nc(W^C5yc4+Gddlcp",
				"HGp+2|JYlXz6-N:^j*9z/B4-WB1*HOUNyB#ZG+kl%Kp+fZ+f9pl/#Z3P>L^DRB31*dy7t-L-RFV+zt|<zl2_cFyUqLFpOR/5J+T^4OFT(UVFFz+|8y.LB2BpzV@K+5cTC7k.#F;+B<SVM)|>)+k#2bbN:(+HB+*5k.VE;+cOOG)p+G+4U|.cW<PYL^%OF74>f|pR(+4(G+pF*K<S+M8|C<Ut(+|PYTfSMXz6-NbR)WVW_Rq#2p2E.B)EWO&D(MBG1BCCpcldddl5ct+c_LKJ9cYAy+52+d^D28KjRO5C^W(",
				"N:^j*Xz6O<Sf9pl/C>+M8|CV@K+l#2E.B)|D(UVFFz9</JNbVM)+kNOGp+2|G+t*5cZGR)WkD(+4(5J+|TB4-|TC7zYLR/8KjRYM(+y.LWBO<z29^%OFp+8lXz6PYAKJp+l2_cTBzF*K<SBK)y7t-cYAK29^4OFT-+pclddG+4Ucy5C^W(cMB+*5k.LdRR+4>f|pFH#Z3P>L(M5||.UqL+dp%WO&D#2bE5FV52cW<SW)+kp+fZD4ct+c+ztZ.#K_Rq#2B.;+B31c_8*HSMF;+Bb&RG1BCOO|fBpzOUNyMF6N:(+H*;",
				"H+M8|CV@+l#2E.B)p|f>4+RRL.k5*+BER(UVFFz9/JNbVM)|FLqU.||5dL>P3Z#>pOGp+2|<t*5cZG+kd+25VF5EM(D&OW%l^D(+4(G+|TB4-R)W<Wc+tc4Db2#k+)WVPYLR/5J+YM(+|TC7tz+c+;.BZf+pK#.k|<z28KjRp+8y.LWB_c13BR&b2#qR_H*1LKJ9^%OFTBlXz6PYOOCB1GFMB+;FMSfTG)p+l2_cKzF*K<SB*H+(:N6OyNUOzpB2dy7t-cYA29^4OFT-C/lp9fS<6zX*j^:NpclddG+4cy5C^W(c",
				"H+M8|CV@K+l#2E.B)|DpOGp+2|G+t*5cZGR)WkPYLR/8KjRYM(+y.LWBOLKJp+l2_cTBzF*K<SBKdpclddG+4Ucy5C^W(cM>#Z3P>L(M5||.UqL+dpVW)+kp+fZD4ct+c+ztZ1*HSMF;+Bb&RG1BCOO|2N:^j*Xz6O<Sf9pl/C>R(UVFFz9</JNbVM)+kN^D(+4(5J+|TB4-|TC7z|<z29^%OFp+8lXz6PYAG)y7t-cYAK29^4OFT-+EB+*5k.LdRR+4>f|pFHl%WO&D#2bE5FV52cW<Sk.#K_Rq#2B.;+B31c_8TfBpzOUNyMF6N:(+H*;",
				"H;/lp9fS<O6zX*j^:N2|H+(:N6FMyNUOzpBfT8OCB1GR&bB+;FMSH*1Zc13B+;.B2#qR_K#.kSz+c+tc4DZf+pk+)WVpWc25VF5Eb2#D&OW%lH+LqU.||5M(L>P3Z#>Mp|f>4+RRdL.k5*+BE+(W^C5ycU4+GddlcpdKTFO4^92KAYc-t7y)GAS<K*FzBTc_2l+pJKLOP6zXl8+pFO%^92z<|zWL.y+(MYRjK8/RLYPkCT|-4BT|+J5(4+(D^N)RGZc5*t+G|2+pGOpD+)MVbNJ/<9zFFVU(R>)B.E2#l+K@VC|8M+",
				")B.E2#l+@VC|8M+Hc(W^C5yc4+Gddlcpp|f>4+RRL.k5*+BEC/lp9fS<6zX*j^:N|)MVbNJ/9zFFVU(R-TFO4^92AYc-t7ydFLqU.||5dL>P3Z#>*H+(:N6OyNUOzpB2k+GZc5*t<|2+pGOpBS<K*FzKc_2l+p)Gd+25VF5EM(D&OW%lOOCB1GFMB+;FMSfTW)R-4BT|+G(4+(D^YP6zXlBTFO%^9JKL<Wc+tc4Db2#k+)WV_c13BR&b2#qR_H*17CT|+(MY+J5/RLYPBWL.y8+pRjK82z<|tz+c+;.BZf+pK#.k",
				"N;*H+(:N6FM<B+;FMSf|OOCB1GR&bp2#qR_K*8_c13B+;.B+Zf+pk+.Ztz+c+tc4D^b2#D&OWS<Wc25VF5EVM(L>P3%pd+LqU.||5ldL.k5*#HFp|f>4+RR-4+GddlBMc(W^C5ycUyAYc-t7p+-TFO4^92KFc_2l+p)KBS<K*FzBT7FO%^92KAYP6zXl8+pORjK8/R<OBWL.y+(MYJ+J5(4+Yz7CT|-4BT|++G|2+pDkW)RGZc5*tU<9zFFVONk+)MVbNJ/zK@VC|8(D|)B.E2#l+-6zX*j^+>C/lp9fS<OByNUOzp",
				"c(W^C5yc4+Gddlcp)B.E2#l+@VC|8M+HC/lp9fS<6zX*j^:Np|f>4+RRL.k5*+BE-TFO4^92AYc-t7yd|)MVbNJ/9zFFVU(R*H+(:N6OyNUOzpB2FLqU.||5dL>P3Z#>BS<K*FzKc_2l+p)Gk+GZc5*t<|2+pGOpOOCB1GFMB+;FMSfTd+25VF5EM(D&OW%lYP6zXlBTFO%^9JKLW)R-4BT|+G(4+(D^_c13BR&b2#qR_H*1<Wc+tc4Db2#k+)WVBWL.y8+pRjK82z<|7CT|+(MY+J5/RLYPtz+c+;.BZf+pK#.k",
				"H(W^C5ORjK82D^d+25+yAYc9JKL_c+tc5ldddlcpCCB1GBM(D&OWE)B.E2p2#qR_WVW)RbN-6zXMSfTYP|+(tU<C|8M+S<K*Fp+G(4+(Rp|f>47FO%^LYP<Wc.|U4+G+p)GOOc+;EV.k5*+BH+(:Nbb2#k+)>|)MVS<B+;F#.k7CTc5+K@VzpB2BL.y8|+zFFVU(TFO4^T+J5/ROpFLqUyFc_2lz<|tz+VFR-L-t7yd*13BRD^L>P3Z#/lp9f+Zf+pK%lk+GZ#ByNUOH*1BW-4B/z9*j^:N-6zXlYJ|2+pG",
				")B.E2#l+@VC|8M+Hc(W^C5yc4+GddlcpEB+*5k.LRR+4>f|pN:^j*Xz6<Sf9pl/C|)MVbNJ/9zFFVU(R-TFO4^92AYc-t7yd>#Z3P>Ld5||.UqLF2BpzOUNyO6N:(+H*k+GZc5*t<|2+pGOpBS<K*FzKc_2l+p)Gl%WO&D(ME5FV52+dTfSMF;+BMFG1BCOOW)R-4BT|+G(4+(D^YP6zXlBTFO%^9JKLVW)+k#2bD4ct+cW<1*H_Rq#2b&RB31c_7CT|+(MY+J5/RLYPBWL.y8+pRjK82z<|k.#Kp+fZB.;+c+zt",
				"H+M8|CV@+l#2E.B)EB+*5k.LRR+4>f|pR(UVFFz9/JNbVM)|>#Z3P>Ld5||.UqLFpOGp+2|<t*5cZG+kl%WO&D(ME5FV52+d^D(+4(G+|TB4-R)WVW)+k#2bD4ct+cW<PYLR/5J+YM(+|TC7k.#Kp+fZB.;+c+zt|<z28KjRp+8y.LWB1*H_Rq#2b&RB31c_LKJ9^%OFTBlXz6PYTfSMF;+BMFG1BCOOG)p+l2_cKzF*K<SB2BpzOUNyO6N:(+H*dy7t-cYA29^4OFT-N:^j*Xz6<Sf9pl/CpclddG+4cy5C^W(c",
				"NpzOUNyBO<Sf9pl/C>+^j*Xz6-+l#2E.B)|D(8|CV@Kz/JNbVM)+kNOVFFz9<Ut*5cZGR)WkDp+2|G++|TB4-|TC7zY+4(5J+JYM(+y.LWBO<R/8KjROp+8lXz6PYAK29^%OF7TBzF*K<SBK)p+l2_cFK29^4OFT-+p7t-cYAyUcy5C^W(cMBlddG+4-RR+4>f|pFH#*5k.Ldl5||.UqL+dp%3P>L(MVE5FV52cW<SWO&D#2b^D4ct+c+ztZ.+kp+fZ+B.;+B31c_8*K_Rq#2pb&RG1BCOO|fSMF;+B<MF6N:(+H*;",
				"H+N||.UqL+dtZ3P>L(MpOz4ct+c+ztY)+kp+fZPYA&RG1BCOOTHSMF;+BLK+<Sf9pl/CU:^j*Xz6dpHJNbVM)+k5UVFFz9<>#STB4-|TC7D(+4(5J+VW8+8lXz6PYbz29^%OF1*;29^4OFT-Oy7t-cYA2NDR+4>f|pF/+*5k.LdR(k5FV52cW<|WO&D#2b^DO.;+B31c_p#K_Rq#2|<KF6N:(+H*KBpzOUNyG)Ml#2E.B)|RM8|CV@KEBp*5cZGR)WEGp+2|G+l%ZM(+y.LWBBLR/8KjRk.|BzF*K<SBMJp+l2_cTf>cy5C^W(c+clddG+4",
				"pclddG+4cy5C^W(cC/lp9fS<6zX*j^:Ndy7t-cYA29^4OFT-*H+(:N6OyNUOzpB2G)p+l2_cKzF*K<SBOOCB1GFMB+;FMSfTLKJ9^%OFTBlXz6PY_c13BR&b2#qR_H*1|<z28KjRp+8y.LWBtz+c+;.BZf+pK#.kPYLR/5J+YM(+|TC7<Wc+tc4Db2#k+)WV^D(+4(G+|TB4-R)Wd+25VF5EM(D&OW%lpOGp+2|<t*5cZG+kFLqU.||5dL>P3Z#>R(UVFFz9/JNbVM)|p|f>4+RRL.k5*+BEH+M8|CV@+l#2E.B)",
				"H+M8|CV@K+l#2E.B)>R(UVFFz9</JNbVM)+DpOGp+2|G+t*5cZGR)N^D(+4(5J+|TB4-|TCkPYLR/8KjRYM(+y.LWz|<z29^%OFp+8lXz6POLKJp+l2_cTBzF*K<SAG)y7t-cYAK29^4OFTKdpclddG+4Ucy5C^W(+EB+*5k.LdRR+4>f|pM>#Z3P>L(M5||.UqL+Hl%WO&D#2bE5FV52cWpVW)+kp+fZD4ct+c+zSk.#K_Rq#2B.;+B31cZ1*HSMF;+Bb&RG1BCO8TfBpzOUNyMF6N:(+H|2N:^j*Xz6O<Sf9pl/;",
				"HC/lpSfS<O5yNUOzpBfTOOCBHGR&b>2#qR_K#.ktz+c+tc4D4b2#D&OW%ld+LqA.||5XdL.k5*+BEc(W^z5ycU-AYc-t7y)GBS<KNFzBTVFO%^92z<|BWL.>+(MY9+J5(4+(D^W)RG|c5*t1<9zFFVU(R|)B.Z2#l++6zX*j^:N2*H+(pN6FMUB+;FMSH*1_c13M+;.BCZf+pk+)WV<Wc2KVF5E*M(L>P3Z#>Fp|fO4+RRy4+Gddlcpd-TFOk^92KZc_2l+pJKLYP6zDl8+pERjK8/RLYP7CT|;4BT|:+G|2+pGOpk+)M8bNJ/BK@VC|8M+",
				"Hk5||.UqL+U#S3P>L(Mp7D4ct+c+zJW8+kp+fZPYb&RG1BCO7*;SMF;+BL-O<Sf9pl/yND^j*Xz6dF/JNbVM)+l(kVFFz9<><|TB4-|TC^DO+4(5J+V_p+8lXz6Pp<K29^%OF1*K29^4OFTB)M7t-cYA2|RR+4>f|pzBp*5k.LdRWE5FV52cW+%ZO&D#2b^BB.;+B31cO.|K_Rq#2|BMF6N:(+HFf>pzOUNyGc+l#2E.B)-+N8|CV@KEdt*5cZGR)VOzp+2|G+ltYM(+y.LW+YAR/8KjRkOTBzF*K<S<K+p+l2_cTCUcy5C^W(-pHlddG+4",
				"H;*H+(:N6FMyNUOzpN2|OOCB1GR&bB+;FMSfT8_c13B+;.B2#qR_K*1Ztz+c+tc4DZf+pk+.kS<Wc25VF5Eb2#D&OWVpd+LqU.||5M(L>P3%lHFp|f>4+RRdL.k5*#>Mc(W^C5ycU4+GddlBE+-TFO4^92KAYc-t7pdKBS<K*FzBTc_2l+p)GAYP6zXl8+pFO%^92KLOBWL.y+(MYRjK8/R<|z7CT|-4BT|+J5(4+YPkW)RGZc5*t+G|2+pD^Nk+)MVbNJ/<9zFFVOpD|)B.E2#l+K@VC|8(R>C/lp9fS<O6zX*j^+",
				"N;*H+(:N6FMyNUOzpBf|OOCB1GR&bB+;FMSH*8_c13B+;.B2#qR_K#.Ztz+c+tc4DZf+pk+)WS<Wc25VF5Eb2#D&OW%pd+LqU.||5M(L>P3Z#HFp|f>4+RRdL.k5*+BMc(W^C5ycU4+Gddlcp+-TFO4^92KAYc-t7y)KBS<K*FzBTc_2l+pJKAYP6zXl8+pFO%^92z<OBWL.y+(MYRjK8/RLYz7CT|-4BT|+J5(4+(DkW)RGZc5*t+G|2+pGONk+)MVbNJ/<9zFFVU(D|)B.E2#l+K@VC|8M+>C/lp9fS<O6zX*j^:",
				"H4+Gddlc+c(W^C5yc>fTc_2l+pJMBS<K*FzB|.kRjK8/RLBBWL.y+(MZ%l+G|2+pGEW)RGZc5*pBEK@VC|8MR|)B.E2#lM)GyNUOzpBK*H+(:N6FK<|2#qR_K#p_c13B+;.OD^b2#D&OW|<Wc25VF5k(RdL.k5*+/Fp|f>4+RDN2AYc-t7yO-TFO4^92;*1FO%^92zbYP6zXl8+8WV+J5(4+(D7CT|-4BTS#><9zFFVU5k+)MVbNJHpd6zX*j^:UC/lp9fS<+KLB+;FMSHTOOCB1GR&AYPZf+pk+)Ytz+c+tc4zOpM(L>P3Ztd+LqU.||N+",
				"c(W^C5yc4+GddlcpC/lp9fS<6zX*j^:N-TFO4^92AYc-t7yd*H+(:N6OyNUOzpB2BS<K*FzKc_2l+p)GOOCB1GFMB+;FMSfTYP6zXlBTFO%^9JKL_c13BR&b2#qR_H*1BWL.y8+pRjK82z<|tz+c+;.BZf+pK#.k7CT|+(MY+J5/RLYP<Wc+tc4Db2#k+)WVW)R-4BT|+G(4+(D^d+25VF5EM(D&OW%lk+GZc5*t<|2+pGOpFLqU.||5dL>P3Z#>|)MVbNJ/9zFFVU(Rp|f>4+RRL.k5*+BE)B.E2#l+@VC|8M+H",
				"pclddG+4cy5C^W(cH+M8|CV@+l#2E.B)C/lp9fS<6zX*j^:Np|f>4+RRL.k5*+BEdy7t-cYA29^4OFT-R(UVFFz9/JNbVM)|*H+(:N6OyNUOzpB2FLqU.||5dL>P3Z#>G)p+l2_cKzF*K<SBpOGp+2|<t*5cZG+kOOCB1GFMB+;FMSfTd+25VF5EM(D&OW%lLKJ9^%OFTBlXz6PY^D(+4(G+|TB4-R)W_c13BR&b2#qR_H*1<Wc+tc4Db2#k+)WV|<z28KjRp+8y.LWBPYLR/5J+YM(+|TC7tz+c+;.BZf+pK#.k",
				"c(W^C5yc4+GddlcpN:^j*Xz6<Sf9pl/C-TFO4^92AYc-t7yd2BpzOUNyO6N:(+H*BS<K*FzKc_2l+p)GTfSMF;+BMFG1BCOOYP6zXlBTFO%^9JKL1*H_Rq#2b&RB31c_BWL.y8+pRjK82z<|k.#Kp+fZB.;+c+zt7CT|+(MY+J5/RLYPVW)+k#2bD4ct+cW<W)R-4BT|+G(4+(D^l%WO&D(ME5FV52+dk+GZc5*t<|2+pGOp>#Z3P>Ld5||.UqLF|)MVbNJ/9zFFVU(REB+*5k.LRR+4>f|p)B.E2#l+@VC|8M+H",
				"H+M8|CV@+l#2E.B)pclddG+4cy5C^W(cEB+*5k.LRR+4>f|pN:^j*Xz6<Sf9pl/CR(UVFFz9/JNbVM)|dy7t-cYA29^4OFT->#Z3P>Ld5||.UqLF2BpzOUNyO6N:(+H*pOGp+2|<t*5cZG+kG)p+l2_cKzF*K<SBl%WO&D(ME5FV52+dTfSMF;+BMFG1BCOO^D(+4(G+|TB4-R)WLKJ9^%OFTBlXz6PYVW)+k#2bD4ct+cW<1*H_Rq#2b&RB31c_PYLR/5J+YM(+|TC7|<z28KjRp+8y.LWBk.#Kp+fZB.;+c+zt",
				"H4+GddlHp-(W^C5ycUCTc_2l+p+K<S<K*FzBTOkRjK8/RAY+WL.y+(MYtl+G|2+pzOV)RGZc5*tdEK@VC|8N+-)B.E2#l+cGyNUOzp>fFH+(:N6FMB|2#qR_K|.Oc13B+;.BB^b2#D&OZ%+Wc25VF5EWRdL.k5*pBzp|f>4+RR|2AYc-t7M)BTFO4^92K*1FO%^92K<pP6zXl8+p_V+J5(4+OD^CT|-4BT|<><9zFFVk(l+)MVbNJ/Fd6zX*j^DNy/lp9fS<O-LB+;FMS;*7OCB1GR&bYPZf+pk+8WJz+c+tc4D7pM(L>P3S#U+LqU.||5k",
				"H;*H+(:N6FM<NUOzpN2|OOCB1GR&bp+;FMSfT8_c13B+;.B+#qR_K*1Ztz+c+tc4D^f+pk+.kS<Wc25VF5EV2#D&OWVpd+LqU.||5l(L>P3%lHFp|f>4+RR-L.k5*#>Mc(W^C5ycUy+GddlBE+-TFO4^92KFYc-t7pdKBS<K*FzBT7_2l+p)GAYP6zXl8+pOO%^92KLOBWL.y+(MYJjK8/R<|z7CT|-4BT|+J5(4+YPkW)RGZc5*tUG|2+pD^Nk+)MVbNJ/z9zFFVOpD|)B.E2#l+-@VC|8(R>C/lp9fS<OBzX*j^+",
				"H+^j*XzBO<Sf9pl/C>R(8|CV@-+l#2E.B)|DpOVFFz9z/JNbVM)+kN^Dp+2|GUt*5cZGR)WkPY+4(5J+|TB4-|TC7z|<R/8KjJYM(+y.LWBOLK29^%OOp+8lXz6PYAG)p+l2_7TBzF*K<SBKdp7t-cYFK29^4OFT-+EBlddG+yUcy5C^W(cM>#*5k.L-RR+4>f|pFHl%3P>L(l5||.UqL+dpVWO&D#2VE5FV52cW<Sk.+kp+f^D4ct+c+ztZ1*K_Rq#+B.;+B31c_8TfSMF;+pb&RG1BCOO|2NpzOUN<MF6N:(+H*;",
				"pclddG+4cy5C^W(cN:^j*Xz6<Sf9pl/Cdy7t-cYA29^4OFT-2BpzOUNyO6N:(+H*G)p+l2_cKzF*K<SBTfSMF;+BMFG1BCOOLKJ9^%OFTBlXz6PY1*H_Rq#2b&RB31c_|<z28KjRp+8y.LWBk.#Kp+fZB.;+c+ztPYLR/5J+YM(+|TC7VW)+k#2bD4ct+cW<^D(+4(G+|TB4-R)Wl%WO&D(ME5FV52+dpOGp+2|<t*5cZG+k>#Z3P>Ld5||.UqLFR(UVFFz9/JNbVM)|EB+*5k.LRR+4>f|pH+M8|CV@+l#2E.B)",
				")B.E2#l+@VC|8M+HEB+*5k.LRR+4>f|p|)MVbNJ/9zFFVU(R>#Z3P>Ld5||.UqLFk+GZc5*t<|2+pGOpl%WO&D(ME5FV52+dW)R-4BT|+G(4+(D^VW)+k#2bD4ct+cW<7CT|+(MY+J5/RLYPk.#Kp+fZB.;+c+ztBWL.y8+pRjK82z<|1*H_Rq#2b&RB31c_YP6zXlBTFO%^9JKLTfSMF;+BMFG1BCOOBS<K*FzKc_2l+p)G2BpzOUNyO6N:(+H*-TFO4^92AYc-t7ydN:^j*Xz6<Sf9pl/Cc(W^C5yc4+Gddlcp",
				"H+M8|CV@+l#2E.B)pclddG+4cy5C^W(cp|f>4+RRL.k5*+BEC/lp9fS<6zX*j^:NR(UVFFz9/JNbVM)|dy7t-cYA29^4OFT-FLqU.||5dL>P3Z#>*H+(:N6OyNUOzpB2pOGp+2|<t*5cZG+kG)p+l2_cKzF*K<SBd+25VF5EM(D&OW%lOOCB1GFMB+;FMSfT^D(+4(G+|TB4-R)WLKJ9^%OFTBlXz6PY<Wc+tc4Db2#k+)WV_c13BR&b2#qR_H*1PYLR/5J+YM(+|TC7|<z28KjRp+8y.LWBtz+c+;.BZf+pK#.k",
				"H+M8|CV@KB/JNb8M)+kpOGp+2|G+:|TB4;|TC7PYLR/8KjREp+8lDz6PYLKJp+l2_cZK29^kOFT-dpclddG+4yRR+4Of|pF>#Z3P>L(M*E5FVK2cW<VW)+kp+fZCB.;+M31c_1*HSMF;+BUMF6Np(+H*2N:^j*Xz6++l#2Z.B)|R(UVFFz9<1t*5c|GR)W^D(+4(5J+9YM(+>.LWB|<z29^%OFVTBzFNK<SBG)y7t-cYA-Ucy5z^W(cEB+*5k.LdX5||.AqL+dl%WO&D#2b4D4ct+c+ztk.#K_Rq#2>b&RGHBCOOTfBpzOUNy5O<SfSpl/C",
				"pclddG+4cy5C^W(cH+M8|CV@+l#2E.B)N:^j*Xz6<Sf9pl/CEB+*5k.LRR+4>f|pdy7t-cYA29^4OFT-R(UVFFz9/JNbVM)|2BpzOUNyO6N:(+H*>#Z3P>Ld5||.UqLFG)p+l2_cKzF*K<SBpOGp+2|<t*5cZG+kTfSMF;+BMFG1BCOOl%WO&D(ME5FV52+dLKJ9^%OFTBlXz6PY^D(+4(G+|TB4-R)W1*H_Rq#2b&RB31c_VW)+k#2bD4ct+cW<|<z28KjRp+8y.LWBPYLR/5J+YM(+|TC7k.#Kp+fZB.;+c+zt",
				"p>REHkG+%<B+(H7V+9jP/Fd2+2V(jNL+2Az^7-5YMc*.z#F8N44B4V+zpqTCW+zS)dPV^lTpO.)MWzp*+_tFk^*>5;G|f_@MRy-+py/DT<|MFy5;F2Vy:>-3OMc6lLCO(kL1|N#YfcG#p8O2z54R-F#%XLJ++<ZcKVOBR|b2J4BS|(G5c+*E5.(fR1F)WP/Fd2(WK:ZLSlpK+|&8Ok(qcz2Ozd+B4U+F+EpORT&9Nclf.+1CZcK.2L+|)cT|BD*yU)J^3RMd+plCDKU.G#Y9bF6lJ<UtBKl5+6+BR^btX9U|B^G+<B+WHp",
				"H;*H+(:N6FMyNUOzpBfT8_c13B+;.B2#qR_K#.kS<Wc25VF5Eb2#D&OW%lHFp|f>4+RRdL.k5*+BE+-TFO4^92KAYc-t7y)GAYP6zXl8+pFO%^92z<|z7CT|-4BT|+J5(4+(D^Nk+)MVbNJ/<9zFFVU(R>C/lp9fS<O6zX*j^:N2|OOCB1GR&bB+;FMSH*1Ztz+c+tc4DZf+pk+)WVpd+LqU.||5M(L>P3Z#>Mc(W^C5ycU4+GddlcpdKBS<K*FzBTc_2l+pJKLOBWL.y+(MYRjK8/RLYPkW)RGZc5*t+G|2+pGOpD|)B.E2#l+K@VC|8M+",
				"H+M8|CV@K+l#2E.B)|DpOGp+2|G+t*5cZGR)WkPYLR/8KjRYM(+y.LWBOLKJp+l2_cTBzF*K<SBKdpclddG+4Ucy5C^W(cM>#Z3P>L(M5||.UqL+dpVW)+kp+fZD4ct+c+ztZ1*HSMF;+Bb&RG1BCOO|2N:^j*Xz6O<Sf9pl/C>R(UVFFz9</JNbVM)+kN^D(+4(5J+|TB4-|TC7z|<z29^%OFp+8lXz6PYAG)y7t-cYAK29^4OFT-+EB+*5k.LdRR+4>f|pFHl%WO&D#2bE5FV52cW<Sk.#K_Rq#2B.;+B31c_8TfBpzOUNyMF6N:(+H*;",
				"HZFpP/35yjP2kWX56qpd<.y|+^WKC>N^VGD*+bK+kzVTM|BfF)plp*A.32W^BEzBG|KEB+Gl%BR4MWCJ^H9NFT5fbFS|ycVGJp8NYyZA+C&dX2tO82T)dHVS_O<c|K+++lc|+T2O;HN/J85*+kcK5YpUb+9zUO-#7R5*M*tpB(#LGd(D<kRO#8.F(R/_G+p+4#lU5(_|f<<-4>PDyp(jcMC4lF)l1|K+R+qMZ9:Mz2ZBBL^BdOLRc2SO)t.>6Vc2|^|KBB+O&czL*4-;++tMNYCl)EWU9+zzY-1c<R>7k(zFU#+:SB1T4%Op7fFD@F(;zV6J.+W-L.",
				"H.L-W+.J6Vz;(F@DFf7pO%4T1BS:+#UFz(k7>R<c1-Yzz+9UWE)lCYNMt++;-4*Lzc&O+BBK|^|2cV6>.t)OS2cRLOdB^LBBZ2zM:9ZMq+R+K|1l)Fl4CMcj(pyDP>4-<<f|_(5Ul#4+p+G_/R(F.8#ORk<D(dGL#(Bpt*M*5R7#-OUz9+bUpY5Kck+*58J/NH;O2T+|cl+++K|c<O_SVHd)T28Ot2Xd&C+AZyYN8pJGVcy|SFbf5TFN9H^JCWM4RB%lG+BEK|GBzEB^W23.A*plp)FfB|MTVzk+Kb+*DGV^N>CKW^+|y.<dpq65XWk2Pjy53/PpFZ",
				"H;*H+(:N6FM<B+;FMSfT8_c13B+;.B+Zf+pk+.kS<Wc25VF5EVM(L>P3%lHFp|f>4+RR-4+GddlBE+-TFO4^92KFc_2l+p)GAYP6zXl8+pORjK8/R<|z7CT|-4BT|++G|2+pD^Nk+)MVbNJ/zK@VC|8(R>C/lp9fS<OByNUOzpN2|OOCB1GR&bp2#qR_K*1Ztz+c+tc4D^b2#D&OWVpd+LqU.||5ldL.k5*#>Mc(W^C5ycUyAYc-t7pdKBS<K*FzBT7FO%^92KLOBWL.y+(MYJ+J5(4+YPkW)RGZc5*tU<9zFFVOpD|)B.E2#l+-6zX*j^+",
				"H+^j*Xz6-+l#2E.B)|DpOVFFz9<Ut*5cZGR)WkPY+4(5J+JYM(+y.LWBOLK29^%OF7TBzF*K<SBKdp7t-cYAyUcy5C^W(cM>#*5k.Ldl5||.UqL+dpVWO&D#2b^D4ct+c+ztZ1*K_Rq#2pb&RG1BCOO|2NpzOUNyBO<Sf9pl/C>R(8|CV@Kz/JNbVM)+kN^Dp+2|G++|TB4-|TC7z|<R/8KjROp+8lXz6PYAG)p+l2_cFK29^4OFT-+EBlddG+4-RR+4>f|pFHl%3P>L(MVE5FV52cW<Sk.+kp+fZ+B.;+B31c_8TfSMF;+B<MF6N:(+H*;",
				"H;*H+(:N6FMyNUOzpBfT8_c13B+;.B2#qR_K#.kS<Wc25VF5Eb2#D&OW%lHFp|f>4+RRdL.k5*+BE+-TFO4^92KAYc-t7y)GAYP6zXl8+pFO%^92z<|z7CT|-4BT|+J5(4+(D^Nk+)MVbNJ/<9zFFVU(R>C/lp9fS<O6zX*j^:N2|OOCB1GR&bB+;FMSH*1Ztz+c+tc4DZf+pk+)WVpd+LqU.||5M(L>P3Z#>Mc(W^C5ycU4+GddlcpdKBS<K*FzBTc_2l+pJKLOBWL.y+(MYRjK8/RLYPkW)RGZc5*t+G|2+pGOpD|)B.E2#l+K@VC|8M+"				
			};
			for (int x=0; x<ciphers.length; x++) {
			String cipher = ciphers[x];
			String name = names[x];
			System.out.println("==== LENGTH: " + cipher.length() + ", NAME: " + name + ", CIPHER: " + cipher);
			List<RepeatingFragmentsFasterBean> list = new ArrayList<RepeatingFragmentsFasterBean>();
			double ioc = fragmentIOC(cipher, 6, false, false, list);
			Collections.sort(list);
			Set<Character> seen = new HashSet<Character>();
			for (RepeatingFragmentsFasterBean bean : list) {
				String ng = bean.ngram;
				boolean skip = false;
				for (int i = 0; i < ng.length(); i++) {
					if (seen.contains(ng.charAt(i))) {
						skip = true;
						break;
					}
				}
				if (skip)
					continue;
				for (int i = 0; i < ng.length(); i++) {
					seen.add(ng.charAt(i));
				}
				System.out.println(bean);
			}
			System.out.println("ioc: " + ioc);
			System.out.println("rarity: " + rarityScore(cipher, 6));
			System.out.println();
		}
	}

	public static void testRarityScore() {
		for (String cipher : new String[] { CipherTransformations.shuffle(Ciphers.Z408).substring(0, 340), Ciphers.Z408.substring(0, 340),
				CipherTransformations.shuffle(Ciphers.Z340), Ciphers.Z340, Periods.rewrite3(Ciphers.Z340, 19) }) {
			System.out.println(rarityScore(cipher, 6));
		}
	}
	
	public static void rarityScoreForTest340s() {
		int count = 0;
		for (String cipher : FileUtil.loadFrom("/Users/doranchak/projects/zodiac/movies/let's crack zodiac/episode_2/test-ciphers-ciphertexts.txt")) {
			count++;
			System.out.println(rarityScore(cipher, 6) + "	" + cipher.length() + "	testCipher" + count);
		}
		
	}

	/** generate beans of repeating patterns, then add up the probabilities for the
	 * least probable patterns.  return the sum as an estimate of how often rare patterns appear in 
	 * the input.  each pattern is mutually exclusive of all other patterns (to avoid overlaps).
	 */
	public static double rarityScore(String cipher, int maxFragmentSize) {
		List<RepeatingFragmentsFasterBean> list = new ArrayList<RepeatingFragmentsFasterBean>();
		double ioc = fragmentIOC(cipher, 6, false, false, list);
		Collections.sort(list);
		Set<Character> seen = new HashSet<Character>();
		double sum = 0;
		
		int found = 0;
		for (RepeatingFragmentsFasterBean bean : list) { 
			String ng = bean.ngram;
			boolean skip = false;
			for (int i=0; i<ng.length(); i++) {
				if (seen.contains(ng.charAt(i))) {
					skip = true;
					break;
				}
			}
			if (skip) continue;
			for (int i=0; i<ng.length(); i++) {
				seen.add(ng.charAt(i));
			}
			//System.out.println(bean);
			sum += 1/bean.probability();
			found++;
		}
		return Math.log10(sum); // return log of sum to smooth out effect of super rare outliers 
	}
	/** old version doesn't prevent overlaps */
	public static double rarityScoreOld(String cipher, int maxFragmentSize, int topN) {
		List<RepeatingFragmentsFasterBean> list = new ArrayList<RepeatingFragmentsFasterBean>();
		double ioc = fragmentIOC(cipher, 6, false, false, list);
		Collections.sort(list);
		double sum = 0;
		for (int i=0; i<topN; i++)
			sum += list.get(i).probability();
		return sum;
	}
	
	public static void testOld() {
		//test2();
		//test3();
//		test();
		//String cipher = "d)J2/(|9K-OFR++2Lpc+2fHKkDLL4yKB8+-G))C;GKzR42z@6BM&;t5q|(-KT*#+&>.+AFT+(4ZMB/*|L<L++FVzy<b.cVUfWTBA1.)OPkGYc7pMBcV.lHO8|Y(pFCXNBpB4F.>^FSYOkWW35dc_FOYT5bEp+O_ZPDGV|*U+2+D5|4CO<PBzV%Z*d-2ORJ|*N29(CctS^OU8jO;#Z^E|+54K6W7kl#+ltl%j++tJ#f:B1z<pp(M^zFqfbV5Ry^*zLCWN>Bc7+^KJ+U/lSN13+WdHR+:pMR+2MlRc9FX.T)kDEpyp985G<z+<6GBcc+FMHNBS_p#(d-U2zly|R+|>HER>pl^VPk|1LTG2dNp+B(#O%DWY.<*Kf)By:cM+UZGW()L#zHJSpp7^l8*V3pO++RK2_9M+ztjd|5FP+&4k/p8R^FlO-*dCkF>2D(#5+Kq%;2UcXGV.zL|(G2Jfj#O+_NYz+@L9d<M+b+ZR2FBcyA64K-zlUV+^J+Op7<FBy-U+R/5tE|DYBpbTMKO2<clRJ|*5T4M.+&BFz69Sy#+N|5FBc(;8RlGFN^f524b.cV4t++yBX1*:49CE>VUZ5-+|c.3zBK(Op^.fMqG2RcT+L16C<+FlWB|)L++)WCzWcPOSHT/()p|FkdW<7tB_YOB*-Cc>MDHNpkSzZO8A|K;+";
		/*for (int i=0; i<340-114+1; i++) {
			String cipher = Ciphers.cipher[0].cipher.substring(i, i+114);
			System.out.println(measure(cipher, true, false) + " " + measure(cipher, false, false) + " " + i);
			
		}*/
//		String cipher = Ciphers.cipher[6].cipher;//.substring(0,340);
		//cipher = Periods.rewrite3(cipher, 15);
		//String cipher = "lz2Jf+KqBy:Np+HER4T5*F<pk/pK2_HJSz#ySc.b4t++5Vb/U+McG6<lz2RFOlBF5|N+&M(+TVc.b4t++^NF9G21*:49CE>VUZ5-+;WB|)L3zBK(Op^.fMqOSHT/()p+L16C<+Fl7tB_YOB*-CcWCzWcPHNpkSzZO8A|K;+dW<c|XByKB8f52RJ|Z^EpBc7DM>kF|)++TcR.A64K^R8RJ|2+DFOYBF5|djtz+M9By-U+Ry++Op3V*8l^7pp4&+P#jz#L)(WCd*-OlF-RcU2;%+@L9d<MYN_+OFkGZU+McG(|Lz.VGX*<.YWD%O#(B5#(D2>d2GTL1|kPV^lp>)fK";
//		String cipher = "C0m0CRn2UAr7KdUZ4SFU-NyI14+IQEdajVdJGTSKzgQMg5VfABFL6klDRlDCCnoW8M3hUgqKOJ24b87G4drgBi+3tPmDw0CunGKebPdUe7XbpV-3d8ABt37pkx0xlDCungPKKB1ULhpSJP+N+IdzbXItq2zI5B1qiEWE2ZN+VUzIABFL+vDmDCwndjMYr+8tp7736fSdZ3oWzpYZjF8sLAYLPMUoi-c2XSFGgBFL6JPcDxv0CwngMBT6WQ2KS8Y15+doEo8Y4sJWOc57IbTY1OhUgBiS3tOklDkkDCwncfL1GK46Ec7rJ4I3UWrXFWKJgBFLyHUI3NonwDJtcInl";
//		System.out.println(measure(cipher, false, true) + " " + measure(cipher, true, false));
		//System.out.println("scoring");
//		String cipher = "ABCDEFCDGH|B|JBKLGMBNLOBEPKBKPMPLRBCNFKDGMFESHTFUGVGHOBMPOBUHJFKLWBUDKLFKGTGLXBCLGLWFMLRFCDGYFKDPXFUHGUGDZFUHNBKDGAFCHZFEPWFCLKGOBMHGAFCLVFMLGTGKBKDRBKDGUBKP1BNLUFKSDYFKG2PAGJFUDCBKLEBED|B|GZBKHUFCL3FCGEFNLMBKDGSRBCLG|B|DOFKLGJFUSABCLGVGLOBMSGAFCDEFCHRBMGKBCHEBEDOBEPOBWPJFKLTBKGTGOBMDEBEDZFEGZFWHOBEP1BEDYF4OBMOBMDOFKLPOBULEBNLUFCHZFNGVFEDOFKSCBKLGUFKD1FKDGMBKSEBED|B|GZFNHOBEG1FCDZFNGTGZFO5HEFNSAFNGEBNLEB1GAFCSHOBMLABKGDGJFUHZFECB|SJBKSOBEGTGZFNHLEBEDABKGJFMDWFMS|FEDPTGOFKLGDAFCDOBEPOBW5YFKSEFCLCBKLCFKPOBESJFKGJFU5HOBEPZFULZFNGZFRDGOFKL|B|GVFNHEBMH2FKDOBMGTGZFNH1BNLJFKGKBKDAFCGEFUD1BELXFUGAFCHAFKHEBWL3FKGXFUSGAFNDP2FCHXFNGAFCHRBC2B|PZFUH6FRPXFMLZFELGXFRLYFKPXFCH|BUHWB|GLGRBC5HOBMPZFUPZFELGOF|LP7LPZGTGD8FRGUGZFRD|B|DZFUPXFNDZFNCBKHOBUHYFKNFKHGOBMHOBEGEBNDUFKLOFKH3FKGZGTGXFUHGDABMHGUGEBNDXFUGZFODNFU|BCSHYFKPJBKDOBMGUGLKSGLCFKLEBCDGZGHOBMDGOBMLEFNH1FKD3FKGTGLGOFKLGUBCHOBKPZFUHGTGYFKDPZFNGHOBMPABCLMBND1BEGCBKSOBES|FKSHYFKPNFKDGOBMDTFUGSKFKHYFKP1BMHGZGTGJBKHOBKHMBND|B|GH1FCGHRFCH1PKPYBKDGUG1FCL3FKGDCFKHUBNLXFUGZGL4BNSGAFC5HUBKHRBCG3FKHWFCSZFEG51BEPWBOHCBK53FKGZGTGOFKLPLPKBKL4FCPZFULCBKLP9FMLGXF|HGNFKSDGLWBWSGEFNLVLOBUDRBCPNBKLYFKGUGNFCP|FCDJFCHGLGRLOFEHYFKPOBWHGTG|B|LOFKHGLGUFKPWBCSMBESEFULGZG1FCLOBMGAFN5UFCHZFNGZGTGLGSGABKHGUFCLGLGJFUHZFELXFNZFUDEFCDGOBNHOBUHNBMD3FKGEBEDMFUSMFCSMFNLCBKDGLTBCSGEFUD|BCDHXFUPZFULGOFKH1BMP3FCLGZFULYFCPUFCDHYFKGMBUDTBCGOFKDKBKDABKGCBKHYFUG3FK5ZFEGKGH3BRDZFYGH|B|P1BMLYF";
//		Map<Integer, RepeatingFragmentsFasterBean> map = score(cipher, false, false, true);
//		for (Integer key : map.keySet())
//			System.out.println(key + "	" + map.get(key));
		//System.out.println(coverage(Ciphers.cipher[0].cipher));
		//testForFragExplorer();
//		fragmentIOCAllPeriods(Ciphers.cipher[0].cipher, 5);
//		System.out.println(fragmentIOC(Ciphers.cipher[0].cipher, 6, true, true));
		//System.out.println(RepeatingFragmentsFaster.measure(cipher));
		
		//test();
		//testForTestCiphers();
		//System.out.println(measure(cipher, false, false) + " " + measure(cipher, true, false));
		/*Set<String> wild = new HashSet<String>();
		wildcards(wild, new StringBuffer("ABCDEFGHIJK"), 0);
		System.out.println(wild);*/
		//System.out.println(CipherTransformations.shuffle(Ciphers.cipher[1].cipher));
		
	}
	
	public static void main(String[] args) {
		testFragmentIOC();
//		testRarityScore();
//		rarityScoreForTest340s();
//		testShuffles(
//				"WEOZQXZEDWDCEGDOD+RMQImJWPWSOYXRZLzQJRFPERsSJVEULDROXLMVJRFLRTLITITQVLYVHOtGIBLAHJYRNZJXPOLPGYMFFmDEDWLGLESURELMACRCACTNOLWCAnIzRTAOTHSRNUKAFOVIITFLPXXAKLRZFMGJSFULPtQWHIZVASTr181l861r3MERGUITAVOY625ISR50",
//				1000, 10);
//		testShuffles(
//				Ciphers.Z340,
//				1000, 4);
	}
	
	
}
