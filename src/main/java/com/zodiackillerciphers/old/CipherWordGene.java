package com.zodiackillerciphers.old;

import java.util.*;

import java.text.*;
import java.io.File;

import com.zodiackillerciphers.ciphers.Ciphers;

import ec.EvolutionState;
import ec.multiobjective.MultiObjectiveFitness;
import ec.util.Code;
import ec.util.MersenneTwisterFast;
import ec.util.Output;
import ec.vector.*;

import gnu.trove.*;

/** This gene represents piles of words plugged into the cipher.  This representation enforces a zero-conflict
 * constraint for all instances.  No genes are allowed to represent word placements that result in conflicts
 * in the derived decoder.  If any are found, they are given 0 fitness.  Encoding is done with word/position tuples.
 */
public class CipherWordGene extends CipherGene {

	//public static int globalId = 0;
	//public int id;
	
	//public int[] genomeClone;
	/** string used to identify serialized instances of ZodiacDictionary.isWordPoolHash */
	public static final String WHICH_POOL = "wordpool-1600";
	
	/** if true, track mutation/crossover success rates by counting non-wildcards */
	public static boolean TRACK_SUCCESS_RATES = false;
	
	public static final boolean DEBUG = false;
	public static boolean USE_WORD_POINTER = false; // if true, we mutate and init by selecting words sequentially from word pool instead of randomly. 
	public static int wordPointer = 0; // used for sequential word mutation, to help guarantee that all words get a fair shot at being used
	//OBSOLETED public static int FUZZY_MIN_LEN = 5; // min chars to match on fuzzy matches
	
	public static boolean ENFORCE_TIGHTER_CONSTRAINTS = false; // if true, we do stuff like look for word feasibilities between word placements.
	
	public static boolean USE_DIVIDE_AND_CONQUER = true; // if enforce tighter constraints, and if this is true, then use divide and conquer technique for larger word feasibility tests.
	public static int DAC_MAX_LEN = 10;
	
	/** TODO: if true, allele put/remove operations will use the constraint db directly instead of maintaining its own incremental constraint detection. */ 
	public static boolean ALLELE_OP_USE_CONSTRAINT_DB = false; // currently does nothing
	
	/** if true, fitness evaluation tracks all "dead ends" in a master list indexed by sortedKey.  if any other individuals are found
	 * with the same sortedKey, it is truncated from the population by setting fitness to zero and then re-initializing the genome.
	 */
	public static boolean TRUNCATE_DEAD_ENDS = false;
	public static THashSet<String> deadEndHash; // used to track dead ends by indexing by sortedKey
	public static float deadEndThreshold = 0.9f; // specifies percentage of non-wildcard characters obtained before candidate solution is considered to be a dead end.
	
	private String debug = "";
	
	public static THashMap<String, String> decoderHash;
	public static int NUM_OBJECTIVES=2;
	public static int GENOME_LENGTH=20;
	public static int RESET_MIN_WORD_LENGTH=4;
	public static int MIN_WORD_LENGTH = 4; // min word length to consider for fitness sharing evaluations
	public static int ALLELE_ADD_MIN_WORD_LENGTH = 4; // alleleAdd rejects words that are shorter than this length
	
	/**full decoded plaintext after conflicted found words are removed */
	public StringBuffer fullDecoded;
	/** number of word/position tuples found in the word search (this is a pre-conflict count) */
	public int foundWordCount;
	/** number of conflicts detected among all words found in the word search */
	public int foundWordConflicts;
	/** number of edges involved in conflict graph generated during word search */
	public int foundWordEdgeNodes;
	/** number of nodes remaining after vertex cover applied */
	public int foundWordNodeCover;
	
	/** cipher start and end: represents the chunk of the cipher we wish to stuff words into. */
	
	/* known cipher, 51 char subset of 30 unique symbols:  #BVpeXqWq_F#8c+@9A9B%OT5RUc+_dYq_^SqWVZeGYKE_TYA9%# 
	 * This cipher range decodes 255 characters (62.5%) of the cipher.
	 * */
	//public static int CIPHER_START = 286; // index of start char
	//public static int CIPHER_END = 286+51-1; // index of end char.  length is substring is CIPHER_END-CIPHER_START+1
	
	/* 50 more chars than the above */
	//public static int CIPHER_START = 236; // index of start char
	//public static int CIPHER_END = 286+51-1; // index of end char.  length is substring is CIPHER_END-CIPHER_START+1
	
	/* known cipher, entire cipher */
	//public static int CIPHER_START = 0;
	//public static int CIPHER_END = Zodiac.cipher[Zodiac.CIPHER].length() - 1;
	
	/* known cipher, 117-char region that includes all symbols */
	//public static int CIPHER_START = 144;
	//public static int CIPHER_END = 144+117-1;
	
	/* known cipher, 52-char region that decodes 90.44% of the cipher. */
	public static int CIPHER_START = 53;
	public static int CIPHER_END = 53+52-1;
	
	/* rayN cipher, 52-char region that decodes 90.48% of the cipher. */
	//public static int CIPHER_START = 311;
	//public static int CIPHER_END = 311+52-1;
	
	/* unknown cipher, 50 char subset of 30 unique symbols 
	 * This cipher range decodes 198 characters (58.235294%) of the cipher.
	 * */
	//public static int CIPHER_START = 273;
	//public static int CIPHER_END = 273+50-1;
	
	/* unknown cipher, 50 char subset of 32 unique symbols, containing NI5FBc (the interestingly joined trigrams) 
	 * This cipher range decodes 211 characters (62.058824%) of the cipher.
	 * */
	//public static int CIPHER_START = 190;
	//public static int CIPHER_END = 190+50-1;

	/* generic container for statistics.  maps stat name to value. */ 
	public static THashMap<String, Float> stats;
	
	
	//THashMap<String, THashSet<String>> conflictWordsL;
	//THashMap<String, THashSet<String>> conflictWordsR;
	THashMap<String, THashSet<Edge>> edgeMap;
	THashSet<Edge> edges;
	
	/** used to count the number of decoded characters as a fitness measure */
	public static THashMap<Character, Integer> symbolCounts;
	
	public static String blankDecoded;
  
	public static String[] wordPool2 = new String[] {"i", "like", "killing", "people", "because", "it", "is", "so", "much", "fun", "it", "is", "more", "fun", "than", "killing", "wild", "game", "in", "the", "forrest", "because", "man", "is", "the", "most", "dangertue", "anamal", "of", "all", "to", "kill", "something", "gives", "me", "the", "most", "thrilling", "experence", "it", "is", "even", "better", "than", "getting", "your", "rocks", "off", "with", "a", "girl", "the", "best", "part", "of", "it", "is", "thae", "when", "i", "die", "i", "will", "be", "reborn", "in", "paradice", "snd", "all", "the", "i", "have", "killed", "will", "become", "my", "slaves", "i", "will", "not", "give", "you", "my", "name", "because", "you", "will", "try", "to", "sloi", "down", "or", "stop", "my", "collecting", "of", "slaves", "foe", "my", "afterlife"};
	
	//public static String[] wordPool = new String[] {"killing", "people", "because", "something", "better", "paradice", "killed", "become", "slaves", "collecting", "afterlife"};
	
	/* word guesses for unknown cipher */
	//public static String[] wordPool = new String[] {"killing", "people", "because", "something", "better", "paradice", "killed", "become", "slaves", "collecting", "afterlife", "zodiac", "speaking", "halloween", "ladies", "school", "police", "gun", "bomb", "blood", "explode", "knife", "death", "dead", "will", "october", "november", "girl" }; 
	
	/* words in the 51 char chunk of the 408 */
	//public static String[] wordPool = new String[] {"become","my","slaves","i","will","not","give","you","my","name","because","you","will"};
	
	
	/* words in the 51 char chunk of the 408 mixed with other top z corpus words*/
	//SMALL SET - public static String[] wordPool = new String[] {"become","my","slaves","i","will","not","give","you","my","name","because","you","will","the", "you", "and", "was", "have", "this", "that", "with", "for", "will", "not", "them", "all", "who", "her", "they"};
	//public static String[] wordPool = new String[] {"become","my","slaves","i","will","not","give","you","my","name","because","you","will","the", "i", "to", "of", "a", "in", "you", "and", "my", "is", "was", "have", "this", "it", "that", "with", "me", "for", "be", "will", "on", "not", "them", "by", "all", "who", "her", "they", "shall", "if", "but", "am", "about", "up", "she", "do", "your", "out", "one", "when", "then", "zodiac", "people", "like", "would", "some", "as", "are", "so", "police", "missed", "did", "car", "back", "speaking", "there", "or", "no", "last", "because", "at", "were", "time", "thing", "more", "from", "can", "never", "killing", "just", "down", "bomb", "way", "very", "vallejo", "think", "their", "slaves", "see", "s", "rather", "over", "off", "now", "nice", "list", "im", "had", "get", "editor", "could", "buttons", "been", "what", "tell", "school", "san", "park", "others", "only"}; // this was used for a lot of experiments up to experiment 254.  it is known words in solution + around 100 top-occurring words in the z corpus.

	/* 200 top words, seeded with known solution of cipher substring */
	//public static String[] wordPool = ZodiacDictionary.getContents(new File("/Users/doranchak/projects/work/java/zodiac/letters/wordpool-200.txt"), false).split(System.getProperty("line.separator"));
	/* n top words, seeded with known solution of cipher substring */
	public static String[] wordPool = ZodiacDictionary.getContents(new File("/Users/doranchak/projects/work/java/zodiac/letters/" + WHICH_POOL + ".txt"), false).split(System.getProperty("line.separator"));
	
	/* this is all 396 unique words from the z corpus that occur at least twice in the z corpus. removed "s".  */ 
	//public static String[] wordPool = new String[] {"the","i","to","of","a","in","you","and","my","is","was","have","this","it","that","with","me","for","be","will","on","not","them","by","all","who","her","they","shall","if","but","am","about","up","she","do","your","out","one","when","then","zodiac","people","like","would","some","as","are","so","police","missed","did","car","back","speaking","there","or","no","last","because","at","were","time","thing","more","from","can","never","killing","just","down","bomb","way","very","vallejo","think","their","slaves","see","rather","over","off","now","nice","list","im","had","get","editor","could","buttons","been","what","tell","school","san","park","others","only","none","light","killed","his","he","gun","got","good","girl","fun","dont","cop","cipher","went","well","wear","shot","said","ps","please","new","much","most","man","little","know","kill","ive","into","dear","cops","around","wont","which","want","tut","theyd","take","streets","set","riverside","red","print","pig","part","page","night","nasty","name","must","murder","make","knife","july","how","him","here","help","happy","hands","frunt","f","die","cut","chronicle","christmass","children","bus","blue","black","best","away","asked","any","yes","year","work","west","wearing","washington","walking","waiting","untill","two","titwillo","times","than","stop","state","something","should","shoot","saw","run","public","prove","parts","paper","p","other","north","next","min","might","melvin","maybe","lot","look","longer","life","let","leave","kind","keep","hope","head","going","give","francisco","first","few","even","end","doesnt","death","control","collecting","code","city","center","boy","blast","area","an","also","after","yours","yet","wouldnt","why","whole","while","whether","where","we","water","warm","victom","use","under","try","town","top","too","told","throat","though","things","thats","stamps","st","sorry","someone","small","sitting","sight","sick","shure","sfpd","seven","sept","sent","seen","seat","scream","say","same","road","right","report","ready","re","radians","quite","put","pulled","price","presidio","plunged","play","pick","phone","peekaboo","paul","parked","paradice","open","ones","oct","news","need","movie","move","mount","marco","maple","machine","loose","lonely","library","left","laugh","knee","killings","kids","inches","hills","high","hey","hell","having","has","hand","go","glorification","gave","game","fry","fran","found","flash","fk","fired","fire","finding","find","felt","feet","fake","except","examiner","every","ever","evening","etc","enterprise","enjoy","ended","else","easy","each","dress","double","dig","diablo","details","dead","date","dark","crack","count","come","clean","cheer","catch","cars","came","cab","brown","brand","bought","book","blood","better","being","began","before","become","beautiful","bay","bates","bat","barrel","aug","attention","ask","anyone","angry","ammo","always","alone","alley","aim","again","address","across"};
	//public static String[] wordPool = ZodiacDictionary.getContents(new File("/Users/doranchak/projects/work/java/zodiac/letters/wordpool-396.txt"), false).split(System.getProperty("line.separator"));
	
	/* top 600 words of the z corpus; it includes words that only appear once in the z corpus. */
	//public static String[] wordPool = ZodiacDictionary.getContents(new File("/Users/doranchak/projects/work/java/zodiac/letters/wordpool-600.txt"), false).split(System.getProperty("line.separator"));
	//public static String[] wordPool = ZodiacDictionary.getContents(new File("/Users/doranchak/projects/work/java/zodiac/letters/wordpool-595.txt"), false).split(System.getProperty("line.separator"));
	
	/* load the entire zodiac corpus (1206 words) as the word pool */
	//public static String[] wordPool = ZodiacDictionary.getContents(new File("/Users/doranchak/projects/work/java/zodiac/letters/allwords-uniq.txt"), false).split(System.getProperty("line.separator"));
	
	
	/* top 100 z corpus words for the 51 char chunk */
	//public static String[] wordPool = new String[] {"the", "i", "to", "of", "a", "in", "you", "and", "my", "is", "was", "have", "this", "it", "that", "with", "me", "for", "be", "will", "on", "not", "them", "by", "all", "who", "her", "they", "shall", "if", "but", "am", "about", "up", "she", "do", "your", "out", "one", "when", "then", "zodiac", "people", "like", "would", "some", "as", "are", "so", "police", "missed", "did", "car", "back", "speaking", "there", "or", "no", "last", "because", "at", "were", "time", "thing", "more", "from", "can", "never", "killing", "just", "down", "bomb", "way", "very", "vallejo", "think", "their", "slaves", "see", "s", "rather", "over", "off", "now", "nice", "list", "im", "had", "get", "editor", "could", "buttons", "been", "what", "tell", "school", "san", "park", "others", "only"};
	
	/** hash the words to their allele representations for fast lookups */
	public static THashMap<String, Integer> wordPoolMap;
	
	/* top 40 words, >2 chars, in the z corpus */ 
	public static String[] top40Pool = new String[] {"the", "you", "and", "was", "have", "this", "that", "with", "for", "will", "not", "them", "all", "who", "her", "they", "shall", "but", "about", "she", "your", "out", "one", "when", "then", "zodiac", "people", "like", "would", "some", "are", "police", "missed", "did", "car", "back", "speaking", "there", "last", "because"};
	
	/* test sample - 20 words (>4 chars in length) known to be in the cipher */
	//public static String[] wordPool = new String[] {"killing", "people", "because", "killing", "forrest", "because", "dangertue", "anamal", "something", "gives", "thrilling", "experence", "better", "getting", "rocks", "reborn", "paradice", "killed", "become", "slaves", "because", "collecting", "slaves", "afterlife"};
	
	/* z corpus words that are >4 chars in length and have appeared more than once in the z corpus. */
	/*public static String[] wordPool = new String[] {"about", "across", "address", "after", "again", "alley", "alone", "always", "angry", "anyone", "around", "asked", "attention", "barrel", "bates", "beautiful", "because", "become", "before", "began", "being", "better", "black", "blast", "blood", "bought", 
		"brand", "brown", "buttons", "catch", "center", "cheer", "children", "christmass", "chronicle", "cipher", "clean", "collecting", "control", "could", "count", "crack", "death", "details", "diablo", "doesnt", "double", "dress", "editor", "ended", "enjoy", "enterprise", "evening", "every", "examiner", 
		"except", "finding", "fired", "first", "flash", "found", "francisco", "frunt", "glorification", "going", "hands", "happy", "having", "hills", "inches", "killed", "killing", "killings", "knife", "laugh", "leave", "library", "light", "little", "lonely", "longer", "loose", "machine", "maple", "marco", 
		"maybe", "melvin", "might", "missed", "mount", "movie", "murder", "nasty", "never", "night", "north", "other", "others", "paper", "paradice", "parked", "parts", "peekaboo", "people", "phone", "please", "plunged", "police", "presidio", "price", "print", "prove", "public", "pulled", "quite", "radians", 
		"rather", "ready", "report", "right", "riverside", "school", "scream", "seven", "shall", "shoot", "should", "shure", "sight", "sitting", "slaves", "small", "someone", "something", "sorry", "speaking", "stamps", "state", "streets", "thats", "their", "there", "theyd", "thing", "things", "think", "though", 
		"throat", "times", "titwillo", "under", "untill", "vallejo", "victom", "waiting", "walking", "washington", "water", "wearing", "where", "whether", "which", "while", "whole", "would", "wouldnt", "yours", "zodiac"};
	*/
	/** hash that links substrings to words that contain them.  Each word, in turn, is linked to an integer indicating the substring's offset. */
	public static THashMap<String, THashMap<String, Integer>> substringPool3;
	public static THashMap<String, THashMap<String, Integer>> substringPool4;
	public static THashMap<String, THashMap<String, Integer>> substringPool5;
	public static THashMap<String, THashMap<String, Integer>> substringPool6;
	public static THashSet<String> substringPrefixesPool3;
	public static THashSet<String> substringPrefixesPool4;
	public static THashSet<String> substringPrefixesPool5;
	public static THashSet<String> substringPrefixesPool6;
	//public static THashMap<String, THashMap<String, Integer>> substringGuessPool;
	public static THashMap<String, THashMap<String, Integer>> substringDictPool;
	public static THashMap<String, THashMap<String, Integer>> substringZodDictPool;
	public static THashMap<String, THashMap<String, Integer>> substringZodSmegDictPool;
	//public static String[] wildCardPool = new String[] {"?","??","???","????","?????","??????","???????","????????","?????????"}; 

	
	/** a hash of the word pool, used for quick dictionary searches */
	public static THashSet<String> wordPoolHash;
	/** a companion hash for wordPoolHash.  this hash indexes all prefixes for faster dictionary searches on arbitrary strings. */
	public static THashSet<String> wordPoolPrefixesHash;
	/** a map of the word pool to word allele representations */
	//public static THashMap<String, Integer> wordPoolRepsHash; //might not need this
	
	/* max number of times to repeat mutation/crossover until zero conflict constraint is satisfied */ 
	public static int NUM_TRIES = 1;
	
	
	/** any allele less than (wildCardProbability*maxGeneAllele) will receive a wildcard placeholder. */
	public static float wildCardProbability = 0.05f;
	/** maximum allele represented in our genome */
	public static int maxAllele = 2000;
	
	/** members used by progressive conflict analysis */
	
	/** string to extract words that are derived from this genome's word placements */
	public StringBuffer conflictDerivedWords;
	/** wildcard init for the conflictDerivedWords */
	public static String conflictDerivedWordsReset; 
	
	/** Map of words to positions */
	THashMap<String,Integer> conflictWordHash;
	/** static Map that caches feasibility lookups on strings (a string is feasible if it is completely covered
	 * by one or more words.)
	 */ 
	public static THashMap<String, Boolean> conflictDictionaryCache;
	/** static Map of decoder characters to decoder positions */
	// REMOVED b/c redundant with Zodiac.alphabetHash.  public static THashMap<Character, Integer> conflictDecoderCharHash;;
	/** static Map of decoder positions to arrays of decoded string positions */ 
	public static THashMap<Integer,int[]> conflictDecoderHash;
	/** array with counts of how many words contribute to each position of a decoder */
	public int[] conflictDecoderWordCount;
	/** Map of words+position to letters' decoder positions */ 
	public THashMap<String,int[]> conflictWordsToDecoder;
	
	
	/** A cache of the parent genes' constraint DB lookups */
	//THashMap<String, THashSet<String>> constraintCacheParent1;
	//THashMap<String, THashSet<String>> constraintCacheParent2;
	/** A cache for this gene's constraint DB lookups */
	//THashMap<String, THashSet<String>> constraintCacheOffspring;
	
	
	
	public void CipherWordGene() {
		resetDecoder();
	}
	/** randomly create a zero-conflict genome of words */
	/*public int[] createRandomGenome() {
		int length = 0; // track word length; once we go over the cipher length, discard the last allele.
		int[] newGenome = new int[genome.length];
		for (int i=0; i<genome.length; i++) {
			
		}
	}*/
	
	
	/** delete me */
	/*
	public String getIdents() {
		return System.identityHashCode(conflictDerivedWords) + "/" + 
		System.identityHashCode(conflictWordHash) + "/" + 
		System.identityHashCode(conflictDecoderWordCount) + "/" + 
		System.identityHashCode(conflictWordsToDecoder) + "/" + 
		System.identityHashCode(decoder) + "/" + 
		System.identityHashCode(decoded);
	}*/
	
	/** trim all words that extend onto or beyond the end of the ciphertext */
	public void trimExcess(int[] genome) {
	}
	
	/** returns the word represented by the given genome allele value.   */
	public static String getWordFromAllele(int allele) {
		if (allele < maxAllele*wildCardProbability) {
			return "";
		}
		return wordPool[allele % wordPool.length];
	}
	/** returns the position represented by the given genome allele value. */
	public static int getPositionFromAllele(int allele) {
		//return allele % Zodiac.cipher[Zodiac.CIPHER].length();
		//System.out.println(" whee " + (CIPHER_START + (allele % (CIPHER_END - CIPHER_START))));
		return CIPHER_START + (allele % (CIPHER_END - CIPHER_START + 1));
	}
	
	/** return a suitable hash key for the given word/pos pair encoding.
	 * if the genome represents a wildcard, return null, b/c we don't want to hash it.
	 * if the genome represents a word with length < minWordLength, return null, b/c we don't want to hash it.
	 **/
	public static String makeKeyFor(int word, int pos, int minWordLength) {
		String w = getWordFromAllele(word);
		if (w.equals("") || w.length() < minWordLength) return null;
		return w + " " + getPositionFromAllele(pos);
	}

	/** return a suitable hash key for all word/pos pairs from the given genome.  sort by
	 *  word/pos, so we can compare uniqueness of keys, subjected to the minWordLength (only
	 *  words with length >= minWordLength are included in the key).
	 **/
	public String makeSortedKeyFor(int[] genome, int minWordLength) {
		String w;
		ArrayList<String> list = new ArrayList<String>();
		
		
		if (conflictWordHash != null) {
			Iterator<String> it = conflictWordHash.keySet().iterator();
			while (it.hasNext()) {
				w = it.next();
				if (w.equals("") || w.length() < minWordLength) {
				} else {
					list.add(w + " " + conflictWordHash.get(w));
				}
			}
		}
		/*
		for (int i=0; i<genome.length; i+=2) {
			w = getWordFromAllele(genome[i]);
			if (w.equals("") || w.length() < minWordLength) {
			} else {
				list.add(w + " " + getPositionFromAllele(genome[i+1]));
			}
		}*/
		Object[] sorted = list.toArray();
		Arrays.sort(sorted);
		String key = "";
		for (int i=0; i<sorted.length; i++) key += (String)sorted[i]+" ";
		return key;
	}

	
	/** returns true if the given word, placed into the given position of the given genome, will produce a conflict.
	 * try to make this run fast, since we will be calling it a lot.
	 * output: this CipherWordGene's decoder is updated with a zero-conflict merged decoder iff this genome has no conflict.
	 **/
	public boolean hasConflictOBSOLETE(int[] genome) {
		//int position = 0; // current position
		
		if (wordPoolHash == null) initWordPoolHash();
		
		String cipher = Zodiac.cipher[Zodiac.CIPHER];
		String mergedDecoder = ""; // current zero-conflicts merged decoder
		//for (int i=0; i<Zodiac.alphabet[Zodiac.CIPHER].length(); i++) mergedDecoder += "?";
		mergedDecoder = resetDecoder;
		
		if (blankDecoded == null) {
			blankDecoded = "";
			for (int i=0; i<Ciphers.cipher[Zodiac.CIPHER].cipher.length(); i++)
				blankDecoded+="?";
		}
		StringBuffer overlapCheck = new StringBuffer(blankDecoded);
		
		String candidateDecoder; // decoder for the word we're looking at, to inspect for conflicts before merging.
		String newMergedDecoder;
		String word;
		THashSet<String> checkDupes = new THashSet<String>();
		int position;
		for (int i=0; i<genome.length; i+=2) {
			word = getWordFromAllele(genome[i]);
			if (word.length() > 0) {
				if (checkDupes.contains(word)) { return true;}
				checkDupes.add(word);
				position = getPositionFromAllele(genome[i+1]);
				
				//if (position + word.length() > cipher.length()) return true; // do not allow words to extend beyond the end of the cipher. buggy version (didn't take CIPHER_END into account)
				if (position + word.length() > CIPHER_END + 1) { return true;}
					
				/* reject any words that overlap other words in the decoded cipher (even if the placements are feasible). */
				//System.out.println(position + "," + (position+word.length()) + ", chunk " + overlapCheck.substring(position, position+word.length()));
				if (overlapCheck.substring(position, position+word.length()).contains("_")) { return true;}
					
				for (int j=position; j<position+word.length(); j++) overlapCheck.setCharAt(j, '_');
				//System.out.println(overlapCheck + ", " + word);
				
				candidateDecoder = Zodiac.getDecoderForWordAtPosition(mergedDecoder, word, position, true);
				if (candidateDecoder == null) { return true;}
				//System.out.println("word " + word);
				//System.out.println(" - merge " + mergedDecoder);
				//System.out.println(" - candi " + candidateDecoder);

				/*
				newMergedDecoder = "";  char c1; char c2; 
				for (int j=0; j<candidateDecoder.length(); j++) {
					c1 = candidateDecoder.charAt(j);
					c2 = mergedDecoder.charAt(j);
					if (c1 != '?' && c2 != '?' && c1 != c2) return true;
					if (c1 != '?') newMergedDecoder += c1; 
					else newMergedDecoder += c2;
				}
				mergedDecoder = newMergedDecoder;*/
				//System.out.println(candidateDecoder);
				mergedDecoder = candidateDecoder;
			}
		}

		/** analyze the decoded string.  if we have forced infeasible one- or two-letter words, then 
		 * reject this solution. 
		 */
		String oldDecoder = this.decoder.toString();
		this.decoder = new StringBuffer(mergedDecoder);
		StringBuffer d = new StringBuffer(decode());
		for (int i=0; i<genome.length; i+=2) {
			word = getWordFromAllele(genome[i]);
			if (word.length() > 0) {
				checkDupes.add(word);
				position = getPositionFromAllele(genome[i+1]);
				d.replace(position, position+word.length(), word.replaceAll(".", "_"));
			}
		}
		String[] forcedWords = d.toString().split("_+");
		if (forcedWords != null) {
			for (int i=0; i<forcedWords.length; i++) {
				word = forcedWords[i];
				//System.out.println(i + " word " + word);
				/* only consider the first (last) word if it is prefixed (suffixed) by an underscore, indicated it is a forced word that is not a substring of some other word. */
				if (i == 0 && d.charAt(0) != '_') continue;
				if (i == forcedWords.length - 1 && d.charAt(d.length()-1) != '_') continue;
				
				if (word.indexOf('?') == -1) {
					//System.out.println(word);
					if (word.length() == 1) {
						if (!isWord(word)) {
							this.decoder = new StringBuffer(oldDecoder);
						  return true;
						}
					} else if (word.length() == 2) {
						if (!isWord(word)) {
							this.decoder = new StringBuffer(oldDecoder);
							return true;
						}
					} else if (word.length() == 3) {
						// word possibilities: 1 letter + 2 letter, 2 letter + 1 letter, or 3 letter
						//System.out.println(word);
						String[] words = { 
								word.substring(0, 1), word.substring(1, 3),
								word.substring(0, 2), word.substring(2, 3),
								word
						};
						if ((isWord(words[0]) && isWord(words[1]))
						  ||(isWord(words[2]) && isWord(words[3]))
						  ||(isWord(words[4]))) {
						} else {
							this.decoder = new StringBuffer(oldDecoder);
							return true;
						}
					} else if (word.length() == 4) {
						/* word length possibilities:
						 *   a b c d
						 *   a bc d
						 *   a b cd
						 *   ab c d
						 *   ab cd
						 *   a bcd
						 *   abc d
						 *   abcd
						 */
						String[] words = { 
								word.substring(0, 1), word.substring(1, 2), word.substring(2, 3), word.substring(3, 4), 
								word.substring(0, 1), word.substring(1, 3), word.substring(3, 4),
								word.substring(0, 1), word.substring(1, 2), word.substring(2, 4),
								word.substring(0, 2), word.substring(2, 3), word.substring(3, 4),
								word.substring(0, 2), word.substring(2, 4),
								word.substring(0, 1), word.substring(1, 4),  
								word.substring(0, 3), word.substring(3, 4),
								word
						};
						if ((isWord(words[0]) && isWord(words[1]) && isWord(words[2]) && isWord(words[3]))
							  ||(isWord(words[4]) && isWord(words[5]) && isWord(words[6]))
							  ||(isWord(words[7]) && isWord(words[8]) && isWord(words[9]))
							  ||(isWord(words[10]) && isWord(words[11]) && isWord(words[12]))
							  ||(isWord(words[13]) && isWord(words[14]))
							  ||(isWord(words[15]) && isWord(words[16]))
							  ||(isWord(words[17]) && isWord(words[18]))
							  ||(isWord(word))) {
						} else {
							this.decoder = new StringBuffer(oldDecoder);
							return true;
						}
					}
				}
			}
		}
		
		// if we got this far, then we found a zero-conflict decoder that produces all of the words represented in the genome.
		this.decoder = new StringBuffer(mergedDecoder);
		return false;
	}
	
	/** returns true if the given word is found in one of our small word dictionaries.
	 * only supports words up to 4 chars in length. */
	public static boolean isWord(String word) {
		//System.out.println("isword " + word);
		if (word.length() == 1)
			return ZodiacDictionary.isWord(word, ZodiacDictionary.D_ONE_LETTER); 
		if (word.length() == 2)
			return ZodiacDictionary.isWord(word, ZodiacDictionary.D_TWO_LETTER); 
		if (word.length() == 3)
			return ZodiacDictionary.isWord(word, ZodiacDictionary.D_THREE_LETTER); 
		if (word.length() == 4)
			return ZodiacDictionary.isWord(word, ZodiacDictionary.D_FOUR_LETTER);
		else return false;
	}
	
	/** returns true if the given word, placed into the given position of the given genome (represented as a decoder), will produce a conflict.
	 * this one assumes that the decoder was alrady computed for the current genome, and we are just testing a candidate word/position pair. 
	 * try to make this run fast, since we will be calling it a lot.
	 * output: this CipherWordGene's decoder is updated with a zero-conflict merged decoder iff this genome has no conflict.
	 **/
	public boolean hasConflictOBSOLETE(int wordAllele, int positionAllele) {
		if (decoder == null || decoder.equals(resetDecoder)) {
			boolean conflict = hasConflictOBSOLETE(this.genome); // do the full analysis if we haven't generated a decoder yet.
			if (conflict) return true; 
		}
		
		
		String w = getWordFromAllele(wordAllele);
		if (w.length() == 0) return false;
		int pos = getPositionFromAllele(positionAllele);
		
		/* check for dupes */
		for (int i=0; i<genome.length; i+=2)
			if (getWordFromAllele(genome[i]).equals(w)) return true; // the incoming word matches an existing word - disallow dupes!

		/* check for overlap */
		for (int i=0; i<genome.length; i+=2) {
			String w2 = getWordFromAllele(genome[i]);
			int pos2 = getPositionFromAllele(genome[i+1]);
			
			int b1 = pos; int b2 = pos2;
			int e1 = pos+w.length()-1; int e2 = pos2+w2.length()-1;
			if ((b1 < b2 && e1 < b2) || (b1 > b2 && e1 > e2)) {
				;
			} else return true;
		}
		
		
		return Zodiac.getDecoderForWordAtPosition(decoder.toString(), w, pos, true) == null;
	}

	/** update the decoder by merging it with the decoder represented by the given word/position pair */
	public void updateDecoderOBSOLETE(int wordAllele, int positionAllele) {
		if (decoder == null) decoder = new StringBuffer(resetDecoder); // avoids NPE
		if (decoder.equals(resetDecoder)) {
			boolean conflict = hasConflictOBSOLETE(this.genome); // do the full analysis if we haven't generated a decoder yet.
		}
		String w = getWordFromAllele(wordAllele);
		if (w.length() == 0) return;
		int pos = getPositionFromAllele(positionAllele);
		decoder = new StringBuffer(Zodiac.getDecoderForWordAtPosition(decoder.toString(), w, pos, true));
	}
	
	
	/** select a random word from the set of zodiac words. selection should be proportionate 
	 * to word-occurrence frequency, to encourage realistic word-selection.  
	 */
	/*public int getRandomWord() {
		return 0;
	}*/
	
	/** select a random word of the given length from the set of zodiac words. selection should be proportionate 
	 * to word-occurrence frequency, to encourage realistic word-selection.  
	 */
	/*public int getRandomWord(int length) {
		return 0;
	}*/
	
	/** initialize the constraint cache */
	
	/*
	public void initConstraintCache() {
		constraintCacheOffspring = new THashMap<String, THashSet<String>>();
		if (constraintCacheParent1 == null) constraintCacheParent1 = new THashMap<String, THashSet<String>>();
		if (constraintCacheParent2 == null) constraintCacheParent2 = new THashMap<String, THashSet<String>>();
	}*/
	
	/** compute fitness.  also, generate the encoding (list of words represented by this gene) and the decoded ciphertext (if there is no conflict). */
	public void fitness() {
		
		//initConstraintCache();
		
		decodedSubstringSpaced = getDecodedSubstringSpaced();
		
		elite=false;
		initAllPools();
		if (wordPoolHash == null) initWordPoolHash();

		
		MultiObjectiveFitness fit = (MultiObjectiveFitness)this.fitness;
		if (fit == null) {
			fit = new MultiObjectiveFitness();
		}
		//if (fit.multifitness == null) {
		//	fit.multifitness = new float[NUM_OBJECTIVES];
		//}
		if (1==1) throw new RuntimeException("FIX THIS");

		
			zodiacScore();
			
			//fit.multifitness[0] = fitnessWordFit(4);
			//fit.multifitness[0] = fitnessUniqueness(3) + fitnessGaps();
			
			/*
			String combined = "";
			if (CIPHER_START > 0) {
				combined = decoded.substring(0, CIPHER_START);
			}
			combined += decoded.substring(CIPHER_START, CIPHER_END+1);
			if (CIPHER_END <= decoded.length() - 1) {
				combined += decoded.substring(CIPHER_END+1);
			}
			
			if (!combined.replaceAll(" ", "").equals(decoded.toString())) {
				throw new RuntimeException("they should be equal: " + combined + " and " + decoded);
			}*/
			
			//fit.multifitness[0] = Math.round((float)fitnessWordFit(4) * countNonWildcardsPlaintext() / (CIPHER_END-CIPHER_START+1));
			
			//fit.multifitness[2] = fitnessUniqueness(4);
			//fit.multifitness[2] = fitnessUniquenessDecoder();
			//fit.multifitness[3] = fitnessDecodedSymbolCounts();
			/*fit.multifitness[1] = 0;
			this.foundWordsDictionary = "";
			for (int i=4; i<8; i++)
				fit.multifitness[1] += i*fitnessConstraintWordCount(i);*/
			//fit.multifitness[1] = fitnessConstraintCoverageCount(4, 10, false, true);
			fitnessConstraintCoverageCount(fit, 4, 10, false, true, 1);
			//fit.multifitness[1] *= ((float)foundWordCount*foundWordConflicts/100);
			//fit.multifitness[0] = foundWordCount;
			
			//fit.multifitness[4] = foundWordEdgeNodes;
			//fit.multifitness[5] = foundWordNodeCover;
			//fit.multifitness[6] = foundWordConflicts;
			//fit.multifitness[1] *= fitnessUniquenessDecoder();
			//System.out.println(fitnessUniquenessDecoder());
			//fit.multifitness[3] = fitnessTrigramScore();
			
			//int factor = (CIPHER_END - CIPHER_START + 1) - countNonWildcardsConflictOverlap();
			//int factor = (CIPHER_END - CIPHER_START + 1) - countNonWildcardsConflictOverlapFeasible();
			/*
			int factor = 2 * (CIPHER_END - CIPHER_START + 1) - countNonWildcardsConflictOverlapFeasible() - countNonWildcardsPlaintext();
			
			factor = factor + 1;
			
			fit.multifitness[0] = Math.round(100.0f * fit.multifitness[0] / factor);
			 */
			//fit.multifitness[0] = Math.round(fit.multifitness[0]* ((float) countNonWildcardsConflictOverlap() / (CIPHER_END - CIPHER_START + 1))); 
			
			//fit.multifitness[1] = 10000+Math.round((float)ZodiacDictionary.computeDecryptoTrigramScore(combined));
			//fit.multifitness[2] = 10000+Math.round((float)ZodiacDictionary.computeDecryptoTrigramScore(fullDecoded.toString()));
			
			if (TRUNCATE_DEAD_ENDS) {
				if (deadEndHash == null)
					deadEndHash = new THashSet<String>();
				
				if (deadEndHash.contains(this.sortedKey)) {
					this.genome = new int[genome.length]; // this will probably take it out of the population completely
					this.conflictInit();
					//for (int i=0; i<fit.multifitness.length; i++)
					//	fit.multifitness[i] = 0;
					//statsInc("deadEndKilled");
				}
				/*if (fit.multifitness[0] >= (deadEndThreshold * (float)decoded.substring(CIPHER_START, CIPHER_END+1).length())) {
					deadEndHash.add(this.sortedKey);
					
					System.out.println("NEW DEAD END ADDED: " + getDebugLine());
					
					this.genome = new int[genome.length]; // this will probably take it out of the population completely
					this.conflictInit();
					for (int i=0; i<fit.multifitness.length; i++)
						fit.multifitness[i] = 0;
					//statsInc("deadEndAdded");
				}*/
			}
			
			//fit.multifitness[1] = fitnessWordFit(3);
			
			
			//fit.multifitness[0] = fitnessUniquenessCount(5);
			//fit.multifitness[0] = fitnessSubstring(true, decode(), substringPool, wordPool);
			//fit.multifitness[0] = fitnessSubstring(true, decode(), substringPool, wordPool);
			
			//fit.multifitness[1] = fitnessSubstring(true, decode(), substringPool, wordPool);
			
			//fit.multifitness[0] = fitnessUniquenessOther(4);
			//fit.multifitness[0] = fitnessSubstring(1, true, decoded.toString(), substringPool5, substringPrefixesPool5, wordPool, decoder.toString(), false, 5);
			//fit.multifitness[1] = fitnessSubstring(1, true, decoded.toString(), substringPool4, substringPrefixesPool4, wordPool, decoder.toString(), false, 4);
			//int bad = ZodiacDictionary.countBadNGrams(decoded.toString());
			//fit.multifitness[1] = bad > 200 ? 0 : 200-bad;

			/*for (int i=1; i<=5; i++) this.fitnessNGramFreq(i);
				double fitD = 0.0059 * fitnessUniqueness(4) +
			  0.0003 * fitnessUniquenessOther(4) -
			  0.0011 * this.fitnessSubstring(4, true, decoded.toString(), substringPool, substringPrefixesPool, wordPool, decoder.toString(), false) + 
			  0.0132 * this.fitnessSubstring(4, true, decoded.toString(), substringPool, substringPrefixesPool, wordPool, decoder.toString(), true) +
			  0.6516 * this.fitnessNGrams[1] 
		     -3.0492 * this.fitnessNGrams[2] +	
		     17.0596 * this.fitnessNGrams[3] +	
		    -26.1943 * this.fitnessNGrams[4] +	
		     10.9192 * this.fitnessNGrams[5] + 
		     0.8035;
				if (fitD < 0) fit.multifitness[1] = 0;
				else fit.multifitness[1] = (float) fitD;
				*/

			
			//fit.multifitness[0] = (float)zodiacMatch;
			//fit.multifitness[1] = (float)zodiacMatch;
			//StringBuffer d = new StringBuffer(decode());
			//d.delete(CIPHER_START, CIPHER_END+1);
			//fit.multifitness[2] = fitnessSubstringGreedy(1, true, decode(), substringPool, wordPool, decoder);
			//fit.multifitness[0] = fitnessSubstring(1, true, decoded.toString(), substringPool, substringPrefixesPool, wordPool, decoder.toString(), false);
			
			//fit.multifitness[0] = fitnessUniqueness(4) * fitnessUniquenessOther(4,10) * fitnessSubstring(1, true, decode(), substringPool, wordPool);
			//fit.multifitness[0] = fitnessSubstringGreedy(1, true, decode(), substringPool, wordPool, decoder);
			/*int f1 = fitnessUniqueness(4);
			int f2 = fitnessUniquenessOther(4,10);
			int f3 = fitnessSubstring(1, true, decode(), substringPool, wordPool);
			float mean = (f1+f2+f3)/3;
			fit.multifitness[0] = mean;
			fit.multifitness[1] = 100-(Math.abs(f1-mean)+Math.abs(f2-mean)+Math.abs(f3-mean));*/
			
			//fit.multifitness[1] = fitnessUniquenessOther(4,10);
			//fit.multifitness[1] = fitnessSubstring(true, decode(), substringZodDictPool, top40Pool);
			//fit.multifitness[0] = (float)this.fitnessNGramFreq(1);
			//fit.multifitness[0] = (float)zodiacMatch;
		//}
		encoding = "";
		
		String word;
		
		/*for (int i=0; i<genome.length; i+=2) {
			word = getWordFromAllele(genome[i]);
			if (word.length() > 0) {
				encoding += word + " (" + getPositionFromAllele(genome[i+1]) + ") ";
			}
		}*/
		
		
		if (conflictWordHash != null) {
			Iterator<String> it = conflictWordHash.keySet().iterator();
			while (it.hasNext()) {
				word = it.next();
				encoding += word + " (" + conflictWordHash.get(word) + ") ";
			}
		}
		
		this.fitness = fit;
		preserveUnshared();
		
		sortedKey = makeSortedKeyFor(genome, MIN_WORD_LENGTH);
		
		
		
		
		evaluated = true;
	}
	
	
	public static void initSymbolCounts() {
		if (symbolCounts == null) {
			symbolCounts = new THashMap<Character, Integer>();
			Integer val; Character key;
			for (int i=0; i<Zodiac.cipher[Zodiac.CIPHER].length(); i++) {
				key = Zodiac.cipher[Zodiac.CIPHER].charAt(i);
				val = symbolCounts.get(key);
				if (val == null) val = 1;
				else val++;
				symbolCounts.put(key,val);
			}
			/*for (Character c : symbolCounts.keySet()) 
				System.out.println(c + ":" + symbolCounts.get(c));*/
		}
	}
	
