package com.zodiackillerciphers.tests.samblake;

public class Counts {
	String key;
	int countTop;
	int countBottom;
	int countAll;
	int N;
	int topSize;

	public int getCountTop() {
		return countTop;
	}

	public void setCountTop(int countTop) {
		this.countTop = countTop;
	}

	public int getCountBottom() {
		return countBottom;
	}

	public void setCountBottom(int countBottom) {
		this.countBottom = countBottom;
	}

	public int getN() {
		return N;
	}

	public void setN(int n) {
		N = n;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public float ratioTop() {
		float result = countTop;
		result /= getTopSize();
		return result;
	}

	public float ratioBottom() {
		float result = countBottom;
		result /= getTopSize();
		return result;
	}

	public float ratioAll() {
		float result = countAll;
		result /= N;
		return result;
	}

	public float diffRatioTop() {
		return ratioTop() - ratioAll();
	}

	public float diffRatioBottom() {
		return ratioBottom() - ratioAll();
	}

	public float diffRatioTopBottom() {
		return ratioTop() - ratioBottom();
	}

	public void incTop() {
		countTop++;
	}

	public void incBottom() {
		countBottom++;
	}

	public void incAll() {
		countAll++;
	}

	public int getCountAll() {
		return countAll;
	}

	public void setCountAll(int countAll) {
		this.countAll = countAll;
	}

	public int getTopSize() {
		return topSize;
	}

	public void setTopSize(int topSize) {
		this.topSize = topSize;
	}

	public String toString() {
		String tab = "	";
		return key + tab + N + tab + topSize + tab + countTop + tab + countBottom + tab + countAll + tab + ratioTop() + tab + ratioBottom() + tab
				+ ratioAll() + tab + diffRatioTop() + tab + diffRatioBottom() + tab + diffRatioTopBottom();
	}
	
	public boolean doPrint() {
		if (Math.abs(diffRatioTop()) >= 0.02) return true;
		if (Math.abs(diffRatioBottom()) >= 0.02) return true;
		if (Math.abs(diffRatioTopBottom()) >= 0.02) return true;
		return false;
	}
}
