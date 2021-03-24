package com.zodiackillerciphers.ciphers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.io.FileUtil;
import com.zodiackillerciphers.names.Census;

public class AlbanyLetter {
	
	/** name part of cipher has 12 symbols. */
	public static String s0 = "CN";
	public static String s1 = "GHNO";
	public static String s2 = "GHNO";
	public static String s3 = "DIRST";
	public static String s3b = "EC";
	public static String s4 = "DRST";
	public static String s5 = "GHNO";
	
	public static String[] matches = new String[] {
		"C",
		"O",
		"N",
		"?",
		"?",
		"E",
		"?",
		//"HO",
		"?",
		"E",
		"N",
		"L",
		"Y"
	};
	
	public static String alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	public static String[] middleInitials = new String[] {
		"", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
	};
	
	static Map<String, List<String>> namesMap;
	
	static Set<String> first; 
	static Set<String> last; 
	static Set<String> all; 
	
	public static void initNames() {
		namesMap = new HashMap<String, List<String>>();
		first = new HashSet<String>();
		last = new HashSet<String>();
		all = new HashSet<String>();
		
		List<String> names = FileUtil.loadFrom("/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/census-names/dist.female.first.txt");
		for (String name : names) {
			String prefix = name.substring(0,3);
			List<String> val = namesMap.get(prefix);
			if (val == null) val = new ArrayList<String>();
			String s = name.split(" ")[0];
			val.add(s);
			first.add(s);
			last.add(s);
			all.add(s);
			namesMap.put(prefix, val);
		}
		names = FileUtil.loadFrom("/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/census-names/dist.male.first.txt");
		for (String name : names) {
			String prefix = name.substring(0,3);
			List<String> val = namesMap.get(prefix);
			if (val == null) val = new ArrayList<String>();
			String s = name.split(" ")[0];
			val.add(s);
			first.add(s);
			last.add(s);
			all.add(s);
			namesMap.put(prefix, val);
		}
		System.out.println(namesMap.size());
		System.out.println(first.size());
		
		names = FileUtil.loadFrom("/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/docs/census-names/dist.all.last.txt");
		for (String name : names) {
			last.add(name.split(" ")[0]);
			first.add(name.split(" ")[0]);
			String s = name.split(" ")[0];
			first.add(s);
			last.add(s);
			all.add(s);
		}
		System.out.println(last.size());
		
	}
	public static void go() {
		
		initNames();
		
		for (int a0=0; a0<s0.length(); a0++) {
			for (int a1=0; a1<s1.length(); a1++) {
				for (int a2=0; a2<s2.length(); a2++) {
					for (int a3=0; a3<s3.length(); a3++) {
						for (int a3b=0; a3b<s3b.length(); a3b++) {
							for (int a4=0; a4<s4.length(); a4++) {
								for (int a5=0; a5<s5.length(); a5++) {
									char c0 = s0.charAt(a0);
									char c1 = s1.charAt(a1);
									char c2 = s2.charAt(a2);
									char c3 = s3.charAt(a3);
									char c3b = s3b.charAt(a3b);
									char c4 = s4.charAt(a4);
									char c5 = s5.charAt(a5);
									
									String name = "" + c0 + c1 + c2 + "_" + c3 + c3b + c4 + c5 + "ENLY";
									String match = match(name);
									if (match != null && match.length() > 0)
										System.out.println(name + " " + match(name));
								}
							}
						}
					}
				}
			}
			
		}
	}
	
