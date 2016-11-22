package com.zodiackillerciphers.transform;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.transform.operations.FlipHorizontal;
import com.zodiackillerciphers.transform.operations.Rotate;

import ec.util.MersenneTwisterFast;

public class CipherTransformations {
	static MersenneTwisterFast rand = new MersenneTwisterFast();

	/** randomly scramble the given text, using http://en.wikipedia.org/wiki/Fisher%E2%80%93Yates_shuffle */
	public static synchronized String shuffle(String cipher) {
		if (cipher == null) return null;
		StringBuffer sb = new StringBuffer(cipher);
		for (int i=sb.length()-1; i>=1; i--) {
			int j = rand.nextInt(i+1);
			if (i==j) continue;
			char tmp = sb.charAt(i);
			sb.setCharAt(i, sb.charAt(j));
			sb.setCharAt(j, tmp);
		}
		return sb.toString();
	}
	
	/** inplace array based version of the above shuffle */
	public static void shuffle(Object[] array) {
		if (array == null) return;
		for (int i=array.length-1; i>=1; i--) {
			int j=rand.nextInt(i+1);
			if (i==j) continue;
			Object tmp = array[i];
			array[i] = array[j];
			array[j] = tmp;
		}
	}
	public static void shuffle(int[] array) {
		if (array == null) return;
		for (int i=array.length-1; i>=1; i--) {
			int j=rand.nextInt(i+1);
			if (i==j) continue;
			int tmp = array[i];
			array[i] = array[j];
			array[j] = tmp;
		}
	}

	
	public static void shuffleOld() {
		String c = Ciphers.cipher[1].cipher;
		for (int i=0; i<c.length(); i++) {
			char ch = c.charAt(i);
			System.out.println(Math.random() + " " + ch);
		}
	}


	

	/*
	public static void testSnake() {
		List<StringBuffer> list = TransformationBase.toList(Ciphers.cipher[0].cipher, 17);
		list = snake(list);
		TransformationBase.dump(list);
	}*/
	
	
	/** Dan Olson's manipulation:
	 * Divide cipher into two blocks: 
	 * Block 1: Rows [0, row-1]
	 * Block 2: Rows [row, height-1]
	 * 
	 * Place Block 1 to the left of Block 2, and read out new cipher text.
	 * 
	 * If reverse is true, place Block 2 to the left of Block 1 instead.
	 * NOTE: this has been replaced by "PeriodRow"
	 */
	/*public static List<StringBuffer> olson(List<StringBuffer> list, int row, boolean reverse) {
		int h = TransformationBase.height(list);
		int w = TransformationBase.width(list);
		if (row < 1) throw new RuntimeException("Row must be > 0");
		if (row >= h) throw new RuntimeException("Row must be < " + h);
		List<StringBuffer> block1 = new ArrayList<StringBuffer>();
		List<StringBuffer> block2 = new ArrayList<StringBuffer>();
		
		
		for (int r=0; r<row; r++) block1.add(list.get(r));
		for (int r=row; r<h; r++) block2.add(list.get(r));		
		

		int b1 = 0;
		int b2 = 0;
		List<StringBuffer> s1 = block1;
		List<StringBuffer> s2 = block2;
		if (reverse) {
			s1 = block2;
			s2 = block1;
		}
		
		StringBuffer line = new StringBuffer();
		while (line.length() < h*w) {
			if (b1<s1.size()) line.append(s1.get(b1++));
			if (b2<s2.size()) line.append(s2.get(b2++));
		}
		
		return TransformationBase.toList(line.toString(), w);
	}
	public static void testOlson() {
		List<StringBuffer> list = TransformationBase.toList(Ciphers.cipher[0].cipher, 17);
		
		for (int row=1; row<20; row++) {
			
			System.out.println("= " + row + ", false =");
			List<StringBuffer> list2 = olson(list, row, false);
			for (StringBuffer sb : list2) System.out.println(sb);
			
			System.out.println("= " + row + ", true =");
			list2 = olson(list, row, true);
			for (StringBuffer sb : list2) System.out.println(sb);
		}
	}*/

	
	

	/*
	public static List<StringBuffer> mergeLinear(List<StringBuffer> list,
			int pos, int length, char c1, char c2) {
		int H = list.size();
		int W = list.get(0).length();
		List<StringBuffer> result = TransformationBase.copy(list);
		if (pos < 0) return result;
		if (pos+length-1 >= H*W) return result;
				
		for (int i=pos; i<pos+length; i++) {
			int r = i / W;
			int c = i % W;
			if (getCharAt(list, r, c) == c2)
				setCharAt(result, r, c, c1);
		}
		return result;
	}*/
	

