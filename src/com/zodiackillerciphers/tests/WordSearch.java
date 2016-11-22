package com.zodiackillerciphers.tests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.dictionary.WordFrequencies;
import com.zodiackillerciphers.names.Census;
import com.zodiackillerciphers.names.Name;

/**
 * use ambiguous interpretations of cipher symbols to convert them to letters of
 * the alphabet. then search for words in various directions, preferring longer
 * words that don't frequently change directions
 */
public class WordSearch {
	
	static int R;
	static int C;
	/*
	 * map of cipher symbols to list of ambiguous interpretations based solely
	 * on appearance. some rotation/mirroring of cipher symbols is permitted to
	 * reveal the interpretations.
	 */
	static Map<Character, String> translations;
	static Set<String> undesired; // track the less desired mappings
	static Set<String> undesiredMinor; // track the mappings that are slightly more acceptable than the undesired ones
	static char[][] cipherGrid; // cipher text converted to grid form
	static boolean[][] visited; // when doing word searches, track which spots
								// we've already visited

	static int MAX_CROSSES = 0; // do not cross more than this number of times (-1 = unlimited)
	static int MAX_DIRECTION_CHANGES = 10; // do not change directions more than this number of times. (-1 = unlimited)
	static int MIN_WORD_LENGTH = 3;
	static int MAX_CHEATS = 0; // max number of cheats allowed when looking for word pairs.
	static int JUMPS_ALLOWED = 3; // max number of jumps allowed (i.e., skipping symbols)
	static boolean ALLOW_DIAGONALS = false; // if true, then we can make diagonal moves.
	
	static {
		translations = new HashMap<Character, String>();
		translations.put('A', "A");
		translations.put('B', "B");
		translations.put('b', "B");
		translations.put('C', "CUVN");
		translations.put('c', "CUVN");
		translations.put('D', "D");
		translations.put('d', "D");
		translations.put('E', "EMW");
		translations.put('e', "EMW");
		translations.put('F', "F");
		translations.put('f', "F");
		translations.put('G', "G");
		translations.put('H', "HI");
		translations.put('I', "IH");
		translations.put('!', "IH");
		translations.put(':', "IH");
		translations.put(';', "IH");
		translations.put('J', "JY");
		translations.put('j', "JY");
		translations.put('K', "K");
		translations.put('k', "K");
		translations.put('=', "K");
		translations.put('L', "L");
		translations.put('l', "L");
		translations.put('M', "MWE");
		translations.put('N', "NZ");
		translations.put('O', "O");
		translations.put('P', "PDBQ");
		translations.put('p', "PDBQ");
		translations.put('&', "PDBQ");
		translations.put('Q', "Q");
		translations.put('q', "Q");
		translations.put('R', "R");
		translations.put('r', "R");
		translations.put('S', "S");
		translations.put('T', "T");
		translations.put('t', "T");
		translations.put('U', "UN");
		translations.put('V', "VL");
		translations.put('W', "WEM");
		translations.put('X', "XT");
		translations.put('Y', "YT");
		translations.put('y', "YT");
		translations.put('Z', "ZN");
		translations.put('z', "OTZ");
		translations.put('(', "OI");
		translations.put(')', "OI");
		translations.put('1', "O");
		translations.put('2', "O");
		translations.put('3', "O");
		translations.put('4', "O");
		translations.put('5', "O");
		translations.put('6', "O");
		translations.put('_', "O");
		translations.put('#', "O");
		translations.put('%', "O");
		translations.put('*', "O");
		translations.put('@', "O");
		translations.put('7', "AD");
		translations.put('8', "AD");
		translations.put('9', "AD");
		translations.put('+', "TX");
		translations.put('-', "I");
		translations.put('.', "O");
		translations.put('/', "I");
		translations.put('<', "VL");
		translations.put('>', "VL");
		translations.put('\\', "I");
		translations.put('^', "VLA");
		translations.put('|', "I");
		translations.put('0', "R");
		translations.put('[', "RTY");
		translations.put('?', "NUV");

		undesired = new HashSet<String>();
		undesired.add("!H");
		//undesired.add("#O");
		//undesired.add("%O");
		undesired.add("&B");
		undesired.add("&D");
		undesired.add("&Q");
		undesired.add("(I");
		//undesired.add("(O");
		undesired.add(")I");
		//undesired.add(")O");
		//undesired.add("*O");
		undesired.add("+X");
		undesired.add("-I");
		undesired.add(".O");
		undesired.add("/I");
		//undesired.add("7A");
		undesired.add("7D");
		//undesired.add("8A");
		undesired.add("8D");
		//undesired.add("9A");
		undesired.add("9D");
		undesired.add(":H");
		undesired.add(";H");
		undesired.add("<L");
		undesired.add("<V");
		undesired.add("=K");
		undesired.add(">L");
		undesired.add(">V");
		//undesired.add("@O");
		undesired.add("CN");
		undesired.add("CU");
		undesired.add("CV");
		undesired.add("EM");
		undesired.add("EW");
		undesired.add("HI");
		undesired.add("IH");
		undesired.add("JY");
		undesired.add("ME");
		undesired.add("MW");
		undesired.add("NZ");
		undesired.add("PB");
		undesired.add("PD");
		undesired.add("PQ");
		undesired.add("UN");
		undesired.add("VL");
		undesired.add("WE");
		undesired.add("WM");
		undesired.add("XT");
		undesired.add("YT");
		undesired.add("ZN");
		//undesired.add("\\I");
		undesired.add("^L");
		//undesired.add("^V");
		//undesired.add("_O");
		undesired.add("cN");
		undesired.add("cU");
		undesired.add("cV");
		undesired.add("eM");
		undesired.add("eW");
		undesired.add("jY");
		undesired.add("pB");
		undesired.add("pD");
		//undesired.add("pQ");
		undesired.add("yT");
		//undesired.add("zO");
		//undesired.add("zT");
		undesired.add("zZ");
		
		undesiredMinor = new HashSet<String>();
		undesiredMinor.add("%O");
		undesiredMinor.add("*O");
		undesiredMinor.add("@O");
		undesiredMinor.add("#O");
		undesiredMinor.add("_O");
		undesiredMinor.add("^V");
		undesiredMinor.add("^A");
		undesiredMinor.add("zT");		
		
	}

	// map directions to row and column increments
	static Map<Integer, Integer> drow;
	static Map<Integer, Integer> dcol;
	static {
		drow = new HashMap<Integer, Integer>();
		drow.put(0, -1); // up left
		drow.put(1, -1); // up
		drow.put(2, -1); // up right
		drow.put(3, 0); // right
		drow.put(4, 1); // right down
		drow.put(5, 1); // down
		drow.put(6, 1); // down left
		drow.put(7, 0); // left

		dcol = new HashMap<Integer, Integer>();
		dcol.put(0, -1); // up left
		dcol.put(1, 0); // up
		dcol.put(2, 1); // up right
		dcol.put(3, 1); // right
		dcol.put(4, 1); // right down
		dcol.put(5, 0); // down
		dcol.put(6, -1); // down left
		dcol.put(7, -1); // left
		
		// extra directions
		drow.put(8, -2);
		drow.put(9, -2);
		drow.put(10, -2);
		drow.put(11, -2);
		drow.put(12, -2);
		drow.put(13, -1);
		drow.put(14, -1);
		drow.put(15, 0);
		drow.put(16, 0);
		drow.put(17, 1);
		drow.put(18, 1);
		drow.put(19, 2);
		drow.put(20, 2);
		drow.put(21, 2);
		drow.put(22, 2);
		drow.put(23, 2);
		
		dcol.put(8, -2);
		dcol.put(9, -1);
		dcol.put(10, 0);
		dcol.put(11, 1);
		dcol.put(12, 2);
		dcol.put(13, -2);
		dcol.put(14, 2);
		dcol.put(15, -2);
		dcol.put(16, 2);
		dcol.put(17, -2);
		dcol.put(18, 2);
		dcol.put(19, -2);
		dcol.put(20, -1);
		dcol.put(21, 0);
		dcol.put(22, 1);
		dcol.put(23, 2);

	}
	
	static Map<Character, String> key408Map;
	static {
		key408Map = new HashMap<Character, String>();
		key408Map.put('A', "8GSl");
		key408Map.put('B', "V");
		key408Map.put('C', "e");
		key408Map.put('D', "fz");
		key408Map.put('E', "+6ENWZp");
		key408Map.put('F', "JQ");
		key408Map.put('G', "R");
		key408Map.put('H', ")M");
		key408Map.put('I', "9PUk");
		key408Map.put('K', "/");
		key408Map.put('L', "#%B");
		key408Map.put('M', "q");
		key408Map.put('N', "(DO^");
		key408Map.put('O', "!TXd");
		key408Map.put('P', "=");
		key408Map.put('R', "\\rt");
		key408Map.put('S', "7@FK");
		key408Map.put('T', "5HIL");
		key408Map.put('U', "Y");
		key408Map.put('V', "c");
		key408Map.put('W', "A");
		key408Map.put('X', "j");
		key408Map.put('Y', "_");
	}

