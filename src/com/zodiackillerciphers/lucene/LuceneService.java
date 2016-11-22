package com.zodiackillerciphers.lucene;

import java.io.File;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import com.zodiackillerciphers.io.FileUtil;
import com.zodiackillerciphers.lucene.DictionaryIndexer;
import com.zodiackillerciphers.lucene.Results;
import com.zodiackillerciphers.lucene.Scorer;

/**
 * Lightweight Lucene service API. This service is instantiated by Spring and
 * made available to the entire web application.
 */
public class LuceneService {
	/** IndexSearcher. The main entry point for Lucene-related operations. Be careful using this - it is shared by all threads! */
	private static IndexSearcher is = null;
	
	public static boolean DEBUG = false;
	public static String indexDir;
	public static Version VERSION = Version.LUCENE_35;
	static Analyzer analyzer = null;
	static QueryParser parser = null;
	
	private LuceneService() {
	}
	
	static void say(String msg) {
		Date d = new Date();
		System.out.println(d.getTime() + ": " + msg);
	}
	
	public static Analyzer analyzer() {
		if (analyzer == null)
		analyzer = new StandardAnalyzer(LuceneService.VERSION, new HashSet<String>());
		return analyzer;
	}
	
	public static QueryParser parser() {
		if (parser == null) 
		parser = new QueryParser(LuceneService.VERSION, "word",
				LuceneService.analyzer());
		return parser;
	}
	
	public static synchronized void init() {
		throw new RuntimeException("Please supply an index directory");
	}
	
	
	
