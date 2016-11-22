package com.zodiackillerciphers;

import ec.Individual;
import ec.multiobjective.MultiObjectiveFitness;

public class Utils {
	public static boolean DEBUG = false;
	public static int W = 17;
	
	/** returns -1 if out of bounds */
	public static int posFrom(int row, int col) {
		int result = row*W + col;
		if (result < 0 || result > 339) {
			return -1;
		}
		return result;
	}

	public static int rowFrom(int pos) {
		return pos/W;
	}

	public static int colFrom(int pos) {
		return pos % W;
	}
	
	public static int posEast(int row, int col) {
		return Utils.posFrom(row, col+1);
	}
	
	public static int posSouth(int row, int col) {
		return Utils.posFrom(row+1, col);
	}
	public static int posWest(int row, int col) {
		return Utils.posFrom(row, col-1);
	}
	public static int posNorth(int row, int col) {
		return Utils.posFrom(row-1, col);
	}
	public static int posEast(int pos) {
		return Utils.posEast(rowFrom(pos), colFrom(pos));
	}
	public static int posSouth(int pos) {
		return Utils.posSouth(rowFrom(pos), colFrom(pos));
	}
	public static int posWest(int pos) {
		return Utils.posWest(rowFrom(pos), colFrom(pos));
	}
	public static int posNorth(int pos) {
		return Utils.posNorth(rowFrom(pos), colFrom(pos));
	}
	
	public static void debug(String msg) {
		if (DEBUG) {
			Thread currentThread = Thread.currentThread();
			System.out.println(currentThread.hashCode() + ": " + msg);
		}
	}

	public static String info(Object o) {
		if (o == null) return null;
		String result = "hashCode " + o.hashCode() + " objectId " + System.identityHashCode(o) + " objectives ";
		if (o instanceof Individual) {
			Individual i = (Individual) o;
			MultiObjectiveFitness m = (MultiObjectiveFitness) i.fitness;
			if (m != null && m.getObjectives() != null) for (double d : m.getObjectives()) result += d + " ";
		}
		return result;
	}
	

}
