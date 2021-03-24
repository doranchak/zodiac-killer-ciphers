package com.zodiackillerciphers.tests.unigrams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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

public class RegionRepeats {

	/** based on shuffle experiments, expected number of shared symbols by length.
	 * array index = length minus 1.
	 */
	static double[] expectedSharedByLengthZ340 = new double[] { 0.019467,
			0.075282, 0.16588, 0.286117, 0.435341, 0.612746, 0.813484,
			1.039246, 1.288292, 1.554188, 1.842793, 2.151709, 2.474476,
			2.817942, 3.17391, 3.544644, 3.935331, 4.336361, 4.75147, 5.175656,
			5.613419, 6.066261, 6.52757, 7.005724, 7.484847, 7.980374,
			8.485748, 8.996416, 9.515629, 10.043013, 10.582009, 11.125797,
			11.67639, 12.238372, 12.803135, 13.378197, 13.957985, 14.54325,
			15.138066, 15.734337, 16.34161, 16.956724, 17.559261, 18.188536,
			18.804955, 19.4414, 20.069023, 20.714918, 21.349627, 22.003248,
			22.654676, 23.31222, 23.970648, 24.635733, 25.302624, 25.977448,
			26.645948, 27.334663, 28.014463, 28.694508, 29.380749, 30.0773,
			30.768856, 31.464619, 32.167496, 32.864597, 33.577866, 34.28774,
			34.99476, 35.714176, 36.420063, 37.1431, 37.863293, 38.582985,
			39.308548, 40.040737, 40.75865, 41.50199, 42.229767, 42.964012,
			43.70372, 44.443768, 45.19372, 45.931465, 46.67686, 47.427624,
			48.17874, 48.92228, 49.674088, 50.4228, 51.19229, 51.947926,
			52.696884, 53.461266, 54.22111, 54.99456, 55.758053, 56.518578,
			57.287212, 58.060867 };

	static double[] expectedSharedByLengthZ408 = new double[] { 0.018428,
			0.072676, 0.160181, 0.280743, 0.428821, 0.606282, 0.814623,
			1.044244, 1.298823, 1.578546, 1.878909, 2.197112, 2.535072,
			2.89664, 3.275362, 3.669526, 4.073365, 4.504157, 4.941018,
			5.394558, 5.859429, 6.341248, 6.829568, 7.332903, 7.844776,
			8.370909, 8.907016, 9.450313, 9.999786, 10.562791, 11.133835,
			11.714567, 12.297599, 12.893325, 13.48993, 14.097165, 14.708035,
			15.331284, 15.955015, 16.587688, 17.227703, 17.863646, 18.521223,
			19.167477, 19.82448, 20.49086, 21.151648, 21.823732, 22.498766,
			23.171844, 23.865875, 24.550959, 25.241564, 25.934698, 26.62764,
			27.325247, 28.03005, 28.736645, 29.451935, 30.157509, 30.874887,
			31.596104, 32.319733, 33.038143, 33.7646, 34.492416, 35.225853,
			35.957905, 36.694416, 37.43333, 38.16773, 38.911423, 39.66143,
			40.403183, 41.149406, 41.904205, 42.65356, 43.41178, 44.155266,
			44.920876, 45.67296, 46.430927, 47.196095, 47.959583, 48.726933,
			49.490356, 50.26401, 51.029495, 51.813946, 52.56961, 53.350544,
			54.12317, 54.905766, 55.683018, 56.455235, 57.24669, 58.017944,
			58.807526, 59.592033, 60.37889 };

