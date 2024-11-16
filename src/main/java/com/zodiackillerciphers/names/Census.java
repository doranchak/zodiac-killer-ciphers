package com.zodiackillerciphers.names;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.zodiackillerciphers.anagram.Anagrams;
import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.dictionary.WordFrequencies;
import com.zodiackillerciphers.io.FileUtil;
import com.zodiackillerciphers.ngrams.Periods;
import com.zodiackillerciphers.old.EditDistance;
import com.zodiackillerciphers.old.MyNameIs;
import com.zodiackillerciphers.stats.StatsWrapper;
import com.zodiackillerciphers.transform.CipherTransformations;

public class Census {
	
	public static String PREFIX = "/Users/doranchak/projects/zodiac/github/zodiac-killer-ciphers/docs/census-names";
	public static String TAB = "" + '\t';

	/** map for all names, used for scoring */
	public static Map<String, Name> mapFirstMale;
	public static Map<String, Name> mapFirstFemale;
	public static Map<String, Name> mapLast;
	static {
		mapFirstMale = new HashMap<String, Name>();
		mapFirstFemale = new HashMap<String, Name>();
		mapLast = new HashMap<String, Name>();
	}

	public static List<Name> firstMale;
	public static List<Name> middleMale;
	public static List<Name> firstFemale;
	public static List<Name> middleFemale;
	public static List<Name> last;
	public static List<Name> prefixes; 
	public static List<Name> firsts;
	
	public static List<Name> all;
	static {
		prefixes = new ArrayList<Name>();
		String[] p = new String[] { "AMB", "AMBASSADOR", "ATTORNEY", "ATTY",
				"CAPT", "CAPTAIN", "CMDR", "COACH", "COL", "COLONEL",
				"COMMANDER", "CORPORAL", "CPL", "DOCTOR", "DR", "FATHER", "FR",
				"GEN", "GENERAL", "GOV", "GOVERNOR", "HON", "HONORABLE",
				"LIEUTENANT", "LIEUTENANTCOLONEL", "LT", "LTCOL", "MAJ",
				"MAJOR", "MASTER", "MISS", "MISTER", "MR", "MRS", "MS", "OFC",
				"OFFICER", "PRES", "PRESIDENT", "PRIVATE", "PROF", "PROFESSOR",
				"PVT", "REP", "REPRESENTATIVE", "REV", "REVEREND", "SEC",
				"SECRETARY", "SEN", "SENATOR", "SERGEANT", "SGT", "SUPT",
				"TREAS", "TREASURER" };
		for (String prefix : p) {
			Name name = new Name(prefix);
			name.prefix = true;
			prefixes.add(name);
		}
	}
	static String alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static List<Name> suffixes;
	//public static String[] SUFFIXES = new String[] { "MD", "PHD", "JR", "JUNIOR", "SR", "SENIOR", "I", "II", "III", "IV", "V", "VI" }; 
	public static String[] SUFFIXES = new String[] { "MD", "PHD", "JR", "JUNIOR", "SR", "SENIOR", "III", "IV"};
	static {
		suffixes = new ArrayList<Name>();
		String[] s = SUFFIXES;
		for (String suffix : s) {
			Name name = new Name(suffix);
			name.suffix = true;
			suffixes.add(name);
		}
	}
	
	/** add initials to the given list of names */
	static void addInitials(List<Name> names) {
		if (names == null) return;
		for (int i=0; i<alpha.length(); i++) {
			char ch = alpha.charAt(i);
			names.add(new Name(""+ch));
		}
	}
	
	/** read all the Census names files into memory */
	public static List<Name> readFirstMale(int max) {
		firstMale = read("dist.male.first.txt", NameType.FIRSTMALE, max);
		return firstMale;
	}
	public static List<Name> readFirstMale() {
		return readFirstMale(-1);
	}
	
	public static List<Name> readFirstFemale(int max) {
		firstFemale = read("dist.female.first.txt", NameType.FIRSTFEMALE, max);
		return firstFemale;
	}
	public static List<Name> readFirstFemale() {
		return readFirstFemale(-1);
	}
	
	public static List<Name> readLast(int max) {
		last = read("dist.all.last.txt", NameType.LAST, max);
		return last;
	}
	public static List<Name> readLast() {
		return readLast(-1);
	}
	
	public static void init() {
		init(-1);
	}
	
	public static void init(int MAX) {
		readFirstMale(MAX);
		readFirstFemale(MAX);
		readLast(MAX);
		
		all = new ArrayList<Name>();
		all.addAll(firstMale);
		all.addAll(firstFemale);
		all.addAll(last);
	}
	public static List<Name> read(String fileName, NameType type) {
		return read(fileName, type, -1);
	}
	public static List<Name> read(String fileName, NameType type, int max) {
		int total = 0;
		List<Name> names = new ArrayList<Name>();
		List<String> lines = FileUtil.loadFrom(PREFIX + "/" + fileName);
		for (String line : lines) {
			Name name = nameFrom(line);
			name.type = type;
			names.add(name);
			//System.out.println(name);
			mapAdd(name);
			total++;
			if (max > -1 && total == max) break;
		}
		
		return names;
	}
	
	
	static Map<String, Name> mapFor(NameType type) {
		if (type == NameType.FIRSTMALE) return mapFirstMale;
		if (type == NameType.FIRSTFEMALE) return mapFirstFemale;
		if (type == NameType.MIDDLEMALE) return mapFirstMale;
		if (type == NameType.MIDDLEFEMALE) return mapFirstFemale;
		if (type == NameType.LAST) return mapLast;
		return null;
	}
	static void mapAdd(Name name1) {
		Map<String, Name> map = mapFor(name1.type);
		Name name2 = map.get(name1.name.toLowerCase());
		
		if (name2 == null) name2 = name1;
		else if (name1.frequency > name2.frequency)
			name2 = name1;
		map.put(name2.name.toLowerCase(), name2);
	}

	
	/** return the max score found */
	public static float score(String name) {
		NameType[] types = new NameType[] { NameType.FIRSTMALE,
				NameType.FIRSTFEMALE, NameType.MIDDLEMALE,
				NameType.MIDDLEFEMALE, NameType.LAST, NameType.PREFIX,
				NameType.SUFFIX };
		
		float score = 0;
		if (name == null) return score;
		for (NameType type : types) {
			if (mapFor(type) == null) continue;
			Name n = mapFor(type).get(name.toLowerCase());
			if (n == null) continue;
			score = Math.max(score, n.frequency);
		}
		return score;
	}
	
