package com.zodiackillerciphers.ngrams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.ciphers.algorithms.Scytale;
import com.zodiackillerciphers.ciphers.generator.CandidateKey;
import com.zodiackillerciphers.ciphers.generator.TrigramUtils;
import com.zodiackillerciphers.stats.StatsWrapper;
import com.zodiackillerciphers.tests.EvenOddAndFactors;
import com.zodiackillerciphers.transform.CipherTransformations;

public class NGramsBean {
	public static boolean DEBUG = false;
	public int n; // ngram length
	public String str; // the input string
	public Map<String, Integer> counts; // occurrence count for each ngram
	public Map<Character, Integer> symbolCounts; // occurrence count for each symbol
	
	public Set<String> repeats; // list of ngrams that occur more than once
	public Set<String> nonrepeats; // list of ngrams than only occur once
	
	public Map<String, List<Integer>> positions; // list of positions for each ngram
	
	/** coverage.  that is, all positions that are covered by at least one repeating ngram */
	public Set<Integer> coverage;
	
	public List<Integer> distribution; // all counts, in descending order
	public List<Integer> distributionWeighted; // all counts, in descending order, weighted by symbol counts

	/** list of distances between repetitions */
	public List<Integer> distances;
	/** count of factors for all distances */
	public Map<Integer, Integer> factorCounts;
	
	public boolean ignoreSpaces = true;
	
	/** NGram beans generated for the reference ciphers.
	 * key is the cipher name.
	 * val is another map, whose key is ngram size, and whose val is the ngramsbean describing ngrams of that size.
	 * */
	public static Map<String, Map<Integer, NGramsBean>> referenceCipherBeans;
	static {
		referenceCipherBeans = new HashMap<String, Map<Integer, NGramsBean>>();
		
		String[][] ciphers = new String[][] {
				{ "z340", CandidateKey.referenceCipher },
				{ "z340odds", EvenOddAndFactors.z340oddsRemoved },
				{ "z340evens", EvenOddAndFactors.z340evensRemoved }, };		
		
		for (String[] cipher : ciphers) {
			String key = cipher[0];
			String ciphertext = cipher[1];
			Map<Integer, NGramsBean> map = new HashMap<Integer, NGramsBean>();
			for (int n=2; n<4; n++) { // for z340, we only get repeats for 2- and 3-grams
				NGramsBean bean = new NGramsBean(n, ciphertext);
				map.put(n, bean);
			}
			referenceCipherBeans.put(key, map);
		}
	}
	
	
	public NGramsBean(int n, String s) {
		this(n, s, true);
	}
	public NGramsBean(int n, String s, boolean ignoreSpaces) {
		this.ignoreSpaces = ignoreSpaces;
		this.n = n;
		this.str = s;
		
		//System.out.println("SMEG construct " + n + ", " + s);
		
		counts = new HashMap<String, Integer>();
		repeats = new HashSet<String>();
		nonrepeats = new HashSet<String>();
		positions = new HashMap<String, List<Integer>>();
		symbolCounts = Ciphers.countMap(s);
		coverage = new HashSet<Integer>();
		
		for (int i=0; i<s.length()-n+1; i++) {
			String ngram = s.substring(i,i+n);
			if (ignoreSpaces && ngram.contains(" ")) continue; // ignore spaces
			//if (TrigramUtils.ignore(ngram)) continue;
			Integer val = counts.get(ngram);
			if (val == null) val = 0;
			val++;
			counts.put(ngram,val);
			
			// track positions
			List<Integer> list = positions.get(ngram);
			if (list == null) list = new ArrayList<Integer>();
			list.add(i);
			positions.put(ngram, list);
			
		}
		
		for (String ngram : counts.keySet()) {
			if (counts.get(ngram) == 1) nonrepeats.add(ngram);
			else {
				repeats.add(ngram);
				
				// update coverage
				for (Integer i : positions.get(ngram))
					for (int k=0; k<n; k++) coverage.add(i+k);
				
				//System.out.println("Added repeat " + ngram);
			}
		}
		
		distribution = new ArrayList<Integer>(counts.values());
		Collections.sort(distribution);
		Collections.reverse(distribution);
		
		distributionWeighted = new ArrayList<Integer>();
		for (String ngram : counts.keySet()) {
			int result = 1;
			for (int i=0; i<ngram.length(); i++) result *= symbolCounts.get(ngram.charAt(i));
			result = (int) Math.pow(result, counts.get(ngram));
			distributionWeighted.add(result);
		}
		Collections.sort(distributionWeighted);
		Collections.reverse(distributionWeighted);
		
		
	}
	public void dump() {
		for (String key : counts.keySet()) {
			System.out.println(key+", " + counts.get(key) + ", " + positions.get(key));
		}
		System.out.println("distribution: " + distribution);
		System.out.println("distribution weighted: " + distributionWeighted);
		System.out.println("repeats: " + numRepeats());
		System.out.println("coverage: " + coverage);
		System.out.println("coverage ratio: " + ((float)coverage.size())/str.length());
	}
	public String toString() {
		return "n="+n+", " + counts.keySet().size() + " unique ngrams, " + repeats.size() + " ngrams that repeat, " + nonrepeats.size() + " ngrams that don't repeat.  Num repeats: " + numRepeats() + ".  Total count of repeats: " + count();
	}
	public String repeatsInfo() {
		String info = "";
		for (String ngram : repeats) {
			info += ngram + " (" + counts.get(ngram) + ") ";
		}
		if (numRepeats() > 0) info += "  TOTAL REPEATS: " + numRepeats();
		return info;
	}
	
