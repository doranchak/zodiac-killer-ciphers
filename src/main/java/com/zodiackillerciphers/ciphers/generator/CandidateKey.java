package com.zodiackillerciphers.ciphers.generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.Utils;
import com.zodiackillerciphers.ciphers.Cipher;
import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.ciphers.FrontMember;
import com.zodiackillerciphers.homophones.HomophonesNew;
import com.zodiackillerciphers.homophones.SequenceMatcher;
import com.zodiackillerciphers.io.FileUtil;
import com.zodiackillerciphers.ngrams.NGrams;
import com.zodiackillerciphers.ngrams.NGramsBean;
import com.zodiackillerciphers.ngrams.PeriodBean;
import com.zodiackillerciphers.pivots.Pivot;
import com.zodiackillerciphers.stats.StatsWrapper;
import com.zodiackillerciphers.tests.PrimePhobia;
import com.zodiackillerciphers.tests.Vectors;
import com.zodiackillerciphers.tests.jarlve.JarlveMeasurements;
import com.zodiackillerciphers.tests.unigrams.UniqueUnigramsPerRow;
import com.zodiackillerciphers.transform.CipherTransformations;
import com.zodiackillerciphers.transform.MeasurementsBean;
import com.zodiackillerciphers.transform.Operations;

import ec.EvolutionState;
import ec.Fitness;
import ec.multiobjective.MultiObjectiveFitness;
import ec.util.MersenneTwisterFast;
import ec.vector.FloatVectorIndividual;
import ec.vector.VectorIndividual;

/** represents operations that map the plain text to the cipher text */
public class CandidateKey extends FloatVectorIndividual implements FrontMember {
	public CandidatePlaintext plain;
	public CandidateCiphertext cipher;
	public boolean archive = false;
	/** a randomly selected set of constraints (trigrams+pivots) we wish to impose on this cipher candidate */
	public CandidateConstraints candidateConstraints;

	/** map cipher symbols to plain text.  errors cause polyalphabetic assignments. */
	public Map<Character, Set<Character>> c2p;
	/**
	 * track counts of assignments. key is cipher symbol. val is a map whose key
	 * is the plain text letter, and whose value is the count for that cipher
	 * symbol / plain text letter combination.
	 */
	public Map<Character, Map<Character, Integer>> counts;
	/** map plaintext to cipher symbols */
	public Map<Character, Set<Character>> p2c;
	
	/** map cipher symbols to positions */
	public Map<Character, List<Integer>> cPos;
	/** map plaintext letters to positions */
	public Map<Character, List<Integer>> pPos;
	
	/** count of transcription errors found during analysis */
	public int transcriptionErrors;
	/** hold the results of the analysis */
	public TranscriptionErrors transcriptionErrorsResults; 

	/** map position to pseudo-word that appears there */
	public Map<Integer, String> wordsMap;
	
	/** track last mutation performed */
	public String lastMutator;
	public double lastFitness;
	public double[] lastObjectives;
	
	/** similarity to rest of population.  1 = totally similar.  0 = totally dissimilar. */
	public float similarity;
	
	public double[] objectivesActual;

	/** to track encipherment error types, for debugging */
	Map<String, Integer> failMap;
	
	/** reference cipher (Z340) */
	public static String referenceCipher = Ciphers.cipher[0].cipher;
	public static StringBuffer spaces; // placeholder 
	static {
		spaces = new StringBuffer();
		for (int i=0; i<referenceCipher.length(); i++) spaces.append(' ');
	}
	/** list of distinct symbols in the reference cipher */
	public static String cipherAlphabet;
	static {
		cipherAlphabet = Ciphers.alphabet(referenceCipher);
	}
	public static String plainAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	/** the "olson rows" of Z340 */
	public static int[] OLSON_ROWS = new int[] {0,1,2, 10,11,12};
	
	/** map cipher symbols to allele value (position in alphabet) */
	public static Map<Character, Integer> alphabetMap;
	static {
		alphabetMap = new HashMap<Character, Integer>();
		for (int val=0; val < cipherAlphabet.length(); val++) {
			Character key = cipherAlphabet.charAt(val);
			alphabetMap.put(key, val);
		}
	}
	/** map cipher symbols to occurrences */
	//public Map<Character, List<Integer>> positions = new HashMap<Character, List<Integer>>(); 
	/** symbol counts in the reference cipher */
	public static Map<Character, Integer> referenceCounts = Ciphers.countMap(referenceCipher);
	
	/** unigram distribution, symbol agnostic */
	public static List<Integer> referenceCountsSorted = sortedCountsFrom(referenceCipher); 
	
	static MersenneTwisterFast rand = new MersenneTwisterFast();
	
	/** ambiguous symbol mappings */
	static Map<Character, String> ambiguous;
	static {
		ambiguous = new HashMap<Character, String>();
		ambiguous.put('A',"A^789");
		ambiguous.put('C',"CcKk");
		ambiguous.put('D',"Dd789");
		ambiguous.put('E',"E");
		ambiguous.put('G',"G");
		ambiguous.put('H',"H");
		ambiguous.put('I',"I:;/|(");
		ambiguous.put('O',"Oz()123456_#%*@");
		ambiguous.put('R',"R");
		ambiguous.put('Z',"Z");
	}
	static Map<Character, Set<Character>> ambiguousSet;
	static {
		ambiguousSet = new HashMap<Character, Set<Character>>();
		for (Character key : ambiguous.keySet()) {
			String val = ambiguous.get(key);
			Set<Character> set = new HashSet<Character>();
			for (int i=0; i<val.length(); i++) set.add(val.charAt(i));
			ambiguousSet.put(key,  set);
		}
	}
	static List<String> zodiacPermutations;
	static {
		/** all possible permutations of the string "ZODIAC".
		 * we allow only a single swap.
		 */
		zodiacPermutations = new ArrayList<String>();
		/*String z = "ZODIAC";
		List<String> list = new ArrayList<String>();
		for (int i=0; i<z.length()-1; i++) {
			StringBuffer sb = new StringBuffer(z);
			char c1 = sb.charAt(i);
			char c2 = sb.charAt(i+1);
			sb.setCharAt(i, c2);
			sb.setCharAt(i+1, c1);
			zodiacPermutations.add(sb.toString());
			//zodiacPermutations.add(sb.reverse().toString());
		}*/
		
		/* i changed this to have only 2 of the stronger-looking permutations */
		zodiacPermutations.add("ZODIAC");
		zodiacPermutations.add("ZODAIC");
	}
	static Map<String, List<String>> cipherWordPermutations;
	static {
		cipherWordPermutations = new HashMap<String, List<String>>();
		cipherWordPermutations.put("HER", cipherWordPermutationsFor("HER"));
		cipherWordPermutations.put("GOD", cipherWordPermutationsFor("GOD"));
		cipherWordPermutations.put("ZODIAC", new ArrayList<String>());
		for (String word : zodiacPermutations) {
			cipherWordPermutations.get("ZODIAC").addAll(cipherWordPermutationsFor(word));
		}
		/*
		for (String key : cipherWordPermutations.keySet()) {
			System.out.println(key);
			for (String s : cipherWordPermutations.get(key)) {
				System.out.println(" - " + s);
			}
		}*/
	}
	static Map<String, Set<String>> cipherWordLookups;
	static {
		cipherWordLookups = new HashMap<String, Set<String>>();
		for (String word : new String[] {"GOD","ZODIAC"}) {
			cipherWordLookups.put(word, new HashSet<String>());
			cipherWordLookups.get(word).addAll(cipherWordPermutations.get(word));
		}
	}
	
	static float[] genomeForInit = new float[] {0.94f, 0.516129f, 0.32258064f, 0.8064516f, 0.048387095f, 0.14516129f, 1.0f, 0.6935484f, 0.4516129f, 0.7419355f, 0.33870968f, 0.20967741f, 0.87096775f, 0.32258064f, 0.83870965f, 0.91935486f, 0.2580645f, 0.88709676f, 0.27419356f, 0.09677419f, 0.66129035f, 0.9032258f, 0.30645162f, 0.983871f, 0.9516129f, 0.7419355f, 0.6935484f, 0.24193548f, 0.33870968f, 0.5645161f, 0.61290324f, 0.16129032f, 0.4032258f, 0.37096775f, 1.0f, 1.0f, 0.9354839f, 0.66129035f, 0.032258064f, 0.30645162f, 0.16129032f, 0.5483871f, 0.3548387f, 0.33870968f, 0.20967741f, 0.88709676f, 0.7903226f, 1.0f, 0.3548387f, 0.08064516f, 0.6935484f, 0.58064514f, 0.29032257f, 0.048387095f, 0.08064516f, 0.24193548f, 0.14516129f, 0.6935484f, 0.6451613f, 0.08064516f, 0.14516129f, 0.9032258f, 0.4516129f, 0.87096775f, 0.9677419f, 0.46774194f, 0.11290322f, 0.983871f, 0.22580644f, 0.08064516f, 0.62903225f, 0.37096775f, 0.5f, 1.0f, 0.9354839f, 0.8548387f, 0.14516129f, 0.4516129f, 0.17741935f, 0.0f, 0.9354839f, 0.016129032f, 0.91935486f, 0.5645161f, 0.87096775f, 0.67741936f, 0.66129035f, 0.29032257f, 0.06451613f, 0.38709676f, 0.7419355f, 0.08064516f, 0.4032258f, 0.7096774f, 0.9677419f, 0.983871f, 0.48387095f, 0.9032258f, 0.46774194f, 0.6935484f, 0.5967742f, 0.83870965f, 0.46774194f, 0.83870965f, 0.5f, 0.5483871f, 0.4516129f, 0.048387095f, 0.9354839f, 0.5f, 0.7580645f, 0.38709676f, 0.62903225f, 0.19354838f, 1.0f, 0.8548387f, 0.4032258f, 0.14516129f, 0.6935484f, 0.0f, 0.29032257f, 0.37096775f, 0.048387095f, 0.8064516f, 0.09677419f, 0.5f, 0.4516129f, 0.62903225f, 0.62903225f, 0.516129f, 0.11290322f, 0.08064516f, 1.0f, 0.62903225f, 0.33870968f, 0.0f, 0.22580644f, 0.91935486f, 0.22580644f, 0.61290324f, 0.9677419f, 0.5f, 0.5483871f, 0.38709676f, 0.62903225f, 0.24193548f, 1.0f, 0.08064516f, 0.11290322f, 0.11290322f, 0.33870968f, 0.0f, 0.2580645f, 0.48387095f, 0.11290322f, 1.0f, 0.9677419f, 0.5f, 0.4516129f, 0.14516129f, 0.62903225f, 0.516129f, 1.0f, 0.08064516f, 1.0f, 0.38709676f, 0.33870968f, 0.7903226f, 0.22580644f, 0.08064516f, 0.11290322f, 0.016129032f, 0.83870965f, 1.0f, 0.09677419f, 0.5645161f, 0.9032258f, 0.82258064f, 0.8548387f, 0.8064516f, 0.29032257f, 0.37096775f, 0.7741935f, 0.06451613f, 1.0f, 0.983871f, 0.7580645f, 0.983871f, 0.27419356f, 0.2580645f, 1.0f, 0.5322581f, 1.0f, 0.22580644f, 0.46774194f, 0.4032258f, 0.82258064f, 0.9032258f, 0.7741935f, 0.48387095f, 0.22580644f, 0.43548387f, 0.11290322f, 0.983871f, 0.0f, 0.7903226f, 1.0f, 0.048387095f, 0.62903225f, 1.0f, 0.22580644f, 0.46774194f, 0.4032258f, 0.82258064f, 0.032258064f, 0.22580644f, 0.7096774f, 1.0f, 0.17741935f, 0.61290324f, 0.41935483f, 1.0f, 0.19354838f, 0.12903225f, 0.032258064f, 0.7741935f, 1.0f, 0.30645162f, 0.5f, 0.14516129f, 0.048387095f, 0.43548387f, 0.0f, 1.0f, 0.4032258f, 0.5f, 0.7580645f, 0.11290322f, 0.58064514f, 0.983871f, 0.41935483f, 0.7096774f, 0.30645162f, 0.17741935f, 0.32258064f, 0.5f, 0.4516129f, 0.08064516f, 0.5f, 0.8548387f, 0.46774194f, 0.29032257f, 0.08064516f, 0.2580645f, 1.0f, 0.06451613f, 0.22580644f, 0.5645161f, 0.06451613f, 0.8064516f, 0.16129032f, 0.87096775f, 0.09677419f, 0.62903225f, 0.27419356f, 0.048387095f, 0.83870965f, 0.43548387f, 0.5322581f, 0.83870965f, 0.82258064f, 0.67741936f, 0.6451613f, 0.6935484f, 0.9677419f, 0.08064516f, 0.91935486f, 0.11290322f, 0.983871f, 0.6935484f, 0.91935486f, 0.58064514f, 0.19354838f, 0.33870968f, 0.32258064f, 0.48387095f, 0.7580645f, 0.7258065f, 0.16129032f, 0.8064516f, 0.12903225f, 0.2580645f, 0.516129f, 0.27419356f, 1.0f, 0.7580645f, 0.67741936f, 0.11290322f, 0.16129032f, 0.33870968f, 0.14516129f, 0.48387095f, 0.5483871f, 0.38709676f, 0.17741935f, 0.11290322f, 0.12903225f, 0.22580644f, 0.7580645f, 0.17741935f, 0.27419356f, 0.5483871f, 0.67741936f, 0.7741935f, 0.16129032f, 0.33870968f, 0.7903226f, 0.37096775f, 0.5483871f, 0.58064514f, 0.983871f, 0.4032258f, 0.12903225f, 0.82258064f, 0.0f, 0.8064516f, 1.0f, 0.9516129f, 0.67741936f, 0.7258065f, 0.983871f, 0.33870968f, 0.27419356f, 0.37096775f, 0.5483871f, 0.7258065f, 0.17741935f, 0.8064516f, 0.12903225f, 0.82258064f, 0.6935484f};
	/** the index to the candidate file might not be correct any more, so look it up based on plaintext */
	static String plaintextForInit = "YSEEAERHBLMUSECOUSWMDFRHBLBLMUSEDEEWMDFRHBLMUSEBTHPOETBARGTADHUWTHROTADIEWTAHRYWICMUCMONABTENWRD_______IHHEWIHAAYETEALYODEEWIHAAYETEALYOCOUWIHAAYETEALYODEEWIHAAYETEALYOTEDEEWMDOTEODONERHRSOEAEOTEODODONERYSEEAEOTEODONERUSERVDOEFIAENSEPIHEPRSNFRSIHTITTOTOENOMNERUWASEE_______WTCERHCPRLSDHPREVOYSEHCPRLSDHPREVOHRSHCPRLSDHPREVOYSEHCPRLSDHPREVOH";
	static boolean USE_SINGLE_STARTER = false;
	
