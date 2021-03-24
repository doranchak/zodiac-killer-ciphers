package com.zodiackillerciphers.old.decrypto;

import java.io.*;
import java.util.*;

public class SpaceDecryptoSolver
{
    byte[] chars;
    Dictionary dict;
    Language lang;

    static final int MAXWORDLEN = 15;

    int[][] optids;
    long[][] masks; // the set of characters which are contained in this word

    int MAXRECURSION = 1000;
    Map[] maps = new Map[MAXRECURSION];
    DictLevel dlevels[] = new DictLevel[MAXRECURSION];
    int wordpos[] = new int[MAXRECURSION];
    int wordlen[] = new int[MAXRECURSION];

    void makeoptids()
    {
	optids = new int[chars.length][MAXWORDLEN+1];
	masks = new long[chars.length][MAXWORDLEN+1];

	for (int off = 0; off < chars.length; off++)
	    {
		int maxlen = chars.length-off;
		if (maxlen > MAXWORDLEN)
		    maxlen = MAXWORDLEN;
		
		long mask = 0;
		for (int len = 1; len<=maxlen; len++)
		    {
			mask |= (1<<(chars[off+len-1]&0xff));
			masks[off][len]=mask;

			for (int soff = 0; soff <= off; soff++)
			    {
				boolean match = true;
				for (int i = 0; i < len && match ; i++)
				    if (chars[soff+i]!=chars[off+i])
					match=false;
				if (match)
				    {
					optids[off][len]=soff;
					break;
				    }
			    }
		    }
	    }
    }

    public SpaceDecryptoSolver(Dictionary dict, byte[] chars)
    {
	this.dict  = dict;
	this.chars = chars;
	this.lang  = dict.getLanguage();

	for (int i = 0; i < MAXRECURSION; i++)
	    maps[i] = new Map(lang.letterCount());

	makeoptids();

	dlevels = new DictLevel[MAXRECURSION];
    }

    public void search()
   {
	Map map = new Map(lang.letterCount());
	map.put((byte) 4, (byte) 4);
	/*	map.put((byte)4,(byte)4);
	  map.put((byte)13,(byte)13);
	  map.put((byte)22,(byte)22); */

	dlevels[0] = new DictLevel(dict, map);
	recurse(0, 0, map);
    }

    int best = 0;

    /** @param numwords words solved for so far (valid length of wordpos/wordlen)
	@param charspos index of next unused character

	uses instance variables wordpos and wordlen
	wordpos[i] is the index of the beginning of the ith word
	wordlen[i] is the length of the ith word
    **/
    void recurse(int numwords, int charspos, Map map)
    {
	if (charspos == chars.length || numwords==MAXRECURSION)
	    {
		String sol = lang.translateWithSpaces(chars, numwords, wordpos, wordlen, map);
		System.out.println("SOL: "+sol);
		return;
	    }

	boolean leaf = true;

	// grab the parent dictionary
	DictLevel dl = dlevels[numwords];

	for (int thiswordlen = MAXWORDLEN; thiswordlen>=1; thiswordlen--)
	    //	for (int thiswordlen = 1; thiswordlen<=MAXWORDLEN; thiswordlen++)
	    {
		if (charspos+thiswordlen > chars.length)
		    continue;

		DictLevel.WordSet wordset = dl.lookup(chars, charspos, thiswordlen, 
						      getUniqueId(charspos, thiswordlen));
		//		int max = wordset.words.size();
		
		for (DumbListChunk chunk = wordset.words.getHead(); chunk != null; chunk = chunk.next)
		    for (int i = 0; i < chunk.sz; i++)
			{
			    byte[] cand = (byte[]) chunk.o[i]; //wordset.words.get(i);

			    // assert(map.isMappingOkay(chars, charspos, cand, wordset.ambg, wordset.nambg));

			    Map ourmap = map.copy();
			    ourmap.map(chars, charspos, cand);
			    wordpos[numwords]=charspos;
			    wordlen[numwords]=thiswordlen;
			
			    DictLevel childdl = dlevels[numwords+1];
			    if (childdl == null)
				{
				    dlevels[numwords+1] = new DictLevel(dl, ourmap);
				    childdl = dlevels[numwords+1];
				}
			    else
				{
				    childdl.reset(dl, ourmap);
				}

			    if (numwords==0)
				{
				    String sol = lang.translateWithSpaces(chars, numwords+1, wordpos, wordlen, ourmap);
				    System.out.println("PRG: "+sol);
				}
			
			    recurse(numwords+1, charspos+thiswordlen, ourmap);
			
			    leaf = false;
			}

		//		if (numwords==0 && thiswordlen==9)
		//		    return;
	    }

	if (leaf  && charspos > best)
	    {
		best = charspos;
		String sol = lang.translateWithSpaces(chars, numwords, wordpos, wordlen, map);
		System.out.println("???: "+sol);
	    }

	// add single-character wild card, i.e., ignore this letter and try the next one.
	if (leaf && false)
	    {
		recurse(numwords+1, charspos+1, map);
	    }
    }

    // The following logic is conservative: it will never erroneously
    // collide, but it will give two different substrings of the input
    // different values.
    public int getUniqueId(int offset, int len)
    {
	int soff = optids[offset][len];

	return (soff<<4)+len;
    }
 
    public static void main(String args[]) 
    {
	try {
	    main_ex(args);
	} catch (IOException ex) {
	    System.out.println("oops: "+ex);
	}
	    
    }

    static void main_ex(String args[]) throws IOException
    {
	Dictionary dict = new Dictionary("ed-tiny.dat");
	Language lang = dict.getLanguage();

	String puzzle = "";
	for (int i = 0; i < args.length ; i++)
	    puzzle = puzzle + args[i];
	puzzle = puzzle.replace(" ", "");

	byte[] chars = lang.stringToBytes(args[0]);

	SpaceDecryptoSolver sds = new SpaceDecryptoSolver(dict, chars);
	sds.search();
	
    }
}

/*
DSDRO XFIJV DIYSB ANQAL TAIMX VBDMB GASSA QRTRT CGGXJ MMTQC IPJSB
AQPDR SDIMS DUAMB CQCMS AQDRS DMRJN SBAGC IYTCY ASBCS MQXKS CICGX
RSRCQ ACOGA SJPAS AQHDI ASBAK GCDIS AWSJN CMDKB AQHAR RCYAE
*/
