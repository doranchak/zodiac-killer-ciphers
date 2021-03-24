package com.zodiackillerciphers.io;

import java.io.BufferedReader;

public class MergeSortBean {
	/** a line from a file */
	public String line;
	/** reference to the reader that is reading the file it came from */
	public BufferedReader reader;
	/** the array index */
	public int index;
	public MergeSortBean(String line, BufferedReader reader, int index) {
		super();
		this.line = line;
		this.reader = reader;
		this.index = index;
	}
	
}
