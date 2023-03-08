package com.zodiackillerciphers.tests.faycal;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.ciphers.Z340Solution;
import com.zodiackillerciphers.dictionary.WordFrequencies;

/** test variations of Fayval Ziraoui's encryption */
public class FaycalZiraoui {
	
	String cipher = Ciphers.Z13;
	/** current state of decryption */
	StringBuffer decryptionState;
	/** integer sequence state */
	int[] sequence;
	
	
	static Set<String> seen = new HashSet<String>();
	static Set<String> matches = new HashSet<String>();
	static {
		matches.add("ARTH");
		matches.add("ALLN");
		matches.add("ALAN");
		matches.add("DICK");
		matches.add("GAIK");
		matches.add("GYKE");
		matches.add("POST");
		matches.add("GARY");
		matches.add("FRAN");
		matches.add("FRED");
		matches.add("TEDK");
		matches.add("KACZ");
		matches.add("UNAB");
		matches.add("THEO");
		matches.add("KANE");
		matches.add("RICH");
		matches.add("RICK");
		matches.add("MARS");
		matches.add("LAWR");
		matches.add("LEIG");
		matches.add("ALLE");
		matches.add("ROSS");
		matches.add("KAYE");
		matches.add("MYER");
		matches.add("IVAN");
		matches.add("EARL");
		matches.add("BEST");
		matches.add("VANB");
		matches.add("EVBJ");
		matches.add("NICH");
		matches.add("ROBN");
		matches.add("JJDA");
		matches.add("JJDE");
		matches.add("JACK");
		matches.add("TARR");
		matches.add("EDED");
		matches.add("WJOG");
		matches.add("WILL");
		matches.add("BILL"); // william joseph grant 
	}
	
	/** apply the given key */
	void stepApplySubstitution(Map<Character, Character> key) {
		decryptionState = new StringBuffer();
		for (int i=0; i<cipher.length(); i++) {
			char c= cipher.charAt(i);
			Character p = key.get(c);
			if (p == null) p = '_';
			decryptionState.append(p);
		}
	}
	
	/** convert letters to numbers, using the given offset */
	void stepAlphaToNumber(int offset) {
		sequence = new int[cipher.length()];
		for (int i = 0; i < decryptionState.length(); i++) {
			char c = decryptionState.charAt(i);
			if (c < 'A' || c > 'Z')
				sequence[i] = -1;
			else
				sequence[i] = (offset + toNumber(decryptionState.charAt(i))) % 26;
		}
	}

	/** substitute the "Cancer" symbols */
	void stepCancer(int target) {
		for (int i=4; i<9; i+=2) {
			sequence[i] = target;
		}
	}
	/** substitute the "Aries" symbol */
	void stepAries(int target) {
		sequence[9] = target;
	}
	
	
	// convert capital letter to numerical equivalent
	static int toNumber(char c) {
		return c-65;
	}
	// convert number (in range [0,25]) back to capital letter. 
	static char fromNumber(int val) {
		if (val < 0 || val > 25) throw new IllegalArgumentException("Must be in range [0,25]");
		return (char) (val+65);
	}
	/** apply modulo function to entire sequence */
	void stepModulo(int modulo) {
		if (modulo == 0) return;
		for (int i=0; i<sequence.length; i++) {
			sequence[i] = sequence[i] % modulo; 	
		}
	}
	
	static String operatorDescription(int operation) {
		if (operation == 0) 
			return "addition";
		if (operation == 1) 
			return "subtraction";
		if (operation == 2) 
			return "multiplication";
		if (operation == 3) 
			return "division";
		if (operation == 4) 
			return "remainder";
		return "UNKNOWN";
	}
	
	/** perform arithmetic operation on adjacent numbers in the sequence.
	 * creates new sequence which has one less element than the original sequence. */
	void stepArithmetic(int operation) {
		// 0=addition
		// 1=subtraction
		// 2=multiplication
		// 3=division (with truncation)
		// 4=remainder
		
		// TODO: gematria
		
		int[] newSequence = new int[sequence.length-1];
		for (int i=0; i<newSequence.length; i++) {
			int a=sequence[i];
			int b=sequence[i+1];
			int result = 0;
			if (operation == 0) {
				result = a + b;
			} else if (operation == 1) {
				result = a - b;
			} else if (operation == 2) {
				result = a * b;
			} else if (operation == 3) {
				if (b == 0) 
					result = a; // zero-safe division 
				else 
					result = a / b;
			} else if (operation == 4) {
				if (b == 0) 
					result = a; // zero-safe division 
				else 
					result = a % b;
			}
			result = Math.abs(result); // don't allow negative numbers;
			newSequence[i] = result;
		}
		sequence = newSequence;
	}
	
	/** return distinct digits in play */
	Integer[] distinctDigits() {
		Set<Integer> uniqs = new HashSet<Integer>();
		for (int i=0; i<sequence.length; i++) 
			uniqs.add(sequence[i]);
		Integer[] arr = new Integer[uniqs.size()];
		int i = 0;
		for (Integer val : uniqs) arr[i++] = val;
		Arrays.sort(arr);
		return arr;
	}

