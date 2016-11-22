package com.zodiackillerciphers.lucene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NGramsWithGaps {
	
	/** compute n-gram statistics, including patterns with gaps such as A?B, using the given corpus text */
	static void loadFrom(String file) {
		System.out.println("loading from [" + file + "]...");

		StringBuffer all = new StringBuffer();
		BufferedReader input = null;
		int counter = 0;
		try {
			input = new BufferedReader(new FileReader(new File(file)));
			String line = null; // not declared within while loop
			while ((line = input.readLine()) != null) {
				all.append(line);
				counter++;
			}
			System.out.println("read " + counter + " lines.");
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
		
		reversible(all); 
		count(all);
	}
	
	/* look for reversible 3-grams, such as in the pivots of the 340 */
	public static void reversible(StringBuffer sb) {
		System.out.println("Searching for 3-gram patterns: ");
		for (int i = 0; i < sb.length() - 6; i++) {
			if (sb.charAt(i + 0) == sb.charAt(i + 6)
					&& sb.charAt(i + 1) == sb.charAt(i + 5)
					&& sb.charAt(i + 2) == sb.charAt(i + 4)) {
				
				
				String sub1 = sb.substring(i,i+3);
				String sub2 = sb.substring(i+4,i+7);
				float val1 = NGrams.valueFor(sub1);
				float val2 = NGrams.valueFor(sub2);
				
				System.out.println((val1+val2) + ", Reversed match at " + i + ": "
					 + sub1 + "," + sub2 + ": "
						+ sb.substring(i, i + 7) + " (" + sb.substring(i-10, i+40));
			} /*else if (sb.charAt(i + 0) == sb.charAt(i + 4)
					&& sb.charAt(i + 1) == sb.charAt(i + 5)
					&& sb.charAt(i + 2) == sb.charAt(i + 6)) {
				System.out.println("Nonreversed match at " + i + ": "
						+ sb.substring(i, i + 7) + " (" + sb.substring(i, i+40));

			}*/

		}
		
		// search for two adjacent doubled letters 
		for (int i = 0; i < sb.length() - 4; i++) {
			String sub1 = sb.substring(i,i+2);
			String sub2 = sb.substring(i+2,i+4);
			
			if (sub1.charAt(0) == sub1.charAt(1) && sub2.charAt(0) == sub2.charAt(1)) {
				System.out.println("Doubled match at " + i + ": "
						 + sub1 + "," + sub2 + ": "
							+ sb.substring(i, i + 4) + " (" + sb.substring(i-10, i+40) + ")");
			}
		}

	}	
	
	public static void count(StringBuffer text) {
		Map<String, Integer> map = new HashMap<String, Integer>(); // ngram counts
		Map<Integer, Integer> mapN = new HashMap<Integer, Integer>(); // totals for each n
		Map<Integer, Integer> mapM = new HashMap<Integer, Integer>(); // totals for each n (for gaps)
		
		for (int i=0; i<text.length(); i++) {
			String key; Integer val;
			// normal ngrams
			for (int n=1; n<=6; n++) {
				if (i+n<text.length()) {
					key = text.substring(i,i+n);
					inc(map, key);
					inc(mapN, n);
				}
			}
			// ngrams with gaps
			for (int n=2; n<=5; n++) {
				if (i+n<text.length()) {
					key = text.substring(i,i+1);
					for (int j=0; j<n-1; j++) key += "?";
					key += text.substring(i+n,i+n+1);
					//System.out.println(key);
					inc(map, key);
					inc(mapM, n);
				}
			}
		}
		
		for (String key : map.keySet()) {
			String line = "";
			line += key + "," + map.get(key) + ",";
			if (key.contains("?")) {
				line += ((float)map.get(key))/mapM.get(key.length()-1);
			} else {
				line += ((float)map.get(key))/mapN.get(key.length());
			}
			System.out.println(line);
		}
		
		for (Integer n : mapN.keySet()) {
			System.out.println("Total " + n + "-grams: " + mapN.get(n));
		}
			
		for (Integer m : mapM.keySet()) {
			System.out.println("Total " + m + "-grams with gaps: " + mapM.get(m));
		}
	}
	
	public static void inc(Map<String, Integer> map, String key) {
		Integer val = map.get(key);
		if (val == null) val = 0;
		val++;
		map.put(key, val);
	}
	
	public static void inc(Map<Integer, Integer> map, Integer key) {
		Integer val = map.get(key);
		if (val == null) val = 0;
		val++;
		map.put(key, val);
	}
	public static void main(String[] args) {
		loadFrom("/Users/doranchak/projects/work/java/zodiac/letters/mobydick.txt");
	}

}
