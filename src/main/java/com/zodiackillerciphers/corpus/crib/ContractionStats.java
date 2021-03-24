package com.zodiackillerciphers.corpus.crib;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.corpus.CorpusBase;
import com.zodiackillerciphers.dictionary.WordFrequencies;
import com.zodiackillerciphers.io.FileUtil;
import com.zodiackillerciphers.lucene.Stats;
import com.zodiackillerciphers.tests.unicity.PlaintextBean;

/** generate stats for words that are contractions.  came up in facebook ACA discussion group. */
public class ContractionStats extends CorpusBase {

	public static int SAMPLE_TRIALS = 100000;
	
	public static void search() {
		KEEP_APOSTROPHE = true;
		SHOW_INFO = false;
		initSources();
		Map<String, Long> counts = new HashMap<String, Long>();
		
		long hits = 0;
		long samples = 0;
		
		long interval = 0;
		while (true) {
			// pick random corpus
			randomSource();
			
			for (String word : tokens) {
				if (word.contains("'")) {
					hits++;
					interval++;
//					System.out.println(word);
					Long count = counts.get(word);
					if (count == null) count = 0l;
					count++;
					counts.put(word,  count);
					
//					if (word.length() == 9) {
//						if (word.charAt(7) == '\'' && word.charAt(1) == word.charAt(3)) {
//							Long count = counts.get(word);
//							if (count == null) count = 0l;
//							count++;
//							counts.put(word,  count);
//						}
//					}
				}
			}
			samples++;
			if (interval > 10000000) { 
				System.out.println(hits + " / " + samples + ", " + counts.size());
				counts = sortByValue(counts);
				counts = sortByValue(cull(counts, 50000));
				System.out.println(counts);
				interval = 0;
			}
		}
	}
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Entry.comparingByValue());
        Collections.reverse(list);

        Map<K, V> result = new LinkedHashMap<>();
        for (Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }
	
	public static Map<String, Long> cull(Map<String, Long> map, int maxSize) {
		int count = 0;
		Map<String, Long> newMap = new HashMap<String, Long>();
		for (String key : map.keySet()) {
			System.out.println(key);
			newMap.put(key,  map.get(key));
			count++;
			if (count > maxSize) {
				return newMap;
			}
		}
		return newMap;
		
	}
	
	
	
	public static void main(String[] args) {
		search();
	}
	
}
