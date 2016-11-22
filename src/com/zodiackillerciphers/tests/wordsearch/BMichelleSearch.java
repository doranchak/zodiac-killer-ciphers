package com.zodiackillerciphers.tests.wordsearch;

import java.util.ArrayList;
import java.util.List;

import com.zodiackillerciphers.dictionary.WordFrequencies;

/** omnidirectional anagram search */
public class BMichelleSearch {
	public static String[] grid = new String[] { "HITHEREMYNAMEISMI",
			"CHELLEIHAVEENJOYE", "DALLTHELINKSTHATA", "LWAYSLEADMEBACKTO",
			"THISSITESOITHOUGH", "TISHOULDJOINUPAND", "READUPONALLTHEINT",
			"ERSTINGTHINGSTHAT", "AREPOSTEDIENJOYRE", "ADINGPOSTSBUTTOBE",
			"HONESTIHAVENOTPOS", "TEDINMANYFORUMSIA", "MNEWATTHISSOPLEAS",
			"EEXCUSEANYERRORSI", "MAYMAKEATFIRSTREG", "ARDINGWHATIDOALSO",
			"IMUSTCONFESSICOUL", "DYOUASPELLCHECKCO", "NNECTEDTOMYTYPING",
			"HANDSBUTNOSUCHLUC" };

	public static void search(String word) {
		List<Character> letters = new ArrayList<Character>();
		for (int i = 0; i < word.length(); i++)
			letters.add(word.charAt(i));
		for (int row = 0; row < grid.length; row++) {
			for (int col = 0; col < grid[row].length(); col++) {
				boolean[][] visited = new boolean[grid.length][grid[0].length()];
				// System.out.println("Searching " + row + " " + col);
				if (search(visited, word, "", letters, row, col,
						new ArrayList<int[]>()))
					continue;
			}
		}
	}

	public static boolean search(boolean[][] visited, String word,
			String anagram, List<Character> letters, int row, int col,
			ArrayList<int[]> positions) {
		// we already visited this spot or we are out of bounds
		if (row < 0)
			return false;
		if (row == grid.length)
			return false;
		if (col < 0)
			return false;
		if (col == grid[row].length())
			return false;
		if (visited[row][col])
			return false;

		// we reached the end of the letters (success)
		if (letters.isEmpty()) {
			System.out.println(word.length() + "," + WordFrequencies.freq(word)
					+ "," + word + "," + anagram + "," + dump(positions));
			return true;
		}

		// letter at this spot does match
		char ch = grid[row].charAt(col);
		if (letters.contains(ch)) {
			for (int i = letters.size() - 1; i >= 0; i--) {
				if (letters.get(i) == ch) {
					letters.remove(i);
					// System.out.println(" - found " + ch + " at " + row + ","
					// + col);
					break;
				}
			}
			positions.add(new int[] { row, col });
		} else {
			// letter at this spot does not match
			return false;
		}

		// continue into neighbors
		boolean found = false;
		if (!found)
			found = search(visited, word, anagram + ch, letters, row - 1, col,
					positions);
		if (!found)
			found = search(visited, word, anagram + ch, letters, row - 1,
					col - 1, positions);
		if (!found)
			found = search(visited, word, anagram + ch, letters, row - 1,
					col + 1, positions);
		if (!found)
			found = search(visited, word, anagram + ch, letters, row, col - 1,
					positions);
		if (!found)
			found = search(visited, word, anagram + ch, letters, row, col + 1,
					positions);
		if (!found)
			found = search(visited, word, anagram + ch, letters, row + 1, col,
					positions);
		if (!found)
			found = search(visited, word, anagram + ch, letters, row + 1,
					col - 1, positions);
		if (!found)
			found = search(visited, word, anagram + ch, letters, row + 1,
					col + 1, positions);

		// put letter back in list and unmark visit
		letters.add(ch);
		visited[row][col] = false;
		positions.remove(positions.size() - 1);
		return found;
	}

	public static String dump(List<int[]> positions) {
		String s = "";
		if (positions == null)
			return s;
		for (int[] pos : positions)
			s += "(" + pos[0] + "," + pos[1] + ") ";
		return s;
	}

	public static void main(String[] args) {
		WordFrequencies.init();
		/*
		 * for (int L=20; L>=5; L--) { List<String> words =
		 * WordFrequencies.byLength.get(L); if (words != null) for (String word
		 * : words) { System.out.println(word); search(word); } }
		 */

		/*
		String[] pennwords = new String[] { "GARETH", "PENN", "SEWELL",
				"ZODIAC", "DETECTIVE", "CRIME", "KILLER", "BERKELEY", "CRYPTO",
				"RIFLE", "ECPHOR", "ECPHORIZE", "ECPHORIZER", "SANFRAN",
				"MENSA", "GOTTFRIED", "STRASSBURG", "INVISIBLE", "TRISTAN",
				"CELTIC", "HUGH", "HUGHSCOTT", "CRYPTOGRAPHER", "MURDER",
				"SECONDPOWER", "SECOND", "POWER", "PORTRAIT", "ARTIST", "MASS",
				"MURDERER", "MICHAEL", "OHARE", "HARE", "MIKE", "EUGENE", "HENRY",
				"STINE", "PAULSTINE", "PAUL", "WEBSTER", "JOAN", "LUCINDA",
				"JOANWEBSTER", "FBI", "JIGISUP", "FORTDIX", "VALLEJO", "ARMY",
				"RAYGRANT", "RAY", "GRANT", "BRYAN", "HARTNELL", "DARLENE",
				"FERRIN", "DAVID", "FARADAY", "BETTY", "BETTYLOU", "JENSEN",
				"DONNA", "LASS", "DONNALASS", "CHERI", "BATES", "CHERIJO", "SANDY",
				"SCOTT", "SANDYSCOTT", "GERMANY", "FULBRIGHT", "CHRISTIANE", "BERLIN",
				"KAFKA", "DRAFT", "CASTLE", "THECASTLE", "FORTSILL", "ARTILLERY",
				"SURVEYOR", "PATRICK", "HAYASHI", "GOLDMAN", "TUBACH", "FREDERIC", "FRITZ",
				"MONATSHEFTE", "GEODESIC","DOME", "DOMEHOUSE", "GEORGE", "OAKES", 
				"NAPA", "MARYANN", "MARY", "ANN","SOLANO", "WINTERROWD", "AMANDA",
				"RADIAN", "THEORY", "MOUNT", "DIABLO", "MTDIABLO", "BLUE", "ROCK", "SPRINGS","PARK",
				"WASHINGTON", "STREET", "HARVARD", "CALCULUS","EVIL", "LOVERS", "LANE",
				"PISTOL", "PROTRACTOR", "ANGLE", "PRESIDIO", "HEIGHTS", "CHERRY", "LAKE",
				"HERMAN", "ROAD", "DEGREES", "ROBERT", "GRAYSMITH","ARTHUR", "LEIGH", "ALLEN",
				"TERMINUS", "EVENT", "FOXGLOVE", "PRESS", "LAURENTIUS", "VALLA", "CHAMPOLLION",
				"BEDRICH", "HRONZY", "VENTRIS", "WIZARD"
				};
		for (String word: pennwords) {
			search(word);
		}*/
		search("SEWEL");
	}

}
