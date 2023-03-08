package com.zodiackillerciphers.tests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.ngrams.NGramsBean;
import com.zodiackillerciphers.ngrams.Periods;
import com.zodiackillerciphers.stats.StatsWrapper;

/** The transposed Z340 plaintext has some repeating ngrams.  How many repeats are preserved if
 * we randomly assign symbols from the known key?  
 */
public class NGramRepeatsTest {
	static String pt = Ciphers.Z340_SOLUTION_TRANSPOSED;
	static String ct = Ciphers.Z340;
	
	/** map plaintext letters to ct homophones */
	static Map<Character, List<Character>> map;
	static {
		map = new HashMap<Character, List<Character>>();
		for (int i=0; i<pt.length(); i++) {
			Character key = pt.charAt(i);
			List<Character> val = map.get(key);
			if (val == null) {
				val = new ArrayList<Character>();
				map.put(key, val);
			}
			Character c = ct.charAt(i);
			if (val.contains(c)) continue;
			val.add(c);
		}
		System.out.println(map);
	}
	
	static Random RANDOM = new Random();
	
	/** make a random encoding of Z340 based on the known key */
	public static String encode() {
		StringBuffer sb = new StringBuffer();
		for (int i=0; i<pt.length(); i++) {
			char p = pt.charAt(i);
			List<Character> list = map.get(p);
			sb.append(list.get(RANDOM.nextInt(list.size())));
		}
		return sb.toString();
	}
	
	/** encode randomly from known homophones, and measure ngrams.  compare to original z340. */
	public static void shuffle(int shuffles) {
		Map<Integer, StatsWrapper> map = new HashMap<Integer, StatsWrapper>();
		for (int n=2; n<5; n++) {
			StatsWrapper stat = new StatsWrapper();
			NGramsBean ng = new NGramsBean(n, ct);
			stat.actual = ng.numRepeats();
			map.put(n, stat);
		}
		for (int n=2; n<5; n++) {
			StatsWrapper stat = map.get(n);
			for (int i=0; i<shuffles; i++) {
				NGramsBean ng = new NGramsBean(n, encode());
				stat.addValue(ng.numRepeats());
			}
		}
		System.out.println(StatsWrapper.header());
		for (int n=2; n<5; n++) {
			StatsWrapper stat = map.get(n);
			System.out.println("N = " + n);
			stat.output();
		}
	}

	/** period 2 version (to expose repeating patterns like the ones around the box corners) */
	public static void shufflePeriod2(int shuffles) {
		Map<Integer, StatsWrapper> map = new HashMap<Integer, StatsWrapper>();
		for (int n=2; n<5; n++) {
			StatsWrapper stat = new StatsWrapper();
			NGramsBean ng = new NGramsBean(n, Periods.rewrite3(ct, 2));
			stat.actual = ng.numRepeats();
			map.put(n, stat);
		}
		for (int n=2; n<5; n++) {
			StatsWrapper stat = map.get(n);
			for (int i=0; i<shuffles; i++) {
				NGramsBean ng = new NGramsBean(n, Periods.rewrite3(encode(),2));
				stat.addValue(ng.numRepeats());
			}
		}
		System.out.println(StatsWrapper.header());
		for (int n=2; n<5; n++) {
			StatsWrapper stat = map.get(n);
			System.out.println("N = " + n);
			stat.output();
		}
	}
	
	public static void main(String[] args) {
//		for (int i=0; i<10; i++)
//			System.out.println(encode());
//		shufflePeriod2(1000000);
		shuffle(1000000);
	}

}
