package com.zodiackillerciphers.tests.mapcode;

public class GrammarString {
	public String string;
	public String description;
	public Float amount;
	public GrammarString(String string) {
		this(string, string, null);
	}
	public GrammarString(String string, Float amount) {
		this(string, string, amount);
	}
	public GrammarString(String string, String description) {
		this(string, description, null);
	}
	public GrammarString(String string, String description, Float amount) {
		super();
		this.string = string;
		this.description = description;
		this.amount = amount;
	}
}
