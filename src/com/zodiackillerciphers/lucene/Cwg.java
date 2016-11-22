package com.zodiackillerciphers.lucene;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
//import java.io.FileInputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;


public class Cwg {


	
	private static final int MAX = 15;
	private static final int LOWER_IT = 32;
	private static final int VALID_CHAR_RANGE_SIZE = 'Z' - '?' + 1;
	private static final int EOW_FLAG = 0X40000000;
	private static final int LIST_FORMAT_INDEX_MASK = 0X3FFE0000;
	private static final int LIST_FORMAT_BIT_SHIFT = 17;
	private static final int CHILD_MASK = 0X0001FFFF;
	
	private static final int powersOfTwo[] = { 1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192, 16384,
			 32768, 65536, 131072, 262144, 524288, 1048576, 2097152, 4194304, 8388608, 16777216, 33554432 };
	
	private static final int[] childListMasks = { 0X0, 0X1, 0X3, 0X7, 0XF, 0X1F, 0X3F, 0X7F, 0XFF, 0X1FF, 0X3FF, 0X7FF, 0XFFF,
			 0X1FFF, 0X3FFF, 0X7FFF, 0XFFFF, 0X1FFFF, 0X3FFFF, 0X7FFFF, 0XFFFFF, 0X1FFFFF, 0X3FFFFF, 0X7FFFFF, 0XFFFFFF, 0X1FFFFFF };
	
	private static final byte[] popCountTable = { 0, 1, 1, 2, 1, 2, 2, 3, 1, 2, 2, 3, 2, 3, 3, 4, 1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4,
			 4, 5, 1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5, 2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 1, 2, 2, 3, 2, 3, 3, 4, 2, 3,
			 3, 4, 3, 4, 4, 5, 2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 3, 4, 4, 5, 4, 5,
			 5, 6, 4, 5, 5, 6, 5, 6, 6, 7, 1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5, 2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 2, 3,
			 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7, 2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5,
			 5, 6, 3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7, 3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7, 4, 5, 5, 6, 5, 6, 6, 7, 5, 6,
			 6, 7, 6, 7, 7, 8 };
	
	// CWG format and array-size variables.
	private int totalNumberOfWords;
	private int nodeArraySize;
	private int listFormatArraySize;
	private int root_WTEOBL_ArraySize;
	private int short_WTEOBL_ArraySize;
	private int byte_WTEOBL_ArraySize;
	
	// The 5 CWG arrays.
	private int theNodeArray[];
	private int theListFormatArray[];
	private int theRoot_WTEOBL_Array[];
	private short theShort_WTEOBL_Array[];
	private byte theByte_WTEOBL_Array[];
	
	// The "Cwg" class-constructor.
	public Cwg() throws Exception {
		int x;
	    DataInputStream cwgDataFile = new DataInputStream(new FileInputStream("/Users/doranchak/Downloads/CWG_Data_For_Word-List.dat"));
	    //DataInputStream cwgDataFile = new DataInputStream(new BufferedInputStream(new FileInputStream("CWG_Data_For_Word-List.dat")));
	    
	    totalNumberOfWords = endianIntConversion(cwgDataFile.readInt());
	    nodeArraySize = endianIntConversion(cwgDataFile.readInt());
	    listFormatArraySize = endianIntConversion(cwgDataFile.readInt());
	    root_WTEOBL_ArraySize = endianIntConversion(cwgDataFile.readInt());
	    short_WTEOBL_ArraySize = endianIntConversion(cwgDataFile.readInt());
	    byte_WTEOBL_ArraySize = endianIntConversion(cwgDataFile.readInt());
	    
	    theNodeArray = new int[nodeArraySize];
	    theListFormatArray = new int[listFormatArraySize];
	    theRoot_WTEOBL_Array = new int[root_WTEOBL_ArraySize];
	    theShort_WTEOBL_Array = new short[short_WTEOBL_ArraySize];
	    theByte_WTEOBL_Array = new byte[byte_WTEOBL_ArraySize];
	    
		
		for (x = 0; x < nodeArraySize; x++) {
			theNodeArray[x] = endianIntConversion(cwgDataFile.readInt());
		}
		for (x = 0; x < listFormatArraySize; x++) {
			theListFormatArray[x] = endianIntConversion(cwgDataFile.readInt());
		}
		for (x = 0; x < root_WTEOBL_ArraySize; x++) {
			theRoot_WTEOBL_Array[x] = endianIntConversion(cwgDataFile.readInt());
		}
		for (x = 0; x < short_WTEOBL_ArraySize; x++) {
			theShort_WTEOBL_Array[x] = endianShortConversion(cwgDataFile.readShort());
		}
		for (x = 0; x < byte_WTEOBL_ArraySize; x++) {
			theByte_WTEOBL_Array[x] = cwgDataFile.readByte();
		}
		
		
		cwgDataFile.close();
	}
	
	private int endianIntConversion(int thisInteger) {
		return ((thisInteger & 0x000000ff) << 24) + ((thisInteger & 0x0000ff00) << 8) + ((thisInteger & 0x00ff0000) >>> 8) + ((thisInteger & 0xff000000) >>> 24);
	}
	
	private short endianShortConversion(short thisShort) {
		return (short)(((thisShort & 0x00ff) << 8) + ((thisShort & 0xff00) >>> 8));
	}
	
	private String searchForStringRecurse(String thisString, int position, int thisIndex, String[] nodePrint, boolean[] result, int[] nodeCount) {
		char thisChar = thisString.charAt(position);
		int thisChildListFormat;
		nodeCount[0] += 1;
		nodePrint[position] = "'<B>" + thisChar + "</B>' @ |" + thisIndex +"|";
		String addThisMessage = "";
		if ( thisString.length() == (position + 1) ) {
			if ( (theNodeArray[thisIndex] & EOW_FLAG) != 0 ) {
				result[0] = true;
				nodePrint[position] += "- <B>found " + (char)0X2713 + "</B>";
				for ( int x = position + 1; x < MAX; x++ ) nodePrint[x] = "";
				return addThisMessage;
			}
			else {
				result[0] = false;
				nodePrint[position] += "- lost <B>EOW</B>";
				for ( int x = position + 1; x < MAX; x++ ) nodePrint[x] = "";
				return (addThisMessage + "\nWord Lost\n");
			}
		}
		else {
			thisChar = thisString.charAt(position + 1);
			if ( (theNodeArray[thisIndex] & CHILD_MASK) != 0 ) {
				thisChildListFormat = theListFormatArray[(theNodeArray[thisIndex] & LIST_FORMAT_INDEX_MASK)>>>LIST_FORMAT_BIT_SHIFT];
				if ( (thisChildListFormat & powersOfTwo[thisChar - 'A']) != 0 ) {
					nodePrint[position] += "- found '<B>" + thisChar + "</B>' child";
					return (addThisMessage + searchForStringRecurse(thisString, position + 1, (theNodeArray[thisIndex] & CHILD_MASK) + listFormatPopCount(thisChildListFormat, thisChar - 'A'), nodePrint, result, nodeCount));
				}
			}
			result[0] = false;
			nodePrint[position] += "- <B>lost '" + thisChar + "</B>' child";
			for ( int x = position + 1; x < MAX; x++ ) nodePrint[x] = "";
			return (addThisMessage + "\n'" +thisChar + "' Is a missing child in position |" + (position + 2) + "|\n\nWord Lost\n");
		}
	}

	public String searchForString(String toSearchFor, String[] nodeCheck, boolean[] theResult, int[] theCount) {
		String holder;
		String upperString = toSearchFor.toUpperCase();
		String traversalResult = new String("Searching for:  |" + upperString + "| - ");
		theCount[0] = 0;
		holder = searchForStringRecurse(upperString, 0, (upperString.charAt(0) - 'A' + 1), nodeCheck, theResult, theCount);
		if ( theResult[0] ) traversalResult += "Word Found " + (char)0X2713;
		else traversalResult += "Word Not Found.\n";
		traversalResult += holder;
		return traversalResult;
	}
	