	static void resetTranslations() {
		translations.clear();
		translations.put('A', "A");
		translations.put('B', "B");
		translations.put('C', "C");
		translations.put('D', "D");
		translations.put('E', "E");
		translations.put('F', "F");
		translations.put('G', "G");
		translations.put('H', "H");
		translations.put('I', "I");
		translations.put('J', "J");
		translations.put('K', "K");
		translations.put('L', "L");
		translations.put('M', "M");
		translations.put('N', "N");
		translations.put('O', "O");
		translations.put('P', "P");
		translations.put('Q', "Q");
		translations.put('R', "R");
		translations.put('S', "S");
		translations.put('T', "T");
		translations.put('U', "U");
		translations.put('V', "V");
		translations.put('W', "W");
		translations.put('X', "X");
		translations.put('Y', "Y");
		translations.put('Z', "Z");
		
	}
	static void init() {
		WordFrequencies.init();
		
		R=48;
		C=17;
		
		String z408c = "ILIKEKILLINGPEOPL" +
				"9%P/Z/UB%kOR=pX=B" +
				"EBECAUSEITISSOMUC" +
				"WV+eGYF69HP@K!qYe"  +
				"HFUNITISMOREFUNTH" +
				"MJY^UIk7qTtNQYD5)"  +
				"ANKILLINGWILDGAME" +
				"S(/9#BPORAU%fRlqE"  +
				"INTHEFORRESTBECAU" +
				"k^LMZJdr\\pFHVWe8Y" +
				"SEMANISTHEMOSTDAN" +
				"@+qGD9KI)6qX85zS(" +
				"GEROUSANIMALOFALL" +
				"RNtIYElO8qGBTQS#B" +
				"TOKILLSOMETHINGGI" +
				"Ld/P#B@XqEHMU^RRk" +
				"VESMETHEMOSTTHRIL" +
				"cZKqpI)Wq!85LMr9#" +
				"LINGEXPERENCEITIS" +
				"BPDR+j=6\\N(eEUHkF" +
				"EVENBETTERTHANGET" +
				"ZcpOVWI5+tL)l^R6H" +
				"TINGYOURROCKSOFFW" +
				"I9DR_TYr\\de/@XJQA" +
				"ITHAGIRLTHEBESTPA" +
				"P5M8RUt%L)NVEKH=G" +
				"RTOFITIATHATWHENI" +
				"rI!Jk598LMlNA)Z(P" +
				"DIEIWILLBEREBORNI" +
				"zUpkA9#BVW\\+VTtOP" +
				"NPARADICEANDALLTH" +
				"^=SrlfUe67DzG%%IM" +
				"EIHAVEKILLEDWILLB" +
				"Nk)ScE/9%%ZfAP#BV" +
				"ECOMEMYSLAVESIWIL" +
				"peXqWq_F#8c+@9A9B" +
				"LNOTGIVEYOUMYNAME" +
				"%OT5RUc+_dYq_^SqW" +
				"BECAUSEYOUWILLTRY" +
				"VZeGYKE_TYA9%#Lt_" +
				"TOSLOWDOWNORSTOPM" +
				"H!FBX9zXADd\\7L!=q" +
				"YCOLLECTINGOFSLAV" +
				"_ed##6e5PORXQF%Gc" +
				"ESFORMYAFTERLIFEE" +
				"Z@JTtq_8JI+rBPQW6" +
				"BEORIETEMETHHPITI" +
				"VEXr9WI6qEHM)=UIk";

		String z408s = "ILIKEKILLINGPEOPL" + 
				"EBECAUSEITISSOMUC" + 
				"HFUNITISMOREFUNTH" + 
				"ANKILLINGWILDGAME" + 
				"INTHEFORRESTBECAU" + 
				"SEMANISTHEMOSTDAN" + 
				"GEROUSANIMALOFALL" + 
				"TOKILLSOMETHINGGI" + 
				"VESMETHEMOSTTHRIL" + 
				"LINGEXPERENCEITIS" + 
				"EVENBETTERTHANGET" + 
				"TINGYOURROCKSOFFW" + 
				"ITHAGIRLTHEBESTPA" + 
				"RTOFITIATHATWHENI" + 
				"DIEIWILLBEREBORNI" + 
				"NPARADICEANDALLTH" + 
				"EIHAVEKILLEDWILLB" + 
				"ECOMEMYSLAVESIWIL" + 
				"LNOTGIVEYOUMYNAME" + 
				"BECAUSEYOUWILLTRY" + 
				"TOSLOWDOWNORSTOPM" + 
				"YCOLLECTINGOFSLAV" + 
				"ESFORMYAFTERLIFEE" + 
				"BEORIETEMETHHPITI";

		String ak = "YESATTHEOLDSITETH" + 
				"EMAINTKPOSTRANPAS" + 
				"TPAGESSOTOHAVESOM" + 
				"EOFTHEMOSTIMPORTA" + 
				"NTANDKEYINFOHEREI" + 
				"NONEORTWOPAGESISN" + 
				"ICEHEREARESOMEVER" + 
				"TICALWORDFINDSDIS" + 
				"COVEREDINTHEZODIA" + 
				"CBYDOUGOSWELLZAND" + 
				"ERKITEANDMYSELFOF" + 
				"COURSEALLOFTHESEC" + 
				"OULDBERANDOMWHITE" + 
				"NOISEBUTTHEYDOALL" + 
				"OCCURINAFAIRLYCON" + 
				"CENTRATEDAREAONOR" + 
				"NEARTHEWORDPARADI" + 
				"CEMOSTBETWEENSIXL" + 
				"INESANDBETWEENCOL" + 
				"UMNSANDTHESEINCLU" + 
				"DETEDTEASEHEALEAR" + 
				"NANDTHEHALLOWEENC" + 
				"ARDINTRODUCESTHEI" + 
				"DEAOFPARADICEINTE";

		
		String opord = "TEGKEANBIIAYTOARO" + 
				"EEELNLNLNEURHRSDH" + 
				"LLJVHEIEAENHTLDTF" + 
				"AEEANASRBNENEEGSR" + 
				"YIHEDRXOATSIEPOIK" + 
				"ESGNSANFROCISKRNN" + 
				"LTESML RIVOABRDTA" + 
				"NARFDXLNEYEUDESTI" + 
				"OHHEBEEGRSLVLWEOS" + 
				"FDAIBENFENEAHSLLF" + 
				"IEGKTREANULEBOHSN" + 
				"RHVAGFARTOOHREPLS" + 
				"DEIALLEEATSLVN AG" + 
				"AASENDTROBRVBOREE" + 
				"LLOYRJOICEKBIETFE" + 
				"AVRNDLSNNENRDHMAR" + 
				"GVOETYECHESAELAHT" + 
				"EEHECDEVINATOKNHE" + 
				"ASIOEHARLYUNLRFCV" + 
				"KHNTEEIADENSWAS E";
		
		String citizen = "DEAR SERGEANT                                   "+                       
				"   I HOPE THE ENCLOSED  KEY  WILL PROVE TO BE   "+                       
				"BENEFICIAL TO YOU IN CONNECTION WITH THE CIPHER "+                         
				"LETTER WRITER.                                  "+                  
				"                                                "+                 
				"   WORKING PUZZLES CRIPTOGRAMS AND WORD PUZZLES "+                         
				" IS ONE OF MY PLEASURES.  PLEASE FORGIVE THE    "+                      
				" ABSENCE OF MY SIGNATURE OR NAME AS I DO NOT    "+                      
				" WISH TO HAVE MY NAME IN THE PAPERS AND IT COULD"+                          
				" BE MENTIOND BY A SLIP OF THE TONGUE.           "+               
				"                                                "+    
				"    WITH BEST WISHES.                           "+            
				"                      CONCERNED CITIZEN         ";


		cipherGrid = new char[R][C];
		visited = new boolean[R][C];
		//String cipher = Ciphers.cipher[0].cipher;  // 340 cipher
		//String cipher = Ciphers.cipherByDescription("BTK word search puzzle").cipher; // R=38, C=9
		String cipher = z408c;
		
		//String cipher = "NOIDONTTHINKSOIBELIEVETHISSOLUTIONHELPSEXPLAINSTHEPOETSPENSOLUTIONANDTHEIDEATHEYHADINMINEBEHINDITSCONSTRUCTIONKEEPINMINETHATTHEPOETSPENISSIXTYEIGHTCHARACTERSANDPENNSTATESINHISTIMESSEVENTEENTHATSIXTYEIGHTISMORSEBINARYFORNINENOLONGDRAGGEDOUTEXPLANATIONSTRAIGHTTOTHEPOSSIBLESOLUTIONANDWHATIBELIEVECANTHAPPENBYCHANCETHEZTHREEFORTYANDTHEMYNAMEISCIPHERISAWORDGAMEANDPUZZLESOMETHINGLIKEACROSTICSANDSECRETWRITINGHIDINGINASHORTSUBSTITUTIONCIPHERTHEPOETSPENROYALTUTORSETCONTOPANDTHEZTHREEFORTYFLIPPEDVERTICALLYISTHERESTOFTHESUBSTITUTIONCIPHERATTHEBOTTOMTHEPLAINTEXTSTARTSAROUNDTHELASTSIXLINESWHATRUNSTHROUGHTHEPLAINTEXTDIAGONALLYISAMAZING ";
		
		
		
		
		//String cipher = Ciphers.shuffle(Ciphers.cipher[0].cipher);
		
		//System.out.println("Searching cipher " + cipher);
		
		//String cipher = Ciphers.cipher[1].cipher;  // 408 cipher
		//String cipher = Ciphers.cipher[1].solution.toUpperCase();  // 408 cipher solution
		//resetTranslations();

		// random shuffle of the 340
		//String cipher = "TLCtBOMD>F+O2HODUyMl^#;+2VbGRPU*2OG(+(cFTfzOzBW4;RSPpl.BLTtUfF5p(BS+t5/|EB+kfzz;)C)(d__EFyAbNlyJDNBRc<+JH|z9pcN&t54(*pc2GWL#H@-7-FZ<M<V#GK|GB4c7L(#K*4l8FV1>lqK|U+cJCOpPYOd6KZkVOzY+1b5+KB*SWF9++4fkdCy5C.3.%|6+/9c+B|c8+HpB^%d2Y+:.Rlz8N_3.F^+zZ:D-2O|GXl+jWS+*<K+2z>k7U(TMEZ)T<p)cyR8+2Rp5>XKFFMBL2L<RW)cNYRkA|+p^-+Wp|p/M*&|J56MOV^4j-9+#d^.VqB1+";
		
		for (int i = 0; i < cipher.length(); i++) {
			int row = i / C;
			int col = i % C;
			cipherGrid[row][col] = cipher.charAt(i);
			visited[row][col] = false;
		}

		/*
		 * for (int row=0; row<cipherGrid.length; row++) { String r = ""; for
		 * (int col=0; col<cipherGrid[row].length; col++) { r +=
		 * cipherGrid[row][col]; } System.out.println(r); }
		 */

	}

