package com.zodiackillerciphers.ngrams;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.zodiackillerciphers.ciphers.Cipher;
import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.stats.StatsWrapper;
import com.zodiackillerciphers.tests.jarlve.JarlveMeasurements;
import com.zodiackillerciphers.transform.CipherTransformations;
import com.zodiackillerciphers.transform.Operations;

public class RepeatingFragments {

	public static List<RepeatingFragmentsBeanEntry> repeatingFragmentsBeansFor(int n1, int n2, String cipher) {
		return repeatingFragmentsBeansFor(n1, n2, cipher, true);
	}
	/** if removeSubstrings true, then get rid of any fragment that is a substring of a larger fragment.*/ 
	public static List<RepeatingFragmentsBeanEntry> repeatingFragmentsBeansFor(int n1, int n2, String cipher, boolean removeSubstrings) {
		List<RepeatingFragmentsBeanEntry> beans = new ArrayList<RepeatingFragmentsBeanEntry>();
		for (int n = n1; n <= n2; n++) {
			RepeatingFragmentsBean bean = RepeatingFragments.repeatingFragments(n, cipher);
			if (bean.beans != null)
				beans.addAll(bean.beans);
		}
		Collections.sort(beans);
		if (removeSubstrings) {
			List<RepeatingFragmentsBeanEntry> beans2 = new ArrayList<RepeatingFragmentsBeanEntry>();
			
			for (RepeatingFragmentsBeanEntry bean1 : beans) {
				boolean found = false;
				// only add this bean if it is not a substring of any other bean
				for (RepeatingFragmentsBeanEntry bean2 : beans2) {
					if (bean1.isSubstringOf(bean2)) {
						//System.out.println(bean1 + " is substring of " + bean2);
						found = true;
						break;
					}
				}
				if (!found) beans2.add(bean1);
			}
			return beans2;
		}
		return beans;
	}
	
	/* started with jarlve's idea but ended up doing my own thing   http://www.zodiackillersite.com/viewtopic.php?p=42947#p42947 */
	public static RepeatingFragmentsBean repeatingFragments(int n, String cipher) {
		return repeatingFragments(n, cipher, false, 0);
	}
	public static RepeatingFragmentsBean repeatingFragments(int n, String cipher, boolean excludeNonSkipgrams, int minNonWildcards) {
		return repeatingFragments(n, n, cipher, excludeNonSkipgrams, minNonWildcards);
	}
	public static RepeatingFragmentsBean repeatingFragments(int n1, int n2, String cipher, boolean excludeNonSkipgrams, int minNonWildcards) {
		//int sum = 0;
		RepeatingFragmentsBean bean = new RepeatingFragmentsBean(cipher);
		
		
		Set<String> seen = new HashSet<String>();

		for (int n=n1; n<=n2; n++) {
			for (int i=0; i<cipher.length()-n+1; i++) {
				String sub1 = cipher.substring(i,i+n);
				for (int j=i+n; j<cipher.length(); j++) {
					try {
						 String sub2 = cipher.substring(j,j+n);
						 if (sub1.charAt(0) == sub2.charAt(0) && sub1.charAt(n-1) == sub2.charAt(n-1)) {
							 //System.out.println(sub1 + ", " + sub2);
							 //int internal = 0;
							 String key = "" + sub1.charAt(0);
							 for (int k=1; k<sub1.length()-1; k++) {
								 if (sub1.charAt(k) == sub2.charAt(k)) {
									 key += sub1.charAt(k);
									 //internal++;
									 //System.out.println(" - " + key + ", " + internal);
								 } else {
									 key += "?";
								 }
							 }
							 //if (internal > 0) {
							 key += sub1.charAt(n-1);
							 if (key.contains(" ")) continue;// ignore spaces unless they are the wildcards
							 bean.map(key, i);
							 bean.map(key, j);
						 }
					} catch (Exception e) {}
				}
			}
		}
		bean.makeBeans(excludeNonSkipgrams, minNonWildcards);
		return bean;
	}
	
	/** search for a single repeating fragment pattern.  return positions of all occurrences. */
	public static List<Integer> findFragment(String cipher, String fragment) {
		List<Integer> list = new ArrayList<Integer>();
		char ch1 = fragment.charAt(0);
		int index = cipher.indexOf(ch1);
		while (index > -1) {
//			System.out.println(index);
			boolean match = false;
			for (int i=0; i<fragment.length() && (index+i) < cipher.length(); i++) {
				char c1 = fragment.charAt(i);
				if (c1 == '?') continue;
				char c2 = cipher.charAt(index+i);
//				System.out.println("c1 " + c1 + " c2 " + c2);
				if (c1 != c2) {
					break;
				}
				if (i == fragment.length() - 1)
					match = true;
			}
			if (match) list.add(index);
			index = cipher.indexOf(ch1,index+1);
		}
		return list;
	}
	
	public static void test() {
		String cipher = Ciphers.cipher[4].cipher;
		for (int i=1; i<=cipher.length()/2; i++) {
			String rewritten = Periods.rewrite3(cipher, i);
			for (int n=3; n<10; n++) {
				RepeatingFragmentsBean bean = repeatingFragments(n, rewritten);
				if (bean.counts.size() == 0) continue;
				System.out.println(bean.info("z340flipped	" + n + "	" + i));
				//bean.dump();
			}
		}
	}
	
	public static void test2() {
		String cipher = ">OpW%l+(D^k+)WV9/RLYPFMSK#.k2l+p2z<|NUOzpBH*1AYc-t7yJKL-6zX*j^:NfTld4+Gddlcp)GtU<K@VC|8M+H25EVM(L.k5*+BEdBT|++G9zFFVU(Rtc4D^b2#L>P3Z#l+(MYJ+J5|2+pG1G+;.B+Zf+pD&OK*Fz8+pORjK8(4+(:N6R&bp2#qR_TFO4^92BT7FO%^C/lp9fS<FM<B+;Fc(W^C5ycUKFc_Nk+)B.E2#l+OBypd+p|f>4+RR-ykW)R)MVbNJ/zS<WcLqU.||5z7CT|GZc5*Ztz+c25VFOBWL.y-48_c13B+AYP6zX|OOCBKBS<>;*HM+-D|H";
		System.out.println(JarlveMeasurements.jarlveFragmentScore(5, cipher));
		System.out.println(new NGramsBean(2, cipher).numRepeats());
		System.out.println(new NGramsBean(3, cipher).numRepeats());
		
		for (int n=3; n<10; n++) {
			RepeatingFragmentsBean bean = repeatingFragments(n, cipher);
			bean.dump();
		}
	}
	
