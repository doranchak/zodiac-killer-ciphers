package com.zodiackillerciphers.old.decrypto;

public class DumbListChunk
{
    DumbListChunk next;
    Object o[];
    int sz;

    public DumbListChunk()
    {
	o = new Object[64];
    }

    public void clear()
    {
	sz = 0;
	next = null;
    }
}
