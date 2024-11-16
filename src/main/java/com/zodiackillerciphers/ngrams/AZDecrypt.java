package com.zodiackillerciphers.ngrams;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import com.zodiackillerciphers.lucene.Stats;

/** Attempts to reproduce some AZDecrypt functionality */
public class AZDecrypt {
	
	public static Map<String, Integer> ngrams;
	public static int n;
	public static void init(String ngramsPath, int ngramsLength) {
		init(ngramsPath, ngramsLength, false);
	}
	public static void init(String ngramsPath, int ngramsLength, boolean spaces) {
		System.out.println("Loading ngram stats from " + ngramsPath);
		ngrams = loadNgrams(ngramsPath, ngramsLength, spaces);
		n = ngramsLength;
	}

	static char nextChar(char ch, boolean spaces) {
		if (spaces && ch == 'Z') return ' ';
		else return (char) (ch + 1);
	}
	static Map<String, Integer> loadNgrams(String filePath, int n, boolean spaces) {
		int count = 0;
		Map<String, Integer> counts = new HashMap<String, Integer>(); 
		StringBuffer key = new StringBuffer();
		for (int i=0; i<n; i++) key.append('A');
				
		try (FileInputStream fis = new FileInputStream(filePath); GZIPInputStream gis = new GZIPInputStream(fis)) {
			int byteData;
			while ((byteData = gis.read()) != -1) {
				int intValue = byteData & 0xFF; // Convert byte to unsigned int
				counts.put(key.toString(), intValue);
				// System.out.println(intValue + " " + key.toString());
				int pos = n-1;
				while (true) {
					char ch = key.charAt(pos);
					ch = nextChar(ch, spaces);
					key.setCharAt(pos, ch);
					if ((spaces && ch == ' ') || (ch >= 'A' && ch <= 'Z')) break;
					key.setCharAt(pos, 'A');
					pos--;
					if (pos < 0) break;					
				}
				count++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Loaded " + count + " " + n + "-grams.");
		// for (String ngram: counts.keySet())
		// 	System.out.println(ngram + " " + counts.get(ngram));
		// System.out.println(counts.size());
		return counts;
	}
	
	public static float score(StringBuffer val) {
		return score(val, true);
	}
	public static float score(StringBuffer val, boolean checkEntropy) {
		return score(val.toString(),checkEntropy, true);
	}
	public static float score(StringBuffer val, boolean checkEntropy, boolean normalize) {
		return score(val.toString(),checkEntropy, normalize);
	}
	public static float score(String val) {
		return score(val, true);
	}
	public static float score(String val, boolean checkEntropy) {
		return score(val, true, true);
	}
	public static float score(String val, boolean checkEntropy, boolean normalize) {
		String ngram;
		int num = val.length()-n+1;
		float score = 0;
		for (int i=0; i<val.length()-n+1; i++) {
			ngram = val.substring(i, i+n);
			Integer freq = ngrams.get(ngram);
			// if (freq != null) 
			score += freq;
		}
		if (normalize) score /= num;
		if (checkEntropy) score *= Stats.entropy(val);
		return score;
	}

	public static void main(String[] args) {
		init("/Users/doranchak/projects/zodiac/github/azdecrypt/AZdecrypt/N-grams/5-grams_english_jarlve_reddit_v1912.gz", 5);
	}

}
