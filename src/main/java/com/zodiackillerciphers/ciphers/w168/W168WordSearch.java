package com.zodiackillerciphers.ciphers.w168;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.zodiackillerciphers.dictionary.WordFrequencies;
import com.zodiackillerciphers.lucene.NGramsCSRA;

/** Jarl's finding: W168 has a strong tendency towards period -1.
 * This suggests plaintext is written out in reversed chunks, probably crossing word boundaries.
 * Brute force find all k-word combinations that can be found using n tuples, where a tuple defines
 * the starting position and length of a reversed chunk.  The set of tuples defines non-overlapping chunks.   
 */
public class W168WordSearch {
	
	static String W168_1D;
	static {
		NGramsCSRA.OPTIONAL_PREFIX = "/Users/doranchak/projects/zodiac/zkdecrypto/zkdecrypto/language/";
		NGramsCSRA.init("EN");
		
		WordFrequencies.init();
		
		// make a 1D reversed version of W168
		W168_1D = "";
		for (String s : W168.W168)
			W168_1D += s;
		W168_1D = new StringBuilder(W168_1D).reverse().toString();
		System.out.println(W168_1D);
	}
	
	/** extract chunks represented by the given non-overlapping tuples */
	public static StringBuilder extract(String cipher, int[][] tuples) {
		StringBuilder sb = new StringBuilder();
		for (int[] tuple : tuples) {
			int pos = tuple[0];
			int len = tuple[1];
			sb.append(cipher.substring(pos, pos+len));
		}
		return sb;
	}
	
	public static void search(String cipher) {
		int steps = 0;
		double max = 0;
		for (int pos1 = 0; pos1 < cipher.length(); pos1++) {
			for (int len1 = 1; pos1 + len1 <= cipher.length(); len1++) {
				for (int pos2 = pos1 + 1; pos2 < cipher.length(); pos2++) {
					for (int len2 = 1; pos2 + len2 <= cipher.length(); len2++) {
						for (int pos3 = pos2 + 1; pos3 < cipher.length(); pos3++) {
							for (int len3 = 1; pos3 + len3 <= cipher.length(); len3++) {
								steps++;
								int[] tuple1 = new int[] { pos1, len1 };
								int[] tuple2 = new int[] { pos2, len2 };
								int[] tuple3 = new int[] { pos3, len3 };
								int[][] tuples = new int[][] { tuple1, tuple2, tuple3 };
//						int[][] tuples = new int[][] { tuple1 };
								StringBuilder sb = extract(cipher, tuples);
								List<String> split = split(sb);
								if (split.size() == 0)
									continue; // empty list

								boolean go = true;
								boolean output = false;
								for (int i = 0; i < split.size(); i++) {
									// terminal "word" must at least be a prefix
									String word = split.get(i);
									if (i == split.size() - 1) {
										if (!WordFrequencies.hasPrefix(word)) {
											go = false;
											break;
										}
										if (WordFrequencies.hasWord(word)) { // entire sequence contains valid words
											output = true;
										}
									} else if (!WordFrequencies.hasWord(word)) {
										go = false;
										break;
									}
								}
								if (!go)
									break;
								if (output) {
									double score = score3(split);
									if (score >= max * .75)
										System.out.println(score + "	" + dump(tuples) + "	" + split);
									max = Math.max(max, score);
								}
							}
						}
					}
				}
			}
		}
		System.out.println("Total steps: " + steps);
	}
	
	public static StringBuilder dump(int[][] tuples) {
		StringBuilder sb = new StringBuilder();
		for (int[] tuple : tuples) {
			sb.append(Arrays.toString(tuple));
			sb.append(" ");
		}
		return sb;
	}
	
	public static long score(List<String> list) {
		long score = 0;
		for (String word : list) {
			score += Math.pow(WordFrequencies.percentile(word), word.length()); 
		}
		return score;
	}
	public static double score2(List<String> list) {
		double score = 0;
		for (String word : list) {
			score += WordFrequencies.percentile(word); 
		}
		return score / list.size();
	}
	public static double score3(List<String> list) {
		StringBuffer sb = new StringBuffer();
		for (String word : list) {
			if (sb.length() > 0) {
				sb.append(" ");
			}
			sb.append(word);
		}
		return NGramsCSRA.zkscore(sb, "EN", true) / sb.length();
	}
	
	public static void searchOLD(String cipher) {
		for (int position = cipher.length()-1; position >= 0; position--) {
			List<String> list = new ArrayList<String>();
			searchOLD(cipher, position, new StringBuilder(), list);
			System.out.println(list);
		}
	}
	
	// find as many valid word sequences starting at this position and going in reverse.
	public static void searchOLD(String cipher, int position, StringBuilder currentPrefix, List<String> foundWords) {
		while (position >= 0) {
			char ch = cipher.charAt(position);
			while (!isLetter(ch) && position >= 0) {
				ch = cipher.charAt(position--);
			}
			
			currentPrefix.append(ch);
			if (!WordFrequencies.hasPrefix(currentPrefix.toString())) {
				
			}
			
			position--;
		}		
		
		// exit conditions: 
		// 1) position out of bounds
		if (position < 0) return;
		char ch = ' ';
		while (!isLetter(ch) && position > -1) 
			ch = cipher.charAt(position--); // go straight to next letter (skip over whitespace and other non-letters)
		// 2) no word with this prefix exists in the dictionary
		currentPrefix.append(ch);
		if (!WordFrequencies.hasPrefix(currentPrefix.toString())) {
			currentPrefix.deleteCharAt(currentPrefix.length()-1);
			return;
		}
		
		String word = currentPrefix.toString();
		// if current prefix matches a word, print it
		if (WordFrequencies.hasWord(word)) {
			//System.out.println(word.length() + " " + WordFrequencies.percentile(word) + " " + word);
			foundWords.add(word + " (" + WordFrequencies.percentile(word) + ")");
		}
		
		searchOLD(cipher, position-1, currentPrefix, foundWords);
		currentPrefix.deleteCharAt(currentPrefix.length()-1);
	}

	public static boolean isLetter(char ch) {
		return ch > 64 && ch < 91;
	}
	
	public static List<String> split(StringBuilder sb) {
		String[] split = sb.toString().split("[ .]");
		List<String> list = new ArrayList<String>();
		for (String s : split) 
			if (s.length() > 0) list.add(s);
		return list;
	}
	
	public static void main(String[] args) {
		search(W168_1D);
//		String[] split = split(new StringBuilder(W168_1D));
//		for (String s : split)
//			if (s.length() > 0)
//				System.out.println(s);
		
	}
}
