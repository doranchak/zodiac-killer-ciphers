package com.zodiackillerciphers.tests;

import com.zodiackillerciphers.dictionary.WordFrequencies;

/** http://zodiackillersite.com/viewtopic.php?f=81&t=2617&p=40644#p40644 */
public class TwoPairPatterns {
	public static void test() {
		WordFrequencies.init();
		for (String word : WordFrequencies.map.keySet()) {
			if (word.length() < 4) continue;
			
			for (int a=0; a<word.length()-3; a++) {
				for (int b=a+1; b<word.length()-2; b++) {
					for (int c=b+1; c<word.length()-1; c++) {
						for (int d=c+1; d<word.length(); d++) {
							char c1 = word.charAt(a);
							char c2 = word.charAt(b);
							char c3 = word.charAt(c);
							char c4 = word.charAt(d);
							if (c1 == c3 && c2 == c4 || c1 == c4 && c2 == c3) {
								String str = "";
								for (int i=0; i<word.length(); i++) {
									if (i == a || i == b || i == c || i == d) {
										str += "<b><u>" + word.charAt(i) + "</u></b>";
									} else str += word.charAt(i);
								}
								System.out.println(WordFrequencies.freq(word) + " " + str + "<br>");
							}
						}
						
					}
				}
				
			}
		}
	}
	
	public static void main(String[] args) {
		test();
	}
}
