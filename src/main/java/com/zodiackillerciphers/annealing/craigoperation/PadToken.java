package com.zodiackillerciphers.annealing.craigoperation;

public class PadToken extends Operation {
	public PadToken() {super();}
	public boolean executeImpl(OperationState state) {
		int which = (int) parameters.get("which");
		if (which >= state.tokensCurrent.size())
			return false;
		int amount = (int) parameters.get("amount");
		String pad = "";
		for (int i = 0; i < amount; i++)
			pad += "_";
		String token = state.tokensCurrent.get(which);
		state.tokensCurrent.add(which, pad);
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
		parameters.put("which", which);
		parameters.put("amount", state.rand.nextInt(50) + 1);
	}
}
