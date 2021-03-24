package com.zodiackillerciphers.dictionary;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.io.FileUtil;
import com.zodiackillerciphers.io.Unzip;
import com.zodiackillerciphers.lucene.NGrams;
import com.zodiackillerciphers.lucene.Stats;
import com.zodiackillerciphers.lucene.ZKDecrypto;
import com.zodiackillerciphers.names.Census;
import com.zodiackillerciphers.tests.cribbing.LocationSearch;

/** another recursive search of words/phrases that fit in Z13 */
public class Z13WordSearch {
	public static int MIN_WORD_LENGTH = 3;
	public static int CIPHER_LENGTH = 13;
	/** does the given string fit Z13?  allows for partial prefixes to fit too */ 
	public static boolean fits(StringBuffer sb) {
		
		/* Z13: */
		if (sb.length() > 11 && sb.charAt(0) != sb.charAt(11)) return false;
		if (sb.length() > 10 && sb.charAt(2) != sb.charAt(10)) return false;
		if (sb.length() > 6 && sb.charAt(4) != sb.charAt(6)) return false;
		if (sb.length() > 8 && sb.charAt(6) != sb.charAt(8)) return false;
		if (sb.length() > 12 && sb.charAt(7) != sb.charAt(12)) return false;
		return true;
		
		
		/* glurk's cipher: 8434567889788 */
		
		//8 4 3 4 5 6 7 8 8 9 7  8  8
		//0 1 2 3 4 5 6 7 8 9 10 11 12
//		if (sb.length() > 7 && sb.charAt(0) != sb.charAt(7)) return false;
//		if (sb.length() > 8 && sb.charAt(0) != sb.charAt(8)) return false;
//		if (sb.length() > 11 && sb.charAt(0) != sb.charAt(11)) return false;
//		if (sb.length() > 12 && sb.charAt(0) != sb.charAt(12)) return false;
		
//		if (sb.length() > 3 && sb.charAt(1) != sb.charAt(3)) return false;
//		if (sb.length() > 10 && sb.charAt(6) != sb.charAt(10)) return false;
		
//		return true;
		
	}
	
	public static boolean ignore(String word) {
		if (word.length() == 1) {
			if (word.equals("A")) return false;
			if (word.equals("I")) return false;
			return true;
		}
		return false;
	}
	
	/** search a giant corpus, looking only at whole word-level tokens that have total length 13
	 * and fit in Z13  
	 */
	public static void searchCorpus() {
		WordFrequencies.init();
		System.out.println("====== starting search ======");
		List<String> fileList = FileUtil.loadFrom("/Volumes/Biggie/projects/zodiac/docs/for-cribbing-experiment/gutenberg-wget/all-files.txt");
		long bytes = 0;
		for (String file : fileList) {
			File f = new File(file);
			Long length = f.length();
			System.out.println("File " + file + " length " + length); 
//			if (length > 10000000)
//				continue; // too big
			String contents = Unzip.read(f);
			bytes += contents.length();
//			System.out.println("Done unzipping.");
			String[] tokens = FileUtil.tokenizeAndConvert(contents);
//			System.out.println("Done tokenizing.");
			for (int i=0; i<tokens.length; i++) {
				String lineWithSpaces = "";
				String lineWithoutSpaces = "";
				for (int j=i; j<tokens.length; j++) {
					String token = tokens[j];
					if (token.length() < 1) continue;
					lineWithSpaces += token + " ";
					lineWithoutSpaces += token;
					if (lineWithoutSpaces.length() == 13 && fits(new StringBuffer(lineWithoutSpaces))) {
						System.out.println(WordFrequencies.scoreLog(lineWithSpaces) + "	" + ZKDecrypto.calcscore(lineWithoutSpaces) + "	" + lineWithSpaces + "	" + f.getName()); 
						continue;
					} else if (lineWithoutSpaces.length() > 13) {
						break;
					}
//					System.out.println("Smeg: " + lineWithSpaces);
				}
			}
		}
		System.out.println("TOTAL BYTES: " + bytes);
	}
	
	public static void search(int maxWords) {
		Census.init();
		WordFrequencies.init();
		search(new ArrayList<String>(), new StringBuffer(), maxWords);
	}
	public static void search(List<String> currentWords, StringBuffer currentPlaintext, int maxWords) {
		int remaining = CIPHER_LENGTH-currentPlaintext.length();
		if (remaining == 0) { // we filled up Z13 without violating any constraints
			dump(currentWords);
			return;
		}
		if (remaining < 0) throw new RuntimeException("" + remaining);
		if (currentWords.size() >= maxWords) return; // too many words 
		// recurse
		for (int L=remaining; L>=MIN_WORD_LENGTH; L--) {
			List<String> words = WordFrequencies.byLength.get(L);
			//Collections.sort(words);
			if (words == null || words.size() == 0) continue;
			for (String word : words) {
				if (ignore(word)) continue;
				StringBuffer sbNew = new StringBuffer(currentPlaintext).append(word);
				if (fits(sbNew)) {
					currentWords.add(word);
					search(currentWords, sbNew, maxWords);
					currentWords.remove(currentWords.size()-1);
				}
			}
		}
	}
	
