package com.zodiackillerciphers.old;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.io.FileUtil;

public class PivotsOLD {
	
	static String CORPUS_DIR = "/Users/doranchak/projects/work/java/zodiac/letters/corpus-for-pivot-experiments/all";
	/** # of characters in block */
	public static int L = 340;
	/** width of block */
	public static int W = 17;
	/** height of block */
	public static int H = 20;
	/** entire corpus, as a single line */
	public static StringBuffer line;
	
	/** map file names to positions in the line */
	public static List<Object[]> index;
	
	public static Map<Character, Integer> letterCounts;
	static {
		letterCounts = new HashMap<Character, Integer>();
		letterCounts.put('E', 20457);
		letterCounts.put('T', 12679);
		letterCounts.put('A', 9029);
		letterCounts.put('H', 8513);
		letterCounts.put('N', 7109);
		letterCounts.put('O', 6896);
		letterCounts.put('R', 6711);
		letterCounts.put('I', 6086);
		letterCounts.put('S', 5998);
		letterCounts.put('D', 3436);
		letterCounts.put('L', 2321);
		letterCounts.put('C', 1450);
		letterCounts.put('M', 1305);
		letterCounts.put('U', 1186);
		letterCounts.put('G', 1060);
		letterCounts.put('W', 1012);
		letterCounts.put('F', 872);
		letterCounts.put('P', 719);
		letterCounts.put('B', 596);
		letterCounts.put('Y', 574);
		letterCounts.put('V', 296);
		letterCounts.put('X', 280);
		letterCounts.put('K', 270);
		letterCounts.put('Z', 40);
		letterCounts.put('J', 8);
		letterCounts.put('Q', 4);
	}
	
	/** load corpus */
	public static void load() {
		index = new ArrayList<Object[]>();
		line = new StringBuffer();
		File[] files = FileUtil.listFrom(CORPUS_DIR);
		for (File file : files) {
			if (file.getName().endsWith(".txt") || file.getName().endsWith(".TXT")) {
				index.add(new Object[] {line.length(), file.getName()});
				List<String> lines = FileUtil.loadFrom(file.getAbsolutePath());
				for (String s : lines) line.append(convert(s));
			}
		}
		
		
		//List<String> lines = FileUtil.loadFrom(CORPUS_DIR);
		//line = lines.get(0);
		System.out.println("Loaded " + line.length() + " characters.");
	}
	
	/** return which file is at this position */
	public static String fileAt(int pos) {
		String fileName = null;
		int i = -1; int j=-1;
		for (Object[] o : index) {
			i = (Integer) o[0];
			if (i>pos) return fileName;
			fileName = (String) o[1];
			j = i;
		}
		return fileName;
	}
	
	/** convert given string to all caps, A-Z only */
	public static String convert(String str) {
		if (str == null) return null;
		String upper = str.toUpperCase();
		StringBuffer result = new StringBuffer();
		for (int i=0; i<upper.length(); i++) {
			char ch = upper.charAt(i);
			if (ch > 64 && ch < 91) result.append(ch);
		}
		//System.out.println(result);
		return result.toString();
	}
	
	
	/** translate position to row number */
	public static int rowFrom(int pos) {
		return pos / W;
	}

	/** translate position to col number */
	public static int colFrom(int pos) {
		return pos % W;
	}
	
	/** translate (row,col) to position */
	public static int posFrom(int row, int col) {
		return row*W + col;
	}
	