	private String hashStringRecurse(String thisString, int position, int thisIndex, String[] nodePrint, int[] currentMarker, int[] nodeCount) {
		char thisChar = thisString.charAt(position);
		int thisChildListFormat;
		nodeCount[0] += 1;
		nodePrint[position] = "'<B>" + thisChar + "</B>' @ |" + thisIndex +"|";
		String addThisMessage = new String();
		if ( (theNodeArray[thisIndex] & EOW_FLAG) != 0 ) currentMarker[0] -= 1;
		nodePrint[position] += " Mark-[ " + (totalNumberOfWords - currentMarker[0]) + " ]";
		if ( thisString.length() == (position + 1) ) {
			if ( (theNodeArray[thisIndex] & EOW_FLAG) != 0 ) {
				nodePrint[position] += " <B>" + (char)0X2713 + "</B>";
				for ( int x = position + 1; x < MAX; x++ ) nodePrint[x] = "";
				return addThisMessage;
			}
			else {
				currentMarker[0] = totalNumberOfWords;
				nodePrint[position] += " <B>X</B>";
				for ( int x = position + 1; x < MAX; x++ ) nodePrint[x] = "";
				return (addThisMessage + "\nWord Lost\n");
			}
		}
		else {
			thisChar = thisString.charAt(position + 1);
			if ( (theNodeArray[thisIndex] & CHILD_MASK) != 0 ) {
				thisChildListFormat = theListFormatArray[(theNodeArray[thisIndex] & LIST_FORMAT_INDEX_MASK)>>>LIST_FORMAT_BIT_SHIFT];
				if ( (thisChildListFormat & powersOfTwo[thisChar - 'A']) != 0 ) {
					thisIndex = theNodeArray[thisIndex] & CHILD_MASK;
					if ( thisIndex < short_WTEOBL_ArraySize ) {
						currentMarker[0] -= theShort_WTEOBL_Array[thisIndex];
						thisIndex += listFormatPopCount(thisChildListFormat, thisChar - 'A');
						currentMarker[0] += (theShort_WTEOBL_Array[thisIndex] & 0XFFFF);
					}
					// When reading "theByte_WTEOBL_Array", it is necessary to mask the values with "0XFF" to cast them as unsigned.
					else {
						currentMarker[0] -= (theByte_WTEOBL_Array[thisIndex - short_WTEOBL_ArraySize] & 0XFF);
						thisIndex += listFormatPopCount(thisChildListFormat, thisChar - 'A');
						currentMarker[0] += (theByte_WTEOBL_Array[thisIndex - short_WTEOBL_ArraySize] & 0XFF);
						
					}
					return (addThisMessage + hashStringRecurse(thisString, position + 1, thisIndex, nodePrint, currentMarker, nodeCount));
				}
			}
			currentMarker[0] = totalNumberOfWords;
			nodePrint[position] += " <B>X</B>";
			for ( int x = position + 1; x < MAX; x++ ) nodePrint[x] = "";
			return (addThisMessage + "\n'" +thisChar + "' Is a missing child in position |" + (position + 2) + "|\n\nWord Lost\n");
		}
	}
	
	public String hashString(String toSearchFor, String[] nodeCheck, int[] hashResult, int[] theCount) {
		String holder;
		String upperString = toSearchFor.toUpperCase();
		String traversalResult = new String("Look for: |" + upperString + "| - ");
		theCount[0] = 0;
		hashResult[0] = theRoot_WTEOBL_Array[upperString.charAt(0) - 'A' + 1];
		holder = hashStringRecurse(upperString, 0, (upperString.charAt(0) - 'A' + 1), nodeCheck, hashResult, theCount);
		hashResult[0] = totalNumberOfWords - hashResult[0];
		if ( hashResult[0] != 0 ) {
			traversalResult += "Word Found @ [" + hashResult[0] + "]\n";
		}
		else traversalResult += "Word Not Found.\n";
		traversalResult += holder;
		return traversalResult;
	}
	
	private String booleanAnagramRecurse(int thisIndex, char thisChar, char[] fuckWithMe, int fillThisPosition, char[] characterBank, int sizeOfBank, int[] wordCounter, int[] nodeCounter) {
		String holder;
		String wordAccumulator = "";
		char currentChar;
		boolean[] theUsedChars = new boolean[VALID_CHAR_RANGE_SIZE];
		int firstChildIndex = (theNodeArray[thisIndex] & CHILD_MASK);
		int theChildListFormat;
		
		for ( int x = 0; x < VALID_CHAR_RANGE_SIZE; x++ ) theUsedChars[x] = false;
		fuckWithMe[fillThisPosition] = thisChar;
		nodeCounter[fillThisPosition] += 1;
		if ( (theNodeArray[thisIndex] & EOW_FLAG) != 0 ) {
			wordCounter[0] += 1;
			wordAccumulator = new String(fuckWithMe, 0, fillThisPosition + 1);
			wordAccumulator = "|" + wordCounter[0] + "| - " + wordAccumulator;
			if ( sizeOfBank == 0 ) wordAccumulator += " ********->\n";
			else wordAccumulator += "\n";
		}
		if ( (sizeOfBank != 0) && (firstChildIndex != 0) ) {
			theChildListFormat = theListFormatArray[(theNodeArray[thisIndex] & LIST_FORMAT_INDEX_MASK)>>>LIST_FORMAT_BIT_SHIFT];
			for ( int x = 0; x < sizeOfBank; x++ ) {
				currentChar = characterBank[x];
				if ( theUsedChars[currentChar - '?'] == true ) continue;
				if ( currentChar == '?' ) {
					removeCharFromArray(characterBank, x, sizeOfBank);
					for ( int y = 'A'; y <= 'Z'; y++ ) {
						if ( (theChildListFormat & powersOfTwo[y - 'A']) != 0 ) {
							holder = booleanAnagramRecurse(firstChildIndex + listFormatPopCount(theChildListFormat, y - 'A'), (char)(y + LOWER_IT), fuckWithMe, fillThisPosition + 1, characterBank, sizeOfBank - 1, wordCounter, nodeCounter);
							wordAccumulator += holder;
						}
					}
					insertCharIntoArray(characterBank, x, currentChar, sizeOfBank);
					theUsedChars[currentChar - '?'] = true;
				}
				else if ( (theChildListFormat & powersOfTwo[currentChar - 'A']) != 0 ) {
					removeCharFromArray(characterBank, x, sizeOfBank);
					holder = booleanAnagramRecurse(firstChildIndex + listFormatPopCount(theChildListFormat, currentChar - 'A'), currentChar, fuckWithMe, fillThisPosition + 1, characterBank, sizeOfBank - 1, wordCounter, nodeCounter);
					wordAccumulator += holder;
					insertCharIntoArray(characterBank, x, currentChar, sizeOfBank);
					theUsedChars[currentChar - '?'] = true;
				}
			}
		}
		return wordAccumulator;
	}
	
	public String booleanAnagram(boolean toSort, String toAnagram, String[] nodeCheck, int[] numberOfWords, int[] theNodeCounts) {
		String holder;
		String upperAnagramString = toAnagram.toUpperCase();
		String traversalResult = "";
		int numberOfLetters = upperAnagramString.length();
		char[] inputCharArray = upperAnagramString.toCharArray();
		char[] workingCharArray = new char[MAX];
		char currentChar;
		boolean[] usedChars = new boolean[VALID_CHAR_RANGE_SIZE];
		
		if ( toSort ) java.util.Arrays.sort(inputCharArray);
		for ( int x = 0; x < VALID_CHAR_RANGE_SIZE; x++ ) usedChars[x] = false;
		
		for ( int x = 0; x < numberOfLetters; x++ ) {
			currentChar = inputCharArray[x];
			if ( usedChars[currentChar - '?'] == true ) continue;
			removeCharFromArray(inputCharArray, x, numberOfLetters);
			if ( currentChar == '?' ) {
				for ( int y = 'A'; y <= 'Z'; y++ ) {
					holder = "-----------------------------\n";
					holder += booleanAnagramRecurse(y - '@', (char)(y + LOWER_IT), workingCharArray, 0, inputCharArray, numberOfLetters - 1, numberOfWords, theNodeCounts);
					traversalResult += holder;
				}
			}
			else {
				holder = "-----------------------------\n";
				holder += booleanAnagramRecurse(currentChar - '@', currentChar, workingCharArray, 0, inputCharArray, numberOfLetters - 1, numberOfWords, theNodeCounts);
				traversalResult += holder;
			}
			insertCharIntoArray(inputCharArray, x, currentChar, numberOfLetters);
			usedChars[currentChar - '?'] = true;
		}
		for ( int x = 0; x < MAX; x++ ) {
			nodeCheck[x] = "| " + theNodeCounts[x] + " |";
			if ( theNodeCounts[x] != 0 )nodeCheck[x]+= " Nodes " + "<B>" + (char)0X2713 + "</B>";
		}
		
		traversalResult = "Anagramming this:  |" + upperAnagramString + "|\nResults in |" + numberOfWords[0] + "| words.\n" + traversalResult;
		return traversalResult;
	}
	
