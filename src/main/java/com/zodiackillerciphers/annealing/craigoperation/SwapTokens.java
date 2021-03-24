package com.zodiackillerciphers.annealing.craigoperation;

public class SwapTokens extends Operation {
	public SwapTokens() {super();}
	public boolean executeImpl(OperationState state) {
		int which1 = (int) parameters.get("which1");
		if (which1 >= state.tokensCurrent.size())
			return false;
		int which2 = (int) parameters.get("which2");
		if (which2 >= state.tokensCurrent.size())
			return false;
		
		String tmp = state.tokensCurrent.get(which1);
		state.tokensCurrent.set(which1, state.tokensCurrent.get(which2));
		state.tokensCurrent.set(which2, tmp);
		
		return true;
	}

	@Override
	public boolean isReady() {
		return parameters.get("which1") != null;
	}

	@Override
	public void generate(OperationState state) {
		if (state.tokensCurrent.size() < 2)
			return;
		int which1 = state.rand.nextInt(state.tokensCurrent.size());
		int which2 = which1;
		while (which1 == which2) {
			which2 = state.rand.nextInt(state.tokensCurrent.size());
		}
		parameters.put("which1", which1);
		parameters.put("which2", which2);
	}
}
