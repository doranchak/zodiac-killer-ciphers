package com.zodiackillerciphers.lucene;

import java.util.List;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.TopDocs;

public class Results {
	public List<Document> docs;
	public TopDocs topDocs;
	public Map<Document, Float> scores;
}
