package com.zodiackillerciphers.tests.unigrams;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.io.FileUtil;
import com.zodiackillerciphers.stats.StatsWrapper;
import com.zodiackillerciphers.transform.CipherTransformations;

public class AnomalousDistances {
	/**
	 * look for large gaps between occurrences of unigrams. if maxes is passed,
	 * then track max gap per symbol.
	 */
	public static double weightedAverageMaxGap(String cipher,
			Map<Character, Integer> maxes) {
		double result = 0;
		Map<Character, List<Integer>> map = Ciphers.positionMap(cipher);
		for (Character c : map.keySet()) {
			List<Integer> val = map.get(c);
			Collections.sort(val);
			int max = val.get(0); // include distance from start of cipher to first occurrence of symbol
			for (int i = 1; i < val.size(); i++) {
				max = Math.max(max, val.get(i) - val.get(i - 1));
			}
			// include distance from end of cipher to last occurrence of symbol
			max = Math.max(max, cipher.length() - 1 - val.get(val.size() - 1));
			
			if (maxes != null)
				maxes.put(c, max);
			result += max * val.size();
			// System.out.println(c + ": " + max + " " + val);
		}
//		System.out.println(result);
		result /= cipher.length();
		return result;
	}

	/** shuffle to compare given cipher's max gap weighted average to random */
	public static void shuffleMaxGap(String cipher, int shuffles) {
		double actual = weightedAverageMaxGap(cipher, null);
		int hits = 0;
		DescriptiveStatistics stats = new DescriptiveStatistics();
		for (int i = 0; i < shuffles; i++) {
			cipher = CipherTransformations.shuffle(cipher);
			double gap = weightedAverageMaxGap(cipher, null);
			if (gap >= actual)
				hits++;
			stats.addValue(gap);
		}
		double sigma = actual - stats.getMean();
		sigma /= stats.getStandardDeviation();

		System.out.println(stats.getMin() + "	" + stats.getMax() + "	"
				+ stats.getMean() + "	" + stats.getPercentile(50) + "	"
				+ stats.getStandardDeviation() + "	" + actual + "	" + sigma
				+ "	" + hits);
	}

	/** use shuffles to find anomalous gaps per symbol */
	public static Map<Character, StatsWrapper> anomalousGaps(String cipher, int shuffles) {
		Map<Character, StatsWrapper> map = new HashMap<Character, StatsWrapper>();
		Map<Character, Integer> actualMaxes = new HashMap<Character, Integer>();
		//Map<Character, Integer> hits = new HashMap<Character, Integer>();

		double actual = weightedAverageMaxGap(cipher, actualMaxes);
		System.out.println(actualMaxes);
		for (int i = 0; i < shuffles; i++) {
			cipher = CipherTransformations.shuffle(cipher);
			Map<Character, Integer> maxes = new HashMap<Character, Integer>();
			double gap = weightedAverageMaxGap(cipher, maxes);
			for (Character key : maxes.keySet()) {
				StatsWrapper val = map.get(key);
				if (val == null) {
					val = new StatsWrapper();
					val.actual = actualMaxes.get(key);
					val.name = "max gap distance for symbol " + key;
				}
				val.addValue(maxes.get(key));
				map.put(key, val);
//				int actualMax = actualMaxes.get(key);
//				if (maxes.get(key) >= actualMax) {
//					Integer h = hits.get(key);
//					if (h == null)
//						h = 0;
//					h++;
//					hits.put(key, h);
//				}
			}
		}
		for (Character key : map.keySet()) {
			StatsWrapper stats = map.get(key);
			//double sigma = actualMaxes.get(key) - stats.getMean();
//			sigma /= stats.getStandardDeviation();
//			System.out.println("max gap distance for symbol " + key + "	" + stats.getMin() + "	"
//					+ stats.getMax() + "	" + stats.getMean() + "	"
//					+ stats.getPercentile(50) + "	"
//					+ stats.getStandardDeviation() + "	" + actualMaxes.get(key) + "	" + sigma
//					+ "	" + hits.get(key));
			stats.output();

		}
		return map; // so we can run further tests
	}
	
