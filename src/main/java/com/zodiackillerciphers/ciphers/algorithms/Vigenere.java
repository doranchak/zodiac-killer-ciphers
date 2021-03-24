package com.zodiackillerciphers.ciphers.algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.zodiackillerciphers.dictionary.WordFrequencies;
import com.zodiackillerciphers.io.FileUtil;
import com.zodiackillerciphers.lucene.ZKDecrypto;
import com.zodiackillerciphers.ngrams.Periods;

/** encrypt/decrypt adapted from http://practicalcryptography.com/ciphers/vigenere-gronsfeld-and-autokey-cipher/ */
public class Vigenere {
	public static String alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static String encrypt(String plaintext, String key) {
		return encrypt(plaintext, key, 0);
	}
	public static String encrypt(String plaintext, String key, int offset) { // offset is for the key
	    //plaintext = plaintext.toLowerCase().replaceAll(" ", "");
	    String ciphertext="";
	    
	    int j=0;
	    for(int i=0; i<plaintext.length(); i++){
//	    		System.out.print(i+" ");
	    		char p = plaintext.charAt(i);
//	    		System.out.print(p+" ");
			if (p == ' ')
				ciphertext += ' ';
	    		else {
	    			ciphertext += shiftChar(p, key.charAt((j+offset)%key.length()), false); 
	    			//ciphertext += (char) ((((plaintext.charAt(i)-97) + (key.charAt((j+offset)%key.length())-97)+26)%26)+97);
	    			j++;
//		    		System.out.print(ciphertext+" "+j);
	    		}
//			System.out.println();
	    } 
	    return ciphertext; 
	}
	public static String encrypt(String plaintext, StringBuffer key, int offset) { // offset is for the key
	    //plaintext = plaintext.toLowerCase().replaceAll(" ", "");
	    String ciphertext="";
	    int j=0;
	    for(int i=0; i<plaintext.length(); i++){
	    		char p = plaintext.charAt(i);
			if (p == ' ')
				ciphertext += ' ';
	    		else {
//	    			ciphertext += (char) ((((plaintext.charAt(i)-97) + (key.charAt((j+offset)%key.length())-97)+26)%26)+97);
	    			ciphertext += shiftChar(p, key.charAt((j+offset)%key.length()), false); 
	    			j++;
	    		}
	    } 
	    return ciphertext; 
	}
	
	/** shift a given lowercase letter by the given amount.  returns space as is. */
	public static char shift(char ch, int val) {
		if (ch == ' ') return ' ';
		int ascii = ch - 97;
		ascii += 26 + val; // in case val is negative
		ascii %= 26; // in case val >= 26
		
		ascii += 97; // put back among the lowercase letters
		return (char) ascii;
	}
	/** shift a given letter (upper or lower) using the given letter (upper or lower).  returns space as is. if decrypt is true, shift left, otherwise shift right.*/
	public static char shiftChar(char c, char key, boolean decrypt) {
		if (c == ' ') return ' ';
		if (key == ' ') throw new RuntimeException("Key cannot be space.");
		
		int base1;
		if (c > 64 && c < 91) base1 = 65;
		else if (c > 96 && c < 123) base1 = 97;
		else throw new RuntimeException("Illegal letter: " + c);
		
		int base2;
		if (key > 64 && key < 91) base2 = 65;
		else if (key > 96 && key < 123) base2 = 97;
		else throw new RuntimeException("Illegal key letter: " + key);
		
		//int result = (((ch - base1) + 26 + (key - base2)) % 26) + base1;
//		System.out.println("  - ch " + ch + " base1 " + base1 + " key " + key + " base2 " + base2 + " result " + ((ch - base1 + key - base2) % 26 + base1));
		int result;
		if (decrypt) 
			result = (c - base1 - key + base2 + 26) % 26 + base1;
		else
			result = (c - base1 + key - base2) % 26 + base1;
		return (char) result;
	}
	
