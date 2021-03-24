package com.zodiackillerciphers.tests.bdholland;

import java.util.HashSet;
import java.util.Set;

/** a node is a single letter on the grid.*/ 
public class Node {
	
	char letter;
	int count;
	Set<Node> links;
	boolean visited;
	
	public Node(char letter, int count) {
		this.letter = letter;
		links = new HashSet<Node>();
		visited = false;
		this.count = count;
	}
	
	public void link(Node node) {
		links.add(node);
	}
	
	public static void link(Node a, Node b) {
		a.link(b);
		b.link(a);
	}

	@Override
	public String toString() {
//		String result = letter + "" + count + " -->";
//		for (Node node : links) result += " " + node.letter + node.count;
////		result += ", " + visited;
//		return result;
		return letter + "" + count;
	}
	
	public void visited() {
		visited = true;
	}
	public void unvisited() {
		visited = false;
	}

	public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}
}
