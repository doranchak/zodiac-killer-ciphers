/* REFACTORED INTO CipherGene */
/* REFACTORED INTO CipherGene */
/* REFACTORED INTO CipherGene */
/* REFACTORED INTO CipherGene */
/* REFACTORED INTO CipherGene */
/* REFACTORED INTO CipherGene */
/* REFACTORED INTO CipherGene */
/* REFACTORED INTO CipherGene */
/* REFACTORED INTO CipherGene */

package com.zodiackillerciphers.old;

import ec.EvolutionState;
import ec.util.Code;
import ec.vector.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

abstract class ZodiacVectorWordIndividualOld extends IntegerVectorIndividual implements ZodiacIndividual {

	private CipherGene _gene;
	public static int NUM_TRIES = 1000;
	private int makeCount = 0;
	public static final boolean TRACK_SUCCESS_RATES = false; // when true, xover/mutation operators track how often they produce an equal or better individual (warning: does not work for multiobjective)
	
	public static int xoverTotal = 0;
	public static int xoverGood = 0;

	public static int mutationTotal = 0;
	public static int mutationGood = 0;
	
	
	/** trap the genome setter, so we can automatically create a CipherGene to go with this individual.*/
  public void setGenome(Object gen) {
  	super.setGenome(gen); 
  	makeGene();
  }

	
	/* does not use the mutations implemented in Zodiac.java. */
	/*public void defaultMutate(EvolutionState state, int thread) {
		IntegerVectorSpecies s = (IntegerVectorSpecies) species;
    if (s.mutationProbability>0.0)
    	if (state.random[thread].nextBoolean(s.mutationProbability)) {
    		Gene spawn = Zodiac.mutate(getGene());
    		genome = spawn.getGenome();
    	}
	}*/

	public CipherGene getGene() {
		if (_gene == null) makeGene();
		return _gene;
	}
	/* make a zodiac gene out of a ZodiacVectorIndividual */
	/*public CipherGene getGene() {
		if (Zodiac.WORD_ENCODING == 0) {
			return getGene(275);
		} else {
			return getGene(0);
		}
	}	*/
	/* make a zodiac gene out of a ZodiacVectorIndividual */
	private void makeGene(/*int startPosition*/) {
		makeCount++;
		if (makeCount > 1) System.err.println("huh?  makeCount " + makeCount);
	  if (ZodiacDictionary.zodiacCorpus == null) ZodiacDictionary.initZodiacCorpus();
	  CipherGene gene = new CipherGene(genome);
	  _gene = gene;
	  
	}	
	
	  
	  /*if (Zodiac.WORD_ENCODING == 0) {
		  String decoder = gene.decoder;
		  int pos = startPosition;
		  String word;
		  for(int x=0; x<forGenome.length; x++) {
		  	word = ZodiacDictionary.zodiacCorpus[forGenome[x]];
		  	pos += word.length(); // start at the last word
		  }
		  
		  //Zodiac.say("pos "+ pos);
	  	
		  for(int x=forGenome.length-1; x>=0; x--) { // go backwards, to preserve the most decoded ciphertext possible
		  	word = ZodiacDictionary.zodiacCorpus[forGenome[x]];
		  	pos -= word.length();
		  	//Zodiac.say("p " + pos + " < " + (decoder.length() - word.length()));
		  	if (pos < Zodiac.cipher[Zodiac.CIPHER].length() - word.length()) {
		  		decoder = Zodiac.getDecoderForWordAtPosition(decoder, word, pos);
			  	gene.decoder = decoder;
		  	}
		  	//Zodiac.say("x " + x + ", w " + word + ", d " + gene.decode() + ", pos " + pos);
		  }
		  
		  gene.decoder = decoder;
	  } else { */
	  	
	  	//System.out.println("encoding " + gene.encoding);
	  	//gene.fitnessWord(genome, 0);
	 // }
	 // return gene;

	public void setGene(CipherGene gene) {
		_gene = gene;
	}
	
  public String genotypeToStringForHumans()
  {
  	return getGene().getDebugLine();
  }

  /** could be quite inefficient, since we sort for every call */
  private String getComparison() {
  	// TODO: VERY IMPORTANT: must compare words directly, because modulo on word list may repeat words.
  	// STUPID HARDCODED THING FOR TEMP TEST:
  	genome[0] = 1;
  	genome[2] = 2;
  	genome[4] = 3;
  	genome[6] = 4;
  	genome[8] = 4;
  	genome[10] = 1;
  	genome[12] = 3;
  	genome[14] = 5;
  	genome[16] = 6;
  	genome[18] = 7;
  	String[] sortem = new String[((int[]) genome).length/2];
  	for (int i=0; i<genome.length; i+=2) sortem[i/2] = "" + genome[i] + "," + CipherGene.getPositionForGene(genome[i+1]); 
  	java.util.Arrays.sort(sortem);
  	String result = "";
  	for (int i=0; i<sortem.length; i++) result += sortem[i] + ",";
  	return result;
  }
  
