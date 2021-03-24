package com.zodiackillerciphers.ciphers.generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.homophones.HomophoneSequenceBean;
import com.zodiackillerciphers.homophones.HomophonesNew;
import com.zodiackillerciphers.homophones.SequenceMatcher;
import com.zodiackillerciphers.homophones.SequenceTarget;
import com.zodiackillerciphers.ngrams.NGramsBean;
import com.zodiackillerciphers.tests.EvenOddAndFactors;
import com.zodiackillerciphers.tests.PrimePhobia;
import com.zodiackillerciphers.tests.PrimePhobiaBean;
import com.zodiackillerciphers.transform.CipherTransformations;

import ec.util.MersenneTwisterFast;

//TODO - swap all occurrences of symbols instead of just one at a time
//TODO - test error correction
public class Mutators {
	
	static MersenneTwisterFast rand = new MersenneTwisterFast();
	
	public static double[] proportions = new double[] {
		0.24048, 0.17571, 0.08474, 0.06377, 0.06302, 0.05978, 0.04518, 0.04156, 0.03682, 0.02783, 0.02496, 0.02209, 0.01747, 0.01435, 0.01335, 0.01248, 0.01198, 0.01186, 0.01123, 0.00849, 0.00524, 0.00399, 0.00212, 0.00125, 0.00025
	};
	public static double[] roulette;
	static {
		roulette = new double[proportions.length];
		roulette[0] = proportions[0];
		for (int i=1; i<proportions.length; i++) {
			roulette[i] = roulette[i-1]+proportions[i];
		}
		System.out.println("Roulette: " + Arrays.toString(roulette));
	}
	
	public static void fixDistribution(CandidateKey key, int which) {
		Map<Character, Integer> countsZ340 = key.referenceCounts;
		Map<Character, Integer> countsThis = Ciphers.countMap(key.cipher.cipher.toString());
		
		/** there are too many of these symbols compared to Z340 */ 
		List<Character> tooMany = new ArrayList<Character>();
		/** there are too few of these symbols compared to Z340 */
		List<Character> tooFew = new ArrayList<Character>();
		
		for (Character c : countsThis.keySet()) {
			Integer val1 = countsThis.get(c);
			Integer val2 = countsZ340.get(c);
			if (val2 == null) val2 = 0;
			if (val1 > val2) tooMany.add(c);
			else if (val1 < val2) tooFew.add(c);
		}
		
		// symbols from countsZ340 that aren't in countsThis
		for (Character c : countsZ340.keySet()) {
			if (countsThis.get(c) != null) continue;  // already counted above
			tooFew.add(c);
		}
		
		/*System.out.println("fixDistribution tooMany " + tooMany);
		System.out.println("fixDistribution tooFew " + tooFew);
		System.out.println("fixDistribution countsZ340 " + countsZ340);
		System.out.println("fixDistribution countsThis " + countsThis);*/
		
		if (which == 0) {
			key.lastMutator = "fixDistribution1";
			// select from the "too many" list
			if (tooMany.size() == 0) return;
			char c = tooMany.get(rand.nextInt(tooMany.size()));
			List<Integer> positions = key.cPos.get(c);
			if (positions == null) {
				System.out.println("SMEG POSITIONS NULL FOR " + c + ", key " + key);
			}
			int randPos = rand.nextInt(positions.size());
			//System.out.println("fixDistribution tooMany randPos " + randPos);
			
				// just replace it with a random symbol
				encode(key.genome, randPos, CandidateKey.randomCipherSymbol());
				
				/*
			 // pick from among other symbols assigned for the plaintext at that spot
				key.lastMutator = "fixDistribution2";
				// what is the plaintext for that position?
				char p = key.plain.plaintext.charAt(randPos);
				// what else does the plaintext map to?
				Set<Character> others = new HashSet<Character>();
				if (key.p2c != null && key.p2c.get(p) != null) {
					others.addAll(key.p2c.get(p));
					others.remove(c);
					List<Character> list = new ArrayList<Character>(others);
					if (!list.isEmpty()) {
						//key.genome[randPos+1] = key.alleleForAlpha(list.get(rand.nextInt(list.size())));
						encode(key.genome, randPos, list.get(rand.nextInt(list.size())));
					}
				}
			}*/
		} else { // select from the "too few" list
			key.lastMutator = "fixDistribution2";
			if (tooFew.size() == 0) return;
			char c = tooFew.get(rand.nextInt(tooFew.size()));
			
			// just add it
			int pos = rand.nextInt(340);
			encode(key.genome, pos, c);
			// what is the corresponding plaintext?
			/*
			if (key.c2p != null && key.c2p.get(c) != null) {
				List<Character> ps = new ArrayList<Character>(key.c2p.get(c)); // might be more than one due to errors
				if (!ps.isEmpty()) {
					char p = ps.get(rand.nextInt(ps.size()));
					// make list of positions that use other symbols that decode to this letter
					List<Integer> pos = new ArrayList<Integer>();
					for (int i=0; i<key.cipher.cipher.length(); i++) {
						char pp = key.plain.plaintext.charAt(i);
						char cc = key.cipher.cipher.charAt(i);
						if (pp != p) continue;
						if (cc == c) continue;
						pos.add(i);
					}
					if (!pos.isEmpty()) {
						int randPos = pos.get(rand.nextInt(pos.size()));
						char cNew = key.cipher.cipher.charAt(randPos);
						key.genome[randPos+1] = key.alleleForAlpha(cNew); 
					}
				}
			}*/
		}
	}
	
