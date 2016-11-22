package com.zodiackillerciphers.ciphers;

public class Cipher {
	public String cipher;
	public String solution;
	public String description;
	public int width;
	public int height;
	public String url;
	public boolean forComparison;
	
	public Cipher(String description, String cipher, String solution) {
		this(description, cipher, solution, null);
	}
	public Cipher(String description, String cipher, String solution, boolean forComparison) {
		this(description, cipher, solution, -1, -1, null, forComparison);
	}
	public Cipher(String description, String cipher, String solution, String url) {
		this(description, cipher, solution, -1, -1, url, false);
	}
	public Cipher(String description, String cipher, String solution, String url, boolean forComparison) {
		this(description, cipher, solution, -1, -1, url, forComparison);
	}
	public Cipher(String description, String cipher, String solution, int width, int height) {
		this(description, cipher, solution, width, height, null, false);
	}
	public Cipher(String description, String cipher, String solution, int width, int height, String url, boolean forComparison) {
		super();
		this.cipher = cipher;
		this.description = description;
		this.solution = solution;
		this.width = width;
		this.height = height;
		this.url = url;
		this.forComparison = forComparison;
		check();
	}
	public void check() {
		if (this.cipher.contains("?")) {
			//System.err.println("WARNING: This cipher uses special character '?' that will break the older-style ngram counts. " + this.toString());
		}
	}
	public String toString() {
		return "[" + description + "] [" + cipher + "] [" + solution + "]";
	}

}
