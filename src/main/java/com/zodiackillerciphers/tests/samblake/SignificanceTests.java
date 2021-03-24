package com.zodiackillerciphers.tests.samblake;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.io.FileUtil;

public class SignificanceTests {

	/** from a set of azdecrypt results, see if the top (bottom) scoring results tend to feature (not feature)
	 * certain transposition operations. 
	 */
	public static void significanceTest(String path, float p, int which) {
		// operations:
		// 1) {tr, normal}
		// 2) {decimate[02,19], del[01,20], per[01,09], reshape[08, 40]}
		// 3) {tr, normal}
		// 4} {acsi, acso, csi, cso, decimate[N][M], h*, l*, r*}
		//
		// 2d decimations are: decimate01_01, decimate01_02, decimate01_03,
		// decimate01_04, decimate01_05, decimate01_06, decimate01_07, decimate01_08,
		// decimate01_09, decimate01_10, decimate01_11, decimate01_12, decimate01_13,
		// decimate01_14, decimate01_15, decimate01_16, decimate01_19, decimate01_21,
		// decimate01_25, decimate01_26, decimate01_28, decimate01_29, decimate02_01,
		// decimate02_02, decimate02_03, decimate02_04, decimate02_05, decimate02_07,
		// decimate02_11, decimate02_13, decimate02_14, decimate02_17, decimate02_19,
		// decimate02_25, decimate02_27, decimate02_28, decimate03_01, decimate03_02,
		// decimate03_03, decimate03_04, decimate03_05, decimate03_06, decimate03_07,
		// decimate03_08, decimate03_09, decimate03_10, decimate03_11, decimate03_12,
		// decimate03_13, decimate03_14, decimate03_15, decimate03_16, decimate03_17,
		// decimate03_18, decimate03_20, decimate03_25, decimate03_29, decimate03_31,
		// decimate04_01, decimate04_02, decimate04_05, decimate04_06, decimate04_07,
		// decimate04_08, decimate04_09, decimate04_13, decimate04_14, decimate04_17,
		// decimate04_19, decimate04_29, decimate04_31, decimate05_01, decimate05_02,
		// decimate05_05, decimate05_06, decimate05_07, decimate05_08, decimate05_10,
		// decimate05_11, decimate05_12, decimate05_13, decimate05_14, decimate05_15,
		// decimate05_17, decimate05_18, decimate05_24, decimate05_28, decimate05_31,
		// decimate06_01, decimate06_03, decimate06_04, decimate06_05, decimate06_07,
		// decimate06_08, decimate06_09, decimate06_11, decimate06_13, decimate06_15,
		// decimate06_16, decimate06_17, decimate07_01, decimate07_02, decimate07_03,
		// decimate07_04, decimate07_05, decimate07_06, decimate07_07, decimate07_08,
		// decimate07_09, decimate07_10, decimate07_11, decimate07_12, decimate07_13,
		// decimate07_14, decimate07_15, decimate07_16, decimate07_17, decimate07_20,
		// decimate07_32, decimate08_01, decimate08_03, decimate08_04, decimate08_05,
		// decimate08_06, decimate08_07, decimate08_09, decimate08_11, decimate08_12,
		// decimate08_13, decimate08_14, decimate08_16, decimate08_17, decimate08_18,
		// decimate08_23, decimate08_29, decimate08_35, decimate08_36, decimate09_01,
		// decimate09_03, decimate09_04, decimate09_05, decimate09_07, decimate09_08,
		// decimate09_09, decimate09_10, decimate09_11, decimate09_12, decimate09_13,
		// decimate09_14, decimate09_15, decimate09_16, decimate09_17, decimate09_19,
		// decimate09_25, decimate09_28, decimate10_01, decimate10_02, decimate10_04,
		// decimate10_05, decimate10_08, decimate10_09, decimate10_10, decimate10_11,
		// decimate10_13, decimate10_14, decimate10_15, decimate10_17, decimate10_19,
		// decimate11_01, decimate11_02, decimate11_03, decimate11_04, decimate11_05,
		// decimate11_06, decimate11_07, decimate11_08, decimate11_10, decimate11_11,
		// decimate11_12, decimate11_13, decimate11_14, decimate11_15, decimate11_16,
		// decimate11_17, decimate11_19, decimate11_20, decimate11_21, decimate12_02,
		// decimate12_03, decimate12_04, decimate12_06, decimate12_07, decimate12_08,
		// decimate12_09, decimate12_10, decimate12_14, decimate12_22, decimate13_01,
		// decimate13_02, decimate13_03, decimate13_04, decimate13_05, decimate13_06,
		// decimate13_07, decimate13_08, decimate13_09, decimate13_10, decimate13_11,
		// decimate13_12, decimate13_13, decimate13_15, decimate13_16, decimate13_19,
		// decimate14_01, decimate14_03, decimate14_05, decimate14_07, decimate14_09,
		// decimate14_11, decimate14_12, decimate14_14, decimate14_15, decimate14_17,
		// decimate14_19, decimate15_01, decimate15_02, decimate15_03, decimate15_04,
		// decimate15_09, decimate15_10, decimate15_11, decimate15_15, decimate15_17,
		// decimate16_01, decimate16_03, decimate16_04, decimate16_06, decimate16_07,
		// decimate16_09, decimate16_10, decimate16_11, decimate16_13, decimate16_14,
		// decimate17_01, decimate17_02, decimate17_03, decimate17_05, decimate17_06,
		// decimate17_07, decimate17_08, decimate17_10, decimate17_12, decimate17_13,
		// decimate17_14, decimate17_15, decimate17_16, decimate18_01, decimate18_13,
		// decimate18_15, decimate19_03, decimate19_04, decimate19_05, decimate19_06,
		// decimate19_07, decimate19_08, decimate19_09, decimate19_10, decimate19_11,
		// decimate19_12, decimate19_14, decimate19_15, decimate19_16, decimate20_01,
		// decimate20_13, decimate21_11, decimate21_14, decimate23_04, decimate24_02,
		// decimate29_03, decimate30_07, decimate31_01, decimate32_04, decimate33_04
		//
		// h*: hvlrbt, hvlrbt2, hvlrbt3, hvlrbt4, hvlrtb, hvlrtb2, hvlrtb3, hvlrtb4,
		// hvrlbt, hvrlbt2, hvrlbt3, hvrlbt4, hvrltb, hvrltb2, hvrltb3, hvrltb4
		//
		// l*: lrabt, lratb, lrbt, lrlrrlrlabt, lrlrrlrlatb, lrrlabt, lrrlatb, lrrlbt,
		// lrrltb, lrtb
		//
		// r*: rlabt, rlatb, rlbt, rllrabt, rllratb, rllrbt, rllrtb, rlrllrlrabt,
		// rlrllrlratb, rltb

		List<String> list = FileUtil.loadFrom(path);
		List<AZDecryptResult> results = new ArrayList<AZDecryptResult>();
		int maxFeatureSize = 0;
		for (String line : list) {
			if (which == 0) {
				String[] split = line.split("	");
				String name = split[0];
				int score = Integer.valueOf(split[1]);
				AZDecryptResult az = azFor(name, score, which);
				results.add(az);
			} else if (which == 1 || which == 2 || which == 3 || which == 4 || which == 5) {
				String[] split = line.split(" ");
				if ((which == 4 || which == 5) && split.length < 2) continue; // ignore 1steps when which==4 or 5
//				System.out.println(line);
				String name = split[1];
				int score = Integer.valueOf(split[0]);
				AZDecryptResult az = azFor(name, score, which);
				//System.out.println(az.features.size() + ", " + az.features + ", " + az.name);
				if ((which != 4 && which != 5) || ((which == 4 || which == 5) && az.features.size() == 6)) {
					results.add(az);
					maxFeatureSize = Math.max(maxFeatureSize, az.features.size());
					System.out.println(az.score + " " + az.features);
				}
			}
		}
		Collections.sort(results, new Comparator<AZDecryptResult>() {

			@Override
			public int compare(AZDecryptResult o1, AZDecryptResult o2) {
				// TODO Auto-generated method stub
				return Integer.compare(o2.getScore(), o1.getScore());
			}
			
		});
		
		System.out.println("max feature size: " + maxFeatureSize);
		
		float topN = results.size(); // we want the top p%
		topN = p * topN;
		int topNInt = Math.round(topN);
		System.out.println("topNInt " + topNInt + " out of " + results.size());
		
		int n = maxFeatureSize;
		int max = (int) pow(2, n)-1; // 2^n - 1
		// create a map
		// key maps to array of counts for each category [top, bottom, all]
		// then compute percentages and differences when compared to all
		// (compare top to bottom, too, to find widest disparities)
		Map<String, Counts> map = new HashMap<String, Counts>(); 
		for (int i=1; i<max-1; i++) {
			String s = "";
			System.out.println(Arrays.toString(bitsFor(i, n)));
			boolean[] mask = bitsFor(i, n); // ignore features in any position that is false in the mask
			for (int j=0; j<results.size(); j++) {
				AZDecryptResult az = results.get(j);
				String key = keyFrom(az, mask);
				Counts val = map.get(key);
				if (val == null) {
					val = new Counts();
					val.setN(results.size());
					val.setTopSize(topNInt);
					val.setKey(key);
					map.put(key, val);
				}
				// all
				val.incAll();
				// top
				if (j<topNInt) val.incTop();
				// bottom  [L-topNint,L-1]   L-1-x+1 = topNint   x = L-topNint
				if (j>=(results.size()-topNInt)) val.incBottom();   
			}
		}
		List<Counts> counts = new ArrayList<Counts>(map.values());
		for (Counts c :counts) if (c.doPrint()) System.out.println(c);
//		System.out.println("======== TOP: ");
//		Collections.sort(counts, new Comparator<Counts>() {
//			@Override
//			public int compare(Counts o1, Counts o2) {
//				// TODO Auto-generated method stub
//				return Float.compare(o1.diffRatioTop(), o2.diffRatioTop());
//			}
//		});
//		for (String key : map.keySet()) {
//			Counts val = map.get(key);
//			System.out.println(val);
//		}
//		System.out.println("======== BOTTOM: ");
//		Collections.sort(counts, new Comparator<Counts>() {
//			@Override
//			public int compare(Counts o1, Counts o2) {
//				// TODO Auto-generated method stub
//				return Float.compare(o1.diffRatioBottom(), o2.diffRatioBottom());
//			}
//		});
//		for (String key : map.keySet()) {
//			Counts val = map.get(key);
//			System.out.println(val);
//		}
//		System.out.println("======== TOP-BOTTOM: ");
//		Collections.sort(counts, new Comparator<Counts>() {
//			@Override
//			public int compare(Counts o1, Counts o2) {
//				// TODO Auto-generated method stub
//				return Float.compare(o1.diffRatioTopBottom(), o2.diffRatioTopBottom());
//			}
//		});
//		for (String key : map.keySet()) {
//			Counts val = map.get(key);
//			System.out.println(val);
//		}
		System.out.println("TOP SAMPLES: " + topNInt);
		System.out.println("TOTAL SAMPLES: " + results.size());
	}
	