	public static void test3() {
		String cipher = Ciphers.cipher[5].cipher;
		System.out.println(JarlveMeasurements.jarlveFragmentScore(5, cipher));
		
		System.out.println("---");
		
		for (int i=1; i<=cipher.length()/2; i++) {
			String rewritten = Periods.rewrite3(cipher, i);
			System.out.println(JarlveMeasurements.jarlveFragmentScore(5, rewritten) + " " + i);
		};
	}
	
	public static void test4() {
		//String bestRandom = "XN*T-d>+dy+4C./1tFJH3(^pTCF+FB_+DL>+dRO5OBc^@.p5#L|4+pW)S_2+qtD+^<#TY5c|N.DXLEb-R5;cO<k5&Rl^yWRG2f)OOzV+2l)p9*_f.HFl<+kF+.%98*Ap5YZW+dfp76MMjBBlFO-U(BZL%BUR26LF5J8*S1R:N/kl2-q<SYbAp>)zKWVc<ky+7+Jy#U1zk+lOz#9:pZ+4+V;MlGLK#+GRJUB7M(D|z&FzT|+.KZ42)2V^CG+8(pY6MMUz39(fyM*2+HB+(2p|Cc+FBOztOK4+/VbE|RF|(zTp|8<P^W>KG*B-OCKSj|NcdBK|EGtc4PBPcNHcVc;W";
		 //String cipher = Ciphers.cipher[5].cipher;
		 //PeriodMap pm = Periods.rewrite4(cipher, 15);
		 //cipher = pm.rewritten;
		 
		 
			Map<Integer, Integer> map = new HashMap<Integer, Integer>(); 
			map.put(0, 338);
			map.put(1, 319);
			map.put(2, 300);
			map.put(3, 281);
			map.put(4, 262);
			map.put(5, 243);
			map.put(6, 224);
			map.put(7, 205);
			map.put(8, 203);
			map.put(9, 184);
			map.put(10, 165);
			map.put(11, 146);
			map.put(12, 127);
			map.put(13, 108);
			map.put(14, 89);
			map.put(15, 70);
			map.put(16, 51);
			map.put(17, 32);
			map.put(18, 13);
			map.put(19, 336);
			map.put(20, 317);
			map.put(21, 298);
			map.put(22, 279);
			map.put(23, 260);
			map.put(24, 241);
			map.put(25, 222);
			map.put(26, 220);
			map.put(27, 201);
			map.put(28, 182);
			map.put(29, 163);
			map.put(30, 144);
			map.put(31, 125);
			map.put(32, 106);
			map.put(33, 87);
			map.put(34, 68);
			map.put(35, 49);
			map.put(36, 30);
			map.put(37, 11);
			map.put(38, 334);
			map.put(39, 315);
			map.put(40, 296);
			map.put(41, 277);
			map.put(42, 258);
			map.put(43, 239);
			map.put(44, 237);
			map.put(45, 218);
			map.put(46, 199);
			map.put(47, 180);
			map.put(48, 161);
			map.put(49, 142);
			map.put(50, 123);
			map.put(51, 104);
			map.put(52, 85);
			map.put(53, 66);
			map.put(54, 47);
			map.put(55, 28);
			map.put(56, 9);
			map.put(57, 332);
			map.put(58, 313);
			map.put(59, 294);
			map.put(60, 275);
			map.put(61, 256);
			map.put(62, 254);
			map.put(63, 235);
			map.put(64, 216);
			map.put(65, 197);
			map.put(66, 178);
			map.put(67, 159);
			map.put(68, 140);
			map.put(69, 121);
			map.put(70, 102);
			map.put(71, 83);
			map.put(72, 64);
			map.put(73, 45);
			map.put(74, 26);
			map.put(75, 7);
			map.put(76, 330);
			map.put(77, 311);
			map.put(78, 292);
			map.put(79, 273);
			map.put(80, 271);
			map.put(81, 252);
			map.put(82, 233);
			map.put(83, 214);
			map.put(84, 195);
			map.put(85, 176);
			map.put(86, 157);
			map.put(87, 138);
			map.put(88, 119);
			map.put(89, 100);
			map.put(90, 81);
			map.put(91, 62);
			map.put(92, 43);
			map.put(93, 24);
			map.put(94, 5);
			map.put(95, 328);
			map.put(96, 309);
			map.put(97, 290);
			map.put(98, 288);
			map.put(99, 269);
			map.put(100, 250);
			map.put(101, 231);
			map.put(102, 212);
			map.put(103, 193);
			map.put(104, 174);
			map.put(105, 155);
			map.put(106, 136);
			map.put(107, 117);
			map.put(108, 98);
			map.put(109, 79);
			map.put(110, 60);
			map.put(111, 41);
			map.put(112, 22);
			map.put(113, 3);
			map.put(114, 326);
			map.put(115, 307);
			map.put(116, 305);
			map.put(117, 286);
			map.put(118, 267);
			map.put(119, 248);
			map.put(120, 229);
			map.put(121, 210);
			map.put(122, 191);
			map.put(123, 172);
			map.put(124, 153);
			map.put(125, 134);
			map.put(126, 115);
			map.put(127, 96);
			map.put(128, 77);
			map.put(129, 58);
			map.put(130, 39);
			map.put(131, 20);
			map.put(132, 1);
			map.put(133, 324);
			map.put(134, 322);
			map.put(135, 303);
			map.put(136, 284);
			map.put(137, 265);
			map.put(138, 246);
			map.put(139, 227);
			map.put(140, 208);
			map.put(141, 189);
			map.put(142, 170);
			map.put(143, 151);
			map.put(144, 132);
			map.put(145, 113);
			map.put(146, 94);
			map.put(147, 75);
			map.put(148, 56);
			map.put(149, 37);
			map.put(150, 18);
			map.put(151, 16);
			map.put(152, 339);
			map.put(153, 320);
			map.put(154, 301);
			map.put(155, 282);
			map.put(156, 263);
			map.put(157, 244);
			map.put(158, 225);
			map.put(159, 206);
			map.put(160, 187);
			map.put(161, 168);
			map.put(162, 149);
			map.put(163, 130);
			map.put(164, 111);
			map.put(165, 92);
			map.put(166, 73);
			map.put(167, 54);
			map.put(168, 35);
			map.put(169, 33);
			map.put(170, 14);
			map.put(171, 337);
			map.put(172, 318);
			map.put(173, 299);
			map.put(174, 280);
			map.put(175, 261);
			map.put(176, 242);
			map.put(177, 223);
			map.put(178, 204);
			map.put(179, 185);
			map.put(180, 166);
			map.put(181, 147);
			map.put(182, 128);
			map.put(183, 109);
			map.put(184, 90);
			map.put(185, 71);
			map.put(186, 52);
			map.put(187, 50);
			map.put(188, 31);
			map.put(189, 12);
			map.put(190, 335);
			map.put(191, 316);
			map.put(192, 297);
			map.put(193, 278);
			map.put(194, 259);
			map.put(195, 240);
			map.put(196, 221);
			map.put(197, 202);
			map.put(198, 183);
			map.put(199, 164);
			map.put(200, 145);
			map.put(201, 126);
			map.put(202, 107);
			map.put(203, 88);
			map.put(204, 69);
			map.put(205, 67);
			map.put(206, 48);
			map.put(207, 29);
			map.put(208, 10);
			map.put(209, 333);
			map.put(210, 314);
			map.put(211, 295);
			map.put(212, 276);
			map.put(213, 257);
			map.put(214, 238);
			map.put(215, 219);
			map.put(216, 200);
			map.put(217, 181);
			map.put(218, 162);
			map.put(219, 143);
			map.put(220, 124);
			map.put(221, 105);
			map.put(222, 86);
			map.put(223, 84);
			map.put(224, 65);
			map.put(225, 46);
			map.put(226, 27);
			map.put(227, 8);
			map.put(228, 331);
			map.put(229, 312);
			map.put(230, 293);
			map.put(231, 274);
			map.put(232, 255);
			map.put(233, 236);
			map.put(234, 217);
			map.put(235, 198);
			map.put(236, 179);
			map.put(237, 160);
			map.put(238, 141);
			map.put(239, 122);
			map.put(240, 103);
			map.put(241, 101);
			map.put(242, 82);
			map.put(243, 63);
			map.put(244, 44);
			map.put(245, 25);
			map.put(246, 6);
			map.put(247, 329);
			map.put(248, 310);
			map.put(249, 291);
			map.put(250, 272);
			map.put(251, 253);
			map.put(252, 234);
			map.put(253, 215);
			map.put(254, 196);
			map.put(255, 177);
			map.put(256, 158);
			map.put(257, 139);
			map.put(258, 120);
			map.put(259, 118);
			map.put(260, 99);
			map.put(261, 80);
			map.put(262, 61);
			map.put(263, 42);
			map.put(264, 23);
			map.put(265, 4);
			map.put(266, 327);
			map.put(267, 308);
			map.put(268, 289);
			map.put(269, 270);
			map.put(270, 251);
			map.put(271, 232);
			map.put(272, 213);
			map.put(273, 194);
			map.put(274, 175);
			map.put(275, 156);
			map.put(276, 137);
			map.put(277, 135);
			map.put(278, 116);
			map.put(279, 97);
			map.put(280, 78);
			map.put(281, 59);
			map.put(282, 40);
			map.put(283, 21);
			map.put(284, 2);
			map.put(285, 325);
			map.put(286, 306);
			map.put(287, 287);
			map.put(288, 268);
			map.put(289, 249);
			map.put(290, 230);
			map.put(291, 211);
			map.put(292, 192);
			map.put(293, 173);
			map.put(294, 154);
			map.put(295, 152);
			map.put(296, 133);
			map.put(297, 114);
			map.put(298, 95);
			map.put(299, 76);
			map.put(300, 57);
			map.put(301, 38);
			map.put(302, 19);
			map.put(303, 0);
			map.put(304, 323);
			map.put(305, 304);
			map.put(306, 285);
			map.put(307, 266);
			map.put(308, 247);
			map.put(309, 228);
			map.put(310, 209);
			map.put(311, 190);
			map.put(312, 171);
			map.put(313, 169);
			map.put(314, 150);
			map.put(315, 131);
			map.put(316, 112);
			map.put(317, 93);
			map.put(318, 74);
			map.put(319, 55);
			map.put(320, 36);
			map.put(321, 17);
			map.put(322, 321);
			map.put(323, 302);
			map.put(324, 283);
			map.put(325, 264);
			map.put(326, 245);
			map.put(327, 226);
			map.put(328, 207);
			map.put(329, 188);
			map.put(330, 186);
			map.put(331, 167);
			map.put(332, 148);
			map.put(333, 129);
			map.put(334, 110);
			map.put(335, 91);
			map.put(336, 72);
			map.put(337, 53);
			map.put(338, 34);
			map.put(339, 15);
			String re = ";*H+(:N6FM<B+;FMSfT|OOCB1GR&bp2#qR_H*18_c13B+;.B+Zf+pK#.kZtz+c+tc4D^b2#k+)WVS<Wc25VF5EVM(D&OW%lpd+LqU.||5ldL>P3Z#>HFp|f>4+RR-L.k5*+BEMc(W^C5ycU4+Gddlcpd+-TFO4^92yAYc-t7y)GKBS<K*FzKFc_2l+pJKLAYP6zXlBT7FO%^92z<|OBWL.y8+pORjK8/RLYPz7CT|+(MYJ+J5(4+(D^kW)R-4BT|++G|2+pGOpNk+GZc5*tU<9zFFVU(RD|)MVbNJ/zK@VC|8M+H>)B.E2#l+-6zX*j^:NC/lp9fS<OByNUOzpB2";
		 
		//String cipher = bestRandom;
		 //cipher = "H+M8|CV@Kz/JNbVM)|DR(UVFFz9<Ut*5cZG+kNpOGp+2|G++|TB4-R)Wk^D(+4(5J+JYM(+|TC7zPYLR/8KjROp+8y.LWBO|<z29^%OF7TBlXz6PYALKJp+l2_cFKzF*K<SBKG)y7t-cYAy29^4OFT-+dpclddG+4Ucy5C^W(cMEB+*5k.L-RR+4>f|pFH>#Z3P>Ldl5||.UqL+dpl%WO&D(MVE5FV52cW<SVW)+k#2b^D4ct+c+ztZk.#Kp+fZ+B.;+B31c_81*H_Rq#2pb&RG1BCOO|TfSMF;+B<MF6N:(+H*;2BpzOUNyBO<Sf9pl/CN:^j*Xz6-+l#2E.B)>";

		 //String cipher = "8R_9MSppBy:Np+HER4T5*F<_@+#(G2G>Vp#ySc.b4t++5Vb/U+McG6<lz2RFOlBF5|N+&M(+TVc.b4t++^NF9G21*:49CE>VUZ5-+;WB|)L3zBK(Op^.fMqOSHT/()p+L16C<+Fl7tB_YOB*-CcWCzWcPHNpkSzZO8A|K;+dW<#(Bd2GTL1|kPV^lp>GZU+Mc)fK*<.YWD%OOp3V*8l^7JHz#L)(W4&+PF5|djtz+2KR++2FXkcCUd2*-OlF^/k#jfJ|;%qKzlzL(zD.A64K-+5L9d<Mc7p+O+DFOYBpBYNBy-U+Ry|TcR)++kF|DM>RJ|2Z^ERJ|f52KB8XBy.c";
			String cipher = "T41M4W189O951XS234E12A14V9E27E24X9X2NTWOO2XT91J2H2R32UXAXE89NRY151E389 5H6D7UA4IOR1V5A10EI6BE3PSV8WC7FJ4J3F12YF9D4Z10GT5K10IPXDW7L3KNNQ11QTWA5TR1012R11M5B11HBE8GLJYSY2UXC6TZ3GN4MOS7IC1H2 498PHIT6TI9TOLK5R5SO3FHT2W95T93PUF429AST245EY851378YO C5O1M7B9A8 71U3I8P3X8NLXKOZM9I26SWYPJCNI8296HXIT2CP9LHA6ES4RY2M578S62 46MLJPZIW77O33YXVEU6PONN1TRT6K2SQW499O17RSY47198V787412 M2N8LP4321TK23RD3E532A2QW83HH3R5SA19OI637611SR4U 923PJI3S6WXK7OMV9Y7U1NG4DE2CHQ5LRT54AHHPE734582W1E6 7S12O84P8HEC9K2GDBI2NLN6USQ9TJIFT5HMKY7TR1O66NY32795A 9S6P77SO2K5X3I8EIS352F4IT1RHLDS6Q82E31TARVY4961S5E2 4TY4I3W9ORPY724JE2O2N5F7QT8HXJE329OZE57RNY3M8916ZU15 26DCB9JI2ZXKRQ38VE79YWSOPM78HNKNG32ALEHEF5SY4T6361V41E829 3TRP9HGFPZI2ILJ1XKVU8IED5T4N1OSQOWKI9CYW7NE25AML46RVY58261 1J167OKP2OLB1NK4EQWFJI7J9N6O9T3H546LLE92VY82433 4J9W844L3PQIXJ29ZK72I35ZN8O663H6R1AWHQE198575SE4 15P67W8K67IF2WSQ778I2Q7HJZXKGRJ3MTY2942SE59SN2 M16#SP6O@E7SK2I4QFE!#YPN*XZVNE7NE9SXQW95$7ASY3MAI5Y83E44N827&J 46PUI4ZK@O8DX$THW6389L8O&JFN5QSHIES78#2E7W4RVY3912SN595@$91# 834XJ8QK6U74N5PYQ5XGHMWTJNR29AFH16XRRY92656SE4 44PXI59QKWKZ8KI3Y8NZ9BVQ6YBJ7XI53AJIWE96SY1524SP4 M2S2P37O&8Q#WV@J63#23O2T&6T183A@H15149458SE5";
		//	String cipher = Ciphers.cipher[0].cipher;

		List<RepeatingFragmentsBeanEntry> beans = repeatingFragmentsBeansFor(2, 20, cipher);
		for (RepeatingFragmentsBeanEntry bean : beans) {
			System.out.println(bean.toString(true));
			/*if (bean.key.contains("?") && bean.numNonWildcards() > 2)
				System.out.println(bean.toJS(map, 17));*/
			
		}
	}
	