	/** return number of repeats (example: AB AB AB has repeats=2) */
	public int numRepeats() {
		int reps = 0;
		if (repeats == null) return reps;
		for (String key : repeats) {
			int count = counts.get(key);
			if (count > 1) reps += counts.get(key) - 1;
		}
		return reps;
	}
	/** static version */
	public static int numRepeats(int n, String cipher) {
		NGramsBean ng = new NGramsBean(n, cipher);
		return ng.numRepeats();
	}
	
	
	/** increment count for given bigram.
	 * returns 1 if the number of repeats increased by 1 */
	public int add(String bigram) {
		int total = 0;
		if (bigram == null) return total;
		Integer val = counts.get(bigram);
		if (val == null) val = 0;
		String msg = null;
		if (DEBUG) msg = "add bigram " + bigram + " before " + val + " after ";
		val++;
		if (DEBUG) msg += val;
		counts.put(bigram, val);
		if (val > 1) {
			repeats.add(bigram);
			nonrepeats.remove(bigram);
			total = 1;
		}
		debug(msg);
		return total;
	}
	/** decrement count for given bigram.  returns -1 if number of repeats went down by 1 */
	public int remove(String bigram) {
		int total = 0;
		if (bigram == null) return total;
		Integer val = counts.get(bigram);
		if (val == null) val = 0;
		String msg = null;
		if (DEBUG) msg = "remove bigram " + bigram + " before " + val + " after ";
		val--;
		if (DEBUG) msg += val;
		counts.put(bigram, val);
		if (val < 2) {
			repeats.remove(bigram);
			nonrepeats.add(bigram);
		}
		if (val > 0)
			total = -1;  // the number of repeats has decreased
		debug(msg);
		return total;
	}

	/** return total count for repeating ngrams (example: AB AB AB has count=3) */
	public int count() {
		int reps = 0;
		if (repeats == null) return reps;
		for (String key : repeats) {
			reps += counts.get(key);
		}
		return reps;
	}
	
	/** jarlve's repeat score, with increased weights for higher counts */
	public int jarlveScore() {
		int reps = 0;
		if (repeats == null) return reps;
		for (String key : repeats) {
			int count = counts.get(key)-1;
			if (count < 0) throw new IllegalArgumentException("Count is 0 for " + key);
			reps += count*(count-1);
			System.out.println(count + ", " + reps);
		}
		System.out.println("done: " + reps);
		return reps;
	}
	
	
	/** return number of ngrams in bean2 that are not in bean1 */
	public static int diff(NGramsBean bean1, NGramsBean bean2) {
		int diff = 0;
		for (String key : bean2.counts.keySet()) {
			Integer val1 = bean2.counts.get(key);
			Integer val2 = bean1.counts.get(key);
			if (val2 == null) diff += val1;
			else if (val1 > val2)
				diff += val1-val2;
		}
		return diff;
	}
	