	/** find all pivots within the given L-character text.  ignore any legs that aren't at least minLegLength in length. */
	public static List<PivotOLD> pivots(String sub, int minLegLength) {
		String[] d = {"N","E","S","W"};
		List<PivotOLD> list = new ArrayList<PivotOLD>();

		for (int i=0; i<sub.length(); i++) {
			int row = rowFrom(i);
			int col = colFrom(i);
			
			PivotOLD pivot = new PivotOLD();
			pivot.row = row; pivot.col = col;
			pivot.positions.add(new int[] {row,col});
			pivot.intersectingSymbol = sub.charAt(i);
			
			// n-grams forming the legs of the pivot
			String[] legs = {"", "", "", ""};
			String[] legsFinal = new String[4];
			// how far to go in each direction
			int leg = 1;
			// number of matching legs so far
			//int legsCount = 4;
			
			String ch;
			boolean go = true;
			while (go) { 
				// array of directions. N, E, S, W.
				int[][] p = { {row-leg,col}, {row, col+leg}, {row+leg, col}, {row, col-leg} };
				for (int j=0; j<p.length; j++) {
					if (legs[j] == null) continue; // this leg already had no more matches
					ch = charAt(sub, p[j]); // get character
					//System.out.println("ch " + ch);
					if (ch == null) { // can't extend this leg any further
						legsFinal[j] = legs[j];
						legs[j] = null;
						//legsCount--;
					} else { // otherwise, extend this leg and see if it still matches one of the other legs.
						legs[j] += ch;
					}
				}

				// legs are all made.  check for matches now.
				for (int j=0; j<p.length; j++) {
					if (legs[j] == null) continue; // this leg already had no more matches
					//System.out.println(charAt(sub,new int[] {row,col})+","+row+","+col+","+leg+","+j+","+legs[j]);
					boolean found = false;
					for (int k=0; k<legs.length; k++) {
						if (j==k) continue;
						if (legs[k] == null) continue;
						if (legs[k].equals(legs[j])) {
							found = true;
							break;
						}
					}
					if (found) { // track positions
						pivot.positions.add(p[j]);
					}
					else { // no match was found, so this leg can no longer be extended.
						if (legs[j].length() > 0)
							legsFinal[j] = legs[j].substring(0, legs[j].length()-1);
						legs[j] = null;
						//legsCount--;
					}
				}
				
				
				// keep looking only if at least two legs remain
				int legsCount = 0;
				for (int j=0; j<legs.length; j++) if (legs[j] != null) legsCount++;
				go = legsCount > 1;
				leg++;
				//System.out.println(go+","+legsCount+","+leg);
			}
			
			for (int j=0; j<legsFinal.length; j++) {
				if (legsFinal[j] == null || "".equals(legsFinal[j])) continue;
				
				if (legsFinal[j].length() >= minLegLength) {
					pivot.legsCount++;
					pivot.legs.add(legsFinal[j]);
					if (legsFinal[j].length() > 2) {
						pivot.directions.add(d[j]);
					}
				}
			}
			pivot.makePairs();
			list.add(pivot);
		}
		return list;
	}

	// return character from given row,col
	public static String charAt(String s, int[] pos) {
		if (pos[0] < 0 || pos[0] >= H) return null; // out of bounds
		if (pos[1] < 0 || pos[1] >= W) return null; // out of bounds
		return ""+s.charAt(posFrom(pos[0], pos[1]));
	}
	
	/** count total number of repeating trigrams in the given string */
	public static int trigrams(String sub) {
		int count = 0;
		if (sub == null) return count;
		Set<String> tris = new HashSet<String>();
		for (int i=0; i<=sub.length()-3; i++) {
			String tri = sub.substring(i,i+3);
			if (tris.contains(tri)) {
				count++;
				//System.out.println(tri);
			}
			else tris.add(tri);
		}
		return count;
	}
	
