package com.zodiackillerciphers.transform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zodiackillerciphers.ciphers.Ciphers;

public abstract class TransformationBase implements Transformation {

	protected Parameter[] parameters;

	private Map<String, Parameter> parameterMap;

	protected List<StringBuffer> input;
	protected List<StringBuffer> output;
	
	/** map positions of original cipher to positions in transformed cipher.
	 * key: position in original cipher
	 * value: corresponding position in transformed cipher 
	 */
	protected Map<Integer, Integer> transformMap;

	public static String alphabet;
	static {
		alphabet = Ciphers.alphabet(Ciphers.cipher[0].cipher);
	}

	/** get alphabet position for given symbol */
	public static int toInt(char ch) {
		for (int i = 0; i < alphabet.length(); i++)
			if (alphabet.charAt(i) == ch)
				return i;
		return -1;
	}

	/** get char for given alphabet position */
	public static char fromInt(int i) {
		return alphabet.charAt(i);
	}

	@Override
	public void processParameters(float[] genomeValues) {
		if (parameters == null
				&& (genomeValues == null || genomeValues.length == 0))
			return;
		if (parameters == null && genomeValues != null)
			throw new IllegalArgumentException("Expected no arguments but got "
					+ genomeValues.length);
		if (parameters != null && genomeValues == null)
			throw new IllegalArgumentException("Expected " + parameters.length
					+ " arguments but got none.");

		if (genomeValues == null || genomeValues.length != parameters.length)
			throw new IllegalArgumentException("Expected " + parameters.length
					+ " arguments but got " + genomeValues.length);
		int i = 0;
		for (float val : genomeValues)
			parameters[i++].setValue(val);
	}

	void initParameterMap() {
		if (parameterMap == null) {
			setupParameters();
			parameterMap = new HashMap<String, Parameter>();
			for (Parameter p : parameters)
				parameterMap.put(p.getName(), p);
		}
	}

	protected void say(String message) {
		System.out.println(message);
	}

	@Override
	public void setParameterValue(String name, Integer value) {
		initParameterMap();
		Parameter p = parameterMap.get(name);
		if (p == null)
			throw new IllegalArgumentException(
					"No parameter definition found for " + name);
		p.setValue(value);
	}
	public void setParameterValue(String name, Float value) {
		initParameterMap();
		Parameter p = parameterMap.get(name);
		if (p == null)
			throw new IllegalArgumentException(
					"No parameter definition found for " + name);
		p.setValue(value);
	}

	@Override
	public Integer getParameterValue(String name) {
		initParameterMap();
		Parameter p = parameterMap.get(name);
		if (p == null)
			throw new IllegalArgumentException(
					"No parameter definition found for " + name);
		else
			return p.getValue();
	}

	public Parameter getParameter(String name) {
		return parameterMap.get(name);
	}

	public void execute() {
		execute(false);
	}

	public void execute(boolean showSteps) {
		if (input == null) 
			throw new IllegalArgumentException("Input is null.");
		if (validateParameters()) {
			executeMain(showSteps);
			// always pad in case one or more lines do not match the grid width
			TransformationBase.pad(input);
			
		}
	}

	public boolean validateParameters() {
		if (parameters == null || parameters.length == 0)
			return true;
		for (Parameter p : parameters) {
			if (!p.validate())
				return false;
		}
		return true;
	}

	public abstract void executeMain(boolean showSteps);

	/** make a duplicate copy */
	public static List<StringBuffer> copy(List<StringBuffer> list) {
		List<StringBuffer> list2 = new ArrayList<StringBuffer>();
		for (StringBuffer sb : list)
			list2.add(new StringBuffer(sb));
		return list2;
	}

	public static void dump(List<StringBuffer> list) {
		if (list == null)
			return;
		for (StringBuffer line : list)
			System.out.println(line);
	}

	/** convert list of stringbuffers into a single line */
	public static StringBuffer fromList(List<StringBuffer> list) {
		return fromList(list, true);
	}
	/** convert list of stringbuffers into a single line */
	public static StringBuffer fromList(List<StringBuffer> list, boolean removeSpaces) {
		StringBuffer sb = new StringBuffer();
		for (StringBuffer s : list)
			sb.append(s.toString().replaceAll(" ", removeSpaces ? "" : " "));
		//System.out.println("fromList " + sb);
		return sb;
	}

	public static List<StringBuffer> toList(List<StringBuffer> cipher, int width) {
		return toList(cipher, width, true);
	}
	public static List<StringBuffer> toList(List<StringBuffer> cipher, int width, boolean removeSpaces) {
		return toList(TransformationBase.fromList(cipher, removeSpaces).toString(), width);
	}

	/**
	 * convert one-lined text into list of stringbuffers of the given length
	 * (width)
	 */
	public static List<StringBuffer> toList(String cipher, int width) {
		List<StringBuffer> list = new ArrayList<StringBuffer>();

		int i = 0;
		while (i < cipher.length()) {
			list.add(new StringBuffer(cipher.substring(i,
					Math.min(i + width, cipher.length()))));
			i += width;
			//System.out.println("toList added " + list.get(list.size()-1));
		}
		TransformationBase.pad(list);
		return list;
	}
	
	/** remove trailing spaces */
	public static StringBuffer rtrim(StringBuffer sb) {
		if (sb == null) return sb;
		return new StringBuffer(sb.toString().replaceAll("\\s+$",""));
	}

	/** return max width of given list */
	public static int width(List<StringBuffer> list) {
		if (list == null)
			return 0;
		int max = 0;
		for (StringBuffer sb : list) {
			if (sb == null)
				continue;
			max = Math.max(sb.length(), max);
		}
		return max;
	}

