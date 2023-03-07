package com.zodiackillerciphers.lucene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.zodiackillerciphers.io.FileUtil;

//import org.apache.log4j.Logger;

public class NGramsCSRA {
	
//	private static Logger LOGGER = Logger.getLogger(NGrams.class);
	
	//public static String DATA_DIR = "conf/language-ngram-stats/";
	public static String DATA_DIR = "";
	public static String OPTIONAL_PREFIX = "";

	public static Map<String, String> ngramDirMap = new HashMap<>();
	
	public static int MAX_NGRAM_LENGTH = 6;
	
	public static List<String> zkLangList = Arrays.asList("EN", "ES", "FR", "DE", "IT");
	static {
		ngramDirMap.put("EN", "eng");
		ngramDirMap.put("ES", "spa");
		ngramDirMap.put("FR", "fre");
		ngramDirMap.put("DE", "ger");
		ngramDirMap.put("IT", "ita");
	}
	
	public static String[] FILES = new String[] {
		"unigraphs",
		"bigraphs",
		"trigraphs",
		"tetragraphs",
		"pentagraphs",
		"hexagraphs"
	};
	
	// EN --> (1 --> (a --> 43))
	//        (2 --> (aa --> 35))
	//        (3 --> (aaa --> 123))
	//        (4 --> (aaaa --> 37))
	//        (5 --> (aaaaa --> 51))
	// ES --> (1 --> (a --> 52))
	// ......
	public static Map<String, Map<Integer, Map<String, Double>>> ngramMapsWithSpaces = 
			new HashMap<String, Map<Integer, Map<String, Double>>>();
	public static Map<String, Map<Integer, Map<String, Double>>> ngramMapsWithoutSpaces = 
			new HashMap<String, Map<Integer, Map<String, Double>>>();
	
	public static Map<String, Map<Integer, Integer>> ngramTotalsWithSpaces = 
			new HashMap<String, Map<Integer, Integer>>();
	public static Map<String, Map<Integer, Integer>> ngramTotalsWithoutSpaces = 
			new HashMap<String, Map<Integer, Integer>>();
	
	/** if true, use the "unrolled" ngram maps directly instead of the nested maps */
	public static boolean useCachedMaps = true;
	public static Map<String, Double>[] cachedMaps;
	
	/** init with defaults */
	public static void init() {
		NGramsCSRA.OPTIONAL_PREFIX = "/Users/doranchak/projects/zodiac/zkdecrypto/zkdecrypto/language/";
		NGramsCSRA.init("EN");
	}
	
	public static void init(String langID) {
		init(langID, false, false);
		init(langID, false, true);
	}
	private static void init(String langID, boolean withSpaces) {
		init(langID, true, withSpaces);
	}
	private static void init(String langID, boolean classpath, boolean withSpaces) {
		Map<String, Map<Integer, Map<String, Double>>> ngramMaps = withSpaces ? ngramMapsWithSpaces
				: ngramMapsWithoutSpaces;
		if ((ngramMaps != null) && (ngramMaps.containsKey(langID))) {
			return;
		}
		
		if (langID == null) {
			langID = "EN";
		}
		
		// load the ngram data for this language
		for (int i = 0; i < FILES.length; i++) {
			loadFrom(langID, FILES[i], i+1, classpath, withSpaces);
		}
		
		if (useCachedMaps)
			initCachedMaps(langID, withSpaces);
	}
	
	static void loadFrom(String langID, String s, int n, boolean withSpaces) {
		loadFrom(langID, s, n, true, withSpaces);
	}
	
