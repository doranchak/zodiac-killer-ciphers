package com.zodiackillerciphers.homophones;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.io.FileUtil;
import com.zodiackillerciphers.ngrams.NGramsBean;
import com.zodiackillerciphers.ngrams.Periods;
import com.zodiackillerciphers.pivots.Pivot;
import com.zodiackillerciphers.pivots.PivotPair;
import com.zodiackillerciphers.pivots.PivotUtils;
import com.zodiackillerciphers.transform.CipherTransformations;

public class SymbolMergeTest {

	/** if true, normal period untransposition via rewrite3.  otherwise, transpose via rewrite3undo. */
	public static boolean untranspose = false; 
	/** reference counts of ngram repeats by period for Z340 */
	public static Map<Integer, Map<Integer, Integer>> periodRepeats;
	static {
		periodRepeats = new HashMap<Integer, Map<Integer, Integer>>();
		for (int n=2; n<6; n++) {
			periodRepeats.put(n,  new HashMap<Integer, Integer>());
			for (int p=1; p<=170; p++) {
				String re = untranspose ? Periods.rewrite3(Ciphers.cipher[0].cipher, p) : Periods.rewrite3undo(Ciphers.cipher[0].cipher, p);
				NGramsBean ng = new NGramsBean(n, re);
				periodRepeats.get(n).put(p, ng.numRepeats());
			}
		}
	}
	
	/** merge all combinations of 2 symbols, then perform measurements to see the effect. */
	public static void mergeTestOLD() {
		String cipher = Ciphers.cipher[0].cipher;
		String alphabet = Ciphers.alphabet(cipher);
		int num = 0;
		
		for (int i=0; i<alphabet.length()-1; i++) {
			for (int j=i+1; j<alphabet.length(); j++) {
				StringBuffer sb = new StringBuffer(cipher); 
				char c1 = alphabet.charAt(i);
				char c2 = alphabet.charAt(j);
				for (int k=0; k<sb.length(); k++) {
					if (sb.charAt(k) == c2) sb.setCharAt(k, c1);
				}
				NGramsBean ng = new NGramsBean(4, sb.toString()); 
				System.out.println(num++ + "	'" + c1 + "" + c2 + "	" + ng.numRepeats());
			}
		}
	}
	
	static void mergeTest(String cipher) {
		for (int n=2; n<6; n++) mergeTest(cipher, n);
	}
	/** run a test on every combination of merges of n symbols */
	static void mergeTest(String cipher, int n) {
		String alphabet = Ciphers.alphabet(cipher);
		int[] indices = new int[n];
		//for (int i=0; i<n; i++) indices[i] = -1;
		recurse(cipher, alphabet, indices);
	}
	static void recurse(String cipher, String alphabet, int[] indices) {
		recurse(cipher, alphabet, indices, 0);
	}
	
	static void recurse(String cipher, String alphabet, int[] indices, int index) {
		if (index >= indices.length) {
			performTask4(cipher, alphabet, indices);
			return;
		}
		int start = 0;
		if (index > 0) start = indices[index-1]+1;
		for (int i=start; i<alphabet.length(); i++) {
			indices[index] = i;
			recurse(cipher, alphabet, indices, index+1);
		}
	}
	