	/** search for the given word in the grid.  if searchWord is null, then dump all found words. */
	static void search(String searchWord) {
		init();
		
		//for (Character key : translations.keySet()) System.out.println(key + ":" + translations.get(key));

		for (int row = 0; row < cipherGrid.length; row++) {
			for (int col = 0; col < cipherGrid[row].length; col++) {
				search(searchWord, new StringBuffer(), 0, 0, row, col, row, col, 
						new ArrayList<int[]>(), 0, 0, JUMPS_ALLOWED);
			}
		}
	}

	static int dchange(int olddirection, int newdirection) {
		if (olddirection == newdirection)
			return 0;
		if (olddirection == -1)
			return 0;
		return 1;
	}

	static void search(String searchWord, StringBuffer word, int direction, int directionChanges,
			int row, int col, int rowOld, int colOld, List<int[]> path, int crossCount,
			int undesiredCount, int jumpsAllowed) {

		//System.out.println("search: " + word + ", " + word.toString().equals(searchWord) + ", " + word + ", " + direction + ", " + directionChanges + ", r" + row + ", c" + col);
		if (jumpsAllowed < 0) return;
		int oldrow = row;
		int oldcol = col;
		// bounds checking
		row = (row + cipherGrid.length) % cipherGrid.length;
		col = (col + cipherGrid[row].length) % cipherGrid[row].length;

		
		// abort if too many direction changes
		if (MAX_DIRECTION_CHANGES > -1 && directionChanges > MAX_DIRECTION_CHANGES)
			return;
		
		boolean undoCrossCount = false;
		
		// special case: we usually jump a line when reading left to right and wrapping around
		boolean ignoreCross = (row == oldrow + 1 && col == 0 && oldcol == cipherGrid[0].length-1) ||
				(oldrow == row + 1 && oldcol == 0 && col == cipherGrid[0].length-1);
		if (!ignoreCross && (oldrow != row || oldcol != col)) {
			crossCount++;
			undoCrossCount = true;
		}
		// abort if too many crosses
		if (MAX_CROSSES > -1 && crossCount > MAX_CROSSES)
			return;

		// abort if we've already visited this location
		if (visited[row][col])
			return;

		
		// abort if we're going diagonally and diagonal moves are not allowed
		if (!ALLOW_DIAGONALS) {
			int diffR = Math.abs(row - rowOld);
			int diffC = Math.abs(col - colOld);
			// at least one must be zero
			
			//System.out.println("old " + rowOld + " " + colOld + ", new " + row + " " + col + ", " + word);
			if (diffR == 0 || diffC == 0) {
				;
			} else return;
			
		}
		
		
		// look at current cipher
		char ch = cipherGrid[row][col];
		// for each corresponding interpreted letter:
		String letters = translations.get(ch);
		if (letters == null) {
			//System.err.println("[" + ch + "]");
		} else {
			for (int a = 0; a < letters.length(); a++) {
				char letter = letters.charAt(a);
				// add letter to candidate word, add to path, and mark location as
				// visited.
				
				if (searchWord != null) {
					//System.out.println("[" + searchWord + "] [" + word + "] [" + a + "]");
					String newWord = word.toString() + letter;
					if (!searchWord.toUpperCase().startsWith(newWord.toUpperCase()))
						continue; // ignore everything other than the given search word
				}
				
				word.append(letter);
				
				
				
				path.add(new int[] { row, col });
				visited[row][col] = true;

				String undKey = "" + ch + letter;
				boolean undoUndesiredCount = false;
				if (undesired.contains(undKey)) {
					undesiredCount++;
					undoUndesiredCount = true;
				}

				if (searchWord != null || WordFrequencies.hasPrefix(word.toString())) {
					// prefix is part of valid word(s), so:
					// 1) is the prefix itself a word? if so, dump it.
					boolean wordMatch = searchWord != null && word.length() == searchWord.length();// && word.length() >= MIN_WORD_LENGTH; // permit partial matches to the search word 
					if (wordMatch)// || word.length() >= MIN_WORD_LENGTH
							//&& WordFrequencies.hasWord(word.toString()))
						dump(word.toString(), directionChanges, path, crossCount, undesiredCount);
					// 2) keep searching recursively.
					int newd;
					// up left
					
					newd = 0;
					if (word.length() == 1)
						direction = -1; // there's no old direction yet
					search(searchWord, word, newd, directionChanges + dchange(direction, newd),
							row + drow.get(newd), col + dcol.get(newd), row, col, path,
							crossCount, undesiredCount, jumpsAllowed);
					newd = 1;
					search(searchWord, word, newd, directionChanges + dchange(direction, newd),
							row + drow.get(newd), col + dcol.get(newd), row, col, path,
							crossCount, undesiredCount, jumpsAllowed);
					newd = 2;
					search(searchWord, word, newd, directionChanges + dchange(direction, newd),
							row + drow.get(newd), col + dcol.get(newd), row, col, path,
							crossCount, undesiredCount, jumpsAllowed);
					newd = 3;
					search(searchWord, word, newd, directionChanges + dchange(direction, newd),
							row + drow.get(newd), col + dcol.get(newd), row, col, path,
							crossCount, undesiredCount, jumpsAllowed);
					newd = 4;
					search(searchWord, word, newd, directionChanges + dchange(direction, newd),
							row + drow.get(newd), col + dcol.get(newd), row, col, path,
							crossCount, undesiredCount, jumpsAllowed);
					newd = 5;
					search(searchWord, word, newd, directionChanges + dchange(direction, newd),
							row + drow.get(newd), col + dcol.get(newd), row, col, path,
							crossCount, undesiredCount, jumpsAllowed);
					newd = 6;
					search(searchWord, word, newd, directionChanges + dchange(direction, newd),
							row + drow.get(newd), col + dcol.get(newd), row, col, path,
							crossCount, undesiredCount, jumpsAllowed);
					newd = 7;
					search(searchWord, word, newd, directionChanges + dchange(direction, newd),
							row + drow.get(newd), col + dcol.get(newd), row, col, path,
							crossCount, undesiredCount, jumpsAllowed);
					
					jumpsAllowed--;
					for (newd = 8; newd<=23; newd++) {
						search(searchWord, word, newd, directionChanges + dchange(direction, newd),
								row + drow.get(newd), col + dcol.get(newd), row, col, path,
								crossCount, undesiredCount, jumpsAllowed);
					}
				}

				// done searching from this location, so revert state
				word.deleteCharAt(word.length() - 1);
				path.remove(path.size() - 1);
				visited[row][col] = false;
				if (undoUndesiredCount)
					undesiredCount--;

			}
		}
		if (undoCrossCount)
			crossCount--;

	}

