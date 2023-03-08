package com.zodiackillerciphers.hackerrank;

/** https://www.hackerrank.com */
public class Exercises {
	
    public static String timeConversion(String s) {
        String[] split = s.split(":");
        boolean pm = split[2].substring(2).equals("PM");
        int hourInt = Integer.valueOf(split[0]);
        if (pm && hourInt < 12) hourInt += 12;
        else if (!pm && hourInt == 12) hourInt = 0;
        String hour = (hourInt < 10 ? "0" : "") + hourInt;
        String min = split[1];
        String sec = split[2].substring(0,2);
        
        return hour + ":" + min + ":" + sec;
    }
    
    public static void testTimeConversion() {
    	System.out.println(timeConversion("01:00:05AM"));
    	System.out.println(timeConversion("01:00:05PM"));
    	System.out.println(timeConversion("12:00:05PM"));
    	System.out.println(timeConversion("12:00:05AM"));
    }
	
	public static void main(String[] args) {
		//System.out.println(String.format("%.6f", 1123.23456));
		testTimeConversion();
	}

}
