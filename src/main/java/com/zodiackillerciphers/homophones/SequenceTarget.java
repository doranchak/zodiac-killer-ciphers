package com.zodiackillerciphers.homophones;

public class SequenceTarget {
	public String sequence; // the pattern that repeats
	public int n; // the number of repetitions 
	public int L; // the length of the entire sequence
	
	public String fromCipher; // the full sequence (involving only the symbols from this target's pattern) extracted from a candidate cipher
	public int hits; // number of repetitions found in the cipher text
	
	public SequenceTarget(String sequence, int n, int l) {
		this.sequence = sequence;
		this.n = n;
		L = l;
		fromCipher = "";
		
	}
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		SequenceTarget that = (SequenceTarget) obj;
		return this.sequence.equals(that.sequence);
	}
	@Override
	public int hashCode() {
		return sequence.hashCode();
	}
	
	/* compute number of errors (differences) between the candidate full sequence and this target */
	public int errors() {
		hits = 0;
		String sub = sequence;
		while (fromCipher.contains(sub)) {
			hits++;
			sub += sequence;
		}
		return Math.abs(n - hits) + Math.abs(fromCipher.length() - L);
	}
	
	/* what is the next symbol expected to occur after the given symbol in the cycle? */
	public char after(char c) {
		if (sequence.indexOf(c) == -1) {
			System.err.println("Can't find " + c + " in " + sequence);
			System.exit(-1); // FATAL
		}
		int pos = (sequence.indexOf(c) + 1) % sequence.length();
		return sequence.charAt(pos);
	}
	
	public static void test() {
		SequenceTarget t = new SequenceTarget("KM", 6, 14);
		t.fromCipher = "KMKMKMKMKMMK";
		System.out.println(t.errors());
	}
	
	public static void main(String[] args) {
		test();
	}
	
	public String toString() {
		return "sequence " + sequence + " n " + n + " L " + L + " fromCipher " + fromCipher + " errors " + errors() + " hits " + hits;
	}
	
}
