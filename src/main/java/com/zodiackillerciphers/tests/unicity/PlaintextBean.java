package com.zodiackillerciphers.tests.unicity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.lucene.Stats;
import com.zodiackillerciphers.suffixtree.LRS;

public class PlaintextBean {
	public List<String> words1;
	public List<String> words2;
	
	public Random rand = new Random();
	
	public PlaintextBean() {
		words1 = new ArrayList<String>();
		words2 = new ArrayList<String>();
	}
	
	public String toStringSpaces(List<String> words) {
		String str = "";
		for (String word : words) {
			if (!str.isEmpty()) str += " ";
			str += word;
		}
		return str;
	}
	public String toStringNoSpaces(List<String> words) {
		String str = toStringSpaces(words).replaceAll(" ", "");
		return str;
	}
	
	/** insert the pair of word n-grams at the given position.  but only if they result in mutually compatible
	 * plaintexts that are not too repetitive or trivial.  returns true if successful.
	 */
	public boolean add(String[] s1, String[] s2, int pos1, int pos2) {
//			System.out.println("Add: " + Arrays.toString(s1) + "	" + Arrays.toString(s2) + "	" + pos1 + "	" + pos2 + "	" + words1 + "	" + words2);
		List<String> new1 = new ArrayList<String>();
		new1.addAll(words1);
		List<String> new2 = new ArrayList<String>();
		new2.addAll(words2);
		
//		String new2 = toStringNoSpaces(words2);
//		for (String s : s1) {
//			new1 += s; 
//		}
//		for (String s : s2) {
//			new2 += s; 
//		}
//		if (!compatible(new1, new2)) return false;
//		if (tooRepetitive(new1)) return false;
//		if (tooRepetitive(new2)) return false;

		if (dupe(s1, pos1, words1)) return false;
		if (dupe(s2, pos2, words2)) return false;
		
		int i=pos1;
		for (String s : s1) {
//			System.out.println(Arrays.toString(s1) + "	" + pos1 + "	[" + s + "]	" + i);
			if (s.length() < 1) continue;
			new1.add(i++, s);
		}
		i=pos2;
		for (String s : s2) {
//			System.out.println(Arrays.toString(s2) + "	" + pos2 + "	[" + s + "]	" + i);
			if (s.length() < 1) continue;
			new2.add(i++, s);
		}

		// skip this since it probably is handled by the iocNgram check 
//		if (tooManyRepeatingWords(new1)) return false;
//		if (tooManyRepeatingWords(new2)) return false;
		
		String ns1 = toStringNoSpaces(new1);
		String ns2 = toStringNoSpaces(new2);
		
		if (tooSimilar(ns1, ns2)) return false;
		
//		if (!compatibleVigenere(ns1, ns2, 5)) return false;
		if (!compatible(ns1, ns2)) return false;
		if (common(new1, new2)) return false;
		if (tooRepetitive(ns1)) return false;
		if (tooRepetitive(ns2)) return false;
		
		if (badIoc(ns1)) return false;
		if (badIoc(ns2)) return false;
		
		// made it past all the compatibility and quality checks so replace the lists with the new ones.
		words1 = new1;
		words2 = new2;
		return true;
	}
	
	// return true if the digraphic ioc is too high (suggesting too much repetition in the plaintext) 
	public static boolean badIoc(String str) {
		return Stats.iocNgram(str, 2) > 0.01;
	}
	
	public static float averageTokenLength(String str) {
		if (str == null || str.isEmpty()) return 0;
		float sum = 0;
		int count = 0;
		for (String s : str.split(" ")) {
			sum += s.length();
			count++;
		}
		if (count == 0) return 0;
		return sum / count; 
	}
	
	public static boolean badAverageTokenLength(String str) {
		return averageTokenLength(str) <= 2;
	}
	public static boolean badAverageTokenLength(List<String> list) {
		float sum = 0;
		if (list == null || list.isEmpty()) return false;
		for (String token : list) {
			sum += token.length();
		}
		sum /= list.size();
		return sum <= 2;
	}
	
