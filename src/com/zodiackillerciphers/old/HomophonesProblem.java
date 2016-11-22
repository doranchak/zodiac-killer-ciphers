
/* Trying to maximize homophone candidate counts */ 
package com.zodiackillerciphers.old;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.ngrams.NGrams;

import ec.util.*;
import ec.*;
import ec.multiobjective.MultiObjectiveFitness;
import ec.simple.*;
import ec.vector.*;

public class HomophonesProblem extends Problem implements SimpleProblemForm {

	//static int which = 4;
	static int which = 0;
	static int H = which == 0 ? 20 : 24;
	static int W = 17;
	
	/** min number of strings (at start of set of candidate strings (reduceStrings3) or within all strings (reduceString4)) in which a symbol must appear, before the symbol is culled from consideration. 
	 * This is part of the string reduction of remove_homophones.  */ 
	public static int F = 4;
	/** min ratio of appearances of a symbol to # of strings.  if this min isn't met, then symbol is removed. */
	public static float R = 0.8f;
	
	/** if true, punish heuristic scores for homophone candidates that contain symbols that have already been seen in higher-scoring combinations. */
	public static boolean SEEN_FACTOR = true;
	
	/** do not search if combinations exceed this number */
	//public static int LIMIT = 0;
	
	//static String alphabet = Zodiac.alphabet[which == 3 ? 1 : which];
	//static int len = Ciphers.cipher[which].length();
	
	static String[] lines; 
	static {
		lines = new String[H];
		for (int i=0; i<Ciphers.cipher[which].cipher.length(); i++) {
			int row = i/W;
			if (i % W == 0) lines[row] = "";
			lines[row] += Ciphers.cipher[which].cipher.charAt(i);
		}
	}
	
	static String[] alphabet;
	static {
		alphabet = Homophones.alphabetFrom(Ciphers.cipher[which].cipher);
	}
	
	public static Map<Character, Integer> symbolCounts;
	/*static {
		System.out.println("Counting");
		symbolCounts = countsMapFor(Ciphers.cipher[which]);
		System.out.println("total " + symbolCounts.size());
	}*/
	
