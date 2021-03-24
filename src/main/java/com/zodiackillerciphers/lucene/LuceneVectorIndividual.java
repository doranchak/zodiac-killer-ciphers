package com.zodiackillerciphers.lucene;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.Sort;


import ec.EvolutionState;
import ec.util.MersenneTwisterFast;
import ec.vector.IntegerVectorIndividual;
import ec.vector.IntegerVectorSpecies;
import ec.vector.VectorIndividual;

public class LuceneVectorIndividual extends IntegerVectorIndividual {
	public StringBuffer decoded;
	public StringBuffer decoder;
	public StringBuffer matches;
	public float zodiacscore;
	public int mutationType = 0;
	public int mutationCountWord = 0;
	public int mutationCountBlank = 0;
	public String mutationWords = null;
	@Override
	public String genotypeToStringForHumans() {
		// TODO Auto-generated method stub
		return super.genotypeToStringForHumans() + ", decoded: [" + decoded
				+ "] matches [" + matches + "] decoder [" + decoder + "] zodiacscore [" + zodiacscore + "]" + 
				" mutationType [" + mutationType + "] " +
				"mutationCountWord [" + mutationCountWord + "] " + 
				"mutationCountBlank [" + mutationCountBlank + "] " + 
				"mutationWords [" + mutationWords + "]"; 
	}

	public String genotypeToStringForHumansSpaces() {
		return super.genotypeToStringForHumans();
	}

	public static void mutateGenome(float prob, int minGene, int maxGene,
			MersenneTwisterFast random, int[] genome) {
		// IntegerVectorSpecies s = (IntegerVectorSpecies) species;
		if (prob > 0.0)
			for (int x = 0; x < genome.length; x++)
				if (random.nextBoolean(prob))
					genome[x] = randomValueFromClosedIntervalStatic(minGene,
							maxGene, random);
	}

	public static int randomValueFromClosedIntervalStatic(int min, int max,
			MersenneTwisterFast random) {
		if (max - min < 0) // we had an overflow
		{
			int l = 0;
			do
				l = random.nextInt();
			while (l < min || l > max);
			return l;
		} else
			return min + random.nextInt(max - min + 1);
	}    
	
	public void defaultMutate(EvolutionState state, int thread) {
		mutateSwapGenome(state.random[thread], genome);
	}

	
	
	public void defaultMutate2(EvolutionState state, int thread) {
		if (1==1) throw new RuntimeException("FIX THIS");
		mutationType = 0;
		mutationCountWord = 0;
		mutationCountBlank = 0;
		mutationWords = "";
		
		
		/*if (true) { //state.random[thread].nextBoolean()) {
			mutationType = 0;
			super.defaultMutate(state, thread);
		}
		else {*/
        IntegerVectorSpecies s = (IntegerVectorSpecies) species;
        //no longer works due to upgrade:  s.mutationProbability = state.random[thread].nextBoolean() ? 0.05f : state.random[thread].nextFloat()/2;
		
		int dart = state.random[thread].nextInt(Settings.MUT_TYPES);
		//no longer works due to upgrade: float prob = s.mutationProbability;
		float prob = 0;
		if (dart == 0) {
			mutationType = 0;
			mutateGenome(prob, (int) s.minGene(0), (int) s.maxGene(0), state.random[thread], genome);
		}
		else if (dart == 1) {
			mutationType = 1;
			mutateWord(state.random[thread], genome);
		}
		/*
		else if (dart == 2) { 
			mutationType = 2;
			mutateDictionary(state.random[thread], genome);
		}*/
		else if (dart == 2) { 
			mutationType = 2;
			mutateWithFreq(prob, state.random[thread], genome);
		}
		/*else if (dart == 4) { 
			mutationType = 4;
			mutateSwap(state.random[thread], genome);
		}
		else if (dart == 5) { 
			mutationType = 5;
			mutateShift(state.random[thread], genome);
		}
		else if (dart == 6) { 
			mutationType = 6;
			mutateScramble(state.random[thread], genome);
		}
		else if (dart == 7) { 
			mutationType = 7;
			mutateReverse(state.random[thread], genome);
		}*/
		else if (dart == 3) { 
			mutationType = 3;
			mutateSwapGenome(state.random[thread], genome);
		}
		else if (dart == 4) {
			mutationType = 4;
			mutateWordPartial(state.random[thread], genome);
		}
	}
	