	/** count the total number of symbols that have been decoded */
	public int fitnessDecodedSymbolCounts() {
		initSymbolCounts();
		int result = 0;
		THashSet<Character> symbols = new THashSet<Character>();
		char c;
		for (int i=0; i<decoder.length(); i++) {
			if (decoder.charAt(i) != '?') {
				c = Zodiac.alphabet[Zodiac.CIPHER].charAt(i);
				if (!symbols.contains(c)) {
					symbols.add(c);
					result += symbolCounts.get(c);
				}
				
			}
		}
		return result;
	}
	
	/** count the number of unique letters in the decoder */
	public int fitnessUniquenessDecoder() {
		THashSet<Character> result = new THashSet<Character>();
		for (int i=0; i<decoder.length(); i++)
			if (decoder.charAt(i) != '?') result.add(decoder.charAt(i));
		return result.size();
	}

	/** count the number of non-conflicted, non-wildcard plaintext letters in the gaps between word placements */
	public int fitnessGaps() {
		int result = 0;
		//hib?c?alaugh???station??????u?uau??albecauseu?uatoh
		//???????_____???_______???????????????_______???????
		int count = 0; int score = 0;
		char letterConflict, letterDecoded;
		String decodedSubstring = decoded.substring(CIPHER_START,CIPHER_END+1);
		String overlapSubstring = conflictOverlap.substring(CIPHER_START,CIPHER_END+1);
		//System.out.println(decodedSubstring.length() +","+overlapSubstring.length());
		for (int i=0; i<overlapSubstring.length(); i++) {
			letterConflict = overlapSubstring.charAt(i);
			letterDecoded = decodedSubstring.charAt(i);
			if (letterConflict == '?' && count <= DAC_MAX_LEN) {
				if (letterDecoded != '?') score++;
				count++;
			} else if (count > DAC_MAX_LEN) { // no score for this guy
				score = 0;
			} else { // we went from ? to _, or started with _, so add our accumulated score.
				result += score;
				count = 0;
				score = 0;
			}
		}
		if (count <= DAC_MAX_LEN) result += score;
		return result;
	}
	
	/** counts the number of positions that might form word prefixes (using constraint db) for words >= length 5, given that
	 * at least 3 of the 5 characters of the prefix are not wildcards.
	 */
	public int fitnessIsWordOther() {
		int LEN = 5;
		
		int result = 0;
		
		int nonWildCount = 0;
		String prefix, cipher;

		this.zodiacWordsOther = "";
		//for (int i=0; i<Zodiac.cipher[Zodiac.CIPHER].length()-LEN-1; i++) {
		for (int i=143; i<257; i++) {
			prefix = decoded.substring(i, i+LEN);
			cipher = Zodiac.cipher[Zodiac.CIPHER].substring(i, i+LEN);
			nonWildCount = 0;
			for (int j=0; j<prefix.length(); j++)
				if (prefix.charAt(j) != '?')
					nonWildCount++;
			if (nonWildCount>2) {
				/*THashSet<String> words = ZodiacDictionary.getWordsConstrainedBy(ZodiacDictionary.wordConstraintDatabase, cipher, prefix);
				if (words.size() > 0) {
					result++;
					this.zodiacWordsOther += " " + i + "," + prefix + "(";
					for (String w : words)
						this.zodiacWordsOther += w + ",";
					this.zodiacWordsOther += ")";
				}*/
				if (ZodiacDictionary.isWord(cipher, prefix, false, false) || ZodiacDictionary.isWord(cipher, prefix, true, false) || ZodiacDictionary.isWord(cipher, prefix, false, true)) {
					result++;
				}
					
			}
			
		}
		return result;
	}
	
	/** use the constraints db to count the number of non-wildcard characters that fit with possible word placements.
	 * only counts words that have more than half of its letters specified by non-wildcard chars in the plaintext.
	 * the number of plaintext positions covered by found words is returned.
	 * uses prefix constraint db to look for all word beginnings.
	 * 
	 * this function is kind of slow.
	 * 
	 * @param minLettersBeforeInclude must find this many non-wildcard chars before they are counted towards the score. 
	 * */
	public int fitnessWordFit(int minLength) {
		
		boolean TRACK_WORDS = true;
		boolean ENFORCE_UNIQUENESS = false && TRACK_WORDS; // if true, only count unique words
		boolean PUNISH_CONFLICTS = true && TRACK_WORDS; // if true, count conflicts in symbol/plaintext mappings derived from found words and subtract from the found word coverage score. 
		
		boolean USE_PREFIX_DB = true; // if true, we test using the prefix db.  otherwise, use the exact matches db.
		
		THashSet<Integer> positions = new THashSet<Integer>();
		
		THashSet<String> found;
		THashSet<String> allWords = null;
		if (ENFORCE_UNIQUENESS)
			allWords = new THashSet<String>();
		boolean doCount;
		int i=0; 
		int count = 0;
		//int total=0;
		StringBuffer chunkPlain;// = new StringBuffer();
		StringBuffer chunkCipher;// = new StringBuffer();
		StringBuffer cover = new StringBuffer(decoded);
		//StringBuffer replace = new StringBuffer();
		StringBuffer foundWords = new StringBuffer();
		String cipher = Zodiac.cipher[Zodiac.CIPHER];
		StringBuffer[] replacements = new StringBuffer[ZodiacDictionary.wordConstraintDatabaseMaxLength];
		THashSet<String> words = null;
		THashMap<Character, THashSet<Character>> symbolMap = new THashMap<Character, THashSet<Character>>(); // track candidates for each cipher symbol.  this way we can measure conflicts.
		//char charAdd, charDel;
		int len, majority;
		int wordConflicts = 0;
		int wordGroupConflicts = 0;
		int allConflicts = 0;
		char chSymbol, chPlain, chD;
		THashSet<Character> plainHash;
		//boolean foundConflict;
		//int maxLenToUse;
		while (i<=cipher.length()) {
			if (TRACK_WORDS)
				words = new THashSet<String>();
			for (len = minLength; len <= ZodiacDictionary.wordConstraintDatabaseMaxLength && i+len <= cipher.length(); len++) {
				if (replacements[len-1] == null) {
					replacements[len-1] = new StringBuffer();
					for (int j=0; j<len; j++)
						replacements[len-1].append("_");
				}
				
				if (!conflictOverlap.substring(i, i+len).contains("_")) { // ignore if we have placed a word nearby.
					majority = len/2 + 1;
					chunkPlain = new StringBuffer(decoded.substring(i, i+len));
					chunkCipher = new StringBuffer(cipher.substring(i, i+len));
					//charAdd = decoded.charAt(i);
					//if (charAdd != '?') count++; // new char is non-wildcard
					//chunkPlain.append(charAdd);
					//chunkCipher.append(cipher.charAt(i));
					//if (chunkPlain.length() > minLength) {
					//	assert chunkPlain.length() == minLength + 1; 
					//	charDel = chunkPlain.charAt(0);
					//	if (charDel != '?') count--;
					//	chunkPlain.deleteCharAt(0);
					//	chunkCipher.deleteCharAt(0);
					count=0; // count the # of non-wildcard chars
					for (int j=0; j<chunkPlain.length(); j++)
						if (chunkPlain.charAt(j) != '?') count++;

					doCount = false;
					if (count >= majority) {
							found = ZodiacDictionary.getWordsConstrainedBy(USE_PREFIX_DB ? ZodiacDictionary.wordConstraintPrefixDatabase : ZodiacDictionary.wordConstraintDatabase, chunkCipher.toString(), chunkPlain.toString());
							if (found != null && found.size() > 0 ) {
								//positions.add(i);
								
								if (TRACK_WORDS) {
									if (ENFORCE_UNIQUENESS) {
										for (String w : found) 
											if (!allWords.contains(w)) {
												doCount = true;
												words.add(w);
												allWords.add(w);
											}
									} else {
										doCount = true;
										words.addAll(found);
									}
								} else doCount = true;
								if (doCount)
									cover.replace(i, i+len, replacements[len-1].toString());
							} else {
								break; // stop, because no further prefixes can possibly be found here.
						}
					} 
				}
			}
			if (TRACK_WORDS) {
				if (words != null && words.size() > 0) {
					
					/* compute the symbol candidates based on found words. count the conflicts. */
					//maxLenToUse = -1;
					if (PUNISH_CONFLICTS) {
						wordGroupConflicts = Integer.MAX_VALUE;
						for (String w : words) {
							//foundConflict = false;
							wordConflicts = 0;
							for (int j=0; j<w.length(); j++) {
								chPlain = w.charAt(j);
								chSymbol = cipher.charAt(i+j);
								plainHash = symbolMap.get(chSymbol);
								chD = decoded.charAt(i+j);
								if (chD != '?' && chD != chPlain) // there is already a fixed decoding for this symbol that the candidate decoding contradicts.
									wordConflicts++;
								else { // check for symbol mapping conflicts with other words found so far.
									if (plainHash == null) { // no other word contributed to the symbol yet
									} else { 
										// some other word contributed to this symbol.  does the current plaintext conflict with another word's mapping of this symbol?
										if (plainHash.size() > 0 && !plainHash.contains(chPlain)) {
											String p = "";
											for (Character c : plainHash)
												p += c + ",";
											//System.out.println(i + " word " + w + " char " + chPlain + " conflicts with symbol's " + chSymbol + " mappings " + p);
											wordConflicts++;
											//foundConflict = true;
										}
									}
								}
							}
							if (wordConflicts < wordGroupConflicts) /* for all these words, count conflicts only within the word that contributes the least number of conflicts. */
								wordGroupConflicts = wordConflicts;
							/*if (!foundConflict) {
								if (w.length() > maxLenToUse) 
									maxLenToUse = w.length(); // only contribute
							}*/
						}
//						System.out.println("wordGroupConflicts " + wordGroupConflicts);
						allConflicts += wordGroupConflicts;
						// add all the plaintext candidates now that we're done counting conflicts.
						for (String w : words) {
							//System.out.println(i + " added mappings for " + w + " " + cipher.substring(i, i+w.length()));
							for (int j=0; j<w.length(); j++) {
								chPlain = w.charAt(j);
								chSymbol = cipher.charAt(i+j);
								plainHash = symbolMap.get(chSymbol);
								if (plainHash == null) { // no other word contributed to the symbol yet
									plainHash = new THashSet<Character>();
									symbolMap.put(chSymbol, plainHash);
									// some other word contributed to this symbol.  does the current plaintext conflict with another word's mapping of this symbol?
								}
								plainHash.add(chPlain);
							}
						}
					}
					
					/* make the info for getDebugLine */
					foundWords.append(i).append(" (");
					for (String w : words)
						foundWords.append(w).append(", ");
					foundWords.delete(foundWords.length()-2, foundWords.length());
					foundWords.append(") ");
				}
			}
			i++;
		}
		//System.out.println("total iterations " + total);
		//return positions.size();
		this.foundWordsDictionary = foundWords;
		count = 0;
		for (i=0; i<cover.length(); i++)
			count += cover.charAt(i) == '_' ? 1 : 0;
		
		if (PUNISH_CONFLICTS) {
			count -= allConflicts;
			System.out.println("conflicts: " + allConflicts);
		}
		return (count > 0 ? count : 0);
	}
	

	/** count the word coverage of words of the given length found using the word constraint database, provided
	 * that the majority (greater than half) of letters in the plaintext under consideration are non-wildcard characters. 
	 */
	public int fitnessConstraintWordCount(int length) {
		ArrayList<Integer> positions = new ArrayList<Integer>();
		
		//int hits = 0;
		//int misses = 0;
		
		boolean TRACK_WORDS = false; // if true, all found words are reported (this slows down the metric significantly)
		boolean AVOID_PLACED_WORDS = true; // if true, we look for already-placed words nearby and prevent the word lookup. (might not care, if looking at different half of the cipher than where the word placements are being explored)
		boolean HALF_CIPHER = false; // if true, only run on half the cipher as a speed boost
		boolean ENFORCE_UNIQUE = false; // if true, only count unique words
		boolean CONFLICT = false; // if true, count conflicts
		
		THashSet<String> found;
		THashSet<String> uniques = null;
		
		THashSet<String> mappings = null;
		if (CONFLICT) mappings = new THashSet<String>();
		
		int i=0; 
		int count = 0;
		int countOverlap = 0;
		//int total=0;
		//StringBuffer chunkPlain = new StringBuffer();
		String chunkPlain;
		String chunkCipher;
		//StringBuffer chunkCipher = new StringBuffer();
		//StringBuffer cover = new StringBuffer(decoded);
		//StringBuffer replace = new StringBuffer();
		StringBuffer foundWords = new StringBuffer();
		String cipher = Zodiac.cipher[Zodiac.CIPHER];
		//StringBuffer replacement = new StringBuffer("_");
		//for (int k=0; k<length; k++) replacement.append("_");
		THashSet<String> words = null;
		if (ENFORCE_UNIQUE)
			uniques = new THashSet<String>();
		
		char charAdd, charDel;
		int majority = length/2 + 1;
		//while (i<=cipher.length()-length) {
		//while (i<=cipher.length()-length) {
		while (i<= (HALF_CIPHER ? cipher.length()/2 : cipher.length()-length)) {
			
			if (TRACK_WORDS)
				words = new THashSet<String>();
			
			if (i==0) {  // we just started, so need to init count
				for (int j=0; j<length; j++) {
					if (decoded.charAt(j) != '?') count++;
					if (AVOID_PLACED_WORDS && conflictOverlap.charAt(j) == '_') countOverlap++;
				}
				
			} else {
				// check next char
				charAdd = decoded.charAt(i+length-1);
				if (charAdd != '?') {
					count++;
				}
				if (AVOID_PLACED_WORDS && conflictOverlap.charAt(i+length-1) == '_') countOverlap++;
			
				charDel = decoded.charAt(i-1);
				if (charDel != '?') {
					count--;
				}
				if (AVOID_PLACED_WORDS && conflictOverlap.charAt(i-1) == '_') countOverlap--;
			}
			
			chunkPlain = decoded.substring(i, i+length);
			chunkCipher = cipher.substring(i, i+length);
			//System.out.println("chunk " + chunkPlain + " count " + count);
			
			//chunkPlain.append(charAdd);
			//chunkCipher.append(cipher.charAt(i));
			/*if (i > length-1) {
				charDel = chunkPlain.charAt(0);
				if (charDel != '?') count--;
				//chunkPlain.deleteCharAt(0);
				//chunkCipher.deleteCharAt(0);
			}*/
			
			//if (chunkPlain.length() == length && count >= majority) {
			if (count >= majority) {
				//hits++;
				//if (!conflictOverlap.substring(i, i+length).contains("_")) { // ignore if we have placed a word nearby.
				if (countOverlap == 0 || !AVOID_PLACED_WORDS) {// ignore if we have placed a word nearby.
					found = ZodiacDictionary.getWordsConstrainedBy(ZodiacDictionary.wordConstraintDatabase, chunkCipher, chunkPlain);
					if (found != null && found.size() > 0 ) {
						if (ENFORCE_UNIQUE) {
							for (String w : found)
								if (!uniques.contains(w)) {
									//System.out.println("word " + w + " pos " + i);
									positions.add(i);
									
									if (CONFLICT) {
										for (int x=0; x<w.length(); x++) {
											mappings.add(cipher.charAt(i+x) + "," + w.charAt(x));
										}
									}
									break;
								}
							uniques.addAll(found);
						} else
							positions.add(i);
						if (TRACK_WORDS)
							words.addAll(found);
						//cover.replace(i, i+length, replacement.toString());
					}
				}
			} //else misses++;
			if (TRACK_WORDS) {
				if (words != null && words.size() > 0) { // this is not quite right for ENFORCE_UNIQUE (it prints some dupes that aren't counted towards fitness)
					foundWords.append(i).append(" (");
					for (String w : words)
						foundWords.append(w).append(", ");
					foundWords.delete(foundWords.length()-2, foundWords.length());
					foundWords.append(") ");
				}
			}
			i++;
		}
		if (TRACK_WORDS) this.foundWordsDictionary.append("len" + length + ":").append(foundWords).append(" ");
		count = 0;
		int prev = -100;
		for (Integer p : positions) {
			if (p - prev >= length)
				count += length;
			else
				count += p - prev;
		}
		/*for (i=0; i<cover.length(); i++)
			count += cover.charAt(i) == '_' ? 1 : 0;*/
		//System.out.println("hits " + hits + " misses " + misses);
		if (CONFLICT) {
			System.out.println("mapping size: " + mappings.size());
		}
		return count;
	}

	/** count the word coverage of words of the given length range (inclusive) found using the word constraint database, provided
	 * that the majority (greater than half) of letters in the plaintext under consideration are non-wildcard characters.
	 * this is basically an ugly c&p job of fitnessConstraintWordCount, with an outer loop that goes through the length range,
	 * and a count only of covered positions (to avoid over-counting overlapping words).
	 * if rewardLength is true, score is weighted by word lengths that contribute to each position.
	 * if enforceUnique is true, only unique words are counted towards the score.
	 */
	public void fitnessConstraintCoverageCount(MultiObjectiveFitness fit, int minLength, int maxLength, boolean enforceUnique, boolean rewardLength, int fitOffset) {
		ArrayList<Integer> positions = new ArrayList<Integer>();
		
		//int hits = 0;
		//int misses = 0;
		
		boolean TRACK_WORDS = false; // if true, all found words are reported (this slows down the metric significantly)
		boolean AVOID_PLACED_WORDS = true; // if true, we look for already-placed words nearby and prevent the word lookup. (might not care, if looking at different half of the cipher than where the word placements are being explored)
		boolean HALF_CIPHER = false; // if true, only run on half the cipher as a speed boost
		boolean ENFORCE_UNIQUE = enforceUnique; // if true, only count unique words
		boolean CONFLICT = true; // if true, count conflicts
		
		THashSet<String> found;
		THashSet<String> uniques = null;
		
		THashSet<String> mappings = null;
		THashMap<Character, THashMap<Character, THashSet<String>>> conflictMappings = null; /* maps cipher char to set of plaintext chars, 
			each mapped to list of (word,pos) pairs that derived the decoding.  cipher char entries with more than one plaintext
			char mapping indicate conflicts among the found words. */
		//conflictWordsL = null; /* maps (word,pos) pairs to other (word,pos) pairs with which they are conflicted.  This is the "left vertex (edge endpoint)" index. */
		//conflictWordsR = null; /* maps (word,pos) pairs to other (word,pos) pairs with which they are conflicted.  This is the "right vertex (edge endpoint)" index. */
		edgeMap = null;
		edges = null;
		THashSet<String> conflictNodes = null; /* distinct set of nodes in the conflict graph */
		
		if (CONFLICT) {
			mappings = new THashSet<String>();
			conflictMappings = new THashMap<Character, THashMap<Character, THashSet<String>>>();
			//conflictWordsL = new THashMap<String, THashSet<String>>();
			//conflictWordsR = new THashMap<String, THashSet<String>>();
			edgeMap = new THashMap<String, THashSet<Edge>>();
			edges = new THashSet<Edge>();
			conflictNodes = new THashSet<String>();
		}
		
		//int total=0;
		//StringBuffer chunkPlain = new StringBuffer();
		String chunkPlain;
		String chunkCipher;
		//StringBuffer chunkCipher = new StringBuffer();
		int[] cover = new int[decoded.length()];
		
		/*StringBuffer[] replacements = new StringBuffer[maxLength];
		for (int i=minLength; i<=maxLength; i++) {
			replacements[i-1] = new StringBuffer();
			for (int j=0; j<i; j++)
				replacements[i].append("_");
		}*/
		
		
		StringBuffer foundWords = new StringBuffer();
		String cipher = Zodiac.cipher[Zodiac.CIPHER];
		//StringBuffer replacement = new StringBuffer("_");
		//for (int k=0; k<length; k++) replacement.append("_");
		THashSet<String> words = null;
		if (ENFORCE_UNIQUE)
			uniques = new THashSet<String>();

		String cacheKey;
		//int countCons = 0, countNonCons = 0;
		char charAdd, charDel, charCover, charCipherAdd, charCipherDel;
		
		//boolean dupeCipherChar;
		THashMap<Character, Integer> cipherCharsDupes; // used to track dupe cipher chars.  if we find no dupes, then we can pass null instead of cipherChunk to the constraint DB search for speedup.
		int dupeCount;
		
		for (int length = minLength; length <= maxLength; length++) {
			
			//cipherChars = new THashSet<Character>();
			cipherCharsDupes = new THashMap<Character, Integer>();
			int i=0; 
			int count = 0;
			int countOverlap = 0;
			int majority = length/2 + 1;
			//while (i<=cipher.length()-length) {
			//while (i<=cipher.length()-length) {
			while (i<= (HALF_CIPHER ? cipher.length()/2 : cipher.length()-length)) {
				
				//System.out.println("i " + i);
				if (TRACK_WORDS)
					words = new THashSet<String>();
				
				if (i==0) {  // we just started, so need to init count
					for (int j=0; j<length; j++) {
						if (decoded.charAt(j) != '?') count++;
						if (AVOID_PLACED_WORDS && conflictOverlap.charAt(j) == '_') countOverlap++;
						charCipherAdd = cipher.charAt(j);
						//System.out.println("put " + charCipherAdd);
						if (cipherCharsDupes.contains(charCipherAdd)) {
							dupeCount = cipherCharsDupes.get(charCipherAdd);
							dupeCount++;
							cipherCharsDupes.put(charCipherAdd, dupeCount);
						} else {
							cipherCharsDupes.put(charCipherAdd, 1);
						}
					}
					
				} else {
					// check next char
					charAdd = decoded.charAt(i+length-1);
					charCipherAdd = cipher.charAt(i+length-1);
					if (charAdd != '?') {
						count++;
					}
					if (AVOID_PLACED_WORDS && conflictOverlap.charAt(i+length-1) == '_') countOverlap++;
					if (!cipherCharsDupes.contains(charCipherAdd)) { 
						//dupeCipherChar = true;
						cipherCharsDupes.put(charCipherAdd,1);
					} else {
						dupeCount = cipherCharsDupes.get(charCipherAdd);
						dupeCount++;
						cipherCharsDupes.put(charCipherAdd, dupeCount);
					}
					//System.out.println("put " + charCipherAdd + " " + cipherCharsDupes.get(charCipherAdd));
				
					charDel = decoded.charAt(i-1);
					charCipherDel = cipher.charAt(i-1);
					if (charDel != '?') {
						count--;
					}
					//System.out.println("del " + charCipherDel);
					dupeCount = cipherCharsDupes.get(charCipherDel);
					if (dupeCount == 1) {
						cipherCharsDupes.remove(charCipherDel);
						//System.out.println(" - removed " + charCipherDel);
						// last one, so remove it
					} else {
						dupeCount--;
						cipherCharsDupes.put(charCipherDel, dupeCount);
					}
					//System.out.println("del left " + cipherCharsDupes.get(charCipherDel));
					
					if (AVOID_PLACED_WORDS && conflictOverlap.charAt(i-1) == '_') countOverlap--;
				}
				
				/*
				System.out.println("...");
				for (Character c : cipherCharsDupes.keySet()) {
					System.out.println(c + ": " + cipherCharsDupes.get(c));
				}*/
				
				//System.out.println(length + ":" + cipherCharsDupes.keySet().size());
				
				chunkPlain = decoded.substring(i, i+length);
				chunkCipher = cipher.substring(i, i+length);
				//System.out.println("chunk " + chunkPlain + " count " + count);
				
				//chunkPlain.append(charAdd);
				//chunkCipher.append(cipher.charAt(i));
				/*if (i > length-1) {
					charDel = chunkPlain.charAt(0);
					if (charDel != '?') count--;
					//chunkPlain.deleteCharAt(0);
					//chunkCipher.deleteCharAt(0);
				}*/
				
				//if (chunkPlain.length() == length && count >= majority) {
				if (count >= majority) {
					//hits++;
					//if (!conflictOverlap.substring(i, i+length).contains("_")) { // ignore if we have placed a word nearby.
					if (countOverlap == 0 || !AVOID_PLACED_WORDS) {// ignore if we have placed a word nearby.
						//if (cipherCharsDupes.keySet().size() == length) countNonCons++;
						//else countCons++;
						//cacheKey = (cipherCharsDupes.keySet().size() == length ? "" : chunkCipher) + " " + chunkPlain;
						//found = this.constraintCacheParent1.get(cacheKey);
						/*if (found == null) {
							found = this.constraintCacheParent2.get(cacheKey);
						} //else { System.out.println("CACHE HIT PARENT 1"); }
						if (found == null) {
							found = this.constraintCacheOffspring.get(cacheKey);
						} //else { System.out.println("CACHE HIT PARENT 2"); }
						if (found == null) {
							//System.out.println("CACHE MISS");
							found = ZodiacDictionary.getWordsConstrainedBy(ZodiacDictionary.wordConstraintDatabase, cipherCharsDupes.keySet().size() == length ? null : chunkCipher, chunkPlain); // don't bother passing chunkCipher unless it truly adds a constraint.
							constraintCacheOffspring.put(cacheKey, found);
						} //else { System.out.println("CACHE HIT OFFSPRING"); }*/
						found = ZodiacDictionary.getWordsConstrainedBy(ZodiacDictionary.wordConstraintDatabase, cipherCharsDupes.keySet().size() == length ? null : chunkCipher, chunkPlain); // don't bother passing chunkCipher unless it truly adds a constraint.
						//found = null;
						if (found != null && found.size() > 0 ) {
							if (ENFORCE_UNIQUE) {
								for (String w : found)
									if (!uniques.contains(w)) {
										//System.out.println("word " + w + " pos " + i);
										positions.add(i);
										
										/*
										if (CONFLICT) {
											for (int x=0; x<w.length(); x++) {
												mappings.add(cipher.charAt(i+x) + "," + w.charAt(x));
											}
										}*/
										for (int j=i; j<i+length; j++) {
											if (rewardLength) {
												//charCover = (char) (31 + length); 
												cover[j] = length;
											} else {
												cover[j] = 1;
											}
										}
										//cover.replace(i, i+length, replacements[length-1].toString());
										break;
									}
								uniques.addAll(found);
							} else {
								positions.add(i);
								
								/*for (String w : found)
									System.out.println(i + ":" + w);
								System.out.println(cipher.substring(i, i+length));
								System.out.println(decoded.substring(i, i+length));*/
								
								/*if (CONFLICT) {
									for (String w : found) {
										for (int x=0; x<w.length(); x++) {
											mappings.add(cipher.charAt(i+x) + "," + w.charAt(x));
										}
									}
								}*/
								
								
								for (int j=i; j<i+length; j++) {
									if (rewardLength) {
										//charCover = (char) ('0' + (char)length); 
										cover[j] = length;
									} else {
										cover[j] = 1;
									}
								}
							}
							if (TRACK_WORDS)
								words.addAll(found);
							
							if (CONFLICT) { // track ciphertext/plaintext mappings to determine conflicts among words.
								String tuple;
								char c, p;
								THashMap<Character, THashSet<String>> map;
								THashSet<String> set;
								THashSet<Edge> edgeSet;
								for (String w: found) {
									tuple = w + "," + i;
									conflictNodes.add(tuple);
									for (int x=0; x<w.length(); x++) {
										c = cipher.charAt(i+x);
										p = w.charAt(x);
										
										map = conflictMappings.get(c);
										if (map == null) { // THashMap<Character, THashMap<Character, THashSet<String>>>
											map = new THashMap<Character, THashSet<String>>();
											conflictMappings.put(c, map);
										}
										set = map.get(p);
										if (set == null) {
											set = new THashSet<String>();
											map.put(p, set);
										}
										set.add(tuple);
										
										/* now inspect the mappings to determine conflicts */
										for (Character plain : map.keySet()) {
											//System.out.println(c + " plain " + plain + " p " + p);
											if (plain.charValue() != p) {
												for (String s : map.get(plain)) {
													if (i != Integer.valueOf(s.split(",")[1]).intValue()) {
														//System.out.println(" - " + tuple + " conflict " + s);
														/* edge index */
														edgeSet = edgeMap.get(tuple);
														if (edgeSet == null) {
															edgeSet = new THashSet<Edge>();
															edgeMap.put(tuple, edgeSet);
														}
														edgeSet.add(new Edge(tuple, s));
														edges.add(new Edge(tuple, s));
														
														edgeSet = edgeMap.get(s);
														if (edgeSet == null) {
															edgeSet = new THashSet<Edge>();
															edgeMap.put(s, edgeSet);
														}
														edgeSet.add(new Edge(tuple, s));
														//edges.add(new Edge(tuple, s));

														
														/* right endpoint index */
														/*set = conflictWordsR.get(s);
														if (set == null) {
															set = new THashSet<String>();
															conflictWordsR.put(s, set);
														}
														set.add(tuple);
														*/
														
													}
													
												}
											}
										}
									}
									
								}
							}
							//cover.replace(i, i+length, replacement.toString());
						}
					}
				} //else misses++;
				if (TRACK_WORDS) {
					if (words != null && words.size() > 0) { // this is not quite right for ENFORCE_UNIQUE (it prints some dupes that aren't counted towards fitness)
						foundWords.append(i).append(" (");
						for (String w : words)
							foundWords.append(w).append(":").append(Zodiac.cipher[Zodiac.CIPHER].substring(i,i+w.length())).append(", ");
						foundWords.delete(foundWords.length()-2, foundWords.length());
						foundWords.append(") ");
					}
				}
				i++;
			}
			if (TRACK_WORDS) this.foundWordsDictionary.append("len" + length + ":").append(foundWords).append(" ");
			foundWords = new StringBuffer(); 
			
		}
		
		
		if (CONFLICT) {
			//System.out.println("mapping size: " + mappings.size());
			
			//int num = 0;
			/*
			for (Character c : conflictMappings.keySet()) {
				System.out.println("c " + c);
				for (Character p : conflictMappings.get(c).keySet()) {
					System.out.println(" - p " + p);
					for (String s : conflictMappings.get(c).get(p)) {
					}
				}
			}*/
			
			if (TRACK_WORDS) {
				String chars = "Mappings: ";
				for (Character c : conflictMappings.keySet()) {
					chars+=c+":";
					for (Character p : conflictMappings.get(c).keySet()) {
						chars+=p+"(";
						for (String s : conflictMappings.get(c).get(p)) {
							chars+=s+" ";
						}
						chars+=") ";
					}
					chars += " ";
				}
				foundWordsDictionary.append(chars);
			}
			
			//VertexCoverProblem vc = new VertexCoverProblem(conflictWordsL, conflictWordsR);
			//vc.findOptimumVertexCover(4, false);
			//System.out.println("LEFT ENDPOINTS");
			String[] kv; int pos;
			//for (String s1 : conflictWordsL.keySet()) {
				//kv = s1.split(","); pos = Integer.valueOf(kv[1]);
				//System.out.println(s1 + " " + cipher.substring(pos, pos+kv[0].length()));
//				num += conflictWordsL.get(s1).size();
				//for (String s2 : conflictWordsL.get(s1)) {
				//	kv = s2.split(","); pos = Integer.valueOf(kv[1]);
				//	System.out.println(" - " + s2 + " " + cipher.substring(pos, pos+kv[0].length()));
				//}
	//		}
			foundWordConflicts = edges.size();
			int num2 = edgeMap.keySet().size();
			
			/*int num2 = 0;
			for (String s1 : conflictWordsR.keySet()) {
				num2 += conflictWordsR.get(s1).size();
			}
			System.out.println(num + " " + num2);*/
			
			
			
			//System.out.println("RIGHT ENDPOINTS");
			/*for (String s1 : conflictWordsR.keySet()) {
				kv = s1.split(","); pos = Integer.valueOf(kv[1]);
				System.out.println(s1 + " " + cipher.substring(pos, pos+kv[0].length()));
				num += conflictWordsR.get(s1).size();
				for (String s2 : conflictWordsR.get(s1)) {
					kv = s2.split(","); pos = Integer.valueOf(kv[1]);
					System.out.println(" - " + s2 + " " + cipher.substring(pos, pos+kv[0].length()));
					//num++;
				}
			}*/
			
			/* let's find a maximal matching, which yields a 2-approximation of vertex set cover. 
			 * subtract the node cover from our distinct set of tuples. */
			//THashSet<String> nodeCover = new THashSet<String>();
			/* select any edge.  remove all nodes that are vertices incident to that edge.
			 * repeat until no remaining edges.
			 */
			
			foundWordCount = conflictNodes.size();
			
			THashSet<Edge> e = VertexCoverProblem.cloneEdges(edges);
			for (String s1 : edgeMap.keySet()) {
				for (Edge edge : edgeMap.get(s1)) { // remove all edges incident to this node
					//System.out.println(s1 + " " + edge + " e size " + e.size());
					if (e.contains(edge)) {
						e.remove(edge);
						conflictNodes.remove(s1);
					}
				}
			}
			/*for (String s1 : conflictWordsL.keySet()) {
				conflictNodes.remove(s1);
				for (String s2 : conflictWordsL.get(s1)) {
					if (conflictWordsR.contains(s2)) { // if this node has not yet been removed
						conflictNodes.remove(s2);
						conflictWordsR.remove(s2);
					}
				}
				conflictWordsL.remove(s1);
				conflictWordsR.remove(s1);
				
				//conflictWordsL.remove(s1);
			}*/
				/* scan all other endpoints to remove any place s1 appears. */
			/*	for (String s2 : conflictWords.get(s1)) {
					conflictNodes.remove(s2);
				}
			}*/
			foundWordEdgeNodes = num2;
			foundWordNodeCover = conflictNodes.size();
			conflictInfo = "num conflicts " + foundWordConflicts + ", #nodes " + foundWordCount + ", #edgeNodes " + foundWordEdgeNodes + ", #nodes cover " + foundWordNodeCover;
			//count -= num;
			
			/* now let's compute the coverage, but only using the words we found after removing the vertex cover. */
			cover = new int[decoded.length()];
			String w;
			fullDecoded = new StringBuffer(decoded);
			
			//String line = "";

			if (ENFORCE_UNIQUE)
				uniques = new THashSet<String>();
			if (TRACK_WORDS) foundWordsDictionary.append(". postconflict words: ");
			for (String tuple : conflictNodes) {
				w = tuple.split(",")[0];
				pos = Integer.valueOf(tuple.split(",")[1]);
				if (TRACK_WORDS) foundWordsDictionary.append(tuple + " ");

				if (!ENFORCE_UNIQUE || (ENFORCE_UNIQUE && !uniques.contains(w))) {
					fullDecoded.replace(pos, pos+w.length(), w);
					//line += w + "," + pos + " ";
					for (int j=pos; j<pos+w.length(); j++) {
						if (rewardLength) {
							//charCover = (char) (31 + length); 
							cover[j] = Math.max(cover[j], w.length()); // use max in case the words aren't ordered in ascending length.
						} else {
							cover[j] = 1;
						}
					}
					if (ENFORCE_UNIQUE) uniques.add(w);
				}
				
			}
			conflictInfo += ", conflictdecoded " + fullDecoded;
			//System.out.println(line);
		}
		
		//System.out.println("cons " + countCons + " noncons " + countNonCons);
		
		//return (count < 0 ? 0 : count);
		int count = 0;
		StringBuffer cov = new StringBuffer();
		for (int i=0; i<cover.length; i++) {
			count+=cover[i];
			cov.append((char)('0' + cover[i]));
		}
		coverageDictionaryScaled = cov.toString();
		conflictInfo += ", coverage count " + count;

		/*if (CONFLICT) {
			fit.multifitness[0+fitOffset] = conflictNodes.size();
			fit.multifitness[1+fitOffset] = (float)count;
		} else {
			fit.multifitness[fitOffset] = (float)count;
		}*/
		//fit.multifitness[fitOffset] = (float)count;
		
		//this.constraintCacheParent1 = null;
		//this.constraintCacheParent2 = null;
	}

