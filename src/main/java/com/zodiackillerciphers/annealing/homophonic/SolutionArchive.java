package com.zodiackillerciphers.annealing.homophonic;

import java.util.Comparator;
import java.util.TreeSet;

import com.zodiackillerciphers.annealing.Solution;

public class SolutionArchive {
	/** max size of the archive */
	public static int maxSize = 10;
	/** set of solutions sorted by energy */
	public static TreeSet<Solution> archive = new TreeSet<Solution>(new Comparator<Solution>() {
		@Override
		public int compare(Solution o1, Solution o2) {
			return Double.compare(o1.energyCached(), o2.energyCached());
		}
	});
	/** add to the archive.  then if archive size exceeds maxSize, remove the worst
	 * one and return it. */
	public synchronized static Solution add(Solution solution) {
//		this method is now broken because i had to comment this out:  archive.add(solution.clone());
		System.out.println("Added to archive: " + solution);
		System.out.println("Archive contents: ");
		dumpArchive();
		if (archive.size() > maxSize) {
			Solution worst = archive.last();
			archive.remove(worst);
			System.out.println("Archive full, so removed worst solution: " + solution);
			return worst;
		}
		return null;
	}
	
	public static void dumpArchive() {
		for (Solution sol : archive) {
			System.out.println(" - solution: " + sol);
		}
	}
}