	public static void test5() {
		String cipher = Ciphers.cipher[0].cipher;
		
		for (int i=0; i<1000000; i++) {
			cipher = CipherTransformations.shuffle(cipher);
			NGramsBean bean = new NGramsBean(2, cipher);
			Integer count = bean.counts.get("G2");
			if (count != null && count > 2) System.out.println(i + " G2 " + count);
			
			RepeatingFragmentsBean bean2 = repeatingFragments(4, cipher);
			Set<Integer> set = bean2.counts.get("G??2");
			if (set == null) continue;
			int count2 = bean2.counts.get("G??2").size();
			if (count2 > 2) System.out.println(i + " G??2 " + count2);
			
		}
	}
	
	public static void test6() {
		String cipher = "C0m0CRn2UAr7KdUZ4SFU-NyI14+IQEdajVdJGTSKzgQMg5VfABFL6klDRlDCCnoW8M3hUgqKOJ24b87G4drgBi+3tPmDw0CunGKebPdUe7XbpV-3d8ABt37pkx0xlDCungPKKB1ULhpSJP+N+IdzbXItq2zI5B1qiEWE2ZN+VUzIABFL+vDmDCwndjMYr+8tp7736fSdZ3oWzpYZjF8sLAYLPMUoi-c2XSFGgBFL6JPcDxv0CwngMBT6WQ2KS8Y15+doEo8Y4sJWOc57IbTY1OhUgBiS3tOklDkkDCwncfL1GK46Ec7rJ4I3UWrXFWKJgBFLyHUI3NonwDJtcInl";
		List<RepeatingFragmentsBeanEntry> beans = repeatingFragmentsBeansFor(2, 20, cipher);
		for (RepeatingFragmentsBeanEntry bean : beans) {
			System.out.println(bean.toString(true));
		}
		
	}
	
