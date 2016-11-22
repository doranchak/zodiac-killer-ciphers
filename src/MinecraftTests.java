
public class MinecraftTests {
	/**
	 * list of farm coordinates, NW corner only. all have 10 levels except
	 * the first farm which has only 3.
	 * 
	 * first layer is at y=3
	 * last layer is at y=39
	 * 
	 * elements are: {x, z, # of layers}
	 */
	static int[][] farms = new int[][] {
			{12640,	10160, 3},
			{12672, 10096, 10},
			{12704, 10128, 10},
			{12688, 10176, 10},
			{12640, 10240, 10}				
	};
	

	/** number of blocks that are within a radius of 24-32 in relation to a player */
	public static void countBlocksForMobs(boolean skipAir) {
		int count = 0;
		for (int x=-40; x<=40; x++) {
			for (int y=-40; y<=40; y++) {
				for (int z=-39; z<=40; z+=(skipAir ? 3 : 1)) {
					double d = distance(x,y,z);
					if (d >= 24 && d <= 32) count++;
				}
				
			}
		}
		// let's assume a spawning platform is 3x3 of blocks surrounded by 2-block wide trapdoors
		// that means the repeating pattern is 5x5 (25) blocks, with 9 spawning blocks and 16 trapdoors
		// so the ratio of spawnable blocks to total blocks is 9/25.
		
		System.out.println(count*9/25);
	}
	
	/** distance from (0,0,0) to (x,y,z) */
	public static double distance(int x, int y, int z) {
		int sum = x*x + y*y + z*z;
		return Math.sqrt(sum);
	}
	
	
	public static void noteScale() {
		for (int n=0; n<16; n++) {
			System.out.println(n+": " + noteScale(n));
		}
	}
	public static int noteScale(int n) {
		float f= 24;
		f /= 16;
		return (int) ( n * f);
	}
	
	public static void sim(float startBalance, int trials, float winProb) {
		int wins = 0;
		for (int i=0; i<trials; i++) {
			if (Math.random() <= winProb) {
				wins++;
				startBalance *= 1.25f;
			} else {
				startBalance *= 0.75;
			}
		}
		System.out.println("wins " + wins + " losses" + (trials-wins) + " final balance " + startBalance);
	}
	
	public static void cancellation(int units, int trials, float winProb) {
		
	}
	
	/** output (cols*rows) inventory positions, starting at upper left (x1,y1) and ending at lower right (x2,y2) */ 
	public static void inventorySlots(String prefix, int x1, int y1, int x2, int y2, int cols, int rows) {
		int dcol = (x2-x1)/(cols-1);
		int drow = (y2-y1)/(rows-1);
		int i=0;
		for (int r=0; r<rows; r++) {
			for (int c=0; c<cols; c++) {
				i++;
				String line = "export " + prefix + i + "=";
				int xx = x1+c*dcol;
				int yy = y1+r*drow;
				line += xx + "," + yy;
				System.out.println(line);
			}
		}
	}
	
	/** OSB slime farms, find optimal spots to afk */
	public static void slime() {
		// farm spawning pads are 16x16
		// surrounded by 4 block thick perimeter 
		// so slimes need to exist in 20x20 region
		
		// spawning rules:
		// 1) Slimes will not spawn within 24 blocks (spherical) of any player
		//       (apply this to the 16x16 platforms)
		// 2) will despawn over time if no player is within 32 blocks
		//       (apply this to all blocks within the 20x20x36 farm)
		// 3) will despawn instantly if no player is within 128 blocks
		//       (apply this to all blocks within the 20x20x36 farm)

		
		/*{12640,	10160},
		{12672, 10096},
		{12704, 10128},
		{12688, 10176},
		{12640, 10240}*/
		int count = 0;
		for (int x=12550; x<=12800; x++) {
			for (int y=1; y<=100; y++) {
				for (int z=10000; z<=10340; z++) {
					
					int numGood = 0; // number of positions that are 24-32 blocks from player
					int numBad = 0; // number of positions that are > 128 blocks from player
					
					boolean go = true;
					for (int[] farm : farms) {
						
						// don't allow player positions inside farm (but above it is ok)
						if (inside(x,y,z,farm)) {
							go = false;
							break;
						}
						
						int[] pos1 = {x,y,z};
						// don't allow any positions closer than 24 blocks to a spawnable block
						go = true;
						for (int layer=0; layer<farm[2]; layer++) {
							for (int row=0; row<16; row++) {
								for (int col=0; col<16; col++) {
									int[] pos2 = {farm[0]+col, layer*4+3, farm[1]+row};
									float distance = distance(pos1, pos2); 
									if (distance<=24) {
										go = false;
										break;
									}
									
									if (distance <= 32) numGood++;
									if (distance >= 128) numBad++;
									
								}
								if (!go) break;
							}
							if (!go) break;
						}
						if (!go) break;
						
					}
					if (!go) continue;
					float score = 1+numGood;
					score /= (1+numBad);
					System.out.println(x+" "+y+" "+z+" "+numGood+" "+numBad+" "+score);
					//if (numGood > 0) System.exit(0);
					count++;
					if (count % 1000 == 0) System.out.println(count+"...");
				}
			}
		}
		System.out.println(count);
	}
	
	/** return true if given coords inside the given farm */
	static boolean inside(int x, int y, int z, int[] farm) {
		if (y > 41) return false;
		if (farm[2] == 3 && y > 15) return false;
		if (x >= farm[0]-4 && x <= farm[0]+19) return true;		
		if (z >= farm[1]-4 && z <= farm[1]+19) return true;
		return false;
	}
	
	static float distance(int[] pos1, int[] pos2) {
		float sum = 0;
		sum += (pos1[0]-pos2[0])*(pos1[0]-pos2[0]);
		sum += (pos1[1]-pos2[1])*(pos1[1]-pos2[1]);
		sum += (pos1[2]-pos2[2])*(pos1[2]-pos2[2]);
		return (float) Math.sqrt(sum);
	}
	
	
	public static void main(String[] args) {
		//countBlocksForMobs(true);
		//noteScale();
		//sim(100f, 10, 0.4737f);
		//inventorySlots("PVINV", 626, 617, 1059, 789, 9, 4);
		//inventorySlots("PV", 628, 256, 1061, 527, 9, 6);
		slime();
	}
}
