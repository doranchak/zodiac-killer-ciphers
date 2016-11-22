package com.zodiackillerciphers.lucene;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.zodiackillerciphers.ciphers.Cipher;
import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.io.FileUtil;
import com.zodiackillerciphers.transform.CipherTransformations;

public class Stats {
	public static float ENGLISH_IOC = 0.0667f; 
	public static float ENGLISH_ENTROPY = 4.1f; 
	public static float ENGLISH_CHI2 = 0.55f; 
	
	public static Double ioc(String s) {
		return ioc(new StringBuffer(s));
	}
	public static Double ioc(StringBuffer sb) {
		long sum = 0;
		Map<Character, Long> map = new HashMap<Character, Long>();
		for (int i=0; i<sb.length(); i++) {
			Character key = sb.charAt(i);
			Long val = map.get(key);
			if (val == null) val = 0l;
			val++;
			map.put(key, val);
		}
		//if (map.size() > 26) throw new IllegalArgumentException("Alphabet size [" + map.size() + "] is too large!");
		
		Long val;
		for (Character key : map.keySet()) {
			val = map.get(key);
			sum += val*(val-1);
			//System.out.println(key+","+val);
		}
		//System.out.println(sum+","+sb.length());
		return ((double)sum)/((double)sb.length()*(sb.length()-1));
	}
	
	/** what is the ioc of the given text and alphabet if all letters have a flat distribution? */
	public static Double iocFlat(String s) {
		return iocFlat(new StringBuffer(s));
	}
	public static Double iocFlat(StringBuffer sb) {
		Map<Character, Integer> counts = Ciphers.countMap(sb.toString());

		/** average occurrences per symbol */
		double avg = ((double)sb.length())/counts.keySet().size();
		return counts.keySet().size() * avg * (avg - 1) / sb.length() / (sb.length() - 1);
	}
	
	public static Double iocDiff(StringBuffer sb) {
		return Math.abs(ENGLISH_IOC - ioc(sb));
	}
	public static Double iocDiff(String sb) {
		return iocDiff(new StringBuffer(sb));
	}
	
	/** compuate n-gram ioc */
	public static Double iocNgram(String s, int n) {
		
		
		Map<String, Long> map = new HashMap<String, Long>();
		double sum = 0;
		int N = s.length()-n+1;
		for (int i=0; i<N; i++) {
			String ngram = s.substring(i,i+n);
			Long val = map.get(ngram); 
			if (val == null) val = 0l;
			val++;
			map.put(ngram, val);
		}
		Long val;
		for (String key : map.keySet()) {
			val = map.get(key);
			sum += val*(val-1);
			//System.out.println(key+","+val+","+sum);
		}
		//System.out.println(sum+","+sb.length());
		return ((double)sum)/((double)N*(N-1));
	}

	/** return difference between IOCs calculated for top and bottom halves. */
	public static Double iocSpread(String s) {
		int halfway = s.length()/2;
		String s1 = s.substring(0, halfway);
		String s2 = s.substring(halfway);
		double i1 = ioc(s1);
		double i2 = ioc(s2);
		System.out.println(i1 + " " + s1);
		System.out.println(i2 + " " + s2);
		return Math.abs(i1-i2);
	}
	
	/** return ioc of the given column of the given string.  column numbering starts at zero. */
	public static double iocColumn(String input, int width, int column) {
		int i = column;
		String s = "";
		while (i<input.length()) {
			s += input.charAt(i);
			i += width;
		}
		double ioc = ioc(s);
		//System.out.println(width + " " + column + " " + ioc+ "  " + s);
		return ioc;
	}
	
	/** BartW's test: compute average IoC and standard deviation by column width */
	public static void iocWidthTest(String input) {
		for (int width=1; width<=input.length()/2; width++) {
			DescriptiveStatistics stats = new DescriptiveStatistics();
			for (int column=0; column<width; column++) {
				stats.addValue(iocColumn(input, width, column));
			}
			System.out.println(width + " " + stats.getMean() + " " + stats.getStandardDeviation());
		}
	}

