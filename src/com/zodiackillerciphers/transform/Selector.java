package com.zodiackillerciphers.transform;

import java.util.List;

public interface Selector {
	/** replace the selection in the original input with the given input */
	public List<StringBuffer> replaceSelection(List<StringBuffer> input);
	/** is the selection valid? */
	public boolean isValidSelection();
}