	/*
	 * generate anomalous gaps stats per symbol, based on shuffles. then run more
	 * shuffles to determine how many samples contain unigrams with anomalous gaps
	 * that exceed some minimum sigma compared to the stats-per-symbol shuffles.
	 * 
	 * for example, Z340 has 6 symbols that have unigram gaps exceeding 1 standard
	 * deviation from the mean observed during shuffles.  how many shuffled ciphers
	 * have that many symbols exceeding 1 standard deviation?
	 */
	static void anomalousGapsNumberOfShufflesExceedingMinSigma(String cipher, int shuffles1, int shuffles2, double minStdDev) {
		Map<Character, StatsWrapper> stats = anomalousGaps(cipher, shuffles1);

		StatsWrapper shuffleStats = new StatsWrapper();
		shuffleStats.name = "Number of symbols with anomalous gaps exceeding " + minStdDev + " sigma";
		Map<Character, Integer> actualMaxes = new HashMap<Character, Integer>();
		weightedAverageMaxGap(cipher, actualMaxes);
		shuffleStats.actual = 0;
		for (Character c : actualMaxes.keySet()) {
			if (stats.get(c).sigma() >= minStdDev) {
				//System.out.println("actual hit: " + stats.get(c));
				shuffleStats.actual++;
			}
		}		
		
		for (int i=0; i<shuffles2; i++) {
			cipher = CipherTransformations.shuffle(cipher);
			actualMaxes = new HashMap<Character, Integer>();
			weightedAverageMaxGap(cipher, actualMaxes);
			int hits = 0;
			for (Character c : actualMaxes.keySet()) {
				stats.get(c).actual = actualMaxes.get(c);
				if (stats.get(c).sigma() >= minStdDev) {
					hits++;
//					if (i==0) {
//						System.out.println("Sample hit: ");
//						stats.get(c).output();
//					}
				}
			}
			shuffleStats.addValue(hits);
		}
		shuffleStats.output();
	}
	
	public static void process() {
		List<String> list =FileUtil.loadFrom("/Users/doranchak/Downloads/HITS XML Mapping (1).html/processed.html");
		StringBuffer sb = new StringBuffer();
		for (String line : list) {
			sb.append(line);
		}
		int count = 0;
		boolean inTag = false;
		for (int i=0; i<sb.length(); i++) {
			char c = sb.charAt(i);
			if (c == '<') {
				inTag = true;
			} else if (c == '>') {
				inTag = false;
			}
			if (sb.substring(i,i+20).startsWith("<div class=\"c ")) {
				System.out.println();
				count++;
			}
			if (!inTag && c != '>') System.out.print(c);
		}
		
	}
	
	/** Jarlve's "unigram distance" measurement   http://zodiackillersite.com/viewtopic.php?p=53902#p53902
	 **/
	public static int unigramDistance(String cipher) {
		int result = 0;
		Map<Character, Integer> previous = new HashMap<Character, Integer>();
		for (int i=0; i<cipher.length(); i++) {
			Character key = cipher.charAt(i);
			Integer val = previous.get(key);
			if (val != null) {
				result += i-val;
			}
			previous.put(key, i);
		}
		return result;
	}
	/** unigram distance, but for individual symbols */
	public static Map<Character, Integer> unigramDistancePerSymbol(String cipher) {
		Map<Character, Integer> map = new HashMap<Character, Integer>();
		Map<Character, Integer> previous = new HashMap<Character, Integer>();
		for (int i=0; i<cipher.length(); i++) {
			Character key = cipher.charAt(i);
			Integer result = map.get(key);
			if (result == null) result = 0;
			Integer val = previous.get(key);
			if (val != null) {
				result += i-val;
			}
			previous.put(key, i);
			map.put(key, result);
		}
		return map;
	}
	public static int unigramDistancePerSymbolShuffle(String cipher, int shuffles, boolean show) {
		
		Map<Character, StatsWrapper> statsMap = new HashMap<Character, StatsWrapper>();
		Map<Character, Integer> actuals = unigramDistancePerSymbol(cipher);
		for (Character key : actuals.keySet()) {
			StatsWrapper stats = new StatsWrapper();
			stats.name = "unigram distance for " + key;
			stats.actual = actuals.get(key);
			statsMap.put(key, stats);
		}
		
		for (int i=0; i<shuffles; i++) {
			cipher = CipherTransformations.shuffle(cipher);
			Map<Character, Integer> map = unigramDistancePerSymbol(cipher);
			for (Character key : actuals.keySet()) {
				statsMap.get(key).addValue(map.get(key));
			}
		}
		int outliers = 0;
		for (Character key : actuals.keySet()) {
			if (show) statsMap.get(key).output();
			if (Math.abs(statsMap.get(key).sigma()) >= 1.0) 
				outliers++;
		}
		if (show) System.out.println("outliers: " + outliers);
		return outliers;
	}
	
	public static void unigramDistanceShuffle(String cipher, int shuffles) {
		StatsWrapper stats = new StatsWrapper();
		stats.name = "Total unigram distance";
		stats.actual = unigramDistance(cipher);
		for (int i=0;i<shuffles; i++) {
			cipher = CipherTransformations.shuffle(cipher);
			stats.addValue(unigramDistance(cipher));
		}
		stats.output();
	}
	