	/** returned wrapped value for position if it goes beyond the beginning or end of the cipher text */ 
	public static int wrap(int pos) {
		while (pos < 0) pos += Settings.cipher.length(); 
		return pos % Settings.cipher.length(); 
	}

	public static String wrapSubstring(StringBuffer sb, int beg, int end) {
		StringBuffer sub = new StringBuffer();
		for (int i=beg; i<end; i++) {
			sub.append(sb.charAt(wrap(i)));
		}
		return sub.toString();
	}
	
	public static void testWrapSubstring() {
		Settings.cipher = new StringBuffer("HelloMyNameIsDave");
		StringBuffer sb = new StringBuffer("HelloMyNameIsDave");
		for (int i=-10; i<100; i++) 
			System.out.println(i+": " + wrapSubstring(sb, i, i+5));
	}
	
	public static void adjustGenomeForWord(int[] genome, String word, int pos) {
		for (int i=0; i<word.length(); i++) {
			char c = word.charAt(i); // plaintext
			char s = Settings.cipher.charAt(wrap(pos+i)); // ciphertext
			int g = -1;
			for (int j=0; j<Scorer.alphabet.length(); j++) {
				if (Scorer.alphabet.charAt(j) == s) {
					g = j;
					break;
				}
			}
			if (g == -1) {
				System.out.println("wtf");
			}
			else {
				genome[g] = ((int) c) - 97;
			}
		}
		
	}
	
	public static String genomeToString(int[] genome) {
		String s = "";
		for (int i=0; i<genome.length; i++) s += genome[i] + ",";
		return s;
	}
	
	public static void testAdjustGenomeForWord() {
		MersenneTwisterFast rand = new MersenneTwisterFast();
		int[] genome = randomGenome(rand);
		StringBuffer decoded = Scorer.decode(genome);
		System.out.println("g before " + genomeToString(genome));
		System.out.println("d before " + decoded);
		
		//adjustGenomeForWord(genome, "fuckballs", 100);
		adjustGenomeForWord(genome, "wueglgituaguwgsorecywbesfgheoinenifaahcwtneilentgweteguaiurudheannilnitaecbgwoeesshriwffeyhteltntutwfeedgehrtcnnhethtetnigeetsthebiinuuahlfhefewhgelnitweteiusehyattoeibaclheggwflswnednuybfwiurcetaiogstnnaelieuiwunetgefbhrtfgnalwenidtaeltetieaawetgwasgcwgenhntdrioyaitruufitaenhegwuulraeetgeothwhrceehsswawtugcluihsriehrnnhwgloreferceawuenwrbgcttwttaiiaanghhroieeyolegutncurhlsncwhrenfsttenwygettwwfyhebiehifa", 0);
		decoded = Scorer.decode(genome);

		System.out.println(" g after " + genomeToString(genome));
		System.out.println(" d after " + decoded);

		adjustGenomeForWord(genome, "ueglgw", 0);
		decoded = Scorer.decode(genome);

		System.out.println(" g after " + genomeToString(genome));
		System.out.println(" d after " + decoded);
	
	}
	
