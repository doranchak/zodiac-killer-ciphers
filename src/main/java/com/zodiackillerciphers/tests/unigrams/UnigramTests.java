package com.zodiackillerciphers.tests.unigrams;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.ciphers.crimo.CrimoCipher;
import com.zodiackillerciphers.io.FileUtil;
import com.zodiackillerciphers.lucene.Stats;
import com.zodiackillerciphers.stats.StatsWrapper;
import com.zodiackillerciphers.transform.CipherTransformations;

public class UnigramTests {
	
	/** for each position of the cipher, measure length of longest non-repeating string. 
	 * return results as a histogram */
	public static Map<Integer, Integer> countNonRepeatingStrings(String cipher) {
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		for (int i=0; i<cipher.length(); i++) {
			int length = 0;
			Set<Character> seen = new HashSet<Character>();
			for (int k=i; k<cipher.length(); k++) {
				char ch = cipher.charAt(k);
				if (seen.contains(ch)) break;
				seen.add(ch);
				length++;
			}
			Integer val = map.get(length);
			if (val == null) val = 0;
			val++;
			map.put(length, val);
			
		}
		return map;
	}
	
	/** shuffle test for nonrepeating string counts */
	public static void shuffleNonRepeatingStrings(String cipher, int shuffles) {
		System.out.println(cipher);
		Date d1 = new Date();
		int alphabetSize = Ciphers.alphabet(cipher).length();
		Map<Integer, Integer> actual = countNonRepeatingStrings(cipher);
		Map<Integer, StatsWrapper> stats = new HashMap<Integer, StatsWrapper>();
		for (int len=1; len<=alphabetSize; len++) {
			StatsWrapper sw = new StatsWrapper();
			Integer count = actual.get(len);
			if (count == null) count = 0;
			sw.actual = count;
			sw.name = "Count of nonrepeating strings of length " + len;
			stats.put(len, sw);
		}
		for (int i=0; i<shuffles; i++) {
			cipher = CipherTransformations.shuffle(cipher);
			Map<Integer, Integer> counts = countNonRepeatingStrings(cipher);
			for (int len=1; len<=alphabetSize; len++) {
				Integer count = counts.get(len);
				if (count == null) count = 0;
				stats.get(len).addValue(count);
			}
		}
		for (StatsWrapper sw : stats.values()) {
			sw.output();
		}
		Date d2 = new Date();
		System.out.println(d2.getTime()-d1.getTime());
	}
	
	/** return longest sequence that contains zero repeated unigrams */
	public static String longestNonRepeating(String cipher) {
		String max = "";
		Set<Character> seen = null; 
		for (int i=0; i<cipher.length(); i++) {
			seen = new HashSet<Character>();
			String current = "";
			for (int j=i; j<cipher.length(); j++) {
				char c = cipher.charAt(j);
				if (seen.contains(c)) break;
				seen.add(c);
				current += c;
			}
			if (current.length() > max.length()) {
				max = current;
			}
		}
		return max;
	}
	/** return sum or average length of all sequences that contain zero repeated unigrams */
	public static double sumNonRepeating(String cipher, boolean average) {
		double sum = 0;
		Set<Character> seen = null; 
		for (int i=0; i<cipher.length(); i++) {
			seen = new HashSet<Character>();
			String current = "";
			for (int j=i; j<cipher.length(); j++) {
				char c = cipher.charAt(j);
				if (seen.contains(c)) break;
				seen.add(c);
				current += c;
			}
			sum += current.length();
		}
		if (average) sum /= cipher.length();
		return sum;
	}
	
