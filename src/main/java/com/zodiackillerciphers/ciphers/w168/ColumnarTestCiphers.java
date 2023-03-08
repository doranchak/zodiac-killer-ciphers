package com.zodiackillerciphers.ciphers.w168;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.zodiackillerciphers.ciphers.algorithms.columnar.ColumnarTransposition;
import com.zodiackillerciphers.ciphers.algorithms.columnar.Variant;
import com.zodiackillerciphers.corpus.CorpusBase;
import com.zodiackillerciphers.io.FileUtil;
import com.zodiackillerciphers.tests.unicity.SubstitutionMutualEvolve;

/** generate test ciphers for Jarl */
public class ColumnarTestCiphers extends CorpusBase {
	// A-Z, period, and space
	public static String alphabet1 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ. ";
	
	// curated collection of plaintexts produced by the generate() method
	public static String plaintextsFile = "/Users/doranchak/projects/ciphers/W168/jarl-plaintexts.txt";
	public static List<String> plaintexts;
	static {
		plaintexts = FileUtil.loadFrom(plaintextsFile);
	}
	
	// generate strings of a given length.  restrict to the given alphabet.
	public static void generate(int n, int length, String alphabet) {
		Random rand = new Random();
		CorpusBase.REDDIT_ONLY = true;
		CorpusBase.initSources();
		System.out.println("Done source init.");
		CorpusBase.SHOW_INFO = false;
		for (int i=0; i<n; i++) {
			boolean success = false;
			System.out.println(CorpusBase.file);
			SubstitutionMutualEvolve.randomSource();
			
			while (!success) {
				//System.out.println(CorpusBase.file);
//				System.out.println("SMEG BEFORE");
//				System.out.println(CorpusBase.contents);
				CorpusBase.contents = scrub(CorpusBase.contents, alphabet);
//				System.out.println("SMEG AFTER");
//				System.out.println(CorpusBase.contents);
				int startPos = rand.nextInt(CorpusBase.contents.length());
				startPos = nextStartOfSentence(CorpusBase.contents, startPos);
				if (startPos > -1) { // found start of a candidate sentence
					if (startPos+length-1 < CorpusBase.contents.length()) { // there's enough remaining string left
						
						// we only want to keep this if we haven't split up a word at the end.
						int nextPos = startPos+length;
						if (nextPos < CorpusBase.contents.length() && Character.isAlphabetic(CorpusBase.contents.charAt(nextPos))) {
							continue;
						}
						System.out.println(CorpusBase.contents.substring(startPos, startPos+length));
						success = true;
					}
				}
			}
		}
	}
	
	/** try to find the next start position of a sentence.  or -1 if not found. */
	public static int nextStartOfSentence(String str, int pos) {
		// case: at start of string.  return position of first alphabetic character.
		if (pos == 0) {
			return nextAlphabetic(str, pos);
		}
		// case: we're somewhere in the string.  find the next period then return position of first alphabet character.
		return nextAlphabetic(str, nextPeriod(str, pos));
	}
	
	// find position of next alphabetic character.  or -1 if not found. 
	public static int nextAlphabetic(String str, int pos) {
		if (pos < 0) return -1;
		for (int i=pos; i<str.length(); i++) {
			char ch = str.charAt(i);
			if (Character.isAlphabetic(ch)) return i;
		}
		return -1;
	}
	// find position of next period.  or -1 if not found. 
	public static int nextPeriod(String str, int pos) {
		if (pos < 0) return -1;
		for (int i=pos; i<str.length(); i++) {
			if (str.charAt(i) == '.') return i;
		}
		return -1;
	}
	
	public static String scrub(String str, String alphabet) {
		str = str.toUpperCase();
		str = str.replaceAll("\\r", " ");
		str = str.replaceAll("\\n", " ");
		str = str.replaceAll(" +", " ");
		Set<Character> set = new HashSet<Character>();
		for (int i=0; i<alphabet.length(); i++) set.add(alphabet.charAt(i));
		StringBuffer sb = new StringBuffer();
		for (int i=0; i<str.length(); i++) {
			char ch = str.charAt(i);
			if (set.contains(ch)) {
				sb.append(ch);
			}
			if (ch == 10 || ch == 13) sb.append(' '); // replace newlines with space
		}
		return sb.toString();
		
	} 
	
	public static void test() {
		int[] key = ColumnarTransposition.randomKeyWithLength(5);
		System.out.println(Arrays.toString(key));
		ColumnarTransposition.testEncodeDecode(Variant.TOP_TO_BOTTOM, new StringBuilder(plaintexts.get(0)), key);
		ColumnarTransposition.testEncodeDecode(Variant.TOP_TO_BOTTOM, new StringBuilder(plaintexts.get(0)), new int[] {0,1,2,3,4});
	}
	
	public static void randomCipherForJarl() {
		Random rand = new Random();
		StringBuilder plaintext = new StringBuilder(plaintexts.get(rand.nextInt(plaintexts.size())));
		Variant variant = ColumnarTransposition.variants[rand.nextInt(ColumnarTransposition.variants.length)];
		int width = rand.nextInt(41) + 2; // [2, 42]
		int[] key = ColumnarTransposition.randomKeyWithLength(width);
		ColumnarTransposition.testEncodeDecode(variant, plaintext, key);
	}
	
	public static void main(String[] args) {
		//generate(100, 168, alphabet1);
		//test();
		for (int i=0; i<10000; i++) randomCipherForJarl();
	}

}
