package com.zodiackillerciphers.old;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.regex.*;
import java.text.*;

import com.zodiackillerciphers.ciphers.Ciphers;

import ec.EvolutionState;
import ec.multiobjective.MultiObjectiveFitness;
import ec.util.Code;
import ec.util.MersenneTwister;
import ec.util.Output;
import ec.vector.*;

import gnu.trove.*;

public class CipherGene extends IntegerVectorIndividual implements ZodiacIndividual {
	//public static String[] tempPlug = { "killing","people","because","itis","itis","killing","because","isthe","something","collecting" }; // 10-word plugger with repeated words
	//public static String[] tempPlug = { "killing","people","because","itis","isthe","something","collecting","slaves","fun","more","after" }; // 11-word plugger with no repeated words

	/* store the sorted key; used for fitness sharing analysis */
	public String sortedKey;
	
	public static String resetDecoder; // generated at one time for re-use
	public static String resetDecoded; // generated at one time for re-use
	
	/** this is true if we marked this guy as elite */
	public boolean elite = false;
	
	/** keeper of statistics */
	public static THashMap<String, Object> stats = new THashMap<String, Object>();
	
	/** string to track word overlap */
	public StringBuffer conflictOverlap;
	
	public static final boolean FITNESS_WORD_SIMPLE_CONFLICT_RATIO = false; // alternate fitness scheme: (word length sum + number of non-? positions in decoder) / (1 + number of conflicts). 
	public static int MIN_WORD_LENGTH_SUM = 60; // if the sum of plugger word lengths is below this, give the gene zero fitness
	public static int MIN_WORD_COUNT = 7; // if a gene's plugger word count is below this, give the gene zero fitness 
	public double shitness;
	public double fitnessAlpha;
	public double fitnessAlphaFreq;
	//public double fitnesscommonWords;
	public double fitnesscommonWordsUnique;
	public double[] fitnessNGrams = new double[10];
	public int[] badNGramCount = new int[10];
	public static float badNGramMin = 0.000065f;
	//public double fitnessDigraph;
	//public double fitnessDigraphEuclidian;
	public String foundDigraphs;
	//public double fitnessDouble;
	public String foundDoubles;
	//public double fitnessTrigraph;
	public String foundTrigraphs;
	//public double fitness2graph;
	//public double fitness3graph;
	//public double fitness4graph;
	//public double fitnessDoubles;
	public double fitnessWord;
	public String corpusGenome;
	public String corpusUnclobbered;
	public int fitnessDictionaryWords;
	public int fitnessDictionaryWordsInteresting;
	public int fitnessZodiacWords;
	public int fitnessZodiacWordsFuzzy;
	public int fitnessZodiacWordsPairings;
	public double fitnessWordLengthDistribution;
	public double phiObserved;
	public double phiPlaintext;
	public double phiRandom;
	public double phiRandomMatch;
	public double phiPlaintextMatch;

	public double fitnessCoverageZodiac;
	public double fitnessCoverageDictionary;
	public double fitnessCoverageDictionaryScaled;
	public String coverageZodiac; //obsolete
	public String coverageDictionary;
	public String coverageDictionaryScaled;
	public String conflictInfo;
	public String[] sentence;
	/* allele type: decoder */
	public StringBuffer decoder;
	protected StringBuffer decoded; // cached decoded string
	protected StringBuffer decodedSubstringSpaced; // with spaces, to indicate word boundaries
	public StringBuffer getDecoded() { return decoded; }
	public String[] decoders; // multiple decoders, for word encoding.  used to compare decoder-clobber conflicts.
	//final private int[] genome; // used for Word encodings; fitnessWord is still passed a genome externally.
	public int[] getGenome() { return genome; }
	public int[] plugger; // local plugger, used for (word,pos) pair encoding.
	public String encoding; // word/position tuples for word_encoding 1 (used for debugging).
	public String unclobberedWords; // tracks every non-conflicting word for word_encoding 1
	public int totalConflicts; // the number of decoder conflicts detected for the given set of words.
	public int totalWildcardConflicts; // the number of wildcards detected in the decoder.
	/* allele type: words */
	public THashMap decoderHash;
	public String words;
	public THashMap wordIndex = new THashMap();


	public String ourLetterFreqs;
	public String ourDigraphFreqs;

	public String foundWords = "";
	public StringBuffer foundWordsDictionary = new StringBuffer();
	public String foundWordsDictionaryInteresting = "";
	public String foundWordsZodiac = "";
	public String foundWordsZodiacPairings = "";

	public int zodiacScore;
	public float clusterCount; // when doing fitness sharing, this is updated with the # of times this gene appears in the population.  it is fractional if we do a more sensitive difference computation.
	public double zodiacMatch;
	public String zodiacWords = "";
	/* other words found outside of the word slider */
	public String zodiacWordsOther;
	/* fuzzily-matched words we found */
	public String zodiacWordsFuzzy = "";
	public static MersenneTwister _rand = null;
	
	public static THashSet zodiacWordHash = null;

	/** track the start pos of each word in the decoded substring; we do this to avoid sorting */
	public boolean[] conflictWordStarts;
  /** track the end pos of each word in the decoded substring; we do this to avoid sorting */
	public boolean[] conflictWordEnds;

	
	/* stuff from ZodVectorWordInd */
	public static int NUM_TRIES = 1000;
	protected int makeCount = 0;
	public static final boolean TRACK_SUCCESS_RATES = false; // when true, xover/mutation operators track how often they produce an equal or better individual (warning: does not work for multiobjective)
	public static final boolean RESET_SUCCESS_RATES_EACH_GEN = true; // when true, xover/mutation success rates are reset each gen (instead of going for a running average) 
	public static int xoverTotal = 0;
	public static int xoverGood = 0;
	public static int mutationTotal = 0;
	public static int mutationGood = 0;
	
	
	/*
	private CipherGene() { // enforce construction with genome
		throw new RuntimeException("Sorry, use CipherGene(genome) instead.");
	}*/
	
	public CipherGene() {
		resetDecoder();
	}
	/** construction of a CipherGene via int[] genome, for word/position pair encoding */
	public CipherGene(final int[] genome) {
		if (genome == null || genome.length == 0 || genome.length % 2 != 0) {
			throw new RuntimeException("Sorry, you must supply a non-empty genome with an even number of alleles.");
		}
		
		//this.genome = new int[genome.length];
		this.genome = genome;
		for (int i=0; i<genome.length; i++) this.genome[i] = genome[i];
		//resetDecoder(); // make the decoder all ?'s for now.  
		//makeDecodersFromGenome(); // an array of decoders is made.  each item is a decoder corresponding to a word/position pair
		  // in the genome.  the final decoder is derived from these decoders.  the final decoder is not computed
		  // until it is needed by the fitness function.
		//fitnessWordSimple();
	}
	
	/** direct construction of a CipherGene via decoder.  pass an empty or null string to force reset decoder. */
	public CipherGene(String d) {
		//int[] newGenome = new
		genome = null;
		if (d == null || d.equals("")) { resetDecoder(); return; }
		decoder = new StringBuffer(d);
	}
	
	
	protected void makeDecodersFromGenome() {
  	String word;
  	int pos;
  	String newDecoder;

  	int[] g = null;
  	int[] plugger = null;
  	if (Zodiac.WORD_ENCODING == 1) { // word/pos pairs
  		g = new int[genome.length/2];
  		plugger = new int[genome.length/2];
  		for (int i=0; i<genome.length; i+=2) {
  			plugger[i/2] = genome[i];
  			g[i/2] = genome[i+1];
  		}
  	}
  	else {
  		g = genome;
  	}
		decoders = new String[g.length];
  	
		//System.out.println("decoders len " + gene.decoders.length);
		int ind;
		
  	for (int x=0; x<g.length; x++) {
  		//ind = (Zodiac.WORD_ENCODING == 1 ? x/2 : x);
  		//ind = x;
  		decoders[x] = decoder.toString();
  		if (Zodiac.WORD_ENCODING == 1) {
  			word = CipherGene.getWordForGene(plugger[x]);
  			/* quick hardcoded test */
  			//word = CipherGene.tempPlug[x];
  			
  			//pos = Gene.getPositionForGene(g[x]);
  		} else {
  			word = Zodiac.PLUGGER[x];
  			//pos = Gene.getPositionForGene(g[x]);
  		}
			pos = CipherGene.getPositionForGene(g[x]);
  		if (!"".equals(word)) {
	  		//if (pos+word.length() < Zodiac.cipher[Zodiac.CIPHER].length()) { // ignore genes that cause words to spill over the end of the cipher
	  			decoders[x] = Zodiac.getDecoderForWordAtPosition(decoder.toString(), word, pos, false);
	  		//} else g[x] = 0; // kill the word, since it causes a spillover at the end of the ciphertext.
  		} 
  		//System.out.println("x/2 " + (x/2) + " d " + gene.decoders[x/2]);
  	}
  	
  	encoding = CipherGene.getEncodingFromGenome(g, plugger);
		
	}
	
	
	public static MersenneTwister rand() {
		if (_rand == null) 
			_rand = new MersenneTwister(new Date().getTime());
		
		return _rand;
	}
	
	public static String getLetter() {
		return "" + (char)(97+rand().nextInt(26));
	}
	
	/** unused; conflicts with word-pairing genome idea 
	public int[] getGenome() {
		int[] result = new int[decoder.length()];
		for (int i=0; i<decoder.length(); i++) result[i] = decoder.charAt(i);
		return result;
	}*/
	
	
	/** experiment 142 fitness: weighted sum */
	public float fitnessWeightedSum() {
    float f = 1f;
    int count = 0;
  	THashSet<String> words = new THashSet<String>();
  	THashMap<String,ArrayList<Integer>> hash;
  	
  	StringBuffer d = new StringBuffer(getDecoded());
  	for (int i=7; i>2; i--) {
  		hash = ZodiacDictionary.getWords(d, i, ZodiacDictionary.D_ZODIAC, true);
  		words.addAll(hash.keySet());
  		count += i*ZodiacDictionary.countDistinctWordsFromIndex(hash);
  	}
    shitness = count;
    fitnessWord = count;
    
    foundWordsDictionary = new StringBuffer();
    Iterator<String> it = words.iterator();
    while (it.hasNext()) {
    	foundWordsDictionary.append(it.next() + " ");
    }
    return (float)shitness;
		
	}
	

	/* randomize a new decoder */
	public void randomize() {
		if (Zodiac.ENCODING == 0) {
			decoder = new StringBuffer("");
			for (int i=0; i<Zodiac.alphabet[Zodiac.CIPHER].length(); i++) {
				decoder.append(getLetter());
			}
		} else {/*
			resetDecoder();
			String word;
			for (int i=0; i<rand().nextInt(7)+1; i++) {
				word = getWord(1,100,true); // 70% interesting words as seeds
				//say("word " + word);
				wordAdd(word, rand().nextInt(Zodiac.cipher[Zodiac.CIPHER].length()-word.length()));
				//say("new decoded " + decode());
			}*/
		}
	}
	
	/* get a random word from the zodiac dictionary.  restrict word length to given bounds, inclusive.
	 * if interesting is true, then return "interesting" words 70% of the time. */ 
	public static String getWord(int lenMin, int lenMax, boolean interesting) {
		String word = "";
		int chance = rand().nextInt(10);
		while (word.length() < lenMin || word.length() > lenMax) {
			if (chance < 7 && interesting) { // 70% of the time, focus on "interesting" words
				word = ZodiacDictionary.zodiacWordsInteresting[rand().nextInt(ZodiacDictionary.zodiacWordsInteresting.length)];
			}
			else { // 30% of the time, focus on all words that appeared in zodiac letters 
				word = ZodiacDictionary.zodiacWords[rand().nextInt(ZodiacDictionary.zodiacWords.length)];
			}
		}
		return word;
	}
	

	protected void resetDecoder() {
		if (resetDecoder == null) {
			decoder = new StringBuffer("");
			for (int i=0; i<Zodiac.alphabet[Zodiac.CIPHER].length(); i++) {
				decoder.append("?"); // wildcard
			}
			decoded = new StringBuffer("");
			for (int i=0; i<Ciphers.cipher[Zodiac.CIPHER].cipher.length(); i++) {
				decoded.append("?"); // wildcard
			}
			
			resetDecoder = decoder.toString();
			resetDecoded = decoded.toString();
		} else {
			decoder = new StringBuffer(resetDecoder);
			decoded = new StringBuffer(resetDecoded);
		}
	}

	/*
	public void resetDecoderFromWords() {
		resetDecoder();
		String w;
		for (int i=0; i<Zodiac.cipher[Zodiac.CIPHER].length(); i++) {
			w = (String) wordIndex.get(new Integer(i));
			if (w != null) {
				decoder = getDecoderForWordAtPosition(decoder, w, i); 
			}
		}

	}*/