	/** calculate euclidian distance between two vectors formed by the given beans.
	 * vectors only consist of ngrams that occur more than once. 
	 */
	public static float distanceOld(NGramsBean bean1, NGramsBean bean2) {
		if (bean1.n != bean2.n) {
			throw new RuntimeException("Illegal comparison: " + bean1.n + " vs " + bean2.n);
		}
		float sum = 0;
		
		for (String key : bean1.counts.keySet()) {
			if (bean1.counts.get(key) < 2) continue;
			Integer val1 = bean1.counts.get(key);
			Integer val2 = bean2.counts.get(key);
			if (val2 == null) val2 = 0;
			sum += (val1-val2)*(val1-val2);
			
			//System.out.println("SMEG1 " + key + " " + val1 + " " + val2 + " " + sum);
		}
		// handle repeated ngrams in 2nd bean that aren't in 1st
		for (String key : bean2.counts.keySet()) {
			if (bean2.counts.get(key) < 2) continue;
			Integer val2 = bean2.counts.get(key);
			Integer val1 = bean1.counts.get(key);
			if (val1 == null) val1 = 0;
			if (val1 > 1) continue; // it was already considered
			sum += (val1-val2)*(val1-val2);
			//System.out.println("SMEG2 " + key + " " + val1 + " " + val2 + " " + sum);
		}
		
		return (float) Math.sqrt(sum);
	}
	
	/** compute error (differences) between the given beans.
	 */
	public static int error(NGramsBean bean1, NGramsBean bean2) {
		if (bean1.n != bean2.n) {
			throw new RuntimeException("Illegal comparison: " + bean1.n + " vs " + bean2.n);
		}
		
		//System.out.println("SMEG bean1 " + bean1.repeatsInfo());
		//System.out.println("SMEG bean2 " + bean2.repeatsInfo());
		
		int sum = 0;
		
		for (String key : bean1.repeats) {
			Integer val1 = bean1.counts.get(key);
			Integer val2 = bean2.counts.get(key);
			if (val2 == null) val2 = 0;
			sum += (val1 > val2 ? val1-val2 : val2-val1);
			
			//System.out.println("SMEG1 " + key + " " + val1 + " " + val2 + " " + sum);
		}
		// handle repeated ngrams in 2nd bean that aren't in 1st
		for (String key : bean2.repeats) {
			Integer val2 = bean2.counts.get(key);
			Integer val1 = bean1.counts.get(key);
			if (val1 == null) val1 = 0;
			if (val1 > 1) continue; // it was already considered
			sum += (val1 > val2 ? val1-val2 : val2-val1);
			//System.out.println("SMEG2 " + key + " " + val1 + " " + val2 + " " + sum);
		}
		return sum;
	}
	
	
	public List<Integer> locationsFor(String search) {
		List<Integer> list = new ArrayList<Integer>();
		
		int found = str.indexOf(search, 0);
		while (found > -1) {
			list.add(found);
			found = str.indexOf(search, ++found);
		}
		return list;
	}
	
