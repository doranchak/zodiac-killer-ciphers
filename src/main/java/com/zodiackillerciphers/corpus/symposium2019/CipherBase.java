package com.zodiackillerciphers.corpus.symposium2019;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.ciphers.HomophonicGenerator;
import com.zodiackillerciphers.corpus.symposium2019.types.Columnar;
import com.zodiackillerciphers.corpus.symposium2019.types.Gibberish;
import com.zodiackillerciphers.corpus.symposium2019.types.OneTypeVsBag;
import com.zodiackillerciphers.corpus.symposium2019.types.Route;
import com.zodiackillerciphers.transform.CipherTransformations;

public abstract class CipherBase extends Thread {
	public static int ALPHABET_SIZE = 63; // match Z340's alphabet size
	
	/** plaintexts to turn into ciphers */
	public List<String> plaintexts;
	/** store the generated ciphers */
	public List<String> ciphers;
	/** store the generated stats for the ciphers */
	public List<double[]> stats;
	
	/** if true, skip the homophonic encryption step */
	public boolean skipHomophonic = false;
	
	/** make a random example of this cipher, using the given plaintext */
	public String makeCipher(String plaintext) {
//		debug("making cipher for pt " + plaintext);
		/** first level of encipherment */
		String cipher = firstLayer(plaintext);
//		System.out.println(this.getClass().getName());
//		debug("first layer: " + cipher);
		if (skipHomophonic) {
			if (this instanceof Gibberish) {
				return CipherTransformations.shuffle(cipher);
			}
			return cipher;
		}
//		System.out.println(this.getClass().getName());
		String hom = homophonic(cipher);
		if (this instanceof Gibberish) { // special case: post-process hom layer
			Gibberish g = (Gibberish) this;
			if (!g.method) 
				hom = CipherTransformations.shuffle(Ciphers.Z340);
		}
//		debug("hom: " + hom);
		return hom;
	}
	/** perform the first level of encipherment, prior to homophonic encryption */
	public abstract String firstLayer(String plaintext);
	
	/** perform second level of encipherment: homophonic substitution */
	public String homophonic(String cipher) {
		return HomophonicGenerator.makeHomophonic(cipher, ALPHABET_SIZE);
	}
	public void addPlaintext(String plaintext) {
		if (plaintexts == null) 
			plaintexts = new ArrayList<String>();
		plaintexts.add(plaintext);
	}
	public void addCipher(String cipher) {
		if (ciphers == null) 
			ciphers = new ArrayList<String>();
		ciphers.add(cipher);
	}
	public void addStats(double[] statValues) {
		if (stats == null) stats = new ArrayList<double[]>();
		if (this instanceof Route) { // special case: we might be adding width as output for Route ciphers
			if (Route.OUTPUT_WIDTH) {
				double[] statValuesNew = new double[statValues.length+1];
				for (int i=0; i<statValues.length; i++) {
					statValuesNew[i] = statValues[i];
				}
				statValuesNew[statValuesNew.length-1] = ((Route)this).width;
				statValues = statValuesNew;
			}
		}
		stats.add(statValues);
	}
	
	public List<String> getPlaintexts() {
		return plaintexts;
	}
	public void setPlaintexts(List<String> plaintexts) {
		this.plaintexts = plaintexts;
	}
	public List<String> getCiphers() {
		return ciphers;
	}
	public void setCiphers(List<String> ciphers) {
		this.ciphers = ciphers;
	}
	public List<double[]> getStats() {
		return stats;
	}
	public void setStats(List<double[]> stats) {
		this.stats = stats;
	}
	@Override
	public void run() {
		int counter = 0;
		for (String pt : plaintexts) {
//			say("making cipher for pt: " + pt);
			String cipher = makeCipher(pt);
			addCipher(cipher);
			if (this instanceof OneTypeVsBag) {
				String standardized = CipherGenerator.standardizeAlphabetFor(cipher);
				int[] vals = Ciphers.toNumeric(cipher, false, true);
				double[] stats = new double[vals.length+1];
				for (int i=0;i<vals.length; i++) {
					stats[i] = vals[i];
				}
				stats[stats.length-1] = ((OneTypeVsBag)this).output;
				addStats(stats);
				OneTypeVsBag o = (OneTypeVsBag) this;
//				System.out.println(o.type + ", " + o.output + ", " + Arrays.toString(stats));
				
			} else if (this instanceof Columnar && Columnar.WITH_KEY_LENGTH) {
				// include key length with stats
				Columnar col = (Columnar) this;
				double[] stats = CipherGenerator.statsFor(cipher);
				double[] statsWithOutput = new double[stats.length+1];
				for (int i=0; i<stats.length; i++) statsWithOutput[i] = stats[i];
				statsWithOutput[stats.length] = col.key.length;
				addStats(statsWithOutput);
			} else {
				addStats(CipherGenerator.statsFor(cipher));
			}
			counter++;
			System.out.println(threadNum() + " made " + counter + " ciphers and stats.");
		}
	}

	public Long threadNum() {
		return this.currentThread().getId();		
	}
	
	public void say(String msg) {
		System.out.println(threadNum() + " " + msg);
	}
	
}