	// fields:
	// 1) word length
	// 2) number of times word crosses cipher grid boundaries
	// 3) number of undesirable interpretations
	// 4) number of direction changes
	// 5) composite score
	// 6) word
	// 7) relative frequency in english
	// 8) path (locations in cipher grid)
	// 9) "true" if normal left-to-right word
	// 10) webtoy highlighter
	static void dump(String word, int directionChanges, List<int[]> path,
			int crossCount, int undesiredCount) {
		
		if (word.length() < MIN_WORD_LENGTH) return;
		if (MAX_DIRECTION_CHANGES > -1 && directionChanges > MAX_DIRECTION_CHANGES) return;
		if (MAX_CROSSES > -1 && crossCount > MAX_CROSSES) return;
		if (MAX_CHEATS > -1 && undesiredCount > MAX_CHEATS) return;
		
		float score = ((float)word.length()) / (1+crossCount+undesiredCount+directionChanges);
		StringBuffer line = new StringBuffer();
		int freq = WordFrequencies.freq(word);
		line.append(word.length()).append(", ").append(crossCount).append(", ")
				.append(undesiredCount).append(", ").append(directionChanges).append(", ")
				.append(score);
		line.append(", ").append(word).append(", ").append(freq).append(", ").
				append(toString(path));
		line.append(", ").append(normalWord(path)).append(", ").append(darken(path, word));


		//if (word.equals("IDOB") && score >= 2.0) 
		System.out.println(line);
	}
	/** returns true only if the path represents a normal left-to-right reading of a word.
	 * helpful for excluding known words in a word search puzzle that is already based on 
	 * plain text. */
	static boolean normalWord(List<int[]> path) {
		if (path == null) return false;
		if (path.size() == 1) return true;
		
		for (int i=1; i<path.size(); i++) {
			int[] rcCurr = path.get(i);
			int[] rcPrev = path.get(i-1);
			if (rcCurr[0] == rcPrev[0] && rcCurr[1] == rcPrev[1] + 1) continue; // next letter of word is on the same line in the next column.
			if (rcCurr[1] == 0 && rcCurr[0] == rcPrev[0] + 1 && rcPrev[1] == C-1) continue; // next letter of word is at beginning of next line;
			return false;
		}
		return true;
	}

	static String darken(List<int[]> path, String word) {
		String line = "showWord([";
		for (int i = 0; i < path.size(); i++) {
			int[] rc = path.get(i);
			if (i > 0)
				line += ", ";
			line += "[" + rc[0] + ", " + rc[1] + "]";
		}
		line += "], \"" + word + "\");";
		return line;
	}

	static float score(String word, int directionChanges, int freq) {
		// maximize freq
		// minimize directionChanges
		// we'll separate out by length later
		float score = ((float) freq) / (directionChanges + 1);
		return score;
	}

	static String toString(List<int[]> path) {
		StringBuffer sb = new StringBuffer();
		for (int[] rc : path) {
			sb.append("(" + rc[0] + "," + rc[1] + ") ");
		}
		return sb.toString();
	}
	
