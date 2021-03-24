package com.zodiackillerciphers.tests.cribbing;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.io.Unzip;

//todo: include donna lass cipher
//z13
//z408 
//z340
//transpositions of z340
// - skytale (linear and 2D grid)
// - periodcolumn 2 period 18
//a shuffle of z340
//a shuffle of z408
// include each cipher in reverse

public class Search extends Base {
	public static boolean INFO = true;
	static String ROOT_PATH;
	static int MIN_LENGTH = 8;

	static Date start = new Date();

	static int fileCount = 0;
	static int sampleCount = 0;
	static long fileSizeTotal = 0;

	static int MAX_ASSIGNMENTS = 1;

	// static boolean ignore = true;

	// static String[] ciphers = new String[] { Ciphers.cipher[0].cipher, //
	// Z340
	// Ciphers.cipher[1].cipher, // Z408
	// Ciphers.cipher[3].cipher, // Z13
	// };

	static String[] ciphers = new String[] { // "ePTWYPNWA_8WkSNZf99Q%9ZZWkW__",
												// // donna
												// lass
												// poster
												// cipher
	// "ePTWYPNWA_8WkSNZfX9Q%9ZZWkW__", // donna lass cipher (modified version)
	// Ciphers.cipher[3].cipher, // Z13
	Ciphers.cipher[0].cipher };

	static void say(String msg) {
		System.out.println(diff() + " " + msg);
	}

	static long diff() {
		Date end = new Date();
		return end.getTime() - start.getTime();
	}

	/**
	 * 
	 * @param wholeCipherOnly
	 *            crib only from the start of the cipher. slower for longer
	 *            ciphers.
	 * @param maxAllowed
	 *            max number of plaintext assignments per cipher symbol. (for
	 *            polyphones, set this > 1)
	 * @param maximizePolyphones
	 *            if true, keep trying symbol assignments even when violations
	 *            are encountered
	 * @param plaintext
	 *            if supplied, then test with this plaintext instead of getting
	 *            plaintext from files.
	 */
	static void search(boolean wholeCipherOnly, int maxAllowed,
			boolean maximizePolyphones, String plaintext) {

		for (String cipher : ciphers) {
			Base.debug("Starting cipher " + cipher);
			State state = new State();
			state.setCipher(cipher);
			Context context = new Context();
			context.setState(state);
			if (ROOT_PATH != null) context.setFile(new File(ROOT_PATH)); // loop through every file
			context.setMaxAssignments(maxAllowed);
			context.setMaximizePolyphones(maximizePolyphones);
			search(context, wholeCipherOnly, plaintext);
			context.getState().getResults().dump();
			System.out.println("Final count: " + sampleCount);
		}

	}

	static void search(Context context, boolean wholeCipherOnly, String plaintext) {

		if (plaintext != null) {
			/** process the plaintext that is passed, rather than go through files */
			TextBean tb = new TextBean(new StringBuilder(plaintext));
			context.setTextBean(tb);
			process(context, wholeCipherOnly);
			return;
		} 
		File file = context.getFile();
		if (file.isDirectory()) {
			System.out.println("Going into directory " + file.getName());
			File[] files = file.listFiles();
			for (File f : files) {
				context.setFile(f);
				search(context, wholeCipherOnly, plaintext);
			}
			context.setFile(file);
		} else if (file.getName().endsWith(".zip")) {
			// if (file.getName().equals("21875-8.zip")) {
			// ignore = false;
			// }
			// if (ignore)
			// say("Skipping file " + file.getName());
			// else {
			say("Reading file " + file.getName());
			fileSizeTotal += file.length();
			context.setTextBean(Unzip.readConverted(file));
			if (context.getTextBean() != null) {
				process(context, wholeCipherOnly);
				fileCount++;
				if (fileCount % 1000 == 0 || fileCount == 100) { /*
																 * dump every
																 * 1000, and
																 * once at 100
																 * early in the
																 * run.
																 */
					float filesPerSecond = diff();
					filesPerSecond = 1000 / filesPerSecond * fileCount;
					float bytesPerSecond = diff();
					bytesPerSecond = 1000 / bytesPerSecond * fileSizeTotal;
					say("COUNT: " + fileCount + " TOTAL SIZE: " + fileSizeTotal
							+ " FILES/S: " + filesPerSecond + " BYTES/S: "
							+ bytesPerSecond + " SAMPLES: " + sampleCount);
					context.getState().getResults().dump();
				}
			}
			// }
		}

		// load file, stream plaintext
		// loop through all ciphers
		// for each cipher, recursively search each position until a cipher
		// constraint is violated.

	}

