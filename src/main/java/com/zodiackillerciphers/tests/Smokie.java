package com.zodiackillerciphers.tests;


import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.homophones.HomophonesNew;
import com.zodiackillerciphers.homophones.HomophonesResultBean;

/** http://zodiackillersite.com/viewtopic.php?f=81&t=267&p=35290#p35290 */
public class Smokie {
	public static void test() {
		String alphabet = "ABCD";
		int[] frequencies = new int[] {4,4,2,2};
		
		String cipher = "";
		
		produce(cipher, alphabet, frequencies);
		
	}
	
	static void produce(String cipher, String alphabet, int[] frequencies) {
		for (int i=0; i<frequencies.length; i++) {
			int f = frequencies[i];
			if (f > 0) {
				frequencies[i]--;
				produce(cipher+alphabet.charAt(i), alphabet, frequencies);
				frequencies[i]++;
			}
		}
		if (cipher.length() == 12) {
			//System.out.println(cipher);
			String num = Ciphers.numericAsString(cipher, false);
			//System.out.println(num);
			//HomophonesResultBean bean = HomophonesNew.bestCycleIn(num.replaceAll(" ",""), 2);
			//if (bean.getRun() == 4) System.out.println(bean);
		}
	}
	
	public static void main(String[] args) {
		test();
	}
}
