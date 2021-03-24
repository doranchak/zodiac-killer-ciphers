package com.zodiackillerciphers.old.decrypto;

import java.util.*;
import java.io.*;

public class Word
{
    public String text; // ciphertext of this word
    public int wordnum; // word number (in order of ciphertext)

    public byte[] uniqueletters;  // the cipher text letters, restricted to occur only once each
    public byte[] word;  // the cipher text letters
    public byte[] pattern;
    public int numUniqueLetters;

    //    public double score;
    public boolean enabled = true;

    public Candidate candidates[];

    /** The candidates array is broken down into two parts: those
     * candidates which have already been eliminated (their index is
     * less than firstcand), and those which are still possible. To
     * eliminate another candidate, it is swapped with the word
     * at index firstcand, then firstcand is incremented.
     * 
     * As we move through the search tree, we will eliminate more words at
     * each depth. The elimination of the words at some depth level can be undone
     * by simply remembering the value of firstcand before the words were eliminated,
     * then restoring firstcand when the words should be permitted again. 
     *
     * Naturally, the order of the candidates is not stable, but
     * provided we're careful, this shouldn't matter.
     **/
    public int firstcand = 0;

    public Word(String text, int wordnum, byte[] word)
    {
	this.text = text;
	this.wordnum = wordnum;
	this.word = word;

	this.pattern = makePattern(word);

	numUniqueLetters = 0;

	// the number of unique letters is equal to the maximum
	// candidate character (plus one)
	for (int i = 0; i < pattern.length; i++)
	    if (pattern[i]+1 > numUniqueLetters)
		numUniqueLetters = pattern[i]+1;


	ArrayList<Byte> f = new ArrayList<Byte>();
	for (int i = 0; i < word.length; i++) {
	    if (!f.contains(word[i]))
		f.add(word[i]);
	}

	uniqueletters = new byte[f.size()];
	for (int i = 0; i < f.size(); i++)
	    uniqueletters[i] = f.get(i);
    }

    protected Word()
    {
    }

    public Word copy()
    {
	Word w = new Word();

	w.text = text;
	w.wordnum = wordnum;
	w.word = word;
	w.pattern = pattern;
	//	w.score = score;
	w.enabled = enabled;
	w.candidates = new Candidate[candidates.length];
	w.numUniqueLetters = numUniqueLetters;
	w.uniqueletters = uniqueletters;

	for (int i = 0; i < candidates.length; i++)
	    w.candidates[i] = candidates[i];

	return w;
    }

    public static byte[] makePattern(byte[] word)
    {
	byte[] pattern = new byte[word.length];

	byte next = 1;

	byte[] map = new byte[256];

	for (int i = 0; i < word.length; i++)
	    {
		int c = word[i]&0xff;

		if (map[c]==0) {
		    map[c] = next;
		    next++;
		}

		pattern[i] = (byte) ( map[c] - 1);
	    }

	return pattern;
    }

    public static int compareArrays(byte[] a, byte[] b)
    {
	if (a.length != b.length)
	    return a.length - b.length;

	for (int i = 0; i < a.length; i++)
	    {
		if (a[i]!=b[i])
		    return a[i]-b[i];
	    }
	return 0;
    }

    public static class WordComparator implements Comparator<Word>
    {
	public int compare(Word a, Word b)
	{
	    if (b==null)
		return -1;

	    return compareArrays(a.word, b.word);
	}
    }

    public static class PatternComparator implements Comparator<Word>
    {
	public int compare(Word a, Word b)
	{
	    int i = compareArrays(a.pattern, b.pattern);
	    if (i!=0)
		return i;

	    return a.text.compareTo(b.text);
	}
    }

    public static class TextComparator implements Comparator<Word>
    {
	public int compare(Word a, Word b)
	{
	    return a.text.compareTo(b.text);
	}
    }

    static Random r = new Random();

    public static class RandomComparator implements Comparator<Word>
    {
	public int compare(Word a, Word b)
	{
	    return r.nextInt(3) - 1;
	}
    }

    public static class DisabledComparator implements Comparator<Word>
    {
	public int compare(Word a, Word b)
	{
	    // disabled words to the rear, please
	    if (a.enabled && !b.enabled)
		return -1;
	    if (!a.enabled && b.enabled)
		return 1;

	    // we don't care.
	    return 0;
	}
    }

    /*
    public static class ScoreComparator implements Comparator<Word>
    {
	public int compare(Word a, Word b)
	{
	    // disabled words to the rear, please
	    if (a.enabled && !b.enabled)
		return -1;
	    if (!a.enabled && b.enabled)
		return 1;

	    return -Double.compare(a.score, b.score);
	}
    }
    */

    /** Canonical order (order they appeared in ciphertext **/
    public static class CanonicalComparator implements Comparator<Word>
    {
	public int compare(Word a, Word b)
	{
	    return Double.compare(a.wordnum, b.wordnum);
	}
    }

}
