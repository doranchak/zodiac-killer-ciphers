package com.zodiackillerciphers.ciphers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.zodiackillerciphers.anagram.Anagrams;
import com.zodiackillerciphers.ciphers.algorithms.Vigenere;
import com.zodiackillerciphers.corpus.CorpusBase;
import com.zodiackillerciphers.dictionary.WordFrequencies;
import com.zodiackillerciphers.lucene.NGramsCSRA;
import com.zodiackillerciphers.lucene.Stats;
import com.zodiackillerciphers.lucene.ZKDecrypto;
import com.zodiackillerciphers.tests.unicity.SubstitutionMutualEvolve;

public class GregoryMcMichaelCipher extends CorpusBase {
	public static String cipherAlphaOnly = "WEOZQXZEDWDCEGDODRMQIJWPWSOYXRZLQJRFPERSJVEULDROXLMVJRFLRTLITITQVLYVHOGIBLAHJYRNZJXPOLPGYMFFDEDWLGLESURELMACRCACTNOLWCAIRTAOTHSRNUKAFOVIITFLPXXAKLRZFMGJSFULPQWHIZVASTMERGUITAVOYISR";
	public static String[] cipherCleanSections = new String[] {// unabmgiuous sections of cipher text
			"JWPW SOYX RZL".toLowerCase(),
			"QJRFPER".toLowerCase(),
			"JVEUL DROXL M VJR FL RT LITIT QVLYV".toLowerCase(),
			"GIBLAHJY RN ZJX POLPGYMFF".toLowerCase(),
			"DEDWL GLE SU REL MACRCACT NOLWC".toLowerCase(),
			"WHIZVAST".toLowerCase()
	};
	static String currentCipher;
	public static String[] cipherWords = new String[] { "AFO", "AKL", "DEDWL", "DROXL", "FMG", "GDOD", "GIBLAHJY",
			"GLE", "JSFULP", "JVEUL", "JWPW", "LITIT", "MACRCACT", "MERGUI", "MQI", "NOLWC", "POLPGYMFF", "QJRFPER",
			"QVLYV", "QXZED", "REL", "RZL", "SOYX", "SRNU", "TAOTH", "TAVOY", "VIITFLPXX", "VJR", "WDCE", "WEOZ",
			"WHIZVAST", "ZJX" };
	public static TreeSet<GregoryMcMichaelCipherResultBean> treeSet;
	public static void init(String lang, boolean words) {
		//NGramsCSRA.OPTIONAL_PREFIX = "C:/Users/oranchakd2/CRYSS/cryss-client/";
		NGramsCSRA.OPTIONAL_PREFIX = "/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/tools/zkdecrypto/zkdecrypto/language/";
//		WordFrequencies.OPTIONAL_PREFIX = NGramsCSRA.OPTIONAL_PREFIX;
		NGramsCSRA.init(lang);
		if (words) WordFrequencies.init();
		treeSet = new TreeSet<GregoryMcMichaelCipherResultBean>();
	}
	/** try out all possible vigenere keys of length L.  maintain top N list of best scoring plaintexts. */
	public static void bruteForceVigenere(int L, int N) {
		int[] key = new int[L];
		bruteForceVigenere(0, key, N);
		System.out.println("===== RESULTS KEY LEN " + L + " FOR CIPHER " + currentCipher + ":");
		for (GregoryMcMichaelCipherResultBean bean : treeSet) System.out.println(bean);
	}
	public static void bruteForceVigenere(int index, int[] key, int N) {
		if (index == key.length) {
			bruteForceVigenereProcess(key, N);
			return;
		}
		for (int i=1; i<26; i++) {
			key[index] = i;
			bruteForceVigenere(index+1, key, N);
		}
	}
	public static void bruteForceVigenereProcess(int[] key, int N) {
		GregoryMcMichaelCipherResultBean bean = new GregoryMcMichaelCipherResultBean();
		bean.key = key.clone();
		bean.plaintext = Vigenere.decrypt(currentCipher, key);
		bean.zkscore = ZKDecrypto.calcscore(bean.plaintext.toUpperCase(), "EN", true);
		put(treeSet, N, bean);
	}
	
	/*
	 * for the given heap, with given capacity, add the given entry only if there is
	 * room or it is better than the worst entry.
	 */
	public static boolean put(TreeSet<GregoryMcMichaelCipherResultBean> treeSet, int capacity, GregoryMcMichaelCipherResultBean bean) {
		boolean result = false;
		if (bean == null) return result;
		if (treeSet == null) return result;
		if (treeSet.contains(bean)) return result; // ignore dupes
		if (treeSet.size() < capacity) {
			treeSet.add(bean);
			System.out.println("Added " + bean);
			result = true;
		} else {
			GregoryMcMichaelCipherResultBean worst = treeSet.last();
			// if current better than worst, remove worst to add current
			if (worst.zkscore < bean.zkscore) {
				treeSet.remove(worst);
//				System.out.println(result + " Removed " + worst);
				treeSet.add(bean);
				System.out.println("Added " + bean);
				result = true;
				if (treeSet.size() > capacity) {
					System.out.println("wtf.  tree got too big.");
					System.out.println("last: " + treeSet.last());
					for (GregoryMcMichaelCipherResultBean b : treeSet)
						System.out.println("      " + b);
					System.exit(-1);
				}
			} else {
				//System.out.println("Ignoring " + bean);
			}
		}
//		System.out.println("==== current tree");
//		for (GregoryMcMichaelCipherResultBean b : treeSet)
//			System.out.println("      " + b);
//		System.out.println("====");
		return result;
	}
	
