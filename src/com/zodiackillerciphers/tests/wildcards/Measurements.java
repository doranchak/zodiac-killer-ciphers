package com.zodiackillerciphers.tests.wildcards;

import java.util.List;
import java.util.Map;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.cosine.CosineSimilarity;
import com.zodiackillerciphers.cosine.CosineSimilarityResult;
import com.zodiackillerciphers.homophones.HomophonesNew;
import com.zodiackillerciphers.homophones.HomophonesResultBean;
import com.zodiackillerciphers.ngrams.NGramResultBean;
import com.zodiackillerciphers.ngrams.NGrams;
import com.zodiackillerciphers.tests.jarlve.JarlveMeasurements;

public class Measurements {
	// 1) mean of probabilities of 25 least probable repeating fragments  (minimize)
	// 2) mean of top 25 cosine similarities   (maximize)
	// 3) homophone cycles, length 2.  mean of probabilities of 25 least probable sequences.   (minimize)
	// 4) Jarlve's "non-repeats" measurement   (maximize)
	
	public static float[] measure(String ciphertext) {
		Map<Character, Integer> counts = Ciphers.countMap(ciphertext);
		float[] m = new float[4];
		
		
		/****************************************************************************************/
		
		List<NGramResultBean> ngrams = NGrams.measure2(ciphertext, true);
		m[0] = 0;
		for (int i=0; i<25; i++) {
			NGramResultBean bean = ngrams.get(i);
			m[0] += bean.getProbability();
		}
		m[0] /= 25;
		
		/****************************************************************************************/
		
		CosineSimilarity c = new CosineSimilarity();
		List<CosineSimilarityResult> results = c.compute(ciphertext, true);
		m[1] = 1 - c.top25(results)[2];
		
		/****************************************************************************************/
		
		m[2] = measureHomophones(ciphertext);
		
		/****************************************************************************************/
		
		m[3] = 10000 - JarlveMeasurements.nonrepeat(ciphertext);
		
		/****************************************************************************************/

		return m;
	}
	
	public static float measureHomophones(String ciphertext) {
		int L = 3;
		String a = Ciphers.alphabet(ciphertext);
		String[] alphabet = new String[a.length()];
		for (int i=0; i<a.length(); i++) alphabet[i] = ""+a.charAt(i);
		List<HomophonesResultBean> beans = HomophonesNew.search(ciphertext, alphabet, L);
		float score = 0;
		for (int i=0; i<25; i++) {
			HomophonesResultBean bean = beans.get(i);
			score += bean.getProbability();
		}
		score /= 25;
		return score;
	}
	
	public static float measureHomophonesMinProbPerSymbol(String ciphertext) {
		int L = 3;
		String a = Ciphers.alphabet(ciphertext);
		String[] alphabet = new String[a.length()];
		for (int i = 0; i < a.length(); i++)
			alphabet[i] = "" + a.charAt(i);
		List<HomophonesResultBean> beans = HomophonesNew.search(ciphertext,
				alphabet, L);
		Map<Character, Float> map = HomophonesNew.lowestProbsPerSymbol(beans);
		float sum = 0;
		for (Character key : map.keySet())
			sum += map.get(key);
		return sum;
	}
	
	public static void main(String[] args) {
		float[] f = measure(Ciphers.cipher[0].cipher);
		for (int i=0; i<f.length; i++) 
			System.out.println(f[i]);
		
	}
}
