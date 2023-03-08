package com.zodiackillerciphers.ciphers.algorithms.columnar;

public enum Variant {
	TOP_TO_BOTTOM, // columns are read from top to bottom (standard columnar transposition)
	BOTTOM_TO_TOP, // columns are read from bottom to top
	ALTERNATING_1, // columns are read alternating between top-to-bottom, and bottom-to-top
	ALTERNATING_2 // columns are read alternating between bottom-to-top, top-to-bottom
}
