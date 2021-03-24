package com.zodiackillerciphers.transform.operations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.lucene.Stats;
import com.zodiackillerciphers.transform.Transformation;
import com.zodiackillerciphers.transform.TransformationBase;

public class Snake extends TransformationBase {
	public static String templateAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

	@Override
	public void setupParameters() {
	}
	
	public Snake(List<StringBuffer> input) {
		this.input = input;
	}

	@Override
	public void executeMain(boolean showSteps) {
		/**
		 * snake through the cipher text. start at upper left, read all the way
		 * to the right, then go down one line, and read to the left. continue
		 * alternating between left-to-right, and right-to-left directions.
		 */
		List<StringBuffer> result = new ArrayList<StringBuffer>();
		for (int i = 0; i < input.size(); i++) {
			StringBuffer sb = input.get(i);
			if (i % 2 == 0)
				result.add(new StringBuffer(sb));
			else
				result.add(new StringBuffer(sb).reverse());
		}
		if (showSteps) {
			say("After snaking:");
			dump(result);
		}
		output = result;
	}
	public String toString() {
		return "Snake()";    
	}
	public static void test() {
		List<StringBuffer> list = TransformationBase.toList(Ciphers.cipher[0].cipher, 17);
		dump(list);
		Transformation t = new Snake(list);
		t.execute(true);
		list = TransformationBase.toList(Ciphers.Z408_SOLUTION.substring(0, 340), 17);
		dump(list);
		t = new Snake(list);
		t.execute(true);
		String cipher = Ciphers.Z408_SOLUTION.substring(0, 340);
		System.out.println("----- simpler transform method for " + cipher + ": ");
		int width = 17;
		for (int i=0; i<4; i++) {
			for (boolean b : new boolean[] {false, true}) {
				String xform = transform(cipher, i, b, width, false, false);
				System.out.println(i + " " + b + " " + xform);
				String unxform = transform(xform, i, b, width, false, true);
				System.out.println(i + " " + b + " " + unxform);
				if (!cipher.equals(unxform)) {
					throw new RuntimeException("NO MATCH");
				}
			}
		}
		System.out.println("----- irregular: ");
		String xform = transform(cipher, 0, false, 4, false, false);
		String unxform = transform(xform, 0, false, 4, false, true);
		System.out.println("  xform: "+ xform);
		System.out.println("unxform: "+ unxform);
		if (!cipher.equals(unxform)) {
			throw new RuntimeException("NO MATCH");
		}
		System.out.println("--- rectangle test.");
		for (int i=0; i<4; i++) {
			for (boolean b : new boolean[] {false, true}) {
				xform = transform(cipher, i, b, 17, true, false);
				System.out.println(i + " " + b + " " + xform);
				unxform = transform(xform, i, b, width, true, true);
				System.out.println(i + " " + b + " " + unxform);
				if (!cipher.equals(unxform)) {
					throw new RuntimeException("NO MATCH");
				}
			}
		}
		
	}
	
