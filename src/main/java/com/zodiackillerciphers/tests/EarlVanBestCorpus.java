package com.zodiackillerciphers.tests;

import java.util.List;

import com.zodiackillerciphers.io.FileUtil;

/** search for occurrences of EARL VAN BEST JUNIOR within large texts such as books */ 
public class EarlVanBestCorpus {
	public static void search(String file, String searchText) {
		int width = searchText.length();
		int height = 20;
		List<String> lines = FileUtil.loadFrom(file);
		String converted = FileUtil.convert(lines);
		//System.out.println(converted);
		for (int i=0; i<converted.length(); i++) {
//			System.out.println(i);
			int index = 0;
			int hits = 0;
			for (int j=0; j<searchText.length(); j++) {
				String column = "";
				for (int row=0; row<height; row++) {
//					System.out.println("i " + i + " j " + j + " row " + row + ", " + (i+width*row+j) + ", " + converted.charAt(i+width*row+j));
					column += converted.charAt(i+width*row+j);
				}
//				System.out.println(column);
				if (column.indexOf(searchText.charAt(index++)) == -1) {
//					System.out.println("break");
//					System.out.println(hits);
					break;
				} else hits++;
			}
			if (hits == searchText.length()) {
				System.out.println(i + " Match: " + converted.substring(i, i+width*height));
			}
		}
	}
	public static void main(String[] args) {
		search("/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/corpora-for-nickpelling-filter-test/pg30.txt", "EARLVANBESTJUNIOR");
	}
}

