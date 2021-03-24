package com.zodiackillerciphers.old;

import java.util.ArrayList;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.io.FileUtil;

/** Search for repeated fragments.  A fragment is a snippet of cipher text, that repeats in at least its first or last symbol, and possibly
 * symbols in between.
 * 
 * maxWildcards is the max wildcard length
 */
public class Fragments {
	/** consider only the top SAMPLE_SIZE results when computing scores */
	public static int SAMPLE_SIZE = 20;
	
	/*
	TODO: use max heap to maintain only SAMPLE_SIZE results for efficiency. 
	 
	TODO: remove redundant substrings.   For example, d?8#???M?)e?????r and d?8#???M?)e are at the top 
	of the results, both with very low probabilities.  But the latter is a substring of the former,
	and should not be overcounted. 
	*/
	public static FragmentScore search(String ciphertext, String category, int maxLength, boolean scoreOnly) {
		
		FragmentInfo.countsMap = HomophonesProblem.countsMapFor(ciphertext);
		FragmentInfo.L = ciphertext.length();
		
		List<FragmentInfo> results = new ArrayList<FragmentInfo>(); 
		
		Map<String, FragmentInfo> map = new HashMap<String, FragmentInfo>(); // track fragment info for each fragment template
		for (int n=2; n<=maxLength; n++) {
			//System.out.println("Searching for fragments of length " + n);
			
			for (int i=0; i<=ciphertext.length()-n; i++) {
				String sub1 = ciphertext.substring(i, i+n);
				for (int j=i+n; j<=ciphertext.length()-n; j++) {
					String sub2 = ciphertext.substring(j,j+n);
					if (sub2.charAt(0) != sub1.charAt(0)) continue;
					if (sub2.charAt(sub2.length()-1) != sub1.charAt(sub1.length()-1)) continue;
					
					//System.out.println(sub1 + ","+sub2 + ","+i+","+j);
					
					// we match at the first and last symbols.  derive the fragment template from the two substrings.
					String frag = fragmentFrom(sub1, sub2);
					
					// skip ahead if we already counted this fragment before.
					if (map.get(frag) != null) {
						if (map.get(frag).positions.contains(j)) continue;
					}
					
					// otherwise, create a new instance of FragmentInfo, or update the existing one
					FragmentInfo fragInfo = map.get(frag);
					if (fragInfo == null) {
						fragInfo = new FragmentInfo();
						fragInfo.fragment = frag;
					}
					if (!fragInfo.positions.contains(i)) {
						fragInfo.positions.add(i);
						fragInfo.fragments.add(sub1);
					}
					if (!fragInfo.positions.contains(j)) {
						fragInfo.positions.add(j);
						fragInfo.fragments.add(sub2);
					}
					fragInfo.category = category;
					map.put(frag, fragInfo);
					
					//if (fragInfo.fragment.equals("#B")) System.out.println("SMEG " + n + " " + i + " " + j + " " + fragInfo);
					
				}
			}
		}

		for (String key : map.keySet()) { // build a smaller list with just the best fragments
			FragmentInfo f1 = map.get(key);
			
			if (results.size() == SAMPLE_SIZE) {
				double max = Double.MIN_VALUE;
				int maxK = -1;
				for (int k=0; k<results.size(); k++) {
					double p = results.get(k).probability();  
					if (p > max) {
						max = p;
						maxK = k;
					}
				}
				if (f1.probability() < max) {
					results.remove(maxK);
					addUnique(results, f1);
				}
				
				
			} else {
				addUnique(results, f1);
			}
			
		}
		
		
		return dump(results, scoreOnly);
	}
	
	static void addUnique(List<FragmentInfo> results, FragmentInfo f1) {
		if (results == null) return;
		for (int k=results.size()-1; k>=0; k--) {
			FragmentInfo f2 = results.get(k);
			int redundant = redundant(f1, f2);
			if (redundant == 1) { // f1 is already covered by f2
				return;
			} else if (redundant == 2) {
				//System.out.println("removing k " + k + " " + f2 + " because redundant with " + f1);
				FragmentInfo fi = results.remove(k);
				//System.out.println("removed " + fi);
			}
		}
		//System.out.println("Added " + f1);
		results.add(f1);
	}
	