	public static void testShuffleLongest(String cipher, int shuffles) {
		StatsWrapper stats = new StatsWrapper();
		stats.name = "Length of longest sequence containing zero repeated unigrams";
		stats.actual = longestNonRepeating(cipher).length();
		stats.histogram = true;
		for (int i=0; i<shuffles; i++) {
			cipher = CipherTransformations.shuffle(cipher);
			stats.addValue(longestNonRepeating(cipher).length());
		}
		stats.output();
	}
	public static void testShuffleSum(String cipher, int shuffles) {
		StatsWrapper stats = new StatsWrapper();
		stats.name = "Sum of lengths of sequences containing zero repeated unigrams";
		stats.actual = sumNonRepeating(cipher, false);
		stats.histogram = true;
		for (int i=0; i<shuffles; i++) {
			cipher = CipherTransformations.shuffle(cipher);
			stats.addValue(sumNonRepeating(cipher, false));
		}
		stats.output();
	}
	public static void testShuffleAverage(String cipher, int shuffles) {
		StatsWrapper stats = new StatsWrapper();
		stats.name = "Average length of sequences containing zero repeated unigrams";
		stats.actual = sumNonRepeating(cipher, true);
		stats.histogram = true;
		for (int i=0; i<shuffles; i++) {
			cipher = CipherTransformations.shuffle(cipher);
			stats.addValue(sumNonRepeating(cipher, true));
		}
		stats.output();
	}
	
	public static void shuffleRowOrColumnCoverage(String cipher, int width, int shuffles) {
		System.out.println("Cipher: " + cipher);
		String alphabet = Ciphers.alphabet(cipher);
		String original = cipher;
		for (boolean column : new boolean[] {false, true}) {
			for (int i=0; i<alphabet.length(); i++) {
				char ch = alphabet.charAt(i);
				
				StatsWrapper stats = new StatsWrapper();
				stats.name = "Number of " + (column ? "columns" : "rows") + " that contain symbol " + ch;
				stats.actual = rowOrColumnCoverage(original, ch, width, column);

				for (int k=0; k<shuffles; k++) {
					cipher = CipherTransformations.shuffle(cipher);
					stats.addValue(rowOrColumnCoverage(cipher, ch, width, column));
				}
				stats.output();
			}
			
		}

	}

	public static void shuffleRowOrColumnCoverageSumOrAverage(String cipher, int width, boolean average, int shuffles) {
		System.out.println("Cipher: " + cipher);
		String original = cipher;
		for (boolean column : new boolean[] {false, true}) {
			
			StatsWrapper stats = new StatsWrapper();
			stats.name = (average ? "Average" : "Total") + " " + (column ? "column" : "row") + "-wise symbol coverage";
			stats.actual = rowOrColumnCoverageSumOrAvg(original, width, column, average);

				for (int k=0; k<shuffles; k++) {
					cipher = CipherTransformations.shuffle(cipher);
					stats.addValue(rowOrColumnCoverageSumOrAvg(cipher, width, column, average));
				}
				stats.output();
		}
	}
	
	static void testRowOrColumnCoverageSumOrAvg(String cipher, int width, boolean column, boolean average) {
		System.out.println(rowOrColumnCoverageSumOrAvg(cipher, width, column, average));
	}
	
	public static void testRowOrColumnCoverage(String cipher, int width) {
		String alphabet = Ciphers.alphabet(cipher);
		
		for (boolean column : new boolean[] {false, true}) {
			for (int i=0; i<alphabet.length(); i++) {
				char ch = alphabet.charAt(i);
				System.out.println(column + " " + ch + " " + rowOrColumnCoverage(cipher, ch, 17, column));
			}
			
		}
	}
	/** return number of rows (columns) covered by the given symbol */
	public static int rowOrColumnCoverage(String cipher, char symbol, int width, boolean column) {
		int count = 0;
		
		int rowMin = 0;
		int rowMax = cipher.length()/width;
		int colMin = 0;
		int colMax = width;
		
		Set<Integer> set = new HashSet<Integer>(); 
		
		for (int row = rowMin; row < rowMax; row++) {
			for (int col = colMin; col < colMax; col++) {
				int pos = row*width + col;
				if (cipher.charAt(pos) == symbol) {
					if (column) set.add(col);
					else set.add(row);
				}
			}
		}
		
		return set.size();
	}
	/** return sum or average of number of rows (columns) covered by each symbol */
	public static float rowOrColumnCoverageSumOrAvg(String cipher, int width, boolean column, boolean average) {
		Map<Character, Integer> counts = Ciphers.countMap(cipher);
		float total = 0;
		for (Character c : counts.keySet()) {
			int cover = rowOrColumnCoverage(cipher, c, width, column);
			//System.out.println(c + " " + cover);
			total += cover;
		}
		if (average) total /= counts.size();
		return total;
	}
	