	public static void fixErrors(CandidateKey key) {
		key.lastMutator = "fixErrors";
		
		List<Character> symbolsWithErrors = new ArrayList<Character>(); 
		for (Character c : key.c2p.keySet())
			if (key.c2p.get(c).size() > 1) symbolsWithErrors.add(c);

		//System.out.println("errors before " + symbolsWithErrors.size());// + " plain " + key.plain.plaintext + " cipher " + key.cipher.cipher);
		//System.out.println(key.dumpp2c());
		//System.out.println(key.dumpc2p());
		
		if (symbolsWithErrors.size() == 0) return;
		int which = rand.nextInt(symbolsWithErrors.size());
		
		char c = symbolsWithErrors.get(which);
		List<Integer> positions = new ArrayList<Integer>();
		List<Character> plains = new ArrayList<Character>();
		for (int i=0; i<key.cipher.cipher.length(); i++) 
			if (key.cipher.cipher.charAt(i) == c) {
				positions.add(i);
				plains.add(key.plain.plaintext.charAt(i));
			}
		
		//System.out.println("fixing symbol " + c + ", positions " + positions);
		// get a random occurrence of the symbol
		int select = positions.get(rand.nextInt(positions.size()));
		// what is the plain text there?
		char plain = key.plain.plaintext.charAt(select);
		
		//System.out.println("plaintext there is " + plain);
		
		// what cipher symbols are valid substitutions for this plain text?
		Set<Character> set = key.p2c.get(plain);
		if (set == null || set.isEmpty()) return;
		List<Character> symbols = new ArrayList<Character>(set);
		// pick a valid symbol assignment at random
		char symbol = symbols.get(rand.nextInt(symbols.size()));
		// encode it in the genome
		//System.out.println("encoding it as " + symbol);
		key.genome[select+1] = key.alleleForAlpha(symbol);
		
		//symbolsWithErrors = new ArrayList<Character>(); 
		//for (Character c2 : key.c2p.keySet())
		//	if (key.c2p.get(c2).size() > 1) symbolsWithErrors.add(c2);
		//System.out.println("errors after " + symbolsWithErrors.size());// + " plain " + key.plain.plaintext + " cipher " + key.cipher.cipher);
		//System.out.println(key.dumpp2c());
		//System.out.println(key.dumpc2p());
		
	}
	
