package com.zodiackillerciphers.old.rectangle;

/** These are the different ways to read the cipher text within a rectangle */
public enum Type {
	
	/** the 8 ways to start at a corner and read normally */
	NORMAL_C1_D1,
	NORMAL_C1_D2,
	NORMAL_C2_D1,
	NORMAL_C2_D2,
	NORMAL_C3_D1,
	NORMAL_C3_D2,
	NORMAL_C4_D1,
	NORMAL_C4_D2,
	
	TRANSPOSE,
	
	SPIRAL,
	
	RAIL_FENCE,
	
	ZIG_ZAG,
	
	DIAGONAL,
	
	INTERLEAVE
	
	
}
