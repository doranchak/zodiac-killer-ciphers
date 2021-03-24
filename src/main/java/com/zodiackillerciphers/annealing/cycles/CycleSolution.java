package com.zodiackillerciphers.annealing.cycles;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.zodiackillerciphers.annealing.Solution;
import com.zodiackillerciphers.homophones.HomophonesNew;
import com.zodiackillerciphers.homophones.HomophonesResultBean;

public class CycleSolution extends Solution {

	/** the ciphertext */
	public String ciphertext;
	/** set of positions to remove */
	public Set<Integer> positions;
	/** positions removed since last mutation. */
	public Set<Integer> positionsReverse;

	/** max number of positions to remove */
	public static int MAX = 20;
	Random random = new Random();
	//double pcs2;
	// number of cycles found with runs of at least 5.  
	double score;

	public CycleSolution(String ciphertext) {
		this.ciphertext = ciphertext;
		iterations = 0;
	}
	
	public void add(int position) {
		positions.add(position);
		positionsReverse.add(position);
//		System.out.println("added " + position + " size " + positions.size() + " rev size " + positionsReverse.size());
	}
	
	@Override
	public boolean mutate() {
		for (int i = 0; i < random.nextInt(3) + 1; i++) {
			int pos = random.nextInt(ciphertext.length());
			add(pos);
		}
		return true;
	}

	@Override
	public void mutateReverse() {
//		System.out.println("mutateReverse");
		for (Integer pos : positionsReverse) {
			positions.remove(pos);
//			System.out.println("put back " + pos);
		}
		
		positionsReverse.clear();
	}
	@Override
	public void mutateReverseClear() {
		positionsReverse.clear();
	}
	
	@Override
	public String representation() {
		return decode() + "	" + positions;
	}
	
	@Override
	public double energy() {
		score = HomophonesNew.perfectCycleScoreFor(2, decode(), 3, false, 3.0);
		return -score;
	}
	public double energy2() {
		score = 0;
		if (positions.size() > MAX) {
			return Double.MAX_VALUE;
		} 
		List<HomophonesResultBean> beans = HomophonesNew.beansFor(2, decode(), false);
		for (HomophonesResultBean bean : beans) {
			int run = bean.getRun();
			if (run > 4) score += run-4; // run of 5 counts as 1 hit, run of 6 counts as 2 hits, etc.
		}
		//score -= ((double)positions.size())/ciphertext.length();
		return -score;
	}

	@Override
	public double energyCached() {
		return -score;
	}

	@Override
	public void initialize() {
		positions = new HashSet<Integer>();
		positionsReverse = new HashSet<Integer>();
	}
	
	/** cipher with the given positions removed */
	public String decode() {
		String result = "";
		for (int i = 0; i < ciphertext.length(); i++) {
			if (positions.contains(i)) continue;
			result += ciphertext.charAt(i);
		}
		return result;
	}

	public static void main(String[] args) {
		//testPartial();
	}

}
