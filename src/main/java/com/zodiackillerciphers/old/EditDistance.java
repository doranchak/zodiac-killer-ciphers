package com.zodiackillerciphers.old;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zodiackillerciphers.transform.CipherTransformations;

public class EditDistance {
	  //****************************
	  // Get minimum of three values
	  //****************************

	  private static int Minimum (int a, int b, int c) {
	  int mi;

	    mi = a;
	    if (b < mi) {
	      mi = b;
	    }
	    if (c < mi) {
	      mi = c;
	    }
	    return mi;

	  }

	  //*****************************
	  // Compute Levenshtein distance
	  //*****************************

	  public static int LD (String s, String t) {
	  int d[][]; // matrix
	  int n; // length of s
	  int m; // length of t
	  int i; // iterates through s
	  int j; // iterates through t
	  char s_i; // ith character of s
	  char t_j; // jth character of t
	  int cost; // cost

	    // Step 1

	    n = s.length ();
	    m = t.length ();
	    if (n == 0) {
	      return m;
	    }
	    if (m == 0) {
	      return n;
	    }
	    d = new int[n+1][m+1];

	    // Step 2

	    for (i = 0; i <= n; i++) {
	      d[i][0] = i;
	    }

	    for (j = 0; j <= m; j++) {
	      d[0][j] = j;
	    }

	    // Step 3

	    for (i = 1; i <= n; i++) {

	      s_i = s.charAt (i - 1);

	      // Step 4

	      for (j = 1; j <= m; j++) {

	        t_j = t.charAt (j - 1);

	        // Step 5

	        if (s_i == t_j) {
	          cost = 0;
	        }
	        else {
	          cost = 1;
	        }

	        // Step 6

	        d[i][j] = Minimum (d[i-1][j]+1, d[i][j-1]+1, d[i-1][j-1] + cost);

	      }

	    }

	    // Step 7

	    return d[n][m];

	  }
	  
	  /** Search the given text for all occurrences of the given search string with an edit distance of d */
	  public Map<Integer, List<String>> matches(String text, String search, int d) {
		  Map<Integer, List<String>> map = new HashMap<Integer, List<String>>();
		  return null;
	  }
	  
	  /** find all pairings of substrings of given length, and print
	   * only the pairings having an edit distance equal to the given threshold d.
	   */
	  public static int find(String text, int len, int d) {
		  int count = 0;
		  String s1;
		  String s2;
		  int ld;
		  Set<String> seen = new HashSet<String>();
		  System.out.println("Matches found for search strings of length <b>" + len + "</b> with an edit distance of <b>" + d + "</b>:");
		  System.out.println("<pre>");
		  
		  for (int i=0; i<text.length()-len+1; i++) {
			  s1 = text.substring(i,i+len);
			  
			  for (int len2=1; len2<len*2; len2++) {
			  for (int j=0; j<text.length()-len2+1; j++) {
				  if (i==j) continue; // ignore self-match
				  s2 = text.substring(j,j+len2);
				  if (s1.charAt(0) != s2.charAt(0)) continue; // ignore if first characters differ
				  if (s1.charAt(len-1) != s2.charAt(len2-1)) continue; // ignore if last characters differ
				  ld = LD(s1, s2);
				  if(ld==d && Math.abs(i-j) > d) { // within given distance, and non-overlapping
					  if (seen.contains(s1+","+s2+","+i+","+j)) continue; // ignore already-seen pairings
					  seen.add(s2+","+s1+","+j+","+i);
					  System.out.println(i+": "+s1+", "+j+": "+s2);
					  count++;
				  }
			  }
			  }
		  }
		  System.out.println("</pre>");
		  System.out.println("Total matches: " + count);
		  return count;
		  
		  
	  }
	  
