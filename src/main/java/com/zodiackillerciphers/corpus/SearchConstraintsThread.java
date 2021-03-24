package com.zodiackillerciphers.corpus;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.zodiackillerciphers.constraints.Info;
import com.zodiackillerciphers.constraints.TopHeap;
import com.zodiackillerciphers.io.FileUtil;

public class SearchConstraintsThread extends Thread {

	public File file;
	public String text;
	public TopHeap heap;
	public Map<Integer, List<Info>> map;
	public String cipher;
	public String solution;

	public SearchConstraintsThread(File file, String text, TopHeap heap, Map<Integer, List<Info>> map, String cipher, String solution) {
		super();
		this.file = file;
		this.text = text;
		this.heap = heap;
		this.map = map;
		this.cipher = cipher;
		this.solution = solution;
	}
	
	public void run() {
		// convert text to uppercase alphabet stream
		StringBuffer converted = FileUtil.convert(text);
		int p1 = 0;
		int p2 = 0;
		for (int i=0; i<converted.length(); i++) {
			//System.out.println(i);
			p2 = (int) (100 * ((float)i/converted.length()));
			if (p2 != p1) {
				System.out.println(i + " of " + converted.length() + " (" + p2 + " %) of file " + file.getAbsolutePath());
				p1 = p2;
			}
			for (Integer len : map.keySet()) {
				if (i+len > converted.length()) continue;
				List<Info> list = map.get(len);
				String substring = converted.substring(i, i+len); // putative plaintext
				//if (spurious(substring)) continue; // ignore putative solutions that have too many repeated plaintext letters
				for (Info info : list) {
					Info newInfo = new Info(info.substring, info.index);
					newInfo.plaintext = substring;
					newInfo.pairs = info.pairs;
					newInfo.probability = info.probability;
					if (newInfo.fit()) { // && newInfo.substring.equals("5+tL)l^R6HI9DR_TYr\\de/@XJQAP5M8RUt")) {
						newInfo.match = SearchConstraints.match(newInfo, substring, solution);
						newInfo.zkscore = SearchConstraints.score(newInfo, cipher);
						heap.add(newInfo);
						//System.out.println("Added " + newInfo);
						if (newInfo.match > 0.5) {
							System.out.println("Good match: " + newInfo);
						}
						//System.out.println("Constraint match: " + file.getAbsolutePath() + ", " + info + ", " + actual + ", " + match(info, substring, solution) + ", " + score(info, cipher));
					}
				}
			}
			
		}
		System.out.println("Done with thread for file " + file.getAbsolutePath());
	}

}
