package com.zodiackillerciphers.corpus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;

import com.larvalabs.megamap.MegaMap;
import com.larvalabs.megamap.MegaMapException;
import com.larvalabs.megamap.MegaMapManager;
import com.zodiackillerciphers.constraints.Info;
import com.zodiackillerciphers.io.FileUtil;
import com.zodiackillerciphers.lucene.DictionaryIndexer;
import com.zodiackillerciphers.lucene.LuceneService;
import com.zodiackillerciphers.lucene.Results;


/**
 * Crawler. Gets data from a summary view or table, and adds or updates it in
 * the Lucene index.
 */
public class GutenbergLuceneIndexer {

	//public static String ZODIAC_INDEX = "/Users/doranchak/projects/work/java/zodiac/letters/index";
	//public static String DICTIONARY = "/Users/doranchak/projects/work/java/zodiac/letters/500k.txt";
	//public static String ZODIAC_DICTIONARY = "/Users/doranchak/projects/work/java/zodiac/letters/dict-zodiac-minus-408.txt";

	//static Map<Integer, Integer> maxE;
	//static Map<Integer, Integer> maxZ;
		
	/**
	 * Writer singleton. Only one index writer can write to an index at a time,
	 * but many threads can access the same writer to update the index.
	 */
	private static IndexWriter writer;
	
