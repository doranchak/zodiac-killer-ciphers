package com.zodiackillerciphers.ciphers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.math3.util.OpenIntToDoubleHashMap.Iterator;

import com.zodiackillerciphers.ciphers.algorithms.Bifid;
import com.zodiackillerciphers.cosine.CosineSimilarity;
import com.zodiackillerciphers.cosine.CosineSimilarityResult;
import com.zodiackillerciphers.io.FileUtil;
import com.zodiackillerciphers.lucene.ZKDecrypto;
import com.zodiackillerciphers.ngrams.Periods;
import com.zodiackillerciphers.transform.CipherTransformations;
import com.zodiackillerciphers.transform.TransformationBase;

public class Ciphers {

	static int which = 5;

	/** convert webtoy transcription to Largo's font */
	static Map<Character, Character> mapLargoZ340 = new HashMap<Character, Character>();
	static {
		mapLargoZ340.put('#','k');
		mapLargoZ340.put('%','l');
		mapLargoZ340.put('&','3');
		mapLargoZ340.put('(','j');
		mapLargoZ340.put(')','q');
		mapLargoZ340.put('*','o');
		mapLargoZ340.put('+','i');
		mapLargoZ340.put('-','6');
		mapLargoZ340.put('.','m');
		mapLargoZ340.put('/','5');
		mapLargoZ340.put('1','f');
		mapLargoZ340.put('2','g');
		mapLargoZ340.put('3','x');
		mapLargoZ340.put('4','4');
		mapLargoZ340.put('5','7');
		mapLargoZ340.put('6','#');
		mapLargoZ340.put('7','v');
		mapLargoZ340.put('8','w');
		mapLargoZ340.put('9','z');
		mapLargoZ340.put(':','s');
		mapLargoZ340.put(';','8');
		mapLargoZ340.put('<','n');
		mapLargoZ340.put('>','a');
		mapLargoZ340.put('@','9');
		mapLargoZ340.put('A','A');
		mapLargoZ340.put('B','B');
		mapLargoZ340.put('C','C');
		mapLargoZ340.put('D','D');
		mapLargoZ340.put('E','E');
		mapLargoZ340.put('F','F');
		mapLargoZ340.put('G','G');
		mapLargoZ340.put('H','H');
		mapLargoZ340.put('J','J');
		mapLargoZ340.put('K','K');
		mapLargoZ340.put('L','L');
		mapLargoZ340.put('M','M');
		mapLargoZ340.put('N','N');
		mapLargoZ340.put('O','O');
		mapLargoZ340.put('P','P');
		mapLargoZ340.put('R','R');
		mapLargoZ340.put('S','S');
		mapLargoZ340.put('T','T');
		mapLargoZ340.put('U','U');
		mapLargoZ340.put('V','V');
		mapLargoZ340.put('W','W');
		mapLargoZ340.put('X','X');
		mapLargoZ340.put('Y','Y');
		mapLargoZ340.put('Z','Z');
		mapLargoZ340.put('^','d');
		mapLargoZ340.put('_','y');
		mapLargoZ340.put('b','0');
		mapLargoZ340.put('c','t');
		mapLargoZ340.put('d','h');
		mapLargoZ340.put('f','p');
		mapLargoZ340.put('j','2');
		mapLargoZ340.put('k','e');
		mapLargoZ340.put('l','c');
		mapLargoZ340.put('p','b');
		mapLargoZ340.put('q','Q');
		mapLargoZ340.put('t','1');
		mapLargoZ340.put('y','r');
		mapLargoZ340.put('z','u');
		mapLargoZ340.put('|','I');		
	}
	static Map<Character, Character> mapLargoZ408 = new HashMap<Character, Character>();
	static {
		mapLargoZ408.put('!','j');
		mapLargoZ408.put('#','r');
		mapLargoZ408.put('%','b');
		mapLargoZ408.put('(','1');
		mapLargoZ408.put(')','p');
		mapLargoZ408.put('+','+');
		mapLargoZ408.put('/','c');
		mapLargoZ408.put('5','o');
		mapLargoZ408.put('6','h');
		mapLargoZ408.put('7','m');
		mapLargoZ408.put('8','w');
		mapLargoZ408.put('9','a');
		mapLargoZ408.put('=','e');
		mapLargoZ408.put('@','i');
		mapLargoZ408.put('A','A');
		mapLargoZ408.put('B','B');
		mapLargoZ408.put('D','D');
		mapLargoZ408.put('E','E');
		mapLargoZ408.put('F','F');
		mapLargoZ408.put('G','G');
		mapLargoZ408.put('H','H');
		mapLargoZ408.put('I','I');
		mapLargoZ408.put('J','J');
		mapLargoZ408.put('K','K');
		mapLargoZ408.put('L','L');
		mapLargoZ408.put('M','M');
		mapLargoZ408.put('N','N');
		mapLargoZ408.put('O','O');
		mapLargoZ408.put('P','P');
		mapLargoZ408.put('Q','Q');
		mapLargoZ408.put('R','R');
		mapLargoZ408.put('S','S');
		mapLargoZ408.put('T','T');
		mapLargoZ408.put('U','U');
		mapLargoZ408.put('V','V');
		mapLargoZ408.put('W','W');
		mapLargoZ408.put('X','X');
		mapLargoZ408.put('Y','Y');
		mapLargoZ408.put('Z','Z');
		mapLargoZ408.put('\\','z');
		mapLargoZ408.put('^','l');
		mapLargoZ408.put('_','q');
		mapLargoZ408.put('c','C');
		mapLargoZ408.put('d','v');
		mapLargoZ408.put('e','g');
		mapLargoZ408.put('f','s');
		mapLargoZ408.put('j','u');
		mapLargoZ408.put('k','d');
		mapLargoZ408.put('l','t');
		mapLargoZ408.put('p','f');
		mapLargoZ408.put('q','k');
		mapLargoZ408.put('r','y');
		mapLargoZ408.put('t','n');
		mapLargoZ408.put('z','x');
	}

	public static String Z408_SOLUTION = "ilikekillingpeoplebecauseitissomuchfunitismorefunthankillingwildgameintheforrestbecausemanisthemoatdangertueanamalofalltokillsomethinggivesmethemoatthrillingexperenceitisevenbetterthangettingyourrocksoffwithagirlthebestpartofitiathaewhenidieiwillbereborninparadicesndalltheihavekilledwillbecomemyslavesiwillnotgiveyoumynamebecauseyouwilltrytosloidownorstopmycollectingofslavesformyafterlifeebeorietemethhpiti";
	public static String Z408_SOLUTION_18 = "ebeorietemethhpiti";
	public static String ALIK_SOLUTION = "idecidedtowriteacipherthatcloselyresemblestheonecreatedthatnoonehassolvedyetiwantthenumberofcharacterstomatchsotwatwecanuseittoimprovethetoolsweusetoexploretheoriginalunsolhedcipheratthispointiwouldliketomentionidonotcollectslahesandiknowhowtoproperlyspellparadiceiwouldalsoliketomentioniamhungryinmyafterlifeslaveswillmakemefreshsandwiches";
	public static Cipher[] cipher = {
		// 340 character unsolved Zodiac cipher
		//original one with mistakes from http://www.dtm.ciw.edu/chris/z/340explain.html    "HER>pl^VPkI1LTG2dNp+B(#O%DWY.<\\Kf)By:cM+UZGW()L#zHJSpp7^l8\\V3pO++RK2_9M+ztjdI5FP+&4k/p8R^FlO-\\dCkF>2D(#5+Kq%;2UcXGV.zLI(G2Jfj#O+_NYz+@L9d<M+b+ZR2FBcyA64K-zlUV+^J+Op7<FBy-U+R/5tEIDYBpbTMKO2<clRjI\\5T5M.+&BFz69Sy#+NI5FBc(;8RlGFN^f525b.cV5t++yBX1\\:49CE>VUZ5-+Ic.3zBK(Op^.fMqG2RcT+L52C<+FlWBI)L++)WCzWcPOSHT/()pIFkdW<7tB_YOB\\-Cc>MDHNpkSzZO8AIK;+",
		//original webtoy transcription: "HER>pl^VPkI1LTG2dNp+B(#O%DWY.<\\Kf)By:cM+UZGW()L#zHJSpp7^l8\\V3pO++RK2_9M+ztjdI5FP+&4k/p8R^FlO-\\dCkF>2D(#5+Kq%;2UcXGV.zLI(G2Jfj#O+_NYz+@L9d<M+b+ZR2FBcyA64K-zlUV+^J+Op7<FBy-U+R/5tEIDYBpbTMKO2<clRJI\\5T4M.+&BFz69Sy#+NI5FBc(;8RlGFN^f524b.cV4t++yBX1\\:49CE>VUZ5-+Ic.3zBK(Op^.fMqG2RcT+L16C<+FlWBI)L++)WCzWcPOSHT/()pIFkdW<7tB_YOB\\-Cc>MDHNpkSzZO8AIK;+",
		new Cipher("Z340: Zodiac killer's unsolved 340 cipher", "HER>pl^VPk|1LTG2dNp+B(#O%DWY.<*Kf)By:cM+UZGW()L#zHJSpp7^l8*V3pO++RK2_9M+ztjd|5FP+&4k/p8R^FlO-*dCkF>2D(#5+Kq%;2UcXGV.zL|(G2Jfj#O+_NYz+@L9d<M+b+ZR2FBcyA64K-zlUV+^J+Op7<FBy-U+R/5tE|DYBpbTMKO2<clRJ|*5T4M.+&BFz69Sy#+N|5FBc(;8RlGFN^f524b.cV4t++yBX1*:49CE>VUZ5-+|c.3zBK(Op^.fMqG2RcT+L16C<+FlWB|)L++)WCzWcPOSHT/()p|FkdW<7tB_YOB*-Cc>MDHNpkSzZO8A|K;+",null, 17, 20, "http://zodiackillerciphers.com/wiki/index.php?title=Unsolved_340-character_cipher", true),
		new Cipher("Z408: Zodiac killer's solved 408 cipher","9%P/Z/UB%kOR=pX=BWV+eGYF69HP@K!qYeMJY^UIk7qTtNQYD5)S(/9#BPORAU%fRlqEk^LMZJdr\\pFHVWe8Y@+qGD9KI)6qX85zS(RNtIYElO8qGBTQS#BLd/P#B@XqEHMU^RRkcZKqpI)Wq!85LMr9#BPDR+j=6\\N(eEUHkFZcpOVWI5+tL)l^R6HI9DR_TYr\\de/@XJQAP5M8RUt%L)NVEKH=GrI!Jk598LMlNA)Z(PzUpkA9#BVW\\+VTtOP^=SrlfUe67DzG%%IMNk)ScE/9%%ZfAP#BVpeXqWq_F#8c+@9A9B%OT5RUc+_dYq_^SqWVZeGYKE_TYA9%#Lt_H!FBX9zXADd\\7L!=q_ed##6e5PORXQF%GcZ@JTtq_8JI+rBPQW6VEXr9WI6qEHM)=UIk",Z408_SOLUTION, 17, 24, "http://zodiackillerciphers.com/wiki/index.php?title=Solved_408-character_cipher", true),
		new Cipher("Z408: Zodiac killer's solved 408 cipher (first 340 only)","9%P/Z/UB%kOR=pX=BWV+eGYF69HP@K!qYeMJY^UIk7qTtNQYD5)S(/9#BPORAU%fRlqEk^LMZJdr\\pFHVWe8Y@+qGD9KI)6qX85zS(RNtIYElO8qGBTQS#BLd/P#B@XqEHMU^RRkcZKqpI)Wq!85LMr9#BPDR+j=6\\N(eEUHkFZcpOVWI5+tL)l^R6HI9DR_TYr\\de/@XJQAP5M8RUt%L)NVEKH=GrI!Jk598LMlNA)Z(PzUpkA9#BVW\\+VTtOP^=SrlfUe67DzG%%IMNk)ScE/9%%ZfAP#BVpeXqWq_F#8c+@9A9B%OT5RUc+_dYq_^SqWVZeGYKE_TYA9%#Lt_",Z408_SOLUTION, 17, 24, "http://zodiackillerciphers.com/wiki/index.php?title=Solved_408-character_cipher", true),
		new Cipher("Z13: Zodiac killer's unsolved 13-char cipher", "AENz0K0M0[NAM", null, 13, 1, "http://zodiackillerciphers.com/wiki/index.php?title=Unsolved_13-character_%22My_name_is%22_cipher", true), // old: AENz8K8M8tNAM
		new Cipher("Z32: Zodiac killer's unsolved 32-char cipher", "C9J|#Ok[AMf8?ORTGX6FDVj%HCELzPW9", null, 17, 2, "http://zodiackillerciphers.com/wiki/index.php?title=Unsolved_32-character_%22map_code%22_cipher", true), // old: C9J|#OktAMf8oORTGX6FDVj%HCELzPW9  

		// 4: flipped and rotated, equivalent to reading 340 from top to bottom and left to right.
		new Cipher("Z340 Reading from top to bottom and left to right","HNBS_p#(d-U2zly|R+|>Epyp985G<z+<6GBcc+FMR+:pMR+2MlRc9FX.T)kD>Bc7+^KJ+U/lSN13+WdHp(M^zFqfbV5Ry^*zLCWNl#+ltl%j++tJ#f:B1z<p^OU8jO;#Z^E|+54K6W7kV%Z*d-2ORJ|*N29(CctSPDGV|*U+2+D5|4CO<PBzkWW35dc_FOYT5bEp+O_Z|Y(pFCXNBpB4F.>^FSYO1.)OPkGYc7pMBcV.lHO8L<L++FVzy<b.cVUfWTBAT*#+&>.+AFT+(4ZMB/*|GKzR42z@6BM&;t5q|(-K2fHKkDLL4yKB8+-G))C;d)J2/(|9K-OFR++2Lpc+", null),
		// 5: horizontal flip only.  this produced greater homophone-related scores in several experiments. 
		new Cipher("Z340 Horizontal flip", "d2GTL1|kPV^lp>REH)fK*<.YWD%O#(B+pNJHz#L)(WGZU+Mc:yB2KR++Op3V*8l^7ppS/k4&+PF5|djtz+M9_(D2>FkCd*-OlF^R8p|Lz.VGXcU2;%qK+5#9L@+zYN_+O#jfJ2G(K46AycBF2RZ+b+M<d-yBF<7pO+J^+VUlz-OKMTbpBYD|Et5/R+UFB&+.M4T5*|JRlc<2R8;(cBF5|N+#yS96z++t4Vc.b425f^NFGl+-5ZUV>EC94:*1XBy2GqMf.^pO(KBz3.c|L)|BWlF+<C61L+TcRp)(/THSOPcWzCW)++cC-*BOY_Bt7<WdkF|+;K|A8OZzSkpNHDM>", null),		
		//  original 340 concatenated with flip/rotation, used to find bidirectional repeated n-grams
		//new Cipher("Z340 - original 340 concatenated with flip/rotation, used to find bidirectional repeated n-grams", "HER>pl^VPk|1LTG2dNp+B(#O%DWY.<*Kf)By:cM+UZGW()L#zHJSpp7^l8*V3pO++RK2_9M+ztjd|5FP+&4k/p8R^FlO-*dCkF>2D(#5+Kq%;2UcXGV.zL|(G2Jfj#O+_NYz+@L9d<M+b+ZR2FBcyA64K-zlUV+^J+Op7<FBy-U+R/5tE|DYBpbTMKO2<clRJ|*5T4M.+&BFz69Sy#+N|5FBc(;8RlGFN^f524b.cV4t++yBX1*:49CE>VUZ5-+|c.3zBK(Op^.fMqG2RcT+L16C<+FlWB|)L++)WCzWcPOSHT/()p|FkdW<7tB_YOB*-Cc>MDHNpkSzZO8A|K;+HNBS_p#(d-U2zly|R+|>Epyp985G<z+<6GBcc+FMR+:pMR+2MlRc9FX.T)kD>Bc7+^KJ+U/lSN13+WdHp(M^zFqfbV5Ry^*zLCWNl#+ltl%j++tJ#f:B1z<p^OU8jO;#Z^E|+54K6W7kV%Z*d-2ORJ|*N29(CctSPDGV|*U+2+D5|4CO<PBzkWW35dc_FOYT5bEp+O_Z|Y(pFCXNBpB4F.>^FSYO1.)OPkGYc7pMBcV.lHO8L<L++FVzy<b.cVUfWTBAT*#+&>.+AFT+(4ZMB/*|GKzR42z@6BM&;t5q|(-K2fHKkDLL4yKB8+-G))C;d)J2/(|9K-OFR++2Lpc+", null),		
		// 6: best version of the 340 from quadrant experiment 2
		new Cipher("Z340 Best version from quadrant experiment 2","*V3pO++RK28l^7ppSZGW()L#zHJU+Mc:yB%DWY.<*Kf)O#(B+pNVPk|1LTG2d^lp>REH+;K|A8OZzSkpNHDM>cC-*BOY_Bt7<WdkF|p)(/THSOPcWzCW)++L)|BWlF+<C61L+TcR2GqMf.^pO(KBz3.c|+-5ZUV>EC94:*1XBy++t4Vc.b425f^NFGlR8;(cBF5|N+#yS96zFB&+.M4T5*|JRlc<2OKMTbpBYD|Et5/R+U-yBF<7pO+J^+VUlz-K46AycBF2RZ+b+M<d9L@+zYN_+O#jfJ2G(|Lz.VGXcU2;%qK+5#(D2>FkCd*-OlF^R8p/k4&+PF5|djtz+M9_", null, "http://zodiackillerciphers.com/wiki/index.php?title=Quadrant_analysis_Part_2"),
		// 7
		new Cipher("Z340 Re-arrangement by Jurgen Koller", "HER>pl^VPk|1LTGNp+B(#O%DWY.<*KBy:cM+UZGW()L#zSpp7^l8*V3pO++R_9M+ztjd|5FP+&4p8R^FlO-*dCkF>2#5+Kq%;2UcXGV.z(G2Jfj#O+_NYz+2df)HJK2k/D(L|@L9d<M+b+ZR2FBcyA64K-zlUV+^J+Op7<FBy-U+R/5tE|DYBpbTMKO2<clRJ|*5T4M.+&BFz69Sy#+N|5FBc(;8RlGFN^f524b.cV4t++yBX1*:49CE>VUZ5-+|c.3zBK(Op^.fMqG2RcT+L16C<+FlWB|)L++)WczWcPOSHT/()p|FkdW<7tB_YOB*-Cc>MDHNpkSzZO8A|K;+", null),
		// 8
		new Cipher("Z340 Re-arrangement by Nick Pelling", "d>2k|RTLGVlH1^EpP)BfWY+*<K%#N.Op(DJcHW(:#LzZ+B)UyMG27K3pp++R*lSO8p^V/+k5FM&+4dt_Pj9z|(^DdCR>F2-lpkO8F*|KLcX+.Vz2%#G;5qU9JL_N2+z@Oj(Y#Gf+K+4FBMAy6R+dcZ<b2-UyOplF<BJ+-7^zV+O/KYBRTbM|tUpE+5DFlBT4c+.&*J2M|<R5RS85F9(c;N#zB+6y|+N+b.F4Vt2flc5G^4+1-E>XZU59:yV4B*C23Gp^.Mfq(B|.KczOL+)+FTBW|C1Rl6cL<pW)OS)/T(cz+HW+CPcdC_Yk*B-t<|O7FWB+H;ZOD|AKSp>8kMNz", null),
		// 9: the 340 with all + symbols removed
		new Cipher("Z340 All + symbols removed", "HER>pl^VPk|1LTG2dNpB(#O%DWY.<*Kf)By:cMUZGW()L#zHJSpp7^l8*V3pORK2_9Mztjd|5FP&4k/p8R^FlO-*dCkF>2D(#5Kq%;2UcXGV.zL|(G2Jfj#O_NYz@L9d<MbZR2FBcyA64K-zlUV^JOp7<FBy-UR/5tE|DYBpbTMKO2<clRJ|*5T4M.&BFz69Sy#N|5FBc(;8RlGFN^f524b.cV4tyBX1*:49CE>VUZ5-|c.3zBK(Op^.fMqG2RcTL16C<FlWB|)L)WCzWcPOSHT/()p|FkdW<7tB_YOB*-Cc>MDHNpkSzZO8A|K;", null, "https://github.com/glurk/zkdecrypto/blob/master/zkdecrypto/cipher/340.zodiac.noplus.txt"),
		// 10
		new Cipher("Z340 All + symbols replaced by unique symbols", "HER>��^VP�I�LTG��N�aB��O�DWy�<�KƣB���MeuZGW��L��HJS�н^̾�V��OghRK���Mi��j�I�FPm���/оR^F�O-��C�F>�D���nKѺ��u�XGV��LI�G�J�j�Oo�Ny�r�L��<Ms�uZR�FBßA��K-��uVv^JwOн<FB�-uxR/��EIDyB��TMKO�<��RJI��T�M�Q�BF���S��0NI�FBâ��R�GFN^Ƶ���V��`~�BX�����CE>VuZ�-!IÕ��BK�O�^��M�G�R�T$L��C<=F�WBI�L{}�WC�W�POSHT/���IF��W<��B�yOB�-C�>MDHN��S�ZO�AIK�+", null, "https://github.com/glurk/zkdecrypto/blob/master/zkdecrypto/cipher/340.zodiac.uniplus.txt"),
		// 11
		new Cipher("Z340 Top half only","HER>pl^VPk|1LTG2dNp+B(#O%DWY.<*Kf)By:cM+UZGW()L#zHJSpp7^l8*V3pO++RK2_9M+ztjd|5FP+&4k/p8R^FlO-*dCkF>2D(#5+Kq%;2UcXGV.zL|(G2Jfj#O+_NYz+@L9d<M+b+ZR2FBcyA64K-zlUV+^J+Op7<FBy-",null),
		// 12
		new Cipher("Z340 Bottom half only","U+R/5tE|DYBpbTMKO2<clRJ|*5T4M.+&BFz69Sy#+N|5FBc(;8RlGFN^f524b.cV4t++yBX1*:49CE>VUZ5-+|c.3zBK(Op^.fMqG2RcT+L16C<+FlWB|)L++)WCzWcPOSHT/()p|FkdW<7tB_YOB*-Cc>MDHNpkSzZO8A|K;+",null),
		// 13 
		new Cipher("Z340 In oxcart path by kfreeze", "HER>��^VP�I�LTG�ģ�K�<�yWD�O��B+�NB���M+uZGW��L��HJ�KR++OдV���^���S��M+��j�I�FP+���/�D�>F�CĻ-O�F^R�з�+KѺ��u�XGV��LI�L�+�yN�+O�j�J�G��<M+�+ZR�FBßA��K-�BF<��O+J^+Vṳ-u+R/��EIDyB��TMKOFB�+�M�T��IJR��<����S��+NI�FBâ��R++ԳVÕ³���^NFG̟BX�����CE>VuZ�-+�G�Mƕ^�O�KB����IR�T+L��C<+F�WBI�LУ�/THSOP�W�CW�++IF��W<��B�yOB�-C�+�KIA�OZ�S��NHDM>", null, "https://github.com/glurk/zkdecrypto/blob/master/zkdecrypto/cipher/340.zodiac.oxcart.txt"),
		// period 19 scheme
		new Cipher("Z340 Period 19 scheme", "H+M8|CV@K+l#2E.B)>EB+*5k.L-RR+4>f|pMR(UVFFz9z/JNbVM)|D>#Z3P>Ldl5||.UqLFHpOGp+2|<Ut*5cZG+kNl%WO&D(MVE5FV52+dp^D(+4(G++|TB4-R)WkVW)+k#2b^D4ct+cW<SPYLR/5J+JYM(+|TC7zk.#Kp+fZ+B.;+c+ztZ|<z28KjROp+8y.LWBO1*H_Rq#2pb&RB31c_8LKJ9^%OF7TBlXz6PYATfSMF;+B<MFG1BCOO|G)p+l2_cFKzF*K<SBK2BpzOUNyBO6N:(+H*;dy7t-cYAy29^4OFT-+N:^j*Xz6-<Sf9pl/CpclddG+4Ucy5C^W(c", null, "http://zodiackillerciphers.com/period-19-bigrams/"),
		// period 15 scheme
		new Cipher("Z340 Period 15 scheme", "dEB+*5k.L(MVE5FV52c+ztZ2H+M8|CV@K<Ut*5cZG|TC7zG)pclddG+4dl5||.UqLcW<STfN:^j*Xz6-z/JNbVM)R)WkLKJy7t-cYAy-RR+4>f|p+dp1*HBpzOUNyBO+l#2E.B)+kN|<z2p+l2_cFKUcy5C^W(cFHk.#KSMF;+B<MF<Sf9pl/C|DPYLR/9^%OF7TB29^4OFT-+MVW)+k_Rq#2pb&R6N:(+H*;>^D(+4(8KjROp+8zF*K<SBKl%WO&Dp+fZ+B.;+G1BCOO|pOGp+2|5J+JYM(+lXz6PYA>#Z3P>L#2b^D4ct+B31c_8R(UVFFz9G++|TB4-y.LWBO", null, "http://zodiackillerciphers.com/period-19-bigrams/"),
		// 3: a random permutation of rows of the 408.  used to test heuristic searches for homophone candidates.
		// the permutation is:  47 11 39 44 38 3 18 19 31 32 29 33 10 46 37 26 25 28 12 17 40 45 0 6
		new Cipher("Z408 Random permutation of rows","Z@JTtq_8JI+rBPQW6VB#PAfZ%%9/EcS)kNMI%%GzD76eUflrS=^@+qGD9KI)6qX85zS(B9A9@+c8#F_qWqXepH6R^l)Lt+5IWVOpcZVEXr9WI6qEHM)=UIk#9rML58!qW)IpqKZcFkHUEe(N\\6=j+RDPBAQJX@/ed\\rYT_RD9IP5M8RUt%L)NVEKH=GWV+eGYF69HP@K!qYe%OT5RUc+_dYq_^SqWPOtTV+\\WVB#9AkpUzY8eWVHFp\\rdJZML^k)5DYQNtTq7kIU^YJMq=!L7\\dDAXz9XBF!HVZeGYKE_TYA9%#Lt_RNtIYElO8qGBTQS#BLd/P#B@XqEHMU^RRkEqlRf%UAROPB#9/(ScG%FQXROP5e6##de_P(Z)ANlML895kJ!IrB=Xp=ROk%BU/Z/P%9", Z408_SOLUTION, true),
		// 4: a grid-oriented permutation of the 408 used for structure-based seearches
		// permutation is: 0,30,1,31,2,32,3,33,4,34,5,35,17,47,16,46,15,45,14,44,13,43,12,42
		new Cipher("Z408 Grid-oriented permutation used for structure-based seearches","9%P/Z/UB%kOR=pX=BB#SQTBGq8OlEYItNRWV+eGYF69HP@K!qYekRR^UMHEqX@B#P/dLMJY^UIk7qTtNQYD5)#9rML58!qW)IpqKZcS(/9#BPORAU%fRlqEFkHUEe(N\\6=j+RDPBk^LMZJdr\\pFHVWe8YH6R^l)Lt+5IWVOpcZ@+qGD9KI)6qX85zS(AQJX@/ed\\rYT_RD9IpeXqWq_F#8c+@9A9BkIU=)MHEq6IW9rXEVNk)ScE/9%%ZfAP#BV6WQPBr+IJ8_qtTJ@Z^=SrlfUe67DzG%%IMcG%FQXROP5e6##de_zUpkA9#BVW\\+VTtOPq=!L7\\dDAXz9XBF!HrI!Jk598LMlNA)Z(P_tL#%9AYT_EKYGeZVP5M8RUt%L)NVEKH=GWqS^_qYd_+cUR5TO%", Z408_SOLUTION, true),
		// 8: best version of the 408 from quadrant experiment 2
		new Cipher("Z408 Best version from quadrant experiment 2", "VB#PAfZ%%9/EcS)kNMI%%GzD76eUflrS=^POtTV+\\WVB#9AkpUzP(Z)ANlML895kJ!IrG=HKEVN)L%tUR8M5PAQJX@/ed\\rYT_RD9IH6R^l)Lt+5IWVOpcZFkHUEe(N\\6=j+RDPB#9rML58!qW)IpqKZckRR^UMHEqX@B#P/dLB#SQTBGq8OlEYItNR(Sz58Xq6)IK9DGq+@Y8eWVHFp\\rdJZML^kEqlRf%UAROPB#9/(S)5DYQNtTq7kIU^YJMeYq!K@PH96FYGe+VWB=Xp=ROk%BU/Z/P%9kIU=)MHEIODYd86WQPBr+qJPAT_#cG%FQXR685X_+Fq=!L7\\dI_ezEc__tL#%9AWq69KUqWqS^_qY9t#XYRWB9A9@+crT#BG5qXJdFeTXE@e!ZOeVZ_HV%p", Z408_SOLUTION, "http://zodiackillerciphers.com/wiki/index.php?title=Quadrant_analysis_Part_2", true),

		new Cipher("Z408 1st section only","9%P/Z/UB%kOR=pX=BWV+eGYF69HP@K!qYeMJY^UIk7qTtNQYD5)S(/9#BPORAU%fRlqEk^LMZJdr\\pFHVWe8Y@+qGD9KI)6qX85zS(RNtIYElO8qGBTQS#BLd/P#B@XqEHMU^RRk",Z408_SOLUTION, "http://zodiackillerciphers.com/wiki/index.php?title=Solved_408-character_cipher", true),
		new Cipher("Z408 2nd section only","cZKqpI)Wq!85LMr9#BPDR+j=6\\N(eEUHkFZcpOVWI5+tL)l^R6HI9DR_TYr\\de/@XJQAP5M8RUt%L)NVEKH=GrI!Jk598LMlNA)Z(PzUpkA9#BVW\\+VTtOP^=SrlfUe67DzG%%IM",Z408_SOLUTION, "http://zodiackillerciphers.com/wiki/index.php?title=Solved_408-character_cipher", true),
		new Cipher("Z408 3rd section only","Nk)ScE/9%%ZfAP#BVpeXqWq_F#8c+@9A9B%OT5RUc+_dYq_^SqWVZeGYKE_TYA9%#Lt_H!FBX9zXADd\\7L!=q_ed##6e5PORXQF%GcZ@JTtq_8JI+rBPQW6VEXr9WI6qEHM)=UIk",Z408_SOLUTION, "http://zodiackillerciphers.com/wiki/index.php?title=Solved_408-character_cipher", true),
		new Cipher("Z408 last 18 only","6VEXr9WI6qEHM)=UIk",Z408_SOLUTION_18, "http://zodiackillerciphers.com/wiki/index.php?title=Solved_408-character_cipher", false),
		
		new Cipher("Z408 Homophone set [ENWZ6p+] replaced by single symbol [Z]", "9%P/Z/UB%kOR=ZX=BZVZeGYFZ9HP@K!qYeMJY^UIk7qTtZQYD5)S(/9#BPORAU%fRlqZk^LMZJdr\\ZFHVZe8Y@ZqGD9KI)ZqX85zS(RZtIYZlO8qGBTQS#BLd/P#B@XqZHMU^RRkcZKqZI)Zq!85LMr9#BPDRZj=Z\\Z(eZUHkFZcZOVZI5ZtL)l^RZHI9DR_TYr\\de/@XJQAP5M8RUt%L)ZVZKH=GrI!Jk598LMlZA)Z(PzUZkA9#BVZ\\ZVTtOP^=SrlfUeZ7DzG%%IMZk)ScZ/9%%ZfAP#BVZeXqZq_F#8cZ@9A9B%OT5RUcZ_dYq_^SqZVZeGYKZ_TYA9%#Lt_H!FBX9zXADd\\7L!=q_ed##Ze5PORXQF%GcZ@JTtq_8JIZrBPQZZVZXr9ZIZqZHM)=UIk", Z408_SOLUTION, true),
		new Cipher("Z408 Homophone set [HIL5] replaced by single symbol [H]", "9%P/Z/UB%kOR=pX=BWV+eGYF69HP@K!qYeMJY^UHk7qTtNQYDH)S(/9#BPORAU%fRlqEk^HMZJdr\\pFHVWe8Y@+qGD9KH)6qX8HzS(RNtHYElO8qGBTQS#BHd/P#B@XqEHMU^RRkcZKqpH)Wq!8HHMr9#BPDR+j=6\\N(eEUHkFZcpOVWHH+tH)l^R6HH9DR_TYr\\de/@XJQAPHM8RUt%H)NVEKH=GrH!JkH98HMlNA)Z(PzUpkA9#BVW\\+VTtOP^=SrlfUe67DzG%%HMNk)ScE/9%%ZfAP#BVpeXqWq_F#8c+@9A9B%OTHRUc+_dYq_^SqWVZeGYKE_TYA9%#Ht_H!FBX9zXADd\\7H!=q_ed##6eHPORXQF%GcZ@JTtq_8JH+rBPQW6VEXr9WH6qEHM)=UHk", Z408_SOLUTION, true),
		new Cipher("Z408 Homophone set [FK7@] replaced by single symbol [F]", "9%P/Z/UB%kOR=pX=BWV+eGYF69HPFF!qYeMJY^UIkFqTtNQYD5)S(/9#BPORAU%fRlqEk^LMZJdr\\pFHVWe8YF+qGD9FI)6qX85zS(RNtIYElO8qGBTQS#BLd/P#BFXqEHMU^RRkcZFqpI)Wq!85LMr9#BPDR+j=6\\N(eEUHkFZcpOVWI5+tL)l^R6HI9DR_TYr\\de/FXJQAP5M8RUt%L)NVEFH=GrI!Jk598LMlNA)Z(PzUpkA9#BVW\\+VTtOP^=SrlfUe6FDzG%%IMNk)ScE/9%%ZfAP#BVpeXqWq_F#8c+F9A9B%OT5RUc+_dYq_^SqWVZeGYFE_TYA9%#Lt_H!FBX9zXADd\\FL!=q_ed##6e5PORXQF%GcZFJTtq_8JI+rBPQW6VEXr9WI6qEHM)=UIk", Z408_SOLUTION, true),
		new Cipher("Z408 Homophone set [TXd!] replaced by single symbol [T]", "9%P/Z/UB%kOR=pT=BWV+eGYF69HP@KTqYeMJY^UIk7qTtNQYD5)S(/9#BPORAU%fRlqEk^LMZJTr\\pFHVWe8Y@+qGD9KI)6qT85zS(RNtIYElO8qGBTQS#BLT/P#B@TqEHMU^RRkcZKqpI)WqT85LMr9#BPDR+j=6\\N(eEUHkFZcpOVWI5+tL)l^R6HI9DR_TYr\\Te/@TJQAP5M8RUt%L)NVEKH=GrITJk598LMlNA)Z(PzUpkA9#BVW\\+VTtOP^=SrlfUe67DzG%%IMNk)ScE/9%%ZfAP#BVpeTqWq_F#8c+@9A9B%OT5RUc+_TYq_^SqWVZeGYKE_TYA9%#Lt_HTFBT9zTADT\\7LT=q_eT##6e5PORTQF%GcZ@JTtq_8JI+rBPQW6VETr9WI6qEHM)=UIk", Z408_SOLUTION, true),
		new Cipher("Z408 Homophone set [DO(^] replaced by single symbol [D]", "9%P/Z/UB%kDR=pX=BWV+eGYF69HP@K!qYeMJYDUIk7qTtNQYD5)SD/9#BPDRAU%fRlqEkDLMZJdr\\pFHVWe8Y@+qGD9KI)6qX85zSDRNtIYElD8qGBTQS#BLd/P#B@XqEHMUDRRkcZKqpI)Wq!85LMr9#BPDR+j=6\\NDeEUHkFZcpDVWI5+tL)lDR6HI9DR_TYr\\de/@XJQAP5M8RUt%L)NVEKH=GrI!Jk598LMlNA)ZDPzUpkA9#BVW\\+VTtDPD=SrlfUe67DzG%%IMNk)ScE/9%%ZfAP#BVpeXqWq_F#8c+@9A9B%DT5RUc+_dYq_DSqWVZeGYKE_TYA9%#Lt_H!FBX9zXADd\\7L!=q_ed##6e5PDRXQF%GcZ@JTtq_8JI+rBPQW6VEXr9WI6qEHM)=UIk", Z408_SOLUTION, true),
		new Cipher("Z408 Homophone set [PU9k] replaced by single symbol [P]", "P%P/Z/PB%POR=pX=BWV+eGYF6PHP@K!qYeMJY^PIP7qTtNQYD5)S(/P#BPORAP%fRlqEP^LMZJdr\\pFHVWe8Y@+qGDPKI)6qX85zS(RNtIYElO8qGBTQS#BLd/P#B@XqEHMP^RRPcZKqpI)Wq!85LMrP#BPDR+j=6\\N(eEPHPFZcpOVWI5+tL)l^R6HIPDR_TYr\\de/@XJQAP5M8RPt%L)NVEKH=GrI!JP5P8LMlNA)Z(PzPpPAP#BVW\\+VTtOP^=SrlfPe67DzG%%IMNP)ScE/P%%ZfAP#BVpeXqWq_F#8c+@PAPB%OT5RPc+_dYq_^SqWVZeGYKE_TYAP%#Lt_H!FBXPzXADd\\7L!=q_ed##6e5PORXQF%GcZ@JTtq_8JI+rBPQW6VEXrPWI6qEHM)=PIP", Z408_SOLUTION, true),
		new Cipher("Z408 Homophone set [GS8l] replaced by single symbol [G]", "9%P/Z/UB%kOR=pX=BWV+eGYF69HP@K!qYeMJY^UIk7qTtNQYD5)G(/9#BPORAU%fRGqEk^LMZJdr\\pFHVWeGY@+qGD9KI)6qXG5zG(RNtIYEGOGqGBTQG#BLd/P#B@XqEHMU^RRkcZKqpI)Wq!G5LMr9#BPDR+j=6\\N(eEUHkFZcpOVWI5+tL)G^R6HI9DR_TYr\\de/@XJQAP5MGRUt%L)NVEKH=GrI!Jk59GLMGNA)Z(PzUpkA9#BVW\\+VTtOP^=GrGfUe67DzG%%IMNk)GcE/9%%ZfAP#BVpeXqWq_F#Gc+@9A9B%OT5RUc+_dYq_^GqWVZeGYKE_TYA9%#Lt_H!FBX9zXADd\\7L!=q_ed##6e5PORXQF%GcZ@JTtq_GJI+rBPQW6VEXr9WI6qEHM)=UIk", Z408_SOLUTION, true),
		new Cipher("Z408 Homophone set [rt\\] replaced by single symbol [r]", "9%P/Z/UB%kOR=pX=BWV+eGYF69HP@K!qYeMJY^UIk7qTrNQYD5)S(/9#BPORAU%fRlqEk^LMZJdrrpFHVWe8Y@+qGD9KI)6qX85zS(RNrIYElO8qGBTQS#BLd/P#B@XqEHMU^RRkcZKqpI)Wq!85LMr9#BPDR+j=6rN(eEUHkFZcpOVWI5+rL)l^R6HI9DR_TYrrde/@XJQAP5M8RUr%L)NVEKH=GrI!Jk598LMlNA)Z(PzUpkA9#BVWr+VTrOP^=SrlfUe67DzG%%IMNk)ScE/9%%ZfAP#BVpeXqWq_F#8c+@9A9B%OT5RUc+_dYq_^SqWVZeGYKE_TYA9%#Lr_H!FBX9zXADdr7L!=q_ed##6e5PORXQF%GcZ@JTrq_8JI+rBPQW6VEXr9WI6qEHM)=UIk", Z408_SOLUTION, true),
		new Cipher("Z408 Homophone set [B#%] replaced by single symbol [B]", "9BP/Z/UBBkOR=pX=BWV+eGYF69HP@K!qYeMJY^UIk7qTtNQYD5)S(/9BBPORAUBfRlqEk^LMZJdr\\pFHVWe8Y@+qGD9KI)6qX85zS(RNtIYElO8qGBTQSBBLd/PBB@XqEHMU^RRkcZKqpI)Wq!85LMr9BBPDR+j=6\\N(eEUHkFZcpOVWI5+tL)l^R6HI9DR_TYr\\de/@XJQAP5M8RUtBL)NVEKH=GrI!Jk598LMlNA)Z(PzUpkA9BBVW\\+VTtOP^=SrlfUe67DzGBBIMNk)ScE/9BBZfAPBBVpeXqWq_FB8c+@9A9BBOT5RUc+_dYq_^SqWVZeGYKE_TYA9BBLt_H!FBX9zXADd\\7L!=q_edBB6e5PORXQFBGcZ@JTtq_8JI+rBPQW6VEXr9WI6qEHM)=UIk", Z408_SOLUTION, true),
		new Cipher("Z408 Homophone set [M)] replaced by single symbol [M]", "9%P/Z/UB%kOR=pX=BWV+eGYF69HP@K!qYeMJY^UIk7qTtNQYD5MS(/9#BPORAU%fRlqEk^LMZJdr\\pFHVWe8Y@+qGD9KIM6qX85zS(RNtIYElO8qGBTQS#BLd/P#B@XqEHMU^RRkcZKqpIMWq!85LMr9#BPDR+j=6\\N(eEUHkFZcpOVWI5+tLMl^R6HI9DR_TYr\\de/@XJQAP5M8RUt%LMNVEKH=GrI!Jk598LMlNAMZ(PzUpkA9#BVW\\+VTtOP^=SrlfUe67DzG%%IMNkMScE/9%%ZfAP#BVpeXqWq_F#8c+@9A9B%OT5RUc+_dYq_^SqWVZeGYKE_TYA9%#Lt_H!FBX9zXADd\\7L!=q_ed##6e5PORXQF%GcZ@JTtq_8JI+rBPQW6VEXr9WI6qEHMM=UIk", Z408_SOLUTION, true),
		new Cipher("Z408 Homophone set [JQ] replaced by single symbol [J]", "9%P/Z/UB%kOR=pX=BWV+eGYF69HP@K!qYeMJY^UIk7qTtNJYD5)S(/9#BPORAU%fRlqEk^LMZJdr\\pFHVWe8Y@+qGD9KI)6qX85zS(RNtIYElO8qGBTJS#BLd/P#B@XqEHMU^RRkcZKqpI)Wq!85LMr9#BPDR+j=6\\N(eEUHkFZcpOVWI5+tL)l^R6HI9DR_TYr\\de/@XJJAP5M8RUt%L)NVEKH=GrI!Jk598LMlNA)Z(PzUpkA9#BVW\\+VTtOP^=SrlfUe67DzG%%IMNk)ScE/9%%ZfAP#BVpeXqWq_F#8c+@9A9B%OT5RUc+_dYq_^SqWVZeGYKE_TYA9%#Lt_H!FBX9zXADd\\7L!=q_ed##6e5PORXJF%GcZ@JTtq_8JI+rBPJW6VEXr9WI6qEHM)=UIk", Z408_SOLUTION, true),
		new Cipher("Z408 Homophone set [fz] replaced by single symbol [f]", "9%P/Z/UB%kOR=pX=BWV+eGYF69HP@K!qYeMJY^UIk7qTtNQYD5)S(/9#BPORAU%fRlqEk^LMZJdr\\pFHVWe8Y@+qGD9KI)6qX85fS(RNtIYElO8qGBTQS#BLd/P#B@XqEHMU^RRkcZKqpI)Wq!85LMr9#BPDR+j=6\\N(eEUHkFZcpOVWI5+tL)l^R6HI9DR_TYr\\de/@XJQAP5M8RUt%L)NVEKH=GrI!Jk598LMlNA)Z(PfUpkA9#BVW\\+VTtOP^=SrlfUe67DfG%%IMNk)ScE/9%%ZfAP#BVpeXqWq_F#8c+@9A9B%OT5RUc+_dYq_^SqWVZeGYKE_TYA9%#Lt_H!FBX9fXADd\\7L!=q_ed##6e5PORXQF%GcZ@JTtq_8JI+rBPQW6VEXr9WI6qEHM)=UIk", Z408_SOLUTION, true),

		new Cipher("Z408 Reduced to simple substitution cipher", "PBP/N/PBBPDR=NT=BNVNeGYFNPHPFFTqYeMJYDPHPFqTtNJYDHMGD/PBBPDRAPBzRGqNPDHMNJTttNFHVNeGYFNqGDPFHMNqTGHzGDRNtHYNGDGqGBTJGBBHT/PBBFTqNHMPDRRPcNFqNHMNqTGHHMtPBBPDRNj=NtNDeNPHPFNcNDVNHHNtHMGDRNHHPDR_TYttTe/FTJJAPHMGRPtBHMNVNFH=GtHTJPHPGHMGNAMNDPzPNPAPBBVNtNVTtDPD=GtGzPeNFDzGBBHMNPMGcN/PBBNzAPBBVNeTqNq_FBGcNFPAPBBDTHRPcN_TYq_DGqNVNeGYFN_TYAPBBHt_HTFBTPzTADTtFHT=q_eTBBNeHPDRTJFBGcNFJTtq_GJHNtBPJNNVNTtPNHNqNHMM=PHP", Z408_SOLUTION, true),
		// TODO: re-encode this: new Cipher("Z408 With Rod's corrections","¼ºP/Z/uBºËOR¥ÐX¥BWV+ÅGyF°¼HP¹K‚ÑyÅMJy^uIË½ÑTÔNQyDµ£S¢/¼ºBPORAuºÆRÌÑEË^LMZJÄÒ\\ÐFHVWÅ¾y¹+ÑGD¼KI£°ÑX½µ¤S¢RNÔ‚yFÌO¾ÑGBTQSºBLÄ/PºB¹XÑEHMu^RRËÃZKÑÐI£WÑ‚½µLMÒ¼ºBPDR+Ê¥°\\N¢ÅEuHËFZÃÐOVWIµ+ÔL£Ì^R°HI¼DR¸TyÒ\\ÄÅ/¹XJQAPµM¾RuÔºL£NVEKH¥GÒI‚JËµ¼½LMÌHA£Z¢P¤uÐËA¼ºBVW\\+VTÔOP^¥SÒÌÆuÅ°¾D¤GººIMNË£SÃE/¼ººZÆAPºBVÐÅXÑWÑ¸Fº¾Ã+¹¼A¼BºOTµRuÃ+¸ÄyÑ¸^SÑWVZÅGyKE¸TyA¼ººLÔ¸H‚FBXA¤XADÄ\\½L‚¥Ñ¸ÅÄºº°ÅµPORXQFºGÃZ¹JTÔÑ¸¾JI+ÒBPQW°VEXÒ¼WI°ÑEHM£¥uIË",Z408_SOLUTION),
		/* alanbenjy320.example. ? replaced by ¥.  hints: "Its a Song Lyric.  There is a Repeated 9 letter word (not adjacent)."   http://www.zodiackillerfacts.com/forum/viewtopic.php?f=50&t=446 */
		/* glurk reposted here: http://www.zodiackillersite.com/viewtopic.php?p=42612#p42612 */
		new Cipher("Test cipher by alanbenjy (very difficult, unsolved)", "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdFeEfghijkWlmnLoYMpqcrstuvwxyAzE0A1G2e3456Jx7O89j!#NYH$h%&X(e)*a1+,i#,x1I-K(.e/2:;<M!f*i=>@$[vlst\\]>^_`qC{|}~0_~U���/�dO�s,��Q^w��b��}[�ui�no�KR4��R���{8���;T���%��Ru[T����}w�$����V3�����=Z�Ndn��4Px�NY�����snd��;�K�DY�B�VTo�}36�[6�j��EV�,�u�:�|WX5��r��x@�vW�uy郂g��}�A`W4O�xfVS�", null, "http://www.zodiackillersite.com/viewtopic.php?p=42612#p42612", true),
		// Adam's ciphers https://www.evernote.com/shard/s1/sh/5660695e-99e7-4380-8bbb-3696a580cde3/167d683037d09102f283063aba1677eb
		new Cipher("Test cipher by ALisowky", "(;+Hc^BE&pPN5FB*UcYW+T2b4FD<OK+RyT+I+JjR8I1C3#LBHlB*F/^2W.zGO7LBV4>KO<X+;y31(d)MFzW/M_Sj+lpfUC)l.DF+TIzOJ*zHVI6zP.2A+U*GZ>+(&1#cJkNpvB2VBFOORIw+Z>Bz68@Y9pTB2C+#lcq5M*RZMIp<b+EUckC+l.zFC5Ik#cG25dp_-^Rct+z7J8M2(#Gc^pMpFHOR-+D2K94bBI)L^5tLpdWpAFpYlOk+NRyKYBR9k4N.^(D+%dOZ<;*-K6<(t+z7S+GFcOL5.SV_Lqly%G:y)f2+T-(f+I-4XBKd5<9:)t+S8fN+>WI4MEPcUV/K",ALIK_SOLUTION, "http://www.zodiackillerfacts.com/forum/viewtopic.php?t=1230&f=50", true),
		new Cipher("Test cipher by ALisowky (Vigenere)", "bkmpowllgupyqgktjqcnxqpggmjtbyxskeklluorxzbukhumpxxhbrjmoigthvvrntpabrolllkmpentmaprtntjrxhmgugkhkgkkzbbstakgyhapnzplkntnzmvzmvqzvkvdrzalbbuezerallbbkqwtbxxaprukpovtghthtlvtikwjqcnxyigzapacububvchbtqrbrmguflvgohuqqugvbpuesmpzlsisklhvqoduwjnhdbbvkvxrxefackesxnxtkqpkbdwhrwhtfuepsrzhtmazbvvvgfocamkfqasrhngkksqsklsiikldqcrfhsrsxmzryaziajppkukl", ALIK_SOLUTION, "http://www.zodiackillerfacts.com/forum/viewtopic.php?t=1230&f=50", true),
		new Cipher("Test cipher by ALisowky (Vigenere plus homophonic)","B+LR^1.4pK()kp+J6q8IyM(_pYfOB)zV%;+*4G^DyWBS+XKYEzy2NFfLA:pO<c5DIH(dBD^*.4+ZRCIOLTEFHIJf>z2LpKp+<+_++WNBlOU+j)2TRIb(.+IJIWZcWL5kWc&P#>Wd.9BGCWCFT.*BB+k1ONzzU(DK+EAcOp2H2O*cH-+/6M8Iy):jWTRd7GBKBc729OMFN>ZpGt4P_^2SkMGpc9RG;lY(W*V-V+4<PM^#S16I2#BB5%czFzCtU8+;lzIyO+q(+N#/<F/2OtSCRlFbXJZUW95ccp3^7dL&3MUV><@p++lkV+.l--+*#M8Ft<VFlzYWF)Tb-dfRR+K+4",ALIK_SOLUTION, "http://www.zodiackillerfacts.com/forum/viewtopic.php?t=1230&f=50", true),
		new Cipher("Test cipher by Bryianzum (part 1)", "���V�ȼ^VI^X�+j+ԑ�^ȸ��ԅ��JX^�A�m�+XJ���+�^Ƒ��^/��IXyԅ+nJ�������M��˅�m+�Ѽ^+��7�ƒ�IXy�԰�ƔHX^��+A�+�mXJ%Լ���IXy��^�԰H�XVn+�m��7X�^���^���^˰��m7�+��V��mIXy�M�ԀXX/M�^������+��^��^�n+���ԑX�m�^ƒ�/�XA����%�y�ƣ�HH�^A�+^��+ImA�nnXA���A�X�+Ƃ^�n+����ԅ+nJV�V��^ƑX�7���m��XH�+IXy%�^���M����ԅ+nJJԂљ^y^m�+^�^+Ѹ", "youbeginbynotevertingyourselfonawasteoftimeandthinktoyourselfthatihavemisusedmineeachdayyoutreadupontheweedsofcrisisyouignoreproblemsascognizantasnietzschesabyssyouoverlookvinesastheyentangleyourtoesandaskhowthiscouldhappenwhentheyswallowyouwholedontletyourselfbeblindtoacatastropheyoucantsaveyourselffromanunseenenemy", "http://bryianzum.deviantart.com/art/The-Cipher-I-54939942", true), 
		new Cipher("Test cipher by Bryianzum (part 2)", "B1Ot+km^pB12^IB1rB2/Q^AXtB13pJ2^BOtHt+BrB2X^A1+^B1+tO2/^XrBB+qHBBpJQ4JNN+jHO7BrB2X^B1+tO2/^BrNAr\\/rt+M+4rB2X^k^A1~B\\XYJ2^fVYB2B/~4Ar3/AXtB1B1+,XmBXJ^XB2I^Xtk^IB1+m2I^/A1+^rf2NOqq~2mQ^7pM+t+f\\XYqQ/BBr|O~7B2p^r^f^rBt+/BY^B243pQt+mXNMO2BBX/~BkmJr,B2p^I2MOB1+fOBrkN/B1+rBBO^B2X^B1+3fOq~^fVO7rYm+A1+^3pQ~^r43zO\\XYQ^fOt/B~^f", "thereisnothingthatisunworthyofinterpretationwhenthereisnoattempttofulfllexpectationthereisntalwaysarevelationinwhatyoufindbutitsalwaysworththecostofnotignoringthesignswhenadilemmaisuncoveredyoumusttakeactionandnatrestuntilyouresolveittosatisfactiongivethedetailstheattentiontheydemandbecausewhenyouanalyzeyouunderstand", "http://bryianzum.deviantart.com/art/The-Cipher-II-54940286", true),
		new Cipher("Test cipher by ccactus (no E's, with encoding errors)", "K)IM3$Um8lnJ#X5-*-MTCgN(96AcPgna$HaRY6cWb0*dN4LCAORc4_RejkHFDE3#KpnAPIH!M(TUS7*50*mHaMbQd*RK4iXHVAL$AGRQXCSa#k-&LK02aiIW1TK7!QJpUmMbH9nH!T*_(OfRH@7lQbHBkSC4K67HSR-gQ3Rc561-ZN(RDd9A%X_VSUhJaRg7&XW4Mn5hRT_m*3D$IHgo!X54b*8T6cpYEHml%Cabk*I@0U9oA#$4G0QX$SW_9E3A(FUCjYnhHdKS0H4m6*CBn-IQMkg-A6(*nH!aLm4GX5JU01CPDZMb4AXH8k*n%d_Vb*F_)mX9#$B6*&%D$TF)","thisparagraphisunusualfogitsblatantomissionofthatmostcommonglyphthatbindsourconsonantsidonotthinkthatzodiacthoughtofthisbutoddphrasinganduncommonfordinvocattioncouldpossibuyfoologtrickcryptologistsasyoucanplainlydistinguishmynarrationiforgythatzodiacscgyptogrammaynotcontainavauidsolutionandthatzisprobablysittingonarockingchaighavingrlaugh", "http://zodiackiller.fr.yuku.com/forum/getrefs/id/33161/type/0", true), 
		/* gardi's 340-char cipher.  ? replaced by |.  solution is approximate (the best guess of zkdecrypto) */
		new Cipher("Test cipher by gardibolt", "3:aON;653<!%C>.\\7#8Z<L6O\\Y|U:AO#7Y=(UB8,^aP+7/a>S#IOY5W;AOGYQ2C_Q3OF[+7PGU]N74()1\\6'\\*@M153\\86O&HRIL;3]4X56NE+*M187!Y<N[7\"\\;YMK\\3\\]6<&HR#Y.+X(>Y#]N\"(N7O3_9O'#P(M12Y+Y^>Q77>KCa4K8$3/,=(UB/,$;I]S-CO0;ND+YEQX;-BQ5\"<;Y(50>+.^18%<SO<Y\"Y3\\<,7<6*]V,/YL]B8#CF[,7_\\M:Q7`+I1DZ:\"7KO1>P5]Y<`[3O8,F*W5'J5\\)`'VZ<X*G05.BP<!(T8*\\^-+XC18%OVU\\,MG:DF(NI/V.Oa6", "rosesareredvioletsaregreenalonestnwillandsmutisonscenehaneynufiguredoutmylastcipherpetetherearedtocgaractersoutthatdnesotmeantherearedtosnlutionsasmistergeepsmithfnundouttohischairinwilliniacanbiewasfunoutabluemeaniewouldhaveneenmnreentertapningalasidontgetoutmuchfromthehomeanemoreandthepeeepmpprettywellmedicatedbutihaveplentyofdisciplesr", "http://zodiackiller.fr.yuku.com/forum/getrefs/id/33983/type/0", true), 
		/* glurk's 340-char cipher , converted to ascii by me.  original link was: http://zodiackiller.21.forumer.com/a/cipher-explorer_post1719.html */
		new Cipher("Test cipher by glurk (1)", ">J&9*+%F8$WT13+!#4*J{;.RTX*5XC;E1&<9=5^<9*;=UC]HM[,6[M3=DE}.6K#3]CQDL]X-HK-_Q*8B524T-Z&<4(7)&MKADKV<K-@KMG4@#IW0:N+=L32PJ1.Z;@6G#8I;&=5^67*-4.;KT$9!ML46C;M3V!P+H-Z#7@93SK#PBP,W:*Q*1Y,}O%]L;,;X:@A#&<GZI6ABW4V3OVU17!DZJT7IQKCJH<%$G,P\\WVF<{*{=8^G#A*=AH{S3|5+'%O!MN':OQAV$UECFZOR5FCO(5J8&\"{Q4BGSNP_Z_RTW..NVACR<@UM{C+M*N4\\PMEWZ#8_!,<[]4,(WI;XS9","alternativelyandfarlessgloriouslythecipherscouldbethebacklashofaluckylowdowncriminalwithatestbookonhowtobeatfrequencyanalysisthefirstcipherwassolvedbyahusbandandwifeteamofamateurcryptanalystsoutoftheirhomeanannoyedkillercouldhavetakentherecipeforcodemakingandbegunconvolutingituntilitbecamemeaninglessenoughtobeunbreakableifindthelattersome", "http://zodiackiller.fr.yuku.com/sreply/32879/Cipher-Explorer", true), 
		/* glurk's 2nd 340-char cipher, ? replaced by |, original link: http://zodiackiller.21.forumer.com/a/cipher-explorer_post1719.html, converted to ascii by glurk */
		new Cipher("Test cipher by glurk (2)", "j_CN<!J4e7ilK0=B4ah0Vl8C7Yk7igO4f:D`^=^NeAbKl8F7AlX:3d00V_AahZQ2m5dQ<mH1JTmml7Wol1ChOehZQD6d@QI3`KF<8ZcB76@nWbF!TTZn=P=7aebYIgmZJFdYea3Y86HRfRDGKMV41VEIF2pJfKL6:H`hAiC0O7Zo7QAnD65_MQh<QBIaRT0FdO^6Na=:HnDf>I@k7Mc=4NGlmmW9YHLTiS!J:fFIPED0jfKDCIYciKRDfi;o39C09<Qd<`N^pZm|hUWKFe4IG@N2B3N2kUNG^g3<NMcXDi_L07Zo>hEgNWcIEPm^_04n2:f|hEE0hWJ|!e4pFdgT","Tis a night, almost Christmas, And all through that room A warm joy is stirring; No sign of a gloom. And �Ma,� sitting up, In gay gown, and cap, No, no! Will not start On a long wintry nap! For, out on that lawn A group of girls stand; A group singing carols With part of our Band. And that moon, in full vigor, Was lustrous; and lo! Our Lady is singing! Aha, now I know That Nancy and Kathlyn And Julius and Bill And also His Honor, Will sing with a will!  (from Gadsby [A Story of Over 50,000 Words Without Using the Letter E] by Ernest Vincent Wright)", "http://zodiackiller.fr.yuku.com/forum/getrefs/id/33072/type/0", true),
		new Cipher("Test cipher by (unknown) (do I already have this?)", "ABCDECFGHIJKLMNOPHCQRSTCUVWFXYFIZaPHCbcdDefCghiIPjkDEClCbmHncGoACpqrCsNJtuSCMvwQxCbLOCWyXz0U1ZB2eujk3YRlG4rJVS56Fa0n7dDWtyfgmpCCX2ihAIPoHsbqECZvNMQIDOKgdzCwukmBCl6pbhyUDYCCArLdKeyMqlCpRKZ2mi5cnIWgvDUHBqwCQ4tujIpFO6vDz89WckVDarDuCGFHSsoJyJPFzmfDz49ErDU1w0YICbgZH3s5bPU86GVd6Weck2bn460bhLulck0bQ1ls47DMpH7vt2ONJa6zd8BPCEors9bwACJezNSX!wRnDnVl", "iamcompelledtokillmoredmorepeopleallmyvictimswillbecomemyslavesimustmakethemostofmytimehearonearthbeforeenterengparadicethissummeriwillslaysomeskoolcidsiamthesameguywhocommittidthosemurdersinvalesscolastmonthblupigscannevercatchmepleasehelpasicannotcontrolmyselfanylongerigetveryangrywtheveryoneandcouldstrikeagainalmostanytimetakeentracare", "http://zodiackiller.fr.yuku.com/forum/getrefs/id/33073/type/0", true),
		new Cipher("Test cipher by King/Bahler","ABCDEFGHIJKLEMNOPQRSDETUVBCHAJWXIDEYHGCUXZAVD0XB1WX2EFGKZ3PM10SKO4PTW2V1FBKFR3NIL2P53YMHZQJ1LLOL2FS0RLWGZXI6DV6BE302GKWXTZJ557MPSHOFDRT0ILM8CJ51V44OSHRIKUTUML3PJUDOSWF2N354THZRE021U4XIWXMDZXJ0KSW3BPGH", "FOURSCOREANDSEVENYEARSAGOOURFATHERSBROUGHTFORTHONTHISCONTINENTANEWNATIONCONCEIVEDINLIBERTYANDDEDICATEDTOTHEPROPOSITIONTHATALLMENARECREATEDEQUALNOWWEAREENGAGEDINAGREATCIVILWARTESTINGWHETHERTHATNATIONOR", "http://www.oranchak.com/king-homophonic-ciphers.pdf", true),
		new Cipher("Test cipher by King/Bahler (encoded from glurk's transcription)","ABCDEFGHIJKLEMNOPQRSDETUVBCHAJWXIDEYHGCUXZAVDaXBbWXcEFGKZdPMbaSKOePTWcVbFBKFRdNILcPfdYMHZQJbLLOLcFSaRLWGZXIgDVgBEdacGKWXTZJffhMPSHOFDRTaILMiCJfbVeeOSHRIKUTUMLdPJUDOSWFcNdfeTHZREacbUeXIWXMDZXJaKSWdBPjH", "FOURSCOREANDSEVENYEARSAGOOURFATHERSBROUGHTFORTHONTHISCONTINENTANEWNATIONCONCEIVEDINLIBERTYANDDEDICATEDTOTHEPROPOSITIONTHATALLMENARECREATEDEQUALNOWWEAREENGAGEDINAGREATCIVILWARTESTINGWHETHERTHATNATIONOR", "http://zodiackillersite.com/viewtopic.php?p=38829#p38829", true),
		// here's jarlve's version: http://www.zodiackillersite.com/viewtopic.php?p=57087#p57087    1 2 3 4 5 6 7 8 9 10 11 12 5 13 14 15 16 17 18 19 4 5 20 21 22 2 3 8 1 10 23 24 9 4 5 25 8 7 3 21 24 26 1 22 4 27 24 2 28 23 24 29 5 6 7 11 26 30 16 13 28 27 19 11 15 31 16 20 23 29 22 28 6 2 11 6 18 30 14 9 12 29 16 32 30 25 13 8 26 17 10 28 12 12 15 12 29 6 19 27 18 12 23 7 26 24 9 33 4 22 33 2 5 30 27 29 7 11 23 24 20 26 10 32 32 34 13 16 19 8 15 6 4 18 20 27 9 12 13 35 3 10 32 28 22 31 31 15 19 8 18 9 11 21 20 21 13 12 30 16 10 21 4 15 19 23 6 29 14 30 32 31 20 8 26 18 5 27 29 28 21 31 24 9 23 24 13 4 26 24 10 27 11 19 23 30 2 16 7 8
		/* kiuku's cipher, with ? replaced by | */
		new Cipher("Test cipher by kiuku", "*QFE8{<IC~8E9FQG>+#H[!}!Iy4\\D*I/&\\|3N4I:E=B@EA-D$$I::XJMVXB#\\F$I\\H$Z\\D!XMB=:I%AIVJK+Q4^*7-5W2J2DCA=\"EBIKGYABM|]0JK;I.&AY%FNYA=:I&9KK\\D$VB5GE-6A7>ECI\\KU;E\\XW\"DB<[CL$T|,NZH&EBVI[N^G{I>39@|35CYI\"#0+%U=Z1.D:u1>W/6YWB;EB/E%C\\E4!0A8TFB~\\E-EC=24\\IEA0=_KUZIB7NM4QVI{,1.*C@|6>$I>2FPQVHB;9H$IG$YE%6E;!33D#GVI;6EMVI*Z4U7L1\"$I^DEB=./F[S~>38SS","considerthisinonehandajarofincrediblyfreshtastinggreenmountaingringoinanotheratrumphofachievementthestrpnottobecmparedtoanyotherdippingutensilthestripwasinventedtogobeyondsturdyandreliabletoreachawholenewlevelovtastesatisfactionthisisthefirstchipworthyofourdelectablegreenmountaingrngosalsaallnanuralsourcofwholegranstheendthelitt", "https://github.com/glurk/zkdecrypto/blob/master/zkdecrypto/cipher/330.kiuku.example.txt", true), 
		new Cipher("Test cipher by Michael Eaton", "kpP+tVH7Vz(;btYtWMJGLX.UtN%:JM/6Z3)Y!b5#TFB!_tVH#K%S8fCd7Et98>AR1QY2fQN1lLdEDW63X5Tp.GJlP)-Ctf1zdUOV_/4JZ(K;)Y+pl5TB/EXtKLESHV6!B!PptY%^3M(V&zk%RbF_H>CX93lW#LN+t<RLD:dCq.;bz1AH#GcU57f_DpR4HD-MZ/W6V!QN3_lGcZ;7)XtJM&EX%:U-<RdQYP+19S5C:tdGftJ7Mb;)NY(WE1Y3Z9YF8(./K&Tp)1S>Bdl+ttRttG;CK;#V)%:JZ(M88k;NY5W:FEZFCX(1q+PtUj#-V>XZJM^Ftt+3HMXFASXpK^+1","wethapeopleofatauniledstatesinordertoformamoraperfectunionastablistjusticeinsuredomeslictrinauilityprovideforthecommondafencepromoteategenepalwelfareandsecurethallessingsofliberlytoourselvesindourposterclydoordainandestillistthisconsailuaionfortteunitedstatesofamericamichaalaalonforpresidenttwottousandandeightattripaddingaaaheendabcdefghi", "https://github.com/glurk/zkdecrypto/blob/master/zkdecrypto/cipher/340.mikee.example.txt", true),
		// ? replaced by |       http://zodiackiller.21.forumer.com/viewtopic.php?p=33105&sid=70bf22ece1661156d33d9a6925216082#33105     http://www.zodiackillerfacts.com/forum/viewtopic.php?p=23320#p23320
		/* mikec's 340-char cipher. ? replaced by |. */
		new Cipher("Test cipher by mikec", "I4X91iZg;`GJ=b=7[2j2fKG8OPEUH6B:dXFXLfXmaJJ0QdWATXPkKZUHkl@ZHnCK4[TJA0Qnnd[gnOSK6CM\\7[U0VL32G415Y4Y9[7KT[J=|dM9;`O5=kM8bK4_CfDT=m5G5IkY4VDFcWZB`TEFcXb67fKTJ9IQ25iVJ0|diZQ]hDLQ;A^ToMkK9[]XQgHX4THmJ]D1a4_K8J;10V_3Ua45onZG=5d^IVgW5iAhBACdWL;LD]GZQAa42GcPY[URpH:;AF[72dWBD2|D765i8KAO^K1cECdM6JeH|nI4JPD1jLbO0V^KN^kWeU0XVB:gdK0VLDgR=|fK92jN7kYAU", "inordertofacilitatetheanalysisofhomophonicciphersolversivewrittenascriptthattakestextasinputandgeneratesacipherofagivenlengthusingagivennumberofsymbolsthescriptgencipherplsupportsseveraloptionsincludingencodingusingstraightintegersorthepopularprintableasciiformattheoutputsgeneratedbythescriptincludeplaintextversionoftheinputciphertextvers", "https://github.com/glurk/zkdecrypto/blob/master/zkdecrypto/cipher/340.mikec.example.txt", true), 
		new Cipher("Test cipher from Nin (transposition cipher)","TJBRQAHGHJUQZBZKHYDSCTQOWFUWBDLZZKKSPHXNECTMQZOALMRXWLBUTHZTXHDTTCKBLDIVSOSLBQORLBQGVCRPOSLSTVTAIZUMGZKNMHECSJMNXJXZEJWHQLJPOWWESFWFWXCOKJJAYCWLNDHHENKAXXUOJCVSPMJEJWSOVYOHAKCKACPNIJQEVRJEVCUYYVIOIFZLBWLFCRGHNNANZUSQPJCLGUWGZAHHTIWFLLODAGCMAXHSHJFRDIQFMNMQTQYXUHQUNADBKJOBDDDOZKOBFDIBMQGMWTQBJXYSEGZMPJHZXXDMYWFVOAEAMCLXGVQHEXSOECUBRHIDDTWJ", null, true),
		/* 2: RayN cipher */
		new Cipher("Test cipher by RayN", "RzG_YW8#7FZZ5j+4MyB6VGTSQA2%tq1K)LXWCJY8>_ZT8#RxA/z2KI31yb=M@l/cYk7zYZWT_GAj#N3B*2=EK1zSZQ4+5Mjb_kRVGQJY#3xZK+%MVCJTW89_)tcnB>pN9#yAW6VG@)SEq*2kRKX7y+=5_DRk+3VGl4)kLbjCQ1yYF#c3%VFZKTxA)yNM8J26VB1)I@SWE=>Rzk_YR+P5MZ#F7yDTjA/qM24X1V3)GC*YDbkW%y+ZRk+ZV6)G8QTJA7KBnzS@W4q2lWC>1WMR/zDYFc3ZTWf8W33)klAtLIP2FB_1zZS#6fPY+N=M@/z%yktE9l5V/Wq*ZRKCQ3_L8jb#=KTA+cWG6x2IBJ13>n)GY_jN#xZyGTRVFA","As grey traces of dawn tinge the eastern sky, the three travelers, men of Willow Dale, emerge from the forest shadow. Fording the River Dawn, they turn South, journeying into the dark and forbidding lands of the Necromancer. Even now the intensity of his dread power can be felt, weakening the body and Saddening the heart. Ultimately they will become empty, mindless spectres, Stripped of will and soul. Only their thirst for freedom gives them hunger for Vengeance... (The Necromancer lyrics, by Rush)","https://github.com/glurk/zkdecrypto/blob/master/zkdecrypto/cipher/378.ray_n.example.txt", true),
		// approximate solution due to some symbols representing more than one letter
		new Cipher("Test cipher by smithy", "D(_SrTTHW;E^DX+[Cn#)OQLDYymy$9ABfrj4r7#5GZyaST+NCFjMEPJW1%XV29YrD>AnzT-+QKC7#pNU];[\\DljPPCk%3)+_ZM<HTD8]f^BH%TEfmDLSPOXk$4Gn;N2Br]YDCD>AaQFE[(j7KrS#pNa%)M-_y3EHA]9L+DmZ#$MzCn#J%TUPTG2jQ#1EfD;[_5Yy]NFrA\\EV+)DrZ7M%SzC3f>TLW;<p[jX$HFrZGl)(mM+PC^jVrkz]>3fE4%9l25T81DOn_4EYEFPpQ^AH@LZa1$yWJUP8PrT;(GQ+B8%3V#2[mNTOf][DFEmnE)-iQ#CDm7M_Vy%N3rY\\El#S", "whenittartedwritingtonewspaperscliidingrespontibiriteformurdersiwasnothinkingaboutthweifficultiesthatwouldcautelawenforcedentbeciuswiwasonrethinkingaboutthepleasureiwasgetoingoutofteeingmelwtterspubrisheditwisntunoillaterthatireariseethatifididicoualledureertomwonedeserfandsavesomeproofofitthenicouldgetabtolutwreanethingiwantedpublisheegn", "http://www.zodiackillerfacts.com/forum/viewtopic.php?f=50&t=444", true), 
		new Cipher("Test cipher from Thing of the Day", "VeXExA4WAuO<X{xEAeV[EMAV3[A[Vx!3EMMA2VMAX{x3E[AX]ApxVWAUVX<2O3pAX2x]{p2AUO3[]UMAW]{xEAU]3[ExO3pAOeAO4A]!VWAME<xEXMAMX]^E3Aex]4A[EEuAO3MO[EAX2EA[x{4AnEVXMA]{XA]eAXO4EGAMe", null, "http://www.zodiackillerfacts.com/forum/viewtopic.php?f=11&t=1346", true),
		new Cipher("Test cipher by Tony Baloney (1)","jfHQCHbc[GS`DlnMz[HKgaPH:VIbA2bG@Tz[HReWQU>H5k6GzY8QCH0HRi[BechjH^LmH9nS;]aHl=3KZHRDMHIJA1<:E@f4U]Y8F2g0cNmSVaXdbT<BOWQI;J>5i^HHA46kjGzh[9RLCH@=nlKGQM`5W1H3]8ifH0d^RkJ:Q2HHjmDW`UJlL0H^g`@4i6XeBGI5=Q:[fL3HKN;]YG^bMd=Q17|Ie8VQTmQ]Hcb[a9hSJSzb1i>Q1N|CmQ:E3<2GHR5@[F9XRz:7dcVWdIUe84RBNd<RkD]0e8<RKE09NOQl^[O=;4MnSTd1W7fzHChm9|R3jHSU1naA_3gBQBV0", "iamcompelledtokillmore&morepeopleallmyvictimswillbecomemyslavesimustmakethemostofmytimehereonearthbeforeenteringparadicethissummeriwillslaysomeskoolcidsiamthesameguywhocommittedthosemurdersinvallejolastmonthblupigscannevercatchmepleasehelpasicannotcontrolmyselfanylongerigetveryangrywitheveryoneandcouldstrikeagainalmostanytimetakeextracare", "http://zodiackiller.fr.yuku.com/forum/getrefs/id/33105/type/0", true),
		// ? replaced by |
		new Cipher("Test cipher by Tony Baloney (2)",",CurN[|dISMT^yhvosdWSPFYwptc+gbfdBzOR`yA+0ZIG-VYa6+_C-UroLZD60Zfu4Jbsdq4{pikGFBJtTc:zS+:OS]mNjxlIJJWgu9vP[C,4Yy7UXr`hJsFclXfN@wtVRKTa8-NB_oDxZrZsUdliqXj4dIWn@JSdl9p,XT`kTU-UlFa[-zX@JhkYq+b@26gGRdfOyDpcA+Vi20ZBu,dE_oZbT^vjIGtT`C2ULZn*YW_PzufSU9rUo[sbZFTUWhw6BGRI_taD-qVrNisp+nmoWxFVjVtu9T`OE_,1n^oW[gYJrTy{CsHhaL]|cR_o-V+ZlDibTUdXjq-SgFZfzZl", "TDROVEYWASQUITESLOWLSSOASNOTEDRAWATTENTHEYCARAMANTELDAPOLICITYCARSBROWNSINEGROABOUTITSEITSFIVESHABBLDRESSEDTSATYPHONEBOOTHHAVESOMEFUNDAVALLISCOCOPWHENHESWALKEBSWHENTHUNGUPAPHONEATHEBEGANERESTDREWATTINTHEMESYCARTWILLCRUISEAROUNDSPICKWALLSTRASPEOPLEORCOUPLESTAREALONIANMOVEONEKILLSOMEMOREUNTILTOKILLEDABOUTIDOPENIFYTELLAMECHIERUPWHENASDOCATCH", "https://www.tapatalk.com/groups/zodiackillerfr/cipher-explorer-t1719-s30.html", true), // old URL: http://zodiackiller.fr.yuku.com/forum/getrefs/id/33817/type/0
		new Cipher("Test cipher by pi, no repeating ngrams","ePCC0Og1Z]T+M]LebB`oSk+PiTLj+V\\;P0*\\iT;h^17*eB3SEH^]+MekOK196IdJWQD_hFE8bI6Jjab]B+LZj1Z;Pk+pTfkm078+R`L=*:-LXOkg++2fI@;e+2V]IJ8QdKjc`Fp-EJ1^PH5@LJ^+OBVPTN6dmjT4A+jKefF*G1DH]A+:dmX4EgBLX=XPTK7I1]TXh0p6+9KZ]0E[01fFJog+36iC=_a+Oi120_Om*:S4U[^:o+G4:5oVfkgeFXQ;\\kmZJOiQLV=+X@idX7U[T240+7+Xf30O6BTW4K=54+PYea39KK;H+R+V`1:02f0C6IS]\\iF:^DbVhW:3o[NY", "I approached the witness stand with a warm and welcoming smile. This, of course, belied my true intent, which was to destroy the woman who sat there with her eyes fixed on me. Claire Welton had just identified my client as the man who had forced her out of her Mercedes at gunpoint on Christmas Eve last year. She said he was the one who then shoved her to the ground before taking off with the car, her purse and all the shopping bags she  (excerpt of The Gods of Guilt by Michael Connelly)", "http://zodiackillersite.com/viewtopic.php?p=5984#p5984", true),
		new Cipher("Test cipher by Unsub","HER>pl^VPpk|1LTG2dN+B(#OP%DWlHY.<*>dKf)yk(:NcO^GMUZ|WzJSRp78>K3Y.LB#T_D*^|+)<Uk%WVcy9(SfPK8OMtZpSL>PjVp5JNRP#Tf^.Bk#(NDpH|Z3*WOSVP17>^%29)_Htc.K+MFf&|ZJ5VRdyW1LBD2YNT*z%kyfK8).LTcMd%Z|7(#jNOJ9Hpl>:UYP^R.&NBDdykpj12zVL+PHWf1pt8(KOZ.TVP#N*LT>pFkf|7(^U)OcY%>PM4p#J42WZHPK+k|t(yL31WzOp^T&VPR%Ky_B>Sfp5|2kUHdLDY","O Jeany, dinna toss your head,  An' set your beauties a' abread!  Ye little ken what cursed speed  The blastie's makin:  Thae winks an' finger-ends, I dread,  Are notice takin.   O wad some Power the giftie gie us  To see oursels as ithers see us!  It wad frae mony a blunder free us,  An' foolish notion:  What airs in dress an' gait wad lea'e us,  An' ev'n devotion!  - That was Scotland's finest speaking to a ??? (from To A Louse by Robert Burns)", "https://github.com/glurk/zkdecrypto/blob/master/zkdecrypto/cipher/306.unsub.example.txt", true),
		new Cipher("Test cipher - Stage 3 Italian homophonic from Simon Singh's code challenge","IXDVMUFXLFEEFXSOQXYQVXSQTUIXWF*FMXYQVFJ*FXEFQUQXJFPTUFXMX*ISSFLQTUQXMXRPQEUMXUMTUIXYFSSFI*MXKFJF*FMXLQXTIEUVFXEQTEFXSOQXLQ*XVFWMTQTUQXTITXKIJ*FMUQXTQJMVX*QEYQVFQTHMXLFVQUVIXM*XEI*XLQ*XWITLIXEQTHGXJQTUQXSITEFLQVGUQX*GXKIEUVGXEQWQTHGXDGUFXTITXDIEUQXGXKFKQVXSIWQXAVPUFXWGXYQVXEQJPFVXKFVUPUQXQXSGTIESQTHGX*FXWFQFXSIWYGJTFXDQSFIXEFXGJPUFXSITXRPQEUGXIVGHFITXYFSSFI*CXC*XSCWWFTIXSOQXCXYQTCXYIESFCX*FXCKVQFXVFUQTPUFXQXKI*UCXTIEUVCXYIYYCXTQ*XWCUUFTIXLQFXVQWFXDCSQWWIXC*FXC*XDI**QXKI*IXEQWYVQXCSRPFEUCTLIXLC*X*CUIXWCTSFTIXUPUUQX*QXEUQ**QXJFCXLQX*C*UVIXYI*IXKQLQCX*CXTIUUQXQX*XTIEUVIXUCTUIXACEEIXSOQXTITXEPVJQCXDPIVXLQ*XWCVFTXEPI*IXSFTRPQXKI*UQXVCSSQEIXQXUCTUIXSCEEIX*IX*PWQXQVZXLFXEIUUIXLZX*ZX*PTZXYIFXSOQXTUVZUFXQVZKZWXTQX*Z*UIXYZEEIRPZTLIXTZYYZVKQXPTZXWITUZJTZXAVPTZXYQVX*ZXLFEUZTHZXQXYZVKQWFXZ*UZXUZTUIXRPZTUIXKQLPUZXTITXZKQZXZ*SPTZXTIFXSFXZ**QJVNWWIXQXUIEUIXUIVTIXFTXYFNTUIXSOQXLQX*NXTIKNXUQVVNXPTXUPVAIXTNSRPQXQXYQVSIEEQXLQ*X*QJTIXF*XYVFWIXSNTUIXUVQXKI*UQXF*XDQXJFVBVXSITXUPUUQX*BSRPQXBX*BXRPBVUBX*QKBVX*BXYIYYBXFTXEPEIXQX*BXYVIVBXFVQXFTXJFPXSIWB*UVPFXYFBSRPQFTDFTXSOQX*XWBVXDPXEIYVBXTIFXVFSOFPEIXX*BXYBVI*BXFTXSILFSQXQXQRPBUIV",null, "https://github.com/glurk/zkdecrypto/blob/master/zkdecrypto/cipher/Simon%20Singh/sscc3.homophonic.txt"),

		// in all the Beale ciphers, ? is replaced by ¥
		//TODO: encoding is messed up
		//new Cipher("Beale cipher #1", "!#$%&()*+,-./0123456789:;<.=>¥@ABC7DEFGHIJKLMNOPQRSTUV)WX;YZ[]^_`a-bcdefghijkl9mnopqFrIs3tuvwxycz@!{|}0U~]ĀāĂăĄuąĆ.ćSĈĉlĊċČr8čĎďĐđbĒ2r8ēĔĕĖėpZ:ĂĘęĚěĜĘĝĞğĠġĢ^ģĤĥyLĦħĨĩĪ.līĬĭĮįrăkİ@fı]Ĳĳ}ĴrĵIĶwķĸĹĺĻ¥ĪXļĽľ}ĿŀĩŁłŃńěŅņħŇň$c&!ŉŊŋŌō$ģĶ6ŎŏŐrőďŐŒœŔ:ŕ,|Ŗŗ[oŘřĆ]ŚśĥŜŝńylŞşŠ8ŎŖ,ĸšŉNŢţŎŤť8ŦŧŨũŪū:ŉŬŭ6Ů:ċyůĚ#ŰğtűŲĴĲfrĀkųŴŵŶŷŸoĺŹź$ŻżŽžƀƁƂƃŰ6Ƅ1ƅĚƆƇƈŉzĔĄLƉƊƋŖăZƌŖƍƆńŐĻƎƏ[ƐƑƒńƓ]0ƔģƑ2ĸŉƕƖ)ŜƗƘƙsƚĤƛƜRoƝĔźƞŉź!ƙƟƠơů~ƢyAŖĢŧƣƤƥ3ƦƔƧƨƩLū$Ƨƪš#~ƫƬƭƮrƯcưƱŖĲ.ƖĂWƲ0ĻHphŖ9]ŮƳ=ƴśD*)Ƶ&ƶ3ƷƸƹƺ/7ňĔƻ5Ř30đƄ0:ĸŞƼƽƾƿǀőńĔǁƯǂƖűǃǄǅǆǇǈQ2ŉƹǉǊęōŀĴm<!ǋƖǌįoōŻZ3ƺǍęǎ)ğǏ", null),
		//new Cipher("Beale cipher #2", "!#$%&()*+,-./0123456789:;<=>¥(@ABCDEFGHIJKLMNO/PIL+QRS=7TUV1WXY&Z[]%$^_`2aB%bGcdefZIghiR)jklm#¥PnB=ophJqrstuvJwWx^yhIaz{J|}2~oT:!pĀhāR¥ĂF9ă1Y~ĄJNąĆć;3ĈĉĊ&ċXČič/PĎďĈāĐWđĒē,Ĕv&ă1%gĕ_ĖWwĒė5ĘmĊBēę4Ěv/ěfdĊĜyĝIfČāĆ(Ğğ<Ġ_ĉ6kĕġ!V3¥xaĢ3g2#8ĕ(L5UĔĖx=`8ģ^)5Yċ7nĤCĥĈĦħĨc0ĩĕĪĉī1Ę:;g!~#mīGąxv/(ĬĖ(iĭ&Ā%&Į`K90P`Ģ,!j%)Lįgğ+,İd¥x9J%g_ăĊıĞ&YW12¥Ĝ&)2Ĳy/VĞĳa2xĴĵĶķĸ0hĹ¥ĺēNuĊĻ/ĞĖ.ĭ/9^;JĖĐĸYCļ+9Ġ1ĽĆĚľĿŀWNĖĞıēę9ySĤĹL&(Ł2ľĠĞ%/Ėğ+S90PJ@ă+jłĸĕľ3ěĞc%gďGYľv&(XĕľĞ_~4Ĝ;/Nă#iĈŃāİj%ĞBoĀĠańgĎ¥{ĠaRĉĵ_2gx1ĖP.ŅJS}ġN9/ZĳĊņĖ~/:Fāĸ%hmģ]ĖāĮĈTdĔĜ172Ňĕx%XOSgňđŉı41ģ:¥gĕĜĊĈnĠD9(UAjćĬģVĉĊ/$ė`%&İā,ĞŊKvēj;ğŁ6ŋt0N9_vJYċJrŌl_ĜīōģĈĕ[J%gTāi#Ğ%ĬŎ{ĜfŏTJGuīc;{_Ė&(ĭŐmīĠiF2¥$Ė0Ĳ#g%¥eāĞćĥL&Ġ.+9āJc10Ġ^<Ė/đWįĬTĞ6Ĉ%hTēUĭőŉŒJd4ĚvŌğĬċ)LWSķœ/L`2¥xgāŊģ1ŔĐĥĉ#¥ĝZĬļiO<ĳņO_i;Ĉ@ıīĚ%oKŕdĶAĉĊX:YĿĹ_Ģ@16ŖOi;ĭİcjŗgīĔxŐĕI_9x12~1Ř", "I have deposited in the county of Bedford, about four miles from Buford's, in an excavation or vault, six feet below the surface of the ground, the following articles, belonging jointly to the parties whose names are given in number three, herewith: The first deposit consisted of ten hundred and fourteen pounds of gold, and thirty-eight hundred and twelve pounds of silver, deposited Nov. eighteen nineteen. The second was made Dec. eighteen twenty-one, and consisted of nineteen hundred and seven pounds of gold, and twelve hundred and eighty-eight of silver; also jewels, obtained in St. Louis in exchange for silver to save transportation, and valued at thirteen thousand dollars. The above is securely packed in iron pots, with iron covers. The vault is roughly lined with stone, and the vessels rest on solid stone, and are covered with others. Paper number one describes the exact locality of the vault, so that no difficulty will be had in finding it."),
		//new Cipher("Beale cipher #3", "!#$%&()*+,-./0123456789:;<=>¥@ABCDEFGHIJKLM,NOPQGRST(UVI!+WXYZK[]^_`¥abcde`fRg+Rh4ieD-Nj:Vklam(no0,pqrK(sWht,E+uv<w.6vx48=y9Fz{G#B|J}~SĀ¥āĂăUhĄXNąĆćĈĉĊvċČčtĎJ:ď¥ĈjĐđ8Ēēr,Č=ĔUĕiĖPvėĖĘęjĚĀěhĜĊĝe¥RUĞğĕĠġĢA¥ģĤĥĦ,S+ħ*ĨĀLĩĪ@īwĬ%:>ĞĭĮįİıĲb@ĳvd-4ĴĵpVĶEķĸĹ+Tĺ-ĻļĽ]Ģľ8@#Ŀŀ~Łă<kĄy.MENSUłZOķđA($~<Ńđ,ĉERńŅL&Vņ^ŇňŉŊŋŌě;Rs(4ō.ŎŏFŐċőŒ#œmWdĢ@ŔŕŖŗŘqřŚśŜŝŞUş0<@=şkgŠv,šŢFXţZSŤJťČXŦŧŨŤħĀĿUeG¥(őĶ¥Ć,>KC3ĝĐčVũťj#ļYŪ[ūŬŭtŌďUgAĚGć@ZvĔŮF%(,ŀkJwůŰűqŲųŊŅŴŵŶiUŷŸ5qFykĉ@ĹXkŹţōŰźōtŻ9żĔSČŅ%$ŇFhůşŽ(,ĞžƀƁƂƃƄƅęƆăƇ,ƈĈF3ĳJjĺ;ĶĐLđ)ħčBƉġĢĖƊƋĊōJ`ƌƍƎƏƐƑĢƒU$őżXĳTĐz9&ŮƓmţťƔęƕbţƓqƕƖƗŝşƘĎƙƚƛűƜ@+SċĺĸFDƝ&<XğţƞŢƓũ&DŁdƟRDƜ=Ĝż,şĈĤ`[ƠơƢƣƤƔƥĚĳUTƦġůĆŤu8ĭƧĿqŌƨcƩƪƫƎƬƭ", null),
		new Cipher("BTK code", "GBSOAP7-TNLTRDEITBSFAV14", "VAGIAN - LET BEATTIE KNOW", false),
		new Cipher("BTK word search puzzle", "MOASDDOPJCRUISEXFGPJKWPTHGORLEOOAGRFOZFLTIJYOWNALPLMURLFNOVSNIIXCTFIVBSTQIAMCLDDLWOSRTTBLSTPINIPUILYHELMQEUFXNSTEAMBXIDT    DX SEI PARF ULARRYEG QEN   CHQWPDSDFIJAEHEZXCFKGNORTSBFLSRNSCHOOLFTEOPLASDHYCN    LLUODFGHJKKIATIHCIWX   MNBVXRUSETYUIXEIEOPFASDANREMODELLSVFGRHJOTUISKSLPSORCSTASLTRAEEELOEPSNMRZEXHEQCADCVBNTWENDIEKAFER AGNORWXNAMYDNAH", "", false),
		//new Cipher("Copiale Cipher", CiphersCopiale.copiale, "solved"), 
		//new Cipher("Copiale Cipher (newlines removed)", CiphersCopiale.copialeNoNewlines, "solved"),
		new Cipher("D'Agapeyeff cipher", "75628 28591 62916 48164 91748 58464 74748 28483 81638 18174 74826 26475 83828 49175 74658 37575 75936 36565 81638 17585 75756 46282 92857 46382 75748 38165 81848 56485 64858 56382 72628 36281 81728 16463 75828 16483 63828 58163 63630 47481 91918 46385 84656 48565 62946 26285 91859 17491 72756 46575 71658 36264 74818 28462 82649 18193 65626 48484 91838 57491 81657 27483 83858 28364 62726 26562 83759 27263 82827 27283 82858 47582 81837 28462 82837 58164 75748 58162 92000", null, false),
		new Cipher("Donna Lass poster cipher", "ePTWYPNWA[1WkSNZf22Q32ZZWkW[[",null,"http://zodiackiller.fr.yuku.com/topic/7153/Donna-Lass-Reward-Poster", true),
		new Cipher("Dorabella cipher", "BPECAHTCKYFRQDRIRRHPPRDXYXGFSTRTHTCKLCERREHGQTRFRHUSQDXKKXFSESHUSEDUWGSERHUQSDCPGSHCDXC", null),
		//http://web.archive.org/web/20041029025822/http://rec-puzzles.org/new/sol.pl/cryptology/Feynman
		new Cipher("Feynman cipher 1 (solved)", "MEOTAIHSIBRTEWDGLGKNLANEAINOEEPEYSTNPEUOOEHRONLTIROSDHEOTNPHGAAETOHSZOTTENTKEPADLYPHEODOWCFORRRNLCUEEEEOPGMRLHNNDFTOENEALKEHHEATTHNMESCNSHIRAETDAHLHEMTETRFSWEDOEOENEGFHETAEDGHRLNNGOAAEOCMTURRSLTDIDOREHNHEHNAYVTIERHEENECTRNVIOUOEHOTRNWSAYIFSNSHOEMRTRREUAUUHOHOOHCDCHTEEISEVRLSKLIHIIAPCHRHSIHPSNWTOIISISHHNWEMTIEYAFELNRENLEERYIPHBEROTEVPHNTYATIERTIHEEAWTWVHTASETHHSDNGEIEAYNHHHNNHTW", null, "http://web.archive.org/web/20041029025822/http://rec-puzzles.org/new/sol.pl/cryptology/Feynman", true),
		new Cipher("Feynman cipher 2 (unsolved)", "XUKEXWSLZJUAXUNKIGWFSOZRAWURORKXAOSLHROBXBTKCMUWDVPTFBLMKEFVWMUXTVTWUIDDJVZKBRMCWOIWYDXMLUFPVSHAGSVWUFWORCWUIDUJCNVTTBERTUNOJUZHVTWKORSVRZSVVFSQXOCMUWPYTRLGBMCYPOJCLRIYTVFCCMUWUFPOXCNMCIWMSKPXEDLYIQKDJWIWCJUMVRCJUMVRKXWURKPSEEIWZVXULEIOETOOFWKBIUXPXUGOWLFPWUSCH", null, "http://web.archive.org/web/20041029025822/http://rec-puzzles.org/new/sol.pl/cryptology/Feynman", true),
		new Cipher("Feynman cipher 3 (unsolved)", "WURVFXGJYTHEIZXSQXOBGSVRUDOOJXATBKTARVIXPYTMYABMVUFXPXKUJVPLSDVTGNGOSIGLWURPKFCVGELLRNNGLPYTFVTPXAJOSCWRODORWNWSICLFKEMOTGJYCRRAOJVNTODVMNSQIVICRBICRUDCSKXYPDMDROJUZICRVFWXIFPXIVVIEPYTDOIAVRBOOXWRAKPSZXTZKVROSWCRCFVEESOLWKTOBXAUXVB", null, "http://web.archive.org/web/20041029025822/http://rec-puzzles.org/new/sol.pl/cryptology/Feynman", true),
		// ? replaced by |
		new Cipher("Kryptos part 1 (solved)", "EMUFPHZLRFAXYUSDJKZLDKRNSHGNFIVJYQTQUXQBQVYUVLLTREVJYQTMKYRDMFD", "solved"),
		new Cipher("Kryptos part 2 (solved)", "VFPJUDEEHZWETZYVGWHKKQETGFQJNCEGGWHKK|DQMCPFQZDQMMIAGPFXHQRLGTIMVMZJANQLVKQEDAGDVFRPJUNGEUNAQZGZLECGYUXUEENJTBJLBQCRTBJDFHRRYIZETKZEMVDUFKSJHKFWHKUWQLSZFTIHHDDDUVH|DWKBFUFPWNTDFIYCUQZEREEVLDKFEZMOQQJLTTUGSYQPFEUNLAVIDXFLGGTEZ|FKZBSFDQVGOGIPUFXHHDRKFFHQNTGPUAECNUVPDJMQCLQUMUNEDFQELZZVRRGKFFVOEEXBDMVPNFQXEZLGREDNQFMPNZGLFLPMRJQYALMGNUVPDXVKPDQUMEBEDMHDAFMJGZNUPLGESWJLLAETG", "solved"),
		new Cipher("Kryptos part 3 (solved)", "ENDYAHROHNLSRHEOCPTEOIBIDYSHNAIACHTNREYULDSLLSLLNOHSNOSMRWXMNETPRNGATIHNRARPESLNNELEBLPIIACAEWMTWNDITEENRAHCTENEUDRETNHAEOETFOLSEDTIWENHAEIOYTEYQHEENCTAYCREIFTBRSPAMHHEWENATAMATEGYEERLBTEEFOASFIOTUETUAEOTOARMAEERTNRTIBSEDDNIAAHTTMSTEWPIEROAGRIEWFEBAECTDDHILCEIHSITEGOEAOSDDRYDLORITRKLMLEHAGTDHARDPNEOHMGFMFEUHEECDMRIPFEIMEHNLSSTTRTVDOHW", "solved"),
		new Cipher("Kryptos part 4 (unsolved)", "|OBKRUOXOGHULBSOLIFBBWFLRVQQPRNGKSSOTWTQSJQSSEKZZWATJKLUDIAWINFBNYPVTTMZFPKWGDKZXTJCDIGKUHUAUEKCAR", null),
		new Cipher("Michael D. Brown code", "GHUOORYHIIEEIYHIIXAHOJHOHAOOPHUHTHONHIYHIEEIHYHITHIIJHYHI", null),
		new Cipher("Ricky McCormick code", "MNDMKNEARSENSTAKNAREALSMTFRNENPINSENPBSERCBBNSENPRSEINCPRSENMRSEBPREHLDCNLDNCBETFXLFTCXLNCBEALPRPPITXLYPPIYNCBEMEKSEINCDRCBRNSEPRSEWLDRCBRNSENTSBNENTXSECRSLECITRSEWLDNCBEALWLPNCBETSMELRSERLSEURGLSNEASNWLDNCBENOPFSENLSRENCBENTEGDDMNSENCURERCBRNETENETFRNENCBRTSENCBEINCFLRSEPRSEDNDE71NCBECDNSEPRSEDNSDE74NCBEPRTSEPRSEONREDE75NCBETFNBCMSPSOLEMRDELUSETOTEWLDNINLDNCBE194WLDSNCBETRFXLALPNTEGLSESEERTEVLSEMTSECTSEWSEFRTSEPNRTRSEONPRSEWLDNCBENWLDXLRCMSPNEWLDSTSMEXLDULMT6TUNSENCBEXLMUNSARSTENMLNARSESAE6NSESENMBSENMNRCBRNSEPTE2PTEWSRCBRCSE26MLSE74SPRKSE29KENOBOLE175RTRSE35GLECLGSEOUNUTREDKRSEPSESHLE651MTCSEHTLSENCUTCTRSNMRE9984B2UNEPLSENCRSEAOLTSENSKSENRSENSREONSEPUTSEWLDNCBE3XARLANMSENRSEIN2NTRLERCBRNSENTSRCRBNELSPNSENGSPSEMKSERBSENCBEAUXLRHMCRENMRENCBE12MUNDDLSESWM4MILXDRLX", null),
		// see also: https://web.archive.org/web/20110101032114/http://www.bokler.com/eapoe.html
		new Cipher("W. B. Tyler / Poe cipher 1", "TAKFQMOTHQSTOCNHTATSCTKONXTOTFNJOKGTAKCWGHEHTVVGEECGOTNITEOKNKCNMPTAKOHGKOFFGAOTEGIZGWGTAKCQOIFTOJNAWMHVJNEAKNWTVGECGTHQKGCYCNOCNOHGTZKQBQMAKFFGAOAONHQMFICNFGKHMJJNAKQMZKHQACQENJGTAKHGWIMOKCTJTFTTAKYSTHWIMHTKKGJECGTAKAOQHSIMOEFHMW", "The soul, secure in her existence, smiles At the drawn dagger, and defies its point, The stars shall fade away, the sun himself Grow dim with age, and nature sink in years; But thou shalt flourish in immortal youth, Unhurt amidst the war of elements, The wrecks of matter, and the crush of worlds.", "http://cryptocrap.blogspot.jp/2011/11/edgar-allen-poe-cipher.html", true),
		new Cipher("W. B. Tyler / Poe cipher 2", "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwKxyz0T123456789!#$%&()*cp+,F-h./:;<&=>@[P\\]^f_Y`{xlHK|IOG}~S�RyZUWy����xmbAt�6e[��Nrde3$25kn�]87&E��9z`(#�u-a�3�5X=|3H[lo�pQ;�W.��FDO�vAXg{�$n><C�1j��0wI\\QjJgG|B8-_kh�@n^oPMU�+�_�9HAVd}svw�`�#r�NuW&CQ)Ff�Em���9*`xlbKjIl*q�pu�p�[(��`+_�s�~�O,yz�I�=�/�Pn@��|V$�aEd�rjZ3T8D�oJ�RK�f15e{�hB��Sc#m%�|��XN�y0A_-H\\!fV��t|+�#_*NMJHu6��S@}{;�sn�WbFD���|O��mG:�YR>qlUI�a���pmz%�j+V��1Z0k��*>xhMP*43$�2�rw8^@7,9H�e�(o#S<YdbA[P�lB=g�qpp�x�-.J|]fzt~EUVWX�&caNx��y��c1K*[�Qk3�,5�8^�h��9$v)yM�-#�OjH�6{<�sno�p7YV>�d�B,5(LSDrP%\\WbGga�3��q}_)Y�=B�d#[�I0Ej@J��wN���O;$h|z:)R1Z8K=��X��oec6UH-�~�,�mv��^0�~�Wr�*xy�>{B%I54��+��Vr`|�OD}b*�", "It was early spring, warm and sultry glowed the afternoon. The very breezes seemed to share the delicious langour of universal nature, are laden the various and mingled perfumes of the rose and the -essaerne, the woodbine and its wildflower. They slowly wafted their fragrant offering to the open window where sat the lovers. The ardent sun shoot fell upon her blushing face and its gentle beauty was more like the ryeatia(creation?) of si-e yifd romance or the fairy inspiration of a dream than the actual reality of earth. Tenderly her lover gazed upon her as her clusterous(clustering?) ringlets were edged(?) by amorous and sportive zephyrs, and when he perceived(?) the rude intrusion of the sunlight he sprang to draw the curtain but she softly stayed him. 'No, no, dear Charles,' she softly said, 'much rather you'ld(would?) I have a little sun than no air at all.'", "http://cryptocrap.blogspot.jp/2011/11/edgar-allen-poe-cipher.html", true),
		//new Cipher("Tyler/Poe solved cipher 1 (See: http://www.bokler.com/eapoe.html)", ",Üß:á][,|á),[°∂|,Ü,)°,ß[∂¶,:∂![ß(,Üß°®(|Ö|,©©(ÖÖ°([,∂*.Ö[ß°∂ß°∂]ø,Üß[|(ß[::(Ü[.Ö(*;(®(,Üß°á[*:,]!∂Ü®]|©!∂ÖÜß∂®,©(Ü°(,|áß(°<°∂[|(,;ßá>á]Üßß:(Ü[Ü[∂|á]:*°∂:(ß|]!∂Üßá];ß|áÜ°áÖ∂!(,Üß|(®*][ß°`°,:,,Üß<),|®*]|,ßß(!Ö°(,ÜßÜ[á!)*][Ö:|]®", null),
		new Cipher("Taman Shud code", "WRGOABABDWTBIMPANETPMLIABOAIAQCITTMTSAMSTGAB", null, "https://en.wikipedia.org/wiki/Taman_Shud_Case", true),
		new Cipher("Voynich manuscript", CiphersVoynich.voynich, null),
		new Cipher("Voynich manuscript (whitespace removed)", CiphersVoynich.voynichNoWhitespace, null),
		new Cipher("Zodiac 'copycat' cipher as found on Ed Neil's website", "ABCADEFGHBIJCKEIJELBMNODPJIMAPGQCAGKREBJIMACAGKLBSSBMNCAGKSBCAEMBMNAKDGMPCBTADBCBCMGAGMADETUGMVKINEBMIOEELBOBSSCLBMWSBAASELBHCIMHRILEICQBATUGRADECLBM", "this is the zodiac speacking why can't you stop me i can't stop killing stop listening t phonys if this is not on the front page in a week i will skin 3 little kids and make a suit from the skin", "https://web.archive.org/web/20110211153440/http://www.thezodiacfiles.com/1971cipher.html", true),
		// ? replaced by |
		new Cipher("Z408: Zodiac killer's solved 408 cipher, false positive high-scoring merge [KTWz]","9%P/Z/UB%kOR=pX=BKV+eGYF69HP@K!qYeMJY^UIk7qKtNQYD5)S(/9#BPORAU%fRlqEk^LMZJdr\\pFHVKe8Y@+qGD9KI)6qX85KS(RNtIYElO8qGBKQS#BLd/P#B@XqEHMU^RRkcZKqpI)Kq!85LMr9#BPDR+j=6\\N(eEUHkFZcpOVKI5+tL)l^R6HI9DR_KYr\\de/@XJQAP5M8RUt%L)NVEKH=GrI!Jk598LMlNA)Z(PKUpkA9#BVK\\+VKtOP^=SrlfUe67DKG%%IMNk)ScE/9%%ZfAP#BVpeXqKq_F#8c+@9A9B%OK5RUc+_dYq_^SqKVZeGYKE_KYA9%#Lt_H!FBX9KXADd\\7L!=q_ed##6e5PORXQF%GcZ@JKtq_8JI+rBPQK6VEXr9KI6qEHM)=UIk",Z408_SOLUTION),
		new Cipher("Z408: Zodiac killer's solved 408 cipher, false positive high-scoring merge [Tj]","9%P/Z/UB%kOR=pX=BWV+eGYF69HP@K!qYeMJY^UIk7qTtNQYD5)S(/9#BPORAU%fRlqEk^LMZJdr\\pFHVWe8Y@+qGD9KI)6qX85zS(RNtIYElO8qGBTQS#BLd/P#B@XqEHMU^RRkcZKqpI)Wq!85LMr9#BPDR+T=6\\N(eEUHkFZcpOVWI5+tL)l^R6HI9DR_TYr\\de/@XJQAP5M8RUt%L)NVEKH=GrI!Jk598LMlNA)Z(PzUpkA9#BVW\\+VTtOP^=SrlfUe67DzG%%IMNk)ScE/9%%ZfAP#BVpeXqWq_F#8c+@9A9B%OT5RUc+_dYq_^SqWVZeGYKE_TYA9%#Lt_H!FBX9zXADd\\7L!=q_ed##6e5PORXQF%GcZ@JTtq_8JI+rBPQW6VEXr9WI6qEHM)=UIk",Z408_SOLUTION),
		new Cipher("Z408: Zodiac killer's solved 408 cipher, false positive high-scoring merge [%fjq]","9%P/Z/UB%kOR=pX=BWV+eGYF69HP@K!%YeMJY^UIk7%TtNQYD5)S(/9#BPORAU%%Rl%Ek^LMZJdr\\pFHVWe8Y@+%GD9KI)6%X85zS(RNtIYElO8%GBTQS#BLd/P#B@X%EHMU^RRkcZK%pI)W%!85LMr9#BPDR+%=6\\N(eEUHkFZcpOVWI5+tL)l^R6HI9DR_TYr\\de/@XJQAP5M8RUt%L)NVEKH=GrI!Jk598LMlNA)Z(PzUpkA9#BVW\\+VTtOP^=Srl%Ue67DzG%%IMNk)ScE/9%%Z%AP#BVpeX%W%_F#8c+@9A9B%OT5RUc+_dY%_^S%WVZeGYKE_TYA9%#Lt_H!FBX9zXADd\\7L!=%_ed##6e5PORXQF%GcZ@JTt%_8JI+rBPQW6VEXr9WI6%EHM)=UIk",Z408_SOLUTION),
		new Cipher("Celebrity Cypher", "zRO1+ dI^2Y3lp IZY+FBI>c+ O<YL>4F, B5 V6 W73 Y89M M!@# Md z$Mp Iq", null, "http://www.zodiackillersite.com/viewtopic.php?t=394&p=1280", true),
		new Cipher("Thomas Dougherty's rearrangement of the 340", "HERVP|LTGNBODWYKBMUZGWLHJSRORKM|FP&RFOCFDKUXGRL|GJONYLMZRFBAKURJOFBURE|DYBTMKORJ|TM&BFSN|FBRGFNVBXCERUZ|BKOMGRTLCFWB|LWCWPOSHT|FWBYOBCMDHNSZOA|K>pl^k12dp+(#%.<*f)y:c+()#zpp7^l8*3p++2_9+ztjd5+4k/p8^l-*dk>2(#5+q%;2c.z(2fj#+_z+@9d<+b+2cy64-zl+^+p7<y-+/5tpb2<cl*54.+z69y#+5c(;8l^f524b.c4t++y1*:49>5-+c.3z(p^.fq2c+16<+l)++)zc/()pkd<7t_*-c>pkz8;+", null, "http://www.zodiackillerciphers.com/?p=183"),
		new Cipher("FBI Ricin Bomber Cipher", "ACMQXOhT[oNi\\jPSBkR*tEeFI+vxlo]BXYsUE0CkZoO|ATG3J\\4iKStMjbNpvx|2QXBr*R\\T/USfI5|4Gc\\/ziKSICq0hvTp|[A+Nx\\ymSEkRQtoPF|XL\\jAkYeKGZbO*tM/JyuEiSUvp|x0qo[\\TxSl|AkZhFOEtLsoi\\ACXBjS-UQ+|AEvIjgGUT3/YoV4NiABk\\Ijz*JvSUEtyn|\\0TKSqCD+QLRoibABv|Tp\\F*whCYuQixSZ4GXNEJvRokyq|BW\\/rSjt*6KTip|UCeQXs*FJMIqF\\[vGkTxSm/ivI24CfOJ=0G+A/jNHZItG-gc/RT1Ei|UoXLq\\vTKSA|4i\\5BOIjEvSGz-", "Ricin Put caster [sic] beans in a clouth [sic] bag and wrap in paper towles [sic] then crush them in a vice, to remove some of the oil. Put the crushed beans in a blender and chop up in cold water, then place the bean pulp and water in a refriuerator [sic] for two days., Strane [sic] or filter and keep the liquid. Saturate the liquid with epson [sic] salts and leave overnight. The ricin will collect on the bottom, simply pour or sipon [sic] off most water and let the rest evaporate off", "http://www.zodiackillerciphers.com/?p=328", true),
		new Cipher("MK-Zodiac (Ricardo) code", "MZtp9F_XYrG@KPROqW^HJ!\\I)U8+j=6tkqND5Ar9LES(ZF@l_8VTYH_dY\\XAO=ptK!^G%=MPBT8d=)_Wj=%SUDMXA_!YrleIkc95P+F\\6B8LNHT_dYtVE%UZQ@_XYrpK8G_F)SB%e!(_kKITJ5M\\W+=ltL8A)9eM8r6H)NPOI\\dfYe5UX^LMEV!z_GDfH)ZeT(eBYFkdOIMp9^5tXzYelP!DqY@He%WSrB_KIl5+_TY\\L)68UFHMNVdf_qY@IEj=%8k(_XYt5)ZK98POq!rpzWLGUBV+f6Fe\\k=H9cNI)EeT^e%Y@PdDqYK5tZ8LSHp_XYrIMWFU@k(z9QJ+\\6O5A!tfKe)Ne/QTrR\\lqq8LPeGBS^z8=E%BUDRZtrd\\FqL/p@YtWHXk(e%Yf+_!YrO8q6q/", "here is your assignment for this experiment write an essay about your own personal philosophy explain how your activities relate to your beliefs your essay shall consist of three parts which are the introduction the body and the conclusion the introduction must clearly state your thesis the body must explain your thesis in more detail be descriptive the conclusion must restate your thesis in different words check for grammatical and spelling errors make sure to include your name - mk", "http://mk-zodiac.com/", true),
		new Cipher("Scheme: move 1 row down, 2 columns right and repeat (wrap around cipher): 340_1rd-2cr-w.txt", "H+M8|CV@Kz/JNbVM)+kN^D(+4(5J+JYM(+y.LWBOLKJp+l2_cFK29^4OFT-+EB+*5k.Ldl5||.UqL+dpVW)+kp+fZ+B.;+B31c_8TfBpzOUNyBO<Sf9pl/C>R(UVFFz9<Ut*5cZGR)WkPYLR/8KjROp+8lXz6PYAG)y7t-cYAyUcy5C^W(cM>#Z3P>L(MVE5FV52cW<Sk.#K_Rq#2pb&RG1BCOO|2N:^j*Xz6-+l#2E.B)|DpOGp+2|G++|TB4-|TC7z|<z29^%OF7TBzF*K<SBKdpclddG+4-RR+4>f|pFHl%WO&D#2b^D4ct+c+ztZ1*HSMF;+B<MF6N:(+H*;", null, "http://www.zodiackillersite.com/viewtopic.php?p=38834#p38834"),
		//new Cipher("Grid 19 by 18, direction North-East (vertical) and 2 \"?\" symbols added: 340_19by18_n-e.txt", ">)B.E2#l+K@VC|8M+HMp|f>4+RR-L.k5*+BED|)MVbNJ/z9zFFVU(RHFLqU.||5ldL>P3Z#>Nk+GZc5*tU<|2+pGOppd+25VF5EVM(D&OW%lkW)R-4BT|++G(4+(D^S<Wc+tc4D^b2#k+)WVz7CT|+(MYJ+J5/RLYPZtz+c+;.B+Zf+pK#.kOBWL.y8+pORjK82z<|8_c13BR&bp2#qR_H*1AYP6zXlBT7FO%^9JKL|OOCB1GFM<B+;FMSfTKBS<K*FzKFc_2l+p)G;*H+(:N6OByNUOzpB2+-TFO4^92yAYc-t7yd?C/lp9fS<-6zX*j^:N?c(W^C5ycU4+Gddlcp", null),
		new Cipher("Grid 17 by 19, 17 symbols filler at end, vertically untransposed: 340_323_17.txt (smokie treats)", "HER>pl^VPEk|1LTG|LGE2dNT+B((E#O%pD(WY^..<*<Kf)y^R|pO:cMUO+ZkdppTYz+.DJSDE1p7Y82d3cSENk_9JtjWR^L#.^d1EZ5jWE#WSEEpFG#B(|9^9|EN:1y5jd38E397U&+(KdfEf&RWEpFl4NT:PDE%cEGT/E3E<8p7<1>P-(JDE>CT+9Gq2H;(z:1Dyt(GMq3;+Pf*5_J.4FGkX<1U&F%@R./EG7R.SqGlJJS|DEk1;3.P<.2P7@G1GD<JY)CGJb)AWEH2ZC48B3&6C1*5#J.FB)A5-2X<bB7@%*DkKlD3kZZSE#Y3Cdl59W3ORjH)DN@<cJ>6pP/E", null, "http://www.zodiackillersite.com/viewtopic.php?p=38834#p38834"),
		new Cipher("Grid 17 by 20, 16 symbols filler at end, vertically untransposed: 340_324_16.txt (smokie treats)", "HER>pl^VPkkR|1LTG2dN+BB(#(O%DW+Y.Tk<*Kf1V)ypTT^N:VB1cMGRUTZNz>pJ*MRlGS7c83dY+2|B+pUR)y3dR|dMRRT_E|Pk.797.Rl<UW93pJzRJ7Z+tVkOp%R%tYdRT_j5f^<FGRL*RE^&RJR(zlZ(U4F/kcGR4-^V7ET>HCk:<UGW8kEKqJCqF%#9ScB5_Ey;(UftVLXYB&REZYBMqEjcc_.GRyUCJBF(B>FZXEMEG(cND-Ec@DbdRH>U-5zPJtA-U#9|cB_P)b9/>;(@PZXL#GyOjDJy))MR|NJ-pj97dJGY4Tj+6FRy.U2^E.1RY3HDGlX(*c4ATF&R", null, "http://www.zodiackillersite.com/viewtopic.php?p=38834#p38834"),
		new Cipher("Grid 17 by 20, 15 symbols filler at end, vertically untransposed: 340_325_15.txt (smokie treats)", "HER>ppl^lVPk|>1LTG2dN+B(#O%TTDRW(pY.<*KfT)Ry:%cd<KMBUZUzJ21>GSp>%fK#*72KS2<KKT8ES3YLZOJLKM.f|_J%cyKcZ)_Z(YV%PKP912KT8tj>9.5*KFdKED&KcKly+Dlf45/YU*K4-D(ZEM)HCYW.f*|zYENqcCT:P^_7Upj8EO;lf+9q5X1p&KE)1p<qEtUU(F*KOfCcp5lp:5)XE8L*lURk-EU@kb2KH:<Ejy3c9A-f^_SUp83f-_/:;l@3)XF^*OVt#bO##<KSRc-%t_Z2ckc4Tt>65KOLfGDELB*1:%MD(3YYKSBFT*1KKJHk*MXldU4AT5&K", null, "http://www.zodiackillersite.com/viewtopic.php?p=38834#p38834"),
		new Cipher("Grid 17 by 20, 14 symbols filler at end, vertically untransposed: 340_326_14.txt (smokie treats)", "HER>pl^VPk||1RL^TG2dN+B|(R#Ok%dD+WY.D<*K2f)GyT)kB+Vl<:+y2D++|cEyMYU:NZ2+W.BzJKk%#+%:(PKUYSk7+78f2+|c3_J:^9N+td+E1j+%+5#)8.BF9&Y<N+F41^:Ep15/YL.BNz*YE>-%/W(HCJZ<T_cEPq5Bp8|O7fTj+E(fTD-E3<<-9;+PB/%T95TO9(;E^tN5<RX4E<@Xb2+HOcUN#M%8A4BCJy<TcMDE_&Oq5@M(;tCNPS3B4JVVD+yR%4k3J:2%VbP|3)69+PUBG1EUlX%FkW1^MYY+ylt|NfNfOTT5C5S7Xz)fU|K++)HXNW;5d<FA|9j+", null, "http://www.zodiackillersite.com/viewtopic.php?p=38834#p38834"),
		new Cipher("Grid 17 by 20, 13 symbols filler at end, vertically untransposed: 340_327_13.txt (smokie treats)", "HER>pl^VRPk|1LTpG2dLN+Bd(#2O%#|lpDWYT.OdTpp^<EO*WKf).fpGYly:B|1Pp1fVNcdpM|UpUZ(dp^<zJSBKWNp7LpE83p1p_P:f9tjt5W.NpjF89fE#ZYlW&YlNy+WE>41/-8_/:c.%J<ESC_l-ZGVHq%3pEV(%T4Ez..^kU(Sl/1%t_%ktV;E4t;p.RXFE.@XbdpHk97N_*1ZAFlq:O.%<*<KNPkC_@*V;7qNSMzTEJ5DTpOR1F|z:fd1lF:Dz#6tpSKl28EK)DbS^G89*WWpO)7^N(X1j|%_q_MUXy#(K^BN(k%)9DS|^^8R&9%Hpp#-XNG;_L.jA^t3p", null, "http://www.zodiackillersite.com/viewtopic.php?p=38834#p38834"),
		new Cipher("Evens only: 340evens.txt", "E>lVk1T2N+(ODY<K)yc+ZW)#HSp^8Vp+R29+td5P&kpRFO*CF2(5K%2cG.L(2f#+Nz@9<++RFcA4-lV^+p<B-+/t|YpTK2cR|54.&F6S#N5B(8lF^54.Vt+B1:9EVZ-|.zKO^fq2c+1C+lB)+)CWPST(pFd<t_O*C>DNkzOAK+", null, "http://www.zodiackillersite.com/viewtopic.php?p=38834#p38834"),
		new Cipher("Odds only: 340odds.txt", "HRp^P|LGdpB#%W.*fB:MUG(LzJp7l*3O+K_Mzj|F+4/8^l-dk>D#+q;UXVz|GJjO_Y+LdMbZ2By6KzU+JO7FyUR5EDBbMO<lJ*TM+Bz9y+|Fc;RGNf2bc4+yX*4C>U5+c3B(p.MGRTL6<FW|L+WzcOH/)|kW7BYB-cMHpSZ8|;", null, "http://www.zodiackillersite.com/viewtopic.php?p=38834#p38834"),
		new Cipher("Randomized, shuffled: 340shuffled.txt (doranchak)", "N;+2.lczX)k;KRV6+p<T%CzcERB4-U.%LVN>Ff+|S)Rc(7ppzlk|.*^z)EOZVW*O^_K+5&fBLXK95fBVBz+(@Bp_56GH4HbH-ZWYjT+K|t#WpdT|+JJly|4p*F4SU>l(c)2GB^OtY.*|lOpMp>Ry(T|<BLB/+FZ)82B9+/2MMt*.^V.OdR_A^WZcJ5P8++B2FcBdU<5^d9K#-+k+1>E|J+#9+tDFG#5+lN3cD84T-W<YF+ONAR5P2G*zLFOOHLU<637yf+|RYjpPC+-FVNyBC#&dl8ycGqSO:24cFMDkM+(7SOM2K(+|/Fp<CbU2zD1c+R+1(CqzWMkbK;:p+GLz", null, "http://www.zodiackillersite.com/viewtopic.php?p=38834#p38834", true),
		
		new Cipher("Jarlve alanbenjy-like cipher", "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz012345em67894!#N$%&()*+,-./:;<&=j>@[\\]^_`{|:}~���A.��֚�J�_������a����7�Z0����/�F�����������i��I��UR��������EO�dTShM�L��B�Wg�Z*�xk���LJNDQ+|1p�crSyf�En�q=\\�G�_�eC�:20#K{XHVl9C��,�8�v^i�$%�c)��/�4<u>�!�P��Yf��ms���(�o5;@�v�Aj[t�3`�,��F|uw�&�6bn]�{.z~,�~��d�B3-W�R��{�X&��~*+}���:�7�", null, "http://www.zodiackillersite.com/viewtopic.php?p=42645#p42645", true),  
		
		new Cipher("Jarlve perfect cycle c_p1.txt", "4vMP]9[RK,st6^>6y;Y0ITAq@b\\_?q$UmLCfA(4NM?Wr1wHmc/OlsP[Sv,(o\"bREtZU!_cV8]f><*^q\\%;I7A?0WTs4qNC@U$?/+l(ow1rmqZcMW7K>HTySV$9[vR?rU!\\O,stobB]qW^N8;U>?/VC<_Ky4(t0j6@*wcL!M\\[q]B^sY;N/01VOl(o@\\N,ct`$A<*rIP?>fHub/8Zo_1SVCw%!q\\67<N$f4/M?VOT\\\"8]s[E,^bu_vRY;*0%r1(4c6l<Z+ML@7sETKyNCw[OlB!9,Sv]+\"bRKY^I>W;UpqyZB0?_u4Sv($/tMB@`rmWpc7Uw%!LTAq]`>m\"[RKV*p", "i like killing...", "http://www.zodiackillersite.com/viewtopic.php?p=38834#p38834", true),
		new Cipher("Jarlve perfect cycle c_p2.txt", "3Gkvt\\V+<8YWmgO2.&oUwpn!,)easJ=%:>i@L<MvE\"lB^mq1fZ%g6\"FS$&kU;!*-G#I]85'WtNVw3.io,pYZn)aDsm>+@;LM%f2=J5$GE1Bv#WlkVU\"Si,^JDtF8.^a]o\\Zn:+G='WM*Oi3mY$&1p@5>k)NBvE2%L#q-IUptlf8Zo)FGV]SWn.*Oi'3MZ,&=\\$<E1vklaY>G\"+sIL@fFWt]m!o%'BJ^2n=!1E(*\"3kgVYIU,JD*>Nit:LZopasDmf@qFIB^Jw]#8eV\\,'-G.*3M5%YSNa\"+>q$@LP#-6nfB<l^8.e=EVFm],gl'W1:iI)v%kEM<aJ\"qt23@UYol>", "There was nothing so very remarkable in that; nor did Alice think it so very much out of the way to hear the Rabbit say to itself \"Oh dear! Oh dear! I shall be too late!\" (when she thought it over afterwards it occurred to her that she ought to have wondered at this, but at the time it all seemed quite natural); but, when the Rabbit actually took a watch out of its waistcoat-pocket, and looked at it, and then hurried on, Alice started t (from Alice's Adventures in Wonderland, by Lewis Carroll)", "http://www.zodiackillersite.com/viewtopic.php?p=38834#p38834", true),
		new Cipher("Jarlve perfect cycle c_p3.txt", "6d#]8*=!W`1K0grn_YLXeOIp4=7#*Hs)v\\(WT2H/.?xuZi6d\\[8b1$cYTPv%K^!_X`/2sOS90-]r)*[ZiQ8e4WFxu17?lHdG_!$^KnNFXRL5I)ve`ZO:lzcs4#P7S9YA8/?.i^G21d6<*\\T$]uNP=KXQOWFrx_5[6g(vl!)4`7YAZ/^G\\zR<E28*PSLWTc1N:?(uI-Q_59!)ixzAQgH`eb62dAYsGFS\\vTx$Q=KuZ/6<-(iHXI<OnNAs4\\85[7%?Lp=z:$SR9RLNe!1K(E`_*P5=XWl0Lz2)O=ZSN#Y^]c4Fu<gpHdQ7I(?r5As$T!zHn8vx9L/Kl#6<=i.*`sb2", "Siddhartha, the Buddha, remembered all his past lives; this is why he was given the title of buddha which means �the Enlightened One.� From him the knowledge of achieving this passed to Greece and shows up in the teachings of Pythagoras, who kept much of this occult, mystical gnosis secret; his pupil Empedocles, however, broke off from the Pythagorean Brotherhood and went public. Empedocles told his friends privately t (from Valis by Philip K. Dick)", "http://www.zodiackillersite.com/viewtopic.php?p=38834#p38834", true),
		new Cipher("Jarlve randomization of cycles r1_p1.txt", "fTQH&Hb08h4zo12o-PDx@.k\\,s%;ZW<cI@YikRjwf\\+p\"&!IRJtm=HQyub4*lhU^z.cMsRwY&i2K:1Z'DPqLkWx+.=;\\%t&+<\\wvm=*,\"pIZL4jc.-2!mT0J<HfT8Wp+M'YQRz*bG&\\c1'tP+2Z'%YKh-ys=zx?o,:M4@&;w;W1GPR[xwJ,\"JtL=zM'%j4*C<kK:pqHZ2i!#fwY.zQ:uJY&D1\\'om\"%<ibwhZwtLJlYMRs^;Pj#fUT[xK,Dp:4Q=o.Km^bqML4v.08't&htmG&Hs-y1^l;uU[P@2cx+CWTLG,\\j#fT0R<%*QGMepIcC4.+&D1qmIZPe2klb8-w\"C", "i like killing...", "http://www.zodiackillersite.com/viewtopic.php?p=38834#p38834", true),
		new Cipher("Jarlve randomization of cycles r2_p2.txt", "XY(#3dC&%fGSl>up*'i#47<2!bn\"Dm0+WvV:c%KbFZPIra9es$Z>Ol_.@'(#42,JYf1j*6/S35\"UsKViC7)$<b!gD+X&\"4G@avp(r6KYF0\"#fSPe:7Z.$Imrg(c*KrC_35ViW&$<vYf,uSjZ/K'0b!6se#d\"7Fpl)@991b#ePXfV(7)$:GpY3f,uSvc*VI'i5K%F<70FC_jS+.D1/Cs)Se/a2iZX!mr&(32iPR,lG<W\"v1b:rg,cdV0>_$e#IDDljCJ/1!mrU/@Kn\"5\"c9Y@,sf6a)&5:+pXJ*IGzK9n(vC>Pm@KO3F!ca_\"WPjS0>V17bZ3Pf%:rlJi./I#s<F)", "There was nothing so very remarkable in that; nor did Alice think it so very much out of the way to hear the Rabbit say to itself \"Oh dear! Oh dear! I shall be too late!\" (when she thought it over afterwards it occurred to her that she ought to have wondered at this, but at the time it all seemed quite natural); but, when the Rabbit actually took a watch out of its waistcoat-pocket, and looked at it, and then hurried on, Alice started t (from Alice's Adventures in Wonderland, by Lewis Carroll)", "http://www.zodiackillersite.com/viewtopic.php?p=38834#p38834", true),
		new Cipher("Jarlve randomization of cycles r3_p3.txt", "-Lw$De;aG)5#=JAw\"@S<ojs.,K#$@(lTLgN@tY(O/RgaiP-zt0Fpu[0Gg^y/#C)D,Yna(<qb=W$A5e05PvTo<@`g)\"j[8lP4TY,`RwXC<];*oiLoaF,I`XZ(#w^[*bG7uO#/z84)Dy-k@tg#$)U4SR<7jeCA-5*0tJNn8Y\",ajev\"P`^tX]kp)5G4*KGgZuXIRN)sW7Tq]YiO-U7vJlao&t)L7e(4C*gP-t[v;#YiOgkWkyl<sN<wX7(j-Fq0j/,S=K*IRUb]bKXoau[Np)D@^*K#G8=;Xa5<S\"qq$eCAZR`)NJ.(Lvjok,A*7(RtYqlwTzgbKn[`w-NKy/GY(&a", "Siddhartha, the Buddha, remembered all his past lives; this is why he was given the title of buddha which means �the Enlightened One.� From him the knowledge of achieving this passed to Greece and shows up in the teachings of Pythagoras, who kept much of this occult, mystical gnosis secret; his pupil Empedocles, however, broke off from the Pythagorean Brotherhood and went public. Empedocles told his friends privately t (from Valis by Philip K. Dick)", "http://www.zodiackillersite.com/viewtopic.php?p=38834#p38834", true),
		new Cipher("Jarlve perfect cycles + 1:1 substitutes c_s4_p1.txt", "2!*gi5-$.&\"#RQ6jd4y7`VX\\:zsmk@,WX0>?X\"29*1WD=NqX\"EMt\"g-3B&\"#FzlH#<Wum\"Sfi?6I=Q\\Je4`VXk7Wt\"2@s>:W,19_<\"#NIDX\\V\"*Wt!6q<$.E,5-d3kDWuSM&\"##zKi@WQJf4W61s9>=mBl2\"#7ZR:IN\"0u*E-\\iOQ\"y4SJ7=sMV\"#:9E&\"#',XI=D`gk6?q[zSft#mI!J>Neu@sj<=9,?2E*1SMVJFfi\"-H&Qz[m$.y4I7eD=\"2\"RtI<_*0:V\"Htd3s>N-M<Ku5&Bli_Fz!$yQ`6W4WP\\.VO7km[2d3\",9#*K:'DXWP\"tWNeu0<X@i'6XF-BlE=P", "i like killing...", "http://www.zodiackillersite.com/viewtopic.php?p=38834#p38834", true),
		new Cipher("Jarlve perfect cycles + 1:1 substitutes c_s4_p2.txt", "Jl'<GB1sODU?(gq2t^i<,<5y@<p[VbwI34vM]/;<h`h&bSTE>\\(O0IL+.^#<NyrPCRr=DZJl'8u,Ut?G1<4vi<@QV`]c[N>;SLs5bm.\\hwM<RChE&<(2lubbQ#=Dtb1J'B?Gg+viU\\;rqC4I].^5<@Z>w<8[<hc`LRTPr<<Eh=Dl#<J?MUsv'trq\\4];C&^GB.3hi<5hu>LlS2Vr=1JU?w4(yEI]@bb+#'yGh)r`>i/[Lr<MbQr=8v5OJ\\w<&VQSUuT4r1bb,]RDp@B[>PCtrL;m(=c8MIsJT.&UoRP0E4ughbDtp#h1]`>@3hLl'/?r<<SGh;O[b(Ti2=M<J5hU", "There was nothing so very remarkable in that; nor did Alice think it so very much out of the way to hear the Rabbit say to itself \"Oh dear! Oh dear! I shall be too late!\" (when she thought it over afterwards it occurred to her that she ought to have wondered at this, but at the time it all seemed quite natural); but, when the Rabbit actually took a watch out of its waistcoat-pocket, and looked at it, and then hurried on, Alice started t (from Alice's Adventures in Wonderland, by Lewis Carroll)", "http://www.zodiackillersite.com/viewtopic.php?p=38834#p38834", true),
		new Cipher("Jarlve perfect cycles + 1:1 substitutes c_s4_p3.txt", "dIlD0;Si2<8pJ'[$cj.#PTP*yYElN9FWK73;>?9L:=&/xoOQd+0e8BG27AIHp`Zc#RKiFTUXJqD[Wj+xL,0PyN`><8E=`9omc?B`p$U`#vbUPWQP/xTw`UGFylAEUX;,0I=:K`mZ8L&z2OdBDRUASp#,Tj`[7cU+>'3o`iWy<EN,xQ`m&Uvzg?0;AU.2OG8Uw=3/Pq,cUXZWIdU,,'9RPe7iK,jFm`U>L&OB,Yp<xodzq3Q9#PzT$U,Fy70U+EH=b*SUwBUvXv.UP?8p3g/cNAUY#;`JbUZWTSxUUl2`DGy`Rz'*9I,EP3=[U,FB>iU9$0K&X.Lp`lOzYo:j<Fe?", "Siddhartha, the Buddha, remembered all his past lives; this is why he was given the title of buddha which means �the Enlightened One.� From him the knowledge of achieving this passed to Greece and shows up in the teachings of Pythagoras, who kept much of this occult, mystical gnosis secret; his pupil Empedocles, however, broke off from the Pythagorean Brotherhood and went public. Empedocles told his friends privately t (from Valis by Philip K. Dick)", "http://www.zodiackillersite.com/viewtopic.php?p=38834#p38834", true),
		new Cipher("Jarlve randomization of cycles + 1:1 substitutes r1_s4_p1.txt", "?'u0mF*W<+CYPq3tfq8A/2e@ARjB>cIae\"[:ehkS?@Jz-yGelj[2`0uUf*7Y]+'(Y2aKRC4[w:D)9q>js;/2ecmJ2hB@S[qa3>4N2lYA-Iec2`kJ2,zG2W<jDF?fU@3ayj[u`YY*nKcJwS[;JI>4j[)+',R7Ym.Pq9AC/yBjkcKZwh8yS4m-j[2hY;S4?lY&ze))D\"0@3:Gbuj[2Y*9WS[msq@4t2-jI:+SR>4[2j][A`B(ky?bu<,8K)wsz97*7t2-2N+/;2C(2fUS[mR[2nq0B',AN]kW<8y/zaKJ!>f2Zwc?buU'hD4Y*n;&3ea!l2Jmsq\"2e@A&Ie]+,Wj)!", "i like killing...", "http://www.zodiackillersite.com/viewtopic.php?p=38834#p38834", true),
		new Cipher("Jarlve randomization of cycles + 1:1 substitutes r2_s4_p2.txt", "I&9`3'O7:GqPy:ErGVU;a`Y.x`[)$]=Deu*QCeG-kWkQ]yjv1*D:+W<_G@YFTsm2iGmLGw4&J'QT<GP9\\>I*3;Qllyq7?TuGDCrv]RG*kUO`Gik=x-D_&)]]6v1GG]?1YXPJe7PJL*Gmoi4W<GV9F\\RI3>'Q;k_WqGj2m`-UkuG&=FCP?Cr*vGmE&LuGiO@UXGekY>Jkxq1&y_$mL)4<*9IWs3yq\\]]rU=.vkNmyuY:QCm;?]6mL'PJ:1i9-Ol$Wu\\jLm?]]T<GG[x')42PGm<GwDI7X\\W_qjGQutG2+3C?ek]GG+Jk?4yCO:kC*Ueim`-D=kG:x]Wjvr1xFLYku", "There was nothing so very remarkable in that; nor did Alice think it so very much out of the way to hear the Rabbit say to itself \"Oh dear! Oh dear! I shall be too late!\" (when she thought it over afterwards it occurred to her that she ought to have wondered at this, but at the time it all seemed quite natural); but, when the Rabbit actually took a watch out of its waistcoat-pocket, and looked at it, and then hurried on, Alice started t (from Alice's Adventures in Wonderland, by Lewis Carroll)", "http://www.zodiackillersite.com/viewtopic.php?p=38834#p38834", true),
		new Cipher("Jarlve randomization of cycles + 1:1 substitutes r3_s4_p3.txt", "lo!ZA)]B<BPs*gzfbk$&hdhLDvG!<8`_?lV4l[SiFtlu=?lylCA-=&wkl>o:HTuP6riN8s5ML\"ZzbkC_?0Ahd)Ilu=DG@`?>PBtTs!eI&p],hPyh[bH^@,wS6f>GxM)0_ysFoTEuAilV)lldfu5>$&H0H)I!l=Owl\"Vo@uPDrt<0b?T>leMVjN_4E,$4lCAx^GV[hg0POpB=yl500\"8Bh-lNo0k`>IOlills0vt[P?lVgVy`6hV&Ze0SHlb,w6:s]*],^dxpMM]5hu_dV-rA)Ee$D)IL$ON=Gv_e,z<@fCDT[V\"L8o0thV&!O08dlBx`!Pilp$?HIZlV]yF4[S-u", "Siddhartha, the Buddha, remembered all his past lives; this is why he was given the title of buddha which means �the Enlightened One.� From him the knowledge of achieving this passed to Greece and shows up in the teachings of Pythagoras, who kept much of this occult, mystical gnosis secret; his pupil Empedocles, however, broke off from the Pythagorean Brotherhood and went public. Empedocles told his friends privately t (from Valis by Philip K. Dick)", "http://www.zodiackillersite.com/viewtopic.php?p=38834#p38834", true),
		new Cipher("Jarlve columnar transposition after encoding: Perfect cycles + 1 high count 1:1 substitute: c_s1_ct_p1.txt", "8v\"dc%<O9Su^sOl)/-[yUbFf'D>6QZ7pB?/05KNv/J_;c>`AFP.%8ue=st4l)q:3f//UNY7#?x-aZP^[b6i/D=K/5'FVD<c/A_t;pfQ.qv96t`d'0JK\"/u=%SUfYFesNl)/^:<xZKDdfw5iYA;[NQ?pb'9/F#\"OZby\\_v^%uc//pZu[;e65_B/.^ArY:/h#;>PD8`T<xJis-=NtYU.Z[a?9'5OqAbPQ%Yivp3ZAc5N/F;6U_cB4).a^?h`u/ls#i=Nb/v\"=4Vtd6;yO/t5Bq[Sl3VpwU)ds%_Kxq\"ugh'bf^?6cD-rA<pT)w=gKve/Qf[/7tyg`auYl%F0d>#T_3", null, "http://www.zodiackillersite.com/viewtopic.php?p=38834#p38834", true),
		new Cipher("Jarlve columnar transposition after encoding: Randomized cycles (0.2): r2_ct_p2.txt", "oNY*Pw)Ah5(F@5IZ\\cSTk`ulh2?\\<b,4a_&<\"mf*HMJvSD(C9frberBL;c0@VWf84?U=-j.sbL]v$r<\")Z,FK5DluI]skb_AppoPfT\\2W*vCM,Jw=9S`e2sK-=LIFaZ8kVL\"NC)rrv]@TSW.$\"HoU?;lYmP\\2D8jJw<@&_BfU9Wv)0u8M\"AeY\\<Lsr5FN;J(vZ$J-`I?2-lKK,f45H=\"M*Kp0uDT.04&K9UC8:k(=S*L\"\\h;,KWp&aS_2ka$jW0b0IwC,$NkTc&mDso.u5=`]mB;f$*)ePrjUHBeCFF,c8-w\"QVcJoWs@ZJf&<.90;Tl(h$ACL_B\"9J\\S5rVD*`I", null, "http://www.zodiackillersite.com/viewtopic.php?p=38834#p38834", true),
		new Cipher("Jarlve columnar transposition after encoding: Randomized cycles (0.1) + 1 high count 1:1 substitute + more orderly columnar transposition: r1_s1_ct_p3.txt", "aLAljhKkY(Tu+LUo1cM6BrZrn3^i_'\\&AyV0K_Y9:dOeN)WcS2QT\"lkp;19:.+(#YMAJB%+u-LRajpy:VaEI&o0b\\Ni3_gfW#e.O\"'2+b6zr%SaYIMegX%%T^B3;-%PKAV9i.:;_MdWksc)LO%Nhf+\"Pl3jb(1p%U0SQ#gBaYyn\\E.Wd;z%@seTfK%'(2)%ViXNQRrlP-%#y;%cPE&RIY1J:TkE_AQ0%dWO)6PN\"S(scU.9+&sroME%B_\\1p%3A\"Zh%nOXz%z-%6YIaiM#@K\\%f+hbynuY%eQ:%V^%g'2L.BsTZR&6WrM3Q%j_E0i%No&lP#h-OS^bQ)AnK9_#Jd", null, "http://www.zodiackillersite.com/viewtopic.php?p=38834#p38834", true),
		new Cipher("Jarlve Half split perfect cycles + fully randomized cycles: (1st batch of mystery ciphers) c_r_p1.txt (previously m1p1)", "bfWKU2:8hDz<>=H>aC0YIT&[ujgsG[vQr5m_Axb#WGX4,]e&Jk'NRK:SfDzFnj8@<6QUsx?;=_Hi,C[goYI/rGuXTJb[#m]QvGkZNRFUi4A[6zWX/hHeTaS?v2:f8G4Q=g'Dx<Fj*C[XY#;uQHGk?m,shabJ<]t>Ui=R5CWg:[=^Ux0]?#Ui#'6J<Cg?jJ<LHri,4I2G4_enb?;6<ji8#;]ou[g>T,kHeD#sGg'/gn;=Rj@sCsnshho=iYoHix:R>T,/@:IuNz@/Shk;C:;6^]KjS8UZnWSa0U5vQUQLGaT^C[DnDa8z4?F:*=LHAQLR/XY0]5T&G=wH&nj8a?iL", null, "http://www.zodiackillersite.com/viewtopic.php?p=38834#p38834", true),
		new Cipher("Jarlve Half split perfect cycles + fully randomized cycles: (1st batch of mystery ciphers) r_c_p1.txt (previously m2p1)", "9!q(/ZP!,eVpb)2%FDu\"4axX\"qceXX2mE6NoxSLcP5H#ktoxS^J1SZq==eVpBP=lpMH/LVdNQo#kkDX^h)4ax5/mMSe5cJ)H#y*vaSp)k#xyMVeYM32oa=Fd#(q!3>#H\"dJPVppq0DXY)dJ/H#XcdJke!=PSpt?%Dj/S4\"Pd9X\"0)Vh/c^tk*I1SpQdc9VpO2ECj#4Zy2no]L^JMpqk-*NDu\"5d%aCc#nP^e>*I1dBJ)S9vL/q]P!=htjQu2kVeSbMCal96D1VvMFGcN\"LIa7)(q,3/lBP-!ht4#YQmOX=10Dye]9FGS2^pL7\"O#xHOVMY)u/6aE5tO2xBq,3*jO", null, "http://www.zodiackillersite.com/viewtopic.php?p=38834#p38834", true),
		new Cipher("Jarle Polyalphabetic cipher: Perfect cycles + 1 high count 1:1 substitute + 15% chance of assigning a random symbol: c_s1_pi15_p1.txt", "<;?-(_70MBE>3\"83\".X-).'N=LZ<(YQ^JU#$'E?/M@%WD^+JET+AE-7I3LEO5<;!>&1H?EK#!BS6\\=NZ:R)%'(]^[E7Y/+C%F@TP+5OPD8JNAEL1.0L$AM\"KA_<I;(Q^GZ#?E>O73HY%E/+!1W@!TM6L#043>=F3R\\]EUC<K?NP9GEXHZ/!DT#&EO=KT7EF*S'6\\8)-(QB$2LZ+&><DM/#R*;YT3[6KW8?Z7@/+.T.#]EL4XC<5?\"I:P\\$XSVE6E3AD&V7UP[E7A;0KUGL+.2H8<)M!42?\"I:=)8^R%,F;A9]K74L0MEQZO[9C*WJS4E&1PXGU['NH,SJ0<\"I/6*", null, "http://www.zodiackillersite.com/viewtopic.php?p=38834#p38834", true),
		new Cipher("Jarlve Mystery cipher: m3p1.txt", ">Jd*';KP!>n(pA4E<'=A^0O`'K@d\"_C[$I+r3Y>fd_my8Ao$gu%xY*dPJ>n:6K?&Fez'KgR\\Ar427'\"9HAIcO\"AmeY>_@+'[C`V-0n('8y3`xgdzc!4o0<?uy;dPJ_C[Af%>n:FKt'\"mAR\\'z4`9V+2K!<Kg('apA7'Y^Ad@>_AQ'n=Afu'8R%eYFA9V>g:W4$72yI*\"CorZd@\\x(K8?f+'HA`uEc2@CrKVd_9\\0R6%'n>&>AdZKJP='7AHy8Y>np02c&d^'xg-e<!f+AK%et';>?JA-6dP!='I4mA[#`?xQ'\">ZK<JYCuFdtAWyOz#gc['H'^0$_A#436K!PR7W", null, "http://www.zodiackillersite.com/viewtopic.php?p=38834#p38834", true),
		new Cipher("Jarlve Mystery cipher: m4p1.txt (codename cascade)", "TU6JPJ(WIZ@:9%N9*C'0G3S5MQY_O[DK->\\?S=)LE#&7+2?-A8F1,JTUW6@^!(I\":<KVZ=X\\H?$.BP5Y;%GRSOC&3AQ[LF0KN#8\"1,^M.D-5<@_&R*7?3UWX$J)I*ONK2Y\\E=:^T/V[&HLFPKD#8X\\+6UW(A:%49C.0,>MZYQ52/V@'HL8PBXF1=^%YL_A:]7S.+$GJON??!)8\\<^E.IXFC;0[Y9RBLD?T86#X\\3Y!FM,(\"Z2Q!_*U'V.H;7+@)=91.<\"E>PRA\"3WIL\\%TF1/CJ6*U0\"!(WI'MG$&2K]5*</VOZ!QUW,N8:_/H]D-&]@RKP;%>3S[C]7-!)I*XB]", null, "http://www.zodiackillersite.com/viewtopic.php?p=38834#p38834", true),
		new Cipher("Jarlve Mystery cipher: m5p1.txt", "GJ$[P8E0%88C#R,466&5H);\"WIS$>@UFLB-+;JEAQ82'Y?NL88*V8=G:)88\\Z<09CZF=Q:<(P>,BTG\"J3@HI;%52V!G@S,WFUWA_]G\\.Y8L\"I5$2VF,N]8:/UQE)0>'F=+*Q8C\\GX*@278(!F\"^SA-B$%TE8CAO#W2.8R^Q/GHPK7+&!<R5YS;I8\\0A/$-CDU/BT'=[>,6N1E6(V\\GY:JE.3=<S4])AU+7/$^-*IJ5(P8'9Q7[1$)V&!T<3'Y/E8#^B]_GRWI!9V%7S-.>*]XE?$:PP_ZQ0%&WH,28FM\"QIK5?Q1G6)8U7C$X!D'LEM8V..3=$];@PD,L]E0%ZTM", null, "http://www.zodiackillersite.com/viewtopic.php?p=38834#p38834", true),
		new Cipher("Jarlve Mystery cipher: m5p1_stage1.txt", "G)$[P?E0%Q8C#7,46!&5HI;\"WGS$>@UFLR-+;8EAQ^2'Y.NL8/*V8[G:)$8\\ZE09C]F=Q8<(P+,BT7\"J3!HI;>52V8G@S-WFU^A_]8\\.Y'L\"I8$2V%,N]6:/U?E)0>'F=<*Q8C\\GXP@27J(!F,^SA-B$%6E8C5O#WT.8R=Q/G\"PK78&!<J5YS*I8\\WA/$8CDU;BT'H[>,+N1E<(V\\QY:J-.3=@S4]BAU+G/$^<*IJZ(P8E9Q7G1$)0&!T53'Y8E8#VB]_QRWI89V%6S-.G*]X=?$:)P_ZE0%&7H,2!FM\"6IK5>Q1G:)8UAC$XWD'L2M8VF.3=R];@PD,LZE0%/TM", null, "http://www.zodiackillersite.com/viewtopic.php?p=38834#p38834 and http://www.zodiackillersite.com/viewtopic.php?p=40999#p40999", true),
		new Cipher("Jarlve Mystery cipher: m6p28.txt", "HER>pl^VPk|1LTG2dN+BB+(#OP%DWY+.<*Kf)y:RDKfHEc*pMUZzkJSRc:O783Z|_E9K.St9)jU5N:>(pFH&Pc54/-CF^N%G:LflRq(Lj)KtZ_.;j;qX)@DlkVbd9czAlF+ScCB+Sc86A>L4p345OBU#ET+NT|9BdtXUN8>P71t9|2*.J_G@_8CMRG7y1H/pKD(-H7T#N4T_^8DOOjj^LN3dzB<O>OC;%(:6EC>(WMCL|U9KNTB3btZk5SkA4t8SG9U&lcJ*cXqV/+T1XzPBMG#2:2H@R>d|Sq(-^.p+f;qE+p8y*4JMJ8kCtDOHX&j_b)Kj4BMddT76G^U#<J5Y", null, "http://www.zodiackillersite.com/viewtopic.php?p=38834#p38834", true),
		new Cipher("Jarlve Mystery cipher: m7p77.txt", "!\"#$%&'()*+,-.-/0123456$$789:%;<2$=>?@(<AB&!CBDE4F;G#$H5$-*I$JK$LM,9!NO,P'QR\"ST!!'CC4&U6-V*<%WX(&E28Y8Z$[D!E(!0);UG)HS,\\4.K81]^5A8<5L_BN*7;D0OEI]>,US%J#:$Q\"3*24Z6P@=F%'V[8(0.8!$<'HBS&$TD5EKM$X1-@$RWX,U<`142NYB(\\8)_$&L:I^G7;S0E>=$B!$,F4[;aDO*!EMS$*VR%-(%$W\\Y^,]5/();P7=>4$U8!BF8ZD92&0AT-62['!;J+E$QE5\"#',.a<U$P?B04S!a$<,M_I`\\$@-54&%^S;&9R>83", null, "http://www.zodiackillersite.com/viewtopic.php?p=38834#p38834", true),

		// converted from original encoding
		new Cipher("Jarlve 340fbi1969.txt", ">MDHNvtSYZOnAIKz+pC-kBOyhBxm<WqtFI++UWCYWpPOSHT/QUvLUIBWuF+<CacL+TpRIp2eYBKQOv^2rMwGb+-fZuV>ECld1kcXByuGFN^rfbdo2pVdx++RnzQpBFfIN+gySlaYb<puRJIkfTdM2+0BFOKMTovByDIExf/R+u-YuuV+^J+Ovm<FBy-KdaAypBFbRZ+o+M<qQGbJrsgO+hNyY+iLlILY2VGXpubzjwK+fgvnR^FuO-kqCtF>bDQ/td0+PFfIqsxY+MlhSvvm^unkVevO++RKbJHYgLUQWGZu+Mp1yBNv+BQgOjDWy2<kKrUqbGTLcItPV^uv>REH", null, "http://www.zodiackillersite.com/viewtopic.php?p=39097#p39097"),
		new Cipher("Jarlve 408plusover.txt", "HER>p>l^EVPk|1L|^TG2dN+B(H#RO%DW+dY.+<l2V*WKf)y+:cMUZ>Hz^RPkJlE22SW7V<82p.3_91B#2Tdt+O2WN:H%jM(WLtc5UZk)2j+7SPtWN^KyUz^83>Rz^OL27#Yl2kkVFp%212MTWDtc8Y_Hz^R:k22|(2)Zd7l#VBp21PGTjc2f8MS<k(#jH:k&K+_93d>O2.yJRcYtkl2E8M)G7%#|N_jD.VcHt8YS)JMp225l1VJHz^GT92GKfP2<|U_S4ld(*:5NEEjY)VM2F7>HE2p4JRz^G22LWTW&BztF2OHJH^EPKcklF2&3+W&<UWTGpdN+%7&K+JHEz8f2", null, "http://zodiackillersite.com/viewtopic.php?p=34534#p34534", true),
		new Cipher("Jarlve 408noplus.txt", "HER>p>l^EVPk|1L|^TG2dN+BH(R#O%DN2WYN.lV<D*Kf)Ny:cMU>HZ^RPkzlEJDSV.7pY83_1+(T29N#DdyHOtcBDL9:jMUkftNSJP9Dd^*)MZ^78>RZ^#LS(WlkkV5pO1cTD%9:7W3HZ^Ryk|BfU2Sl(V+p1PGTt:K7cJ.kB(tHykF*N3_82>#Y)zR:W9klE7cfGSO(|d3t%YV:H97WJfzcpjl1VzHZ^GT_G*KP.|M3J&l2B<yjdEEtWfVc5S>HEp&zRZ^GLDTDF+Z95#HzH^EP*:kl5F8NDF.MDTGp2dNOSF*NzHEZ7K", null, "http://zodiackillersite.com/viewtopic.php?p=34534#p34534", true), 
		new Cipher("Jarlve perfect cycles, letter 'a' not homophonically substituted (count 24): p14hs63a24.txt", "zL6ZQi1~Vf0'7?#KoE]{+BpYSwnE=^G\\lN&fIQ%G�gMk6@l$J1w\\G#)c=(2%=/0R97T=�D.?^K5vl+:ownr3|pNzYLGZ{]IMV~(2=Rc6:l/SC&1im0'^�.3gkG7!KzQr+p=J=$@?#ND]oCS=|nl�)9QvVM&RT6@f1\\%G$g.=i0r:=37w~=#Dz=\\%=�n]LKY{?w+2\\=ZpfN='V=$=lkoB=|G&RS/i=:lZ.M/%�rGQ#YIlZc('V/?k6~&E=391|^G)Tzf0w\\L=m7]{ovgIR=.r�iK@Yc+�9p%NB:TM'LlV{Sv&63I1k0=zcG.D7=]9lRGrQ$K?E:fo+!lnp#3w~@NV", null, "http://zodiackillersite.com/viewtopic.php?p=34955#p34955", true),
		new Cipher("Jarlve superimposed 4 suspected wildcards then removed them", "HER>>plE^VPk|1klLTG2dHNR+B(#GO%GDp^W#Y.<*GKf)yH:lRVPcEM#U^DZz%JS7|NLO8G3#THB_)d#1f9ytP<_GUMV8#TlY*y:lZJ>R:l+1UNjpPP^5zB|)L#fZjSH:lRKP3kd<OU^2z|VFL_f3.MDPdN_HKP&YGS7JO>+%Rfj8PpEZ)UBNkTS_%^fH8ZjM<c)z9|^cH:lFL73FY.VDkySMpOdK9TEE_j<^)5U>HE4c:lF1#L#&2:853+HcHEYfPp53JG#Dy#LFzOTGU&YGcHE:Z.", "thecoreyswereoneofthefewoldfamilieswholingeredinbellinghamplacethehandsomequietoldstreetwhichthesympatheticobservermustgrievetoseeabandonedtoboardinghousesthedwellingsarestatelyandtallandthewholeplacewearsanairofaristocraticseclusionwhichmrscoreysfathermightwellhavethoughtassuredwhenheleftherhishousethereathisdeathitisoneoftwoevidentlydes", "http://zodiackillersite.com/viewtopic.php?p=35042#p35042", true),
		new Cipher("Jarlve perfect cycles, 4 wildcard symbols, no homophones: p70chs63.txt", "[VK]EXk;,nHRix(^m\\B^\"wpvS}sH/�YFDq$bo|=�gblb&_-uwRGx9Ma^'b1*:Q~.;4,\\p2|X([^}\\\"TunbUMbFVQi]@vqDY�[-K2EoYbU]^g.t$l:&b/Y,n_Vk'm=p9b*v(;RYF\\4+|XuGb\\M}ql-Q.\\,2po_�Si|Y:�b'xYKgs\\u1*(bFVM\"/bt9wQ~DY.[q-,@=EypkBm$Xnlb}|Sob_:u;Rg^9MXsx}yHi\"U�\\&^/4D'$v*VQYK\\\\b1(-.�:E]koFba=RgB+m\\S9Y,qblVx[Y-�bnKY_\"XyEv2bwp;k'bi�mUb}B|S/*:&D=ut�(M;KosE$i[Vk]mgn^2Ub9F", "shamanarespiritualguidesandpractitionersnotofthedivinebutoftheveryelementsunlikesomeothermysticsshamancommunewithforcesthatarenotstrictlybenevolenttheelementsarechaoticandlefttotheirowndevicestheyrageagainstoneanotherinunendingprimalfuryitisthecalloftheshamantobringbalancetothischaosactingasmoderatorsamongearthfirewaterandairshamansummont", "http://zodiackillersite.com/viewtopic.php?p=35043#p35043", true),
		new Cipher("Jarlve perfect cycles, 4 wildcard symbols, no homophones: p70chs63.txt version 2", "YacUyF'pSwCE-kV\"cmo\"1Z8IyJNb='nMs_4eu*LqFeze\\!:S6E0kJ8&\"3e.V>*AS;28m*,SuMv\"Fm1/8YeW*e_+SQ5rwzsnIq@cUyJne,W\"u8)4!O\\epn*v3a'Vc-SFeMY_=Enzm2^8J*0emSu!3:8*mS58FVwyL*n>'eMkncJZmS._ze!+81;eRuN*AsnSI3@8rQyB*'oc4FqVeJSyueMO8pEF\"J*u6kFBC-1U'm\\\"=2s_4vzaSncmme.!:8Y>y,'J3e&LEuo^cmyFn*VeM+kwn@'eIcn_1JByqWeZS;'zeQvc5euo8yp!O\\s-*)'3S=cFNy4LYa'UcJw\",WeuV", "shamanarespiritualguidesandpractitionersnotofthedivinebutoftheveryelementsunlikesomeothermysticsshamancommunewithforcesthatarenotstrictlybenevolenttheelementsarechaoticandlefttotheirowndevicestheyrageagainstoneanotherinunendingprimalfuryitisthecalloftheshamantobringbalancetothischaosactingasmoderatorsamongearthfirewaterandairshamansummont", "http://zodiackillersite.com/viewtopic.php?p=38807#p38807", true),
		new Cipher("Jarlve some randomization in the cycles, some 1:1 substitutes: p44mhs63.txt", "kuuo,er1F.73[g%tV^J^R`v_>,w!<\\OXpP@E3JTX3[g'N767wvEutGI-(vh.+JzPX<@QTL:OzoNi`-;7Z71`tTvULh3Ep,kXr\\VFQ@E!RNP3z+>T'Ie[Jl`.@yquTGhd%L+n#iu(woIig:Cle^3t\\EJz_OrNTP<7#-Jz^pXk/-@VY;7R>_1vN[P3:=PJzwgO\\!'Nh;J,eL.Enh3GN^XNT1@qh;t[;?3y+J`/1iU(VikoXCErRIi;7F!h>,LhevN_<N67nwOY'@GIe3EdT;J%(E1+uPO\\JT@go,73iN7v!e3ruhXv^RI:tPeOzp-#nTz/uU?kJzm_#l3,_yFOz>N[", "allthetracesofaffinitywithorconsciousnessofthebeowulfthatwecandiscoverandtheyareveryfewaresuchastofavourthisdatetheonlycompleteparalleltothefableisfoundintheicelandicsagaofgrettirwhoisakindofnorthernherculesthisheroperformsmanygreatfeatsbuttherearethreewhichbelongtothesupernaturalinoneofthesehewrestleswithafiendcalledglamandkillshimandtho", "http://zodiackillersite.com/viewtopic.php?p=38807#p38807", true),
		new Cipher("Jarlve Wildcard simulation (slightly randomized cycles, hiding of bigrams) though not 100% sure about plaintext anymore", "=`Y>[Ta1Tj3ZC@RykoqB7yRLu7-F?(ONo5n:dx+VSK7%IgaU4PW0tX!kFBApu;H3Ed(E:wuj=74+v7w#TSdC777?u7PV.z%qeUgSOb`RZSa7`A>dF7][73nLiwKyGvV#5HT=[pXeB%gUk(Hx?]7HFbBVg-ak7IO;d`1w%:AEj+gqo.Su7PeCFVLVdi@0p3dUc+Kt50yN=7cbXKFv4:a]xTEkG>%NHz71`u51Bj(-d!TX77`VRk?T5b7IcCOoAw77v%#:(XO+ZTFSEL0P@kP>g7BNi%WpHYun-.%kdjIbCzd:?7wgt;qdS#PO5GFLzceudPoTwik[ykUXn>VEp%Bv", "ilikekillingpeoplebecauseitissomuchfunitismorefunthankillingwildgameintheforrestbecausemanisthemostdangerousanimalofalltokillsomethinggivesmethemostthrillingexperenceitisevenbetterthangettingyourrocksoffwithagirlthebestpartofitisthatwhenidieiwillbereborninparadiceandalltheihavekilledwillbecomemyslavesiwillnotgiveyoumynamebecauseyouwilltry", "http://zodiackillersite.com/viewtopic.php?p=38807#p38807, http://zodiackillersite.com/viewtopic.php?p=35458#p35458", true),
		new Cipher("Jarlve Mystery cipher before columnar transposition, perfect cycles and a few 1:1 substitutes I believe", "OiL6Mua5A1=-d;oh[8:1z_1K0dMAU4&\\H;yIZ@kq>o7uh8zUl10dHGb']Ooue8Q/U1k3zSf!=^-d_rqHeu7s<n:Uag[foLYc510Z48;'-lHGt`z1doh8%MS1ku]qi=@&\\7KaQUg4Z:z<>H1^'S%1/dy1CoQ3u;Uc1=h80`Hlt9r1IZkizfuG&\\U;>d1Lq<'.]57/H1+uKaSy4%10r-koQ6AL!Ulg38b=GqH]nz<+_5suatK4%9:U7Hl&GZ0d1^'f`yMcr]iS@-k\\oa.84qn617Lu:10=>U11k@Q!9/zfd3o-8h5Hlz<^Z:q.'9doY1.u7`8A1K[fUGgH]tn+iu1c", "chaplinalsorememberselsatellinghimaboutthetimeeinsteinconceivedhistheoryofrelativityduringbreakfastonemorningheseemedlostinthoughtandignoredhisfoodsheaskedhimifsomethingwasbotheringhimhesatdownathispianoandstartedplayinghecontinuedplayingandwritingnotesforhalfanhourthenwentupstairstohisstudywhereheremainedfortwoweekswithelsabringinguphisf", "http://zodiackillersite.com/viewtopic.php?p=38807#p38807", true),
		new Cipher("Jarlve Mystery cipher after columnar transposition (I ran into trouble here and had to manually retrace cycles back from the original)", "HER>pl^VPk|1LTG2dN+B(#O%DRdLW>Y.dV<*W+Kf)y.:T#cd^MkpUWZ>YdzJS#7ky^831_k3+9c|tj:YD5>F&FH.d4OM/#P-G2(^WCq)#TRz;Y98>^+VX1d|S7HdyW:(lcNB/*k%dt>ZMOK<@Y;dzdj+9-kYX^7)RbW#.|qdJT+MRW&d<ydlB%k>Ff8GzNk2:HAKSd6Z9ctYy.W_(;)&J1L27/^dES85G#Uk:qY6D+H4|tcb>.N(dOzW8M)jY;BSV^%FXH-#&1y9AlK*2EW<(:ddyc.|Odk4dFbGT*7YZ++1#J)>_^zOkAtjc:#ACd>XbM^8Pl6LdkF-Wq4Yd/NS", null, "http://zodiackillersite.com/viewtopic.php?p=38807#p38807", true),
		new Cipher("Jarlve cycle randomness and wildcards", "HEER>pl^VPk|1LTG2dNd+B(#O>%DWY.<*Kf)|Ny<|1L:ckMk%()EGUZzJ(SP7N8K<Wf3y_9.8RctBzjk5k^BGy(F_S|)*>H<lY2V3f)D+cK|87Oy:Zp1N&BPf4/EyUS-T_7CqtEJ%RZtL9;&pd|GY)N8#.lcyKWkqzN8d*<HXzf2@jk+O#^(c1K|9bKN8%L.YD:cSjN>p_P)CS|Ucd<cy^f/SjG1jA|47NBX^tFJ2tHR<;)l+ZtjkVDSO>_Sp(c#WcMkC%.@:fUZp|)-yjNTJ)^7EK.YNyfLR>k|tck(Dp|lES<(d+Z9GKp.8*zqCy8XEFAHN86#q&|>#4V.8Oc1", null, "http://zodiackillersite.com/viewtopic.php?p=35276#p35276", true),
		new Cipher("Jarlve p44.txt", "kuuo,er1F.73[g%tV^J^R`v_>,w!<\\OXpP@E3JTX3[g'N767wvEutGI-(vh.+JzPX<@QTL:OzoNi`-;7Z71`tTvULh3Ep,kXr\\VFQ@E!RNP3z+>T'Ie[Jl`.@yquTGhd%L+n#iu(woIig:Cle^3t\\EJz_OrNTP<7#-Jz^pXk/-@VY;7R>_1vN[P3:=PJzwgO\\!'Nh;J,eL.Enh3GN^XNT1@qh;t[;?3y+J`/1iU(VikoXCErRIi;7F!h>,LhevN_<N67nwOY'@GIe3EdT;J%(E1+uPO\\JT@go,73iN7v!e3ruhXv^RI:tPeOzp-#nTz/uU?kJzm_#l3,_yFOz>N[", null, "http://zodiackillersite.com/viewtopic.php?p=35361#p35361", true),
		new Cipher("Jarlve mystery cipher", "HER>pl^VPk|1LTG2dN+B(#O%DRdLW>Y.dV<*W+Kf)y.:T#cd^MkpUWZ>YdzJS#7ky^831_k3+9c|tj:YD5>F&FH.d4OM/#P-G2(^WCq)#TRz;Y98>^+VX1d|S7HdyW:(lcNB/*k%dt>ZMOK<@Y;dzdj+9-kYX^7)RbW#.|qdJT+MRW&d<ydlB%k>Ff8GzNk2:HAKSd6Z9ctYy.W_(;)&J1L27/^dES85G#Uk:qY6D+H4|tcb>.N(dOzW8M)jY;BSV^%FXH-#&1y9AlK*2EW<(:ddyc.|Odk4dFbGT*7YZ++1#J)>_^zOkAtjc:#ACd>XbM^8Pl6LdkF-Wq4Yd/NS", null, "http://zodiackillersite.com/viewtopic.php?p=35459#p35459", true),
		new Cipher("Jarlve 408 redone", "071<=<2893EIK>LK:?O@QSWYA4\\5Z[MaXRcfWF6]0YbNgBfXG^dTH<1;72EJj38kIUaC4F_eDfLhi=Z`P>QVW[?bSG5Y\\c@aMZ]kTHJAgNX[UE6bV9LfS:;^M<078YNaB_d1FIJ2lCZbD`e=aL[\\]ch39:4GI>mK?i@HRA5^6YBlCEOD_`=g\\dTFJ>]^0GInMWhiNQ<ZLffj1_eUJ2g;`c?P@[\\KVh]Mf3^4Y_dS`jeAH5k6B0j178OCiDPNgE2FKThUk3R=VGkS9:\\c>4dTl?<5;7@kj689OAQLbBanZ:UlC[0j1;7HM]I2lDnNXbnEVa=P>RSWY?nLXj389^in", null, "http://www.zodiackillersite.com/viewtopic.php?p=29640#p29640", true),
		new Cipher("Jarlve plaintext in word search arrangement, CHS", "0157<?FHM23P41RN=>@U8ZIO]9JGK`A:0VcS^BdLW;<2Q73CHXD_]86P[M\\ENaTFefIRGS9JhbY^ic?@4K:`_j;75FdATgi=U1Vda8BL>HC]^eWIDkjkZJE6ch_OX?YM@QU]Ki9AV[d2PfLWN\\5HIjBROZb^34MC:gDX;YE?e1c`U<2l@aS=VNd[>WA7\\B<6TJXb=3>41`2l5Qa_fKRZb[0gm<kSCLGeF34\\f=Hg8]YP1U9ZIO^V_jD>Q`[:EamTW;<X\\JYk?j@UGK7MN8ZLVAWB6XdFneH=0mIYUCk[bDlE52f34Jj900OV\\:]MN?;OKZ7@RgeA8MdWLB>X[9kS", "h+dnsemit++f++gtssernoitanimilenhrwgaeuirns+fn+eireaandfotoetlgmccigmgniqlrabwee+inlapnndmuegcbsr+rulneisieaacriepppoiedwqatrertefraibnerou+fcirtodiipegtola++tencernreec+wlrs+velgsrtuosrenoesdgirls+s++l+vdflacigolohcyspgeimcm++ocsicnarf+rnoitarapesflonelygrnsroirpeperminttnoireredrumkcishyirrepoleved+c++ipnhhtronattentionegccenturiesronpg", "http://www.zodiackillersite.com/viewtopic.php?p=29972#p29972", true),
		new Cipher("Jarlve plaintext in word search arrangement, RHS", "016;<AFHM43Q14TM<=EW8\\LM^;KFKbE:0VcT_AdKV7>2Q;3ELWE^^95PZO\\BOaRFegLSGT;KhaV]ic?B1J9b_j::6Fd@Rfi>X4Ud`8?H<LE^_gXHBjkjZH?5ch_NYBUM?QU^Ki;?X\\d1PfIYO\\6LLk?TOZb_42NC8gCU;V@Ce2c`V>2lB`T>WMdZ>UB7ZA=5TLYa<4>21a4l6P`]fHS\\b\\0em>kR@JGgG21Zf<If;]YP3U;\\LN]V]kC<QbZ:DamRV7<W[IWj@jAYFH8OO8[IUEYB6YdFneJ>0mKXU?jZ`Al?63g31Kk;00OX[:^MMC;NH[7@Tee@:NdYKC=X\\9kT", "h+dnsemit++f++gtssernoitanimilenhrwgaeuirns+fn+eireaandfotoetlgmccigmgniqlrabwee+inlapnndmuegcbsr+rulneisieaacriepppoiedwqatrertefraibnerou+fcirtodiipegtola++tencernreec+wlrs+velgsrtuosrenoesdgirls+s++l+vdflacigolohcyspgeimcm++ocsicnarf+rnoitarapesflonelygrnsroirpeperminttnoireredrumkcishyirrepoleved+c++ipnhhtronattentionegccenturiesronpg", "http://www.zodiackillersite.com/viewtopic.php?p=29972#p29972", true),
		new Cipher("Jarlve plaintext in word search arrangement, MHS: CHS with about 1/4 RHS", "0159>?FJM23P41RN<=CU:\\JO]7JGHa@80VcS^AdIW9>2Q93EJXB_]:6PZM[CNbTFefKRFS7Jh`Y^icDE1K;a_j785Fd?Sfi=U4Vdb9@L>HA]^gWKBkjk[JC6ch_OWDXMAQY]Ii:EY\\d1QeJUNZ6LKj@RO[`^21M?;f@V;WABg3caX>4lC`T<YNd[<YD9\\E=5RLUb>1=23`1l5Qa_fHSZb[0em<jS?HGgF41\\f=Ig8]UP1X7ZJO^V_k@>Pa[8A`mSW9<X\\IYj?kAVGL:MN;[KUBVC6VdFneL=0mHWX@j[aDlE62f34Kk700OXZ8_MN?9MI\\:@SgeA;OdYKB>U[7jS", "h+dnsemit++f++gtssernoitanimilenhrwgaeuirns+fn+eireaandfotoetlgmccigmgniqlrabwee+inlapnndmuegcbsr+rulneisieaacriepppoiedwqatrertefraibnerou+fcirtodiipegtola++tencernreec+wlrs+velgsrtuosrenoesdgirls+s++l+vdflacigolohcyspgeimcm++ocsicnarf+rnoitarapesflonelygrnsroirpeperminttnoireredrumkcishyirrepoleved+c++ipnhhtronattentionegccenturiesronpg", "http://www.zodiackillersite.com/viewtopic.php?p=29972#p29972", true),
		new Cipher("Jarlve full grid diagonal NE-SE transposition, encoding CHS", "g(ihIOUj?Z';Q1m\"AnLf=;9&NiJ*cL)Y>piA6<g-VE@/b5nJ?(ahY=;C%.fIW7'TGRL2c)D+UeZ4=bjQm\"5466_N9aK;:@IBRLEjm&.X>7JGC9AapO'N%21DT_cRb:.=IEeX;mLgC4(7-NVW+=9i\\'<@h;nULVZI=*/O;BfbLK)=\\;Tp&L=NGaA?e@W+RB.nm691a%Q'R;L2Dm:f=39*)\"K6AnV>gX_(W;LC/ahcf)IpY0Ni%5<IUZ=Qb\"-;R+LN=E70Jj>T@i;SI_\\LSg=Y.N@?c;<'EILD2AS7&m(NbSnf)0VC_A=h91T\\;.n:fL'bI)*aU.=;OGZ0QSc-0\"R0", "ilikekillingpeoplebecauseitissomuchfunitismorefunthankillingwildgameintheforrestbecausemanisthemostdangerousanimalofalltokillsomethinggivesmethemostthrillingexperenceitisevenbetterthangettingyourrocksoffwithagirlthebestpartofitisthatwhenidieiwillbereborninparadiceandalltheihavekilledwillbecomemyslavesiwillnotgiveyoumynamebecauseyouwilltry", "http://www.zodiackillersite.com/viewtopic.php?p=30542#p30542", true),
		// re-encoded
		new Cipher("Jarlve Z340 manipulation: NE-SE, oxcart even", "HER>pl^VPk|1LTG2dN+B(T#lO+%pB1DWY.%<W+*Kf+)dT#y:fVc|MUyZTzJS7^8TTlTL3d_Gc9<tj_5F&LElF54J/H-F>5cCTYq7;jCY3F+TqROU/k1T2<tX4#*f+L@bE+tXzOTG^WT2)>3T5*NfVXTl^KTbEA<*LTpj#&VF*4E5|dX#l9_MTjl-+%lGLJFSO|C1U5@X|Lp^%.WZVTlJp1(OXTKqK*p96<+p1ZJpZYWM+l^5;c&9L|PTfATHp*J3j%MElUj.D;TzLC.>GFf%OL213(K*>bA+z.GO^3A/5W4BdlOUFp*Y8%NcETAO4tRH/6G5)ER<OMF4Ykf_*)8T", null, "http://www.zodiackillersite.com/viewtopic.php?p=30725#p30725"),
		// re-encoded
		new Cipher("Jarlve Z340 manipulation: SE-SW, horizontal", "HER>pl^VPEk|1VLTG2dN+B(k#O%BNDR#WEkY.<+*#K1kVf%<).y(:OcMU>ZyNf#zJ*S*7f83>1_9Vy)2t|BHjDJy59dK+#FH|&4&G^WF#d95S/ct-9Cq#;##X#&M@*E#y))(+@cEP)bC1S;*E*95&O8>k4J)#yU2d#DO9f7AR)9)5MA/7l%^y&t5BR&6YdVp#MB88(fp9+3#-F#lq#X|BqK(<Lc;)8E#f@B4OXMS^|V2>5t8l#E3)yP9B+GSLd28Y*#6*_H1dSJpO+G*DFc#&6MbV3.-*tGY4|9z&97EzyT1T<@11>jZk1C6S9(#3GNJ&^<<)B&RH4d#NP#y%B5K", null, "http://www.zodiackillersite.com/viewtopic.php?p=30725#p30725"),
		new Cipher("Jarlve Z340 manipulation: 340n_b19n", "H+M8|CV@K+l#2E.B)>EB+*5k.L-RR+4>f|pMR(UVFFz9z/JNbVM)|D>#Z3P>Ldl5||.UqLFHpOGp+2|<Ut*5cZG+kNl%WO&D(MVE5FV52+dp^D(+4(G++|TB4-R)WkVW)+k#2b^D4ct+cW<SPYLR/5J+JYM(+|TC7zk.#Kp+fZ+B.;+c+ztZ|<z28KjROp+8y.LWBO1*H_Rq#2pb&RB31c_8LKJ9^%OF7TBlXz6PYATfSMF;+B<MFG1BCOO|G)p+l2_cFKzF*K<SBK2BpzOUNyBO6N:(+H*;dy7t-cYAy29^4OFT-+N:^j*Xz6-<Sf9pl/CpclddG+4Ucy5C^W(c", null, "http://www.zodiackillersite.com/viewtopic.php?p=42923#p42923"),
		new Cipher("Jarlve Z340 manipulation: 340n_b19m", ")B.E2#l+K@VC|8M+H|f>4+RR-L.k5*+BE>MVbNJ/z9zFFVU(RMpU.||5ldL>P3Z#>D|)c5*tU<|2+pGOpHFLqF5EVM(D&OW%lNk+GZT|++G(4+(D^pd+25VD^b2#k+)WVkW)R-4BJ+J5/RLYPS<Wc+tc4Zf+pK#.kz7CT|+(MYjK82z<|Ztz+c+;.B+qR_H*1OBWL.y8+pOR^9JKL8_c13BR&bp2#MSfTAYP6zXlBT7FO%p)G|OOCB1GFM<B+;FB2KBS<K*FzKFc_2l+d;*H+(:N6OByNUOzp+-TFO4^92yAYc-t7yC/lp9fS<-6zX*j^:Nc(W^C5ycU4+Gddlcp", null, "http://www.zodiackillersite.com/viewtopic.php?p=42923#p42923"),
		new Cipher("Jarlve Z340 manipulation: 340n_b19f", "pclddG+4Ucy5C^W(cN:^j*Xz6-<Sf9pl/Cy7t-cYAy29^4OFT-+pzOUNyBO6N:(+H*;d+l2_cFKzF*K<SBK2BF;+B<MFG1BCOO|G)p%OF7TBlXz6PYATfSM#2pb&RB31c_8LKJ9^ROp+8y.LWBO1*H_Rq+B.;+c+ztZ|<z28KjYM(+|TC7zk.#Kp+fZ4ct+cW<SPYLR/5J+JB4-R)WkVW)+k#2b^DV52+dp^D(+4(G++|TZG+kNl%WO&D(MVE5FqLFHpOGp+2|<Ut*5c)|D>#Z3P>Ldl5||.UpMR(UVFFz9z/JNbVM>EB+*5k.L-RR+4>f|H+M8|CV@K+l#2E.B)", null, "http://www.zodiackillersite.com/viewtopic.php?p=42923#p42923"),
		new Cipher("Jarlve Z340 manipulation: 340n_b19r", "c(W^C5ycU4+GddlcpC/lp9fS<-6zX*j^:N+-TFO4^92yAYc-t7yd;*H+(:N6OByNUOzpB2KBS<K*FzKFc_2l+p)G|OOCB1GFM<B+;FMSfTAYP6zXlBT7FO%^9JKL8_c13BR&bp2#qR_H*1OBWL.y8+pORjK82z<|Ztz+c+;.B+Zf+pK#.kz7CT|+(MYJ+J5/RLYPS<Wc+tc4D^b2#k+)WVkW)R-4BT|++G(4+(D^pd+25VF5EVM(D&OW%lNk+GZc5*tU<|2+pGOpHFLqU.||5ldL>P3Z#>D|)MVbNJ/z9zFFVU(RMp|f>4+RR-L.k5*+BE>)B.E2#l+K@VC|8M+H", null, "http://www.zodiackillersite.com/viewtopic.php?p=42923#p42923"),
		new Cipher("Jarlve Z340 manipulation: 340m_b15n", "dEB+*5k.L(MVE5FV52c+ztZ2H+M8|CV@K<Ut*5cZG|TC7zG)pclddG+4dl5||.UqLcW<STfN:^j*Xz6-z/JNbVM)R)WkLKJy7t-cYAy-RR+4>f|p+dp1*HBpzOUNyBO+l#2E.B)+kN|<z2p+l2_cFKUcy5C^W(cFHk.#KSMF;+B<MF<Sf9pl/C|DPYLR/9^%OF7TB29^4OFT-+MVW)+k_Rq#2pb&R6N:(+H*;>^D(+4(8KjROp+8zF*K<SBKl%WO&Dp+fZ+B.;+G1BCOO|pOGp+2|5J+JYM(+lXz6PYA>#Z3P>L#2b^D4ct+B31c_8R(UVFFz9G++|TB4-y.LWBO", null, "http://www.zodiackillersite.com/viewtopic.php?p=42923#p42923"),
		new Cipher("Jarlve Z340 manipulation: 340m_b15m", "5VF5EVM(L.k5*+BEd<K@VC|8M+H2Ztz+c2lcp)Gz7CT|GZc5*tU<WcLqU.||5ld4+GddbNJ/z-6zX*j^:NfTSAYc-t7yJKLkW)R)MVBH*1pd+p|f>4+RR-y+)B.E2#l+OByNUOzpycUKFc_2l+p2z<|Nk+;FMSK#.kHFc(W^C5LYPD|C/lp9fS<FM<BTFO4^92BT7FO%^9/RR&bp2#qR_k+)WVM+-K8(4+(D^>;*H+(:N6W%lKBS<K*Fz8+pORjOCB1G+;.B+Zf+pD&O+(MYJ+J5|2+pGOp|Ob2#L>P3Z#>AYP6zXlFVU(R8_c13B+tc4D^OBWL.y-4BT|++G9zF", null, "http://www.zodiackillersite.com/viewtopic.php?p=42923#p42923"),
		new Cipher("Jarlve Z340 manipulation: 340m_b15f", "Fz9G++|TB4-y.LWBO^D4ct+B31c_8R(UVFlXz6PYA>#Z3P>L#2bO|pOGp+2|5J+JYM(+O&Dp+fZ+B.;+G1BCOjROp+8zF*K<SBKl%W6N:(+H*;>^D(+4(8K-+MVW)+k_Rq#2pb&RR/9^%OF7TB29^4OFTB<MF<Sf9pl/C|DPYL5C^W(cFHk.#KSMF;+kN|<z2p+l2_cFKUcypzOUNyBO+l#2E.B)+y-RR+4>f|p+dp1*HBVM)R)WkLKJy7t-cYASTfN:^j*Xz6-z/JNbddG+4dl5||.UqLcW<Ut*5cZG|TC7zG)pcl2c+ztZ2H+M8|CV@K<dEB+*5k.L(MVE5FV5", null, "http://www.zodiackillersite.com/viewtopic.php?p=42923#p42923"),
		new Cipher("Jarlve Z340 manipulation: 340m_b15r", "OBWL.y-4BT|++G9zFFVU(R8_c13B+tc4D^b2#L>P3Z#>AYP6zXl+(MYJ+J5|2+pGOp|OOCB1G+;.B+Zf+pD&OW%lKBS<K*Fz8+pORjK8(4+(D^>;*H+(:N6R&bp2#qR_k+)WVM+-TFO4^92BT7FO%^9/RLYPD|C/lp9fS<FM<B+;FMSK#.kHFc(W^C5ycUKFc_2l+p2z<|Nk+)B.E2#l+OByNUOzpBH*1pd+p|f>4+RR-yAYc-t7yJKLkW)R)MVbNJ/z-6zX*j^:NfTS<WcLqU.||5ld4+Gddlcp)Gz7CT|GZc5*tU<K@VC|8M+H2Ztz+c25VF5EVM(L.k5*+BEd", null, "http://www.zodiackillersite.com/viewtopic.php?p=42923#p42923"),
		new Cipher("Jarlve Z340 manipulation: 340f_b15n", ">;*H+(:N6R&bp2#qR_k+)WVM+-TFO4^92BT7FO%^9/RLYPD|C/lp9fS<FM<B+;FMSK#.kHFc(W^C5ycUKFc_2l+p2z<|Nk+)B.E2#l+OByNUOzpBH*1pd+p|f>4+RR-yAYc-t7yJKLkW)R)MVbNJ/z-6zX*j^:NfTS<WcLqU.||5ld4+Gddlcp)Gz7CT|GZc5*tU<K@VC|8M+H2Ztz+c25VF5EVM(L.k5*+BEdOBWL.y-4BT|++G9zFFVU(R8_c13B+tc4D^b2#L>P3Z#>AYP6zXl+(MYJ+J5|2+pGOp|OOCB1G+;.B+Zf+pD&OW%lKBS<K*Fz8+pORjK8(4+(D^", null, "http://www.zodiackillersite.com/viewtopic.php?p=42923#p42923"),
		new Cipher("Jarlve Z340 manipulation: 340f_b15m", "Rq#2pb&R6N:(+H*;>B29^4OFT-+MVW)+k_l/C|DPYLR/9^%OF7T.#KSMF;+B<MF<Sf9p2_cFKUcy5C^W(cFHkl#2E.B)+kN|<z2p+lp+dp1*HBpzOUNyBO+Jy7t-cYAy-RR+4>f|z6-z/JNbVM)R)WkLK|.UqLcW<STfN:^j*XC7zG)pclddG+4dl5|M8|CV@K<Ut*5cZG|T(MVE5FV52c+ztZ2H+4-y.LWBOdEB+*5k.Lc_8R(UVFFz9G++|TBZ3P>L#2b^D4ct+B315J+JYM(+lXz6PYA>#.;+G1BCOO|pOGp+2|K<SBKl%WO&Dp+fZ+B^D(+4(8KjROp+8zF*", null, "http://www.zodiackillersite.com/viewtopic.php?p=42923#p42923"),
		new Cipher("Jarlve Z340 manipulation: 340f_b15f", "*Fz8+pORjK8(4+(D^B+Zf+pD&OW%lKBS<K|2+pGOp|OOCB1G+;.#>AYP6zXl+(MYJ+J513B+tc4D^b2#L>P3ZBT|++G9zFFVU(R8_cL.k5*+BEdOBWL.y-4+H2Ztz+c25VF5EVM(T|GZc5*tU<K@VC|8M|5ld4+Gddlcp)Gz7CX*j^:NfTS<WcLqU.|KLkW)R)MVbNJ/z-6z|f>4+RR-yAYc-t7yJ+OByNUOzpBH*1pd+pl+p2z<|Nk+)B.E2#lkHFc(W^C5ycUKFc_2p9fS<FM<B+;FMSK#.T7FO%^9/RLYPD|C/l_k+)WVM+-TFO4^92B>;*H+(:N6R&bp2#qR", null, "http://www.zodiackillersite.com/viewtopic.php?p=42923#p42923"),
		new Cipher("Jarlve Z340 manipulation: 340f_b15r", "^D(+4(8KjROp+8zF*K<SBKl%WO&Dp+fZ+B.;+G1BCOO|pOGp+2|5J+JYM(+lXz6PYA>#Z3P>L#2b^D4ct+B31c_8R(UVFFz9G++|TB4-y.LWBOdEB+*5k.L(MVE5FV52c+ztZ2H+M8|CV@K<Ut*5cZG|TC7zG)pclddG+4dl5||.UqLcW<STfN:^j*Xz6-z/JNbVM)R)WkLKJy7t-cYAy-RR+4>f|p+dp1*HBpzOUNyBO+l#2E.B)+kN|<z2p+l2_cFKUcy5C^W(cFHk.#KSMF;+B<MF<Sf9pl/C|DPYLR/9^%OF7TB29^4OFT-+MVW)+k_Rq#2pb&R6N:(+H*;>", null, "http://www.zodiackillersite.com/viewtopic.php?p=42923#p42923"),
		new Cipher("Jarlve Z340 manipulation: 340r_b19n", "+-TFO4^92yAYc-t7yd;*H+(:N6OByNUOzpB2KBS<K*FzKFc_2l+p)G|OOCB1GFM<B+;FMSfTAYP6zXlBT7FO%^9JKL8_c13BR&bp2#qR_H*1OBWL.y8+pORjK82z<|Ztz+c+;.B+Zf+pK#.kz7CT|+(MYJ+J5/RLYPS<Wc+tc4D^b2#k+)WVkW)R-4BT|++G(4+(D^pd+25VF5EVM(D&OW%lNk+GZc5*tU<|2+pGOpHFLqU.||5ldL>P3Z#>D|)MVbNJ/z9zFFVU(RMp|f>4+RR-L.k5*+BE>)B.E2#l+K@VC|8M+Hc(W^C5ycU4+GddlcpC/lp9fS<-6zX*j^:N", null, "http://www.zodiackillersite.com/viewtopic.php?p=42923#p42923"),
		new Cipher("Jarlve Z340 manipulation: 340r_b19m", "y7t-cYAy29^4OFT-+pzOUNyBO6N:(+H*;d+l2_cFKzF*K<SBK2BF;+B<MFG1BCOO|G)p%OF7TBlXz6PYATfSM#2pb&RB31c_8LKJ9^ROp+8y.LWBO1*H_Rq+B.;+c+ztZ|<z28KjYM(+|TC7zk.#Kp+fZ4ct+cW<SPYLR/5J+JB4-R)WkVW)+k#2b^DV52+dp^D(+4(G++|TZG+kNl%WO&D(MVE5FqLFHpOGp+2|<Ut*5c)|D>#Z3P>Ldl5||.UpMR(UVFFz9z/JNbVM>EB+*5k.L-RR+4>f|H+M8|CV@K+l#2E.B)pclddG+4Ucy5C^W(cN:^j*Xz6-<Sf9pl/C", null, "http://www.zodiackillersite.com/viewtopic.php?p=42923#p42923"),
		new Cipher("Jarlve Z340 manipulation: 340r_b19f", "C/lp9fS<-6zX*j^:Nc(W^C5ycU4+Gddlcp)B.E2#l+K@VC|8M+H|f>4+RR-L.k5*+BE>MVbNJ/z9zFFVU(RMpU.||5ldL>P3Z#>D|)c5*tU<|2+pGOpHFLqF5EVM(D&OW%lNk+GZT|++G(4+(D^pd+25VD^b2#k+)WVkW)R-4BJ+J5/RLYPS<Wc+tc4Zf+pK#.kz7CT|+(MYjK82z<|Ztz+c+;.B+qR_H*1OBWL.y8+pOR^9JKL8_c13BR&bp2#MSfTAYP6zXlBT7FO%p)G|OOCB1GFM<B+;FB2KBS<K*FzKFc_2l+d;*H+(:N6OByNUOzp+-TFO4^92yAYc-t7y", null, "http://www.zodiackillersite.com/viewtopic.php?p=42923#p42923"),
		new Cipher("Jarlve Z340 manipulation: 340r_b19r", "N:^j*Xz6-<Sf9pl/CpclddG+4Ucy5C^W(cH+M8|CV@K+l#2E.B)>EB+*5k.L-RR+4>f|pMR(UVFFz9z/JNbVM)|D>#Z3P>Ldl5||.UqLFHpOGp+2|<Ut*5cZG+kNl%WO&D(MVE5FV52+dp^D(+4(G++|TB4-R)WkVW)+k#2b^D4ct+cW<SPYLR/5J+JYM(+|TC7zk.#Kp+fZ+B.;+c+ztZ|<z28KjROp+8y.LWBO1*H_Rq#2pb&RB31c_8LKJ9^%OF7TBlXz6PYATfSMF;+B<MFG1BCOO|G)p+l2_cFKzF*K<SBK2BpzOUNyBO6N:(+H*;dy7t-cYAy29^4OFT-+", null, "http://www.zodiackillersite.com/viewtopic.php?p=42923#p42923"),
		new Cipher("Jarlve Z340 manipulation: 14x25 grid, diagonal: 340m_14by25_ut13n", ">pOl%W^D(+VW)+kPYLR/9k.#KSMF|<z2p+l21*HBpzOUNLKJy7t-cYATfN:^j*Xz6-G)pclddG+4dl2H+M8|CV@K<UtdEB+*5k.L(MVE5R(UVFFz9G++|TB#Z3P>L#2b^D4ctGp+2|5J+JYM(+lO&Dp+fZ+B.;+G14(8KjROp+8zF*K_Rq#2pb&R6N:(+^%OF7TB29^4OFT;+B<MF<Sf9pl/C_cFKUcy5C^W(cFyBO+l#2E.B)+kNy-RR+4>f|p+dpz/JNbVM)R)Wk5||.UqLcW<S*5cZG|TC7zFV52c+ztZ4-y.LWBO+B31c_8Xz6PYABCOO|<SBKH*;>-+M|DH", null, "http://www.zodiackillersite.com/viewtopic.php?p=43041#p43041"),
		new Cipher("Jarlve Z340 manipulation: 14x25 grid, diagonal: 340m_14by25_ut13m", "+)WV+(D^W%lOp>FMSK#.k9/RLYPkzpBH*12l+p2z<|TAYc-t7yJKLNUOcp)G-6zX*j^:Nf|8M+H2ld4+Gddlk5*+BEdtU<K@VCzFFVU(R5EVM(L.#L>P3Z#BT|++G9J5|2+pGtc4D^b2Zf+pD&Ol+(MYJ+ORjK8(41G+;.B+bp2#qR_K*Fz8+pBT7FO%^+(:N6R&<FM<B+;TFO4^92ycUKFc_C/lp9fS2#l+OByFc(W^C5>4+RR-yNk+)B.E)MVbNJ/zpd+p|f<WcLqU.||5kW)R5VFz7CT|GZc5*SOBWL.y-4Ztz+c2BAYP6zX8_c13B++->;*HKBS<|OOCHD|M", null, "http://www.zodiackillersite.com/viewtopic.php?p=43041#p43041"),
		new Cipher("Jarlve Z340 manipulation: 14x25 grid, diagonal: 340m_14by25_ut14n", ">OpW%l+(D^k+)WV9/RLYPFMSK#.k2l+p2z<|NUOzpBH*1AYc-t7yJKL-6zX*j^:NfTld4+Gddlcp)GtU<K@VC|8M+H25EVM(L.k5*+BEdBT|++G9zFFVU(Rtc4D^b2#L>P3Z#l+(MYJ+J5|2+pG1G+;.B+Zf+pD&OK*Fz8+pORjK8(4+(:N6R&bp2#qR_TFO4^92BT7FO%^C/lp9fS<FM<B+;Fc(W^C5ycUKFc_Nk+)B.E2#l+OBypd+p|f>4+RR-ykW)R)MVbNJ/zS<WcLqU.||5z7CT|GZc5*Ztz+c25VFOBWL.y-48_c13B+AYP6zX|OOCBKBS<>;*HM+-D|H", null, "http://www.zodiackillersite.com/viewtopic.php?p=43041#p43041"),
		new Cipher("Jarlve Z340 manipulation: 14x25 grid, diagonal: 340m_14by25_ut14m", "W)+k^D(+l%WpO>k.#KSMFPYLR/9VBpzOUN|<z2p+l2-LKJy7t-cYA1*H+4dlTfN:^j*Xz6V@K<UtG)pclddG.L(MVE52H+M8|C9G++|TBdEB+*5k2b^D4ctR(UVFFz+JYM(+l#Z3P>L#+B.;+G1Gp+2|5Jp+8zF*KO&Dp+fZ&R6N:(+4(8KjRO29^4OFT_Rq#2pbSf9pl/C^%OF7TB5C^W(cF;+B<MF<E.B)+kN_cFKUcy>f|p+dpyBO+l#2bVM)R)Wky-RR+4||.UqLcW<Sz/JNztZ*5cZG|TC7z54-y.LWBOFV52c+|Xz6PYA+B31c_8+MH*;><SBKBCOOH|D-", null, "http://www.zodiackillersite.com/viewtopic.php?p=43041#p43041"),
		new Cipher("Jarlve Z340 manipulation: 14x25 grid, diagonal: 340m_14by25_ut15n", "H|D-+MH*;><SBKBCOO|Xz6PYA+B31c_84-y.LWBOFV52c+ztZ*5cZG|TC7z5||.UqLcW<Sz/JNbVM)R)Wky-RR+4>f|p+dpyBO+l#2E.B)+kN_cFKUcy5C^W(cF;+B<MF<Sf9pl/C^%OF7TB29^4OFT_Rq#2pb&R6N:(+4(8KjROp+8zF*KO&Dp+fZ+B.;+G1Gp+2|5J+JYM(+l#Z3P>L#2b^D4ctR(UVFFz9G++|TBdEB+*5k.L(MVE52H+M8|CV@K<UtG)pclddG+4dlTfN:^j*Xz6-LKJy7t-cYA1*HBpzOUN|<z2p+l2k.#KSMFPYLR/9VW)+k^D(+l%WpO>", null, "http://www.zodiackillersite.com/viewtopic.php?p=43041#p43041"),
		new Cipher("Jarlve Z340 manipulation: 14x25 grid, diagonal: 340m_14by25_ut15m", "KBS<>;*HM+-D|H3B+AYP6zX|OOCBVFOBWL.y-48_c1T|GZc5*Ztz+c25S<WcLqU.||5z7C-ykW)R)MVbNJ/zOBypd+p|f>4+RRFc_Nk+)B.E2#l+B+;Fc(W^C5ycUKO%^C/lp9fS<FM<qR_TFO4^92BT7F8(4+(:N6R&bp2#D&OK*Fz8+pORjK+pG1G+;.B+Zf+p3Z#l+(MYJ+J5|2U(Rtc4D^b2#L>PBEdBT|++G9zFFV+H25EVM(L.k5*+cp)GtU<K@VC|8Mj^:NfTld4+GddlYc-t7yJKL-6zX*2z<|NUOzpBH*1ALYPFMSK#.k2l+p%l+(D^k+)WV9/R>OpW", null, "http://www.zodiackillersite.com/viewtopic.php?p=43041#p43041"),
		new Cipher("Jarlve Z340 manipulation: 14x25 grid, diagonal: 340m_14by25_ut16n", "HD|M+->;*HKBS<|OOCBAYP6zX8_c13B+OBWL.y-4Ztz+c25VFz7CT|GZc5*S<WcLqU.||5kW)R)MVbNJ/zpd+p|f>4+RR-yNk+)B.E2#l+OByFc(W^C5ycUKFc_C/lp9fS<FM<B+;TFO4^92BT7FO%^+(:N6R&bp2#qR_K*Fz8+pORjK8(41G+;.B+Zf+pD&Ol+(MYJ+J5|2+pGtc4D^b2#L>P3Z#BT|++G9zFFVU(R5EVM(L.k5*+BEdtU<K@VC|8M+H2ld4+Gddlcp)G-6zX*j^:NfTAYc-t7yJKLNUOzpBH*12l+p2z<|FMSK#.k9/RLYPk+)WV+(D^W%lOp>", null, "http://www.zodiackillersite.com/viewtopic.php?p=43041#p43041"),
		new Cipher("Jarlve Z340 manipulation: 14x25 grid, diagonal: 340m_14by25_ut16m", "<SBKH*;>-+M|DHc_8Xz6PYABCOO|tZ4-y.LWBO+B31ZG|TC7zFV52c+z5||.UqLcW<S*5cdpz/JNbVM)R)Wk+kNy-RR+4>f|p+(cFyBO+l#2E.B)l/C_cFKUcy5C^WOFT;+B<MF<Sf9p:(+^%OF7TB29^4F*K_Rq#2pb&R6N+G14(8KjROp+8z(+lO&Dp+fZ+B.;4ctGp+2|5J+JYM|TB#Z3P>L#2b^DVE5R(UVFFz9G++<UtdEB+*5k.L(M+4dl2H+M8|CV@Kj*Xz6-G)pclddGKJy7t-cYATfN:^p+l21*HBpzOUNLR/9k.#KSMF|<z2%W^D(+VW)+kPYL>pOl", null, "http://www.zodiackillersite.com/viewtopic.php?p=43041#p43041"),
		new Cipher("Jarlve Z340 manipulation: 14x25 grid, diagonal: 340f_14by25_ut13n", "|AY8_cOBWLZtz+cz7CT|GS<WcLqUkW)R)MVbpd+p|f>4+Nk+)B.E2#lHFc(W^C5ycUD|C/lp9fS<FMM+-TFO4^92BT7>;*H+(:N6R&bp2KBS<K*Fz8+pORjOOCB1G+;.B+Zf+P6zXl+(MYJ+J5|13B+tc4D^b2#L>.y-4BT|++G9zFF25VF5EVM(L.k5*Zc5*tU<K@VC|8M.||5ld4+GddlcpNJ/z-6zX*j^:NfRR-yAYc-t7yJKL+OByNUOzpBH*1KFc_2l+p2z<|<B+;FMSK#.kFO%^9/RLYP#qR_k+)WVK8(4+(D^pD&OW%l2+pGOpP3Z#>VU(R+BEd+H2)GT", null, "http://www.zodiackillersite.com/viewtopic.php?p=43041#p43041"),
		new Cipher("Jarlve Z340 manipulation: 14x25 grid, diagonal: 340f_14by25_ut13m", "+ztZLWBOc_8YA|UqLcW<SG|TC7zcf|p+dpbVM)R)WkHl#2E.B)+kN+4>/C|DUcy5C^W(cFOFT-+MMF<Sf9pl:(+H*;>7TB29^4F*K<SBK2pb&R6N+G1BCOOjROp+8z(+lXz6P+fZ+B.;4ct+B31|5J+JYM|TB4-y.>L#2b^DVE5FV52FFz9G++<Ut*5cZ*5k.L(M4dl5||.M8|CV@Kz6-z/JNpclddG+cYAy-RRfN:^j*XOUNyBO+LKJy7t-p+l2_cFK1*HBpz.#KSMF;+B<|<z2Rq#PYLR/9^%OFk^D(+4(8KVW)+k_PpOGp+2l%WO&DpH+dEB+R(UV>#Z3TG)2", null, "http://www.zodiackillersite.com/viewtopic.php?p=43041#p43041"),
		new Cipher("Jarlve Z340 manipulation: 14x25 grid, diagonal: 340f_14by25_ut14n", "|YAc_8LWBOc+ztZG|TC7zUqLcW<SbVM)R)Wk+4>f|p+dpl#2E.B)+kNUcy5C^W(cFHMF<Sf9pl/C|D7TB29^4OFT-+M2pb&R6N:(+H*;>jROp+8zF*K<SBK+fZ+B.;+G1BCOO|5J+JYM(+lXz6P>L#2b^D4ct+B31FFz9G++|TB4-y.*5k.L(MVE5FV52M8|CV@K<Ut*5cZpclddG+4dl5||.fN:^j*Xz6-z/JNLKJy7t-cYAy-RR1*HBpzOUNyBO+|<z2p+l2_cFKk.#KSMF;+B<PYLR/9^%OFVW)+k_Rq#^D(+4(8Kl%WO&DppOGp+2>#Z3PR(UVdEB+2H+G)T", null, "http://www.zodiackillersite.com/viewtopic.php?p=43041#p43041"),
		new Cipher("Jarlve Z340 manipulation: 14x25 grid, diagonal: 340f_14by25_ut14m", "tz+cOBWL8_cAY|S<WcLqUz7CT|GZp|f>4+kW)R)MVbUNk+)B.E2#lpd+S<FMHFc(W^C5yc^92BT7D|C/lp9fN6R&bp2M+-TFO4z8+pORj>;*H+(:;.B+Zf+KBS<K*FMYJ+J5|OOCB1G+D^b2#L>P6zXl+(++G9zFF13B+tc4M(L.k5*.y-4BT|K@VC|8M25VF5EV+GddlcpZc5*tU<X*j^:Nf.||5ld4-t7yJKLNJ/z-6zOzpBH*1RR-yAYc2l+p2z<|+OByNUB+;FMSK#.kKFc_)WVFO%^9/RLYP<K8(4+(D^#qR_k+>2+pGOppD&OW%lH2+BEdVU(RP3Z#T)G+", null, "http://www.zodiackillersite.com/viewtopic.php?p=43041#p43041"),
		new Cipher("Jarlve Z340 manipulation: 14x25 grid, diagonal: 340f_14by25_ut15n", "T)G+H2+BEdVU(RP3Z#>2+pGOppD&OW%lK8(4+(D^#qR_k+)WVFO%^9/RLYP<B+;FMSK#.kKFc_2l+p2z<|+OByNUOzpBH*1RR-yAYc-t7yJKLNJ/z-6zX*j^:Nf.||5ld4+GddlcpZc5*tU<K@VC|8M25VF5EVM(L.k5*.y-4BT|++G9zFF13B+tc4D^b2#L>P6zXl+(MYJ+J5|OOCB1G+;.B+Zf+KBS<K*Fz8+pORj>;*H+(:N6R&bp2M+-TFO4^92BT7D|C/lp9fS<FMHFc(W^C5ycUNk+)B.E2#lpd+p|f>4+kW)R)MVbS<WcLqUz7CT|GZtz+cOBWL8_cAY|", null, "http://www.zodiackillersite.com/viewtopic.php?p=43041#p43041"),
		new Cipher("Jarlve Z340 manipulation: 14x25 grid, diagonal: 340f_14by25_ut15m", "R(UVdEB+2H+G)T&DppOGp+2>#Z3Pq#^D(+4(8Kl%WOR/9^%OFVW)+k_Rk.#KSMF;+B<PYLO+|<z2p+l2_cFK-RR1*HBpzOUNyB/JNLKJy7t-cYAy||.fN:^j*Xz6-z5cZpclddG+4dl5V52M8|CV@K<Ut*-y.*5k.L(MVE5FB31FFz9G++|TB4z6P>L#2b^D4ct+COO|5J+JYM(+lXSBK+fZ+B.;+G1B*;>jROp+8zF*K<-+M2pb&R6N:(+H/C|D7TB29^4OFT^W(cFHMF<Sf9pl#2E.B)+kNUcy5CR)Wk+4>f|p+dplC7zUqLcW<SbVM)_8LWBOc+ztZG|T|YAc", null, "http://www.zodiackillersite.com/viewtopic.php?p=43041#p43041"),
		new Cipher("Jarlve Z340 manipulation: 14x25 grid, diagonal: 340f_14by25_ut16n", "TG)2H+dEB+R(UV>#Z3PpOGp+2l%WO&Dp^D(+4(8KVW)+k_Rq#PYLR/9^%OFk.#KSMF;+B<|<z2p+l2_cFK1*HBpzOUNyBO+LKJy7t-cYAy-RRfN:^j*Xz6-z/JNpclddG+4dl5||.M8|CV@K<Ut*5cZ*5k.L(MVE5FV52FFz9G++|TB4-y.>L#2b^D4ct+B31|5J+JYM(+lXz6P+fZ+B.;+G1BCOOjROp+8zF*K<SBK2pb&R6N:(+H*;>7TB29^4OFT-+MMF<Sf9pl/C|DUcy5C^W(cFHl#2E.B)+kN+4>f|p+dpbVM)R)WkUqLcW<SG|TC7zc+ztZLWBOc_8YA|", null, "http://www.zodiackillersite.com/viewtopic.php?p=43041#p43041"),
		new Cipher("Jarlve Z340 manipulation: 14x25 grid, diagonal: 340f_14by25_ut16m", "VU(R+BEd+H2)GTW%l2+pGOpP3Z#>WVK8(4+(D^pD&O^9/RLYP#qR_k+)<B+;FMSK#.kFO%*1KFc_2l+p2z<|JKL+OByNUOzpBH:NfRR-yAYc-t7ylcpNJ/z-6zX*j^|8M.||5ld4+Gddk5*Zc5*tU<K@VCzFF25VF5EVM(L.#L>.y-4BT|++G9J5|13B+tc4D^b2Zf+P6zXl+(MYJ+ORjOOCB1G+;.B+bp2KBS<K*Fz8+pBT7>;*H+(:N6R&S<FMM+-TFO4^92^C5ycUD|C/lp9fk+)B.E2#lHFc(W)MVbpd+p|f>4+NT|GS<WcLqUkW)R_cOBWLZtz+cz7C|AY8", null, "http://www.zodiackillersite.com/viewtopic.php?p=43041#p43041"),
		new Cipher("Jarlve: bigram 15/19 scheme, 20% random cycles", "/NaAhDMBC*O[)Y07h!]\"_<h-.P,'g$*#@LaK64(!FKSWeJ)N1D/^I&0<XKNd+;$7)U\\e<4a?afKD=ah9C13#4h/h?G9a>,E%L+ghh[F0AOOM\"(dTHYR-O9hhh@2W.P2h$&!*a)h<h^0hDCfGaR1eXB_;6h,7$27g'YH>F(!1h4KE%#S/)UEA^?XHaTD=>C[3<aL2=Y$@J1*d*\"_#'KFfG]0[L-.,PhAgd+eIhh&04h++(WM=\"H6(aO7h>fh!\"^9?]=?S>hXG)e-9%hOR12ETA+.NhUP/&20;7<YBdK[@^IL\"e1/CFdJfhM_>-ID'C6S%>,@e<dg.]9=eBJP'h&$N", null, "http://www.zodiackillersite.com/viewtopic.php?p=42934#p42934", true),
		new Cipher("doranchak Z340 manipulation, width 16, diagonal", ">M+D|CHFc(Nk+)Bpd+p|fkW)R)MVS<WcLqU.z7CT|GZc5Ztz+c25VF5OBWL.y-4BT|8_c13B+tc4D^AYP6zXl+(MYJ+|OOCB1G+;.B+ZfKBS<K*Fz8+pORjK;*H+(:N6R&bp2#qR-TFO4^92BT7FO%^9/lp9fS<FM<B+;FMSW^C5ycUKFc_2l+p2.E2#l+OByNUOzpBH>4+RR-yAYc-t7yJKbNJ/z-6zX*j^:NfT||5ld4+Gddlcp)G*tU<K@VC|8M+H2EVM(L.k5*+BEd++G9zFFVU(Rb2#L>P3Z#>J5|2+pGOp+pD&OW%l8(4+(D^_k+)WV/RLYPK#.kz<|*1L", null, "http://www.zodiackillersite.com/viewtopic.php?p=43070#p43070"),
		new Cipher("Jarlve: bigram 19 scheme, inserted one random row", "721QJB&0RT9>XF5.\"S(=J%@$*7,43L#=:V<&0G'@BX26$U[Z,#9HNRS>*-KJUP%#:1FV\\C.'754B=+#6\"TK3<QXC(>\"$>+I,TL&Z\\R192.W\"%H66-G:0[FC@*<+VZ2PXNBQ)J59'>(LGHK-[\\1.P$S*'74@UN13G@[QN%<\"(&RISLT:[ZVKH-9N7C&2K$5,9<90F0KR9.#-I0+PFT54\\CX+.=%J[3R0WI)WJ<S#FT7Z;Q'5946(G3L$0&HBJR-P\"I,F>*)QUW2:51XK4@,VU$#3N*G%0(JW<@[WCBL:.\\HX1'KS\"#VB-P\\QF)5Z94=32:0(.$F+,%)7L<5N6VH-&", null, "http://www.zodiackillersite.com/viewtopic.php?p=43532#p43532", true),
		new Cipher("Jarlve Z340 manipulation: right shift, period 19", "dpclddG+4Ucy5C^W(+H+M8|CV@-+l#2E.B)>EB+*5k.L-RR+4>f|cMR(UVFFzKz/JNbVM)|D>#Z3P>Ldl5||.UqpFHpOGp+29<Ut*5cZG+kNl%WO&D(MVE5FV5L+dp^D(+4|G++|TB4-R)WkVW)+k#2b^D4ct2cW<SPYLR(5J+JYM(+|TC7zk.#Kp+fZ+B.;+c+ztZ|<z/8KjROp+8y.LWBO1*H_Rq#2pb&+B31c_8LK29^%OF7TBlXz6PYATfSMF;+B<MRG1BCOO|GJp+l2_cFKzF*K<SBK2BpzOUNyBF6N:(+H*;)y7t-cYAy29^4OFT-N:^j*Xz6O<Sf9pl/C", null, "http://www.zodiackillersite.com/viewtopic.php?p=43532#p43532"),
		new Cipher("Jarlve: Transposition cipher, period 19 scheme, one random row of filler", "R$[+4^G?DD?&-',78(-5#/=\\aK>Y)9FO#*L$IEBLS`H>T&(\"44K1SU>^HJR[XE+7]::ZN=L'A?#W,NY98T\\J<H5#FIES1-B/>4aSU2OY9?X>*8(`RJ&-T:)N\\K/4ID^ZSG[A`Oa=W*[41:JU#>7WA$;UL:=*L^N1AFDE-X(O\\K>'G?YIaXS-W+H85)B*\"F:NA*/,_WWR1V9OGTGEB>[Y;\\\"^(G&a`a9EU51&-TRI7_8EU;D\\:Z(IF2+'*$XX)?(D,TLAK\"WBS5#\"81$GXD&[DKIT1E&TNaA\\F`;>F/\\*'Y-9/?:J)T&aH+BK,/=5R0U^_7?HJLDU#>1*NF+[$\\IW", null, "http://www.zodiackillersite.com/viewtopic.php?p=43578#p43578", true),
		new Cipher("Jarlve: Transposition cipher, one random column of filler", "CJPB-)\"9WY:DRZ0#;!EC7119'\\P+2>(/VIABQ[8K0L'3CIK?,:-&G4>M)0Y+R?HO\"DZ(,8J+\\'PQG/BN<=&1W[I781CYRSMJ-:H2S#VC3IE<WO@:#4)AZ7(2@!B\\=NSYR;[QK;VA8>G0&/I<HZ1O'(\"\\=3W@M4?2)#C7AE<P/O-V@C>8I3WN:EA0[D@J',H>BQ4A\\+0Y['H)#G:?7\"\\,MN>P+<8VRS2ZJQ30?&=-\"'O4[():>W27G0V&SP9@,B'Y3MJR+-K4)Z#(!H><EO=B7QNGEY;SDI8R\"AZ;(P?;V\\M9K9W[B#2OY!S@D&3;-4J)R0ZHI\\,27S'(\"+VAK3QG", null, "http://www.zodiackillersite.com/viewtopic.php?p=43596#p43596", true),
		new Cipher("Jarlve: Z408 with column offsets to make bigram period peaks", "9ZTqcf#8LdLeL^SSYW%e5WEUBL)e)EMR#(MVPGRq/eVMN/lUrRBSJ+/YU_96WlV@^H9kk(YeZKcF%7\\NEXRk#@^/^G/E+#%D+AKJ6FR+L9UYU__8ZzV)HQHLNqM#IFBTdcfGTZ=AcdtGZBk6%YY+A%t(GBZ/IDJP79kAq@P%OPZPKPY9dOqHO9_9#IPIcDq#EKrRTPR%^ABMP9pRpBlI\\At@=#S9Vr5DO+I@O)pUNKpLqBzIMRVj)X86F%Q!XtW^U!8_W=WqqqHfYq=_N=pJRTI6qEGXVRDYBpkSkkUY5\\!HB8Wl5e%e)rA5tr+N8MT5eq)VOXSl99%\\t(5UQz8E", null, "http://www.zodiackillersite.com/viewtopic.php?p=43586#p43586", true),
		new Cipher("Jarlve Z340 manipulation: column period 2, incremental per-column upshift", "H+M8|CV@Kz/JNbVM)N:^j*Xz6-+l#2E.B)BpzOUNyBO<Sf9pl/CSMF;+B<MF6N:(+H*;_Rq#2pb&RG1BCOO|2p+fZ+B.;+B31c_8Tf#2b^D4ct+c+ztZ1*H(MVE5FV52cW<Sk.#Kdl5||.UqL+dpVW)+k-RR+4>f|pFHl%WO&DUcy5C^W(cM>#Z3P>L29^4OFT-+EB+*5k.LzF*K<SBKdpclddG+4lXz6PYAG)y7t-cYAyy.LWBOLKJp+l2_cFK|TC7z|<z29^%OF7TBR)WkPYLR/8KjROp+8+kN^D(+4(5J+JYM(+|DpOGp+2|G++|TB4->R(UVFFz9<Ut*5cZG", null, "http://www.zodiackillersite.com/viewtopic.php?p=43597#p43597"),
		new Cipher("Jarlve Z340 manipulation", "H+M8|CV@K<Ut*5cZGN:^j*Xz6-z/JNbVM)BpzOUNyBO+l#2E.B)SMF;+B<MF<Sf9pl/C_Rq#2pb&R6N:(+H*;p+fZ+B.;+G1BCOO|2#2b^D4ct+B31c_8Tf(MVE5FV52c+ztZ1*Hdl5||.UqLcW<Sk.#K-RR+4>f|p+dpVW)+kUcy5C^W(cFHl%WO&D29^4OFT-+M>#Z3P>LzF*K<SBKdEB+*5k.LlXz6PYAG)pclddG+4y.LWBOLKJy7t-cYAy|TC7z|<z2p+l2_cFKR)WkPYLR/9^%OF7TB+kN^D(+4(8KjROp+8|DpOGp+2|5J+JYM(+>R(UVFFz9G++|TB4-", null, "http://www.zodiackillersite.com/viewtopic.php?p=43611#p43611"),
		new Cipher("Jarlve Z340 manipulation", "2dG)TfLKJ1*H|<z2k.#KPYLR/VW)+k^D(+4(l%WO&DpOGp+2|>#Z3P>LR(UVFFz9EB+*5k.LH+M8|CV@KpclddG+4N:^j*Xz6-y7t-cYAyBpzOUNyBOp+l2_cFKSMF;+B<MF9^%OF7TB_Rq#2pb&R8KjROp+8p+fZ+B.;+5J+JYM(+#2b^D4ct+G++|TB4-(MVE5FV52<Ut*5cZGdl5||.UqLz/JNbVM)-RR+4>f|p+l#2E.B)Ucy5C^W(c<Sf9pl/C29^4OFT-+6N:(+H*;zF*K<SBKG1BCOO|lXz6PYAB31c_8y.LWBOc+ztZ|TC7zcW<SR)Wk+dp+kNFH|D>M", null, "http://www.zodiackillersite.com/viewtopic.php?p=43639#p43639"),
		new Cipher("Jarlve: column 17 is random before encoding", "[)H'S&%.7$9RGC=XAN)#KJ4UZ*,F@P:\"+7C1JSDQYN./[$ZQ5AF*'GQ.QR6T&WP,9XK@U<DJYA/%+O[*=15C:'(U6M<P72HR\"Z(S,O&CT$3WD%5GY:[K73F.A@='9&X+WI,I1#DY[/\"U2'ZT9F&6SL$LGX=4)/%(NKDPU3MR:.AY[+M1M#'4IJ5&DYN#FH*\"U2Q64I[S(2TZQN%O3(:H56@9$J*LW'#G4I7&<.@,O2A\"()F=Z)%K+LN17XS:$R\"GKH#/+JMZ*4.OCD1)YS>TW9P=M3[5X<Q6:,AN/<TWC%$5'37=#U@96H4PGKF+X)N#&1LSURCJ\"Z$W=Q)3.4NM", null, "http://www.zodiackillersite.com/viewtopic.php?p=43643#p43643", true),
		new Cipher("Jarlve Z340 manipulation: add one random column", "HER>pl^VPk|1LTG2d8Np+B(#O%DWY.<*Kf)FBy:cM+UZGW()L#zHJTSpp7^l8*V3pO++RK2c_9M+ztjd|5FP+&4k/+p8R^FlO-*dCkF>2D(c#5+Kq%;2UcXGV.zL|p(G2Jfj#O+_NYz+@L9jd<M+b+ZR2FBcyA64K/-zlUV+^J+Op7<FBy-YU+R/5tE|DYBpbTMKO_2<clRJ|*5T4M.+&BF.z69Sy#+N|5FBc(;8RylGFN^f524b.cV4t++cyBX1*:49CE>VUZ5-+M|c.3zBK(Op^.fMqG2ORcT+L16C<+FlWB|)L^++)WCzWcPOSHT/()p_|FkdW<7tB_YOB*-Cc+>MDHNpkSzZO8A|K;+D", null, "http://www.zodiackillersite.com/viewtopic.php?p=43645#p43645"),

		new Cipher("_pi test cipher, pluses as wildcards", "O9PP5o97W`flWZ8OmAQ+@@k\\Ah8OEW9Mg0n\\Kh8+D7NRVe=anVD`SLia:C7oX0@++ZDi`fR+b0X?dKk?+E8LV7YMgcSNhZaE5o6EYZMo+\\KM1ocgbm2[BZ8dE1LZB`6+aCGU+f;KjQ7+g+5]M?Dl;e1gfFX@lO+4A+iCG`hj67D+QAEgal2[jgAMLHWghCNB74hWIBoX+;C1Z5RIB7+fIcgk=XAPHOeENe7WBd@SRga]++D9@bJ?\\0a1`a9ih2+8ga+WZoe`+L:mL?ea++TIhWZBSoSY4=5NXKh3[CH0?E9^de=oCCMik2SYZ7gBL[BPXB@4\\eh3_=[R[ELYPVb+", "I approached the witness stand with a warm and welcoming smile. This, of course, belied my true intent, which was to destroy the woman who sat there with her eyes fixed on me. Claire Welton had just identified my client as the man who had forced her out of her Mercedes at gunpoint on Christmas Eve last year. She said he was the one who then shoved her to the ground before taking off with the car, her purse and all the shopping bags she  (excerpt of The Gods of Guilt by Michael Connelly)", "http://zodiackillersite.com/viewtopic.php?p=34557#p34557", true),
		new Cipher("Smokie treats: full replacement of z340's suspected wildcards with unique symbols", "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopOaVhMWqArstuvGFweHxyXz0CfP12m3q45QK67I89!J#$wCG%FX&eQ(J)DPZVW6*f+Y,Pol-OHcqMKVOPrg5WX.1Rbq/:M2Qdm;<=pCP>@lj[\\!f&qFoH]Gr^X_vd`{j&o|C#64BKZb}~<NmfXPdlFCrKe6N!mc�9��q\2sjh�RK6��lV,wCFO�RGg6P!<clH!4��j�-Lek!2(BDHop6&�Klcxq�fVX�Gcgm+OPClN�MLP(d��Fa�KhM��ha(qalIXsAN#Vh�K�JQadv4�1bX�e&(lDmZAR�JsqpXw[Kf,�", null, "http://zodiackillersite.com/viewtopic.php?p=35746#p35746"),
		new Cipher("Smokie treats: first 340 of 408, perfect cycles, no wildcards, Z's worked", "ABCDEDFGHIJKLMNOBPQRSTUVWXYAZabcdefgUhCiFVcjklgdmnopqDIGHXJrsABtKucvChwxygz01EZ2QMS3UaPcTmFVY4Rc5Zi6pqrWkNdauJIc3GbgTHBnjDXGHVzclwfAhKrC7vZcy2oEc5aYix0FBGImKM8LP1RqeWXVl7vJQynwEk24phrMYiAmK9NU01bSDZjgg!CnfurFkHwoPQRa2O30YzgIiXVnxTws4WqA!CBGQl1vQ5kJFhLp0utIey3m6THB2fEXop7MDAGHPtsCBGQRSNcWc9ZHu7laF!IBGqbYKX7v9jdc9J3cyQEeTUVM9zdsAHBi19n5ZGN!", null, "http://zodiackillersite.com/viewtopic.php?p=35926#p35926", true),
		new Cipher("Smokie treats: Wildcard n-gram Experiment 2a", "ABCDEDFGHIJKLMNLBOPQRSTUVAWCXYZaTbcdTeFfIghijkdTlmnopDAGHCJKqFBrKsatIeuvEdwxyMUzPOR0TXQh1lAYW2Va3gf4SpKk5NTUoJChsGZd0HBmiDFGHXwatucIeKKA6EYhMznOa3gWfvjCBGFlKQ7LVxkpbtIUE6MJPOmuQyz21eKVWfAlK8NT5jZRDXiddqCmcSKFxHunkPtYzLoyWwdIfAgmvsuq2EpCqFBGPM5OP3jJIeL0x19AbQSlroHBzcVCns6kDFGHt4qIBGPERNhMa8UH06OXAqCBGpZWKF6Q8iTh8J1aVPkbSTYt8wTqIHBfy8m3gGNq", null, "http://zodiackillersite.com/viewtopic.php?p=35952#p35952", true),
		new Cipher("Smokie treats: Wildcard n-gram Experiment 2b", "ABCDEDFGHIJKLMNLBOEPQRSTUAVCWXYZSabcSdFeIfghijcSklmnEEAGHCJKoEBpKqZrIdstucvwxMTyzOQ0SEPg1kAXV2UZ3fe4R5Kj6NSTnJCgqGYc0HBlhDFGHWvZrsbIdKKA7uXgMymOZ3fVetiCBGFkKP8LUwjEarITu7MJzOlsPxy21EKUVeAkK9NS6iYQDWhccoClbRKFwHsmjzrXyLnxVvcIeAfltqso2u5CoFBGzM6Oz3iJIdL0w1!AaPRkpnHBEbUCmq7jDFGHr4oIBGzuQNgMZ9TH07OWAoCBG5YVKE7P9hSg9J1ZUzjaRSXr9vSoEHBex9l3fGNo", null, "http://zodiackillersite.com/viewtopic.php?p=35952#p35952", true),
		new Cipher("Smokie treats: Experiment 3 ZZ Wildcard n-graphs", "ABCDEDFGHIJKLMNLBOPQRSTUVAWCXYZaTbcdTeFQIfghijdTklmnoDAGHCJKpFBQQqarIesQEdtuvMUwQORxTXygzkAYW0Va1f23SoKjQNTUnJCgqGZdxHBlhDFGHXtQrscIQKKA4EYQMQmOa1fW25iCBGFkKy6LVQjobrIUE4MQPOlsyvw0zeKVW2AkK7NT8iZRDXhdQpClcSKFuHQmjPrYwLnvWtdI2Afl5qsp0EoCQQBGPM8OP1iJIeLxuz9AbySk!nHBwcVCmq4jDFGQr3pIBQPERNgMaQQHx4OXApCBGoZWKF4y7hTg7JzaVPjbSTYr7tTpIHB2v7l1fGNQ", null, "http://zodiackillersite.com/viewtopic.php?p=35998#p35998", true),
		new Cipher("Smokie treats: Experiment 4 ZZ Wildcard n-graphs", "ABCDEDFGHIJKLMNLBOPQRRSTUAVCWXYZSabcSdFeIfghijcSklmnoDARRCJKpFBRRqZrIdstEcuvwMTxPOyzSWQg0kAXV1UZ2fe34oKjRRSTnJCgqGYczHBlhDFGHWuZrsbIdKKA5EXgMxmOZ2fRRtiCBGFkKQ6LUvjoarITE5MJPOlsQwx10dKUVeAkK7NS8iYRRWhccpClbRRFvHsmjPrXxLnwVucIeAfltqsp1EoCpFBGPM8OP2iJIRRzv09AaQ4k!nHBxbUCmq5jDFGHr3pIBGPERRgMZ7TRR5OWApCBGoYVKF5Q7hSg7J0ZUPja4SXr7uSpIHBew7l2fGNp", null, "http://zodiackillersite.com/viewtopic.php?p=36001#p36001", true),
		new Cipher("Smokie treats: Wildcard expansion tests 1", "ABCDEDFGHIJKLMNLBOPQRSTUVAWCXYZaTbcdTeFfIghijkdTlmnopDAGHCJKqFBrKsatIeuvEdwxyMUzPOR0TXQh1lAYW2Va3gf4SpKk5NTUoJChsGZd0HBmiDFGHXwatucIeKKA6EYhMznOa3gWfvjCBGFlKQ7LVxkpbtIUE6MJPOmuQyz21eKVWfAlK8NT5jZRDXiddqCmcSKFxHunkPtYzLoyWwdIfAgmvsuq2EpCqFBGPM5OP3jJIeL0x19AbQSlroHBzcVCns6kDFGHt4qIBGPERNhMa8UH06OXAqCBGpZWKF6Q8iTh8J1aVPkbSTYt8wTqIHBfy8m3gGNq", null, "http://zodiackillersite.com/viewtopic.php?p=37215#p37215", true),
		new Cipher("Smokie treats: Wildcard expansion tests 2", "ABCDEDFGHIJKLMNLBOEPQRSTUAVCWXYZSabcSdFPIefghicSjklmEEAGHCJKnEBPPoZpIdqPrcstuMTvPOQwSExfyjAXVzUZ0e12R3KiPNSTmJCfoGYcwHBkgDFGHWsPpqbIPKKA4rXPMPlOZ0eV15hCBGFjKxPLUPiEapITr4MP6OkqxuvzyEKUV1AjK7NS8hYQDWgcPnCkbRKFtHPli6pXvLmuVscI1Aek5oqnzr3CPPBG6M8O60hJIdLwtyPAaxRj9mHBEbUClo4iDFGPp2nIBP6rQNfMZPPHw4OWAnCBG3YVKE4x7gSf7JyZU6iaRSXp7sSnEHB1u7k0eGNP", null, "http://zodiackillersite.com/viewtopic.php?p=37215#p37215", true),
		new Cipher("Smokie treats: Wildcard expansion tests 3", "ABCDEDFGHIJKLMNLBOPQRSTUVAWCXYZaTbcdTeFfIghijkdTlmnopqAGHCJKrsBtuvawIexyzd012MU34OR5T67h8lAYW9Va!g#$S%Kk&NTUoJChvGZd5HBmiDFGHX0(wxcI)KKA*zY+M,nOa!gW#-jCBGFlK7.LV/k:bwIUz*M;<Omx72398=KVW#AlK>NT@jZRDXid[rCmcSKF1H\\nk<wY3Lo2W0dI#Agm-vxr9z%C]^BG<M@O<!jJIeL518_Ab7Sl`oHB{cVCnv*kDFG|w$rIB}<zRNhMa~�H5*OXArCBG%ZWK�*7>iTh>J8aV<kbSTYw>0Tr�HB#2>m!gGN�", null, "http://zodiackillersite.com/viewtopic.php?p=37215#p37215", true),
		new Cipher("Smokie treats: Wildcard expansion tests 4", "ABCDEDFGHIJKLMNLBOEPQRSTUAVCWXYZSaQbScFPIdefghbSijklEEAGHCJKmEBPPnZoIcpPqbrstMTuPOvwSExeyiAXVzUZ0d12R3KhPNSTlJCenGYbwHBjfDFGHWrPop4IPKKA5qXPMPkOZ0QV16gCBGFiKxPLUPhEaoIQq5MP7OjpxtuzQEKUV1AiK8NS9gYvDWfbPmQj4RKFsHPkh7oQuLltVrbI1Adj6npmzq3CPPBQ7M9O70gJIcLwsyPAaxRiQlHBE4UCkn5hDFGPo2mIBP7qvQeMZPPHw5OWAmCBG3YVKE5x8fSe8JQZU7QaRSXo8rSmEHB1t8j0dGNP", null, "http://zodiackillersite.com/viewtopic.php?p=37215#p37215", true),
		new Cipher("Smokie treats: Wildcard expansion tests 5", "ABCDEDFGHIJKLMNLBOPQRSTUVAWCXYZaTbcdTeFfIghijkdTlmnopqAGHCJKrsBtuvawIexyzd012MU34O56T78h9lAYW!Va#g$%S&Kk(NTUoJChvGZd6HBmiDFGHX0)wx*I+KKA,zY-M.nOa#/W$:jCBGFlK8;LV<k=bwI>z,M@[Omx823!\\]KVW$AlK^NT_jZ5DXid`r{m*SKF1H|nk[w}3Lo2W0dI$Agm:vxr!z&C~�B�[M_O[#jJIeL619�Ab8Sl�oHB�*VCnv,kDFG�w%rIB�[z5�hMa��H6,OXArCBG&ZWK�,8^iTh^J�aV[�bSTYw^0Tr�HB$2^m#gGN�", null, "http://zodiackillersite.com/viewtopic.php?p=37215#p37215", true),
		new Cipher("Smokie treats: Wildcard expansion tests 6", "ABACDCABBAEFGHIGBJKLMNOPQARAPPSTOUVWOEAXAPTYZaWOEbcdefABBAEFghBijkTlAEmnoWpZZHPqrJUsOtuTvEAPRcQTwPxyNEFazIOPdEATkBSWsBBbYCABBPp0lmcA1FFA2oP3H4cJTw5RxcZABBAEFu6GQ7a8UlA9o2H!#JbmuZqc$%FQRxAEF&IOZZSUCPYW(g)bcNFAZB*ca#l+qGdZRpWAxAPbckmgcoEA,-B.#HZJ#wZEAEGsZv/AUuNE:dBB;cQAck2aCAB<lygAB=#oU>THT@[Bs2JPAgABBESRF\2u&YOT&E]TQ#^UNOPl&pOg_BBxZ&bwPBI`", null, "http://zodiackillersite.com/viewtopic.php?p=37215#p37215", true),
		new Cipher("Smokie treats: hillclimber results for the Purple H experiment, message with only perfect cycles", "ABCDEFGHIJKLMNOPQRSTUVEWXYLQZabcdefghijklPmnopHPFKqXrOstVcQRBZuvwhixOyz01Q23teJPYzGNMkUxbjpmal2xQAB4DEonTIFWLMHC5tVfv6cXxhg0rsuPqwPNO7BAySf5zVKPU1TDAQ64bcPreJCQ0GWZY8kSuiNjmaHXdU4EDtZKpAlLMghPonFEDPJ1YLAPkwGOy5aQlTnDBCAMo1WIFHELbVPQJQY2v6cixhgzrsumefKQwSON7GXQyt8kd5ZPlRM60UV7REgzbcdPQPrhvujNimwPy4C506S9BpXZaoFOfgsiUPJnYLDPkQlT1At4DMoGWIF!", null, "http://zodiackillersite.com/viewtopic.php?p=38800#p38800", true),

		new Cipher("Smokie treats: Perfect cycles + 3 high count 1:1 substitutes + 4 medium-high count wildcards/polyalphabetic symbols: smokie.txt (before masking)", "041D,%(!9=:AN).-8\"1J@B,T3KA8G?MO'2$/X3FU\\-GL_R!-%:#3W.&HBO8\"4G]<CX3+.P6IV8Z;H2=-K6()NU@+MFRG?\\Z+8041D,_LJ9%TAN!1YHB$<^O3+X/IW&]-#C-).>40P1$Y6B:-@VJD08^1MO-W2=18I(TGK5U1]3)FG?!3'@1,DHG:R0\\AN/X-_L%,D-=VKA0-UC(.PY?8\\JLD410N_VT9%!,AMB-8=8KZ<^O3+X/6W&]G2$:8C1.)>(38PH5U'YG-\\\"N^I@B>\",/6MO'-8-WX<]F)3GC-P11YI^1*4R3G?_%.$/&3@-=LKAD-U8\\JV0H1DN_(T9%7", "purplehazeallinmybrainlatelythingsdontseemthesameactinfunnybutIdontknowwhyscusemewhileIkisstheskypurplehazeallarounddontknowifImcomingupordownamIhappyorinmiserywhateveritisthatgirlputaspellonmehelpmehelpmeohnoohyeahpurplehazeallinmyeyesdontknowifitsdayornightyouvegotmeblowingblowingmymindisittomorroworjusttheendoftimehelpmeyeahpurplehazex", "http://www.zodiackillersite.com/viewtopic.php?f=81&t=2617", true),
		new Cipher("Smokie treats: Perfect cycles + 3 high count 1:1 substitutes + 4 medium-high count wildcards/polyalphabetic symbols: smokie.txt (after masking)", "041D,%(!9=:AN).-8\"1J@Q,T3KA8G?MO'2E/X3FU\\-GL_R!-%:#3W.&HBO8\"4G]<CX[+.E6IV8Z;H2=-K6()NU@+MFRS?\\Z+8EQ1E,_LJ9%TQN!EYHS$<^O3EX/IWE]-#C-E.>40P1$Y6B:-@VJD08^1MOEWQ=18IETGK5U1]3)FGS!3'@1,DHG:R0\\AQQX-_L%SD-=VKS0-UC(.PY?8[JLD4Q0N_VT9E!,AMB-E=8KZ<EOQ+X[6W&]G2$:8C1.)>(38PH5E'YG-\\\"N^I@B>\",/6MS'-E-WX<]F)3QCQPS1YI^[*4R3G?_%.$/&3@-ELKAD-UE\\JV0HEDNS(TE%7", "purplehazeallinmybrainlatelythingsdontseemthesameactinfunnybutIdontknowwhyscusemewhileIkisstheskypurplehazeallarounddontknowifImcomingupordownamIhappyorinmiserywhateveritisthatgirlputaspellonmehelpmehelpmeohnoohyeahpurplehazeallinmyeyesdontknowifitsdayornightyouvegotmeblowingblowingmymindisittomorroworjusttheendoftimehelpmeyeahpurplehazex", "http://www.zodiackillersite.com/viewtopic.php?f=81&t=2617", true),
		new Cipher("Smokie treats: Mixed cycles + 3 medium-high count wildcards/polyalphabetic symbols: smokie2.txt (before masking)", "ABCDEFGHIJKLMNOPQRSDGTLUAVGWXKYZaHJBESbIcdJefANSghLJDOFGiHUFLjkASKJDGLQlmYnoETpqCBRXIOYrstuGUFQENeLSbWTgvcNGdCBfsKOULwHJDIYSKAQxHBAJSEmHJjOhIYQETSOGXJHSkbqcDYyZHJKEFLCBlSDXJzGbIt0OpiYNSAJKcRC1GTnG2SUeNE3QWfOYTFH4LX5ENJDOUG1bABSLjMTGLIoHJKcdDAQgsGFUYwaNCmq6BEvHISKLG7c0JuAlRFLSDGWOYTE1LQGfOBJYbSPyLtKc1GjDCUFUH4LXJ3IQGUbVLNe2EuiOTScZUGJPBILF", "InaholeinthegroundtherelivedahobbitNotanastydirtywetholefilledwiththeendsofwormsandanoozysmellnoryetadrybaresandyholewithnothinginittositdownonortoeatitwasahobbitholeandthatmeanscomfortIthadaperfectlyrounddoorlikeaportholepaintedgreenwithashinyyellowbrassknobintheexactmiddleThedooropenedontoatubeshapedhalllikeatunnelaverycomfortabletunnel", "http://www.zodiackillersite.com/viewtopic.php?f=81&t=2617", true),
		new Cipher("Smokie treats: Mixed cycles + 3 medium-high count wildcards/polyalphabetic symbols: smokie2.txt (after masking)", "ABCDEFGHIJKLMNOPQRSDGTLUAVGWXKYZaHbBEbcIdeJfgANShiLJFOjGkHUbblmASKJbGLQnoYpqETrsbBRXIObtuvwGbjQENfLScWThxdNGeCBguKOULybJDIYJzAQ0HBAJSEoHJlOiIYQETbOGXJASmcsdDY1ZHbKEjLCbnSDXbrGcIv2OrkYNSAJKbRC3LTpG4SUfNE5QWgbYTjH6LX7ENbDOUG3XABSLlMTGFIqFzKdeDAQhuGjFYyaNCos8BExHISFLG9X2JwAnRjbSbbWzYTE7bQGgOBJYcSPZLvbd3GlDCUjUH6bbJ5IQGjcVLNf4E!kYTSdZUGJPBILj", "InaholeinthegroundtherelivedahobbitNotanastydirtywetholefilledwiththeendsofwormsandanoozysmellnoryetadrybaresandyholewithnothinginittositdownonortoeatitwasahobbitholeandthatmeanscomfortIthadaperfectlyrounddoorlikeaportholepaintedgreenwithashinyyellowbrassknobintheexactmiddleThedooropenedontoatubeshapedhalllikeatunnelaverycomfortabletunnel", "http://www.zodiackillersite.com/viewtopic.php?f=81&t=2617", true),
		new Cipher("Smokie treats: Mixed cycles + 1 medium count 1:1 substitute + some small diverse interruptions: smokie3.txt", "O@E<9*P3FXQ+4!RS5,%LG[AM-&=YO.\">PH<BI\\C#Q?E]FR'*=+ON,&>XP$T6-QJ8(.L3!R\"Z*DG@HYO%9U+1MI[PVE%,W\\-.AQ:]SFT[*BU^@;?=9@+V,NR#C)>8Z-O0GLHD.WIS\\E^*$MPT4+]PN,(9KR]'!AL\"C2-0MF@?:15VVG)#N<%LH[C%XWS6.T3IM$]!D(\"%)E@*A@#^BESC+&,OP!N;=^%-V4.Q!@*'F>HL@\"N<9D2RIAS6+(ENQ#?P8F_)0S5-T3G]_L>HI<Q>;<.!Z:E?8A\"D(YD=9<*FU4+MRJ:),NOW4-L.#M+GB>^P9]+^,Q:BRW*$(H0.;16W", null, "http://www.zodiackillersite.com/viewtopic.php?f=81&t=2617", true),
		new Cipher("Smokie treats: Mixed cycles + 4 polyalphabetic symbols (cycle swap) + a few wildcards symbols: smokie4.txt", "S3)L*(1=!M+E.T4,UFN&5>926V8A1W3-9BS)L:GMH/U4*%?E'<5FXO+P6G[,(@-UC)\\HNQVE0@^\"IJL*4+CR;FAOM,#=9_-(W5)K8N$S*P[+L,9BIGQR-OP:HBC.T3C3FYQ*!A(RUGM+OV4,N-\\\"OW5)&#Q<H/'E2B\"'S6*C-\\,M-T3(JFC<!E)%D*\")!R%+0GN,$B)\\4$US-C.H>);B'N(\"Q*)C^5ALMFCEGW!P91BH0$E^KL8OAB+M9&FX?)GE>^]Y)1-S6!T\">?3\")I+N:R4()DB)@C5*!LUO@HV*@,PAN-=^C3\"S;3$)EFT%(+BU4+N,VGJ-M;P5\\8WDS3(@", null, "http://www.zodiackillersite.com/viewtopic.php?f=81&t=2617", true),
		new Cipher("Smokie treats: Polyalphabetic, one key for uneven rows and another key for even rows: smokie5.txt", "6>7<*=8?@9C0K+GL>QGOKC3.P[0[/.%_4L3.YD8W9UBIO+/ZEX4D!][^^[\"W:[^MXE_O9DV5-.JPQ*RW%+&!Y/P_C![.2ZQ_$/0ND\"0*OHZR!C7B\"?I/#@>1%][^^/$_P2Y[!WX[[,TB-V5*AHUWX3P7>^[\"VQ<&O+P#LQ[0[.+[,C$-WX*OV4!D1+W2[#X>$3-*%K]/$RS;8V5\"09O@W3,%-TXL#,0%T[1[/2YC0:ZP![(9+6\\7>?$,Q-%IOC8#&D+EN[LQF!MC^^1Y+64#[,=7?@-)\\8>?$OK%_P_?.^E9Q/[;[^?FGX17[-^HZB^C\"A*JQLC5.O?$3:[^^0,@", "ilikekillingpeoplebecauseitissomuchfunitismorefunthankillingwildgameintheforrestbecausemanisthemostdangerousanimalofalltokillsomethinggivesmethemostthrillingexperenceitisevenbetterthangettingyourrocksoffwithagirlthebestpartofitisthatwhenidieiwillbereborninparadiceandalltheihavekilledwillbecomemyslavesiwillnotgiveyoumynamebecauseyouwilltry", "http://www.zodiackillersite.com/viewtopic.php?f=81&t=2617", true),
		new Cipher("Smokie treats: Polyalphabetic, different keys for odd/even positions: smokie6.txt", "778;+<98?6D1L\"HM@#%$'VZN+3V4SPIA[(3/Z>9S:QBFN\"0[FT4WG;77>4D2]5?)1XC#:>Y-./KIP!RR&\"'YZO-A!?7PW..BHQX*\"@2!QE[N#=8C$:J0!7>UK<99@OHA,R36E123\\#TB.S4!CEUTY-N4=89?1\"^L-I.@(!:R7N,\\-=%$WT+JY.\">2\"VS8?1_JZQHK';OH/0]9T3X26O9Y,-&.PVM$JWE/3X4UU5VV]6!G5)6,3]4@7%#Q$&FN=9>LWOX*6(!$?)V>9W+,34W\\#<4@7.*]5>9%!'GA\"B_R:#\\-O:]77>@HT14\\$_E[C_=$A+&,(!ZT#_F[]99@UP_", "ilikekillingpeoplebecauseitissomuchfunitismorefunthankillingwildgameintheforrestbecausemanisthemostdangerousanimalofalltokillsomethinggivesmethemostthrillingexperenceitisevenbetterthangettingyourrocksoffwithagirlthebestpartofitisthatwhenidieiwillbereborninparadiceandalltheihavekilledwillbecomemyslavesiwillnotgiveyoumynamebecauseyouwilltry", "http://www.zodiackillersite.com/viewtopic.php?f=81&t=2617", true),
		new Cipher("Smokie treats: Polyalphabetic, 3 keys (interval parts): smokie7.txt", "#&@+02)59,<3DTJF*]%=O0&7.144?P,.F$-K5/L-,NK@/A55E::!8<GBABTT*AMLLJF2G;Z=HK^/7Q1*OT)HI6;CM8/<P2E/3H+0UK)NU<42MVD=OIMCLK?>NDMQ,9OF[JXB:5,AI-\"PXP=CBWVE@+->R%AW(*\\864L:N9RTU+U??[IS@$GVCT-R=]+C,4@J&>O)8?HBK&+V@AS/J3;L+T>Q<BA3:WAM7OD5#.P#XB!V<J1\\(?F=82LF6)N0FZR%H6'4M5EG>,PB0,\"QY2U;=KLQW:R-ET@Z;R=NGS8)-,310._7I^G2Z<0.=4>HK]1OY+99%14<@QC+?H)E6T3*", null, "http://www.zodiackillersite.com/viewtopic.php?f=81&t=2617", true),
		new Cipher("Smokie treats: Polyalphabetic, different keys for odd/even positions with added masking of the scheme: smokie8.txt", "RMKR!YDAI]L*?S@,NW7'9X+WS2,Z8P0TMB=-4\\0GL/R[<^_V(66D-NEP1D:X/@&:F>7@TL!/+YG=)%,3-4*>1R([QKO;0(A78B9N5S)ODD(&2H'LJ8P//0NT)@H.46=O#,*5$ZUIY\\Q_YVRN!FM'*DW\\6@V2,.3I3\"/W?DBX1K#/IT(M$=)PXDN80W:]?DCX/6+_3BQ^AWL)%F!2ZN6ZLQN*5\\^P!B=<A:BR$@8R2SL0H(BX-W>F;\\L^6B*O$FC2*O=_/NQPS!LM$':@1-D)&2-X0\\7-+$*A6@,WUL-2/RL!Q2WK+0,W?8BXM)QM/XSML;WO4-5V&*YR3@/BFE\\]", "ThreecompartmentsdividedthecofferInthefirstblazedpilesofgoldencoininthesecondwererangedbarsofunpolishedgoldwhichpossessednothingattractivesavetheirvalueinthethirdEdmondgraspedhandfulsofdiamondspearlsandrubieswhichastheyfellononeanothersoundedlikehailagainstglassAfterhavingtouchedfeltexaminedthesetreasuresEdmondrushedthroughthecavernslikea", "http://www.zodiackillersite.com/viewtopic.php?f=81&t=2617", true),
		new Cipher("Smokie treats: Partial columnar un-transposition: smokie9.txt", "+#(%5-DBW127!BUA;3[LK,!P+'BU>&HFY3D).EY!<$:@03'?2QIW3NTG_'3KR43)4ZJA.WUB[]&=;4G)^%P4T7\"!NJ9']Q+3ZWA2#3*;K3$=>XYB\\U4,:^J43#TP4->M5)2.7QTE%G%0Z(A>!D:.+BXSI3),E\\34K^\"TDWO33JY03YG%>2I+]3#U4Q)1U)ZJP](/KL\\)+BC96V_^W?BJW^8TZBKU$3X)%!BD>45WQT;O'B7!63@*#-UAPGB,'F'!RU4:EK)>LC^X0V1Y>Y3ZQP(GGW\"&-F33.%BMU32BHB<4U>A!UJ@K6+Q^BI=4T3D-!,%;><P_]Q9.57UW33\\O", "ilikekillingpeoplebecauseitissomuchfunitismorefunthankillingwildgameintheforrestbecausemanisthemostdangerousanimalofalltokillsomethinggivesmethemostthrillingexperenceitisevenbetterthangettingyourrocksoffwithagirlthebestpartofitisthatwhenidieiwillbereborninparadiceandalltheihavekilledwillbecomemyslavesiwillnotgiveyoumynamebecauseyouwilltry", "http://www.zodiackillersite.com/viewtopic.php?f=81&t=2617", true),
		new Cipher("Smokie treats: Partial columnar un-transposition: smokie10.txt", "W#(>54D)W427!BUA\"3/L3E:!+@))>&HFF^DU.E4!<$P@03S?2QIW3NT._'3^&-3)-Z4(.+BB[]&=;]ZU^%P1>7\"!NJ9@]QD3ZWA2#AC;KT$=3XYB\\U4X:^143<3P\\->65B2.7QTE3G%0Z((>!3:GDBXSI3U,E\\34K3\"TDWO3>JF02YGQ%KI+]D<)4QB1U)ZJP](/3L4BWUC9MV_K3R)43^8T.BKU=%X)3!BD>45WQQ;O'UN:63@V<Y)APGU,SF'!RU4:EK)%LC^X0VJY>43ZQP(.GW\"&YFT3Z%UMU32)HB#YU>A!B4@K6+Q3BI=4T3D-:,Q;><P_]Q9.57UW33\\O", null, "http://www.zodiackillersite.com/viewtopic.php?f=81&t=2617", true),
		new Cipher("Smokie treats: Partial columnar un-transposition including misalignments: smokie11.txt", "2)/%;+ISZ0\"?5^>E!K$-'1(@D=LQ+38.*H4C,MAV%YRU]<[!J:\")OT#N8$>(W;=:'J\"_-H?2E$0<I:QSL@+M7Z\\RGY[$V&]B-&T^U3[:(_'.FP@!45/8A2=&JCMEV<$N'H:,\"WQ3<;&$:RY\\-R$@BI8X]1?MHV,:T$)=S>K-9LDZ:UR_!8+'^#P=7JU3)&'$G9X:?N>/H4#27TO0L$KA1%-'9CB&(RDPGUE![,?;=8:<X4SQZHY1&P$O^L.]J0M[_':C6><$N-\"TR2ME\\K(P:@QN$M3V!4Y-XA&7,@'U8[G%N*D<(MHO:;D!).0&Q_1J#2/!L&XE\\.$&>C,8V:$I", null, "http://www.zodiackillersite.com/viewtopic.php?f=81&t=2617", true),
		new Cipher("Smokie treats: Partial columnar un-transposition including misalignments: smokie12.txt", ".5)G%&+=KS[(!H\":#U@2-?HLAJV_$55[[\"8_*1')5CG\\(F&3?SWT!:ZPEB4%LQZ2OJ1>T=XE*]Y6)N;#![@KQ1H<,U\\^:+V(\"@6WE?3H.4]_LCI$-GJA,VPT87;%QG)S[FB=Y*!9^GKU.#(\\36O9S:(X1<QG9$S[CR<L04N@_5ZJM%9T!WG[-5<(+&=<:,$7!2PA_@TY54IT.*B83Q.IG1C(Z(8:,S6[;:\\EPR3)TY'<_45\\6GHC5Q9O[L\"K_6117HK\";C/)KQ[)G\"$4C!3!\"FAJ?S[(\\T*&!?\"LIVV2A:_;:T_7?OP1YAGE^.@NQW[^<E)%$P-!+@#=F[:E_K<5", null, "http://www.zodiackillersite.com/viewtopic.php?f=81&t=2617", true),
		new Cipher("Smokie treats: Partial columnar un-transposition including misalignments: smokie13.txt", "/6.(!H$0%5&Y73M)@2N]\\5?U.#DI>Z/<J-4#LT!I^Z96%H[NA2Y/@W_)3]4$&U.CM'6\\<N^HY3%@OUVQ,D^.W!JGC9]4'HY3I%VAZ@Z.HU^^>/;&#>@AJW!Y:6)#<L7/?ZUS^1.%>[N_V]@+W.HUO$IZ7^/(%C5_\\#\\>)5F!@AA)QJ)IU4Y#I_T6NW2L]]8SX]&M%'/-N]<%D=@TV(IZ%W4!3F.J5V?]^>FU@#[/5CA>UM2IU\\OLTB5DTE&>%@UM^%9\\N]!XI7HF<4/J]I5KVYOT)NFP=UUACG!S&]7^D\"]Z%@^MAR4^J[TNV%H-*@./?2W&0U\\](^AT4%I&!H@G", null, "http://www.zodiackillersite.com/viewtopic.php?f=81&t=2617", true),
		new Cipher("Smokie treats: Partial columnar un-transposition including misalignments: smokie14.txt", ",\")0/AG-EY(8[O%*DU2!X=Q;-H\\3199!O.430/9HF3&%IG\\+B]M>$=!1#:[L<E3Q)/3J7VY-@_T0^3:GH2*9%I'=Z\\;.3][351QPIFZ]A31%I4U.4=,DK5X7)4,]X3G3AQOM6(EU34M)[YQJ9=QXR43$FA,13%C1WB=-@/^EE?QU1M3#50H+%T1F!3<4YG;2S3=E[3YVI')]O-1I9D]X]/\"%3#?C4;7A@J.FYU4\\%M]]1,=)TQ31]8WA]+G(3I]\\U1<<I]6%4[Y103=I@QQ-H\\3)D_+]##J\"-TT1*BW%_Y6UN9\\,=]JRZI>R&%N0#<9SH\"U\\.-341)E4OAF(3G0U", null, "http://www.zodiackillersite.com/viewtopic.php?f=81&t=2617", true),
		new Cipher("Smokie treats: Partial columnar un-transposition including misalignments: smokie15.txt", "U9X%P*J$NK>)H*R!1>7F@WY%^8HNa@M0[,C8I$RPDN-6N9H#W\\1:EZN2U7Nd(h=K@g>`8H]fabTY^$RRNIA3*J_P3,M$BRc%[-?WKEN\"N$PN>RidJ'Yg(DAB\\!=P/B?,`>&NZ[9(B$I%\"bF*gNJN%)-@/'0(R1KLRW>N#Y*$\\)AREB/S\"6Z72N?WN^,N$U[-,RW\\b9NN1&\"8$F8AR#(',@%W8%YJZ!^XNP^7C3?cc1N*\"NZ-^9:N'!g#b)QfHF/\\9U\"aTKY$@d>RBHZ3W%HC6LN[_^-KWa,B(@,BNgb$*8)/IDEC\"Kd9A>SU\"HRQ`(,IP>>@618WJ^Nb!&N$gFR6", "THERIBSANDTERRORSINTHEWHALEARCHEDOVERMEADISMALGLOOMWHILEALLGODSSUNLITWAVESROLLEDBYANDLIFTMEDEEPENINGDOWNTODOOMISAWTHEOPENINGMAWOFHELLWITHENDLESSPAINSANDSORROWSTHEREWHICHNONEBUTTHEYTHATFEELCANTELLOHIWASPLUNGINGTODESPAIRINBLACKDISTRESSICALLEDMYGODWHENICOULDSCARCEBELIEVEHIMMINEHEBOWEDHISEARTOMYCOMPLAINTSNOMORETHEWHALEDIDMECONFINEWITHSPEEDHEF", "http://www.zodiackillersite.com/viewtopic.php?f=81&t=2617", true),
		new Cipher("Smokie treats: Normal cipher with only 35 unique symbols: smokie16a.txt", ")+9*%*=;C)-'/8./+:\">#!31%92=B1A,3#(&3@)<9B,.08&3-2(?@*=;C)-'59+$'!,:=@<(>&A00:12\"8#?3B:,!-)1<(>,.B2$?@'%0A31!-9,?;.&!C+<A*=;CB.,82()@''94:1,><(%,AB2<(0=+;)-'86/:0>@#%92=184:-\"><2%0<(?@'82<)-'7.300A#*B.&&592(!'=0C<(:\">12/?0<A&)29B<(!25(%@=$)895=+;\":0>\".0-)@/?0!$9#%?-$!C+<(8=(?4:*);C>$59+;\"%#A,8,71C!4:B=5)+;@.2'94>7A3,7-?,%\"8#!31:7.35=C+<07", null, "http://www.zodiackillersite.com/viewtopic.php?f=81&t=2617", true),
		new Cipher("Smokie treats: Message randomly distributed over 55 different \"period 19\" lines: smokie17.txt", "5-WKdiKZVR@\"h&>Y1$HIA0)LegD4'3WfQ7)_?N8h[@Z!eH(M-0&<.!/GDQXCdc(@57U8_ib[@V.<gLcd3*NCK_Fi&/Q5AZ4V\\]'a85gR`[6LD.LW()&#D-880<?dXe7U_\"(>0IW/fhH@&K'cMC.^-AQL7bi/1*,)6?48e#hVH5\"$XK;0[?N<a23?AZ$W>!@)`LL4fLXfg@M^)20Q7\\hWDdiL(80H[*(!'@_.Lg?CdKZD\"i8)/([gP_8.h3Hf`-<L830c#Abe[7VH@diM_ag!]L543-0ZN8I&.[D7?hVY\"<d_@/I$;cQiLURC;a7-XV8KeP-g[fA./T@LI4Q,)(<1", null, "http://www.zodiackillersite.com/viewtopic.php?f=81&t=2617", true),
		new Cipher("Smokie treats: Bigram repeat period 19 emulation attempt without transposition: smokie18a.txt", "h,>05W<3haWIRghfDK\\P0\"?b<5R39RYIS(RGIdWIIR@05W+WRM1NA<5h0VW=h;T)ILR[W3h.]<5WQh3W[W3\"gW_h3WI&a5hI0Rfh[Ri3<5`I2h0W<5WR8JQ9RE*6W0WBh3hU,W><R05WDhXNWKI@R/\\!PY<5WbSWJhdC(=Ih:hRAO3W0<)3c5R`Ih^K;TRg.R305W385W3LG6WI<5PI5W3R*W3fR3HIEh\\\":3Wh0DWh<I+10<5W3Wh3W053WW?5ba5XWURYO<R05WI&BW3dh<i3h,(;R.WR@05WIW5WM3WI<>WIV)05hA`W8]9hNJW2:6hHh\\!^KU,I5PEhYC<5R", null, "http://www.zodiackillersite.com/viewtopic.php?f=81&t=2617", true),
		new Cipher("Smokie treats: Bigram repeat period 19 emulation attempt without transposition: smokie18b.txt", "@7DWX'Q>5/?[=Fg;%,\\f!$Z6:#c\"I*OV/iLJ`KT.[CF+<-a]=ZJ_;WGPQZhI@09,V/c)8N5\\Y!4e$gA')?>$%TZP\"-`JIX@.:*F5)LJN+#f[9gW]Q<hCO($/=dS78!eSPA@D_'(:c+G?;5a7T6V%*JKYi0W4-,I]Dg\\9f/`PR@LFR>hQ!6\"ZXCi.53,OY=;KcN:#8A0<e>IJ_'[+GfV4?\"*STN%LAd`dg\\$R>-PWF]@Q.aJ!:Xh\"85Ne+#A'?Z<6/GaT(CORW=Q4-[JS]>Kg!J\"P7i0c\\h*;:X8Ve#'ZN?`+DT.Z,W<@%f-O9I5_(]YR7gdPK936D_[Gid@0YQ4L", null, "http://www.zodiackillersite.com/viewtopic.php?f=81&t=2617", true),		
		new Cipher("Smokie treats: smokie18c.txt", "ABCDEFGHIJKLMNOPQRSTUVWRXYZabZcdJeMfLgFLLhiDjKkFhlmnPUopUWKJqSrTdJMsFaActXuKvIaFsKHVwKlAHKLfbxpdDZwpshmHGERLtAUFXYKMgyvJZz0nFDK1IaOCnFyGhUjKQq2BFediZfc3TgXxKRbFCpgreJLA4IhP5aKUGTaluhRdO6RS7MNcZHUxFagEKHbmnFdXYedjKah1KHwMa8LzqSV4HKqDQFAGdkfUXoKaFIHKDuaFKWxRJE2Fyhc5GhUYKLm1FHgOXfaqBecMcKZiDjFLKEF9aKLDCFdleUupPeKg!bAnCFt4BI8OS36eBnLETzqcrXEh", null, "http://www.zodiackillersite.com/viewtopic.php?p=43800#p43800", true),
		new Cipher("Smokie treats: cycles randomized from 2% to 30% step 2% from Row 6 down. One 1:1 substitute with count of 9", "ABCDEFGHIJKLMNOPQRSTUVWXYZabAcdeGfDghijkKlCmIOnFaLAoRZeJGpqrYKstucTHNOdvFwUBfbASExLyXhVGzCSR0iYcWK1mPIqVFgx2B3laEBLzRoOkj4etvYA5UTfwc0hPiC2FpXGqMLmGoRuE6OmnNWTdj7Y5XIBl1yQzzU4koDSTfVjSJ0PrcqHhXpmNwudS4CBFWBk2gCPjLZRAGNo3a2SYzMcKNBFnIefTBdoDEw7OhWPrLuCoKklGtI845PQYqHUm8TefhDKe3DcNv1CltWdwubwaEDFIxMLXOs14RoA0MYTckXLUge2GEmL2RK1gO0Fpuf5c3yr0", null, "http://www.zodiackillersite.com/viewtopic.php?p=39756#p39756", true),
		new Cipher("Smokie treats: some polyalphabetic symbols, wildcards to mask bigrams, key has some changes between halves of the cipher", "ABCDEFGHIJKLMNOPQRSTUVWXYZabGcBdWeACDfgJhiQOEjkLlmURnoKpYgqPFrdQsCthSuZLvrwxyzDEOKs01RboJP2HW3dFcUC4aS5AEpqKDPWeygu0dopfhesMNBsBR6uEIbF0QgJKoZOPSdtxocUCT2umhilLXexlAYEsdtPJdNBFzRsmILCj7ExCI0jKvgSP5eCtO5QAdsMhVC1elSFxuECswUbDJRsLgcIpWGehv5Lw4DaobeKJWTRnkCgLVw86CGdAYINxVkBxCyKSf0OFC7eCrsUEIDQorhZErPpbSdHwsBxA1B5CLRNjFKeQOKSPZgzdJ1pUtac7ABFr", null, "http://www.zodiackillersite.com/viewtopic.php?p=39804#p39804", true),
		new Cipher("Smokie treats: reversed suspected diagonal shift, increased cycle scores, top half cycle scores, period 0 bigram repeats, bottom half period 0 repeats, and decreased period 18 bigram repeats", "ABCDEFGHIJKLMNOPQRESTUVWXYUZabcdefTEghFSijOkkfSVlAmnEopGqrMHsEWScCdPtuqSlvwQKxyISz0J1ESCGyFW2MQ3JyDPYUVxrd4X5Pih6OHalcKUOPmSwVWSTRZl78cuQbqS9ejCPytho!S0d2lFiHSKmSWEpbqTo2iSC1xvBGYZTE9NydWPbhFCmKM0NxqaSzTyl7unofSRKxyThU5SCFOyRGexP09ahH0vSroT6LMg0u3BDHijO2SKhaslTdUWEGaeq4xPChNScLP3bSyFkTKfcSSfk3lvhIWnAN1UfEKYJQkbpkTtZWTd23hDqyAREJnljWr!KM5S", null, "http://www.zodiackillersite.com/viewtopic.php?p=41406#p41406", true),
		new Cipher("Smokie treats: manipulation of z340, horizontal or vertical untransposition, 340_323_17", "ABCDEFGHIBJKLMNOKMOBPQRNSTUUBVWXEYUZaGbbcdcefghGCKEWijklWSmJQEENanSbYopYBLEqarPQsjpBRJtuovwZCGMVbGQLBmxwZBVZpBBEyOVTUKuGuKBRiLhxwQsrBsuqlzSUeQfBfzCZBEyF0RNiIYBXjBON1BsBcrEqcLDI2UoYBD3NSuO4PA5UniLYhvUOk4s5SIfdxtob0yOJ6cLlzyX7Cb1BOqCbp4OFoopKYBJL5sbIcbPIq7OLOYcoag3Oo8g9ZBAPm30rTsz!3LdxVobyTg9x2P6c8Tq7XdYJeFYsJmmpBVas3QFxuZsWCwAgYR7cjoD!EI1B", null, "http://www.zodiackillersite.com/viewtopic.php?p=41604#p41604"),
		new Cipher("Smokie treats: manipulation of z340, horizontal or vertical untransposition, 340_324_16", "ABCDEFGHIJJCKLMNOPQRSTTUVUWXYZSabNJcdefLHghENNGRiHTLjkOClNmRnDEodkCFOpqjrsQaSPKTSElCghsQCKQkCCNtBKIJbquqbCFclZusEonCoqmSvHJWEXCXvaQCNtwxfGcyOCMdCBGzCoCUnFmUl0y1JjOC02GHqBNDA3JiclOZrJBe4o34yXVupjTxtBh5UlfvHM6aTzCBmaTk4BwjjtbOChl3oTyUTDym6BkBOUjRY2Bj7Y8QCADl2xnIov92lVuKjTtIg8u1D5U7Im6MVOhWwYohggkCKRo2EwuqQoOa0NwS!yChblPGBbLCasAYOF6Udj09NyzC", null, "http://www.zodiackillersite.com/viewtopic.php?p=41604#p41604"),
		new Cipher("Smokie treats: manipulation of z340, horizontal or vertical untransposition, 340_325_15", "ABCDEEFGFHIJKDLMNOPQRSTUVWXNNYCZUEabcdefNgChiXjQcekTlmlnoPLDOpEDXfeVdqPepPceeNrBpsaMmWoMekbfKtoXjhejmgtmUaHXIeIuLPeNrvwDubxdeyQeBYzejeFhSYFf0x1alde02YUmBkgA3aZbfdKnaBR4j3NiIGtqlEwrBW5FfSu4x6LEzeBgLEc4BvllUydeWf3jExFEixg6BrMdFlCJ2Bl7J8PeAicBwhsju92fGtplErsf2t1i5F7sg6yGdWHvV8WVVcepCj2XvtmPjJj0NvD!xeWMfOYBMTdLiXkYUsaaepTyNdLeeoAJdk6FQl09Nxze", null, "http://www.zodiackillersite.com/viewtopic.php?p=41604#p41604"),
		new Cipher("Smokie treats: manipulation of z340, horizontal or vertical untransposition, 340_326_14", "ABCDEFGHIJKKLCMGNOPQRSTKUCVWJXQYSZabYcdePfgOhNgJTSHFciShPYSSKjBhkaliRmPSZbTnoeJXVSXiUIelapJqSqrfPSKjstoiGuRSvQSBLwSXSxVgrbTyuzacRSy0LGiBELx1aMbTRndaBD2X1ZUA3omcNtjBI4xTErKWqfNwSBUfNY2Bscc2u5SIT1XNuxNWuU5BGvRxcC60Bc768PSAWjlRVkXr90T3ohcNjkYBtzW4x7kU5v3RIpsT0oHHYShCX0JsoiPXH8IKsg!uSIlTOLBlF6XyJZLGkaaShFvKRfRfWNNx3xpq6ngflKeSSgA6RZ5xQcy9KuwS", null, "http://www.zodiackillersite.com/viewtopic.php?p=41604#p41604"),
		new Cipher("Smokie treats: manipulation of z340, horizontal or vertical untransposition, 340_327_13", "ABCDEFGHCIJKLMNEOPQMRSTQUVPWXVKFEYZaNbWQNEEGcBWdZefgbfEOaFhiTKLIELfHRjQEkKlElmUQEGcnopTeZREqMEBrsELEtIifuvwvxZbREwyrufBVmaFZzaFRhSZBD0L12rt1ijbXocBp3tF2mOHA4XsEBHUXN0BnbbGJlUpF1LXvtXJvH5B0v5EbC6yBb768QEAJuqRtdLm9yF4iWbXcdceRIJ3t7dH5q4RpknNBoxYNEWCLyKnifQLFyiYnV!vEpeFPrBegY8pGOrudZZEWgqGRU6LwKXt4tkl6hVUeGTRUJXguYpKGGrCzuXAEEV26RO5tMbw9GvsE", null, "http://www.zodiackillersite.com/viewtopic.php?p=41604#p41604"),
		new Cipher("Smokie treats: manipulation of z340, untransposition, 340.322.down.righttoleft", "ABCDEFGHIJKLLMGNOPQRSTPMJUVWDXYZJSNXabIJccdeRfEgJQcXPZWhijklmlnnWHAbneoHRXXEMLebpqrsNfJKrGEgtHuXTJNKvMLJTEWnISWZAwxvByCBPcdIQzXJJKAIJAwWuBGJtGEwChTsfJPBp0DzXJAZ1jJjEkce1ftlJGJ2RQJraJNUsRXQBeRFYJNvc3UYTlu45G4qQcxhNTsoc5Oge1pTl6MQz0nvyCmjUzvvDQ4KnZuQJ2nZ7aKQ7uUgnlUnG5TMJNPTgOJA8i9vQFiHvlNQLdznvICmTF!1Gdt0FiDkMNma7ud9l6g3C8GbZwOiNf7lrvY!XU2J", null, "http://www.zodiackillersite.com/viewtopic.php?p=42438#p42438"),
		new Cipher("Smokie treats: manipulation of z340, untransposition, 340.321.down.righttoleft", "ABCDEFGHIJKLMNOCPQRNSTUVWXYZaaBVYbcdebfcBYghiSjkldmCjEnXYmmHoepTLYQnjcliqrsAJDJttiWuCtovWejjTBaonwxyzBpYZyVTL0WGjfYCZ1NaYfTitXbilQ23zR2iRcmHXd4jYYZQXYQRwGRVY0VT2NqfupYc5p6S4jYQl5sYsTAmoej0JYVY7edYyErCguG8dRoeUkYCzmMgkfJLo9V8xdm3qCfuvm9Pg45wfJKBd46tz1NDsEZzzSd8ZtlGdY7tlFcfdFGgLtJgtV9fBYCdaLPYQOrIzdUrWzJCUrH4tzXNDfU!5VH06SVnl2PrCpFJyzk!jg7Y", null, "http://www.zodiackillersite.com/viewtopic.php?p=42438#p42438"),
		new Cipher("Smokie treats: manipulation of z340, untransposition, 340.320.down.righttoleft", "ABCDEFGHIJKLMNOPQRSPFTUVLWXYZaEKbcdeEfgHKhDbijjSklmnlocGnSbpqrfstbduPsTuDbmmLvowgZhxPsnkryz0RXFXBBr1CSBv2hossgSjvu34i5Ewbi1KgZMhVsGbPe6rjbGgrBDcrkd67Cde3enmLDlAsbbidDbnJwVeKbMKg6EyGxwbvosNfAsbdkJ0b0gRmxV8MXbKb9olb1TbPpXZvlevoHtbPCmaptGQpA!K84lm7yPGx2m!0TiJ3GXYSlANBC5EFUnGCCfl8iBkVlb9BkPljlUVpZBXpBK!GSbPHzZQbdOzWClHzhCXLfKuk6QzPwUX1CtIsp9b", null, "http://www.zodiackillersite.com/viewtopic.php?p=42438#p42438"),
		new Cipher("Smokie treats: manipulation of z340, untransposition, 340.319.down.righttoleft", "ABCDEFGHIEJGKLDMANOGPQRSITUVWXQVBYZaMRbcdWFKefgahHiCjQklIVJPBmnnoBCpqHrYSqaBstukvfJwMMvbpPBiiWxryluzGavqhu0E1ZKRKOO2m3QOx4Jrvvlanxp5Mj6uyBmzVlfXJdvSBGCj5nBSluOPYuhC67BqUyjqiWPHNvBBmCPBxrvdjVBXVl6Q0Swyiwd8LkNvBChU1B1lZsKfxXKBVB9rHBzbBMSAsNHjxrIoBMGigso!1bm!V82Hi70MSw4iRcqSU5SKeaHNLOG3QhMHnGGkH8mOhdHB9OBMIEHcdsfOKsOV!SafWkVph6AEMycKzGoTvs9B", null, "http://www.zodiackillersite.com/viewtopic.php?p=42438#p42438"),
		new Cipher("Smokie treats: manipulation of z340, untransposition, 340.322.up.righttoleft", "ABCDEFGHIJKLMNOPQIRSTUOEVWXYZIabWcMdefPQJTgWQhTKDgVgGHiMSKiSZjVCeDklTTAgmlSnHgeoSnGFRYpVKLCgRbSTqOErisjZmtguvwDVxyujdMmgzs0W1eDTuNi1VKH2aKeZeo0ge3FeDix04bAR2efnYrer5BusYpHzZeaZ56OwVx4ekz7zkuIUgR2eelfUef6OceV57SU87nf6vTzqC4el3Z5MahH2VeDlTDSsyh0225Ccs9pt3x92kn7wQrBKEKSS7hfuD2F9UeuuIs045Meg8kg08VkCei!7A21nedfzOA5WZhUelccCZDZ9n6dQD4GK3T1X2ioe", null, "http://www.zodiackillersite.com/viewtopic.php?p=42438#p42438"),
		new Cipher("Smokie treats: manipulation of z340, untransposition, 340.321.up.righttoleft", "ABCDEFGHIJKLAMNIOPQRSTOUDVIOWDXYVZVabcPCXcCLdHeRYfHDDgVhiCjbVRkCjaliKmHXneVBNCDoFGpcBdLhqVrstYHuvrdQPwVxwyIzRYDr0czHXbhMXRLRkyVR1lOYcuy2NgB2RSjKpRp34rwK5bxLRML36FtHu5RfxmxfrAEVB2RRiSERS67ZRH37CE87jS6sDxoF5Ri1L3PMWb2HRYiDeCwvWy223eZw9mq1uY2fj7tOp4XGXCC7WS9Y2l9ERrrAwy53PRVrfVy8HfeRc!7g2zjR8SxFg3ILWERiZZeLYQ4eYGlabAUXnP0FTgL9j6QOY5aX1DzJ2ckR", null, "http://www.zodiackillersite.com/viewtopic.php?p=42438#p42438"),
		new Cipher("Smokie treats: manipulation of z340, untransposition, 340.320.up.righttoleft", "ABCDEFGHIJFKHLMJFINOPAQLPQRSTUCMIVHHWIXYQZOICaQZNbTcdTLeUIfgQHhijklYSRXmInopMTqrnSBPfIstuJvCMHnwPvTLAtxLCRCauICylCMPqOXgWfzCDZckCk01ntuzOsRCxR02ipTq3Cbc3sbn45IfzCCYD5CDsdVCT06Q576ZD2oHs263CYyR0AxKOzTCMYhiQtrKuzz0UVt8dmyHUzbZ6pFk1LjLQQ6KqMMzl85Cnn4tu30ACD8bIu7TbUCP96WzvZInDsiW0JRK5CYVVURC71UMjlNO4GLeAwiEMBfQH5ijTJ!cR4xg4WR8Z2BFM3NLyHv!zPaC", null, "http://www.zodiackillersite.com/viewtopic.php?p=42438#p42438"),
		new Cipher("Smokie treats: manipulation of z340, untransposition, 340.319.up.righttoleft", "ABCDEFGDFHIJKLMNOPPQARSFTCALUFTMAVWXJGYKAZaFPbcdBeJIHRfAghiMJjkgIlmSAnopNqLMPgrDqJsDZtGLHLUpALumLMDGEoaQZvLwTWlLlxygjCRCnHLtHxzciJj0Lopvneg12AZvLLSw2LeW0VLJx3F243TwzhPwnX0LSuHxEt5CvJLMnz3Fok5pvvxKVo6XfSbcveT3iOlyGdGFF3uPKMvm62Lgg1op0xE5jMeAp4JeKLD73QvqLw6wncQxNH52LSVVKTAgyKMdmBC18GYErcHL4ZFP2cdJN9WH1ta!MssLw!O8PANO5PGE1QH6TzsOM0BGuPq9vDUL", null, "http://www.zodiackillersite.com/viewtopic.php?p=42438#p42438"),
		new Cipher("Jarlve column randomization test jarlve_col2.txt", "[)H'S&%.7$9RGC=XAN)#KJ4UZ*,F@P:\"+7C1JSDQYN./[$ZQ5AF*'GQ.QR6T&WP,9XK@U<DJYA/%+O[*=15C:'(U6M<P72HR\"Z(S,O&CT$3WD%5GY:[K73F.A@='9&X+WI,I1#DY[/\"U2'ZT9F&6SL$LGX=4)/%(NKDPU3MR:.AY[+M1M#'4IJ5&DYN#FH*\"U2Q64I[S(2TZQN%O3(:H56@9$J*LW'#G4I7&<.@,O2A\"()F=Z)%K+LN17XS:$R\"GKH#/+JMZ*4.OCD1)YS>TW9P=M3[5X<Q6:,AN/<TWC%$5'37=#U@96H4PGKF+X)N#&1LSURCJ\"Z$W=Q)3.4NM", null, "http://www.zodiackillersite.com/viewtopic.php?p=43643#p43643", true),
		new Cipher("Jarlve manipulation of z340, added one random column at the end", "HER>pl^VPk|1LTG2d8Np+B(#O%DWY.<*Kf)FBy:cM+UZGW()L#zHJTSpp7^l8*V3pO++RK2c_9M+ztjd|5FP+&4k/+p8R^FlO-*dCkF>2D(c#5+Kq%;2UcXGV.zL|p(G2Jfj#O+_NYz+@L9jd<M+b+ZR2FBcyA64K/-zlUV+^J+Op7<FBy-YU+R/5tE|DYBpbTMKO_2<clRJ|*5T4M.+&BF.z69Sy#+N|5FBc(;8RylGFN^f524b.cV4t++cyBX1*:49CE>VUZ5-+M|c.3zBK(Op^.fMqG2ORcT+L16C<+FlWB|)L^++)WCzWcPOSHT/()p_|FkdW<7tB_YOB*-Cc+>MDHNpkSzZO8A|K;+D", null, "http://www.zodiackillersite.com/viewtopic.php?p=43645#p43645"),
		new Cipher("Jarlve transposition test for Mr Lowe", ",/\"\\=(YXHC?9ZG-OB'AIDX5>4N6U!@&8TTE)9H%='12Z(-ELYM\"CIP*<D7,/4GVH.PA?!&BU1D\\(2OKTIX:<@,56M.8'4:PG&9ZE<IL-Q75/>\"OMA!.)CB'34@V?X,9N\\=GN=6:*/%YM)ZA8N$!>U2%1L-POI(@V<7\"Y,\\TC>.K56)2I7VGA!XP<:9%DYZ@-?BK2\\7$&'6=O$8*2\"4.U)X>9V,H/\\%KGCZ&B-675)H':4AO1NXTVH9L*5\\!:TMDY/8$@\"Z5,&G-U>L1PADEOC!=B8@U?2,6IG%<HN=$'.3L\"X41A9KCZ(!D@3,Y-PGA:*7L23)BK3EL&3VK8H*U1", null, "http://www.zodiackillersite.com/viewtopic.php?p=43645#p43645", true),
		new Cipher("Jarlve transposition test for Mr Lowe, added one random column at the end", ",/\"\\=(YXHC?9ZG-OBI'AIDX5>4N6U!@&8TT*E)9H%='12Z(-ELYM\"BCIP*<D7,/4GVH.PA?Y!&BU1D\\(2OKTIX:<@Y,56M.8'4:PG&9ZE<I4L-Q75/>\"OMA!.)CB'534@V?X,9N\\=GN=6:*</%YM)ZA8N$!>U2%1L'-POI(@V<7\"Y,\\TC>.MK56)2I7VGA!XP<:9%&DYZ@-?BK2\\7$&'6=O/$8*2\"4.U)X>9V,H/\\O%KGCZ&B-675)H':4ADO1NXTVH9L*5\\!:TMD/Y/8$@\"Z5,&G-U>L1PXADEOC!=B8@U?2,6IGT%<HN=$'.3L\"X41A9K&CZ(!D@3,Y-PGA:*7L923)BK3EL&3VK8H*U16", null, "http://www.zodiackillersite.com/viewtopic.php?p=43645#p43645", true),
		new Cipher("Jarlve manipulation of z340 based on Mr Lowe's transposition idea", "H+M8|CV@K+l#2E.B)>EB+*5k.L-RR+4>f|pMR(UVFFz9z/JNbVM)|D>#Z3P>Ldl5||.UqLFHpOGp+2|<Ut*5cZG+kNl%WO&D(MVE5FV52+dp^D(+4(G++|TB4-R)WkVW)+k#2b^D4ct+cW<SPYLR/5J+JYM(+|TC7zk.#Kp+fZ+B.;+c+ztZ|<z28KjROp+8y.LWBO1*H_Rq#2pb&RB31c_8LKJ9^%OF7TBlXz6PYATfSMF;+B<MFG1BCOO|G)p+l2_cFKzF*K<SBK2BpzOUNyBO6N:(+H*;dy7t-cYAy29^4OFT-+pclddG+4Ucy5C^W(cN:^j*Xz6-<Sf9pl/C", null, "http://www.zodiackillersite.com/viewtopic.php?p=43674#p43674"),
		new Cipher("Jarlve test cipher, for each step move left by 2, move up by 1, and wrap around cipher", ":S?Y*4B1#AG)R=/ZWMU$,3'L+)9NG66@-FG=1K>VQ-&YXMVUC!*4#/BZ\\D+3K*':JNR8,T6.5?I4,W!)Y9>JQCAOLD1=8WO#/-6U$Q[3@*\"&:T1[\\?AFUR-!\"$SVJ9S4#OI.C/B[35DZ*ML649TQ,.'8N)\\,=?F\"@-A&L+:W[T#\"BCY[I1F?RUQM-\"$/@D'93NKC[&!*YD>M:4RUA+Z'IJK9)$>18N#C@\\Y=Z5.+M&W!/LJ'3K\"G)O5:N8R\\TY1>?MDB*A4$@V=+#W!&:Z/QUBOS3FJI8R*.L1T49?GVGWC$SK)#S/F3'*@!&=S:5B[OB4\\QJ8RV\"$>Z#NO@L+DU", null, "http://www.zodiackillersite.com/viewtopic.php?p=43726#p43726", true),
		new Cipher("Jarlve manipulation of z340, 20 by 17 grid, reading SW-SE (diagonal)", "HEBR(U>#Z3pOGp+l%WO&D^D(+4(GVW)+k#2bPYLR/5J+Jk.#Kp+fZ+B|<z28KjROp+1*H_Rq#2pb&RLKJ9^%OF7TBlXTfSMF;+B<MFG1BG)p+l2_cFKzF*K<2BpzOUNyBO6N:(+Hdy7t-cYAy29^4OFT-N:^j*Xz6-<Sf9pl/CpclddG+4Ucy5C^W(c+M8|CV@K+l#2E.B)>+*5k.L-RR+4>f|pMVFFz9z/JNbVM)|DP>Ldl5||.UqLFH2|<Ut*5cZG+kN(MVE5FV52+dp++|TB4-R)Wk^D4ct+cW<SYM(+|TC7z.;+c+ztZ8y.LWBOB31c_8z6PYACOO|SBK*;+", null, "http://www.zodiackillersite.com/viewtopic.php?p=41500#p41500"),
		new Cipher("Jarlve manipulation of z340, 19 by 18 grid, reading SN-WE (vertical)", ">)B.E2#l+K@VC|8M+HMp|f>4+RR-L.k5*+BED|)MVbNJ/z9zFFVU(RHFLqU.||5ldL>P3Z#>Nk+GZc5*tU<|2+pGOppd+25VF5EVM(D&OW%lkW)R-4BT|++G(4+(D^S<Wc+tc4D^b2#k+)WVz7CT|+(MYJ+J5/RLYPZtz+c+;.B+Zf+pK#.kOBWL.y8+pORjK82z<|8_c13BR&bp2#qR_H*1AYP6zXlBT7FO%^9JKL|OOCB1GFM<B+;FMSfTKBS<K*FzKFc_2l+p)G;*H+(:N6OByNUOzpB2+-TFO4^92yAYc-t7yd?C/lp9fS<-6zX*j^:N?c(W^C5ycU4+Gddlcp", null, "http://www.zodiackillersite.com/viewtopic.php?p=41500#p41500"),
		new Cipher("Jarlve 19 part message, possibly relating to the period 19 observation in the 340", "MT4F`RQCUG'hE5-_d0T+8<,3V1;?<cVVD1S><WAX\\H*&7iVO#C'1.`X?Oh$_M[5U&'G<96F1R*_]E)A<\"8Qc&\\I3#(6S;Y=hVVIDUK75'>Be.$]WMVFi;gVC?1-R_A&`[JUJG0\\*7'V9Y.V_&CMQEN8a+'\",d_#IHDFc3B/hVN?RA>(W/K\\0J<$7.MF+*41V9YX],JRiIY&HOKQ)gIV=#$<'`1<aA\\0G+J;76C1U4Y?VIT*eVd]E8N,D;_>VWhUi`)H&G<(V1KC=S.E-M8!'[_5\"/BFQ&6X#<;?0'6_ec$D]RgU-+S1&Q4,5>W*i'THKA`aG3hc<VVE[\"OdBC0+,", null, "http://www.zodiackillersite.com/viewtopic.php?p=41501#p41501", true),
		new Cipher("Jarlve manipulation of z340: 340_d1_n-e.txt", "NO+p8>kAMS|DzKHZ;kB-d_CWYc<O|7BFt*+WT+c/)P(WO)CSpzH)LFL1lR6WcCBT<|++M.Oq3pGz^2B.|Kfc(Vy4UB9ZXC51E-*>+:b+^.+fcl5VG24F4tNN(9|;S58yFR#Bz+c6JM2|.<*+c5&lTBR4F/YK5BOtpUEb+|TRDMzJFl+BUOyVp-+7-^<K+cdZy<RAM26+F4bB@J_LfN9jY(#zGO+2+V52.+UzKcLqX|%G#;C(lkpOF8->R*2^dDF|4+5kzF/tP_j+9d&M8+p*+pVR73K^p2lOSM(J+)BULyZ#:GzcWH+DKBWf(Y)#.NO<p%*H^LEVTRPG>k2p|dl1", null, "http://www.zodiackillersite.com/viewtopic.php?p=41580#p41580"),
		new Cipher("Jarlve manipulation of z340: based on mr. lowe's scheme", "H+M8|CV@K+l#2E.B)>EB+*5k.L-RR+4>f|pMR(UVFFz9z/JNbVM)|D>#Z3P>Ldl5||.UqLFHpOGp+2|<Ut*5cZG+kNl%WO&D(MVE5FV52+dp^D(+4(G++|TB4-R)WkVW)+k#2b^D4ct+cW<SPYLR/5J+JYM(+|TC7zk.#Kp+fZ+B.;+c+ztZ|<z28KjROp+8y.LWBO1*H_Rq#2pb&RB31c_8LKJ9^%OF7TBlXz6PYATfSMF;+B<MFG1BCOO|G)p+l2_cFKzF*K<SBK2BpzOUNyBO6N:(+H*;dy7t-cYAy29^4OFT-+pclddG+4Ucy5C^W(cN:^j*Xz6-<Sf9pl/C", null, "http://www.zodiackillersite.com/viewtopic.php?p=43674#p43674"),
		new Cipher("Jarlve/doranchak manipulation of z340: period 101, repeating 5-gram: 340_p101.txt", "H(B(E#F)R5zp>+6|pK9FlqSk^%ydV;#WP2+<kUN7|c|t1X5BLGF_TVBYG.cO2z(BdL;*N|8-p(RC+GlcB2G>(JFM#fNDOj^H%#fNDO5pW+2kY_4S.Nbz<Y.Z*zcOK+V8f@4A)Lt|B9+Kyd+;:<y+cMBM+X+b1U+*ZZ:GR4W29(FC)BELc>#yVzAUH6ZJ45SK-p-+pz|7lc^U.lV38+z*^BVJK3+(pOOOpp+7^+<.RFfKBM2yq_-G9U2M+R+Rcz/Tt5+jtLdE1||65DCFY<PB++pF&bl4TWkMB/K|pO)82LR<+^c+Fl)lRWOJC-|z**Wd5cCTPk4OFMS>.H2+TD&/", null, "http://www.zodiackillersite.com/viewtopic.php?p=43877#p43877"),

		new Cipher("Jarlve period 19 transposition, no filler, no interruptions: p19_t1", "Q5A#-2.O=E)@V%L8#7)P,$GUB3\\R@W(5YY49D3SO5)CMG\\:K[:.X7YBP<%-@V\"I'NU2IB\\=>(L\"Q0'OSAO>+.M9,<EAX\\'34RXXQKA;#7GI$NK58+:D*RUCYR7G%E=SP'@[@U\".)Y>#I<C>NM4QRO\"=W0V$8A9GCR[4P@#C(4-5>+27)E5[KRM<B=CB'QV*0;,$@[L93I+.ENW-+2,P8X@=W\"RJG5'-\\)KL;(Q*SO2:0A>MVD%Q$,.R=PYC@\\S(U:4W<S#@%7;NN#.Q\"IB8%+;[P9;LR-U723,GK@*W>NMQUB\\A$58R%XC-=>5*9('W.I>,AQ7YDEM0\"LO-)$G[0", null, "http://www.zodiackillersite.com/viewtopic.php?p=43806#p43806", true),
		new Cipher("Jarlve period 19 transposition, no filler, no interruptions: p19_t2", "9QT3H+.$X4,FCK2P5ITJD\\OH</L@W+R-!\"V7P&DH+36NG2E':XM/QI8.0&\"30KEYY,F)Y>,$F88PNJ46%:93D\"R.\\D!ELC72'T@1WA+V4LWYC1A$K)<XG5IF\\O&5PA6OH<Q?9!7>NQ3.9J8R1-,%'TOE:).PXL6EI/0\"2E4VRC,FGQ-W@0$<OJV&!+:\\\"O26>9)!NK15'>7A1Y4V:%ELG@AHPID<CJKI,!%%\"FY2C'%.?\\KL+G?@<9TN/&W3?$Q/)C8X2F5'A.P74:6\\)O7<D9W$?TV!HX1\",W\"$NPA6/J2'O:IRVQPIH0693+X7>,1F@?4,--N/T&.K8)J8G:EF", null, "http://www.zodiackillersite.com/viewtopic.php?p=43806#p43806", true),
		new Cipher("Jarlve period 19 transposition, no filler, no interruptions: p19_t3", "KN5%J?1.!$#730'#N[NBS=<V@\\!M=P:@*+0$=J?E6B.C&$:E19#\\%SE9E7I;?UP!C;3+V)K+%.YIS46=U*XP#%GV1O)0LQ57R:QJ!>KP;J/U?IX3&@?$L/R99+U?;KC3UF!FS[?.%C:VG6R#YM&1SA*A<#'BN#XQBS?0V/O7@A.?K*O$OBK[F\\I%6&&>M5+RVGE1[F?JQG;BE<X4/GR5XI=C3\\+A??>$BFLK)9=!4Q9:QN9U:NX3SABJ!Y3R37!$S5>#*\\O@\\[M4P&J'%*DYUY0'O/6XC)EI+L.<;)C'PI$1&/!UBP=;X4>033.*YNBB?SAJV7P+@R*'UEN/9[B>", null, "http://www.zodiackillersite.com/viewtopic.php?p=43806#p43806", true),
		new Cipher("Jarlve period 19 transposition, no filler, no interruptions: p19_t4", "24-\\D7K\\TLX*;*F$\\\"C>77C@=\"'NS0>>G\\''V-08*5S,/H8Y7&VE=Q5RV8W$G.%B.U0\"05L3$%7\"=AFYT2W>;#>[L8-GZ5#8Q\"=NF;Y5==\\XUV0C$%.QB%B\\4\"TKX'E>4MRF?&ZAT9D93REZ\"&&H.LN*J&UNZ8K4#L,QJ,GF0,-%ULWU,B92.&AMG2KM4T2GSKJJ[#ZFJ3'Y.Z0H\"WS?=;;E50MVHHR@2[=L#\"-M\\F/[>CX-MW'JSQVD1ACH4=U[K./D%EXB9R/??-B/RR?ENDQVYSD[M9R;/-A.%E;UW,NU*$@@73&N7#C'3AZ@@73/3CNVHG[A@*2*Q,X25J-H", null, "http://www.zodiackillersite.com/viewtopic.php?p=43806#p43806", true),
		new Cipher("Jarlve period 19 transposition, no filler, no interruptions: p19_t5", "/#SO9.BG1TH<'6<HF96AWV#GM-%Z07RB@J)[/@'\"-R\\WN3*M&)U/9N,<BPZ7\\0V:.*K*'VW&XH>-Q\"UU\"59DFTMB1*[\"@T6Q3=OA-0:#GA0SW>F;.\",+/P+P-AU;;=XOHDRZ3[)K)K6!#;WF/J&N+VQ+!Z=A9%QD:K0OT\\@H%'.7T06<5/MP&3*.UB,K=FU,K1JZSX+G:5>6DT\"&)'!-:6X>N#VWR/G+*O@D9H!3A5J%=;W\\KD=>,'FPRQ)N#:WQ=#/X+&<<U6VMZ;A0G-[0)S7@TP<.1%3[1FRM[>K',&BUN:*Z%7G5\\+F0T-.J7/=AW92*BV&U,OPZ!S[S3XH@", null, "http://www.zodiackillersite.com/viewtopic.php?p=43806#p43806", true),
		new Cipher("Jarlve period 19 transposition, no filler, no interruptions: p19_t6", "K*10S8$QE:4JC>V4*VK@Q&M-)D#7Z/\"T?J!PW@Y-A\"0<I2H.H.KZ[.MC:,7$.*FK0K@;D(12\"U!P*:4[RO5*I*JE>O1S@TC8T7:/$(Z<VYC\"MR4HQ,&7Y)<T24FS8O.NHU;GI!![(QPF;ON&?[I/@$54!K@)[CTJ7@A:DE>R5VJ/;DPNYIE?>1ZV#(W7@E$>:2?,AF#(U.\"M0S[Y@G-Z\"82.RMD5#P#1?CVU-YA$G)O<TSY%2.JT&Z<RID/G:M7(U\"W0:SHA$KV)Q-W;-2.,@M8ZGC7G5PF#?Y#[;\"WENAI/R0S>1D4,NQPTM7($JU><C!H!;21I!G;[8@N5F>.R", null, "http://www.zodiackillersite.com/viewtopic.php?p=43806#p43806", true),
		new Cipher("Jarlve period 19 transposition, no filler, no interruptions: p19_t7", "#$3MWKV69!KR\"\"A7ER91@@?%7AN+=?%OGDY0&X(;>,W6D#:HKA<MF\"5B.EYM2G[N!@H$OS);>19<2)0&7\"%-$Q0CW83-F1QV.SU%:7G8A'?H!'DY)E5O0X8&B>(W=;\"Y!F$M5#<N[VM9.UQC7S-;6C2QFD8X!KQE1U.3%$N78:YAO=>0#,!Q-G&KO6NCWC%S@(=EH,>[3,1G#D!3VK(9B56N:<20;G=Y@8R()5A=H-VF#1,SKOX0.&C3?(6W<2:A[D$$X)'YEGUH-0B;1S&OFR?R<>C'@[W'DEYN032:9'A5X8)X&V%2G-?8C,(W=)3;6!$:'.#DYX10.R[A9W(H", null, "http://www.zodiackillersite.com/viewtopic.php?p=43806#p43806", true),
		new Cipher("Jarlve period 19 transposition, no filler, no interruptions: p19_t8", "8189L,#D\\?NLI#O$JK3T*R+BW(H)YE[2.;DJBW=6XV7U9C\\T6C'?'!UI!O)(*-\"2,C>)WJG.YT$'<X91\\286@LU-8:P?7K;EV*+RI\\!,B3%CO1J-[H(@>=E+DLTWBKCGE8I:V\";.%<K+2R)GH@N6[X$7$YV:BOG,*1'=\"LUH3.W<9XD6;IK1%\"=>\\3$7,!2JVO*[\\E.CXT-?7(G;OP>@P.+LNW,#):J'TF%KUH-I6-3R8DX@D,!>7E=:).B'OHYU2WV3\"XON-719#R?##'+8!;[%#>L,<Y(9)?=.\"6P(J@EIT*#\\YB9C+:D7U13!$-X(?ON2N*HRK,D;J\\TP.<2Y", null, "http://www.zodiackillersite.com/viewtopic.php?p=43806#p43806", true),
		new Cipher("Jarlve period 19 transposition, no filler, no interruptions: p19_t9", "XH'2MIA2)Z3P?K[#4\\R8/V5W32U\"<9F&N-L!QP#4?X87IB\"IM9D%2>HZ/Z=&5X$[KXLA3QN-I\"XM.\\DR52+,<)JGIK%LP4/?WQ#=4F'MFVZ[\"78'\\89HUAR2G59!3?+7$J<#=K,'LPN)W[U.=F&34$A\\<8>!GQJ$&B5RM'>WU2VIG!BDA.%<-NJSVPK96?HDB4S)<,W8#V%[7&[L'9:Q>H-6+U=!MB5X/\\GX2K\"D7&LIV\\Q<MBP4)AV,%H.N8SV)\"9>A3R#\"M2S:-5NG%:7+%DK6=\\?J&FR-6LLI[9>$QWM'<#UP#23AF$/NS4!TJ/-G=KZN86L',W?&UZ::<S.!", null, "http://www.zodiackillersite.com/viewtopic.php?p=43806#p43806", true),
		new Cipher("Jarlve period 19 transposition, no filler, no interruptions: p19_t10", "!Y('RVWH%M=.!+@Y,\"7QO?,D41JC(F%$L\\#'W.H9(>9GMV5*E%NF>#+:FH@[!#?D0<&,S2)=$JY*R8Q%)O\"5'[WH4NHM7L&<.1?!93R:@QD8ECJ7V[(G=GEYN\"'$FJ01\\:<*7V4[,&LQ8)3$@=\"N.S?+RJ7>%[E20HY*VC525+D%1<W&:4LQ.,ML2!S?D@+OYQ9\\>5'W,4GL)8J973%1M$O\\R\"#/2:[=>@P.C!Y?'NWVD754<18>L.G/FHM*E(?6S0GD<3!2#)F&RE+$4G=*CL\"QR(<)4$J:YCN6,O\\/503>S.'6W@%*G?&O8,D=NRO+6\\47M&L.V[!/Y6)V/'1/", null, "http://www.zodiackillersite.com/viewtopic.php?p=43806#p43806", true),

		new Cipher("Smokie treats: smokie18d.txt", "ABCDEFGHIJKLMNOPPQRSTUVQDWXYZaRbZcdeLfFgLhNGWFiKMVjkPTEAGVlJIRmcLnXoFpOqrGWKUAplsFtUuKvIHlgwnxAgTaNAodeYDWSLyIGlTEKhfzUJM01BlDF1OtACkKBGXTxlPI2BFQguMjRm3qDEK3ZlzORrSnbO4IdN4plDTQ5VWhcbO63RyaPqaHDxKYfEltJezFgGWSbWKpa1l5udH0L0ORU75FITuKODgieGGEKtFA5KDWplFvxQnE2Fzhq4TMTWlbj1FHfIDwYOBcRXqKauGxlLFWKVtlgTCFLv3DWAuSKqrnIBBlr8BO9ARr6QCkLxc!IqmGEh", null, "http://zodiackillersite.com/viewtopic.php?p=43810#p43810", true),
		new Cipher("Smokie treats: smokie18e.txt", "ABCDEFGHAIJKLMNOOPQRSTUVDWXYZabcdefgDhiKcLMjWEkFlmnSOGioGUJIAQpPDIXqEHNQpDrETosFtiuTvimAEEKwdENcjaMoqfgiGiRDxNSFDiJLhyTIlz0SijE0NSNBCFCjXSrJON1BiVKvagb2eQDWEPZFSNh2Rdco3Afv3HJjGRYUEaeDN4PbplOQX5SiishrEudg6FKDWecEJEa0iivli7DzAbT8HEAjOJNGKkgSDiJYio5EjrHFJUWVZE1iCLQ9GLGWEcn0FuQADwSN6ebLQEaOjrJDFWFUiiKGyiDmPSEoMPEQ2IASBFx3CNzoQp4VCyDie7AQpDrf", null, "http://zodiackillersite.com/viewtopic.php?p=43811#p43811", true),
		new Cipher("Smokie treats: smokie21.txt", "ABCDAEFGHIFJKLMNOPQRSETUVHWXYZaTFbcdUeIQOfCTbATghiGjPklmnUdHopqIKrsUtVZASuiJCCvHbHaEFefSUwxQySGrzEBgBmD0Xi1M23bcq0hIIhVFUebHcSfd4Ck5bSO6NIMwiGgv7zKFKeYI0UsWORDZaTScutjIn82pmLsAq3GAoQtLbzONSUIrKkMV0u6vUJePkLhiIuLCHO3vgczglxXRSEKNXFyBDM3s0uVm9TXLAvqeQGOSCJHbnI62aMZcEoDu6z7NCvAKFqeUdrt9HcJQhxyOSzLAsECGgPFLua2eUKAukO0J7b23VUSHiEFpvQJePNmcxcnZ", null, "http://www.zodiackillersite.com/viewtopic.php?p=44230#p44230", true),
		new Cipher("Smokie treats: smokie23.txt", "ABCDEFGHGIJKLMNOPQRSTTUVWXRFRYLOZabGOcTRVdQTefETAghiTjkJKlHSmbZUTnchGjlAFEYLoDCKFpLVHYqkSWrUTZCEbGKQlRhstdukTobcjUVQqNOrvHlJwNxlVyFXkdluHZXlYWLJz0QFisxofhbXpGrTqXmPxncZjYoYZE1Ak2KDxlc33hRbgcUL4Go5PqUc4EhRzWbjdNTXSkUWQIGOHzAOkrYVRGobcqiH4XnLksJZ2KgT1NWroAM1ej2WOYEOEHVNDqebWlZU4iNUvcxAdQKHZ6eYr7E2TB0VblMX4wNGJPeuXRfOWAI0bVLGc4qEU3JdHZnToXAh", null, "http://zodiackillersite.com/viewtopic.php?p=44614#p44614", true),

		new Cipher("Smokie treats: reversed words in 'I like killing' message, transposed and encoded: smokie26.txt (before untransposition)", "ABCDEFGHIJKLMMNBJOPJQRSTUVWFXYZabcdeEfgCIhFijkLlbmJnDELopqYAGBdUErJIRTUWMsHtGsQquIKivEwxZNNyJiivNAzcTVeDUHkYUtY01aKaB2LFmoWPAEX3Ab4SfUIZ5RxxQhWTqmA2SjwoGUxWJr5F5DEnoh63PC7GQeOFP8MqJNuI5uBJohqSHMfCVyu1vUUqYGy78gxfjpoLAkMZvQOX567SNFRVscTwEuCYUB2WhrnGdKwJMhMiBxbIaDzPJ49tpH0v5rsT7E6KV8qjZofbUDHSMKIQGdc85hYTirENp6gbBJzIToGU8D9Hwde3vIuatFD0pOUM", null, "http://www.zodiackillersite.com/viewtopic.php?p=44879#p44879", true),
		new Cipher("Smokie treats: reversed words in 'I like killing' message, transposed and encoded: smokie26.txt (untransposed for solver)", "ABCDEFGHEEDCIEJKLJMNOPQIRSHNTUVWXYZaObSANAMcUdaYbGefghiTjEDklmnopqrhaHIWgsVMtuvbBVYeQBwiedVHLWSsVUZxOKupFayzErqHGr0Um1rvsjmTCFan2gLdKNVp3AhpZpfSSVvZFinjmDt2gLQGMtB45BgHxniB6ptMsSBRGPfShaAsgIFcOU7NkXvc0bU2gD8tHFPfgESVpRBscP40KSHNHSyWsipf8HIHzAjmDoMRGuKwIuinIXAlytP4lir1EeMWgp3PfTzMEmnk1mHoBdvQBw9qxB3yjxDj1HogKaL6ThOK77ZBqPipxYeXLRYK7j1T89cs", null, "http://www.zodiackillersite.com/viewtopic.php?p=44879#p44879", true),

		new Cipher("Mr Lowe 1, scytale interpretation of the 340", "ABCDEFGHIGJKLMNOPQRSOTUVWRTXCYZBRaCYbcdefSghcSBijBRkZBefgKlmnoBpCGqmYGmfrsmEEkUdtQccBmuiESUnWmFKvpnfwKxTYyQBOYMIaXRIsNOfz01fWktvRTyiEyF2O3MAc4v55keIB5VGv4B5Els6vThluuej7zutqRBMm5ktSnsrr8BxW2oQn01WSBsfbnYIwK6TY2yRDILcTSBDm9B90CpB8BBEyRmQaUoMmnj8O!9VgGC4Bs4fqkR4aXiwSs!FPVJpBAMZ83RFTTErR7mnbD84nBOojtI9nvaXEaOfBrSz0tc!YAKlpBxpCST8SBfEcpUGYYOw", null, "http://www.zodiackillersite.com/viewtopic.php?p=44117#p44117"),
		new Cipher("Mr Lowe 2, scytale interpretation of the 340", "ABCDEFGHIBJKLMNOKPQRJSSTBUVRWXFYZaRGbYcdefghijklQJmFSWnohRpqWLlYUrsthBLOQfrVuWOrgubaBAdvTKQBJLwRsIfsdIijOItkjCsvBOiCsTxOFrrEyIzlY0rsntOJefL1pqxdAw234LQ562O7XRwDEifLDIc2rQBDWNyZOr8N4IQBk9BONvBRBfo91py2mXzBzpCaBEtFnfGZKB84L5Y!XRoBRZijY!aBUaTBBEtOXh2KZ8J0Zr6!aCGMXsGXLBSQQrTQBLEiVodXR9TB8uP4971PySJXEENV3ysA2aVGssflfmzu5GCKE!MOBdX8Nyh22BXPkEQC", null, "http://www.zodiackillersite.com/viewtopic.php?p=44345#p44345"),
		new Cipher("Mr Lowe 3, scytale interpretation of the 340", "ABCDEFGHIJKLMNHOLPQDNBRSHTUVWMQSIXAOYZaEbKcdBDeBfHBPghDCBiGBjUEkLXlESmnKoUeIpqUSrHIIXBsqoUfYktIjlSBQiuSvVPwUxmWMEYLLNFVBWOtyz0v12ZeKByoFnJddeHk3YBBZD4UkbyjMyDr2LrOBVm1uwFz0WwxFOcyePjGRKZOBB5KWBZQk4BKQgQBLvJ64FmlBaKfUHTqCyABGBia7HpzOjurnFjJmljUXs5DvmP84Vhm4iFXHopsjSERPDjUBuKh9k3lqSr8DtbdSHxLqdC!XDJ!DKN9ydFFZ4RkL0MABXhNG2NOHHL7xe", null, "http://www.zodiackillersite.com/viewtopic.php?p=44631#p44631"),

		new Cipher("Smokie scytale alteration of the 340", "HER>pl^VPk|1LTG2dNp+B(#O%DWY.<*Kf)By:cM+UZGW()L#zHJSpp7^l8*V3pO++RK2_9M+ztjd|5FP+&4k/p8R^FlO-*dCkF>2D(#5+Kq%;2UcXGV.zL|(G2Jfj#O+_NYz+@L9d<M+b+ZR2FBcyA64K-zlUV+^J+Op7<FBy-U+R/5tE|DYBpbTMKO2<clRJ|*4T5M.+&BFz69Sy)+N|5FBc(;8RlGFN^f524b.cV4t++yBX1*:49CE>VUZ5-+|c.3zBK(Op^.fMqG2RcT+L12C<+FlWB|)L++)WCzWcPOSHT/()p|FkdW<7tB_YOB*-Cc>MDHNpkSzZO8A|K;+", null, "http://www.zodiackillersite.com/viewtopic.php?p=44351#p44351				"),
		new Cipher("Jarlve Period 19 scytale cipher with one null symbol inserted after transposition", "K[/HE@[B_G\\PSOQ2KI&?$4TMP0\\FZLB[1;\"WD0I!<STN_O=54XWDP'K1RFH4S/XUZ1-_!T33>\"QP<]*2YT&$X`WN8?\"*/F;&BAHK>\\O\"T13^\\_$W/+^JD.$S\\T4]GM'K@LEWB0=`QYZ?T2H$I]U&F_<;!D<I\"!+3SRX@+Z-K=FWO1;R3I4<STJ`00L@BN5/1D8P!*2;4_>VO_5Z\\Y`MW^QD[/K\\'\"<1!!OU/XS/J>/YENTR-\\3PK!1O!IY5/'A&O!<S.W\"\\G4XS___!?\"K_/]KAVW_`$<AT*T^;JN$O[L.\\2?!U&OMB!EJYUD&VF`=WZG\"]*I^KTVF&T/<5<SJS\\", null, "http://www.zodiackillersite.com/viewtopic.php?p=44360#p44360", true),

		new Cipher("Jarlve peak at 19 and 38", "XF.V!O1NGE4;TPZ4FKFJE:JDU@&I$,=-T:P!@HY5%JB>VCU5+N'$XT5I5;[QV3,G4>E:DWO@YB'[!.%$ZT1PQX)D+9W,&M\\;U=)!G#VP4!S3O[1CY-%H&SUNI:ZV>X'CZ2G2E<VIOQ=DMY->4B%+!ATAJ>3KF'[)#TX,DS9;UABVO!9H9JX<2$1Y%XVJB.:=DM5+K2YC)MQ#5<[.S)-\\11@4E$:AVOKTJ2&YWN@G.MIU)FBZ=F[!CAK!&>H-C;&ET.#'!$9U$<N.P%H3XCRQZ4,39SV+>W5[:GIJ'WQZP[E1OS&ZK,:41\\<PT!BH>F#<YCAHD;,@==E335FSNJK#", null, "http://www.zodiackillersite.com/viewtopic.php?p=44284#p44284", true),
		new Cipher("Jarlve Z340 with cycle-score-improving string reversals", "HER<7pO+J^+VUlz-K46AycBF2RZ+b+M<d9L@+zYN_+O#jfJ2G(|5T4M.+&BFz69Sp8R^FlO-*dCkF>2D(#5+Kq%;2UcXGV.zL*|JRlc<2OKMTbpBYD|Et5/R+U-yBF>pl^VPk|1LTG2dNp+B(#O%DWY.<*Kf)By:cM+UZGW()L#zHJSpp7^l8*V3pO++RK2_9M+ztjd|5FP+&4k/y#+N|5FBc(;8)|BWlF+<C61L+TcR2GqMf.^pO(KBz3.c|+-5ZUV>EC94:*1XBy+Y_Bt7<WdkF|p)(/THSOPcWzCW)++LRlGFN^f524b.cV4t+OB*-Cc>MDHNpkSzZO8A|K;+", null, "http://www.zodiackillersite.com/viewtopic.php?p=44310#p44310"),
		new Cipher("Jarlve smokie9 with cycle-score-improving string reversals", "+D!>A(Z0%G%ETQ7.2)5M>-4PT#34J^:,4U\\BYX>=$3K;*3#2AWZ3+Q]'9JN!\"7T4P%^)GBUA;3[LK,!P+'BU>&HFY3D).EY!<$:@03'?2QIW3NTG_'3KR43)4ZJA.#(%5-DBW127!4;=&][BUW:.+BXSI3),E\\34K^\"TDWO33JY03YG%>2I+]3#U4Q)1U)ZJP](/KL\\)+BC96V_^WF',BGPAU-#*@36!7B'O;TQW54>DB!%)X3$UKBZT8^WJB?'!RU4:EK>U4<BHB23UMB%.33F-&\"WGG(PQZ3Y>Y1V0X^CL>)A!UJ@K6+Q^BI=4T3D-!,%;><P_]Q9.57UW33\\O", null, "http://www.zodiackillersite.com/viewtopic.php?p=44310#p44310", true),
		new Cipher("Jarlve Z340 manipulation, left-most column shifted to right-most side", "dHER>pl^VPk|1LTG2)Np+B(#O%DWY.<*KfJBy:cM+UZGW()L#zH2Spp7^l8*V3pO++RK/_9M+ztjd|5FP+&4k(p8R^FlO-*dCkF>2D|#5+Kq%;2UcXGV.zL9(G2Jfj#O+_NYz+@LKd<M+b+ZR2FBcyA64--zlUV+^J+Op7<FByOU+R/5tE|DYBpbTMKF2<clRJ|*5T4M.+&BRz69Sy#+N|5FBc(;8+lGFN^f524b.cV4t++yBX1*:49CE>VUZ5-2|c.3zBK(Op^.fMqGLRcT+L16C<+FlWB|)p++)WCzWcPOSHT/()c|FkdW<7tB_YOB*-C+>MDHNpkSzZO8A|K;", null, "http://www.zodiackillersite.com/viewtopic.php?p=44660#p44660"),
		new Cipher("Jarlve period 18 transposition that looks like a period 19 message", "8OA68Z@FW,,XTIT&5C>UYU97S\\-%5$&8%>V#[N/\"/AFJ!.2R!O:$SE1'V6X\"S@AP<2/N,3?4>89TOUSN.ZWC87-.B8AH'<#2?,65\\EOL\"B[CY.P!KZR<XV%W[JK>'3S79?\\6\"VO6E'P.A:IZ#W\\UN!R@F2-,7'$%(X>69&?N8\\34/N3CBFP:OV2\"[<#Y5!RKVX&2%\\-YFE/USP,>.HS4'X5(9U>CK#8<Z&H>26,CPY5Y\"$&W77%-1@<ET5P!R2Y/AXJVZ@.:>&O19KIW%L7?E-F24J[H#J,$P9Z:JV'2C%5IC4@<A5\"F4H(VJ%-[4:!VNB/5K!RWXU\\>46H@AJS>", null, "http://www.zodiackillersite.com/viewtopic.php?p=44662#p44662", true),
		new Cipher("Jarlve test cipher with Mr lowe's railfence transposition", ",X;,(:\"?T%*YZR9:&B1J7G=)UK3)@\"O2A\\C;>,%BN-(26FZWEKP[4N)T&WJOG<A!Y@+8(T=Y58X=\\Z:-*<%W>7K6P13C;R!N@UFME?+381MA*,O9FGU[2(BP7\\)9C[&N;?@J+6Y5HB?6=G5TT$>1E7J@Y*RRW!:P\\CA<\"Z,KM2K(5:\\GU=X3+N<M14R>[H6E);.W*7%TN!><Z<KUCN\\4+!17M@F(5EC!3X18A?EB,\";&5>-\"M;RMX84->&9:,CXTUZ8K-BF[2A(W<J\\67:THEP4?HY24)=XJ9JUZ*-7F\\A,R.&(B9K2KO43F.&G@85H.T..[OW:PB!%69Z\\Y>R*X", null, "http://www.zodiackillersite.com/viewtopic.php?p=44790#p44790", true),
		new Cipher("Jarlve 11/8/2015 manipulation of z340, 340n", "HER>pl^VPk|1LTG2dNp+B(#O%DWY.<*Kf)By:cM+UZGW()L#zHJSpp7^l8*V3pO++RK2_9M+ztjd|5FP+&4k/p8R^FlO-*dCkF>2D(#5+Kq%;2UcXGV.zL|(G2Jfj#O+_NYz+@L9d<M+b+ZR2FBcyA64K-zlUV+^J+Op7<FBy-U+R/5tE|DYBpbTMKO2<clRJ|*5T4M.+&BFz69Sy#+N|5FBc(;8RlGFN^f524b.cV4t++yBX1*:49CE>VUZ5-+|c.3zBK(Op^.fMqG2RcT+L16C<+FlWB|)L++)WCzWcPOSHT/()p|FkdW<7tB_YOB*-Cc>MDHNpkSzZO8A|K;+", null, "http://www.zodiackillersite.com/viewtopic.php?p=43004#p43004"),
		new Cipher("Jarlve 11/8/2015 manipulation of z340, 340m", "d2GTL1|kPV^lp>REH)fK*<.YWD%O#(B+pNJHz#L)(WGZU+Mc:yB2KR++Op3V*8l^7ppS/k4&+PF5|djtz+M9_(D2>FkCd*-OlF^R8p|Lz.VGXcU2;%qK+5#9L@+zYN_+O#jfJ2G(K46AycBF2RZ+b+M<d-yBF<7pO+J^+VUlz-OKMTbpBYD|Et5/R+UFB&+.M4T5*|JRlc<2R8;(cBF5|N+#yS96z++t4Vc.b425f^NFGl+-5ZUV>EC94:*1XBy2GqMf.^pO(KBz3.c|L)|BWlF+<C61L+TcRp)(/THSOPcWzCW)++cC-*BOY_Bt7<WdkF|+;K|A8OZzSkpNHDM>", null, "http://www.zodiackillersite.com/viewtopic.php?p=43004#p43004"),
		new Cipher("Jarlve 11/8/2015 manipulation of z340, 340f", ">MDHNpkSzZO8A|K;+|FkdW<7tB_YOB*-Cc++)WCzWcPOSHT/()pRcT+L16C<+FlWB|)L|c.3zBK(Op^.fMqG2yBX1*:49CE>VUZ5-+lGFN^f524b.cV4t++z69Sy#+N|5FBc(;8R2<clRJ|*5T4M.+&BFU+R/5tE|DYBpbTMKO-zlUV+^J+Op7<FBy-d<M+b+ZR2FBcyA64K(G2Jfj#O+_NYz+@L9#5+Kq%;2UcXGV.zL|p8R^FlO-*dCkF>2D(_9M+ztjd|5FP+&4k/Spp7^l8*V3pO++RK2By:cM+UZGW()L#zHJNp+B(#O%DWY.<*Kf)HER>pl^VPk|1LTG2d", null, "http://www.zodiackillersite.com/viewtopic.php?p=43004#p43004"),
		new Cipher("Jarlve 11/8/2015 manipulation of z340, 340r", "+;K|A8OZzSkpNHDM>cC-*BOY_Bt7<WdkF|p)(/THSOPcWzCW)++L)|BWlF+<C61L+TcR2GqMf.^pO(KBz3.c|+-5ZUV>EC94:*1XBy++t4Vc.b425f^NFGlR8;(cBF5|N+#yS96zFB&+.M4T5*|JRlc<2OKMTbpBYD|Et5/R+U-yBF<7pO+J^+VUlz-K46AycBF2RZ+b+M<d9L@+zYN_+O#jfJ2G(|Lz.VGXcU2;%qK+5#(D2>FkCd*-OlF^R8p/k4&+PF5|djtz+M9_2KR++Op3V*8l^7ppSJHz#L)(WGZU+Mc:yB)fK*<.YWD%O#(B+pNd2GTL1|kPV^lp>REH", null, "http://www.zodiackillersite.com/viewtopic.php?p=43004#p43004"),
		new Cipher("Jarlve 11/8/2015 manipulation of z340, 340n_b19n", "H+M8|CV@K+l#2E.B)>EB+*5k.L-RR+4>f|pMR(UVFFz9z/JNbVM)|D>#Z3P>Ldl5||.UqLFHpOGp+2|<Ut*5cZG+kNl%WO&D(MVE5FV52+dp^D(+4(G++|TB4-R)WkVW)+k#2b^D4ct+cW<SPYLR/5J+JYM(+|TC7zk.#Kp+fZ+B.;+c+ztZ|<z28KjROp+8y.LWBO1*H_Rq#2pb&RB31c_8LKJ9^%OF7TBlXz6PYATfSMF;+B<MFG1BCOO|G)p+l2_cFKzF*K<SBK2BpzOUNyBO6N:(+H*;dy7t-cYAy29^4OFT-+N:^j*Xz6-<Sf9pl/CpclddG+4Ucy5C^W(c", null, "http://www.zodiackillersite.com/viewtopic.php?p=43004#p43004"),
		new Cipher("Jarlve 11/8/2015 manipulation of z340, 340n_b19m", ")B.E2#l+K@VC|8M+H|f>4+RR-L.k5*+BE>MVbNJ/z9zFFVU(RMpU.||5ldL>P3Z#>D|)c5*tU<|2+pGOpHFLqF5EVM(D&OW%lNk+GZT|++G(4+(D^pd+25VD^b2#k+)WVkW)R-4BJ+J5/RLYPS<Wc+tc4Zf+pK#.kz7CT|+(MYjK82z<|Ztz+c+;.B+qR_H*1OBWL.y8+pOR^9JKL8_c13BR&bp2#MSfTAYP6zXlBT7FO%p)G|OOCB1GFM<B+;FB2KBS<K*FzKFc_2l+d;*H+(:N6OByNUOzp+-TFO4^92yAYc-t7yC/lp9fS<-6zX*j^:Nc(W^C5ycU4+Gddlcp", null, "http://www.zodiackillersite.com/viewtopic.php?p=43004#p43004"),
		new Cipher("Jarlve 11/8/2015 manipulation of z340, 340n_b19f", "pclddG+4Ucy5C^W(cN:^j*Xz6-<Sf9pl/Cy7t-cYAy29^4OFT-+pzOUNyBO6N:(+H*;d+l2_cFKzF*K<SBK2BF;+B<MFG1BCOO|G)p%OF7TBlXz6PYATfSM#2pb&RB31c_8LKJ9^ROp+8y.LWBO1*H_Rq+B.;+c+ztZ|<z28KjYM(+|TC7zk.#Kp+fZ4ct+cW<SPYLR/5J+JB4-R)WkVW)+k#2b^DV52+dp^D(+4(G++|TZG+kNl%WO&D(MVE5FqLFHpOGp+2|<Ut*5c)|D>#Z3P>Ldl5||.UpMR(UVFFz9z/JNbVM>EB+*5k.L-RR+4>f|H+M8|CV@K+l#2E.B)", null, "http://www.zodiackillersite.com/viewtopic.php?p=43004#p43004"),
		new Cipher("Jarlve 11/8/2015 manipulation of z340, 340n_b19r", "c(W^C5ycU4+GddlcpC/lp9fS<-6zX*j^:N+-TFO4^92yAYc-t7yd;*H+(:N6OByNUOzpB2KBS<K*FzKFc_2l+p)G|OOCB1GFM<B+;FMSfTAYP6zXlBT7FO%^9JKL8_c13BR&bp2#qR_H*1OBWL.y8+pORjK82z<|Ztz+c+;.B+Zf+pK#.kz7CT|+(MYJ+J5/RLYPS<Wc+tc4D^b2#k+)WVkW)R-4BT|++G(4+(D^pd+25VF5EVM(D&OW%lNk+GZc5*tU<|2+pGOpHFLqU.||5ldL>P3Z#>D|)MVbNJ/z9zFFVU(RMp|f>4+RR-L.k5*+BE>)B.E2#l+K@VC|8M+H", null, "http://www.zodiackillersite.com/viewtopic.php?p=43004#p43004"),
		new Cipher("Jarlve 11/8/2015 manipulation of z340, 340m_b15n", "dEB+*5k.L(MVE5FV52c+ztZ2H+M8|CV@K<Ut*5cZG|TC7zG)pclddG+4dl5||.UqLcW<STfN:^j*Xz6-z/JNbVM)R)WkLKJy7t-cYAy-RR+4>f|p+dp1*HBpzOUNyBO+l#2E.B)+kN|<z2p+l2_cFKUcy5C^W(cFHk.#KSMF;+B<MF<Sf9pl/C|DPYLR/9^%OF7TB29^4OFT-+MVW)+k_Rq#2pb&R6N:(+H*;>^D(+4(8KjROp+8zF*K<SBKl%WO&Dp+fZ+B.;+G1BCOO|pOGp+2|5J+JYM(+lXz6PYA>#Z3P>L#2b^D4ct+B31c_8R(UVFFz9G++|TB4-y.LWBO", null, "http://www.zodiackillersite.com/viewtopic.php?p=43004#p43004"),
		new Cipher("Jarlve 11/8/2015 manipulation of z340, 340m_b15m", "5VF5EVM(L.k5*+BEd<K@VC|8M+H2Ztz+c2lcp)Gz7CT|GZc5*tU<WcLqU.||5ld4+GddbNJ/z-6zX*j^:NfTSAYc-t7yJKLkW)R)MVBH*1pd+p|f>4+RR-y+)B.E2#l+OByNUOzpycUKFc_2l+p2z<|Nk+;FMSK#.kHFc(W^C5LYPD|C/lp9fS<FM<BTFO4^92BT7FO%^9/RR&bp2#qR_k+)WVM+-K8(4+(D^>;*H+(:N6W%lKBS<K*Fz8+pORjOCB1G+;.B+Zf+pD&O+(MYJ+J5|2+pGOp|Ob2#L>P3Z#>AYP6zXlFVU(R8_c13B+tc4D^OBWL.y-4BT|++G9zF", null, "http://www.zodiackillersite.com/viewtopic.php?p=43004#p43004"),
		new Cipher("Jarlve 11/8/2015 manipulation of z340, 340m_b15f", "Fz9G++|TB4-y.LWBO^D4ct+B31c_8R(UVFlXz6PYA>#Z3P>L#2bO|pOGp+2|5J+JYM(+O&Dp+fZ+B.;+G1BCOjROp+8zF*K<SBKl%W6N:(+H*;>^D(+4(8K-+MVW)+k_Rq#2pb&RR/9^%OF7TB29^4OFTB<MF<Sf9pl/C|DPYL5C^W(cFHk.#KSMF;+kN|<z2p+l2_cFKUcypzOUNyBO+l#2E.B)+y-RR+4>f|p+dp1*HBVM)R)WkLKJy7t-cYASTfN:^j*Xz6-z/JNbddG+4dl5||.UqLcW<Ut*5cZG|TC7zG)pcl2c+ztZ2H+M8|CV@K<dEB+*5k.L(MVE5FV5", null, "http://www.zodiackillersite.com/viewtopic.php?p=43004#p43004"),
		new Cipher("Jarlve 11/8/2015 manipulation of z340, 340m_b15r", "OBWL.y-4BT|++G9zFFVU(R8_c13B+tc4D^b2#L>P3Z#>AYP6zXl+(MYJ+J5|2+pGOp|OOCB1G+;.B+Zf+pD&OW%lKBS<K*Fz8+pORjK8(4+(D^>;*H+(:N6R&bp2#qR_k+)WVM+-TFO4^92BT7FO%^9/RLYPD|C/lp9fS<FM<B+;FMSK#.kHFc(W^C5ycUKFc_2l+p2z<|Nk+)B.E2#l+OByNUOzpBH*1pd+p|f>4+RR-yAYc-t7yJKLkW)R)MVbNJ/z-6zX*j^:NfTS<WcLqU.||5ld4+Gddlcp)Gz7CT|GZc5*tU<K@VC|8M+H2Ztz+c25VF5EVM(L.k5*+BEd", null, "http://www.zodiackillersite.com/viewtopic.php?p=43004#p43004"),
		new Cipher("Jarlve 11/8/2015 manipulation of z340, 340f_b15n", ">;*H+(:N6R&bp2#qR_k+)WVM+-TFO4^92BT7FO%^9/RLYPD|C/lp9fS<FM<B+;FMSK#.kHFc(W^C5ycUKFc_2l+p2z<|Nk+)B.E2#l+OByNUOzpBH*1pd+p|f>4+RR-yAYc-t7yJKLkW)R)MVbNJ/z-6zX*j^:NfTS<WcLqU.||5ld4+Gddlcp)Gz7CT|GZc5*tU<K@VC|8M+H2Ztz+c25VF5EVM(L.k5*+BEdOBWL.y-4BT|++G9zFFVU(R8_c13B+tc4D^b2#L>P3Z#>AYP6zXl+(MYJ+J5|2+pGOp|OOCB1G+;.B+Zf+pD&OW%lKBS<K*Fz8+pORjK8(4+(D^", null, "http://www.zodiackillersite.com/viewtopic.php?p=43004#p43004"),
		new Cipher("Jarlve 11/8/2015 manipulation of z340, 340f_b15m", "Rq#2pb&R6N:(+H*;>B29^4OFT-+MVW)+k_l/C|DPYLR/9^%OF7T.#KSMF;+B<MF<Sf9p2_cFKUcy5C^W(cFHkl#2E.B)+kN|<z2p+lp+dp1*HBpzOUNyBO+Jy7t-cYAy-RR+4>f|z6-z/JNbVM)R)WkLK|.UqLcW<STfN:^j*XC7zG)pclddG+4dl5|M8|CV@K<Ut*5cZG|T(MVE5FV52c+ztZ2H+4-y.LWBOdEB+*5k.Lc_8R(UVFFz9G++|TBZ3P>L#2b^D4ct+B315J+JYM(+lXz6PYA>#.;+G1BCOO|pOGp+2|K<SBKl%WO&Dp+fZ+B^D(+4(8KjROp+8zF*", null, "http://www.zodiackillersite.com/viewtopic.php?p=43004#p43004"),
		new Cipher("Jarlve 11/8/2015 manipulation of z340, 340f_b15f", "*Fz8+pORjK8(4+(D^B+Zf+pD&OW%lKBS<K|2+pGOp|OOCB1G+;.#>AYP6zXl+(MYJ+J513B+tc4D^b2#L>P3ZBT|++G9zFFVU(R8_cL.k5*+BEdOBWL.y-4+H2Ztz+c25VF5EVM(T|GZc5*tU<K@VC|8M|5ld4+Gddlcp)Gz7CX*j^:NfTS<WcLqU.|KLkW)R)MVbNJ/z-6z|f>4+RR-yAYc-t7yJ+OByNUOzpBH*1pd+pl+p2z<|Nk+)B.E2#lkHFc(W^C5ycUKFc_2p9fS<FM<B+;FMSK#.T7FO%^9/RLYPD|C/l_k+)WVM+-TFO4^92B>;*H+(:N6R&bp2#qR", null, "http://www.zodiackillersite.com/viewtopic.php?p=43004#p43004"),
		new Cipher("Jarlve 11/8/2015 manipulation of z340, 340f_b15r", "^D(+4(8KjROp+8zF*K<SBKl%WO&Dp+fZ+B.;+G1BCOO|pOGp+2|5J+JYM(+lXz6PYA>#Z3P>L#2b^D4ct+B31c_8R(UVFFz9G++|TB4-y.LWBOdEB+*5k.L(MVE5FV52c+ztZ2H+M8|CV@K<Ut*5cZG|TC7zG)pclddG+4dl5||.UqLcW<STfN:^j*Xz6-z/JNbVM)R)WkLKJy7t-cYAy-RR+4>f|p+dp1*HBpzOUNyBO+l#2E.B)+kN|<z2p+l2_cFKUcy5C^W(cFHk.#KSMF;+B<MF<Sf9pl/C|DPYLR/9^%OF7TB29^4OFT-+MVW)+k_Rq#2pb&R6N:(+H*;>", null, "http://www.zodiackillersite.com/viewtopic.php?p=43004#p43004"),
		new Cipher("Jarlve 11/8/2015 manipulation of z340, 340r_b19n", "+-TFO4^92yAYc-t7yd;*H+(:N6OByNUOzpB2KBS<K*FzKFc_2l+p)G|OOCB1GFM<B+;FMSfTAYP6zXlBT7FO%^9JKL8_c13BR&bp2#qR_H*1OBWL.y8+pORjK82z<|Ztz+c+;.B+Zf+pK#.kz7CT|+(MYJ+J5/RLYPS<Wc+tc4D^b2#k+)WVkW)R-4BT|++G(4+(D^pd+25VF5EVM(D&OW%lNk+GZc5*tU<|2+pGOpHFLqU.||5ldL>P3Z#>D|)MVbNJ/z9zFFVU(RMp|f>4+RR-L.k5*+BE>)B.E2#l+K@VC|8M+Hc(W^C5ycU4+GddlcpC/lp9fS<-6zX*j^:N", null, "http://www.zodiackillersite.com/viewtopic.php?p=43004#p43004"),
		new Cipher("Jarlve 11/8/2015 manipulation of z340, 340r_b19m", "y7t-cYAy29^4OFT-+pzOUNyBO6N:(+H*;d+l2_cFKzF*K<SBK2BF;+B<MFG1BCOO|G)p%OF7TBlXz6PYATfSM#2pb&RB31c_8LKJ9^ROp+8y.LWBO1*H_Rq+B.;+c+ztZ|<z28KjYM(+|TC7zk.#Kp+fZ4ct+cW<SPYLR/5J+JB4-R)WkVW)+k#2b^DV52+dp^D(+4(G++|TZG+kNl%WO&D(MVE5FqLFHpOGp+2|<Ut*5c)|D>#Z3P>Ldl5||.UpMR(UVFFz9z/JNbVM>EB+*5k.L-RR+4>f|H+M8|CV@K+l#2E.B)pclddG+4Ucy5C^W(cN:^j*Xz6-<Sf9pl/C", null, "http://www.zodiackillersite.com/viewtopic.php?p=43004#p43004"),
		new Cipher("Jarlve 11/8/2015 manipulation of z340, 340r_b19f", "C/lp9fS<-6zX*j^:Nc(W^C5ycU4+Gddlcp)B.E2#l+K@VC|8M+H|f>4+RR-L.k5*+BE>MVbNJ/z9zFFVU(RMpU.||5ldL>P3Z#>D|)c5*tU<|2+pGOpHFLqF5EVM(D&OW%lNk+GZT|++G(4+(D^pd+25VD^b2#k+)WVkW)R-4BJ+J5/RLYPS<Wc+tc4Zf+pK#.kz7CT|+(MYjK82z<|Ztz+c+;.B+qR_H*1OBWL.y8+pOR^9JKL8_c13BR&bp2#MSfTAYP6zXlBT7FO%p)G|OOCB1GFM<B+;FB2KBS<K*FzKFc_2l+d;*H+(:N6OByNUOzp+-TFO4^92yAYc-t7y", null, "http://www.zodiackillersite.com/viewtopic.php?p=43004#p43004"),
		new Cipher("Jarlve 11/8/2015 manipulation of z340, 340r_b19r", "N:^j*Xz6-<Sf9pl/CpclddG+4Ucy5C^W(cH+M8|CV@K+l#2E.B)>EB+*5k.L-RR+4>f|pMR(UVFFz9z/JNbVM)|D>#Z3P>Ldl5||.UqLFHpOGp+2|<Ut*5cZG+kNl%WO&D(MVE5FV52+dp^D(+4(G++|TB4-R)WkVW)+k#2b^D4ct+cW<SPYLR/5J+JYM(+|TC7zk.#Kp+fZ+B.;+c+ztZ|<z28KjROp+8y.LWBO1*H_Rq#2pb&RB31c_8LKJ9^%OF7TBlXz6PYATfSMF;+B<MFG1BCOO|G)p+l2_cFKzF*K<SBK2BpzOUNyBO6N:(+H*;dy7t-cYAy29^4OFT-+", null, "http://www.zodiackillersite.com/viewtopic.php?p=43004#p43004		"),

		new Cipher("daikon test cipher 1", "9V[J%:;*S#K&_/$T'_A(]#4$B)$%a0M$+USP<$`#W,Q5L1a#Y-$&E2$$F#G39$.X:*N/#$_+VW#6J#=;^C,T0ZD$^#HI`#R7K#>Y?-P8L^A.^*QEJ4#a#@`#U1<9#K^B+^,RFL5#a#=`#S2:;#J$C#>#3<$D#[#9$PG#6$Q'H7RO#8M#:^A-^B?/$#X.@(P$*Z=)]C;%IE[#+]F#^D,A&a#VG'``_B(4K<)5L0>?1$%$26YH&QZC9'IEND3FJ@#7^A-(=#R[/8K)$#:%4L0T]G.>#_*P^1$B2U[H&$$'IE]#+[F#^C,(?#;D)b#35YA%6J<9B&b#C'7K:G/b#$D($)8L%HI", "SFTDASSOMEDAYITMAYHAPENTHATAVICTOMMUSTBEFOUNDIVETOTALITTLEIISTOFSOCIETYOFFENDERSWHOMIGHTWELLBEUNDERGROUNDWHOWOULDNEVERBEMISSEDWHOWOULDNEVERBEMISSEDTHEREISTHEPESTULENTUAINUTENCESWHOWHRITEFORAUTOTRATHSALLPEOPLEWHOHAVEFLABBYHANDSANDIRRITATINGLAUGHSAILCHILDRENWHOAREUTINDATESANDIMPLOREYOUWITHIMPLATTALITEOPLEWHOARESHAKEINTHANDSSHATEHANDSLIKETHATANDALL", "http://www.zodiackillersite.com/viewtopic.php?p=36966#p36966", true),
		new Cipher("daikon test cipher 2", "i76O<d6P01ZSAj6A[i8GH2I6QK=^gR6OAL634B]>CJ65TU96A?DG6P06k1Vn26M:WA6A@E;Sr<XA7Z[=FH63TU86A>BIjV_J?iQA`]R^GkOAZPAC64Np5[6Hm06k12DI]@KjA<9EYJ:hA66;LA_G6k3Sn4HAgFI=er>WA7Z8M5fANJ69KA0GH1[?BX6:`L^I6QAMYh2aACAd@_N6R];DZ3KeAf6OT4<JAq6PnALg=G5n0E6QUAmM71AhR6OH[>NiA?8WXA6A@F9V<jPA`]Q^IBAC6RJ:gA66;KA_G62D6OZ7iLA[=ME3S4n5F6P8s`9NA^H6k0Td6QjAI]1Jr>YA:Z", "path of the ecliptic passes through thirteen constellations the twelve traditional bodiac constellations plus ophimchus which interfects between scorpio and sagittarius twelve signs of bodiac are first aries second tamrus third gemini fourth cancer fifth leo sinth virgo seventh libra eighth scorpio addition alophimchus ninth sagittarius tenth capricorn eleventh atmarius twelfth pisces bodiac", "http://www.zodiackillersite.com/viewtopic.php?p=37034#p37034", true),
		new Cipher("daikon test cipher 3", "vKeO<]r4]GdgRq<aa@tHZ_LWAo1MiXBI[2r3+gCYNSDnE^T56P+JUF_K+7Xf+bV8cGm9+ZoAkde@a1q2+gBl3+CnQ5r6+Rm7[Dp8+O]4<EYLWFo9Mi^GIhNq@gO+hHSZnJ+1P+KTA_d+2Xf+bU3cBk5+CoDlHe64iYEgF^LVGn7Mi_ZQm8RAa<s9p@WSo1[P<BXNTCn2OiYDIE^4UFoG_V35ghJWZn6LiXAQ+KSBYd+7^f+bT8cCk9+DoElHeF_MUG@1+ghNhZnhJfVRm2[AP<+hW<+]OQ]KBX4SCoR3r5+IhdTDn[6q7+g+L+8E9@+P+HUFYJ+1^f+bV2cGk3+ZoAlKeBm5+CnQ6r7+gDk8+EohMq9Rl@[Z0dI4<]", "ZODIACVICTORSVALLEYOTHIRTYEIGHTYSEVENRTHIRTYTHREESNORTHONEHUNDREDTWENTYTWODELEVENRTWENTYSEVENSWESTBENICIATHIRTYEIGHTYFIVERINFORTYONESNORTHONEHUNDREDTWENTYTWODEIGHTRTHIRTYEIGHTSWESTLAKEBERRYESSATHIRTYEIGHTYTHIRTYTHREERFORTYEIGHTSNORTHONEHUNDREDTWENTYTWODTHIRTEENRFIFTYFOURSWESTSANFRANCISCOTHIRTYSEVENYFORTYSEVENRNINETEENSNORTHONEHUNDREDTWENTYTWODTWENTYSEVENRTWENTYFIVESWESTZOYIAC", "http://www.zodiackillersite.com/viewtopic.php?p=37219#p37219", true),
		new Cipher("daikon5, 2 letters per symbol", "CV@pIpAuvik+9jTZn0_5bLdB]wH6ea^SgbGC4wc7PIfaDx1Ep@uvhlAUYi2e+vxZg8MN0xnd_3bJIc5Fwx+e91<W2wy[aZ3G5c1V6f2uzpBuK7e0xvhiCozcdxIe83]xO@uvh+rkyZD^0A4wdoIEn+<[yx5wZ]4vim9bPM6_pL7gflBx1hCNTx0ndxj2Ozg@<A3x5Il>FBXC+@lAunyZn8PGvk1M2YB^zDW3ux0CQ5odp@uIXlAun+_9cZemHU1ozBlCuE6[i@o0m7acmF2ednI^3bJ+m8alAu]NmzKV9BY6lG7Ox8jcm_9uZ^4vh6fLT5ozg7Pem1f4yT@f+ZDK", "ipikekinagpeoplebecauseitssomuchfunitsmorefundorkinartillgameadeforledbecausemansdemoonsasidueanamopofanekinsomedargivemedemoatdrinarespienceitseverbendidasettagyourrocksofftidorilldebedparefiniadaethnicieitinbiebornaporalicensandeihavekinectinbecomemysloveitinrodgiveyoumynamebecauseyoutintlyespoilotnordopmyconectarofslaveformyoftilifeens", "http://www.zodiackillersite.com/viewtopic.php?p=42460#p42460", true),
		new Cipher("daikon6, 2 letters per symbol", "LZhiCM+ks8t9uNuVFab0mvklDWkGLMw4xfVEjIyW1JoCkxgz2fAc3HdksN5yVZhksXyFbiODm+ksPmGLaBTlZ6h0EiU1l2RH!ksjC@l#lT3$M+0z1g8c2NDz3kj%UeEQY+ks$LwWCjKzFMGNDI0J9L^ra%HlOFZbXE!wGCKla7IDjMZhiEN1zAuTC$dh2DiU3j%THfJEth+vitLMatyVN4yWZhz0tj%Ul#5FihxgiCx6j^tLw7xfVDjKj%Tl1$EWGMBVmyug2sNyW3mvklCVkj%Ul+eDPY0W$L4yVah$M12z3f8c+yWHdksNwVEjIksj%TS9m#UCLMzFNGLyDzvJAM^rZ%HlQFabElNvm0RCjz&jD1rZ%GlOHB8tKE52raFm9T3@yWksXy6g&s$e+GzvCdz0cjHzsuLFbZU7y1sMYDEPVxA42WBX5GCI", null, "http://www.zodiackillersite.com/viewtopic.php?p=42499#p42499", true),
		new Cipher("Jarlve 2 letters per symbol", "5*bD0<3F17+Bh*cabY0.UZfBS%,;-CQ+(1'JDMRTL\\?X]*9!E(%UTiQ\\?%;E,[ZD3Y7G-']Ua/ebA<cS7A0Q+R9VgJXiW)[.\\Zf0=!1hT&D&S?7OE*CF)+fBce).]GUW:&9SAIW\\B;X3TL0<?]iM(;ce9LQY7SICgEO+TIWUf@\\6?&IRBE%J,'-Fb@!5afD7]9[<TMiQ#Jcgh?e`!'XCGM(PS;IWUOgffD%5*1\\O\"YhZfBEiM!e\"AOX30R%-UiQ[1ge+(5#]bZMY;9<DFfi0Q[*T?5%7O\\bCGeP+W:0,.]=!-h9acES%aD7SFQ#TIWR1'YKCG7I-gM!U1i?F\\Y+X", "dennisfongalsoknownasthresholdofpainisanamericanentrepeneurandretiredproffesionalgamerwhatnowfollowsisanexcerptoutofthreshsquakebiblelearningtoshoottothegroundbelowyouropponentsfeetcanpotentiallyincreaseyourhitratebyanorderofmagnitudewhileeffectiverocketjumpingcanhelpyoureachhiddenareastakeshortcutstoweaponsandartifactsandevenescapefightsifneededlearningthesoundsofthequakeworldwillgiveyouanamazinglyaccurategrasp", "http://www.zodiackillersite.com/viewtopic.php?p=42522#p42522", true),

			/*
			 * glurk homophonic vigenere
			 * 
			 * I like eating people because they taste so much better than the
			 * wild game I kill in the forest because man is the most tasty
			 * animal of all to kill men gives me the most thrilling experience
			 * it is even better than steaks on a grill the best part of it is
			 * that when I eat I will add my sauce and all my meat I have killed
			 * will become even tastier I will not give you the name of my sauce
			 * because you will try to buy it all up and make my meals less
			 * tasty
			 * 
			 * Keyword: FIREGUN
			 * 
			 * N tzok ynyqek vybutv fkwnzav xnyl yijxk mb rctl hygymi xnua ypv
			 * aofq lidi O evqt zr zbr kwiiyn ojkryyy zfv zw zbr rwjx zufyg
			 * rrognq ww erf gt szpr grs ozzkm zj byi sify byvofynvx idjrwqvriy
			 * vy qj ibya gmkxkl gmie wzynpa fr g aentc xny ojak tglg tn zx om
			 * gmik anya N mrx O qvqt rhj gl xilgk uai icp ss zjik M nuij szpryq
			 * bqcp hyptuv ibya yijxoye N ezpr hby ozzk sbz byi tuzj ww qe mnzkv
			 * fkwnzav cuo jntc xxs gt jlc on nqt lt ghq ribi ss zjicw ryfx
			 * brwzs
			 */
		new Cipher("glurk homophonic vigenere", "ABCDEFGFHIJKLMNOPQJRSTUPVAFWXYZaJbcdeOWfLghbiaANjFkPjlQHWmnmoIKHOTdCMpqRmYhAoZJrFLFsQKCRCMdrRtVCNQhurpDgAHRRIpQuOvCwpxdvoTCqbCZMFmvmQXchKDQLSPamnZrRHPrihPLHZiMLygbzazWxbYIRsLSwyQdgyIAO0aALDtjJBgWgBSCVobxbizjGXyGbd1DHKHOrftuW1YWgJNymm0kvvTtYzbSNiZvskrXHMH0kfXkONKiMXjFit1lLIAICwpfcFossEvMCMFiONTtRRHIbGszPQzRGsyP0NltSOeaavgBZWelSSHOWBufHrmMivvsZieRdLQacdRTvB", null, "http://www.zodiackillersite.com/viewtopic.php?p=44895#p44895", true),
		new Cipher("doranchak z340 manipulation, rectangular swap with large reduction in Jarlve non-repeat score", "HER>pl^VPk|1LTG2dNcyA64O%DWY.<*Kf)B7<FByUZGW()L#zHJSpbTMK8*V3pO++RK2_M.+&Bjd|5FP+&4k/pBc(;8O-*dCkF>2D(#cV4t+;2UcXGV.zL|(VUZ5-#O+_NYz+@L9d.fMqGZR2FBp+B(#K-lWB|)^J+Opy:cM+-U+R/5tE|DYBpp7^lO2<clRJ|*5T49M+ztFz69Sy#+N|5F8R^FlRlGFN^f524b.5+Kq%+yBX1*:49CE>G2Jfj+|c.3zBK(Op^<M+b+2RcT+L16C<+FzlUV+L++)WCzWcPOSHT/()p|FkdW<7tB_YOB*-Cc>MDHNpkSzZO8A|K;+", null, "http://www.zodiackillersite.com/viewtopic.php?p=43712#p43712"),
		new Cipher("doranchak z340 manipulation, rectangular swap with large reduction in Jarlve non-repeat score", "HER>pl^VPk|1LTG2dNp+B(#O%DWY.<*Kf)By:cM+UZGW()L#zHJSpp7^l8*V3pO++RK2_9M+ztjd|5FP+&4k/(;8RFlO-*dCkF>2D(4t++q%;2UcXGV.zL|Z5-+fj#O+_NYz+@L9MqG2b+ZR2FBcyA64KB|)LV+^J+Op7<FBy-U+R/5tE|DYBpbTMKO2<clRJ|*5T4M.+&BFz69Sy#+N|5FBcp8R^lGFN^f524b.cV#5+KyBX1*:49CE>VU(G2J|c.3zBK(Op^.fd<M+RcT+L16C<+FlW-zlU++)WCzWcPOSHT/()p|FkdW<7tB_YOB*-Cc>MDHNpkSzZO8A|K;+", null, "http://www.zodiackillersite.com/viewtopic.php?p=43710#p43710"),
		// glurk has a beale test cipher here but i have not added it: http://www.zodiackillersite.com/viewtopic.php?p=41577#p41577
		new Cipher("glurk 2015 cipher challenge 1", "ABCDCEFGHIJKLMNOPQRSTSUIVWXYZaAbcKUNdefNgDhijeklmnKMBYoIcNpREYGXqrstuavdmXwlxjekIrylvxhePoZgQqyclrXlz0efegUUrgQum1DCl2whnys1LxrhPvfF3drcRpWCPdi3Ph0MEXQusb2yYOcOct1pxpMIX02nbUi3L4cQsbHVfo5ynwnB6RYQWYvEr7JZJqVQR8IiXJxeIPvbxeqmJx2u4z32hSrZa0u0o4CWDojynXrja2Q92nbgZo0fs1VvBcJR!C931BijpXWpYqJ5MRgv4cJMC!93uefnn7QW3S4wdofLLRFXfgsdsv3f22uSBcAoYeJo", null, "http://www.zodiackillersite.com/viewtopic.php?p=27082#p27082", true),
		new Cipher("glurk 2015 cipher challange 2", "ABCDEFGHBIJKLMFNKOOLPJQRSTUVWXAYZabJKMcSLdeDFfUOLaScghTDRiIjkRlmFNnDODoGgpqEgDrZsgthNZIuEBuvNCwxgyqzcVdzndKY0sveT1wewSTx2GrXPjoMTD3lZsXxCxDnkkjQA4CsjE45Lg6Za7q8wNm9Ia8a!EWFRv6aklqbHmxkZUbDZwmtrKA8DS4AclQwYnonrNWgpTVhZRgKh!u0NXb4ENXEiIuYKuuLMTkq87j2IxeraqhWCbfXRbxWJWcnIQazS8wkQzkWoev!xueJcvoQ3WXdK732WwK1jE8FZJghQxzjveItJgsZoBJn7sSKYFR0radxZirLRAQzHk9tK7IEh", null, "http://www.zodiackillersite.com/viewtopic.php?p=27118#p27118", true),
		new Cipher("Jarlve 2015 cipher challenge 1", "16AO-1=+Y<T=&P>HT(yVG<=@vlZ>^T6Z1{,\\H<%F^%<G>0<O/w}J.O@Y?S5DGJ\\;BB44FO=FY<G_&&ZI]{lo\\}OTf>hMJ--BD&+JHw'HVZ,I,n1}~ZCYVKQnO'YT?H'(M%f>4ARSh1IQIwZw^.}J=S,?yNh*-QKSP+%%F5\\GQvP~\"Dw45BkOhJIRH_B6B<h?f'}~0(M1,+AZK@_.VG\"6]Ek<5E-C<O\"YNQv0RPnZ}f*K0A{yvY6A%\"~]yRC\"A,E0I@,5,^N.f@j+<,RN5S_4VBM=_M\",6HGl''O)n<yv_R-,kBVDw+TB>H?%FO\\y,w6VEDYT4EkC5KD0I~f\"QWJB", null, "http://www.zodiackillersite.com/viewtopic.php?p=27083#p27083", true),
		new Cipher("Jarlve 2015 cipher challenge 2", "KKxbh/K@.KKrSs>cH>GIlJKXluI^L2lJmK.@Ql?Ln0j=,jIE@fbN?'u)(0,iQXd+YH=]')o\\K3YWeza\\jUKL2T:f\\'ICKeLS0>V(i+.ijrn?KEzUCGQtFKGKKJ:mR+E]B6BKXmJj,MugYaz)H)V?R2Ec.@sd3`sICTMg]hRTGSG^K!GUr.Yx!\\iJg]K,?i:W\\MJ:M6C,K^6rTx(F`UK(o@.3)tH(JjLSmF>)zXY=j+.S'lC2,ja]'n.EL/,rna2l0>?Mdih]hcd/HWH0Vj2@s0W=ulY2`jEfB2+.@DKzUNr:tK\\!aSmKLWuKLtsUbmKV`/xKN(3fhJVoF>BaQMuX", null, "http://www.zodiackillersite.com/viewtopic.php?p=38712#p38712", true),
		new Cipher("Validator cipher challenge", "ABCDEFGHIJKLMNOPQRSMTUVWXYZabcdefgShijklMMmZTUnoIKBpOYqZPrRHsITWEAPmtuGKjvwWYxyOkz0R1k2V3YkbhjxRIguTWQr4PNcG5ZCDB67vAeUIC4tTxjyM2bvLQUzkG48eZwcShk1aCmj9rxcsbUY32MWmI9PWgOqkY30zPHdbljWIxGZoPZRGkjV4yXIgTkIu5ZOXNeWlFMRZe5jxuSur0TxfOZajvGWm17xpYDAIkwASm1YxyVE4GWmOXLV5ZEtBKsCtgr4tACu7zEMWRrxLsOT4QHjyDzVKID19G2jkCgVwnRZyA578m5yUnTl!1yCVnUfooSZZ", null, "http://zodiackiller.fr.yuku.com/topic/7560/340-Cipher-Challenge", true),
		new Cipher("Smokie Treats: smokie27a: homophonic period 19 scytale transposition", "ABCDECFGHIJHKLMNOPQRSTUVWXYQZVaGSNbcdeLfYghQijklmgJnoMGpdTqrAstSOBuSVvwSYQSIEHKejuhxSFsDMuykraCfwMJiRZlUzSvNnGvPLDLP0VK1iuSUqghobHwvAkFSBXpKWGjS2eUrSIg3qFiTLPOhazYjt4ZuNSnHSmSvfS5SKR6vOSBLQZQsMqedJ7EG0dGItPgjxRhFHTSxlJsMhQ1y6Dfy0QoBVnw5gHNYkrSevQXhKozIZZDNfKVKv1TcufOXDUTQPUBkSIhM3QoISr6GSgd1lPEVSWB3qzIpPZNOtCSD1JPZSkRhvxLBJiIrvW4n37YUMzZM", null, "http://zodiackillersite.com/viewtopic.php?p=45078#p45078", true),
		new Cipher("Smokie Treats: smokie27b: homophonic cipher", "ABCDEFGHIJKLMNOPQCQRSITUCVWXYZabcdeQfghijkMCllJmnAHNODCITopFKRSHqZCPbkrlCaCYsnjBGItghXDJuQfvKwVExyRFzC0eayf1WLA1NeobrpS23eXdCbJhpO2smvZ4Yt4Hj0iVkKCQ1ICJgCcDNrRCxUXeCWmCqpPbC5CFPLSsgKltYijkhBMNOI0KQ6JDNaPjk7sxzZhaRCO5bkFjCOJpwIJaCqTvZhGks3VtAqUOXxHjTQPIJrANWTwieEumCd66LZOwkh1qTGvRRxCSabkCOBNhHX3QeGTibBoibeCzzcgCqZpKsDaVQX2kKtyQf1l60r2WCFGz", null, "http://zodiackillersite.com/viewtopic.php?p=45078#p45078", true),
		new Cipher("Smokie Treats: smokie27c: homophonic cipher", "ABCDEDFGHIJKLMNOPCQRSTUVCWXPYZabcdeQfghijULCklImnAoMpqCHUkrDJRSGTICObsNlCaCtrnjBFHughPqIvQfwJxWcyzRD0C1eazfiWKAoUpkTNCS23ePdCbUh4e2rmXZ5tu5Gj1OWMJCQoHsIgCcqMeRCdVPeCXmCT4GbUtZSoCSrgaluYijsaBLCpT1JQ6IqMaoTU7Cd0MhJRPptbsDjCeIrvjMaCTUwZaFIr3WunbVpQdGjMQOHINAMXMvieKvmud66KUexsJiTMCwRCdCSabMCNcMJGQ3QeFUOHEkojpRzzCgFTZCarDamPP2shuzQfol61e2XKDFz", "plaintext [Signing the diver to go on, he followed him round the vessel's stern. The sand on the other side was high and one could climb on board, but Lister shrank from the dark alleyway that led to the engine-room. For all that, he went in and saw the diver had opened the jambed door. When he reached the ledge a flash from the other's electric lamp pierced the gloom and he tried to forget his throbbing head and looked about. Sparkling b]", "http://zodiackillersite.com/viewtopic.php?p=45078#p45078", true),
		new Cipher("Smokie Treats: smokie27d: homophonic cipher", "ABCDEFGHIJKLMNOPQCRSTIUVCWXQYZabBcORdefghUMCijJkAAHNlDCIminFKQTHoZCPbUpjCaCqnAhBGIrefQTJsRdWKtWEuvSFwCxOavdyWz0CNliblCF12OQuCbma3l1nX4Z5YC5HhCPWUKgRyICJeGLDZpSCuVQOCXkCo3HbmqZFPCTneairYgINfEMZlIxKR6JDmaPom73cwZfKSQpYbUFhZpJ3sINaComkZfGUn2WrjbVlCuHhURPIJp0NXmtgOEs4xc66zZltNayINCWRCuCTabJPOzNfHS2RlGmyhCigopRvwBeGomCfnDKXCQ1UarvQdyj6rl1XEFGv", null, "http://zodiackillersite.com/viewtopic.php?p=45078#p45078", true),

		new Cipher("doranchak: multiobjective evolution cipher 1", "9%(1UyKbjJ.#5BR+3+28@TSp1l-^NBtHER+B|JLY8OzFR(4>bl*VLk+FU2)^RJ/c5.DOzB(WH8MNR+|c+.cO6|5FU+<+RJ|*b.cVOL|5FBc)T(ZU+7XzR+k>+lpyV)D|(#kcNz):68Vp%CK-*<WqC2#pc-Ff2B9+>;ZlCP^BU-7tLRd|D5.p9O)*ZM6Bctz:&yVOp%<K+>^CFqNLPp*-WfzZ2d7;kl<S^+/|dT9f4YK+WGj4EyM+WAlH#+VB+L<z|4&+OkNpB1V2Ff/)z+Mp_*(;KSp2(TGO+FBcMSEG3dWKc.4_G5pDCE4GyTY+_BAdP2p|+tFMPHYGK+F6pX^2", "plaintext [OPEN__________WERENOPRISONERATALLWETHEYFOUNDWETHEYHUNTEDHIGHWETHEYHUNTEDLOWAWETHEYHUNTEDHEREWETHEYHUUNTEDTHERETHEMANWETHEYSOUGHTWITHANGXIOUSCAREHADVANISHEDINTOEMPTYAIRTHEMANWETHEYSOUGHTWITHANXIOUUSCAREHHADVANISHEDINTOEMPTYAIRENTERWILFREDFOLLOWEDBYLIEUTENANTLIEUTASTOUNDINGNEWSTHEPRISNERFLEDTHWILFREDRHYLIFESHALLFORFEITBEINSTEADWILFFREDISARR] tokens [[OPEN, WERE, NO, PRISONER, AT, ALL, WE, THEY, FOUND, WE, THEY, HUNTED, HIGH, WE, THEY, HUNTED, LOWA, WE, THEY, HUNTED, HERE, WE, THEY, HUUNTED, THERE, THE, MAN, WE, THEY, SOUGHT, WITH, ANGXIOUS, CARE, HAD, VANISHED, INTO, EMPTY, AIR, THE, MAN, WE, THEY, SOUGHT, WITH, ANXIOUUS, CARE, HHAD, VANISHED, INTO, EMPTY, AIR, ENTER, WILFRED, FOLLOWED, BY, LIEUTENANT, LIEUT, ASTOUNDING, NEWS, THE, PRISNER, FLED, TH, WILFRED, RHY, LIFE, SHALL, FORFEIT, BE, INSTEAD, WILFFRED, IS, ARRESTED]]", "http://www.zodiackillersite.com/viewtopic.php?p=42099#p42099", true),
		new Cipher("doranchak: multiobjective evolution cipher 2", "RT;%cqtp/+2F4Wk*|5FBcK9O#y)HER6SW8<+JlFM(2/k45R.zZ)d9|k2C52WT&+zc9|LGG+tLf>W^zjBd>6<c4N#OVDA+zSYCZ1NB.F^pNY+OT+@OpA69+7*L(MdVc.bUXRKl2#BO+5RlcHt8B4|MpD5T-NBJ+.2^+|ylCp2<-O+UpMbdSY1#j<Ff)zpZ>KEUFL)V(&zN+:cB+k6yl3MO(Cb*|JRHzLq_pl+4GlCX|3.*-OM#>_c2H;D6BJ4^|T5(ktKP+)GG%dR(^8B-KWVL.;+FBcSPFDy62B/pKP|MZY1f+UcF+<+8p:-*fB/Ez+73^|5FpO<(V7+yOG*KzR_", "plaintext [ASBEINGONEOFDANTESFAITHFULANDATTACHEDYFRIOENDSANDWASHENOTSOASKEDTHEABBEGASPARDGASPARIDMURMUREDTHEWOMANFROMHERSEATONTHESTAIRSMINDWHATYOUARESAYINGCADEROUSSEMADENOREPLYTOTHESEWORDSTHOUGH__________________EVIDENTLYIRRITATEDANDANNOYEDBYTHEINTERRUPTIONBUTADDRESSINGTHEABBESAIDCANNAMANBEFAITHFULTOANOTHERWHOSEWIFEHECOVETSANDDESIRESFORHIMSELFBUTDAN] tokens [[AS, BEING, ONE, OF, DANTES, FAITHFUL, AND, ATTACHEDY, FRIOENDS, AND, WAS, HE, NOT, SO, ASKED, THE, ABBE, GASPARD, GASPARID, MURMURED, THE, WOMAN, FROM, HER, SEAT, ON, THE, STAIRS, MIND, WHAT, YOU, ARE, SAYING, CADEROUSSE, MADE, NO, REPLY, TO, THESE, WORDS, THOUGH, EVIDENTLY, IRRITATED, AND, ANNOYED, BY, THE, INTERRUPTION, BUT, ADDRESSING, THE, ABBE, SAID, CANN, A, MAN, BE, FAITHFUL, TO, ANOTHER, WHOSE, WIFE, HE, COVETS, AND, DESIRES, FOR, HIMSELF, BUT, DANTES]]", "http://www.zodiackillersite.com/viewtopic.php?p=42099#p42099", true),
		new Cipher("Smokie Treats: Z340 untransposed parallegram scheme", "HEBR(U>#Z3pOGp+l%WO&D^D(+4(GVW)+k#2bPYLR/5J+Jk.#Kp+fZ+B|<z28KjROp+1*H_Rq#2pb&RLKJ9^%OF7TBlXTfSMF;+B<MFG1BG)p+l2_cFKzF*K<2BpzOUNyBO6N:(+Hdy7t-cYAy29^4OFT-N:^j*Xz6-<Sf9pl/CpclddG+4Ucy5C^W(c+M8|CV@K+l#2E.B)>+*5k.L-RR+4>f|pMVFFz9z/JNbVM)|DP>Ldl5||.UqLFH2|<Ut*5cZG+kN(MVE5FV52+dp++|TB4-R)Wk^D4ct+cW<SYM(+|TC7z.;+c+ztZ8y.LWBOB31c_8z6PYACOO|SBK*;+", null, "http://zodiackillersite.com/viewtopic.php?p=45198#p45198"),
		new Cipher("Jarlve transposition cipher. Inscription: normal; Transcription: columnar.  No nulls and/or secondary transposition. pr1_jarlve1.txt", "ABCDEFGHIJKLMNBOLPQIRSTJUAVWXYTZMaDbcdeMfCFghijbTkYlbmJnKOGoQFpcUScVqaAlmkrsOWGghZdEtTkbGDrguLHKNvrMCWwlJiXYAQUAnFslPGflKtQdOjkmZfTbqqhQEkZofHDvJICBUCupmSIBDEhNARPJAlYjurTlwwpWcpUPgesmVatxKQnXmMbOGWPjdSFwMLoskRCHBEptVHUSaqSgJSEXZiAAIYfmdJnMMwWsPgZtMNhqJLrOutlYFGovnuSkKRAfvNHQJwFijUVCaRXjWAMmOSaclsoigKhftBdKeQgTYePXeLeTZvJLwIierNqksNuhVOnL", null, "http://www.zodiackillersite.com/viewtopic.php?p=45212#p45212", true),
		new Cipher("Jarlve transposition cipher. pr1_jarlve2.txt", "ABCDEFGHIJKLMNOPOQRDBSTUVWXBYZabBcdFefghibfTjGeeKkDNlmnoABIpqarCMlGRfUstWnKPuYYItnZKafVivwhtodAmLXSFJOpjsDCMhXPlBSDnNTxRILrVnmEUtnQsogkAkyGyjcJZFNaOYCgbeGhyzZwvfpeiRSMKUljVZY0wWFEmqpTkosuNVqLHgAzRR1GYuItXyyATJOaPgbQkhmcvujwGCMldUsVYJLISYopfZVtynhSqKgygXJzP1YecDQOhWwFkNuaAvOC0MibRfUlZTwLEdnFeNVKTDiHBj2aGpI1WFRbXsEzoUscItuZHfAeaPFcnvKQXyEHE", null, "http://zodiackillersite.com/viewtopic.php?p=45309#p45309", true),
		new Cipher("jroberson 405-character cipher (1st ordering)", "T[jaIfd+@rg=Dhj2moK0Vs.uJ*v;w*AEGbX>^GH5Uk(:?<Tu_tF!#jx�i~R]7-z{}pNg$3lM9fS*@I-�Z2�|ks/J>4Tq?PAsWH61yt,G$mQ\\a�!iB=d09+vr{ezUWd%6[w&\\L#hJ>Ri5-yXp{m\\�:_3Zs0v8!<jw4g^`j(GM1OCTxQ�#-17n,};fW9@/[a&2jm�{|E0CuvrX=V!KDhcZJ~l,.53)|?_osw-y$41]b�^Fe(*x�RCUSyq/:\\<&8�z`?�7W#�6/�-O}Q>au=�0vUF.W!:IwdE^zG*(d-�Xx<R#Z4A)7u/9k1ay=�{Hl}YfJ.I:t~&\\DQ�@r8<V3X]1S*m`icOe0B6p%v5NM|Ph#Z>w*^do�4(/9xI+7bga1[�}Gq�KLfT=d.Ak&!@uUFHtBz", "I LIKE EATING ICECREAM BECAUSE IT IS SO DELICUS IT IS MORE DELICIOUS THAN EATING SHSRBENOT (sherbet?) THE FROZEN FOOD AISLE BECAUSE ICECREAM IS THE MOST DELICIOUS GELATO OF ALL TO EAT SOMETHING SWEET GIVES ME THE MOST PLEASING EXPERENCE IT IS EVEN MORE PLEASING THAN SLURPING DOWN AWEATYSSHAKE (***milkshake?) THE BEST PART OF IT IS THAT WHEN I URINATE ALL OF ICECREAM I HAVE EATEN WILL BE REBORN AS LEMONADE I WILL NOT GIVE YOU THE NAME OF MY GROCER BECAUSE YOU WILL SLOW DOWN OR STOP MY EATING OF ICECREAM HAANCZRTESTDIA (filler?)", "http://www.zodiackillersite.com/viewtopic.php?f=81&t=2368", true),
		new Cipher("jroberson 405-character cipher (2nd ordering)", "au=�0vUF.W!:IwdE^zG*(d-�Xx<R#Z4A)7u/9k1ay=�{Hl}YfJ.I:t~&\\DQ�@r8<V3X]1S*m`icOe0B6p%v5NM|Ph#Z>w*^do�4(/9xI+7bga1[�}Gq�KLfT=d.Ak&!@uUFHtBzJ>Ri5-yXp{m\\�:_3Zs0v8!<jw4g^`j(GM1OCTxQ�#-17n,};fW9@/[a&2jm�{|E0CuvrX=V!KDhcZJ~l,.53)|?_osw-y$41]b�^Fe(*x�RCUSyq/:\\<&8�z`?�7W#�6/�-O}Q>T[jaIfd+@rg=Dhj2moK0Vs.uJ*v;w*AEGbX>^GH5Uk(:?<Tu_tF!#jx�i~R]7-z{}pNg$3lM9fS*@I-�Z2�|ks/J>4Tq?PAsWH61yt,G$mQ\\a�!iB=d09+vr{ezUWd%6[w&\\L#h", "I LIKE EATING ICECREAM BECAUSE IT IS SO DELICUS IT IS MORE DELICIOUS THAN EATING SHSRBENOT (sherbet?) THE FROZEN FOOD AISLE BECAUSE ICECREAM IS THE MOST DELICIOUS GELATO OF ALL TO EAT SOMETHING SWEET GIVES ME THE MOST PLEASING EXPERENCE IT IS EVEN MORE PLEASING THAN SLURPING DOWN AWEATYSSHAKE (***milkshake?) THE BEST PART OF IT IS THAT WHEN I URINATE ALL OF ICECREAM I HAVE EATEN WILL BE REBORN AS LEMONADE I WILL NOT GIVE YOU THE NAME OF MY GROCER BECAUSE YOU WILL SLOW DOWN OR STOP MY EATING OF ICECREAM HAANCZRTESTDIA (filler?)", "http://www.zodiackillersite.com/viewtopic.php?p=32199#p32199", true),
		new Cipher("RTF 408-character cipher", "q%2@FMGH&WK5Qh/%+aWL4lc2JbH&#sTWT~XoB9zxQ0!H<6dlEqRFJ5oUYw4p6KUbam@7kmOsxnHAaedd4@QW9*2poTln%+NQXFcLbUEO/FX+~qH&%Q�5AOspDB>*W<K4eH9!^2CWcR^QX</Fqr2d6Ub#D=+7*U^n9z@WABp+X9S&!hU#<bQ�Ob&GDw7xa0n4ClE/MU+9~oqQMPWpp5TOoUYbAnwHDGTr^E/WlQJq%mN<#O�EzTG�dOxU0FwQ065bTk&Y*o2O+K4noxEYhUQp@ao>c4R+%9H7sxhQ^SMPEUKDl5Id<qW%9O4JJA#T@!<zk&O7/~21GH2zqoMH7#I�^FAGe*TX�hRcUrnEaxDlewa6W0X<zSW^P+KS25OT&Ms&L�EOJQrK%0>T5c%s�%2+FoHY", null, "http://www.zodiackillersite.com/viewtopic.php?p=32476#p32476", true),
		new Cipher("Smokie columnar transposition", "ABCDEFGHIJKLMNOPQRSTUCRVWXYSFKZTabIcMOdBANefghijaklKmnLoQpSDjbUHdjqrstuVvKsXQwaxCalydIzcGD0FhjbMfOwRVNGiC1KmxiQ2BThZP3hLSNypwduqmuh4LRbV05mszNYZCjfXXuc3EiaKylKtvFINO6S1bOIo7djeKoyxon8KpDVLdoUsKIEiktaLwnoHFMA0bS8WoS9sismESzWRNtGMOlhVwkTCBzSXtgr2YGKcILHnN18AqCQP5Q9HS9YxkM9ezdfIN3jXH4D0nKUbDOXwCuWSt8qcFQkmMwsCvDyNOxPbdjHim9K3Q1VUwyroU7SXcCz4", null, "http://zodiackillersite.com/viewtopic.php?p=45346#p45346", true),
		new Cipher("Jarlve diagonal transposition, pr1_jarlve3", "ABCDEFGHIJKLMNOPQRSTUVWXBIYZaKbcdICefQgACRFJHODIUfhiMgNjSEkGAVlbmTZjEeJGWLnZopqSPVDIUcXdrmjBbehsGkEJtTrYuviGOfHcwSPnOgltApLGDVIMmPuFdIUxyObiaPZKxNkGLeJTz0fgqGGAzCyOSyRVm0bqWcejEQBsDozLGPdOJ0PkUfgySKYAGqGZapHjTruM1EGChRWQVZXGDctiCbdUFBfeGKJjgSREkVGNlpWGBKrQZsGtzRnuAjObGW0TBPeOPmGscoqdXxYkhGiJLHTISIODUfgVacvbyeyJAnmlMIKdRNk1FpzDUvL1fsSVvW0s", null, "http://www.zodiackillersite.com/viewtopic.php?p=45360#p45360", true),
		new Cipher("Jarlve diagonal transposition, pr1_jarlve4", "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdeUfghiFjdklDEmHPQnAoNhIMjCpKeqgLTBWGfJiFZnSkrLsACmQtXjqVdYnpDMlReuBvWGbkOmQUrIJgEXPYZThDicvBAGEWfkHFsRKJutmOorMXVYbegKPsBQiaVNdqoGWRkTZwmSfovDJZAElacNQMuFIeWLPdkrRfbgmrXSiCaAROcKucMtYHQqBNTuFegdItxfDVGJhEUFOUiIbXKVqWHlSrYAMbakLhsmKlBpejhRtodgclQfhruvPilFVnOUsAvIbKGTwDhOLMNSJRleELdqgfaXWYPVtFvdNCBfkbLmKQGTiIcA", null, "http://www.zodiackillersite.com/viewtopic.php?p=45366#p45366", true),
		new Cipher("Smokie transposed at period 20, symbols 1 and 14 polyalphabetic", "ABCDEFGHIJKLMNOPQRSTUVWXYZBaKbcdeRMfAgDRERhiHjklmPZXnoBpqarsigYMRpobtuDGvwdxDyLpKzD0QYhJRZT1ud2vcBGhl3eSWE4UR5KaMHb6Kjd4DmEErmDmC7LhRM0bl8cKSRFXNeYoBKRkS4UsdFQ10btuxnAttRRNLLVZUS8aEer9BKtQoRkrqRZvUzr3DbxbmyvR4XHgBDNXKIh2GFQ0LaJCesnlVRROOZjdfmWOV4aDLRpFK9Tz2m80Za3GRLwDqAXrmRaSMMP0rtXzgODGNHuwRZkJS9vH7hLyzv0ubdRW7YDe0DjxJsCK3PSAUFR2DEMRV0Z8", null, "http://www.zodiackillersite.com/viewtopic.php?p=46842#p46842", true),
		new Cipher("Smokie30, transposition with misalignments", "ABCDEFGHIJKLMNOPQRSTUVWXYBBZKEabcRDdAeDQfRghRijklPmXnoBpqrZsheYMRtoEuvDGwxbywzLpKtD0QP1JRmT2vA3wIBGgk1cSWf4UR5KZMHE6Qib4DlAfoFDFRdLgRMJEh7ImJmlXNcYZmRRjSFUebFQ70DivynAziRRNHLTmJS7ZfcZ8KKzmZRjrqqmpUwo1YMyElzwR4PHeBDjXKI13G4Q0crJCcsnkTRRO6mzb9lxOV4rDHBwFB8Tt3l2SQZgvRLWYCDYolRr0MMXSZuXwe6DGjcGWRBySS8pHdgLutwJGEABx9YDR0biyJeqK1PSEUFR3DffnV0K7", null, "http://www.zodiackillersite.com/viewtopic.php?p=46860#p46860", true),
		new Cipher("Smokie31, bifid, 2 symbols polyalphabetic (39 and 48)", "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefEUghSijkFelJmnSopqWKrJksZtrquvBwrxyzJUsS0wbcQ1AdIePElvXSm2KtNqqh3y456gXlvr7sGw1SuTDk4Y00SmLJPHSFMpg8ASyPDhCVsRxiaFIHUSznSv3jSANB1P9qr0KSIBTtaQSxSj54jnfuDy1ruCS0!vNeKEgoRJmLrV1Ie4Fbaur7XGbSDyRxY3WZSctH8udLzSt0GS3HNLi3ZeSUfpeG1KvzPSnjQMu9IfMTS!7!OvNkJtmDCSLWa5Vqb6hvrSpjFsjxmi0iJk2e!HtSD2rYoPUwruUeszxzaSSAlhGh", "The discovery of the coin perplexed Giles. It was certainly the trinket attached to the bangle which he had given Anne. And here he found it in the grounds of the Priory. This would argue that she was in the neighborhood, in the house it might be. She had never been to the Priory when living at The Elms, certainly not after the New Year, when she first became possessed of the coin. (plus some gibberish at the end.  excerpt is from A Coin of Edward VII by Fergus Hume)", "http://www.zodiackillersite.com/viewtopic.php?p=47399#p47399", true), // key: A 1 2 3 48 B 4 5 6 C 7 8 9 D 10 11 12 E 13 14 F 15 16 G 17 18 H 19 20 I 21 22 23 39 K 24 25 L 26 27 M 28 29 30 N 31 32 33 O 34 35 36 39 P 37 38 Q 39 40 41 R 42 43 44 S 45 46 47 39 T 48 49 50 U 51 52 V 53 54 W 55 56 X 57 58 59 Y 60 61 Z 62 63
		new Cipher("Smokie32A, 5 polyalphabetic symbols covering 60-70 count across the entire message, randomization of symbol selection within cycle groups", "ABCDEFGHIJKLMNOPQRELSTUVWXYZabcdefghiAABjklmnoJYBpMPqUXrstdRLNuvvBXaAZCwqbKLMexryzB01mH20mQlgUvJj3BYNSTcdPp3R4xWbHBABjoZYehiFX5TAgaDvtDsIJK35mWpLMBCdP6NcPzZS0mhSb7XhaBAALMelgBWJjkmVtus2RpkRBqrKzboALwQ7BjDn4N5k8GSSuLABUyBHIcMZWUKodXxe1lmhFrszg6WwEBB6NnJK3APvyhDRQMZkG9Z78xuc6dZBQUhOmrqVG0ZlmhLiTZDSUb6MD1cw2eKMBOAUzguSBWAwLIJK1XVU4PsBaBqAR15", null, "http://zodiackillersite.com/viewtopic.php?p=47427#p47427", true),
		new Cipher("Smokie32B, 5 polyalphabetic symbols covering 60-70 count across the entire message, all perfect cycles", "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijkAABlmnopqBaBrMPsWWtuvfRLNwESBZYAbCxsdKTMgyz01B23oH42WQXiWEJlmBhNUVefPr5R6yYdHBABBqbagjkFZ7VAicGSvGuIJKm8oYQLMBCfPTNeR1bU2WpUd3ZjcBAALMgngBYJl5oXvwuIPrqRBstK1dmfTxQ7BlDpON85zGUUweABW0xHIAMbYWrqfZyg3nojFtu1iLYBEBBTNpJKmAPS0UDRQMb5G9g7zyweAfiBlWj6ZtsXx2NnopLkVbBUWdTAD8eG4grMBOAWYiwjBCfxLIJKUZXE6PuBcBSAR33", null, "http://zodiackillersite.com/viewtopic.php?p=47427#p47427", true),
		new Cipher("Smokie33, 8 polyalphabetic symbols with count of about 80 across the message. The plaintext is original. Cyclic substitution; no transposition.", "ABCDEFGHIJKLMNOPQRSTUFVEWXVYZabcdefghXijklmnopqrsPtuJJJUEvWwxkBpJXXaplRcyzRM01Of0XSspL2KCQNDMZJDgo2bJKILTTJ3LGzJMy4qW5i6FRmfdh7XN8QMoAup8yrcMavPJfdJ0wVxWszSFJMtRL8MXGrZXnNpRefgPXJAJUb1JCBHJKdaqcki3liMuJJEGfYRUWakrqORwlEsJ3SDNS51ugwxkEzgF6XOV7JtRJWEIkEBli2JGm8UpXhYWPXkcWJRflH0ZRWvvlsQ4MXqAJkl0zCMJaGPYJzukD6MXWJ0Czk9X8Cxl7ZtFLzRcJEXG1Oa6PMU", null, "http://zodiackillersite.com/viewtopic.php?p=47451#p47451", true),
		new Cipher("Jarlve jarlve_tw1.txt. Transposition + wildcards", "kEbYYNOC6%T;[:Nj\"41?B43m!nPcMWV>JYQBB]j$b_f.2S@I(N/^?*%=R;NFekX<&nTo3[hBlY$!MN<FT\"EP#BVY,NNC3><_Nbc1j^@fTFNI/-:6klQ%O&mY3jjJ?.$W]S=\"YNNn[CM1_VA>NRe#FXbo<2^?!6m%/hjE;)YBSTMN3cjJPnfV>.Fb%N<Q*,Nj2)BB@&]I4M$kYT[lY=!P4S\"1B3/)-Re<CT6NO;3n#BV,_m@JFF<NNlFSF^oI4Xk]ANFh>bYY\"TQEC-32c;jjNn1OQ6jmf<[NB/&#T*.!?%M3=R*J*S)$]PBj_lY*\"@YINek1N[o&h<VE>bTcj!3P", null, "http://www.zodiackillersite.com/viewtopic.php?p=47271#p47271", true),
		new Cipher(
				"Jarlve p105_jarlve_e1.txt. some difference in the encoding that also makes it unsolveable",
				"8#C.3E+]J@9'R#OJTUdVLB1EH&cTEh44e!,*a`iZ&:3V<AK`*)I:e<7/GIY8`CP_@QKgO^>TcER4'%Z\\R]=#'BahTEH^><XOdLY:=(`2(i9!)@/&OGFIQi3,^AX4P/_*Q^ce8BO>:Cc\\EcRLT3RZY24,U+aV<XA#EV)E^`@=,ZKF]8G_PYP*:*/T\\12V)QI/87\\XG=G'3&R>He%c[SLR/6dEg)A]J,.=1.9a_H()ZB1V)CcL!62^)<@1iF#4#i,*3.)UY_!K/`QQ:\\'B>`IOLG%JO297&dE+I(Qg62]]U=TP^i6.\\XU8YAB.Y(6'6EH..SKFR#I!//3Zh+_+3QPe",
				null,
				"http://www.zodiackillersite.com/viewtopic.php?p=47984#p47984",
				true),
		new Cipher(
				"Jarlve mimic homophonic substitution using addition and modulo functions",
				"8DMX]h1=IR`g7<K[g[]bef;NS\\09L_n;PSJPe3<PYl9HZ_e:H\\dTbm6BNWelCLX\\cd16n<PX]c2DV[nBDILMbdi67ENa5=BO^1EIJXNSe4I\\]k4ABN]cd0<?NYbn:M\\inBJSah/8=BUbg;CHUd7K_g9BNIR`glDTYk0>AFOcl?3IN\\^c7KPb6>?MTYm09GNg6K]/>AL_n4:QI]efm6HTh057<Oc345IX^g;DWk34H_gl:C6?DMdm9EGL^ce4FT]Zjk=>BKNSTbfg3?S[OX`a7<GP\\hm1HQ]ik_b1>CPi<HI_d7@W`lg5DX_h>C\\k@Mf45BG8=@AVinGVkBKWc7Ib",
				"i like killing...",
				"http://www.zodiackillersite.com/viewtopic.php?p=29105#p29105",
				true),
		new Cipher(
				"Jarlve p105_jarlve_e2.txt per 4 row period 19 transposition after encoding", 
				"ABCDEFGHIJKLMNEOADPQRSTUVWXYZNTabGcdefNghijkNflmnopNqrNBYstrCuvwNxyzzNFLj0xm123BNYTLadk4vtVHwOqZrlJReIqPNi5QyRMnSKDhc5pwb04A56NJTX4INPGWEvSRUJ640cBNlzPAeQWNNBP7luLEfjRwuNnZmcZcONDI3eoMOqt3ID1F5MqywfTzZ5SaH8fGzfxyEnpjNSsmoPZNB93EzYtN9M5TaflqErVqrTsh2VNP0bwGUzINcEJvk1L3uK4R5qiWB9URLrfFZoTcEHPODzj2P1m0T2vs5zqXgKNkhd6tewIV9G!MYn7xNfCYgnQJsAEj",
				null,
				"http://www.zodiackillersite.com/viewtopic.php?p=48017&sid=f565b035139f1ebb908542b1a208303d#p48017",
				true),
		new Cipher(
				"Jarlve p105_jarlve_e3.txt didn't try to cycle but more or less had some kind of window/range in which he tried not to repeat symbols. Per row or per 2 rows",
				"ABCDEFGHIJKLMNIIOPQRSTUVWXYZabcdefghiELVBjklMmPnASCodpWqiZrstYiuevcfpwxtdVBYSTsnMHZNraibuEfwhiJeLyzjXFiy0rPf1gFXZnxCEzkueNFoMqJ2vwBYVTIv3den2YExgFMsZyCZKGi4MJEmtR1OwiuFglKye4MgE5isCZquFH3R1vdFAknJZ6aLtXexhdU37evnlDLVW1mH4O0XpcKMak81ATHROYdok0ywhMJZrvSYNeu2f8l95g3PqFvOCELeynYlviTII3Pf6QtGZ8SWDqHzPXJME5F0nueAQNU3pD8LB2fZD!PyMmlkExW4bGoGtqzd",
				null,
				"http://www.zodiackillersite.com/viewtopic.php?p=48017&sid=f565b035139f1ebb908542b1a208303d#p48017",
				true),
		new Cipher("doranchak: multiobjective evolution cipher 3", "82(1UyKbjJ.#5BR+3+29@TSp1l-^NBtHER+B|JLY9OzFR(4>bl*VLk+FU2)^RJ/c5.DOzB(WH9MNR+|c+.cO6|5FU+<+RJ|*b.cVOL|5FBc)T(ZU+7XzR+k>+lpyV)D|(#kcNz):69Vp%CK-*<WqC2#pc-F_2B8+>;ZlCPTBU-7tLRd|D5.p8O)*ZM6Bctz:&yVOp%<K+>^CFqNLPp*-WfzZ2d7;kl<S^+/|dT8f4YK+WGj4EyM+WAlH#+VB+L<z|4&+OkNpB1V2Ff/)z+Mp_*(;KSp2(^GO+FBcMSEG3dWKc.4fG5pDCE4GyTY+_BAdPzp|+tFMPHYGK+F6pX^2", "plaintext [OPEN__________WERENOPRISONERATALLWETHEYFOUNDWETHEYHUNTEDHIGHWETHEYHUNTEDLOWAWETHEYHUNTEDHEREWETHEYHUUNTEDTHERETHEMANWETHEYSOUGHTWITHANGXIOUSCAREHADVANISHEDINTOEMPTYAIRTHEMANWETHEYSOUGHTWITHANXIOUUSCAREHHADVANISHEDINTOEMPTYAIRENTERWILFREDFOLLOWEDBYLIEUTENANTLIEUTASTOUNDINGNEWSTHEPRISNERFLEDTHWILFREDRHYLIFESHALLFORFEITBEINSTEADWILFFREDISARR] tokens [[OPEN, WERE, NO, PRISONER, AT, ALL, WE, THEY, FOUND, WE, THEY, HUNTED, HIGH, WE, THEY, HUNTED, LOWA, WE, THEY, HUNTED, HERE, WE, THEY, HUUNTED, THERE, THE, MAN, WE, THEY, SOUGHT, WITH, ANGXIOUS, CARE, HAD, VANISHED, INTO, EMPTY, AIR, THE, MAN, WE, THEY, SOUGHT, WITH, ANXIOUUS, CARE, HHAD, VANISHED, INTO, EMPTY, AIR, ENTER, WILFRED, FOLLOWED, BY, LIEUTENANT, LIEUT, ASTOUNDING, NEWS, THE, PRISNER, FLED, TH, WILFRED, RHY, LIFE, SHALL, FORFEIT, BE, INSTEAD, WILFFRED, IS, ARRESTED]]", null, true),
		new Cipher("doranchak: multiobjective evolution cipher 4", "zkLBlp^tW)FP2OX+<;5q>KG7DR&V%jYtk+dO(|5FWKLclp-42#lMLC<5BNtG3DX*|JRf+DzRO8#pcS^+|JH(TVK(ypM*Vc.b#JdB>&FBc|5FBc4+>LRBV+OY-*4+yp.#/K^B+4GWR2|cqzUb6;+Ed+cT#-Z3M|y582_1:fp49-YCFP*-O2_Rb+)SkB>8HUGBM+Z).VEj6T^9*|fHERO+G2KS/klF)D8.z(A5Tl|N:VUzWO++k2d+9F^LcC(zKf4+YNO<p7UWz<p;(1zZ%7|Ak(p2.LF++FMCcB|H<pdUC2JT+GcWlZ5M+y^SM+_NP)/tBFO^+1l*N+yK@O9.<R6B", "plaintext [LOVESHOULDENCOUNTER_____________ONEOFTHELOVESWHICHSAVETHROUGHOUTTHEWNOLEOFTHEMONTHOFAYOFTHATYEARTHEREWERETHEREINEVERYNIGHTINTHATPOORNEGLECTEDGARDENBENEATHTHATTHICKETWHICHGREWTHICKERNDMOREFRAGRANTDAYBYDAOYTTWOBEINGCOMPOSEDOFALLCHASTITYALLINNOCENCEOVERFLOWINGWITHIALLTHEFELTCITYOFHEAVENNEARERTOTHEARCHANGELSTHANTOMANKINDPUREHONESTINTOXICATEDR] tokens [[LOVE, SHOULD, ENCOUNTER, ONE, OF, THE, LOVES, WHICH, SAVE, THROUGHOUT, THE, WNOLE, OF, THE, MONTH, OF, AY, OF, THAT, YEAR, THERE, WERE, THERE, IN, EVERY, NIGHT, IN, THAT, POOR, NEGLECTED, GARDEN, BENEATH, THAT, THICKET, WHICH, GREW, THICKER, ND, MORE, FRAGRANT, DAY, BY, DAOY, TTWO, BEING, COMPOSED, OF, ALL, CHASTITY, ALL, INNOCENCE, OVERFLOWING, WITH, IALL, THE, FELTCITY, OF, HEAVEN, NEARER, TO, THE, ARCHANGELS, THAN, TO, MANKIND, PURE, HONEST, INTOXICATED, RADIANT]]", null, true),
		new Cipher("doranchak: multiobjective evolution cipher 5", "G#d>pXCOfcVTFRy)+d2JzB-M61lWH+Z9L+#VAWGk2M)>^+B7NpUK;<4.jP2zZ_9;^C:DUK41Wy#+Y+(.+DtC+TMZVlk5XKzU#FKkpM.(zOcHERq8l>5_FZP/O+YKGTJRL|pyLfBb2^TFBct|5F-zVk*6+(|+DRJ|*%yl1+5SdB-S&K<k|URLpV(5cO-84@l+2/R*WNGBq<MO>7F9(ANCJ)c+DO/%MW2Yp|p)4O3EG6OF2+tN8-*cYlGNMB(bpj3zfK|4+RPOF):B.8l4*-f2;py|<^Cp/cEL&|+OHzc<*B+b.cV<#27^BL_1CWS+R9SU+t%+zO(FB+|5FBc;.5+z", "plaintext [ATOTHERREGIMENTS__________________TILLATLASTTONLYHISWHITEPLUMESWEREVISIBLETOROSTOVFROMAMIDTHESUITESTHATSURROUNDEDTHEEMPERORSAMONGTHEGENTLEMENOFTHESUITEROSTOVNOTTCEDBOLKONSKISITTINGHISHORSEINDOLENTLYANDCARELESSLYROSTOVRECALLEDTHEIRQUARRELOFYESTERDAYANDTHEQUESTIONPRESENTEDITSELWHETHERHEOUGHTOROUGHTNOTTOCHALLENGEBOLKONSKIOFCOURSENOTHENOWTHOU] tokens [[AT, OTHER, REGIMENTS, TILL, AT, LASTT, ONLY, HIS, WHITE, PLUMES, WERE, VISIBLE, TO, ROSTOV, FROM, AMID, THE, SUITES, THAT, SURROUNDED, THE, EMPERORS, AMONG, THE, GENTLEMEN, OF, THE, SUITE, ROSTOV, NOTTCED, BOLKONSKI, SITTING, HIS, HORSE, INDOLENTLY, AND, CARELESSLY, ROSTOV, RECALLED, THEIR, QUARREL, OF, YESTERDAY, AND, THE, QUESTION, PRESENTED, ITSEL, WHETHER, HE, OUGHT, OR, OUGHT, NOT, TO, CHALLENGE, BOLKONSKI, OF, COURSE, NOT, HE, NOW, THOUGHT]]", null, true),
		new Cipher("doranchak: multiobjective evolution cipher 6", "BT;+cqtp/+2F4bk*|5FBcK<O#y)HER6SW89+JlFM(2|H45R.zZ)d9|k2C5pWK&+zc9+LG;+tLf>W^zjBd>-<c4N#OVDA+zSYCU1NB.F^pNY+OT+K%pk69+_*L(MdVc.bUXR@l2#BO+5Rlc7t8B4|MpD5T-NBJ+.2^+|ylCp1<-O+UpMbdSY2#j<Ff)zVZ2TEUFW)>(&zN+:cB+kCyl3MO(@W*|JRHzLq7pl+4GlCX|%.*-OM#>7cp/GD6BJ4^|T5(HtKP+)GGOdR(^8BAKWVL.;+FBcSPFDy62B/pKP|MZY1f+UcF+<+8p:-*fL/Ez+_3^|5F2O<(V_+yOG*KzRk", "plaintext [ASBEINGONEOFDANTESFAITHFULANDATTACHEDYFRIOENDSANDWASHENOTSOASKEDTHEABBEGASPARDGASPARIDMURMUREDTHEWOMANFROMHERSEATONTHESTAIRSMINDWHATYOUARESAYINGCADEROUSSEMADENOREPLYTOTHESEWORDSTHOUGH__________________EVIDENTLYIRRITATEDANDANNOYEDBYTHEINTERRUPTIONBUTADDRESSINGTHEABBESAIDCANNAMANBEFAITHFULTOANOTHERWHOSEWIFEHECOVETSANDDESIRESFORHIMSELFBUTDAN] tokens [[AS, BEING, ONE, OF, DANTES, FAITHFUL, AND, ATTACHEDY, FRIOENDS, AND, WAS, HE, NOT, SO, ASKED, THE, ABBE, GASPARD, GASPARID, MURMURED, THE, WOMAN, FROM, HER, SEAT, ON, THE, STAIRS, MIND, WHAT, YOU, ARE, SAYING, CADEROUSSE, MADE, NO, REPLY, TO, THESE, WORDS, THOUGH, EVIDENTLY, IRRITATED, AND, ANNOYED, BY, THE, INTERRUPTION, BUT, ADDRESSING, THE, ABBE, SAID, CANN, A, MAN, BE, FAITHFUL, TO, ANOTHER, WHOSE, WIFE, HE, COVETS, AND, DESIRES, FOR, HIMSELF, BUT, DANTES]]", null, true),
		new Cipher("doranchak: multiobjective evolution cipher 7", ".53lW+1p2VzZ_d/^C4p+O1^*;2UfL|5FBc+ZP-pC<)NKD#Rb8lH9k6z<NLcFyCW5+%p4S2.FXNzfR+V%+dJc+dMctdB+V4+kSRJ|*|LFpb.cV5_KzBMMT|+R4HPk2cMCH4GOyGJ7z+2Ft#.+*9cB^|)RT-SVz<Mb2LHER8+ZA-;p&(.+q#/:kjF^W1R+cK_*-z#YB+U5BU.yp8LO|92|^>tM(N7TWKS+B8R-*l#pk;_@D:TGOd2(7UHB+>69(p+|GZ&2/FENX)+OY>Kfc^B*jB5<LUD)|5FBc+WJFK+(OBlqT6YfAWz>3EFM(|*Kp+(D)+ptGY<COO4y<llOPGOl", "plaintext [IUNLESSITBBEOFIMPRISONMENTCADEROUSSEWWIPEDAWAYTHELARGEBEADSOFPERSPIRATIONTHAT__________________GATHEREDONHISBROWBUTTHESTRANGESTPARTOFTHEBSTORYISRESUMEDTHEABBETHTDANTESEVENINHISDYINGMOMENTSSWOREBYHUSCRUCIFIEDREDEEMERTHATHEWASUTTERLYIGNORANHTOFTHECAUSEOFHISETENTIONANDSOHEWASMURMUREDCADEROUSSEHOWSHOULDHEHAVEBEENOTHERWISEAHSIRTHEPOORFELLOWTOL] tokens [[IUNLESS, IT, BBE, OF, IMPRISONMENT, CADEROUSSE, WWIPED, AWAY, THE, LARGE, BEADS, OF, PERSPIRATION, THAT, GATHERED, ON, HIS, BROW, BUT, THE, STRANGEST, PART, OF, THE, BSTORY, IS, RESUMED, THE, ABBE, THT, DANTES, EVEN, IN, HIS, DYING, MOMENTS, SWORE, BY, HUS, CRUCIFIED, REDEEMER, THAT, HE, WAS, UTTERLY, IGNORANHT, OF, THE, CAUSE, OF, HIS, ETENTION, AND, SO, HE, WAS, MURMURED, CADEROUSSE, HOW, SHOULD, HE, HAVE, BEEN, OTHERWISE, AH, SIR, THE, POOR, FELLOW, TOLD]]", null, true),
		new Cipher("doranchak: multiobjective evolution cipher 8", "(Gkj|qC5MY>Ey4pP*tT3FLK+Hp2)Bz.c_GMJ#6fPTNp+&^CFUqZ+D++O++U/c+|5BX<Tt+.E6RlKS#@d|5FBc+p+YByzOtFHERtNW)#zZ(9|7cCBO2)81B+_7SL4.ylB(p<9Mzfj^A4;#Y*2|kH+Z<W+8-+HO2/9B*-+^l8(/-<pODG(K*zLf2WcF|+N#cRFJpyLXlVbN;O4z>Ck85(-*P+BW2c3|^LMWT+GN|lkMp%^V5O4-1R>K6G5DKz+Ud;OVpARJ|*FlVVkb.cV++Gp2RL|S7T:|5FBc&W+1.)zJ>^FpY(dC.cKB)2OURB:+_fSZ4bylUDp29MGO<<K%zdM", "plaintext [PACKEVERYTHINGINTOTHESESAIDNATASHAYOUCANTMISSWEHAVE___________TRIEDTOSAIDTHEBUTLERSASSISTANTNOWAITOMINUTEPLEASEANDNATASHABEGANRAPIDLYTAKLNGOUTTHECASEDISHESANDPLATESWRAPPEDINPAPERTHRDISHESMUSTGOINHEREAMONGTHECARPETSSAIDSHEWHYITSAMERCYIFWECNGETTHECARPETSALONEINTOETHREECAASESSAIDTHEBUTLERSASSISTANTOHWAITPLEASEANDNATALSHABEGANRAPIDLYANDDEFTLY] tokens [[PACK, EVERYTHING, INTO, THESE, SAID, NATASHA, YOU, CANT, MISS, WE, HAVE, TRIED, TO, SAID, THE, BUTLERS, ASSISTANT, NO, WAIT, O, MINUTE, PLEASE, AND, NATASHA, BEGAN, RAPIDLY, TAKLNG, OUT, , THE, CASE, DISHES, AND, PLATES, WRAPPED, IN, PAPER, THR, DISHES, MUST, GO, IN, HERE, AMONG, THE, CARPETS, SAID, SHE, WHY, ITS, A, MERCY, IF, WE, CN, GET, THE, CARPETS, ALONE, INTO, ETHREE, CAASES, SAID, THE, BUTLERS, ASSISTANT, OH, WAIT, PLEASE, AND, NATALSHA, BEGAN, RAPIDLY, AND, DEFTLY]]", null, true),
		new Cipher("doranchak: multiobjective evolution cipher 9", "PH3lW+(.GVzZOd;^C4p+_/^*EMUfL|5FBc+Z&p.C<)NKD#2bWlf9k6z<NLcFy%85+Cp4S2.F1NzfR+VyBdJc+dVcRdB+:t+kDRJ|*|LFpb.cV5_KzBMRT|+RtHpk2cG%H4GOdMJ7z+2F4#.+*9+B^|)RT-SVz<Mb2LHER8+ZA-:p3(.+q#;/k^Oj8X2+cK_*-z#(P+U5BUpyp8L-|9O|^>tMTN7(WKS+B2R-*l#pkX_@D/TGOyMY>UHB+Z69bp+|G>12;FEN1)+O(7KSc^BtjB*<LUD)|5FBc+WYFK+YOBlqT6(fAWz>5EFMY|*KP+pH)+;42(<COO4y<llF&GOl", "plaintext [IUNLESSITBBEOFIMPRISONMENTCADEROUSSEWWIPEDAWAYTHELARGEBEADSOFPERSPIRATIONTHAT__________________GATHEREDONHISBROWBUTTHESTRANGESTPARTOFTHEBSTORYISRESUMEDTHEABBETHTDANTESEVENINHISDYINGMOMENTSSWOREBYHUSCRUCIFIEDREDEEMERTHATHEWASUTTERLYIGNORANHTOFTHECAUSEOFHISETENTIONANDSOHEWASMURMUREDCADEROUSSEHOWSHOULDHEHAVEBEENOTHERWISEAHSIRTHEPOORFELLOWTOL] tokens [[IUNLESS, IT, BBE, OF, IMPRISONMENT, CADEROUSSE, WWIPED, AWAY, THE, LARGE, BEADS, OF, PERSPIRATION, THAT, GATHERED, ON, HIS, BROW, BUT, THE, STRANGEST, PART, OF, THE, BSTORY, IS, RESUMED, THE, ABBE, THT, DANTES, EVEN, IN, HIS, DYING, MOMENTS, SWORE, BY, HUS, CRUCIFIED, REDEEMER, THAT, HE, WAS, UTTERLY, IGNORANHT, OF, THE, CAUSE, OF, HIS, ETENTION, AND, SO, HE, WAS, MURMURED, CADEROUSSE, HOW, SHOULD, HE, HAVE, BEEN, OTHERWISE, AH, SIR, THE, POOR, FELLOW, TOLD]]", null, true),
		new Cipher("doranchak: multiobjective evolution cipher 10", ".pEk-lT<PF*9C+B^At^qUG|yVc+BKZl(RTH-*V<NF#pcC2L5WKBG@|tN)Oj|2J#+SBcK>4kpbM+<Mz9dVp*N2O1X6Y#5pOCLOdc)K+<_&*FBc5ORNF(UU%ZpBM+^^L>>;6ctYL2+OTyzZ)8A|K_H:WFB4YfC5Ob.cV^WlM2>9+-tyS;F/Bc<O#HERp-LlVGfUp(.fz4;W%8dz)kRKE7+b|5FS41P(O#|+T3GR&_/<.l9UCWzLHO+BdpD++J+++B+f++p8^%*.7RJ|*T2+b4qz|YjWG5D(F|)NzKMF+*-k.c+(P8JM+24l.zV6/y|5FBcRyd1NpR(:lk73XSGH+BM", "plaintext [ATTEEMOSTTHEOTHERREPLIEWWITHSOMESOMEHESITATIONANDSHIVERINGBENEATHHISFEETHATSAREALTHINGYOUCANTGOAGAINSTSUCHTHINGSITELLYOTHATHEAFFAAIRCANTGOWRONGRESUMEDTHECLONGHAIREDMANFATERWHATSHISNAMESTEAMWILLBEALREADYHARNESSEDTHENTHEYBEGANTODISCUSSAMELODRAMATHAT____________THEYHADSEEHONTHEPRECEDINGEVENINGATTHEGAITETHEATREMARIUSWENTHISWAYITSEEMEDDOHIMTHA] tokens [[AT, TEE, MOST, THE, OTHER, REPLIE, WWITH, SOMESOME, HESITATION, AND, SHIVERING, BENEATH, HIS, FEE, THATS, A, REAL, THING, YOU, CANT, GO, AGAINST, SUCH, THINGS, I, TELL, YO, THAT, HE, AFFAAIR, CANT, GO, WRONG, RESUMED, THEC, LONG, HAIRED, MAN, FATER, WHATS, HIS, NAMES, TEAM, WILL, BE, ALREADY, HARNESSED, THEN, THEY, BEGAN, TO, DISCUSS, A, MELODRAMA, THAT, THEY, HAD, SEEH, ON, THE, PRECEDING, EVENING, AT, THE, GAITE, THEATRE, MARIUS, WENT, HIS, WAY, IT, SEEMED, DO, HIM, THAT]]", null, true),
		new Cipher(
				"Smokie Vigenere keyword ALPHABETSOUP",
				"ABCDEFGHIJKLMNOBPQRSTUVWXYZMabacdefTTgChAieWjkljmEnWmFdHIoKpBiqrsfetuvmoXeOwDQndxynNTObizUGPFpgfVIZrhUr0j1VWqwCxqdagklRAUpCyPbac2Kf3mDpMLQBQJWfbGlKqiZLvIl114NdoEkAsTa1AZOb1XVc0FWwsYaMTsQ0GufumURsgUIUPsblFZHnW4jpEPLweHOA2uyCW3nmCyXoKitF0coCSEYPNFdRSjkpIDg0TMvpYpn2YzwkKPG0hJ2fNLMlNFdAoVncqvMDpGceDrlTrHOCOAdNs4KuSpMmpTm1syzgeXmNfaQ1WGr1hPX4R",
				"i like killing...",
				"http://www.zodiackillersite.com/viewtopic.php?p=48176#p48176",
				true),
		new Cipher(
				"moonrock1",
				"I79,L8#;T<WJ2.4QG1/ZNAD{-0W$U4OCYJVXK\\9;TTS:;27L_\"R3,4)2<6AD;Z?SF/8{=X&0_[F-G&N74_*,0\"+.NJO44D:]WTTKN7R0<V\\_T}&UOG,4<F#TTO0'P4D4270QH\"+VG=X_8MZ44}7TC;Z']/63K0#1%AJ#GSH2:DIB;*^4CI27JZ2DUVR,%8H(/]08WI2\"B;);9,T}U.>)6-4EYR}:.D45T=0*P1F#Y4]G&#Y[.1EJ*K&:6;/Z8=KT]()/6#DC[B_&;+}\"A+2]G)B6^Y*$E4G#-?L[)ZE4#:A>9PDYL}934B\":(1Z#(TNG)V6^79)B;-=ESJ5-CH;R",
				"",
				"http://zodiackillersite.com/viewtopic.php?p=50012#p50012",
				true),
		new Cipher(
				"smokie palindromic cycles 1",
				"#.OW|Y%<*DyNS1U7KLpT^H/_G%5O9tZf-VB2/:#F#j)z8Td/y&(EMWO*<%c+;D.PNRfL%:4B12J38|t&l|^>-91fRyO_FBL)z_5kEyNT8Z/9H:#fH.U2E<*5UW#K*tZfGF(Oc+N%CTj)L&B1fzt4&B3D<.%MN|@S|81c^LOF#9TqG:pT55L8F(Ry+1&4#yNbJ/38zVY_Zd2XO&B>N%8.FB|p|_5SR35U2DF%9&(E4;B1:OP#L#;O<*lT8GpU8c%M7H3HPD^TEckRK*&BL%(>C1WO<.|PX#.<p|^Zf1)At*RCLj#;OK*:zF+%qTbJ-fbyEfGlTVH/tLAz/;D<.58b",
				"",
				"http://www.zodiackiller.net/viewtopic.php?p=50823&sid=5d7003defe5518140d9825e660eb4ff9#p50823",
				true),
		new Cipher(
				"smokie palindromic cycles 2",
				"#.OW|Y%<*DyNS1U7KLpT^H/_G%5O9tZf-VB2/:#FOj)z8Td-c&(EMW%*<Dc+;%.PNRfLO:4B12J38|t&l1^>/9L)Ry#_F(Tfz95kE:+G3Z-tHcO)E<UdR*KFZY%*<jzfT&BDMN+%CLt)14(|fJ9&FB8O.<#cN1@SL3T:VGO5%_TqLyp1F&|84(>:+1&FDcNbz/38Z^W9U2dX%5BR+O3*F(LlTt&7E84Z2#&OjFBH5;(GM%PDT%XOK*pL31lz8c#:SE3RkOV|>yPR<.FB1%(ECLYD<*Tk;%K*pG^J)TfAt<HqL9OX#.<:z&NOC1bZ-)AcEf|l1VR/_LbU-;%*K48A",
				"",
				"http://www.zodiackiller.net/viewtopic.php?p=50827&sid=5d7003defe5518140d9825e660eb4ff9#p50827",
				true),
		new Cipher(
				"largo names-only cipher 1",
				"XwQbZKDEIXVxnmfsxdlh3oCLqHZwFgvMzTJSEeYaiNOaDcmIhjojpkbVRrqGwFXnSxDCAIEtHvdgGVhkUyeMzFBnJSfZPaiYcW1jrOEplnwsmLNDtAdoqC3BrHRXPMYIgVU1kWCHbv3zwMaJFSeyf3lAYLuiCZdO3BxjmRupHXgoGDsEbjeIVNiMcKFwYjpCqtSbankvNzEFxDHMcSIrVa3waJUaejADOdcmEZYoF3PxjquIgNvSzki4VnJyrOEC1fmpHMbNNenoYqXijsLhRwCajdDhcAgpHjbMZxkvzaJdgeYICHnOFMijmXhYjVCZwHpokubdgeDaU3IMqXWv",
				"",
				"http://www.zodiackiller.net/viewtopic.php?p=51025&sid=176822cec68e365110ffa201f7c8ed2e#p51025",
				true),
		new Cipher(
				"largo names-only cipher 2",
				"XwQbZKDEIXVxnmfsxdlh0oCLqHZ2FgvMzTJSEeYaiNOawc3Dhjmjpk4IRroGVFXnSx25AwEtCq9dGDhgUybHvFBnzSfZPaeMcW+jrJEi7nIsO8NVtAk3mY0lr5LXBCH29wRUdPMYpo0qD5avFS4yf0WAC+ubHZgz07xjJ8ueMXkOGIsEijpV2N4YcKFw5jbC3tSean9mNoEFxDHMcSIrVa02aqLaijAwvdczEZYJF0lxjOuDgN3Smkp1InoyrqE5Rfv4CHbNNenzMJXijsUh+VYaj92hcAdp5j4CZxgO3amk9bHwMYnoF5ejqXhCjDHZIMivdupgk4Va802YzXBJ",
				"",
				"http://www.zodiackiller.net/viewtopic.php?p=51025&sid=176822cec68e365110ffa201f7c8ed2e#p51025",
				true),
		new Cipher(
				"jarlve z408 with random columns removed",
				"9P/Z/UBkO=pX=BWV+GYF6HP@K!qeMY^UIk7qTNQYD)S(/9#POAU%fRlqE^LMZdr\\pFHWeY@+qGD9K)6qX5zS(RNIYlO8qGBTQ#BLdP#B@XqHM^RRkcZKqI)Wq85LMr9BPR+j=6\\N(EUHkZcpOVW5+L)l^R6HIDR_Tr\\de/@JQP5M8RUt%)NVEH=GrI!k58LMlNA)ZPzUpA9#BVW+VtOP^=SrlUe67zG%%IMk)cE/9%%ZfP#BVeXqWq_#8+@9A9B%O5RUc_dYq_^qWZeGYKE_TA9%#t_H!FB9zADd\\7L!=_ed#6e5PORQFGcZ@JTtq8JI+BPQW6VXrWI6qEHM)UIk",
				"",
				"http://www.zodiackiller.net/viewtopic.php?p=51014&sid=176822cec68e365110ffa201f7c8ed2e#p51014"
				),
		new Cipher(
				"jarlve 18% cycle randomization with his other encoding stats closely matching the 340",
				"HER>p>l^EVPk|1L|^TGp2dN+1B(H#OL%DWY.N<R*lOKLf1)DPy:d<>VE^BPcMHEUZz%JR<SY17L83J_9tpj5N#1KdPl+(YJ%L#*Fz<c&4LDO5PVKd^L)zE^yL>BE^+L%TS:HPZkR/1OKJ-Y&%L_C9:flE^V<cTq|TfpPWpB(HO1/J<G&*y&4SY5PZT-CR<k;LN83L2>+L77Xl9:dcV4E(Ypt1O*|zfyL.BSH_*:5y@YJPRbl&VAB^^GT8pGL3<HP|d4zUHj&5<FdEES:TBYz/p>H^E1UMR^EtJWLK&%6+^5/T#lXVE^PL-ZB/p;LDK;<d%1GJ2zNO&6LD@HE^yf;",
				"",
				"http://www.zodiackiller.net/viewtopic.php?p=51222#p51222",
				true),
		new Cipher(
				"jarlve non-repeat window 15, 25% cycle randomization, unique sequence peak of 31 at length 19",
				"HER>pl^VPk|1LTG2dN+TB(#Op%DHWY.<*Kf)y|R:^OcGMTU#ZzfJZ>kdS%|78HP31(_TRZD9Nt.jMpY5+NFJ*OT<(|kW&fpcGY:4JZ1Tj.yO(|%_(dG/JSEDG-^VPY.<N59kZ71HCpOcT&fp_GW:z9MRPd^|7TqLNjpZBT%:HYN;N|+pzDNj5f(Z1T&:R|7X.#MjGKlO.)U@^z9J1kMSDfp+TO52(j&Gt%:HWzfJD89NZR3^pk@%EV+TMN+Gj|RZL(MJ4Hbp(Z3JPd59TRfJANl^SEp48kVP+Tb.<NcXYE(6pOk@RVP|.&7^ATXG*_XZJ<N+pF(yYTX.#8kdS:jX",
				"",
				"http://www.zodiackiller.net/viewtopic.php?p=51230#p51230",
				true),
		new Cipher(
				"jarlve mystery cipher with a randomized plaintext.  2 seperate encodings, one from position 1 to 170 and the other from 171 to 340",
				"HER>pl^HVPk|1pLT^G2dN+B(#O%RDWY.>+N<*KfHN)y(:cMk1UdRZl>zJHS7N8f3_N9Ntjp+|^2#N5(lRF>MN+HONY&W7c(L4^*1/pt-C#2lqRL^GH>d*+Nk;2(X.%pflR)U@kb3L9W*HA^:2kl^|pc27|YGNl^2>U8M/1E&HpD_#l:yZckEW876qVXljHPCc&>TF%z1J8zY3K^JzJlc;lUNL*OGckl5-S4T._:YW|2qTb@z.y5)bEPJRYKq<c)TAlG/zMtcCYl6k>:yW9dYcJpzkPq@kkjT+(ykJzY3qP*JVT5Y^kGOl;d)BqS2+z.9V>lNcj_dl3Yq:byyV|MW",
				"",
				"http://www.zodiackiller.net/viewtopic.php?p=51258#p51258",
				true),
		new Cipher(
				"jarlve solvable cipher",
				"HER>Hpl^VHPk|1LlTG2|d2N2+GB(#O%DWY.<*K1f)y:cpM|UZTEO2zJS7G*<|1*>8G<>H>1GR#1G3(E|<P2*L^##|^<H|%2>_91ptjK5FM&N*4>MF*/HkJ7f-HRVpjPCLtdpBy)q;%>+8XY)@.41p*&>)MpCLW|HpKb>3/GZ^-9c%OPc1UG7Azd|jyflPLXbWG<*Z2p&p#c6./5>H+)z<#%_*_-Xb1GE<2PZkNJ^M;E3VY:yfj#|qO|&2G8tZpAp7HS*W7G9j<L1c>|pHC3HUc+Gt;Mc8k%JWO*B1P)|.dJZH&yZT2&3K:2*&>Gq/@5F)-1L;VX>|Dk^14>|f*<9",
				"",
				"http://www.zodiackiller.net/viewtopic.php?p=51266#p51266",
				true),
		new Cipher(
				"smokie39",
				"HER>pl^VPk|1LTG2d1N+BG(#O%DHWYkd.D<^*>Kf)y:BcLBMUZlzEYJ1^|p1)PTJS7zDOGV83Jc2z_9tjH>dN%+5(F^&M.l8Yy4/JB)-/^5^TLUE^C4POq;:p&*XZV@B%ES^b|Tt(Ez^|8GYOE.f<4Uj2MK^Z8T/.lDLM-GTNf+JqCVA^WYkMjf):z^^1%Yl6B9TqjD*ERlUt:Y^G74LMjDGOM2fSUy^3B6&4;D/>*-SC2X.@bd^(^RBS:^Wl1pbPT^V%BF7+>*>DYZ66GGcC(Ay-T2S:^NB^85.DGzJ*EUpHU(y^M8f-+Jc^qXJ|CTB@Af^#YDRObc.77H*@zbU",
				"",
				"http://www.zodiackillersite.com/viewtopic.php?p=51515#p51515",
				true),
		new Cipher(
				"Jarlve, old lady killer",
				"HER>pl^VPk|1LTG2dN+B(#O%DdWY.R<*LKdpfN.)y:cMW+D*UZlLE>O|fz^cdRVyT*pJS7N.:YL>NU8l3)_1P8D#|9tjE5d*LVZMHPRFPpD)2%RN#zdE&4S*/PW-.#CGpS4:y|N3.:_D>|E)NPq.91c+fL:(dSY43NWZU^*.:#lHcK4tRTV;4%Mp>OJl|VXD1S2yTZNL&3.f:EZdc%:@J1cM4G-2FR.P:4)4Z7>Sz(S4cW)*/Yylp3DR|LdTZFNWObA)68)#J#UVq.:4kEH1S%cyPp.23fZ1*UD:Gc/FRLZA#cS3)4HpbE%WD)Z>WRylVb/L|N1M:Z+P#4Y>EpDK",
				"",
				"http://www.zodiackillersite.com/viewtopic.php?p=51514#p51514",
				true),
		new Cipher(
				"smokie40",
				"HER>p>>pHpl^VH>PHk|HV1l1^PLLT>^GV^G2dlGHNT>+1pH2ll>d|RH+VVRpL>1Lpd^+pBldVpp>G^>HH+PN>>GpLVpTL1LHlHdHB1>ldHlpGRLVRLB+1G1HHl>GVG^HRBR1H+>H>>d>1|H^dkpVdERHB1LP1GLRPHRpHNLHRdGVdH>>l>dHlllVH^Hd|>GL^(1>pV+N1TVL1+k12V++d^d>TGdLH>RlH>+L>^>HGBG2lN|H>1|lp1lVp1HHLdlp1GL^RHR>LLL>H^BB11lRLVNHRGH+L+pL|^HHRlR>TV>HTBH11l|L+lHlkpGLV(R1G1LV1l>H^Hlp1G>HkBl2",
				"",
				"http://www.zodiackillersite.com/viewtopic.php?p=51665#p51665",
				true),
		new Cipher(
				"smokie41",
				"HER>pl^>VlPk|>1LLTG12d>N1^+21|N^T2122^p^+1p^>>N>1RLELd>1VBp21E2|^+E+(Bp|LLd2VL^lEL1V(pGLlP>N^>+G>E1N^^G1(1GEEG2^R^(G^>L(#E>NLkBd2lVG^>PNl+^+1L^+RL>+^E>1G^>^G(EP++G^P1NTPE^^11pdP#^LVd1+^1NG^L12NLO2L|2RG>L>G2+^L1+BR+ENL^N+^^2>G>O1NG^RO^GpG>>^1#+V|>d>dP^TPR2+>E2GL+dEN>LN+2^LP2>L>+^l11EE^N^O>+^R1LE>dGE^G^L1L^N^N+RN+E11BGE^N^|dENN+d^^>^pd^d>>p",
				"",
				"http://www.zodiackillersite.com/viewtopic.php?p=51668#p51668",
				true),
		new Cipher(
				"smokie42",
				"HER>pl^VPPVkp|1VLT|HpR>V^l>TGL2dpV2HN+HPPVkdk|V+|lNkPLBVNBL>|2PLPLL+1pBdH|BdV1B1V>ddkLl|HN>TH(>d>l>H|lPd+l>||k>VV|HppTN1VpB>RLlL|+1LN^dVGd#B>VVVOVLllllHdPL|llHP|k2VV>p|kp2V|LRRNBLP+PpP2VGHd^L|EV|HVHHd|pHVkPpdVLRlL>NGkkHlHHPVLG2V>Bd1|2lG2Bk^>|VpHdHVplHd2LBVPHdLP^H>T1R>pE2>dBpB2VHl^ORd2P^VVBpH|R^|kpO>LkTdV+THP>H>VV|p>dVRVklkTd1^VkkLdTpPL||H",
				"",
				"http://www.zodiackillersite.com/viewtopic.php?p=51668#p51668",
				true),
		new Cipher(
				"smokie43A",
				"HER>pl^VP^k|1LpH|^V1H^Tl|L^Rl>PpVGklE12d^pkG|^>1l>pdGNk2HHR|PPTkHRR^l11VNNHN+G1HR|^^kRklBBlHBRTR|R|HH^R|Hl>PRHkBV|TVVHpHTHl|N>ll|LLR^B1TplB^pV1T1T+lRpl2T>klE|VB1pTplEHpTkkllVG^HNlBGV>TTHRkHE|H|+T1Tp>k2RTVTLHNE|Bl>VlG^Vp2T2R1>HpH2|B^HlTRlpkTPkllpN(llTV|pHVplE>RlHV>RkH^1^lpBkp|lTHH>^GTR|HHlTGElPTEEB2TH(||pRT>NR1ERlp#dGOlp#RlHV^>TT%LH>>THpVV",
				"",
				"http://www.zodiackillersite.com/viewtopic.php?p=51710&sid=439a2e23cc58459fe4f6e2cbab0acaee#p51710",
				true),
		new Cipher(
				"smokie43B",
				"HEERE>pEEl^HVRPlkV|HR1|PHRVpEHLETVEPH>l^GlT2EEGTl^2EPEH2lVk1P^HHpG^V2HTGdV^G1N>LHHLdp+RBp|2HG(#d#|BE#VLGEEHRpdHRPRpp^BEGTHlELpHH2G^^|L^p2kPd2HGRHPpHGTp2VdE1lHNORRLplRGHkVB2Vd^p^RLELlP|+^kpGlPVG+RLP^VGR%RBEHH1Od^GEH^p+pd2lHGEH11VH^GR2RPP2RGRpE2plEPP1pHHVH1B^VB+pHHdV22dHdGOHPdplGRpE+P%RpVpH1^lGlEGpO^lRlREpRl2Vld^p2RLEl2HPlR^NRl+PG+PLLEE%GEE",
				"",
				"http://www.zodiackillersite.com/viewtopic.php?p=51710&sid=439a2e23cc58459fe4f6e2cbab0acaee#p51710",
				true),
		new Cipher(
				"smokie43C",
				"HER>EpRl^V>>P^kpH|1H^LHTpGHH>EP2R2dN^pHk>+NNNdEBTkRHG1H|pNT>TkkGkkkPNdN^Pp+(Gk1HNd+Hd2d>EHpTpGdpTdPHEH+1H>Hk^+1>NTTGVHN1^EHk1N1H>ddkHVEkBG1B>HTPTH2N1T1p+#V2pTRp1T>>^ENGlEOllLGTVPH2GPdPp(^>>NdVlE11HTBHRkpN1plHHTHVd1TddNVNHdNkkGH11VNHTHNTPEPk>HPddHERR1kEETEE2BR2HBBHH1(pppdkVBkV1PH>NTk+1PTk^NHdpPdpHV>pPpdHPNpd^kRH#>H#RVVHk>HVBGNGpdp^HkpHdPp1",
				"",
				"http://www.zodiackillersite.com/viewtopic.php?p=51710&sid=439a2e23cc58459fe4f6e2cbab0acaee#p51710",
				true),
		new Cipher(
				"smokie44A",
				"HER>pl^VPk|R1LTG2dN+Bl++d(HH(+#V22ON%DWH|YG.<P^V1H*<OVKTfk)>2dB^k.Hyf|P*GB|G:V^LHVlBY1OR%*lWpO|cHN>M*1#KVR%GOBW.U|G*Z+zL<DEHBYfL|GlJpk1l>OSV^*2BH|Nz%#G1lB|G.fBWO2TNPcE*kS+Y2<|G:%VUJBPD<:HL.fU7|^(pkNY^WG1ly:+VJ2k1%Ol+HWVZB8%Wd1N|*DL%RO.7PHVzlE*YW(>1O)l%W%WKzEHJyM2LVY*H^LO8)G<BY1Vkl%R1PH^fLVkHYpN2(LlW<+%.VHV|#H7*cfYLKJ>#PO^yGB1|8G.VlUN1HVWH",
				"",
				"http://www.zodiackiller.net/viewtopic.php?p=51726#p51726",
				true),
		new Cipher(
				"smokie44B",
				"HER>pl^VPk|1LTpG>2dHN|+B(R#EO^%DWY.Vl<*T#G+*KRfLN)pVy^TBP2l#:.Blc.MBpREGNLT^PU+#R(>*^1ZGzDdMNTE2fGNPJVlOTS.BZR^U2G#lYP#|RL.d^7kRpMNELd)^+BTG2P*>lNHzTGNMTlBB(MUW7<WP(+USG>:V2J#GRkN(+)KpPl:fd|Md%TGME>#dVJZ^R^MkNWl:p|2.VTRBPGJ>z^pdV*:.NR:)+^#lO2ZBT(S:R|>^#*pR%8+lkM^.PR2#PVWGNTJ7*2OB:cJ3:^GL1lpNPT#JER^HVGR+NLEp)T%^yL_GW*k+*D+NSV(2:).TJ:zUl|JG",
				"",
				"http://www.zodiackiller.net/viewtopic.php?p=51726#p51726",
				true),
		new Cipher(
				"smokie44C",
				"HER>pl^VPpkR|1LTHGP>R2dN+dB(p#O+Pp%ND>WPBYOp.<%*KP+f)yBpLd*PN1Op%:PEyRDcM+dBHWLO>HLU%1RfHp2G+V*PfL>YRl>RpB<%P>.B(NdZTkO%WB+VNOzREHTU%cd>BpR%><PWN<#VBLpW<P(pJY2yPEUp%|WBPK^R%d>1clpPYUzS)<+H#ORpP*W>pB%T+OVNdBNd(Y%7U|YDlNcL<Wy(<URZB+8THYPdVTV)Y2V):Ydz|G%P<cfT>%7zBUR(Gp#%NO1.|WZVWGYLGGlPp:dTUW+B<%B)VzT%PB%VWO+N*#B8HyYLK%OpJcHL+|PZU3HY<>pUTdD2",
				"",
				"http://www.zodiackiller.net/viewtopic.php?p=51726#p51726",
				true),
		new Cipher(
				"smokie45A",
				"HERH>pl^VEHPk|1LTG2dN+B+lR(#O+T%DWYP%^L.<*LK+fG)LyK^:cML(UPZzfJWSl7E8+UR3_9tj.%(5F2p1&yfK|4T#*/WWDd-klNTdCqR^3-ztL;1yXM4&(@.H_fWKE<Td7.lb+-BHNf)4/pFjck>Pt17zpbMH__^+TlZLFN|/E-NcRpf(/_2+:9*_f.BY5>f(BjO%f->Rt@(bt:MjNE_O^|c)7S/fL-lk@&UN@cL:yW>;p#+(XATD(q4>b1<B)G7t*^@CfW.Hff-BY(jyEzSMdk4bNppHJ2;CKAB>c^dfcFdzMEjW+3kG-(#|)b<Fy&)fOTl_y.tW>t1WB3K",
				"",
				"http://www.zodiackillersite.com/viewtopic.php?p=51731#p51731",
				true),
		new Cipher(
				"smokie45B",
				"HER>pl^VPk|1LTG2dRN+HB(>#1O%DW+Y.^<*Kf)y:c<M(LWUZTzJd)(S7R<G8Ep3^_9>p*t(j+1ZTc53#l.zd>FZ2M)ZUp(pY&O(M&WVkK74*/-BPFCfpZqDE>N<t(f:F+%cl;3UkkXZ12TCYtP5|dtSypV/O(ky*OR@Lt.y^b;Cq>B-UyDZ;1FHAkFTj|_GU2d&K#6dt>/X/OMA>%9;63qB./J:fW<TC3TqTD@UY>3|+DK1q*ck|9&TdR_HyKL7Z#_2AY5Ot6>RlWD8_<)(KE.3:VcC(X/p:FT5+LN@j%G#:&Ul4A^q9WZfB.dpP|N6L<^M2JObk-+L1#q^c:>1",
				"",
				"http://www.zodiackillersite.com/viewtopic.php?p=51731#p51731",
				true),
		new Cipher(
				"smokie45C",
				"HER>pl^VPk|1LRTkG2d>NT+|B(#O1%DHRWY.<Ll1*Kf)y:V)Op|E)cMHyU2ZzPJZS)78l3W_p9JT1DWMt(jT5VHEUFR&^Y.K4V/5N-yCkUyEl|S1(D8MHLq3FRL8;lkXG4Z97@UdTc)7W>d5lO/Yb3_.M+.yL^cVPEB>Mjl7JF(^53T2qyU^9#1MZV&:EW/^FTR.y8>OCAl#E4_Slt+Z1;D;VRBk*LS2<FYjTydcjTFLL1CHA96+Kl(3T59:7:W<yb&C:YKVB^LAE5c#C45fAFY)J):TP/yzU@KB917k6>5.DW8y>YSGTAzpTA)j)pF.zc;q3_XpLNSMyT<z.;*y",
				"",
				"http://www.zodiackillersite.com/viewtopic.php?p=51731#p51731",
				true),
		new Cipher(
				"jarlve cycle type test (randomized plaintext)",
				"+E7'D*!$3*.)HSF1$M&^*%Y6[ZU=($VQ,*$R+>)*'/KG$(*8V$BITC#X3>E14#]+H\\LX-3*=$)*@II^1HQ$[>!YP^<R\\U:T,<?$IO;#QK*5A\"$9I0&CE*^.+0D]M/JV$!K'*QR46)-4Z\"MI$8H1?9'?E&7^A?(!-B)\\V?Z+55A*Q_M9KX+G_/$2-4%E]R!;SFL)_<*8\"1I3&#N=#HL';Y$O0Z*F@[S!X.V\"':$*-^V\\:DK3UF,*E!8AB\"TQ$7K>[4=*[\"^\\H#$XME*T3C49+[]PG>-;%5#X*\\&N)T/RE1@?N3>2S\\IQ#]P$P'[JU*;O*:1@@K$*#$U:?RW2,Y*9E",
				"",
				"http://www.zodiackillersite.com/viewtopic.php?p=56744#p56744",
				true),
		new Cipher(
				"bartw test 1",
				"ABCDEFGHIJKLCMBNOKPJQIRSTUSVBQWOXEBYZSabWAVVBcNdeWIRfgRDZGhRifOABSjkiRYeekilmQDcUQnohpDGqrAApRshRdgtuirHRvwFKTSSxXRINqCFnBYKyBzwSRytZCgQuI0NsqXSO12LI3TdilF45IyBoUkxfrBr35bsGXlwPSldikIC3g2K2DJA0M05F2dgBW6hfuUnEwIIUAuiO7Lq3vSWwHXeUo0ainrOJaeHTYssX5JqF3f6RoxOaxeh53gnkxGpvArTNXnJrYezSPOKSFTEugDkxl1k65dSIiyk602yNDrxSbkL7HkxtpITB5DGhh6fOpY3cp5y",
				"",
				"",
				true),
		new Cipher(
				"bartw test 2",
				"ABCDEFGHCIJKEJBGLMNOCCEPQHCRQSTUVEHGWCGPJXYZWCCSKaQESbCcSJdEQTSUHCefgEJKKTQhiCjklSYJmanhoTpqrChLsStuSvTcHwSTHxYyqyClCIaTNHJz0HCSCEGTWWthSCWCHI1C2ljlWSXSQI3SEvJXh4TgTJlTSEXH56s3TyCxKTQN7JE8EONtSJjE9CT!iTFmT7!nH\"ISHMSljC9hSQWe3n#KHhj1KST6T5KDghmdhEAI!5C$7Gg3GEP8E9K1T8heCCmaC%&TyhWCYTjJCSB2xJRgqCHTCESCu6xTY3EJCTTgCXagCDTgGfh%HE!'m8&TjfGxQaET",
				"",
				"",
				true),
		new Cipher(
				"bartw test 3",
				"ABCDEFGHIJKLMNBOPQRSQIPTAUVWXYZabMcdeVfgIBhNiIQNjIkMlCEmBnIMopqAUVdIdUnbrcstoQCNXIYnOSqdnTbGbEPuvwDAIuTHDNxyolpFmzEJQ0GLfynvHNTbVMBS1HYT2QATH0bVN1loTzbhOsETUNzaAE3lkx1TTUsHozT4N5To6NYGoDIueGmJCN7UGEUTigIapoFHDVOnpGjVlTu0Tr1Im89bUGaMLHT!XobQPf9bIUA0oom7zAtNnbg\"U5N#NoASIGqGQIm2LG\"I$%aLVGGAoOf5&Q'RGUTVNj'FieMnTtTtVlIKIxOtAHlAUU9MjKss7bnorSUZ",
				"",
				"",
				true),
		new Cipher(
				"jelberg 1",
				"V´P¾LR¾MO•SŸ^N-»-¢TWEMJWV•£EMŸ^D¹G^BWI¼/HXUÃƒÆµÂJASµÊ+º¼Ê³-¢YºT¹´VS¾¸ƒDÔTƒ°ÄN-•+ÂSÃÌ²D^¢Ð+L½Ð-FUG+E+·»X²P»³„½ÌM¹ƒˆ-S¢ˆÂˆ±JO¼BTM£EÔÃH„EÊ„<ÔËVÄ»ÊUƒGA°°´£PPF´¢V»Ì¼-+µÂJ¸ÄJO£³ËŸ´•ºFº++E/W³Z´H¸UPKXF¾Gƒ-S±»R¢KIEOWVJºÊ+T¸„²Y¹¢G^+µ+UÃZY¾F¸U•K+„M/TJ¼»¾²½ADZ»Y¸DHIEMÐ+ÊFÆ-•+¤ÔÃ/ÌLXˆÂÔŸÐ¢U>M·JC°ÔÑOG^£/¤GY+ÂJ+RÆ³°PÄº´AƒP/+TNGMŸ-YUE•TDÃ»±•¤»Æ-+I¹U¾ŸVµ+",
				"",
				"http://www.zodiackillersite.com/viewtopic.php?t=3748&p=59170",
				true),
		new Cipher(
				"jelberg 2",
				"•AZ+´PÔU^UIÌ£M/+ÃCD+¸JL³G¤Ì¾R„½TÑ•B½+ÂBŸJJÃX+¢ÊL£CG°INGJ½VÆFFÆSµR½+YMI/ÆM»ÄÑMº¤BSˆ£•MD°µÆAHŸJMºUU»+Y£HZ/^>BBMNAMEU¾+²<E¸£R¤WH£¼M¾„Ê¸ˆÐ£NX+KJYŸ^+¢JÆVÑR£BMR½^¾ÆL+>Ê½SU+O¤µ++±PUZ+ËJŸ<ÂBNµLÊÊO¾J³ED•°GG¹·/ÃARL¾ÆUFÐ+ˆRC»+±£ºÄAÊNÊƒGBÔµÑÄUPLÆµJ¾+´UDBBY¸/HJ„^+²¾Ô³ÆUMÊ½/EL-R+SYBGM¹ÊOVX+ÆFA¤Y½µ+¢^Jµ¸³¤+C´ˆµGÂNÑIAÆAD+RLÊ½T£BGI½NµGÊ¹¤·µÌLJELBURGL„VOID",
				"",
				"http://www.zodiackillersite.com/viewtopic.php?t=3753&p=59257",
				true),
		new Cipher(
				"jelberg 3",
				"Ì°¹ËJEBHLÔKIÃ·¾¢TG>F>„XC¸+µR¤<Ê±^>ˆOŸ£U„B°PÄ½N»Ñ¹KMÔÆ„-YI„ÃDH·FÌËÂU¸M±µVOÊRG¤SW+AT¹BKIEJ·²„FO„¹º¼L¢<°Z¸PWXH±¾^SCFNŸ»½>OYÑT£ˆV°„Ä+B²ÂAËJº„ÔE¢½K´³„µÐ„ZXDRÆÌ<„YL¾²SÃ^CUI·•»M„¸½VW±H/¹ÑG¤Ð<+¼YŸAS²´FOˆ¹º¾ÊÄ>„TËZ»½„³NB„JELBERG<VPYÔK„¼ÂEµ£Ê´AIºP/¢D³·X^ZRˆÌ¼J¸Ã±Ô„G¤´ÄŸÆWL„¾°µ³B„ËFEÊ-»VPHOM£¹/KA²Z½DÐ¢CIXÃNY·ºM„T^Ô„¸ƒ-ˆÆ„µ±²ÌFÄËÂ+B°GHE¤¢¾JKXOÑ„-•Ÿ½¹",
				"",
				"http://www.zodiackillersite.com/viewtopic.php?t=3762&p=59376#p59376",
				true),
		new Cipher(
				"largo side-by-side diagonal transpositions",
				"klHxJMs2q+cmAsKBdksvDAnE3rhiysrzAfkjBOOPeBI+h8ickdev5DFXYj4EkfoTGrpQ1+XSY2kAhHkZaT5LWq+W0ivrsjlcxBJm5PVWM+hdxqNKe+YOknPwLLzMkt+oR+pbdCDrc0SlTE+UumV+nYxy+MdkA8eoIz5qtpNfr0OqZXR8BgCi6D+WQkr+dyvyzdRjHNFTK0LxGsUaJbEeIV+d5AZd+lqKsHWBXmayFnAD8o+zMbNQhLi3E6J0PxCrSoRl65ZHSsrkm9AdSWrcUjfqB+KL7wyr2V+xt6ucd55f1nfca+dshA2M+WHaiEJXA30++xY9yo+KNz0qLelJ",
				"MANIACMANSIONISAGRAPHICADVENTUREVIDEOGAMEDEVELOPEDANDPUBLISHEDBYLUCASFILMGAMESITFOLLOWSTEENAGEPROTAGONISTDAVEMILLERASHEATTEMPTSTORESCUEHISGIRLFRIENDFROMAMADSCIENTISTWHOSEMINDHASBEENENSLAVEDBYASENTIENTMETEORTHEPLAYERUSESAPOINTANDCLICKINTERFACETOGUIDEDAVEANDTWOOFHISSIMPLAYABLEFRIENDSTHROUGHTHESCIENTISTSMANSIONWHILESOLVINGPUZZLESANDAVOIDINGD",
				"http://www.zodiackillersite.com/viewtopic.php?p=64744#p64744",
				true),
		new Cipher(
				"jarlve L-route transposition",
				"Z@(V)PN6#\"O!%V;S^\\%@IN-(3(SC3T)'\\NCNG4^4%$YN^SDZ02KI.OM1_M?U\\*HO?$8SK*$4CDBU@IMB@UWDI:I]Y&8U1K4SN&YBO5644N9SOO9WN#NFL(>Y5??7!G#:SA+.:/G9G9#X6WT@<1UJQUIK;7P7BHBNJ?D?P]I,-OWX\\S<O#HZU#XF72I@DUB/LD:\"%[E6=)LBOO0N#EN].IDN?L4Y\\__^KJ*;@I'0)>S5R4!SDZR?@2KI1U+O?VSHIZ!;J,Z%QBF\\/#U3.'AU@(0$?U>I#=:DO@NO6\"DW*28IPOF@S'[)F&?N;QVBU9#TS1LD2<#F%=-;S!MBUJH3O",
				"ASPOSSIBLYTHECOOLESTGRANDMAONTHEINTERNETYOUMAYWELLHAVEALREADYHEARDOFSHIRLEYCURRYSHESANEIGHTYTWOYEAROLDYOUTUBERWHOPRIMARILYRECORDSHERSELFPLAYINGSKYRIMANDHASPRETTYMUCHWONTHEHEARTSOFEVERYONEINTHEELDERSCROLLSCOMMUNITYREFERRINGTOHERSUBSCRIBERSASGRANDKIDSSHEGOESOUTOFHERWAYTOREPLYTOEVERYCOMMENTONHERVIDEOSANDHERLETSPLAYSAREBASICALLYTHEMOSTWHOLESO",
				"http://www.zodiackillersite.com/viewtopic.php?p=69123#p69123",
				true),
		new Cipher(
				"jarlve spiral transposition",
				"OV[-ENT]XA5D^GK\\#,3K.F]UE@IY^;JBL_3>WP@>J97/;\"C?FD@S##9>KVF;')!U]TT7Y&&&8QD&#_/75HN-0;ND/\"51.@/79'[E*U^,3?J7;>FU3=9(D%DZT\\:X/<#^,7D@\":#W86@@3_#R2K/<;&YQ>\"J5QW#28\"[S+MG#AJ@9A#*\\NG?@':S8L]9!ZXT&(-17QX;3$]@/G+^ABZL9/DT#D>@\"7J7#0.D$JBE@VIJ<&;F8<6S4R]7*<Y1SS(=:OD\"%@KJM67/AY>)#6*JYNO5FX^?C+A>DH3/_@YF#\"?IB3>D3CSH7?(=5/7^U4DAVX?/9/75J>1X/M>+4#S3X",
				"ILIKEWILLINGPEOPLEBECAUSEITISSOMUCHFUNITISMOREFUNTHANKILLINGWILDGAMEINTHEFORRESTBECAUSEDANISTHEMOSTDANGEROUSANIMALOFALLTOWILLSOMETHINGGIVESDETHEMOSTTHRILLINGEXPERENCEITISEVENBETTERTHANGETTINGYOURROCKSOFFWITHAGIRLTHEBESTPARTOFITISTHATWHENIDIEIWILLBEREBORNINPARADICEANDALLTHEIHAVEWILLEDWILLBECOMEMYSLAVESIWILLNOTGIVEYOUDYNAMEBECAUSEYOUWILLTRY",
				"http://www.zodiackillersite.com/viewtopic.php?p=69306#p69306",
				true),
		new Cipher(
				"largo mystery cipher (partial columnar transposition)",
				";b33h4xnF7q19chd9=740PqKNFAexNXTyF;vA9TLCGr0NM;Q+mlFGF3R:njJ0i+45d-YTAV4yXCLQKmSRoBtVdYti+5LHl=qrXbuM1X+BSMRIC0pKabveBw=9oj+JLhiTNir0U-xGPSE7sdeQ0bYZc4in0m5oJeAXK;t+KuV3:4RF7LPqTUMlT=v91rejbF+XmcG5bhNnC-J0TYHoQd+;S9tTyYV=Tuy0=APN+XlxDR:1JrL+Nni49swM-+UKi+;sOwdD+h7HVG0Yqi0CeR:THZcvbjtYXRK34yF=5E+0TAI0xwOc90XeqlubvRQ1oMxYBShOim+jP=-3=7RMFTU",
				"IACCESSWHICHISWHYTHISWASPOSTEDONAMONDAYINSTEADOFOVERTHEWEEKENDANDTHISYEARINSTEADOFTALKINGABOUTANOTHEROFMYFAVORITEFICTIONALSUPERHEROESASIVEDONEINYEARSPASTIVEDECIDEDTOWRITETHEINSANELYOVERTHETOPSTORYOFAREALLIFEMANOFSTEELSOFALLSHATTERINGLYRIDICULOUSYOUDTHINKTHEENTIRESTORYWASTHEPRODUC",
				"http://www.zodiackillersite.com/viewtopic.php?p=70092#p70092",
				true),
		new Cipher(
				"masootz mystery cipher (unknown system)",
				"623+86kL4mEX6PBKRTz1cwzF7cn3I91xSQMbZgncUJd4NEYG+xaYAfW9xbCYXegjIglrSJUrVI6SUkib9yEgcDL+TC+QHGWczOK+svV6jltEqTZDBi4lHYvzFx9sM+8Wc4M6uBUdKWfHe3lhqB5N9ZOmStgzvElTzVW+1X+MEA6NP+yC9nNwMfhDkAcJ9td95RVrB4z6MrcUMhOPxTSrI4fL9oaq8Bg2zVGr3L95WEQ+PpaZ+vcCSKtP4LzwSqyAu3U9hQtWAN+vwFfGdSCnEJ+5YiN+wPwhXNvKAj9TGQV9OGgudirB+vbLcxnsjWktdz8asRS+PEb30PZKGlvA",
				"",
				"http://www.zodiackillersite.com/viewtopic.php?p=70667#p70667",
				true)
		
				
	}; // end of ciphers
	
	public static String Z340 = cipher[0].cipher;
	public static String Z408 = cipher[1].cipher;
	public static String Z13 = cipher[3].cipher;
	public static String Z32 = cipher[4].cipher;
	
	// combine cipher lists
	static {
		Cipher[] combined = new Cipher[cipher.length+CiphersFromGeneratorExperiments.cipher.length];
		int i=0;
		for (Cipher c : cipher) combined[i++] = c;
		for (Cipher c : CiphersFromGeneratorExperiments.cipher) combined[i++] = c;
		cipher = combined;		
	}

	public static String alphabet408 = "ABDEFGHIJKLMNOPQRSTUVWXYZ56789cdefjklpqrtz!#%()=+/@\\^_";
	public static String decoder408 = "wlnesattfsthenifgaoibeouetesaivocdxiaemrrdollnhpeksrny";
	
	/** decoder map for Z408 */
	public static Map<Character, Character> decoderMap;
	static {
		decoderMap = new HashMap<Character, Character>();
		for (int i=0; i<alphabet408.length(); i++) {
			decoderMap.put(alphabet408.charAt(i), decoder408.charAt(i));
		}
	}
	
	/** starting point for numeric representations: the original order of symbols as they appear in Z340 */
	public static Map<Character, Integer> decoderMapNumeric340;
	public static int decoderMapNumeric340Max; // track the largest number assigne in the numeric representaton of Z340
	static {
		decoderMapNumeric340 = new HashMap<Character, Integer>();
		int current = 1;
		String cipher = Ciphers.cipher[0].cipher;
		for (int i=0; i<cipher.length(); i++) {
			char ch = cipher.charAt(i);
			Integer val = decoderMapNumeric340.get(ch);
			if (val == null) {
				val = current++;
				decoderMapNumeric340.put(ch, val);
				decoderMapNumeric340Max = val;
			}
		}
	}
	
	/** returns true if the given set of cipher symbols is a homophone of a single plaintext symbol. */
	public static boolean isHomophone(String symbols) {
		Set<Character> plain = new HashSet<Character>();
		for (int i=0; i<symbols.length(); i++) {
			char c = symbols.charAt(i);
			char p = decoderMap.get(c);
			if (plain.isEmpty()) plain.add(p);
			else if (!plain.contains(p)) return false;
		}
		return true;
	}
	
	public static Map<Character, Character> decoderMapFor(String cipher, String plaintext) {
		if (cipher.length() != plaintext.length()) throw new IllegalArgumentException("Cipher must equal plaintext length");
		Map<Character, Character> map = new HashMap<Character, Character>();
		for (int i=0; i<cipher.length(); i++) {
			map.put(cipher.charAt(i), plaintext.charAt(i));
		}
		return map;
	}

	/** this version returns null if any violations of the key are found.
	 * if no violations found, returns new map that has old map assignments merged with new assignments */
	public static Map<Character, Character> decoderMapFor(Map<Character, Character> map, String cipher, String plaintext) {
		if (cipher.length() != plaintext.length()) return null;
		Map<Character, Character> newMap = new HashMap<Character, Character>();
		if (map != null)
			for (Character c : map.keySet()) newMap.put(c, map.get(c));
		for (int i=0; i<cipher.length(); i++) {
			char c = cipher.charAt(i);
			char p = plaintext.charAt(i);
			if (newMap.get(c) == null) {
				newMap.put(c,  p);
			} else if (newMap.get(c) != p) return null;
		}
		return newMap;
	}
	
	/** returns true if the given key (decoder map) has homophonic assignments; i.e., at least one plaintext
	 * letter is assigned to more than one cipher symbol.
	 */
	public static boolean isHomophonic(Map<Character, Character> map) {
		if (map == null) return false;
		Set<Character> seen = new HashSet<Character>();
		for (Character p : map.values()) {
			if (seen.contains(p)) return true;
			seen.add(p);
		}
		return false;
	}
	
	public static void dumpDecoderFor(String cipher, Map<Character, Character> map) {
		StringBuffer alpha = new StringBuffer("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
		for (int i=0; i<alpha.length(); i++) {
			char ch = alpha.charAt(i);
			String line = ch+": ";
			for (char key : map.keySet()) {
				if (map.get(key) == ch) {
					line += key;
				}
			}
			System.out.println(line);
		}
	}
	
	public static String decode(String cipher, Map<Character, Character> decoderMap) {
		return decode(cipher, decoderMap, true);
	}
	public static String decode(String cipher, Map<Character, Character> decoderMap, boolean decodeSpaces) {
		StringBuffer sb = new StringBuffer();
		for (int i=0; i<cipher.length(); i++) {
			char key = cipher.charAt(i);
			if (!decodeSpaces && key == ' ') sb.append(" ");
			else {
				Character val = decoderMap.get(key);
				if (val == null) val = '_';
				sb.append(val);
			}
		}
		return sb.toString();
	}
	
	
	/* cosine similarities */
	public static CosineSimilarity c = new CosineSimilarity();
	public static List<CosineSimilarityResult> cosineSimilarities; // list of all similarities
	public static Map<String, Float> cosineSimilaritiesMap;
	
	public static void initCS() {
		cosineSimilaritiesMap = new HashMap<String, Float>();
		cosineSimilarities = c.compute(cipher[which].cipher, false);
		int count=0;
		for (CosineSimilarityResult r : cosineSimilarities) {
			cosineSimilaritiesMap.put(r.key, r.val);
			cosineSimilaritiesMap.put(""+r.key.charAt(1)+r.key.charAt(0), r.val); // the equivalent reverse, for convenience
		}
	}

	
	/* the Zodiac alphabets */
	public static final String[] alphabet = {
		"ABCDEFGH|JKLMNOPRSTUVWXYZ123456789plkdfycjqbtz()>^+.<-/#_@*%&;:",
		"ABDEFGHIJKLMNOPQRSTUVWXYZ56789cdefjklpqrtz!#%()=+/@\\^_",
		"#%)*+/123456789=>@ABCDEFGIJKLMNPQRSTVWXYZ_bcfjklnpqtxyz",
		"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!#$%&()*+,-./:;<=>@[\\]^_`{|}~", // 91-symbol alphabet for general use.  purposefully avoids "?", single quote, and double quotes.
		"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!\"#$%&'()*+,-./:;<=>@[\\]^_`{|}~�����֚��������������������������������������邮�������ȼ�ᦵ������ǻ�������", // collection of 172 single-byte symbols 
		"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!#$%&()*+-./:;<=>@[\\]^_`{|}~", // same as alphabet[3] but excludes comma 
		//"ABDEFGHIJKLMNOPQRSTUVWXYZ56789defklpqrtz!#%()*+/@\\^", // alphabet for 100 char subset of solved cipher
		//"BCDEFGHIJKLMNOPRSTUVWYZ12345789cdfjklptyz#%&()+-./:<>\\^_", // alphabet for 100 char subset of unsolved cipher
		//"ABEFGKORSTUVWXYZ589cdepq#%+@^_", // alphabet for the 51 chars of the 408
		//null
	};
	public static HashMap<Character, Integer> alphabetHash; // used to speed up the getDecoderForWordAtPosition method.
	
	/* known solutions (as decoders) */
	public static final String[] solutions = {
		"UNKNOWN", // 63 chars
	 //ABDEFGHIJKLMNOPQRSTUVWXYZ56789cdefklpqrtz!#$%()*+/@\^_
		"wlnesattfsthenifgaoibeouetesaivocdxiaemrrdollnhpeksrny" // 54 chars
		//"wlnesattfsthenifgaoibeouetesaiocdiaemrrdollhnpeksrn",
		//null,
		//"wlesasngaoibeouetaivocemllesny", // the 30 chars of the 54
	};
	
	public static void dumpCiphers(String path) {
		for (int i=0; i<cipher.length; i++) {
			FileUtil.writeText(path + "/cipher_" + i + ".txt", cipher[i].cipher);
		}
	}
	
	/** get the cipher using the given description */
	public static Cipher cipherByDescription(String description) {
		for (Cipher c : cipher)
			if (description.equalsIgnoreCase(c.description)) return c;
		return null;
	}
	
	/* known solutions (full text) */
	public static final String[] solutionsFull = {
		"UNKNOWN",
		"ilikekillingpeoplebecauseitissomuchfunitismorefunthankillingwildgameintheforrestbecausemanisthemoatdangertueanamalofalltokillsomethinggivesmethemoatthrillingexperenceitisevenbetterthangettingyourrocksoffwithagirlthebestpartofitiathaewhenidieiwillbereborninparadicesndalltheihavekilledwillbecomemyslavesiwillnotgiveyoumynamebecauseyouwilltrytosloidownorstopmycollectingofslavesformyafterlifeebeorietemethhpiti"
	};
	

	/** convert one-line cipher into grid form with the given width */
	public static String[] grid(String text, int width) {
		return grid(text, width, false);
	}
	public static String[] grid(String text, int width, boolean pad) {
		int rows = text.length() / width;
		int rem = text.length() % width;
		if (rem > 0) {
			rows++;
		}
		if (pad) {
			while (text.length() % width != 0) text += ' ';
		}
		//System.out.println("[" + text + "]");
		String[] grid = new String[rows];
		for (int i=0; i<rows; i++)
			grid[i] = text.substring(i*width, i*width+width);
		return grid;
	}
	
	/** returns true if each symbol in the given string translates to the same plaintext letter in the 408 cipher */
	public static boolean realHomophone(String str) {
		Set<Character> set = new HashSet<Character>();
		for (int i=0; i<str.length(); i++) {
			Character ch = decoderMap.get(str.charAt(i));
			set.add(ch);
		}
		return set.size() == 1;
	}
	
	/** derive the cipher alphabet, in sorted order, from the given cipher text */ 
	public static String alphabet(String cipher) {
		Set<Character> set = new TreeSet<Character>();
		for (int i=0; i<cipher.length(); i++) {
			//if (cipher.charAt(i) == ' ') continue; // ignore spaces
			//if (cipher.charAt(i) == '	') continue; // ignore tabs
			set.add(cipher.charAt(i));
		}
		StringBuffer sb = new StringBuffer();
		for (Character c : set) sb.append(c);
		return sb.toString();
	}
	public static String alphabet(StringBuffer cipher) {
		Set<Character> set = new TreeSet<Character>();
		for (int i=0; i<cipher.length(); i++) {
			//if (cipher.charAt(i) == ' ') continue; // ignore spaces
			//if (cipher.charAt(i) == '	') continue; // ignore tabs
			set.add(cipher.charAt(i));
		}
		StringBuffer sb = new StringBuffer();
		for (Character c : set) sb.append(c);
		return sb.toString();
	}
	
	public static String[] alphabetAsArray(String cipher) {
		String a = alphabet(cipher);
		String[] result = new String[a.length()];
		for (int i=0; i<a.length(); i++) result[i] = a.charAt(i) + "";
		return result;
	}

	/** derive the cipher alphabet, in sorted order, from the given cipher text */ 
	public static List<Character> alphabetAsList(String cipher) {
		Set<Character> set = new TreeSet<Character>();
		for (int i=0; i<cipher.length(); i++) {
			set.add(cipher.charAt(i));
		}
		return new ArrayList<Character>(set);
	}
	/** derive the cipher alphabet, in sorted order, from the given cipher text */ 
	public static Set<Character> alphabetAsSet(String cipher) {
		Set<Character> set = new TreeSet<Character>();
		for (int i=0; i<cipher.length(); i++) {
			set.add(cipher.charAt(i));
		}
		return set;
	}

	/** return a map of symbol counts for the given cipher text */
	public static Map<Character, Integer> countMap(String str) {
		Map<Character, Integer> symbolCounts = new HashMap<Character, Integer>();
		if (str == null) return symbolCounts;
		for (int i=0; i<str.length(); i++) {
			char ch = str.charAt(i);
			Integer val = symbolCounts.get(ch);
			if (val == null) val = 0;
			val++;
			symbolCounts.put(ch, val);
		}
		return symbolCounts;
	}
	/** return the sum of count diferences between the two character maps */
	public static int countMapDifferences(Map<Character, Integer> map1, Map<Character, Integer> map2) {
		int diffs = 0;
		for (Character c : map1.keySet()) {
			Integer val1 = map1.get(c);
			Integer val2 = map2.get(c);
			if (val2 == null) val2 = 0;
			diffs += Math.abs(val1-val2);
		}
		for (Character c : map2.keySet()) {
			if (map1.containsKey(c)) continue;
			diffs += map2.get(c);
		}
		return diffs;
	}
	
	/** compute how much of the cipher is decoded by the given partial key */
	public static double coverage(String cipher, Map<Character, Character> key, Map<Character, Integer> counts) {
		double result = 0;
		for (Character c : key.keySet()) {
			Character p = key.get(c);
			if (p != null) result += counts.get(c);
		}
		result /= cipher.length();
		return result;
	}
	
	/** merge the two keys.  but returns null if they are in conflict (at least one cipher unit is in both keys but mapped
	 * to different plaintexts)  
	 */
	public static Map<Character, Character> mergeKeys(Map<Character, Character> key1, Map<Character, Character> key2) {
		if (key1 == null) return key2;
		if (key2 == null) return key1;
		Map<Character, Character> merged = new HashMap<Character, Character>();
		for (Character key : key1.keySet()) {
			merged.put(key, key1.get(key));
		}
		
		for (Character key : key2.keySet()) {
			Character p1 = merged.get(key);
			Character p2 = key2.get(key);
			if (p1 != null && p1 != p2) 
				return null;
			merged.put(key, p2);
		}
		return merged;
	}
	
	/** returns true if the two count maps are equal */
	public static boolean countMapsEqual(Map<Character, Integer> map1, Map<Character, Integer> map2) {
		if (map1 == null && map2 == null) return false;
		if (map1 == null || map2 == null) return false;
		for (Character key : map1.keySet()) {
			if (map2.containsKey(key)) {
				if (!map1.get(key).equals(map2.get(key))) return false;
			} else return false;
		}
		for (Character key : map2.keySet()) {
			if (map1.containsKey(key)) {
				if (!map2.get(key).equals(map1.get(key))) return false;
			} else return false;
		}
		return true;
	}
	
	/** return a map of symbols to all their positions */
	public static Map<Character, List<Integer>> positionMap(String str) {
		Map<Character, List<Integer>> map = new HashMap<Character, List<Integer>>();
		for (int i=0; i<str.length(); i++) {
			Character key = str.charAt(i);
			List<Integer> val = map.get(key);
			if (val == null) val = new ArrayList<Integer>();
			val.add(i);
			map.put(key, val);
		}
		return map;
	}
	
	/** returns true if both maps have the same frequency distributions (even if the symbols are different) */
	public static boolean sameDistribution(Map<Character, Integer> map1, Map<Character, Integer> map2) {
		if (map1.size() != map2.size()) return false;
		Collection<Integer> v1 = map1.values();
		Collection<Integer> v2 = map2.values();
		if (v1.size() != v2.size()) return false;
		List list1 = new ArrayList(v1);
		List list2 = new ArrayList(v2);
		Collections.sort(list1);
		Collections.sort(list2);
		for (int i=0; i<list1.size(); i++) 
			if (list1.get(i) != list2.get(i)) return false;
		return true;
	}

	
	
	public static void testAlphabet() {
		System.out.println(cipher[0]);
		System.out.println(alphabet(cipher[0].cipher));
		System.out.println(cipher[1]);
		System.out.println(alphabet(cipher[1].cipher));
	}
	
	public static void decodeGilesCorey() {
		String cipher = "Od5MUOR (+A Y^zpt FY^ >Df qTXO B!rz G(f *DcW\\ K8U^L SDz 7A>kO /(AJN G^f e#Z8c6t B_E SDz *T>q G AP#f VX_ HYrO+z !Bf IdT @X!( 8^ p*zW\\ QUt SD >(ekN^5 =#G9D 8 VSR MZ>f edq6 LT eBGPq )UF HMrXOE 8 K*Sc+ k( R!#z I)>5 GB* ed(JT\\q LX S7/ HMp e>I5#W Ld 9DQTtq _XY  ";
		Map<Character, Character> decoder = decoderMapFor(
				Ciphers.cipher[1].cipher, Ciphers.cipher[1].solution);
		String plain = "";
		for (int i = 0; i < cipher.length(); i++) {
			char ch = cipher.charAt(i);
			if (ch == ' ')
				plain += ch;
			else {
				if (decoder.get(ch) == null)
					plain += ch;
				else
					plain += decoder.get(ch);
			}
		}
		System.out.println(plain);
	}
	
	
	/** generate a numerical version of the given ciphertext */
	/*public static String numericAsString(String cipher) {
		return numericAsString(cipher, 0);
	}*/
	/*public static String numericAsString(String cipher, int start) {
		String result = "";
		for (int i : numeric(cipher, start))
			result += i + " ";
		return result;
	}*/
	
	/** init the decoder map using Z340's alphabet. */
	/*public static void initDecoderMapNumeric() {
		numeric(Ciphers.cipher[0].cipher, 1);
	}*/

	public static String numericAsString(String cipher, boolean useDecoderFrom340) {
		return Arrays.toString(toNumeric(cipher, useDecoderFrom340));
	}
	public static int[] toNumeric(String cipher, boolean useDecoderFrom340) {
		return toNumeric(cipher, useDecoderFrom340, false);
	}
	public static int[] toNumeric(String cipher, boolean useDecoderFrom340, boolean startAtZero) {
		Map<Character, Integer> decoderMapNumeric = new HashMap<Character, Integer>();
		int[] result = new int[cipher.length()];
		int current = useDecoderFrom340 ? decoderMapNumeric340Max + 1 : (startAtZero ? 0 : 1);
		
		for (int i=0; i<cipher.length(); i++) {
			char ch = cipher.charAt(i);
			// first, see if there's a mapping in the original Z340's symbol alphabet 
			Integer val = useDecoderFrom340 ? decoderMapNumeric340.get(ch) : null;
			if (val == null) {
				val = decoderMapNumeric.get(ch);
				if (val == null) {
					val = current++;
					decoderMapNumeric.put(ch, val);
				}
			}
			result[i] = val;
		}
		return result;
	}
	
	public static String toNumericWithPadding(String cipher, boolean useDecoderFrom340) {
		int[] num = toNumeric(cipher, useDecoderFrom340);
		String n = "";
		for (int i : num) n += (i<10 ? "0"+i : i) + " ";
		return n;
	}

	public static String fromNumeric(String cipher, boolean useDecoderFrom340) {
		cipher = cipher.replaceAll(",","");
		String[] split = cipher.split(" ");
		int[] nums = new int[split.length];
		for (int i=0; i<nums.length; i++) {
//			System.out.println(split[i]);
			nums[i] = Integer.valueOf(split[i]);
		}
		return fromNumeric(nums, useDecoderFrom340);
	}
	public static String fromNumeric(int[] cipher, boolean useDecoderFrom340) {
		return fromNumeric(cipher, useDecoderFrom340, 4);
	}
	public static String fromNumeric(int[] cipher, boolean useDecoderFrom340, int whichAlphabet) {
		Map<Integer, Character> map = new HashMap<Integer, Character>();
		if (useDecoderFrom340) {
			for (Character key : decoderMapNumeric340.keySet()) {
				Integer val = decoderMapNumeric340.get(key);
				if (map.get(val) != null) throw new RuntimeException("Duplicate mapping for symbol " + key + " val " + val + ": " + map.get(val));
				map.put(val, key);
			}
		}
		
		int pos = 0;
		String result = "";
		
		for (int i=0; i<cipher.length; i++) {
			Integer key = cipher[i];
			Character val = map.get(key);
			if (val == null) val = alphabet[whichAlphabet].charAt(pos++);
			result += val;
			map.put(key,val);
		}
		return result;
	}
	
	/** convert from cipher units that have the given delimiters */
	public static String fromDelimited(String cipher, Character unitDelimiter, Character wordDelimiter) {
		String result = "";
		Map<String, Character> map = new HashMap<String, Character>();
		int pos = 0;
		String[] words = cipher.split(""+wordDelimiter);
		for (String word : words) {
			if (result.length() > 0) result += " ";
			String[] units = word.split(""+unitDelimiter);
			for (String key : units) {
				Character val = map.get(key);
				if (val == null) val = alphabet[3].charAt(pos++);
				map.put(key, val);
				result += val;
			}
		}
		return result;
	}
	
	/** convert oddly encoded ciphers from a space-delimited hex dump */
	public static String fromHex(String hex) {
		String[] split = hex.split(" ");
		int pos = 0;
		String cipher = "";
		Map<String, Character> map = new HashMap<String, Character>();
		for (String key : split) {
			Character val = map.get(key);
			if (val == null) val = alphabet[3].charAt(pos++);
			map.put(key, val);
			cipher += val;
		}
		return cipher;
	}
	
	/** convert weirdly encoded unsub from hex dump to normal printable ascii */ 
	public static void unsubFromRaw() {
		String[] hex = new String[] { "42", "53", "d1", "c5", "45", "5a", "49",
				"b9", "50", "45", "b7", "2b", "4d", "44", "bc", "5e", "a3",
				"41", "c6", "d0", "75", "b0", "b8", "4b", "50", "cb", "a4",
				"54", "5a", "42", "58", "ba", "4c", "be", "c5", "41", "47",
				"52", "cc", "b5", "b7", "b0", "48", "c6", "4f", "4b", "49",
				"5e", "4e", "56", "79", "2b", "54", "4a", "bd", "c3", "d1",
				"45", "51", "82", "c5", "47", "46", "58", "ba", "44", "75",
				"b8", "bc", "2f", "a4", "be", "49", "2b", "d0", "cc", "4c",
				"56", "b7", "cb", "54", "b9", "4f", "b5", "d4", "b0", "c3",
				"52", "50", "47", "82", "4b", "4e", "5c", "79", "45", "c3",
				"44", "c5", "50", "57", "b9", "45", "c4", "bd", "c6", "d1",
				"50", "b8", "bc", "52", "49", "ba", "75", "b7", "b8", "b0",
				"c6", "a4", "45", "42", "2b", "79", "46", "be", "54", "4b",
				"c3", "b9", "50", "4d", "51", "c5", "49", "cb", "a3", "d4",
				"cc", "2f", "42", "5c", "4f", "ba", "47", "d0", "4e", "d2",
				"52", "a2", "2b", "79", "bd", "c4", "b9", "d1", "41", "b5",
				"54", "4d", "44", "75", "a4", "a3", "58", "c6", "bc", "be",
				"4a", "cb", "b7", "b5", "52", "47", "82", "cc", "ba", "44",
				"bc", "4f", "4e", "41", "cb", "79", "2b", "51", "b0", "b8",
				"57", "c6", "4b", "bd", "d4", "42", "45", "5a", "c5", "48",
				"56", "58", "50", "49", "d1", "ba", "a2", "c6", "75", "a4",
				"41", "b5", "b7", "45", "57", "4d", "a3", "4a", "b9", "44",
				"d0", "50", "42", "54", "52", "4d", "45", "5c", "82", "b0",
				"47", "4b", "79", "ba", "bc", "b9", "50", "b8", "c6", "be",
				"44", "bc", "c5", "45", "d2", "b7", "52", "2b", "51", "b0",
				"49", "56", "cc", "4b", "4f", "58", "cb", "c5", "50", "4e",
				"ca", "45", "b8", "bd", "ca", "a3", "54", "79", "42", "50",
				"47", "d0", "b7", "2b", "5c", "b0", "b5", "44", "46", "4d",
				"54", "4a", "4b", "45", "49", "bc", "a2", "b9", "50", "d1",
				"cb", "47", "b5", "2f", "75", "c5", "c3", "52", "45", "c4",
				"2b", "a3", "b7", "56", "42", "41", "44", "a4", "58" };
		String a = "";
		for (int i=0; i<Ciphers.cipher[0].cipher.length(); i++) {
			char c = Ciphers.cipher[0].cipher.charAt(i);
			if (a.contains(""+c)) continue;
			a+=c;
		}
		System.out.println(a);
		
		String cipher = "";
		int pos = 0;
		Map<String, Character> map = new HashMap<String, Character>();
		for (String key : hex) {
			Character val = map.get(key);
			if (val == null) val = a.charAt(pos++);
			map.put(key, val);
			cipher += val;
		}
		System.out.println(cipher);
	}
	
	public static void smokie31() {
		// String cipher =
		// "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefEUghSijkFelJmnSopqWKrJksZtrquvBwrxyzJUsS0wbcQ1AdIePElvXSm2KtNqqh3y456gXlvr7sGw1SuTDk4Y00SmLJPHSFMpg8ASyPDhCVsRxiaFIHUSznSv3jSANB1P9qr0KSIBTtaQSxSj54jnfuDy1ruCS0!vNeKEgoRJmLrV1Ie4Fbaur7XGbSDyRxY3WZSctH8udLzSt0GS3HNLi3ZeSUfpeG1KvzPSnjQMu9IfMTS!7!OvNkJtmDCSLWa5Vqb6hvrSpjFsjxmi0iJk2e!HtSD2rYoPUwruUeszxzaSSAlhGh";
		int[] cipher = new int[] { 34, 51, 35, 45, 53, 42, 10, 36, 37, 49, 57,
				24, 13, 38, 52, 43, 54, 46, 39, 22, 1, 44, 26, 14, 60, 19, 23,
				2, 20, 31, 40, 32, 53, 1, 17, 7, 39, 61, 27, 58, 42, 40, 8, 49,
				59, 21, 39, 62, 11, 50, 26, 57, 48, 49, 58, 4, 19, 41, 48, 50,
				15, 33, 51, 5, 48, 28, 6, 12, 49, 1, 4, 39, 47, 5, 2, 20, 54,
				29, 34, 31, 37, 40, 43, 53, 8, 33, 14, 39, 59, 55, 57, 41, 38,
				50, 50, 7, 25, 6, 18, 16, 3, 17, 14, 8, 33, 48, 9, 4, 10, 5,
				29, 39, 15, 22, 45, 58, 18, 60, 47, 47, 39, 59, 24, 49, 43, 36,
				39, 42, 13, 11, 17, 63, 34, 39, 6, 43, 45, 7, 35, 44, 4, 46,
				28, 61, 23, 42, 37, 36, 1, 39, 12, 21, 39, 33, 25, 27, 39, 34,
				38, 51, 29, 43, 30, 50, 48, 47, 57, 39, 37, 51, 22, 41, 23, 54,
				39, 28, 39, 27, 16, 18, 27, 21, 32, 15, 45, 6, 29, 48, 15, 35,
				39, 47, 56, 33, 38, 40, 57, 53, 17, 62, 46, 49, 59, 24, 48, 44,
				29, 37, 40, 18, 42, 2, 23, 15, 48, 9, 14, 10, 2, 39, 45, 6, 46,
				28, 60, 25, 26, 19, 39, 20, 41, 36, 63, 15, 31, 24, 12, 39, 41,
				47, 10, 39, 25, 36, 38, 24, 61, 25, 19, 40, 39, 1, 32, 11, 40,
				10, 29, 57, 33, 12, 43, 39, 21, 27, 54, 13, 15, 30, 37, 32, 13,
				22, 39, 56, 9, 56, 52, 33, 38, 58, 49, 41, 59, 45, 35, 39, 24,
				26, 23, 16, 44, 50, 2, 3, 7, 33, 48, 39, 11, 27, 42, 4, 27, 28,
				59, 61, 47, 61, 49, 58, 55, 40, 56, 36, 41, 39, 45, 55, 48, 60,
				62, 43, 1, 5, 48, 15, 1, 40, 4, 12, 28, 12, 23, 39, 39, 34, 8,
				7, 10, 7 };
		
		Map<Integer, Character> key = new HashMap<Integer, Character>();
		key.put(1, 'A');
		key.put(2, 'A');
		key.put(3, 'A');
		key.put(48, 'A');
		key.put(4, 'B');
		key.put(5, 'B');
		key.put(6, 'B');
		key.put(7, 'C');
		key.put(8, 'C');
		key.put(9, 'C');
		key.put(10, 'D');
		key.put(11, 'D');
		key.put(12, 'D');
		key.put(13, 'E');
		key.put(14, 'E');
		key.put(15, 'F');
		key.put(16, 'F');
		key.put(17, 'G');
		key.put(18, 'G');
		key.put(19, 'H');
		key.put(20, 'H');
		key.put(21, 'I');
		key.put(22, 'I');
		key.put(23, 'I');
		key.put(39, 'I');
		key.put(24, 'K');
		key.put(25, 'K');
		key.put(26, 'L');
		key.put(27, 'L');
		key.put(28, 'M');
		key.put(29, 'M');
		key.put(30, 'M');
		key.put(31, 'N');
		key.put(32, 'N');
		key.put(33, 'N');
		key.put(34, 'O');
		key.put(35, 'O');
		key.put(36, 'O');
		key.put(39, 'O');
		key.put(37, 'P');
		key.put(38, 'P');
		key.put(39, 'Q');
		key.put(40, 'Q');
		key.put(41, 'Q');
		key.put(42, 'R');
		key.put(43, 'R');
		key.put(44, 'R');
		key.put(45, 'S');
		key.put(46, 'S');
		key.put(47, 'S');
		key.put(39, 'S');
		key.put(48, 'T');
		key.put(49, 'T');
		key.put(50, 'T');
		key.put(51, 'U');
		key.put(52, 'U');
		key.put(53, 'V');
		key.put(54, 'V');
		key.put(55, 'W');
		key.put(56, 'W');
		key.put(57, 'X');
		key.put(58, 'X');
		key.put(59, 'X');
		key.put(60, 'Y');
		key.put(61, 'Y');
		key.put(62, 'Z');
		key.put(63, 'Z');

		String plain = "";
		for (int i : cipher) {
			plain += key.get(i);
		}
		System.out.println("plain " + plain);
		
		//String square = "abcdefghiklmnopqrstuvwxyz";
		String square = "aflqvbgmrwchnsxdiotyekpuz";
		/*String dec = Bifid.decrypt(plain, square, 19);
		System.out.println("dec " + dec);
		String re = Periods.rewrite3(dec, 1);
		System.out.println("re " + re + " zk " + ZKDecrypto.calcscore(new StringBuffer(re.toUpperCase())));
		*/
		
		
		// 1) inscribe plaintext (flip horizontally, at width 17 or 38)
		// 2) apply period    (possibly swap steps 1 and 2)
		// 3) apply bifid
		// so to decode, decrypt bifid, undo period, then flip horizontal
		
		for (int period1 = 1; period1 < plain.length()/2; period1++) {
			String dec = Bifid.decrypt(plain, square, period1);
			
			String[] flips = new String[] {
					dec,
					TransformationBase.fromList(CipherTransformations.flipHorizontal(TransformationBase.toList(dec, 17), 1)).toString(),		
					TransformationBase.fromList(CipherTransformations.flipHorizontal(TransformationBase.toList(dec, 38), 1)).toString()		
			};
			int f = 0;
			for (String flip : flips) {
				for (int period2 = 1; period2 < plain.length()/2; period2++) {
					String re = Periods.rewrite3(flip, period2);
					System.out.println(ZKDecrypto.calcscore(new StringBuffer(re.toUpperCase())) + " " + period1 + " " + period2 + " " + f + " " + re);  
				}
				f++;
			}			
			
		}
		
		
		
	}
	
	public static void testIsHomophone() {
		String[] homs = new String[] { "PU", "9P", "#B", ")M", "9U", "Pk",
				"9k", "Uk", "5L", "GO", "6O", "8E", "Bc", "6p", "Nk", "Zp",
				"DO", "WZ", "Wp", "D^", "O^", "Dp", "IL", "+p", "+W", "Ze",
				"Nr", ")H", "OX", "5H", "Mk", "+Z", "HL", "Kd", "9N", "\\l",
				"/K", "!F", "@K", "/X", "^l", "6^", "Nl", "lt", "DW", "@O",
				"\\r", "DZ", ")9", "/@", "=M", "=O", "Fd", ")I", "/T", "TX",
				"(6", "ep", "GS", "Ur", "(Y", "@T", "8=", "LM", "(X", "TZ",
				"AP", "+6", "AB", ")r", "5t", "5P", "6Z", "(W", "(G", "+B",
				")U", "(+", "6W", "rt", "#W", ")P", "(^", "(p", "#e", "5I",
				"BW", "(O", "(/", "Kl", "(d", "(@", "(K", "el", "(D", "(\\",
				"Vl", "\\e", "9^", "Xl", "6l", "KO", "Tl", "JQ", "Z\\", "Dl",
				"Gl", "P^", "@P", "Dk", "@l", "NX", "\\t", "NO", "kp", "KT",
				"!d", "=l", "NZ", "+N", "Ec", "KZ", "DU", "Np", "\\p", "/V",
				"^e", "6N", "/t", "@t", "Ep", "SY", "OW", "=^", "/N", ")p",
				"Lr", "E^", "GT", "@N", "MN", "5W", "It", "NW", "^t", "5p",
				"N^", "+T", "5=", "HI", "Lp", ")O", "=t", "6t", ")=", "TW",
				"Te", "=p", "9L", "9H", "5G", "Oe", "er", "EZ", "Mt", "Tt",
				"Pr", ")8", "8U", "8t", "#L", "5Z", "UZ", "EO", "Zk", "5O",
				"LZ", "+E", "OZ", "et", "=r", ")Z", "PZ", "Ak", "6I", "EP",
				"5r", "EL", "8k", "LW", "ET", "5U", "+O", "MU", "Vr", "Xt",
				"5k", "We", "AU", ")k", "EW", ")A", "8I", "5M", "Pz", "(k",
				"(U", "5E", "He", "Ee", "(H", "kz", "(Z", "Oz", "Be", "!(",
				"(T", "(r", "(5", "R\\", "^z", "(=", "9l", "tz", "(Q", "()",
				"(S", "!%", "Ul", "(l", "KY", "(c", "pz", "I\\", "KP", "KQ",
				"kl", "U\\", "!K", "V\\", "!X", "O\\", "W\\", "E\\", "KX",
				"9p", "GK", "\\k", "K^", "!/", "NQ", "QV", "\\^", "!D", "lr",
				"=\\", "QT", "KL", "Sl", "Kk", "Qt", "Kc", ")l", "!\\", "HK",
				"9@", "!A", "5K", "FK", "@\\", "@Q", "dl", "KN", "F\\", ")K",
				"H\\", "B^", "!J", "BS", "5^", "Kt", "IJ", "JK", "SU", "@Y",
				"DN", "!@", "Fl", "IN", "N\\", "DY", "^p", "Up", "/H", "BF",
				"DG", "#^", "NU", "FQ", "Tc", "!c", "FR", "/W", "/D", "Od",
				"Tp", "V^", "/^", "@_", "U^", "EN", "@D", "IS", "@^", "Td",
				"^d", "/P", "T\\", "Nd", "DE", "Pp", "Se", "Xd", "DH", "@d",
				"ES", "=N", "@F", "G^", "Ac", "W^", "/5", "Jr", "#c", "X^",
				"HN", "6@", "Ld", "/M", "+c", "Y^", "=J", "Dt", "9t", "9O",
				"dt", "^r", "/F", "FO", "8N", ")@", "@L", "5@", "@X", "/6",
				"HJ", "NV", "Hd", "GI", "/p", ")^", "Dd", "IT", "FL", "Z^",
				"OS", "Fr", "_q", "NY", "DI", "+/", "=I", "de", "@E", "Mp",
				")/", "Ok", "9f", "If", "Gp", "/O", "GN", "@H", "FZ", "Ut",
				"Op", "Er", "Ht", ")6", "FT", "Gt", "Fp", "%f", "Mr", "Vd",
				"OU", "5F", "9A", "6E", "9Z", "EG", "6G", "Or", "=A", "7R",
				"Zc", "Tr", "kr", "=T", "TV", "OV", "Yt", "6k", "MO", "MZ",
				")L", "GY", "Ft", "TU", "AV", "GP", "GM", "8G", "+=", "Vt",
				"HO", "9M", "Zr", "+A", ")G", "BL", "6U", "Uf", "7I", "LX",
				"=Z", "=W", "Pt", "8V", "Lt", "7U", "8r", ")5", "56", ")t",
				"5Y", "6R", "HP", "68", "8L", "+e", "7P", "ef", "6X", ")Y",
				"LP", "HR", "fk", "5X", "MV", "O_", "Ef", "BV", "6H", "8Y",
				"MP", "IM", "PW", "HV", "AW", "Ae", "+H", "LU", "HW", "Ue",
				"Ve", "UV", "7k", "+U", "HX", "XY", "Xq", "Zf", "6f", "Wk",
				"EX", "R_", "7M", "%A", "+k", "UW", "78", "Mf", "MW", "Ek",
				"RX", "Bk", "7_", "%e", "Y_", "#P", ")7", "7H", ")f", "#V",
				"9W", "9E", "Gf", "7O", "7L", "PX", "/f", "9Y", "PV", "57",
				"Ie", "ek", "Sf", "Df", "7p", "Yq", "Pe", "7N", "7^", "Nf",
				"EI", "/7", "9e", "cf", "IR", "7K", "7\\", "7t", "RU", "Uq",
				"(9", "!7", "9z", "%9", "7@", "fz", "9B", "(B", "Rz", "Bz",
				"7f", "Iz", "Uz", "(I", "%(", "%z", "ez", "(P", "#z", "#(",
				"(V", "6z", "(E", "(e", "Wz", "5z", "(M", "Az", "8z", "Tz",
				"Qq", "+z", "Hz", "9\\", "Mz", "!9", ")z", "rz", "Lz", "=z",
				"(L", "(_", "(t", "@z", "(F", "dz", "Dz", "lz", "(J", "B\\",
				"\\z", "!z", "Sz", "!B", "Q^", "IK", "QY", "Fz", "Pl", "Rl",
				"PQ", "!#", "KR", "BK", "Ql", "Bl", "KS", "#Q", "QU", "El",
				"X\\", "K\\", "!R", "Ml", "BQ", "DK", "%Q", "QW", "!^", "IQ",
				"!Y", "!k", "!H", "#\\", "/Q", "lp", "Qr", "QS", "Qd", "!Q",
				"6K", "KW", "#l", "Q\\", "Qp", "!L", "Jq", "Qc", "Fq", "Y\\",
				"Q_", "Qk", "MQ", "+l", "!+", "LQ", "AK", "GQ", "!T", "!Z",
				"%\\", "%K", "L\\", "!W", "=K", "+\\", "#K", "Qe", "KU", "Sq",
				"QX", "/l", "+Q", "QZ", "Al", "G\\", "!l", "=Q", "KV", "KM",
				"\\d", "Hl", "8K", "pq", "!_", "!I", "!V", ")\\", "Wl", "/\\",
				"Ll", "8Q", "!O", "+K", "6Q", "5l", "!=", "!G", "!r", "OQ",
				"Kp", "K_", "8\\", "Jl", "Zl", "NS", "9c", "/9", "Ol", "M\\",
				"D\\", "6\\", "Bd", "dq", "/c", "HQ", "9J", "5\\", "/B", "/d",
				"JR", "EK", "9F", "!6", "F^", "JN", "De", "DF", "@p", "@S",
				"!p", "BN", "Dc", "@R", "9D", "%D", "Rc", "/=", "BJ", "R^",
				"@B", "+@", "Bp", "\\c", "dp", "%@", "/U", "pt", "Ic", "_d",
				"/G", "Uc", "NP", "JV", "%N", "Rp", "/r", "9d", "5c", "SX",
				"%^", "FN", "/S", "8J", "DJ", "=c", "T^", "6S", "FS", "S^",
				"8S", "6c", "ST", "pr", "Hp", "8c", "%J", "DP", "JP", "5N",
				"NT", "Jt", "DX", "ct", "BD", "JS", "Dr", "LN", "Rd", "JO",
				"JT", "%c", "@V", "RS", "Fc", "6D", "=@", "+d", "@I", "DV",
				"DL", "cp", "=d", "%/", "^_", "8d", "Nc", "#F", "Sp", "Ne",
				"D_", "/R", "FP", "H^", "Tq", "MS", "/k", "#p", "^k", "cr",
				"/J", "Sd", "Sr", "Wd", "%d", "Vp", "DR", "DS", "%p", "JM",
				"=D", "JZ", "8@", "SV", "5D", "DM", "FU", "Hc", "@W", "Wc",
				")N", "%S", "@Z", "PS", "Mc", "+S", "FI", "+D", "EF", "ck",
				"SZ", "JU", "Yd", "%F", "JL", "5J", "FH", "/8", "6d", "#/",
				"8p", "/e", "/Y", "Ip", "St", ")S", ")D", "AJ", "HS", "Oc",
				")c", "@U", "9=", "#N", "AF", "FV", "=F", "J_", "+^", "EJ",
				"GJ", "Pc", "Vc", "M^", "8D", "FW", "ce", "Nt", "Sk", "Xc",
				"8F", "#D", "Xp", "/I", "_c", "8^", "FG", "FY", "Zd", "qt",
				"#@", "F_", "Lc", ")J", "Gq", "#d", "5d", "/Z", "6F", ")d",
				"Fe", "Gd", "dr", "5T", "6T", "I^", ")F", "Hr", "GH", "#J",
				"Bt", "+r", "Ed", "BO", "6r", "LT", "HT", "BT", "GL", "+G",
				"RT", "SW", ")T", "=G", ")+", "5S", "OR", "At", "9G", "Wr",
				"AH", "Yr", "T_", "Br", "TY", "Xr", "=B", "MT", "%T", "Zt",
				"9r", "OT", "=Y", "Ot", "GV", "GW", "#=", "=L", "%G", "Rt",
				"=R", "GZ", "Ar", "Ir", "GX", "Dj", "Ej", "Fj", "@j", "Aj",
				"Bj", "Lj", "Mj", "Nj", "Oj", "Gj", "Hj", "Ij", "Jj", "Kj",
				"Uj", "Tj", "Wj", "Vj", "Qj", "Pj", "Sj", "Rj", "\\j", "_j",
				"^j", "Yj", "Xj", "Zj", "fj", "!j", "dj", "ej", "%j", "cj",
				"#j", "(j", ")j", "jt", "jr", "jp", "jq", "jl", "jk", "jz",
				"+j", "/j", "6j", "5j", "9j", "8j", "7j", "=j", "%O", "HZ",
				"=H", "GU", "6M", "XZ", "Et", "%=", "=U", "6=", "H_", "#T",
				"AG", "Tk", "BG", "%t", "Rr", "=P", "8O", "#t", "6L", "8T",
				"=X", "AR", "AZ", "PT", "IO", "A_", "Gk", "HM", "=k", "8H",
				"6Y", "5_", "#O", "+_", "Wt", "+t", "kt", "EH", "+M", "LO",
				"6_", "6V", ")q", "+5", "Zq", "Hk", "%+", "AL", "Ge", "#G",
				")E", "=V", "Lk", "OP", "MR", ")e", "#M", "8M", "5V", "8W",
				"BZ", ")V", "L_", "+I", "GR", "6e", "EM", "+8", "#5", "+L",
				"%)", "YZ", ")R", ")W", "6B", "%5", "BH", "8Z", "6q", "89",
				"#r", "VZ", "#+", "AE", "+X", "%6", "8X", "HY", "P_", "8_",
				"58", "+V", "IZ", "X_", "U_", "9_", "Me", "AI", "%_", "5e",
				"+Y", "BM", "%M", "B_", "#)", "8P", "RZ", "HU", "69", "LV",
				"WX", "UX", "#Z", "+9", ")B", "5B", "#A", "8R", "VX", "#H",
				"%H", "%Z", "#8", "59", "LR", "%8", "Le", "+R", "%X", "#6",
				"VW", "EV", "#X", "#_", "EY", "8B", "6P", "EU", "+P", "WY",
				"5R", "%E", "RW", "kq", "Ye", "Yk", "Ik", "Eq", "%V", "%W",
				"#E", "9V", "Rk", "ER", "9X", "#k", "VY", "Wq", "IU", "#U",
				"#I", "IX", "PY", "IW", "BE", "IV", "BX", "RV", "%U", "Xe",
				"#%", "BU", "#9", "IP", "UY", "%I", "%P", "BR", "BP", "eq",
				"RY", "%B", "Re", "#q", "%R", "BI", "9I", "PR", "%q", "Iq",
				"Pq", "fq", "7q", "7B", "79", "Pf", "Rf", "Bf", "Yf", "%7",
				"7Y", "7e", "Wf", "7X", "#7", "Xf", "Vf", "#f", "Hf", "67",
				"+f", "_f", "+7", "8f", "7E", "7A", "Af", "7V", "7D", "fr",
				"7G", "7Z", "7W", "Ff", "5f", "Lf", "Tf", "^f", "7r", "ft",
				"@f", "Jf", "=f", "7c", "Of", "7z", "fp", "7J", "7l", "(7",
				"7=", "fl", "7T", "Qf", "7S", "Kf", "7d", "\\f", "(q", "(f",
				"qz", "7Q", "7F", "!f", "df", "(z", "Yz", "(R", "Qz", "(N",
				"Xz", "/z", "cz", "Nz", "_z", "Zz", "Kz", "!S", "Vz", "Jc",
				"S\\", "\\q", "Gz", "\\_", "lq", "(8", "Jz", "Ez", "cl", "DQ",
				"5Q", "J\\", "(A", "!N", "9K", "J^", "Jd", "Kq", "AQ", "Sc",
				"8l", "9Q", "!q", "Kr", "cd", "!t", "EQ", "@r", "^c", ")Q",
				"!)", "Il", "_l", "Jp", "%l", "!E", "!8", "A\\", "@c", "!U",
				"P\\", "QR", "@J", "Ke", "Yl", "FJ", "!e", "/_", "_p", "DT",
				"Ad", "FM", "!5", "Md", "AD", "LS", "/A", "@k", "N_", "S_",
				"L^", "+J", "Gc", "G_", "/L", "+F", "AS", "@e", "cq", "Ap",
				"!M", "Je", "6J", "AN", "!P", "9S", "Gr", "Dq", "Fk", "JX",
				"@M", "@G", "Yc", "=_", "Id", "Nq", "=S", "NR", "dk", "/E",
				"AO", "Ud", "5A", "@A", "@q", "AT", "Jk", "/q", "Yp", "JW",
				"Pd", "A^", "FX", "M_", "_r", "^q", "OY", "_t", "Z_", "qr",
				"6A", "#S", "JY", "W_", "AM", ")X", "=E", "Oq", ")_", "8A",
				"V_", "=q", "=e", "%r", "%L", "E_", "AY", "_e", "MX", "LY",
				"_k", "AX", "9T", "MY", "Xk", "Vk", "Lq", "8e", "I_", "#Y",
				"%k", "Mq", "8q", "%Y", "Aq", "+q", "BY", "Hq", "#R", "IY",
				"5q", "Vq", "Bq", "9R", "Rq", "9q" };
		for (String hom : homs) System.out.println(hom + "	" + isHomophone(hom));
	}
	
	public static void testIsHomophone2() {
		List<String> list = FileUtil.loadFrom("/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/tests/homophone-shuffles/z408-symbol-triplets");
		for (String hom : list) System.out.println(hom + "	" + isHomophone(hom));
	}
	
	public static String toLargoZ340(String inp) {
		String result = "";
		for (int i=0; i<inp.length(); i++) {
			result += mapLargoZ340.get(inp.charAt(i));
		}
		return result;
	}
	public static String toLargoZ408(String inp) {
		String result = "";
		for (int i=0; i<inp.length(); i++) {
			result += mapLargoZ408.get(inp.charAt(i));
		}
		return result;
	}
	
	/** generate homophone groups for Z408.  returns map.  key is plaintext letter.  value
	 * is set of cipher symbols assigned to that letter. */
	public static Map<Character, Set<Character>> z408Homophones() {
		Map<Character, Set<Character>> map = new HashMap<Character, Set<Character>>();
		
		for (int i=0; i<Z408.length(); i++) {
			char p = Z408_SOLUTION.charAt(i);
			char c = Z408.charAt(i);
			Set<Character> set = map.get(p);
			if (set == null) {
				set = new HashSet<Character>();
				map.put(p,  set);
			}
			set.add(c);
		}
		return map;
	}
	
	/** takes a key, cipher, and plaintext as input.
	 * returns number of positions for which the cipher-plaintext
	 * mapping is correct in the given cipher and plaintext.  
	 */
	public static int countCorrectMappings(Map<Character, Character> decoder, String cipher, String plaintext) {
		plaintext = plaintext.toLowerCase();
		int hits = 0;
		for (int i=0; i<cipher.length(); i++) {
			char c = cipher.charAt(i);
			char p = plaintext.charAt(i);
			Character val = decoder.get(c);
			if (val != null && val == p) hits++;
		}
		return hits;
	}
	
	/** for the given Z408 cipher, output the full sequences of homophones */
	public static void dumpZ408Sequences(String cipher) {
		Map<Character, Set<Character>> map = z408Homophones();
		int size = map.values().size();
		String[] sequences = new String[size];
		for (int i=0; i<sequences.length; i++) sequences[i] = "";
		List<Set<Character>> list = new ArrayList<Set<Character>>(map.values());
		for (int i=0; i<cipher.length(); i++) {
			char c = cipher.charAt(i);
			for (int k=0; k<list.size(); k++)
				if (list.get(k).contains(c))
					sequences[k] += c;
		}
		for (String seq  :sequences) System.out.println(seq);
	}

	/** try to remove the worst-scoring positions (on average) to produce a new cipher text that might produce
	 * better plaintexts 
	 */
	static void maskZ340() {
		
		int[] remove = new int[] { 0, 52, 51, 1, 48, 50, 49, 2, 53, 54, 288, 3, 334, 289, 55, 286, 287, 189, 304, 188,
				187, 47, 190, 59, 305, 4, 56, 185, 285, 155, 154, 61, 183, 233, 157, 57, 60, 184, 191, 232, 186, 280,
				279, 153, 234, 181, 5, 156, 180, 58, 303, 158, 281, 290, 235, 46, 333, 152, 306, 291, 192, 182, 283,
				282, 193, 151, 179, 292, 252, 302, 62, 284, 6, 278, 260, 293, 236, 231, 259, 88, 253, 194, 90, 178, 45,
				261, 150, 316, 307, 317, 301, 159, 251, 177, 89, 63, 91, 93, 294, 87, 26, 195, 92, 230, 27, 315, 318,
				258, 262, 295, 314, 176, 319, 7, 308, 332, 28, 85, 321, 320, 300, 277, 254, 221, 257, 328, 160, 114, 94,
				263, 25, 44, 220, 255, 313, 16, 256, 229, 29, 86, 329, 8, 326, 327, 296, 139, 250, 228, 95, 309, 222,
				298, 84, 18, 196, 330, 199, 17, 331, 264, 15, 140, 310, 43, 83, 325, 322, 137, 113, 297, 312, 237, 115,
				225, 299, 324, 198, 224, 161, 149, 9, 274, 272, 96, 249, 311, 100, 223, 138, 202, 197, 30, 323, 201,
				136, 271, 273, 276, 175, 24, 275, 141, 242, 82, 219, 227, 244, 99, 112, 241, 200, 101, 226, 42, 97, 243,
				174, 248, 81, 207, 98, 245, 64, 203, 111, 23, 135, 65, 173, 148, 12, 265, 238, 247, 246, 102, 13, 116,
				169, 19, 31, 143, 14, 162, 218, 10, 170, 11, 206, 205, 240, 147, 110, 142, 172, 163, 239, 208, 204, 117,
				270, 168, 134, 41, 164, 68, 103, 66, 80, 109, 67, 22, 171, 127, 32, 129, 130, 126, 167, 128, 39, 165,
				217, 33, 133, 40, 131, 216, 269, 20, 144, 69, 118, 79, 125, 21, 36, 209, 268, 108, 38, 166, 71, 78, 37,
				146, 266, 132, 267, 35, 107, 124, 70, 104, 123, 106, 34, 105, 72, 77, 122, 145, 210, 215, 75, 119, 121,
				74, 214, 213, 120, 211, 73, 212, 76 
		};
		
		StringBuilder cipher = new StringBuilder(Z340);
		
		Set<Integer> removed = new HashSet<Integer>();
		int n = 4; // we will remove 1/n of the cipher's symbols
		
		int i = 0;
		while (removed.size() <= cipher.length() / n) {
			int pos = remove[i];
			for (int p=0; p<6; p++) {
				removed.add(pos+p);
				cipher.setCharAt(pos+p, ' ');
			}
			i++;
		}
		System.out.println(cipher);
		System.out.println(cipher.toString().replaceAll(" ", ""));
		
	}
	// http://www.nickpelling.com/ChallengeCiphers/Constrained-Homophonic-Challenge-Ciphers.zip
	public static void nicksConstrainedCiphers() {
		int[][] ciphers = new int[][] {
			{121,213,310,406,516,108,200,323,416,513,112,208,308,409,515,102,216,309,425,509,114,215,309,417,507,102,201,323,401,517,111,200,306,408,500,113,203,313,407,512,103,223,313,403,511,119,213,316,416,511,102,204,324,418,517,120,203,324,407,516,105,209,312,401,504,117,208,310,408,500,113,203,301,425,513,115,201,313,408,515,115,214,308,406,501,122,204,322,408,509,114,209,305,412,504,117,213,316,402,509,100,200,310,423,513,100,214,320,419,509,114,209,309,419,520,101,200,320,416,518,120,211,313,403,509,103,207,313,421,513,107,209,305,407,523,115,224,313,416,508,102,203,306,416,514,107,200,310,401,509,103,212,324},
			{116,215,315,400,500,104,223,315,401,516,116,206,319,424,501,119,211,321,411,521,111,213,323,418,500,103,202,323,407,512,119,215,320,403,500,104,204,316,408,500,124,214,313,401,503,116,215,315,400,500,105,201,308,400,525,114,225,316,411,513,100,225,319,406,523,109,215,315,413,503,100,222,310,402,525,110,203,304,411,505,104,222,314,407,504,119,213,323,418,513,119,211,321,411,521,109,214,313,424,501,122,202,311,405,505,101,220,321,411,521,111,215,310,414,524,105,201,319},
			{103,203,304,408,514,116,220,311,412,522,109,200,312,412,515,108,210,307,400,523,100,206,301,400,520,117,223,318,401,519,125,205,306,408,514,116,208,301,400,508,118,205,300,413,509,123,215,319,401,511,117,220,312,402,507,112,204,321,410,518,116,223,320,414,515,108,210,317,401,524,112,203,318,401,511,117,204,321,410,515,116,206,317,410,514,109,222,306,408,514,116,216},
			{105,223,309,414,521,114,209,303,421,507,124,205,300,424,524,105,215,305,425,519,108,205,307,414,507,108,223,317,409,504,125,219,317,409,512,111,205,319,412,517,104,205,305,409,507,121,225,314,405,504,110,202,321,424,520,112,222,317,411,507,105,220,305,409,523,116,209,320,412,504,120,220,305,406,508,106,219,316,409,517,122,214},
			{103,200,303,405,517,121,215,307,408,514,124,218,314,414,502,113,205,302,415,500,102,206,320,415,514,124,202,325,415,512,112,221,309,411,524,122,217,311,402,512,101,224,308,415,519,113,221,303,422,512,113,217,314,412,518,113,204,309,400,521,111,216,325,422,523,110,220,312,407,500,123,207,325,422,523,110,220,318,400,512,109,225},
			{120,215,316,401,501,113,209,319,425,504,105,215,305,400,521,101,209,304,403,524,116,215,304,403,515,118,217,325,407,504,105,206,325,408,509,107,215,313,419,524,117,201,320,412,503,116,207,315,402,504,107,215,316,417,504,105,201,312,423,525,120,204,325,419,505,117,207,315,410,524,101,222,319,400,505,118,215,314,412},
			{101,221,324,421,512,120,225,307,413,501,120,220,325,422,506,102,211,311,409,503,101,204,317,407,519,118,209,301,409,503,114,220,309,408,506,102,223,320,417,519,106,224,315,411,517,114,207,316,424,510,118,201,319,412,517,114,209,314,411,510,111,217,312,409,520,114,209,315,422,501,105,220,300,410}
		};
		for (int[] cipher : ciphers) {
			String c = fromNumeric(cipher, false);
			System.out.println(Arrays.toString(cipher));
			System.out.println(c);
		}
	}
	
	public static void main(String[] args) {
//		System.out.println(toLargoZ340(Z340));
//		System.out.println(toLargoZ408(Z408));
		//System.out.println(Ciphers.cipher[13].cipher.length());
		
		//for (int i=0; i<cipher.length; i++) {
			//if (cipher[i].url == null)
			//	System.out.println(i+" "+cipher[i].cipher.length() + " " + cipher[i].description + " " + cipher[i].cipher);
			/*if (cipher[i].cipher.startsWith("A")) {
				System.out.println(cipher[i].description + " " + fromNumeric(numeric(cipher[i].cipher, false), true) + " " + cipher[i].cipher);
			}*/
		//}
		
		//unsubFromRaw();
//		System.out.println(fromNumeric("1 2 3 1 4 5 6 7 8 2 1 9 10 11 12 13 14 15 16 17 18 19 20 19 6 3 21 22 23 19 14 24 25 26 27 9 24 7 13 28 29 30 13 31 19 32 15 33 13 34 31 7 35 36 37 13 21 38 9 39 40 32 41 26 42 6 43 2 44 19 38 3 45 46 47 48 49 28 24 21 50 51 16 5 12 52 34 32 31 11 53 14 22 30 54 26 26 25 17 55 10 6 18 14 17 56 57 3 7 45 55 40 48 13 58 12 34 59 37 53 52 21 60 28 1 46 32 26 31 2 29 14 17 43 28 6 61 19 55 20 1 18 32 33 53 54 5 51 49 36 10 4 9 48 12 43 40 5 61 37 1 46 46 7 19 14 6 39 13 51 18 11 54 2 55 18 36 3 5 32 21 54 46 16 19 35 47 30 46 32 28 20 27 50 4 32 21 20 49 23 24 32 55 4 3 48 60 21 61 48 35 37 49 18 2 46 23 7 11 36 33 43 42 54 32 13 55 6 10 60 52 38 18 60 36 13 35 34 26 4 58 5 22 19 21 59 62 14 25 21 57 53 4 61 12 29 20 33 15 43 48 30 7 60 56 32 26 28 1 32 32 55 20 27 21 49 34 2 40 42 37 17 10 53 61 18 5 5 1 41 16 58 56 31 62 20 4 36 7 17 32 36 51 17 40 37 2 49 26 19 45 10 15 55 21 22 11 33 61 29 51 34 52 33 32 23 14 6 46 34 28 48 26 4 48 12 26 20 45 31", true));
//		System.out.println(fromNumeric("1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 3 18 19 1 20 21 4 22 12 23 24 25 26 19 27 28 7 29 30 31 32 33 34 35 36 29 37 21 13 26 38 39 14 40 41 17 33 21 42 43 3 29 15 44 2 5 45 7 46 47 4 5 30 48 21 49 19 12 39 14 36 50 45 22 6 28 40 17 4 51 39 16 37 33 39 38 5 21 5 27 52 23 21 37 52 26 8 10 31 43 53 30 54 55 20 9 51 56 32 5 39 57 25 2 4 18 29 48 21 32 35 51 19 24 36 6 58 45 38 10 10 59 39 12 16 14 56 27 48 9 50 11 17 48 42 34 5 8 54 23 21 10 34 30 23 3 60 13 48 28 34 7 61 58 56 57 4 20 55 38 34 25 39 58 12 51 1 62 10 51 14 49 11 46 15 38 16 17 52 31 22 63 17 48 4 54 59 54 23 37 64 4 24 47 58 63 45 57 20 28 54 41 35 32 26 29 14 56 45 14 57 14 25 60 38 27 4 45 11 19 25 31 12 57 30 36 10 11 47 52 14 17 3 46 1 34 31 13 43 39 22 46 16 62 27 50 23 48 63 4 3 6 26 25 44 46 29 33 21 31 2 28 45 35 8 36 56 21 59 54 5 35 51 14 50 19 13 18 60 49 24 15 22 35 52 38 6 53 64 7 57 47 26 39 32 65 28 17 5 9 11 18 63 13 29 7 37 16 41 23 61 10 55 19 13 12 22 57 7 36 35 4 12", true));
//		System.out.println(fromNumeric("1 2 3 4 5 6 7 8 9 10 11 12 13 3 14 10 15 16 17 4 18 14 19 11 20 21 22 23 12 24 25 1 3 26 27 28 29 13 6 12 30 31 32 33 34 35 8 33 23 5 11 2 33 36 37 1 34 38 16 39 40 9 41 39 42 33 43 44 6 45 26 46 5 47 41 14 12 25 26 37 48 21 49 14 50 8 1 2 38 51 3 52 7 27 28 31 53 8 54 50 18 55 34 56 10 38 34 2 6 11 42 12 21 25 44 37 1 13 57 45 51 3 13 44 58 6 10 59 15 53 39 47 43 60 38 17 14 36 33 43 26 4 17 50 6 23 54 27 61 45 46 28 37 19 28 34 13 7 36 8 9 2 20 4 37 49 6 43 41 51 21 7 50 45 14 16 57 34 38 7 47 22 12 37 39 8 52 35 2 26 54 7 51 14 3 28 34 44 4 23 56 62 6 22 2 53 46 42 6 48 19 39 12 58 25 58 8 3 20 10 30 13 42 16 29 51 27 49 14 34 17 36 49 14 51 13 13 12 56 1 62 47 63 19 31 6 21 45 14 50 47 35 43 35 26 29 34 61 52 56 35 27 31 8 20 7 13 62 2 50 36 22 56 53 50 32 64 51 27 33 41 33 35 14 9 54 34 40 38 60 31 20 47 12 43 10 63 4 50 28 25 26 44 34 4 27 42 15 14 64 40 5 14 64 33 49 33 5 51 28 40 36 58 57 45 46 59 5 13 18 42 37 34 14 29 40 28 58 30 34", true));
		//System.out.println(fromHex("2a 2d d19a 2a 2b e280a1 c2a4 c2ab 48 2d d08a d088 d19a e28098 e280a1 d08a d088 e280a1 d09e 2d 3d d094 d09a 2b d098 d088 d08a 3d 2a d098 c2ab c298 d19a 2a c2ab e28098 d084 e280a1 2d d088 d08a 3d 2a d19a 2a c2ab e28098 d09e 2d d198 d198 2d 3d d094 d19a 2a c2ab e28098 d198 2d d19a 2a e280a1 3d 2d 3d d094 2a e28098 2b c2ab 3d d098 d19a 2d d09c 2a 2b 2d d19a 2d d19a 3d c2ab 2a c2ab 3d 2a 2b e280a1 d09c 5d c2ab 3d 54 e28098 d08a d094 e280a1 2d 3d d08a d09a e280a1 e280a1 d09e 2d d09a 2d d198 d198 d19a d09e 2d 3d 33 d198 2d 2a 2a d198 e280a1 d09e 2d 48 d19a d08a 3d 48 d084 d08a d09e e280a1 d08a d19a c298 2d 2a d09c 5d c2ab d084 2a 2b e280a1 d19a d09e 2d 3d"));
		 
		//decodeGilesCorey();
		//System.out.println(numericAsString(Ciphers.cipher[1].cipher, false));
		//System.out.println(numericAsString(Ciphers.cipher[1].cipher, true));
		//System.out.println(Arrays.toString(numeric(Ciphers.cipher[0].cipher)));
		//System.out.println(Arrays.toString(numeric(Ciphers.cipher[1].cipher)));
		//CipherTransformations.testToList();
		//testAlphabet();
		//test10s();
		
		/*
		Cipher c = cipher[cipher.length-1];
		System.out.println(c);
		String line = "";
		for (int i=0; i<c.cipher.length(); i++) {
			line += "[" + c.cipher.charAt(i) + "]";
		}
		System.out.println(line);*/
		/*for (int i=0; i<cipher.length; i++) {
			System.out.println(cipher[i].cipher);
			char[] c = cipher[i].cipher.toCharArray();
			System.out.println(String.valueOf(c));
		}*/
		//dumpCiphers("/Users/doranchak/projects/work/java/zodiac");

		
		/*
		Map<Character, Character> map = decoderMapFor(cipher[0].cipher, "IBOUTFRRUHNINAKNILTEANTSLAIXESUGISAAERUEMIKINSNTLIBOTTIRFOURRTSEEOGNASUELGBINGIUEUHHKTOORIFSTUIRHIUNANTGEGDLSNMRYKRELNNNKNBIBTSEALXLEXNSISUEVEIONIARAAYHGTLFMRERBESTISIAATMEOKGGBNAXATVAUGSNSRFOBNUGAHUEEUAILYSOATELNGIARNSOOFKILRIGNHVERRHGEEAAYIUEHSRBURMIGTENRERLAGNSTREIUDKNORAENIYRSEIFIANSNEESIRLIRUSOIAKNSTNIHIISIGAAXSAUTRRUUAILTHOLISOBNGSE");
		String thomas = cipherByDescription("Thomas Dougherty's rearrangement of the 340").cipher;
		System.out.println(decode(thomas, map));
		dumpDecoderFor(thomas, map);
		*/
		
		//for (int i=0; i<1000; i++) System.out.println(shuffle("012"));
		
		/*
		String symbols = "#()*+78GHJLMSUWZ^lpz";
		Set<Character> set = new HashSet<Character>();
		for (int i=0; i<symbols.length(); i++) set.add(symbols.charAt(i));
		int total = 0;
		for (int i=0; i<cipher[0].cipher.length(); i++) {
			if (set.contains(cipher[0].cipher.charAt(i))) total++;
		}
		System.out.println(total);
		*/
		
		//char[] c = cipher[cipher.length-1].cipher.toCharArray();
		//for (int i=0; i<c.length; i++) System.out.println(c[i]);
		//smokie31();
		//System.out.println(toNumericWithPadding(Ciphers.cipher[1].cipher, false));
		//System.out.println(fromNumeric("22 28 23 26 11 27 24 29 30 25 34 18 42 12 38 43 31 13 5 14 7 1 54 46 15 24 50 23 47 48 39 32 55 8 20 16 54 35 22 51 23 49 33 40 44 14 17 55 36 52 21 2 37 26 24 30 29 25 36 19 58 24 28 9 18 3 32 13 23 35 53 20 12 16 41 45 44 11 48 52 6 12 7 4 54 47 13 33 3 34 22 46 51 21 14 32 40 47 50 10 2 35 19 15 45 39 55 48 1 36 23 33 2 29 38 17 3 30 31 51 39 27 24 30 29 49 40 32 14 52 20 25 37 18 19 24 56 13 48 33 12 53 21 11 32 41 47 52 51 20 44 23 28 29 22 36 18 12 60 42 13 45 14 35 8 15 23 50 24 46 14 57 13 34 5 12 51 52 11 44 53 21 4 35 19 12 52 51 25 36 18 61 40 54 45 44 39 7 26 47 38 16 17 59 24 50 20 3 19 23 45 30 51 21 13 6 14 48 52 43 2 44 53 39 16 22 52 23 49 51 20 1 50 58 21 15 37 24 9 25 14 24 59 23 31 30 5 13 45 12 6 40 44 36 22 35 42 2 45 3 10 23 8 11 4 34 9 3 29 28 51 20 12 24 21 2 56 13 27 25 29 30 14 10 58 24 31 30 5 15 7 41 33 14 32 62 48 29 1 57 13 47 23 59 22 28 29 35 40 52 18 23 56 12 61 39 55 33 62 36 2 32 11 6 12 8 3 54 46 13 61 38 55 58 24 30 31 53 44 62", true));
		
		//System.out.println(fromNumeric("121 213 310 406 516 108 200 323 416 513 112 208 308 409 515 102 216 309 425 509 114 215 309 417 507 102 201 323 401 517 111 200 306 408 500 113 203 313 407 512 103 223 313 403 511 119 213 316 416 511 102 204 324 418 517 120 203 324 407 516 105 209 312 401 504 117 208 310 408 500 113 203 301 425 513 115 201 313 408 515 115 214 308 406 501 122 204 322 408 509 114 209 305 412 504 117 213 316 402 509 100 200 310 423 513 100 214 320 419 509 114 209 309 419 520 101 200 320 416 518 120 211 313 403 509 103 207 313 421 513 107 209 305 407 523 115 224 313 416 508 102 203 306 416 514 107 200 310 401 509 103 212 324", true));
		//System.out.println(fromNumeric("121 213 310 406 516 108 200 323 416 513 112 208 308 409 515 102 216 309 425 509 114 215 309 417 507 102 201 323 401 517 111 200 306 408 500 113 203 313 407 512 103 223 313 403 511 119 213 316 416 511 102 204 324 418 517 120 203 324 407 516 105 209 312 401 504 117 208 310 408 500 113 203 301 425 513 115 201 313 408 515 115 214 308 406 501 122 204 322 408 509 114 209 305 412 504 117 213 316 402 509 100 200 310 423 513 100 214 320 419 509 114 209 309 419 520 101 200 320 416 518 120 211 313 403 509 103 207 313 421 513 107 209 305 407 523 115 224 313 416 508 102 203 306 416 514 107 200 310 401 509 103 212 324", false));
//		System.out.println(Arrays.toString(toNumeric(Z408, false, true)));
		//System.out.println	(toNumericWithPadding("shwshl/t|~w/h4wo+h~/33=~=h~4hn+h8~hh/%04n=s3|", false));
		 
		//testIsHomophone2();
//		String[] pts = new String[] {
//				"tttsasoptexhkeekpletsoonstdtvialostboroieelforwoanhoistoptxhootchelderstabmniendelsgovtloatiihslegnyoihroiodexglopfwoopsmstopvelddtorhhelaileihllagnstntoptahtqksirisdodenalexelintosherhsditahufonimssvebwotntghootshredidkoniabentgsterohaityoeeotopelitefoxtrkonecosseayottitreholdstttacotopeeselllunogltvtotptxfnholtumolurolleasooidufoottosoudanpetyeoamiesaklusmoossntxhewntolavbfolugbitnptwlsedentlislddthkoie",
//				"leteeelneopsadkandebolhgslitsosihoochalsomineryhawunfelontpsalexsbitoadoectaodgiedoohsbilalosusikowenfsreshtbpoilnnynondtetonskitiolassoeeoidsudisowdoalontasbjasorfotliogeedpedswbedubassislasvnhaotoeskcyatwoosleeduretoialasscowlodobrauefteldoalonedobeneptaanabxlosmaeleesorouneteleeexatonedokidivgooebslalnepnwslebvthivanideeolhotvnhaleodevisgnklekaatomdsaivotoosowtpskygeleescneivocsbantydsetkaldssitioualso",
//				"lweeoernwsleadmanrenatncmlkeaslinastngresviiytfnebtszelaneleprwdeairsgesothiadckeraananitelsetmimabuszetyenralaitnifsaneheeanamirksrgeesoosidetrilabesilaneeenjamatzarrkscoodlerebnyetagemkeleeoiniahaeamtfpebsaerywetterskatieltsblaesatptozeurdsplaneraneiylegasiadramveutwweststsorelwwodpeanedamiriocaaonalplnwliberonohniogsireoatnsroinplwaeyoklcnmlumpehavelaioahaamabelemfcwtooatiyioateninefrmermilremirkstares",
//				"isambmersoneanaarsefatigvieamduniassiseyoxnstraitltchmicranereskeentososbswhangeesalimfnttidytvnallpchertyitenlntrsaccrowmacrmanteseseeoobdnnytsnulloshicratefjavarhateeogbonnesylftoteseveyiteosihawammasaralsleetsotretdeathyusoliloserrtbhapenoricresafestnasachekeavxtptssysrotcotmissbkracrenaansnogclofmirirsnsleeofowinoscnsebatidtosiriscotoeugraipartwaxouanoawccvalaneaagstobmsstnolsyfhraasvetahisyvntestaeyo",
//				"peulllererstedierdegygantpeuvlnoayweanerrbotlamaaahtclporustreekteolrnswlefaxdneedytavgogaplrhtoitartctalralestogrtmtorsfluorviolewenttrillodrhdontaswaporuatgjetxacyleernlidsedraglshentterpatitaaxfylviemruawtteleshaelleegarneraptswearhlcuredrrporedxgetlsunetaekeytbargeerwarhtillpeelkruoredyiodoinotigvprprestateigifaointodelygallitarpeosliennriprirafxbsneoiyfootyaustimnegilvetloitergarumdteliapdrtolewheerr",
//				"alfvovaklseionsokialactamadfixyetaentraespeondetoursjvaskfeibaldieensraeonwtenadaiagtilecoaxermesgulsjidnetneegeckoesskawvfskisendeariisooxenerieyguaetaskfoilhomedjanadsaooneaieulnarerimdeaoirottewavisnebfuegianlardanxdocteynsuagaeedbrojflansbaskaielaonefrostedaampolclleedsrsonvallodbfskanaseierasgoliabakleouiaolrwterrseiaoactxnrotbalsanrdyaksalsbowepayoerawssmaufeisealcooinonergneltkfeimanstaiemenderoaes",
//				"oystetotysatraertinediereoeshtimedleecogsfmanivetpkintoptsattoyztamsscaleeunbarenidrehemitotgkemerprintingesaarmitaviptautspthemselocttstetmagkimirpalnoptsttexrebindsoesretaanigpenakacteegottwaenbudtheevtsplrtonyakinsteringiesporalaitkensroastoptnibenanascrinazodeftriyygliskitstoyyeztsptnademimwrprtehototyaaptotewuemwciminedietswaetoypanweirteorettubfairmwduppedpsatevryiteheanmwregentsviensenoigemselkrogs",
//				"eleksketlithslostnosriaftenetzecarawadeaixcribeatgunskeetethrelphochidraswimalfnonryatscitezautcoygvnshbiaahotycitrenetrikeettochnaedhhieszclaunceygrameetethsqstabsrhenifseltonagsiruodhtnaetheramairktoweregayheilrubohznsimaewigeyraobrussevelireetonasoritedsnmopertxtvillaabiunehkellspreetolrocncefeyesteretltrgheeseiacedncnosriazherarelerieneftoevortiaxrescerieetrgethoefliestwriceywasmteentohomenatchnauseai",
//				"eeitntodeonghsahdcoftouiaeriestrutadugotokrebsdunrpextendingloejguraogyandormsirocteuefronestparaervexgsbtuaunerodedendyotindeararaogggornsrstpcrteryarendingfwhamsxtaoroinrsnoctrfbypuggartengueurmotteaddliraegobeypsoasrhorttdoreeyauslpnxivosolendocmfoebnigherujotaknvoeetasoperateeenjlindostarcruinerfeeledenergorfuourugercontousaueuleenyburtidaevalnomkythrutonnatringadieornedebruedtfrdidcaoaarectararaphoto",
//				"sdrmimacdyehewtecrtaangelsermvregardgsaayfeinbogokrlamspcreheadthieaysiridolsweetrasgmaenosvarletskulahbnagaiesenciolpciomrpcmteaerashhyjivewarrerskirlspcrohazelsbaaaaeyeijwetrakanirishleasohriglsoammtdoerkrshandirbtaveenlardykssiriberiaruawyespctrsatinersellitaalfounddarbyrljamsdditerpctwatererepsjamsescdeikhajarogerslertiangvarigesdpinrerectsuteoosfireeraopplakrehtoednjimdinersdaalcrorltatlsraleaerreaay",
//				"efavnvrpfelsnxanptomcodstetaheardcagdpriearuawsdkbnervespalsyrfzsareeplangorsxstotcadhmrokeeintraabeerswaidealaropusesplovaspharetarpsseinerxintraablarespaksmintswrcertesnixlotibmalnapsttieksaudrsocvhagsyabaasraflnwoeetnoriagebealaawynnraerxeyespotsmoualapnerazrctakeoffiaweneieveffnzyaspoxcartrassaimheyepflubsrimaodrapertoncodeeaudyefslaataspaeeaykosalanracosstcbalsassfoinhguaraagimrpasttoearetitretannrie",
//				"etyrcrontferalsangaradiategyashuiamriioufjursswireirereenyertotaraunfilmcrvbelagagapiarudresuituspearerssuinaepudnrwrenlvryenasungmoirrfkcsuluiguhpelmbeenyrrrzateseanogfackleaguersliairtguerreribevarasrwtyemprostlisansgadbuhrfeeplmasticeyaolfteenagerarseyiarbaaoatjradttumsfirknrettcatyenalasugueaepkraetentererokreviueirugacadisneritetelseghanseastrvejlhaueaveetaeyerswatdkcarrsueprurbnywgtansbegutungmiaouf",
//				"atopepottgdldeadtyresahuaaborxiwhsznhlotgkwinephasletpantodlrotflawsglazenacleubrysshrewaaaxtlawassmetlenthsadswatipentaapontrawsbzolllgvexwetlywissazcantoaleqdaletssobguevedrytsenalallabtaalaihclaspranproszslontalersxbdactingsasazaerletomoegrantrylerindoldecafosakamatttzeglevspattefrontresawywaunsverarattdisloveaahwalewyresahxsaihratnanabiutaamaraalkaidwasannassodlaputaverninwasntectopyarsacaytawsbzldotg",
//				"atebabalterslgolltoawetlyarersaktwestratefkpucitdmonvballershatjsukaerceasebeglrotwstrakedastoykosmnnvscuttaurskelpinllcebellrokarearssegaskgtotkasmceballedsaxlyecvwaarelaggrottmaucoursyrtadsiptbeewbrosihemessautcocoasrlebtasemasceuchoavenagehalloteaopurerlnbujawyfdnettteceongabattajhellogwoktkillsgarahaltrpmsagaietkirnktoawetsaipthatlcuiralloanohdeefcalkiwellywmersoiltegarspukisstableityoaobattykareolate",
//				"althohellsitvaivleduriapiaztlsimarecatersfmeedkangliohailtitselwtamestbeocatnapzdernaluminasrlimingriotderaeainmilekiilbahtillimezeetttsnosmarlemingbetailtntuxvindoreezsponaidergueblattizrantyeatnarhlickstgenteelblddeszvitricsganbeadslootreassaildenudeeittvitawerifnrillredslinehallowstildarimemypinnulasalliegtenuyaamytimedoriaseyeasalibeyzipliarisnanfbivmyraiiirgtitikplinolceemyncrutltkeideitaerimezelvers",
//				"tclaraaacnedbnebaahnhotatttlowesthaktdarnusagiptrilisatoaledeacydzsendnarkssjnathahmtonsortwrltsemivisdigrtezemsoaapioansaloaoesetaadddnirwsnrlaseminastoalrdnfbtjisheatnarineharingnlzddttrtrdeatsjshaoekpeliamdagcnlihewtbosreknitmnazielrslvannetoahajnhageldbiszyahturvoccrainliieatccryeloahnhesaseaominotetaceaidainestsedisahrhotweeatetcongeteaaetveersjunebsehsoothiledepacoirokagsemkrnsalpatheestartsetalbarn",
//				"utomemantrasorlonratscpiyujoamvepsolpeafrxeurebpdtessmugnoastatesteareroelakerijarsipatecdumfeyelitessserfpataiecnubsgnramognaleajoaessrhemerferevitrokugnodstwoyeessaajriehraarfttrretesyjfudstupkeasmallbtotoisartreeaamjockfvlrtuiroteteesoearrtugnaretauraoeoskteasyxdecttfoereshamutteetognarsleretigihtautuntautsahttapeteseraescpmatuptutgrrtjvinlueltdaexrvoetsaggystoaslbitchealuretilftknobryaalkurfyeajoeoafr",
//				"usovrverststytgyrrbleedisunoscsedersdneatweisahdnkhatvufrostoesztheatnarrsmoltinbreedsleenucahsegektattasadahseeerihaframvofrsgeanrentttercetahresekaroufrontlxyslateaentiretsbraklsahhntsnauntpidolmevsgshookretessahabacnyeoasstkuearhaohrtotettoufrbrllbissonyaohzeeswntessarathaeavussrzoofrbtegerepifeelsuourssikteelpmdepnaerbreedcapidousfaspnsirgutgonmlwasyepemffsekostghiseerssisepesalorohrsbagouraseanrhyeat",
//				"gtwosorottooucruorexileftgawkbpaeilaetrutvanjoeeyermzogoowooirthosaattalsalpacfaeridekxalygburtardeemzoojueasodalonemooalowookraaalrtootisbacurrapdealpgoowyoxlutaoziaratfsicoeruexjarstotaugyonnepaliokraeiweldorjtaroeabaulpupategdalsoirszwerctigooeraxenjowtumpshritvyelttulotrmiaogttshiwooeciraranfodixkgigotoneorixnleantmaresilebanneigtoajnapforgeriylavapuanilootiewooreftliskanjandauxpowertearpgrutaaalrurut",
//				"lehteteceeorateacageiconnltheyapoiironesexpasodoorburtlachorceevrzplentierwsltntgaiaoeepcolysbnpearfurrossolzoapccaduactwthaceepltienrrekeyptsbapaartislachoremanloriletenektogasrestbznrntslorsaoslwiteerdchriaresetboglytacssarerlatizocberhfeteclacgalegasohnauszveinxofceesioebukltleeevchacgtiepapsnaakeelclceoarrekeswopsnupageicoylsaocleatsstancelfecowlxtaapsiwaanirhoredneckeeraspsarseschdangleslasnpltibaese",
//				"esidadosskbassossurunpeireaidslienldecotkvisawhersgaydensibadosjafinkcaladeldsiarunteduiprestgriotsmayawatenfbtipsshansaedinsdoinalocaakrasistguiltsallensirauxsrdwynnoakiarsbrutsuaagfcarateranseldenddodhdisltaoasagwrnsaspltldksetalfwdgayimoskdensrudursabicsalfjonrvrmpsstlwkgarndessajdinsrsnoiuinintrudedessbssaoruneeincaiuranpesnnsedesnaanalisoemodredvalsinnennrnsibaohispraddsaintdtulsihurrnoleutrinalgsotk",
//				"ositstdtsrveeopetochweeeaoliqpblewtueddsrelatareenpfmtottiverdsteelyrdatsunrgoelcowoeqhleeopspalponxfmeatseyevoletarfttantittqplyltddeerssplospolbonatrottieehkeagamwydlressovcosnhtapedealsoeeoaergnwtqpurrintoedtsapacypleersburnooatearpsmixdorrottcoghcatvidefretdwaeexessstarpfsytossstrittcowploloetoshqorotsvanedshonelodflocsweepyoaerostatolbetpoxprengeabelownttawnivepresessquatlooushrtiroacyproosalyltpedsr",
//				"rentbtweedraartaemonorsmgrinkflasouisswedtatersshaycztreenraawevaxaodspubiatirmiomocsknarhrfeygatcaeczareesoxrcaretsceepatneektaoiuwsaadobfareymalcaputreenhanjagirzoowidmborromeanepyxsagierhantstiaotktisanaucaweepyroofiartelidarcpuxraybznewrdareeominoternsactxvwogthereeeurdycootreebvaneeorotamanmeconkrareertaawonnasanscamoborsfontsareepenilmetretahaitplaanoaeegoanratsmerobkiteancientensmgoottrmegaoiuyawed",
//				"ofstytacfsulkaokcerebrationsmzsdaboiaeansadrgeoawastetoacsulnafjlodesetoyishpatnrebtamedrwoznsidotaetelegnaeoutdrcrotactstsacmodenoaellsyyzdansedstatohoacswlevkipeebeanstyyaurenaegtsoelinnowlerahpsbtmoionsaotlagftsereznkrhnsisaottooensyeseaasnoacreperrgusekthojabiawerffnoesstyetoffyjnsacrabodedetatyemonocfuralayeesadeetderybrazeeranofatgenstcooeonwspatskdebsaaibasulootfryymirgdetinehcsoeireohoenidenoskans",
//				"rfmsosoeftbtriereatariaaersmchedarlrasoptvdolaeakudunsrgembtaofwtodetsnloriamiastareacadikrhpdedeeuyuntalpaeobedieoeugenismgecedeslostttnohdipdadeeunlargemktaxremanreostaonibtapualndostesprktzoaamirscereamuletolfndatehsriaperturenloaadonmyoitargetamatolbmsruaoworevkyiffplatdunesrffowamgetiredadzagenacrarefboutonaziadzsudatoriahezoaarfgnlzseaeeryeakimvnerdzriggerumbteeafinocroldzerpaaemeaeteearapedesldropt",
//				"erseaerardteayraaelfwisenessrjzbswhishrudobeaamstahaleepasteerrkexbudhchaiordyeslewisrfbitejuhnbriagaleaausuxtibiaemapacoesparrbushrheeddajbyuhebziachrepastefvandalwursdeadytleuafachxhensuetetesrdowerrimesahierarchalujsairuzidaeichxaehalsgrydeepaledfleatshaarxkrwnotgirruhadhadueerrakespalywrbebtepidfreeearteaerdftosbthabelawisjuteseerpcatszearegretodoczabtwoppnwastermeridarieabtiiufrasmenlurreeunbushharud",
//				"riatatheismlmoamensifprdareacedurfgrrahnskueosdrytewitrbeamlthixleussangareesodesnfarciupyreneauaatvwilsonrsemaupeedwbenetabecauseghallsnaeuonenudatngerbeaylizmassifshesdanomsnntioneealaenrylsereseftcardtatgalhoinessseempendrstrangesteaiavhostrbesnsiseomaamweexhfakyvpiingssewnstriiaxtabesofaunusdbanicrtreimetlhniserusawunsafpressertribnoseddearvatyeskndmusfebbaftamladdipnacreousarnieeadnassaernnausegemhns",
//				"soapipahoawltaithirespetasialargessdevanakgygumeistabpschawlxaojldgravesideesatirisielegpisantagiisdablugnerdwigphymacheepachligrisavllaniagantigriseseschaileftasubsraiatinawrinsegetdvlainsiloyeesesplidmxassilagoeturraitpenrdassiesduxtibadaaaxschriserygwavtaedjasakidpoonsuatanrpsooijxachrasigigotcinelsxshowyslaneoeegovagirispearoyexsocegoirthisdixieskertgoseccassawlimtopnildyggoidneehamiarriesinagristtana",
//				"tsnnonlisreaordoihttovtottingrpatorattlerfanibetossskntwineailsbazaartmroashyroithoetgtavotrestadesuskabietazeeavineswimsnnwigdaairltaarlorareshapesmrhtwinoatjotybkoaliroolrethestimsztatietoacnthysongdaeinsrealismsbtariovheparstemrzbisoknulrritwithyttnientoshzblotfouvsserbrsslantssobinwitrodahacoweltgtitisensalltcstactsahtoovtracntitswmicipoidtudiosyfmpoacoswwtosneadeosvloganiaceaethinehttadhthetaairsoler",
//				"esalollhselenionhoctaxesremandedeaykepleeddatedefvriblethaletlszerdaepayokionismcoagentdxfederrdogviibeeteearlgdxhadithailathnodamylpeeewoddierodegvayoethafetjnrnebaalmesowilcoevttarrpermeefeuaeonialnokdtavygeltsarecadmnxoeekevegayretrobailietethcontcatlapniorzlardfixsseyeeriwalessoztathciaododustgwtnetehslavelwtuiedupidocoaxedauaetestatumeshoeiotfindaenduaittravaleodssxwonkatdugketohadorcaooeoerdamyrnlee",
//				"mwukskeawerlluelainerotsemhubeostrintleneasixfdttatyakmtaurllewzlasceltisnartushniratbesotmenteseaavyalfxntcarasoaidytatakutabeschiellletsesuntisoaatirmtautlegletfarcehessturninaexttallehnmtlpitrtarkbendluaialexwttfncehlornoneamatiafltsauveuelmtanitenixrullyrazereatvowwnifetytckmwwszlutanuresispstatebmlmawrialetepatsplysinsrotecpitlmwttxphosaemveltatatolspratteraurledswotsbnixspanneraudiencermineschitlene",
//				"arndcdetriesnrtntzearoseiasnyoonsrlasweeignlepastopiadaftnesservsansiwslcauhpresezrmsyanotaoepintmokiaspeessaemnotlaiftsudnftytnsslewssibconrepznomoslhaftntsajnipparsesiecbreezeoaespawsiseatsylshpurdytaasnolmseersppesosnoheoaioamslapspcankerisaftezpaeleenwnihaverigtkorrelpipibsdarrcvsnfterrtnznyefmbayasatrelosebayusnywinzecrososylssarfseysoettaktstupgsonnyruffironestaerobcyalenymaeahtnaziesthazeinsslpneei",
//				"elansneileysfglfientpetroedasdvetplntreueleorsattichjnetiaysweltseeieralsnarbgrdnephtsteeteducoelhikhjssrutieyheeioahtiaanatisleidlersseasdeguceevhialretiatstqfobsjpiedersagyneuitracersoduetsmotrbapnslnawailhserlacsniddferuvneiehaleswcsjakegewetinebtnoryarfhretepoltkellulsechainellstwatingpleeemrthatseweilyoiseatmatemrheenspetdimotweltarmdvrileklwtablavfempattopiayslarleassnoremhnutriaaeonilreeuoeidlcfeue",
//				"atevavoutsayhoehualisoeciapetmikeshaeworsekrnexedstarvabueaynotjyokeswshaaitrocplasnetikodamrtikensmaryenreeoankourxabusivebutekephowyysgamkortakinsshtabuedyifhirerseopscagoalarsinstowyipradyaretrisvteaxneshnyontstelemphotriassanshoentaremoosnabularilrnaewhatojosiedmottrhestagevattajnebulosekakacbngitanautarsyogiaiekawakalasoemearenatbsnapicueamendiresihkasibbisseayexctogatarnkanarituexaileetaarikephthors",
//				"esinenatsitbloeltpmihactuelifnalchitcdasijlesgxcokeernestitblasnbaleidtietumrotlmphncfilaoenseulenkverbgssceatnlatexesttunistfeleliadbbiwenloseplanktimestiobiylurgrhealitewotmpskisteadbulseobaecmruhnfetxlikinbasstegmenllamsatikentiagleerivaoilestmprimestidlemanahujovasssigieewenessenlistmohelplatsnwifeletstekbawiaucladelpmehacneaeclesstsalatteevelourjtallahussuhkitbextsawefteslantsimtixpumeemepsulelielasi",
//				"olodadatlaicpweptathreeroonorselertfenasaglsbedelvtoydomtoictalkceleanitafurnwrntarserhlelosstolesvjoycebseeeisletsdomtiudomtrelentanccazaslwstalesvitromtolchqponeyreanarazwitasvhbitenconsolcasernurdrefdtovtscablitetesnpersefavositeettayojawatomttanhtsbionporekarogljellsteatozedollaktomttwrelalarmszhrototlisvcazhauelanolatareeseasetolmibanerteojetlungieplarummorvoicedrlezarfsblasfshrtodaoteeroasolenttpasa",
//				"utadedistasteoaesotereenounalragerozeniraxgtawfebphamdudsastritttegeancoezaboonntorselegeburrhogaspyamtwareeessgestfadscadadslagenointtakergorhogaspcobudsabteveoowmreinanekostorpeachentonrubtiteboardlazfrapostiatchwterneebrazapuscoewrhemayioarudstooettasaneabetiroxbyettrowahakeduttetradstoragogindskeluruststptikeiaeginagotereereiterutdcainansauyarbaoxcaegiraddorpastafntekelztagiszrebsafooteabuorogenoheira",
//				"itodadahtowceeaehegonaaxyitobautaneiasanoftinalasnryndithowclatjcvtooseeaielzextgenrabotasianrytarnkyncannaovwrtahilytheedothbatoteasccomaatenreturneelithoscopeyzannoatoxamewgennonervscytnischialzendbaillonercanteragoatealnuionireevalranokaeolithgezoginwoseylvjanyfskattneaorymodittajlothgenatethxtrmobilihtwincamoheathsyteganaaaohialittenhtuxhaikalsezfeuethnettynnowcalxtamabiinthrinolholeygoalienytotereano",
//				"apahyhelpsnmeoaelfserenoaanatjvrnrtunzeistritannwlcoghallanmtepimereszatyuokboonsfronterewajicaraolnogmatineenorelinollaohalltarentezmmsdyjroicfrvolatkallawmeneabagreensoydonsfiletacezmaniawmainkborhtauntaltometpacasejneekivuslaoateatcyganeostallsfbesitnazeokeieratwneppitascodehappyitallsorarfraolodetatalpnilmedeaonrazorfsyrenjeaintaplatanvolaanatwobtaverarollarlanmanopedytuitraouieklanfaseakafiarentceeis",
//				"smritieamkeassosaatoleoersyrmaenoldroielkantisboyieaxiseareagemzafnwkindtrhupseytaldomoneysalernoditaxasilowfedneatbaeanhireamonwydeiaakntansleanedindusearyaojsrpsxlweyketnsetalioinefiarylsyactouphlimorbgriddaeimnestwayseulerkisdndfsgetxrteskgseatapottierisaufzelraytemmldskeanwismmtzgreatslonanceednomsgsametiaenochoncianattleoawctogsmenicyeeaostogyhpanesnclheerlireaobementmrtincdrlouarbartwousalrnwydeselk",
//				"hotsnsoioaptlyalienawfirshetcguliweeiaotaelssobimrealshoitptsooqtvlsaadeneockyrenewticalfmhgteslatraaltostisvptlfisbaoidostoicalseeoattaenglyteelutrdechoitmtaxlskolwsoearneypnetrasdevatsethmtisickowscaebstrettosodeonsgelfctuearhtdevosenltaoyashoinekanssptalacvqowsemafooteoaeaesshoonqstoinywaleliroteachshiopsrtoeaioiliaalennwfigsisishoodsieuriahaasmokedulliwoooswrtptabrofencesslitetacitbesnsachetslseeelota",
//				"okirerackadeyihycotesjannositoaoasseatapazorueoawesbfroaciderakeemoratmseeagrinstoshateojwoopsnohhelbfeeuparmdhojcrobacmariacthorssateeaveooipsooahemsgoaciweexynrefsrasanevidtopeeumsmtenspowerragrasrtheoriesheaukmsetrosyjgpaeaeohmsmersefilaiaroactoretruditybgmeasnzwljkkpseasbvrrokkeeriactishooornahvetorockdreeaveraaortbootesjaorrrarokamursancholhrwarzmayorsaaanseidehonkjveteruorhepegcioontrhgoopnorsssyapa",
//				"rmdecegympeinsbnynitreeaarsdoltoerwlevgspjollaaeaeitherdydeiegmkinoepvowcluessasinrheotoearlsiaobhefthialseenehoeylatdyoueddyoboeswgviipaclossinotheowerdydaitznasahregspacaseinsetloinviassraisleesureoblaedewhiglmoiaielsneestlperhownaeichdfgsperdyinstilledvntenkgrajafemmswapitaeermmckeddyisrbonosadhatorerymeleigatsueosvtonicreelesleermdolsstaybrfbeausjotnosruddaredeibaameacollloshlsteydanaiebernsaoeswingsp",
//				"aelrarosesieduadsiteneanearlcsamaninaromsxmgkrratneafrayslieloevermhsrbianusbunrtinmacemetasmeemamnwaferkmahrimmesgraysburlyscamhrioreespasmumeimamnbisayslteeqdebrfnhorsnapuitimnekberreermateogasbunrcanrllnimeokeberthsrdesmansnambirrleaflwouslaystibetgkilrdasrvonextweeemirseaphraeeavllystunamimonympecalaseigneopeouamoramitaneashogalaeybkoransaawaltubxbadmonuyyennliearneepacngkmomnmesslriethasaimemhriedoms",
//				"wkioaoohksefcoachingtotdawoivaeltteitsonsxlneertytresowchiefbokzfullssueaisomodonitstvgloywanralastaesfeentlueslohnrechusoichvalloeosffspaalonrilestueowchiyfgjcamestloosdapoenintgeurusfaonwyfantomstovairbitesfoekurenlaocooneistwsueuebrasiaoosbwchnimgnneeisceouzotaxyaokkneesreplowkkazbichnotaliladcspgvwbwhkentfopgastlaselinatotalantbwkcueaoedhawaabysmxueclatsccattiefardkopavinelasingohirianlaowinalloercons",
//				"hoastshaonletettalnaigrjshaatewariyarshinzaabuirkdortshmaalechoveoasnsrytassiejanlinrtaagkheiosatndxrteubirsolnagaairmarssamattasayhseeneteaeiolawndryshmaakeaftsiutishanjteelnlidabroosesaihkeparsisisttaicadynehborounseatgsiwandhnryoucottaxhenchmanlianablastrsovhiszkxgooiyunoresshootvcamaneitalapjmneathchaoladeheapsrapsralntigresparchomrbpawjathxtcksizrwtapismmsidaletijogettaabapnaiasaailsnstshlisasayothin",
//				"seenrncueypaxwaxupenwhidtskedpasiwebigcoyzslodnireearnsduepaecevansiygmerbonewdkepwtidnshrspoetsatefaradooiinptshulnadumonedudasikecgaayerpswoepsatemensdueranqxtedrwickydrewpepoenomengatkosratlineowndabneeeetacoemedeipkxhnoabyestmendeerrefcwyesduepenelopegxannvcwtzrfheeoedyeaeinseerveeduewwaspstddtendsesuepleacentoistgasperwhipitliesedmotkaduasfaeroezmaxstwoddtweepaandeherdblosttbonnuenpteianspotsikeexcoy",
//				"hbosestybumperueyedueaserhiodinrsetcsstaugratatsocjowshryompttbtpareusetecslareideeesduraohiajrruecfowpataseamerayatoryessorydureittsppukeirrajernecetlhryoopuzeraaweetiueekrmdeacutejaspriahopvaslasesducttoctepttbejadeiiealancucheetaatjewoftruthrydeaudatmoseolattergofabbataujokeshbbettorydreurerverekudhthybmacptkuvssrvsoredeeasievasthbretvineyuhfutosagenervesrrrecomputebakedcatrvecaulyoterdeulhearreitjetau",
//				"ofwhaharfleeenoertsimengtoewcepsnmrrnjaolysedsandoruxhoarweelaflekstljirarecongestmtncisedoeortsototuxesdontketsereauariehwarcosterajeelbaesnortsptoircoarwdeivetosxmtaelgabnestooidirkjeteoodezencoemhcoralworteadfirssteeeecoprlootirkslraxwtanlloarstoisedewjeucklamtydtefforslrubthoffallwarsnmostszgatbicolorfeeoeabizenszjustsamenetzenlofaidzepgrootoldeoyipeszmeaatmoweeoagfebacredsztroicrwattstocototsterreaol",
//				"iaafhfibattlvoevboisdcoliidaeoerodewoointerrexuolsemefigbatlriaklsrstolehwypsoldiodaoesrclioneireastmelxenosstarcbrumgblyfagbeersdeiolltphoroneoreaslepigballsjvisxedsidtlhpotionsselesolidnilleropsydfeewurasealiealexisodvcpnewtsialesxreheatiotrigbiossiretaovmpskidieltcaanextempsfiaahkragbioderorelgapseiribatrslipseyoreomroihdcooseroriagleedelbeiterlyselevredyggidsatleulacphewrereawnspbauoiisepionirsdeevint",
//				"arnosoeartniwgtwalassaibcaantlfoisthipeetvolureidesheoananniyerkiaomtpdtshitegbaalstitsoadalescottezheirueimantoaalehnadionnattomatepiitislogesloftedttanandisqwceresmeatbsignaleesudsapicaeadinliteisottheynettieurdsramlawatefhteatdtaryssenzegtyanaalesalunnpwhtakescvdzarretrtshimoarrskynnaagstolonbntistayaarnleieisniionpholassailmnliyarndunafbataztydievdfwonsinncsennitebraisthluonthestanelcamttalecomatsweet",
//				"usisesehsaltdbodhnakbaitouginsatibetisepaetrystilooefsuchiltresxtatnasmeetawgbtganblinktaluspootoloveftsypinalltahrtechmasichnotngeesttarestbpontalomewuchiltkqdogsfbnegaterblanpokymoastogpultoriwgabsnottrioelteysmosansgdawpataoulmeasroefivebaruchangkarylisdewaxeboelvasspesaoernsussexrichabbotntotclrknuruhslroterkoaitosetnaebaisnoriruscmyogathouvorlagemadtobaccoboiltottsarentrytoltpkwhitnoanowunpotngeodepa",
//				"batarapoawheyssyorkcovenlbetttaieogeespawqistseejaumtaboothespafediawsugreloasnekronetcivjbtaulisnaimtestaeadhnivosemooulatootsiaegpseewartisaurianaugobootjecxylastoapewnrashkraactuudseleabjesseoaloatseestagneptauuskateyvoaaewabnugdssurttipswsbookracksthtsymodfpolqjivaaagswumaaabaarfstooksosirisnonactbsboahsaepacsleissmirkrovetassesbaoutseanosbissjlaquayisolooloathesenavartestisneacooterlkasobraliaeguypaw",
//				"olatiteglensdyadgatauaixbonacneliudsirerealheepisteiqtongansreloseleermdisftryxntausicalasonreblastkiqseerieenslaghpingmftangcalenderssevinlyrealestmdtongassawdbrequeenexivyntartaemeersbnrossahitrfutcaspratdsseelmeetenndatresetosmdeereiqakeyerongtarathenarditeoeubaskallrdeeeivetolliorangtyualalaxnsvacoroglnhtsevaafilarilatiuaineahirolnmeanexgaokarsframedlaufnnbutansapxlavicshelassratgapabteatoarblendedere",
//				"oratitsgramelenlgoredeaveokaynapadehalsraspzactallnebtongamessrsespialueihodwevkrodsayepelonrnepnslxebecaraismspegztenguotangynpikesleeatinpernopasluedongaleeflewcbdiskavitemrorleaunsleekroleszadwodtynhtsalesesaruncrinkledrahalosuescsnibaxseasongrowerzamalledsssdeslxerrrecanetitorrissangrednpopsvnsteyosogrmzlestesoapsleporideaniszasornuaskavgnoxnslowsualpsdonnedlamentvretiyhzapsshredgatoerindoorepikenlsra",
//				"ernntnearleakalkaaylloocresniueboltsobegljbsudtotieexnedaneaverzambalbottsappacsyalroilboteugerblrihexadugoamerboastedaoanndailbastebaalwtubageaberiotpedantalfkrpdxlaeslctwaeyagiluoembarsgetaesoppalnilstvnitraeuroedyauskopgeslierotmdvetxnhealvedayaplysuenbkepmzelrjthorrgtdleewanerrtzvndayallbabecdrwlievearesiaewleaobebebaytloouaesoverdouesecalehlvtapjoekbeladdrlinealtcrowtissubersglpantaryalpeagrbastekegl",
//				"casamasaaokbateaaewettynactsvpenytoryishognueerysildzaclaskbasaqbrnooilomreertntwetfyventscphlanefirdzbeehyorkfntaurdlaleaslavenotosibboompnthlenefiloeclassbejaareztostonmotkwehieellribathcsbtuyeretaverrasiofbseallewoptateheroicflorealmzsrstoaclawerewueksiaderqstagsrtaahoeoldooacaamqaslawttenentnlfoevcacaakuibsoeteyntidnewmttypotuyacallettenaecreasergleanttellatiskbernatomvruentfrheeasreawoeecehanotolasho",
//				"pakavardahdaangadlabelmotptkjoremewcmorahseinesmlabryapedkdairafaxenhoowvcmianotalesmjbellpoabtegsauryaenamnxdseldisredomakedjgentwroaahavoenablersaowipedklabiataeyenrthovandalaabnobxoattaplasimiameajgcsikawsarnaobeanotaliarchapsowxeibvykurnhipedalabaindkoarixfretslulaaawehbranapaavfikedanegelesoesabjpipdadiaarabsmmesorelavelmonsimipaeonstrodgpugilmasoraesemeeteakdagsoalavjcinesscabidksltangiplatentwbarah",
//				"argzezlernettdstewruinsofaegymnasicasalinmaanipsidsexzagegetslrathaanaoceankjdoerwissyuaniamisfassdeextinisahesaneapegeonzggeysaaeclattnbemadiswansdockagegituvtfjixialenoebderwidunoshatfeiaitnaskjnizysapsgdcstlnrosirametnkinandasochissexgeldnsagerwjuranegatekhalifmienrricinsebazarreasggerdisawanogsbuyasaereadtlbunnsanaeawreinsmanassargonnenoesaessinjmontaninggfidgetspornbeyaanansaiukegpwfraskawifaaecstlin",
//				"atabebastendkyiksoperoaseanagnucaredaratewclarvalcoinbassandfatidiceerheederxysnportagecolantoecitcbindrataeintcoslvisshebassgicenearddemencytoocutcherassaldeqkexrnreanesemynpotceahoirdentaldalarxerbgidvfacetdaathorpennkortudecatheirfoenabayefasspoxeplanarkiriiarewlbotttereoimebatteifasspyricocasstmegafastnlcdameaeacaricoperoanealafatshaanussiabiflexwhukcaressercandivstomegdlacatdtersavoepeiraoteceneokate",
//				"aslpopessjotsbesselanaemiaelsxeyenzoeaeljayveefeniceapaislotgesfttytjatzoourdbmelenpesayanaxlciyepikeateelettopyasvfeistuplisseytezeattjroxyblceyepitzraislntawsideanteejmorboleliaetctatielanthverdunpseofglizpteestceltxesarleojiaptztegcoalkebjgaisledalveolasertfeniankasslzejcertpassofglislbneyeyhmiprasagassoviterahueyhaeyelonaexthvegasiteheemseakegnudatesyhnuiiinilotefmsarosoveyhpolarslfeilteraeliytezcselj",
//				"ofaemenrfbveensersustraotoharctiateearnebzipsasakrledeogravewnfoeyimbruementenohustgarsirkoceltisgreedeaseamyvgirrpsegruneagrrsimhenreebamcinelsitgruetograkesjeteadtmnhbomanvuserssulyretheokespatenterseswaregensfulaumchertetebrogueyawlmdaennbwogrusesupsvareetyonttzkerffeeableameoffmowagruntsisisoggasroworfvprenassnaisreisumtracmspawofgusshtorsoeswknezuteistnggttravessoframrepsisgeestrasstumstosetimheleneb",
//				"ndueteradastgeagaetollbawnaucaorblxvbsrtahrnordbelyskensausterdetarfassxtvoemeaatelpbcorlenatywraplasktrotbfasprlandssasoeusacarfaxrsttartaretyeroplsxensauetojgwmrklfraaatrestetloosyastwatnetinbemolecavdeulxptrodsyrtfaagletovalnpsxareytkuareaensatemotnosusgseaerlwhealddtxraysrfenddteeusatelareriasprocnenadsnltrroiobrissrettllbafinbendssoiaoaaanaaeeomhsogrilosswllustadadlrtcvnoripvtoeaudewtfaenetwrfaxygrta",
//				"rgbakaemgnepdtodmlaobuyherabbessyboxyaerneseracynierfarombepaegipvsdnanokxwarthaalbeybosunrereesoeijrfparrydveesumecromnwabombosdaoeappnskestrelsseinoarombnponderafbdeanhkstealriornevapearrnpseyarwbaboxcabioepergneaadeaduarsxnirenovaaekfbjetnaromalroaerebadraviebeenjuggroanersdarggkiabomatboslsshoesobrarmgeeipesoswyssarslakbuyedseyargonrsashmorjoanwrensdssbwooebibepochguskbxerssexroambcleadoarlresdaoedern",
//				"denataboeyalllwlosevomhssdenehlahorehebayfatpikhagsrsadconalebezltatyeartenialseesorhevamadhassawrgurslipahttaramotkrcoanancoewaterbellyithalassalrgaridconalvxlsaisotbeystilaesagvpastelseadalithianoaewekengrrlbpeasiethelmialeygdrartiestsnublyedcoesavetpanelritzbosfaumeeariysritadeetzencoelowasaiscrivededoeatglbivinhaierasetomhhtithedecapielsowduweanafallaionccsognalwksemiteetpaireavionkssetwidsasaterslbay",
//				"prygigasrsdronaosakareleapcydsmnlrholeatsfnatollopelxgpesydrearvrrnesethiojabneckarildaneopsteanaipulxrottlerdinesallestjgyesdanechaerrsdisnnteanmipthapesyorazoaboxreacseidndkatpattereractporyalabjrgdaoleyphiratrteokescoeatmosppithroeeixyuansepeskabakatdyeolarvarafouerrthoseldegprriveyesknrananyeeidadpepsrdapradayjlnyelnakirelseyaleprettycmesapuaeojbftmonyrjeearpydraleredidoatnyiotaasylaakeaapatanecheoats",
//				"ologvgirlsofahaaresepicopomopabacpsicoirsqaaluncurlingoaroofsilzfoalsoysviedrhomseptcpeaiuoarlpaatrainfulrclootairaniaryegoarpaalmsioffskvaahrleabtrysdoaroufewaprunplimsovkhoserrelyloofpmrouftacdrepgpainsorstfillyluslamaidrbisrotysouslvnoaihssoarseresaloooaidozippquaillrsusliklgollvzsoarshpaaeatoatkeposorloarfiketecatoiaesvpicaltacsolayltmboraoaasuerqybaatpeaapproofanolikvpialattiredronepsladoerpalmslairs",
//				"tmrihitumefwaetaunaescagrtervsliassiaetbezilileaoaekoitourfwetmewjipeershipdtegeanseaveicotsberiteaakowlibapjfeiculekourpirouvtipestewweyhsiebenilearsdtourowexartlospteeghyefanbaeirejewrebtownladtpsivtieerasewtimrelapseacdblieatersjleehorateeetouantealifreakdjetsrzoacmmbsleekypitmmheerouaestiningoeyevtetumflawtyenpainekinahscaspnlaetmorinelguttateoptzrlainspoorsarfwtegmcyhviliineibedurenraptdtnbripeseatbe",
//				"dorehencouleesnecbatellttdarygfaleaolknpuzaxcwalertseedscrleinovemarukuahorsestaabetlytaledgpttantreseewcplrmltalcxasscurerscynaraankeeuehgasptbaftruasdscreetjeteweernautheslabprtcutmketapdeerxlsereeynoairratencoutwargaelspfourdtuamwitherensuidscabetaxclrkessmvnetzeeloopawutseredoohvirscasenabartstetydidcolxrenetrrlarksabahellgrrxlidosucraftcndenierezufearerssterrlenatolehyoxcartoptscrabtarnsdbptaraatenpu",
//				"rrmurutbryusareabtaneinplromdklsnewlnetoyjsiegvnanppaurabmusetrusastyeswrlscarpoateendnsiarkoplseenfpasgeontauesibivpabssumabdestowtessyorksroptslenswcrabmasnzalagaettoyproruatonnespaesloorashincaseudelvemnwesterspgatkoaicollynreswagepramftryerabatanaieumeapcauteljafirrowgyppoturrrruemabareestshpaeondrerbruinstonhsnshepstareinkthinerraseholpberfeeasajslashesaalenmusevpriordliesheloncbmvtlatecrtolstowpatoy",
//				"asulelifsittrierfdmetersbatuiserrtwkraioixrananrgmveclanfutthisstoryiatwekapristmdtarieregasovbreamsectanoryotarefanenftalunfierytwiattiresriovdreamtwpanfugtezrbractyitiseritmdomentvoatbtoagtearpratlieknhumwatinstvamystrepoekimaatwoahvecusiiihanfmdremantuarepositbxgsessowaiverylasseshunfmiterdresnareiahafstamtireearreaerdmetersyearhasntnetesfeasehgarxterretannbtmuttenssereikanreakoepfundbmyepadobrytwvrioi",
//				"enulaliknearcrackopasietoesuleamestxegiseembiodeastifleskuardinwrymeegotaxvtortsposselamiaeestomassjifroiseeyasmikbdiskovlusklamestigrrehaemrstomassotteskuaraqcooofseisetahrapossaiotygrossearlbetovsllaxddustsriinotopeescitsaxesesotyodtafujiredeskpooapbiaugcitywisoeajinnstoetihelennawduskprsamomltsshaledeknabsrihalvemlgimopasieeelbedensoilsatkaejadavoeoacmlsvssossuaradtnihalxbimlsxsatkudoopeateosomesttcise",
//				"alebabsslentaidasicatieluateonemetekefssegmhivierptexbarsentyslwtomoefleakinmiltcitseoamiranstumdsprextviseoonsmishierslibersodmotesfttelanmistimesplenarsertajaumvxtostelalincispailtoftutsartshenmitbodkiyepestsilltvcontainsekepasleovytaxersieyarscimachinefaenowstugrrillseveteloballawyerscitdmimslrslaoayaslnhptslasiemsfemicatienosheyalrlistelsdardyrimgleamstirrutpentdillilaokhimssksanseiiucodnaisumotetasse",
//				"lerpaptoeausnaanosdeutomalarmianouknoetnaznjawyoaferrploorusbtexsqngaeskantshamadsuiomentalineanaifcrrswanogquintojyroostproomangaktessarainanesnaifsksloorasennahwrugtaamaraudsnfeaseqesaanlastjoshtupmanybrfkistaesewdgiantsnanafliskqwbearrctaabloodshedjaurenrsqxtuazacteenkwaerrgpleeaxbroodauansntmoiremlbloeujfstrettonternsdautoigtjobleosataamoalcabathzsanntutooaufrusaymetramnjantinnesorysadgaslsnangakentna",
//				"eoarcropombowlpwprefertilesaretategutcotmvastxsterohtrespaboyookozaymcagcurielisereetrfareeetolapernhtoxtttyzbearpsshsparrasprpaysgocoomacealtorateragiespaeofjwlexteyosmicalbertrftaozcolsteeodstiererrpusyargeootoaoxeyeswrittumreeagzxyoctanolmyesperefestbacwhizkoelvenrootgxmohayreoockyaspeleparadiseafreyepobsrooafdrtadcharecerteydstyeosatdstippenpyerevatwadersslerabopsioracrustadeutfipasrleypiertlaysgowotm",
//				"arfdodeirsuntartiemenkoniadfebaronhtotessarstypouthowdalifunkerxnqrestchotegsandmenioeerkuabshirrittownytsoequirkispolicedflierredhetnnsvobrasheraitchgalifunejtisywneedsnovaumestetchqtnidsaunasogsendertpkfthinetrchymebdtkgsatstaichqykhowfteaskalimesemstufttogqxeniautkrrshyshovedarroxkflimanrreranliveeakairustneveaeoratoremonkobeasokarlctadaniratrkuesacatranellintfunrpnrkvoetstraitsegifpeimergaesiredhhtess",
//				"lourfrsroomyoiaoretiadepalhutgvneatnessdowncorrearddlrlorumyesojyeneosttfntzkiphteaeetindalgddanaeredlyrodeeemendrcrdorttruortanehtssyyoafgniddenverttzloruayiboakrlaeshopfaimtedriotdesyahdlaytcezktartanreurteysootdrteghodzdvnorletteredfluesioelortekitcomusodzejsaawaedoodtroddaerloofjeuortiaanentpoeaitlelromcrysaittentsdnetfadegetceelootothvpraleaeatkwtvontatooaarumyarpodaftncontendizrureateazledanehtdosdo",
//				"ylncmcbalgoledseaswtoareyynnaiktroyerebagvtorturnnriacypanoleblzlstageoymenhadenwsoerattanyiarytsenjialtrarasoetaaouipaoncnpaastanybellgsmitdarstkenoyhypannltfeyataoabngemsdowsantrorselynaynleorhanocaseuennyelbrlortwaineahakegnyeoystermanjbdgeypawsatworoneeihszboyvnjallaytgrisacyllmzenpawdoststeepestayeyaloonlbstenrteeitswmoariaeoreylporenkeasyjsennavoketeonppyonnolsuelasmaeorteeeathanusywashysaytanyrebag",
//				"aptklkyipcenedheiteragrosamtevraraobreyocwaessurelshikaritentypentatceoolbxfzdometarrerageavossahrlthinssortteragieuhrioxktriehatmoyenncalvadostarrloofaritenrjeszsiatymcoladeetolrsostensmoaenserfzxakehbuttlornyspossetvmegforbclarootstslittydctarietzreeseteehfteyaswetgppooscshatkapplettriedahatasorrareataipeelnyarsxrasehatelagrvtsertaprossmroihathtexzworeasaxrrsaltenhuopgalebesasrborfitutsethfatosatmoseyoc",
//				"tettatiresapawlarihonklegtntbunolnsylviasjofsewlcentsttartapiiespzoesvisayaomwenhinslbookctuangolseatspesalezasokrfwtariattarbloensivppssauowanionseisotartcpoxagmesneinseaswahiaeosinzvpgnatcpdflomantblywitesspiseineheunakoanysetsiszeinastaiwsitarhimohfsatvatozsingjcakeeasesntsetteeasitarhwnloiodeassobtitreafepisodalodvtoihankluedfliteaisdnnerltalicamjinaodnaaagnetaplweeksabyfsodsyaoortwighelotiagoensnaias",
//				"nohsssrpomadegdeptelawssdnohstresaaeserrmqebonxsuealksniphadaroedteemecaseryfgsoetadsslewuntradeddeilkdnorsetadewpbxlipcrshipsdeeoareddmostegraterdecayniphudlzedfnkaeromssogaetrelocateddornudlbsyfrassdexaheaddroocaneetoewyrremendcatnaaskhirgmanipetfleboaheelyteradquiwooranmaloesnooseahipegadetelsidolsnanpoabedrollrseleletesawstelbsanoicolorspdnidaurfqcreelariidaehaddxsowosseboelderlyphxtdeedyntrdeeoaaerrm",
//				"ayepipwdynredeoddvsacetteazernattcosthwenethreatstlakpandererwyjewtanhboisorletzsvcitratesaneletoitfakeeretawritedhaandbopendrotazowheenminteelvtaitborandeseaudelekcawzntimersvetarblwheezeaseghtrlocprosaretoiewryblesanzdereasntaibowerlikefwenrandsvlashrrehdarwjwceesfeyyeoenlamapayyijrendsecotvtgtnimararadyrhtewmagottghatvsicetnaghtraynbrgzatdoaforsolebadtgconnectereoatyemirshrtgiseardeavesaoraveetazoldwen",
//				"itepapottinghiehtalariasainercamarsyazooiimbuewaderofpintengrotxgimeizssayatvisnlarkaramidicoramekedofgeuoaeinkmitbwontsapentremensozggitacmioramakesstintedgajhavefreonisatinlaoeausrizganoidgebatvarpreywreeskgoutsrelecnhitoayieikssierrafedoiirintlavalbunezhotixoraiddittoseirotepittaxrentliremamesnktaririttnbegotaeaamezomalariaceebaritnsuenasteiderdavisahmerannareengewstitarybumekyoattewaaleetiaoamensrhooi",
//				"nemasassetviramrslectalbtnpmokoelteflysutoeftralweathanesmviisemisertyresfguxabpeltsloceawnkuatemseothirtulrsvseasfatesrgamesomerpesyiitsskeaualeosereunesmwiczrtxrhtrsptbssaveluectrasyitpunwidfluxgtaomfaimeesisterarerkprauuoftensresriashmosatineselxceftvmyrtusmsttowoaeeuertatsraneesmimeseatmeledbessconinsevfeisscdgledytelestalkrdflineertdpobsmnomiwgxororedtgeettemvimabeassofftedsfucusmaltermunluterpearsut",
//				"ngsoaowigrielailieramoseanispkzrsmneshworyrtfebsnuvirontisieawgjeersrhinaeatcaeiremnsparonnkovarinudireefosseinroitbitiiaostipirsinwheerpakraoverznuintntisneaxlacermswireapaireouafiveheaionnertstcamopiebasunnewfgiverskilotozerunnineeavarsdwarantirecartfishlitejwmayndoggonervipsonggajastiramirerretnpapnanigituewparasrrhireramosksrtsangtifrizeiindianacyizlrrmattamusieibegopapetfrrneoatisbearsitneoarsinvlwor",
//				"onukekidnapsvievdemetasciosuingrstarslisahrealssotblykoadupsdinwserialoaerstoicsmetasieraoonsbireatxlyslassieparadesladoskuadierisailssazenrisbergatoatoaduosefviolytiisacezipmesteaobelsissoosuestostkiersdutaasianoblminsvatsgratoaoaeldbeyuxiiadoadmeoemeapulvltewitihoxannsalablzikonnewduadmitererucaazeiododnpetsizeussrullremetasniuesdonaoausgcdeoxedosohogvrutsaaittupsescnazeirearuarsetduseimietoesirisabvisa",
//				"sautntneaocaindiepantyproshuesxmptsbpsnoonmakevpentertsleucaenajatmiosisnbaftnrhaptapenmyessotomdangeraekopitcamyeaveleiatuleedmihsnsaaownsmnotpmxanisfsleueanriotertinhornwncaponnkittsaohoseaeapftattedbveunsaankaiteaishiyfoxbonsaisteetnrugnnoesleaptnaakcusieftjntonegyaaoseotewitsaanjeuleantdmpmerlawneseseacananwneapmesempantypsieapesalikehxredsgdeeatniximetallotnucadvraywnebakmeabonfeuvpoaidfspoomihstinoo",
//				"itewrwpttmanaocatersntlaaifeqirnlntulepsmynaiavlodhaiwisteanpptinknomebtruesgoafrenalqsntoiishancadrainaislokaanttavastbewestqcnoftpennmerinoshenradbtsisteonsjaagainopfmareoaresdsibhkenafsiontalsgenwqcuvpedtanpitbharoifatssrumdiabtkaphrierpompistregsraiaeeaaskipnayortttstamhaeowittripestroncnentasaesqipittaadnpestelnteanerrntliotalpitsbitfratcircpoegybrantnessandeancvatterquaintaussstevearocsiesanofthapsm",
//				"hoistssgoaleefdegviaurotrheipamaousaokstajaunsnowsidyshogileasoneeanakastabsefteivueopaarwhatiradesqdyesntoneleargundogabsiogpdanesskeeactaaftivamesasshogiweaxeresyunseattcflivtsanaiekerethwetuosebuspdanaisseesnoaisinaeerstmaasheasesaityiqsfaahogiveaiunlikedsensurjwqrootssaidcnshootnaiogifudavattoecaphahgolusescatboatkdavituroantuoahooantemtgdhqdawbejameatuboorusiledntorctpaunateatasginvrindshvtranesiesta",
//				"atcknkrstweavbnvslenostunagcoeditormttrowfixpeathoarskaisceatrtearidwternmttybugelodtonishaeoanindoorsaepotdredissxarisetkcisonidgrrtaaweneiboaliddoertaischanjvnyesodrgwunebeeloonpeartangoahaexttytokonmatcordarpteaeedegvstodmwoaderretanscorbwtaiselynexpectvrtreronfhosttorewaredkattnetcisebonilieuidenoatastexoarenettietrilenostedexttatiepegdusnaonthtyfedvieotiinooceanautsenomxpiedmontscalnedntalonidgravrow"
//			};
//		System.out.println(Ciphers.cipher[1].solution);
//		System.out.println(ZKDecrypto.calcscore(Ciphers.cipher[1].solution.toUpperCase()));
//		for (String pt : pts) {
//			int hits = 0;
//			for (int i=0; i<pt.length(); i++) {
//				if (Ciphers.cipher[1].solution.charAt(i) == pt.charAt(i)) hits++;
//			}
//			float ratio = ((float)hits)/408;
//			
//			System.out.println(ratio + "	"+ ZKDecrypto.calcscore(pt.toUpperCase()));
//		}
//		System.out.println(z408Homophones());
//		dumpZ408Sequences(Z408);
//		System.out.println(Arrays.toString(toNumeric("9 %P/Z /UB%kOR =pX=BW V+eGYF6 9H P@ K! qYeM JY^ UI k7 qTtN QYD 5)S( /9#BPOR AU%f RlqE k^ LMZ Jdr\\pFH VWe8Y@+ qGD 9K I)6 qX85 zS(RNtIYE lO8qGB TQ S#B Ld /P#B @XqEHMU^R RkcZK qp I)W q!85 LMr9#BPDR +j=6\\N(eE UH kF ZcpO VWI5+t L)l^ R6HI9DR _TYr \\de/@ XJQ AP5M 8 RUt% L)N VEKH =GrI !J k5 98 LMlN A)Z( P zUp k A9#B VW \\+VTtO P^ =SrlfUe6 7Dz G%% IMN k )ScE /9%%Zf AP#B VpeXqW q_ F#8c+@ 9 A9B% OT5 RUc+ _dY q_ ^SqW VZeGYKE _TY A9%# Lt_ H! FBX9 zXAD d\\ 7L!= q_ ed##6e5POR XQ F%GcZ@ JTt q_ 8JI+rBPQW 6VEXr9WI6qEHM)=UIk", false)));
//		System.out.println(fromNumeric("14 37 4 78 3 35 77 26 47 6 15 11 1 9 77 37 14 99 17 33 7 47 18 26 20 53 17 33 15 2 11 53 9 16 9 7 29 99 24 88 47 47 21 96 21 77 15 26 79 16 77 78 98 7 92 24 7 6 77 10 53 26 4 9 6 99 5 77 9 55 15 35 99 26 29 4 3 1 65 12 7 22 24 99 18 29 22 96 26 35 99 17 33 78 22 20 36 96 7 26 11 99 17 33 47 79 26 18 77 99 32 77 35 78 29 1 3 24 55 4 8 65 7 21 24 16 7 24 6 17 5 8 33 25 53 9 85 15 99 25 12", false));
//		System.out.println(fromDelimited("14,37,4,78,3 35,77 26,47,6,15 11,1,9,77,37 14,99,17,33,7,47,18,26 20,53,17,33,15,2 11,53,9,16 9,7,29,99,24,88,47 47,21 96,21,77,15,26 79,16,77,78 98,7,92 24,7,6,77 10,53,26,4,9 6,99,5,77 9,55,15,35 99,26 29,4,3 1,65 12,7,22 24,99,18 29,22,96 26,35,99,17,33 78,22,20,36 96,7 26,11,99,17,33,47,79 26,18,77,99,32 77,35 78 29,1,3 24,55,4,8,65 7,21 24,16,7,24 6,17,5 8,33,25 53,9,85 15,99,25,12​", ',', ' '));
//		System.out.println(Arrays.toString(toNumeric("THIS IS A TEST", false)));
//		dumpZ408Sequences("9%P/Z/UB%kOR=pX=BWV+eGYF69HP@K!qYeMJY^UIk7qTtNQYD5)S(/9#BPORAU%fRlqEk^LMZJdr\\pFHVWe8Y@+qGD9KI)6qX85zS(Ld/P#B@XqEHMU^RRkcZKqpI)Wq!85LMr9#BPDR+j=6\\N(eEUHkFZcpOVWI5+tL)l^R6HI9DR_TYr\\de/@XJQAP5M8RUt%L)NVEKH=GrI!Jk598LMlNA)Z(PzUpkA9#BVW\\+VTtOP^=SrlfUe67DzG%%IMNk)ScE/9%%ZfAP#BVpeXqWq_F#8c+@9A9BH!FBX9zXADd\\7L!=q_ed##6e5PORXQF%GcZ@JTtq_8JI+rBPQW6");
//		System.out.println(decoderMapFor("JQBCYYWQJKICENDXRPBGDMBFU", "CANILLBACKHIMFORTENPOUNDS"));
		System.out.println(fromNumeric("01 02 03 04 05 06 07 08 09 10 11 12 13 14 15 07 16 17 18 19 20 21 22 02 07 10 08 23 17 02 07 12 26 16 24 25 11 14 13 03 27 19 18 15 28 14 29 09 02 30 31 31 19 01 32 13 33 34 35 04 20 25 07 36 37 02 12 17 28 38 03 21 05 08 16 39 15 13 14 15 07 16 17 12 02 10 40 36 13 33 03 05 35 15 41 07 19 42 11 15 43 16 11 46 21 02 30 44 13 45 24 09 08 45 46 47 48 22 13 09 10 26 04 25 26 50 51 08 52 03 07 40 13 39 03 31 02 52 24 17 31 31 19 47 02 51 21 10 45 08 24 03 43 13 01 46 26 07 39 08 03 27 13 10 21 04 10 40 15 02 16 54 17 12 13 53 03 07 39 08 24 30 01 19 44 46 08 03 02 35 10 07 13 30 16 04 54 19 14 46 55 15 13 47 08 41 48 03 27 04 39 10 14 48 28 19 54 20 50 51 13 23 10 09 33 16 27", false));
//		System.out.println(toNumericWithPadding(Ciphers.Z408, false));
//		maskZ340();
//		nicksConstrainedCiphers();
		String s1 = "APPEINMONEOFDANTESFAITHFULANDATARCHERYFRIOONESANDWASSENOTSOASKEDTHEAPPENASCASEGASCARIDMURNMMERTHEWOMANFRONHERSEATUREHENTAIRSMINDWHATYOUAREPAYINGCALEMOUSSOMADENOREOLYTOTHOSEWORDSTHOUGHELIVERTLYIRMITATEDANDANNOVETBYTREINTORMUCHIUNBUTADDRESSINGTHEABBESAIDCARRAMANREFAITHFULTOAROTHERWTOSEWIFEHESOLOTSANDDESIRESFORHIMSELFBUTDAN";
		String s2 = "ASBEINGONEOFDANTESFAITHFULANDATTACHEDYFRIOENDSANDWASHENOTSOASKEDTHEABBEGASPARDGASPARIDMURMUREDTHEWOMANFROMHERSEATONTHESTAIRSMINDWHATYOUARESAYINGCADEROUSSEMADENOREPLYTOTHESEWORDSTHOUGHEVIDENTLYIRRITATEDANDANNOYEDBYTHEINTERRUPTIONBUTADDRESSINGTHEABBESAIDCANNAMANBEFAITHFULTOANOTHERWHOSEWIFEHECOVETSANDDESIRESFORHIMSELFBUTDAN";
		int wrong = 0;
		for (int i=0; i<s1.length(); i++)
			if (s1.charAt(i) != s2.charAt(i)) wrong++;
		System.out.println(wrong);
			
	}
	

}
