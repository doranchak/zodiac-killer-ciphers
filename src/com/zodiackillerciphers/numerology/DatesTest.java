package com.zodiackillerciphers.numerology;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// same day of week
// all dates are on the weekend
// same day of month
// add parts
// multiply parts
// add until single digit.  all single digits same, or all are divisible by some factor.
// (option for above three: include year prefix or not?)
// all fall on federal holiday
// all dates have prime day of month.  or all dates have
// sums are the same (day+month+year, day+year, day+month, etc)

public class DatesTest {

	static int TESTS = 52; // number of tests
	
	static Map<String, String> holidays;
	static {
		holidays = new HashMap<String, String>();
		holidays.put("1/1/68", "New Year's Day");
		holidays.put("2/12/68", "Lincoln's Birthday");
		holidays.put("2/14/68", "Valentine's Day");
		holidays.put("2/22/68", "Presidents' Day");
		holidays.put("4/14/68", "Easter Sunday");
		holidays.put("5/12/68", "Mother's Day");
		holidays.put("5/30/68", "Memorial Day");
		holidays.put("6/16/68", "Father's Day");
		holidays.put("7/4/68", "Independence Day");
		holidays.put("9/2/68", "Labor Day");
		holidays.put("10/12/68", "Columbus Day");
		holidays.put("10/31/68", "Halloween");
		holidays.put("11/5/68", "Election Day");
		holidays.put("11/11/68", "Veterans Day");
		holidays.put("11/28/68", "Thanksgiving Day");
		holidays.put("12/24/68", "Christmas Eve");
		holidays.put("12/25/68", "Christmas Day");
		holidays.put("12/31/68", "New Year's Eve");
		holidays.put("1/1/69", "New Year's Day");
		holidays.put("2/12/69", "Lincoln's Birthday");
		holidays.put("2/14/69", "Valentine's Day");
		holidays.put("2/22/69", "Presidents' Day");
		holidays.put("4/6/69", "Easter Sunday");
		holidays.put("5/11/69", "Mother's Day");
		holidays.put("5/30/69", "Memorial Day");
		holidays.put("6/15/69", "Father's Day");
		holidays.put("7/4/69", "Independence Day");
		holidays.put("9/1/69", "Labor Day");
		holidays.put("10/12/69", "Columbus Day");
		holidays.put("10/31/69", "Halloween");
		holidays.put("11/11/69", "Veterans Day");
		holidays.put("11/27/69", "Thanksgiving Day");
		holidays.put("12/24/69", "Christmas Eve");
		holidays.put("12/25/69", "Christmas Day");
		holidays.put("12/31/69", "New Year's Eve");
		holidays.put("1/1/70", "New Year's Day");
		holidays.put("2/12/70", "Lincoln's Birthday");
		holidays.put("2/14/70", "Valentine's Day");
		holidays.put("2/22/70", "Presidents' Day");
		holidays.put("3/29/70", "Easter Sunday");
		holidays.put("5/10/70", "Mother's Day");
		holidays.put("5/30/70", "Memorial Day");
		holidays.put("6/21/70", "Father's Day");
		holidays.put("7/3/70", "Independence Day (observed)");
		holidays.put("7/4/70", "Independence Day");
		holidays.put("9/7/70", "Labor Day");
		holidays.put("10/12/70", "Columbus Day");
		holidays.put("10/31/70", "Halloween");
		holidays.put("11/11/70", "Veterans Day");
		holidays.put("11/26/70", "Thanksgiving Day");
		holidays.put("12/24/70", "Christmas Eve");
		holidays.put("12/25/70", "Christmas Day");
		holidays.put("12/31/70", "New Year's Eve");
	}
	
	public static Calendar randomDate(Calendar min, Calendar max) {
		Calendar cal = Calendar.getInstance();
		
		long start = min.getTimeInMillis();
		long end = max.getTimeInMillis();
		
		long delta = end - start;
		
		long rand = (long) (Math.random() * delta);
		cal.setTimeInMillis(rand+start);
		//System.out.println(stringFor(cal, false) + "  " + stringFor(cal, true));
		return cal;
	}
	
	/** generate n random dates that fall in the given date range */
	public static List<Calendar> randomDates(int n, Calendar min, Calendar max) {
		List<Calendar> list = new ArrayList<Calendar>();
		
		for (int i=0; i<n; i++) {
			list.add(randomDate(min, max));
		}
		return list;
	}
	