	public static void init(String indexDir) {

		try {
			Analyzer analyzer = LuceneService.analyzer();
			Directory dir = FSDirectory.open(new File(indexDir));
			IndexWriterConfig conf = new IndexWriterConfig(LuceneService.VERSION,
					analyzer);
			writer = new IndexWriter(dir, conf);
			conf.setRAMBufferSizeMB(1024);
			//writer.setUseCompoundFile(false);
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

	/*
	static void checkWriter() {
		if (writer == null)
			init();
	}*/

	/** commit pending changes submitted to the index writer */
	public static void writerCommit() throws Exception {
		//checkWriter();
		writer.commit();
	}

	public static void writerOptimize() throws Exception {
		//checkWriter();
		writer.optimize();
	}

	public static void writerAddDocument(Document d) throws Exception {
		//checkWriter();
		writer.addDocument(d);
	}

	public static void eraseIndex() {
		System.out.println("Erasing index...");
		try {
			writer.deleteAll();
			writer.commit();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static void processFile(StringBuffer sb, int minLength, int maxLength) {
		LuceneService.DEBUG = false;
		String freq;
		List<Thread> threads = new ArrayList<Thread>();
		for (int L=minLength; L<=maxLength; L++) {
			//System.out.println("length " + L + "...");
			
			threads.add(new GutenbergLuceneIndexerThread(L, sb));
		}

		long timeStart = new Date().getTime();
		for (Thread thread : threads) thread.start();
		for (Thread thread : threads) 
			try {
				thread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		long timeEnd = new Date().getTime();
		System.out.println("Elapsed: " + (timeEnd - timeStart));
		
	}

	static void mapFile(MegaMap map, StringBuffer sb, int minLength, int maxLength) {
		try {
			for (int L=minLength; L<=maxLength; L++) {
				System.out.println("L = " + L);
				for (int i = 0; i < sb.length() - L + 1; i++) {
					String sub = sb.substring(i, i + L);
					if (!Info.enoughUniques(sub))
						continue;
					Integer val = (Integer) map.get(sub);
					if (val == null) val = 0;
					val++;
					map.put(sub, val);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	static void testUpdate() {
		LuceneService.DEBUG = true;
		
		
		for (int i=0; i<5; i++) {
			Document d = new Document();
			d.add(new Field("word", "z", Field.Store.YES,
					Field.Index.ANALYZED));
			d.add(new Field("frequency", ""+i, Field.Store.YES,
					Field.Index.NO));
			d.add(new Field("patterns", "1,2", Field.Store.YES, Field.Index.ANALYZED));

			try {
				deleteDocument(d);
				writerAddDocument(d);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		

		
		
	}
	
	
	/** index the gutenberg corpus files */
	public static void index(String indexDir, String dirTmp, String dirCorpus, int minLength, int maxLength) {
		
		LuceneService.init(indexDir);
		init(indexDir);
		eraseIndex();
		init(indexDir);
		if (1==2) {
			testUpdate(); 
			try {
				writerOptimize();
				writer.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}

		MegaMapManager manager = null;
		MegaMap map = null;
		try {
			manager = MegaMapManager.getMegaMapManager();
			map = manager.createMegaMap("map1", false, false);
		} catch (MegaMapException e1) {
			e1.printStackTrace();
		}
		
		
		Search.makeUnzipDir(dirTmp+"/unzipped");
		List<File> files = Reader.list(dirCorpus);
		System.out.println("Number of files to process: " + files.size());
		long length = 0;
		String text;
		Set<String> set = new HashSet<String>();
		for (int f=0; f<files.size(); f++) {
			float p = 100*((float)f)/files.size();
			System.out.println(f + " of " + files.size() + " (" + (int) p + "%)");
			File file = files.get(f);
			if (file.getName().toLowerCase().endsWith("_h.zip")) {
				continue; // ignoring HTML zips 
			}
			try {
				text = Search.read(file, dirTmp);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
			if (text == null) continue;
			length += text.length();
			// convert text to uppercase alphabet stream
			StringBuffer converted = FileUtil.convert(text);
			//mapFile(map, converted, minLength, maxLength);
			//processFile(converted, minLength, maxLength);
			testMemory(set, converted, 30);
			System.out.println("Set size: " + set.size());
		}

		//System.out.println("Done initializing map.  Indexing...");
		//indexMap(map);
		manager.shutdown();
		System.out.println("Total set size: " + set.size());
		System.out.println("Done processing files.  Total text length: " + length + ".  Optimizing...");
		try {
			writerOptimize();
			writer.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//processFile(DICTIONARY, false);
		//LuceneService.init();
		//processFile(ZODIAC_DICTIONARY, true);
		
	}
	
	static void testMemory(Set<String> set, StringBuffer sb, int L) {
		for (int i = 0; i < sb.length() - L + 1; i++) {
			//if (i >= 0 && (i % 25000) == 0) System.out.println(" - length " + L + " pos " + i + " of " + sb.length() + " (" + (int)(100*((float)i)/sb.length()) + "%)"); 
			String sub = sb.substring(i, i + L);
			if (!Info.enoughUniques(sub))
				continue;
			set.add(sub);
		}		
	}
	
	static void indexMap(MegaMap map) {
		try {
			Set set = map.getKeys();
			int total = set.size();
			int count = 0;
			for (Object k : set) {
				String key = (String) k;
				Integer val = (Integer) map.get(key);

				Document d = new Document();
				d.add(new Field("word", key.toLowerCase(), Field.Store.YES,
						Field.Index.ANALYZED));
				d.add(new Field("frequency", ""+val, Field.Store.YES, Field.Index.NO));
				d.add(new Field("patterns", DictionaryIndexer.patterns(key, true),
						Field.Store.YES, Field.Index.ANALYZED));
				GutenbergLuceneIndexer.writerAddDocument(d);
				count++;
				
				float p = 100*((float)count)/total;
				if (count % 1000 == 0) System.out.println(count + " of " + total + " (" + (int)p + "%)");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		QueryParser parser = LuceneService.parser(); 
		try {
			String str = "+word:\"" + word + "\"";
			//System.out.println("Deleting: [" + str + "]");
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
	
	/** remove the given document by word */
	public static void updateDocument(Document d) {
		if (writer == null || d == null)
			return;
		Fieldable f = d.getFieldable("word");
		if (f == null)
			return;
		String word = f.stringValue();
		if (word == null || "".equals(word))
			return;

		Term term = new Term("word",word);
		List<Document> docs = new ArrayList<Document>();
		docs.add(d);
		try {
			writer.updateDocuments(term, docs);
		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void testSort(String indexDir) {
		try {
			LuceneService.init(indexDir);
			Sort s = new Sort(new SortField("frequency", SortField.FLOAT, true));
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
		//System.out.println(DictionaryIndexer.patterns("_dYq_^SqWVZeGYKE_TY", true));
		index(args[0], args[1], args[2], Integer.valueOf(args[3]), Integer.valueOf(args[4]));
		//dumpAll();
		//testSort();
	}

}
