package com.zodiackillerciphers.anagram.dalejulin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import com.zodiackillerciphers.io.Stream7Zip;

public class DaleJulinAnagrams {
    public static void processResults(String[] filePaths) {
        Map<String, Integer> counts = new HashMap<String, Integer>();
        for (String filePath : filePaths) {
            System.out.println("Reading " + filePath + "...");
            try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
                lines.forEach(line -> {
                    String[] split = line.split(""+'\t');
                    System.out.println(split[0] + " " + split[3]);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }        
        }
    }
    public static void main(String[] args) {
        String filePath = args[0];
        String password = args[1];
        String inputString = args[2];
        int minLength = args.length > 3 ? Integer.valueOf(args[3]) : 1;
        StreamProcessorAnagramImpl sp = new StreamProcessorAnagramImpl(inputString, minLength);
        try {
            Stream7Zip.stream(filePath, password, sp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }   
}