	/** compute fitness trigram score using combined decoding */
	public int fitnessTrigramScore() {
		String combined = "";
		if (CIPHER_START > 0) {
			combined = decoded.substring(0, CIPHER_START);
		}
		combined += decoded.substring(CIPHER_START, CIPHER_END+1);
		if (CIPHER_END <= decoded.length() - 1) {
			combined += decoded.substring(CIPHER_END+1);
		}
		
		if (!combined.replaceAll(" ", "").equals(decoded.toString())) {
			throw new RuntimeException("they should be equal: " + combined + " and " + decoded);
		}
		
		return 10000+Math.round((float)ZodiacDictionary.computeDecryptoTrigramScore(combined));
	}
		
	
	/** compute fitness based on substring matches.  if unique is true, only count the total number of unique positions
	 * where words fit.  any word with length < minLength is ignored.  decoder is passed along to check for feasibility.
	 * if RESPECT_OVERLAP is false, we count the number of total number of unique positions (if unique is true).  this can cause
	 *    overly increased totals due to overlapped or words containing substrings that are themselves words.
	 * if RESPECT_OVERLAP is true, we count the number of characters the found words cover.  this absorbs all the overlaps.
	 * if useFuzzyConflictPoints is true, then we return the conflict points as the fitness measure.
	 * fuzzyMinLength specifies the minimum word size that counts towards a fuzzy match. 
	 */
	public int fitnessSubstring(int minLength, boolean unique, String text, THashMap<String, THashMap<String, Integer>> subPool, THashSet<String> prefixes, String[] wPool, String decoder, boolean useFuzzyConflictPoints, int fuzzyMinLen) {

		boolean RESPECT_OVERLAP = true; /* if true, we merge overlapped words to avoid over-counting mulitple words that occupy the same space.
	  otherwise, we ignore overlap and simply count all unique word lengths.*/

		StringBuffer overlap = new StringBuffer(decoded); 
		
		
		zodiacWordsFuzzy = "";
		String word; int pos;
		List<Object[]> list = substringMatches(minLength, text, subPool, prefixes, wPool, fuzzyMinLen);
		if (!unique) return list.size();
		
		THashSet[] plaintextGuessesHash = new THashSet[Zodiac.cipher[Zodiac.CIPHER].length()]; // track plaintext guesses for each position.
		if (useFuzzyConflictPoints) {
			for (int i=0; i<plaintextGuessesHash.length; i++) plaintextGuessesHash[i] = new THashSet<Character>();
		}
		
		
		THashSet<Integer> hash = new THashSet<Integer>();
		THashSet<String> fuzzyFound = new THashSet<String>(); 
		for (int i=0; i<list.size(); i++) {
			if (Zodiac.getDecoderForWordAtPosition(decoder, (String)list.get(i)[1], (Integer)list.get(i)[2], true) != null) {
				//System.out.println("hash added " + (Integer)list.get(i)[2]);
				word = (String) list.get(i)[1];
				pos = (Integer)list.get(i)[2];
				if (useFuzzyConflictPoints) {
					for (int j=0; j<word.length(); j++) {
						plaintextGuessesHash[j+pos].add(word.charAt(j));
					}
				}
				//hash.add((Integer)list.get(i)[2]);
				hash.add(pos);
			  if (!fuzzyFound.contains(word + " " + pos)) {
			  	zodiacWordsFuzzy += word + " " + pos + " ";
			  	fuzzyFound.add(word + " " + pos);
			  }
				if (RESPECT_OVERLAP)
					overlap.replace(pos, pos+word.length(), word.replaceAll(".", "_"));
			}
		}
		
		
		/* fuzzy match conflict analysis.  
		 * 
		 * a word guess is in conflict if its plaintext mapping to covered symbols conflicts with at least one other
		 * word guess in another position that covers one or more of the same symbols.
		 * 
		 * if we have n words that all conflict with each other, removing one or more words may resolve the conflict.
		 *
		 * example: 
		 * 
		 * tell 7 				
     *   k	
     * attention 33 	
     *  MJ ^Ik7
     * riverside 69 
     * k^L Z dr 
     * bates 116 
     * S  L 
     *
     * decodings for k: l, i, r
		 * decodings for ^: n, i
		 * decodings for L: v, e
		 * 
		 * 
		 * because of the possibilities for k, the words tell, attention, and riverside cannot all coexist.  because
		 * there are 3 possibilities for k, only one word can be selected without conflict.  which word do we pick to keep?
		 * if we pick tell, then we can pick bates, but not attention and riverside.  if we pick riverside, then 
		 * we cannot pick tell or attention, AND we cannot pick bates.  2 words vs 1 word, based on our selection.
		 * 
		 * good guesses will be harmonious with other good guesses.  so, the number of shared plaintext decodings for each
		 * symbol will be large.  
		 * 
		 * make a hashset, one for each symbol.  as we encounter guesses, track the plaintext decodings for each symbol.
		 * if we find a decoding that we already found before, add a point, as long as the symbol position is different 
		 * from the current position. 
		 * 
		 */
		
		/* map each symbol to a hashset of all guess across all positions; we add a point if we encounter 
		 * plaintext dupes for any symbol. */
		if (useFuzzyConflictPoints) {
			THashMap<Character, THashSet<Character>> guessMap = new THashMap<Character, THashSet<Character>>();
			Iterator<Character> it;
			char symbol, plaintext;
			int points = 0;
			THashSet<Character> value;
			for (int i=0; i<plaintextGuessesHash.length; i++) {
				if (decoded.charAt(i) == '?') { // don't count decoded spots, because they are already "baked into" the plaintext.
					symbol = Zodiac.cipher[Zodiac.CIPHER].charAt(i);
					if (guessMap.get(symbol) == null) guessMap.put(symbol, new THashSet<Character>());
					
					value = guessMap.get(symbol);
					it = plaintextGuessesHash[i].iterator();
					while (it.hasNext()) {
						plaintext = it.next();
						if (value.contains(plaintext)) points++;
						else value.add(plaintext);
					}
				}
			}
			//System.out.println("FUZZY CONFLICT POINTS: " + points);
			return points;
		}
		
		if (RESPECT_OVERLAP) {
			return overlap.toString().replaceAll("[^_]", "").length(); // remove all non-covered spaces.  this counts all spaces the found words occupy.
		}
		
		
		
		return hash.size();
		
		
	}
	
	/** compute fitness based on substring matches.  this greedy version of fitnessSubstring sums the total length of unique non-overlapping words
	 * that are fuzzily matched in the string.  this routines prefers to choose words with maximal length as it scans the found words.  it also filters out
	 * infeasible words.  decoder is passed in to check for feasibility.
	 */
	public static int fitnessSubstringGreedy(int minLength, boolean unique, String text, THashMap<String, THashMap<String, Integer>> subPool, String[] wPool, String decoder) {
		List<Object[]> list = substringMatchesSLOW(minLength, text, subPool, null, wPool);
		//(matched substring, matched word, word position)
		String[] biggest = new String[Zodiac.cipher[Zodiac.CIPHER].length()]; // track the biggest feasible words by position.
		
		String word; int pos;
		for (int i=0; i<list.size(); i++) {
			word = (String)list.get(i)[1];
			pos = (Integer)list.get(i)[2];
			if (biggest[pos] == null || (biggest[pos] != null && biggest[pos].length() < word.length())) { // a slot is availble
				if (Zodiac.getDecoderForWordAtPosition(decoder, word, pos, true) != null)
					biggest[pos] = word;
			}
		}
		
		THashSet<String> words = new THashSet<String>(); // count the number of distinct big non-overlapping words
		int i=0; int sum=0;
		while (i<biggest.length) {
			if (biggest[i] == null) i++;
			else {
				if (!words.contains(biggest[i])) {
					words.add(biggest[i]);
					sum+=biggest[i].length();
					//System.out.println("found "+ biggest[i] + " at " + i);
				}
				i+=biggest[i].length();
			}
		}
		return sum;
	}
	/** compute word uniqueness metric, as sum of lengths of each unique word found, provided that each word
	 * satisfies the given word length. */
	public int fitnessUniqueness(int minLength) {
		/* old version:
		THashSet<String> words = new THashSet<String>();
		String word;
		int sum = 0;
		for (int i=0; i<genome.length; i+=2) {
			word = getWordFromAllele(genome[i]);
			if (word.length() >= minLength) {
				if (!words.contains(word)) {
					words.add(word);
					sum += word.length();
					//System.out.println("fu " + word);
				}
			}
		}
		//return words.size();
		return sum;
		*/
		/* new version */ 
		int sum = 0;
		String word;
		
		if (conflictWordHash != null) {
			Iterator<String> it = conflictWordHash.keySet().iterator();
			while (it.hasNext()) {
				word = it.next();
				if (word.length() >= minLength) {
					sum += word.length();
				}
			}
			return sum;
		} else return 0;
		
	}

	/** compute word uniqueness metric, as sum of lengths of each unique word found, provided that each word
	 * satisfies the given word length. this version is applied ONLY on the parts of the cipher the word packing
	 * mechanism does not cover.  the idea is to measure how well the word plugging causes new words to arise
	 * elsewhere in the cipher.  */
	public int fitnessUniquenessOtherSLOW(int minLength, int maxLength) {
		int sum = 0;
		THashSet<String> words = new THashSet<String>();
		String word;
		StringBuffer d = new StringBuffer(decoded);
		d.delete(CIPHER_START, CIPHER_END+1);
		for (int len=minLength; len<=maxLength; len++) {
			for (int i=0; i<d.length() - len + 1; i++) {
				word = d.substring(i, i+len);
				if (wordPoolHash.contains(word)) { // word is in our word pool
					if (!words.contains(word)) {
						words.add(word);
						sum += word.length();
						//System.out.println(word);
					}
				}
			}
		}
		return sum;
	}
	
	/** compute word uniqueness metric, as sum of lengths of each unique word found, provided that each word
	 * satisfies the given word length. this version is applied ONLY on the parts of the cipher the word packing
	 * mechanism does not cover.  the idea is to measure how well the word plugging causes new words to arise
	 * elsewhere in the cipher.  */
	public int fitnessUniquenessOther(int minLength) {
		
		boolean RESPECT_OVERLAP = true; /* if true, we merge overlapped words to avoid over-counting mulitple words that occupy the same space.
		  otherwise, we ignore overlap and simply count all unique word lengths.*/

		StringBuffer overlap = new StringBuffer(decoded); 
		

		int sum = 0;
		THashSet<String> words = new THashSet<String>();
		String word;
		StringBuffer d = new StringBuffer(decoded);
		d.delete(CIPHER_START, CIPHER_END+1);
		zodiacWordsOther = "";
		int indexStart = 0; int indexEnd = 0;
		while (indexStart < d.length()) {
			String f = indexStart + "," + indexEnd + ", d len " + d.length();
			//System.out.println(" f " + f);
			word = d.substring(indexStart, indexEnd+1);
			//System.out.println("word " + word);
			
			if (wordPoolPrefixesHash.contains(word)) { // there's at least one word with this prefix
				// so, let's see if we have a word match
				if (word.length() >= minLength) {
					if (wordPoolHash.contains(word)) { // make sure we haven't seen this word before already
						if (!words.contains(word)) {
							words.add(word);
							zodiacWordsOther += word + " " + indexStart + " "; 
							//System.out.println(" - added " + word);
							if (!RESPECT_OVERLAP) sum += word.length();
							else overlap.replace(indexStart, indexStart+word.length(), word.replaceAll(".", "_"));
						}
					}
				}
				// advance the end pointer to look for more words that share this prefix
				if (indexEnd < d.length()-1) indexEnd++;
				else {
					indexStart++;
					indexEnd = indexStart;
				}
			} else {
				indexStart++;
				indexEnd = indexStart;
			}
		}
		
		if (RESPECT_OVERLAP) {
			sum = overlap.toString().replaceAll("[^_]", "").length(); // remove all non-covered spaces.  this counts all spaces the found words occupy.
		}
		return sum;
	}

	/** take the given dictionary hashset and generate a hashset of prefixes used to speed up dictionary searches. */
	public static THashSet<String> makePrefixesHash(THashSet<String> dictionary) {
		THashSet<String> prefixes = new THashSet<String>();
		if (dictionary != null) {
			Iterator<String> it = dictionary.iterator();
			String word;
			while (it.hasNext()) {
				word = it.next();
				for (int j=1; j<=word.length(); j++) {
					prefixes.add(word.substring(0,j));
				}
			}
		}
		return prefixes;
	}
	
	/** compute word uniqueness metric, as count of unique words found, provided that each word
	 * satisfies the given word length. */
	public int fitnessUniquenessCount(int minLength) {
		//THashSet<String> words = new THashSet<String>();
		
		String word;
		int sum = 0;
		if (conflictWordHash != null) {
			Iterator<String> it = conflictWordHash.keySet().iterator();
			while (it.hasNext()) {
				word = it.next();
				if (word.length() >= minLength) {
					sum ++;
				}
			}
		} 
		return sum;
	}
	
	/** compute decoder metric */
	public int fitnessDecoder() {
		return 0;
	}

	
	/** it is assumed that the decoder will be computed as a side effect to crossover  */
  public void defaultCrossover(EvolutionState state, int thread, 
      VectorIndividual ind) {
  	//System.out.println("crossing");
  	
	  /* too memory intensive 
	this.constraintCacheParent1 = this.constraintCacheOffspring;
	this.constraintCacheParent2 = ((CipherWordGene)ind).constraintCacheOffspring;
	this.constraintCacheOffspring = null;
	((CipherWordGene)ind).constraintCacheOffspring = null;
	*/
	  
  	int countNWO1 = 0, countNWO2 = 0, countNWO3 = 0, countNWP1 = 0, countNWP2 = 0, countNWP3 = 0;
  	
  	if (TRACK_SUCCESS_RATES) {
    	countNWO1 = this.countNonWildcardsConflictOverlap();
    	countNWO2 = ((CipherWordGene)ind).countNonWildcardsConflictOverlap();
    	countNWP1 = this.countNonWildcardsPlaintext();
    	countNWP2 = ((CipherWordGene)ind).countNonWildcardsPlaintext();
  	}
  	
  	
  	int type = state.random[thread].nextInt(2);
  	//int type = 0;
  	if (type == 0) {
  		defaultCrossoverCombine(state, thread, ind);
  	}
  	else if (type == 1) {
  		debug("defaultCrossoverMerge before");

  		defaultCrossoverMerge(state, thread, ind);
  		debug("defaultCrossoverMerge after");
  	} else {
  		/** DONT USE THIS ONE!  IT DOESN'T WORK! */
  		debug("defaultCrossoverPreserve before");
    	defaultCrossoverPreserve(state, thread, ind);
  		debug("defaultCrossoverPreserve after");
  		throw new RuntimeException("YOU SHOULD NOT USE THIS ONE!");
  	}

  	if (TRACK_SUCCESS_RATES) {
    	countNWO3 = this.countNonWildcardsConflictOverlap();
    	countNWP3 = this.countNonWildcardsPlaintext();

    	/* possibilities: this is worse than no parent, worse than one parent, or worse than both parents. */
    	if (countNWO3 < countNWO1 && countNWO3 < countNWO2) {
    		statsInc("defaultCrossoverCOWorseThanBothParents");
    		statsInc("defaultCrossoverCOWorseThanAtLeastOneParent");
    	}
    	else if (countNWO3 < countNWO1 || countNWO3 < countNWO2) {
    		statsInc("defaultCrossoverCOWorseThanOneParent");
    		statsInc("defaultCrossoverCOWorseThanAtLeastOneParent");
    	} else
    		statsInc("defaultCrossoverCOWorseThanNoParent");
    		
    	if (countNWP3 < countNWP1 && countNWP3 < countNWP2) {
    		statsInc("defaultCrossoverPTWorseThanBothParents");
    		statsInc("defaultCrossoverPTWorseThanAtLeastOneParent");
    	}
    	else if (countNWP3 < countNWP1 || countNWP3 < countNWP2) {
    		statsInc("defaultCrossoverPTWorseThanOneParent");
    		statsInc("defaultCrossoverPTWorseThanAtLeastOneParent");
    	} else
    		statsInc("defaultCrossoverPTWorseThanNoParent");
  	}
  	
  	//int[] oldGenome = genome.clone();
  	//defaultCrossoverPreserve(state, thread, ind);
  	
  	//if (hasConflict(genome)) { // sometimes there are STILL conflicts for some unknown reason.
  		//System.err.println("CONFLICT BY CROSSOVER");
  		//genome = oldGenome;
  	//}
  	
  	/*if (this.decoder == null || this.decoder.equals(resetDecoder)) { // we have a non-feasible individual, or a null decoder, for some reason.  reset it!
  		genome = new int[genome.length];
  		reset(state, thread);
  	}*/
  	
  	xoverTotal++;
  	
  }

  /** select one of the two parents at random.  keep its entire genome, and try to mix the other parent's genome
   * into it.  the goal is to do non-destructive crossover.
   */
  public void defaultCrossoverCombine(EvolutionState state, int thread, VectorIndividual ind) {
  	int[] g;
  	//String plug1, plug2;
  	//plug1 = this.encoding;
  	//plug2 = ((CipherWordGene)ind).encoding;
  	Date date1, date2, date3, date4;
  	date3 = new Date();
  	
  	//if (rand().nextBoolean()) { // take the other genome as is, and combine with this genome.
  	if (false) { // let's never destroy the current genome
  		String bug1, bug2;
  		bug1 = this.getDebugLine();
  		bug2 = ((CipherWordGene)ind).getDebugLine();
  		g = this.genome.clone();
  		conflictInit();
  		genome = new int[g.length];
  		int[] parentG = ((CipherWordGene)ind).genome;
  		date1 = new Date();
  		
  		/*
  		for (int i=0; i<parentG.length; i+=2) {
  			if (!allelePut(i,parentG[i],parentG[i+1])) {
  				System.err.println("Could not clone the other parent when trying allelePut(" + i + ","+parentG[i]+","+parentG[i+1]+").  This should NEVER happen!");
  				System.err.println("FATAL: bug1: " + bug1);
  				System.err.println("FATAL: bug2: " + bug2);
  				//throw new RuntimeException("Could not clone the other parent when trying allelePut(" + i + ","+parentG[i]+","+parentG[i+1]+").  This should NEVER happen!");
  			}
  		}*/
  		
  		copyIntoSelfFrom((CipherWordGene)ind);
  		
  		date2 = new Date();
  		//statsInc("defaultCrossoverCombinePutInitTime",(float)(date2.getTime()-date1.getTime()));
  	}
  	else { // take this genome as is, and combine with the other genome.
  		g = ((CipherWordGene)ind).genome;
  	}
  	
  	//int counter1, counter2;
  	//String d1, d2;
		//counter1 = countNonWildcards();
		//d1 = decoded.substring(CIPHER_START, CIPHER_END+1);
  	  	
  	ArrayList<Integer> pickFrom = new ArrayList<Integer>();
  	ArrayList<Integer> emptySpots = new ArrayList<Integer>();
  	
  	for (int i=0; i<genome.length; i+=2) {
  		if (genome[i] == 0)
  			emptySpots.add(i);
  	}
  	/*
  	if (emptySpots.size() > 0)
			statsInc("defaultCrossoverCombineEmptySpots");
  	else
			statsInc("defaultCrossoverCrombineNoEmptySpots");
  	 */
  	
  	for (int i=0; i<g.length; i+=2) {
  		if (getWordFromAllele(g[i]).length() > 0) {
  			pickFrom.add(i);
  		}
  	}
  	if (pickFrom.size() == 0) {
			statsInc("defaultCrossoverNoPickFrom");
  	}
  	
  	int choice;
  	int spot;
  	date1 = new Date();
  	while (pickFrom.size() > 0 && emptySpots.size() > 0) {
  		choice = rand().nextInt(pickFrom.size());
  		spot = emptySpots.get(0);
  		if (allelePut(spot, g[pickFrom.get(choice)], g[pickFrom.get(choice)+1])) 
  			statsInc("defaultCrossoverCombinePutAccept");
  		else
  			statsInc("defaultCrossoverCombinePutReject");
  		pickFrom.remove(choice);
  		emptySpots.remove(0);
  	}
  	date2 = new Date();
		//statsInc("defaultCrossoverCombinePutCombineTime",(float)(date2.getTime()-date1.getTime()));
  	
  	
		//counter2 = countNonWildcards();
		//decode();
		//d2 = decoded.substring(CIPHER_START, CIPHER_END+1);
		/*if (counter2 > counter1)
			statsInc("defaultCrossoverCombineGood");
		else if (counter2 == counter1) {
			statsInc("defaultCrossoverCombineSame");
			//System.out.println("d1: " + d1 + ", d2: " + d2);
			//System.out.println("plug1: " + plug1 + ", plug2: " + plug2);
		}
		else
			statsInc("defaultCrossoverCombineBad");
  	if (d1.equals(d2)) {
			statsInc("defaultCrossoverCombineDecodedSame");
  	} else {
			statsInc("defaultCrossoverCombineDecodedDifferent");
  	}*/
  	date4 = new Date();
		statsInc("defaultCrossoverCombineTotalTime",(float)(date4.getTime()-date3.getTime()));
  }
  /** mix two genomes together as much as possible without producing conflicts.  mixing is done
   * by selecting word/pos pairs at random from each of the two genomes and adding them one at
   * a time to the new set of word/pos pairs.
   * mixing is complete if we haven't had a successful placement of a word/pos pair within the
   * last MAX_ITERATIONS iterations.
   */
  public void defaultCrossoverMerge(EvolutionState state, int thread, VectorIndividual ind) {
  	int MAX_ITERATIONS = 100;

  	int[] genome1 = this.genome.clone();
  	int[] genome2 = ((CipherWordGene)ind).genome;
  	int[] gen;
  		
  	/* set up the current object as a new gene */
  	conflictInit();
  	genome = new int[genome1.length];
  	
  	int count = 0;
  	String word;
  	int w, p;
  	int index=0;
  	
  	while (count < MAX_ITERATIONS) {
  		if (state.random[thread].nextBoolean()) { // choose a random word from 1st gene
  			gen = genome1;
  		} else {
  			gen = genome2;
  		}
			index = 2*state.random[thread].nextInt(gen.length/2); 
			word = getWordFromAllele(gen[index]);
			if (word.equals("")) {
				count++; // blank spot; ignore
			} else {
				if (allelePut(index, gen[index], gen[index+1])) {
					;
				} else {
					count++;
				}
			} 
  	}
  	
  }
  /** repeat crossover until 0-conflict constraint is preserved, or NUM_TRIES is exceeded. 
   * also, picks randomly from the 3 different types of crossover, for a more diverse recombination approach.
   * 
   * JUST DISCOVERED that this doesn't work.  it is not calling the IntegerVectorIndividual overriden defaultCrossover method.
   * instead, the call jumps back into CipherWordGene.defaultCrossover.
   * 
   **/
  public void defaultCrossoverPreserve(EvolutionState state, int thread, 
      VectorIndividual ind) {
  	
  	/* debugging 
  	fitness();
  	String before = getDebugLine();
  	double beforeF = fitnessNGrams[1]; */ 
  	
  	//int[] oldGenome = genome.clone();
  	//String oldDecoder = decoder.toString();
  	int tries = 0;
  	boolean tryAgain = true;
  	/* randomly pick one of the 3 crossover types */
  	int type = state.random[thread].nextInt(3);
  	switch(type) {
  		case 0 :
  			((IntegerVectorSpecies) species).crossoverType = VectorSpecies.C_ONE_POINT;
  			break;
  		case 1 :
  			((IntegerVectorSpecies) species).crossoverType = VectorSpecies.C_TWO_POINT;
  			break;
  		case 2 :
  			((IntegerVectorSpecies) species).crossoverType = VectorSpecies.C_ANY_POINT;
  			break;
  	}
  	
  	CipherWordGene clone;
  	int[] g;
  	while (tryAgain && tries < NUM_TRIES) {
    	clone = (CipherWordGene)this.clone();
  		
  		((IntegerVectorIndividual)clone).defaultCrossover(state, thread, ind);
  		
  		/** check our crossover-applied clone for conflicts */
  		clone.conflictInit();
  		g = clone.genome.clone();
  		for (int i=0; i<clone.genome.length; i++) {
  			clone.genome[i] = 0;
  		}
  		for (int i=0; i<g.length; i+=2) {
  			if (!clone.allelePut(i, g[i], g[i+1])) {
  				break;
  			}
  			if (i==g.length-2) {
  	  		// made it to the end successfully, so "commit" the crossover-affected clone because we found one that has no conflicts.
  	  		conflictInit();
  	  		for (int j=0; j<genome.length; j++) {
  	  			genome[j] = 0;
  	  		}
  	  		for (int j=0; j<g.length; j+=2) {
  	  			allelePut(j, g[j], g[j+1]);
  	  			tryAgain = false;
  	  		}
  				
  			}
  		}

  		tries++;
  	}
  	
  }
  
  /** mutation that preserves the no-conflict constraint.  picks from several mutation schemes
   * at random for more diverse mutation.
   */
  public void defaultMutate(EvolutionState state, int thread) {
  	//System.out.println("mutating");
  	//resetDecoder(); 
  	
  	/* debugging
  	fitness();
  	double fitnessF = fitnessNGrams[1];
  	String before = getDebugLine(); */
  	//int[] oldGenome = genome.clone();
  	
  	
  	int countNWO1 = 0, countNWO2 = 0, countNWP1 = 0, countNWP2 = 0;
  	
  	if (TRACK_SUCCESS_RATES) {
    	countNWO1 = this.countNonWildcardsConflictOverlap();
    	countNWP1 = this.countNonWildcardsPlaintext();
  	}
  	
  	
  	boolean good = false;
  	//int type = state.random[thread].nextInt(3);
  	float p = state.random[thread].nextFloat();
  	//p = 0; // hack to force defaultMutateStandardPreserve, b/c it is much faster.
  	int type;
  	
  	type = state.random[thread].nextInt(11);
  	if (type == 10) {
  	  	for (int i=0; i<genome.length; i+=2) /* check for mutatation for each allele pair */
  	  		good = defaultMutateRemoveWord(state, thread); /* remove word one out of 11 times */
  	}
  	else if (type >= 5 && type < 10) /* 5 out of 11 times */ {
  		good = defaultMutateStandardPreserve(state, thread);
  	}
  	else  /* 5 out of 11 times */ {
  	  	for (int i=0; i<genome.length; i+=2) /* check for mutatation for each allele pair */
  	  		good = defaultMutateInsertWord(state, thread); /* add word nine out of ten times */
  	}
  	
  	//int counter = 0;
  	
  	/* selecting a mutation type based on a probability.  the probabilities are skewed based on a experimental sampling
  	 * that showed that the best mutator for producting non-conflicts was defaultMutateSliderPreserve, which worked
  	 * about 80% of the time.  defaultMutateStandardPreserve worked about 18% of the time, and defaultMutateSwapPreserve
  	 * about 2% of the time.
  	 */
  	//if (0 <= p && p < 0.02) /* 2% */ {
  	//	type = 1;
	  //	good = defaultMutateSwapPreserve(state, thread);
  	//}
  	/*else*/
  	//if (0 <= p && p < 0.20) /* 15% */ {
  	//	type = 0;
  		//debug += " mutate standard";
  		//counter = countNonWildcards();
  		//Date date1 = new Date();
	  	//good = defaultMutateStandardPreserve(state, thread);
	  	//Date date2 = new Date();
  		//statsInc("defaultMutateStandardPreserveTime",(float)(date2.getTime()-date1.getTime()));

	  	//if (good) {
	  		/*statsInc("defaultMutateStandardPreserveAccept");
	  		if (countNonWildcards() >= counter)
		  		statsInc("defaultMutateStandardPreserveAcceptGood");
	  		else
		  		statsInc("defaultMutateStandardPreserveAcceptBad");
	  			*/
	  		//debug("good defaultMutateStandardPreserve");
	  	//} else {
	  		//statsInc("defaultMutateStandardPreserveReject");
	  		//debug("bad defaultMutateStandardPreserve");
	  	//}
  	//} else if (0.15 <= p && p < 0.25) /* 10% */ { 
  	//	defaultMutateRemove(state, thread);
  	//}
  	//else /* 80% */ {
  		//type = 2;
    	//IntegerVectorSpecies s = (IntegerVectorSpecies) species;
  		
  		/* let's apply the mutation probability to each allele, so it acts more like the standard mutator */
      /*for(int x=1;x<genome.length;x++)
        if (state.random[thread].nextBoolean(s.mutationProbability)) {
      		boolean choose = state.random[thread].nextBoolean();
    		//boolean choose = false;
      	  good = defaultMutateSliderPreserve(state, thread, choose); // 50/50 split on chooseExistingWord.
    	  	if (good) {
    	  		debug("good defaultMutateSliderPreserve, choose " + choose);
    	  	} else {
    	  		debug("bad defaultMutateSliderPreserve, choose " + choose);
    	  	}
        }
  	}*/

		//String g = ""; for (int i=0; i<genome.length; i++) g+=genome[i]+",";
		//System.out.println(id + ": mutation complete; genome " + g);
  	
  	/*if (this.decoder == null || this.decoder.equals(resetDecoder)) { // we have a non-feasible individual, or a null decoder, for some reason.  reset it!
  		genome = new int[genome.length];
  		reset(state, thread);
  	}*/
  	mutationTotal++;
  	
  	//if (hasConflict(genome)) { // sometimes there are STILL conflicts for some unknown reason.
  		//System.err.println("CONFLICT BY MUTATION TYPE " + type);
  	//	genome = oldGenome;
  	//}

  	
  	/* debugging 
  	if (good) {
  		fitness();
  		if (fitnessF != fitnessNGrams[1]) {
    		state.output.message("GOOD MUTATION TYPE " + type + " BEFORE " + before);
    		state.output.message("GOOD MUTATION TYPE " + type + " AFTER " + getDebugLine());
  		}
  	} */

  	
  	if (TRACK_SUCCESS_RATES) {
    	countNWO2 = this.countNonWildcardsConflictOverlap();
    	countNWP2 = this.countNonWildcardsPlaintext();

    	if (countNWO2 < countNWO1) {
    		statsInc("defaultMutateCOWorse");
    	} else
    		statsInc("defaultMutateCONotWorse");
    		
    	if (countNWP2 < countNWP1) {
    		statsInc("defaultMutatePTWorse");
    	} else
    		statsInc("defaultMutatePTNotWorse");
  	}

  		
  }

  public static String genomeToString(int[] g) {
  	String result = "";
  	if (g == null) return result;
  	for (int i=0; i<g.length; i++) result += g[i] + (i==g.length-1 ? "" : ",");
  	return result;
  }

  
  /** randomly remove words, as a rough way to perform stochastic backtracking */
  public boolean defaultMutateRemove(EvolutionState state, int thread) {
  	IntegerVectorSpecies s = (IntegerVectorSpecies) species;
  	ArrayList<Integer> indices  = new ArrayList<Integer>();
  	boolean good = false;
    /*if (s.mutationProbability>0.0) {
      for(int x=0;x<genome.length;x+=2) {
      	if (getWordFromAllele(genome[x]).length() > 0) {
      		indices.add(x);
      	}
      }
      if (indices.size() > 0) {
      	for (int i=0; i<indices.size(); i++) {
          if (state.random[thread].nextBoolean(s.mutationProbability)) {
          	alleleRemove(indices.get(i));
          	good = true;
          }
      	}
      }
    }*/
    return good;
  }
  
  /** basic allele-wise mutation. when mutating alleles, ensure that the zero-conflict constraint is preserved.
  * returns true iff we performed a non-conflict mutation. */
  public boolean defaultMutateStandardPreserve(EvolutionState state, int thread) {
  	
  	//int[] oldGenome = genome.clone();
  	
  	int tries = 0; boolean tryAgain = true;
  	boolean good = true;
  	
  	int newAllele; int w; int p; int index;
  	IntegerVectorSpecies s = (IntegerVectorSpecies) species;
    //if (s.mutationProbability>0.0)
  	if (0>0.0)
        for(int x=0;x<genome.length;x++)
            //if (state.random[thread].nextBoolean(s.mutationProbability)) {
        	if (false) {
            		int oldAllele = genome[x];
            		while (tryAgain && tries < NUM_TRIES) {
                  //genome[x] = randomValueFromClosedInterval((int)s.minGene(x), (int)s.maxGene(x), state.random[thread]);
            			index = x;
            			if (index % 2 == 1) index = x-1;
            			newAllele = randomValueFromClosedInterval((int)s.minGene(x), (int)s.maxGene(x), state.random[thread]);
            			if (x % 2 == 0) { // we're mutating the word
            				if (USE_WORD_POINTER) {
            					w = wordPointer;
            				} else {
            					w = newAllele;
            				}
            				p = genome[index+1];
            			} else { // we're mutating the position
            				w = genome[index];
            				//p = genome[index+1];
            				p = newAllele;
            			}
            				
                  //tryAgain = hasConflict(genome[x/2*2],genome[x/2*2+1]);
            			//System.out.println(id + ": " + this.toString() + ": " + x + "," + tries + " trying " + getWordFromAllele(w) + "," +getPositionFromAllele(p) + "(" + w + "," + p + ")");
            			
            			//int[] check = genome.clone();
                  tryAgain = !allelePut(index, w, p);
                  /*if (tryAgain) {
                  for (int i=0; i<genome.length; i++) {
                  	if (genome[i] != check[i]) throw new RuntimeException("goddammit " + i + " " + genome[i] + " " + check[i]);
                  }
                  }*/
                  
                  //System.out.println(id + ": tryAgain " + tryAgain);
                  tries++;
              		//if (tryAgain)
              		//	genome[x] = oldAllele; // could not mutate and satisfy 0-conflict constraint; revert to original allele.
            		}
            		if (!tryAgain) {
            			// should already be done via full hasConflict call above.  updateDecoder(genome[x/2*2], genome[x/2*2+1]);
            			mutationGood++;
            			if (USE_WORD_POINTER) wordPointer = (wordPointer + 1) % wordPool.length;
            		} else good = false;
            }
    return good;
		}	

  /** mutate by swapping two words. when mutating alleles, ensure that the zero-conflict constraint is preserved.
   * returns true iff we performed a non-conflict mutation. */
  /*
  public boolean defaultMutateSwapPreserve(EvolutionState state, int thread) {
  	
  	//int[] oldGenome = genome.clone();
  	
  	boolean tryAgain = true;
  	int tries = 0;
  	boolean good = true;
  	
  	IntegerVectorSpecies s = (IntegerVectorSpecies) species;
    if (s.mutationProbability>0.0)
        for(int x=1;x<genome.length;x++)
            if (state.random[thread].nextBoolean(s.mutationProbability)) {
            		int y = -1;
            		int oldAlleleX = genome[x];
            		int oldAlleleY;
            		while (tryAgain && tries < NUM_TRIES) {
              		y=x;
              		while (y==x) // get a 2nd position to swap with; ensure it is different than the 1st. 
              			y = state.random[thread].nextInt(genome.length);
              		oldAlleleY = genome[y];

              		int tmp = genome[x];
                  genome[x] = genome[y];
                  genome[y] = tmp;
                  //tryAgain = hasConflict(genome[x/2*2],genome[x/2*2+1]) || hasConflict(genome[y/2*2],genome[y/2*2+1]);
                  tryAgain = hasConflict(genome);
              		if (tryAgain) {
              			genome[x] = oldAlleleX; // could not mutate and satisfy 0-conflict constraint; revert to original allele.
              			genome[y] = oldAlleleY;
              		}
                  tries++;
            		}
            		if (!tryAgain) {
            			//updateDecoder(genome[x/2*2], genome[x/2*2+1]);
            			//updateDecoder(genome[y/2*2], genome[y/2*2+1]);
            			mutationGood++;
            		} else good = false;
            }
    return good;
		}*/	
  
  /** mutate by picking a new random word, and stuffing it into some random zero-conflict position. when mutating alleles, ensure that the zero-conflict constraint is preserved. 
   * returns true iff we performed a non-conflict mutation.
   * if chooseExistingWord is true, then we remove an existing word and slide it around in new positions.  we then pick one position at random.
   *  */
  public boolean defaultMutateSliderPreserve(EvolutionState state, int thread, boolean chooseExistingWord) {
  	
  	IntegerVectorSpecies s = (IntegerVectorSpecies) species;
    //if (s.mutationProbability>0.0)
  	if (false)
		  //if (state.random[thread].nextBoolean(s.mutationProbability)) {
  		if (false) {

		  	int w;
		  	if (USE_WORD_POINTER) {
		  		w = wordPointer;
		  	} else {
		  		if (chooseExistingWord) { // pick existing word at random
		  			ArrayList<Integer> existingWords = new ArrayList<Integer>(); // list of genome positions to pick from
		  			String word;
		  			for (int i=0; i<genome.length; i+=2) {
		  				word = getWordFromAllele(genome[i]);
		  				if (word.length()>0) {
		  					existingWords.add(i);
		  				}
		  			}
		  			if (existingWords.size() == 0) { // didn't find any usable word
		  				w = state.random[thread].nextInt(wordPool.length); // then use some random new word 
		  			} else {
			  			// get random genome pos, get the word allele there, and translate it to an index into the word pool.
			  			int pos = existingWords.get(state.random[thread].nextInt(existingWords.size()));
			  			w = genome[pos] % wordPool.length;
			  			//System.out.println(sortedKey + ": removed word " + w + " (" + wordPool[w] + ") from pos " + getPositionFromAllele(genome[pos+1]));
			  			alleleRemove(pos); // remove the word from the found position.  it is expected that the random selection will then have AT MINIMUM one feasible spot to pick from.
		  				
		  			}
		  			
		  		} else {
				  	w = state.random[thread].nextInt(wordPool.length);
		  		}
		  	}
		  	
		  	ArrayList<Integer> list = new ArrayList<Integer>(); // list of candidate positions
		  	int gIndex = -1;
		  	//String debug = "";
		  	for (int i=0; i<genome.length; i+=2) { // look for free spot to replace in the genome
		  		if (genome[i] < wildCardProbability*maxAllele)
		  			gIndex = i; break;
		  	}
		  	if (gIndex == -1) { // didn't find a free spot; pick an occupied one at random then.
		  		gIndex = state.random[thread].nextInt(genome.length/2)*2;
		  		//debug += "clobbered ";
		  	}
		  	
		  	
		  	
		  	String word;// = wordPool[w];
		  	while (w<wildCardProbability*maxAllele) w += wordPool.length; // avoid the blanks
		  	
		  	//int pos = state.random[thread].nextInt(Zodiac.cipher[Zodiac.CIPHER].length());
		  	//for (int i=0; i<Zodiac.cipher[Zodiac.CIPHER].length(); i++) { // this wasn't taking cipher subset into account
		  	for (int i=CIPHER_START; i<CIPHER_END+1; i++) {
		  		//if (!hasConflict(w, i)) {  // was (w, pos); was buggy b/c pos was a fixed random int during entire loop.
		  		if (allelePut(gIndex, w, i)) { // test feasibility
		  			alleleRemove(gIndex); // remove it, since we're just testing now
		  			list.add(i); 
		  		}
		  	}
		  	if (list.size() > 0) { // choose a zero-conflict position at random and assign it to the genome.
		  		/* perhaps hasConflict(w,i) is not truly detecting conflicts.  verify first before we do the assignment. */
		  		/*int oldW = genome[gIndex];
		  		int oldP = genome[gIndex+1];
		    	genome[gIndex] = w;
		    	genome[gIndex+1] = list.get(state.random[thread].nextInt(list.size()));
		    	updateDecoder(genome[gIndex], genome[gIndex+1]);
		    	if (hasConflict(genome)) {
		    		//System.err.println("- CONFLICT WITHIN SLIDER!");
		    		genome[gIndex] = oldW;
		    		genome[gIndex+1] = oldP;
		    		return false;
		    	} else {
			    	mutationGood++;
			    	return true;
		    	}*/
		  		if (allelePut(gIndex, w, list.get(state.random[thread].nextInt(list.size())))) {
		  			if (USE_WORD_POINTER) wordPointer = (wordPointer + 1) % wordPool.length;
		  			//System.out.println(sortedKey + ": added word  (" + getWordFromAllele(w) + ") to pos " + genome[gIndex+1]);

		  			return true;
		  		} else {
		  			System.err.println("EARLIER SLIDER FEASIBILITY TEST FAILED; THIS SHOULD NOT OCCUR.");
		  			return false;
		  		}
		    	//debug += "injected word " + word + " at " + genome[gIndex+1]; 
		  	} //else debug += " bah, no free spots.";
		  	//System.out.println(debug);
		  	return false;
		  }
  	
  	return true;
  }
  