	/** calculate euclidian distance between vectors represented by the ngram/count combinations from each
	 * given cipher text.
	 * 
	 * stops when repeating ngrams are no longer found in cipher1 
	 */
	public static int measureNgramDistance(String cipher1, String cipher2) {
		//System.out.println("SMEG measure " + cipher1 + ", " + cipher2);
		int result = 0;
		int n = 2;
		while (true) {
			NGramsBean bean1 = new NGramsBean(n, cipher1);
			NGramsBean bean2 = new NGramsBean(n, cipher2);

			if (bean1.repeats.size() == 0 && bean2.repeats.size() == 0) break;
			int error = error(bean1, bean2);
			int errorPower = error;
			for (int i=0; i<n-1; i++) 
				errorPower *= error;
			result += errorPower;
			n++;
		}
		return result;
	}
	public static int measureNgramDistance(String cipher1, Map<Integer, NGramsBean> map) {
		//System.out.println("SMEG measure " + cipher1 + ", " + cipher2);
		int result = 0;
		int n = 2;
		while (true) {
			NGramsBean bean1 = new NGramsBean(n, cipher1);
			NGramsBean bean2 = map.get(n);
			if (bean2 == null) break;
			if (bean1.repeats.size() == 0 && bean2.repeats.size() == 0) break;
			int error = error(bean1, bean2);
			int errorPower = 1;
			for (int i=0; i<n-1; i++) 
				errorPower *= error;
			//System.out.println("SMEG " + n + " " + error + " " + errorPower);
			result += errorPower;
			n++;
		}
		return result;
	}

	/** compare only the counts, rather than the exact ngrams */
	public static int measureNgramDistanceCountsOnly(String cipher1, Map<Integer, NGramsBean> map) {
		//System.out.println("SMEG measure " + cipher1 + ", " + cipher2);
		int result = 0;
		int n = 2;
		while (true) {
			NGramsBean bean1 = new NGramsBean(n, cipher1);
			NGramsBean bean2 = map.get(n);
			if (bean2 == null) break;
			if (bean1.repeats.size() == 0 && bean2.repeats.size() == 0) break;
			
			int reps1 = bean1.numRepeats();
			int reps2 = bean2.numRepeats();
			int error = reps1 > reps2 ? reps1-reps2 : reps2-reps1; 
			result += error;
			n++;
		}
		return result;
	}
	
	public static Map<Integer, NGramsBean> makeBeansFor(String cipher) {
		Map<Integer, NGramsBean> map = new HashMap<Integer, NGramsBean>();
		int n = 2;
		boolean repeats = true;
		while (repeats) {
			NGramsBean bean = new NGramsBean(n, cipher);
			repeats = bean.repeats.size() > 0;
			if (repeats) map.put(n, bean);
			n++;
		}
		return map;
	}
	
	public static int dumpNGramsFor(String cipher) {
		int n = 2;
		boolean repeats = true;
		int reps = 0;
		while (repeats) {
			NGramsBean bean = new NGramsBean(n, cipher);
			repeats = bean.repeats.size() > 0;
			String r = bean.repeatsInfo();
			if (r.length() > 0)
				System.out.println(bean.repeatsInfo());
			n++;
			reps += bean.numRepeats();
		}
		return reps;
	}

	public static void dumpMissingGramsFor(String cipher) {
		List<String> list = missingRepeats(makeBeansFor(cipher), referenceCipherBeans.get("z340"));
		String missing = "";
		if (list != null) {
			System.out.println("These Z340 ngrams are missing: " + list);
		}
		list = missingRepeats(referenceCipherBeans.get("z340"), makeBeansFor(cipher));
		if (list != null) {
			System.out.println("These ngrams are extra: " + list);
		}
		
	}
	
	
	/** return list of repeating ngrams from map2 that are missing from map1 */ 
	public static List<String> missingRepeats(Map<Integer, NGramsBean> map1, Map<Integer, NGramsBean> map2) {
		List<String> list = new ArrayList<String>();
		for (Integer key2 : map2.keySet()) {
			NGramsBean val2 = map2.get(key2);
			for (String ngram : val2.repeats) {
				// case 1: this ngram is not among map1's list of repeats
				NGramsBean val1 = map1.get(key2);
				if (val1 == null || !val1.repeats.contains(ngram)) {
					list.add(ngram);
				} else { // case 2: map1 has it listed but its count is not high enough
					if (val1.counts.get(ngram) < val2.counts.get(ngram)) {
						list.add(ngram);
					}
					
				}
			}
		}
		return list;
	}
	
	public static String factors(int val) {
		String result = "";
		for (int i=2; i<val; i++) {
			if (val % i == 0) result += i + " ";
		}
		return result;
	}
	
