package com.zodiackillerciphers.old;

import ec.EvolutionState;
import ec.simple.*;
import ec.Individual;
import ec.Problem;
import ec.simple.SimpleProblemForm;
import ec.util.Parameter;
import ec.vector.*;
import gnu.trove.THashMap;
import gnu.trove.THashSet;

import java.util.*;

/** Our representation of a vertex cover problem.
 * 
 * A node is a (word,pos) pair.  An edge between two nodes indicates conflict between the pair.
 * Conflict is defined as inconsistent decodings for cipher symbols shared by the pair.
 * 
 * ASSUMPTION: max gene is no greater than the number of nodes in this graph.
 * 
 * @author doranchak
 *
 */
public class VertexCoverProblem extends Problem { //implements SimpleProblemForm {
	//public static THashMap<String, THashSet<String>> leftEdges; /* maps (word,pos) pairs to other (word,pos) pairs with which they are conflicted.  This is the "left vertex (edge endpoint)" index. */
	//public static THashMap<String, THashSet<String>> rightEdges; /* maps (word,pos) pairs to other (word,pos) pairs with which they are conflicted.  This is the "right vertex (edge endpoint)" index. */

	/* map every (word,pos) vertex to all of its incident edges */
	public static THashMap<String, THashSet<Edge>> edgesMap; 
	/* set of all edges */
	public static THashSet<Edge> edges; 
	
	
	public static THashSet<String> nodes; /* all nodes */
	public static String[] nodeIndex; /* all nodes */
	public static THashMap<String, Integer> nodeMap; /* maps a node to its index in the node index */
	
	public static int[] edgeCounts; // all edge counts indexed by vertex
	public static int totalEdges; // total number of edges in this graph
	public static int totalVertices; // total number of vertices in this graph
	
	static boolean DEBUG = true;
	
	/* we might need to force init of leftEdges and rightEdges from somewhere, which
	 *  only needs to be done once. */
	/*private VertexCoverProblem() {
	}*/
	
	//public static void initProblem(THashMap<String, THashSet<String>> l, THashMap<String, THashSet<String>> r) {
	public static void initProblem(THashMap<String, THashSet<Edge>> map) {
		//leftEdges = l;
		//rightEdges = r;
		edgesMap = map;
		edges = new THashSet<Edge>();
		
		//say("right " + r.size() + " left " + l.size());
		/* create a node index */
		nodes = new THashSet<String>();
		for (String n : map.keySet()) {
			nodes.add(n);
			for (Edge e : map.get(n)) {
				edges.add(new Edge(e));
			}
		}
		//for (String n : rightEdges.keySet()) {
		//	nodes.add(n);
		//}
		
		say("nodes " + nodes.size());
		
		totalVertices = nodes.size();
		
		nodeIndex = new String[nodes.size()];
		nodeMap = new THashMap<String, Integer>();
		int i=0;
		for (String n : nodes) {
			nodeIndex[i] = n; 
			nodeMap.put(n, i);
			i++;
		}
		
		/* create an edge count index and total edge count*/
		totalEdges = edges.size();
		edgeCounts = new int[nodeIndex.length];
		for (String n : edgesMap.keySet()) {
			edgeCounts[nodeMap.get(n)] += edgesMap.get(n).size();
		}
		
		
	}
	
	
	
	public void describe(Individual ind, EvolutionState state, int threadnum,
			int log, int verbosity) {
	    state.output.println(ind.genotypeToStringForHumans() + ":" + ind.fitness.fitnessToStringForHumans(),
	            verbosity,log);
		
	}

	public void evaluate(EvolutionState state, Individual ind, int threadnum) {
		if (ind.evaluated) return;
		
		/* an individual is a bit array of size n, where n is the number of nodes we can represent via a gene */
		BitVectorIndividual b = (BitVectorIndividual) ind;
		
		int f = b.genome.length;
		for (int i=0; i<f; i++) {
			if (b.genome[i]) f--;
		}
		((SimpleFitness) ind.fitness).setFitness(state, f, false);
		//state.output.message(ind.genotypeToStringForHumans() + " " + ind.fitness.fitnessToStringForHumans());
		
	}

