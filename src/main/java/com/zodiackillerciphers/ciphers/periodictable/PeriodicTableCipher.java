package com.zodiackillerciphers.ciphers.periodictable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.zodiackillerciphers.ciphers.crimo.CrimoCipher;
import com.zodiackillerciphers.lucene.NGramsCSRA;

public class PeriodicTableCipher {
	static float bestScore;
	static {
		NGramsCSRA.OPTIONAL_PREFIX = "/Users/doranchak/projects/zodiac/zkdecrypto/zkdecrypto/language/";
		NGramsCSRA.init("EN");
	}
	public static Map<String, String> decoder = new HashMap<String, String>();
	static {
		decoder.put("1", "H");
		decoder.put("2", "HE");
		decoder.put("3", "LI");
		decoder.put("4", "BE");
		decoder.put("5", "B");
		decoder.put("6", "C");
		decoder.put("7", "N");
		decoder.put("8", "O");
		decoder.put("9", "F");
		decoder.put("10", "NE");
		decoder.put("11", "NA");
		decoder.put("12", "MG");
		decoder.put("13", "AL");
		decoder.put("14", "SI");
		decoder.put("15", "P");
		decoder.put("16", "S");
		decoder.put("17", "CL");
		decoder.put("18", "AR");
		decoder.put("19", "K");
		decoder.put("20", "CA");
		decoder.put("21", "SC");
		decoder.put("22", "TI");
		decoder.put("23", "V");
		decoder.put("24", "CR");
		decoder.put("25", "MN");
		decoder.put("26", "FE");
		decoder.put("27", "CO");
		decoder.put("28", "NI");
		decoder.put("29", "CU");
		decoder.put("30", "ZN");
		decoder.put("31", "GA");
		decoder.put("32", "GE");
		decoder.put("33", "AS");
		decoder.put("34", "SE");
		decoder.put("35", "BR");
		decoder.put("36", "KR");
		decoder.put("37", "RB");
		decoder.put("38", "SR");
		decoder.put("39", "Y");
		decoder.put("40", "ZR");
		decoder.put("41", "NB");
		decoder.put("42", "MO");
		decoder.put("43", "TC");
		decoder.put("44", "RU");
		decoder.put("45", "RH");
		decoder.put("46", "PD");
		decoder.put("47", "AG");
		decoder.put("48", "CD");
		decoder.put("49", "IN");
		decoder.put("50", "SN");
		decoder.put("51", "SB");
		decoder.put("52", "TE");
		decoder.put("53", "I");
		decoder.put("54", "XE");
		decoder.put("55", "CS");
		decoder.put("56", "BA");
		decoder.put("71", "LU");
		decoder.put("72", "HF");
		decoder.put("73", "TA");
		decoder.put("74", "W");
		decoder.put("75", "RE");
		decoder.put("76", "OS");
		decoder.put("77", "IR");
		decoder.put("78", "PT");
		decoder.put("79", "AU");
		decoder.put("80", "HG");
		decoder.put("81", "TL");
		decoder.put("82", "PB");
		decoder.put("83", "BI");
		decoder.put("84", "PO");
		decoder.put("85", "AT");
		decoder.put("86", "RN");
		decoder.put("87", "FR");
		decoder.put("88", "RA");
		decoder.put("103", "LR");
		decoder.put("104", "RF");
		decoder.put("105", "DB");
		decoder.put("106", "SG");
		decoder.put("107", "BH");
		decoder.put("108", "HS");
		decoder.put("109", "MT");
		decoder.put("110", "DS");
		decoder.put("111", "RG");
		decoder.put("112", "CN");
		decoder.put("113", "NH");
		decoder.put("114", "FL");
		decoder.put("115", "MC");
		decoder.put("116", "LV");
		decoder.put("117", "TS");
		decoder.put("118", "OG");
		decoder.put("57", "LA");
		decoder.put("58", "CE");
		decoder.put("59", "PR");
		decoder.put("60", "ND");
		decoder.put("61", "PM");
		decoder.put("62", "SM");
		decoder.put("63", "EU");
		decoder.put("64", "GD");
		decoder.put("65", "TB");
		decoder.put("66", "DY");
		decoder.put("67", "HO");
		decoder.put("68", "ER");
		decoder.put("69", "TM");
		decoder.put("70", "YB");
		decoder.put("89", "AC");
		decoder.put("90", "TH");
		decoder.put("91", "PA");
		decoder.put("92", "U");
		decoder.put("93", "NP");
		decoder.put("94", "PU");
		decoder.put("95", "AM");
		decoder.put("96", "CM");
		decoder.put("97", "BK");
		decoder.put("98", "CF");
		decoder.put("99", "ES");
		decoder.put("100", "FM");
		decoder.put("101", "MD");
		decoder.put("102", "NO");
	}
	
