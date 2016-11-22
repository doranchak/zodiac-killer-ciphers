package com.zodiackillerciphers.numerology;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.ciphers.generator.Generator;
import com.zodiackillerciphers.dictionary.WordFrequencies;

public class MarcleanTests {
	/**
	 * http://zodiacode1933.blogspot.com.br/2013/07/ebeorietemethhpiti-theory.
	 * html
	 */
	/** http://www.zodiackillersite.com/viewtopic.php?f=25&t=769 */

	static int splitPoint;
	
	static int[] driversLicense = new int[] { 6,7,2,3,5,2 };
	
	public static void marcleanFactors() {
		factor(8672352);
		factor(8672);
		factor(352);
	}
	
	public static void countsFor(StringBuffer sb, Map<Character, Integer> counts) {
		for (int i=0; i<sb.length(); i++) {
			Character key = sb.charAt(i);
			Integer val = counts.get(key);
			if (val == null) val = 0;
			val++;
			counts.put(key, val);
		}
	}
	
	public static int sumFor(String word, Map<Character, Integer> counts) {
		int sum = 0;
		for (int i=0; i<word.length(); i++) {
			sum += counts.get(word.charAt(i));
		}
		return sum;
	}
	
	public static boolean is67(String word, Map<Character, Integer> counts, int digit1, int digit2) {
		int sum = 0;
		
		int i;
		for (i=0; i<word.length(); i++) {
			sum += counts.get(word.charAt(i));
			if (sum > digit1) return false;
			if (sum == digit1) break;
		}
		
		if (sum != digit1) return false;
		sum = 0;
		for (int j=i+1; j<word.length(); j++) {
			sum += counts.get(word.charAt(j));
			if (sum > digit2) return false;
		}
		
		if (sum == digit2) {
			splitPoint = i;
			return true;
		}
		return false;
		
	}
	
	public static void marcleanDOB() {
		int MIN_LENGTH = 4;
		
		WordFrequencies.init(50000);
		Map<Character, Integer> counts = new HashMap<Character, Integer>();
		
		/*
		for (String word1 : WordFrequencies.map.keySet()) {
			count++;
			pNew = (int) ((float)100*count)/WordFrequencies.map.size();
			if (pNew != pOld) System.out.println(pNew + "%...");
			pOld = pNew;
			if (word1.length() < MIN_LENGTH) continue;
			for (String word2 : WordFrequencies.map.keySet()) {
				if (word2.length() < MIN_LENGTH) continue;
				for (String word3 : WordFrequencies.map.keySet()) {
					if (word3.length() < MIN_LENGTH) continue;

					StringBuffer sb = new StringBuffer();
					sb.append(word1.toUpperCase()).append(word2.toUpperCase()).append(word3.toUpperCase());
					counts.clear();
					countsFor(sb, counts);
					if (sumFor(word1, counts) != 10) continue;
					if (sumFor(word2, counts) != 13) continue;
					if (!is67(word3, counts)) continue;
					dump(word1, word2, word3, counts);
				}
				
			}
			
		}*/
		
		/*
		while (true) {
			String word1 = randWord();
			String word2 = randWord();
			String word3 = randWord();
			
			if (!WordFrequencies.isAlpha(word1)) continue;
			if (!WordFrequencies.isAlpha(word2)) continue;
			if (!WordFrequencies.isAlpha(word3)) continue;
			
			StringBuffer sb = new StringBuffer();
			sb.append(word1.toUpperCase()).append(word2.toUpperCase()).append(word3.toUpperCase());
			counts.clear();
			countsFor(sb, counts);
			if (sumFor(word1, counts) != 10) continue;
			if (sumFor(word2, counts) != 13) continue;
			if (!is67(word3, counts)) continue;
			dump(word1, word2, word3, counts);
			
		}*/
		
		String[] testWords = new String[] {
				"THERE", "IS", "A", "STANDARD", "METHOD", "IS", "THAT", "WHOEVER", "CREATED", "THE", "REPETITION", "OF", "LETTERS", "IN", "A", "CERTAIN", "SEQUENCE", "IS", "NOT", "NUMEROLOGY", "AGAIN", "I", "WANT", "YOU", "TO", "SHOW", "THE", "SAME", "RESULT", "STARTING", "FROM", "THE", "SAME", "PREMISE", "AND", "NOT", "ONLY", "GETTING", "A", "PART", "OF", "IT", "HALLOWEN", "BELOW", "THE", "DEFAULT", "METHOD", "IS", "THE", "SAME"		
		};
		
		for (String word1 : testWords) {
			for (String word2 : testWords) {
				for (String word3 : testWords) {
					
					/*
					StringBuffer sb = new StringBuffer();
					sb.append(word1.toUpperCase()).append(word2.toUpperCase()).append(word3.toUpperCase());
					counts.clear();
					countsFor(sb, counts);
					if (sumFor(word1, counts) != 12) continue;
					if (sumFor(word2, counts) != 18) continue;
					if (!is67(word3, counts, 3, 3)) continue;
					dump(word1, word2, word3, counts);*/
					
					NumerologyDate n = new NumerologyDate(word1, word2, word3);
					n.dump();

					
				}
				
			}
			
		}
		
		
	}
	
