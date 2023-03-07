package com.zodiackillerciphers.rest.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.rest.beans.Cipher;



/** services related to retrieving ciphers */
@RestController
@CrossOrigin
public class CipherService {
	/** return cipher with the given name */
	@GetMapping("/cipher")
	public Cipher cipher(@RequestParam(value = "name", defaultValue = "z340") String cipherName) {
		if (cipherName.equals("z340"))
			return new Cipher(Ciphers.Z340, "z340", "Zodiac's 340-character cipher", 17);
		else if (cipherName.equals("z340t"))
			return new Cipher(Ciphers.Z340T, "z340", "Zodiac's 340-character cipher", 17);
		else if (cipherName.equals("z408"))
			return new Cipher(Ciphers.Z408, "z408", "Zodiac's 408-character cipher", 17);
		return null;
	};

	//- convert z340-like transposition back into normal order
	
	//- get a random sample of the given length from corpora (whitespace does not contribute to length)
	
	//- make a homophonic cipher from the given plaintext (no transposition) 

	
	public static Map<Character, Character> simpleZodiacKey;
	static {
		simpleZodiacKey = new HashMap<Character, Character>();
		simpleZodiacKey.put('A','8');
		simpleZodiacKey.put('B','V');
		simpleZodiacKey.put('C','e');
		simpleZodiacKey.put('D','z');
		simpleZodiacKey.put('E','6');
		simpleZodiacKey.put('F','Q');
		simpleZodiacKey.put('G','R');
		simpleZodiacKey.put('H',')');
		simpleZodiacKey.put('I','9');
		simpleZodiacKey.put('J','2'); // not in 408 key
		simpleZodiacKey.put('K','/');
		simpleZodiacKey.put('L','#');
		simpleZodiacKey.put('M','q');
		simpleZodiacKey.put('N','^');
		simpleZodiacKey.put('O','d');
		simpleZodiacKey.put('P','=');
		simpleZodiacKey.put('Q','b'); // not in 408 key
		simpleZodiacKey.put('R','t');
		simpleZodiacKey.put('S','@');
		simpleZodiacKey.put('T','5');
		simpleZodiacKey.put('U','Y');
		simpleZodiacKey.put('V','c');
		simpleZodiacKey.put('W','A');
		simpleZodiacKey.put('X','j');
		simpleZodiacKey.put('Y','_');
		simpleZodiacKey.put('Z','>'); // not in 408 key
	}	
	/**
	 * make a simple substitution cipher using the 408's key. non-homophonic, so
	 * limit the cipher alphabet to 26 symbols. but use "interesting" symbols so the
	 * resulting cipher is more zodiac-like. (Made for CCI Cyber Camp challenges)
	 */
	@GetMapping("/simpleZodiacCipher")
	public String simpleZodiacCipher(@RequestParam(value = "plaintext") String plaintext) {
		plaintext = plaintext.toUpperCase();
		StringBuffer cipher = new StringBuffer();
		for (int i=0; i<plaintext.length(); i++) {
			char pt = plaintext.charAt(i);
			Character val = simpleZodiacKey.get(pt);
			if (val == null) val = ' ';
			cipher.append(val);
		}
		return cipher.toString(); 
	}
	
	/** 
	 * make a simple reversal cipher where each word is reversed 
	 */
	@GetMapping("/reversedWordsCipher")
	public String reversedWordsCipher(@RequestParam(value = "plaintext") String plaintext) {
		plaintext = plaintext.toUpperCase();
		String[] split = plaintext.split(" ");
		StringBuffer cipher = new StringBuffer();
		for (String s : split) {
			if (cipher.length() > 0) cipher.append(" ");
			cipher.append(new StringBuffer(s).reverse());
		}
		return cipher.toString();
	}
	
	/** 
	 * make a simple book cipher.  plaintext letters correspond to the first letter in the Nth word.
	 * book is given as a space-delimited sequence of words.
	 *   
	 */
	@GetMapping("/bookCipher")
	public int[] bookCipher(@RequestParam(value = "plaintext") String plaintext, @RequestParam(value = "book") String book) {
		plaintext = plaintext.toUpperCase();
		book = book.toUpperCase();
		String[] split = book.split(" ");
		
		Map<Character, List<Integer>> map = new HashMap<Character, List<Integer>>(); // map plaintext letters to lists of words
		for (int i=0; i<split.length; i++) {
			String str = split[i];
			int wordNumber = i+1;
			char first = str.charAt(0);
			List<Integer> val = map.get(first);
			if  (val == null) {
				val = new ArrayList<Integer>();
				map.put(first, val);
			}
			val.add(wordNumber);
		}

		// pointers to which words are selected for each plaintext letter.  move through them sequentially to minimize repeated numbers.
		Map<Character, Integer> indices = new HashMap<Character, Integer>();
		for (Character key : map.keySet())
			indices.put(key, 0);
		
		int[] cipher = new int[plaintext.length()];
		for (int i=0; i<plaintext.length(); i++) {
			char p = plaintext.charAt(i);
			List<Integer> words = map.get(p);
			if (words == null) {
				cipher[i] = -1;
			} else {
				int ind = indices.get(p);
				cipher[i] = words.get(ind);
				indices.put(p, (ind+1)%words.size());
			}
		}
		return cipher;
		
	}
	
	// TODO: RS232 cipher (see Post Secret cipher)

}
