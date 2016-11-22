package com.zodiackillerciphers.old;

import java.util.HashSet;
import java.util.Set;

import ec.EvolutionState;
import ec.util.MersenneTwisterFast;
import ec.vector.IntegerVectorIndividual;
import ec.vector.IntegerVectorSpecies;
import ec.vector.VectorIndividual;

public class HomophonesIndividual extends IntegerVectorIndividual {

	String newAlphabet;
	String newCipher;
	
	float correctness;
	public float[] scoreReal;
	
	static int L = HomophonesProblem.H * HomophonesProblem.W;
	
	@Override
	public String genotypeToStringForHumans() {
		// TODO Auto-generated method stub
		StringBuffer sb = new StringBuffer();
		for (int g : genome) {
			//sb.append(g%(HomophonesProblem.H * HomophonesProblem.W));
			sb.append(g);
			sb.append(" ");
		}
		return sb.toString() +", scoreReal " + scoreReal(); 
	}
	
	String scoreReal() {
		if (scoreReal == null) return "N/A";
		String s = "";
		for (int i=0; i<scoreReal.length; i++) s += scoreReal[i] + " ";
		return s;
	}

	public void reset(EvolutionState state, int thread) {
        for(int x=0;x<genome.length;x++) {
        	genome[x] = x;
        }
	}
	
    /** Destructively mutates the individual in some default manner.  The default form
    simply randomizes genes to a uniform distribution from the min and max of the gene values. */
// notice that we bump to longs to avoid overflow errors
public void defaultMutate(EvolutionState state, int thread)
    {
    IntegerVectorSpecies s = (IntegerVectorSpecies) species;
    MersenneTwisterFast rand = state.random[thread]; 
    // choose from 4 different mutation operators
    int which = rand.nextInt(4);

    if (which == 0) { // row swaps
    	//System.out.println("Before " + this.genotypeToStringForHumans());
        for(int x=0;x<genome.length;x++) {
        	
        	// randomly swap 
            //if (rand.nextBoolean(s.mutationProbability)) {
        	if (false) {
                //genome[x] = randomValueFromClosedInterval((int)s.minGene(x), (int)s.maxGene(x), rand);
            	// swap with another genome to preserve a valid permutation
            	int i = randomValueFromClosedInterval(0, HomophonesProblem.H-1, rand);
            	int tmp = genome[i];
            	genome[i] = genome[x];
            	genome[x] = tmp;
            }
            
            // randomly flip between reverse and normal order
            //if (rand.nextBoolean(s.mutationProbability)) {
        	if (false) {
            	genome[x] = (genome[x] + HomophonesProblem.H) % (2 * HomophonesProblem.H); 
            }
            
        }
    	//System.out.println("After " + this.genotypeToStringForHumans());
    } else if (which == 1) { // move
		int a = 0;
		int b = 0;
		while (a==b) {
			a = (int) (rand.nextInt(genome.length));
			b = (int) (rand.nextInt(genome.length));
		}
		int d = 1 + (int)(rand.nextInt(5));
		int H = 1 + (int)(rand.nextInt(genome.length));
		
		mutateMove(genome, a, b, H, d);
    	
    } else if (which == 2) { // flip
		int a = (int) (rand.nextInt(genome.length));
		int d = 1 + (int)(rand.nextInt(5));
		int H = 1 + (int)(rand.nextInt(genome.length));
		boolean direction = rand.nextBoolean();
		mutateFlip(genome, direction, a, H, d);
    	
    } else if (which == 3) { // interleave
		int start = (int) (rand.nextInt(genome.length));
		int dOld = 1 + (int)(rand.nextInt(5));
		int dNew = 1 + (int)(rand.nextInt(5));
		int H = 1 + (int)(rand.nextInt(genome.length));
		mutateInterleave(genome, dOld, dNew, start, H);
    	
    } else throw new RuntimeException("which wtf " + which);
    }

	// out of bounds?
	static boolean oob(int[] genome, int pos) {
		return (pos < 0 || pos >= genome.length);
	}