	public static void test7() {
		String cipher = Ciphers.cipher[0].cipher;
		RepeatingFragmentsBean bean = repeatingFragments(3, 9, cipher, false, 3);
		bean.dump();
		cipher = Ciphers.cipher[1].cipher;
		bean = repeatingFragments(3, 9, cipher, false, 3);
		bean.dump();
		
		
//		cipher = Ciphers.cipher[1].cipher;
//		bean = repeatingFragments(3, cipher);
//		System.out.println("Coverage: " + bean.coverage(true));
		
		// period test
		cipher = Ciphers.cipher[1].cipher;
		for (int n=1; n<=204; n++) {
			String re = Periods.rewrite3(cipher, n);
			bean = repeatingFragments(3, re, true, 0);
			float cov = bean.coverage();
			System.out.println(cov + "	" + n + "	z408");
		}
//		cipher = Ciphers.cipher[0].cipher;
//		for (int n=1; n<=170; n++) {
//			String re = Periods.rewrite3(cipher, n);
//			bean = repeatingFragments(3, re);
//			float cov = bean.coverage(true);
//			System.out.println(cov + "	" + n + "	z340");
//		}
		
		// shuffle test
//		float z340reference = 0.19117647f;
//		int equalOrBetterCount = 0;
//		cipher = Ciphers.cipher[0].cipher;
//		DescriptiveStatistics stats = new DescriptiveStatistics(); 
//		for (int i=0; i<1000000; i++) {
//			if (i % 10000 == 0) System.out.println(i+"...");
//			cipher = CipherTransformations.shuffle(cipher);
//			bean = repeatingFragments(3, cipher);
//			float cov = bean.coverage(true);
//			stats.addValue(cov);
//			if (cov >= z340reference) equalOrBetterCount++;
//		}
//		System.out.println("Equal or better: " + equalOrBetterCount);
//		System.out.println("Min " + stats.getMin() + " Max " + stats.getMax() + " Mean " + stats.getMean() +
//				" Median " + stats.getPercentile(50) + " Stddev " + stats.getStandardDeviation());

//		float z408reference = 0.3480392f;
//		int equalOrBetterCount = 0;
//		cipher = Ciphers.cipher[1].cipher;
//		DescriptiveStatistics stats = new DescriptiveStatistics(); 
//		for (int i=0; i<1000000; i++) {
//			if (i % 10000 == 0) System.out.println(i+"...");
//			cipher = CipherTransformations.shuffle(cipher);
//			bean = repeatingFragments(3, cipher);
//			float cov = bean.coverage();
//			stats.addValue(cov);
//			if (cov >= z408reference) equalOrBetterCount++;
//		}
//		System.out.println("Equal or better: " + equalOrBetterCount);
//		System.out.println("Min " + stats.getMin() + " Max " + stats.getMax() + " Mean " + stats.getMean() +
//				" Median " + stats.getPercentile(50) + " Stddev " + stats.getStandardDeviation());
		
	}
	
