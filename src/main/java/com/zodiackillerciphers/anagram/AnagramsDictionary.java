package com.zodiackillerciphers.anagram;

import java.util.List;

import com.zodiackillerciphers.dictionary.WordFrequencies;


/** look in word dictionary for all words that can be found as anagrams in the given string.  leftover letters are allowed (but entire word must be found). */
public class AnagramsDictionary {
    public static int vowelCount(String word) {
        int count = 0;
        for (int i=0; i<word.length(); i++) {
            if ("AEIOU".indexOf(word.charAt(i)) != -1) count++;
        }
        return count;
    }
    public static int consonantCount(String word) {
        return word.length() - vowelCount(word);
    }
        public static void main(String[] args) {
        if (args.length != 1)
            throw new IllegalArgumentException("Please specify the input string.");
        List<String> list = Anagrams.anagramsFor(args[0].toUpperCase(), 0);

        // Sort the list by length first (descending), then by frequency (also descending)
        list.sort((s1, s2) -> {
            // Compare by string length, descending order
            int lengthCompare = Integer.compare(s2.length(), s1.length());
            if (lengthCompare != 0) {
                return lengthCompare;
            }
            // If lengths are the same, compare by frequency (descending order)
            return Integer.compare(WordFrequencies.freq(s2), WordFrequencies.freq(s1));
        });        
        System.out.println("length frequency vowel_count consonant_count word");
        for (String word : list) {
            System.out.println(word.length() + " " + WordFrequencies.freq(word) + " " + vowelCount(word) + " " + consonantCount(word) + " " + word);
        }
    }
}