	private String hashAnagramRecurse(int thisIndex, int currentMarker, char thisChar, char[] fuckWithMe, int fillThisPosition, char[] characterBank, int sizeOfBank, int[] wordCounter, int[] nodeCounter) {
		String holder;
		String wordAccumulator = "";
		char currentChar;
		boolean[] theUsedChars = new boolean[VALID_CHAR_RANGE_SIZE];
		int firstChildIndex = (theNodeArray[thisIndex] & CHILD_MASK);
		int theChildListFormat;
		int nextMarker;
		boolean isChildShort;
		
		for ( int x = 0; x < VALID_CHAR_RANGE_SIZE; x++ ) theUsedChars[x] = false;
		fuckWithMe[fillThisPosition] = thisChar;
		nodeCounter[fillThisPosition] += 1;
		if ( (theNodeArray[thisIndex] & EOW_FLAG) != 0 ) {
			currentMarker -= 1;
			wordCounter[0] += 1;
			wordAccumulator = new String(fuckWithMe, 0, fillThisPosition + 1);
			wordAccumulator = "|" + wordCounter[0] + "| - " + wordAccumulator + " [ " + (totalNumberOfWords - currentMarker) + " ]";
			if ( sizeOfBank == 0 ) wordAccumulator += " ********->\n";
			else wordAccumulator += "\n";
		}
		if ( (sizeOfBank != 0) && (firstChildIndex != 0) ) {
			isChildShort = ( firstChildIndex < short_WTEOBL_ArraySize ) ? true: false;
			theChildListFormat = theListFormatArray[(theNodeArray[thisIndex] & LIST_FORMAT_INDEX_MASK)>>>LIST_FORMAT_BIT_SHIFT];
			if ( isChildShort ) currentMarker -= theShort_WTEOBL_Array[firstChildIndex];
			else currentMarker -= (theByte_WTEOBL_Array[firstChildIndex - short_WTEOBL_ArraySize] & 0XFF);
			for ( int x = 0; x < sizeOfBank; x++ ) {
				currentChar = characterBank[x];
				if ( theUsedChars[currentChar - '?'] == true ) continue;
				if ( currentChar == '?' ) {
					removeCharFromArray(characterBank, x, sizeOfBank);
					for ( int y = 'A'; y <= 'Z'; y++ ) {
						if ( (theChildListFormat & powersOfTwo[y - 'A']) != 0 ) {
							thisIndex = firstChildIndex + listFormatPopCount(theChildListFormat, y - 'A');
							if ( isChildShort ) nextMarker = currentMarker + theShort_WTEOBL_Array[thisIndex];
							// When reading "theByte_WTEOBL_Array", it is necessary to mask the values with "0XFF" to cast them as unsigned.
							else nextMarker = currentMarker + (theByte_WTEOBL_Array[thisIndex - short_WTEOBL_ArraySize] & 0XFF);
							holder = hashAnagramRecurse(thisIndex, nextMarker, (char)(y + LOWER_IT), fuckWithMe, fillThisPosition + 1, characterBank, sizeOfBank - 1, wordCounter, nodeCounter);
							wordAccumulator += holder;
						}
					}
					insertCharIntoArray(characterBank, x, currentChar, sizeOfBank);
					theUsedChars[currentChar - '?'] = true;
				}
				else if ( (theChildListFormat & powersOfTwo[currentChar - 'A']) != 0 ) {
					removeCharFromArray(characterBank, x, sizeOfBank);
					thisIndex = firstChildIndex + listFormatPopCount(theChildListFormat, currentChar - 'A');
					if ( isChildShort ) nextMarker = currentMarker + theShort_WTEOBL_Array[thisIndex];
					// When reading "theByte_WTEOBL_Array", it is necessary to mask the values with "0XFF" to cast them as unsigned.
					else nextMarker = currentMarker + (theByte_WTEOBL_Array[thisIndex - short_WTEOBL_ArraySize] & 0XFF);
					holder = hashAnagramRecurse(thisIndex, nextMarker, currentChar, fuckWithMe, fillThisPosition + 1, characterBank, sizeOfBank - 1, wordCounter, nodeCounter);
					wordAccumulator += holder;
					insertCharIntoArray(characterBank, x, currentChar, sizeOfBank);
					theUsedChars[currentChar - '?'] = true;
				}
			}
		}
		return wordAccumulator;
	}
	
	public String hashAnagram(boolean toSort, String toAnagram, String[] nodeCheck, int[] numberOfWords, int[] theNodeCounts) {
		String holder;
		String upperAnagramString = toAnagram.toUpperCase();
		String traversalResult = "";
		int numberOfLetters = upperAnagramString.length();
		char[] inputCharArray = upperAnagramString.toCharArray();
		char[] workingCharArray = new char[MAX];
		char currentChar;
		boolean[] usedChars = new boolean[VALID_CHAR_RANGE_SIZE];
		
		if ( toSort ) java.util.Arrays.sort(inputCharArray);
		for ( int x = 0; x < VALID_CHAR_RANGE_SIZE; x++ ) usedChars[x] = false;
		
		for ( int x = 0; x < numberOfLetters; x++ ) {
			currentChar = inputCharArray[x];
			if ( usedChars[currentChar - '?'] == true ) continue;
			removeCharFromArray(inputCharArray, x, numberOfLetters);
			if ( currentChar == '?' ) {
				for ( int y = 'A'; y <= 'Z'; y++ ) {
					holder = "-----------------------------\n";
					holder += hashAnagramRecurse(y - '@', theRoot_WTEOBL_Array[y - '@'], (char)(y + LOWER_IT), workingCharArray, 0, inputCharArray, numberOfLetters - 1, numberOfWords, theNodeCounts);
					traversalResult += holder;
				}
			}
			else {
				holder = "-----------------------------\n";
				holder += hashAnagramRecurse(currentChar - '@', theRoot_WTEOBL_Array[currentChar - '@'], currentChar, workingCharArray, 0, inputCharArray, numberOfLetters - 1, numberOfWords, theNodeCounts);
				traversalResult += holder;
			}
			insertCharIntoArray(inputCharArray, x, currentChar, numberOfLetters);
			usedChars[currentChar - '?'] = true;
		}
		for ( int x = 0; x < MAX; x++ ) {
			nodeCheck[x] = "| " + theNodeCounts[x] + " |";
			if ( theNodeCounts[x] != 0 )nodeCheck[x]+= " Nodes " + "<B>" + (char)0X2713 + "</B>";
		}
		
		traversalResult = "Anagramming this:  |" + upperAnagramString + "|\nResults in |" + numberOfWords[0] + "| words.\n" + traversalResult;
		return traversalResult;
	}
	
	private String booleanPatternSearchRecurse(int thisIndex, char thisChar, String thisString, char[] toyWithMe, int thisPosition, String[] nodeFills, int[] downForTheCount, int[] nodeTally) {
		char currentChar;
		int firstChildIndex;
		int thisChildListFormat;
		String wordAccumulator = "";
		toyWithMe[thisPosition] = thisChar;
		nodeTally[thisPosition] += 1;
		nodeFills[thisPosition] = "'<B>" + thisChar + "</B>' @ |" + thisIndex +"|";
		if ( thisString.length() == (thisPosition + 1) ) {
			if ( (theNodeArray[thisIndex] & EOW_FLAG) != 0 ) {
				downForTheCount[0] += 1;
				nodeFills[thisPosition] += "- <B>found " + (char)0X2713 + "</B>";
				for ( int x = thisPosition + 1; x < MAX; x++ ) nodeFills[x] = "";
				wordAccumulator = new String(toyWithMe, 0, thisPosition + 1);
				wordAccumulator = "|" + downForTheCount[0] + "| - " + wordAccumulator + "\n";
				return wordAccumulator;
			}
			else {
				nodeFills[thisPosition] += "- lost <B>EOW</B>";
				for ( int x = thisPosition + 1; x < MAX; x++ ) nodeFills[x] = "";
				return wordAccumulator;
			}
		}
		firstChildIndex = (theNodeArray[thisIndex] & CHILD_MASK);
		if ( firstChildIndex != 0 ) {
			thisChildListFormat = theListFormatArray[(theNodeArray[thisIndex] & LIST_FORMAT_INDEX_MASK)>>>LIST_FORMAT_BIT_SHIFT];
			currentChar = thisString.charAt(thisPosition + 1);
			if ( currentChar == '?' ) {
				for ( int x = 'A'; x <= 'Z'; x++ ) {
					if ( (thisChildListFormat & powersOfTwo[x - 'A']) != 0 ){
						wordAccumulator += booleanPatternSearchRecurse(firstChildIndex + listFormatPopCount(thisChildListFormat, x - 'A'), (char)(x + LOWER_IT), thisString, toyWithMe, thisPosition + 1, nodeFills, downForTheCount, nodeTally);
					}
				}
			}
			else if ( (thisChildListFormat & powersOfTwo[currentChar - 'A']) != 0 ){
				wordAccumulator += booleanPatternSearchRecurse(firstChildIndex + listFormatPopCount(thisChildListFormat, currentChar - 'A'), currentChar, thisString, toyWithMe, thisPosition + 1, nodeFills, downForTheCount, nodeTally);
			}
		}
		return wordAccumulator;
	}
	
