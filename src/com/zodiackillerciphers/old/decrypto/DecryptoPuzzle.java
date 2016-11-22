package com.zodiackillerciphers.old.decrypto;

import java.util.*;

public class DecryptoPuzzle
{
    public String cipherText;
    public byte[] cipherBytes;

    public Language lang;
    public ArrayList<Word> words;
    public Map initialMap;

    String clues ="";

    public int[] histogram; // for each ciphertext letter, how many times does it appear?
    public byte[] historder; // ciphertext letters, in decreasing order of frequency

    public DecryptoPuzzle(Dictionary dict, String cipherText)
    {
	this.cipherText = despacePuzzle(cipherText.trim());
	
	lang = dict.getLanguage();

	words = lang.cipherTextToWords(this.cipherText);

	DecryptoSolverOptimizations.removeDuplicateWords(this);

	for (Word w : words)
	    {
		w.candidates = Candidate.toCandidates(dict.lookup(w.pattern), lang);
		if (w.candidates == null || w.candidates.length == 0)
		    w.enabled = false;
	    }

	this.cipherBytes = lang.cipherTextToCipherBytes(this.cipherText);

	initialMap = new Map(lang.letterCount());

	ArrayList<Hist> hists = new ArrayList<Hist>();
	for (int i = 0; i < lang.letterCount(); i++)
	    hists.add(new Hist(i));

	for (Word w : words)
	    {
		for (int i = 0; i < w.word.length; i++)
		    hists.get(w.word[i]).count++;
	    }

	histogram = new int[lang.letterCount()];

	for (int i = 0; i < lang.letterCount(); i++)
	    histogram[i] = hists.get(i).count;

	Collections.sort(hists);

	historder = new byte[lang.letterCount()];

	for (int i = 0; i < lang.letterCount(); i++)
	    {
		//System.out.println(((char) ('A'+hists.get(i).letter))+" "+hists.get(i).count);
		historder[i] = (byte) hists.get(i).letter;
	    }

	if (DecryptoParameters.verbosity >=2) {
	    System.out.printf("%15s  %15s  %10s  %s\n", "ciphertext", "pattern", "cands", "enabled");
	    for (Word w: words)
		{
		    System.out.printf("%15s  %15s  %10d  %b\n", w.text, lang.bytesToString(w.pattern), w.candidates.length, w.enabled);
		}
	}
    }

    static class Hist implements Comparable<Hist>
    {
	int count;
	int letter;
	public Hist(int letter)
	{
	    this.letter = letter;
	}

	public int compareTo(Hist h)
	{
	    return h.count - count;
	}
    }

    /** If they entered the puzzle in with a lot of spaces, T H E  E M E R G E N...,
	remove the extra spaces. **/
    static String despacePuzzle(String s)
    {
	// count the spaces. 
	int spacecount = 0;

	for (int i = 0; i < s.length(); i++)
	    {
		if (s.charAt(i)==' ')
		    spacecount++;
	    }

	double frac = ((double) spacecount)/s.length();

	// less than half the characters are spaces. I think the puzzle is encoded properly
	if (frac < .5)
	    return s;

	// now count the number of non-space characters which are followed by a space.
	spacecount = 0;
	for (int i = 0; i < s.length()-2; i++)
	    {
		if (s.charAt(i)==' ' && s.charAt(i+2)==' ')
		    spacecount++;
	    }
	
	frac = ((double) spacecount)/(s.length()-2);
	//	System.out.println(frac);
	if (frac < .4)
	    return s;

	// remove any occurence of one space, replace any occurence of
	// more than one space with just one space.
	StringBuffer sb = new StringBuffer();
	int spacerun = 0;
	for (int i = 0; i < s.length(); i++)
	    {
		if (s.charAt(i)==' ')
		    {
			spacerun++;
			continue;
		    }

		// it's not a space.

		// if we didn't see many spaces, just encode this directly out.
		if (spacerun<=1)
		    sb.append(s.charAt(i));
		else
		    {
			sb.append(' ');
			sb.append(s.charAt(i));
		    }

		spacerun = 0;

	    }
	return sb.toString();
    }


    protected DecryptoPuzzle()
    {
    }

    public DecryptoPuzzle copy()
    {
	DecryptoPuzzle dp = new DecryptoPuzzle();
	dp.cipherText = cipherText;
	dp.cipherBytes = cipherBytes;
	dp.lang = lang;
	dp.initialMap = initialMap.copy();

	dp.historder = historder;
	dp.histogram = histogram;

	dp.words = new ArrayList<Word>();
	for (Word w : words)
	    dp.words.add(w.copy());

	return dp;
    }

    public void handleClues(String clues)
    {
	this.clues = clues;

	String toks[]=clues.split("[\\s,]+");

	for (int i = 0; i < toks.length; i++)
	    {
		if (toks[i].length()==0)
		    continue;

		String kv[] = toks[i].split("=");
		if (kv==null || kv.length!=2)
		    {
			System.out.println("Unable to parse clue: "+toks[i]);
			continue;
		    }

		if (kv[0].length() != kv[1].length())
		    {
			System.out.println("Must have same number of letters before and after equal sign: "+toks[i]);
			continue;
		    }
		
		if (kv[0].length() < 1)
		    {
			System.out.println("Didn't find a clue in this token: "+toks[i]);
			continue;
		    }

		for (int j = 0; j < kv[0].length() ; j++)
		    {
			byte k = lang.charToByte(kv[0].charAt(j));
			byte v = lang.charToByte(kv[1].charAt(j));
			initialMap.put(k, v);
		    }
	    }
    }

    public void scramble()
    {
	Map permutedMap = new Map(lang.letterCount());
	permutedMap.setToRandomPermutation(DecryptoParameters.scrambleAllowIdentityMappings);

	cipherText = lang.translateString(cipherText, permutedMap);

	String toks[]=clues.split("[\\s,]+");

	String newclues = "";
	for (int i = 0; i < toks.length; i++)
	    {
		if (toks[i].length()==0)
		    continue;

		String kv[] = toks[i].split("=");
		if (kv.length==2)
		    {
			newclues = newclues + lang.translateString(kv[0], permutedMap) + "=" + kv[1];
			if (i < toks.length-1)
			    newclues = newclues + " ";
		    }
	    }

	clues = newclues;
	initialMap = new Map(lang.letterCount());
	handleClues(newclues);
    }
}
