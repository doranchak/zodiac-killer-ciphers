package com.zodiackillerciphers.lucene;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import com.zodiackillerciphers.lucene.BestMatches;
import com.zodiackillerciphers.lucene.Edge;
import com.zodiackillerciphers.lucene.LuceneVectorIndividual;
import com.zodiackillerciphers.lucene.Match;
import com.zodiackillerciphers.lucene.NGrams;
import com.zodiackillerciphers.lucene.PositionMatches;
import com.zodiackillerciphers.lucene.ProblemLucene;
import com.zodiackillerciphers.lucene.Settings;
import com.zodiackillerciphers.lucene.Stats;

import com.zodiackillerciphers.ciphers.Ciphers;

import ec.multiobjective.MultiObjectiveStatistics;
import ec.util.MersenneTwisterFast;
import ec.vector.IntegerVectorSpecies;

public class Scorer {
	
	static int[][] wordSymbolCounts;
	public static Map<Character, Integer> symbolCounts;
	
	
	/** tells lucene to sort results in descending order of score field */
	public static Sort sortScore = new Sort(new SortField("score", SortField.FLOAT, true));
	public static Sort freqScore = new Sort(new SortField("texts", SortField.INT, true));
	
	public static String alphabet = Ciphers.alphabet(Settings.cipher.toString());
	static {
		System.out.println("alphabet [" + alphabet + "], size [" + alphabet.length() + "]");
		initSymbolCounts(Settings.cipher);
	}
	
	public static Map<Character, Character> decoderMap(StringBuffer key) {
		Map<Character, Character> map = new HashMap<Character, Character>(alphabet.length());
		for (int i=0; i<key.length(); i++) {
			map.put(alphabet.charAt(i), key.charAt(i));
		}
		return map;
	}

	/** get genome position based on symbol */
	public static int genomePosFor(String symbol) {
		for (int i=0; i<alphabet.length(); i++) 
			if (alphabet.substring(i,i+1).equals(symbol)) return i;
		return -1;
	}
	
	/** get decoded plaintext using the given key map */
	public static StringBuffer decode(Map<Character, Character> key) {
		StringBuffer decoded = new StringBuffer(Settings.cipher.length());
		for (int i=0; i<Settings.cipher.length(); i++) decoded.append(key.get(Settings.cipher.charAt(i)));
		return decoded;
	}
	
	/** use genome to obtain decoded plaintext */
	public static StringBuffer decode(int[] genome) {
		StringBuffer decoder = ProblemLucene.keyFrom(genome);
		Map<Character, Character> dm = Scorer.decoderMap(decoder);
		return decode(dm);
	}
	
	public static int numWild(String word) {
		int n = 0;
		for (int i=0; i<word.length(); i++) if (word.charAt(i) == '?') n++;
		return n;
	}
	
	public static void initWordSymbolCounts(StringBuffer cipher) {
		wordSymbolCounts = new int[cipher.length()][Settings.L_MAX-Settings.L_MIN+1];
		for (int i=0; i<cipher.length(); i++) {
			for (int L=Settings.L_MIN; L<=Settings.L_MAX; L++) {
				Set<Character> set = new HashSet<Character>();
				for (int j=0; j<L && i+j<cipher.length(); j++) {
					set.add(cipher.charAt(i+j));
				}
				wordSymbolCounts[i][L-Settings.L_MIN]=set.size();
			}
		}
	}
	public static void initSymbolCounts(StringBuffer cipher) {
		symbolCounts = new HashMap<Character, Integer>();
		for (int i=0; i<cipher.length(); i++) {
			char c = cipher.charAt(i);
			Integer val = symbolCounts.get(c);
			if (val == null) val = 0;
			val++;
			symbolCounts.put(c, val);
		}
	}
	
	public static int wordSymbolCount(int pos, int L) {
		if (pos >= wordSymbolCounts.length) return -1;
		if (L-Settings.L_MIN >= wordSymbolCounts[pos].length) return -1;
		return wordSymbolCounts[pos][L-Settings.L_MIN];
	}
	public static void testWordSymbolCounts() {
		StringBuffer sb = new StringBuffer(Ciphers.cipher[1].cipher);
		initWordSymbolCounts(sb);
		
		for (int i=0; i<sb.length(); i++) {
			for (int L=Settings.L_MIN; L<=Settings.L_MAX; L++) {
				String word = sb.substring(i, Math.min(sb.length(), i+L));
				System.out.println(i + "," + L + "," + word + ": " + wordSymbolCount(i, L));
			}
		}
		
	}
	
	/** returns lucene queries.  first item: full match query.  2nd item: wildcard query. */
	public static String[] queriesFor(StringBuffer decoded, int pos, boolean zodiac) {
		StringBuffer q1 = new StringBuffer();
		StringBuffer q2 = new StringBuffer();
		
		for (int L=Settings.L_MIN; L<=Settings.L_MAX; L++) {
			if (pos+L >= decoded.length()) break;
			String word = decoded.substring(pos, pos+L);
			if (word.startsWith("?")) break; // don't allow wildcard prefixes
			if (word.contains("?")) { // at least one wildcard present
				if (numWild(word) > word.length()/2) continue; // don't allow TOO many wildcards
				if (q2.length() == 0) q2.append("+word:(");
				q2.append(word).append(" ");
			} else {
				if (q1.length() == 0) q1.append("+word:(");
				q1.append("word:").append(word).append(" ");
			}
		}
		if (q1.length() > 0) q1.append(")");
		if (q2.length() > 0) q2.append(")");
		if (zodiac) {
			if (q1.length() > 0) q1.append(" +zodiac:true");
			if (q2.length() > 0) q2.append(" +zodiac:true");
		}
		say("q1 " + q1);
		say("q2 " + q2);
		return new String[] {q1.toString(), q2.toString()};
	}
	
	/** construct a small set of single queries to find words in the given string */
	public static List<Match> words(StringBuffer sb) {
/*			String[] queries = queriesFor(decoded, i, zodiac);
			List<Match> matches1 = null, matches2 = null;
			if (queries[0].length() > 0) {
				long n1 = System.nanoTime();
				matches1 = Match.matchesFrom(LuceneService.query(queries[0].toString(), sortScore, 10).docs, i, false);
*/
		StringBuffer query = new StringBuffer();
		query.append("+word:(");
		int clauseCount = 0;
		List<Match> results = new ArrayList<Match>();
		for (int i=0; i<sb.length()-Settings.L_MIN; i++) {
			for (int L=Settings.L_MIN; L<=Settings.L_MAX; L++) {
				if (i+L > sb.length()) break;
				String chunk = sb.substring(i, i+L);
				if (chunk.contains("?")) continue; // avoid wildcards
				query.append(sb.substring(i, i+L));
				query.append(" ");
				clauseCount++;
				if (clauseCount == 1000) {
					query.append(")");
					//System.out.println(query);
					results.addAll(Match.matchesFrom(LuceneService.query(query.toString(), sortScore, 1000).docs, -1, false));
					clauseCount = 0;
					query = new StringBuffer("+word:(");
				}
			}
		}
		if (clauseCount > 0) {
			query.append(")");
			//System.out.println(query);
			results.addAll(Match.matchesFrom(LuceneService.query(query.toString(), sortScore, 1000).docs, -1, false));
		}
		//System.out.println(results.size());
		return results;
	}
	
	/** simple score for given list of words.  rewards long words more. */
	public static float scoreWords(List<Match> words) {
		Map<Integer, Float> sums = new HashMap<Integer, Float>();
		for (int L=Settings.L_MIN; L<=Settings.L_MAX; L++) sums.put(L, 0f);
		for (Match m : words) {
			Float val = sums.get(m.word.length());
			val += m.score;
			sums.put(m.word.length(), val);
		}
		float result = 0;
		for (int L=Settings.L_MIN; L<=Settings.L_MAX; L++) {
			say("sums L " + L + " val " + sums.get(L));
			result += sums.get(L) / Math.pow(2,Settings.L_MAX-L);
		}
		return result;
		
	}

	/** return score and best matches for given decoding */
	public static BestMatches score(StringBuffer decoded, boolean zodiac) {
		Map<Integer, PositionMatches> map = bestMatches(decoded, zodiac);
		conflicts(map, decoded);
		BestMatches bm = best(map, decoded);
		//say("sum " + bm.sum);
		return bm;
		
	}
	public static Map<Integer, PositionMatches> bestMatches(StringBuffer decoded, boolean zodiac) {
		say("bestMatches for [" + decoded + "]");
		Map<Integer, PositionMatches> words = new HashMap<Integer, PositionMatches>();
	
		/* 
		 *	for each position of cipher text
		 *		construct lucene queries for words of lengths 4 through 10.
		 */
		
		for (int i=0; i<decoded.length(); i++) {
			say("bestMatches for " + i);
			String[] queries = queriesFor(decoded, i, zodiac);
			List<Match> matches1 = null, matches2 = null;
			if (queries[0].length() > 0) {
				long n1 = System.nanoTime();
				matches1 = Match.matchesFrom(LuceneService.query(queries[0].toString(), sortScore, 10).docs, i, false);
				long n2 = System.nanoTime();
				say((n2-n1) + " for " + queries[0]);
			}
			if (queries[1].length() > 0) {
				long n1 = System.nanoTime();
				matches2 = Match.matchesFrom(LuceneService.query(queries[1].toString(), sortScore, 10).docs, i, false);
				long n2 = System.nanoTime();
				say((n2-n1) + " for " + queries[1]);
			}
			dumpMatches(matches1, matches2);
			words.put(i, new PositionMatches(matches1, matches2));
		}
		
		
		 /*
		 *		if a word of length L contains a wildcard, put the results in a queue for conflict analysis
		 *		otherwise, put the results in a "full match" queue
		 *		fetch top 10 scoring words for each query
		 *		store cipher position with each word match
		 *		convert lucene document into word match data
		 *		associate position with best-scoring word from the full match queue, then remove the word
		 *			keep picking best-scoring words if the found word is no longer relevant to the current position
		 *		position score = the score of the best word
		 *		repeat the above for the zodiac dictionary to compute a zodiac-specific score for the position
		 *		
		 *	lucene queries
		 *		non-wildcard searches are simple:  abc def ghij
		 *		wildcard searches might require pattern matches if they have repeated symbols:
		 *			(+word:ki?? +patterns:aaXX) or (+word:a???? +patterns:aXXaa +patterns:XaaXa)
		 *		don't mix non-wildcard searches with wildcard searches
		 *		don't do wildcard search unless the word segment contains at least S/2 plaintext letter assignments already, where S is the number of distinct ciphertext 
		 *			symbols in it
		 *		don't do wildcard searches that are prefixed by wildcard
		 *
		 *	merge the two sets of words together, and for each position, choose the best scoring word.
		 *		full matches are assigned the full score
		 *		wildcard matches are assigned the word score multiplied by the ratio of distinct non-wildcards to wildcards
		 *
		 *
		 *		
		 * 		
		 */
		
		return words;
	}
	
	public static void conflicts(Map<Integer, PositionMatches> words, StringBuffer decoded) {
		Map<Character, Map<Character, Set<Match>>> map = new HashMap<Character, Map<Character, Set<Match>>>(); 
		
		PriorityQueue<Edge> pq = new PriorityQueue<Edge>(100, Edge.comparatorMin); 
		for (Integer i : words.keySet()) {
			PositionMatches pm = words.get(i);
			if (pm.matches2 == null || pm.matches2.size() == 0) continue;
			for (Match m : pm.matches2) {
				adjust(m, decoded); // re-score based on # of wildcards
				String word = m.word;
				for (int j=m.position; j<m.position+word.length(); j++) {
					Character chWord = word.charAt(j-m.position);
					Character chCipher = Settings.cipher.charAt(j);
					Character chDecoded = decoded.charAt(j);
					say("chWord " + chWord + " chCipher " + chCipher + " chDecoded " + chDecoded);
					if (chDecoded.equals('?')) { // decoded cipher text has wildcard at this position
						say("wildcard");
						Map<Character, Set<Match>> map2 = map.get(chCipher); // map of candidate decodings for this symbol
						if (map2 == null) map2 = new HashMap<Character, Set<Match>>();
						Set<Match> set = map2.get(chWord); // list of words that decode the symbol to this plaintext letter
						if (set == null) set = new HashSet<Match>();
						set.add(m);
						
						// add all conflicting words to priority queue of edges
						for (Character ch : map2.keySet()) {
							if (ch.equals(chWord)) continue; // not a conflict, since same plaintext assignment
							Set<Match> matches = map2.get(ch);
							for (Match m2 : matches) {
								Edge edge = new Edge(m, m2);
								edge.c = chCipher; 
								edge.p1 = chWord;
								edge.p2 = ch;
								pq.add(edge);
								say("added " + edge + " to conflict queue ");
							}
						}
						
						map2.put(chWord, set);
						map.put(chCipher, map2);
						
						say("sizes " + map.size() + ", " + map2.size() + ", " + set.size());
					}
				}
			}
		}
		
		//Map<Character, Map<Character, Set<Match>>> map = new HashMap<Character, Map<Character, Set<Match>>>();
		say("Dumping map...");
		for (Character c1 : map.keySet()) {
			say("c1 " + c1);
			for (Character c2 : map.get(c1).keySet()) {
				say(" - c2 " + c2);
				for (Match m : map.get(c1).get(c2)) {
					say("    - match " + m);
				}
			}
		}
		
		say("processing edge queue");
		while (!pq.isEmpty()) {
			Edge e = pq.poll();
			say("got edge " + e);
			if (e.m1.deleted || e.m2.deleted) continue; // the edge was already deleted, so conflict no longer exists here
			e.m1.deleted = true;
			e.m2.deleted = true;
		}
		
		for (Integer i : words.keySet()) {
			PositionMatches pm = words.get(i);
			if (pm.matches2 == null || pm.matches2.size() == 0) continue;
			for (Match m : pm.matches2) {
				say("match: " + m + ", " + m.deleted);
			}
		}

		say("which words are left:");
		for (Integer i : words.keySet()) {
			PositionMatches pm = words.get(i);
			if (pm.matches2 == null || pm.matches2.size() == 0) continue;
			for (Match m : pm.matches2) {
				if (!m.deleted) {
					String s = "";
					for (i=0; i<m.position; i++) s += " ";
					say(s + m);
				}
			}
		}
		
	}
	
