package com.zodiackillerciphers.tests.mapcode;

public class Fraction {
	public String whole;
	public String numerator;
	public String denominator;
	public String conjunction;
	
	public Integer valWhole;
	public Integer valNumerator;
	public Integer valDenominator;
	
	public Fraction(String whole, String numerator, String denominator, String conjunction, Integer valWhole, Integer valNumerator, Integer valDenominator) {
		super();
		this.whole = whole;
		this.numerator = numerator;
		this.denominator = denominator;
		this.conjunction = conjunction;
		this.valWhole = valWhole;
		this.valNumerator = valNumerator;
		this.valDenominator = valDenominator;
	}
	
	public Fraction(String whole, Integer valWhole) {
		this(whole, null, null, null, valWhole, null, null);
	}

	public boolean notEmpty(String val) {
		return val != null && !"".equals(val);
	}
	public String toString() {
		String val = "";
		if (notEmpty(whole)) val += whole + " ";
		if (notEmpty(conjunction)) val += conjunction + " ";
		if (notEmpty(numerator)) val += numerator + " ";
		if (notEmpty(denominator)) val += denominator;
		return val;
	}
	
	public Float toFloat() {
		Float result = 0f;
		if (valWhole != null) result += valWhole;
		if (valNumerator != null) result += ((float)valNumerator)/valDenominator;
		return result;
	}
	
	
	
}
