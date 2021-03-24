package com.zodiackillerciphers.tests;

import java.util.ArrayList;
import java.util.List;

/** 340 is an arithmetic number whose divisors average to 63, its alphabet size.
 * 408 minus 18 = 390 whose divisors also average to 63.
 * generate more arithmetic numbers to see what the averages look like.
 **/
public class ArithmeticNumbers {
	public static void find() {
		for (int i=1; i<1000000000; i++) {
			List<Integer> divisors = divisors(i);
			int sum = sum(divisors);
			if (sum % divisors.size() == 0) {
				System.out.println(i + " " + average(divisors) + " " + divisors.size() + " " + divisors);
			}
		}
	}
	
	public static List<Integer> divisors(int n) {
		List<Integer> list = new ArrayList<Integer>();
		for (int i=1; i<=n; i++)
			if (n%i==0) list.add(i);
		return list;
	}
	
	public static int sum(List<Integer> list) {
		int sum = 0;
		for (Integer i : list) sum += i;
		return sum;
	}
	public static int average(List<Integer> list) {
		int sum = sum(list);
		if (sum % list.size() != 0) throw new IllegalArgumentException("Average of these numbers will not be an integer!  " + list);
		return sum / list.size();
	}
	
	public static void main(String[] args) {
		find();
	}
}