	/** go through each position and pick the best word.  return the sum of scores for all best words, and list of beat matches */
	public static BestMatches best(Map<Integer, PositionMatches> words, StringBuffer decoded) {
		boolean bad = false;
		BestMatches bm = new BestMatches();
		Set<String> seen = new HashSet<String>(); // don't count the same word more than once

		bm.cover = new boolean[decoded.length()];
		float[][] covermax = new float[2][decoded.length()];
		//float[] covermax2 = new float[decoded.length()];
		//float sum = 0;
		List<Match> candidates = new ArrayList<Match>();
		for (Integer pos : words.keySet()) {
			
			float max = 0;
			Match best = null;
			for (int i=candidates.size()-1; i>=0; i--) {
				// remove candidate if it doesn't cover this position.
				Match m = candidates.get(i);
				if (pos > m.position+m.word.length()) {
					say(m + " no longer covers.  removing.");
					candidates.remove(i);
				} else if (seen.contains(m.word)) {
						say(m + " aleady counted.  removing.");
						candidates.remove(i);
				} else {
					if (score(m) > max) {
						max = score(m);
						best = m;
					}
				}
			}
				
				PositionMatches pm = words.get(pos);
				if (pm.matches1 != null) for (Match m1 : pm.matches1) {
					if (!seen.contains(m1.word)) {
						candidates.add(m1);
						if (score(m1) > max) {
							max = score(m1);
							best = m1;
						}
						
					}
				}
				if (pm.matches2 != null) for (Match m2 : pm.matches2) {
					if (!seen.contains(m2.word) && !m2.deleted) {
						candidates.add(m2);
						if (score(m2) > max) {
							max = score(m2);
							best = m2;
						}
					}
				}
				
			
			if (best != null) {
				//bm.sum += max;
				//int notcovered = 0;
				bm.matches.add(best);
				seen.add(best.word);
				for (int i=0; i<best.word.length(); i++) {
					
					say("pos " + pos + " i " + i);
					if (best.position+i < bm.cover.length) {
						if (!bm.cover[best.position+i]) {
							// count new positions
							//notcovered++;
							say("setting cover at " + (best.position+i) + " for word " + best.word);
							bm.cover[best.position+i] = true;
						}
						
					}
					else  {
						System.out.println("bad pos " + best.position + " i " + i + " best " + best + " decoded " + decoded);
						bad = true;
					}
					int wc = best.word.length() < 6 ? 0 : 1; 
					covermax[wc][i+best.position] = Math.max(max/best.word.length(), covermax[wc][i+best.position]);
				}
				//System.out.println("max " + max + " notcov " + notcovered + " best len " + best.word.length() + " word " + best.word);
				//bm.sum += max* ((float) notcovered/best.word.length());
				//System.out.println("new max "+ bm.sum);
				if (bad) {
					bm.sums[0] = 0;
					bm.sums[1] = 0;
				}
				say("pos " + pos + " best: " + best);
			}
			
		}
		for (int i=0; i<covermax[0].length; i++) bm.sums[0] += covermax[0][i]; 
		for (int i=0; i<covermax[1].length; i++) bm.sums[1] += covermax[1][i];
		
		/* compute median of all covermax scores */
		float[] sorted = new float[covermax[0].length];
		for (int i=0; i<covermax[0].length; i++) sorted[i] = Math.max(covermax[0][i], covermax[1][i]);
		Arrays.sort(sorted);
		//for (int i=0; i<sorted.length; i++) System.out.println("FUCK " + sorted[i]);
		bm.medianScore = sorted[sorted.length/2];
		//System.out.println("FACK " + bm.medianScore);
		/*for (int i=0; i<covermax.length; i++) {
			for (int j=0; j<covermax[i].length; j++) {
				System.out.println("fuck " + i + " " + j + " " + covermax[i][j]);
			}
		}*/
		
		int c = 0;
		//String shit = "";
		for (int i=0; i<bm.cover.length; i++) {
			if (bm.cover[i]) {
				c++; 
				//shit += "X";
			} //else shit += "_";
		}
		//System.out.println(shit);
		bm.coverage = ((float)c)/decoded.length();
		return bm;
	}
	
	public static float score(Match m) {
		//System.out.println("score for word " + m.word + " is " + Math.pow(m.word.length()-L_MIN+1, 2));
		return (float) Math.pow(m.word.length()-Settings.L_MIN+1, 2);
	}

	public static float score(List<Match> list) {
		float sum = 0;
		
		/** repeated words get reduced scores */
		Map<String, Integer> wordCounts = new HashMap<String, Integer>(); 
		
		float[] max = new float[Settings.cipher.length()]; // track contribution of largest word found that covers each position
		for (Match m : list) {
			//System.out.println("word " + m.word + " pos " + m.position);
			Integer count = wordCounts.get(m.word);
			if (count == null) count = 0;
			count++;
			wordCounts.put(m.word, count);
			float factor = (float) Math.pow(2, count-1); // 1st occurrence: full score, 2nd: score/2, 3rd: score/4, 4th: score/8, etc
			
			for (int i=0; i<m.word.length(); i++) {
				int pos = m.position + i;
				float score = score(m)/m.word.length()/factor; // divide up score per letter, and adjust based on # of repetitions
				if (pos >= max.length) continue;
				if (score > max[pos]) {
					//System.out.println("new best at " + pos + ", score " + score + " which beats " + max[pos]);
					max[pos] = Math.max(max[pos], score); 
				} //else System.out.println("already had max " + max[pos] + " which beats " + score);
			}
			//sum += score(m);
		}
		for (int i=0; i<max.length; i++) sum += max[i];
		return sum;
	}
	
	
	/** recomputes score for this match based on ratio of distinct non-wildcard symbols to wildcard symbols */ 
	public static void adjust(Match m, StringBuffer decoded) {
		int symCount = wordSymbolCount(m.position, m.word.length());
		Set<Character> set = new HashSet<Character>();
		for (int i=0; i<m.word.length(); i++) {
			if (decoded.charAt(m.position+i) == '?') {
				set.add(Settings.cipher.charAt(m.position+i));
			}
		}
		float oldscore = m.score;
		m.score *= (float)(symCount - set.size())/symCount;
		say("word [" + m.word + "] for decoding [" + decoded.substring(m.position,m.position+m.word.length()) + "] for cipher [" + Settings.cipher.substring(m.position,m.position+m.word.length()) + "] was [" + oldscore + "] now [" + m.score + "]");
	}
	
	public static void testScore() {
		LuceneService.init();
		initWordSymbolCounts(new StringBuffer(Ciphers.cipher[1].cipher));
		
		
		StringBuffer decoded = new StringBuffer("ilikekillingpeoplebecauseitissomuchfunitismorefunthankillingwildgameintheforrestbecausemanisthemoatdangertueanamalofalltokillsomethinggivesmethemoatthrillingexperenceitisevenbetterthangettingyourrocksoffwithagirlthebestpartofitiathaewhenidieiwillbereborninparadicesndalltheihavekilledwillbecomemyslavesiwillnotgiveyoumynamebecauseyouwilltrytosloidownorstopmycollectingofslavesformyafterlifeebeorietemethhpiti");
		dump(score(decoded, false), decoded);
		decoded = new StringBuffer("il????il?in?pe?pl?be?au?ei?is?om?ch?un?ti?mo?ef?nt?an?il?in?wi?dg?me?nt?ef?rr?st?ec?us?ma?is?he?oa?da?ge?tu?an?ma?of?ll?ok?ll?om?th?ng?iv?sm?th?mo?tt?ri?li?ge?pe?en?ei?is?ve?be?te?th?ng?tt?ng?ou?ro?ks?ff?it?ag?rl?he?es?pa?to?it?at?ae?he?id?ei?il?be?eb?rn?np?ra?ic?sn?al?th?ih?ve?il?ed?il?be?om?my?la?es?wi?ln?tg?ve?ou?yn?me?ec?us?yo?wi?lt?yt?sl?id?wn?rs?op?yc?ll?ct?ng?fs?av?sf?rm?af?er?if?eb?or?et?me?hh?it?");
		dump(score(decoded, false), decoded);

		//decoded = new StringBuffer("ilikekillingpeoplebecauseitissomuchfunitismorefunthankillingwildgameintheforrestbecausemanisthemoatdangertueanamalofalltokillsomethinggivesmethemoatthrillingexperenceitisevenbetterthangettingyourrocksoffwithagirlthebestpartofitiathaewhenidieiwillbereborninparadicesndalltheihavekilledwillbecomemyslavesiwillnotgiveyoumynamebecauseyouwilltrytosloidownorstopmycollectingofslavesformyafterlifeebeorietemethhpiti");
		//System.out.println(score(decoded, true));
		decoded = new StringBuffer("il????il?in?pe?pl?be?au?ei?is?om?ch?un?ti?mo?ef?nt?an?il?in?wi?dg?me?nt?ef?rr?st?ec?us?ma?is?he?oa?da?ge?tu?an?ma?of?ll?ok?ll?om?th?ng?iv?sm?th?mo?tt?ri?li?ge?pe?en?ei?is?ve?be?te?th?ng?tt?ng?ou?ro?ks?ff?it?ag?rl?he?es?pa?to?it?at?ae?he?id?ei?il?be?eb?rn?np?ra?ic?sn?al?th?ih?ve?il?ed?il?be?om?my?la?es?wi?ln?tg?ve?ou?yn?me?ec?us?yo?wi?lt?yt?sl?id?wn?rs?op?yc?ll?ct?ng?fs?av?sf?rm?af?er?if?eb?or?et?me?hh?it?");
		dump(score(decoded, true), decoded);
		
	}
	
	public static void testScore2() {
		LuceneService.init();
		initWordSymbolCounts(Settings.cipher);
		
		//StringBuffer decoded = new StringBuffer("?w??d?t??d?n?cx????e??bzvl??d?e?nt?g?j??n?"); //"llingwildgameintheforrestbecausemanisthem");
		System.out.println("for string ["+Settings.cipdec+"]");
		dump(score(Settings.cipdec, false), Settings.cipdec);
		System.out.println("for string ["+Settings.zodiacsolution+"]");
		dump(score(Settings.zodiacsolution, false), Settings.zodiacsolution);
		//dump(score(decoded, true), decoded);
	}
	
