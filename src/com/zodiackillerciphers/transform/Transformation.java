package com.zodiackillerciphers.transform;

import java.util.List;

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
	
	/** perform the transformation.*/
	public void execute();
	/** perform the transformation.  if showSteps is true, show output as modifications are made. */
	public void execute(boolean showSteps);
	
	public List<StringBuffer> getInput();
	public void setInput(List<StringBuffer> input);
	public List<StringBuffer> getOutput();
	public void setOutput(List<StringBuffer> output);
	
}
