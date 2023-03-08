package com.zodiackillerciphers.ciphers.kaczynski;

import java.util.List;

public class MatrixSearchBean {
	public static String tab = "	";
	public List<int[]> positions;
	public float jumps;
	public float normLength;
	public float normJumps;
	public int clusterId;

	public void calculateDistance() {
		jumps = 0;
		if (positions.size() < 2)
			return;
		for (int i = 1; i < positions.size(); i++) {
			jumps += distance(positions.get(i), positions.get(i - 1));
		}
		
		// normalize 
		jumps /= (positions.size()-1);
	}

	public static float distance(int[] pos1, int[] pos2) {
		float distance = (pos1[0] - pos2[0]) * (pos1[0] - pos2[0]);
		distance += (pos1[1] - pos2[1]) * (pos1[1] - pos2[1]);
		distance = (float) Math.sqrt(distance);
		return distance;
	}

	public static String toString(List<int[]> list) {
		StringBuffer sb = new StringBuffer();
		for (int[] val : list) {
			if (sb.length() > 0)
				sb.append(" ");
			sb.append(val[0]).append(",").append(val[1]);
		}
		return sb.toString();
	}
	
	float score() {
		return normLength * (1-normJumps);
	}

	public String toString() {
		return score() + tab + positions.size() + tab + normLength + tab + normJumps + tab + clusterId + tab
				+ MatrixSearch.directionChanges(positions) + tab + toString(positions);
	}

}
