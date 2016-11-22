package com.zodiackillerciphers.lucene;

import java.util.List;

public class PositionMatches {
	public List<Match> matches1;
	public List<Match> matches2;
	public PositionMatches(List<Match> m1, List<Match> m2) {
		matches1 = m1;
		matches2 = m2;
	}
}