	/** find all pivots within the corpus.  report counts for minLegLength, the minimum leg length. */
	public static void pivotSearch(int minLegLength) {
		load();
		
		int counter = 0;
		
		int trigrams;
		int trigramsMin = Integer.MAX_VALUE;
		int trigramsMax = 0;
		int trigramsSum = 0;
		
		int counterSameOrientation = 0;
		
		// Number of tests that have at least [key] pivots of min leg length [minLegLength]. 
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();

		// alphabet frequency within the pivots 
		Map<String, Integer> alphaMap = new HashMap<String, Integer>();
		
		// track pivots vs repeated trigram counts for each test.  occurrences of pivots are probably proportional to number of repeated trigrams
		// (only include tests that have at least 2 pivots)
		List<int[]> trigramStats = new ArrayList<int[]>();
		
		// test all 340-character substrings 
		for (int i=0; i<line.length()-L; i+=L) {
			if (i%1000000 == 0) {
				System.out.println(i+"...");
				dumpMap(map, minLegLength);
			}
			String sub = line.substring(i,i+L);
			
			trigrams = trigrams(sub);
			trigramsMin = Math.min(trigramsMin, trigrams);
			trigramsMax = Math.max(trigramsMax, trigrams);
			trigramsSum += trigrams;
			// locate any pivots
			List<PivotOLD> pivots = pivots(sub, minLegLength);
			
			int count = 0;
			
			count += count(pivots, minLegLength, true, sub);
			
			/*
			if (minLegLength > 2 && count > 1) {
				System.out.println("found " + count + " pivots in this test.  File " + fileAt(i) + "<br><br>");
				for (Pivot p : pivots) {
					if (p.longestLeg() >= minLegLength) {
						//System.out.println(p + " i " + i + " " + p.html(sub));
						System.out.println(p+"<br>");
						//System.out.println(p.html(sub)+"<br><br>");
					}
				}
				System.out.println(Pivot.html(sub, pivots)+"<br><br>");
			}*/
			
			if (count > 1) {
				// we have at least 2 pivots, so track them
				int[] stats = new int[] {count, trigrams};
				trigramStats.add(stats);
				
				// count the number of tests that have at least 2 pivots in the same orientation
				Set<String> pairs = new HashSet<String>();
				for (PivotOLD p : pivots) {
					for (String pair : p.pairs) {
						if (pairs.contains(pair)) counterSameOrientation++;
						else pairs.add(pair);
					}
				}
				
				// track the frequency distribution of letters
				for (int z=0; z<sub.length(); z++) {
					String ch = ""+sub.charAt(z);
					Integer val = alphaMap.get(ch);
					if (val == null) val = 0;
					val++;
					alphaMap.put(ch, val);
				}
				/*Set<String> set = new HashSet<String>();
				for (Pivot p : pivots) {
					for (int[] pos : p.positions) {
						String key = pos[0]+","+pos[1];
						if (set.contains(key)) continue;
						String ch = charAt(sub, pos);
						Integer val = alphaMap.get(ch);
						if (val == null) val = 0;
						val++;
						alphaMap.put(ch, val);
					}
				}*/
			}
			//System.out.println("Count	" + count+"	"+counter);
			for (int key=0; key<=count; key++) {
				Integer val = map.get(key);
				if (val == null) val = 0;
				val ++;
				map.put(key, val);
			}
			
//			info(pivots); // print pivot info
			//if (dumpPivots(pivots)) { // dump the L-character block, but only if the pivots are significant
//				dump(sub, pivots);
			//}
			
			counter++;
		}
		
		dumpMap(map, minLegLength);
		System.out.println("Total tests: " + counter);
		System.out.println("Average trigrams: " + ((float)trigramsSum)/counter + ", min " + trigramsMin + ", max " + trigramsMax);

		for (String a : alphaMap.keySet()) System.out.println("Frequency " + a + " " + alphaMap.get(a));
		
		// for each trigram repetition count, determine number of tests that had at least 2 pivots AND a repetition count less than or equal to this threshold.
		
		/*
		for (int i=trigramsMin; i<=trigramsMax; i++) {
			int c = 0;
			for (int j=0; j<trigramStats.size(); j++) {
				int t = trigramStats.get(j)[1];
				if (t<=i) c++;
			}
			System.out.println(c + " tests found at least 2 pivots with repeated trigrams less than or equal to " + i);
		}
		// for each trigram repetition count, determine number of tests that had at least 2 pivots AND a repetition count greater than or equal to this threshold.
		for (int i=trigramsMin; i<=trigramsMax; i++) {
			int c = 0;
			for (int j=0; j<trigramStats.size(); j++) {
				int t = trigramStats.get(j)[1];
				if (t>=i) c++;
			}
			System.out.println(c + " tests found at least 2 pivots with repeated trigrams greater than or equal to " + i);
		}*/
		System.out.println("counterSameOrientation " + counterSameOrientation);
	}
	
