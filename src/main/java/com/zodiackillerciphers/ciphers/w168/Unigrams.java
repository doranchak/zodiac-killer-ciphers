package com.zodiackillerciphers.ciphers.w168;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.io.FileUtil;
import com.zodiackillerciphers.stats.StatsWrapper;

/** unigram stats */
public class Unigrams {
	/** compute unigram stats for the given samples of text */
	public static Map<Character, StatsWrapper> statsFor(List<String> lines) {
		
		String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ. ";
		Map<Character, StatsWrapper> stats = new HashMap<Character, StatsWrapper>();
		for (int i=0; i<alphabet.length(); i++) {
			stats.put(alphabet.charAt(i), new StatsWrapper());
		}
		for (String line : lines) {
			System.out.println("Length " + line.length() + ": " + line);
			Map<Character, Integer> counts = Ciphers.countMap(line);
			for (int i=0; i<alphabet.length(); i++) {
				char ch = alphabet.charAt(i);
				Integer count = counts.get(ch);
				if (count == null) count = 0;
				stats.get(ch).addValue(count);
			}
		}
		return stats;
	}
	
	/** calculate a sample's euclidian distance from the given average distribution. */
	public static double distanceFromMean(String sample, Map<Character, StatsWrapper> stats) {
		Map<Character, Integer> counts = Ciphers.countMap(sample);
		
		double sum = 0;
		for (Character key : stats.keySet()) {
			Integer count1 = counts.get(key);
			if (count1 == null) count1 = 0;
			double count2 = stats.get(key).stats.getMean();
			double diff = count2 - count1;
			sum = sum + diff*diff;
		}
		sum = Math.sqrt(sum);
		return sum;
	}
	/** calculate a sample's euclidian distance from the expected letter frequencies. */
	public static double distanceFromExpected(String sample, Map<Character, Double> freqs) {
		int len = sample.length();
		Map<Character, Integer> counts = Ciphers.countMap(sample);
		
		double sum = 0;
		for (Character key : freqs.keySet()) {
			Double expected = freqs.get(key);
			if (expected == null) expected = 0d;
			expected *= len;
			
			Integer actual = counts.get(key);
			if (actual == null) actual = 0;
			
			double diff = expected - actual;
			sum = sum + diff*diff;
		}
		sum = Math.sqrt(sum);
		return sum;
	}
	
	/** read the given corpus file and compute unigram stats.  use the W168 rules:
	 * 1) convert everything to uppercase
	 * 2) remove everything except spaces, periods, and A-Z.
	 * 3) don't allow more than one space in a row
	 * 4) no period appears with a space immediately preceding or following it 
	 */
	public static Map<Character, Double> unigramFrequencies(String path) {
		StringBuilder full = new StringBuilder(); 
		List<String> list = FileUtil.loadFrom(path);
		for (String line : list) {
			if (full.length() > 0) 
				full.append(" ");
			line = line.toUpperCase();
			for (int i=0; i<line.length(); i++) {
				char ch = line.charAt(i);
				if (isAlpha(ch) || ch == '.' || ch == ' ') {
					full.append(ch);
				} else full.append(' ');
			}
		}
		full = new StringBuilder(full.toString().replaceAll(" +", " "));
		full = new StringBuilder(full.toString().replaceAll(" \\.", "."));
		full = new StringBuilder(full.toString().replaceAll("\\. ", "."));
		//for (int i=0; i<full.length(); i+=100) 
		//	System.out.println(full.substring(i, i+100));
		int count = 0;
		Map<Character, Double> freqs = new HashMap<Character, Double>();
		for (int i=0; i<full.length(); i++) {
			char ch = full.charAt(i);
			Double val = freqs.get(ch);
			if (val == null) {
				val = 0d;
			}
			val++;
			freqs.put(ch, val);
			count++;
		}
		for (Character key : freqs.keySet()) {
			freqs.put(key, freqs.get(key)/count);
			System.out.println(key + "	" + freqs.get(key));
		}
		return freqs;
	}
	
	public static boolean isAlpha(char ch) {
		return (ch > 64 && ch < 91);
	}
	public static void testStats() {
		Map<Character, StatsWrapper> map = statsFor(W168TestCiphers.plaintexts);
		for (Character key : map.keySet()) {
			StatsWrapper stats = map.get(key);
			System.out.println(key + "	" + stats.stats.getMean()/168); // convert to ratio
		}
		StatsWrapper distanceStats = new StatsWrapper();
		for (String pt : W168TestCiphers.plaintexts) {
			double distance = distanceFromMean(pt, map);
			System.out.println("distance " + distance + ": " + pt);
			distanceStats.addValue(distance);
		}
		for (String pt : W168TestCiphers.plaintexts) {
			distanceStats.actual = distanceFromMean(pt, map);
			System.out.println(distanceStats.sigma() + "	" + pt);
		}

		System.out.println("W168: " + W168.cipherLine);
		distanceStats.actual = distanceFromMean(W168.cipherLine, map);
		System.out.println("Distance: " + distanceStats.actual);
		System.out.println(distanceStats.sigma() + "	" + W168.cipherLine);

		Map<Character, Integer> counts = Ciphers.countMap(W168.cipherLine);
		for (Character key : counts.keySet()) {
			System.out.println(key + "	" + ((double)counts.get(key))/168);
		}
		
	}
	public static void testStats2() {
		StatsWrapper stats = new StatsWrapper(); 
		Map<Character, Double> freqs = unigramFrequencies("/Users/doranchak/projects/ciphers/W168/corpus-for-unigram-stats.txt");
		for (String pt : W168TestCiphers.plaintexts) {
			double distance = distanceFromExpected(pt, freqs);
			stats.addValue(distance);
			System.out.println(distanceFromExpected(pt, freqs));
		}
		System.out.println("W168:");
		double distance = distanceFromExpected(W168.cipherLine, freqs);
		System.out.println(distance);
		stats.actual = distance;
		System.out.println("mean " + stats.stats.getMean() + ", stddev " + stats.stats.getStandardDeviation() + ", sigma " + stats.sigma());
		
	}
	
	public static void main(String[] args) {
//		testStats();
//		unigramFrequencies("/Users/doranchak/projects/ciphers/W168/corpus-for-unigram-stats.txt");
		testStats2();
	}
}