	/** another search based on reduced search space due to better scan */
	public static void go2() {
		Census.init();
		initNames();

		int total = 0;
		
		System.out.println("name name");
		for (String firstName : first) {
			total++;
//			if (total % 10000 == 0) System.out.println(total+"...");
			
			String name = firstName;
			if (match2(name)) {
				for (String lastName : last) {
					name = firstName + lastName;
					if (match2(name)) {
						if (name.length() == matches.length) {
							float score = (1+Census.score(firstName))*(1+Census.score(lastName));
							System.out.println(score + " " + firstName + " " + lastName);
						}
					}
				}
			}
		}

		System.out.println("name x name");
		for (String firstName : first) {
			total++;
//			if (total % 10000 == 0) System.out.println(total+"...");
			
			String name = firstName;
			if (match2(name)) {
				for (String lastName : last) {
					
					for (int i=0; i<alpha.length(); i++) {
						char middle = alpha.charAt(i);
						name = firstName + middle + lastName;
						if (match2(name)) {
							if (name.length() == matches.length) {
								float score = (1+Census.score(firstName))*(1+Census.score(lastName));
								System.out.println(score + " " + firstName + " " + middle + " " + lastName);
							}
						}
					}
				}
			}
		}

		System.out.println("name name name");
		for (String firstName : first) {
			total++;
//			if (total % 10000 == 0) System.out.println(total+"...");
			
			String name = firstName;
			if (match2(name)) {
				
				for (String middleName : first) {
					name = firstName + middleName;
					if (match2(name)) {
						for (String lastName : last) {
							
							name = firstName + middleName + lastName;
							if (match2(name)) {
								if (name.length() == matches.length) { 
									float score = (1+Census.score(firstName))*(1+Census.score(lastName))*(1+Census.score(middleName));
									System.out.println(score + " " + firstName + " " + middleName + " " + lastName);
								}
							}
						}
					}
				}
				
				
			}
		}
		
		
	}
	
	/** slide names to see if single names fit */
	public static void go3() {
		Census.init();
		initNames();

		System.out.println("name");
		for (String firstName : first) {
			for (int i=0; i<matches.length; i++) {
				String name = "";
				for (int j=0; j<i; j++) name += "?";
				name += firstName;
				if (name.length() > matches.length) break;
				if (match2(name)) {
					float diff = firstName.length();
					float score = (1+Census.score(firstName))*diff;
					System.out.println(score + " " + name);
				}
			}
		}
	}
	
	/** test all name combinations */
	public static void go4() {
		Census.init();
		initNames();
		
		// let's just treat all names as equally applicable as first, middle or last names

		System.out.println("=== first + (middle initial) + last ===");
		for (String firstName : all) {
			for (String middleName : middleInitials) {
				String full = firstName + middleName;
				if (!isValid(full)) continue;
				for (String lastName : all) {
					full = firstName + middleName + lastName;
					if (full.length() != 12) continue;
					if (!isValid(full)) continue;
					float score = Census.score(new String[] {firstName, lastName});
					System.out.println(score + " " + firstName + " " + middleName + " " + lastName);
				}
			}
		}
		
		System.out.println("=== first + middle + last ===");
		for (String firstName : all) {
			if (!isValid(firstName)) continue;
			for (String middleName : all) {
				String full = firstName + middleName;
				if (!isValid(full)) continue;
				for (String lastName : all) {
					full = firstName + middleName + lastName;
					if (full.length() != 12) continue;
					if (!isValid(full)) continue;
					float score = Census.score(new String[] {firstName, middleName, lastName});
					System.out.println(score + " " + firstName + " " + middleName + " " + lastName);
				}
			}
		}

		System.out.println("=== last + first + (middle initial) ===");
		for (String lastName : all) {
			if (!isValid(lastName)) continue;
			for (String firstName : all) {
				String full = lastName + firstName;
				if (!isValid(full)) continue;
				for (String middleName : middleInitials) {
					full = lastName + firstName + middleName;
					if (full.length() != 12) continue;
					if (!isValid(full)) continue;
					float score = Census.score(new String[] {firstName, lastName});
					System.out.println(score + " " + lastName + " " + firstName + " " + middleName);
				}
			}
		}
		
		System.out.println("=== last + first + middle ===");
		for (String lastName : all) {
			if (!isValid(lastName)) continue;
			for (String firstName : all) {
				String full = lastName + firstName;
				if (!isValid(full)) continue;
				for (String middleName : all) {
					full = lastName + firstName + middleName;
					if (full.length() != 12) continue;
					if (!isValid(full)) continue;
					float score = Census.score(new String[] {firstName, middleName, lastName});
					System.out.println(score + " " + lastName + " " + firstName + " " + middleName);
				}
			}
		}
		
		System.out.println("=== (initial) (initial) last ===");
		for (String middleName1 : middleInitials) {
			if (!isValid(middleName1)) continue;
			for (String middleName2 : middleInitials) {
				if (!isValid(middleName1+middleName2)) continue;
				for (String lastName : all) {
					String full = middleName1 + middleName2 + lastName;
					if (full.length() != 12) continue;
					if (!isValid(full)) continue;
					float score = Census.score(new String[] {lastName});
					System.out.println(score + " " + middleName1 + " " + middleName2 + " " + lastName);
				}
			}
		}

		System.out.println("=== (initial) middle last ===");
		for (String middleName1 : middleInitials) {
			if (!isValid(middleName1)) continue;
			for (String middleName2 : all) {
				if (!isValid(middleName1+middleName2)) continue;
				for (String lastName : all) {
					String full = middleName1 + middleName2 + lastName;
					if (full.length() != 12) continue;
					if (!isValid(full)) continue;
					float score = Census.score(new String[] {lastName});
					System.out.println(score + " " + middleName1 + " " + middleName2 + " " + lastName);
				}
			}
		}
		
	}
	
