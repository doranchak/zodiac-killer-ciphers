package com.zodiackillerciphers.old.rectangle;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Selection {
	static int H = 20;
	static int W = 17;

	static int MIN_AREA = 0;
	static void countRectangles() {
		
		Date start = new Date();
		Map<Integer, Integer> counts = new HashMap<Integer, Integer>();
		int total = 0;
		
		int minarea;
		
		for (int row1=0; row1<H; row1++) { // 1st rectangle UL corner
			for (int col1=0; col1<W; col1++) { // 1st rectangle UL corner
				
				for (int row2=row1; row2<H; row2++) { // 1st rectangle BR corner
					for (int col2=col1; col2<W; col2++) { // 1st rectangle BR corner

						if (tooSmall(row1, col1, row2, col2)) continue;
						
						for (int row3=0; row3<H; row3++) { // 2nd rectangle UL corner
							//System.out.println("("+row1+","+col1+"), (" + row2+","+col2+") " + row3);
							for (int col3=0; col3<W; col3++) { // 2nd rectangle UL corner
								if (overlap(row3, col3, row1, col1, row2, col2)) continue; // this corner overlaps the 1st rectangle.

								
								for (int row4=row3; row4<H; row4++) { // 2nd rectangle BR corner
									for (int col4=col3; col4<W; col4++) { // 2nd rectangle BR corner
										
										if (tooSmall(row3, col3, row4, col4)) continue;
										if (overlap(row4, col4, row1, col1, row2, col2)) continue; // this corner overlaps the 1st rectangle.
										
/*
										for (int row5=0; row5<H; row5++) { // 3rd rectangle UL corner
											for (int col5=0; col5<W; col5++) { // 3rd rectangle UL corner
												if (overlap(row5, col5, row1, col1, row2, col2)) continue; // this corner overlaps the 1st rectangle
												if (overlap(row5, col5, row3, col3, row4, col4)) continue; // this corner overlaps the 2nd rectangle
												
												for (int row6=row5; row6<H; row6++) { // 3rd rectangle BR corner
													for (int col6=col5; col6<W; col6++) { // 3rd rectangle BR corner
														if (tooSmall(row5, col5, row6, col6)) continue;
														if (overlap(row6, col6, row1, col1, row2, col2)) continue; // this corner overlaps the 1st rectangle
														if (overlap(row6, col6, row3, col3, row4, col4)) continue; // this corner overlaps the 2nd rectangle
*/
													
														total++;
														
														minarea = area(row1,col1, row2,col2);
														minarea = Math.min(minarea, area(row3,col3, row4,col4));
//														minarea = Math.min(minarea, area(row5,col5, row6,col6));
														Integer val = counts.get(minarea);
														if (val == null) val = 0;
														val++;
														counts.put(minarea, val);
														
	//												}
		//										}
			//								}
				//						}
										
										
										
										
									}
								}
							}
						}
					}
				}
				
				
			}
		}
		Map<Integer, Integer> sortedMap = new TreeMap(counts);
		for (Integer key : sortedMap.keySet())
			System.out.println("minarea " + key + " count " + sortedMap.get(key));
		System.out.println("Total number of rectangles: " + total);
		for (int i=1; i<H*W; i++) {
			Integer val = sortedMap.get(i);
			if (val == null) continue;
			total -= val;
			System.out.println("Removed all with area <= " + i + ": " + total);
		}
		
		Date end = new Date();
		System.out.println("Time: " + (end.getTime()-start.getTime()) + " ms");
	}
	
	static int area(int row1, int col1, int row2, int col2) {
		assert row2>=row1: "Bad rows";
		assert col2>=col1: "Bad cols";
		return (row2-row1+1)*(col2-col1+1);
	}
	
	static boolean tooSmall(int row1, int col1, int row2, int col2) {
		return area(row1,col1,row2,col2) < MIN_AREA; 
	}

	static boolean overlap(int row1, int col1, int row2, int col2, int row3, int col3) {
		if (row1 >= row2 && row1 <= row3 && col1 >= col2 && col2 <= col3) return true;
		return false;
	}
	public static void main(String[] args) {
		countRectangles();
	}
}