	/*
	public static List<StringBuffer> deleteLinear(List<StringBuffer> list,
			int pos, int length, char c1, boolean collapse) {
		int H = list.size();
		int W = list.get(0).length();
		List<StringBuffer> result = TransformationBase.copy(list);
		if (pos < 0) return result;
		if (pos+length-1 >= H*W) return result;
				
		for (int i=pos; i<pos+length; i++) {
			int r = i / W;
			int c = i % W;
			if (getCharAt(list, r, c) == c1)
				setCharAt(result, r, c, ' ');
		}
		
		if (collapse) {
			StringBuffer sbNew = new StringBuffer();
			for (StringBuffer sb : result) {
				sbNew.append(sb.toString().replaceAll(" ", ""));
			}
			result = TransformationBase.toList(sbNew.toString(), result.get(0).length());
		}
		return result;
	}*/
	
	

	/** reverse the text within the given line of the given length */
	/*public static List<StringBuffer> reverseLinear(List<StringBuffer> list, int pos, int length) {
		List<StringBuffer> result = TransformationBase.copy(list);
		if (pos < 0) return result;
		if (pos+length-1 >= list.size()*list.get(0).length()) return result;
		
		int W = list.get(0).length();
		int H = list.size();
		
		for (int i=0; i<length; i++) {
			int p1 = pos+i;
			int p2 = pos+length-1-i;
			
			int r1 = p1/W;
			int c1 = p1%W;
			
			int r2 = p2/W;
			int c2 = p2%W;
			
			setCharAt(result, r2, c2, getCharAt(list, r1, c1));
		}
		return result;
	}*/
	
	
	
	
	/** extract a linear region from the given cipher text */
	public static List<StringBuffer> extractLine(List<StringBuffer> list, int pos, int length) {
		if (pos < 0) return TransformationBase.copy(list);
		if (pos+length-1 >= list.size()*list.get(0).length()) return TransformationBase.copy(list);
		
		int H = list.size();
		int W = list.get(0).length();
		
		String line = "";
		for (int i=pos; i<pos+length; i++) {
			int row = i/W;
			int col = i % W;
			line += list.get(row).charAt(col);
		}
		return TransformationBase.toList(line, W);
	}	
	
	public static void testToList() {
		String cipher = "abcdefghijklmn";
		
		for (int i=1; i<20; i++) {
			System.out.println("=== WIDTH: " + i + " ===");
			List<StringBuffer> list = TransformationBase.toList(cipher, i);
			for (StringBuffer sb : list) System.out.println("R0: " + sb.length() + ", " + sb);
			List<StringBuffer> list2 = rotate(list, 90);
			for (StringBuffer sb : list2) System.out.println("R90: " + sb);
			List<StringBuffer> list3 = rotate(list, 180);
			for (StringBuffer sb : list3) System.out.println("R180: " + sb);
			List<StringBuffer> list4 = rotate(list, 270);
			for (StringBuffer sb : list4) System.out.println("R270: " + sb);
			
		}
	}
	
	/*
	public static void convert408() {
		List<StringBuffer> list = TransformationBase.toList(Ciphers.cipher[1].cipher, 17);
		list = snake(list);
		list = olson(list, 20, false);
		System.out.println(TransformationBase.fromList(list));
	}*/
	
	/** how often do random shuffles of the 340 produce n occurrences of G2? */
	public static void testG2(int n) {
		String z340 = Ciphers.cipher[0].cipher;
		System.out.println(StringUtils.countMatches(z340, "G2"));
		
		long total=0;
		long matches=0;
		while (true) {
			String shuffle = shuffle(z340);
			total++;
			if (StringUtils.countMatches(shuffle,"G2") >= n) { 
				matches++;
				System.out.println(matches + " of " + total);
			}
		}
	}
	public static int indexOf(Pattern pattern, String s) {
	    Matcher matcher = pattern.matcher(s);
	    return matcher.find() ? matcher.start() : -1;
	}	
	public static void testPattern() {
		String z340 = Ciphers.cipher[0].cipher;
		
		//int index = indexOf(Pattern.compile("\\+...........\\|5F"), z340);
		//System.out.println(index);

		
		long total=0;
		long matches=0;
		Pattern p = Pattern.compile("5.4.\\.");
		while (true) {
			String shuffle = shuffle(z340);
			total++;
			int index = indexOf(p, shuffle);
			if (index == -1) continue;
			String shuffle2 = shuffle.substring(index+5);
			index = indexOf(p, shuffle2);
			if (index == -1) continue;
			matches++;
			System.out.println(matches + " of " + total + ", " + shuffle);
		}
		
	}

