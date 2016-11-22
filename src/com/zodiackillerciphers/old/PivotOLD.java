package com.zodiackillerciphers.old;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.ciphers.generator.CandidatePlaintext;
import com.zodiackillerciphers.ciphers.generator.Position;

/** A Pivot is an n-gram (n > 1) that intersects at some symbol in a block of text. */ 
public class PivotOLD {
	
	/** row of intersection */
	public int row;
	/** col of intersection */
	public int col;
	
	/** symbol the legs intersect upon */
	public char intersectingSymbol;	
	
	/** number of "legs" of this pivot */
	public int legsCount;
	
	/** the legs */
	public List<String> legs;
	
	/** list of positions occupied by this pivot */
	public List<int[]> positions;
	
	/** list of directions of lengths having min length 3 */
	public Set<String> directions;

	/** list of leg pairs, so we can match orientations */
	public Set<String> pairs;
	
	public PivotOLD() {
		positions = new ArrayList<int[]>();
		legs = new ArrayList<String>();
		directions = new HashSet<String>();
		pairs = new HashSet<String>();
	}
	
	public int longestLeg() {
		int max = 0;
		if (legs == null) return 0;
		for (String leg : legs) {
			max = Math.max(max, leg.length());
		}
		return max;
	}
	
	public String toString() {
		String line = "row " + row + ", col " + col + ", legsCount " + legsCount + ", longestLeg " + longestLeg() + ", " + dumpLegs() + ", " + pairs();
		return line;
	}
	
	public String pairs() {
		if (pairs == null) return null;
		String line = "";
		for (String pair : pairs) line += pair + " ";
		return line;
		
	}
	
	public String dumpLegs() {
		String line = "";
		if (legs == null) return line;
		for (String leg : legs) if (leg != null) line += intersectingSymbol + "["+leg + "] ";
		return line;
	}

	/** return one leg */
	public String leg() {
		if (legs == null || legs.isEmpty()) return null;
		for (String leg : legs) return leg;
		return null;
	}
	
	
	public void makePairs() {
		if (directions == null) return;
		if (directions.contains("N") && directions.contains("E")) pairs.add("NE");
		if (directions.contains("N") && directions.contains("S")) pairs.add("NS");
		if (directions.contains("N") && directions.contains("W")) pairs.add("NW");
		if (directions.contains("E") && directions.contains("S")) pairs.add("ES");
		if (directions.contains("E") && directions.contains("W")) pairs.add("EW");
		if (directions.contains("S") && directions.contains("W")) pairs.add("SW");
	}
	
	public String html(String sub) {
		String html = "<tt>";
		
		Set<String> set = new HashSet<String>();
		set.add(row+","+col);
		for (int[] rc : positions) set.add(rc[0]+","+rc[1]);
		
		for (int r=0; r<PivotsOLD.H; r++) {
			for (int c=0; c<PivotsOLD.W; c++) {
				String letter = PivotsOLD.charAt(sub, new int[] {r, c});
				String key = r+","+c;
				if (set.contains(key)) html += "<b>" + letter + "</b>";
				else html += letter;
			}
			html += "<br>";
		}
		
		html += "</tt>";
		return html;
	}
	
	public static String html(String sub, List<PivotOLD> pivots) {
		String html = "<tt>";
		
		Set<String> set = new HashSet<String>();
		
		for (PivotOLD pivot : pivots) {
			if (pivot.legsCount > 1 && pivot.longestLeg() > 2) {
				set.add(pivot.row+","+pivot.col);
				for (int[] rc : pivot.positions) set.add(rc[0]+","+rc[1]);
			}
		}
		
		for (int r=0; r<PivotsOLD.H; r++) {
			for (int c=0; c<PivotsOLD.W; c++) {
				String letter = PivotsOLD.charAt(sub, new int[] {r, c});
				String key = r+","+c;
				if (set.contains(key)) html += "<b>" + letter + "</b>";
				else html += letter;
			}
			html += "<br>";
		}
		
		html += "</tt>";
		return html;
		
	}
	
	// produce ordered lists of positions for the given pivots
	public static List<Position> positionsFor(char direction, PivotOLD pivot) {
		
		List<Position> pos = new ArrayList<Position>();
		
		int row = pivot.row;
		int col = pivot.col;

		int[] deltas = deltasFor(direction);
		for (int i=0; i<4; i++) {
			if (row < 0 || row > 19) {
				throw new RuntimeException("bad row " + row + " dir " + direction + " pivot " + pivot);
			}
			if (col < 0 || col > 16) {
				throw new RuntimeException("bad col " + col + " dir " + direction + " pivot " + pivot);
			}
			Position p = new Position();
			p.row = row;
			p.col = col;
			pos.add(p);
			row += deltas[0];
			col += deltas[1];
		}
		return pos;
	}
	
	// return n-grams using the given pivot definition upon the given string 
	public static List<String> toNgram(String direction, PivotOLD pivot, String str) {
		List<String> list = new ArrayList<String>();
		for (int i=0; i<direction.length(); i++) {
			char d = direction.charAt(i);
			List<Position> pos = positionsFor(d, pivot);
			String ngram = "";
			for (Position p : pos) {
				ngram += str.charAt(CandidatePlaintext.posFrom(p.row, p.col));
			}
			list.add(ngram);
		}
		return list;
	}
	
	public static String dumpPositions(List<Position> pos) {
		String line = "";
		for (Position p : pos) {
			line += "(" + p.row + ", " + p.col + ") ";
		}
		return line;
	}
	
	
	public static int[] deltasFor(char direction) {
		if (direction == 'N') return new int[] {-1, 0};
		if (direction == 'S') return new int[] {1, 0};
		if (direction == 'E') return new int[] {0, 1};
		if (direction == 'W') return new int[] {0, -1};
		throw new RuntimeException("Bad direction: " + direction);
	}
	
	public static void main(String[] args) {
		String s1 = "UV";
		String s2 = "WX";
		String s3 = "YZ";
		
		for (int a=0; a<2; a++) {
			for (int b=0; b<2; b++) {
				for (int c=0; c<2; c++) {
					for (int d=0; d<2; d++) {
						for (int e=0; e<2; e++) {
							for (int f=0; f<2; f++) {
								String c1 = ""+s1.charAt(a)+s2.charAt(b)+s3.charAt(c);
								String c2 = ""+s1.charAt(d)+s2.charAt(e)+s3.charAt(f);
								System.out.println(c1+" "+c2+" "+(c1.equals(c2)));
							}	
						}
					}
				}
			}
		}
		}
	
}
