package com.zodiackillerciphers.tests.ngrams;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.io.FileUtil;
import com.zodiackillerciphers.lucene.Stats;
import com.zodiackillerciphers.ngrams.NGramsBean;
import com.zodiackillerciphers.ngrams.Periods;
import com.zodiackillerciphers.stats.StatsWrapper;
import com.zodiackillerciphers.transform.CipherTransformations;
import com.zodiackillerciphers.transform.Transformation;
import com.zodiackillerciphers.transform.TransformationBase;
import com.zodiackillerciphers.transform.operations.RectangularSelection;

public class Ngrams {
	/**
	 * find largest rectangular region that has no repeating ngrams. example: z340
	 * has an 8x6 region that has zero repeating ngrams.
	 * 
	 * returns: [row, col, row, col, area] specifies upper left corner and lower
	 * right corner
	 * 
	 */
	public static int[] largestRegionWithoutNgrams(String cipher, int width, int n) {
		NGramsBean ng = new NGramsBean(n, cipher);
		List<StringBuffer> list = TransformationBase.toList(cipher, width, false);
		int height = list.size();
		// for (StringBuffer sb : list) {
		// System.out.println("[" + sb + "]");
		// }
		int[] result = null;
		int maxArea = 0;
		for (int row1 = 0; row1 < height; row1++) {
			for (int col1 = 0; col1 < width; col1++) {
				for (int row2 = row1; row2 < height; row2++) {
					for (int col2 = col1; col2 < width; col2++) {
						boolean go = true;
						for (int r = row1; r <= row2; r++) {
							for (int c = col1; c <= col2; c++) {
								int pos = r * width + c;
								if (pos >= cipher.length()) {
									go = false;
									continue;
								}
								if (ng.coverage.contains(pos)) {
									go = false;
									break;
								}
							}
							if (!go)
								break;
						}
						if (go) {
							int w = col2 - col1 + 1;
							int h = row2 - row1 + 1;
							int area = w * h;
							if (area > maxArea) {
								maxArea = area;
								result = new int[] { row1, col1, row2, col2, area };
							}
							// String line = w*h + " " + row1 + " " + col1 + " " + row2 + " " + col2;
							// System.out.println(line);
						}
					}
				}
			}
		}
		return result;
	}

	public static void testLargestByWidths() {
		for (int width = 1; width <= 340; width++) {
			int[] result = largestRegionWithoutNgrams(Ciphers.cipher[0].cipher, width, 2);
			System.out.println(result[4] + "	" + width + "	" + Arrays.toString(result));
		}
	}

	public static void testLargest() {
		int[] result = largestRegionWithoutNgrams(Ciphers.cipher[0].cipher, 17, 2);
		System.out.println(Arrays.toString(result));
	}

	public static void testShuffles() {
		Map<Integer, Integer> counts = new HashMap<Integer, Integer>();
		DescriptiveStatistics stats = new DescriptiveStatistics();
		String cipher = Ciphers.cipher[0].cipher;
		int max = 0;
		for (int i = 0; i < 1000000; i++) {
			cipher = CipherTransformations.shuffle(cipher);
			int[] result = largestRegionWithoutNgrams(cipher, 17, 2);
			int area = result[4];
			if (area > max) {
				System.out.println(area + "	" + Arrays.toString(result) + "	" + cipher);
				max = area;
			}
			Integer val = counts.get(area);
			if (val == null)
				val = 0;
			val++;
			counts.put(area, val);
			stats.addValue(area);
		}
		System.out.println(counts);
		System.out.println("Min " + stats.getMin() + " Max " + stats.getMax() + " Mean " + stats.getMean() + " Median "
				+ stats.getPercentile(50) + " Stddev " + stats.getStandardDeviation());
	}