	/*
	public static void testPeriodRows() {
		String cipher = Ciphers.cipher[0].cipher;
		List<StringBuffer> grid = TransformationBase.toList(cipher, 17); 
		for (int period=1; period<11; period++) {
			System.out.println(period);
			List<StringBuffer> result = periodRows(grid, period);
			for (StringBuffer sb : result) System.out.println(sb);
			System.out.println();
		}
	}
	public static void testPeriodCols() {
		String cipher = Ciphers.cipher[0].cipher;
		List<StringBuffer> grid = TransformationBase.toList(cipher, 17);
		
		TransformationBase.dump(grid); System.out.println();
		int period = 3;
		List<StringBuffer> result = periodColumns(grid, period);
		TransformationBase.dump(result);
	}*/
	
	public static boolean equals(List<StringBuffer> grid1, List<StringBuffer> grid2) {
		if (grid1 == null && grid2 == null) return true;
		if (grid1 == null && grid2 != null) return false;
		if (grid1 != null && grid2 == null) return false;
		
		if (grid1.size() != grid2.size()) return false;
		for (int i=0; i<grid1.size(); i++) {
			StringBuffer sb1 = grid1.get(i);
			StringBuffer sb2 = grid2.get(i);
			if (sb1.length() != sb2.length()) return false;
			if (!sb1.toString().equals(sb2.toString())) {
				return false;
			}
		}
		return true;
	}
	
	public static List<StringBuffer> rotate(List<StringBuffer> list, int degrees) {
		if (degrees == 90) degrees = 1;
		else if (degrees == 180) degrees = 2;
		else if (degrees == 270) degrees = 3;
		Rotate r = new Rotate(list, degrees);
		r.execute();
		return r.getOutput();
	}
	public static List<StringBuffer> flipHorizontal(List<StringBuffer> list, int f) {
		if (f == 0) return list;
		FlipHorizontal flip = new FlipHorizontal(list);
		flip.execute();
		return flip.getOutput();
	}

	/** simple version of horizontal flip */
	public static String flipHorizontal(String str, int rows, int cols) {
		String flip = "";
		for (int row=0; row<rows; row++) {
			for (int col=(cols-1); col>=0; col--) {
				flip += str.charAt(row*cols+col);
			}
		}
		return flip;
	}
	
	public static void testExtract() {
		String cipher = Ciphers.cipher[0].cipher;
		List<StringBuffer> grid = TransformationBase.toList(cipher, 17);
		
		for (int i=0; i<10; i++) {
			int row = (int) (Math.random() * 15);
			int col = (int) (Math.random() * 10);
			int width = (int) (Math.random() * 10 + 5);
			int height = (int) (Math.random() * 10 + 5);
			System.out.println(row + " " + col + " " + width + " " + height);
			TransformationBase.dump(TransformationBase.extractBox(grid, row, col, height, width, true));
			System.out.println();
		}
		for (int i=0; i<10; i++) {
			int pos = (int) (Math.random() * 300+ 50);
			int length = (int) (Math.random() * 30 + 20);
			System.out.println(pos + " " + length);
			TransformationBase.dump(extractLine(grid, pos, length));
			System.out.println();
		}
	}
	
	/*
	public static void testSwap() {
		String cipher = Ciphers.cipher[0].cipher;
		List<StringBuffer> grid = TransformationBase.toList(cipher, 17);
		
		/*for (int i=0; i<10; i++) {
			int row1 = (int) (Math.random() * 15);
			int col1 = (int) (Math.random() * 10);
			int row2 = (int) (Math.random() * 15);
			int col2 = (int) (Math.random() * 10);
			int width = (int) (Math.random() * 10 + 5);
			int height = (int) (Math.random() * 10 + 5);
			System.out.println(row1 + " " + col1 + " " + row2 + " " + col2 + " " + width + " " + height);
			List<StringBuffer> result = swap(grid, row1, col1, row2, col2, height, width);
			System.out.println("equal? " + equals(grid, result));
			dump(result);
			System.out.println();
		}*/
		