	/** my variation of BartW's test: compute average IoC and see how many standard deviations it is from
	 * average for shuffles. */
	public static void iocWidthTestWithShuffles(String input, int numShuffles, int widthStart, int widthEnd) {
		
		/* first, test on shuffles.  collect stats for each width tested. */
		Map<Integer, DescriptiveStatistics> map = new HashMap<Integer, DescriptiveStatistics>();
		for (int width=widthStart; width<=widthEnd; width++) {
			System.out.println(numShuffles + " shuffles for width " + width + "...");
			DescriptiveStatistics val = map.get(width);
			if (val == null) val = new DescriptiveStatistics();
			map.put(width, val);

			String shuffled = input;
			for (int i=0; i<numShuffles; i++) {
				shuffled = CipherTransformations.shuffle(shuffled);
				DescriptiveStatistics stats = new DescriptiveStatistics();
				for (int column=0; column<width; column++) {
					stats.addValue(iocColumn(shuffled, width, column));
				}
				val.addValue(stats.getMean()); // add mean of this shuffle's column IoC to the stats for the current width.  
			}
			
			System.out.println("width " + width + " mean " + val.getMean() + " stddev " + val.getStandardDeviation());
		}
		
		/* next, collect stats on the given input, and compare to stats of shuffles. */
		for (int width=widthStart; width<=widthEnd; width++) {
			DescriptiveStatistics stats = new DescriptiveStatistics();
			for (int column=0; column<width; column++) {
				stats.addValue(iocColumn(input, width, column));
			}
			
			/* get mean and std dev for random shuffles for this width */
			DescriptiveStatistics statsForShuffle = map.get(width);
			
			/* output number of std devs from the mean the actual input's columnar IoC is */
			double sigma = (stats.getMean() - statsForShuffle.getMean())/statsForShuffle.getStandardDeviation();
			System.out.println(width + " " + sigma + " " + stats.getMean() + " " + stats.getStandardDeviation() + " " + statsForShuffle.getMean() + " " + statsForShuffle.getStandardDeviation());
		}
		
	}
	
	
	public static Double entropy(String sb) {
		return entropy(new StringBuffer(sb));
	}
	/** TODO: fix sign problem that happens on long texts; see fixes in ioc */
	public static Double entropy(StringBuffer sb) {
		float sum = 0; float pm; // probability mass
		int L = sb.length();
		Map<Character, Integer> map = new HashMap<Character, Integer>();
		for (int i=0; i<L; i++) {
			Character key = sb.charAt(i);
			Integer val = map.get(key);
			if (val == null) val = 0;
			val++;
			map.put(key, val);
		}
		for (Character key : map.keySet()) {
			pm = ((float)map.get(key))/L; // probability of symbol occurring in the ciphertext 
			sum += pm*Math.log(pm)/Math.log(2);
		}
		return -1.0*sum;
		
	}
	
	public static Double entropyDiff(String sb) {
		return entropyDiff(new StringBuffer(sb));
	}
	public static Double entropyDiff(StringBuffer sb) {
		return Math.abs(ENGLISH_ENTROPY - entropy(sb));
	}

