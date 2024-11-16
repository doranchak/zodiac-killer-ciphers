package com.zodiackillerciphers.anagram.dalejulin;

import com.zodiackillerciphers.anagram.Anagrams;
import com.zodiackillerciphers.io.FileUtil;

// find Kaczynski's name anagrammed in his manifesto
public class ManifestoTest {

    public static String TAB = "\t";
    public static void find(String filePath, String searchString, int maxLength) {
        StringBuffer sb = FileUtil.loadSBFrom(filePath, true);
        searchString = searchString.toUpperCase();
        for (int i=0; i<sb.length()-maxLength+1; i++) {
            find(sb.substring(i, i+maxLength), searchString);
        }
    }    
    public static void find(String sub, String searchString) {
        String converted = FileUtil.convert(sub).toString();
        if (searchString.indexOf(converted.charAt(0)) == -1) return;
        
        if (Anagrams.anagram(searchString, converted)) {
            for (int len=searchString.length(); len<converted.length(); len++) {
                if (Anagrams.anagram(searchString, converted.substring(0, len))) {
                    System.out.println(converted.substring(0, len).length() + TAB + converted.substring(0, len));
                    return;
                }
            }
        }
    }
    public static void main(String[] args) {
        if (args.length != 3) {
            throw new IllegalArgumentException("Please supply file path, search string, and max length.");
        }
        find(args[0], args[1], Integer.valueOf(args[2]));
    }
}
