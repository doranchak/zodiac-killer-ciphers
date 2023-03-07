package com.zodiackillerciphers.ciphers.algorithms.columnar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.ciphers.FatesUnwind;
import com.zodiackillerciphers.ngrams.NGramsBean;
import com.zodiackillerciphers.ngrams.Periods;
import com.zodiackillerciphers.ngrams.RepeatingFragmentsFaster;
import com.zodiackillerciphers.transform.TransformationBase;

/** thanks to http://rumkin.com/tools/cipher/js/coltrans.js
 *  (I don't think this implementation accounts for padding )
 **/
public class ColumnarTranspositionRumkin {

	/** columns are indexed starting at 1. */
	public static String encode(String plaintext, int[] columns) {
		// Performs the actual transposition.  Notice how simple the code looks.
		//function ColTrans_Encode(t, NumberList)
		StringBuffer[] s = new StringBuffer[columns.length];
		int[] back = new int[columns.length];
		StringBuffer out = new StringBuffer();
		for (int i=0; i<columns.length; i++) {
			s[i] = new StringBuffer();
			back[columns[i]-1] = i;
		}
		for (int i=0; i<plaintext.length(); i++) {
			
			s[i%columns.length].append(plaintext.charAt(i));
		}
		for (int i=0; i<columns.length; i++) {
			out.append(s[back[i]]);
		}
		return out.toString();
	}
	
	/** columns are indexed starting at 1 */
	public static String encodeOLD(String plaintext, int[] columns, boolean regular) {
		if (!regular) throw new IllegalArgumentException("Not yet supported");
		//if (regular && plaintext.length() % columns.length != 0) throw new RuntimeException("Text length must be multiple of column count");
		StringBuffer pt = new StringBuffer(plaintext);
		int rows = plaintext.length()/columns.length;
		int rem = plaintext.length() % columns.length;
		if (rem > 0) {
			rows++;
			if (regular) {
				for (int i=0; i<columns.length-rem; i++) // pad leftover
					pt.append((char)(Math.random()*26+64));
			}
		}
		
		StringBuffer[] grid = new StringBuffer[rows];
		
		for (int row=0; row<rows; row++) {
			grid[row] = new StringBuffer(pt.substring(row*columns.length, (row+1)*columns.length));
			//System.out.println(row+": " + grid[row]);
		}
		
		StringBuffer ct = new StringBuffer();
		int[] cols = new int[columns.length];
		for (int i=0; i<columns.length; i++) cols[columns[i]-1]=i;
		for (int c=0; c<cols.length; c++) {
			for (int row=0; row<rows; row++) {
				ct.append(grid[row].charAt(cols[c]));
			}
		}
		return ct.toString();
	}
	
	public static String decode(String ciphertext, int[] columns) {
		int[] num = new int[columns.length];
		int[] back = new int[columns.length];
		StringBuffer[] s = new StringBuffer[columns.length];
		int i, j;
		StringBuffer out = new StringBuffer();
		int minNum;

		minNum = ciphertext.length() / columns.length;

		for (i = 0; i < num.length; i++) {
			num[i] = minNum;
			back[columns[i] - 1] = i;
		}

		j = minNum * columns.length;
		i = 0;

		while (j < ciphertext.length()) {
			num[columns[i] - 1]++;
			i++;
			j++;
		}

		for (i = 0; i < columns.length; i++) {
			s[back[i]] = new StringBuffer(ciphertext.substring(0, num[i]));
			ciphertext = ciphertext.substring(num[i], ciphertext.length());
		}

		for (i = 0; i < minNum + 1; i++) {
			for (j = 0; j < s.length; j++) {
				if (s[j].length() > i) {
					out.append(s[j].charAt(i));
				}
			}
		}
		return out.toString();

	}
	
