package com.zodiackillerciphers.old;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class CelebrityCypher {

	/** look for matching names in the given file */
	public static void scanNames(String filePath) {
		BufferedReader input = null;

		System.out.println("Scanning file [" + filePath + "]...");

		//Set<String> names = new HashSet<String>();

		try {
			input = new BufferedReader(new FileReader(new File(filePath)));
			String line = null; // not declared within while loop

			while ((line = input.readLine()) != null) {
				String[] split = line.split(" ");
				for (String s : split) {
					//if (names.contains(s)) continue;
					if (s.length() == 13) {
						//look for matches to "my name is" cipher symbol frequency
						if (MyNameIs.sameDist(s)) 
							System.out.println("MY NAME IS freq match: " + s + " in line: " + line);
					}
					if (s.length() == 10) {
						if (s.charAt(0) == s.charAt(6)) {
							if (s.charAt(3) == s.charAt(9)) {
								System.out.println("Found match [" + s + "] in line: " + line);
								//names.add(s);
							}
						}
					}
				}
			}
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (input != null) {
					input.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

	}

	public static void main(String[] args) {
		scanNames("/Users/doranchak/projects/work/java/zodiac/letters/names-and-corpus/fbnames/facebook-names-withcount.txt");
	}
}