	public static Float chi2(String sb) {
		return chi2(new StringBuffer(sb));
	}
	/** TODO: fix sign problem that happens on long texts; see fixes in ioc */
	public static Float chi2(StringBuffer sb) {
		
		int uniq = 0; float chi2 = 0; float curr;
		int L = sb.length();
		float pm; // probability mass
		
		Map<Character, Integer> map = new HashMap<Character, Integer>();
		for (int i=0; i<L; i++) {
			Character key = sb.charAt(i);
			Integer val = map.get(key);
			if (val == null) {
				uniq++;
				val = 0;
			}
			val++;
			map.put(key, val);
		}
		pm = ((float)L)/uniq; // looks like this is the inverse of multiplicity
		for (Character key : map.keySet()) {
			curr = map.get(key)-pm;
			curr *= curr;
			curr /= pm;
			chi2 += curr;
		}
		return chi2/L;
	}
	public static Float chi2Diff(String sb) {
		return chi2Diff(new StringBuffer(sb));
	}
	public static Float chi2Diff(StringBuffer sb) {
		return Math.abs(ENGLISH_CHI2 - chi2(sb));
	}
	
	
	/** compute chi2 statistic against uniform distribution */
	public static Double chi2Flat(String sb) {
		return chi2Flat(new StringBuffer(sb));
	}
	public static Double chi2Flat(StringBuffer sb) {
		Map<Character, Integer> counts = Ciphers.countMap(sb.toString());
		
		/** expected # of occurrences per symbol for the given cipher length */
		double expected = ((double)sb.length())/counts.keySet().size();
		//System.out.println("expected " + expected);
		double chi2 = 0;
		for (Character c : counts.keySet()) {
			double count = counts.get(c);
			double val = (count-expected)*(count-expected)/expected;
			chi2 += val;
			//System.out.println("val " + val);
			
		}
		return chi2;
		
	}
	
	
	public static void testOnFile(String path) {
		StringBuffer sb = FileUtil.loadSBFrom(path);
		System.out.println(ioc(sb));
	}
	
	/** from the given cipher, derive the alphabet and its letter frequencies.
	 * return the probability that any symbol occurs n times.
	 * (similar to IoC calculation, but each symbol selected is removed from the fixed
	 * sized alphabet)
	 */
	public static double probRepeat(String cipher, int n) {
		if (n < 2) throw new IllegalArgumentException("n must be > 1");
		Map<Character, Integer> counts = Ciphers.countMap(cipher);
		
		double sum = 0;
		int L = cipher.length();
		
		for (Character key : counts.keySet()) {
			Double val = (double) (counts.get(key));
			if (val < n) continue;
			Double mult = 1.0d;
			for (int i=0; i<n; i++) 
				mult *= ((val-i)/L--);
			sum += mult;
		}
		return sum;
		
	}
	
