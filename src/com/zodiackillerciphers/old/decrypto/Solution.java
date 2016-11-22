package com.zodiackillerciphers.old.decrypto;

import java.util.*;

public class Solution implements Scorable
{
    public String solution;
    public double score;

    public Solution(DecryptoPuzzle puzzle, Map map, double score)
    {
	this.solution = puzzle.lang.translateString(puzzle.cipherText, map);
	this.score = score;
    }

    public Solution(String solution, double score)
    {
	this.solution = solution;
	this.score = score;
    }

    public double getScore()
    {
	return score;
    }

    public int compareTo(Solution s)
    {
	return -Double.compare(score, s.score);
    }

    public String toString()
    {
	return String.format("%.3f",score);
    }

}