	/** http://www.zodiackillersite.com/viewtopic.php?p=45481#p45481 */
	public static void marclean408DOB() {
		String z408sol = Ciphers.cipher[1].solution.toUpperCase();
		System.out.println(z408sol);
		Map<Character, Integer> map = Ciphers.countMap(z408sol);
		List<Character> list = new ArrayList<Character>(map.keySet());
		for (int i=0; i<list.size()-1; i++) {
			for (int j=i+1; j<list.size(); j++) {
				char c1 = list.get(i);
				char c2 = list.get(j);
				
				int val1 = map.get(c1);
				int val2 = map.get(c2);
				
				int val3 = c1-64;
				int val4 = c2-64;
				
				int[][] trials = new int[][] {
						{val1, val2, val3, val4},
						{val1, val2, val4, val3},
						{val1, val3, val2, val4},
						{val1, val3, val4, val2},
						{val1, val4, val2, val3},
						{val1, val4, val3, val2},
						{val2, val1, val3, val4},
						{val2, val1, val4, val3},
						{val2, val3, val1, val4},
						{val2, val3, val4, val1},
						{val2, val4, val1, val3},
						{val2, val4, val3, val1},
						{val3, val1, val2, val4},
						{val3, val1, val4, val2},
						{val3, val2, val1, val4},
						{val3, val2, val4, val1},
						{val3, val4, val1, val2},
						{val3, val4, val2, val1},
						{val4, val1, val2, val3},
						{val4, val1, val3, val2},
						{val4, val2, val1, val3},
						{val4, val2, val3, val1},
						{val4, val3, val1, val2},
						{val4, val3, val2, val1}
					};
				for (int[] trial : trials) {
					if (validDate2(""+trial[0], ""+trial[1], ""+trial[2], ""+trial[3])) {
						String line = c1 + "=" + pad(val3) + ", " + pad(val1) + " occurrences; ";
						line += c2 + "=" + pad(val4) + ", " + pad(val2) + " occurrences; ";
						line += pad(trial[0])+"/"+pad(trial[1])+"/"+pad(trial[2])+pad(trial[3]);
						System.out.println(line);
					}
				}
			}
		}
	}
	
	public static void marcleanDateSums(int search) {
		Calendar cal = Calendar.getInstance();
		
		cal.set(Calendar.YEAR, 1968);
		cal.set(Calendar.MONTH, 0);
		cal.set(Calendar.DATE, 1);
		
		for (int i=0; i<800; i++) {
			if (cal.get(Calendar.YEAR) >= 1970) break;
			int sum = dateSumFor(cal);
			if (sum == search) {
				dump(cal, sum, true, search);
			}
			if (productFor(sum) == search) {
				dump(cal, sum, false, search);
			}
			cal.add(Calendar.DAY_OF_MONTH, 1);
		}
		
	}
	
