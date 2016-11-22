package com.zodiackillerciphers.lucene;

import java.util.Comparator;

public class Edge {
	public Match m1 = null;
	public Match m2 = null;
	Character p1 = null, p2 = null, c = null; // the conflicted plaintext letters, and the cipher symbol
	public Edge(Match m1, Match m2) {
		this.m1 = m1;
		this.m2 = m2;
	}
	/* sort matches by score ascending */
	public static Comparator<Edge> comparatorMin = new Comparator<Edge>() {
		public int compare(Edge e1, Edge e2) {
			float sum1 = e1.m1.score + e1.m2.score;
			float sum2 = e2.m1.score + e2.m2.score;
			return Float.compare(sum1,sum2);
		}
	};
	
	public String toString() {
		return "word1 [" + m1.toString() + "] word2 [" + m2.toString() + "] plaintext [" + p1 + ", " + p2 + "] cipher symbol [" + c + "] score sum [" + (m1.score+m2.score) + "]";
	}
	
}