	/*
	 * what is the empirical prob of repeating fragments?
	 */
	static void testShuffles2() {
		String cipher = Ciphers.cipher[1].cipher;

		int total = 0;
		for (long i = 0; i < 1000000000000l; i++) {
			cipher = CipherTransformations.shuffle(cipher);
			int matches = 0;
			int index = cipher.indexOf("#B");
			while (index > -1) {
				matches++;
				index = cipher.indexOf("#B", index + 2);
			}
			if (matches > 5) {
				total++;
				System.out.println((i + 1) + " " + total + " " + matches + " " + cipher);
			}
		}
	}

	static void testRyanGarlick() {
		String cipher = "OKMTbpBYDd2GTL1|kPDWY.<*Kf)5T4M.+&BFR8;(cBF5|JHz#L)(WGV3pO++RK24b.cV4t+++-5ZUV>EC/k4&+PF5|*dCkF>2D(Op^.fMqG2L)|BWlF+<|Lz.VGXcU+_NYz+@L9POSHT/()pcC-*BOY_BK46AycBF2+Op7<FBy-zZO8A|K;+|Et5/R+UV^lp>REHNp+B(#O%2<clRJ|*N+#yS96zZU+Mc:yBSpp7^l8*lGFN^f5294:*1XBydjtz+M9_p8R^FlO-|c.3zBK(C61L+TcR2;%qK+5#(G2Jfj#O++)WCzWct7<WdkF|RZ+b+M<d-zlUV+^J>MDHNpkS";
		// cipher = Ciphers.cipher[0].cipher;
		NGramsBean ng = new NGramsBean(3, cipher);
		ng.dump();
	}

	/** average position [0-339] of all symbols involved in repeating bigrams */
	static void testAveragePosition(String cipher, int n) {
		NGramsBean ng = new NGramsBean(n, cipher);
		double sum = 0;
		for (Integer pos : ng.coverage) {
			sum += pos;
		}
		sum /= ng.coverage.size();
		System.out.println("Unmodified cipher: " + sum);

		DescriptiveStatistics stats = new DescriptiveStatistics();
		for (int i = 0; i < 1000000; i++) {
			ng = new NGramsBean(n, CipherTransformations.shuffle(cipher));
			sum = 0;
			for (Integer pos : ng.coverage) {
				sum += pos;
			}
			sum /= ng.coverage.size();
			// System.out.println(sum);
			stats.addValue(sum);
		}
		System.out.println("Min " + stats.getMin() + " Max " + stats.getMax() + " Mean " + stats.getMean() + " Median "
				+ stats.getPercentile(50) + " Stddev " + stats.getStandardDeviation());
	}

	static void testAverageRowCol(String cipher, int n, int width) {
		NGramsBean ng = new NGramsBean(n, cipher);
		double sumRow = 0;
		double sumCol = 0;
		for (Integer pos : ng.coverage) {
			sumRow += pos / width;
			sumCol += pos % width;
		}
		sumRow /= ng.coverage.size();
		sumCol /= ng.coverage.size();
		System.out.println("Unmodified cipher, average row: " + sumRow);
		System.out.println("Unmodified cipher, average col: " + sumCol);

		DescriptiveStatistics statsRow = new DescriptiveStatistics();
		DescriptiveStatistics statsCol = new DescriptiveStatistics();
		for (int i = 0; i < 1000000; i++) {
			ng = new NGramsBean(n, CipherTransformations.shuffle(cipher));
			sumRow = 0;
			sumCol = 0;
			for (Integer pos : ng.coverage) {
				sumRow += pos / width;
				sumCol += pos % width;
			}
			sumRow /= ng.coverage.size();
			sumCol /= ng.coverage.size();
			statsRow.addValue(sumRow);
			statsCol.addValue(sumCol);
		}
		System.out.println("Min " + statsRow.getMin() + " Max " + statsRow.getMax() + " Mean " + statsRow.getMean()
				+ " Median " + statsRow.getPercentile(50) + " Stddev " + statsRow.getStandardDeviation());
		System.out.println("Min " + statsCol.getMin() + " Max " + statsCol.getMax() + " Mean " + statsCol.getMean()
				+ " Median " + statsCol.getPercentile(50) + " Stddev " + statsCol.getStandardDeviation());
	}