	/** flip a coin.  if heads, mutate only one allele in the genome.
	 * if tails, mutate between some random selection of 2 and max alleles.
	 * @param key
	 */
	public static void alleleMutation(CandidateKey key, int max, int which) {
		int num = 1;
		if (which == 1) { // select from [2, max]
			key.lastMutator = "alleleMutation2";
			num = 2 + rand.nextInt(max-2+1);
		} else 			key.lastMutator = "alleleMutation1";

		for (int i=0; i<num; i++) {
			int selected = rand.nextInt(key.genome.length);
			key.genome[selected] = rand.nextFloat();
		}
	}
	/** flip a coin.  if heads, swap only one pair of alleles in the genome.
	 * otherwise, swap up to max times.
	 */
	public static void swap(CandidateKey key, int max, int which) {
		int num = 1;
		if (which == 1) { // select from [2, max]
			num = 2 + rand.nextInt(max-2+1);
			key.lastMutator = "swap2";
		} else
			key.lastMutator = "swap1";
			
		for (int i=0; i<num; i++) {			
			int a = rand.nextInt(key.genome.length-1) + 1;
			int b = a;
			while (b==a) b = rand.nextInt(key.genome.length-1) + 1;
			float tmp = key.genome[a];
			key.genome[a] = key.genome[b];
			key.genome[b] = tmp;
		}
	}
	
	/** more tweaks */
	public static void tweak(CandidateKey key, int which) {
		
		key.lastMutator = "tweak" + which;
		
		// let C be set of symbols assigned to plaintext letter p
		// let P be set of all occurrences of p in the plaintext
		// here are some tweaks:
		// 1) pick one occurrence of p at random, replace it with a random c from C
		// 2) same as 1 but repeat multiple times
		
		if (which == 0 || which == 1) {
			// pick a random plaintext letter
			List<Character> letters = new ArrayList<Character>(key.p2c.keySet());
			char p = letters.get(rand.nextInt(letters.size()));
			// available cipher symbols assigned to this letter
			List<Character> choices = new ArrayList<Character>(key.p2c.get(p));
			if (choices.size() > 1) { // only bother if more than one cipher symbol is assigned to p
				int iterations = which == 0 ? 1 : rand.nextInt(key.pPos.get(p).size());
				for (int i=0; i<iterations; i++) {
					// get a random occurrence of p
					int pos = key.pPos.get(p).get(rand.nextInt(key.pPos.get(p).size()));
					// encode a random c there
					char c = choices.get(rand.nextInt(choices.size()));
					key.encode(pos, p, c);
				}
			}
		} else if (which == 2 || which == 3) {
			// 3) pick a pair of occurrences of p at random.  swap their assigned cipher symbols.
			// 4) same as 3 but repeat multiple times
			List<Character> letters = new ArrayList<Character>(key.p2c.keySet());
			char p = letters.get(rand.nextInt(letters.size()));
			if (key.pPos.get(p).size() > 1) { // only do this if p occurs more than once
				int iterations = which == 0 ? 1 : rand.nextInt(key.pPos.get(p).size());
				for (int i=0; i<iterations; i++) {
					int pos1 = key.pPos.get(p).get(rand.nextInt(key.pPos.get(p).size()));
					int pos2 = pos1;
					while (pos1 == pos2) pos2 = key.pPos.get(p).get(rand.nextInt(key.pPos.get(p).size()));
					key.encode(pos1, p, key.cipher.cipher.charAt(pos2));
					key.encode(pos2, p, key.cipher.cipher.charAt(pos1));
				}
			}
		} else if (which == 4) {
			// 5) pick any c1 and c2 from the cipher alphabet.  swap all occurrences of c1 with c2.
			List<Character> choices = new ArrayList<Character>(key.c2p.keySet());
			if (choices.size() > 1) {
				char c1 = choices.get(rand.nextInt(choices.size()));
				char c2 = c1;
				while (c1 == c2) {
					c2 = choices.get(rand.nextInt(choices.size()));
				}
				for (int pos=0; pos<key.cipher.cipher.length(); pos++) {
					char c = key.cipher.cipher.charAt(pos);
					char p = key.plain.plaintext.charAt(pos);
					if (c == c1)
						key.encode(pos, p, c2);
					else if (c == c2)
						key.encode(pos, p, c1);
				}
			}
			
		} else if (which == 5) {
			// 6) consider p1 and p2.  p1 is assigned to symbols C1.  p2 is assigned to symbols C2.
			//       move a random c1 from C1 to C2.  so, every instance of c1 assigned to p1 is replaced
			//       with a random c from C1  (can randomize selection each time, or just once)
			//             	example:  p1: {A,B,C}   ==>  p1: {A,B}
			//				          p2: {D,E,F}   ==>  p2: {D,E,F,C}  (randomly place the C, preserving its frequency)
			List<Character> letters = new ArrayList<Character>(key.p2c.keySet());
			char p1 = letters.get(rand.nextInt(letters.size()));
			char p2 = p1;
			while (p1 == p2) 
				p2 = letters.get(rand.nextInt(letters.size()));
			List<Character> choices = new ArrayList<Character>(key.p2c.get(p1));
			if (choices.size() > 1) { // don't bother unless this p has more than one c 
				char c = choices.get(rand.nextInt(choices.size()));
				choices = new ArrayList<Character>();
				for (Character cc : key.p2c.get(p1)) if (c != cc) choices.add(cc);
				
				// unassign c from p1 with random selections from C1
				int cCount = key.cPos.get(c).size(); // preserve count since it is affected by unassignment
				
				List<Integer> positionsToProcess = new ArrayList<Integer>();
				for (Integer pos : new ArrayList<Integer>(key.cPos.get(c))) {
					char pActual = key.plain.plaintext.charAt(pos);
					if (pActual == p1) positionsToProcess.add(pos); 
				}
				
				
				// get all positions associated with p2
				Integer[] positions = key.pPos.get(p2).toArray(new Integer[0]);
				CipherTransformations.shuffle(positions);
				// encode c at at most cCount of p2's positions
				for (int i=0; i<Math.min(cCount, positions.length); i++) {
					key.encode(positions[i], p2, c);
				}
			}
		}

	}
	
	
	
	
	/** encode genome at location corresponding to the given cipher position, with the given cipher text symbol */
	public static void encode(float[] genome, int position, char c) {
		genome[position+1] = CandidateKey.alleleForAlpha(c);
	}
	
