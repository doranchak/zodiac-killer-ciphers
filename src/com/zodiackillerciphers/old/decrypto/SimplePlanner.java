package com.zodiackillerciphers.old.decrypto;

import java.util.*;

// static planner
public class SimplePlanner implements Planner
{
    DecryptoPuzzle puzzle;
    int wordorder[];

    public SimplePlanner()
    {
    }

    public Planner make(DecryptoPuzzle puzzle)
    {
	SimplePlanner p = new SimplePlanner();
	p.puzzle = puzzle;
	p.wordorder = computeWordOrder(puzzle);
	return p;
    }

    public int getAction(int wordlist[], int solvedwords, MapSet map)
    {
	// are we done?
	if (solvedwords == wordorder.length)
	    return -1;

	return wordorder[solvedwords] | WORD_FLAG;
    }

    static class ScoreRecord implements Comparable<ScoreRecord>
    {
	int idx;
	double score;

	public ScoreRecord(int idx)
	{
	    this.idx = idx;
	}

	public int compareTo(ScoreRecord r)
	{
	    return -Double.compare(score, r.score);
	}
    }

    /*
		int ncands = w.candidates.length - w.firstcand;
		int nscore = ncands / w.numUniqueLetters;
		if (nscore > DecryptoParameters.singleLetterThreshold)
		    {
			//		System.out.println("Maybe better to search a letter: "+nscore);

			int wordstate[] = new int[puzzle.words.size()];
			MapSet ourmap = map.copy();
			saveWordState(wordstate, wordnum+1);

			// which letter should we search for?
			int theletter = -1;
			for (int i = 0; i < puzzle.lang.letterCount() && theletter<0; i++)
			    {
				if (!map.isMapped((byte)puzzle.historder[i]))
				    theletter = puzzle.historder[i];
			    }

			// System.out.println(recursiondepth+" "+wordnum+":  Substituting char "+((char) ('A'+theletter)));
			if (theletter < 0) 
			    {
				reportSolution(map);
				return;
			    }
    */

    public static int[] computeWordOrder(DecryptoPuzzle puzzle)
    {
	int count[] = new int[256];
	int wordorder[];

	ScoreRecord records[] = new ScoreRecord[puzzle.words.size()];
	for (int i = 0; i < records.length; i++)
	    records[i] = new ScoreRecord(i);

	// how many times does each letter occur in the puzzle?
	for (Word w : puzzle.words)
	    {
		for (int i = 0 ; i < w.word.length ; i++) 
		    {
			count[w.word[i]]++;
		    }
	    }

	// now we compute the score for each word.

	// If we match a word, we solve simultaneously a number of other
	// letters in the puzzle. Words that would give us many letters
	// are good. But words that have a zillion candidates make us 
	// work very hard for it. So the score for a word is the number of
	// letters it gives us, divided by the number of candidate words
	// we'd need to search.

	for (int wordnum = 0; wordnum < puzzle.words.size(); wordnum++)
	    {
		Word w = puzzle.words.get(wordnum);

		double score = 0;

		// each unique character only gets counted once
		boolean used[] = new boolean[puzzle.lang.letterCount()];

		for (int i = 0 ; i < w.word.length ; i++)
		    if (!used[w.word[i]])
			{
			    score += count[w.word[i]] - 1;
			    used[w.word[i]] = true;
			}

		records[wordnum].score =  - Math.pow(w.candidates.length, 1.0 / score);
		
		// version 6 score was computed as:
		// heuristic: how many choices do we have to try per letter?
		//		w.score = - Math.pow(w.candidates.size(), 1.0 / w.word.length);

		if (DecryptoParameters.verbosity >= 1)
		    System.out.println(String.format("%8.3f %8d %12.3f : %s", 
						     score, w.candidates.length, records[wordnum].score, w.text));
	    }

	Arrays.sort(records);

	wordorder = new int[records.length];
	for (int i = 0; i < records.length; i++)
	    wordorder[i] = records[i].idx;

	if (DecryptoParameters.verbosity >= 1)
	    {
		System.out.println("XXX Word priorities: ");
		for (int i = 0; i < records.length; i++)
		    {
			Word w = puzzle.words.get(wordorder[i]);

			System.out.println(String.format("%3d : %8.3f %s", 
							 i, 
							 records[i].score,
							 puzzle.lang.bytesToString(w.word)));
		    }
	    }

	return wordorder;
    }
    
}
