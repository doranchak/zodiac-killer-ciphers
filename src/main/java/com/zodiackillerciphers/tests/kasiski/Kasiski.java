package com.zodiackillerciphers.tests.kasiski;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.ciphers.FatesUnwind;
import com.zodiackillerciphers.ciphers.algorithms.Scytale;
import com.zodiackillerciphers.lucene.Stats;
import com.zodiackillerciphers.ngrams.NGramsBean;
import com.zodiackillerciphers.ngrams.Periods;
import com.zodiackillerciphers.stats.StatsWrapper;
import com.zodiackillerciphers.transform.CipherTransformations;

public class Kasiski {
	public static int MAX_FACTOR=100;
	public static List<Integer> factors(int x) {
		List<Integer> f = new ArrayList<Integer>();
		for (int i=2; i<MAX_FACTOR && i<=x; i++) {
			if (x % i == 0) f.add(i);
		}
		return f;
	}
	
	/** perform kasiski exam.  detects repeated ngrams.  computes distances between repetitions
	 * and all factors that divide those distances.  tabulates the factor counts by ngram length.
	 * 
	 * if shuffles > 0, then perform shuffle study
	 * 
	 */
	public static KasiskiResult exam(String cipher, int shuffles) {
		KasiskiResult kr = examSub(cipher, true);
		kr.dump();
		if (shuffles > 0) {
			/** init statistics for factor counts by ngram length, used during shuffle studies */
			Map<Integer, Map<Integer, StatsWrapper>> factorCountsByLengthStats = new HashMap<Integer, Map<Integer, StatsWrapper>>(); 
			for (Integer length : kr.factorCountsByLength.keySet()) {
				for (Integer factor : kr.factorCountsByLength.get(length).keySet()) {
					Map<Integer, StatsWrapper> map = factorCountsByLengthStats.get(length);
					if (map == null) map = new HashMap<Integer, StatsWrapper>();
					factorCountsByLengthStats.put(length, map);
					StatsWrapper stats = map.get(factor);
					if (stats == null) stats = new StatsWrapper();
					map.put(factor,  stats);
					stats.name = length + "	" + factor;
					stats.actual = kr.factorCountsByLength.get(length).get(factor);
				}
			}
			
			for (int i=0; i<shuffles; i++) {
				cipher = CipherTransformations.shuffle(cipher);
				KasiskiResult kr2 = examSub(cipher, false);
				for (Integer length : kr.factorCountsByLength.keySet()) {
					for (Integer factor : kr.factorCountsByLength.get(length).keySet()) {
						Map<Integer, StatsWrapper> map = factorCountsByLengthStats.get(length);
						StatsWrapper stats = map.get(factor);
						
						Map<Integer, Integer> map2 = kr2.factorCountsByLength.get(length);
						if (map2 == null)
							stats.addValue(0);
						else {
							Integer count = map2.get(factor);
							if (count == null) stats.addValue(0);
							else stats.addValue(count);
						}
					}
				}
			}
			System.out.println(StatsWrapper.header());
			for (Integer length : factorCountsByLengthStats.keySet())
				for (StatsWrapper sw : factorCountsByLengthStats.get(length).values()) 
					sw.output();
			
			analyze(factorCountsByLengthStats);
		}
		return kr;
	}
	
	/** for a given repeating fragment length, determine how much significance is covered
	 * by a particular factor.
	 * @param map
	 */
	static void analyze(Map<Integer, Map<Integer, StatsWrapper>> map) {
		System.out.println("Sigma sum analysis:");
		for (Integer length : map.keySet()) {
			Map<Integer, StatsWrapper> stats = map.get(length);
			int maxFactor = Integer.MIN_VALUE;
			for (Integer factor : stats.keySet())
				maxFactor = Math.max(factor,  maxFactor);
			double maxSigma = Double.MIN_VALUE; // used as a substitute for infinite sigma values
			for (StatsWrapper sw : stats.values()) {
				double sigma = sw.sigma();
				if (Double.isInfinite(sigma)) sigma = 1;
				maxSigma = Math.max(maxSigma, sigma);
			}
			for (int f = 2; f<=maxFactor; f++) {
				double sum = 0;
				for (Integer factor : stats.keySet()) {
					if (factor % f == 0) {
						double sigma = stats.get(factor).sigma(); 
						if (Double.isInfinite(sigma)) sigma = maxSigma;
						sum += sigma;
					}
				}
				if (sum > 0) System.out.println(length + "	" + f + "	" + sum);
			}
		}
	}
	
