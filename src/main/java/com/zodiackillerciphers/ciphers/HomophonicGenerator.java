package com.zodiackillerciphers.ciphers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.zodiackillerciphers.ciphers.algorithms.columnar.ColumnarTransposition;
import com.zodiackillerciphers.ngrams.NGramsBean;
import com.zodiackillerciphers.tests.LetterFrequencies;
import com.zodiackillerciphers.transform.CipherTransformations;

public class HomophonicGenerator {
	public static Random rand = new Random();
	//public static String AZ = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	/**
	 * make a homophonic cipher from the given plaintext. apply simple rule: number
	 * of homophones proportionate to plaintext's letter frequency.
	 * 
	 * if cipherAlphabetSize is given, then make sure the generated cipher has that many unique symbols in it.
	 */
	public static String makeHomophonic(String plaintext, boolean textVersion) {
		plaintext = fix(plaintext);
		// compute length without spaces
		int length = 0;
		for (int i = 0; i < plaintext.length(); i++) {
			if (plaintext.charAt(i) == ' ')
				continue;
			length++;
		}
//		System.out.println("Length without spaces and punctuation: " + length);

		Map<Character, Integer> counts = Ciphers.countMap(plaintext);
		Map<Character, int[]> homophones = new HashMap<Character, int[]>();
		int cipherAlphabetSize = 0;
		double factor = 0.75 - 3*Math.random()/10;  
		for (Character p : counts.keySet()) {
			if (p == ' ')
				continue;
			float freq = counts.get(p);
			freq /= length;
			long homophoneCount = Math.round(freq * 100 * factor);
//			System.out.println(p + " " + counts.get(p) + " " + Math.round(10000 * freq) / 100f + "% " + homophoneCount);
			if (homophoneCount < 1)
				homophoneCount = 1;
			homophones.put(p, new int[(int)homophoneCount]);
			cipherAlphabetSize += homophoneCount;
		}

		int[] alphabet = new int[cipherAlphabetSize];
		for (int i = 0; i < alphabet.length; i++) {
			alphabet[i] = i;
		}
		shuffle(alphabet);
//		System.out.println("Alphabet size " + alphabet.length + ": " + Arrays.toString(alphabet));
//		System.out.println("Multiplicity: " + ((float)cipherAlphabetSize)/length);
		// set the homophones for each plaintext letter
		int index = 0;
		for (Character p : homophones.keySet()) {
			int[] val = homophones.get(p);
			for (int i = 0; i < val.length; i++)
				val[i] = alphabet[index++];
//			System.out.println("Homophones for " + p + ": " + Arrays.toString(val));
		}

		// generate cipher by randomly drawing homophone for each plaintext letter
		Random rand = new Random();
		String cipher = "";
		String cipherAsText = "";
		for (int i = 0; i < plaintext.length(); i++) {
			char p = plaintext.charAt(i);
			if (p == ' ')
				cipher += "/ ";
			else {
				int[] homs = homophones.get(p);
				int hom = homs[rand.nextInt(homs.length)];
				cipher += hom + " ";
				cipherAsText += Ciphers.alphabet[5].charAt(hom);
			}
		}
		cipher = cipher.replaceAll(" / ", "/");
		cipher = cipher.replaceAll("//", "/");
		cipher = cipher.replaceAll("/ ", "/");
		cipher = cipher.replaceAll(" /", "/");
		
//		String[] split = cipher.split("/");
//		for (int i=0; i<split.length; i++) {
//			System.out.println(" - word #" + i + ", length " + split[i].split(" ").length);
//		}
		if (textVersion) return cipherAsText;
		return cipher;
	}
	
	/** make homophonic cipher that has the given cipher alphabet size. */
	public static String makeHomophonic(String plaintext, int cipherAlphabetSize) {
		StringBuilder alphabetCipher = new StringBuilder();
		StringBuilder ca = new StringBuilder(Ciphers.alphabet[0]);
		shuffle(ca);
		for (int i=0; i<cipherAlphabetSize; i++) {
			alphabetCipher.append(ca.charAt(i));
		}
		shuffle(alphabetCipher);
//		System.out.println("cipher alphabet: " + alphabetCipher);

		while (true) {
			// map a pt unit to one or more ct units
			Map<Character, StringBuilder> map = new HashMap<Character, StringBuilder>();

			StringBuilder cipher = new StringBuilder();
			// seed with one cipher letter to ensure each pt letter is represented.
			int c = 0;
			String alphabetPlaintext = Ciphers.alphabet(plaintext);
//			System.out.println("alphabetPlaintext " + alphabetPlaintext);
			for (int i=0; i<alphabetPlaintext.length(); i++) {
				Character pt = alphabetPlaintext.charAt(i);
				Character ct = alphabetCipher.charAt(c++);
				StringBuilder sb = new StringBuilder();
				sb.append(ct);
				map.put(pt, sb);
//				System.out.println(i + " " + pt + " " + ct);
			}
//			System.out.println("map " + map);
			for (; c<cipherAlphabetSize; c++) {
				// pick random plaintext letters, proportional to their frequency in the given plaintext
				Character pt = plaintext.charAt(rand.nextInt(plaintext.length()));
				StringBuilder ct = map.get(pt);
				if (ct == null) ct = new StringBuilder();
				ct.append(alphabetCipher.charAt(c));
//				System.out.println(" - pt " + pt + " ct " + ct + " c " + c);
				map.put(pt, ct);
			}
			for (int i=0; i<plaintext.length();i++) {
				Character pt = plaintext.charAt(i);
				StringBuilder ct = map.get(pt);
				cipher.append(ct.charAt(rand.nextInt(ct.length())));
			}
			
//			System.out.println("map: " + map);
			if (Ciphers.alphabet(cipher.toString()).length() == cipherAlphabetSize) {
				return cipher.toString();
			}
		}
	}
	
