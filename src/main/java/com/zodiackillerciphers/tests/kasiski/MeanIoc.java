package com.zodiackillerciphers.tests.kasiski;

public class MeanIoc {
	int period;
	double sigma;
	double mean;
	double stdDev;
	double actual;
	public int getPeriod() {
		return period;
	}
	public void setPeriod(int period) {
		this.period = period;
	}
	public double getSigma() {
		return sigma;
	}
	public void setSigma(double sigma) {
		this.sigma = sigma;
	}
	public double getMean() {
		return mean;
	}
	public void setMean(double mean) {
		this.mean = mean;
	}
	public double getStdDev() {
		return stdDev;
	}
	public void setStdDev(double stdDev) {
		this.stdDev = stdDev;
	}
	public double getActual() {
		return actual;
	}
	public void setActual(double actual) {
		this.actual = actual;
	}
	@Override
	public String toString() {
		return "MeanIoc [period=" + period + ", sigma=" + sigma + ", mean=" + mean + ", stdDev=" + stdDev + ", actual="
				+ actual + "]";
	}
}
