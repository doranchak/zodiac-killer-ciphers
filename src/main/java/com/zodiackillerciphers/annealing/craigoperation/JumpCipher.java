package com.zodiackillerciphers.annealing.craigoperation;

import java.util.List;

/** UNUSED */
public class JumpCipher extends Operation {
	public boolean executeImpl(OperationState state) {
		int pos = (int) parameters.get("which");
		state.setCipherPosition(pos);
		return true;
	}

	@Override
	public boolean isReady() {
		return parameters.get("which") != null;
	}

	@Override
	public void generate(OperationState state) {
		parameters.put("which", state.getRand().nextInt(state.getCipher().length()));
	}
}
