package com.zodiackillerciphers.corpus;

import com.zodiackillerciphers.tests.unicity.SubstitutionMutualEvolve;

/** Experiments for LCZ episode 22.  Find patterns matching "i like killing ABCAL", and "kill ABme CDing gi" */
public class PatternSearch extends CorpusBase {
	public static void search() {
		SubstitutionMutualEvolve.initSources();
		System.out.println("Done source init.");
		CorpusBase.SHOW_INFO = false;
		long samples = 0;
		long hits = 0;
		long sources = 0;
		boolean go = true;
		while (go) {
			sources++;
			go = !SubstitutionMutualEvolve.randomSource();
			for (int i=0; i<tokens.length; i++) {
				pattern7(i);
			}
		}
		System.out.println("Sources: " + sources);
		System.out.println("Samples: " + samples);
		System.out.println("Hits: " + hits);
	}

	public static void pattern1(int i) {
		StringBuffer nospaces = new StringBuffer();
		StringBuffer spaces = new StringBuffer();
		for (int j=i+1; j<tokens.length; j++) {
			nospaces.append(tokens[j]);
			spaces.append(tokens[j]).append(" ");
			if (nospaces.length() > 3) break;
		}
		if (nospaces.length() > 3 && nospaces.charAt(0) == nospaces.charAt(3)) {
			if (nospaces.length() == 4) 
				System.out.println(spaces);
			else if (nospaces.charAt(4) == 'L')
				System.out.println(spaces);
		}
	}
	public static void pattern2(int i) {
		String pattern = "INGGI";
		StringBuffer nospaces = new StringBuffer();
		StringBuffer spaces = new StringBuffer();
		for (int j=i+1; j<tokens.length; j++) {
			nospaces.append(tokens[j]);
			spaces.append(tokens[j]).append(" ");
			if (nospaces.length() > 5) break;
		}
		if (nospaces.length() < 6) return;
		if (nospaces.charAt(2) != 'M' || nospaces.charAt(3) != 'E') return;
		for (int j=0; j<pattern.length() && (j+6) < nospaces.length(); j++) {
			if (nospaces.charAt(j+6) != pattern.charAt(j)) return;
		}
		System.out.println(spaces);
	}
	public static void pattern3(int i) {
		String pattern = "HINGGI";
		StringBuffer nospaces = new StringBuffer();
		StringBuffer spaces = new StringBuffer();
		for (int j=i+1; j<tokens.length; j++) {
			nospaces.append(tokens[j]);
			spaces.append(tokens[j]).append(" ");
			if (nospaces.length() > 5) break;
		}
		if (nospaces.length() < 6) return;
		if (nospaces.charAt(2) != 'M' || nospaces.charAt(3) != 'E') return;
		for (int j=0; j<pattern.length() && (j+5) < nospaces.length(); j++) {
			if (nospaces.charAt(j+5) != pattern.charAt(j)) return;
		}
		System.out.println(spaces);
	}

	public static void pattern4(int i) {
		StringBuffer nospaces = new StringBuffer();
		StringBuffer spaces = new StringBuffer();
		for (int j=i+1; j<tokens.length; j++) {
			nospaces.append(tokens[j]);
			spaces.append(tokens[j]).append(" ");
			if (nospaces.length() > 4) break;
		}
		if (nospaces.length() < 5) return;
		if (nospaces.charAt(0) != 'N' || nospaces.charAt(3) != 'G' || nospaces.charAt(4) != 'I') return;
		System.out.println(spaces);
	}

	public static void pattern5(int i) {
		StringBuffer nospaces = new StringBuffer();
		StringBuffer spaces = new StringBuffer();
		for (int j=i+1; j<tokens.length; j++) {
			nospaces.append(tokens[j]);
			spaces.append(tokens[j]).append(" ");
			if (nospaces.length() > 4) break;
		}
		if (nospaces.length() < 5) return;
		if (nospaces.charAt(0) != 'W' || nospaces.charAt(1) != 'H' || nospaces.charAt(3) != 'G' || nospaces.charAt(4) != 'I') return;		
		if (nospaces.length() > 6 && nospaces.charAt(6) != 'L') return;
		if (nospaces.length() > 7 && nospaces.charAt(7) != 'T') return;
		if (nospaces.length() > 11 && nospaces.charAt(11) != 'E') return;
		if (nospaces.length() > 13 && nospaces.charAt(13) != 'T') return;
		System.out.println(spaces);
	}

	public static void pattern6(int i) {
		StringBuffer nospaces = new StringBuffer();
		StringBuffer spaces = new StringBuffer();
		for (int j=i+1; j<tokens.length; j++) {
			nospaces.append(tokens[j]);
			spaces.append(tokens[j]).append(" ");
			if (nospaces.length() > 3) break;
		}
		if (nospaces.length() < 4) return;
		if (nospaces.charAt(0) != nospaces.charAt(3) || nospaces.charAt(2) != 'O') return;
		if (nospaces.length() > 4 && nospaces.charAt(4) != 'L') return;
		System.out.println(spaces);
	}

	/* ALL TH ????????????????? E I HAVE */
	public static void pattern7(int i) {
		StringBuffer nospaces = new StringBuffer();
		StringBuffer spaces = new StringBuffer();
		for (int j=i+1; j<tokens.length; j++) {
			nospaces.append(tokens[j]);
			spaces.append(tokens[j]).append(" ");
			if (nospaces.length() > 27) break;
		}
		if (nospaces.length() < 28) return;
		if (nospaces.indexOf("ALLTH") != 0) return;
		if (nospaces.indexOf("EIHAVE") != 22) return;
		System.out.println(spaces);
	}

	public static void main(String[] args) {
		search();
	}

}
