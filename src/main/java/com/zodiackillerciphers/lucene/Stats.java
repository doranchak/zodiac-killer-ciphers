package com.zodiackillerciphers.lucene;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.zodiackillerciphers.ciphers.Cipher;
import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.homophones.HomophonesNew;
import com.zodiackillerciphers.io.FileUtil;
import com.zodiackillerciphers.stats.StatsWrapper;
import com.zodiackillerciphers.tests.LetterFrequencies;
import com.zodiackillerciphers.tests.jarlve.JarlveMeasurements;
import com.zodiackillerciphers.tests.kasiski.Kasiski;
import com.zodiackillerciphers.transform.CipherTransformations;

public class Stats {
	public static float ENGLISH_IOC = 0.0667f; 
	public static float ENGLISH_ENTROPY = 4.1f; 
	public static float ENGLISH_CHI2 = 0.55f; 

	public static String KAPPA_IC = "KAPPA_IC"; 
	public static String ENTROPY = "ENTROPY";
	public static String CHI2 = "CHI2";
	
	// A list of known expected IC value for zk languages
	public static final double ENGLISH_IC = 1.73;
	public static final double SPANISH_IC = 1.94;
	public static final double FRENCH_IC = 2.02;
	public static final double GERMAN_IC = 2.05;
	public static final double ITALIAN_IC = 1.94;
	
	public static Map<String, Map<String, Double>> langStatsMap = new HashMap<>();
	
	static {
		// hard-code for zk-English
		Map<String, Double> enStats = new HashMap<>();
		enStats.put(KAPPA_IC, new Double(0.067));
		enStats.put(ENTROPY, new Double(4.1));
		enStats.put(CHI2, new Double(0.55));
		langStatsMap.put("EN", enStats);
		
		// hard-code IC for other zk-languages
		Map<String, Double> esStats = new HashMap<>();
		esStats.put(KAPPA_IC, ENGLISH_IC / 26);
		langStatsMap.put("ES", esStats);
		Map<String, Double> frStats = new HashMap<>();
		frStats.put(KAPPA_IC, FRENCH_IC / 26);
		langStatsMap.put("FR", frStats);
		Map<String, Double> deStats = new HashMap<>();
		deStats.put(KAPPA_IC, GERMAN_IC / 26);
		langStatsMap.put("DE", deStats);
		Map<String, Double> itStats = new HashMap<>();
		itStats.put(KAPPA_IC, ITALIAN_IC / 26);
		langStatsMap.put("IT", itStats);
		
		// We will need to calculate the stats for the other four zk-languages when we load their monome data
	}
	
	public static double kappaIocDiff(String[] words, String langID) {
		StringBuffer sb = new StringBuffer();
		for (String word: words) {
			sb.append(word);
		}
		return kappaIocDiff(sb, langID);
	}
	
	public static double kappaIoc(String s) {
		return kappaIoc(new StringBuffer(s));
	}
	
	// calculate Index of Coincidence without normalizing denominator, thus called Kappa IoC
	public static double kappaIoc(StringBuffer sb) {
		double sum = 0;
		/* map symbols to frequencies */
		Map<Character, Long> map = new HashMap<Character, Long>();
		for (int i=0; i<sb.length(); i++) {
			Character key = sb.charAt(i);
			Long val = map.get(key);
			if (val == null) {
				val = 0l;
			}
			val++;
			map.put(key, val);
		}
		//if (map.size() > 26) throw new IllegalArgumentException("Alphabet size [" + map.size() + "] is too large!");
		
		Long val;
		for (Character key : map.keySet()) {
			val = map.get(key);
			sum += val*(val-1);
		}

		return sum / ((double)sb.length() * (sb.length()-1));
	}
	
	public static double kappaIocDiff(StringBuffer sb, String langID) {
		return Math.abs(langStatsMap.get(langID).get(KAPPA_IC) - kappaIoc(sb));
	}
	
	public static double kappaIocDiff(String sb, String langID) {
		return kappaIocDiff(new StringBuffer(sb), langID);
	}
	
	public static double kappaIocDiff(double langKappaIC, StringBuffer sb) {
		return Math.abs(langKappaIC - kappaIoc(sb));
	}

	
	public static Double iocDiff(String[] words) {
		StringBuffer sb = new StringBuffer();
		for (String word: words) sb.append(word);
		return iocDiff(sb);
	}
	public static Double ioc(String s) {
		return ioc(new StringBuffer(s));
	}
	public static Double ioc(StringBuffer sb) {
		long sum = 0;
		/* map symbols to frequencies */
		Map<Character, Long> map = new HashMap<Character, Long>();
		for (int i=0; i<sb.length(); i++) {
			Character key = sb.charAt(i);
			Long val = map.get(key);
			if (val == null) val = 0l;
			val++;
			map.put(key, val);
		}
		//if (map.size() > 26) throw new IllegalArgumentException("Alphabet size [" + map.size() + "] is too large!");
		
		Long val;
		for (Character key : map.keySet()) {
			val = map.get(key);
			sum += val*(val-1);
			//System.out.println(key+","+val);
		}
		//System.out.println(sum+","+sb.length());
		return ((double)sum)/((double)sb.length()*(sb.length()-1));
	}
	