	/** shuffle tests to identify linear subsections of the cipher text with too many or too few repeating unigrams */
	
	/** shuffle tests to identify linear subsections of the cipher text with anomalous IOC */
	public static void shuffleLinearIOC(String cipher, int shuffles) {
		String original = cipher;
		long count = 0;
		for (int a=0; a<cipher.length()-1; a++) {
			for (int b=a+1; b<cipher.length(); b++) {
				if (b-a < 2) continue;
				//if (b-a > 40) continue;
				if (a != 43 || b != 87) continue;
				count++;
				String sub = original.substring(a,  b);
				System.out.println(sub);
				StatsWrapper stats = new StatsWrapper();
				stats.actual = Stats.ioc(sub);
				stats.name = "IOC of substring from " + a + " to " + b;
				for (int i=0; i<shuffles; i++) {
					cipher = CipherTransformations.shuffle(cipher);
					stats.addValue(Stats.ioc(cipher.substring(a, b)));
				}
				stats.output();
			}
		}
		System.out.println(count);
	}
	
	public static void processLinearIOCResults(String cipher, int width, String file) {
		
		boolean is408 = cipher.equals(Ciphers.Z408);
		
		/** min sigma observed for each position of cipher */
		Map<Integer, Double> sigmaMin = new HashMap<Integer, Double>(); 
		/** max sigma observed for each position of cipher */
		Map<Integer, Double> sigmaMax = new HashMap<Integer, Double>(); 

		double minmin = Double.POSITIVE_INFINITY;
		double minmax = Double.NEGATIVE_INFINITY;
		double maxmax = Double.NEGATIVE_INFINITY;
		double maxmin = Double.POSITIVE_INFINITY;
		
		List<String> lines = FileUtil.loadFrom(file);
		for (String line : lines) {
			String[] split = line.split("	");
			int start = Integer.valueOf(split[0]);
			int end = Integer.valueOf(split[1]);
			double sigma = Double.valueOf(split[8]);
			//if (start == 0 && end == 339) continue;
			if ((end - start) > cipher.length()/4) continue;
			for (int i=start; i<end; i++) {
				Double min = sigmaMin.get(i);
				Double max = sigmaMax.get(i);
				if (min == null) min = sigma;
				if (max == null) max = sigma;
				min = Math.min(min,  sigma);
				max = Math.max(max,  sigma);
				sigmaMin.put(i,  min);
				sigmaMax.put(i,  max);
				minmin = Math.min(minmin,  min);
				maxmax = Math.max(maxmax, max);
			}
		}
		
		for (int key : sigmaMin.keySet()) {
			minmax = Math.max(minmax,  sigmaMin.get(key));
		}
		for (int key : sigmaMax.keySet()) {
			maxmin = Math.min(maxmin,  sigmaMax.get(key));
		}
		
//		System.out.println(sigmaMin);
//		System.out.println(sigmaMax);
//		System.out.println(minmin);
//		System.out.println(minmax);
		
		String largo = is408 ? Ciphers.toLargoZ408(cipher) : Ciphers.toLargoZ340(cipher);
		String html = "<table>";
		for (int row = 0; row < cipher.length() / width; row++) {
			html += "<tr>";
			for (int col = 0; col < width; col++) {
				int pos = row*width + col;
				Double sigma = sigmaMin.get(pos);
				if (sigma == null) sigma = 0d;
				double norm = (sigma-minmax)/(minmin-minmax);
				double color = 255*norm;
				int rgb = (int) color;
				String fontColor = "";
				if (rgb < 40) {
					fontColor="color: #444;";
				}
				html += "<td title=\"" + sigma + "\" style=\"background-color: rgb(" + rgb + ", " + rgb + ", " + rgb + "); " + fontColor + "\">" + largo.charAt(pos) + "</td>"; 
			}
			html += "</tr>" + System.getProperty("line.separator");
		}
		html += "</table><br><br><br>";
		
		System.out.println(html);
		
		html = "<table>";
		for (int row = 0; row < cipher.length() / width; row++) {
			html += "<tr>";
			for (int col = 0; col < width; col++) {
				int pos = row*width + col;
				Double sigma = sigmaMax.get(pos);
				if (sigma == null) sigma = 0d;
				double norm = (sigma-maxmin)/(maxmax-maxmin);
				double color = 255*norm;
				int rgb = (int) color;
				String fontColor = "";
				if (rgb < 40) {
					fontColor="color: #444;";
				}
				html += "<td title=\"" + sigma + "\" style=\"background-color: rgb(" + rgb + ", " + rgb + ", " + rgb + "); " + fontColor + "\">" + largo.charAt(pos) + "</td>"; 
			}
			html += "</tr>" + System.getProperty("line.separator");
		}
		html += "</table>";
		
		System.out.println(html);
		
	}
	