	public static Map<Character, Integer> countsMapFor(String ciphertext) {
		Map<Character, Integer> map = new HashMap<Character, Integer>();
		for (int i=0; i<ciphertext.length(); i++) {
			char ch = ciphertext.charAt(i);
			Integer val = map.get(ch);
			if (val == null) val = 0;
			val++;
			map.put(ch, val);
		}
		return map;
	}

	
	public void evaluate(final EvolutionState state, final Individual ind,
			final int subpopulation, final int threadnum) {
		if (!(ind instanceof HomophonesIndividual))
			state.output
					.fatal("The individuals for this problem should be HomophonesIndividual.");

		HomophonesIndividual temp = (HomophonesIndividual) ind;
		int[] genome = temp.genome;
		/*if (genome.length != len)
			state.output
					.fatal("Expected genome length of " + len + " but found " + genome.length);*/

		// construct new cipher by converting genome into series of swaps
		String newcipher = toCipher(Ciphers.cipher[which].cipher, genome);
		
		double[] objectives = ((MultiObjectiveFitness) ind.fitness)
				.getObjectives();

		if (newcipher == null) throw new RuntimeException("cipher can't be null");
		
		/*
		int[] hitMiss = new int[] {0,0}; 
		int[] highRanks = new int[40];
		Map<Integer, Integer> scores = Homophones.longestRuns(newcipher, hitMiss, highRanks);
		
		objectives[0] = 200; objectives[1] = 0;
		for (int i=0; i<highRanks.length; i++) {
			if (i<9) {
				if (objectives[0] > 0) objectives[0] -= highRanks[i];
			} else objectives[1] += highRanks[i];
		}
		*/
		// minimize the number of 0-counts
		//if (scores.get(0) == null) objectives[0] = 1;
		//else objectives[0] = (float)1/(scores.get(0)+1);

		// maximize total sum for all h>0, skewed towards higher lengths
 		/*float total = 0; int max = 17; // picked 17 because that is max length for the 408
		for (Integer key : scores.keySet()) {
			if (key > 0) {
				int d = Math.max(max-key,0); 
				total += ((float)scores.get(key)/Math.pow(d+1,3));
			}
		}
		objectives[2] = total;
		*/
		//objectives[0] = hitMiss[0];
		// hit-miss ratio
		//objectives[1] = ((float)hitMiss[0]) / hitMiss[1];
		//objectives[1] = Homophones.weightedAvg(scores);
		
		
		//objectives[2] = count3Grams(newcipher);
		/*Integer count = scores.get(10);
		if (count == null) objectives[0] = 0;
		else objectives[0] = count;
		
		count = scores.get(12);
		if (count == null) objectives[1] = 0;
		else objectives[1] = count;*/
		
		// second objective: count of distinct repeating 3-grams
		//objectives[1] = count3Grams(newcipher);
		
		
		
		
		
		/*
		List<Object[]> h = Homophones.homophoneSearch(3, alphabet, newcipher);
		Map<Integer, Integer> score = scoreHomophones2(h);
		objectives[0] = 0;
		objectives[1] = 0; 
		
		for (Integer key : score.keySet()) {
			if (key == 5 || key == 6) objectives[0]+=score.get(key);
			else if (key > 6) objectives[1]+=score.get(key);
		}*/
		
		
		/*float[] score = HomophonesKingBahler.evaluateCipher(newcipher);
		objectives[0] = score[0];
		objectives[1] = score[1];
		objectives[2] = score[2];
		objectives[3] = score[3];
		objectives[4] = count3Grams(newcipher);*/
		
		// new and improved
		
		float[][] score = HomophonesKingBahler.evaluateCipher2(newcipher);
		int struct = structure(genome);
		objectives[0] = score[2][0];
		objectives[1] = struct;
		temp.scoreReal = new float[] {score[2][1]};
		
		int nonZero = 0;
		for (int i=0; i<score.length; i++) if (score[i][0]>0) nonZero++;
		objectives[2] = nonZero; 
		
		/*
		int matches = 0;
		for (int i=0; i<newcipher.length(); i++) {
			if (newcipher.charAt(i) == Ciphers.cipher[1].charAt(i)) matches++;
		}
		objectives[0]= matches;
		*/
		((MultiObjectiveFitness) ind.fitness).setObjectives(state, objectives);
		ind.evaluated = true;

	}
	
	static String swap(String c, int i, int j) {
		
		i %= (H*W); j %= (H*W);
		
		StringBuffer sb = new StringBuffer(c);
		
		char ch1 = sb.charAt(i);
		char ch2 = sb.charAt(j);
		
		sb.replace(i, i+1, ""+ch2);
		sb.replace(j, j+1, ""+ch1);
		
		return sb.toString();
		
	}
	
	// return count of unique symbols found among all repeated 3-grams
	static int count3Grams(String c) {
		int[] totalsOriginal = new int[3];
		NGrams.countNgrams(c, 3, totalsOriginal);
		return totalsOriginal[2];
		
	}

	
	static String toCipher(String originalCipher, int[] genome) {
		StringBuffer newcipher = new StringBuffer();
		int row;
		for (int i=0; i<genome.length; i++) {
			row = genome[i];
			if (row < H) newcipher.append(lines[row]);
			else {
				row %= H;
				newcipher.append(new StringBuffer(lines[row]).reverse());
			}
			
		}
		return newcipher.toString();
	}
	
	static String toCipherOLD(String originalCipher, int[] genome) {
		String newcipher = originalCipher;
		for (int i=0; i<genome.length; i+=2) {
			if (genome[i] > 0 && genome[i+1] > 0) {
				newcipher = swap(newcipher, genome[i], genome[i+1]);
			}
		}
		return newcipher;
	}
	
	static int[] makeRandomGenome() {
		int[] genome = new int[H];
		
		List<Integer> list = new ArrayList<Integer>(); 
		for (int i=0; i<H; i++) {
			if (Math.random() < 0.5) list.add(i); else list.add(i+H);
		}

		int i=0;
		while (!list.isEmpty()) {
			int j = (int) (Math.random()*list.size());
			genome[i] = list.get(j);
			list.remove(j);
			i++;
		}
		return genome;
	}
	
