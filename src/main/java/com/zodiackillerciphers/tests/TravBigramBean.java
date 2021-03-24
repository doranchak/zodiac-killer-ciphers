package com.zodiackillerciphers.tests;

import java.util.ArrayList;
import java.util.List;

public class TravBigramBean {

	public String bigram;
	public List<int[]> locations;
	
	@Override
	public boolean equals(Object obj) {
		//System.out.println("calling equals");
		TravBigramBean bean = (TravBigramBean) obj;
		return keyFor(bean.bigram).equals(keyFor(bigram));
	}
	
	public int hashCode() {
		return keyFor(bigram).hashCode();
	}
	
	public static String keyFor(String bigram) {
		if (bigram.charAt(0) < bigram.charAt(1))
			return bigram;
		else
			return ""+bigram.charAt(1)+bigram.charAt(0);	
			
		
	}
	
	public TravBigramBean(String bigram, int row, int col) {
		locations = new ArrayList<int[]>();
		this.bigram = keyFor(bigram);
		addLocation(row,col);
	}
	
	public void addLocation(int row, int col) {
		locations.add(new int[] {row, col});
	}
	
	public void dump() {
		for (int[] i : locations) {
			System.out.println(keyFor(bigram) + " location: [Row " + i[0]+" Col "+i[1] + "]");
		}
	}

	public static void main(String[] args) {
		TravBigramBean bean1 = new TravBigramBean("AB",0,0);
		TravBigramBean bean2 = new TravBigramBean("BA",0,0);
		System.out.println((bean1==bean2) + "," + bean1.equals(bean2));
		System.out.println(keyFor("BA"));
		
	}
	
}