	/** starting point is the candidate plaintext, which is given to constructor */
	public CandidateKey(CandidatePlaintext plain) {
		this.plain = plain;
		this.cipher = new CandidateCiphertext(); 
	}
	
	public CandidateKey() {
	}
	
	/** initialize a key from the given genome */
	public CandidateKey(float[] genome) {
		this.genome = genome;
		expressGenome();
	}
	
	public void mutate() {
		;
	}
	
	public void init() {
		if (p2c == null) p2c = new HashMap<Character, Set<Character>>();
		if (c2p == null) c2p = new HashMap<Character, Set<Character>>();
		if (counts == null) counts = new HashMap<Character, Map<Character, Integer>>();
		if (cPos == null) cPos = new HashMap<Character, List<Integer>>();
		if (pPos == null) pPos = new HashMap<Character, List<Integer>>();
	}
	
	/** convert float from range [0,1] to an integer in the range [a,b] */
	public static int toInt(float val, int a, int b) {
		if (val < 0 || val > 1) throw new IllegalArgumentException("Out of range: " + val);
		int diff = b-a;
		if (diff < 0) throw new IllegalArgumentException(b + " is less than " + a);
		diff++;
		int result = (int) (a + val * diff);
		if (result == b+1) result = b;
		return result;
	}
	/** convert int from range [a,b] to a float in the range [0,1] */
	public static float toFloat(int val, int a, int b) {
		if (val < a || val > b) throw new IllegalArgumentException("Out of range: " + val);
		if (a > b) throw new IllegalArgumentException(b + " is less than " + a);
		int diff = b-a;
		
		return ((float)val - a)/diff;
		
	}
	
	/** return list of indexes of all occurrences of the given string in the cipher text */
	public List<Integer> searchInCipher(String search) {
		List<Integer> list = new ArrayList<Integer>();
		int index = cipher.cipher.indexOf(search);
		while (index >= 0) {
			list.add(index);
		    index = cipher.cipher.indexOf(search, index+1);
		}
		return list;
	}
	
	/** return list of indexes of all occurrences of the given string in the plain text */
	public List<Integer> searchInPlaintext(String search) {
		List<Integer> list = new ArrayList<Integer>();
		int index = plain.plaintext.indexOf(search);
		while (index >= 0) {
			list.add(index);
		    index = plain.plaintext.indexOf(search, index+1);
		}
		return list;
	}

	public static int indexFromGenomeValue(float val) {
		return toInt(val, 0, CandidatePlaintextLoader.size()-1);
	}
	
	public static float genomeValueFromIndex(int i) {
		return toFloat(i, 0, CandidatePlaintextLoader.size()-1);
	}
	
	/** decode given ciphertext using c2p mappings.  if multiple p exists for a given c, resolve using
	 * the mapping having the highest number of assignments.
	 */
	
	public String decode(String ciphertext) {
		String plain = "";
		for (int i=0; i<ciphertext.length(); i++) {
			char c = ciphertext.charAt(i);
			char p = decode(c);
			plain += p;
		}
		return plain;
	}
	
	/** decode given ciphertext using c2p mappings.  if multiple p exists for a given c, resolve using
	 * the mapping having the highest number of assignments.
	 */
	public char decode(char c) {
		Character bestPlain = ' ';
		Integer bestCount = 0;
		if (counts.get(c) != null)
			for (Character p : counts.get(c).keySet()) {
				int currentCount = counts.get(c).get(p);
				if (currentCount > bestCount) {
					bestCount = currentCount;
					bestPlain = p;
				}
			}
		return bestPlain;
		
	}
	
	/** convert genome into an action that produces cipher text from plain text.
	 * the genome selects candidate plain text, and also computes the cipher text. */
	public void expressGenome() {

		/*try {
			throw new RuntimeException("expressing " + hashCode());
		} catch (RuntimeException e) {
			System.out.println(e);
			e.printStackTrace();
		}*/
		//System.out.println("expressing " + hashCode());
		
		/* reset everything in case we expressed the genome before already */
		init();
		resetMaps();
		resetCipher();
		
		// index 0 is the candidate selector
		// indices [1,n] are the symbol assignments for each of position of the plain text (length = n)
		
		//int which = toInt(genome[0], 0, CandidatePlaintextLoader.size()-1);
		int which = indexFromGenomeValue(genome[0]);
		this.plain = CandidatePlaintextLoader.get(which);
		
		StringBuffer sb = new StringBuffer();
		for (int i=1; i<= referenceCipher.length(); i++) {
			char c = alphaForAllele(genome[i]);
			sb.append(c);
			//int a = toInt(genome[i], 0, cipherAlphabet.length()-1);
			//sb.append(cipherAlphabet.charAt(a));
			int pos = i-1;
			encode(pos, this.plain.plaintext.charAt(pos), c);
		}
		
		cipher.cipher = new StringBuffer(sb);
		encode(0, this.plain.plaintext, sb.toString());
		
		
	}
	
	/** return alphabet entry for the given allele */
	public static char alphaForAllele(float val) {
		if (val < 0) return ' ';
		return cipherAlphabet.charAt(toInt(val, 0, cipherAlphabet.length()-1));
	}
	/** return allele value corresponding to the given alphabet entry */
	public static float alleleForAlpha(char ch) {
		//System.out.println(ch);
		if (ch == ' ') return -1;
		if (alphabetMap.get(ch) == null) {
			System.err.println("alphabetMap null for " + ch);
		}
		int pos = alphabetMap.get(ch);
		return toFloat(pos, 0, cipherAlphabet.length()-1);
	}
	
	/** map cipher text to plain text, and vice versa.  also track positions for each symbol and letter. */
	public void makeMapsOBSOLETE() {
		c2p = new HashMap<Character, Set<Character>>();
		p2c = new HashMap<Character, Set<Character>>();
		counts = new HashMap<Character, Map<Character, Integer>>();
		//c2pCounts = new HashMap<Character, Map<Character, Integer>>(); TODO
		//map(plain.plaintext, cipher.cipher.toString());
		
		/*
		for (int i=0; i<plain.plaintext.length(); i++) {
			if (isFiller(i)) continue; // ignore filler section
			char p = plain.plaintext.charAt(i);
			char c = cipher.cipher.charAt(i);
			Set<Character> setP = c2p.get(c);
			Set<Character> setC = p2c.get(p);
			if (setP == null) setP = new HashSet<Character>();
			if (setC == null) setC = new HashSet<Character>();
			setP.add(p);
			setC.add(c);
			c2p.put(c, setP);
			p2c.put(p, setC);

			List<Integer> val = cPos.get(c);
			if (val == null) val = new ArrayList<Integer>();
			val.add(i);
			cPos.put(c, val);
			
			val = pPos.get(p);
			if (val == null) val = new ArrayList<Integer>();
			val.add(i);
			cPos.put(p, val);
		}
		*/
	}
	
	public boolean isFiller(int pos) {
		return pos >= plain.fillerStart && pos <= plain.fillerEnd;
	}
	
	/** feasibility checks */
	
	/** does this cipher text preserve one of the pivots pairs specified by the given plain text? */
	//public boolean hasPivots() {
		/*List<Direction> list = new ArrayList<Direction>();
		list.add(Direction.N);
		list.add(Direction.S);
		list.add(Direction.E);
		list.add(Direction.W);
		List<Pivot> pivots = PivotUtils.findPivots(Ciphers.grid(cipher.cipher, 17), 4, list, false);
		return (pivots != null && !pivots.isEmpty());*/
		/*
		Map<String, Integer> map = pivots();
		
		Set<String> ngram = new HashSet<String>();
		
		int count = 0;
		for (String key : map.keySet()) {
			if (map.get(key) > 1) {
				count++;
				ngram.add(key);
			}
			if (count > 1) {
				return ngram.contains("*|JR") && ngram.contains("Vc.b");
			}
		}
		return false;*/
	//}
	
	/** return counts of pivot n-grams in the cipher text, to compare with the pivot constraints in the plaintext */
	/*public Map<String, Integer> pivots() {
		Map<String, Integer> map = new HashMap<String, Integer>();
		for (String key : plain.pivotPairs.keySet()) {
			for (Pivot pivot : plain.pivotPairs.get(key)) {
				List<String> ngrams = Pivot.toNgram(key, pivot, cipher.cipher);
				for (String s : ngrams) {
					Integer val = map.get(s);
					if (val == null) val = 0;
					val++;
					map.put(s,  val);
				}
			}
		}
		return map;
	}*/
	
	/** does the cipher text preserve a pair of box corners? */
	public boolean hasBoxCorners() {
		BoxCornerPair pair = candidateConstraints.boxCornerPair;
		BoxCorner[] corners = pair.corners;
		for (BoxCorner corner : corners) {
			String c = corner.cipher();
			for (int i=0; i<corner.positions().size(); i++) {
				//System.out.println(corner.positions() + ", " + corner.symbols + ", " + c);
				int pos = corner.positions().get(i);
				int expectedSymbol = c.charAt(i);
				if (cipher.cipher.charAt(pos) != expectedSymbol) return false;
			}
		}
		return true;
	}
	
	/** return list of indexes of all occurrences of the given search within the given string */
	public static List<Integer> findInString(String search, String str) {
		 if (search == null || str == null) return null;
		 int start = 0;
		 int found = str.indexOf(search, start);
		 List<Integer> pos = new ArrayList<Integer>();
		 while (found > -1) {
			 pos.add(found);
			 start = found + search.length();
			 found = str.indexOf(search, start);
		 }
		 return pos;
	}
	
	/** does the cipher preserve the repeating trigram pair that repeats in the same column and intersects with another trigram? */
	public boolean hasTrigram() {
		// for now, let's mimic the 340 by using the exact trigrams, "|5F" and
		// "FBc".
		String tri1 = "|5F";
		String tri2 = "FBc";
		String joined = "|5FBc";
		
		List<Integer> list = findInString(tri1, cipher.cipher.toString());
		if (list.size() < 2) return false;
		if (list.get(0) % 17 != list.get(1) % 17) return false;
		//System.out.println("|5F " + list.get(0) + " " + list.get(1) + " " + cipher.cipher);
		if (findInString(tri2, cipher.cipher.toString()).size() < 2) return false;
		if (cipher.cipher.indexOf(joined) == -1) return false;
		return true;
	}
	/** does the cipher have not too many errors in the mapping?*/
	public boolean hasAcceptableErrors() {
		return false;	
	}
	/** does the cipher have words that stand out, like HER, GOD, ZODAIK, etc? */
	public boolean hasWords() {
		wordsMap = new HashMap<Integer, String>();
		return hasWords(cipher.cipher.toString(), wordsMap);
	}
	/** does the cipher have words that stand out, like HER, GOD, ZODAIK, etc? */
	public static boolean hasWords(String cipher, Map<Integer, String> wordsMap) {
		int her = cipher.indexOf("HER");
		if (her == -1) {
			return false;
		}
		wordsMap.put(her, "HER");
		
		boolean found = false;
		for (int i=0; i<cipher.length()-2; i++) {
			String sub = cipher.substring(i,i+3);
			if (cipherWordLookups.get("GOD").contains(sub)) {
				found = true;
				wordsMap.put(i, "GOD");
				break;
			}
		}
		if (!found) return false;
		found = false;
		for (int i=0; i<cipher.length()-6; i++) {
			String sub = cipher.substring(i,i+7);
			if (!sub.startsWith("z")) continue;
			if (cipherWordLookups.get("ZODIAC").contains(sub.substring(1))) {
				found = true;
				wordsMap.put(i, "ZODIAC");
				break;
			}
		}
		return found;
	}

