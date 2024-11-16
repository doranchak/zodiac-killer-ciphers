package com.zodiackillerciphers.anagram;

import java.util.ArrayList;
import java.util.List;

import com.zodiackillerciphers.io.FileUtil;
import com.zodiackillerciphers.ngrams.AZDecrypt;

// Read a file where each line contains a sequence of words, such as anagrams produced by https://wordsmith.org/anagram/
// For each line, try out all possible rearrangements of the indiviual words and output the arrangement that produces the highest ngram score.
public class AnagramImprover {
    public static void init() {
        AZDecrypt.init("/Users/doranchak/projects/zodiac/github/azdecrypt/AZdecrypt/N-grams/Spaces/English/5-grams_english+spaces_jarlve_reddit_v1912.gz", 5, true);        
    }
        // public static void main(String[] args) {
        //     String[] array = {"A", "B", "C"};
    
        //     List<String[]> result = new ArrayList<>();
        //     generatePermutations(array, 0, result);
    
        //     // Print all permutations
        //     for (String[] perm : result) {
        //         for (String s : perm) {
        //             System.out.print(s + " ");
        //         }
        //         System.out.println();
        //     }
        // }
    
    public static String toString(String[] array) {
        String result = "";
        for (String str : array) {
            if (result.length() > 0) result += " ";
            result += str;
        }
        return result;
    }
    public static void generatePermutations(String[] array, int index, float[] bestScore, String[] bestArray) {
        if (index == array.length - 1) {
            // result.add(array.clone()); // Add a clone of the array to avoid modifying it later
            String line = toString(array);
            float score = AZDecrypt.score(line);
            if (score > bestScore[0]) {
                bestScore[0] = score;
                for (int i=0; i<array.length; i++) bestArray[i] = array[i];
            }
            return;
        }

        for (int i = index; i < array.length; i++) {
            swap(array, index, i);
            generatePermutations(array, index + 1, bestScore, bestArray);
            swap(array, index, i);  // backtrack
        }
    }
    
    private static void swap(String[] array, int i, int j) {
        String temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    public static void improveAnagram(String line) {
        String[] split = line.split(" ");
        for (int i=0; i<split.length; i++) split[i] = FileUtil.convert(split[i]).toString();
        float[] bestScore = {0};
        String[] bestArray = new String[split.length];
        generatePermutations(split, 0, bestScore, bestArray);
        System.out.println(bestScore[0] + "	" + toString(bestArray) + "	" + toString(split));
    }
    public static void improve(String filePath) {
        init();
        List<String> lines = FileUtil.loadFrom(filePath);
        for (String line : lines) improveAnagram(line);
    }
    public static void main(String[] args) {
        improve(args[0]);
    }
}
