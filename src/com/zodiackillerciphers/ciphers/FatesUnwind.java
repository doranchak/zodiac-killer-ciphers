package com.zodiackillerciphers.ciphers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.ciphers.algorithms.ColumnarTranspositionRumkin;
import com.zodiackillerciphers.ciphers.algorithms.InlineTransposition;
import com.zodiackillerciphers.ciphers.algorithms.RailFence;
import com.zodiackillerciphers.dictionary.WordFrequencies;
import com.zodiackillerciphers.io.FileUtil;
import com.zodiackillerciphers.lucene.NGrams;
import com.zodiackillerciphers.lucene.Stats;
import com.zodiackillerciphers.ngrams.NGramsBean;

public class FatesUnwind {
	
	public static Map<String, Integer> ngrams;
	public static Map<String, List<Integer>> ngramsLoc;
	public static Map<String, Integer> ngramsTransposed;
	public static Map<String, List<String>> ngramsTransposedList;
	static {
		ngrams = new HashMap<String, Integer>();
		ngramsLoc = new HashMap<String, List<Integer>>();
		ngramsTransposed = new HashMap<String, Integer>();
		ngramsTransposedList = new HashMap<String, List<String>>();
	}
	
	public static String tale = "FRANCELESSFAVOUREDONTHEWHOLEASTOMATTERSSPIRITUALTHANHERSISTEROFTHESHIELDANDTRIDENTROLLEDWITHEXCEEDINGSMOOTHNESSDOWNHILLMAKINGPAPERMONEYANDSPENDINGIT.UNDERTHEGUIDANCEOFHERCHRISTIANPASTORSSHEENTERTAINEDHERSELFBESIDESWITHSUCHHUMANEACHIEVEMENTSASSENTENCINGAYOUTHTOHAVEHISHANDSCUTOFFHISTONGUETORNOUTWITHPINCERSANDHISBODYBURNEDALIVEBECAUSEHEHADNOTKNEELEDDOWNINTHERAINTODOHONOURTOADIRTYPROCESSIONOFMONKSWHICHPASSEDWITHINHISVIEWATADISTANCEOFSOMEFIFTYORSIXTYYARDS.ITISLIKELYENOUGHTHATROOTEDINTHEWOODSOFFRANCEANDNORWAYTHEREWEREGROWINGTREESWHENTHATSUFFERERWASPUTTODEATHALREADYMARKEDBYTHEWOODMANFATETOCOMEDOWNANDBESAWNINTOBOARDSTOMAKEACERTAINMOVABLEFRAMEWORKWITHASACKANDAKNIFEINITTERRIBLEINHISTORY.ITISLIKELYENOUGHTHATINTHEROUGHOUTHOUSESOFSOMETILLERSOFTHEHEAVYLANDSADJACENTTOPARISTHEREWERESHELTEREDFROMTHEWEATHERTHATVERYDAYRUDECARTSBESPATTEREDWITHRUSTICMIRESNUFFEDABOUTBYPIGSANDROOSTEDINBYPOULTRYWHICHTHEFARMERDEATHHADALREADYSETAPARTTOBEHISTUMBRILSOFTHEREVOLUTION.BUTTHATWOODMANANDTHATFARMERTHOUGHTHEYWORKUNCEASINGLYWORKSILENTLYANDNOONEHEARDTHEMASTHEYWENTABOUTWITHMUFFLEDTREADTHERATHERFORASMUCHASTOENTERTAINANYSUSPICIONTHATTHEYWEREAWAKEWASTOBEATHEISTICALANDTRAITOROUS.INENGLANDTHEREWASSCARCELYANAMOUNTOFORDERANDPROTECTIONTOJUSTIFYMUCHNATIONALBOASTING.DARINGBURGLARIESBYARMEDMENANDHIGHWAYROBBERIESTOOKPLACEINTHECAPITALITSELFEVERYNIGHTFAMILIESWEREPUBLICLYCAUTIONEDNOTTOGOOUTOFTOWNWITHOUTREMOVINGTHEIRFURNITURETOUPHOLSTERERSWAREHOUSESFORSECURITYTHEHIGHWAYMANINTHEDARKWASACITYTRADESMANINTHELIGHTANDBEINGRECOGNISEDANDC";
	public static String taleNoPeriods = "FRANCELESSFAVOUREDONTHEWHOLEASTOMATTERSSPIRITUALTHANHERSISTEROFTHESHIELDANDTRIDENTROLLEDWITHEXCEEDINGSMOOTHNESSDOWNHILLMAKINGPAPERMONEYANDSPENDINGITUNDERTHEGUIDANCEOFHERCHRISTIANPASTORSSHEENTERTAINEDHERSELFBESIDESWITHSUCHHUMANEACHIEVEMENTSASSENTENCINGAYOUTHTOHAVEHISHANDSCUTOFFHISTONGUETORNOUTWITHPINCERSANDHISBODYBURNEDALIVEBECAUSEHEHADNOTKNEELEDDOWNINTHERAINTODOHONOURTOADIRTYPROCESSIONOFMONKSWHICHPASSEDWITHINHISVIEWATADISTANCEOFSOMEFIFTYORSIXTYYARDSITISLIKELYENOUGHTHATROOTEDINTHEWOODSOFFRANCEANDNORWAYTHEREWEREGROWINGTREESWHENTHATSUFFERERWASPUTTODEATHALREADYMARKEDBYTHEWOODMANFATETOCOMEDOWNANDBESAWNINTOBOARDSTOMAKEACERTAINMOVABLEFRAMEWORKWITHASACKANDAKNIFEINITTERRIBLEINHISTORYITISLIKELYENOUGHTHATINTHEROUGHOUTHOUSESOFSOMETILLERSOFTHEHEAVYLANDSADJACENTTOPARISTHEREWERESHELTEREDFROMTHEWEATHERTHATVERYDAYRUDECARTSBESPATTEREDWITHRUSTICMIRESNUFFEDABOUTBYPIGSANDROOSTEDINBYPOULTRYWHICHTHEFARMERDEATHHADALREADYSETAPARTTOBEHISTUMBRILSOFTHEREVOLUTIONBUTTHATWOODMANANDTHATFARMERTHOUGHTHEYWORKUNCEASINGLYWORKSILENTLYANDNOONEHEARDTHEMASTHEYWENTABOUTWITHMUFFLEDTREADTHERATHERFORASMUCHASTOENTERTAINANYSUSPICIONTHATTHEYWEREAWAKEWASTOBEATHEISTICALANDTRAITOROUSINENGLANDTHEREWASSCARCELYANAMOUNTOFORDERANDPROTECTIONTOJUSTIFYMUCHNATIONALBOASTINGDARINGBURGLARIESBYARMEDMENANDHIGHWAYROBBERIESTOOKPLACEINTHECAPITALITSELFEVERYNIGHTFAMILIESWEREPUBLICLYCAUTIONEDNOTTOGOOUTOFTOWNWITHOUTREMOVINGTHEIRFURNITURETOUPHOLSTERERSWAREHOUSESFORSECURITYTHEHIGHWAYMANINTHEDARKWASACITYTRADESMANINTHELIGHTANDBEINGRECOGNISEDANDC";
	public static String cipherFull = "˛¿?,.ç*ϕ,¥ϕ'¡ˇΔ-.#-ϕπ¡q.μ•Δˇ•.,*ϕ,°ç¿¡˛¡ç°•ç¡çq˘.Ω'*ϕ¡Ô.çº-.¿°¡Δ,.¥μç,.˛¡*º•¡º•ˇ.°'ϕ.ˇ¡π.ç∞¿μ¿.ç¡Δ-.y¡,ˇç*•¡•°.¿¡'•˛•¥ç¡çμ•¿,¿.ç.•Ωç¿ϕ¡'.ˇ°.ºÔq.¿π˘¡-*°Δ.•*.•#-˛¿ˇ'ç.¥¡ˇ¿¡π¿μ¡¿.çΔ',Ω.,°¡•˘.ˇ•Δ.¿,˘¡˛¡ç.μ•¥.•*∞Ô¡º-Δ¿˘ç°ϕ-**.¿¥¡ç.•.ç¡,.¿πˇ•.q'ˇ¡˘',º-ˇΔ.•ºΩ.#¡•μ*˘.°¥¿ç.-Δ'¡˛π˘.ç•¿*•,.ç.Ôqç,¿¡ç-Δº.•˘'¿'•.ˇμ˛ç,ˇ°ˇΔ¿¡*y,∞¿μ¿ç.˛Ωπ.-.,•¡'º•.çΔ¥yç°.ç¿,¿ç*Ô¡'•.πϕ-˘*•ç.•˛¡-.¿,.μç•Ω¡¿.º-μˇ•.^ˇ°∞¿π*ç'Δ•μ¡Ô˘ˇ.μ¥*μ°Δ.¿¡Ω•.-ˇ¿•.,πç˘º.-º*Δ¡*°•qÔ¡'.°'#¿˛*ˇ.˘πçˇ•Ω¡¿¥π.μˇ¿˘.Δº°μç°•.*'Δμ¡-˘¡¿-ç.,¿.¿*Δ.,•¡¿çπ*.-.¿˘,Ω'Ω.ç#ˇμ•'°μÔ¡¿˛*¥¡'.¿Δçπ*.•¥Ωq,˘∞.¿¿-*μ-*¥¡•μçμ¡•.,˛.¿μΔ°,.º-çμπˇÔμ¡'μ.'•ϕ'¥-#y°Δμº-¡ˇ¿μ•ç.μ¡˛.•˘π¡Ô,μ¡°°μ'μ,¿,Δ-˛.Ωç•.¿¥μ.μçμ,-˘¿π¡q•.ÔΔ.°ˇ-,μˇ'μç•'¿μ-˘¡ˇ¡μ°.πΔ.•∞-μç*Ô˛˘¿.Ω¡¥°ˇ,•y.,Δ•¡˛-¡*,¿π,,μ.˛çΔ-μ°*˘•.'•.ˇ'-μ¿.ˇΩçyÔçˇ¡•˘¡°*',.Δº¿ç•çπ,¥˘•¿.∞-¡°˘*μ.Ω•.¿-˛qç,¡,•Δ˛¿˘-¿.•ˇ¥μçˇ,,'*ç'*°ˇ¡Δπ,*Ô¿'˛¿•.μy¡,ç•ºΩç-°¥,ˇ•¿Δ¿μ˛¡˘-¿•,¡*.πΔqç.º•,º*¿#¡˛.,μ°.¿çq˛çˇ,ç'¡μ•.¿'.,.qç˛¥Δçˇ˘*•,-∞•¿,˘¡qÔ¥.Δμ˛•.°'¿,-Δ^,•Ω¿*çμπçº•q˘°-˛¡ˇ-,,.μ#.'*ˇyº˛ϕ,Ôˇç,˘¡Δq˘¥.¿'ç˛¿,•º*º•Ω¿μ°,,çˇ˛.q¡¿Δπ¿Δμ.,ç¥¿-¡¿.˘.ç˛Δ¡¿•¡¿çy-.¿º*.¿*ç∞¿Δ'¡,μˇ•.'•,.ç¿-¿ˇº*ç*¡°.'¥π.çºΩ¡*˘˛¿Δ.#.,•¡¿çμμ¿ÔΔ•ç*Δ-μº*qμ°.-.¿Ω,çμ˛¿çˇ¥'¡μˇ•¡ˇ•.,π.'ç¥*Δ¿μyç.°'μ¡¿,-çº*ºμ.•˛.°Ô¿μq¡ˇ.¥•˘.*,.¿-°,μπ¡*.¿•μ¿μç˛μ-°ç¥∞μç.•'¡ˇ,.-.'Δ.Ωq'Ô¿•°¡¿π.#,.¥.¿˛¡,ç-˘*yμ*.¿•Δ.ˇ,¿°çº-˘¡˛,ç¿¥-*ç¡μ.,Ω•.°πçq¿•ˇç¿Ô.ˇ¡'Ωˇμ.*Δ.ç'˛,¡¿,.¥-.*°'•.*π¡ºΩº.yç-.˛μ.¿¡ˇ¥μ,μç•,.˘.,.˛¡¿Δº.qç*ç¿°-.•*.¿,˘ºç,ç¡∞.Ω'.ˇ˘ˇ¡¿ÔΔ.#ç°•μçΩμç˛¿μμ¥ˇ*ç¿π*.*Δ.•¿μ,ç-'ç°-*•¡-Ωπ,¿¥¡çΔ°¿.Ôq*¿¿∞.•ˇ.˛¿çˇ¡q.¿,μ•'¿-,.°¡'-¡*º•˛ç.ˇ,,¿.μçΔΩπ.y¥μç°*¡˛¿,¥,'¥¡Δçμ-ç•*.^Ô°¿q•Ω.˛,˘μç¥ˇμ-ˇ∞*.'πΔ¡'*°q˘•ˇ.qΔ¥μ¡˘.*¥Ô¿μ˛Δç,.?μ˛¿.¡";
	public static String cipherFullDallison = "ABCDEFGHDJHKLMNOEPOHQLRESTNMTEDGHDUFBLALFUTFLFRVEWKGHLXEFYOEBULNDEJSFDEALGYTLYTMEUKHEMLQEFZBSBEFLNOE1LDMFGTLTUEBLKTATJFLFSTBDBEFETWFBHLKEMUEYXREBQVLOGUNETGETPOABMKFEJLMBLQBSLBEFNKDWEDULTVEMTNEBDVLALFESTJETGZXLYONBVFUHOGGEBJLFETEFLDEBQMTERKMLVKDYOMNETYWEPLTSGVEUJBFEONKLAQVEFTBGTDEFEXRFDBLFONYETVKBKTEMSAFDMUMNBLG1DZBSBFEAWQEOEDTLKYTEFNJ1FUEFBDBFGXLKTEQHOVGTFETALOEBDESFTWLBEYOSMTE2MUZBQGFKNTSLXVMESJGSUNEBLWTEOMBTEDQFVYEOYGNLGUTRXLKEUKPBAGMEVQFMTWLBJQESMBVENYUSFUTEGKNSLOVLBOFEDBEBGNEDTLBFQGEOEBVDWKWEFPMSTKUSXLBAGJLKEBNFQGETJWRDVZEBBOGSOGJLTSFSLTEDAEBSNUDEYOFSQMXSLKSEKTHKJOP1UNSYOLMBSTFESLAETVQLXDSLUUSKSDBDNOAEWFTEBJSESFSDOVBQLRTEXNEUMODSMKSFTKBSOVLMLSUEQNETZOSFGXAVBEWLJUMDT1EDNTLAOLGDBQDDSEAFNOSUGVTEKTEMKOSBEMWF1XFMLTVLUGKDENYBFTFQDJVTBEZOLUVGSEWTEBOARFDLDTNABVOBETMJSFMDDKGFKGUMLNQDGXBKABTES1LDFTYWFOUJDMTBNBSALVOBTDLGEQNRFEYTDYGBPLAEDSUEBFRAFMDFKLSTEBKEDERFAJNFMVGTDOZTBDVLRXJENSATEUKBDON2DTWBGFSQFYTRVUOALMODDESPEKGM1YAHDXMFDVLNRVJEBKFABDTYGYTWBSUDDFMAERLBNQBNSEDFJBOLBEVEFANLBTLBF1OEBYGEBGFZBNKLDSMTEKTDEFBOBMYGFGLUEKJQEFYWLGVABNEPEDTLBFSSBXNTFGNOSYGRSUEOEBWDFSABFMJKLSMTLMTEDQEKFJGNBS1FEUKSLBDOFYGYSETAEUXBSRLMEJTVEGDEBOUDSQLGEBTSBSFASOUFJZSFETKLMDEOEKNEWRKXBTULBQEPDEJEBALDFOVG1SGEBTNEMDBUFYOVLADFBJOGFLSEDWTEUQFRBTMFBXEMLKWMSEGNEFKADLBDEJOEGUKTEGQLYWYE1FOEASEBLMJSDSFTDEVEDEALBNYERFGFBUOETGEBDVYFDFLZEWKEMVMLBXNEPFUTSFWSFABSSJMGFBQGEGNETBSDFOKFUOGTLOWQDBJLFNUBEXRGBBZETMEABFMLREBDSTKBODEULKOLGYTAFEMDDBESFNWQE1JSFUGLABDJDKJLNFSOFTGE2XUBRTWEADVSFJMSOMZGEKQNLKGURVTMERNJSLVEGJXBSANFDECSABEL";
	public static String[] cipher = new String[] {
		"˛¿?,.ç*ϕ,¥ϕ'¡ˇΔ-.#-ϕπ¡q.μ•Δˇ•.,*ϕ,°ç¿¡˛¡ç°•ç¡çq˘.Ω'*ϕ¡Ô.çº-.¿°¡Δ,.¥μç,.˛",
		"¡*º•¡º•ˇ.°'ϕ.ˇ¡π.ç∞¿μ¿.ç¡Δ-.y¡,ˇç*•¡•°.¿¡'•˛•¥ç¡çμ•¿,¿.ç.•Ωç¿ϕ¡'.ˇ°.ºÔq.¿π˘",
		"¡-*°Δ.•*.•#-˛¿ˇ'ç.¥¡ˇ¿¡π¿μ¡¿.çΔ',Ω.,°¡•˘.ˇ•Δ.¿,˘¡˛¡ç.μ•¥.•*∞Ô¡º-Δ¿˘ç°ϕ-**.",
		"¿¥¡ç.•.ç¡,.¿πˇ•.q'ˇ¡˘',º-ˇΔ.•ºΩ.#¡•μ*˘.°¥¿ç.-Δ'¡˛π˘.ç•¿*•,.ç.Ôqç,¿¡ç-",
		"º.•˘'¿'•.ˇμ˛ç,ˇ°ˇΔ¿¡*y,∞¿μ¿ç.˛Ωπ.-.,•¡'º•.çΔ¥yç°.ç¿,¿ç*Ô¡'•.πϕ-˘*•ç.•˛¡-.",
		"¿,.μç•Ω¡¿.º-μˇ•.^ˇ°∞¿π*ç'Δ•μ¡Ô˘ˇ.μ¥*μ°Δ.¿¡Ω•.-ˇ¿•.,πç˘º.-º*Δ¡*°•qÔ",
		"¡'.°'#¿˛*ˇ.˘πçˇ•Ω¡¿¥π.μˇ¿˘.Δº°μç°•.*'Δμ¡-˘¡¿-ç.,¿.¿*Δ.,•¡¿çπ*.-.",
		"¿˘,Ω'Ω.ç#ˇμ•'°μÔ¡¿˛*¥¡'.¿Δçπ*.•¥Ωq,˘∞.¿¿-*μ-*¥¡•μçμ¡•.,˛.¿μΔ°,.º-",
		"μπˇÔμ¡'μ.'•ϕ'¥-#y°Δμº-¡ˇ¿μ•ç.μ¡˛.•˘π¡Ô,μ¡°°μ'μ,¿,Δ-˛.Ωç•.¿¥μ.μçμ,-˘¿π",
		"¡q•.ÔΔ.°ˇ-,μˇ'μç•'¿μ-˘¡ˇ¡μ°.πΔ.•∞-μç*Ô˛˘¿.Ω¡¥°ˇ,•y.,Δ•¡˛-¡*,¿π,,μ.˛çΔ-",
		"°*˘•.'•.ˇ'-μ¿.ˇΩçyÔçˇ¡•˘¡°*',.Δº¿ç•çπ,¥˘•¿.∞-¡°˘*μ.Ω•.¿-˛qç,¡,•Δ˛¿˘-",
		"¿.•ˇ¥μçˇ,,'*ç'*°ˇ¡Δπ,*Ô¿'˛¿•.μy¡,ç•ºΩç-°¥,ˇ•¿Δ¿μ˛¡˘-¿•,¡*.πΔqç.º•,º*¿#",
		"¡˛.,μ°.¿çq˛çˇ,ç'¡μ•.¿'.,.qç˛¥Δçˇ˘*•,-∞•¿,˘¡qÔ¥.Δμ˛•.°'¿,-Δ^,•Ω",
		"¿*çμπçº•q˘°-˛¡ˇ-,,.μ#.'*ˇyº˛ϕ,Ôˇç,˘¡Δq˘¥.¿'ç˛¿,•º*º•Ω¿μ°,,çˇ˛.q¡¿Δπ",
		"¿Δμ.,ç¥¿-¡¿.˘.ç˛Δ¡¿•¡¿çy-.¿º*.¿*ç∞¿Δ'¡,μˇ•.'•,.ç¿-¿ˇº*ç*¡°.'¥π.çºΩ¡*˘˛",
		"¿Δ.#.,•¡¿çμμ¿ÔΔ•ç*Δ-μº*qμ°.-.¿Ω,çμ˛¿çˇ¥'¡μˇ•¡ˇ•.,π.'ç¥*Δ¿μyç.°'μ¡¿,-",
		"º*ºμ.•˛.°Ô¿μq¡ˇ.¥•˘.*,.¿-°,μπ¡*.¿•μ¿μç˛μ-°ç¥∞μç.•'¡ˇ,.-.'Δ.Ωq'Ô¿•°¡¿π.#,.",
		"¥.¿˛¡,ç-˘*yμ*.¿•Δ.ˇ,¿°çº-˘¡˛,ç¿¥-*ç¡μ.,Ω•.°πçq¿•ˇç¿Ô.ˇ¡'Ωˇμ.*Δ.ç'˛,¡¿,.",
		"¥-.*°'•.*π¡ºΩº.yç-.˛μ.¿¡ˇ¥μ,μç•,.˘.,.˛¡¿Δº.qç*ç¿°-.•*.¿,˘ºç,ç¡∞.Ω'.ˇ˘ˇ",
		"¡¿ÔΔ.#ç°•μçΩμç˛¿μμ¥ˇ*ç¿π*.*Δ.•¿μ,ç-'ç°-*•¡-Ωπ,¿¥¡çΔ°¿.Ôq*¿¿∞.•ˇ.˛",
		"¿çˇ¡q.¿,μ•'¿-,.°¡'-¡*º•˛ç.ˇ,,¿.μçΔΩπ.y¥μç°*¡˛¿,¥,'¥¡Δçμ-ç•*.^Ô°¿q•Ω.˛,˘μç",
		"¥ˇμ-ˇ∞*.'πΔ¡'*°q˘•ˇ.qΔ¥μ¡˘.*¥Ô¿μ˛Δç,.?μ˛¿.¡"
	};
	
