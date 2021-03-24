package com.zodiackillerciphers.old;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.io.FileUtil;
import com.zodiackillerciphers.lucene.BestMatches;
import com.zodiackillerciphers.lucene.Match;
import com.zodiackillerciphers.lucene.Scorer;

public class Jurgen10s {
	/* test count of all jurgen's selections of 10 */ 
	public static void test10s() {
		int count = 0;
		for (int a=0; a<310; a++) {
			for (int b=a+10; b<320; b++) {
				for (int c=b+10; c<330; c++) {
					count++;
				}
			}
		}
		System.out.println(count);
		// result in wolfram alpha:  http://www.wolframalpha.com/input/?i=Sum%5BSum%5BSum%5B1%2C%7Bc%2C%28b%2B10%29%2C329%7D%5D%2C%7Bb%2C%28a%2B10%29%2C319%7D%5D%2C%7Ba%2C0%2C309%7D%5D
	}
	
	/** score each group of n symbols */ 
	public static void score(int which, int n, boolean forWiki) {
		HomophonesProblem.which = which;
		String cipher = Ciphers.cipher[HomophonesProblem.which].cipher;
		HomophonesProblem.symbolCounts = HomophonesProblem.countsMapFor(cipher);
		
		Map<String, Integer> ngramCounts = new HashMap<String, Integer>();
		for (int i=0; i<cipher.length(); i++) {
			String key;
			Integer val;
			String ngram;
			if (i<cipher.length()-1) { // 2-grams
				ngram = cipher.substring(i,i+2);
				val = ngramCounts.get(ngram);
				if (val == null) val = 0;
				val++;
				ngramCounts.put(ngram, val);
			}
			if (i<cipher.length()-2) { // 3-grams
				ngram = cipher.substring(i,i+3);
				val = ngramCounts.get(ngram);
				if (val == null) val = 0;
				val++;
				ngramCounts.put(ngram, val);
			}
			if (i<cipher.length()-3) { // 4-grams
				ngram = cipher.substring(i,i+4);
				val = ngramCounts.get(ngram);
				if (val == null) val = 0;
				val++;
				ngramCounts.put(ngram, val);
			}
		}
		
		// get best homophone scores per symbol
		char[] alphabet = Ciphers.alphabet(cipher).toCharArray();
		System.out.println("Cipher " + HomophonesProblem.which + ": " + cipher);
		System.out.println("Alphabet: " + Ciphers.alphabet(cipher));
		Map<Character, Double> best = new HashMap<Character, Double>();
		double[] d = Homophones.homophoneSearch(2, alphabet, cipher, true, true, false, false, false, HomophonesProblem.symbolCounts, best);
		
		Set<String> ngrams = new HashSet<String>(); // only ngrams that repeat
		for (String key : ngramCounts.keySet()) {
			if (ngramCounts.get(key) > 1) {
				System.out.println("ngram " + key + " count " + ngramCounts.get(key));
				ngrams.add(key);
			}
		}
		
		String sub;
		
		List<Row> list = new ArrayList<Row>();
		for (int i=0; i<cipher.length()-n+1; i++) {
			Row row = new Row();
			
			row.pos = i;
			row.sub = cipher.substring(i, i+n);
			
			row.counts = Homophones.counts(row.sub, HomophonesProblem.symbolCounts); // coverage
			int distincts = Homophones.distinct(row.sub); // # of distinct symbols
			row.reps = (n-distincts);
			
			row.n2 = ngramsIn(row.sub, ngrams, 2);
			row.n3 = ngramsIn(row.sub, ngrams, 3);
			row.n4 = ngramsIn(row.sub, ngrams, 4);

			row.nscore = row.n4 + ((float)row.n3)/2 + ((float)row.n2)/4;
			
			row.besthoms = besthoms(best, row.sub);
			
			row.combined = (1+row.nscore)*row.besthoms*(n-distincts+1)*row.counts;
			list.add(row);
		}
		
		Collections.sort(list);
		for (Row r : list) {
			if (forWiki) r.wikiPrint();
			else r.normalPrint();
		}
	}
	
	/** sum the best hom scores for distinct symbols in the given string */
	public static double besthoms(Map<Character, Double> best, String str) {
		Set<Character> seen = new HashSet<Character>();
		double sum = 0;
		for (int i=0; i<str.length(); i++) {
			char ch = str.charAt(i);
			if (seen.contains(ch)) continue;
			seen.add(ch);
			sum += best.get(ch);
			
		}
		return sum;
	}
	
	/** count number of times an ngram that is known to repeat occurs in the given string */
	public static int ngramsIn(String str, Set<String> ngrams, int n) {
		int total = 0;
		for (int i=0; i<str.length()-n+1; i++) {
			String ngram = str.substring(i,i+n);
			if (ngrams.contains(ngram)) total++;
		}
		return total;
	}
	