	/** return all positions covered by pseudo-words */
	public List<Integer> wordPositions() {
		return wordPositions(wordsMap);
	}
	public static List<Integer> wordPositions(Map<Integer, String> wordsMap) {
		if (wordsMap == null) return null;
		List<Integer> list = new ArrayList<Integer>();
		for (Integer key : wordsMap.keySet()) {
			String val = wordsMap.get(key);
			for (int i=0; i<val.length(); i++) {
				list.add(key+i);
			}
			if ("ZODIAC".equals(val)) list.add(key+6);
		}
		return list;
	}
	
	/** does the cipher have "fold lines" in the middle of the cipher? */
	public boolean hasFoldLines() {
		return CandidateConstraints.hasFoldLines(cipher.cipher.toString());
	}
	/** does the cipher have various occurrences of "by"? */
	public boolean hasBy() {
		return false;
	}
	
	/** measurements */

	/** flatness of plaintext-symbol mapping */ 
	public float measureFlatness() {
		return 0;
	}
	/** measure similarity to Z340's unigram distribution */
	public float measureUnigramDistance() {
		return measureUnigramDistance(cipher.cipher.toString());
	}
	

	public static List<Integer> sortedCountsFrom(String cipher) {
		List<Integer> counts = new ArrayList<Integer>(Ciphers.countMap(cipher)
				.values());
		Collections.sort(counts);
		Collections.reverse(counts);
		return counts;
	}
	
	/** symbol-agnostic unigram distance.  returns sum of squares of differences.  */
	public static double measureSymbolAgnosticUnigramDistance(String cipher, boolean sqrt) {
		List<Integer> list = sortedCountsFrom(cipher);
		return Vectors.distance(list, referenceCountsSorted, sqrt);
	}
	
	/** measures symbol-specific unigram distance.  */
	public static float measureUnigramDistance(String cipher) {
		int errors = 0;
		
		Map<Character, Integer> counts1 = referenceCounts;
		Map<Character, Integer> counts2 = Ciphers.countMap(cipher);
		
		for (Character ch : counts1.keySet()) {
			Integer val1 = counts1.get(ch);
			Integer val2 = counts2.get(ch);
			if (val1 == null) val1 = 0;
			if (val2 == null) val2 = 0;
			errors += (val1 > val2 ? val1 - val2 : val2 - val1);
		}
		for (Character ch : counts2.keySet()) {
			if (counts1.keySet().contains(ch)) continue;
			Integer val = counts2.get(ch);
			errors += val; 
		}
		return errors;
	}
	
	/** produce a string showing unigram distribution differences between the given cipher and z340 */ 
	public static String dumpDifferences(String cipher) {
		String diffs = "";
		
		Map<Character, Integer> counts1 = referenceCounts;
		Map<Character, Integer> counts2 = Ciphers.countMap(cipher);
		
		for (Character ch : counts1.keySet()) {
			Integer val1 = counts1.get(ch);
			Integer val2 = counts2.get(ch);
			if (val1 == null) val1 = 0;
			if (val2 == null) val2 = 0;
			if (val1 > val2) {
				diffs += ch + ":-" + (val1-val2) + " ";
			} else if (val1 < val2) {
				diffs += ch + ":+" + (val2-val1) + " ";
			}
		}
		for (Character ch : counts2.keySet()) {
			if (counts1.keySet().contains(ch)) continue;
			Integer val = counts2.get(ch);
			diffs += ch + ":+" + (val) + " ";
		}
		return diffs;
	}

	/** count how many per-line repeats occur for lines 1-3, and 11-13.
	 * in z340, there are 0 total.  
	 */
	public int measureLineRepeats() {
		return measureLineRepeats(cipher.cipher.toString());
	}
	
	public static int measureLineRepeats(String cipher) {
		int count = 0;
		for (int row : OLSON_ROWS) count += countRepeats(cipher, row);
		return count;
	}
	public static int measureLineNonRepeats(String cipher) {
		int count = 0;
		for (int row : OLSON_ROWS) count += UniqueUnigramsPerRow.countNonRepeats(cipher, row);
		return count;
	}
	
	/** count the number of repeating symbols in the given row of the cipher */
	public static int countRepeats(String cipher, int row) {
		int count = 0;
		Set<Character> seen = new HashSet<Character>();
		for (int pos=row*17; pos<row*17+17; pos++) {
			char ch = cipher.charAt(pos);
			if (seen.contains(ch)) count++;
			else seen.add(ch);
		}
		return count;
	}
	
	/** instead of matching Z340's exact counts, just make sure we have the same number of repeats, no matter what the ngrams are */ 
	public float measureNgramDistance() {
		return measureNgramDistance(cipher.cipher.toString());
	}
	public static float measureNgramDistance(String cipher) {
		return NGramsBean.measureNgramDistanceCountsOnly(cipher.toString(), NGramsBean.referenceCipherBeans.get("z340"));
	}
	
	/** distance of weighted 2-gram distribution from z340's */
	public static double measureNgramDistance(int n, String cipher) {
		NGramsBean ng = new NGramsBean(n, cipher); 
		return Vectors.distance(ng.distributionWeighted, NGramsBean.referenceCipherBeans.get("z340").get(n).distributionWeighted, true);
	}

	/** measure similarity to Z340's "period 19 bigram" phenomenon */ 
	public float measureBigrams19() {
		return MeasurementsBean.measureBigrams19(cipher.cipher.toString());
	}
	/** measure similarity to Z340's repeating ngram counts */ 
	public float measureNgramDistanceOLD() {
		return NGramsBean.measureNgramDistance(cipher.cipher.toString(), NGramsBean.referenceCipherBeans.get("z340"));
	}
	public int measureHomophoneCycleErrors() {
		SequenceMatcher s = new SequenceMatcher(cipher.cipher.toString());
		return s.totalErrors();
	}
	public float measureCycleComparison() {
		return HomophonesNew.measureCompare(cipher.cipher.toString());
	}
	
	public float measureRepeatingFragmentDistance() {
		return NGrams.measureRepeatingFragmentDistance(cipher.cipher.toString());
	}
	
	/** absolute distance to reference non-repeat (Jarlve) measurement for Z340 */
	public int measureNonRepeatDistance() {
		return Math.abs(4462 - JarlveMeasurements.nonrepeat(cipher.cipher.toString()));
	}
	/** absolute distance to reference non-repeat alternate (Jarlve) measurement for Z340 */
	public float measureNonRepeatAlternateDistance() {
		return (float) Math.abs(1599.5929 - JarlveMeasurements.nonrepeatAlternate(cipher.cipher.toString()));
	}
	
	/** measure similarity to Z340's "prime-phobia" for + and B */
	public int measurePrimePhobia() {
		return PrimePhobia.errors(cipher.cipher.toString()).errors;
	}
	
	/** measure number of repeated symbols within the "olson" lines.  compare to Z340. */
	public int measureOlson1() {
		// Z340's 6 lines have 6*17 = 102 symbols.  53 of them are unique.
		Set<Character> seen = new HashSet<Character>();
		for (int row : OLSON_ROWS) {
			for (int i=0; i<17; i++) {
				char c = cipher.cipher.charAt(row*17+i);
				seen.add(c);
			}
		}
		return Math.abs(seen.size() - 53);
	}
	/** measure line repeats */
	public int measureOlson2() {
		return measureLineRepeats();
	}
	
	/** returns true if the ciphertext has:
	 * 1) a trigram that repeats in the same column
	 * 2) the trigram in 1) intersects with another repeating trigram (in the way the trigram in the 340 does)
	 */
	public boolean criteriaNgrams() {
		Set<TrigramCandidatePair> trigrams = TrigramUtils.trigramsFrom(cipher.cipher.toString());
		if (trigrams == null || trigrams.isEmpty()) return false;
		return true;
	}
	
	/** returns true only if the ciphertext, when arranged into a cipher block,
	 * contains at least two pivots with the same orientation.
	 */
	public boolean hasPivots() {
		return CandidateConstraints.hasPivots(cipher.cipher.toString());
	}
	
	/** combined errors */
	public float errors(boolean setupCache) {
		
		
		float errors = errorsEncipherment();
		if (errors > 0) {
			//System.out.println("ERRORS: encipherment");
			failMapIncrement("encipherment");
			//System.out.println("walrus " + plain.plaintext);
			//System.out.println("walrus " + cipher.cipher);
			return errors;
		}
		
		/** make sure all constraints are still satisfied */
		
		if (!hasWords()) {
			failMapIncrement("words");
			return 1;
		}
		if (!criteriaNgrams()) {
			failMapIncrement("ngrams");
			return 1;
		}
		if (!hasPivots()) {
			failMapIncrement("pivots");
			return 1;
		}
		if (!hasFoldLines()) {
			failMapIncrement("foldLines");
			return 1;
		}
		if (!hasBoxCorners()) {
			failMapIncrement("boxCorners");
			return 1;
		}
		
		// for improved performance, check the cache.

		/*
		if (!setupCache) {
			if (CandidateKeyCache.isCached(plain.index)) {
				if (CandidateKeyCache.satisified(plain.index, cipher.cipher.toString()))
					return 0;
				else {
					failMapIncrement("cacheUnsatisfied");
					return 1;
				}
			} else {
				failMapIncrement("cacheMiss");
				return 1; // cache miss: i think this would only happen if we tried to switch to a new candidate plaintext index
			}
		}*/
		
		// we are setting up the cache, so we have to compute the constraints each time.
		// store them after successful init, so during evolution we can reuse the cache entries.
		
		//CandidatePlaintext cp = new CandidatePlaintext(plain.index, cipher.cipher.toString()); /* using CandidatePlaintext, but with cipher instead */
		/*cp.candidateKey = this;
		cp.criteriaAll(true);
		if (cp.candidateConstraints == null || cp.candidateConstraints.isEmpty()) {
			failMapIncrement("criteriaAll2");
			return 1;
		}
		
		CandidateKeyCache.addToCache(plain.index, candidateConstraints, cipher.cipher.toString()); // use the constraints we created here in the key, since it was used from all the make methods
		*/
		return 0;
		
		/*
		if (!hasTrigram()) {
			//System.out.println("ERRORS: trigram");
			return 1;
		}
		if (!hasPivots()) {
			//System.out.println("ERRORS: pivots");
			return 1;
		}*/
		/* TODO: uncomment these
		if (!hasFoldLines()) {
			//System.out.println("ERRORS: fold lines");
			return 1;
		}
		if (!hasWords()) {
			//System.out.println("ERRORS: words");
			return 1;
		}*/
		//return errors;
	}

	/** count the number of encipherment errors.  make sure to ignore filler. */
	public float errorsEncipherment() {
		this.transcriptionErrorsResults = new TranscriptionErrors();
		this.transcriptionErrors = transcriptionErrorsResults.errors(c2p, p2c, counts, this);
		if (this.transcriptionErrors > TranscriptionErrors.MAX_ERRORS) {
			// invalid encipherment was found, or max transcription errors was reached
			//System.out.println("Errors: " + transcriptionErrors + ": " + (transcriptionErrorsResults == null ? "null" : transcriptionErrorsResults.toString(counts)));
			Utils.debug("this.transcriptionErrors = " + this.transcriptionErrors);
			return 1;
		}
		
		return 0;
	}
	
	
	/** count the number of encipherment errors.  make sure to ignore filler. */
	public float errorsEnciphermentOLD() {
		float errors = 0;

		
		// count all cipher symbols that map to more than one plain text letter
		Map<Character, Character> map = new HashMap<Character, Character>();
		for (int i=0; i<plain.plaintext.length(); i++) {
			char c = cipher.cipher.charAt(i);
			char p = plain.plaintext.charAt(i);
			if (p == '_') continue;
			if (c == ' ') continue;
			Character val = map.get(c);
			if (val != null && val != p) errors++;
			if (val == null) map.put(c, p);
		}
		
		return errors;
	}
	
	public static void testConversions() {
		for (int i=0; i<10000; i++) {
			float val = (float) Math.random();
			//System.out.println(val + ", " + toInt(val, -3,2));
		}
		
		for (float val = 0; val <= 1; val+=.01) {
			int i= toInt(val,0,2);
			float f = toFloat(i,0,2);
			int i2 = toInt(f,0,2);
			//System.out.println(val+", "+i+", "+f+", " + i2);
		}
		//System.out.println(1.0f + ", " + toInt(1.0f, 0,2));
		
		for (int i=0; i<=1000; i++) {
			float f= toFloat(i,0,1000);
			int k = toInt(f,0,1000);
			float f2= toFloat(k,0,1000);
			System.out.println(i+", "+f+", "+k+", " + f2);
		}
	}
	
	
	public String dumpp2c() {
		if (p2c == null) return null;
		String d = "p2c: ";
		for (Character key : p2c.keySet()) {
			d += key + " [";
			for (Character c : p2c.get(key)) {
				d += c;
			}
			d += "] ";
		}
		return d;
	}
	public String dumpc2p() {
		if (c2p == null) return null;
		String d = "c2p: ";
		for (Character key : c2p.keySet()) {
			d += key + " [";
			for (Character c : c2p.get(key)) {
				d += c;
			}
			d += "] ";
		}
		return d;
	}
	
