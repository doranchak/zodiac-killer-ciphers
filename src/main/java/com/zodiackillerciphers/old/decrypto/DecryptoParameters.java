package com.zodiackillerciphers.old.decrypto;

public class DecryptoParameters
{
    public static boolean pruneEachNode = true;
    public static boolean pruneRootNode = true;
    public static boolean betterWordPrioritization = true;
    public static boolean enableSingleLetters   = true;
    //    public static int     singleLetterThreshold = 40;
    public static int     verbosity = 0;
    public static Planner planner = new GreedyPlanner(); //SimplePlanner();
    public static int     maxReduceIterations = 50;

    public static int     maxRandomWordDisable = 3; // maximum number of words to try to disable

    public static boolean scrambleAllowIdentityMappings = false;
}
