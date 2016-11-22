package com.zodiackillerciphers.old.decrypto;

import java.util.*;

public class DictLevel
{
    Map map; // the map that is enforced at this level
    DumbHashMap wordcache = new DumbHashMap(1024);
    DictLevel parent;
    Dictionary root;
    int depth = 0;

    DumbListAllocator dla = new DumbListAllocator();

    WordSet sets[] = new WordSet[1024];
    int nsets = 0;

    void init()
    {
	for (int i = 0; i < sets.length; i++)
	    {
		sets[i] = new WordSet();
		sets[i].words = new DumbList(dla);
		sets[i].ambg = new byte[16];
	    }
    }

    WordSet getWordSet()
    {
	if (nsets < sets.length)
	    {
		WordSet set = sets[nsets++];
		set.nambg = 0;
		set.words.reset();
		return set;
	    }

	WordSet set = new WordSet();
	set.words = new DumbList(dla);
	set.ambg = new byte[16];
	return set;
    }

    public DictLevel(Dictionary root, Map map)
    {
	this.root = root;
	this.map = map;
	init();
    }

    public DictLevel(DictLevel dl, Map map)
    {
	this.parent = dl;
	this.map = map;
	this.depth = dl.depth+1;
	init();
    }

    public static class WordSet
    {
	byte ambg[]; // minimal index of ambiguous letters
	int  nambg;  // number of valid entries in ambg;

	int id;
	DumbList words;
    }

    /** @param words The input puzzle
	@param offset the index of the first character of this word
	@param len The length of this word
	@param id An identifier which uniquely identifies this word's ciphertext
	@param wordmask The set of characters in this word
	ciphertext.
    **/
    public WordSet lookup(byte[] words, int offset, int len, int id)
    {
	WordSet ws = (WordSet) wordcache.get(id);

	// if we already have it, return it.
	if (ws!=null)
	    {
		//		System.out.println("HIT");
		return ws;
	    }

	// we need to get it from our parent or the root
	if (parent != null)
	    {
		ws = parent.lookup(words, offset, len, id);

		// if our map changed one of the letters in the word, then compute
		// the reduced set.
		ws = prune(ws, words, offset, len);

		wordcache.put(ws.id, ws);

		return ws;
	    }
	else
	    {
		// we need to do a lookup in the Dictionary.
		// build the word array.
		byte[] w = new byte[len];
		for (int i = 0; i < len; i++)
		    w[i] = words[offset+i];
		
		ws = getWordSet(); 
		ws.id = id;
		ArrayList<byte[]> ww = root.lookup(Word.makePattern(w));
		ws.words.add(ww);

		// reduce the word set if our mask affected this word
		//		if ((wordmask & mask)!=0 || true)
		ws = prune(ws, words, offset, len);

		wordcache.put(ws.id, ws);

		long seen = 0;
		// make ambg point to only unique letters (skip doubled letters)
		ws.nambg = 0;
		for (int i = 0; i < len ;i++)
		    {
			int c = words[offset+i]&0xff;
			if ((seen&(1<<c))!=0) // repeated letter.
			    continue;
			seen |= (1<<c);  // mark this letter as seen.

			ws.ambg[ws.nambg++]=(byte) i;
		    }
		return ws;
	    }
    }

    /** Make the word set only include words that are consistent with
	our mapping. We need to make a copy of the underlying
	candidate list since it might be modified (right?).
    **/
    public WordSet prune(WordSet ws, byte[] words, int offset, int len)
    {
	WordSet prunedws = getWordSet();
	prunedws.id = ws.id;

	boolean reduced = false;

	for (DumbListChunk chunk = ws.words.getHead(); chunk != null; chunk = chunk.next)
	    for (int i = 0; i < chunk.sz; i++)
		{
		    byte[] thisword = (byte[]) chunk.o[i]; //ws.words.get(i);
		    
		    if (map.isMappingOkay(words, offset, thisword, ws.ambg, ws.nambg))
			prunedws.words.add(thisword);
		    else
			reduced = true;
		}


	// if this wordlist is identical to our parent's, just use the
	// parents. This frees up more buffer space.
	/*
	if (!reduced)
	    {
		// no changes. Link the wordset to our table.
		wordcache.put(ws.id, ws);

		prunedwords.unget();

		return ws;
	    }
	*/
	// we reduced the set, create a new word set.
	/*	WordSet prunedws = getWordSet(); //new WordSet();
	prunedws.id = ws.id;
	prunedws.words = prunedwords;
	*/
	// compute minimal set of ambiguous letters i.e., testing just
	// this subset is the same as testing the whole word. This is
	// because A) some letters may be repeated and B) some letters
	// may already be known by the map.
	
	//	prunedws.ambg = new int[ws.nambg];
	if (false)
	    {
		prunedws.nambg = 0;
		
		for (int i = 0; i < ws.nambg; i++)
		    {
			int idx = ws.ambg[i];
			int c = words[offset+idx]&0xff;
			
			// duplicate letters were already eliminated
			// when the first wordset was created
			
			// this letter has already been mapped (successfully)
			if (map.isMapped((byte) c)) 
			    continue;
			
			// new letter to remember.
			prunedws.ambg[prunedws.nambg++] = (byte) idx;
		    }
	    }
	else
	    {

		prunedws.ambg = ws.ambg;
		prunedws.nambg = ws.nambg;
	    }

	return prunedws;
    }

    public void reset(DictLevel parent, Map map)
    {
	this.parent = parent;
	this.map = map;
	wordcache.reset();
	dla.reclaimAll();
	nsets=0;
    }
}
