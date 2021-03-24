package com.zodiackillerciphers.tests.mapcode;

public class Decimal {
	public float value;
	public String valueString;
	public Decimal(float value, String valueString) {
		super();
		this.value = value;
		this.valueString = valueString;
	}
	/**
	 * @return the value
	 */
	public float getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(float value) {
		this.value = value;
	}
	/**
	 * @return the valueString
	 */
	public String getValueString() {
		return valueString;
	}
	/**
	 * @param valueString the valueString to set
	 */
	public void setValueString(String valueString) {
		this.valueString = valueString;
	}
	
	public String toString() {
		return "{"+value + "," + valueString+"}";
	}
}