	public static String printCipher(String nulls, boolean reverse, boolean revNgram) {
		String full = "";
		for (int k=reverse?cipher.length-1:0; reverse?k>=0:k<cipher.length;k+=reverse?-1:1) {
			String out = "";
			String line = cipher[k];
			for (int i=reverse?line.length()-1:0; reverse?i>=0:i<line.length(); i+=reverse?-1:1) {
				char ch1 = line.charAt(i);
				boolean print = true;
				for (int j=0; j<nulls.length(); j++) {
					char ch2 = nulls.charAt(j);
					if (ch1 == ch2)  {
						print = false;
						break;
					}
				}
				if (print) {
					out += ch1;
					full += ch1;
				}
			}
			System.out.println(out);
		}
		
		for (int n=1; n<12; n++) {
			for (int i=0; i<full.length()-n+1; i++) {
				String key = full.substring(i,i+n);
				Integer val = ngrams.get(key);
				if (val == null) val = 0;
				val++;
				ngrams.put(key, val);
				
				List<Integer> list = ngramsLoc.get(key);
				if (list == null) {
					list = new ArrayList<Integer>();
				}
				list.add(i);
				ngramsLoc.put(key, list);
				
				
				if (revNgram) {
					key = reverse(key);
					val = ngrams.get(key);
					if (val == null) val = 0;
					val++;
					ngrams.put(key, val);
				}
				
				char[] keyCh = key.toCharArray();
				Arrays.sort(keyCh);
				String keySorted = new String(keyCh);
				//System.out.println(key + "," + keySorted);
				val = ngramsTransposed.get(keySorted);
				if (val == null) val = 0;
				val++;
				ngramsTransposed.put(keySorted, val);
				List<String> index = ngramsTransposedList.get(keySorted);
				if (index == null) index = new ArrayList<String>();
				index.add(key);
				ngramsTransposedList.put(keySorted, index);
				
			}
		}
		return full;
	}
	public static String reverse(String s) {
	    return new StringBuffer(s).reverse().toString();
	}	
	public static void dumpNgrams(int n, boolean repeatsOnly) {
		for (String key : ngrams.keySet()) {
			if (key.length() != n) continue;
			Integer val = ngrams.get(key);
			if (!repeatsOnly || (repeatsOnly && val > 1)) {
				System.out.println(n + " " + key+" " + val);
				List<Integer> list = ngramsLoc.get(key);
				for (int i=1; i<list.size(); i++) {
					int d = list.get(i) - list.get(i-1);
					System.out.println(n + " distance from last [" + key + "]: " + d);
				}
			}
		}
		
		for (String key : ngramsTransposed.keySet()) {
			if (key.length() != n) continue;
			Integer val = ngramsTransposed.get(key);
			if (!repeatsOnly || (repeatsOnly && val > 1)) {
				System.out.println("transposed " + n + ", " + key+", " + val + ", " + toString(ngramsTransposedList.get(key)));
			}
			
		}
	}
	
