package com.zodiackillerciphers.old.decrypto;

import java.io.*;
import java.util.*;
import java.util.zip.*;
import java.net.*;

public class MakeDict
{
    public static void main(String args[])
    {
	try {
	    main_ex(args);
	} catch (Exception ex) {
	    System.out.println("ex: "+ex);
	    ex.printStackTrace();
	}
    }

    public static void main_ex(String args[]) throws Exception
    {
	HashMap<String, String> properties = new HashMap<String,String>();
	properties.put("version", "1.0");
	properties.put("language", "decrypto.EnglishLanguage");

	if (args.length!=2) 
	    {
		System.out.println("Usage: wordlist dictfile");
		return;
	    }

	Language lang = Dictionary.getLanguage(properties.get("language"));
	FileReader fr = new FileReader(args[0]);
	BufferedReader ins = new BufferedReader(fr);

	ArrayList<Word> words = new ArrayList<Word>();

	String line;
	while ((line = ins.readLine())!=null) 
	    {
		byte[] bs = lang.stringToBytes(line);
		if (bs == null)
		    continue;

		words.add(new Word(line, 0, bs));
	    }

	System.out.println("Sorting");
	Collections.sort(words, new Word.PatternComparator());

	System.out.println("Removing duplicates");
	for (int i = 0; i < words.size()-1; i++)
	    {
		if (Arrays.equals(words.get(i).word, words.get(i+1).word))
		    {
			//			System.out.println("duplicate found at index: "+i);
			words.remove(i);
		    }
	    }

	RandomAccessFile outs = new RandomAccessFile(args[1], "rw");

	outs.writeInt(Dictionary.MAGIC);
	outs.writeInt(properties.size());
	for (String s: properties.keySet())
	    {
		outs.writeUTF(s);
		outs.writeUTF(properties.get(s));
	    }

	int nwords = words.size();
	int firstword = 0;
	int lastword = 1;

	while (lastword < nwords)
	    {
		if (Arrays.equals(words.get(firstword).pattern, 
				  words.get(lastword).pattern))
		    {
			lastword++;
			continue;
		    }

		emit(outs, words, firstword, lastword);
		firstword = lastword;
		lastword = firstword+1;
	    }

	if (firstword < nwords)
	    emit(outs, words, firstword, lastword);

	System.out.println("done");
    }

    static void emit(RandomAccessFile outs, ArrayList<Word> words, int first, int last) throws IOException
    {
	boolean verbose = false;

	if (last-first==0)
	    return;

	outs.writeByte(0xff); // marker byte
	outs.write(words.get(first).word.length); // length of the word

	if (verbose)
	    System.out.println("pattern ct: "+(last-first));

	for (int i = first; i < last; i++)
	    {
		Word w = words.get(i);
		outs.write(w.word);
	    }
    }
}
