package com.zodiackillerciphers.ngrams;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.tests.jarlve.JarlveMeasurements;
import com.zodiackillerciphers.transform.CipherTransformations;

public class RepeatingFragments {

	/** if removeSubstrings true, then get rid of any fragment that is a substring of a larger fragment.*/ 
	public static List<RepeatingFragmentsBeanEntry> repeatingFragmentsBeansFor(int n1, int n2, String cipher) {
		boolean removeSubstrings = true;
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
		//int sum = 0;
		RepeatingFragmentsBean bean = new RepeatingFragmentsBean(cipher);
		
		
		Set<String> seen = new HashSet<String>();
		
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
		bean.makeBeans();
		return bean;
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
	
	public static void main(String[] args) {
		//test2();
		//test4();
		//test3();
		test6();
	}
}