	/** count total unique symbols in text between a and b (inclusive).
	 * if map is given, weigh each symbol by frequency.
	 */
	public static int uniqueSymbolsIn(String ciphertext, int a, int b, Map<Character, Integer> map) {
		int result = 0;
		Set<Character> seen = new HashSet<Character>();
		for (int i=a; i<=b; i++) {
			seen.add(ciphertext.charAt(i));
		}
		if (map == null) return seen.size();
		for (Character ch : seen) {
			result += map.get(ch);
		}
		return result;
	}
	
	public static void shuffleUniqueSymbolsIn(String ciphertext, boolean weigh, int shuffles) {
		String original = ciphertext;
		Map<Character, Integer> map = null;
		if (weigh) map = Ciphers.countMap(original);
		for (int a=0; a<ciphertext.length()-1; a++) {
			for (int b=a+2; b<ciphertext.length(); b++) {
				StatsWrapper stats = new StatsWrapper();
				stats.name = "Unique symbols in [" + a + "," + b + "]";
				stats.actual = uniqueSymbolsIn(original, a, b, map);
				for (int i=0; i<shuffles; i++) {
					ciphertext = CipherTransformations.shuffle(ciphertext);
					stats.addValue(uniqueSymbolsIn(ciphertext, a, b, map));
				}
				stats.output();
			}
		}
	}
	
	public static String symbolOrder(String cipher) {
		Set<Character> set = new HashSet<Character>();
		String order = "";
		for (int i=0; i<cipher.length(); i++) {
			char ch = cipher.charAt(i);
			if (set.contains(ch)) continue;
			order += ch;
			set.add(ch);
		}
		return order;
	}
	
	public static void shuffleSymbolOrder(String cipher, int shuffles) {
		String original = cipher;
		String order = symbolOrder(original);
		Map<Character, StatsWrapper> map = new HashMap<Character, StatsWrapper>();
		for (int i=0; i<order.length(); i++) {
			Character key = order.charAt(i);
			StatsWrapper stats = new StatsWrapper();
			stats.name = "Order of symbol " + key;
			stats.actual = i;
			map.put(key, stats);
		}
		for (int i=0; i<shuffles; i++) {
			cipher = CipherTransformations.shuffle(cipher);
			order = symbolOrder(cipher);
			for (int k=0; k<order.length(); k++) {
				Character key = order.charAt(k);
				StatsWrapper stats = map.get(key);
				stats.addValue(k);
			}
		}
		for (Character key : map.keySet()) map.get(key).output();
	}
	
