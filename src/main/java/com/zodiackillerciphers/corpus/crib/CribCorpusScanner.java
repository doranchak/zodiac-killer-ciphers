package com.zodiackillerciphers.corpus.crib;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.corpus.CorpusBase;
import com.zodiackillerciphers.dictionary.WordFrequencies;
import com.zodiackillerciphers.io.FileUtil;
import com.zodiackillerciphers.lucene.NGrams;
import com.zodiackillerciphers.lucene.Stats;
import com.zodiackillerciphers.lucene.ZKDecrypto;
import com.zodiackillerciphers.tests.unicity.PlaintextBean;

/** grab random samples of word ngrams from a corpus.
 * see if they fit as cribs in the given cipher.
 * try to output only the results that are significant.
 * i.e., cipher section that matches should have a 
 */
public class CribCorpusScanner extends CorpusBase {

	public static int SAMPLE_TRIALS = 100000;
	
	/** generate ioc stats for all possible substrings of the given cipher text */
	public static void dumpSubstringIocStats(String cipher) {
		for (int i=0; i<cipher.length()-19; i++) {
			for (int j=i+20; j<cipher.length(); j++) {
				String sub = cipher.substring(i, j);
				int len = sub.length();
				double ioc = Stats.ioc(sub);
				double weighted = ioc/(len*len);
				System.out.println(weighted + "	" + ioc + "	" + len + "	" + sub); 
			}
		}
	}
	
