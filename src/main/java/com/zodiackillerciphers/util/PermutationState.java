package com.zodiackillerciphers.util;

public interface PermutationState {
	/** action to perform when visiting a permutation */
	public void action();
	/** swap the given elements */
	public void swap(int a, int b);
	/** return number of elements */
	public int numElements();
}
