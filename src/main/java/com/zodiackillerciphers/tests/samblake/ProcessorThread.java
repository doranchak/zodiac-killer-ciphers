package com.zodiackillerciphers.tests.samblake;

import java.util.ArrayList;
import java.util.List;

public class ProcessorThread extends Thread {

	List<Cipher> ciphers;

	public ProcessorThread() {
		this.ciphers = new ArrayList<Cipher>();
	}
	public void addCipher(Cipher cipher) {
		this.ciphers.add(cipher);
	}

	@Override
	public void run() {
		System.out
				.println("Thread #" + Thread.currentThread().getId() + " starting with " + ciphers.size() + " ciphers.");
		int count = 0;
		for (Cipher cipher : ciphers) {
			TransformedCiphers.process(cipher);
			//System.out.println(cipher.azPlaintextWithBreaks);
			count++;
			System.out.println(" - " + Thread.currentThread().getId() + ", count " + count);
		}
		System.out
		.println("Thread #" + Thread.currentThread().getId() + " done with " + ciphers.size() + " ciphers.");
	}

}
