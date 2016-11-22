package com.zodiackillerciphers.old.decrypto;

public interface DecryptoListener
{
    /** Called when a solution is found. If you want to store the map,
     * you must make a copy (or create a Solution object. 
     **/
    public void decryptoSolution(MapSet set, double score);

    public void decryptoPartialSolution(MapSet set, double score);

    public void decryptoProgress(double progress);
}
