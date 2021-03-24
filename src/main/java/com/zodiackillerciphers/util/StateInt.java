package com.zodiackillerciphers.util;

import java.util.Arrays;

public class StateInt implements PermutationState {

	private int[] elements;

	public StateInt(int[] elements) {
		this.elements = elements;
	}
	
	@Override
	public void action() {
		System.out.println(Arrays.toString(elements));
	}

	@Override
	public void swap(int a, int b) {
		int tmp = elements[a];
		elements[a] = elements[b];
		elements[b] = tmp;
	}

	@Override
	public int numElements() {
		// TODO Auto-generated method stub
		return elements.length;
	}

}
