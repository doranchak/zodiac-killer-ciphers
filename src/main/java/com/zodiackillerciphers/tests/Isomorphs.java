package com.zodiackillerciphers.tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.ciphers.FatesUnwind;
import com.zodiackillerciphers.io.FileUtil;
import com.zodiackillerciphers.ngrams.NGramsBean;

/** sources:  William F. Friedman’s Military Cryptanalysis (Part III) (section III paragraph 12 and onwards) 
 * and Moshe Rubin's comment here: http://www.ciphermysteries.com/2012/12/20/small-explosion-in-unicode-factory-nobody-hurt
 *
 */
public class Isomorphs {
	
	public static void search(String text, int maxLength) {
		Map<String, List<IsomorphBean>> map = new HashMap<String, List<IsomorphBean>>();
		for (int i=0; i<text.length()-1; i++) {
			//System.out.println(i+"...");
			for (int j=i+1; j<text.length(); j++) {
				if ((j-i)>maxLength) break;
				String sub = text.substring(i,j);
				if (!candidate(sub)) {
					//System.out.println("not a candidate: " + sub);
					continue;
				}
				
				String key = keyFrom(sub);
				List<IsomorphBean> val = map.get(key);
				if (val == null) val = new ArrayList<IsomorphBean>();
				val.add(new IsomorphBean(sub, i));
				map.put(key, val);
				
			}
		}
		for (String key : map.keySet()) {
			List<IsomorphBean> val = map.get(key);
			if (val.size() < 2) continue;
			String line = key.split(",").length + ", " + val.size() + ", " + strength(val.get(0).string) + ", [" + key + "]," + relations(val) + ",";
			for (IsomorphBean bean : val) line += " [" + bean.string + " " + bean.position + " " + repeats(bean.string) + "]";
			System.out.println(line);
		}
	}
	
	/** return list of symbols that repeat */
	public static String repeats(String text) {
		String line = "";
		NGramsBean bean = new NGramsBean(1, text);
		for (String rep : bean.repeats) line += rep;
		return line;
	}
	
	/** derive related symbol sets from the given set of isomorphisms */
	public static String relations(List<IsomorphBean> beans) {
		String line = "";
		
		List<Set<Character>> list = new ArrayList<Set<Character>>(); 
		
		StringBuffer[] sb = new StringBuffer[beans.size()];
		for (int i=0; i<beans.size(); i++) sb[i] = new StringBuffer();
		
		for (int i=0; i<beans.size(); i++) {
			IsomorphBean bean = beans.get(i);
			for (int j=0; j<bean.string.length(); j++) {
				char ch = bean.string.charAt(j);
				sb[i].append(ch);
			}
		}
		
		for (int i=0; i<sb[0].length(); i++) { // for each column
			Set<Character> set = new HashSet<Character>();
			for (int j=0; j<sb.length; j++) { // for each row
				set.add(sb[j].charAt(i));
			}
			list.add(set);
		}
		
		boolean found = true;
		while (found) {
			found = false;
			for (int i=0; i<list.size()-1; i++) {
				for (int j=i+1; j<list.size(); j++) {
					for (Character ch : list.get(i)) {
						if (list.get(j).contains(ch)) {
							list.get(i).addAll(list.get(j));
							list.remove(j);
							found = true;
							break;
						}
					}
					if (found) break;
				} if (found) break;
			}
		}
		
		for (Set<Character> set : list) {
			line += " (";
			List<Character> sorted = new ArrayList<Character>(set);
			Collections.sort(sorted);
			for (Character ch : sorted) line += ch;
			line += ")";
		}
		return line;
	}
	
	
	
	public static int strength(String key) {
		NGramsBean bean = new NGramsBean(1, key);
		int s = 0;
		for (String alpha : bean.repeats) s += bean.counts.get(alpha);
		return s;
	}
	
	/** it's a candidate if the first and last characters both repeat in the text */
	static boolean candidate(String text) {
		if (text == null || text.length() < 2) return false;
		Set<Character> set = new HashSet<Character>();
		for (int i=1; i<text.length()-1; i++) set.add(text.charAt(i));
		
		char ch1 = text.charAt(0);
		char ch2 = text.charAt(text.length()-1);
		
		return (set.contains(ch1) && set.contains(ch2)) || ch1==ch2;
	}
	
	
	/** generate a key that represents the sequence of assignments used in the given string */
	public static String keyFrom(String str) {
		StringBuffer result = new StringBuffer();
		Map<Character, Integer> map = new HashMap<Character, Integer>();
		int current = 0;
		
		for (int i=0; i<str.length(); i++) {
			char ch = str.charAt(i);
			Integer val = map.get(ch);
			if (val == null) {
				val = current;
				current++;
				map.put(ch, val);
			}
			result.append(val);
			if (i<str.length()-1) result.append(",");
		}
		return result.toString();
		
	}
	
	public static void testFates() {
		/*
		List<IsomorphBean> results = search(FatesUnwind.cipherFull);
		for (IsomorphBean bean : results) {
			System.out.println("==================");
			bean.dump();
		}*/
		//search(FatesUnwind.cipherFullDallison, 25);
		search(Ciphers.cipher[0].cipher, 50);
	}
	
	public static void testFatesRandom() {
		String[] pool = LetterFrequencies.letterPool(LetterFrequencies.frequenciesFates);
		for (int i=0; i<10000; i++) {
			String word = LetterFrequencies.randomWord(1501, pool);
			System.out.println("random trial #" + i + ", " + word);
			search(word, 25);
		}
	}
	
	public static void testFatesRandomResults() {
		List<String> lines = FileUtil.loadFrom("/Users/doranchak/projects/zodiac/iso");
		
		int trial = 0; String trialString = null;
		for (String line : lines) {
			if (line.startsWith("random trial")) {
				trial = Integer.valueOf(line.split(",")[0].split("#")[1]);
				trialString = line;
			}
			else {
				String[] split = line.split(",");
				int L = Integer.valueOf(split[0]);
				int strength = Integer.valueOf(split[2].replaceAll(" ", ""));
				if (L>=16 && strength >= 4) {
					System.out.println(trialString);
					System.out.println("random trial #" + trial + ", " + line);
				}
			}
		}
		
	}
	
	public static void main(String[] args) {
		testFates();
		//testFatesRandomResults();
		//System.out.println(keyFrom("¿¿-*?-"));
	}
}