	public static void patterns(int numDates, int numTests, Calendar min, Calendar max) {
		int hits = 0;
		for (int i=0; i<numTests; i++) {
			boolean hit = false;
			StringBuffer line = new StringBuffer();
			line.append("Test #" + (i+1) + ": ");
			List<Calendar> dates = randomDates(numDates, min, max);
			for (Calendar cal : dates) line.append(stringFor(cal, false)).append(" ");
			line.append("--> ");
			for (int j=0; j<TESTS; j++) {
				hit |= patternTest(j, dates, line);
				if (hit) break;
			}
			if (hit) hits++;
			else line.append("No pattern.");
			System.out.println(line);
		}
		System.out.println(numTests + " tested dates.  " + hits + " were found with patterns, " + (numTests-hits) + " without.  Ratio: " + (((float)hits)/numTests));
	}
	
	public static boolean patternTest(int testNumber, List<Calendar> dates, StringBuffer line) {
		//System.out.println(" dates " + dates.size());
		if (testNumber == 0) return patternTestFactor(dates, line, false, false, false, false, true, false);
		if (testNumber == 1) return patternTestFactor(dates, line, true, false, false, false, true, false);
		if (testNumber == 2) return patternTestFactor(dates, line, true, true, false, false, true, false);
		if (testNumber == 3) return patternTestFactor(dates, line, true, true, false, false, true, false);
		if (testNumber == 4) return patternTestFactor(dates, line, false, false, false, true, true, false);
		if (testNumber == 5) return patternTestFactor(dates, line, true, false, false, true, true, false);
		if (testNumber == 6) return patternTestFactor(dates, line, true, true, false, true, true, false);
		if (testNumber == 7) return patternTestFactor(dates, line, true, true, false, true, true, false);
		if (testNumber == 8) return patternTestFactor(dates, line, false, false, true, false, true, false);
		if (testNumber == 9) return patternTestFactor(dates, line, true, false, true, false, true, false);
		if (testNumber == 10) return patternTestFactor(dates, line, true, true, true, false, true, false);
		if (testNumber == 11) return patternTestFactor(dates, line, true, true, true, false, true, false);
		if (testNumber == 12) return patternTestFactor(dates, line, false, false, true, true, true, false);
		if (testNumber == 13) return patternTestFactor(dates, line, true, false, true, true, true, false);
		if (testNumber == 14) return patternTestFactor(dates, line, true, true, true, true, true, false);
		if (testNumber == 15) return patternTestFactor(dates, line, true, true, true, true, true, false);
		if (testNumber == 16) return patternTestFactor(dates, line, false, false, false, false, false, false);
		if (testNumber == 17) return patternTestFactor(dates, line, true, false, false, false, false, false);
		if (testNumber == 18) return patternTestFactor(dates, line, true, true, false, false, false, false);
		if (testNumber == 19) return patternTestFactor(dates, line, true, true, false, false, false, false);
		if (testNumber == 20) return patternTestFactor(dates, line, false, false, false, true, false, false);
		if (testNumber == 21) return patternTestFactor(dates, line, true, false, false, true, false, false);
		if (testNumber == 22) return patternTestFactor(dates, line, true, true, false, true, false, false);
		if (testNumber == 23) return patternTestFactor(dates, line, true, true, false, true, false, false);
		if (testNumber == 24) return patternTestFactor(dates, line, false, false, true, false, false, false);
		if (testNumber == 25) return patternTestFactor(dates, line, true, false, true, false, false, false);
		if (testNumber == 26) return patternTestFactor(dates, line, true, true, true, false, false, false);
		if (testNumber == 27) return patternTestFactor(dates, line, true, true, true, false, false, false);
		if (testNumber == 28) return patternTestFactor(dates, line, false, false, true, true, false, false);
		if (testNumber == 29) return patternTestFactor(dates, line, true, false, true, true, false, false);
		if (testNumber == 30) return patternTestFactor(dates, line, true, true, true, true, false, false);
		if (testNumber == 31) return patternTestFactor(dates, line, true, true, true, true, false, false);

		if (testNumber == 32) return patternTestFactor(dates, line, true, true, false, false, true, true);
		if (testNumber == 33) return patternTestFactor(dates, line, true, true, false, false, true, true);
		if (testNumber == 34) return patternTestFactor(dates, line, true, true, false, true, true, true);
		if (testNumber == 35) return patternTestFactor(dates, line, true, true, false, true, true, true);
		if (testNumber == 36) return patternTestFactor(dates, line, true, true, true, false, true, true);
		if (testNumber == 37) return patternTestFactor(dates, line, true, true, true, false, true, true);
		if (testNumber == 38) return patternTestFactor(dates, line, true, true, true, true, true, true);
		if (testNumber == 39) return patternTestFactor(dates, line, true, true, true, true, true, true);
		if (testNumber == 40) return patternTestFactor(dates, line, true, true, false, false, false, true);
		if (testNumber == 41) return patternTestFactor(dates, line, true, true, false, false, false, true);
		if (testNumber == 42) return patternTestFactor(dates, line, true, true, false, true, false, true);
		if (testNumber == 43) return patternTestFactor(dates, line, true, true, false, true, false, true);
		if (testNumber == 44) return patternTestFactor(dates, line, true, true, true, false, false, true);
		if (testNumber == 45) return patternTestFactor(dates, line, true, true, true, false, false, true);
		if (testNumber == 46) return patternTestFactor(dates, line, true, true, true, true, false, true);
		if (testNumber == 47) return patternTestFactor(dates, line, true, true, true, true, false, true);

		if (testNumber == 48) return patternTestDayOfWeek(dates, line);
		if (testNumber == 49) return patternTestWeekend(dates, line);
		if (testNumber == 50) return patternTestHoliday(dates, line);
		if (testNumber == 51) return patternTestDayOfMonth(dates, line);
		
		return false;
	}
	