	public static KasiskiResult examSub(String cipher, boolean dump) {
		KasiskiResult kr = new KasiskiResult();
		//Map<Integer, Integer> factorCounts = new HashMap<Integer, Integer>();
		
		boolean go = true;
		int n = 1;
		
		while (go) {
			NGramsBean bean = new NGramsBean(n, cipher);
			kr.addBean(bean);
			for (String str : bean.repeats) {
				String line = "n" + n + ": " + str + " ";
				List<Integer> locations = bean.locationsFor(str);
				if (locations.size() < 2) {
					System.err.println("Expected at least 2, got " + locations.size() + " for " + str);
					continue;
				}
				//line += locations.size() + " ";
				for (Integer i : locations) line += i + " ";
				if (dump) System.out.println(line);
				
				for (int j=1; j<locations.size(); j++) {
					int diff = locations.get(j)-locations.get(j-1);
					line = " - locs " + locations.get(j-1) + " " + locations.get(j) + " diff " + diff + " factors "; 
					List<Integer> factors = factors(diff);
					for (Integer f: factors) {
						kr.incrementFactor(f, n);
						line += f + " ";
//						Integer val = factorCounts.get(f);
//						if (val == null) val = 0;
//						val++;
//						factorCounts.put(f, val);
					}
					if (dump) System.out.println(line);
				}
			}
			if (bean.repeats.size() == 0) {
				go = false;
				break;
			}
			n++;
		}
		
//		for (Integer key : factorCounts.keySet()) {
//			System.out.println(key + ", " + factorCounts.get(key) + ", " + (key * factorCounts.get(key)));
//		}
		
		
		return kr;
	}
	
	/** count ngram repeats based on their distances */
	public static int[] distribution(String cipher, int n, boolean showAll) {
		Map<Integer, Integer> counts = new HashMap<Integer, Integer>();
		// track position of most recent occurrence of the ngram  
		Map<String, Set<Integer>> positions = new HashMap<String, Set<Integer>>(); 
		for (int i=0; i<cipher.length()-n+1; i++) {
			String ngram = cipher.substring(i,i+n);
			//System.out.print(i + " " + ngram + " ");
				
			Set<Integer> set = positions.get(ngram);
			if (set == null) set = new HashSet<Integer>();
			positions.put(ngram, set);
			for (Integer pos : set) {
				int diff = i-pos;
				Integer val = counts.get(diff);
				if (val == null) val = 0;
				val++;
				counts.put(diff, val);
			}
			set.add(i);
			//System.out.print(set);
			//System.out.println();
		}
		int max = -1;  int which = -1;
		for (Integer key : counts.keySet()) {
			if (showAll) System.out.println(counts.get(key) + " " + key);
			if (counts.get(key) > max) {
				max = counts.get(key);
				which = key;
			}
		}
		//System.out.println("Max " + max + " at " + which);
		return new int[] {which, max};
	}
	
	/** bartW's way of performing the KE   http://www.zodiackillersite.com/viewtopic.php?p=48156#p48156  */
	public static void bartW(String input) {
		int length = input.length();
		for (int offset = 1; offset < length; offset++) {
			bartWCount(input, offset, true);
		}
	}
	
	public static int bartWCount(String input, int offset, boolean dump) {
		int length = input.length();
		int count = 0;
		String shifted = "";
		for (int index = 0; index < length; index++) {
			char ch = input.charAt((index + offset) % length);
			if (dump) shifted += ch;
			if ((input.charAt(index) == ch)) {
				count++;
				if (dump)
					System.out.println("repeat: " + input.charAt(index) + " " + index + " " + (index + offset));
			}
		}
		if (dump) System.out.println(count + " " + offset);
		if (dump) System.out.println("shifted: " + shifted);
		return count;
	}

	/** shuffle and keep stats for each offset */
	public static void bartWShuffle(String input, int shuffles) {
		System.out.println(new Date().getTime());
		int length = input.length();
		int count;
		Map<Integer, StatsWrapper> map = new HashMap<Integer, StatsWrapper>(); 
		for (int offset = 1; offset < length; offset++) {
			StatsWrapper stats = new StatsWrapper();
			stats.actual = bartWCount(input, offset, false);
			stats.name = "Coincidence count for offset " + offset;
			map.put(offset,  stats);
			for (int i=0; i<shuffles; i++) {
				String shuffled = CipherTransformations.shuffle(input);
				stats.addValue(bartWCount(shuffled, offset, false));
			}
		}
		System.out.println(StatsWrapper.header());
		for (StatsWrapper stats : map.values()) stats.output();
		System.out.println(new Date().getTime());
	}
	