	static void searchWikiWords() {
		String[] words = new String[] {
				"A", "ABANDONED", "ABDUCTED", "ABDUCTION", "ABDUCTOR", "ABLE", "ABOUT", "ABOUTCOM", "ACCOMPANYING", 
				"ACCORDING", "ACCOUNT", "ACCOUNTS", "ACROSS", "ACTIVITIES", "ACTIVITY", "ACTUALLY", "AD", "ADAMS", 
				"ADDED", "ADDING", "ADDITIONAL", "ADDRESS", "ADDRESSCITATION", "ADDRESSED", "ADMITTED", "ADULT", 
				"ADVANCED", "ADVERTISEMENT", "ADVERTISEMENTS", "AFTER", "AFTERLIFE", "AGAIN", "AGENCIES", "AGES", 
				"AGREE", "AGREED", "AIRING", "AKA", "ALERTED", "ALERTING", "ALIVE", "ALL", "ALLEGED", "ALLEGEDLY", 
				"ALLEN", "ALLENS", "ALMOST", "ALONG", "ALONGSIDE", "ALPHABET", "ALSO", "ALSOEDIT", "ALTHOUGH", 
				"AM", "AMATEUR", "AMAZING", "AMBULANCE", "AMERICAN", "AMERICAS", "AMWCOM", "AN", "ANALYSIS", 
				"ANALYSISDEAD", "AND", "ANGELES", "ANGLE", "ANN", "ANNEX", "ANONYMOUS", "ANOTHER", "ANY", 
				"APPARENTLY", "APPEALED", "APPEAR", "APPEARANCE", "APPEARED", "APPEARING", "APPEARS", "APPROACHED", 
				"APPROACHING", "APPROXIMATELY", "APRIL", "ARCHIVED", "ARE", "AREA", "AREAS", "ARGUED", "ARGUES", 
				"ARMISTEAD", "ARMSTRONG", "ARMY", "AROUND", "ARRIVAL", "ARRIVE", "ARRIVED", "ARTHUR", "ARTICLE", 
				"ARTIST", "AS", "ASKED", "ASSAILANT", "ASSIGNED", "ASSISTANT", "ASSOCIATE", "ASSOCIATED", 
				"ASSUMED", "AT", "ATTACK", "ATTACKEDIT", "ATTACKER", "ATTACKS", "ATTEMPTED", "ATTEND", "ATTORNEY", 
				"ATTORNEYS", "ATTRIBUTED", "AUGUST", "AUTHENTIC", "AUTHENTICATED", "AUTHENTICITY", "AUTHOR", 
				"AUTHORED", "AUTHORITIES", "AUTHORSHIP", "AUTHORSHIPEDIT", "AVAILABLE", "AVENGER", "AVERLY", 
				"AVERY", "AVERYEDIT", "AVERYS", "AWAY", "B", "BACK", "BACKROADS", "BADGE", "BADLANDS", "BAILEY", 
				"BAKER", "BANANA", "BARBARA", "BARTO", "BASED", "BATES", "BATESS", "BAY", "BE", "BEACH", "BEATEN", 
				"BECAUSE", "BEEN", "BEFORE", "BEGAN", "BEHIND", "BEING", "BELIEVE", "BELIEVED", "BELLI", "BENEATH", 
				"BENICIA", "BERKLEY", "BERNARDINO", "BERRYESSA", "BESIDE", "BEST", "BETTY", "BETTYE", "BETWEEN", 
				"BIBLIKE", "BILL", "BIND", "BLACK", "BLAST", "BLOCK", "BLOCKS", "BLOOD", "BLOODSTAINED", "BLOODY", 
				"BLOW", "BLUE", "BLURRY", "BOAST", "BODIES", "BODY", "BOMB", "BOMBING", "BONDS", "BOOK", "BOOKS", 
				"BOOTH", "BORGES", "BOTH", "BOTTOM", "BOUNCING", "BOUND", "BOXES", "BRAND", "BRENDA", "BRIAN", 
				"BRIEFLY", "BRITISH", "BROUGHT", "BRUTALLY", "BRYAN", "BULLETS", "BURIED", "BURN", "BUS", "BUT", 
				"BUTONS", "BUTTON", "BUTTONS", "BY", "C", "CA", "CAB", "CABDRIVER", "CALIBER", "CALIFORNIA", 
				"CALIFORNIAS", "CALL", "CALLED", "CALLER", "CALLS", "CALVIN", "CAME", "CAMPUS", "CAN", "CANNOT", 
				"CAP", "CAPSTONE", "CAR", "CARD", "CARDS", "CARROLL", "CARRYING", "CARSON", "CARVED", "CASE", 
				"CASELOAD", "CASES", "CASINO", "CATEGORIES", "CAUSE", "CAUSED", "CBS", "CECELIA", "CELTIC", 
				"CERTIFICATE", "CHANGE", "CHANNEL", "CHAPTER", "CHARACTER", "CHARACTERISTICS", "CHARACTERS", 
				"CHARGES", "CHARLES", "CHARLIE", "CHECKED", "CHENEY", "CHERI", "CHERRY", "CHEST", "CHIEF", "CHILD", 
				"CHP", "CHRIS", "CHRISTMAS", "CHRONICLE", "CID", "CIPHER", "CIPHERS", "CIRCLE", "CIRCUMSTANCES", 
				"CIRCUMSTANTIAL", "CITING", "CITIZEN", "CITY", "CLAIM", "CLAIMED", "CLAIMING", "CLAIMS", "CLAIR", 
				"CLEAR", "CLEARED", "CLIENTS", "CLIMBED", "CLIPON", "CLOSE", "CLOSED", "CLOSELY", "CLOTHESLINE", 
				"CLUB", "CLUE", "CLUES", "CNN", "CODE", "CODED", "CODES", "COED", "COLLAGE", "COLLECTING", 
				"COLLECTION", "COLLEGE", "COLLINS", "COLUMNISTS", "COMA", "COME", "COMIDY", "COMMITTED", "COMMONS", 
				"COMMUNICATE", "COMMUNICATIONS", "COMMUNICATIONSCITATION", "COMMUNICATIONSEDIT", "COMPARED", 
				"COMPARISON", "COMPLAINING", "COMPLAINT", "COMPOSITE", "CONCEDE", "CONCERNS", "CONCERT", 
				"CONCLUDED", "CONCLUSIVE", "CONDOMINIUMS", "CONDUCTED", "CONFESSED", "CONFESSION", 
				"CONFIDENTIALITY", "CONFIRMED", "CONFIRMEDCITATION", "CONJUNCTION", "CONNECT", "CONNECTED", 
				"CONNECTION", "CONSCIOUS", "CONSCIOUSNESS", "CONSIDERED", "CONSISTING", "CONSPIRACY", "CONT", 
				"CONTACTED", "CONTACTING", "CONTAINED", "CONTAINING", "CONTENTS", "CONTINUED", "CONTRIBUTORS", 
				"CONVICT", "CONVINCE", "COORDINATES", "COP", "COPIES", "COPY", "CORRESPONDENCE", "CORROBORATING", 
				"COULD", "COUNTERCULTURE", "COUNTY", "COUPLE", "COURT", "COVE", "COVERING", "COVERUP", "CRACKED", 
				"CREDIT", "CREDITED", "CRIME", "CRIMES", "CRIMESCITATION", "CRIMINAL", "CROSS", "CROSSCIRCLE", 
				"CROSSED", "CROSSEDCIRCLE", "CRUSE", "CRYPTOGRAM", "CRYPTOGRAMEDIT", "CRYPTOGRAMS", "CULTURE", 
				"CULTUREEDIT", "CUNNINGHAM", "CUNNINGHAMS", "CURRENT", "CURSE", "CURT", "CUT", "CYPHER", "D", 
				"DAHLIA", "DAILY", "DALY", "DANIEL", "DARK", "DARLENE", "DATA", "DATE", "DATED", "DAUGHTER", 
				"DAVE", "DAVID", "DAY", "DAYS", "DEAD", "DEADLY", "DEAR", "DEATH", "DEBATED", "DEBORAH", "DEC", 
				"DECADES", "DECAPITATED", "DECEMBER", "DECLARED", "DECODED", "DEEMED", "DEER", "DEFINITE", 
				"DEFINITIVE", "DEFINITIVELY", "DEMANDED", "DEMANDING", "DEMANDS", "DEMOLISHED", "DENIED", "DENIES", 
				"DENNIS", "DEPARTMENT", "DEPUTIES", "DESCRIBED", "DESCRIPTION", "DESCRIPTIONS", "DESKTOP", 
				"DESPITE", "DETAILED", "DETAILS", "DETECTIVE", "DETECTIVES", "DETERMINED", "DEVELOPED", "DEVICE", 
				"DIABLO", "DIAGRAM", "DID", "DIE", "DIED", "DIES", "DIRECTED", "DIRTY", "DISAPPEARANCE", 
				"DISAPPEARANCEEDIT", "DISBARRED", "DISCOVERED", "DISCOVERING", "DISCOVERY", "DISCREDITED", 
				"DISPATCHER", "DISPUTES", "DISTANCE", "DISTRIBUTOR", "DISTRICT", "DITCH", "DNA", "DO", "DOCUMENT", 
				"DOCUMENTS", "DOMINGOS", "DON", "DONALD", "DONNA", "DOOMED", "DOOR", 
				"DORANCHAKTALKSANDBOXPREFERENCESWATCHLISTCONTRIBUTIONSLOG", "DORGAN", "DOWN", "DOWNCITATION", 
				"DOZEN", "DR", "DRAGON", "DRAWN", "DREW", "DRIVE", "DRIVEN", "DRIVER", "DRIVING", "DROVE", "DUE", 
				"DUNBAR", "DURING", "DUTTON", "DUTY", "E", "EACH", "EARLIER", "EARLY", "EASY", "EDITION", "EDITOR", 
				"EDITORIAL", "EDWARDS", "EIGHT", "EIGHTEEN", "ELITE", "ELIZABETH", "ELUSIVE", "EMERGENCY", 
				"EMPLOYER", "ENCOUNTER", "END", "ENFORCEMENT", "ENJOY", "ENOUGH", "ENSUED", "ENTERED", "ENTIRELY", 
				"ENVELOPE", "ENVELOPES", "EPISODE", "ESCAPED", "ESCAPEE", "ESTIMATED", "EUREKA", "EVEN", "EVENING", 
				"EVENTUALLY", "EVER", "EVIDENCE", "EVIL", "EXACTLY", "EXAGGERATED", "EXAMINATION", "EXAMINER", 
				"EXAMINES", "EXAMINING", "EXCAVATION", "EXCERPTS", "EXCLUDED", "EXCLUSIVE", "EXECUTIONERSTYPE", 
				"EXGUMSHOES", "EXHUSBAND", "EXISTS", "EXITED", "EXORCIST", "EXPERT", "EXPERTS", "EXPLAINING", 
				"EXPLORES", "EXPRESSED", "EXTERNAL", "EXTRACTED", "EYEHOLES", "EYES", "EYEWITNESS", "F", "FACE", 
				"FACT", "FACTS", "FAILURE", "FALL", "FALSELY", "FAMILY", "FAN", "FARADAY", "FASCINATE", "FATHER", 
				"FBI", "FBIS", "FEARING", "FEATURED", "FEB", "FEBRUARY", "FEET", "FELTTIP", "FERRIN", "FERRINS", 
				"FEW", "FIANCÉE", "FIELD", "FILE", "FILES", "FILM", "FILMS", "FINAL", "FINALLY", "FIND", "FINDING", 
				"FINDINGS", "FINGERS", "FINISHING", "FIRING", "FIRST", "FISHING", "FIVE", "FLASHING", "FLASHLIGHT", 
				"FLEE", "FLEEING", "FOLLOW", "FOLLOWED", "FOLLOWING", "FOR", "FORENSIC", "FOREST", "FORGED", 
				"FORGING", "FORMED", "FORMER", "FORTH", "FORWARD", "FOUKE", "FOUND", "FOUNDATION", "FOUR", 
				"FOXGLOVE", "FRANCISCO", "FRANCISCOCA", "FREED", "FREEDMAN", "FRIEND", "FROM", "FRONT", 
				"FRONTPAGE", "FUGITIVE", "FULL", "FULLPAGE", "FURTHER", "GAFNI", "GAIKOWSKI", "GAIKOWSKIS", 
				"GARETH", "GAS", "GATE", "GAVE", "GAVIOTA", "GEARY", "GEORGE", "GILBERT", "GIRL", "GIVE", "GIVING", 
				"GLORY", "GO", "GOLDEN", "GOOD", "GOODYEAR", "GOOGLE", "GOT", "GRAVE", "GRAVEL", "GRAYSMITH", 
				"GRAYSMITHS", "GREATER", "GREETING", "GREETINGS", "GROUP", "GUARD", "GUMSHOES", "GUN", "GUNNED", 
				"GUTTED", "GUY", "HAD", "HALFWAY", "HALLOWEEN", "HANDS", "HANDWRITING", "HANDWRITTEN", "HAPPEN", 
				"HAPPENED", "HARDEN", "HARPER", "HARRY", "HARTNELL", "HARTNELLS", "HAS", "HAUGEN", "HAVE", 
				"HAVING", "HE", "HEAD", "HEADING", "HEADLIGHTS", "HEARING", "HEIGHTS", "HELL", "HELP", 
				"HENDRICKSON", "HER", "HERALD", "HERMAN", "HERRON", "HID", "HIDE", "HIGH", "HIGHLY", "HIGHWAY", 
				"HIKED", "HILL", "HIM", "HIMCITATION", "HIMSELF", "HINT", "HINTS", "HIS", "HISTORIC", "HISTORY", 
				"HISTORYWATCH", "HIT", "HITCHED", "HOAX", "HODEL", "HOGAN", "HOLES", "HOME", "HOMES", "HOMICIDE", 
				"HONKING", "HOOD", "HOODED", "HOOK", "HOPE", "HORN", "HOSPITAL", "HOSTED", "HOTEL", "HOURS", 
				"HOUSES", "HOWEVER", "HTTPBLOGSSFWEEKLYCOMTHESNITCHHOWMANYDISBARREDLAWYERSDOEPHP", 
				"HTTPMEMBERSCALBARCAGOVFALMEMBERDETAIL", "HURT", "I", "IDENTICAL", "IDENTIFICATION", "IDENTIFIED", 
				"IDENTIFY", "IDENTITY", "IDENTITYDEAD", "IF", "IGNORED", "II", "ILLEGITIMATE", "IMAGE", 
				"IMMEDIATELY", "IN", "INACTIVE", "INAUTHENTIC", "INCH", "INCHES", "INCIDENT", "INCLINE", "INCLUDE", 
				"INCLUDED", "INCLUDES", "INCLUDING", "INCONCLUSIVE", "INCONCLUSIVECITATION", "INCRIMINATING", 
				"INDICATED", "INDICATES", "INFANT", "INFORMING", "INITIALLY", "INITIALS", "INJURIES", "INSIDE", 
				"INSPECTORS", "INSPIRED", "INSTEAD", "INSTRUCTIONS", "INTERPRETED", "INTERSECTION", "INTERVIEW", 
				"INTO", "INVESTIGATE", "INVESTIGATED", "INVESTIGATIONS", "INVESTIGATIONSEDIT", "INVESTIGATORS", 
				"INVOLVED", "IRONY", "IS", "ISBN", "ISLAND", "IT", "ITEMS", "ITS", "ITSELF", "IVE", "JACK", 
				"JAMES", "JANET", "JANUARY", "JENSEN", "JENSENS", "JFKS", "JIM", "JO", "JOHN", "JOHNS", "JOSEPH", 
				"JUL", "JULY", "JUMPED", "JUNCTION", "JUNE", "JUST", "JUSTICE", "KACZYNSKI", "KAISER", "KATHLEEN", 
				"KAUFMAN", "KAUFMANS", "KEEP", "KELLEHER", "KELLY", "KEN", "KENNEDY", "KEYCHAIN", "KEYS", 
				"KIDDIES", "KILL", "KILLED", "KILLER", "KILLERS", "KILLING", "KILLINGS", "KING", "KNIFE", "KNOWN", 
				"KNOXVILLE", "KRIS", "KVON", "L", "LA", "LABYRINTH", "LAFFERTY", "LAKE", "LANCE", "LAND", 
				"LANDLORD", "LANE", "LANGUAGE", "LAPSED", "LARGE", "LASS", "LASSS", "LAST", "LASTED", "LATE", 
				"LATER", "LATEST", "LAW", "LAWYER", "LAWYERS", "LEAD", "LEADING", "LEADS", "LEAST", "LEAVE", "LED", 
				"LEE", "LEFT", "LEIGH", "LENGTHS", "LESS", "LETTER", "LETTEREDIT", "LETTERING", "LETTERS", 
				"LIBERATION", "LIBRARY", "LIFT", "LIMITS", "LINDA", "LINE", "LINES", "LINK", "LINKS", "LINKSEDIT", 
				"LIST", "LITTLE", "LIVED", "LIVING", "LIVINGUNWILLING", "LLOYD", "LOCAL", "LOCATED", "LOCATION", 
				"LOCATIONS", "LODGE", "LOMPOC", "LONE", "LOOKED", "LOOKOUT", "LOOSELY", "LOS", "LOT", "LOU", 
				"LOVERS", "LOW", "LUCID", "LUG", "LUGER", "LULU", "LYNDON", "LYRICS", "MADE", "MAG", "MAGAZINE", 
				"MAGEAU", "MAGEAUS", "MAGNET", "MAIL", "MAILED", "MAIN", "MAINTAIN", "MAINTAINED", "MAINTAINS", 
				"MAKING", "MALE", "MAN", "MANDAMUS", "MANS", "MANY", "MAP", "MAPLE", "MARCH", "MARCO", "MARINER", 
				"MARK", "MARKED", "MARSHA", "MARTINEZ", "MARTINEZCA", "MASON", "MASSACHUSETTS", "MATCH", "MATCHED", 
				"MATERIAL", "MATTHIAS", "MAUPIN", "MAY", "MCCARTHY", "MCDONNELL", "ME", "MEANING", "MEDIA", "MEET", 
				"MELVIN", "MEMBERS", "MEMO", "MEN", "MENU", "MERCHANT", "MESSAGE", "MEXICO", "MICHAEL", "MIDNIGHT", 
				"MIGHT", "MIKADO", "MIKE", "MILE", "MILES", "MILS", "MIND", "MINUTES", "MISSPELLED", "MIXUP", "MM", 
				"MOANING", "MODESTO", "MONEY", "MONTALDO", "MONTANA", "MONTH", "MONTHOLD", "MONTHS", "MORE", 
				"MORNING", "MORRILL", "MOST", "MOTHER", "MOTHERS", "MOUNT", "MOVE", "MOVIE", "MOVIES", "MT", 
				"MUCH", "MULTIPLE", "MURDER", "MURDERED", "MURDERER", "MURDERGLORIFICATION", "MURDERS", 
				"MURDERSCITATION", "MY", "MYSTERIES", "MYSTERY", "MYSTERYQUEST", "N", "NAME", "NAMED", "NANCY", 
				"NANETTE", "NAPA", "NARLOW", "NAVIGATION", "NEAR", "NEARBY", "NEAREST", "NEARLY", "NECK", "NEEDED", 
				"NEIGHBORHOOD", "NEIGHBORS", "NEITHER", "NEVADA", "NEVER", "NEW", "NEWS", "NEWSGAZETTE", 
				"NEWSPAPER", "NEWSPAPERS", "NEXT", "NICE", "NIGHT", "NINE", "NO", "NONE", "NOR", "NORDEN", "NORSE", 
				"NORTH", "NORTHERN", "NOT", "NOTE", "NOTHING", "NOTICED", "NOVELS", "NOVEMBER", "NUMBER", 
				"NUMEROUS", "NURSE", "NUTS", "NUYS", "OAK", "OAKLAND", "OBSERVED", "OCCULT", "OCCURRED", "OCTOBER", 
				"OF", "OFF", "OFFERED", "OFFERING", "OFFICE", "OFFICER", "OFFICERS", "OLD", "ON", "ONCE", "ONE", 
				"ONES", "ONETHIRD", "ONLINE", "ONLY", "ONTO", "OPEN", "OPERATED", "OPINION", "OPPORTUNISTIC", "OR", 
				"ORDERING", "ORIGINAL", "ORIGINATED", "OTHER", "OUT", "OUTARTICLETALKREADEDITVIEW", "OUTSET", 
				"OVER", "OWN", "OWNED", "P", "PACIFIC", "PAGE", "PAGES", "PAIR", "PALM", "PAPER", "PAPERBACKS", 
				"PAPERS", "PARADICE", "PARAMETER", "PARAPHRASED", "PARK", "PARKED", "PARKING", "PARTIAL", "PARTS", 
				"PASS", "PASSED", "PASSENGER", "PAST", "PASTED", "PASTING", "PAT", "PATIENT", "PATROL", 
				"PATTERSON", "PAUL", "PAY", "PD", "PEEK", "PEEKABOO", "PEN", "PENGUIN", "PENN", "PEOPLE", "PER", 
				"PEREZ", "PERIOD", "PERSON", "PERSUASIVELY", "PETALUMA", "PHANTOM", "PHILLIPS", "PHONE", "PHONED", 
				"PHOTO", "PHOTOCOPY", "PHOTOS", "PICK", "PICKEL", "PICNICKING", "PICTURE", "PIERCE", "PINES", 
				"PISTOL", "PLACE", "PLACED", "PLANNED", "PLASTIC", "PLAUSIBLE", "PLOTTING", "PM", "POEM", "POEMS", 
				"POINT", "POINTED", "POLICE", "POLICEMEN", "POPULAR", "PORTAL", "POSITIVELY", "POSSIBILITY", 
				"POSSIBLE", "POSSIBLY", "POSTAL", "POSTCARD", "POSTMARKED", "POSTULATED", "PP", "PRAEGER", 
				"PRAISING", "PRECUT", "PREGNANT", "PREPARE", "PREPARED", "PRESENTS", "PRESIDIO", "PRESS", 
				"PRESSENTERPRISE", "PRESSURE", "PREVIOUS", "PREVIOUSLY", "PRIMARY", "PRIME", "PRINT", "PRINTED", 
				"PRIOR", "PROBABLE", "PRODUCTIONS", "PROFILE", "PROGRESS", "PROMINENT", "PRONOUNCED", "PROOF", 
				"PROPERTY", "PROSECUTED", "PROVE", "PROVIDING", "PS", "PUBLIC", "PUBLISHED", "PULLED", "PUNCHED", 
				"PURSUE", "PURSUED", "PUT", "QUANTICO", "QUEEN", "QUESTIONED", "QUILL", "RADETICH", "RADIAN", 
				"RADIANS", "RADIO", "RAGE", "RAMBLER", "RANGERS", "RASMUSSEN", "RAY", "READ", "READING", 
				"READINGEDIT", "REAR", "REASON", "REASONS", "RECEIVED", "RECEIVING", "RECENT", "RECENTLY", 
				"RECOGNIZED", "RECORDING", "RECORDINGS", "RECOUNT", "RED", "REDLANDS", "REENTER", "REFERENCES", 
				"REFERENCESEDIT", "REFERRING", "REFUSED", "REGAINED", "REGARDING", "REGISTER", "RELATED", 
				"RELEASE", "RELEASED", "REMAINDER", "REMAINED", "REMAINS", "RENAULT", "RENOVATIONS", "REOPENED", 
				"REPEATEDLY", "REPORT", "REPORTED", "REPORTER", "REQUEST", "REQUESTED", "REQUESTING", "RESEARCHER", 
				"RESEARCHERS", "RESEMBLED", "RESEMBLES", "RESIDENCE", "RESIDENTS", "RESOLUTION", "RESOURCE", 
				"RESPONDING", "RESPONSE", "RESPONSIBILITY", "RESPONSIBLE", "RESTAURANT", "RESULT", "RESULTS", 
				"RETCITATION", "RETIRED", "RETIREMENT", "RETRIEVED", "RETURN", "RETURNED", "RETURNING", "REVEAL", 
				"REVEALED", "REVEALING", "REVISED", "REVISITED", "RH", "RHYME", "RICH", "RICHARD", "RIDE", "RIDGE", 
				"RIGHT", "RIVERSIDE", "RIVERSIDECITATION", "ROAD", "ROADMAP", "ROBBERY", "ROBERT", "ROCK", 
				"RODELLI", "ROLLS", "ROWLETT", "ROYAL", "RUSSELL", "S", "SACRAMENTO", "SAHARA", "SAID", "SALINAS", 
				"SALIVA", "SALUTATION", "SAM", "SAME", "SAN", "SAND", "SANTA", "SAT", "SATERICAL", "SATISFIED", 
				"SAYING", "SAYS", "SCENE", "SCHOOL", "SCHOOLCHILDREN", "SCORE", "SCRAWL", "SCREAM", "SCREAMS", 
				"SCRIBBLE", "SEAMAN", "SEARCH", "SEARCHED", "SECOND", "SECONDS", "SECTION", "SEE", "SEEING", 
				"SEEMINGLY", "SEEMS", "SEEN", "SEND", "SENIOR", "SENT", "SEPTEMBER", "SERGEANT", "SERIAL", 
				"SERIES", "SERIOUSLY", "SERVICE", "SET", "SEVEN", "SEVENPAGE", "SEVERAL", "SF", "SFPD", "SGT", 
				"SHACK", "SHE", "SHEPARD", "SHERIFF", "SHERIFFS", "SHERWOOD", "SHIRT", "SHOOT", "SHOOTING", 
				"SHOOTINGS", "SHORT", "SHORTLY", "SHORTS", "SHOT", "SHOTS", "SHOW", "SHOWED", "SHOWEDIT", "SHUT", 
				"SIC", "SICK", "SIDE", "SIDEWALK", "SIERRA", "SIGN", "SIGNATURE", "SIGNATURECITATION", "SIGNED", 
				"SILENCED", "SILENT", "SIMILAR", "SIMILARITIES", "SIMILARITY", "SINCE", "SITE", "SITTING", "SIX", 
				"SIXANDAHALF", "SKETCH", "SKIPPED", "SLATED", "SLAVE", "SLAVES", "SLAYER", "SLEMEN", "SLOVER", 
				"SLOW", "SMALL", "SMITH", "SNIPPET", "SNOW", "SO", "SOLANO", "SOLUTION", "SOLUTIONS", "SOLVED", 
				"SOLVING", "SOME", "SOMEONE", "SOMETIME", "SON", "SONG", "SOON", "SOUGHT", "SOURCES", 
				"SOURCESEDIT", "SOUTH", "SPEAKING", "SPECIFIC", "SPELLED", "SPENT", "SPINELLI", "SPIT", "SPOKE", 
				"SPREE", "SPRINGS", "SQUAD", "SQUARE", "STAB", "STABBED", "STABBING", "STAIRWAY", "STAMPS", 
				"STANLEY", "START", "STATE", "STATED", "STATELINE", "STATEMENT", "STATES", "STATING", "STATION", 
				"STATIONS", "STATUS", "STELLA", "STEPFATHER", "STEPPING", "STEVE", "STILL", "STILLWET", "STILTZ", 
				"STILTZS", "STINE", "STINES", "STOCKTON", "STOLEN", "STOP", "STOPPED", "STOPPING", "STORIES", 
				"STORY", "STRANGE", "STREET", "STREETS", "STUDENTS", "STYLE", "SUBJECT", "SUBSEQUENTLY", "SUGGEST", 
				"SUGGESTED", "SULLIVAN", "SUMMER", "SUMMONED", "SUNGLASSES", "SUNGLASSESCITATION", "SUNSTONE", 
				"SUPPRESSED", "SUPREME", "SURFACE", "SURFACED", "SURROUNDING", "SURVIVED", "SURVIVEDCITATION", 
				"SUSPECT", "SUSPECTED", "SUSPECTS", "SUSPECTSEDIT", "SUSPICIOUS", "SWATCH", "SYMBIONESE", "SYMBOL", 
				"T", "TAHOE", "TAIL", "TAKE", "TAKEN", "TALE", "TALES", "TALK", "TAPPAAN", "TARBOX", "TARBOXS", 
				"TARGETED", "TARRANCE", "TARRANCES", "TAUNTING", "TED", "TEEN", "TEENAGERS", "TELEPHONE", 
				"TELEVISION", "TELLING", "TEN", "TERROR", "TEST", "TESTING", "TEXT", "TH", "THAN", "THAT", "THE", 
				"THEIR", "THEM", "THEMSELVES", "THEN", "THEORY", "THERE", "THESE", "THEY", "THIRD", "THIRTEEN", 
				"THIS", "THOUGH", "THOUGHT", "THOUGHTS", "THREAT", "THREATEDIT", "THREATENED", "THREE", 
				"THREEBYFIVE", "THREETENTHS", "THROUGH", "TICKET", "TIE", "TIED", "TIGHTEN", "TIGHTENED", "TIME", 
				"TIMELINE", "TIMELINEEDIT", "TIMES", "TIMESHERALD", "TIMEX", "TIP", "TIPLINE", "TIPS", "TIRE", 
				"TITLED", "TO", "TOLD", "TOM", "TONE", "TOOK", "TOP", "TORCHED", "TORE", "TORN", "TORTURE", 
				"TOSCHI", "TOWARD", "TOWARDS", "TOWN", "TRACED", "TRACKING", "TRACY", "TRAIL", "TRANSPORT", 
				"TREATING", "TRIED", "TRUE", "TRUTVCOM", "TRYING", "TUCKER", "TUOLUMNE", "TURNED", "TURNOUT", 
				"TWENTYEIGHT", "TWICE", "TWIN", "TWO", "TYPE", "TYPEWRITER", "TYPEWRITTEN", "UNABLE", "UNABOMBER", 
				"UNACCREDITED", "UNCERTAIN", "UNCOVERED", "UNCRACKED", "UNEXPLAINED", "UNIDENTIFIED", "UNION", 
				"UNITED", "UNKNOWN", "UNLESS", "UNLIKELY", "UNMARKED", "UNMASKED", "UNSOLVED", "UNSUCCESSFULLY", 
				"UNTIL", "UNUSUAL", "UNVERIFIED", "UP", "UPON", "UPSET", "US", "USE", "USED", "USING", "USUAL", 
				"UTILIZING", "V", "VALLEJO", "VALLEJOSEPT", "VALLEY", "VAN", "VEHICLE", "VERSE", "VERY", "VIA", 
				"VICTIM", "VICTIMS", "VICTIMSEDIT", "VIEWED", "VIEWERS", "VILLAGE", "VISIT", "VISITED", "VOICE", 
				"VOLKSWAGENS", "W", "WAIT", "WAKE", "WALKED", "WALKING", "WALLET", "WANTED", "WARD", "WARNED", 
				"WAS", "WASH", "WASHINGTON", "WATCH", "WAYS", "WEAPON", "WEARING", "WEB", "WEBSITE", "WEEK", 
				"WEEKEND", "WEEKS", "WEIRD", "WEISS", "WELL", "WELLKNOWN", "WENT", "WERE", "WEST", "WHAT", "WHEEL", 
				"WHEN", "WHERE", "WHICH", "WHILE", "WHITE", "WHO", "WHOM", "WHOSE", "WHY", "WIDELY", "WIKIMEDIA", 
				"WIKISOURCE", "WILL", "WILLIAM", "WILLIAMS", "WIPING", "WIRE", "WIRES", "WITH", "WITHIN", 
				"WITHOUT", "WITNESSES", "WOBBLING", "WOMAN", "WOMEN", "WORD", "WORDS", "WORE", "WORK", "WORKED", 
				"WORKING", "WORN", "WOULD", "WOUNDS", "WRISTBAND", "WRISTWATCH", "WRITER", "WRITING", "WRITINGS", 
				"WRITTEN", "WROTE", "X", "YARD", "YARDS", "YEAR", "YEAROLD", "YEARS", "YET", "YIELDED", "YORK", 
				"YOU", "YOUNG", "YOURSELVES", "Z", "ZAMORRA", "ZERO", "ZIMMERMAN", "ZODIAC", "ZODIACEDIT", 
				"ZODIACKILLERCOM", "ZODIACS"				
		};
		for (String word : words) {
			if (word.length() < 4) continue;
			search(word);
		}
	}
	
