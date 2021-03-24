package com.zodiackillerciphers.old.decrypto;

import java.io.*;
import java.util.*;
import java.util.zip.*;
import java.net.*;
import java.lang.reflect.*;

public class Dictionary
{
    BufferedRandomAccessFile raf;
    long filesize;
    long lowoff;
    HashMap<String,String> properties = new HashMap<String,String>();
    WeakHashMap<ByteArrayWrapper, ArrayList<byte[]>> patternCache = new WeakHashMap<ByteArrayWrapper, ArrayList<byte[]>>();
    //HashMap<ByteArrayWrapper, ArrayList<byte[]>> patternCache = new HashMap<ByteArrayWrapper, ArrayList<byte[]>>();
    static final int MAGIC = 0xDEC0DED;

    /** Use this constructor only when creating a new dictionary **/
    public Dictionary()
    {
    }

    public Dictionary(String path) throws IOException
    {
	raf = new BufferedRandomAccessFile(path, "r");

	filesize = raf.length();

	int id = raf.readInt();
	if (id != MAGIC)
	    throw new IOException("Not a dictionary file");

	int nprops = raf.readInt();
	for (int i=0;i<nprops;i++)
	    {
		String key = raf.readUTF();
		String value = raf.readUTF();
		properties.put(key,value);
	    }

	lowoff = raf.getFilePointer();
    }

    public String getProperty(String key)
    {
	return properties.get(key);
    }

    public ArrayList<byte[]> lookup(byte[] pattern)
    {
	return lookup(pattern, false);
    }

    public ArrayList<byte[]> lookup(byte[] pattern, boolean recycle)
    {
	try {
	    return lookup_ex(pattern, recycle);
	} catch (IOException ex) {
	    System.out.println("ex: "+ex);
	    ex.printStackTrace();
	    return new ArrayList<byte[]>();
	}
    }

    public ArrayList<byte[]> readAll() throws IOException
    {
	ArrayList<byte[]> pats = new ArrayList<byte[]>();

	raf.seek(lowoff);

	try {
	    while (true)
		{
		    while (raf.readByte()!=(byte) 0xFF);
		    
		    int wordlength = raf.readByte()&0xff;
		    if (wordlength==0)
			continue;

		    int wcount = raf.readShort();

		    for (int i = 0; i < wcount; i++)
			{
			    byte[] thisword = new byte[wordlength];
			    raf.readFully(thisword);
			    pats.add(thisword);
			}
		}
	} catch (IOException ex) {
	    return pats;
	}
    }

    public ArrayList<byte[]> lookup_ex(byte[] pattern, boolean recycle) throws IOException
    {
	ArrayList<byte[]> matches;

	/* have we already looked up this pattern? if so, return a copy of the candidate list */
	ByteArrayWrapper pw = new ByteArrayWrapper(pattern);
	matches = patternCache.get(pw);
	if (matches != null) 
	    {
		if (recycle)
		    return matches;
		else
		    return new ArrayList<byte[]>(matches);
	    }
	//	System.out.println("M");

	/* we haven't seen this pattern before. Create a new list (and add it to the cache */
	matches = new ArrayList<byte[]>();
	patternCache.put(pw, matches);

	long low = lowoff;
	long high = filesize-1;

	byte[] thisword, thispattern;
	int wordlength;
	long lastpos = -1;
	int nwords=0;

	while (true)
	    {
		long pos = (low + high) / 2;

		if (pos == lastpos)
		    return matches;

		lastpos = pos;

		raf.seek(pos);

		//		System.out.println(String.format("%8d %8d %8d", low, high, pos));

		int cnt = 0;

		try {
		    while (raf.readByte()!=(byte) 0xFF)
			cnt++;
		} catch (EOFException ex) {
		    // we're too high
		    high = pos;
		    continue;
		}

		wordlength = raf.readByte()&0xff;
		nwords = raf.readShort();

		thisword = new byte[wordlength];
		raf.readFully(thisword);

		thispattern = Word.makePattern(thisword);

		int cmp = Word.compareArrays(thispattern, pattern);
		if (cmp != 0 && matches.size()>0)
		    return matches;

		if (cmp < 0) {
		    low = pos;
		    continue;
		}
		if (cmp > 0) {
		    high = pos;
		    continue;
		}
		
		break;
	    }
	// cmp == 0, we've found our record. read it!
	
	// the word we already read should be added...
	matches.add(thisword);

	for (int i = 0; i < nwords-1; i++)
	    {
		thisword = new byte[wordlength];
		try {
		    raf.readFully(thisword);
		} catch (EOFException ex) {
		    break;
		}
		thispattern = Word.makePattern(thisword);
		matches.add(thisword);
	    }

	return matches;
    }

    public static Language getLanguage(String classname)
    {
	if (classname == null) 
	    classname = "decrypto.EnglishLanguage";

	try {
	    Class theClass = ClassLoader.getSystemClassLoader().loadClass(classname);
	    Constructor theConstructor = theClass.getConstructor();
	    return (Language) theConstructor.newInstance();
	} catch (Exception ex) {
	    System.out.println("Couldn't instantiate language: "+classname);
	    return null;
	}
    }

    public Language getLanguage()
    {
	return getLanguage(getProperty("langclass"));
    }

    public static void sortAndPruneWords(ArrayList<Word> words)
    {
	//	System.out.println("Sorting");
	Collections.sort(words, new Word.PatternComparator());

	//	System.out.println("Removing duplicates");
	for (int i = 0; i < words.size()-1; i++)
	    {
		// remove any empty entries
		while (i < words.size() && 
		       (words.get(i).text.trim().length()==0 || words.get(i).word.length==0))
		    {
			words.remove(i);
		    }

		while (i < words.size()-1 && Arrays.equals(words.get(i).word, words.get(i+1).word))
		    {
			words.remove(i);
		    }
	    }
    }

    public void write(String path, ArrayList<Word> words) throws IOException
    {
	sortAndPruneWords(words);

	///////////////////////////////////////////////////////
 	RandomAccessFile outs = new RandomAccessFile(path, "rw");

	outs.writeInt(MAGIC);
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

	outs.close();
    }

    static void emit(RandomAccessFile outs, ArrayList<Word> words, int first, int last) throws IOException
    {
	boolean verbose = false;

	if (last-first==0)
	    return;

	outs.writeByte(0xff); // marker byte
	outs.write(words.get(first).word.length); // length of the word
	outs.writeShort(last-first);

	if (verbose)
	    System.out.println("pattern ct: "+(last-first));

	for (int i = first; i < last; i++)
	    {
		Word w = words.get(i);
		outs.write(w.word);
	    }
    }
}

