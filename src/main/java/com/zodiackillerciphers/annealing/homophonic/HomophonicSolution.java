package com.zodiackillerciphers.annealing.homophonic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.zodiackillerciphers.annealing.Solution;
import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.lucene.NGrams;
import com.zodiackillerciphers.lucene.Stats;
import com.zodiackillerciphers.lucene.ZKDecrypto;
import com.zodiackillerciphers.tests.LetterFrequencies;

public class HomophonicSolution extends Solution {

	/** the ciphertext */
	public String ciphertext;
	/** the putative plaintext */
	public String plaintext;
	/** zkscore for putative plaintext */
	public float score;
	/** the key. maps cipher to plaintext. */
	public Map<Character, Character> key;
	/** track original key values, to reverse mutations. */
	public Map<Character, Character> keyReverse;
	/** the cipher alphabet */
	public String cipherAlphabet;
	/** the plaintext alphabet */
	public String plainAlphabet;
	
	public Random random = new Random();

	public HomophonicSolution() {		
	}
	
	public HomophonicSolution(String ciphertext) {
		this(ciphertext, null);
	}
	public HomophonicSolution(String ciphertext, String plaintext) {
		this.ciphertext = ciphertext;
		this.plaintext = plaintext;
		iterations = 0;
	}
	
	public void put(char c, char p) {
		// preserve original value
		if (keyReverse.get(c) == null)
			keyReverse.put(c, key.get(c));
		// put new value in key
		//System.out.println("key before: " + key);
		char pOld = key.get(c);
		key.put(c,  p);
//		System.out.println("put " + c + " from " + pOld + " to " + p);
	}
	
	@Override
	public boolean mutate() {

		// try different kinds of mutation
		
		int which = random.nextInt(4);
		
		if (which < 2) { // twice of often, due to mutator stats showing this is a good mutator
			// 1) randomly assign plaintext symbol to one or more cipher symbols in the key
			int count = random.nextInt(3)+1;
			lastMutator = "random plaintext x" + count;
			for (int i=0; i<count; i++) {
				char c = cipherAlphabet.charAt(random.nextInt(cipherAlphabet.length()));
				char p = LetterFrequencies.randomLetter().toUpperCase().charAt(0);
//				System.out.println("mut 1");
				put(c, p);
				//System.out.println(i + " mut1 c " + c + " p " + p + " key before " + keyBefore + " after " + key);
			}
		} else if (which == 2) {
			// 2) randomly swap one or more pairs of assignments in the key
			int count = random.nextInt(3)+1;
			lastMutator = "random swap x" + count;
			for (int i=0; i<count; i++) {
				int a = random.nextInt(cipherAlphabet.length());
				int b = a;
				while (a==b)
					b = random.nextInt(cipherAlphabet.length());
				char c1 = cipherAlphabet.charAt(a);
				char c2 = cipherAlphabet.charAt(b);
				char p = key.get(c1);
//				System.out.println("mut 2");
				put(c1, key.get(c2));
				put(c2,  p);
				//System.out.println(i + " mut2 c1 " + c1 + " c2 " + c2 + " key before " + keyBefore + " after " + key);
			}
		} else if (which == 3) {
			// 3) pick a random ngram fragment and stick it somewhere
			int n = random.nextInt(4) + 2;
			lastMutator = "random " + n + "gram";
			List<String> ngrams = NGramPool.ngramsList.get(n);
			int i = random.nextInt(ngrams.size());
			String ngram = ngrams.get(i);
			int pos = random.nextInt(ciphertext.length()-n+1);
//			System.out.println("mut 3");
			for (int j=0; j<n; j++) {
				char c = ciphertext.charAt(pos+j);
				put(c, ngram.charAt(j));
			}
		}

		return true;
	}
	@Override
	public void mutateReverse() {
		for (Character c : keyReverse.keySet()) {
//			System.out.println("reverting " + c + " to " + keyReverse.get(c));
			key.put(c, keyReverse.get(c));
		}
		keyReverse.clear();
		//System.out.println("after reverse: " + key);
	}
	@Override
	public void mutateReverseClear() {
//		System.out.println("keeping, so clearing mutation history.");
		keyReverse.clear();
	}
	
	
	
	public static void diffKeys(Map<Character, Character> key1, Map<Character, Character> key2) {
		for (Character key : key1.keySet()) {
			if (!key2.containsKey(key)) 
				System.out.println("key2 lacks key: " + key);
			else 
			{
				char val1 = key1.get(key);
				char val2 = key2.get(key);
				if (val1 != val2) {
					System.out.println("key1: " + key + "(" + val1 + ", " + val2 + ")");
				}
			}
		}
		for (Character key : key2.keySet()) {
			if (!key1.containsKey(key)) 
				System.out.println("key1 lacks key: " + key);
		}
	}

	@Override
	public String representation() {
		// TODO Auto-generated method stub
//		return percentMatchesZ408Solution() + "	" + decode() + "	" + key;
		return decode() + "	" + key;
	}
	
//	public int percentMatchesZ408Solution() {
//		int matches = 0;
//		for (int i=0; i<plaintext.length(); i++) {
//			if (plaintext.charAt(i) == Ciphers.Z408_SOLUTION.toUpperCase().charAt(i))
//				matches++;
//		}
//		return 100*matches/408;
//	}

