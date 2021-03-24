package com.zodiackillerciphers.corpus;

import static com.zodiackillerciphers.corpus.CorpusBase.file;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.zodiackillerciphers.io.FileUtil;
import com.zodiackillerciphers.io.Unzip;
import com.zodiackillerciphers.tests.unicity.PlaintextBean;
import com.zodiackillerciphers.tests.unicity.SubstitutionMutualEvolve;

public class CorpusBase {
	public static String alpha = "BCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static Random rand = new Random();
	public static String alphaFull = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	public static boolean SHOW_INFO = true;
	public static boolean REDDIT_ONLY = false;
	public static boolean GUTENBERG_ONLY = false;
	public static boolean KEEP_APOSTROPHE = false;
	
	/* list of all zip files in the corpus */
	static List<String> fileList;
	/* files we've already seen */
	static Set<String> seen = new HashSet<String>();
	/* currently selected file */
	public static String file;
	/* tokens read from the file */
	public static String[] tokens;
	/* list of word n-grams */
	static List<String[]> ngrams;
	
	/** map unique n-grams by length */
	public static Map<Integer, List<String>> ngramMap = new HashMap<Integer, List<String>>();
	
	public static int currentRandomSelection;
	
	/** from the currently selected source, return all ngrams that add up to given length N. */
	public static List<List<String>> ngrams(int N) {
		List<List<String>> results = new ArrayList<List<String>>();
		for (int i=0; i<tokens.length; i++) {
			List<String> list = new ArrayList<String>();
			StringBuffer nospaces = new StringBuffer();
			for (int j=i; j<tokens.length; j++) {
				String token = tokens[j];
				list.add(token);
				nospaces.append(token);
				if (nospaces.length() == N) {
					results.add(list);
					break;
				} else if (nospaces.length() > N) {
					break;
				}
			}
		}
		return results;
	}
	public static StringBuffer flatten(List<String> list) {
		return flatten(list, false);
	}
	public static StringBuffer flatten(List<String> list, boolean keepSpaces) {
		StringBuffer sb = new StringBuffer();
		for (String str : list) {
			if (keepSpaces && sb.length() > 0) sb.append(" ");
			sb.append(str);
		}
		return sb;
	}
	public static StringBuffer flatten(String[] list, boolean keepSpaces) {
		StringBuffer sb = new StringBuffer();
		for (String str : list) {
			if (keepSpaces && sb.length() > 0) sb.append(" ");
			sb.append(str);
		}
		return sb;
	}
	
	/** returns an array of the given text's length.
	 * each position of the array points to the next position
	 * that contains the same character, or -1 if none is found.
	 */
	public static int[] nextMatches(String str) {
		int[] result = new int[str.length()];
		for (int i=0; i<result.length-1; i++) {
			int next = -1;
			for (int j=i+1; j<result.length; j++) {
				char c1 = str.charAt(i);
				char c2 = str.charAt(j);
				if (c1 == c2) {
					next = j;
					break;
				}
			}
			result[i] = next;
		}
		result[result.length-1] = -1;
		return result;
	}
	public static int[] nextMatches(StringBuffer str) {
		int[] result = new int[str.length()];
		for (int i=0; i<result.length-1; i++) {
			int next = -1;
			for (int j=i+1; j<result.length; j++) {
				char c1 = str.charAt(i);
				char c2 = str.charAt(j);
				if (c1 == c2) {
					next = j;
					break;
				}
			}
			result[i] = next;
		}
		result[result.length-1] = -1;
		return result;
	}
	public static boolean validSubstitution(String cipher, StringBuffer plaintext) {
		Map<Character, Character> c2p = new HashMap<Character, Character>();
		for (int i=0; i<cipher.length(); i++) {
			char c = cipher.charAt(i);
			char p = plaintext.charAt(i);
			Character val = c2p.get(c);
			if (val == null) val = p;
			if (val != p) return false;
			c2p.put(c, val);
		}
		return true;
	}
	// prepare the corpus
	public static void initSources() {
		fileList = FileUtil.loadFrom("/Volumes/Smeggabytes/projects/zodiac/docs/all-files.txt");
		for (int i=fileList.size()-1; i>=0; i--) {
			if (fileList.get(i).contains("filetypes[]=txt")) {
				fileList.remove(i);
			}
			if (CorpusBase.REDDIT_ONLY && !fileList.get(i).contains("reddit_data")) {
				fileList.remove(i);
			} else if (CorpusBase.GUTENBERG_ONLY && !fileList.get(i).contains("gutenberg")) {
				fileList.remove(i);
			}
		}
		shuffleList();
	}
	// shuffle the list of sources in place.  makes random selection simpler, without repeating sources.
	public static void shuffleList() {
		for (int i=fileList.size()-1; i>0; i--) {
			// swap with an element in [0,i-1]
			int j = rand.nextInt(i);
			String iVal = fileList.get(i);
			String jVal = fileList.get(j);
			fileList.set(i, jVal);
			fileList.set(j, iVal);
		}
		currentRandomSelection = 0;
	}
	
