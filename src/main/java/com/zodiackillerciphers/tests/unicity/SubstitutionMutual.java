package com.zodiackillerciphers.tests.unicity;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.dictionary.WordFrequencies;
import com.zodiackillerciphers.io.FileUtil;
import com.zodiackillerciphers.io.Unzip;
import com.zodiackillerciphers.suffixtree.LRS;

public class SubstitutionMutual {
	
	/** index a corpus by isomorphisms.  restrict to given length. 
	 * path is the folder in which to find zip files to scan.
	 **/ 
	public static void search(int L) {
		System.out.println("====== starting search ======");
		Random rand = new Random();
		List<String> fileList = FileUtil.loadFrom("/Volumes/Biggie/projects/zodiac/docs/for-cribbing-experiment/gutenberg-wget/all-files.txt");
		Set<String> seen = new HashSet<String>();
		String file = null;
		
		Map<String, Set<String>> map = new HashMap<String, Set<String>>();
		int hits = 0;
		long total = 0;
		while (true) {
			if (total > 50000000) {
				System.out.println("REACHED 50,000,000 TRIALS.  EXITING.");
				return;
			}
			if (total % 100000 == 0) {
				System.out.println("Trials: " + total);
			}
			while (true) {
				file = fileList.get(rand.nextInt(fileList.size()));
				if (seen.contains(file)) continue;
				if (!file.endsWith(".zip")) continue;
				break;
			}
			seen.add(file);
			File f = new File(file);
			Long length = f.length();
			System.out.println("File " + file + " length " + length); 
			if (length > 10000000)
				continue; // too big
			String contents = Unzip.read(f);
//			System.out.println("Done unzipping.");
			String[] tokens = FileUtil.tokenizeAndConvert(contents);
//			System.out.println("Done tokenizing.");
			for (int i=0; i<tokens.length; i++) {
				String lineWithSpaces = "";
				String lineWithoutSpaces = "";
				for (int j=i; j<tokens.length; j++) {
					String token = tokens[j];
//					System.out.println("j " + j + " token [" + token + "] tokenlen " + token.length() + " len " + lineWithoutSpaces.length());
					if (token.length() < 1) continue;
					
					lineWithSpaces += token + " ";
					lineWithoutSpaces += token;
					if (lineWithoutSpaces.length() == L) {
						total++;
//						System.out.println(lineWithSpaces);
						String key = Arrays.toString(Ciphers.toNumeric(lineWithoutSpaces, false));
						Set<String> set = map.get(key);
						if (set == null) set = new HashSet<String>();
						if (add(set, lineWithSpaces)) {
							hits++;
							if (set.size() > 1) {

							    long free =  Runtime.getRuntime().freeMemory();
							    long max =  Runtime.getRuntime().maxMemory();
							    long totalMem =  Runtime.getRuntime().totalMemory();
							    long used = total-free;
								
								System.out.println(hits + "	" + total + "	" + free + "	" + key + "	" + map.get(key)); 
							}
						}
						map.put(key, set);
						continue;
					} else if (lineWithoutSpaces.length() > L) {
						break;
					}
//					System.out.println("Smeg: " + lineWithSpaces);
				}
			}
		}
	}
	
	/** try to find two plaintexts that a single substitution cipher could decode to.
	 *  
	 */
	public static void searchRandomWords() {
		WordFrequencies.init();
		List<String> list1 = new ArrayList<String>(); 
		List<String> list2 = new ArrayList<String>();
		StringBuffer sb1 = new StringBuffer();
		StringBuffer sb2 = new StringBuffer();
		Random rand = new Random();
		Map<Integer, Integer> lengths = new HashMap<Integer, Integer>(); 
		while (true) {
			String word1 = WordFrequencies.randomWord(99);
			String word2 = WordFrequencies.randomWord(word1.length(), 99);
			if (word1.length() < 4) continue;
			if (word1.equals(word2)) continue;
			
			if (list1.contains(word1)) continue;
			if (list2.contains(word2)) continue;

			sb1.append(word1);
			sb2.append(word2);
			String a1 = Arrays.toString(Ciphers.toNumeric(sb1.toString(), false));
			String a2 = Arrays.toString(Ciphers.toNumeric(sb2.toString(), false));
			
			Integer lengthCount = lengths.get(word1.length());
			if (lengthCount == null) lengthCount = 0;
			
			if (a1.equals(a2) && lengthCount != 3) {
				list1.add(word1);
				list2.add(word2);
				lengthCount++;
				lengths.put(word1.length(), lengthCount);
				System.out.println(sb1.length() + "	" + list1 + "	" + list2 + "	"
						+ Ciphers.fromNumeric(Ciphers.toNumeric(sb1.toString(), false), false));
				;
			} else {
				sb1.delete(sb1.length()-word1.length(), sb1.length());
				sb2.delete(sb2.length()-word2.length(), sb2.length());
			}
		}
	}
	
	/** add the given text to the set but only if it is sufficiently dissimilar to all other entries.
	 * returns true only if the add was successful AND the set was non-empty before adding. */
	public static boolean add(Set<String> set, String line) {
		String lineNS = line.replaceAll(" ", "");
		
		float limit = lineNS.length() * 4;
		limit /= 10;
		
		if (LRS.lrs(lineNS).length() >= limit) {
//			System.out.println("Rejected for too many repeats: " + line);
			return false;
		}
		
		if (set.isEmpty()) {
			set.add(line);
			return false;
		}

		int max = lineNS.length()/4;
		
		for (String str : set) {
			int matches = VigenereMutual.matches(str.replaceAll(" ", ""), lineNS);
			if (matches > max) return false;
		}
		set.add(line);
		return true;
	}
	
	public static void find2() {
		WordFrequencies.init();
		String str1 = "";
		String str2 = "";

		while (true) {
			String word1 = WordFrequencies.randomWord();
			if (word1.length() < 3) continue;
			if (WordFrequencies.percentile(word1) < 95) continue;

			String word2 = WordFrequencies.randomWord();
			if (word2.length() != word1.length()) continue;
			if (WordFrequencies.percentile(word2) < 95) continue;

			String new1 = str1 + word1 + " ";
			String new2 = str2 + word2 + " ";
			
			String comp1 = Arrays.toString(Ciphers.toNumeric(new1.replaceAll(" ", ""), false));
			String comp2 = Arrays.toString(Ciphers.toNumeric(new2.replaceAll(" ",  ""), false));
			if (!comp1.equals(comp2)) continue;
			
			str1 = new1;
			str2 = new2;
			System.out.println("===========================================");
			System.out.println(str1.replaceAll(" ",  "").length());
			System.out.println(str1);
			System.out.println(str2);
			System.out.println(comp1);
		}
		
	}
	
	public static void main(String[] args) {
		//find();
//		while (true)
//			search(25);
//		System.out.println(Arrays.toString(Ciphers.toNumeric("BLACKJACKS", false)));
		searchRandomWords();
	}
}