	public boolean tooManyRepeatingWords(List<String> words) {
		Set<String> uniq = new HashSet<String>();
		String ns1 = "";
		String ns2 = "";
		for (int i=0; i<words.size(); i++) {
			String word = words.get(i);
			// avoid consecutive repeating words
			if (i>0 && words.get(i-1).equals(word)) return true;
			if (!uniq.contains(word))
				ns2 += word;
			uniq.add(word);
			ns1 += word;
		}
		float max = 0.25f;
		int diff = ns1.length()-ns2.length();
		float ratio = diff;
		ratio /= ns1.length();
		return ratio >= max;
		
	}
	
//	we are inserting ngrams n1, n2, n3 into words w1, w2, w3, w4, w5
//	assume we are inserting between w3 and w4
//	prevent the insertion if:
//		1) n1 == w3 or
//		2) n3 = w4
//	this will try to avoid plaintexts with too many repeated words
	public boolean dupe(String[] s, int pos, List<String> words) {
//		System.out.println("dupe " + Arrays.toString(s) + " " + pos + " " + words);
		if (pos > 0)
			if (s[0].equals(words.get(pos - 1)))
				return true;
		if (pos+1 < words.size())
			if (s[s.length - 1].equals(words.get(pos + 1)))
				return true;
		return false;
	}
	
	/** returns true if there is a shared word */
	public boolean common(List<String> list1, List<String> list2) {
		for (int i=0; i<list1.size() && i<list2.size(); i++) {
			if (list1.get(i).equals(list2.get(i))) return true;
		}
		return false;
	}
	
	/** returns true if the longest repeating substring is too long */
	public static boolean tooRepetitive(String str) {
		float limit = str.length() * 33;
		limit /= 100;
		
		return LRS.lrs(str).length() >= limit;
	}
	
	/** returns true if too many letters are shared */
	public static boolean tooSimilar(String s1, String s2) {
		int count = 0;
		int max = s1.length()/2;
		for (int i=0; i<s1.length(); i++) {
			if (s1.charAt(i) == s2.charAt(i)) {
				count++;
				if (count > max) return true;
			}
		}
		return false;
	}
	
	public boolean compatible(String s1, String s2) {
		String a1 = Arrays.toString(Ciphers.toNumeric(s1, false));
		String a2 = Arrays.toString(Ciphers.toNumeric(s2, false));
		return a1.equals(a2);
	}
	
	public boolean compatibleVigenere(String s1, String s2, int d) {
		String v1 = Arrays.toString(VigenereMutual.letterDifferences(s1, d));
		String v2 = Arrays.toString(VigenereMutual.letterDifferences(s2, d));
		return v1.equals(v2);
	}
	
	/** generate a random position for insertion into a words list */
	public int randomPosition1() {
		if (rand.nextBoolean()) return 0;
		return words1.size();
//		return rand.nextInt(words1.size()+1); // [0, N] include one position beyond end, so we can append
	}
	public int randomPosition2() {
		if (rand.nextBoolean()) return 0;
		return words2.size();
//		return rand.nextInt(words2.size()+1); // [0, N] include one position beyond end, so we can append
	}
	
	public int length() {
		String str = "";
		for (String word : words1) str += word;
		return str.length();
	}
	
	public double ioc(List<String> words) {
		return Stats.iocDiff(toStringNoSpaces(words));
	}
	
	public String toString() {
		return length() + "	" + words1 + "	" + words2 + "	" + Stats.iocNgram(toStringNoSpaces(words1), 2) + "	"
				+ Stats.iocNgram(toStringNoSpaces(words2), 2) + "	" + this.hashCode();
	}
	