	static void testMakeRandomGenome() {
		int[] gen = makeRandomGenome();
		String s = "";
		for (int i=0; i<gen.length; i++) 
			s += gen[i] + (i == gen.length-1 ? "" : ", ");
		System.out.println(s);
	}
	
	// element 0: total number of homophones scoring longest run of 5 or higher
	// element 1: average ratio of longest run to sequence length
	static float[] scoreHomophones(List<Object[]> homophones) {
		float[] result = new float[2];
		for (Object[] o : homophones) {
			Integer c = (Integer)o[1];
			if ((Integer)o[1] > 4) {
				Integer len = (Integer)o[0];
				int n = ((String) o[2]).length();
				result[0]++;
				result[1]+=(float)n*c/len;
			}
// each returned row is an array: [sequence length, longest run count, subsequence in longest run, sequence]    
		}
		if (result[0] == 0) result[1] = 0;
		else result[1]/=result[0];
		return result;
	}
	
	// maps longest run counts to number of occurrences
	// element 1: average ratio of longest run to sequence length
	static Map<Integer, Integer> scoreHomophones2(List<Object[]> homophones) {
		Map<Integer, Integer> result = new HashMap<Integer, Integer>();
		for (Object[] o : homophones) {
			Integer c = (Integer)o[1];
			Integer val = result.get(c);
			if (val == null) val = 1;
			else val ++;
			result.put(c, val);
		}
		return result;
	}
	
	
	// return "structure" count; i.e., number of sequences found.
	// 2nd returned element is number of structures.
	public static int structure(int[] genome) {
		boolean debug = false;
		
		int dstart = 1; int dend = 4;
		boolean found = false;
		int num = 0;
		String sequence;
		List<Structure> structures = new ArrayList<Structure>();
		for (int d=dstart; d<=dend; d++) {
			for (int i=0; i<d; i++) {
				found = false;
				sequence = "";
				int diff1 = -1; // previous difference
				int diff2 = -1;
				List<Integer[]> diffs = new ArrayList<Integer[]>(genome.length); // list of [diff, first genome position, 2nd genome position]
				
				for (int j=i; j<genome.length; j+=d) {
					if (j-d >= 0) {
						diffs.add(new Integer[] {Math.abs(genome[j]-genome[j-d]), j-d, j});
					}
					
				}
				
				boolean hit = false; Structure s = null;
				for (int k=1; k<diffs.size(); k++) {
					if (diffs.get(k)[0] == diffs.get(k-1)[0]) {
						if (!hit) {
							sequence += "New sequence. [" + diffs.get(k-1)[1] + "," + diffs.get(k-1)[2] + "]: " + diffs.get(k-1)[0] + " ";
							s = new Structure(diffs.get(k)[0]);
							s.add(diffs.get(k-1)[1]);
							s.add(diffs.get(k-1)[2]);
						}
						hit = true;
						sequence += "[" + diffs.get(k)[1] + "," + diffs.get(k)[2] + "]: " + diffs.get(k)[0] + " ";
						s.add(diffs.get(k)[2]);
						if (debug) System.out.println("d " + d + " i " + i + " hit for " + k + ": " + diffs.get(k)[0] + ", pos1 " + diffs.get(k)[1] + ", pos2 " + diffs.get(k)[2]);
					} else {
						if (hit) { // avoid the problem of counting overlaps
							sequence += "Sequence ended. ";
							structures.add(s);
							s = null;
							hit = false;
							k++;
						}
					}
				}
				if (hit) {
					sequence += "Sequence ended. ";
					structures.add(s);
					s = null;
				}
				if (debug) System.out.println(sequence);
			}
		}

		Set<Integer> covered = new HashSet<Integer>();
		if (debug) {
			System.out.println("Before:");
			for (Structure s : structures) {
				System.out.println(s.toString());
			}
		}
		for (int i=0; i<structures.size(); i++) {
			Structure s1 = structures.get(i);
			for (int j=0; j<i; j++) {
				Structure s2 = structures.get(j);
				if (s1 == null || s2 == null) continue;
				if (s1.subsetOf(s2)) {
					structures.set(i, null);
					s1 = null;
				}
			}
			if (s1 != null) {
				for (Integer pos : s1.positions) covered.add(pos);
			}
		}
		
		if (debug) {
			System.out.println("After:");
			for (Structure s : structures) {
				if (s == null) System.out.println("null");
				else System.out.println(s.toString());
			}
		}
		//return new int[] {Math.min(count, genome.length), num};
		return covered.size();
	}
	
