package com.zodiackillerciphers.transform;

import java.util.Arrays;

import com.zodiackillerciphers.ciphers.generator.CandidateKey;

public class Parameter {
	/** name of parameter */
	public String name;
	
	/** min value allowed for this parameter */
	public int min;
	/** max value allowed for this parameter */
	public int max;
	
	/** optionally limit values to this domain */
	public int[] domain;
	
	
	/** current value of this parameter*/
	public Integer value;

	
	public Parameter(String name, int[] domain) {
		int min = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;
		for (int d : domain) {
			min = Math.min(d, min);
			max = Math.max(d, max);
		}
		this.name = name;
		this.min = min;
		this.max = max;
		this.domain = domain;
		
	}	
	public Parameter(String name, int min, int max) {
		this(name, min, max, null, 0);
	}
	
	public Parameter(String name, int min, int max, int value) {
		this(name, min, max, null, value);
	}
	
	public Parameter(String name, int min, int max, int[] domain, int value) {
		super();
		this.name = name;
		this.min = min;
		this.max = max;
		this.domain = domain;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public int[] getDomain() {
		return domain;
	}

	public void setDomain(int[] domain) {
		this.domain = domain;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		if (value < min || value > max) 
			throw new IllegalArgumentException("Value [" + value + "] does not fall within range [" + min + ", " + max + "]");
		if (domain != null) {
			boolean found = false;
			for (int d : domain) {
				if (d == value) {
					found = true;
					break;
				}
			}
			if (!found) throw new IllegalArgumentException("Value [" + value + "] not found in domain " + Arrays.toString(domain));
		}
		this.value = value;
	}


	/** convert float value from [0,1] to a value within [min, max] */
	public void setValue(float val) {
		if (domain == null) 
			value = CandidateKey.toInt(val, min, max);
		else {
			int i = CandidateKey.toInt(val, 0, domain.length - 1);
			value = domain[i];
		}
	}

	public String toString() {
		return name + " " + value + " " + min + " " + max + " " + Arrays.toString(domain);
	}
	public static void test() {
		Parameter p = new Parameter("blah", 0, 270, new int[] {0, 90,180, 270}, 0);
		System.out.println(p);
		
		for (int i=0; i<100; i++) {
			float val = (float) Math.random();
			p.setValue(val);
			System.out.println(val + ": " + p);
		}
		
	}
	
	public boolean validate() {
		if (value == null) return false;
		if (value < min || value > max) return false;
		if (domain == null) return true;
		boolean found = false;
		for (int d : domain) {
			if (d == value) {
				return true;
			}
		}
		return false;
	}
	public static void main(String[] args) {
		test();
	}
	
}
