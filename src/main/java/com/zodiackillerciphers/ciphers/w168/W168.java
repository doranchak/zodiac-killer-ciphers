package com.zodiackillerciphers.ciphers.w168;

import java.util.Arrays;
import java.util.TreeSet;

import com.zodiackillerciphers.ciphers.algorithms.ColumnarTransposition;
import com.zodiackillerciphers.ciphers.algorithms.RailFence;
import com.zodiackillerciphers.dictionary.InsertWordBreaks;
import com.zodiackillerciphers.dictionary.WordFrequencies;
import com.zodiackillerciphers.lucene.ZKDecrypto;
import com.zodiackillerciphers.tests.jarlve.Permutations;

/** W168 cipher from Jeanne */
public class W168 {
	public static String TAB = "	";
	public static String[] cipher = new String[] { 
		" R SE.NELMYESEDESEN A T LGTO", 
		"T LPA S MEHTI YATT NSOOG SES",
		"I TS RRT SUIOSIMOHSO VEP SOO", 
		"T  EEDERXDFIDJCE MSDUMD  . L", 
		"TEGNGIIFTN RMALOT SAPMOTLIGA",
		".NEPLL I TLAN ELHTT .EL  EF " 
	};
	
	public static String cipherLine = "";
	static {
		for (String line : cipher) cipherLine += line;
	}
	
	/** try out different railfence possibilities */
	public static void railFence() {
		for (int i=1; i<100; i++) {
			String pt = RailFence.decode(cipherLine, i);
			double zk = ZKDecrypto.calcscore(pt, "EN", true);
			System.out.println(zk + " " + i + " " + badSpaces(pt) + " " + averageWordLength(pt) + " " + pt);
		}
	}
	
	/** count number of times a space happens after another space.  too many like that implies a bad transposition. */
	public static int badSpaces(String str) {
		int count = 0;
		for (int i=1; i<str.length(); i++) 
			if (str.charAt(i) == ' ' && str.charAt(i-1) == ' ') count++;
		return count;
	}
	/** calculate average word length */
	public static float averageWordLength(String str) {
		String[] split = str.split("[ .]");
		int count = 0;
		int sum = 0;
		for (String s : split) {
			if (s.length() > 0) {
				count++;
				sum += s.length();
			}
		}
		float result = sum;
		result /= count;
		return result;
	}
	/** look for bugs in encode/decode procedure */
	public static void columnarVerify() {
		
		StringBuilder sb = new StringBuilder(
				"THIS IS A TEST OF THE COLUMNAR TRANSPOSITION IN JAVA SE 7 AND LATER, YOU CAN REPLACE THE TYPE ARGUMENTS REQUIRED TO INVOKE THE CONSTRUCTOR OF A GENERIC CLASS WITH AN EM");
		StateColumnarVerify state = new StateColumnarVerify(new int[] { 0, 1 }, null, sb);
		com.zodiackillerciphers.util.Permutations.recurse(state);
		state = new StateColumnarVerify(new int[] { 0, 1, 2 }, null, sb);
		com.zodiackillerciphers.util.Permutations.recurse(state);
		state = new StateColumnarVerify(new int[] { 0, 1, 2, 3}, null, sb);
		com.zodiackillerciphers.util.Permutations.recurse(state);
		state = new StateColumnarVerify(new int[] { 0, 1, 2, 3, 4 }, null, sb);
		com.zodiackillerciphers.util.Permutations.recurse(state);
	}

	/** brute force some columnar transposition keys */
	public static void columnarBrute1() {
		StringBuilder ct = new StringBuilder("   HLRNI AENTYAP TAERROOHNCOGIAINHSEF MTPINA7 RU AHPGTQDIE TO N SHESATTOAASNJSAA CEE  M ITVTOU  RLWATITOEU STIV DEONLTYRNEE KESTFECST I S CNROO   L, RCEEUSU N CRRAEC  M");
		for (int n=2; n<100; n++) {
			int[] elems = new int[n];
			for (int i=0; i<n; i++) {
				elems[i] = i;
			}
			System.out.println("====== KEY LENGTH " + n + ":");
			StateColumnarBrute state = new StateColumnarBrute(elems, ct, null, false);
			com.zodiackillerciphers.util.Permutations.recurse(state);
		}
		
	}
	public static void columnarBrute2(boolean encode) {
		StringBuilder ct = new StringBuilder(cipherLine);
		for (int n=2; n<100; n++) {
			int[] elems = new int[n];
			for (int i=0; i<n; i++) {
				elems[i] = i;
			}
			System.out.println("====== KEY LENGTH " + n + ":");
			StateColumnarBrute state = encode ? new StateColumnarBrute(elems, null, ct, encode) :
				new StateColumnarBrute(elems, ct, null, encode);
			com.zodiackillerciphers.util.Permutations.recurse(state);
		}
		
	}
	/** try all words in the dictionary */
	public static void dictionary() {
		WordFrequencies.init();
		//StringBuilder ct = new StringBuilder("NALCEHWTTDTTFSEELEEDSOAFEAHL");
		StringBuilder ct = new StringBuilder(cipherLine);
		
		/** use TreeSet to implement min/max heap of fixed number of elements. */
		TreeSet<Double> treeSet = new TreeSet<Double>();
		/** max size of heap */
		int maxHeapSize = 10;
		
		for (String word : WordFrequencies.map.keySet()) {
			int[] key = ColumnarTransposition.keyFor(word);
			StringBuilder pt = ColumnarTransposition.decode(ct, key, false);
			Double scorePt = StateColumnarBrute.ngramScore(pt, 4);
			boolean print = false;
			// if heap not full, just add it
			if (treeSet.size() < maxHeapSize) {
				treeSet.add(scorePt);
				print = true;
			} else {
				// is this score better than the worst score? 
				Double worst = treeSet.first();
				if (scorePt > worst && !treeSet.contains(scorePt)) { // it's better and not a dupe score
					print = true;
					treeSet.add(scorePt); // so add to heap
					treeSet.remove(worst); // and remove the worst score
				}
			}
			
			//if (print) System.out.println(scorePt + TAB + treeSet + TAB + Arrays.toString(elements) + TAB + pt);
			if (print) System.out.println(scorePt + TAB + word + TAB + Arrays.toString(key) + TAB + pt + TAB + treeSet);
}
	}
	
	public static void main(String[] args) {
		InsertWordBreaks.init("EN", false);
//		railFence();
		//System.out.println(averageWordLength(cipherLine));
//		columnarVerify();
		//columnarBrute1();
		columnarBrute2(true);
//		dictionary();
		
	}
}
