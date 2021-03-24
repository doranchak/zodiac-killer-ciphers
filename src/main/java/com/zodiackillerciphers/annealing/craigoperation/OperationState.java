package com.zodiackillerciphers.annealing.craigoperation;

import java.util.List;
import java.util.Random;

import ec.util.MersenneTwisterFast;

public class OperationState {
	List<String> tokensSource;
	List<String> tokensCurrent;
	StringBuffer cipher;
	StringBuffer plaintext;
	int cipherPosition;
	int tokenPosition;
	MersenneTwisterFast rand;
	public OperationState(List<String> tokensSource,
			List<String> tokensCurrent, StringBuffer cipher,
			StringBuffer plaintext, int cipherPosition, int tokenPosition, MersenneTwisterFast rand) {
		super();
		this.tokensSource = tokensSource;
		this.tokensCurrent = tokensCurrent;
		this.cipher = cipher;
		this.plaintext = plaintext;
		this.cipherPosition = cipherPosition;
		this.rand = rand;
		this.tokenPosition = tokenPosition;
	}
	/**
	 * @return the tokensSource
	 */
	public List<String> getTokensSource() {
		return tokensSource;
	}
	/**
	 * @param tokensSource the tokensSource to set
	 */
	public void setTokensSource(List<String> tokensSource) {
		this.tokensSource = tokensSource;
	}
	/**
	 * @return the tokensCurrent
	 */
	public List<String> getTokensCurrent() {
		return tokensCurrent;
	}
	/**
	 * @param tokensCurrent the tokensCurrent to set
	 */
	public void setTokensCurrent(List<String> tokensCurrent) {
		this.tokensCurrent = tokensCurrent;
	}
	/**
	 * @return the cipher
	 */
	public StringBuffer getCipher() {
		return cipher;
	}
	/**
	 * @param cipher the cipher to set
	 */
	public void setCipher(StringBuffer cipher) {
		this.cipher = cipher;
	}
	/**
	 * @return the plaintext
	 */
	public StringBuffer getPlaintext() {
		return plaintext;
	}
	/**
	 * @param plaintext the plaintext to set
	 */
	public void setPlaintext(StringBuffer plaintext) {
		this.plaintext = plaintext;
	}
	/**
	 * @return the cipherPosition
	 */
	public int getCipherPosition() {
		return cipherPosition;
	}
	/**
	 * @param cipherPosition the cipherPosition to set
	 */
	public void setCipherPosition(int cipherPosition) {
		this.cipherPosition = cipherPosition;
	}
	/**
	 * @return the rand
	 */
	public MersenneTwisterFast getRand() {
		return rand;
	}
	/**
	 * @param rand the rand to set
	 */
	public void setRand(MersenneTwisterFast rand) {
		this.rand = rand;
	}
	/**
	 * @return the tokenPosition
	 */
	public int getTokenPosition() {
		return tokenPosition;
	}
	/**
	 * @param tokenPosition the tokenPosition to set
	 */
	public void setTokenPosition(int tokenPosition) {
		this.tokenPosition = tokenPosition;
	}
	
	
}
