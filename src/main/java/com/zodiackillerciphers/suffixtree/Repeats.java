package com.zodiackillerciphers.suffixtree;

import com.zodiackillerciphers.homophones.HomophoneSequenceBean;
import com.zodiackillerciphers.homophones.HomophonesNew;
import com.zodiackillerciphers.homophones.HomophonesResultBean;
import com.zodiackillerciphers.transform.CipherTransformations;

public class Repeats {
    // brute force method of finding longest repeating substring.
    // doing it this way to avoid overlaps and non-contiguous matches.
    public static RepeatsResultBean lrsBrute(String s) {
    	return lrsBrute(s, 0);
    }
    public static RepeatsResultBean lrsBrute(String s, int actualPosition) {
    	if (s.length() == 0 || s == null) return null;
    	//System.out.println("considering " + s);
    	RepeatsResultBean best = new RepeatsResultBean();
    	
    	//for (int L=1; L<=s.length()/2; L++) {
    	for (int L=2; L<=s.length()/2; L++) {
    		int count = 0;
    		int pos = 0;
    		String sequence = s.substring(pos, pos+L);
    		while (true) {
    			count++;
    			pos+=L;
    			try {
    				String newSequence = s.substring(pos, pos+L);
        			if (!sequence.equals(newSequence)) break;
    			} catch (Exception e) {
    				break;
    			}
    		}
    		
    		if (count > 1) {
    			RepeatsResultBean bean = new RepeatsResultBean(sequence, count, actualPosition);
    			if (bean.length() > best.length()) best = bean;
    		}
    	}
    	
    	// recurse: return the current best only if there isn't a better one for the substring starting
    	// at the second position
    	RepeatsResultBean subproblem = lrsBrute(s.substring(1), actualPosition+1);
    	if (subproblem != null && subproblem.length() > best.length()) {
    		return subproblem;
    	}
    	if (best.length() == 0) return null;
    	return best;
    }

	/**
	 * find palindromic sequences.
	 */
    public static RepeatsPalindromicBean largestPalindromicSequence(String s) {
    	return largestPalindromicSequence(s, 0);
    }
    public static RepeatsPalindromicBean largestPalindromicSequence(String s, int originalPos) {
    	if (s == null || s.length() < 2) return null;
    	RepeatsPalindromicBean best = new RepeatsPalindromicBean();
    	
    	String palindrome = null;
    	for (int i=s.length(); i>=2; i--) {
    		String sub = s.substring(0, i);
    		if (isPalindrome(sub)) {
    			palindrome = sub;
    			break;
    		}
    	}
    	// we now have a palindromic string.  the remainder of the sequence might be an incomplete palindrome,
    	// so add it.  TODO
//    	int p = palindrome.length();
//    	for (int i=1; i<p; i++) {
//    		//System.out.println("p " + p + " i " + i);
//    		if (p+i >= s.length()) break;
//    		if (s.charAt(p+i) == palindrome.charAt(i)) palindrome += s.charAt(p+i);
//    	}
//    	System.out.println("pal: " + palindrome);
    	best.setPos(originalPos);
    	best.setSequence(palindrome);
    	best.setFullLength(s.length());
    	
    	HomophoneSequenceBean seq = new HomophoneSequenceBean();
    	seq.fullSequence = palindrome;
    	HomophonesResultBean hom = HomophonesNew.bestRepeatingSequenceIn(seq, 0);
    	best.setHom(hom);
    	
    	// recurse to see if we find better ones
    	RepeatsPalindromicBean subproblem = largestPalindromicSequence(s.substring(1), originalPos+1);
//    	if (subproblem != null && subproblem.length() > best.length())
//    		best = subproblem;
    	
    	if (subproblem != null && subproblem.getHomScore() > best.getHomScore())
    		best = subproblem;
    	if (best.length() == 0) return null;
    	return best;
    }
    
    public static boolean isPalindrome(String s) {
    	return s.equals(new StringBuffer(s).reverse().toString());
    }
    
