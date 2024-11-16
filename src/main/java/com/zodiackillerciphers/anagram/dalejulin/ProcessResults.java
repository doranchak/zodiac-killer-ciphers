package com.zodiackillerciphers.anagram.dalejulin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProcessResults {
    public static Map<String, Integer> sortByValue(Map<String, Integer> unsortedMap) {
        // Create a LinkedHashMap to maintain insertion order of the sorted elements
        return unsortedMap.entrySet()
            .stream()
            // Sort by values
            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
            // Collect into a LinkedHashMap to maintain order
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (oldValue, newValue) -> oldValue, // in case of a tie in values, keep the old value
                LinkedHashMap::new
            ));
    }
    public static void process(String[] filePaths) {
        Map<String, Integer> counts = new HashMap<String, Integer>();
        for (String filePath : filePaths) {
            System.out.println("Reading " + filePath + "...");
            try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
                lines.forEach(line -> {
                    String[] split = line.split(""+'\t');
                    String key = split[0] + " " + split[3];
                    Integer val = counts.get(key);
                    if (val == null) val = 0;
                    val++;
                    counts.put(key, val);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }        
        }
        Map<String, Integer> sortedMap = sortByValue(counts);
        sortedMap.forEach((key, value) -> System.out.println(key + "\t" + value));
    }
    public static void main(String[] args) {
        process(java.util.Arrays.stream(args)
        .filter(arg -> !arg.isEmpty())
        .toArray(String[]::new));
    }   
}
