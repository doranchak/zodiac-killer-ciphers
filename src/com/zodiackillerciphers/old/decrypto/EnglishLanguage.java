package com.zodiackillerciphers.old.decrypto;

import java.util.*;

public class EnglishLanguage extends Language
{
    public char byteToChar(byte v)
    {
	if (v==-1)
	    return '~';

	return (char) ('A'+v);
    }

    public final byte charToByte(char c)
    {
	int v = ((int) c)&0x7f;
	if (v>'Z')
	    v-=32;

	if (v<'A' || v>'Z')
	    return -1;

	return (byte) (v-'A');
    }

    public int letterCount()
    {
	return 26;
    }

    public String translateString(String ciphertext, Map map)
    {
	StringBuffer sb = new StringBuffer();

	for (int i = 0; i < ciphertext.length(); i++)
	    {
		char c = ciphertext.charAt(i);

		if (Character.isLetter(c))
		    {
			int mc = map.get(charToByte(c));
			if (mc == -1) 
			    {
				sb.append("~");
				continue;
			    }
			
			if (Character.isLowerCase(c))
			    sb.append((char) ('a'+mc));
			else
			    sb.append((char) ('A'+mc));
		    }
		else
		    {
			sb.append(c);
		    }
	    }
	
	return sb.toString();
    }

    static final double unknownLetterPenalty = -10;

    public double computeCandidateScore(byte[] candidate)
    {
	int ma = 27; // any character
	int mb = 26; // a space
	double score = 0;

 	for (int i = 0; i < candidate.length; i++)
	    {
		int mc = (int) candidate[i];

		score += EnglishTrigrams.trigrams[ma][mb][mc];
		ma = mb;
		mb = mc;
	    }

	score += EnglishTrigrams.trigrams[ma][mb][26]; // add a space at the end.

	return score;
    }

    public double computeScore(byte[] cipherbytes, MapSet mapset)
    {
	int ma = 27;
	int mb = 26;
	double score = 0;

 	for (int i = 0; i < cipherbytes.length; i++)
	    {
		byte c = cipherbytes[i];
		int mc;

		if (c < 0)   // space?
		    mc = 26;
		else
		    mc = mapset.getMapping(c);

		if (mc == -1)
		    {
			mc = 27;
			score+=unknownLetterPenalty;
		    }

		score += EnglishTrigrams.trigrams[ma][mb][mc];
		ma = mb;
		mb = mc;
	    }

	score += EnglishTrigrams.trigrams[ma][mb][26]; // add a space at the end.

	return score;
    }

    final boolean isLetter(char c)
    {
	if ((c>='A' && c<='Z') || (c>='a' && c<='z'))
	    return true;
	return false;
    }

    public String translateWithSpaces(byte[] chars, int numwords, int wordpos[], int wordlen[], Map map)
    {
	StringBuffer sb = new StringBuffer();

	for (int i = 0; i < numwords; i++)
	    {
		int off = wordpos[i];
		for (int j = 0; j < wordlen[i]; j++)
		    sb.append(byteToChar((byte) map.get(chars[off + j])));
		sb.append(" ");
		//		if (i<(numwords-1))
		//		    sb.append(" ");
	    }

	// if there are undecoded words, spit them out.
	int off;
	if (numwords==0)
	    off = 0;
	else
	    off = wordpos[numwords-1]+wordlen[numwords-1];

	if (off < chars.length)
	    {
		sb.append(" [ ");
		while (off < chars.length)
		    {
			sb.append(byteToChar((byte) map.get(chars[off])));
			off++;
		    }
		sb.append(" ]");
	    }

	return sb.toString();
    }
}