	/**
	 * bigram affinity/phobia: generate shuffle stats for all bigram counts. look
	 * for bigram counts in the given cipher that are significantly higher/lower
	 * than expected.
	 */
//	public static void bigramAffinityPhobia2(String cipher, int shuffles) {
//		System.out.println(cipher);
//		String alphabet = Ciphers.alphabet(cipher);
//		for (int a = 0; a < alphabet.length(); a++) {
//			for (int b = 0; b < alphabet.length(); b++) {
//				String key = alphabet.charAt(a) + "" + alphabet.charAt(b);
//				ngramAffinityPhobia(cipher, shuffles, key);
//			}
//		}
//	}

	public static int countNGram(String cipher, String ngram) {
		int count = 0;
		int index = cipher.indexOf(ngram);
		while (index > -1) {
			count++;
			index = cipher.indexOf(ngram, index + 1);
		}
		return count;
	}

	public static void bigramAffinityPhobia(String cipher, int shuffles) {
		System.out.println(cipher);
		int n = 2;
		Map<String, Integer> actual = new HashMap<String, Integer>();
		for (int i = 0; i < cipher.length() - n + 1; i++) {
			String ngram = cipher.substring(i, i + n);
			Integer val = actual.get(ngram);
			if (val == null)
				val = 0;
			val++;
			actual.put(ngram, val);
		}

		// map ngram to map of count to number of occurrences of that count
		Map<String, Map<Integer, Integer>> map = new HashMap<String, Map<Integer, Integer>>();
		for (int k = 0; k < shuffles; k++) {
			cipher = CipherTransformations.shuffle(cipher);
			Map<String, Integer> counts = new HashMap<String, Integer>();
			for (int i = 0; i < cipher.length() - n + 1; i++) {
				String ngram = cipher.substring(i, i + n);
				Integer val = counts.get(ngram);
				if (val == null)
					val = 0;
				val++;
				counts.put(ngram, val);
			}
			for (String ngram : counts.keySet()) {
				Map<Integer, Integer> histogram = map.get(ngram);
				if (histogram == null)
					histogram = new HashMap<Integer, Integer>();
				Integer num = histogram.get(counts.get(ngram));
				if (num == null)
					num = 0;
				num++;
				histogram.put(counts.get(ngram), num);
				map.put(ngram, histogram);
			}
		}
		//System.out.println(map);
		String alphabet = Ciphers.alphabet(cipher);
		for (int a = 0; a < alphabet.length(); a++) {
			for (int b = 0; b < alphabet.length(); b++) {
				String key = alphabet.charAt(a) + "" + alphabet.charAt(b);
				StatsWrapper stats = new StatsWrapper();
				int samples = 0;
				stats.name = "Occurrences of " + key;
				Integer val = actual.get(key);
				if (val == null) val = 0;
				stats.actual = val;
				Map<Integer, Integer> histogram = map.get(key);
				if (histogram != null) {
					for (Integer count : histogram.keySet()) {
						Integer num = histogram.get(count);
						// there are num samples with count of count
						samples += num;
						for (int i=0; i<num; i++) stats.addValue(count);
					}
				}
				// remaining samples were zero
				for (int i=0; i<(shuffles-samples); i++) stats.addValue(0);
				stats.output();
			}
		}
	}

