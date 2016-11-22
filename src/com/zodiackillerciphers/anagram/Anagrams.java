package com.zodiackillerciphers.anagram;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.dictionary.WordFrequencies;
import com.zodiackillerciphers.io.FileUtil;

public class Anagrams {
	
	/** use sorted char arrays for more efficient tabulation of differences between two strings.
	 * returns vector of two counts:
	 *   1) The number of letters in s1 that aren't in s2
	 *   2) The number of letters in s2 that aren't in s1
	 *  */
	public static int[] differences(String s1, String s2) {
		int[] diffs = new int[2];
		
		char[] c1 = s1.toCharArray();
		Arrays.sort(c1);
		char[] c2 = s2.toCharArray();
		Arrays.sort(c2);

		//System.out.println(new String(c1) + "," + new String(c2));
		boolean go = true;
		
		int i1 = 0; int i2 = 0;
		
		while (go) {
			if (i1 == c1.length) {
				diffs[1] += c2.length - i2;
				break;
			}
			if (i2 == c2.length) {
				diffs[0] += c1.length - i1;
				break;
			}
			char ch1 = c1[i1];
			char ch2 = c2[i2];
			if (ch1 == ch2) {
				i1++;
				i2++;
			} else if (ch1 < ch2) {
				diffs[0]++;
				i1++;
			} else if (ch1 > ch2) {
				diffs[1]++;
				i2++;
			}
		}
		return diffs;
		
	}
	
	public static boolean anagram(String word, String str) {
		return anagram(word, str, false);
	}
	/** return true if the given word can be found in the given string.
	 * if exact is true, then only include results of the same length as the input. */
	public static boolean anagram(String word, String str, boolean exact) {
		char[] c1 = word.toCharArray();
		char[] c2 = str.toCharArray();
		Arrays.sort(c1); Arrays.sort(c2);
		//System.out.println(c1);
		//System.out.println(c2);
		int i1 = 0; int i2 = 0;
		int total = 0;
		
		while (i1 < word.length() && i2 < str.length()) {
			while (c1[i1] != c2[i2]) {
				i2++;
				if (i2 > str.length()-1) break;
			}
			if (i2 > str.length()-1) break;
			
			total++; i1++; i2++;
			//System.out.println("total " + total);
		}
		boolean b = !exact || word.length() == str.length();
		if (total == word.length() && b) return true;
		return false;
	}
	
	/** return the "leftover" letters when the first text is removed from second text */
	public static String leftover(String first, String second) {
		if (!anagram(first, second)) throw new IllegalArgumentException("First string is not found in second.");
		
		List<Character> all = new ArrayList<Character>();
		for (char ch : second.toCharArray()) all.add(ch);
		
		for (char ch : first.toCharArray()) {
			all.remove((Character) ch);
		}
		StringBuffer sb = new StringBuffer();
		for (Character ch : all) sb.append(ch);
		return sb.toString();
	}
	
	/** construct an anagram map using the given list of words */
	public static Map<String, List<String>> anagramMap(Set<String> words) {
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		for (String word : words) {
			char[] charray = word.toCharArray();
			java.util.Arrays.sort(charray);
			String key = new String(charray);
			List<String> val = map.get(key);
			if (val == null) val = new ArrayList<String>();
			val.add(word);
			map.put(key, val);
		}
		return map;
	}

	/** look in the given file, which contains a list of words, for all words that fit in the given message */
	public static void findWords(String file, String message, int minLengthToPrint) {
		WordFrequencies.init();
		List<String> words = FileUtil.loadFrom(file);
		
		for (String word : words) {
			if (word.length() < minLengthToPrint) continue;
			if (anagram(FileUtil.convert(word).toString(), message)) {
				System.out.println(word + ", " + word.length() + ", " + WordFrequencies.freq(word));
			}
		}
	}
	
	/** look in the given file for all strings that fit in the given message */
	public static void findInFile(String file, String message, int minLengthToPrint) {
		String sb = FileUtil.convert(FileUtil.loadFrom(file));

		String sub = null;
		for (int i=0; i<sb.length()-1; i++) {
			int j=1;
			sub = sb.substring(i,i+1);
			boolean found = anagram(sub, message);
			if (!found) continue;
			while (found && (i+j) < sb.length()-1) {
				sub = sb.substring(i,i+j);
				found = anagram(sub, message);
				j++;
			}
			sub = sub.substring(0,sub.length()-1);
			if (sub.length() >= minLengthToPrint) System.out.println((j-1) + ", " + i + ", " + file + ", " + sub);
		}
	}
	
