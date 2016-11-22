package com.zodiackillerciphers.dictionary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* find solutions for the Word Wheel puzzle that I got from Charleston */
public class WordWheelPuzzle {
	public static void search() {
		WordFrequencies.init();
		long maxscore = Long.MIN_VALUE;
		String alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		for (int a = 0; a < alpha.length(); a++) {
			for (int b = 0; b < alpha.length(); b++) {
				for (int c = 0; c < alpha.length(); c++) {
					for (int d = 0; d < alpha.length(); d++) {
						
						List<String> words = new ArrayList<String>();
						long score = 0;
						
						for (int offset = 0; offset < 25; offset++) {
							
							boolean found = false;
							String word = "" + alpha.charAt((a + offset) % 26)
									+ alpha.charAt((b + offset) % 26)
									+ alpha.charAt((c + offset) % 26)
									+ alpha.charAt((d + offset) % 26);
							
							int freq = WordFrequencies.freq(word);
							if (freq > 100) {
								words.add(word);
								score+=freq;
								found = true;
							}
							
							if (!found) {
								StringBuffer sb = new StringBuffer(word);
								word = sb.reverse().toString();
								freq = WordFrequencies.freq(word);
								if (freq > 100) {
									words.add(word);
									score+=freq;
								}
							}
							
						}
						if (words.size() > 1) {
							Collections.sort(words);
							System.out.println(words.size() + " " + score + " " + words);
							maxscore = score;
						} 
						if (score > maxscore) {
							maxscore = score;
						}
					}

				}

			}

		}
	}
	/* a similar problem: http://www.reddit.com/r/codes/comments/1klnyu/so_i_have_a_four_letter_lock_that_ive_forgotten/ */
	public static void combinations() {
		WordFrequencies.init();
		String[] combos = new String[] {
				"LRPSMTWHBD",
				"OURYEILTNA",
				"SITALEMNCO",
				"TLDASKNEPY"
		};
		
		for (int a=0; a<combos[0].length(); a++) {
			for (int b=0; b<combos[0].length(); b++) {
				for (int c=0; c<combos[0].length(); c++) {
					for (int d=0; d<combos[0].length(); d++) {
						String word = "" + combos[0].charAt(a)+ combos[1].charAt(b) + combos[2].charAt(c) + combos[3].charAt(d);
						int freq = WordFrequencies.freq(word);
						if (freq > 0) System.out.println(freq + " " + word);
					}
					
				}
				
			}
			
		}
	}
	
	public static void main(String[] args) {
		//search();
		combinations();
	}
}
