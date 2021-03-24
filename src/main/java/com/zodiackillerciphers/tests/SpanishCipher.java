package com.zodiackillerciphers.tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.stats.StatsWrapper;
import com.zodiackillerciphers.tests.unigrams.AnomalousDistances;
import com.zodiackillerciphers.transform.CipherTransformations;

/** CONFIDENTIAL */
public class SpanishCipher {
	static String cipherOriginal = "15 @ 2 w 5 % 2 w !! y 8 @ 8 16 @ 1 z !! 6 @ 4 z 10 @ 5 ! 1 @ 1 ! 6 ! z 9 @ 2 4 % 1 w !! 6 % 5 x y 14 % 3 !! 21 !! y 10 @ 6 ! 10 @ 3 y 16 % 1 z 7 @ 3 w 1 z % 1 !! 14 !! z 19 @ 2 z !! z 7 % 6 z 9 % 2 w !! 12 % 1 w ! 19 % 3 y !! 3 !! w 20 % 3 y 4 @ 1 w !! 15 % 2 y 20 % 5 ! 7 % 2 z 1 !! 10 @ 6 y !! 15 % 2 z 21 % 6 z !! 14 !! 1 @ 1 w 9 @ 1 w !! 3 @ 1 ! 11 @ 4 z 3 % 1 x w 12 % 1 !! 18 ! 15 !! 16 % 3 w 2 @ 1 z 5 @ 5 w 8 @ 8 !! 20 @ 1 y 3 % 2 z 17 % 2 !! 5 % 4 z 6 @ 1 w !! x 9 @ 2 z !! 8 @ 8 w 10 % 1 z !! 16 % 3 x 15 @ 2 z !! 21 !! z 10 @ 6 x !! 15 % 2 w 5 % 2 y 9 @ 6 !! 1 @ 2 y 5 @ 5 w 10 @ 5 ! 10 @ 7 ! 16 % 1 z 14 @ 1 !! 14 !! 10 @ 6 w 5 @ 5 w 20 % 5 !! 20 % 7 y 10 @ 5 ! 11 @ 5 w 12 % 13 10 @ 6 10 @ 6 w 10 % 1 z ! 10 @ 3 z 5 @ 4 z 3 % 2 y 20 % 4 !! 21 % 5 w 10 % 1 x 2 @ 1 z !! 3 % 1 w 11 % 1 w !! 6 @ 1 x 5 @ 5 w 8 @ 8 !! 20 % 4 y 10 @ 5 !! 11 % 2 w 20 % 4 !! 14 !! 20 % 10 v y 5 % 4 x 15 % 4 !! 5 @ 4 z 5 @ 11 !! 15 % 2 x y 10 @ 6 z 15 @ 1 !! w !! 16 % 3 y 20 % 5 2 @ 8 x 16 @ 1 x 10 @ 5 !! 14 !! 20 % 4 y z 5 @ 5 w 15 @ 1 !! 16 % 6 w 7 % 6 x 8 % 5 w ! 14 !! 10 % 1 z !! w 15 % 2 w 10 @ 5 ! 10 @ 7 v 12 % 1 x 5 % 2 z 2 @ 1 z 2 % 1 ! 10 @ 5 x 16 % 1 !! 16 % 3 v y 10 @ 5 ! 20 % 3 z 20 % 4 !! 20 % 9 w !! 10 % 1 z 8 @ 8 !! 21 % 4 y 5 @ 6 y 18 % 1 w 10 @ 6 !! 14 !! 20 % 4 y 11 @ 4 z 16 % 1 !! 1 @ 1 w 15 % 2 !! 5 % 3 w 11 @ 5 z 19 % 3 !! 15 % 2 y 15 % 1 y 5 @ 7 z 17 % 1 !! 19 % 3 x !! 15 % 4 w ! 18 % 2 y 19 % 4 !! 15 % 2 y 9 @ 2 18 @ 8 ! 20 % 3 y 25 % 9 ! 19 % 6 z 20 % 5 z !! 19 % 9 z 12 % 1 ! 19 % 2 y 8 @ 3 y 7 @ 8 !! z !! 9 @ 2 v y 10 @ 6 ! 20 % 3 ! 20 % 5 z !! 10 % 8 z ! 22 z ! 20 !! 14 !! w 30 % 9 !! 3 !! 20 % 9 w 8 @ 8 w 20 % 3 ! 19 % 4 w 20 % 4 2 @ 1 y 10 @ 3 y 12 % 1 4 % 1 y !! 10 @ 6 y 10 @ 5 !! 5 @ 4 w 3 % 1 z 10 % 1 y 17 % 1 !! 20 !! 14 !! 19 % 3 y !! w 15 @ 6 y !! 3 !! 5 @ 4 w 19 % 3 !! 20 % 10 w 4 % 3 8 @ 2 x y 12 % 1 ! 16 @ 1 w 10 @ 6 !! 3 !! z 1 x !! z 2 % 1 z 5 @ 2 w !! 20 !! y 10 @ 6 w !! 10 @ 6 z 5 % 4 y 11 % 1 w 17 % 1 !! 14 !! z !! 10 % 1 z !! 3 @ 2 y 10 @ 1 ! 20 % 3 y !! 14 !! 12 % 1 w ! 20 % 4 !! y 10 @ 6 ! 15 @ 2 z 6 @ 4 w 11 @ 5 !! 2 @ 1 x 10 @ 5 x 8 % 1 x y 12 % 1 ! 4 % 1 w !! 10 @ 6 w 14 % 3 !! x 1 % 1 y 12 % 1 ! 11 @ 6 y !! 20 !! 14 !! 16 @ 1 y 10 @ 1 y 6 @ 4 w 8 @ 8 !! y 11 % 2 !! y 9 % 2 y 11 ! 1 ! 12 @ 1 w !! 3 !! 11 % 2 z 17 % 1 !! w 2 % 1 y 3 @ 4 z 19 % 3 !! 20 !! z 13 @ 4 x !! 15 % 2 v y 3 @ 1 y 9 @ 2 !! 17 % 1 y 7 % 2 x 20 % 5 !! v 11 @ 5 ! 7 @ 10 y 2 @ 1 v 17 % 1 !! 20 !! 14 !! 10 % 1 z ! z ! 1 @ 1 z 13 % 2 ! 20 % 3 z 2 @ 1 z ! 27 % 6 z !! 19 % 8 y !! 4 % 1 x w !! z 7 @ 7 x !! 5 % 4 z 21 z 10 @ 3 w 9 @ 2 ! 4 @ 1 z 12 % 1 !! 3 !! 4 % 2 ! 8 % 2 w 16 % 2 y !! z !! 9 z 10 % 5 y 12 % 1 ! 10 @ 7 y !! 14 !! 8 @ 8 z 10 % 1 y !! z !! 3 % 1 ! 7 @ 2 x 6 @ 5 x 5 % 3 z !! 20 !! 11 % 2 z 10 @ 6 !! 15 % 4 v 3 % 2 y 20 % 4 !! 20 !! 14 !! z !! x !! 15 @ 1 y !! 3 % 2 z !! z !! 16 % 3 y 20 % 5 z 19 % 4 20 % 3 w 5 % 2 w !! 10 @ 4 y 10 @ 3 y 4 @ 6 w 17 % 1 !! 5 @ 5 w 6 % 3 x 6 % 2 x 6 % 4 z 2 @ 1 w !! z 14 @ 2 x !! 5 % 3 w 8 @ 2 w y 19 % 3 ! 20 % 3 z !! 10 % 1 y 11 @ 6 ! 17 % 2 z !! 20 % 3 z 12 % 2 ! 3 % 2 w y 12 % 1 !! 6 @ 3 w 14 @ 2 !! 10 % 1 w 1 @ 1 w 15 @ 1 !! 2 @ 1 y 12 % 3 !! 17 % 1 v 20 % 5 !! 1 !! y 17 @ 9 w 20 % 12 !! y 8 @ 8 ! 11 @ 6 z 12 %";
	static String cipherWithMergedTokens = "15@2 w 5%2 w-y 8@8 16@1 z-6@4 z 10@5 1@1 6 z 9@2 4%1 w-6%5 x y 14%3-21-y 10@6 10@3 y 16%1 z 7@3 w 1 z%1-14-z 19@2 z-z 7%6 z 9%2 w-12%1 w 19%3 y-3-w 20%3 y 4@1 w-15%2 y 20%5 7%2 z 1-10@6 y-15%2 z 21%6 z-14-1@1 w 9@1 w-3@1 11@4 z 3%1 x w 12%1-18 15-16%3 w 2@1 z 5@5 w 8@8-20@1 y 3%2 z 17%2-5%4 z 6@1 w-x 9@2 z-8@8 w 10%1 z-16%3 x 15@2 z-21-z 10@6 x-15%2 w 5%2 y 9@6-1@2 y 5@5 w 10@5 10@7 16%1 z 14@1-14-10@6 w 5@5 w 20%5-20%7 y 10@5 11@5 w 12%13 10@6 10@6 w 10%1 z 10@3 z 5@4 z 3%2 y 20%4-21%5 w 10%1 x 2@1 z-3%1 w 11%1 w-6@1 x 5@5 w 8@8-20%4 y 10@5-11%2 w 20%4-14-20%10 v y 5%4 x 15%4-5@4 z 5@11-15%2 x y 10@6 z 15@1-w-16%3 y 20%5 2@8 x 16@1 x 10@5-14-20%4 y z 5@5 w 15@1-16%6 w 7%6 x 8%5 w 14-10%1 z-w 15%2 w 10@5 10@7 v 12%1 x 5%2 z 2@1 z 2%1 10@5 x 16%1-16%3 v y 10@5 20%3 z 20%4-20%9 w-10%1 z 8@8-21%4 y 5@6 y 18%1 w 10@6-14-20%4 y 11@4 z 16%1-1@1 w 15%2-5%3 w 11@5 z 19%3-15%2 y 15%1 y 5@7 z 17%1-19%3 x-15%4 w 18%2 y 19%4-15%2 y 9@2 18@8 20%3 y 25%9 19%6 z 20%5 z-19%9 z 12%1 19%2 y 8@3 y 7@8-z-9@2 v y 10@6 20%3 20%5 z-10%8 z 22 z 20-14-w 30%9-3-20%9 w 8@8 w 20%3 19%4 w 20%4 2@1 y 10@3 y 12%1 4%1 y-10@6 y 10@5-5@4 w 3%1 z 10%1 y 17%1-20-14-19%3 y-w 15@6 y-3-5@4 w 19%3-20%10 w 4%3 8@2 x y 12%1 16@1 w 10@6-3-z 1 x-z 2%1 z 5@2 w-20-y 10@6 w-10@6 z 5%4 y 11%1 w 17%1-14-z-10%1 z-3@2 y 10@1 20%3 y-14-12%1 w 20%4-y 10@6 15@2 z 6@4 w 11@5-2@1 x 10@5 x 8%1 x y 12%1 4%1 w-10@6 w 14%3-x 1%1 y 12%1 11@6 y-20-14-16@1 y 10@1 y 6@4 w 8@8-y 11%2-y 9%2 y 11 1 12@1 w-3-11%2 z 17%1-w 2%1 y 3@4 z 19%3-20-z 13@4 x-15%2 v y 3@1 y 9@2-17%1 y 7%2 x 20%5-v 11@5 7@10 y 2@1 v 17%1-20-14-10%1 z z 1@1 z 13%2 20%3 z 2@1 z 27%6 z-19%8 y-4%1 x w-z 7@7 x-5%4 z 21 z 10@3 w 9@2 4@1 z 12%1-3-4%2 8%2 w 16%2 y-z-9 z 10%5 y 12%1 10@7 y-14-8@8 z 10%1 y-z-3%1 7@2 x 6@5 x 5%3 z-20-11%2 z 10@6-15%4 v 3%2 y 20%4-20-14-z-x-15@1 y-3%2 z-z-16%3 y 20%5 z 19%4 20%3 w 5%2 w-10@4 y 10@3 y 4@6 w 17%1-5@5 w 6%3 x 6%2 x 6%4 z 2@1 w-z 14@2 x-5%3 w 8@2 w y 19%3 20%3 z-10%1 y 11@6 17%2 z-20%3 z 12%2 3%2 w y 12%1-6@3 w 14@2-10%1 w 1@1 w 15@1-2@1 y 12%3-17%1 v 20%5-1-y 17@9 w 20%12-y 8@8 11@6 z 12 %";
	static String cipherWithMergedTokensNoSeparators = "15@2 w 5%2 w y 8@8 16@1 z 6@4 z 10@5 1@1 6 z 9@2 4%1 w 6%5 x y 14%3 21 y 10@6 10@3 y 16%1 z 7@3 w 1 z%1 14 z 19@2 z z 7%6 z 9%2 w 12%1 w 19%3 y 3 w 20%3 y 4@1 w 15%2 y 20%5 7%2 z 1 10@6 y 15%2 z 21%6 z 14 1@1 w 9@1 w 3@1 11@4 z 3%1 x w 12%1 18 15 16%3 w 2@1 z 5@5 w 8@8 20@1 y 3%2 z 17%2 5%4 z 6@1 w x 9@2 z 8@8 w 10%1 z 16%3 x 15@2 z 21 z 10@6 x 15%2 w 5%2 y 9@6 1@2 y 5@5 w 10@5 10@7 16%1 z 14@1 14 10@6 w 5@5 w 20%5 20%7 y 10@5 11@5 w 12%13 10@6 10@6 w 10%1 z 10@3 z 5@4 z 3%2 y 20%4 21%5 w 10%1 x 2@1 z 3%1 w 11%1 w 6@1 x 5@5 w 8@8 20%4 y 10@5 11%2 w 20%4 14 20%10 v y 5%4 x 15%4 5@4 z 5@11 15%2 x y 10@6 z 15@1 w 16%3 y 20%5 2@8 x 16@1 x 10@5 14 20%4 y z 5@5 w 15@1 16%6 w 7%6 x 8%5 w 14 10%1 z w 15%2 w 10@5 10@7 v 12%1 x 5%2 z 2@1 z 2%1 10@5 x 16%1 16%3 v y 10@5 20%3 z 20%4 20%9 w 10%1 z 8@8 21%4 y 5@6 y 18%1 w 10@6 14 20%4 y 11@4 z 16%1 1@1 w 15%2 5%3 w 11@5 z 19%3 15%2 y 15%1 y 5@7 z 17%1 19%3 x 15%4 w 18%2 y 19%4 15%2 y 9@2 18@8 20%3 y 25%9 19%6 z 20%5 z 19%9 z 12%1 19%2 y 8@3 y 7@8 z 9@2 v y 10@6 20%3 20%5 z 10%8 z 22 z 20 14 w 30%9 3 20%9 w 8@8 w 20%3 19%4 w 20%4 2@1 y 10@3 y 12%1 4%1 y 10@6 y 10@5 5@4 w 3%1 z 10%1 y 17%1 20 14 19%3 y w 15@6 y 3 5@4 w 19%3 20%10 w 4%3 8@2 x y 12%1 16@1 w 10@6 3 z 1 x z 2%1 z 5@2 w 20 y 10@6 w 10@6 z 5%4 y 11%1 w 17%1 14 z 10%1 z 3@2 y 10@1 20%3 y 14 12%1 w 20%4 y 10@6 15@2 z 6@4 w 11@5 2@1 x 10@5 x 8%1 x y 12%1 4%1 w 10@6 w 14%3 x 1%1 y 12%1 11@6 y 20 14 16@1 y 10@1 y 6@4 w 8@8 y 11%2 y 9%2 y 11 1 12@1 w 3 11%2 z 17%1 w 2%1 y 3@4 z 19%3 20 z 13@4 x 15%2 v y 3@1 y 9@2 17%1 y 7%2 x 20%5 v 11@5 7@10 y 2@1 v 17%1 20 14 10%1 z z 1@1 z 13%2 20%3 z 2@1 z 27%6 z 19%8 y 4%1 x w z 7@7 x 5%4 z 21 z 10@3 w 9@2 4@1 z 12%1 3 4%2 8%2 w 16%2 y z 9 z 10%5 y 12%1 10@7 y 14 8@8 z 10%1 y z 3%1 7@2 x 6@5 x 5%3 z 20 11%2 z 10@6 15%4 v 3%2 y 20%4 20 14 z x 15@1 y 3%2 z z 16%3 y 20%5 z 19%4 20%3 w 5%2 w 10@4 y 10@3 y 4@6 w 17%1 5@5 w 6%3 x 6%2 x 6%4 z 2@1 w z 14@2 x 5%3 w 8@2 w y 19%3 20%3 z 10%1 y 11@6 17%2 z 20%3 z 12%2 3%2 w y 12%1 6@3 w 14@2 10%1 w 1@1 w 15@1 2@1 y 12%3 17%1 v 20%5 1 y 17@9 w 20%12 y 8@8 11@6 z 12 %";
	static String[] cipherUnits = cipherOriginal.split(" ");
	static Map<String, String> toNormalized;
	static Map<String, String> fromNormalized;
	static {
		toNormalized = new HashMap<String, String>();
		fromNormalized = new HashMap<String, String>();
		int which = 0;
		for (String unit : cipherUnits) {
			if (toNormalized.containsKey(unit)) continue;
			String val = Ciphers.alphabet[0].substring(which, which+1);
			which++;
			toNormalized.put(unit, val);
			fromNormalized.put(val, unit);
		}
		
	}
	static Map<String, String> key;
	static {
		key = new HashMap<String, String>();
		key.put("1", "S"); // mine
		key.put("6","H");
		key.put("14","A");
		key.put("21","Y");
		
		key.put("1@1","C");
		key.put("1@2","D");
		key.put("2@1","D"); // mine
		key.put("2@8","M"); // mine
		key.put("3@1","F"); // mine
		key.put("3@2","G"); // mine
		key.put("3@4","J"); // mine
		key.put("4@1","G"); // mine
		key.put("4@6","M"); // mine
		key.put("5@2","J"); // mine
		key.put("5@4","L"); // mine
		key.put("5@5","M"); // mine
		key.put("5@6","N"); // mine
		key.put("5@7","P"); // mine
		key.put("5@11","T"); // mine
		key.put("6@1","J");
		key.put("6@3","L");
		key.put("6@4","M");
		key.put("6@5","N");
		key.put("7@2","L");
		key.put("7@3","M"); // mine
		key.put("7@7","Q"); // mine
		key.put("7@8","R"); // mine
		key.put("7@10","T"); // mine
		key.put("8@2","M"); 
		key.put("8@3","N"); 
		key.put("8@8","S");
		key.put("9@1","M"); // mine
		key.put("9@2","N");
		key.put("9@6","R"); // P O D E 9@6   poder
		key.put("10@1","N"); // mine
		key.put("10@3","P"); // mine
		key.put("10@4","Q"); // mine
		key.put("10@5","R");
		key.put("10@6","S"); // 20%10 O 4%3 8@2 I E N T O 10@6 doscientos
		key.put("10@7","T"); // mine
		
//		key.put("10@8","T");
		key.put("11@4","R"); // mine
		key.put("11@5","S"); // mine
		key.put("11@6","T"); // mine
		key.put("12@1","P"); // mine
		key.put("13@4","T"); // mine
		key.put("14@1","R"); // mine
		key.put("14@2","S"); // mine
		key.put("15@1","S"); // mine
		key.put("15@2","T");
		key.put("15@6","Y");
		key.put("16@1","T");
		key.put("17@9","V");
		key.put("19@2","Y");

		key.put("2%1", "B"); // mine
		key.put("3%1", "C"); // mine
		key.put("3%2", "B"); // mine
		key.put("4%1","D");
		key.put("4%2","F"); // ???
		key.put("4%3","B"); // ???   20%10 O 4%3 8@2 I E N T O 10@6 doscientos
		key.put("5%2","D");
		key.put("5%3","C"); // ???
		key.put("5%4","B");
		key.put("6%1","G"); // next logical
		key.put("6%2","F"); // M O 6%3 I 6%2 I 6%4 A D O
		key.put("6%3","D"); // M O 6%3 I 6%2 I 6%4 A D O
		key.put("6%4","C"); // M O 6%3 I 6%2 I 6%4 A D O
		key.put("6%5","B");
		key.put("7%2","G");
		key.put("7%6","B");
		key.put("8%1","J");
		key.put("8%2","H");
		key.put("8%5","D");
		key.put("9%2","J");
		key.put("10%1", "L");
		key.put("10%5", "G");
		key.put("10%8", "C");
		key.put("11%1","M"); // mine
		key.put("11%2","L"); // ???
		key.put("12%1","N"); // mine
		key.put("12%2","M"); // mine
		key.put("12%3","L"); // mine
		key.put("13%2","N"); // mine
		key.put("14%3","N");
		key.put("15%1", "Q"); // mine
		key.put("15%2", "P"); // ???
		key.put("15%4", "N"); // ???
		key.put("16%1", "R"); // mine
		key.put("16%2", "Q"); // mine
		key.put("16%3", "P"); // mine
		key.put("16%6", "L"); // mine
		key.put("17%1", "S"); // mine
		key.put("17%2", "R"); // mine
		key.put("18%1", "T"); // mine
		key.put("18%2", "S"); // mine
		key.put("19%3", "T"); // mine
		key.put("19%3", "S"); // mine
		key.put("19%4", "R"); // mine
		key.put("19%6", "P"); // mine
		key.put("19%8", "M"); // mine
		key.put("19%9", "L"); // mine
		key.put("20%3", "T"); // mine
		key.put("20%4", "S"); // mine
		key.put("20%5", "R"); // mine
		key.put("20%7", "P"); // mine
		key.put("20%9", "M"); // mine
		key.put("20%10", "L"); // 20%10 O 4%3 8@2 I E N T O 10@6 doscientos
		key.put("20%12", "J"); // mine
		key.put("21%4", "T"); // mine
		key.put("21%5", "S"); // mine
		key.put("21%6", "R"); // mine

		key.put("v","U");
		key.put("w","O");
		key.put("x","I");
		key.put("y","E");
		key.put("z","A");	
		/*

o 15%2 o r 10@7 u 12%1 i d a 2@1 a 2%1 r i 16%1  opportunidad
t e 10@1 e m o s  tenemos
20%9 O S O 20%3 19%4 O 20%4 2@1 E 10@3 E 12%1 D E  nosotros depende?
16%3 E 20%5 2@8 I T I R permitir
E 10@6 10@3 E 16%1 A 7@3 O 1 z%1  emperadora
P O D A 5@5 O S   podamos

		 */
		
	}
	
	
	static String toNormalized(String[] units) {
		StringBuffer result = new StringBuffer();
		if (units == null) return result.toString();
		for (String unit : units) 
			result.append(toNormalized.get(unit));
		return result.toString();
	}
	static String[] fromNormalized(String norm) {
		String[] result = new String[norm.length()];
		for (int i=0; i<norm.length(); i++) {
			String c = norm.substring(i, i+1);
			result[i] = fromNormalized.get(c);
		}
		return result;
	}
	
