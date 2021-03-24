package com.zodiackillerciphers.tests.cribbing;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import com.zodiackillerciphers.lucene.Stats;

/**
 * top-level details that we use during recursion. State is made for each
 * cipher. the file and text bean are shared by each cipher state.
 */
public class Context extends Base {
	/** current File. Could be directory or zip file */
	File file;
	/** TextBean which has candidate plaintext stream and original text stream */
	TextBean textBean;
	/**
	 * current State. we are considering multiple ciphers, so we track positions
	 * and keys per cipher.
	 */
	public State state;
	public static double MAX_IOC = 0.20;
	char currentP;
	char currentC;
	int depth = 0;

	/** max number of substitution assignments allowed per key entry */
	int maxAssignments = 1;
	/**
	 * if true, keep trying symbol assignments even when violations are
	 * encountered
	 */
	boolean maximizePolyphones = false;
	/** track violation positions when maximizePolyphones is true */
	Set<Integer> violations;

	/** read the zip file, extract and convert its text */
	void extractPlaintext() {
	}

	/**
	 * @return the file
	 */
	public File getFile() {
		return file;
	}

	/**
	 * @param file
	 *            the file to set
	 */
	public void setFile(File file) {
		this.file = file;
	}

	/**
	 * @return the state
	 */
	public State getState() {
		return state;
	}

	/**
	 * @param state
	 *            the state to set
	 */
	public void setState(State state) {
		this.state = state;
	}

	/**
	 * @return the textBean
	 */
	public TextBean getTextBean() {
		return textBean;
	}

	/**
	 * @param textBean
	 *            the textBean to set
	 */
	public void setTextBean(TextBean textBean) {
		this.textBean = textBean;
	}

	/** get next plaintext character */
	public char nextPlaintextCharacter() {
		return textBean.getConverted().charAt(state.posPlainEnd - 1);
	}

	/** get next ciphertext character */
	public char nextCiphertextCharacter() {
		return state.getCipher().charAt(state.getPosCipherEnd() - 1);
	}

	/** add next item to key. return true if successful, false otherwise. */
	public boolean addToKey() {
		char c = this.nextCiphertextCharacter();
		char p = this.nextPlaintextCharacter();
		this.currentC = c;
		this.currentP = p;
//		System.out.println("c " + c + " p " + p);
		return encode(c, p);
	}

	public void addViolation(int pos) {
		if (violations == null)
			violations = new HashSet<Integer>();
		violations.add(pos);
	}

	/**
	 * add given cipher symbol to plaintext letter mapping to the key. return
	 * true if successful, false otherwise.
	 */
	public boolean encode(char c, char p1) {
		// special case: padding
		if (p1 == '_') {
			state.plaintext.append("_");
//			System.out.println("plain now: " + state.plaintext);
			return true;
		}
		if (withinAllowable(c, p1)) {
			updateKey(c, p1);
			Search.debug("Successfully encoded {" + c + "," + p1 + "} " + state.getPolyphones());
			state.plaintext.append(p1);
			return true;
		}
		Search.debug("Cannot encode {" + c + "," + p1 + "}");
		if (isMaximizePolyphones())
			state.plaintext.append("_");
		return false;
	}

	void updateKey(char c, char p) {
		// update 1-to-many key
		if (state.getKey().get(c) == null)
			state.getKey().put(c, p);
		// update many-to-many key
		Set<Character> set = state.getPolyphones().get(c);
		if (set == null)
			set = new HashSet<Character>();
		set.add(p);
		state.getPolyphones().put(c, set);

		incrementCount(c);
		incrementPolyphoneCount(c, p);

	}

	public boolean withinAllowable(char c, char p) {
		Character p2 = state.getKey().get(c);
		if (p2 == null || p == p2)
			return true;
		Set<Character> set = state.getPolyphones().get(c);
		return (set == null || set.size() + 1 <= maxAssignments);
	}

	/**
	 * remove the given encoding. if the count is 1, then it it removed from the
	 * key entirely otherwise, it is not yet removed. in both cases, the count
	 * is decremented.
	 */
	public void removeEncodingUNUSED(char c) {
		Integer val = state.getCounts().get(c);
		if (val == null) {
			fatal("No count for [" + c + "]");
		}
		val--;
		state.getCounts().put(c, val);
		if (val == 0) {
			state.getCounts().remove(c);
			state.getKey().remove(c);
		}
	}

	public void incrementCount(char c) {
		Integer val = state.getCounts().get(c);
		if (val == null)
			val = 0;
		val++;
		state.getCounts().put(c, val);
	}

	public void incrementPolyphoneCount(char c, char p) {
		String key = "" + c + "" + p;
		Integer val = state.getPolyphoneCounts().get(key);
		if (val == null)
			val = 0;
		val++;
		state.getPolyphoneCounts().put(key, val);
	}

	public void decrementCount(char c) {
		Integer val = state.getCounts().get(c);
		if (val == null) {
			fatal("FATAL: count is null for [" + c + "]");
		}
		val--;
		state.getCounts().put(c, val);
	}

	/**
	 * @return the currentP
	 */
	public char getCurrentP() {
		return currentP;
	}

	/**
	 * @param currentP
	 *            the currentP to set
	 */
	public void setCurrentP(char currentP) {
		this.currentP = currentP;
	}

	/**
	 * @return the currentC
	 */
	public char getCurrentC() {
		return currentC;
	}