	public static int sum(int sum, StringBuffer text) {
		
		String digits = ""+sum;
		int val = 0;
		text.append("[");
		for (int i=0; i<digits.length(); i++) {
			char ch = digits.charAt(i);
			text.append(ch);
			if (i < digits.length()-1) text.append("+");
			val += Integer.valueOf(""+ch);
		}
		text.append("=" + val + "] ");
		return val;
	}
	
	public static boolean patternTestFactor(List<Calendar> dates, StringBuffer line, boolean prefix, boolean single, boolean minusMonth, boolean minusDay, boolean includeYear, boolean product) {
		if (product && !single) throw new RuntimeException("sorry, can't use product=true while single=false");
		Map<Integer, Integer> counts = new HashMap<Integer, Integer>();
		StringBuffer text = new StringBuffer();
		for (Calendar cal : dates) {
			int sum;
			int day = day(cal, minusDay);
			int month = month(cal, minusMonth);
			if (product) { // no point in negative values when doing products
				day = Math.abs(day);
				month = Math.abs(month);
			}
			int year = year(cal, prefix);
			if (product) sum = day * month;
			else sum = day + month;
			if (includeYear) {
				if (product) sum *= year; 
				else sum += year;
			}
			String monthOp = minusMonth ? "-" : "+";
			String dayOp = minusDay ? "-" : "+";
			if (product) 
				text.append((includeYear ? year + "*" : "") + (month >= 0 ? month : "(" + month + ")") + "*" + (day >= 0 ? day : "(" + day + ")") + "=" + sum + "  ");
			else text.append((includeYear ? year : "") + monthOp + month(cal, false) + dayOp + day(cal, false) + "=" + sum + "  ");
			
			if (single) {
				if (sum < 0) sum = -sum;
				while (sum > 9) {
					sum = sum(sum, text); // yeah, i just did this.
				}
			}
			
			
			int maxFactor = -1;
			boolean maxFound = false;
			for (Integer key : factors(sum, 3)) {
				Integer val = counts.get(key);
				if (val == null) val = 0;
				val++;
				counts.put(key, val);
				if (val == dates.size()) { // hit
					//line.append(text).append(" (Sums" + " divisible by " + key + ")  ");
					//return true;
					maxFound = true;
					maxFactor = Math.max(maxFactor, key);
				}
			}
			if (maxFound) {
				String word = product ? "Products" : "Sums";
				line.append(text).append(" (" + word + " divisible by " + maxFactor + ")  ");
				return true;
			}
		}
		return false;
	}

	public static String weekName(int w) {
		String name = "";
		switch (w) {
		case 1: name = "Sunday"; break;
		case 2: name = "Monday"; break;
		case 3: name = "Tuesday"; break;
		case 4: name = "Wednesday"; break;
		case 5: name = "Thursday"; break;
		case 6: name = "Friday"; break;
		case 7: name = "Saturday"; break;
		}
		return name;
	}
	
