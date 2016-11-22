package com.zodiackillerciphers.corpus;

public enum Compare {
	EQUAL, // A equals B on all metrics
	DOMINATES,// each of A's metrics is equal to or superior to the corresponding metric in B.
	DOMINATED,// each of B's metrics is equal to or superior to the corresponding metric in A.
	NON_DOMINATED// A is not dominated by B, nor does A dominate B.
}
