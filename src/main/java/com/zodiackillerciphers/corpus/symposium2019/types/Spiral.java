package com.zodiackillerciphers.corpus.symposium2019.types;

import java.util.Arrays;
import java.util.Random;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.corpus.symposium2019.CipherBase;
import com.zodiackillerciphers.ngrams.Periods;

public class Spiral extends CipherBase {
	static Random rand = new Random();
	
	@Override
	public String firstLayer(String plaintext) {
		boolean reverse = rand.nextBoolean();
		String spiral = com.zodiackillerciphers.transform.operations.Spiral.transform(plaintext, 17, reverse);
		return spiral;
	}

	public static void test() {
		String pt = Ciphers.Z408_SOLUTION.substring(0, 340);
		Spiral sp = new Spiral();
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
