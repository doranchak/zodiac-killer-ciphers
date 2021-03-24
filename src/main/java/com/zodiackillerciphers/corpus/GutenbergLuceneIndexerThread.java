package com.zodiackillerciphers.corpus;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import com.zodiackillerciphers.constraints.Info;
import com.zodiackillerciphers.lucene.DictionaryIndexer;

/**
 * Crawler. Gets data from a summary view or table, and adds or updates it in
 * the Lucene index.
 */
public class GutenbergLuceneIndexerThread extends Thread {

	int L;
	StringBuffer sb;
	public GutenbergLuceneIndexerThread(int L, StringBuffer sb) { this.L = L; this.sb = sb; }
	
	public void run() {
		System.out.println("starting length " + L + "...");
		Document d = new Document();
		Field fieldWord = new Field("word", "tmp", Field.Store.YES, Field.Index.ANALYZED);
		Field fieldFrequency = new Field("frequency", "1", Field.Store.YES, Field.Index.NO);
		Field fieldPatterns = new Field("patterns", "tmp", Field.Store.YES, Field.Index.ANALYZED);
		d.add(fieldWord);
		d.add(fieldFrequency);
		d.add(fieldPatterns);
		
		for (int i = 0; i < sb.length() - L + 1; i++) {
			if (i >= 0 && (i % 25000) == 0) System.out.println(" - length " + L + " pos " + i + " of " + sb.length() + " (" + (int)(100*((float)i)/sb.length()) + "%)"); 
			String sub = sb.substring(i, i + L);
			if (!Info.enoughUniques(sub))
				continue;
			
			fieldWord.setValue(sub.toLowerCase());
			fieldPatterns.setValue(DictionaryIndexer.patterns(sub, true));

			try {
				//GutenbergLuceneIndexer.deleteDocument(d);
				//GutenbergLuceneIndexer.writerAddDocument(d);
				GutenbergLuceneIndexer.updateDocument(d);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("done with length " + L);
	}

}
