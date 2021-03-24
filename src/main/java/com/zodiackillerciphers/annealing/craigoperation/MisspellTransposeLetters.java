package com.zodiackillerciphers.annealing.craigoperation;

import com.zodiackillerciphers.old.Zodiac;

public class MisspellTransposeLetters extends Operation {
	public MisspellTransposeLetters() {super();}
	public boolean executeImpl(OperationState state) {
//		System.out.println("exec params " + parameters);
		int which = (int) parameters.get("which");
		if (which >= state.tokensCurrent.size())
			return false;
		int where1 = (int) parameters.get("where1");
		int where2 = (int) parameters.get("where2");
		StringBuffer newToken = new StringBuffer(state.tokensCurrent.get(which));
		if (where1 >= newToken.length()) return false;
		if (where2 >= newToken.length()) return false;
		char c1 = newToken.charAt(where1);
		char c2 = newToken.charAt(where2);
		newToken.setCharAt(where1, c2);
		newToken.setCharAt(where2, c1);
		state.tokensCurrent.set(which, newToken.toString());

		return true;
	}

	@Override
	public boolean isReady() {
		return parameters.get("which") != null;
	}

	@Override
	public void generate(OperationState state) {
//		System.out.println("gen mt");
		if (state.tokensCurrent.isEmpty())
			return;
//		System.out.println("gen mt 1");
		int which = state.rand.nextInt(state.tokensCurrent.size());
		String token = state.tokensCurrent.get(which);
//		System.out.println("gen mt 2 " + which + " " + token + " " + token.length());
		if (token.length() < 2)
			return;

		int where1 = state.rand.nextInt(token.length());
		int where2 = where1;
		while (where1 == where2)
			where2 = state.rand.nextInt(token.length());

		parameters.put("which", which);
		parameters.put("where1", where1);
		parameters.put("where2", where2);
		
//		System.out.println("gen params " + parameters);
	}
}