	private String hashPatternSearchRecurse(int thisIndex, int currentMarker, char thisChar, String thisString, char[] toyWithMe, int thisPosition, String[] nodeFills, int[] downForTheCount, int[] nodeTally) {
		char currentChar;
		int firstChildIndex;
		int thisChildListFormat;
		int nextMarker;
		boolean isChildShort;
		String wordAccumulator = "";
		toyWithMe[thisPosition] = thisChar;
		nodeTally[thisPosition] += 1;
		nodeFills[thisPosition] = "'<B>" + thisChar + "</B>' @ |" + thisIndex +"|";
		if ( (theNodeArray[thisIndex] & EOW_FLAG) != 0 ) currentMarker -= 1;
		nodeFills[thisPosition] += " Mark-[ " + (totalNumberOfWords - currentMarker) + " ]";
		if ( thisString.length() == (thisPosition + 1) ) {
			if ( (theNodeArray[thisIndex] & EOW_FLAG) != 0 ) {
				downForTheCount[0] += 1;
				nodeFills[thisPosition] += " <B>" + (char)0X2713 + "</B>";
				for ( int x = thisPosition + 1; x < MAX; x++ ) nodeFills[x] = "";
				wordAccumulator = new String(toyWithMe, 0, thisPosition + 1);
				wordAccumulator = "|" + downForTheCount[0] + "| - " + wordAccumulator + " [ " + (totalNumberOfWords - currentMarker) + " ]\n";
				return wordAccumulator;
			}
			else {
				nodeFills[thisPosition] += " <B>X</B>";
				for ( int x = thisPosition + 1; x < MAX; x++ ) nodeFills[x] = "";
				return wordAccumulator;
			}
		}
		firstChildIndex = (theNodeArray[thisIndex] & CHILD_MASK);
		if ( firstChildIndex != 0 ) {
			isChildShort = ( firstChildIndex < short_WTEOBL_ArraySize ) ? true: false;
			if ( isChildShort ) currentMarker -= theShort_WTEOBL_Array[firstChildIndex];
			else currentMarker -= (theByte_WTEOBL_Array[firstChildIndex - short_WTEOBL_ArraySize] & 0XFF);
			thisChildListFormat = theListFormatArray[(theNodeArray[thisIndex] & LIST_FORMAT_INDEX_MASK)>>>LIST_FORMAT_BIT_SHIFT];
			currentChar = thisString.charAt(thisPosition + 1);
			if ( currentChar == '?' ) {
				for ( int x = 'A'; x <= 'Z'; x++ ) {
					if ( (thisChildListFormat & powersOfTwo[x - 'A']) != 0 ){
						thisIndex = firstChildIndex + listFormatPopCount(thisChildListFormat, x - 'A');
						if ( isChildShort ) nextMarker = currentMarker + theShort_WTEOBL_Array[thisIndex];
						// When reading "theByte_WTEOBL_Array", it is necessary to mask the values with "0XFF" to cast them as unsigned.
						else nextMarker = currentMarker + (theByte_WTEOBL_Array[thisIndex - short_WTEOBL_ArraySize] & 0XFF);
						wordAccumulator += hashPatternSearchRecurse(thisIndex, nextMarker, (char)(x + LOWER_IT), thisString, toyWithMe, thisPosition + 1, nodeFills, downForTheCount, nodeTally);
					}
				}
			}
			else if ( (thisChildListFormat & powersOfTwo[currentChar - 'A']) != 0 ){
				thisIndex = firstChildIndex + listFormatPopCount(thisChildListFormat, currentChar - 'A');
				if ( isChildShort ) nextMarker = currentMarker + theShort_WTEOBL_Array[thisIndex];
				// When reading "theByte_WTEOBL_Array", it is necessary to mask the values with "0XFF" to cast them as unsigned.
				else nextMarker = currentMarker + (theByte_WTEOBL_Array[thisIndex - short_WTEOBL_ArraySize] & 0XFF);
				wordAccumulator += hashPatternSearchRecurse(thisIndex, nextMarker, currentChar, thisString, toyWithMe, thisPosition + 1, nodeFills, downForTheCount, nodeTally);
			}
		}
		return wordAccumulator;
	}
	
	public String patternSearch(boolean includeHash, String theInputString,String[] nodeFlags, int[] countEmUp, int[] nodeCountSpread) {
		String holder;
		String upperInputString = theInputString.toUpperCase();
		String traversalResult = "";
		char[] workingCharArray = new char[MAX];
		char currentChar = upperInputString.charAt(0);
		
		if ( currentChar == '?' ) {
			for ( int x = 'A'; x <= 'Z'; x++ ) {
				if ( !includeHash ) holder = booleanPatternSearchRecurse(x - '@', (char)(x + LOWER_IT), upperInputString, workingCharArray, 0, nodeFlags, countEmUp, nodeCountSpread);
				else holder = hashPatternSearchRecurse(x - '@', theRoot_WTEOBL_Array[x - '@'], (char)(x + LOWER_IT), upperInputString, workingCharArray, 0, nodeFlags, countEmUp, nodeCountSpread);
				traversalResult += holder;
			}
		}
		else {
			if ( !includeHash ) holder = booleanPatternSearchRecurse(currentChar - '@', currentChar, upperInputString, workingCharArray, 0, nodeFlags, countEmUp, nodeCountSpread);
			else holder = hashPatternSearchRecurse(currentChar - '@', theRoot_WTEOBL_Array[currentChar - '@'], currentChar, upperInputString, workingCharArray, 0, nodeFlags, countEmUp, nodeCountSpread);
			traversalResult += holder;
		}
		for ( int x = 0; x < MAX; x++ ) {
			if ( nodeCountSpread[x] > 1 ) {
				nodeFlags[x] = "| " + nodeCountSpread[x] + " | Nodes " + "<B>" + (char)0X2713 + "</B>";
			}
		}
		
		traversalResult = "Pattern Searching this:  |" + upperInputString + "|\nGenerates |" + countEmUp[0] + "| words.\n" + traversalResult;
		return traversalResult;
	}
	
	private void removeCharFromArray(char[] thisArray, int thisPosition, int size) {
		System.arraycopy(thisArray, thisPosition + 1, thisArray, thisPosition, (size - thisPosition - 1));
	}
	
	private void insertCharIntoArray(char[] thisArray, int thisPosition, char thisChar, int size) {
		System.arraycopy(thisArray, thisPosition, thisArray, thisPosition + 1, (size - thisPosition - 1));
		thisArray[thisPosition] = thisChar;
	}
	
	private int listFormatPopCount(int completeChildList, int LetterPosition){
		int result = 0;
		completeChildList &= childListMasks[LetterPosition];
		
		switch (LetterPosition) {
		case 25:
		case 24:
			result += popCountTable[(completeChildList & 0XFF000000)>>>24];
		case 23:
		case 22:
		case 21:
		case 20:
		case 19:
		case 18:
		case 17:
		case 16:
			result += popCountTable[(completeChildList & 0XFF0000)>>>16];
		case 15:
		case 14:
		case 13:
		case 12:
		case 11:
		case 10:
		case 9:
		case 8:
			result += popCountTable[(completeChildList & 0XFF00)>>>8];
		case 7:
		case 6:
		case 5:
		case 4:
		case 3:
		case 2:
		case 1:
			result += popCountTable[(completeChildList & 0XFF)];
		case 0:
			
		}
		return result;
	}
	
	public static boolean startsWith(Cwg c, String prefix, int[] wordCount, int[] nodeCounterArray, String[] m) {
		wordCount[0] = 0;
		c.patternSearch(false, prefix+"?", m, wordCount, nodeCounterArray);
		return wordCount[0] > 0 || m[prefix.length()] != null;
	}
	
	public static boolean isWord(Cwg c, String word, boolean[] searchResult, String[] m, int[] nodeCounter) {
		searchResult[0] = false;
		c.searchForString(word.toUpperCase(), m, searchResult, nodeCounter);
		return searchResult[0];
	}
	
	public static List<Match> wordsIn(Cwg c, StringBuffer sb, boolean[] searchResult, String[] m, int[] nodeCounter, int[] wordCount, int[] nodeCounterArray) {

		
		//System.out.println(sb);
		List<Match> words = new ArrayList<Match>();
		for (int i=0; i<sb.length()-(Settings.L_MIN+1); i++) {
			for (int L=Settings.L_MIN;L<=Settings.L_MAX;L++) {
				if (i+L>sb.length()) break;
				String prefix = sb.substring(i,i+L).toUpperCase();
				if (prefix.contains("?")) continue;
				//System.out.println("prefix " + prefix);
				if (isWord(c,prefix, searchResult, m, nodeCounter)) {
					//System.out.println(" - is word!");
					Match match = new Match();
					match.word = prefix;
					match.position = i;
					words.add(match);
				}
				//int starts = startsWith(c, prefix);
				//System.out.println("- words with this prefix: " + starts);
				int[] nodeCounterArray2 = new int[MAX];
				String[] m2 = new String[MAX];
				int[] wordCount2 = { 0 };
				boolean s = startsWith(c, prefix, wordCount2, nodeCounterArray2, m2);
				//System.out.println("s " + s);
				//for (int ii=0; ii<m2.length; ii++) System.out.println("m2 " + ii + " " + m2[ii]);
				if (!s) {
					break;
				}
				
			}
		}
		return words;
	}
	
