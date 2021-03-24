package com.zodiackillerciphers.lucene;

import java.util.ArrayList;
import java.util.List;

public class BestMatches {
	public float[] sums = new float[2];
	public float coverage = 0;
	public float medianScore = 0;
	public boolean[] cover;
	public List<Match> matches = new ArrayList<Match>();
}
