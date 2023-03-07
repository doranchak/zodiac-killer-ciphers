package com.zodiackillerciphers.tests.unigrams;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.stats.StatsWrapper;
import com.zodiackillerciphers.transform.CipherTransformations;

public class UniqueUnigramsPerRow {

	/** count the number of unique symbols in the given row of the cipher */
	public static int countNonRepeats(String cipher, int row) {
		int count = 0;
		Set<Character> seen = new HashSet<Character>();
		for (int pos = row * 17; pos < row * 17 + 17; pos++) {
			if (pos < cipher.length()) {
				char ch = cipher.charAt(pos);
				seen.add(ch);
			}
		}
		return seen.size();
	}

	public static int countNonRepeats(String cipher, int row, int width) {
		int count = 0;
		Set<Character> seen = new HashSet<Character>();
		for (int pos = row * width; pos < row * width + width
				&& pos < cipher.length(); pos++) {
			if (pos < cipher.length()) {
				char ch = cipher.charAt(pos);
				seen.add(ch);
			}
		}
		return seen.size();
	}

	public static void shuffleTest(String cipher, int width, int shuffles) {
		Map<Integer, Integer> actual = new HashMap<Integer, Integer>();
		int rows = cipher.length() / width;
		if (cipher.length() % width > 0)
			rows++;
		Map<Integer, StatsWrapper> map = new HashMap<Integer, StatsWrapper>();
		for (int row = 0; row < rows; row++) {
			int val = countNonRepeats(cipher, row, width);
			actual.put(row, val);
			StatsWrapper stats = new StatsWrapper();
			stats = new StatsWrapper();
			stats.name = "width " + width + " row " + row;
			stats.actual = val;
			map.put(row, stats);
		}
//		System.out.println(actual);

		for (int i = 0; i < shuffles; i++) {
			cipher = CipherTransformations.shuffle(cipher);
			for (int row = 0; row < rows; row++) {
				int val = countNonRepeats(cipher, row, width);
				StatsWrapper stats = map.get(row);
				stats.addValue(val);
				map.put(row, stats);
			}
		}
		double sum = 0;
		for (StatsWrapper s : map.values()) {
//			s.output();
			if (Double.isNaN(s.sigma())) continue;
			sum += s.sigma();
		}
		System.out.println("Width " + width + " Sigma sum: " + sum);
		System.out.println("Width " + width + " Sigma mean: " + sum/map.size());
	}

	public static void testCount(String cipher, int width) {
		int rows = cipher.length() / width;
		if (cipher.length() % width > 0)
			rows++;
		for (int row = 0; row < rows; row++) {
			System.out.println(row + " " + countNonRepeats(cipher, row, width));
		}
	}
	/** return number of lines that have no repeating unigrams */
	public static int countNonRepeatingLines(String cipher, int width) {
		int result = 0;
		int rows = cipher.length() / width;
		if (cipher.length() % width > 0)
			rows++;
		for (int row = 0; row < rows; row++) {
			if (countNonRepeats(cipher, row, width) == width)
				result++;
		}
		return result;
	}
	
	public static void nonRepeatingLinesShuffle(String cipher, int width, int shuffles) {
		StatsWrapper stats = new StatsWrapper();
		stats.name = "width " + width;
		stats.actual = countNonRepeatingLines(cipher, width);
		for (int i=0; i<shuffles; i++) {
			cipher = CipherTransformations.shuffle(cipher);
			stats.addValue(countNonRepeatingLines(cipher, width));
		}
		stats.output();
	}
	
	/**
	 * calculate sum of repeating symbols per row (for example, if symbol A occurs 3
	 * times, then there are 2 repeats)
	 */
	public static int repeatsPerRow(String cipher, int width) {
		int reps = 0;
		int height = cipher.length()/width;
		for (int row=0; row<height; row++) {
			Set<Character> seen = new HashSet<Character>(); 
			for (int col=0; col<width; col++) {
				char c = cipher.charAt(row*width + col);
				if (seen.contains(c)) {
					reps++;
					continue;
				}
				seen.add(c);
			}
		}
		return reps;
	}
	
