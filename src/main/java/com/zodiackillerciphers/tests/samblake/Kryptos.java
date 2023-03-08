package com.zodiackillerciphers.tests.samblake;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zodiackillerciphers.dictionary.WordFrequencies;
import com.zodiackillerciphers.dictionary.WordFrequencies.WordBean;
import com.zodiackillerciphers.io.FileUtil;
import com.zodiackillerciphers.lucene.NGramsCSRA;

/** sam's kryptos experiments */
public class Kryptos {
	/** process enumerations to see if anything readable is already noticeable before running them through azdecrypt */
	public static void process(String folder) {
		
		NGramsCSRA.OPTIONAL_PREFIX = "/Users/doranchak/projects/zodiac/zkdecrypto/zkdecrypto/language/";
		NGramsCSRA.init("EN");
		
		File dir = new File(folder);
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isFile() && file.getName().endsWith("txt")) {
				List<String> list = FileUtil.loadFrom(file);
				for (String line : list) {
					if (line.isEmpty()) continue;
					float zks = NGramsCSRA.zkscore(new StringBuffer(line), "EN", false);
					System.out.println(zks + "	" + line + "	" + file.getName());
					line = new StringBuffer(line).reverse().toString();
					zks = NGramsCSRA.zkscore(new StringBuffer(line), "EN", false);
					System.out.println(zks + "	" + line + "	" + file.getName());
				}
			}
		}
	}
	/** output found words */
	public static void processWords(String path) {
		WordFrequencies.init();
		String tab = "	";
		int maxLength = 0;
		
		List<String> lines = FileUtil.loadFrom(path);
		Map<String, Integer> counts = new HashMap<String, Integer>(); 
		for (String line : lines) {
			if (line.isEmpty()) continue;
			String line2 = line.replaceAll(" ", "").replaceAll("\\.", "");
			System.out.println(line);
			System.out.println(line2);
			List<WordBean> words = WordFrequencies.findAllWordsInOrder(line, 4, -1);
			if (words != null) {
				for (WordBean word : words) {
					System.out.println(word.word.length() + tab + word.frequency + tab + word.word);
					if (word.word.length() > maxLength) maxLength = word.word.length();
					Integer count = counts.get(word.word);
					if (count == null) count = 0;
					count++;
					counts.put(word.word, count);
				}
			}
		}
		for (int L = maxLength; L >= 4; L--) {
			for (String key : counts.keySet()) {
				if (key.length() == L) {
					System.out.println(L + tab + key + tab + counts.get(key) + tab + WordFrequencies.freq(key));
				}
			}
		}
	}
	
	public static void main(String[] args) {
		//process("/Users/doranchak/projects/kryptos/sam-blake-enumerations/multistep");
		//process("/Users/doranchak/Downloads/k");
		processWords("/Users/doranchak/projects/ciphers/kryptos/new-experiment/output-combined-nospaces.txt");
		
	}
}
