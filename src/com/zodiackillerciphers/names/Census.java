package com.zodiackillerciphers.names;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.zodiackillerciphers.anagram.Anagrams;
import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.io.FileUtil;
import com.zodiackillerciphers.ngrams.Periods;
import com.zodiackillerciphers.old.MyNameIs;

public class Census {
	
	public static String PREFIX = "/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/census-names";

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

	
	public static float score(String name, NameType type) {
		if (name == null) return 0;
		Name n = mapFor(type).get(name.toLowerCase());
		if (n == null) return 0;
		return n.frequency;
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
	
	public static void main(String[] args) {
		//readLast();
		//anagramSearch("APETERSPLANTE");
		//anagramSearch("EBEORIETEMETHHPITI", 1);
		//anagramSearchZ408Last18();
		scytaleSearch();
		//anagramSearchMorf();
		//anagramSearch(Ciphers.cipher[2].cipher, 10);
		//z13Search();
		//init();
		//System.out.println(score("ALICE"));
		//alphaSeq();
		//List<Name> name = buildName(-1, 50);
		//for (Name n : name) System.out.println(n);
	}
}
