package com.zodiackillerciphers.old.decrypto;

/** Planner determines what we do at every step of the search **/
public interface Planner
{
    public static final int WORD_FLAG = 0x100000, LETTER_FLAG = 0x200000;
    public static final int MASK = 0xffff;

    /** factory method **/
    public Planner make(DecryptoPuzzle p);

    public int getAction(int wordlist[], int solvedwords, MapSet map);
}
