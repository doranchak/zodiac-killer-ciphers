package com.zodiackillerciphers.morse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zodiackillerciphers.dictionary.WordFrequencies;
import com.zodiackillerciphers.io.FileUtil;
import com.zodiackillerciphers.io.Text;

/** exploring ray grant's ideas */
public class Morse {
	public static Map<Character, String> morseTable;
	static {
		morseTable = new HashMap<Character, String>();
        morseTable.put('A',"01");
        morseTable.put('B',"1000");
        morseTable.put('C',"1010");
        morseTable.put('D',"100");
        morseTable.put('E',"0");
        morseTable.put('F',"0010");
        morseTable.put('G',"110");
        morseTable.put('H',"0000");
        morseTable.put('I',"00");
        morseTable.put('J',"0111");
        morseTable.put('K',"101");
        morseTable.put('L',"0100");
        morseTable.put('M',"11");
        morseTable.put('N',"10");
        morseTable.put('O',"111");
        morseTable.put('P',"0110");
        morseTable.put('Q',"1101");
        morseTable.put('R',"010");
        morseTable.put('S',"000");
        morseTable.put('T',"1");
        morseTable.put('U',"001");
        morseTable.put('V',"0001");
        morseTable.put('W',"011");
        morseTable.put('X',"1001");
        morseTable.put('Y',"1011");
        morseTable.put('Z',"1100");
	}

	/* gareth penn's conversion of the 340 cipher to morse code (dots removed) */
	public static String PENN_MORSE_340 = "0010010000111001000101111000010010001010000111110000010111010111101110110001100101010110011110100010111100101011001111010001111001111100010101100100110111001001101000010111100100000000110111011101010001110011011001101110000011101010110011101110000011111110011000000111011111010100111111011010100011101000001001010111100000000101101001";
	public static String PENN_MORSE_340_REVERSED = "1001011010000000011110101001000001011100010101101111110010101111101110000001100111111100000111011100110101011100000111011001101100111000101011101110110000000010011110100001011001001110110010011010100011111001111000101111001101010011110100010111100110101010011000110111011110101110100000111110000101000100100001111010001001110000100100";
	public static String PENN_MORSE_RANDOM = "0011011011011000000100111001011110110111010001100010100010000111001000010001011001000001101000000000100111100010100011011111011010100101101100100101110100011111111111011110110000011101100010010100001001111111101001100010111111100101000011100100001101110101011001100111011111011010110010101001010011000000111001101100001111100111011101";
	
	public static String pi = "3141592653589793238462643383279502884197169399375105820974944592307816406286208998628034825342117067";

	/** convert the given string to digital morse */
	public static String toMorse(String str) {
		return toMorse(str, false, false);
	}

	/** convert the given string to digital morse */
	public static String toMorse(String str, boolean delimiters, boolean symbols) {
		StringBuffer sb = new StringBuffer();
		String upper = str.toUpperCase();
		for (int i=0; i<upper.length(); i++) {
			String m = morseTable.get(upper.charAt(i));
			if (symbols) m = m.replaceAll("0", "*").replaceAll("1","-");
			if (delimiters && sb.length() > 0) sb.append(" / ");
			sb.append(m);
		}
		return sb.toString();
	}
	/** scan the given morse string for words using the frequency-backed word list */
	public static void findWords(String morse) {
		WordFrequencies.init();
		for (String word : WordFrequencies.map.keySet()) {
			String m = toMorse(word);
			if (morse.contains(m)) {
				System.out.println(word.length() + ", " + WordFrequencies.freq(word) + ", " + word + ", " + toMorse(word, true, true) + ", " + m.length() + ", " + morse.indexOf(m));
			}
		}
	}
	
