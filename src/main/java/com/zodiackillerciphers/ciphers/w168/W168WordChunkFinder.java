package com.zodiackillerciphers.ciphers.w168;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.dictionary.WordFrequencies;

/** Dictionary search.
 * Look for words that appear in reverse, split across up to K chunks.
 */
public class W168WordChunkFinder {

	static int MATCH_LIMIT = 10;
	
	static String JARL_Z408_TEST_1 = "CLEUN FPRLD FOUN.HEAE LLIIS TTNG I KILIKEALANI GA THITE KIFL. HSE  TIN  ISSMUSESG O O LMUCES TMAIN LAUN NMEROS A S BEG WNGLOSHE RE  CAT BUT MOMEHI AL OFN IMOPEOKILT DII";
	static String JARL_Z408_TEST_2 = " FUNHOA KILIE ITIU MNE KINSU TING LATSOL WILDR . IOHECAUL   MREESELIKNSTAUNALAE BOCGFHANEL I MUKMISE OSNNG N MPLS AITLLEH.CH FO ORE I SM TEB PEOF LIMET IS  OG GAT DT IS";

	static String cipher = W168WordSearch.W168_1D; // 1D version of W168, reversed 
	//static String cipher = JARL_Z408_TEST_1;
	
	/** map all substrings of contiguous letters (A-Z) to their positions in the cipher text */  
	static Map<String, Set<Integer>> substringIndex;
	static {
		substringIndex = new HashMap<String, Set<Integer>>();
		for (int i=0; i<cipher.length(); i++) {
			StringBuilder sb = new StringBuilder();
			for (int j=i; j<cipher.length(); j++) {
				char ch = cipher.charAt(j);
				if (!W168WordSearch.isLetter(ch)) {
					break;
				}
				sb.append(ch);
				Set<Integer> set = substringIndex.get(sb.toString());
				if (set == null) set = new HashSet<Integer>();
				set.add(i);
				substringIndex.put(sb.toString(), set);
			}
		}
		System.out.println(substringIndex);
	}
	
	/** find all occurrences of the given string crossing exactly k chunks */ 
	public static void search(String str, int k) {
		int[] splits = null;
		if (k > 1) splits = new int[k-1];
		for (int pos = 0; pos < k-1; pos++)
			splits[pos] = pos+1;
		search(str, splits, 0);
	}
	
	// k chunks splits the string into k substrings at k-1 points.
	// if k = 2: [0, c[0]-1], [c[0], str.length()-1]
	// if k = 3: [0, c[0]-1], [c[0], c[1]-1], [c[1], str.length()-1]
	
	public static void search(String str, int[] splits, int index) {
		
		/* special case: no splits */
		if (splits == null) {
			List<String> split = new ArrayList<String>();
			split.add(str);
			List<ChunkMatchBean> matches = match(MATCH_LIMIT, split);
			if (matches != null && !matches.isEmpty()) {
				output(str, 0, split, matches);
			}
			return;
		}
		
		if (index == splits.length) {
			List<String> split = split(str, splits);
			List<ChunkMatchBean> matches = match(MATCH_LIMIT, split);
			if (matches != null && !matches.isEmpty()) {
				//System.out.println(str.length() + "	" + Arrays.toString(splits) + "	" + split(str, splits));
				output(str, splits.length, split, matches);
			}
			return;
		}
		if (splits[index] >= str.length()) return;
		
		int start = 1;
		if (index > 0) start = splits[index-1]+1;
		for (int i=start; i<str.length(); i++) {
			splits[index] = i;
			search(str, splits, index+1);
		}
	}
	
	public static void output(String str, int splitsLength, List<String> split, List<ChunkMatchBean> matches) {
		System.out.println(splitsLength + "	" + str.length() + "	" + score(str) + "	" + str + "	" + split + "	"
				+ matches);
	}
	
	public static long score(String str) {
		return WordFrequencies.percentile(str);
	}
	
	public static Map<String, Set<Integer>> matchOLD(List<String> chunks) {
		Map<String, Set<Integer>> matches = new HashMap<String, Set<Integer>>();
		for (String chunk : chunks) {
			Set<Integer> set = substringIndex.get(chunk);
			if (set == null || set.isEmpty()) return null;
			matches.put(chunk, set);
		}
		return matches;
	}

	/** return a list of all non-overlapping chunks that appear the cipher text */
	public static List<ChunkMatchBean> match(int limit, List<String> chunks) {
		
		List<List<Integer>> matches = new ArrayList<List<Integer>>();
		List<ChunkMatchBean> beans = new ArrayList<ChunkMatchBean>();
		
		for (String chunk : chunks) {
			Set<Integer> set = substringIndex.get(chunk);
			if (set == null || set.isEmpty()) return null;
			matches.add(new ArrayList<Integer>(set));
		}
		
		match(limit, chunks, matches, new int[chunks.size()], 0, beans);
		return beans;
	}
	
	public static void match(int limit, List<String> chunks, List<List<Integer>> matches, int[] indices, int index,
			List<ChunkMatchBean> beans) {
		if (beans.size() >= limit) return; // limit quantity of results
		if (index == indices.length) {
			ChunkMatchBean bean = new ChunkMatchBean();
			for (int i=0; i<indices.length; i++)
				bean.add(matches.get(i).get(indices[i]));
			if (!bean.overlap(chunks))
				beans.add(bean);
			return;
		}
		for (int i=0; i<matches.get(index).size(); i++) {
			indices[index] = i;
			match(limit, chunks, matches, indices, index+1, beans);
		}
	}
	
	public static List<String> split(String str, int[] splits) {
		List<String> list = new ArrayList<String>();
		int start = 0;
		for (int i=0; i<splits.length; i++) {
			list.add(str.substring(start, splits[i]));
			start = splits[i];
		}
		list.add(str.substring(start));
		return list;
	}
	
	public static void searchAllWords() {
		for (int splits=0; splits < 6; splits++) {
			for (String word : WordFrequencies.map.keySet()) {
				if (word.length() < 4) continue;
				if (splits >= word.length()-1) continue;
				search(word, splits);
			}
		}
	}
	
	public static void main(String[] args) {
//		for (int split=0; split < 6; split++) 
//			search("GOOGOO", split);
		searchAllWords();
	}
}
