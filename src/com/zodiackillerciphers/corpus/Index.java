package com.zodiackillerciphers.corpus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zodiackillerciphers.io.FileUtil;

/** class for generating index to map project gutenberg file names to bibliographic records */
public class Index {
	
	/** generate a map of filenames to bibliographic info, using the given index file */
	public static Map<String, IndexEntry> makeIndex(String indexFilePath) {
		Map<String, IndexEntry> map = new HashMap<String, IndexEntry>();
		List<String> lines = FileUtil.loadFrom(indexFilePath);
		for (String line : lines) {
			String key = line.split(":")[0];
			IndexEntry val = map.get(key);
			if (val == null) val = new IndexEntry();
			if (line.contains(" Author: ")) {
				val.author = line.substring(line.indexOf(":")+10);
			} else if (line.contains(" File: ")) {
				val.fileNames.add(line.substring(line.indexOf(":")+8));
			} else if (line.contains(" Title: ")) {
				val.title= line.substring(line.indexOf(":")+9);
			}
			map.put(key, val);
		}
		
		Map<String, IndexEntry> result = new HashMap<String, IndexEntry>();
		for (IndexEntry ind : map.values()) {
			for (String fileName : ind.fileNames) {
				result.put(fileName.toLowerCase(), ind);
			}
		}
		return result;
	}

	
	public static void test() {
		Map<String, IndexEntry> map = makeIndex("docs/corpus/gutenberg-index.txt");
		for (String key : map.keySet()) {
			IndexEntry val = map.get(key);
			System.out.println(key + ", " + val.title + ", " + val.author);
		}
	}
	
	public static void main(String[] args) {
		test();
	}

}