	public String decode() {
		return decode(0, Zodiac.cipher[Zodiac.CIPHER].length()-1);
	}
	/* decode the cipher (from start position to end position, inclusive) with this genome's decoder */
	public String decode(int start, int end) {
		//if (decoderHash == null) initDecoderHash();

		String result = "";
		char letter;
		//String letter;
		char dchar;
		for (int i=start; i<=end; i++) {
			//letter = ""+Zodiac.cipher[Zodiac.CIPHER].charAt(i);
			letter = Zodiac.cipher[Zodiac.CIPHER].charAt(i);
			//say("letter " + letter);
			dchar = decoder.charAt(Zodiac.alphabet[Zodiac.CIPHER].indexOf(letter));
			result += dchar;
			//result += decoderHash.get(letter);
		}

		/*
		if (result.length() != Zodiac.cipher[Zodiac.CIPHER].length()) {
			write("ERROR ");
			debug();
			throw new RuntimeException("The decoded text length " + result.length() + " does not match the cipher text length " + Zodiac.cipher[Zodiac.CIPHER].length());
		}*/
		if (decoder.length() != Zodiac.alphabet[Zodiac.CIPHER].length()) {
			write("ERROR ");
			debug();
			throw new RuntimeException("The decoder length " + result.length() + " does not match the cipher alphabet length " + Zodiac.alphabet[Zodiac.CIPHER].length());
		}
		decoded = new StringBuffer(result);
		return result;
	}


	public THashMap wordIndexCopy() {
		if (wordIndex == null) return null;
		THashMap copy = new THashMap();
		Iterator it = wordIndex.keySet().iterator();
		Integer key;
		String value;

		while (it.hasNext()) {
			key = (Integer)it.next();
			copy.put(new Integer(key.intValue()), wordIndex.get(key));
		}
		return copy;
	}

	public int wordCount() {
		if (wordIndex == null) return 0;
		else return wordIndex.size();
	}

	/* word clobber: the proportion of placed words that appear in the decoded ciper unaltered */ 
	public double wordClobber() {
		if (wordIndex.size() == 0) return 0;
		double result = 0;
		int count = 0;
		String d = decode();

		Integer key;
		String value;
		int index;

		Iterator it = wordIndex.keySet().iterator();
		while (it.hasNext()) {
			key = (Integer) it.next();
			index = key.intValue();
			value = (String) wordIndex.get(key);
			try {
				if (value.equals(d.substring(index, index+value.length()))) count += 1;
			} catch (java.lang.StringIndexOutOfBoundsException ex) {
				; //swallow
			}
		}

		return (double)count/wordIndex.size();
	}

	public String wordAt(int i) {
		return (String) wordIndex.get(new Integer(i));
	}

	/* returns true if the give word will fit at this spot */
	public boolean wordFits(String word, int where) {
		for (int i=0; i<word.length(); i++) {
			if (wordAt(where+i) != null)
				return false;
		}
		if (where == 0) return true;

		int prev = wordPrevious(where);
		String w = wordAt(prev);
		if (w == null) return true;
		if (prev+w.length()>where) return false;

		return true;
	}

	/* adds a word to the ciphertext, but only if there is room (returns false if there is no room) */
/*	public boolean wordAdd(String word, int where) {
		if (!wordFits(word,where)) return false;
		wordIndex.put(new Integer(where), word);
		//say("adding word " + word + " at " + where);
		decoder = getDecoderForWordAtPosition(decoder, word, where);

		//say("hmm " + wordAt(where));
		return true;
	}*/

	/* removes a word from the word index */
	/*public void wordRemove(int where) {
		wordIndex.remove(new Integer(where));
		resetDecoderFromWords();
	}*/

	/* shifts a word left one position in the ciphertext (if there is room) */
/*	public boolean wordShiftLeft(int where) {
		if (where == 0) return false;
		String w = wordAt(where);
		if (w == null) return false;
		int prev = wordPrevious(where);
		String prevWord = wordAt(prev);
		if (prevWord == null || prev+prevWord.length()<where) {
			wordRemove(where);
			wordAdd(w, where-1);
			return true;
		} else return false;
	}
*/
	
	/* shifts a word right one position in the ciphertext (if there is room) */
/*	public boolean wordShiftRight(int where) {
		String w = wordAt(where);
		if (w == null) return false;
		if (where+w.length() >= Zodiac.cipher[Zodiac.CIPHER].length()) return false;
		if (wordAt(where+w.length()) != null) return false; 
		wordRemove(where);
		wordAdd(w, where+1);
		return true;
	}
*/
	
	/* get index of previous word; -1 if none found */
	public int wordPrevious(int where) {
		if (where == 0) return -1;
		for (int i=where-1; i>=0; i--) {
			if (wordAt(i) != null) return i;
		}
		return -1;
	}

	/* get index of next word; -1 if none found */
	public int wordNext(int where) {
		if (where == Zodiac.cipher[Zodiac.CIPHER].length()-1) return -1;
		for (int i=where+1; i<=Zodiac.cipher[Zodiac.CIPHER].length()-1; i++) {
			if (wordAt(i) != null) return i;
		}
		return -1;
	}

	/* count how many empty spaces are to the left of this word */
	public int wordSpacesToLeft(int where) {
		if (where == 0) return 0;
		int prev = wordPrevious(where);
		if (prev == -1) return where;
		String word = wordAt(prev);
		return where-prev-word.length();
	}

	/* count how many empty spaces are to the right of this word */
	public int wordSpacesToRight(int where) {
		if (where == Zodiac.cipher[Zodiac.CIPHER].length()-1) return 0;
		int next = wordNext(where);
		String word = wordAt(where);
		if (next == -1) return Zodiac.cipher[Zodiac.CIPHER].length()-1-word.length();
		return next-where-word.length();
	}

	/* get random word from the word index */
	public int wordRandom() {
		Object[] keys = wordIndex.keySet().toArray();
		if (keys == null || keys.length == 0) return -1;
		return ((Integer)keys[rand().nextInt(keys.length)]).intValue();
	}

	/* debug info - dump all words */
	public String wordDebugAll() {
		StringBuffer result = new StringBuffer();
		for (int i=0; i<Zodiac.cipher[Zodiac.CIPHER].length(); i++) result.append(".");
		Iterator it = wordIndex.keySet().iterator();
		String value;
		Integer key;
		while (it.hasNext()) {
			key = (Integer)it.next();
			value = (String)wordIndex.get(key);
			//say("key " + key.intValue() + " val " + value);
			result.replace(key.intValue(), key.intValue()+value.length(), value);
		}
		return result.toString();
	}

	/** always call this fitness evaluator.  never call the other ones directly. */
	public void fitness() {
		resetDecoder();
		makeDecodersFromGenome();

		
    MultiObjectiveFitness fit = (MultiObjectiveFitness) fitness;
		
    
    if (fit == null) {
    	fit = new MultiObjectiveFitness();
    }
    //if (fit.multifitness == null) fit.multifitness = new float[2];
    
		//fit.multifitness[0] = fitnessWordSimple();
	if (1==1) throw new RuntimeException("FIX THIS");

		//fit.multifitness[1] = fitnessWeightedSum();

		preserveUnshared(); // keep copies of fitness values before fitness sharing is applied.
		
		evaluated = true;
		//throw new RuntimeException("Sorry, we are running CipherWordGene fitness right now.");
	}
	
	/** keep a copy of Fitness to show its value before fitness sharing is applied. */
	public void preserveUnshared() {
    MultiObjectiveFitness fit = (MultiObjectiveFitness) fitness;
		MultiObjectiveFitness fitU = new MultiObjectiveFitness();
		/*fitU.multifitness = new float[fit.multifitness.length];
		for (int i=0; i<fit.multifitness.length; i++)
			fitU.multifitness[i] = fit.multifitness[i];
		fitnessUnshared = fitU;*/
		if (1==1) throw new RuntimeException("FIX THIS");
		
	}
	
	/* compute the fitness */
	public void fitnessOld() {
		//say(" decoder after " + decoder);
		shitness = 0;
		//fitness += 
		//double a = fitnessAlphaFreq();
		//double d = fitnessDigraphFreq();
		//fitness2graph = fitnessNgraph(2);
		//fitness3graph = fitnessNgraph(3);
		//fitness4graph = fitnessNgraph(4);
		//fitness += fitnessZodiacDictionary.commonWords1000() * fitnessAlphaFreq();
	  //double comp = commonWords5000();
	  //phiTest();
		//fitness += ((fitnessAlphaFreq() + fitnessAlphaFreq + fitnessDigraphFreq())/3)*fitnesscommonWordsUnique;
	  //fitness = fitnesscommonWordsUnique;
	  //fitness = 1-phiPlaintextMatch;
		//double comp = fitnessAlphaFreq();

		decoded = new StringBuffer(decode());
		
		if (Zodiac.ENCODING == 1) { 
			double clobber = wordClobber();
			double factor;
			if (clobber < 0.75) {
				factor = 0.001;
			}
			else factor = 1.0;
			//fitness = factor * fitnessAlphaFreq * (fitnessZodiacWords() + fitnessZodiacWordsFuzzy());
		}
		//else fitness = fitnessAlphaFreq * (commonWords5000() + fitnessZodiacWords() + fitnessZodiacWordsFuzzy());
		else {
			//phiTest();
			
			/*
			double phiFactor;
			phiTest();
			if (phiPlaintextMatch >= 1)
				phiFactor = 0;
			else if (phiPlaintextMatch > 0.1)
				phiFactor = (1-phiPlaintextMatch)/100;
			else {
				if (phiPlaintextMatch < 0.025)
					phiFactor = (1-phiPlaintextMatch/100); // may need to consider clipping this to 1.0 for values within a "sweet" range.
				else 
					phiFactor = (1-phiPlaintextMatch);
				
			}*/
			
			//System.out.println("faster " + fitnessZodiacWordsFaster(4,8,false));
			//fitnessDictionaryWords(1,10,true,false,true);
			//double coverFactor = (fitnessCoverageZodiac > 0.75 ? fitnessCoverageZodiac : fitnessCoverageZodiac/100.0);
			//commonWords5000();
			//fitnessZodiacWords();
			//fitness = coverFactor * phiFactor * (commonWords5000()/2 + fitnessZodiacWords + 5*fitnessZodiacWordsPairings + fitnessZodiacWordsFuzzy()/2);
			//fitnessAlphaFreq();
			fitnessNGramFreq();
			//fitnessDigraphFreq(30);
			//fitnessDigraphFreqFromSentence();
			//fitnessZodiacWordsFuzzy(4,10);
			//fitnessZodiacWordsPairings();
			//fitness = fitnessAlphaFreq*coverFactor*(fitnessZodiacWords + fitnessZodiacWordsPairings + fitnessZodiacWordsFuzzy()/100.0);
			//fitness = phiFactor*fitnessAlphaFreq*coverFactor*fitnessZodiacWords;
	    //float f1 = (float) (badNGramCount[2] >= 200 ? 0.0 : ((float) 200 - badNGramCount[2])/200.0);
	    //float f2 = (float) (badNGramCount[3] >= 400 ? 0.0 : ((float) 400 - badNGramCount[3])/400.0);
	    //fitness = f1*f2;

			//fitness = fitnessNGrams[1] * fitnessNGrams[2] * fitnessNGrams[3] * f1 * f2 * this.fitnessCoverageDictionaryScaled;
			shitness = fitnessNGrams[1] * fitnessNGrams[2] * fitnessNGrams[3];
		}
		zodiacScore();
	}
	
	
	public void initDecoderHashh() {
		decoderHash = new THashMap();
		String letter;
		for (int i=0; i<Zodiac.alphabet[Zodiac.CIPHER].length(); i++) {
			letter = ""+Zodiac.alphabet[Zodiac.CIPHER].charAt(i);
			decoderHash.put(letter, ""+decoder.charAt(i));
		}
	}
	