	public static void initNgramMap() {
		ngramMap.clear();
		int minLength = 5;
		int maxLength = 15;
		for (int a=0; a<tokens.length; a++) {
			String spaces = "";
			String noSpaces = "";
			for (int b=a; b<tokens.length; b++) {
				String token = tokens[b];
				if (tokens.length < 1) continue;
				if (spaces.length() > 0) spaces += " ";
				spaces += token;
				noSpaces += token;
				int len = noSpaces.length();
				if (len >= minLength && len <= maxLength) {
					List<String> val = ngramMap.get(len);
					if (val == null) val = new ArrayList<String>();
					if (!val.contains(spaces)) val.add(spaces);
//					System.out.println(a + ": " + spaces);
					ngramMap.put(len, val);
				}
				if (len > maxLength) break;
			}
		}
	}
	
	// pick and load a random source from the corpus
	public static void randomSourceOLD() {
		while (true) {
			file = fileList.get(rand.nextInt(fileList.size()));
			if (seen.contains(file)) continue;
			if (!file.endsWith(".zip")) continue;
			File f = new File(file);
			Long length = f.length();
			if (CorpusBase.SHOW_INFO) System.out.println("File " + file + " length " + length); 
			if (length > 10000000)
				continue; // too big
			seen.add(file);
			Unzip.SHOW_INFO = CorpusBase.SHOW_INFO;
			String contents = Unzip.read(f);
//			System.out.println("Done unzipping.");
			tokens = FileUtil.tokenizeAndConvert(contents);
			if (tokens.length < 3) continue;
			break;
		}
	}

	/** returns true if we wrapped around back to the beginning */
	static boolean incRandSel() {
		currentRandomSelection = (currentRandomSelection + 1) % fileList.size();
		if (currentRandomSelection == 0) return true;
		return false;
	}
	/** returns true if we reached the end of the list and are about to repeat. */
	public static boolean randomSource() {
		boolean result = false;
		while (true) {
			file = fileList.get(currentRandomSelection);
			result = incRandSel();
			if (!file.endsWith(".zip")) {
				continue;
			}
			File f = new File(file);
			Long length = f.length();
			if (CorpusBase.SHOW_INFO) System.out.println("File " + file + " length " + length); 
			if (length > 10000000) {
				continue; // too big
			}
			Unzip.SHOW_INFO = CorpusBase.SHOW_INFO;
			String contents = Unzip.read(f);
			tokens = FileUtil.tokenizeAndConvert(contents, KEEP_APOSTROPHE);
			if (tokens.length < 3) {
				continue;
			}
			break;
		}
		return result;
	}
	
	public static String[] randomNgram(int L) {
		List<String> list = ngramMap.get(L);
		if (list == null) return null;
		int which = rand.nextInt(list.size());
		return list.get(which).split(" ");
	}
	
	static String[] randomNgramOLD(int L) {
		List<String> list = new ArrayList<String>();
		while (true) {
			list.clear();
			String str = "";
			int pos = rand.nextInt(tokens.length);
			for (int i=pos; i<tokens.length; i++) {
				String token = tokens[i];
				str += token;
				list.add(token);
				if (str.length() == L) {
					return list.toArray(new String[0]);
				} else if (str.length() > L) {
					break;
				}
			}
		}
	}
	
	/** generate the given number of samples of the given length */
	public static void produceRandomSamplesWithLength(int number, int length) {
		String tab = "	";
		initSources();
		Random rand = new Random();
		int found = 0;
		while (found < number) {
			randomSource();
			List<List<String>> candidates = ngrams(length);
			if (candidates.isEmpty()) continue;
			List<String> candidate = candidates.get(rand.nextInt(candidates.size()));
			StringBuffer flat = flatten(candidate);
			System.out.println(flat + tab + candidate + tab + file);
			found++;
		}
	}
	
	public static void testRandomSources() {
		initSources();
		for (int i=0; i<50; i++) {
			randomSource();
			System.out.println(file);
			System.out.println(Arrays.toString(tokens));
		}
	}
	public static void main(String[] args) {
//		testRandomSources();
		GUTENBERG_ONLY = true;
		produceRandomSamplesWithLength(1, 340);
	}
}