	static void performTask1(String cipher, String alphabet, int[] indices) {
		Set<Character> set = new HashSet<Character>();
		String toMerge = ""; 
		for (int ind : indices)
			toMerge += alphabet.charAt(ind);
		
		String merged = mergeSymbols(cipher, toMerge);
		Map<Character, Integer> countMap = Ciphers.countMap(merged);
		int denom = countMap.get(toMerge.charAt(0));
		
		String line = toMerge.length() + " " + toMerge + " ";
		
		List<Pivot> pivots = PivotUtils.findPivots(merged, 4);
		List<PivotPair> pivotPairs = PivotUtils.pairsFrom(pivots);
		int p1 = 0;
		int p2 = 0;
		if (pivots != null) p1 = pivots.size(); 
		if (pivotPairs != null) p2 = pivotPairs.size(); 
		
		line += p1 + " " + p2 + " ";
		
		int[] nums = new int[4];
		for (int n=2; n<6; n++) {
			NGramsBean ng = new NGramsBean(n, merged);
			int num = ng.numRepeats();
			line += num + " ";
			nums[n-2] = num+1;
			
			// normalized repeat count based on symbol frequencies
			double norm = 0;
			for (String ngram : ng.repeats) {
				double reps = ng.counts.get(ngram)-1;
				int freq = 0;
				for (int i=0; i<ngram.length(); i++) freq += ng.symbolCounts.get(ngram.charAt(i));
				reps /= freq;
				norm += reps;
			}
			line += norm + " ";
		}
		double score = nums[0]*Math.pow(nums[1],2)*Math.pow(nums[2],2)*Math.pow(nums[3],3)/denom;
		line += score; // + " " + Ciphers.isHomophone(toMerge);
		System.out.println(line);
		
	}
	/** pivot tests */
	static void performTask2(String cipher, String alphabet, int[] indices) {
		String toMerge = ""; 
		for (int ind : indices)
			toMerge += alphabet.charAt(ind);
		
		String merged = mergeSymbols(cipher, toMerge);
		System.out.println(toMerge);
		String line = toMerge.length() + " " + toMerge + " ";
		
		List<Pivot> pivots = PivotUtils.findPivots(merged, 4);
		List<PivotPair> pivotPairs = PivotUtils.pairsFrom(pivots);
		int p1 = 0;
		int p2 = 0;
		if (pivots != null) p1 = pivots.size(); 
		if (pivotPairs != null) p2 = pivotPairs.size();
		if (p1 > 0 || p2 > 0) {
			line += p1 + " " + p2 + " " + merged;
			System.out.println("FOUND " + line);
		}
	}
	/** repeating periodic bigrams */
	static void performTask3(String cipher, String alphabet, int[] indices) {
		String toMerge = ""; 
		for (int ind : indices)
			toMerge += alphabet.charAt(ind);
		
		String merged = mergeSymbols(cipher, toMerge);
		//System.out.println(toMerge);
		
		for (int n=2; n<6; n++) {
			int sum = 0;
			String line = toMerge.length() + " " + n + " " + toMerge + " ";
			for (int p=1; p<=170; p++) {
				String re = Periods.rewrite3(merged, p);
				NGramsBean ng = new NGramsBean(n, re);
				int val = ng.numRepeats()-periodRepeats.get(n).get(p);
				line += "" + val + " ";
				sum += val;
			}
			line += sum;
			System.out.println(line);
		}
	}
	
