package com.zodiackillerciphers.corpus;

import java.util.ArrayList;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.ciphers.Cipher;
import com.zodiackillerciphers.ciphers.Ciphers;


/** a snippet is a section of highly-constrained cipher text.  we are looking for corpus snippets that fit into the constraints. */
public class Snippet {
	
	/** generate candidate snippets from a given cipher text */
	public static void analyzeSnippets(String cipher, String solution) {
		Map<Character, Integer> counts = Ciphers.countMap(cipher.toString());
		//for (int L=2; L<20; L++) {
		int L = 20;	
			List<Score> scores = new ArrayList<Score>();
			for (int i=0; i<cipher.length()-L+1; i++) {
				String sub = cipher.substring(i, i+L);
				Score score = new Score();
				score.scores = score(sub, counts, cipher.length());
				score.substring = sub;
				if (solution != null) score.solution = solution.substring(i,i+L); 
				score.startPos = i;
				//System.out.println(L+","+sub+","+scores[0]+","+scores[1]+","+scores[2]);
				scores.add(score);
			}
			List<Score> front;
			//List<Score> scoresClone;
			for (int i=1; i<11; i++) {
				System.out.println("Front " +i+":");
				//scoresClone = new ArrayList<Score>();
				//scoresClone.addAll(scores);
				front = Pareto.frontFrom(scores);
				Pareto.dump(front);
			}
		//}
	}
	
	/** score each row */
	public static void analyzeRows(String cipher, String solution) {
		Map<Character, Integer> counts = Ciphers.countMap(cipher.toString());
		//for (int L=2; L<20; L++) {
		int W = 17;
		for (int i=0; i<cipher.length(); i+=W) {
			String row = cipher.substring(i,i+W);
			Score score = new Score();
			score.scores = score(row, counts, cipher.length());
			score.substring = row;
			if (solution != null) score.solution = solution.substring(i,i+W); 
			score.startPos = i;
			System.out.println(score);
			
		}
	}
	
	/** generate pareto optimal set from the given list of scores */
	public static void dumpPareto(List<float[]> scores) {
		List<float[]> front = new ArrayList<float[]>();
		
		for (float[] score : scores) {
			
			if (front.isEmpty()) front.add(score);
			else {
				// possibilities:
				// 1) the new score beats an existing score.  replace the existing score.
				// 2) the new score equals an existing score 
				for (int i=front.size()-1; i>=0; i--) {
				}					
			}
			
		}
	}
	
	
	/** compute scores that reflect how constrained this substring is.  first measurement
	 * is the number of unique symbols (lower count means more constrained).
	 * second measurement is the product of probabilities for each symbol (lower values mean rarer symbols)
	 * 
	 * counts = symbol counts
	 * len = length of entire cipher text
	 */
	public static float[] score(String sub, Map<Character, Integer> counts, int len) {
		float[] scores = new float[2];
		scores[0] = 0;
		scores[1] = 1f;
		
		Set<Character> set = new HashSet<Character>();
		for (int i=0; i<sub.length(); i++) {
			char ch = sub.charAt(i);
			set.add(ch);
			scores[1] *= ((float)1)/counts.get(ch);
		}
		
		scores[0] = set.size();
		//scores[2] = scores[0]*scores[1];
		return scores;
	}
	
	public static void main(String[] args) {
		Cipher c = Ciphers.cipher[Integer.valueOf(args[0])];
		analyzeSnippets(c.cipher, c.solution);
		//analyzeRows(Ciphers.cipher[1].cipher, null);
	}
}