	/**
	 * @param currentC
	 *            the currentC to set
	 */
	public void setCurrentC(char currentC) {
		this.currentC = currentC;
	}

	/** move to the next end position */
	public void nextEnd() {
		// Search.debug("Moving cipher/plain end positions.");
		state.setPosCipherEnd(state.getPosCipherEnd() + 1);
		state.setPosPlainEnd(state.getPosPlainEnd() + 1);
	}

	/** move to the prev end position */
	public void prevEnd() {
		state.setPosCipherEnd(state.getPosCipherEnd() - 1);
		state.setPosPlainEnd(state.getPosPlainEnd() - 1);
	}

	/** move to the next plaintext start position */
	public void nextStartPlain() {
		state.setPosPlainStart(state.getPosPlainStart() + 1);
	}

	/** move to the next ciphertext start position */
	public void nextStartCipher() {
		state.setPosCipherStart(state.getPosCipherStart() + 1);
		state.setPosCipherEnd(state.getPosCipherStart() + 1);
	}

	public String subCipher() {
		return state.getCipher().substring(state.getPosCipherStart(),
				state.getPosCipherEnd());
	}

	public String subPlain() {
		return textBean.getConverted().substring(state.getPosPlainStart(),
				state.getPosPlainEnd());
	}

	public String plaintext() {
		String plain = textBean.getConverted().substring(
				state.getPosPlainStart(), state.getPosPlainEnd());
		if (maximizePolyphones) {
//			StringBuffer sb = new StringBuffer(plain);
//
//			if (violations != null)
//				for (Integer pos : violations)
//					sb.setCharAt(state.getPosPlainStart(), '_');
//			return sb.toString();
			return state.plaintext.toString();
		}
		return plain;
	}

	/**
	 * could not advance any further into this crib, so track the result and set
	 * up for next search
	 */
	public void finish(boolean success) {
		if (!success)
			prevEnd(); // move end pointer back one position since we stopped
						// due to
		// key violation at current position.
		float mult = state.multiplicity();
		if (mult > 1) {
			fatal("BAD MULT " + mult);
		}
		float score = 0;
		int len = state.getPosCipherEnd() - state.getPosCipherStart();
		debug("Match length: " + len);

		float lenScore = ((float) len) / state.getCipher().length(); // normalize
																		// to
																		// cipher
																		// length

		String plaintext = plaintext();
		float ioc = Stats.iocAsFloat(plaintext);
		float diff = 1 - (float) Math.abs(0.0667 - ioc);
		Base.debug("ioc " + ioc + " len " + len);
		/** punish bad solutions */
		if (ioc >= MAX_IOC)
			score = 0;
		else
			score = lenScore * (1 - mult) * (diff * diff * diff); // extra
																	// reward if
																	// ioc is
		// much closer to english

		if (len >= Search.MIN_LENGTH && score > 0) {
			Result result = new Result();
			result.setMultiplicity(mult);
			result.setLength(len);
			result.setIoc(ioc);
			result.setScore(score);
			result.setCiphertext(state.getCipher().substring(
					state.getPosCipherStart(), state.getPosCipherEnd()));
			result.setPlaintext(plaintext);
			if (!textBean.isIgnoreConversion()) result.setPlaintextOriginal(textBean.originalFor(
					state.getPosPlainStart(), len, true, 10));
			result.setFilename(textBean.getFileName());
			result.setPosCipherStart(state.getPosCipherStart());
			result.setPosPlainStart(state.getPosPlainStart());
			state.results.addResult(result, state.getCipher().length(), isMaximizePolyphones());
			// state.results.dump();
			// String out = "RESULT:" + "	" + score
			// + "	"
			// + len
			// + "	"
			// + mult
			// + "	"
			// + state.getCipher().substring(state.getPosCipherStart(),
			// state.getPosCipherEnd());
			// out += "	";
			// out +=
			// textBean.getConverted().substring(state.getPosPlainStart(),
			// state.getPosPlainEnd());
			// out += "	" + state.getPosCipherStart() + "	" +
			// state.getPosPlainStart() + "	";
			// out += textBean.originalFor(state.getPosPlainStart(), len, true,
			// 10);
			//
			// System.out.println(out);
		}
		// set up for next search: go to next start point in cipher, and start
		// over in the plaintext

		reset(false);
		nextStartCipher();

	}
	
	public void reset(Boolean resetCipherPos) {
		state.plaintext = new StringBuffer();
		state.reset(resetCipherPos);
	}

	public void depthInc() {
		depth++;
		Base.debug("recursion depth is " + depth);
	}

	public void depthDec() {
		depth--;
		Base.debug("recursion depth is " + depth);
	}

	/**
	 * @return the maxAssignments
	 */
	public int getMaxAssignments() {
		return maxAssignments;
	}

	/**
	 * @param maxAssignments
	 *            the maxAssignments to set
	 */
	public void setMaxAssignments(int maxAssignments) {
		this.maxAssignments = maxAssignments;
	}

	/**
	 * @return the maximizePolyphones
	 */
	public boolean isMaximizePolyphones() {
		return maximizePolyphones;
	}

	/**
	 * @param maximizePolyphones
	 *            the maximizePolyphones to set
	 */
	public void setMaximizePolyphones(boolean maximizePolyphones) {
		this.maximizePolyphones = maximizePolyphones;
	}

}