	/*
	Name portion is 12 symbols long.  Possible assignments:
		0: C, N
		1: O, H
		2: N
		3: ?
		4: ?
		5: E
		6: ?
		7: ?
		8: E
		9: N
		10: L
		11: Y
*/		
	static boolean isValid(String name) {
		try {
			char ch;
			if (name == null)
				return true;
			if (name.length() > 12)
				return false;
			ch = name.charAt(0);
			if (ch != 'C' && ch != 'N') return false;
			ch = name.charAt(1);
			if (ch != 'O' && ch != 'H') return false;
			ch = name.charAt(2);
			if (ch != 'N') return false;
			ch = name.charAt(5);
			if (ch != 'E') return false;
			ch = name.charAt(8);
			if (ch != 'E') return false;
			ch = name.charAt(9);
			if (ch != 'N') return false;
			ch = name.charAt(10);
			if (ch != 'L') return false;
			ch = name.charAt(11);
			if (ch != 'Y') return false;
		} catch (Exception e) {
			// exception-oriented programming.  ouch.  :)
		}
		return true;
	}	
	
	public static boolean match2(String name) {
		if (name == null) return false;
		for (int i=0; i<matches.length && i<name.length(); i++) {
			String match = matches[i];
			if (match == "?") continue;
			boolean hit = false;
			for (int j=0; j<match.length(); j++) {
				char ch = match.charAt(j);
				if (ch == name.charAt(i) || name.charAt(i) == '?') {
					hit = true;
					break;
				}
			}
			if (!hit) return false;
		}
		return true;
	}
	
	public static String match(String name) {
		String result = "";
		List<String> val = namesMap.get(name.substring(0,3));
		if (val == null) return result;
		if (val.isEmpty()) return result;
		//System.out.println(val.size());
		
		for (String n : val) {
			if (match(name, n)) result += n + " ";
		}
		if (!"".equals(result)) return "Match: " + result;
		return result;
	}
	
	public static boolean match(String cipherName, String censusName) {
		//System.out.println("[" + cipherName + "] [" + censusName + "]");
		if (censusName.length() < 5) return true;
		for (int i=4; i<censusName.length() && i<cipherName.length(); i++) {
			if (censusName.charAt(i) != cipherName.charAt(i)) return false;
		}
		return true;
	}
	
	public static void main(String[] args) {
		//go2();
		go4();
		//System.out.println(isValid("CONNIEOHENLY"));
		//System.out.println(match("CON_IEDGENLY","CONNIE"));
	}
}