	/** pick word and position at random, try to derive new genome based on injected word.
	 * half the time, inject a blank word (all wildcards) */ 
	public static void mutateWord(MersenneTwisterFast random, int[] genome) {
		//IntegerVectorSpecies ss = (IntegerVectorSpecies) species;
		//int repeat = state.random[thread].nextInt(8) + 1;
		int repeat = 1;
		for (int rr=0; rr<repeat; rr++) {
			//if (!state.random[thread].nextBoolean(ss.mutationProbability)) continue;
			int L = 4+random.nextInt(7);
			// draw pos from [0, genome.length-L]
			int pos = random.nextInt(Settings.cipher.length()-L+1);
			int a = random.nextInt(Settings.alpha.length);
			
			boolean blank = random.nextBoolean();
			if (blank) {
				//mutationCountBlank++;
				for (int i=0; i<L; i++) {
					char s = Settings.cipher.charAt(pos+i); // ciphertext
					int g = -1;
					for (int j=0; j<Scorer.alphabet.length(); j++) {
						if (Scorer.alphabet.charAt(j) == s) {
							g = j;
							break;
						}
					}
					if (g == -1) {
						System.out.println("wtf");
					}
					else {
						genome[g] = 26;
					}
					
				}
				continue;
			} else {
				//mutationCountWord++;
			}
			
			String query = "+word:"+Settings.alpha[a];
			for (int i=0; i<L-1; i++) query += "?";
			Results r = LuceneService.query(query, Scorer.sortScore, 1000);
			String word = null;
			if (r.docs != null && r.docs.size() > 0) {
				//word = LuceneService.value(r.docs.get(state.random[thread].nextInt(r.docs.size())), "word");
				word = LuceneService.value(randomWord(random, r.docs),"word"); //r.docs.get(state.random[thread].nextInt(r.docs.size())), "word");
				//mutationWords += word + " " + pos + " ";
				adjustGenomeForWord(genome, word, pos);
			}
			//System.out.println("word " + word + " pos " + pos + " L " + L + " a " + a);
			
		}
		
	}
	
	/** pick word and position at random, try to derive new genome based on injected word.
	 * use some of the decoded letters to filter the list of words.
	 */ 
	public static void mutateWordPartial(MersenneTwisterFast random, int[] genome) {
		//IntegerVectorSpecies ss = (IntegerVectorSpecies) species;
		//int repeat = state.random[thread].nextInt(8) + 1;
		int repeat = 1;
		for (int rr=0; rr<repeat; rr++) {
			//if (!state.random[thread].nextBoolean(ss.mutationProbability)) continue;
			int L = 3+random.nextInt(8);
			// draw pos from [0, genome.length-L]
			int pos = random.nextInt(Settings.cipher.length()-L+1);
			
			StringBuffer decoded = Scorer.decode(genome);
			StringBuffer chunk = new StringBuffer(decoded.substring(pos,pos+L)); 
			// reset at most L/2 symbols 
			int n = 1 + random.nextInt(L/2);
			Set<Integer> seen = new HashSet<Integer>();
			for (int m=0; m<n; m++) {
				int which = random.nextInt(L);
				while (seen.contains(which)) which = random.nextInt(L);
				seen.add(which);
				chunk.setCharAt(which, '?');
			}
			
			String query = "+word:"+chunk;
			Results r = LuceneService.query(query, Scorer.sortScore, 1000);
			String word = null;
			if (r.docs != null && r.docs.size() > 0) {
				word = LuceneService.value(randomWord(random, r.docs),"word"); //r.docs.get(state.random[thread].nextInt(r.docs.size())), "word");
				adjustGenomeForWord(genome, word, pos);
			}
			//System.out.println("query " + query + " word " + word + " pos " + pos + " L " + L + " chunk " + chunk + " n " + n);
			//StringBuffer sb = Scorer.decode(genome);
			//Scorer.stringCompare(decoded, sb);
			
		}
		
	}
	
	
	/** use dictionary to select a word that fits a random portion of the cipher text.
	 * select random word, proportional to their scores.
	 * derive new genome based on the word match. 
	 * @param state
	 * @param thread
	 */
	public static void mutateDictionary(MersenneTwisterFast random, int[] genome) {
		StringBuffer decoded = Scorer.decode(genome);
		if (decoded == null) return;

		int L = 4+random.nextInt(7);
		// draw pos from [0, genome.length-L]
		int pos = random.nextInt(Settings.cipher.length()-L+1);
		
		String query = decoded.substring(pos,pos+L);
		//System.out.println("selected [" + query + "]");
		
		Results r = LuceneService.query("word:" + query, Scorer.sortScore, 100);
		if (r.docs == null || r.docs.size() == 0) {
			//System.out.println("no matches");
			return;
		}
		
		String word = LuceneService.value(randomWord(random, r.docs), "word");

		adjustGenomeForWord(genome, word, pos);
		//mutationWords = word;
		
	}
	