	static void testDumpFragments() {
//		String cipher = Ciphers.Z408;
		System.out.println("Begin");
		String cipher = "YMBFPTYSYSNAOSBYAACPTMYSNEMTMOIYJGTEBYNIAJAPSNDETMYLGAIdISYMIHFBYTFAAWOTYCMCHNEFTYIHSAWOYYHNEAWOWIWLTDIHAJBSASTFYTMMFdOETTDITWYGMTIPDTTOFYANRSITLOACYANWYAJAHCTDSIHNDONIPEICNCOACOHMOOOTYPMDISWtOKIEYWOP,R,AAOTWFFFYOTYOMIHLYDWICSTOTIhhNCFBYHNLAOWALYCWOWTDSOLIOMWWWYNTLTYMYFYLNAWNMWSSINDIDCWGOOBOYAYOFWGYKODLIAALACNSuFMSIAtOJBaHTYIANaWJSWDETOMYHABEODIASMGWIhdtTOTYHNdTIAPASYOAYdTSWBAAF,YFNSmTMOTOHYANSYJDWMABTdEKMMSAIHO,M,LTPYANWADNWTSWISAIHNGTSTBFANTdWL.IAAYYAARYNTTMIFLYTIABYWYEWGOTAYNTTMWITACOSSOCAYSLIYATMTTOTTNAAMDOTWYNSOETBRNBAHTMAYSIDKINADRHBOFTWTTFBWSYJSBYKIWBMATOGTDALAYANIATADWCBYDDLOAHWIAFHWSTAYSTFOHSELTODTNOAMaHMAHWCUALFMTFYM.YNWTGTOG.AHTCHASOD.ATRISWNB.WISIAAOHB,BYCSTSIPYAAFITIYWSTASIABBaagiWSYCIATOYSIAWyd,BHSAIIMYMMFUIOIYRYCHCLEFAOWNHOTSADIHIWYCTTACBIWLFYTTiiM,LPIDOE,GSSIiMaTCIDLSDMSFHATHTLTIFTNWIATOYLMJOMATLOIMFJMWYTOOMdYLDBLPCEAT";
//		cipher = CipherTransformations.shuffle(cipher);
		//cipher = "HER>pl^VPk|1LTG2d|c.3zBK(Op^.fMqG2U+R/5tE|DYBpbTMKOSpp7^l8*V3pO++RK2Np+B(#O%DWY.<*Kf)p8R^FlO-*dCkF>2D(#5+Kq%;2UcXGV.zL|RcT+L16C<+FlWB|)Ld<M+b+ZR2FBcyA64K2<clRJ|*5T4M.+&BF>MDHNpkSzZO8A|K;+-zlUV+^J+Op7<FBy-z69Sy#+N|5FBc(;8R|FkdW<7tB_YOB*-CcyBX1*:49CE>VUZ5-+_9M+ztjd|5FP+&4k/(G2Jfj#O+_NYz+@L9++)WCzWcPOSHT/()plGFN^f524b.cV4t++By:cM+UZGW()L#zHJ";
		//cipher = "KBS<K*FzKFc_2l+pJ*H+(:N6FByNUOzpB)TFO4^92yAYc-t7yN;lp9fS<O6zX*j^:pd-^C5ycU4+Gddlc+HC/E2#l+-@VC|8MBE+(W4+RR-L.k5*+(R>)B.NJ/zKzFFVU#>Mc|f>|5ldL>P3ZOpD|)MVbtU<92+pG%lHFpqU.|VM(D&OWD^Nk+GZc5*+G|4+(WVpd+L5VF5E2#k+)YPkW)R-4BT|+5(RL.kS<Wc2tc4D^bpK#<|z7CT|+(MYJ+J/z*1Ztz+c+;.B+Zf+HKLOBWL.y8+pORjK8fT8_c13B+&bp2#qR_GAYP6zXlBT7FO%^922|OOCB1GRM<B+;FMS"; // largo's
		//String cipher = "HTEGR2F-kCdcW<7tLB#yz:HYWD%O#JRBfL_&94Mkp3V*8lf|()+#.5z+L2Vc.b425^2VdA<6M4lf.^pO(KF(yUT+MRKpO+J^+V-Uybz(6;98SM+#+N|5FBcyZB5X-1zt:49CE>VURBc|T)+LL16C<+FlW|*>dO5^VPk|1B_YOB<.cJRyUZGW()+Kp*N+O+/+*jd|5FPFlBc&<+K|q%;2UcXG+N+FtG4+Kb+ZR2FBc2@G+(<7/pltE|DYBplBzF-.M4T5*^7KpRp+SFkCd*-OlBz^DR28>pzYN_+O#jf9JL23G.qcM|THSOPcW())WpCz+/+A8OZzSkpN+H;DKM|>";
		List<RepeatingFragmentsBeanEntry> beans = RepeatingFragments.repeatingFragmentsBeansFor(2, 9, cipher);
		for (RepeatingFragmentsBeanEntry bean : beans) {
			System.out.println(bean);
		}
		System.out.println("Median of top 51: " + Operations.measureFragments(cipher, 25));
	}
	