	/**
	 * Two fragments, f1 and f2, are redundant if:
	 * 1) One of them is a substring of the other
	 * 2) The number of repetitions for each fragment is the same
	 * 3) The span of one occurrence of f1 (f2) is completely covered by the span of an occurrence of f2 (f1) 
	 * 
	 * returns 0 if they aren't redundant, 1 if f1 is substring of f2, 2 if f2 is substring of f1 */
	static int redundant(FragmentInfo f1, FragmentInfo f2) {
		boolean sub1 = f2.fragment.indexOf(f1.fragment) > -1;
		boolean sub2 = f1.fragment.indexOf(f2.fragment) > -1;
		
		if (!sub1 && !sub2) return 0;
		
		boolean reps = f1.repeats() == f2.repeats();
		if (!reps) return 0;

		//System.out.println(sub1 + ", " + sub2 + ", " + f1.fragment + " is redundant with " + f2.fragment);
		if (sub1) return 1;
		return 2;
	}
	
	/** dump map in increasing order of wildcard length */
	static FragmentScore dump(List<FragmentInfo> results, boolean scoreOnly) {
		
		FragmentScore score = new FragmentScore();
		Collections.sort(results);
		
		score.min = Double.MAX_VALUE;
		score.sum = 0;
		int num = 0;
		for (FragmentInfo info : results) {
			double prob = info.probability();
			if (!scoreOnly) System.out.println("Result: " + info.toString());
			if (num == 0) score.min = prob;
			score.sum += prob;
			num++;
		}
		score.mean = score.sum/SAMPLE_SIZE;
		if (!scoreOnly) System.out.println(score.toString());
		return score;
	}
	
	static void wikiStart(int i) {
		System.out.println("=== Wildcard length: " + i + " ===");
		System.out.println("{|class=\"wikitable sortable\" style=\"border-collapse: separate; border-spacing: 0; border-width: 1px; border-style: solid; border-color: #000; padding: 0\"");
		System.out.println("|-valign=\"top\"");
		System.out.println("! style=\"border-style: solid; border-width: 1px\"|  Pattern");
		System.out.println("! style=\"border-style: solid; border-width: 1px\"|  Positions");
		System.out.println("! style=\"border-style: solid; border-width: 1px\"|  Fragments");
		System.out.println("! style=\"border-style: solid; border-width: 1px\"|  Order");
		System.out.println("! style=\"border-style: solid; border-width: 1px\"|  Reps");
		System.out.println("! style=\"border-style: solid; border-width: 1px\"|  Probability");
	}
	
	static void wikiEnd() {
		System.out.println("|}");
	}
	
	
	/** generate fragment template from given strings.  replace non-matching symbols with wildcards */
	public static String fragmentFrom(String sub1, String sub2) {
		StringBuffer sb = new StringBuffer();
		for (int i=0; i<sub1.length(); i++) {
			if (sub1.charAt(i) == sub2.charAt(i)) sb.append(sub1.charAt(i));
			else sb.append("?");
		}
		return sb.toString();
	}
	