	// ioc based on passed frequencies
	public static Double ioc(Collection<Integer> values) {
		long sum = 0;
		long length = 0;
		for (Integer val : values) {
			sum += val*(val-1);
			length += val;
		}
		return ((double)sum)/((double)length*(length-1));
	}
	
	public static float iocAsFloat(String s) {
		long sum = 0;
		/* map symbols to frequencies */
		Map<Character, Long> map = new HashMap<Character, Long>();
		for (int i=0; i<s.length(); i++) {
			Character key = s.charAt(i);
			Long val = map.get(key);
			if (val == null) val = 0l;
			val++;
			map.put(key, val);
		}
		//if (map.size() > 26) throw new IllegalArgumentException("Alphabet size [" + map.size() + "] is too large!");
		
		Long val;
		for (Character key : map.keySet()) {
			val = map.get(key);
			sum += val*(val-1);
			//System.out.println(key+","+val);
		}
		//System.out.println(sum+","+sb.length());
		return ((float)sum)/((float)s.length()*(s.length()-1));
	}
	
	/** what is the ioc of the given text and alphabet if all letters have a flat distribution? */
	public static Double iocFlat(String s) {
		return iocFlat(new StringBuffer(s));
	}
	public static Double iocFlat(StringBuffer sb) {
		Map<Character, Integer> counts = Ciphers.countMap(sb.toString());

		/** average occurrences per symbol */
		double avg = ((double)sb.length())/counts.keySet().size();
		return counts.keySet().size() * avg * (avg - 1) / sb.length() / (sb.length() - 1);
	}
	
	public static Double iocDiff(StringBuffer sb) {
		return Math.abs(ENGLISH_IOC - ioc(sb));
	}
	public static Double iocDiff(String sb) {
		return iocDiff(new StringBuffer(sb));
	}
	
	/** compute n-gram ioc */
	public static Double iocNgram(String s, int n) {
		
		Map<String, Long> map = new HashMap<String, Long>();
		double sum = 0;
		int N = s.length()-n+1;
		for (int i=0; i<N; i++) {
			String ngram = s.substring(i,i+n);
			Long val = map.get(ngram); 
			if (val == null) val = 0l;
			val++;
			map.put(ngram, val);
		}
		Long val;
		for (String key : map.keySet()) {
			val = map.get(key);
			sum += val*(val-1);
			//System.out.println(key+","+val+","+sum);
		}
		//System.out.println(sum+","+sb.length());
		return ((double)sum)/((double)N*(N-1));
	}
	public static Double iocNgram(StringBuilder s, int n) {
		
		Map<String, Long> map = new HashMap<String, Long>();
		double sum = 0;
		int N = s.length()-n+1;
		for (int i=0; i<N; i++) {
			String ngram = s.substring(i,i+n);
			Long val = map.get(ngram); 
			if (val == null) val = 0l;
			val++;
			map.put(ngram, val);
		}
		Long val;
		for (String key : map.keySet()) {
			val = map.get(key);
			sum += val*(val-1);
			//System.out.println(key+","+val+","+sum);
		}
		//System.out.println(sum+","+sb.length());
		return ((double)sum)/((double)N*(N-1));
	}

	/** return difference between IOCs calculated for top and bottom halves. */
	public static Double iocSpread(String s) {
		int halfway = s.length()/2;
		String s1 = s.substring(0, halfway);
		String s2 = s.substring(halfway);
		double i1 = ioc(s1);
		double i2 = ioc(s2);
		System.out.println(i1 + " " + s1);
		System.out.println(i2 + " " + s2);
		return Math.abs(i1-i2);
	}
	
	/** return ioc of the given column of the given string.  column numbering starts at zero. */
	public static double iocColumn(String input, int width, int column) {
		return iocColumn(input, width, column, false);
	}
	public static double iocColumn(String input, int width, int column, boolean dump) {
		String s = column(input, width, column);
		double ioc = ioc(s);
		if (dump) System.out.println("   - iocColumn width " + width + " col " + column + " ioc " + ioc+ " s " + s);
		return ioc;
	}
	public static String column(String input, int width, int column) {
		int i = column;
		StringBuilder s = new StringBuilder();
		while (i<input.length()) {
			s .append(input.charAt(i));
			i += width;
		}
		return s.toString();
	}
	/** return ioc of the given row.  row numbering starts at zero. */
	public static double iocRow(String input, int width, int row) {
		return iocRow(input, width, row, false);
	}
	public static double iocRow(String input, int width, int row, boolean dump) {
		String s = row(input, width, row);
		double ioc = ioc(s);
		if (dump) System.out.println("   - iocRow width " + width + " row " + row + " ioc " + ioc+ " s " + s);
		return ioc;
	}
	public static String row(String input, int width, int row) {
		StringBuilder s = new StringBuilder();
		for (int i=row*width; i<row*width+width; i++) {
			s.append(input.charAt(i));
		}
		return s.toString();
	}
	
	
	/** BartW's test: compute average IoC and standard deviation by column width */
	public static void iocWidthTest(String input) {
		for (int width=1; width<=input.length()/2; width++) {
			DescriptiveStatistics stats = new DescriptiveStatistics();
			for (int column=0; column<width; column++) {
				stats.addValue(iocColumn(input, width, column, true));
			}
			System.out.println(width + " " + stats.getMean() + " " + stats.getStandardDeviation());
		}
	}