	/** Use brute force to locate optimum vertex covers of the given size. 
	 *  
	 * @param k size of vertex cover to find
	 * @param firstFound if true, return only the first found vertex cover rather than all found ones.
	 * @return List of vertex covers, themselves represented as lists. 
	 */ 
	public List<List<String>> findOptimumVertexCover(int k, boolean firstFound) {
		ArrayList<List<String>> result = new ArrayList<List<String>>();
		
		
		/* degenerate case: no edges.  just return all possible vertex covers anyway? */
		int i;
		/* init cover */
		int[] cover = new int[k];
		int currentCount = totalEdges;
		for (i=0; i<k; i++) {
			cover[i] = i;
			currentCount -= edgeCounts[i];
			
			/* we may have found the vertex cover upon init */
			if (currentCount == 0) {
				addCoverToList(cover, result, nodeIndex);
				if (firstFound) return result;
			}
		}
		
		say("total edges: " + totalEdges);
		
		String line;
		boolean go = true;
		while (go) {
			for (i=k-1; i>=0; i--) {
				/* stopping condition */
				if (i==0 && cover[i] == nodes.size()-k) {
					go = false;
					break;
				}
				currentCount += edgeCounts[cover[i]]; /* restore count before changing cover node index */
				cover[i] ++;
				if (cover[i] < nodes.size()) {
					currentCount -= edgeCounts[cover[i]];
					break;
				} else {
					cover[i] = 0;
					currentCount -= edgeCounts[cover[i]];
				}
			} 

			line = "";
			for (int j=0; j<k; j++) {
				line += cover[j] + ",";
			}
			line += ": " + currentCount;
			say(line);
			
			if (!go) break;
			
		}
		
		
		return result;
	}
	
	/** add the given node cover to the results list. 
	 * 
	 * @param cover int array indicating nodes in the cover
	 * @param list results list we want to add to
	 * @param nodeIndex index of nodes
	 */
	public static void addCoverToList(int[] cover, List<List<String>> list, String[] nodeIndex) {
		ArrayList<String> nodes = new ArrayList<String>();
		for (int i=0; i<cover.length; i++) {
			nodes.add(nodeIndex[i]);
		}
		list.add(nodes);
	}
	
	public static void say(String msg) {
		if (DEBUG) System.out.println(msg);
	}

	/** clone a brand new independent copy of the given hash of edges */
	public static THashSet<Edge> cloneEdges(THashSet<Edge> edges) {
		
		THashSet<Edge> newSet = new THashSet<Edge>();
		
		for (Edge e : edges) {
			newSet.add(new Edge(e));
		}
		return newSet;
		
	}
	
	public static void init() {
		if (edges == null) {
	  		ZodiacDictionary.USE_ISWORDHASH = false;

	  		/* known solution */
	  		int w = CipherWordGene.wordPool.length;
	  		
	  		/*CipherWordGene gene = CipherWordGene.runTestProgressive(new int[] {  //llbecomemyslavesiwillnotgiveyoumynamebecauseyouwill
	  				CipherWordGene.getAlleleFromWord("become")+w, 2,
	  				CipherWordGene.getAlleleFromWord("my")+w, 8,
	  				CipherWordGene.getAlleleFromWord("slaves")+w, 10,
	  				CipherWordGene.getAlleleFromWord("i")+w, 16,
	  				CipherWordGene.getAlleleFromWord("will")+w, 17,
	  				CipherWordGene.getAlleleFromWord("not")+w, 21,
	  				CipherWordGene.getAlleleFromWord("give")+w, 24,
	  				CipherWordGene.getAlleleFromWord("you")+w, 28,
	  				CipherWordGene.getAlleleFromWord("my")+w, 31,
	  				CipherWordGene.getAlleleFromWord("name")+w, 33,
	  				CipherWordGene.getAlleleFromWord("because")+w, 37,
	  				CipherWordGene.getAlleleFromWord("you")+w, 44,
	  				CipherWordGene.getAlleleFromWord("will")+w, 47
	  		});*/
	  		
	  		/* wordpool-1000 */
	  		CipherWordGene gene;
	  		gene = CipherWordGene.runTestProgressive(new int[] {
  				//98,1328,  1963,476,  1097,649,  0,0,  0,0,  169,1665,  1510,1755,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  1741,10,  0,0
	  			544,226,  1505,1518,  1743,1897,  0,0,  0,0,  1692,1206,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  0,0,  377,935,  0,0,  0,0,  0,0,  0,0

			});

	  		initProblem(gene.edgeMap);
		}
	}
	
	
}