	/** select random word from list of Documents, proportionally weighted by scores */
	public static Document randomWord(MersenneTwisterFast random, List<Document> docs) {
		//System.out.println("random word");
		Document d = null;
		if (docs == null || docs.size() == 0) return d;
		
		float sum = 0;
		int c = 0;
		for (Document doc : docs) {
			Float val = Float.valueOf(LuceneService.value(doc, "score"));
			if (val != null) sum += val;
			//System.out.println(c + ": " + LuceneService.value(doc, "word") + ", " + val);
			c++;
		}
		
		float dart = random.nextFloat()*sum;  // [0,sum)
		//System.out.println("sum: " + sum + ", dart: " + dart);
		sum = 0; c = 0;
		for (Document doc : docs) {
			Float val = Float.valueOf(LuceneService.value(doc, "score"));
			if (val != null) sum += val;
			//System.out.println("looking " + c + ": " + LuceneService.value(doc, "word") + ", " + val);
			if (sum > dart) {
				//System.out.println("THIS ONE");
				return doc;
			}
			c++;
		}
		//System.out.println("Got to the end of the list, returning last word.");
		return docs.get(docs.size()-1);
	}

	/** frequency-proportional selection of genome mutations */
	public static void mutateWithFreq(float prob, MersenneTwisterFast random, int[] genome) {
		//IntegerVectorSpecies s = (IntegerVectorSpecies) species;
		if (prob > 0.0)
			for (int x = 0; x < genome.length; x++)
				if (random.nextBoolean(prob))
					genome[x] = randomLetterPos(random);
	}    
	
	/** swap two chunks of plain text.  the chunk has a random length. */
	public static void mutateSwap(MersenneTwisterFast rand, int[] genome) {
		int L = rand.nextInt(Settings.cipher.length());
		StringBuffer decoded = Scorer.decode(genome);
		int p1 = rand.nextInt(Settings.cipher.length());
		int p2 = rand.nextInt(Settings.cipher.length());
		
		String chunk1 = wrapSubstring(decoded, p1, p1+L);
		String chunk2 = wrapSubstring(decoded, p2, p2+L);
		
		//System.out.println(L+","+p1+","+p2+","+chunk1+","+chunk2);
		//System.out.println("before: " + decoded);
		
		adjustGenomeForWord(genome, chunk2, p1);
		adjustGenomeForWord(genome, chunk1, p2);
		//decoded = Scorer.decode(genome);
		//System.out.println(" after: " + decoded);
	}    

	/** shift a chunk of plain text */
	public static void mutateShift(MersenneTwisterFast rand, int[] genome) {
		int L = rand.nextInt(Settings.cipher.length());
		StringBuffer decoded = Scorer.decode(genome);
		int p1 = rand.nextInt(Settings.cipher.length()); // old pos
		int p2 = rand.nextInt(Settings.cipher.length()); // new pos
		
		if (p1+L>decoded.length()) return;
		if (p2+L>decoded.length()) return;
		
		String chunk1 = decoded.substring(p1, p1+L);

		StringBuffer chunk2 = new StringBuffer(decoded); 
		for (int i=p1; i<p1+L; i++) 
			chunk2.setCharAt(i, '*');
		
		chunk2.insert(p2, chunk1);
		for (int i=chunk2.length()-1; i>=0; i--)
			if (chunk2.charAt(i) == '*') chunk2.deleteCharAt(i);

		//System.out.println(L+","+p1+","+p2+","+chunk1+","+chunk2);
		//System.out.println("before: " + decoded);
		//System.out.println(decoded.equals(chunk2));
		adjustGenomeForWord(genome, chunk2.toString(), 0);
		adjustGenomeForWord(genome, chunk1, p2);
		//decoded = Scorer.decode(genome);
		//System.out.println(" after: " + decoded);
	}    