	/** look for dictionary words that fit in the given string */
	public static List<String> anagramsFor(String str, int minLength) {
		List<String> list = new ArrayList<String>();
		String upper = str.toUpperCase();
		WordFrequencies.init();
		for (String word : WordFrequencies.map.keySet()) {
			if (word.equals(str)) continue;
			if (word.length() < minLength) continue;
			if (anagram(word.toUpperCase(), str)) {
				//System.out.println(word.length() + ", " + WordFrequencies.freq(word) + ", " + word);
				list.add(word);
			}
		}
		return list;
	}

	
	public static void dump(Map<String, List<String>> anagramMap) {
		for (String key : anagramMap.keySet()) {
			String line = key + ", " + toString(anagramMap.get(key));
			System.out.println(line);
		}
	}
	
	public static String toString(List<String> list) {
		if (list == null) return "";
		String line = "";
		for (String word : list) line += word + " ";
		return line;
	}

	public static void lathersSearch() {
		String msg = "TLSDCLYOVCEIZAWNHLLDHOSIISVAEEVSENEARKOLIMFDRPOUTSIEITSBDARNSRO";
		String prefix ="/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/gutenberg/";
		String[] names = new String[] {
				"pg10462.txt",
				"pg10799.txt",
				"pg11364.txt",
				"pg11889.txt",
				"pg12180.txt",
				"pg1322.txt",
				"pg1342.txt",
				"pg135.txt",
				"pg1661.txt",
				"pg2591.txt",
				"pg30601.txt",
				"pg4300.txt",
				"pg5200.txt",
				"pg76.txt",
				"pg9296.txt",
				"pg9798.txt",
				"pg9881.txt",
				"tale.txt"
		};
		for (String name : names)
			findInFile(prefix + name, msg, 30);
	}
	
	/** find words that can be found anagrammed in ALL the given strings.
	 * see http://zodiackillersite.forummotion.com/t1813-kane-s-name-an-anagram-at-all-murder-locations
	 **/
	public static void commonAnagramsIn(List<String> list) {
		WordFrequencies.init();
		for (String word : WordFrequencies.map.keySet()) {
			if (word.length() < 4) continue;
			
			boolean found = true;
			for (String str : list) {
				if (!anagram(word, str.toUpperCase())) {
					found = false;
					break;
				}
			}
			if (found) {
				System.out.println(word.length() + ", " + word + ", " + WordFrequencies.freq(word));
			}
		}
	}
	
	/** re: Chris Williams' email (tigerland555@gmail.com).  Look for anagrams in the list of symbols by frequency. */
	// results:
	//19,0,BFOIRMKWLCUNTSJZYDH,CUSHY,5,25
	//6,0,BFOIRM,FORM,4,25074
	//6,0,BFOIRM,FIRM,4,6340
	//7,0,BFOIRMK,FORK,4,642
	//14,0,BFOIRMKWLCUNTS,CUTS,4,2242
	//14,0,BFOIRMKWLCUNTS,NUTS,4,836
	//21,0,BFOIRMKWLCUNTSJZYDHPE,HYDE,4,385
	//21,0,BFOIRMKWLCUNTSJZYDHPE,HYPE,4,283
	//21,0,BFOIRMKWLCUNTSJZYDHPE,SHEP,4,37
	//22,0,BFOIRMKWLCUNTSJZYDHPEA,SHEA,4,23
	
	// results (reversed list):
	//14,0,QXAEPHDYZJSTNU,SHUT,4,1281
	//14,0,QXAEPHDYZJSTNU,HUNT,4,1000
	//11,0,QXAEPHDYZJS,APES,4,245
	//15,0,QXAEPHDYZJSTNUC,DUCT,4,198
	//14,0,QXAEPHDYZJSTNU,SHUNT,5,92
	//14,0,QXAEPHDYZJSTNU,SHUN,4,52
	//14,0,QXAEPHDYZJSTNU,STUN,4,43
	//15,0,QXAEPHDYZJSTNUC,SYNC,4,23
	//7,0,QXAEPHD,APED,4,14
	
