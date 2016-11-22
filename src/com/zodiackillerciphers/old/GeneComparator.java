package com.zodiackillerciphers.old;
import java.util.Comparator;

public class GeneComparator implements Comparator {
		public int compare(Object o1, Object o2) {
			CipherGene g1 = (CipherGene) o1;
			CipherGene g2 = (CipherGene) o2;
			if (g1.shitness < g2.shitness) return 1;
			if (g1.shitness > g2.shitness) return -1;
			else return 0;
		}
	}