	/** encrypt by shifting using a numeric key */
	public static String encrypt(String plaintext, int[] key) {
		plaintext = plaintext.toLowerCase();
		StringBuffer cipher = new StringBuffer();
		int j=0;
		for (int i=0; i<plaintext.length(); i++) {
			char p = plaintext.charAt(i);
			if (p == ' ')
				cipher.append(' ');
    			else {
    				cipher.append(shift(plaintext.charAt(i), key[j%key.length]));
    				j++;
    			}
		}
		return cipher.toString();
	}
	/** decrypt by shifting using a numeric key */
	public static String decrypt(String ciphertext, int[] key) {
		ciphertext = ciphertext.toLowerCase();
		StringBuffer plain = new StringBuffer();
		int j=0;
		for (int i=0; i<ciphertext.length(); i++) {
			char c = ciphertext.charAt(i);
			if (c == ' ')
				plain.append(" ");
			else { 
				plain.append(shift(ciphertext.charAt(i), 0-key[j%key.length]));
				j++;
			}
		}
		return plain.toString();
	}
	
	public static String decrypt(String plaintext, String key) {
		return decrypt(plaintext, key, 0);
	}
	public static String decrypt(String ciphertext, String key, int offset) { // offset is for the key 
	    //ciphertext = ciphertext.toLowerCase();  
	    // do some error checking 
	    String plaintext="";
	    int j=0;
	    for(int i=0; i<ciphertext.length(); i++){ 
			char c = ciphertext.charAt(i);
			if (c == ' ')
				plaintext += " ";
			else {
//				plaintext += (char) ((((ciphertext.charAt(i) - 97) - (key.charAt((j + offset) % key.length()) - 97)
//						+ 26) % 26) + 97);
				plaintext += shiftChar(c, key.charAt((j+offset)%key.length()), true); 
				j++;
			}
	    } 
	    return plaintext; 
	}
	public static String decrypt(String ciphertext, StringBuffer key, int offset) { // offset is for the key 
	    //ciphertext = ciphertext.toLowerCase();  
	    // do some error checking 
	    String plaintext="";
	    int j=0;
	    for(int i=0; i<ciphertext.length(); i++){ 
			char c = ciphertext.charAt(i);
			if (c == ' ')
				plaintext += " ";
			else { 
//				plaintext += (char) ((((ciphertext.charAt(i) - 97) - (key.charAt((j + offset) % key.length()) - 97)
//						+ 26) % 26) + 97);
				plaintext += shiftChar(c, key.charAt((j+offset)%key.length()), true); 
				j++;
			}
	    } 
	    return plaintext; 
	}
	
	/** brute force all dictionary words as keys.  score solutions based on zkscore. */
	public static void bruteForce(String cipher) {
		cipher = cipher.toLowerCase();
		WordFrequencies.init();
		for (String word : WordFrequencies.map.keySet()) {
			String plain = decrypt(cipher, word.toLowerCase());
			float score = ZKDecrypto.calcscore(plain.toUpperCase());
			//System.out.println(score + "	" + word + "	" + plain);
			String result = WordFrequencies.bestWords(plain.toUpperCase());
			System.out.println(result + " (" + word + ")");
		}
	}
	
	public static void testShannon() {
		String plain = "CREAS";
		for (int i=0; i<26; i++) {
			char ch = (char) (i + 97);
			System.out.println(ch + ": " + encrypt(plain, ""+ch));
		}
	}