	/** composite score */
	public static float score(String[] names) {
		float result = 1;
		for (String name : names) result *= (1+score(name));
		return result;
	}

	public static float score(String name, NameType type) {
		if (name == null) return 0;
		System.out.println(type);
		System.out.println(mapFor(type));
		System.out.println(name);
		try {
			Name n = mapFor(type).get(name.toLowerCase());
			if (n == null) return 0;
			return n.frequency;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	public static Name nameFrom(String line) {
		Name name = new Name();
		String[] vals = valsFrom(line);
		name.name = vals[0];
		name.frequency = Float.valueOf(vals[1]);
		name.frequencyCumulative = Float.valueOf(vals[2]);
		name.rank = Integer.valueOf(vals[3]);
		return name;
	}
	
	public static String[] valsFrom(String line) {
		String[] split = line.split(" ");
		String[] vals = new String[4];
		int i=0;
		for (String s : split) {
			if ("".equals(s)) continue;
			vals[i++] = s;
		}
		return vals;
	}
	
	/** do an anagram search for all combinations of first/last names in the given string.  permit at most max letters for a middle initial. */
	public static void anagramSearch(String str, int max) {
		List<Name> firstMale = readFirstMale();
		List<Name> firstFemale = readFirstFemale();
		List<Name> last = readLast();

		for (Name l : last) {
			if (Anagrams.anagram(l.name, str)) {
				for (Name f : firstMale) {
					String n = l.name + f.name;
					if (n.length() < str.length()-max) // only permit a single wildcard for the middle initial 
						continue;
					if (Anagrams.anagram(n, str)) {
						String middle = Anagrams.leftover(n, str);
						String m = "";
						if (middle.length() > 0) {
							for (int i=0; i<middle.length(); i++) {
								m += middle.charAt(i);
								if (i < middle.length()-1) m += " ";
							}
						}
						System.out.println((l.rank+f.rank) + ", " + (n.length()) + ", " + f.name + " " + m + " " + l.name + ", " + l.rank + ", " + f.rank + ", male");
					}
				}
				for (Name f : firstFemale) {
					String n = l.name + f.name;
					if (n.length() < str.length()-max) // only permit a single wildcard for the middle initial 
						continue;
					if (Anagrams.anagram(l.name+f.name, str)) {
						String middle = Anagrams.leftover(n, str);
						String m = "";
						if (middle.length() > 0) {
							for (int i=0; i<middle.length(); i++) {
								m += middle.charAt(i);
								if (i < middle.length()-1) m += " ";
							}
						}
						System.out.println((l.rank+f.rank) + ", " + (n.length()) + ", " + f.name + " " + m + " " + l.name + ", " + l.rank + ", " + f.rank + ", female");
					}
				}
			}
		}
	}

	/** http://www.zodiackillersite.com/viewtopic.php?p=47578#p47578 */
	public static void anagramSearchZ408Last18() {
		String str = "EBEORIETEMETHHPITI";
		List<Name> firstMale = readFirstMale();
		List<Name> firstFemale = readFirstFemale();
		
		List<Name> last = readLast();

		for (Name l : last) {
			if (Anagrams.anagram(l.name, str)) {
				for (Name f : firstMale) {
					for (Name m : firstMale) {
					String n = l.name + f.name + m.name;
					//if (n.length() < str.length()) // only permit a single wildcard for the middle initial 
					//	continue;
					if (Anagrams.anagram(n, str)) {
						System.out.println((l.rank+f.rank) + " " + (n.length()) + " " + f.name + " " + m.name + " " + l.name + " " + f.rank + " " + m.rank + " " + l.rank + " male " + Anagrams.leftover(n, str));
					}
				}
				}
				for (Name f : firstFemale) {
					for (Name m : firstFemale) {
					String n = l.name + f.name + m.name;
					//if (n.length() < str.length()) // only permit a single wildcard for the middle initial 
					//	continue;
					if (Anagrams.anagram(n, str)) {
						System.out.println((l.rank+f.rank) + " " + (n.length()) + " " + f.name + " " + m.name + " " + l.name + " " + f.rank + " " + m.rank + " " + l.rank + " female " + Anagrams.leftover(n, str));
					}
				}
				}
			}
		}
	}
	public static void anagramSearchZ408Last18_2() {
		String str = "EBEORIETEMETHHPITI";
		List<Name> names = readFirstMale();
		names.addAll(readFirstFemale());
		names.addAll(readLast());
		List<Name> matches = new ArrayList<Name>();
		for (Name name : names) {
			if (Anagrams.anagram(name.name, str, false)) {
				System.out.println(name.name.length() + " " + name.name + " " + Anagrams.leftover(name.name, str));
				matches.add(name);
			}
		}
		// all combinations within matches 
		for (int i=0; i<matches.size()-1; i++) {
			for (int j=i+1; j<matches.size(); j++) {
				Name name1 = matches.get(i);
				Name name2 = matches.get(j);
				String combined = name1.name + name2.name;
				float score = score(new String[] {name1.name, name2.name});
				if (Anagrams.anagram(combined, str, false)) {
					System.out.println(combined.length() + " " + score + " " + name1.name + " " + name2.name + " " + Anagrams.leftover(combined, str));
				}
			}
		}
		
//		List<Name> firstFemale = readFirstFemale();
//		
//		List<Name> last = readLast();
//
//		for (Name l : last) {
//			if (Anagrams.anagram(l.name, str)) {
//				for (Name f : firstMale) {
//					for (Name m : firstMale) {
//					String n = l.name + f.name + m.name;
//					//if (n.length() < str.length()) // only permit a single wildcard for the middle initial 
//					//	continue;
//					if (Anagrams.anagram(n, str)) {
//						System.out.println((l.rank+f.rank) + " " + (n.length()) + " " + f.name + " " + m.name + " " + l.name + " " + f.rank + " " + m.rank + " " + l.rank + " male " + Anagrams.leftover(n, str));
//					}
//				}
//				}
//				for (Name f : firstFemale) {
//					for (Name m : firstFemale) {
//					String n = l.name + f.name + m.name;
//					//if (n.length() < str.length()) // only permit a single wildcard for the middle initial 
//					//	continue;
//					if (Anagrams.anagram(n, str)) {
//						System.out.println((l.rank+f.rank) + " " + (n.length()) + " " + f.name + " " + m.name + " " + l.name + " " + f.rank + " " + m.rank + " " + l.rank + " female " + Anagrams.leftover(n, str));
//					}
//				}
//				}
//			}
//		}
	}

	/** http://zodiackillersite.com/viewtopic.php?p=47725#p47725 */
	public static void scytaleSearch() {
		List<Name> firstMale = readFirstMale();
		List<Name> last = readLast();
		
		char[] initials = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
		List<Name> middles = new ArrayList<Name>();
		for (char ch : initials) {
			Name n = new Name();
			n.name = ""+ch;
			middles.add(n);
		}
		Name blank = new Name();
		blank.name = "";
		middles.add(blank);
		middles.addAll(firstMale);

		for (Name l : last) {
			if (l.name.length() > 13) continue;
			for (Name f : firstMale) {
				if (l.name.length() + f.name.length() > 13) continue;
				
				for (Name m : middles) {
					if (m.name.equals(f.name)) continue;
					String n = f.name + m.name + l.name;
					if (n.length() != 13) continue;
					for (int period=1; period<11; period++) {
						String re = Periods.rewrite3(n, period);
						if (re.charAt(4) == re.charAt(6) && re.charAt(6) == re.charAt(8)) {
							System.out.println(rank(f, m, l) + ", " + f.name + " " + m.name + " " + l.name + ", " + re + ", " + period);
						}
					}
				}
			}
		}
	}
	

	/** output all names that can fit into z13. */
	public static void z13Search() {
		List<Name> middle = new ArrayList<Name>();
		middle.add(new Name(""));
		middle.add(new Name("A"));
		middle.add(new Name("B"));
		middle.add(new Name("C"));
		middle.add(new Name("D"));
		middle.add(new Name("E"));
		middle.add(new Name("F"));
		middle.add(new Name("G"));
		middle.add(new Name("H"));
		middle.add(new Name("I"));
		middle.add(new Name("J"));
		middle.add(new Name("K"));
		middle.add(new Name("L"));
		middle.add(new Name("M"));
		middle.add(new Name("N"));
		middle.add(new Name("O"));
		middle.add(new Name("P"));
		middle.add(new Name("Q"));
		middle.add(new Name("R"));
		middle.add(new Name("S"));
		middle.add(new Name("T"));
		middle.add(new Name("U"));
		middle.add(new Name("V"));
		middle.add(new Name("W"));
		middle.add(new Name("X"));
		middle.add(new Name("Y"));
		middle.add(new Name("Z"));
		middle.addAll(readFirstMale());

		int total = 0;
		String cipher = Ciphers.cipher[2].cipher;
		List<Name> first = readFirstMale(5000);
		//first.addAll(readFirstFemale());
		
		List<Name> last = readLast(5000);
		
		for (Name l : last) {
			for (Name f : first) {
				
				/*
				for (Name m : middle) {
					
					String n = l.name + f.name + m.name;
					if (n.length() != cipher.length()) 
						continue;
				
					if (MyNameIs.sameDist(n)) {
						System.out.println(rank(l, f, m) + ", " + f.name + " " + m.name + " " + l.name + ", " + f.rank + ", " + m.rank + ", " + l.rank + ", " + (f.male ? "male" : "female"));
						total++;
						if (total >= 10000000) return;
						if (total % 100000 == 0) System.out.println("TOTAL: " + total);
					}
				
				}
				*/
				String n = l.name + f.name;
				if (n.length() != cipher.length()) 
					continue;

				if (MyNameIs.sameDist(n)) {
					long rank = rank(l, f);
					System.out.println(rank + ", " + f.name + " " + l.name + ", " + f.rank + ", " + l.rank);
					total++;
					//if (total >= 10000000) return;
					if (total % 100000 == 0) System.out.println("TOTAL: " + total);
				}
			}
		}
		
	}
	
	/** generate random common name with the given total length.  MAX is limit loaded per name part.  include prefixes and suffixes. */
	public static List<Name> buildName(int L, int MAX) {
		
		if (firstMale == null || firstMale.isEmpty()) {
			readFirstMale(MAX);
			//firstMale.add(new Name(""));
			//addInitials(firstMale);
			middleMale = new ArrayList<Name>();
			//addInitials(middleMale); // force initials
			middleMale.add(new Name(""));
			readLast(MAX);
			//last.add(new Name(""));
			suffixes.add(new Name(""));
		}
		return buildNameSub(L, MAX, NameType.FIRSTMALE);
	}
	/** generate random common name with the given total length.  name parts returned in random order.  MAX is limit loaded per name part.
	 * 
	 *  */
	public static List<Name> buildName(int L, int MAX, boolean male, boolean female, boolean initial) {
		Random rand = new Random();
		if (firstMale == null || firstMale.isEmpty()) readFirstMale(MAX);
		if (firstFemale == null || firstFemale.isEmpty()) readFirstFemale(MAX);
		if (firsts == null || firsts.isEmpty()) {
			firsts = new ArrayList<Name>();
			if (male) firsts.addAll(firstMale);
			if (female) firsts.addAll(firstFemale);
		}
		if (last == null || last.isEmpty()) readLast(MAX);
		
		List<Name> result = new ArrayList<Name>();
		List<String> choices = new ArrayList<String>();
		choices.add("first");
		choices.add("last");
		if (initial) choices.add("initial");
		boolean usedInitial = false;
		while (true) {
			int len = 0;
			while (len < L) {
				int which = rand.nextInt(choices.size());
				String choice = choices.get(which);
				if (!usedInitial && "initial".equals(choice)) {
					result.add(new Name(firsts.get(rand.nextInt(firsts.size())).name.substring(0, 1)));
					len++;
					usedInitial = true;
				} else if ("first".equals(choice)) {
					Name name = firsts.get(rand.nextInt(firsts.size()));
					result.add(name);
					len+=name.name.length();
				} else if ("last".equals(choice)) {
					Name name = last.get(rand.nextInt(last.size()));
					result.add(name);
					len+=name.name.length();
				}
			}
			if (len == L) {
				return result;
			}
			// otherwise, try again
			if (initial) choices.add("initial");
			result.clear();
		}
		
	}
	
	/** generate random common name with the given total length.  MAX is limit loaded per name part.  include prefixes and suffixes. */
	public static List<Name> buildNameFemale(int L, int MAX) {
		
		if (firstFemale == null || firstFemale.isEmpty()) {
			readFirstFemale(MAX);
			middleFemale = new ArrayList<Name>();
			addInitials(middleFemale); // force initials
			middleFemale.add(new Name(""));
			middleFemale.add(new Name(""));
			middleFemale.add(new Name(""));
			middleFemale.add(new Name(""));
			middleFemale.add(new Name(""));
			middleFemale.add(new Name(""));
			middleFemale.add(new Name(""));
			middleFemale.add(new Name(""));
			middleFemale.add(new Name(""));
			middleFemale.add(new Name(""));
			middleFemale.add(new Name(""));
			middleFemale.add(new Name(""));
			middleFemale.add(new Name(""));
			middleFemale.add(new Name(""));
			middleFemale.add(new Name(""));
			middleFemale.add(new Name(""));
			middleFemale.add(new Name(""));
			middleFemale.add(new Name(""));
			middleFemale.add(new Name(""));
			middleFemale.add(new Name(""));
			middleFemale.add(new Name(""));
			middleFemale.add(new Name(""));
			readLast(MAX);
			suffixes.add(new Name(""));
		}
		
		return buildNameSub(L, MAX, NameType.FIRSTFEMALE);
	}
	
	public static List<Name> buildNameSub(int L, int MAX, NameType firstMiddleType) {
		Random r = new Random(System.nanoTime());
		
		while (true) {
			List<Name> firstList = firstMiddleType == NameType.FIRSTMALE ? firstMale : firstFemale;
			//List<Name> middleList = firstMiddleType == NameType.FIRSTMALE ? middleMale : middleFemale;
			Name first = firstList.get(r.nextInt(firstList.size()));
			//Name first = earl;
			//Name middle = middleList.get(r.nextInt(middleList.size()));
			Name las = last.get(r.nextInt(last.size()));
			//Name suffix = suffixes.get(r.nextInt(suffixes.size()));
			
			//if (first.name.equals(middle.name)) continue;
			
			//String key = first.name + " " + middle.name + " " + las.name + " " + suffix.name;
			int len = first.name.length() + las.name.length();// + suffix.name.length();
			if (L > -1 && len != L) continue;
			//if (seen.contains(key)) continue;
			//seen.add(key);
			List<Name> list = new ArrayList<Name>();
			list.add(first); /*list.add(middle);*/ list.add(las); //list.add(suffix);
			//names.add(list);
			//System.out.println(dump(list));
			return list;
		}
		
		
	}
	
	/** generate random list of R common names of the given total length.  MAX is limit per name part.  include prefixes and suffixes. */
	public static List<List<Name>> buildNames(int R, int L, int MAX) {
		List<List<Name>> names = new ArrayList<List<Name>>();

		// first, middle, last, suffix
		// each part can have an empty element
		// first and middle can each be initials
		
		// male names
		//prefixes.clear();
		//prefixes.add(new Name(""));
		readFirstMale(MAX);
		firstMale.add(new Name(""));
		addInitials(firstMale);
		List<Name> middleMale = firstMale;
		readLast(MAX);
		last.add(new Name(""));
		suffixes.add(new Name(""));
		
		int matches = 0;
		
		Set<String> seen = new HashSet<String>();
		
		Random r = new Random(System.nanoTime());
		while (matches < R) {
			Name first = firstMale.get(r.nextInt(firstMale.size()));
			Name middle = middleMale.get(r.nextInt(middleMale.size()));
			Name las = last.get(r.nextInt(last.size()));
			Name suffix = suffixes.get(r.nextInt(suffixes.size()));
			
			if (first.name.equals(middle.name)) continue;
			
			String key = first.name + " " + middle.name + " " + las.name + " " + suffix.name;
			int len = first.name.length() + middle.name.length() + las.name.length() + suffix.name.length();
			if (len != L) continue;
			if (seen.contains(key)) continue;
			seen.add(key);
			List<Name> list = new ArrayList<Name>();
			list.add(first); list.add(middle); list.add(las); list.add(suffix);
			names.add(list);
			//System.out.println(dump(list));
			matches++;
		}
		
		return names;
	}
	
	
	public static long rank(Name l, Name f) {
		return (1+(long)l.rank)*(1+(long)f.rank);
	}
	public static long rank(Name l, Name f, Name m) {
		return (1+(long)l.rank)*(1+(long)f.rank)*(1+(long)m.rank);
	}
	public static long rank(List<Name> names) {
		long rank = 1;
		if (names == null) return rank;
		for (Name name : names) 
			rank *= (1+(long)name.rank);
		return rank;
	}
	
	public static String dump(List<Name> names) {
		String s = "" + rank(names) + " ";
		for (Name name : names) s += name.name + " ";
		return s;
	}
	
	/** http://www.zodiackillersite.com/viewtopic.php?f=81&t=866&sid=2abfb3d9cd52879b3ea4989acf9c03c5 */
	public static void anagramSearchMorf() {
		String str = "AHNFERELEMNAL";
		List<Name> firstMale = readFirstMale();
		List<Name> firstFemale = readFirstFemale();
		List<Name> last = readLast();

		for (Name l : last) {
			if (l.name.length() < 3) continue;
			String sub = l.name.substring(l.name.length()-3);
			if (sub.contains("N") && sub.contains("A") && sub.contains("M") ) {
				if (Anagrams.anagram(l.name, str)) {
					for (Name f : firstMale) {
						if (!f.name.startsWith("A")) continue;
						if (Anagrams.anagram(l.name+f.name, str)) {
							if (l.name.length() + f.name.length() >= 12)
								System.out.println((l.rank+f.rank) + ", " + (l.name.length() + f.name.length()) + ", " + f.name + " " + Anagrams.leftover(l.name+f.name,str) + " " + l.name + ", " + l.rank + ", " + f.rank + ", male");
						}
					}
					for (Name f : firstFemale) {
						if (!f.name.startsWith("A")) continue;
						if (Anagrams.anagram(l.name+f.name, str)) {
							if (l.name.length() + f.name.length() >= 12)
								System.out.println((l.rank+f.rank) + ", " + (l.name.length() + f.name.length()) + ", " + f.name + " " + Anagrams.leftover(l.name+f.name,str) + " " + l.name + ", " + l.rank + ", " + f.rank + ", female");
						}
					}
				}
			}
				
		}
	}
	
	/* http://zodiackiller.fr.yuku.com/topic/7002/Letters-in-names-in-Alphabetical-sequence */
	public static void alphaSeq() {
		List<Name> names = readLast().subList(0, 50000);
		System.out.println(names.size());
		
		int count= 0;
		for (Name name : names) {
			char c1 = name.name.charAt(0);
			char c2 = name.name.charAt(1);
			int diff = Math.abs(c1-c2);
			if (diff == 1) {
				System.out.println(name.name);
				count++;
			}
		}
		System.out.println(count);
		
		Random r = new Random();
		
		int met = 0;
		for (int i=0; i<1000000; i++) {
			Set<String> selection = new HashSet<String>();
			while (selection.size() < 10) {
				Name name = names.get(r.nextInt(names.size()));
				selection.add(name.name);
			}
			int num = 0;
			for (String name : selection) {
				char c1 = name.charAt(0);
				char c2 = name.charAt(1);
				int diff = Math.abs(c1-c2);
				if (diff == 1) {
					num++;
				}
			}
			if (num > 3) met++;
		}
		System.out.println("met " + met + " out of 1,000,000");
		
	}
	
	/** shuffle cipher and print out any that have names that appear */
	public static void shuffleNamesTest(String cipher, int min, boolean show) {
		int hits = 0;
		int total = 0;

		StatsWrapper statsMale = new StatsWrapper();
		StatsWrapper statsFemale = new StatsWrapper();
		StatsWrapper statsLast = new StatsWrapper();
		
		statsMale.actual = 1;
		statsFemale.actual = 1;
		statsLast.actual = 1;
		
		init(1000);
		while (true) {
			Set<String> set = new HashSet<String>(); 
			total++;
			cipher = CipherTransformations.shuffle(cipher);
			
			int count;
			
			count = 0;
			for (Name name : firstMale) {
				if (name.name.length() >= min && cipher.contains(name.name)) {
					hits++;
					count++;
//					System.out.println("firstMale	" + name.frequency + " " + hits + " " + total + " " + name + " " + cipher);
					set.add(name.name);
				}
			}
			statsMale.addValue(count);
			count = 0;
			for (Name name : firstFemale) {
				if (name.name.length() >= min && cipher.contains(name.name)) {
					hits++;
					count++;
//					System.out.println("firstFemale	" + name.frequency + " " + hits + " " + total + " " + name + " " + cipher);
					set.add(name.name);
				}
			}
			statsFemale.addValue(count);
			count = 0;
			for (Name name : last) {
				if (name.name.length() >= min && cipher.contains(name.name)) {
					hits++;
					count++;
//					System.out.println("last	" + name.frequency + " " + hits + " " + total + " " + name + " " + cipher);
					set.add(name.name);
				}
			}
			statsLast.addValue(count);
			if (show && set.size() > 1) {
				System.out.println(set.size() + " names: " + set + ", " + cipher);
			}
			if (total % 100000 == 0) {
				System.out.println(total + " shuffles");
				statsMale.output();
				statsFemale.output();
				statsLast.output();
			}
		}
	}
	/** look for random names that fit z13.  treat circled 8s as wildcards (http://www.zodiackillersite.com/viewtopic.php?t=4281&p=69081#p69081) */
	public static void z13Search2() {
		WordFrequencies.init();
		Set<String> seen = new HashSet<String>(); 
		while (true) {
			List<Name> name = buildName(13, 10000, true, false, true);
			StringBuffer sb = flatten(name, true);
			StringBuffer sbWithoutSpaces = flatten(name, false);
			if (seen.contains(sb.toString())) continue;
			if (MyNameIs.fitSub(sbWithoutSpaces, true)) {
				System.out.println(Name.score(name) + " " + sb);
				seen.add(sb.toString());
			}
		}
	}

	/** look for random names that fit z13.  use backwards/embedded first name method described here: http://www.zodiackillersite.com/viewtopic.php?p=69126#p69126 */
	public static void z13Search3() {
		WordFrequencies.init();
		Set<String> seen = new HashSet<String>(); 
		while (true) {
			List<Name> name = buildName(13, 10000, true, true, false);
			if (name.size() != 2) continue;
			int len1 = name.get(0).name.length();
			int len2 = name.get(1).name.length();
			if (len1 == 6 && len2 == 7 || len2 == 6 && len1 == 7) {
				
				Name n1;
				Name n2;
				if (len1 == 7) {
					n1 = name.get(0);
					n2 = name.get(1);
				}
				else {
					n1 = name.get(1);
					n2 = name.get(0);
				}
				
				StringBuffer sb = new StringBuffer(n1.name);
				StringBuffer rev = new StringBuffer(n2.name).reverse();
				sb.insert(0, rev.substring(0, 3));
				sb.append(rev.substring(3));
				
				if (seen.contains(sb.toString())) continue;
				if (MyNameIs.fitSub(sb, false)) {
					System.out.println(Name.score(name) + " " + sb + " [" + n1.name + " " + n2.name + "]");
					seen.add(sb.toString());
				}
			} 
//			StringBuffer sb = flatten(name, true);
//			StringBuffer sbWithoutSpaces = flatten(name, false);
//			if (seen.contains(sb.toString())) continue;
//			if (MyNameIs.fitSub(sbWithoutSpaces, true)) {
//				System.out.println(Name.score(name) + " " + sb);
//				seen.add(sb.toString());
//			}
		}
	}
	/** find all full names that fit z13 */
	public static void z13Search4() {
		init();
		for (Name n : last) {
			if (n.name.length() != 13) continue;
			if (MyNameIs.sameDist(n.name)) 
				System.out.println(n.name);
		}
	}
	
	public static StringBuffer flatten(List<Name> names, boolean keepSpaces) {
		if (names == null) return null;
		StringBuffer result = new StringBuffer();
		for (Name name : names) {
			if (keepSpaces && result.length() > 0) result.append(" ");
			result.append(name.name);
		}
		return result;
	}
	public static void testBuildName() {
		WordFrequencies.init();
		for (int i=0; i<1000; i++) {
			List<Name> name = buildName(13, 1000, true, true, true);
			System.out.println(Name.score(name) + " " + flatten(name, true));
		}
	}
	static void z13Search2Process() {
		readFirstMale();
		readFirstFemale();
		readLast();
		System.out.println(mapFirstMale.size());
		for (int i=0; i<6; i++) {
			List<String> lines = FileUtil.loadFrom("/Users/doranchak/projects/zodiac/MyNameIs" + i + ".txt");
			for (String line : lines) {
				if (line.startsWith("Loading from")) continue;
				line = line.substring(line.indexOf(" ")+1);
				String[] split = line.split(" ");
				float score = 1;
				boolean initial = false;
				boolean top = true;
				for (String s : split) {
					if (s.length() < 2) {
						initial = true;
						continue;
					}
					score *= frequency(s);
					Name n = mapFirstMale.get(s.toLowerCase());
					if (n == null) n = mapFirstFemale.get(s.toLowerCase()); 
					if (n == null) n = mapLast.get(s.toLowerCase());
					if (n.rank > 1000) top = false;
				}
				if (initial && top && split[split.length-1].length() > 1 && split.length < 4) 
					if (split[1].length() == 1)
					System.out.println(score + " " + line);
			}
		}
	}
	
	/** return max frequency of the given name part */
	public static float frequency(String name) {
		float freq = 0;
		Name n;
		n = mapFirstMale.get(name.toLowerCase());
//		System.out.println(name + " map " + n);
		if (n != null) freq = Math.max(freq, n.frequency);
		n = mapFirstFemale.get(name.toLowerCase());
		if (n != null) freq = Math.max(freq, n.frequency);
		n = mapLast.get(name.toLowerCase());
		if (n != null) freq = Math.max(freq, n.frequency);
//		System.out.println(name + " freq " + freq);
		return freq;
	}

	// Method to sort a string alphabetically
	public static String sortString(String inputString) {
		// convert input string to char array
		char tempArray[] = inputString.toCharArray();

		// sort tempArray
		Arrays.sort(tempArray);

		// return new sorted string
		return new String(tempArray);
	}

	/** find random names that can be found as anagrams of the last 18 of z408 */
	public static void z408last18search() {
		init();
		String last18 = "EBEORIETEMETHHPITI";
		String last18sorted = sortString(last18);
		
		Set<String> seen = new HashSet<String>();
		while (true) {
			List<Name> list = buildName(16, Integer.MAX_VALUE);
			String name = "";
			for (Name n : list) 
				name += n.name;
			String sorted = sortString(name);
			int diff = EditDistance.LD(last18sorted,  sorted);
			if (diff < 5) {
				if (seen.contains(name)) continue;
				seen.add(name);
				System.out.println(diff + "	" + Name.score(list) + "	" + list);
			}
		}
	}
	
	/** search for names that are in rearrangements of the last 18 of z408 (or any given string)
	 * approach:
	 * - pick random name part that can be found among the 18 letters
	 * - remove those letters from the 18
	 * - pick another random name part that can be found the remaining letters
	 * - continue until no letters are left or no more names can be generated
	 */
	public static void findNames(String source) {
		init(2000);
		Random random = new Random();
		
		while (true) {
			StringBuffer sb = new StringBuffer(source);
			String result = "";
			List<Name> selected = new ArrayList<Name>();
			while (true) {
				if (sb.length() < 1) break; // current string is empty
				// generate all names that can be found in the current string
				List<Name> names = new ArrayList<Name>();
				for (Name name : all) {
					if (Anagrams.anagram(name.name, sb.toString())) {
						names.add(name);
					}
				}
				if (names.isEmpty()) {
					break;
				}
				// pick a name at random
				Name part = names.get(random.nextInt(names.size()));
				selected.add(part);
				result += part.name + " ";
				// remove it from the current string
				delete(sb, part.name);
				
			}
			System.out.println(sb.length() + "	" + Name.score(selected) + "	" + result + " " + sb);
		}
	}

	/** search for names that are in rearrangements of the last 18 of z408 (or any given string)
	 * allow for a maximum edit distance so errors can be present
	 */
	public static void findNames2(String source, int maxEditDistance, int minLength, int maxLength) {
		init();
		Random random = new Random();
		
		String sourceSorted = sortString(source);
		while (true) {
			
			String nameWithoutSpaces = "";
			String nameWithSpaces = "";
			List<Name> names = new ArrayList<Name>();
			while (nameWithoutSpaces.length() <= minLength) {
				Name name = all.get(random.nextInt(all.size()));
				nameWithoutSpaces += name.name;
				nameWithSpaces += name.name + " ";
				names.add(name);
			}
			if (nameWithoutSpaces.length() > maxLength) continue;
			String nameSorted = sortString(nameWithoutSpaces);
			int editDistance = EditDistance.LD(sourceSorted, nameSorted);
			if (editDistance > maxEditDistance) continue;
			
			System.out.println(editDistance + "	" + Name.score(names) + "	" + nameWithSpaces);
		}
	}

	/** search for names in z18 that are similar to the format "robert emmet the hippie".
	 * allow for a max edit distance.
	 */
	public static void findNames3(String source, int maxEditDistance) {
		init();
		WordFrequencies.init();
		System.out.println("Begin");
		Random random = new Random();
		
		String sourceSorted = sortString(source);
		
		String[] words = new String[] {
				"",
				"THE",
				"IS",
				"A",
				"THAT",
				"THIS"
		};
		while (true) {

			Name first = all.get(random.nextInt(all.size()));
			Name last = all.get(random.nextInt(all.size()));
			List<Name> names = new ArrayList<Name>();
			names.add(first);
			names.add(last);
			String middle = "";
			if (random.nextBoolean()) {
				Name mid = all.get(random.nextInt(all.size()));
				middle = mid.name.substring(0,1);
			}
			
			String word1 = words[random.nextInt(words.length)];
			String word2 = WordFrequencies.randomWord();
			
			String combined = first.name + middle + last.name + word1 + word2;
			String withSpaces = first.name + " " + middle + " " + last.name + " " + word1 + " " + word2;
			String nameSorted = sortString(combined);
			int editDistance = EditDistance.LD(sourceSorted, nameSorted);
			if (editDistance > maxEditDistance) continue;
			
			float score = Name.score(names);
			score *= WordFrequencies.percentile(word2);
			
			System.out.println(editDistance + "	" + score + "	" + withSpaces);
		}
	}
	
	// remove all letters that appear in str from sb  
	public static void delete(StringBuffer sb, String str) {
		for (int i=0; i<str.length(); i++) {
			for (int j=0; j<sb.length(); j++) {
				if (sb.charAt(j) == str.charAt(i)) {
					sb.deleteCharAt(j);
					break;
				}
			}
		}
	}
	
	// generate random 13-letter names that fit the distribution of symbols in Z13 
	public static void buildNamesZ13(int max) {
		for (int i=0; i<max; i++) {
			List<Name> list = buildName(13, 1000);
			float score = 0;
			String full = "";
			String fullWithSpaces = "";
			for (Name name  :list) {
				full += name.name;
				fullWithSpaces += name.name + " ";
				score += name.frequency;
			}
			if (MyNameIs.sameDist(full)) {
				System.out.println(score + " " + fullWithSpaces + " " + list);
			}
			
		}
	}

	// extract as many random name parts from the given string until no more names can be found
	public static void nameStuffer(String inputString) {
		nameStuffer(inputString, Integer.MAX_VALUE);
	}
	public static void nameStuffer(String inputString, int numNames) {
		inputString = inputString.toUpperCase();
		Random rand = new Random();
		List<Name> firstMale = readFirstMale(1000);
		List<Name> firstFemale = readFirstFemale(1000);
		List<Name> last = readLast(2000);
		// System.out.println("Name list sizes: " + firstMale.size() + ", " + firstFemale.size() + ", " + last.size());

		// lookup tables for rank/freq info
		Map<String, Name> mapFirstMale = new HashMap<String, Name>();
		firstMale.forEach((name) -> mapFirstMale.put(name.name, name));
		Map<String, Name> mapFirstFemale = new HashMap<String, Name>();
		firstFemale.forEach((name) -> mapFirstFemale.put(name.name, name));
		Map<String, Name> mapLast = new HashMap<String, Name>();
		last.forEach((name) -> mapLast.put(name.name, name));

		for (int i=0; i<numNames; i++) {
			List<Name> namePoolFirst = new ArrayList<Name>();
			List<Name> namePoolLast = new ArrayList<Name>();
			boolean female = rand.nextBoolean();
			if (female) addToPool(firstFemale, namePoolFirst, inputString);
			else addToPool(firstMale, namePoolFirst, inputString);
			addToPool(last, namePoolLast, inputString);
			// System.out.println("Pool init: " + female + " " + namePoolFirst.size() + " " + namePoolLast.size());
			String remainingString = inputString;

			List<Name> names = new ArrayList<Name>();
			// Map<String, Name> mapFirst = female ? mapFirstFemale : mapFirstMale;
			// List<String> first = female ? firstFemale : firstMale;
			while (remainingString.length() > 0 && (namePoolFirst.size() > 0 || namePoolLast.size() > 0)) {
				// System.out.println(" - pool status: " + female + " " + namePoolFirst.size() + " " + namePoolLast.size());
				Name namePart;
				boolean which; // if true, use last names, otherwise first names
				if (namePoolFirst.size() == 0) which = true;
				else if (namePoolLast.size() == 0) which = false;
				else which = rand.nextBoolean();
				List<Name> namePool = which ? namePoolLast : namePoolFirst;
				namePart = namePool.get(rand.nextInt(namePool.size()));
				names.add(namePart);
				remainingString = Anagrams.leftover(namePart.name, remainingString);
				// System.out.println(" - " + remainingString + TAB + names);
				removeFromPool(namePoolFirst, remainingString);
				removeFromPool(namePoolLast, remainingString);
			}
			float score = Name.score(names) / names.size();
			int length = 0;
			String flat = "";
			for (Name name : names) {
				length += name.name.length();
				if (flat.length() > 0) flat += " ";
				flat += name.name;
			}
			System.out.println(length + TAB + score + TAB + flat);
		}
	}
	// add names to the name pool if they appears as anagrams in the given string
	public static void addToPool(List<Name> names, List<Name> namePool, String inputString) {
		names.forEach((name) -> {
			if (Anagrams.anagram(name.name, inputString)) namePool.add(name);
		});
	}
	// remove names from pool if they don't appear as anagrams in the given string
	public static void removeFromPool(List<Name> namePool, String inputString) {
		// System.out.println("New input string: " + inputString + ", current size " + namePool.size());
		// String removed = "";
		// String kept = "";
		for (int i=namePool.size()-1; i>=0; i--)
			if (!Anagrams.anagram(namePool.get(i).name, inputString)) {
				// removed += namePool.get(i).name + " ";
				namePool.remove(i);				
			} 
			// else kept += namePool.get(i).name + " ";
		// System.out.println("Removed " + removed);
		// System.out.println("Kept " + kept);
		// System.out.println("New size " + namePool.size());
	}
	
	
	public static void main(String[] args) {
//		System.out.println(readLast());
		//anagramSearch("APETERSPLANTE");
//		anagramSearch("DEAROLDPOOPFACE", 10);
		//anagramSearch("EBEORIETEMETHHPITI", 1);

		//anagramSearchZ408Last18();
		//scytaleSearch();
		//shuffleNamesTest(Ciphers.Z340, 3, false);
		//anagramSearchMorf();
		//anagramSearch(Ciphers.cipher[2].cipher, 10);
//		z13Search();
		//init();
		//System.out.println(score("ALICE"));
		//alphaSeq();
//		List<Name> name = buildName(13, 20000);
//		for (Name n : name) System.out.println(n);
//		testBuildName();
//		z13Search3();
		
//		z13Search2Process();
//		z408last18search();
//		anagramSearchZ408Last18_2();
//		findNames("EBEORIETEMETHHPITI");
//		findNames2("EBEORIETEMETHHPITI", 3, 15, 21);
//		findNames3("EBEORIETEMETHHPITI", 3);
//		z13Search4();
//		System.out.println(buildName(13, -1));
		buildNamesZ13(1000000);
	}
}
