package com.zodiackillerciphers.corpus.symposium2019;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.corpus.symposium2019.types.Columnar;
import com.zodiackillerciphers.corpus.symposium2019.types.Homophonic;
import com.zodiackillerciphers.lucene.Stats;
import com.zodiackillerciphers.transform.CipherTransformations;
import com.zodiackillerciphers.transform.operations.Permutation;

public class SnakeFragmentStats {

	public String cipher = null;
	
	/** generate fragment repeat stats for untranspositions for all keys 
	 * does this for fragment sizes in the range [Lmin, Lmax]
	 * returns a wide vector of stats for all combinations, instead of maximums  
	 */
	public static List<Integer> generateUntransposedFragmentStatsFor(String cipher, int Lmin, int Lmax) {
		List<Integer> all = new ArrayList<Integer>();;
		
		for (int corner=0; corner<4; corner++) {
			for (boolean directionRow : new boolean[] {false, true}) {
				for (int width=2; width<=20; width++) {
					String untransposed = com.zodiackillerciphers.transform.operations.Snake.transform(cipher, corner, directionRow, width, false, true);
					List<Integer> fragStats = FragmentStats.generateFragmentStatsFor(untransposed, Lmin, Lmax);
//					if (fragStats.get(0) > 1000) {
//						System.out.println(corner + " " + directionRow + " " + width + " " + untransposed);
//						System.out.println(fragStats);
//						System.exit(-1);
//					}
					all.addAll(fragStats);
				}
			}
		}
		return all;
	}
	public static void test1() {
		com.zodiackillerciphers.corpus.symposium2019.types.Snake snake = new com.zodiackillerciphers.corpus.symposium2019.types.Snake();
		int Lmax = 5;

		// corner 0 dir true width 17
		String snakeCipher = "LkJyzy60tbqB9EY9tPOZvFWbcJzFOi5IQDuAOhbM6WTvg7AO2muzZwBNnJ3B2b0t!y2Cb2MuDlvggEWMQzPCOh1N81vZDSoFLhwTzFBEgmOD1q1ZCfsl1ft!BBhJuoGTvWt0!ysVjEWpEmSGZ4wmmSg6tWemLGPh7gzra7BxLk7jzhQGV87g8uCxBGo3AA4Wy5YggO4RBxJcemSdBJg0mSGQGF8K1!qGu3zwu8w!8!AvmgN!EL3!0tHzgEQYgXJuVnniN2FI5bNigw9XzJSCjEybtkGN3bf0Q0e3LWEjifWRZzUvPGn24cBLjGR4OpRx1TERgM00!3OYREFO1PIQ";
		System.out.println("perm: " + snakeCipher);
		System.out.println("Ref stats for unmodified cipher:");
		System.out.println(FragmentStats.generateFragmentStatsFor(snakeCipher, 2, Lmax));
		System.out.println("Best measurements for all untranspositions:");
		double elapsed = new Date().getTime();
		System.out.println(generateUntransposedFragmentStatsFor(snakeCipher, 2, Lmax));
		elapsed = (new Date().getTime() - elapsed)/1000;
		System.out.println("elapsed: " + elapsed + " sec");
		String untransposed = com.zodiackillerciphers.transform.operations.Snake.transform(snakeCipher, 0, true, 17, false, true);
		System.out.println("Untransposition:");
		System.out.println(untransposed);
		System.out.println("Ref stats for untransposition:");
		System.out.println(FragmentStats.generateFragmentStatsFor(untransposed, 2, Lmax));
		String shuffled = CipherTransformations.shuffle(snakeCipher);
		System.out.println("Ref stats for shuffled:");
		System.out.println(FragmentStats.generateFragmentStatsFor(shuffled, 2, Lmax));
		System.out.println("Best measurements for all untranspositions of shuffled:");
		System.out.println(
				generateUntransposedFragmentStatsFor(shuffled, 2, Lmax));
		
		
	}
	
	public static void main(String[] args) {
		test1();
	}
}