	public static void test() {
		System.out.println(encrypt("aaaaaaaaaaa", "abc", 2));
		System.out.println(decrypt("aaaaaaaaaaa", "abc", 0));
//		System.out.println(encrypt("goldenbrown", "ddaic").toUpperCase());
//		System.out.println(decrypt("ZBNATXYNIZSJUPNLNQPFKHCWD", "gujlz").toUpperCase());
//		System.out.println(decrypt("XPTHACLINK", "xdews").toUpperCase());
//		System.out.println(decrypt("JRLLGQERWYQ", "xxasy").toUpperCase());
//		System.out.println(keyFor("XPTHACLINK","profoundly"));
//		System.out.println(keyFor("JRLLGQERWYQ","multithreat"));
//		System.out.println(decrypt("iswxvibjexiggbocewkbjeviggqs", "fortification"));
//		System.out.println(decrypt("osvvzzytzckjlqz", "love"));
//		bruteForce("osvvzzytzckjlqz");
//		testShannon();
//		ambiguousCiphersBRUTE(2, 6);
//		ambiguousCiphers();
//		System.out.println(Arrays.toString(letterDifferences("THISISATEST", 2)));
//		System.out.println(encrypt("afsp", "zz"));
//		WordFrequencies.init();
//		System.out.println(WordFrequencies.percentile("GOLDENBROWN"));
//		System.out.println(WordFrequencies.percentile("MULTITHREAT"));
//		System.out.println(WordFrequencies.percentile("FFFFFFFFFFFF"));
//		System.out.println(WordFrequencies.percentile("GREATGREATGREAT"));
//		makeVigenereMutual(12, 5);
//		process("/Users/doranchak/projects/zodiac/mutual16");
//		randomCipher("CLYBURNREISERFS", "CAVIARCOLOSTOMY", 5);
//		System.out.println(encrypt("DAVE", new int[] {25}));
	}
	
	static void testShiftChar() {
		String[] alpha = new String[] {
				"ABCDEFGHIJKLMNOPQRSTUVWXYZ",
				"abcdefghijklmnopqrstuvwxyz"
		};
		for (String a1 : alpha) {
			for (String a2 : alpha) {
				for (int i=0; i<a1.length(); i++) {
					char c = a1.charAt(i);
					for (int j=0; j<a2.length(); j++) {
						char k = a2.charAt(j);
						System.out.println("shift " + c + "" + k + " = " + shiftChar(c, k, false) + ", " + shiftChar(c, k, true));
					}
				}
			}
		}
	}
	
	public static void main(String[] args) {
//		while(true)
//			makeVigenereMutual(Integer.valueOf(args[0]), 5);
//		test();
//		System.out.println(encrypt("puzz  lin gly","BCQXX"));
//		System.out.println(decrypt("qwpw  ijp wiv","BCQXX"));
//		System.out.println(encrypt("puzz  lin gly",new int[] {1,2,3}));
//		System.out.println(decrypt("qwca  nlo ioz",new int[] {1,2,3}));
//		System.out.println(encrypt("GIBLAHJY RN ZJX POLPGYMFF".toLowerCase(), "HOWEVERTHEYWILLPROBABLY", 7));
//		System.out.println(decrypt("GIBLAHJY RN ZJX POLPGYMFF".toLowerCase(), "HOWEVERTHEYWILLPROBABLY", 7));
//		System.out.println(encrypt("GIBLAHJY RN ZJX POLPGYMFF", "HOWEVERTHEYWILLPROBABLY", 7));
//		System.out.println(decrypt("GIBLAHJY RN ZJX POLPGYMFF", "HOWEVERTHEYWILLPROBABLY", 7));
//
//		System.out.println(decrypt("zpfjwpuj ge nkx qzjwuuqaj", "HOWEVERTHEYWILLPROBABLY", 7));
//		System.out.println(encrypt("THIS IS A TEST OF THE VIGENERE ENCODER", "ZODIAC", 0));
//		System.out.println(decrypt("SVLA IU Z HHAT QE HKM VKFSQMRG DBFWDGQ", "ZODIAC", 0));
		
		//		System.out.println(decrypt("jjhbwjssdnjbobh",new int[] {6, 1, -8, 1, -17}));
//		test();
//		bruteForce("WEOZ");
//		testShiftChar();
	}

}
