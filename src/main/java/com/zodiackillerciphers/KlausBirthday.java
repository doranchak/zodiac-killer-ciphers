package com.zodiackillerciphers;

import java.util.HashMap;
import java.util.Map;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.transform.CipherTransformations;

public class KlausBirthday {
	
	/** the one i gave klaus:  qNq;9_qBAqkS;NWEBWAWppzUqX|A#Wzq|qBBV>E4A3Ek|V_qBABV>9Vk9WAWppzUqX|A#WzSq*9B>9|A9>VB||AXqNNqkS9Rp9Xq9k49q_qNNkV|Sq*9zVE>zkW>9U94WEB9zVE_qNN|Xz|VBNV_#V_kVXB|Vp>z_qBAqkSV3AWppzUqX|A#WzB */
	/** the one i gave bart: 
	 		dTdpqKd1Gd|3jJ
	 		L>JGJWWFjdL>G)
	 		JFd>d11O+M4G@M
	 		|>OKd1G1O+qO|q
	 		JGJWWFjdL>G)JF
	 		3dzq1+q>Gq+O1>
	 		>GLdTTd|3q7WqL
	 		dq|4qdKdTT|O>3
	 		dzqFOM+F|J+qjq
	 		4JM1qFOMKdTT>L
	 		F>O1TOK)OK|OL1
	 		>OW+FKd1Gd|3O@
	 		GJWWFjdL>G)JF1 
	 */

	public static void makeCipher() {
		boolean go = true;
		String cipher = "";
		while (go) {
			String plaintext = "ILIKEWISHINGBARTAHAPPYBIRTHDAYITISSOMUCHFUNTOWISHSOMEONEAHAPPYBIRTHDAYGIVESMETHEMOSTTHRILLINGEXPERIENCEIWILLNOTGIVEYOUMYNAMEBECAUSEYOUWILLTRYTOSLOWDOWNORSTOPMYWISHINGOFHAPPYBIRTHDAYS";
			Map<Character, Character> map = new HashMap<Character, Character>();
			String alphabet = Ciphers.alphabet(Ciphers.Z340);
			alphabet = CipherTransformations.shuffle(alphabet);
			int current = 0;
			cipher = "";
			for (int i=0; i<plaintext.length(); i++) {
				char p = plaintext.charAt(i);
				Character c = map.get(p);
				if (c == null) {
					c = alphabet.charAt(current++);
					map.put(p, c);
				}
				cipher += c;
			}
			if (cipher.contains("z") && !cipher.contains(".")) go = false;
		}
		System.out.println(cipher);
	}
	public static void main(String[] args) {
		makeCipher();
	}
}
