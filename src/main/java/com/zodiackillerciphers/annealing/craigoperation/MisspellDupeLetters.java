package com.zodiackillerciphers.annealing.craigoperation;

public class MisspellDupeLetters extends Operation {
	public MisspellDupeLetters() {super();}
	public boolean executeImpl(OperationState state) {
		int which = (int) parameters.get("which");
		if (which >= state.tokensCurrent.size())
			return false;
		int where = (int) parameters.get("where");
		StringBuffer newToken = new StringBuffer(state.tokensCurrent.get(which));
		if (where >= newToken.length()) return false;
		char letter = newToken.charAt(where);
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
		int where = state.rand.nextInt(state.tokensCurrent.get(which).length());
		parameters.put("which", which);
		parameters.put("where", where);
	}

}