	/** 
	 * search for "plain-to-cipher" word pairs.
	 * a word pair is a plain text word that, when enciphered using the 408's
	 * key, can be interpreted as another word.
	 * 
	 * scan every possible variant (since a plaintext word can have numerous encipherments).
	 * scan every interpretation of the symbols.
	 * 
	 * in the output, include a measurement of how different the enciphered word is from the 
	 * original plaintext word.  it'd be interesting to discover long words that are very similar.
	 *  - bonus: word pairs that are palindromes or anagrams of each other 
	 * 
	 * in the output, also include counts of how many "cheats" are needed to make the word fit.
	 * examples of cheats:
	 * 1) Using one the the 408 key's transcription mistakes
	 * 2) Requiring flips/rotations/ambiguity to interpret resulting symbol  
	 * 
	 */
	public static void searchForPairs() {
		init();
		for (String word : WordFrequencies.map.keySet()) {
			searchForPairs(word, new StringBuffer(), 0);
		}
	}
	static void searchForPairs(String plaintext, StringBuffer enciphered, int pos) {
		if (plaintext.length() == 0) return;
		//System.out.println("searching " + plaintext + ", " + enciphered + ", " + pos);
		// stop condition: there's no interpretation that results in a valid prefix
		List<String> foundWords = new ArrayList<String>();
		if (enciphered.length() > 0 && !validPrefixForInterpretation(enciphered, foundWords))
			return;
		
		// stop condition: reached end of word
		if (pos >= plaintext.length()) {
			// the found words are either prefixes or full words; we only care to dump the full words
			if (hasWord(foundWords)) {
				for (String word : foundWords)
					dumpPairs(plaintext, enciphered, word);
			}
			return;
		}
		// stop condition: current plaintext letter has no ciphertext equivalents.
		char key = plaintext.charAt(pos);
		String val = key408Map.get(key);
		if (val == null || "".equals(val)) return;
		
		
		//recurse to find all possible ciphertext equivalents for the rest of the word
		for (int i=0; i<val.length(); i++) {
			char ch = val.charAt(i);
			enciphered.append(ch);
			
			searchForPairs(plaintext, enciphered, pos+1);
			enciphered.deleteCharAt(enciphered.length()-1);
		}
		
	}
	/** returns true if the given list contains at least one valid word */
	static boolean hasWord(List<String> words) {
		if (words == null) return false;
		for (String word : words) if (WordFrequencies.hasWord(word)) return true;
		return false;
	}
	/** returns true if the given encipherment has an interpretation that
	 * is the prefix of at least one valid word (or is itself a word) 
	 */
	static boolean validPrefixForInterpretation(StringBuffer enciphered, List<String> foundWords) {
		if (enciphered == null || "".equals(enciphered)) return true;
		
		return validPrefixForInterpretation(enciphered, new StringBuffer(), 0, foundWords);
	}
	