	/** scramble a chunk of plain text */
	public static void mutateScramble(MersenneTwisterFast rand, int[] genome) {
		int L = rand.nextInt(Settings.cipher.length());
		StringBuffer decoded = Scorer.decode(genome);
		int pos = rand.nextInt(Settings.cipher.length());
		if (pos+L>decoded.length()) return;
		String chunk = decoded.substring(pos,pos+L);
		float[][] f = new float[L][2];
		for (int i=0; i<L; i++) {
			f[i][0] = rand.nextFloat();
			f[i][1] = i;
		}
	    //System.out.println("unsorted");
		/*for (int i=0; i<L; i++) {
			System.out.println(f[i][0]+","+f[i][1]);
		}*/
	    Arrays.sort(f, new Comparator<float[]>() {

			public int compare(float[] arg0, float[] arg1) {
				Float f1 = arg0[0];
				Float f2 = arg1[0];
				return f1.compareTo(f2);
			}
        });
	    /*System.out.println("sorted");
		for (int i=0; i<L; i++) {
			System.out.println(f[i][0]+","+f[i][1]);
		}
		*/
		StringBuffer sb = new StringBuffer();
		for (int i=0; i<L; i++) {
			sb.append(chunk.charAt((int) f[i][1]));
		}
		//System.out.println("chunk " + chunk + " sb " + sb);
		
		adjustGenomeForWord(genome, sb.toString(), pos);
		/*System.out.println("before: " + decoded);
		decoded = Scorer.decode(genome);
		System.out.println(" after: " + decoded);*/
	}    
	
	/** reverse a chunk of plain text */
	public static void mutateReverse(MersenneTwisterFast rand, int[] genome) {
		int L = rand.nextInt(Settings.cipher.length());
		StringBuffer decoded = Scorer.decode(genome);
		int pos = rand.nextInt(Settings.cipher.length());
		if (pos+L>decoded.length()) return;
		StringBuffer chunk = new StringBuffer(decoded.substring(pos,pos+L));
		//System.out.println(chunk);
		chunk.reverse();
		//System.out.println(chunk);
		
		adjustGenomeForWord(genome, chunk.toString(), pos);
		//System.out.println("before: " + decoded);
		//decoded = Scorer.decode(genome);
		//System.out.println(" after: " + decoded);
	}    
	public static void testMutateShift() {
		MersenneTwisterFast rand = new MersenneTwisterFast();
		
		for (int i=0; i<100; i++) {
			int[] genome = randomGenome(rand);
			mutateShift(rand, genome);
		}
	}
	
	/** swap a pair of genomes once.  every once in a while, swap up to 10 pairs. */
	public static void mutateSwapGenome(MersenneTwisterFast random, int[] genome) {
		int n;
		if (random.nextBoolean(Settings.PROB_SWAP_ONE)) n = 1;
		else n = random.nextInt(11);
		
		for (int i=0; i<n; i++) {
			int a = random.nextInt(genome.length);
			int b = a;
			while (a==b || genome[a] == genome[b]) {
				b = random.nextInt(genome.length);
			}
			int tmp = genome[a];
			genome[a] = genome[b];
			genome[b] = tmp;
		}
	}
	