	// fisher-yates shuffle given array 
	public static void shuffle(int[] array) {
		Random rand = new Random();
		if (array == null) return;;
		for (int i=array.length-1; i>=1; i--) {
			int j = rand.nextInt(i+1);
			int tmp = array[i];
			array[i] = array[j];
			array[j] = tmp;
		}
	}
	
	/** normalize to base N by converting to digits selected from [0, N-1] */
	void stepNormalizeToBase(Integer[] digitAssignments) {
		Map<Integer, Integer> map = new HashMap<Integer, Integer>(); // map digits to normalized digits
		for (int i=0; i<digitAssignments.length; i++) {
			map.put(digitAssignments[i], i);
		}
		for (int i=0; i<sequence.length; i++) {
			sequence[i] = map.get(sequence[i]);
		}
		
	}
	
	/** convert from given base back to base 10 sequence.  group size is assumed to be 3. */ 
	void stepFromBase(int base) {
		//if (base < 2 || base > 4) return;
		if (base < 2 || base > 4) {
			throw new IllegalArgumentException("Only base in range [2,4] is allowed for now.");
		}
		
		int GROUP_SIZE = 3;
		
		int[] base10 = new int[sequence.length/GROUP_SIZE];
		
		for (int i=0; i<base10.length; i++) {
			
			int val = 0;
			for (int j=0; j<GROUP_SIZE; j++) {
				int k = i*GROUP_SIZE + j;
				int increment = (int) Math.pow(base, GROUP_SIZE-j-1);
				val += sequence[k]*increment;
				//System.out.println(i + " " + j + " " + k + " " + sequence[k] + " " + increment + " " + val);
			}
			base10[i] = val;
		}
		sequence = base10;
	}
	
	/** convert numbers back to alphabet.  assumes numbers are in range [0,25].  apply mod 26 to avoid going past alphabet boundary. */
	void stepBackToAlphabet() {
		decryptionState = new StringBuffer();
		for (int i=0; i<sequence.length; i++) 
			decryptionState.append(fromNumber(sequence[i] % 26));
	}
	
	static StringBuffer decrypt(Params params, boolean showSteps) {
		FaycalZiraoui fz = new FaycalZiraoui();
		fz.stepApplySubstitution(params.key);
		if (showSteps)
			System.out.println(" - Applied key: " + fz.decryptionState);
		fz.stepAlphaToNumber(params.offset);
		if (showSteps)
			System.out.println(" - Converted to numbers: " + Arrays.toString(fz.sequence));
		fz.stepCancer(params.substitutes[0]);
		fz.stepAries(params.substitutes[1]);
		if (showSteps)
			System.out.println(" - Assigned numbers to special symbols: " + Arrays.toString(fz.sequence));
		fz.stepModulo(params.modulo);
		if (showSteps)
			System.out.println(" - Applied modulo " + params.modulo + ": " + Arrays.toString(fz.sequence));
		fz.stepArithmetic(params.operator);
		if (showSteps)
			System.out.println(" - Performed " + operatorDescription(params.operator) + " to successive numbers: " + Arrays.toString(fz.sequence));
		Integer[] digits = fz.distinctDigits();
		if (showSteps)
			System.out.println(" - All digits used: " + Arrays.toString(digits));
		if (digits.length != params.base)
			return null; // TODO: we can still convert base n using less than n distinct digits
		Integer[] digitsReordered = new Integer[digits.length];
		for (int i=0; i<digits.length; i++) 
			digitsReordered[i] = digits[params.digitOrdering[i]];
		if (showSteps)
			System.out.println(" - Digits reordered: " + Arrays.toString(digitsReordered));
		fz.stepNormalizeToBase(digitsReordered);
		if (showSteps)
			System.out.println(" - Converted to base " + params.base + " sequence: " + Arrays.toString(fz.sequence));
		fz.stepFromBase(params.base);
		if (showSteps)
			System.out.println(" - Back to base 10: " + Arrays.toString(fz.sequence));
		fz.stepBackToAlphabet();
		if (showSteps)
			System.out.println("BACK TO LETTERS: " + fz.decryptionState);
		
		return fz.decryptionState;

	}