	// move a block.  
	//	from: block's original position
	//  to: block's new position
    //  H: block height
    //  d: interleaving amount
	static void mutateMove(int[] genome, int from, int to, int H, int d) {
		if (d<1) return;
		
		int[] newgenome = new int[genome.length];
		for (int i=0; i<genome.length; i++) newgenome[i] = -1;
		
		int num = 0; int i = from; int j;
		while (num < H) {			
			j = to+i-from;
			if (!oob(genome,i) && !oob(genome,j)) {
				newgenome[j] = genome[i]; genome[i] = -1;
			}
			i += d;
			num++;
		}
		
		merge(genome, newgenome);
	}

	static void merge(int[] oldgenome, int[] newgenome) {
		int i=0; int j=0; boolean quit = false;
		while (i<oldgenome.length && j<newgenome.length) {
			while (oldgenome[i] == -1) {
				i++;
				if (oob(oldgenome, i)) { quit = true; break; }
			}
			while (newgenome[j] > -1) {
				j++;
				if (oob(newgenome, j)) { quit = true; break; }
			}
			if (quit) break;
			newgenome[j] = oldgenome[i];
			i++; j++;
		}
		for (i=0; i<oldgenome.length; i++) oldgenome[i] = newgenome[i];
	}
	static void swap(int[] genome, int i, int j) {
		if (oob(genome, i)) return;
		if (oob(genome, j)) return;
		
		int tmp = genome[j];
		genome[j] = genome[i];
		genome[i] = tmp;
	}
	
	static void testMutateMove() {
		
		for (int i=0; i<100; i++) {
			int[] genome = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23};
			int a = 0;
			int b = 0;
			while (a==b) {
				a = (int) (Math.random() * genome.length);
				b = (int) (Math.random() * genome.length);
			}
			int d = 1 + (int)(Math.random()*5);
			int H = 1 + (int)(Math.random()*10);
			
			mutateMove(genome, a, b, H, d);
			
			System.out.println("a " + a + " b " + b + " H " + H + " d " + d + ": " + HomophonesProblem.genomeToString(genome));
		}
	}
	
	// flip a block horizontally (direction = false) or vertically (direction = true)
	static void mutateFlip(int[] genome, boolean direction, int start, int H, int d) {
		int num = 0; int from = start;
		while (num < H) {
			
			if (direction) {
				//System.out.println("num " + num + " x " + start + " y " + (from+(H-num-1)*d));
				swap(genome, start, from+(H-num-1)*d);
			}
			else {
				genome[start] += genome.length;
				genome[start] %= (genome.length*2);
			}
			
			num++;
			start += d;
			if (start >= genome.length) break;
			if (direction && num == H/2) break;
		}
	}

	static void testMutateFlip() {
		
		for (int i=0; i<100; i++) {
			int[] genome = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23};
			int a = (int) (Math.random() * genome.length);
			int d = 1 + (int)(Math.random()*5);
			int H = 1 + (int)(Math.random()*10);
			boolean direction = Math.random() > 0.5;
			mutateFlip(genome, direction, a, H, d);
			
			System.out.println("a " + a + " dir " + direction + " H " + H + " d " + d + ": " + HomophonesProblem.genomeToString(genome));
		}
	}
	
	// change the interleaving value for the given set of rows
	static void mutateInterleave(int[] genome, int dOld, int dNew, int start, int H) {
		if (dOld == dNew) return;
		int[] newgenome = new int[genome.length];
		for (int i=0; i<genome.length; i++) newgenome[i] = -1;
		for (int num=0; num<H; num++) {
			int i = start + num*dOld;
			int j = start + num*dNew;
			if (oob(genome, i)) continue;
			if (oob(genome, j)) continue;
			newgenome[j] = genome[i];
			genome[i] = -1;
		}
		
		merge(genome, newgenome);
	}

	static void testMutateInterleave() {
		
		for (int i=0; i<100; i++) {
			int[] genome = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23};
			int start = (int) (Math.random() * genome.length);
			int dOld = 1 + (int)(Math.random()*5);
			int dNew = 1 + (int)(Math.random()*5);
			int H = 1 + (int)(Math.random()*10);
			mutateInterleave(genome, dOld, dNew, start, H);
			System.out.println("dOld " + dOld + " dNew " + dNew + " start " + start + " H " + H + ": " + HomophonesProblem.genomeToString(genome));
		}
	}
	

	public void defaultCrossover(EvolutionState state, int thread,
			VectorIndividual ind) {
		// DO NOTHING
	}
	
	public static void main(String[] args) {
		testMutateInterleave();
	
	}
	

}
