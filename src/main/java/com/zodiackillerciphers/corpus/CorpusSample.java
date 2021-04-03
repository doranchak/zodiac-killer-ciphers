package com.zodiackillerciphers.corpus;

import java.util.List;

public class CorpusSample {
	/** list of delimited words */
	List<String> words;
	/** words with spaces removed */
	String noSpaces;
	/** source file */
	String source;
	public List<String> getWords() {
		return words;
	}
	public void setWords(List<String> words) {
		this.words = words;
	}
	public String getNoSpaces() {
		return noSpaces;
	}
	public void setNoSpaces(String noSpaces) {
		this.noSpaces = noSpaces;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	@Override
	public String toString() {
		return "CorpusSample [words=" + words + ", noSpaces=" + noSpaces + ", source=" + source + ", getLength()="
				+ getLength() + "]";
	}
	public int getLength() {
		return noSpaces.length();
	}
}