	public String c2pErrors() {
		String e = "";
		for (Character key : c2p.keySet()) {
			if (key == ' ') continue;
			Set<Character> val = c2p.get(key);
			if (val.size() < 2) continue;
			if ("".equals(e)) e += "ERRORS: ";
			e += key + " " + val + " ";
		}
		if (transcriptionErrorsResults != null) e += "Transcription errors: " + transcriptionErrors + ", " + transcriptionErrorsResults.toString(counts);
		return e;
	}
	
	public String genome() {
		String g = "";
		for (int i=0; i<genome.length; i++) g += genome[i] + " ";
		return g;
	}
	
	public String words() {
		String result = "";
		if (wordsMap == null) return result;
		for (Integer key : wordsMap.keySet()) {
			String val = wordsMap.get(key);
			String sub = cipher.cipher.substring(key, key+val.length());
			result += key + " " + sub + " (" + val + ") ";
		}
		return result;
	}
	public String toStringOLD() {
		return (archive ? "[ARCHIVE] " + compositeFitness() : "") + c2pErrors() + plain.toString() + " " + cipher.toString() + " " + dumpp2c() + " " + dumpc2p() + " words [" + words() + "]";
	}
	public String toString() {
		return compositeFitness() + " " + compositeFitnessActual()
				+ " objectives " + objectivesToString() + " sim " + similarity
				+ " " + c2pErrors() + plain.toString() + " "
				+ cipher.toString() + " " + dumpp2c() + " " + dumpc2p()
				+ " words [" + words() + "]";
	}
	
	public String compositeFitness() {
		return "compositeFitness " + compositeFitnessAsDouble() + " ";
	}
	public String compositeFitnessActual() {
		return "compositeFitnessActual " + compositeFitnessAsDouble(objectivesActual) + " ";
	}
	public double compositeFitnessAsDouble() {
		return compositeFitnessAsDouble(this.fitness);
	}
	public static double compositeFitnessAsDouble(Fitness fitness) {
		MultiObjectiveFitness fit = (MultiObjectiveFitness) fitness;
		return compositeFitnessAsDouble(fit.getObjectives());
		
	}
	public static double compositeFitnessAsDouble(double[] objectives) {
		if (objectives == null) return 0;
		double result = 1d;
		for (double d : objectives) result *= (1+d);
		return result;
	}


	public void defaultMutate(EvolutionState state, int thread) {

		//System.out.println("mutate before " + this.hashCode());
		if (this.isBroken()) {
			System.err.println("defaultMutate on broken key " + this);
			System.exit(-1);
		}
		
		/*if (archive) {
			Utils.debug("SMEG will not mutate archive entry " + Utils.info(this));
			return; // do not mutate archive!
		} else {
			Utils.debug("SMEG mutate " + Utils.info(this));
			
		}*/
		expressGenome();

		// proportional random selection, to cause more effective mutators to be run more often
		double d = rand.nextDouble(true, true);
		int which = 0;
		
		for (int i=0; i<Mutators.roulette.length; i++) {
			if (d < Mutators.roulette[i]) {
				which = i;
				break;
			}
		}
		
		int val = 0;
		if (which == val++) {
			Mutators.fixLineRepeats(this);
		}
		else if (which == val++) {
			Mutators.fixErrors(this);
		}
		else if (which == val++) {
			Mutators.fixPrimePhobia(this);
		}
		else if (which == val++) {
			Mutators.fixNgrams(this, 1);
		}
		else if (which == val++) {
			Mutators.alleleMutation(this, 10, 0);
		}
		else if (which == val++) {
			Mutators.fixDistribution(this, 1);
		}
		else if (which == val++) {
			Mutators.fixDistribution(this, 0);
		}
		else if (which == val++) {
			Mutators.fixCycle(this, 0);
		}
		else if (which == val++) {
			Mutators.fixOddEvens(this, 1, false);
		}
		else if (which == val++) {
			Mutators.fixCycle(this, 2);
		}
		else if (which == val++) {
			Mutators.swap(this, 10, 0);
		}
		else if (which == val++) {
			Mutators.tweak(this, 0);
		}
		else if (which == val++) {
			Mutators.fixNgrams(this, 0);
		}
		else if (which == val++) {
			Mutators.fixCycle(this, 1);
		}
		else if (which == val++) {
			Mutators.tweak(this, 1);
		}
		else if (which == val++) {
			Mutators.tweak(this, 3);
		}
		else if (which == val++) {
			Mutators.tweak(this, 2);
		}
		else if (which == val++) {
			Mutators.fixOddEvens(this, 1, true);
		}
		else if (which == val++) {
			Mutators.fixOddEvens(this, 0, true);
		}
		else if (which == val++) {
			Mutators.fixOddEvens(this, 0, false);
		}
		else if (which == val++) {
 			Mutators.exchange(this);
		}
		else if (which == val++) {
			Mutators.tweak(this, 5);
		}
		else if (which == val++) {
			Mutators.alleleMutation(this, 10, 1);
		}
		else if (which == val++) {
			Mutators.tweak(this, 4);
		}
		else if (which == val++) {
			Mutators.swap(this, 10, 1);
		}
		else {
			System.err.println("Roulette setup is messed up.  Which: [" + which + "]");
		}
		
		//System.out.println("d " + d + " which " + which + " val " + val);
		
	}
    public static String shuffle(String input){
        List<Character> characters = new ArrayList<Character>();
        for(char c:input.toCharArray()){
            characters.add(c);
        }
        StringBuilder output = new StringBuilder(input.length());
        while(characters.size()!=0){
            int randPicker = rand.nextInt(characters.size());
            output.append(characters.remove(randPicker));
        }
        return output.toString();
    }
    
	/** init by making a cipher text with no encipherment errors.
	 * try to make the symbols match the distribution of the reference cipher.
	 **/
	public void reset(EvolutionState state, int thread) {
		System.out.println("Resetting!");
		init();
		make();
		
	}
	
	/** return other cipher symbols that map to the same plaintext as this one */
	public String cipherSymbolsMappedToSamePlaintextAsThisSymbol(char c) {
		String result = "";
		for (Character p : c2p.get(c)) {
			if (p == '_') continue;
			Set<Character> cipherSymbols = p2c.get(p);
			for (Character cs: cipherSymbols) {
				if (cs == c) continue;
				result += cs;
			}
		}
		return result;
	}
	
	/** return cipher symbols that have the given frequency */
	public String cipherSymbolsWithFrequency(int freq) {
		String result = "";
		for (Character c : counts.keySet()) {
			int total = 0;
			for (Character p : counts.get(c).keySet()) {
				if (p == '_') continue;
				total += counts.get(c).get(p);
			}
			if (total == freq) result += c;
		}
		return result;
	}
	
	/** encode and map all occurrences of the given plain text to the given cipher symbol */
	public void encodeAll(char p, char c) {
		for (int i=0; i<plain.plaintext.length(); i++) {
			char pp = plain.plaintext.charAt(i);
			if (p == pp) {
				encode(i, p, c);
			}
		}
	}
	
	/** map the given plaintext to the given ciphertext, and track counts */
	private void mapOLD(char plain, char cipher) {
		
		if (cipher == ' ') return; // spaces disallowed in cipher text
		boolean isFiller = plain == '_' || plain == ' ';
		
		//System.out.println("mapping " + cipher + " to " + plain);
		init();
		Set<Character> val;
		
		/** track assignment counts */
		Map<Character, Integer> map = counts.get(cipher);
		if (map == null) map = new HashMap<Character, Integer>();
		Integer count = map.get(plain);
		if (count == null) count = 0;
		count++;
		map.put(plain, count);
		counts.put(cipher, map);
		
		val = p2c.get(plain);
		if (val == null) val = new HashSet<Character>();
		val.add(cipher);
		p2c.put(plain, val);
		
		val = c2p.get(cipher);
		if (val == null) val = new HashSet<Character>();
		val.add(plain);
		c2p.put(cipher, val);
		//if (val.size() > 1) {
		//	System.out.println("ERROR: " + cipher + " mapped to " + val);
		//}
		//System.out.println("mapped " + plain + " to " + cipher);
	}
	/** map the given plaintext string to the given ciphertext string */
	// DOESN'T WORK WITH SNIPPETS
	// NEEDS TO CHECK EXISING VALUES OF CIPHER AND PLAIN
	// if any mapping already exists, need to update
	private void mapOLD(String plain, String cipher) {
		if (plain == null || cipher == null) throw new RuntimeException("Different lengths: [" + plain + "] [" + cipher + "]");
		if (plain.length() != cipher.length()) throw new RuntimeException("Different lengths: [" + plain + "] [" + cipher + "]");
		
		if (cPos == null) cPos = new HashMap<Character, List<Integer>>();
		if (pPos == null) pPos = new HashMap<Character, List<Integer>>();
		
		for (int i=0; i<plain.length(); i++) { // THIS ASSUMES FULL PLAINTEXT
			char p = plain.charAt(i);
			char c = cipher.charAt(i);
			mapOLD(p, c);

			// map plaintext letters and ciphertext symbols to positions 
			List<Integer> val = cPos.get(c);
			if (val == null) val = new ArrayList<Integer>();
			val.add(i);
			cPos.put(c, val);
			
			val = pPos.get(p);
			if (val == null) val = new ArrayList<Integer>();
			val.add(i);
			pPos.put(p, val);
		}
	}

	private void encodeUpdateCounts(char cCurrent, char cPrevious, char p) {
		// increment count for (cCurrent -> p) mapping
		//String msg = "encodeUpdateCounts cCurr " + cCurrent + " cPrev " + cPrevious + " p " + p + " hashcode " + hashCode(); 
		Map<Character, Integer> map = counts.get(cCurrent);
		if (map == null) map = new HashMap<Character, Integer>();
		
		// if cCurr == cPrev, we must have already counted the cCurr->p mapping
		if (cCurrent != cPrevious) {
			Integer count = map.get(p);
			if (count == null) count = 0;
			count++;
			map.put(p, count);
			counts.put(cCurrent, map);
			//msg += ", cCurr->p count is now " + count;
			//Utils.debug("encodeUpdateCounts cCurr " + cCurrent + " -> " + p + " count is now " + count);
		}
		
		// If there was a cPrevious, there must still be a mapping (cPrevious -> p).   
		if (cPrevious != ' ' && cPrevious != cCurrent) { 
			map = counts.get(cPrevious);
			/*if (map == null) {
				Utils.debug(msg);
			}*/
			assert map != null : "Expected map for cipher " + cPrevious;
			Integer val = map.get(p);
			/*if (val == null) {
				Utils.debug(msg);
			}*/
			assert val != null : "Expected map for plain " + p + " -> cPrev " + cPrevious;
			val--;
			
			//Utils.debug("encodeUpdateCounts cPrev " + cPrevious + " -> " + p + " count is now " + val);
			//msg += ", cPrev->p count is now " + val;
			
			
			if (val == 0)
				map.remove(p);
			else map.put(p, val);
			counts.put(cPrevious, map);
		}
		//Utils.debug(msg);
		//System.out.println("counts " + counts);
		
	}
	private void encodeUpdateMaps(int pos, char cCurrent, char cPrevious, char p) {
		// Note: This method assumes encodeUpdateCounts was already called.
		
		/** map plaintext to cipher symbols */
		//public Map<Character, Set<Character>> p2c;
		//public Map<Character, Set<Character>> c2p;
		
		// first, if there was a previous cipher symbol, check
		// if its mapping needs to be removed
		if (cPrevious != ' ' && cPrevious != cCurrent) {
			Map<Character, Integer> map = counts.get(cPrevious);
			if (map == null) {
				// cPrevious has no mappings whatsoever
				Set<Character> set = p2c.get(p);
				set.remove(cPrevious);
				c2p.remove(cPrevious);
				Utils.debug("encodeUpdateMaps removed cPrev " + cPrevious + " from p2c for p " + p + " and from c2p");
			} else {
				Integer count = map.get(p);
				if (count == null || count == 0) {
					// cPrevious no longer has a mapping with this plaintext
					p2c.get(p).remove(cPrevious);
					assert c2p.get(cPrevious) != null : "c2p is null for cPrev " + cPrevious + " cCurr " + cCurrent + " p " + p; 
					c2p.get(cPrevious).remove(p);
					Utils.debug("encodeUpdateMaps removed cPrev " + cPrevious + " from p2c for p " + p + " and removed " + cPrevious + " -> " + p + " mapping from c2p");
				}
			}
		}
		
		// now map the current one
		Set<Character> set = p2c.get(p);
		if (set == null) set = new HashSet<Character>();
		set.add(cCurrent);
		p2c.put(p, set);
		Utils.debug("encodeUpdateMaps mapped cCurr " + cCurrent + " to p " + p + " in p2c"); 
		
		set = c2p.get(cCurrent);
		if (set == null) set = new HashSet<Character>();
		set.add(p);
		c2p.put(cCurrent, set);
		Utils.debug("encodeUpdateMaps mapped cCurr " + cCurrent + " to p " + p + " in c2p"); 
		
		//System.out.println("c2p " + c2p);
		//System.out.println("p2c " + p2c);
	}
	private void encodeUpdatePositions(Integer pos, char cCurrent, char cPrevious, char p) {
		/** map cipher symbols to positions */
		//public Map<Character, List<Integer>> cPos;
		/** map plaintext letters to positions */
		//public Map<Character, List<Integer>> pPos;
		
		// check if previous cipher assignment needs to be removed
		if (cPrevious != ' ' && cPrevious != cCurrent) {
			assert cPos.get(cPrevious) != null : "cPos null for cPrev " + cPrevious + " cCurr " + cCurrent + " p " + p;  
			cPos.get(cPrevious).remove(pos);
			Utils.debug("encodeUpdatePositions removed pos " + pos + " for cPrev " + cPrevious); 
		}
		
		// add position for current assignment
		List<Integer> list = cPos.get(cCurrent);
		if (list == null) list = new ArrayList<Integer>();
		if (!list.contains(pos)) list.add(pos);
		cPos.put(cCurrent, list);
		Utils.debug("encodeUpdatePositions added pos " + pos + " for cCurr " + cCurrent); 
		
		list = pPos.get(p);
		if (list == null) list = new ArrayList<Integer>();
		if (!list.contains(pos)) list.add(pos);
		pPos.put(p, list);
		Utils.debug("encodeUpdatePositions added pos " + pos + " for p " + p); 
		
		//System.out.println("cPos " + cPos);
		//System.out.println("pPos " + cPos);
	}

