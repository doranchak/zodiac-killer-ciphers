package com.zodiackillerciphers.ciphers.generator;

import ec.util.MersenneTwisterFast;

public class GeneratorThread extends Thread {
	MersenneTwisterFast rand;
	public GeneratorThread() {
		rand = new MersenneTwisterFast();
	}
	
	public void run() {
		int count = 0;
		int countnull = 0;
		while (true) {
			count++;
			int i = rand.nextInt(Generator.tokens.size());
			//int i = 2217;
			CandidatePlaintext can = Generator.plaintext(i, 340);
			if (can != null && can.criteriaAll()) {
				System.out.println(this.hashCode() + ", " + count + ", " + can.toString());
				Generator.write(can);
				
			}
			//if (count % 10000 == 0) System.out.println(this.hashCode() + ", " + count+" tries, " + countnull + " nulls");
		}
		
	}
}