	public static void testWords() {
		Settings.DEBUG = true;
		LuceneService.init();
		System.out.println("for string ["+Settings.zodiacsolution+"]");
		List<Match> words = words(Settings.zodiacsolution);
		dumpMatches(words, null);
		System.out.println(scoreWords(words));

		
		StringBuffer sb = new StringBuffer("shtesirreshteltdtyetoogdterldcofcshtsrglateshedeshdgtrshitdhlyskbtwisterhdgthisthatlydsrsofseedltilingsorgtrdbghiliglingsolersafftchiygfetdjsitlotltertelrleslitypdgrebrditlosefdrelrsethligksofillttleselrgridthjdbgdtehtetsisovdlgansdtofdrhtghldsdbtdilijchesoerdreisgdrdtatderihjsthitsibitbdersjchrtdthsdpsprotisshojosbtjeexptrtiyleitltsbijte");
		System.out.println("for string ["+sb+"]");
		words = words(sb);
		dumpMatches(words, null);
		System.out.println(scoreWords(words));

		
		Settings.DEBUG = false;

		System.out.println("for string ["+Settings.cipdec+"]");
		words = words(Settings.cipdec);
		dumpMatches(words, null);
		System.out.println(scoreWords(words));

		String[] test = new String[] {
				"ene?m?u?nhp?b?tb?rh?fk??eegenit??f????u?h??uar???mer??e??ep?sun????nh???m?h?u??ghrfo?n??k?ei?ee?tomrr??ra??n?po?k?u?r???h?e??nt?ng?u???hhmi???er?tom???e??e????beur?fnugh?mh?phr?m?a?e???eg?e???u??uhf?nt??sem?o?uan?erhnigbk??t?hmeo???rsem?eru?hse??hru?huape?br???ufe??rknn??rherhn?ennm?se??h?ft?r????oh?nese?npum?uh??h????r?rhmfk?in?u?sen??a?gt??terts?hu??tb??fh??efmep?t??nkhmn?ua??o?????e?rehnt?er?e?ng?ebu?h",
				"?e????x?e?l??au??nirg?o?e?d??n?sogukocx??qsmer?otgt?w??i??l?ixeh??si?c?u?ki?wa?ding?o?rs?t?n?tesu?g??w?re?oi?l?s??m??i??i??i??usiduxc?????nsa?tns??g?u??i??t?re?ewrwgixd????alin?gre?t?c?ed??t?amo?wig??uk?i?gu??xee?triind?????k?g???u?rit?w??xa?i?i?inwrimel?c????hxgeqt??ee?ur?t??i??ee?hi?i?iagusnsa?i??r??i??elmg?x?raiosac?sni?g?oniamoi?ei?ead???u??uitiwq???sagiiiegg?l?u??e????kmesa?k?r????neiiu??n?esidut?x??",
				"?c?omomichq?t??tibh?okt?o?g?pifltoirt?mihsl?aytto?s??o??i?q?smcb?rloh??imre????ghbo?tp?lko?iisol???r???yaitorq?lki?t??i?eo??ip?logim???hmmil?isblf???i???i?o???to?y?oomgh?mm?qhbi??a?sr??ogi?o???t??eoop?rts??i??mac?syhoigtk?ifrh????iryssm??rm?hs??ihb??h?aq??t??rbmoosorkcciiyhs?moo?ccmbs??ih?o?lbl????m?p?s?icq???mm??etl???lbhmoktio??ts?c??a?gf?i??r?soe?s?ftl?oe??oo??q??t?ckmmpr?al??ri??i?tboho???biologistmih",
				"rogooohcozview?ecnts???t?rogien???re??htzs?md?i?r????orhcgviyhotia?tz?aroeilwwtotn???is??rret?????????i?dt?tav???cmi?hcaioghci??torh?iizpoe?wt?n?n??arlrhcgrisee?w???thoztopwvtnt?sda?a?i?otrriam?lwi?oi?eiyg?r?ihdoa??tteoe?ltnez?r?ara?y?o?g?hwzyrhctnwstmdvg?e?lath??sr??ootr?z??ptoroootyghctw???n?ath?psiryrcovm?ihpsai??a???nto???etam?yrohadaontc?r??yriwsane?a?ihh???gvi?ito?poiemd?a?etslcgin?tt?lrnt??tor?ehtz",
				"ieaerehleogdteetlrer?anaeiya?vn?n?k?nohnoc???oon?gthweiilagdihe?dh??oowkr?sg?eayer?mn?r?a?ivnte?emg?hwdo?nn?hgm?al?ohilwseail?e??ykhoddosrv?entr?nmgwkgiila?dr?te?ow??hyoarsegerngr?wthodeyni?dp?ng?s?e?e?oiagkmdh?ewtoe?vytagnn?ogimwkhoitrwa?heoiiiler?re??gaothgh?h?ec??aeenkooths?eieer?iailee?e?r?paimsr?iiileg?gdhsrpsn?poh?rer?anv?p?niieiw?pynalei?ei?s?cwnt?p?siie?gagdeoaeasr?????pm?nrglaoree?egirne??yktthno",
				"ieaerehleogdteetlrer?anaeiya?vn?n?k?nohnoc???oon?gthweiilagdihe?dh??oowkr?sg?eayer?mn?r?a?ivnte?emg?hwdo?nn?hgm?al?ohilwseail?e??ykhoddosrv?entr?nmgwkgiila?dr?te?ow??hyoarsegerngr?wthodeyni?dp?ng?s?e?e?oiagkmdh?ewtoe?vytagnn?ogimwkhoitrwa?heoiiiler?re??gaothgh?h?ec??aeenkooths?eieer?iailee?e?r?paimsr?iiileg?gdhsrpsn?poh?rer?anv?p?niieiw?pynalei?ei?s?cwnt?p?siie?gagdeoaeasr?????pm?nrglaoree?egirne??yktthno",
				"?ogslsweohla??n?esal???????gnede??ue??wrhienari?obhsos?heglaawoga?esh??ulee?i???as?t?nle?o?erh?entbrsoarar?s?lte?enishe?esghennes?uw?aahmlee?rhsedtb?u??hegoal???iro?sw?h?lm?lasrbla?h??a??r?oa?n??ie?snneiagbutawao?hrase????rdehb?t?u?rahlogrw?ha?heasilanalg??s??gw??ior?oorurhhsmss?oolgaghea??nese??htmln?a?eolnbawml?e?e??sesal???es?n?a?oh?a??d?en?rnaoeii?d?e??ehh??bglani?o?mlnenae?terl?egis?asn??sr?es?uh?wrh",
				"ieaerehleogdteetlrer?anaeiyadvn?n?k?nohnoc?e?oon?gthweiilagdihe?dh??oowkr?sg?eayer?mndr?a?ivnte?emg?hwdo?nn?hgm?aleohilwseailde??ykhoddosrv?entr?nmgwkgiila?dr?te?ow??hyoarsegerngr?wthodeyni?dpeng?s?ede?oiagkmdh?ewtoe?vytagnn?ogimwkhoitrwa?heoiiiler?ree?gaothgh?h?ec??aeenkooths?eieer?iailee?e?r?paimsrdiiilegegdhsrpsn?poh?rer?anv?peniieiw?pynalei?ei?s?cwnt?p?siie?gagdeoaeasrd?e??pm?nrglaoree?egirne??yktthno",
				"tcnoto?echlo??e?ere?iu?cvten?ena?i?a?m?ih?aadt??cgln?otienlo??c?ooashmw?taphr?ceerif???aucteilvaefgsn?otdi?solfauea?niewponie?ease??moohhtea?ilranfgw?htienco?e?vrt?is?ehcth?lerig?dwlomoveitcoma?hrpio?ea??ng?fo?dcwltesee?uhinahgtfw?ot?lt?ns??h?tieerr?eadlnm?nho??iv?csucci?thlnhsotcct??niee?iearamcifh??t?teclago?h?mp?ammnaretiu?esma??tciwdmenceetse?cpr?wn?amipiivignloe?ccuht?aadamfai?hen?rvesehtrivase?l??ih",
				"tcnoto?echlo??e?ere?iu?cvten?ena?i?a?m?ih?aadt??cgln?otienlo??c?ooashmw?taphr?ceerif???aucteilvaefgsn?otdi?solfauea?niewponie?ease??moohhtea?ilranfgw?htienco?e?vrt?is?ehcth?lerig?dwlomoveitcoma?hrpio?ea??ng?fo?dcwltesee?uhinahgtfw?ot?lt?ns??h?tieerr?eadlnm?nho??iv?csucci?thlnhsotcct??niee?iearamcifh??t?teclago?h?mp?ammnaretiu?esma??tciwdmenceetse?cpr?wn?amipiivignloe?ccuht?aadamfai?hen?rvesehtrivase?l??ih",
				"m?gooohi?zvibwpbinte????nmogneay??re??htzgymd?i?ogarlomtigvish?tiaypz?aroeilww?otn?e?ney?ometanypegsrli?dt?pavey?imirtiaiogtinpyporh?iizpoeywtanyaegarlmtigoieebnw?l?phoz?opwvtntgedaaa?inotmoiam?lwi?onpeisggreihd?aa?tpeob?ltaezgmeara?saolgshwzsmtitnwetmdvg?brlath?ngos???tr?zarppom??otsgtitw?pynya?tepenmsmi?vmgihpeai?ya?rynto???epam?sm?tadaoa?ipmspsoiwgaabya?ittn?ggvipi???ponemdyaeeteliginntpplmntnyporabhtz",
				"m?gooohi?zvibwpbinte????nmogneay??re??htzgymd?i?ogarlomtigvish?tiaypz?aroeilww?otn?e?ney?ometanypegsrli?dt?pavey?imirtiaiogtinpyporh?iizpoeywtanyaegarlmtigoieebnw?l?phoz?opwvtntgedaaa?inotmoiam?lwi?onpeisggreihd?aa?tpeob?ltaezgmeara?saolgshwzsmtitnwetmdvg?brlath?ngos???tr?zarppom??otsgtitw?pynya?tepenmsmi?vmgihpeai?ya?rynto???epam?sm?tadaoa?ipmspsoiwgaabya?ittn?ggvipi???ponemdyaeeteliginntpplmntnyporabhtz",
				"??sotode?bg?ruererein?nlf?esdvuann??nyd?blaaloonthi?ro?hesg?ad?e?taebyw?t?piruleernbndia?t?v?ifaebht?r?ol?netgba?eao?hewposhedeaee?dy??bhtvau?iraubhw?i?hest?idrfrornedeblthuger?hilwity?fe??t??anirpnode?oash?b?dl?wioeever?i?u?bh?bw?toaitrstduba?heerriealgsyr?itednfltt?????obi?heo???teasheeuneara?lhbhid?a?e?gah?dhi?pna?y?aretn?nve?ana??hwl?eulee?teatprlwura?nphhfnhsg?eol??htd?ala?b??iiesorfeeei?r?faee?ird?b",
				"rlgooohclzviewpecnts???terogieoy??re??htzsymd?i?rsdrworecgviyhltiaytz?aroeilwwtotn?m?isy?rretdeypmssrwi?dt?tavmy?cmirecaiogecipytorh?iizpoeywtdnyomsarlrecgriseeew?w?thoztopwvtntssdada?ieotrriam?lwi?oipeiygsrmihdlad?tteoe?ltoezsrmara?ydowgshwzyrectnwstmdvg?erlath?esrs?lltr?zdrptorllotygectw?pynyatempsiryrclvmsihpsai?ya?rynto???etam?yrleadaootcprspyriwsaoeya?ieee?sgvipitl?poiemdyametslcginettplrnteytordehtz",
				"ansntndsnbg?huehsresieuveaesivtaui??uyd?bcazloouime?lnatssg?idne?tagbyw?t?hccuveeriouisaeiav?eeaeomt?l?ol?ugtgoaeszo?tswhnstsieage?dy??bhtvau?eratomw?catssi?sdhecoligdebvthuger?mslwety?ee?ai??zucchinie?oism?o?dlnweoegvehec?t?bmaow?toietlstdubiatsercsezlgsyh?ctediecitenn??obe?hgnannteistseuieara?vtohsiaiasngzm?dhs?hua?y?aretieuvg?zuiantwl?etvseateiihccwtha?ihtteimsg?eovnehti?zla?o??scssoreegecar?eage?ehd?b",
				"tceomouichpqostoirh?ok?setgeninl?oa??wuehsluiy??ogl?iotiiepqsuchqolshwkam?esussghroe?n?lkotieleltegt?iqyie?sopelkiu??iikeoeiintlsgauwqqhmmilselrlnegkastiieoq?toeuyiosughsmmsphreg?iklowqegetoqou?sueoont??segaequicklyhsigoksen?hgtekaoyslmietushstiihru?huipewo?sohuoesotkcceayhl?msotccmhseiihsotlrlosiem?ntsticpugqum?oe?low?lrhmok?isou?stcikiognsittttsoeusknolooeiieogepqt?sckmmn?uiloe?e?sie?rehststreelsgaloueh",
				"eeeemeuieepqbstbirhrok??eegenitl?oa??wueecluir??ogir?eeiiepqsuehqolnewkam?esus?ghrom?nrlkoeieieltmgtr?qrie?nopmlkiu?riikeeeiintlngauwqqeimilseirltmgkaseiieoqrtbeur?onuge?misphregrikiowqegeeoqou?sueoent??segamquiekirhnigbkset?egemkaorsim?etuseseiihrurhuipewbrsohuoecotkeeeareirineeeemhseiihsotlrlo?imirneseiepugquiroe?lowrlrhmok?inou?seeikiogt?itettsoeucktblooeiieogepqt??ekimn?uilom?ersie?rehntsereelngaibuee",
				"stno?oseth?srscremtiei?lfsyndiuo?ebw?osphlord?a??aifroshen?snst?sooehoeb?wphnslytmee?dioi?sipifocealfrs?dp?eo?eoierafheeponhedcoeybsosshi?iospimoueaebhshen?sierfn?reesyhl?is?tmpaideioosfyps?sbr?hnpeodcwannabessdtei?teiyrihpuwhaseebo?ni?rnlsshnshetmnitrd?norfho?sefl?littpb?hifieostt??nnhetsecomoblheiidsnset?rassiibp?obofomt?ei?iebr?nsthedbyulecslcn?pnleurobephhfean?scaltii?dwrdobewpihenamftechsmpfoeybirsph",
				"m?gooohi?zvibwpbinteiknsnmogneamniren?htzgmm??inogarlomtigvish?tiampz?aroeiewwsotniennemkometanmpegsrli??tnpavemkimirtiaiogtinpmporh?iizpoemwtanmaegaremtigoieebnw?liphozsopwvtntge?aaa?inotmoiamnewiionpeisggreih??aa?tpeobketaezgmeara?saolgshwzsmtitnwetm?vg?breathingosk??tr?zarppom??otsgtitwipmnmastepenmsmi?vmgihpeainma?rmntoiknepamnsm?ta?aoasipmspsoiwgaabmaiittniggvipis?kponem?maeeteeiginntppemntnmporabhtz",
				"edrthtuideilbnsbisaxnke??etrniaaenr?egueegamireeogpn?te?irilsudwldasegkrh?ithn?tasneenxakoeiep?aseggn?lrieesdieakimen?ikitr?insastruglledhianepsaaegkrte?irolx?b?hr?nsute?hdniasegxikpdgl?teeolomethintns?esrgreluidkprasitbktea?egeekrdrsph?rgunese?iashxamiirgbntdwun?gogkdderrepndsteddhwsr?iannsasao??edxneseidimgludxoieaognasahnkeisomesed?kiota?isegssoihgkabaoni???ngrilse?dkdhn?miaoe?extires?asstese?astrpbuee",
				"aninsntsnrlrhnuhsets?eoveagi?itao?phoytlrca?drkoimeslnatsilritnerhagrycpshphrnvgte?oo?saeiaileeauomgslrrdloghloaes?kstscpnits?uaggptyrrrisianleeatomcphatsiirsdherrl?gtgrvsinltelmsdcehyreglairm?ohrp?n?uhkiimportdncertgighehlthrmaocphriesligtnriatsterst?dliyhshhet?ecigennlprresignannseiitstn?uaeamvtois?aiasnl?mrtismpoamysaets?eoigm?oiantcdmgtvsuaguiiprcctham?ptte?milrukvneis?h?damohlshsikeetguhaeleaggpehtlr",
				"?ogslsweogla?nn?esalekms???gnedumeuemewrgiundrimobhsos?heglaawosahusge?ulethins?asetmnluko?erh?untbrsoardrmshltukenishe?tsghennus?uweaagileunrhsudtb?uh?hegoal???iroesw?gslinlasrbld?hhea??r?oarnmhitesnneiagbutawdo?hrase??khrdegb?t?uhrahlogrwnga?heasilandlge?shhswe?iorkoorurghsiss?oolsagheanenusurshtiln?a?eolnbawilrtmuresusalekmesrnma?oh?dr?dsen?rnaotii?d?urethh?ebglanisokilnendurterlhegis?asnh?sr?us?uh?wrg",
				"stao?osltc?e?uc?lmtinhoafsyaeiuaonyeocspclarlosoihifroshla?essteetaaccey?epinuaytmnboeiahisipifacbhtfreolpoat?bahlrsfhlepoahlecaayysceech?iaupimaubheyishlaieid?fnornasyca?hu?tmphileitcefypsiearoinpnoecessahybeslteiotaiy?hipuechsbeytosi?ratsucsshltmnitrl?ac?fitesnflithttpyocifhaostt?esahltuncamaaahbhiessslt?rheshiapoaacfamt?nhoiaarossthelayualcstcsipnleu?aanphhfnha?ecsathh?eerlaabepiilasmftacismpfaayyi?spc",
				"enenmnuinepqbstbirhsok??eegenitl?oa??wueecluir??omerlnetiepqsunhqolnewkam?esus?ghroo?nslkoeieeeltomtrlqrie?nopolkiu?rtikenetintlngauwqqeimilseerltomkasetieoqstbeurlonuge?misphremsikeowqegeeoqou?sueonnt??semaoquinkerhnigbkset?emeokaorsemletusesetihrushuipewbrsohuoecotknneareerinnennmhsetihsotlrlo?toisneseinpumquisoe?lowrlrhmok?inou?sentkiogt?itettsoeucktblooetteomepqt??nkimn?uiloo?essie?rehntsereelngaebuee",
				"aninsntsnrlrhnuhsets?eoveagi?itao?phoytlrcasdrkoimeslnatsilritnerhagrycpshphrnvgte?oo?saeiaileeauomsslrrdloghloaesskstscpnits?uaggptyrrrisianleeatomcphatsiirsdherrl?gtgrvsinltelmsdcehyreglairmsohrp?n?uhkiimportdncertgighehlthrmaocphrieslistnriatsterstsdliyhshhet?ecisennlprresignannseiitstn?uaeamvtois?aiasnlsmrtismpoamysaets?eoigmsoiantcdmgtvsuasuiiprcctham?ptte?milrukvneis?hsdamohlshsikeetguhaeleaggpehtlr",
				"stao?osltl?e?nc?lmtiehoafsyaeiueoeyeoospllerdosoihifroshla?esstwedealoey?etlnnaytmeboeiehisipifecbhgfreodpoad?behlrsfhletoahleceayysoeele?ienpimeubheylshlaiei??fnoreasyla?en?tmphideidoefypsiebrolnteoecessahybesdteiotaiy?hlpuelhsbeydosi?ragsnlsshltmnitrd?ao?fldwseflighttpyolifeaostt?wsahltnecemebahbeiessslt?rheseibtoebofemt?ehoiabrossthedbyualcsgcsitnleu?ebethhfeha?ecsathe?eerdebbepillasmftaclsmpfeayyi?spl",
				"snsotodencgoruerere?nin?fsesdvtann??nyd?claaloonthlniostesgoadneotaecyw?t?piru?eernbnd?aitsv?lfaebhtniool?netgbaieaontewpostedeaee?dyoochtvau?lratbhw?istesto?drfroinedec?thuger?h?lwltyofe?stomanirpnode?oash?bodlnwloeeverii?t?chsbw?toaltistducasteerr?ealgsyrnitednflttinn??oclnheosnnteasteeunearam?tbh?dsasengahodh?mpnamynaretninvemanasntwlmet?eesteatprlwtramnpttfnhsgoeo?nihtd?alamb???iesorfeeeisr?faee?lrd?c",
				"iegesexlecletnutlnirsroaeidgdntaosukolxdccamlrkotgin?eiilgleixegeuaaclauskpornadinsmodrartindieaumgsn?erldoaulmarlmknilapegilduaaduxleechsnandinatmgauoiilgterdterr?saxdcashnlindgrlaiuleedditegmoorpsedukkiggumexleairiandtrodtkcgimauuriis?gsxnciiilinrrimllgltnougxsectsreedurcinhaeieesgigilinsuanagaimhrdiiilelmgexhrgpoaglnanissronagmoiieialgdtaluisuitprcattagspiiesggleukaerhsdkmlagmkdrolgkneiauoindeaaduitxdc",
				"legsssxlecleonuolnimsroahldgdniaosukolxdceamlrkotttneslglgleixegeuaaclauskpornadinsaodmartlndthauatsneerldoaulaarlmknglapsgglduaaduxleechsnandtnaiatauolglgtemdohrresaxdcashnlindtmlatulehddltegmoorpssdukkigtuaexleatriandorodikctlaauuritsegsxncilglinrmimllglonougxshetsreedurctnhasleesgigglinsuanagagahmdlillelmtexhmgpoaglnanissronagmoilegalgdialulsuitpreaioagspgghstgleukaerhsdkmlagakdmolgknhiauolndhaadutoxdc",
				"aninsntsndlrhnuhsets?eoveagi?itao?phoytldcasarkoimeslnatsilritnercagdycpshparnvgte?oo?saeiaileeauomtslrralogcloaesskstscpnits?uaggptyrrdisianleeatomcpaatsiirsdherrl?gtgdvsinltelmsacecyreglairmsoarp?n?uhkiimportancertgighealthdmaocpcrieslittndiatsterstsaliyhsacet?ecitennlprdesignannseiitstn?uaeamvtois?aiasnlsmrtismpoamysaets?eoigmsoiantcamgtvsuatuiiprcctham?ptte?milrukvneis?hsaamohlsasikeetguaaeleaggpehtld",
				"sts?t?detcgoruerereiiinlfsesdvuani??nyd?claaloonthinr?shesgoadteotaecyu?t?sijuleeribndiaitsv?ifaebhtnrool?netgbaieaonheus?shedeaee?dyoochtvau?iraubhu?ishestoidrfjoriedeclthuger?hiluityofe?stolanijsi?de?oash?bodltuioeeverii?u?chsbu?toaitrstducasheerjiealgsyrnitediflttitt??ocinhe?sttteasheeuiearallhbhidsasetgahodhilsnalynaretiinvelanasthulleuleesteatsjluuralishhfihsgoeoltihtd?alalb??iiesorfeeeisr?faee?ird?c",
				"stsolodutgsornerutiteinlfsesdvuune??ned?gluonoonthinrosqussoadtsohuegew?l?thanleitebndtuitsv?ifuebhrnroon?nehsbuiuoonquwtosqudeuee?deoogilvun?ituubhw?hsqustot?rfaoreedegllinsit?htnwiheofe?storonhateode?oash?bodntwioieverih?u?ghsbw?hoailrsrdngasquitationssernhhsdefltritt??oginieosttlsasquineeuturlqbitdsasutsohoditrtnurenutileinveronastqwnreuluesreattalwururetqqfehssoeoltiild?onurb??thusotfieehst?fuee?ird?g",
				"stsrtrdetcgyruerereieinlfsesdvtane??nyd?clauloonthinrrshesgyadteytaecyw?t?tgsuleerebndiaitsv?ifaebhtnryol?netgbaieuonhewtrshedeaee?dyyychtvau?iratbhw?gshestyidrfsoreedeclthuger?hilwityyfe?styoungsterde?oash?bydltwioeeverig?t?chsbw?toaitrstducasheersieulgsyrngtedeflttitt??ocinhersttteasheeueearaolhbhidsasetguhydhiotnaoynareteinveounasthwloetleesteattslwtraoethhfehsgyeoltihtd?ulaob??igesorfeeegsr?faee?ird?c",
				"iegesexlezletautlnirgroaeidgdnnsogukocxozcsmerkotgtnweiilgleixeheosazcauskitwaadingmodrsrtinotesumgonwereooaolmsrlmknilaiegildusaduxceezpsnsaotnsnmgautiilgteretewrwgaxdzaspalinogreatoceedoiteamotwigedukkiggumexeeatriandtrtonkzgimauoritswgoxaziiilinwrimelgctntohxgectoreeourztnpaeieeshigiliagusnsaaimprdiiilelmgexpraiosacnsnisgronaamoiieiaeadnaluiouitiwcantsagiiiegggleukaerpsdkmesamkortlgkneiautinoesaduttxoz",
				"sssntndescgonuenereeiinlisesdvrani??nyd?coaaloontrcnlnstesgoadseotaecyu?t?sijuleeribndeaitsv?ciaebrtnlool?netgbaieaonteusnstedeaee?dyoochtvau?crarbru?istestoednijoliedeclthuger?reluctyoie?stolanijsinde?oasr?bodlsucoeevenii?r?crsbu?toactlstducasteerjeealgsynnitediiottiss??occnhensssteasteeuiearalltbhedsasesgarodhelsnalynaretiinvelanasstullerleesteatsjournalisttiirsgoeolsihtd?alalb??eiesorieeeisr?iaee?cnd?c",
				"sssntndescgonuenereeiinlisesdvdani??nyd?coaaloontrcnlnshesgoadseotaecyu?t?sijuleeribndeaitsv?ciaebrtnlool?netgbaieaonheusnshedeaee?dyoochtvau?cradbru?ishestoednijoliedeclthuger?reluctyoie?stolanijsinde?oasr?bodlsucoeevenii?d?crsbu?toactlstducasheerjeealgsynnitediiottiss??occnhensssteasheeuiearallhbhedsasesgarodhelsnalynaretiinvelanasshulledleesteatsjoudnalishhiirsgoeolsihtd?alalb??eiesorieeeisr?iaee?cnd?c",
				"stsotodetcgoruerereininlfsesdvuann??nyd?claaloonthinroshesgoadteotaecyw?t?piruleernbndiaitsv?ifaebhtnrool?netgbaieaonhewposhedeaee?dyoochtvau?iraubhw?ishestoidrfrornedeclthuger?hilwityofe?stomanirpnode?oash?bodltwioeeverii?u?chsbw?toaitrstducasheerriealgsyrnitednflttitt??ocinheosttteasheeunearamlhbhidsasetgahodhimpnamynaretninvemanasthwlmeuleesteatprlwuramnphhfnhsgoeoltihtd?alamb??iiesorfeeeisr?faee?ird?c",
				"sssitidescgonunnereerinlisesevranr?dnyd?coaalolntrcnlistesgoydseotaecyu?tdfijuleerrbneeaitsv?cianbrtnlool?netgbaiealnteufisteenaee?dyoochtvau?crarbru?istestoednijolredeclthuger?reluctyoie?stolanijfriendlysr?bodlsucoeevenii?rdcrsbu?toyctlstducysteerjeealgsynnitedriottiss??occnheisssteysteeurnaralltbheesysesgarodhelfnalynaretrinvelanysstullerlenstnytfjournalrfttirrsgonllsihtedalalbd?eieslrieenisr?iaee?cnd?c",
				"togysy?eozl?eaueenincrathtegindsacuka??ozesmdrkaiatniythegl?a?oh?ostz?auskngeateinceainsritnothsuearni?rdoatolesremknheanygheiusteu????zpsnsaotnsdeaaugthegi?neeherict?eztspalinoandato??heoti?amagencyiukkagaue??doatritneergodkzateauoratsigr?azatheinenimdlg?engoh?cheirrooourztnptytooshagheiacusnsathepnitateolma??pnanasa?nsniscrantamaatohadaedteutruaineeadesacnhhhcagl?uktorpsikmdsaekongegknhitugtnohsteute?oz",
				"m?gohooi?nvednsdisaeekmsnmogneeameremfotnsati?nmaganlomtigveho?wedapnfsrherhansoaseemneakametanaseggnle?itmpdveakitnntisrogtinsaporofeentheantasaeegsrhmtigaee?dna?lepoonshtnvastgeisadfenotmaertmhareonsenhggreeoi?sa?apeodkhteengmesrd?hahlggonnhmtiasaeativgfdnhdwoensagk??tr?nantpom??hwhgtianesasarstetenmhmi?vtgeoterrmarfnasahekmeprtmhm?tsiroesismgsharassedarerttneggvesns?kthnetiareetehignsnapshmstnaporadotn",
				"stnrtr?ethlorserereieuncfsendetanedany?ihlaadtanchinrrshenloa?t?ooashywdtaphrsceerebndiaucseiifaebhsnrotdinsolbaueaanhewprnhedeased?yoohhteasiiratbhwdhshencoierfrtres?ehcthslerihidwioyofeiscomanhrperdeaaanhdbo?dtwiteseeruhitahhsbwdotaitrns?shasheerrieadlnyrnho??eflcsuttidthinhsrsttt?anheeseearamchbhidsasetlaho?himpnamynareteunesmanasthwdmetceesseacprlwtramephhfehnloeactuhtdaadambaiihenarfesehsrifasedir?ih",
				"ieetttdeezlotarterernunceiee?ensnndankdizcsadtancgtewtireeloideeoosszkpdtaegraceernmn?rsucieitesrmgrewotdinsolmsueaaerepetere?rsseddkoozhtesaitrsnmgpdgireecoretertwnsdezcthalerigrdptokoeeiicomangrent?raaiegdmoddeptteseetuginazgimpdotittwerdaziireerrreadlektegoedneccrueeidtztehstieeteiereeanrsrsmcrmhr?iiieelagodhrmensmkesretnunesmaniierpdmencerirricercpntsmnerrengeloraceuht?aadsmmairgeeareesrgiriessedttdiz",
				"tmnstssimhpsiseiiheveuncpthnienoneeanysyh?ordtancesnisthinpsism?soothywetaphrschehefnivoucteyspoefelnistdyntopfouiranhiwpsnhiieothesysshiteosyshonfewehthincsveiprtietshhctispehyevdwsoysphytcsernhrpesieaaineefssdmwstetehiuhynahetfweotistinlsshithiehrverdpnyinho?sep?clummyethsnitstmmt?inhieseeohoechfivititimpressivepnoeynoheteuneternitmhwdehncietleicpr?wnioeephhpeenpseacmuitiardoefayvhinahpetehthypothesisyh",
				"tonsts?eohlooaeoereleuncvtenienanedany?ih?aaltancrtnisthenloi?o?ooashywdtapnraceerefnilaucteitvaefrtniotlinsolfaueaanhewpsnheieased?yoohhteaaitranfrwdnthencoleovrties?ehcthalerirllwtoyoveitcomannrpesieaainrdfo?lowtteseeouninahrtfwdotittint?ahitheerrleallnyonno??ev?ctuooidthtnhsstoot?inheeaeearamchfhlititeolaro?hlmpnamynareteunesmanitohwlmenceetteicpr?wnoamephhvernloeacouhtiaalamfailnenarvesentrivasedto?ih",
				"tonstsdeozlooaroerelnuncoteniedsnndankdizesadtancbheasthenloidoeoosszkwdtapgraceerninilsucteihosribreaotdinsolisueaaehewpsnheirsseddkoozhtesaihrsdibwdgthencoleoortansdezcthaleribldwhokooeitcomangrpnsiraainbdioddowhteseeougidazbtiwdotihtanrdazitheerrleadlnkoegoednoecruooidtzhehsstooteinheeanrsrsmchihlititeolabodhlmpnsmkesretnunesmanitohwdmedcertrricprewdosmnphhonbnloracouhtiaadsmiailgenaroesrgtriossedhodiz",
				"tonstsdioflor?rridea?in?htendvnan?ernydefeaaltanteneisthinloedoeotaefyretrpir??eed?gndaaittvenhargeteiotlenetlgaiiaaehirpsnhidraeeedyoofhtva?endangereithintoadrhrti?edef?th?ledeealrntyoheettomanirp?sdrraeneegodlornteeveriienrfetgrettentintd?fethiedraeallnyreited?hettiooeetfnehestooteenhie??radam?hghadtetiolaeodhampnamyeadet?invemanetohrlmen?irttretprernram?phhh?enlora?oihtdralamgreaiinadheeritdehaeeenrdef",
				"tonstsdeowloouroerelnuncoteniedanndankdiweaadtancbheasthenloidoeotaswkwdtapiruceerninilaucteihoaribgeaotdinstliaueaaehewpsnheiraseddkoowhteauihradibwdithencoldoortansdewcthuleribldwhtkooeitcomanirpnsiraainbdioddowhteseeouiidawbtiwdttihtangduwitheerrleadlnkoeitednoecguooidtwhehsstooteinheeunraramchihlititeolabodhlmpnamkearetnunesmanitohwdmedcertgricprewdoamnphhonbnloracouhtiaadamiailienaroesritrioasedhodiw",
				"annntnssnhpshsehsreseunceaen?etanedanysihcarstanimenlnatsnpsisnhsoashywdtaphrsceereon?sauiaeieeaeomtnlstsinsopoausrantswpnnts?easedsysshiteasieratomwdhatsnissehertlessehctisperimssweoyseeiaisernhrpen?eaainmdosssnweteseehuhitahmaowdotietlntsshiatserrserspnyhnhohseecitunnidthenisnannthintseseearaectois?aiasnprmssisepnaeynareteuneserniantwseetcseateiiprcwthaeeptteemnpseacnuit?arsaeoaishsnareeseharieasedehsih",
				"tmnstssimhpsoseoireveuncvtenienanedanysih?ardtancetnisthinpsism?soashywdtaphrsceerefnivaucteitvaefesnistdinsopfauiranhiwpsnhiieasedsysshiteasitranfewdhthincsveovrtiessehctisperievdwtoysveitcsernhrpesieaainedfssdmwtteseeouhinahetfwdotittinssshithierrverdpnyonho?sev?csummidthtnisstmmt?inhieseearaechfivititimpressivepnaeynareteunesernitmhwdeencietseicpr?wnoaeephhveenpseacmuitiardaefaivhinarvesehtrivasedtosih",
				"tonstsdioflorurridea?inlhtendvnan?ernydefeaaltanteneisthinloedoeotaefyretrpiruleed?gndaaittvenhargeteiotlenetlgaiiaaehirpsnhidraeeedyoofhtvauendangereithintoadrhrti?edeflthuledeealrntyoheettomanirp?sdrraeneegodlornteeveriienrfetgrettentintdufethiedraeallnyreited?hettiooeetfnehestooteenhieu?radamlhghadtetiolaeodhampnamyeadet?invemanetohrlmenlirttretprernram?phhh?enloraloihtdralamgreaiinadheeritdehaeeenrdef",
				"tmnstssimhpsoseoireveuncvtenienanedanysih?arstancetnisthinpsism?soashywdtaphrsceerefnivaucteitvaefetnistsinsopfauiranhiwpsnhiieasedsysshiteasitranfewdhthincsveovrtiessehctisperievswtoysveitcsernhrpesieaainedfsssmwtteseeouhinahetfwdotittintsshithierrverspnyonho?sev?ctummidthtnisstmmt?inhieseearaechfivititimpressivepnaeynareteunesernitmhwseencietteicpr?wnoaeephhveenpseacmuitiarsaefaivhinarvesehtrivasedtosih",
				"tmnstssimhpsoseoireveuncvtenienanedanysih?arstancetnisthinpsism?soashywdtaphrsceerefnivaucteitvaefetnistsinsopfauiranhiwpsnhiieasedsysshiteasitranfewdhthincsveovrtiessehctisperievswtoysveitcsernhrpesieaainedfsssmwtteseeouhinahetfwdotittintsshithierrverspnyonho?sev?ctummidthtnisstmmt?inhieseearaechfivititimpressivepnaeynareteunesernitmhwseencietteicpr?wnoaeephhveenpseacmuitiarsaefaivhinarvesehtrivasedtosih",
				"totsssxeocleeuaeeninnroshtdtunnaonnrocxoceamlraoratnisthetlenxoeetauccansrpiwusdinneounarrtnothaaeatnierlooutlearemanheapstheuaaudnxceechsnauotnaneaanithetrendehwrinuxdcsshulinoanlattcehdotreamoiwpnsuarantaneexloatriunderionrcateantrntsittxucntheinwnimlltcenitexnhertrooonrctnhustoosentheiunaanaashehnutnteolmaexhnapoaacnanisnronuamontohaladnseattanrpweaneaanphhhnatleaasorhsurmlaaeronietanhiuaitnohaudntexoc",
				"leosvsegedeeonhogsamersthlroysiiseyisleadeinthesrttweslggoeeueeeecitdlsyvincentraseasymirrlsathihatlweehtastceairgnewggsnsoggyhitryeleedivsinatsiiatsyclggoremeoheheeterdtvineasatmtstclehralrehnscenesyhieuotyaeetesthatsrorcaiidtlasychutveolendulggasemanteolowcceeeherlreeayhdtwitsleeveuogganehisihtgaimylulgeenteeimhnsihlwisaverssthnsulegsthritghllhurneesioihengghetoeeheterivyintihaiamcgoeshathclsahitrytoead",
				"lerstslnericonsonsamermthlerysidmetbmeloredudhemittweslgnriculenceddresttbthinteaseamymdrilsothdsatgwechdomdeiadrnuewgnstsrgnysddetleccretsdnotsdiatsthlgnricm?ohiheedlertteniasotmdsteecheolicyumhitesysbeurttacldesthadseorhoibrtlastehutterglnrulgnasimaudireowhenleheigreeothrtwedsleetnurgnanesdsdytgaemylulneiutclemytmdyewdsatermsdyumulegsdyeitnslgsuitiesiodyetgghetricseteretybuddyabomhnreshadshlsohddettolor",
				"togshsoeonleenseesaiermshtdginnameukmfoundationmratnisthegleaoowedaunfauhkthinsdaseemiiarrtnuthaseagnieoiumudlearetnnheatsgheisauduofeenthnanutsaneaauhthegrei?ehioieuodnshtnlasuaiiatdfehdutrertmhitesisknagaueeoioatoaunderhunknateaudoathiggonnatheasiiatilgfenhdwoehdrgroouuontntustoohwagheanesasarshetiitateoltaeotirtmarfnasahermnurtmatohairdnsestgsartidanearethhheaglesnsorthiktiarekuihegnshaushtsuhauduteoun",
				"totsssxeolleenaeenineroshtdtunteoenrooxoleemdraoratnosthetlenxowedeuloansrtlwnsdineeounerrtnotheaeagnoerdooudleeremanheatstheuaeudnxoeelesnenotneteaanlthetren?ehwroeuxdlssenlinoandatdoehdotrebmolwtesuarantaneexdoatriunderlotrlateandrntsotgxnlntheinwnimdltoenldwxehergrooonrltneustooswntheineaenebsheenutnteolmaexenbtoeboneniseronubmontohadbdtseatganrtweateebethhheatleaasoresurmdeberonletanhiualtnoheudntexol",
				"totsssxeolleenaeenineroshtdtunnaoenrooxoleamiraoratnisthetlenxowedauloansrthwnsdineeounarrtnothaaeagnieriooudlearemanheatstheuaaudnxoeelesnanotnaneaanhthetren?ehwrieuxdlssenlinoaniatdoehdotrebmohwtesuarantaneexioatriunderhonrlateandrntsitgxnlntheinwnimiltoenhdwxehergrooonrltneustooswntheineaanabsheenutnteolmaexenbtoabonaniseronubmontohaibdnseatganrtweaneabethhheatleaasoresurmiaberonhetanhiuahtnohaudntexol",
				"lershsoeeniconsoesamnrethlerisiaentoefooneamiheelatneslgericuoewcdatnfnthoithnteasnoeimarllsothasoagnechioetdioaremengenisrgeisatetofccnthsanotsaioanttlgerlcm?ohhhentoenthtniasoamintdfcheollcomethinsisoeuratocoienthatseortoionalontdhuthergonnulgeashmamiirfontdwonhelgreeothntnttsleehwurgeannsasaotgotmiluleeimacotmoieaofnasahnrestomeulegnioeiteslgsulihenioaonigghnaricseterthiomiaoooomtereshatstlsohatettooon",
				"nensasheenetenseesemrchahningsiehrtohfhaneenihehiatresngenetahewtdetnfstaoithnaiesrohgmecinsathesoagrethiahtdeoecenergesisngegsetithfttntasenatseioasttngenitm?ehhherthinaatneesaamistdfthianitonhthirsgsoeanatothiesthetsiectaionanostdhataenghnnangeeshmenienfertdwhrheigceeathntrttsneeawangeenrseseoagotmgnaneeenathtmoiheofresearchstonhanegsioiiaesngsaiihesieeorigghranetseaectagonieoooamteneshetstnsahetittehan",
				"lerstslnericonsonsamecmthlerysiametomeloreandhemiatweslgnriculenc?adresttothinteaseomymacilsothasoa?wechdomd?ioacnnewgnstsrgnysadetleccritsanotsaioasthlgnricm?ohiheedlerttiniasoamdst?echeolicrnmhitesysoeuratocldesthadseochoioralost?hutter?lnrulgnasimandireowh?nlehei?ceeothrtwidsleetnurgnanesasartgoimylulneinaclimrtmarewasatecmsdrnmulegsdreitnsl?suitiesioaretgghearicsetecityondarooomhnreshadshlsohadettolor",
				"lesitienerecyntynsamersthlesystasetoseeorcavdhoslitueiltnsecueeechadreittolhinteaseosymarllsothatoisuechdosdheoarnvoutnilistnytadeteeccritsanotsatoiithltnslcm?yhiheedeerttineasoimdithecheollcrvshileiytoousitocedeithadseyrhotoriloithhuttessenrultnasimavdeseyuhheeehclsreeothrtuidileeteustnanetasarttoimylulneeviceimrlsareuasaterssdrvsuletidrettntlstullicityarelttheisectoterityovdarooomhnsoshadthlsohadettyeor",
				"oeertrlheriionsohsasermtroeeistameyomeloreandhemsatwerotheiiulenieadrehytotpinteaseemisarsosotraseagweihdomdeiearhnewthhtrethisadeyleiiretsanotsateahypothesis?oriheedlertteniasoasdhteeireoosibnmpiterisoeueayeildehthadseorpotoraoehyehutteeglnruothasisandieeowpenleresgreeoyhrtwedroeetnuethanesasabtteesiouoheinailesbtmabewasatermsdbnmuoethdbetthsogsustiehtoabetttreaeiiseteretiondabeoospheesradsposoradeytolor",
				"lerstshnecitoutonsamirsthlerysiasitosehoceanlhesittweslgnrituheettadcesttosiiuteasiasymarilsothatattwethlosdtiaarnnewgnsssrgnytadethettcitsauotsaiatstilgnritmdohiheidhecttiuiasotmlsttetheolitrnsiisisytoeurttathlesthadseorioioctlastthutterthuculgnasimanlireowitehiheitreeothctwidsleeteurgnauitasartgaimylulneintthimrssarewasatirssdrnsulegslreitntlttuisiesioarisgghitritteterityonlaraoominreshadtilsohadettohoc",
				"lerstshnelitontonsamirsthlerysiesitoseholeendhesittweslgnrituheetdedlesttoslinteasiasymerilsothetatgwethdosddiaernnewgnsssrgnytedethettlitsenotseiatstllgnritmdohiheidhelttiniasotmdstdetheolitrnslisisytoeurttathdesthadseorloioltlastdhutterghnlulgnasimandireowldehiheigreeothltwidsleeteurgnanitesertgaimylulneintthimrsserewesatirssdrnsulegsdreitntlgtuisiesioerisgghitritteterityonderaoomlnreshadtllsohedettohol",
				"lerstslnericonsonsamermthlerysiametomeloreandhemiatweslgnriculenchadresttothinteaseomymarilsothasoagwechdomdhioarnnewgnstsrgnysadetleccritsanotsaioasthlgnricm?ohiheedlerttiniasoamdsthecheolicrnmhitesysoeuratocldesthadseorhoioralosthhutterglnrulgnasimandireowhhnleheigreeothrtwidsleetnurgnanesasartgoimylulneinaclimrtmarewasatermsdrnmulegsdreitnslgsuitiesioaretgghearicseterityondarooomhnreshadshlsohadettolor",
				"lerstslnericonsonsamermthlerysiametomeloreandhemiatweslgnriculeechadresttothinteaseomymarilsothasoalwechdomdhioarnnewgnstsrgnysadetleccritsanotsaioasthlgnricmeohiheedlerttiniasoamdsthecheolicrnmhitesysoeuratocldesthadseorhoioralosthhutterllnrulgnasimandireowhheleheilreeothrtwidsleeteurgnanesasartgoimylulneinaclimrtmarewasatermsdrnmulegsdreitnsllsuitiesioaretgghearicseterityondarooomhnreshadshlsohadettolor",
				"lerstshnericonponsamersthlerysiasetosehoreandhesittweslgnricuheechadresttothinteaseasymarilsothapathwechdosdhiaarnnewgnstsrgnypadetheccritsanotsaiatsthlgnricm?ohiheedherttiniasotmdsthecheolicrnshitesypoeurttachdesthadseorhoiortlasthhutterhhnrulgnasimandireowhheheheihreeothrtwidsleeteurgnanepasartgaimylulneintchimrtsarewasaterssdrnsulegsdreitnplhpuitiesioaretgghetricpeterityondaraoomhnreshadphlsohadettohor",
				"torhthleoriconsoesanerathterisnaaetoaeloreandplaywrighthericulonchadresttothinteaseeainarytsorhasewsigcpdoadhiearenlihesthrheisadetleccritsanorsanewsththerycn?ohipgedlerttiniasowndsrhecheotycrnahitehisolurwtecldosrpadseorhonorwtesthpurtgrslnrutheasinandireoihhnleheysroootprriidhtootnurheanesasartheinituteoinwclinrtaareiasaterasdrnautohsdrentestssuytiesnoarethhhewricsltoritiondareoonherlshadshtsohadetrolor",
				"lerstslnericonsonsanermthlerystametomeloreandhemiatweslhnriculenchadresttothinteaseomynarilsothasoagwechdomdhioarnnewhnstsrhnysadetleccritsanotsatoasthlhnricn?ohiheedlerttiniasoandsthecheolicrnmhitesysoeuratocldesthadseorhotoralosthhutterglnrulhnasinandireowhhnleheigreeothrtwidsleetnurhnanesasarthoinylulneinaclinrtmarewasatermsdrnmulehsdrettnslgsuitiestoarethhhearicseterityondarooonhnreshadshlsohadettolor",
				"torstsleoriconsoesaiermthteristametomelureanvheminrwisthericuloncsadreettothinteaseemiiaritsurhasenswichvumdsiearenewheetsrheisadetleccritsanursateneththerici?ohihiedlerttiniasuniversecheuticrnmhitesisoeurnteclvoerhadseorhutornteetshurtirslnrutheasiianvireowhsnleheisroouthrrwidstootnurheanesasartheiiituteoinncliirtmarewasatermsdrnmutohevrettestssuitieetoarethhhenricsetoritionvareouihereshadshtsuhadetrolur",
				"tprstslepticonsoesanermthterisnamelomeloteandiemiaicisthericulpechaltesltothinteaseeminaritsoihaseascicidomlhiearenechestsrheisalellecctatsanoisaneaslhthericneohiiielletttaniasoandsihecheoticbnmhitesisoeuralecldpsiialseorhonotateslhiuitirslntutheasinandireochheleheisrppoliticalstppteurheanesasabtheanitutepinaclanbtmabecasatermslbnmutphsdbentestssuitiesnoabethhhearicsetprationdabeoonhereshalshtsohaleliolot",
				"lerstshneritontonsamersthlerysiasetosehoreandhesittweslgnrituhenthadresttothinteaseasymarilsothatatswethdosdhiaarnnewgnstsrgnytadethettritsanotsaiatsthlgnritm?ohiheedherttiniasotmdsthetheolitrnshitesytoeurttathdesthadseorhoiortlasthhuttershnrulgnasimandireowhhnheheisreeothrtwidsleetnurgnanetasartgaimylulneintthimrtsarewasaterssdrnsulegsdreitntlstuitiesioaretgghetritteterityondaraoomhnreshadthlsohadettohor",
				"lerstshneritontonsamersthlerysiasetosehoreandhesittweslgnrituheethadresttothinteaseasymarilsothatatswethdosdhiaarnnewgnstsrgnytadethettritsanotsaiatsthlgnritmeohiheedherttiniasotmdsthetheolitrnshitesytoeurttathdesthadseorhoiortlasthhuttershnrulgnasimandireowhheheheisreeothrtwidsleeteurgnanetasartgaimylulneintthimrtsarewasaterssdrnsulegsdreitntlstuitiesioaretgghetritteterityondaraoomhnreshadthlsohadettohor",
				"torstsmeoricynsyesanarmttterisnamatomemoreandpemiatwlsthericumonchasresttothonteasaeminaritsottaseagwlcpdomshiearenewhestsrheisasetmeccritsanotsaneasththericncytoplasmerttiniasoandsthecteoticrnmhotasisoeuratecmdostpasseyrhonoratesthputtlrgmnrutheasonandireywhhnmateigroootprtwisstootnurheanasasartheinituteoinacminrtmarewasatarmssrnmutohsdrentestgsuitoesnyarathhtaaricsetoritiondareoonherestasshtsotasettymor",
				"seratahneretontonsamersthserysiasetosehoreandhesittwcasenretuheethadresttothinteaseasymarissothatatgwcthdosdheaarnnewenstarenytadethettritsanotsaiatsthsenritmeohihcedherttineasotmdsthetheositrnshiteaytoeurttathdesthadseorhoiortsasthhuttcrghnrusenasimandereowhheheheigreeothrtwidaseeteurenanetasarteaimysusneentthimrtsarewasaterssdrnsuseesdreitntsgtuitiesioareteehetretteterityondaraoomhnreshadthssohadettohor",
				"seratahneretontonsamersthserysiasetosehoreandhesittwcasenretuheethadresttothinteaseasymarissothatatswcthdosdheaarnnewenstarenytadethettritsanotsaiatsthsenritmeohihcedherttineasotmdsthetheositrnshiteaytoeurttathdesthadseorhoiortsasthhuttcrshnrusenasimandereowhheheheisreeothrtwidaseeteurenanetasarteaimysusneentthimrtsarewasaterssdrnsuseesdreitntsstuitiesioareteehetretteterityondaraoomhnreshadthssohadettohor",
				"terrtrleericonsoesanermtrteristametomeloreandhemiatwerthericulenceadresttothinteaseeminaritsotraseagwechdomdeiearenewhestrrheisadetleccretsanotsateasththericn?oriheedlertteniasoandsteecreoticbnmhiterisoeuratecldesthadseorhotoratestehutterglnrutheasinandireowhenlereigreeothrtwedrteetnurheanesasabtheenituteeinaclenbtmabewasatermsdbnmutehsdbettestgsuitiestoabethhrearicseteretiondabeoonheresradshtsoradettolor",
				"torstsleoriconsoesanermthterisnametomeloreandhemiatwisthericulonceadresttothinteaseeminaritsothaseagwichdomdeiearenewhestsrheisadetleccretsanotsaneasththericn?ohihiedlertteniasoandsteecheoticbnmhitesisoeuratecldosthadseorhonoratestehuttirglnrutheasinandireowhenleheigrooothrtwedstootnurheanesasabtheenituteoinaclenbtmabewasatermsdbnmutohsdbentestgsuitiesnoabethhhearicsetoretiondabeoonhereshadshtsohadettolor",
				"tsrntnhesretenteesaeerstiterisdasetoseharoandhesircwlntheretahsethatresttothinteaseesiearitsaciaterswlthdastheearenewhestnrheitatethettritsanacsadersththeriteeeiihletherttineasaredschetieatitrnshitenitoearrtethdsschatseerhadorrtesthhactlrshnratheasieandereewhheheioisrssathrcwitntsstearheanetasartheieitatesenrthiertsarewasatersstrnsatshsdredtettstaitiosdearethhierrettetsritiondareoaeheresiatthtsaiatetcehar",
				"torstsleoriconsoesanermthterisnametomeloreandhemiatwisthericulonchadresttothinteaseeminaritsothaseagwichdomdhiearenewhestsrheisadetleccritsanotsaneasththericn?ohihiedlerttiniasoandsthecheoticrnmhitesisoeuratecldosthadseorhonoratesthhuttirglnrutheasinandireowhhnleheigrooothrtwidstootnurheanesasartheinituteoinaclinrtmarewasatermsdrnmutohsdrentestgsuitiesnoarethhhearicsetoritiondareoonhereshadshtsohadettolor",
				"torstsleoriconsoesanermthterisnametomeloreandhemiatwisthericulonchadresttothinteaseeminaritsothaseaswichdomdhiearenewhestsrheisadetleccritsanotsaneasththericn?ohihiedlerttiniasoandsthecheoticrnmhitesisoeuratecldosthadseorhonoratesthhuttirslnrutheasinandireowhhnleheisrooothrtwidstootnurheanesasartheinituteoinaclinrtmarewasatermsdrnmutohsdrentestssuitiesnoarethhhearicsetoritiondareoonhereshadshtsohadettolor",
				"terstsleericenseesanermthteristametomeloreandhemiatwesthericalenceatresttothinteaseeminaritsothaseagwechdomteiearenewhestsrheisatetleccretsanotsateasththericn?ehiheetlertteniasoandsteecheoticbnmhitesisoearatecldesthatseerhotoratestehatterglnratheasinandireewhenleheigreeothrtwetsteetnarheanesasabtheenitateeinaclenbtmabewasatermstbnmatehsdbettestgsaitiesteabethhhearicseteretiondabeoonhereshatshtsohatettelor",
				"torstsheoritonsoesanermthterisdametomehoreandhemiatwastherituhobthadresttothinteaseeminaritsothasearwathdomdhiearenewhestsrheisadethettritsanotsadeasththeritn?ohihaedherttiniasoandsthetheotitrnmhitesisoeuratethdosthadseorhodoratesthhuttarrhnrutheasinandireowhhbheheirrooothrtwidstootburheanesasartheinituteoinathinrtmarewasatermsdrnmutohsdredtestrsuitiesdoarethhhearitsetoritiondareoonhereshadshtsohadettohor",
				"torstsheoretonsoesanermthteristametomehoreandhemiatwostheretuhobthadresttothinteaseeminaritsothasealwothdomdheearenewhestsrheisadethettritsanotsateasththeritn?ohihoedherttineasoandsthetheotitrnmhitesisoeuratethdosthadseorhotoratesthhuttorlhnrutheasinandereowhhbheheilrooothrtwidstootburheanesasartheinituteoenathinrtmarewasatermsdrnmutohsdrettestlsuitiestoarethhhearetsetoritiondareoonhereshadshtsohadettohor",
				"tonstsheoretontoesanersthtenisnasetosehoreandhesiatwisthenetuhoethadresttothinteaseesinaritsothatealwithdosdheearenewhestsnheitadethettritsanotsaneasththenitneohihiedherttineasoandsthetheotitrnshitesitoeunatethdosthadseorhonoratesthhuttinlhnrutheasinandeneowhheheheilrooothrtwidstooteunheanetasartheinituteoenathinrtsarewasaterssdrnsutohsdrentettltuitiesnoarethhheanettetoritiondareoonheneshadthtsohadettohor",
				"torstsheoritontoesanersthterisnasetosehoreandhesiatwistherituhonthadresttothinteaseesinaritsothateaswithdosdhiearenewhestsrheitadethettritsanotsaneasththeritn?ohihiedherttiniasoandsthetheotitrnshitesitoeuratethdosthadseorhonoratesthhuttirshnrutheasinandireowhhnheheisrooothrtwidstootnurheanetasartheinituteoinathinrtsarewasaterssdrnsutohsdrentettstuitiesnoarethhhearittetoritiondareoonhereshadthtsohadettohor",
				"torstsheoritontoesanersthterisnasetosehoreandhesiatwistherituhoethadresttothinteaseesinaritsothateaswithdosdhiearenewhestsrheitadethettritsanotsaneasththeritneohihiedherttiniasoandsthetheotitrnshitesitoeuratethdosthadseorhonoratesthhuttirshnrutheasinandireowhheheheisrooothrtwidstooteurheanetasartheinituteoinathinrtsarewasaterssdrnsutohsdrentettstuitiesnoarethhhearittetoritiondareoonhereshadthtsohadettohor",
				"torstsheoretontoesanersthterisnasetosehoreandhesiatwistheretuhoethadresttothinteaseesinaritsothateaswithdosdheearenewhestsrheitadethettritsanotsaneasththeritneohihiedherttineasoandsthetheotitrnshitesitoeuratethdosthadseorhonoratesthhuttirshnrutheasinandereowhheheheisrooothrtwidstooteurheanetasartheinituteoenathinrtsarewasaterssdrnsutohsdrentettstuitiesnoarethhhearettetoritiondareoonhereshadthtsohadettohor",
				"terrtrheeretenseesanermtrteristametomehareandhemiatwertheretaheethatresttotninteaseeminaritsatraseagwethdamtheearenewhestrrheisatethettritsanatsateastntheritn?eriheetherttineasaandsthetreatitrnmniterisoearatethdesthatseernatoratesthhatterghnratheasinandereewnhehereigreeathrtwitrteetearheanesasartheinitateeenathinrtmarewasatermstrnmatehsdrettestgsaitiestearethhrearetseteritiondareoanneresratsntsaratettehar",
				"torytyheoretenseesanermthterisdametomehareandhemiatwiytheretahoethatresttothinteaseeminaritsathaseaswithdamtheearenewhestyrheisatethettritsanatsadeasththeritn?ehihietherttineasaandsthetheatitrnmhiteyisoearatethdosthatseerhadoratesthhattirshnratheasinandereewhheheheisrooathrtwitytootearheanesasartheinitateoenathinrtmarewasatermstrnmatohsdredtestssaitiesdearethhhearetsetoritiondareoanhereshatshtsahatettehar",
				"torstsheoretenteesanersthterisdasetosehareandhesiatwistheretahonteatresttothinteaseesinaritsathateagwithdasteeearenewhestsrheitatethettritsanatsadeasththeritn?ehihietherttineasaandsteetheatitrnshitesitoearatethdosthatseerhadoratestehattirghnratheasinandereewhenheheigrooathrtwitstootnarheanetasartheinitateoenathinrtsarewasatersstrnsatohsdredtettgtaitiesdearethhhearettetoritiondareoanhereshatthtsahatettehar",
				"terstsheeretenseesanermthteristametomehareandhemiatwestheretahebthatresttothinteaseeminaritsathasealwethdamtheearenewhestsrheisatethettritsanatsateasththeritn?ehiheetherttineasaandsthetheatitrnmhitesisoearatethdesthatseerhatoratesthhatterlhnratheasinandereewhhbheheilreeathrtwitsteetbarheanesasartheinitateeenathinrtmarewasatermstrnmatehsdrettestlsaitiestearethhhearetseteritiondareoanhereshatshtsahatettehar",
				"torstsheoretenseesanermthteristametomehareandhemiatwistheretahoethatresttothinteaseeminaritsathaseaswithdamtheearenewhestsrheisatethettritsanatsateasththeritn?ehihietherttineasaandsthetheatitrnmhitesisoearatethdosthatseerhatoratesthhattirshnratheasinandereewhheheheisrooathrtwitstootearheanesasartheinitateoenathinrtmarewasatermstrnmatohsdrettestssaitiestearethhhearetsetoritiondareoanhereshatshtsahatettehar",
				"terstsheeretenteesanersthteristasetosehareandhesiatwestheretaheethatresttotiinteaseesinaritsathateagwethdastheearenewhestsrheitatethettritsanatsateastitheritneehiheetherttineasaandsthetheatitrnsiitesitoearatethdesthatseeriatoratesthhatterghnratheasinandereewiheheheigreeathrtwitsteetearheanetasartheinitateeenathinrtsarewasatersstrnsatehsdrettettgtaitiestearethhhearetteteritiondareoaniereshattitsahatettehar",
				"torstsheoretenteesanersthterisdasetosehareandhesiatwistheretahoethatresttothinteaseesinaritsathateaswithdastheearenewhestsrheitatethettritsanatsadeasththeritneehihietherttineasaandsthetheatitrnshitesitoearatethdosthatseerhadoratesthhattirshnratheasinandereewhheheheisrooathrtwitstootearheanetasartheinitateoenathinrtsarewasatersstrnsatohsdredtettstaitiesdearethhhearettetoritiondareoanhereshatthtsahatettehar",
				"terstsheeretenteesanersthteristasetosehareandhesiatwestheretaheethatresttothinteaseesinaritsathatealwethdastheearenewhestsrheitatethettritsanatsateasththeritneehiheetherttineasaandsthetheatitrnshitesitoearatethdesthatseerhatoratesthhatterlhnratheasinandereewhheheheilreeathrtwitsteetearheanetasartheinitateeenathinrtsarewasatersstrnsatehsdrettettltaitiestearethhhearetteteritiondareoanhereshatthtsahatettehar",
				"terstsheeretenteesanersthteristasetosehareandhesiatwestheretaheethatresttothinteaseesinaritsathateaswethdastheearenewhestsrheitatethettritsanatsateasththeritn?ehiheetherttineasaandsthetheatitrnshitesitoearatethdesthatseerhatoratesthhattershnratheasinandereewhheheheisreeathrtwitsteetearheanetasartheinitateeenathinrtsarewasatersstrnsatehsdrettettstaitiestearethhhearetteteritiondareoanhereshatthtsahatettehar",
				"terstsheeretenteesanersthteristasetosehareandhesiatwestheretaheethatresttothinteaseesinaritsathateaswethdastheearenewhestsrheitatethettritsanatsateasththeritneehiheetherttineasaandsthetheatitrnshitesitoearatethdesthatseerhatoratesthhattershnratheasinandereewhheheheisreeathrtwitsteetearheanetasartheinitateeenathinrtsarewasatersstrnsatehsdrettettstaitiestearethhhearetteteritiondareoanhereshatthtsahatettehar",
				"terstsheeretenteesanersthteristasetosehareandhesiatwestheretaheethatresttothinteaseesinaritsathateaswethdastheearenewhestsrheitatethettritsanatsateasththeritneehiheetherttineasaandsthetheatitrnshitesitoearatethdesthatseerhatoratesthhattershnratheasinandereewhheheheisreeathrtwitsteetearheanetasartheinitateeenathinrtsarewasatersstrnsatehsdrettettstaitiestearethhhearetteteritiondareoanhereshatthtsahatettehar"
		};
		
		long n1 = System.nanoTime();
		for (int i=0; i<test.length; i++) {
			words = words(new StringBuffer(test[i]));
			System.out.println(i + ": " + scoreWords(words) + ", " + test[i]);
		}
		long n2 = System.nanoTime();
		
		System.out.println((n2-n1) + " nanoseconds.");
		float rate = 1000000000f*100/(n2-n1);
		System.out.println(rate + " per second.");
		
		
		
		
	}

