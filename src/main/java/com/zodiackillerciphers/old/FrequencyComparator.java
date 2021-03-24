package com.zodiackillerciphers.old;
import java.util.Comparator;


public class FrequencyComparator implements Comparator {
		public int compare(Object o1, Object o2) {
			Frequency f1 = (Frequency) o1;
			Frequency f2 = (Frequency) o2;
			if (f1.total < f2.total) return 1;
			if (f1.total > f2.total) return -1;
			else return 0;
		}
	}
