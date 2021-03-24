package com.zodiackillerciphers.ciphers.generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.ngrams.NGramsBean;

/** use the symbol distribution of Z340 to create a valid initial assignment of cipher symbols 
 * in a generated cipher  
 */
public class SymbolAssignments {
	
	/** Z340 symbols in descending order of frequency  */
	public static String symbols = "+Bp|cOFz2RlMK5(^WVLG<4.*ykdUTNC-)#tfZYSJHD>98b_PE;761/qjXA:3&%@";
	
	/** frequencies associated with the above implicit array */
	public static int[] counts = new int[] {
		24, 12, 11, 10, 10, 10, 10, 9, 9, 8, 7, 7, 7, 7, 7, 6, 6, 6, 6, 6, 6, 6, 6, 6, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 2, 2, 2, 2, 2, 2, 2, 1		
	};
	
	/** map symbols to their index in the arrays */
	public static Map<Character, Integer> indexMap;
	static {
		indexMap = new HashMap<Character, Integer>();
		for (int i=0; i<symbols.length(); i++) {
			indexMap.put(symbols.charAt(i), i);
		}
	}

	/** make symbol assignments, and try to keep the assigned cipher symbol frequency as close to Z340 
	 * as possible.
	 * 
	 * TODO: might want to insert bigrams first
	 * 
	 */
	public static void makeAssignments(CandidateKey key) {
		//System.out.println("Making assignments");
		
		
		// TEMPORARY.  REMOVE ME!
		TranscriptionErrors.MAX_ERRORS = 0;
		
		/** make a copy of the Z340 counts */
		int[] counts = SymbolAssignments.counts.clone();

		/** count plaintext letters, and track plaintext positions */
		//Map<Character, Integer> plainCounts = new HashMap<Character, Integer>();
		Map<Character, List<Integer>> plainPositions = new HashMap<Character, List<Integer>>();
		for (int i=0; i<key.plain.plaintext.length(); i++) {
			char p = key.plain.plaintext.charAt(i);
			if (p == '_') continue; // ignore filler
			char c = key.cipher.cipher.charAt(i);
			if (c == ' ') { // plaintext assignment has not yet been made 
				/*Integer val = plainCounts.get(p);
				if (val == null) val = 0;
				val++;
				plainCounts.put(p, val);*/ 
				
				// update list of positions; we draw randomly from them later.  list also used
				// for counts.
				List<Integer> positions = plainPositions.get(p);
				if (positions == null) positions = new ArrayList<Integer>();
				positions.add(i);
				plainPositions.put(p, positions);
				
			} else { // some assignments have already been made, so deduct those symbols from the cipher symbol counts
				counts[indexMap.get(c)]--;
			}
		}
		
		/*
		 * start at highest-frequency cipher symbol. map to plaintext letter
		 * chosen randomly from among those that have equal or higher frequency.
		 * continue until all cipher symbols have been assigned.
		 */
		
		for (int i=0; i<counts.length; i++) {
			char c = symbols.charAt(i);
			int n = counts[i];
			
			// get all plaintext letters that have equal or higher count, AND are valid assignments
			String choices = "";
			for (char p : plainPositions.keySet()) {
				if (!key.isValidAssignment(c, p)) continue; // this symbol is already assigned to a different plaintext letter
				if (plainPositions.get(p).size() >= n) choices += p;
			}
			if (choices.length() > 0) { // found at least one valid plain text letter
				char r = choices.charAt(CandidateKey.rand.nextInt(choices.length()));
				
				// assign the cipher symbol to n random positions of the selected plaintext
				for (int j=0; j<n; j++) {
					//System.out.println("choices " + choices + " r" + r + " c " + c + " j " + j + " positions " + plainPositions);
					int pos = plainPositions.get(r).remove(CandidateKey.rand.nextInt(plainPositions.get(r).size()));
					//System.out.println("before encode " + key.plain.plaintext + " " + key.cipher.cipher);
					if (valid(key, pos, r, c))
						key.encode(pos, r, c); // for some reason, existing
												// symbol assignments at filler
												// positions were getting
												// overwritten. also, some
												// assignments were getting
												// overwritten, so now this only
												// makes a new assignment if the
												// symbol in the selected
												// position is blank.					
					
					/*key.expressGenome();
					if (key.errorsEncipherment() > 0) {
						System.out.println("ERROR " + key.plain.plaintext.charAt(pos) + ", " + key.plain.plaintext + " " + key.cipher.cipher);
						System.exit(-1);
					}*/
					
					counts[indexMap.get(c)]--;
				}
			}
		}
		
		//key.expressGenome(); System.out.println("after ass 1 " + key.errorsEncipherment());
		
		// deal with leftover plaintext letters
		// which cipher symbols didn't get used?
		List<Character> leftoverCipherSymbols = new ArrayList<Character>();
		for (int i=0; i<key.cipherAlphabet.length(); i++) {
			char ch = key.cipherAlphabet.charAt(i);
			if (key.c2p.containsKey(ch)) continue;
			leftoverCipherSymbols.add(ch);
		}
		//String blah = null;
		for (char p : plainPositions.keySet()) {
			//System.out.println("leftover " + p + " " + plainPositions.get(p));
			if (plainPositions.get(p) == null) continue;
			if (plainPositions.get(p).isEmpty()) continue;
			
			// we can map it to one of its existing cipher symbol mappings,
			// or to one of the leftover symbols.  give preference to leftovers,
			// since this should bring unigram distribution closer to Z340.
			
			for (int pos : plainPositions.get(p)) {
				//key.expressGenome();  System.out.println("baboon " + key.plain.plaintext + " " + key.cipher.cipher);
				if (leftoverCipherSymbols.isEmpty()) {
					// pick random cipher symbol mapping
					if (key.p2c.get(p) == null) {
						//System.out.println("Cipher mappings for " + p + " is null");
					} else {
						List<Character> choices = new ArrayList<Character>(key.p2c.get(p));
						char chosen = choices.get(key.rand.nextInt(choices.size()));
						if (valid(key, pos, p, chosen)) key.encode(pos, p, chosen);
						//blah = "choices " + choices + " chosen " + chosen;
					}
				} else {
					//blah = "leftoverCipherSymbols " + leftoverCipherSymbols.toString();
					char chosen = leftoverCipherSymbols.remove(key.rand
							.nextInt(leftoverCipherSymbols.size()));
					if (valid(key, pos, p, chosen)) key.encode(pos, p, chosen);
					//blah += " chosen " + chosen;
				}
				/*key.expressGenome();
				if (key.errorsEncipherment() > 0) {
					System.out.println("ERROR ASS 2 pos " + pos + " p " + p + " blah " + blah + " " + key.plain.plaintext + " " + key.cipher.cipher);
					System.exit(-1);
				}*/
			}
			
		}
		
		//key.expressGenome(); System.out.println("after ass 2 " + key.errorsEncipherment());
		
		
		// Now, we have that portion of filler we need to deal with.  we can put any cipher
		// symbols there.  so, check the cipher symbol counts for leftovers that we can assign there.
		
		List<Integer> indexes = new ArrayList<Integer>();
		for (int i=0; i<counts.length; i++) {
			if (counts[i] == 0) continue;
			indexes.add(i);
		}
		
		//System.out.println("indexes " + indexes + " filler " + key.plain.fillerStart + " " + key.plain.fillerEnd);
		
		for (int i=0; i<key.plain.plaintext.length(); i++) {
			char c = 0;
			char p = key.plain.plaintext.charAt(i);
			if (p != '_') continue;
			if (indexes.isEmpty())
				c = CandidateKey.randomCipherSymbol(); // any will do, since we
														// reached perfect match
														// to Z340 symbol counts
			else { // otherwise, use up the remaining counts from Z340
				int which = indexes.get(CandidateKey.rand.nextInt(indexes.size()));
				c = symbols.charAt(which);
				counts[which]--;
			}
			//System.out.println("encode " + i + " " + p + " " + c);
			key.encode(i, p, c);
		}

		//key.expressGenome(); System.out.println("after ass 3 " + key.errorsEncipherment());
		
		/* finally, some plaintext letters might remain unassigned.
		 * if all cipher symbols are assigned, then some backpedalling is needed,
		 * because a cipher symbol cannot map to multiple plaintext letters (except for transcription errors,
		 * which we don't account for here)
		 * 
		 * let p be the plaintext letter that has no cipher equivalent.
		 * it has frequency f(p)
		 * we need to select some cipher symbol c such that f(c) = f(p),
		 * and |P(c)| > 1 where p(c) denotes set of plaintext letter mappings to c 
		 */
		
		//key.expressGenome();
		for (char p : plainPositions.keySet()) {
			Set<Character> symbols = key.p2c.get(p);
			if (symbols == null || symbols.isEmpty()) {
				int n = plainPositions.get(p).size(); // n = frequency of p
				// find cipher symbols with the same frequency, that have more than one plaintext assignment
				String choose = key.cipherSymbolsWithFrequency(n);
				if (choose.isEmpty()) continue;
				// pick a symbol to move into the missing assignment
				char symbolToMove = choose.charAt(CandidateKey.rand.nextInt(choose.length()));
				// we need to find out what to replace it with.
				String possibleReplacements = key.cipherSymbolsMappedToSamePlaintextAsThisSymbol(symbolToMove);
				if (possibleReplacements.isEmpty()) continue;
				char replacement = possibleReplacements.charAt(CandidateKey.rand.nextInt(possibleReplacements.length())); 
				
				
				
				//key.expressGenome();  System.out.println("nasal " + key.plain.plaintext + " " + key.cipher.cipher);

				// replace all occurrences of symbolToMove with the chosen replacement symbol 
				key.encodeReplace(symbolToMove, replacement);
				
				//String debug = "p " + p + " n " + n + " choose " + choose + " symbolToMove " + symbolToMove + " possibleReplacements " + possibleReplacements
				//		+ " replacement " + replacement;
				// perform the replacement
				/*for (int i=0; i<key.plain.plaintext.length(); i++) {
					char pp = key.plain.plaintext.charAt(i);
					char cc = key.cipher.cipher.charAt(i);

					
					if (cc == ' ') key.encode(i, pp, symbolToMove);
					else if (cc == symbolToMove) key.encode(i, pp, replacement);
					debug  += " n " + n + " choose " + choose + " i " + i + " p " + p + " pp " + pp + " cc " + cc + " symbolToMove " + symbolToMove + " replacement " + replacement + " ";
					
				}*/
				
				// symbolToMove is now no longer assigned to anything, so assign it to our missing plaintext
				key.encodeAll(p, symbolToMove);
				
				
				/*key.expressGenome();
				if (key.errorsEncipherment() > 0) {
					System.out.println("ERROR ASS 4 " + debug + " " + key.plain.plaintext + " " + key.cipher.cipher);
					System.out.println(key);
					System.exit(-1);
				}*/
			}
			
		}
		
		//key.expressGenome(); System.out.println("after ass 4 " + key.errorsEncipherment());
		
		
		
		// one more thing: let's fix ngrams while we're here
		/*
		key.expressGenome();
		System.out.println("Cipher " + key.cipher.cipher);
		boolean repeat = true;
		while (repeat) {
			Map<Integer, NGramsBean> beans = NGramsBean.makeBeansFor(key.cipher.cipher);
			List<String> ngramsMissing = NGramsBean.missingRepeats(beans, NGramsBean.referenceCipherBeans.get("z340"));
			List<String> ngramsExtra = NGramsBean.missingRepeats(NGramsBean.referenceCipherBeans.get("z340"), beans);
			if (ngramsMissing.isEmpty() && ngramsExtra.isEmpty()) {
				repeat = false;
				break; // success!
			}
			
			for (String ngram : ngramsMissing) {
				// decode ngram to plaintext.  this assumes every cipher symbols maps to exactly one plaintext letter.
				String ngramPlain = key.decode(ngram);
				
				// now we have the plaintext equivalent of the ngram.  find all occurrences.
				List<Integer> positions = key.searchInPlaintext(ngramPlain);
				if (positions.isEmpty()) {
					System.out.println("Can't find " + ngramPlain + " in plaintext.");
					repeat = false; // can't repeat, because plaintext will never satisify this ngram
				} else {
					// pick one at random and place the ngram there.
					int which = positions.get(CandidateKey.rand.nextInt(positions.size()));
					key.encode(which, ngramPlain, ngram);
				}
			}
			
			for (String ngram : ngramsExtra) {
				// this ngrams repeats too much.
				// pick an occurrence at random.
				List<Integer> positions = key.searchInCipher(ngram);
				int which = positions.get(CandidateKey.rand.nextInt(positions.size()));
				// pick a spot in the ngram at random, and assign a different cipher symbol there.
				int ngramSpot = CandidateKey.rand.nextInt(ngram.length());
				char c = ngram.charAt(ngramSpot);
				String candidates = key.cipherSymbolsMappedToSamePlaintextAsThisSymbol(c);
				if (candidates.isEmpty()) continue; // there are no alternate symbols to pick from
				char newC = candidates.charAt(CandidateKey.rand.nextInt(candidates.length()));
				key.encode(which + ngramSpot, key.plain.plaintext.charAt(which + ngramSpot), newC);
			}
			
			key.expressGenome();
			System.out.println("Cipher " + key.cipher.cipher);
		}
		*/
		
		
		//key.expressGenome();
		//System.out.println("Cipher " + key.cipher.cipher);
		//System.out.println("Plain " + key.plain.plaintext);
		//System.out.println("Counts " + Arrays.toString(counts));
		//System.exit(-1);
	}
	
	/** reject some encodings if they result in encipherment errors */
	public static boolean valid(CandidateKey key, int pos, char plain, char symbol) {
		char existingSymbol = key.cipher.cipher.charAt(pos);
		if (existingSymbol != ' ' && existingSymbol != symbol) return false; // don't change existing mapping
		/*if (key.c2p.containsKey(symbol) && !key.c2p.get(symbol).contains(plain)) {
			return false; // don't allow multiple plaintext assignments for a single cipher symbol
		}*/
		return true;
	}
}
