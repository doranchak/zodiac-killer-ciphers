package com.zodiackillerciphers.annealing.craigoperation;

import java.util.List;

public class DupeToken extends Operation {
	public DupeToken() {super();}
	public boolean executeImpl(OperationState state) {
		int which = (int)parameters.get("which");
		if (which >= state.tokensCurrent.size()) return false;
		state.tokensCurrent.add(which, state.tokensCurrent.get(which));
		return true;
	}

	@Override
	public boolean isReady() {
		return parameters.get("which") != null;
	}

	@Override
	public void generate(OperationState state) {
		List<String> tokens = state.getTokensCurrent();
		if (tokens == null)
			return;
		if (tokens.isEmpty())
			return;
		parameters.put("which", state.getRand().nextInt(tokens.size()));
	}

}