	/** randomly select two cipher symbols.  replace all occurrences of the first with the second,
	 * and vice-versa 
	 */
	
	public static void exchange(CandidateKey key) {
		key.lastMutator = "exchange";
		
		char c1 = 'x';
		char c2 = 'x';
		while (c1 == c2) {
			c1 = CandidateKey.cipherAlphabet.charAt(rand.nextInt(CandidateKey.cipherAlphabet.length()));
			c2 = CandidateKey.cipherAlphabet.charAt(rand.nextInt(CandidateKey.cipherAlphabet.length()));
		}
		for (int i=0; i<key.cipher.cipher.length(); i++) {
			char c = key.cipher.cipher.charAt(i);
			if (c == c1) key.genome[i+1] = CandidateKey.alleleForAlpha(c2);
			else if (c == c2) key.genome[i+1] = CandidateKey.alleleForAlpha(c1);
		}
		
	}
	
	/** look for a missing repeated ngram and try to insert it somewhere.
	 * or, find a repeating ngram that isn't in Z340 and remove it.
	 **/
	public static void fixNgrams(CandidateKey key, int which) {
		
		Map<Integer, NGramsBean> map1 = NGramsBean.makeBeansFor(key.cipher.cipher.toString());
		Map<Integer, NGramsBean> map2 = NGramsBean.referenceCipherBeans.get("z340");

		if (which == 0) {
			key.lastMutator = "fixNgrams1";
			// insert a missing ngram
			List<String> list = NGramsBean.missingRepeats(map1, map2);
			if (list == null || list.isEmpty()) return;
			// pick a missing ngram at random
			String ngram = list.get(rand.nextInt(list.size()));
			// stick it somewhere at random
			int pos = rand.nextInt(key.cipher.cipher.length()-ngram.length()+1);
			for (int i=0; i<ngram.length(); i++) encode(key.genome, pos+i, ngram.charAt(i));
		} else {
			key.lastMutator = "fixNgrams2";
			// remove an extra ngram
			List<String> list = NGramsBean.missingRepeats(map2, map1);
			if (list == null || list.isEmpty()) return;
			// pick an extra ngram at random
			String ngram = list.get(rand.nextInt(list.size()));
			// find all occurrences
			List<Integer> positions = new ArrayList<Integer>();
			int pos = key.cipher.cipher.indexOf(ngram);
			while (pos > -1 && pos < key.cipher.cipher.length() - ngram.length() + 1) {
				positions.add(pos);
				pos = key.cipher.cipher.indexOf(ngram, pos+ngram.length());
			}
			// pick one at random
			Integer position = positions.get(rand.nextInt(positions.size()));
			// replace one of its symbols with another one chosen at random
			int i = rand.nextInt(ngram.length());
			encode(key.genome, position+i, CandidateKey.randomCipherSymbol());
		}
		
	}
	