	public static String toString(List<String> list) {
		String result = "";
		for (String s : list) result += "[" + s + "] ";
		return result;
	}
	
	public static String compute(String str) {
		return Stats.iocDiff(str) + ", " + Stats.chi2Diff(str) + ", " + Stats.entropyDiff(str);
	}
	
	public static void findMatch() {
		for (int i=0; i<cipherFull.length()-7; i++) {
			String word = cipherFull.substring(i,i+8);
			if (word.charAt(0) == word.charAt(3) && word.charAt(3) == word.charAt(5) &&
			    word.charAt(1) == word.charAt(4)) {
				System.out.println(i+": " + word);
			}
			
		}
	}
	
	/** compare ngrams of Tale to Fates */
	public static void testNgrams() {
		System.out.println("tale: " + tale.length());
		System.out.println("fates: " + cipherFull.length());
		for (int n=2; n<10; n++) {
			NGramsBean bean1 = new NGramsBean(n, tale);
			NGramsBean bean2 = new NGramsBean(n, cipherFull);
			System.out.println(" tale, " + bean1);
			System.out.println("fates, " + bean2);
			
			//if (n==5) bean1.dump();
		}
		
		for (int L=1; L<10; L++) {
			String rail = RailFence.encode(tale, L);
			NGramsBean bean = new NGramsBean(5, rail);
			System.out.println(" tale rail "+ L + ", " + bean);
		}
		for (int L=1; L<1500; L++) {
			String rail = RailFence.decode(cipherFullDallison, L);
			NGramsBean bean = new NGramsBean(7, rail);
			System.out.println(L + ", " + bean.repeats.size());
		}
	}
	
