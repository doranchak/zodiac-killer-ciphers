package com.zodiackillerciphers.lucene;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;
import com.zodiackillerciphers.lucene.Results;


/**
 * Crawler. Gets data from a summary view or table, and adds or updates it in
 * the Lucene index.
 */
public class DictionaryIndexer {

	public static String ZODIAC_INDEX = "/Users/doranchak/projects/work/java/zodiac/letters/index";
	public static String DICTIONARY = "/Users/doranchak/projects/work/java/zodiac/letters/500k.txt";
	public static String ZODIAC_DICTIONARY = "/Users/doranchak/projects/work/java/zodiac/letters/dict-zodiac-minus-408.txt";

	static Map<Integer, Integer> maxE;
	static Map<Integer, Integer> maxZ;
		
	/**
	 * Writer singleton. Only one index writer can write to an index at a time,
	 * but many threads can access the same writer to update the index.
	 */
	private static IndexWriter writer;

	public static void init() {

		try {
			StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_32, new HashSet<String>());
			Directory dir = FSDirectory.open(new File(ZODIAC_INDEX));
			IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_32,
					analyzer);
			writer = new IndexWriter(dir, conf);
		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			writer = null;
		} catch (LockObtainFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			writer = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			writer = null;
		}

	}

	static void checkWriter() {
		if (writer == null)
			init();
	}

	/** commit pending changes submitted to the index writer */
	public static void writerCommit() throws Exception {
		checkWriter();
		writer.commit();
	}

	public static void writerOptimize() throws Exception {
		checkWriter();
		writer.optimize();
	}

	public static void writerAddDocument(Document d) throws Exception {
		checkWriter();
		writer.addDocument(d);
	}

	public static void eraseIndex() {
		try { // wipe out the dictionary index
			QueryParser parser = new QueryParser(Version.LUCENE_30, "ID",
					new StandardAnalyzer(Version.LUCENE_30, new HashSet<String>()));
			parser.setAllowLeadingWildcard(true);
			String str = "+word:*";
			Query query = parser.parse(str);
			
			writer.deleteDocuments(query);
		} catch (CorruptIndexException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public static Map<Integer, Integer> maximums(String fileName, boolean zodiac) {
		Map<Integer, Integer> maximums = new HashMap<Integer, Integer>();
		BufferedReader input = null;
		int counter = 0;
		try {
			input = new BufferedReader(new FileReader(new File(fileName)));
			String line = null; // not declared within while loop
			while ((line = input.readLine()) != null) {
				String[] split = line.split("\t");
				if (split.length == 4 || split.length == 2) {
					int f = Integer.valueOf(split[0]);
					if (!zodiac && f < 200) break;
					if (isAlpha(split[1])) {
						Integer key = split[1].length();
						Integer value;
						if (maximums.get(key) == null) value = 0;
						else value = maximums.get(key);
						
						value = Math.max(value, Integer.valueOf(split[0]));
						maximums.put(key, value);
						
						counter++;
						if ((counter % 1000) == 0) {
							System.out.println(counter + "...");
						}
					}
				}
			}
			System.out.println("scanned " + counter + " lines.");
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} 
		
		for (Integer key : maximums.keySet()) System.out.println("Max frequency for words of length " + key + ": " + maximums.get(key));
		try {
			input.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		return maximums;
	}
	
	public static String patterns(String word) { return patterns(word, false);} 
	
	public static String patterns(String word, boolean positions) {
		Map<Character, List<Integer>> map = new HashMap<Character, List<Integer>>();
		
		for (int i=0; i<word.length(); i++) {
			char ch = word.charAt(i);
			List<Integer> list = map.get(ch);
			if (list == null) list = new ArrayList<Integer>();
			list.add(i);
			map.put(ch, list);
		}
		
		StringBuffer sb = new StringBuffer();
		for (Character ch : map.keySet()) {
			List<Integer> list = map.get(ch);
			if (list.size() > 1) {
				
				List<Integer> pairs = new ArrayList<Integer>();
				for (int i=0; i<word.length(); i++) {
					char ch2 = word.charAt(i);
					if (ch2 == ch) {
						if (positions) {
							//if (sb.length() > 0 && sb.charAt(sb.length()-1) != ' ') sb.append(",");
							//sb.append(i);
							pairs.add(i);
						}
						else {
							sb.append("X");
						}
					}
					else if (!positions) sb.append("a");
				}
				//for (int i=0; i<pairs.size(); i++) System.out.println(pairs.get(i));
				//System.out.println("===");
				
				if (positions && pairs.size() > 0) {
					for (int i=0; i<pairs.size()-1; i++) {
						for (int j=i+1; j<pairs.size(); j++) {
							sb.append(pairs.get(i)+","+pairs.get(j)+" ");
						}
					}
				}
				if (!positions) sb.append(" ");
			}
		}
		return sb.toString();
		
	}
	
	static void docInsert(String[] split) {
		float wordrank = ((float)Integer.valueOf(split[0]))/maxE.get(split[1].length());
		//float score = split[1].length()*wordrank;
		float score = wordrank;
		Document d = new Document();
		d.add(new Field("word", split[1], Field.Store.YES,
				Field.Index.ANALYZED));
		d.add(new Field("frequency", split[0], Field.Store.YES,
				Field.Index.NO));
		d.add(new Field("wordrank", ""+wordrank, Field.Store.YES,
				Field.Index.NO));
		d.add(new Field("score", ""+score, Field.Store.YES,
				Field.Index.NOT_ANALYZED));
		d.add(new Field("pos", split[2], Field.Store.YES,
				Field.Index.ANALYZED));
		d.add(new Field("texts", split[3], Field.Store.YES,
				Field.Index.NO));
		d.add(new Field("patterns", patterns(split[1]), Field.Store.YES, Field.Index.ANALYZED));

		if (split[1].equals("that")) {
			System.out.println("docInsert " + d);
		}
		
		
		try {
			//deleteDocument(d);
			writerAddDocument(d);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	static void docZodiac(String[] split) {
		if (split[1].equals("that")) {
			System.out.println("docZodiac " + split[0] + " " + split[1]);
		}
		
/*		does it exist?  get old doc, retrieve its values for the new doc
		otherwise, create new doc with just the zodiac values, and copy zod frequencies to normal frequencies
	*/	
		float wordrank;
		float score;
		float wordrankz = ((float)Integer.valueOf(split[0]))/maxZ.get(split[1].length());;
		//float scorez = split[1].length()*wordrankz;
		float scorez = wordrankz;
		String freq;
		String pos = "";
		String texts = "";
		String freqz = ""+split[0];
		Results r = LuceneService.query("word:" + split[1]);
		if (r != null && r.docs != null && r.docs.size() > 0) {
			if (r.docs.size() > 1) System.out.println("why gt 1???");
			Document d = r.docs.get(0);
			freq = LuceneService.value(d, "frequency");
			wordrank = Float.valueOf(LuceneService.value(d, "wordrank"));
			score = Float.valueOf(LuceneService.value(d, "score"));
			pos = LuceneService.value(d, "pos");
			texts = LuceneService.value(d, "texts");
		} else {
			freq = freqz;
			wordrank = wordrankz;
			//score = scorez;
			score = 0.0000001f; // never saw this word before, so it probably has small significance.
		}
		
		//float wordrank = ((float)Integer.valueOf(split[0]))/maxE.get(split[1].length());
		//float score = split[1].length()*wordrank;
		Document d = new Document();
		d.add(new Field("word", split[1], Field.Store.YES,
				Field.Index.ANALYZED));
		d.add(new Field("frequency", freq, Field.Store.YES,
				Field.Index.NO));
		d.add(new Field("frequencyz", freqz, Field.Store.YES,
				Field.Index.NO));
		d.add(new Field("wordrank", ""+wordrank, Field.Store.YES,
				Field.Index.NO));
		d.add(new Field("wordrankz", ""+wordrankz, Field.Store.YES,
				Field.Index.NO));
		d.add(new Field("score", ""+score, Field.Store.YES,
				Field.Index.NOT_ANALYZED));
		d.add(new Field("scorez", ""+scorez, Field.Store.YES,
				Field.Index.NOT_ANALYZED));
		d.add(new Field("pos", pos, Field.Store.YES,
				Field.Index.ANALYZED));
		d.add(new Field("patterns", patterns(split[1]), Field.Store.YES, Field.Index.ANALYZED));
		d.add(new Field("zodiac","true", Field.Store.YES, Field.Index.ANALYZED));

		try {
			deleteDocument(d);
			writerAddDocument(d);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	
	static void processFile(String fileName, boolean zodiac) {
		BufferedReader input = null;
		int counter = 0;
		Set<String> seen = new HashSet<String>();
		try {
			// use buffering, reading one line at a time
			// FileReader always assumes default encoding is OK!
			input = new BufferedReader(new FileReader(new File(fileName)));
			String line = null; // not declared within while loop
			/*
			 * readLine is a bit quirky : it returns the content of a line MINUS
			 * the newline. it returns null only for the END of the stream. it
			 * returns an empty String if two newlines appear in a row.
			 */
			counter = 0;
			int ignored = 0;
			while ((line = input.readLine()) != null) {
				String[] split = line.split("\t");
				if (split.length == 4 || split.length == 2) {
					
					/* not zodiac:
					 * 	insert the new document as is.  or skip if word seen already, or if word has non-alpha char.
					 * zodiac:	
					 *  existing document?
					 *  	yes: retrieve, delete, populate new doc with old doc's fields, update new doc with zodiac info, save
					 *  	no: populate new doc with zodiac info, save
					 */
					int f = Integer.valueOf(split[0]);
					if (!zodiac && f < 200) break;
					if ((zodiac || !seen.contains(split[1])) && isAlpha(split[1])) {
						seen.add(split[1]);
						if (!zodiac) 
							docInsert(split);
						else {
							docZodiac(split);
						}
						counter++;
						try {
							if (counter % 1000 == 0) {
								System.out.println(counter + "...");
								writerCommit();
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						/*
						Entry entry = entry(split);
						seen.add(split[1]);
						Document d = document(split[1]);
						
						float wordrank = ((float)Integer.valueOf(split[0]))/maximums.get(split[1].length());
						float score = split[1].length()*wordrank;
						d.add(new Field("word", split[1], Field.Store.YES,
								Field.Index.ANALYZED));
						d.add(new Field("frequency", split[0], Field.Store.YES,
								Field.Index.NO));
						d.add(new Field("wordrank", ""+wordrank, Field.Store.YES,
								Field.Index.NO));
						d.add(new Field("score", ""+score, Field.Store.YES,
								Field.Index.NO));
						d.add(new Field("pos", split[2], Field.Store.YES,
								Field.Index.ANALYZED));
						d.add(new Field("texts", split[3], Field.Store.YES,
								Field.Index.NO));

						try {
							deleteDocument(d);
							writerAddDocument(d);
							counter++;
							if ((counter % 1000) == 0) {
								System.out.println(counter + "...");
								writerCommit();
							}
							if ((ignored % 1000) == 0)
								System.out
										.println("ignored " + ignored + "...");
						} catch (Exception e) {
							e.printStackTrace();
						}*/
					} else
						ignored++;
				}
			}
			try {
				writerCommit();
				writerOptimize();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("processed " + counter + " lines.");
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (input != null) {
					// flush and close both "input" and its underlying
					// FileReader
					input.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
	}
	
	/** index the dictionary files */
	public static void index() {
		
		init();
		
		eraseIndex();
		
		maxE = maximums(DICTIONARY, false);
		maxZ = maximums(ZODIAC_DICTIONARY, true);
		
		processFile(DICTIONARY, false);
		LuceneService.init(ZODIAC_INDEX);
		
		processFile(ZODIAC_DICTIONARY, true);
		
	}

	/** remove the given document by word */
	public static void deleteDocument(Document d) {
		if (writer == null || d == null)
			return;
		Fieldable f = d.getFieldable("word");
		if (f == null)
			return;
		String word = f.stringValue();
		if (word == null || "".equals(word))
			return;

		// logger.info("Trying to delete document with id [" + id + "]");
		QueryParser parser = new QueryParser(Version.LUCENE_30, "ID",
				new StandardAnalyzer(Version.LUCENE_30, new HashSet<String>()));
		try {
			String str = "+word:" + word;
			Query query = parser.parse(str);
			writer.deleteDocuments(query);
		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public static void dumpAll() {
		try {
			IndexReader r = IndexReader.open(FSDirectory.open(new File(DictionaryIndexer.ZODIAC_INDEX)));
			int num = r.numDocs();
			System.out.println("num " + num);
			int maxpat = 0;
			for ( int i = 0; i < num; i++)
			{
					Document d = r.document( i);
					String pat = LuceneService.value(d, "patterns");
					if (pat != null) {
						String[] pats = pat.split(" ");
						if (pats.length > maxpat) {
							maxpat = pats.length;
							System.out.println("max pat " + maxpat + ", " + pat + ": " + d);
						}
					}
			}
			
			r.close();
			
			LuceneService.init(ZODIAC_INDEX);
			Results re = LuceneService.query("+word:this");
			if (re != null && re.docs != null) {
				for (Document d : re.docs) System.out.println("found " + d);
			}
			re = LuceneService.query("+word:kill");
			if (re != null && re.docs != null) {
				for (Document d : re.docs) System.out.println("found " + d);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean isAlpha(String s) {
		return s.matches("[a-z]*");
	}
	
	public static void testSort() {
		try {
			LuceneService.init(ZODIAC_INDEX);
			Sort s = new Sort(new SortField("score", SortField.FLOAT, true));
			Results re = LuceneService.query("+patterns:XaaXa +patterns:aXXaa", s, 10);
			for (Document d : re.docs) {
				System.out.println("1st sort " + LuceneService.value(d, "word") + ", " + LuceneService.value(d, "score") + ", " + LuceneService.value(d, "scorez"));
			}
			
			s = new Sort(new SortField("scorez", SortField.FLOAT, true));
			re = LuceneService.query("+patterns:XaaXa", s, 10);
			for (Document d : re.docs) {
				System.out.println("2nd sort " + LuceneService.value(d, "word") + ", " + LuceneService.value(d, "score") + ", " + LuceneService.value(d, "scorez"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	public static void main(String[] args) {
		System.out.println(patterns("SNOTTHATDIGNITYOFPOSITIONWHICHITS", false));
		System.out.println(patterns("^UIk7qTtNQYD5)S(/9#BPORAU%fRlqEk^", false));
		System.out.println(patterns("SNOTTHATDIGNITYOFPOSITIONWHICHITS", true));
		System.out.println(patterns("^UIk7qTtNQYD5)S(/9#BPORAU%fRlqEk^", true));
		
		//index();
		//dumpAll();
		//testSort();
	}

}