  /**
	 * Inspects the decoding to locate feasible positions to place words.
	 * Selects one of these positions at random and selects a random feasible
	 * word to place there. returns true iff we performed a non-conflict
	 * mutation.
	 */
	public boolean defaultMutateInsertWord(EvolutionState state, int thread) {

		boolean good = false;
		
		IntegerVectorSpecies s = (IntegerVectorSpecies) species;
		//if (s.mutationProbability > 0.0)
		if (false)
			//if (state.random[thread].nextBoolean(s.mutationProbability)) {
			if (false) {
				
				
				int g = -1;
				for (int i=0; i<genome.length; i+=2) {
					if ("".equals(getWordFromAllele(genome[i]))) {
						g = i;
						break;
					}
				}
				if (g == -1) {
					debug(thread + " no spot available");
					return good;
				}

				
				/* inspect positions */
				ArrayList<Integer> positions = new ArrayList<Integer>();
				
				int pos = -1;
				String overlap = conflictOverlap.substring(CipherWordGene.CIPHER_START, CipherWordGene.CIPHER_END+1);
				//debug(thread + " looking at " + overlap);
				for (int i=0; i<overlap.length(); i++) {
					if (pos == -1 && overlap.charAt(i) == '?') { /* first occurence of '?' */
						pos = i;
					} else if (overlap.charAt(i) != '?') { /* not an occurrence of '?' */
						pos = -1;
					} else if (pos > -1) {
						if ((i-pos+1) >= ALLELE_ADD_MIN_WORD_LENGTH) {
							positions.add(pos);
							//debug(thread + " added " + pos + " for " + overlap + "[" + overlap.substring(pos) + "]");
							pos++;
						}
					}
				}
				
				if (positions.size() > 0) {
					int posPick = state.random[thread].nextInt(positions.size()); // pick random free spot
					int len = 0;
					for (int i=positions.get(posPick); i<overlap.length(); i++) { // compute the length of the free spot
						if (overlap.charAt(i) == '?')
							len++;
						else
							break;
						if (len >= 10) break;
					}
					ArrayList<String> possibleWords = new ArrayList<String>();
					int cpos = CIPHER_START + positions.get(posPick);
					for (int i=4; i<=Math.min(len,10); i++) {
						possibleWords.addAll(ZodiacDictionary.getWordsConstrainedBy(ZodiacDictionary.wordConstraintDatabase, 
							Zodiac.cipher[Zodiac.CIPHER].substring(cpos, cpos+i), decoded.substring(cpos, cpos+i)));
					}
					
					/*
					String line = "";
					for (String w : possibleWords) {
						line += w + " ";
					}
					
					debug(thread + " len " + len + " cpos " + cpos + " position " + positions.get(posPick));
					
					debug(thread + " words that fit in [" + Zodiac.cipher[Zodiac.CIPHER].substring(cpos, cpos+len) + "] for plaintext [" +
							decoded.substring(cpos, cpos+len) + "]: " + line);
					*/
					if (possibleWords.size() > 0) {
						int wordPick = state.random[thread].nextInt(possibleWords.size()); // pick random free spot
						good = allelePut(g, getAlleleFromWord(possibleWords.get(wordPick)), positions.get(posPick));
						//debug(thread + " alleleput word " + possibleWords.get(wordPick) + " for position " + positions.get(posPick) + ", genome index " + g + "? " + good);
					}
				}
			}
		return good;
	}	
	/**
	 * Remove a random word from the current encoding.  Returns true only if we perform removal. 
	 */
	public boolean defaultMutateRemoveWord(EvolutionState state, int thread) {
		
		IntegerVectorSpecies s = (IntegerVectorSpecies) species;
		//if (s.mutationProbability > 0.0)
		if (false)
			//if (state.random[thread].nextBoolean(s.mutationProbability)) {
			if (false) {
				ArrayList<Integer> choices = new ArrayList<Integer>(); /* word removal choices */
				for (int i=0; i<genome.length; i+=2) {
					if (!"".equals(getWordFromAllele(genome[i]))) {
						choices.add(i);
					}
				}
				if (choices.size() > 0) {
					alleleRemove(choices.get(state.random[thread].nextInt(choices.size())));
					return true;
				} else {
					debug("no choices available.");
				}
				
			}	
		
		return false;
	}  
  
  	/** pick a reset method at random */
	  public void reset(EvolutionState state, int thread) {
	  	// int type = state.random[thread].nextInt(2);
			/*
			 * if (id == 0) { CipherWordGene.globalId++; id = globalId; }
			 */
	  	
	  	if (conflictWordHash == null) conflictInit();
	  	
	  	int type = 0;
	  	switch(type) {
	  		case 0 :
	  	  	resetOneRandomWord(state, thread);
	  			break;
	  		case 1 :
	  	  	// resetSentence(state, thread);
	  			break;
	  	}
	  }
  
		/**
		 * starts with one random word placed in some random location of the
		 * ciphertext.
		 */
	  public void resetOneRandomWord(EvolutionState state, int thread) {
		  IntegerVectorSpecies s = (IntegerVectorSpecies) species;
		  
		  boolean again = true;
		  
		  int g1 = -1;
		  int g2 = -1;
		  int x = randomValueFromClosedInterval(0, genome.length/2-1, state.random[thread]) * 2;
		  while (again) {
			  int word = 0;
			  if (USE_WORD_POINTER) {
			  	word = wordPointer;
			  } else {
				  System.out.println("s " + (s == null ? "n" : s));
				  while (getWordFromAllele(word).length() < RESET_MIN_WORD_LENGTH)
				  	word = randomValueFromClosedInterval((int)s.minGene(0), 
				  			(int)s.maxGene(0), 
				  			state.random[thread]);
			  }
			  
			  g1 = word;
			  g2 = randomValueFromClosedInterval((int)s.minGene(x), (int)s.maxGene(x)-getWordFromAllele(word).length(), state.random[thread]);
			  //System.out.println(id + ": reset trying " + x + ", " + g1 + "," + g2);
			  again = !allelePut(x,g1,g2);
		  }
		  
		  if (USE_WORD_POINTER) wordPointer = (wordPointer + 1) % wordPool.length;
		  
		  //System.out.println(id + ": reset done");
		  //genome[x] = g1;
		  //genome[x+1] = g2;
	  }
  
		/** starts with one random word placed in some random location of the ciphertext (but don't use species). */
	  public void resetOneRandomWordWithoutSpecies(EvolutionState state, int thread) {
		  //IntegerVectorSpecies s = (IntegerVectorSpecies) species;
		  
		  boolean again = true;
		  
		  int g1 = -1;
		  int g2 = -1;
		  int x = randomValueFromClosedInterval(0, genome.length/2-1, state.random[thread]) * 2;
		  while (again) {
			  int word = 0;
			  if (USE_WORD_POINTER) {
			  	word = wordPointer;
			  } else {
				  while (getWordFromAllele(word).length() < RESET_MIN_WORD_LENGTH)
				  	word = randomValueFromClosedInterval(0, 
				  			maxAllele-1, 
				  			state.random[thread]);
			  }
			  
			  g1 = word;
			  g2 = randomValueFromClosedInterval(0, maxAllele-1-getWordFromAllele(word).length(), state.random[thread]);
			  //System.out.println(id + ": reset trying " + x + ", " + g1 + "," + g2);
			  again = !allelePut(x,g1,g2);
		  }
		  
		  if (USE_WORD_POINTER) wordPointer = (wordPointer + 1) % wordPool.length;
		  
		  //System.out.println(id + ": reset done");
		  //genome[x] = g1;
		  //genome[x+1] = g2;
	  }
		/** starts with one random word placed in some random location of the ciphertext. */
	  public void resetWithThisRandomWord(EvolutionState state, int thread, int wordAllele) {
		  IntegerVectorSpecies s = (IntegerVectorSpecies) species;
		  
		  boolean again = true;
		  
		  int g1 = -1;
		  int g2 = -1;
		  int x = randomValueFromClosedInterval(0, genome.length/2-1, state.random[thread]) * 2;
		  while (again) {
			  g1 = wordAllele;
			  g2 = randomValueFromClosedInterval((int)s.minGene(x), (int)s.maxGene(x)-getWordFromAllele(wordAllele).length(), state.random[thread]);
			  //System.out.println(id + ": reset trying " + x + ", " + g1 + "," + g2);
			  again = !allelePut(x,g1,g2);
		  }
		  
		  //System.out.println(id + ": reset done");
		  //genome[x] = g1;
		  //genome[x+1] = g2;
	  }
  	/** start at the beginning, and fill in words randomly until 0-conflict constraint can no
  	 * longer be satisfied. */
	  public void resetSentenceOBSOLETE(EvolutionState state, int thread) {
		  IntegerVectorSpecies s = (IntegerVectorSpecies) species;
		  int word;
	  	
		  ArrayList<Integer> words;
		  boolean keepGoing = true;
		  int pos = 0;
		  
		  int offset = 0;
		  while (offset < wildCardProbability*maxAllele)
		  	offset += wordPool.length;
		  
		  int x = 0;
		  
		  while (keepGoing && x < genome.length) {
		  	/* for each candidate position, loop thru word pool and collect all words that can be placed without conflict.
		  	 * then place one of these words randomly into the position, then continue.
		  	 */
		  	words = new ArrayList<Integer>();
		  	for (int i=0; i<wordPool.length; i++) {
		  		if (!hasConflictOBSOLETE(i+offset, pos))
		  			words.add(i);
		  	}
		  	if (words.isEmpty()) {// 0-conflict can no longer be satisfied
		  		//keepGoing = false; 
		  		pos++; // try the next position
		  	}
		  	else {
		  		word = words.get(state.random[thread].nextInt(words.size()));
		  		genome[x] = offset + word;
		  		genome[x+1] = pos;
		  		updateDecoderOBSOLETE(genome[x], genome[x+1]);
		  		pos += wordPool[word].length();
		  	}
		  	x += 2;
		  }
		  
	  }
	  
		/** init all substring pools */
		public static void initAllPools() {
			if (substringPool3 == null) {
				substringPool3 = initSubstringPool(wordPool, 3);
				substringPool4 = initSubstringPool(wordPool, 4);
				substringPool5 = initSubstringPool(wordPool, 5);
				substringPool6 = initSubstringPool(wordPool, 6);
				THashSet<String> tmp;
				tmp = new THashSet<String>();
				Iterator<String> it;
				
				tmp = new THashSet<String>();
				it=substringPool3.keySet().iterator();
				while (it.hasNext()) tmp.add(it.next());
				substringPrefixesPool3 = makePrefixesHash(tmp);
				tmp = new THashSet<String>();
				it=substringPool4.keySet().iterator();
				while (it.hasNext()) tmp.add(it.next());
				substringPrefixesPool4 = makePrefixesHash(tmp);
				tmp = new THashSet<String>();
				it=substringPool5.keySet().iterator();
				while (it.hasNext()) tmp.add(it.next());
				substringPrefixesPool5 = makePrefixesHash(tmp);
				tmp = new THashSet<String>();
				it=substringPool6.keySet().iterator();
				while (it.hasNext()) tmp.add(it.next());
				substringPrefixesPool6 = makePrefixesHash(tmp);
			}
			
			//if (substringGuessPool == null) substringGuessPool = initSubstringPool(guessPool);
			if (substringDictPool == null) substringDictPool = initSubstringPool(ZodiacDictionary.commonWords5000, 3);
			if (substringZodDictPool == null) substringZodDictPool = initSubstringPool(top40Pool, 3);
			if (substringZodSmegDictPool == null) substringZodSmegDictPool = initSubstringPool(ZodiacDictionary.zodiacTopWordsSMEG, 3);
			
		}
		
		/** init a wordpool hash from the wordpool */
		public static void initWordPoolHash() {
			wordPoolHash = new THashSet<String>();
			//wordPoolPrefixesHash = new THashSet<String>();
			int w;
			for (int i=0; i<wordPool.length; i++) {
				wordPoolHash.add(wordPool[i]);
				w=i+wordPool.length;
				while (w<wildCardProbability*maxAllele) w += wordPool.length;
				//wordPoolRepsHash.put(wordPool[i], w);
				/*for (int j=1; j<=wordPool[i].length(); j++) {
					wordPoolPrefixesHash.add(wordPool[i].substring(0,j));
				}*/
			}
			wordPoolPrefixesHash = makePrefixesHash(wordPoolHash);
		}
	  
	  /* creates a new substring pool hash using the given word pool */
	  public static THashMap<String, THashMap<String, Integer>> initSubstringPool(String[] pool, int fuzzyMinLen) {
	  	//int LENGTH = 4; // number of non-substring characters we want to match in the substring
	  	//substringPool = new THashMap[4];
	  	THashMap<String, THashMap<String, Integer>> result = new THashMap<String, THashMap<String, Integer>>();
	  	//substringPool[LENGTH] = new THashMap<String, THashMap<String, Integer>>();
	  	
	  	String word;
	  	String sub;
	  	String[] wildcards = new String[fuzzyMinLen-1];
	  	for (int i=0; i<wildcards.length; i++) wildcards[i] = "";
	  	
	  	boolean go = true;
	  	List subs;
	  	for (int i=0; i<pool.length; i++) {
	  		word = pool[i];
	  		if (word.length() >= fuzzyMinLen) {
	  			//for (int j=0; j<word.length() - LENGTH + 1; j++) {
	  				//sub = word.substring(j, j+LENGTH);
	  				
	  				//addSubstringToPool(LENGTH, sub, word);
	  				//wildcardIncrement(wildcards,3);
	  			//}
  				List<Object[]> wilds = getWildPermutations(wildcards, 4, word);
  				if (wilds != null) {
  					for (int j=0; j<wilds.size(); j++) {
  						addSubstringToPool(result, (String)wilds.get(j)[0], (Integer)wilds.get(j)[1], word);
  						//if (word.equals("much"))
  						//	System.out.println("add " + word + " " + (String)wilds.get(j)[0]);
  						
  					}
  				}

	  		}
	  	}
	  	System.out.println("substring pool initialized:");
	  	//for (int i=0; i<substringPool.length; i++) {
	  	//	if (substringPool[i] != null)
	  			System.out.println("substring pool size: " + result.size());
	  	//}
	  	return result;
	  	
	  }
	  /** add given substring to the given pool for the given word */
	  public static void addSubstringToPool(THashMap<String, THashMap<String, Integer>> pool, String substring, Integer offset, String word) {
	  	THashMap<String, Integer> val = pool.get(substring);
	  	if (val == null) {
	  		val = new THashMap<String, Integer>();
	  		pool.put(substring, val);
	  	}
	  	val.put(word, offset);
	  }
	  /** increment the given wildcard array, where max is the maximum number of wildcard characters in each position. 
	   * returns false IFF no further increments possible. */
	  public static boolean wildcardIncrement(String[] wildcards, int max) {
	  	String w;
	  	for (int i=wildcards.length-1; i>=0; i--) {
	  		if (wildcards[i].length() < max) {
	  			wildcards[i] += "?";
	  			return true;
	  		}
	  		wildcards[i] = "";
	  	}
	  	return false;
	  }
	  
	  /** get all wildcard substring permutations for the given word, ensuring each substring contains
	   * exactly (wildcards.length+1) non-wildcard letters, for the given word.  wildcard slots have max length of maxWild.
	   * each substring is paired in an array with its offset in the word.
	   */
		public static List<Object[]> getWildPermutations(String[] wildcards, int maxWild, String word) {
			boolean go = true;
			ArrayList<Object[]> result = new ArrayList<Object[]>();
			int len;
			int pos;
			String sub;
			while (go) {
				go = wildcardIncrement(wildcards, maxWild);
				len = 0;
				for (int i=0; i<wildcards.length; i++) len += wildcards[i].length();
				len += wildcards.length + 1;
				if (len <= word.length()) { // check that sum of wildcard lengths, when placed, will not exceed word length.
					sub = "";
					for (int j=0; j<= word.length() - len; j++) {
						pos = j;
						sub = ""+word.charAt(pos); 
						pos++;
						for (int k=0; k<wildcards.length; k++) {
							sub += wildcards[k]; 
							pos += wildcards[k].length();
							sub += word.charAt(pos); pos++;
						}
						//sub += word.charAt(pos); 
						result.add(new Object[] { sub, j });
					}
					//System.out.println(sub);
				}
			}
			return result;
		}

		/** another wildcard counter - this one generates all possible wildcard combinations for the given word, with no constraints */
		public static List<String> getWildPermutations(String word) {
			ArrayList<String> result = new ArrayList<String>();
			for (int len=1; len<=word.length(); len++) {
				int[] pointers = new int[len];
				StringBuffer wild;
				boolean go = true;
				while (go) {
					wild = new StringBuffer(word);
					for (int i=0; i<pointers.length; i++) {
						wild.setCharAt(pointers[i], '?');
					}
					if (!result.contains(wild.toString())) result.add(wild.toString());
					for (int i=pointers.length-1; i>=0; i--) {
						pointers[i]++;
						if (pointers[i] == word.length()) {
							pointers[i] = 0;
							if (i==0) go=false;
						} else break;
					}
				}
			}
			return result;
		}
		
		
		/** given a string, returns a List of (matched substring, matched word, word position) array entries using the substring/fuzzy match method. 
		 * ignore any word matches with length < minLength.
		 * */
		public static List<Object[]> substringMatchesSLOW(int minLength, String text, THashMap<String, THashMap<String, Integer>> subPool, THashSet<String> prefixes, String[] wPool) {
			//if (subPool == null)
			//	subPool = initSubstringPool(wPool);
			ArrayList<Object[]> list = new ArrayList<Object[]>();
			String chunk;
			Object[] o;
			
			String comp1, comp2;
			boolean match;
			THashMap<String, Integer> words;
			for (int i=0; i<text.length(); i++) {
				for (int len=3; len<=9 && len+i<text.length(); len++) {
					chunk = text.substring(i, i+len);
					if (chunk.startsWith("?")) break; // no substrings in the hash begin with a wildcard.
					words = subPool.get(chunk);
					//o = new Object[] {chunk, , i};
					//words = (THashMap<String, Integer>) o[1];
					if (words != null && words.size() > 0) {
						Iterator<String> it = words.keySet().iterator();
						while (it.hasNext()) { // see if words would fit into the original string 
							comp1 = it.next(); // matched word
							if (comp1.length() >= minLength) {
								if (i-words.get(comp1)+comp1.length() <= text.length() && i-words.get(comp1) >= 0) {
									comp2 = text.substring(i-words.get(comp1), i-words.get(comp1)+comp1.length()); // word candidate from original string
									match=true;
									for (int j=0; j<comp1.length(); j++) {
										if (comp2.charAt(j) != '?' && comp2.charAt(j) != comp1.charAt(j)) {
											match=false; break;
										}
									}
									if (match) {
										//System.out.println(chunk + "," + comp1 + "," + (i-words.get(comp1)));
										o = new Object[] {chunk, comp1, i-words.get(comp1)};
										list.add(o);
									}
								}
							}
						}
					}
				}
			}
			return list;
		}
		
		/** given a string, returns a List of (matched substring, matched word, word position) array entries using the substring/fuzzy match method. 
		 * ignore any word matches with length < minLength.
		 * 
		 * this is a slightly faster version, using prefixes hash.
		 * 
		 * 	- before: did 10000 allelePuts (with fitness) in 15695 ms.
		 *  -  after: did 10000 allelePuts (with fitness) in 14744 ms.
		 * */
		public static List<Object[]> substringMatches(int minLength, String text, THashMap<String, THashMap<String, Integer>> subPool, THashSet<String> prefixes, String[] wPool, int fuzzyMinLen) {
			//if (subPool == null)
			//	subPool = initSubstringPool(wPool);
			//int MIN_LEN = ;
			ArrayList<Object[]> list = new ArrayList<Object[]>();
			String chunk;
			Object[] o;
			
			String comp1, comp2;
			boolean match;
			THashMap<String, Integer> words;
			
			
			int indexStart = 0; int indexEnd = fuzzyMinLen-1;
			while (indexStart + fuzzyMinLen-1 < text.length()) {
				chunk = text.substring(indexStart, indexEnd+1);
				if (prefixes.contains(chunk)) { // there's at least one word with this prefix
					// so, let's see if we have a word match
					words = subPool.get(chunk);
					//o = new Object[] {chunk, , i};
					//words = (THashMap<String, Integer>) o[1];
					if (words != null && words.size() > 0) {
						Iterator<String> it = words.keySet().iterator();
						while (it.hasNext()) { // see if words would fit into the original string 
							comp1 = it.next(); // matched word
							if (comp1.length() >= minLength) {
								if (indexStart-words.get(comp1)+comp1.length() <= text.length() && indexStart-words.get(comp1) >= 0) {
									comp2 = text.substring(indexStart-words.get(comp1), indexStart-words.get(comp1)+comp1.length()); // word candidate from original string
									match=true;
									for (int j=0; j<comp1.length(); j++) {
										if (comp2.charAt(j) != '?' && comp2.charAt(j) != comp1.charAt(j)) {
											match=false; break;
										}
									}
									if (match) {
										//System.out.println(chunk + "," + comp1 + "," + (i-words.get(comp1)));
										o = new Object[] {chunk, comp1, indexStart-words.get(comp1)};
										list.add(o);
									}
								}
							}
						}
					}
					// advance the end pointer to look for more words that share this prefix
					if (indexEnd < text.length()-1) indexEnd++;
					else {
						indexStart++;
						indexEnd = indexStart + fuzzyMinLen-1;
					}
				} else {
					indexStart++;
					indexEnd = indexStart + fuzzyMinLen-1;
				}
			}
			return list;
		}
		
		/** initialize all members for progressive incremental conflict analysis */
		public void conflictInit() {
			resetDecoder();
			
			conflictOverlap = new StringBuffer(resetDecoded);

			if (conflictDerivedWordsReset == null) {
				conflictDerivedWordsReset = "";
				for (int i=0; i<(CIPHER_END - CIPHER_START + 1); i++) {
					conflictDerivedWordsReset += "?";
				}
			}
			conflictDerivedWords = new StringBuffer(conflictDerivedWordsReset);
			
			conflictWordStarts = new boolean[CIPHER_END-CIPHER_START+1];
			conflictWordEnds = new boolean[CIPHER_END-CIPHER_START+1];
			
			conflictWordHash = new THashMap<String,Integer>();
			
			if (Zodiac.alphabetHash == null)
				Zodiac.initAlphabetHash();
			
			if (conflictDictionaryCache == null)
				conflictDictionaryCache = new THashMap<String, Boolean>();
			
			/** init the decoder char to decoder position map */
			/*if (conflictDecoderCharHash == null) {
				conflictDecoderCharHash = new THashMap<Character, Integer>();
				for (int i=0; i<Zodiac.alphabet[Zodiac.CIPHER].length(); i++) {
					conflictDecoderCharHash.put(Zodiac.alphabet[Zodiac.CIPHER].charAt(i), i);
				}
			}*/
			
				
			/** init the decoder-to-decoded position mapping */
			if (conflictDecoderHash == null) {
				Character key;
				THashMap<Character, ArrayList<Integer>> mapping = new THashMap<Character, ArrayList<Integer>>();
				//conflictDecoderHash = new THashMap<Integer,List<Integer>>();
				//char key;
				for (int i=0; i<Zodiac.alphabet[Zodiac.CIPHER].length(); i++) {
					mapping.put(Zodiac.alphabet[Zodiac.CIPHER].charAt(i), new ArrayList<Integer>());
				}
				for (int i=0; i<Zodiac.cipher[Zodiac.CIPHER].length(); i++) {
					key = Zodiac.cipher[Zodiac.CIPHER].charAt(i);
					ArrayList<Integer> val = mapping.get(key);
					val.add(i);
					//System.out.println("putting " + key + ", " + i + "," + val.size());
					if (val.size() == 0) throw new RuntimeException("wtf");
					mapping.put(key,val);
				}
				
				/** convert our local mapping to an int[] array for later performance */
				Iterator<Character> it = mapping.keySet().iterator();
				conflictDecoderHash = new THashMap<Integer,int[]>();
				int dpos;
				int[] positions;
				List<Integer> positionsAsList;
				while (it.hasNext()) {
					key = it.next();
					dpos = Zodiac.alphabetHash.get(key);
					positionsAsList = mapping.get(key);
					positions = new int[positionsAsList.size()];
					for (int i=0; i<positions.length; i++) positions[i] = positionsAsList.get(i);
					//System.out.println("putting " + dpos + " (" + key + "), size " + positions.length);
					conflictDecoderHash.put(dpos, positions);
				}
			}
			
			/*
			Iterator<Character> it = conflictDecoderCharHash.keySet().iterator();
			char key;
			while (it.hasNext()) {
				key = it.next();
				System.out.println(key + "," + conflictDecoderCharHash.get(key));
			}
			
			Iterator<Integer> it2 = conflictDecoderHash.keySet().iterator();
			int key2; int[] val;
			String line;
			while (it2.hasNext()) {
				key2 = it2.next();
				val = conflictDecoderHash.get(key2);
				line = "";
				for (int i=0; i<val.length; i++) line += val[i] + " ";
				System.out.println(key2 + "," + Zodiac.alphabet[Zodiac.CIPHER].charAt(key2) + "," + line);
			}*/
			
			conflictDecoderWordCount = new int[Zodiac.alphabet[Zodiac.CIPHER].length()];
			
			conflictWordsToDecoder = new THashMap<String, int[]>();

		}
		
		public void checkShitFU() {
			if (conflictWordHash!= null) {
			Iterator<String> it = conflictWordHash.keySet().iterator();
			while (it.hasNext()) {
				String word = it.next();
				boolean found = false;
				for (int i=0; i<genome.length; i+=2) {
					if (getWordFromAllele(genome[i]).equals(word)) {
						found = true; break;
					}
				}
				if (!found) {
					throw new RuntimeException(this.toString() + ": well, sheeit; " + word + " isn't in the genome.  " + debug + ":" + getDebugLine());
				}
			}
			}
		}
		
		/*public void checkClone() {
			if (genomeClone == null) {
				genomeClone = genome.clone();
			} else {
				for (int i=0; i<genomeClone.length; i++) {
					if (genome[i] != genomeClone[i]) {
						throw new RuntimeException("something changed position " + i + " from " + genomeClone[i] + " to " + genome[i]);
					}
				}
				genomeClone = genome.clone();
				
			}
		}*/
		
		/** put the given word/pos pair at the given index of the genome.  may require removing the old value first,
		 * which necessitates updating the conflict state info.
		 */
		public boolean allelePut(int index, int w, int pos) {
			//String g = ""; for (int i=0; i<genome.length; i++) g+=genome[i]+",";
			//System.out.println(id + ": allelePut called with genome " + g);
			//g = ""; Iterator<String> shit = conflictWordHash.keySet().iterator();
			//while (shit.hasNext()) g += shit.next() + " ";
			//System.out.println(id + ": allelePut called with wordHash " + g);
			
			//checkClone();
			//checkShit();
			/*if (id == 0) {
				CipherWordGene.globalId++;
				id = globalId;
			}*/
			
			if (index % 2 == 1) throw new RuntimeException("Sorry, you must put an allele pair at an even index.");
			String word = getWordFromAllele(genome[index]);
			//String dbug = id + ": put " + index + "," + w + "," + pos + " (" + getWordFromAllele(w) + "," + getPositionFromAllele(pos) + ") ";
			//debug += dbug;
			//if (clusterCount != 666) System.out.println(dbug);
			int oldW = genome[index];
			int oldP = genome[index+1];
			boolean result;
			if (!word.equals("")) { // we have to replace the existing word/pos tuple.
				alleleRemove(index);
				//if (clusterCount != 666) System.out.println(id + ": remove resulted in derived " + conflictDerivedWords);
				//if (clusterCount != 666) System.out.println(id + ": remove resulted in decoder " + decoder);
			}
			if (alleleAdd(index, w, pos)) {
				//debug += " addsuccess ";
				result = true;
				//if (!getWordFromAllele(w).equals("") && clusterCount != 666) System.out.println(id + ": addsuccess resulted in derived " + conflictDerivedWords);
				//if (!getWordFromAllele(w).equals("") && clusterCount != 666) System.out.println(id + ": addsuccess resulted in decoder " + decoder);
				//g = ""; for (int i=0; i<genome.length; i++) g+=genome[i]+",";
				//System.out.println(this.id + ": just finished calling alleleAdd from allelePut.  " + g);
			} else {
				//debug += " addfail ";
				if (genome[index] != oldW || genome[index+1] != oldP)
					allelePut(index, oldW, oldP);
				//alleleRemove(index);
				//alleleAdd(index, oldW, oldP);
				//genome[index] = oldW;  // this was a bug
				//genome[index+1] = oldP; // this was a bug
				//if (!getWordFromAllele(w).equals("") && clusterCount != 666) System.out.println(id + ": addfail resulted in derived " + conflictDerivedWords);
				//if (!getWordFromAllele(w).equals("") && clusterCount != 666) System.out.println(id + ": addfail resulted in decoder " + decoder);
				result = false;
			}
			//genomeClone = genome.clone();
			//checkShitFU();
			return result;
		}
		
		public void debug(String msg) {
			if (DEBUG) {
				System.out.println("DEBUG: " + msg);
			}
		}
		
		/** attempt to add the given word/position tuple at the given genome index.
		 * returns true iff the placement produces no conflicts.
		 * this method won't change the genome state if it detects conflicts.
		 **/
		public boolean alleleAdd(int index, int w, int pos) {
			//String g = ""; for (int i=0; i<genome.length; i++) g+=genome[i]+",";
			//System.out.println(id + ": alleleAdd called with genome " + g);
			if (index % 2 == 1) throw new RuntimeException("Sorry, you must add an allele pair at an even index.");
			//checkShit();
			/* conditions for conflict:
			 * 		- the incoming word already exists in the genome (enforce uniqueness)
			 * 	  - when placed, the incoming word overlaps an existing word.
			 * 		- when placed, the incoming word extends beyond the end of the cipher (or the end of the cipher substring under consideration)
			 * 		- when placed, the incoming word forces a small infeasible word to form between two fixed word placements.
			 * 		- when placed, the incoming word produces a corresponding decoder that has at least one character that
			 * 				contradicts the current encoder.
			 */

			//debug += "add " + index + "," + w + "," + pos + " (" + getWordFromAllele(w) + "," + getPositionFromAllele(pos) + ") ";
			//System.out.println(id +  ": trying to add " + index + "," + w + "," + pos + " (" + getWordFromAllele(w) + "," + getPositionFromAllele(pos) + ") ");
			String word = getWordFromAllele(w);
			if (word.equals("")) return true; // empty spot.  ignore.
			
			if (word.length() < ALLELE_ADD_MIN_WORD_LENGTH)
				return false; // reject words that are too short.
			
			if (conflictWordHash.containsKey(word)) {
				debug("rejecting word " + word + ": it was already placed.");
				return false; // no dupes allowed.
			}
			int position = getPositionFromAllele(pos);
			if (position + word.length() > CIPHER_END + 1) {
				debug("rejecting word " + word + " pos " + position + ": it extends beyond the end.");
				return false; // don't allow words to fall off the end.
			}

			// check for overlap.  a word overlaps another if its placement in the decoded string encounters part of a previously placed word in any position.
			if (conflictOverlap.substring(position, position + word.length()).indexOf('_') > -1) {
				debug("rejecting word " + word + " pos " + position + ": it overlaps with another word placement.");
				return false;
			}
			
			//if (ALLELE_OP_USE_CONSTRAINT_DB) {
			//	newDerivedWords = null;
			//} else {
				// check for decoder conflicts.
				//StringBuffer newDecoded = new StringBuffer(decoded);
				char cipherSymbol;
				Character check;
				int decoderPos;
				//conflictWordsToDecoder
				int[] decoderPositions = new int[word.length()]; for (int i=0; i<word.length(); i++) decoderPositions[i] = -1;
				THashMap<Character, Character> seenChars = new THashMap<Character, Character>(); // track cipher symbols we've seen, so we know which ones this word's placement contributes to.  Map to the plaintext letter.
				for (int j=0; j<word.length(); j++) {
					cipherSymbol = Zodiac.cipher[Zodiac.CIPHER].charAt(position+j);
					//System.out.println("cipher " + cipherSymbol + ", word letter " + word.charAt(j));
					//if (!seenChars.contains(cipherSymbol)) { // no sense processing the same zodiac symbol twice.
						check = seenChars.get(cipherSymbol);
						if (check != null && check != '?' && check != word.charAt(j)) {
							debug("rejecting word " + word + " pos " + position + ": decoder self-conflict at word pos " + j + ": plaintext " + word.charAt(j) + " conflicts with already assigned plaintext " + check + " for symbol " + cipherSymbol);
							return false; // we've come across this cipher symbol in the same word already, but the symbol's letter assignment already conflicts with this one.
						}
						seenChars.put(cipherSymbol, word.charAt(j));
						decoderPos = Zodiac.alphabetHash.get(cipherSymbol);
						decoderPositions[j] = decoderPos;
						check = decoder.charAt(decoderPos);
						//System.out.println("check " + check);
						if (check != '?' && check != word.charAt(j)) { // decoder position was already specified with a different letter for a different word.
							debug("rejecting word " + word + " pos " + position + ": decoder conflict at word pos " + j + ": plaintext " + word.charAt(j) + " conflicts with already assigned plaintext " + check + " for symbol " + cipherSymbol);
							return false;
						}
					//} 
					conflictWordsToDecoder.put(word + " " + position, decoderPositions);
				}
				
				/* derive the new decoded string in the fewest steps, using the direct mappings between zodiac symbols and the list of decoded string positions. */
				StringBuffer newDerivedWords = new StringBuffer(conflictDerivedWords); // update the derived words at the same time
				int[] list;
				char plaintext;
				Iterator<Character> it = seenChars.keySet().iterator();
				while (it.hasNext()) {
					cipherSymbol = it.next(); 
					plaintext = seenChars.get(cipherSymbol);
					list = conflictDecoderHash.get(Zodiac.alphabetHash.get(cipherSymbol)); // get the list of string positions in the decoded plaintext for this cipher symbol
					//System.out.println("symbol " + cipherSymbol);
					//decoderPos = Zodiac.alphabetHash.get(cipherSymbol); // decoder position for this cipher symbol
					//System.out.println("new decoded " + newDecoded);
					for (int i=0; i<list.length; i++) {
						//newDecoded.setCharAt(list[i], plaintext);
						if (list[i] >= CIPHER_START && list[i] <= CIPHER_END)
							if (newDerivedWords.charAt(list[i]-CIPHER_START) == '?') newDerivedWords.setCharAt(list[i]-CIPHER_START, plaintext);
					}
				}
				
			//}  
			
			
			if (ENFORCE_TIGHTER_CONSTRAINTS) {
				// check the derived words (in gaps between our genome's words) for feasibility
				//System.out.println("pos " + position + ", p+w " + (position + word.length) + ", ndw len " + newDerivedWords.len
				newDerivedWords.replace(position-CIPHER_START, position-CIPHER_START+word.length(), word.replaceAll(".", "_"));
				//System.out.println("ndw " + newDerivedWords);
				String derivedWord;
				//String newDerivedWordsCipher = Zodiac.cipher[Zodiac.CIPHER].substring(CIPHER_START, CIPHER_END+1);
				String[] forcedWords = newDerivedWords.toString().split("_+");
				//List<THashSet<String>> found;
				boolean found;
				if (forcedWords != null) {
					for (int i=0; i<forcedWords.length; i++) {
						derivedWord = forcedWords[i];
						//System.out.println(i + " word " + derivedWord);
						if (derivedWord.equals("")) continue; // first derived word is sometimes an empty string.
						/* only consider the first (last) word if it is prefixed (suffixed) by an underscore, indicated it is a forced word that is not a substring of some other word. */
						if (i == 0 && newDerivedWords.charAt(0) != '_') {
							/* we are looking at the first chunk, which is a suffix.*/
							if (USE_DIVIDE_AND_CONQUER && derivedWord.length() <= DAC_MAX_LEN) {
								found = ZodiacDictionary.isWord(derivedWord, derivedWord, false, true);
								if (!found)
										return false;
							}
							continue;
						}
						if (i == forcedWords.length - 1 && newDerivedWords.charAt(newDerivedWords.length()-1) != '_') {
							// this is the last word - let's check its feasibility.
							// the last word is feasible if it is not a prefix of any known word.
							//System.out.println(i + " LAST word " + derivedWord);
							//if (!derivedWord.startsWith("?")) // let's ignore suffixes that start with wildcards, b/c they might not lead to correct determinations of feasibility (example:  ???esyes <== each ? is considered a word, and "esy" is not a valid prefix)
								//if (!feasiblePrefix(derivedWord)) return false;
							//TODO
							
							if (USE_DIVIDE_AND_CONQUER && derivedWord.length() <= DAC_MAX_LEN) {
								found = ZodiacDictionary.isWord(derivedWord, derivedWord, true, false);
								if (!found)
										return false;
							}
							
							continue;
						}
						
						//if (derivedWord.indexOf('?') == -1) { // don't consider derived words if they have any wildcards.
							//System.out.println(word);
						if (USE_DIVIDE_AND_CONQUER && derivedWord.length() <= DAC_MAX_LEN) {
							found = ZodiacDictionary.isWord(derivedWord, derivedWord, false, false);
							if (!found)
									return false;
						} else {
							
							if (derivedWord.length() < 5 /*&& derivedWord.indexOf('?') == -1*/) {
								if (!isDerivedWord(derivedWord)) {
									//System.out.println(derivedWord +" is not a word.");
									debug("rejecting word " + word + " pos " + position + ": derivedWord " + derivedWord + " is not feasible.  newDerivedWords was " + newDerivedWords); 
									return false;
								} else
									;
									//System.out.println(derivedWord +" is a word.");
							} else
								;
								//System.out.println("ignoring " + derivedWord);
								
							//}
						}
					}
				}
			}
			
			/* if we made it this far, we need to "commit" the changes we've collected up to this point.
			 */
			// decoder word contrib counts + new decoder
			it = seenChars.keySet().iterator();
			//StringBuffer newDecoder = new StringBuffer(decoder);
			int[] positions;
			while (it.hasNext()) {
				cipherSymbol = it.next();
				plaintext = seenChars.get(cipherSymbol);
				decoderPos = Zodiac.alphabetHash.get(cipherSymbol);
				this.conflictDecoderWordCount[decoderPos]++;
				decoder.setCharAt(decoderPos, plaintext);
				//new decoded
				positions = conflictDecoderHash.get(decoderPos);
				if (positions != null) {
					for (int j=0; j<positions.length; j++) {
						decoded.setCharAt(positions[j], plaintext);
					}
				}
			}
			
			
			// add new word to word hash
			this.conflictWordHash.put(word, position);
			// update the overlap string
			this.conflictOverlap.replace(position, position+word.length(), word.replaceAll(".", "_"));
			// update the derived words string
			this.conflictDerivedWords = new StringBuffer(newDerivedWords);
			//System.out.println("derived "+ this.conflictDerivedWords);
			genome[index] = w;
			genome[index+1] = pos;
			//if (!getWordFromAllele(w).equals(word)) throw new RuntimeException("ok, seriously, w " + w + " ain't loving word " + word);

			//g = ""; for (int i=0; i<genome.length; i++) g+=genome[i]+",";
			//System.out.println(this.id + ": " + this.toString() + ": we just put " + word + " is position " + position + " (genome " + genome[index] + "," + genome[index+1] + "), " + g);
			//checkShit();
			
			//System.out.println(this.toString() + " added " + word + "(" + w + ") at " + position + ", decoder " + decoder + ": " + decoded.substring(CipherWordGene.CIPHER_START, CipherWordGene.CIPHER_END+1));

			
			/*if (decoded.substring(CIPHER_START, CIPHER_END+1).indexOf(word) == -1) {
				throw new RuntimeException("Wtf, added " + word + " but decoded doesn't have it!  " + getDebugLine());
			}*/

			//System.out.println("added " + word + "," + position + ", overlap is " + conflictOverlap + ", derived is  " + conflictDerivedWords);
			
/*			
			if (this.clusterCount != 666) {
			CipherWordGene shit = new CipherWordGene();
			shit.clusterCount = 666;
			int[] newG = genome.clone();
			shit.genome = new int[genome.length];
			shit.conflictInit();
			for (int i=0; i<newG.length; i+=2) {
				shit.allelePut(i, newG[i], newG[i+1]);
			}
			if (!shit.decoded.toString().equals(decoded.toString())) {
				System.err.println("wtf on add?? ");
				System.err.println(this.debug + ":" + this.toString() + ":" + getDebugLine());
				System.err.println(shit.debug + ":" + shit.toString() + ":" + shit.getDebugLine());
				//throw new RuntimeException();
			}
			}
	*/		
			
			conflictWordStarts[position-CIPHER_START] = true;
			conflictWordEnds[position-CIPHER_START+word.length()-1] = true;
			//System.out.println(decoded.substring(CIPHER_START, CIPHER_END+1));
			return true;
		}
		