	static boolean validPrefixForInterpretation(StringBuffer enciphered, StringBuffer interpretation, int pos, List<String> foundWords) {
		// stop condition: interpretation is not a valid prefix
		if (interpretation.length() > 0 && !WordFrequencies.hasPrefix(interpretation.toString()))
			return false;
		// stop condition: reached end of word
		if (pos >= enciphered.length()) {
			if (WordFrequencies.hasPrefix(interpretation.toString())) {
				foundWords.add(interpretation.toString());
				return true;
			}
			return false;
		}
		
		// scan all interpretations of current symbol, and recursively search all combinations of remaining interpretations for the rest of the encipherment.
		Character key = enciphered.charAt(pos);
		String val = translations.get(key);
		if (val == null) throw new RuntimeException("No translations found for [" + key + "]");
		
		boolean result = false;
		for (int i=0; i<val.length(); i++) {
			interpretation.append(val.charAt(i));
			result = validPrefixForInterpretation(enciphered, interpretation, pos+1, foundWords);
			interpretation.deleteCharAt(interpretation.length()-1);
			if (result) return true;
		}
		return result;
	}
	
	
	/*
	 * 1) length
	 * 2) score
	 * 3) pair diffs
	 * 4) cheats
	 * 5) freq plain
	 * 6) freq word
	 * 7) plain
	 * 8) word
	 */
	static void dumpPairs(String plaintext, StringBuffer enciphered, String word) {
		float cheats = cheats(enciphered.toString(), word);
		if (cheats > MAX_CHEATS)
			return; // disallow too many cheats
		System.out.println(plaintext.length() + ", "
				+ score(plaintext, word, enciphered) + ", "
				+ pairDiff(plaintext, word) + ", " + cheats + ", "
				+ WordFrequencies.freq(plaintext) + ", "
				+ WordFrequencies.freq(word) + ", " + plaintext + ", "
				+ enciphered + ", " + word);
	}