	/** What tends to precede and follow each special unit? */
	static void contactAnalysis() {
		Map<String, Integer> bigrams = ngrams(cipherUnits, 2);
		for (String key : bigrams.keySet()) {
			Integer val = bigrams.get(key);
			System.out.println(key + " " + val);
		}
	}

	/** count ngrams and compare them to shuffles. */
//	public static void shuffleNgramsFull(int n, int shuffles) {
//		String[] copy = new String[cipherUnits.length];
//		for (int i = 0; i < cipherUnits.length; i++)
//			copy[i] = cipherUnits[i];
//
//		Map<String, StatsWrapper> stats = new HashMap<String, StatsWrapper>();
//		Map<String, Integer> ngrams = ngrams(cipherUnits, n);
//		int[] alpha = new int[n];
//		init(alpha, stats, 0);
//		// set the actual (observed) values
//		for (String key : ngrams.keySet()) {
//			stats.get(key).actual = ngrams.get(key);
//		}
//		
//		for (int i=0; i<shuffles; i++) {
//			if (i % 1000 == 0) System.out.println(i+"...");
//			CipherTransformations.shuffle(copy);
////			System.out.println(Arrays.toString(copy));
//			Map<String, Integer> ngramsShuffle = ngrams(copy, n);
//			
//			alpha = new int[n];
//			update(alpha, stats, ngramsShuffle, 0);
//			
//			
//			for (String key : ngramsShuffle.keySet()) {
////				System.out.println("["+key+"]");
//				stats.get(key).addValue(ngramsShuffle.get(key));
//			}
//		}
//		for (String key : stats.keySet()) {
////			System.out.println(key);
//			stats.get(key).output();
//		}
//	}