	/** test inline transpositions (reorderings of symbols within fixed-length groups of symbols */
	public static void testInline() {
		for (int L=2; L<13; L++) {
			System.out.println("L=" + L);
			boolean[] selected = new boolean[L];
			int[] cols = new int[L];
			
			testInline(selected, cols, 0);
		}
	}
	
	public static void testInline(boolean[] selected, int[] cols, int index) {
		if (index == cols.length) {

 			String pt = InlineTransposition.decode(pad(cipherFullDallison, cols.length) , cols);
			NGramsBean bean4 = new NGramsBean(4, pt);
			NGramsBean bean5 = new NGramsBean(5, pt);
			NGramsBean bean6 = new NGramsBean(6, pt);

			if (bean4.repeats.size() > 23 || bean5.repeats.size() > 2 || bean6.repeats.size() > 1)
				System.out.println(bean4.repeats.size() + ", " + bean5.repeats.size() + ", " + bean6.repeats.size() + ", " + ColumnarTranspositionRumkin.toString(cols));
			
			return;
		}
		for (int i=0; i<cols.length; i++) {
			if (selected[i]) continue;
			selected[i]=true;
			cols[index]=i;
			testInline(selected, cols, index+1);
			selected[i]=false;
		}
		
	}
	
	
	/** run a columnar transposition test to see if any configurations improve the ngram counts */
	public static void testCol() {
		for (int L=2; L<13; L++) {
			System.out.println("L=" + L);
			boolean[] selected = new boolean[L];
			int[] cols = new int[L];
			
			testCol(selected, cols, 0);
		}
	}