	/** try some random variations of Faycal's approach */ 
	static void randomVariation() {
		
		Random random = new Random();
		
		// what about "3" for Cancer (c=3) and "1" for Aries (A=1)?  or other assignments based on alternate zodiac sign lists?
		// try 8 and 0 for the circled 8's.		
		int[][] substitutes = new int[][] {
			{4, 1}, // faycal's assignments
			{3, 1}, // cancer=3, aries=1
			{8, 1}, // cancer=8, aries=1  (because cancer looks like circled 8)
			{0, 1}, // cancer=0, aries=1  (because cancer looks like 0)
			{6, 3}, // in tropical zodiac, the signs start June and March
			{7, 4}, // in tropical zodiac, the signs end July and April
			{7, 4}, // in sidereal zodiac, the signs start July and April
			{8, 5}, // in sidereal zodiac, the signs end July and April
			
			{8, 3}, // cancer=8, aries=March
			{0, 3}, // cancer=0, aries=March

			{8, 4}, // cancer=8, aries=April
			{0, 4}, // cancer=0, aries=April

			{8, 4}, // cancer=8, aries=May
			{0, 4}, // cancer=0, aries=May
			
		};
		
		Params params = new Params();
		
		boolean whichKey = random.nextBoolean();
		params.key = whichKey ? Z340Solution.z340SolutionKey()
				: Ciphers.decoderMapFor(Ciphers.Z408, Ciphers.Z408_SOLUTION.toUpperCase());
		params.whichKey = whichKey ? 340 : 408;
		
		params.offset = random.nextBoolean() ? 0 : 1;
		
		params.substitutes = substitutes[random.nextInt(substitutes.length)];
		
		params.modulo = random.nextInt(26);
		
		params.operator = random.nextInt(5);

		
		params.base = random.nextInt(3) + 2; // [2,4]
		params.digitOrdering = new int[params.base];
		for (int i=0; i<params.base; i++)
			params.digitOrdering[i] = i;
		shuffle(params.digitOrdering);
		
		StringBuffer sb = decrypt(params, false);
		if (sb != null) {
			int freq = WordFrequencies.freq(sb.toString());
			String oneMistake = oneMistake(sb.toString());
			if (oneMistake.length() > 0 || matches.contains(sb.toString())) freq = 100000000;
			if (freq > 100) {
				String result = freq + "	" + sb + "	" + params + "	" + oneMistake;
				if (seen.contains(result)) return;
				seen.add(result);
				System.out.println(result);
			}
		}
	}
	
	static String oneMistake(String str) {
		StringBuffer hits = new StringBuffer();
		for (String match : matches) {
			int errors = 0;
			for (int i=0; i<match.length(); i++) {
				char c1 = match.charAt(i);
				char c2 = str.charAt(i);
				if (c1 != c2) errors++;
				if (errors > 1) break;
			}
			if (errors == 1) {
				if (hits.length() > 0) hits.append(", ");
				hits.append(match);
			}
		}
		return hits.toString();
	}
	
	
	/** test faycal's original encoding */
	static void testFaycalSolution() {
		// parameterized version
		Params params = new Params();
		params.key = Z340Solution.z340SolutionKey();
		params.offset = 1;
		params.substitutes = new int[] {4, 1};
		params.modulo = 10;
		params.operator = 1;
		params.digitOrdering = new int[] {1, 2, 0};
		params.base = 3;
		System.out.println(decrypt(params, true));
	}
	static void testSolutions() {
		Params params = new Params();
		// SAID
		params.key = Ciphers.decoderMapFor(Ciphers.Z408, Ciphers.Z408_SOLUTION.toUpperCase());
		params.offset = 0;
		params.substitutes = new int[] {8, 4};
		params.modulo = 5;
		params.operator = 3;
		params.digitOrdering = new int[] {1, 2, 0};
		params.base = 3;
		System.out.println(decrypt(params, true));
		// EARL
		params.key = Z340Solution.z340SolutionKey();
		params.offset = 1;
		params.substitutes = new int[] {0, 4};
		params.modulo = 5;
		params.operator = 1;
		params.digitOrdering = new int[] {1, 2, 0, 3};
		params.base = 4;
		System.out.println(decrypt(params, true));
		// FRED
		params.key = Ciphers.decoderMapFor(Ciphers.Z408, Ciphers.Z408_SOLUTION.toUpperCase());
		params.offset = 1;
		params.substitutes = new int[] {6, 3};
		params.modulo = 19;
		params.operator = 3;
		params.digitOrdering = new int[] { 3, 0, 2, 1 };
		params.base = 4;
		System.out.println(decrypt(params, true));
		// 43888    ALEX    Params [key=408, offset=1, substitutes=[4, 1], modulo=6, operator=3, digitOrdering=[1, 2, 3, 0], base=4]
		// 20560    JAKE    Params [key=340, offset=0, substitutes=[8, 4], modulo=3, operator=0, digitOrdering=[1, 0, 2, 3], base=4]
		// 20560    JAKE    Params [key=340, offset=0, substitutes=[8, 1], modulo=3, operator=0, digitOrdering=[1, 0, 2, 3], base=4]
		// 12474    MARC    Params [key=408, offset=1, substitutes=[0, 4], modulo=11, operator=4, digitOrdering=[3, 0, 1, 2], base=4]
		
		
		
		
	}
	
	public static void searchVariations() {
		WordFrequencies.init();
		while (true) 
			randomVariation();
	}
	
	public static void main(String[] args) {
		testFaycalSolution();
		testSolutions();
//		searchVariations();
	}
	
	
	
}