	/** compute the word fitness. genome is the list of zodiac words we are trying to fit into the decoder.
	 * word fitness for word_encoding 0 is a metric that shows how far (in percentage terms) we got into the decoded string before
	 * the decoder caused word clobber to occur.  for word_encoding 1, the word fitness measures the total
	 * conflicts caused by each word (how much each word's placement interferes with all the other words in the
	 * genome).
	 * 
	 * TODO: This probably doesn't work right since i've refactored the genome for WORD_ENCODING 1.
	 * 
	 * @param genome the genome representing words we want to test
	 * @param startPosition the location at which to begin the stream of words (not relevent if encoding includes word positions)
	 */
	public void fitnessWord(int startPosition) {
		//initDecoderHash();
		decoded = new StringBuffer(decode());
		String chunk = "";
		corpusGenome = "";
		corpusUnclobbered = "";
		fitnessWord = 0.0;
		String word;
		
		if (Zodiac.WORD_ENCODING == 0) {
			for (int i=0; i<genome.length; i++) {
				word = ZodiacDictionary.zodiacCorpus[genome[i]];
				chunk += word;
				corpusGenome += word + " "; 
			}
			
			/* find the first occurrence of the "best fit"; that is, the decoder that produces the biggest non-clobbering substring
			 * of the targeted set of words.
			 */
			/*int bestCount = 0;		
			this.resetDecoder();
			String reset = decoder;
			String bestDecoder = decoder;
			String bestDecoded = decoded;
			boolean stop = false;
			//boolean[] positions = new boolean[decode().length()];
			//for (int i=0; i<positions.length; i++) { positions[i] = true; }
			//String newDecoder = decoder;
			for (int i=chunk.length(); i>= 1; i--) {
				word = chunk.substring(0,i);
				for (int j=0; j<decoded.length() - chunk.length(); j++) {
					//say("i,j " + i + "," + j);
					//if (positions[j]) {
						decoder = Zodiac.getDecoderForWordAtPosition(reset, word, j);
						decoded = decode();
						if (decoded.indexOf(word) > -1) {
							if (i > bestCount) {
								bestCount = i;
								bestDecoder = decoder;
								bestDecoded = decoded;
								//say("word: " + word + ", decoded " + decoded);
								stop = true;
								break;
							}
					//	} else positions[j] = false;
					}
				}
				if (stop) break;
			}
			decoder = bestDecoder;
			decoded = bestDecoded;
			fitnessWord = (double)bestCount/decoded.length();
			*/
			
			double factor = 1.0;
			for (int i=0; i<chunk.length() && i+startPosition < decoded.length(); i++) {
				if (chunk.charAt(i) == decoded.charAt(i+startPosition)) {
					fitnessWord += factor;
				} else factor = 0.0001; // we have clobber; so, add tiny amounts for any matches to make the metric a bit more continuous.
				if (factor == 1.0) corpusUnclobbered += chunk.charAt(i);
			}
			
			
			fitnessWord = fitnessWord / decoded.length();
			//fitnessDictionaryWords(1, 10, true, false, true);
			//fitnessWord = this.fitnessCoverageDictionaryScaled;
			//fitnessAlphaFreq();
			fitnessNGramFreq();
			int bad = 0;
			for (int i=0; i<4; i++) bad+= badNGramCount[i];
			if (bad >= 200) bad = 200;
			fitnessWord = fitnessWord * (((double)200-bad)/200.0);
			
			
		} else { /* the old fitness computations for word_encoding 1:
			int position;
			resetDecoder();
			String d = decoder;
			String keepDecoder = d;
			String keepDecoded = decoded;
			boolean clobber = false;
			for (int i=0; i<genome.length; i+=2) {
				word = ZodiacDictionary.zodiacCorpus[genome[i]];
				position = Gene.getScaledPosition(genome[i+1]);
				//say("i " + i + " w " + word + " p " + position);
				//chunk += word;
				corpusGenome += "(" + position + ") " + word + " ";
				d = Zodiac.getDecoderForWordAtPosition(d, word, position);
				decoder = d;
				decoded = decode();
				for (int j=position; j<position+word.length(); j++) {
					if (word.charAt(j-position) == decoded.charAt(j)) {
						fitnessWord += (clobber ? 0.0001 : 1.0);
					} else clobber = true;
					if (!clobber) {
						keepDecoder = d;
						keepDecoded = decoded;
					}
				}
			}
			decoder = keepDecoder;
			decoded = keepDecoded;*/
			
			
			/* compute the number of matches, as a measure of how conflicted each word in this genome is with the other words in the genome. */
			fitnessWord = 0;
			unclobberedWords = "";
			int matches;
			totalConflicts = 0;
			boolean noConflict; 
			String d1, d2;
			String d;
			int pos;
			char c1, c2;
			//this.resetDecoder();
			String unclobberedDecoder = this.decoder.toString();
			String checkWord;
			int checkPos;
			boolean itself;
			for (int i=0; i<decoders.length; i++) { 
				//d1 = decoders[i];
				if (Zodiac.WORD_ENCODING == 1) {
					checkWord = CipherGene.getWordForGene(genome[i*2]);
					checkPos = CipherGene.getPositionForGene(genome[i*2+1]);
				} else {
					checkWord = Zodiac.PLUGGER[i];
					checkPos = CipherGene.getPositionForGene(genome[i]);
				}
				
				matches = 0;
				itself = false;
				for (int j=0; j<decoders.length; j++) {
					//System.out.println("decoder " + j + " " + decoders[j]);
					if (Zodiac.WORD_ENCODING == 1) {
						word = CipherGene.getWordForGene(genome[j*2]);
						pos = CipherGene.getPositionForGene(genome[j*2+1]);
					} else {
						word = Zodiac.PLUGGER[j];
						pos = CipherGene.getPositionForGene(genome[j]);
						
					}
					//System.out.println(checkWord + "," + checkPos + "  " + word + "," + pos);
					if (word.equals(checkWord) && pos == checkPos && !itself) {
						//System.out.println("found itself at pos " + pos + ", " + word + "," + checkWord);
						itself = true;
					} else if (word.equals(checkWord) && pos == checkPos && itself) { // we've found a 2nd copy of the word (other than itself).  this needs to be punished.
						//System.out.println("found a dupe!");
						totalConflicts += word.length();
						continue;
					}
					//say("word " + word + ", pos " + pos);
					if (!"".equals(word)) {
						d1 = decoders[j]; // the decoder just for the given word
						//System.out.println("d1 " + d1 + " len " + decoders.length + " i " + i + ", j " + j);
						d2 = Zodiac.getDecoderForWordAtPosition(d1, checkWord, checkPos, false); // apply the comparison word to see how it clobbers the 1st decoder.
						decoder = new StringBuffer(d2);
						//System.out.println(word +"  pos " + pos + ", " + pos+word.length());
						d = decode(pos, Math.min(pos+word.length()-1, Zodiac.cipher[Zodiac.CIPHER].length()-1));
						//say("checkword " + checkWord + ", word " + word + ", d "+ d);
						if (d.equals(word) || word.equals("")) { 
							matches++;
						}
						for (int k=0; k<word.length(); k++) {
							if (k<d.length()) {
								c1 = d.charAt(k); c2 = word.charAt(k);
								if (c1 != c2) {
									totalConflicts++;
								}
							} else { // we spilled over the end of the decoded ciphertext.  we consider this a conflict.
								totalConflicts++;
							}
						}
						/*
						for (int k=0; k<d1.length(); k++) {
							c1 = d1.charAt(k); c2 = d2.charAt(k);
							if (c1 == c2 && c1 != '?' && c2 != '?') {
								matches++;
								//say("matches " + matches);
							} else noConflict = false;
						}*/
						/*if (noConflict) {
							//say("no conflict");
							unclobberedDecoder = Zodiac.getDecoderForWordAtPosition(unclobberedDecoder, word, pos);
						}*/
						
					}
				}
				//if (matches > 0) System.out.println("matches " + matches + ", dl " + decoders.length);
				if (matches >= decoders.length) { 
					unclobberedDecoder = Zodiac.getDecoderForWordAtPosition(unclobberedDecoder, checkWord, checkPos, false);
					unclobberedWords += checkWord + "(" + checkPos + ")  ";
					fitnessWord += checkWord.length()*checkWord.length();
				}
			}

			
			this.decoder = new StringBuffer(unclobberedDecoder);
			this.decoded = new StringBuffer(decode());
			// the best fitness would be if every word's decoder was fully specified and did not clobber any other decoder.
			//fitnessWord = matches;
			//say("totalConflicts " + totalConflicts);
		}
		
		//fitnessDictionaryWords(4, 10, true, false, true);
		
		//fitnessWord = (fitnessWord / decoded.length()) * (fitnessCoverageDictionary / 2.0);
		fitnessDictionaryWords(2, 10, true, false, true, genome);
		zodiacScore();
		
		if (Zodiac.WORD_ENCODING == 2) {
			fitnessWord = ((totalConflicts > 200 ? 0 : 200 - totalConflicts) + fitnessCoverageDictionaryScaled/2);
			shitness = fitnessWord; 
		}
		//System.out.println(this.getDebug());
	}

	/** return a human-readable list of word/position pairs for the given genome.  g is the genome of positions; p is the list of words (if we are encoding words into the genome too; otherwise the words are fixed) */
	public static String getEncodingFromGenome(int[] g, int[] p) {
		String e = "";
		for (int i=0; i<g.length; i++) {
			e += "[" + (Zodiac.WORD_ENCODING == 2 ? Zodiac.PLUGGER[i] : CipherGene.getWordForGene(p[i])) + " (" + getPositionForGene(g[i]) + ")]  ";
			//UNCOMMENT FOR TEMP TEST e += "[" + (Zodiac.WORD_ENCODING == 2 ? Zodiac.PLUGGER[i] : tempPlug[i]) + " (" + getPositionForGene(g[i]) + ")]  ";
		}
		return e;
	}

	/** returns the sum of word lengths */
	public static int getWordLengthSum(int[] g) {
		int result = 0;
		for (int i=0; i<g.length; i+=2) result += (Zodiac.WORD_ENCODING == 1 ? CipherGene.getWordForGene(g[i]) : Zodiac.PLUGGER[i]).length();
		return result;
	}
	
	
	/* word encoding.  simpler conflict computation, whereby
	 * we just count the total unique chars per position of each word's decoder
	 */
	public float fitnessWordSimple(/*int[] forGenome*/) {
		int[] forGenome = genome;
		
		int[] fitnessGenome;
		int[] plugger;
		
		if (Zodiac.WORD_ENCODING == 1) {
			fitnessGenome = new int[forGenome.length/2];
			plugger = new int[forGenome.length/2];
			for (int i=0; i<forGenome.length; i+=2) {
				plugger[i/2] = forGenome[i];
				fitnessGenome[i/2] = forGenome[i+1];
			}
		} else {
			fitnessGenome = forGenome;
			plugger = null;
		}
		
		CipherGene tmp = new CipherGene("");
		StringBuffer newDecoder = new StringBuffer(tmp.decoder);
		int conflicts = 0;
		THashSet letters;
		char c;
		for (int col=0; col<decoders[0].length(); col++) {
			letters = new THashSet();
			for (int d=0; d<decoders.length; d++) {
				c = decoders[d].charAt(col);
				if (c != '?') {
					letters.add(""+c);
					if (newDecoder.charAt(col) == '?')
						newDecoder.setCharAt(col, c); // newdecoder char is set to first-found char. 
				}
			}
			if (letters.size() > 0)
				conflicts += (letters.size() - 1);
		}
		
		String word;
		THashSet wordHash = new THashSet();
		//int totalLength = 0;
		// further conflicts are caused when words spill over the end of the ciphertext
		for (int i=0; i<fitnessGenome.length; i++) {
			//System.out.println("SMEG " + (Zodiac.PLUGGER[i].length()) + "," + fitnessGenome[i]);
			word = (Zodiac.WORD_ENCODING == 1 ? CipherGene.getWordForGene(plugger[i]) : Zodiac.PLUGGER[i]);
			//uncomment for temp test:  word = (Zodiac.WORD_ENCODING == 1 ? tempPlug[i] : Zodiac.PLUGGER[i]);
			
			wordHash.add(word);
			//totalLength += word.length();
			if (word.length() + getPositionForGene(fitnessGenome[i]) > Zodiac.cipher[Zodiac.CIPHER].length()) {
				conflicts += word.length();
			}
		}
		
		// sometimes, when the same word appears twice in the cipher, the fitnessGenome
		// will try to put the two identical words in the same spot.  we must treat
		// this as a conflict.
		THashSet dupes = new THashSet();
		String key;
		boolean dupe = false;
		for (int i=0; i<fitnessGenome.length; i++) {
			word = (Zodiac.WORD_ENCODING == 1 ? CipherGene.getWordForGene(plugger[i]) : Zodiac.PLUGGER[i]);
			// uncomment for temp test:  word = (Zodiac.WORD_ENCODING == 1 ? tempPlug[i] : Zodiac.PLUGGER[i]);
			//experiment 120: punish word dupes, regardless of position.
			key = word;  //uncomment if we want to discourage ALL dupes.
			//key = word + getPositionForGene(fitnessGenome[i]);  
			if (dupes.contains(key)) {
				//conflicts += word.length();
				dupe = true;
			}
			dupes.add(key);
		}
		
		decoder = new StringBuffer(newDecoder);
		
		/* for every non-specified position in the decoder, treat this as a conflict, because we'd rather have a maximum number of specified positions. */
		totalWildcardConflicts = 0;
		//for (int i=0; i<decoder.length(); i++) if (decoder.charAt(i) == '?') { conflicts++; totalWildcardConflicts++; }
		
		decoded = new StringBuffer(decode());
		encoding = getEncodingFromGenome(fitnessGenome, plugger); // have to redo this because the fitnessGenome may have changed via ECJ's default mutator
		
		// another problem: a word may conflict with itself if there are repeated
		// cipher letters within the word.  let's count these as conflicts.
		char c1, c2;
	
		for (int i=0; i<fitnessGenome.length; i++) {
			word = (Zodiac.WORD_ENCODING == 1 ? CipherGene.getWordForGene(plugger[i]) : Zodiac.PLUGGER[i]);
			//uncomment for temp test: word = (Zodiac.WORD_ENCODING == 1 ? tempPlug[i] : Zodiac.PLUGGER[i]);
			for (int j=0; j<word.length() && j+getPositionForGene(fitnessGenome[i]) < Zodiac.cipher[Zodiac.CIPHER].length(); j++) {
				c1 = word.charAt(j);
				c2 = decoded.charAt(getPositionForGene(fitnessGenome[i])+j);
				if (c1 != c2)
					conflicts++;
			}
		}
		/* truncate the fitness for any word selections whose word length sum 
		 * does not exceed some good minimum.  this helps discourage spurious solutions, like many repeated selections
		 * of small words.
		 * also, truncate fitness if there are less than 7 unique words in the selection.
		 * this discourages overly repeated words. 
		 */
		int sum = getWordLengthSum(this.genome);
		if (Zodiac.WORD_ENCODING == 1 && (sum < MIN_WORD_LENGTH_SUM || wordHash.size() < MIN_WORD_COUNT)) {
			fitnessWord = 0;
		} else {
			//System.out.println("SMEG! " + conflicts + " d " + decoder + ", " + decoded);
			if (FITNESS_WORD_SIMPLE_CONFLICT_RATIO) {
				/* two rewards: 1) add the sum of word lengths, 2) add the total number of specified (non-'?') characters in the decoder. */
				fitnessWord = sum;
				for (int i=0; i<decoder.length(); i++) fitnessWord += decoder.charAt(i) != '?' ? 1 : 0;
				
				/* then, punish the fitness based on the conflict counts */
				fitnessWord /= (1+conflicts);
				
			} else {
				fitnessWord = (conflicts > 500 ? 0 : 500-conflicts);
			}
			/*if (fitnessWord >= 500) {
				Iterator it = dupes.iterator();
				String dup = "";
				while (it.hasNext()) {
					dup += it.next() + ",";
				}
				//System.out.println("SMEGGY! DUPES " + dup + ", plugger " + encoding);
			}*/
			
			/* reward longer and/or more numerous words */
			/*if (conflicts == 0) {
				for (int i=0; i<fitnessGenome.length; i++) {
					word = (Zodiac.WORD_ENCODING == 1 ? CipherGene.getWordForGene(plugger[i]) : Zodiac.PLUGGER[i]);
					fitnessWord += word.length();
				}
			}*/
		
		}
		//fitnessDictionaryWords(2, 10, true, false, true, fitnessGenome);
		zodiacScore();
		
		if (dupe) {
			shitness = 0;
			fitnessWord = 0;
			return 0;
		}
		//fitnessWord = fitnessWord + fitnessCoverageDictionaryScaled/2;
		
		//fitnessNGramFreq();
		//fitnessWord = fitnessWord/500 * fitnessNGrams[1] * fitnessNGrams[2] * fitnessNGrams[3];
		shitness = fitnessWord;
		totalConflicts = conflicts - totalWildcardConflicts;
		return (float)shitness;
	}

