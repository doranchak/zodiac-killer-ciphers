package com.zodiackillerciphers.anagram.dalejulin;

import java.io.IOException;

import com.zodiackillerciphers.io.Stream7Zip;

public class DaleJulinAnagramsNameCount {
    public static void process(String[] args) {
        String password = args[args.length-1]; // last arg is password
        StreamProcessorCounterImpl sp = new StreamProcessorCounterImpl();
        for (int i=0; i<args.length-1; i++) {
            String filePath = args[i];
            try {
                Stream7Zip.stream(filePath, password, sp);
            } catch (IOException e) {
                e.printStackTrace();
            }
    
        }
        sp.finish();
    }
    public static void main(String[] args) {
        process(java.util.Arrays.stream(args)
        .filter(arg -> !arg.isEmpty())
        .toArray(String[]::new));
    }   
}
