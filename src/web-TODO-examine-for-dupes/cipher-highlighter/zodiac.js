var WIDTH=17;
var HEIGHT=0;

/* many thanks to http://mspeight.blogspot.com/2007/05/how-to-disable-backspace-in-ie-and.html for this delete/backspace trapper */
if (typeof window.event != 'undefined') // IE
  document.onkeydown = function() // IE
    {
    var t=event.srcElement.type;
    var kc=event.keyCode;
    return ((kc != 8) || ( t == 'text') ||
             (t == 'textarea') || ( t == 'submit'))
    }
else
  document.onkeypress = function(e)  // FireFox/Others 
    {
    var t=e.target.type;
    var kc=e.keyCode;
    if ((kc != 8) || ( t == 'text') ||
        (t == 'textarea') || ( t == 'submit'))
        return true
    else {
        return false
    }
   }

	var doStats = false;
	var stats = new Array(2);
	var frequencies = new Array();
	var statsSortedKeys = new Array(2);
	var cipherLength;

	var cipherReset;

	var letterFrequencies = new Array(
	);
	letterFrequencies["A"] = 0.08167;
	letterFrequencies["B"] = 0.01492;	
	letterFrequencies["C"] = 0.02782;
	letterFrequencies["D"] = 0.04253; 	
	letterFrequencies["E"] = 0.12702; 	
	letterFrequencies["F"] = 0.02228; 	
	letterFrequencies["G"] = 0.02015; 	
	letterFrequencies["H"] = 0.06094; 	
	letterFrequencies["I"] = 0.06966; 	
	letterFrequencies["J"] = 0.00153; 	
	letterFrequencies["K"] = 0.00772; 	
	letterFrequencies["L"] = 0.04025; 	
	letterFrequencies["M"] = 0.02406; 	
	letterFrequencies["N"] = 0.06749;
	letterFrequencies["O"] = 0.07507;
	letterFrequencies["P"] = 0.01929;
	letterFrequencies["Q"] = 0.00095;
	letterFrequencies["R"] = 0.05987;
	letterFrequencies["S"] = 0.06327;
	letterFrequencies["T"] = 0.09056;
	letterFrequencies["U"] = 0.02758;
	letterFrequencies["V"] = 0.00978;
	letterFrequencies["W"] = 0.02360;
	letterFrequencies["X"] = 0.00150;
	letterFrequencies["Y"] = 0.01974;
	letterFrequencies["Z"] = 0.00074;
	
	
	var images = new Array( "alphabet/a.jpg","alphabet/b.jpg","alphabet/bb.jpg","alphabet/bc.jpg","alphabet/bd.jpg","alphabet/bf.jpg","alphabet/bj.jpg","alphabet/bk.jpg","alphabet/bl.jpg",
	"alphabet/bp.jpg","alphabet/bq.jpg","alphabet/by.jpg","alphabet/c.jpg","alphabet/caret.jpg","alphabet/d.jpg","alphabet/dash.jpg","alphabet/dot.jpg","alphabet/e.jpg",
	"alphabet/f.jpg","alphabet/g.jpg","alphabet/gt.jpg","alphabet/h.jpg","alphabet/i.jpg","alphabet/idl.jpg","alphabet/idr.jpg","alphabet/j.jpg","alphabet/k.jpg",
	"alphabet/l.jpg","alphabet/lt.jpg","alphabet/m.jpg","alphabet/n.jpg","alphabet/n1.jpg","alphabet/n2.jpg","alphabet/n3.jpg","alphabet/n4.jpg","alphabet/n5.jpg",
	"alphabet/n6.jpg","alphabet/n7.jpg","alphabet/n8.jpg","alphabet/n9.jpg","alphabet/o.jpg","alphabet/p.jpg","alphabet/perp.jpg","alphabet/pf.jpg","alphabet/phi.jpg",
	"alphabet/plus.jpg","alphabet/r.jpg","alphabet/s.jpg","alphabet/slash.jpg","alphabet/sq.jpg","alphabet/sqd.jpg","alphabet/sqe.jpg","alphabet/sql.jpg","alphabet/sqr.jpg",
	"alphabet/t.jpg","alphabet/theta.jpg","alphabet/u.jpg","alphabet/v.jpg","alphabet/w.jpg","alphabet/x.jpg","alphabet/y.jpg","alphabet/z.jpg","alphabet/zodiac.jpg");

	function preload() {
		var objImage = new Image();
		for	(i=0; i<images.length; i++)
		{
			objImage.src = images[i];
		}
	}

	function triTest() {
		for (i=0; i<20; i++) {
			for (j=0; j<20; j++) {
				if (i!=j) {
					if (tri[i].charAt(2) == tri[j].charAt(0)) {
						document.write(tri[i]+tri[j].substring(1,3) + ".  ");
					}
				}
			}
		}
	}

	var tri = new Array("the",
	"ing",
	"her",
	"you",
	"thi",
	"ill",
	"eth",
	"all",
	"and",
	"nth",
	"ave",
	"tha",
	"sth",
	"tth",
	"hat",
	"out",
	"his",
	"was",
	"ent",
	"int",
	"one",
	"hem",
	"hav",
	"ver",
	"ith",
	"ist",
	"ere",
	"wil",
	"hen",
	"ont",
	"for",
	"hin",
	"ter",
	"est",
	"oth",
	"not",
	"wit",
	"dth",
	"ast",
	"rth",
	"fth",
	"lli",
	"utt",
	"lle",
	"ome",
	"but",
	"eve",
	"ght",
	"sha",
	"ers",
	"tin",
	"ish",
	"ngt",
	"heb",
	"hey",
	"ear",
	"ple",
	"she",
	"hep",
	"kin",
	"lin",
	"ein",
	"oul",
	"hes",
	"who",
	"uld",
	"eni",
	"edt",
	"era",
	"ice",
	"kil",
	"abo",
	"are",
	"eri",
	"oft",
	"som",
	"sis",
	"bou",
	"res",
	"par",
	"rea",
	"edi",
	"tor",
	"rin",
	"ive",
	"iss",
	"ell",
	"ack",
	"hew",
	"hal",
	"ton",
	"ert",
	"ati",
	"tto",
	"igh",
	"sse",
	"ore",
	"hel",
	"ort",
	"iti",
	"hec",
	"our",
	"red",
	"oun",
	"eof",
	"sho",
	"whe",
	"ate",
	"odi",
	"emi",
	"ons",
	"ran",
	"iam",
	"eco",
	"sta",
	"att",
	"dia",
	"ove",
	"ion",
	"art",
	"ndt",
	"idi",
	"yth",
	"ead",
	"con",
	"unt",
	"der",
	"nge",
	"eca",
	"ngi",
	"itw",
	"ath",
	"hed",
	"eli",
	"lic",
	"nce",
	"sed",
	"wou",
	"isi",
	"peo",
	"een",
	"tot",
	"ean",
	"lea",
	"llo",
	"tof",
	"ero",
	"iha",
	"eto",
	"tis",
	"own",
	"han",
	"way",
	"nto",
	"las",
	"sto",
	"led",
	"llt",
	"nin",
	"tim",
	"san",
	"dit");


	var which = 0;

	var ciphers = new Array(
//z408		
//"9%P/Z/UB%kOR=pX=BWV+eGYF69HP@K!qYeMJY^UIk7qTtNQYD5)S(/9#BPORAU%fRlqEk^LMZJdr\\pFHVWe8Y@+qGD9KI)6qX85zS(RNtIYElO8qGBTQS#BLd/P#B@XqEHMU^RRkcZKqpI)Wq!85LMr9#BPDR+j=6\\N(eEUHkFZcpOVWI5+tL)l^R6HI9DR_TYr\\de/@XJQAP5M8RUt%L)NVEKH=GrI!Jk598LMlNA)Z(PzUpkA9#BVW\\+VTtOP^=SrlfUe67DzG%%IMNk)ScE/9%%ZfAP#BVpeXqWq_F#8c+@9A9B%OT5RUc+_dYq_^SqWVZeGYKE_TYA9%#Lt_H!FBX9zXADd\\7L!=q_ed##6e5PORXQF%GcZ@JTtq_8JI+rBPQW6VEXr9WI6qEHM)=UIk"			
//z408 solution
//"ILIKEKILLINGPEOPLEBECAUSEITISSOMUCHFUNITISMOREFUNTHANKILLINGWILDGAMEINTHEFORRESTBECAUSEMANISTHEMOATDANGERTUEANAMALOFALLTOKILLSOMETHINGGIVESMETHEMOATTHRILLINGEXPERENCEITISEVENBETTERTHANGETTINGYOURROCKSOFFWITHAGIRLTHEBESTPARTOFITIATHAEWHENIDIEIWILLBEREBORNINPARADICESNDALLTHEIHAVEKILLEDWILLBECOMEMYSLAVESIWILLNOTGIVEYOUMYNAMEBECAUSEYOUWILLTRYTOSLOIDOWNORSTOPMYCOLLECTINGOFSLAVESFORMYAFTERLIFEEBEORIETEMETHHPITI"
//z340
//"HER>pl^VPk|1LTG2dNp+B(#O%DWY.<*Kf)By:cM+UZGW()L#zHJSpp7^l8*V3pO++RK2_9M+ztjd|5FP+&4k/p8R^FlO-*dCkF>2D(#5+Kq%;2UcXGV.zL|(G2Jfj#O+_NYz+@L9d<M+b+ZR2FBcyA64K-zlUV+^J+Op7<FBy-U+R/5tE|DYBpbTMKO2<clRJ|*5T4M.+&BFz69Sy#+N|5FBc(;8RlGFN^f524b.cV4t++yBX1*:49CE>VUZ5-+|c.3zBK(Op^.fMqG2RcT+L16C<+FlWB|)L++)WCzWcPOSHT/()p|FkdW<7tB_YOB*-Cc>MDHNpkSzZO8A|K;+"
// z340 mean sigma 0.42876895529129533
//"HER>pl^VPk|1LTG2dNp+B(#O%DWY.<*Kf)2<clRJ|*5T4M.+&BFz69Sy#+N|5FBc(;8RlGFN^f524b.cV4t++|FkdW<7tB_YOB*-Cc>MDHNpkSzZO8A|K;+(G2Jfj#O+_NYz+@L9d<M+b+ZR2FBcyA64K-zlUV+^J+Op7<FBy-U+R/5tE|DYBpbTMKOBy:cM+UZGW()L#zHJSpp7^l8*V3pO++RK2_9M+ztjd|5FP+&4k/yBX1*:49CE>VUZ5-+|c.3zBK(Op^.fMqG2RcT+L16C<+FlWB|)L++)WCzWcPOSHT/()pp8R^FlO-*dCkF>2D(#5+Kq%;2UcXGV.zL|"
//z340 mean sigma 0.4690438940801294
//"HER>pl^VPk|1LTG2dNp+B(#O%DWY.<*Kf)p8R^FlO-*dCkF>2D(z69Sy#+N|5FBc(;8RlGFN^f524b.cV4t++|FkdW<7tB_YOB*-Cc>MDHNpkSzZO8A|K;+(G2Jfj#O+_NYz+@L9d<M+b+ZR2FBcyA64K-zlUV+^J+Op7<FBy-U+R/5tE|DYBpbTMKOBy:cM+UZGW()L#zHJSpp7^l8*V3pO++RK2_9M+ztjd|5FP+&4k/yBX1*:49CE>VUZ5-+|c.3zBK(Op^.fMqG2RcT+L16C<+FlWB|)L++)WCzWcPOSHT/()p2<clRJ|*5T4M.+&BF#5+Kq%;2UcXGV.zL|"
// z340 mean sigma 0.5869620666091453
//"++)WCzWcPOSHT/()p2<clRJ|*5T4M.+&BF>MDHNpkSzZO8A|K;+lGFN^f524b.cV4t++By:cM+UZGW()L#zHJp8R^FlO-*dCkF>2D(_9M+ztjd|5FP+&4k/d<M+b+ZR2FBcyA64KSpp7^l8*V3pO++RK2(G2Jfj#O+_NYz+@L9U+R/5tE|DYBpbTMKO|FkdW<7tB_YOB*-CcHER>pl^VPk|1LTG2dNp+B(#O%DWY.<*Kf)yBX1*:49CE>VUZ5-+|c.3zBK(Op^.fMqG2RcT+L16C<+FlWB|)L#5+Kq%;2UcXGV.zL|z69Sy#+N|5FBc(;8R-zlUV+^J+Op7<FBy-"
//z340 kasiski version
//"HER>pl^VPk|1LTG2dNp+B(#O%DWY.<*Kf)FP+&4k/p8R^FlO-*dCkF>2D(#5+Kq%;2UcBy:cM+UZGW()L#zHJSpp7^l8*V3pO++RK2XGV.zL|(G2Jfj#O+_NYz+@L9d<M+b+ZR2F_9M+ztjd|5FP+&4k/p8R^FlO-*dCkF>2D(BcyA64K-zlUV+^J+Op7<FBy-U+R/5tE|DY#5+Kq%;2UcXGV.zL|(G2Jfj#O+_NYz+@L9BpbTMKO2<clRJ|*5T4M.+&BFz69Sy#+N|5d<M+b+ZR2FBcyA64K-zlUV+^J+Op7<FBy-FBc(;8RlGFN^f524b.cV4t++yBX1*:49CEU+R/5tE|DYBpbTMKO2<clRJ|*5T4M.+&BF>VUZ5-+|c.3zBK(Op^.fMqG2RcT+L16C<+z69Sy#+N|5FBc(;8RlGFN^f524b.cV4t++FlWB|)L++)WCzWcPOSHT/()p|FkdW<7tB_yBX1*:49CE>VUZ5-+|c.3zBK(Op^.fMqG2YOB*-Cc>MDHNpkSzZO8A|K;+HER>pl^VPkRcT+L16C<+FlWB|)L++)WCzWcPOSHT/()p|1LTG2dNp+B(#O%DWY.<*Kf)By:cM+UZGW|FkdW<7tB_YOB*-Cc>MDHNpkSzZO8A|K;+()L#zHJSpp7^l8*V3pO++RK2_9M+ztjd|5"
//"OB+M|C|lU%DB(SqZOT-BRBc^>Fdcc5Ek+HL2pWc*R++P;lVtXz4BzW2D.9L(pK8z2)7@(1qGlE7MFVccp+J/6ycBL9zOcLYRG(1CHGU+:yy3+TyVbM8K^+LWK(F+Ldft>bUpp+*;O:S2*%5+Rk-4|F4Dj/#<V5D.</b;-KjB5GO.6c+.+S2)WFd.Z8pJ+G-(pU)Fyf|_zO.<C&Vp*F#O>)l+|_9OMZT|+BWl^Tc+*YBM5+PVJ3tldFH^MFPUzA*|l5O1ZN2MRNB5R|t+fBRT6>+#N29zO|EFYJ8+4kpp^fK<2+-KX2CK#&7k4<+z<+N)CW^(pk_dR4HzNGAYS|B#"
//z340 period 19
//"H+M8|CV@K+l#2E.B)>EB+*5k.L-RR+4>f|pMR(UVFFz9z/JNbVM)|D>#Z3P>Ldl5||.UqLFHpOGp+2|<Ut*5cZG+kNl%WO&D(MVE5FV52+dp^D(+4(G++|TB4-R)WkVW)+k#2b^D4ct+cW<SPYLR/5J+JYM(+|TC7zk.#Kp+fZ+B.;+c+ztZ|<z28KjROp+8y.LWBO1*H_Rq#2pb&RB31c_8LKJ9^%OF7TBlXz6PYATfSMF;+B<MFG1BCOO|G)p+l2_cFKzF*K<SBK2BpzOUNyBO6N:(+H*;dy7t-cYAy29^4OFT-+N:^j*Xz6-<Sf9pl/CpclddG+4Ucy5C^W(c"
//z340 period 26
//"HWpF+YUby4B|7;EYpPKzVT#tK)t+R.7+q++M++(LB><^&%@^KN+O+_p*l4;LJO|yp+YlK8k29+25B^)O^f*/UdO<FX.WBV)Vpc<pcB1fC*PB38XM7lc*Mz-kypRG+<R(:qWC|:O^VbFJ;4Gcc1c+F.+B|892P>LM+lzZy*RCROMT+ROLR-5lEcSDGUK-|2UTG>THH2Z2*(F+4FV+TNdG_dGBRMNUL/pNW9C2c/.^Z1(kp(MkJy5+f56)S+)+FfAt&5-CpzBLz>j6EB2+<|Z(#t2#4|F4|+FO#zjDOKDzbcFk8OHd(+-Y6..ldA%J|#_zB9c3WW|DS55NlpSVzB<K"
//z340 period 39
//"H+FLU54c7EUP|VTtTtRZ+(+4++B>G&G^M+L_pW42J.y1Yl(kJ++B6O^)/fO&XCBVLpjpB1<*P#8#7F*+-kzRO<z:FC|H^+F64lc1JF_B99W>LSlNySCBMTpOY-yE|DGp-zU#>)H27*+++VLNd^d@RNU+pNlCL/|Z+kp8k9555)S+*FdtF-WzBV><EB+CZ(32M|c|zO#pD+D(cW8OO(bY;.cA%+#+B83P|D+5ZpRzOKWR+RblBS;YKK2TGKH+.2qFMF(T<_%BKNO/*9;cO^p(KM2y2f^)f+UA<5.p)zc6c2f|BtX4l4MFyjGKRbqk:dV-J.Gdc|.z|c2WM5zl*VR<"
//z340 rot90 ccw + vertical flip
//"HNBS_p#(d-U2zly|R+|>Epyp985G<z+<6GBcc+FMR+:pMR+2MlRc9FX.T)kD>Bc7+^KJ+U/lSN13+WdHp(M^zFqfbV5Ry^*zLCWNl#+ltl%j++tJ#f:B1z<p^OU8jO;#Z^E|+54K6W7kV%Z*d-2ORJ|*N29(CctSPDGV|*U+2+D5|4CO<PBzkWW35dc_FOYT5bEp+O_Z|Y(pFCXNBpB4F.>^FSYO1.)OPkGYc7pMBcV.lHO8L<L++FVzy<b.cVUfWTBAT*#+&>.+AFT+(4ZMB/*|GKzR42z@6BM&;t5q|(-K2fHKkDLL4yKB8+-G))C;d)J2/(|9K-OFR++2Lpc+"
//z340 mr lowe scytale operations (vertical pattern)
//"H+>5^|kBLT2ON<BRO*W4<+fFB6cyUNWFL(HRSG7^823.+4K+_B+*j95>+Zk+pc^zO(d^FMD2#cKL;CcFVBLL(+JC#c_Sz/LpdF+WZtFYy*4c-MUN^SOO<|y+UR/lEPY1bGK2+l#|DT..KBz:S++G5)cz8lpNl5VbOVR+yM1t4|EPU4-|R3lK*pkf2GR++%6U+GWz)+2WjW+OYT@)|Md+72_cB6C>lH+k+Z7AB;ERptVD|pTMdpc(J%5YM*&)y9M#Z|(B#;JpF^f*4pc+t29Xz:dCFV&5/8.FB-OC.>q(5Tq12<Xl.||G)fzOPNH+(9<kb<RBBOA-KzDVpJzp8FK-"
//"H+FLU54c7EUP|VTtTtRZ+(+4++B>G&G^M+L_pW42J.y1Yl(kJ++B6O^)/fO&XCBVLpjpB1<*P#8#7F*+-kzRO<z:FC|H^+F64lc1JF_B99W>LSlNySCBMTpOY-yE|DGp-zU#>)H27*+++VLNd^d@RNU+pNlCL/|Z+kp8k9555)S+*FdtF-WzBV><EB+CZ(32M|c|zO#pD+D(cW8OO(bY;.cA%+#+B83P|D+5ZpRzOKWR+RblBS;YKK2TGKH+.2qFMF(T<_%BKNO/*9;cO^p(KM2y2f^)f+UA<5.p)zc6c2f|BtX4l4MFyjGKRbqk:dV-J.Gdc|.z|c2WM5zl*VR<"
//z340 flipped 
//"d2GTL1|kPV^lp>REH)fK*<.YWD%O#(B+pNJHz#L)(WGZU+Mc:yB2KR++Op3V*8l^7ppS/k4&+PF5|djtz+M9_(D2>FkCd*-OlF^R8p|Lz.VGXcU2;%qK+5#9L@+zYN_+O#jfJ2G(K46AycBF2RZ+b+M<d-yBF<7pO+J^+VUlz-OKMTbpBYD|Et5/R+UFB&+.M4T5*|JRlc<2R8;(cBF5|N+#yS96z++t4Vc.b425f^NFGl+-5ZUV>EC94:*1XBy2GqMf.^pO(KBz3.c|L)|BWlF+<C61L+TcRp)(/THSOPcWzCW)++cC-*BOY_Bt7<WdkF|+;K|A8OZzSkpNHDM>"
//z340 flipped + period 29
//"d(32+Rb2f^)d2BV>5ZpR^p(kG+*F#+B8NO/FTp8k9bY;F(T|LNlCL+D(GKH+1J^d@M|clBS;|H7*+<EB+zOKkzp-zdtF-3P|P#pOY-555.cAVLSlNy/|ZcW8^)/F_BRNU|zOl(k^+F++VLCZpW4RO<U#>)Wz>G&8#7FyE|)SRZ+pjpBSCB+kEUP|fO&99W+pH+FLJ++64lcN)M5z2J.z:FCHfc|.G^M+*+-DK:dV(+4+1<*M*yjGKVTtXCB><BtX4U54B6O.2zc6l*Vy1YYK+UAz|c2L_WRM2y-J.G+BD+9;cORbqTt%+_%BKl4Mc7OO(qFMc2fR<#pDK2T<5.pW"
		// flipped + period 15
		//"dEB+*5k.L(MVE5FV52c+ztZ2H+M8|CV@K<Ut*5cZG|TC7zG)pclddG+4dl5||.UqLcW<STfN:^j*Xz6-z/JNbVM)R)WkLKJy7t-cYAy-RR+4>f|p+dp1*HBpzOUNyBO+l#2E.B)+kN|<z2p+l2_cFKUcy5C^W(cFHk.#KSMF;+B<MF<Sf9pl/C|DPYLR/9^%OF7TB29^4OFT-+MVW)+k_Rq#2pb&R6N:(+H*;>^D(+4(8KjROp+8zF*K<SBKl%WO&Dp+fZ+B.;+G1BCOO|pOGp+2|5J+JYM(+lXz6PYA>#Z3P>L#2b^D4ct+B31c_8R(UVFFz9G++|TB4-y.LWBO"		
		// flipped + period 15 + period row 10
		//"dEB+*5k.L(MVE5FV5B<MF<Sf9pl/C|DPYL2c+ztZ2H+M8|CV@K<R/9^%OF7TB29^4OFTUt*5cZG|TC7zG)pcl-+MVW)+k_Rq#2pb&RddG+4dl5||.UqLcW<6N:(+H*;>^D(+4(8KSTfN:^j*Xz6-z/JNbjROp+8zF*K<SBKl%WVM)R)WkLKJy7t-cYAO&Dp+fZ+B.;+G1BCOy-RR+4>f|p+dp1*HBO|pOGp+2|5J+JYM(+pzOUNyBO+l#2E.B)+lXz6PYA>#Z3P>L#2bkN|<z2p+l2_cFKUcy^D4ct+B31c_8R(UVF5C^W(cFHk.#KSMF;+Fz9G++|TB4-y.LWBO"
		// period col 2, period 18, period row 15
	//"H+M8|CV@Kz/JNbVM)+B.;+B31c_81*H_Rq|DR(UVFFz9<Ut*5cZ#2pb&RG1BCOO|TfSMG+kNpOGp+2|G++|TBF;+B<MF6N:(+H*;2B4-R)Wk^D(+4(5J+JYpzOUNyBO<Sf9pl/CNM(+|TC7zPYLR/8KjR:^j*Xz6-+l#2E.B)>Op+8y.LWBO|<z29^%OF7TBlXz6PYALKJp+l2_cFKzF*K<SBKG)y7t-cYAy29^4OFT-+dpclddG+4Ucy5C^W(cMEB+*5k.L-RR+4>f|pFH>#Z3P>Ldl5||.UqL+dpl%WO&D(MVE5FV52cW<SVW)+k#2b^D4ct+c+ztZk.#Kp+fZ"
//		"IDEYETGEHDTHENODOS_HRTDLSDEEMTOGCNAREEHTSDFIHOAULIDODAERHENH__ADTIEPCNHENDAPBHTHEHTTONHNTMIHVATWTVEPWOE_ENHILIIHTPHUHOSTEWYEOHAHETOBLTITOAHEDLST__HEADSMNEOLILTMITNTWTFECEIBUTEOLTHHIDAPROW__ELETEAPBSASRIHTWKTNGTNEDNESHSUFTAAGNRELDI__AYYIDNRLITHINEHENHTHAEASAOSTTOENOSHLOCTE__DOTTELOSITGBNRLEHAROHURDMDBKHHENEWRAEWOTR__LNHSMEMIMHSEDEEYETEUARW"
//"+A|d4SNp(+EHp_z>||7|(RB4S+HfN6OO_pRlcpV&c4clTzVccWFG^4+K+BB+yJB+6PtLcVcCKC)RF^)F3H*EfO#+-B>FLCkV|zT4p++|;#8(bzB+Ep9||22<F>ZK5OyG.zO2F+J+;Bt&OKU*)CWD+ZR+/+-*RSlFM|9T#bGNt<Oj#G9zV8Mc1RUMzpBW<+MFk/%52cT+K6fd2_^-yMRkt)M+2Y/.W+.X(5*.5.+85:)^X42((B|PY18^p3d%lYK>pqzl<yfN+O.FkMD;FZ:-ljD(ABC*TYWZ#9L<NH75WVUOb5LBqDGPS<cRL+l2Jz2K^1pL*-+BypUOkdJdUG7@"		
//"Hp:7zl;-2-BMc45GL|>E+c^t-2+Fpp.(t-2+FMRBMlj-U_B7b+;++R+kD>(+8d*cNc<T&8+|c)dHp#U*|dXYyFMBRycTWWNl-ZV5CGzABKFlB.+C<p^%G3FkV+6y-zGX3Lz7kVDWpPF.@4-26F1z1WtSPW(-+>zLKU<9N*B6cBzkY)+&2L9-+cS^:KCP_Z|.L+4D|dzRlyf4(<-Y-1<#Rk((<l/R#59-+S-8L*zK/#GMU5J+2CpFHBATKH2p52+Vt|N4E^lT*|GfJ_8+Jb+E*|b>.W/-K2)S9RKf+^|55.VfB(C;dBpM^qjZJDTFcUM|)c+Nyp+F%#R+Y4BVZq)p"
//"Hl|2BD*yU)J^3RMd+plCDKU.G#Y9bF6lJ<UtBKl5+6+BR^btX9U|B^G+<B+(Hp(_-DSAE^1d((K:ZLSlpK+|&8Ok(qcz2Ozd+B4U+F+EpORT&9Nclf.+1CZcK.2L+|)cT|<YCHz|RVLN#YfcG#p8O2z54R-F#%XLJ++<ZcKVOBR|b2J4BS|(G5c+*E5.(fR1F)(P/F7OcNZK>PTpO.)M(zp*+_tFk^*>5;G|f_@MRy-+py/DT<|MFy5;F2Vy:>-3OMc6lLCO(ktB>pO;pkG+%<B+(H7V+9jP/Fd2+2V(jNL+2Az^7-5YMc*.z#F8N44B4V+zpqTC(+zS)dB*Mk8+"
// shuffle
//"z+zR2dG-S6)+)|JyO@6pyL:%U.|*&2<++SlyJB-HD<NfB|l+O^cB+T4>T^M5KcqO8UD>.jMzCF.DWf##MPC)5;5N+|XY&GNFJ.^(k3lW4d>kz55(RK#pFC2yMV-2(7LOAT9ZRd4zBSF;OXJlz1PON<FOk6KpBZ#G/zt%:+;-FZ)pR+(p+UUVK+pTt(|8|^+c1cY2q*+GEMSH+FGZt7V2+2W85W1cR_cE4B7.L/Y+_*W+B*|93ORB_pM4c+L^5b.9(|GRkt+c*blkcz2^</lB#Hp(9dWBVpV+p+VcF<|-fz2f+bF|Np>*dKOREAYPBUHTK+LCDKyCM4jL<8+BOF)l"
// ryan garlick
//"OKMTbpBYDd2GTL1|kPDWY.<*Kf)5T4M.+&BFR8;(cBF5|JHz#L)(WGV3pO++RK24b.cV4t+++-5ZUV>EC/k4&+PF5|*dCkF>2D(Op^.fMqG2L)|BWlF+<|Lz.VGXcU+_NYz+@L9POSHT/()pcC-*BOY_BK46AycBF2+Op7<FBy-zZO8A|K;+|Et5/R+UV^lp>REHNp+B(#O%2<clRJ|*N+#yS96zZU+Mc:yBSpp7^l8*lGFN^f5294:*1XBydjtz+M9_p8R^FlO-|c.3zBK(C61L+TcR2;%qK+5#(G2Jfj#O++)WCzWct7<WdkF|RZ+b+M<d-zlUV+^J>MDHNpkS" 
// largo
//"KBS<K*FzKFc_2l+pJ*H+(:N6FByNUOzpB)TFO4^92yAYc-t7yN;lp9fS<O6zX*j^:pd-^C5ycU4+Gddlc+HC/E2#l+-@VC|8MBE+(W4+RR-L.k5*+(R>)B.NJ/zKzFFVU#>Mc|f>|5ldL>P3ZOpD|)MVbtU<92+pG%lHFpqU.|VM(D&OWD^Nk+GZc5*+G|4+(WVpd+L5VF5E2#k+)YPkW)R-4BT|+5(RL.kS<Wc2tc4D^bpK#<|z7CT|+(MYJ+J/z*1Ztz+c+;.B+Zf+HKLOBWL.y8+pORjK8fT8_c13B+&bp2#qR_GAYP6zXlBT7FO%^922|OOCB1GRM<B+;FMS"
//shuffle - leo ron and rob
//"y+4|tRON2BG6+cAp.R<Jl9|9)VcPFz|l*+p)3+V|4RG(Z+c+8+z92C2GSzU()d7E;|k^LA32j#<)p+FJl%|z>cyT1Zd2O2cjq2pC((J.RBY1YBp|Hc-GR^9<MFlLtHFWkOKzF*7Hb;TO*+dE4T:755z)bS-CV#FY<DK<HfM5^BX(Npc>t+*L+_@fOWb2NROBS(D1^-_l#k^+.MU+#LEOLV+F*lUK-zpF+LcC84+Nk.YBOOD<tZVlUU%+BqpMPKOz5f4+/R6W+BS+c(GKNp.W_84M:.BMfBy5/+BJpPKB6#X&5y>+T-W*c/|pd|dy|+D2Z;W8GMKRF&TFVzk>^C5+"
// shuffle - lou bob and ted
//"1O2MFElR+BH5+(#N*4+K7jU2z*^OF|T&AP^|+Dp1+(/-dp+R#&+V+N.2MS:O/BMpy557WL2JDc<_Ud<+zyMVVT9+)cb43K;Rc*_GyBJZB(tAFP+WtTED(KM.<kU<52L2tF94|#|5WpFdM9ccZd#<8;DC<JX9.+J)Ekp)-+8FOBOB5+4fp;M^LOUztZ2|+pY17F8cS:#YWc_Y2+pRz*4RckGBSRkWV.+qR2fcGl|/N6OTKf%*-4lWC)+ckK|5.Y-b+dq|+FC|HVT%pBy8HPj)^3LlpXzllU-(Kz>C|RfV^6(l^N.>*NB+GCbLG@O++6OZFzzLBKFS(zOG>>yH+BBp"
"ABCDEFGHIJKLCMBNOKPJQIRSTUSVBQWOXEBYZSabWAVVBcNdeWIRfgRDZGhRifOABSjkiRYeekilmQDcUQnohpDGqrAApRshRdgtuirHRvwFKTSSxXRINqCFnBYKyBzwSRytZCgQuI0NsqXSO12LI3TdilF45IyBoUkxfrBr35bsGXlwPSldikIC3g2K2DJA0M05F2dgBW6hfuUnEwIIUAuiO7Lq3vSWwHXeUo0ainrOJaeHTYssX5JqF3f6RoxOaxeh53gnkxGpvArTNXnJrYezSPOKSFTEugDkxl1k65dSIiyk602yNDrxSbkL7HkxtpITB5DGhh6fOpY3cp5y"
	);


	var cipher;
	
		var cipherlineUNUSED = new Array(
		"HER>pl^VPk|1LTG2d"+
	  "Np+B(#O%DWY.<*Kf)"+
	  "By:cM+UZGW()L#zHJ"+
	  "Spp7^l8*V3pO++RK2"+
	  "_9M+ztjd|5FP+&4k/"+
	  "p8R^FlO-*dCkF>2D("+
	  "#5+Kq%;2UcXGV.zL|"+
	  "(G2Jfj#O+_NYz+@L9"+
	  "d<M+b+ZR2FBcyA64K"+
	  "-zlUV+^J+Op7<FBy-"+
	  "U+R/5tE|DYBpbTMKO"+
	  "2<clRJ|*5T4M.+&BF"+
	  "z69Sy#+N|5FBc(;8R"+
	  "lGFN^f524b.cV4t++"+
	  "yBX1*:49CE>VUZ5-+"+
	  "|c.3zBK(Op^.fMqG2"+
	  "RcT+L16C<+FlWB|)L"+
	  "++)WCzWcPOSHT/()p"+
	  "|FkdW<7tB_YOB*-Cc"+
	  ">MDHNpkSzZO8A|K;+",
	
		"9%P/Z/UB%kOR=pX=B" + 
		"WV+eGYF69HP@K!qYe" + 
		"MJY^UIk7qTtNQYD5)" + 
		"S(/9#BPORAU%fRlqE" + 
		"k^LMZJdr\\pFHVWe8Y" +
		"@+qGD9KI)6qX85zS(" +
		"RNtIYElO8qGBTQS#B" +
		"Ld/P#B@XqEHMU^RRk" +
		"cZKqpI)Wq!85LMr9#" +
		"BPDR+j=6\\N(eEUHkF" +
		"ZcpOVWI5+tL)l^R6H" +
		"I9DR_TYr\\de/@XJQA" +
		"P5M8RUt%L)NVEKH=G" +
		"rI!Jk598LMlNA)Z(P" +
		"zUpkA9#BVW\\+VTtOP" + 
		"^=SrlfUe67DzG%%IM" +
		"Nk)ScE/9%%ZfAP#BV" +
		"peXqWq_F#8c+@9A9B" +
		"%OT5RUc+_dYq_^SqW" +
		"VZeGYKE_TYA9%#Lt_" +
		"H!FBX9zXADd\\7L!=q" +
		"_ed##6e5PORXQF%Gc" +
		"Z@JTtq_8JI+rBPQW6" +
		"VEXr9WI6qEHM)=UIk"
	);

	var alphabet = new Array("ABCDEFGH|JKLMNOPRSTUVWXYZ123456789plkdfycjqbtz()>^+.<-/#_@*%&;:", "ABDEFGHIJKLMNOPQRSTUVWXYZ56789cdefjklpqrtz!#%()=+/@\\^_"
	);

		var interestingHash = new Array(new Array(
/*			{name:"killing",decoder:"llvicdlotkrbeggbwmnsehontiitaapinegimteseielldstbirvkaeeibhsgtw"},
			{name:"screaming",decoder:"ostpagnepeiearenmvrrapjytrspuspgalcihodtccrohorspsoestoataaebel"},
			{name:"robberies",decoder:"iotpaisepbboarenovrrapjytsnhrsosnmrssostecfthrbnpioeaatyeountyn"},
			{name:"killedher",decoder:"eeltshavesutlmrpfoeiototaukcttriblnpsrwaoetshdgnicehlnhaioiirnh"},
			{name:"zodiac",decoder:"hictedevniumtnrpfoesotpdtzacitukgaigslnhoetsehaoncehegmnesorard"},
			{name:"murderer",decoder:"tispaedepstsarenuvrrahdytsnsisormrtrnorticenhamhpeothgiitiosaka"},
			{name:"murder banjo",decoder:"eaeteenmnslohiobmoogrietujlaeeeydtrdayocteeeeameueewieefnelreeh"},
		    {name:"got followed",decoder:"eeetoiagdwueyohwtaceotehehnarfeittolelmrneeehaimflsemertnesaeea"},
			{name:"last inches",decoder:"eaetudeotnsoiiaoteshjienwhtrbeewaeneyhpvhoeerdshingcheelleesaee"},
			{name:"another vallejo",decoder:"eoevohnzetoeeseudiashaelemdastesltacriaujyeetitbisnleeehoejrbet"},
			{name:"cages of the north",decoder:"eoebieocapeeahhepeseroeutgfheeeleterctsisaeewirthantleeteeiaeen"},
			{name:"toss blind",decoder:"eataoaetldvsetedsahvnulsalegehdkllblaasliaetonnhsieevicbneicsol"},
			{name:"shall cause slaves death",decoder:"?aiaoastl?v?eledsah?nu?s??hg?hdkl?ll?asvs?eton?l?iae?tcb??ic??e"},
			{name:"because school death",decoder:"oughasirt?a?blp?iaaoalkl?iovidd?snetha?zl?s?eeic??celcd?otlcahl"},
			{name:"halloween killing",decoder:"lig??ealdn????ahslo??mkcii??iilounno??eiwxftd?ee?yh?z?wta?lu?nl"},
			{name:"paradice afterlife",decoder:"?eh?iikga?mlrlecr?eugkeb?sa?ed??ettfare?sabtpnilnleec?fi??oe?cm"},
			{name:"at the current time i new the bombs were on the",decoder:"ctamtsmanfettnbrtsiirbgsnteshfasfdeceioh-sscfghnhuwwessefgroffe"},
			{name:"happy radio dud",decoder:"ostpaonepegeorhodoirapjyarupedpdaleusudtccrehkrshdousisanatabel"},
			{name:"nice some sisters white",decoder:"?m???ee?n???????ite?t?????t?hci?hs?t???e???iosa???ss???r??i?ow?"}*/
		), new Array(
			                               //ABDEFGHIJKLMNOPQRSTUVWXYZ56789cdefjklpqrtz!#%()*+/@\^_
			{name:"The correct one",decoder:"wlnesattfsthenifgaoibeouetesaivocdxiaemrrdollnhpeksrny"}/*,
			{name:"experiment398-01",decoder:"tl?eb????w???s??toteare??a??aes?h???ti????l?l???t?r?na"},
			{name:"experiment439-01",decoder:"ke?ddk???n??????i?pnpo?ieo??snt?n????s????e?e????????i"}*/
			
		));

	var commonWords = new Array(
		"the", "name", "of", "very", "to", "through", "and", "just", "a", "form", "in", "sentence", "is", "great", "it",
		"think", "you", "say", "that", "help", "he", "low", "was", "line", "for", "differ", "on", "turn", "are", "cause",
		"with", "much", "as", "mean", "I", "before", "his", "move", "they", "right", "be", "boy", "at", "old", "one",
		"too", "have", "same", "this", "tell", "from", "does", "or", "set", "had", "three", "by", "want", "hot", "air",
		"word", "well", "but", "also", "what", "play", "some", "small", "we", "end", "can", "put", "out", "home", "other",
		"read", "were", "hand", "all", "port", "there", "large", "when", "spell", "up", "add", "use", "even", "your",
		"land", "how", "here", "said", "must", "an", "big", "each", "high", "she", "such", "which", "follow", "do", "act",
		"their", "why", "time", "ask", "if", "men", "will", "change", "way", "went", "about", "light", "many", "kind",
		"then", "off", "them", "need", "write", "house", "would", "picture", "like", "try", "so", "us", "these", "again",
		"her", "animal", "long", "point", "make", "mother", "thing", "world", "see", "near", "him", "build", "two", "self",
		"has", "earth", "look", "father", "more", "head", "day", "stand", "could", "own", "go", "page", "come", "should",
		"did", "country", "number", "found", "sound", "answer", "no", "school", "most", "grow", "people", "study", "my",
		"still", "over", "learn", "know", "plant", "water", "cover", "than", "food", "call", "sun", "first", "four", "who",
		"between", "may", "state", "down", "keep", "side", "eye", "been", "never", "now", "last", "find", "let", "any",
		"thought", "new", "city", "work", "tree", "part", "cross", "take", "farm", "get", "hard", "place", "start", "made",
		"might", "live", "story", "where", "saw", "after", "far", "back", "sea", "little", "draw", "only", "left", "round",
		"late", "man", "run", "year", "dont", "came", "while", "show", "press", "every", "close", "good", "night", "me",
		"real", "give", "life", "our", "few", "under", "north", "open", "ten", "seem", "simple", "together", "several",
		"next", "vowel", "white", "toward", "children", "war", "begin", "lay", "got", "against", "walk", "pattern",
		"example", "slow", "ease", "center", "paper", "love", "group", "person", "always", "money", "music", "serve",
		"those", "appear", "both", "road", "mark", "map", "often", "rain", "letter", "rule", "until", "govern", "mile",
		"pull", "river", "cold", "car", "notice", "feet", "voice", "care", "unit", "second", "power", "book", "town",
		"carry", "fine", "took", "certain", "science", "fly", "eat", "fall", "room", "lead", "friend", "cry", "began",
		"dark", "idea", "machine", "fish", "note", "mountain", "wait", "stop", "plan", "once", "figure", "base", "star",
		"hear", "box", "horse", "noun", "cut", "field", "sure", "rest", "watch", "correct", "color", "able", "face",
		"pound", "wood", "done", "main", "beauty", "enough", "drive", "plain", "stood", "girl", "contain", "usual",
		"front", "young", "teach", "ready", "week", "above", "final", "ever", "gave", "red", "green", "list", "oh",
		"though", "quick", "feel", "develop", "talk", "ocean", "bird", "warm", "soon", "free", "body", "minute",
		"dog", "strong", "family", "special", "direct", "mind", "pose", "behind", "leave", "clear", "song", "tail",
		"measure", "produce", "door", "fact", "product", "street", "black", "inch", "short", "multiply", "numeral",
		"nothing", "class", "course", "wind", "stay", "question", "wheel", "happen", "full", "complete", "force",
		"ship", "blue", "area", "object", "half", "decide", "rock", "surface", "order", "deep", "fire", "moon",
		"south", "island", "problem", "foot", "piece", "system", "told", "busy", "knew", "test", "pass", "record",
		"since", "boat", "top", "common", "whole", "gold", "king", "possible", "space", "plane", "heard", "stead",
		"best", "dry", "hour", "wonder", "better", "laugh", "true", "thousand", "during", "ago", "hundred", "ran",
		"five", "check", "remember", "game", "step", "shape", "early", "equate", "hold", "hot", "west", "miss", "ground",
		"brought", "interest", "heat", "reach", "snow", "fast", "tire", "verb", "bring", "sing", "yes", "listen",
		"distant", "six", "fill", "table", "east", "travel", "paint", "less", "language", "morning", "among", "grand",
		"cat", "ball", "century", "yet", "consider", "wave", "type", "drop", "law", "heart", "bit", "am", "coast",
		"present", "copy", "heavy", "phrase", "dance", "silent", "engine", "tall", "position", "sand", "arm", "soil",
		"wide", "roll", "sail", "temperature", "material", "finger", "size", "industry", "vary", "value", "settle",
		"fight", "speak", "lie", "weight", "beat", "general", "excite", "ice", "natural", "matter", "view", "circle",
		"sense", "pair", "ear", "include", "else", "divide", "quite", "syllable", "broke", "felt", "case", "perhaps",
		"middle", "pick", "kill", "sudden", "son", "count", "lake", "square", "moment", "reason", "scale", "length",
		"loud", "represent", "spring", "art", "observe", "subject", "child", "region", "straight", "energy", "consonant",
		"hunt", "nation", "probable", "dictionary", "bed", "milk", "brother", "speed", "egg", "method", "ride", "organ",
		"cell", "pay", "believe", "age", "fraction", "section", "forest", "dress", "sit", "cloud", "race", "surprise",
		"window", "quiet", "store", "stone", "summer", "tiny", "train", "climb", "sleep", "cool", "prove", "design",
		"lone", "poor", "leg", "lot", "exercise", "experiment", "wall", "bottom", "catch", "key", "mount", "iron", "wish",
		"single", "sky", "stick", "board", "flat", "joy", "twenty", "winter", "skin", "sat", "smile", "written",
		"crease", "wild", "hole", "instrument", "trade", "kept", "melody", "glass", "trip", "grass", "office", "cow",
		"receive", "job", "row", "edge", "mouth", "sign", "exact", "visit", "symbol", "past", "die", "soft", "least",
		"fun", "trouble", "bright", "shout", "gas", "except", "weather", "wrote", "month", "seed", "million", "tone",
		"bear", "join", "finish", "suggest", "happy", "clean", "hope", "break", "flower", "lady", "clothe", "yard",
		"strange", "rise", "gone", "bad", "jump", "blow", "baby", "oil", "eight", "blood", "village", "touch", "meet",
		"grew", "root", "cent", "buy", "mix", "raise", "team", "solve", "wire", "metal", "cost", "whether", "lost",
		"push", "brown", "seven", "wear", "paragraph", "garden", "third", "equal", "shall", "sent", "held", "choose",
		"hair", "fell", "describe", "fit", "cook", "flow", "floor", "fair", "either", "bank", "result", "collect",
		"burn", "save", "hill", "control", "safe", "decimal", "gentle", "truck", "woman", "noise", "captain", "level",
		"practice", "chance", "separate", "gather", "difficult", "shop", "doctor", "stretch", "please", "throw",
		"protect", "shine", "noon", "property", "whose", "column", "locate", "molecule", "ring", "select", "character",
		"wrong", "insect", "gray", "caught", "repeat", "period", "require", "indicate", "broad", "radio", "prepare",
		"spoke", "salt", "atom", "nose", "human", "plural", "history", "anger", "effect", "claim", "electric",
		"continent", "expect", "oxygen", "crop", "sugar", "modern", "death", "element", "pretty", "hit", "skill",
		"student", "women", "corner", "season", "party", "solution", "supply", "magnet", "bone", "silver", "rail",
		"thank", "imagine", "branch", "provide", "match", "agree", "suffix", "thus", "especially", "capital", "fig",
		"wont", "afraid", "chair", "huge", "danger", "sister", "fruit", "steel", "rich", "discuss", "thick", "forward",
		"soldier", "similar", "process", "guide", "operate", "experience", "guess", "score", "necessary", "apple",
		"sharp", "bought", "wing", "led", "create", "pitch", "neighbor", "coat", "wash", "mass", "bat", "card", "rather",
		"band", "crowd", "rope", "corn", "slip", "compare", "win", "poem", "dream", "string", "evening", "bell",
		"condition", "depend", "feed", "meat", "tool", "rub", "total", "tube", "basic", "famous", "smell", "dollar",
		"valley", "stream", "nor", "fear", "double", "sight", "seat", "thin", "arrive", "triangle", "master", "planet",
		"track", "hurry", "parent", "chief", "shore", "colony", "division", "clock", "sheet", "mine", "substance",
		"tie", "favor", "enter", "connect", "major", "post", "fresh", "spend", "search", "chord", "send", "fat",
		"yellow", "glad", "gun", "original", "allow", "share", "print", "station", "dead", "dad", "spot", "bread",
		"desert", "charge", "suit", "proper", "current", "bar", "lift", "offer", "rose", "segment", "continue",
		"slave", "block", "duck", "chart", "instant", "hat", "market", "sell", "degree", "success", "populate",
		"company", "chick", "subtract", "dear", "event", "enemy", "particular", "reply", "deal", "drink", "swim",
		"occur", "term", "support", "opposite", "speech", "wife", "nature", "shoe", "range", "shoulder", "steam",
		"spread", "motion", "arrange", "path", "camp", "liquid", "invent", "log", "cotton", "meant", "born",
		"quotient", "determine", "teeth", "quart", "shell", "nine", "neck"
	);


	var zodiacWords = new Array( "a","abnomily","abot","about","accidents","across","acting","activity","ad","adapted","additional","address","adhesive","adjusted","ads","advertisement","afraid","after","afterlife","afternoon","again","aim","airplane","alive","all","alley","alleys","allways","alone","along","also","always","am","ammo","ammonium","an","anamal","and","anger","angry","anilating",
"ann","announce","anonymously","another","answer","ant","any","anyone","anyway","apart","aprox","are","area","aredead","army","arose","around","arthur","as","ask","asked","asking","ass","asses","at","attention","attn","aug","autographs","averly","avery","awake","away","awfully","baby","babysits","back","backwards","bad","badlands","bag","bags","ball","ban","banjo",
"barrel","basement","bat","bates","battered","battery","bay","bayonet","be","beam","beautiful","because","become","beef","been","before","began","behind","being","belli","best",
"better","betty","beware","big","billiard","billowy","black","blank","blanks","blast","block","blocks","blond","blood","bloodsoak","bluber","blue","bluff","blurb","bomb","boo",
"booboos","book","booth","boots","bore","bought","boughten","bouncing","boy","brand","breast","broke","brown","brunett","brush","bryan","bulb","bullet","bullshit","burn","burned","burning","bury","bus","buss","busses","bussy","but","buton","butons","buttons","by","bye","cab","caen","cages","calif","call","called","calvin","came","can","cancel","cannot","capable","car","cardboard","cars","catch","caught","cecelia","ceiling","cell","cement","cene","cent","center","centuries","cerous","change","chapter","check","checks","cheer","cherry","chief","children","chocked","christmas","christmass","chronicle","cicles","cid","cipher","circle","citizen","city","clean","clever","clews","clock","closeing","clowns","coated","coats","code","collect","collecting","columbus","column","come","comidy","commic","committ","complet","complete","complied","comply","concern","concerning","concerns","conditions","confession","conscience","considerably","consists","consternation","consternt","contains","continually","continues","contrary","control","controol","cop","copper","cops","corner","could","count","country","county","coupled","coupple","coupples","cover","coverage","crack","cracked","crackproof","credit","crime","crooked","cruse","cruzeing","cry","cues","cut","cutting","cyipher","daily","damn","dangertue","darck","dark","darkened","darlene","date","dates","daughters","david","dead","dear","death","deep","delicious","deplorable","deposit","des","descise","describe","described","description","deserve","detail","details","developer","diablo","did","die","died","different","dificult","dig","directed","disappeared","disorder","distributor","do","doesnt","dogs","doing","dont","doo","doomed","door","dot","double","down","dozen","dragon","draining","draw","dress","dressed","drew","dripping","driven","driver","drivers","drove","drownding","dud","dump","dungen","during","each","east","easy","eat","eats","ebeorietemethhpitithe","echo","editor","efect","efective","either","electric","elimination","elizabeth","else","elses","end","ended","engine","enjoy","enough","enterprise","entirle","envelope","epasode","etc","even","evening","events","ever","evere","every","everyone","everything","evidenced","examiner","except","exorcist","experence","expression","extreamly","face","fact","facts","fake","fall","faraday","fart","fat","featuring","fed","feel","feet","fellows","felt","female","ferrin","fertilizer","few","fiddle","figgure","filling","find","finding","fingerprints","fingertip","fingertips","finish","finished","fire","fired","fireing","firm","first","flabby","flash","flicting","floor","foe","followed","following","for","forest","forrest","fought","found","four","fran","francisco","friend","from","front","frunt","fry","full","fun","funny","future","gal","game","gave","geary","get","getting","ghia","gilbert","girl","girls","give","given","gives","glorification","glory","glove","gloves","go","going","good","goof","gorged","got","grabbed","grave","gravel","great","groups","grown","guards","guess","gummed","gun","guy","had","half","hand","hands","handwritten","hang","happen","happy","hard","hartnell","has","hate","have","having","he","head","headquarters","heads","hear","heat","heights","hell","hellhole","help","her","herald","herb","here","herman","hey","high","hill","hills","him","himself","his","hit","hold","holding","hole","holes","holly","home","homicide","hope","horizon","horrible","hose","how","howers","hummerist","hung","i","identity","idiout","if","ignored","im","implied","implore","impriest","in","inches","initials","insane","inside","insist","instead","interesting","inthusiastic","into","irritating","is","isdead","isnt","it","its","ive","jensen","joaquin","job","johns","jolly","judicial","july","just","justifiable","kathleen","keep","kharmann","kicked","kiddies","kids","kill","killed","killedher","killedhim","killer","killing","killings","kind","kissed","kit","knee","knife","know","lack","lady","lake","lamb","last","laugh","laughs","lay","leaf","leaped","leave","leaving","left","legs","let","letter","liberation","library","lies","life","lift","light","like","lips","list","listen","lit","little","living","lonely","long","longer","look","looking","loose","lot","lou","luger","lyeing","machine","made","mageau","mail","mailed","make","making","man","manner","manpower","map","maple","marco","marked","market","mask","mason","massive","masterpiece","material","matte","matter","may","maybe","me","meaning","meannie","meannies","meanwhile","mech","melvin","melvins","men","mery","messy","michael","middle","might","mikado","mildly","mile","miles","min","mind","mine","minutes","mirror","miss","missed","mission","modesto","moment","money","months","more","morning","most","motor","motorcicles","mount","mouth","move","movie","mr","much","murder","murderer","murders","must","my","myself","nails","name","nasty","near","neck","need","needling","needs","negro","neither","never","nevermind","new","news","newspaper","next","nice","night","nights","nine","nineth","nitrate","no","noise","none","norse","north","not","note","notice","now","noze","nucences","oct","of","off","offenders","offered","offs","oh","oil","old","on","once","one","ones","only","open","openly","or","order","orginast","origionaly","other","others","out","oute","outfits","over","own","pace","page","pages","pain","pane","panes","pants","paper","paradice","park","parked","parking","parkway","part","parts","passed","patterned","paul","pay","peek","peekaboo","peeled","pen","pencel","people","pepermint","permits","persons","pestulentual","phantom","phomphit","phone","photoelectric","phraises","piano","pick","pictures","piece","pig","pigs","pine","pines","place","placed","platt","play","players","please","pleass","plunged","point","pointblank","police","polish","poor","posibly","positivily","pow","power","presidio","press","price","print","prior","private","promiced","proof","properly","prove","provences","prowl","ps","psychological","public","published","pulled","punish","punished","put","questions","quietly","quite","race","raceing","races","radians","rage","rain","rampage","rather","re","reach","read","ready","really","reason","reborn","recent","red","refer","reflector","remain","renault","report","reports","requires","rest","ride","rife","right","rile","ring","riverside","road","roat","robberies","rocks","roger","rolled","rope","routine","rub","rubber","rubed","run","runnig","running","rush","safe","said","salt","same","san","sat","saterical","save","saw","say","saying","schedule","school","scream","screaming","searched","searching","seat","see","seeing","seen","self","selling","selves","sensibilities","sent","separation","sept","seranader","serious","set","seven","sfpd","shabbly","shake","shaking","shall","shapely","she","shepard","shirt","shit","shoe","shoes","shook","shoot","shot","shots","should","show","shrink","shure","shut","sick","side","sides","sight","sights","signed","silowets","since","singurly","sirs","sisters","sitting","skin","sla","slaughter","slaves","slay","sloi","slower","slowly","small","smarter","snd","so","society","som","some","someday","someone","something","sorry","sound","south","speaking","spell","spilling","splinters","spoiling","spot","spray","springs","spurting","square","squealing","squirm","squirmed","srounded","st","stab","stabbed","stained","stalking","stamps","start","starting","state","stated","station","stine","stop","store","stored","stove","strange","stray","street","streets","strike","struggle","stumbling","stupid","such","suggest","suicides","sullivan","summer","sun","super","superior","supply","suspicious","swamped","switch","symbionese","sympathy","system","tag","take","talk","talked","tape","taped","targets","task","taste","taxi","teenagers","tell","telling","ten","tenth","teritory","tests","thae","than","thank","thashing","that","thats","the","their","them","then","there","theres","these","they","theyd","thing","thingmebob","things","think","thinking","third","thirteenth","this","thos","those","though","three","thrilling","throat","through","thumbs","thus","tie","till","time","times","tip","tire","tired","tires","tissues","titwillo","to","told","tone","too","tools","top","torture","toschi","town","trace","transparent","trees","trigger","trucks","truley","truly","try","trying","tubes","tut","twich","twiched","twisted","two","type","uncertain","uncompromising","under","underground","unhappy","union","unnoticible","unspoiling","untill","unwilling","up","upon","use","used","useing","vallejo","ventalate","very","victim","victims","victom","violence","violently","volkswagen","wachamacallit","wait","waited","waiting","walking","walks","wall","wallet","wandering","want","warm","warning","was","washington","watch","water","wave","waveing","way","we","wear","wearing","week","well","went","were","west","western","what","whatshisname","when","whence","where","whether","which","while","white","who","whole","whom","whrite","why","wild","will","willing","willingly","window","wipe","wiped","wipeing","wire","wise","wish","wishes","with","wives","woeman","wonder","wondering","wont","word","work","would","wouldnt","wound","write","writing","xmass","xxx","year","years","yes","yet","you","youll","young","your","yours","zodiac");
  var wordHash = new Array();



	var decoder = new Array();

	var getKey = false;
	var getKeyFor;
	var getKeyId;

	var newLoad = false;          
	
	var sequences;
	                  
	/** highlight the given sequence #*/
	function hSeq(seq) {
		/** unhighlight all others */
		for (var i=0; i<sequences.length; i++)
			hmatch(sequences[i], false);
		/** highlight selected sequence */
		hmatch(sequences[seq], true);	
	}

	function resetCipher() {
		cipher = new Array();
		for (var i=0; i<ciphers.length; i++) {
			var c = ciphers[i];
			cipher[i] = new Array();
			var j = 0;
			while (j<c.length) {
				cipher[i][cipher[i].length] = c.substring(j,j+WIDTH);
				j+=WIDTH;
			}
		}
		cipherLength = new Array(cipher.length);

		cipherReset = new Array();
		for (i=0; i<cipher.length; i++) {
			cipherReset[i] = cipher[i].slice();
		}
	}
	
	function d(name) {
		return document.getElementById(name);
	}
	
	function init() {
		resetCipher();
		preload();
		cipherLength[which] = 0;
		for (var i=0; i<cipher[which].length; i++) {
			for (var j=0; j<cipher[which][i].length; j++) {
				decoder[cipher[which][i].substring(j,j+1)] = "";
				cipherLength[which]++;
			}
		}
		for (var i=0; i<commonWords.length; i++)
			wordHash[commonWords[i]] = true;
		for (var i=0; i<zodiacWords.length; i++)
			wordHash[zodiacWords[i]] = true;
		render();

//		computeStatistics();
//		setTimeout("render()", 250);
//		renderInteresting();
	}

	function renderGridAll() {
		var d = ""; 
				var ctable = document.getElementById("cipher");
				if (ctable.childNodes[0]) ctable.removeChild(ctable.childNodes[0]);

				var newChild = document.createElement("table");
				newChild.className = "cipher";
				var tbody = document.createElement("tbody");
				var cell; var trow;

/*
				trow = document.createElement("tr");
								for (var col=0; col<=cipher[which][0].length; col++) {
									cell = document.createElement("td");
									if (col>0) cell.innerHTML = "<span class='index'>" + (col-1) + "</span>";
									trow.appendChild(cell);
								}
								tbody.appendChild(trow);
*/				
				
				for (var row=0; row<cipher[which].length; row++) {
					trow = document.createElement("tr");
					trow.setAttribute("style","height: 20px");
					
					/*
					cell = document.createElement("td");
					cell.className = "index";
					//cell.innerHTML = "<span class='index'>" + (row*cipher[which][row].length) + "</span>";
					cell.innerHTML = "<span class='index'>" + row + "</span>";
					trow.appendChild(cell);*/
					for (var col=0; col<cipher[which][row].length; col++) {
						letter = cipher[which][row].substring(col,col+1);
						id = row + "_" + col;
						d += (getDecoded(letter) == "" ? " " : getDecoded(letter));
				    cell = document.createElement( "td" );
						cell.setAttribute("id",id);
						

//						if (isPrime1(row*17+col+1)) cell.setAttribute("class","prime");
						if ((row*WIDTH+col+1) % 2 == 0) cell.setAttribute("class","even"); 
						else cell.setAttribute("class","odd"); 
						
//						cell.onmouseover = new Function("h('" + id + "')");
//						cell.onmouseout = new Function("u('" + id + "')");
//			 		  cell.setAttribute("onmouseout","u('" + id + "')");
						cell.setAttribute("onclick","tog(event, " + row + "," + col + ")");
						cell.setAttribute("title","row " + row + " col " + col + " pos " + (cipher[which][0].length * row + col) + " symbol " + (cipher[which][row][col]));
//						cell.setAttribute("ondblclick","dbl(" + row + "," + col + ")");
//						cell.onclick = function() { var temp=new Function("g('" + letter + "','" + id + "')"); temp(); };
//						cell.onclick = new Function("g(\"" + (letter == "\\" ? "\\\\" : letter) + "\",\"" + id + "\")");
//						eval("cell.onClick = g('" + letter + "','" + id + "')");
//						cell.className = getName(letter, false);
						if (getName(letter, false)=="blank") {
//							cell.style.paddingLeft = "1px";
//						 	cell.style.paddingRight = "1px";
					  }
						else {
//							cell.style.paddingLeft = "10px";
//						 	cell.style.paddingRight = "10px";
						}
						
		//				cell.setAttribute("style",(getName(letter, false) == "blank" ? "padding-left: 1px; padding-right: 1px;" : ""));
//						cell.innerHTML = "<center>"+getDecoded(letter) + "</center>&nbsp;" 
						cell.innerHTML = getImg(letter, row);
						trow.appendChild(cell);
					  //html += "<td id=\"" + id + "\" onmouseover=\"h('" + id + "')\" onmouseout=\"u('" + id + "')\" onclick=\"g('" + letter + "','" + id + "')\" class=\"" + getName(letter, false) + "\" style=\"" + (getName(letter, false) == "blank" ? "padding-left: 1px; padding-right: 1px;" : "") + "\">" + getDecoded(letter) + "</td>";
						//html += "</td>";
					}


					tbody.appendChild(trow);
					//html += "</tr>";
				}
				thead = document.createElement("thead");
				tfoot = document.createElement("tfoot");
				newChild.appendChild(thead);
				newChild.appendChild(tfoot);
				newChild.appendChild(tbody);
				ctable.appendChild(newChild);
				return d;
	}

	function renderCellsFor(symbol, plaintext) {
		var cell;
		var d = "";
		for (var row=0; row<cipher[which].length; row++) {
			for (var col=0; col<cipher[which][row].length; col++) {
				letter = cipher[which][row].substring(col,col+1);
				d += (getDecoded(letter) == "" ? " " : getDecoded(letter));
				if (letter == symbol) {
					id = row + "_" + col;
				 	cell = document.getElementById(id);
					//			cell.setAttribute("onmouseover","h('" + id + "')");
					//			cell.setAttribute("onmouseout","u('" + id + "')");
//								cell.setAttribute("onclick","g('" + letter + "','" + id + "')");
					//			cell.onClick = "g('" + letter + "','" + id + "')";
								cell.className = getName(letter, false);
								if (getName(letter, false)=="blank") {
//									cell.style.paddingLeft = "1px";
//								 	cell.style.paddingRight = "1px";
							  }
								else {
//									cell.style.paddingLeft = "10px";
//								 	cell.style.paddingRight = "10px";
								}
//					if (cell.childNodes[0]) cell.removeChild(cell.childNodes[0]);
//					cell.appendChild(document.createTextNode(getDecoded(letter)));
					cell.innerHTML = getDecoded(letter);
				}
			}
		}
		document.getElementById("s"+getName(symbol,true)).className = getName(letter, false);
		document.getElementById("s"+getName(symbol,true)).innerHTML = plaintext;
		renderCipherInfo(d);
	}
	
	function renderCipherInfo(d) {
		var decoded = "<p><u>Decoded ciphertext</u>: <b>";
		var plaintext = "";
		
		if (doStats) {
			frequencies = new Array();
			for (var i=0; i<d.length; i++) {
				decoded += d.charAt(i).toLowerCase() + " ";
				plaintext += d.charAt(i);
				if (!frequencies[d.charAt(i)]) frequencies[""+d.charAt(i)] = 0;
				frequencies[""+d.charAt(i)]++;
			}
			decoded += "</b></p>";

			var keys = new Array();
			for (x in frequencies) {
				keys[keys.length] = x;
			}
			var frequenciesSortedKeys = null;
			frequenciesSortedKeys = sortByValue(keys, frequencies);
			decoded += "<p id=\"stats2\" style=\"display:" + document.getElementById("stats").style.display + "\"><u>Letter frequencies</u>: ";
		
			decoded += "<table><tr valign=\"top\"><td style=\"border-right: thin solid #999\"><center><b>PLAINTEXT:</b></center>";
			decoded += "<table class=\"lettertable\" style=\"padding: 5px 5px 5px 5px\">";
			var max = 0; var scale;
			for (x in frequenciesSortedKeys) {
				letter = frequenciesSortedKeys[x];
				if (letter != " ") {
					if (max == 0) {
						max = frequencies[frequenciesSortedKeys[x]]; 
					}
					scale = Math.round(100*frequencies[frequenciesSortedKeys[x]]/max);
					decoded += "<tr>";
					decoded += "<td class=\"letter\">" + letter + "</td>";
					//html += (getDecoded(letter) ? "<div class=\"letter\">" + getDecoded(letter) : "<img src=\"alphabet/" + getName(letter, false) + ".jpg\" style=\"display:inline\"/>") + 
					decoded += "<td nowrap=\"yes\" class=\"normal\">" + frequencies[frequenciesSortedKeys[x]] + " (" + Math.round(100*(frequencies[frequenciesSortedKeys[x]]/cipherLength[which])) + "%)</td>";
					decoded += "<td><img style=\"border-right: thick solid #009; border-left: 2px solid #ccf\" \" src=\"dot1.gif\" height=\"20\" width=\"" + scale + "\"/></td>";
				  decoded += "</tr>";
				}
			}
			decoded += "</table></td><td><center><b>EXPECTED:</b></center>";
			decoded += "<table class=\"lettertable\" style=\"padding: 5px 5px 5px 5px\">";
		
			keys = new Array();
			for (x in letterFrequencies) {
				keys[keys.length] = x;
			}
			var letterFreqSortedKeys = null;
			letterFreqSortedKeys = sortByValue(keys, letterFrequencies);
			max = 0;
			for (x in letterFreqSortedKeys) {
				letter = letterFreqSortedKeys[x];
				if (letter != " ") {
					if (max == 0) {
						max = letterFrequencies[letterFreqSortedKeys[x]]; 
					}
					scale = Math.round(100*letterFrequencies[letterFreqSortedKeys[x]]/max);
					decoded += "<tr>";
					decoded += "<td class=\"letter\">" + letter + "</td>";
					//html += (getDecoded(letter) ? "<div class=\"letter\">" + getDecoded(letter) : "<img src=\"alphabet/" + getName(letter, false) + ".jpg\" style=\"display:inline\"/>") + 
					decoded += "<td nowrap=\"yes\" class=\"normal\">" + Math.round(letterFrequencies[letterFreqSortedKeys[x]]*cipherLength[which]) + " (" + Math.round(100*(letterFrequencies[letterFreqSortedKeys[x]])) + "%)</td>";
					decoded += "<td><img style=\"border-right: thick solid #009; border-left: 2px solid #ccf\" \" src=\"dot1.gif\" height=\"20\" width=\"" + scale + "\"/></td>";
				  decoded += "</tr>";
				}
			}
			decoded += "</table><a href=\"http://en.wikipedia.org/wiki/Letter_frequencies#Relative_frequencies_of_letters_in_the_English_language\">(source)</a>";
		
			decoded += "</td></tr></table>";
		}
		//for (x in frequenciesSortedKeys) if (frequenciesSortedKeys[x] != " ") decoded += "(" + frequenciesSortedKeys[x] + ", " + frequencies[frequenciesSortedKeys[x]] + ") ";


		var found = new Array();
		for (var i=1; i<20; i++) {
			for (var j=0; j<d.length-i-1; j++) {
				if (wordHash[d.substring(j,j+i).toLowerCase()]) {
					found[d.substring(j,j+i).toLowerCase()] = true;
				}
			}
		}
		var words = "";
		for (var x in found) {
			words += x + " | ";
		}
		if (doStats) {
			html = "<u>Symbol frequencies</u>: <table class=\"lettertable\">";
		
			var max = 0;
			for (x in statsSortedKeys[which]) {
				letter = statsSortedKeys[which][x];
				if (max == 0) {
					max = stats[which][statsSortedKeys[which][x]]; 
				}
				scale = Math.round(60*stats[which][statsSortedKeys[which][x]]/max);
			
			
				html += "<tr valign=\"middle\">";
				html += "<td align=\"right\"><img style=\"border-left: thick solid #009; border-right: 2px solid #ccf\" src=\"dot1.gif\" height=\"20\" width=\"" + scale + "\"/></td>";
				html += "<td align=\"middle\" id=\"s" + getName(letter, true) + "\" onclick=\"g('" + letter + "','bogus')\" class=\"" + getName(letter, false) + "\" style=\"" + (getName(letter, false) == "blank" ? "padding-left: 1px; padding-right: 1px;" : "") + "\"><div class=\"letter\">" + getDecoded(letter) + "</div></td>";
				//html += (getDecoded(letter) ? "<div class=\"letter\">" + getDecoded(letter) : "<img src=\"alphabet/" + getName(letter, false) + ".jpg\" style=\"display:inline\"/>") + 
				html += "<td nowrap=\"yes\" class=\"normal\">" + stats[which][statsSortedKeys[which][x]] + " (" + Math.round(100*(stats[which][statsSortedKeys[which][x]]/cipherLength[which])) + "%)</td>";
				html += "</tr>";
			}
			html += "</table>";
			document.getElementById("stats").innerHTML = html;
		}
		
		
	}
	
	/* break cipher into rows */
	function split(c) {
		var a = new Array();
		var w = cipher[which][0].length;
		for (var i=0; i<c.length/w; i++) {
			a[i] = c.substring(i*w, i*w+w);
		}
		return a;
	}
	
	function render() {
		//var html = "<table border=\"1\" class=\"cipher\">";
		var html = "";
		var id;
		var d1 = new Date();
		var d = renderGridAll();
		renderCipherInfo(d);
		HEIGHT = cipher[which].length;
	}
	
	function renderInteresting() {
		var html;
		
		html = "Interesting decoders: ";
		
		for (var i=0; i<interestingHash[which].length; i++) {
			html += "<a href=\"javascript:getInteresting(" + i + ")\">" + interestingHash[which][i].name + "</a> | ";
		}
		
		var elem = document.getElementById("interesting");
		if (elem)
			elem.innerHTML = html;
		
	}

	function reset() {
		init();
	}

	function h(id) {
		var elem = document.getElementById(id);
		elem.style.border = "thin solid gray";
	}
	function h2(id) {
		var elem = document.getElementById(id);
		elem.style.border = "thick solid red";
	}
	
	function hall(letter) {
		for (var j=0; j<cipher[which].length; j++)
			for (var i=0; i<cipher[which][j].length; i++)
				if (cipher[which][j].charAt(i) == letter) h2(j+"_"+i);
	}
	
	function u(id) {
		var elem = document.getElementById(id);
		elem.style.border = "thin solid white";
	}

	function getDecoded(letter) {
		if (decoder[letter]=="") return "";
		if (!decoder[letter]) return "";
		return (decoder[letter]);
	}

	function getDecoder() {
		var d = "";
		for (i=0; i<alphabet[which].length; i++) d+= (!decoder[alphabet[which][i]] || decoder[alphabet[which][i]] == "" ? "?" : decoder[alphabet[which][i]]) + " ";
		return d;
	}


	function g(letter, id) {
		getKey = true;
		getKeyFor = letter;
		document.getElementById("key").innerHTML = "Type the letter you want for <img src=\"alphabet/" + getName(letter, true) + ".jpg\">.  " + 
			"Or, <a href=\"#\" onclick=\"clearletter()\">[reset this letter]</a>.";

		var elem = document.getElementById(id);
		/*if (elem) {
			elem.style.border = "thin solid #0f0";
	  }*/
		getKeyId = id;
	}

     function chr(c) {
     var h = c . toString (16);
     h = unescape ('%'+h);
     return h;
     }

	function getName(letter, ignoreDecoder) {
		if (!ignoreDecoder && decoder[letter] != "") return "blank";
		switch (letter) {
			case "1" : return("n1");
			case "2" : return("n2");
			case "3" : return("n3");
			case "4" : return("n4");
			case "5" : return("n5");
			case "6" : return("n6");
			case "7" : return("n7");
			case "8" : return("n8");
			case "9" : return("n9");
			case "^" : return("caret");
			case "#" : return("sq");
			case "_" : return("sqe");
			case "@" : return("sqd");
			case "*" : return("sql");
			case "%" : return("sqr");
			case "(" : return("theta");
			case ")" : return("phi");
			case "z" : return("zodiac");
			case "t" : return("perp");
			case "&" : return("pf");
			case ";" : return("idl");
			case ":" : return("idr");
			case ">" : return("gt");
			case "." : return("dot");
			case "<" : return("lt");
			case "+" : return("plus");
			case "/" : return("slash");
			case "\\" : return("backslash");
			case "-" : return("dash");
			case "!" : return("funnyi");
			case "=" : return("sidek");
			case "|" : return("bar");

/*			case "a" : return("blank");
			case "g" : return("blank");
			case "h" : return("blank");
			case "i" : return("blank");
			case "m" : return("blank");
			case "n" : return("blank");
			case "o" : return("blank");
			case "s" : return("blank");
			case "t" : return("blank");
			case "u" : return("blank");
			case "v" : return("blank");
			case "w" : return("blank");
			case "x" : return("blank");
			case "z" : return("blank");*/

            case "b" : return("bb");
            case "c" : return("bc");
            case "d" : return("bd");
            case "e" : return("be");
            case "f" : return("bf");
            case "j" : return("bj");
            case "k" : return("bk");
            case "l" : return("bl");
            case "p" : return("bp");
            case "q" : return("bq");
            case "r" : return("br");
            case "y" : return("by");

			case "A" : return("a");
			case "B" : return("b");
			case "C" : return("c");
			case "D" : return("d");
			case "E" : return("e");
			case "F" : return("f");
			case "G" : return("g");
			case "H" : return("h");
			case "I" : return("i");
			case "J" : return("j");
			case "K" : return("k");
			case "L" : return("l");
			case "M" : return("m");
			case "N" : return("n");
			case "O" : return("o");
			case "P" : return("p");
			case "Q" : return("q");
			case "R" : return("r");
			case "S" : return("s");
			case "T" : return("t");
			case "U" : return("u");
			case "V" : return("v");
			case "W" : return("w");
			case "X" : return("x");
			case "Y" : return("y");
			case "Z" : return("z"); 

			// symbols unique to 13- and 32-character ciphers
			case "?" : return("omega");    
			case "0" : return("taurus");
			case "[" : return("t2");

			case " " : return("blank");
			//case "$" : return("tao");
			default : return("unknown");
		}
		//if (letter == letter.toLowerCase()) return "b" + letter.toLowerCase();
		//else return letter.toLowerCase();
	}

	function keypress(event) {
		var key = window.event ? event.keyCode : event.which;
		if (getKey) {
      if (key < 32 || key > 126)
				letter = "";
			else
				letter = chr(key).toUpperCase();

			decoder[getKeyFor] = letter;

			getKey = false;
			renderCellsFor(getKeyFor, letter);
			getKeyFor = null;
			document.getElementById("key").innerHTML = "";
			return false;
		}
	}
	
	function clearletter() {
		decoder[getKeyFor] = ""; getKey = false;
		renderCellsFor(getKeyFor, "");
		getKeyFor = null;
		document.getElementById("key").innerHTML = "";
	}

	function randomize() {
		for (var i=0; i<cipher[which].length; i++) {
			for (var j=0; j<cipher[which][i].length; j++) {
				decoder[cipher[which][i].substring(j,j+1)] = chr(Math.floor(Math.random()*26+65));
			}
		}
		render();
	}

	function getInteresting(w) {
		for (var i=0; i<interestingHash[which][w].decoder.length; i++) {
			letter = interestingHash[which][w].decoder.substring(i,i+1).toUpperCase();
			if (letter != "?") decoder[alphabet[which].substring(i,i+1)] = letter;
			else decoder[alphabet[which].substring(i,i+1)] = "";
		}
		render();
	}
	
	function switchCipher(w) {
		which = w;
		init();
	}
	
	/* highlight all occurrences of the given cipher letters */
	function highlightLetters(letters) {
		unhighlightAll();
		for (var row=0; row<cipher[which].length; row++) {
			for (var col=0; col<cipher[which][row].length; col++) {
				id = row + "_" + col;
				letter = cipher[which][row].substring(col,col+1);
				for (var i=0; i<letters.length; i++) {
					if (letter == letters.substring(i,i+1)) {
						h(id);
						break;
					}
				}
			}
		}
	}
	
	/* clear all letter highlighting */
	function unhighlightAll() {
		for (var row=0; row<cipher[which].length; row++) {
			for (var col=0; col<cipher[which][row].length; col++) {
				u(row+"_"+col);
			}
		}
	}
	
	/* compute frequency statistics */
	function computeStatistics() {
		if (doStats) {
			stats[0] = new Array();
			stats[1] = new Array();
			for (var i=0; i<cipher[which].length; i++) {
				for (var j=0; j<cipher[which][i].length; j++) {
					c = cipher[which][i].charAt(j);
					if (stats[which][""+c] == null) stats[which][""+c] = 0;
					stats[which][""+c]++;
				}
			}
		
			var keys = new Array();
			for (x in stats[which]) {
				keys[keys.length] = x;
			}
			statsSortedKeys[which] = sortByValue(keys, stats[which]);
		}
		
	}
	
	function sortByValue(keyArray, valueMap) {
		return keyArray.sort(function(a,b){return valueMap[a]-valueMap[b];}).reverse();
	}
	
	function toggleStats() {
		var elem = document.getElementById("stats").style;
		if (elem.display == "none") {
			elem.display = ""; 
		}
		else {
			elem.display = "none";
		}
		elem = document.getElementById("stats2").style;
		if (elem.display == "none") {
			elem.display = ""; 
		}
		else {
			elem.display = "none";
		}
	}
	
	function flipH(c) {
		var newLine;
		for (i=0; i<c.length; i++) {
			newLine = "";
			for (j=c[i].length-1; j>=0; j--) {
				newLine += c[i].charAt(j);
			}
			c[i] = newLine;
		}
		return c;
	}
	
	function flipV(c) {
		var newArray = new Array();
		for (i=c.length-1; i>=0; i--) {
			newArray[c.length-1 - i] = c[i];
		}
		c = newArray;
		return c;
	}

	function rotateCW(c) {
		var newArray = new Array();
		var newLine;
		for (i=0; i<c[0].length; i++) {
			newLine = "";
			for (j=c.length-1; j>=0; j--) {
				newLine += c[j].charAt(i);
			}
			newArray[newArray.length] = newLine;
		}
		c = newArray;
		return c;
	}
	
	function rotateCCW(c) {
		var newArray = new Array();
		var newLine;
		for (i=c[0].length-1; i>=0; i--) {
			newLine = "";
			for (j=0; j<c.length; j++) {
				newLine += c[j].charAt(i);
			}
			newArray[newArray.length] = newLine;
		}
		c = newArray;
		return c;
	}                           
	
	/* for each letter, map it to list of positions of all its appearances in the cipher. */
	function makeIndex() {
//		var s = "";
		var index = {};
		for (var row=0; row<cipher[which].length; row++) {
			for (var col=0; col<cipher[which][row].length; col++) {
				var key = cipher[which][row].charAt(col);
//				s += "("+row+","+col+","+key+") ";
				if (index[key]) {
					index[key][index[key].length] = [row, col];
				} else {
					index[key] = [[row, col]];
				}
			}
		}
//    	alert(s);

		return index;
	}

	/** find all repeated sequences */
	function repeatsfind() {repeats(cipher[which])}
	
	function makeFrom(cipherblock) {
		var a = []; var s = "";
		for (var row=0; row<cipherblock.length; row++) {
			a[row] = "";
			for (var col=0; col<cipherblock[row].length; col++) {
				a[row] += cipherblock[row][col];
				s+= cipherblock[row][col];
			}
		}
		cipher[which] = a;
	}
	
	function repeats(cipherblock) {
		makeFrom(cipherblock);
		var index = makeIndex();
		var count1 = 0; var count2 = 0;
		var r = []; // each entry is three arrays: 1) the sequence, 2) the positions of the first sequence, 3) the positions of the second sequence.  then the two directions.
		for (var row=0; row<cipherblock.length; row++) {
			for (var col=0; col<cipherblock[row].length; col++) {
				var found = repeatsFor(index, row, col);
				if (found) {
					count1++;
					for (var i=0; i<found.length; i++) {
						if (found[i][0].length > 1) {
							count2++;
							r[r.length] = new Array(
								found[i][0],
								found[i][1],
								found[i][2],
								found[i][3], found[i][4] // the two directions.
							);
						}
					}
				}
				
			}
		}
		           
		var rNew = [];
		var hash = {};

		/* discard if:
			- sequence length is less than 3
			- sequence is palindromic and matches itself
			- duplicate			
		 */
		for (var i=0; i<r.length; i++) {
			
			/* sequence length is less than 2 */
			var len = r[i][0].length;
			if (len < 2) continue;

			/* check for palindrome.  the two sets of positions will be equal. */
			var check = new Array();
			var count = 0;
			for (var j=0; j<r[i][1].length; j++) {
				var key = ""+r[i][1][j];
				if (!check[key]) {
					count++;
					check[key] = true;
				}
				key = ""+r[i][2][j];
				if (!check[key]) {
					count++;
					check[key] = true;
				}
			}
			if (count == len) continue;
			
			/* is this a dupe? */
			var union = r[i][1].concat(r[i][2]); // combine the two sets positions
			union.sort(); // sort so dupes will look the same in a hash
			var s = "";
			for (var j=0; j<union.length; j++) s+= union[j]+";";
			if (hash[s]) continue;			
			hash[s] = true;
			rNew[rNew.length] = new Array( r[i][0], r[i][1], r[i][2], r[i][3], r[i][4]);
		}
		
		sequences = rNew;
		    
		var lengths = {};
		if (sequences.length == 0) { html += "None found"; return; }
		for (var i=0; i<sequences.length; i++) { // make distinct set of all lengths
			lengths[rNew[i][0].length] = true;
		}
		var l2 = [];
		for (var l in lengths) l2[l2.length] = l;
		l2.sort(function(a,b) { if (a<b) return -1; if (a>=b) return 1; else return 0});
		    
		var html = "Repeated sequences.  Click to highlight.  ";
		
		for (var i=l2.length-1; i>=0; i--) {
			html += "<b>Length: " + l2[i] + "</b>: "
			for (var j=0; j<sequences.length; j++) {
				if (rNew[j][0].length == l2[i]) {
					html += "<a href=\"javascript:hSeq(" + j + ")\">" + rNew[j][0] + "</a> | ";
				}
			}
		}

		html += "<br>Number of sequences by direction: <table>";
		for (var i=0; i<8; i++) {
			html += "<tr valign='top'>";
			html += "<td>";
			if (i==0) html += "right";
			else if (i==1) html += "right-down";
			else if (i==2) html += "down";
			else if (i==3) html += "left-down";
			else if (i==4) html += "left";
			else if (i==5) html += "left-up";
			else if (i==6) html += "up";
			else if (i==7) html += "right-up";
			else html += "???";
			html += "</td><td>";
			html += "</td></tr>";
		}
		html += "</table>";


		document.getElementById("seq").innerHTML = html;
	}

	/** find all repeated sequences for strings starting at [row, col] */
	function repeatsFor(index, row, col) {
		var r = new Array();
		
		var directions = new Array(
			new Array(0, 1), // right
			new Array(1, 1), // right-down
			new Array(1, 0), // down
			new Array(1, -1), // left-down
			new Array(0, -1), // left
			new Array(-1, -1), // left-up
			new Array(-1, 0), // up
			new Array(-1, 1) // right-up
		);
		
		/* inspect each direction */
		var key = get(row, col);
		var candidates = index[key];
		if (candidates) {
			for (var c=0; c<candidates.length; c++) {
				for (var i=0; i<directions.length; i++) {
					for (var j=0; j<directions.length; j++) {
						var result = new Array();
						result[0] = "";
						result[1] = new Array();
						result[2] = new Array();       

						if (row == candidates[c][0] && col == candidates[c][1] && i==j ) {
							; // do nothing, because we don't want to match the sequence at (row,col) with itself. 
						} else {
							matches(result, row, col, directions[i][0], directions[i][1], candidates[c][0], candidates[c][1], directions[j][0], directions[j][1]);
							r[r.length] = new Array(result[0], result[1], result[2], i, j);
						}
					}
				}
			}
		}     

		                            
		return r;
	}               
	
	/** look for string match for sequences beginning at [r0,c0] and [r1,c1].  direction of sequences is determined by 
	    (dr0, dc0) and (dr1, dc1).  "result" is an array that tracks the maximum matched sequence and occurrence positions. */
	function matches(result, r0, c0, dr0, dc0, r1, c1, dr1, dc1) {
		var ch1 = get(r0, c0);
		var ch2 = get(r1, c1);
//		alert("r0 " + r0 + " c0 " + c0 + " dr0 " + dr0 + " dc0 " + dc0 + " r1 " + r1 + " c1 " + c1 + " dr1 " + dr1 + " dc1 "+ dc1 + " ch1 " + ch1 + " ch2 " + ch2);
		
		if (ch1 == ch2) {
			result[0] += ch1;
			result[1][result[1].length] = new Array(getR(r0), getC(c0));
			result[2][result[2].length] = new Array(getR(r1), getC(c1));
			matches(result, (r0+dr0), (c0+dc0), dr0, dc0, (r1+dr1), (c1+dc1), dr1, dc1);
		}
	}          

	// get all ngrams from the given cipher
	function ngrams(line, n) {
		var map = {};
		var ngram; var count;
		for (var i=0; i<line.length-n+1; i++) {
			ngram = line.substring(i,i+n);
			count = map[ngram];
			if (count) count++;
			else count=1;
			map[ngram] = count;
		}
		return map;
	}  

	// given a map of ngrams, return a new map of start positions for each repeating ngram
	function ngramRepeatPositions(line, map) {
		var positions = {};
		for (var key in map) {
			if (map[key] < 2) continue;
			if (!positions[key]) positions[key] = [];
			var i=0;
			while (true) {
				i = line.indexOf(key, i);
				if (i > -1) positions[key].push(i);
				else break;
				i++;
			}
		}    
		return positions;
	}
	
	// transposed ngrams
	function ngramstrans(line, n) {
		var map1 = {}; var map2 = {};
		var ngram; var val; var list;
		
		var positions = {}; var pos;
		var ignore;
		
		for (var i=0; i<line.length-n+1; i++) {
			ngram = line.substring(i,i+n);
			ngramSorted = sortByChar(ngram);
			
			// do not count overlapping matches.  For example, the string ABCA can be split into two 3-grams: ABC and BCA, whose transpositions match each other.
			ignore=false;
			if (positions[ngramSorted]) {
				pos = positions[ngramSorted];
				for (var j=0; j<pos.length; j++) {
					if (i-pos[j] < n) {
						ignore = true;
						break;
					}
				}
			} else pos = [];
			
			if (ignore) continue;
			
			pos[pos.length] = i;
			positions[ngramSorted] = pos;
			
			// first map tracks the count of total occurrences
			if (!map1[ngramSorted]) val = 0;
			else val = map1[ngramSorted];
			val++;
			map1[ngramSorted] = val;
			
			// second map tracks the string sequences
			if (!map2[ngramSorted]) list = {};
			else list = map2[ngramSorted];
			list[ngram] = true;
			map2[ngramSorted] = list;
			
		}
		
		// remove all strictly non-transposed sequences, and remove anything with count of 1.
		var found = false;
		for (var key1 in map2) {
			if (map1[key1] <= 1) {
				map1[key1] = false;
				map2[key1] = false;
				continue;
			}
			var u = {};
			var count = 0;
			for (var key2 in map2[key1]) {
				if (!u[key2]) {
					count++;
					u[key2] = true;
				}
				if (count > 1) break;
			}
			if (count == 1) {
				map1[key1] = false;
				map2[key1] = false;
			} else {
				found = true;
			}
		}
		
		var r = [];
		r.push(map1);
		r.push(map2);
		r.push(found);
		return r;
	}
	
	function sortByChar(s) {
		var r = [];
		for (var i=0; i<s.length; i++) r.push(s.charAt(i));
		r.sort();
		var sorted = "";
		for (var i=0; i<r.length; i++) sorted += r[i];
		return sorted;
	}
	
	function dumpNGrams(all) {
		var line = "";
		for (var i=0; i<cipher[which].length; i++) line += cipher[which][i];
		var html = "";
		
		var go = true;
		var n = 1; var repeats = 0; var uniqueRepeats=0;
		while (go) {
			go=false;
			var map = ngrams(line, n);
			
			html += "<h3>" + n + "-grams:</h3>";
			var keys = [];
			for (var key in map) keys[keys.length] = key;
			var sorted = sortByValue(keys, map);
			
			var result1 = "";
			var result2 = "";
			for (var i=0; i<sorted.length; i++) {
				count = map[sorted[i]];
				if (count > 1) go = true;
				if (all || count > 1) {
					result1 += getImg(sorted[i]) + " (" + count + ") ";
					result2 += sorted[i] + " (" + count + ") ";
				}
				if (count > 1) {
					repeats += count;
					uniqueRepeats++;
				}
			}
			html += result1 + "<br><br>ASCII version:<br>" + result2
		
			html += "<br><br><u>Total repeated " + n + "-grams</u>: <b>" + repeats + " (" + uniqueRepeats + " unique " + n + "-grams.)</b>";
			if (!go) html += "<br>No more repeats found."
			html += "<hr>"
			n++;
		}
		document.getElementById("ngrams").innerHTML = html;
		window.location="#ngrams";
	}
	
	function dumpAlphabetStats() {
		var html = "";
		var html2 = "<pre>";
		var counts = [];
		var symbols = [];
		for (var i=0; i<cipher.length; i++) {
			counts[i] = [];
			for (var row=0; row<cipher[i].length; row++) {
				for (var col=0; col<cipher[i][row].length; col++) {
					var symbol = cipher[i][row].charAt(col);
					symbols[symbol] = symbol;
					if (counts[i][symbol]) counts[i][symbol]++; else counts[i][symbol] = 1;
				}
			}
		}
		
		html += "<table border=1><tr><th>Symbol</th><th>ASCII</th><th>408 count</th><th>340 count</th><th>Total</th><th>Difference</th><th>408 Plaintext</th></tr>";

		var sorted = [];
		for (var symbol in symbols) sorted[sorted.length] = symbol;
		sorted.sort();
		
		var c3 =0; var c4=0;
		for (var i=0; i<sorted.length; i++) {
			var symbol = sorted[i];
			var count3 = (counts[0][symbol] ? counts[0][symbol] : 0);
			var count4 = (counts[1][symbol] ? counts[1][symbol] : 0);
			var decoded = (count4 > 0 ? decode408For(symbol) : "");
			html += "<tr>";
			html += "<td><img src=\"alphabet/" + getName(symbol, true) + ".jpg\"></td>";
			html += "<td>" + symbol + "</td>";
			c3+=count3;
			c4+=count4;
			html += "<td>" + count4 + "</td>";
			html += "<td>" + count3 + "</td>";
			html += "<td>" + (count4+count3) + "</td>";
			html += "<td>" + Math.abs(count4-count3) + "</td>";
			html += "<td>" + decoded + "</td>"
			html += "</tr>";
			
			var bg = (count3 > 0 && count4 > 0) ? "#ccc" :
				(count3 > 0 ? "#ccff99" : "#99ccff");
			
			html2 += "|-valign=\"top\" style=\"background-color: " + bg + "\"\n";
			html2 += "| style=\"text-align: right; border-style: solid; border-width: 1px\"| [[File:" + getName(symbol, true) + ".jpg]]\n";
			html2 += "| style=\"text-align: right; border-style: solid; border-width: 1px\"| &lt;tt>" + symbol + "&lt;/tt>\n";
			html2 += "| style=\"text-align: right; border-style: solid; border-width: 1px\"| " + count3 + "\n";
			html2 += "| style=\"text-align: right; border-style: solid; border-width: 1px\"| " + count4 + "\n";
			html2 += "| style=\"text-align: right; border-style: solid; border-width: 1px\"| " + (count4+count3) + "\n";
			html2 += "| style=\"text-align: right; border-style: solid; border-width: 1px\"| " + Math.abs(count4-count3) + "\n";
			html2 += "| style=\"text-align: right; border-style: solid; border-width: 1px\"| " + decoded + "\n";
			
		}
//		alert(c3); alert(c4);
		html += "</tr></table>";
		
		
		document.getElementById("seq").innerHTML = html;// + html2;
	}
	
	/** get image corresponding to given string of cipher letters */
	function getImg(s, row) {
		var result = "";
		for (var i=0; i<s.length; i++) {
			var g = "";
			//if (row % 2 == 0) g = "green/lighter/"
			result += "<img src=\"alphabet2/" + g + getName(s.substring(i,i+1)) + ".jpg\">";
		}
		return result;
	}
	/** get image corresponding to given string of cipher letters */
	function getImgDarker(s) {
		var result = "";
		for (var i=0; i<s.length; i++) {
			result += "<img src=\"alphabet/darker/" + getName(s.substring(i,i+1)) + ".jpg\">";
		}
		return result;
	}
	
	/** get cipher character at position (r, c).  translate out of bounds values to within bounds values. */
	function get(r, c) {
		
		r=getR(r);
		c=getC(c);
		
		return cipher[which][r].substring(c,c+1);
	}     
	
	function getR(r) {
		r = r % cipher[which].length;
		if (r < 0) r = cipher[which].length + r;
		return r;
	}
	
	function getC(c) {
		c = c % cipher[which][0].length;
		if (c < 0) c = cipher[which][0].length + c;
		return c;
	}
	
	/** highlight/unhighlight the given matches */
	function hmatch(matches, highlight) {
		for (var j=1; j<3; j++) {
			for (var i=0; i<matches[j].length; i++) {
				var id = getR(matches[j][i][0])+"_"+getC(matches[j][i][1]);
//				alert(id);
				if (highlight) h2(id); else u(id);
			}
		}
	}

	// generate list of all homophones for each unique plaintext letter in the 408 solution
	function homophonesFor408() {
		var d = interestingHash[1][0]["decoder"];
		var u = {};
		for (i=0;i<alphabet[1].length; i++) {
			var symbol = alphabet[1][i];
			var plaintext = d[i];
			if (u[plaintext]) u[plaintext].push(symbol);
			else u[plaintext] = [symbol];
		}
		return u;
	}
        
    // replace all occurences of symbol c with plaintext p
	function decode(c, p) {
		for (var row = 0; row < cipher[which].length; row++) {
			for (var col = 0; col < cipher[which][row].length; col++) {
				if (cipher[which][row][col] == c) plaintext(p, row, col);
			}
		}
	} 
	
	// write plaintext letter pt at given row,col
	function plaintext(p, row, col) {
		var e = document.getElementById(row+"_"+col);
		e.className = "pt";
		e.style = "font-size: 20pt; color: #090;";
		e.innerHTML = p;
	}
	
	function decode408For(ch) {
		var d = interestingHash[1][0]["decoder"];
		var i;
		for (i=0;i<alphabet[1].length; i++) if (alphabet[1].charAt(i) == ch) break;
		if (d.charAt(i)) return d.charAt(i).toUpperCase();
		return "";
	}
	
	function decode408(str) {
		var s = "";
		for (var i=0; i<str.length; i++) s+=decode408For(str.charAt(i));
		return s;
	}	

	/* hide the given symbol */
	function hide(row, col) {
		document.getElementById(row+"_"+col).style.visibility="hidden";
	}
	/* show the given symbol */
	function show(row, col) {
		document.getElementById(row+"_"+col).style.visibility="";
	}

	/* darken the symbols at the given row,col */
	function darkenrc(row, col) {
		var e = document.getElementById(row+"_"+col);
		if (e == null) return;
		e.className = "dimmed";
		e.innerHTML = getImgDarker(cipher[which][row].charAt(col));
	}
	/* darken the symbols at the given row,col */
	function darkenrc2(row, col) {
		var e = document.getElementById(row+"_"+col);
		if (e == null) return;
		e.innerHTML = getImgDarker(cipher[which][row].charAt(col));
	}         
	
	function darkenAll() {
		for (var row=0; row<HEIGHT; row++) {
			for (var col=0; col<WIDTH; col++) {
				darkenrc2(row, col);
			}
		}
	}
	
	/* lighten the symbols at the given row,col */
	function lightenrc(row, col) {
		var e = document.getElementById(row+"_"+col);
		if (e == null) return;
		e.className = "";
		e.innerHTML = getImg(cipher[which][row].charAt(col), row);
	}
	
	function darkenpos(pos) {
		var H=cipher[which].length;
		var W=cipher[which][0].length;
		darkenrc(parseInt(pos/W), pos%W);
	}
	function darkenpos2(pos) {
		var H=cipher[which].length;
		var W=cipher[which][0].length;
		darkenrc2(parseInt(pos/W), pos%W);
	}
	function darkenposarray(a) {
		for (var i=0; i<a.length; i++) {
			var H=cipher[which].length;
			var W=cipher[which][0].length;
			darkenrc(parseInt(a[i]/W), a[i]%W);
		}
	}
	
	// assign a random color to the given positions
	function randcolor(a) {
		var color = randomRGB();
		for (var i=0; i<a.length; i++)
			rgb(a[i], color[0], color[1], color[2]);
	}
	function randomRGB() {
		var r = Math.floor(Math.random()*192) + 64;
		var g = Math.floor(Math.random()*192) + 64;
		var b = Math.floor(Math.random()*192) + 64;
		return [r, g, b];
	}
	
	function rgb(pos, r, g, b) {
		var H=cipher[which].length;
		var W=cipher[which][0].length;
		var row = parseInt(pos/W);
		var col = pos%W;

		darkenrc2(row,col);
		var elem = document.getElementById(row+"_"+col);
		if (elem) {
				elem.style.backgroundColor="rgb("+r+","+g+","+b+")";
				elem.childNodes[0].style.opacity="0.35";
				//elem.style.paddingBottom="2px";
		}
	}            
	
	function rgbClear(pos) {
		var H=cipher[which].length;
		var W=cipher[which][0].length;
		var row = parseInt(pos/W);
		var col = pos%W;
		
		var elem = document.getElementById(row+"_"+col);
		if (elem) {
				elem.style.backgroundColor="";
				elem.childNodes[0].style.opacity="";
				//elem.style.paddingBottom="2px";
		}
	}
	
	// mark rectangle defined by the given upper left and lower right corners
	function rgbRectangle(r1, c1, r2, c2, color) {
		if (!color) color = randomRGB();
		for (var r=r1; r<=r2; r++) {
			for (var c=c1; c<=c2; c++) {
				rgb(r*WIDTH+c, color[0], color[1], color[2]);
			}
		}
	}
	
	function hsl(pos, h, s, l) {
		var H=cipher[which].length;
		var W=cipher[which][0].length;
		var row = parseInt(pos/W);
		var col = pos%W;

		darkenrc2(row,col);
		var elem = document.getElementById(row+"_"+col);
		if (elem) {
				elem.style.backgroundColor="hsl("+h+","+s+"%,"+l+"%)";
				elem.childNodes[0].style.opacity="0.25";
				//elem.style.paddingBottom="2px";
		}
	}
	
	// from http://stackoverflow.com/questions/470690/how-to-automatically-generate-n-distinct-colors
	// assumes hue [0, 360), saturation [0, 100), lightness [0, 100)
	// takes array of arrays of positions as input
	function hsl_random(a) {
		var num_colors = a.length;
		var colors = [];
		for(var i = 0; i < 360; i += 360 / num_colors) {
		    var c = [];
			c[0] = i; // hue
		    c[1] = 90 + Math.random() * 10; // sat
		    c[2] = 50 + Math.random() * 10; // light
		    colors[colors.length] = c;
		}
		
		for (var i=0; i<a.length; i++) {
			var pos = a[i];
			var color = colors[i];
			for (var j=0; j<pos.length; j++) {
				var p = pos[j];
				hsl(p, color[0], color[1], color[2]);
			}
		}
	}
	
	
	function lightenpos(pos) {
		var H=cipher[which].length;
		var W=cipher[which][0].length;
		lightenrc(parseInt(pos/W), pos%W);
	}
	/* darken all the symbols specified by the given array */
	function darken(rc) {
		var maxrow = -1;
		var minrow = 100000;
		var maxcol = -1;
		var mincol = 100000;
		for (var i=0; i<rc.length; i++) {
			var r = rc[i][0];
			var c = rc[i][1];
			maxrow = Math.max(maxrow, r);
			minrow = Math.min(minrow, r);
			maxcol = Math.max(maxcol, c);
			mincol = Math.min(mincol, c);
			darkenrc(r,c);
		}
		if (maxrow >= minrow && maxcol >= mincol) {
			for (var r=0; r<minrow-1; r++) {
				for (var c=0; c<cipher[which][r].length; c++) {
					hide(r, c);
				}
			}
			for (var r=(minrow==0 ? 0 : minrow-1); r<=maxrow+1 && r<cipher[which].length; r++) {
				for (var c=0; c<mincol-1; c++) {
					hide(r,c);
				}
				for (var c=maxcol+2; c<cipher[which][r].length; c++) {
					hide(r,c);
				}
			}
			for (var r=maxrow+2; r<cipher[which].length; r++) {
				for (var c=0; c<cipher[which][r].length; c++) {
					hide(r, c);
				}
			}
		}
	}
	/* highlight the path in the grid, then output the symbols, word, and the needed rotations/mirrors to make the word clearer */
	function showWord(rc, word) {
//		render();
//		darken(rc);
		var c=cipher[which];
		var html = "<table class=\"show\">";
		var row1 = "<tr style=\"visibility:visible\">";
		for (var i=0; i<rc.length; i++) {
			row1 += "<td>"+getImgDarker(c[rc[i][0]].charAt(rc[i][1])) + "</td>";
		}
		row1 += "</tr>"
		var row2 = "<tr style=\"visibility:visible\">";
		for (var i=0; i<rc.length; i++) {
			row2 += "<td><img src=\"" + translate(c[rc[i][0]].charAt(rc[i][1]), word.charAt(i)) + "\"></td>";
		}
		row2 += "</tr>"
		
		if (row1 == row2) {
			row1 = row1.replace("visible","hidden");
		}
		html += row1 + row2;
		
		
		html += "<tr>"
		for (var i=0; i<word.length; i++) {
			html += "<td>" + word.charAt(i) + "</td>";
		}
		html += "</tr>"
		html += "</table>";
		document.getElementById("word").innerHTML = html;
	}
	/* return the image that demonstrates the given symbol-to-interpretation mapping. */
	
	var translations = [];
	translations["!H"] = "funnyi-h";
	translations["#O"] = "sqe";
	translations["%O"] = "sqe";
	translations["&B"] = "pf-b";
	translations["&D"] = "pf-d";
	translations["&P"] = "pf";
	translations["&Q"] = "pf-q";
	translations[")I"] = "theta";
	translations["*O"] = "sqe";
	translations["+X"] = "x";
	translations["-I"] = "bar";
	translations[".O"] = "o";
	translations["/I"] = "bar";
	translations["0R"] = "r";
	translations["7A"] = "a";
	translations["7D"] = "n9-d";
	translations["8A"] = "a";
	translations["8D"] = "n9-d";
	translations["9A"] = "a";
	translations["9D"] = "n9-d";
	translations[":H"] = "idr-h";
	translations[";H"] = "idl-h";
	translations["<L"] = "lt-l";
	translations["<V"] = "v";
	translations["=K"] = "k";
	translations[">L"] = "lt-l";
	translations[">V"] = "v";
	translations["@O"] = "sqe";
	translations["CN"] = "c-n";
	translations["CU"] = "c-u";
	translations["CV"] = "c-v";
	translations["EM"] = "e-m";
	translations["EW"] = "e-w";
	translations["HI"] = "h-i";
	translations["IH"] = "i-h";
	translations["JY"] = "j-y";
	translations["ME"] = "m-e";
	translations["MW"] = "m-w";
	translations["NZ"] = "n-z";
	translations["PB"] = "p-b";
	translations["PD"] = "p-d";
	translations["PQ"] = "p-q";
	translations["UN"] = "u-n";
	translations["VL"] = "lt-l";
	translations["WE"] = "w-e";
	translations["WM"] = "w-m";
	translations["XT"] = "plus";
	translations["YT"] = "y-t";
	translations["ZN"] = "z-n";
	translations["\\I"] = "bar";
	translations["^L"] = "lt-l";
	translations["^A"] = "a";
	translations["^V"] = "v";
	translations["_O"] = "sqe";
	translations["bB"] = "b";
	translations["cC"] = "c";
	translations["cN"] = "c-n";
	translations["cU"] = "c-u";
	translations["cV"] = "c-v";
	translations["dD"] = "d";
	translations["eE"] = "e";
	translations["eM"] = "e-m";
	translations["eW"] = "e-w";
	translations["fF"] = "f";
	translations["jJ"] = "j";
	translations["jY"] = "j-y";
	translations["kK"] = "k";
	translations["lL"] = "l";
	translations["pB"] = "p-b";
	translations["pD"] = "p-d";
	translations["pP"] = "p";
	translations["pQ"] = "p-q";
	translations["qQ"] = "q";
	translations["rR"] = "r";
	translations["tT"] = "t";
	translations["yT"] = "y-t";
	translations["yY"] = "y";
	translations["zO"] = "o";
	translations["zT"] = "plus";
	translations["zZ"] = "z";
	
	function translate(symbol, plaintext) {
		var val = translations[symbol + plaintext];
		var tr;
		if (val == null) tr = getName(symbol);
		else tr = val;
		return "alphabet/darker/" + tr + ".jpg";
	}


