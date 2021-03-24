package com.zodiackillerciphers.tests;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.homophones.ProgressiveSearch;
import com.zodiackillerciphers.ngrams.NGramsBean;

public class DanOlson {
	
	public static int[] OLSON_ROWS = new int[] {0,1,2, 10,11,12};
	
	/** Dan olson suggests splicing lines 11-13 right next to lines 1-3.
	 * 
	 * The operation is:  
	 * Select two lines, combine them into line 1.
	 * Select two lines, combine them into line 2.
	 * Select two lines, combine them into line 3.
	 * 
	 * Generalized to include other operations:
	 * 
	 * Select line A for line 1
	 * Select line B for line 1
	 * 
	 * Optionally reverse line A.
	 * Optionally reverse line B.
	 * 
	 * Now we have two line segments to combine.  Operators:
	 *   - Simple: line 1 is line A immediately followed by line B.
	 *   - Spliced: line 1 is line A interleaved with line B.
	 */

	public DanOlsonOperation[] operations;
	public int count = 0;
	
	public Set<Integer> seen = new HashSet<Integer>();
	public DanOlson() {
		operations = new DanOlsonOperation[3];
		for (int i=0; i<3; i++) operations[i] = new DanOlsonOperation();
	}

	public static String lineFrom(int lineNumber) {
		int row = OLSON_ROWS[lineNumber];
		return Ciphers.cipher[0].cipher.substring(17*row,17*row+17);
	}
	
	/** create all possible cipher texts resulting from the above operations */
	public void permute() {
		permute(0);
	}
	
	public boolean unique() {
		Set<Integer> set = new HashSet<Integer>();
		for (DanOlsonOperation op : operations) {
			set.add(op.line1);
			set.add(op.line2);
		}
		return set.size() == 6;
			
	}
	
	public void dump() {
		System.out.println();
		String msg = "";
		for (DanOlsonOperation op : operations) {
			msg += "[" + op.toString() + "] ";
		}
		System.out.println(msg);
		System.out.println();
	}
	
	public void produce() {
		dump();
		
		String cipher = "";
		for (int i=0; i<3; i++) {
			String line = operations[i].produce(); 
			cipher += line;
		}
		
		int n2 = new NGramsBean(2, cipher).numRepeats();
		int n3 = new NGramsBean(3, cipher).numRepeats();
		int n4 = new NGramsBean(4, cipher).numRepeats();
		int n5 = new NGramsBean(5, cipher).numRepeats();
		System.out.println("n2 " + n2 + " n3 " + n3 + " n4 " + n4 + " n5 " + n5 + " " + cipher);
		
		String alphabet = Ciphers.alphabet(cipher); 
		//System.out.println("z340 reference: " + ActiveCycles.sum(z340scores) + " " + Arrays.toString(z340scores));
		ProgressiveSearch.search(cipher, alphabet, true);
		
		seen.add(cipher.hashCode());
	}
	
	public void permute(int index) {
		if (index == operations.length) {
			if (unique()) {
				produce();
			}
			return;
		}
		
		DanOlsonOperation op = operations[index];
		
		for (int a=0; a<6; a++) {
			for (int b=0; b<6; b++) {
				for (boolean rev1 : new Boolean[] {false, true}) {
					for (boolean rev2 : new Boolean[] {false, true}) {
						for (boolean interleave : new Boolean[] {false, true}) {
							op.line1 = a;
							op.line2 = b;
							op.reverse1 = rev1;
							op.reverse2 = rev2;
							op.interleave = interleave;
							
							permute(index+1);
						}
						
					}
					
				}
			}
			
		}
		
	}
	

	public static void test() {
		DanOlson dan = new DanOlson();
		dan.permute();
		System.out.println(dan.count + ", " + dan.seen.size());
		
		/*for (int line=0; line<6; line++) {
			System.out.println(lineFrom(line));
		}*/
	}

	public static void test2() {
		String[] eights = new String[] {
				"012534",
				"013425",
				"014352",
				"015243",
				"041253",
				"042153",
				"045312",
				"045321",
				"102534",
				"103425",
				"104352",
				"105243",
				"120453",
				"123540",
				"124035",
				"125304",
				"210453",
				"213540",
				"214035",
				"215304",
				"250134",
				"251034",
				"253401",
				"253410",
				"340125",
				"341025",
				"342501",
				"342510",
				"351240",
				"352140",
				"354012",
				"354021",
				"401235",
				"402135",
				"403512",
				"403521",
				"430152",
				"431052",
				"435201",
				"435210",
				"520143",
				"521043",
				"524301",
				"524310",
				"530412",
				"530421",
				"531204",
				"532104"		};
		
		String comp = "031425";
		for (String eight : eights) {
			int count = 0;
			for (int i=0; i<eight.length(); i++) {
				if ((""+comp.charAt(i)).equals(""+eight.charAt(i))) {
					count++;
				}
			}
			System.out.println(count + ": " + eight);
		}
	}
	
	public static void main(String[] args) {
		test();
	}
	public static void main2(String[] args) {

	    int input = 15;

	    boolean[] bits = new boolean[7];
	    for (int i = 6; i >= 0; i--) {
	        bits[i] = (input & (1 << i)) != 0;
	    }

	    System.out.println(input + " = " + Arrays.toString(bits));
	}	
}
