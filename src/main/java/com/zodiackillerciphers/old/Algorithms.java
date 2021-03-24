package com.zodiackillerciphers.old;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Algorithms {

	/** dates in the AK dates theory test */
	// 13 of these are divisible by 5.  less than half are divisible by 3.
	public static int[][] datesZodiac = new int[][] {
		{12,20,68}, // faraday/jensen
		{7,4,69}, // ferrin/mageau
		{7,5,69}, // ferrin/mageau
		{9,27,69}, // shepard/hartnell
		{10,11,69}, // paul stine
		{11,10,69},
		{3,22,70}, // kathleen johns
		{6,19,70},
		{9,6,70},
		{9,11,75},
		{10,1,79},
		{11,15,79},
		{7,27,81},
		{11,28,81}
	};
	
	public static int[][] datesZodiacOthers = new int[][] {
		{10,30,66}, // cheri jo bates
		{6,4,63}, // domingos and edwards
	};
	
	public static int[][] datesApocalypse = new int[][] {
		{4,23,90},
		{4,29,07},
		{4,29,87},
		{8,17,87},
		{8,18,99},
		{8,20,67},
		{8,9,69},
		{12,17,96},
		{12,21,54},
		{12,31,99},
		{2,13,25},
		{2,4,62},
		{1,1,00},
		{1,11,73},
		{1,12,73},
		{1,13,73},
		{1,14,73},
		{1,15,73},
		{1,16,73},
		{1,17,73},
		{1,18,73},
		{1,19,73},
		{1,20,73},
		{1,21,73},
		{6,21,82},
		{3,10,82},
		{3,26,97},
		{3,31,95},
		{3,31,98},
		{5,2,94},
		{5,21,11},
		{5,5,00},
		{11,29,03},
		{10,2,84},
		{10,21,11},
		{10,23,97},
		{10,28,92},
		{10,9,00},
		{9,28,92},
		{9,29,11},
		{9,30,89}
	};
	
	
	/** return the longest common substring shared by S1 and S2.
	 * returns Object[] of { substring, position of first string, position of second string }
	 **/
    public static Object[] longestCommonSubstring(String S1, String S2)
    {
        int Starti = 0;
        int Startj = 0;
        int Max = 0;
        for (int i = 0; i < S1.length(); i++)
        {
            for (int j = 0; j < S2.length(); j++)
            {
                int x = 0;
                while (S1.charAt(i + x) == S2.charAt(j + x))
                {
                    x++;
                    if ((i + x >= S1.length()) || (j + x >= S2.length())) break;
                }
                if (x > Max)
                {
                    Max = x;
                    Starti = i;
                    Startj = j;
                }

            }
        }
        return new Object[] { S1.substring(Starti, Starti+Max), Starti, Startj };
    }
    
    public static String reverseIt(String source) {
        int i, len = source.length();
        StringBuffer dest = new StringBuffer(len);

        for (i = (len - 1); i >= 0; i--)
          dest.append(source.charAt(i));
        return dest.toString();
      }
    
    /** generate all permutations of a string.  usage:  permuteString(list, "", "String"); */
    public static void permuteString(List<String> results, String beginningString, String endingString) {
    	
        if (endingString.length() <= 1)
          results.add(beginningString + endingString);
        else
          for (int i = 0; i < endingString.length(); i++) {
            try {
              String newString = endingString.substring(0, i) + endingString.substring(i + 1);

              permuteString(results, beginningString + endingString.charAt(i), newString);
            } catch (StringIndexOutOfBoundsException exception) {
              exception.printStackTrace();
            }
          }
      }
    
    /** return internally sorted version of the given string */
    public static String sortInternal(String s) {
    	if (s==null) return null;
    	char[] ch = new char[s.length()];
    	for (int i=0; i<s.length(); i++) ch[i] = s.charAt(i);
    	Arrays.sort(ch);
    	return new String(ch);
    }
    
    /** display integer factors of the sum of the given numbers (see AK's divisible-by-five theory)*/
    public static void factors(int[] numbers) {
    	int sum = 0;
    	
    	String header = "";
    	for (int n : numbers) {
    		sum += n;
    		header += n + " ";
    	}
    	
    	header += "(" + sum + "): ";
    	factors(header, sum);
    }
    
    public static void factors(String header, int n) {
    	for (int i=2; i<=n; i++) {
    		if (n%i == 0) 
    			System.out.println(header + i);
    	}
    }
    public static void factors(Calendar c) {
    	System.out.println(c.getTime());
    	factors(new int[] {c.get(Calendar.MONTH)+1, c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.YEAR)-1900});
    }
    
    public static void testSortInternal() {
    	System.out.println(sortInternal("ijfdjf9dji8fj1jnmzmlaljfo9sk"));
    }
    
    public static void testLCS() {
    	String s1 = "i like to have cats in my house";
    	String s2 = "i think 'catsup' is a strange way for spelling it";
    	Object[] o = longestCommonSubstring(s1, s2);
    	System.out.println(o[0] + "," + o[1] + "," + o[2]);
    }
    
    
    public static void testFactors() {
    	//for (int[] n : datesZodiac)
    	//	factors(new int[] {n[0],n[1],n[2]});
    	
    	    	
    	Calendar c = Calendar.getInstance();
    	c.set(Calendar.DAY_OF_MONTH, 1);
    	c.set(Calendar.MONTH, 0);
    	c.set(Calendar.YEAR, 1968);
    	int count = 0;
    	for (int i=0; i<365*20; i++) {
    		if (c.get(Calendar.YEAR) > 1981) break;
    		if (c.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY || c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
		    	factors(c);
		    	count++;
    		}
	    	c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH)+1);
    	}
    	System.out.println("done looking at " + count + " dates.");
    	
    	
    }
    
    public static void main(String[] args) {
    	//testLCS();
    	//testSortInternal();
    	testFactors();
    }
    
    
}
