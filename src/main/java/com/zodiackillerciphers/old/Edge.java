package com.zodiackillerciphers.old;

import gnu.trove.THashSet;
import java.io.Serializable;

public class Edge implements Serializable {
	public String u; // endpoint
	public String v; // endpoint;
	
	public Edge(String u, String v) {
		this.u = u; 
		this.v = v;
	}
	
	public Edge(Edge e) {
		this.u = e.u; 
		this.v = e.v;
	}
	

	public boolean equals(Object obj) {
		Edge e1 = (Edge) obj;
		Edge e2 = (Edge) this;
		if (e1.u.equals(e2.u) && e1.v.equals(e2.v)) return true;
		if (e1.u.equals(e2.v) && e1.v.equals(e2.u)) return true;
		return false;
	}



	public int hashCode() {
		if (u.hashCode() < v.hashCode()) return (u+v).hashCode();
		else return (v+u).hashCode();
	}

	public String toString() {
		return "(" + u + "," + v + ")";
	}

	public static void main(String[] args) {
		Edge e1 = new Edge("x","y");
		Edge e2 = new Edge("y","x");
		Edge e3 = new Edge("a","a");
		Edge e4 = new Edge("a","a");
		System.out.println(e1.equals(e2));
		System.out.println(e2.equals(e3));
		System.out.println(e1.equals(e3));
		System.out.println(e4.equals(e3));
		THashSet<Edge> hash = new THashSet<Edge>();
		hash.add(e1);
		hash.add(e2);
		hash.add(e3);
		hash.add(e4);
		System.out.println(hash.size());
	}
}