	@Override
	public double energy() {
		decode();
		
//		int matches = 0;
//		for (int i=0; i<plaintext.length(); i++) {
//			if (plaintext.charAt(i) == Ciphers.Z408_SOLUTION.toUpperCase().charAt(i))
//				matches++;
//		}
//		if (1==1) return -matches;
		
		score = ZKDecrypto.calcscore(plaintext);
		//zkscore = NGrams.zkscore2(plaintext);
		return -score;
	}
	
	public static void dumpStats(String plaintext) {
		System.out.println("plaintext: " + plaintext);
		System.out.println("- zkscore: " + NGrams.zkscore(new StringBuffer(plaintext)));
		System.out.println("- zkscore2: " + NGrams.zkscore2(plaintext));
		System.out.println("- calcscore: " + ZKDecrypto.calcscore(plaintext));
		System.out.println("- ioc: " + Stats.ioc(plaintext) + " diff: " + Stats.iocDiff(plaintext));
		System.out.println("- chi2: " + Stats.chi2(plaintext) + " diff: " + Stats.chi2Diff(plaintext));
		System.out.println("- entropy: " + Stats.entropy(plaintext) + " diff: " + Stats.entropyDiff(plaintext));
	}

	@Override
	public double energyCached() {
		return -score;
	}

	@Override
	public void initialize() {
		key = new HashMap<Character, Character>();
		keyReverse = new HashMap<Character, Character>();
		cipherAlphabet = Ciphers.alphabet(ciphertext);
		if (plaintext == null) {
			for (int i = 0; i < cipherAlphabet.length(); i++) {
				char c = cipherAlphabet.charAt(i);
				char p = LetterFrequencies.randomLetter().toUpperCase().charAt(0);
				key.put(c, p);
			}
		} else {
			// otherwise, build the key based on the given plaintext
			for (int i=0; i<plaintext.length(); i++) {
				key.put(ciphertext.charAt(i), plaintext.charAt(i));
			}
		}
		
	}
	
	@Override
	public Solution clone() {
		HomophonicSolution newSol = new HomophonicSolution(this.ciphertext, this.plaintext);
		newSol.score = this.score;
		newSol.cipherAlphabet = this.cipherAlphabet;
		newSol.plainAlphabet = this.plainAlphabet;
		newSol.key = new HashMap<Character, Character>();
		for (Character k : this.key.keySet())
			newSol.key.put(k, this.key.get(k));
		newSol.iterations = this.iterations;
		newSol.temperature = this.temperature;
		return newSol;

	}

	/** decode the cipher with the current key */
	public String decode() {
		plaintext = "";
		for (int i = 0; i < ciphertext.length(); i++) {
			plaintext += key.get(ciphertext.charAt(i));
		}
		return plaintext;
	}

	public static void test() {
		HomophonicSolution hom = new HomophonicSolution(Ciphers.Z340);
		hom.initialize();
		System.out.println(hom.key);
	}
	public static void testPartial() {
		HomophonicSolution hom = new HomophonicSolution(Ciphers.Z408, "INODEDATNATHFEAFTEHECOURAITORSOMUCHFUTASALMOREBUTTHINDINTOTHSANDHEMEATTHEFORCERTHECSUREMOTISSHAMASTWINHERSUEETSMOTOBINTTODONTRAMETHATHHAVESMESHEMOSTTHRINTOTHERFACENCEATAREVETHESTERTHETHATSITHYOURCOCDRAFBSOTHSHARNTHEHESTFORSOFATISTHEESHENOWAEASINTHECEHORTOTFIREDACALTWONNSHEAHIVEDINNEDSONTHECAMEMYRNSVERISITNTOTHAVEYOUMYTIMEHECOUSEYOUSINNTRYTORTAIWASTOCLTOFMYCONNACTOTHABRNOVERFORMYSFSERTOBEAHEARIESAMETHHFASA");
		hom.plainAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		hom.initialize();
		System.out.println(hom.key);
		hom.energy();
		System.out.println(hom);
		double energy = hom.energy();
		long hits = 0;
		long total = 0;
		for (int i=0; i<hom.cipherAlphabet.length(); i++) {
			for (int j=0; j<hom.plainAlphabet.length(); j++) {
				char c = hom.cipherAlphabet.charAt(i);
				char p = hom.plainAlphabet.charAt(j);
				char pPrev = hom.key.get(c);
				total++;
				hom.key.put(c, p);
				double newEnergy = hom.energy();
				if (newEnergy < hom.energy()) {
					System.out.println("better: " + hom);
					hits++;
				}
				hom.key.put(c,  pPrev);
			}
		}
		System.out.println(hits + " out of " + total);

		hits = 0; total = 0;
		for (int i=0; i<hom.cipherAlphabet.length(); i++) {
			for (int j=0; j<hom.cipherAlphabet.length(); j++) {
				if (i==j) continue;
				char c1 = hom.cipherAlphabet.charAt(i);
				char c2 = hom.cipherAlphabet.charAt(j);
				char p1 = hom.key.get(c1);
				char p2 = hom.key.get(c2);
				hom.key.put(c1,  p2);
				hom.key.put(c2,  p1);
				double newEnergy = hom.energy();
				if (newEnergy < hom.energy()) {
					System.out.println("better: " + hom);
					hits++;
				} else System.out.println(newEnergy);
				hom.key.put(c1,  p1);
				hom.key.put(c2,  p2);
			}
		}
		System.out.println(hits + " out of " + total);

		for (int n=2; n<=5; n++) {
			
		}
		
	}
	

	public static void main(String[] args) {
		//testPartial();
	}

}
