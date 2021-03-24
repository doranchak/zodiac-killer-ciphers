package com.zodiackillerciphers.names;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.dictionary.WordFrequencies;
import com.zodiackillerciphers.io.FileUtil;

/** scan the gigantic facebook names file and see which names fit Z13 */
public class FacebookNames {
	
	public static int MAX_TOKENS_SEEN = 0;
	
	/** track total number of 13-character plaintexts examined */
	public static long num_13_plain = 0;
		
	public static boolean test(String line, boolean facebook) {
		String sub;
		if (facebook) sub = line.substring(8);
		else sub = line;
		
		List<String> newList = new ArrayList<String>();
		String[] tokens = sub.split(" ");
		if (tokens.length > MAX_TOKENS_SEEN) {
			System.out.println("New max tokens " + tokens.length +" for " + line);
			MAX_TOKENS_SEEN = tokens.length;
		}
		for (String token: tokens) {
			newList.add(token);
		}
		return test(line, tokens, newList, 0, new StringBuffer(), new StringBuffer());
	}
	
	/** test all combinations of tokens */
	public static boolean test(String line, String[] tokens, List<String> tokensList, int pos, StringBuffer sb, StringBuffer sbWithSpaces) {
		if (tokens.length > 5) return false; // too many tokens
		if (sb.length() > 13) return false; // too many letters in the plaintext

		/*
		if (sb.length() == 8) { 
			// A E N z t N A M
			// 0 1 2 3 4 5 6 7
			if (sb.charAt(0) == sb.charAt(6) &&
				sb.charAt(2) == sb.charAt(5)) {
				System.out.println("Hit an 8-letter name: " + sbWithSpaces + " for " + line);
			}
		}*/
		
		if (sb.length() == 13) { // plaintext has reached exactly 13 letters
			num_13_plain++;
			// A E N z 8 K 8 M 8 t N  A  M
			// 0 1 2 3 4 5 6 7 8 9 10 11 12
			if (sb.charAt(0) != sb.charAt(11)) return false;
			if (sb.charAt(2) != sb.charAt(10)) return false;
			if (sb.charAt(4) != sb.charAt(6)) return false;
			if (sb.charAt(6) != sb.charAt(8)) return false;
			if (sb.charAt(7) != sb.charAt(12)) return false;
			
			// hit!
			System.out.println("Matched " + sbWithSpaces + " for " + line);
			return true;
		}
		
		
		
		if (pos >= tokens.length) return false; // no more tokens

		/* there are more tokens, and the putative plaintext is still less than 13 characters, so recurse */ 
		boolean matched = false;
		for (int i=0; i<tokensList.size(); i++) {

			String name = tokensList.get(i);
			if (name == null || name.length() == 0) {
				System.out.println("Empty token for: " + line);
				continue;
			}
			
			List<String> newList = new ArrayList<String>();
			for (String token: tokensList) {
				if (token.equals(name)) continue;
				newList.add(token);
			}
			
			String initial = ""+name.charAt(0);
			// test with full name part
			matched |= test(line, tokens, newList, pos+1, new StringBuffer(sb).append(name), new StringBuffer(sbWithSpaces).append(name).append(" "));
			// test with initial
			matched |= test(line, tokens, newList, pos+1, new StringBuffer(sb).append(initial), new StringBuffer(sbWithSpaces).append(initial).append(" "));
			// test with blank (zero-length string)
			matched |= test(line, tokens, newList, pos+1, new StringBuffer(sb), new StringBuffer(sbWithSpaces));
			
		}

		return matched;
		
	}
	