	/**
	 * find cribs that fit somewhere in the given ciphers.
	 * 
	 * we want to only track significant results. so, we enforce that a crib has to
	 * have a minimum length. furthermore, the cipher substring that matches the
	 * crib should have sufficient repetition of symbols to suggest that the match
	 * is significant or interesting. but there are numerous possible matches. so,
	 * for each crib length, we track the top N values of IoC for matched cipher
	 * substrings. some matches will be rejected because the corresponding cipher
	 * substring IoC will be too low (i.e., not enough repetitions in the cipher
	 * substring). over time, the top N IoC values will increase. eventually, we
	 * will only be outputting "interesting" matches of plaintext to cipher
	 * substrings.
	 * 
	 * but how to determine N?  too high and it may cover all substrings for the 
	 * given length.  too low and we may miss interesting hits.  
	 * 
	 * another approach:  for each cipher substring length, track the rate of matches for each IoC.
	 * after sufficient samples, remove from consideration any IoC that results in too many matches.
	 * set some minimum hit rate, like 1 in 1000. 
	 * 
	 */
	public static void search(String[] ciphers, CipherSubstrings[] cipherSubstrings, int minLength, boolean samplePhase) {
		if (!samplePhase)
			for (CipherSubstrings cs : cipherSubstrings)
				cs.resetCounts();
		initSources();
		REDDIT_ONLY = false;
		SHOW_INFO = false;
		WordFrequencies.init();
		// pick a random corpus
		
		// for all word ngrams with total length greater than minimum:
		// - find all available cipher substrings
		// - for each one:
		//   - if the plaintext fits the cipher substring:
		//     - output the cipher substring info and scored plaintext
		//     - increment with hit
		//   - otherwise, increment without hit
		//   - if the rate is too high for this ioc, mark this ioc as ignore, and remove this cipher substring from further consideration

		// output hits.  header:
		// length, plaintext score, plaintext, which cipher, position, ciphertext, plaintext source
		
		State state = new State();
		
//		for (CipherSubstrings cs : cipherSubstrings)
//			CipherSubstrings.dump(cs);
		
		
		while (true) {
			// pick random corpus
			randomSource();
			
			List<String> ngrams = new ArrayList<String>();
			StringBuffer flattened = new StringBuffer();

			long trials = 0;
			
			for (int a=0; a<tokens.length-1; a++) {
				ngrams.clear();
				flattened = new StringBuffer(); 
				for (int b=a+1; b<tokens.length; b++) {
					ngrams.add(tokens[b]);
					flattened.append(tokens[b]);
					int length = flattened.length();
					if (length >= minLength) {
					
						if (PlaintextBean.badAverageTokenLength(ngrams)) break;
						if (PlaintextBean.tooRepetitive(flattened.toString())) break;
						if (PlaintextBean.badIoc(flattened.toString())) break;
						
						/** get all available cipher substrings with the same length as the candidate plaintext, and look for fits.
						 *  if no fits are found, then advance the starting token. 
						 **/
						boolean found = false;
						
						for (int i=0; i<cipherSubstrings.length; i++) {
							CipherSubstringMap csm = cipherSubstrings[i].map.get(length);
							if (csm == null) continue;
							
							for (Integer pos  :csm.map.keySet()) {
								CipherSubstring cs = csm.map.get(pos);
								String cipherSubstring = cs.substring;
								if (Ciphers.decoderMapFor(null, cipherSubstring, flattened.toString()) != null) {
									found = true;
									cs.increment(true);
									if (!samplePhase)
										System.out
												.println(cs.rate() + "	" + length + "	" + i + "	" + cipherSubstring
														+ "	" + pos + "	" + ngrams + "	" + cs.ioc + "	" + file);
								} else { 
									cs.increment(false);
								}
							}
							
						}
//						System.out.println(trials + " " + length + " " + found);
						trials++;
						if (samplePhase && trials == SAMPLE_TRIALS) { 
							for (CipherSubstrings cs : cipherSubstrings) {
//								CipherSubstrings.dump(cs);
								cs.cull();
							}
							return;
						}
						if (!found) 
							break; // we didn't find any matches so no need to extend the current token stream.  advance the starting token instead.
//						System.out.println(ngrams);
//						ngrams.clear();
//						flattened = new StringBuffer();
//						break;
					}
				}
			}	
			
//			while (true) {
//				String token = tokens[tokenIndex++];
//				ngrams.add(token);
//				flattened.append(token);
//				System.out.println(flattened.length() + ": " + ngrams + ", " + flattened);
//				if (tokenIndex == tokens.length) break; // reached end of tokens
//				if (flattened.length() >= minLength) {
//					tokenIndex--;
//					flattened.delete(0, ngrams.get(0).length());
//					System.out.println("after deleting from beginning: " + flattened + ".  " + (flattened.length()-token.length()) + ", " + flattened.length());
//					flattened.delete(flattened.length()-token.length(), flattened.length());
//					System.out.println("after deleting from end: " + flattened);
//					ngrams.remove(0);
//					ngrams.remove(ngrams.size()-1);
//					System.out.println(" - tokens: " + ngrams);
//				}
//			}
		}
	}
	
	
	public static void testDumpSubstringIocStats() {
		dumpSubstringIocStats(Ciphers.Z408);
	}
	
	public static void search() {
		String[] ciphers = new String[] { Ciphers.Z408, Ciphers.Z340, Ciphers.Z13 }; 
		CipherSubstrings[] cipherSubstrings = new CipherSubstrings[ciphers.length]; 
		for (int i=0; i<ciphers.length; i++) cipherSubstrings[i] = new CipherSubstrings(ciphers[i], 13);

		// first, generate stats so we can cull overly matching cipher substrings 
		search(ciphers, cipherSubstrings, 13, true);
		// generate results based on the remaining cipher substrings
		currentRandomSelection = 0; // start over at the begining of the list of random corpora 
		search(ciphers, cipherSubstrings, 13, false);
	}
	
