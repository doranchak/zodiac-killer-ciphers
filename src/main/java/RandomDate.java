import java.util.Arrays;
import java.util.Calendar;
import java.util.Random;

public class RandomDate {
	/** generate a random number N, from [1,max], to represent "N days ago".
	 * values of N are weighted higher, to give preference to more recent dates.
	 * convert to a date and a GMail URL for loading emails from that day in the starred folder. 
	 */
	public static void go(int max, int startWeight, float factor) {
		int[] weights = new int[max];
		int sum = 0;
		float weight = startWeight;
		for (int i=1; i<max; i++) {
			weights[i-1] = (int) weight;
			sum += (int) weight;
			weight /= factor;
			if (weight < 1) weight = 1;
		}
		//System.out.println(sum);
		//System.out.println(Arrays.toString(weights));
		int selection = new Random().nextInt(sum);
		
		sum = 0;
		for (int i=0; i<weights.length; i++) {
			sum += weights[i];
			if (selection < sum) {
				//System.out.println("selection: " + selection);
				//System.out.println("days: " + (i+1));
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR)-(i+1));
				Calendar cal2 = Calendar.getInstance();
				cal2.setTime(cal.getTime());
				cal2.set(Calendar.DAY_OF_YEAR, cal2.get(Calendar.DAY_OF_YEAR)-1);
				//System.out.println(cal2.getTime());
				
				String url = "https://mail.google.com/mail/u/0/#search/is%3Astarred+before%3A";
				String slash = "%2F";
				url += cal.get(Calendar.YEAR) + slash; 
				url += (cal.get(Calendar.MONTH) + 1) + slash; 
				url += cal.get(Calendar.DAY_OF_MONTH); //+ "+after%3A"; 
				//url += cal2.get(Calendar.YEAR) + slash; 
				//url += (cal2.get(Calendar.MONTH) + 1) + slash; 
				//url += cal2.get(Calendar.DAY_OF_MONTH);
				System.out.println(url);
				return;
			}
		}
	}
	
	public static void main(String[] args) {
		//go(365*3, 1000, 1.02f);
		go(365*9, 1, 1.02f);
	}
}