	/** try out all word ngrams from n=1, stop when n creates sequence of ngrams longer than the cipher text being tested. 
	 * consider all offsets of the key sequence.
	 * */
	public static void corpusSearch() {
		init("EN", false);
		SubstitutionMutualEvolve.initSources();
		System.out.println("Done source init.");
		Map<String, TreeSet<GregoryMcMichaelCipherResultBean>> map = new HashMap<String, TreeSet<GregoryMcMichaelCipherResultBean>>();
		CorpusBase.SHOW_INFO = false;
		long samples = 0;
		long sources = 0;
		int minLength = 6;
		int capacity = 10;
		boolean go = true;
		while (go) {
			sources++;
			go = !SubstitutionMutualEvolve.randomSource();
			System.out.println("FILE: " + file);
//			System.out.println(Arrays.toString(tokens));
			for (int ci=0; ci<cipherCleanSections.length; ci++) {
				String cipher = cipherCleanSections[ci].toLowerCase();
				int len = cipher.length();
				int maxKeyLen = len*2;
				TreeSet<GregoryMcMichaelCipherResultBean> treeSet = map.get(cipher);
				if (treeSet == null) {
					treeSet = new TreeSet<GregoryMcMichaelCipherResultBean>();
					map.put(cipher, treeSet);
				}
				StringBuffer key = new StringBuffer();
				StringBuffer keySpaces = new StringBuffer();
				for (int i=0; i<tokens.length; i++) {
					key = new StringBuffer();
					keySpaces = new StringBuffer();
					for (int j=i; j<tokens.length; j++) {
						key.append(tokens[j]);
						if (keySpaces.length() > 0) keySpaces.append(" ");
						if (key.length() < 7) continue;
						keySpaces.append(tokens[j]);
						for (int offset=0; offset<Math.min(len, key.length()); offset++) {
							GregoryMcMichaelCipherResultBean bean = new GregoryMcMichaelCipherResultBean();
							bean.plaintext = Vigenere.encrypt(cipher, key, offset);
							bean.zkscore = ZKDecrypto.calcscore(bean.plaintext.toUpperCase(), "EN", true);
							bean.offset = offset;
							bean.decrypted = false;
							bean.tokens = keySpaces;
							bean.cipher = ci;
							put(treeSet, capacity, bean);
							bean = new GregoryMcMichaelCipherResultBean();
							bean.plaintext = Vigenere.decrypt(cipher, key, offset);
							bean.zkscore = ZKDecrypto.calcscore(bean.plaintext.toUpperCase(), "EN", true);
							bean.offset = offset;
							bean.decrypted = true;
							bean.tokens = keySpaces;
							bean.cipher = ci;
							put(treeSet, capacity, bean);
						}
						if (key.length() >= maxKeyLen) break;
					}
				}
			}
		}
		System.out.println("Sources: " + sources);
		System.out.println("Samples: " + samples);
	}
	static void bruteForceVigenere() {
		init("EN", false);
		for (int L=1; L<8; L++) {
			for (int which : new int[] {0,2,3,4}) {
				treeSet.clear();
				currentCipher = cipherCleanSections[which];
				bruteForceVigenere(L, 100);
			}
		}
	}
	
	// go through all words in a dictionary.  use as a vigenere key (in both directions) against each word
	// of the ciphertext, to see if any real words pop out.
	public static void wordSearch() {
		WordFrequencies.init();
		String tab = "	";
		for (String wordKey : WordFrequencies.map.keySet()) {
			for (String wordCipher : cipherWords) {
				String wordShifted = null;
				wordShifted = Vigenere.encrypt(wordCipher, wordKey);
				if (WordFrequencies.hasWord(wordShifted)) {
					System.out.println(wordShifted.length() + tab + WordFrequencies.freq(wordShifted) + tab
							+ wordShifted + tab + WordFrequencies.freq(wordKey) + tab + wordKey + tab + "encrypted" + tab + wordCipher);
				}
				wordShifted = Vigenere.decrypt(wordCipher, wordKey);
				if (WordFrequencies.hasWord(wordShifted)) {
					System.out.println(wordShifted.length() + tab + WordFrequencies.freq(wordShifted) + tab
							+ wordShifted + tab + WordFrequencies.freq(wordKey) + tab + wordKey + tab + "decrypted" + tab + wordCipher);
				}
			}
		}
	}
	public static void main(String[] args) {
//		System.out.print(Stats.iocAsFloat(cipherAlphaOnly));
//		System.out.print(Stats.iocAsFloat(Ciphers.Z408_SOLUTION));

		//init("EN", false);
//		String test = "THIS IS THE ZODIAC SPEAKING";
//		System.out.println(ZKDecrypto.calcscore(test, "EN", true));
//		System.out.println(ZKDecrypto.calcscore(test.toLowerCase(), "EN", true));
//		bruteForceVigenere();
//		corpusSearch();
		wordSearch();
//		System.out.println(Vigenere.encrypt(cipherCleanSections[3].toLowerCase(), "HOWEVERTHEYWILLPROBABLY", 7));
//		System.out.println(Vigenere.decrypt("zpfjwpuj ge nkx qzjwuuqaj", "HOWEVERTHEYWILLPROBABLY", 7));
//		System.out.println(Vigenere.encrypt(cipherCleanSections[4].toLowerCase(), "PROJECT".toLowerCase(), 0));
//		System.out.println(Vigenere.encrypt(cipherCleanSections[4].toLowerCase().replaceAll(" ", ""), "PROJECT".toLowerCase(), 0));
	}
	
}