	/** consider rectangle R1 of given height and width.
	 *  consider identical rectangle R2
	 *  place R1 and R2 in cipher of given width, such that R1 and R2 do not overlap.
	 *  compute number of symbols shared by both R1 and R2
	 *    - track counts.  for example, symbol X may occur twice in R1 and twice in R2,
	 *      therefore the total count of shared symbols should increase.
	 *  divide by area of R1
	 *  call this result D (density of shared unigrams)
	 *  
	 *  find R1 and R2 such that D is maximum
	 *  
	 *  variation:  if minimize is true, find R1 and R2 such that D is minimum  
	 */
	public static RegionRepeatsBean maxSharedUnigramDensity(String cipher,
			int cipherWidth, int height, int width, boolean minimize) {
		/** upper left corner range:
		 * 		row: [0, cipherHeight-1-height+1]
		 * 		col: [0, cipherWidth-1-width+1]
		 */
		
		int cipherHeight = cipher.length()/cipherWidth;
		int rowBound = cipherHeight-1-height+1;
		int colBound = cipherWidth-1-width+1;
		
		RegionRepeatsBean result = new RegionRepeatsBean();
		result.z340 = Ciphers.Z340.equals(cipher);
		
		for (int pos1=0; pos1<cipher.length(); pos1++) {
			int r1 = pos1/cipherWidth;
			int c1 = pos1%cipherWidth;
			if (r1>rowBound || c1>colBound) continue;
			Rectangle rect1 = new Rectangle(r1, c1, height, width); 
			for (int pos2=pos1+1; pos2<cipher.length(); pos2++) {
				int r2 = pos2/cipherWidth;
				int c2 = pos2%cipherWidth;
				if (r2>rowBound || c2>colBound) continue;
				Rectangle rect2 = new Rectangle(r2, c2, height, width); 
				// check for overlap, using all 4 points of R2
				if (rect1.overlap(rect2)) {
//					System.out.println("overlap " + rect1 + ", " + rect2 + ", " + rect1.extract(cipher, cipherWidth) +", " + rect2.extract(cipher, cipherWidth));
					continue;
				}
//				System.out.println("valid " + rect1 + ", " + rect2 + ", " + rect1.extract(cipher, cipherWidth) +", " + rect2.extract(cipher, cipherWidth));
//				System.out.println("render(); " + rect1.render() + " " + rect2.render());
				String s1 = rect1.extract(cipher, cipherWidth); 
				String s2 = rect2.extract(cipher, cipherWidth);
				double density = shared(s1, s2);
				density /= s1.length();
				boolean better = false;
				if (result.density == null) better = true;
				else if (minimize && density < result.density) better = true;
				else if (density > result.density) better = true;
				if (better) {
					result.rect1 = rect1;
					result.rect2 = rect2;
					result.density = density;
				}
				
			}
		}
//		for (int r1=0; r1<=rowBound; r1++) {
//			for (int c1=0; c1<=colBound; c1++) {
//				Rectangle rect1 = new Rectangle(r1, c1, height, width); 
//				for (int r2=0; r2<=rowBound; r2++) {
//					for (int c2=0; c2<=colBound; c2++) {
//						Rectangle rect2 = new Rectangle(r2, c2, height, width); 
//						// check for overlap, using all 4 points of R2
//						if (rect1.overlap(rect2)) {
////							System.out.println("overlap " + rect1 + ", " + rect2 + ", " + rect1.extract(cipher, cipherWidth) +", " + rect2.extract(cipher, cipherWidth));
//							continue;
//						}
//						System.out.println("valid " + rect1 + ", " + rect2 + ", " + rect1.extract(cipher, cipherWidth) +", " + rect2.extract(cipher, cipherWidth));
//					}
//				}
//				
//			}
//		}
		return result;
	}

	
	/** return number of symbols shared between two string samples */
	public static int shared(String s1, String s2) {
		Map<Character, Integer> c1 = counts(s1);
		Map<Character, Integer> c2 = counts(s2);
		int result = 0;
		for (Character key : c1.keySet()) {
			Integer val1 = c1.get(key);
			Integer val2 = c2.get(key);
			if (val2 == null) continue;
			result += Math.min(val1, val2);
		}
		return result;
	}
	public static Map<Character, Integer> counts(String s) {
		Map<Character, Integer> counts = new HashMap<Character, Integer>();
		if (s == null) return counts;
		for (int i=0; i<s.length(); i++) {
			char key = s.charAt(i);
			Integer val = counts.get(key);
			if (val == null) val = 0;
			val++;
			counts.put(key, val);
		}
		return counts;
	}
	
	public static void testShared() {
		String s1 = "ABCDEEFG";
		String s2 = "EEEEEFGHIJKLMN";
		System.out.println(shared(s1, s2));
	}
	
	/** a simple shuffle test: select two substrings of shuffled cipher at random and compute how many symbols they have in common. */ 
	public static void shuffleShared(int shuffles) {
		String cipher = Ciphers.Z408;
		for (int L=1; L<=100; L++) {
			float count = 0;
			for (int i=0; i<shuffles; i++) {
				cipher = CipherTransformations.shuffle(cipher);
				String s1 = cipher.substring(0,L);
				String s2 = cipher.substring(L,2*L);
				count += shared(s1, s2);
			}
			System.out.println(L+"	"+(count/shuffles));
		}
	}

