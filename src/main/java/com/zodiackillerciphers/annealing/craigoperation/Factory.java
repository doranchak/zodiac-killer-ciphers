package com.zodiackillerciphers.annealing.craigoperation;

import java.util.Random;

import ec.util.MersenneTwisterFast;

public class Factory {
	public static int NUM_OPERATIONS = 10;

	public static Operation random(MersenneTwisterFast rand, OperationState state) {
		Operation result = null;
		switch (rand.nextInt(NUM_OPERATIONS)) {
		case 0:
			result = new DeleteToken();
			break;
		case 1:
			result = new DupeToken();
			break;
		case 2:
			result = new InsertZodiacWord();
			break;
		case 3:
			result = new MisspellAddRandomLetter();
			break;
		case 4:
			result = new MisspellDupeLetters();
			break;
		case 5:
			result = new MisspellRemoveRandomLetter();
			break;
		case 6:
			result = new MisspellReplaceRandomLetter();
			break;
		case 7:
			result = new MisspellTransposeLetters();
			break;
		case 8:
			result = new PadToken();
			break;
		case 9:
			result = new SwapTokens();
			break;
		default:
			throw new RuntimeException("Illegal operation.");
		}
		
//		if (1==1) result = new PadToken();
		return result;
	}

	public static Operation clone(Operation op) {
		Operation newOp = null;
		if (op instanceof DeleteToken)
			newOp = new DeleteToken();
		else if (op instanceof DupeToken)
			newOp = new DupeToken();
		else if (op instanceof InsertZodiacWord)
			newOp = new InsertZodiacWord();
		else if (op instanceof JumpCipher)
			newOp = new JumpCipher();
		else if (op instanceof JumpToken)
			newOp = new JumpToken();
		else if (op instanceof MisspellAddRandomLetter)
			newOp = new MisspellAddRandomLetter();
		else if (op instanceof MisspellDupeLetters)
			newOp = new MisspellDupeLetters();
		else if (op instanceof MisspellRemoveRandomLetter)
			newOp = new MisspellRemoveRandomLetter();
		else if (op instanceof MisspellReplaceRandomLetter)
			newOp = new MisspellReplaceRandomLetter();
		else if (op instanceof MisspellTransposeLetters)
			newOp = new MisspellTransposeLetters();
		else if (op instanceof PadToken)
			newOp = new PadToken();
		else if (op instanceof SwapTokens)
			newOp = new SwapTokens();

		newOp.init();
		for (String key : op.parameters.keySet())
			newOp.parameters.put(key, op.parameters.get(key));
		return newOp;
	}
}
