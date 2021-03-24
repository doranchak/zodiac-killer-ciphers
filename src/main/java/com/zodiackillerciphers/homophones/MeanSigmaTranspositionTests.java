package com.zodiackillerciphers.homophones;

import com.zodiackillerciphers.ciphers.Ciphers;

public class MeanSigmaTranspositionTests {
	// try all SwapLinear operations
	public static void swapLinear() {
		String cipher = Ciphers.Z340;
		int total = 0;
		for (int pos1=0; pos1<cipher.length(); pos1++) {
			for (int pos2=pos1+1; pos2<cipher.length(); pos2++) {
				// pos1 is always before pos2
				for (int L=17; L<=cipher.length(); L++) {
					// check if pos1 overlaps pos2
					if (pos1+L-1 >= pos2) break;
					// check if pos2 extends beyond end of cipher
					if (pos2+L-1 >= cipher.length()-1) break;
					total++;
				}
			}
		}
		System.out.println(total);
	}
	
	public static void main(String[] args) {
		swapLinear();
	}
}
