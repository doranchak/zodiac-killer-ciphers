package com.zodiackillerciphers.old;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.transform.CipherTransformations;
import com.zodiackillerciphers.transform.TransformationBase;

/** Brute force search for cipher transformations that results in the highest repeated fragment scores */
public class FragmentTransformationSearch {
	
	/** the unmodified cipher's fragment score is used as a reference */
	static FragmentTransformationSearchResult referenceResult;
	
	static int MAX_WILDCARDS = 10;
	
	static int count = 0;
	
	public static void search(String cipher, int width, int depth) {
		Map<String, String> seen = new HashMap<String, String>(); // list of best results, forward and reversed, so we can skip redundant transformations.
		List<FragmentTransformationSearchResult> results = new ArrayList<FragmentTransformationSearchResult>();
		List<StringBuffer> list = TransformationBase.toList(cipher, width);
		
		referenceResult = new FragmentTransformationSearchResult();
		referenceResult.score = Fragments.search(cipher, "0", MAX_WILDCARDS, true);
		referenceResult.cipher = new StringBuffer(cipher);
		referenceResult.name = "0";
		
		seen.put(referenceResult.cipher.toString(), "nothing");
		seen.put(new StringBuffer(referenceResult.cipher).reverse().toString(), "nothing");
		
		System.out.println(referenceResult);
		StringBuffer name = new StringBuffer();
		search(list, "", seen, results, depth);
	}
	public static void search(List<StringBuffer> list, String name, Map<String, String> seen, List<FragmentTransformationSearchResult> results, int depth) {
		if (depth == 0) {
			count++;
			if (count % 1000 == 0) System.out.println("Count: " + count);
			// don't bother computing if we've already kept this as one of the better results 
			StringBuffer cipher = TransformationBase.fromList(list);
			String seenName = seen.get(cipher.toString());
			if (seenName == null) seenName = seen.get(new StringBuffer(cipher).reverse().toString());
			
			if (seenName != null) {
				System.out.println("Current [" + name + "] already seen as [" + seenName + "].");
				return;
			}
			// we are done with all transformations, so perform the computations.
			FragmentScore score = Fragments.search(cipher.toString(), name, MAX_WILDCARDS, true);
			if (score.equalOrBetterThan(referenceResult.score)) {
				FragmentTransformationSearchResult result = new FragmentTransformationSearchResult();
				result.cipher = cipher;
				result.name = name;
				result.score = score;
				results.add(result);
				
				seen.put(cipher.toString(), name);
				seen.put(new StringBuffer(cipher).reverse().toString(), name);
				
				System.out.println("Found " + result);
			}
			
			return;
		}

		/* 1) Do nothing */
		search(TransformationBase.copy(list), name + " nothing", seen, results, depth-1);
		/* 2) Flip horizontally */
		search(CipherTransformations.flipHorizontal(list, 1), name + " flip", seen, results, depth-1);
		/* 3) Rotate */
		for (int deg = 90; deg <= 270; deg+=90) 
			search(CipherTransformations.rotate(list, deg), name + " rot-" + deg, seen, results, depth-1);
		/* 4) Snake */
		//search(CipherTransformations.snake(list), name + " snake", seen, results, depth-1);
		/* 5) Diagonal */
		for (int d = 0; d<4; d++) {
			//search(CipherTransformations.diagonal(list, d), name + " diagonal-" + d, seen, results, depth-1);
		}
		/* 6) Olson */
		int height = TransformationBase.height(list);
		for (int row=1; row<height; row++) {
			//search(CipherTransformations.olson(list, row, false), name + " olson-" + row + "-false", seen, results, depth-1);			
			//search(CipherTransformations.olson(list, row, true), name + " olson-" + row + "-true", seen, results, depth-1);			
		}
		
	}
	
	public static void main(String[] args) {
		search(Ciphers.cipher[0].cipher.toString(), 17, 3);
	}
}
