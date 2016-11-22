package com.zodiackillerciphers.tests;

import java.util.HashMap;
import java.util.Map;

import com.zodiackillerciphers.ciphers.Ciphers;

/** exploring Traveller1st's ideas about reversible bigrams. 
 * see http://zodiackillersite.forummotion.com/t1310p30-z340-dissected#34284
 *  */
public class TravBigrams {
	
	/** find all repeating vertical bigrams.  the order of symbols of each bigram doesn't matter,
	 * as long as the symbols are the same.  
	 * track distance between repeating bigrams.
	 * number of repeats must be even.  
	 */
	public static void findBigrams(String[] cipherGrid) {
		int W = cipherGrid[0].length();
		int H = cipherGrid.length;
		
		Map<TravBigramBean, Integer> map = new HashMap<TravBigramBean, Integer>(); 
		
		for (int row=0; row<H-1; row++) {
			for (int col=0; col<W; col++) {
				String bigram = "" + cipherGrid[row].charAt(col) + cipherGrid[row+1].charAt(col);
				TravBigramBean bean = new TravBigramBean(bigram, row, col);
				Integer val = map.get(bean);
				if (val == null) {
					val = 0;
				} else {
					for (TravBigramBean key : map.keySet()) {
						//System.out.println("checking " + key.bigram);
						if (key.equals(bean)) {
							key.addLocation(row, col);
							break;
						}
					}
				}
				val++;
				map.put(bean, val);
			}
		}
		
		for (TravBigramBean key : map.keySet()) {
			Integer val = map.get(key);
			//if (val < 2) continue;
			String s = "";
			if (val > 1) s = "s";
			System.out.println("Vertical bigram [" + key.bigram + "] occurs [" + val + "] time" + s + ".");
			key.dump();
		}
		
		System.out.println("=== searching horizontally ===");
		for (int row=0; row<H; row++) {
			for (int col=0; col<W-1; col++) {
				String bigram = "" + cipherGrid[row].charAt(col) + cipherGrid[row].charAt(col+1);
				TravBigramBean bean = new TravBigramBean(bigram, row, col);
				Integer val = map.get(bean);
				String bigramKey = TravBigramBean.keyFor(bigram);
				if (val == null) {
					System.out.println("Bigram [" + bigramKey + "] not found vertically.");
				} else if (val == 1) {
					System.out.println("Bigram [" + bigramKey + "] is found vertically only once"); 
				} else System.out.println("Bigram [" + bigramKey + "] is found vertically " + val + " times.");
			}
		}
		

		
	}
	
	public static void main(String[] args) {
		findBigrams(Ciphers.grid(Ciphers.cipher[1].cipher, 17));
	}
	
}