	/** simple version.  output snake path for the given input, starting at one of the 4 corners, going in one
	 * of two available directions (if directionRow is true, then favor going by rows, otherwise by columns).
	 * 
	 * Should support widths in [2,input.length/2]
	 * 
	 * NOTE: do NOT include spaces in the input.  this is not supported because they are treated specially for
	 * irregular transpositions 
	 * 
	 * if rectangle is true, then instead of snaking, start over from the next row/column when hitting the grid boundary
	 *  
	 */
	public static String transform(String input, int corner, boolean directionRow, int width, boolean rectangle, boolean reverse) {
		int leftover = input.length() % width;
		boolean irregular = leftover > 0;
		//System.out.println("leftover " + leftover + " irregular " + irregular + " len " + input.length());
		if (irregular) {
			if (reverse) {
				// we need to inject the padding back into the input, which was already transposed.
				// so let's see where the padding would be.
				int lengthWithPadding = input.length() + width - leftover;
				StringBuilder template = new StringBuilder();
				int index = 0;
				for (int i=0; i<input.length(); i++) {
					template.append(templateAlphabet.charAt(index));
					index = (index + 1) % templateAlphabet.length();
				}
				for (int i=0; i<width-leftover; i++) {
					template.append(" ");
				}
				// transpose the template to see where the spaces are
				StringBuilder templateTransposed = new StringBuilder(transform(template.toString(), corner, directionRow, width, false, false));
				//System.out.println("- length with padding: " + lengthWithPadding);
				//System.out.println("- template length " + template.length() + ": " + template);
				//System.out.println("- template transposed: " + templateTransposed);
				// replace the template with the input
				index = 0;
				for (int i=0; i<templateTransposed.length(); i++) {
					char ch = templateTransposed.charAt(i);
					if (ch == ' ') continue;
					templateTransposed.setCharAt(i, input.charAt(index++));
				}
				input = templateTransposed.toString();
				//System.out.println("- input with padding length " + input.length() + ": " + input);
			} else {
				for (int i=0; i<width-leftover; i++) {
					input = input + " ";
				}
			}
		}
		
		StringBuilder sb = new StringBuilder();
		int height = input.length() / width;
		for (int i=0; i<input.length(); i++) {
			sb.append(" ");
		}
		int[] rowCol = new int[2];
		SnakeDirection directionPrimary = null;
		SnakeDirection directionSecondary = null;
		if (corner == 0) {
			rowCol = new int[] {0,0};
			if (directionRow) {
				directionPrimary = SnakeDirection.SOUTH;
				directionSecondary = SnakeDirection.EAST;
			}
			else {
				directionPrimary = SnakeDirection.EAST;
				directionSecondary = SnakeDirection.SOUTH;
			}
		} else if (corner == 1) {
			rowCol = new int[] {0,width-1};
			if (directionRow) {
				directionPrimary = SnakeDirection.SOUTH;
				directionSecondary = SnakeDirection.WEST;
			}
			else {
				directionPrimary = SnakeDirection.WEST;
				directionSecondary = SnakeDirection.SOUTH;
			}
		} else if (corner == 2) {
			rowCol = new int[] {height-1,0};
			if (directionRow) {
				directionPrimary = SnakeDirection.NORTH;
				directionSecondary = SnakeDirection.EAST;
			}
			else {
				directionPrimary = SnakeDirection.EAST;
				directionSecondary = SnakeDirection.NORTH;
			}
		} else if (corner == 3) {
			rowCol = new int[] {height-1,width-1};
			if (directionRow) {
				directionPrimary = SnakeDirection.NORTH;
				directionSecondary = SnakeDirection.WEST;
			}
			else {
				directionPrimary = SnakeDirection.WEST;
				directionSecondary = SnakeDirection.NORTH;
			}
		}
		int i=0;
		while (i<input.length()) {
			int posTransformed = rowCol[0]*width+rowCol[1];
			int posOriginal = i;
//			System.out.println("i " + i + " rowCol " + Arrays.toString(rowCol) + " pos " + pos + " primary " + directionPrimary + " secondary " + directionSecondary);
			if (reverse) {
				sb.setCharAt(posTransformed, input.charAt(posOriginal));
			} else {
				sb.setCharAt(posOriginal, input.charAt(posTransformed));
			}
			//System.out.println(Arrays.toString(rowCol) + " " + posTransformed + ": " + sb + ": " + input + ", " + input.charAt(posTransformed));
			i++;
			
			// attempt to move.
			boolean result = move(rowCol, directionSecondary, height, width);

			// if failed, then 1) move in the primary direction, and flip the secondary direction (if snaking) or
			// jump to the beginning of the next row/column (if not snaking)
			if (!result) {
				move(rowCol, directionPrimary, height, width);
				if (rectangle) {
					if (directionSecondary == SnakeDirection.NORTH) 
						rowCol[0] = height-1;
					else if (directionSecondary == SnakeDirection.SOUTH) 
						rowCol[0] = 0;
					else if (directionSecondary == SnakeDirection.EAST) 
						rowCol[1] = 0;
					else if (directionSecondary == SnakeDirection.WEST) 
						rowCol[1] = width-1;
				} else {
					directionSecondary = flip(directionSecondary);
				}
			}
		}
		return sb.toString().replaceAll(" ", "");
	}

	/**
	 * 2-line thick version. if outputBigrams is true, output bigrams as we go.
	 * otherwise, output characters from one line at a time.
	 */
	public static String transform2Thick(String input, int corner, boolean directionRow, boolean outputBigrams, int width, boolean reverse) {
		StringBuilder sb = new StringBuilder();
		int height = input.length() / width;
		for (int i=0; i<input.length(); i++) {
			sb.append(" ");
		}
		int[] rowCol = new int[2];
		SnakeDirection directionPrimary = null;
		SnakeDirection directionSecondary = null;
		if (corner == 0) {
			rowCol = new int[] {0,0};
			if (directionRow) {
				directionPrimary = SnakeDirection.SOUTH;
				directionSecondary = SnakeDirection.EAST;
			}
			else {
				directionPrimary = SnakeDirection.EAST;
				directionSecondary = SnakeDirection.SOUTH;
			}
		} else if (corner == 1) {
			rowCol = new int[] {0,width-1};
			if (directionRow) {
				directionPrimary = SnakeDirection.SOUTH;
				directionSecondary = SnakeDirection.WEST;
			}
			else {
				directionPrimary = SnakeDirection.WEST;
				directionSecondary = SnakeDirection.SOUTH;
			}
		} else if (corner == 2) {
			rowCol = new int[] {height-1,0};
			if (directionRow) {
				directionPrimary = SnakeDirection.NORTH;
				directionSecondary = SnakeDirection.EAST;
			}
			else {
				directionPrimary = SnakeDirection.EAST;
				directionSecondary = SnakeDirection.NORTH;
			}
		} else if (corner == 3) {
			rowCol = new int[] {height-1,width-1};
			if (directionRow) {
				directionPrimary = SnakeDirection.NORTH;
				directionSecondary = SnakeDirection.WEST;
			}
			else {
				directionPrimary = SnakeDirection.WEST;
				directionSecondary = SnakeDirection.NORTH;
			}
		}
		int i=0;
		while (i<input.length()) {
			int posTransformed1 = rowCol[0]*width+rowCol[1];
			int posOriginal = i;
//			System.out.println("i " + i + " rowCol " + Arrays.toString(rowCol) + " pos " + pos + " primary " + directionPrimary + " secondary " + directionSecondary);
			if (reverse) {
				sb.setCharAt(posOriginal, input.charAt(posTransformed1));
			} else {
				sb.setCharAt(posTransformed1, input.charAt(posOriginal));
			}
			i++;
			
			// attempt to move.
			boolean result = move(rowCol, directionSecondary, height, width);

			// if failed, then 1) move in the primary direction, and flip the secondary direction
			if (!result) {
				move(rowCol, directionPrimary, height, width);
				directionSecondary = flip(directionSecondary);
			}
		}
		
		return sb.toString();
	}
	
