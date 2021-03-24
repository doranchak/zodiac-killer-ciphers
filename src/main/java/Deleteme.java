import java.util.Arrays;

public class Deleteme {
	public static void loop(int[] counters, int[] bounds, int index) {
		if (index == counters.length) {
			System.out.println(Arrays.toString(counters));
			return;
		}
		for (int i=0; i<bounds[index]; i++) {
			counters[index] = i;
			loop(counters, bounds, index+1);
		}
	}
	public static void main(String[] args) {
		loop(new int[] {0,0,0,0,0}, new int[] {3,3,3,3,3}, 0);
	}
}