		/** returns true if the given word chunk is a feasible prefix; that is, if it is a valid prefix,
		 * or itself contains a word followed by a valid prefix.
		 */
		public static boolean feasiblePrefix(String word) {
			//System.out.println("checking " + word);
			int indexStart = 0, indexEnd = 0;
			String chunk;
			boolean result = true;
			while (indexStart < word.length()) {
				chunk = word.substring(indexStart, indexEnd+1);
				if (chunk.length() > 4) { // should we ever get here?
					System.out.println("SORRY, CHUNKS SHOULD NOT GET HERE.  [" + chunk + "].");
				}
				if (ZodiacDictionary.zodiacWordsSmallPrefixes.contains(chunk)) { // this chunk is a valid prefix
					if (isWord(chunk)) { // this portion IS a word, so it is feasible; move to the next portion of the word chunk.
						//System.out.println("a word " + chunk);
						if (word.equals(chunk)) { // we're at the end, and we found a word, so feasible.
							return true;
						}
						return feasiblePrefix(word.substring(indexEnd+1)); // recurse to determine the next chunk's feasibility. 
					} else {
						//System.out.println("a prefix " + chunk);
						indexEnd++;
						if (indexEnd-indexStart+1 > word.length()) return true;
					}
					
				} else { // we found an unknown prefix.  big assumption: we're only using our smallish list of prefixes.  we might exclude valid prefixes of larger words that we don't know about here.
					//System.out.println("not a prefix " + chunk);
					return false;	
				}
			}
			return result;
		}
		
		/** check the given word to see if it is a "Derived word"; that is, it has length between [1,4] and
		 * some combination of substrings within the input string form words that cover the entire input string.  
		 */
		public boolean isDerivedWord(String word) {
			boolean result = false;
			if (this.conflictDictionaryCache.containsKey(word))
				return conflictDictionaryCache.get(word);
			if (word.length() == 1) {
				if (isWord(word)) {
					result = true;
				}
			} else if (word.length() == 2) { // NOTE: this assumes that there are no feasible combinations of adjacent one-letter words (could be a bad assumption)
				if (isWord(word)) {
					result = true;
				}
			} else if (word.length() == 3) {
				// word possibilities: 1 letter + 2 letter, 2 letter + 1 letter, or 3 letter
				//System.out.println(word);
				String[] words = { 
						word.substring(0, 1), word.substring(1, 3),
						word.substring(0, 2), word.substring(2, 3),
						word
				};
				if ((isWord(words[0]) && isWord(words[1]))
				  ||(isWord(words[2]) && isWord(words[3]))
				  ||(isWord(words[4]))) {
					result = true;
				}
			} else if (word.length() == 4) {
				/* word length possibilities:
				 *   a b c d
				 *   a bc d
				 *   a b cd
				 *   ab c d
				 *   ab cd
				 *   a bcd
				 *   abc d
				 *   abcd
				 */
				String[] words = { 
						word.substring(0, 1), word.substring(1, 2), word.substring(2, 3), word.substring(3, 4), 
						word.substring(0, 1), word.substring(1, 3), word.substring(3, 4),
						word.substring(0, 1), word.substring(1, 2), word.substring(2, 4),
						word.substring(0, 2), word.substring(2, 3), word.substring(3, 4),
						word.substring(0, 2), word.substring(2, 4),
						word.substring(0, 1), word.substring(1, 4),  
						word.substring(0, 3), word.substring(3, 4),
						word
				};
				if ((isWord(words[0]) && isWord(words[1]) && isWord(words[2]) && isWord(words[3]))
					  ||(isWord(words[4]) && isWord(words[5]) && isWord(words[6]))
					  ||(isWord(words[7]) && isWord(words[8]) && isWord(words[9]))
					  ||(isWord(words[10]) && isWord(words[11]) && isWord(words[12]))
					  ||(isWord(words[13]) && isWord(words[14]))
					  ||(isWord(words[15]) && isWord(words[16]))
					  ||(isWord(words[17]) && isWord(words[18]))
					  ||(isWord(word))) {
					result = true;
				}
			}
			this.conflictDictionaryCache.put(word, result);
			return result;
		}

		
		/** remove the word/position tuple at the given index.  this requires undoing all state information associated with
		 * this allele pairing.
		 */
		public void alleleRemove(int index) {
			//String g = ""; for (int i=0; i<genome.length; i++) g+=genome[i]+",";
			//System.out.println(id + ": alleleRemove called with genome " + g);
			if (index % 2 == 1) throw new RuntimeException("Sorry, you must remove an allele pair at an even index.");
			//checkShit();
			//debug += "remove " + index + "," + genome[index] + "," + genome[index+1] + " (" + getWordFromAllele(genome[index]) + "," + getPositionFromAllele(genome[index+1]) + ") ";
			//System.out.println(id + ": " + debug);
			// decoder word contrib counts + new decoder 
			THashSet<Character> seen = new THashSet<Character>();
			String word = getWordFromAllele(genome[index]);
			if (word.equals("")) {
				genome[index+1] = 0;
				return;
			}
			
			int position = getPositionFromAllele(genome[index+1]);
			char plaintext, cipherSymbol;
			int dPos;
			int[] positions;
			for (int i=0; i<word.length(); i++) {
				plaintext = word.charAt(i);
				cipherSymbol = Zodiac.cipher[Zodiac.CIPHER].charAt(position+i);
				//if (word.equals("because")) System.out.println("because! " + cipherSymbol + "/" + plaintext);
				if (!seen.contains(cipherSymbol)) {
					seen.add(cipherSymbol);
					//if (word.equals("because")) System.out.println("haven't seen " + cipherSymbol + "/" + plaintext);
					dPos = Zodiac.alphabetHash.get(cipherSymbol);
					if (this.conflictDecoderWordCount[dPos] > 0) this.conflictDecoderWordCount[dPos]--;
					// if zero words contribute to this symbol's position in the conflictDecoderWordCount, then we can reset the decoder position.
					positions = conflictDecoderHash.get(dPos);
					if (this.conflictDecoderWordCount[dPos] == 0) {
						decoder.setCharAt(dPos, '?');
						// reset the corresponding positions in the decoded string
						if (positions != null) {
							for (int j=0; j<positions.length; j++) {
								// decoded string is updated whenever a cipherSymbol no longer has a decoding
								decoded.setCharAt(positions[j], '?');
								// update derived words string
								if (positions[j] >= CIPHER_START && positions[j] <= CIPHER_END) {
									this.conflictDerivedWords.setCharAt(positions[j]-CIPHER_START, '?');
								}
							}
						}
					} else { // special case: conflictDerivedWords needs to be updated, but some other word is contributing
						       // to a position we need to remove "_" from.
						for (int j=0; j<positions.length; j++) {
							// update derived words string.  only do this for positions that our current word covers.
							if (positions[j] >= CIPHER_START && positions[j] <= CIPHER_END && positions[j] >= position && positions[j] < position+word.length()) {
								
								this.conflictDerivedWords.setCharAt(positions[j]-CIPHER_START, plaintext);
							}
						}
					}
					
				}
			}
	
			/*
			if (decoded.substring(position).startsWith(word)) {
				throw new RuntimeException("Wtf, removed " + word + " but decoded still has it!  " + getDebugLine());
			}*/
			// hashed word/pos map
			//checkShit();
			conflictWordHash.remove(word);
			// overlap string
			this.conflictOverlap.replace(position, position+word.length(), word.replaceAll(".", "?"));
			// tuple mapping to decoder positions
			conflictWordsToDecoder.remove(word + " " + position);
			
			genome[index] = 0;
			genome[index+1] = 0;
			
			conflictWordStarts[position-CIPHER_START] = false;
			conflictWordEnds[position-CIPHER_START+word.length()-1] = false;
			
			//checkShit();
			
			//System.out.println("removed " + word + "," + position + ", overlap is back to " + conflictOverlap + ", derived is back to " + conflictDerivedWords);

			/*
			if (this.clusterCount != 666) {
				CipherWordGene shit = new CipherWordGene();
				shit.clusterCount = 666;
				int[] newG = genome.clone();
				shit.genome = new int[genome.length];
				shit.conflictInit();
				for (int i=0; i<newG.length; i+=2) {
					shit.allelePut(i, newG[i], newG[i+1]);
				}
				if (!shit.decoded.toString().equals(decoded.toString())) {
					System.err.println("wtf on remove?? ");
					System.err.println("   this: " + conflictOverlap);
					System.err.println("compare: " + shit.conflictOverlap);
					System.err.println("   this: " + decoder);
					System.err.println("compare: " + shit.decoder);
					System.err.println("   this: " + conflictDerivedWords);
					System.err.println("compare: " + shit.conflictDerivedWords);
					System.err.println(this.debug + ":" + this.toString() + ":" + getDebugLine());
					System.err.println(shit.debug + ":" + shit.toString() + ":" + shit.getDebugLine());
					//throw new RuntimeException();
				}
				}
*/
			
		}
		
	  
	  /* another min conflict test */
	  public static void testMinConflict() {
	  	CipherGene gene = new CipherGene();
	  	gene.genome = new int[50];
	  	THashSet<String> words = new THashSet<String>();
	  	int i = 0;
	  	while (i<50) {
	  		gene.genome[i] = CipherGene.rand().nextInt(wordPool.length);
	  		if (!words.contains(wordPool[gene.genome[i]])) {
	  			words.add(wordPool[gene.genome[i]]);
	  			gene.genome[i+1] = CipherGene.rand().nextInt(maxAllele);
	  			i+=2;
	  		}
	  	}
	  	
	  	boolean again = true;
	  	
	  	int bestWord = -1;
	  	int bestPos = -1;
	    double bestFitness = Float.MIN_VALUE;
	  	while (again) {
	  		again = false;
	  		for (i=0; i<gene.genome.length; i+=2) {
	  			int oldpos = gene.genome[i+1];
	  			String word = getWordFromAllele(gene.genome[i]);
	  			for (int pos=0; pos<Zodiac.cipher[Zodiac.CIPHER].length()-word.length(); pos++) {
	  				gene.genome[i+1] = pos;
	  				gene.fitness();
	  				if (gene.fitnessWord > bestFitness) {
	  					bestFitness = gene.fitnessWord;
	  					bestPos = pos;
	  					bestWord = i;
	  					again = true;
	  				}
	  			}
	  			gene.genome[i+1] = oldpos;
	  		}
	  		if (again) {
		  		gene.genome[bestWord+1] = bestPos;
		  		gene.fitness();
	  		}
	  		
		  	System.out.println(gene.getDebugLine());
	  	}
	  }
	  
  
  	public static void testGene() {
  		CipherWordGene gene = new CipherWordGene();
  		int w = wordPool.length*3;
  		
  		gene.genome = new int[wordPool2.length*2];
  		int pos = 0;
  		
  		int which = 0;
  		int find = 0;
  		THashSet<String> words = new THashSet<String>();
  		for (int i=0; i<wordPool2.length; i++) {
  			if (wordPool2[i].length() >= 5) {
  				for (int j=0; j<wordPool.length; j++) {
  					if (wordPool[j].equals(wordPool2[i])) {
  						if (!words.contains(wordPool[j])) {
  							words.add(wordPool[j]);
    						find = j;
    		  			gene.genome[which*2] = w+find;
    		  			gene.genome[which*2+1] = pos;
    	  				System.out.println(wordPool2[i] + ":" + pos + " conflict " + gene.hasConflictOBSOLETE(gene.genome));
    		  			which++;
  							
  						}
  					}
  				}
	  			//if (i>5) break;
  			}
  			pos += wordPool2[i].length();
  		}
  		
  		//gene.genome = new int[] {0+w,0, 1+w,1, 2+w,5, 3+w,12, 4+w,18, 5+w,25};
  			//new int[] {0,317, 89,359, 210,180, 383,276, 389,63, 374,353, 493,311, 364,129, 487,49, 269,345, 59,174, 327,354, 315,445, 397,83,
  			//	 128,459, 0,328, 217,411, 319,0, 386,166, 88,73, 279,158, 423,359, 367,307, 79,354, 171,44};
  		 
  		System.out.println("conflicts? " + gene.hasConflictOBSOLETE(gene.genome));
  		gene.fitness();
  		gene.fitnessNGramFreq(1);
  		gene.fitnessNGramFreq(2);
  		gene.fitnessNGramFreq(3);
  		gene.fitnessNGramFreq(4);
  		gene.fitnessNGramFreq(5);
  		gene.fitnessNGramFreq(6);
  		System.out.println(gene.getDebugLine());
  		System.out.println(gene.badNGramCount[1] + " " + gene.badNGramCount[2] + " " + gene.badNGramCount[3] + " " + gene.badNGramCount[4]);
  		
  		gene.genome = new int[] {159,229, 268,1, 112,313, 160,336, 50,118, 57,23, 208,157, 91,314, 479,230, 399,484, 105,224, 363,29, 467,310, 134,399, 42,43, 232,472, 84,217, 223,382, 318,70, 416,283, 388,225, 407,181, 147,108, 420,97, 183,125};  		
  		System.out.println("conflicts? " + gene.hasConflictOBSOLETE(gene.genome));
  		gene.fitness();
  		gene.fitnessNGramFreq(1);
  		System.out.println(gene.getDebugLine());
  		System.out.println(gene.badNGramCount[1] + " " + gene.badNGramCount[2] + " " + gene.badNGramCount[3] + " " + gene.badNGramCount[4]);
  		
  		gene.resetDecoder();
  		gene.fitness();
  		gene.fitnessNGramFreq(1);
  		System.out.println(gene.getDebugLine());
  		System.out.println(gene.badNGramCount[1] + " " + gene.badNGramCount[2] + " " + gene.badNGramCount[3] + " " + gene.badNGramCount[4]);
  		
  		gene.genome = new int[] { // a tightly packed word set, but bad zodiac match.
  				5,284, 0,155, 10,466, 16,494, 24,486, 2,246, 9,373, 18,175, 13,171, 3,327, 0,302, 372,278, 24,242, 90,297, 353,141, 11,259, 10,280, 30,476, 13,145, 76,56, 73,77, 260,326, 23,181, 182,156, 47,452  				
  		};
  		gene.fitness();
  		gene.fitnessNGramFreq(1);
  		gene.fitnessNGramFreq(2);
  		gene.fitnessNGramFreq(3);
  		gene.fitnessNGramFreq(4);
  		gene.fitnessNGramFreq(5);
  		gene.fitnessNGramFreq(6);
  		System.out.println(gene.getDebugLine());
  		System.out.println(gene.badNGramCount[1] + " " + gene.badNGramCount[2] + " " + gene.badNGramCount[3] + " " + gene.badNGramCount[4]);

  		gene.genome = new int[] { // a tightly packed word set, but bad zodiac match.
  				//479,104, 22,376, 19,30, 0,30, 1,67, 23,490, 301,145, 0,120, 14,188, 2,93, 12,460, 15,290, 20,107, 24,140, 1,338, 156,368, 1,259, 24,405, 6,126, 73,79, 18,241, 13,473, 141,157, 2,470, 7,140,
  				107,369, 0,132, 0,109, 42,328, 0,30, 157,319, 0,152, 0,478, 0,284, 16,0, 172,100, 166,73, 144,144, 0,0, 0,0, 0,0, 0,0, 265,461, 0,0, 0,460, 0,79, 0,0, 301,140, 0,66, 73,259
  		};
  		gene.fitness();
  		gene.fitnessNGramFreq(1);
  		gene.fitnessNGramFreq(2);
  		gene.fitnessNGramFreq(3);
  		System.out.println(gene.getDebugLine());
  		
  		
  		//initAllPools();
  		/*gene.decoder = "??????????????u???i??c?ml????y";
  		System.out.println(gene.decode() + "," + 
  				fitnessSubstring(true, gene.decode(), substringDictPool, ZodiacDictionary.commonWords5000));
  		 */
  		
  		
  		              //ABDEFGHIJKLMNOPQRSTUVWXYZ56789cdefklpqrtz!#$%()*+/@\^_
  									//wlnesattfsthenifgaoibeouetesaivocdiaemrrdolxlhnpeksrny
  		gene.decoder = new StringBuffer("?t?????t??????i?e????????????i???????e??????t?????????");
  		//gene.decoder = "???????????????????????u?????p??c????m????l??????????y";
  		/*System.out.println( 
  				fitnessSubstringSLOW(1, true, gene.decode(), substringZodSmegDictPool, ZodiacDictionary.zodiacTopWordsSMEG, gene.decoder.toString()) +
  				"," + gene.decode());*/
  		
  		System.out.println(Zodiac.cipher[1]);
  	
  	}

  	
  	/** init the word pool map of words to alleles */
  	public static void initWordPoolMap() {
  		wordPoolMap = new THashMap<String, Integer>();
  		int w = 0;
  		while (w <= maxAllele*wildCardProbability) w += wordPool.length;
  		for (int i=0; i<wordPool.length; i++) wordPoolMap.put(wordPool[i], w + i);
  	}
  	
  	/** get an allele representation for the given word */
  	public static int getAlleleFromWord(String w) {
  		if (wordPoolMap == null) initWordPoolMap();
  		Integer result = wordPoolMap.get(w);
  		if (result == null) return -1;
  		return result;
  		
  		/*for (int i=0; i<wordPool.length; i++) if (wordPool[i].equals(w)) return i;
  		return -1;*/
  	}
  	public static void testGene2() {
  		int w = wordPool.length*3;
  		//CipherWordGene gene = new CipherWordGene();
  		//"become","my","slaves","i","will","not","give","you","my","name","because","you","will","the", "i", "to", "of", "a", "in", "you", "and", "my", "is", "was", "have", "this", "it", "that", "with", "me", "for", "be", "will", "on", "not", "them", "by", "all", "who", "her", "they", "shall", "if", "but", "am", "about", "up", "she", "do", "your", "out", "one", "when", "then", "zodiac", "people", "like", "would", "some", "as", "are", "so", "police", "missed", "did", "car", "back", "speaking", "there", "or", "no", "last", "because", "at", "were", "time", "thing", "more", "from", "can", "never", "killing", "just", "down", "bomb", "way", "very", "vallejo", "think", "their", "slaves", "see", "s", "rather", "over", "off", "now", "nice", "list", "im", "had", "get", "editor", "could", "buttons", "been", "what", "tell", "school", "san", "park", "others", "only"
  		
  		runTest(new int[] {
  				7+w,28,10+w,37,5+w,21,2+w,10,4+w,17,9+w,33,3+w,16,0+w,2,6+w,24
  		});

  		runTest(new int[] {
  				getAlleleFromWord("over")+w,33,
  				getAlleleFromWord("on")+w,16,
  				getAlleleFromWord("not")+w,47,
  				getAlleleFromWord("s")+w,19,
  				getAlleleFromWord("there")+w,4,
  				getAlleleFromWord("as")+w,0,
  				getAlleleFromWord("are")+w,43,
  				getAlleleFromWord("rather")+w,10,
  				getAlleleFromWord("then")+w,20,
  				getAlleleFromWord("or")+w,2,
  				getAlleleFromWord("out")+w,37,
  				getAlleleFromWord("a")+w,50,
  				getAlleleFromWord("her")+w,30,
  				getAlleleFromWord("others")+w,24,
  				getAlleleFromWord("who")+w,40
  		});
  		
  		runTest(new int[] {
  				72,191,  51,69,  0,66,  13,78,  79,238,  16,73,  64,276,  0,195,  61,404,  116,351,  0,379,  335,311,  134,126,  92,221,  0,126,  122,307,  0,0,  48,15,  0,0,  120,61,  2,0,  111,254,  52,332,  0,135,  130,186	
  		});
  		
  		runTest(new int[] {
  				93,50, 28,228, 137,275, 0,194, 12,292, 6,241, 20,69, 91,272, 181,263, 127,298, 39,156, 15,415, 10,335, 6,68, 18,76, 118,80, 11,64, 35,241, 6,182, 10,70, 1,168, 24,303, 0,0, 73,85, 11,33
  		});
  		
  		runTest(new int[] {
  				93,50, 28,228, 10,275, 6,311, 12,19, 6,347, 100,173, 91,272, 181,212, 127,298, 39,156, 20,460, 0,422, 135,335, 10,59, 22,252, 126,64, 337,240, 11,156, 0,221, 1,312, 19,226, 23,105, 2,221, 34,33
  		});
  		System.out.println("experiment 195");
  		
  		runTest(new int[] {
  				90,367, 0,300, 44,187, 116,76, 113,53, 0,311, 0,0, 0,390, 0,373, 120,232, 0,0, 0,0, 0,32, 123,88, 0,241, 0,75, 0,0, 0,0, 0,0, 145,119, 118,21, 0,0, 0,0, 0,0, 0,33
  		});

  		runTest(new int[] {
  				449,398, 0,0, 0,0, 0,0, 0,0, 0,0, 443,238, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 168,353, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 102,175, 0,0, 0,0
  		});
  		
  		runTest(new int[] {
  	  		93,50, 28,228, 137,275, 17,36, 0,154, 18,5, 17,69, 91,272, 181,212, 127,298, 39,156, 10,415, 3,107, 135,335, 17,434, 8,80, 126,64, 337,240, 1,312, 7,7, 3,484, 4,182, 0,123, 9,378, 73,340
  		});
  		
  		runTest(new int[] {
  				117,68, 0,388, 0,165, 0,0, 0,34, 0,214, 0,0, 0,419, 113,206, 90,469, 72,292, 3,131, 0,186, 0,0, 124,385, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 122,339, 0,0, 0,0, 0,0
  		});
  		
  		runTest(new int[] {
  				96,276, 20,467, 2,343, 9,115, 0,373, 12,21, 0,386, 130,182, 113,206, 90,469, 0,293, 14,347, 298,139, 4,294, 0,254, 10,242, 0,427, 98,305, 0,166, 3,0, 1,0, 122,339, 107,119, 18,49, 0,374
  		});
  		
  		runTest(new int[] {
  	  		58,175, 104,303, 63,104//, 0,0, 0,0, 4,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 2,385, 0,0, 0,339, 17,0, 123,301, 25,0, 0,0 
  		});
  		runTest(new int[] {
  				80,125, 23,75, 34,353, 412,163, 20,39, 53,0, 12,266, 407,50, 9,182, 0,485, 17,380, 13,234, 20,330, 491,238, 126,20, 18,448, 0,285, 3,366, 12,453, 17,185, 0,342, 3,319, 0,160, 0,82, 93,242
  		});
  		
  		runTest(new int[] {
  				66,21, 0,391, 0,0, 0,0, 279,0, 0,0, 0,370, 9,192, 0,0, 0,240, 98,251, 0,0, 133,25, 0,0, 190,135, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 33,162, 0,0, 0,267, 364,244
  		});
  		
  		runTest(new int[] {
  				122,123, 12,472, 20,349, 20,101, 4,0, 1,450, 92,107, 12,84, 0,375, 25,0, 0,0, 8,202, 18,0, 0,288, 499,135, 16,369, 0,0, 68,113, 0,63, 2,267, 104,303, 70,366, 0,447, 0,386, 93,38
  		});
  		
  		runTest(new int[] {
  				105,288, 239,0, 8,480, 139,316, 17,241, 273,233, 333,152, 299,175, 92,327, 14,404, 9,225, 33,228, 2,358, 22,207, 0,53, 491,245, 79,89, 0,160, 9,239, 262,99, 0,0, 75,165, 116,119, 0,204, 60,310
  		});
  		runTest(new int[] {
  	  		461,340, 0,0, 0,0, 119,320, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 407,111, 0,0, 0,0, 0,0, 0,0, 111,325, 0,0, 288,38
  		});
  		
  		runTest(new int[] {
  				93,326, 23,326, 324,386, 0,308, 0,0, 0,0, 0,0, 68,316, 0,0, 0,0, 0,0, 306,372, 0,0, 0,0, 81,292, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 82,288, 0,0
  		});
  		
  		runTest(new int[] {
  				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 450, 101
  		});
  		
  		
  		/*
  		String[] lines = ZodiacDictionary.getContents(new java.io.File("/Users/doranchak/projects/work/java/zodiac/experiment195-genomes"), false).split(System.getProperty("line.separator"));
  		
  		int[] g = new int[50];
  		for (int i=0; i<lines.length; i++) {
  			String[] split = lines[i].split(",");
  			for (int j=0; j<split.length; j++) {
  				g[j] = Integer.valueOf(split[j]);
  			}
  			runTest(g);
  		}*/
  		
  		//String test = "????er????????????ees??r???????r??s???re????????????ee??r????eree?r???re?????hi?e????e???irr?????i?????her????????????????????re????e?????re?e???is?????r??r??e??ere??is???e??h?????se??rrr???r????h??r?e?si??????e??his?????r?i???h????????ee?s????????????h?e?????sr?re???r??????e?????eir?s???ee???????r??????e?i??????s??rs?????r???e????r???r?e";
  		//System.out.println("fuzzy " + fitnessSubstring(1, true, test, substringPool, wordPool, "?s???i????r?r?r??????????????h????er??????????????e????????????"));
  	}
  	
  	public static void testScaled() {
  		String[] lines = ZodiacDictionary.getContents(new java.io.File("/Users/doranchak/projects/work/java/zodiac/feh2"), false).split(System.getProperty("line.separator"));
  		String [] fits;
  		String line;
  		int val;
  		for (int i=0; i<lines.length; i++) {
  			fits = lines[i].split("\\|");
  			line = "";
  			for (int j=0; j<fits.length; j++) {
    			val = (int) Float.valueOf(fits[j]).floatValue();
    			val /= ZodiacWordProblem.DISCRETIZATION_FACTOR;
    			line += val + " ";
  			}
  			System.out.println(line);
  		}
  	}
  	
  	
  	public static void runTest(int[] g) {
  		CipherWordGene gene = new CipherWordGene();
  		gene.genome = g;
  		gene.hasConflictOBSOLETE(gene.genome);
  		
  		/*
  		String key = ""; String k;
			for (int i=0; i<gene.genome.length; i+=2) {
				k = CipherWordGene.makeKeyFor(gene.genome[i],gene.genome[i+1], 4);
				if (k != null)
					key += k + ", ";
			}

  		System.out.println("key " + key);
  		*/
  		//gene.fitness();
  		//gene.fitnessNGramFreq();
  		//System.out.println(gene.getDebugLine());
  	}
  	
  	public static void runTestSpeed(int[] g) {
  		CipherWordGene gene = new CipherWordGene();
  		gene.genome = g;
  		gene.hasConflictOBSOLETE(gene.genome);
  		gene.fitness();
  	}
  	
  	public static void testWI() {
  		initAllPools();
  		String[] wild = new String[2];
  		for (int i=0; i<wild.length; i++) wild[i] = "";
  		//List<String> subs = getWildPermutations(wild, 3, "collecting");
  		//for (int i=0; i<subs.size(); i++) System.out.println(subs.get(i));
  		
  		//String test = "ou??h?aiuobe?s??ite?hknosom?gr?tnhtinmaro?tl??lnf?ee??odi?because?tsomething?something?tkforrest????e?e??rns?bitkilledien??dig?tsmtameeo?hrtsrett???etgodi?fe???s???hsamooh?sbetr???ee?mesmrofe?lng?nh?g?ilc??tiea?uee?esrm?kgr?io?o?et??ceh???asocodiet??el?b?m?eg?sahs?f?kuurt?oee?s?ouuhsc?diesh?ttt?odi??gocoiubl?ea???nnt?mettehhknrs?lncoude??m?oi?o??cfn??e??t?hnddsh??be?louk?hgil?t?iir?gi?ltses?gotrstsmte?aro";
  		//String test = "ilikekillingpeoplebecauseitiss?muchfuniti?morefunt?ankilling?ildgameintheforrestbecausemanist?emo?tdangertueanamalofalltokillsomethinggivesmet?em??tthrillingexperenceitisevenbettert?angetting?ourrocksoff?ithagirlt?ebestpart?fiti?thae??enidiei?illbereborninparadice?ndallthei?avekilled?illbecomem?slavesi?illnotgive?oum?namebecause?ou?illtr?t?sloido?nor?t?pm?collectingofslavesfoem?afterlifeebeorietemeth?piti";
  		//String test = "i?ik?k?l??ng????l????????i?i?????????????????????????killing????g?????????????????????????i???????????g??????n???l???ll??kill????????gg????????????????illi?g????????????????n??????????g???i?g???????k?????i???g??????????????????i?????????i?????ill???????ni???????????????????????ki?????ill?????????l????i?il?n??g???????????????????????i?l??????l?i??????????????ll???ing??????????????????li???????i????????????";
  		//String test = "i?i????l?a?g????l?????e??i?i???ge?h?e??dangertuen?????illi?g????g?g?a?th???r????????e??g?ni?d??g?n????gtrde????g?leu?llt??ill??g??h??gga???g?d??g?n?thrilling?????t?????a???????d??rt???g??ding?eer???????u?i?h?g?r?t?t??????rd??a?inth?t????i???a?ill?????er?i???r?????nn????dhta?????i?????ill????g?g??l????i?il??e?g?????eg???g?????e???ee?i?ltr????l?i???n??nt??g???ll???i?g?u???????e?g???d?rliu?????ri?d?g??h???da";
  		//String test = "ih??g??gh???e?wegintfiisrik?t?s?iftoi??h?o?nt?ai?m????ing?????h??g?n??etgo?he?sknifeitt?i?i?h?r?wom?????thing?e?igna?nge???ngtw?nkt?????ng???h?i?something???there??fn?k?sgn??nihmtte?g??rkhi???nihe?f?twoa??mte??the??nn?keihhso?mioetg???g???????ingnietnnt???e?hg??fro??ihhht????nn?ihhg???ngn?fw?i??snentti?igh?nm??nt??i?????ingfii?n?ni?ihnet?kssgwi?w???eoese??f?nnrfm???washingtont??eohthg?airnnwhiihr?nkt?e?h?";
  		//String test = "il?ldl?gl?ns????g????lti?i??????t???t??e????mokt??killing?ns??l?si?h????d?????i????gt???l?i?ek??????ilsomething?lg?king??l?ng???h????ss?ed???ek????????ing??s?????ol?h???ide?n??e??m?ki?s??ei?s??t????l???k????gs?ml?ko?h???l?e????i???io?kdl??????ing??????mn???i?i???????llle?o?kiehlilld???ng????????inge??i?igln??s?e???t???i???d?lt?h??t?iln?m???ig?i??????????????nn????ns?killed??????g?e??g?k???h??i?e??h??k??e?";
  		//String test = "ilikekillingpeoplebecauseitis??muchfuniti?m?r?fu?t?a?killing?ildgamein?hefor?estbecausema?i?t?emo?t?a?g?rtueanamal?fall?okillsomethinggive?met?em??t?hrilli?ge?pe???ceitisevenbetter??angetti?g??ur?ocksoff?ithagirl???be?tpart?fiti??ha???e?i?iei?illbe?eb?rninparadice???allth?i?avekilled?illbecomem?slavesi?illn?tgive?oum?namebecau?e??u?ill?r?t?sloi?o??o????pm?collectingofslavesf?em?afterlifeebeorietemeth?piti";
  		//String test = "?tl?r??et?i?e?see?d?etrec??l???ere??r??a?le?pubr?a?es??leli?i?tk?dea????r???a?e?d?ecr??et???a?ceslaves?uparadicete?bele???lle?sea???????er?e?a??e?la????lel???becausea???ere?id?aa?p??d??c?a?????r?a?e??s?bila?c??pt??uda??et?a???a?l??dui?rslv???i?led?a?d?pil?ee?dk?ecl?vttta?u??eea??ttrkilled?ese?e?elce???i?eti?a??e???re??ee?dretr?a??ri?tl?p???ees?vsi??al??ee?e?llceali?sbetter????e?c?a??elb?cdas???acea???e?a?";
  		//String test = "l???c?m?my?l????i?i?????????y?umy??m???c?u??y?u?i?l";
  		//String test = "i???????????????????c?u??i?????muc??u?????m????u??????il??????????m???????????????c?u??m??i????m??????????u????m?????l?????l???m???????????m????m??????il???????????c???????????????????????i??y?u???c?????????????????????????????i???????????????il?????????????????c????????????????i??????l???c?m?my?l????i?i?????????y?umy??m???c?u??y?u?i?l??y?????i??????????myc?ll?c???????????????my??????????????i???m????????";
  		String test = "il?????ll??g????l????????i?????m???????e??m???????????i?l??g??l?g?m????????????????????m??i?e??m??????g??e?????m?l????l?????l??m?????gg????m?e??m??????i?l??g???????????????????e???????g??ei?g?????????????????g??l??????????e????i???????????????i?l??????????????????????lle????????ill?????l????m?m???????i?ill???g??????m???m????????????il???????l?i??????????m??????????g???l???????m???e??l????????i?e?m??????e?";
  		
  		List<Object[]> list = substringMatchesSLOW(1, test, substringZodSmegDictPool, null, ZodiacDictionary.zodiacTopWordsSMEG);
  		System.out.println("list size " + list.size());
  		for (int i=0; i<list.size(); i++) {
  			String word = (String) list.get(i)[1];
  			Integer pos = (Integer) list.get(i)[2];
  			//Iterator it = words.keySet().iterator();
  			String chunk = (String) list.get(i)[0];
				System.out.println("chunk " + chunk + " pos " + pos + " word " + word);
  		}
  		//System.out.println("fitnessSubstring(false) " + fitnessSubstring(1, false, test, substringDictPool, ZodiacDictionary.commonWords5000) + ", fitnessSubstring(true) " + fitnessSubstring(1, true, test, substringDictPool, ZodiacDictionary.commonWords5000));
  	}
  	
  	/* brute force attack using the top 6 cipher characters, using fuzzy match as a metric */
  	public static void testBruteSix() {
  		initAllPools();
  		CipherWordGene gene = new CipherWordGene();
  	}
  	
  	public static void test51() {
  		CipherWordGene gene = new CipherWordGene();
  		//"become","my","slaves","i","will","not","give","you","my","name","because","you","will"
  		gene.decoder = new StringBuffer(Zodiac.getDecoderForWordAtPosition(gene.decoder.toString(), "llbecomemyslavesiwillnotgiveyoumynamebecauseyouwill", 286, true));
  		gene.fitnessNGramFreq();
  		gene.fitnessDictionaryWords(1, 10, true, true, true, null);
  		System.out.println(gene.getDebugLine());
  		
  		gene = new CipherWordGene();
  		gene.decoder = new StringBuffer(Zodiac.getDecoderForWordAtPosition(gene.decoder.toString(), "asorthererratheronosthenothersheroveroutwhoarehnota", 286, true));
  		System.out.println(gene.getDebugLine());
  		gene.fitnessNGramFreq();
  		gene.fitnessDictionaryWords(1, 10, true, true, true, null);
  		System.out.println(gene.getDebugLine());
  		
  		
  	}
  	
  	public static boolean[] randBool() {
  		Random r = new Random();
  		boolean[] result = new boolean[1000];
  		for (int i=0; i<1000; i++) { 
  			//result[i] = CipherGene.rand().nextBoolean();
  			result[i] = r.nextBoolean();
  		}
  		return result; 
  	}
  	public static void testHamming() {
  		boolean[] b1;
  		boolean[] b2;
  		
  		int total = 0;
  		int hits = 0;
  		int hamming;
  		int mean = 0;
  		while (true) {
  			hamming = 0;
  			b1 = randBool();
  			b2 = randBool();
  			for (int i=0; i<b1.length; i++) {
  				if (b1[i] != b2[i]) hamming++;
  			}
  			if (hamming >= 495 && hamming <= 505)
  				hits++;
  			
  			
  			total++;
  			mean = (mean*(total-1) + hamming) / total;
  			
  			if (total % 10000 == 0) System.out.println("hits " + hits + " total " + total + " mean " + mean + " p = " + ((float) hits/total));
  		}
  	}
  	
