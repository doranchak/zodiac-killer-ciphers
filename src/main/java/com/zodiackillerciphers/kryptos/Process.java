package com.zodiackillerciphers.kryptos;

import java.util.List;
import java.util.Set;

import com.zodiackillerciphers.dictionary.Search;
import com.zodiackillerciphers.io.FileUtil;

public class Process {
	public static String K4 = "docs/kryptos/k4.txt";
	public static void process() {
		Set<String> dict = Search.dictionary();
		System.out.println(dict.contains("smegma"));
		List<String> lines = FileUtil.loadFrom(K4);
		int num = 0;
		for (String line : lines) {
			//System.out.println("Working on line " + line);
			String name = line.split(",")[0];
			String textForward = line.split(",")[1];
			String textReverse = "";
			for (int i=textForward.length()-1; i>=0; i--) textReverse += textForward.charAt(i);
			List<String> words = Search.search(dict, textForward, 4, 20);
			for (String word : words) {
				System.out.println(name+",forward,"+word.length()+","+word);
			}
			words = Search.search(dict, textReverse, 4, 20);
			for (String word : words) {
				System.out.println(name+",reverse,"+word.length()+","+word);
			}			
			num++;
		}
	}
	
	public static void main(String[] args) {
		process();
	}
}