	public static String decodeOLD(String ciphertext, int[] columns, boolean regular) {
		if (ciphertext.length() % columns.length != 0) {
			throw new IllegalArgumentException("Ciphertext must be a multiple of " + columns.length);
		}
		if (!regular) throw new IllegalArgumentException("Not yet supported");
		int[] cols = new int[columns.length];
		for (int i=0; i<columns.length; i++) cols[columns[i]-1]=i;
		int rows = ciphertext.length()/columns.length;
		int col = 0; int row=0;
		StringBuffer pt = new StringBuffer(ciphertext);
		for (int i=0; i<ciphertext.length(); i++) {
			row = i % rows;
			if (i%rows==0) {
				col = cols[i/rows];
			}
			//System.out.println(i+","+row+","+col+","+(row*columns.length + col));
			pt.setCharAt(row*columns.length + col, ciphertext.charAt(i));
		}
		return pt.toString();
	}
	
	/** produce a list of column orderings derived from the given key phrase. 
	 * 
	 * @param keyPhrase the key phrase
	 * @param dupesForward if true, dupes are numbered in ascending order.  else descending.
	 * @return
	 */
	public static int[] columnarKeyFor(String keyPhrase, boolean dupesForward) {

		char[] charray = keyPhrase.toCharArray();

		// Values is an array of numbers. Convert to an array of numbers that
		// start from 1 and progress up without duplicates.
		int[] values2 = new int[keyPhrase.length()];

		for (int loop = 0; loop < values2.length; loop++) {
			int lowestIdx = -1;
			for (int i = 0; i < values2.length; i++) {
				if (values2[i] == 0) {
					if (lowestIdx == -1) {
						lowestIdx = i;
					} else {
						char a = keyPhrase.charAt(lowestIdx);
						char b = keyPhrase.charAt(i);
						if (a > b || (a == b && !dupesForward)) {
							lowestIdx = i;
						}
					}
				}
			}
			values2[lowestIdx] = loop + 1;
		}
		return values2;
	}
	
	/** permute all possible keys of length L.  columns are numbered starting at 1. */
	public static void permuteKeys(int L) {
		int[] columns = new int[L];
		boolean[] numbers = new boolean[L+1]; // mark numbers that are in use.  index starts at 1.
		for (int i=1; i<=L; i++) numbers[i] = false; 
		permuteKeys(columns, numbers, 0);
	}
	
	/**
	 * 
	 * @param columns the current key
	 * @param numbers marks which numbers are used
	 * @param index the current position in the key array
	 */
	public static void permuteKeys(int[] columns, boolean[] numbers, int index) {
		boolean stop = true;
		for (int i=1; i<numbers.length; i++) {
			if (!numbers[i]) {
				stop = false;
				break;
			}
		}
		if (stop) { // stop condition
			permuteKeysAction(columns);
			return;
		}
		
		for (int i=1; i<numbers.length; i++) {
			if (!numbers[i]) {
				numbers[i] = true;
				columns[index] = i;
				permuteKeys(columns, numbers, index+1);
				numbers[i] = false;
			}
		}
	}
	
	public static void permuteKeysActionOLD(int[] columns) {
		String ct = "Mt#/TU2|p4U>|G_U|UJS)(8;+A87SZp3R+pYX@HH&c|5FBcOkJf6(7U3#U&X3*1U9p|Z7&E#7U(OcX3RRJ|*B#U;PHbZ3(#HdVA;<3ZAzNU*MO^f|fC8Sk7cREkcKGUTfd|R#U/3_k8Vk#bU7VTFJ^z3%-p<Z|*-)ULcLp;+V-pqdY>|^7)7ZBE&O6l/.Hc^%f||5F9k4#PK3+:jHY-*lK#UT1UUZD8UJfD>U@Jq8&Xz@6A&&j2zF3@MD@f1KNBzT9XyzbkzZ277/K&bYbB;^X.WUUU#@HER&-+6GAHcWdFBc6P%HH_/bb.cVJ54lqkCUyqOGG1D;U:).AST/H4Z";
		String decoded = decode(ct, columns);
		double ioc = RepeatingFragmentsFaster.fragmentIOC(decoded, 6, false);
		NGramsBean bean = new NGramsBean(2, decoded);
		System.out.println(bean.numRepeats()*ioc + " " + Arrays.toString(columns));
	}
	
