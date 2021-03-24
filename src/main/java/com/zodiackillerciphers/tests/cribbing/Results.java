package com.zodiackillerciphers.tests.cribbing;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/** maintain a fixed sized heap of the best results */
public class Results extends Base {
	/** use TreeSet to implement min/max heap of fixed number of elements.
	 * 
	 *  */
	TreeSet<Result> treeSet;
	/** max size of heap */
	int maxHeapSize = 10000;
	/** hashset to enforce unique plaintext solutions */
	Set<String> seen;
	public static boolean SHOW_NEW_BEST = true;
	/** track score of best and worst heap entry */
	float bestScore;
	float worstScore;
	
	public Results() {
		treeSet = new TreeSet<Result>();
		seen = new HashSet<String>();
		worstScore = 0;
		bestScore = Float.MIN_VALUE;
	}
	
	/** add new Result */
	public void addResult(Result result, int cipherLength, boolean maximizePolyphones) {
		debug("ADDING " + result);
		if (result.getLength() == cipherLength && !maximizePolyphones) {
//			System.out.println("FITS ENTIRE CIPHER: " + result);
		}
		try {
			if (result.getScore() > bestScore) {
				bestScore = result.getScore();
				if (SHOW_NEW_BEST) System.out.println("NEW BEST: " + result);
			}
				
			// if seen, ignore
			if (seen.contains(result.getPlaintext())) {
				debug("already seen " + result.getPlaintext());
				return;
			}
			// if heap not full, just add it
			if (treeSet.size() < maxHeapSize) {
				treeSet.add(result);
				seen.add(result.getPlaintext());
				debug("heap has room");
				debug("HEAPWORTHY 1: " + result);
				return;
			}

			// don't bother with heap operations if the result is no better than the worst score we've seen. 
			if (result.getScore() < worstScore) {
				debug("result " + result.getScore() + " is not better than worst " + worstScore);
				return;
			}
			
			debug("HEAPWORTHY 2: " + result);
			
			// other wise, add to seen and to heap, and remove worst member of heap, and remove worst member from seen
			seen.add(result.getPlaintext());
			treeSet.add(result);
			Result worst = treeSet.last();
			seen.remove(worst.getPlaintext());
			debug("Removing worst: " + worst);
			debug("removed: " + treeSet.remove(worst));
			debug("Done removing worst: " + worst);
			
			// we might now have a new worst score 
			worstScore = treeSet.last().getScore();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (treeSet.size() > maxHeapSize) 
				fatal("WTF, heap is too big.");
		}
		
	}
	/** dump the results */
	public void dump() {
		System.out.println(" ======= DUMPING HEAP === ");
		Iterator<Result> it = treeSet.iterator();
		while (it.hasNext())
			System.out.println(it.next());
		System.out.println(" ======= DONE DUMPING HEAP === ");
		
	}
	
	
	/**
	 * @return the treeSet
	 */
	public TreeSet<Result> getTreeSet() {
		return treeSet;
	}
	/**
	 * @param treeSet the treeSet to set
	 */
	public void setTreeSet(TreeSet<Result> treeSet) {
		this.treeSet = treeSet;
	}
	/**
	 * @return the maxHeapSize
	 */
	public int getMaxHeapSize() {
		return maxHeapSize;
	}
	/**
	 * @param maxHeapSize the maxHeapSize to set
	 */
	public void setMaxHeapSize(int maxHeapSize) {
		this.maxHeapSize = maxHeapSize;
	}
	/**
	 * @return the seen
	 */
	public Set<String> getSeen() {
		return seen;
	}
	/**
	 * @param seen the seen to set
	 */
	public void setSeen(Set<String> seen) {
		this.seen = seen;
	}
	
}
