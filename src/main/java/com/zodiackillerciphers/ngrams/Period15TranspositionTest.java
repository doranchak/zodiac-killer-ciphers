package com.zodiackillerciphers.ngrams;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


public class Period15TranspositionTest {
	/** Periods.rewrite3 yields max bigrams for period 15 on the mirrored z340.
	 * does some selection of the resulting "chunks" produce more fragments?
	 */
	static String[] chunks = new String[] { "dEB+*5k.L(MVE5FV52c+ztZ",
			"2H+M8|CV@K<Ut*5cZG|TC7z", "G)pclddG+4dl5||.UqLcW<S",
			"TfN:^j*Xz6-z/JNbVM)R)Wk", "LKJy7t-cYAy-RR+4>f|p+dp",
			"1*HBpzOUNyBO+l#2E.B)+kN", "|<z2p+l2_cFKUcy5C^W(cFH",
			"k.#KSMF;+B<MF<Sf9pl/C|D", "PYLR/9^%OF7TB29^4OFT-+M",
			"VW)+k_Rq#2pb&R6N:(+H*;>", "^D(+4(8KjROp+8zF*K<SBK",
			"l%WO&Dp+fZ+B.;+G1BCOO|", "pOGp+2|5J+JYM(+lXz6PYA",
			">#Z3P>L#2b^D4ct+B31c_8", "R(UVFFz9G++|TB4-y.LWBO" };
	
	static String reference;
	static {
		for (String chunk : chunks) reference += chunk;
	}
	
	public static void findGoodChunkCombinations5() {
		
		int[] selection = new int[5];
		
		int count = 0;

		// testing 360,360 ways to select 5 chunks
		for (selection[0]=0; selection[0]<chunks.length; selection[0]++) {
			for (selection[1]=0; selection[1]<chunks.length; selection[1]++) {
				if (dupe(selection, 1)) continue;
				for (selection[2]=0; selection[2]<chunks.length; selection[2]++) {
					if (dupe(selection, 2)) continue;
					for (selection[3]=0; selection[3]<chunks.length; selection[3]++) {
						if (dupe(selection, 3)) continue;
						for (selection[4]=0; selection[4]<chunks.length; selection[4]++) {
							if (dupe(selection, 4)) continue;
								
								count++;
								
								String cipher = "";
								for (int i=0; i<selection.length; i++) cipher += chunks[selection[i]];

								float m1 = RepeatingFragmentsFaster.measure(cipher, false, false);
								float m2 = RepeatingFragmentsFaster.measure(cipher, true, false);
								System.out.println(m1 + "	" + m2 + "	" + Arrays.toString(selection) + "	" + cipher);
								
								if (count % 1000 == 0) {
									System.out.println("count " + count + " " + new Date());
								}
							
						}
						
					}
					
				}
				
			}
			
		}
	}
	
	public static void findGoodChunkCombinations6() {
		
		double ref_ioc = RepeatingFragmentsFaster.fragmentIOC(reference, 6, false);
		System.out.println("ref_ioc " + ref_ioc);

		int[] selection = new int[15];
		
		int count = 0;

		// testing 3,603,600 ways to select 6 chunks
		for (selection[0]=0; selection[0]<chunks.length; selection[0]++) {
			for (selection[1]=0; selection[1]<chunks.length; selection[1]++) {
				if (dupe(selection, 1)) continue;
				for (selection[2]=0; selection[2]<chunks.length; selection[2]++) {
					if (dupe(selection, 2)) continue;
					for (selection[3]=0; selection[3]<chunks.length; selection[3]++) {
						if (dupe(selection, 3)) continue;
						for (selection[4]=0; selection[4]<chunks.length; selection[4]++) {
							if (dupe(selection, 4)) continue;
							for (selection[5]=0; selection[5]<chunks.length; selection[5]++) {
								if (dupe(selection, 5)) continue;
								
								count++;
								
								// first part of cipher is our selection
								// second part is just the unselected chunks in original order.
								String cipher = makeCipher(selection, 6);

								// print out results for any that score better
								double ioc = RepeatingFragmentsFaster.fragmentIOC(cipher, 6, false);
								if (ioc > ref_ioc) { 
									System.out.println(ioc + "	" + Arrays.toString(selection) + "	" + cipher);
								}
								
								if (count % 1000 == 0) {
									System.out.println("count " + count + " " + new Date());
								}
							}
							
						}
						
					}
					
				}
				
			}
			
		}
	}
	
	// ensure selection at given index does not duplicate a selection at a lower index.
	public static boolean dupe(int[] selection, int index) {
		for (int i=0; i<index; i++) if (selection[i] == selection[index]) return true;
		return false;
	}
	
	// make cipher based on selections from [0,index-1].  fill rest with unselected chunks.    
	public static String makeCipher(int[] selection, int index) {
		String cipher = "";
		Set<Integer> set = new HashSet<Integer>();
		for (int i=0; i<index; i++) {
			set.add(selection[i]);
			cipher += chunks[selection[i]];
		}

		int current = 0;
		for (int i=index; i<selection.length; i++) {
			while (set.contains(current)) current++;
			cipher += chunks[current];
			selection[i] = current;
			set.add(current);
		}
		if (cipher.length() != 340) {
			System.out.println("wtf " + cipher.length());
			System.exit(-1);
		}
		return cipher;
	}

	public static void test6(int[] selection) {
		String cipher = makeCipher(selection, 6);
		float m1 = RepeatingFragmentsFaster.measure(cipher, false, false);
		float m2 = RepeatingFragmentsFaster.measure(cipher, true, false);
		System.out.println(m1 + "	" + m2 + "	" + Arrays.toString(selection) + "	" + cipher);
		
	}
	
	public static void main(String[] args) {
		findGoodChunkCombinations6();
		//test6(new int[] {6, 8, 10, 12, 14, 1, 0, 2, 4, 5, 7, 3, 9, 11, 13});
	}
}
