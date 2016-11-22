package com.zodiackillerciphers.old.decrypto;

import java.io.*;
import java.util.*;

public class DecryptoSolverOptimizations
{
    /** Remove words that appear more than once without otherwise
     *  affecting the word order. Duplicate words needlessly slow down
     *  the search while yielding no useful information. This routine,
     *  I'm embarassed to admit, is O(N^2).
     **/
    public static void removeDuplicateWords(DecryptoPuzzle puzzle)
    {
	ArrayList<Word> uniqueWords = new ArrayList<Word>();
	Word.WordComparator wcomp = new Word.WordComparator();
	//	HashMap<String,String> uniqueMap = new HashMap<String,String>();

	for (Word w : puzzle.words)
	    {
		boolean existsAlready = false;

		for (Word uw : uniqueWords) 
		    {
			if (wcomp.compare(w, uw) == 0)
			    {
				existsAlready = true;
				break;
			    }
		    }

		if (!existsAlready)
		    uniqueWords.add(w);
	    }

	puzzle.words = uniqueWords;
    }
}