	/** does the given list have n pivots pointing in the same direction? */
	public static boolean sameDirection(List<PivotOLD> pivots, int n) {
		Map<String, Integer> counts = new HashMap<String, Integer>();
		for (PivotOLD pivot : pivots) {
			for (String pair : pivot.pairs) {
				Integer val = counts.get(pair);
				if (val == null) val = 0;
				val++;
				if (val == n) return true;
				counts.put(pair, val);
			}
		}
		return false;
	}
	
	/** return pivots organized by direction */
	public static Map<String, List<PivotOLD>> sameDirectionPivots(List<PivotOLD> pivots) {
		Map<String, List<PivotOLD>> map = new HashMap<String, List<PivotOLD>>(); // map pivot direction to list of all pivots going in that direction
		for (PivotOLD pivot : pivots) {
			for (String pair : pivot.pairs) {
				List<PivotOLD> val = map.get(pair);
				if (val == null) val = new ArrayList<PivotOLD>();
				val.add(pivot);
				map.put(pair, val);
			}
		}
		return map;
	}
	
	/** count the number of pivots that meet our requirements */
	public static int count(List<PivotOLD> pivots, int minLegLength, boolean sameOrientation, String sub) {
		int c = 0;
		String maxPair = null;
		if (sameOrientation) {
			// count the max number of tests that have at least 2 pivots in the same orientation
			Map<String, Integer> pairs = new HashMap<String, Integer>();
			int max = 0;
			for (PivotOLD p : pivots) {
				for (String pair : p.pairs) {
					//if (!pair.equals("NW")) continue;  // only the ones that point like the 340
					Integer val = pairs.get(pair);
					if (val == null) val = 0;
					val++;
					pairs.put(pair, val);
					if (val > max) {
						max = val;
						maxPair = pair;
					}
				}
			}
			c = max;
			/*
			if (c > 1) {
				int pivotScore = 0;
				for (Pivot p : pivots) {
					for (String leg : p.legs) {
						System.out.println("leg " + leg);
						for (int i=0; i<leg.length(); i++) pivotScore += letterCounts.get(leg.charAt(i));
					}
				}
				System.out.println("pivotScore " + pivotScore + " <br>" + Pivot.html(sub, pivots)+"<br><br>");

			}*/
		} else {
			for (PivotOLD p : pivots) {
				if (p.legsCount > 1 && p.longestLeg() >= minLegLength) {
					c++;
				}
			}
		}
		return c;
	}

	
	public static void dumpMap(Map<Integer, Integer> map, int minLegLength) {
		for (Integer key : map.keySet()) {
			System.out.println(map.get(key) + " tests had at least " + key + " pivots of min leg length " + minLegLength);
		}
	}
	
	public static void testPivots() {
		//String sub = Ciphers.cipher[0].cipher;
		String sub = "HER>pl^VPk|1LTG2dNp+B(#O%DWY.<*Kf)By:cM+UZGW()L#zHJSpp7^l8*V3pO++RK2_9M+ztjd|5FP+&4k/p8R^FlO-*dCkF>2D(#5+Kq%;2UcXGV.zL|(G2Jfj#O+_NYz+@L9d<M+b+ZR2FBcyA64K-zlUV+^J+Op7<FBy-U+R/5tE|DYBpbTMKO2<cORJ|*5T4M.+&BFz69Sy#+||5FBc(;8RlGFN^f5J4b.cV4t++yBX1*:49CE>VUZ5-+|c.3zBK(Op^.fMqG2RcT+L16C<+FlWB|)L++)WCzWcPOSHT/()p|FkdW<7tB_YOB*-Cc>MDHNpkSzZO8A|K;+";
		List<PivotOLD> list = pivots(sub, 3);
		for (PivotOLD p : list) if (p.longestLeg() > 0) System.out.println(p);
	}
	
	public static void main(String[] args) {
		//load();
		//System.out.println(line.length());
		//pivotSearch(3);
		//System.out.println(trigrams(Ciphers.cipher[1].cipher));
		testPivots();
		
	}
}
