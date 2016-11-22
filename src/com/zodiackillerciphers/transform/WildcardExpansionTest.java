package com.zodiackillerciphers.transform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.cosine.CosineSimilarity;
import com.zodiackillerciphers.io.FileUtil;

public class WildcardExpansionTest {
	
	public static String expand(String cipher, char symbol) {
		Set<Character> seen = new HashSet<Character>(Ciphers.alphabetAsList(cipher));
		String newCipher = "";
		int pos = 0;
		for (int i=0; i<cipher.length(); i++) {
			char ch = cipher.charAt(i);
			if (ch == symbol) {
				boolean go = true;
				while (go) {
					ch = Ciphers.alphabet[4].charAt(pos++);
					if (!seen.contains(ch)) go = false;
				}
				newCipher += ch;
				seen.add(ch);
			} else {
				newCipher += ch;
			}
		}
		return newCipher;
	}
	public static int[] expand(int[] cipher, int symbol) {
		int next = 0;
		//Set<Integer> seen = new HashSet<Integer>();
		for (int s : cipher) {
			//seen.add(s);
			next = Math.max(next, s);
		}
		next++;
		
		List<Integer> newCipher = new ArrayList<Integer>();
		for (int i=0; i<cipher.length; i++) {
			int ch = cipher[i];
			if (ch == symbol) {
				boolean go = true;
				newCipher.add(next);
				//seen.add(next);
				next++;
			} else {
				newCipher.add(ch);
			}
		}
		int[] result = new int[newCipher.size()];
		int pos = 0;
		for (int i : newCipher) result[pos++] = i;
		return result;
	}