	/* test all assignments for By:cM+UZGW */
	public static void testLine() {
		/* 
			B - hret
			y - hrte
			: - etaoinvkxjqz
			c - etonrd
			M - etairh
			+ - eins
			U - hraiet
			Z - etnrh
			G - thearn
			W - pgwb
		 */
		/*
		String[] assignments = new String[] {
				"hret", // B
				"hrte", // y
				"etaoinvkxjqz", // :
				"etonrd", // c
				"etairh", // M
				"eins", // +
				"hraiet", // U
				"etnrh", // Z
				"thearn", // G
				"pgwb" // W
		};*/
		/*
			( - theainrsid
			) - gwybv
			L - zqjxkmuchra
			# - vbywgfmucdhate
			z - etaoinsrhldcumf
			H - kvbfmuc
			J - bvkfmucaoinsrhte
		*/
		String[] assignments = new String[] {
				"theainrsid", // (
				"gwybv", // )
				"zqjxkmuchra", // L
				"vbywgfmucdhate", // #
				"etaoinsrhldcumf", // z
				"kvbfmuc", // H
				"bvkfmucaoinsrhte" // J
		};
		
		int total = 1;
		for (int i=0; i<assignments.length; i++) total*=assignments[i].length();
		System.out.println(total + " combinations.");

		int[] indices = new int[assignments.length];
		int i = indices.length-1;
		
		boolean go = true;
		
		int count = 0;
		StringBuffer plain;
		float zk;
		BestMatches bm;
		while (go) {
			
			plain = new StringBuffer();
			for (int j=0; j<indices.length; j++) plain.append(assignments[j].charAt(indices[j]));
			zk = com.zodiackillerciphers.lucene.NGrams.zkscore(plain);
			bm = com.zodiackillerciphers.lucene.Scorer.score(plain, false);
			System.out.println("result	"+plain+"	"+zk+"	"+bm.sums[0]+"	"+bm.sums[1]+"	"+bm.coverage+"	"+bm.medianScore+"	"+((1+zk)*(1+bm.sums[0]))+"	"+words(bm));
			
			
			indices[i]++;
			count++;
			while (indices[i] == assignments[i].length()) {
				indices[i] = 0;
				i--;
				if (i<0) {
					go = false;
					break;
				}
				indices[i]++;
			}
			if (!go) break;
			
			i=indices.length-1;
			
		}
		System.out.println(count);
		
	}

	public static String words(BestMatches bm) {
		StringBuffer line = new StringBuffer();
		List<Match> matches = bm.matches;
		if (matches !=null) {
			for (Match m : matches) {
				line.append(m.word).append(" ");
			}
		}
		return line.toString();
		
	}
	public static void analyzeWords() {
		List<String> lines = FileUtil.loadFrom("/Users/doranchak/projects/work/java/zodiac/jurgen-10s-experiment-01-sorted4-top10000.txt");
		for (String s : lines) {
			String[] split = s.split("	");
			String phrase = split[1];
			BestMatches bm = Scorer.score(new StringBuffer(phrase), false);
			StringBuffer line = new StringBuffer(phrase+": ");
			line.append(words(bm));
			System.out.println(line);
		}
	}

	public static void main(String[] args) {
		//test10s();
		//score(10, 10, true);
		testLine();
		//analyzeWords();
	}
	
	private static class Row implements Comparable {
		public int pos;
		public String sub;
		public int reps;
		public int counts;
		public int n2;
		public int n3;
		public int n4;
		public float nscore;
		public double besthoms;
		public double combined;
		
		public String prefix = "| style=\"text-align: right; border-style: solid; border-width: 1px\"|";

		
		@Override
		public int compareTo(Object o) {
			Row r = (Row) o;
			return Double.compare(r.combined, combined);
		}
		
		public void wikiPrint() {
			System.out.println("|-valign=\"top\"");
			System.out.println(prefix+pos);
			System.out.println(prefix+"<tt>"+sub.replaceAll("&","&amp;").replaceAll("\\|","&#124;")+"</tt>");
			System.out.println(prefix+reps);
			System.out.println(prefix+counts);
			System.out.println(prefix+n2);
			System.out.println(prefix+n3);
			System.out.println(prefix+n4);
			System.out.println(prefix+nscore);
			System.out.println(prefix+new DecimalFormat("#.##E0").format(Double.valueOf(besthoms)));
			System.out.println(prefix+new DecimalFormat("#.##E0").format(Double.valueOf(combined)));
		}
		
		public void normalPrint() {
			System.out.println(pos+"	"+sub+"	"+reps+"	"+counts+"	"+n2+"	"+n3+"	"+n4+"	"+nscore+"	"+besthoms+"	"+combined);
		}
	}
}