	public static void search(String path) {
		BufferedReader input = null;
		int counter = 0; int hits = 0; 
		try {
			input = new BufferedReader(new FileReader(new File(path)));
			String line = null; // not declared within while loop
			while ((line = input.readLine()) != null) {
				counter++;
				if (test(line, true)) {
					hits++;
					System.out.println("Hits: " + hits + " out of " + num_13_plain + " 13-character plaintexts from " + counter + " inputs.");
				}
				if (counter % 100000 == 0) 
					System.out.println("read " + counter + " lines.");
			}
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		try {
			input.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		System.out.println("Totals: Hits: " + hits + ", Plaintexts: " + num_13_plain + ", Lines: " + counter);
	}
	public static void searchLegaledge(String path) {
		BufferedReader input = null;
		int counter = 0; int hits = 0; 
		try {
			input = new BufferedReader(new FileReader(new File(path)));
			String line = null; // not declared within while loop
			while ((line = input.readLine()) != null) {
				counter++;
				if (test(line, false)) {
					hits++;
					System.out.println("Hits: " + hits + " out of " + counter);
				}
				if (counter % 100000 == 0) 
					System.out.println("read " + counter + " lines.");
			}
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		try {
			input.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		System.out.println("Totals: Hits: " + hits + ", Lines: " + counter);
	}
	
	public static void showFreq(String path) {
		WordFrequencies.init();
		List<String> list = FileUtil.loadFrom(path);
		for (String line : list) {
			String[] split = line.split(" ");
			float freq = 1.0f;
			for (String s : split) {
				if (s.length() < 4) continue;
				freq *= (1+WordFrequencies.percentile(s)) * s.length();
			}
			System.out.println(split.length + ", " + freq + ", " + line);
		}
	}
	
	/** find names that are exactly 19 letters long */
	public static void show19LetterNames(String path) {
		BufferedReader input = null;
		int counter = 0; int hits = 0; 
		try {
			input = new BufferedReader(new FileReader(new File(path)));
			String line = null; // not declared within while loop
			while ((line = input.readLine()) != null) {
				counter++;
				String[] split = line.substring(8).split(" ");

				String name = "";
				for (String s : split) {
					name += s;
					if (name.length() > 19) break;
					if (name.length() == 19) {
						System.out.println("Match: " + name + ", " + line);
					}
				}
				/** try middle initial */
				if (split.length == 3) {
					if (split[1].length() == 0) continue;
					name = split[0] + split[1].charAt(0) + split[2];
					if (name.length() == 19) {
						System.out.println("Match: " + name + ", " + line);
					}
				}
				
			}
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		try {
			input.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}
	
	/** http://zodiackillersite.com/viewtopic.php?f=63&t=2886 */
	public static void findNamesSam(String path) {
		Map<Character, Integer> count1 = Ciphers.countMap("arthurleighallen");
		Map<Character, Integer> count2;
		BufferedReader input = null;
		try {
			input = new BufferedReader(new FileReader(new File(path)));
			String line = null; // not declared within while loop
			while ((line = input.readLine()) != null) {
				String[] split = line.split(" ");

				String name = "";
				String newLine = "";
				for (String s : split) {
					name += s;
					newLine += s + " ";
					if (name.length() > 16) break;
					if (name.length() == 16) {
						count2 = Ciphers.countMap(name);
						if (Ciphers.sameDistribution(count1, count2))
								dump(name, newLine);
					}
				}
				/** try middle initial */
				if (split.length == 3) {
					if (split[1].length() == 0) continue;
					name = split[0] + split[1].charAt(0) + split[2];
					if (name.length() == 16) {
						count2 = Ciphers.countMap(name);
						if (Ciphers.sameDistribution(count1, count2))
							dump(name, newLine);
					}
				}
				
			}
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		try {
			input.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}
	
	public static void dump(String match, String line) {
		match = match.toUpperCase();
		String cipher = "FIRE GUN KNIFE ROPE";
		Map<Character, Integer> counts = Ciphers.countMap(match);
		Map<Character, Character> decoder = new HashMap<Character, Character>();
		
		String[] symbols = new String[] {
				"GKOPU", // freq 1
				"FINR", // freq 2
				"E" // freq 1
		};
		
		for (int i=0; i<match.length(); i++) {
			char p = match.charAt(i);
			int freq = counts.get(p);
			String s = symbols[freq-1];
			if (s.length() == 0) continue;
			//System.out.println(match + " " + p + " " + freq + " " + s);
			char c = s.charAt(0);
			symbols[freq-1] = symbols[freq-1].substring(1);
			decoder.put(c, p);
		}
		
		System.out.println(decoder);
		String encoded = "";
		for (int i=0; i<cipher.length(); i++) {
			char c= cipher.charAt(i);
			if (c == ' ') encoded +=  ' ';
			else encoded += decoder.get(c);
		}
		System.out.println(cipher);
		System.out.println(encoded);
		System.out.println(line.toUpperCase());
		System.out.println();
		
		if (encoded.replaceAll(" ", "").length() != 16) {
			System.exit(-1);
		}
		
	}
	
	public static void main(String[] args) {
		//search("/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/facebook-names/facebook-names-withcount.txt");
		findNamesSam("/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/facebook-names/facebook-names-unique.txt");
		//showFreq("/Users/doranchak/projects/zodiac/feh");
		//searchLegaledge("/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/legaledge-names/names.txt");
		//show19LetterNames("/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/facebook-names/facebook-names-withcount.txt");
	}
}