	public static void process(String file) {
		boolean measureCorrectness = false;
		boolean dumpScores = true;
		BufferedReader input = null;
		int counter = 0;
		try {
//			input = new BufferedReader(new FileReader(new File(path)));
			input = new BufferedReader(
			           new InputStreamReader(new FileInputStream(file), "UTF-8"));
			
			String line = null; // not declared within while loop
			while ((line = input.readLine()) != null) {
				if (line.startsWith("culled")) continue;
				String[] split = line.split("	");
				if (split.length != 8) continue;
				double rate = Double.valueOf(split[0]);
				int length = Integer.valueOf(split[1]);
				int which = Integer.valueOf(split[2]);
				String cipher = split[3];
				int pos = Integer.valueOf(split[4]);
				String ngrams = split[5];
				String plaintext = ngrams.replaceAll("\\[", "");
				plaintext = plaintext.replaceAll("\\]", "");
				plaintext = plaintext.replaceAll(" ", "");
				plaintext = plaintext.replaceAll(",", "");
				double ioc = Double.valueOf(split[6]);
				String source = split[7];
				if (measureCorrectness && which == 0) {
					float correct = Ciphers.countCorrectMappings(Ciphers.decoderMap, cipher, plaintext);
					correct /= plaintext.length();
					if (correct >= 0.5)
						System.out.println(correct + "	" + line + " " + plaintext + " "
								+ Ciphers.Z408_SOLUTION.substring(pos, pos + plaintext.length()));
				}
				if (dumpScores) {
					double score = length;
					score /= rate;
					score *= ioc;
					System.out.println(score + "	" + line);
				}
				
			}
			//System.out.println("read " + counter + " lines.");
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} 
		
		try {
			input.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	/** get pool of top 100,000 cribs found for Z340.
	 * maintain a forest of candidate keys (each key is initially empty)
	 * for each candidate key, pick a random crib
	 * if the crib's key is compatible with the forest's key, merge the keys and report coverage and plaintext 
	 */
	public static void findCompatibleCribs(String path, int forestSize) {
		
		Map<Character, Integer> counts = Ciphers.countMap(Ciphers.Z340);
		Random rand = new Random();
		Map<Integer, String> keyDetails = new HashMap<Integer, String>(); 
		
		// init forest with empty keys
		List<Map<Character, Character>> forest = new ArrayList<Map<Character, Character>>();
		for (int i=0; i<forestSize; i++) forest.add(new HashMap<Character, Character>());

		// pool of candidate partial keys
		List<Map<Character, Character>> pool = new ArrayList<Map<Character, Character>>();
		List<String> lines = FileUtil.loadFrom(path);
		for (String line : lines) {
			String[] split = line.split("	");
			String cipher = split[4];
			String plaintext = split[6];
			plaintext = plaintext.replaceAll("\\[", "");
			plaintext = plaintext.replaceAll("\\]", "");
			plaintext = plaintext.replaceAll(" ", "");
			plaintext = plaintext.replaceAll(",", "");
//			System.out.println(cipher);
//			System.out.println(plaintext);
			pool.add(Ciphers.decoderMapFor(cipher, plaintext));
		}
		
		// main loop
		while (true) {
			for (int i=0; i<forest.size(); i++) {
				Map<Character, Character> key = forest.get(i);
				int which = rand.nextInt(pool.size());
				Map<Character, Character> candidate = pool.get(which);
				Map<Character, Character> merged = Ciphers.mergeKeys(key, candidate);
				if (merged != null) { // hit
					forest.set(i, merged);
					double coverage = Ciphers.coverage(Ciphers.Z340, merged, counts);
					String pt = Ciphers.decode(Ciphers.Z340, merged);

					String details = "";
					if (key.size() != merged.size()) {
						details = keyDetails.get(i);
						if (details == null) details = i+":";
						if (!details.endsWith(":")) details += ",";
						details += which;
						keyDetails.put(i, details);
					}
					System.out.println(coverage + "	" + ZKDecrypto.calcscore(pt) + "	" + details + "	" + pt); 
				}
			}
		}
		
	}

	public static void main(String[] args) {
//		testDumpSubstringIocStats();
//		search();
//		process("/Volumes/My Passport - 4TB/projects/zodiac/CribCorpusScanner-1.txt");
		findCompatibleCribs("/Volumes/My Passport - 4TB/projects/zodiac/CribCorpusScanner-2-sorted-top-100000.txt", 100000);
	}
	
}