	/** for each position of the cipher, if it is involved with a repeated pattern, then
	 * mark it with the probability of the least probable pattern.
	 * 
	 * return the mean of all per-position probabilities.
	 * 
	 * any position not involved with a repeating pattern gets marked with probability of 1.
	 * 
	 * don't count "skipped" positions.
	 */
	public static double meanProbability(String cipher) {
		Map<Integer, Double> map = new HashMap<Integer, Double>();
		List<RepeatingFragmentsBeanEntry> beans = RepeatingFragments.repeatingFragmentsBeansFor(2, 9, cipher, false);
		for (RepeatingFragmentsBeanEntry bean : beans) {
			for (Integer pos : bean.positionsInt) {
				for (int i=0; i<bean.key.length(); i++) {
					if (bean.key.charAt(i) == '?') continue; // ignore wildcards;
					Integer key = pos+i;
					Double val = map.get(key);
					if (val == null) val = Double.MAX_VALUE;
					val = Math.min(val, bean.probability);
					map.put(key, val);
				}
			}
		}
		//System.out.println(map);
		double mean = 0;
		for (int i=0; i<cipher.length(); i++) {
			Double val = map.get(i);
			if (val == null) val = 1d;
			mean += val;
		}
		mean /= cipher.length();
		return mean;
	}
	
	/** this period for z340 has median probability (n=51) of 1.1185882E-9, compared to 6.5236065E-9 for period 1 */
	static void testDumpFragmentsPeriod84() {
		String cipher = Ciphers.cipher[0].cipher;
		cipher = Periods.rewrite3(cipher, 84);
		List<RepeatingFragmentsBeanEntry> beans = RepeatingFragments.repeatingFragmentsBeansFor(2, 9, cipher);
		for (RepeatingFragmentsBeanEntry bean : beans) {
			System.out.println(bean);
		}
		
	}
	
	/** some of ryan's transpositions have interesting fragments */
	static void testDumpFragmentsRyanGarlick() {
		String cipher = "OKMTbpBYDd2GTL1|kPDWY.<*Kf)5T4M.+&BFR8;(cBF5|JHz#L)(WGV3pO++RK24b.cV4t+++-5ZUV>EC/k4&+PF5|*dCkF>2D(Op^.fMqG2L)|BWlF+<|Lz.VGXcU+_NYz+@L9POSHT/()pcC-*BOY_BK46AycBF2+Op7<FBy-zZO8A|K;+|Et5/R+UV^lp>REHNp+B(#O%2<clRJ|*N+#yS96zZU+Mc:yBSpp7^l8*lGFN^f5294:*1XBydjtz+M9_p8R^FlO-|c.3zBK(C61L+TcR2;%qK+5#(G2Jfj#O++)WCzWct7<WdkF|RZ+b+M<d-zlUV+^J>MDHNpkS";
		List<RepeatingFragmentsBeanEntry> beans = RepeatingFragments.repeatingFragmentsBeansFor(2, 9, cipher);
		for (RepeatingFragmentsBeanEntry bean : beans) {
			System.out.println(bean);
		}
		System.out.println("Median of top 51: " + Operations.measureFragments(cipher, 25));
	}
	static void testFindFragment() {
		String cipher = Ciphers.cipher[0].cipher;
		System.out.println(findFragment(cipher, "2?c"));
	}
	