	public static void permuteKeysAction(int[] columns) {
		String ct = Ciphers.cipher[0].cipher;
		String decoded = decode(ct, columns);
		//double ioc = RepeatingFragmentsFaster.fragmentIOC(decoded, 6, false);
		//NGramsBean bean = new NGramsBean(2, decoded);
		//System.out.println(bean.numRepeats()*ioc + " " + Arrays.toString(columns));
		List<StringBuffer> list = TransformationBase.toList(decoded, 17);
		System.out.println("cipher_information=z340 columnar L=" + columns.length + " columns=" + Arrays.toString(columns));
		for (StringBuffer line : list) {
			System.out.println(line);
		}
		System.out.println();
	}
	
	public static void test() {
		String pt = "WEAREDISCOVEREDFLEEATONCE";
		String ct = encode(pt, new int[] {6,3,2,4,1,5});
		System.out.println(ct);
		System.out.println(decode("EVLNQACDTRESEARROFOKDEECYWIREE", new int[] {6,3,2,4,1,5}));
		for (int i=0; i<100; i++) {
			int[] r = rand();
			System.out.println(toString(r));
			System.out.println(" - pt [" + pt + "]");
			ct = encode(pt, r);
			System.out.println(" - ct [" + ct + "]");
			System.out.println(" - d  [" + decode(ct, r) + "]");

			System.out.println(" - pt [" + FatesUnwind.pad(pt, r.length) + "]");
			ct = encode(FatesUnwind.pad(pt, r.length), r);
			System.out.println(" - ct [" + ct + "]");
			System.out.println(" -  d [" + decode(ct, r) + "]");
		}
	}
	public static void testEncodeDecode(String pt, int[] key) {
		String ct = encode(pt, key);
		System.out.println(ct);
		System.out.println(decode(ct, key));
	}
	
	public static void testExperiment57() {
		String pt = "TIDTTOHELEOMEIDOEOENCAEDAAETNNLFE___NTRRREEHSPEWUESYATOFTONNFITOTLENLREDTOAW___EEEEIPDODSRHNFACRESADVFNAIVOITHMSEEGENUT___UEHIOTSAEEDOTFDUTSUIHOTSTSEMIFCSLVNEISCO__BLDASSLFEEAEMTCTNPERWYRTTREMCSEEHSXUECRH___NRESIRHIOTTOONEEOESEAOIEFERNITYARRNHISFS__TSTHVPITXNBIHUINHTTTHRHEHPDMNTKOOODRREER__YIAREKESPEYRSRRDTHHTESEHERFUGOBFWIITEDOK__ANTTREN";
		int[] columns = new int[] {3,5,1,2,4,7,8,6};
		System.out.println(decode(pt, columns));
		String ct = "Mt#/TU2|p4U>|G_U|UJS)(8;+A87SZp3R+pYX@HH&c|5FBcOkJf6(7U3#U&X3*1U9p|Z7&E#7U(OcX3RRJ|*B#U;PHbZ3(#HdVA;<3ZAzNU*MO^f|fC8Sk7cREkcKGUTfd|R#U/3_k8Vk#bU7VTFJ^z3%-p<Z|*-)ULcLp;+V-pqdY>|^7)7ZBE&O6l/.Hc^%f||5F9k4#PK3+:jHY-*lK#UT1UUZD8UJfD>U@Jq8&Xz@6A&&j2zF3@MD@f1KNBzT9XyzbkzZ277/K&bYbB;^X.WUUU#@HER&-+6GAHcWdFBc6P%HH_/bb.cVJ54lqkCUyqOGG1D;U:).AST/H4Z";
		System.out.println(decode(ct, columns));
		
		Periods.test(ct, false);
		Periods.test(decode(ct, columns), false);
		
		System.out.println("ioc " + RepeatingFragmentsFaster.fragmentIOC(ct, 6, false));
		System.out.println("ioc " + RepeatingFragmentsFaster.fragmentIOC(decode(ct, columns), 6, false));
		
	}
	
