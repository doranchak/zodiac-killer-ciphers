package com.zodiackillerciphers.annealing.craigoperation;

import com.zodiackillerciphers.old.Zodiac;

public class MisspellAddRandomLetter extends Operation {
	public MisspellAddRandomLetter() {super();}
	public boolean executeImpl(OperationState state) {
		int which = (int) parameters.get("which");
		if (which >= state.tokensCurrent.size())
			return false;
		String letter = (String) parameters.get("letter");
		int where = (int) parameters.get("where");
		StringBuffer newToken = new StringBuffer(state.tokensCurrent.get(which));
		if (where >= newToken.length()) return false;
		newToken.insert(where, letter);
		state.tokensCurrent.set(which, newToken.toString());
		return true;
	}

	@Override
	public boolean isReady() {
		return parameters.get("which") != null;
	}

	@Override
	public void generate(OperationState state) {
		if (state.tokensCurrent.isEmpty())
			return;
		int which = state.rand.nextInt(state.tokensCurrent.size());
		String letter = Zodiac.getLetterRoulette(true);
		int where = state.rand.nextInt(state.tokensCurrent.get(which).length());
		parameters.put("which", which);
		parameters.put("letter", letter);
		parameters.put("where", where);
	}

}