	public static int nonWilds(StringBuffer decoded) {
		int nonwilds = 0;
		for (int i=0; i<decoded.length(); i++) {
			if (decoded.charAt(i) != '?') nonwilds++;
		}
		return nonwilds;
	}

	
	public static void dump(BestMatches bm, StringBuffer decoded) {
		float[] scores = new float[] {bm.sums[0], bm.sums[1]};
		//System.out.println("Score: " + MultiObjectiveStatistics.dumpScores(scores) + ", Coverage: " + bm.coverage + ", nonwilds: " + nonWilds(decoded) + ", medianScore: " + bm.medianScore);
		System.out.println(decoded);
		String s = "";
		for (int i=0; i<decoded.length(); i++)
			
			if (bm.cover[i]) s+="_";
			else s+=decoded.charAt(i);
		
		System.out.println(s);
		if (bm.matches != null) {
			for (Match m : bm.matches) {
				String line = "";
				line += Settings.cipher.substring(Math.max(0, m.position-10), Math.max(0, m.position));
				line += "[" + Settings.cipher.substring(m.position, m.position+m.word.length()) + "]";
				line += Settings.cipher.substring(Math.min(Settings.cipher.length()-1, m.position+m.word.length()), Math.min(Settings.cipher.length(), m.position+m.word.length()+10));
				System.out.println(line);
				line = "";
				line += decoded.substring(Math.max(0, m.position-10), Math.max(0, m.position));
				line += "[" + decoded.substring(m.position, m.position+m.word.length()) + "]";
				line += decoded.substring(Math.min(decoded.length()-1, m.position+m.word.length()), Math.min(decoded.length(), m.position+m.word.length()+10));
				System.out.println(line);
				System.out.println("           " + m);
			}
		}
	}
	