	public static int getScaledPosition(int p) {
		return Math.round(((float)p/3799)*(408-21)); // in this encoding, max int is 3799.  known cipher is 408.  we subtract 21 to prevent words from spilling over at the end. 
	}
	
	public static int getEncodingForScaledPosition(int p) {
		return p*3799/(408-21);
	}
	
	/* word-encoding = 1,2 */
	public static int getPositionForGene(int g) {
		return g % Zodiac.cipher[Zodiac.CIPHER].length();
	}
	/* word-encoding = 1 and 2*/
	public static String getWordForGene(int g) {
		//System.out.println("getWord " + g);
		//if (g < Zodiac.ENCODING_MAX / 2) return ""; // uncomment this to allow words to zero out.  MAY NO LONGER WORK.
		
		if (Zodiac.WORD_ENCODING == 1) {
			//return ZodiacDictionary.zodiacTopWords[g % ZodiacDictionary.zodiacTopWords.length];
			return CipherWordGene.wordPool[g % CipherWordGene.wordPool.length];
		}
		else
			return Zodiac.PLUGGER[g % Zodiac.PLUGGER.length];
	}
	
	
	/* fitness testing */
	public void fitnessTest() {
		decoded = new StringBuffer(decode());
		//phiTest();
		fitnessDictionaryWords(1,10,true,false,true,null);
		//fitnessAlphaFreq();
		//fitnessDigraphFreq(30);
		//fitnessZodiacWordsFuzzy(4,10);
		zodiacScore();
	}
	
	
	public void phiTest() {
		int[] counts = new int[26];
		for (int i=0; i<counts.length; i++) counts[i] = 0;
		String d = decoded.toString();
		int j;
		//say(d.length() + "," + decoder.length());
		for (int i=0; i<d.length(); i++) {
			if (d.charAt(i) < 97) {
				//say("char at " + i + " is " + d.charAt(i));
				//debug();
			}
			if (d.charAt(i) != '?') {
				j = d.charAt(i)-97;
			  counts[j]++;
			}
		}
		double result = 0;
		for (int i=0; i<counts.length; i++) {
			result += counts[i]*(counts[i]-1);
		}

		phiRandom = 0.0385*d.length()*(d.length()-1);
		phiPlaintext = 0.0667*d.length()*(d.length()-1);
		phiObserved = result;

		phiRandomMatch = Math.abs(phiObserved-phiRandom)/phiRandom;
		phiPlaintextMatch = Math.abs(phiObserved-phiPlaintext)/phiPlaintext;

	}
	
	/*
	public double fitnessCommonWords() {
		double result = 0;
		String d = decode();
		foundWords = "";
		for (int i=0; i<commonWords.length; i++) {
			if (commonWords[i].length() > 2) {
				if (d.indexOf(commonWords[i]) > -1) {
					result+=commonWords[i].length();
					foundWords += commonWords[i] + " ";
				}
			}
		}
		return result;
	}*/

	public int fitnessZodiacWordsFuzzy(int minLength, int maxLength) {
		String d = decoded.toString();
		String word;
		fitnessZodiacWordsFuzzy = 0;
		int count;
		int countMax = 0;
		for (int i=0; i<ZodiacDictionary.zodiacWords.length; i++) {
			word = ZodiacDictionary.zodiacWords[i];
			if (word.length() >= minLength && word.length() <= maxLength) {
				for (int j=0; j<d.length() - word.length(); j++) {
					count = 0;
					for (int k=0; k<word.length(); k++) {
						if (word.charAt(k) == d.charAt(k+j)) count++;
					}
					if (count > countMax) countMax = count;
				}
				if (countMax > 0) 
					fitnessZodiacWordsFuzzy+= Math.pow(3, countMax);
					countMax = 0;
			}
		}
		return fitnessZodiacWordsFuzzy;
	}
	
	
	public double fitnessZodiacWordsFuzzyOld() {
		String d = decoded.toString();
		String matcher;
		String word;
		zodiacWordsFuzzy = "";
		fitnessZodiacWordsFuzzy = 0;
		boolean found = false;
		char[] c = null;
		for (int i=0; i<ZodiacDictionary.zodiacWordsInteresting.length; i++) {
			word = ZodiacDictionary.zodiacWordsInteresting[i];
			found = false;
			if (word.length() > 4) {
				if (Zodiac.fuzzyMatch(d, word) > -1) {
					//fitnessZodiacWordsFuzzy+=word.length();
					fitnessZodiacWordsFuzzy+=1;
					zodiacWordsFuzzy += word + " ";
				}
			}
		}
		return fitnessZodiacWordsFuzzy;
	}

	public void initZodiacWordHash() {
		zodiacWordHash = new THashSet();
		for (int i=0; i<ZodiacDictionary.zodiacWords.length; i++)
			zodiacWordHash.add(ZodiacDictionary.zodiacWords[i]);
	}

	/* count zodiac words within given min/max length in the decoded string.  if unique is true, we only
	 * count distinct words (we don't count multiple occurrences of the same word)
	 */
	public int fitnessZodiacWordsFaster(int minLength, int maxLength, boolean unique) {
		if (zodiacWordHash == null) initZodiacWordHash();
		StringBuffer d = new StringBuffer(decoded);
		int result = 0;
		foundWordsZodiac = "";
		String word;
		THashSet found = new THashSet();
		for (int i=minLength; i<maxLength; i++) {
			for (int j=0; j<d.length()-(i-1); j++) {
				word = d.substring(j, j+i);
				if (zodiacWordHash.contains(word)) {
					if (unique) found.add(word);
					else {
						result++;
						foundWordsZodiac += word + " ";
					}
				}
			}
		}
		
		fitnessZodiacWords = result;
		if (!unique) return result;
		
		Iterator it = found.iterator();
		while (it.hasNext())
			foundWordsZodiac += it.next() + " ";
		
		fitnessZodiacWords = found.size();
		return found.size();
	}

	public String charForLength(int length) {
		if (length < 10) return ""+length;
		else if (length == 10) return "A";
		else if (length == 11) return "B";
		else if (length == 12) return "C";
		else if (length == 13) return "D";
		else if (length == 14) return "E";
		else if (length == 15) return "F";
		else if (length == 16) return "G";
		else return "H";
	}
	
	public int lengthForChar(String c) {
		if ("1".equals(c)) return 1;
		if ("2".equals(c)) return 2;
		if ("3".equals(c)) return 3;
		if ("4".equals(c)) return 4;
		if ("5".equals(c)) return 5;
		if ("6".equals(c)) return 6;
		if ("7".equals(c)) return 7;
		if ("8".equals(c)) return 8;
		if ("9".equals(c)) return 9;
		if ("A".equals(c)) return 10;
		if ("B".equals(c)) return 11;
		if ("C".equals(c)) return 12;
		if ("D".equals(c)) return 13;
		if ("E".equals(c)) return 14;
		if ("F".equals(c)) return 15;
		if ("G".equals(c)) return 16;
		if ("H".equals(c)) return 17;
		return 0;
	}
	