	public static String pad(String text, int m) {
		int rem = text.length() % m;
		if (rem == 0) return text;
		String result = text;
		for (int i=0; i<(m-rem); i++) result += " ";
		return result;
	}
	public static void testCol(boolean[] selected, int[] cols, int index) {
		if (index == cols.length) {

			String pt = ColumnarTranspositionRumkin.decode(cipherFull, cols);
			NGramsBean bean4 = new NGramsBean(4, pt);

			if (bean4.repeats.size() >= 30)
				System.out.println(bean4.repeats.size() + ", false, " + ColumnarTranspositionRumkin.toString(cols));

			if (cipherFull.length() % cols.length != 0) {
				pt = ColumnarTranspositionRumkin.decode(pad(cipherFull, cols.length), cols);
				bean4 = new NGramsBean(4, pt);
				if (bean4.repeats.size() >= 30)
					System.out.println(bean4.repeats.size() + ", true, " + ColumnarTranspositionRumkin.toString(cols));
			}
			
			return;
		}
		for (int i=1; i<=cols.length; i++) {
			if (selected[i-1]) continue;
			selected[i-1]=true;
			cols[index]=i;
			testCol(selected, cols, index+1);
			selected[i-1]=false;
		}
		
	}
	
	
	
	static void duh2(int n, String letter) {
		System.out.println(((float)n)/1501 +", " + letter);
	}
	
