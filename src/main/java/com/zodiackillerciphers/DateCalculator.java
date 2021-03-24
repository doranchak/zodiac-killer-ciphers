package com.zodiackillerciphers;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class DateCalculator {
	/* http://www.zodiackillersite.com/viewtopic.php?p=75429#p75429
	 * "I found that there was a Sat 23rd in 1980. Need help finding other years for that date."
	 */
	public static void findDayOfWeek(int day, int weekday) {
		Calendar cal = GregorianCalendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-YYYY");
		cal.set(Calendar.DAY_OF_MONTH, day);
		System.out.println(cal.getTime());
		while(true) {
			if (cal.get(Calendar.DAY_OF_WEEK) == weekday) {
				System.out.println(sdf.format(cal.getTime()));
			}
			cal.set(Calendar.MONTH, cal.get(Calendar.MONTH)-1);
			if (cal.get(Calendar.YEAR) < 1900)
				break;
		}
	}
	
	public static void main(String[] args) {
		findDayOfWeek(23, Calendar.SATURDAY);
	}
}