	public static String keyFrom(AZDecryptResult az, boolean[] mask) {
		String key = "";
		int which = 0;
		for (boolean b : mask) {
			if (which == az.features.size()) break;
			String val = "*";
			if (b) val = az.features.get(which);
			if (!key.isEmpty()) key += " ";
			key += val;
			which++;
		}
		return key;
	}
	
	public static long pow(int base, int exponent) {
		long result = base;
		if (exponent == 0) return 1;
		for (int i=0; i<exponent-1; i++) {
			result *= base;
		}
//		System.out.println(base + "^" + exponent + "=" + result);
		return result;
	}
	
	public static long digit(int num, int which) {
		int n = which + 1;
		return (num % pow(2, n)) / pow(2, n-1);
	}
	public static boolean[] bitsFor(int num, int n) {
		boolean[] result = new boolean[n];
		for (int i=0; i<n; i++) 
			result[result.length-1-i] = digit(num, i) == 1 ? true : false;
		return result;
	}
	
	public static AZDecryptResult azFor(String name, int score, int which) {
		AZDecryptResult az = new AZDecryptResult();
		az.setName(name);
		az.makeFeatures(which);
		az.setScore(score);
		return az;
	}
	public static void main(String[] args) {
//		significanceTest(
//				"/Users/doranchak/projects/zodiac/sam blake ciphers/part 0/sam-blake-experiments/azdecrypt-scores-by-filename.txt",
//				0.1f, 0); // 2s
		
//		significanceTest(
//				"/Users/doranchak/projects/zodiac/sam blake ciphers/part 2/3rd batch (duplicates removed, mistakes corrected)/azdecrypt-results-batch-3/scores-by-features.txt",
//				0.1f, 1); // 26625 block enumerations

//		significanceTest(
//				"/Users/doranchak/projects/zodiac/sam blake ciphers/part 2/4th batch (grid enumerations, 4x8x9)/azdecrypt-results-batch-4/scores-by-filename-features-only.txt",
//				0.1f, 2); // 15400 4x8x9 grid enumerations

//		significanceTest(
//				"/Users/doranchak/projects/zodiac/sam blake ciphers/part 2/5th batch (grid enumerations, 4x9x9)/azdecrypt-results/scores-by-features.txt",
//				0.1f, 3); // 4x9x9 grid enumerations

//		significanceTest("/Users/doranchak/projects/zodiac/sam blake ciphers/ordered_list_23042020_daves_edit.txt",
//				0.1f, 4); // 25603 2 and 3 step enumerations, zkdecrypto results from sam

		significanceTest("/Users/doranchak/projects/zodiac/sam blake ciphers/part 1/scores-by-features.txt",
				0.1f, 5); // 2 and 3 step enumerations, azdecrypt results (to compare with which==4)
		
		String pt = "ILITATEGLASSNEONGREMAYNISIUSEAREWONDETERASMEEHLETSTAMISSLETSSIGNITCOATINADEDRESSWORTECONSTOMUTRYITAMENSHEREAISTMAGELONGASSTENSAMONGNITEIMAYMERTOMATSINDINSASEARCHRUNVESTIGAMESWORSEEITITSUSTLDONTRERDEESSTIRISNTSEELITHWAYSNAICATTHINITISADARDNEEATINGWOREWEESINRLLANTSURESIDONTHATOMATILLASTINGWGITINEMTNSSMOMORELSESSEMESEEMSTOMOSEINLITEESAYEARAWSASGOINOTTERSIANMMALSLOSSISRUNNERSANDEEMSTDREDGILOUARENTNSAMUROIDOAW";
		int c=0;
		for (int i=0; i<pt.length(); i++) {
			if (Ciphers.Z408_SOLUTION.toUpperCase().charAt(i) == pt.charAt(i)) c++;
		}
		System.out.println(c);
		
				
		
		//		for (int num=0; num<=32; num++) {
//			System.out.println(num);
//			for (int i=0; i<5; i++)
//				System.out.println(" - " + digit(num, i));
//			
//		}
	}
}
