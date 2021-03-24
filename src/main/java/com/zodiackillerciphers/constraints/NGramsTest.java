package com.zodiackillerciphers.constraints;

import java.util.List;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.corpus.SearchConstraints;
import com.zodiackillerciphers.io.FileUtil;

public class NGramsTest {
	
	public static String[] substrings = new String[] {
		"LALALLALALLALALLALALLATRALLALALLALALLALALLALALLALL"	
	};
	public static void test1() {
		String cipher = Ciphers.cipher[1].cipher;

		List<String> list = FileUtil.loadFrom("docs/test.txt");
		
		for (String line : list) {
			String[] split = line.split(",");
			int pos = Integer.valueOf(split[1].substring(1));
			String sub = split[2].substring(1);
			String plain = split[3].substring(1);
			Info info1 = new Info(sub, pos);
			info1.plaintext = plain;
			System.out.println(SearchConstraints.score(info1, cipher) + ", " + info1);
		}
	}
	
	public static void main(String[] args) {
		test1();
	}
}
