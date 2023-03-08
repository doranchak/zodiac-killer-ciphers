package com.zodiackillerciphers.tests;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zodiackillerciphers.dictionary.WordFrequencies;
import com.zodiackillerciphers.io.FileUtil;
import com.zodiackillerciphers.lucene.NGramsCSRA;

/** process Sam's "hieroglyph" results */
public class SamHieroglyph {
	public static String tab = "	";
	public static void process(String folder) {
		NGramsCSRA.OPTIONAL_PREFIX = "/Users/doranchak/projects/zodiac/zkdecrypto/zkdecrypto/language/";
		NGramsCSRA.init("EN");
		
		File folderFile = new File(folder);
		File[] files = folderFile.listFiles();
		for (File file : files) {
			if (file.getName().endsWith(".txt")) {
				List<String> list = FileUtil.loadFrom(file.getAbsolutePath());
				for (String line : list) {
					String[] split = line.split(",");
					String str = split[0];
					double zk = NGramsCSRA.zkscore(new StringBuffer(str), "EN", false);
					System.out.println(zk + "	" + str);
				}
			}
		}
	}
	/** output all found words */
	public static void process2(String folder) {
		WordFrequencies.init();
		File folderFile = new File(folder);
		File[] files = folderFile.listFiles();
		Map<String, Integer> wordCounts = new HashMap<String, Integer>(); 
		for (File file : files) {
			if (file.getName().endsWith(".txt")) {
				List<String> list = FileUtil.loadFrom(file.getAbsolutePath());
				for (String line : list) {
					String[] split = line.split(",");
					String str = split[0];
					for (int i=0; i<str.length(); i++) {
						for (int j=i+4; j<=str.length(); j++) {
							String sub = str.substring(i,j);
							if (WordFrequencies.hasPrefix(sub)) {
								if (WordFrequencies.hasWord(sub)) {
									Integer val = wordCounts.get(sub);
									if (val == null) {
										val = 0;
									}
									val++;
									wordCounts.put(sub, val);
								}
							} else break;
						}
					}
				}
			}
		}
		for (String word : wordCounts.keySet()) {
			System.out.println(word.length() + tab + wordCounts.get(word) + tab + word + tab + WordFrequencies.percentile(word));
		}
	}
	public static void main(String[] args) {
		//process("/Users/doranchak/Downloads/enumerations");
		process2("/Users/doranchak/Downloads/enumerations");
	}
}