	public static void testSlice() {
		System.out.println(Ciphers.cipher[0].cipher.substring(0,170).length());
		System.out.println(Ciphers.cipher[0].cipher.substring(170).length());
		NGramsBean bean1 = new NGramsBean(2, Ciphers.cipher[0].cipher.substring(0,170));
		NGramsBean bean2 = new NGramsBean(2, Ciphers.cipher[0].cipher.substring(170));
		System.out.println(bean1.numRepeats());
		System.out.println(bean2.numRepeats());
		
		Map<Integer, Integer> counts = new HashMap<Integer, Integer>();
		
		int hits = 0;
		for (int i=0; i<1000000; i++) {
			String cipher = CipherTransformations.shuffle(Ciphers.cipher[0].cipher);
			bean1 = new NGramsBean(2, cipher.substring(0,170));
			bean2 = new NGramsBean(2, cipher.substring(170));
			int diff = Math.abs(bean1.numRepeats() - bean2.numRepeats()); 
			if (diff >= 8) {
				hits++;
				System.out.println(hits + " out of " + i);
			}
			Integer val = counts.get(diff);
			if (val == null) val = 0;
			val++;
			counts.put(diff, val);
			//System.out.println(bean1.numRepeats() + " " + bean2.numRepeats() + " " + Math.abs(bean1.numRepeats() - bean2.numRepeats()));
		}
		for (Integer key : counts.keySet())
			System.out.println(key + " " + counts.get(key));
		
	}
	
	public static void testRegionsWithNoBigrams() {
		String cipher = Ciphers.cipher[1].cipher;
		for (int L=30; L<cipher.length(); L++) {
			for (int i=0; i<cipher.length()-L+1; i++) {
				String sub = cipher.substring(i,i+L);
				NGramsBean bean = new NGramsBean(2, sub);
				System.out.println(L+" "+i+" "+bean.numRepeats());
			}
		}
		
	}

	public static void testShuffle(String cipher, int n, int shuffles) {
		System.out.println(n + ", " + cipher);
		// String cipher =
		// "dpclddG+4-RR+4>f|pFHl%WO&D#2b^D4ct+c+ztZ1*HSMF;+B<MF6N:(+H*;H+M8|CV@Kz/JNbVM)+kN^D(+4(5J+JYM(+y.LWBOLKJp+l2_cFK29^4OFT-+EB+*5k.Ldl5||.UqL+dpVW)+kp+fZ+B.;+B31c_8TfBpzOUNyBO<Sf9pl/C>R(UVFFz9<Ut*5cZGR)WkPYLR/8KjROp+8lXz6PYAG)y7t-cYAyUcy5C^W(cM>#Z3P>L(MVE5FV52cW<Sk.#K_Rq#2pb&RG1BCOO|2N:^j*Xz6-+l#2E.B)|DpOGp+2|G++|TB4-|TC7z|<z29^%OF7TBzF*K<SBK";
		// // period 15 scheme
		NGramsBean bean = new NGramsBean(n, cipher);
		StatsWrapper stats = new StatsWrapper();
		stats.actual = bean.numRepeats();
		stats.name = "ngram repeats, n=" + n;
		int hits = 0;
		for (int i = 0; i < shuffles; i++) {
			cipher = CipherTransformations.shuffle(cipher);
			//
			// for (int period=1; period<=170; period++) {
			// cipher = Periods.rewrite3(cipher, period);
			bean = new NGramsBean(n, cipher);
			// System.out.println(bean.numRepeats());
			// if (bean.numRepeats() > 0) {
			// hits++;
			// System.out.println(hits + " in " + (i+1));
			// }
			// }
			stats.addValue(bean.numRepeats());
		}
		stats.output();
	}
	/** generate histogram of repeat counts */
	public static void testShuffle2(String cipher, int n) {
		System.out.println(n + ", " + cipher);
		// String cipher =
		// "dpclddG+4-RR+4>f|pFHl%WO&D#2b^D4ct+c+ztZ1*HSMF;+B<MF6N:(+H*;H+M8|CV@Kz/JNbVM)+kN^D(+4(5J+JYM(+y.LWBOLKJp+l2_cFK29^4OFT-+EB+*5k.Ldl5||.UqL+dpVW)+kp+fZ+B.;+B31c_8TfBpzOUNyBO<Sf9pl/C>R(UVFFz9<Ut*5cZGR)WkPYLR/8KjROp+8lXz6PYAG)y7t-cYAyUcy5C^W(cM>#Z3P>L(MVE5FV52cW<Sk.#K_Rq#2pb&RG1BCOO|2N:^j*Xz6-+l#2E.B)|DpOGp+2|G++|TB4-|TC7z|<z29^%OF7TBzF*K<SBK";
		// // period 15 scheme
		Map<Integer, Long> map = new HashMap<Integer, Long>();
		int shuffles = 0;
		while (true) {
			cipher = CipherTransformations.shuffle(cipher);
			shuffles++;
			NGramsBean bean = new NGramsBean(n, cipher);
			Integer key = bean.numRepeats();
			Long val = map.get(key);
			if (val == null) val = 0l;
			val++;
			map.put(key, val);
			if (shuffles % 1000000 == 0)
				System.out.println(shuffles + ": " + map);
		}
	}
	