	/** use the given symbol frequencies and pt-ct assignments to generate a homophonic cipher.
	 */
	public static String makeHomophonic(Map<Character, Integer> ctCounts, Map<Character, Set<Character>> p2c,
			String plaintext) {
		StringBuffer ctAlphabet = new StringBuffer();
		for (Character c : ctCounts.keySet()) ctAlphabet.append(c);
		Random rand = new Random();
		// make letter pools
		Map<Character, String> letterPool = new HashMap<Character, String>(); 
		for (Character p : p2c.keySet()) {
			StringBuffer sb = new StringBuffer();
			for (Character c : p2c.get(p)) {
				for (int i=0; i<ctCounts.get(c); i++) {
					sb.append(c);
				}
			}
			letterPool.put(p, CipherTransformations.shuffle(sb.toString()));
		}
		// indices into letter pools
		Map<Character, Integer> letterPoolIndices = new HashMap<Character, Integer>();
		for (Character p : letterPool.keySet())
			letterPoolIndices.put(p, 0);
		
		StringBuffer cipher = new StringBuffer();
		for (int i=0; i<plaintext.length(); i++) {
			char p = plaintext.charAt(i);
			int pos = letterPoolIndices.get(p);
			String letters = letterPool.get(p);
			if (letters.isEmpty()) {
				// no ct available, so pick one at random
				cipher.append(ctAlphabet.charAt(rand.nextInt(ctAlphabet.length())));
			} else {
				cipher.append(letters.charAt(pos % letters.length())); // wrap around if we run out of ct assignments
				pos++;
				letterPoolIndices.put(p, pos);
			}
		}
		return cipher.toString();
	}
	
	/** calculate variance of the symbol counts, but just the numerator */
	public double varianceNumerator(StringBuilder sb) {
		Map<Character, Integer> map = new HashMap<Character, Integer>();
		for (int i=0; i<sb.length(); i++) {
			Character key = sb.charAt(i);
			Integer val = map.get(key);
			if (val == null) val = 0;
			val++;
			map.put(key, val);
		}
		
		double mean = 0;
		for (Integer i : map.values()) {
			mean += i;
		}
		mean = mean/map.size();
		
		double variance = 0;
		for (Integer i : map.values()) {
			variance += Math.pow(i-mean, 2);
		}
		return variance;
	}
	
	/** convert to uppercase, remove punctuation */
	public static String fix(String str) {
		String result = "";
		String notLetters = "";
		str = str.toUpperCase();
		for (int i=0; i<str.length(); i++) {
			char ch = str.charAt(i);
			if (Character.isLetter(ch) || ch == ' ') 
				result += ch;
			else notLetters += "[" + ch + "] ";
		}
//		System.out.println("not letters: " + notLetters);
		return result;
	}

	public static void shuffle(int[] array) {
		if (array == null)
			return;
		Random rand = new Random();
		for (int i = array.length - 1; i >= 1; i--) {
			int j = rand.nextInt(i + 1);
			if (i == j)
				continue;
			int tmp = array[i];
			array[i] = array[j];
			array[j] = tmp;
		}
	}
	public static void shuffle(StringBuilder sb) {
		if (sb == null)
			return;
		Random rand = new Random();
		for (int i = sb.length() - 1; i >= 1; i--) {
			int j = rand.nextInt(i + 1);
			if (i == j)
				continue;
			char tmp = sb.charAt(i);
			sb.setCharAt(i, sb.charAt(j));
			sb.setCharAt(j, tmp);
		}
	}
	
	/** convert symbolic homophonic cipher into floating point representation.
	 * each unique symbol is numbered in order of appearance (0, 1, 2, 3, etc).
	 * then each occurrence is normalized based on the alphabet size.
	 * for example, if alphabet size is 51, then symbol 4 is converted to 4/(51-1) = 0.08   
	 */
	public static double[] toDoubles(String cipher) {
		if (cipher == null) return null;
		double[] result = new double[cipher.length()];
		int next = 0;
		Map<Character, Integer> map = new HashMap<Character, Integer>();
		for (int i=0; i<cipher.length(); i++) {
			char ch = cipher.charAt(i);
			Integer val = map.get(ch);
			if (val == null) {
				val = next++;
			}
			map.put(ch, val);
			result[i] = val;
		}
		next--;
		for (int i=0; i<result.length; i++) {
			result[i] = result[i] / next;
		}
		return result;
	}
	
