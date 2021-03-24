package com.zodiackillerciphers.tests.mapcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Grammars {

	/**
	 * it is assumed that an optional delimiter exists between each grammar
	 * element
	 */
	public static GrammarElement[][] grammars = new GrammarElement[][] {
			{ GrammarElement.MILS_AMOUNT, GrammarElement.MILS_UNIT,
					GrammarElement.CONJUNCTION, GrammarElement.INCHES_AMOUNT,
					GrammarElement.INCHES_UNIT },
			{ GrammarElement.INCHES_AMOUNT, GrammarElement.INCHES_UNIT,
					GrammarElement.CONJUNCTION, GrammarElement.MILS_AMOUNT,
					GrammarElement.MILS_UNIT } };

	public static String[] mils = new String[] { "MILS", "MIL", "MILLIRADIAN",
			"MILLIRADIANS" };

	public static String[] radians = new String[] { "RADIANS", "RADIAN", "RADS", "RAD" };

	public static String[] inches = new String[] { "INCHES", "INCH", "IN" };

	public static int[] denominators = new int[] { 2, 3, 4, 5, 8, 10, 16, 32 };

	static List<GrammarString> toGrammarStrings(List<Fraction> fractions) {
		List<GrammarString> list = new ArrayList<GrammarString>();
		if (fractions == null) return list;
		for (Fraction fraction : fractions) {
			String desc = fraction.toString();
			list.add(new GrammarString(desc.replaceAll(" ",""), desc, fraction.toFloat()));
		}
		return list;
	}

	public static List<GrammarString> grammarElementToStrings(
			GrammarElement element, boolean fraction, String delimiter) {
		List<GrammarString> list = new ArrayList<GrammarString>();
		if (element == null)
			return list;
		if (element == GrammarElement.MILS_AMOUNT) {
			// assume mils times 100
			for (int mils = 0; mils <= 640; mils++) {
				float milsActual = ((float)mils)/10; // resolve to tenths of a (100*mil)
				//list.add(new GrammarString("" + mils, (float)mils));
				//String converted = EnglishNumberToWords.convertUpper(mils);
				
				List<Decimal> decimals = DecimalGenerator.generate(milsActual);
				for (Decimal d : decimals) {
					String str = d.getValueString();
					if (!" ".equals(delimiter)) str = str.replaceAll(" ", "");
					list.add(new GrammarString(str, d.getValueString(), milsActual));
				}
				if (fraction && mils % 10 == 0) {
					for (int d : denominators) {
						for (int n = 0; n < d; n++) {
							if (n == 0 && mils == 0)
								continue;
							list.addAll(toGrammarStrings(FractionGenerator
									.generate(mils, n, d, " ".equals(delimiter))));

						}

					}
				}
			}
			// assume raw mils
			for (int mils = 64; mils <= 6400; mils++) {
				//list.add(new GrammarString("" + mils, (float)mils));
				String converted = EnglishNumberToWords.convertUpper(mils);
				if (" ".equals(delimiter))
					list.add(new GrammarString(converted, converted, (float)mils));
				else
					list.add(new GrammarString(converted.replaceAll(" ", ""),
							converted, (float)mils));
				
				converted = EnglishNumberToWords.convertUpperDigits(mils);
				if (" ".equals(delimiter))
					list.add(new GrammarString(converted, converted, (float)mils));
				else
					list.add(new GrammarString(converted.replaceAll(" ", ""),
							converted, (float)mils));
			}
		} else if (element == GrammarElement.CONJUNCTION) {
			list.add(new GrammarString("AND", "AND"));
			list.add(new GrammarString("&", "AND"));
			list.add(new GrammarString("+", "AND"));
			list.add(new GrammarString("", ""));
		} else if (element == GrammarElement.INCHES_AMOUNT) {
			for (int inches = 0; inches <= 800; inches++) { 
				//list.add(new GrammarString("" + inches, (float)inches));
				float inchesActual = ((float)inches)/100;
				List<Decimal> decimals = DecimalGenerator.generate(inchesActual);
				for (Decimal d : decimals) {
					String str = d.getValueString();
					if (!" ".equals(delimiter)) str = str.replaceAll(" ", "");
					list.add(new GrammarString(str, d.getValueString(), inchesActual));
				}
//				String desc = EnglishNumberToWords.convertUpper(inches);
//				list.add(new GrammarString(desc.replaceAll(" ", ""), desc, (float)inches));
				if (fraction && inches % 100 == 0) {
					for (int d : denominators) {
						for (int n = 0; n < d; n++) {
							if (n == 0 && inches == 0)
								continue;
							list.addAll(toGrammarStrings(FractionGenerator.generate(inches, n,
									d, " ".equals(delimiter))));

						}

					}
				}
			}
		} else if (element == GrammarElement.INCHES_UNIT) {
			list.addAll(toGrammarStrings2(Arrays.asList(inches)));
		} else if (element == GrammarElement.MILS_UNIT) {
			list.addAll(toGrammarStrings2(Arrays.asList(mils)));
			list.addAll(toGrammarStrings2(Arrays.asList(radians)));
		}
		return list;
	}
	
	static List<GrammarString> toGrammarStrings2(List<String> list) {
		List<GrammarString> result = new ArrayList<GrammarString>();
		if (list == null) return result;
		for (String str : list) {
			result.add(new GrammarString(str.replaceAll(" ",""), str));
		}
		return result;
	}
}
