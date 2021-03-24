package com.zodiackillerciphers.morse;

import com.zodiackillerciphers.dictionary.WordFrequencies;

public class MorseWordBean {
	String morse;
	String morseBinary;
	int morseLength; // length without delimiters and spaces
	int index; // location of this morse within input string
	String word;
	int frequency;
	public MorseWordBean(String morse, String morseBinary, int morseLength, int index, String word, int frequency, int percentile) {
		super();
		this.morse = morse;
		this.morseBinary = morseBinary;
		this.morseLength = morseLength;
		this.index = index;
		this.word = word;
		this.frequency = frequency;
		this.percentile = percentile;
	}
	int percentile;
	public String toString() {
		return word.length() + "	" + frequency + "	" + percentile + "	" + word + "	"
				+ morse + "	" + morseBinary + "	" + morseLength + "	" + index;
	}
}