	public static void chrisWilliamsTest() {
		WordFrequencies.init();
		//String plain = "BFOIRMKWLCUNTSJZYDHPEAXQ"; // most to least frequent
		String plain = "QXAEPHDYZJSTNUCLWKMRIOFB"; // least to most frequent
		//int[] freq = new int[] {12, 10, 10, 10, 8, 7, 7, 6, 6, 5, 5, 5, 5, 4, 4, 4, 4, 4, 4, 3, 3, 2, 2, 2};  // most to least frequent
		int[] freq = new int[] {2, 2, 2, 3, 3, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 6, 6, 7, 7, 8, 10, 10, 10, 12}; // least to most frequent
		//B   F   O   I   R  M  K  W  L  C  U  N  T  S  J  Z  Y  D  H  P  E  A  X  Q
		//12  10  10  10  8  7  7  6  6  5  5  5  5  4  4  4  4  4  4  3  3  2  2  2
		
		//Q  X  A  E  P  H  D  Y  Z  J  S  T  N  U  C  L  W  K  M  R  I   O   F   B
		//2  2  2  3  3  4  4  4  4  4  4  5  5  5  5  6  6  7  7  8  10  10  10  12

		Map<Character, Integer> counts = new HashMap<Character, Integer>();
		Map<Integer, Integer> adjacencies = new HashMap<Integer, Integer>();
		for (int i=0; i<freq.length-1; i++) {
			if (freq[i]==freq[i+1]) continue;
			adjacencies.put(freq[i], freq[i+1]);
		}
		Set<String> found = new HashSet<String>();
		for (Integer key : adjacencies.keySet()) System.out.println(key+","+adjacencies.get(key));
		for (int i=0; i<plain.length(); i++) {
			counts.put(plain.charAt(i), freq[i]);
		}
		
		for (int i=0; i<plain.length(); i++) {
			int maxLength = plain.length()-i;
			for (int L=4; L<=maxLength; L++) {
				String sub = plain.substring(i,i+L);
				for (String word : WordFrequencies.map.keySet()) {
					if (found.contains(word)) continue; // already kept this word
					if (word.length() > L || word.length() < 4) continue;
					if (!anagram(word, sub)) continue;

					// need to make sure anagrammed word does not violate order of frequencies.
					// consider a letter of the word X and the letter to its right Y
					// 1) the frequency of X must be equal to the frequency of Y, or
					// 2) the frequency of X must be adjacent to the frequency of Y in the frequency list
					
					boolean fit = true;
					for (int j=0; j<word.length()-1; j++) {
						char x = word.charAt(j);
						char y = word.charAt(j+1);
						int valX = counts.get(x);
						int valY = counts.get(y);
						if (valX == valY) continue;
						if (adjacencies.get(valX) != null && adjacencies.get(valX)==valY) continue;
						fit = false;
						break;
					}
					if (fit) {
						System.out.println(L+","+i+","+sub+","+word+","+word.length()+","+WordFrequencies.freq(word));
						found.add(word);
					}
					
				}
			}
		}
	}
	
	static void testMorf() {
		String test = "HERCPCAAPYIOLTGODUTREOHEIDTMPBTEEO";
		List<String> list = anagramsFor(test, 4);
		for (String word : list) System.out.println(word.length() + ", " + WordFrequencies.freq(word) + " " + word);
		
	}
	
	public static void main(String[] args) {
		//System.out.println(anagram("george bush", "he bugs gore"));
		//lathersSearch();
		//System.out.println(leftover("TEMPER","EBEORIETEMETHHPITI"));

		/*
		List<String> list = new ArrayList<String>();
		list.add("lakehermanroadbenicia");
		list.add("bluerockspringsparkvallejo");
		list.add("lakeberryessanapa");
		list.add("washingtonandmaplestreetssanfrancisco");
		commonAnagramsIn(list);
		*/
		//chrisWilliamsTest();
		//testMorf();
		for (String word : anagramsFor("HERLGUATAEHLRINL",4)) {
			System.out.println(word.length() + ", " + WordFrequencies.percentile(word) + ", " + word);
		}
		//System.out.println(anagramsFor("UNITE", 5));
		
		//findWords("/Users/doranchak/projects/zodiac/words-from-wikipedia.txt", "TNNSGPADOSHITPICLTCE", 4);
		
		
		//int[] diffs = differences("DTOSCHISATCLIPGPENN","TNNSGPADOSHITPICLTCE");
		//System.out.println(diffs[0]+","+diffs[1]);
		
	}
}
