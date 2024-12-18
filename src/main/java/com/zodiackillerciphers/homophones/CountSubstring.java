package com.zodiackillerciphers.homophones;
/** http://rosettacode.org/wiki/Count_occurrences_of_a_substring#Java */
public class CountSubstring {
	public static int countSubstring(String subStr, String str){
		return (str.length() - str.replace(subStr, "").length()) / subStr.length();
	}
 
	public static void main(String[] args){
		System.out.println(countSubstring("th", "the three truths"));
		System.out.println(countSubstring("aba", "ababababab"));
		System.out.println(countSubstring("a*b", "abaabba*bbaba*bbab"));
	}
}