	/** encode the given plaintext with the given cipher text at the given position */
	public void encode(int index, String plain, String ciphersnippet) {
		Utils.debug("encode " + index + " p " + plain + " c " + ciphersnippet + " hashcode " + hashCode());
		//map(plain, ciphersnippet);
		for (int i=0; i<plain.length(); i++) {
			char cCurrent = ciphersnippet.charAt(i); // incoming cipher symbol
			char p = plain.charAt(i); // corresponding plaintext at this position
			
			int pos = index+i; // position in the full cipher/plaintext
			
			char cPrevious = cipher.cipher.charAt(pos); // check for existing cipher symbol
			char pActual = this.plain.plaintext.charAt(pos);
			if (pActual != '_' && pActual != p) {
				// incoming plaintext does not match actual
				System.err.println("Was provided plaintext [" + p + "] for position [" + pos + "] but actual is [" + pActual + "].");
				continue; // skip encoding this spot
			}
			
			// another situation: incoming plaintext is non filler but actual is filler
			// so we need to use filler character to avoid messing up counts
			if (pActual == '_') p = '_';
			
			genome[index+i+1] = alleleForAlpha(cCurrent);
			// side effect: update the cipher as we go
			cipher.cipher.setCharAt(index+i, cCurrent);
			
			// now update counts, maps, and lists of positions
			encodeUpdateCounts(cCurrent, cPrevious, p);
			encodeUpdateMaps(pos, cCurrent, cPrevious, p);
			encodeUpdatePositions(pos, cCurrent, cPrevious, p);
			
			// assertions:
			// 1) p2c should have mapping for cCurrent
			// 2) p2c should not have mapping for cPrev if its count is 0 or absent 
			// 3) c2p should have mapping for cCurrent
			// 4) c2p should not have mapping for cPrev if its count is 0 or absent
			// 5) counts should be decremented by one for cPrev if it != cCurrent
			// 6) counts should be incremented by one for cCurrent if it != cPrev
			// 7) if cCur != cPrev, position should be in pPos and cPos. 
		}
	}
	
	public void encode(int index, char plain, char cipher) {
		encode(index, ""+plain, ""+cipher);
	}

	/** swap all occurrences of the given cipher symbol with the other given cipher symbol */
	public void encodeSwap(char c1, char c2) {
		char p1 = decode(""+c1).charAt(0);
		char p2 = decode(""+c2).charAt(0);
		
		for (int i=0; i<cipher.cipher.length(); i++) {
			char c = cipher.cipher.charAt(i);
			if (c == c1) 
				encode(i, p2, c2);
			else if (c == c2) 
				encode(i, p1, c1);
		}
	}

	/** replace all occurrences of the given cipher symbol with the other given cipher symbol */
	public void encodeReplace(char c1, char c2) {
		char p2 = decode(""+c2).charAt(0);
		
		for (int i=0; i<cipher.cipher.length(); i++) {
			char c = cipher.cipher.charAt(i);
			if (c == c1) 
				encode(i, p2, c2);
		}
	}
	
	/** encode the given pivot to the given string, starting at the intersection and moving outwards */
	public void encodePivot(List<Integer> positions, String cipherText) {
		int i=0;
		// first position
		encode(positions.get(0), ""+plain.plaintext.charAt(positions.get(0)), ""+cipherText.charAt(0));
		i++;
		while (i<7) { // each pivot has two legs so each cipher symbol appears twice.  (hard coded max size 7 to account for longer pivots that might be found)
			//System.out.println("SMEG encode pivot " + i + " size " + positions.size() + " [" + positions + "]");
			encode(positions.get(i), ""+plain.plaintext.charAt(positions.get(i)), ""+cipherText.charAt((i+1)/2));
			i++;
		}
	}
	

	boolean makeTrigrams() {
		
		// get one of its intersections
		Trigram intersectingTri = candidateConstraints.trigrams.intersectingTrigram;
		Trigram columnarTri = candidateConstraints.trigrams.columnarTrigram;
		
		Utils.debug("intersectingTri " + intersectingTri);
		Utils.debug("columnarTri " + columnarTri);
		
		// get positions of two of the repeating columnar trigrams
		int pos1 = intersectingTri.whichColumnRepeaterPosition;
		int pos2 = pos1;
		while (pos2 == pos1) pos2 = columnarTri.randomPosition(rand);
		
		
		// get positions of the intersecting trigrams
		int pos3 = candidateConstraints.trigrams.intersectingPosition;
		int pos4 = intersectingTri.randomPosition(rand);
		while (pos4 == pos3) pos4 = intersectingTri.randomPosition(rand);
		
		// supply preferred symbol assignments, but if other assignments already exist at shared positions, use them
		// to avoid conflicts
		String tri1 = merge(new int[] {pos1, pos2}, "|5F");
		String tri2 = merge(new int[] {pos3, pos4}, "FBc");

		Utils.debug("pos1 " + pos1 + " pos2 " + pos2 + " pos3 " + pos3 + " pos4 " + pos4);
		
		Utils.debug("encoding trigram c1 " + columnarTri.trigram + " as " + tri1 + " at pos " + pos1);
		encode(pos1, columnarTri.trigram, tri1);  // columnar trigram
		Utils.debug("encoding trigram c2 " + columnarTri.trigram + " as " + tri1 + " at pos " + pos2);
		encode(pos2, columnarTri.trigram, tri1);
		
		Utils.debug("encoding trigram i1 " + intersectingTri.trigram + " as " + tri2 + " at pos " + pos3);
		encode(pos3, intersectingTri.trigram, tri2);  // intersecting trigram
		Utils.debug("encoding trigram i2 " + intersectingTri.trigram + " as " + tri2 + " at pos " + pos4);
		encode(pos4, intersectingTri.trigram, tri2);
		
		// mark the positions we used
		candidateConstraints.trigrams.columnarTrigram.positionsEnciphered = new HashSet<Integer>();
		candidateConstraints.trigrams.columnarTrigram.positionsEnciphered.add(pos1);
		candidateConstraints.trigrams.columnarTrigram.positionsEnciphered.add(pos2);
		candidateConstraints.trigrams.intersectingTrigram.positionsEnciphered = new HashSet<Integer>();
		candidateConstraints.trigrams.intersectingTrigram.positionsEnciphered.add(pos3);
		candidateConstraints.trigrams.intersectingTrigram.positionsEnciphered.add(pos4);
		
		
		return true;
	}
	
	/** we wish to encode the given snippet at the given start positions.  but other symbols
	 * might already exist at those positions.  if so, update the snippet so it reflects the existing
	 * symbols.
	 */
	String merge(int[] positions, String snippet) {
		String newSnippet = "";

		for (int i = 0; i < snippet.length(); i++) {
			char c1 = snippet.charAt(i);
			for (int pos : positions) {
				char c2 = cipher.cipher.charAt(pos+i);
				if (c2 != ' ' && c1 != c2) {
					c1 = c2;
				}
			}
			newSnippet += c1;
		}
		return newSnippet;
	}
	
	boolean makePivots() {
		// step 2: hard code the assignments for the pivots we wish to preserve
		// System.out.println("encoding pivot in " + plain.plaintext);
		List<Pivot> pivots = new ArrayList<Pivot>();
		pivots.add(candidateConstraints.pivotColumnar);
		pivots.add(candidateConstraints.pivotIntersecting);
		//Map<String, List<Pivot>> map = PivotsOLD.sameDirectionPivots(pivots);
		//String key = map.keySet().iterator().next(); // there should be only one
		//System.out.println("making pivots, key " + key + ", columnar " + candidateConstraints.pivotColumnar + " intersecting " + candidateConstraints.pivotIntersecting);
		
		for (int i=0; i<2; i++) {
			Pivot pivot = pivots.get(i);
			Utils.debug("pivot " + pivot);
			String pivotString;
			if (i==0) pivotString = "*|JR";
			else pivotString = "Vc.b";
			encodePivot(pivot.positions, pivotString);
		}
		return true;
	}
	
	/*
	void makePivotsOLD() {
		// step 2: hard code the assignments for the pivots we wish to preserve
		// pick the first one at random
		List<String> directions = new ArrayList<String>();
		directions.addAll(plain.pivotPairs.keySet());
		String key = directions.remove(rand.nextInt(directions.size()));
		List<Pivot> pivots = new ArrayList<Pivot>();
		pivots.addAll(plain.pivotPairs.get(key));
		List<Position> positions;

		//System.out.println("encoding pivot in " + plain.plaintext);
		for (int i=0; i<2; i++) {
			Pivot pivot = pivots.remove(rand.nextInt(pivots.size()));
			for (int j=0; j<2; j++) {
				String pivotString;
				if (i==0) pivotString = "*|JR";
				else pivotString = "Vc.b";
				encodePivot(Pivot.positionsFor(key.charAt(j), pivot), pivotString);
			}
			
		}
	}*/
	boolean makeFolds() {
		// step 3: make the fold lines
		
		FoldMarks fm = candidateConstraints.foldMarks;
		if (fm != null) {
			char p1 = plain.plaintext.charAt(fm.positionLeft);
			char p2 = plain.plaintext.charAt(fm.positionRight);
			encode(fm.positionLeft, p1, '-');
			encode(fm.positionRight, p2, '-');
		}
		return true;
		
		/* TODO
		List<int[]> folds = plain.folds();
		if (!folds.isEmpty()) {
			int[] f = folds.get(rand.nextInt(folds.size()));
			char p1 = plain.plaintext.charAt(f[0]);
			char p2 = plain.plaintext.charAt(f[1]);
			encode(f[0], p1, '-');
			encode(f[1], p2, '-');
		}*/
	}
	
	boolean makeBoxCorners() {
		BoxCornerPair pair = candidateConstraints.boxCornerPair;
		BoxCorner[] corners = pair.corners;
		for (BoxCorner corner : corners) {
			String c = corner.cipher();
			for (int i=0; i<corner.positions().size(); i++) {
				//System.out.println(corner.positions() + ", " + corner.symbols + ", " + c);
				encode(corner.positions().get(i), corner.symbols.charAt(i), c.charAt(i));
			}
		}
		return true;
	}
	
	
	
	boolean makeAssignmentsOLD() {
		// step 4: make sure every letter of the plain text has a cipher equivalent
		boolean failed; 
		String cipherAlphabetShuffled = shuffle(cipherAlphabet);
		String plainAlphabet = Ciphers.alphabet(plain.plaintext);
		
		//System.out.println("makeAss: cipher shuffled: " + cipherAlphabetShuffled);
		//System.out.println("makeAss: alphabet: " + plainAlphabet);
		int j = 0;
		failed = false;
		for (int i=0; i<plainAlphabet.length(); i++) {
			char p = plainAlphabet.charAt(i);
			if (p == '_') continue;
			if (p == ' ') continue;
			
			boolean go = true;
			Character c = null;
			while (go) {
				if (j>cipherAlphabetShuffled.length()-1) break;
				//System.out.println(j+","+cipherAlphabetShuffled.length());
				c = cipherAlphabetShuffled.charAt(j++);
				go = c2p.keySet().contains(c); // already mapped this symbol
				//System.out.println("makeAss: plain [" + p + "]: [" + c + "] already mapped? " + go);
			}
			if (!go) {
				//System.out.println("makeAss: mapping " + p + " to " + c);
				mapOLD(p, c);
			}
			else {
				failed = true;
				break; // couldn't form a complete mapping
			}
		}
		//System.out.println("makeAss: " + dumpp2c() + ", " + dumpc2p());
		return failed;
	}
	
