package com.zodiackillerciphers.old;
import java.util.ArrayList;

import ec.EvolutionState;
import ec.vector.*;

import gnu.trove.*;


public class VertexCoverIndividual extends BitVectorIndividual {

	
	/* perform heuristic vertex crossover (Ketan Kotecha and Nilesh Gambhava) */
	public void defaultCrossover(EvolutionState state, int thread,
			VectorIndividual ind) {
		
		/* clone the edges so we can remove em non-destructively */
		THashSet<Edge> e = VertexCoverProblem.cloneEdges(VertexCoverProblem.edges);
		
		/* count number of parents that have each vertex in its cover */
		int[] f = new int[VertexCoverProblem.totalVertices];
		boolean[] g1 = ((BitVectorIndividual)this).genome;
		boolean[] g2 = ((BitVectorIndividual)ind).genome;
		for (int i=0; i<f.length; i++) {
			if (g1[i]) f[i]++;
			if (g2[i]) f[i]++;
		}
		
		
		/* create table of tuples (N(v), F(v), v).
		 * 
		 * N(v): number of edges incident to vertex v (a.k.a. degree of vertex)
		 * F(v): number of parents having v in their vertex cover
		 * v: vertex number
		 *  
		 **/
		int[][] vt = new int[VertexCoverProblem.totalVertices][3];
		for (int i=0; i<VertexCoverProblem.totalVertices; i++) {
			vt[i] = new int[] { VertexCoverProblem.edgeCounts[i], f[i], i }; 
		}
		/* sort in descending order of (N(v), F(v)) */
		sort(vt);
		
		/* select vertices until there are no more edges */
		this.genome = new boolean[VertexCoverProblem.totalVertices];
		
		int i=0; String tuple;
		while (e.size() > 0) {
			tuple = VertexCoverProblem.nodeIndex[vt[i][2]];
			this.genome[vt[i][2]] = true;
			for (Edge edge : VertexCoverProblem.edgesMap.get(tuple)) {
				e.remove(edge);
			}
			i++;
		}
		
		/* perform local optimization.  select nodes in random order, try to remove them one by one. */
		ArrayList<Integer> selections = new ArrayList<Integer>();
		for (i=0; i<genome.length; i++) {
			if (genome[i])
				selections.add(i);
		}
		
		int j;
		boolean[] newGenome = genome.clone();
		while (selections.size() > 0) {
			i=state.random[thread].nextInt(selections.size());
			j=selections.get(i);
			selections.remove(i);
			/* try to remove allele j from the node cover. */
			newGenome[j] = false;
			if (feasible(newGenome)) {
				// keep
				genome = newGenome;
			} else {
				// revert
				newGenome[j] = true;
			}
		}
		
		/*
		boolean[] newGenome = genome.clone();
		boolean best = true;
		for (i=0; i<newGenome.length; i++) {
			if (newGenome[i]) {
				newGenome[i] = false;
				if (feasible(newGenome)) {
					best = false;
					break;
				}
				newGenome[i] = true;
			}
		}
		if (best) state.output.message("best!");*/
		
	}

	/* mutate, but preserve feasibility of this vertex cover */
	public void defaultMutate(EvolutionState state, int thread) {
		boolean[] newGenome;
        VectorSpecies s = (VectorSpecies)species;  // where my default info is stored
        //if (s.mutationProbability>0.0)
        if (false) 
            for(int x=0;x<genome.length;x++)
                //if (state.random[thread].nextBoolean(s.mutationProbability)) {
            	if (false) {
                	/* if we remove a node from the cover, the # edges resulting must not be greater than zero */
                	/* we can always add a node to the cover. */
                	
                	newGenome = genome.clone();
                    newGenome[x] = !newGenome[x];
                    if (feasible(newGenome)) genome = newGenome;
                }
		
	}

	/* randomly create a feasible vertex cover */
	public void reset(EvolutionState state, int thread) {
		VertexCoverProblem.init();
		this.genome = new boolean[genome.length];
		
		//int edges = VertexCoverProblem.totalEdges;
		THashSet<Edge> e = VertexCoverProblem.cloneEdges(VertexCoverProblem.edges);
		int i; String tuple;
		while (e.size() > 0) {
			i = state.random[thread].nextInt(genome.length);
			if (!this.genome[i]) {
				tuple = VertexCoverProblem.nodeIndex[i];
				if (VertexCoverProblem.edgesMap.get(tuple) != null && VertexCoverProblem.edgesMap.get(tuple).size() > 0) {
					for (Edge edge : VertexCoverProblem.edgesMap.get(tuple)) {
						if (e.contains(edge)) {
							e.remove(edge);
							this.genome[i] = true;
						}
					}
				}
			}
		}
		
		state.output.message(genotypeToStringForHumans() + " " + fitness.fitnessToStringForHumans());
		if (!feasible(genome)) throw new RuntimeException("wtf, not feasible"); 
	}
	
	/* checks if the given genome is a feasible vertex cover */
	public static boolean feasible(boolean[] g) {
		int edges = VertexCoverProblem.totalEdges;
		//System.out.println("edges start " + edges);
		for (int i=0; i<g.length; i++) {
			edges -= (g[i] ? VertexCoverProblem.edgeCounts[i] : 0);
			
		}
		return (edges <= 0);
	}
	
	/* sort the given vertex table in descending order according to Ketan Kotecha and Nilesh Gambhava */
	public static void sort(int[][] vertexTable) {
		 /* each row of the 2D array represents a vertex and contains three integers.  
		  * the first value indicates the number of edges
		  * incident to the vertex (N).  the 2nd value indicates how many parents contain that vertex in their
		  * vertex cover (F).  the third is the vertex number.
		  * 
		  * the array is sorted in descending order of (N, F). in case of ties, it is broken using a random 
		  * selection. 
		  * 
		  */
		 java.util.Arrays.sort(vertexTable, new java.util.Comparator<int[]>() {
		      public int compare(int[] o1, int[] o2) {
		        if(o1[0] > o2[0])
		          return -1;
		        else if(o1[0] < o2[0])
		          return 1;
		        else {
		        	if (o1[1] > o2[1])
		        		return -1;
		        	else if (o1[1] < o2[1])
		        		return 1;
		        	else if (Math.random() > 0.5)
		        		return -1;
		        	else 
		        		return 1;
		        }
		      }
		 }
		 );
	}

	public static void main(String[] args) {
		int[][] feh = new int[][] { {5,2,1}, {4,1,2}, {5,2,3}, {3,2,4}, {2,1,6} };
		
		sort(feh);
		
		 String line;
		for (int i=0; i<feh.length; i++) {
			line = "";
			for (int j=0; j<feh[i].length; j++)
				line += feh[i][j];
			System.out.println(line);
		}
				
	}

	public String genotypeToStringForHumans() {
		String result = super.genotypeToStringForHumans() + " | ";
		for (int i=0; i<genome.length; i++) {
			if (!genome[i]) result += VertexCoverProblem.nodeIndex[i] + " ";
		}
		return result;
	}
}
