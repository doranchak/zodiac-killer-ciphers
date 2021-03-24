package com.zodiackillerciphers.dictionary;

import java.util.ArrayList;
import java.util.List;

public class BoggleBean implements Comparable {
	public String word;
	public int freq;
	public Integer direction;
	public int row;
	public int col;
	public List<int[]> path;
	
	public char[][] grid;
	
	public BoggleBean(String word, int freq, Integer direction, int row, int col,
			List<int[]> path, char[][] grid) {
		super();
		this.word = word;
		this.freq = freq;
		this.direction = direction;
		this.row = row;
		this.col = col;
		this.path = new ArrayList<int[]>();
		//this.path.add(row + "," + col);
		this.path.addAll(path);
		this.grid = grid;
	}
	public static String dumpPath(List<int[]> path) {
		if (path == null) return "";
		StringBuffer sb = new StringBuffer();
		for (int[] p : path) {
			sb.append(p[0]).append(",").append(p[1]).append(" ");
		}
		return sb.toString();
	}
	
	public String toString() {
		return word + ", " + word.length() + ", " + freq + ", d" + direction + ", row " + row + ", column " + col + ", path " + dumpPath(path);
	}
	public int compareTo(Object o) {
		if (o == null) return 1;
		BoggleBean b1 = this;
		BoggleBean b2 = (BoggleBean) o;
		if (b1.word.length() > b2.word.length()) return -1;
		if (b2.word.length() > b1.word.length()) return 1;
		return Integer.compare(b2.freq, b1.freq);
		
	}
	
	public String toHtml() {
		String[][] cells = new String[grid.length][grid[0].length];
		for (int[] p : path) {
			int row = p[0];
			int col = p[1];
			cells[row][col] = "<span class=\"h\">" + grid[row][col]+"</span>";
		}
		
		for (int row=0; row<grid.length; row++) {
			for (int col=0; col<grid[row].length; col++) {
				if (cells[row][col] != null) continue;
				cells[row][col] = "" + grid[row][col];
			}
		}
		
		String html = "<span class=\"g\">";
		for (int row=0; row<grid.length; row++) {
			for (int col=0; col<grid[row].length; col++) {
				html += cells[row][col];
			}
			html += "<br/>";
		}
		html += "</span>";
		return html;
	}
	
	public static String toHtml(List<BoggleBean> beans) {
		if (beans == null) return "";
		String html = "<style> " +
		".g { font-family: courier, monospace; font-size: 10pt;} " + 
		".h { color: white; background-color: black; font-weight: bold;} " +
		".w { font-weight: bold; color: #090; } " + 
		"td { text-align: center; } " + 
		"</style>";

		int maxLength = -1;
		for (BoggleBean bean : beans) maxLength = Math.max(maxLength, bean.word.length());
		
		int[] countByLen = new int[maxLength+1];
		
		String words = "<p><b>Found " + beans.size() + " words:</b>  ";
		for (BoggleBean bean : beans) {
			words += bean.word + " ";
			countByLen[bean.word.length()]++;
		}
		words += "</p><p><b>Totals by word length:</b><ul>";
		html += words;

		for (int i=countByLen.length-1; i>=0; i--) {
			if (countByLen[i] == 0) continue;
			html += "<li><b>Length " + i + ":</b> " + countByLen[i] + "</li>"; 
		}
		html += "</ul></p>";
		
		int count = 0;
		for (BoggleBean bean : beans) {
			if (count % 5 == 0) {
				if (count == 0) html += "<table><tr>";
				else html += "</tr><tr>";
			}
			html += "<td><span class=\"w\">" + bean.word.toUpperCase() + "</span><br/>" + bean.toHtml() + "</td>";
			count++;
		}
		html += "</table>";
		return html;
		
		

	}
	
}