	/** CURRENTLY UNUSED */
	void makeAssignments2() {
		// step 5: for all remaining unassigned cipher symbols, assign them proportionally to the plaintext letter distribution
		
		for (int i=0; i<cipherAlphabet.length(); i++) {
			char c = cipherAlphabet.charAt(i);
			if (c2p.keySet().contains(c)) continue; // already mapped this symbol
			
			char p = '_';
			while (p == '_') {
				p = plain.plaintext.charAt(rand.nextInt(plain.plaintext.length()));
			}
			mapOLD(p, c);
		}
		//System.out.println(dumpc2p());
		//System.out.println(dumpp2c());

		for (int i=0; i<plain.plaintext.length(); i++) {
			char p = plain.plaintext.charAt(i);
			if (p == '_') {
				// since plaintext is filler here, we can pick any symbol we want
				encode(i, p, CandidateKey.randomCipherSymbol());
				continue;
			}
			if (p2c.get(p) != null) {
				List<Character> list = new ArrayList(p2c.get(p));
				if (genome[i+1] > 0) continue; // already made an assignment here
				genome[i+1] = alleleForAlpha(list.get(rand.nextInt(list.size())));
			} else System.out.println("ERROR: No symbol mapping for [" + p + "]!");
		}
	}
	
	static List<String> cipherWordPermutationsFor(String word) {
		List<String> list = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		cipherWordPermutationsFor(list, word, sb, 0);
		return list;
	}
	static boolean hasAny(String str, String search) {
		for (int i=0; i<search.length(); i++) {
			if (str.indexOf(""+search.charAt(i)) > -1) return true;
		}
		return false;
	}
	static void cipherWordPermutationsFor(List<String> list, String word, StringBuffer cipher, int pos) {
		if (pos == word.length()) { // stop condition
			if ("GOD".equals(word)) { // disallow some possibilities
				if (hasAny(cipher.toString(), "789")) return;
			}
			//System.out.println("word " + word + " cipher " + cipher);
			list.add(cipher.toString());
			return;
		}
		
		// current plaintext letter
		char p = word.charAt(pos);
		// recursively process all possible interpretations
		for (int i=0; i<ambiguous.get(p).length(); i++) {
			char c = ambiguous.get(p).charAt(i);
			cipherWordPermutationsFor(list, word, new StringBuffer(cipher).append(c), pos+1);
		}
	}
	
	boolean makeWords() {
		if (!makeWordHer()) {
			failMapIncrement("word her");
		}
		if (!makeWordGod()) {
			failMapIncrement("word god");
		}
		if (!makeWordZodiac()) {
			failMapIncrement("word zodiac");
		}
		debug("after makeWords()");
		
		//expressGenome();
		return errorsEncipherment() == 0;
	}
	
	/** try to encode the repeating ngrams of Z340 in random locations */
	void makeNgramsTODO() {
		// trigrams are already handled by now.  so we only focus on bigrams here.
		
		// first, generate a list of all repeating unassigned bigrams in the plaintext.
		Map<String, List<Integer>> bigramsPlain = new HashMap<String, List<Integer>>();
		for (int i=0; i<plain.plaintext.length()-1; i++) {
			String bigramPlain = plain.plaintext.substring(i,i+2);
			if (bigramPlain.contains("_")) continue; // ignore filler
			List<Integer> val = bigramsPlain.get(bigramPlain);
			if (val == null) val = new ArrayList<Integer>();
			val.add(i);
		}
		for (String key : bigramsPlain.keySet()) {
			if (bigramsPlain.get(key).size() < 2)
				bigramsPlain.remove(key);
		}		
		
		// for each repeating ciphertext bigram:
		// let N be number of repeats for bigram
		// pick N random unassigned plaintext bigrams.
		// encode the ciphertext bigrams in those N spots.
		// skip any overlaps that result

		NGramsBean z340Bigrams = NGramsBean.referenceCipherBeans.get("z340").get(2);
		for (String z340Bigram : z340Bigrams.repeats) {
			int n = z340Bigrams.counts.get(z340Bigram);
		}
	}

	/** returns true if the given cipher text snippet can be plugged into the given position without
	 * violating any mappings
	 */
	public boolean fits(String ciphertext, int position) {
		boolean fits = true;
		
		Map<Character, Character> seen = new HashMap<Character, Character>();
		
		for (int i=0; i<ciphertext.length(); i++) {
			char c1 = ciphertext.charAt(i);
			if (!fits(c1, position+i)) {
				fits = false;
				break;
			}
			
			// possibility: cipher symbol repeats in snippet, so need to make sure it is
			// consistent with existing mapping
			char p1 = plain.plaintext.charAt(position+i);
			if (seen.get(c1) == null) {
				seen.put(c1, p1);
			} else {
				if (seen.get(c1) != p1) { // we already mapped this symbol to something else
					fits = false;
					break;
				}
			}
			
		}
		return fits;
	}
	
	public boolean fits(char c1, int position) {
		char c2 = cipher.cipher.charAt(position);
		if (c2 != ' ' && c2 != c1) return false; // already a different cipher symbol there
		char p1 = decode(c1);
		if (p1 == ' ') return true; // no c2p mapping yet exists
		char p2 = plain.plaintext.charAt(position);
		return p1 == p2; // make sure existing c2p mapping matches the p at the intended position
	}
	