	public static void trigramAffinityPhobia(String cipher, int shuffles) {
		System.out.println(cipher);
		int n = 3;
		Map<String, Integer> actual = new HashMap<String, Integer>();
		for (int i = 0; i < cipher.length() - n + 1; i++) {
			String ngram = cipher.substring(i, i + n);
			Integer val = actual.get(ngram);
			if (val == null)
				val = 0;
			val++;
			actual.put(ngram, val);
		}

		// map ngram to map of count to number of occurrences of that count
		Map<String, Map<Integer, Integer>> map = new HashMap<String, Map<Integer, Integer>>();
		for (int k = 0; k < shuffles; k++) {
			cipher = CipherTransformations.shuffle(cipher);
			Map<String, Integer> counts = new HashMap<String, Integer>();
			for (int i = 0; i < cipher.length() - n + 1; i++) {
				String ngram = cipher.substring(i, i + n);
				Integer val = counts.get(ngram);
				if (val == null)
					val = 0;
				val++;
				counts.put(ngram, val);
			}
			for (String ngram : counts.keySet()) {
				Map<Integer, Integer> histogram = map.get(ngram);
				if (histogram == null)
					histogram = new HashMap<Integer, Integer>();
				Integer num = histogram.get(counts.get(ngram));
				if (num == null)
					num = 0;
				num++;
				histogram.put(counts.get(ngram), num);
				map.put(ngram, histogram);
			}
		}
		// System.out.println(map);
		String alphabet = Ciphers.alphabet(cipher);
		for (int a = 0; a < alphabet.length(); a++) {
			for (int b = 0; b < alphabet.length(); b++) {
				for (int c = 0; c < alphabet.length(); c++) {
					String key = alphabet.charAt(a) + "" + alphabet.charAt(b) + "" + alphabet.charAt(c);
					StatsWrapper stats = new StatsWrapper();
					int samples = 0;
					stats.name = "Occurrences of " + key;
					Integer val = actual.get(key);
					if (val == null)
						val = 0;
					stats.actual = val;
					Map<Integer, Integer> histogram = map.get(key);
					if (histogram != null) {
						for (Integer count : histogram.keySet()) {
							Integer num = histogram.get(count);
							// there are num samples with count of count
							samples += num;
							for (int i = 0; i < num; i++)
								stats.addValue(count);
						}
					}
					// remaining samples were zero
					for (int i = 0; i < (shuffles - samples); i++)
						stats.addValue(0);
					stats.output();
				}
			}
		}
	}
	
	/** count number of adjacent unigram repeats (double symbols) */
	public static int doubles(String cipher, boolean output) {
		int total = 0;
		for (int i=0; i<cipher.length()-1; i++) {
			if (cipher.charAt(i) == cipher.charAt(i+1)) {
				total++;
				if (output) {
					String dub = cipher.charAt(i) + "" + cipher.charAt(i+1);
					String line = "Double " + dub + " found at position " + i;
					System.out.println(line);
				}
			}
		}
		return total;
	}
	
	public static void doubleShuffles(String cipher, int shuffles) {
		StatsWrapper stats = new StatsWrapper();
		stats.name = "Doubled unigrams";
		stats.actual = doubles(cipher, false);
		for (int i=0; i<shuffles; i++) {
			cipher = CipherTransformations.shuffle(cipher);
			stats.addValue(doubles(cipher,false));
		}
		stats.output();
	}
//	public static void doubleShufflesPerSymbol(String cipher, int shuffles) {
//		String alphabet = Ciphers.alphabet(cipher);
//		Map<>
//		StatsWrapper stats = new StatsWrapper();
//		stats.name = "Doubled unigrams";
//		stats.actual = doubles(cipher, false);
//		for (int i=0; i<shuffles; i++) {
//			cipher = CipherTransformations.shuffle(cipher);
//			stats.addValue(doubles(cipher,false));
//		}
//		stats.output();
//	}
	
	//	public static void ngramAffinityPhobia2(String cipher, int shuffles, String ngram) {
//		int actual = countNGram(cipher, ngram);
//		StatsWrapper stats = new StatsWrapper();
//		stats.actual = actual;
//		stats.name = "ngram count for " + ngram;
//		for (int i = 0; i < shuffles; i++) {
//			cipher = CipherTransformations.shuffle(cipher);
//			int count = countNGram(cipher, ngram);
//			stats.addValue(count);
//		}
//		stats.output();
//	}