    public static RepeatsPalindromicBean largestPalindromicSequenceOLD2(String s, int originalPos) {
    	RepeatsPalindromicBean best = new RepeatsPalindromicBean();
    	/*boolean started = false;
    	String pattern = "1234";
    	int pos = -1;
    	for (int i=0; i<s.length(); i++) {
    		char ch = s.charAt(i);
    		if (pattern.indexOf(ch) > -1) {
    			if (!started) {
    				started = true;
    				
    			}
    		} else {
    			pattern += ch;
    		}
    	}*/
    	
    	// first, determine the pattern
    	String pattern = "";
    	for (int i=0; i<s.length()/2; i++) {
    		char ch = s.charAt(i);
    		if (pattern.indexOf(ch) > -1) break;
    		pattern += ch;
    	}
    	System.out.println("pattern is: " + pattern);
    	PalindromicState state = new PalindromicState();
    	state.setPattern(pattern);
    	state.setForward(true);
    	state.setPos(-1);
    	
    	for (int i=0; i<s.length(); i++) {
    		if (!state.move(s.charAt(i))) break;
    	}
    	
    	System.out.println(state);
    	
    	
    	if (best.length() == 0) return null;
    	return best;
    }
    
    public static RepeatsPalindromicBean largestPalindromicSequenceOLD(String s, int originalPos) {
    	RepeatsPalindromicBean best = new RepeatsPalindromicBean();
    	for (int L=2; L<s.length()/2; L++) {
    		String patternOriginal = s.substring(0,L);
    		String pattern = s.substring(0,L);
    		String sequence = pattern;
    		int pos = L;
    		while (pos+L<s.length()) {
    			pattern = reverse(pattern);
    			if (!s.substring(pos,pos+L).equals(pattern)) break;
    			sequence += pattern;
    			pos += L;
    		}
    		if (pos+L >= s.length()) {
    			System.out.println("at end: " + s.substring(pos));
    			System.out.println("pattern " + pattern);
    		}
    		
    		RepeatsPalindromicBean bean = new RepeatsPalindromicBean(sequence, originalPos);
    		if (bean.length() > best.length() && bean.length() > L) {
    			best = bean;
    		}
    	}
    	if (best.length() == 0) return null;
    	return best;
    }
    
    public static String reverse(String s) {
    	return new StringBuilder(s).reverse().toString();
    }
    
    public static void testShuffle() {
    	String s = "+++++++q++++A++++++++q++++A+";
    	int matches = 0;
    	int shuffles = 0;
    	while (true) {
    		s = CipherTransformations.shuffle(s);
    		shuffles++;
    		RepeatsResultBean bean = lrsBrute(s, 3);
    		if (bean == null) continue;
    		if (bean.getSequence().length() == 14) {
    			matches++;
    			System.out.println(matches + " " + shuffles);
    		}
    	}
    }
    

    public static void main(String[] args) {
    	System.out.println(lrsBrute("ZpW+6NEZpW+6NEEZpW+6NEZpW+6NENZpW+6NEZpW++WZE6Z+W6EW6E"));
    	/*System.out.println(lrsBrute("l++l+++F+FlF+++++Fl++F+l+F+FlF+++++Fl++F+"));
    	System.out.println(lrsBrute("lMlMFFlFMFlFMlMFFlFMFlFM"));
    	System.out.println(lrsBrute("OMZOMOOMZOMOMZOMOOMZO"));
    	System.out.println(lrsBrute("MMFFFXMFFMMFFFXMFFM"));
    	System.out.println(lrsBrute("MMFFFXMFFMMFFFXMFFM"));
    	System.out.println(lrsBrute("++U+++++U++++AU++U+++++U+++++A+"));*/
//    	System.out.println(largestPalindromicSequence("12343212343"));
//    	System.out.println(largestPalindromicSequence("1234432123443"));
//    	System.out.println(largestPalindromicSequence("123432112343"));
    	//jtestShuffle();
    }
}
