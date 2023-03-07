package com.zodiackillerciphers.tests;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LetterFrequencies {
	public static float[] frequenciesEnglish = new float[] {
		.08167f, .01492f, .02782f, .04253f, .12702f, .02228f, .02015f, .06094f, .06966f, .00153f, .00772f, .04025f, .02406f, .06749f, .07507f, .01929f, .00095f, .05987f, .06327f, .09056f, .02758f, .00978f, .02360f, .00150f, .01974f, .00074f		
	};
	public static String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static Map<Character, Float> frequencyMapEnglish;
	static {
		frequencyMapEnglish = new HashMap<Character, Float>();
		for (int i=0; i<alphabet.length(); i++) 
			frequencyMapEnglish.put(alphabet.charAt(i), frequenciesEnglish[i]);
	}
	
	public static String[] letterPoolEnglish;
	static {
		letterPoolEnglish = letterPool(frequenciesEnglish);
	}
	
	/** freq distribution for fates unwind code */
	public static float[] frequenciesFates = new float[] {
		0.11925383f, 0.08194537f, 0.07594936f, 0.07061959f, 0.06662225f, 0.06262492f, 0.059960026f, 0.0473018f, 0.042638242f, 0.04197202f, 0.03797468f, 0.037308462f, 0.035309795f, 0.03131246f, 0.028647568f, 0.026648901f, 0.023317788f, 0.022651566f, 0.019320453f, 0.017321786f, 0.01598934f, 0.008660893f, 0.008660893f, 0.007328448f, 0.007328448f, 0.0019986676f, 0.0013324451f
	};
	
	// computes probability that a letter repeats n times.  based on frequency distribution of letters in the English language.
	public static float computeIOC(int n) {
		float sum = 0;
		for (int i=0; i<frequenciesEnglish.length; i++) {
			sum += Math.pow(frequenciesEnglish[i], n);
		}
		//System.out.println(sum);
		return sum;
	}

	/* generate random words of length L.  sample letters from a distribution that matches expected English letter frequencies. */
	public static String randomWord(int L, String[] letterPool) {
		String word = "";
		for (int i=0; i<L; i++) {
			word += randomLetter(letterPool);
		}                                                       
		return word;
	}
	public static String randomWord(int L) {
		return randomWord(L, letterPoolEnglish);
	}
	
	public static String randomLetter(String[] letterPool) {
		return letterPool[(int)(Math.random()*letterPool.length)];
	}

	public static String randomLetter() {
		return randomLetter(letterPoolEnglish);
	}
	
	/* make a letter pool that we can select random letters from.  this letter pool has a random distribution
	 * that approximates the expected letter frequency distribution.
	 */ 
	public static String[] letterPool(float[] frequencies) {
		int[] counts = new int[frequencies.length];
		int total = 0;
		for (int i=0; i<frequencies.length; i++) {
			counts[i] = (int)Math.round(1000*frequencies[i]);
			total += counts[i];
		}
		String[] letterPool = new String[total];
		char letter;
		int index = 0;
		for (int i=0; i<counts.length; i++) {
			for (int j=0; j<counts[i]; j++) {
				letter = (char)(97+i);
				letterPool[index] = ""+letter;
				index++;
			}
		}
		return letterPool;
	}
	
	/* test the given string to determine if it has the given distribution of symbols.
	 * see http://zodiackillersite.forummotion.com/t419p405-proposed-solutions-to-the-unsolved-zodiac-codes-including-the-zodiac-340-and-general-caesar-code-analysis
	 * (ak's post about a section of the raw solution having the same distribution as z13)
	 * 
	 *  This method assumes the input array is in ascending order.
	 */
	public static boolean hasDistribution(String string, int[] distribution) {
		Map<Character, Integer> map = new HashMap<Character, Integer>();
		for (int i=0; i<string.length(); i++) {
			char key = string.charAt(i);
			Integer val = map.get(key);
			if (val == null) val = 0;
			val++;
			map.put(key, val);
		}
		if (distribution.length != map.values().size()) return false;
		Object[] dist = map.values().toArray();
		Arrays.sort(dist);
		for (int i=0; i<dist.length; i++) 
			if ((int)dist[i] != distribution[i]) return false;
		return true;
	}
	
	/** http://zodiackillersite.forummotion.com/t419p405-proposed-solutions-to-the-unsolved-zodiac-codes-including-the-zodiac-340-and-general-caesar-code-analysis 
	 * See how often Z13's distribution occurs in random text.
	 */
	public static void distributionTest() {
		int[] dist = new int[] {1, 1, 1, 1, 2, 2, 2, 3};
		int count = 0; int total = 0;
		
		/*
		String cipher = Ciphers.cipher[0].cipher;
		for (int i=0; i<cipher.length()-13+1; i++) {
			String sub = cipher.substring(i,i+13);
			if (hasDistribution(sub, dist)) {
				count++;
				System.out.println(i+","+sub);
			}
		}*/
		
		
		//String test = FileUtil.loadSBFrom("/Users/doranchak/Desktop/tale-nodigits-nospace-nopunct.txt").toString();
		String test = "HERCEANBIGIVETHEMHELLTOOBTSALTESEHLSEILUEHSTHEOLHSSEEANAMEBWEOLLRSESEILLLFMIAPILLSGAEMRNPAODEMAGPCETTOALSTBNEUSHBLLEITHESEFOOLSHALLSEEMTILKLEREPLSAASSDLAUBLNSLOEATPLSDULRAALEITALEKTISOETARFIEATAILLLLPLAESSOLHIAPLTNMRAHPHNEAEAKLBALLLSLSVEESEAECBUEADLILWLLSTOENLEITHERTLEAEATLPASLIHELLHSALSIOSHTATHEIPGMSTALLSAOLEDACITHHEGSLEOMAISNL";
		for (int i=0; i<test.length()-13+1; i++) {
			String sub = test.substring(i,i+13);
			if (hasDistribution(sub, dist)) {
				count++;
				System.out.println(i+","+sub);
			}
			total++;
		}
		System.out.println(count+","+total+","+(100*((float)count)/total));
	}
	
	/** calculate distance between the two frequency maps */
	public static float distance(Map<Character, Float> map1, Map<Character, Float> map2) {
		float distance = 0;
		for (Character c : map1.keySet()) {
			Float val1 = map1.get(c);
			Float val2 = map2.get(c);
			if (val2 == null) val2 = 0f;
			distance += Math.pow(val1-val2, 2);
		}
		for (Character c : map2.keySet()) {
			if (map1.containsKey(c)) continue;
			Float val2 = map2.get(c);
			distance += Math.pow(val2, 2);
		}
		return (float) Math.sqrt(distance);
	}
	
	/** compute letter frequencies of the given string */
	public static Map<Character, Float> frequenciesFor(String str) {
		Map<Character, Float> map = new HashMap<Character, Float>();
		for (int i=0; i<alphabet.length(); i++) {
			map.put(alphabet.charAt(i), 0f);
		}
		int total = 0;
		for (int i=0; i<str.length(); i++) {
			char c = str.charAt(i);
			Float val = map.get(c);
			if (val == null) continue; // ignore non-alpha
			val++;
			map.put(c, val);
			total++;
		}
		for (Character c : map.keySet()) {
			Float val = map.get(c);
			val /= total;
			map.put(c, val);
		}
		return map;
	}
	
	public static void testEntropy() {
		double sum = 0;
		for (float freq : frequenciesEnglish) {
			double val = freq * Math.log((1/freq)) / Math.log(2);
			sum += val;
		}
		System.out.println(sum);
	}
	
	/** compute sum of letter frequencies for the given text. */
	public static float frequencySum(String text) {
		float sum = 0;
		text = text.toUpperCase();
		for (int i = 0; i < text.length(); i++) {
			char ch = text.charAt(i);
			if (ch < 65 || ch > 90)
				throw new IllegalArgumentException("Bad character [" + ch + "] in string [" + text + "]");
			sum += frequenciesEnglish[ch - 65];
		}
		return sum;
	}	
	
	public static void main(String[] args) {
//		testEntropy();
		
		//System.out.println(computeIOC(Integer.valueOf(args[0])));
		//String[] letterPool = letterPool(frequenciesFates);
		
		//for (int i=0; i<30; i++) {                        
		//	String word = randomWord(10,letterPool);
		//	System.out.println(word);
		//	//System.out.println("http://wordsmith.org/anagram/anagram.cgi?anagram="+word+"&language=english&t=1000&d=&include=&exclude=&n=5&m=&source=adv&a=n&l=y&q=n&k=1");
		//}
		
		//System.out.println(hasDistribution("AEN+8K8M8^NAM", new int[] {1, 1, 1, 1, 2, 2, 2, 3}));
		//distributionTest();
//		for (int i=0; i<500; i++)
//			System.out.println(randomWord(18));
		
//		for (int i=1; i<5; i++) System.out.println(computeIOC(i));
		System.out.println(frequencySum("cadet"));
		System.out.println(frequencySum("bings"));
		System.out.println(frequencySum("flory"));
		System.out.println(frequencySum("whump"));
		
	}
}