	static float score(String plaintext, String word, StringBuffer enciphered) {
		
		// maximize word frequencies
		// minimize cheats
		// maximize word differences
		
		
		float cheats = cheats(enciphered.toString(), word);
		int pairDiff = word.length() - pairDiff(plaintext, word); // 0 is best 
		float score = WordFrequencies.freq(word) + WordFrequencies.freq(plaintext);
		score /= (1+cheats);
		score /= (1+pairDiff);
		return score;
	}
	/** number of differences between words */
	static int pairDiff(String plaintext, String word) {
		int result = 0;
		for (int i=0; i<plaintext.length(); i++) {
			char ch1 = plaintext.charAt(i);
			char ch2 = word.charAt(i);
			if (ch1 != ch2) result++;
		}
		return result;
	}
	/** number of "cheats" (undesired mappings) needed to force the word */
	static float cheats(String enciphered, String word) {
		float cheats = 0;
		for (int i=0; i<enciphered.length(); i++) {
			String key = "" + enciphered.charAt(i) + word.charAt(i);
			if (undesired.contains(key)) {
				cheats+=1.0;
				//System.out.println(key);
			}
			if (undesiredMinor.contains(key)) {
				cheats+=0.5;
			}
		}
		return cheats;
	}


	public static void main(String[] args) {
		//search("MORF");
		//searchWikiWords();
		//searchForPairs();

		
		Census.init(2000);
		
		System.out.println("===== last");
		
		
		for (Name name : Census.last) {
			// all suffix combinations
			for (String suf : Census.SUFFIXES)
				search(name.name + suf);
		}
		System.out.println("===== random combinations");
		while (true) {
			List<Name> name = Census.buildName(-1, 2000);
			String s = "";
			for (Name n : name) s += n.name;
			search(s);
		}
		//for (String name : last) search(name+"SR");
		//search("EVBESTJR");
		//System.out.println(translations.get('>'));
		
		/*
		for (int i=0; i<1000; i++) {
			System.out.println(i);
			search("IDOB");
		}*/
		
		//System.out.println(cheats("WHeELErS","WHEELERS"));

		/*
		for (Character key : translations.keySet()) {
			String val = translations.get(key);
			for (int i=0; i<val.length(); i++) {
				char plain = val.charAt(i);
				
				System.out.println("translations[\"" + key + plain + "\"] = \"?\";"); 
			}
		}*/
	}
}
