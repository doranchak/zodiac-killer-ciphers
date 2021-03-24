package com.zodiackillerciphers.annealing.craigoperation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** generic operation applied to token(s) */
public abstract class Operation {
	public Operation() {
		super();
		init();
	}
	public Map<String, Object> parameters;
	public void init() {
		if (parameters == null) 
			parameters = new HashMap<String, Object>();
	}
	
	/** perform the operation. returns true if successful. */
	public boolean execute(OperationState state) {
//		System.out.println(this.getClass().getSimpleName() + " exec ");
		init();
		if (!isReady()) {
//			System.out.println(this.getClass().getSimpleName() + " not ready so gen ");
			generate(state);
		}
		if (!isReady()) {
			return false; // still not ready even after generating params
		}
//		System.out.println("execute operation: " + this);
		return executeImpl(state);
	}
	public abstract boolean executeImpl(OperationState state); 
	/** returns true if operation's parameters have already been set */
	public abstract boolean isReady();
	/** generate random parameters */
	public abstract void generate(OperationState state);
	public String toString() {
		return getClass().getSimpleName() + " " + parameters;
	}
}
