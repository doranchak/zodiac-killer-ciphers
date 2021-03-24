package com.zodiackillerciphers.transform;

import java.util.List;
import java.util.Map;

public interface Transformation {

	/** convert the given portion of the genome to input parameters */
	public void processParameters(float[] genomeValues);
	
	/** set the given input parameter value */
	public void setParameterValue(String name, Integer value);
	
	/** get the given input parameter */
	public Integer getParameterValue(String name);

	/** validate input parameters */
	public boolean validateParameters();
	
	/** configure parameters */
	public void setupParameters();
	
	/** perform the transformation.  don't modify the input. */
	public void execute();
	/** perform the transformation.  don't modify the input.  if showSteps is true, show output as modifications are made. */
	public void execute(boolean showSteps);
	
	public List<StringBuffer> getInput();
	public void setInput(List<StringBuffer> input);
	public List<StringBuffer> getOutput();
	public void setOutput(List<StringBuffer> output);

	public Map<Integer, Integer> getTransformMap();
	public void setTransformMap(Map<Integer, Integer> transformMap);

	/** Transform the cipher using the given map.
	 * The map maps original positions to transformed positions.
	 * If reverse is true, then perform the transformation in reverse.
	 */
	public static String transform(String cipher, Map<Integer, Integer> map, boolean reverse) {
		StringBuilder sb = new StringBuilder(cipher.length());
		for (int i=0; i<cipher.length(); i++) sb.append(" ");
		for (Integer posOriginal : map.keySet()) {
			Integer posTransformed = map.get(posOriginal);
			if (reverse) {
				sb.setCharAt(posOriginal, cipher.charAt(posTransformed));
			} else {
				sb.setCharAt(posTransformed, cipher.charAt(posOriginal));
			}
		}
		return sb.toString();
	}
	
}