	public static String genomeToString(int[] genome) {
		String s = "";
		for (int i=0; i<genome.length; i++) {
			s += genome[i];
			if (i < genome.length-1) s += ", ";
		}
		return s;
	}
	
	/** use given homophone set to extract homophone sequence from cipher text. */
	public static String[] extractSequence(String cipher, String homophones) {
		List<Character> list = new ArrayList<Character>();
		for (int i=0; i<homophones.length(); i++) list.add(homophones.charAt(i));
		
		String[] result = new String[] {"",""};
		
		for (int i=0; i<cipher.length(); i++) {
			char ch = cipher.charAt(i);
			if (list.contains(ch)) {
				result[0] += ch;
				for (int j=0; j<list.size(); j++) {
					if (list.get(j) == ch) {
						result[1] += j; 
					}
				}
			}
		}
		return result;
	}
	
	public int matches(int[] genome) {
		int[] solution = {0,30,1,31,2,32,3,33,4,34,5,35,17,47,16,46,15,45,14,44,13,43,12,42};
		int m = 0;
		for (int i=0; i<solution.length; i++)
			if (solution[i] == genome[i]) m++;
		return m;
		
	}
	
	public static void testCiphers() {
		testCipher(Ciphers.cipher[0].cipher);
		//testCipher(Ciphers.cipher[1]);
		
		// first 9 lines only (see http://www.zodiackillerfacts.com/forum/viewtopic.php?p=17281#p17281)
		//testCipher("HER>pl^VPk|1LTG2dNp+B(#O%DWY.<*Kf)By:cM+UZGW()L#zHJSpp7^l8*V3pO++RK2_9M+ztjd|5FP+&4k/p8R^FlO-*dCkF>2D(#5+Kq%;2UcXGV.zL|(G2Jfj#O+_NYz+@L9d<M+b+ZR2FBcyA64K");
		
		// first 10 lines only
		//testCipher("HER>pl^VPk|1LTG2dNp+B(#O%DWY.<*Kf)By:cM+UZGW()L#zHJSpp7^l8*V3pO++RK2_9M+ztjd|5FP+&4k/p8R^FlO-*dCkF>2D(#5+Kq%;2UcXGV.zL|(G2Jfj#O+_NYz+@L9d<M+b+ZR2FBcyA64K-zlUV+^J+Op7<FBy-");
		
		// last 10 lines only
		//testCipher("U+R/5tE|DYBpbTMKO2<clRJ|*5T4M.+&BFz69Sy#+N|5FBc(;8RlGFN^f524b.cV4t++yBX1*:49CE>VUZ5-+|c.3zBK(Op^.fMqG2RcT+L16C<+FlWB|)L++)WCzWcPOSHT/()p|FkdW<7tB_YOB*-Cc>MDHNpkSzZO8A|K;+");
		
		// last 10 lines only, flipped horizontally
		//testCipher("OKMTbpBYD|Et5/R+UFB&+.M4T5*|JRlc<2R8;(cBF5|N+#yS96z++t4Vc.b425f^NFGl+-5ZUV>EC94:*1XBy2GqMf.^pO(KBz3.c|L)|BWlF+<C61L+TcRp)(/THSOPcWzCW)++cC-*BOY_Bt7<WdkF|+;K|A8OZzSkpNHDM>");
		
		/*testCiphers(new int[][] {
				{1,0,28,23,2,25,6,27,10,9,12,4,14,13,16,19,38,15,37,11},
				{0,28,6,25,2,11,7,3,10,12,38,14,36,13,19,21,4,15,29,37}
//				{0,2,4,6,8,10,25,27,29,31,33,35,22,20,18,16,14,12,47,45,43,41,39,37}, // correct permutation
//				{2,4,6,8,10,27,29,31,33,35,22,20,18,40,14,36,15,41,45,1,37,23,0,19}
		
		});*/
		
	}
	