	// same as bartW but return the max count observed over all offsets
	public static int bartWMax(String input) {
		int length = input.length();
		int count;
		int max = 0;
		for (int offset = 1; offset < length; offset++) {
			count = 0;
			for (int index = 0; index < length; index++) {
				if ((input.charAt(index) == input.charAt((index + offset)
						% length))) {
					count++;
					//System.out.println("repeat: " + input.charAt(index) + " " + index + " " + (index+offset));
				}
			}
			//System.out.println(count + " " + offset);
			max = Math.max(max, count);
		}
		return max;
	}

	/** return list of positions for a given offset (shift) instead */
	public static Set<Integer> bartWPositions(String input, int offset) {
		int length = input.length();
		int count;
		Set<Integer> set = new HashSet<Integer>();
		count = 0;
		for (int index = 0; index < length; index++) {
			if ((input.charAt(index) == input.charAt((index + offset) % length))) {
				count++;
				set.add(index % length);
				set.add((index + offset) % length);
				//System.out.println((index % length) + "," + ((index + offset) % length));
			}
		}
		// System.out.println("" + offset + "," + count);
		return set;
	}
	
	/** modified bartW's way of performing the KE, so that it is run on all periods (including Scytale.decode) */
	public static void bartWPeriodic(String input) {
		
		for (int period=1; period<=170; period++) {
			String[] ciphers = new String[] { Scytale.decode(input, period),
					Periods.rewrite3(input, period) };
			for (int i=0; i<ciphers.length; i++) {
				String type = i == 0 ? "scytale" : "period";
				String decoded = ciphers[i];
				int length = decoded.length();
				int count;
				for (int offset = 1; offset < length; offset++) {
					count = 0;
					for (int index = 0; index < length; index++) {
						if ((decoded.charAt(index) == decoded.charAt((index + offset)
								% length))) {
							count++;
							//System.out.println("repeat: " + input.charAt(index) + " " + index + " " + (index+offset));
						}
					}
					System.out.println(count + " " + offset + " " + type + " " + period);
				}
			}
		}
	}

	/** return the intersection of KE exam positions at the given shift, and periodic repeating bigram positions at the given period */
	public static void intersections(String input, int shift, int period) {
		Set<Integer> set1 = bartWPositions(input, shift);
		Set<Integer> set2 = Periods.positions(input, period);
		Set<Integer> intersection = new HashSet<Integer>(set1);
		intersection.retainAll(set2);
		System.out.println(shift + "	" + period + "	" + set1.size() + "	" + set2.size() + "	" + intersection.size());
	}
	
	public static void testCompare() {
		for (int c = 0; c<2; c++) {
			for (int n = 1; n<5; n++) {
				String cipher = Ciphers.cipher[c].cipher;
				for (int period=1; period<cipher.length()/2; period++) {
					String re = Periods.rewrite3(cipher, period);
					//System.out.println(period);
					int[] max = distribution(re, n, false);
					System.out.println((c==0?"z340":"z408") + "	" + n + "	" + period + "	" + max[0] + "	" + max[1]);
				}
			}
		}
	}
	
	public static void testShuffle() {
		String cipher = Ciphers.cipher[1].cipher;
		/** maps max KE score to number of occurrences */ 
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		DescriptiveStatistics d = new DescriptiveStatistics();
		for (int i=0; i<1000000; i++) {
			if (i%10000 == 0) System.out.println(i+"...");
			cipher = CipherTransformations.shuffle(cipher);
			int[] dist = distribution(cipher, 1, false);
			Integer key = dist[1];
			Integer val = map.get(key);
			if (val == null) val = 0;
			val++;
			map.put(key, val);
			d.addValue(key);
		}
		for (Integer key : map.keySet()) {
			System.out.println(key + " " + map.get(key));
		}
		System.out.println("Min: " + d.getMin());
		System.out.println("Max: " + d.getMax());
		System.out.println("Mean: " + d.getMean());
		System.out.println("Std Dev: " + d.getStandardDeviation());
	}
	public static void testShuffle2(String cipher, int shuffles) {
		int actual = bartWMax(cipher);
		StatsWrapper stats = new StatsWrapper();
		stats.actual = actual;
		stats.name = "Max unigram repeats observed over all shifts";
		Map<Integer, Integer> counts = new HashMap<Integer, Integer>();  
		for (int i=0; i<shuffles; i++) {
			cipher = CipherTransformations.shuffle(cipher);
			int max = bartWMax(cipher);
			stats.addValue(max);
			Integer count = counts.get(max);
			if (count == null) count = 0;
			count++;
			counts.put(max,  count);
		}
		System.out.println(counts);
		stats.output();
	}
	