	/** gather statistics for maxSharedUnigramDensity for shuffled cipher */
	public static void shuffleMaxSharedUnigramDensity(String cipher,
			int cipherHeight, int cipherWidth, int shuffles) {
		System.out.println("Cipher: " + cipher);
		String refCipher = cipher;
		for (int height = 1; height <= cipherHeight / 2; height++) {
			for (int width = 1; width <= cipherWidth / 2; width++) {
				StatsWrapper stats = new StatsWrapper();
				stats.actual = maxSharedUnigramDensity(refCipher,
						cipherWidth, height, width, false).density;
				for (int i = 0; i < shuffles; i++) {
					cipher = CipherTransformations.shuffle(cipher);
					stats.addValue(maxSharedUnigramDensity(cipher, cipherWidth,
							height, width, false).density);
				}
				stats.name = height + " " + width;
				stats.output();
			}
		}
	}
	public static void shuffleMaxSharedUnigramDensityOLD_VERSION(String cipher,
			int cipherHeight, int cipherWidth, int shuffles) {
		String refCipher = cipher;
		for (int height = 1; height <= cipherHeight / 2; height++) {
			for (int width = 1; width <= cipherWidth / 2; width++) {
				DescriptiveStatistics stats = new DescriptiveStatistics();
				double reference = maxSharedUnigramDensity(refCipher,
						cipherWidth, height, width, false).density;
				for (int i = 0; i < shuffles; i++) {
					cipher = CipherTransformations.shuffle(cipher);
					stats.addValue(maxSharedUnigramDensity(cipher, cipherWidth,
							height, width, false).density);
				}
				double sigma = 0;
				if (stats.getStandardDeviation() != 0)
					sigma = Math.abs(reference - stats.getMean())
							/ stats.getStandardDeviation();
				System.out.println(height + "	" + width + "	" + stats.getMin()
						+ "	" + stats.getMax() + "	" + stats.getMean() + "	"
						+ stats.getPercentile(50) + "	"
						+ stats.getStandardDeviation() + "	" + reference + "	"
						+ sigma);
			}
		}
	}
	
	public static void testMaxSharedZ340() {
		for (int height=1; height<=10; height++) {
			for (int width=0; width<=9; width++) {
				System.out.println(height + "	" + width + "	" + maxSharedUnigramDensity(Ciphers.Z340, 17, height, width, false));
			}
		}
//		Rectangle r1= new Rectangle(0, 0, 10, 17); 
//		Rectangle r2= new Rectangle(10, 0, 10, 17);
//		System.out.println(r1);
//		System.out.println(r2);
//		System.out.println(r1.overlap(r2));
	}
	public static void testMaxSharedZ408() {
		for (int height=1; height<=12; height++) {
			for (int width=0; width<=9; width++) {
				System.out.println(height + "	" + width + "	" + maxSharedUnigramDensity(Ciphers.Z408, 17, height, width, false));
			}
		}
//		Rectangle r1= new Rectangle(0, 0, 10, 17); 
//		Rectangle r2= new Rectangle(10, 0, 10, 17);
//		System.out.println(r1);
//		System.out.println(r2);
//		System.out.println(r1.overlap(r2));
	}
	
	/** find all pairs of non-overlapping NxM rectangular regions.
	 * for each pair, find all symbols that:
	 * 1) occur at least once in each region of the pair
	 * and
	 * 2) do not occur outside of either region
	 */
	
	public static void exclusiveSymbolsInRegionPair(String cipher, Map<Character, Integer> countMap, int cipherWidth, int height, int width, int shuffles) {
		/** upper left corner range:
		 * 		row: [0, cipherHeight-1-height+1]
		 * 		col: [0, cipherWidth-1-width+1]
		 */
		
		int cipherHeight = cipher.length()/cipherWidth;
		int rowBound = cipherHeight-1-height+1;
		int colBound = cipherWidth-1-width+1;
		
		for (int pos1=0; pos1<cipher.length(); pos1++) {
			int r1 = pos1/cipherWidth;
			int c1 = pos1%cipherWidth;
			if (r1>rowBound || c1>colBound) continue;
			Rectangle rect1 = new Rectangle(r1, c1, height, width); 
			for (int pos2=pos1+1; pos2<cipher.length(); pos2++) {
				int r2 = pos2/cipherWidth;
				int c2 = pos2%cipherWidth;
				if (r2>rowBound || c2>colBound) continue;
				Rectangle rect2 = new Rectangle(r2, c2, height, width); 
				// check for overlap, using all 4 points of R2
				if (rect1.overlap(rect2)) {
					continue;
				}
				//System.out.println("valid " + rect1 + ", " + rect2 + ", " + rect1.extract(cipher, cipherWidth) +", " + rect2.extract(cipher, cipherWidth));
//				System.out.println("render(); " + rect1.render() + " " + rect2.render());
				
				RegionRepeatsBean result = process(cipher, countMap, cipherWidth, rect1, rect2);
				
				/** now do some shuffles */
				String shuffle = cipher;
				StatsWrapper stats1 = new StatsWrapper();
				stats1.actual = result.symbols.size();
				StatsWrapper stats2 = new StatsWrapper();
				stats2.actual = result.positions;
				for (int i=0; i<shuffles; i++) {
					shuffle = CipherTransformations.shuffle(cipher);
					RegionRepeatsBean resultShuffle = process(shuffle, countMap, cipherWidth, rect1, rect2);
					stats1.addValue(resultShuffle.symbols.size());
					stats2.addValue(resultShuffle.positions);
				}
				String line = result.area + "	";
				line += height + "	";
				line += width + "	";
				line += pos1 + "	";
				line += pos2 + "	";
				line += result.symbols() + "	";
				line += result.symbols().length() + "	";
				line += stats1.stats.getMean() + "	";
				line += stats1.sigma() + "	";
				line += result.positions + "	";
				line += stats2.stats.getMean() + "	";
				line += stats2.sigma() + "	";
				line += result.density;
				System.out.println(line);
			}
		}
	}
	
