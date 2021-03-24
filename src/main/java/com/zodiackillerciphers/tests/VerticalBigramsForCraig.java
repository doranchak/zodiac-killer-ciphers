package com.zodiackillerciphers.tests;

import java.util.List;

import com.zodiackillerciphers.ngrams.NGramsBean;

public class VerticalBigramsForCraig {
	/** output occurrences of vertically repeating bigrams for craig */
	public static void process() {

		/** reading bigrams vertically is equivalent to ROT90 CCW, vertical flip */
		String cipher = "HNBS_p#(d-U2zly|R+|>Epyp985G<z+<6GBcc+FMR+:pMR+2MlRc9FX.T)kD>Bc7+^KJ+U/lSN13+WdHp(M^zFqfbV5Ry^*zLCWNl#+ltl%j++tJ#f:B1z<p^OU8jO;#Z^E|+54K6W7kV%Z*d-2ORJ|*N29(CctSPDGV|*U+2+D5|4CO<PBzkWW35dc_FOYT5bEp+O_Z|Y(pFCXNBpB4F.>^FSYO1.)OPkGYc7pMBcV.lHO8L<L++FVzy<b.cVUfWTBAT*#+&>.+AFT+(4ZMB/*|GKzR42z@6BM&;t5q|(-K2fHKkDLL4yKB8+-G))C;d)J2/(|9K-OFR++2Lpc+";
		//"9WMSk@RLcBZIPrz^Np%VH_ZV%VJ(^+NdZPc95IU=keOZ!e@EP+Y/Lqt/KDpDM!pS)XTeFdJX/e^9MGIPqROR8JkrSq5GB#TrZGU#ZDY#p+V_RkAlcWRYX#t9/YIBJ9EBIjWTU59fEqUK96qWUFkPdKl@)=IYt9#U/_cEze_IB67OrIOXW65r%8Be9F+_X586%9qR\\)8qq\\+\\LLV6%#_TAPJqkHTAp6qE!Ntd)MW7%8dYDOIEOPtUFqGH8(LeNl\\DZcYAdR+HR@N%HXBM5e)/VN+zf+q9\\XrM=KQfV8TULEl@EAVGA@_%7QB)p!YRW5Q^MU^XK)T%P9^#LFP=XqDlezSRrHRJHZt%#ASL!%QU=Y5q8S#R9k6Q=(OIB9qt=GWIBe)EY(Bk#FHAGPPMVBW_qc6k";
		NGramsBean ng = new NGramsBean(3, cipher);
		//ng.dump(); // 23 repeats

		for (String key : ng.positions.keySet()) {
			List<Integer> positions = ng.positions.get(key);
			if (positions.size() < 2) continue;
			//System.out.println(key);
			String p = "";
			for (Integer pos : positions) {
				int r1 = pos / 20;
				int c1 = pos % 20;
				int r2 = (pos+1) / 20;
				int c2 = (pos+1) % 20;
				int r3 = (pos+2) / 20;
				int c3 = (pos+2) % 20;
				
				//System.out.println(pos + " (" + r + "," + c + ")");
				//System.out.println("darkenrc(" + c + "," + r + ")");
				int pos2 = c1*17+r1;
				int pos3 = c2*17+r2;
				int pos4 = c3*17+r3;
				if (p.length() > 0) p += ",";
				p += pos2 + "," + pos3 + "," + pos4;
			}
			System.out.println("randcolor([" + p + "])");
		}

		// convert rotated/flip positions back to normal positions
		// (r1, c1)
		// (r2, c2)

	}

	public static void main(String[] args) {
		process();
	}
}
