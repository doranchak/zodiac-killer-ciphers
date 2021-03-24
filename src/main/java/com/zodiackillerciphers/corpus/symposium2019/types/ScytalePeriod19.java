package com.zodiackillerciphers.corpus.symposium2019.types;

import java.util.Arrays;
import java.util.Random;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.corpus.symposium2019.CipherBase;
import com.zodiackillerciphers.ngrams.Periods;

public class ScytalePeriod19 extends CipherBase {
	static Random rand = new Random();
	
	@Override
	public String firstLayer(String plaintext) {
		// scytale
		int period = 19;
		String scytale = com.zodiackillerciphers.ciphers.algorithms.Scytale.encode(plaintext, period);
//		say("period " + period + " cipher " + scytale);
		return scytale;
	}

	public static void test() {
		String pt = Ciphers.Z408_SOLUTION.substring(0, 340);
		ScytalePeriod19 sp = new ScytalePeriod19();
		sp.addPlaintext(pt);
		sp.run();
//		String result = sp.makeCipher(pt);
		System.out.println("Result: " + sp.getCiphers());
		System.out.println(Arrays.toString(sp.getStats().get(0)));
	}

	public static void main(String[] args) {
		 test();
		 System.out.println(Periods.rewrite3undo("itttolmlhdtcbyiaahkesknnrsrlekgioeakielfbvilrlfoelltiwrsliuniniinegtiwngaehnigwnxaplpiapgalelmeirnodarraopgleldtlaontigemfchcibeaeeeveilibsecnltenyattisdouhostausekeplmefivalyiolertntrlnthairsboemseoefiessmtihbotettaembteivcuehraeaccittkuhanhhisfugaaleusgnelyneigweoimvehdutaetewwinstniisimiillmsendllottgibtrhhyeereeeoicyfmmuwouoorimnaarle",  49));
	}

}
