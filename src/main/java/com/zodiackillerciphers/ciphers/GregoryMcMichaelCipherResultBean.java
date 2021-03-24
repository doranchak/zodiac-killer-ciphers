package com.zodiackillerciphers.ciphers;

import java.util.Arrays;

public class GregoryMcMichaelCipherResultBean implements Comparable {
	public double zkscore;
	public String plaintext;
	public int cipher;
	public int[] key;
	public int offset;
	public boolean decrypted;
	public StringBuffer tokens;
	@Override
	public int compareTo(Object o) {
		GregoryMcMichaelCipherResultBean bean1 = this;
		GregoryMcMichaelCipherResultBean bean2 = (GregoryMcMichaelCipherResultBean) o;
		return Double.compare(bean2.zkscore, bean1.zkscore);
	}
	@Override
	public String toString() {
		return cipher + "	" + zkscore + "	" + plaintext + "	"
				+ Arrays.toString(key) + "	" + offset + "	" + decrypted + "	" + tokens;
	}
	
}