var rgb_current = randomRGB();
function tog(event, row, col) {
	//console.log(event.shiftKey + " " + event.altKey + " " + event.ctrlKey);
	if (event.altKey && event.shiftKey) {
		rgb_current = randomRGB();
	}
	if (event.altKey) {
		rgb(row*WIDTH+col,rgb_current[0], rgb_current[1], rgb_current[2]);
		return;
	}
	if(event.shiftKey) {
		dbl(row, col);
		return;
	}
	
	var elem = document.getElementById(row+"_"+col);
	var src = elem.childNodes[0].src;
	if (src.indexOf("darker") > -1) {
		lightenrc(row, col);
	} else darkenrc2(row, col);
}	

function dbl(row, col) {
	var elem = document.getElementById(row+"_"+col);
	var src = elem.childNodes[0].src;
	var lighten = false;
	if (src.indexOf("darker") > -1) lighten = true;
	
	var ch = cipher[which][row][col];
	for (var r = 0; r<cipher[which].length; r++) {
		for (var c = 0; c<cipher[which][r].length; c++) {
			if (cipher[which][r][c] == ch)
				if (lighten) lightenrc(r,c);
				else darkenrc(r,c);
			
		}
	}
}	

function isPrime1(n) {
 if (isNaN(n) || !isFinite(n) || n%1 || n<2) return false; 
 var m=Math.sqrt(n);
 for (var i=2;i<=m;i++) if (n%i==0) return false;
 return true;
}
	
