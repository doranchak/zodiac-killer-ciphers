package com.zodiackillerciphers.annealing.craigoperation;

import com.zodiackillerciphers.dictionary.RouletteDictionary;
import com.zodiackillerciphers.dictionary.WordFrequencies;

public class InsertZodiacWord extends Operation {
	public InsertZodiacWord() {super();}
	
	static {
		RouletteDictionary.init(WordFrequencies.ZODIAC_WORDS_WITH_COUNTS);
	}
	
	public boolean executeImpl(OperationState state) {
		int which = (int)parameters.get("which");
		if (which >= state.tokensCurrent.size()) return false;
		String word = (String) parameters.get("word");
		state.tokensCurrent.add(which, word);
		return true;
	}

	@Override
	public boolean isReady() {
		return parameters.get("which") != null;
	}

	@Override
	public void generate(OperationState state) {
		// get a random word, and insert it into a random location
		String word = RouletteDictionary.randomWord();
		int which = 0;
		if (!state.tokensCurrent.isEmpty())
			which = state.rand.nextInt(state.tokensCurrent.size());
		parameters.put("word", word);
		parameters.put("which", which);
	}
}
