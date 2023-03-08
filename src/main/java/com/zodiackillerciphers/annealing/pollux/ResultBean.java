package com.zodiackillerciphers.annealing.pollux;

public class ResultBean {
	
	public static float averageCodeLength = 2.54167f; //4.5268f;
	
	String key;
	String morse;
	String plaintext;

	int morseLength;
	int morseTokensCount;
	int morseTokensValid;
	int morseExcessSpaces;
	int morseAlphabetSize; // distinct combinations of morse symbols (testing for custom morse alphabets)
	double morseAlphabetIoc; // ioc of cipher when considering morse units and their frequencies
	double morseIocDiff;
	
	// Multiplying the code lengths by their frequency, we find that an average
	// letter, weighted by frequency, has code length 2.54167
	float morseAverageTokenLength;
	float morseAverageTokenLengthDistance;
	float plaintextScore;
	
	float ioc;
	float iocDiff; // compared to English

	
	float normMorseTokensValid; // [0-1], 1 is best
	float normMorseExcessSpaces; // [0-1], 0 is best
	float normMorseAverageTokenLengthDistance; // [0-1], 0 is best.
	float normPlaintextScore; // [0-1], 1 is best
	float normIocDiff; // [0-1], 0 is best.
	
	float combinedScore;
	
	@Override
	public String toString() {
		String tab = "	";
		return combinedScore + tab + key + tab + morseLength + tab + morseTokensCount + tab + morseTokensValid + tab
				+ morseAlphabetSize + tab + morseAlphabetIoc + tab + morseIocDiff + tab + morseExcessSpaces + tab + morseAverageTokenLength + tab + morseAverageTokenLengthDistance + tab
				+ plaintextScore + tab + ioc + tab + iocDiff + tab + normMorseTokensValid + tab + normMorseExcessSpaces + tab
				+ normMorseAverageTokenLengthDistance + tab + normPlaintextScore + tab + normIocDiff + tab + "[" + morse + "]" + tab + "["
				+ plaintext + "]";
	}
	
}
