package com.zodiackillerciphers.corpus;

public class AnagramsSearchDaleHalloween {
    public static void main(String[] args) {
        if (args.length < 1) 
            AnagramsSearch.searchDaleHalloween(14);
        else
            AnagramsSearch.searchDaleHalloween(Integer.valueOf(args[0]));
    }
}