	/** count ngrams and compare them to shuffles (simple totals and averages) */
	public static void shuffleNgrams(int n, int shuffles) {
		String[] copy = new String[cipherUnits.length];
		for (int i = 0; i < cipherUnits.length; i++)
			copy[i] = cipherUnits[i];

		Map<String, Integer> ngramsActual = ngrams(cipherUnits, n);
		Map<String, Long> ngramsShuffleTotals = new HashMap<String, Long>();
		
		Map<String, Integer> ngramsShuffle;
		for (int i=0; i<shuffles; i++) {
			if (i % 10000 == 0) System.out.println(i+"...");
			CipherTransformations.shuffle(copy);
//			System.out.println(Arrays.toString(copy));
			ngramsShuffle = ngrams(copy, n);
			for (String key : ngramsShuffle.keySet()) {
				Long val = ngramsShuffleTotals.get(key);
				if (val == null) val = 0l;
				val += ngramsShuffle.get(key);
				ngramsShuffleTotals.put(key, val);
			}
			
		}
		String tab = "	";
		for (String key : ngramsShuffleTotals.keySet()) {
//			System.out.println(key)
			Integer actual = ngramsActual.get(key);
			if (actual == null) actual = 0;			
			
			float avg = ngramsShuffleTotals.get(key);
			avg /= shuffles;
			float diff = actual - avg;
			System.out.println(diff + tab + actual + tab + avg + tab + key); 
		}
	}
	
