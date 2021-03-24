package com.zodiackillerciphers.annealing.craigoperation;

/** UNUSED */
public class JumpToken extends Operation {
	public boolean executeImpl(OperationState state) {
		int which = (int)parameters.get("which");
		if (which >= state.tokensCurrent.size()) return false;
		state.setTokenPosition(which);
		return true;
	}

	@Override
	public boolean isReady() {
		return parameters.get("which") != null;
	}

	@Override
	public void generate(OperationState state) {
		int which = 0;
		if (!state.tokensCurrent.isEmpty())
			which = state.rand.nextInt(state.tokensCurrent.size());
		parameters.put("which", which);
	}
}