	static void duh(int n, String letter) {
		System.out.println(((float)n)/591194 +", " + letter);
	}
	
	/** perform columnar transposition brute force attack using every string of length L in the Fates corpus */
	// TODO: allow irregular columnar transposition
	public static void bruteCorpus(int L, boolean removeDupes) {
		String corpus = FileUtil.convert(FileUtil.loadFrom("/Users/doranchak/projects/zodiac/fates-original.txt"));
		System.out.println("Converted size: " + corpus.length() + " (" + (corpus.length()-L+1) + " strings of length " + L + ")");
		
		for (int i=0; i<corpus.length()-L+1; i++) {
			
			if (i>0) {
				int curr = (100*i)/(corpus.length()-L+1);
				int prev = (100*(i-1))/(corpus.length()-L+1);
				if (curr != prev && curr % 10 == 0) System.out.println("Progress: " + curr + "%...");
			}
			// two substrings: one with dupes, one without
			
			String sub1 = corpus.substring(i,i+L);
			String sub2 = "";
			
			if (removeDupes) {
				int j = i;
				Set<Character> seen = new HashSet<Character>();
				while (sub2.length() < L && j < corpus.length()) {
					char ch = corpus.charAt(j++);
					if (seen.contains(ch)) continue;
					seen.add(ch);
					sub2 += ch;
					//System.out.println(sub2);
				}
			}

			// sub1 has dupes, so we have two possible keys: one with dupes numbered in forward order, one backward	
			int[] colkeyForward = ColumnarTranspositionRumkin.columnarKeyFor(sub1, true);
			int[] colkeyBackward = ColumnarTranspositionRumkin.columnarKeyFor(sub1, false);
			int[] colkeyUniq = null;
			if (removeDupes) ColumnarTranspositionRumkin.columnarKeyFor(sub2, true);

			// unpadded ciphertext tests
			bruteCorpus(cipherFullDallison, sub1, colkeyForward, false, false, true);
			bruteCorpus(cipherFullDallison, sub1, colkeyBackward, false, false, false);
			if (removeDupes && !sub1.equals(sub2))
				bruteCorpus(cipherFullDallison, sub2, colkeyUniq, true, false, true);
			
			// padded ciphertext tests
			if (cipherFullDallison.length() % colkeyForward.length > 0) {
				String padded = pad(cipherFullDallison, colkeyForward.length);
				bruteCorpus(padded, sub1, colkeyForward, false, true, true);
				bruteCorpus(padded, sub1, colkeyBackward, false, true, false);
				if (removeDupes && !sub1.equals(sub2))
					bruteCorpus(padded, sub2, colkeyUniq, true, true, true);
			}
		}
	}
	