	/** inspired by http://zodiackillersite.com/viewtopic.php?p=56330#p56330
	 * 
	 * brute force search of all selection of rows and counts of symbols that only occur on those rows.  
	 */
	public static void exclusiveSymbols(String cipher, int width, int shuffles) {
		System.out.println(ExclusiveSymbolsContext.heading());
		Map<Character, Integer> counts = Ciphers.countMap(cipher); 
		for (int n=1; n<=cipher.length()/width; n++) {
			ExclusiveSymbolsContext context = new ExclusiveSymbolsContext(cipher, width, n);
			
			/** perform randomizations so we can determine statistical significance */
			String shuffle = cipher;
			int[] rowsForShuffle = new int[n];
			for (int i=0; i<n; i++) rowsForShuffle[i] = i;
			StatsWrapper statsS = new StatsWrapper();
			StatsWrapper statsP = new StatsWrapper();
			statsS.actual =  exclusiveSymbolsIn(cipher, width, rowsForShuffle).length();
			statsP.actual =  exclusiveSymbolsIn(cipher, width, rowsForShuffle, counts);
			for (int i=0; i<shuffles; i++) {
				shuffle = CipherTransformations.shuffle(shuffle);
				statsS.addValue(exclusiveSymbolsIn(shuffle, width, rowsForShuffle).length());
				statsP.addValue(exclusiveSymbolsIn(shuffle, width, rowsForShuffle, counts));
			}
			context.statsSymbols = statsS; 
			context.statsPositions = statsP; 

			/** perform brute force search for all selections of n rows */
			int k = 0;
			exclusiveSymbols(context, k);
		}
	}
	public static void exclusiveSymbols(ExclusiveSymbolsContext context, int k) {
		if (k>=context.rows.length) {
			context.processResults();
			return;
		}
		int start;
		if (k==0) start = 0;
		else start = context.rows[k-1] + 1;
		int end = context.rows()-context.n+k;
		for (int i=start; i<=end; i++) {
			context.rows[k] = i;
			exclusiveSymbols(context, k+1);
		}
		
	}
	
	
	
	/** return symbols that appear ONLY in the given rows */
	public static String exclusiveSymbolsIn(String cipher, int width, int[] rows) {
		Set<Integer> rowSet = new HashSet<Integer>();
		for (int row : rows) rowSet.add(row); 
		Set<Character> insideRows = new HashSet<Character>(); 
		Set<Character> outsideRows = new HashSet<Character>();
		for (int i=0; i<cipher.length(); i++) {
			int row = i/width;
			char ch = cipher.charAt(i);
			if (rowSet.contains(row)) insideRows.add(ch);
			else outsideRows.add(ch);
		}
		String result = "";
		for (Character ch : insideRows) if (!outsideRows.contains(ch)) result += ch;
		return result;
	}
	/** return count of all positions of symbols that appear ONLY in the given rows */
	public static int exclusiveSymbolsIn(String cipher, int width, int[] rows, Map<Character, Integer> countMap) {
		String symbols = exclusiveSymbolsIn(cipher, width, rows);
		int result = 0;
		for (int i=0; i<symbols.length(); i++) {
			char ch = symbols.charAt(i);
			result += countMap.get(ch);
		}
		return result;
	}
	
	/**
	 * if countMap is not null, then measure count of all positions occupied by
	 * exclusive symbols, instead of just the count of symbols.
	 */
	public static void shuffleExclusiveSymbolsIn(String cipher, int width, int[] rows, int shuffles,
			Map<Character, Integer> countMap) {
		StatsWrapper stats = new StatsWrapper();
		stats.name = "exclusive symbols in rows " + Arrays.toString(rows);
		stats.actual = countMap == null ? exclusiveSymbolsIn(cipher, width, rows).length()
				: exclusiveSymbolsIn(cipher, width, rows, countMap);
		for (int i = 0; i < shuffles; i++) {
			cipher = CipherTransformations.shuffle(cipher);
			stats.addValue(countMap == null ? exclusiveSymbolsIn(cipher, width, rows).length()
					: exclusiveSymbolsIn(cipher, width, rows, countMap));
		}
		stats.output();
	}	
	
	
	/** calculate average distance between occurrences of the given symbol */
	public static double averageSymbolDistance(String str, String symbol) {
		double average = 0;
		int count = 0;
		int index = str.indexOf(symbol);
		int indexPrev = index;
		while (index > -1) {
			if (index+1 == str.length()) break;
			index = str.indexOf(symbol, index+1);
			if (index == -1) break;
			int diff = index-indexPrev;
			//System.out.println(indexPrev + " " + index + " " + diff);
			average += diff;
			count++;
			indexPrev = index;
		}
		average /= count;
		return average;
	}
	