	public static void testCiphers(int[][] ciphers) {
		for (int i=0; i<ciphers.length; i++) {
			int[] cipher = ciphers[i];
			testCipher(cipher);
		}
		
	}
	
	public static void testCipher(int[] cipher) {
		String s = "Testing permutation [";
		for (int i=0; i<cipher.length; i++) {
			s += cipher[i] + " ";
		}
		s += "]";
		System.out.println(s);
		String c = toCipher(Ciphers.cipher[0].cipher, cipher );
		testCipher(c);
		int struct = structure(cipher);
		System.out.println("Structure: " + struct);
		
		
	}
	
	public static void testCipher(String c) {
		//c=Ciphers.cipher[1];
		System.out.println("Cipher text: " + c);
		int[] hitMiss = new int[] {0,0}; 
		int[] highestRanks = new int[40];
		Map<Integer, Integer> scores = Homophones.longestRuns(c, hitMiss, highestRanks);
		Homophones.dump(scores, hitMiss, highestRanks); 
		System.out.println("weighted average: " + Homophones.weightedAvg(scores));
		System.out.println("distinct symbols in 3grams: " + count3Grams(c));

		
		// compute ngram counts
		int[] totals = new int[3];
		int[] sums = new int[3];
		
		for (int n=2; n<6; n++) { 
			NGrams.countNgrams(c, n, totals);
			System.out.println(n+"-grams all: " + totals[0]);
			System.out.println(n+"-grams distinct: " + totals[1]);
			System.out.println(n+"-grams symbols: " + totals[2]);
			sums[0]+=totals[0];
			sums[1]+=totals[1];
			sums[2]+=totals[2];
		} // 95
		System.out.println("n-grams all sum: " + sums[0]);
		System.out.println("n-grams distinct sum: " + sums[1]);
		System.out.println("n-grams symbols sum: " + sums[2]);
	
		
		/*
		List<Object[]> h = Homophones.homophoneSearch(3, Homophones.alphabetFrom(c), c);
		float[] score = scoreHomophones(h);
		System.out.println("homophone score: " + score[0] + ", " + score[1]);
		Map<Integer, Integer> score2 = scoreHomophones2(h);
		for (Integer i : score2.keySet()) System.out.println(i + ": " + score2.get(i));
		*/
		
		HomophonesKingBahler.testFindStrings(c);
	}
	
	public static void testStructure() {
		int[][] genomes = {
		{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23},
		{0,2,4,6,8,10,25,27,29,31,33,35,22,20,18,16,14,12,47,45,43,41,39,37}, // correct permutation
		{6,8,10,27,29,31,33,35,22,20,18,16,45,12,25,38,41,39,43,47,37,0,2,4}
};
		
		for (int i=0; i<genomes.length; i++) {
			int s = structure(genomes[i]);
			System.out.println("Structure score: " + s);
		}
		
	}
	
	public static void testExtract() {
		String[] s = extractSequence(Ciphers.cipher[0].cipher, "^*KM");
		System.out.println(s[0]+", " + s[1]);
		
	}
	
	public static void main(String[] args) {
		//testCiphers();
		//testExtract();
		//testStructure();
		testCipher(Ciphers.cipher[which].cipher);
		//Quadrants.testResult(8, 3, 1, 0, 90, 0, 0, 0, 0, 0, 0, 0, 0, 4);
		//Quadrants.testResult(13,1,0,0,0,180,0,180,0,0,0,1,0);
		//QuadrantsProcess.processResultsFile("/Users/doranchak/projects/work/java/zodiac/quad3-results-0-0.txt");
	}

}