  /** z1 and z2 are equal iff for each (w,p) in z1's genome, there exists a corresponding (w,p) in z2's genome. */
  public boolean equals (Object o) {
  	if (o == null || !(o instanceof ZodiacVectorWordIndividualOld))
  		return false;
  	if (genome == null) return false;
  	
  	// two of these are only equal if for every (w,p) in o1, there is a corresponding (w,p) in o2.  they do not
  	// have to be in the same positions.
  	/*
  	ArrayList list = new ArrayList();
  	String key;
  	for (int i=0; i<genome.length; i+=2) {
  		key = ""+genome[i]+","+genome[i+1];
  		list.add(key);
  	}
  	int[] genome2 = ((ZodiacVectorWordIndividual)o).genome;
  	for (int i=0; i<genome2.length; i+=2) {
  		key = ""+genome2[i]+","+genome2[i+1];
  		if (!list.contains(key)) {
  			return false;
  		}
  		list.remove(key);
  	}
  	if (list.size() == 0) return true;
  	return false;*/
  	return ((ZodiacVectorWordIndividualOld)o).getComparison().equals(this.getComparison());
  	
  }
  
  
  public int hashCode() {
  	return getComparison().hashCode();
  }

  /** Initializes the individual by picking unique words from the word list, ensuring no dupes. */
  public void resetWithUniques(EvolutionState state, int thread) {
    IntegerVectorSpecies s = (IntegerVectorSpecies) species;

  	boolean again = true;
  	int k;
  	int sum;
  	int count = 0;
  	
  	HashMap<String,Integer> hash = new HashMap<String,Integer>();
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
  private static boolean noDupes(int[] g) {
  	HashSet<String> hash;
		hash = new HashSet<String>();
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
    //if (s.individualGeneMinMaxUsed())
    if (false) 
        {
        //if (s.mutationProbability>0.0)
    	if (false)
            for(int x=1;x<genome.length;x+=2)
            	//if (state.random[thread].nextBoolean(s.mutationProbability)) {
                if (false) {
                    genome[x] = randomValueFromClosedInterval((int)s.minGene(x), (int)s.maxGene(x), state.random[thread]);
                }
        }
    else  // quite a bit faster
        {
        //if (s.mutationProbability>0.0)
    	if (false)
            for(int x=0;x<genome.length;x+=2)
                //if (state.random[thread].nextBoolean(s.mutationProbability))
            	if (false) ;
                    //genome[x] = randomValueFromClosedInterval((int)s.minGene, (int)s.maxGene, state.random[thread]);
        }
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
    if (false) //if (s.individualGeneMinMaxUsed())
        {
        if (false) //if (s.mutationProbability>0.0)
            for(int x=0;x<genome.length;x++)
                if (false) { //if (state.random[thread].nextBoolean(s.mutationProbability)) {
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
        if (false) //if (s.mutationProbability>0.0)
            for(int x=0;x<genome.length;x++)
                if (false) { //if (state.random[thread].nextBoolean(s.mutationProbability)) {
                	while (again && tries < NUM_TRIES) {
                		tries++;
                  	testGenome = new int[genome.length];
                		for (int i=0; i<genome.length; i++) testGenome[i] = genome[i];
                    //testGenome[x] = randomValueFromClosedInterval((int)s.minGene, (int)s.maxGene, state.random[thread]);
                		if (noDupes(testGenome) && CipherGene.getWordLengthSum(testGenome) >= CipherGene.MIN_WORD_LENGTH_SUM) again = false;
                	}
                	genome = testGenome;
                }
        }
    
    
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
  	
  	HashMap<String,Integer> hashPositions1 = new HashMap<String,Integer>();
  	HashMap<String,Integer> hashPositions2 = new HashMap<String,Integer>();

  	HashMap<String,Integer> hashWordIndices1 = new HashMap<String,Integer>();
  	HashMap<String,Integer> hashWordIndices2 = new HashMap<String,Integer>();

  	
  	
  	//HashMap<String,Integer> hashCounts = new HashMap<String,Integer>();
  	
  	ArrayList<String> words = new ArrayList<String>();
  	HashSet<String> hashWords = new HashSet<String>();
  	
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
  public void defaultCrossoverPreserve(EvolutionState state, int thread, VectorIndividual ind) {
  	
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
  
  /*
  public static void testEquals() {
  	ZodiacVectorWordIndividual z1 = new ZodiacVectorWordIndividual();
  	ZodiacVectorWordIndividual z2 = new ZodiacVectorWordIndividual();
  	z1.setGenome(new int[] {1,2,3,4,5,8});
  	z2.setGenome(new int[] {3,4,5,8,1,2});
  	
  	System.out.println("equals? " + z1.equals(z2));
  	System.out.println("hashes " + z2.hashCode() + "," + z1.hashCode());
  	
  	HashSet h = new HashSet();
  	h.add(z1);
  	h.add(z2);
  	System.out.println(h.size());
  }*/
  
  public static void main(String[] args) {
  	//testEquals();
  }
  
  

}