	// randomly select one of the targeted homophone cycle sequences.
	// try to fix it in the generated cipher.
	public static void fixCycle(CandidateKey key, int which) {
		
		// pick random target cycle from z340
		SequenceTarget target = SequenceMatcher.targets.get(rand.nextInt(SequenceMatcher.targets.size()));
		
		// isolate the full sequence from the generated cipher
		HomophoneSequenceBean sequence = HomophonesNew.isolateSequence(key.cipher.cipher.toString(), target.sequence);
		
		// pick a random spot to start
		// determine the next expected letter in the cycle.
		// continue until an unexpected letter is found, then fix it. 
		// or exit if none found.
		int r = sequence.fullSequence.length() - target.sequence.length() + 1;
		if (r < 1) return; 
		int pos = rand.nextInt(r);
		
		//System.out.println("fixCycle " + target.sequence + ", " + sequence.fullSequence + ", " + pos);
		while (pos < sequence.fullSequence.length() - 1) {
			// symbol from full sequence
			char c1 = sequence.fullSequence.charAt(pos);
			// given this symbol, what is the next symbol we expect from the target cycle? 
			char c2 = target.after(c1);
			// what is the one we actually have?
			char c3 = sequence.fullSequence.charAt(pos+1);

			//System.out.println("fixCycle " + pos + " " + c1 + "" + c2 + "" + c3);
			
			if (c2 == c3) {
				pos++;
				continue; // they match, so it looks good.  keep looking.
			}
			
			// fix it with one of
			// 3 possible fixes:		

			if (which == 0) {
				// 1) replace the unexpected symbol with the expected symbol
				//System.out.println("fixCycle encoding " + c2 + " at " + sequence.positions.get(pos+1) + ", cipher " + key.cipher.cipher);
				encode(key.genome, sequence.positions.get(pos+1), c2);
				
				//System.out.println("fixCycle after fix " + key.cipher.cipher);
				key.lastMutator = "fixCycle1";
				
			} else if (which == 1) {
				// 2) remove wrong symbol and place the correct symbol somewhere nearby at random.
				//      "nearby" means somewhere in the region of cipher between the current position
				//      and the next occurrence of a symbol in the sequence

				// remove the wrong symbol (replace it with another random symbol)
				encode(key.genome, sequence.positions.get(pos+1), CandidateKey.randomCipherSymbol());
				
				// determine eligible positions to put the right symbol
				int posBegin = sequence.positions.get(pos+1);
				int posEnd = key.cipher.cipher.length(); 
				if (pos+2 < sequence.positions.size()) 
					posEnd = sequence.positions.get(pos+2);
				
				// draw random position from [posBegin, posEnd)  
				int randPos = rand.nextInt(posEnd-posBegin) + posBegin;
				
				//encode the correct symbol there
				encode(key.genome, randPos, c2);
				key.lastMutator = "fixCycle2";
			} else if ((pos+2) < sequence.positions.size()) {
				// 3) remove wrong symbol.  determine the correct symbol's plaintext equivalent.  
				//      scan forward until that plaintext is found.  stick symbol there.
				//      if different symbols from cycle found, remove them first so they don't break the fix.
				// remove the wrong symbol (replace it with a random symbol)
				encode(key.genome, sequence.positions.get(pos+1), CandidateKey.randomCipherSymbol());
				
				// determine eligible positions to put the right symbol
				int posBegin = sequence.positions.get(pos+1);
				int posEnd = key.cipher.cipher.length();
				if (pos+2 < sequence.positions.size()) 
					posEnd = sequence.positions.get(pos+2);
				
				// what is the plaintext assignment for the correct symbol?
				if (key.c2p.get(c2) != null) {
					List<Character> plaintexts = new ArrayList<Character>(key.c2p.get(c2));
					List<Integer> candidates = new ArrayList<Integer>();
					for (char p : plaintexts) {
						// where does this plaintext occur in our range of eligible positions?
						for (int i=posBegin; i<posEnd; i++) {
							if (key.plain.plaintext.charAt(i) == p) candidates.add(i);
						}
					}
					if (!candidates.isEmpty()) {
						encode(key.genome, candidates.get(rand.nextInt(candidates.size())), c2);
					}
				}
				key.lastMutator = "fixCycle3";
			} 
			
			// possible fix #4:
			// cycle might be almost perfect but offset by one. 
			// i.e. target is ABABAB but actual is BABABA.
			// fix: shift left or right
			
			break;
		}
		
	}
	public static void fixPrimePhobia(CandidateKey key) {
		key.lastMutator = "fixPrimePhobia";
		
		// get list of all +s and Bs that fall on prime positions.
		// pick one at random 
		// fix it by:
		// 1) removing it and replacing with a random symbol.
		// 2) if the distribution is wrong, add new symbol to random location
		// note: i am not implementing #3 because it is the job of the fixDistribution method
		
		PrimePhobiaBean bean = PrimePhobia.errors(key.cipher.cipher.toString());
		if (bean.positions.isEmpty()) return;
		Integer pos = bean.positions.get(rand.nextInt(bean.positions.size()));
		// replace it with a random symbol instead of blanking it out
		encode(key.genome, pos, CandidateKey.randomCipherSymbol());
	}