	/** shuffle test of mean IOC */
	public static void iocWidthTestWithShuffles(String input, int numShuffles, int widthStart, int widthEnd) {
		
		for (int width=widthStart; width<=widthEnd; width++) {
			StatsWrapper stats = new StatsWrapper();
			stats.name = "width" + width;
			stats.actual = Kasiski.meanIoc(width, input);

			String shuffled = input;
			for (int i=0; i<numShuffles; i++) {
				shuffled = CipherTransformations.shuffle(shuffled);
				stats.addValue(Kasiski.meanIoc(width, shuffled));
			}
			stats.output();
		}
	}
	
	
	public static Double entropy(String sb) {
		return entropy(new StringBuffer(sb));
	}
	/** TODO: fix sign problem that happens on long texts; see fixes in ioc */
	public static Double entropy(StringBuffer sb) {
		float sum = 0; float pm; // probability mass
		int L = sb.length();
		Map<Character, Integer> map = new HashMap<Character, Integer>();
		for (int i=0; i<L; i++) {
			Character key = sb.charAt(i);
			Integer val = map.get(key);
			if (val == null) val = 0;
			val++;
			map.put(key, val);
		}
		for (Character key : map.keySet()) {
			pm = ((float)map.get(key))/L; // probability of symbol occurring in the ciphertext 
			sum += pm*Math.log(pm)/Math.log(2);
		}
		return -1.0*sum;
		
	}
	
	public static Double entropyDiff(String sb) {
		return entropyDiff(new StringBuffer(sb));
	}
	public static Double entropyDiff(StringBuffer sb) {
		return Math.abs(ENGLISH_ENTROPY - entropy(sb));
	}
	public static double entropyDiff(String sb, String langID) {
		return entropyDiff(new StringBuffer(sb), langID);
	}
	
	public static double entropyDiff(StringBuffer sb, String langID) {
		return Math.abs(langStatsMap.get(langID).get(ENTROPY)- entropy(sb));
	}
	
	public static double entropyDiff(double targetEntropy, StringBuffer sb) {
		return Math.abs(targetEntropy - entropy(sb));
	}

	public static double chi2Diff(String sb, String langID) {
		return chi2Diff(new StringBuffer(sb), langID);
	}
	
	public static double chi2Diff(StringBuffer sb, String langID) {
		return Math.abs(langStatsMap.get(langID).get(CHI2) - chi2(sb));
	}
	
	public static double chi2Diff(double targetChi2, StringBuffer sb) {
		return Math.abs(targetChi2 - chi2(sb));
	}
	

	public static Float chi2(String sb) {
		return chi2(new StringBuffer(sb));
	}
	/** TODO: fix sign problem that happens on long texts; see fixes in ioc */
	public static Float chi2(StringBuffer sb) {
		
		int uniq = 0; float chi2 = 0; float curr;
		int L = sb.length();
		float pm; // probability mass
		
		Map<Character, Integer> map = new HashMap<Character, Integer>();
		for (int i=0; i<L; i++) {
			Character key = sb.charAt(i);
			Integer val = map.get(key);
			if (val == null) {
				uniq++;
				val = 0;
			}
			val++;
			map.put(key, val);
		}
		pm = ((float)L)/uniq; // looks like this is the inverse of multiplicity
		for (Character key : map.keySet()) {
			curr = map.get(key)-pm;
			curr *= curr;
			curr /= pm;
			chi2 += curr;
		}
		return chi2/L;
	}
	public static Float chi2Diff(String sb) {
		return chi2Diff(new StringBuffer(sb));
	}
	public static Float chi2Diff(StringBuffer sb) {
		return Math.abs(ENGLISH_CHI2 - chi2(sb));
	}
	
	
	/** compute chi2 statistic against uniform distribution */
	public static Double chi2Flat(String sb) {
		return chi2Flat(new StringBuffer(sb));
	}
	public static Double chi2Flat(StringBuffer sb) {
		Map<Character, Integer> counts = Ciphers.countMap(sb.toString());
		
		/** expected # of occurrences per symbol for the given cipher length */
		double expected = ((double)sb.length())/counts.keySet().size();
		//System.out.println("expected " + expected);
		double chi2 = 0;
		for (Character c : counts.keySet()) {
			double count = counts.get(c);
			double val = (count-expected)*(count-expected)/expected;
			chi2 += val;
			//System.out.println("val " + val);
			
		}
		return chi2;
		
	}
	
	
	public static void testOnFile(String path) {
		StringBuffer sb = FileUtil.loadSBFrom(path);
		System.out.println(ioc(sb));
	}
	
	/** from the given cipher, derive the alphabet and its letter frequencies.
	 * return the probability that any symbol occurs n times.
	 * (similar to IoC calculation, but each symbol selected is removed from the fixed
	 * sized alphabet)
	 */
	public static double probRepeat(String cipher, int n) {
		if (n < 2) throw new IllegalArgumentException("n must be > 1");
		Map<Character, Integer> counts = Ciphers.countMap(cipher);
		
		double sum = 0;
		int L = cipher.length();
		
		for (Character key : counts.keySet()) {
			Double val = (double) (counts.get(key));
			if (val < n) continue;
			Double mult = 1.0d;
			for (int i=0; i<n; i++) 
				mult *= ((val-i)/L--);
			sum += mult;
		}
		return sum;
		
	}

