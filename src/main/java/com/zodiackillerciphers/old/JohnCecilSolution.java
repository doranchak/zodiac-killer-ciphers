package com.zodiackillerciphers.old;

import java.util.HashMap;
import java.util.Map;

import com.zodiackillerciphers.ciphers.Ciphers;

public class JohnCecilSolution {
	static String solution = "IWILLMAKETHISVERYCLEARMYONEREASONIAMKILLINGIFIDONTKILLIAMLIKELYTOOABANDONMYCHANCETOCOLLECTMYSLAVESFORTHEAFTERLIFEEVENWHENIKNOWYOUCANTFINDMEIAMNOTSAFEWALKINGIFIAMCOLLECTMY SLAVESWHENSLAVESYOUFOUNDSAVEDDEADINACARHECHANGEDHISMINDANDRAODFROMMEHEWALKEDBYWITHOUTHIDINGFROMAENOTHINGISSAFEHEWASHIDINGAKNIFEYOUEVENLISTENEDTOOMYSLAVEWASCALLINGFORHELP";
	
	static void analyze() {
		Map<Character, String> map = new HashMap<Character, String>();
		Map<Character, Integer> counts = new HashMap<Character, Integer>();
		
		String s1 = "";
		String s2 = "";
		for (int i=0; i<solution.length(); i++) {
			char ch = Ciphers.cipher[0].cipher.charAt(i);
			s1 += ch;
			char plain = solution.charAt(i);
			s2 += plain;
			String s = map.get(ch);
			if (s == null) s = "";
			if (s.indexOf(plain) < 0) s += plain; 
			map.put(ch, s);
			
			Integer count = counts.get(ch);
			if (count == null) count = 0;
			count++;
			counts.put(ch, count);
			
			System.out.println(s1);
			System.out.println(s2);
		}
		
		for (Character ch : map.keySet()) {
			System.out.println(ch+": " + map.get(ch) + ", " + counts.get(ch) + ", " + ((float)counts.get(ch)/map.get(ch).length()));
		}
		
		
	}
	
	public static void main(String[] args) {
		analyze();
	}
}
