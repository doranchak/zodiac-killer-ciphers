package com.zodiackillerciphers.corpus;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.zodiackillerciphers.io.FileUtil;

public class Search {
	/**
	 * search entire contents of a corpus directory, looking for matches in the
	 * given Map of substrings. count the matches. print the results, but
	 * suppress if the count for a substring exceeds some maximum. when printing
	 * results, include 1) name of corpus that matched, 2) list of zodiac
	 * correspondences that matched
	 */
	public static void search(String dirCorpus, String dirZodiac, String dirTmp, int minSubstringLength, int maxSubstringLength) {

		MapBean bean = Processor.generateMap(dirZodiac, maxSubstringLength, 1);
		for (String key : bean.mapCounts.keySet()) {
			//System.out.println("Map: " + key + ", " + map.get(key));
			bean.mapCounts.put(key, 0); // reset counts since we are going to count corpus matches against Zodiac substrings
		}
		
		makeUnzipDir(dirTmp+"/unzipped");
		
		List<File> files = Reader.list(dirCorpus);
		System.out.println("Number of files to process: " + files.size());
		long length = 0;
		String text;
		for (int f=0; f<files.size(); f++) {
			float p = 100*((float)f)/files.size();
			System.out.println(f + " of " + files.size() + " (" + (int) p + "%)");
			File file = files.get(f);
			if (file.getName().toLowerCase().endsWith("_h.zip")) {
				continue; // ignoring HTML zips 
			}
			try {
				text = read(file, dirTmp);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
			if (text == null) continue;
			length += text.length();
			// convert text to uppercase alphabet stream
			StringBuffer converted = FileUtil.convert(text);
			for (int i=0; i<converted.length(); i++) {
				for (int j=i; j<converted.length(); j++) {
					String key = converted.substring(i,j+1);
					//System.out.println("key " + key);
					Integer val = bean.mapCounts.get(key);
					if (val == null) break;
					val++; bean.mapCounts.put(key, val);
					if (key.length() >= minSubstringLength && val < 100) {
						System.out.println("Substring match: " + (j-i+1) + ", " + file.getAbsolutePath() + ", " + key + ", " + val);
					}
				}
			}
		}
		
		System.out.println("Done processing files.  Total text length: " + length);

	}
	
	public static String read(File file, String dirTmp) {
		System.out.println("Search.read " + file.getAbsolutePath());
		if (file.getName().toLowerCase().endsWith(".zip")) {
			return Zip.loadFrom(file, dirTmp);
		} else if (file.getName().toLowerCase().endsWith(".txt")) {
			return FileUtil.loadSBFrom(file).toString();
		} else {
			System.out.println("Not handling this file: [" + file.getAbsolutePath() + "]");
			return null;
		}
		
	}
	public static void makeUnzipDir(String path) {
		new File(path).mkdir();
	}
	

	public static void main(String[] args) {
		search(args[0], args[1], args[2], Integer.valueOf(args[3]), Integer.valueOf(args[4]));
	}
}