	public static void testMutateScramble() {
		MersenneTwisterFast rand = new MersenneTwisterFast();
		
		for (int i=0; i<100; i++) {
			int[] genome = randomGenome(rand);
			mutateScramble(rand, genome);
		}
	}
	public static void testMutateReverse() {
		MersenneTwisterFast rand = new MersenneTwisterFast();
		
		for (int i=0; i<100; i++) {
			int[] genome = randomGenome(rand);
			mutateReverse(rand, genome);
		}
	}
	public static int[] randomGenome(MersenneTwisterFast rand) {
		int[] genome = new int[Settings.GENOME_SIZE];
		for (int i=0; i<genome.length; i++) genome[i] = randomLetterPos(rand);
		return genome;
	}
	public static void testMutateSwap() {
		MersenneTwisterFast rand = new MersenneTwisterFast();
		
		for (int i=0; i<100; i++) {
			int[] genome = randomGenome(rand);
			mutateSwap(rand, genome);
		}
	}
	/*
	@Override
	public void defaultCrossover(EvolutionState state, int thread,
			VectorIndividual ind) {
		System.out.println("CROSS");
		super.defaultCrossover(state, thread, ind);
	}*/
	
	/** frequency-proportional selection of random letter of the alphabet */
	public static String randomLetter(MersenneTwisterFast rand) {
		return Settings.alpha[randomLetterPos(rand)];
	}
	
	public static int randomLetterPos(MersenneTwisterFast rand) {
		float dart = rand.nextFloat();
		float sum = 0;
		for (int i=0; i<Settings.freqs.length; i++) {
			sum += Settings.freqs[i];
			if (dart < sum) return i;
		}
		return Settings.alpha.length-1;
	}
	
	public static void testRandomLetter() {
		MersenneTwisterFast rand = new MersenneTwisterFast();
		String s1 = "";
		String s2 = "";
		for (int i=0; i<1000; i++) {
			s1 += randomLetter(rand);
			s2 += Settings.alpha[rand.nextInt(Settings.alpha.length)];
			//System.out.println(s);
		}
		System.out.println(s1);
		System.out.println(NGrams.diffs(new StringBuffer(s1)));
		System.out.println(s2);
		System.out.println(NGrams.diffs(new StringBuffer(s2)));
	}
	
	/** init using the ZKD method */
	public void reset(EvolutionState state, int thread) {
		NGrams.genomeInitKey(genome);
	}
	
	public void reset2(EvolutionState state, int thread) {
		/*IntegerVectorSpecies s = (IntegerVectorSpecies) species;
		for (int x = 0; x < genome.length; x++)
			genome[x] = randomValueFromClosedInterval((int) s.minGene(x),
					(int) s.maxGene(x), state.random[thread]);
		*/
		
		
		List<String> symbols = new ArrayList<String>();
		for (int i=0; i<Scorer.alphabet.length(); i++) symbols.add(Scorer.alphabet.substring(i,i+1));
		String msg = "Resetting [" + this.hashCode() + "] ";
		// try to assign plaintext letters to symbols based on expected english letter frequencies
		for (int i=0; i<Settings.alpha.length; i++) {
			int expected = (int)(Settings.freqs[i]*Settings.cipher.length());
			msg += Settings.alpha[i] + " " + expected + " (";
			int sum = 0;
			while (sum < expected && !symbols.isEmpty()) {
				// pick a symbol at random
				int j = state.random[thread].nextInt(symbols.size());
				String s = symbols.get(j);
				symbols.remove(j);
				sum += Scorer.symbolCounts.get(s.charAt(0));
				genome[Scorer.genomePosFor(s)] = i;
				msg += s + " " + Scorer.symbolCounts.get(s.charAt(0)) + " gp " + Scorer.genomePosFor(s) + " ";
			}
			msg += ") " + sum + "; ";
		}
		System.out.println(msg);
	}

	
	
	/*
	public void reset(EvolutionState state, int thread) {
		int[] g = new int[] {21,15,3,0,0,8,4,8,0,4,21,19,4,24,8,18,14,4,11,19,17,12,18,7,19,0,7,17,13,19,7,18,18,24,18,18,13,10,14,3,11,22,17,14,14,7,2,20,0,8,11,2,1,18,18,4,4,11,8,2,13,0,15,20,19};
		genome = g.clone();
	}*/
	
	
	public static void main(String[]args) {
		//testRandomLetter();
		//testWrapSubstring();
		//testMutateReverse();
		//testAdjustGenomeForWord();
	}

	
	

	
}