	static void testShuffleFragments(String cipher, String fragment, int shuffles) {
		System.out.println("Testing fragment " + fragment);
		//System.out.println(findFragment(cipher, "2?c"));
		StatsWrapper stats = new StatsWrapper();
		List<Integer> list = findFragment(cipher, fragment);
		stats.name = "Number of occurrences of fragment " + fragment;
		stats.actual = list.size();
//		int hits = 0;
//		DescriptiveStatistics stats = new DescriptiveStatistics(); 
		for (int i=0; i<shuffles; i++) {
			cipher = CipherTransformations.shuffle(cipher);
			list = findFragment(cipher, fragment);
			stats.addValue(list.size());
			if (stats.stats.getN() % 1000000 == 0) {
				System.out.println("Samples: " + stats.stats.getN());
				stats.output();
			}
		}
		System.out.println("Final stats:");
		stats.output();
	}
	/** a version of testShuffleFragments that doesn't store stats, so memory usage is very small */ 
	static void testShuffleFragmentsLowMem(String cipher, String fragment) {
		System.out.println("Testing fragment " + fragment + " in cipher " + cipher);
		Map<Integer, Long> counts = new HashMap<Integer, Long>(); 
		List<Integer> list = findFragment(cipher, fragment);
		System.out.println("Number of occurrences of fragment " + fragment + ": " + list.size());
		long samples = 0;
		while (true) {
			cipher = CipherTransformations.shuffle(cipher);
			samples++;
			list = findFragment(cipher, fragment);
			Long val = counts.get(list.size());
			if (val == null) val = 0l;
			val++;
			counts.put(list.size(), val);
			if (samples % 1000000 == 0) {
				System.out.println("Counts after " + samples + " samples: " + counts);
			}
		}
	}

	static void testShuffleFragmentsZ340(int shuffles) {
		String[] fragments = new String[] { "+???FBc", "J??p7", "#O???Y",
				"5?4?.", "H???p?^", "O?*?C", "c??????()", "G2", "D??p????O",
				"Np????O", "M??2?c", "|5F", "+?????)L", "2?c", "FB????R",
				"p????O", "G2???+", "FB" };
		int[] counts = new int[] { 2, 2, 2, 2, 2, 2, 2, 3, 2, 2, 2, 2, 2, 3, 2,
				3, 2, 3 };
		for (int i = 0; i < fragments.length; i++) {
			testShuffleFragments(Ciphers.Z340, fragments[i], shuffles);
		}
	}
	static void testShuffleFragmentsZ408(int shuffles) {
		String[] fragments = new String[] { "#B", "WV?eGY", "qEHM????k",
				"I)?q?85", "9#BP?R", "V?e?Y", "9#B", "U?k", "V?e", "A?#BV",
				"9#B???+", "q_", "5?S(", "F????@", "F??c?@", "5??L?l", "Tt",
				"(??U?k", "DR????\\", "p???W" };
		int[] counts = new int[] { 6, 2, 2, 2, 2, 3, 3, 4, 4, 2, 2, 4, 2, 3, 2, 2, 3, 2, 2, 3 };
		for (int i = 0; i < fragments.length; i++) {
			testShuffleFragments(Ciphers.Z408, fragments[i], shuffles);
		}
	}
	
	/** consider median of top 51 fragments based on rarity.
	 * shuffle to determine the average median.
	 */
	static void testShuffleMedianFragmentProbability(String cipher) {
		float valReference = Operations.measureFragments(cipher, 25);
		System.out.println("ref value " + valReference);
		DescriptiveStatistics stats = new DescriptiveStatistics();
		int hits = 0;
		int shuffles = 10000;
		for (int i=0; i<shuffles; i++) {
			cipher = CipherTransformations.shuffle(cipher);
			float val = Operations.measureFragments(cipher, 25); // median of top 51 entries
			if (val <= valReference) hits++;
			stats.addValue(val);
		}
		System.out.println("Hits: " + hits);
		System.out.println("Misses: " + (shuffles-hits));
		System.out.println("Min " + stats.getMin() + " Max " + stats.getMax() + " Mean " + stats.getMean() +
				" Median " + stats.getPercentile(50) + " Stddev " + stats.getStandardDeviation());

	}
	
	public static void testMeanProbability() {
		System.out.println(meanProbability(Ciphers.Z340));
		System.out.println(meanProbability(Ciphers.Z408));
		System.out.println(meanProbability(Ciphers.Z13));
		System.out.println(meanProbability(Ciphers.Z32));
	}
	
	/** shuffle tests for meanProbability measurement */
	public static void shuffleMeanProbability(String cipher, int shuffles) {
		DescriptiveStatistics stats = new DescriptiveStatistics();
		double meanReference = meanProbability(cipher);
		for (int i=0; i<shuffles; i++) {
			cipher = CipherTransformations.shuffle(cipher);
			stats.addValue(meanProbability(cipher));
		}
		double sigma = meanReference - stats.getMean();
		sigma /= stats.getStandardDeviation();
		
		System.out.println(stats.getMin()
				+ "	" + stats.getMax() + "	" + stats.getMean() + "	"
				+ stats.getPercentile(50) + "	"
				+ stats.getStandardDeviation() + "	" + meanReference + "	"
				+ sigma);
		
	}
	
	public static void shuffleCoverage(String cipher, int shuffles) {
		NGramsBean ng = new NGramsBean(2, cipher);
		double refCoverage = ng.coverageRatio();
		StatsWrapper stats = new StatsWrapper();
		stats.actual = refCoverage;
		stats.name = "Coverage";
		for (int i=0; i<shuffles; i++) {
			cipher = CipherTransformations.shuffle(cipher);
			ng = new NGramsBean(2, cipher);
			stats.addValue(ng.coverageRatio());
		}
		stats.output();
		
	}
	
	/** measure mean probabilities at all periods */
	public static void meanProbabilityPeriods(String cipher) {
		for (int n=1; n<=cipher.length()/2; n++) {
			String re = Periods.rewrite3(cipher, n);
			System.out.println(n + "	" + meanProbability(re));
		}
	}
	/** measure median of top 51 patterns at all periods */
	public static void medianProbabilityPeriods(String cipher) {
		for (int n=1; n<=cipher.length()/2; n++) {
			String re = Periods.rewrite3(cipher, n);
			float val = Operations.measureFragments(re, 25); // median of top 51 entries
			float val2 = n;
			val2 /= 19;
			System.out.println(n + "	" + val + "	" + val2);
		}
	}
	
