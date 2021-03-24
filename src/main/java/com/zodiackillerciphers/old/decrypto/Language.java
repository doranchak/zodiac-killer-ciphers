package com.zodiackillerciphers.old.decrypto;

import java.util.*;

public abstract class Language
{
    /** Map a character to a byte value. Letters should be mapped to
     * sequentially numbered, small-valued bytes. Non-letters should
     * be mapped to -1.
     **/
    public abstract byte charToByte(char c);

    /** The inverse of charToByte. **/
    public abstract char byteToChar(byte b);

    /** Return the number of unique letters in the language (e.g., the
	largest value ever returned by charToByte.) Note that the
	current implementation of Map doesn't work for values > 63
    **/
    public abstract int letterCount();

    /** Assume s is a single word (e.g., from a word list.) Return
     * the byte version of it. If no word is present, return null.
     **/
    public byte[] stringToBytes(String s)
    {
	ArrayList<Byte> bytes = new ArrayList<Byte>();

	for (int i = 0; i < s.length(); i++)
	    {
		char c = s.charAt(i);
		byte b = charToByte(c);

		if (b>=0)
		    bytes.add(b);
	    }

	byte[] bs = new byte[bytes.size()];
	for (int i = 0; i < bytes.size(); i++)
	    bs[i] = bytes.get(i).byteValue();

	return bs;
    }

    /** Given a byte array (such as a candidate word from a
     * dictionary), produce some human-readable version of it.
     **/
    public String bytesToString(byte[] candidate)
    {
	StringBuffer sb = new StringBuffer();
	for (int i = 0; i < candidate.length; i++)
	    sb.append(byteToChar(candidate[i]));
	return sb.toString();
    }

    /** Parse a ciphertext, produce a set of words. Note that word
     * candidates do not need to be filled in. This function is also
     * used when creating a new dictionary file by calling it with one
     * line of the dictionary at a time; the first returned word is
     * then considered a candidate. 
     **/
    public ArrayList<Word> cipherTextToWords(String ciphertext)
    {
	ArrayList<Word> words = new ArrayList<Word>();

	String[] toks = ciphertext.split("\\s+|-|\\.|,|;|/|\\+|\\(|\\)|\\\\");

	int wordnum = 0;

	for (int i = 0; i < toks.length; i++)
	    {
		byte[] bs = stringToBytes(toks[i]);
		if (bs.length > 0)
		    {
			Word word = new Word(toks[i], wordnum++, bs);
			if (toks[i].charAt(0)=='^')
			    word.enabled=false;
			words.add(word);
		    }
	    }

	return words;
    }

    /** Produce a byte[] which contains the mapped values of all
	ciphertext characters, with -1 used to mark word
	boundaries. Multiple spaces are converted to a single space.
	This function is primarily for use by computeScore.
    **/
    public byte[] cipherTextToCipherBytes(String cipherText)
    {
	ArrayList<Byte> bytes = new ArrayList<Byte>();

	boolean lastWasSpace = false;

	for (int i = 0; i < cipherText.length(); i++)
	    {
		char c = cipherText.charAt(i);
		byte b = charToByte(c);

		// strip multiple consecutive spaces
		if (b<0 && lastWasSpace)
		    continue;

		lastWasSpace = (b<0);

		bytes.add(b);
	    }

	byte[] bs = new byte[bytes.size()];
	for (int i = 0; i < bytes.size(); i++)
	    bs[i] = bytes.get(i).byteValue();

	return bs;	
    }

    /** Given some text and a map, produce a new string while
     * performing a mapping. The original case and punctuation of the
     * string should be reproduced as well as possible. **/
    public abstract String translateString(String ciphertext, Map map);

    /** Compute the score for a solution. **/
    public abstract double computeScore(byte[] cipherbytes, MapSet map);

    /** Used to rank candidates... not necessarily related to the solution's score. **/
    public abstract double computeCandidateScore(byte[] candidate);

    /** Supports SpaceDecryptoSolver **/
    public abstract String translateWithSpaces(byte[] chars, int numwords, int wordpos[], int wordlen[], Map map);
}