	/** roulette-style search.  keep adding random words until cipher constraints violated
	 * or match found.
	 */
	public static void searchRoulette() {
		Census.init();
		RouletteDictionary.init();
		WordFrequencies.init();
		long total = 0;
		while(true) {
			StringBuffer plain = new StringBuffer();
			StringBuffer desc = new StringBuffer();
			boolean valid = true;
			float scoreCensus = 1;
			float scoreFreq = 1;
			int n = 0;
			total++;
			while(valid) {
				
				String word = RouletteDictionary.randomWord();
				plain.append(word);
				desc.append(word + " ");
				n++;

				scoreFreq *= (WordFrequencies.percentile(word) + 1) * word.length();
				scoreCensus *= (1+Census.score(word));
				
				if (!fits(plain) || plain.length() > 13) {
					valid = false;
				} else if (plain.length() == 13) {
					System.out.println(scoreFreq + "	" + scoreCensus + "	" + n + "	" + total + "	" + plain + "	" + desc);
					valid = false;
				}
			}
		}
	}

	public static void searchRouletteDonnaLass1() {
		RouletteDictionary.init();
		WordFrequencies.init();
		Census.init();
		long total = 0;
		while(true) {
			StringBuffer plain = new StringBuffer();
			StringBuffer desc = new StringBuffer();
			boolean valid = true;
			float scoreCensus = 1;
			float scoreFreq = 1;
			int n = 0;
			total++;
			while(valid) {
				
				String word = RouletteDictionary.randomWord();
				plain.append(word);
				desc.append(word + " ");
				n++;

				scoreFreq *= (WordFrequencies.percentile(word) + 1) * word.length();
				scoreCensus *= (1+Census.score(word));
				
				if (LocationSearch.errors4(plain.toString()) > 0 || plain.length() > 29) {
					valid = false;
				} else if (plain.length() >= 20) {
					System.out.println(scoreFreq + "	" + scoreCensus + "	" + plain.length() + "	" + n + "	" + total + "	" + plain + "	" + desc);
					valid = false;
				}
			}
		}
	}

	public static void searchRouletteDonnaLass2() {
		RouletteDictionary.init();
		WordFrequencies.init();
		Census.init();
		long total = 0;
		while(true) {
			StringBuffer plain = new StringBuffer();
			StringBuffer desc = new StringBuffer();
			boolean valid = true;
			float scoreCensus = 1;
			float scoreFreq = 1;
			int n = 0;
			total++;
			while(valid) {
				
				String word = RouletteDictionary.randomWord();
				plain.append(word);
				desc.append(word + " ");
				n++;

				scoreFreq *= (WordFrequencies.percentile(word) + 1) * word.length();
				scoreCensus *= (1+Census.score(word));
				
				if (LocationSearch.errors3(plain.toString()) > 0 || plain.length() > 9) {
					valid = false;
				} else if (plain.length() == 9) {
					System.out.println(scoreFreq + "	" + scoreCensus + "	" + plain.length() + "	" + n + "	" + total + "	" + plain + "	" + desc);
					valid = false;
				}
			}
		}
	}
	
	public static void dump(List<String> words) {
		float score = scoreFreq(words.toArray(new String[0]));
		String[] arr = words.toArray(new String[0]);
		float scoreCensus = scoreCensus(arr);
		String line = "" + score + ", " + scoreCensus + ", " + words.size() + ", ";
		for (String word : words) line += word + " ";
		for (String word : words) line += WordFrequencies.percentile(word) + " ";
		System.out.println(line);
	}
	
	public static float scoreFreq(String[] words) {
		float score = 1.0f;
		for (String word : words) {
			score *= (WordFrequencies.percentile(word) + 1) * word.length();
		}
		return score;
	}
	public static float scoreCensus(String[] words) {
		return Census.score(words);		
	}
	public static float scoreZk1(String[] words) {
		//return ZKDecrypto.calcscore(words);
		return NGrams.zkscore(words);
	}
	public static float scoreZk2(String[] words) {
		return ZKDecrypto.calcscore(words);
	}
	
	/** read solutions (space delimited words, one solution per line) and score then.
	 * 
	 * scores: word frequencies, census frequencies, zkdecrypto score, ioc 
	 */
	public static void processSolutions(String path) {
		WordFrequencies.init();
		Census.init();
		BufferedReader input = null;
		try {
			input = new BufferedReader(new FileReader(new File(path)));
			String line = null; // not declared within while loop
			while ((line = input.readLine()) != null) {
				String[] words = line.split(" ");
				StringBuffer sb = new StringBuffer();
				StringBuffer concat = new StringBuffer();
				for (String word : words) concat.append(word);
				if (concat.length() == 13) {
					sb.append(scoreZk1(words) + "	" + scoreZk2(words) + "	" + scoreFreq(words) + "	"
							+ scoreCensus(words) + "	" + Stats.iocDiff(words) + "	"
							+ Arrays.toString(words));
					System.out.println(sb);
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
	
	public static void main(String[] args) {
		//search(Integer.valueOf(args[0]));
		//search(2);
		//search(3);
		//search(4);
		//searchRoulette();
//		System.out.println(NGrams.zkscore(new StringBuffer("SARAHTHEHORSE")));
//		processSolutions("/Users/doranchak/projects/zodiac/z13-results-combined.txt");
		//searchRouletteDonnaLass2();
		searchCorpus();
	}
}