	/** trigram version */
//	public static void trigramAffinityPhobia(String cipher, int shuffles) {
//		System.out.println(cipher);
//		String alphabet = Ciphers.alphabet(cipher);
//		for (int a = 0; a < alphabet.length(); a++) {
//			for (int b = 0; b < alphabet.length(); b++) {
//				for (int c = 0; c < alphabet.length(); c++) {
//					String key = alphabet.charAt(a) + "" + alphabet.charAt(b) + "" + alphabet.charAt(c);
//					ngramAffinityPhobia(cipher, shuffles, key);
//				}
//			}
//		}
//	}
	public static void process(String file) {
		List<String> list = FileUtil.loadFrom(file);
		StatsWrapper stats = new StatsWrapper(); 
		for (String line : list) {
			if (line.startsWith("Occ")) {
				String[] split = line.split("	");
				Double val = Double.valueOf(split[7]);
				if (Double.isNaN(val)) val =0d;
				if (val > 0) stats.addValue(val);
			}
		}
		stats.output();
	}
	
	/** shuffle test of counts of repeating ngrams in all possible rectangular selections 
	 * of the given cipher. */
	public static void rectangularNgramsShuffle(String cipher, int n, int shuffles, int width) {
		List<StringBuffer> cipherList = TransformationBase.toList(
				cipher, width);
		
		//Map<String, StatsWrapper> map = new HashMap<String, StatsWrapper>(); 
		int height = cipher.length()/width;
		int count = 0;
		for (int row=0; row<height; row++) {
			for (int col=0; col<width; col++) {
				for (int h=1; h+row<=height; h++) {
					for (int w=1; w+col<=width; w++) {
						count++;
						if (count % 1000 == 0) System.out.println(count+"...........");
						Transformation d = new RectangularSelection(cipherList, row, col, h, w);
						d.execute();
						String select = TransformationBase.fromList(d.getOutput()).toString();
						if (select.length() < 4) continue;
						String key = row+","+col+","+h+","+w;
//						System.out.println(key+":" + select);
						StatsWrapper stats = new StatsWrapper();
						NGramsBean ng = new NGramsBean(n, select);
						stats.actual = ng.numRepeats();
						stats.name = key;
						
						for (int i=0; i<shuffles; i++) {
							String cipher2 = CipherTransformations.shuffle(cipher);
							List<StringBuffer> cipher2List = TransformationBase.toList(
									cipher2, width);
							Transformation d2 = new RectangularSelection(cipher2List, row, col, h, w);
							d2.execute();
							String select2 = TransformationBase.fromList(d2.getOutput()).toString();
							NGramsBean ng2 = new NGramsBean(n, select2);
							stats.addValue(ng2.numRepeats());
						}
						stats.output();
					}
				}
			}
		}
		
	}
	
	/** return repeating ngram count of top (bottom) half */
	public static int topBottomHalfRepeatingNgrams(String cipher, int n, boolean top) {
		int half = cipher.length()/2;
		String split = null;
		if (top) split = cipher.substring(0, half);
		else split = cipher.substring(half);
		NGramsBean ng = new NGramsBean(n, split);
		return ng.numRepeats();
	}
	