	static void loadFrom(String langID, String s, int n, boolean classpath, boolean withSpaces) {
		String w = withSpaces? "" : "out";
		s += "-with" + w + "-spaces.txt";
		
		if (n==1) {
			s = "unigraphs.txt";
		}
		
		String fullPath = OPTIONAL_PREFIX + DATA_DIR + ngramDirMap.get(langID) + "/" + s;
		//System.out.println("loading from [" + fullPath + "] " + classpath + "...");

		Map<String, Map<Integer, Map<String, Double>>> ngramMaps = withSpaces ? ngramMapsWithSpaces
				: ngramMapsWithoutSpaces;
				
		Map<Integer, Map<String, Double>> currentNgramMap = null;
		if (!ngramMaps.containsKey(langID)) {
			currentNgramMap = new HashMap<Integer, Map<String, Double>>();
			ngramMaps.put(langID, currentNgramMap);
		}
		else {
			currentNgramMap = ngramMaps.get(langID);
		}
		
		Map<String, Double> dataMap = new HashMap<String, Double>();
		currentNgramMap.put(n, dataMap);
		
		Map<String, Map<Integer, Integer>> ngramTotals = withSpaces ? ngramTotalsWithSpaces : ngramTotalsWithoutSpaces;
		
		Map<Integer, Integer> currentCountMap = null;
		if (!ngramTotals.containsKey(langID)) {
			currentCountMap = new HashMap<Integer, Integer>();
			ngramTotals.put(langID, currentCountMap);
		}
		else {
			currentCountMap = ngramTotals.get(langID);
		}
		
		BufferedReader input = null;
		int counter = 0;
		try {
			InputStream is = null;
			if (classpath) {
				is = NGrams.class.getClassLoader().getResourceAsStream(fullPath);
				if (is == null) {
					classpath = false;
				}
			}
			System.out.println("Loading ngram stats from " + fullPath);
			input = classpath ? new BufferedReader(new InputStreamReader(is)) : new BufferedReader(new FileReader(new File(fullPath)));
			String line = null; // not declared within while loop
			int sum = 0;
			while ((line = input.readLine()) != null) {
				if (n == 1) {
					int col = 3;
					String[] split = line.split(" ");
					String key = split[0];
					Double val = Double.valueOf(split[col]);
					dataMap.put(key, val);
				} else {
					String key = line.substring(0, n);
					String sub = line.substring(n + 3);
					String[] split = sub.split(" ");
					Double val = Double.valueOf(split[0]);
					if (n > 1) {
						sum += val;
					}
					dataMap.put(key, val);
				}
			}
			
			if (n > 1) {
				currentCountMap.put(n, sum);
			}
			
			// if n == 1, we are loading the unigraphs. So we will calculate the entropy and chi-squared for this zk-language
			if (n == 1) {
				Stats.calculateLanguageStats(langID, dataMap);
			}
			
//			LOGGER.debug("read " + counter + " lines.  sum: " + sum);
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

	protected static void initCachedMaps(String langID, boolean withSpaces) {
		cachedMaps = new HashMap[5]; // [2, 6]
		for (int n=2; n<7; n++) {
			if (withSpaces)
				cachedMaps[n-2] = ngramMapsWithSpaces.get(langID).get(n);
			else
				cachedMaps[n-2] = ngramMapsWithoutSpaces.get(langID).get(n);
		}
		
	}
	
	// expects uppercase ngram 
	public static Double valueFor(String ngram, String langID, boolean withSpaces) {
		if (ngram == null) {
			return new Double(0);
		}
		Double val;
		int n = ngram.length();
		/** attempt for faster performance: use cached (unnested) references to inner maps */
		if (useCachedMaps) {
			val = cachedMaps[n-2].get(ngram);
			return val == null ? 0 : val;
		}
		
		if (withSpaces) {
			val = ngramMapsWithSpaces.get(langID).get(ngram.length()).get(ngram);
		} else {
			val = ngramMapsWithoutSpaces.get(langID).get(ngram.length()).get(ngram);
		}
		return val == null ? 0 : val;
	}
	
	// sum of n-gram scores across the given string 
	public static Double ngramSum(String inp, String langID, boolean withSpaces, int n) {
		Double sum = 0d;
		for (int i=0; i<inp.length()-n+1; i++) {
			String ngram = inp.substring(i,i+n);
			double val = valueFor(ngram, langID, withSpaces);
			sum += val;
		}
		return sum;
	}
	public static Double ngramSumOLD(String inp, String langID, boolean withSpaces, int n, List<Double> all) {
		Double sum = 0d;
		if (all != null) all.clear();
		for (int i=0; i<inp.length()-n+1; i++) {
			String ngram = inp.substring(i,i+n);
			double val = valueFor(ngram, langID, withSpaces);
			sum += val;
			all.add(val);
		}
		return sum;
	}
	
	/** return zkdecrypto-style score for the given string.  expects uppercase string. */
	/*
	public static float zkscore(String[] words) {
		StringBuffer sb = new StringBuffer();
		for (String word: words) {
			sb.append(word);
		}
		return zkscore(sb);
	}
	*/
	
	static boolean has(String langID, boolean withSpaces) {
		if (withSpaces) {
			return ngramMapsWithSpaces.containsKey(langID);
		}
		return ngramMapsWithoutSpaces.containsKey(langID);
	}
	public static float zkscore(StringBuffer sb, String langID, boolean withSpaces) { return zkscore(sb, false, false, langID, withSpaces); }

	/** uniquesOnly is no longer used */
	public static float zkscore(StringBuffer sb, boolean uniquesOnly, boolean debug, String langID, boolean withSpaces) {
//		LOGGER.info("zkscore sb " + sb);
		if (!useCachedMaps) {
			if (!has(langID, withSpaces)) {
				// we don't have ngram stats for this language with spaces, so revert to ngram stats without spaces.
				System.out.println("WARN: we don't have ngram stats for this language with spaces, so revert to ngram stats without spaces.");
				withSpaces = false;
				sb = new StringBuffer(sb.toString().replaceAll(" ", ""));
			}
		}
		if (sb == null) {
			return 0;
		}
//		Set<String> seen = null;
//		if (uniquesOnly) {
//			seen = new HashSet<String>();
//		}
		float result = 0;
		for (int n=2; n<=MAX_NGRAM_LENGTH; n++) {
			float sum = 0;
			for (int i=0; i<sb.length()-n+1; i++) {
				String sub = sb.substring(i, i+n);
//				if (!uniquesOnly || (uniquesOnly && !seen.contains(sub))) {
				Double val = valueFor(sub, langID, withSpaces); 
				sum += val;
//					if (debug && val > 0) {
//						LOGGER.debug(n + " " + val + " " + sub);
//					}
//				}
//				if (uniquesOnly) {
//					seen.add(sub);
//				}
			}
			//System.out.println("len " + n + " sum " + sum);
			//sum = sum/((float)Math.pow(2, MAX_NGRAM_LENGTH-n));
			sum = sum/pow(MAX_NGRAM_LENGTH-n);
			result += sum;
		}
		//System.out.println(result);
		return result;
	}
	
	// return 2^n, possibly faster than Math.pow?
	static int pow(int n) {
		if (n==0) return 1;
		if (n==1) return 2;
		if (n==2) return 4;
		if (n==3) return 8;
		if (n==4) return 16;
		if (n==5) return 32;
		if (n==6) return 64;
		throw new RuntimeException("Don't call this with " + n);
	}
	
	// for CSRA testing
	/*
	public static float zkscore2(String sb) {
		if (sb == null) {
			return 0;
		}
		int n = 5;
		float sum = 0;
		Set<String> seen = new HashSet<String>();
		for (int i = 0; i < sb.length() - n + 1; i++) {
			String sub = sb.substring(i, i + n);
			if (seen.contains(sub)) {
				continue;
			}
			seen.add(sub);
			float val = valueFor(sub);
			if (val > 0) {
				//sum += Math.log(val);
				sum += val;
			}
		}
		return sum;
	}
	*/

	static String alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	static int count = 0;
	/** for Let's Crack Zodiac episode 4.  test all keys for cipher ABCCBCCBDDB (plaintext: MISSISSIPPI) */
	public static void lczCipher() {
		NGramsCSRA.OPTIONAL_PREFIX = "/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/tools/zkdecrypto/zkdecrypto/language/";
		init("EN");
		char[] key = new char[4];
		String cipher = "ABCCBCCBDDB";
		lczCipher(cipher, key, 0);
	}
	public static void lczCipher(String cipher, char[] key, int index) {
		if (index == key.length) {
			// we have the full key, generate plaintext and measure.
			String pt = "";
			for (int i=0; i<cipher.length(); i++) {
				int k=cipher.charAt(i)-65;
				pt += key[k];
			}
			double zk = zkscore(new StringBuffer(pt), "EN", false);
			if (zk >= 400) System.out.println(zk + " " + pt + " " + Arrays.toString(key));
			count++;
			return;
		}
		for (int i=0; i<alpha.length(); i++) {
			char p = alpha.charAt(i);
			boolean go = true;
			for (int j=index-1; j>=0; j--) {
				if (key[j] == p) { // don't repeat pt assignments in the key
					go = false;
					break;
				}
			}
			if (go) {
				key[index] = p;
				lczCipher(cipher, key, index+1);
				key[index] = 0;
			}
		}
	}
	/** score all lines in the given file */
	public static void scoreFile(String path, boolean includeSpaces) {
		String TAB = "	";
		NGramsCSRA.OPTIONAL_PREFIX = "/Users/doranchak/projects/zodiac/zkdecrypto/zkdecrypto/language/";
		init("EN");
		List<String> list = FileUtil.loadFrom(path);
		for (String line : list) {
			float score = NGramsCSRA.zkscore(new StringBuffer(line), "EN", includeSpaces);
			System.out.println(score + TAB + line);
		}
	}
	
	/** score all lines in the given file.  file is huge, so continually track the top N results using a heap. */
	public static void scoreFileTopN(String path, boolean includeSpaces, int N) {
		String TAB = "	";
		NGramsCSRA.OPTIONAL_PREFIX = "/Users/doranchak/projects/zodiac/zkdecrypto/zkdecrypto/language/";
		init("EN");
		
		TreeSet<ScoreBean> treeSet = new TreeSet<ScoreBean>();
		
		BufferedReader input = null;
		int counter = 0;
		try {
			input = new BufferedReader(
			           new InputStreamReader(new FileInputStream(path), "UTF-8"));
			
			String line = null; // not declared within while loop
			while ((line = input.readLine()) != null) {
				counter++;
				float score = NGramsCSRA.zkscore(new StringBuffer(line), "EN", includeSpaces);
				ScoreBean bean = new ScoreBean(score, line);
				boolean print = false;
				// cases:
				// 1) heap still has room
				if (treeSet.size() < N) {
					treeSet.add(bean);
					print = true;
				}
				// 2) heap full but already has this entry
				if (treeSet.contains(bean)) 
					continue;
				// 3) heap full but new bean is better than heap's worst bean
				ScoreBean worstBean = treeSet.first();
				if (worstBean.score < bean.score) {
					treeSet.remove(worstBean);
					treeSet.add(bean);
					print = true;
				}
				
				if (print) {
					System.out.println(bean);
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

		System.out.println("======== HEAP: ");
		for (ScoreBean bean : treeSet) {
			System.out.println(bean);
		}
		
	}
	
	
	public static void main(String[] args) {
		NGramsCSRA.OPTIONAL_PREFIX = "/Users/doranchak/projects/zodiac/zkdecrypto/zkdecrypto/language/";
		init("EN");
		System.out.println(zkscore(new StringBuffer("HE LIES STILL"), "EN", true));
//		System.out.println(zkscore(new StringBuffer("IOQUWEIOUIO"), "EN", false));
		//scoreFile("/Users/doranchak/projects/ciphers/W168/sam-plaintexts-combined.txt", true);
//		scoreFile("/Users/doranchak/projects/ciphers/W168/test.txt", true);
//		lczCipher();
//		URL url = NGrams.class.getClassLoader().getResource(".");
//		LOGGER.debug(url.toString());
//		scoreFileTopN("/Users/doranchak/projects/ciphers/W168/more_enumerations/combined-for-ngram-scores.txt", true, 1000);
	}
}