	public static void test() {
		try {
			int MAX = 15;
			Cwg c = new Cwg();
			
			/*System.out.println(startsWith(c, "sme"));
			System.out.println(startsWith(c, "the"));
			System.out.println(startsWith(c, "sam"));
			System.out.println(startsWith(c, "col"));
			System.out.println(startsWith(c, "ili"));
			System.out.println(startsWith(c, "gpe"));
			
			System.out.println(isWord(c, "smell"));
			System.out.println(isWord(c, "iliklsdf"));*/

			Settings.DEBUG = true;
			//public static List<Match> wordsIn(Cwg c, StringBuffer sb, boolean[] searchResult, String[] m, int[] nodeCounter) {
			
			boolean[] searchResult = { false };
			int[] nodeCounter = { 0 };
			int[] nodeCounterArray = new int[MAX];
			String[] m = new String[MAX];
			int[] wordCount = { 0 };
			
			StringBuffer sb;
			sb = new StringBuffer("ilikekillingpeoplebecauseitissomuchfunitismorefunthankillingwildgameintheforrestbecausemanisthemoatdangertueanamalofalltokillsomethinggivesmethemoatthrillingexperenceitisevenbetterthangettingyourrocksoffwithagirlthebestpartofitiathaewhenidieiwillbereborninparadicesndalltheihavekilledwillbecomemyslavesiwillnotgiveyoumynamebecauseyouwilltrytosloidownorstopmycollectingofslavesformyafterlifeebeorietemethhpiti"); testDump(c, sb, wordsIn(c, sb,searchResult, m, nodeCounter, wordCount, nodeCounterArray));
			sb = new StringBuffer("anakekannasmveovnepecifdeataddolfchufsatadloreufsthiskannasmwangmileastheuorredtpecifdelisadtheloitgismertfeisilinouinntokanndolethasmmabedletheloitthrannasmehveresceatadebespetterthismettasmyofrrockdouuwathimarnthepedtvirtouataithiewhesagaeawannpereporsasvirigacedsginntheahibekannegwannpecolelydnibedawannsotmabeyoflysilepecifdeyofwanntrytodnoagowsordtovlyconnectasmoudnibeduorlyiuternaueepeoraetelethhvata"); testDump(c, sb, wordsIn(c, sb,searchResult, m, nodeCounter, wordCount, nodeCounterArray));
			sb = new StringBuffer("anakekannasmveovnepeciudeataddoluchfusatadlorefusthiskannasmwangmileastheforredtpeciudelisadtheloitgismertueisilinofinntokanndolethasmmabedletheloitthrannasmehveresceatadebespetterthismettasmyourrockdoffwathimarnthepedtvirtofataithiewhesagaeawannpereporsasvirigacedsginntheahibekannegwannpecolelydnibedawannsotmabeyoulysilepeciudeyouwanntrytodnoagowsordtovlyconnectasmofdnibedforlyifternafeepeoraetelethhvata"); testDump(c, sb, wordsIn(c, sb,searchResult, m, nodeCounter, wordCount, nodeCounterArray));
			sb = new StringBuffer("anakekannasmveovnepeciudeataddoluchfusatadlorefusthiskannasmwangmileastheforredtpeciudelisadtheloitgismertueisilinofinntokanndolethasmmabedletheloitthrannasmeiveresceatadebespetterthismettasmyourrockdoffwathimarnthepedtvirtofataithiewhesagaeawannpereporsasvirigacedsginntheahibekannegwannpecolelydnibedawannsotmabeyoulysilepeciudeyouwanntrytodnoagowsordtovlyconnectasmofdnibedforlyifternafeepeoraetelethhvata"); testDump(c, sb, wordsIn(c, sb,searchResult, m, nodeCounter, wordCount, nodeCounterArray));
			sb = new StringBuffer("anakekannasmveovnepeciudeataddoluchfusatadlorefusthiskannasmwangmileastheforredtpeciudelisadtheloitgismertueisilinofinntokanndolethasmmabedletheloitthrannasmeuveresceatadebespetterthismettasmyourrockdoffwathimarnthepedtvirtofataithiewhesagaeawannpereporsasvirigacedsginntheahibekannegwannpecolelydnibedawannsotmabeyoulysilepeciudeyouwanntrytodnoagowsordtovlyconnectasmofdnibedforlyifternafeepeoraetelethhvata"); testDump(c, sb, wordsIn(c, sb,searchResult, m, nodeCounter, wordCount, nodeCounterArray));
			sb = new StringBuffer("anayeyannaslveovnewefiureatarrocufhmusatarcodemusthisyannaslpangliceasthemoddertwefiurecisarthecoitgisledtueisicinominntoyannrocethasllabercethecoitthdannaslekvedesfeatarebeswettedthislettaslhouddofyrommpathiladnthewertvidtomataithiephesagaeapannwedewodsasvidigafersginntheahibeyannegpannwefocechrniberapannsotlabehouchsicewefiurehoupanntdhtornoagopsodrtovchfonneftaslomrnibermodchimtednameeweodaetecethhvata"); testDump(c, sb, wordsIn(c, sb,searchResult, m, nodeCounter, wordCount, nodeCounterArray));
			sb = new StringBuffer("anayeyannaslveovnewefiureatarrocufhmusatarcodemusthisyannaslpangliceasthemoddertwefiurecisarthecoitgisledtueisicinominntoyannrocethasllabercethecoitthdannaslelvedesfeatarebeswettedthislettaslhouddofyrommpathiladnthewertvidtomataithiephesagaeapannwedewodsasvidigafersginntheahibeyannegpannwefocechrniberapannsotlabehouchsicewefiurehoupanntdhtornoagopsodrtovchfonneftaslomrnibermodchimtednameeweodaetecethhvata"); testDump(c, sb, wordsIn(c, sb,searchResult, m, nodeCounter, wordCount, nodeCounterArray));
			sb = new StringBuffer("anayeyannaslveovnewefiureatarrocufhmusatarcodemusthisyannaslpangliceasthemoddertwefiurecisarthecoitgisledtueisicinominntoyannrocethasllabercethecoitthdannasleuvedesfeatarebeswettedthislettaslhouddofyrommpathiladnthewertvidtomataithiephesagaeapannwedewodsasvidigafersginntheahibeyannegpannwefocechrniberapannsotlabehouchsicewefiurehoupanntdhtornoagopsodrtovchfonneftaslomrnibermodchimtednameeweodaetecethhvata"); testDump(c, sb, wordsIn(c, sb,searchResult, m, nodeCounter, wordCount, nodeCounterArray));
			sb = new StringBuffer("asakekassangveovsepeciudeataddoluchfunatadlorefunthinkassangmaswgileantheforredtpeciudelinadtheloitwingertueinilisofisstokassdolethanggabedletheloitthrassangehverenceatadebenpetterthingettangyourrockdoffmathigarsthepedtvirtofataithiemhenawaeamassperepornanviriwacednwisstheahibekassewmasspecolelydsibedamassnotgabeyoulynilepeciudeyoumasstrytodsoawomnordtovlycossectangofdsibedforlyiftersafeepeoraetelethhvata"); testDump(c, sb, wordsIn(c, sb,searchResult, m, nodeCounter, wordCount, nodeCounterArray));
			sb = new StringBuffer("asakekassangveovsepeciudeataddoluchfunatadlorefunthinkassangwasmgileantheforredtpeciudelinadtheloitmingertueinilisofisstokassdolethanggabedletheloitthrassangehverenceatadebenpetterthingettangyourrockdoffwathigarsthepedtvirtofataithiewhenamaeawassperepornanvirimacednmisstheahibekassemwasspecolelydsibedawassnotgabeyoulynilepeciudeyouwasstrytodsoamownordtovlycossectangofdsibedforlyiftersafeepeoraetelethhvata"); testDump(c, sb, wordsIn(c, sb,searchResult, m, nodeCounter, wordCount, nodeCounterArray));
			sb = new StringBuffer("asakekassangveovsepeciudeataddoluchfunatadlorefunthinkassangwasmgileantheforredtpeciudelinadtheloitmingertueinilisofisstokassdolethanggabedletheloitthrassangeiverenceatadebenpetterthingettangyourrockdoffwathigarsthepedtvirtofataithiewhenamaeawassperepornanvirimacednmisstheahibekassemwasspecolelydsibedawassnotgabeyoulynilepeciudeyouwasstrytodsoamownordtovlycossectangofdsibedforlyiftersafeepeoraetelethhvata"); testDump(c, sb, wordsIn(c, sb,searchResult, m, nodeCounter, wordCount, nodeCounterArray));
			sb = new StringBuffer("asakekassangveovsepeciudeataddoluchfunatadlorefunthinkassangwasmgileantheforredtpeciudelinadtheloitmingertueinilisofisstokassdolethanggabedletheloitthrassangesverenceatadebenpetterthingettangyourrockdoffwathigarsthepedtvirtofataithiewhenamaeawassperepornanvirimacednmisstheahibekassemwasspecolelydsibedawassnotgabeyoulynilepeciudeyouwasstrytodsoamownordtovlycossectangofdsibedforlyiftersafeepeoraetelethhvata"); testDump(c, sb, wordsIn(c, sb,searchResult, m, nodeCounter, wordCount, nodeCounterArray));
			sb = new StringBuffer("asakekassangveovsepeciudeataddoluchfunatadlorefunthinkassangwasmgileantheforredtpeciudelinadtheloitmingertueinilisofisstokassdolethanggabedletheloitthrassangeuverenceatadebenpetterthingettangyourrockdoffwathigarsthepedtvirtofataithiewhenamaeawassperepornanvirimacednmisstheahibekassemwasspecolelydsibedawassnotgabeyoulynilepeciudeyouwasstrytodsoamownordtovlycossectangofdsibedforlyiftersafeepeoraetelethhvata"); testDump(c, sb, wordsIn(c, sb,searchResult, m, nodeCounter, wordCount, nodeCounterArray));
			sb = new StringBuffer("asayeyassanlveovsewefiureatarrocufhmunatarcodemunthinyassanlpasgliceanthemoddertwefiurecinarthecoitginledtueinicisomisstoyassrocethanllabercethecoitthdassanleivedenfeatarebenwettedthinlettanlhouddofyrommpathiladsthewertvidtomataithiephenagaeapasswedewodnanvidigaferngisstheahibeyassegpasswefocechrsiberapassnotlabehouchnicewefiurehoupasstdhtorsoagopnodrtovchfosseftanlomrsibermodchimtedsameeweodaetecethhvata"); testDump(c, sb, wordsIn(c, sb,searchResult, m, nodeCounter, wordCount, nodeCounterArray));
			sb = new StringBuffer("asayeyassanlveovsewefiureatarrocufhmunatarcodemunthinyassanlpasgliceanthemoddertwefiurecinarthecoitginledtueinicisomisstoyassrocethanllabercethecoitthdassanleuvedenfeatarebenwettedthinlettanlhouddofyrommpathiladsthewertvidtomataithiephenagaeapasswedewodnanvidigaferngisstheahibeyassegpasswefocechrsiberapassnotlabehouchnicewefiurehoupasstdhtorsoagopnodrtovchfosseftanlomrsibermodchimtedsameeweodaetecethhvata"); testDump(c, sb, wordsIn(c, sb,searchResult, m, nodeCounter, wordCount, nodeCounterArray));
			sb = new StringBuffer("isikekissingveovsepecaudeitiddoluchfunitidlorefunthankissingwismgaleintheforredtpecaudelanidtheloatmangertueanalasofasstokissdolethinggibedletheloatthrissingesverenceitidebenpetterthangettingyourrockdoffwithagirsthepedtvartofitiathaewhenimieiwisspereporninvaramicednmasstheihabekissemwisspecolelydsabediwissnotgibeyoulynalepecaudeyouwisstrytodsoimownordtovlycossectingofdsabedforlyaftersifeepeorietelethhviti"); testDump(c, sb, wordsIn(c, sb,searchResult, m, nodeCounter, wordCount, nodeCounterArray));
			sb = new StringBuffer("totbebtootnuneinoepemswhetathhidwmlcwntathdirecwnalsnbtootnuftoyusdetnalecirrehapemswhedsnthaledisaysnuerawesnsdsoicsooaibtoohidealtnuutvehdealedisaalrtootnuernerenmetathevenpeaaeralsnueaatnugiwrrimbhiccftalsutroalepehansraictatsalseflentytetftooperepirntnnsrsytmehnysooaletlsvebtooeyftoopemidedghosvehtftooniautvegiwdgnsdepemswhegiwftooargaihoityifnirhaindgmiooematnuichosvehcirdgscaerotceepeirteaedeallntat"); testDump(c, sb, wordsIn(c, sb,searchResult, m, nodeCounter, wordCount, nodeCounterArray));
			sb = new StringBuffer("totbebtootsuheihoepemnchetathhidcmlwcstathdirewcsalnsbtootsuftoyundetsalewirrehapemnchednsthaledinaynsueracensndnoiwnooaibtoohidealtsuutvehdealedinaalrtootsuerheresmetathevespeaaeralnsueaatsugicrrimbhiwwftalnutroalepehahnraiwtatnalneflestytetftooperepirstshnrnytmehsynooaletlnvebtooeyftoopemidedghonvehtftoosiautvegicdgsndepemnchegicftooargaihoityifsirhaihdgmiooematsuiwhonvehwirdgnwaerotweepeirteaedeallhtat"); testDump(c, sb, wordsIn(c, sb,searchResult, m, nodeCounter, wordCount, nodeCounterArray));
			sb = new StringBuffer("totbebtootsuheihoepemnwhetathhidwmlcwstathdirecwsalnsbtootsuftoyundetsalecirrehapemnwhednsthaledinaynsuerawensndnoicnooaibtoohidealtsuutvehdealedinaalrtootsuekheresmetathevespeaaeralnsueaatsugiwrrimbhiccftalnutroalepehahnraictatnalneflestytetftooperepirstshnrnytmehsynooaletlnvebtooeyftoopemidedghonvehtftoosiautvegiwdgsndepemnwhegiwftooargaihoityifsirhaihdgmiooematsuichonvehcirdgncaerotceepeirteaedeallhtat"); testDump(c, sb, wordsIn(c, sb,searchResult, m, nodeCounter, wordCount, nodeCounterArray));
			sb = new StringBuffer("totbebtootsuheihoepemnwhetathhidwmlcwstathdirecwsalnsbtootsuftoyundetsalecirrehapemnwhednsthaledinaynsuerawensndnoicnooaibtoohidealtsuutvehdealedinaalrtootsuerheresmetathevespeaaeralnsueaatsugiwrrimbhiccftalnutroalepehahnraictatnalneflestytetftooperepirstshnrnytmehsynooaletlnvebtooeyftoopemidedghonvehtftoosiautvegiwdgsndepemnwhegiwftooargaihoityifsirhaihdgmiooematsuichonvehcirdgncaerotceepeirteaedeallhtat"); testDump(c, sb, wordsIn(c, sb,searchResult, m, nodeCounter, wordCount, nodeCounterArray));
			sb = new StringBuffer("totbebtootsukeikoefemnwhetathhidwmlcwstathdirecwsalnsbtootsuptoyundetsalecirrehafemnwhednsthaledinaynsuerawensndnoicnooaibtoohidealtsuutvehdealedinaalrtootsuelkeresmetathevesfeaaeralnsueaatsugiwrrimbhiccptalnutroalefehaknraictatnalneplestytetptooferefirstsknrnytmehsynooaletlnvebtooeyptoofemidedghonvehtptoosiautvegiwdgsndefemnwhegiwptooargaihoityipsirhaikdgmiooematsuichonvehcirdgncaerotceefeirteaedeallktat"); testDump(c, sb, wordsIn(c, sb,searchResult, m, nodeCounter, wordCount, nodeCounterArray));
			sb = new StringBuffer("totbebtootsukeikoefemnwhetathhidwmlcwstathdirecwsalnsbtootsuptoyundetsalecirrehafemnwhednsthaledinaynsuerawensndnoicnooaibtoohidealtsuutvehdealedinaalrtootsuerkeresmetathevesfeaaeralnsueaatsugiwrrimbhiccptalnutroalefehaknraictatnalneplestytetptooferefirstsknrnytmehsynooaletlnvebtooeyptoofemidedghonvehtptoosiautvegiwdgsndefemnwhegiwptooargaihoityipsirhaikdgmiooematsuichonvehcirdgncaerotceefeirteaedeallktat"); testDump(c, sb, wordsIn(c, sb,searchResult, m, nodeCounter, wordCount, nodeCounterArray));
			sb = new StringBuffer("totbebtootsukeikoefemnwhetathhidwmlcwstathdirecwsalnsbtootsuptoyundetsalecirrehafemnwhednsthaledinaynsuerawensndnoicnooaibtoohidealtsuutvehdealedinaalrtootsueskeresmetathevesfeaaeralnsueaatsugiwrrimbhiccptalnutroalefehaknraictatnalneplestytetptooferefirstsknrnytmehsynooaletlnvebtooeyptoofemidedghonvehtptoosiautvegiwdgsndefemnwhegiwptooargaihoityipsirhaikdgmiooematsuichonvehcirdgncaerotceefeirteaedeallktat"); testDump(c, sb, wordsIn(c, sb,searchResult, m, nodeCounter, wordCount, nodeCounterArray));
			sb = new StringBuffer("totbebtootsukeikoepemnwhetathhidwmlcwstathdirecwsalnsbtootsuftoyundetsalecirrehapemnwhednsthaledinaynsuerawensndnoicnooaibtoohidealtsuutvehdealedinaalrtootsuenkeresmetathevespeaaeralnsueaatsugiwrrimbhiccftalnutroalepehaknraictatnalneflestytetftooperepirstsknrnytmehsynooaletlnvebtooeyftoopemidedghonvehtftoosiautvegiwdgsndepemnwhegiwftooargaihoityifsirhaikdgmiooematsuichonvehcirdgncaerotceepeirteaedeallktat"); testDump(c, sb, wordsIn(c, sb,searchResult, m, nodeCounter, wordCount, nodeCounterArray));
			sb = new StringBuffer("totbebtootsukeikoepemnwhetathhidwmlcwstathdirecwsalnsbtootsuftoyundetsalecirrehapemnwhednsthaledinaynsuerawensndnoicnooaibtoohidealtsuutvehdealedinaalrtootsueokeresmetathevespeaaeralnsueaatsugiwrrimbhiccftalnutroalepehaknraictatnalneflestytetftooperepirstsknrnytmehsynooaletlnvebtooeyftoopemidedghonvehtftoosiautvegiwdgsndepemnwhegiwftooargaihoityifsirhaikdgmiooematsuichonvehcirdgncaerotceepeirteaedeallktat"); testDump(c, sb, wordsIn(c, sb,searchResult, m, nodeCounter, wordCount, nodeCounterArray));
			sb = new StringBuffer("totbebtootsukeikoepemnwhetathhidwmlcwstathdirecwsalnsbtootsuftoyundetsalecirrehapemnwhednsthaledinaynsuerawensndnoicnooaibtoohidealtsuutvehdealedinaalrtootsuerkeresmetathevespeaaeralnsueaatsugiwrrimbhiccftalnutroalepehaknraictatnalneflestytetftooperepirstsknrnytmehsynooaletlnvebtooeyftoopemidedghonvehtftoosiautvegiwdgsndepemnwhegiwftooargaihoityifsirhaikdgmiooematsuichonvehcirdgncaerotceepeirteaedeallktat"); testDump(c, sb, wordsIn(c, sb,searchResult, m, nodeCounter, wordCount, nodeCounterArray));
			sb = new StringBuffer("totbebtootsukeikoepemnwhetathhidwmlcwstathdirecwsalnsbtootsuftoyundetsalecirrehapemnwhednsthaledinaynsuerawensndnoicnooaibtoohidealtsuutvehdealedinaalrtootsueskeresmetathevespeaaeralnsueaatsugiwrrimbhiccftalnutroalepehaknraictatnalneflestytetftooperepirstsknrnytmehsynooaletlnvebtooeyftoopemidedghonvehtftoosiautvegiwdgsndepemnwhegiwftooargaihoityifsirhaikdgmiooematsuichonvehcirdgncaerotceepeirteaedeallktat"); testDump(c, sb, wordsIn(c, sb,searchResult, m, nodeCounter, wordCount, nodeCounterArray));
			sb = new StringBuffer("totbebtootsukeikoepemnwhetathhidwmlcwstathdirecwsalnsbtootsuftoyundetsalecirrehapemnwhednsthaledinaynsuerawensndnoicnooaibtoohidealtsuutvehdealedinaalrtootsuewkeresmetathevespeaaeralnsueaatsugiwrrimbhiccftalnutroalepehaknraictatnalneflestytetftooperepirstsknrnytmehsynooaletlnvebtooeyftoopemidedghonvehtftoosiautvegiwdgsndepemnwhegiwftooargaihoityifsirhaikdgmiooematsuichonvehcirdgncaerotceepeirteaedeallktat"); testDump(c, sb, wordsIn(c, sb,searchResult, m, nodeCounter, wordCount, nodeCounterArray));
			sb = new StringBuffer("totbebtootsukeikoepemnwhetathhidwmlcwstathdirecwsalnsbtootsuftoyundetsalecirrehapemnwhednsthaledinaynsuerawensndnoicnooaibtoohidealtsuutvehdealedinaalrtootsuexkeresmetathevespeaaeralnsueaatsugiwrrimbhiccftalnutroalepehaknraictatnalneflestytetftooperepirstsknrnytmehsynooaletlnvebtooeyftoopemidedghonvehtftoosiautvegiwdgsndepemnwhegiwftooargaihoityifsirhaikdgmiooematsuichonvehcirdgncaerotceepeirteaedeallktat"); testDump(c, sb, wordsIn(c, sb,searchResult, m, nodeCounter, wordCount, nodeCounterArray));
			sb = new StringBuffer("totfeftootswkeikoepevnchetathhidcvlmcstathdiremcsalnsftootswutoywndetsalemirrehapevnchednsthaledinaynsweracensndnoimnooaiftoohidealtswwtbehdealedinaalrtootsweckeresvetathebespeaaeralnsweaatswgicrrivfhimmutalnwtroalepehaknraimtatnalneulestytetutooperepirstsknrnytvehsynooaletlnbeftooeyutoopevidedghonbehtutoosiawtbegicdgsndepevnchegicutooargaihoityiusirhaikdgviooevatswimhonbehmirdgnmaerotmeepeirteaedeallktat"); testDump(c, sb, wordsIn(c, sb,searchResult, m, nodeCounter, wordCount, nodeCounterArray));
			sb = new StringBuffer("tothehtootsureiroepewnmhetathhidmwlcmstathdirecmsalnshtootsuftoyundetsalecirrehapewnmhednsthaledinaynsueramensndnoicnooaihtoohidealtsuutbehdealedinaalrtootsuevrereswetathebespeaaeralnsueaatsugimrriwhhiccftalnutroalepeharnraictatnalneflestytetftooperepirstsrnrnytwehsynooaletlnbehtooeyftoopewidedghonbehtftoosiautbegimdgsndepewnmhegimftooargaihoityifsirhairdgwiooewatsuichonbehcirdgncaerotceepeirteaedeallrtat"); testDump(c, sb, wordsIn(c, sb,searchResult, m, nodeCounter, wordCount, nodeCounterArray));
			sb = new StringBuffer("totpeptootsukeikoebemnwhetathhidwmlcwstathdirecwsalnsptootsuftoyundetsalecirrehabemnwhednsthaledinaynsuerawensndnoicnooaiptoohidealtsuutvehdealedinaalrtootsueokeresmetathevesbeaaeralnsueaatsugiwrrimphiccftalnutroalebehaknraictatnalneflestytetftooberebirstsknrnytmehsynooaletlnveptooeyftoobemidedghonvehtftoosiautvegiwdgsndebemnwhegiwftooargaihoityifsirhaikdgmiooematsuichonvehcirdgncaerotceebeirteaedeallktat"); testDump(c, sb, wordsIn(c, sb,searchResult, m, nodeCounter, wordCount, nodeCounterArray));
			sb = new StringBuffer("totrertootsuheihoepewnchetathhidcwlmcstathdiremcsalnsrtootsuftoyundetsalemirrehapewnchednsthaledinaynsueracensndnoimnooairtoohidealtsuutbehdealedinaalrtootsuekhereswetathebespeaaeralnsueaatsugicrriwrhimmftalnutroalepehahnraimtatnalneflestytetftooperepirstshnrnytwehsynooaletlnbertooeyftoopewidedghonbehtftoosiautbegicdgsndepewnchegicftooargaihoityifsirhaihdgwiooewatsuimhonbehmirdgnmaerotmeepeirteaedeallhtat"); testDump(c, sb, wordsIn(c, sb,searchResult, m, nodeCounter, wordCount, nodeCounterArray));
			sb = new StringBuffer("totrertootsuheihoepewnchetathhidcwlmcstathdiremcsalnsrtootsuftoyundetsalemirrehapewnchednsthaledinaynsueracensndnoimnooairtoohidealtsuutbehdealedinaalrtootsuevhereswetathebespeaaeralnsueaatsugicrriwrhimmftalnutroalepehahnraimtatnalneflestytetftooperepirstshnrnytwehsynooaletlnbertooeyftoopewidedghonbehtftoosiautbegicdgsndepewnchegicftooargaihoityifsirhaihdgwiooewatsuimhonbehmirdgnmaerotmeepeirteaedeallhtat"); testDump(c, sb, wordsIn(c, sb,searchResult, m, nodeCounter, wordCount, nodeCounterArray));
			sb = new StringBuffer("totrertootsuheihoepewnchetathhircwlmcstathridemcsalnsrtootsuftoyunretsalemiddehapewnchernsthalerinaynsuedacensnrnoimnooairtoohirealtsuutbehrealerinaaldtootsuevhedeswetathebespeaaedalnsueaatsugicddiwrhimmftalnutdoalepehahndaimtatnalneflestytetftoopedepidstshndnytwehsynooaletlnbertooeyftoopewirerghonbehtftoosiautbegicrgsnrepewnchegicftooadgaihoityifsidhaihrgwiooewatsuimhonbehmidrgnmaedotmeepeidteaereallhtat"); testDump(c, sb, wordsIn(c, sb,searchResult, m, nodeCounter, wordCount, nodeCounterArray));
			sb = new StringBuffer("totrertootsuheihoepewnmhetathhidmwlcmstathdirecmsalnsrtootsuftoyundetsalecirrehapewnmhednsthaledinaynsueramensndnoicnooairtoohidealtsuutbehdealedinaalrtootsuekhereswetathebespeaaeralnsueaatsugimrriwrhiccftalnutroalepehahnraictatnalneflestytetftooperepirstshnrnytwehsynooaletlnbertooeyftoopewidedghonbehtftoosiautbegimdgsndepewnmhegimftooargaihoityifsirhaihdgwiooewatsuichonbehcirdgncaerotceepeirteaedeallhtat"); testDump(c, sb, wordsIn(c, sb,searchResult, m, nodeCounter, wordCount, nodeCounterArray));
			sb = new StringBuffer("totvevtootscheihoepewnuhetathhiduwlmustathdiremusalnsvtootscftoycndetsalemirrehapewnuhednsthaledinaynscerauensndnoimnooaivtoohidealtscctbehdealedinaalrtootscekhereswetathebespeaaeralnsceaatscgiurriwvhimmftalnctroalepehahnraimtatnalneflestytetftooperepirstshnrnytwehsynooaletlnbevtooeyftoopewidedghonbehtftoosiactbegiudgsndepewnuhegiuftooargaihoityifsirhaihdgwiooewatscimhonbehmirdgnmaerotmeepeirteaedeallhtat"); testDump(c, sb, wordsIn(c, sb,searchResult, m, nodeCounter, wordCount, nodeCounterArray));
			sb = new StringBuffer("totvevtootscreiroepewnuhetathhiduwlmustathdiremusalnsvtootscftoycndetsalemirrehapewnuhednsthaledinaynscerauensndnoimnooaivtoohidealtscctbehdealedinaalrtootscekrereswetathebespeaaeralnsceaatscgiurriwvhimmftalnctroalepeharnraimtatnalneflestytetftooperepirstsrnrnytwehsynooaletlnbevtooeyftoopewidedghonbehtftoosiactbegiudgsndepewnuhegiuftooargaihoityifsirhairdgwiooewatscimhonbehmirdgnmaerotmeepeirteaedeallrtat"); testDump(c, sb, wordsIn(c, sb,searchResult, m, nodeCounter, wordCount, nodeCounterArray));
			sb = new StringBuffer("totvevtootsuheihoepewnchetathhidcwlmcstathdiremcsalnsvtootsuftoyundetsalemirrehapewnchednsthaledinaynsueracensndnoimnooaivtoohidealtsuutbehdealedinaalrtootsuekhereswetathebespeaaeralnsueaatsugicrriwvhimmftalnutroalepehahnraimtatnalneflestytetftooperepirstshnrnytwehsynooaletlnbevtooeyftoopewidedghonbehtftoosiautbegicdgsndepewnchegicftooargaihoityifsirhaihdgwiooewatsuimhonbehmirdgnmaerotmeepeirteaedeallhtat"); testDump(c, sb, wordsIn(c, sb,searchResult, m, nodeCounter, wordCount, nodeCounterArray));
			sb = new StringBuffer("totvevtootsureiroepemnwhetathhidwmlcwstathdirecwsalnsvtootsuftoyundetsalecirrehapemnwhednsthaledinaynsuerawensndnoicnooaivtoohidealtsuutbehdealedinaalrtootsuehreresmetathebespeaaeralnsueaatsugiwrrimvhiccftalnutroalepeharnraictatnalneflestytetftooperepirstsrnrnytmehsynooaletlnbevtooeyftoopemidedghonbehtftoosiautbegiwdgsndepemnwhegiwftooargaihoityifsirhairdgmiooematsuichonbehcirdgncaerotceepeirteaedeallrtat"); testDump(c, sb, wordsIn(c, sb,searchResult, m, nodeCounter, wordCount, nodeCounterArray));
			sb = new StringBuffer("totvevtootsureiroepemnwhetathhidwmlcwstathdirecwsalnsvtootsuftoyundetsalecirrehapemnwhednsthaledinaynsuerawensndnoicnooaivtoohidealtsuutbehdealedinaalrtootsuekreresmetathebespeaaeralnsueaatsugiwrrimvhiccftalnutroalepeharnraictatnalneflestytetftooperepirstsrnrnytmehsynooaletlnbevtooeyftoopemidedghonbehtftoosiautbegiwdgsndepemnwhegiwftooargaihoityifsirhairdgmiooematsuichonbehcirdgncaerotceepeirteaedeallrtat"); testDump(c, sb, wordsIn(c, sb,searchResult, m, nodeCounter, wordCount, nodeCounterArray));
			sb = new StringBuffer("totvevtootsureiroepewnchetathhidcwlmcstathdiremcsalnsvtootsuftoyundetsalemirrehapewnchednsthaledinaynsueracensndnoimnooaivtoohidealtsuutbehdealedinaalrtootsuekrereswetathebespeaaeralnsueaatsugicrriwvhimmftalnutroalepeharnraimtatnalneflestytetftooperepirstsrnrnytwehsynooaletlnbevtooeyftoopewidedghonbehtftoosiautbegicdgsndepewnchegicftooargaihoityifsirhairdgwiooewatsuimhonbehmirdgnmaerotmeepeirteaedeallrtat"); testDump(c, sb, wordsIn(c, sb,searchResult, m, nodeCounter, wordCount, nodeCounterArray));
			sb = new StringBuffer("totvevtootswkeikoepefnchetathhidcflmcstathdiremcsalnsvtootswutoywndetsalemirrehapefnchednsthaledinaynsweracensndnoimnooaivtoohidealtswwtbehdealedinaalrtootsweckeresfetathebespeaaeralnsweaatswgicrrifvhimmutalnwtroalepehaknraimtatnalneulestytetutooperepirstsknrnytfehsynooaletlnbevtooeyutoopefidedghonbehtutoosiawtbegicdgsndepefnchegicutooargaihoityiusirhaikdgfiooefatswimhonbehmirdgnmaerotmeepeirteaedeallktat"); testDump(c, sb, wordsIn(c, sb,searchResult, m, nodeCounter, wordCount, nodeCounterArray));
			sb = new StringBuffer("tstbebtsstnukeiksepemowhetathhidwmlcwntathdirecwnalonbtsstnuftsyuodetnalecirrehapemowhedonthaledioayonueraweonodosicossaibtsshidealtnuutvehdealedioaalrtsstnuewkerenmetathevenpeaaeralonueaatnugiwrrimbhiccftaloutrsalepehakoraictatoaloeflentytetftssperepirntnkoroytmehnyossaletlovebtsseyftsspemidedghsovehtftssniautvegiwdgnodepemowhegiwftssargaihsityifnirhaikdgmissematnuichsovehcirdgocaerstceepeirteaedeallktat"); testDump(c, sb, wordsIn(c, sb,searchResult, m, nodeCounter, wordCount, nodeCounterArray));
			sb = new StringBuffer("tstpeptsstnukeiksebemowhetathhidwmlcwntathdirecwnalonptsstnuftsyuodetnalecirrehabemowhedonthaledioayonueraweonodosicossaiptsshidealtnuutvehdealedioaalrtsstnueokerenmetathevenbeaaeralonueaatnugiwrrimphiccftaloutrsalebehakoraictatoaloeflentytetftssberebirntnkoroytmehnyossaletloveptsseyftssbemidedghsovehtftssniautvegiwdgnodebemowhegiwftssargaihsityifnirhaikdgmissematnuichsovehcirdgocaerstceebeirteaedeallktat"); testDump(c, sb, wordsIn(c, sb,searchResult, m, nodeCounter, wordCount, nodeCounterArray));
			sb = new StringBuffer("tstpeptsstnwkeiksebemouhetathhidumlcuntathdirecunalonptsstnwftsywodetnalecirrehabemouhedonthaledioayonweraueonodosicossaiptsshidealtnwwtvehdealedioaalrtsstnweokerenmetathevenbeaaeralonweaatnwgiurrimphiccftalowtrsalebehakoraictatoaloeflentytetftssberebirntnkoroytmehnyossaletloveptsseyftssbemidedghsovehtftssniawtvegiudgnodebemouhegiuftssargaihsityifnirhaikdgmissematnwichsovehcirdgocaerstceebeirteaedeallktat"); testDump(c, sb, wordsIn(c, sb,searchResult, m, nodeCounter, wordCount, nodeCounterArray));
			sb = new StringBuffer("tstpeptsstnwkeiksebemouhetathhidumlcuntathdirecunalonptsstnwftsywodetnalecirrehabemouhedonthaledioayonweraueonodosicossaiptsshidealtnwwtvehdealedioaalrtsstnweukerenmetathevenbeaaeralonweaatnwgiurrimphiccftalowtrsalebehakoraictatoaloeflentytetftssberebirntnkoroytmehnyossaletloveptsseyftssbemidedghsovehtftssniawtvegiudgnodebemouhegiuftssargaihsityifnirhaikdgmissematnwichsovehcirdgocaerstceebeirteaedeallktat"); testDump(c, sb, wordsIn(c, sb,searchResult, m, nodeCounter, wordCount, nodeCounterArray));
			sb = new StringBuffer("tstpeptsstoukeiksebemnghetathhidgmlcgotathdirecgoalnoptsstouftsyundetoalecirrehabemnghednothaledinaynoueragenondnsicnssaiptsshidealtouutvehdealedinaalrtsstouenkereometatheveobeaaeralnoueaatouwigrrimphiccftalnutrsalebehaknraictatnalnefleotytetftssberebirotoknrnytmehoynssaletlnveptsseyftssbemidedwhsnvehtftssoiautvewigdwondebemnghewigftssarwaihsityifoirhaikdwmissematouichsnvehcirdwncaerstceebeirteaedeallktat"); testDump(c, sb, wordsIn(c, sb,searchResult, m, nodeCounter, wordCount, nodeCounterArray));
			sb = new StringBuffer("tstpeptsstoukeiksebemnwhetathhidwmlcwotathdirecwoalnoptsstouftsyundetoalecirrehabemnwhednothaledinaynouerawenondnsicnssaiptsshidealtouutvehdealedinaalrtsstouewkereometatheveobeaaeralnoueaatougiwrrimphiccftalnutrsalebehaknraictatnalnefleotytetftssberebirotoknrnytmehoynssaletlnveptsseyftssbemidedghsnvehtftssoiautvegiwdgondebemnwhegiwftssargaihsityifoirhaikdgmissematouichsnvehcirdgncaerstceebeirteaedeallktat"); testDump(c, sb, wordsIn(c, sb,searchResult, m, nodeCounter, wordCount, nodeCounterArray));
			sb = new StringBuffer("ssresesesssesessereeeeseessrseseseeesessseesmessreeseesterseesseeresseseseseseeseressseeerseseeesseeseeemsssrsseeessstessertessesseseeeslseeeseresseseesterreesseseeessssesleserseemsereeesssresssesseessesereesesmsseeesesseessesesssereeeseresesesteerseesmsressereseeereessseeseslsesssseerteeeeseresetslessesessseeslesssesesereseesessssesstsmssseesseserssesssesestteeersesseselssesmesseseeersreessesrseesseessss"); testDump(c, sb, wordsIn(c, sb,searchResult, m, nodeCounter, wordCount, nodeCounterArray));

			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static void testDump(Cwg c, StringBuffer sb, List<Match> list) {
		System.out.println(sb);
		Scorer.dumpMatches(list, null);
		Scorer.dumpCoverage(list, sb);
		System.out.println("Score: " + Scorer.score(list));
	}
	
	public static float score(Cwg c, StringBuffer sb, boolean[] searchResult, String[] m, int[] nodeCounter, int[] wordCount, int[] nodeCounterArray) {
		List<Match> list = wordsIn(c, sb, searchResult, m, nodeCounter, wordCount, nodeCounterArray);
		float result = Scorer.score(list);
		//System.out.println(list.size() + " matches, score: " + result + ", " + sb );
		return result;
		
	}

	public static void test2() {
		try {
			int MAX = 15;
			Cwg c = new Cwg();
			
			Settings.DEBUG = true;
			
			boolean[] searchResult = { false };
			int[] nodeCounter = { 0 };
			int[] nodeCounterArray = new int[MAX];
			String[] m = new String[MAX];
			int[] wordCount = { 0 };
			
			//testDump(c, wordsIn(c, new StringBuffer("peop"),searchResult, m, nodeCounter, wordCount, nodeCounterArray));
			System.out.println(searchResult[0]);
			for (int i=0; i<m.length; i++) System.out.println("m " + i + " " + m[i]);
			System.out.println(nodeCounter[0]);
			
			boolean s = startsWith(c, "people", wordCount, nodeCounterArray, m);
			System.out.println("s " + s);
			System.out.println(searchResult[0]);
			for (int i=0; i<m.length; i++) System.out.println("m " + i + " " + m[i]);
			for (int i=0; i<nodeCounterArray.length; i++) System.out.println("nc " + i + " " + nodeCounterArray[i]);
			System.out.println(nodeCounter[0]);
			
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		test();
	}
	
}