	public static void dump(Calendar cal, int val, boolean sum, int search) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
		System.out.println(sdf.format(cal.getTime()) + ", " + expanded(cal) + "=" + valFor(val, sum, search));
	}
	public static String valFor(int val, boolean sum, int total) {
		if (sum) return "" + val;
		
		String line = val + "=";
		String digits = "" + val;
		for (int i=0; i<digits.length(); i++) {
			line += digits.charAt(i);
			if (i<digits.length()-1) line += "*";
		}
		line += "="+total;
		return line;
	}
	public static String expanded(Calendar cal) {
		SimpleDateFormat sdf = new SimpleDateFormat("MMddyy");
		String formatted = sdf.format(cal.getTime());
		String line = "";
		for (int i=0; i<formatted.length(); i++) {
			line += formatted.charAt(i);
			if (i<formatted.length()-1) line +="+";
		}
		return line;
	}
	
	public static int dateSumFor(Calendar cal) {
		int year = cal.get(Calendar.YEAR) - 1900;
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		
		String digits = "" + year;
		digits += month;
		digits += day;
		
		int sum = 0;
		for (int i=0; i<digits.length(); i++) {
			String digit = "" + digits.charAt(i);
			sum += Integer.valueOf(digit);
		}
		return sum;
	}
	
	public static int productFor(int val) {
		String digits = "" + val;
		int product = 1;
		for (int i=0; i<digits.length(); i++) {
			String digit = "" + digits.charAt(i);
			product *= Integer.valueOf(digit);
		}
		return product;
	}
	
	static String randWord() {
		return WordFrequencies.randomWord();
	}
	
	public static String sequenceFor(String word, Map<Character, Integer> counts) {
		String line = "";
		for (int i=0; i<word.length(); i++) {
			line += counts.get(word.charAt(i));
			if (i < word.length() - 1) line += " ";
		}
		return line;
	}
	
	public static void dump(String word1, String word2, String word3, Map<Character, Integer> counts) {
		long score = WordFrequencies.freq(word1) + WordFrequencies.freq(word2) + WordFrequencies.freq(word3);
		
		
		String line = "" + score + ", ";
		line += word1 + " ";
		line += "(";
		for (int i=0; i<word1.length(); i++) {
			line += counts.get(word1.charAt(i));
			if (i < word1.length() - 1) line += " ";
		}
		line += ") ";
		
		line += word2 + " ";
		line += "(";
		for (int i=0; i<word2.length(); i++) {
			line += counts.get(word2.charAt(i));
			if (i < word2.length() - 1) line += " ";
		}
		line += ") ";

		line += word3 + " ";
		line += "(";
		for (int i=0; i<word3.length(); i++) {
			line += counts.get(word3.charAt(i));
			if (i < word3.length() - 1) line += " ";
			if (i == splitPoint) line += "/ ";
		}
		line += ") ";
		
		System.out.println(line);

	}

	public static void factor(long n) {
		System.out.print(n + ": ");
		// for each potential factor i
		for (long i = 2; i <= n / 2; i++) {
			if (n % i == 0)
				System.out.print(i + " ");
		}

		System.out.println();
	}

	public static void testBean() {
		NumerologyDate n = new NumerologyDate("TAURUS", "ZODIAC", "ZODIAC");
		n.dump();
	}
	
	/** search the given string for substrings that form ALA's drivers license number.  
	 * see http://www.zodiackillersite.com/viewtopic.php?p=6567#p6567
	 */
	public static void driversLicenseSums(String str) {
		str = str.replaceAll(" ", "");
		int start=0; // inclusive 
		int end=6; // exclusive 
		StringBuffer sub, out; 
		Map<Character, Integer> countMap;
		while (end <= str.length()) {
			sub = new StringBuffer(str.substring(start,end));
			out = new StringBuffer();
			//System.out.println("testing " + sub);
			countMap = Ciphers.countMap(sub.toString());
			int test = testForDriversLicense(sub, countMap, out);
			if (test == -1) {
				start++;
				end = start + 6;
			} else if (test == 0) {
				end++;
			} else if (test == 1) {
				System.out.println(start+","+ sub+","+out);
				start++;
				end = start + 6;
			} else throw new RuntimeException("Unexpected value: " + test);
		}
	}
	
	public static void driversLicenseCorpus() {
		Generator.loadLetters();
		for (int i=0; i<Generator.tokens.size(); i++) {
			StringBuffer sb = new StringBuffer();
			StringBuffer out = new StringBuffer();
			for (int j=i; j<Generator.tokens.size(); j++) {
				if (sb.length() > 0) sb.append(" ");
				sb.append(Generator.tokens.get(j));
				StringBuffer noSpaces = new StringBuffer(sb.toString().replaceAll(" ", ""));
				int test = testForDriversLicense(noSpaces, Ciphers.countMap(noSpaces.toString()), out);
				if (test == 1) {
					System.out.println(i + ", " + sb + ", " + out);
					break;
				} else if (test == -1) {
					break;
				}
			}
		}
	}
	
	/** returns:
	 * -1 if impossible
	 * 0 if possible by expanding string
	 * 1 if found 
	 */
	public static int testForDriversLicense(StringBuffer sb, Map<Character, Integer> countMap, StringBuffer out) {
		if (sb.length() > 25) return -1;
		StringBuffer sb1 = new StringBuffer();
		StringBuffer sb2 = new StringBuffer();
		int sum = 0; int index = 0;
		for (int i=0; i<sb.length(); i++) {
			char ch = sb.charAt(i);
			int count = countMap.get(ch);
			
			if (sb1.length() > 0) sb1.append(' ');
			sb1.append(ch);
			if (sb2.length() > 0) sb2.append('+');
			sb2.append(count);
			sum += count;
			if (sum == driversLicense[index]) {
				out.append("(").append(sb1).append(") [").append(sb2).append("=").append(driversLicense[index]).append("] ");
				sb1 = new StringBuffer(); sb2 = new StringBuffer(); 
				index++;
				sum = 0;
			}
			if (index == driversLicense.length) {
				if (i==sb.length()-1) return 1;
				return 0;
			}
			//if (sum > driversLicense[index]) return -1;
		}
		out.delete(0,  out.length());
		return 0;
	}
	public static boolean validDate(String date) {
		DateFormat df = new SimpleDateFormat("MM/dd/yy");
		df.setLenient(false);
		try {
			df.parse(date);
			return true;
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return false;
	}
	public static boolean validDate2(String month, String day, String year1, String year2) {
		if (!year1.equals("19")) return false;
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		df.setLenient(false);
		try {
			df.parse(month + "/" + day + "/" + year1 + year2);
			return true;
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return false;
	}
	public static String pad(int n) {
		if (n > 9) return "" + n;
		return "0" + n;
	}
	
	public static class NumerologyDate {
		String wordMonth;
		String sequenceMonth;
		
		String wordDay;
		String sequenceDay;
		
		String wordYear;
		
		List<String[]> sequenceYears;
		
		int month;
		int day;
		List<Integer> years;

		
		public NumerologyDate(String word1, String word2, String word3) {
			
			Map<Character, Integer> counts = new HashMap<Character, Integer>();
			countsFor(new StringBuffer(word1+word2+word3), counts);
			
			wordMonth = word1;
			wordDay = word2;
			wordYear = word3;
			
			month = sumFor(wordMonth, counts);
			day = sumFor(wordDay, counts);
			
			sequenceMonth = sequenceFor(wordMonth, counts);
			sequenceDay = sequenceFor(wordDay, counts);
			
			sequenceYears = new ArrayList<String[]>();
			years = new ArrayList<Integer>();
			
			int val = sumFor(wordYear, counts);
			if (val < 100) {
				sequenceYears.add(new String[] {sequenceFor(wordYear, counts), ""});
				years.add(val);
			}
			for (int i=1; i<wordYear.length()-1; i++) {
				String part1 = wordYear.substring(0, i);
				String part2 = wordYear.substring(i);
				int val1 = sumFor(part1, counts);
				int val2 = sumFor(part2, counts);
				if (val1 > 9) continue;
				if (val2 > 9) continue;
				years.add(val1*10 + val2);
				sequenceYears.add(new String[] {sequenceFor(part1, counts), sequenceFor(part2, counts)});
			}
			
		}
		
		public String date(int n) {
			String line = "";
			line += pad(month) + "/" + pad(day) + "/" + pad(years.get(n));
			return line;
		}
		public String date2(int n) {
			String line = "";
			line += pad(years.get(n)) + "/" + pad(month) + "/" + pad(day);
			return line;
		}
		public void dump() {
			for (int i=0; i<years.size(); i++) {
				String d = date(i);
				if (!validDate(d)) continue;
				System.out.println(date2(i) + " " + d + ": " + wordMonth + " (" + sequenceMonth + ") " + wordDay + " (" + 
						sequenceDay + ") " + wordYear + " (" + sequenceYears.get(i)[0] + " / " + sequenceYears.get(i)[1] + ")");
			}
		}
	}
	public static void main(String[] args) {
		//marcleanFactors();
		//marcleanDOB();
		//marcleanDateSums(33);
		//driversLicenseSums(Ciphers.cipher[2].cipher);
		//driversLicenseSums("HERES ONE OF MINE I ALWAYS INSIST ON THE STANDARD NUMBER OF REPETITIONS LETTERS OR SYMBOLS IN A FREQUENCY EXAMPLE EXAMPLE 2111112 MARCELO");
		//driversLicenseSums("I SUPPOSE YOU ARE RELATED TO ALA SOMEHOW SINCE YOU ARE ALSO HIDING THE DRIVERS LICENSE NUMBER IN YOUR OWN WORDS");
		//driversLicenseCorpus();
		//driversLicenseSums(Ciphers.cipher[1].cipher);
		
		//StringBuffer sb = new StringBuffer("AENz8K8M8");
		//Map<Character, Integer> countMap = Ciphers.countMap(sb.toString());
		//jSystem.out.println(testForDriversLicense(sb, countMap));
		
		//testBean();
		//System.out.println(validDate2("12","3","19","33"));
		marclean408DOB();
	}
	
	
}