	/** compute multiplicity for the given string */
	public static float multiplicity(String str) {
		return ((float)Ciphers.alphabet(str).length())/str.length();		
	}
	
	public static void entropyTest() {
//		double a = Math.log(26)/Math.log(2);
//		double sum = 0;
//		for (float freq : LetterFrequencies.frequenciesEnglish) {
//			sum += freq * Math.log(freq)/Math.log(2);
//		}
//		sum = -sum;
//		System.out.println("Entropy: " + sum);
//		System.out.println("Max entropy (based on alphabet size): " + a);
//		System.out.println("Redundancy: " + (a-sum));

		List<String> list = FileUtil.loadFrom("/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/gutenberg/all.txt");
		String all = FileUtil.convert(list);
		
		for (int n=1; n<150; n++) {
			double Hmax; // max possible entropy for this source 
			double H; // entropy for this source
			Map<String, Integer> counts = new HashMap<String, Integer>();
			int total = 0;
			for (int i=0; i<all.length()-n+1; i++) {
				String ngram = all.substring(i, i+n);
				Integer count = counts.get(ngram);
				if (count == null) count = 0;
				count++;
				counts.put(ngram, count);
				total++;
			}
			H = 0;
			double freqSum = 0;
			for (String ngram : counts.keySet()) {
				float freq = counts.get(ngram);
				freq /= total;
				freqSum += freq;
				H += freq * Math.log((1/freq))/Math.log(2);
			}
			//H = H/n;
			Hmax = Math.log(Math.pow(26, n))/Math.log(2);
			System.out.println("======== " + n + "-grams");
			System.out.println("Entropy: " + H);
			System.out.println("Max entropy (based on alphabet size): " + Hmax);
			System.out.println("Relative entropy: " + H/Hmax);
			System.out.println("Redundancy: " + (1-H/Hmax));
			System.out.println("freqSum: " + freqSum);
			
//			if (n==4) {
//				for (String key : counts.keySet()) {
//					System.out.println(counts.get(key) + " " + Math.log(counts.get(key))/Math.log(2) + " " + key);
//				}
//			}
		}
	}

	/** http://languagelog.ldc.upenn.edu/myl/Shannon1950.pdf, p51 */
	public static double shannonNgramEntropy(int n) {
		if (n==0) {
			return Math.log(26)/Math.log(2);
		}
		List<String> list = FileUtil.loadFrom("/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/gutenberg/all.txt");
		String all = FileUtil.convert(list);

		double H; // entropy
		Map<String, Integer> counts1 = new HashMap<String, Integer>(); // (N)gram stats
		Map<String, Integer> counts2 = new HashMap<String, Integer>(); // (N-1)gram stats
		int total1 = 0;
		int total2 = 0;
		for (int i=0; i<all.length()-n+1; i++) {
			String ngram = all.substring(i, i+n);
			Integer count = counts1.get(ngram);
			if (count == null) count = 0;
			count++;
			counts1.put(ngram, count);
			total1++;
		}
		for (int i=0; i<all.length()-n+2; i++) {
			String ngram = all.substring(i, i+n-1);
			Integer count = counts2.get(ngram);
			if (count == null) count = 0;
			count++;
			counts2.put(ngram, count);
			total2++;
		}
//		System.out.println("counts1: " + counts1);
//		System.out.println("counts2: " + counts2);
		System.out.println("total1 " + total1 + " total2 " + total2);
		H = 0;
		double freqSum = 0;
		for (String ngram : counts1.keySet()) {
			float freq = counts1.get(ngram);
			freq /= total1;
			freqSum += freq;
			H += freq * Math.log((freq))/Math.log(2);
		}
		H = -H;
		for (String ngram : counts2.keySet()) {
			float freq = counts2.get(ngram);
			freq /= total2;
			freqSum += freq;
			H += freq * Math.log((freq))/Math.log(2);
		}
		return H;
		
	}
	// calculate non-English zk-languages' entropy and chi-squared
	// Note the mono freq data is a percentage data. We will have to use 100 as the total count. 
	public static void calculateLanguageStats(String langID, Map<String, Double> monoFreqPercentageMap) {
		Map<String, Double> stats = langStatsMap.get(langID);
		
		if (stats == null || langID.equals("EN")) {
			return;
		}
		
		// calculate entropy & chi2
		double entropy = 0f, probability = 0f;
		double chi2 = 0, curr = 0;
		double freqCount = 0;
		double pm = 100.0 / monoFreqPercentageMap.size(); // probability mass
		for (String key : monoFreqPercentageMap.keySet()) {
			freqCount = monoFreqPercentageMap.get(key);
			probability = freqCount / 100.0; // probability
			
			if (probability != 0) {  // avoid Math.log(0) that results in NaN.
				entropy += probability * Math.log(probability) / Math.log(2);
			}
			
			curr = freqCount - pm;
			curr *= curr;
			curr /= pm;
			chi2 += curr;
		}
		entropy = entropy * (-1);
		chi2 = chi2 / 100;
		
		stats.put(ENTROPY, entropy);
		stats.put(CHI2, chi2);
	}
	
	
	public static void testShannonNgramEntropy() {
		for (int n=0; n<100; n++) {
			System.out.println(n + ": " + shannonNgramEntropy(n));
		}
	}
	