	public static void testExperiment61() {
		String pt = "NHRRWO_EIMYHLTFWLENETRRCRFA_EEEETAILEWIIRRLTLSOIE_ARNEOHNAEHESRBHLOBMCATACEWSWICHMEMYUIEAEHROCROEIEHOARATROEEUUOHNHRLHITHSCAAHATLCSHHAH_ETASHESTERTRESHAOATI_RETTOPTHAFOSATEUTTGT_TAHECREDEIEETRATOTIE_OAHEAFHNTYSSUESEFFVC_NGAHTTIDHTHIEBRTNSES_YROROTEETOPACICTTAIIFLASDSSERSESERFOTOTRT_DMFOFIDNOULIHNESHSLR_MSTRHFCADANSGEBHLSRNN_SETDUEFNSERTIU";
		int[] columns = new int[] {4,6,2,15,10,11,3,16,9,7,8,12,14,1,5,13};
		System.out.println(decode(pt, columns));
		//String ct = "Mt#/TU2|p4U>|G_U|UJS)(8;+A87SZp3R+pYX@HH&c|5FBcOkJf6(7U3#U&X3*1U9p|Z7&E#7U(OcX3RRJ|*B#U;PHbZ3(#HdVA;<3ZAzNU*MO^f|fC8Sk7cREkcKGUTfd|R#U/3_k8Vk#bU7VTFJ^z3%-p<Z|*-)ULcLp;+V-pqdY>|^7)7ZBE&O6l/.Hc^%f||5F9k4#PK3+:jHY-*lK#UT1UUZD8UJfD>U@Jq8&Xz@6A&&j2zF3@MD@f1KNBzT9XyzbkzZ277/K&bYbB;^X.WUUU#@HER&-+6GAHcWdFBc6P%HH_/bb.cVJ54lqkCUyqOGG1D;U:).AST/H4Z";
		//System.out.println(decode(ct, columns));
		
		//Periods.test(ct, false);
		//Periods.test(decode(ct, columns), false);
		
		//System.out.println("ioc " + RepeatingFragmentsFaster.fragmentIOC(ct, 6, false));
		//System.out.println("ioc " + RepeatingFragmentsFaster.fragmentIOC(decode(ct, columns), 6, false));
		
	}
	
	public static String toString(int[] array) {
		String result = "";
		for (int i : array) result += i + " ";
		return result;
	}
	
	public static int[] rand() {
		int L=(int)(1+Math.random()*10);
		int[] result = new int[L];
		Set<Integer> set = new HashSet<Integer>();
		for (int i=0; i<L; i++) {
			int col = 1 + (int) (Math.random()*L);
			while (set.contains(col)) col = 1 + (int) (Math.random()*L);
			set.add(col);
			result[i] = col;
		}
		return result;
	}
	
	public static void forAzdecrypt() {
		// 7!+6!+5!+4!+3!+2! = 5912 possible ciphers to solve
		for (int L=2; L<8; L++) {
			permuteKeys(L);
		}
	}
	
	public static void main(String[] args) {
		testEncodeDecode("DEFENDTHEEASTWALLOFTHECASTLEXX", new int[] {5, 2, 1, 4, 6, 3});
//		test();
		//testExperiment57();
//		testExperiment61();
		//permuteKeys(8);
		//int[] i = columnarKeyFor("FATESUNWINDINFINITY",false);
		//System.out.println(toString(i));
		//forAzdecrypt();
		/*
		int[] cols = new int[] {4,2,5,3,1};
		String pt = "WHICHWRISTWATCHESARESWISSWRISTWATCHES";
		String ct = encode(pt, cols);
		System.out.println(ct);
		pt = decode(ct, cols);
		System.out.println(pt);
		*/
	}
}
