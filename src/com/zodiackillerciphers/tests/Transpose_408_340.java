package com.zodiackillerciphers.tests;

import java.util.ArrayList;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.dictionary.Search;
import com.zodiackillerciphers.dictionary.WordFrequencies;
import com.zodiackillerciphers.lucene.NGrams;
import com.zodiackillerciphers.ngrams.NGramsBean;

public class Transpose_408_340 {
	public static String[] pt = new String[] { 
			"TEG_EANBIIT_TOA_O",
			"EEELNLNLNEU___SDH",
			"L__VHEIEAENHTLDTF",
			"AEESNAA_B_ENEEGS_",
			"YIHEDRXOTTSIE__IK",
			"EAGNSAN__O_IS__NN",
			"LTESML__IVOAB_DTT",
			"NA_FDXLNEYEUDESTI",
			"O_HE_EEG_SLV_WE_S",
			"_DAIBENFENES_SL__",
			"IEGKTRETNULE_OHSN",
			"__VAGFT_TO_H_E_LS",
			"DEIA_LEETTSLVN_AG",
			"AASENDT____VB_REE",
			"_LO____I_E_BIET_E",
			"TV__DLSNNEN_DHMA_",
			"GVOET_E__ESAELTHT",
			"EEHE_DEVINATOKNHE",
			"TSIOE_SRLYUNL___V",
			"_HNTEEIADENAWTS_E"
	};
	
	static TreeSet<ScoreBean> beans = new TreeSet<ScoreBean>();
	static float best = 0;
	
	/** permute all possible transpositions of N columns selected from the plaintext */
	public static void permute(int n) {
		boolean[] selected = new boolean[pt[0].length()];
		
		List<Integer> selections = new ArrayList<Integer>();
		Integer count = 0;
		permute(n, selected, selections, new Count());
		
		for (ScoreBean bean : beans) System.out.println(bean);
	}
	
	/**
	 * 
	 * @param n how many columns to select
	 * @param selected which columns are already selected
	 * @param selections list of current selections
	 */
	public static void permute(int n, boolean[] selected, List<Integer> selections, Count count) {
		if (count.count % 100000 == 0) System.out.println("count " + count.count + " " + new Date().getTime());
		if (n==0) {
			process(selections);
			return;
		}
		
		for (int i=0; i<selected.length; i++) {
			if (selected[i]) continue;
			selected[i] = true;
			selections.add(i);
			count.count++;
			permute(n-1, selected, selections, count);
			selected[i] = false;
			selections.remove(selections.size()-1);
		}
	}
	
	public static void process(List<Integer> selections) {
		StringBuffer all = new StringBuffer();
		for (String s : pt) {
			StringBuffer line = new StringBuffer();
			for (Integer i : selections) {
				line.append(s.charAt(i));
			}
			all.append(line).append(",");
		}
		ScoreBean bean = new ScoreBean();
		bean.zkscore = NGrams.zkscore(all);
		bean.selections = toString(selections);
		bean.plaintext = all;
		
		beans.add(bean);
		if (beans.size() > 10000) beans.remove(beans.first());
		
		if (bean.zkscore > best) {
			System.out.println("NEW BEST " + bean);
			best = bean.zkscore;
		}
	}
	
	public static String toString(List<Integer> list) {
		StringBuffer sb = new StringBuffer();
		for (Integer i : list) sb.append(i).append(",");
		return sb.toString();
	}
	
	/** examine the plaintext grid, and look for all words that can fit into each line */
	public static void anagrams() {
		Set<String> dict = Search.allWords();
		for (int row=0; row<pt.length; row++) {
			String line = pt[row];
			List<String> words = new ArrayList<String>();
			System.out.println("Words found in line " + row + " [" + line + "]: ");
			NGramsBean bean1 = new NGramsBean(1, line);
			for (String wordLower : dict) {
				if (wordLower.length() > line.length()) continue;
				if (wordLower.length() < 3) continue;
				NGramsBean bean2 = new NGramsBean(1, wordLower.toUpperCase());
				boolean found = true;
				for (String key : bean2.counts.keySet()) {
					if (bean1.counts.get(key) == null) {
						found = false; break;
					}
					if (bean1.counts.get(key) < bean2.counts.get(key)) {
						found = false; break;
					}
				}
				if (found) words.add(wordLower);
			}
			WordFrequencies.prettyPrint(WordFrequencies.sort(words));
		}
	}
	
	/* http://www.zodiackillerfacts.com/forum/viewtopic.php?f=50&t=1517
	 * start at the upper right corner of the 340 and output symbols in diagonal order    
	 */
	
	public static void diagonalDump() {
		String z = Ciphers.cipher[0].cipher;
		char[][] grid = new char[20][17];
		for (int i=0; i<z.length(); i++) {
			int row = i/17;
			int col = i%17;
			grid[row][col] = z.charAt(i);
		}
		
		for (int row=0; row<20; row++) {
			String r = "";
			for (int col=0; col<17; col++) {
				r += grid[row][col];
			}
			System.out.println(r);
		}
		
		String line = "";
		for (int col=16; col>=0; col--) {
			int c = col; int r = 0;
			while (c<17) {
				line += grid[r++][c++];
			}
		}
		for (int row=1; row<20; row++) {
			int c = 0; int r = row;
			while (c<17 && r<20) {
				line += grid[r++][c++];
			}
		}
		
		System.out.println(line);
	}
	
	public static void test() {
		
		for (String s : pt) {
			System.out.println(""+
			s.charAt(1-1)+
			s.charAt(3-1)+
			s.charAt(2-1)+
			s.charAt(4-1)+
			s.charAt(5-1)+
			s.charAt(7-1)+
			s.charAt(6-1)+
			s.charAt(8-1)+
			s.charAt(12-1)+
			s.charAt(11-1)+
			s.charAt(10-1)+
			s.charAt(9-1)+
			s.charAt(14-1)+
			s.charAt(13-1)+
			s.charAt(15-1)+
			s.charAt(16-1)+
			s.charAt(17-1));
		}
	
	}
	
	public static class ScoreBean implements Comparable {
		public float zkscore;
		public String selections;
		public StringBuffer plaintext;
		@Override
		public int compareTo(Object o) {
			return Float.compare(zkscore, ((ScoreBean)o).zkscore);
		}
		
		public String toString() {
			return NGrams.zkscore(plaintext) + " ["+selections+"] " + plaintext;
		}

	}
	
	public static class Count {
		public int count;
	}
	
	public static void main(String[] args) {
		//permute(8); // 980,179,200 tests
		//anagrams();
		diagonalDump();
	}
}