	public static void testFactors() {
		//int[] nums = new int[] {78, 75, 150, 162, 109, 35, 42, 6, 39, 105, 30, 71, 10, 129, 113, 137, 26, 15, 69, 106, 133, 149, 5, 43, 25, 136, 110, 7, 81, 163, 84, 70, 3, 80, 68, 142, 120, 143, 21, 40, 23, 128, 100, 60, 115, 50, 66, 93, 2, 119, 20, 147};
		int[] nums = new int[] {61, 163, 49, 92, 97, 91, 23, 149, 74, 16, 46, 21, 169, 136, 189, 173, 39, 63, 98, 145, 32, 64, 141, 194, 73, 53, 115, 126, 128, 166, 35, 157, 176, 187, 37};
		for (int num : nums) {
			List<Integer> factors = factors(num);
			for (Integer factor : factors) System.out.println(factor);
		}
	}
	
	/** if length mod period == 0, there are exactly (length/period) columns.
	 * 
	 */
	public static double meanIoc(int period, String ciphertext) {
		double ioc = 0;
		for (int i = 0; i < period; i++) {
			ioc += Stats.iocColumn(ciphertext, period, i);
		}
		return ioc/=period;
	}	
	public static MeanIoc meanIocShuffle(int period, String ciphertext, int shuffles) {
		MeanIoc meanIoc = new MeanIoc();
		StatsWrapper stats = new StatsWrapper();
		stats.actual = meanIoc(period, ciphertext);
		stats.name = "meanIoc period " + period;
		for (int i=0; i<shuffles; i++) {
			ciphertext = CipherTransformations.shuffle(ciphertext);
			stats.addValue(meanIoc(period, ciphertext));
		}
		//stats.output();
		meanIoc.setActual(stats.actual);
		meanIoc.setMean(stats.stats.getMean());
		meanIoc.setPeriod(period);
		meanIoc.setSigma(stats.sigma());
		meanIoc.setStdDev(stats.stats.getStandardDeviation());
		return meanIoc;
	}
	
	public static List<MeanIoc> unusualMeanIoc(int periodStart, int periodEnd, String ciphertext, int shuffles) {
		List<MeanIoc> list = new ArrayList<MeanIoc>();
		for (int p = periodStart; p <= periodEnd; p++) {
			list.add(meanIocShuffle(p, ciphertext, shuffles));
		}
		Collections.sort(list, new Comparator<MeanIoc>() {
			@Override
			public int compare(MeanIoc o1, MeanIoc o2) {
				return Double.compare(o2.sigma,  o1.sigma);
			}
		});
		return list;
	}
	
