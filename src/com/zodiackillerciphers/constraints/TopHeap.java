package com.zodiackillerciphers.constraints;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.corpus.SearchConstraints;

/** Data structure to track Top N results (based on zkscore) per (position, length) combination from the constraint search results. */
public class TopHeap {
	
	/** each tested combination of (position,length) gets its own Top N list */
	public Map<String, TreeSet<Info>> map;
	
	/** the number of top records to keep per (position,length) */
	public int max = 100;
	
	public TopHeap(int N) {
		map = new HashMap<String, TreeSet<Info>>();
		max = N;
	}
	
	public synchronized void add(Info info) {
		if (info == null) return;
		String key = keyFrom(info);
		TreeSet<Info> val = map.get(key);
		if (val == null) {
			//System.out.println("new treeset for " + key);
			val = new TreeSet<Info>(new Comparator<Info>() {
				public int compare(Info info1, Info info2) {
					int c = (new Float(info1.zkscore)).compareTo(info2.zkscore);
					if (c==0) c = info1.plaintext.compareTo(info2.plaintext);
					//System.out.println("Result " + c + " when comparing " + info1 + " to " + info2);

					return c;
				}
			});
		}
		else {
			/*
			for (Info i : val) {
				if (i.equals(info)) {
					System.out.println("dupe " + i + "; " + info);
					System.out.println("contains? " + val.contains(info));
					return;
				}
			}*/
		}
		//System.out.println("Adding for key " + key + ", set size is " + val.size());
		/*if (val.contains(info)) {
			System.out.println("Ignored dupe! " + info);
			for (Info i : val) {
				System.out.println("Looking at " + i);
				if (i.substring.equals(info.substring)) System.out.println("Dupe of: " + i);
			}
			return; // don't try to add dupes
		}*/
		
		// is there still room in the tree?
		if (val.size() < max) {
			//System.out.println("Added " + info);
			val.add(info);
		}
		else { // no more room in map.  
			Info min = val.first(); // get smallest Info item
			if (info.zkscore > min.zkscore) { // given Info item belongs to the tree because its zkscore is higher than the tree's minimum
				//System.out.println(key + ": " + info.zkscore + " is better than min of " + min.zkscore);
				val.remove(min);
				val.add(info);
			} else {
				//System.out.println(key + ": ignored because " + info.zkscore + " is worse than min of " + min.zkscore);
			}
		}
		map.put(key, val);
	}
	
	public String keyFrom(Info info) {
		return info.substring;
		//return info.index + "," + info.substring.length();
	}
	
	public void dump() {
		for (String key : map.keySet()) {
			System.out.println("key " + key + ", values " + map.get(key).size());
			for (Info info : map.get(key)) {
				System.out.println(" - Info: " + info);
			}
		}
	}
	
	public static void test2() {
		TopHeap heap = new TopHeap(1000);

//	     [java] Added 34, 177, 5+tL)l^R6HI9DR_TYr\de/@XJQAP5M8RUt, THEKNOWNINTERNETINTHENEXTSECTIONSE, 12, 2.2950595E-5, {7 13} {0 28} {7 31} {2 33} , 0.0882353, 626.625, 18.430147
//	     [java] Added 34, 177, 5+tL)l^R6HI9DR_TYr\de/@XJQAP5M8RUt, OUDONTGETBURNEDTHESERVICERECOMMEND, 15, 2.2950595E-5, {7 13} {0 28} {7 31} {2 33} , 0.029411765, 626.625, 18.430147

		Info info1 = new Info("5+tL)l^R6HI9DR_TYr\\de/@XJQAP5M8RUt", 34);
		info1.plaintext = "THEKNOWNINTERNETINTHENEXTSECTIONSE";
		info1.zkscore = SearchConstraints.score(info1, Ciphers.cipher[1].solution.toString());
		heap.add(info1);
		
		Info info2 = new Info("5+tL)l^R6HI9DR_TYr\\de/@XJQAP5M8RUt", 34);
		info2.plaintext = "OUDONTGETBURNEDTHESERVICERECOMMEND";
		info2.zkscore = SearchConstraints.score(info2, Ciphers.cipher[1].solution.toString());
		heap.add(info2);
		
		heap.dump();
		
		if (heap.map.get(heap.keyFrom(info1)).contains(info2)) System.out.println("Contains!");
		
		
	}
	public static void test() {
		TopHeap heap = new TopHeap(1000);
		
		List<Integer[]> keys = new ArrayList<Integer[]>();
		for (int i=0; i<2000; i++) {
			int pos = (int) (Math.random() * 100);
			int len = 5 + (int) (Math.random() * 10);
			keys.add(new Integer[] {pos, len});
		}
		
		for (int i=0; i<10000*2000*10; i++) {
			Integer[] key = keys.get((int)(Math.random()*keys.size()));
			String sub = "";
			for (int j=0; j<key[1]; j++) {
				char ch = (char) ((int)(Math.random() * 26 + 65));
				sub += ch; 
			}
			Info info = new Info(sub, key[0]);
			info.plaintext = sub;
			info.match = (float) Math.random();
			info.probability = (float) Math.random();
			info.zkscore = (float) (100 * Math.random());
			heap.add(info);
			
			if (i % 10000 == 0) System.out.println(i+"...");
		}
		
		heap.dump();
	}
	
	public static void main(String[] args) {
		//test();
		test2();
	}
}