	/** brute force solve the given string.  allow for bigram reversals and no cipher unit delimiters. */
	public static void solve(String cipher) {
		solve(cipher, new ArrayList<String>(), new ArrayList<String>(), 0);
	}
	
	public static void solve(String cipher, List<String> plaintext, List<String> interpretation, int index) {
		//System.out.println(plaintext +", " + interpretation + ", " + index);
		if (index >= cipher.length()) {
			process(plaintext, interpretation);
			return;
		}

		boolean found = false;
		String key;
		// one character key
		key = cipher.substring(index,index+1);
		found = found | recurse(key, cipher, plaintext, interpretation, index);
		// two character key
		if (index < cipher.length() - 1) {
			key = cipher.substring(index,index+2);
			found = found | recurse(key, cipher, plaintext, interpretation, index);
		}
		// three character key
		if (index < cipher.length() - 2) {
			key = cipher.substring(index,index+3);
			found = found | recurse(key, cipher, plaintext, interpretation, index);
		}
		
		// keep trying if we didn't find a key this time
		if (!found) {
			plaintext.add("_");
			interpretation.add(cipher.substring(index,index+1));
			solve(cipher, plaintext, interpretation, index+1);
			plaintext.remove(plaintext.size()-1);
			interpretation.remove(interpretation.size()-1);
		}
		
	}
	
	public static boolean recurse(String key, String cipher, List<String> plaintext, List<String> interpretation, int index) {
		if (decoder.containsKey(key)) {
			String pt = decoder.get(key);
			plaintext.add(pt);
			interpretation.add(key);
			solve(cipher, plaintext, interpretation, index+key.length());
			plaintext.remove(plaintext.size()-1);
			interpretation.remove(interpretation.size()-1);
			// do the reversal if the decoded pt is long enough
			if (pt.length() > 1) { 
				pt = new StringBuffer(pt).reverse().toString();
				plaintext.add(pt);
				interpretation.add(key+"'");
				solve(cipher, plaintext, interpretation, index+key.length());
				plaintext.remove(plaintext.size()-1);
				interpretation.remove(interpretation.size()-1);
			}
			return true;
		}
		return false;
	}
	
	public static void process(List<String> plaintext, List<String> interpretation) {
		StringBuffer sb = new StringBuffer();
		for (String p : plaintext) sb.append(p.replaceAll("_", ""));
		float score = NGramsCSRA.zkscore(sb, "EN", false) / sb.length();
		
//		if (score >= 0.8 * bestScore) {
			System.out.println(score + "	" + sb + "	" + plaintext + "	" + interpretation);
//		}
		
		
		if (score > bestScore) {
			bestScore = score;
		}
		
	}
	
	static void solveCrimo() {
		for (int start=0; start<CrimoCipher.cipherCrimo9.length()-16; start++) {
			String sub = CrimoCipher.cipherCrimo9.substring(start, start+16);
			System.out.println("==== start " + start + " sub " + sub);
			solve(sub);
			bestScore = 0;
		}
	}
	
	public static void main(String[] args) {
		//solve("155700049");
//		solve("1557537");
//		solve("1733149532022102");
	//	solve("2390382085723830"); // first 16 of crimo page 1
		solve("901453335211787895");
		
		
	}
}
