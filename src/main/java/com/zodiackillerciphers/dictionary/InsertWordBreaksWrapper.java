package com.zodiackillerciphers.dictionary;

public class InsertWordBreaksWrapper {
	// load text from the given file.
	// for each line, try to guess word breaks
	public static void main(String[] args) {
		InsertWordBreaks.findWordBreaks(args[0]);
	}
}