	public static boolean patternTestDayOfWeek(List<Calendar> dates, StringBuffer line) {
		int weekday = dates.get(0).get(Calendar.DAY_OF_WEEK);
		for (int i=1; i<dates.size(); i++) {
			if (dates.get(i).get(Calendar.DAY_OF_WEEK) != weekday) return false;
		}
		line.append("All dates fall on " + weekName(weekday));
		return true;
	}

	public static boolean patternTestDayOfMonth(List<Calendar> dates, StringBuffer line) {
		int day = dates.get(0).get(Calendar.DAY_OF_MONTH);
		for (int i=1; i<dates.size(); i++) {
			if (dates.get(i).get(Calendar.DAY_OF_MONTH) != day) return false;
		}
		line.append("All dates fall on same day of the month");
		return true;
	}

	public static boolean patternTestWeekend(List<Calendar> dates, StringBuffer line) {
		for (Calendar cal : dates) {
			if (!isWeekend(cal.get(Calendar.DAY_OF_WEEK))) return false;
		}
		line.append("All dates fall on the weekend");
		return true;
	}
	
	public static boolean patternTestHoliday(List<Calendar> dates, StringBuffer line) {
		String text = "";
		for (Calendar cal : dates) {
			//System.out.println(cal.getTime());
			String holiday = holiday(cal);
			if (holiday == null) return false; 
			text += holiday + " "; 
		}
		line.append("These are all U.S. holidays (" + text + ")");
		return true;
	}
	
	public static String holiday(Calendar cal) {
		//System.out.println(stringFor(cal, false));
		return holidays.get(stringFor(cal, false));
	}
	
	public static boolean isWeekend(int w) {
		return w == 1 || w == 7;
	}
	
	
	
	public static List<Integer> factors(int num, int start) {
		if (num < 0) num = -num;
		List<Integer> list = new ArrayList<Integer>();
		for (int i=start; i<=num; i++) {
			if (num % i == 0) list.add(i);
		}
		return list;
	}
	
	/** brute force search between startDate and endDate for every unique combination of n dates whose parts add up to numbers divisible by the given factor */
	public static void search(int n, int factor, Calendar startDate, Calendar endDate) {
		List<Calendar> results = new ArrayList<Calendar>();
		search(n, factor, startDate, endDate, results);
	}
	
	public static void dump(List<Calendar> cals, int factor) {
		String line = "";
		for (Calendar cal : cals) line += stringFor(cal, false) + " (" + sum(cal) + "/" + factor +"=" + (sum(cal)/factor) + ") ";
		System.out.println(line);
	}
	public static void dump(List<Calendar> cals) {
		String line = "";
		for (Calendar cal : cals) line += stringFor(cal, false) + " ";
		System.out.println(line);
	}

	public static int sum(Calendar date) {
		return date.get(Calendar.YEAR) - 1900 + 
				date.get(Calendar.MONTH) + 1 + 
				date.get(Calendar.DAY_OF_MONTH);
	}
	
	/** return true if date parts add to a sum that is evenly divisible by the given factor */
	public static boolean fits(Calendar date, int factor) {
		return sum(date) % factor == 0;
	}
	
	/** generate list of dates from given range whose date parts add to a sum that is evenly divisible by the given factor */ 
	public static List<Calendar> datesFor(int factor, Calendar min, Calendar max) {
		List<Calendar> list = new ArrayList<Calendar>();
		Calendar cal = copyOf(min);
		while (!cal.after(max)) {
			if (fits(cal, factor)) {
				list.add(copyOf(cal));
				System.out.println(stringFor(cal, false));
			}
			increment(cal, 1);
		}
		return list;
	}
	