	/** generate javascript snippet to highlight the repeating bigrams */ 
	public String jsHsl() {
		return jsHsl(null);
	}
	/** same as jsHsl but uses given map to translate new positions to old positions */
	public String jsHsl(Map<Integer, Integer> map) {
		String js = "hsl_random([";
		for (String ngram : repeats) {
			js += "[";
			for (Integer pos : positions.get(ngram)) {
				int a = pos;
				int b = pos+1;
				if (map != null) {
					a = map.get(pos);
					b = map.get(pos+1);
				}
				 js += a + ","+b + ",";
			}
			js += "],";
		}
		js += "])";
		js = js.replaceAll(",\\]", "]");
		return js;
	}
	
	public void copy(Map<String, Integer> countsCopy, Set<String> repeatsCopy, Set<String> nonrepeatsCopy) {
		counts = new HashMap<String, Integer>(countsCopy);
		repeats = new HashSet<String>(repeatsCopy);
		nonrepeats = new HashSet<String>(nonrepeatsCopy);
	}
	
	/** returns true if both beans show the same counts of ngrams */
	public static boolean sameCounts(NGramsBean ng1, NGramsBean ng2) {
		if (ng1 == null && ng2 == null) return true;
		if (ng1 == null || ng2 == null) return false;
		if (ng1.numRepeats() != ng2.numRepeats()) return false;
		for (String key : ng1.counts.keySet()) {
			Integer val1 = ng1.counts.get(key);
			Integer val2 = ng2.counts.get(key);
			if (val1 == 0 && val2 == null) continue;
			if (val2 == null) {
				System.out.println("mismatch: " + key + " " + val1 + " " + val2);
				return false;
			}
			if (val1 != val2) {
				System.out.println("mismatch: " + key + " " + val1 + " " + val2);
				return false;
			}
		}
		for (String key : ng2.counts.keySet()) {
			Integer val1 = ng2.counts.get(key);
			Integer val2 = ng1.counts.get(key);
			if (val1 == 0 && val2 == null) continue;
			if (val2 == null) {
				System.out.println("mismatch: " + key + " " + val2 + " " + val1);
				return false;
			}
			if (val1 != val2) {
				System.out.println("mismatch: " + key + " " + val2 + " " + val1);
				return false;
			}
		}
		return true;
	}
	
	public double coverageRatio() {
		double result = coverage.size();
		return result/str.length();
	}
	
	public void debug(String msg) {
		if (DEBUG) System.out.println(msg);
	}
	
	/** add a distance */
	public void addDistance(int distance) {
		if (distances == null) distances = new ArrayList<Integer>();
		distances.add(distance);
	}
	/** increment the given factor by 1 */
	public void incrementFactor(int factor) {
		if (factorCounts == null) 
			factorCounts = new HashMap<Integer, Integer>();
		Integer val = factorCounts.get(factor);
		if (val == null) val = 0;
		val++;
		factorCounts.put(factor,  val);
	}
	