	// try to fix ngram distribution when cipher's even (odd) positions are removed
	public static void fixOddEvens(CandidateKey key, int which, boolean odd) {
		
		String[] removed = EvenOddAndFactors.removeOddsEvens(key.cipher.cipher.toString());
		String removedOdds = removed[0];
		String removedEvens = removed[1];

		Map<Integer, NGramsBean> mapRemovedOdds = NGramsBean.makeBeansFor(removedOdds);
		Map<Integer, NGramsBean> mapRemovedEvens = NGramsBean.makeBeansFor(removedEvens);
		
		Map<Integer, NGramsBean> mapZ340RemovedOdds = NGramsBean.referenceCipherBeans.get("z340odds");
		Map<Integer, NGramsBean> mapZ340RemovedEvens = NGramsBean.referenceCipherBeans.get("z340evens");
		
		Map<Integer, NGramsBean> map1 = null; // cipher map
		Map<Integer, NGramsBean> map2 = null; // reference map
		
		String shortCipher = null;
		
		//boolean odd = rand.nextBoolean();
		if (odd) {
			shortCipher = removedOdds;
			map1 = mapRemovedOdds;
			map2 = mapZ340RemovedOdds;
		} else {
			shortCipher = removedEvens;
			map1 = mapRemovedEvens;
			map2 = mapZ340RemovedEvens;
		}
		
		//System.out.println("fixOddEvens " + odd + " " + shortCipher + " map1 " + map1 + " map 2 " + map2);

		//boolean which = rand.nextBoolean();
		if (which == 0) {
			key.lastMutator = "fixOddEvens1" + (odd ? "Odd" : "Even");
			// insert a missing ngram
			List<String> list = NGramsBean.missingRepeats(map1, map2);
			//System.out.println("fixOddEvens missing ngrams " + list);
			if (list == null || list.isEmpty()) return;
			// pick a missing ngram at random
			String ngram = list.get(rand.nextInt(list.size()));
			// stick it somewhere at random
			int pos = rand.nextInt(shortCipher.length()-ngram.length()+1);
			//System.out.println("fixOddEvens ngram " + ngram + " pos " + pos + " translated " + translate(pos, odd));
			//System.out.println("fixOddEvens cipher before: " + key.cipher.cipher);
			for (int i=0; i<ngram.length(); i++) encode(key.genome, translate(pos+i, odd), ngram.charAt(i));
			//key.expressGenome();
			//System.out.println("fixOddEvens cipher  after: " + key.cipher.cipher);
		} else {
			key.lastMutator = "fixOddEvens2" + (odd ? "Odd" : "Even");
			// remove an extra ngram
			List<String> list = NGramsBean.missingRepeats(map2, map1);
			if (list == null || list.isEmpty()) return;
			// pick an extra ngram at random
			String ngram = list.get(rand.nextInt(list.size()));
			// find all occurrences
			List<Integer> positions = new ArrayList<Integer>();
			int pos = shortCipher.indexOf(ngram);
			while (pos > -1 && pos < shortCipher.length() - ngram.length() + 1) {
				positions.add(pos);
				pos = shortCipher.indexOf(ngram, pos+ngram.length());
			}
			// pick one at random
			Integer position = positions.get(rand.nextInt(positions.size()));
			// replace one of its symbols at random with a randomly selected symbol
			//for (int i=0; i<ngram.length(); i++)
			int i = rand.nextInt(ngram.length());
			encode(key.genome, translate(position+i, odd), CandidateKey.randomCipherSymbol());
		}
	}
	