	// recursive init of ngram array 
//	public static void init(int[] index, Map<String, StatsWrapper> stats, int which) {
//		if (which == index.length) {
////			System.out.println(Arrays.toString(index));
//			String key = "";
//			for (int i=0; i<index.length; i++) {
//				if (i>0) key += " ";
//				key += cipherAlphabet[index[i]];
//			}
////			System.out.println("["+key+"]");
//			StatsWrapper sw = new StatsWrapper();
//			sw.actual = 0;
//			sw.name = key;
//			stats.put(key, sw);
//			return;
//		}
//		for (int i=0; i<cipherAlphabet.length; i++) {
//			index[which] = i;
//			init(index, stats, which+1);
//			
//		}
//	}

	// recursive update of ngram array 
//	public static void update(int[] index, Map<String, StatsWrapper> stats, Map<String, Integer> ngrams, int which) {
//		if (which == index.length) {
////			System.out.println(Arrays.toString(index));
//			String key = "";
//			for (int i=0; i<index.length; i++) {
//				if (i>0) key += " ";
//				key += cipherAlphabet[index[i]];
//			}
////			System.out.println("["+key+"]");
//			StatsWrapper sw = stats.get(key);
//			Integer val = ngrams.get(key);
//			if (val == null) val = 0;
//			sw.addValue(val);
//			return;
//		}
//		for (int i=0; i<cipherAlphabet.length; i++) {
//			index[which] = i;
//			update(index, stats, ngrams, which+1);
//			
//		}
//	}
	
