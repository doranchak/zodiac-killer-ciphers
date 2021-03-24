package com.zodiackillerciphers.ciphers.algorithms;

import com.zodiackillerciphers.ciphers.Ciphers;

public class RailFence {
	public static String encode(String plaintext, int L, boolean startAtBottom) {
		if (L==1) return plaintext;
		String[] rails = new String[L];
		for (int i=0; i<L; i++) rails[i] = ""; 
			
		int which = 0;  int inc = 1;
		if (startAtBottom) {
			which = L-1;
			inc = -1;
		}
		for (int i=0; i<plaintext.length(); i++) {
			rails[which] += plaintext.charAt(i);
			which += inc;
			if (which == L) {
				inc = -1;
				which--; which--;
			} else if (which < 0) {
				inc = 1;
				which++; which++;
			}
		}

		//for (String rail: rails) System.out.println("ct " + rail);
		
		String result = "";
		for (int i=0; i<L; i++) result += rails[i];
		//System.out.println(L+": "+result);
		return result;
	}
	
	/** doesn't yet work for bottom-up rail fence encodings */
	public static String decode(String ciphertext, int L) {
		if (L==1) return ciphertext;
		StringBuffer[] plaintext = new StringBuffer[L];
		for (int i=0; i<L; i++) {
			plaintext[i] = new StringBuffer();
			for (int j=0; j<ciphertext.length(); j++) plaintext[i].append(" ");
		}
		
		int index=0; int step=2*(L-1); int rail = 0; int gap=0;  boolean doGap = false;
		for (int i=0; i<ciphertext.length(); i++) {
			char ch = ciphertext.charAt(i);
			plaintext[rail].setCharAt(index, ch);
			//System.out.println(rail+","+plaintext[rail]);
			index += (doGap? gap : step);
			if (rail > 0 && rail < L-1) doGap = !doGap;
			if (index >= ciphertext.length()) {
				doGap = false;
				rail++;
				index=rail;
				step -= 2;
				if (step == 0) step = 2*(L-1);
				gap += 2;
				if (rail==L-1) gap = 0;
			}
		}
		
		//for (StringBuffer sb : plaintext) System.out.println("pt " + sb);
		
		StringBuffer result = new StringBuffer();
		rail = 0;  int inc = 1;
		for (int i=0; i<ciphertext.length(); i++) {
			char ch = plaintext[rail].charAt(i);
			result.append(ch);
			rail += inc;
			if (rail==L) {
				rail-=2; inc=-1;
			} else if (rail<0) {
				rail+=2; inc=1;
			}
		}
		return result.toString();
				
	}
	
	public static void test() {
		String pt = "WEAREDISCOVEREDFLEEATONCE";
//		String ct = "ntrrhenhoiidrfnrrteiefhytfuteynohudt"; // werendereoefiohfsiuunrnvtedteyrtzh";
//		String ct = "werendereoefiohfsiuunrnvtedteyrtzh";
		System.out.println(encode(pt, 3, false));
		System.out.println(encode(pt, 3, true));
		System.out.println(decode("WECRLTEERDSOEEFEAOCAIVDEN", 3));
//		for (int L=1; L<100; L++)
//			System.out.println(L+": " + decode(encode(pt, L),L));
//		for (int L=1; L<100; L++)
//		System.out.println(L+": " + decode(ct, L));
		pt = Ciphers.Z408_SOLUTION.substring(0,340);
		System.out.println(encode(pt, 30, false));
		System.out.println(encode(pt, 30, true));
	}
	
	public static void main(String[] args) {
		test();
	}
}
