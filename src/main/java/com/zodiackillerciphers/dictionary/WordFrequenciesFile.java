package com.zodiackillerciphers.dictionary;

import org.glassfish.jaxb.runtime.v2.schemagen.xmlschema.List;

import com.zodiackillerciphers.io.FileUtil;

public class WordFrequenciesFile {
    // process list of words and dump frequencies for each word
    public static void main(String[] args) {
        WordFrequencies.init();
        if (args.length != 1) {
            throw new IllegalArgumentException("Please provide file path.");
        }
        for (String word : FileUtil.loadFrom(args[0])) 
            System.out.println(WordFrequencies.freq(FileUtil.convert(word).toString()) + "\t" + word);
    }
}
