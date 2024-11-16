package com.zodiackillerciphers.anagram;

import com.zodiackillerciphers.io.FileUtil;

public class AnagramsTest {
    // Read lines from a file.
    // Output all lines that can be found as anagrams of the given input string.
    // If 3rd argument (of any value) is given, then limit to exact anagrams.
    public static void main(String[] args) {
        if (args.length < 2) throw new IllegalArgumentException("Please provide input file path and input string.");
        boolean exact = false;
        String inputPath = args[0];
        String inputString = FileUtil.convert(args[1]).toString();
        if (args.length > 2) exact = true;
        for (String line : FileUtil.loadFrom(inputPath)) {
            String converted = FileUtil.convert(line).toString();
            if (Anagrams.anagram(converted, inputString, exact)) {
                String lineOut = converted.equals(line) ? line : line + " (" + converted + ")";
                System.out.println(converted.length() + " " + lineOut);
            }
        }
    }
}