	/** measure distance between sorted probability vector for the given cipher and the origin */
	public static double probabilityDistance(String cipher) {
		List<Double> list1 = new ArrayList<Double>();
		List<RepeatingFragmentsBeanEntry> beans1 = RepeatingFragments.repeatingFragmentsBeansFor(2, 9, cipher);
		for (RepeatingFragmentsBeanEntry bean : beans1) list1.add(bean.probability);
		Collections.sort(list1);
		
		double distance = 0;
		for (int i=0; i<list1.size(); i++) {
			double val1 = list1.get(i);
			distance += val1*val1;
		}
		distance = Math.sqrt(distance);
		return distance;
	}
	
	/** shuffle study for probability distance */
	public static void probabilityDistanceShuffles(String cipher, int shuffles) {
		StatsWrapper stats = new StatsWrapper();
		stats.name = "Probability distance";
		double actual = probabilityDistance(cipher);
		System.out.println("Actual: " + actual);
		stats.actual = actual;
		for (int i=0; i<shuffles; i++) {
			cipher = CipherTransformations.shuffle(cipher);
			double val = probabilityDistance(cipher);
			stats.addValue(val);
			if (val <= actual) stats.hits++;
		}
		stats.output();
	}

	/** measure distance between sorted probability vectors for the two given ciphers */
	public static double probabilityDistance(String cipher1, String cipher2) {
		List<Double> list1 = new ArrayList<Double>();
		List<Double> list2 = new ArrayList<Double>();
		List<RepeatingFragmentsBeanEntry> beans1 = RepeatingFragments.repeatingFragmentsBeansFor(2, 9, cipher1);
		List<RepeatingFragmentsBeanEntry> beans2 = RepeatingFragments.repeatingFragmentsBeansFor(2, 9, cipher2);
		for (RepeatingFragmentsBeanEntry bean : beans1) list1.add(bean.probability);
		for (RepeatingFragmentsBeanEntry bean : beans2) list2.add(bean.probability);
		Collections.sort(list1);
		Collections.sort(list2);
		System.out.println(list1);
		System.out.println(list2);
		
		double distance = 0;
		for (int i=0; i<Math.max(list1.size(), list2.size()); i++) {
			double val1 = 0;
			double val2 = 0;
			if (i<list1.size()) val1 = list1.get(i);
			if (i<list2.size()) val2 = list2.get(i);
			distance += (val1-val2)*(val1-val2);
		}
		distance = Math.sqrt(distance);
		return distance;
	}

	/** measure distance between sorted probability vectors for the given cipher compared 
	 * to random shuffles.
	 */
	public static double probabilityDistance(String cipher, int shuffles) {
		List<Double> actual = new ArrayList<Double>();
		List<Double> means = new ArrayList<Double>();
		// generate probability vector for given cipher 
		List<RepeatingFragmentsBeanEntry> beans1 = RepeatingFragments.repeatingFragmentsBeansFor(2, 9, cipher);
		for (RepeatingFragmentsBeanEntry bean : beans1) actual.add(bean.probability);
		Collections.sort(actual);

		// generate another vector for shuffles.  each element is the mean of the corresponding position 
		// in probability vectors for individual shuffles.
		for (int i=0; i<shuffles; i++) {
			cipher = CipherTransformations.shuffle(cipher);
			List<RepeatingFragmentsBeanEntry> beans2 = RepeatingFragments.repeatingFragmentsBeansFor(2, 9, cipher);
			List<Double> tmp = new ArrayList<Double>();
			for (RepeatingFragmentsBeanEntry bean : beans2) tmp.add(bean.probability);
			Collections.sort(tmp);
			for (int k=0; k<tmp.size(); k++) {
				Double val = tmp.get(k);
				if (k>=means.size()) means.add(val);
				else means.set(k, means.get(k)+val);
			}
		}
		// convert to mean
		for (int k=0; k<means.size(); k++) {
			means.set(k, means.get(k)/shuffles);
		}
		System.out.println(actual);
		System.out.println(means);
		double distance = 0;
		for (int i=0; i<Math.max(means.size(), actual.size()); i++) {
			double val1 = 0;
			double val2 = 0;
			if (i<means.size()) val1 = means.get(i);
			if (i<actual.size()) val2 = actual.get(i);
			distance += (val1-val2)*(val1-val2);
		}
		distance = Math.sqrt(distance);
		return distance;
	}
	
	public static void main(String[] args) {
//		test2();
		//test4();
		//test3();
		//test6();
		//test7();
		testDumpFragments();
//		System.out.println(probabilityDistance(Ciphers.Z340, CipherTransformations.shuffle(Ciphers.Z340)));
		//System.out.println(probabilityDistance(CipherTransformations.shuffle(Ciphers.Z408), 1000));
		//System.out.println(probabilityDistance(Ciphers.Z408, 1000));
//		System.out.println(probabilityDistance(Ciphers.Z408));
//		System.out.println(probabilityDistance(Ciphers.Z340));
		//probabilityDistanceShuffles(Ciphers.Z340, 1000);
		//System.out.println(probabilityDistance(Ciphers.Z408, 100));
		//shuffleCoverage(Ciphers.Z340, 10000);
//		String[] fragments = new String[] { "+???FBc", "J??p7", "#O???Y",
//				"5?4?.", "H???p?^", "O?*?C", "c??????()", "G2", "D??p????O",
//				"Np????O", "M??2?c", "|5F", "+?????)L", "2?c", "FB????R",
		//testShuffleFragments(Ciphers.Z408, "WV?eGY", 1000000000);
//		testShuffleFragmentsLowMem(Ciphers.Z340, "5?4?.");
		//testMeanProbability();
		//meanProbabilityPeriods(Ciphers.Z340);
		//medianProbabilityPeriods(Ciphers.Z340);
		//shuffleMeanProbability(Ciphers.Z408, 1000);
		//shuffleMeanProbability(Ciphers.Z340, 1000);
		//testShuffleMedianFragmentProbability();
		//testFindFragment();
		//testDumpFragmentsPeriod84();
		//testDumpFragmentsRyanGarlick();
		//testShuffleFragments(Ciphers.Z340, "G2", 3, 1000000); 
		//testShuffleFragments(Ciphers.Z340, "G?2", 3, 1000000); 
		//testShuffleFragmentsZ340(1000);
		//testShuffleFragmentsZ408(10000000);
	}
}
