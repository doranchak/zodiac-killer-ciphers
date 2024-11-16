package com.zodiackillerciphers.anagram.dalejulin;

import java.util.ArrayList;
import java.util.List;

import com.zodiackillerciphers.anagram.Anagrams;
import com.zodiackillerciphers.io.StreamProcessor;

public class StreamProcessorAnagramImpl implements StreamProcessor{
    public static String TAB = "" + '\t';
    long counter, hits;
    String inputString;
    int minLength;
    public StreamProcessorAnagramImpl(String inputString, int minLength) {
        this.counter = 0;
        this.hits = 0;
        this.inputString = inputString;
        this.minLength = minLength;
    }

    public static boolean isEmpty(String inp) {
        return inp == null || "".equals(inp);
    }
    @Override
    public void process(String line) {
        String[] split = line.split(",");
        String lastName = "";
        String firstName = "";
        String middleName = "";
        String suffix = "";

        if (split.length > 1)
            firstName = split[1];
        if (split.length > 2)
            lastName = split[2];
        if (split.length > 3)
            middleName = split[3];
        if (split.length > 5)
            suffix = split[4];

        List<String> combinationsSpaces = new ArrayList<String>();
        List<String> combinationsNoSpaces = new ArrayList<String>();
        if (!isEmpty(lastName) && !isEmpty(firstName)) {
            combinationsSpaces.add(firstName + " " + lastName);
            combinationsNoSpaces.add(firstName + lastName);
        } 
        if (!isEmpty(lastName) && isEmpty(firstName)) {
            combinationsSpaces.add(lastName);
            combinationsNoSpaces.add(lastName);
        }
        if (!isEmpty(lastName) && !isEmpty(firstName) && !isEmpty(middleName)) {
            combinationsSpaces.add(firstName + " " + middleName + " " + lastName);
            combinationsNoSpaces.add(firstName + middleName + lastName);
        }
        if (!isEmpty(lastName) && !isEmpty(firstName) && !isEmpty(middleName) && !isEmpty(suffix)) {
            combinationsSpaces.add(firstName + " " + middleName + " " + lastName + " " + suffix);
            combinationsNoSpaces.add(firstName + middleName + lastName + suffix);
        }
        for (int i=0; i<combinationsNoSpaces.size(); i++) {
            this.counter++;
            String noSpaces = combinationsNoSpaces.get(i);
            String spaces = combinationsSpaces.get(i);
            // System.out.println(this.counter + " " + spaces);
            if (noSpaces.length() >= minLength && Anagrams.anagram(noSpaces, inputString)) {
                this.hits++;
                System.out.println(noSpaces.length() + TAB + this.hits + TAB + this.counter + TAB + spaces);
            }    
        }
    }
}