	  static void findAll() {

		  // 408:
		  
		  // rot 90, no flip
		  String t1 = Zodiac.cipher408[1];
		  // rot 0, flip 
		  String t2 = Zodiac.cipher408[2];
		  // rot 90, flip
		  String t3 = Zodiac.cipher408[3];
		  
		  // 340:
		  // rot 90, no flip
		  String t4 = Zodiac.cipher340[1];
		  // rot 0, flip 
		  String t5 = Zodiac.cipher340[2];
		  // rot 90, flip
		  String t6 = Zodiac.cipher340[3];
		  
		  
		  for (int len=2; len<13; len++) {
				 for (int d=0; d<=len/2; d++) {
					int c1 = find(Zodiac.cipher[1], len, d);
					int ct1 = find(t1, len, d);
					int ct2 = find(t2, len, d);
					int ct3 = find(t3, len, d);
					int c2 = find(Zodiac.cipher[0], len, d);
					int ct4 = find(t4, len, d);
					int ct5 = find(t5, len, d);
					int ct6 = find(t6, len, d);
					
					int max1 = Math.max(c1, Math.max(ct1, Math.max(ct2, ct3)));
					int max2 = Math.max(c2, Math.max(ct4, Math.max(ct5, ct6)));
					
					if (max1 == 0) max1 = Integer.MAX_VALUE;
					if (max2 == 0) max2 = Integer.MAX_VALUE;
					
					System.out.println("|-valign=\"top\"");
					System.out.println("| style=\"text-align: right; border-style: solid; border-width: 1px\"|" + len);
					System.out.println("| style=\"text-align: right; border-style: solid; border-width: 1px\"|" + d);
					System.out.println("| style=\"background-color: #" + (c1 == max1 ? "ddd" : "fff") + "; text-align: right; border-style: solid; border-width: 1px\"|" + (c1 == 0 ? "0" : "[[C1L" + len + "D" + d+ "|" + c1 + "]]"));
					System.out.println("| style=\"background-color: #" + (ct1 == max1 ? "ddd" : "fff") + "; text-align: right; border-style: solid; border-width: 1px\"|" + (ct1 == 0 ? "0" : "[[CT1L" + len + "D" + d+ "|" + ct1 + "]]"));
					System.out.println("| style=\"background-color: #" + (ct2 == max1 ? "ddd" : "fff") + "; text-align: right; border-style: solid; border-width: 1px\"|" + (ct2 == 0 ? "0" : "[[CT2L" + len + "D" + d+ "|" + ct2 + "]]"));
					System.out.println("| style=\"background-color: #" + (ct3 == max1 ? "ddd" : "fff") + "; text-align: right; border-style: solid; border-width: 1px\"|" + (ct3 == 0 ? "0" : "[[CT3L" + len + "D" + d+ "|" + ct3 + "]]"));
					System.out.println("| style=\"background-color: #" + (c2 == max2 ? "ddd" : "fff") + "; text-align: right; border-style: solid; border-width: 1px\"|" + (c2 == 0 ? "0" : "[[C0L" + len + "D" + d+ "|" + c2 + "]]"));
					System.out.println("| style=\"background-color: #" + (ct4 == max2 ? "ddd" : "fff") + "; text-align: right; border-style: solid; border-width: 1px\"|" + (ct4 == 0 ? "0" : "[[CT4L" + len + "D" + d+ "|" + ct4 + "]]"));
					System.out.println("| style=\"background-color: #" + (ct5 == max2 ? "ddd" : "fff") + "; text-align: right; border-style: solid; border-width: 1px\"|" + (ct5 == 0 ? "0" : "[[CT5L" + len + "D" + d+ "|" + ct5 + "]]"));
					System.out.println("| style=\"background-color: #" + (ct6 == max2 ? "ddd" : "fff") + "; text-align: right; border-style: solid; border-width: 1px\"|" + (ct6 == 0 ? "0" : "[[CT6L" + len + "D" + d+ "|" + ct6 + "]]"));
					
				 }
			  }
	  }
	  
	  /** look for similar patterns that repeat within a string, based on the idea that a small amount of edits will make
	   * the patterns identical 
	   */
	public static void findSimilarPatterns(String text, int minLength, int maxLength, int maxDistance) {
		for (int a = 0; a < text.length() - 1; a++) {
			for (int b = a + 1; b < text.length(); b++) {
				String sub1 = text.substring(a, b);
				if (sub1.length() < minLength)
					continue;
				if (sub1.length() > maxLength)
					continue;
				for (int c = b; c < text.length() - 1; c++) {
					for (int d = c + 1; d < text.length() - 1; d++) {
						String sub2 = text.substring(c, d);
						if (sub2.length() < minLength)
							continue;
						if (sub2.length() > maxLength)
							continue;
						if (Math.abs(sub2.length() - sub1.length()) > maxDistance)
							continue;
						int distance = LD(sub1, sub2);
						if (distance > maxDistance) continue;
						System.out.println(Math.max(sub1.length(), sub2.length()) + "	" + distance + "	" + a + "	" + c + "	" + sub1 + "	" + sub2);
					}
				}

			}
		}
	}
	
