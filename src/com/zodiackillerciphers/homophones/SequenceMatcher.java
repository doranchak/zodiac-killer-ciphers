package com.zodiackillerciphers.homophones;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.transform.CipherTransformations;

public class SequenceMatcher {
	
	// list of target sequences and how many repetitions we are targeting
	public static List<SequenceTarget> targets;
	static {
		targets = new ArrayList<SequenceTarget>();
		targets.add(new SequenceTarget("lM", 7, 14));
		targets.add(new SequenceTarget("^*", 6, 12));
		targets.add(new SequenceTarget("KM", 6, 14));
		targets.add(new SequenceTarget("OK", 6, 17));
		targets.add(new SequenceTarget("2z", 6, 18));
		targets.add(new SequenceTarget("RK", 5, 15));
		targets.add(new SequenceTarget("O<", 5, 16));
		targets.add(new SequenceTarget("*9t", 4, 14));
		targets.add(new SequenceTarget("^*9", 4, 16));
	}
	
	// candidate cipher whose sequences we want to measure
	String cipher;
	
	// map cipher symbol to all the SequenceTargets that are involved with it
	Map<Character, List<SequenceTarget>> sequenceMap;
	
	public SequenceMatcher(String cipher) {
		this.cipher = cipher;
		init();
		makeSequencesFromCipher();
	}
	
	/** create sequence map based on symbols involved in the targets */
	public void init() {
		sequenceMap = new HashMap<Character, List<SequenceTarget>>();
		for (SequenceTarget target : targets) {
			target.fromCipher = ""; // reset this, because otherwise it will hang around after evaluations.
			for (int i=0; i<target.sequence.length(); i++) {
				char key = target.sequence.charAt(i);
				List<SequenceTarget> val = sequenceMap.get(key);
				if (val == null) val = new ArrayList<SequenceTarget>();
				val.add(target);
				sequenceMap.put(key, val);
			}
		}
	}
	
	/** generate sequences out of the cipher text */
	public void makeSequencesFromCipher() {
		for (int i=0; i<cipher.length(); i++) {
			char ch = cipher.charAt(i);
			List<SequenceTarget> targets = sequenceMap.get(ch);
			if (targets == null) continue;
			for (SequenceTarget target : targets) {
				target.fromCipher = target.fromCipher + ch;
			}
		}
	}

	public static void test(String cipher) {
		//SequenceMatcher s = new SequenceMatcher(CipherTransformations.shuffle(Ciphers.cipher[0].cipher));
		SequenceMatcher s = new SequenceMatcher(cipher);
		
		for (SequenceTarget target : s.targets) {
			System.out.println(target);
		}
		System.out.println("Errors: " + s.totalErrors());
	}
	public static void test() {
		test(Ciphers.cipher[0].cipher);
	}
	
	public int totalErrors() {
		int e = 0;
		for (SequenceTarget target : targets) {
			e += target.errors();
		}
		return e;
	}
	
	public static void main(String[] args) {
		//test();
		Map<Character, Integer> map = Ciphers.countMap(Ciphers.cipher[0].cipher);
		for (Character key  :map.keySet()) 
			if (map.get(key) > 3 && map.get(key) < 11) {
				System.out.println(key);
			}
	}
}
