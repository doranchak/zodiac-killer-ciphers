package com.zodiackillerciphers.tests.unicity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.ciphers.algorithms.Vigenere;
import com.zodiackillerciphers.corpus.CorpusBase;
import com.zodiackillerciphers.lucene.Stats;

/** pick random plaintexts from a big corpus.  see if ciphertexts exist that can decode into two 
 * different plaintexts.
 * 
 * second attempt, new approach: maintain database of substitution plaintext candidates.
 * explore random vigenere ciphers and see if any are compatible with the entries in the database.
 */
public class SubVigCorpusScanner2 extends CorpusBase {

	public static Random rand = new Random();
	static {
		CorpusBase.SHOW_INFO = false;
	}
	
	/** map a hash of the "next matches" array for a plaintext, to the plaintext. */
	static Map<Integer, String> map;
	
	/** generate a database of substitution plaintext candidates, each with the given length, with a given max size */
	public static void makeDatabase(int length, int maxSize) {
		map = new HashMap<Integer, String>();
		SubstitutionMutualEvolve.initSources();
		while (true) {
			SubstitutionMutualEvolve.randomSource();
			List<List<String>> ngramsList = ngrams(length);
			for (List<String> ngrams : ngramsList) {
				StringBuffer ptSpaces = flatten(ngrams, true);
				StringBuffer ptNoSpaces = flatten(ngrams, false);
				int[] nextMatches = nextMatches(ptNoSpaces);
				Integer key = Arrays.hashCode(nextMatches);
				map.put(key, ptSpaces.toString());
				if (map.size() >= maxSize) return;
				if (map.size() % 10000 == 0) {
					System.out.println(map.size() + " " + Runtime.getRuntime().totalMemory());
				}
			}
		}
	}
	
	/**
	 * explore samples of vigenere plaintexts and random keys. see if any generated
	 * cipher texts are compatible with samples of substitution plaintexts.
	 */
	public static void search(int length, int vigenereKeyLength, int maxSubstitutionSamples,
			int trialsPerVigenereCorpusSample, int maxHits) {
		// make the substitution plaintext database, indexed by "next matching pair"
		// arrays.
		makeDatabase(length, maxSubstitutionSamples);
		// reshuffle the corpus
		SubstitutionMutualEvolve.initSources();

		long trials = 0;
		long hits = 0;
		while (true) {
			SubstitutionMutualEvolve.randomSource();
			List<List<String>> ngramsList = ngrams(length);
			if (ngramsList == null || ngramsList.isEmpty()) {
				continue;
			}
			long trialsCurrent = 0;
			while (true) {
				// get a random set of ngrams
				List<String> ngrams = ngramsList.get(rand.nextInt(ngramsList.size()));
				StringBuffer nospaces = flatten(ngrams);

				// generate a random vigenere key
				StringBuffer vigKey = randomKey(vigenereKeyLength);
				
				// encode the vigenere cipher
				String cipher = Vigenere.encrypt(nospaces.toString().toLowerCase(), vigKey.toString().toLowerCase());
				int hashCode = Arrays.hashCode(nextMatches(cipher));
				if (map.containsKey(hashCode)) {
					String ptSpaces = map.get(hashCode);
					String ptWithoutSpaces = ptSpaces.replaceAll(" ","");
					if (validSubstitution(cipher, new StringBuffer(ptWithoutSpaces))) {
						if (PlaintextBean.tooRepetitive(ptWithoutSpaces)) continue;
						if (PlaintextBean.tooRepetitive(nospaces.toString())) continue;
						if (PlaintextBean.tooSimilar(ptWithoutSpaces, nospaces.toString())) continue;
					
						hits++;
						String vigpt = ngrams.toString().replaceAll(",", "");
						System.out.println(hits + "	" + trials + "	" + vigpt+ "	" + vigKey + "	" + cipher.toUpperCase() + "	[" + ptSpaces + "]");
						if (hits == maxHits)
							return;
					}
				}
				
				
				trials++;
				trialsCurrent++;
				if (trialsCurrent == trialsPerVigenereCorpusSample) {
					break;
				}
			}
		}

	}
	
	public static StringBuffer randomKey(int vigenereKeyLength) {
		StringBuffer key = new StringBuffer();
		for (int i=0; i<vigenereKeyLength; i++) {
			key.append(alpha.charAt(rand.nextInt(alpha.length())));
		}
		return key;
	}

	public static void testNextMatches() {
		String s = "HUJJXYCZHLSZZDYSXSRN";
		int[] nm = nextMatches(s);
		System.out.println(s);
		System.out.println(Arrays.toString(nm));
		System.out.println(Arrays.hashCode(nm));
		System.out.println(nm.hashCode());
		s = "NEPLENDASNENIUSOPIRO";
		nm = nextMatches(s);
		System.out.println(s);
		System.out.println(Arrays.toString(nm));
		System.out.println(Arrays.hashCode(nm));
		System.out.println(nm.hashCode());
	}
	
	public static void testMakeDatabase() {
		makeDatabase(15, 10000000);
//		System.out.println(map);
	}
	public static void main(String[] args) {
//		testMakeDatabase();
		search(27, 5, 10000000, 20000, 1000);
//		testNextMatches();
	}
}