	/** find clusters of ngrams that are related by a fixed number of edits.
	 * for example, CAT, BAT, FAT, and FAD are all linked because they can be linked to each other with a single edit operation each. 
	 */
	public static void findClusters(String text, int n, int editDistance) {
		List<Set<String>> clusters = new ArrayList<Set<String>>();

		Set<String> seen = new HashSet<String>();
		// init clusters.  each cluster has exactly one ngram to start.
		
		for (int i=0; i<text.length()-n+1; i++) {
			String ngram = text.substring(i, i+n);
			if (seen.contains(ngram)) continue; // avoid dupes
			seen.add(ngram);
			Set<String> set = new HashSet<String>();
			set.add(ngram);
			clusters.add(set);
		}
		
//		List<Set<String>> newClusters = new ArrayList<Set<String>>();
		// examine all pairs of clusters
		
		boolean found = true;
		while (found) {
			found = false;
			for (int i=0; i<clusters.size()-1; i++) {
				for (int j=i+1; j<clusters.size(); j++) {
					Set<String> cluster1 = clusters.get(i);
					Set<String> cluster2 = clusters.get(j);
					if (related(cluster1, cluster2, editDistance)) {
						cluster1.addAll(cluster2);
						cluster2.clear();
						found = true;
					} else {
					}
				}
			}
			// remove empty clusters
			for (int i=clusters.size()-1; i>=0; i--) {
				if (clusters.get(i).isEmpty())
					clusters.remove(i);
			}
		}
		for (Set<String> cluster : clusters)
			if (cluster.size() > 1) 
				System.out.println(cluster.size() + " CLUSTER: " + cluster);
	}
	
	/** two clusters are related if at least one ngram from the first has the given edit distance to at least one ngram from the second */
	static boolean related(Set<String> cluster1, Set<String> cluster2, int editDistance) {
		for (String ngram1 : cluster1) {
			for (String ngram2 : cluster2) {
				int distance = LD(ngram1, ngram2);
				if (distance == editDistance) {
//					System.out.println(distance + " " + ngram1 + " " + ngram2);
					return true;
				}
			}
		}
		return false;
	}

	  
	  public static void main(String[] args) {
		  //findAll();
		  System.out.println(LD("kitten","kittn"));
//		  String cipher = "YMBFPTYSYSNAOSBYAACPTMYSNEMTMOIYJGTEBYNIAJAPSNDETMYLGAIdISYMIHFBYTFAAWOTYCMCHNEFTYIHSAWOYYHNEAWOWIWLTDIHAJBSASTFYTMMFdOETTDITWYGMTIPDTTOFYANRSITLOACYANWYAJAHCTDSIHNDONIPEICNCOACOHMOOOTYPMDISWtOKIEYWOP,R,AAOTWFFFYOTYOMIHLYDWICSTOTIhhNCFBYHNLAOWALYCWOWTDSOLIOMWWWYNTLTYMYFYLNAWNMWSSINDIDCWGOOBOYAYOFWGYKODLIAALACNSuFMSIAtOJBaHTYIANaWJSWDETOMYHABEODIASMGWIhdtTOTYHNdTIAPASYOAYdTSWBAAF,YFNSmTMOTOHYANSYJDWMABTdEKMMSAIHO,M,LTPYANWADNWTSWISAIHNGTSTBFANTdWL.IAAYYAARYNTTMIFLYTIABYWYEWGOTAYNTTMWITACOSSOCAYSLIYATMTTOTTNAAMDOTWYNSOETBRNBAHTMAYSIDKINADRHBOFTWTTFBWSYJSBYKIWBMATOGTDALAYANIATADWCBYDDLOAHWIAFHWSTAYSTFOHSELTODTNOAMaHMAHWCUALFMTFYM.YNWTGTOG.AHTCHASOD.ATRISWNB.WISIAAOHB,BYCSTSIPYAAFITIYWSTASIABBaagiWSYCIATOYSIAWyd,BHSAIIMYMMFUIOIYRYCHCLEFAOWNHOTSADIHIWYCTTACBIWLFYTTiiM,LPIDOE,GSSIiMaTCIDLSDMSFHATHTLTIFTNWIATOYLMJOMATLOIMFJMWYTOOMdYLDBLPCEAT";
//		  cipher = CipherTransformations.shuffle(cipher);
//		  findSimilarPatterns(cipher, 6, 20, 2);
//		  findClusters(cipher, 4, 1);
//		  findSimilarPatterns(CipherTransformations.shuffle(cipher), 6, 20, 2);
		  /*
		  for (int len=2; len<13; len++) {
				 for (int d=0; d<=len/2; d++) {
					 int c1 = find(Zodiac.cipher[0], len, d);
				 }
		  }*/
	  }
}
