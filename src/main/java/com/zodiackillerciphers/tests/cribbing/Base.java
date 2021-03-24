package com.zodiackillerciphers.tests.cribbing;

public class Base {
	public static void fatal(String message) {
		System.err.println("FATAL: " + message);
		Thread.dumpStack();
		System.exit(-1);
	}

	public static boolean DEBUG = false;

	public static void debug(String msg) {
		if (DEBUG)
			System.out.println(msg);
	}

	public static void info(String msg) {
		if (Search.INFO)
			System.out.println(msg);
	}
}