	public int fitnessDictionaryWords(int minLength, int maxLength, boolean unique, boolean includeDictionary, boolean includeZodiac, int[] genome) {
		StringBuffer d = new StringBuffer(decoded);
		if (Zodiac.WORD_ENCODING == 2 & genome != null) { // if word-plugging, do not count them towards the score
			String w;
			int pos;
			for (int i=0; i<genome.length; i++) {
				w = Zodiac.PLUGGER[i];
				pos = CipherGene.getPositionForGene(genome[i]);
				//System.out.println("d [" + d + "], w + [" + w + "], " + pos + "," +  Math.min(pos+w.length(), d.length()));
				d.replace(pos, Math.min(pos+w.length(), d.length()), w.replaceAll(".", "_"));
				//System.out.println("d " + d);
			}
		}
	
		StringBuffer cover = new StringBuffer(d);
		StringBuffer coverScaled = new StringBuffer();
		for (int i=0; i<d.length(); i++) coverScaled.append('?');
		int result = 0;
		foundWordsDictionary = new StringBuffer();
		foundWordsDictionaryInteresting = "";
		fitnessDictionaryWordsInteresting = 0;
		fitnessWordLengthDistribution = 0.0;
		sentence = new String[d.length()];
		String word;
		THashSet found = new THashSet();
		THashSet foundInteresting = new THashSet();
		float[] distribution = new float[maxLength];
		boolean alreadyCovered;
		for (int i=0; i<distribution.length; i++) distribution[i] = 0.0f;
		
		for (int i=maxLength; i>=minLength; i--) {
			for (int j=0; j<d.length()-(i-1); j++) {
				word = d.substring(j, j+i);
				if (ZodiacDictionary.isWord(word, ZodiacDictionary.D_BIG) || ZodiacDictionary.isWord(word, ZodiacDictionary.D_ZODIAC)) {
					distribution[i-1] += 1.0;
					alreadyCovered =coverScaled.substring(j,j+i).matches(".*[0-9A-Z].*"); 
					if (!found.contains(word)) {
						for (int k=j; k<j+i; k++) {
							cover.replace(k, k+1, "*");
							if (!alreadyCovered) {
								//say("coverscaled " + coverScaled.substring(j,j+i) + ", word " + word);
								coverScaled.replace(k, k+1, charForLength(word.length()));
								sentence[j] = word;
							}
						}
					} else { // dupe word, so give smaller reward
						for (int k=j; k<j+i; k++) {
							if (!alreadyCovered) {
								//say("coverscaled " + coverScaled.substring(j,j+i) + ", word " + word);
								coverScaled.replace(k, k+1, "0");
								sentence[j] = word;
							}
						}
					}
						
					if (unique) found.add(word);
					else {
						result++;
						foundWordsDictionary.append(word + " ");
					}
				}
				if (ZodiacDictionary.isWord(word, ZodiacDictionary.D_INTERESTING)) {
					if (unique) foundInteresting.add(word);
					else {
						fitnessDictionaryWordsInteresting++;
						foundWordsDictionaryInteresting += word + " ";
					}
				}
			}
		}
		
		if (!unique) {
			for (int i=0; i<distribution.length; i++) {
				distribution[i] /= (float) result;
			}
			/*for (int i=0; i<distribution.length; i++) {
				say ("distribution " + i + " = " + distribution[i]);
			}*/
			fitnessDictionaryWords = result;
		} else {
			fitnessDictionaryWordsInteresting = foundInteresting.size();
			for (int i=0; i<distribution.length; i++) {
				distribution[i] /= (float) found.size();
			}
			/*for (int i=0; i<distribution.length; i++) {
				say ("distribution " + i + " = " + distribution[i]);
			}*/
			Iterator it = found.iterator();
			while (it.hasNext())
				foundWordsDictionary.append(it.next() + " ");
			it = foundInteresting.iterator();
			while (it.hasNext())
				foundWordsDictionaryInteresting += it.next() + " ";
			
			fitnessDictionaryWords = found.size();
		}
		int c = 0;
		for (int i=0; i<cover.length(); i++) {
			if (cover.charAt(i) == '*') c++;
		}
		fitnessCoverageDictionary = (float)c/cover.length();
		coverageDictionary = cover.toString();
		
		c = 0;
		int r = 0;
		for (int i=0; i<coverScaled.length(); i++) {
			if (coverScaled.charAt(i) == '0')
				c += 1; // tiny reward for dupe words
			else {
				r = lengthForChar(""+coverScaled.charAt(i))*lengthForChar(""+coverScaled.charAt(i));
				if (r > 0)
					c += 100 + r;
			}
		}
		//System.out.println(c);
		//fitnessCoverageDictionaryScaled = (float)c/37163.0; // max value for known solution was 37163.
		fitnessCoverageDictionaryScaled = (float)c/37172.0; // max value for known solution was 37172 (not sure yet why it changed)
		coverageDictionaryScaled = coverScaled.toString();
		/* quick and dirty word distribution fitness.  it considers words of length 4 through 10. */		
		//known cipher has this distribution by (word length - 1) using fitnessDictionaryWords(4,10,true):
		//Sun Apr 22 15:10:42 EDT 2007 : 1177269042863: distribution 3 = 0.75409836
		//Sun Apr 22 15:10:42 EDT 2007 : 1177269042863: distribution 4 = 0.22950819
		//Sun Apr 22 15:10:42 EDT 2007 : 1177269042863: distribution 5 = 0.14754099
		//Sun Apr 22 15:10:42 EDT 2007 : 1177269042864: distribution 6 = 0.114754096
		//Sun Apr 22 15:10:42 EDT 2007 : 1177269042864: distribution 7 = 0.0
		//Sun Apr 22 15:10:42 EDT 2007 : 1177269042864: distribution 8 = 0.032786883
		//Sun Apr 22 15:10:42 EDT 2007 : 1177269042864: distribution 9 = 0.0
		// the known maximum for fitnessWordLengthDistribution is 0.7142857142857143 for the known solution.
		double total = 0;
		float f;
		for (int i=3; i<distribution.length; i++) {
			f = distribution[i];
			if (i==3) total += (f >= 0.75 ? 1.0 : f/0.75); 
			if (i==4) total += (f >= 0.2 ? 1.0 : f/0.2); 
			if (i==5) total += (f >= 0.1 ? 1.0 : f/0.1); 
			if (i==6) total += (f >= 0.1 ? 1.0 : f/0.1); 
			if (i>6) total += (f > 0.0 ? 1.0 : 0.0); 
		}
		fitnessWordLengthDistribution = (double)total/7.0f;
		
		return fitnessDictionaryWords;
		
	}

	
	public int fitnessZodiacWords() {
		String d = decoded.toString();
		StringBuffer cover = new StringBuffer(d);
		StringBuffer coverMatch = new StringBuffer(d);
		fitnessZodiacWords = 0;
		foundWordsZodiac = "";
		foundWordsZodiacPairings = "";
		String word;
		int index;
		String replace;
		for (int i=0; i<ZodiacDictionary.zodiacWords.length; i++) {
			word = ZodiacDictionary.zodiacWords[i];
			if (d.indexOf(word) > -1) { 
				//fitnessZodiacWords+=word.length();
				if (word.length() > 3) {
					fitnessZodiacWords+=1;
					foundWordsZodiac += word + " ";
				}
				if (word.length() > 2) {
					coverMatch = new StringBuffer(d);
					index = coverMatch.indexOf(word);
					replace = "";
					for (int j=0; j<word.length(); j++) replace += "@";

					while(index > -1) {
						cover.replace(index, index+word.length(), replace);
						coverMatch.replace(index, index+word.length(), replace);
						index = coverMatch.indexOf(word);
						//say("cover now " + cover);
					}

					coverageZodiac = cover.toString();
					fitnessCoverageZodiac = 0;
					for (int j=0; j<coverageZodiac.length(); j++) {
						if (coverageZodiac.charAt(j) == '@') fitnessCoverageZodiac++;
					}
					fitnessCoverageZodiac = (double)fitnessCoverageZodiac/Zodiac.cipher[Zodiac.CIPHER].length();
				}
			}
		}


		return fitnessZodiacWords;
	}

	public double fitnessZodiacWordsPairings() {
		/* find common zodiac word pairings */
		String word;
		String d = decoded.toString();
		for (int i=0; i<ZodiacDictionary.zodiacWordsPaired.length; i++) {
			word = ZodiacDictionary.zodiacWordsPaired[i];
			if (d.indexOf(word) > -1) {
				fitnessZodiacWordsPairings += 1;
				foundWordsZodiacPairings += word + " ";
			}
		}
		return fitnessZodiacWordsPairings;
		
	}

	public double commonWords5000() {
		double result = 0;
		String d = decoded.toString();
		fitnesscommonWordsUnique = 0;
		//fitnesscommonWordsUnique = 0.01;
		//if (d.indexOf("kill") == -1) return 0.01;
		foundWords = "";
		String chunk;
		int dupes = 0;
		double factor = 1.0;


		for (int i=0; i<ZodiacDictionary.commonWords5000.length; i++) {
			if (ZodiacDictionary.commonWords5000[i].length() < 5) {
				factor = 0.01;
			} else
				factor = 1.0;

			if (d.indexOf(ZodiacDictionary.commonWords5000[i]) >-1 ) {
				//fitnesscommonWordsUnique += factor*ZodiacDictionary.commonWords5000[i].length();
				fitnesscommonWordsUnique += factor;
				foundWords += ZodiacDictionary.commonWords5000[i] + " ";
			}
			/*if (ZodiacDictionary.commonWords5000[i].length() > 2) {
				chunk = d;
				dupes = 0;
				while (chunk.indexOf(ZodiacDictionary.commonWords5000[i]) > -1 && dupes < 2) {
					result += ZodiacDictionary.commonWords5000[i].length();
					foundWords += ZodiacDictionary.commonWords5000[i] + " ";
					dupes++;
					if (chunk.length() == ZodiacDictionary.commonWords5000[i].length())
						chunk = "";
					else
						chunk = chunk.substring(chunk.indexOf(ZodiacDictionary.commonWords5000[i])+ZodiacDictionary.commonWords5000[i].length());
				}
			}*/
		}
		//fitnesscommonWords = fitnesscommonWordsUnique; //result;
		return result;
	}

	public void zodiacScore() {
		if (Zodiac.CIPHER == 1 || Zodiac.CIPHER == 4) { // if working the known cipher, compare derived solution to known solution.
			
			/*
			zodiacScore = 0;
			zodiacMatch = 0.0;
			zodiacWords = "";
			String d = decoded;
			for (int i=0; i<ZodiacDictionary.zodiacWords.length; i++) {
				if (ZodiacDictionary.zodiacWords[i].length() > 3) {
					if (d.indexOf(ZodiacDictionary.zodiacWords[i]) > -1) {
						zodiacScore ++;
						zodiacWords += ZodiacDictionary.zodiacWords[i] + " ";
					}
				}
			}
			for (int i=0; i<Zodiac.firstCipherDecoded.length(); i++) {
				if (Zodiac.firstCipherDecoded.charAt(i) == d.charAt(i))
					zodiacMatch++;
			}
			zodiacMatch = (double) zodiacMatch / Zodiac.firstCipherDecoded.length();
			*/
			
			int match = 0;
			for (int i=0; i<decoder.length(); i++) {
				if (decoder.charAt(i) == Zodiac.solutions[Zodiac.CIPHER].charAt(i)) match++;
			}
			zodiacMatch = (double) match / decoder.length();
		}
			
	}

	/* returns a factor between 0 and 1.  1 means perfect match. */
	public double fitnessAlphaFreq() {
		double result = 0;
		THashMap hash = new THashMap();
		Frequency f;
		String letter;
		double freq = 0.0;
		for (int i=0; i<26; i++) {
			f = new Frequency();
			f.total = 0;
			letter = ""+((char)(i+97));
			f.item = letter;
			//say("putting " + f.item);
			hash.put(f.item, f);
		}

		String d = decoded.toString();
		for (int i=0; i<d.length(); i++) {
			if (d.charAt(i) != '?') {
				f = (Frequency)hash.get(""+d.charAt(i)); 
				f.total++;
			}
		}

		double expected, ours;
		for (int i=0; i<Zodiac.expectedFrequencies.length; i++) {
			letter = ""+((char)(i+97));
			f = (Frequency) hash.get(letter);
			expected = Zodiac.expectedFrequencies[i];
			//say("letter " + letter + ", " + f);
			if (f == null) {
				freq += (expected*expected);
			} else {
				ours = (double)f.total / Zodiac.cipher[Zodiac.CIPHER].length();
				freq += Math.abs((expected*expected) - (ours*ours));
			}
		}
		
		Object[] sorted = hash.values().toArray();
		Arrays.sort(sorted, new FrequencyComparator());
		this.ourLetterFreqs = "";
		for (int i=0; i<sorted.length; i++) {
			this.ourLetterFreqs += ((Frequency)sorted[i]).item; 
		}

		
		//say("fs " + fs);
		/*
		ourLetterFreqs = fs;
		for (int i=0; i<Zodiac.letterFrequencies.length(); i++) {
			//say(i + ": ref letter " + letterFrequencies.charAt(i) + ", our position: " +fs.indexOf(letterFrequencies.charAt(i)));
			if (fs.indexOf(Zodiac.letterFrequencies.charAt(i)) > -1)
				result += Zodiac.letterFrequencies.length() - Math.abs(i - fs.indexOf(Zodiac.letterFrequencies.charAt(i)));
		}*/
		//fitnessAlpha = result/(Zodiac.letterFrequencies.length()*Zodiac.letterFrequencies.length());
		//fitnessAlpha = (fitnessAlpha >= 0.75 ? 1.0 : fitnessAlpha);
		//fitnessAlphaFreq = (freq >= 0.70 ? 1.0 : freq);
		fitnessAlphaFreq = 1.0 - Math.sqrt(freq);
		//say("freq! " + fitnessAlphaFreq);
		return fitnessAlphaFreq;
	}

	/*
	public double fitnessDigraphFreqFromSentence() {
		if (ZodiacDictionary.zodiacNGramFrequencies == null) 
			ZodiacDictionary.computeNGramFrequenciesFromZodiacWords(true);
		
		fitnessDigraph = 0;
		String d = decode();
		String digraph;
		THashMap freq = new THashMap();
		Float val;
		int totalLength = 0;
		for (int i=0; i<sentence.length; i++) {
			if (sentence[i] != null) {
				totalLength += sentence[i].length();
				for (int j=0; j<sentence[i].length()-1; j++) {
					digraph = sentence[i].substring(j,j+2);
					if (freq.get(digraph) == null) {
						freq.put(digraph,new Float(0));
					}
					val = (Float)freq.get(digraph);
					val = new Float(val.floatValue()+1);
					freq.put(digraph, val);
					//say("put " + digraph + ", " + val);
				}
			}
		}
		Iterator it = freq.keySet().iterator();
		while (it.hasNext()) {
			digraph = (String)it.next();
			val = (Float)freq.get(digraph);
			val = new Float((float)val.floatValue()/totalLength);
			//say("new put " + digraph + ", " + val);
			freq.put(digraph, val);
		}
		
		
		it = ZodiacDictionary.zodiacNGramFrequencies[2].keySet().iterator();
		Float zVal;
		
		while (it.hasNext()) {
			digraph = (String)it.next();
			val = (Float)freq.get(digraph);
			zVal = (Float)ZodiacDictionary.zodiacNGramFrequencies[2].get(digraph);
			//say("digraph " + digraph + ", " + (val == null ? "" : Zodiac.nformat(val.doubleValue())) + ", " + Zodiac.nformat(zVal.doubleValue()));
			if (val == null) {
				fitnessDigraph += zVal.floatValue();
			} else {
				fitnessDigraph += Math.abs(zVal.floatValue() - val.floatValue());
			}
		}
		
		//fitnessDigraph = fitnessDigraph / (float) ZodiacDictionary.zodiacDigraphFrequencies.size();
		fitnessDigraph = 1-fitnessDigraph;
		return fitnessDigraph;
	}*/

	public void fitnessNGramFreq() {
		for (int i=1; i<=3; i++) fitnessNGramFreq(i);
	}
	
