import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CoronaVirus {

	static SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy");

	public static String[] datesChina = new String[] { "Jan 22", "Jan 23", "Jan 24", "Jan 25", "Jan 26", "Jan 27",
			"Jan 28", "Jan 29", "Jan 30", "Jan 31", "Feb 01", "Feb 02", "Feb 03", "Feb 04", "Feb 05", "Feb 06",
			"Feb 07", "Feb 08", "Feb 09", "Feb 10", "Feb 11", "Feb 12", "Feb 13", "Feb 14", "Feb 15", "Feb 16",
			"Feb 17", "Feb 18", "Feb 19", "Feb 20", "Feb 21", "Feb 22", "Feb 23", "Feb 24", "Feb 25", "Feb 26",
			"Feb 27", "Feb 28", "Feb 29", "Mar 01", "Mar 02", "Mar 03", "Mar 04", "Mar 05", "Mar 06", "Mar 07",
			"Mar 08", "Mar 09", "Mar 10", "Mar 11", "Mar 12", "Mar 13", "Mar 14", "Mar 15", "Mar 16", "Mar 17",
			"Mar 18", "Mar 19" };
	public static int[] casesChina = new int[] { 571, 830, 1287, 1975, 2744, 4515, 5974, 7711, 9692, 11791, 14380,
			17205, 20440, 24324, 28018, 31161, 34546, 37198, 40171, 42638, 44653, 58761, 63851, 66492, 68500, 70548,
			72436, 74185, 74576, 75465, 76288, 76936, 77150, 77658, 78064, 78497, 78824, 79251, 79824, 80026, 80151,
			80270, 80409, 80552, 80651, 80695, 80735, 80754, 80778, 80793, 80813, 80824, 80844, 80860, 80881, 80894,
			80928, 80967 };
	public static String[] datesUSA = new String[] { "Feb 15", "Feb 16", "Feb 17", "Feb 18", "Feb 19", "Feb 20",
			"Feb 21", "Feb 22", "Feb 23", "Feb 24", "Feb 25", "Feb 26", "Feb 27", "Feb 28", "Feb 29", "Mar 01",
			"Mar 02", "Mar 03", "Mar 04", "Mar 05", "Mar 06", "Mar 07", "Mar 08", "Mar 09", "Mar 10", "Mar 11",
			"Mar 12", "Mar 13", "Mar 14", "Mar 15", "Mar 16", "Mar 17", "Mar 18", "Mar 19", "Mar 20", "Mar 21" };
	public static int[] casesUSA = new int[] { 12, 12, 12, 12, 12, 10, 29, 29, 28, 48, 51, 54, 54, 57, 60, 65, 85, 106,
			138, 200, 289, 401, 504, 663, 949, 1248, 1625, 2157, 2830, 3553, 4503, 6196, 9003, 13474, 19383, 24207 };

	public static void projectUSCasesBasedOnChinaGrowth() {
		int indexChina = 0;
		int indexUSA = 0;
		while (true) {
			if (casesUSA[indexUSA] >= casesChina[indexChina])
				break;
			indexUSA++;
		}

		float growthMultiplier = 0;
		while (indexChina < casesChina.length && indexUSA < casesUSA.length) {
			String dateChina = datesChina[indexChina];
			String dateUSA = datesUSA[indexUSA];
			int caseChina = casesChina[indexChina];
			int caseUSA = casesUSA[indexUSA];
			float diff = caseUSA;
			diff = diff - caseChina;
			diff = diff / caseChina;
			float growthChina = 0;
			float growthUSA = 0;
			if (indexChina > 0) {
				growthChina = casesChina[indexChina] - casesChina[indexChina - 1];
				growthChina = growthChina / casesChina[indexChina - 1];
			}
			if (indexUSA > 0) {
				growthUSA = casesUSA[indexUSA] - casesUSA[indexUSA - 1];
				growthUSA = growthUSA / casesUSA[indexUSA - 1];
			}
			System.out.println(dateChina + "	" + caseChina + "	" + growthChina + "	" + dateUSA + "	" + caseUSA
					+ "	" + growthUSA + "	" + diff);

			if (growthChina > 0)
				growthMultiplier = growthUSA / growthChina;
			indexChina++;
			indexUSA++;
		}
		indexUSA--;
		String dateUSA = datesUSA[indexUSA];
		Date dateUSAdate = null;
		float caseUSA = casesUSA[indexUSA];
		float caseUSA2 = casesUSA[indexUSA];
		try {
			dateUSAdate = sdf.parse(dateUSA + " " + 2020);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// now project USA based on China's day to day growth
		while (indexChina < casesChina.length) {
			float growthChina = 0;
			int caseChina = casesChina[indexChina];
			growthChina = (caseChina - casesChina[indexChina - 1]);
			growthChina = growthChina / casesChina[indexChina - 1];
			caseUSA = caseUSA * (1 + growthChina * growthMultiplier);
			Calendar c = Calendar.getInstance();
			c.setTime(dateUSAdate);
			c.add(Calendar.DATE, 1);
			dateUSAdate = c.getTime();
			dateUSA = sdf.format(dateUSAdate).substring(0, 6);
			String dateChina = datesChina[indexChina];

			float diff = caseUSA;
			diff = diff - caseChina;
			diff = diff / caseChina;

			System.out.println(dateChina + "	" + caseChina + "	" + growthChina + "	" + dateUSA + "	" + caseUSA
					+ "	" + growthChina * growthMultiplier + "	" + diff + "	*");

			indexChina++;
		}
		System.out.println("multiplier: " + growthMultiplier);
	}
	
	public static void growth(float startingCaseCount, float growthRate, float adjustmentFactor, int days) {
		String d = "day";
		for (int i=0; i<days; i++) {
			growthRate *= adjustmentFactor;
			startingCaseCount = startingCaseCount * (1+growthRate);
			if (i==1) d = "days";
			float percentF = 100*growthRate;
			//int percent = (int) percentF;
			System.out.println("After " + (i+1) + " " + d + ": " + (int) startingCaseCount + " (growth rate: " + percentF + "%)");
		}
	}

	public static void main(String[] args) {
//		projectUSCasesBasedOnChinaGrowth();
		//growth(33546f, 0.33f, 0.8f, 30);
		growth(118452f, 0.0063f, 1f, 300);
	}
}