	/** count repeating bigrams in top and bottom half.  measure difference.
	 * compare to shuffles
	 */
	public static void topHalfBottomHalfShuffle(String cipher, int shuffles, int n) {
		System.out.println(cipher);
		StatsWrapper statsTopCount = new StatsWrapper();
		statsTopCount.name = "repeating " + n + "-grams in top half";
		statsTopCount.actual = topBottomHalfRepeatingNgrams(cipher, n, true);
		StatsWrapper statsBottomCount = new StatsWrapper();
		statsBottomCount.name = "repeating " + n + "-grams in bottom half";
		statsBottomCount.actual = topBottomHalfRepeatingNgrams(cipher, n, false);
		StatsWrapper statsDifference = new StatsWrapper();
		statsDifference.name = "difference";
		statsDifference.actual = Math.abs(statsBottomCount.actual - statsTopCount.actual);
		
		for (int i=0; i<shuffles; i++) {
			String shuf = CipherTransformations.shuffle(cipher);
			int val1 = topBottomHalfRepeatingNgrams(shuf, n, true);
			int val2 = topBottomHalfRepeatingNgrams(shuf, n, false);
			int diff = Math.abs(val1-val2);
			statsTopCount.addValue(val1);
			statsBottomCount.addValue(val2);
			statsDifference.addValue(diff);
		}
		
		statsTopCount.output();
		statsBottomCount.output();
		statsDifference.output();
	}
	
	/** test given cipher for arbitrary number of repeating ngrams */
	public static void shuffleNgrams(String cipher, int n, int shuffles, int repeats) {
		StatsWrapper stats = new StatsWrapper();
		stats.name = ""+repeats;
		stats.actual = repeats;
		// track the distribution of repeats
		Map<Integer, Integer> counts = new HashMap<Integer, Integer>(); 
		for (int i=0; i<shuffles; i++) {
			if (i%10000 == 0) System.out.println(i+"...");
			cipher = CipherTransformations.shuffle(cipher);
			NGramsBean ng = new NGramsBean(n, cipher);
			int reps = ng.numRepeats();
			stats.addValue(reps);
			Integer val = counts.get(reps);
			if (val == null) val = 0;
			val++;
			counts.put(reps, val);
		}
		stats.output();
		System.out.println(counts);
	}
	
	/** test given cipher for ngram ioc */
	public static void shuffleNgramIoc(String cipher, int n, int shuffles) {
		StatsWrapper stats = new StatsWrapper();
		double ioc = Stats.iocNgram(cipher, n);
		stats.name = n + "gram ioc";
		stats.actual = ioc;
		for (int i=0; i<shuffles; i++) {
			if (i%10000 == 0) System.out.println(i+"...");
			cipher = CipherTransformations.shuffle(cipher);
			ioc = Stats.iocNgram(cipher, n);
			stats.addValue(ioc);
			
		}
		System.out.println(stats.header());
		stats.output();
	}
	/** test given cipher for ngram ioc, max across all periods */
	public static void shuffleNgramIoc2(String cipher, int n, int shuffles) {
		StatsWrapper stats = new StatsWrapper();
		double ioc = Stats.iocNgram(cipher, n);
		stats.name = n + "gram ioc, all periods";
		stats.actual = ioc;
		for (int i=0; i<shuffles; i++) {
			if (i%10000 == 0) System.out.println(i+"...");
			cipher = CipherTransformations.shuffle(cipher);
			
			ioc = 0;
			// look for max ioc across all periods
			for (int p=1; p<=cipher.length(); p++) {
				String re = Periods.rewrite3(cipher, p);
				ioc = Math.max(ioc, Stats.iocNgram(re, n));
			}
			stats.addValue(ioc);
			
		}
		System.out.println(StatsWrapper.header());
		stats.output();
	}
	
	/** test given cipher for arbitrary number of repeating ngrams, taking the max across all periods */
	public static void shuffleNgrams2(String cipher, int n, int shuffles, int repeats) {
		StatsWrapper stats = new StatsWrapper();
		stats.name = ""+repeats;
		stats.actual = repeats;
		// track the distribution of repeats
		Map<Integer, Integer> counts = new HashMap<Integer, Integer>(); 
		for (int i=0; i<shuffles; i++) {
			if (i%10000 == 0) System.out.println(i+"...");
			cipher = CipherTransformations.shuffle(cipher);
			
			int max = 0;
			for (int p=1; p<=cipher.length()/2; p++) {
				String re = Periods.rewrite3(cipher, p);
				NGramsBean ng = new NGramsBean(n, re);
				max = Math.max(max, ng.numRepeats());
			}
			stats.addValue(max);
			Integer val = counts.get(max);
			if (val == null) val = 0;
			val++;
			counts.put(max, val);
			
		}
		stats.output();
		System.out.println(counts);
	}