	/** convert all words to Morse, and determine the morse strings that have the most ambiguous interpretations */
	public static void ambiguousWords() {
		Map<String, List<String>> map = new HashMap<String, List<String>>(); 
		WordFrequencies.init();
		for (String word : WordFrequencies.map.keySet()) {
			if (WordFrequencies.freq(word) < 100) continue;
			String morse = toMorse(word);
			if (morse.contains("null")) continue;
			List<String> list = map.get(morse);
			if (list == null) list = new ArrayList<String>();
			list.add(WordFrequencies.freq(word) + " " + word);
			map.put(morse, list);
		}
		for (String key : map.keySet()) {
			//if (map.get(key).size() > 5) 
				System.out.println(map.get(key).size() + ", " + key + ", " + map.get(key));
		}
	}

	
	/** scan the given morse string for words using the given file of words.  exclude words that were already in the original. */
	public static void findWords(String dictionaryFile, String fileToSearch) {
		//LuceneService.init("/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/dictionaries/lucene-index");
		
		Text text = new Text(FileUtil.loadSBFrom(fileToSearch));
		text.morse();
		text.dump();

		BufferedReader input = null;
		int counter = 0;
		try {
			input = new BufferedReader(new FileReader(new File(dictionaryFile)));
			String line = null; // not declared within while loop
			while ((line = input.readLine()) != null) {
				line = FileUtil.convert(line).toString();
				//String wordInMorse = toMorse(line);
				System.out.println(line);
				if (line.length() < 4) continue;
				//if (original.indexOf(line) > -1) continue;
				
				/*
				int start = 0;
				int found = morse.indexOf(wordInMorse, start);
				while (found > -1) {
					System.out.println(line.length() + "," + line + "," + wordInMorse+","+found);
					start = found+1;
					found = morse.indexOf(wordInMorse, start);
				}*/
				text.findStringAsMorse(line);
				counter++;
			}
			System.out.println("read " + counter + " lines.");
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} 
		
		try {
			input.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}

	public static void process(String dictionaryFile, String fileToSearch) {
		findWords(dictionaryFile, fileToSearch);
	}
	
	/** consider digitized morse string of length L.  determine how many plaintext interpretations can be represented with it. */
	public static long plaintextsFor(int L) {
		if (L == 0) return 0;
		
		long sum = 0;
		for (Character key : morseTable.keySet() ) {
			String val = morseTable.get(key);
			if (val.length() > L) continue;
			if (val.length() == L) sum++;
			else sum += plaintextsFor(L-val.length());
		}
		return sum;
	}
	
	public static long toDecimal(String binary) {
		long sum = 0;
		long factor = 1;
		
		for (int i=binary.length()-1; i>=0; i--) {
			char ch = binary.charAt(i);
			if (ch == '1') {
				sum += factor;
			}
			factor *= 2;
		}
		
		return sum;
	}
	
	/** dump all substrings, converted to decimal, of the given binary string */ 
	public static void dumpDecimals(String morse, Long search) {
		for (int L=1; L<morse.length(); L++) {
			for (int i=0; i<morse.length()-L; i++) {
				String sub = morse.substring(i,i+L);
				 
				if (search == null || (toDecimal(sub) - search == 0)) {
					long d = toDecimal(sub);
					if (d > 0 && d < 10000000000000000l && !sub.startsWith("0"))
						System.out.println(d + ", " + sub);
				}
			}
		}
	}
	/** dump all substrings, converted to decimal, of the given binary string - only show PI matches */ 
	public static void dumpDecimalsPi(String morse, Long search) {
		for (int L=4; L<morse.length(); L++) {
			for (int i=0; i<morse.length()-L; i++) {
				String sub = morse.substring(i,i+L);
				 
				if (search == null || (toDecimal(sub) - search == 0)) {
					long d = toDecimal(sub);
					if (d > 999) {
						String ps = piSearch(d, 2);
						if (!ps.equals("-1"))
							System.out.println(ps + ", " + d + ", " + sub);
					}
				}
			}
		}
	}
	
	public static String piSearch(long search, int max) {
		String result = "-1";
		for (int i=0; i<pi.length(); i++) {
			boolean go = true;
			int errors = 0;
			
			int p1 = i;
			int p2 = 0;
			boolean found = false;
			int matches = 0;
			while (go) {
				char ch1 = pi.charAt(p1);
				char ch2 = (""+search).charAt(p2);
				
				if (ch1 != ch2) errors ++;
				else {
					p2++;
					matches++;
				}
				p1++; 
				if (errors > max) {
					go = false;
					break;
				}
				if (p1 > pi.length()-1 || p2 > (""+search).length()-1) {
					go = false;
				}
				if (matches == (""+search).length()) {
					go = false;
					found = true;
				}
			}
			if (found) {
				result = ""+i;
				break;
			}
		}
		return result;
	}
	
	public static void main(String[] args) {
		//process("/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/dictionaries/words-300k.txt", "/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/letters/1966-11-29-the-confession.txt");;
		
		//WordFrequencies.init("/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/dictionaries/all.num.o5");
		//process("/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/ray-grant/myth-words-found-in-ray-article.txt", "/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/ray-grant/ray-article.txt");
		//process("/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/dictionaries/words-300k.txt", "/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/letters/1966-11-29-the-confession.txt");
		//process("/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/ray-grant/myth-words.txt", "/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/ray-grant/site-mirror/all.txt");
		//System.out.println(toMorse("gmywebsitessoIwontrepeatwhatIvesaidalreadyThepointaboutGarethwasthathe"));
		//System.out.println(toMorse("THRASKIAS"));
		//for (int i=0; i<100; i++)
		//	System.out.println(i+","+Math.pow(2,i)+","+plaintextsFor(i));
		//System.out.println(toMorse("BATES",true));
		//System.out.println(toMorse("BABE",true));
		//System.out.println(toMorse("BEGS",true));
		//System.out.println(toMorse("DIGS",true));
		//System.out.println(toMorse("BANS",true));
		//System.out.println(toMorse("KEITH", true));
		//System.out.println(toMorse("TASTES", true));
		//System.out.println(toMorse("CEASE", true));
		//System.out.println(toMorse("TRIBE", true));
		//System.out.println(toMorse("CITES", true));
		//System.out.println(toMorse("CUES", true));
		//findWords(PENN_MORSE_RANDOM);
		//ambiguousWords();
		//System.out.println(toDecimal("0010010000111"));
		findWords("1110010100111");
		//dumpDecimals(PENN_MORSE_340, null);
		//System.out.println(piSearch(1159l,1));
	}
}