	/** compute sum of absolute differences between expected ngram counts and actual ngram counts. */
	public int fitnessNGramCount(int n) {
		if (ZodiacDictionary.zodiacNGramFrequencies == null) 
			ZodiacDictionary.computeNGramFrequenciesFromZodiacWords(0.0000f, true);
		int result = 0;
		
		
		return result;
	}
	/** euclidian distance between zodiac ngram distribution and current ngram distribution */
	public double fitnessNGramFreq(int n) {
		badNGramCount[n] = 0;
		if (ZodiacDictionary.zodiacNGramFrequencies == null) 
			ZodiacDictionary.computeNGramFrequenciesFromZodiacWords(0.0003f, true);
		
		double fitnessNGram = 0;
		String d = decode();
		//String digraph;
		//String doub;
		//String trigraph;
		String ngram;
		
		Frequency val;
		THashMap ours = new THashMap();
		//THashMap oursDoub = new THashMap();
		//THashMap oursTrigraph = new THashMap();
		
		THashSet both = (THashSet) ZodiacDictionary.zodiacNGramKeySet[n].clone(); // distinct union of our ngrams and the corpus ngrams
		//System.out.println("size " + both.size());
		/* feed corpus ngrams into the union */
		/*Iterator it = ZodiacDictionary.zodiacNGramFrequencies[n].keySet().iterator();
		Object key;
		while (it.hasNext()) {
			key = it.next();
			both.add(key);
		}*/
		//say("n " + n + ", size " + both.size());
		
		
		for (int i=0; i<d.length()-(n-1); i++) {
			ngram = d.substring(i, i+n);
			if (!ngram.contains("?")) {
				both.add(ngram);
				if (ours.get(ngram) == null)
					ours.put(ngram, new Frequency());
				
				val = (Frequency) ours.get(ngram);
				val.item = ngram;
				val.total++;
				ours.put(ngram, val);

				/* handle double letters 
				if (digraph.charAt(0) == digraph.charAt(1)) {
					if (oursDoub.get(digraph) == null)
						oursDoub.put(digraph, new Frequency());
					
					val = (Frequency) oursDoub.get(digraph);
					val.item = digraph;
					val.total++;
					oursDoub.put(digraph, val);
				}*/
				/* handle trigraphs 
				if (i < d.length() - 2) {
					trigraph = d.substring(i, i+3);
					if (oursTrigraph.get(trigraph) == null)
						oursTrigraph.put(trigraph, new Frequency());
					
					val = (Frequency) oursTrigraph.get(trigraph);
					val.item = trigraph;
					val.total++;
					oursTrigraph.put(trigraph, val);
				}
				*/
			}
		}

		/*
		Object[] sorted = ours.values().toArray();
		Arrays.sort(sorted, new FrequencyComparator());
		Object[] sortedDoub = oursDoub.values().toArray();
		Arrays.sort(sortedDoub, new FrequencyComparator());
		Object[] sortedTrigraph = oursTrigraph.values().toArray();
		Arrays.sort(sortedTrigraph, new FrequencyComparator());
		
		foundDigraphs = "";
		foundDoubles = "";
		foundTrigraphs = "";
		
		int score = 0;
		for (int i=0; i<top; i++) {
			val = (Frequency) sorted[i];
			//say("top " + i + " " + val.item);
			for (int j=0; j<top; j++) {
				if (val.item.equals(ZodiacDictionary.zodiacTop166Digraphs[j])) {
					score++;
					foundDigraphs += ZodiacDictionary.zodiacTop166Digraphs[j] + " ";
					break;
				}
			}
		}
		fitnessDigraph = (float)score / top;
		
		score = 0;
		for (int i=0; i<sortedDoub.length; i++) {
			val = (Frequency) sortedDoub[i];
			//say("top " + i + " " + val.item);
			for (int j=0; j<Zodiac.doubles.length; j++) {
				if (val.item.equals(Zodiac.doubles[j])) {
					score++;
					foundDoubles += Zodiac.doubles[j] + " ";
					break;
				}
			}
		}
		fitnessDouble = (float)score / Zodiac.doubles.length;
		
		score = 0;
		for (int i=0; i<sortedTrigraph.length; i++) {
			val = (Frequency) sortedTrigraph[i];
			//say("top " + i + " " + val.item);
			for (int j=0; j<Zodiac.trigraphs.length; j++) {
				if (val.item.equals(Zodiac.trigraphs[j])) {
					score++;
					foundTrigraphs += Zodiac.trigraphs[j] + " ";
					break;
				}
			}
		}
		fitnessTrigraph = (float)score / Zodiac.trigraphs.length;*/
		/*Iterator it = ours.keySet().iterator();
		while (it.hasNext()) {
			digraph = (String) it.next();
			val = (Float) ours.get(digraph);
			val = new Float(val.floatValue()/d.length());
			ours.put(digraph, val);
			//say("digraph "+ digraph + ", freq " + val);
		}	*/	
		
		THashMap frequencies = ZodiacDictionary.zodiacNGramFrequencies[n];		
		Iterator it = both.iterator();
		Float zVal = null;
		float zValf;
		double oursf;
		Frequency f;
		double fitnessNGramEuclidian = 0.0;
		while (it.hasNext()) {
			ngram = (String) it.next();
			zVal = (Float) frequencies.get(ngram);
			f = (Frequency) ours.get(ngram);
			
			
			zValf = 0f;
			oursf = 0;
			if (zVal != null)
				zValf = zVal.floatValue();
			if (f != null) {
				oursf = (double)f.total/d.length();
			}
			
			if (zValf <= badNGramMin) {
				badNGramCount[n]++;
			}
			
			/*if (n==2)
				say("ngram " + ngram + ", zf " + zValf + ", oursf " + oursf);*/
				
			fitnessNGramEuclidian += (zValf - oursf)*(zValf - oursf);
			//say("ngram " + ngram + " " + fitnessNGramEuclidian);
			
			/*
			if (f == null) {
				fitnessDigraphEuclidian += zVal.floatValue()*zVal.floatValue();
			} else {
				fitnessDigraphEuclidian += Math.abs(((double)f.total/d.length())*((double)f.total/d.length()) - (zVal.floatValue()*zVal.floatValue()));
			}*/
			//say("d " + digraph + ", zVal " + zVal + ", f " + f + ", fde " + fitnessDigraphEuclidian);
		}
		//fitnessDigraph = 1 - fitnessDigraph/(float)frequencies.size();
		fitnessNGramEuclidian = 1 - Math.sqrt(fitnessNGramEuclidian);
		
		fitnessNGrams[n] = fitnessNGramEuclidian;
		/*if (n == 1 && fitnessNGramEuclidian >= 0.940789) fitnessNGrams[n] = 1; 
		if (n == 2 && fitnessNGramEuclidian >= 0.938565) fitnessNGrams[n] = 1; 
		if (n == 3 && fitnessNGramEuclidian >= 0.941984) fitnessNGrams[n] = 1;*/ 
		return fitnessNGramEuclidian;
	}
	
	/* returns a factor between 0 and 1.  1 means perfect match. 
	 * this digraph compares the ordering of digraph occurrences, not the raw frequency.
	 * 
	public double fitnessDigraphFreqOld() {
		double result = 0;
		THashMap hash = new THashMap();
		Frequency f;
		String di = "";
		for (int i=0; i<Zodiac.digraphs.length; i++) {
			f = new Frequency();
			f.total = 0;
			f.item = Zodiac.digraphs[i];
			//say("putting " + f.item);
			hash.put(f.item, f);
			di += Zodiac.digraphs[i] + ",";
		}

		String d = decoded;
		for (int i=0; i<d.length()-1; i++) {
			f = (Frequency)hash.get(""+d.charAt(i)+d.charAt(i+1));
			if (f!=null)
				f.total++;
		}
		Object[] sorted = hash.values().toArray();
		Arrays.sort(sorted, new FrequencyComparator());
		String ds = "";
		for (int i=0; i<sorted.length; i++) {
			if (((Frequency)sorted[i]).total > 0) ds += ((Frequency)sorted[i]).item+",";
			//say("sorted " + ((Frequency)sorted[i]).item + "," + ((Frequency)sorted[i]).total);
		}
		//say("ds " + ds);
		ourDigraphFreqs = ds;
		for (int i=0; i<Zodiac.digraphs.length; i++) {
			//say("i " + i + " " + ds.indexOf(digraphs[i]));
			//say(i + ": ref letter " + letterFrequencies.charAt(i) + ", our position: " +fs.indexOf(letterFrequencies.charAt(i)));
			if (ds.indexOf(Zodiac.digraphs[i]) > -1)
				result += Zodiac.digraphs.length - Math.abs(i - ds.indexOf(Zodiac.digraphs[i])/3);*/
			/*else
				result += digraphs.length;*/
			//say("r " + result );
		/*}
		fitnessDigraph = result/(Zodiac.digraphs.length*Zodiac.digraphs.length);
		fitnessDigraph = fitnessDigraph > 0.50 ? 1.0 : fitnessDigraph;
		return fitnessDigraph;
	}*/

	/*
	public double fitnessNgraph(int n) {
		String d = decode();
		double result = 0.0;

		THashMap freqs = null;
		if (n == 2)
			freqs = Frequency2graph;
		else if (n == 3)
			freqs = Frequency3graph;
		else if (n == 4)
			freqs = Frequency4graph;

		THashMap ourFreqs = ZodiacDictionary.measureNgraphs(d, n);
		Iterator it = ourFreqs.keySet().iterator();
		String key;
		double x;
		double y;
		double diff;
		while (it.hasNext()) {
			key = (String)it.next();

			if (freqs.get(key) == null) {
				x = 0;
			}
			else {
				x = ((Double)freqs.get(key)).doubleValue(); 
			}
			y = ((Double)ourFreqs.get(key)).doubleValue();
			diff = Math.abs(x-y);
			result += diff*diff;
		}
		result = Math.sqrt(result);
		return result;
	}
*/
	
	protected void debug() {
		write(getDebug());
	}
	
	protected void debugLine() {
		write(getDebugLine());
	}
	
	
	public String getGenomeAsString() {
		if (genome == null) return null;
		String result = "";
		for (int i=0; i<genome.length; i+=2) {
			result += genome[i] + "," + genome[i+1] + ",  ";
		}
		return result;
	}
	/* short debug info on one line */
	public String getDebugLine() {
		String fitU = "";
		
		String decodedSubstring = "";
		if (CipherWordGene.CIPHER_START != 0 || CipherWordGene.CIPHER_END != Zodiac.cipher[Zodiac.CIPHER].length()-1) {
			decodedSubstring += ", decodedSubstring [" + decoded.substring(CipherWordGene.CIPHER_START, CipherWordGene.CIPHER_END+1) + "]";
		}
		
		if (evaluated) {
			//MultiObjectiveFitness fit = (MultiObjectiveFitness) fitnessUnshared;
			//if (fit != null)
			//	for (int i=0; i<fit.multifitness.length; i++) fitU += fit.multifitness[i] + "|";
			if (1==1) throw new RuntimeException("FIX THIS");

		}
		String fitM = "";
		MultiObjectiveFitness fit = (MultiObjectiveFitness) fitness;
		if (fit != null)
			//for (int i=0; i<fit.multifitness.length; i++) fitM += fit.multifitness[i] + "|";
			if (1==1) throw new RuntimeException("FIX THIS");

		return("fitness [" + fitM + "]" +
				", fitnessUnshared [" + fitU + "]" + 
				", fitnessWord " + Zodiac.nformat(fitnessWord) + 
				", fitnessNGrams[1] " + Zodiac.nformat(fitnessNGrams[1]) + 
				", fitnessNGrams[2] " + Zodiac.nformat(fitnessNGrams[2]) + 
				", fitnessNGrams[3] " + Zodiac.nformat(fitnessNGrams[3]) + 
				", fitnessNGrams[4] " + Zodiac.nformat(fitnessNGrams[4]) + 
				", fitnessNGrams[5] " + Zodiac.nformat(fitnessNGrams[5]) + 
				", fitnessNGrams[6] " + Zodiac.nformat(fitnessNGrams[6]) + 
				", fitnessCoverageDictionaryScaled " + this.fitnessCoverageDictionaryScaled + 
				", corpus [" + corpusGenome + 
				"], corpusUnclobbered [" + corpusUnclobbered + "], decoder [" + decoder + "], decoded [" + decoded 
				+ "]" + decodedSubstring + ", decodedSubstringSpaced [" + decodedSubstringSpaced + "], conflictOverlap [" + conflictOverlap.substring(CipherWordGene.CIPHER_START, CipherWordGene.CIPHER_END+1) + "], words " + 
				foundWordsDictionary + ", coverage " + coverageDictionaryScaled + (Zodiac.CIPHER == 1 || Zodiac.CIPHER == 4 ? ", zodiacMatch " + 
				Zodiac.nformat(this.zodiacMatch) : "") + ", clusterCount " + clusterCount + 
				(Zodiac.WORD_ENCODING > 0 ? (", plugger " + encoding + ", zodiacWordsOther " + zodiacWordsOther + ", zodiacWordsFuzzy " + zodiacWordsFuzzy +  
				", sortedKey " + sortedKey + ", genome " + getGenomeAsString() + ", totalConflicts " + 
				totalConflicts + ", totalWildcardConflicts " + totalWildcardConflicts + ", conflictInfo " + conflictInfo + ", bestWords " + 
				this.unclobberedWords) : ""));
	}
	
