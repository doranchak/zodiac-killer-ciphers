package com.zodiackillerciphers.ciphers.samples;

import java.io.File;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.io.FileUtil;

/** process ACA cons to use as input data for cipher type classifiers */
public class ProcessSamples {
	
	
	/** map of russ' original list.  used to merge digital cons with his info. */
	public static Map<String, String> map = new HashMap<String, String>();
	public static Set<String> seen = new HashSet<String>();
	static {
		List<String> list = FileUtil.loadFrom("/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/ciphers/classifier-samples/aca-cons/ciphers-russ-only.csv");
		for (String line : list) {
			if (line.startsWith("Source,Year,Issue")) continue;
			String[] split = line.split(",");
			String key = split[3] + "." + split[4];
			map.put(key, line);
		}
		//System.out.println(map);
	}
	
	
	public static void dump(String year, String name, String label, String cipher) {
		if (label == null && cipher == null) return;
		
		String lab1 = "";
		String lab2 = "";
		boolean part2 = false;
		for (int i=0; i<label.length(); i++) {
			char ch = label.charAt(i);
			if (ch == ' ' || ch == '.') {
				part2 = true;
			} else {
				if (part2) lab2 += ch;
				else lab1 += ch;
			}
		}
		String conv = convertLabel(lab1.replaceAll(":","").toUpperCase());
		String key = name + "." + conv;
		seen.add(key);
		boolean hit = map.containsKey(key);
		if (hit) {
			String[] split = map.get(key).split(",");
			System.out.println("Russ + digital con" + "	" + split[1] + "	"+ split[2] + "	" + split[3] + "	" + split[4] + "	" + split[5] + "	" + split[6] + "	" + split[7] + "	" + split[8] + "	" + cipher);  
			
		} else
			System.out.println("Digital cons" + "	" + year + "	" + "	" + name + "	" + conv + "	" + "	" + "	" + "	" + "	" + cipher);
		
		System.out.println("LABEL: " + name + ": " + label);
	}
	
	
	/** convert to Russ' format */
	
//	A-
//	AC-
//	P-
//	X-
//	E-
//	C-
//	CC-
//	
//	P-Sp-1 => PS1
//	X-Sp-1 => XS1
//	C-Sp-1 => CS1
	
	
	public static String convertLabel(String label) {
		String newLabel = label.replaceAll("-","");
		if (newLabel.startsWith("PSP") || newLabel.startsWith("XSP") || newLabel.startsWith("CSP")) {
			try {
				return newLabel.charAt(0) + "S" + newLabel.charAt(3);
			} catch (Exception e) {
				return newLabel;
			}
		}
		if (label.startsWith("A") || 
				label.startsWith("AC-") || 
				label.startsWith("P") || 
				label.startsWith("X") || 
				label.startsWith("E-") || 
				label.startsWith("C") || 
				label.startsWith("D") || 
				label.startsWith("H") || 
				label.startsWith("CC-"))
			return pad(label.replaceAll("-", ""));
		if (label.equals("X-SP1")) return "XS1";
		if (label.equals("X-SP2")) return "XS2";
		return label;
	}
	public static String pad(String str) {
		String prefix = "";
		String suffix = "";
		for (int i=0; i<str.length(); i++) {
			char c = str.charAt(i);
			if (Character.isDigit(c)) {
				suffix += c;
			} else prefix += c;
		}

//		System.out.println("BEFORE: str " + str + " prefix " + prefix + " suffix " + suffix);
		if (suffix.length() > 0) {
			if (prefix.equals("AC")) {
				if (Integer.valueOf(suffix) < 100)
					suffix = "0" + suffix;
				if (Integer.valueOf(suffix) < 10)
					suffix = "0" + suffix;
			} else if (Integer.valueOf(suffix) < 10)
				suffix = "0" + suffix;
		}
//		System.out.println("AFTER: str " + str + " prefix " + prefix + " suffix " + suffix);
		return prefix + suffix;
	}
	
	public static void process(String folder) {
		File dir = new File(folder);
		for (File file : dir.listFiles()) {
			if (file.getName().endsWith(".txt")) {
				//System.out.println("File: " + file.getName());
				
				String year = file.getName().substring(2,4);
				if (year.startsWith("0") || year.startsWith("1")) year = "20" + year;
				else year = "19" + year;
				
				List<String> list = FileUtil.loadFrom(file.getAbsolutePath());
				String cipher = null;
				String label = null;
				for (int i=0; i<list.size(); i++) {
					String line = list.get(i);
					if (i==0) {
						cipher = null; label = null;
					}
					if (line.replaceAll(" ","").length() == 0) {
						dump(year, file.getName().substring(0,4).toUpperCase(), label, cipher);
						cipher = null; label = null;
						continue;
					}
					if (cipher == null && label == null) {
						label = line;
					} else if (cipher == null) {
						cipher = line;
					} else cipher += " " + line;
				}
				dump(year, file.getName().substring(0,4).toUpperCase(), label, cipher);
			}
		}
		for (String key : map.keySet()) {
			if (seen.contains(key)) continue;
			System.out.println(map.get(key).replaceAll(",", "	"));
			
		}
	}
	
	static void processHints(String path) {
		List<String> lines = FileUtil.loadFrom(path);
		for (String line : lines) {
			String[] split = line.split("	");
			String cipher = "";
			String clue = "";
			String original = "";
			String con = split[4];
			if (split.length > 9) {
				cipher = split[9];
				original = cipher;
			}
			if (cipher.contains("(")) {
				clue = cipher.substring(cipher.indexOf("("));
				cipher = cipher.substring(0,cipher.indexOf("("));
			} 
			if (con.startsWith("C")) {
				System.out.println(original);
				continue;
			}
			System.out.println(cipher + "	" + clue + "	" + original);
		}
	}
	
	static void processTypes(String path) {
		List<String> lines = FileUtil.loadFrom(path);
		for (String line : lines) {
			String[] split = line.split("	");
			String con = split[4];
			String type = typeFrom(con);
			System.out.println(split[0] + "	" + split[1] + "	" + split[2] + "	" + split[3] + "	" + split[4] + "	" + "	" + "	" + "	" + type);
		}
	}
	
	static String typeFrom(String con) {
		if (con == null || con.length() == 0) return "";
		if (con.charAt(0) == 'A' && Character.isDigit(con.charAt(1))
				&& Character.isDigit(con.charAt(2)))
			return "Aristocrat";
		if (con.charAt(0) == 'C' && Character.isDigit(con.charAt(1))
				&& Character.isDigit(con.charAt(2)))
			return "Cryptarithm";
		if (con.charAt(0) == 'P' && Character.isDigit(con.charAt(1))
				&& Character.isDigit(con.charAt(2)))
			return "Patristocrat";
		if (con.charAt(0) == 'P' && Character.isDigit(con.charAt(1))
				&& Character.isDigit(con.charAt(2)))
			return "Xenocrypt";
		return "";
	}

	
	public static void main(String[] args) {
		//process("/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/ciphers/classifier-samples/aca-cons/preprocessed");
		processTypes("/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/ciphers/classifier-samples/aca-cons/tab");
		//processHints("/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/ciphers/classifier-samples/aca-cons/ciphers-cons-merged-with-russ.tab");
		//System.out.println("A-B-C".replaceAll("-",""));
		//System.out.println(convertLabel("X-Sp-1.").toUpperCase());
	}
}
