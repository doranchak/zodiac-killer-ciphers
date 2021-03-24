import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Minion {
	/** highest tier for a minion */
	public static int TIER_MAX = 11;

	public static Map<String, int[]> requiredItemsMap;
	public static Map<String, int[]> periodMap;
	static {
		requiredItemsMap = new HashMap<String, int[]>();
		requiredItemsMap.put("Coal", new int[] {
				80, 160, 320, 512, 1280, 3840, 10240, 20480, 40960, 81920, 204800
		});
		requiredItemsMap.put("Cobblestone", new int[] {
				80, 160, 320, 512, 1280, 2560, 5120, 10240, 20480, 40960, 82920
		});
		requiredItemsMap.put("EndStone", new int[] {
				8*10, 8*20, 8*40, 8*64,
				8*5*32, 8*2*5*32, 8*4*5*32, 8*8*5*32,
				8*16*5*32, 8*32*5*32, 8*64*5*32
		});
		requiredItemsMap.put("Gold", new int[] {
				8*10, 8*20, 8*40, 8*64, 8*1*5*32, 8*3*5*32, 8*8*5*32, 8*16*5*32, 8*32*5*32, 8*64*5*32, 8*5*32*5*32
		});
		requiredItemsMap.put("Oak", new int[] {
				10*8, 20*8, 40*8, 64*8, 1*8*5*32, 2*8*5*32, 4*8*5*32, 8*8*5*32, 16*8*5*32, 32*8*5*32, 64*8*5*32
		});
		requiredItemsMap.put("Redstone", new int[] {
				16*8, 32*8, 64*8, 5*32*1, 5*32*3, 5*32*8, 5*32*16, 5*32*32, 5*32*64, 8*5*32*5*32, 16*5*32*5*32 
		});
		requiredItemsMap.put("Bone", new int[] {
				10*8, 20*8, 40*8, 64*8, 1*8*5*32, 2*8*5*32, 4*8*5*32, 8*8*5*32, 16*8*5*32, 32*8*5*32, 64*8*5*32
		});
		requiredItemsMap.put("Cave Spider", new int[] {
				10*8, 20*8, 40*8, 64*8, 1*8*5*32, 3*8*5*32, 8*8*5*32, 16*8*5*32, 32*8*5*32, 64*8*5*32, 8*2*64*5*32
		});
		requiredItemsMap.put("Enderman", new int[] {
				8*8, 16*8, 8*4*5, 3*8*4*5, 6*8*4*5, 12*8*4*5, 8*16*4*5, 3*8*16*4*5, 6*8*16*4*5, 12*8*16*4*5, 24*8*16*4*5
		});
		requiredItemsMap.put("Clay", new int[] {
				80, 160, 320, 512, 1280, 2560, 5120, 10240, 20480, 40960, 163632
		});
		requiredItemsMap.put("Porkchop", new int[] {
				8*8, 16*8, 32*8, 64*8, 8*5*32, 3*8*5*32, 8*8*5*32, 16*8*5*32, 32*8*5*32, 64*8*5*32, 8*5*32*5*32 
		});
		requiredItemsMap.put("Lapis", new int[] {
				32*8, 64*8, 8*5*32, 3*8*5*32, 8*8*5*32, 16*8*5*32, 32*8*5*32, 64*8*5*32, 8*5*32*5*32, 2*8*5*32*5*32, 4*8*5*32*5*32  
		});
		requiredItemsMap.put("Diamond", new int[] {
				80, 160, 320, 512, 8*5*32, 24*5*32, 64*5*32, 128*5*32, 256*5*32, 512*5*32, 8*5*32*5*32  
		});

		periodMap = new HashMap<String, int[]>();
		periodMap.put("Coal", new int[] {
				15, 15, 13, 13, 12, 12, 10, 10, 9, 9, 7
		});
		periodMap.put("Cobblestone", new int[] {
				14, 14, 12, 12, 10, 10, 9, 9, 8, 8, 7
		});
		periodMap.put("EndStone", new int[] {
				26, 26, 24, 24, 22, 22, 19, 19, 16, 16, 13
		});
		periodMap.put("Gold", new int[] {
				22, 22, 20, 20, 18, 18, 16, 16, 14, 14, 11
		});
		periodMap.put("Oak", new int[] {
				48, 48, 45, 45, 42, 42, 38, 38, 33, 33, 27
		});
		periodMap.put("Redstone", new int[] {
				29, 29, 27, 27, 25, 25, 23, 23, 21, 21, 18
		});
		periodMap.put("Bone", new int[] {
				26, 26, 24, 24, 22, 22, 20, 20, 17, 17, 13
		});
		periodMap.put("Cave Spider", new int[] {
				26, 26, 24, 24, 22, 22, 20, 20, 17, 17, 13
		});
		periodMap.put("Enderman", new int[] {
				32, 32, 30, 30, 28, 28, 25, 25, 22, 22, 18
		});
		periodMap.put("Clay", new int[] {
				32, 32, 30, 30, 28, 28, 24, 24, 20, 20, 16 
		});
		periodMap.put("Porkchop", new int[] {
				26, 26, 24, 24, 22, 22, 20, 20, 17, 17, 13
		});
		periodMap.put("Lapis", new int[] {
				29, 29, 27, 27, 25, 25, 23, 23, 21, 21, 18
		});
		periodMap.put("Diamond", new int[] {
				29, 29, 27, 27, 25, 25, 22, 22, 19, 19, 15
		});
		// each period is between actions.  most times, it takes two actions to collect a thing.
		// so convert to the real wait time between collections.
		for (String key : periodMap.keySet()) {
			int[] periods = periodMap.get(key);
			for (int i=0; i<periods.length; i++) {
				periods[i] *= 2;
			}
		}
	}
	
	/** i think there are 49 different types */
	public static int MINION_TYPES = 49;
	
	int level;
	String type;

	public Minion(int level, String type) {
		this.level = level;
		this.type = type;
	}
	
	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
	
	public int getPeriod() {
		return periodMap.get(type)[level-1];
	}
	
	public int itemsNeededForNextLevel() {
		return requiredItemsMap.get(type)[level];
	}
	public int itemsNeededForLevel(int level) {
		return requiredItemsMap.get(type)[level-1];
	}
	
	public String toString() {
		return type + " " + level; 
	}
	
	public Minion upgrade() {
		if (level == TIER_MAX) return null;
		Minion m = new Minion(this.level+1, this.type);
		return m;
	}

	public static void main(String[] args) {
		for (String key : requiredItemsMap.keySet()) {
			System.out.println(key + ": " + Arrays.toString(requiredItemsMap.get(key)));
		}
		for (String key : periodMap.keySet()) {
			System.out.println(key + ": " + Arrays.toString(periodMap.get(key)));
		}
	}
}
