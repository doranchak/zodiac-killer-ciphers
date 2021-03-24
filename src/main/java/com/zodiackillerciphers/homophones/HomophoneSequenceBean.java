package com.zodiackillerciphers.homophones;

import java.util.ArrayList;
import java.util.List;

public class HomophoneSequenceBean {
	public String fullSequence;
	public List<Integer> positions;
	public HomophoneSequenceBean() {
		fullSequence = "";
		positions = new ArrayList<Integer>();
	}
	
	public boolean isAllEven() {
		/*if (fullSequence.equals("MUJMUJMUJUMJMUMM")) {
			System.out.println("SMEG " + fullSequence + " " + positions);
		}*/
		for (Integer pos : positions) {
			if ((pos+1) % 2 != 0) return false;
		}
		return true;
	}
	public boolean isAllOdd() {
		for (Integer pos : positions) {
			/*if (fullSequence.equals("MUJMUJMUJUMJMUMM")) {
				System.out.println("SMEG pos " + pos + " [" + ((pos+1)%2) + "]");
			}*/
			if ((pos+1) % 2 == 0) return false;
		}
		return true;
	}
	public String toString() {
		return "sequenceBean fullSequence " + fullSequence + " positions " + positions;
	}
}