  	public static void computeAverages() {
  		String[] lines = ZodiacDictionary.getContents(new java.io.File("/Users/doranchak/projects/work/java/zodiac/feh3"), false).split(System.getProperty("line.separator"));
  		String [] fits;
  		String line;
  		int val;
  		
  		float sum;
  		float product;
  		for (int i=0; i<lines.length; i++) {
  			fits = lines[i].split("\\|");
  			sum = 0; product = 1;
  			for (int j=0; j<3; j++) {
  				sum += Float.valueOf(fits[j]).floatValue();
  				product *= Float.valueOf(fits[j]).floatValue();
  			}
  			sum /= 3;
  			System.out.println(sum + "," + product + "," + fits[3]);
  			
  		}
  		
  	}
  	/*
  	public static void testKeySorted() {
  		int[] genome = new int[] {  				72,191,  51,69,  0,66,  13,78,  79,238,  16,73,  64,276,  0,195,  61,404,  116,351,  0,379,  335,311,  134,126,  92,221,  0,126,  122,307,  0,0,  48,15,  0,0,  120,61,  2,0,  111,254,  52,332,  0,135,  130,186	};
  		System.out.println(makeSortedKeyFor(genome, 4));
  		
  		genome = new int[] {93,50, 28,228, 137,275, 0,194, 12,292, 6,241, 20,69, 91,272, 181,263, 127,298, 39,156, 15,415, 10,335, 6,68, 18,76, 118,80, 11,64, 35,241, 6,182, 10,70, 1,168, 24,303, 0,0, 73,85, 11,33};
  		System.out.println(makeSortedKeyFor(genome, 4));
  		genome = new int[] {93,50, 28,228, 10,275, 6,311, 12,19, 6,347, 100,173, 91,272, 181,212, 127,298, 39,156, 20,460, 0,422, 135,335, 10,59, 22,252, 126,64, 337,240, 11,156, 0,221, 1,312, 19,226, 23,105, 2,221, 34,33};
  		System.out.println(makeSortedKeyFor(genome, 4));
  		genome = new int[] {90,367, 0,300, 44,187, 116,76, 113,53, 0,311, 0,0, 0,390, 0,373, 120,232, 0,0, 0,0, 0,32, 123,88, 0,241, 0,75, 0,0, 0,0, 0,0, 145,119, 118,21, 0,0, 0,0, 0,0, 0,33};
  		System.out.println(makeSortedKeyFor(genome, 4));
  		genome = new int[] {449,398, 0,0, 0,0, 0,0, 0,0, 0,0, 443,238, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 168,353, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 102,175, 0,0, 0,0};
  		System.out.println(makeSortedKeyFor(genome, 4));
  		genome = new int[] {93,50, 28,228, 137,275, 17,36, 0,154, 18,5, 17,69, 91,272, 181,212, 127,298, 39,156, 10,415, 3,107, 135,335, 17,434, 8,80, 126,64, 337,240, 1,312, 7,7, 3,484, 4,182, 0,123, 9,378, 73,340};
  		System.out.println(makeSortedKeyFor(genome, 4));
  		genome = new int[] {117,68, 0,388, 0,165, 0,0, 0,34, 0,214, 0,0, 0,419, 113,206, 90,469, 72,292, 3,131, 0,186, 0,0, 124,385, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 122,339, 0,0, 0,0, 0,0};
  		System.out.println(makeSortedKeyFor(genome, 4));
  		genome = new int[] {96,276, 20,467, 2,343, 9,115, 0,373, 12,21, 0,386, 130,182, 113,206, 90,469, 0,293, 14,347, 298,139, 4,294, 0,254, 10,242, 0,427, 98,305, 0,166, 3,0, 1,0, 122,339, 107,119, 18,49, 0,374};
  		System.out.println(makeSortedKeyFor(genome, 4));
  		genome = new int[] {58,175, 104,303, 63,104}; 
  		System.out.println(makeSortedKeyFor(genome, 4));

  	}*/
  	
  	public static void testGeneSpeed() {
  		
  		
  		Date start = new Date();
  		for (int i=0; i<100; i++) {
  			
  		int w = wordPool.length*3;
  		runTestSpeed(new int[] {
  				getAlleleFromWord("over")+w,33,
  				getAlleleFromWord("on")+w,16,
  				getAlleleFromWord("not")+w,47,
  				getAlleleFromWord("s")+w,19,
  				getAlleleFromWord("there")+w,4,
  				getAlleleFromWord("as")+w,0,
  				getAlleleFromWord("are")+w,43,
  				getAlleleFromWord("rather")+w,10,
  				getAlleleFromWord("then")+w,20,
  				getAlleleFromWord("or")+w,2,
  				getAlleleFromWord("out")+w,37,
  				getAlleleFromWord("a")+w,50,
  				getAlleleFromWord("her")+w,30,
  				getAlleleFromWord("others")+w,24,
  				getAlleleFromWord("who")+w,40
  		});
  		
  		runTestSpeed(new int[] {
  				72,191,  51,69,  0,66,  13,78,  79,238,  16,73,  64,276,  0,195,  61,404,  116,351,  0,379,  335,311,  134,126,  92,221,  0,126,  122,307,  0,0,  48,15,  0,0,  120,61,  2,0,  111,254,  52,332,  0,135,  130,186	
  		});
  		
  		runTestSpeed(new int[] {
  				93,50, 28,228, 137,275, 0,194, 12,292, 6,241, 20,69, 91,272, 181,263, 127,298, 39,156, 15,415, 10,335, 6,68, 18,76, 118,80, 11,64, 35,241, 6,182, 10,70, 1,168, 24,303, 0,0, 73,85, 11,33
  		});
  		
  		runTestSpeed(new int[] {
  				93,50, 28,228, 10,275, 6,311, 12,19, 6,347, 100,173, 91,272, 181,212, 127,298, 39,156, 20,460, 0,422, 135,335, 10,59, 22,252, 126,64, 337,240, 11,156, 0,221, 1,312, 19,226, 23,105, 2,221, 34,33
  		});
  		
  		runTestSpeed(new int[] {
  				90,367, 0,300, 44,187, 116,76, 113,53, 0,311, 0,0, 0,390, 0,373, 120,232, 0,0, 0,0, 0,32, 123,88, 0,241, 0,75, 0,0, 0,0, 0,0, 145,119, 118,21, 0,0, 0,0, 0,0, 0,33
  		});

  		runTestSpeed(new int[] {
  				449,398, 0,0, 0,0, 0,0, 0,0, 0,0, 443,238, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 168,353, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 102,175, 0,0, 0,0
  		});
  		
  		runTestSpeed(new int[] {
  	  		93,50, 28,228, 137,275, 17,36, 0,154, 18,5, 17,69, 91,272, 181,212, 127,298, 39,156, 10,415, 3,107, 135,335, 17,434, 8,80, 126,64, 337,240, 1,312, 7,7, 3,484, 4,182, 0,123, 9,378, 73,340
  		});
  		
  		runTestSpeed(new int[] {
  				117,68, 0,388, 0,165, 0,0, 0,34, 0,214, 0,0, 0,419, 113,206, 90,469, 72,292, 3,131, 0,186, 0,0, 124,385, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 122,339, 0,0, 0,0, 0,0
  		});
  		
  		runTestSpeed(new int[] {
  				96,276, 20,467, 2,343, 9,115, 0,373, 12,21, 0,386, 130,182, 113,206, 90,469, 0,293, 14,347, 298,139, 4,294, 0,254, 10,242, 0,427, 98,305, 0,166, 3,0, 1,0, 122,339, 107,119, 18,49, 0,374
  		});
  		
  		runTestSpeed(new int[] {
  	  		58,175, 104,303, 63,104//, 0,0, 0,0, 4,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 2,385, 0,0, 0,339, 17,0, 123,301, 25,0, 0,0 
  		});
  		runTestSpeed(new int[] {
  				80,125, 23,75, 34,353, 412,163, 20,39, 53,0, 12,266, 407,50, 9,182, 0,485, 17,380, 13,234, 20,330, 491,238, 126,20, 18,448, 0,285, 3,366, 12,453, 17,185, 0,342, 3,319, 0,160, 0,82, 93,242
  		});
  		
  		runTestSpeed(new int[] {
  				66,21, 0,391, 0,0, 0,0, 279,0, 0,0, 0,370, 9,192, 0,0, 0,240, 98,251, 0,0, 133,25, 0,0, 190,135, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 33,162, 0,0, 0,267, 364,244
  		});
  		
  		runTestSpeed(new int[] {
  				122,123, 12,472, 20,349, 20,101, 4,0, 1,450, 92,107, 12,84, 0,375, 25,0, 0,0, 8,202, 18,0, 0,288, 499,135, 16,369, 0,0, 68,113, 0,63, 2,267, 104,303, 70,366, 0,447, 0,386, 93,38
  		});
  		
  		runTestSpeed(new int[] {
  				105,288, 239,0, 8,480, 139,316, 17,241, 273,233, 333,152, 299,175, 92,327, 14,404, 9,225, 33,228, 2,358, 22,207, 0,53, 491,245, 79,89, 0,160, 9,239, 262,99, 0,0, 75,165, 116,119, 0,204, 60,310
  		});
  		runTestSpeed(new int[] {
  	  		461,340, 0,0, 0,0, 119,320, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 407,111, 0,0, 0,0, 0,0, 0,0, 111,325, 0,0, 288,38
  		});
  		
  		runTestSpeed(new int[] {
  				93,326, 23,326, 324,386, 0,308, 0,0, 0,0, 0,0, 68,316, 0,0, 0,0, 0,0, 306,372, 0,0, 0,0, 81,292, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 82,288, 0,0
  		});
  		}
  		
  		Date end = new Date();
  		
  		long num = 1000*1600/(end.getTime()-start.getTime());
  		System.out.println(num + " genes per second.");
  		System.out.println("10000 genes would take " + (10000/num) + " seconds.");
  		
  	}
  	
  	
  	
  	/** ensure deep cloning to avoid cross-references when individuals are cloned during breeding */
		public Object clone() {
			CipherWordGene newGene = (CipherWordGene) super.clone();
			
			if (conflictDerivedWords == null)
				newGene.conflictDerivedWords = null;
			else
				newGene.conflictDerivedWords = new StringBuffer(conflictDerivedWords);
			
			if (decoder == null)
				newGene.decoder = null;
			else
				newGene.decoder = new StringBuffer(decoder);

			if (decoded == null)
				newGene.decoded = null;
			else
				newGene.decoded = new StringBuffer(decoded);

			if (conflictWordHash == null)
				newGene.conflictWordHash = null;
			else
				newGene.conflictWordHash = conflictWordHash.clone();

			if (conflictDecoderWordCount == null)
				newGene.conflictDecoderWordCount = null;
			else
				newGene.conflictDecoderWordCount = conflictDecoderWordCount.clone();

			if (conflictWordsToDecoder == null)
				newGene.conflictWordsToDecoder = null;
			else
				newGene.conflictWordsToDecoder = conflictWordsToDecoder.clone();

			if (conflictOverlap == null)
				newGene.conflictOverlap = null;
			else
				newGene.conflictOverlap = new StringBuffer(conflictOverlap);
			
			if (conflictWordStarts == null)
				newGene.conflictWordStarts = null;
			else
				newGene.conflictWordStarts = conflictWordStarts.clone();
			
			if (conflictWordEnds == null)
				newGene.conflictWordEnds = null;
			else
				newGene.conflictWordEnds = conflictWordEnds.clone();
			
			/*return System.identityHashCode(conflictDerivedWords) + "/" + 
			System.identityHashCode(conflictWordHash) + "/" + 
			System.identityHashCode(conflictDecoderWordCount) + "/" + 
			System.identityHashCode(conflictWordsToDecoder) + "/" + 
			System.identityHashCode(decoder) + "/" + 
			System.identityHashCode(decoded);*/
			return newGene;
		}
		
		/** copy the given gene by deep cloning it and copying its values into the current gene */
		public void copyIntoSelfFrom(CipherWordGene gene) {
			CipherWordGene clone = (CipherWordGene)gene.clone();
			this.genome = clone.genome;
			
			this.conflictDerivedWords = clone.conflictDerivedWords;
			this.conflictWordHash = clone.conflictWordHash;
			this.conflictDecoderWordCount = clone.conflictDecoderWordCount;
			this.conflictWordsToDecoder = clone.conflictWordsToDecoder;
			this.sortedKey = clone.sortedKey;
			this.elite = clone.elite;
			this.conflictOverlap = clone.conflictOverlap;
			this.fitnessAlpha = clone.fitnessAlpha;
			this.fitnessAlphaFreq = clone.fitnessAlphaFreq;
			this.fitnesscommonWordsUnique = clone.fitnesscommonWordsUnique;
			this.fitnessNGrams = clone.fitnessNGrams;
			this.badNGramCount = clone.badNGramCount;
			this.foundDigraphs = clone.foundDigraphs;
			this.foundDoubles = clone.foundDoubles;
			this.foundTrigraphs = clone.foundTrigraphs;
			this.fitnessWord = clone.fitnessWord;
			this.corpusGenome = clone.corpusGenome;
			this.corpusUnclobbered = clone.corpusUnclobbered;
			this.fitnessDictionaryWords = clone.fitnessDictionaryWords;
			this.fitnessDictionaryWordsInteresting = clone.fitnessDictionaryWordsInteresting;
			this.fitnessZodiacWords = clone.fitnessZodiacWords;
			this.fitnessZodiacWordsFuzzy = clone.fitnessZodiacWordsFuzzy;
			this.fitnessZodiacWordsPairings = clone.fitnessZodiacWordsPairings;
			this.fitnessWordLengthDistribution = clone.fitnessWordLengthDistribution;
			this.phiObserved = clone.phiObserved;
			this.phiPlaintext = clone.phiPlaintext;
			this.phiRandom = clone.phiRandom;
			this.phiRandomMatch = clone.phiRandomMatch;
			this.phiPlaintextMatch = clone.phiPlaintextMatch;
			this.fitnessCoverageZodiac = clone.fitnessCoverageZodiac;
			this.fitnessCoverageDictionary = clone.fitnessCoverageDictionary;
			this.fitnessCoverageDictionaryScaled = clone.fitnessCoverageDictionaryScaled;
			this.coverageDictionary = clone.coverageDictionary;
			this.coverageDictionaryScaled = clone.coverageDictionaryScaled;
			this.sentence = clone.sentence;
			this.decoder = clone.decoder;
			this.decoded = clone.decoded;
			this.decoders = clone.decoders;
			this.plugger = clone.plugger;
			this.encoding = clone.encoding;
			this.unclobberedWords = clone.unclobberedWords;
			this.totalConflicts = clone.totalConflicts;
			this.totalWildcardConflicts = clone.totalWildcardConflicts;
			this.words = clone.words;
			this.wordIndex = clone.wordIndex;
			this.ourLetterFreqs = clone.ourLetterFreqs;
			this.ourDigraphFreqs = clone.ourDigraphFreqs;
			this.foundWords = clone.foundWords;
			this.foundWordsDictionary = clone.foundWordsDictionary;
			this.foundWordsDictionaryInteresting = clone.foundWordsDictionaryInteresting;
			this.foundWordsZodiac = clone.foundWordsZodiac;
			this.foundWordsZodiacPairings = clone.foundWordsZodiacPairings;
			this.zodiacScore = clone.zodiacScore;
			this.clusterCount = clone.clusterCount;
			this.zodiacMatch = clone.zodiacMatch;
			this.zodiacWords = clone.zodiacWords;
			this.zodiacWordsOther = clone.zodiacWordsOther;
			this.zodiacWordsFuzzy = clone.zodiacWordsFuzzy;
			this.conflictWordStarts = clone.conflictWordStarts;
			this.conflictWordEnds = clone.conflictWordEnds;
			this.makeCount = clone.makeCount;
		}


		public static void findRepeats() {
  		String alphabet = Zodiac.alphabet[Zodiac.CIPHER];
  		String cipher = Zodiac.cipher[Zodiac.CIPHER];
  		
  		if (Zodiac.CIPHER == 1) {
  			decoderHash = new THashMap<String,String>();
  			for (int i=0; i<alphabet.length(); i++) {
  				decoderHash.put(""+alphabet.charAt(i), ""+Zodiac.solutions[Zodiac.CIPHER].charAt(i));
  			}
  		}
  		
  		String word;
  		String search;
  		int total;
  		for (int i=0; i<alphabet.length(); i++) {
  			for (int j=0; j<alphabet.length(); j++) {
  				if (j!=i) {
  					findRepeatsSub("" + alphabet.charAt(i) + alphabet.charAt(j), cipher, alphabet);
  					for (int k=0; k<alphabet.length(); k++) {
  						if (k!=j && k!=i) {
  							findRepeatsSub("" + alphabet.charAt(i) + alphabet.charAt(j) + alphabet.charAt(k), cipher, alphabet);  							
  							for (int l=0; l<alphabet.length(); l++) {
  								if (l!=k && l!=j && l!=i) {
  									findRepeatsSub("" + alphabet.charAt(i) + alphabet.charAt(j) + alphabet.charAt(k) + alphabet.charAt(l), cipher, alphabet);
  								}
  							}
  						}
  					}
  				}
  			}
  		}
  	}
  	
  	public static void findRepeatsSub(String word, String cipher, String alphabet) {
			String search = "";
			String dec = "";
			//System.out.println(word);
			boolean found;
			for (int a=0; a<cipher.length(); a++) {
				found = false;
				for (int i=0; i<word.length(); i++) found = found || (cipher.charAt(a) == word.charAt(i)); 
				if (found) {
					search += "" + cipher.charAt(a);
					//System.out.println(search);
				}
			}
			int total = 0;
			String sub;
			if (search.length() > 0) {
				/*
				for (int a=0; a<search.length()-word.length()-1; a++) {
					if (search.substring(a,a+word.length()).equals(word)) total++;
				}*/
				sub = word; total = 0;
				while (search.indexOf(sub) > -1) {
					sub += word; total++;
				}
				
				if (Zodiac.CIPHER == 1) {
					for (int i=0; i<word.length(); i++)
						dec += decoderHash.get(""+word.charAt(i));
				}
				
				if (total > 1)
					System.out.println(word + (Zodiac.CIPHER == 1 ? " [" + dec + "]" : "") + "," + total + "," + search.length() + "," + (float)total/search.length() + "," + search);
			}
  		
  	}
  	
  	public static void testBrute9() {
  		if (Zodiac.CIPHER != 0) throw new RuntimeException("sorry, only for cipher 0");
  		initAllPools();
  		String alph = "abcdefghijklmnoprstuvwy";
  		int[] pointers = new int[] {0,1,8,0,0,0};
  		//for (int i=0; i<pointers.length; i++) pointers[i] = 0;
  		//ABCDEFGHIJKLMNOPRSTUVWXYZ123456789plkdfycjqbtz()>^+.<-/#_@\%&;:
  		//012345678901234567890123456789012345678901234567890123456789012
  		//0         1         2         3         4         5         6
  		
  		int[] decoderIndices = new int[] {50, 1, 34, 29, 5}; // +Bp5F
  		int[] decoderIndicesCollapsed = new int[] {35, 10, 12, 14}; // lKMO
  		
  		CipherWordGene gene = new CipherWordGene();
  		gene.resetDecoder();
  		
  		String word;
  		StringBuffer dec;
  		String decoded;
  		int fit;
  		boolean go = true;
  		while (go) {
  			word = "";
  			for (int i=0; i<pointers.length; i++) {
  				word += alph.charAt(pointers[i]);
  			}
  			for (int j=pointers.length-1; j>=0; j--) {
  				pointers[j]++;
  				if (pointers[j]==alph.length()) {
  					pointers[j] = 0;
  					if (j==0) go = false;
   				} else break;
  				if (j==3) System.out.println("-- " + new java.util.Date().getTime() + " - made it to " + word);
  			}
  			
  			dec = new StringBuffer(CipherGene.resetDecoder);
  			for (int i=0; i<decoderIndices.length; i++) dec.setCharAt(decoderIndices[i], word.charAt(i));
  			for (int i=0; i<decoderIndicesCollapsed.length; i++) dec.setCharAt(decoderIndicesCollapsed[i], word.charAt(word.length()-1));
  			gene.decoder = new StringBuffer(dec.toString());
  			decoded = gene.decode();
  			//FIX THIS IF WANT TO RE_RUN THIS: fit = fitnessSubstring(1, true, decoded, substringPool, wordPool, gene.decoder.toString());
//  		FIX THIS IF WANT TO RE_RUN THIS: if (fit > 4) System.out.println(word + "," + fit + "," + gene.decoder + "," + decoded);
  		}
  		
  		
  	}
  	
  	public static void testArraySpeed() {
  		int NUM = 1000000;
  		int[] test1 = new int[NUM];
  		ArrayList<Integer> test2 = new ArrayList<Integer>(NUM);  		
  		Vector<Integer> test3 = new Vector<Integer>();
  		Date time1;
  		Date time2;
  		
  		int val;
  		
  		time1 = new Date();
  		for (int i=0; i<NUM; i++) {
  			test1[i] = NUM-i;
  		}
  		for (int i=0; i<NUM; i++) {
  			val = test1[i];
  		}
  		time2 = new Date();
  		
  		System.out.println("took " + (time2.getTime() - time1.getTime()) + " ms.");
  		test1 = null;
  		
  		time1 = new Date();
  		for (int i=0; i<NUM; i++) {
  			test2.add(NUM-i);
  		}
  		for (int i=0; i<NUM; i++) {
  			val = test2.get(i);
  		}
  		time2 = new Date();
  		
  		System.out.println("took " + (time2.getTime() - time1.getTime()) + " ms.");
  		test2 = null;
  		
  		time1 = new Date();
  		for (int i=0; i<NUM; i++) {
  			test3.add(NUM-i);
  		}
  		for (int i=0; i<NUM; i++) {
  			val = test3.get(i);
  		}
  		time2 = new Date();
  		
  		System.out.println("took " + (time2.getTime() - time1.getTime()) + " ms.");
  		
  	}
  	
  	
  	/** take a gene and derive a new decoder that has fuzzy matches filled in.
  	 * to do this, we search all fuzzy matches for the selection that mazimizes the remaining fuzzy match score, 
  	 * and then repeat until no unfilled positions (that are covered by remaining fuzzy matches) are left.
  	 */
  	public static void fillIn(CipherWordGene gene) {
  		initAllPools();
  		if (wordPoolHash == null) initWordPoolHash();
  		
  		//fitnessSubstring(1, true, decoded.toString(), substringPool, substringPrefixesPool, wordPool, decoder.toString());
  		//public int fitnessSubstring(int minLength, boolean unique, String text, THashMap<String, THashMap<String, Integer>> subPool, THashSet<String> prefixes, String[] wPool, String decoder) {

  		/* TODO
  		String zodiacWordsFuzzy = "";
  		String word; int pos;
  		List<Object[]> list = substringMatches(1, gene.decoded.toString(), substringPool, substringPrefixesPool, wordPool);
  		
  		THashSet<Integer> hash = new THashSet<Integer>();
  		THashSet<String> fuzzyFound = new THashSet<String>(); 
  		for (int i=0; i<list.size(); i++) {
  			if (Zodiac.getDecoderForWordAtPosition(decoder, (String)list.get(i)[1], (Integer)list.get(i)[2], true) != null) {
  				hash.add((Integer)list.get(i)[2]);
  				//System.out.println("hash added " + (Integer)list.get(i)[2]);
  				word = (String) list.get(i)[1];
  				pos = (Integer)list.get(i)[2];
  			  if (!fuzzyFound.contains(word + " " + pos)) {
  			  	zodiacWordsFuzzy += word + " " + pos + " ";
  			  	fuzzyFound.add(word + " " + pos);
  			  }
  				if (RESPECT_OVERLAP)
  					overlap.replace(pos, pos+word.length(), word.replaceAll(".", "_"));
  			}
  		}
  		*/
  	}

  	/** load genomes from csv file and generate fitness measures as csv */
  	public static void generateMetrics() {
		ZodiacDictionary.USE_ISWORDHASH = false;
		ZodiacDictionary.makeWordConstraintDatabase(CipherWordGene.wordPool);
  		//String[] lines = ZodiacDictionary.getContents(new File("/Users/doranchak/projects/work/java/zodiac/genome-inputs-for-arffs-production"), false).split(System.getProperty("line.separator"));
  		String[] lines = ZodiacDictionary.getContents(new File("/Users/doranchak/projects/work/java/zodiac/experiment4556-459-gen950-genomes"), false).split(System.getProperty("line.separator"));
  		for (int i=0; i<lines.length; i++) {
  			lines[i] = lines[i].replaceAll(" ", "").replaceAll("\t", "").replaceAll("\n", "").replaceAll("\r", "");
  		}
  		int[] genome; String[] genomeString;
  		for (int i=0; i<lines.length; i++) {
  			//System.out.println(lines[i]);
  			genomeString = lines[i].split(",");
  			genome = new int[genomeString.length];
  			for (int j=0; j<genomeString.length; j++) {
  				genome[j] = Integer.valueOf(genomeString[j]).intValue();
  			}
  			System.out.println(getMetricsFor(genome));
  		}
  	}
  	
  	public static void testMetrics() {
  		int w = wordPool.length;
  		System.out.println(getMetricsFor(new int[] {  //llbecomemyslavesiwillnotgiveyoumynamebecauseyouwill
  				getAlleleFromWord("become")+w, 2,
  				getAlleleFromWord("my")+w, 8,
  				getAlleleFromWord("slaves")+w, 10,
  				getAlleleFromWord("i")+w, 16,
  				getAlleleFromWord("will")+w, 17,
  				getAlleleFromWord("not")+w, 21,
  				getAlleleFromWord("give")+w, 24,
  				getAlleleFromWord("you")+w, 28,
  				getAlleleFromWord("my")+w, 31,
  				getAlleleFromWord("name")+w, 33,
  				getAlleleFromWord("because")+w, 37,
  				getAlleleFromWord("you")+w, 44,
  				getAlleleFromWord("will")+w, 47
  		}));
  		System.out.println(getMetricsFor(new int[] {
  				280,327, 0,0, 0,0, 0,0, 0,0, 498,232, 0,0, 309,126, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 318,119, 0,0, 0,0, 408,309, 0,0, 240,59, 372,87, 0,0, 0,0
  		}));
  		
  		System.out.println(getMetricsFor(new int[] {
  				103,315, 0,0, 48,330, 0,0, 208,317, 0,0, 399,182, 93,425, 187,238, 0,0, 259,311, 0,0, 331,141, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 60,313, 209,327
  		}));
  		
  	}

  	/** used to test deep vs shallow cloning speed */
  	public static void testCloning() {
  		int w = wordPool.length;
  		//CipherWordGene gene = new CipherWordGene();
  		//"become","my","slaves","i","will","not","give","you","my","name","because","you","will","the", "i", "to", "of", "a", "in", "you", "and", "my", "is", "was", "have", "this", "it", "that", "with", "me", "for", "be", "will", "on", "not", "them", "by", "all", "who", "her", "they", "shall", "if", "but", "am", "about", "up", "she", "do", "your", "out", "one", "when", "then", "zodiac", "people", "like", "would", "some", "as", "are", "so", "police", "missed", "did", "car", "back", "speaking", "there", "or", "no", "last", "because", "at", "were", "time", "thing", "more", "from", "can", "never", "killing", "just", "down", "bomb", "way", "very", "vallejo", "think", "their", "slaves", "see", "s", "rather", "over", "off", "now", "nice", "list", "im", "had", "get", "editor", "could", "buttons", "been", "what", "tell", "school", "san", "park", "others", "only"
  		CipherWordGene gene = runTestProgressive(new int[] {  //llbecomemyslavesiwillnotgiveyoumynamebecauseyouwill
  				getAlleleFromWord("become")+w, 2,
  				getAlleleFromWord("my")+w, 8,
  				getAlleleFromWord("slaves")+w, 10,
  				getAlleleFromWord("i")+w, 16,
  				getAlleleFromWord("will")+w, 17,
  				getAlleleFromWord("not")+w, 21,
  				getAlleleFromWord("give")+w, 24,
  				getAlleleFromWord("you")+w, 28,
  				getAlleleFromWord("my")+w, 31,
  				getAlleleFromWord("name")+w, 33,
  				getAlleleFromWord("because")+w, 37,
  				getAlleleFromWord("you")+w, 44,
  				getAlleleFromWord("will")+w, 47
  		});
  		int iterations = 25000;
  		Object clone;
  		Date start = new Date();
  		for (int i=0; i<iterations; i++) {
  			clone = gene.clone();
  		}
  		Date end = new Date();
  		long diff = end.getTime() - start.getTime();
  		System.out.println("cloning time " + diff + " ms (" + (1000*(float)iterations/diff) + " / s)");
  	}
  	public static void testConflictProgressive() {
  		TRUNCATE_DEAD_ENDS = false;
  		ZodiacDictionary.USE_ISWORDHASH = false;
  		int w = wordPool.length;
  		//CipherWordGene gene = new CipherWordGene();
  		//"become","my","slaves","i","will","not","give","you","my","name","because","you","will","the", "i", "to", "of", "a", "in", "you", "and", "my", "is", "was", "have", "this", "it", "that", "with", "me", "for", "be", "will", "on", "not", "them", "by", "all", "who", "her", "they", "shall", "if", "but", "am", "about", "up", "she", "do", "your", "out", "one", "when", "then", "zodiac", "people", "like", "would", "some", "as", "are", "so", "police", "missed", "did", "car", "back", "speaking", "there", "or", "no", "last", "because", "at", "were", "time", "thing", "more", "from", "can", "never", "killing", "just", "down", "bomb", "way", "very", "vallejo", "think", "their", "slaves", "see", "s", "rather", "over", "off", "now", "nice", "list", "im", "had", "get", "editor", "could", "buttons", "been", "what", "tell", "school", "san", "park", "others", "only"
  		runTestProgressive(new int[] {  //llbecomemyslavesiwillnotgiveyoumynamebecauseyouwill
  				getAlleleFromWord("become")+w, 2,
  				getAlleleFromWord("my")+w, 8,
  				getAlleleFromWord("slaves")+w, 10,
  				getAlleleFromWord("i")+w, 16,
  				getAlleleFromWord("will")+w, 17,
  				getAlleleFromWord("not")+w, 21,
  				getAlleleFromWord("give")+w, 24,
  				getAlleleFromWord("you")+w, 28,
  				getAlleleFromWord("my")+w, 31,
  				getAlleleFromWord("name")+w, 33,
  				getAlleleFromWord("because")+w, 37,
  				getAlleleFromWord("you")+w, 44,
  				getAlleleFromWord("will")+w, 47
  		});
  		runTestProgressive(new int[] {
  				280,327, 0,0, 0,0, 0,0, 0,0, 498,232, 0,0, 309,126, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 318,119, 0,0, 0,0, 408,309, 0,0, 240,59, 372,87, 0,0, 0,0
  		});
  		
  		runTestProgressive(new int[] {
  				103,315, 0,0, 48,330, 0,0, 208,317, 0,0, 399,182, 93,425, 187,238, 0,0, 259,311, 0,0, 331,141, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 60,313, 209,327
  		});
  		
  		runTestProgressive(new int[] {
  				221,302,  0,0,  140,287,  0,0,  0,0,  296,0,  0,0,  0,0,  0,0,  0,0,  0,0,  220,310,  0,0,  0,0,  0,0,  0,0,  38,329,  126,319,  228,293,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0
			});
  		
  		runTestProgressive(new int[] {
  	  		77,316,  0,0,  0,0,  216,381,  0,0,  0,0,  0,0,  0,0,  0,0,  376,206,  147,441,  0,0,  0,0,  0,0,  0,0,  0,0,  401,334,  0,0,  416,327,  0,0,  0,0,  0,0,  454,445,  0,0,  414,455
			});

  		runTestProgressive(new int[] {
  				54,314,  179,17,  170,291,  0,0,  150,295,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  164,20,  0,0,  0,0,  338,316,  0,0,  0,0,  0,0,  216,491,  0,0,  0,0,  0,0,  0,0,  0,0  		
			});
  		
  		System.out.println("=============");  		
  		CipherWordGene gene = new CipherWordGene();
  		gene.genome = new int[50];
  		gene.conflictInit();
  		gene.allelePut(0, 54, 23);
  		gene.allelePut(24,396,287);
  		gene.allelePut(24,0,0);
  		System.out.println("removed 24 " + gene.decoder);
  		gene.allelePut(24,396,288);
  		System.out.println("hmm: " + gene.debug + ": " + gene.getDebugLine());

  		
  		  
  		System.out.println("=============");  		
  		gene = new CipherWordGene();
  		gene.genome = new int[50];
  		gene.conflictInit();
  		gene.allelePut(14, 157, 85); //put 14,157,85 (happy,320)  addsuccess 
  		gene.allelePut(14, 157, 491);//put 14,157,491 (happy,318) remove 14,157,85 (happy,320)  addfail remove 14,0,0 (,286)
  		gene.allelePut(14, 157, 439);//put 14,157,439 (happy,317) remove 14,157,85 (happy,320)  addfail remove 14,0,0 (,286) 
  		gene.allelePut(14, 157, 138);//put 14,157,138 (happy,322) remove 14,157,85 (happy,320)  addfail remove 14,0,0 (,286) 
  		gene.allelePut(14, 157, 106);//put 14,157,106 (happy,290) remove 14,157,85 (happy,320)  addfail remove 14,0,0 (,286) 
  		gene.allelePut(14, 157, 360);//put 14,157,360 (happy,289) remove 14,157,85 (happy,320)
  		System.out.println("hmm: " + gene.debug + ": " + gene.getDebugLine());

  		//: put 14,157,85 (happy,320)  addsuccess put 14,157,491 (happy,318) remove 14,157,85 (happy,320)  addsuccess put 14,157,439 (happy,317) remove 14,157,491 (happy,318)  addfail remove 14,0,0 (,286) put 14,157,138 (happy,322) remove 14,157,491 (happy,318)  addsuccess put 14,157,106 (happy,290) remove 14,157,138 (happy,322)  addfail remove 14,0,0 (,286) put 14,157,360 (happy,289) remove 14,157,138 (happy,322)  addfail remove 14,0,0 (,286) : fitness [], fitnessUnshared [], fitnessWord 0, fitnessNGrams[1] 0, fitnessNGrams[2] 0, fitnessNGrams[3] 0, fitnessNGrams[4] 0, fitnessNGrams[5] 0, fitnessNGrams[6] 0, fitnessCoverageDictionaryScaled 0.0, corpus [null], corpusUnclobbered [null], decoder [?????????????????????h????????????????????????????????], decoded [?????????????????h???????????????????????????????????????????????????????????????h?????????????????????????????????????????????????????????????h???????????????????????????????h???????????????????????????????????????????????????????????????????????h?????????????????????????????????????????????h????????????????????????????h??????????????????????????????????????????????????????????????????h??????h???????????], decodedSubstring [???????h????????????????????????????h??????????????], conflictOverlap [???????????????????????????????????????????????????], words , coverage null, zodiacMatch 0, clusterCount 0.0, plugger null, zodiacWordsOther null, zodiacWordsFuzzy , sortedKey null, genome 0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  157,138,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  , totalConflicts 0, totalWildcardConflicts 0, bestWords null
  		//: put 14,157,85 (happy,320)  addsuccess put 14,157,491 (happy,318) remove 14,157,85 (happy,320)  addfail remove 14,0,0 (,286) put 14,157,439 (happy,317) remove 14,157,85 (happy,320)  addfail remove 14,0,0 (,286) put 14,157,138 (happy,322) remove 14,157,85 (happy,320)  addfail remove 14,0,0 (,286) put 14,157,106 (happy,290) remove 14,157,85 (happy,320)  addfail remove 14,0,0 (,286) put 14,157,360 (happy,289) remove 14,157,85 (happy,320)
  		
  		
  		System.out.println("=============");  		
  		gene = new CipherWordGene();
  		gene.genome = new int[50];
  		gene.conflictInit();
  		//put 38,388,276 (always,307)  addsuccess
  		gene.allelePut(38,388,276);
  		//put 0,151,286 (knife,317)  addfail remove 0,0,0 (,286) 
  		gene.allelePut(0,151,286);
  		//put 0,151,287 (knife,318)  		
  		gene.allelePut(0,151,287);
  		System.out.println("hmm: " + gene.debug + ": " + gene.getDebugLine());

  		
  		System.out.println("=============");  		
  		gene = new CipherWordGene();
  		gene.genome = new int[50];
  		gene.conflictInit();
  		//put 34,423,146 (shall,330)  addsuccess 
  		gene.allelePut(34,423,146);
  		//put 2,311,0 (knee,286)  addfail remove 2,0,0 (,286) 
  		gene.allelePut(2,311,0);
  		//put 2,256,0 (things,286)  addfail remove 2,0,0 (,286) 
  		gene.allelePut(2,256,0);
  		//put 2,273,0 (scream,286)  addfail remove 2,0,0 (,286) 
  		gene.allelePut(2,273,0);
  		//put 2,421,0 (her,286)  addfail remove 2,0,0 (,286) 
  		gene.allelePut(2,421,0);
  		//put 2,181,0 (untill,286)  addfail remove 2,0,0 (,286) 
  		gene.allelePut(2,181,0);
  		//put 2,259,0 (st,286)
  		gene.allelePut(2,259,0);
  		System.out.println("hmm: " + gene.debug + ": " + gene.getDebugLine());
  		
  		runTestProgressive(new int[] {
  	  		796,1897,  1771,1343,  0,0,  877,1176,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  1026,444,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  704,334,  0,0,  0,0,  0,0
  		});
  		
  		runTestProgressive(new int[] {
  	  		342,632,  0,0,  1843,905,  0,0,  0,0,  0,0,  405,1389,  0,0,  184,1972,  898,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  1206,1437,  0,0,  0,0,  0,0,  0,0,  712,118,  0,0,  0,0,  
  		});
  		runTestProgressive(new int[] {
  	  		342,785,  0,0,  1843,905,  0,0,  0,0,  0,0,  405,1389,  0,0,  184,1972,  898,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  1206,1437,  0,0,  0,0,  0,0,  0,0,  712,118,  0,0,  0,0,  
  		});
  		
  		gene = runTestProgressive(new int[] {
  				1047,318,  0,0,  0,0,  0,0,  0,0,  0,0,  845,0,  922,21,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  1897,1257,  237,292,  0,0,  0,0  
  		});
  		System.out.println("=============BEFORE FILLING IT OUT:");
  		System.out.println(gene.getDebugLine());
  		fillIn(gene);
  		System.out.println("=============AFTER FILLING IT OUT:");
  		System.out.println(gene.getDebugLine());
  		/*
  		
  		runTestProgressive(new int[] {
  	  		0,0, 0,0, 0,0, 0,0, 60,304, 73,89, 0,0, 0,0, 451,297, 0,0, 0,0, 0,0, 0,0, 298,313, 0,0, 0,0, 103,307, 341,319, 0,0, 0,0, 413,288, 296,22, 0,0, 0,0, 280,311
  		});
  		
  		runTestProgressive(new int[] {
  	  		103,315, 0,0, 0,0, 0,0, 208,317, 0,0, 0,0, 93,425, 187,238, 0,0, 259,311, 350,327, 331,141, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 60,313, 197,335
  		});*/
  		
  		/*
  		runTestProgressive(new int[] {
  				7+w,28,10+w,37,5+w,21,2+w,10,4+w,17,9+w,33,3+w,16,0+w,2,6+w,24
  		});
  		
  		runTestProgressive(new int[] {
  				getAlleleFromWord("over")+w,33,
  				getAlleleFromWord("on")+w,16,
  				getAlleleFromWord("not")+w,47,
  				getAlleleFromWord("s")+w,19,
  				getAlleleFromWord("there")+w,4,
  				getAlleleFromWord("as")+w,0,
  				getAlleleFromWord("are")+w,43,
  				getAlleleFromWord("rather")+w,10,
  				getAlleleFromWord("then")+w,20,
  				getAlleleFromWord("or")+w,2,
  				getAlleleFromWord("out")+w,37,
  				getAlleleFromWord("a")+w,50,
  				getAlleleFromWord("her")+w,30,
  				getAlleleFromWord("others")+w,24,
  				getAlleleFromWord("who")+w,40
  		});
  		
  		runTestProgressive(new int[] {
  				72,191,  51,69,  0,66,  13,78,  79,238,  16,73,  64,276,  0,195,  61,404,  116,351,  0,379,  335,311,  134,126,  92,221,  0,126,  122,307,  0,0,  48,15,  0,0,  120,61,  2,0,  111,254,  52,332,  0,135,  130,186	
  		});
  		
  		runTestProgressive(new int[] {
  				93,50, 28,228, 137,275, 0,194, 12,292, 6,241, 20,69, 91,272, 181,263, 127,298, 39,156, 15,415, 10,335, 6,68, 18,76, 118,80, 11,64, 35,241, 6,182, 10,70, 1,168, 24,303, 0,0, 73,85, 11,33
  		});
  		
  		runTestProgressive(new int[] {
  				93,50, 28,228, 10,275, 6,311, 12,19, 6,347, 100,173, 91,272, 181,212, 127,298, 39,156, 20,460, 0,422, 135,335, 10,59, 22,252, 126,64, 337,240, 11,156, 0,221, 1,312, 19,226, 23,105, 2,221, 34,33
  		});
  		System.out.println("experiment 195");
  		
  		runTestProgressive(new int[] {
  				90,367, 0,300, 44,187, 116,76, 113,53, 0,311, 0,0, 0,390, 0,373, 120,232, 0,0, 0,0, 0,32, 123,88, 0,241, 0,75, 0,0, 0,0, 0,0, 145,119, 118,21, 0,0, 0,0, 0,0, 0,33
  		});

  		runTestProgressive(new int[] {
  				449,398, 0,0, 0,0, 0,0, 0,0, 0,0, 443,238, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 168,353, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 102,175, 0,0, 0,0
  		});
  		
  		runTestProgressive(new int[] {
  	  		93,50, 28,228, 137,275, 17,36, 0,154, 18,5, 17,69, 91,272, 181,212, 127,298, 39,156, 10,415, 3,107, 135,335, 17,434, 8,80, 126,64, 337,240, 1,312, 7,7, 3,484, 4,182, 0,123, 9,378, 73,340
  		});
  		
  		runTestProgressive(new int[] {
  				117,68, 0,388, 0,165, 0,0, 0,34, 0,214, 0,0, 0,419, 113,206, 90,469, 72,292, 3,131, 0,186, 0,0, 124,385, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 122,339, 0,0, 0,0, 0,0
  		});
  		
  		runTestProgressive(new int[] {
  				96,276, 20,467, 2,343, 9,115, 0,373, 12,21, 0,386, 130,182, 113,206, 90,469, 0,293, 14,347, 298,139, 4,294, 0,254, 10,242, 0,427, 98,305, 0,166, 3,0, 1,0, 122,339, 107,119, 18,49, 0,374
  		});
  		
  		runTestProgressive(new int[] {
  	  		58,175, 104,303, 63,104//, 0,0, 0,0, 4,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 2,385, 0,0, 0,339, 17,0, 123,301, 25,0, 0,0 
  		});
  		runTestProgressive(new int[] {
  				80,125, 23,75, 34,353, 412,163, 20,39, 53,0, 12,266, 407,50, 9,182, 0,485, 17,380, 13,234, 20,330, 491,238, 126,20, 18,448, 0,285, 3,366, 12,453, 17,185, 0,342, 3,319, 0,160, 0,82, 93,242
  		});
  		
  		runTestProgressive(new int[] {
  				66,21, 0,391, 0,0, 0,0, 279,0, 0,0, 0,370, 9,192, 0,0, 0,240, 98,251, 0,0, 133,25, 0,0, 190,135, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 33,162, 0,0, 0,267, 364,244
  		});
  		
  		runTestProgressive(new int[] {
  				122,123, 12,472, 20,349, 20,101, 4,0, 1,450, 92,107, 12,84, 0,375, 25,0, 0,0, 8,202, 18,0, 0,288, 499,135, 16,369, 0,0, 68,113, 0,63, 2,267, 104,303, 70,366, 0,447, 0,386, 93,38
  		});
  		
  		runTestProgressive(new int[] {
  				105,288, 239,0, 8,480, 139,316, 17,241, 273,233, 333,152, 299,175, 92,327, 14,404, 9,225, 33,228, 2,358, 22,207, 0,53, 491,245, 79,89, 0,160, 9,239, 262,99, 0,0, 75,165, 116,119, 0,204, 60,310
  		});
  		runTestProgressive(new int[] {
  	  		461,340, 0,0, 0,0, 119,320, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 407,111, 0,0, 0,0, 0,0, 0,0, 111,325, 0,0, 288,38
  		});
  		
  		runTestProgressive(new int[] {
  				93,326, 23,326, 324,386, 0,308, 0,0, 0,0, 0,0, 68,316, 0,0, 0,0, 0,0, 306,372, 0,0, 0,0, 81,292, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 0,0, 82,288, 0,0
  		});
  		
  		runTestProgressive(new int[] {
  				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 450, 101
  		});
  		*/
  		/* speed test */
  		int[] g = 
  		new int[] {
  				7+w,28,10+w,37,5+w,21,2+w,10,4+w,17,9+w,33,3+w,16,0+w,2,6+w,24
  		};
  		
  		
  		int NUM = 10000;
  		Date d1; Date d2;
  		/*
  		System.out.println("timing inits of " + NUM + " genes. first timing is old hasConflict.  2nd timing is via progressive conflict.");
  		Date d1 = new Date();
  		for (int i=0; i<NUM; i++) runTest(g);
  		Date d2 = new Date();
  		System.out.println("did " + NUM + " in " + (d2.getTime() - d1.getTime()) + " ms.");
  		d1 = new Date();
  		for (int i=0; i<NUM; i++) runTestProgressive(g);
  		d2 = new Date();
  		System.out.println("did " + NUM + " in " + (d2.getTime() - d1.getTime()) + " ms.");
  		*/
  		/*
  		System.out.println("timing random poking");
  		CipherWordGene gene1 = new CipherWordGene();
  		CipherWordGene gene2 = new CipherWordGene();
  		int LEN = 10; // 5 words
  		int[] g1 = new int[LEN];
  		int[] g2 = new int[LEN];
  		gene1.genome = g1;
  		gene2.genome = g2;
  		
  		gene2.conflictInit();
  		int index; int p; int oldW, oldP;
  		boolean h;
  		d1 = new Date();
  		for (int i=0; i<NUM; i++) {
  			index = rand().nextInt(LEN/2)*2;
  			w = rand().nextInt(500);
  			p = rand().nextInt(500);
  			oldW = gene1.genome[index];
  			oldP = gene1.genome[index+1];
  			gene1.genome[index] = w;
  			gene1.genome[index+1] = p;
  			if (gene1.hasConflictOBSOLETE(gene1.genome)) {
  				gene1.genome[index] = oldW;
  				gene1.genome[index+1] = oldP;
  			}
  			//System.out.println(h + "," + gene1.decoder);
  		}
  		d2 = new Date();
  		System.out.println("did " + NUM + " hasConflicts in " + (d2.getTime() - d1.getTime()) + " ms.");

  		d1 = new Date();
  		for (int i=0; i<NUM; i++) {
  			index = rand().nextInt(LEN/2)*2;
  			w = rand().nextInt(500);
  			p = rand().nextInt(500);
  			gene2.allelePut(index, w, p);
  		}
  		d2 = new Date();
  		System.out.println("did " + NUM + " allelePuts in " + (d2.getTime() - d1.getTime()) + " ms.");

  	  CipherWordGene.initAllPools();
  		d1 = new Date();
  		for (int i=0; i<NUM; i++) {
  			index = rand().nextInt(LEN/2)*2;
  			w = rand().nextInt(500);
  			p = rand().nextInt(500);
  			gene2.allelePut(index, w, p);
  			gene2.fitness();
  		}
  		d2 = new Date();
  		System.out.println("did " + NUM + " allelePuts (with fitness) in " + (d2.getTime() - d1.getTime()) + " ms.");*/
  		/*
  		runTestProgressive(new int[] {
  				243,316, 416,342, 0,0, 0,0, 26,288, 342,344, 244,297, 0,0, 62,322, 0,0, 67,2, 0,0, 79,331, 0,0, 400,335, 281,332, 56,313, 0,0, 0,0, 0,0, 215,326, 0,0, 0,0, 0,0, 0,0
  		});*/
  		
  		
  		
  		runTestProgressive(new int[] {
  				192,1776,  850,636,  191,1642,  0,0,  0,0,  1456,1100,  0,0,  1303,342,  138,323,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  155,319,  0,0,  0,0,  347,326,  0,0,  0,0,  210,308,  0,0,  0,0
  		});
  		
  		runTestProgressive(new int[] {
  	  		138,455,  1640,1182,  343,33,  801,867,  0,0,  1070,756,  0,0,  1240,1296,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  736,954,  0,0,  0,0,  0,0,  0,0,  0,0,  473,319,  0,0,  272,332,  0,0
  		});
  		
  		runTestProgressive(new int[] {
  				226,1859,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  411,313,  0,0,  1602,1665,  416,315,  1405,88,  0,0,  0,0,  155,325,  1635,0,  1058,471,  0,0,  0,0,  296,298,  0,0,  794,302,  280,311
  		});
  		
  		runTestProgressive(new int[] {
  				226,1859,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  411,313,  0,0,  1602,1665,  416,315,  1405,88,  0,0,  0,0,  155,325,  1635,0,  1058,471,  0,0,  0,0,  296,298,  0,0,  794,302,  280,311
  		});

  		runTestProgressive(new int[] {
  	  		269,309,  435,332,  0,0,  0,0,  0,0,  197,288,  0,0,  0,0,  399,300,  0,0,  471,292,  0,0,  488,314,  103,320,  0,0,  0,0,  0,0,  0,0,  0,0,  396,331,  455,328,  0,0,  0,0,  426,297,  414,323
  		});

  		runTestProgressive(new int[] {
  				482,289,  0,0,  0,0,  0,0,  320,307,  0,0,  0,0,  0,0,  0,0,  408,297,  443,322,  259,316,  318,318,  0,0,  0,0,  299,335,  494,293,  429,324,  0,0,  0,0,  0,0,  0,0,  396,288,  0,0,  479,328
  		});
  		
  		runTestProgressive(new int[] {
  				650,229,  271,486,  636,4,  823,504,  344,329,  338,987,  0,0,  0,0,  0,0,  0,0,  976,28,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  758,150,  0,0,  0,0,  0,0,  0,0,  0,0
  		});
  		
  		runTestProgressive(new int[] {
  	  		587,608,  181,172,  739,993,  0,0,  402,191,  0,0,  594,571,  0,0,  190,34,  883,233,  800,420,  0,0,  0,0,  0
  	  		,0,  692,84,  0,0,  0,0,  0,0,  0,0,  328,2,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0, 
  	  		 0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0
  		});

  		runTestProgressive(new int[] {
  		 978,859,  604,0,  0,0,  0,0,  214,798,  0,0,  591,321,  0,0,  771,241,  921,935,  0,0,  0,0,  0,0,  568,444,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0
  		});

  		
  	}
  	