	public static void testBestMatches() {
		LuceneService.init();
		initWordSymbolCounts(new StringBuffer(Ciphers.cipher[1].cipher));
		
		
		StringBuffer decoded = new StringBuffer("ilikekillingpeoplebecauseitissomuchfunitismorefunthankillingwildgameintheforrestbecausemanisthemoatdangertueanamalofalltokillsomethinggivesmethemoatthrillingexperenceitisevenbetterthangettingyourrocksoffwithagirlthebestpartofitiathaewhenidieiwillbereborninparadicesndalltheihavekilledwillbecomemyslavesiwillnotgiveyoumynamebecauseyouwilltrytosloidownorstopmycollectingofslavesformyafterlifeebeorietemethhpiti");
		bestMatches(decoded, false);
		decoded = new StringBuffer("il?ke?il?in?pe?pl?be?au?ei?is?om?ch?un?ti?mo?ef?nt?an?il?in?wi?dg?me?nt?ef?rr?st?ec?us?ma?is?he?oa?da?ge?tu?an?ma?of?ll?ok?ll?om?th?ng?iv?sm?th?mo?tt?ri?li?ge?pe?en?ei?is?ve?be?te?th?ng?tt?ng?ou?ro?ks?ff?it?ag?rl?he?es?pa?to?it?at?ae?he?id?ei?il?be?eb?rn?np?ra?ic?sn?al?th?ih?ve?il?ed?il?be?om?my?la?es?wi?ln?tg?ve?ou?yn?me?ec?us?yo?wi?lt?yt?sl?id?wn?rs?op?yc?ll?ct?ng?fs?av?sf?rm?af?er?if?eb?or?et?me?hh?it?");
		bestMatches(decoded, false);
	}
	
