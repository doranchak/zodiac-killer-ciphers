package com.zodiackillerciphers.tests;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.transform.CipherTransformations;

/** scott monsma <samonsma@gmail.com> says: 
 * THe other thing I'm noticing, that I cant find any discussion of, is
periodicity in symbol repeats. If you look at the 340 as written,
there are several characters that repeat on a 35- position cycle.
However taking the FBI idea of the top and bottom being two lateral
halves, that alters the periodicity.
 */
public class Repeats {
	/** find repeats, separated by the given number of letters */
	public static void repeats(String ciphertext, int n) {
		//Map<String, Integer> map = new HashMap<String, Integer>();
		int total = 0;
		Set<Character> uniques = new HashSet<Character>();
		for (int i=0; (i+n+1)<ciphertext.length(); i++) {
			char ch1 = ciphertext.charAt(i);
			char ch2 = ciphertext.charAt(i+n+1);
			if (ch1 == ch2) {
				System.out.println(ch1 + ", " + i + ", " + (i+n+1));
				total++;
			}
		}
		System.out.println("Total for " + n + ": " + total);
	}
	public static void main(String[] args) {
		String cipher = Ciphers.cipher[1].cipher;
		//cipher = CipherTransformations.shuffle(cipher);
		for (int n=0; n<cipher.length(); n++) {
			repeats(cipher, n);
		}
		/*for (int n=0; n<408; n++) {
			repeats(Ciphers.cipher[1].cipher, n);
		}*/
	}
}
