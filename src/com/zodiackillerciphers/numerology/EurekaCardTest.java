package com.zodiackillerciphers.numerology;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/** from zodiackiller.fr.yuku.com/topic/5941
 * "ebeoreitem" observes that the digits of the keys add up to 27.
 * 
 * "I don't think it's particularly important but 7+8+4+0+8= 27 = 5+8+8+5+1
 *	Maybe the number 27 means something to him. "
 *
 * testEureka() shows that of all 10,000 5-digit numbers, 220 of them have a sum of 27.
 * So the chances of 2 random numbers having a sum of 27 = (220/1000)^2 = 4.84%
 * 
 * Also:
 * 
 * "If you add 78408 and 58851 you get 137259 1+3+7+2+5+9=27.  If you subtract 58851 from 78408 you get 19557 1+9+5+5+7=27"
 *  
 **/
public class EurekaCardTest {
	
	public static void testEureka() {
		// look at all possible 5-digit numbers, and add up the digits.
		// track count of numbers per sum, to calculate the odds
		// of equals sums occurring by chance.
		
		Map<Integer, Integer> counts = new HashMap<Integer, Integer>();
		for (int i=0; i<10000; i++) {
			String digits = "" + i;
			int sum = sumDigits(digits);
			Integer val = counts.get(sum);
			if (val == null) val = 0;
			val++;
			counts.put(sum, val);
		}
		for (Integer key : counts.keySet())
			System.out.println(key + ": " + counts.get(key));
		
		Random r = new Random();
		int hits = 0;
		int trials = 1000000;
		for (int i=0; i<trials; i++) {
			String n1 = ""+r.nextInt(10000);
			String n2 = ""+r.nextInt(10000);
			if (sumDigits(n1) == sumDigits(n2)) hits++;
		}
		System.out.println("In " + trials + " random trials, " + hits + " resulted in equal sums.  Ratio: " + ((float)hits/trials));
		
		hits = 0;
		for (int i=0; i<trials; i++) {
			int n1 = r.nextInt(10000);
			int n2 = r.nextInt(10000);
			int n3 = n1+n2;
			int n4 = Math.abs(n1-n2);
			
			if (sumDigits(""+n1) != sumDigits(""+n2)) continue;
			if (sumDigits(""+n2) != sumDigits(""+n3)) continue;
			if (sumDigits(""+n3) != sumDigits(""+n4)) continue;
			
			hits++;
		}
		System.out.println("In " + trials + " random trials, " + hits + " resulted in equal sums AND have the add/subtract equal sums property.  Ratio: " + ((float)hits/trials));
	}
	
	public static int sumDigits(String digits) {
		int sum = 0;
		for (int i=0; i<digits.length(); i++) {
			sum += Integer.valueOf(digits.substring(i,i+1));
		}
		return sum;
	}
	public static void main(String[] args) {
		System.out.println(sumDigits("78408"));
		System.out.println(sumDigits("58851"));
		System.out.println(sumDigits(""+(58851+78408)));
		System.out.println(sumDigits(""+(78408-58851)));
		
		testEureka();
	}
}
