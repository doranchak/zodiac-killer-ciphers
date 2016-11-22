package com.zodiackillerciphers.dictionary;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.zodiackillerciphers.io.FileUtil;

public class Search {
	//public static String DICT = "docs/dictionaries/english.txt";
	public static String DICT = "/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/dictionaries/words-300k.txt";
	/** create word lookup from dictionary file */
	public static Set<String> dictionary() {
		HashSet<String> set = new HashSet<String>();
		List<String> words = FileUtil.loadFrom(DICT);
		for (String word : words) {
			set.add(FileUtil.convert(word.toUpperCase()).toString().toLowerCase());
		}
		return set;
	}
	
	/** return all words found within the given string */
	public static List<String> search(Set<String> dictionary, String text, int minLength, int maxLength) {
		List<String> list = new ArrayList<String>();
		for (int l=maxLength; l>=minLength; l--) {
			for (int i=0; i<text.length()-l+1; i++) {
				String sub = text.substring(i, i+l).toLowerCase();
				if (dictionary.contains(sub)) list.add(sub);
			}
		}
		return list;
	}
	/** playing with the zkdecrypto results for the Fates Unwind Infinity code */
	public static void fatesWordFind() {
		String[] all = new String[] {
				"TGE ENAB TIIOTA OEEELNNLL UEN  SDHL  VHIEEHNEALTDTFAEESNAA NE BEEGS YHIEDXROISTT E IKEGANSNA I O  S NNLETSM L AOVI BDTTN AFDLXNUEYEEDSTIOH E EEGVLS W E S ADIBNEFSENES L  IGEKTERTELUNO HSN V AGTF H OTE  LSDIEA ELELSTTNV AGASAENTD V    BREE OL    IB E EIT ET V DSLN NENHDMA GOVETE  ASE LETHTEHEE EDVTANIKONHETISOES RNUYL L  V NHTEIEAANEDTWS E"
		};
		WordFrequencies.init();
		String big = "";
		for (String s : all) big += s + " ";
		List<String> words = search(dictionary(), big, 3, 15);
		for (String s : words) System.out.println(WordFrequencies.freq(s) + " " + s);
	}
	
	/** return words from both the Word Frequency dictionary, and the 300k dictionary */
	public static Set<String> allWords() {
		Set<String> dictionary = dictionary();
		System.out.println("Start dict size: " + dictionary.size());
		
		WordFrequencies.init();
		System.out.println("Word Freq dict size: " + WordFrequencies.map.size());
		for (String word : WordFrequencies.map.keySet()) {
			dictionary.add(word.toLowerCase());
		}
		System.out.println("Merged dict size: " + dictionary.size());
		return dictionary;
	}
	
	public static void findWordsInFile(String path) {
		WordFrequencies.init();
		Set<String> dictionary = dictionary();
		List<String> lines = FileUtil.loadFrom(path);
		for (String line : lines) {
			List<String> words = search(dictionary, line, 3, 15);
			for (String s : words) System.out.println(s.length() + " " + WordFrequencies.freq(s) + " " + s);
		}
		
	}
	
	public static void main(String[] args) {
		//fatesWordFind();
		//findWordsInFile("/Users/doranchak/projects/zodiac/feh.txt");
		allWords();
	}
}
