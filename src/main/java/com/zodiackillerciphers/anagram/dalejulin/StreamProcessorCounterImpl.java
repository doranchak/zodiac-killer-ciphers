package com.zodiackillerciphers.anagram.dalejulin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.zodiackillerciphers.anagram.Anagrams;
import com.zodiackillerciphers.io.StreamProcessor;

// count distinct names that were tested from the input files
public class StreamProcessorCounterImpl implements StreamProcessor{
    public static String TAB = "" + '\t';
    Set<String> names;
    long lineCount;
    public StreamProcessorCounterImpl() {
        this.names = new HashSet<String>();
        this.lineCount = 0;
    }

    public static boolean isEmpty(String inp) {
        return inp == null || "".equals(inp);
    }
    @Override
    public void process(String line) {
        this.lineCount++;
        if (this.lineCount % 100000 == 0) {
            System.out.println(this.lineCount + " lines, " + names.size() + " tested (difference: " + (this.lineCount - names.size()) + ")");
        }
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
        if (!isEmpty(lastName) && !isEmpty(firstName)) {
            combinationsSpaces.add(firstName + " " + lastName);
        } 
        if (!isEmpty(lastName) && isEmpty(firstName)) {
            combinationsSpaces.add(lastName);
        }
        if (!isEmpty(lastName) && !isEmpty(firstName) && !isEmpty(middleName)) {
            combinationsSpaces.add(firstName + " " + middleName + " " + lastName);
        }
        if (!isEmpty(lastName) && !isEmpty(firstName) && !isEmpty(middleName) && !isEmpty(suffix)) {
            combinationsSpaces.add(firstName + " " + middleName + " " + lastName + " " + suffix);
        }
        names.addAll(combinationsSpaces);
    }

    public void finish() {
        System.out.println("Total unique names that were tested: " + names.size());
        System.out.println("Total lines from input files: " + lineCount);
    }
}
