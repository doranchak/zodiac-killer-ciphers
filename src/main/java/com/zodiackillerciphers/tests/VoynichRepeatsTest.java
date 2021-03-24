package com.zodiackillerciphers.tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import com.sun.xml.internal.bind.v2.util.EditDistance;
import com.zodiackillerciphers.io.FileUtil;

/** https://www.voynich.ninja/thread-2841-post-28917.html#pid28917 */
public class VoynichRepeatsTest {
	public static void test(String path, int n) {
		String text = FileUtil.loadSBFrom(path).toString().toUpperCase();
		//System.out.println(text);
		String[] split = text.split(" ");
		
		// 1000-word windows
		for (int w=0; w<split.length-999; w++) {
			Map<String, Integer> map = new HashMap<String, Integer>(); 
			String[] window = Arrays.copyOfRange(split, w, w+1000);
			boolean hasDupes = false;
			for (int i=0; i<window.length-n+1; i++) {
				String[] slice = Arrays.copyOfRange(window, i, i+n);
				String key = Arrays.toString(slice);
				Integer val = map.get(key);
				if (val == null) val = 0;
				val++;
				map.put(key, val);
				if (val > 1) {
					hasDupes = true;
				}
			}
			if (hasDupes) {
				System.out.println("==== WINDOW START " + w + ":");
				for (String key : map.keySet()) {
					Integer val = map.get(key);
					if (val > 1) {
						System.out.println(key + ": " + val);
					}
				}
			}
		}
		
	}
	
	public static void editDistances(String path, int n, int maxDistance) {
		String text = FileUtil.loadSBFrom(path).toString().toUpperCase();
		//System.out.println(text);
		String[] split = text.split(" ");
		
		// 1000-word windows
		for (int w=0; w<split.length-999; w++) {
//			System.out.println("Processing window " + w + " of " + (split.length-999));
			List<String> samples = new ArrayList<String>();
			String[] window = Arrays.copyOfRange(split, w, w+1000);
			for (int i=0; i<window.length-n+1; i++) {
				String[] slice = Arrays.copyOfRange(window, i, i+n);
				String key = Arrays.toString(slice);
				for (String sample : samples) {
					// had to comment this out for compilation to succeed:
					//int distance = EditDistance.editDistance(sample, key);
					int distance = 0;
					if (distance <= maxDistance) {
						System.out.println(distance + " " + w + " " + key + " " + sample);
					}
				}
				samples.add(key);
			}
		}
		
	}
	
	public static void main(String[] args) {
//		test("/Users/doranchak/Downloads/023 Utopia.txt", 3);
		editDistances("/Users/doranchak/Downloads/023 Utopia.txt", 4, 5);
	}
}
