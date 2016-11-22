package com.zodiackillerciphers.ciphers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zodiackillerciphers.io.FileUtil;

public class AlbanyLetter {
	public static String s0 = "CN";
	public static String s1 = "GHNO";
	public static String s2 = "GHNO";
	public static String s3 = "DIRST";
	public static String s3b = "EC";
	public static String s4 = "DRST";
	public static String s5 = "GHNO";
	
	static Map<String, List<String>> namesMap;
	
	public static void initNames() {
		namesMap = new HashMap<String, List<String>>();
		
		List<String> names = FileUtil.loadFrom("/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/census-names/dist.female.first.txt");
		for (String name : names) {
			String prefix = name.substring(0,3);
			List<String> val = namesMap.get(prefix);
			if (val == null) val = new ArrayList<String>();
			val.add(name.split(" ")[0]);
			namesMap.put(prefix, val);
		}
		names = FileUtil.loadFrom("/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/census-names/dist.male.first.txt");
		for (String name : names) {
			String prefix = name.substring(0,3);
			List<String> val = namesMap.get(prefix);
			if (val == null) val = new ArrayList<String>();
			val.add(name.split(" ")[0]);
			namesMap.put(prefix, val);
		}
		System.out.println(namesMap.size());
	}
	public static void go() {
		
		initNames();
		
		for (int a0=0; a0<s0.length(); a0++) {
			for (int a1=0; a1<s1.length(); a1++) {
				for (int a2=0; a2<s2.length(); a2++) {
					for (int a3=0; a3<s3.length(); a3++) {
						for (int a3b=0; a3b<s3b.length(); a3b++) {
							for (int a4=0; a4<s4.length(); a4++) {
								for (int a5=0; a5<s5.length(); a5++) {
									char c0 = s0.charAt(a0);
									char c1 = s1.charAt(a1);
									char c2 = s2.charAt(a2);
									char c3 = s3.charAt(a3);
									char c3b = s3b.charAt(a3b);
									char c4 = s4.charAt(a4);
									char c5 = s5.charAt(a5);
									
									String name = "" + c0 + c1 + c2 + "_" + c3 + c3b + c4 + c5 + "ENLY";
									String match = match(name);
									if (match != null && match.length() > 0)
										System.out.println(name + " " + match(name));
								}
							}
						}
					}
				}
			}
			
		}
	}
	
	public static String match(String name) {
		String result = "";
		List<String> val = namesMap.get(name.substring(0,3));
		if (val == null) return result;
		if (val.isEmpty()) return result;
		//System.out.println(val.size());
		
		for (String n : val) {
			if (match(name, n)) result += n + " ";
		}
		if (!"".equals(result)) return "Match: " + result;
		return result;
	}
	
	public static boolean match(String cipherName, String censusName) {
		//System.out.println("[" + cipherName + "] [" + censusName + "]");
		if (censusName.length() < 5) return true;
		for (int i=4; i<censusName.length() && i<cipherName.length(); i++) {
			if (censusName.charAt(i) != cipherName.charAt(i)) return false;
		}
		return true;
	}
	
	public static void main(String[] args) {
		go();
		//System.out.println(match("CON_IEDGENLY","CONNIE"));
	}
}
