package com.zodiackillerciphers.tests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SymbolGroupDistances {
	static Map<Integer, List<String>> map = new HashMap<Integer, List<String>>();
	
	public static void distances(String str) {
		System.out.println("====== For [" + str + "] ====== ");
		for (int i=0; i<str.length(); i++) {
			char ch1 = str.toUpperCase().charAt(i);
			for (int j=i+1; j<str.length(); j++) {
				char ch2 = str.charAt(j);
				
				int d1 = 0;
				
				if (ch2 > ch1) {
					d1 = ch2 - ch1;
				} else {
					d1 = ch1 - ch2;
				}
				if (d1 > 13) d1 = 26 - d1;
				System.out.println(ch1 + ", " + ch2 + ", " + d1);
			}
		}
	}
	public static void distances2(String p, String c) {
		System.out.println("====== DISTANCES2 [" + c + "] ====== ");
		
		char ch1 = p.charAt(0);
		for (int i=0; i<c.length(); i++) {
				char ch2 = c.charAt(i);
				
				int d1 = 0;
				int d2 = 0;
				
				if (ch2 > ch1) {
					d1 = ch2 - ch1;
				} else {
					d1 = ch1 - ch2;
				}
				if (d1 > 13) d1 = 26 - d1;
				System.out.println(ch1 + ", " + ch2 + ", " + d1);
				String s = ""+ch1+ch2;
				List<String> val = map.get(d1);
				if (val == null) val = new ArrayList<String>();
				val.add(s);
				map.put(d1, val);
		}
		
	}
	public static void main(String[] args) {
		distances("GSL");
		distances("ZPWONE");
		distances("JQ");
		distances("PUK");
		distances("OVD");
		distances("XITD");
		distances("RT");
		distances("FK");
		distances("HIOL");

		distances2("A","GSL");
		distances2("B","V");
		distances2("C","E");
		distances2("D","F");
		distances2("E","ZPWONE");
		distances2("F","JQ");
		distances2("G","R");
		distances2("H","M");
		distances2("I","PUK");
		distances2("K","I");
		distances2("L","B");
		distances2("M","Q");
		distances2("N","OVD");
		distances2("O","XITD");
		distances2("P","K");
		distances2("R","RTI");
		distances2("S","FK");
		distances2("T","HIOL");
		distances2("U","Y");
		distances2("V","C");
		distances2("W","A");
		distances2("X","J");

		for (Integer i : map.keySet()) {
			for (String s : map.get(i)) {
				System.out.println(i+", " + s);
			}
		}
		
	}
}