	public static void process() {
		List<String> list = FileUtil.loadFrom("/Users/doranchak/projects/zodiac/feh2");
		for (String line : list) {
			if (line.contains("smokie")) {
				String[] split = line.split("\\t");
				//System.out.println(split.length);
				System.out.println("cipher_information=" + split[1].substring(1) + " sum " + split[0]);
				
				String cstr = split[2].replaceAll(",", "").replace("[","").replace("]", "");
				String[] c = cstr.split(" ");
				for (int i=0; i<c.length; i++) {
					if (i>0 && i%17==0) System.out.println();
					System.out.print(c[i] + " ");
				}
				
				//List<StringBuffer> sb = TransformationBase.toList(split[2], 17);
				//for (StringBuffer s : sb) System.out.println(s);
				System.out.println();
				System.out.println();
				//System.exit(-1);
			}
		}
	}
	public static void testSmokie33() {
		//String cipher = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijkAABlmnopqBaBrMPsWWtuvfRLNwESBZYAbCxsdKTMgyz01B23oH42WQXiWEJlmBhNUVefPr5R6yYdHBABBqbagjkFZ7VAicGSvGuIJKm8oYQLMBCfPTNeR1bU2WpUd3ZjcBAALMgngBYJl5oXvwuIPrqRBstK1dmfTxQ7BlDpON85zGUUweABW0xHIAMbYWrqfZyg3nojFtu1iLYBEBBTNpJKmAPS0UDRQMb5G9g7zyweAfiBlWj6ZtsXx2NnopLkVbBUWdTAD8eG4grMBOAWYiwjBCfxLIJKUZXE6PuBcBSAR33";
		//String cipher = "ABCDEFGHIJKLMNOPQRSTUFVEWXVYZabcdefghXijklmnopqrsPtuJJJUEvWwxkBpJXXaplRcyzRM01Of0XSspL2KCQNDMZJDgo2bJKILTTJ3LGzJMy4qW5i6FRmfdh7XN8QMoAup8yrcMavPJfdJ0wVxWszSFJMtRL8MXGrZXnNpRefgPXJAJUb1JCBHJKdaqcki3liMuJJEGfYRUWakrqORwlEsJ3SDNS51ugwxkEzgF6XOV7JtRJWEIkEBli2JGm8UpXhYWPXkcWJRflH0ZRWvvlsQ4MXqAJkl0zCMJaGPYJzukD6MXWJ0Czk9X8Cxl7ZtFLzRcJEXG1Oa6PMU";
		
		int[] cipher = new int[] { 21, 46, 1, 35, 10, 33, 2, 50, 11, 14, 30,
				26, 6, 27, 39, 57, 51, 23, 40, 34, 15, 33, 4, 10, 16, 54, 4, 7,
				41, 52, 19, 47, 61, 22, 48, 42, 58, 54, 13, 5, 17, 18, 20, 59,
				28, 55, 24, 8, 49, 57, 53, 25, 14, 14, 14, 15, 10, 43, 16, 44,
				45, 17, 46, 55, 14, 54, 54, 52, 55, 18, 23, 47, 62, 56, 23, 6,
				38, 9, 39, 48, 38, 54, 40, 49, 55, 26, 31, 30, 1, 51, 27, 35,
				6, 41, 14, 35, 42, 28, 31, 19, 14, 30, 11, 26, 34, 34, 14, 12,
				26, 2, 56, 14, 6, 62, 60, 24, 16, 36, 13, 3, 33, 23, 20, 48,
				61, 58, 37, 54, 27, 32, 51, 6, 28, 21, 25, 55, 32, 62, 8, 47,
				6, 52, 43, 57, 14, 48, 61, 14, 38, 44, 4, 45, 16, 49, 56, 40,
				33, 14, 6, 53, 23, 26, 32, 6, 54, 2, 8, 41, 54, 59, 27, 55, 23,
				22, 48, 42, 57, 54, 14, 21, 14, 15, 19, 9, 14, 1, 46, 50, 14,
				30, 61, 52, 24, 47, 17, 13, 12, 18, 13, 6, 25, 14, 14, 10, 2,
				48, 7, 23, 15, 16, 52, 17, 8, 24, 39, 23, 44, 18, 10, 49, 14,
				12, 40, 35, 27, 40, 36, 9, 25, 42, 44, 45, 17, 10, 56, 42, 33,
				3, 54, 39, 4, 37, 14, 53, 23, 14, 16, 10, 11, 17, 10, 46, 18,
				13, 31, 14, 2, 20, 32, 15, 55, 54, 58, 7, 16, 57, 54, 17, 47,
				16, 14, 23, 48, 18, 50, 38, 41, 23, 16, 43, 43, 18, 49, 51, 60,
				6, 54, 24, 21, 14, 17, 18, 38, 56, 1, 6, 14, 52, 2, 57, 7, 14,
				56, 25, 17, 35, 3, 6, 54, 16, 14, 38, 1, 56, 17, 29, 54, 32, 1,
				45, 18, 37, 41, 53, 33, 26, 56, 23, 47, 14, 10, 54, 2, 9, 39,
				52, 3, 57, 6, 15 };
		
		Map<Integer, Integer> countMap = new HashMap<Integer, Integer>();
		for (int key : cipher) {
			Integer val = countMap.get(key);
			if (val == null) val = 0;
			val++;
			countMap.put(key,  val);
		}
		
		List<Integer> symbol = new ArrayList<Integer>(countMap.keySet());
		System.out.println(symbol);
		Set<String> seen = new HashSet<String>();
		int num = 0;
		for (int i=0; i<symbol.size()-2; i++) {
			for (int j=i+1; j<symbol.size()-1; j++) {
				for (int k=j+1; k<symbol.size(); k++) {
					int c1 = symbol.get(i);
					int c2 = symbol.get(j);
					int c3 = symbol.get(k);
					String key = ""+c1+" "+c2+" "+c3;
					if (c1 == c2 || c1 == c3 || c2 == c3) continue;
					if (seen.contains(key)) {
						System.out.println("already seen " + key);
						System.exit(-1);
					}
					seen.add(key);
					
					int[] newCipher = expand(cipher, c1);
					newCipher = expand(newCipher, c2);
					newCipher = expand(newCipher, c3);
					
					int sum = countMap.get(c1) + countMap.get(c2) + countMap.get(c3); 
					System.out.println(sum + "	" + " smokie33 " + num + " " + key + "	" + Arrays.toString(newCipher));
					//System.out.println("cipher_information=smokie33 " + num + " " + c1 + " " + c2 + " " + c3);
					//List<StringBuffer> sb = TransformationBase.toList(newCipher, 17);
					//for (StringBuffer s : sb) System.out.println(s);
					//System.out.println();
					num++;
				}
			}
		}
	}
	public static void testSmokie33b() {
		
		int[] cipher = new int[] { 21, 46, 1, 35, 10, 33, 2, 50, 11, 14, 30,
				26, 6, 27, 39, 57, 51, 23, 40, 34, 15, 33, 4, 10, 16, 54, 4, 7,
				41, 52, 19, 47, 61, 22, 48, 42, 58, 54, 13, 5, 17, 18, 20, 59,
				28, 55, 24, 8, 49, 57, 53, 25, 14, 14, 14, 15, 10, 43, 16, 44,
				45, 17, 46, 55, 14, 54, 54, 52, 55, 18, 23, 47, 62, 56, 23, 6,
				38, 9, 39, 48, 38, 54, 40, 49, 55, 26, 31, 30, 1, 51, 27, 35,
				6, 41, 14, 35, 42, 28, 31, 19, 14, 30, 11, 26, 34, 34, 14, 12,
				26, 2, 56, 14, 6, 62, 60, 24, 16, 36, 13, 3, 33, 23, 20, 48,
				61, 58, 37, 54, 27, 32, 51, 6, 28, 21, 25, 55, 32, 62, 8, 47,
				6, 52, 43, 57, 14, 48, 61, 14, 38, 44, 4, 45, 16, 49, 56, 40,
				33, 14, 6, 53, 23, 26, 32, 6, 54, 2, 8, 41, 54, 59, 27, 55, 23,
				22, 48, 42, 57, 54, 14, 21, 14, 15, 19, 9, 14, 1, 46, 50, 14,
				30, 61, 52, 24, 47, 17, 13, 12, 18, 13, 6, 25, 14, 14, 10, 2,
				48, 7, 23, 15, 16, 52, 17, 8, 24, 39, 23, 44, 18, 10, 49, 14,
				12, 40, 35, 27, 40, 36, 9, 25, 42, 44, 45, 17, 10, 56, 42, 33,
				3, 54, 39, 4, 37, 14, 53, 23, 14, 16, 10, 11, 17, 10, 46, 18,
				13, 31, 14, 2, 20, 32, 15, 55, 54, 58, 7, 16, 57, 54, 17, 47,
				16, 14, 23, 48, 18, 50, 38, 41, 23, 16, 43, 43, 18, 49, 51, 60,
				6, 54, 24, 21, 14, 17, 18, 38, 56, 1, 6, 14, 52, 2, 57, 7, 14,
				56, 25, 17, 35, 3, 6, 54, 16, 14, 38, 1, 56, 17, 29, 54, 32, 1,
				45, 18, 37, 41, 53, 33, 26, 56, 23, 47, 14, 10, 54, 2, 9, 39,
				52, 3, 57, 6, 15 };
		
		/*
		 * 
		 * Sixth.

The 6-gram session has so far expanded symbols: 14,54,18,6,40,58
The 7-gram session has so far expanded symbols: 14,54,6,18,40,35

.k., you have been doing great. However, there are two more poly and symbol 40 is not poly

Fifth.

Thanks for following it up smokie.

The 6-gram session has so far expanded symbols: 14,54,18,6,40
The 7-gram session has so far expanded symbols: 14,54,6,18,40

You are expanding the correct symbols. 7 gram on top, 6 gram on bottom

Fourth.

The 6-gram session has so far expanded symbols: 14,54,18,6
The 7-gram session has so far expanded symbols: 14,54,6,18

		 */

		// correct poly symbols are: 6, 14, 18, 35, 54, 58
		cipher = expand(expand(expand(expand(expand(expand(cipher, 6), 14), 18), 35), 54), 58);
		System.out.println(Arrays.toString(cipher));
		
		// contradictions: row 18 col 10 = A vs E  (will lock at A)
		//                 row 10 col 16 = T vs S  (will leave unlocked)
		
		// most up to date correct positions
		/*
1: 1 2 3 4 5 7 8 10 12 17
2: 2 3 7 8 12
3: 1 2 5 7 8 10 14 15 17
4: 1 2 6 8 11 12
5: 2 6 9 12 13 15 16
6: 1 4 5 9 12 16
7: 2 3 4 5 6 7 8 9 10 15 16 17
8: 1 5 11 12 15 16
9: 1 3 5 10 13 17
10: 1 2 3 7 9 10 11 13 14 15 17
11: 5 6 10 11 16 17
12: 1 8 9 10 11 12 14 15 17  
13: 1 2 6 8 9 14 15 16 
14: 1 2 5 6 8 9 12 13 14 15 17
15: 6 9 10 12 13 14 15 16
16: 2 4 10 13 15
17: 1 2 3 4 5 7 10 11 12 17
18: 1 2 3 4 5 6 8 10 14 15 16
19: 1 2 3 4 5 6 7 8 9 12 13 15 17
20: 1 3 4 7 8 10 14 16

*/
		String[] partial = new String[] {
				"GRANDSASHEWILLINS",
				"TOMISSDESSIONTHEP",
				"ROVEDHEEUWUBTCRNS",
				"HESAIDGETHERBROWN",
				"BETHATTONWIRNFORB",
				"ITWASLABOUTOUTTOW",
				"HIMMEDIATEDATTEND",
				"ASTURESPELLSOUGHB",
				"LACHINGNOREANTSHE",
				"RTOSAYSTILLYACOOW",
				"LBTPRONGAGEITWEAR",
				"STWENTHEDDEDTHEAD",
				"ARITIENECTITTEDRE",
				"DOLLONWHOTHEDTOSA",
				"YISPOSTHEDHEDREDT",
				"HAULIBERIENCEHEST",
				"RESNOTEGGERSTHATG",
				"REENTAEONANINTHER",
				"ALTERNATEOFLAHEPO",
				"SSITTHEDRAWINANCI"				
		};
		int[][] correct = new int[][] { { 1, 1 }, { 1, 2 }, { 1, 3 }, { 1, 4 },
				{ 1, 5 }, { 1, 7 }, { 1, 8 }, { 1, 10 }, { 1, 12 }, { 1, 17 },
				{ 2, 2 }, { 2, 3 }, { 2, 7 }, { 2, 8 }, { 2, 12 }, { 3, 1 },
				{ 3, 2 }, { 3, 5 }, { 3, 7 }, { 3, 8 }, { 3, 10 }, { 3, 14 },
				{ 3, 15 }, { 3, 17 }, { 4, 1 }, { 4, 2 }, { 4, 6 }, { 4, 8 },
				{ 4, 11 }, { 4, 12 }, { 5, 2 }, { 5, 6 }, { 5, 9 }, { 5, 12 },
				{ 5, 13 }, { 5, 15 }, { 5, 16 }, { 6, 1 }, { 6, 4 }, { 6, 5 },
				{ 6, 9 }, { 6, 12 }, { 6, 16 }, { 7, 2 }, { 7, 3 }, { 7, 4 },
				{ 7, 5 }, { 7, 6 }, { 7, 7 }, { 7, 8 }, { 7, 9 }, { 7, 10 },
				{ 7, 15 }, { 7, 16 }, { 7, 17 }, { 8, 1 }, { 8, 5 }, { 8, 11 },
				{ 8, 12 }, { 8, 15 }, { 8, 16 }, { 9, 1 }, { 9, 3 }, { 9, 5 },
				{ 9, 10 }, { 9, 13 }, { 9, 17 }, { 10, 1 }, { 10, 2 },
				{ 10, 3 }, { 10, 7 }, { 10, 9 }, { 10, 10 }, { 10, 11 },
				{ 10, 13 }, { 10, 14 }, { 10, 15 }, { 10, 17 }, { 11, 5 },
				{ 11, 6 }, { 11, 10 }, { 11, 11 }, { 11, 16 }, { 11, 17 },
				{ 12, 1 }, { 12, 8 }, { 12, 9 }, { 12, 10 }, { 12, 11 },
				{ 12, 12 }, { 12, 14 }, { 12, 15 }, { 12, 17 }, { 13, 1 },
				{ 13, 2 }, { 13, 6 }, { 13, 8 }, { 13, 9 }, { 13, 14 },
				{ 13, 15 }, { 13, 16 }, { 14, 1 }, { 14, 2 }, { 14, 5 },
				{ 14, 6 }, { 14, 8 }, { 14, 9 }, { 14, 12 }, { 14, 13 },
				{ 14, 14 }, { 14, 15 }, { 14, 17 }, { 15, 6 }, { 15, 9 },
				{ 15, 10 }, { 15, 12 }, { 15, 13 }, { 15, 14 }, { 15, 15 },
				{ 15, 16 }, { 16, 2 }, { 16, 4 }, { 16, 10 }, { 16, 13 },
				{ 16, 15 }, { 17, 1 }, { 17, 2 }, { 17, 3 }, { 17, 4 },
				{ 17, 5 }, { 17, 7 }, { 17, 10 }, { 17, 11 }, { 17, 12 },
				{ 17, 17 }, { 18, 1 }, { 18, 2 }, { 18, 3 }, { 18, 4 },
				{ 18, 5 }, { 18, 6 }, { 18, 8 }, { 18, 10 }, { 18, 14 },
				{ 18, 15 }, { 18, 16 }, { 19, 1 }, { 19, 2 }, { 19, 3 },
				{ 19, 4 }, { 19, 5 }, { 19, 6 }, { 19, 7 }, { 19, 8 },
				{ 19, 9 }, { 19, 12 }, { 19, 13 }, { 19, 15 }, { 19, 17 },
				{ 20, 1 }, { 20, 3 }, { 20, 4 }, { 20, 7 }, { 20, 8 },
				{ 20, 10 }, { 20, 14 }, { 20, 16 } };
		/*for (int row=0; row<partial.length; row++) {
			for (int col=0; col<partial[row].length(); col++) {
				
			}
		}*/
		for (int[] rc : correct) {
			int row = rc[0]-1;
			int col = rc[1]-1;
			int n = cipher[row*17+col];
			char p = partial[row].charAt(col);
			if (n<=62) System.out.println(p + " " + n);
		}
		
		Map<Integer, Integer> merge = new HashMap<Integer, Integer>();
		merge.put(2,1);
		merge.put(3,1);
		merge.put(12,10);
		merge.put(13,10);
		merge.put(17,16);
		merge.put(38,36);
		merge.put(41,40);
		merge.put(42,40);
		merge.put(48,46);
		merge.put(49,46);
		merge.put(51,50);
		merge.put(53,50);
		
		
		List<Integer> alphabet = new ArrayList<Integer>(); 
		for (int c=0; c<cipher.length; c++) {
			int s = cipher[c];
			if (merge.containsKey(s)) s = merge.get(s);
			if (!alphabet.contains(s)) alphabet.add(s);
		}
		System.out.println(alphabet);
		
		cipher = new int[] {
				21, 46, 1, 113, 10, 33, 1, 50, 11, 76, 30, 26, 63, 27, 39, 57, 50, 23, 40, 34, 15, 33, 4, 10, 16, 118, 4, 7, 40, 52, 19, 47, 61, 22, 46, 40, 134, 119, 10, 5, 16, 104, 20, 59, 28, 55, 24, 8, 46, 57, 50, 25, 77, 78, 79, 15, 10, 43, 16, 44, 45, 16, 46, 55, 80, 120, 121, 52, 55, 105, 23, 47, 62, 56, 23, 64, 36, 9, 39, 46, 36, 122, 40, 46, 55, 26, 31, 30, 1, 50, 27, 114, 65, 40, 81, 115, 40, 28, 31, 19, 82, 30, 11, 26, 34, 34, 83, 10, 26, 1, 56, 84, 66, 62, 60, 24, 16, 36, 10, 1, 33, 23, 20, 46, 61, 135, 37, 123, 27, 32, 50, 67, 28, 21, 25, 55, 32, 62, 8, 47, 68, 52, 43, 57, 85, 46, 61, 86, 36, 44, 4, 45, 16, 46, 56, 40, 33, 87, 69, 50, 23, 26, 32, 70, 124, 1, 8, 40, 125, 59, 27, 55, 23, 22, 46, 40, 57, 126, 88, 21, 89, 15, 19, 9, 90, 1, 46, 50, 91, 30, 61, 52, 24, 47, 16, 10, 10, 106, 10, 71, 25, 92, 93, 10, 1, 46, 7, 23, 15, 16, 52, 16, 8, 24, 39, 23, 44, 107, 10, 46, 94, 10, 40, 116, 27, 40, 36, 9, 25, 40, 44, 45, 16, 10, 56, 40, 33, 1, 127, 39, 4, 37, 95, 50, 23, 96, 16, 10, 11, 16, 10, 46, 108, 10, 31, 97, 1, 20, 32, 15, 55, 128, 136, 7, 16, 57, 129, 16, 47, 16, 98, 23, 46, 109, 50, 36, 40, 23, 16, 43, 43, 110, 46, 50, 60, 72, 130, 24, 21, 99, 16, 111, 36, 56, 1, 73, 100, 52, 1, 57, 7, 101, 56, 25, 16, 117, 1, 74, 131, 16, 102, 36, 1, 56, 16, 29, 132, 32, 1, 45, 112, 37, 40, 50, 33, 26, 56, 23, 47, 103, 10, 133, 1, 9, 39, 52, 1, 57, 75, 15				
		};
		
		int num = 0;
		for (int i=0; i<alphabet.size()-1; i++) {
			for (int j=i+1; j<alphabet.size(); j++) {
					int c1 = alphabet.get(i);
					int c2 = alphabet.get(j);
					if (c1 > 62 || c2 > 62) continue;
					
					int[] newCipher = expand(cipher, c1);
					newCipher = expand(newCipher, c2);
					
					//System.out.println("smokie33 " + num + " " + key + "	" + Arrays.toString(newCipher));
					System.out.println("cipher_information=smokie33 " + num + " " + c1 + " " + c2);
					for (int row=0; row<20; row++) {
						for (int col=0; col<17; col++) {
							System.out.print(newCipher[row*17+col]);
							if (col < 16) System.out.print(" ");
						}
						System.out.println();
					}
					System.out.println();
					num++;
				}
			}
		
	}
	public static void test() {
		String cipher = "041D,%(!9=:AN).-8\"1J@Q,T3KA8G?MO'2E/X3FU\\-GL_R!-%:#3W.&HBO8\"4G]<CX[+.E6IV8Z;H2=-K6()NU@+MFRS?\\Z+8EQ1E,_LJ9%TQN!EYHS$<^O3EX/IWE]-#C-E.>40P1$Y6B:-@VJD08^1MOEWQ=18IETGK5U1]3)FGS!3'@1,DHG:R0\\AQQX-_L%SD-=VKS0-UC(.PY?8[JLD4Q0N_VT9E!,AMB-E=8KZ<EOQ+X[6W&]G2$:8C1.)>(38PH5E'YG-\\\"N^I@B>\",/6MS'-E-WX<]F)3QCQPS1YI^[*4R3G?_%.$/&3@-ELKAD-UE\\JV0HEDNS(TE%7";
		double[] o = com.zodiackillerciphers.transform.columnar.Evolve.objectives2(cipher);
		double cs = CosineSimilarity.measureWeightedSum(cipher);
		System.out.println(cs + " " + o[0] + " " + o[1] + " " + cipher);
		Map<Character, Integer> countMap = Ciphers.countMap(cipher);

		for (Character symbol : countMap.keySet()) {
			if (countMap.get(symbol) < 2) continue;
			Set<Character> seen = new HashSet<Character>(countMap.keySet());
			String newCipher = "";
			
			int a = 0;
			for (int i=0; i<cipher.length(); i++) {
				char ch = cipher.charAt(i);
				if (symbol == ch) {
					while (seen.contains(Ciphers.alphabet[4].charAt(a)))
						a++;
					char newCh = Ciphers.alphabet[4].charAt(a);
					seen.add(newCh);
					newCipher += newCh;
				} else newCipher += ch;
			}
			o = com.zodiackillerciphers.transform.columnar.Evolve.objectives2(newCipher);
			cs = CosineSimilarity.measureWeightedSum(newCipher);
			System.out.println(cs + " " + o[0] + " " + o[1] + " " + symbol + " " + countMap.get(symbol) + " " + newCipher);
		}
	}
	
