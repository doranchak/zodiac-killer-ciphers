package com.zodiackillerciphers.dictionary.service;

public interface WordLookup {
	/** look up single words that fit with the given ciphertext constraints.  return at most [limit] words. */
	public Results find(String ciphertext, int limit);
	/** look up single words that fit with the given ciphertext and plaintext constraints.  return at most [limit] words. */
	public Results find(String ciphertext, String plaintext, int limit);

}