	/** repeating periodic bigrams with comparisons to shuffles */
	static void performTask4(String cipher, String alphabet, int[] indices) {
		Date start = new Date();
		
		// merge symbols
		String toMerge = ""; 
		for (int ind : indices)
			toMerge += alphabet.charAt(ind);
		
		int shuffles = 100;
		// perform shuffles
		Map<Integer, DescriptiveStatistics> mapStats = new HashMap<Integer, DescriptiveStatistics>(); // map ngram length to spike stats  
		for (int i = 0; i < shuffles; i++) {
			String shuffled = CipherTransformations.shuffle(cipher);
			// make reference bigram counts
			Map<Integer, Map<Integer, Integer>> periodRepeats;
			periodRepeats = new ConcurrentHashMap<Integer, Map<Integer, Integer>>();
			List<SymbolMergeTestThread> threads = new ArrayList<SymbolMergeTestThread>();
			for (int n = 2; n < 6; n++) {
				SymbolMergeTestThread thread = new SymbolMergeTestThread(periodRepeats, n, shuffled);
				threads.add(thread);
				thread.start();
			}
			try {
				for (SymbolMergeTestThread thread : threads) thread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			String merged = mergeSymbols(shuffled, toMerge);
			for (int n=2; n<6; n++) {
				int sum = 0;
				int max = -1;
				for (int p=1; p<=170; p++) {
					String re = untranspose ? Periods.rewrite3(merged, p) : Periods.rewrite3undo(merged, p);
					NGramsBean ng = new NGramsBean(n, re);
					int val = ng.numRepeats()-periodRepeats.get(n).get(p);
					max = Math.max(max, val); // track the maximum increase in bigrams seen across all periods
					sum += val;
				}
				// update stats for the max increase found
				DescriptiveStatistics stat = mapStats.get(n);
				if (stat == null) stat = new DescriptiveStatistics();
				stat.addValue(max);
				mapStats.put(n, stat);
			}
		}		

		String merged = mergeSymbols(cipher, toMerge);
		//System.out.println(toMerge);
		
		for (int n=2; n<6; n++) {
			int sum = 0;
			String line = toMerge.length() + " " + n + " " + toMerge + " ";
			int max = -1;
			int maxPeriod = 0;
			for (int p=1; p<=170; p++) {
				String re = untranspose ? Periods.rewrite3(merged, p) : Periods.rewrite3undo(merged, p);
				NGramsBean ng = new NGramsBean(n, re);
				int val = ng.numRepeats()-periodRepeats.get(n).get(p);
				if (val > max) {
					max = val;
					maxPeriod = p;
				}
				line += "" + val + " ";
				sum += val;
			}
			line += sum + " ";
			line += max + " ";
			line += maxPeriod + " ";
			line += mapStats.get(n).getMin() + " ";
			line += mapStats.get(n).getMax() + " ";
			double mean = mapStats.get(n).getMean();
			line += mean + " ";
			double stddev = mapStats.get(n).getStandardDeviation();
			double sigma = 0;
			if (stddev > 0) sigma = (max-mean)/stddev;
			line += sigma;
			System.out.println(line);
		}
		Date end = new Date();
		long diff = end.getTime()-start.getTime();
		System.out.println("elapsed: " + diff);
	}
	
	/** find largest repeating ngrams, for any period, that form when symbols are merged */
	static void performTask5(String cipher, String alphabet, int[] indices) {
		String toMerge = ""; 
		for (int ind : indices)
			toMerge += alphabet.charAt(ind);
		
		String merged = mergeSymbols(cipher, toMerge);
		
		int n=2;
		String line = toMerge.length() + " " + n + " " + toMerge + " ";
		int maxp = 0;
		while (true) {
			boolean found = false;
			for (int p=1; p<=170; p++) {
				String re = untranspose ? Periods.rewrite3(merged, p): Periods.rewrite3undo(merged, p);
				NGramsBean ng = new NGramsBean(n, re);
				int g = 0;
				if (periodRepeats.get(n) != null) g = periodRepeats.get(n).get(p);
				int val = ng.numRepeats()-g;
//				if (val > 0 && n == 6) {
//					System.out.println(n + " " + p + " " + line);
//					System.exit(-1);
//				}
				if (val > 0) {
					found = true;
					maxp = p;
					break;
				}
			}
			if (!found) break;
			n++;
		}
		n--;
		if (n>5) System.out.println(line + " " + n + " " + maxp);
	}
	
	public static String mergeSymbols(String cipher, String toMerge) {
		Set<Character> set = new HashSet<Character>();
		for (int i=1; i<toMerge.length(); i++) set.add(toMerge.charAt(i));
		String result = "";
		for (int i=0; i<cipher.length(); i++) {
			char c = cipher.charAt(i);
			if (set.contains(c)) result += toMerge.charAt(0);
			else result += c;
		}
		return result;
	}
	
	static void mergePivotTest() {
		String[] toMerge = new String[] { "2MUV"};
		
		
		for (String merge : toMerge) {
			String merged = mergeSymbols(Ciphers.cipher[0].cipher, merge);
			List<Pivot> pivots = PivotUtils.findPivots(merged, 4);
			System.out.println("=== " + merge + " === " + merged);
			for (Pivot pivot : pivots) System.out.println(pivot);
		}
	}
	
	static void mergePivotTest2() {
		List<String> lines = FileUtil.loadFrom("/Users/doranchak/projects/zodiac/mergetest2.txt");
		Map<Character, Integer> counts = Ciphers.countMap(Ciphers.cipher[0].cipher);
		for (String line : lines) {
			if (line.contains("FOUND")) {
				String out = "";
				String[] split = line.split(" ");
				String merge = split[2];
				String merged = mergeSymbols(Ciphers.cipher[0].cipher, merge);
				List<Pivot> pivots = PivotUtils.findPivots(merged, 4);
				
				out += merge.length() + "	";
				out += "'" + merge + "	" + split[3] + "	" + split[4] + "	";

				int sum = 0;
				for (int i=0; i<merge.length(); i++) sum += counts.get(merge.charAt(i));
				
				int div13 = 0;
				int div39 = 0;
				for (Pivot pivot : pivots) {
					int pos = pivot.positions.get(0) + 1;
					if (pos % 13 == 0) div13++;
					if (pos % 39 == 0) div39++;
				}
				out += sum + "	" + div13 + "	" + div39 + "	";
				float score = ((float)div13*div39)/sum;
				out += score;
				System.out.println(out);
			}
		}
	}
	static void mergePivotTest3() {
		List<String> lines = FileUtil.loadFrom("/Users/doranchak/projects/zodiac/mergetest2.txt");
		Map<Character, Integer> counts = Ciphers.countMap(Ciphers.cipher[0].cipher);
		int[][] heatmap = new int[20][17];
		for (int i=0; i<20; i++) heatmap[i] = new int[17];
		
		Set<Integer> ignore = new HashSet<Integer>();
		ignore.add(173);
		ignore.add(194);
		ignore.add(233);
		
		ignore.add(81);
		ignore.add(73);
		ignore.add(218);
		ignore.add(72);
		ignore.add(55);
		ignore.add(292);
		ignore.add(217);
		ignore.add(250);
		ignore.add(101);
		ignore.add(245);
		ignore.add(231);
		ignore.add(229);
		ignore.add(148);
		ignore.add(334);

		
		for (String line : lines) {
			if (line.contains("FOUND")) {
				String[] split = line.split(" ");
				String merge = split[2];
				if (merge.length() != 5) continue;
				String merged = mergeSymbols(Ciphers.cipher[0].cipher, merge);
				List<Pivot> pivots = PivotUtils.findPivots(merged, 4);
				
				for (Pivot pivot : pivots) {
					String str = pivot.toString();
					if (!str.contains("directions: N W") && !str.contains("directions: W N")) continue;
					int pos = pivot.positions.get(0);
					if (ignore.contains(pos)) continue;
					int row = pos/17;
					int col = pos%17;
					System.out.println(pos + " " + row + " " + col + " " + merge.length() + " " + merge + " " + pivot);
					heatmap[row][col]++;
					
					//if (row == 11 && col == 8) System.out.println(pivot);
				}
			}
		}
		for (int row=0; row<20; row++) {
			String line = "[";
			for (int col=0; col<17; col++) {
				if (col > 0) line += ", ";
				double val = 0;
				if (heatmap[row][col] > 0)
					val = Math.log(heatmap[row][col]);
				line += val;
				//System.out.println("data[" + col + "][" + row + "]=" + Math.log(heatmap[row][col]) + ";");
			}
			line += "], ";
			System.out.println(line);
		}
		System.out.println("Normal:");
		for (int row=0; row<20; row++) {
			String line = "[";
			for (int col=0; col<17; col++) {
				if (col > 0) line += ", ";
				line += heatmap[row][col];
				//System.out.println("data[" + col + "][" + row + "]=" + Math.log(heatmap[row][col]) + ";");
			}
			line += "], ";
			System.out.println(line);
		}
	}
	static void mergeBigramTest() {
		List<String> lines = FileUtil.loadFrom("/Users/doranchak/projects/zodiac/mergetest4-merge2.txt");
		Map<Character, Integer> counts = Ciphers.countMap(Ciphers.cipher[0].cipher);
		for (String line : lines) {
				String out = "";
				String[] split = line.split(" ");
				//out += split[0] + "	" + split[1] + "	=('" + split[2] + "')	";
				for (int i=0; i<split.length; i++) {
					out += split[i] + "	";
				}
				String merge = split[2];
				
				int sum = 0;
				for (int i=0; i<merge.length(); i++) sum += counts.get(merge.charAt(i));
				out += sum;
				System.out.println(out);
		}
	}
	static void processForPlotting() {
		List<String> lines = FileUtil.loadFrom("/Users/doranchak/projects/zodiac/mergetest9-untranspose-is-false.txt");
		for (String line : lines) {
			if (!line.startsWith("2")) continue;
				String out;
				String[] split = line.split(" ");
				int s = Integer.valueOf(split[0]);
				int n = Integer.valueOf(split[1]);
				
				out = "all[" + n + "].push({" + "s:" + s + ","; 
				String m = split[2];
				out += "m:\"" + m + "\",";
				out += "y:[";
				
				int maxperiod = Integer.MIN_VALUE;
				int maxval = Integer.MIN_VALUE;
				int sum = 0;
				DescriptiveStatistics d = new DescriptiveStatistics(); 
				for (int i=3; i<3+170; i++) {
					if (i>3) out += ",";
					out += split[i];
					int val = Integer.valueOf(split[i]);
					sum += val;
					if (val > maxval) {
						//System.out.println("val " + val + " bigger than maxval " + maxval + " at period " + (i-2));
						maxval = val;
						maxperiod = i-2;
					}
					d.addValue(val);
				}
				
				double mean = d.getMean();
				double stddev = d.getStandardDeviation();
				double g = 0;
				if (stddev > 0) g = ((double)maxval-mean)/stddev;
				double f = Double.valueOf(split[179]);
				
				out += "],p:" + maxperiod + ",x:" + maxval+",g:" + g + ",u:" + sum + ",f:" + f +"});"; 
				System.out.println(out);
		}
	}
	
	public static void mergeBigramTest2() {
		String merged = mergeSymbols(Ciphers.cipher[0].cipher, "%4");
		System.out.println(merged);
		String re = Periods.rewrite3(merged, 19);
		System.out.println(re);
		NGramsBean ng = new NGramsBean(2, re);
		System.out.println(ng.numRepeats());
		ng = new NGramsBean(3, re);
		System.out.println(ng.numRepeats());
		ng = new NGramsBean(4, re);
		System.out.println(ng.numRepeats());
		ng = new NGramsBean(5, re);
		System.out.println(ng.numRepeats());
		
	}
	public static void mergeBigramTest3() {
		String merged = mergeSymbols(Ciphers.cipher[0].cipher, "Mb");
		System.out.println(merged);
		String re = Periods.rewrite3(merged, 160);
		System.out.println(re);
		NGramsBean ng = new NGramsBean(4, re);
		System.out.println(ng.numRepeats());
		ng.dump();
		
	}
	
	
	public static void main(String[] args) {
//		mergeTest(Ciphers.cipher[0].cipher);
		//mergeBigramTest3();
		processForPlotting();
//		for (int i=1; i<171; i++) {
//			System.out.println("<option value=\"" + (i+4) + "\">Period " + i + "</option>");
//		}
		//mergePivotTest3();
		//System.out.println(mergeSymbols(Ciphers.cipher[0].cipher, "%4"));
		
	}
}