	/** http://www.zodiackillersite.com/viewtopic.php?p=79455#p79455 
	 *  generate multiplicity for all substrings of the given cipher.
	 *  
	 *  calculate distance of that and other stats against jarlve's solvable 4-line section of z408
	 *  
	 *  length: 68  (max: 408)
	 *  multiplicity: 0.6617647  (max: 1.0)
	 *  homScore: 752.6822157407399  (max: 2995.3886543142944)
	 */
	public static void testMultiplicity(String cipher, int minLength) {
		double maxHom = 2995.3886543142944;
		double[] d1 = new double[] {
				68.0/408.0,
				0.6617647,
				752.6822157407399 / maxHom
		};
		System.out.println(Arrays.toString(d1));
		
		for (int i=0; i<cipher.length(); i++) {
			for (int j=i+1; j<cipher.length()+1; j++) {
				if (j-i < minLength) continue;
				String sub = cipher.substring(i,j);
				//double pcs2 = HomophonesNew.perfectCycleScoreFor(2, sub, 3, false);
				double homScore = JarlveMeasurements.homScore(sub);
				double mult = multiplicity(sub);
				double[] d2 = new double[] {
						((double)sub.length()) / 408.0,
						mult,
						homScore / maxHom
				};
				double distance = distance(d1, d2);
				if (distance > 0.2) continue;
				System.out.println(distance + " " + mult + " " + homScore + " " + sub.length() + " " + i + " " + j + " " + sub);
			}
		}
	}
	public static void testMultiplicity2(String cipher, int minLength) {
		for (int i=0; i<cipher.length(); i++) {
			for (int j=i+1; j<cipher.length()+1; j++) {
				if (j-i < minLength) continue;
				String sub = cipher.substring(i,j);
				//double pcs2 = HomophonesNew.perfectCycleScoreFor(2, sub, 3, false);
				double homScore = JarlveMeasurements.homScore(sub);
				double mult = multiplicity(sub);
				System.out.println(mult + " " + homScore + " " + sub.length() + " " + i + " " + j + " " + sub);
			}
		}
	}

	/** compute multiplicity of all substrings.  output only when it falls within the given range. */ 
	public static void testMultiplicity3(String cipher, float minMultiplicity, float maxMultiplicity) {
		for (int i=0; i<cipher.length(); i++) {
			for (int j=i+1; j<cipher.length()+1; j++) {
				String sub = cipher.substring(i,j);
				double mult = multiplicity(sub);
				if (mult < minMultiplicity || mult > maxMultiplicity) continue;
				System.out.println(mult + " " + sub.length() + " " + i + " " + j + " " + sub);
			}
		}
	}
	
	public static double distance(double[] d1, double[] d2) {
		double distance = 0;
		for (int i=0; i<d1.length; i++) {
			distance += Math.pow(d1[i]-d2[i], 2);
		}
		return Math.sqrt(distance);
	}
	