		/*int row1 = 1;
		int col1 = 1;
		int row2 = 17;
		int col2 = 12;
		int width = 5;
		int height = 3;
		System.out.println(row1 + " " + col1 + " " + row2 + " " + col2 + " " + height + " " + width);
		List<StringBuffer> result = swap(grid, row1, col1, row2, col2, height, width);
		System.out.println("equal? " + equals(grid, result));
		TransformationBase.dump(result);
		System.out.println();

	}*/
	/*
	public static void testShift() {
		String cipher = Ciphers.cipher[0].cipher;
		List<StringBuffer> grid = TransformationBase.toList(cipher, 17);
		TransformationBase.dump(grid);
		System.out.println();
		//List<StringBuffer> result = shift(grid, 4, 5, 4, 8, 0, 2);
		List<StringBuffer> result = shift(grid, 5, 5, 10, 8, 1, 1);
		TransformationBase.dump(result);
		
	}
	public static void testReverse() {
		String cipher = Ciphers.cipher[0].cipher;
		List<StringBuffer> grid = TransformationBase.toList(cipher, 17);
		TransformationBase.dump(grid);
		System.out.println();
		List<StringBuffer> result = reverse(grid, 5, 5, 10, 8);
		TransformationBase.dump(result);
		
	}

	public static void testReverseLine() {
		String cipher = Ciphers.cipher[0].cipher;
		List<StringBuffer> grid = TransformationBase.toList(cipher, 17);
		TransformationBase.dump(grid);
		System.out.println();
		List<StringBuffer> result = reverseLinear(grid, 36, 100);
		TransformationBase.dump(result);
		
	}
	
	public static void testSpiral() {
		String cipher = Ciphers.cipher[0].cipher;
		List<StringBuffer> grid = TransformationBase.toList(cipher, 17);
		TransformationBase.dump(grid);
		
		for (int i=0; i<8; i++) {
			System.out.println();
			List<StringBuffer> result = spiral(grid, 4, 5, 5, 8, i, false);
			TransformationBase.dump(result);
		}
		
	}
	public static void testPeriod() {
		String cipher = Ciphers.cipher[0].cipher;
		List<StringBuffer> grid = TransformationBase.toList(cipher, 17);
		TransformationBase.dump(grid);
		
		for (int i=0; i<10; i++) {
			System.out.println("Period " + i);
			List<StringBuffer> result = period(grid, 4, 5, 5, 7, i);
			TransformationBase.dump(result);
		}
		
	}

	public static void testQuadrants() {
		String cipher = Ciphers.cipher[0].cipher;
		List<StringBuffer> grid = TransformationBase.toList(cipher, 17);
		TransformationBase.dump(grid);

		System.out.println();
		List<StringBuffer> result = quadrants(grid, 3, 2, 10, 9, 5, 4, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0);
		TransformationBase.dump(result);

	}
	
	public static void testMerge() {
		String cipher = Ciphers.cipher[0].cipher;
		List<StringBuffer> grid = TransformationBase.toList(cipher, 17);
		TransformationBase.dump(grid);

		System.out.println();
		//List<StringBuffer> result = merge(grid, 0, 0, 20, 17, 'B', 'c');
		List<StringBuffer> result = mergeLinear(grid, 300, 40, 'B', '+');
		TransformationBase.dump(result);

	}
	public static void testDelete() {
		String cipher = Ciphers.cipher[0].cipher;
		List<StringBuffer> grid = TransformationBase.toList(cipher, 17);
		TransformationBase.dump(grid);

		System.out.println();
		//List<StringBuffer> result = delete(grid, 0, 0, 20, 17, '+', true);
		List<StringBuffer> result = deleteLinear(grid, 280, 60, '+', true);
		TransformationBase.dump(result);

	}*/
	
	public static void main(String[] args) {
		//testToList();
		//testSnake();
		//testExtract();
		//testDiagonal();
		//testOlson();
		//convert408();
		//testG2(4);
		//testPattern();
		//System.out.println(shuffle(Ciphers.cipher[1].cipher));
		//testPeriodRows();
		//testPeriodCols();
		//testSwap();
		//testShift();
		//testReverse();
		//testReverseLine();
		//testSpiral();
		//testPeriod();
		//testQuadrants();
		//testMerge();
		//testDelete();
	}

}
