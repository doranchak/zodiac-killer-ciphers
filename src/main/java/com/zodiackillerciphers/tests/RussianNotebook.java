package com.zodiackillerciphers.tests;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.zodiackillerciphers.io.FileUtil;

/**
 * http://scienceblogs.de/klausis-krypto-kolumne/2016/08/31/an-encrypted-
 * notebook-that-waits-to-be-solved/
 */
public class RussianNotebook {
	public static void test() {
		List<String> list = FileUtil
				.loadFrom("/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/dictionaries/russian-words.txt");

		/** search for pattern: ?A???AB?B */
		/*
		 * for (String word : list) { if (word.length() < 9) continue; char c1 =
		 * word.charAt(1); char c2 = word.charAt(5); char c3 = word.charAt(6);
		 * char c4 = word.charAt(8); if (c1 == c2 && c3 == c4) {
		 * System.out.println(word); } }
		 */
		for (String word : list) {
			/*
			 * if (word.length() < 5) continue; char c1 = word.charAt(0); char
			 * c2 = word.charAt(2); if (c1 == c2 && (c1 == 'п' || c1 == 'p')) {
			 * System.out.println(word); }
			 */
			if (word.length() < 7)
				continue;
			char c1 = word.charAt(3);
			char c2 = word.charAt(4);
			char c3 = word.charAt(5);
			char c4 = word.charAt(6);
			if (c1 == c3 && (c2 == 'п' || c2 == 'p') && (c4 == 'o' || c4 == 'п')) {
				System.out.println(word);
			}
		}
	}

	public static void main(String[] args) {
		test();
	}
}