	public static void main(String[] args) {
		/*
		testOnFile("/Users/doranchak/Desktop/tale.txt");
		testOnFile("/Users/doranchak/Desktop/tale-nodigits.txt");
		testOnFile("/Users/doranchak/Desktop/tale-nodigits-nopunct.txt");
		testOnFile("/Users/doranchak/Desktop/tale-nodigits-nospace.txt");
		testOnFile("/Users/doranchak/Desktop/tale-nodigits-nospace-nopunct.txt");*/
		//for (int n=2; n<100; n++) 
		//	System.out.println(probRepeat(Ciphers.cipher[0].cipher, n));
		
		/*
		for (Cipher c : Ciphers.cipher) {
			StringBuffer sb = new StringBuffer(c.cipher);
			double d1 = ioc(sb);
			double d2 = iocFlat(sb);
			double diff = (d1-d2)/d2*100;
			System.out.println(ioc(sb) + " " + iocFlat(sb) + " " + diff + " " + c.description);
			System.out.println(chi2Flat(sb));
		}
		
		StringBuffer test = new StringBuffer("wmzfxtdhzfngfwxwnwxjevxdmzoxfkvxdmzowmkwmkfgzzexenfzpjotkebmnelozlfjpbzkofxwvjefxfwfjpfngfwxwnwxeszyzobdhkxewzawvmkokvwzopjoklxppzozewvxdmzowzawvmkokvwzoxwlxppzofpojtvkzfkovxdmzoxewmkwwmzvxdmzokhdmkgzwxfejwfxtdhbwmzkhdmkgzwfmxpwzlxwxfvjtdhzwzhbrntghzl");
		System.out.println(ioc(test) + " " + iocFlat(test) + " " + test);
		
		test = new StringBuffer("vptzmdrttzysubxaykkwcjmgjmgpwreqeoiivppalrujtlrzpchljftupucywvsyiuuwufirtaxagfpaxzxjqnhbfjvqibxzpotciiaxahmevmmagyczpjxvtndyeuknulvvpbrptygzilbkeppyetvmgpxuknulvjhzdtgrgapygzrptymevppaxygkxwlvtiawlrdmipweqbhpqgngioirnxwhfvvawpjkglxamjewbwpvvmafnlojalh");
		System.out.println(ioc(test) + " " + iocFlat(test) + " " + test);

		test = new StringBuffer("abcdefghijklmnopqrstuvwxyzz");
		System.out.println("chi2flat " + chi2Flat(test));*/

//		int n = 2;
//		System.out.println(iocNgram(Ciphers.Z408, n));
//		System.out.println(iocNgram(Ciphers.Z408_SOLUTION, n));
//		System.out.println(iocNgram(Ciphers.Z340, n));
//		System.out.println(iocNgram(Ciphers.Z340T,n));
//		System.out.println(iocNgram(Ciphers.Z340_SOLUTION_UNTRANSPOSED, n));
		//System.out.println(iocDiff("THIS IS THE ZODIAC SPEAKING"));
		
		//System.out.println(iocSpread(Ciphers.cipher[0].cipher));
		
		//System.out.println(ioc("EVT+DKcWH^p2tPpOF52.2+@+F4Vp-tp2|.6N(F4t1E-z^21l+WTFt*Dz"));
		//System.out.println(ioc(Ciphers.cipher[0].cipher.substring(0,170)));
//		System.out.println(ioc("TAITEEEAETEXESLTIEEEEAIISEEEEEEEEEEEDENITEEEETEEEIETEESTEEIEHATSMITAEEIIFTEEEWEIEATEEIIETEEIREAITEEAEANSIEEEEAEEEAEEAIEETEEETNTIEUEETETEEEEETREIESNTEIEN6DETIEEITAENEIEEEEIEEAEEEAIEEEIEEEEEAITEEEANEEEIA6IIITTUECEEEEITIEEEE5EEENEAIEIIHSEETTETUEETESSITSHI4HSHIEEHEETNEDHAEUDIITIEEEESAEINENEEEIIITTSESINEEIIETEEIENESIIIETEEEITIIIEEEIIERTEEEEAMNIIEETEEEIIETUEEEEESEEESHDEREEEEEEINEEESEIIEEETTEIIEEESEEIININETIEEEEIEIAUEIIEEUEAESREIEEEAESSDEEEEI"));
//		System.out.println(ioc(Ciphers.Z340_SOLUTION_TRANSPOSED));
//		System.out.println(ioc(Ciphers.countMap(Ciphers.Z340_SOLUTION_TRANSPOSED).values()));
		System.out.println(entropy("THISISATESTOFENTROPY"));
		System.out.println(entropy("UAUUAUAAUAUAUAAUAUAA"));
//		iocWidthTest(Ciphers.cipher[0].cipher);
//		iocWidthTestWithShuffles(Ciphers.Z340, 10000, 1, 204);
//		System.out.println(chi2("THISISATESTOFTHEEMERGENCYBROADCASTSYSTEMILIKELAMPHELLOBRICK"));
		//iocWidthTestWithShuffles("KEAGRDZFGDCGFNTQOGHQTZQPGORXODKKFBYCXGEDHFPBBBMZNOTPNIBCTFRPGTPNIHOFNMUZPXXGUWWQNCBTYWTZDEZZNHMZGKCMPSCUHZRFWQFRQPIFUTEPWICPGORKEDPRRXTSRZSEEFKBHBZZRYKJCCZWZYEDOZLIMCKECURYCGHFKECAFZGHMCIFNMGIRXGTUIMMZZMCBBZFCURYCGHFIFDQKEADFAUEPMTCYCZSIZPTGIGHLMTFNKOFSHPWZYCAFZYHMZKPADGSMIAOYZTAHJRTIGZYAFGEAGRDZRMQUJYZXZUNEMGIRWXQUECQRKQPKSJZSFKEAIBBIIYBZFEGTDNPEERZAXTZRPCXCJQXZORTRKRKMZKOZYYOQUCHZSTZRMNCWHNPYKIFWKCSBTLZRQPKJTGUZYSFTZLZLCLTHMTRAIXFYZNFJZMJMDAKWTKCCRBDNZREIZLTKORCYEWSQIBHAKEFJZQPFSTLMNGIMUZZGIAOZZPHTGGIEUPGSIMZKIEMTZCMZSZKIAPJYCWGUDEOKGFTKGEJTQOJKPRIYZSXKXFIEMSFRQQIDTPSXTHMTRAIXFYNHQPFSIIIZREDULQIASTLMNGIRWTHCZRQKENJMQUUEEQGCGTHKUBKULZHMWZLTUPXYRZCXUIZIKMPEOXXEOQUCQHCQNHU", 10000, 1, 340);
		//iocWidthTestWithShuffles("bkmpowllgupyqgktjqcnxqpggmjtbyxskeklluorxzbukhumpxxhbrjmoigthvvrntpabrolllkmpentmaprtntjrxhmgugkhkgkkzbbstakgyhapnzplkntnzmvzmvqzvkvdrzalbbuezerallbbkqwtbxxaprukpovtghthtlvtikwjqcnxyigzapacububvchbtqrbrmguflvgohuqqugvbpuesmpzlsisklhvqoduwjnhdbbvkvxrxefackesxnxtkqpkbdwhrwhtfuepsrzhtmazbvvvgfocamkfqasrhngkksqsklsiikldqcrfhsrsxmzryaziajppkukl", 10000, 1, 170);
		//iocWidthTestWithShuffles("B+LR^1.4pK()kp+J6q8IyM(_pYfOB)zV%;+*4G^DyWBS+XKYEzy2NFfLA:pO<c5DIH(dBD^*.4+ZRCIOLTEFHIJf>z2LpKp+<+_++WNBlOU+j)2TRIb(.+IJIWZcWL5kWc&P#>Wd.9BGCWCFT.*BB+k1ONzzU(DK+EAcOp2H2O*cH-+/6M8Iy):jWTRd7GBKBc729OMFN>ZpGt4P_^2SkMGpc9RG;lY(W*V-V+4<PM^#S16I2#BB5%czFzCtU8+;lzIyO+q(+N#/<F/2OtSCRlFbXJZUW95ccp3^7dL&3MUV><@p++lkV+.l--+*#M8Ft<VFlzYWF)Tb-dfRR+K+4", 10000, 1, 170);
		//iocWidthTestWithShuffles("ABCDEFGFHIJKLMNOPQJRSTUPVAFWXYZaJbcdeOWfLghbiaANjFkPjlQHWmnmoIKHOTdCMpqRmYhAoZJrFLFsQKCRCMdrRtVCNQhurpDgAHRRIpQuOvCwpxdvoTCqbCZMFmvmQXchKDQLSPamnZrRHPrihPLHZiMLygbzazWxbYIRsLSwyQdgyIAO0aALDtjJBgWgBSCVobxbizjGXyGbd1DHKHOrftuW1YWgJNymm0kvvTtYzbSNiZvskrXHMH0kfXkONKiMXjFit1lLIAICwpfcFossEvMCMFiONTtRRHIbGszPQzRGsyP0NltSOeaavgBZWelSSHOWBufHrmMivvsZieRdLQacdRTvB", 10000, 1, 170);
//		iocWidthTestWithShuffles(">|+R|ylz2U-d(#p_SBNHMF+ccBG6<+z<G589pypEDk)T.XF9cRlM2+RMp:+RHdW+31NSl/U+JK^+7cB>NWCLz*^yR5VbfqFz^M(pp<z1B:f#Jt++j%ltl+#lk7W6K45+|E^Z#;Oj8UO^StcC(92N*|JRO2-d*Z%VzBP<OC4|5D+2+U*|VGDPZ_O+pEb5TYOF_cd53WWkOYSF^>.F4BpBNXCFp(Y|8OHl.VcBMp7cYGkPO).1ABTWfUVc.b<yzVF++L<L|*/BMZ4(+TFA+.>&+#*TK-(|q5t;&MB6@z24RzKG;C))G-+8BKy4LLDkKHf2+cpL2++RFO-K9|(/2J)d", 10000, 1, 170);
//		iocWidthTestWithShuffles("ABCDEFGHIJKLMNHOLPQDNBRSHTUVWMQSIXAOYZaEbKcdBDeBfHBPghDCBiGBjUEkLXlESmnKoUeIpqUSrHIIXBsqoUfYktIjlSBQiuSvVPwUxmWMEYLLNFVBWOtyz0v12ZeKByoFnJddeHk3YBBZD4UkbyjMyDr2LrOBVm1uwFz0WwxFOcyePjGRKZOBB5KWBZQk4BKQgQBLvJ64FmlBaKfUHTqCyABGBia7HpzOjurnFjJmljUXs5DvmP84Vhm4iFXHopsjSERPDjUBuKh9k3lqSr8DtbdSHxLqdC!XDJ!DKN9ydFFZ4RkL0MABXhNG2NOHHL7xeknrjskjbSex", 10000, 1, 170);
//		testShannonNgramEntropy();
//		System.out.println(iocNgram("CYBERATTACKSSUCHASRANSOMWARECANSTRIKEATANYTIME", 3));
//		String str = "DEARKATHRYNWEARECONSTANTLYONTHESEARCHFORWORKSHOPTOPICSTHATARERELEVANTTOYOUWHATSORTOFCOURSESDOYOUWANTTOSEEWHICHCOULDHELPYOUGROWTHEMOSTASAPRACTITIONERWEVEPARTNEREDWITHDRDAVIDBURNSTOCREATEALISTOFTOPICSWETHINKYOULLLIKEFORFUTUREWORKSHOPDEVELOPMENTPLEASETAKEAMOMENTTOFILLOUTTHISBRIEFSURVEYANDLETUSKNOWWHICHTOPICSANDCOURSESYOUTHINKWILLBETHEMOSTINTERESTINGINFORMATIVEANDMATTERTOYOUITSVERYSHORTYOUSHOULDBEABLETOFINISHINABOUTMINUTESORLESSANDYOUCOULDMAKEABIGDIFFERENCEINWHATCECMETRAININGWEWILLBEOFFERINGINANDBEYONDTHANKYOUANDWEHOPETOSEEYOUINTHENEWYEARWARMLYTHEINSTITUTEFORBETTERHEALTHCUETHECONFETTIWEVEBEENTOGETHERSINCECANYOUBELIEVEITASWECELEBRATETHISMOMENTOUSMILESTONEWEWANTEDTOSENDYOUABIGFATTHANKYOUFORCHOOSINGUSASYOURWEBHOSTYOURETHEREASONWEGETUPANDGOTOWORKINTHEMORNINGWELOVEWATCHINGYOUSUCCEEDLETSSPREADTHELOVESHALLWEKNOWANYONEELSEWHOSAPERFECTFITFORDREAMHOSTSHAREAPERSONALIZEDLINKWITHFRIENDSFAMILYANDTHECATFISHWHOFOLLOWSYOUONTWITTERYOULLEARNUPTOWHENTHEYMAKETHEIRFIRSTPURCHASEONBEHALFOFTHEGROWINGUPINTHEVALLEYFAMILYWEWISHYOUAVERYHAPPYTHANKSGIVINGTHANKYOUFORYOURSUPPORTANDDEDICATIONTOOURPUBLICATIONASWECONTINUETOSTRIVETOPROVIDEYOUWITHARELIABLEENGAGINGANDTRUSTWORTHYFAMILYPUBLICATIONWELOVEGROWINGUPWITHYOUANDYOURFAMILYINTHEVALLEYREMEMBERSMALLBUSINESSSATURDAYISTHISWEEKENDNOVTHSHOPSMALLSHOPLOCALALLHOLIDAYSEASONJOINGUITVBLESSINGBAGSPROGRAMFORCHRISTMASKIDSPROGRAMANDHELPCHILDRENHAVESOMETHINGJUSTFORTHEMTOOPENATCHRISTMASTIMETHEPROGRAMSTARTSTODAYNOVEMBERTHTHRUDECEMBERTHGIFTSARELIMITEDTOJUSTTHISYEARANDWEHOPETHATYOUWOULDCONSIDERSPONSORINGONEORMOREOFTHEKIDSONOURLISTTHISYEARSUPPORTTHISPROGRAMTHISYEARBYTAKINGACHILDSNAMEPURCHASEGIFTSFORTHEMORSIMPLYMAKEADONATIONONTHEWEBSITEANDLETUSKNOWWHOYOUWOULDLIKETOHELPSPONSORCLICKFORMOREINFOFEATUREDEVENTSTHISWEEKENDSACTIVITIESUNIONBANKTRUSTDRUMSTICKDASHTHURSDAYAMDOWNTOWNROANOKETHETHANNUALUNIONDRUMSTICKDASHENJOYTHEMUSICALONGTHEUSATRACKFIELDCERTIFIEDROUTEASTHEUNIONDRUMSTICKDASHPROVIDESAFUNFAMILYFRIENDLYRAINORSHINETHANKSGIVINGMORNINGEVENTTHISYEARTHERESCUEMISSIONWILLPROVIDEMORETHANMEALSTOHUNGRYANDHOMELESSFAMILIESMOVEYOURFEETSOOTHERSCANEATMOREINFORAILYARDDAWGSTHANKSGIVINGGAMETHURSDAYPMBERGLUNDCENTERROANOKEROANOKERAILYARDDAWGSTAKEONTHEFAYETTEVILLEMARKSMENGATESOPENATPMANDPUCKDROPISATPMMOREINFOFROSTYATTHETAUBMANFRIDAYAMPMSATURDAYAMPMTAUBMANMUSEUMOFARTROANOKECELEBRATETHEHOLIDAYSEASONWITHABELOVEDFRIENDFROSTYWILLBEATTHEMUSEUMFROMAMPMBOTHBLACKFRIDAYNOVEMBERRDANDSATURDAYNOVEMBERTHBRINGYOURCAMERAORUSEYOURPHONETOTAKEAPICTURETHATISSURETOMAKEEVENTHEGRINCHSMILEFREEMOREINFOHAVEYOUCHECKEDOUTOURCALENDAROFEVENTSONOURWEBSITEDOYOUHAVEANEVENTGOINGONYOUWANTTOADDITSFREEHEADOVERTOCHECKITOUTNOW";
//		for (int i=0; i<str.length()-20; i++) {
//			System.out.println(iocNgram(str.substring(i, i+20), 2) + " " + str.substring(i,i+20));
//		}
//		String test = "SHEDHAVEWONEASILYAYWITHFIVEMINUTESTOSPAREIFSHEDHAVECROSSEDATNUNSFORDIPASSEDTHERELASTWEEKWITHOUTWETTINGAGIRTHSHELLNOTTHANKYOUYOUNGGENTLEMANWHOEVERYOUARESAIDTHEOLDESTOFTHEPARTYTURNINGTOCONYERSFORYOURGALLANTRYSHELLONLYREMEMBERYOUASHAVINGHELPEDHERTOLOSEAWAGERTHATSTRUECRIEDANOTHERINEVERGOTASMUCHASTHANKYOUFORCATCHINGHERHORSEONEDAYATLYRATHTHOUGH"; 
//		System.out.println(iocDiff(test));
//		System.out.println(entropyDiff(test));
//		System.out.println(chi2Diff(test));
//		System.out.println(iocColumn(Ciphers.Z340, 17, 1, true));
//		System.out.println(column(Ciphers.Z340, 17, 16));
		//testMultiplicity(Ciphers.Z408, 17); // 0.6617647
//		testMultiplicity2(Ciphers.Z340, 17);
//		testMultiplicity3("ABCDEFGHIJKLMNOGPQRSTUVBGJHWQBGLXPYZKNMCaSRObNcIBdeeSAfMghiDTZGjkBLQblCUEHPmOMNOGPQLBJnjMgCEiOoGSpKOqPKrUBdsMtYIHtruvVMIJXDZXwxHyCGnMmCeByYQeeSuBxUJtHYCqMArXGmHCaMJUDJnOBPzQLM0CGmHYdASsrHCBiJGMdPDzSNr1OMuHovCaDmJNvbSzTwxMWJIgPa", 0f, 0.35f);
//		System.out.println(ioc(Ciphers.Z340_SOLUTION_UNTRANSPOSED));
	}

}