	static void bruteCorpus(String ciphertext, String key, int[] colkey, boolean keyDupesRemoved, boolean ciphertextPadded, boolean forward) {
		String decoded = ColumnarTranspositionRumkin.decode(ciphertext, colkey);
		NGramsBean bean = new NGramsBean(4, decoded);
		if (bean.repeats.size() >= 30) {
			System.out.println(bean.repeats.size() + ", " + colkey.length + ", " + keyDupesRemoved + ", " + ciphertextPadded + ", " + forward + ", " + key + ", " + ColumnarTranspositionRumkin.toString(colkey));
		}
	}
	
	public static void test19() {
		String pt;
		pt = ColumnarTranspositionRumkin.decode(cipherFullDallison, new int[] {4, 1, 15, 3, 14, 17, 10, 18, 6, 11, 2, 7, 12, 5, 8, 13, 9, 16, 19});
		NGramsBean bean;

		bean = new NGramsBean(4, tale);
		System.out.println(tale);
		System.out.println(bean);
		
		bean = new NGramsBean(4, cipherFullDallison);
		System.out.println(cipherFullDallison);
		System.out.println(bean);

		bean = new NGramsBean(4, pt);
		System.out.println(pt);
		System.out.println(bean);
		
		pt = ColumnarTranspositionRumkin.decode(cipherFullDallison, new int[] {5, 1, 16, 3, 14, 17, 13, 18, 9, 12, 2, 8, 11, 4, 7, 10, 6, 15, 19});
		bean = new NGramsBean(4, pt);
		System.out.println(pt);
		System.out.println(bean);

		String ct = ColumnarTranspositionRumkin.encode(tale, new int[] {4, 1, 15, 3, 14, 17, 10, 18, 6, 11, 2, 7, 12, 5, 8, 13, 9, 16, 19});
		bean = new NGramsBean(4, ct);
		System.out.println(ct);
		System.out.println(bean);

		ct = ColumnarTranspositionRumkin.encode(tale, new int[] {5, 1, 16, 3, 14, 17, 13, 18, 9, 12, 2, 8, 11, 4, 7, 10, 6, 15, 19});
		bean = new NGramsBean(4, ct);
		System.out.println(ct);
		System.out.println(bean);
		
	}
	
