package com.zodiackillerciphers.annealing.skydrop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.zodiackillerciphers.dictionary.WordFrequencies;
import com.zodiackillerciphers.io.FileUtil;
import com.zodiackillerciphers.ngrams.AZDecrypt;
import com.zodiackillerciphers.skydrop.Matrix;

public class Solver {
	public static boolean TOP_ONLY = false;
	public static boolean EXCLUDE_KNOWN = false;
	public static Thread[] threads;
	public static void solve(int numThreads) {
		AZDecrypt.init("/Users/doranchak/projects/zodiac/github/azdecrypt/AZdecrypt/N-grams/5-grams_english_jarlve_reddit_v1912.gz", 5, false);
		List<String> lines = FileUtil.loadFrom("/Users/doranchak/projects/zodiac/github/project-skydrop/matrix3.txt");
		Set<String> known_twelve = new HashSet<String>();
		WordFrequencies.init(10000);
		known_twelve.add("century");
		known_twelve.add("throw");
		known_twelve.add("task");
		known_twelve.add("corn");
		known_twelve.add("library");
		known_twelve.add("liar");
		known_twelve.add("artefact");
		known_twelve.add("seat");
		known_twelve.add("general");
		known_twelve.add("daring");
		known_twelve.add("reduce");
		known_twelve.add("unlock");		
		SkydropSolution.dictionary = new ArrayList<String>();
		SkydropSolution.plaintextMap = new HashMap<String, String>();
		for (String line : lines) {
			String[] split = line.split(" ");
			if (EXCLUDE_KNOWN && known_twelve.contains(split[0])) continue; // exclude known solution
			SkydropSolution.dictionary.add(split[0]);
			SkydropSolution.plaintextMap.put(split[0], split[1]);
		}

		// hard-code to top-scoring words (3-gram wise)
		if (TOP_ONLY) {
			SkydropSolution.dictionary = new ArrayList<String>();
			String[] top = {"advance","analyst","army","base","before","benefit","brave","carry","come","critic","defense","devote","diagram","diamond","disagree","embody","final","fish","front","fun","gravity","guard","indoor","inflict","ladder","leaf","letter","match","material","merry","orbit","pelican","pottery","rent","reunion","road","slab","slice","ten","toward","turkey","usual","warrior","wheat","wine","wolf"};
			for (String word : top) SkydropSolution.dictionary.add(word);
			}

		Matrix.init();
		threads = new SkydropThread[numThreads];
		for (int i=0; i<numThreads; i++) {
			threads[i] = new SkydropThread(100, 1000000, i);
			threads[i].start();
			System.out.println("Kicked off thread #" + i);
		}
	}
	public static void main(String[] args) {
		solve(8);
	}
}