	public static void outlierShuffle(String cipher, int shuffles1, int shuffles2) {
		StatsWrapper stats = new StatsWrapper();
		stats.name = "Number of unigram distance outliers (symbols with abs(sigma) >= 1.0)";
		stats.actual = unigramDistancePerSymbolShuffle(cipher, shuffles1, false);
		for (int i=0; i<shuffles2; i++) {
			cipher = CipherTransformations.shuffle(cipher);
			stats.addValue(unigramDistancePerSymbolShuffle(cipher, shuffles1, false));
		}
		stats.output();
	}
	
	/** count unique symbols in given string */
	public static int countUnique(String str) {
		if (str == null) return 0;
		Set<Character> set = new HashSet<Character>();
		for (int i=0; i<str.length(); i++) set.add(str.charAt(i));
		return set.size();
	}
	
	/** which substrings of the Z340 have unusually low counts of unique symbols?
	 * http://www.zodiackillersite.com/viewtopic.php?f=81&t=3444
	 * For example, many symbols avoid the middle 8 rows. 
	 **/
	public static void uniqueUnigrams(String cipher, int shufflesPerLength) {
		/** reference set: how many unique symbols should we expect in a substring of a Z340 scramble */
		Map<Integer, StatsWrapper> reference = new HashMap<Integer, StatsWrapper>();
		String shuffle = cipher;
		for (int L=17; L<=340; L++) {
			System.out.println("Computing reference for L=" + L + "...");
			StatsWrapper stats = new StatsWrapper();
			stats.name = "Length " + L;
			for (int i=0; i<shufflesPerLength; i++) {
				shuffle = CipherTransformations.shuffle(shuffle);
				stats.addValue(countUnique(shuffle.substring(0, L)));
			}
			reference.put(L, stats);
		}
		
		for (int pos1=0; pos1<cipher.length(); pos1++) {
			for (int pos2=pos1+17; pos2<cipher.length(); pos2++) {
				String sub = cipher.substring(pos1, pos2);
				StatsWrapper stats = reference.get(pos2-pos1);
				stats.actual = countUnique(sub);
				//if (Math.abs(stats.sigma()) >= 2) {
					String name = stats.name;
					stats.name = stats.sigma() + "	" + pos1 + " " + pos2 + " " + name;
					stats.output();
					stats.name = name;
				//}
			}
		}
	}
	
	/** find every symbol from the cipher alphabet that isn't found in the given string.
	 * return sum of symbol frequencies for each such found symbol. 
	 */
	public static int phobicUnigrams(Map<Character, Integer> counts, String str, boolean show, List<Character> results) {
		Set<Character> seen = new HashSet<Character>();
		for (int i=0; i<str.length(); i++) seen.add(str.charAt(i));
		
		int sum = 0;
		for (Character key : counts.keySet()) {
			if (seen.contains(key)) continue;
			if (show)
				System.out.println(key+" " + counts.get(key));
			sum += counts.get(key);
			if (results != null) results.add(key);
		}
		return sum;
	}
	public static void testPhobicUnigrams() {
		String cipher = Ciphers.Z340;
		//System.out.println(phobicUnigrams(Ciphers.countMap(cipher), cipher.substring(7*17,7*17 + 7*17), true));
		System.out.println(phobicUnigrams(Ciphers.countMap(cipher), cipher.substring(51, 272), true, null));
//		cipher = CipherTransformations.shuffle(cipher);
//		System.out.println(phobicUnigrams(Ciphers.countMap(cipher), cipher.substring(7*17,7*17 + 7*17), true));
	}