	public StringBuffer getDecodedSubstringSpaced() {
		StringBuffer result = new StringBuffer("");
		
		String d = decoded.substring(CipherWordGene.CIPHER_START, CipherWordGene.CIPHER_END+1);
		for (int i=0; i<d.length(); i++) {
			if (conflictWordStarts[i]) result.append(" ").append(d.charAt(i));
			else if (conflictWordEnds[i]) result.append(d.charAt(i)).append(" ");
			else result.append(d.charAt(i));
		}
		return result;
	}
	
	public String getDebug() {
		String msg = "";
		String eol = System.getProperty("line.separator");
		
		/*write("------------ DEBUG ------------------------------------------------ ");
		write("    cipher " + Zodiac.cipher[Zodiac.CIPHER]);
		write("   alphabe " + Zodiac.alphabet[Zodiac.CIPHER]);
		write("   decoder " + decoder);
		write("decodes to " + decode());
		write("coverage z " + coverageZodiac);
		if (Zodiac.ENCODING == 1) write("alle words " + wordDebugAll());
		if (Zodiac.ENCODING == 1) write("clobber ratio " + wordClobber());
		write("letter freq " + Zodiac.letterFrequencies);
		write("   our freq " + ourLetterFreqs);
		String d = ""; for (int i=0; i<Zodiac.digraphs.length; i++) { d += Zodiac.digraphs[i] + ","; }
		//write("digraph freq " + d);
		//write("   our freq " + ourDigraphFreqs);
		//write("2graph fitness " + fitness2graph + " 3graph fitness " + fitness3graph + " 4graph fitness " + fitness4graph); 
		write("phi random " + phiRandom + ", phi plaintext " + phiPlaintext + " phi observed " + phiObserved + " random match " + phiRandomMatch + " plaintext match " + phiPlaintextMatch);
		write("foundWords " + foundWords + ", foundWordsZodiacPairings " + this.foundWordsZodiacPairings);
		write("zodiac score " + zodiacScore + " zodiac match " + zodiacMatch + " zodiac words " + foundWordsZodiac + " fuzzy words " + zodiacWordsFuzzy);
		write("fitness " + Zodiac.nformat(fitness) + ", fitnessAlpha " + Zodiac.nformat(fitnessAlpha) + ", fitnessAlphaFreq " + Zodiac.nformat(fitnessAlphaFreq) + ", fitnessDigraph " + Zodiac.nformat(fitnessDigraph) + ", fitnesscommonWordsUnique " + Zodiac.nformat(fitnesscommonWordsUnique) + ", fitnessZodiacWords " + fitnessZodiacWords + ", fitnessZodiacWordsFuzzy " + fitnessZodiacWordsFuzzy + ", fitnessCoverageZodiac " + fitnessCoverageZodiac + ", fitnessZodiacWordsPairings " + fitnessZodiacWordsPairings);

		if (Zodiac.CIPHER == 1) {
			int diff = 0;
			for (int i=0; i<decoder.length(); i++) {
				diff += (decoder.charAt(i) == Zodiac.solutions[1].charAt(i) ? 0 : 1); 
			}
			write("diff from known solution: " + diff);
		}
		write("------------------------------------------------------------------- ");
*/
		msg+="------------ DEBUG ------------------------------------------------ " + eol;
		msg+="    cipher " + Zodiac.cipher[Zodiac.CIPHER] + eol;
		msg+="   alphabe " + Zodiac.alphabet[Zodiac.CIPHER] + eol;
		msg+="   decoder " + decoder + eol;
		msg+="decodes to " + decoded + eol;
		msg+="coverage z " + coverageZodiac + eol;
		msg+="coverage d " + coverageDictionary + eol;
		msg+="coverage s " + coverageDictionaryScaled + eol;
		if (sentence != null) {
			msg+="sentence: ";
			for (int i=0; i<sentence.length; i++) {
				if (sentence[i] != null) msg += sentence[i] + " ";
			}
			msg+=eol;
		}
		if (Zodiac.ENCODING == 1) msg+="alle words " + wordDebugAll() + eol;
		if (Zodiac.ENCODING == 1) msg+="clobber ratio " + wordClobber() + eol;
		msg+="letter freq " + Zodiac.letterFrequencies + eol;
		msg+="  wiki freq " + Zodiac.letterFrequenciesWiki + eol;
		msg+=" our l freq " + ourLetterFreqs + eol;
		String d = ""; for (int i=0; i<Zodiac.digraphs.length; i++) { d += Zodiac.digraphs[i] + ","; }
//		msg+="digraph freq " + d + eol;
//		msg+="   our freq " + ourDigraphFreqs + eol;
//		msg+="2graph fitness " + fitness2graph + " 3graph fitness " + fitness3graph + " 4graph fitness " + fitness4graph + eol; 
		msg+="phi random " + phiRandom + ", phi plaintext " + phiPlaintext + " phi observed " + phiObserved + " random match " + phiRandomMatch + " plaintext match " + phiPlaintextMatch + eol;
		msg+="corpusUnclobbered: " + corpusUnclobbered + eol;
		msg+="foundWordsDictionary " + foundWordsDictionary + ", foundWordsZodiacPairings " + this.foundWordsZodiacPairings + ", fitnessDictionaryWords " + fitnessDictionaryWords + ", fitnessDictionaryWordsInteresting " + fitnessDictionaryWordsInteresting + eol;
		msg+="foundWordsDictionaryInteresting " + foundWordsDictionaryInteresting + eol;
		msg+="zodiac score " + zodiacScore + " zodiac match " + zodiacMatch + " zodiac words " + foundWordsZodiac + " fuzzy words " + zodiacWordsFuzzy + eol;
		msg+="clusterCount " + clusterCount + eol;
		msg+="fitness " + Zodiac.nformat(shitness) + ", fitnessAlphaFreq " + Zodiac.nformat(fitnessAlphaFreq) + ", fitnesscommonWordsUnique " + Zodiac.nformat(fitnesscommonWordsUnique) + ", fitnessZodiacWords " + fitnessZodiacWords + ", fitnessZodiacWordsFuzzy " + fitnessZodiacWordsFuzzy + ", fitnessCoverageZodiac " + fitnessCoverageZodiac + ", fitnessCoverageDictionary " + fitnessCoverageDictionary + ", fitnessCoverageDictionaryScaled " + fitnessCoverageDictionaryScaled + ", fitnessZodiacWordsPairings " + fitnessZodiacWordsPairings + eol + ", fitnessWordLengthDistribution " + fitnessWordLengthDistribution + eol;
		msg+="fitnessWord " + Zodiac.nformat(fitnessWord) + ", corpusGenome " + corpusGenome + eol;
		msg+="foundDigraphs: [" + foundDigraphs + "]" + eol + "foundDoubles: [" + foundDoubles + "]" + eol + "foundTrigraphs: [" + foundTrigraphs + "]" + eol; 
		for (int i=1; i<=5; i++) msg += "fitnessNGrams(" + i + "): " + Zodiac.nformat(fitnessNGrams[i]) + ", ";
		msg += eol;
		for (int i=1; i<=5; i++) msg += "badNGramCount[" + i + "]: " + badNGramCount[i] + ", ";
		msg += eol;
		
		if (Zodiac.WORD_ENCODING > 0) {
			msg += "Total conflicts: " + totalConflicts +", Plugger: " + encoding + eol;
			msg += "Best words: " + unclobberedWords + eol;
		}
		
		if (Zodiac.CIPHER == 1) {
			int diff = 0;
			for (int i=0; i<decoder.length(); i++) {
				diff += (decoder.charAt(i) == Zodiac.solutions[1].charAt(i) ? 0 : 1); 
			}
			msg+="diff from known solution: " + diff + eol;
		}
		msg+="------------------------------------------------------------------- " + eol;
		
		return msg;
	}

	/*
	public boolean equals(Object o) {
		if (o == null) return false;
		if (!(o instanceof CipherGene)) return false;
		if (((CipherGene)o).decoder.equals(this.decoder)) return true;
		else return false;
	}*/
	
	/** given two decoders, returns the total number of differences between them. 
	 * 
	 * @param d1 first decoder string
	 * @param d2 second decoder string
	 * @return count of characters that differ between the given decoders.
	 */
	public static int differences(String d1, String d2) {
		int result = 0;
		if (d1 != null && d2 != null) {
			for (int i=0; i<d1.length(); i++) {
				if (d1.charAt(i) != d2.charAt(i)) result++;
			}
		}
		return result;
	}

	/** given two decoders, returns the total number of shared characters between them, as a percentage.
	 * 1.0 = perfect match; 0.0 = nothing matches. 
	 * 
	 * @param d1 first decoder string
	 * @param d2 second decoder string
	 * @return percentage of characters that match
	 */
	public static float sameness(String d1, String d2) {
		int result = 0;
		if (d1 != null && d2 != null && d1.length() == d2.length() && d1.length() > 0) {
			for (int i=0; i<d1.length(); i++) {
				if (d1.charAt(i) == d2.charAt(i)) result++;
			}
		} else throw new IllegalArgumentException("The two strings must be non-empty, and must have equal length.");
		return (float)result/d1.length();
	}
	
	
	public static void say(String msg) {
		System.out.println(new Date() + " : " + new Date().getTime() + ": " + msg);
	}
	
	public void write(String msg) {
			say(msg);
	}

	
	
	
	
	
  public String genotypeToStringForHumans()
  {
  	return getDebugLine();
  }
	
  /** Initializes the individual by picking unique words from the word list, ensuring no dupes. */
  public void resetMinConflict(EvolutionState state, int thread) {
    IntegerVectorSpecies s = (IntegerVectorSpecies) species;

  	boolean again = true;
  	int k;
  	int sum;
  	int count = 0;
  	
  	THashMap<String,Integer> hash = new THashMap<String,Integer>();
  	for (int i=0; i<ZodiacDictionary.zodiacTopWords.length; i++) {
  		hash.put(ZodiacDictionary.zodiacTopWords[i], i);
  	}
  	
  	while (again) { // repeat until a min word length sum is met
  		sum = 0;
  		
    	ArrayList<String> list = new ArrayList<String>();
    	for (int i=0; i<ZodiacDictionary.zodiacTopWords.length; i++) {
    		list.add(ZodiacDictionary.zodiacTopWords[i]);
    	}
    	
    	//String shit = "";
    	String word;
  		
    	for(int x=0;x<genome.length;x++) { // randomly select a word from the list, then remove it.
    		if (x % 2 == 0) {
      		k = randomValueFromClosedInterval(0, list.size()-1, state.random[thread]);
      		word = list.get(k);
      		genome[x] = hash.get(word);
      		sum += word.length();
      		//shit += " - (" + k + ")" + word;
      		list.remove(k);
    		} else {
          genome[x] = randomValueFromClosedInterval((int)s.minGene(x), (int)s.maxGene(x), state.random[thread]);
    		}
    		count++;
    		//if (count % 100 == 0) System.out.println("try " + count);
    	}
    	if (sum >= CipherGene.MIN_WORD_LENGTH_SUM) { again = false; /*state.output.message("SHIT " + shit);*/ };
  	}
  	//CipherGene g = new CipherGene(genome);
  	//g.fitnessWordSimple();
  	//state.output.message("RESET " + g.getDebugLine());
  }

  /** checks to see if the given genome represents a list of words with no dupes */
  protected static boolean noDupes(int[] g) {
  	THashSet<String> hash;
		hash = new THashSet<String>();
		for (int i=0; i<g.length; i+=2) {
			hash.add(CipherGene.getWordForGene(g[i]));
		}
		return hash.size() == g.length / 2;
  }

  
  /*public void defaultMutate(EvolutionState state, int thread) { 
  	defaultMutatePosition(state, thread);
  }*/
  
  
  /** mutate a position only (leave the words alone)  */
  public void defaultMutatePosition(EvolutionState state, int thread) {
    IntegerVectorSpecies s = (IntegerVectorSpecies) species;
    /*
    if (s.individualGeneMinMaxUsed())
        {
        if (s.mutationProbability>0.0)
            for(int x=1;x<genome.length;x+=2)
                if (state.random[thread].nextBoolean(s.mutationProbability)) {
                    genome[x] = randomValueFromClosedInterval((int)s.minGene(x), (int)s.maxGene(x), state.random[thread]);
                }
        }
    else  // quite a bit faster
        {
        if (s.mutationProbability>0.0)
            for(int x=0;x<genome.length;x+=2)
                if (state.random[thread].nextBoolean(s.mutationProbability))
                    genome[x] = randomValueFromClosedInterval((int)s.minGene, (int)s.maxGene, state.random[thread]);
        }*/
	if (1==1) throw new RuntimeException("FIX THIS");

  }

