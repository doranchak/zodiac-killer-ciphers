package com.zodiackillerciphers.tests;

public class CraigBauerWordSearchBean implements Comparable<CraigBauerWordSearchBean> {

	public int exact;
	public int ambiguous;
	public int length;
	public int freq;
	public int pos;
	public CraigBauerWordSearchBean(int exact, int ambiguous, int length,
			int freq, int pos, String word, String cipher) {
		super();
		this.exact = exact;
		this.ambiguous = ambiguous;
		this.length = length;
		this.freq = freq;
		this.pos = pos;
		this.word = word;
		this.cipher = cipher;
	}

	public String word;
	public String cipher;
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(CraigBauerWordSearchBean o) {
		// TODO Auto-generated method stub
		int c = Float.compare(this.score(), o.score());
		if (c == 0) {
			return Integer.compare(this.hashCode(), o.hashCode());
		} else return c;
	}
	
	public float score() {
		//float score = ((float)(exact+ambiguous))/length;
		//float score = (exact + ambiguous/10) * length;
		float score = exact + ((float)ambiguous)/10 + ((float)exact+ambiguous)/length;
		// add more based on word frequency
		double more = 0;
		if (freq > 0) {
			more = Math.log10(freq);
			// flatten with sigmoid function
			more = 1/(1+Math.exp(-more));
		}
		score += more;
		if (Float.isNaN(score)) {
			System.out.println(this);
			System.exit(-1);
		}
		return score;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return score() + " [exact=" + exact + ", ambiguous="
				+ ambiguous + ", length=" + length + ", freq=" + freq + ", pos=" + pos
				+ ", word=" + word + ", cipher=" + cipher + "]";
	}
	
	public String js1() {
		return "results[" + pos + "][results[" + pos + "].length] = \"" + word + "\"";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return false;
	}

}
