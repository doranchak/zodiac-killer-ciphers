package com.zodiackillerciphers.old.decrypto;

import java.util.*;

public class Candidate
{
    /** This score is used to rank candidates for the purposes of
	searching. It is not used for scoring solutions (in general,
	at least, because trigrams span multiple words.) 
    **/
    public double score;
    public byte[] cand;

    public Candidate(byte[] cand, double score)
    {
	this.cand = cand;
	this.score = score;
    }

    public static Candidate[] toCandidates(ArrayList<byte[]> cands, Language lang)
    {
	Candidate c[] = new Candidate[cands.size()];
	for (int i = 0; i < cands.size(); i++)
	    c[i] = new Candidate(cands.get(i), lang.computeCandidateScore(cands.get(i)));

	Arrays.sort(c, new CandidateScoreComparator());
	return c;
    }

    public static class CandidateScoreComparator implements Comparator<Candidate>
    {
	public int compare(Candidate a, Candidate b)
	{
	    return -Double.compare(a.score, b.score);
	}
    }
}
