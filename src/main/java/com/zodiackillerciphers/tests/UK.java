package com.zodiackillerciphers.tests;

import java.util.HashMap;
import java.util.Map;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.dictionary.WordFrequencies;
import com.zodiackillerciphers.web.WebtoySymbols;

/** UKSpyCatcher tests */
public class UK {
	public static void test() {
		for (int a=1; a<26; a++) {
			for (int b=1; b<26; b++) {
				for (int c=1;c<26; c++) {
					System.out.println(a + " " + b + " " + c + " " + c + " " + a + " " + b);
				}
			}
		}
			
	}
	/** https://www.evernote.com/l/AAH35deqs29CappY2HhvMzgI_LY3gu5pwJE */
	public static void test2() {
		Map<Character, Character> map = new HashMap<Character, Character>();
		map.put('&','P');
		map.put('(','O');
		map.put(')','O');
		map.put('+','T');
		map.put('1','O');
		map.put('2','O');
		map.put('3','O');
		map.put('4','O');
		map.put('5','O');
		map.put('6','O');
		map.put(':','I');
		map.put(';','I');
		map.put('^','V');
		map.put('b','B');
		map.put('c','C');
		map.put('d','D');
		map.put('f','F');
		map.put('j','J');
		map.put('k','K');
		map.put('l','L');
		map.put('p','P');
		map.put('q','Q');
		map.put('t','T');
		map.put('y','Y');
		map.put('z','O');
		map.put('|','I');
		String alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		for (int i=0; i<alpha.length(); i++) map.put(alpha.charAt(i), alpha.charAt(i));
		
		String tab = "	";
		String cipher = Ciphers.cipher[0].cipher;
		WordFrequencies.init();
		for (String word : WordFrequencies.map.keySet()) {
			if (word.length() != 6) continue;
			for (int i=0; i<cipher.length()-5; i++) {
				String result = " ";
				String result2 = "";
				String sub = cipher.substring(i,i+6);
				String interpret = "";
				
				for (int j=0; j<6; j++) {
					char c1 = sub.charAt(j);
					char c2 = word.charAt(j);
					Character c3 = map.get(c1);
					if (c3 == null) {
						result += "0 ";
						result2 += "<span>0</span>";
						interpret += c1;
					}
					else {
						result += Math.abs(c3-c2) + " ";
						result2 += "<span>" + Math.abs(c3-c2) + "</span>";
						interpret += c3;
					}
				}
				int row = (i/17)+1;
				int col = (i%17)+1;
				if (result.contains(" 8 8 8") && result.contains(" 0 0 0 ")) {
					System.out.println(WordFrequencies.freq(word) + tab + "row " + row + " col " + col + tab + WebtoySymbols.toHtml(sub) + tab + WebtoySymbols.toHtml(interpret) + tab + WebtoySymbols.toHtml(word) + tab + result2 + "<br><br>");
				}
				
			}
		}
	}
	
	public static void main(String[] args) {
		test2();
	}

}