	/** increment the given calendar by the given number of days */
	public static void increment(Calendar cal, int increment) {
		cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH)+increment);
	}
	
	/** new copy of the given calendar */
	public static Calendar copyOf(Calendar cal) {
		Calendar newCal = Calendar.getInstance();
		newCal.setTimeInMillis(cal.getTimeInMillis());
		return newCal;
	}
	
	/** clock face search: find all combinations of n dates that fit the given clock face positions.
	 * dates are made into sums based on their parts.  only dates whose sums are evenly divisible by the given factor are considered.
	 * 
	 * you must pass positions in ascending order 
	 */
	public static void clocksearch(int n, int factor, Calendar startDate, Calendar endDate, int[] positions) {
		List<Calendar> dates = datesFor(factor, startDate, endDate);
		
		// figure out the min and max possible sums.  and make a map of sums to dates.
		int minSum = Integer.MAX_VALUE;
		int maxSum = Integer.MIN_VALUE;
		Map<Integer, List<Calendar>> map = new HashMap<Integer, List<Calendar>>();
		for (Calendar date : dates) {
			minSum = Math.min(minSum, sum(date));
			maxSum = Math.max(maxSum, sum(date));
			int key = sum(date);
			List<Calendar> val = map.get(key);
			if (val == null) val = new ArrayList<Calendar>();
			val.add(date);
			map.put(key, val);
		}
		System.out.println("minSum " + minSum + ", maxSum " + maxSum);
		for (Integer key : map.keySet()) {
			System.out.println(key + ": " + map.get(key).size());
		}
		int first = minSum/5;
		int diff = first-positions[0];
		int total = 0;
		while (true) {
			int[] sequence = new int[positions.length];
			for (int i=0; i<positions.length; i++) sequence[i] = (positions[i]+diff)*factor;
			if (sequence[sequence.length-1] > maxSum) break;
			String s = ""; String p = ""; int product = 1; 
			for (int i=0; i<sequence.length; i++) {
				s += sequence[i];
				if (i<sequence.length-1) s+=",";
				product *= map.get(sequence[i]).size();
				p += map.get(sequence[i]).size();
				if (i<sequence.length-1) p+="*";
			}
			total += product;
			System.out.println(s + "  offset " + diff + "  " + p + "=" + product);
			first++; diff++;
		}
		System.out.println("total: " + total);
		
	}
	
	public static void search(int n, int factor, Calendar startDate, Calendar endDate, List<Calendar> results) {
		//System.out.println(n+","+startDate.getTime()+","+endDate.getTime()+","+results.size());
		if (n == 0) return;
		if (startDate.after(endDate)) return;
		if (results.size() == n) { // hit!
			dump(results, factor);
			return;
		}
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate.getTime());
		while(!cal.after(endDate)) {
			
			if (fits(cal, factor)) {
				results.add(cal);
				// process the subproblem
				Calendar newStart = Calendar.getInstance();
				newStart.setTime(cal.getTime());
				newStart.set(Calendar.DAY_OF_MONTH, newStart.get(Calendar.DAY_OF_MONTH)+1);
				search(n, factor, newStart, endDate, results);
				results.remove(results.size()-1);
			}
			cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH)+1);
		}
	}
	
	
	public static int day(Calendar cal, boolean minus) { int val = cal.get(Calendar.DAY_OF_MONTH); if (minus) val = -val; return val; }
	public static int month(Calendar cal, boolean minus) { int val = cal.get(Calendar.MONTH) + 1; if (minus) val = -val; return val; }
	public static int year(Calendar cal, boolean prefix) { if (prefix) return cal.get(Calendar.YEAR); return cal.get(Calendar.YEAR) - 1900; }
	
	public static String stringFor(Calendar cal, boolean yearPrefix) {
		String date = "";
		date += (cal.get(Calendar.MONTH)+1) + "/";
		date += cal.get(Calendar.DAY_OF_MONTH) + "/";
		if (yearPrefix) date += cal.get(Calendar.YEAR);
		else date += (cal.get(Calendar.YEAR) - 1900);
		return date;
	}
	
	public static void main(String[] args) {
		Calendar min = Calendar.getInstance();
		min.setTimeInMillis(0);
		min.set(Calendar.YEAR, 1968);
		min.set(Calendar.MONTH, 0);
		min.set(Calendar.DAY_OF_MONTH, 1);
		Calendar max = Calendar.getInstance();
		max.setTimeInMillis(0);
		max.set(Calendar.YEAR, 1970);
		max.set(Calendar.MONTH, 11);
		max.set(Calendar.DAY_OF_MONTH, 31);
		
		//randomDates(10000, min, max);
		//patterns(9, 10000, min, max);
		//search(4, 5, min, max);
		//List<Calendar> list = datesFor(5, min, max);
		//dump(list);
		
		/*
		clocksearch(4, 5, min, max, new int[] {6,8,9,10});
		clocksearch(4, 5, min, max, new int[] {6,8,9,11});
		clocksearch(4, 5, min, max, new int[] {6,8,10,11});
		clocksearch(4, 5, min, max, new int[] {6,9,10,11});
		clocksearch(4, 5, min, max, new int[] {8,9,10,11});
		*/
		
		clocksearch(5, 5, min, max, new int[] {6,8,9,10,11});
		
	}
	
	

}