	/** generate ngrams from the given string array */
	public static Map<String, Integer> ngrams(String[] str, int n) {
		Map<String, Integer> ngrams = new HashMap<String, Integer>();
		for (int i = n - 1; i < str.length; i++) {
			StringBuffer key = new StringBuffer();
			for (int j = i - n + 1; j <= i; j++) {
				if (key.length() > 0)
					key.append(" ");
				key.append(str[j]);
			}
			String keyS = key.toString();
			Integer val = ngrams.get(keyS);
			if (val == null)
				val = 0;
			val++;
			ngrams.put(keyS, val);
		}
//		System.out.println(ngrams);
		return ngrams;
	}

	/** find doubles */
	public static void doubles() {
		Map<String, Integer> ngrams = ngrams(cipherUnits, 2);
		for (String key : ngrams.keySet()) {
			String[] split = key.split(" ");
			if (split[0].equals(split[1])) {
				System.out.println(ngrams.get(key) + "	" + key);
			}
		}
	}
	
	public static void solveSpanishHom() {
		// WordFrequencies.WORDS6 =
		// "/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/dictionaries/spanish-word-frequencies.txt";
		// WordFrequencies.SCRUB = true;
		// StringBuffer sb = new StringBuffer("OWEW YHFZ FZJ A ZIDW FXYN YJ JYPZGWQ ZSZ
		// ZGZIW LW SY WTYDW OYT DZJY OZUZ AWIW C KCXWL PWBZEWH TYCZQ EZFW XDZ HW JZ
		// FXOZ ZJX OWEYI AYEWJ J PZN JWE KEZLPYZNJY EWHOVLRWJ B JVYJO DZMWLBXCYJAXZ
		// YBWJ YKPZBZL XJAWLBWM");
		// findRepeats(
		// sb.toString(),
		// 1, 1);
		// solve("MWBLWAJX ZXAJYCXBLWMZD", "");
		// solve("KEZLPYZNJY", "");
		// solve("KEZLPYZNJY YKPZBZL", "");

		// V through Z are assumed to be vowels.
		StringBuffer cipher = new StringBuffer(
				"15 W 5 W-Y 8 6 Z-6 Z 10-1-Z 9 4 W-6 X Y 14-Y 10-10 Y 16 Z 7 W 17-Z 19 Z-Z 7 Z 9 W-12 W-19 Y-W 20 Y 4 W-15 Y 20-4 Z 10 Y-15 Z 21 Z-1 W 9 W-3-11 3 X W 12-16 W 2 Z 5 W 8-20 Y 3 Z 17-5 Z 6 W-X 4 Z-8 W-10 Z-6 X 15 Z-Z 10 X-15 W 5 Y 9-1 Y 5 W 10-10-16 Z 14-10 W 5-11 5 Z 12 16 Y Z 14 10 Y-5 W 8 15 V 12 18 W 10-2-10 V Y 10 15-4 Z 13 W 12 2 X 3 Y 10 1 X Z-Y 2 W 10-Y 11 16 Z 2 Z 12-X 10 1 W 12 2 W 13");
		// StringBuffer cipher = new StringBuffer("N A C I D O E N U N A F A M I L I A B
		// U R G U E S A H U M I L D E S E C R I O E N L A L O C A L I D A D D E M E I D
		// L I N G C E R C A N A A V I E N A D O N D E C U R S O L O S E S T U D I O S A
		// N T E S D E I N G R E S A R E N L A U N I V E R S I D A D D E V I E N A R E A
		// L I Z O E S T U D I O S D E T E O L O G I A Y E N S E O R D E O R D E");
		StringBuffer rev1 = new StringBuffer(); // reverse consonants only
		StringBuffer rev2 = new StringBuffer(); // reverse vowels only
		StringBuffer rev3 = new StringBuffer(); // reverse both

		String[] words = cipher.toString().split("-");
		for (String word : words) {
			// System.out.println(word);
			String[] units = word.split(" ");

			if (rev1.length() > 0) {
				rev1.append("-");
				rev2.append("-");
				rev3.append("-");
			}

			rev1.append(SpanishCipher.reverse(units, false, true));
			rev2.append(SpanishCipher.reverse(units, true, false));
			rev3.append(SpanishCipher.reverse(units, true, true));
			StringBuffer add = new StringBuffer();
			for (int i = units.length - 1; i >= 0; i--) {
				if (add.length() > 0)
					add.append(" ");
				add.append(units[i]);
			}
		}
		System.out.println(cipher);
		System.out.println(rev1);
		System.out.println(rev2);
		System.out.println(rev3);

		cipher = new StringBuffer(cipher.toString().replaceAll("-", " "));
		Map<String, Integer> counts = new HashMap<String, Integer>();
		String[] split = cipher.toString().split(" ");
		System.out.println("split length " + split.length + ": " + Arrays.toString(split));
		for (int n = 2; n < 6; n++) {
			System.out.println("============== n = " + n);
			counts.clear();
			for (int i = 0; i < split.length - n + 1; i++) {
				String key = "";
				for (int j = 0; j < n; j++) {
					if (key.length() > 0)
						key += " ";
					key += split[i + j];
				}
				Integer val = counts.get(key);
				if (val == null)
					val = 0;
				val++;
				counts.put(key, val);
			}
			for (String key : counts.keySet()) {
				Integer val = counts.get(key);
				if (val < 2)
					continue;
				System.out.println(key + " " + val);
			}
		}
		System.out.println("==============");
		System.out.println("Original: ");
		SpanishCipher.reverseSpanishHom(false, false);
		System.out.println("Reverse all vowels: ");
		SpanishCipher.reverseSpanishHom(true, false);
		System.out.println("Reverse all consonants: ");
		SpanishCipher.reverseSpanishHom(false, true);
		System.out.println("Reverse both: ");
		SpanishCipher.reverseSpanishHom(true, true);
	}