	public static void main(String[] args) {
		/*
		testOnFile("/Users/doranchak/Desktop/tale.txt");
		testOnFile("/Users/doranchak/Desktop/tale-nodigits.txt");
		testOnFile("/Users/doranchak/Desktop/tale-nodigits-nopunct.txt");
		testOnFile("/Users/doranchak/Desktop/tale-nodigits-nospace.txt");
		testOnFile("/Users/doranchak/Desktop/tale-nodigits-nospace-nopunct.txt");*/
		//for (int n=2; n<100; n++) 
		//	System.out.println(probRepeat(Ciphers.cipher[0].cipher, n));
		
		/*
		for (Cipher c : Ciphers.cipher) {
			StringBuffer sb = new StringBuffer(c.cipher);
			double d1 = ioc(sb);
			double d2 = iocFlat(sb);
			double diff = (d1-d2)/d2*100;
			System.out.println(ioc(sb) + " " + iocFlat(sb) + " " + diff + " " + c.description);
			System.out.println(chi2Flat(sb));
		}
		
		StringBuffer test = new StringBuffer("wmzfxtdhzfngfwxwnwxjevxdmzoxfkvxdmzowmkwmkfgzzexenfzpjotkebmnelozlfjpbzkofxwvjefxfwfjpfngfwxwnwxeszyzobdhkxewzawvmkokvwzopjoklxppzozewvxdmzowzawvmkokvwzoxwlxppzofpojtvkzfkovxdmzoxewmkwwmzvxdmzokhdmkgzwxfejwfxtdhbwmzkhdmkgzwfmxpwzlxwxfvjtdhzwzhbrntghzl");
		System.out.println(ioc(test) + " " + iocFlat(test) + " " + test);
		
		test = new StringBuffer("vptzmdrttzysubxaykkwcjmgjmgpwreqeoiivppalrujtlrzpchljftupucywvsyiuuwufirtaxagfpaxzxjqnhbfjvqibxzpotciiaxahmevmmagyczpjxvtndyeuknulvvpbrptygzilbkeppyetvmgpxuknulvjhzdtgrgapygzrptymevppaxygkxwlvtiawlrdmipweqbhpqgngioirnxwhfvvawpjkglxamjewbwpvvmafnlojalh");
		System.out.println(ioc(test) + " " + iocFlat(test) + " " + test);

		test = new StringBuffer("abcdefghijklmnopqrstuvwxyzz");
		System.out.println("chi2flat " + chi2Flat(test));*/

		
		//System.out.println(iocNgram(Ciphers.cipher[0].cipher, 2));
		//System.out.println(iocDiff("THIS IS THE ZODIAC SPEAKING"));
		
		//System.out.println(iocSpread(Ciphers.cipher[0].cipher));
		
		//System.out.println(ioc("EVT+DKcWH^p2tPpOF52.2+@+F4Vp-tp2|.6N(F4t1E-z^21l+WTFt*Dz"));
		//System.out.println(ioc(Ciphers.cipher[0].cipher.substring(0,170)));
		//System.out.println(ioc(Ciphers.cipher[0].cipher.substring(170)));
		//iocWidthTest(Ciphers.cipher[0].cipher);
		//iocWidthTestWithShuffles(Ciphers.cipher[1].solution, 10000, 1, 204);
		
		//iocWidthTestWithShuffles("KEAGRDZFGDCGFNTQOGHQTZQPGORXODKKFBYCXGEDHFPBBBMZNOTPNIBCTFRPGTPNIHOFNMUZPXXGUWWQNCBTYWTZDEZZNHMZGKCMPSCUHZRFWQFRQPIFUTEPWICPGORKEDPRRXTSRZSEEFKBHBZZRYKJCCZWZYEDOZLIMCKECURYCGHFKECAFZGHMCIFNMGIRXGTUIMMZZMCBBZFCURYCGHFIFDQKEADFAUEPMTCYCZSIZPTGIGHLMTFNKOFSHPWZYCAFZYHMZKPADGSMIAOYZTAHJRTIGZYAFGEAGRDZRMQUJYZXZUNEMGIRWXQUECQRKQPKSJZSFKEAIBBIIYBZFEGTDNPEERZAXTZRPCXCJQXZORTRKRKMZKOZYYOQUCHZSTZRMNCWHNPYKIFWKCSBTLZRQPKJTGUZYSFTZLZLCLTHMTRAIXFYZNFJZMJMDAKWTKCCRBDNZREIZLTKORCYEWSQIBHAKEFJZQPFSTLMNGIMUZZGIAOZZPHTGGIEUPGSIMZKIEMTZCMZSZKIAPJYCWGUDEOKGFTKGEJTQOJKPRIYZSXKXFIEMSFRQQIDTPSXTHMTRAIXFYNHQPFSIIIZREDULQIASTLMNGIRWTHCZRQKENJMQUUEEQGCGTHKUBKULZHMWZLTUPXYRZCXUIZIKMPEOXXEOQUCQHCQNHU", 10000, 1, 340);
		//iocWidthTestWithShuffles("bkmpowllgupyqgktjqcnxqpggmjtbyxskeklluorxzbukhumpxxhbrjmoigthvvrntpabrolllkmpentmaprtntjrxhmgugkhkgkkzbbstakgyhapnzplkntnzmvzmvqzvkvdrzalbbuezerallbbkqwtbxxaprukpovtghthtlvtikwjqcnxyigzapacububvchbtqrbrmguflvgohuqqugvbpuesmpzlsisklhvqoduwjnhdbbvkvxrxefackesxnxtkqpkbdwhrwhtfuepsrzhtmazbvvvgfocamkfqasrhngkksqsklsiikldqcrfhsrsxmzryaziajppkukl", 10000, 1, 170);
		//iocWidthTestWithShuffles("B+LR^1.4pK()kp+J6q8IyM(_pYfOB)zV%;+*4G^DyWBS+XKYEzy2NFfLA:pO<c5DIH(dBD^*.4+ZRCIOLTEFHIJf>z2LpKp+<+_++WNBlOU+j)2TRIb(.+IJIWZcWL5kWc&P#>Wd.9BGCWCFT.*BB+k1ONzzU(DK+EAcOp2H2O*cH-+/6M8Iy):jWTRd7GBKBc729OMFN>ZpGt4P_^2SkMGpc9RG;lY(W*V-V+4<PM^#S16I2#BB5%czFzCtU8+;lzIyO+q(+N#/<F/2OtSCRlFbXJZUW95ccp3^7dL&3MUV><@p++lkV+.l--+*#M8Ft<VFlzYWF)Tb-dfRR+K+4", 10000, 1, 170);
		//iocWidthTestWithShuffles("ABCDEFGFHIJKLMNOPQJRSTUPVAFWXYZaJbcdeOWfLghbiaANjFkPjlQHWmnmoIKHOTdCMpqRmYhAoZJrFLFsQKCRCMdrRtVCNQhurpDgAHRRIpQuOvCwpxdvoTCqbCZMFmvmQXchKDQLSPamnZrRHPrihPLHZiMLygbzazWxbYIRsLSwyQdgyIAO0aALDtjJBgWgBSCVobxbizjGXyGbd1DHKHOrftuW1YWgJNymm0kvvTtYzbSNiZvskrXHMH0kfXkONKiMXjFit1lLIAICwpfcFossEvMCMFiONTtRRHIbGszPQzRGsyP0NltSOeaavgBZWelSSHOWBufHrmMivvsZieRdLQacdRTvB", 10000, 1, 170);
		iocWidthTestWithShuffles(">|+R|ylz2U-d(#p_SBNHMF+ccBG6<+z<G589pypEDk)T.XF9cRlM2+RMp:+RHdW+31NSl/U+JK^+7cB>NWCLz*^yR5VbfqFz^M(pp<z1B:f#Jt++j%ltl+#lk7W6K45+|E^Z#;Oj8UO^StcC(92N*|JRO2-d*Z%VzBP<OC4|5D+2+U*|VGDPZ_O+pEb5TYOF_cd53WWkOYSF^>.F4BpBNXCFp(Y|8OHl.VcBMp7cYGkPO).1ABTWfUVc.b<yzVF++L<L|*/BMZ4(+TFA+.>&+#*TK-(|q5t;&MB6@z24RzKG;C))G-+8BKy4LLDkKHf2+cpL2++RFO-K9|(/2J)d", 10000, 1, 170);
		iocWidthTestWithShuffles("ABCDEFGHIJKLMNHOLPQDNBRSHTUVWMQSIXAOYZaEbKcdBDeBfHBPghDCBiGBjUEkLXlESmnKoUeIpqUSrHIIXBsqoUfYktIjlSBQiuSvVPwUxmWMEYLLNFVBWOtyz0v12ZeKByoFnJddeHk3YBBZD4UkbyjMyDr2LrOBVm1uwFz0WwxFOcyePjGRKZOBB5KWBZQk4BKQgQBLvJ64FmlBaKfUHTqCyABGBia7HpzOjurnFjJmljUXs5DvmP84Vhm4iFXHopsjSERPDjUBuKh9k3lqSr8DtbdSHxLqdC!XDJ!DKN9ydFFZ4RkL0MABXhNG2NOHHL7xeknrjskjbSex", 10000, 1, 170);


	}

}