	/** convert back from float */
	public static String fromDoubles(double[] doubles) {
		if (doubles == null) return null;
		String cipher = "";
		Map<Double, Character> map = new HashMap<Double, Character>();
		int next = 0;
		for (int i=0; i<doubles.length; i++) {
			double d = doubles[i];
			Character c = map.get(d);
			if (c == null) {
				c = Ciphers.alphabet[5].charAt(next++);
			}
			map.put(d, c);
			cipher += c;
		}
		return cipher;
	}
	public static String fromDoubles(String doublesString) {
		doublesString = doublesString.replaceAll(" ", "");
		String[] split = doublesString.split(",");
		return fromDoubles(split);
	}
	public static String fromDoubles(String[] split) {
		double[] doubles = new double[split.length];
		for (int i=0; i<split.length; i++) {
			doubles[i] = Double.valueOf(split[i]);
		}
		return fromDoubles(doubles);
	}
	
	public static void testOld() {
		//		System.out.println(makeHomophonic(
		//		"I LIKE KILLING PEOPLE BECAUSE IT IS SO MUCH FUN IT IS MORE FUN THAN KILLING WILD GAME IN THE FORREST BECAUSE MAN IS THE MOST DANGEROUS ANIMAL OF ALL TO KILL SOMETHING GIVES ME THE MOST THRILLING EXPERENCE IT IS EVEN BETTER THAN GETTING YOUR ROCKS OFF WITH A GIRL THE BEST PART OF IT IS THAT WHEN I DIE I WILL BE REBORN IN PARADICE AND ALL THE I HAVE KILLED WILL BECOME MY SLAVES I WILL NOT GIVE YOU MY NAME BECAUSE YOU WILL TRY TO SLOW DOWN OR STOP MY COLLECTING OF SLAVES FOR MY AFTERLIFE",
		//		false));
		//String pt = "WITH THE WHOLE MENTAL HEALTH AND PSYCHEDELICSCAREER GPA DOESNT MATTER BUT I SHOULDNT HAVE TO START ON A PC OR MAC EDIT SON OF A BITCH NOT SECONDS AFTER I SAY THAT IT SHITS THE BED AGAIN THAT MILK CARTON IS OBVIOUSLY A FORGERY WOW SO VERIFY U YODOGEBOT U KINGBOBTV DOGECOIN S NBSP HELP HTTP WWW REDDIT COM R DOGETIPBOT WIKI INDEX I WATCHED UP UNTIL AROUND WRESTLEMANIA I HEARD ABOUT THE NETWORK AND THOUGHT THATS A BOLD";
		//pt = pt.replaceAll(" ", "");
		//int[] key = ColumnarTransposition.randomKey(20);
		//System.out.println(Arrays.toString(key));
		//StringBuilder col = ColumnarTransposition.encode(new StringBuilder(pt), key);
		//System.out.println(col);
		//StringBuilder dec = ColumnarTransposition.decode(col, key);
		//System.out.println(dec);
		//String hom = makeHomophonic(col.toString(), true);
		//String hom = makeHomophonic(pt, true);
		//System.out.println(hom);
		//double[] doubles = toDoubles(hom);
		//System.out.println(Arrays.toString(doubles));
		//System.out.println(fromDoubles(doubles));
		//String hom = makeHomophonic(Ciphers.Z408_SOLUTION.substring(0, 340), 63);
		//System.out.println(hom);
		//NGramsBean ng = new NGramsBean(2, hom);
		//System.out.println(ng.numRepeats());
		//System.out.println(makeHomophonic("IFTHEREARENODOGSINHEAVENTHENWHENIDIEIWANTTOGOWHERETHEYWENTWILLROGERS", 16));
	}
	public static void makeSubstitutionCipherForMyShow() {
//		System.out.println(makeHomophonic("IFTHEREARENODOGSINHEAVENTHENWHENIDIEIWANTTOGOWHERETHEYWENTWILLROGERS", 16));
		//System.out.println(makeHomophonic("RUNNINGISNOTHINGMORETHANASERIESOFARGUMENTSBETWEENTHEPARTOFYOURBRAINTHATWANTSTOSTOPANDTHEPARTTHATWANTSTOKEEPGOINGUNKNOWN", 19));
		System.out.println(makeHomophonic("THEDESIREOFKNOWLEDGELIKETHETHIRSTOFRICHESINCREASESEVERWITHTHEACQUISITIONOFITLAURENCESTERNE", 19));
	
	}

	public static void main(String[] args) {
		makeSubstitutionCipherForMyShow();
	}
}