	public static void dumpMatches(List<Match> m1, List<Match> m2) {
		if (m1 != null)
			for (Match m : m1) {
				say(" - match1: " + m);
				if (m.word.length()+m.position >= Settings.cipher.length()) say("  - WTF, it goes beyond the end");
			}
		if (m2 != null)
			for (Match m : m2) {
				say(" - match2: " + m);
				if (m.word.length()+m.position >= Settings.cipher.length()) say("  - WTF, it goes beyond the end");
			}
	}
	
	public static void dumpCoverage(List<Match> list, StringBuffer sb) {
		StringBuffer cover = new StringBuffer(sb);
		//for (int i=0; i<sb.length(); i++) cover.append("_");
		
		for (Match m : list) {
			for (int i=0; i<m.word.length(); i++) cover.setCharAt(m.position+i, '_');  
		}
		System.out.println(cover);
	}
	
	public static void testPQ() {
		PriorityQueue<Integer> pq = new PriorityQueue<Integer>();
		
		for (int i=0; i<100; i++) {
			int val = (int)(100000*Math.random());
			pq.add(val);
		}
		while (!pq.isEmpty()) {
			System.out.println(pq.poll());
		}
	}
	
	public static void jurgen() {
		
		LuceneService.init();
		initWordSymbolCounts(new StringBuffer(Ciphers.cipher[0].cipher));
		
		
		
		String[][] letters = new String[][] {
				{"a","e"},
				{"b","c"},
				{"c","i"},
				{"i","p"},
				{"i","y"},
				{"n","g"},
				{"n","i"},
				{"n","o"},
				{"s","c"}
		};
		
		String decoded = "????ronlynchin8anfra62isco1a4rega36extradi8123ii5?dirr?no?el?rsaa?gascra5??ncw0ya?nn?r??n0os?en?n0?ao2iwagdc?adt?8l45ic28ada?isasfa5aticnrratai?a06tering?5odlandasr?r07e?da??w??coa6rtnrgsarto?dcewnnr4a?605icieiafcw06t2???o80fnawant4tln?aae6?hexnc???ldiw?act4?56g2srn4ard8a?tnaihi?ra0o16c3iaa31?51tysi?n?23rc0nn1r??6sas6e??t?ro?frni5is?rcg?a";
		String translated = decoded;
		for (int a = 0; a < 2; a++) {
			for (int b = 0; b < 2; b++) {
				for (int c = 0; c < 2; c++) {
					for (int d = 0; d < 2; d++) {
						for (int e = 0; e < 2; e++) {
							for (int f = 0; f < 2; f++) {
								for (int g = 0; g < 2; g++) {
									for (int h = 0; h < 2; h++) {
										for (int i = 0; i < 2; i++) {
											translated = decoded.replaceAll("0",letters[0][a]);
											translated = translated.replaceAll("1",letters[1][b]);
											translated = translated.replaceAll("2",letters[2][c]);
											translated = translated.replaceAll("3",letters[3][d]);
											translated = translated.replaceAll("4",letters[4][e]);
											translated = translated.replaceAll("5",letters[5][f]);
											translated = translated.replaceAll("6",letters[6][g]);
											translated = translated.replaceAll("7",letters[7][h]);
											translated = translated.replaceAll("8",letters[8][i]);
											System.out.println(translated);
											Map<Integer, PositionMatches> map = bestMatches(new StringBuffer(translated), false);
											StringBuffer sb1 = new StringBuffer("exact matches: ");
											StringBuffer sb2 = new StringBuffer("partial matches: ");
											for (Integer pos : map.keySet()) {
												boolean found1 = false;
												boolean found2 = false;
												PositionMatches pm = map.get(pos);
												if (pm.matches1 != null) {
													for (Match m : pm.matches1) {
														if (!found1) sb1.append("pos " + pos + ": ");
														found1 = true;
														sb1.append("[" + m.word + " " + m.score + "] ");
													}
												}
												if (pm.matches2 != null) {
													for (Match m : pm.matches2) {
														if (m.deleted) continue;
														if (!found2) sb2.append("pos " + pos + ": ");
														found2 = true;
														sb2.append("[" + m.word + " " + m.score + "] ");
													}
												}
												
											}
											System.out.println(sb1);
											System.out.println(sb2);
										}
									}
								}
							}
						}
					}
				}
			}
		}
		
	}

