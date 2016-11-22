package com.zodiackillerciphers.corpus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/** fetch the contents of a corpus, translated into a stream that contains only the uppercase letters A-Z, ignoring any other symbols it finds. */  
public class Reader {
	
	/** return list of candidate files from the given directory and all its subdirectories */
	public static List<File> list(String startPath) {
		List<File> files = new ArrayList<File>();
		
		add(new File(startPath), files);
		
		System.out.println("Final list size: [" + files.size() + "]");
		return files;
	}
	
	/** add all .txt and .zip files found in the given directory, and in all descendent directories therein */
	public static void add(File dir, List<File> list) {
		System.out.println("Looking in [" + dir + "]...");
		File[] files = dir.listFiles();
		
		for (File file : files) {
			if (file.isDirectory()) add(file, list); // recursively dive into subdirectories
			else {
				String name = file.getName();
				if (name.toLowerCase().endsWith(".zip")) {
					list.add(file);
					System.out.println("Added " + dir + "/" + name);
				}
				else if (name.toLowerCase().endsWith(".txt")) {
					list.add(file);
					System.out.println("Added " + dir + "/" + name);
				}
				else System.out.println("Ignored " + dir + "/" + name);
			}
		}
	}
	
	public static void main(String[] args) {
		if (args == null || args.length != 1 || args[0].equals("")) {
			System.err.println("Usage: com.zodiackillerciphers.corpus.Reader [path]");
			System.exit(-1);
		}
		list(args[0]);
	}
	
}
