package com.zodiackillerciphers.ciphers.algorithms;

/** encrypt/decrypt adapted from http://practicalcryptography.com/ciphers/vigenere-gronsfeld-and-autokey-cipher/ */
public class Vigenere {
	public static String encrypt(String plaintext, String key) {
	    plaintext = plaintext.toLowerCase().replaceAll(" ", "");  
	    String ciphertext="";
	    for(int i=0; i<plaintext.length(); i++){ 
	        ciphertext += (char) ((((plaintext.charAt(i)-97) + (key.charAt(i%key.length())-97)+26)%26)+97); 
	    } 
	    return ciphertext; 
	}
	
	public static String decrypt(String ciphertext, String key) { 
	    ciphertext = ciphertext.toLowerCase();  
	    // do some error checking 
	    String plaintext="";
	    for(int i=0; i<ciphertext.length(); i++){ 
	        plaintext += (char) ((((ciphertext.charAt(i)-97) - (key.charAt(i%key.length())-97)+26)%26)+97); 
	    } 
	    return plaintext; 
	}
	
	public static void test() {
		System.out.println(encrypt("defend the east wall of the castle", "fortification"));
		System.out.println(decrypt("iswxvibjexiggbocewkbjeviggqs", "fortification"));
	}
	
	public static void main(String[] args) {
		test();
	}

}