	/** compare various configurations of the 408 and 340 ciphers to determine if any produce
	 * lower probability repeated patterns than the others
	 */
	public static void compareConfigurations() {
		int maxLength = 20;
		
		//Unmodified 408
		search(Ciphers.cipher[1].cipher, "z408", maxLength, false);
		//Found  olson-12-false Scores: 6.956513168344105E-24, 8.149932852745386E-14, 8.149932852745386E-15 [9%P/Z/UB%kOR=pX=BP5M8RUt%L)NVEKH=GeYq!K@PH96FYGe+VWP(Z)ANlML895kJ!IrMJY^UIk7qTtNQYD5)zUpkA9#BVW\+VTtOPEqlRf%UAROPB#9/(SMI%%GzD76eUflrS=^k^LMZJdr\pFHVWe8YNk)ScE/9%%ZfAP#BV(Sz58Xq6)IK9DGq+@B9A9@+c8#F_qWqXepRNtIYElO8qGBTQS#B%OT5RUc+_dYq_^SqWkRR^UMHEqX@B#P/dL_tL#%9AYT_EKYGeZVcZKqpI)Wq!85LMr9#H!FBX9zXADd\7L!=qFkHUEe(N\6=j+RDPBcG%FQXROP5e6##de_ZcpOVWI5+tL)l^R6HZ@JTtq_8JI+rBPQW6AQJX@/ed\rYT_RD9IkIU=)MHEq6IW9rXEV]
		search("9%P/Z/UB%kOR=pX=BP5M8RUt%L)NVEKH=GeYq!K@PH96FYGe+VWP(Z)ANlML895kJ!IrMJY^UIk7qTtNQYD5)zUpkA9#BVW\\+VTtOPEqlRf%UAROPB#9/(SMI%%GzD76eUflrS=^k^LMZJdr\\pFHVWe8YNk)ScE/9%%ZfAP#BV(Sz58Xq6)IK9DGq+@B9A9@+c8#F_qWqXepRNtIYElO8qGBTQS#B%OT5RUc+_dYq_^SqWkRR^UMHEqX@B#P/dL_tL#%9AYT_EKYGeZVcZKqpI)Wq!85LMr9#H!FBX9zXADd\\7L!=qFkHUEe(N\\6=j+RDPBcG%FQXROP5e6##de_ZcpOVWI5+tL)l^R6HZ@JTtq_8JI+rBPQW6AQJX@/ed\\rYT_RD9IkIU=)MHEq6IW9rXEV", "olson-12-false", maxLength, false);
		//Found  snake olson-20-false Scores: 4.492008662594648E-24, 4.3508750263575635E-16, 4.3508750263575636E-17 [9%P/Z/UB%kOR=pX=BH!FBX9zXADd\7L!=qWV+eGYF69HP@K!qYe_ed##6e5PORXQF%GcMJY^UIk7qTtNQYD5)Z@JTtq_8JI+rBPQW6S(/9#BPORAU%fRlqEVEXr9WI6qEHM)=UIkk^LMZJdr\pFHVWe8Y@+qGD9KI)6qX85zS(RNtIYElO8qGBTQS#BLd/P#B@XqEHMU^RRkcZKqpI)Wq!85LMr9#BPDR+j=6\N(eEUHkFZcpOVWI5+tL)l^R6HI9DR_TYr\de/@XJQAP5M8RUt%L)NVEKH=GrI!Jk598LMlNA)Z(PzUpkA9#BVW\+VTtOP^=SrlfUe67DzG%%IMNk)ScE/9%%ZfAP#BVpeXqWq_F#8c+@9A9B%OT5RUc+_dYq_^SqWVZeGYKE_TYA9%#Lt_]
		search("9%P/Z/UB%kOR=pX=BH!FBX9zXADd\\7L!=qWV+eGYF69HP@K!qYe_ed##6e5PORXQF%GcMJY^UIk7qTtNQYD5)Z@JTtq_8JI+rBPQW6S(/9#BPORAU%fRlqEVEXr9WI6qEHM)=UIkk^LMZJdr\\pFHVWe8Y@+qGD9KI)6qX85zS(RNtIYElO8qGBTQS#BLd/P#B@XqEHMU^RRkcZKqpI)Wq!85LMr9#BPDR+j=6\\N(eEUHkFZcpOVWI5+tL)l^R6HI9DR_TYr\\de/@XJQAP5M8RUt%L)NVEKH=GrI!Jk598LMlNA)Z(PzUpkA9#BVW\\+VTtOP^=SrlfUe67DzG%%IMNk)ScE/9%%ZfAP#BVpeXqWq_F#8c+@9A9B%OT5RUc+_dYq_^SqWVZeGYKE_TYA9%#Lt_", "snake olson-20-false", maxLength, false);
		//Found  flip olson-15-true diagonal-1 Scores: 6.340571898230302E-25, 7.31311784895673E-14, 7.31311784895673E-15 [^=9S%VrPBel/#YpfZPqeMU/A!XJWeUfKqYqE6BZ@W^SqV7%%PqU^lZkDk%H_I_Re^qzO99FkqfGL=(GR/6#7Y%YM!S_%=EF8qdUKZLzeR%pcYcT_AEJ75dN6IXSG+t+R_d\8#tWkM=)e@NcOTrdX#IQRVBk+9QUPY\Dq6YPREcNVAYRBApA6eEB^XZBW9D5#9FX)5lrUrKPZB5T9%HzIPO+M9qDcI)O/#V9KO8IHWpRp9P%(LWX9RqJEII+OD5rSteBDXG8q6)jVRMIz_8FGQB_XqW=W_8!UY!qFTq@Eq6ITRJpH+%QtBH!\5YUkk@GST#M8N+rt5Ac#JP)5(t\%99B@/=LeLdL8#ZdUME)e)LBLIrUl/NMVk9H^@VlW#kRXEN\F6JKA+HQH)VA=ZTG(tPOP]
		search("^=9S%VrPBel/#YpfZPqeMU/A!XJWeUfKqYqE6BZ@W^SqV7%%PqU^lZkDk%H_I_Re^qzO99FkqfGL=(GR/6#7Y%YM!S_%=EF8qdUKZLzeR%pcYcT_AEJ75dN6IXSG+t+R_d\\8#tWkM=)e@NcOTrdX#IQRVBk+9QUPY\\Dq6YPREcNVAYRBApA6eEB^XZBW9D5#9FX)5lrUrKPZB5T9%HzIPO+M9qDcI)O/#V9KO8IHWpRp9P%(LWX9RqJEII+OD5rSteBDXG8q6)jVRMIz_8FGQB_XqW=W_8!UY!qFTq@Eq6ITRJpH+%QtBH!\\5YUkk@GST#M8N+rt5Ac#JP)5(t\\%99B@/=LeLdL8#ZdUME)e)LBLIrUl/NMVk9H^@VlW#kRXEN\\F6JKA+HQH)VA=ZTG(tPOP", "flip olson-15-true diagonal-1", maxLength, false);
		
		//Unmodified 340
		search(Ciphers.cipher[0].cipher, "z340", maxLength, false);
		
		search("d_<H9MlEM+-GR+bpzF>z+N8lNptZypRU^ljRUB+^Vf^d2#+XBF+5V|FB5R1(l^2P5B|y+/*#OJ4kFc2c:K5:O-+b|Py(<.cqt4%*O.1+ASGc3M%E9DdpcL&6Rp2lz+;|CWC7VT44zcpJRBU2DEYk<4GkK+6T7fJKZUY>.FFt2/|+9+^j|(GcBV<>B+d>F)SLl#*OWXpU*2y+MkWy18O5p(GbZKD-DdC#6*+T^)VT5f(HWz+CV_4.L.M-)N<WN<3NMf#zK+p7c|+pY.MzLOktP5FOz+qH|SBOFl++&GJz_SBW+@B2ZYHcBRLFOOT(|K98B/;)2A*(8L|-)RKCp;c+", "wtf", maxLength, false);
		search("y>l_z|2SU+-BdR(N#|pHBMG96F<p++zy<cGp5c8EXDFM9kcpR)l:MT2++.RR1HN+Sdl7/WUc++JBK3^>*N^zyWR^5CVMbLf(qzFp:pft#<Jltz+++1j#%Bll4k5j+7|8EW^UZ6#O;KO^9S2dNt**|cJZRCO%2(-VCz4||B5VDP+G2<+DUO*PEZb55_T3YOOWF+_Wcpdk>O.FFY4pBSp(BFNYX^C|V8cPBOMOpH7)clY.G.k1UAV+cB.+bT<LyWz<VfFLZ|4&(*++T/F#AB+*.M>T5Kt4;-&RM(Bz6|@Kzq2G-;+k8CBKK)yH4)LfLGD2+++/RcF2Op-JKL9)|2(d", "olson-15-false olson-10-false rot-90", maxLength, false);
		search("_9M+ztjd|5FP+&4k/|c.3zBK(Op^.fMqG2p8R^FlO-*dCkF>2D(|FkdW<7tB_YOB*-Cc#5+Kq%;2UcXGV.zL|RcT+L16C<+FlWB|)L(G2Jfj#O+_NYz+@L9HER>pl^VPk|1LTG2dd<M+b+ZR2FBcyA64K++)WCzWcPOSHT/()p-zlUV+^J+Op7<FBy->MDHNpkSzZO8A|K;+U+R/5tE|DYBpbTMKONp+B(#O%DWY.<*Kf)2<clRJ|*5T4M.+&BFBy:cM+UZGW()L#zHJz69Sy#+N|5FBc(;8RSpp7^l8*V3pO++RK2lGFN^f524b.cV4t++yBX1*:49CE>VUZ5-+", "olson-18-true olson-17-true olson-9-true", maxLength, false);
		search("HER>pl^VPk|1LTG2d|c.3zBK(Op^.fMqG22<clRJ|*5T4M.+&BFRcT+L16C<+FlWB|)Ld<M+b+ZR2FBcyA64K++)WCzWcPOSHT/()p_9M+ztjd|5FP+&4k/|FkdW<7tB_YOB*-CcNp+B(#O%DWY.<*Kf)>MDHNpkSzZO8A|K;+z69Sy#+N|5FBc(;8R-zlUV+^J+Op7<FBy-p8R^FlO-*dCkF>2D(By:cM+UZGW()L#zHJlGFN^f524b.cV4t++U+R/5tE|DYBpbTMKO#5+Kq%;2UcXGV.zL|Spp7^l8*V3pO++RK2yBX1*:49CE>VUZ5-+(G2Jfj#O+_NYz+@L9", "olson-8-false olson-7-false olson-15-false", maxLength, false);
		search(">MDHNpkSzZO8A|K;+HER>pl^VPk|1LTG2dlGFN^f524b.cV4t++RcT+L16C<+FlWB|)L_9M+ztjd|5FP+&4k/U+R/5tE|DYBpbTMKOyBX1*:49CE>VUZ5-+(G2Jfj#O+_NYz+@L9p8R^FlO-*dCkF>2D(Np+B(#O%DWY.<*Kf)|c.3zBK(Op^.fMqG2++)WCzWcPOSHT/()p#5+Kq%;2UcXGV.zL|2<clRJ|*5T4M.+&BFd<M+b+ZR2FBcyA64KBy:cM+UZGW()L#zHJ|FkdW<7tB_YOB*-Ccz69Sy#+N|5FBc(;8R-zlUV+^J+Op7<FBy-Spp7^l8*V3pO++RK2", "olson-10-false olson-13-false olson-13-true", maxLength, false);
	}
	
	
	public static String filenameFrom(String line) {
		line = line.substring(7);
		line = line.substring(0, line.indexOf("Scores:")-1);
		return line.replaceAll(" ", "__") + ".txt";
	}
	public static String cipherFrom(String line) {
		line = line.substring(line.indexOf("[")+1);
		return line.substring(0,line.length()-1);
		
	}
	public static void sampleFragments() {
		List<String> lines = FileUtil.loadFrom("/Users/doranchak/projects/zodiac/FragmentTransformationSearch-results-340c.txt");
		String base = "/Users/doranchak/projects/zodiac/fragment-transformations";
		
		List<String> found = new ArrayList<String>();
		for (String line : lines) {
			if (!line.startsWith("Found")) continue;
			found.add(line);
		}
		
		System.out.println("===============");
		for (int i=0; i<720; i++) {
			String f = found.remove(0);
			String fn = "0_" + i + "_" + filenameFrom(f);
			FileUtil.writeText(base+"/"+fn, cipherFrom(f));
			//System.out.println(fn + ", " + cipherFrom(f));
			//System.out.println(f);
		}
		
		List<String> shuffled = new ArrayList<String>();
		while (!found.isEmpty()) {
			int which = (int)(Math.random()*found.size());
			String f = found.remove(which);
			shuffled.add(f);
		}

		
		for (int i=0; i<720*3; i++) {
			if (i%720 == 0) 
				System.out.println("===============");
			String f = shuffled.get(i);
			String fn = (1+(i/720)) + "_" + i + "_" + filenameFrom(f);
			FileUtil.writeText(base+"/"+fn, cipherFrom(f));
			//System.out.println(f);
		}
	}
	