	public static void main(String[] args) {
		//distribution(Ciphers.cipher[1].cipher, 1, true);
		//bartW("YENSZNUMGLNYYRFVHENMZFZZFDHZVTQHAKXFPKCZPJITSMRYKVSTVOYNKTRRLUVP");
		//bartW("ABCDEFGHIJKLMNBOPQRSQIPTAUVWXYZabMcdeVfgIBhNiIQNjIkMlCEmBnIMopqAUVdIdUnbrcstoQCNXIYnOSqdnTbGbEPuvwDAIuTHDNxyolpFmzEJQ0GLfynvHNTbVMBS1HYT2QATH0bVN1loTzbhOsETUNzaAE3lkx1TTUsHozT4N5To6NYGoDIueGmJCN7UGEUTigIapoFHDVOnpGjVlTu0Tr1Im89bUGaMLHT!XobQPf9bIUA0oom7zAtNnbg\"U5N#NoASIGqGQIm2LG\"I$%aLVGGAoOf5&Q'RGUTVNj'FieMnTtTtVlIKIxOtAHlAUU9MjKss7bnorSUZ");
//		bartW(Ciphers.Z340);
		
		String cipher = Ciphers.Z340;
		for (int period=1; period < cipher.length()/2; period++) {
			System.out.println(bartWCount(cipher, period, false) + " at period " + period);
		}
				
//		bartWShuffle(Ciphers.Z340, 1000);
//		System.out.println(exam("KBFXTTJGGGTTXNQBMWGKJWAWSWLUTNKWKYAKNLMQGKQSARFMZMEWETXJMNDPAMOWPDICKLGCWWQYHMBHPGUWVRXMEBSTNFHKGBCEASWL", 1000));
		

//		String cipher ="LFWKIMJCLPSISWKHJOGLKMVGURAGKMKMXMAMJCVXWUYLGGIISWALXAEYCXMFKMKBQBDCLAEFLFWKIMJCGUZUGSKECZGBWYMOACFVMQKYFWXTWMLAIDOYQBWFGKSDIULQGVSYHJAVEFWBLAEFLFWKIMJCFHSNNGGNWPWDAVMQFAAXWFZCXBVELKWMLAVGKYEDEMJXHUXDAVYXL"; // 6-letter keyword SYSTEM
		//String cipher = "GTXIPEMSSGFNUINVDLUSGPWMMXWIUYBUSGFIKJYQQWIILCDIGFNDHCGAYEUYDWLLULLWEGIIFADMFREQBIWLLWZFWBMPPRSNRPLLULLWSGPBVTDMKNPICSGWMWBGFIIFJVYSGEXVFTEAJRSJQUVIZTURNFTPUWXHDPCCWGZJQUKMSGHQXHWMEAXCSSZEKWWTKZJRUKGEZMFRETTSUKDQMBAYKSJDZNFYTRYICRCQWYEIQMDPBSSVTBBUG"; // quagmire 1, period 3
		//String cipher = Ciphers.Z340;
//		System.out.println(exam(
//				cipher,
//				10000));
//		for (int p=1; p<100; p++) System.out.println(p + "	" + meanIoc(p, cipher));

//		List<MeanIoc> list = unusualMeanIoc(2, 30, cipher, 10000); 
//		for (MeanIoc m : list) 
//			System.out.println(m);

//		testShuffle2(Ciphers.Z340, 10000);
//		testShuffle2(Ciphers.Z408, 1000000);
		//bartW(Ciphers.cipher[0].cipher);
		//bartWPeriodic(Ciphers.cipher[1].cipher);
		//testFactors();
		//bartWPositions(Ciphers.cipher[1].cipher, 61);
		//System.out.println(bartWPositions(Ciphers.cipher[0].cipher, 78));
		/*for (int shift=1; shift<=204; shift++) {
			for (int period=1; period<=204; period++) {
				intersections(Ciphers.cipher[1].cipher, shift, period);
			}
		}*/
		//testShuffle();
		//test("CSASTPKVSIQUTGQUCSASTPIUAQJB");
		//test(FatesUnwind.cipherFullDallison);
//		test("VQBUPPVSPGGFPNUEDOKDXHEWTIYCLKXRZAPVUFSAWEMUXGPNIVQJMNJJNIZYKBPNFRRHTBWWNUQJAJGJFHADQLQMFLXRGGWUGWVZGKFBCMPXKEKQCQQLBODOQJVEL");
//		System.out.println(factors(78));
//		System.out.println(factors(75));
//		System.out.println(factors(150));
//		System.out.println(factors(162));
//		System.out.println(factors(109));
//		System.out.println(factors(35));
//		System.out.println(factors(42));
//		System.out.println(factors(6));
//		System.out.println(factors(39));
//		System.out.println(factors(105));
//		System.out.println(factors(30));
//		System.out.println(factors(71));
//		System.out.println(factors(10));
//		System.out.println(factors(129));
//		System.out.println(factors(113));
//		System.out.println(factors(137));
//		System.out.println(factors(26));
//		System.out.println(factors(15));
//		System.out.println(factors(69));
//		System.out.println(factors(106));
//		System.out.println(factors(133));
//		System.out.println(factors(149));
//		System.out.println(factors(5));
//		System.out.println(factors(43));
//		System.out.println(factors(25));
//		System.out.println(factors(136));
//		System.out.println(factors(110));
//		System.out.println(factors(7));
//		System.out.println(factors(81));
//		System.out.println(factors(163));
//		System.out.println(factors(84));
//		System.out.println(factors(70));
//		System.out.println(factors(3));
//		System.out.println(factors(80));
//		System.out.println(factors(68));
//		System.out.println(factors(142));
//		System.out.println(factors(120));
//		System.out.println(factors(143));
//		System.out.println(factors(21));
//		System.out.println(factors(40));
//		System.out.println(factors(23));
//		System.out.println(factors(128));
//		System.out.println(factors(100));
//		System.out.println(factors(60));
//		System.out.println(factors(115));
//		System.out.println(factors(50));
//		System.out.println(factors(66));
//		System.out.println(factors(93));
//		System.out.println(factors(2));
//		System.out.println(factors(119));
//		System.out.println(factors(20));
//		System.out.println(factors(147));
	}
}
