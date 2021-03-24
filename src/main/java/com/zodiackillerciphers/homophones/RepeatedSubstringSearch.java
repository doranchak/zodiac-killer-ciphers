package com.zodiackillerciphers.homophones;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.old.CombinationGenerator;

/** utilities to help find sets of all repeating, non-overlapping substrings within a given string */
public class RepeatedSubstringSearch {
	public static List<RepeatedSubstringSearchResult> search(String string, Map<Character, Integer> counts, String combination) {
		List<RepeatedSubstringSearchResult> list = new ArrayList<RepeatedSubstringSearchResult>();
		int maxSubstringLength = string.length()/2;
		if (maxSubstringLength < 2) {
			return list;
		}
		
		Set<String> seen = new HashSet<String>();
		
		for (int L=2; L<=maxSubstringLength; L++) {
			for (int i=0; i<string.length()-L+1; i++) {
				String sub = string.substring(i,i+L);
				if (seen.contains(sub)) continue;
				seen.add(sub);
				int count = CountSubstring.countSubstring(sub, string);
				if (count > 1) {
					RepeatedSubstringSearchResult bean = new RepeatedSubstringSearchResult();
					bean.sequence = sub;
					bean.combination = combination; 
					dump(bean, sub, string, counts);
					list.add(bean);
				}
			}
		}
		return list;
	}
	
	public static void dump(RepeatedSubstringSearchResult bean, String substring, String string, Map<Character, Integer> counts) {
		String result = "";
		String bbcode = "";
		int index = string.indexOf(substring);
		while (index > -1 && index < string.length()) {
			bean.addPosition(index);
			if (index > 0) {
				result += string.substring(0,index) + " ";
				bbcode += string.substring(0,index) + " ";
			}
			result += "[" + substring + "] ";
			bbcode += "[b][color=#00aa00][size=130]" + substring + "[/size][/color][/b] ";
			string = string.substring(index+substring.length());
			index = string.indexOf(substring);
		}
		if (string.length() > 0) {
			result += string;
			bbcode += string;
		}
		bean.dumpText = result;
		bean.dumpBBCode = bbcode;
		
		bean.computeScore(counts);
		String prefix = bean.score + ", " + bean.combination + ", " + bean.sequence + ", ";
		bean.dumpText = prefix + bean.dumpText; 
		bean.dumpBBCode = prefix + bean.dumpBBCode;
	}
	
	public static void search(String cipher, int L1, int L2) {
		String[] alphabet = Ciphers.alphabetAsArray(cipher);
		Map<Character, Integer> map = Ciphers.countMap(cipher);		
		for (int n=L1; n<=L2; n++) {
			int[] indices;
			CombinationGenerator x = new CombinationGenerator(alphabet.length, n);
			StringBuffer combination;
			while (x.hasMore()) {
				combination = new StringBuffer();
				indices = x.getNext();
				for (int i = 0; i < indices.length; i++) {
					combination.append(alphabet[indices[i]]);
				}
				String key = combination.toString();
				if (key.contains("+")) continue; // ignore +
				HomophoneSequenceBean fullsequence = HomophonesNew.isolateSequence(cipher, key);
				
				//System.out.println(key + ": " + fullsequence.fullSequence);
				List<RepeatedSubstringSearchResult> list = search(fullsequence.fullSequence, map, key);
				for (RepeatedSubstringSearchResult bean : list) {
					System.out.println(bean.toString(true));
				}
				
			}
		}
	}
	
	public static void test() {
		String cipher = Ciphers.cipher[0].cipher;
		List<RepeatedSubstringSearchResult> list = search("|BOBcO|Oc|OBcOB|BOc|B|BccB|cBOcB|cO|BOBcO|", Ciphers.countMap(cipher), null);
		for (RepeatedSubstringSearchResult bean : list)
			System.out.println(bean.toString(true));
	}
	
	public static void main(String[] args) {
		String cipher = Ciphers.cipher[1].cipher;
		search(cipher, 2, 4);
	}
}