	static void processRegionResults(String file) {
		List<String[]> list = new ArrayList<String[]>();
		
		/** track smallest region area per symbol set */
		Map<String, Integer> mapArea = new HashMap<String, Integer>();
		Map<String, String> mapLine = new HashMap<String, String>(); 
		
		for (String line : FileUtil.loadFrom(file)) {
			String[] split = line.split("	");
			int area = Integer.valueOf(split[0]);
			String symbols = split[5];
			if (symbols.length() < 2) continue;
			Integer val = mapArea.get(symbols);
			if (val == null) val = Integer.MAX_VALUE;
			if (area < val) {
				mapArea.put(symbols,  area);
				mapLine.put(symbols, line);
			}
		}
		for (String key : mapLine.keySet()) {
			System.out.println(key.length() + "	" + mapLine.get(key));
		}
	}
	
	static RegionRepeatsBean process(String cipher, Map<Character, Integer> countMap, int cipherWidth, Rectangle rect1, Rectangle rect2) {
		String s1 = rect1.extract(cipher, cipherWidth);
		Map<Character, Integer> count1 = Ciphers.countMap(s1);
		String s2 = rect2.extract(cipher, cipherWidth);
		Map<Character, Integer> count2 = Ciphers.countMap(s2);
		
		/** keep all symbols that appear in both rectangles, AND do not appear elsewhere in the cipher text. */
		Set<Character> set = new HashSet<Character>();
		int positions = 0;
		for (char ch : count1.keySet()) {
			if (!count2.containsKey(ch)) continue;
			int count = count1.get(ch) + count2.get(ch);
			if (count == countMap.get(ch)) { 
				set.add(ch);
				positions += countMap.get(ch);
			}
		}
		double density = positions;
		int area = s1.length() + s2.length();
		density /= (area);
		
		RegionRepeatsBean result = new RegionRepeatsBean();
		result.symbols = set;
		result.positions = positions;
		result.area = area;
		result.density = density;
		result.rect1 = rect1;
		result.rect2 = rect2;
		return result;
	}
	
	static void exclusiveSymbolsInRegionPair(String cipher, int width, int shuffles) {
		int H = cipher.length()/width;
		for (int h=1; h<=H/2; h++) {
			for (int w=1; w<=width; w++) {
				if (h==1 && w==1) continue;
				//if (h*w == 102)
				exclusiveSymbolsInRegionPair(cipher, Ciphers.countMap(cipher), width, h, w, shuffles);
			}
		}
		
	}
	
	public static void main(String[] args) {
		//testShared();
		//shuffleShared(1000000);
		//testMaxSharedZ408();
		//testMaxSharedZ340();
//		shuffleMaxSharedUnigramDensity(Ciphers.Z408, 24, 17, 1000);
//		shuffleMaxSharedUnigramDensity(Ciphers.Z340, 24, 17, 1000);
		//exclusiveSymbolsInRegionPair(Ciphers.Z340, Ciphers.countMap(Ciphers.Z340), 17, 6, 17);
		//exclusiveSymbolsInRegionPair(Ciphers.Z408, 17, 1000);
		processRegionResults("/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/tests/unigrams/z340-exclusiveSymbolsInRegionPair.txt");
		
	}
}