	/**
	 * find all valid places for the given cipher text to fit without violating
	 * existing c2p mappings
	 */
	List<Integer> freeSpotsFor(String ciphertext) {
		List<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < cipher.cipher.length() - ciphertext.length() + 1; i++) {
			if (fits(ciphertext, i))
				list.add(i);
		}
		return list;
	}

	/** find all free spots for a cipher string of length L */
	List<Integer> freeSpotsFor(int L) {
		List<Integer>  list = new ArrayList<Integer>();
		for (int i=0; i<cipher.cipher.length()-L+1; i++) {
			boolean free = true;
			for (int j=0; j<L; j++) {
				if (genome[i+j+1] > -1) {
					free = false;
					break;
				}
			}
			if (free) list.add(i);
		}
		System.out.println("freeSpotsFor " + L + " " + list);
		return list;
	}
	
	
	boolean makeWordHer() {
		String cipherword = "HER";
		List<Integer> free = freeSpotsFor(cipherword);
		Utils.debug("makeWordHer free " + free.toString());
		if (free.isEmpty()) {
			//System.out.println("Can't fit [" + cipherword + "] [" + decode(cipherword) + "] anywhere. " + plain.plaintext + " " + cipher.cipher);
			return false;
		}
		int pos = free.get(rand.nextInt(free.size()));
		String plain = this.plain.plaintext.substring(pos, pos+3);
		encode(pos, plain, "HER");
		return true;
	}
	boolean makeWordGod() {
		List<String> words = cipherWordPermutations.get("GOD");
		String cipherword = words.get(rand.nextInt(words.size()));
		List<Integer> free = freeSpotsFor(cipherword);
		Utils.debug("makeWordGod free " + free.toString());
		if (free.isEmpty()) {
			//System.out.println("Can't fit [" + cipherword + "] [" + decode(cipherword) + "] anywhere. " + plain.plaintext + " " + cipher.cipher);
			return false;
		}
		int pos = free.get(rand.nextInt(free.size()));
		String plain = this.plain.plaintext.substring(pos, pos+3);
		encode(pos, plain, cipherword);
		return true;
	}
	boolean makeWordZodiac() {
		List<String> words = cipherWordPermutations.get("ZODIAC");
		String cipherword = "z" + words.get(rand.nextInt(words.size()));
		List<Integer> free = freeSpotsFor(cipherword);
		Utils.debug("makeWordZodiac " + cipherword + " free " + free.toString());
		if (free.isEmpty()) {
			//System.out.println("Can't fit [" + cipherword + "] [" + decode(cipherword) + "] anywhere. " + plain.plaintext + " " + cipher.cipher);
			return false;
		}
		int pos = free.get(rand.nextInt(free.size()));
		String plain = this.plain.plaintext.substring(pos, pos+7);
		encode(pos, plain, cipherword);
		return true;
	}

	void resetGenome() {
		genome = new float[genome.length];
		for (int i=0; i<genome.length; i++) genome[i] = -1;
		cipher.cipher = new StringBuffer();
		for (int i=0; i<340; i++) cipher.cipher.append(' ');

		resetMaps();
	}
	
	void resetMaps() {
		if (c2p != null) c2p.clear();
		if (p2c != null) p2c.clear();
		if (counts != null) counts.clear();
		if (cPos != null) cPos.clear();
		if (pPos != null) pPos.clear();
	}
	
	void resetCipher() {
		cipher = new CandidateCiphertext();
		cipher.cipher = new StringBuffer(spaces.toString());
	}

	void failMapIncrement(String key) {
		//System.out.println("fail " + key + " " + cipher.cipher);
		Utils.debug("fail " + key + " " + (cipher == null ? "null" : cipher.cipher));
		if (failMap == null) failMap = new HashMap<String, Integer>(); 
		Integer val = failMap.get(key);
		if (val == null) val = 0;
		val++;
		failMap.put(key, val);
		//System.exit(-1);
	}
	
	void debug(String msg) {
		if (Utils.DEBUG) {
			expressGenome();
			System.out.println(msg);
			System.out.println("CIPHER:");
			for (int i=0; i<20; i++) {
				System.out.println(cipher.cipher.substring(i*17, i*17+17));
			}
			System.out.println(dumpp2c());
			System.out.println(dumpc2p());
		}
	}
	
	public void make() {
		
		/** make entire population from a single starting genome */
		if (USE_SINGLE_STARTER) {
			if (genomeForInit != null) {
				genome = genomeForInit.clone();
				int which = CandidatePlaintextLoader.find(plaintextForInit);
				if (which > -1) {
					genome[0] = genomeValueFromIndex(which); // fix in case indexes have changed
					plain = CandidatePlaintextLoader.get(which);
					expressGenome();
					// force cache update to avoid cacheMiss error
					errors(true);
					System.out.println("errors after single init: " + errors(true));
				}
				return;
			}
		}
		
		if (cipher == null) cipher = new CandidateCiphertext();
		
		boolean failed = true;
		int tries = 0;
		
		failMap = new HashMap<String, Integer>();
		TranscriptionErrors.MAX_ERRORS = 0; // temporarily disallow transcription errors
		while (failed) {
			tries++;
			resetGenome();
			if (tries > 0 && tries % 1000 == 0) {
				System.out.println("retry #" + tries + "...");
				System.out.println(failMap);
			}
			
			int which = rand.nextInt(CandidatePlaintextLoader.size());
			genome[0] = toFloat(which,0,CandidatePlaintextLoader.size()-1);
			this.plain = CandidatePlaintextLoader.get(which);
			this.plain.candidateKey = this;
			
			Utils.debug("Using plaintext " + this.plain.toString(true));
			// check criteria since it generates useful objects as side effects
			if (!this.plain.criteriaAll()) {
				failMapIncrement("criteriaAll");
				continue;
			}
			
			if (Utils.DEBUG) {
				for (CandidateConstraints cc : plain.candidateConstraints) {
					Utils.debug("available constraint " + cc);
					Utils.debug("ciphers[0] = \"" + plain.plaintext + "\"; init();");
					Utils.debug(cc.highlight());
				}
			}

			
			
			
			// select a random set of constraints to enforce
			this.candidateConstraints = this.plain.candidateConstraints.get(rand.nextInt(this.plain.candidateConstraints.size()));
			Utils.debug("selected cc " + this.candidateConstraints.toString());
			//System.out.println("Loaded " + this.plain);

			p2c = new HashMap<Character, Set<Character>>();
			c2p = new HashMap<Character, Set<Character>>();

			// encode the pivots, trigrams, fold marks, and box corner pairs
			// then, ensure they are still there after expressing genome.
				
			boolean mp = makePivots();
			debug("after makePivots()");
			if (errorsEncipherment() > 0) {
				failMapIncrement("pivots");
				continue;
			}
			//expressGenome(); System.out.println("after folds " + errorsEncipherment());
			//expressGenome();
			//Utils.debug(cipher.cipher);
			boolean mb = makeBoxCorners();
			debug("after makeBoxCorners()");
			if (errorsEncipherment() > 0) {
				failMapIncrement("boxcorners");
				continue;
			}
			//expressGenome(); System.out.println("after trigrams " + errorsEncipherment());
			//expressGenome();
			//Utils.debug(cipher.cipher);
			boolean mf = makeFolds();
			debug("after makeFolds()");
			if (errorsEncipherment() > 0) {
				failMapIncrement("folds");
				continue;
			}
			//expressGenome(); System.out.println("after pivots " + errorsEncipherment());
			//expressGenome();
			//Utils.debug(cipher.cipher);
			
			boolean mt = makeTrigrams();
			debug("after makeTrigrams()");
			if (errorsEncipherment() > 0) {
				failMapIncrement("trigrams");
				continue;
			}
			//expressGenome(); System.out.println("after boxcorners " + errorsEncipherment());
			
			if (!makeWords()) {
				failMapIncrement("words");
				continue;
			}
			
			//expressGenome(); System.out.println("after words " + errorsEncipherment());
			
			
			//expressGenome();
			//makeNgrams();
			
			//expressGenome();
			
			//Utils.debug(cipher.cipher);
			//SymbolAssignments.makeAssignments(this);
			
			makeRandomAssignments(); // because SymbolAssignments.makeAssignments sometimes leaves unassigned letters, making the cipher invalid 
			debug("after makeAssignments()");
			if (errorsEncipherment() > 0) {
				failMapIncrement("assignments");
				continue;
			}
			
			// for some reason, there may still be spaces in the cipher text.  fix them here.
			fixSpaces();
			debug("after fixSpaces()");
			
			//makeAssignments2();
			//makeAssignmentsGenome();
			//expressGenome();
			//System.out.println("after assignments " + errorsEncipherment());
			
			//Utils.debug("ERRORS: " + errorsEncipherment());
			
			// cipher is invalid if it contains any spaces (nulls).
			// this is because i am trying to avoid the search exploiting the "hidden" effects of nulls
			if (cipher.cipher.toString().contains(" ")) {
				failMapIncrement("spaces");
				continue;
			}
			
			if (errors(true) > 0) {
				failMapIncrement("most");
				continue;			
			}

			/*
			CandidatePlaintext cp = new CandidatePlaintext(0, cipher.cipher);
			boolean success = cp.criteriaAll();
			Utils.debug("SUCCESS? " + success);
			//System.exit(-1);
			if (cp.candidateConstraints == null || cp.candidateConstraints.isEmpty()) {
				failMapIncrement(failMap, "criteriaAll");
				Utils.debug("fail criteriaAll");
				continue;			
			}*/
			
			failed = false;
			
			//makeBoxCornerPairs();
			
			//System.out.println("after pivots: " + this);
			
			// don't bother if there are no pivots
			//if (!hasPivots()) {
				//System.out.println("ERROR: pivots, wtf");
			//	failMapIncrement(failMap, "pivots");
			//	continue;
			//}
			/*
			if (!makeTrigrams()) {
				failMapIncrement(failMap, "trigrams");
				continue;
			}
			expressGenome();
			//System.out.println("after trigrams: " + this);
			if (errorsEncipherment() > 0) {
				failMapIncrement(failMap, "encipherment1");
				continue;			
			}
			
			makeFolds();
			expressGenome();
			//System.out.println("after folds: " + this);
			if (errorsEncipherment() > 0) {
				failMapIncrement(failMap, "encipherment2");
				continue;			
			}
			
			//makeWords();
			expressGenome();
			//System.out.println("after words: " + this);
			if (errorsEncipherment() > 0) {
				failMapIncrement(failMap, "encipherment3");
				continue;			
			}
			
			failed = makeAssignments();
			if (failed) {
				failMapIncrement(failMap, "assignments1");
				continue;
			}
			//expressGenome();
			//System.out.println("after assignments: " + this);
			//if (errorsEncipherment() > 0) continue;			
			
			makeAssignments2();
			makeAssignmentsGenome();
			
			expressGenome();
			//System.out.println("after assignments2: " + this);
			failed = errors() > 0;
			if (failed) {
				failMapIncrement(failMap, "assingments2");
			}
			*/
			
				
		}
		TranscriptionErrors.MAX_ERRORS = 20; // restore allowance for transcription errors
		
		System.out.println("Made after " + tries +" tries, failMap [" + dump(failMap) + "]: " + this);
		//System.out.println("GENOME ZERO " + genome[0]);
		if (USE_SINGLE_STARTER)
			genomeForInit = genome;
	}
	
	boolean hasNonTrivialAssignment(Set<Character> set) {
		Set<Character> compare = new HashSet<Character>(set);
		compare.remove('_');
		compare.remove(' ');
		return !compare.isEmpty();
	}
	
	char popRandom(StringBuffer sb) {
		if (sb == null || sb.length() == 0) return 0;
		int which = rand.nextInt(sb.length());
		char c = sb.charAt(which);
		sb.deleteCharAt(which);
		return c;
	}
	
	void makeRandomAssignments() {
		
		StringBuffer alphabetC = new StringBuffer(cipherAlphabet);
		
		// make a local plain/cipher map
		Map<Character, Set<Character>> map = new HashMap<Character, Set<Character>>();
		// load it with mappings that have already been created
		for (Character key : p2c.keySet()) {
			Set<Character> val = p2c.get(key);
			for (Character c : val) {
				if (c == '_') continue;
				if (c == ' ') continue;
				
				Set<Character> set = map.get(key);
				if (set == null) set = new HashSet<Character>();
				set.add(c);
				alphabetC.deleteCharAt(alphabetC.indexOf(""+c)); // reduce the cipher alphabet
				map.put(key, set);
			}
		}
		
		// make sure each plaintext letter has at least one cipher symbol to start
		for (int i=0; i<plainAlphabet.length(); i++) {
			char p = plainAlphabet.charAt(i);
			Set<Character> set = map.get(p);
			if (set == null) set = new HashSet<Character>();
			else continue;
			set.add(popRandom(alphabetC));
			map.put(p, set);
		}
		
		// now assign symbols randomly
		while (alphabetC.length() > 0) {
			char c = popRandom(alphabetC);
			char p = plainAlphabet.charAt(rand.nextInt(plainAlphabet.length()));
			map.get(p).add(c);
		}

		// now perform the encoding
		for (int i=0; i<cipher.cipher.length(); i++) {
			char c = cipher.cipher.charAt(i);
			char p = plain.plaintext.charAt(i);
			if (c != ' ') continue;
			if (p == '_') {
				// filler, so pick a random symbol
				encode(i, p, randomCipherSymbol());
			} else {
				List<Character> list = new ArrayList<Character>(map.get(p));
				encode(i, p, list.get(rand.nextInt(list.size())));
			}
		}
	}
	
	/** look for spaces that might remain in the expressed cipher text, and replace them with assigned symbols */
	void fixSpaces() {
		expressGenome();
		for (int i=0; i<cipher.cipher.length(); i++) {
			char c = cipher.cipher.charAt(i);
			if (c == ' ') {
				char p = plain.plaintext.charAt(i);
				Set<Character> set = p2c.get(p);
				if (set == null) set = new HashSet<Character>(); 
				List<Character> list = new ArrayList<Character>(set);
				Character space = ' ';
				list.remove(space);
				char chosen;
				if (set == null || list.isEmpty()) {
					chosen = randomCipherSymbol();
				} else {
					chosen = list.get(rand.nextInt(list.size()));
				}
				encode(i, p, chosen);
			}
		}
	}
	String dump(Map<String, Integer> map) {
		String s = "";
		if (map == null) return s;
		for (String key : map.keySet()) s += key + ":" + map.get(key) + " ";
		return s;
		
	}
	
	public void makeAssignmentsGenome() {
		Map<Character, List<Character>> cipherSequences = new HashMap<Character, List<Character>>();
		Map<Character, Integer> positions = new HashMap<Character, Integer>();
		for (Character key : p2c.keySet()) {
			cipherSequences.put(key, new ArrayList<Character>(p2c.get(key)));
			positions.put(key, 0);
		}
		
		for (int i=0; i<plain.plaintext.length(); i++) {
			if (genome[i+1] > -1) continue; // assignment already made
			char p = plain.plaintext.charAt(i);
			if (!positions.containsKey(p)) continue;
			if (!cipherSequences.containsKey(p)) continue;
			int pos = positions.get(p);
			char c = cipherSequences.get(p).get(pos);
			genome[i+1] = alleleForAlpha(c);
			positions.put(p, pos+1);
		}
	}
	
	public static void testNGrams() {
		String cipher = "d9JX_lcW3YbA#K|V3)#2;BO<c(RccpklV+JRcCp^EPzL59S^pTUL*++-BDAUfNBz+DPKTB8MqFM|_%<(2W45yNW|*|VZ.42fp*j6KG1l;kOXPz-tYp_GK+5G1TM6dzbF+++++++++++++qB>EBt:pRLtpRCSM@MG^lFkGMlKOW58|./|U|-p^yF.42|S<p*G<CF*L2BV+RH7cUyN5+.&Fy/zHcd#(+dd9k|W2YT>5#2|t7*l)#&+)>7LF^Ofp<VzZV+<-F1OBzELR.4clU82CFKO^c(HZBN8MTS)jFWTzc>J+)bpCO(4JZR:5OK.RN(y;FYD/p(fHBO542B6B9Dk";
		NGramsBean bean = new NGramsBean(2, cipher);
		bean.dump();
	}
	
	public static void testWords() {
		CandidateKey key = new CandidateKey();
		key.cipher = new CandidateCiphertext("M+G#(K*Op5H2C^MyUt<+E%4GCO(2lp7KLU^>MVpl:Y|R5;.BFz*Z8/^Kb(CH4J)t_<+|K&fZLNRzWzf2#*-FBT+PVR922j+VfK|U96cb)|5FBcq#Sj/VZfS_kBy|U&);F1lH+LkJ/#t-*cA3BRNz(l<G5d43OkcFSPT+pM+;5Y-y+t(d|79pjOC<pG-HERyB*lN+DVd.4^KN#^d6+1#Vc.bETYX;qYc+L@p8cfV-_M+6O:*|JR|p4.+yNpWSGcM|d<BklEb8(O+Tp-#DJKBO*pc1W24OyO+^7RSl>++DZWBCKzBO2c>Gt)LFBcP>T2Rqk-.UzW|5F+B#(lFXD>k_");
		System.out.println(key.hasWords());
		System.out.println(key.words());
		
	}
	public static void testPermutations() {
		for (String word : zodiacPermutations) System.out.println(word);
	}

	public Object clone() {
		CandidateKey key = (CandidateKey) super.clone();
		if (this.plain == null) key.plain = null; else {
			key.plain = new CandidatePlaintext(this.plain.index, this.plain.plaintext);
		}
		if (this.cipher == null) key.cipher = null; else key.cipher = new CandidateCiphertext(this.cipher.cipher.toString());
		key.c2p = null;
		key.p2c = null;
		key.cPos = null;
		key.pPos = null;
		key.counts = null;
		key.evaluated = false;
		key.archive = archive;
		key.transcriptionErrors = 0;
		key.transcriptionErrorsResults = null;
		if (lastObjectives != null) key.lastObjectives = lastObjectives.clone();
		/*float[] obj = key.objectives();
		if (obj != null) for (int i=0; i<obj.length; i++) obj[i] = 0;
		*/
		if (this.fitness != null) key.fitness = (Fitness) this.fitness.clone();
		
		return key;
	}

	@Override
	public String fit() {
		// TODO Auto-generated method stub
		Fitness fit = this.fitness;
		return fit.fitnessToStringForHumans();
	}
	
	public double[] objectives() {
		if (fitness != null) return ((MultiObjectiveFitness)fitness).getObjectives();
		return null;
	}
	
	/** effectively kill off this individual by punishing its objectives */
	public void punish() {
		double[] objectives = objectives();
		if (objectives == null) return;
		for (int i=0; i<objectives.length; i++) 
			objectives[i] = Double.MAX_VALUE;
	}

	public String objectivesToString() {
		double[] obj = objectives();
		if (obj == null) return "";
		return Arrays.toString(obj) + " actual " + Arrays.toString(objectivesActual);
	}

	@Override
	public String dump() {
		return toString() + " genome " + genome();
	}

	@Override
	public String html() {
		// TODO Auto-generated method stub
		return "TODO: make some html";
	}

	@Override
	public void defaultCrossover(EvolutionState state, int thread,
			VectorIndividual ind) {
		
		if (1==1) return;

		if (this.isBroken()) {
			System.err.println("defaultCrossover on this broken key " + this);
			System.exit(-1);
		}
		if (((CandidateKey)ind).isBroken()) {
			System.err.println("defaultCrossover on that broken key " + (CandidateKey)this);
			System.exit(-1);
		}
		System.out.println("crossover before: archive " + archive + " " + this.hashCode() + " " + ind.hashCode());
		if (archive) {
			Utils.debug("SMEG will not cross to this " + Utils.info(this));
			return;
		} else if (((CandidateKey)ind).archive) {
			Utils.debug("SMEG will not cross with that " + Utils.info(ind));
			return;
		} else {
			Utils.debug("SMEG crossover to " + Utils.info(this) + " with " + Utils.info(ind));
			super.defaultCrossover(state, thread, ind);
		}
		//System.out.println("crossover after: archive " + archive + " " + this.hashCode() + " " + ind.hashCode());
	}
	
	/** returns true if the given cipher symbol can be mapped to the given plaintext symbol.
	 * NOTE: does not take permitted transcription errors into account.
	 **/
	public boolean isValidAssignment(char c, char p) {
		return c2p.get(c) == null || c2p.get(c).contains(p); 
	}
	
	/** return true if at least one of this individual's objectives indicates an error state */ 
	public boolean isBroken() {
		double[] objectives = ((MultiObjectiveFitness) fitness)
				.getObjectives();
		for (double f : objectives) if (f == Double.MAX_VALUE) return true;
		return false;
	}
	
	/** pick a cipher symbol at random */
	public static char randomCipherSymbol() {
		return cipherAlphabet.charAt(rand.nextInt(cipherAlphabet.length()));
	}

	/** pick a plain text letter at random */
	public static char randomPlaintextLetter() {
		return plainAlphabet.charAt(rand.nextInt(plainAlphabet.length()));
	}

	/** returns true if this individual is dominated by the given individual */
	public boolean isDominatedBy(double[] objectives) {
		MultiObjectiveFitness fitThis = (MultiObjectiveFitness)this.fitness;
		return isDominatedBy(fitThis.getObjectives(), objectives);
	}
	/** returns true if objectives1 is dominated by objectives2 */
	public static boolean isDominatedBy(double[] objectives1, double[] objectives2) {
		//at least one objective2 has to be better.
		//none of objectives1 can be better.
		
		boolean found = false;
		for (int i=0; i<objectives1.length; i++) {
			double f1 = objectives1[i];
			double f2 = objectives2[i];
			if (f1 < f2) return false;
			if (f2 < f1) found = true;
		}
		return found;
	}
	
	public static boolean equal(double[] objectives1, double[] objectives2) {
		if (objectives1 == null || objectives2 == null) return false;
		for (int i=0; i<objectives1.length; i++) {
			if (objectives1[i] != objectives2[i]) return false;
		}
		return true;
	}
	
	public String c() {
		return cipher.cipher.toString();
	}
	
	/** compute objectives' total distance from zero (origin). */  
	public double distanceFromZero() {
		double[] o = objectives();
		double sum = 0;
		for (double d : o) sum += d*d;
		return Math.sqrt(sum);
	}
	
	
	
	public static void testFit() {
		CandidateKey key = new CandidateKey(new float[341]);
		key.resetGenome();
		key.encode(
				0,
				"OFABATTALIONFFROMTHESUBURBSMASSEDIN__________________THERUESAINTDENISENJOLRASONTHEWATCHTHOUGHTHEDISTINGUISHEDTHEPECULIARSOUNDWHICHEISPRODUCEDWHENWHENTHESSHELLSOFGRAPESHOTAREDRAWNFROMTHECAISSONSANDHESAWTHECOMMANDEROFTHEPIECECHKANGETHEELEVATIONANDINCLINETHEMOUTHOFTHECANNONSLIGHTLYTOTHELEFTTHENTHECANNONEERSBEGANTOLOADTHEPIECETHECHIEFSEIZEDTH",
				"                                                              *|JR             |            Vc.bJ            c   R            .                b   FBc                    -               -                                                      *-               |   |5F                       |5FB|               -*                              ");
		System.out.println(key.decode("HER"));
		System.out.println(key.freeSpotsFor("HER"));
	}
	
	public static void testEncode() {
			CandidateKey key = new CandidateKey();
			key.genome = new float[341];
			
			String p = "USESAIDTHEOLDMANTHEREISNOWINEWHATTNOWINESAIDDANTESTURNINGPALEANDLOOKINGALTERNATELYATTHEHOLLOWCHEEKSOFTHEOLDMANANDTHEEMPTY________________CUPBOARDSWHATNOWINEHAVEYOUWANTEDMONEYFATHERIWANTNOTHINGNOWTHATIHAVEYIUSAIDTHEOLDMANYETSTMMEREDDANTESWIPINGTHEPERSPIRATIONFROMHISBROWYSTIGAVEYOUTWOHUNDREDFRACSWHENLEFTTHREEMONTHSAGOYESYESEDMOGDTHATISTRUEB";
			key.plain = new CandidatePlaintext();
			key.plain.plaintext = p;
			key.plain.candidateKey = key;
			key.cipher = new CandidateCiphertext();
			
			key.resetGenome();
			key.init();
			
			Map<Character, Character> map = new HashMap<Character, Character>();
			map.put('A','#');
			map.put('B','%');
			map.put('C','&');
			map.put('D','(');
			map.put('E',')');
			map.put('F','*');
			map.put('G','+');
			map.put('H','-');
			map.put('I','.');
			map.put('J','/');
			map.put('K','1');
			map.put('L','2');
			map.put('M','3');
			map.put('N','4');
			map.put('O','5');
			map.put('P','6');
			map.put('Q','7');
			map.put('R','8');
			map.put('S','9');
			map.put('T',':');
			map.put('U',';');
			map.put('V','<');
			map.put('W','>');
			map.put('X','@');
			map.put('Y','^');
			map.put('Z','_');

			for (int i=0; i<p.length(); i++) {
				if (p.charAt(i) != '_') key.encode(i, p.charAt(i), map.get(p.charAt(i)));
			}

			System.out.println(key.cipher.cipher);
			System.out.println(key.decode(key.cipher.cipher.toString()));
			
			//key.init();
			//key.resetGenome();
			//key.make();
			/*
			key.encode(103,"D", "*");
			key.encode(120,"A", "|");
			key.encode(104,"A", "|");
			key.encode(137,"N", "J");
			key.encode(105,"N", "J");
			key.encode(154,"D", "R");
			key.encode(106,"D", "R");
			key.encode(272,"H", "V");
			key.encode(289,"E", "c");
			key.encode(273,"E", "c");
			key.encode(306,"R", ".");
			key.encode(274,"R", ".");
			key.encode(323,"A", "b");
			key.encode(275,"A", "b");
			key.encode(276,"ATT", "|5F");
			*/
	}

	public static void testGenome() {
		CandidateKey key = new CandidateKey();

		String g1 = "0.94 0.516129 0.32258064 0.8064516 0.048387095 0.14516129 1.0 0.6935484 0.4516129 0.7419355 0.33870968 0.20967741 0.87096775 0.32258064 0.83870965 0.91935486 0.2580645 0.88709676 0.27419356 0.09677419 0.66129035 0.9032258 0.30645162 0.983871 0.9516129 0.7419355 0.6935484 0.24193548 0.33870968 0.5645161 0.61290324 0.16129032 0.4032258 0.37096775 1.0 1.0 0.9354839 0.66129035 0.032258064 0.30645162 0.16129032 0.5483871 0.3548387 0.33870968 0.20967741 0.88709676 0.7903226 1.0 0.3548387 0.08064516 0.6935484 0.58064514 0.29032257 0.048387095 0.08064516 0.24193548 0.14516129 0.6935484 0.6451613 0.08064516 0.14516129 0.9032258 0.4516129 0.87096775 0.9677419 0.46774194 0.11290322 0.983871 0.22580644 0.08064516 0.62903225 0.37096775 0.5 1.0 0.9354839 0.8548387 0.14516129 0.4516129 0.17741935 0.0 0.9354839 0.016129032 0.91935486 0.5645161 0.87096775 0.67741936 0.66129035 0.29032257 0.06451613 0.38709676 0.7419355 0.08064516 0.4032258 0.7096774 0.9677419 0.983871 0.48387095 0.9032258 0.46774194 0.6935484 0.5967742 0.83870965 0.46774194 0.83870965 0.5 0.5483871 0.4516129 0.048387095 0.9354839 0.5 0.7580645 0.38709676 0.62903225 0.19354838 1.0 0.8548387 0.4032258 0.14516129 0.6935484 0.0 0.29032257 0.37096775 0.048387095 0.8064516 0.09677419 0.5 0.4516129 0.62903225 0.62903225 0.516129 0.11290322 0.08064516 1.0 0.62903225 0.33870968 0.0 0.22580644 0.91935486 0.22580644 0.61290324 0.9677419 0.5 0.5483871 0.38709676 0.62903225 0.24193548 1.0 0.08064516 0.11290322 0.11290322 0.33870968 0.0 0.2580645 0.48387095 0.11290322 1.0 0.9677419 0.5 0.4516129 0.14516129 0.62903225 0.516129 1.0 0.08064516 1.0 0.38709676 0.33870968 0.7903226 0.22580644 0.08064516 0.11290322 0.016129032 0.83870965 1.0 0.09677419 0.5645161 0.9032258 0.82258064 0.8548387 0.8064516 0.29032257 0.37096775 0.7741935 0.06451613 1.0 0.983871 0.7580645 0.983871 0.27419356 0.2580645 1.0 0.5322581 1.0 0.22580644 0.46774194 0.4032258 0.82258064 0.9032258 0.7741935 0.48387095 0.22580644 0.43548387 0.11290322 0.983871 0.0 0.7903226 1.0 0.048387095 0.62903225 1.0 0.22580644 0.46774194 0.4032258 0.82258064 0.032258064 0.22580644 0.7096774 1.0 0.17741935 0.61290324 0.41935483 1.0 0.19354838 0.12903225 0.032258064 0.7741935 1.0 0.30645162 0.5 0.14516129 0.048387095 0.43548387 0.0 1.0 0.4032258 0.5 0.7580645 0.11290322 0.58064514 0.983871 0.41935483 0.7096774 0.30645162 0.17741935 0.32258064 0.5 0.4516129 0.08064516 0.5 0.8548387 0.46774194 0.29032257 0.08064516 0.2580645 1.0 0.06451613 0.22580644 0.5645161 0.06451613 0.8064516 0.16129032 0.87096775 0.09677419 0.62903225 0.27419356 0.048387095 0.83870965 0.43548387 0.5322581 0.83870965 0.82258064 0.67741936 0.6451613 0.6935484 0.9677419 0.08064516 0.91935486 0.11290322 0.983871 0.6935484 0.91935486 0.58064514 0.19354838 0.33870968 0.32258064 0.48387095 0.7580645 0.7258065 0.16129032 0.8064516 0.12903225 0.2580645 0.516129 0.27419356 1.0 0.7580645 0.67741936 0.11290322 0.16129032 0.33870968 0.14516129 0.48387095 0.5483871 0.38709676 0.17741935 0.11290322 0.12903225 0.22580644 0.7580645 0.17741935 0.27419356 0.5483871 0.67741936 0.7741935 0.16129032 0.33870968 0.7903226 0.37096775 0.5483871 0.58064514 0.983871 0.4032258 0.12903225 0.82258064 0.0 0.8064516 1.0 0.9516129 0.67741936 0.7258065 0.983871 0.33870968 0.27419356 0.37096775 0.5483871 0.7258065 0.17741935 0.8064516 0.12903225 0.82258064 0.6935484";
		String[] split = g1.split(" ");
		float[] f = new float[split.length];
		for (int i=0; i<split.length; i++) f[i] = Float.valueOf(split[i]);
		
		key.genome = f;
		key.expressGenome();
		//System.out.println(key);
		double[] obj = new double[5];
		Evolve.objectives56(obj, key.cipher.cipher.toString());
		System.out.println(Arrays.toString(obj));
		
	}
	
	/** returns the first zodiac word found; null if none found */
	static String zodiacWordIn(String cipher) {
		for (String z : zodiacPermutations) {
			
			for (int i=0; i<cipher.length(); i++) {
				boolean found = true;
				String partial = "";
				for (int k=0; k<z.length(); k++) {
					char c = cipher.charAt(i+k);
					Set<Character> set = ambiguousSet.get(z.charAt(k));
					if (!set.contains(c) || i+k>=cipher.length()-1) {
						found = false;
						break;
					}
					partial += c; 
				}
				//System.out.println(partial);
				if (found) return cipher.substring(i, i+z.length());
			}
		}
		return null;
	}
	
	public static void shuffleZodiac(String cipher, int shuffles) {
		StatsWrapper stats = new StatsWrapper();
		stats.actual = 1;
		for (int i = 0; i < shuffles; i++) {
			cipher = CipherTransformations.shuffle(cipher);
			String z = zodiacWordIn(cipher);
			if (z != null) {
				System.out.println(z);
				stats.addValue(1);
			} else
				stats.addValue(0);
		}
		stats.output();
	}
	
	/** find "ZO8A|K" in shuffles.  keeps going indefinitely. */
	public static void shuffleZodiac2(String cipher) {
		//StatsWrapper stats = new StatsWrapper();
		//stats.actual = 1;
		long shuffles = 0;
		while (true) {
			cipher = CipherTransformations.shuffle(cipher);
			shuffles++;
			if (cipher.contains("ZO8A|K")) {
				//stats.addValue(1);
				System.out.println("Shuffle #" + shuffles + ": " + cipher);
				//stats.output();
			} //else
				//stats.addValue(0);
			if (shuffles%100000000==0) System.out.println(shuffles+"...");
		}
	}
	public static void main(String[] args) {
		//testConversions();
		//testFit();
		//testNGrams();
		//testEncode();

		/*
		for (int i=0; i<Ciphers.cipher.length; i++) {
			Cipher c = Ciphers.cipher[i];
			if (c.forComparison) {
				System.out.println(measureNgramDistance(2, c.cipher) + " " + measureSymbolAgnosticUnigramDistance(c.cipher, true) + " " + Ciphers.cipher[i].description);
			}
		}*/
		
		//String c = "t_U|+<*4#L<|5FyL3MH5*pp2Upk2ybjjN:OC@FBc5pGR3|5FBcNY-cYDzW2L#U/SG#>kdGO@CMl|zC8.RT/M.*lDFBcW2-^|5;d)XD;Mp2|5PB1f|A#LkM^7k@-^SpL/9-z<Y(zl3AbN2UctSp2pMCOc(SbB-2M(WzkW|A#Kz2-.Kz)XG7^qX65BDZ-#c|zGz#3R|O1fWAZOTVc.b&34.DSCK3yp8qk659(R1^BpWAJ2ROb>k(.dJOOG&.yNRC.pO:@CS|:Zd(G9JRPYOHM%4z*|JRSkBl%OEZ9lAGLc-5XX++*+++&++FF++E+KA%XG_P+Wz.XP&G<f%-WzCV42";
		//System.out.println(findInString("|5F", c));
		
		//testWords();
		//testPermutations();
		//System.out.println(zodiacWordIn("abcdeZD-7k"));
		//shuffleZodiac(Ciphers.Z340, 10000000);
		shuffleZodiac2(Ciphers.Z340);
		//System.out.println(ambiguous);
		//testGenome();
	}
	
}