  	public static void testConflictProgressive2() {
  		ZodiacDictionary.USE_ISWORDHASH = false;
  		ZodiacDictionary.makeWordConstraintDatabase(wordPool);
  		/* known solution */
  		int w = wordPool.length;
  		/*runTestProgressive(new int[] {  //llbecomemyslavesiwillnotgiveyoumynamebecauseyouwill
  				getAlleleFromWord("become")+w, 2,
  				getAlleleFromWord("my")+w, 8,
  				getAlleleFromWord("slaves")+w, 10,
  				getAlleleFromWord("i")+w, 16,
  				getAlleleFromWord("will")+w, 17,
  				getAlleleFromWord("not")+w, 21,
  				getAlleleFromWord("give")+w, 24,
  				getAlleleFromWord("you")+w, 28,
  				getAlleleFromWord("my")+w, 31,
  				getAlleleFromWord("name")+w, 33,
  				getAlleleFromWord("because")+w, 37,
  				getAlleleFromWord("you")+w, 44,
  				getAlleleFromWord("will")+w, 47
  		});
  		runTestProgressive(new int[] {  //mostthrillingexperenceitisevenbetterthangettingyourrocksoffwithagirlthebestpartofitisthaewhenidieiwillbereborninparad
  				getAlleleFromWord("most")+w, 0,
  				getAlleleFromWord("thrilling")+w, 4,
  				getAlleleFromWord("experence")+w, 13,
  				getAlleleFromWord("even")+w, 26,
  				getAlleleFromWord("better")+w, 30,
  				getAlleleFromWord("than")+w, 36,
  				getAlleleFromWord("getting")+w, 40,
  				getAlleleFromWord("your")+w, 47,
  				getAlleleFromWord("rocks")+w, 51,
  				getAlleleFromWord("girl")+w, 64,
  				getAlleleFromWord("best")+w, 71,
  				getAlleleFromWord("part")+w, 75,
  				getAlleleFromWord("thae")+w, 85,
  				getAlleleFromWord("when")+w, 89,
  				getAlleleFromWord("will")+w, 98,
  				getAlleleFromWord("reborn")+w, 104
  		});*/
  		runTestProgressive(new int[] {  //killingwildgameintheforrestbecausemanisthemostdanger
  				getAlleleFromWord("killing")+w, 0,
  				getAlleleFromWord("wild")+w, 7,
  				getAlleleFromWord("game")+w, 11
  				//getAlleleFromWord("inthe")+w, 15,
  				//getAlleleFromWord("forrest")+w, 20,
  				//getAlleleFromWord("because")+w, 27,
  				//getAlleleFromWord("isthe")+w, 37,
  				//getAlleleFromWord("most")+w, 42,
  				//getAlleleFromWord("danger")+w, 46
  		});
  		
  		//killingwildgameintheforrestbecausemanisthemostdanger
  		//012345678901234567890123456789012345678901234567890123456789
  		//0         1         2         3         4         5
  		//mostthrillingexperenceitisevenbetterthangettingyourrocksoffwithagirlthebestpartofitisthaewhenidieiwillbereborninparad
          //0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789
//                    1         2         3         4         5         6         7         8         9         0         1               
  		//runPoolMisc();
  		//runPool500();
  		//runPool600();
  		//runPool700();
  		//runPool800();
  		//runPool1000();
  		//runPool1206();
  		//runPool340_1206();
  		
  		
  		
  	}

  	public static void runPoolMisc() {
  		runTestProgressive(new int[] {
 	  		 978,859,  604,0,  0,0,  0,0,  214,798,  0,0,  591,321,  0,0,  771,241,  921,935,  0,0,  0,0,  0,0,  568,444,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0
 	  		});
 		
 		runTestProgressive(new int[] {
 	  		312,357,  883,400,  0,0,  207,24,  0,0,  487,593,  520,248,  907,377,  279,855,  239,786,  399,189,  806,718,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0
	  		});
 		
 		runTestProgressive(new int[] {
 	  		0,0,  771,849,  265,249,  783,380,  883,413,  664,99,  930,462,  240,741,  0,0,  917,0,  0,0,  407,90,  0,0,  0,0,  808,197,  0,0,  0,0,  0,0,  666,335,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0  		
	  		});
 		
 		runTestProgressive(new int[] {
		  		0,0,  580,343,  0,0,  294,503,  932,0,  695,71,  0,0,  0,0,  0,0,  635,849,  639,627,  0,0,  465,232,  490,276,  0,0,  883,553,  916,776,  478,694,  220,789,  826,192,  361,698,  364,718,  907,212,  0,
		  		0,  0,0
	  		});
 		
 		runTestProgressive(new int[] {
 	  		0,0,  831,469,  0,0,  229,325,  255,635,  704,323,  299,84,  0,0,  804,683,  0,0,  0,0,  808,400,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  193,88,  0,0
	  		});
 		
 		runTestProgressive(new int[] {
 	  		0,0,  0,0,  139,950,  264,61,  368,531,  231,370,  638,631,  767,141,  0,0,  553,584,  937,869,  0,0,  152,748,  295,343,  0,0,  624,47,  0,0,  317,488,  0,0,  918,366,  0,0,  0,0,  294,4,  0,0,  808,221  		
	  		});
 		
 		runTestProgressive(new int[] {
 	  		 583,118,  224,701,  964,232,  663,267,  392,0,  995,380,  806,973,  0,0,  438,924,  928,121,  285,84,  591,928,  794,987,  0,0,  243,978, 0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0
	  		});
 		
 		runTestProgressive(new int[] {
 				416,471,  205,572,  433,594,  139,410,  0,0,  186,654,  927,584,  438,598,  700,702,  0,0,  0,0,  0,0,  356,146,  0,0,  0,0,  0,0,  0,0,  0,0,  289,833,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0  		
 			});
 		
 		runTestProgressive(new int[] {
 	  		324,393,  301,532,  544,689,  819,91,  749,674,  0,0,  370,984,  820,441,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  672,476,  0,0,  0,0,  0,0,  0,0
 			});

 		runTestProgressive(new int[] {
 	  		637,411,  421,0,  0,0,  850,914,  0,0,  0,0,  868,737,  0,0,  0,0,  0,0,  767,26
 			});
 		
 		runTestProgressive(new int[] {
 	  		675,321,  850,605,  866,666,  625,10,  0,0,  638,399,  399,206,  0,0,  0,0,  452,33,  187,21,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  883,527
 			});
 		
 		runTestProgressive(new int[] {
 	  		0,0,  0,0,  0,0,  353,937,  817,423,  0,0,  0,0,  0,0,  0,0,  712,3,  663,289,  0,0,  318,814,  364,177,  386,888,  352,538,  356,33,  0,0,  751,629,  264,367,  0,0,  937,960,  0,0,  736,650,  0,0  
 			});
 		
 		runTestProgressive(new int[] {
 	  		568,444,  830,0,  0,0,  981,298,  389,798,  0,0,  591,321,  142,335,  771,241,  0,0,  412,455,  0,0,  860,264,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0  		
 			});
 		
 		runTestProgressive(new int[] {
 	  		342,391,  747,746,  146,157,  204,329,  885,503,  383,603,  731,0,  591,609,  582,159,  0,0,  0,0,  0,0,  0,0,  368,743,  339,393,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0
 			});
 		
 		runTestProgressive(new int[] {
 				433,401,  0,0,  432,33,  783,788,  529,486,  0,0,  487,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  520,192,  0,0,  0,0  		
			});
 		
 		runTestProgressive(new int[] {
 	  		731,0,  0,0,  428,386,  125,582,  0,0,  140,805,  0,0,  859,973,  300,646,  0,0,  0,0,  75,318,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0
			});
 		
 		runTestProgressive(new int[] {
 	  		959,318,  874,863,  0,0,  526,748,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  522,501
			});
 		
 		runTestProgressive(new int[] {
 	  		60,901,  510,887,  757,663,  913,599,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  126,654,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0
			});
 		
 		runTestProgressive(new int[] {
 	  		236,674,  276,125,  80,886,  0,0,  0,0,  180,956,  0,0,  0,0,  0,0,  0,0,  280,84,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0 
			});
 		
 		runTestProgressive(new int[] {
 	  		0,0,  372,378,  730,0,  909,769,  0,0,  247,193,  0,0,  0,0,  910,841,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  
 	  		0,0,  0,0  		
			});
 		
 		runTestProgressive(new int[] {
 	  		496,549,  777,61,  0,0,  937,992,  0,0,  0,0,  0,0,  530,119,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  474,543,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0
			});
 		
 		runTestProgressive(new int[] {
 	  		710,214,  137,432,  836,496,  577,629,  0,0,  621,798,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0
			});
 		
 		runTestProgressive(new int[] {
 				530,365,  495,292,  0,0,  466,579,  0,0,  614,857,  948,543,  0,0,  0,0,  188,657,  0,0,  271,176,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0
			});
 		
 		runTestProgressive(new int[] {
 	  		0,0,  188,794,  466,374,  0,0,  872,481,  937,316,  0,0,  0,0,  640,394,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0
			});
  	}
  	public static void runPool500() {
  		runTestProgressive(new int[] {
  	  		0,0,  1820,704,  1828,1870,  882,578,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  1340,1539,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0
			});
  	}

  	public static void runPool600() {
  		runTestProgressive(new int[] {
  		  		0,0,  677,826,  1948,1869,  100,1551,  0,0,  0,0,  0,0,  1259,955,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  1326,1445,
			});
  		
  		runTestProgressive(new int[] {
  		  		1219,1496,  677,112,  0,0,  297,685,  0,0,  0,0,  0,0,  1259,955,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  1427,1687,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0
			});
  		
  		System.out.println("Bad ones:");

  		runTestProgressive(new int[] {
  				1276,672,  614,462,  273,449,  0,0,  872,1246,  496,695,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  466,1802,  0,0,  0,0,  0,0,  0,0,  0,0
			});
  		
  		runTestProgressive(new int[] {
  		  		1276,672,  0,0,  1788,1214,  1344,360,  1986,547,  496,287,  0,0,  0,0,  0,0,  0,0,  0,0,  872,277,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  466,1802,  0,0,  0,0,  0,0,  0,0,  0,0
			});
  		
  		runTestProgressive(new int[] {
  		  		544,1923,  1172,182,  0,0,  0,0,  1465,1723,  0,0,  1472,1960,  0,0,  0,0,  0,0,  0,0,  0,0,  530,671,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  1666,1037,  0,0,  0,0,  0,0,  0,0
			});
  		
  		runTestProgressive(new int[] {
  		  		0,0,  900,1089,  317,445,  0,0,  1744,1246,  573,42,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  138,1690,  0,0,  0,0,
			});
  		
  		
  	}
  	public static void runPool700() {
  		runTestProgressive(new int[] {
  			  	1128,1742,  881,397,  0,0,  882,935,  1693,226,  0,0,  0,0,  1642,801,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0
			});
  		
  		runTestProgressive(new int[] {
  		  		1035,572,  671,920,  1110,273,  645,1366,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  1584,901
			});
  		
  		runTestProgressive(new int[] {
  		  		1623,1519,  0,0,  1683,1715,  0,0,  1939,463,  1266,1908,  0,0,  1216,1236,  1885,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0
			});
  	}
  	