	/** return the max number of occurrences in the same column */
	public static int sameColumnCount(List<Integer> positions, int width) {
		Map<Integer, Integer> counts = new HashMap<Integer, Integer>();
		int result = 0;
		for (Integer pos : positions) {
			int key = pos % width;
			Integer val = counts.get(key);
			if (val == null) val = 0;
			val++;
			result = Math.max(result, val);
			counts.put(key,  val);
		}
		return result;
	}
	
	public static double prob(String ngram, int sameColCount, Map<Character, Integer> symbolCounts, int length) {
		double prob = 1;
		for (int i=0; i<ngram.length(); i++) {
			int val = symbolCounts.get(ngram.charAt(i));
			prob *= ((double)val)/length;
		}
		return Math.pow(prob,  sameColCount);
	}
	
	/** find all ngrams that have repeats in the same column.
	 * keep the repeating ngram whose probability (p) is lowest.
	 * return 1/p
	 */
	public static double sameColumnRepeatProbability(String cipher, int width, Map<Character, Integer> symbolCounts) {
		int n = 2;
		boolean go = true;
		double prob = 0;
		while (go) {
			go = false;
			NGramsBean ng = new NGramsBean(n, cipher);
			for (String ngram : ng.repeats) {
				int sameColCount = sameColumnCount(ng.positions.get(ngram), width);
				if (sameColCount > 1) {
					go = true;
					double prob2 = prob(ngram, sameColCount, symbolCounts, cipher.length());
//					System.out.println("ngram " + ngram + " count " + sameColCount + " prob " + prob2);
					if (prob == 0) prob = prob2;
					else prob = Math.min(prob,  prob2);
				}
			}
			n++;
		}
//		System.out.println("prob result: " + prob);
		if (prob == 0) return prob;
		return ((double)1)/prob;
	}
	
	public static void testSameCol(String cipher) {
		Map<Character, Integer> counts = Ciphers.countMap(cipher);
		System.out.println("result: " + sameColumnRepeatProbability(cipher, 17, counts));
		
	}
	
	public static void shuffleSameCol(String cipher, int width, int shuffles) {
		System.out.println(cipher);
		StatsWrapper stats = new StatsWrapper();
		Map<Character, Integer> counts = Ciphers.countMap(cipher);
		stats.actual = sameColumnRepeatProbability(cipher, 17, counts);
		for (int i=0; i<shuffles; i++) {
			cipher = CipherTransformations.shuffle(cipher);
			double prob = sameColumnRepeatProbability(cipher, 17, counts);
//			System.out.println("shuffle prob: " + prob);
			stats.addValue(prob);
		}
		stats.output();
	}
	