	/** reverse all the vowels/consonants/both in place (cipher-level) */
	public static void reverseSpanishHom(boolean reverseVowels, boolean reverseConsonants) {
		String cipher = "15 W 5 W - Y 8 6 Z - 6 Z 10 - 1 - Z 9 4 W - 6 X Y 14 - Y 10 - 10 Y 16 Z 7 W 17 - Z 19 Z - Z 7 Z 9 W - 12 W - 19 Y - W 20 Y 4 W - 15 Y 20 - 4 Z 10 Y - 15 Z 21 Z - 1 W 9 W - 3 - 11 3 X W 12 - 16 W 2 Z 5 W 8 - 20 Y 3 Z 17 - 5 Z 6 W - X 4 Z - 8 W - 10 Z - 6 X 15 Z - Z 10 X - 15 W 5 Y 9 - 1 Y 5 W 10 - 10 - 16 Z 14 - 10 W 5 - 11 5 Z 12 16 Y Z 14 10 Y - 5 W 8 15 V 12 18 W 10 - 2 - 10 V Y 10 15 - 4 Z 13 W 12 2 X 3 Y 10 1 X Z - Y 2 W 10 - Y 11 16 Z 2 Z 12 - X 10 1 W 12 2 W 13";
		String[] units = cipher.split(" ");
		int vowelIndex = units.length;
		int consonantIndex = units.length;
		String result = "";
		for (int i = 0; i < units.length; i++) {
			if ("-".equals(units[i])) {
				result = SpanishCipher.add(result, units[i]);
				continue;
			}
			if (SpanishCipher.isVowel(units[i])) {
				if (reverseVowels) {
					vowelIndex = SpanishCipher.next(units, vowelIndex, true);
					result = SpanishCipher.add(result, units[vowelIndex]);
				} else {
					result = SpanishCipher.add(result, units[i]);
				}
			} else {
				if (reverseConsonants) {
					consonantIndex = SpanishCipher.next(units, consonantIndex, false);
					result = SpanishCipher.add(result, units[consonantIndex]);
				} else {
					result = SpanishCipher.add(result, units[i]);
				}
				// System.out.println("interim result: " + result);

			}
		}
		System.out.println(result);
	}