  	public static void runPool800() {
  		runTestProgressive(new int[] {0,0,  0,0,  0,0,  0,0,  787,1664,  388,0,  788,91,  0,0,  0,0,  1507,1056,  1491,943,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  1496,313,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {0,0,  0,0,  0,0,  1916,426,  1885,492,  116,1773,  436,1553,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  1572,1436,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {0,0,  0,0,  0,0,  696,1263,  1919,367,  1045,1042,  0,0,  285,511,  1434,659,  1040,1359,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {0,0,  0,0,  1045,1042,  696,1263,  1919,1948,  1272,1359,  0,0,  285,511,  0,0,  0,0,  0,0,  1926,782,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {0,0,  0,0,  1045,1042,  696,1263,  1919,367,  1272,1359,  0,0,  285,511,  1434,659,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {0,0,  0,0,  1491,1773,  0,0,  1156,1543,  1337,119,  121,459,  1375,1806,  0,0,  779,1409,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {0,0,  0,0,  1503,747,  0,0,  471,584,  1011,1621,  1540,1386,  0,0,  1232,0,  0,0,  0,0,  1466,1854,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {0,0,  0,0,  1503,798,  1170,1553,  255,1671,  0,0,  0,0,  0,0,  1232,0,  471,1677,  0,0,  1466,375,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {0,0,  0,0,  1639,1664,  1501,221,  1156,1543,  1536,1889,  0,0,  1201,958,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {0,0,  0,0,  473,1920,  696,1263,  1337,119,  121,459,  0,0,  0,0,  0,0,  0,0,  1491,943,  0,0,  1055,1083,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {0,0,  0,0,  780,1042,  0,0,  1919,1948,  1272,1359,  0,0,  0,0,  53,47,  740,855,  0,0,  920,668,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {0,0,  0,0,  780,1042,  696,1263,  1919,1081,  1272,1410,  0,0,  0,0,  285,511,  0,0,  0,0,  1926,782,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {0,0,  1019,965,  1045,1042,  696,1314,  1919,367,  1272,1359,  0,0,  285,511,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {0,0,  1232,91,  668,327,  285,715,  0,0,  1272,1359,  1919,367,  0,0,  920,668,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {0,0,  1492,350,  315,346,  839,412,  1273,0,  1348,237,  0,0,  1697,687,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {0,0,  1812,497,  0,0,  707,1094,  556,420,  633,782,  0,0,  0,0,  1434,659,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  691,747,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {0,0,  1819,374,  0,0,  285,511,  255,1671,  1113,1400,  0,0,  1540,1692,  0,0,  0,0,  0,0,  0,0,  839,1710,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {0,0,  1819,374,  1580,1195,  696,447,  1919,367,  1272,1359,  0,0,  0,0,  0,0,  285,1480,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {0,0,  1819,374,  787,1195,  696,1263,  1919,367,  472,1359,  0,0,  0,0,  0,0,  0,0,  0,0,  285,511,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {0,0,  385,326,  269,1206,  0,0,  1156,1543,  0,0,  121,459,  0,0,  0,0,  740,855,  1491,943,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {0,0,  387,1612,  0,0,  674,888,  1466,1627,  0,0,  1138,189,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  298,1786,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {0,0,  471,1218,  1503,1614,  666,1110,  0,0,  0,0,  0,0,  1540,1692,  1177,23,  0,0,  0,0,  1232,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {0,0,  471,1677,  1511,798,  0,0,  692,1314,  102,1247,  0,0,  0,0,  0,0,  0,0,  0,0,  1466,375,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {0,0,  53,1649,  269,1461,  1509,1641,  0,0,  1930,346,  285,1480,  670,1806,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {0,0,  695,1524,  0,0,  1496,313,  0,0,  388,0,  0,0,  0,0,  1812,193,  780,695,  0,0,  0,0,  1055,369,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {0,0,  787,1664,  502,1213,  0,0,  1273,0,  1544,1247,  0,0,  734,1115,  0,0,  0,0,  0,0,  1496,313,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {105,856,  1507,1056,  839,693,  1272,774,  0,0,  1498,788,  0,0,  1814,1543,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {1337,1336,  0,0,  666,1955,  696,1008,  780,185,  1491,1330,  472,379,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {1338,359,  0,0,  0,0,  0,0,  1530,1700,  1299,1298,  1115,186,  0,0,  0,0,  0,0,  1429,142,  703,1116,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {1495,0,  1986,579,  0,0,  1814,1543,  1498,788,  0,0,  396,91,  0,0,  1855,1075,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {1496,106,  0,0,  0,0,  1149,1920,  1639,221,  0,0,  1170,1910,  1540,1692,  0,0,  473,958,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {1496,106,  0,0,  1366,1122,  1170,1553,  255,1671,  0,0,  0,0,  0,0,  0,0,  0,0,  839,1710,  471,1677,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {1503,747,  377,1725,  0,0,  0,0,  0,0,  0,0,  1134,74,  1496,106,  1232,0,  0,0,  839,27,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {1863,1008,  1986,579,  1588,306,  0,0,  94,992,  0,0,  0,0,  1622,1664,  1855,1075,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {1863,1008,  1986,579,  1588,306,  1456,1094,  0,0,  0,0,  0,0,  1622,1664,  1855,1075,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {1863,1008,  1986,579,  1588,306,  1720,1094,  0,0,  0,0,  0,0,  1622,1664,  1855,1075,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {1863,1008,  1986,579,  1588,306,  533,1094,  0,0,  0,0,  0,0,  1622,1664,  1855,1075,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {1863,1008,  1986,579,  1588,306,  550,1094,  0,0,  0,0,  0,0,  1622,1664,  1855,1075,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {1863,1008,  1986,579,  1588,306,  920,891,  0,0,  0,0,  0,0,  1622,1664,  1855,1075,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {1919,265,  1478,608,  285,1480,  1272,288,  780,1450,  696,1008,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {1919,367,  1045,1042,  696,1875,  1434,1292,  285,511,  0,0,  0,0,  1272,1359,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {1938,1064,  786,1460,  0,0,  1828,1859,  0,0,  1232,91,  1538,1632,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {1951,1672,  386,0,  839,693,  0,0,  0,0,  1498,788,  703,1116,  1814,1543,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {329,1847,  1356,0,  0,0,  1501,221,  0,0,  499,1060,  0,0,  692,208,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {421,0,  703,1317,  0,0,  0,0,  1861,1016,  338,1806,  0,0,  0,0,  192,594,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {421,0,  703,144,  0,0,  0,0,  1861,1016,  338,1806,  0,0,  0,0,  192,594,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {421,0,  703,195,  0,0,  0,0,  1861,1016,  338,1806,  0,0,  0,0,  192,594,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {421,612,  703,1317,  0,0,  0,0,  1861,1016,  338,1806,  0,0,  0,0,  192,594,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {476,1195,  1819,374,  0,0,  0,0,  1919,367,  472,1359,  696,1263,  0,0,  0,0,  0,0,  0,0,  285,511,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {476,1195,  285,664,  0,0,  696,1263,  1919,367,  1272,1359,  0,0,  0,0,  0,0,  0,0,  0,0,  1926,782,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {476,685,  1019,17,  0,0,  696,1263,  1919,367,  472,1359,  0,0,  0,0,  0,0,  920,668,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {549,346,  1191,1481,  0,0,  1476,1094,  556,420,  633,782,  0,0,  0,0,  1434,659,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {549,346,  1191,1481,  0,0,  707,1094,  556,420,  633,782,  0,0,  0,0,  1434,659,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {612,849,  1491,972,  0,0,  1116,1803,  344,1488,  0,0,  455,497,  0,0,  1796,431,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {612,849,  1491,972,  0,0,  470,1803,  1814,1452,  0,0,  455,497,  227,1488,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {612,849,  1491,972,  1588,1488,  1116,1803,  0,0,  0,0,  455,497,  473,687,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {612,849,  1491,972,  1588,1488,  1116,1803,  1839,1910,  0,0,  455,497,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {620,1570,  0,0,  0,0,  0,0,  0,0,  891,1241,  1177,23,  1540,1692,  0,0,  1496,106,  1503,390,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {633,833,  1498,1155,  1188,1773,  0,0,  0,0,  277,1489,  0,0,  1995,278,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {692,208,  0,0,  1491,1773,  1881,237,  1232,1145,  1337,119,  121,459,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {695,1422,  0,0,  0,0,  1507,1056,  787,1664,  388,0,  1584,1825,  1491,943,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {695,1524,  0,0,  0,0,  1988,0,  780,695,  0,0,  788,91,  0,0,  0,0,  0,0,  0,0,  0,0,  1055,1848,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  1496,313,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {695,1524,  0,0,  1640,21,  0,0,  1300,818,  0,0,  1584,91,  1491,943,  0,0,  0,0,  0,0,  0,0,  1055,369,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {696,1008,  0,0,  0,0,  0,0,  1919,367,  1272,1359,  0,0,  0,0,  1819,374,  1045,991,  0,0,  285,511,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {696,1008,  0,0,  0,0,  0,0,  1919,367,  1272,1512,  920,668,  0,0,  1945,940,  0,0,  0,0,  1926,782,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {696,1008,  0,0,  1580,1195,  0,0,  1919,367,  1272,1359,  0,0,  0,0,  1434,659,  285,1480,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {696,1008,  0,0,  780,1042,  107,1037,  1919,367,  1272,1359,  0,0,  285,511,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {696,1008,  0,0,  780,1042,  285,103,  1919,367,  1272,1359,  0,0,  0,0,  1434,221,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {696,1008,  107,986,  1045,175,  0,0,  1919,367,  1272,1359,  0,0,  0,0,  0,0,  285,1480,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {696,1008,  1653,1802,  1045,1042,  0,0,  1919,367,  1272,1359,  0,0,  285,511,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {696,1008,  285,919,  1945,1195,  0,0,  1919,367,  472,1359,  0,0,  0,0,  1819,374,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {696,1008,  53,1088,  780,1042,  285,1480,  1919,367,  1272,900,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {696,1059,  0,0,  780,1042,  285,1888,  678,1343,  1272,900,  1919,367,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {696,1059,  0,0,  780,1042,  285,715,  0,0,  1272,1359,  1919,367,  0,0,  0,0,  0,0,  0,0,  1926,1730,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {696,1059,  1019,812,  780,940,  0,0,  0,0,  1272,1053,  1119,367,  0,0,  0,0,  0,0,  285,511,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {696,345,  285,511,  0,0,  0,0,  1919,775,  1272,1512,  0,0,  0,0,  1945,940,  0,0,  0,0,  1926,782,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {696,702,  285,511,  780,1042,  107,149,  0,0,  1272,1359,  1919,367,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {730,1328,  0,0,  338,802,  685,1293,  0,0,  1496,266,  1337,1563,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {773,1124,  0,0,  0,0,  1149,1920,  1639,221,  1498,788,  0,0,  779,365,  0,0,  473,958,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {773,1124,  352,1655,  0,0,  1149,1920,  1639,221,  329,1570,  0,0,  779,365,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {787,1409,  1019,1322,  603,888,  0,0,  1919,367,  0,0,  285,1480,  920,668,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {921,1400,  0,0,  0,0,  839,1710,  0,0,  0,0,  0,0,  1540,1692,  1273,1532,  1466,1854,  0,0,  471,1677,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  	
  	}
  	public static void runPool1000() {
  		runTestProgressive(new int[] {
  				98,1328,  1963,476,  1097,649,  0,0,  0,0,  169,1665,  1510,1755,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  1741,10,  0,0
			});
  		
  		runTestProgressive(new int[] {
  				544,226,  1505,1518,  1743,1897,  0,0,  0,0,  1692,1206,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  377,935,  0,0,  0,0,  0,0,  0,0
			});
  		
  	}
  	public static void runPool340_1206() {
  		runTestProgressive(new int[] {
  		  		0,0,  544,178,  997,1593,  879,1864,  0,0,  0,0,  1489,1158,  54,921,  0,0,  0,0,  0,0,  0,0, 
  		  		 0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0
			});
  	}
  	public static void runPool1206() {
  		/* bad zodiac matches.
  		 * observations:
  		 *  - their trigram stats are surprisingly better than that of the good zodiac match (zodiacmatch = 0.296).
  		 *  - doesn't matter whether or not we enforce uniqueness.
  		 *  - #nodes (pre conflict) is always less than #nodes of the good zodiac match.
  		 *  - numconflicts is always less than num conflicts of the good zodiac match.
  		 *  */
  		System.out.println("- bad matches");
  		runTestProgressive(new int[] {0,0,  0,0,  998,1206,  1869,884,  0,0,  926,531,  132,1213,  0,0,  0,0,  915,1745,  349,1634,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {0,0,  0,0,  998,1206,  1869,884,  1584,1876,  926,531,  0,0,  0,0,  0,0,  915,1745,  349,1634,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {0,0,  0,0,  998,1206,  1869,884,  1681,1876,  926,531,  0,0,  0,0,  0,0,  915,1745,  349,1634,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {0,0,  1070,11,  489,1297,  1223,83,  0,0,  0,0,  1551,1583,  636,17,  0,0,  1068,1978,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {0,0,  1070,11,  489,1297,  1223,83,  0,0,  0,0,  1551,1583,  636,17,  0,0,  789,1978,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {0,0,  558,1978,  998,1206,  1869,884,  0,0,  926,531,  0,0,  0,0,  0,0,  915,1745,  349,1634,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {0,0,  707,652,  998,1206,  1869,884,  0,0,  926,531,  0,0,  0,0,  0,0,  915,1745,  349,1634,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {0,0,  992,1388,  998,1206,  1869,884,  0,0,  926,531,  132,1213,  0,0,  0,0,  0,0,  349,1634,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {1863,837,  1547,410,  116,1438,  1270,1519,  711,1359,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  1289,1241,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {913,776,  0,0,  998,1053,  1869,884,  0,0,  992,531,  132,1621,  0,0,  0,0,  0,0,  349,1634,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {966,776,  1187,1264,  998,1206,  1869,884,  0,0,  926,531,  0,0,  0,0,  0,0,  0,0,  349,1634,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {971,776,  0,0,  998,1206,  1869,1190,  514,1111,  926,531,  0,0,  0,0,  0,0,  0,0,  349,1634,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {971,776,  0,0,  998,1206,  1869,884,  0,0,  926,531,  132,1213,  0,0,  0,0,  0,0,  349,1634,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {971,776,  0,0,  998,1206,  1869,884,  1925,1213,  926,531,  0,0,  0,0,  0,0,  0,0,  349,1634,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {971,776,  0,0,  998,1206,  1869,884,  336,1519,  926,531,  0,0,  0,0,  0,0,  0,0,  349,1634,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {971,776,  1187,1264,  998,1206,  1869,884,  0,0,  926,531,  0,0,  0,0,  0,0,  0,0,  349,1634,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {971,776,  1505,1264,  998,1206,  1869,884,  0,0,  926,531,  0,0,  0,0,  0,0,  0,0,  349,1634,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {971,776,  1715,1264,  998,1206,  1869,884,  0,0,  926,531,  0,0,  0,0,  0,0,  0,0,  349,1634,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {971,776,  405,1264,  998,1206,  1869,884,  0,0,  926,531,  0,0,  0,0,  0,0,  0,0,  349,1634,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {971,776,  507,1264,  998,1206,  1869,884,  0,0,  926,531,  0,0,  0,0,  0,0,  0,0,  349,1634,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {971,776,  577,1264,  998,1206,  1869,884,  0,0,  926,531,  0,0,  0,0,  0,0,  0,0,  349,1634,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {992,521,  1388,1264,  998,1206,  1869,884,  0,0,  926,531,  0,0,  0,0,  0,0,  0,0,  349,1634,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {992,521,  1715,1264,  998,1206,  1869,884,  0,0,  926,531,  0,0,  0,0,  0,0,  0,0,  349,1634,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {992,521,  1862,1264,  998,1206,  1869,884,  0,0,  926,531,  0,0,  0,0,  0,0,  0,0,  349,1634,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {996,776,  0,0,  998,1053,  0,0,  0,0,  926,531,  132,1621,  0,0,  0,0,  1869,1598,  349,869,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		runTestProgressive(new int[] {998,1751,  339,461,  0,0,  1790,1053,  0,0,  712,1387,  1088,177,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  1969,40,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0});
  		System.out.println("one good zodiac match with similar fit0 and lower fit1");
  		runTestProgressive(new int[] {1169,1802,  586,1400,  437,1920,  947,112,  0,0,  1151,1583,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  257,1058,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0  });
  		System.out.println("experiment 455: one with relatively high result but bad zodiac match");
  		runTestProgressive(new int[] {1259,1705,  903,138,  831,235,  350,1234,  1388,1751,  67,1672,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0  });
  		System.out.println("experiment 457: one with relatively high result but bad zodiac match");
  		runTestProgressive(new int[] {803,1673,  0,0,  82,1450,  1805,827,  0,0,  1551,1736,  0,0,  1769,1496  });
  		System.out.println("experiment 459: relatively high result but bad zodiac match");
  		runTestProgressive(new int[] {100,1366,  1695,389,  1047,569,  1259,971,  1779,1502,  0,0,  380,189,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  663,527,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0  });
  		runTestProgressive(new int[] {0,0,  182,1131,  1419,686,  0,0,  0,0,  1482,1563,  0,0,  1132,272,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  1046,1262  });
  		
  		
  	}

  	
  	public static CipherWordGene runTestProgressive(int[] g) {
  		return runTestProgressive(g, true);
  	}
  	
  	public static CipherWordGene runTestProgressive(int[] g, boolean debug) {
  		ZodiacDictionary.checkDictionary();
  		CipherWordGene gene = new CipherWordGene();
  		gene.conflictInit();

  		String decodersAdded[] = new String[g.length];
  		String decodedsAdded[] = new String[g.length];
  		gene.genome = new int[g.length];
  		//System.out.println("putting...");
  		for (int i=0; i<g.length; i+=2) {
  			if (gene.allelePut(i, g[i], g[i+1])) {
  				//System.out.println("accepted " + getWordFromAllele(g[i]) + " at pos " + getPositionFromAllele(g[i+1]));
  			} else {
  				//System.out.println("rejected " + getWordFromAllele(g[i]) + " at pos " + getPositionFromAllele(g[i+1]));
  			}
  			decodersAdded[i] = gene.decoder.toString();
  			decodedsAdded[i] = gene.decoded.toString();
  			//System.out.println("decoder " + gene.decoder + " decoded " + gene.decoded);
  		}
  		gene.fitness();
  		gene.fitnessNGramFreq();
  		if (debug) System.out.println("from adding " + gene.getDebugLine());
  		//System.out.println("removing...");
  		
  		
  		/*for (int i=g.length-2; i>=0; i-=2) {
  			gene.allelePut(i, 0, 0);
  			//System.out.println("decoder " + gene.decoder + " decoded " + gene.decoded);
  			if (i-2>=0 && !gene.decoder.toString().equals(decodersAdded[i-2])) {
  				System.out.println("ERROR; decoders should be the same.");
  				System.out.println(" -   added decoder: " + decodersAdded[i-2]);
  				System.out.println(" - removed decoder: " + gene.decoder.toString());
  				System.exit(-1);
  			}
  			if (i-2>=0 && !gene.decoded.toString().equals(decodedsAdded[i-2])) {
  				System.out.println("ERROR; decodeds should be the same.");
  				System.out.println(" -   added decoded: " + decodedsAdded[i-2]);
  				System.out.println(" - removed decoded: " + gene.decoded.toString());
  			}
  		}*/
  		//gene.fitness();
  		//gene.fitnessNGramFreq();
  		//if (gene.hasConflict(gene.genome)) System.out.println(" -- THIS GENE STILL HAS A CONFLICT.  SHOULD NOT HAPPEN.");
  		//System.out.println("from removing " + gene.getDebugLine());
  		return gene;
  	}

  	/** returns csv of fitness computations on the given genome */
  	public static String getMetricsFor(int[] g) {

  		initAllPools();
  		if (wordPoolHash == null) initWordPoolHash();
  		
  		
  		String metrics = "";
  		ZodiacDictionary.checkDictionary();
  		CipherWordGene gene = new CipherWordGene();
  		gene.conflictInit();

  		gene.genome = new int[g.length];
  		//System.out.println("putting...");
  		for (int i=0; i<g.length; i+=2) {
  			gene.allelePut(i, g[i], g[i+1]);
  		}
  		
			
			/*metrics += gene.fitnessUniqueness(1);
			for (int i=2; i<=5; i++) metrics += ", " + gene.fitnessUniqueness(i);
			for (int i=1; i<=5; i++) metrics += ", " + gene.fitnessUniquenessCount(i);
			for (int i=1; i<=5; i++) metrics += ", " + gene.fitnessUniquenessOther(i);
			for (int i=1; i<=5; i++) {
				metrics += ", " + gene.fitnessSubstring(i, true, gene.decoded.toString(), substringPool3, substringPrefixesPool3, wordPool, gene.decoder.toString(), false, 3);
				metrics += ", " + gene.fitnessSubstring(i, false, gene.decoded.toString(), substringPool3, substringPrefixesPool3, wordPool, gene.decoder.toString(), false, 3);
				metrics += ", " + gene.fitnessSubstring(i, true, gene.decoded.toString(), substringPool3, substringPrefixesPool3, wordPool, gene.decoder.toString(), true, 3);
				metrics += ", " + gene.fitnessSubstring(i, false, gene.decoded.toString(), substringPool3, substringPrefixesPool3, wordPool, gene.decoder.toString(), true, 3);
			}
			for (int i=1; i<=5; i++) gene.fitnessNGramFreq(i);
			for (int i=1; i<=5; i++) {
				metrics += ", " + gene.fitnessNGrams[i];
			}
			
			gene.phiTest();
			metrics += ", " + gene.phiRandomMatch;
			metrics += ", " + gene.phiPlaintextMatch;*/
  		
			/*metrics += gene.fitnessSubstring(1, true, gene.decoded.toString(), substringPool5, substringPrefixesPool5, wordPool, gene.decoder.toString(), false, 5);
			metrics += ", " + gene.fitnessSubstring(1, true, gene.decoded.toString(), substringPool4, substringPrefixesPool4, wordPool, gene.decoder.toString(), false, 4);
			metrics += ", " + gene.fitnessSubstring(1, true, gene.decoded.toString(), substringPool3, substringPrefixesPool3, wordPool, gene.decoder.toString(), false, 3);

			metrics += ", " + gene.fitnessSubstring(1, true, gene.decoded.toString(), substringPool5, substringPrefixesPool5, wordPool, gene.decoder.toString(), true, 5);
			metrics += ", " + gene.fitnessSubstring(1, true, gene.decoded.toString(), substringPool4, substringPrefixesPool4, wordPool, gene.decoder.toString(), true, 4);
			metrics += ", " + gene.fitnessSubstring(1, true, gene.decoded.toString(), substringPool3, substringPrefixesPool4, wordPool, gene.decoder.toString(), true, 3);
				*/
  		
  			gene.fitness();
  		
			//fit.multifitness[0] = fitnessSubstring(true, decode(), substringPool, wordPool);
			gene.zodiacScore();
			
			MultiObjectiveFitness mfit = (MultiObjectiveFitness) gene.fitness;
			//for (int i=0; i<mfit.multifitness.length; i++) {
			//	metrics += (i > 0 ? "," : "") + mfit.multifitness[i];
			//}
			
			metrics += "," + gene.zodiacMatch; 
			//fit.multifitness[0] = fitnessUniqueness(4);
			//fit.multifitness[1] = fitnessSubstring(true, decode(), substringPool, wordPool);
			
			//fit.multifitness[0] = fitnessUniqueness(4);
			//fit.multifitness[0] = fitnessUniquenessOther(4);
			//fit.multifitness[0] = (float)zodiacMatch;
			//fit.multifitness[1] = (float)zodiacMatch;
			//StringBuffer d = new StringBuffer(decode());
			//d.delete(CIPHER_START, CIPHER_END+1);
			//fit.multifitness[2] = fitnessSubstringGreedy(1, true, decode(), substringPool, wordPool, decoder);
			//fit.multifitness[0] = fitnessSubstring(1, true, decoded.toString(), substringPool, substringPrefixesPool, wordPool, decoder.toString(), false);
			
			//fit.multifitness[0] = fitnessUniqueness(4) * fitnessUniquenessOther(4,10) * fitnessSubstring(1, true, decode(), substringPool, wordPool);
			//fit.multifitness[0] = fitnessSubstringGreedy(1, true, decode(), substringPool, wordPool, decoder);
			/*int f1 = fitnessUniqueness(4);
			int f2 = fitnessUniquenessOther(4,10);
			int f3 = fitnessSubstring(1, true, decode(), substringPool, wordPool);
			float mean = (f1+f2+f3)/3;
			fit.multifitness[0] = mean;
			fit.multifitness[1] = 100-(Math.abs(f1-mean)+Math.abs(f2-mean)+Math.abs(f3-mean));*/
			
			//fit.multifitness[1] = fitnessUniquenessOther(4,10);
			//fit.multifitness[1] = fitnessSubstring(true, decode(), substringZodDictPool, top40Pool);
			//fit.multifitness[0] = (float)this.fitnessNGramFreq(1);
			//fit.multifitness[0] = (float)zodiacMatch;
  		
  		//gene.fitnessNGramFreq();
  		return metrics;
  	}
  	
  	/** initializes the stats map */
  	public static void statsInit() {
  		stats = new THashMap<String, Float>();
  	}
  	
  	/** dumps all stats */
  	public static String statsDump() {
  		String result = "";
  		TreeSet<String> sorted = new TreeSet<String>(stats.keySet());
  		if (sorted != null)
  			for (String k : sorted)
  				result += k + "," + stats.get(k) + ",";
  		return result;
  	}
  	/** increment the given stat.  not yet threadsafe. */
  	public static void statsInc(String key) {
  		statsInc(key, 1);
  	}
  	/** increment the given stat by the given amount.  not yet threadsafe. 
  	 * 
  	 * Exception in thread "Thread-304" java.lang.IllegalArgumentException: Equal objects must have equal hashcodes. During rehashing, Trove discovered that the following two objects claim to be equal (as in java.lang.Object.equals()) but their hashCodes (or those calculated by your TObjectHashingStrategy) are not equal.This violates the general contract of java.lang.Object.hashCode().  See bullet point two in that method's documentation. object #1 =defaultCrossoverPTWorseThanBothParents; object #2 =defaultCrossoverPTWorseThanBothParents
	at gnu.trove.TObjectHash.throwObjectContractViolation(TObjectHash.java:328)
	at gnu.trove.THashMap.rehash(THashMap.java:365)
	at gnu.trove.THash.postInsertHook(THash.java:359)
	at gnu.trove.THashMap.put(THashMap.java:172)
	at org.oranchak.CipherWordGene.statsInc(CipherWordGene.java:5130)
	at org.oranchak.CipherWordGene.defaultMutate(CipherWordGene.java:2131)
	at ec.vector.breed.VectorMutationPipeline.produce(VectorMutationPipeline.java:73)
	at ec.simple.SimpleBreeder.breedPopChunk(SimpleBreeder.java:179)
	at ec.simple.SimpleBreederThread.run(SimpleBreeder.java:248)
	at java.lang.Thread.run(Thread.java:613)
Exception in thread "main" java.lang.NullPointerException
	at org.oranchak.ZodiacESStatistics.printGenomeDiversity(ZodiacESStatistics.java:294)
	at org.oranchak.ZodiacESStatistics.postBreedingStatistics(ZodiacESStatistics.java:35)
	at ec.simple.SimpleEvolutionState.evolve(SimpleEvolutionState.java:120)
	at ec.EvolutionState.run(EvolutionState.java:388)
	at ec.Evolve.main(Evolve.java:599)

  	 * 
  	 * 
  	 * 
  	 * */
  	public static void statsInc(String key, float amount) {
  		if (stats == null) statsInit();
  	  stats.put(key, stats.get(key) == null ? amount : stats.get(key) + amount);
  	}
  	
  	public static void testTHashMapSpeed() {
  		
  		/* conclusions:
  		 * 
  		 *  - map inits+puts with big enough initial capacities are faster
  		 *  - 
  		 */
  		
  		THashMap<String, Integer> map1;
  		//THashMap<String, Integer> map2;
  		Date d1;
  		Date d2;
  		Date d3; 
  		Date d4;
  		
  		int NUM = 1000;
  		int SIZE = 1000;
  		//int ITERATIONS = 100;

  		/* init+put test */
  		d1 = new Date();
  		for (int i=0; i<NUM; i++) {
  			map1 = new THashMap<String,Integer>();
  			for (int j=0; j<SIZE; j++) {
  				map1.put(""+j, j);
  			}
  		}
  		d2 = new Date();
  		System.out.println((d2.getTime() - d1.getTime()) + " ms");

  		d1 = new Date();
  		for (int i=0; i<NUM; i++) {
  			map1 = new THashMap<String,Integer>(SIZE);
  			for (int j=0; j<SIZE; j++) {
  				map1.put(""+j, j);
  			}
  		}
  		d2 = new Date();
  		System.out.println((d2.getTime() - d1.getTime()) + " ms");
  		
  		/* get test */
			map1 = new THashMap<String,Integer>();
			for (int j=0; j<SIZE; j++) {
  				map1.put(""+j, j);
			}
			int blah;
			d1 = new Date();
  		for (int i=0; i<NUM*NUM; i++) {
  			blah = map1.get(""+(i % SIZE));
  		}
  		d2 = new Date();
  		System.out.println((d2.getTime() - d1.getTime()) + " ms");
  		

			map1 = new THashMap<String,Integer>(SIZE);
			for (int j=0; j<SIZE; j++) {
  				map1.put(""+j, j);
			}
			d1 = new Date();
  		for (int i=0; i<NUM*NUM; i++) {
  			blah = map1.get(""+(i % SIZE));
  		}
  		d2 = new Date();
  		System.out.println((d2.getTime() - d1.getTime()) + " ms");
  		
  		
  	}
  	
  	public static void testWildcards() {
  		String word = "testes";
  		List<String> result = getWildPermutations(word);
  		for (int i=0; i<result.size(); i++) {
  			System.out.println(result.get(i));
  		}
  		
  	}
  	
  	
  	/*
  	@Override
		public Object clone() {
			// TODO Auto-generated method stub
  		System.out.println(id + ": clone() called");
			Object result = super.clone();
			for (int i=0; i<genome.length; i++) {
				if (genome[i] != ((CipherWordGene)result).genome[i]) throw new RuntimeException("fucking shit ass");
			}
			return result;
		}*/

		public static void testFeasiblePrefix() {
  		ZodiacDictionary.initDictionaries();
  		String word;
  		
  		word = "hrrtime";
  		System.out.println(word + ":" + feasiblePrefix(word));
  		word = "timehrr";
  		System.out.println(word + ":" + feasiblePrefix(word));
  		word = "fart";
  		System.out.println(word + ":" + feasiblePrefix(word));
  		word = "shit";
  		System.out.println(word + ":" + feasiblePrefix(word));
  		word = "????ohyes";
  		System.out.println(word + ":" + feasiblePrefix(word));
  		word = "????imeyes";
  		System.out.println(word + ":" + feasiblePrefix(word));
  		word = "????esyes";
  		System.out.println(word + ":" + feasiblePrefix(word));
  	}
		
		public static void testCountAllFeasibilePlacements() {
			int count = 0;
			int total = 0;
			int w = wordPool.length;
			
			CipherWordGene gene = new CipherWordGene();
			gene.conflictInit();
			gene.genome = new int[2];
			for (int i=0; i<wordPool.length; i++) {
				for (int j=0; j<51; j++) {
					if (gene.allelePut(0,i,j)) {
						count++;
						gene.alleleRemove(0);
					}
					total++;
				}
			}
			System.out.println(count + " out of " + total + " positions worked.  Ratio: " + ((float) count/total));
			
		}
		
		/** randomly poke bits of the real solution into a bunch of genes, just to make sure allelePut does not
		 * indicate any false negatives.
		 */
		public static void testRealSolutionBits() {
  		int w = wordPool.length;
  		//CipherWordGene gene = new CipherWordGene();
  		//"become","my","slaves","i","will","not","give","you","my","name","because","you","will","the", "i", "to", "of", "a", "in", "you", "and", "my", "is", "was", "have", "this", "it", "that", "with", "me", "for", "be", "will", "on", "not", "them", "by", "all", "who", "her", "they", "shall", "if", "but", "am", "about", "up", "she", "do", "your", "out", "one", "when", "then", "zodiac", "people", "like", "would", "some", "as", "are", "so", "police", "missed", "did", "car", "back", "speaking", "there", "or", "no", "last", "because", "at", "were", "time", "thing", "more", "from", "can", "never", "killing", "just", "down", "bomb", "way", "very", "vallejo", "think", "their", "slaves", "see", "s", "rather", "over", "off", "now", "nice", "list", "im", "had", "get", "editor", "could", "buttons", "been", "what", "tell", "school", "san", "park", "others", "only"
  		int[] solution = 
	  		new int[] {  //llbecomemyslavesiwillnotgiveyoumynamebecauseyouwill
	  				getAlleleFromWord("become")+w, 2,
	  				getAlleleFromWord("my")+w, 8,
	  				getAlleleFromWord("slaves")+w, 10,
	  				getAlleleFromWord("i")+w, 16,
	  				getAlleleFromWord("will")+w, 17,
	  				getAlleleFromWord("not")+w, 21,
	  				getAlleleFromWord("give")+w, 24,
	  				getAlleleFromWord("you")+w, 28,
	  				getAlleleFromWord("my")+w, 31,
	  				getAlleleFromWord("name")+w, 33,
	  				getAlleleFromWord("because")+w, 37,
	  				getAlleleFromWord("you")+w, 44,
	  				getAlleleFromWord("will")+w, 47
	  		};
  		
  		int pop = 1000;
  		int iterations = 10000;
  		CipherWordGene[] genes = new CipherWordGene[pop];
  		for (int i=0; i<genes.length; i++) {
  			genes[i] = new CipherWordGene();
  			genes[i].genome = new int[50];
  			genes[i].conflictInit();
  		}
  		
  		ZodiacDictionary.makeIsWordHash();
  		
  		int whichGene;
  		int whichAllele;
  		int whichSolution;
  		
  		String word;
  		int position;
  		int ww;
  		boolean go;
  		Date start = new Date();
  		int putCount = 0;
  		for (int i=0; i<iterations; i++) {
  			if (i % (iterations/10) == 0)
  				System.out.println(100*((float)i/iterations));
  				
  			go = true;
  			whichGene = rand().nextInt(genes.length);
  			whichAllele = rand().nextInt(25);
  			whichSolution = rand().nextInt(13);
  			ww = solution[whichSolution*2];
  			if (rand().nextBoolean(0.40))
  				ww=0; // 40% of the time, reset an allele.
				word = getWordFromAllele(ww);
				position = getPositionFromAllele(solution[whichSolution*2+1]);
  			
  			for (int j=0; j<genes[whichGene].genome.length; j+=2) {
  				if (!"".equals(word) &&
  						getWordFromAllele(genes[whichGene].genome[j]).equals(word) &&
  						getPositionFromAllele(genes[whichGene].genome[j+1]) == position) {
  					go = false;
  				}
  				if (genes[whichGene].conflictWordHash.contains(word))
  					go = false;
  			}
  			if (go) {
  				if (genes[whichGene].allelePut(whichAllele*2, ww, solution[whichSolution*2+1])) {
  					//System.out.println(genes[whichGene].decoded.substring(CIPHER_START, CIPHER_END+1));
  					genes[whichGene].fitness();
  				} else {
  					System.out.println("allelePut(" + whichAllele*2+","+ww+","+solution[whichSolution*2+1]+") was rejected.  (" + word + "," + position + ")");
  					System.out.println(genes[whichGene].getDebugLine());
  					System.exit(-1);
  				}
  				putCount++;
  			}
  		}
  		Date end = new Date();
  		long diff = end.getTime() - start.getTime();
  		System.out.println("put " + putCount + " times in " + diff + " ms (" + (1000*putCount/diff) + "/s)");
		}
		
		public static void testFitnessGaps() {

  		int w = wordPool.length;
  		//CipherWordGene gene = new CipherWordGene();
  		//"become","my","slaves","i","will","not","give","you","my","name","because","you","will","the", "i", "to", "of", "a", "in", "you", "and", "my", "is", "was", "have", "this", "it", "that", "with", "me", "for", "be", "will", "on", "not", "them", "by", "all", "who", "her", "they", "shall", "if", "but", "am", "about", "up", "she", "do", "your", "out", "one", "when", "then", "zodiac", "people", "like", "would", "some", "as", "are", "so", "police", "missed", "did", "car", "back", "speaking", "there", "or", "no", "last", "because", "at", "were", "time", "thing", "more", "from", "can", "never", "killing", "just", "down", "bomb", "way", "very", "vallejo", "think", "their", "slaves", "see", "s", "rather", "over", "off", "now", "nice", "list", "im", "had", "get", "editor", "could", "buttons", "been", "what", "tell", "school", "san", "park", "others", "only"
  		runTestProgressive(new int[] {  //llbecomemyslavesiwillnotgiveyoumynamebecauseyouwill
  				getAlleleFromWord("become")+w, 2,
  				getAlleleFromWord("my")+w, 8,
  				getAlleleFromWord("slaves")+w, 10,
  				getAlleleFromWord("i")+w, 16,
  				getAlleleFromWord("will")+w, 17,
  				getAlleleFromWord("not")+w, 21,
  				getAlleleFromWord("give")+w, 24,
  				getAlleleFromWord("you")+w, 28,
  				getAlleleFromWord("my")+w, 31,
  				getAlleleFromWord("name")+w, 33,
  				getAlleleFromWord("because")+w, 37,
  				getAlleleFromWord("you")+w, 44,
  				getAlleleFromWord("will")+w, 47
  		});

  		runTestProgressive(new int[] {
  				242,324,  0,0,  0,0,  0,0,  0,0,  599,308
  		});
  		
  		runTestProgressive(new int[] {
  	  		117,286,  0,0,  0,0,  253,291,  0,0,  480,316,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  479,309,  0,0,  0,0,  0,0,  0,0,  289,320,  0,0,  567,295,  355,325
  		});

  		runTestProgressive(new int[] {
  				487,288,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  984,221,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0
  		});
  		
  		runTestProgressive(new int[] {
  				487,288,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  384,323,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  605,286,  0,0,  0,0,  0,0,  0,0,  0,0
  		});
  		
  		runTestProgressive(new int[] {
  				585,293,  0,0,  0,0,  100,255,  139,247,  0,0,  0,0,  526,310,  0,0,  403,236,  0,0,  0,0,  365,321,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  395,330,  0,0,  0,0,  496,275
  		});
  		
  		
  		int RUNS = 300;
  		int[] genome = new int[50];
  		Date d1 = new Date();
  		for (int i=0; i<RUNS; i++) {
  			genome[CipherWordGene.rand().nextInt(50)] = CipherWordGene.rand().nextInt(maxAllele);
  			runTestProgressive(genome, false);
  		}
  		long diff = new Date().getTime() - d1.getTime();
  		System.out.println("endurance test: " + diff + " ms (" + (long)(1000*RUNS/diff) + " per second)");
  		
			
		}
	
		/* given the cipher substring, locates other min-length chunks that have the same set of cipher symbols */
		public static void findSimilarChunks() {
			THashSet<Character> symbols;
			
			char ch; String cipher = Zodiac.cipher[Zodiac.CIPHER];
			String found;
			System.out.println(cipher.substring(CIPHER_START, CIPHER_END+1));
			for (int i=0; i<cipher.length(); i++) {
				symbols = new THashSet<Character>();
				for (int j=CIPHER_START; j<CIPHER_END+1; j++)
					symbols.add(cipher.charAt(j));

				for (int j=i+1; j<Zodiac.cipher[Zodiac.CIPHER].length(); j++) {
					ch = cipher.charAt(j);
					if (symbols.contains(ch))
						symbols.remove(ch);
					if (symbols.size() == 0) {
						found = cipher.substring(i, j);
						System.out.println("start " + i + " end " + j + " len " + found.length() + ", " + found);
						break;
					}
				}
			}
			
			
		}
		
		/** return a count of all non-wildcard letters in the decoded plaintext */
		public int countNonWildcardsPlaintext() {
			return countNonWildcards(decoded.substring(CIPHER_START, CIPHER_END+1));
		}
		
		/** return a count of all non-wildcard letters in the conflictOverlap */
		public int countNonWildcardsConflictOverlap() {
			return countNonWildcards(conflictOverlap.substring(CIPHER_START, CIPHER_END+1));
		}
		
		/** return a count of all non-wildcard letters in the conflictOverlap, and includes any feasible wildcard chunk in the count. */
		public int countNonWildcardsConflictOverlapFeasible() {
			int result = countNonWildcardsConflictOverlap();
			String[] wilds = conflictOverlap.substring(CIPHER_START, CIPHER_END+1).split("_+");
			for (String w : wilds)
				if (w.length() <= DAC_MAX_LEN)
					result += w.length();
					
			return result;
		}
		
		/** return a count of all non-wildcard letters in the given string */
		public static int countNonWildcards(String str) {
			int result = 0;
			for (int i=0; i<str.length(); i++) {
				if (str.charAt(i) != '?') result++;
			}
			return result;
		}
		
		/** allelePut was rejecting these for some reason */
		public static void testRejectedAlleles() {
			CipherWordGene gene = runTestProgressive(new int[] {
					//0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  614,17,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0
					0,0,  0,0,  0,0,  0,0,  0,0,  603,8,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0

			});
			System.out.println(gene.allelePut(28,603,8)); // trying to put word "you" at position 330, which is this plaintext: "y?o".
		}
  	
		/** test to recreate errors such as "Could not clone the other parent when trying allelePut(48,421,801).  This should NEVER happen!" */
		public static void testCloneBug() {
			ZodiacDictionary.USE_ISWORDHASH = true;
	  	int[] g1 = new int[] {
	  			272,875,  423,196,  0,0,  0,0,  260,482,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0
	  	};
	  	
	  	int[] g2 = new int[] {
	  			470,428,  405,623,  0,0,  174,415,  0,0,  0,0,  840,629,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  421,801
	  	};
	  	
	  	//runTestProgressive(g1);
	  	runTestProgressive(g2);
	  	
			
		}
		
		/** a random trial and error explorer */
		public static void testTrialAndError() {
			ZodiacDictionary.makeIsWordHash();
			CipherWordGene gene = new CipherWordGene();
			boolean go;
			int index = 0;
			ArrayList<Object[]> choices;
			Object[] choice;
			String cipher = Zodiac.cipher[Zodiac.CIPHER];
			String chunkC, chunkP, chunkO;
			
			boolean GREEDY = true; /* if true, always pick from the best word placements.  best = those which produce highest resulting fitness. */
			float[] bestFitnesses;
			ArrayList[] bestChoices;
			
			int word, pos;
			THashSet<String> found;
			while (true) {
				go = true;
				gene.conflictInit();
				gene.genome = new int[50];
				index = 0;
				while (go) {
					bestFitnesses = new float[NUM_OBJECTIVES];
					bestChoices = new ArrayList[NUM_OBJECTIVES];
					for (int x=0; x<NUM_OBJECTIVES; x++)
						bestChoices[x] = new ArrayList<Object[]>(); 
					/* get list of all possible word placements */
					choices = new ArrayList<Object[]>();
					for (int i=CIPHER_START; i<CIPHER_END - ALLELE_ADD_MIN_WORD_LENGTH + 1; i++) {
						for (int j=ALLELE_ADD_MIN_WORD_LENGTH; j<=ZodiacDictionary.wordConstraintDatabaseMaxLength && (i+j<=CIPHER_END); j++) {
							chunkC = cipher.substring(i, i+j);
							chunkP = gene.decoded.substring(i, i+j);
							chunkO = gene.conflictOverlap.substring(i, i+j);
							if (!chunkO.contains("_")) {
								//System.out.println("C " + chunkC + " P " + chunkP);
								found = ZodiacDictionary.getWordsConstrainedBy(ZodiacDictionary.wordConstraintDatabase, chunkC, chunkP);
								if (found != null && found.size() > 0) {
									for (String w : found) {
										word = gene.getAlleleFromWord(w); 
										pos = i-CIPHER_START;
										if (gene.allelePut(index, word, pos)) {
											choice = new Object[] {i-CIPHER_START,w};
											if (GREEDY) {
												gene.fitness(); 
												for (int x=0; x<NUM_OBJECTIVES; x++) { // check if any of the objectives are equal or better.
													//if (((MultiObjectiveFitness)gene.fitness).multifitness[x] > bestFitnesses[x]) {
													if (false) {
														/* outperformed.  reset the choices. */
														//bestFitnesses[x] = ((MultiObjectiveFitness)gene.fitness).multifitness[x];
														bestChoices[x] = new ArrayList<Object[]>();
														bestChoices[x].add(choice);
													//} else if (((MultiObjectiveFitness)gene.fitness).multifitness[x] == bestFitnesses[x]) {
														/* same fitness, so add it to the list of choices. */
													//	bestChoices[x].add(choice);
													//} // otherwise, ignore this placement because it will make fitness worse.
													}
												}
												
											} else {
												choices.add(choice);
											}
											gene.alleleRemove(index);
										}
									}
								}
							}
						}
					}
					for (int x=0; x<NUM_OBJECTIVES; x++)
						choices.addAll(bestChoices[x]);
					
					/*for (Object[] o : choices) {
						System.out.println(o[0] + "," + o[1]);
					}*/
					/* if the list is empty, then we've reached the dead end.  compute the score and break. */
					if (choices.size() == 0) {
						go = false;
						gene.fitness();
						System.out.println("DEAD END: " + gene.getDebugLine());
						break;
					}
					
					/* otherwise, pick a random word from the list to use and keep going.*/
					choice = choices.get(rand().nextInt(choices.size()));
					if (!gene.allelePut(index, getAlleleFromWord((String)choice[1]), (Integer)choice[0])) {
						System.err.println("wtf? rejected " + index + ", " + getAlleleFromWord((String)choice[1]) + "(" + choice[1] + "), " + choice[0]);
					}
					//System.out.println(index + " - " + choices.size() + " choices remaining. " + gene.getDebugLine());
					index += 2;
				}
				
			}
		}
		
		public static void testSpeed() {
			/*
			int[][] genomes = new int[][] { {3463,0,  4140,7,  3299,11,  3421,15,  3279,20,  2901,27,  3432,37,  3577,42,  3100,46},
					{2302,959,  1441,47,  642,1129,  0,0,  111,27,  2182,42,  1904,11,  1353,0,  0,0,  0,0,  0,0,  0,0,  0,0,  1972,891,  0,0,  631,639,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0},
					{1904,791,  2414,32,  642,1493,  0,0,  756,1171,  2182,42,  0,0,  1353,0,  0,0,  122,23,  0,0,  0,0,  0,0,  1972,891,  0,0,  631,639},
					{1884,384,  1100,1592,  2037,1181,  1904,11,  1361,27,  2182,42,  1705,46,  1353,0,  0,0,  0,0,  0,0,  0,0,  631,639,  1235,891,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0}
			};*/
			int[][] genomes = new int[][] {
					{873,1758,  2261,46,  2165,11,  742,364,  0,0,  1106,1635,  3141,7,  0,0,  0,0,  0,0,  1724,1015,  1361,329,  0,0,  0,0,  2306,37,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0},
					{2471,42,  1949,46,  2165,1467,  742,0,  1724,1015,  0,0,  3141,579,  2704,23,  0,0,  0,0,  0,0,  0,0,  1374,17,  0,0,  2306,37,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0}
			};
			
			
  		ZodiacDictionary.USE_ISWORDHASH = false;
  		ZodiacDictionary.initDictionaries();
  		ZodiacDictionary.makeWordConstraintDatabase(wordPool);
  		initAllPools();
  		int STEPS = 100;
			CipherWordGene[] genes = new CipherWordGene[genomes.length];
			int w, p, index;
			for (int i=0; i<genes.length; i++) {
				genes[i] = new CipherWordGene();
				genes[i].conflictInit();
				genes[i].genome = new int[50];
				index = 0;
				for (int j=0; j<genomes[i].length; j+=2) {
					genes[i].allelePut(index, genomes[i][j], genomes[i][j+1]);
					index+=2;
				}
				/*genes[i].genome = new int[50];
				for (int j=0; j<1000; j++) {
					w = rand().nextInt(1000);
					p = rand().nextInt(1000);
					if (getWordFromAllele(w).length() > 0) {
						if (genes[i].allelePut(index, w, p))
							index += 2;
					}
				}*/
				//System.out.println(i + ": " + index + ", " + genomeToString(genes[i].genome));
			}

			for (int j=0; j<5; j++) {
				Date start = new Date();
				for (int i=0; i<STEPS; i++) {
					index = rand().nextInt(genes.length);
					genes[index].fitness();
					//System.out.println(genes[index].getDebugLine());
				}
				Date end = new Date();
				long diff = end.getTime() - start.getTime();
				System.out.println("took " + diff + " ms (" + (1000*(float)STEPS/diff) + "/s)");
			}
		}
		
	  public static void main(String[] args) {
	  	//testWildcards();
	  	//testMinConflict();
	  	//testGene2();
	  	//testArraySpeed();
	  	//testConflictProgressive();
	  	//testTrialAndError();
	  	//testFormat();
	  	testConflictProgressive2();
		  //countCipherSymbols();
		  //findSequences();
	  	//testSpeed();
	  	//testBruteConflict();
	  	/*String test = "??_____________________________??___________???????";
			String[] stuff = test.split("_+");
			for (String s : stuff)
				System.out.println(s);
*/
	  	//testCloneBug();
	  	//testFitnessGaps();
	  	//findSimilarChunks();
	  	//testRealSolutionBits();
	  	//testCloning();
	  	System.out.println(Zodiac.cipher[Zodiac.CIPHER].substring(CIPHER_START, CIPHER_END+1));
	  	System.out.println(Zodiac.firstCipherDecoded.substring(CIPHER_START, CIPHER_END+1));
	  	
	  	//countCipherSymbols();
	  	//testRejectedAlleles();
	  	//System.out.println(getWordFromAllele(601));
	  	//System.out.println(getPositionFromAllele(44));
	  	//testCountAllFeasibilePlacements();
	  	//generateMetrics();
	  	//testFeasiblePrefix();
	  	//ZodiacDictionary.initDictionaries();
	  	//testGeneSpeed();
	  	//testTHashMapSpeed();
	  	//findRepeats();
	  	//testBrute9();
	  	/*String blah = "this____is_a_test";
	  	String[] split = blah.split("_+");
	  	for (int i=0; i<split.length; i++)
	  		System.out.println("split " + i + ": " + split[i]);
	  	
	  	System.out.println("thisisatestofcoolstuff".replaceAll(".", "_"));*/
	  	
	  	//testKeySorted();
	  	//testScaled();
	  	//testHamming();
	  	//computeAverages();
	  	//test51();
	  	//testWI();
	  	//initSubstringPool();
	  }
		public static void testFormat() {
			Float test = new Float(-12);
	  	DecimalFormat df = new DecimalFormat();
	  	df.applyPattern("################.00");
			System.out.println(df.format(test));
			
		}

		public static void testBruteConflict() {
			ZodiacDictionary.makeWordConstraintDatabase(wordPool);
			CipherWordGene gene = new CipherWordGene();
			gene.conflictInit();
			gene.genome = new int[50];
			testBruteConflict(gene, CIPHER_START, 0);
		}
		
		/* find the best fitness exhaustively with the given gene starting at the given ciphertext index.
		 * 
		 *  
		 *  */ 
		public static void testBruteConflict(CipherWordGene gene, int cipherIndex, int genomeIndex) {
			ZodiacDictionary.USE_ISWORDHASH = false;
			String plaintext, ciphertext;
			THashSet<String> words = new THashSet<String>();
			
			/* try all possible words at all positions starting at index, working our way to the end of the ciphertext */ 
			for (int i=cipherIndex; i<CIPHER_END; i++) {
				for (int len=5; len<=8 && len + i <= Zodiac.cipher[Zodiac.CIPHER].length(); len++) {
					plaintext = gene.decoded.substring(i, i+len);
					ciphertext = Zodiac.cipher[Zodiac.CIPHER].substring(i, i+len);
					words.addAll(ZodiacDictionary.getWordsConstrainedBy(ZodiacDictionary.wordConstraintDatabase, ciphertext, plaintext));
				}
				
				for (String w : words) {
					if (gene.allelePut(genomeIndex, getAlleleFromWord(w), i-CIPHER_START)) {
						//System.out.println(gene.decoded.substring(CIPHER_START, CIPHER_END));
						if (genomeIndex < 7) {
							System.out.println(genomeIndex + " #words " + words.size() + " word " + w + " pos " + i + " d " + gene.decoded.substring(CIPHER_START, CIPHER_END));
						}
						testBruteConflict(gene, cipherIndex + w.length(), genomeIndex+2);
						gene.alleleRemove(genomeIndex);
					}
				}
				
			}
			
		}
		
		/* find substring sequences that yield the highest number of decoded symbols 
		
		 *  best one for the 408:
			 *  
			 *  117,144,q!75LMr9#BPDR+$*6\N)eEUHkFZcpOVWI5+tL(l^R6HI9DR_TYr\de/@XJQAP5M8RUt%L(NVEKH*GrI!Jk597LMlNA(Z)PzUpkA9#BVW\+VTtOP^*Srlf,mostthrillingexperenceitisevenbetterthangettingyourrocksoffwithagirlthebestpartofitisthaewhenidieiwillbereborninparad,408
		*/
		public static void findSequences() {
			THashMap<Character, Integer> counts = new THashMap<Character, Integer>();
			String cipher = Zodiac.cipher[Zodiac.CIPHER];
			
			char c; int num;
			for (int i=0; i<cipher.length(); i++) {
				c = cipher.charAt(i);
				if (counts.get(c) == null)
					num = 0;
				else
					num = counts.get(c);
				
				num++;
				counts.put(c, num);
			}
			
			
			int best; String chunk; THashSet<Character> symbols;
			String bestChunk = ""; int bestPos = 0;
			for (int len=4; len <= 200; len++) {
				best = 0; num = 0;
				for (int i=0; i<=cipher.length()-len; i++) {
					chunk = cipher.substring(i, i+len);
					symbols = new THashSet<Character>();
					num = 0;
					for (int j=0; j<chunk.length(); j++) {
						//num += counts.get(chunk.charAt(j));
						symbols.add(chunk.charAt(j));
					}
					for (Character ch : symbols)
						num += counts.get(ch);
					
					if (num > best) {
						best = num;
						bestChunk = chunk;
						bestPos = i;
						//System.out.println(len+","+i+","+chunk+","+Zodiac.firstCipherDecoded.substring(i, i+len)+","+num);
					}
				}
				System.out.println(len+","+bestPos+","+best+","+(100*(float)best/cipher.length())+"%,"+countUniqueSymbols(bestChunk)+","+bestChunk+","+(Zodiac.CIPHER == 1 ? Zodiac.firstCipherDecoded.substring(bestPos, bestPos+len) : ""));
			}
		}
		
		/* count the number of ciphertext characters that will be decoded given the current CIPHER_START/CIPHER_END range. */
		public static void countCipherSymbols() {
			initSymbolCounts();
			THashSet<Character> set = new THashSet<Character>();
			int count = 0; char ch;
			for (int i=CIPHER_START; i<=CIPHER_END; i++) {
				ch = Zodiac.cipher[Zodiac.CIPHER].charAt(i);
				if (!set.contains(ch)) {
					set.add(ch);
					count += symbolCounts.get(ch);
				}
			}
			System.out.println("This cipher range decodes " + count + " characters (" + 
					((float)count/Zodiac.cipher[Zodiac.CIPHER].length()) + "%) of the cipher.");
		}
		
		/* count the number of unique symbols in the given string */
		public static int countUniqueSymbols(String msg) {
			THashSet<Character> chars = new THashSet<Character>();
			for (int i=0; i<msg.length(); i++) {
				chars.add(msg.charAt(i));
			}
			return chars.size();
		}
		
	}