	/** http://ciphermysteries.com/2018/11/18/usage-patterns-in-the-beale-ciphers */
	public static void testPellingJarlveBeale() {
		String sequence = ".*.*..*.**.*.**..***..*.*..*.***..*.*.*..*.*.**.*.**.*.*.*.*..*..**.***..*.**.****.***.**.*.*.**.****.*.*.*...*...**.*.*.**.**..*.**..*.***.*.***..*..*.***.**.**..*****...*.*.*..****..*....***..*.****.*****..***...*.*.****.*******..**.*.*.*..**..**.****.**...**..*..**.*.*.***.**.**.****.****.******.***..****.**...*.*.**..*.***.*******..*...**.******.**..*.*.**.*.***.*.**..*.*.**..*****..***.**..*.***.**.****.****.*.***.*...**...**.*......*.*..**.**..*.*****.****.*****.*.*.*.*.**.*.*....*.*.*********.*********..**.**.*******..**.*..******.*.****..*.**.****.*****.....*.**..*******.****..*.*.***..*********.******";
		StatsWrapper stats = new StatsWrapper();
		stats.actual = 1;
		while (true) {
			String shuffled = CipherTransformations.shuffle(sequence);
			// look for non-overlapping
			int hits = 0;
			for (int i=0; i<shuffled.length()-36+1; i++) {
				String sub1 = shuffled.substring(i, i+18);
				for (int j=i+18; j<shuffled.length()-18+1; j++) {
					String sub2 = shuffled.substring(j, j+18);
					if (sub1.equals(sub2)) {
						hits++;
						System.out.println(i + "," + j + ": " + sub1 + "   "  + shuffled);
					}
				}
			}
			stats.addValue(hits);
			if (hits > 0) 
				stats.output();
			break;
		}
	}
	
	public static void main(String[] args) {
//		topHalfBottomHalfShuffle(Ciphers.Z340, 1000000, 3);
		//shuffleNgrams(Ciphers.Z340, 2, 1000000, 37);
//		shuffleNgrams2(Ciphers.Z340, 2, 1000000, 37);
		for (int p=1; p<171; p++) {
			System.out.println(p + " " + Stats.iocNgram(Periods.rewrite3(Ciphers.Z340, p), 2));
		}
		shuffleNgramIoc2(Periods.rewrite3(Ciphers.Z340, 19), 2, 1000000);
//		testSameCol(CipherTransformations.shuffle(Ciphers.Z340));
//		testSameCol(Ciphers.Z340);
//		shuffleSameCol(Ciphers.Z408, 17, 1000000);
//		rectangularNgramsShuffle(Ciphers.Z408, 3, 10000, 17);
//		process("/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/tests/bigram-trigram-affinity-phobia/z408-n3.txt");
//		process("/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/tests/bigram-trigram-affinity-phobia/z340-n3.txt");
//		System.out.println(doubles(CipherTransformations.shuffle(Ciphers.Z408), true));
//		doubleShuffles(Ciphers.Z340, 10000000);
		// testLargest();
		// testLargestByWidths();
		// testShuffles();j
		// testShuffles2();
		// testRyanGarlick();
		// testAveragePosition(Ciphers.cipher[1].cipher, 2);
		// testAverageRowCol(Ciphers.cipher[1].cipher, 2, 17);
		// NGramsBean ng = new NGramsBean(2,
		// "9%P/Z/UB%kOR=pX=BWV+eGYF69HP@K!qYeMJY^UIk7qTtNQYD5)S(/9#BPORAU%fRlqEk^LMZJdr\\pFHVWe8Y@+qGD9KI)6qX85zS(RNtIYElO8qGBTQS#BLd/P#B@XqEHMU^RRkcZKqpI)Wq!85LMr9#BPDR+j=6\\N(eEUHkFZcpOVWI5+tL)l^R6HI9DR_TYr\\de/@XJQAP5M8RUt%L)NVEKH=GrI!Jk598LMlNA)Z(PzUpkA9#BVW\\+VTtOP^=SrlfUe67DzG%%IMNk)ScE/9%%ZfAP#BVpeXqWq_F#8c+@9A9B%OT5RUc+_dYq_^SqWVZeGYKE_TYA9%#Lt_H!FBX9zXADd\\7L!=q_ed##6e5PORXQF%GcZ@JTtq_8JI+rBPQW6");
		// System.out.println(ng.coverageRatio());
		//trigramAffinityPhobia(Ciphers.Z340, 10000);
		 //bigramAffinityPhobia(Ciphers.Z408, 1000000);
//		 trigramAffinityPhobia(Ciphers.Z340, 100000);
//		// System.out.println(countNGram(Ciphers.Z408, "#B"));
//		testPellingJarlveBeale();
	}
}