	public static void main(String[] args) {
		String[] test = new String[] {
				"A AG A BA G F G G F E FE F ED D D E C CB C DC B A B B",
				"C CB C DC B A B B A G AG A GF F F G E ED E FE D C D D",
				"G GF G AG F E F F E D ED E DC C C D B BA B CB A G A A",
				"F F E D ED E DC C C D B BA B CB A G A A G F GF G FE E",
				"G G F E FE F ED D D E C CB C DC B A B B A G AG A GF F",
				"A A B G GF G AG F E F F E D ED E DC C C D B BA B CB A",
				"B B C A AG A BA G F G G F E FE F ED D D E C CB C DC B",
				"C C D B BA B CB A G A A G F GF G FE E E F D DC D ED C",
				"D D E C CB C DC B A B B A G AG A GF F F G E ED E FE D",
				"A A D F B F B B E G C G C C F A D A D D G B E B E E A C F C",
				"A A D F B F BB E G C G C C F A D A D D G B E B E E A C F C",
				"B B E G C G C C F A D A D D G B E B E E A C F C F F B D G D",
				"BB E G C G C C F A D A D D G B E B E E A C F C F F B D G D",
				"C C F A D A D D G B E B E E A C F C F F B D G D G G C E A E",
				"D D G B E B E E A C F C F F B D G D G G C E A E A A D F B F",
				"D D G B E B E E A C F C F F B D G D G GCE A E A A D F B F",
				"E E A C F C F F B D G D G G C E A E A A D F B F B B E G C G",
				"E E A C F C F F B D G D G GCE A E A A D F B F BB E G C G",
				"F F B D G D G G C E A E A A D F B F B B E G C G C C F A D A",
				"F F B D G D G GCE A E A A D F B F BB E G C G C C F A D A",
				"G G C E A E A A D F B F B B E G C G C C F A D A D D G B E B",
				"G GCE A E A A D F B F BB E G C G C C F A D A D D G B E B",
				"AA DEC R NOV NOV AA DEC R DEC NOV AA DEC R",
				"AA OCT R SEP SEP AA OCT R OCT SEP AA OCT R",
				"A E A A D F B F B B E G C G C C F A D A D D G B E B E E A C",
				"A E A A D F B F BB E G C G C C F A D A D D G B E B E E A C",
				"B F BB E G C G C C F A D A D D G B E B E E A C F C F F B D",
				"B F B B E G C G C C F A D A D D G B E B E E A C F C F F B D",
				"C G C C F A D A D D G B E B E E A C F C F F B D G D G G C E",
				"D A D D G B E B E E A C F C F F B D G D G G C E A E A A D F",
				"E B E E A C F C F F B D G D G G C E A E A A D F B F B B E G",
				"E B E E A C F C F F B D G D G GCE A E A A D F B F BB E G",
				"F C F F B D G D G GCE A E A A D F B F BB E G C G C C F A",
				"F C F F B D G D G G C E A E A A D F B F B B E G C G C C F A",
				"G D G GCE A E A A D F B F BB E G C G C C F A D A D D G B",
				"G D G G C E A E A A D F B F B B E G C G C C F A D A D D G B",
				"GO GO GO GO GO PR PR PR PR PR ST ST ST ST ST",
				"PR PR PR PR PR ST ST ST ST ST BA BA BA BA BA",
				"ST ST ST ST ST BA BA BA BA BA KR KR KR KR KR",
				"E D ED E DC C C D B BA B CB A G A A G F GF G FE E E F",
				"F E FE F ED D D E C CB C DC B A B B A G AG A GF F F G",
				"D ED E DC C C D B BA B CB A G A A G F GF G FE E E F D",
				"E FE F ED D D E C CB C DC B A B B A G AG A GF F F G E",
				"A D A D D G B E B E E A C F C F F B D G D G G C E A E A A D",
				"B E B E E A C F C F F B D G D G G C E A E A A D F B F B B E",
				"B E B E E A C F C F F B D G D G GCE A E A A D F B F BB E",
				"C F C F F B D G D G GCE A E A A D F B F BB E G C G C C F",
				"C F C F F B D G D G G C E A E A A D F B F B B E G C G C C F",
				"D G D G G C E A E A A D F B F B B E G C G C C F A D A D D G",
				"D G D G GCE A E A A D F B F BB E G C G C C F A D A D D G",
				"E A E A A D F B F B B E G C G C C F A D A D D G B E B E E A",
				"F B F B B E G C G C C F A D A D D G B E B E E A C F C F F B",
				"F B F BB E G C G C C F A D A D D G B E B E E A C F C F F B",
				"G C G C C F A D A D D G B E B E E A C F C F F B D G D G G C",
				"AG A BA G F G G F E FE F ED D D E C CB C DC B A B B A",
				"CB C DC B A B B A G AG A GF F F G E ED E FE D C D D C",
				"GF G AG F E F F E D ED E DC C C D B BA B CB A G A A G",
				"A G A C E F G B D E F A C D E D G F B A C B C E G A B D F G",
				"B A B D F G A C E F G B D E F E A G C B D C D F A B C E G A",
				"C B C E G A B D F G A C E F G F B A D C E D E G B C D F A B",
				"D C D F A B C E G A B D F G A G C B E D F E F A C D E G B C",
				"E D E G B C D F A B C E G A B A D C F E G F G B D E F A C D",
				"F E F A C D E G B C D F A B C B E D G F A G A C E F G B D E",
				"B C B E D G F A G A C E F G B D E F A C D E D G F B A C B C",
				"D E D G F B A C B C E G A B D F G A C E F G F B A D C E D E",
				"E F E A G C B D C D F A B C E G A B D F G A G C B E D F E F",
				"F G F B A D C E D E G B C D F A B C E G A B A D C F E G F G",
				"G A G C B E D F E F A C D E G B C D F A B C B E D G F A G A",
				"E F F E D ED E DC C C D B BA B CB A G A A G F GF G FE",
				"F G G F E FE F ED D D E C CB C DC B A B B A G AG A GF",
				"R III CHAPTER IV CHAPTER V CHAPTER VI",
				"XIII SERMON XIV SERMON XV SERMON XVI",
				"B G GF G AG F E F F E D ED E DC C C D B BA B CB A G A",
				"C A AG A BA G F G G F E FE F ED D D E C CB C DC B A B",
				"E C CB C DC B A B B A G AG A GF F F G E ED E FE D C D",
				"A D D G B E B E E A C F C F F B D G D G G C E A E A A D F B",
				"A D D G B E B E E A C F C F F B D G D G GCE A E A A D F B",
				"B E E A C F C F F B D G D G G C E A E A A D F B F B B E G C",
				"B E E A C F C F F B D G D G GCE A E A A D F B F BB E G C",
				"C F F B D G D G GCE A E A A D F B F BB E G C G C C F A D",
				"C F F B D G D G G C E A E A A D F B F B B E G C G C C F A D",
				"D G GCE A E A A D F B F BB E G C G C C F A D A D D G B E",
				"D G G C E A E A A D F B F B B E G C G C C F A D A D D G B E",
				"E A A D F B F BB E G C G C C F A D A D D G B E B E E A C F",
				"E A A D F B F B B E G C G C C F A D A D D G B E B E E A C F",
				"F BB E G C G C C F A D A D D G B E B E E A C F C F F B D G",
				"F B B E G C G C C F A D A D D G B E B E E A C F C F F B D G",
				"G C C F A D A D D G B E B E E A C F C F F B D G D G G C E A",
				"A C F A C F B D G B D G C E A C E A D F B D F B E G C E G C",
				"A C F A C F B D G B D G C E AC E A D F B D F BEG C E G C",
				"B D G B D G C E A C E A D F B D F B E G C E G C F A D F A D",
				"B D G B D G C E AC E A D F B D F BEG C E G C F A D F AD",
				"C E A C E A D F B D F B E G C E G C F A D F A D G B E G B E",
				"D F B D F B E G C E G C F A D F A D G B E G B E A C F A C F",
				"E G C E G C F A D F A D G B E G B E A C F A C F B D G B D G",
				"F A D F A D G B E G B E A C F A C F B D G B D G C E A C E A",
				"F A D F A D G B E G B E A C F A C F B D G B D G C E AC E A",
				"G B E G B E A C F A C F B D G B D G C E A C E A D F B D F B",
				"A D F A D G B E G B E A C F A C F B D G B D G C E A C E A D",
				"A D F A D G B E G B E A C F A C F B D G B D G C E AC E A D",
				"B E G B E A C F A C F B D G B D G C E A C E A D F B D F B E",
				"C F A C F B D G B D G C E AC E A D F B D F BEG C E G C F",
				"C F A C F B D G B D G C E A C E A D F B D F B E G C E G C F",
				"D G B D G C E A C E A D F B D F B E G C E G C F A D F A D G",
				"D G B D G C E AC E A D F B D F BEG C E G C F A D F AD G",
				"E A C E A D F B D F B E G C E G C F A D F A D G B E G B E A",
				"F B D F B E G C E G C F A D F A D G B E G B E A C F A C F B",
				"G C E G C F A D F A D G B E G B E A C F A C F B D G B D G C",
				"B A C B C E G A B D F G A C E F G F B A D C E D E G B C D F",
				"C B D C D F A B C E G A B D F G A G C B E D F E F A C D E G",
				"D C E D E G B C D F A B C E G A B A D C F E G F G B D E F A",
				"E D F E F A C D E G B C D F A B C B E D G F A G A C E F G B",
				"G F A G A C E F G B D E F A C D E D G F B A C B C E G A B D",
				"A C E A D F B D F B E G C E G C F A D F A D G B E G B E A C",
				"B D F B E G C E G C F A D F A D G B E G B E A C F A C F B D",
				"C E G C F A D F A D G B E G B E A C F A C F B D G B D G C E",
				"D F A D G B E G B E A C F A C F B D G B D G C E A C E A D F",
				"D F A D G B E G B E A C F A C F B D G B D G C E AC E A D F",
				"E G B E A C F A C F B D G B D G C E A C E A D F B D F B E G",
				"E G B E A C F A C F B D G B D G C E AC E A D F B D F BEG",
				"F A C F B D G B D G C E A C E A D F B D F B E G C E G C F A",
				"F A C F B D G B D G C E AC E A D F B D F BEG C E G C F A",
				"G B D G C E AC E A D F B D F BEG C E G C F A D F AD G B",
				"G B D G C E A C E A D F B D F B E G C E G C F A D F A D G B",
				"EHT ELOM SI GNIYAL A TNALP OT TEG MEHT",
				"FIU FMPN TJ HOJZBM B UOBMQ PU UFH NFIU",
				"F E D ED E DC C C D B BA B CB A G A A G F GF G FE E E",
				"G F E FE F ED D D E C CB C DC B A B B A G AG A GF F F",
				"A C F C F F B D G D G G C E A E A A D F B F B B E G C G C C",
				"A C F C F F B D G D G GCE A E A A D F B F BB E G C G C C",
				"B D G D G GCE A E A A D F B F BB E G C G C C F A D A D D",
				"B D G D G G C E A E A A D F B F B B E G C G C C F A D A D D",
				"C E A E A A D F B F B B E G C G C C F A D A D D G B E B E E",
				"D F B F BB E G C G C C F A D A D D G B E B E E A C F C F F",
				"D F B F B B E G C G C C F A D A D D G B E B E E A C F C F F",
				"E G C G C C F A D A D D G B E B E E A C F C F F B D G D G G",
				"F A D A D D G B E B E E A C F C F F B D G D G G C E A E A A",
				"G B E B E E A C F C F F B D G D G GCE A E A A D F B F BB",
				"G B E B E E A C F C F F B D G D G G C E A E A A D F B F B B",
				"A C B C E G A B D F G A C E F G F B A D C E D E G B C D F A",
				"B D C D F A B C E G A B D F G A G C B E D F E F A C D E G B",
				"C E D E G B C D F A B C E G A B A D C F E G F G B D E F A C",
				"D F E F A C D E G B C D F A B C B E D G F A G A C E F G B D",
				"F A G A C E F G B D E F A C D E D G F B A C B C E G A B D F",
				"G B A B D F G A C E F G B D E F E A G C B D C D F A B C E G",
				"A B C B E D G F A G A C E F G B D E F A C D E D G F B A C B",
				"C D E D G F B A C B C E G A B D F G A C E F G F B A D C E D",
				"D E F E A G C B D C D F A B C E G A B D F G A G C B E D F E",
				"E F G F B A D C E D E G B C D F A B C E G A B A D C F E G F",
				"F G A G C B E D F E F A C D E G B C D F A B C B E D G F A G",
				"G A B A D C F E G F G B D E F A C D E G B C D C F E A G B A",
				"D CB B B C A AG A BA G F G G F E FE F ED D D E C CB C",
				"E DC C C D B BA B CB A G A A G F GF G FE E E F D DC D",
				"F ED D D E C CB C DC B A B B A G AG A GF F F G E ED E",
				"A B G GF G AG F E F F E D ED E DC C C D B BA B CB A G",
				"B C A AG A BA G F G G F E FE F ED D D E C CB C DC B A",
				"D E C CB C DC B A B B A G AG A GF F F G E ED E FE D C",
				"L A D D G B E B E E A C F C F F B D G D G GCE A E A A D F",
				"T C F F B D G D G GCE A E A A D F B F BB E G C G C C F A",
				"A D F B D F B E G C E G C F A D F A D G B E G B E A C F A C",
				"B E G C E G C F A D F A D G B E G B E A C F A C F B D G B D",
				"C F A D F A D G B E G B E A C F A C F B D G B D G C E A C E",
				"C F A D F A D G B E G B E A C F A C F B D G B D G C E AC E",
				"D G B E G B E A C F A C F B D G B D G C E A C E A D F B D F",
				"D G B E G B E A C F A C F B D G B D G C E AC E A D F B D F",
				"E A C F A C F B D G B D G C E AC E A D F B D F BEG C E G",
				"E A C F A C F B D G B D G C E A C E A D F B D F B E G C E G",
				"F B D G B D G C E A C E A D F B D F B E G C E G C F A D F A",
				"G C E A C E A D F B D F B E G C E G C F A D F A D G B E G B",
				"G C E AC E A D F B D F BEG C E G C F A D F AD G B E G B",
				"A D C E D E G B C D F A B C E G A B A D C F E G F G B D E F",
				"B E D F E F A C D E G B C D F A B C B E D G F A G A C E F G",
				"D G F A G A C E F G B D E F A C D E D G F B A C B C E G A B",
				"F B A C B C E G A B D F G A C E F G F B A D C E D E G B C D",
				"G C B D C D F A B C E G A B D F G A G C B E D F E F A C D E",
				"SI GNIYAL A TNALP OT TEG MEHT THGINOT",
				"TJ HOJZBM B UOBMQ PU UFH NFIU UIHJOPU",
				"AG F E F F E D ED E DC C C D B BA B CB A G A A G F GF",
				"BA G F G G F E FE F ED D D E C CB C DC B A B B A G AG",
				"DC B A B B A G AG A GF F F G E ED E FE D C D D C B CB",
				"A D F B F BB E G C G C C F A D A D D G B E B E E A C F C F",
				"A D F B F B B E G C G C C F A D A D D G B E B E E A C F C F",
				"B E G C G C C F A D A D D G B E B E E A C F C F F B D G D G",
				"C F A D A D D G B E B E E A C F C F F B D G D G G C E A E A",
				"D G B E B E E A C F C F F B D G D G G C E A E A A D F B F B",
				"E A C F C F F B D G D G G C E A E A A D F B F B B E G C G C",
				"E A C F C F F B D G D G GCE A E A A D F B F BB E G C G C",
				"F B D G D G G C E A E A A D F B F B B E G C G C C F A D A D",
				"F B D G D G GCE A E A A D F B F BB E G C G C C F A D A D",
				"G C E A E A A D F B F B B E G C G C C F A D A D D G B E B E",
				"GCE A E A A D F B F BB E G C G C C F A D A D D G B E B E",
				"A C D E D G F B A C B C E G A B D F G A C E F G F B A D C E",
				"B D E F E A G C B D C D F A B C E G A B D F G A G C B E D F",
				"C E F G F B A D C E D E G B C D F A B C E G A B A D C F E G",
				"D F G A G C B E D F E F A C D E G B C D F A B C B E D G F A",
				"E G A B A D C F E G F G B D E F A C D E G B C D C F E A G B",
				"F A B C B E D G F A G A C E F G B D E F A C D E D G F B A C",
				"MEHT THGINOT NEEWTEB THGIE DNA ENIN",
				"NFIU UIHJOPU OFFXUFC UIHJF EOB FOJO",
				"A B C E G A B A D C F E G F G B D E F A C D E G B C D C F E",
				"B C D F A B C B E D G F A G A C E F G B D E F A C D E D G F",
				"D E F A C D E D G F B A C B C E G A B D F G A C E F G F B A",
				"E F G B D E F E A G C B D C D F A B C E G A B D F G A G C B",
				"F G A C E F G F B A D C E D E G B C D F A B C E G A B A D C",
				"G A B D F G A G C B E D F E F A C D E G B C D F A B C B E D",
				"CASE V CASE VI CASE VII CASE VIII CASE",
				"PART V PART VI PART VII PART VIII PART",
				"JOHN X JOHN XI JOHN XII JOHN XIII JOHN",
				"A B C E G A B D F G A G C B E D F E F A C D E G B C D F A B",
				"B C D F A B C E G A B A D C F E G F G B D E F A C D E G B C",
				"C D E G B C D F A B C B E D G F A G A C E F G B D E F A C D",
				"E F G B D E F A C D E D G F B A C B C E G A B D F G A C E F",
				"F G A C E F G B D E F E A G C B D C D F A B C E G A B D F G",
				"G A B D F G A C E F G F B A D C E D E G B C D F A B C E G A",
				"A B D F G A G C B E D F E F A C D E G B C D F A B C B E D G",
				"B C E G A B A D C F E G F G B D E F A C D E G B C D C F E A",
				"C D F A B C B E D G F A G A C E F G B D E F A C D E D G F B",
				"E F A C D E D G F B A C B C E G A B D F G A C E F G F B A D",
				"F G B D E F E A G C B D C D F A B C E G A B D F G A G C B E",
				"G A C E F G F B A D C E D E G B C D F A B C E G A B A D C F",
				"A B D F G A C E F G B D E F E A G C B D C D F A B C E G A B",
				"B C E G A B D F G A C E F G F B A D C E D E G B C D F A B C",
				"C D F A B C E G A B D F G A G C B E D F E F A C D E G B C D",
				"D E G B C D F A B C E G A B A D C F E G F G B D E F A C D E",
				"E F A C D E G B C D F A B C B E D G F A G A C E F G B D E F",
				"G A C E F G B D E F A C D E D G F B A C B C E G A B D F G A",
				"A B D F G A C E F G F B A D C E D E G B C D F A B C E G A B",
				"B C E G A B D F G A G C B E D F E F A C D E G B C D F A B C",
				"C D F A B C E G A B A D C F E G F G B D E F A C D E G B C D",
				"D E G B C D F A B C B E D G F A G A C E F G B D E F A C D E",
				"F G B D E F A C D E D G F B A C B C E G A B D F G A C E F G",
				"G A C E F G B D E F E A G C B D C D F A B C E G A B D F G A",
				"A D G B E G B E A C F A C F B D G B D G C E A C E A D F B D",
				"A D G B E G B E A C F A C F B D G B D G C E AC E A D F B D",
				"B E A C F A C F B D G B D G C E A C E A D F B D F B E G C E",
				"B E A C F A C F B D G B D G C E AC E A D F B D F BEG C E",
				"C F B D G B D G C E AC E A D F B D F BEG C E G C F A D F",
				"C F B D G B D G C E A C E A D F B D F B E G C E G C F A D F",
				"D G C E AC E A D F B D F BEG C E G C F A D F AD G B E G",
				"D G C E A C E A D F B D F B E G C E G C F A D F A D G B E G",
				"E A D F B D F B E G C E G C F A D F A D G B E G B E A C F A",
				"F B E G C E G C F A D F A D G B E G B E A C F A C F B D G B",
				"G C F A D F A D G B E G B E A C F A C F B D G B D G C E AC",
				"G C F A D F A D G B E G B E A C F A C F B D G B D G C E A C",
				"A G C B D C D F A B C E G A B D F G A G C B E D F E F A C D",
				"B A D C E D E G B C D F A B C E G A B A D C F E G F G B D E",
				"C B E D F E F A C D E G B C D F A B C B E D G F A G A C E F",
				"E D G F A G A C E F G B D E F A C D E D G F B A C B C E G A",
				"G F B A C B C E G A B D F G A C E F G F B A D C E D E G B C",
				"A C E F G F B A D C E D E G B C D F A B C E G A B A D C F E",
				"B D F G A G C B E D F E F A C D E G B C D F A B C B E D G F",
				"C E G A B A D C F E G F G B D E F A C D E G B C D C F E A G",
				"D F A B C B E D G F A G A C E F G B D E F A C D E D G F B A",
				"F A C D E D G F B A C B C E G A B D F G A C E F G F B A D C",
				"G B D E F E A G C B D C D F A B C E G A B D F G A G C B E D",
				"MODEL A MODEL B MODEL C MODEL F MODEL N",
				"MODEL B MODEL C MODEL F MODEL N MODEL R",
				"MODEL C MODEL F MODEL N MODEL R MODEL S",
				"MUSIC D MUSIC E MUSIC F MUSIC G MUSIC H",
				"B D E F A C D E D G F B A C B C E G A B D F G A C E F G F B",
				"C E F G B D E F E A G C B D C D F A B C E G A B D F G A G C",
				"D F G A C E F G F B A D C E D E G B C D F A B C E G A B A D",
				"E G A B D F G A G C B E D F E F A C D E G B C D F A B C B E",
				"F A B C E G A B A D C F E G F G B D E F A C D E G B C D C F",
				"G B C D F A B C B E D G F A G A C E F G B D E F A C D E D G",
				"A C D E G B C D F A B C B E D G F A G A C E F G B D E F A C",
				"C E F G B D E F A C D E D G F B A C B C E G A B D F G A C E",
				"D F G A C E F G B D E F E A G C B D C D F A B C E G A B D F",
				"E G A B D F G A C E F G F B A D C E D E G B C D F A B C E G",
				"F A B C E G A B D F G A G C B E D F E F A C D E G B C D F A",
				"G B C D F A B C E G A B A D C F E G F G B D E F A C D E G B",
				"A C F B D G B D G C E AC E A D F B D F BEG C E G C F A D",
				"A C F B D G B D G C E A C E A D F B D F B E G C E G C F A D",
				"B D G C E A C E A D F B D F B E G C E G C F A D F A D G B E",
				"B D G C E AC E A D F B D F BEG C E G C F A D F AD G B E",
				"C E A D F B D F B E G C E G C F A D F A D G B E G B E A C F",
				"D F B E G C E G C F A D F A D G B E G B E A C F A C F B D G",
				"E G C F A D F A D G B E G B E A C F A C F B D G B D G C E A",
				"F A D G B E G B E A C F A C F B D G B D G C E A C E A D F B",
				"F A D G B E G B E A C F A C F B D G B D G C E AC E A D F B",
				"G B E A C F A C F B D G B D G C E A C E A D F B D F B E G C",
				"G B E A C F A C F B D G B D G C E AC E A D F B D F BEG C",
				"B E D G F A G A C E F G B D E F A C D E D G F B A C B C E G",
				"D G F B A C B C E G A B D F G A C E F G F B A D C E D E G B",
				"E A G C B D C D F A B C E G A B D F G A G C B E D F E F A C",
				"F B A D C E D E G B C D F A B C E G A B A D C F E G F G B D",
				"G C B E D F E F A C D E G B C D F A B C B E D G F A G A C E",
				"SERMON I SERMON II SERMON III SERMON",
				"VOLUME I VOLUME II VOLUME III VOLUME",
				"SERMON X SERMON XI SERMON XII SERMON",
				"SERMON V SERMON VI SERMON VII SERMON",
				"VOLUME X VOLUME XI VOLUME XII VOLUME",
				"A MODEL B MODEL C MODEL F MODEL N MODEL",
				"B MODEL C MODEL F MODEL N MODEL R MODEL",
				"BOUNDS MOUNDS LOUNDS FOUNDS KOUNDS",
				"C HOURS D HOURS E HOURS F HOURS G HOURS",
				"D MUSIC E MUSIC F MUSIC G MUSIC H MUSIC",
				"A C E F G B D E F A C D E D G F B A C B C E G A B D F G A C",
				"B D F G A C E F G B D E F E A G C B D C D F A B C E G A B D",
				"C E G A B D F G A C E F G F B A D C E D E G B C D F A B C E",
				"D F A B C E G A B D F G A G C B E D F E F A C D E G B C D F",
				"E G B C D F A B C E G A B A D C F E G F G B D E F A C D E G",
				"F A C D E G B C D F A B C B E D G F A G A C E F G B D E F A",
				"A C E F G B D E F E A G C B D C D F A B C E G A B D F G A G",
				"B D F G A C E F G F B A D C E D E G B C D F A B C E G A B A",
				"C E G A B D F G A G C B E D F E F A C D E G B C D F A B C B",
				"D F A B C E G A B A D C F E G F G B D E F A C D E G B C D C",
				"E G B C D F A B C B E D G F A G A C E F G B D E F A C D E D",
				"G B D E F A C D E D G F B A C B C E G A B D F G A C E F G F",
				"A G C B E D F E F A C D E G B C D F A B C B E D G F A G A C",
				"C B E D G F A G A C E F G B D E F A C D E D G F B A C B C E",
				"E D G F B A C B C E G A B D F G A C E F G F B A D C E D E G",
				"F E A G C B D C D F A B C E G A B D F G A G C B E D F E F A",
				"G F B A D C E D E G B C D F A B C E G A B A D C F E G F G B",
				"EVAH NOITAMROFNI EHT ELOM SI GNIYAL",
				"FWBI OPJUBNSPGOJ FIU FMPN TJ HOJZBM",
				"A B C D E F G H I J K L M N O P Q R S T U V W X Y Z A B C D",
				"B C D E F G H I J K L M N O P Q R S T U V W X Y Z A B C D E",
				"C D E F G H I J K L M N O P Q R S T U V W X Y Z A B C D E F",
				"D E F G H I J K L M N O P Q R S T U V W X Y Z A B C D E F G",
				"E F G H I J K L M N O P Q R S T U V W X Y Z A B C D E F G H",
				"F G H I J K L M N O P Q R S T U V W X Y Z A B C D E F G H I",
				"G H I J K L M N O P Q R S T U V W X Y Z A B C D E F G H I J",
				"H I J K L M N O P Q R S T U V W X Y Z A B C D E F G H I J K",
				"I J K L M N O P Q R S T U V W X Y Z A B C D E F G H I J K L",
				"J K L M N O P Q R S T U V W X Y Z A B C D E F G H I J K L M",
				"K L M N O P Q R S T U V W X Y Z A B C D E F G H I J K L M N",
				"L M N O P Q R S T U V W X Y Z A B C D E F G H I J K L M N O",
				"M N O P Q R S T U V W X Y Z A B C D E F G H I J K L M N O P",
				"N O P Q R S T U V W X Y Z A B C D E F G H I J K L M N O P Q",
				"O P Q R S T U V W X Y Z A B C D E F G H I J K L M N O P Q R",
				"P Q R S T U V W X Y Z A B C D E F G H I J K L M N O P Q R S",
				"Q R S T U V W X Y Z A B C D E F G H I J K L M N O P Q R S T",
				"R S T U V W X Y Z A B C D E F G H I J K L M N O P Q R S T U",
				"S T U V W X Y Z A B C D E F G H I J K L M N O P Q R S T U V",
				"T U V W X Y Z A B C D E F G H I J K L M N O P Q R S T U V W",
				"U V W X Y Z A B C D E F G H I J K L M N O P Q R S T U V W X",
				"V W X Y Z A B C D E F G H I J K L M N O P Q R S T U V W X Y",
				"W X Y Z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z",
				"X Y Z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z A",
				"Y Z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z A B",
				"Z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z A B C",				
		};
		for (String str : test) {
			String ns = str.replaceAll(" ", "");
			System.out.println(averageTokenLength(str) + "	" + badIoc(ns) + "	" + tooRepetitive(ns) + ": " + str);
		}
	}
}