	public static void test17() {
		String corpus = FileUtil.convert(FileUtil.loadFrom("/Users/doranchak/projects/zodiac/fates-original.txt"));
		for (int i=0; i<corpus.length()-16; i++) {
			String sub = corpus.substring(i,i+17);
			NGramsBean bean = new NGramsBean(1, sub);
			if (bean.counts.size() >= 14) {
				System.out.println(i + ", " + sub + ", " + bean.counts.size());
			}
		}
		System.out.println(corpus.length()-16);
	}
	
	/** try measuring IoC for all key periods */
	static void testIoc() {
		Set<Character> all = new HashSet<Character>();
		for (char ch : cipherFullDallison.toCharArray()) all.add(ch);
		
		for (Character ch : all) {
			StringBuffer cipher = new StringBuffer();
			for (char c : cipherFullDallison.toCharArray()) {
				if (c != ch) cipher.append(c);
			}
			System.out.println(ch + ", ioc " + Stats.ioc(cipher) + ", diff " + Math.abs(Stats.ioc(cipher)-Stats.ENGLISH_IOC));
			for (int n=1; n<50; n++) {
				double diffSum = 0; int num = 0;
				for (int start=0; start < n; start++) {
					int i=start;
					StringBuffer sb = new StringBuffer();
					while (i<cipher.length()) {
						sb.append(cipher.charAt(i));
						i+=n;
					}
					double ioc = Stats.ioc(sb); 
					diffSum += Math.abs(ioc-Stats.ENGLISH_IOC);
					num++;
				}
				System.out.println(ch+", " + cipher.length() + ", " + n + ", " + diffSum + ", " + (diffSum/num));
			}
		}
	}
	/** test IOC of all 1501-character windows of Tale of Two Cities */
	static void testIocTale() {
		String tale = FileUtil.convert(FileUtil.loadFrom("/Users/doranchak/Desktop/tale.txt"));
		int total = 0; int bad = 0;
		for (int i=0; i<tale.length()-1500; i++) {
			String chunk = tale.substring(i,i+1501);
			double d = Stats.iocDiff(chunk);
			if (d >= 0.005452754807461685) {
				System.out.println(d + ", " + i + ", " + chunk);
				bad++;
			}
			total++;
		}
		System.out.println("bad " + bad + " total " + total + " (" + 100*((float)bad)/total + ")");
	}
	
	public static void main(String[] args) {
		//testCol();
		//System.out.println(Stats.ioc(new StringBuffer(cipherFullDallison)));
		//System.out.println(Stats.ioc(new StringBuffer(taleNoPeriods)) + ", " + Math.abs(Stats.ENGLISH_IOC-Stats.ioc(new StringBuffer(taleNoPeriods))));
		//testIoc();
		testIocTale();
		//test17();
		//testInline();
		//for (int L=54; L<100; L++) bruteCorpus(L, false);
		
		//testNgrams();
		//String full;
		//findMatch();
		//full = printCipher("", false, false);
		//dumpNgrams(10, true);
		
		//System.out.println("Smeg " + Stats.ioc(new StringBuffer(full)));
		//dumpNgrams(3, true);
		
		/*System.out.println("none, " + compute(full));
		String symbols = ".¿ç¡•,μ*-ˇ'Δ°˛˘¥ºπΩqÔ∞yϕ#^?";
		
		for (int i=0; i<symbols.length()-1; i++) {
				String nulls = "" + symbols.charAt(i);
				full = printCipher(nulls, false, false);
				System.out.println(nulls + " removed: " + Stats.ioc(new StringBuffer(full)));
		}*/
		
	}
	
}