	public static String reverse(String[] units, boolean reverseVowels, boolean reverseConsonants) {
		List<String> consonants = new ArrayList<String>();
		List<String> vowels = new ArrayList<String>();
		for (String unit : units)
			if (SpanishCipher.isVowel(unit))
				vowels.add(unit);
			else
				consonants.add(unit);
		if (reverseVowels)
			Collections.reverse(vowels);
		if (reverseConsonants)
			Collections.reverse(consonants);
		String[] reversed = new String[units.length];
		int vowelIndex = 0;
		int consonantIndex = 0;
		for (int i = 0; i < units.length; i++) {
			if (SpanishCipher.isVowel(units[i]))
				reversed[i] = vowels.get(vowelIndex++);
			else
				reversed[i] = consonants.get(consonantIndex++);
		}
		StringBuffer revsb = new StringBuffer();
		for (String rev : reversed) {
			if (revsb.length() > 0) {
				revsb.append(" ");
			}
			revsb.append(rev);
		}
		return revsb.toString();
	}

	public static boolean isVowel(String str) {
		if ("V".equals(str))
			return true;
		if ("W".equals(str))
			return true;
		if ("X".equals(str))
			return true;
		if ("Y".equals(str))
			return true;
		if ("Z".equals(str))
			return true;
		return false;
	}

