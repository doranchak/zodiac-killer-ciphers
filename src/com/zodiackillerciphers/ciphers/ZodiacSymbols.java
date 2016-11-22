package com.zodiackillerciphers.ciphers;

import java.util.HashMap;
import java.util.Map;

/** misc. tools related to the Zodiac's symbols */
public class ZodiacSymbols {

	/* symbols from all ciphers */
	public static String all = "!#%&()*+-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\^_bcdefjklpqrtyz|";
	
	/* for each symbol, associate a list of plaintext interpretations (backwards L ==> L, for example) */
	public static Map<Character, String> interpretationMap;
	static {
		interpretationMap = new HashMap<Character, String>();
		interpretationMap.put('!',"I");
		interpretationMap.put('&',"P");
		interpretationMap.put('(',"O");
		interpretationMap.put(')',"O");
		interpretationMap.put('+',"T");
		interpretationMap.put('/',"I");
		interpretationMap.put('0',"08");
		interpretationMap.put('1',"O");
		interpretationMap.put('2',"O");
		interpretationMap.put('3',"O");
		interpretationMap.put('4',"O");
		interpretationMap.put('5',"O");
		interpretationMap.put('6',"O");
		//interpretationMap.put('7',"A");
		//interpretationMap.put('8',"A");
		//interpretationMap.put('9',"A");
		interpretationMap.put(':',"I");
		interpretationMap.put(';',"I");
		interpretationMap.put('<',"V");
		interpretationMap.put('=',"K");
		interpretationMap.put('>',"V");
		interpretationMap.put('?',"U");
		interpretationMap.put('A',"A");
		interpretationMap.put('B',"B");
		interpretationMap.put('C',"C");
		interpretationMap.put('D',"D");
		interpretationMap.put('E',"E");
		interpretationMap.put('F',"F");
		interpretationMap.put('G',"G");
		interpretationMap.put('H',"H");
		interpretationMap.put('I',"I");
		interpretationMap.put('J',"J");
		interpretationMap.put('K',"K");
		interpretationMap.put('L',"L");
		interpretationMap.put('M',"M");
		interpretationMap.put('N',"N");
		interpretationMap.put('O',"O");
		interpretationMap.put('P',"P");
		interpretationMap.put('Q',"Q");
		interpretationMap.put('R',"R");
		interpretationMap.put('S',"S");
		interpretationMap.put('T',"T");
		interpretationMap.put('U',"U");
		interpretationMap.put('V',"V");
		interpretationMap.put('W',"W");
		interpretationMap.put('X',"X");
		interpretationMap.put('Y',"Y");
		interpretationMap.put('Z',"Z");
		interpretationMap.put('[',"T");
		interpretationMap.put('\\',"I");
		interpretationMap.put('^',"V");
		interpretationMap.put('b',"B");
		interpretationMap.put('c',"C");
		interpretationMap.put('d',"D");
		interpretationMap.put('e',"E");
		interpretationMap.put('f',"F");
		interpretationMap.put('j',"J");
		interpretationMap.put('k',"K");
		interpretationMap.put('l',"L");
		interpretationMap.put('p',"P");
		interpretationMap.put('q',"Q");
		interpretationMap.put('r',"R");
		interpretationMap.put('t',"T");
		interpretationMap.put('y',"Y");
		interpretationMap.put('z',"OT");
		interpretationMap.put('|',"I");	
	}
	
	/** return interpretations of the given string of zodiac symbols */
	public static String interpret(String str) {
		String result = "";
		for (char ch : str.toCharArray()) {
			result += interpretationMap.get(ch) == null ? "" : interpretationMap.get(ch);
		}
		return result;
	}
	
	
}