	/** return height of given list */
	public static int height(List<StringBuffer> list) {
		if (list == null)
			return 0;
		return list.size();
	}

	public List<StringBuffer> getInput() {
		return input;
	}

	public void setInput(List<StringBuffer> input) {
		this.input = input;
	}

	public List<StringBuffer> getOutput() {
		return output;
	}

	public void setOutput(List<StringBuffer> output) {
		this.output = output;
	}
	
	public int inputLength() {
		return inputHeight() * inputWidth();
	}

	public int inputHeight() {
		if (input == null || input.size() == 0) return 0;
		return input.size();
	}
	public int inputWidth() {
		if (input == null || input.size() == 0) return 0;
		return input.get(0).length();
	}

	/** set the given (row,col) position to the given character */
	public static void setCharAt(List<StringBuffer> list, int row, int col, char ch) {
		int H = list.size();
		int W = list.get(0).length();
		if (row < 0 || col < 0) return;
		if (row >= H || col >= W) return;
		
		//System.out.println("setting " + row + " " + col + " to " + ch + " from " + getCharAt(list, row, col));
		list.get(row).setCharAt(col, ch);
	}
	/** position-based version */
	public void setCharAt(List<StringBuffer> list, int pos, char ch) {
		setCharAt(list, rowFromPos(pos), colFromPos(pos), ch);
	}
	
	/** return char at given position */
	public static char getCharAt(List<StringBuffer> list, int row, int col) {
		return list.get(row).charAt(col);
	}
	
	/** position-based version */
	public char getCharAt(List<StringBuffer> list, int pos) {
		return list.get(rowFromPos(pos)).charAt(colFromPos(pos));
	}
	
	/** return row from position */
	public int rowFromPos(int pos) {
		return pos / inputWidth();
	}

	/** return column from position */
	public int colFromPos(int pos) {
		return pos % inputWidth();
	}
	
	/** return position from row and column */
	public int posFromRowCol(int row, int col) {
		return row*inputWidth() + col;
	}

	/** pad any truncated lines to fill out the grid */
	public static void pad(List<StringBuffer> list) {
		if (list == null) return;
		int max = width(list);
		for (StringBuffer sb : list) if (sb.length() < max) {
			while (sb.length() < max) sb.append(' ');
		}
	}

	/** replace a rectangular region in the given grid with the given region */
	public static List<StringBuffer> replaceBox(List<StringBuffer> list, List<StringBuffer> box, int row, int col) {
		List<StringBuffer> result = copy(list);
		if (box == null) return result;
		if (row < 0 || col < 0) return result;
		int H = list.size();
		int W = list.get(0).length();
		int boxHeight = box.size();
		if (boxHeight == 0) return result;
		int boxWidth = box.get(0).length();
		if (row+boxHeight-1 >= H) return result;
		if (col+boxWidth-1 >= W) return result;
		
		for (int r=0; r<boxHeight; r++) {
			for (int c=0; c<boxWidth; c++) {
				setCharAt(result, row+r, col+c, getCharAt(box, r, c));
			}
		}
		
		return result;
	}
	
	public static List<StringBuffer> truncateBox(List<StringBuffer> box, int width, int height) {
		//System.out.println("truncate width " + width + " height " + height);
		if (box == null) return null;
		List<StringBuffer> boxNew = new ArrayList<StringBuffer>();
		for (int i=0; i<height&&i<box.size(); i++) {
			StringBuffer sb = box.get(i);
			if (sb.length() <= width) boxNew.add(sb);
			else boxNew.add(new StringBuffer(sb.substring(0, width)));
		}
		return boxNew;
	}

	/* detects overlapped boxes
	 */
	public static boolean overlap(Box box1, Box box2) {
		if (box1 == null) return false;
		if (box2 == null) return false;
		for (Point point : box1.corners())
			if (box2.isPointInside(point)) return true;
		for (Point point : box2.corners())
			if (box1.isPointInside(point)) return true;
		return false;
	}

	/** extract a rectangular region from the given cipher text */
	public static List<StringBuffer> extractBox(List<StringBuffer> list, int row, int col, int height, int width, boolean showSteps) {
		if (row < 0 || col < 0) {
			if (showSteps) System.out.println("Can't select: Box overlaps top or left.");
			return copy(list);
		}
		if (row >= list.size() || col >= list.get(0).length()) {
			if (showSteps) System.out.println("Can't select: Box overlaps bottom or right.");
			return copy(list);
		}
		if (row+height-1 >= list.size() || col+width-1 >= list.get(0).length()) {
			if (showSteps) System.out.println("Can't select: Box overlaps bottom or right.");
			return copy(list);
		}
		List<StringBuffer> result = new ArrayList<StringBuffer>();
		for (int r=row; r<row+height; r++) {
			StringBuffer newRow = new StringBuffer();
			for (int c=col; c<col+width; c++) {
				newRow.append(list.get(r).charAt(c));
			}
			result.add(newRow);
		}
		return result;
	}
	
	/** extract a linear region from the given cipher text */
	public static String extractLinear(List<StringBuffer> list, int pos, int length) {
		if (list == null) return null;
		StringBuffer s = fromList(list, false);
		if (pos + length - 1 >= s.length()) return s.substring(pos);
		return s.substring(pos, pos+length);
	}
	
	/** fill the entire grid with the given symbol */
	public static void fill(List<StringBuffer> grid, char symbol) {
		for (StringBuffer sb : grid)
			for (int i = 0; i < sb.length(); i++)
				sb.setCharAt(i, symbol);
	}	
	
}