  /** a word-uniqueness-preserving mutation - repeats until uniqueness is satisfied, so we don't make too many bad individuals. */
  public void defaultMutatePreserve(EvolutionState state, int thread) {
  	
  	boolean again = true;
  	int tries = 0;
  	int[] testGenome = genome;
  	
  	CipherGene g;
  	double d;
  	if (TRACK_SUCCESS_RATES) {
    	g = new CipherGene(genome);
    	g.fitnessWordSimple();
    	d = g.shitness;
  	}
  	
  	
    IntegerVectorSpecies s = (IntegerVectorSpecies) species;
    /*if (s.individualGeneMinMaxUsed())
        {
        if (s.mutationProbability>0.0)
            for(int x=0;x<genome.length;x++)
                if (state.random[thread].nextBoolean(s.mutationProbability)) {
                  //CipherGene g = new CipherGene(genome);
                  //g.fitnessWordSimple();
                	//state.output.message("before " + g.fitnessWord);
                	//state.output.message("before " + this.genotypeToStringForHumans());
                  	while (again && tries < NUM_TRIES) {
                  		tries++;
                    	testGenome = new int[genome.length];
                  		for (int i=0; i<genome.length; i++) testGenome[i] = genome[i];
                      testGenome[x] = randomValueFromClosedInterval((int)s.minGene(x), (int)s.maxGene(x), state.random[thread]);
                  		if (noDupes(testGenome) && CipherGene.getWordLengthSum(testGenome) >= CipherGene.MIN_WORD_LENGTH_SUM) again = false;
                  	}
                  	genome = testGenome;
                  	if (TRACK_SUCCESS_RATES) {
                      g = new CipherGene(genome);
                      g.fitnessWordSimple();
                      if (g.shitness >= d) mutationGood++;
                      mutationTotal++;
                  	}
                  	//state.output.message("after " + g.fitnessWord);
                  	//state.output.message("after " + this.genotypeToStringForHumans());
                }
        }
    else  // quite a bit faster
        {
        if (s.mutationProbability>0.0)
            for(int x=0;x<genome.length;x++)
                if (state.random[thread].nextBoolean(s.mutationProbability)) {
                	while (again && tries < NUM_TRIES) {
                		tries++;
                  	testGenome = new int[genome.length];
                		for (int i=0; i<genome.length; i++) testGenome[i] = genome[i];
                    testGenome[x] = randomValueFromClosedInterval((int)s.minGene, (int)s.maxGene, state.random[thread]);
                		if (noDupes(testGenome) && CipherGene.getWordLengthSum(testGenome) >= CipherGene.MIN_WORD_LENGTH_SUM) again = false;
                	}
                	genome = testGenome;
                }
        }
    */
	if (1==1) throw new RuntimeException("FIX THIS");

    
  }
  
  /** smarter crossover that only exchanges words that preserve uniqueness - position of the (w,p) tuple
  is not relevent so position in genome is not important */
  public void defaultCrossoverSmarter(EvolutionState state, int thread, VectorIndividual ind) {
  	/* strategy: each individual represents unique list of words.  combine them into a single list of
  	 * unique words.  the new genome is created by selecting words at random from the combined list,
  	 * thereby avoiding the need to repeat the crossover operation unnecessarily to satisfy
  	 * the uniqueness constraint.  This also avoids the position-dependent representation used
  	 * by the standard crossover operators.
  	 */
  	int[] g1 = genome;
  	int[] g2 = (int[]) ind.getGenome();
  	
  	THashMap<String,Integer> hashPositions1 = new THashMap<String,Integer>();
  	THashMap<String,Integer> hashPositions2 = new THashMap<String,Integer>();

  	THashMap<String,Integer> hashWordIndices1 = new THashMap<String,Integer>();
  	THashMap<String,Integer> hashWordIndices2 = new THashMap<String,Integer>();

  	
  	
  	//THashMap<String,Integer> hashCounts = new THashMap<String,Integer>();
  	
  	ArrayList<String> words = new ArrayList<String>();
  	THashSet<String> hashWords = new THashSet<String>();
  	
  	String word;
  	for (int i=0; i<g1.length; i+=2) { // track the positions and count (1 or 2) of each word from both genomes.
  		// we make two hashes of mappings between words/encodings(indices), and words/positions.
  		// this is so we can flip a coin and pick from either list to generate a new allele.
  		word = CipherGene.getWordForGene(g1[i]); // word from first parent
  		//hashCounts.put(word, hashCounts.get(word) + 1);
  		hashWordIndices1.put(word, g1[i]);
  		hashPositions1.put(word, g1[i+1]);
  		if (!hashWordIndices2.containsKey(word)) {
    		hashWordIndices2.put(word, g1[i]);
    		hashPositions2.put(word, g1[i+1]);
  			
  		}
  		if (!hashWords.contains(word)) {
  			words.add(word);
    		hashWords.add(word);
  		}
  		
  		word = CipherGene.getWordForGene(g2[i]);
  		//hashCounts.put(word, hashCounts.get(word) + 1);
  		hashWordIndices2.put(word, g2[i]);
  		hashPositions2.put(word, g2[i+1]);
  		if (!hashWordIndices1.containsKey(word)) {
    		hashWordIndices1.put(word, g2[i]);
    		hashPositions1.put(word, g2[i+1]);
  			
  		}
  		if (!hashWords.contains(word)) {
  			words.add(word);
    		hashWords.add(word);
  		}
  	}
  	
  	int[] newGenome = new int[g1.length];
  	int k;
  	for (int i=0; i<g1.length; i+=2) { // make the child by mixing words at random from the distinct list of words from its parents.
  		k = state.random[thread].nextInt(words.size());
  		word = words.get(k);
			if (state.random[thread].nextBoolean(0.5)) {
				newGenome[i] = hashWordIndices1.get(word);
				newGenome[i+1] = hashPositions1.get(word);
			} else {
				newGenome[i] = hashWordIndices2.get(word);
				newGenome[i+1] = hashPositions2.get(word);
			}
  		words.remove(k);
  	}
  	this.genome = newGenome; 
  }
  
  
  /*public void defaultCrossover(EvolutionState state, int thread, VectorIndividual ind) {
  	//defaultCrossoverPreserve(state, thread, ind);
  	defaultCrossoverSmarter(state, thread, ind);
  }*/
  
  /** a word-uniqueness-preserving crossover - repeats until uniqueness is satisfied, so we don't make too many bad individuals. */
  public void defaultCrossoverPreserveOBSOLETE(EvolutionState state, int thread, VectorIndividual ind) {
  	
  	CipherGene g;
  	double d;
  	if (TRACK_SUCCESS_RATES) {
    	g = new CipherGene(genome);
    	g.fitnessWordSimple();
    	d = g.shitness;
  	}
  	
  	boolean again = true;
  	int tries = 0;
  	int[] original = new int[genome.length];
  	for (int i=0; i<genome.length; i++) original[i] = genome[i];
  	
  	//CipherGene g = new CipherGene(genome);
  	//g.fitnessWordSimple();
  	//state.output.message("xover before " + this.genotypeToStringForHumans());
  	
  	while (again && tries < NUM_TRIES) {
  		tries++;
  		for (int i=0; i<genome.length; i++) genome[i] = original[i]; // use the original each time for crossover.
  		super.defaultCrossover(state, thread, ind);
  		if (noDupes(genome) && CipherGene.getWordLengthSum(genome) >= CipherGene.MIN_WORD_LENGTH_SUM) again = false;
  	}
  	if (TRACK_SUCCESS_RATES) {
    	g = new CipherGene(genome);
    	g.fitnessWordSimple();
    	if (g.shitness >= d) xoverGood++;
    	xoverTotal++;
  	}
  }
  
  /** overrode this so I could put the multiobjective fitness on the same line with the genotypeToStringForHumans. */ 
  public void printIndividualForHumansBROKEN(final EvolutionState state,
      final int log, 
      final int verbosity)
	{
		state.output.println( "Zodiac genome dump: " + EVALUATED_PREAMBLE + Code.encode(evaluated) + " | " + 
				fitness.fitnessToStringForHumans() + " | " + genotypeToStringForHumans(), verbosity, log );
	}
	
	
	
	
	public static void testGene() {
		for (int c=0; c<1000; c++) {
		CipherGene gene; 
		int[] genome = new int[20];
		for (int i=0; i<20; i++) {
			genome[i] = rand().nextInt(500);
		}
		//genome[0] = Zodiac.PLUGGER.length;
		//System.out.println(CipherGene.getWordForGene(genome[0]));
		//gene = ZodiacVectorWordIndividual.getGene(genome, 0);
		gene = new CipherGene(genome);
		
		//gene.genome = genome;
		gene.fitnessWordSimple();
		gene.debugLine();
		}
	}
	
	public static void testSameness() {
		String d1 = Zodiac.solutions[1];
		String d2 = Zodiac.solutions[1];
		
		Date d = new Date();
		float f;
		for (int i=0; i<5000; i++) {
			if (i % 100 == 0) System.out.println(i+"...");
			for (int j=0; j<5000; j++)
				f = sameness(d1,d2);
			
		}
		
		System.out.println((new Date().getTime() - d.getTime())+ " ms.");
				
	}
	
	public static void testGene2() {
		/*
		int[] genome = new int[] {1,5,2,12,3,80,4,166,4,225,1,53,3,323,5,90,6,125,7,358};
		CipherGene g = new CipherGene(genome);
		g.fitnessWordSimple();
		System.out.println(g.getDebugLine());
		
		genome[1] = 6;
		g.fitnessWordSimple();*/
		
		CipherGene g = new CipherGene("?lnesattfs?henifg?oibeouetesaivoc?i?emrr??l?lh?peks?n?");
		
		System.out.println(g.decode());
	}
	
	public static void testBruteAllPositions() {
		String words[] = new String[] { "because", "killing", "people" };
	}
	
	public static void testBrutePluggingWithKnown() {
		String word;
		int[] indices = new int[ZodiacDictionary.brute4Plugger.length];
		for (int i=0; i<ZodiacDictionary.brute4Plugger.length; i++) {
			word = ZodiacDictionary.brute4Plugger[i];
			for (int j=0; j<Zodiac.firstCipherDecoded.length()-word.length(); j++) {
				if (Zodiac.firstCipherDecoded.substring(j,j+word.length()).equals(word)) {
					indices[i] = j;
					break;
				}
			}
		}
		CipherGene g = new CipherGene("");
		String d = g.decoder.toString();
		String result;
		
		int wordcounts[][] = new int[indices.length][8];
		ArrayList words[] = new ArrayList[indices.length];

		int p;
		for (int i=0; i<indices.length; i++) {
			words[i] = new ArrayList();
			word = ZodiacDictionary.brute4Plugger[i];
			g = new CipherGene(Zodiac.getDecoderForWordAtPosition(d, word, indices[i], false));
			result = g.decode();
			result = result.replace(word, "_");
			
			/*
			for (int j=3; j<10; j++) {
				for (int k=0; k<result.length()-j; k++) {
					word = result.substring(k, k+j);
					if (ZodiacDictionary.isWord(word, ZodiacDictionary.D_BIG) || ZodiacDictionary.isWord(word, ZodiacDictionary.D_ZODIAC)) {
						words[i].add(word);
						wordcounts[i][j-3]++;
					}
				}
			}*/
		
			//g.fitnessExperiment141();

			p = 1;
			int count;
			StringBuffer r = new StringBuffer(result);
			for (int j=3; j<9; j++) {
				count = ZodiacDictionary.countDistinctWordsFromIndex(ZodiacDictionary.getWords(r, j, ZodiacDictionary.D_ZODIAC, true)); 
				p *= (count == 0 ? 1 : count);
			}
			
			
			/*String w = "";
			for (int j=0; j<words[i].size(); j++)
				w += words[i].get(j) + " ";
				
			System.out.println(w + "    - " + result);*/
			System.out.println(p + " " + ZodiacDictionary.brute4Plugger[i]);
		}
	}
	
	public static void testSpeed() {
		Date start = new Date();
		for (int i=0; i<1000; i++) {
			int count;
			StringBuffer d = new StringBuffer(Zodiac.firstCipherDecoded);
			for (int j=3; j<9; j++) {
				count = ZodiacDictionary.countDistinctWordsFromIndex(ZodiacDictionary.getWords(d, j, ZodiacDictionary.D_ZODIAC, true)); 
			}
		}
		System.out.println("Speed: " + 1000*1000/(new Date().getTime() - start.getTime()));
		
	}
	
	public static void testSplit() {
		String test = "abc?ef?????haha??poo?whee";
		String[] split = test.split("\\?");
		for (int i=0; i<split.length; i++) System.out.println(split[i].length() + "," + split[i]);
	}
	
	public static void main(String[] args) {
		//testBrutePlugging();
		//testSpeed();
		testSplit();
		
		//System.out.println("iliketurtlesshit".replace("ilike", "_"));
		//testGene2();
		//testGene2();
		//System.out.println("abcdef".matches(".*[0-9A-Z].*"));
		//System.out.println("abcdef".replaceAll(".","_"));
		//testSameness();
		
	}
}
