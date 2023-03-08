package com.zodiackillerciphers.ciphers.w168;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import com.zodiackillerciphers.lucene.NGramsCSRA;
import com.zodiackillerciphers.ngrams.NGramsBean;

/** process Sam's enumerations */
public class SamBlakeEnumerations {
	/** process Sam's millions of enumerations and track the top N results for the following measurements:
	 * - zkscore 
	 * - n-gram repeats, n from 2 to 5
	 */
	
	static int MAX_HEAP_SIZE = 10000;
	
	public static void process(String[] files) {
		int counter = 0;
		
		// collection of max heaps 
		Map<String, TreeSet<EnumerationBean>> heaps = new HashMap<String, TreeSet<EnumerationBean>>(); 
		
		for (String path : files) {
			System.out.println("==== PROCESSING " + path);
			BufferedReader input = null;
			try {
				input = new BufferedReader(
				           new InputStreamReader(new FileInputStream(path), "UTF-8"));
				
				String line = null; // not declared within while loop
				while ((line = input.readLine()) != null) {
					String enumeration = line.substring(line.indexOf(", ")+2);
					//System.out.println(enumeration.length() + " " + enumeration);
					
					double zkscore = NGramsCSRA.zkscore(new StringBuffer(enumeration), "EN", true);
					if (zkscore == 0) {
						System.out.println("wtf, zkscore 0 " + enumeration);
						//return;
					}
						
					score("ZK", enumeration, zkscore, heaps, false);
					
					for (int n=2; n<6; n++) {
						NGramsBean ng = new NGramsBean(n, enumeration);
						score(n+"gram", enumeration, ng.numRepeats(), heaps, false);
					}
					
					counter++;
					if (counter % 10000 == 0) {
						System.out.println("Counter: " + counter + "...");
					}
				}
			} catch (FileNotFoundException ex) {
				ex.printStackTrace();
			} catch (IOException ex) {
				ex.printStackTrace();
			} 
			
			try {
				input.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
		}
		
		for (String key : heaps.keySet()) {
			System.out.println("========================= TOP RESULTS FOR KEY " + key);
			for (EnumerationBean bean : heaps.get(key)) {
				System.out.println(bean);
			}
			
		}
		
		System.out.println("Processed " + counter + " enumerations.");
		
	}
	
	static void score(String key, String enumeration, double score, Map<String, TreeSet<EnumerationBean>> heaps, boolean feedback) {
		EnumerationBean bean = new EnumerationBean();
		bean.enumeration = enumeration;
		bean.score = score;
		
		TreeSet<EnumerationBean> heap = heaps.get(key);
		if (heap == null) { 
			heap = new TreeSet<EnumerationBean>();
			heaps.put(key, heap);
		}

		// if heap not full, just add it
		if (heap.size() < MAX_HEAP_SIZE) {
			heap.add(bean);
			if (feedback) System.out.println(key + " Added1 " + bean);
			//dump(heap, key);
		} else {
			// tree already has this score?  then ignore;
			if (heap.contains(bean)) {
				;
			} else {
				// is this score better than the worst score? 
				EnumerationBean worst = heap.first();
				if (bean.score > worst.score) { // it's better
					if (!heap.remove(worst)) throw new RuntimeException("ERROR!  CANNOT REMOVE WORST!"); // and remove the worst score
					if (!heap.add(bean)) {
						//System.out.println(heap);
						throw new RuntimeException("ERROR!  CANNOT ADD SCORE! " + bean); // so add to heap
					}
					if (feedback) System.out.println(key + " Added2 " + bean + " Removed " + worst);
					//dump(heap, key);
				}
			}
		}
		
		
	}
	
	static void dump(TreeSet<EnumerationBean> heap, String key) {
		System.out.println("=== dumping heap key " + key);
		for (EnumerationBean bean : heap)
			System.out.println("   " + bean);
	}

	public static void main(String[] args) {
		NGramsCSRA.init();
		String prefix = "/Users/doranchak/projects/ciphers/W168/fbi_cipher_enumerations_2/";
		
		// length 162
		String[] files = new String[] { prefix + "enum_3by3_wocol1_final.txt", prefix + "enum_3by3_wocol28_final.txt" };
		process(files);
		
		// length 168
		files = new String[] { prefix + "enum_all_global_all_local_final.txt",
				prefix + "enum_all_global_all_local_rltb_rlbt_final.txt",
				prefix + "enum_all_local_all_global_final.txt" };
		process(files);
		
	}
}
