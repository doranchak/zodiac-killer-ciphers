package com.zodiackillerciphers.tests;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.zodiackillerciphers.io.StreamProcessor;

public class StreamProcessorZ13NameTestImpl implements StreamProcessor{
    public static String TAB = "" + '\t';
    public StreamProcessorZ13NameTestImpl() {
    }

    public static boolean isEmpty(String inp) {
        return inp == null || "".equals(inp);
    }

    public static List<String> generatePermutations(List<String> strings, int targetLength) {
        List<String> result = new ArrayList<>();
        generatePermutationsRecursive(strings, "", "", new boolean[strings.size()], targetLength, result);
        return result;
    }

    private static void generatePermutationsRecursive(
            List<String> strings, String current, String currentWithSpaces, boolean[] used, int targetLength, List<String> result) {
        // Base case: If the current string matches the target length, add to the result.
        if (current.length() == targetLength && fits(current) > 0) {
            result.add(currentWithSpaces);
            return;
        }

        // Prune if the current string is already too long.
        if (current.length() > targetLength) {
            return;
        }

        // Recursively try each unused string.
        for (int i = 0; i < strings.size(); i++) {
            if (!used[i]) {
                used[i] = true;
                String space = "";
                if (currentWithSpaces.length() > 0) space = " ";
                generatePermutationsRecursive(strings, current + strings.get(i), currentWithSpaces + space + strings.get(i), used, targetLength, result);
                generatePermutationsRecursive(strings, current + strings.get(i).substring(0,1), currentWithSpaces + space + strings.get(i).substring(0,1), used, targetLength, result); // abbreviations
                used[i] = false;
            }
        }
    }

    // returns 0 if it doesn't fit in Z13
    // returns 1 if fits in Z13
    // returns 2 if fits in Z13 and is homophonic (multiple cipher symbols stand for the same plaintext)
    static int fits(String str) {
        // 0==11
        // 2==10
        // 4==6
        // 6==8
        // 7==12
        boolean fits = str.charAt(0) == str.charAt(11) && 
            str.charAt(2) == str.charAt(10) &&
            str.charAt(4) == str.charAt(6) &&
            str.charAt(6) == str.charAt(8) &&
            str.charAt(7) == str.charAt(12);
        if (!fits) return 0;
        Set<Character> set = new HashSet<Character>();
        for (int i=0; i<str.length(); i++) set.add(str.charAt(i));
        return set.size() == 8 ? 1 : 2;
    }
    @Override
    public void process(String line) {
        // System.out.println(line);

        List<String> parts = new ArrayList<String>();
        String[] split = line.split(",");
        String original = "";
        for (int i=1; i<5 && i<split.length; i++) {
            String part = split[i].trim();
            if (!isEmpty(part)) {
                parts.add(part);
                if (original.length() > 0) original += " ";
                original += part;
            }
        }

        int targetLength = 13;

        List<String> result = generatePermutations(parts, targetLength);
        if (result.size() > 0) {
            System.out.println("Hits for " + original + ":");   
            for (String hit : result) System.out.println(" - " + hit.replaceAll(" ", "") + " (" + hit + ") " + (fits(hit) == 1 ? "" : "(homophonic)"));
        }
    }
}