	/**
	 * recursively process ciphers and text stream. find all plaintext
	 * substrings that fit in the cipher texts. track stats and the best
	 * results.
	 */
	static boolean process(Context context, boolean wholeCipherOnly) {
		context.depthInc();
		// initial case: state is null. so loop through ciphers, init state, and
		// process.
		try {
			if (context.getState() == null) {
			} else {
				int end = wholeCipherOnly ? 0 : context.getState().getCipher()
						.length()
						- MIN_LENGTH;
				for (int cipherStart = 0; cipherStart <= end; cipherStart++) {
					context.getState().reset(false);
					for (int plainStart = 0; plainStart <= context
							.getTextBean().getConverted().length()
							- MIN_LENGTH; plainStart++) {
						Base.debug("cipherStart " + cipherStart
								+ " plainStart " + plainStart);
						context.getState().setPosCipherStart(cipherStart);
						context.getState().setPosCipherEnd(cipherStart + 1);
						context.getState().setPosPlainStart(plainStart);
						context.getState().setPosPlainEnd(plainStart + 1);
						boolean go = true;
						while (go) {
							go = loop(context);
						}
						sampleCount++;
						// debug("done with loop. " + context.getState());
					}
				}
				// context.getState().getResults().dump();
				// System.out.println("Final count: " + sampleCount);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			context.depthDec();
		}
		return true;
	}

	public static boolean loop(Context context) {
		State state = context.getState();
		Base.debug("cipher [" + context.subCipher() + "] plain ["
				+ context.subPlain() + "] state: " + state);
		// stop conditions:
		// - reached end of cipher
		// if (state.getPosCipherEnd() == state.getCipher().length()+1) {
		// Base.debug("Got to end of cipher");
		// state.setPosCipherEnd(state.getPosCipherEnd())
		// context.finish();
		// return false;
		// }
		// - reached end of plaintext stream
		if (state.getPosPlainEnd() == context.getTextBean().getConverted()
				.length()) {
			Base.debug("Got to end of plaintext stream");
			context.finish(true);
			return false;
		}
		// - start position of cipher is too close to end
		if (state.getCipher().length() - state.getPosCipherStart() < MIN_LENGTH) {
			Base.debug("Start is too close to end of cipher");
			context.finish(false);
			return false;
		}
		// - start position of plaintext stream is too close to end
		if (context.getTextBean().getConverted().length()
				- state.getPosPlainStart() < MIN_LENGTH) {
			Base.debug("Start is too close to end of plaintext stream");
			context.finish(false);
			return false;
		}
		// - violated the cipher key
		boolean success = context.addToKey();
		if (!success) {
			if (context.isMaximizePolyphones()) {
				context.addViolation(state.getPosPlainEnd());
			} else {
				context.finish(false);
				// debug("Violated cipher key");
				return false;
			}
		}

		// - reached end of cipher
		if (state.getPosCipherEnd() == state.getCipher().length()) {
			Base.debug("Got to end of cipher");
			context.finish(true);
			return false;
		}

		// char currentC = context.getCurrentC();
		// debug("currentC " + context.getCurrentC() + " currentP " +
		// context.getCurrentP());
		// debug("cipher [" + context.subCipher() + "] plain [" +
		// context.subPlain() + "]");
		// advance end pointers
		context.nextEnd();
		return true;
	}

	public static void main(String[] args) {
//		if (args.length != 1) {
//			fatal("Please specify root path.");
//		}
//		ROOT_PATH = args[0];
		search(true, 3, true, Ciphers.cipher[1].solution);

	}
}
