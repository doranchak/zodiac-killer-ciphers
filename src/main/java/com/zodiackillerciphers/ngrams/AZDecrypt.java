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
		System.out.println("Loading ngram stats from " + ngramsPath);
		ngrams = loadNgrams(ngramsPath, ngramsLength);
		n = ngramsLength;
	}

	static Map<String, Integer> loadNgrams(String filePath, int n) {
		int count = 0;
		Map<String, Integer> counts = new HashMap<String, Integer>(); 
		StringBuffer key = new StringBuffer();
		for (int i=0; i<n; i++) key.append('A');
				
		try (FileInputStream fis = new FileInputStream(filePath); GZIPInputStream gis = new GZIPInputStream(fis)) {
			int byteData;
			while ((byteData = gis.read()) != -1) {
				int intValue = byteData & 0xFF; // Convert byte to unsigned int
				counts.put(key.toString(), intValue);
				int pos = n-1;
				while (true) {
					key.setCharAt(pos, (char) (key.charAt(pos) + 1));
					if (key.charAt(pos) >= 'A' && key.charAt(pos) <= 'Z') break;
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
//		for (String ngram: counts.keySet())
//			System.out.println(ngram + " " + counts.get(ngram));
//		System.out.println(counts.size());
		return counts;
	}
	
	public static float score(String val) {
		String ngram;
		int num = val.length()-n+1;
		float score = 0;
		for (int i=0; i<val.length()-n+1; i++) {
			ngram = val.substring(i, i+n);
			score += ngrams.get(ngram);
		}
		score /= num;
		score *= Stats.entropy(val);
		return score;
	}

	public static void main(String[] args) {
		init("/Users/doranchak/projects/zodiac/github/azdecrypt/AZdecrypt/N-grams/5-grams_english_jarlve_reddit_v1912.gz", 5);
	}

}
