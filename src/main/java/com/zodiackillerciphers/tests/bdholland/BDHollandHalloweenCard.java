package com.zodiackillerciphers.tests.bdholland;

import java.util.ArrayList;
import java.util.List;

import com.zodiackillerciphers.dictionary.WordFrequencies;
import com.zodiackillerciphers.names.Census;
import com.zodiackillerciphers.names.Name;

/** https://www.reddit.com/r/SkeletonsAreDancing/comments/f8bxm9/zodiac_tim_holt_halloween_card_farley_name/ */
public class BDHollandHalloweenCard {
	public static void search(int minLength, int maxLength, int maxSkips) {
		Node a1 = new Node('A', 1);
		Node a2 = new Node('A', 2);
		Node a3 = new Node('A', 3);
		Node b1 = new Node('B', 1);
		Node b2 = new Node('B', 2);
		Node b3 = new Node('B', 3);
		Node b4 = new Node('B', 4);
		Node c1 = new Node('C', 1);
		Node d1 = new Node('D', 1);
		Node e1 = new Node('E', 1);
		Node e2 = new Node('E', 2);
		Node e3 = new Node('E', 3);
		Node e4 = new Node('E', 4);
		Node e5 = new Node('E', 5);
		Node f1 = new Node('F', 1);
		Node f2 = new Node('F', 2);
		Node g1 = new Node('G', 1);
		Node i1 = new Node('I', 1);
		Node i2 = new Node('I', 2);
		Node i3 = new Node('I', 3);
		Node k1 = new Node('K', 1);
		Node l1 = new Node('L', 1);
		Node n1 = new Node('N', 1);
		Node n2 = new Node('N', 2);
		Node o1 = new Node('O', 1);
		Node p1 = new Node('P', 1);
		Node p2 = new Node('P', 2);
		Node r1 = new Node('R', 1);
		Node r2 = new Node('R', 2);
		Node r3 = new Node('R', 3);
		Node s1 = new Node('S', 1);
		Node s2 = new Node('S', 2);
		Node u1 = new Node('U', 1);
		Node v1 = new Node('V', 1);
		Node y1 = new Node('Y', 1);
		Node y2 = new Node('Y', 2);
		Node y3 = new Node('Y', 3);
		Node y4 = new Node('Y', 4);

		Node.link(b1, y1);
		Node.link(b1, f1);

		Node.link(y1, p1);
		Node.link(y1, a1);
		Node.link(y1, f1);

		Node.link(f1, i1);
		Node.link(f1, p1);
		Node.link(f1, a1);

		Node.link(i1, r1);
		Node.link(i1, a1);
		Node.link(i1, r2);

		Node.link(r1, a1);
		Node.link(r1, r2);
		Node.link(r1, e1);

		Node.link(e1, r2);
		Node.link(e1, s1);
		Node.link(e1, l1);

		Node.link(b2, p1);
		Node.link(b2, y2);
		Node.link(b2, g1);
		Node.link(b2, a1);
		
		Node.link(y2, g1);

		Node.link(g1, a1);
		Node.link(g1, u1);

		Node.link(u1, a1);
		Node.link(u1, r2);
		Node.link(u1, n1);

		Node.link(n1, r2);
		Node.link(n1, v1);
		Node.link(n1, e2);
		Node.link(n1, s2);

		Node.link(p1, a1);
		Node.link(p1, g1);
		
		Node.link(a1, r2);
		
		Node.link(r2, l1);
		Node.link(r2, a2);
		Node.link(r2, v1);

		Node.link(s1, l1);
		Node.link(s1, b3);

		Node.link(l1, a2);
		Node.link(l1, b3);
		Node.link(l1, d1);

		Node.link(a2, v1);
		Node.link(a2, d1);

		Node.link(v1, e2);
		Node.link(v1, d1);
		Node.link(v1, b4);

		Node.link(e2, s2);
		Node.link(e2, y4);
		Node.link(e2, b4);

		Node.link(s2, b4);
		Node.link(s2, y4);

		Node.link(b3, d1);
		Node.link(b3, y3);

		Node.link(y3, d1);
		Node.link(y3, i3);
		Node.link(y3, k1);

		Node.link(k1, d1);
		Node.link(k1, i3);
		Node.link(k1, c1);
		Node.link(k1, n2);

		Node.link(n2, i3);
		Node.link(n2, c1);
		Node.link(n2, e4);
		Node.link(n2, i2);

		Node.link(i2, c1);
		Node.link(i2, e4);
		Node.link(i2, f2);

		Node.link(f2, c1);
		Node.link(f2, e4);
		Node.link(f2, e3);

		Node.link(e3, e4);

		Node.link(d1, b4);
		Node.link(d1, r3);
		Node.link(d1, i3);

		Node.link(i3, b4);
		Node.link(i3, r3);
		Node.link(i3, o1);
		Node.link(i3, c1);

		Node.link(c1, r3);
		Node.link(c1, o1);
		Node.link(c1, p2);
		Node.link(c1, e5);
		Node.link(c1, e4);

		Node.link(e4, p2);
		Node.link(e4, e5);

		Node.link(b4, y4);
		Node.link(b4, r3);

		Node.link(y4, r3);

		Node.link(r3, o1);
		Node.link(o1, p2);

		Node.link(p2, e5);
		
		List<Node> nodes = new ArrayList<Node>();
		nodes.add(a1);
		nodes.add(a2);
		nodes.add(a3);
		nodes.add(b1);
		nodes.add(b2);
		nodes.add(b3);
		nodes.add(b4);
		nodes.add(c1);
		nodes.add(d1);
		nodes.add(e1);
		nodes.add(e2);
		nodes.add(e3);
		nodes.add(e4);
		nodes.add(e5);
		nodes.add(f1);
		nodes.add(f2);
		nodes.add(g1);
		nodes.add(i1);
		nodes.add(i2);
		nodes.add(i3);
		nodes.add(k1);
		nodes.add(l1);
		nodes.add(n1);
		nodes.add(n2);
		nodes.add(o1);
		nodes.add(p1);
		nodes.add(p2);
		nodes.add(r1);
		nodes.add(r2);
		nodes.add(r3);
		nodes.add(s1);
		nodes.add(s2);
		nodes.add(u1);
		nodes.add(v1);
		nodes.add(y1);
		nodes.add(y2);
		nodes.add(y3);
		nodes.add(y4);

		WordFrequencies.init();
		for (String word : WordFrequencies.map.keySet()) {
			if (word.length() < minLength) continue; 
			if (word.length() > maxLength) continue; 
			search(word, nodes, maxSkips);
		}
		Census.init();
		System.out.println("============= NAMES");
		for (Name name : Census.firstMale) {
			String n = name.name;
			if (n.length() < minLength) continue; 
			if (n.length() > maxLength) continue; 
			search(n, nodes, maxSkips);
		}
		for (Name name : Census.last) {
			String n = name.name;
			if (n.length() < minLength) continue; 
			if (n.length() > maxLength) continue; 
			search(n, nodes, maxSkips);
		}
	}
	