	/** try to remove repeats from lines 1-3, 11-13 */
	public static void fixLineRepeats(CandidateKey key) {
		key.lastMutator = "lineRepeats";
		
		int[] lines = new int[] {0,1,2,10,11,12};
		
		/** count symbols on these lines */
		Map<Character, List<Integer>> map = new HashMap<Character, List<Integer>>();
		for (int line : lines) {
			for (int pos=line*17; pos<line*17+17; pos++) {
				char c = key.cipher.cipher.charAt(pos);
				List<Integer> val = map.get(c);
				if (val == null) val = new ArrayList<Integer>();
				val.add(pos);
				map.put(c, val);
			}
		}
		
		/** make list of positions with repeated symbols */ 
		List<Integer> positions = new ArrayList<Integer>();
		for (Character c : map.keySet())
			if (map.get(c).size() > 1)
				positions.addAll(map.get(c));
		
		/** pick a position at random */
		int pos = positions.get(rand.nextInt(positions.size()));
		char c = key.cipher.cipher.charAt(pos);
		/** what are the other symbols that can stand for the plaintext there? */
		String candidates = key.cipherSymbolsMappedToSamePlaintextAsThisSymbol(c);
		if (candidates == null || candidates.isEmpty()) return;
		char replacement = candidates.charAt(rand.nextInt(candidates.length()));
		key.encode(pos, key.plain.plaintext.charAt(pos), replacement);
	}
	
	
	/**
	 * given position in cipher that has even/odd positions removed, translate
	 * it back to corresponding position in the original cipher
	 */
	public static int translate(int position, boolean odd) {
		// if odd is true, cipher has had odd positions removed.
		// so, remaining symbols are in positions {0, 2, 4, etc}
		if (odd) return position*2+1;
		// if odd is false, cipher has had even positions removed.
		// so, remaining symbols are in positions {1, 3, 5, etc}
		return position*2;
	}
	
	public static void main(String[] args) {
		;
	}
	
}