	static SnakeDirection flip(SnakeDirection direction) {
		if (direction == SnakeDirection.NORTH) {
			return SnakeDirection.SOUTH;
		} else if (direction == SnakeDirection.SOUTH) {
			return SnakeDirection.NORTH;
		} else if (direction == SnakeDirection.EAST) {
			return SnakeDirection.WEST;
		}
		return SnakeDirection.EAST;
	}
	
	static boolean move(int[] rowCol, SnakeDirection direction, int height, int width) {
		int[] rowColNew = new int[] {rowCol[0], rowCol[1]};
		if (direction == SnakeDirection.NORTH) {
			rowColNew[0]--;
		} else if (direction == SnakeDirection.SOUTH) {
			rowColNew[0]++;
		} else if (direction == SnakeDirection.EAST) {
			rowColNew[1]++;
		} else if (direction == SnakeDirection.WEST) {
			rowColNew[1]--;
		}
		boolean result = true;
		if (rowColNew[0] < 0 || rowColNew[0] == height) {
			result = false;
		}
		if (rowColNew[1] < 0 || rowColNew[1] == width) {
			result = false;
		}
		if (result) {
			rowCol[0] = rowColNew[0];
			rowCol[1] = rowColNew[1];
		}
		return result;
	}
	
	public static void untransposeZ340() {
		String tab = "	";
		String cipher = Ciphers.Z340;
		for (int corner=0; corner<4; corner++) {
			for (int width=2; width<=170; width++) {
				for (boolean directionRow : new boolean[] {false, true}) {
					for (boolean reverse : new boolean[] {false, true}) {
						String untransposed = transform(cipher, corner, directionRow, width, false, reverse).replaceAll(" ", "");
						String reversed = transform(untransposed, corner, directionRow, width, false, !reverse).replaceAll(" ", "");
						if (!reversed.equals(cipher))
							throw new RuntimeException("NO MATCH " + corner + "	" + width + "	" + directionRow + "	" + reverse + "	" + untransposed + "	" + reversed);
						untransposed = untransposed.replaceAll(" ", "");
						double[] ngraphs = new double[5];
						for (int n=2; n<=6; n++) {
							ngraphs[n-2] = Stats.iocNgram(untransposed, n);
						}
						String iocs = "";
						for (double ngraph : ngraphs) {
							iocs += ngraph + tab;
						}
						System.out.println(corner + tab + width + tab + directionRow + tab + reverse + tab + iocs + "=(\"" + untransposed + "\")");
					}
				}
			}
		}
	}
	
	/** average ngraphic iocs for untransposed/transposed snakes for all widths in [2,L/2] */
	public static double[] iocsSnakesMean(String cipher, boolean doUntranspose) {
		double[] iocsSnakesMean = new double[3]; // 3 ngram sizes
		int iocsSnakesMeanCount = 0;
		for (int width=2; width<cipher.length()/2; width++) {
			for (int corner = 0; corner < 4; corner++) {
				for (boolean direction : new boolean[] {false, true}) {
					for (int n=2; n<5; n++) {
						String untransposed = Snake.transform(cipher, corner, direction, width, false, doUntranspose);
						iocsSnakesMean[n-2] += Stats.iocNgram(untransposed, n);
						iocsSnakesMeanCount++;
					}
				}
			}
		}
		for (int i=0; i<iocsSnakesMean.length; i++) iocsSnakesMean[i] = iocsSnakesMean[i] / iocsSnakesMeanCount;
		return iocsSnakesMean;
	}
	
	public static void main(String[] args) {
		test();
//		untransposeZ340();
	}

}
