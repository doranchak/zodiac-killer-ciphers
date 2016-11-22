package com.zodiackillerciphers.ciphers.generator;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.zodiackillerciphers.io.FileUtil;

public class CandidatePlaintextLoader {
	
	public static List<CandidatePlaintext> candidates;
	/** list of candidate plaintexts previous explored, that we want to now avoid */
	public static Set<String> taboo;
	
	static { load(); }
	
	public static void load() {
		System.out.println("Loading taboo list...");
		loadTaboo();
		
		System.out.println("Loading candidate plaintexts...");
		File dir = new File(Generator.OUTPUT_PATH);
		File[] files = dir.listFiles();
		
		int count = 0;
		candidates = new ArrayList<CandidatePlaintext>();
		for (File file : files) {
			if (file.isFile() && file.getName().endsWith(".txt") && file.getName().startsWith("candidate")) {
				System.out.println("Loading " + file.getName());
				Long timeStart = new Date().getTime();
				CandidatePlaintext can = new CandidatePlaintext(file.getAbsolutePath());
				//System.out.println(can.index);
				if (taboo(can)) {
					System.out.println("Ignoring a taboo entry: " + can.plaintext);
					continue;
				}
				candidates.add(can);
				count++;
				
				Long timeEnd = new Date().getTime();
				System.out.println("Time to load: " + (timeEnd-timeStart) + " ms");
				
				//if (count % 100 == 0) System.out.println(count + " loaded...");
				//if (count > 1000) {
				//	System.out.println("Breaking early.");
				//	break;
				//}
			}
		}
		Collections.sort(candidates, new Comparator<CandidatePlaintext>() {
			public int compare(CandidatePlaintext o1, CandidatePlaintext o2) {
				return Integer.compare(o1.index, o2.index);
			}
		});
		
		System.out.println("Done loading " + candidates.size() + " candidate plaintexts");
	}
	public static boolean taboo(CandidatePlaintext can) {
		if (taboo == null) return false;
		return taboo.contains(can.plaintext);
	}
	
	
	public static void writeHTML() {
		System.out.println("Writing HTML...");
		for (CandidatePlaintext cp : candidates) {
			System.out.println(cp);
			Generator.writeHTML(cp);
		}
		System.out.println("Done writing HTML");
	}
	
	
	
	public static int size() { return candidates.size(); }
	public static CandidatePlaintext get(int i) {
		if (candidates == null) load();
		return candidates.get(i); }

	static void loadTaboo() {
		List<String> list = FileUtil.loadFrom(Stats.TABOO);
		taboo = new HashSet<String>();
		taboo.addAll(list);
		for (String line : list) System.out.println("Loaded taboo entry " + line);
	}
	
	public static int find(String plaintext) {
		int i = 0;
		for (CandidatePlaintext cp : candidates) {
			if (plaintext.equals(cp.plaintext)) return i;
			i++;
		}
		return -1;
	}
	
	public static void main(String[] args) {
		writeHTML();
	}

}
