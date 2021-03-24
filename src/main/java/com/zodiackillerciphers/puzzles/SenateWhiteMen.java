package com.zodiackillerciphers.puzzles;

public class SenateWhiteMen {
	
	/** in 2017, 13 white men were picked for senate health care group but there are 10 minorities and 21 women in the senate.
	 * if you pick 13 senators at random, how often would they all be strictly white men? 
	 *  
	 *  */
	public static void experiment() {
		
		int women = 0;
		int minorities = 0;
		int either = 0;
		int both = 0;
		int allMale = 0;
		int allWhite = 0;
		int allMaleAndAllWhite = 0;
		for (int i=0; i<1000000; i++) {
			boolean w = false;
			boolean m = false;
			for (int j=0; j<13; j++) {
				if (Math.random() <= (5d/52)) {
					w = true;
				}
				if (Math.random() <= (3d/52)) {
					m = true;
				}
			}
			if (w) women++; else allMale++;
			if (m) minorities++; else allWhite++;
			if (w || m) either++;
			if (w && m) both++;
			if (!w && !m) allMaleAndAllWhite++;
		}
		System.out.println(women + " " + minorities + " " + both);
		System.out.println(either);
		System.out.println(allMale + " " + allWhite + " " + allMaleAndAllWhite);
	}
	public static void main(String[] args) {
		experiment();
	}
}