	public static void testSymbolDistance() {
		for (int i=0; i<10; i++) {
			System.out.println("RESULT FOR " + i + ": " + averageSymbolDistance(CrimoCipher.cipherCrimo9, "" + i));
		}
	}
	
	public static void main(String[] args) {
//		System.out.println(countNonRepeatingStrings(Ciphers.Z408));
//		shuffleNonRepeatingStrings(Ciphers.Z408, 100000000);
		//for (int i=0; i<17; i++) System.out.println(rowOrColumnCoverage(Ciphers.Z340, '+', 17, false, i));
		//testRowOrColumnCoverage(Ciphers.Z340, 17);
//		shuffleRowOrColumnCoverage(Ciphers.Z408, 17, 1000000);
		//shuffleRowOrColumnCoverage(Ciphers.Z340, 17, 1000000);
		//shuffleRowOrColumnCoverage(CipherTransformations.shuffle(Ciphers.Z340), 17, 1000000);
		//shuffleLinearIOC(Ciphers.Z408, 10000);
		//System.out.println(uniqueSymbolsIn(Ciphers.Z408, 0, 339, null));
		//shuffleUniqueSymbolsIn(Ciphers.Z340, true, 10000);
//		System.out.println(symbolOrder(Ciphers.Z408));
//		System.out.println(symbolOrder(Ciphers.Z340));
		//shuffleSymbolOrder(Ciphers.Z340, 1000000);
//		System.out
//				.println(exclusiveSymbolsIn(Ciphers.Z340, 17, new int[] { 0, 1, 2, 3, 4, 5, 19, 18, 17, 16, 15, 14 }));
//		shuffleExclusiveSymbolsIn(Ciphers.Z340, 17, new int[] { 0, 1, 2, 3, 4, 5, 19, 18, 17, 16, 15, 14 }, 1000000,
//				Ciphers.countMap(Ciphers.Z340));
//		exclusiveSymbols(Ciphers.Z340, 17, 1000000);
		//shuffleUniqueSymbolsIn(Ciphers.Z408, false, 1000);
		//processLinearIOCResults(Ciphers.Z340, 17, "/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/tests/unigrams/z340-unusual-ioc.txt");
		//processLinearIOCResults(Ciphers.Z408, 17, "/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/tests/unigrams/z408-unusual-ioc.txt");

//		shuffleRowOrColumnCoverageSumOrAverage(Ciphers.Z408, 17, false, 1000000);
//		shuffleRowOrColumnCoverageSumOrAverage(Ciphers.Z340, 17, false, 1000000);
//		shuffleRowOrColumnCoverageSumOrAverage(Ciphers.Z408, 17, true, 1000000);
//		shuffleRowOrColumnCoverageSumOrAverage(Ciphers.Z340, 17, true, 1000000);
		
		//testRowOrColumnCoverageSumOrAvg(Ciphers.Z340, 17, true, false);

		//UnigramTests.testRowOrColumnCoverage(Ciphers.Z340, symbol, width, column, which, shuffles);
//		System.out.println(longestNonRepeating(Ciphers.Z408));
//		System.out.println(longestNonRepeating(Ciphers.Z340));
//		System.out.println(sumNonRepeating(Ciphers.Z408, false));
//		System.out.println(sumNonRepeating(Ciphers.Z340, false));
//		System.out.println(sumNonRepeating(Ciphers.Z408, true));
//		System.out.println(sumNonRepeating(Ciphers.Z340, true));
//		testShuffleSum(Ciphers.Z408, 1000000);
//		testShuffleSum(Ciphers.Z340, 1000000);
		testSymbolDistance();
	}
}