	public static void test() {
		//testCount(Ciphers.Z340, 10);

//		for (int width=2; width<=204; width++) {
//			shuffleTest(Ciphers.Z408, width, 10000);
//		}

		//		shuffleTest(Ciphers.Z408, 11, 10000);
		
//		System.out.println(countNonRepeatingLines(Ciphers.Z408, 17));

//		System.out.println("Z408");
//		for (int width=2; width<=170; width++) {
//			//nonRepeatingLinesShuffle(Ciphers.cipher[5].cipher, width, 100000);
//			nonRepeatingLinesShuffle(Ciphers.cipher[1].cipher, width, 1000000);
//		}
//		System.out.println("Z340");
//		for (int width=2; width<=170; width++) {
//			//nonRepeatingLinesShuffle(Ciphers.cipher[5].cipher, width, 100000);
//			nonRepeatingLinesShuffle(Ciphers.cipher[0].cipher, width, 1000000);
//		}
//		nonRepeatingLinesShuffle(Ciphers.cipher[1].cipher, width, 100000);
//		nonRepeatingLinesShuffle(Ciphers.Z408, 17, 10000000);
//		nonRepeatingLinesShuffle(Ciphers.Z340, 17, 10000000);
		// z408 by column
//		System.out.println("z408 by column");
//		nonRepeatingLinesShuffle(
//				"9WMSk@RLcBZIPrz^Np%VH_ZV%VJ(^+NdZPc95IU=keOZ!e@EP+Y/Lqt/KDpDM!pS)XTeFdJX/e^9MGIPqROR8JkrSq5GB#TrZGU#ZDY#p+V_RkAlcWRYX#t9/YIBJ9EBIjWTU59fEqUK96qWUFkPdKl@)=IYt9#U/_cEze_IB67OrIOXW65r%8Be9F+_X586%9qR\\\\)8qq\\\\+\\\\LLV6%#_TAPJqkHTAp6qE!Ntd)MW7%8dYDOIEOPtUFqGH8(LeNl\\\\DZcYAdR+HR@N%HXBM5e)/VN+zf+q9\\\\XrM=KQfV8TULEl@EAVGA@_%7QB)p!YRW5Q^MU^XK)T%P9^#LFP=XqDlezSRrHRJHZt%#ASL!%QU=Y5q8S#R9k6Q=(OIB9qt=GWIBe)EY(Bk#FHAGPPMVBW_qc6k",
//				24, 10000000);
//		for (int width = 2; width <= 170; width++) {
//			nonRepeatingLinesShuffle(
//					"9WMSk@RLcBZIPrz^Np%VH_ZV%VJ(^+NdZPc95IU=keOZ!e@EP+Y/Lqt/KDpDM!pS)XTeFdJX/e^9MGIPqROR8JkrSq5GB#TrZGU#ZDY#p+V_RkAlcWRYX#t9/YIBJ9EBIjWTU59fEqUK96qWUFkPdKl@)=IYt9#U/_cEze_IB67OrIOXW65r%8Be9F+_X586%9qR\\\\)8qq\\\\+\\\\LLV6%#_TAPJqkHTAp6qE!Ntd)MW7%8dYDOIEOPtUFqGH8(LeNl\\\\DZcYAdR+HR@N%HXBM5e)/VN+zf+q9\\\\XrM=KQfV8TULEl@EAVGA@_%7QB)p!YRW5Q^MU^XK)T%P9^#LFP=XqDlezSRrHRJHZt%#ASL!%QU=Y5q8S#R9k6Q=(OIB9qt=GWIBe)EY(Bk#FHAGPPMVBW_qc6k",
//					width, 1000000);
//		}
		// z340 by column
		System.out.println("z340 by column");
		nonRepeatingLinesShuffle(
				"HNBS_p#(d-U2zly|R+|>Epyp985G<z+<6GBcc+FMR+:pMR+2MlRc9FX.T)kD>Bc7+^KJ+U/lSN13+WdHp(M^zFqfbV5Ry^*zLCWNl#+ltl%j++tJ#f:B1z<p^OU8jO;#Z^E|+54K6W7kV%Z*d-2ORJ|*N29(CctSPDGV|*U+2+D5|4CO<PBzkWW35dc_FOYT5bEp+O_Z|Y(pFCXNBpB4F.>^FSYO1.)OPkGYc7pMBcV.lHO8L<L++FVzy<b.cVUfWTBAT*#+&>.+AFT+(4ZMB/*|GKzR42z@6BM&;t5q|(-K2fHKkDLL4yKB8+-G))C;d)J2/(|9K-OFR++2Lpc+",
				20, 10000000);
		for (int width = 2; width <= 170; width++) {
			// nonRepeatingLinesShuffle(Ciphers.cipher[5].cipher, width, 100000);
			nonRepeatingLinesShuffle("HNBS_p#(d-U2zly|R+|>Epyp985G<z+<6GBcc+FMR+:pMR+2MlRc9FX.T)kD>Bc7+^KJ+U/lSN13+WdHp(M^zFqfbV5Ry^*zLCWNl#+ltl%j++tJ#f:B1z<p^OU8jO;#Z^E|+54K6W7kV%Z*d-2ORJ|*N29(CctSPDGV|*U+2+D5|4CO<PBzkWW35dc_FOYT5bEp+O_Z|Y(pFCXNBpB4F.>^FSYO1.)OPkGYc7pMBcV.lHO8L<L++FVzy<b.cVUfWTBAT*#+&>.+AFT+(4ZMB/*|GKzR42z@6BM&;t5q|(-K2fHKkDLL4yKB8+-G))C;d)J2/(|9K-OFR++2Lpc+", width, 1000000);
		}

	}
	
	public static void shuffleRepeatsPerRow(String cipher, int width, int shuffles) {
		StatsWrapper stats = new StatsWrapper();
		stats.actual = repeatsPerRow(cipher, width);
		for (int i=0; i<shuffles; i++) {
			cipher = CipherTransformations.shuffle(cipher);
			stats.addValue(repeatsPerRow(cipher, width));
		}
		System.out.println(stats.header());
		stats.output();
	}
	
	public static void testRepeatsPerRow() {
		System.out.println(repeatsPerRow(Ciphers.Z408, 17));
		System.out.println(repeatsPerRow(Ciphers.Z340, 17));
	}

	public static void main(String[] args) {
		//testRepeatsPerRow();
		shuffleRepeatsPerRow(Ciphers.Z408, 17, 1000000);
	}

}