	public static void say(String message) {
		if (Settings.DEBUG) {
			System.out.println(message);
		}
	}
	
	public static void stringCompare(StringBuffer sb1, StringBuffer sb2) {
		int same = 0;
		String line = "String X: ";
		System.out.println("String 1: " + sb1);
		System.out.println("String 2: " + sb2);
		for (int i=0; i<Math.min(sb1.length(), sb2.length()); i++) {
			if (sb1.charAt(i) == sb2.charAt(i)) line += sb1.charAt(i); else line += "_";			
		}
		System.out.println(line);
	}
	
	/** start with a nearly half-correct genome from experiment 60 and see if mutation can reach a better solution */
	public static void testLucene060() {
		//int[] basegenome = new int[] { 14, 11, 11, 13, 7, 14, 19, 19, 4, 14, 8, 4, 7, 13, 22, 11, 17, 4, 18, 4, 19, 19, 18, 3, 19, 7, 4, 13, 0, 18, 18, 0, 4, 8, 12, 0, 4, 0, 4, 12, 18, 3, 3, 14, 17, 3, 5, 8, 4, 14, 13, 17, 0, 2};
		int[] basegenome = new int[] { 17,3,13,4,0,0,4,18,0,4,15,19,3,0,8,14,14,8,6,17,6,13,18,18,11,4,7,13,3,3,3,2,18,12,18,11,13,17,14,2,17,19,17,18,7,19,17,19,8,19,0,17,14,19,19,1,15,7,8,11,11,21,13,4,13};
		StringBuffer sb = decode(basegenome);
		System.out.println(Settings.zodiacsolution + ", " + NGrams.zkscore(Settings.zodiacsolution) + ", " + (1/(1+Stats.iocDiff(Settings.zodiacsolution))));
		System.out.println(sb + ", " + NGrams.zkscore(sb) + ", " + (1/(1+Stats.iocDiff(sb))));

		float[] nd1 = NGrams.ngraphDistance(1, sb);
		float[] nd2 = NGrams.ngraphDistance(2, sb);
		float[] nd3 = NGrams.ngraphDistance(3, sb);
		
		String nd = "";
		nd += "," + nd1[0] + "," + nd1[1];
		nd += "," + nd2[0] + "," + nd2[1];
		nd += "," + nd3[0] + "," + nd3[1];
		System.out.println(nd);
		
		double[] scores = new double[] {NGrams.zkscore(sb), 
				1/(1+Stats.iocDiff(sb)), 
				ProblemLucene.zodiacScore(sb), 
				nd1[0], nd1[1], nd2[0], nd2[1], nd3[0], nd3[1], 
				((double)1)/(1+NGrams.badNgraphs(2, sb)), 
				((double)1)/(1+NGrams.badNgraphs(3, sb)), 
				((double)1)/(1+NGrams.badNgraphs(4, sb)), 
				((double)1)/(1+NGrams.badNgraphs(5, sb)) 
		};
		
		MersenneTwisterFast random = new MersenneTwisterFast(); 
		int mutationType = -1;
		int count = 0;
		while (true) {
			int[] genome = basegenome.clone();
			int dart = random.nextInt(9);
			float prob = 0.05f;
			if (dart == 0) {
				mutationType = 0;
				LuceneVectorIndividual.mutateGenome(prob, (int) 0, (int) 25, random, genome);
			}
			else if (dart == 1) {
				mutationType = 1;
				LuceneVectorIndividual.mutateWord(random, genome);
			}
			else if (dart == 2) { 
				mutationType = 2;
				LuceneVectorIndividual.mutateDictionary(random, genome);
			}
			else if (dart == 3) { 
				mutationType = 3;
				LuceneVectorIndividual.mutateWithFreq(prob, random, genome);
			}
			else if (dart == 4) { 
				mutationType = 4;
				LuceneVectorIndividual.mutateSwap(random, genome);
			}
			else if (dart == 5) { 
				mutationType = 5;
				LuceneVectorIndividual.mutateShift(random, genome);
			}
			else if (dart == 6) { 
				mutationType = 6;
				LuceneVectorIndividual.mutateScramble(random, genome);
			}
			else if (dart == 7) { 
				mutationType = 7;
				LuceneVectorIndividual.mutateReverse(random, genome);
			}
			else if (dart == 8) { 
				mutationType = 8;
				LuceneVectorIndividual.mutateSwapGenome(random, genome);
			}
			
			sb = decode(genome);
			nd1 = NGrams.ngraphDistance(1, sb);
			nd2 = NGrams.ngraphDistance(2, sb);
			nd3 = NGrams.ngraphDistance(3, sb);
			double[] scores2 = new double[] {NGrams.zkscore(sb), 
					1/(1+Stats.iocDiff(sb)), 
					ProblemLucene.zodiacScore(sb), 
					nd1[0], nd1[1], nd2[0], nd2[1], nd3[0], nd3[1], 
					((double)1)/(1+NGrams.badNgraphs(2, sb)), 
					((double)1)/(1+NGrams.badNgraphs(3, sb)), 
					((double)1)/(1+NGrams.badNgraphs(4, sb)), 
					((double)1)/(1+NGrams.badNgraphs(5, sb)) 
			};
			
			boolean better = false;
			/*
			for (int i=0; i<scores2.length; i++) {
				if (scores2[i] > scores[i]) {
					better = true;
					break;
				}
			}*/
			better = (scores2[9]>scores[9] || scores2[10]>scores[10] || scores2[11]>scores[11] || scores2[12]>scores[12]) && (
					scores2[9]>=scores[9] && scores2[10]>=scores[10] && scores2[11]>=scores[11] && scores2[12]>=scores[12] &&
					scores2[1]>=scores[1]);
			if (better) {
				String s = mutationType + ",";
				for (int i=0; i<scores2.length; i++) {
					s += scores2[i] + "," + (scores2[i] - scores[i]) + ",";
				}
				s+="genome:[" + LuceneVectorIndividual.genomeToString(genome) + "]";
				System.out.println(s);
				count++;
				basegenome = genome;
				scores = scores2;
			}
			//if (count > 38767) break;
		}
		
		

		
	}
	
	public static void main(String[] args) {
		//StringBuffer sb = new StringBuffer("shtesirreshteltdtyetoogdterldcofcshtsrglateshedeshdgtrshitdhlyskbtwisterhdgthisthatlydsrsofseedltilingsorgtrdbghiliglingsolersafftchiygfetdjsitlotltertelrleslitypdgrebrditlosefdrelrsethligksofillttleselrgridthjdbgdtehtetsisovdlgansdtofdrhtghldsdbtdilijchesoerdreisgdrdtatderihjsthitsibitbdersjchrtdthsdpsprotisshojosbtjeexptrtiyleitltsbijte");
		//dump(score(sb,false),sb);
		//testPQ();
		//testWordSymbolCounts();
		//testBestMatches();
		//jurgen();
		testScore2();
		//testWords();
		//testLucene060();
		//String s = "TECULKSIIWIANSIASERNHCRTHHRNBTAPNEEEEDGTOFOODVYETOAMLU";
		//for (int i=0; i<s.length(); i++) System.out.println(s.charAt(i)-65);
		//System.out.println((int) 'A'-65);
	}
}