	public static void search(String search, List<Node> nodes, int maxSkips) {
		for (Node node : nodes) {
//			System.out.println("Starting search at " + node);
			search(search, node, 0, new ArrayList<Node>(), maxSkips, 0);
		}
	}
	public static void search(String search, Node node, int current, List<Node> path, int skipsRemaining, int depth) {
//		String str = "";
//		for (int i=0; i<depth; i++) str += "   ";
//		str += "current " + current + " matched " + (current == 0 ? "" : search.substring(0, current)) + " node " + node + " skips " + skipsRemaining;
//		System.out.println(str);
		
		boolean match = true;
		
		if (node.isVisited()) {
			match = false; // stop condition: we already visited this node
		} else if (!match(node.letter, search.charAt(current))) {
			match = false; // stop condition: this node's letter does not match the search.
		}
		if (!match) {
			// stop condition: no more skipped letters remaining
			if (skipsRemaining == 0) return;
			skipsRemaining--;
		} else {
			path.add(node); // we matched so track the path so far
			// did we match the entire word?
			if (current == search.length()-1) {
				// stop condition: we found a match
				System.out.println(search.length() + "	" + search + "	" + WordFrequencies.percentile(search) + "	" + path);
				path.remove(path.size()-1);
				return;
			}
			
			// otherwise, partial match, so update word index and visit state
			current++;
			node.visited(); // mark this node visited		
		}
		// then search all the linked nodes
		for (Node linked : node.links) {
			search(search, linked, current, path, skipsRemaining, depth+1);
		}
		if (match) {
			path.remove(path.size()-1);
			node.unvisited(); // done visiting this node
		}
	}
	public static boolean match(char c1, char c2) {
		if (c1 == c2) return true;
		return false;
//		if (c1 == 'B') c1 = 'E';
//		if (c2 == 'B') c2 = 'E';
//
//		if (c1 == 'R') c1 = 'P';
//		if (c2 == 'R') c2 = 'P';
//
//		if (c1 == 'E') c1 = 'F';
//		if (c2 == 'E') c2 = 'F';
//		
//		if (c1 == c2) return true;
	}
	public static void main(String[] args) {
		search(4, 80, 0);
	}
}
