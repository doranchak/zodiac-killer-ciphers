package com.zodiackillerciphers.tests;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.zodiackillerciphers.io.FileUtil;
import com.zodiackillerciphers.lucene.Settings;
import com.zodiackillerciphers.lucene.TranspositionVectorIndividual;

import ec.util.MersenneTwisterFast;

public class NickPelling {
	/** http://ciphermysteries.com/2017/03/15/might-z408-zodiac-killer-cipher-clue-book */
	static Map<String, int[]> preMap = new HashMap<String, int[]>();
	static {
		preMap.put("E", new int[] {7,7});
		preMap.put("TAOINS", new int[] {4,4});
		preMap.put("LR", new int[] {3,3});
		preMap.put("DFH", new int[] {2,2});
		preMap.put("BCGKMPUVWYJQZ", new int[] {0,1});
		preMap.put("X", new int[] {0,3});
	}
	
	/** map letter to accepted range of counts */
	static Map<Character, int[]> map = new HashMap<Character, int[]>();
	static {
		for (String key : preMap.keySet()) {
			for (int i=0; i<key.length(); i++) {
				char ch = key.charAt(i);
				map.put(ch, preMap.get(key));
			}
		}
		
	}
	
	public static void filterTestAll(String directory) {
		File dir =new File(directory);
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isDirectory()) continue;
			if (file.getName().endsWith(".txt"))
				filterTest(file.getPath());
		}
	}
	public static void filterTest(String path) {
		StringBuffer text = FileUtil.loadSBFrom(path);
		text = FileUtil.convert(text.toString());
		System.out.println("Length " + text.length() + " Path " + path);
		filterTestText(text.toString());
	}
	public static boolean filterTestText(String text) {
		boolean match = false;
		/** scan given text to look for segments that satisfy the filter */
		for (int i=0; i<text.length(); i++) {
			String sub = "";
			Map<Character, Integer> counts = new HashMap<Character, Integer>(); 
			for (int j=i; j<text.length(); j++) {
				char ch = text.charAt(j);
				sub += ch;
				Integer val = counts.get(ch);
				if (val == null) val = 0;
				val++;
				counts.put(ch ,val);
//				System.out.println("added " + ch + ": " + counts);
				int result = check(counts);
				if (result == -1) {
//					System.out.println("violation at " + ch);
					break;
				}
				if (result == 1) {
					System.out.println(i + ": " + sub);
					match = true;
					break;
				}
			}
		}
		return match;
		
	}
	
	/** return -1 if ranges violated, 0 if not, 1 if all ranges are exact */
	static int check(Map<Character, Integer> counts) {
		boolean exact = true;
		for (Character ch : map.keySet()) {
			int min = map.get(ch)[0];
			int max = map.get(ch)[1];
			Integer val = counts.get(ch);
			if (val == null) val = 0;
			if (val > max) return -1;
			if (val < min) exact = false;
		}
		if (exact) return 1;
		return 0;
	}
	public static void testZodiacLetters() {
		String[] files = new String[] {
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/letters/1966-11-29-the-confession.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/letters/1966-12-desk.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/letters/1967-04-30-bates.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/letters/1969-07-31-cipher.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/letters/1969-07-31-letter-envelope.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/letters/1969-07-31-letter-sf-examiner-envelope.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/letters/1969-07-31-letter-sf-examiner.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/letters/1969-07-31-letter-vallejo-times-herald-envelope.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/letters/1969-07-31-letter-vallejo-times-herald.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/letters/1969-07-31-letter.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/letters/1969-08-04-examiner.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/letters/1969-09-27-car-door.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/letters/1969-10-13-examiner-envelope.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/letters/1969-10-13-examiner.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/letters/1969-11-08-340-cipher-envelope.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/letters/1969-11-08-340-cipher.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/letters/1969-11-09-chronicle-envelope.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/letters/1969-11-09-chronicle.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/letters/1969-12-20-melvin-envelope.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/letters/1969-12-20-melvin.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/letters/1970-04-20-my-name-is-envelope.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/letters/1970-04-20-my-name-is.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/letters/1970-04-28-chronicle.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/letters/1970-06-26-chronicle-cipher-envelope.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/letters/1970-06-26-chronicle-cipher.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/letters/1970-07-24-chronicle-envelope.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/letters/1970-07-24-chronicle.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/letters/1970-07-26-chronicle-envelope.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/letters/1970-07-26-chronicle.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/letters/1970-10-05.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/letters/1970-10-27-avery-envelope.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/letters/1970-10-27-avery.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/letters/1971-03-13-times-envelope.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/letters/1971-03-13-times.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/letters/1971-03-22-lake-tahoe-card-back.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/letters/1971-03-22-lake-tahoe-card.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/letters/1974-01-29-exorcist-envelope.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/letters/1974-01-29-exorcist.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/letters/1974-02-14-sla-envelope.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/letters/1974-02-14-sla.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/letters/1974-05-08-badlands-envelope.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/letters/1974-05-08-badlands.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/letters/1974-07-08-count-marco-red-phantom-envelope.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/letters/1974-07-08-count-marco-red-phantom.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/letters/1978-04-24-chronicle.txt"
			};
		for (String file : files) {
			System.out.println(file);
			filterTest(file);
		}
	}
	
	public static void testBooks() {
		String[] files = new String[] {
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/gutenberg/pg10462.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/gutenberg/pg10799.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/gutenberg/pg11364.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/gutenberg/pg11889.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/gutenberg/pg12180.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/gutenberg/pg1322.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/gutenberg/pg1342.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/gutenberg/pg135.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/gutenberg/pg1661.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/gutenberg/pg2591.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/gutenberg/pg30601.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/gutenberg/pg4300.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/gutenberg/pg5200.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/gutenberg/pg76.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/gutenberg/pg9296.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/gutenberg/pg9798.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/gutenberg/pg9881.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/gutenberg/tale.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/generator/corpus-for-cipher-generator-with-others/pg1184.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/generator/corpus-for-cipher-generator-with-others/pg135.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/generator/corpus-for-cipher-generator-with-others/pg14833.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/generator/corpus-for-cipher-generator-with-others/pg25880.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/generator/corpus-for-cipher-generator-with-others/pg2600.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/generator/corpus-for-cipher-generator-with-others/pg2701.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/generator/corpus-for-cipher-generator-with-others/pg505.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/generator/corpus-for-cipher-generator-with-others/pg747.txt",
				"/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/generator/corpus-for-cipher-generator-with-others/pg808.txt"				
		};
		for (String file : files) {
			System.out.println(file);
			filterTest(file);
		}
	}
	
	static void testRandom() {
		MersenneTwisterFast rand = new MersenneTwisterFast();
		
		// generate random strings of length 59, since that's the max allowed by the filter
		// use random letter selection that samples from english language letter frequencies
		String str;
		int count = 0;
		while (true) {
			str = "";
			for (int i=0; i<59; i++) {
				str += TranspositionVectorIndividual.randomLetter(rand);
			}
			count++;
			str = str.toUpperCase();
			System.out.println(str);
			if (filterTestText(str)) {
				System.out.println(count + " " + str);
			}
			if (count % 100000 == 0) System.out.println(count+"...");
		}
		
		
	}
	public static void main(String[] args) {
		//filterTest("/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/generator/corpus-for-cipher-generator-with-others/pg2701.txt");
		//filterTest("/Users/doranchak/Downloads/test.txt");
		//filterTest("/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/gutenberg/most-dangerous-game.txt");
		//System.out.println(FileUtil.convert("L'Îuvre "));
		filterTestAll("/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/corpora-for-nickpelling-filter-test");
		//testZodiacLetters();
		//System.out.println(filterTestText("HABWCELTTEEDINLFAGSAIAEUMYNEELOEITROIROTNFSODSHRNS"));
		//testRandom();
	}
}
