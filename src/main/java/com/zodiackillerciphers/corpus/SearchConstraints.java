package com.zodiackillerciphers.corpus;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.ciphers.Cipher;
import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.constraints.Compute;
import com.zodiackillerciphers.constraints.Info;
import com.zodiackillerciphers.constraints.TopHeap;
import com.zodiackillerciphers.io.FileUtil;
import com.zodiackillerciphers.lucene.NGrams;

public class SearchConstraints {
	
	static int NUM_THREADS = 20;
	/** search corpus using pairwise constraints */
	public static void search(String cipher, String solution, String dirCorpus, String dirTmp, int maxLength, float maxProbability) {
		System.out.println("dirCorpus " + dirCorpus);
		System.out.println("dirTmp " + dirTmp);
		System.out.println("cipher " + cipher);
		System.out.println("solution " + solution);
		System.out.println("maxLength " + maxLength);
		System.out.println("maxLength " + maxLength);
		Map<Integer, List<Info>> map = Compute.constraints(cipher, solution, maxLength, maxProbability);

		Search.makeUnzipDir(dirTmp+"/unzipped");
		
		List<File> files = Reader.list(dirCorpus);
		System.out.println("Number of files to process: " + files.size());
		long length = 0;
		String text;
		
		TopHeap heap = new TopHeap(1000);
		List<SearchConstraintsThread> threads = new ArrayList<SearchConstraintsThread>();
		Long timeStartTotal = new Date().getTime();
		Long timeStart = new Date().getTime();
		Long timeDump = new Date().getTime();
		for (int f=0; f<files.size(); f++) {
			float p = 100*((float)f)/files.size();
			System.out.println(f + " of " + files.size() + " (" + (int) p + "%)");
			File file = files.get(f);
			if (file.getName().toLowerCase().endsWith("_h.zip")) {
				continue; // ignoring HTML zips 
			}
			
			try {
				text = Search.read(file, dirTmp);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
			if (text == null) continue;
			length += text.length();
			
			threads.add(new SearchConstraintsThread(file, text, heap, map, cipher, solution));
			
			if (threads.size() == NUM_THREADS || f == files.size()-1) {
				// ready to kick off threads
				for (int i=0; i<threads.size(); i++) threads.get(i).start(); 
				// wait for them to complete
				for (int i=0; i<threads.size(); i++) {
					try {
						threads.get(i).join();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
				}
				// reset thread queue 
				threads.clear();
				
				Long elapsed = new Date().getTime() - timeStart;
				System.out.println("Current thread batch completed in " + elapsed + " ms.");
				timeStart = new Date().getTime();
				System.out.println("Total elapsed " + (new Date().getTime() - timeStartTotal) + " for total length " + length);
				
				if ((new Date().getTime() - timeDump) > 7200000) { // dump heap every two hours just in case.
					System.out.println("Dump interval reached.");
					heap.dump();
					timeDump = new Date().getTime();
				}
			}
		}
		heap.dump();
		System.out.println("Done processing files.  Total text length: " + length);

	}

	/** return a zkscore of the partially decoded plaintext imposed by the given constraint match */
	public static float score(Info info, String cipher) {
		Map<Character, Character> decoder = new HashMap<Character, Character>();
		for (int i=0; i<info.substring.length(); i++) {
			char chcipher = info.substring.charAt(i);
			char chplain = info.plaintext.charAt(i);
			decoder.put(chcipher, chplain);
		}
		StringBuffer decoded = new StringBuffer();
		for (int i=0; i<cipher.length(); i++) {
			char key = cipher.charAt(i);
			Character val = decoder.get(key);
			if (val == null || (i >= info.index &&  i <= info.index+info.substring.length()-1)) decoded.append(" ");
			else decoded.append(val);
		}
		
		//System.out.println("scoring cipher " + info.substring + " plaintext " + info.plaintext + " decoded " + decoded);
		return NGrams.zkscore(decoded);
		
	}
	
	public static boolean spurious(String substring) {
		if (substring == null) return false;
		Set<Character> set = new HashSet<Character>();
		for (int i=0; i<substring.length(); i++) {
			set.add(substring.charAt(i));
			if (set.size() > 2) return false;
		}
		return set.size() < 3;
	}
	
	/** determine correctness of putative solution */
	public static float match(Info info, String putative, String solution) {
		if (solution == null) return 0f;
		
		int count = 0;
		for (int i=0; i<info.substring.length(); i++) {
			if (putative.charAt(i) == solution.charAt(i+info.index)) count++;
		}
		return ((float)count)/info.substring.length();
	}
	
	
//	public static void search(String dirCorpus, String dirTmp, String cipher, String solution, int maxLength, float maxProbability) {
	public static void main(String[] args) {
		Cipher c = Ciphers.cipher[Integer.valueOf(args[0])];
		search(c.cipher, c.solution == null ? null : c.solution.toUpperCase(), args[1], args[2], Integer.valueOf(args[3]), Float.valueOf(args[4]));
	}
	
}