	/** which substrings of the Z340 are unusually "unigram phobic"?
	 * http://www.zodiackillersite.com/viewtopic.php?f=81&t=3444
	 * For example, many symbols avoid the middle 8 rows.
	 * 
	 * use step=1 for all substrings
	 * use step=17 for just rows
	 *  
	 **/
	public static void phobicUnigrams(String cipher, int shufflesPerLength, int step) {
		System.out.println(cipher);
		// best (most phobic) region observed for the given substring length in the given cipher 
		Map<Integer, StatsWrapper> reference = new HashMap<Integer, StatsWrapper>();
		Map<Character, Integer> counts = Ciphers.countMap(cipher);
		
		for (int pos1=0; pos1<cipher.length(); pos1+=step) {
			for (int pos2=pos1+17; pos2<=cipher.length(); pos2+=step) {
				int L = pos2-pos1;
				String sub = cipher.substring(pos1, pos2);
				StatsWrapper stats = reference.get(L);
				List<Character> unigrams = new ArrayList<Character>(); 
				int value = phobicUnigrams(counts, sub, false, unigrams);
				if (stats == null || value > stats.actual) {
					// if this region has the same length as one already observed, and it
					// has more phobic unigrams, replace the stats 
					stats = new StatsWrapper();
					stats.actual = value;
					reference.put(L, stats);
					stats.name = pos1 + " " + pos2 + " " + unigrams + " Length " + L;
				}
			}
		}
		String tab = "	";	
		String shuffle = cipher;
		for (int L=17; L<=cipher.length(); L+=step) {
			StatsWrapper stats = reference.get(L);
			for (int i=0; i<shufflesPerLength; i++) {
				shuffle = CipherTransformations.shuffle(shuffle);
				stats.addValue(phobicUnigrams(counts, shuffle.substring(0, L), false, null));
			}
			stats.name = stats.sigma() + tab + stats.name;
			stats.output();
		}
		
	}
	
		
	public static void main(String[] args) {
		phobicUnigrams(Ciphers.Z408, 100000, 17);
//		testPhobicUnigrams();
//		System.out.println(weightedAverageMaxGap(Ciphers.Z340, null));
		//System.out.println(weightedAverageMaxGap(Ciphers.cipher[8].cipher, null));
		//shuffleMaxGap(Ciphers.Z340, 1000000);
		 //shuffleMaxGap(Ciphers.Z340, 1000);
		//anomalousGaps(Ciphers.Z340T, 100000);
//		anomalousGapsNumberOfShufflesExceedingMinSigma(Ciphers.Z340, 10000, 10000, 1.0);
//		System.out.println(unigramDistance("HER>pl^VPk|1LTG2dNp+B(#O%DWY.<*Kf)2<clRJ|*5T4M.+&BFz69Sy#+N|5FBc(;8RlGFN^f524b.cV4t++|FkdW<7tB_YOB*-Cc>MDHNpkSzZO8A|K;+(G2Jfj#O+_NYz+@L9d<M+b+ZR2FBcyA64K-zlUV+^J+Op7<FBy-U+R/5tE|DYBpbTMKOBy:cM+UZGW()L#zHJSpp7^l8*V3pO++RK2_9M+ztjd|5FP+&4k/yBX1*:49CE>VUZ5-+|c.3zBK(Op^.fMqG2RcT+L16C<+FlWB|)L++)WCzWcPOSHT/()pp8R^FlO-*dCkF>2D(#5+Kq%;2UcXGV.zL|"));
//		System.out.println(unigramDistance(Ciphers.Z340T));
		//System.out.println(unigramDistancePerSymbol(Ciphers.Z340));
//		unigramDistancePerSymbolShuffle(Ciphers.Z408, 10000);
		//System.out.println(unigramDistancePerSymbolShuffle(Ciphers.Z340, 10000, false));
//		unigramDistancePerSymbolShuffle(CipherTransformations.shuffle(Ciphers.Z340), 10000);
//		outlierShuffle(Ciphers.Z408, 10000, 1000);
//		outlierShuffle(Ciphers.Z340, 10000, 1000);
		//process();
//		unigramDistanceShuffle(Ciphers.Z408, 10000000);
//		unigramDistanceShuffle(Ciphers.Z340, 10000000);
//		unigramDistanceShuffle("ABCDEDFGHIJKLMNOPQRSTUVWXYZAabcdefghijCkFlmnoEpVqrstuDIvBYJwxAGyKz0MCjZ1Qh234SWk5X67eaEdUqFbr8MmNlZ9tuwQ!ciWzJI07HnpUPvk2DYBGaNdSr\"AjKwC#XbmEZgM0clkrsoFHPIqKQ$LS3XuTEYZAWM%QJ&SkrX4Z1tjwEkrCqK'nV!o2fDaNhpxFZ8zwI3vk\"M(QbrO74ZchYkAlrgUZxsSuC)FXIxYBGRE!M5noJAjLt3zyC6Q7q9UHPk1SF8t*XDIvBE)xYGH&MT2dQm+WPz#SaAxCvBuNrKF%X,ce0-J7dE(MfUibQ'nVxIGHZ4+", 10000000);
//		unigramDistanceShuffle("ABCDEDFGHIJKLMNOPQRSTUVWXYNAWZabcdefghCiFjNklEmVnopqNDIrBYJstAGuKvwMChNFQfxyzSWi0X12cWEbUnFZo3MNNjN4qNsQ5agWvJIw2HkmUPrixDYBGWNbSo6AhKsC7XZNENeMwajioplFHPInKQ8LSyXNTEYNAWM9QJ9SioXzNFqhsEioCnK!kV5lxdDWNfmtFN3vsIyri6M!QZoO2zNafYiAjoeUNtpSNCAFXItYBGRE5M0klJAhLqyvuC1Q2n4UHPiFSF3qQXDIrBEAtYGH9MTxbQNEWPv7SWAtCrBNNoKF9XMacwEJ2bE!MdUgZQ!kVtIGHNzE", 10000000);
	}
}
