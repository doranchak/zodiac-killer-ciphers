package com.zodiackillerciphers.rest.beans;

import java.util.List;

import com.zodiackillerciphers.homophones.HomophonesResultBean;

public class HomophonesSummary {
	/** perfect cycle score */
	double pcs;
	/** jarl's score */
	double jarlveHomophoneScore;
	/** list of beans of found sequences */
	List<HomophonesResultBean> results;
	public double getPcs() {
		return pcs;
	}
	public void setPcs(double pcs) {
		this.pcs = pcs;
	}
	public double getJarlveHomophoneScore() {
		return jarlveHomophoneScore;
	}
	public void setJarlveHomophoneScore(double jarlveHomophoneScore) {
		this.jarlveHomophoneScore = jarlveHomophoneScore;
	}
	public List<HomophonesResultBean> getResults() {
		return results;
	}
	public void setResults(List<HomophonesResultBean> results) {
		this.results = results;
	}
	
	
}