	/** email subject: Normality assumptions in Z340 histogram result */
	public static void testRichardBean() {
		System.out.println(Ciphers.Z340);
		String cipher = Periods.rewrite3(Ciphers.Z340, 19);
		System.out.println(cipher);
		NGramsBean ng = new NGramsBean(2, cipher);
		ng.dump();
	}
	public static void main(String[] args) {
		//String cipher = "T41M4W189O951XS234E12A14V9E27E24X9X2NTWOO2XT91J2H2R32UXAXE89NRY151E389?5H6D7UA4IOR1V5A10EI6BE3PSV8WC7FJ4J3F12YF9D4Z10GT5K10IPXDW7L3KNNQ11QTWA5TR1012R11M5B11HBE8GLJYSY2UXC6TZ3GN4MOS7IC1H2?498PHIT6TI9TOLK5R5SO3FHT2W95T93PUF429AST245EY851378YO?C5O1M7B9A8?71U3I8P3X8NLXKOZM9I26SWYPJCNI8296HXIT2CP9LHA6ES4RY2M578S62?46MLJPZIW77O33YXVEU6PONN1TRT6K2SQW499O17RSY47198V787412?M2N8LP4321TK23RD3E532A2QW83HH3R5SA19OI637611SR4U?923PJI3S6WXK7OMV9Y7U1NG4DE2CHQ5LRT54AHHPE734582W1E6?7S12O84P8HEC9K2GDBI2NLN6USQ9TJIFT5HMKY7TR1O66NY32795A?9S6P77SO2K5X3I8EIS352F4IT1RHLDS6Q82E31TARVY4961S5E2?4TY4I3W9ORPY724JE2O2N5F7QT8HXJE329OZE57RNY3M8916ZU15?26DCB9JI2ZXKRQ38VE79YWSOPM78HNKNG32ALEHEF5SY4T6361V41E829?3TRP9HGFPZI2ILJ1XKVU8IED5T4N1OSQOWKI9CYW7NE25AML46RVY58261?1J167OKP2OLB1NK4EQWFJI7J9N6O9T3H546LLE92VY82433?4J9W844L3PQIXJ29ZK72I35ZN8O663H6R1AWHQE198575SE4?15P67W8K67IF2WSQ778I2Q7HJZXKGRJ3MTY2942SE59SN2?M16#SP6O@E7SK2I4QFE!#YPN*XZVNE7NE9SXQW95$7ASY3MAI5Y83E44N827&J?46PUI4ZK@O8DX$THW6389L8O&JFN5QSHIES78#2E7W4RVY3912SN595@$91#?834XJ8QK6U74N5PYQ5XGHMWTJNR29AFH16XRRY92656SE4?44PXI59QKWKZ8KI3Y8NZ9BVQ6YBJ7XI53AJIWE96SY1524SP4?M2S2P37O&8Q#WV@J63#23O2T&6T183A@H15149458SE5?";
		//String cipher = "H+M8|CV@KN:^j*Xz6-BpzOUNyBSMF;+B<2_Rq#2pTfp+fZ+1*H#2b^k.#K(MVVW)+kdll%WO&D->#Z3P>LEB+*5k.LpclddG+4y7t-cYAyp+l2_cFd9^%OF7G)8KjROLKJ5J+J|<z2G++PYLR/<U^D(+4(zpOGp+2|R(UVFFz9Ucy5C^W(c29^4OFT-+zF*K<SBKlXz2PYAKy.LWBOTB|TC7zp+8R)WkYM(++kN|TB5-|Dt*5cZG>/JNbVM)+l)2E.B)<Sf9pl/C6N:(+H*;G1BCOO|OB35c_8MFc+ztZb&RcW<SB.;++dpD5ct+FHE5FV52M5||.UqLRR+5>f|p";
//		String cipher = Ciphers.cipher[0].cipher;
		//String re = Scytale.encode(cipher, 19);
		//String cipher = Ciphers.Z408;
//		String cipher = "ABCDEFGHIJKLMNOGPQRSTUVBGJHWQBGLXPYZKNMCaSRObNcIBdeeSAfMghiDTZGjkBLQblCUEHPmOMNOGPQLBJnjMgCEiOoGSpKOqPKrUBdsMtYIHtruvVMIJXDZXwxHyCGnMmCeByYQeeSuBxUJtHYCqMArXGmHCaMJUDJnOBPzQLM0CGmHYdASsrHCBiJGMdPDzSNr1OMuHovCaDmJNvbSzTwxMWJIgPa";
//		cipher = "KBS<K*FzKFc_2l+pJ*H+(:N6FByNUOzpB)TFO4^92yAYc-t7yN;lp9fS<O6zX*j^:pd-^C5ycU4+Gddlc+HC/E2#l+-@VC|8MBE+(W4+RR-L.k5*+(R>)B.NJ/zKzFFVU#>Mc|f>|5ldL>P3ZOpD|)MVbtU<92+pG%lHFpqU.|VM(D&OWD^Nk+GZc5*+G|4+(WVpd+L5VF5E2#k+)YPkW)R-4BT|+5(RL.kS<Wc2tc4D^bpK#<|z7CT|+(MYJ+J/z*1Ztz+c+;.B+Zf+HKLOBWL.y8+pORjK8fT8_c13B+&bp2#qR_GAYP6zXlBT7FO%^922|OOCB1GRM<B+;FMS";
		//String cipher = Ciphers.Z340_SOLUTION_UNTRANSPOSED;
		String cipher = "H+M8|CV@KEB+*5k.LdR(UVFFz9<>#Z3P>L(MpOGp+2|G+l%WO&D#2b^D(+4(5J+VW)+kp+fZPYLR/8KjRk.#K_Rq#2|<z29^%OF1*HSMF;+BLKJp+l2_cTfBpzOUNyG)y7t-cYA2N:^j*Xz6dpclddG+4-RR+4Ef|pz/JNb>M)+l5||.VqL+Ut*5cUGR)VE5FVZ2cW+|TB45|TC^D4ct-c+zJYM(+y.LW+B.;+B31cOp+8lXz6Ppb&RG+BCOTBzF1K<SMF6N*(+HK29^:OFTO<Sf4pl/Ucy59^W(+l#2C.B)7<FBy-dkF|W<7t_BOYB*-CM>cHD8OZzSkpNA|K;+";
		int nr = 1; int i=2;
		while (nr > 0) {
			NGramsBean bean = new NGramsBean(i, cipher);
			System.out.println(i + " numRepeats " + bean.numRepeats());
			System.out.println(bean.repeatsInfo());
			nr = bean.numRepeats();
			i++;
		}
//		int i = 4;
//		NGramsBean bean = new NGramsBean(6, cipher);
//		System.out.println("numRepeats " + bean.numRepeats());
//		System.out.println(bean.repeatsInfo());
//		bean.dump();
//		for (int i=2; i<=5; i++) {
//			testShuffle(Ciphers.Z408, i, 1000000);
//			testShuffle(Ciphers.Z340, i, 1000000);
//			testShuffle(cipher, i, 10000);
//		}
		//testShuffle2(Ciphers.Z340, 2);
//		testShuffle(Ciphers.cipher[1].solution.toUpperCase(), 4, 1000000);
		
		/*for (int i=0; i<10000; i++) {
			bean = new NGramsBean(3, CipherTransformations.shuffle(Ciphers.cipher[0].cipher));
j			System.out.println(bean);
		}*/
		//testSlice();
		//testRegionsWithNoBigrams();
		//testShuffle();
		
		//bean.dump();
		/*for (String ngram : bean.repeats) {
			System.out.println(ngram + ": " + bean.positions.get(ngram));
			
			Integer prev = null;
			for (Integer curr : bean.positions.get(ngram)) {
				if (prev != null) {
					System.out.println(" - difference: " + (curr-prev) + ". factors: " + factors(curr-prev));
				}
				prev = curr;
			}
		}*/
		
		//testRichardBean();
	}
	
}
