package com.zodiackillerciphers.corpus.symposium2019;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.transform.CipherTransformations;

/** counts up groups of repeating fragments, indexed by the relative positions the repeating symbols appear in */
public class FragmentStats {
	
	static boolean[] bools = new boolean[] {false, true};
	
	/** generate all fragment masks of length L.
	 *  since first and last entries have to be true,
	 *  the boolean sequence represents only odd numbers in the range [2^(L-1) ... and 2^L-1]
	 *  so it's easier to just generate via integer arithmetic 
	 **/
	static void masksNAIVE(int L) {
		System.out.println("==== masks for L=" + L);
		boolean[] mask = new boolean[L];
		int[] count = new int[1];
		masksNAIVE(mask, 0, count);
		System.out.println("Count: "+ count[0]);
	}
	static void masksNAIVE(boolean[] mask, int index, int[] count) {
		if (index == 0) {
			mask[index] = true;
			masksNAIVE(mask, index+1, count);
		} else if (index == mask.length - 1) {
			count[0]++;
			mask[index] = true;
			System.out.println(Arrays.toString(mask));
			return;
		} else {
			for (boolean b : bools) {
				mask[index] = b;
				masksNAIVE(mask, index+1, count);
			}
		}
	}
	
	/** get the mask for these two strings.  returns zero if they don't at least match on the first and last positions. */
	public static int maskFor(String s1, String s2) {
		if (s1.length() != s2.length())
			return 0;
		int L = s1.length();
		if (s1.charAt(0) != s2.charAt(0))
			return 0;
		if (s1.charAt(L-1) != s2.charAt(L-1))
			return 0;
		
		int mask = (int) Math.pow(2, L - 1) + 1; // 100....001
		for (int i = 1; i < L - 1; i++) {
			if (s1.charAt(i) == s2.charAt(i)) {
//				System.out.println(i + " " + Math.pow(2, L - 1 - i));
				mask += (int) Math.pow(2, L - 1 - i);
			}
		}
		return mask;
	}
	
	/** initialize a map of masks to counts for the given length */
	public static Map<Integer, Integer> maskMapFor(int L) {
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		int start = (int) Math.pow(2, L - 1) + 1;
		int end = (int) Math.pow(2, L) - 1;
		for (int i = start; i <= end; i+=2) {
			map.put(i, 0);
		}
		return map;
	}

	public static void testMasks() {
//		for (int n=2; n<10; n++) {
//			masksNAIVE(n);
//		}
		int mask = maskFor("ABCDEFAP","ABDDFFEP"); // 11010101
		System.out.println(mask);
		System.out.println(Integer.toBinaryString(mask));
		System.out.println(maskMapFor(2));
		System.out.println(maskMapFor(3));
		System.out.println(maskMapFor(4));
		System.out.println(maskMapFor(5));
	}
	
	public static Map<Integer, Integer> fragmentCountsFor(String input, int L) {
		Map<Integer, Integer> map = maskMapFor(L);
		for (int i=0; i<input.length()-L*2+1; i++) {
			String sub1 = input.substring(i,i+L);
			for (int j=i+L; j<input.length()-L+1; j++) {
				String sub2 = input.substring(j,j+L);
				int key = maskFor(sub1, sub2);
				if (key == 0) continue;
//				System.out.println(i + " "+ j + " " + sub1 + " "+ sub2 + " " + key + " (" + Integer.toBinaryString(key) + ")");
				Integer val = map.get(key);
				val++;
				map.put(key, val);
			}
		}
		return map;
	}
	
	/** return counts as list, in ascending order of the mask */
	public static List<Integer> asList(Map<Integer, Integer> map, int L) {
		List<Integer> list = new ArrayList<Integer>();
		int start = (int) Math.pow(2, L - 1) + 1;
		int end = (int) Math.pow(2, L) - 1;
		for (int i=start; i<=end; i+=2) {
			list.add(map.get(i));
		}
		return list;
	}

	static void dump(Map<Integer, Integer> map) {
		for (Integer key : map.keySet()) {
			if (map.get(key) > 0)
				System.out.println("- " + key + " (" + Integer.toBinaryString(key) + "): " + map.get(key));
		}
	}
	
	public static List<Integer> generateFragmentStatsFor(String cipher, int Lmin, int Lmax) {
		List<Integer> list = new ArrayList<Integer>();
		for (int n=Lmin; n<=Lmax; n++) {
			list.addAll(asList(fragmentCountsFor(cipher, n), n));
		}
		return list;
	}
	
	/** modifies list1 so it has the max corresponding elements from list1 and list2 */ 
	public static void max(List<Integer> list1, List<Integer> list2) {
		if (list1== null || list2 == null || list1.size() != list2.size())
			throw new RuntimeException("INVALID ARGS");
		for (int i=0; i<list1.size(); i++) {
			list1.set(i, Math.max(list1.get(i), list2.get(i)));
		}
	}
	
	public static void test() {
		String cipher = 
		"qR31OQpZovJ24dyqKTp8ryy!Vyd3Ki4Vqp1DFzYuooOoAqSHT4aIkRBvNzFAeU9LJbenAJHeqQ0wfvF2dWMd2MIe!aJMhIeeLmlHHgT266NUkdakM0BFb5KHH0AfGot3ZfZbI0V0V0FLl0wHLEoeUcSVMS0LpbOEmotWOJ5IpMr4u6dACBxZCNvTkzHkFiqnWszNv0bQceT7zYd5x6YBQtzGlVxvpe9xMWbiBd3IzUUrf9feM43POxvt3qH9xSBiYDAJg63yHgn1xSDQSkINjckQ29Yt5zeBJwe!ouIeotpq0yf8VeMeocBd4Oo8ftN0eNAe4LdkCsXMYIvkjeYu";                                                    
//		[1252, 22, 1128, 13, 0, 0, 1035, 17, 1, 0, 0, 0, 0, 0, 946, 15, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 861, 12, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 780]
		System.out.println(fragmentCountsFor(cipher, 7));
	}
	
	public static void main(String[] args) {
//		testMasks();
		
//		for (int n=2; n<20; n++) {
//			System.out.println("================== n = " + n);
////			String cipher = Ciphers.Z408.substring(0,340);
//			String cipher = Ciphers.Z408;
//			dump(fragmentCountsFor(cipher, n));
//			System.out.println(asList(fragmentCountsFor(cipher, n), n));
//			System.out.println(" ----------- shuffled");
//			cipher = CipherTransformations.shuffle(cipher);
//			dump(fragmentCountsFor(cipher, n));
//			System.out.println(asList(fragmentCountsFor(cipher, n), n));
//			
//		}
		System.out.println(generateFragmentStatsFor(Ciphers.Z340, 2, 5));
//		test();
	}
}
