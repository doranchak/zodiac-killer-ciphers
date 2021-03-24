package com.zodiackillerciphers.util;

/** https://www.baeldung.com/java-array-permutations Heap's Algorithm */
public class Permutations {
	public static void recurse(PermutationState state) {
		recurse(state.numElements(), state);
	}
	public static void recurse(int n, PermutationState state) {

		if (n == 1) {
			//printArray(elements, delimiter);
			state.action();
		} else {
			for (int i = 0; i < n - 1; i++) {
				//recurse(n - 1, elements, delimiter);
				recurse(n - 1, state);
				if (n % 2 == 0) {
					state.swap(i, n - 1);
				} else {
					state.swap(0, n - 1);
				}
			}
			recurse(n - 1, state);
		}
	}

//	private static <T> void printArray(T[] input, char delimiter) {
//		System.out.print('\n');
//		for (int i = 0; i < input.length; i++) {
//			System.out.print(input[i]);
//		}
//	}

//	private static void swap(PermutationState state, int a, int b) {
//		Object[] input = state.getElements();
//		Object tmp = input[a];
//		input[a] = input[b];
//		input[b] = tmp;
//	}
	
	public static void main(String[] args) {
		//printAllRecursive(5, new Integer[] {0,1,2,3,4}, ' ');
		StateInt state = new StateInt(new int[] {0,1,2,3,4});
		recurse(state);
	}
}
