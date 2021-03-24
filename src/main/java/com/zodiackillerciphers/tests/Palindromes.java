package com.zodiackillerciphers.tests;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class Palindromes {
	/** returns true if the given string is a palindrome */
	public static boolean isPalindrome(String s) {
		return s.equals(new StringBuffer(s).reverse().toString());
	}

	/** check given file for any palindromes */
	public static void search(String file) {
		BufferedReader input = null;
		int counter = 0;
		try {
			// input = new BufferedReader(new FileReader(new File(path)));
			input = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));

			String line = null; // not declared within while loop
			while ((line = input.readLine()) != null) {
				if (isPalindrome(line))
					System.out.println(line);
			}
			// System.out.println("read " + counter + " lines.");
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		try {
			input.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}
	
	public static void main(String[] args) {
		search("/Users/doranchak/projects/zodiac/palindrome-search.txt");
	}
}