	public static synchronized void init(String indexDir) {
		LuceneService.indexDir = indexDir;
		/** Create a single IndexSearcher for all threads.  This avoids the "too many files open" error, and other possible performance problems.
		 * This method is synchronized because we don't want more than one thread initializing the IndexSearcher at the same time. */
		if (is == null) {
			try {
				say("Creating new IndexSearcher for [" + indexDir + "]");
				Directory dir = FSDirectory.open(new File(indexDir));
				is = new IndexSearcher(dir, true);
				say("Done creating new IndexSearcher");
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}
	}

	/** reload the index searcher */
	public static synchronized void reload() {
		if (is == null) return;
		try {
			say("reopening the index reader...");
			IndexReader r = is.getIndexReader();
			say("isCurrent? " + r.isCurrent());
			is = new IndexSearcher(r.reopen());
			say("done reopening the index reader.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Results query(String q) {
		return query(q, (Object[]) null, 10);
	}

	public static Results query(String q, Object[] sort, int num) {
		init(indexDir);
		Results results = new Results();
		results.docs = new ArrayList<Document>();
		try {
			QueryParser parser = new QueryParser(VERSION,
					"word", analyzer());
			parser.setAllowLeadingWildcard(true);
			Query query = parser.parse(q);
			
			long start = System.currentTimeMillis();
			
			if (sort == null || sort.length == 0)
				results.topDocs = is.search(query, num);
			else
				results.topDocs = is.search(query, null, num, sortFrom(sort)); // include sort parameters
			
			long end = System.currentTimeMillis();
			if (DEBUG) say("Found " + results.topDocs.totalHits + " document(s) (in "
					+ (end - start) + " milliseconds) that matched query '" + q
					+ "':");
			results.scores = new HashMap<Document, Float>();
			
			for (ScoreDoc scoreDoc : results.topDocs.scoreDocs) {
				Document doc = is.doc(scoreDoc.doc);
				if (DEBUG) say(doc.toString());
				//logger.info(doc.toString());
				results.docs.add(doc);
				results.scores.put(doc, scoreDoc.score);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;
	}

	public static Results query(String q, Sort s, int num) {
		Results results = new Results();
		results.docs = new ArrayList<Document>();
		try {
			QueryParser parser = new QueryParser(VERSION,
					"SEARCH_TEXT", LuceneService.analyzer());
			parser.setAllowLeadingWildcard(true);
			Query query = parser.parse(q);
			
			long start = System.currentTimeMillis();
			
			results.topDocs = is.search(query, null, num, s); // include sort parameters
			
			long end = System.currentTimeMillis();
			if (DEBUG) say("Found " + results.topDocs.totalHits + " document(s) (in "
					+ (end - start) + " milliseconds) that matched query '" + q
					+ "':");
			results.scores = new HashMap<Document, Float>();
			
			for (ScoreDoc scoreDoc : results.topDocs.scoreDocs) {
				Document doc = is.doc(scoreDoc.doc);
				if (DEBUG) say(doc.toString());
				//logger.info(doc.toString());
				results.docs.add(doc);
				results.scores.put(doc, scoreDoc.score);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;
	}
	static Sort sortFrom(Object[] sort) {
		if (sort == null) return null;
		if (sort.length == 0) return null;
		
		SortField[] sf = new SortField[sort.length];
		for (int i=0; i<sf.length; i++) {
			Object[] pair = (Object[]) sort[i];
			if (pair == null || pair.length < 2) continue;
			int sfType = SortField.STRING;
			
			if (pair.length == 3) {
				String type = (String) pair[2];
				if ("int".equalsIgnoreCase(type)) {
					sfType = SortField.INT;
				}
			}  
			sf[i] = new SortField((String)pair[0], sfType, (Boolean)pair[1]);
		}
		return new Sort(sf);
	}
	
	public static String wordFrom(Document d) {
		if (d==null) return null;
		return value(d, "word");
	}
	
	public static float scoreFrom(Document d) {
		if (d==null) return 0f;
		return Float.valueOf(value(d, "score"));
	}
	public static String freqFrom(Document d) {
		return value(d, "frequency");
	}
	public static int freqAsIntFrom(Document d) {
		String val = value(d, "frequency");
		if (val == null) return 0;
		try {
			return Integer.valueOf(val);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static String value(Document d, String name) {
		if (d==null) return null;
		if (d.getFieldable(name) == null) return null;
		return d.getFieldable(name).stringValue();
	}
	
	
	public static void testZ2() {
		String[] z = { "i", "like", "killing", "people", "because", "it", "is",
				"so", "much", "fun", "it", "is", "more", "fun", "than",
				"killing", "wild", "game", "in", "the", "forrest", "because",
				"man", "is", "the", "most", "dangeroue", "animal", "of", "all",
				"to", "kill", "something", "gives", "me", "the", "most",
				"thrilling", "experence", "it", "is", "even", "better", "than",
				"getting", "your", "rocks", "off", "with", "a", "girl", "the",
				"best", "part", "is", "thae", "when", "i", "die", "i", "will",
				"be", "reborn", "in", "paradice", "and", "all", "the", "i",
				"have", "killed", "will", "become", "my", "slaves", "i",
				"will", "not", "give", "you", "my", "name", "because", "you",
				"will", "try", "to", "sloi", "down", "or", "stop", "my",
				"collecting", "of", "slaves", "for", "my", "afterlife",
				"ebeorietemethhpiti" };
		
		//init();
		for (String s : z) {
			Results r = query("word:"+s);
			say(s+":");
			if (r.docs != null && r.docs.size() > 0) {
				String line = " - ";
				for (Document d : r.docs) {
					line += wordFrom(d) + " " + freqFrom(d) + ", ";
				}
				say(line);
			}
			
		}
	}
	
	public static void testRand() {
		//init();
		
		Object[][] counts = new Object[][] {
				{"a", 82},
				{"b", 15},
				{"c", 28},
				{"d", 43},
				{"e", 127},
				{"f", 22},
				{"g", 20},
				{"h", 61},
				{"i", 70},
				{"j", 02},
				{"k", 8},
				{"l", 40},
				{"m", 24},
				{"n", 67},
				{"o", 75},
				{"p", 19},
				{"q", 01},
				{"r", 60},
				{"s", 63},
				{"t", 91},
				{"u", 28},
				{"v", 10},
				{"w", 24},
				{"x", 02},
				{"y", 20},
				{"z", 01}
		};
		
		String[] pool;
		int sum = 0;
		for (int i=0; i<counts.length; i++) {
			sum += (Integer)counts[i][1];
		}
		pool = new String[sum]; int count = 0;
		for (int i=0; i<counts.length; i++) {
			int num = (Integer)counts[i][1];
			String letter = (String) counts[i][0]; 
			for (int j=0; j<num; j++) pool[count++] = letter;
		}
		
		StringBuffer cipher = new StringBuffer();
		for (int i=0; i<408; i++) {
			cipher.append(pool[(int)(Math.random()*pool.length)]);
		}
		System.out.println(cipher);
		
		testCipher(cipher.toString());
		
	}
	
	public static void testZ() {
		testCipher("ilikekillingpeoplebecauseitissomuchfunitismorefunthankillingwildgameintheforrestbecausemanisthemoatdangertueanamalofalltokillsomethinggivesmethemoatthrillingexperenceitisevenbetterthangettingyourrocksoffwithagirlthebestpartofitiathaewhenidieiwillbereborninparadicesndalltheihavekilledwillbecomemyslavesiwillnotgiveyoumynamebecauseyouwilltrytosloidownorstopmycollectingofslavesformyafterlifeebeorietemethhpiti");
		
	}
	public static void testCipher(String cipher) {
		//init();
		
		String z = cipher;

		Date d1 = new Date();
		say("start");
		String word;
		Results r;

		float sum = 0;
		for (int L=4; L<12; L++) {
			for (int i=0; i<z.length()-(L-1); i++) {
				word = z.substring(i,i+L);
				//say(word+":");
				r = query("word:" + word);
				if (r.docs != null && r.docs.size() > 0) {
					String line = "L: " + L + ", ";
					for (Document d : r.docs) {
						line += wordFrom(d) + " (" + freqFrom(d) + ", " + value(d, "wordrank") + ", " + value(d, "score") + ") ";
						sum += Float.valueOf(value(d, "score"));
					}
					say(line);
				}
			}
		}
		say("end");
		Date d2 = new Date();
		System.out.println(d2.getTime() - d1.getTime() + " ms");
		System.out.println("Total score: " + sum);
	}
	
	public static void test9() {
		Results r = query("word:?????????", (Object[])null, 10000000);
		for (Document d : r.docs) {
			System.out.println(wordFrom(d));
		}
		
	}

	public static void test13() {
		Results r = query("+word:o*", Scorer.freqScore, 10000000);
		int i=0;
		for (Document d : r.docs) {
			System.out.println(freqAsIntFrom(d) + ", " + wordFrom(d));
		}
		
	}
	
	public static void main(String[] args) {
		//test9();
		LuceneService.init("/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/dictionaries/lucene-index");
		//test13();
		List<String> list = FileUtil.loadFrom("/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/ray-grant/myth-words-all-1.txt");
		for (String word : list) {
			Results r= query("+word:" + word);
			int freq = 0;
			if (r.docs.size() > 0) {
				Document d= r.docs.get(0);
				freq = freqAsIntFrom(d);
			}
			System.out.println(freq + ", " + word.toUpperCase());
			
		}
			
		//testZ();
		//testRand();
	}
	
}