	public static String add(String str, String toAdd) {
		if (str.length() > 0)
			str += " ";
		str += toAdd;
		return str;
	}

	public static int next(String[] units, int current, boolean vowel) {
		while (current > 0) {
			current--;
			if (units[current].equals("-"))
				continue;
			if (vowel) {
				if (isVowel(units[current]))
					return current;
			} else {
				if (!isVowel(units[current]))
					return current;
			}
		}
		return -1;
	}
	
	// find a pair of sequences {s1, s2} of length L such that:
	// 1) first unit of s1 equals first unit of s2
	// 2) last unit of s1 equals last unit of s2
	// 3) there are exactly DIFF differences between the other corresponding units
	public static void findMatches(String[] units, int L, int DIFF) {
		for (int i=0; i<units.length-L+1; i++) {
			for (int j=i+L; j<units.length-L+1; j++) {
				if (findMatchesCriteria(cipherUnits, L, DIFF, i, j)) {
					String s1 = toString(cipherUnits, i, L);
					String s2 = toString(cipherUnits, j, L);
					System.out.println(s1 + " - " + s2);
				}
			}
		}
	}
	public static boolean findMatchesCriteria(String[] units, int L, int DIFF, int index1, int index2) {
		if (!units[index1].equals(units[index2])) return false;
		if (!units[index1+L-1].equals(units[index2+L-1])) return false;
//		String test = "";
		int errors = 0;
		for (int i=1; i<L; i++) {
			if (!units[index1+i].equals(units[index2+i])) errors++;
//			test += index1 + " " + index2 + " " + units[index1+i] + " " + units[index2+i] + ", ";
			if (errors > DIFF) {
				return false;
			}
		}
		return errors == DIFF;
	}
	
	// return sequence of units of length L as a string
	public static String toString(String[] units, int index, int L) {
		StringBuffer sb = new StringBuffer();
		for (int i=0; i<L; i++) {
			if (i > 0) sb.append(" ");
			sb.append(units[index+i]);
		}
		return sb.toString();
	}
	
	public static void testNormalization() {
		System.out.println(toNormalized);
		System.out.println(fromNormalized);
		String norm = toNormalized(cipherUnits);
		String[] original = fromNormalized(norm);
		System.out.println("norm: " + norm);
		System.out.println("from norm: " + Arrays.toString(original));
		original = fromNormalized("GFH");
		System.out.println("pattern " + Arrays.toString(original));
		
	}
	
	public static void testMergedTokens() {
		String[] words = cipherWithMergedTokens.split("-");
		System.out.println(Arrays.toString(words));
		String norm = "";
		Map<String, Integer> map = new HashMap<String, Integer>();
		int number = 0;
		for (String unit : cipherWithMergedTokensNoSeparators.split(" ")) {
			Integer val = map.get(unit);
			if (val == null) {
				val = number++;
				map.put(unit, val);
			}
			norm += val + " ";
		}
		System.out.println(norm);
		
	}
	
	public static void applyKey() {
		String[] words = cipherWithMergedTokens.split("-");
		String full = "";
		for (String word : words) {
			String out = "";
			String out2 = "";
			String[] tokens = word.split(" ");
			for (String token : tokens) {
				String decoded = key.get(token);
				if (decoded == null) {
					out += token;
					out2 += "e"; // filler
				}
				else {
					out += decoded.toLowerCase();
					out2 += decoded.toLowerCase();
				}
			}
			System.out.println(out + "                " + word);
			full += out2 + " ";
		}
//		System.out.println("==== @ ====");
//		dumpKeyGrid("@");
//		System.out.println("==== % ====");
//		dumpKeyGrid("%");
		System.out.println(full);
	}
	public static void dumpKeyGrid(String symbol) {
		
		for (int row=1; row<31; row++) {
			String out = "";
			for (int col=1; col<31; col++) {
				
				String unit = row+symbol+col;
				String val = key.get(unit);
//				System.out.println("unit " + unit + " val " + val);
				if (val != null) out += val;
				out += "	";
			}
			System.out.println(out);
		}
	}
	
	
	public static void main(String[] args) {
//		contactAnalysis();
//		shuffleNgrams(5, 100000);
//		System.out.println(cipherNormalized);
//		testNormalization();
//		doubles();
//		AnomalousDistances.anomalousGaps(toNormalized(cipherUnits), 100000);
		//findMatches(cipherUnits, 12, 1);
//		testMergedTokens();
		applyKey();
	}

}