	public static void main(String[] args) {
		//compareConfigurations();
		sampleFragments();
		//search("HNBS_p#(d-U2zly|R+|>Epyp985G<z+<6GBcc+FMR+:pMR+2MlRc9FX.T)kD>Bc7+^KJ+U/lSN13+WdHp(M^zFqfbV5Ry^*zLCWNl#+ltl%j++tJ#f:B1z<p^OU8jO;#Z^E|+54K6W7kV%Z*d-2ORJ|*N29(CctSPDGV|*U+2+D5|4CO<PBzkWW35dc_FOYT5bEp+O_Z|Y(pFCXNBpB4F.>^FSYO1.)OPkGYc7pMBcV.lHO8L<L++FVzy<b.cVUfWTBAT*#+&>.+AFT+(4ZMB/*|GKzR42z@6BM&;t5q|(-K2fHKkDLL4yKB8+-G))C;d)J2/(|9K-OFR++2Lpc+");
		//search("d2GTL1|kPV^lp>REH)fK*<.YWD%O#(B+pNJHz#L)(WGZU+Mc:yB2KR++Op3V*8l^7ppS/k4&+PF5|djtz+M9_(D2>FkCd*-OlF^R8p|Lz.VGXcU2;%qK+5#9L@+zYN_+O#jfJ2G(K46AycBF2RZ+b+M<d-yBF<7pO+J^+VUlz-OKMTbpBYD|Et5/R+UFB&+.M4T5*|JRlc<2R8;(cBF5|N+#yS96z++t4Vc.b425f^NFGl+-5ZUV>EC94:*1XBy2GqMf.^pO(KBz3.c|L)|BWlF+<C61L+TcRp)(/THSOPcWzCW)++cC-*BOY_Bt7<WdkF|+;K|A8OZzSkpNHDM>");
		
		// http://www.ciphermysteries.com/2011/11/09/the-lady-magdalene-de-lancey-ciphers-2
		//search("shtesirreshteltdtyetoogdterldcofcshtsrglateshedeshdgtrshitdhlyskbtwisterhdgthisthatlydsrsofseedltilingsorgtrdbghiliglingsolersafftchiygfetdjsitlotltertelrleslitypdgrebrditlosefdrelrsethligksofillttleselrgridthjdbgdtehtetsisovdlgansdtofdrhtghldsdbtdilijchesoerdreisgdrdtatderihjsthitsibitbdersjchrtdthsdpsprotisshojosbtjeexptrtiyleitltsbijte");
		// http://en.wikipedia.org/wiki/D'Agapeyeff_cipher
		//search("75628285916291648164917485846474748284838163818174748262647583828491757465837575759363656581638175857575646282928574638275748381658184856485648585638272628362818172816463758281648363828581636363047481919184638584656485656294626285918591749172756465757165836264748182846282649181936562648484918385749181657274838385828364627262656283759272638282727283828584758281837284628283758164757485816292000");
		
	}
}
