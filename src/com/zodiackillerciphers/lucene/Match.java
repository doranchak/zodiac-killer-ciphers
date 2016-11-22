package com.zodiackillerciphers.lucene;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.lucene.document.Document;


public class Match {
	public String word;
	public float score;
	public String[] patterns;
	public int position;
	public boolean deleted = false;
	
	/* sort matches by score ascending */
	public static Comparator<Match> comparatorMin = new Comparator<Match>() {
		public int compare(Match m1, Match m2) {
			return Float.compare(m1.score, m2.score);
		}
	};
	/* sort matches by score descending */
	public static Comparator<Match> comparatorMax = new Comparator<Match>() {
		public int compare(Match m1, Match m2) {
			return Float.compare(m2.score, m1.score);
		}
	};
	
	
	public static void testComparators() {
		List<Match> m = new ArrayList<Match>();
		for (int i=0; i<100; i++) {
			Match item = new Match();
			item.score = (float) Math.random();
			m.add(item);
		}
		Collections.sort(m, comparatorMin);
		System.out.println("min");
		for (Match item : m) System.out.println(item.score);
		Collections.sort(m, comparatorMax);
		System.out.println("max");
		for (Match item : m) System.out.println(item.score);

	}

	public static List<Match> matchesFrom(List<Document> d, int position, boolean zodiac) {
		List<Match> list = new ArrayList<Match>();
		if (d==null) return list;
		for (Document doc : d) list.add(matchFrom(doc, position, zodiac));
		return list;
	}
	
	public static Match matchFrom(Document d, int position, boolean zodiac) {
		Match m = new Match();
		float score;
		if (zodiac) score = Float.valueOf(LuceneService.value(d, "scorez"));
		else score = Float.valueOf(LuceneService.value(d, "score"));
		
		String word = LuceneService.value(d, "word");
		String pat = LuceneService.value(d, "patterns");
		String[] patterns = null;
		if (pat != null) patterns = pat.split(" ");
		
		m.score = score;
		m.word = word;
		m.patterns = patterns;
		m.position = position;
		return m;
	}
	
	public String toString() {
		return word + ", " + score + ", " + patternsString() + ", " + position;
	}
	public String patternsString() {
		if (patterns == null) return null;
		String p = "";
		for (int i=0; i<patterns.length; i++) p+=patterns[i] + " ";
		return p;
	}
	
	public boolean equals(Match m) {
		if (m.word == null) return false;
		if (this.word == null) return false;
		return this.word.equals(m.word);
	}
	
	public static void main(String[] args) {
		testComparators();
	}
}