	public static void main(String[] args) {
		//test();
		//System.out.println(expand(Ciphers.cipher[0].cipher, '+'));
		//testSmokie32B();
		//testSmokie33();
		testSmokie33b();
		//process();
		System.out.println("17035/182=" + 17035/182);
		System.out.println("20656/356=" + 20656/356);
		System.out.println("20680/356=" + 20680/356);
		System.out.println("20782/356=" + 20782/356);
		System.out.println("20787/359=" + 20787/359);
		System.out.println("20805/356=" + 20805/356);
		System.out.println("20822/347=" + 20822/347);
		System.out.println("20829/353=" + 20829/353);
		System.out.println("20835/356=" + 20835/356);
		System.out.println("20836/356=" + 20836/356);
		System.out.println("20844/362=" + 20844/362);
		System.out.println("20885/353=" + 20885/353);
		System.out.println("20887/365=" + 20887/365);
		System.out.println("20895/353=" + 20895/353);
		System.out.println("20905/362=" + 20905/362);
		System.out.println("20911/359=" + 20911/359);
		System.out.println("20919/356=" + 20919/356);
		System.out.println("20926/362=" + 20926/362);
		System.out.println("20927/353=" + 20927/353);
		System.out.println("20941/365=" + 20941/365);
		System.out.println("20941/365=" + 20941/365);
		System.out.println("20946/356=" + 20946/356);
		System.out.println("20946/356=" + 20946/356);
		System.out.println("20948/356=" + 20948/356);
		System.out.println("20958/359=" + 20958/359);
		System.out.println("20959/362=" + 20959/362);
		System.out.println("20969/350=" + 20969/350);
		System.out.println("20970/359=" + 20970/359);
		System.out.println("20972/362=" + 20972/362);
		System.out.println("20974/356=" + 20974/356);
		System.out.println("20974/350=" + 20974/350);
		System.out.println("20989/350=" + 20989/350);
		System.out.println("20993/359=" + 20993/359);
		System.out.println("20997/371=" + 20997/371);
		System.out.println("20999/353=" + 20999/353);
		System.out.println("21001/359=" + 21001/359);
		System.out.println("21003/362=" + 21003/362);
		System.out.println("21008/356=" + 21008/356);
		System.out.println("21008/359=" + 21008/359);
		System.out.println("21011/362=" + 21011/362);
		System.out.println("21012/359=" + 21012/359);
		System.out.println("21012/356=" + 21012/356);
		System.out.println("21025/365=" + 21025/365);
		System.out.println("21027/362=" + 21027/362);
		System.out.println("21030/362=" + 21030/362);
		System.out.println("21032/365=" + 21032/365);
		System.out.println("21032/356=" + 21032/356);
		System.out.println("21033/359=" + 21033/359);
		System.out.println("21036/362=" + 21036/362);
		System.out.println("21041/359=" + 21041/359);
		System.out.println("21042/359=" + 21042/359);
		System.out.println("21042/356=" + 21042/356);
		System.out.println("21044/353=" + 21044/353);
		System.out.println("21047/353=" + 21047/353);
		System.out.println("21047/359=" + 21047/359);
		System.out.println("21048/359=" + 21048/359);
		System.out.println("21052/356=" + 21052/356);
		System.out.println("21052/362=" + 21052/362);
		System.out.println("21054/365=" + 21054/365);
		System.out.println("21055/362=" + 21055/362);
		System.out.println("21056/365=" + 21056/365);
		System.out.println("21057/362=" + 21057/362);
		System.out.println("21060/356=" + 21060/356);
		System.out.println("21061/368=" + 21061/368);
		System.out.println("21061/356=" + 21061/356);
		System.out.println("21064/359=" + 21064/359);
		System.out.println("21067/365=" + 21067/365);
		System.out.println("21069/365=" + 21069/365);
		System.out.println("21072/356=" + 21072/356);
		System.out.println("21080/359=" + 21080/359);
		System.out.println("21087/359=" + 21087/359);
		System.out.println("21087/365=" + 21087/365);
		System.out.println("21091/353=" + 21091/353);
		System.out.println("21094/365=" + 21094/365);
		System.out.println("21096/359=" + 21096/359);
		System.out.println("21097/365=" + 21097/365);
		System.out.println("21102/359=" + 21102/359);
		System.out.println("21103/368=" + 21103/368);
		System.out.println("21104/365=" + 21104/365);
		System.out.println("21106/362=" + 21106/362);
		System.out.println("21106/356=" + 21106/356);
		System.out.println("21106/353=" + 21106/353);
		System.out.println("21107/365=" + 21107/365);
		System.out.println("21108/356=" + 21108/356);
		System.out.println("21109/353=" + 21109/353);
		System.out.println("21109/368=" + 21109/368);
		System.out.println("21110/365=" + 21110/365);
		System.out.println("21112/371=" + 21112/371);
		System.out.println("21117/371=" + 21117/371);
		System.out.println("21122/362=" + 21122/362);
		System.out.println("21122/359=" + 21122/359);
		System.out.println("21123/365=" + 21123/365);
		System.out.println("21124/362=" + 21124/362);
		System.out.println("21124/374=" + 21124/374);
		System.out.println("21126/371=" + 21126/371);
		System.out.println("21129/362=" + 21129/362);
		System.out.println("21130/359=" + 21130/359);
		System.out.println("21135/365=" + 21135/365);
		System.out.println("21136/362=" + 21136/362);
		System.out.println("21141/350=" + 21141/350);
		System.out.println("21145/353=" + 21145/353);
		System.out.println("21147/359=" + 21147/359);
		System.out.println("21150/362=" + 21150/362);
		System.out.println("21150/359=" + 21150/359);
		System.out.println("21152/359=" + 21152/359);
		System.out.println("21152/359=" + 21152/359);
		System.out.println("21153/362=" + 21153/362);
		System.out.println("21153/365=" + 21153/365);
		System.out.println("21153/374=" + 21153/374);
		System.out.println("21154/362=" + 21154/362);
		System.out.println("21158/347=" + 21158/347);
		System.out.println("21161/362=" + 21161/362);
		System.out.println("21163/362=" + 21163/362);
		System.out.println("21164/362=" + 21164/362);
		System.out.println("21164/362=" + 21164/362);
		System.out.println("21164/365=" + 21164/365);
		System.out.println("21166/371=" + 21166/371);
		System.out.println("21167/350=" + 21167/350);
		System.out.println("21168/356=" + 21168/356);
		System.out.println("21170/365=" + 21170/365);
		System.out.println("21174/371=" + 21174/371);
		System.out.println("21175/368=" + 21175/368);
		System.out.println("21175/362=" + 21175/362);
		System.out.println("21176/359=" + 21176/359);
		System.out.println("21178/365=" + 21178/365);
		System.out.println("21180/353=" + 21180/353);
		System.out.println("21180/374=" + 21180/374);
		System.out.println("21182/371=" + 21182/371);
		System.out.println("21183/356=" + 21183/356);
		System.out.println("21185/356=" + 21185/356);
		System.out.println("21186/371=" + 21186/371);
		System.out.println("21188/362=" + 21188/362);
		System.out.println("21191/359=" + 21191/359);
		System.out.println("21191/368=" + 21191/368);
		System.out.println("21191/365=" + 21191/365);
		System.out.println("21193/356=" + 21193/356);
		System.out.println("21195/368=" + 21195/368);
		System.out.println("21196/362=" + 21196/362);
		System.out.println("21196/359=" + 21196/359);
		System.out.println("21198/356=" + 21198/356);
		System.out.println("21201/359=" + 21201/359);
		System.out.println("21202/356=" + 21202/356);
		System.out.println("21203/359=" + 21203/359);
		System.out.println("21204/356=" + 21204/356);
		System.out.println("21205/356=" + 21205/356);
		System.out.println("21207/356=" + 21207/356);
		System.out.println("21208/350=" + 21208/350);
		System.out.println("21208/353=" + 21208/353);
		System.out.println("21208/362=" + 21208/362);
		System.out.println("21211/362=" + 21211/362);
		System.out.println("21211/362=" + 21211/362);
		System.out.println("21212/371=" + 21212/371);
		System.out.println("21213/362=" + 21213/362);
		System.out.println("21216/371=" + 21216/371);
		System.out.println("21217/365=" + 21217/365);
		System.out.println("21217/362=" + 21217/362);
		System.out.println("21218/368=" + 21218/368);
		System.out.println("21219/365=" + 21219/365);
		System.out.println("21219/356=" + 21219/356);
		System.out.println("21222/359=" + 21222/359);
		System.out.println("21222/362=" + 21222/362);
		System.out.println("21223/365=" + 21223/365);
		System.out.println("21225/368=" + 21225/368);
		System.out.println("21226/365=" + 21226/365);
		System.out.println("21226/365=" + 21226/365);
		System.out.println("21228/359=" + 21228/359);
		System.out.println("21228/374=" + 21228/374);
		System.out.println("21230/376=" + 21230/376);
		System.out.println("21231/359=" + 21231/359);
		System.out.println("21231/371=" + 21231/371);
		System.out.println("21231/368=" + 21231/368);
		System.out.println("21232/368=" + 21232/368);
		System.out.println("21232/365=" + 21232/365);
		System.out.println("21232/368=" + 21232/368);
		System.out.println("21232/371=" + 21232/371);
		System.out.println("21234/359=" + 21234/359);
		System.out.println("21236/371=" + 21236/371);
		System.out.println("21237/359=" + 21237/359);
		System.out.println("21238/362=" + 21238/362);
		System.out.println("21238/365=" + 21238/365);
		System.out.println("21238/365=" + 21238/365);
		System.out.println("21239/368=" + 21239/368);
		System.out.println("21240/359=" + 21240/359);
		System.out.println("21240/371=" + 21240/371);
		System.out.println("21241/365=" + 21241/365);
		System.out.println("21242/365=" + 21242/365);
		System.out.println("21244/371=" + 21244/371);
		System.out.println("21246/359=" + 21246/359);
		System.out.println("21246/368=" + 21246/368);
		System.out.println("21247/368=" + 21247/368);
		System.out.println("21249/353=" + 21249/353);
		System.out.println("21251/353=" + 21251/353);
		System.out.println("21251/362=" + 21251/362);
		System.out.println("21251/362=" + 21251/362);
		System.out.println("21251/362=" + 21251/362);
		System.out.println("21253/362=" + 21253/362);
		System.out.println("21253/359=" + 21253/359);
		System.out.println("21257/359=" + 21257/359);
		System.out.println("21260/368=" + 21260/368);
		System.out.println("21261/362=" + 21261/362);
		System.out.println("21262/371=" + 21262/371);
		System.out.println("21263/365=" + 21263/365);
		System.out.println("21265/362=" + 21265/362);
		System.out.println("21265/371=" + 21265/371);
		System.out.println("21265/376=" + 21265/376);
		System.out.println("21266/365=" + 21266/365);
		System.out.println("21267/374=" + 21267/374);
		System.out.println("21268/365=" + 21268/365);
		System.out.println("21268/359=" + 21268/359);
		System.out.println("21269/365=" + 21269/365);
		System.out.println("21269/362=" + 21269/362);
		System.out.println("21269/359=" + 21269/359);
		System.out.println("21270/359=" + 21270/359);
		System.out.println("21271/365=" + 21271/365);
		System.out.println("21271/359=" + 21271/359);
		System.out.println("21275/374=" + 21275/374);
		System.out.println("21275/368=" + 21275/368);
		System.out.println("21276/368=" + 21276/368);
		System.out.println("21278/365=" + 21278/365);
		System.out.println("21280/368=" + 21280/368);
		System.out.println("21281/365=" + 21281/365);
		System.out.println("21283/368=" + 21283/368);
		System.out.println("21284/365=" + 21284/365);
		System.out.println("21286/371=" + 21286/371);
		System.out.println("21286/365=" + 21286/365);
		System.out.println("21287/374=" + 21287/374);
		System.out.println("21287/368=" + 21287/368);
		System.out.println("21289/368=" + 21289/368);
		System.out.println("21289/371=" + 21289/371);
		System.out.println("21289/371=" + 21289/371);
		System.out.println("21290/362=" + 21290/362);
		System.out.println("21290/368=" + 21290/368);
		System.out.println("21293/359=" + 21293/359);
		System.out.println("21294/371=" + 21294/371);
		System.out.println("21294/365=" + 21294/365);
		System.out.println("21295/371=" + 21295/371);
		System.out.println("21296/365=" + 21296/365);
		System.out.println("21296/356=" + 21296/356);
		System.out.println("21297/371=" + 21297/371);
		System.out.println("21298/356=" + 21298/356);
		System.out.println("21298/374=" + 21298/374);
		System.out.println("21299/374=" + 21299/374);
		System.out.println("21299/362=" + 21299/362);
		System.out.println("21300/368=" + 21300/368);
		System.out.println("21302/365=" + 21302/365);
		System.out.println("21303/374=" + 21303/374);
		System.out.println("21303/353=" + 21303/353);
		System.out.println("21305/362=" + 21305/362);
		System.out.println("21305/365=" + 21305/365);
		System.out.println("21305/368=" + 21305/368);
		System.out.println("21310/359=" + 21310/359);
		System.out.println("21311/371=" + 21311/371);
		System.out.println("21311/362=" + 21311/362);
		System.out.println("21311/362=" + 21311/362);
		System.out.println("21313/368=" + 21313/368);
		System.out.println("21315/368=" + 21315/368);
		System.out.println("21315/365=" + 21315/365);
		System.out.println("21316/362=" + 21316/362);
		System.out.println("21317/365=" + 21317/365);
		System.out.println("21317/362=" + 21317/362);
		System.out.println("21318/362=" + 21318/362);
		System.out.println("21318/368=" + 21318/368);
		System.out.println("21321/365=" + 21321/365);
		System.out.println("21321/362=" + 21321/362);
		System.out.println("21322/362=" + 21322/362);
		System.out.println("21322/365=" + 21322/365);
		System.out.println("21323/362=" + 21323/362);
		System.out.println("21326/371=" + 21326/371);
		System.out.println("21328/353=" + 21328/353);
		System.out.println("21329/359=" + 21329/359);
		System.out.println("21331/368=" + 21331/368);
		System.out.println("21333/356=" + 21333/356);
		System.out.println("21334/365=" + 21334/365);
		System.out.println("21334/362=" + 21334/362);
		System.out.println("21335/362=" + 21335/362);
		System.out.println("21335/362=" + 21335/362);
		System.out.println("21336/368=" + 21336/368);
		System.out.println("21337/365=" + 21337/365);
		System.out.println("21337/353=" + 21337/353);
		System.out.println("21338/368=" + 21338/368);
		System.out.println("21339/359=" + 21339/359);
		System.out.println("21340/356=" + 21340/356);
		System.out.println("21340/362=" + 21340/362);
		System.out.println("21340/365=" + 21340/365);
		System.out.println("21342/362=" + 21342/362);
		System.out.println("21342/371=" + 21342/371);
		System.out.println("21343/359=" + 21343/359);
		System.out.println("21343/374=" + 21343/374);
		System.out.println("21344/371=" + 21344/371);
		System.out.println("21347/362=" + 21347/362);
		System.out.println("21348/353=" + 21348/353);
		System.out.println("21348/362=" + 21348/362);
		System.out.println("21348/368=" + 21348/368);
		System.out.println("21348/362=" + 21348/362);
		System.out.println("21349/376=" + 21349/376);
		System.out.println("21350/368=" + 21350/368);
		System.out.println("21352/379=" + 21352/379);
		System.out.println("21352/368=" + 21352/368);
		System.out.println("21353/368=" + 21353/368);
		System.out.println("21353/362=" + 21353/362);
		System.out.println("21355/379=" + 21355/379);
		System.out.println("21355/365=" + 21355/365);
		System.out.println("21356/356=" + 21356/356);
		System.out.println("21359/371=" + 21359/371);
		System.out.println("21359/374=" + 21359/374);
		System.out.println("21360/371=" + 21360/371);
		System.out.println("21360/356=" + 21360/356);
		System.out.println("21363/356=" + 21363/356);
		System.out.println("21365/371=" + 21365/371);
		System.out.println("21366/359=" + 21366/359);
		System.out.println("21367/362=" + 21367/362);
		System.out.println("21368/368=" + 21368/368);
		System.out.println("21371/362=" + 21371/362);
		System.out.println("21374/365=" + 21374/365);
		System.out.println("21376/365=" + 21376/365);
		System.out.println("21378/374=" + 21378/374);
		System.out.println("21379/374=" + 21379/374);
		System.out.println("21381/371=" + 21381/371);
		System.out.println("21382/359=" + 21382/359);
		System.out.println("21387/362=" + 21387/362);
		System.out.println("21387/371=" + 21387/371);
		System.out.println("21388/362=" + 21388/362);
		System.out.println("21392/362=" + 21392/362);
		System.out.println("21393/365=" + 21393/365);
		System.out.println("21394/379=" + 21394/379);
		System.out.println("21396/365=" + 21396/365);
		System.out.println("21399/362=" + 21399/362);
		System.out.println("21402/362=" + 21402/362);
		System.out.println("21402/365=" + 21402/365);
		System.out.println("21405/365=" + 21405/365);
		System.out.println("21405/374=" + 21405/374);
		System.out.println("21410/356=" + 21410/356);
		System.out.println("21410/362=" + 21410/362);
		System.out.println("21411/368=" + 21411/368);
		System.out.println("21412/362=" + 21412/362);
		System.out.println("21413/362=" + 21413/362);
		System.out.println("21414/365=" + 21414/365);
		System.out.println("21414/362=" + 21414/362);
		System.out.println("21414/362=" + 21414/362);
		System.out.println("21415/356=" + 21415/356);
		System.out.println("21416/359=" + 21416/359);
		System.out.println("21417/359=" + 21417/359);
		System.out.println("21417/385=" + 21417/385);
		System.out.println("21418/362=" + 21418/362);
		System.out.println("21418/376=" + 21418/376);
		System.out.println("21418/371=" + 21418/371);
		System.out.println("21419/371=" + 21419/371);
		System.out.println("21421/362=" + 21421/362);
		System.out.println("21421/376=" + 21421/376);
		System.out.println("21421/368=" + 21421/368);
		System.out.println("21423/359=" + 21423/359);
		System.out.println("21423/371=" + 21423/371);
		System.out.println("21423/374=" + 21423/374);
		System.out.println("21427/368=" + 21427/368);
		System.out.println("21429/371=" + 21429/371);
		System.out.println("21429/359=" + 21429/359);
		System.out.println("21430/371=" + 21430/371);
		System.out.println("21431/365=" + 21431/365);
		System.out.println("21434/365=" + 21434/365);
		System.out.println("21436/374=" + 21436/374);
		System.out.println("21437/365=" + 21437/365);
		System.out.println("21439/362=" + 21439/362);
		System.out.println("21440/368=" + 21440/368);
		System.out.println("21443/365=" + 21443/365);
		System.out.println("21444/359=" + 21444/359);
		System.out.println("21448/365=" + 21448/365);
		System.out.println("21448/376=" + 21448/376);
		System.out.println("21449/374=" + 21449/374);
		System.out.println("21452/374=" + 21452/374);
		System.out.println("21454/365=" + 21454/365);
		System.out.println("21454/359=" + 21454/359);
		System.out.println("21454/365=" + 21454/365);
		System.out.println("21456/359=" + 21456/359);
		System.out.println("21456/371=" + 21456/371);
		System.out.println("21457/374=" + 21457/374);
		System.out.println("21457/368=" + 21457/368);
		System.out.println("21459/368=" + 21459/368);
		System.out.println("21460/368=" + 21460/368);
		System.out.println("21460/368=" + 21460/368);
		System.out.println("21461/365=" + 21461/365);
		System.out.println("21461/371=" + 21461/371);
		System.out.println("21462/368=" + 21462/368);
		System.out.println("21463/362=" + 21463/362);
		System.out.println("21463/371=" + 21463/371);
		System.out.println("21464/379=" + 21464/379);
		System.out.println("21464/379=" + 21464/379);
		System.out.println("21468/371=" + 21468/371);
		System.out.println("21469/365=" + 21469/365);
		System.out.println("21469/359=" + 21469/359);
		System.out.println("21470/356=" + 21470/356);
		System.out.println("21471/376=" + 21471/376);
		System.out.println("21471/359=" + 21471/359);
		System.out.println("21471/368=" + 21471/368);
		System.out.println("21474/379=" + 21474/379);
		System.out.println("21477/362=" + 21477/362);
		System.out.println("21478/374=" + 21478/374);
		System.out.println("21478/365=" + 21478/365);
		System.out.println("21479/359=" + 21479/359);
		System.out.println("21480/376=" + 21480/376);
		System.out.println("21480/379=" + 21480/379);
		System.out.println("21481/371=" + 21481/371);
		System.out.println("21481/365=" + 21481/365);
		System.out.println("21481/371=" + 21481/371);
		System.out.println("21483/362=" + 21483/362);
		System.out.println("21484/362=" + 21484/362);
		System.out.println("21485/362=" + 21485/362);
		System.out.println("21485/376=" + 21485/376);
		System.out.println("21486/368=" + 21486/368);
		System.out.println("21486/385=" + 21486/385);
		System.out.println("21487/374=" + 21487/374);
		System.out.println("21488/368=" + 21488/368);
		System.out.println("21489/365=" + 21489/365);
		System.out.println("21489/382=" + 21489/382);
		System.out.println("21491/368=" + 21491/368);
		System.out.println("21491/365=" + 21491/365);
		System.out.println("21493/368=" + 21493/368);
		System.out.println("21493/359=" + 21493/359);
		System.out.println("21493/368=" + 21493/368);
		System.out.println("21493/374=" + 21493/374);
		System.out.println("21494/365=" + 21494/365);
		System.out.println("21495/379=" + 21495/379);
		System.out.println("21496/374=" + 21496/374);
		System.out.println("21497/376=" + 21497/376);
		System.out.println("21498/368=" + 21498/368);
		System.out.println("21500/368=" + 21500/368);
		System.out.println("21500/362=" + 21500/362);
		System.out.println("21501/362=" + 21501/362);
		System.out.println("21501/362=" + 21501/362);
		System.out.println("21502/362=" + 21502/362);
		System.out.println("21503/374=" + 21503/374);
		System.out.println("21503/382=" + 21503/382);
		System.out.println("21504/374=" + 21504/374);
		System.out.println("21504/362=" + 21504/362);
		System.out.println("21506/359=" + 21506/359);
		System.out.println("21506/371=" + 21506/371);
		System.out.println("21509/368=" + 21509/368);
		System.out.println("21509/376=" + 21509/376);
		System.out.println("21512/368=" + 21512/368);
		System.out.println("21514/371=" + 21514/371);
		System.out.println("21514/376=" + 21514/376);
		System.out.println("21514/362=" + 21514/362);
		System.out.println("21515/359=" + 21515/359);
		System.out.println("21519/371=" + 21519/371);
		System.out.println("21520/371=" + 21520/371);
		System.out.println("21522/374=" + 21522/374);
		System.out.println("21525/362=" + 21525/362);
		System.out.println("21526/365=" + 21526/365);
		System.out.println("21527/365=" + 21527/365);
		System.out.println("21528/365=" + 21528/365);
		System.out.println("21528/365=" + 21528/365);
		System.out.println("21531/371=" + 21531/371);
		System.out.println("21532/371=" + 21532/371);
		System.out.println("21540/371=" + 21540/371);
		System.out.println("21545/368=" + 21545/368);
		System.out.println("21545/362=" + 21545/362);
		System.out.println("21546/376=" + 21546/376);
		System.out.println("21547/371=" + 21547/371);
		System.out.println("21548/362=" + 21548/362);
		System.out.println("21548/368=" + 21548/368);
		System.out.println("21549/371=" + 21549/371);
		System.out.println("21549/368=" + 21549/368);
		System.out.println("21549/362=" + 21549/362);
		System.out.println("21550/371=" + 21550/371);
		System.out.println("21550/368=" + 21550/368);
		System.out.println("21552/371=" + 21552/371);
		System.out.println("21552/376=" + 21552/376);
		System.out.println("21554/368=" + 21554/368);
		System.out.println("21554/376=" + 21554/376);
		System.out.println("21554/376=" + 21554/376);
		System.out.println("21557/371=" + 21557/371);
		System.out.println("21559/376=" + 21559/376);
		System.out.println("21560/371=" + 21560/371);
		System.out.println("21560/371=" + 21560/371);
		System.out.println("21560/371=" + 21560/371);
		System.out.println("21561/368=" + 21561/368);
		System.out.println("21566/359=" + 21566/359);
		System.out.println("21568/362=" + 21568/362);
		System.out.println("21573/385=" + 21573/385);
		System.out.println("21573/368=" + 21573/368);
		System.out.println("21574/362=" + 21574/362);
		System.out.println("21574/374=" + 21574/374);
		System.out.println("21575/368=" + 21575/368);
		System.out.println("21576/371=" + 21576/371);
		System.out.println("21580/365=" + 21580/365);
		System.out.println("21581/374=" + 21581/374);
		System.out.println("21582/368=" + 21582/368);
		System.out.println("21582/362=" + 21582/362);
		System.out.println("21584/382=" + 21584/382);
		System.out.println("21585/368=" + 21585/368);
		System.out.println("21585/368=" + 21585/368);
		System.out.println("21587/371=" + 21587/371);
		System.out.println("21588/382=" + 21588/382);
		System.out.println("21588/368=" + 21588/368);
		System.out.println("21590/368=" + 21590/368);
		System.out.println("21590/359=" + 21590/359);
		System.out.println("21593/376=" + 21593/376);
		System.out.println("21593/382=" + 21593/382);
		System.out.println("21594/368=" + 21594/368);
		System.out.println("21594/374=" + 21594/374);
		System.out.println("21594/371=" + 21594/371);
		System.out.println("21595/362=" + 21595/362);
		System.out.println("21597/379=" + 21597/379);
		System.out.println("21597/374=" + 21597/374);
		System.out.println("21598/374=" + 21598/374);
		System.out.println("21599/371=" + 21599/371);
		System.out.println("21601/388=" + 21601/388);
		System.out.println("21604/376=" + 21604/376);
		System.out.println("21605/368=" + 21605/368);
		System.out.println("21606/382=" + 21606/382);
		System.out.println("21608/371=" + 21608/371);
		System.out.println("21610/365=" + 21610/365);
		System.out.println("21611/374=" + 21611/374);
		System.out.println("21612/391=" + 21612/391);
		System.out.println("21612/379=" + 21612/379);
		System.out.println("21613/374=" + 21613/374);
		System.out.println("21614/365=" + 21614/365);
		System.out.println("21615/371=" + 21615/371);
		System.out.println("21616/376=" + 21616/376);
		System.out.println("21617/382=" + 21617/382);
		System.out.println("21617/382=" + 21617/382);
		System.out.println("21618/365=" + 21618/365);
		System.out.println("21618/374=" + 21618/374);
		System.out.println("21619/368=" + 21619/368);
		System.out.println("21626/374=" + 21626/374);
		System.out.println("21630/371=" + 21630/371);
		System.out.println("21630/385=" + 21630/385);
		System.out.println("21633/376=" + 21633/376);
		System.out.println("21635/368=" + 21635/368);
		System.out.println("21635/365=" + 21635/365);
		System.out.println("21636/374=" + 21636/374);
		System.out.println("21637/362=" + 21637/362);
		System.out.println("21638/365=" + 21638/365);
		System.out.println("21638/368=" + 21638/368);
		System.out.println("21638/376=" + 21638/376);
		System.out.println("21639/368=" + 21639/368);
		System.out.println("21640/374=" + 21640/374);
		System.out.println("21642/356=" + 21642/356);
		System.out.println("21642/368=" + 21642/368);
		System.out.println("21642/394=" + 21642/394);
		System.out.println("21643/374=" + 21643/374);
		System.out.println("21643/382=" + 21643/382);
		System.out.println("21645/374=" + 21645/374);
		System.out.println("21645/365=" + 21645/365);
		System.out.println("21646/376=" + 21646/376);
		System.out.println("21648/368=" + 21648/368);
		System.out.println("21649/376=" + 21649/376);
		System.out.println("21650/368=" + 21650/368);
		System.out.println("21650/362=" + 21650/362);
		System.out.println("21651/365=" + 21651/365);
		System.out.println("21652/374=" + 21652/374);
		System.out.println("21655/374=" + 21655/374);
		System.out.println("21657/374=" + 21657/374);
		System.out.println("21658/376=" + 21658/376);
		System.out.println("21659/365=" + 21659/365);
		System.out.println("21662/368=" + 21662/368);
		System.out.println("21663/368=" + 21663/368);
		System.out.println("21669/385=" + 21669/385);
		System.out.println("21669/365=" + 21669/365);
		System.out.println("21672/368=" + 21672/368);
		System.out.println("21673/376=" + 21673/376);
		System.out.println("21674/359=" + 21674/359);
		System.out.println("21676/368=" + 21676/368);
		System.out.println("21680/371=" + 21680/371);
		System.out.println("21681/368=" + 21681/368);
		System.out.println("21682/368=" + 21682/368);
		System.out.println("21687/359=" + 21687/359);
		System.out.println("21695/376=" + 21695/376);
		System.out.println("21695/362=" + 21695/362);
		System.out.println("21695/374=" + 21695/374);
		System.out.println("21700/368=" + 21700/368);
		System.out.println("21702/374=" + 21702/374);
		System.out.println("21703/385=" + 21703/385);
		System.out.println("21703/368=" + 21703/368);
		System.out.println("21708/365=" + 21708/365);
		System.out.println("21710/385=" + 21710/385);
		System.out.println("21710/368=" + 21710/368);
		System.out.println("21710/374=" + 21710/374);
		System.out.println("21713/368=" + 21713/368);
		System.out.println("21714/382=" + 21714/382);
		System.out.println("21717/371=" + 21717/371);
		System.out.println("21720/368=" + 21720/368);
		System.out.println("21721/391=" + 21721/391);
		System.out.println("21722/374=" + 21722/374);
		System.out.println("21722/371=" + 21722/371);
		System.out.println("21722/374=" + 21722/374);
		System.out.println("21722/376=" + 21722/376);
		System.out.println("21723/376=" + 21723/376);
		System.out.println("21727/371=" + 21727/371);
		System.out.println("21727/374=" + 21727/374);
		System.out.println("21728/368=" + 21728/368);
		System.out.println("21728/365=" + 21728/365);
		System.out.println("21728/379=" + 21728/379);
		System.out.println("21729/385=" + 21729/385);
		System.out.println("21729/365=" + 21729/365);
		System.out.println("21729/382=" + 21729/382);
		System.out.println("21730/379=" + 21730/379);
		System.out.println("21733/374=" + 21733/374);
		System.out.println("21735/371=" + 21735/371);
		System.out.println("21735/362=" + 21735/362);
		System.out.println("21736/371=" + 21736/371);
		System.out.println("21736/397=" + 21736/397);
		System.out.println("21737/365=" + 21737/365);
		System.out.println("21738/376=" + 21738/376);
		System.out.println("21740/374=" + 21740/374);
		System.out.println("21742/365=" + 21742/365);
		System.out.println("21744/371=" + 21744/371);
		System.out.println("21745/385=" + 21745/385);
		System.out.println("21750/379=" + 21750/379);
		System.out.println("21751/382=" + 21751/382);
		System.out.println("21751/368=" + 21751/368);
		System.out.println("21751/374=" + 21751/374);
		System.out.println("21753/371=" + 21753/371);
		System.out.println("21753/365=" + 21753/365);
		System.out.println("21753/400=" + 21753/400);
		System.out.println("21756/365=" + 21756/365);
		System.out.println("21758/394=" + 21758/394);
		System.out.println("21758/374=" + 21758/374);
		System.out.println("21758/362=" + 21758/362);
		System.out.println("21761/391=" + 21761/391);
		System.out.println("21769/368=" + 21769/368);
		System.out.println("21769/376=" + 21769/376);
		System.out.println("21771/365=" + 21771/365);
		System.out.println("21774/382=" + 21774/382);
		System.out.println("21775/388=" + 21775/388);
		System.out.println("21776/356=" + 21776/356);
		System.out.println("21777/359=" + 21777/359);
		System.out.println("21778/368=" + 21778/368);
		System.out.println("21781/376=" + 21781/376);
		System.out.println("21782/400=" + 21782/400);
		System.out.println("21788/379=" + 21788/379);
		System.out.println("21789/371=" + 21789/371);
		System.out.println("21790/379=" + 21790/379);
		System.out.println("21791/362=" + 21791/362);
		System.out.println("21791/385=" + 21791/385);
		System.out.println("21794/385=" + 21794/385);
		System.out.println("21796/379=" + 21796/379);
		System.out.println("21797/376=" + 21797/376);
		System.out.println("21800/374=" + 21800/374);
		System.out.println("21804/379=" + 21804/379);
		System.out.println("21804/376=" + 21804/376);
		System.out.println("21805/371=" + 21805/371);
		System.out.println("21807/368=" + 21807/368);
		System.out.println("21808/356=" + 21808/356);
		System.out.println("21808/371=" + 21808/371);
		System.out.println("21809/371=" + 21809/371);
		System.out.println("21810/382=" + 21810/382);
		System.out.println("21813/379=" + 21813/379);
		System.out.println("21815/391=" + 21815/391);
		System.out.println("21818/388=" + 21818/388);
		System.out.println("21821/388=" + 21821/388);
		System.out.println("21824/382=" + 21824/382);
		System.out.println("21825/374=" + 21825/374);
		System.out.println("21826/374=" + 21826/374);
		System.out.println("21826/371=" + 21826/371);
		System.out.println("21827/368=" + 21827/368);
		System.out.println("21830/368=" + 21830/368);
		System.out.println("21832/365=" + 21832/365);
		System.out.println("21833/365=" + 21833/365);
		System.out.println("21834/376=" + 21834/376);
		System.out.println("21837/374=" + 21837/374);
		System.out.println("21838/391=" + 21838/391);
		System.out.println("21839/397=" + 21839/397);
		System.out.println("21839/359=" + 21839/359);
		System.out.println("21843/379=" + 21843/379);
		System.out.println("21844/388=" + 21844/388);
		System.out.println("21845/368=" + 21845/368);
		System.out.println("21850/394=" + 21850/394);
		System.out.println("21851/365=" + 21851/365);
		System.out.println("21852/362=" + 21852/362);
		System.out.println("21852/374=" + 21852/374);
		System.out.println("21854/371=" + 21854/371);
		System.out.println("21857/371=" + 21857/371);
		System.out.println("21858/394=" + 21858/394);
		System.out.println("21859/394=" + 21859/394);
		System.out.println("21863/379=" + 21863/379);
		System.out.println("21863/365=" + 21863/365);
		System.out.println("21870/376=" + 21870/376);
		System.out.println("21870/382=" + 21870/382);
		System.out.println("21873/379=" + 21873/379);
		System.out.println("21873/382=" + 21873/382);
		System.out.println("21875/374=" + 21875/374);
		System.out.println("21880/397=" + 21880/397);
		System.out.println("21880/365=" + 21880/365);
		System.out.println("21881/385=" + 21881/385);
		System.out.println("21881/388=" + 21881/388);
		System.out.println("21885/376=" + 21885/376);
		System.out.println("21886/379=" + 21886/379);
		System.out.println("21886/379=" + 21886/379);
		System.out.println("21890/365=" + 21890/365);
		System.out.println("21890/385=" + 21890/385);
		System.out.println("21893/379=" + 21893/379);
		System.out.println("21899/403=" + 21899/403);
		System.out.println("21901/388=" + 21901/388);
		System.out.println("21902/403=" + 21902/403);
		System.out.println("21906/385=" + 21906/385);
		System.out.println("21908/379=" + 21908/379);
		System.out.println("21911/397=" + 21911/397);
		System.out.println("21917/385=" + 21917/385);
		System.out.println("21919/376=" + 21919/376);
		System.out.println("21922/394=" + 21922/394);
		System.out.println("21927/397=" + 21927/397);
		System.out.println("21927/376=" + 21927/376);
		System.out.println("21930/391=" + 21930/391);
		System.out.println("21934/394=" + 21934/394);
		System.out.println("21943/374=" + 21943/374);
		System.out.println("21946/388=" + 21946/388);
		System.out.println("21948/385=" + 21948/385);
		System.out.println("21948/394=" + 21948/394);
		System.out.println("21948/400=" + 21948/400);
		System.out.println("21948/388=" + 21948/388);
		System.out.println("21966/400=" + 21966/400);
		System.out.println("21968/397=" + 21968/397);
		System.out.println("21973/391=" + 21973/391);
		System.out.println("21980/394=" + 21980/394);
		System.out.println("21985/388=" + 21985/388);
		System.out.println("21988/376=" + 21988/376);
		System.out.println("21990/400=" + 21990/400);
		System.out.println("21991/365=" + 21991/365);
		System.out.println("21991/391=" + 21991/391);
		System.out.println("21991/374=" + 21991/374);
		System.out.println("21994/385=" + 21994/385);
		System.out.println("21997/379=" + 21997/379);
		System.out.println("21997/397=" + 21997/397);
		System.out.println("21998/391=" + 21998/391);
		System.out.println("21999/397=" + 21999/397);
		System.out.println("22003/382=" + 22003/382);
		System.out.println("22003/376=" + 22003/376);
		System.out.println("22004/394=" + 22004/394);
		System.out.println("22006/391=" + 22006/391);
		System.out.println("22011/406=" + 22011/406);
		System.out.println("22013/397=" + 22013/397);
		System.out.println("22015/376=" + 22015/376);
		System.out.println("22020/374=" + 22020/374);
		System.out.println("22023/374=" + 22023/374);
		System.out.println("22024/388=" + 22024/388);
		System.out.println("22030/374=" + 22030/374);
		System.out.println("22031/403=" + 22031/403);
		System.out.println("22033/391=" + 22033/391);
		System.out.println("22034/403=" + 22034/403);
		System.out.println("22038/400=" + 22038/400);
		System.out.println("22040/400=" + 22040/400);
		System.out.println("22040/406=" + 22040/406);
		System.out.println("22042/382=" + 22042/382);
		System.out.println("22043/388=" + 22043/388);
		System.out.println("22045/379=" + 22045/379);
		System.out.println("22047/385=" + 22047/385);
		System.out.println("22048/397=" + 22048/397);
		System.out.println("22050/388=" + 22050/388);
		System.out.println("22056/403=" + 22056/403);
		System.out.println("22056/376=" + 22056/376);
		System.out.println("22059/403=" + 22059/403);
		System.out.println("22063/412=" + 22063/412);
		System.out.println("22063/412=" + 22063/412);
		System.out.println("22068/382=" + 22068/382);
		System.out.println("22068/385=" + 22068/385);
		System.out.println("22068/400=" + 22068/400);
		System.out.println("22078/397=" + 22078/397);
		System.out.println("22085/403=" + 22085/403);
		System.out.println("22088/397=" + 22088/397);
		System.out.println("22089/409=" + 22089/409);
		System.out.println("22090/394=" + 22090/394);
		System.out.println("22093/400=" + 22093/400);
		System.out.println("22093/397=" + 22093/397);
		System.out.println("22095/394=" + 22095/394);
		System.out.println("22098/391=" + 22098/391);
		System.out.println("22101/374=" + 22101/374);
		System.out.println("22102/409=" + 22102/409);
		System.out.println("22104/400=" + 22104/400);
		System.out.println("22106/391=" + 22106/391);
		System.out.println("22112/397=" + 22112/397);
		System.out.println("22114/403=" + 22114/403);
		System.out.println("22122/394=" + 22122/394);
		System.out.println("22129/406=" + 22129/406);
		System.out.println("22130/397=" + 22130/397);
		System.out.println("22132/406=" + 22132/406);
		System.out.println("22139/409=" + 22139/409);
		System.out.println("22144/394=" + 22144/394);
		System.out.println("22144/385=" + 22144/385);
		System.out.println("22147/403=" + 22147/403);
		System.out.println("22148/382=" + 22148/382);
		System.out.println("22152/385=" + 22152/385);
		System.out.println("22154/394=" + 22154/394);
		System.out.println("22155/406=" + 22155/406);
		System.out.println("22160/400=" + 22160/400);
		System.out.println("22160/397=" + 22160/397);
		System.out.println("22162/391=" + 22162/391);
		System.out.println("22167/394=" + 22167/394);
		System.out.println("22169/412=" + 22169/412);
		System.out.println("22170/409=" + 22170/409);
		System.out.println("22174/385=" + 22174/385);
		System.out.println("22178/397=" + 22178/397);
		System.out.println("22179/406=" + 22179/406);
		System.out.println("22179/400=" + 22179/400);
		System.out.println("22180/412=" + 22180/412);
		System.out.println("22182/409=" + 22182/409);
		System.out.println("22187/403=" + 22187/403);
		System.out.println("22189/388=" + 22189/388);
		System.out.println("22191/403=" + 22191/403);
		System.out.println("22196/394=" + 22196/394);
		System.out.println("22199/403=" + 22199/403);
		System.out.println("22201/388=" + 22201/388);
		System.out.println("22204/397=" + 22204/397);
		System.out.println("22206/400=" + 22206/400);
		System.out.println("22212/403=" + 22212/403);
		System.out.println("22214/403=" + 22214/403);
		System.out.println("22215/397=" + 22215/397);
		System.out.println("22217/403=" + 22217/403);
		System.out.println("22218/388=" + 22218/388);
		System.out.println("22220/412=" + 22220/412);
		System.out.println("22222/397=" + 22222/397);
		System.out.println("22226/374=" + 22226/374);
		System.out.println("22227/403=" + 22227/403);
		System.out.println("22231/400=" + 22231/400);
		System.out.println("22231/385=" + 22231/385);
		System.out.println("22237/388=" + 22237/388);
		System.out.println("22238/406=" + 22238/406);
		System.out.println("22242/400=" + 22242/400);
		System.out.println("22242/382=" + 22242/382);
		System.out.println("22243/409=" + 22243/409);
		System.out.println("22255/409=" + 22255/409);
		System.out.println("22256/400=" + 22256/400);
		System.out.println("22264/385=" + 22264/385);
		System.out.println("22267/394=" + 22267/394);
		System.out.println("22272/397=" + 22272/397);
		System.out.println("22276/403=" + 22276/403);
		System.out.println("22281/403=" + 22281/403);
		System.out.println("22282/403=" + 22282/403);
		System.out.println("22288/409=" + 22288/409);
		System.out.println("22292/403=" + 22292/403);
		System.out.println("22294/409=" + 22294/409);
		System.out.println("22294/412=" + 22294/412);
		System.out.println("22298/400=" + 22298/400);
		System.out.println("22300/403=" + 22300/403);
		System.out.println("22301/406=" + 22301/406);
		System.out.println("22303/412=" + 22303/412);
		System.out.println("22311/406=" + 22311/406);
		System.out.println("22313/403=" + 22313/403);
		System.out.println("22316/403=" + 22316/403);
		System.out.println("22320/397=" + 22320/397);
		System.out.println("22321/397=" + 22321/397);
		System.out.println("22324/409=" + 22324/409);
		System.out.println("22326/400=" + 22326/400);
		System.out.println("22331/406=" + 22331/406);
		System.out.println("22332/403=" + 22332/403);
		System.out.println("22342/406=" + 22342/406);
		System.out.println("22346/406=" + 22346/406);
		System.out.println("22348/400=" + 22348/400);
		System.out.println("22349/415=" + 22349/415);
		System.out.println("22350/409=" + 22350/409);
		System.out.println("22351/412=" + 22351/412);
		System.out.println("22351/412=" + 22351/412);
		System.out.println("22356/418=" + 22356/418);
		System.out.println("22357/400=" + 22357/400);
		System.out.println("22358/397=" + 22358/397);
		System.out.println("22358/394=" + 22358/394);
		System.out.println("22359/409=" + 22359/409);
		System.out.println("22359/409=" + 22359/409);
		System.out.println("22381/397=" + 22381/397);
		System.out.println("22381/397=" + 22381/397);
		System.out.println("22386/412=" + 22386/412);
		System.out.println("22387/409=" + 22387/409);
		System.out.println("22387/418=" + 22387/418);
		System.out.println("22389/412=" + 22389/412);
		System.out.println("22399/406=" + 22399/406);
		System.out.println("22406/409=" + 22406/409);
		System.out.println("22406/403=" + 22406/403);
		System.out.println("22411/397=" + 22411/397);
		System.out.println("22417/400=" + 22417/400);
		System.out.println("22419/400=" + 22419/400);
		System.out.println("22420/394=" + 22420/394);
		System.out.println("22426/409=" + 22426/409);
		System.out.println("22427/400=" + 22427/400);
		System.out.println("22430/400=" + 22430/400);
		System.out.println("22431/421=" + 22431/421);
		System.out.println("22433/415=" + 22433/415);
		System.out.println("22433/400=" + 22433/400);
		System.out.println("22435/406=" + 22435/406);
		System.out.println("22440/388=" + 22440/388);
		System.out.println("22444/412=" + 22444/412);
		System.out.println("22444/397=" + 22444/397);
		System.out.println("22445/400=" + 22445/400);
		System.out.println("22445/415=" + 22445/415);
		System.out.println("22454/403=" + 22454/403);
		System.out.println("22462/400=" + 22462/400);
		System.out.println("22464/409=" + 22464/409);
		System.out.println("22468/415=" + 22468/415);
		System.out.println("22474/403=" + 22474/403);
		System.out.println("22475/412=" + 22475/412);
		System.out.println("22482/400=" + 22482/400);
		System.out.println("22482/409=" + 22482/409);
		System.out.println("22485/400=" + 22485/400);
		System.out.println("22498/406=" + 22498/406);
		System.out.println("22498/418=" + 22498/418);
		System.out.println("22508/400=" + 22508/400);
		System.out.println("22513/415=" + 22513/415);
		System.out.println("22520/424=" + 22520/424);
		System.out.println("22520/394=" + 22520/394);
		System.out.println("22526/400=" + 22526/400);
		System.out.println("22527/394=" + 22527/394);
		System.out.println("22528/421=" + 22528/421);
		System.out.println("22536/412=" + 22536/412);
		System.out.println("22550/406=" + 22550/406);
		System.out.println("22552/403=" + 22552/403);
		System.out.println("22554/409=" + 22554/409);
		System.out.println("22556/406=" + 22556/406);
		System.out.println("22556/412=" + 22556/412);
		System.out.println("22568/397=" + 22568/397);
		System.out.println("22575/409=" + 22575/409);
		System.out.println("22577/421=" + 22577/421);
		System.out.println("22593/403=" + 22593/403);
		System.out.println("22596/421=" + 22596/421);
		System.out.println("22598/412=" + 22598/412);
		System.out.println("22618/424=" + 22618/424);
		System.out.println("22625/432=" + 22625/432);
		System.out.println("22625/415=" + 22625/415);
		System.out.println("22630/418=" + 22630/418);
		System.out.println("22638/409=" + 22638/409);
		System.out.println("22638/409=" + 22638/409);
		System.out.println("22643/412=" + 22643/412);
		System.out.println("22665/406=" + 22665/406);
		System.out.println("22669/400=" + 22669/400);
		System.out.println("22702/424=" + 22702/424);
		System.out.println("22703/403=" + 22703/403);
		System.out.println("22715/415=" + 22715/415);
		System.out.println("22716/400=" + 22716/400);
		System.out.println("22753/406=" + 22753/406);
		System.out.println("22758/432=" + 22758/432);
		System.out.println("22759/412=" + 22759/412);
		System.out.println("22774/403=" + 22774/403);
		System.out.println("22818/415=" + 22818/415);
		System.out.println("22822/424=" + 22822/424);
		System.out.println("22830/426=" + 22830/426);
		System.out.println("22881/406=" + 22881/406);
		System.out.println("22892/426=" + 22892/426);
		System.out.println("22906/421=" + 22906/421);
		System.out.println("22982/435=" + 22982/435);
		System.out.println("22994/418=" + 22994/418);
		System.out.println("23024/435=" + 23024/435);
		System.out.println("23030/424=" + 23030/424);
		System.out.println("23102/441=" + 23102/441);
		System.out.println("23112/438=" + 23112/438);
		System.out.println("23116/415=" + 23116/415);
		System.out.println("23125/438=" + 23125/438);
		System.out.println("23200/450=" + 23200/450);
		System.out.println("23273/435=" + 23273/435);
		System.out.println("23303/444=" + 23303/444);
		System.out.println("23459/447=" + 23459/447);
		System.out.println("23463/450=" + 23463/450);
		
		
	}
}
