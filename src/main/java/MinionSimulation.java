import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class MinionSimulation {
	/** simulate hypixel skyblock minions.  trying to figure out fastest path to unlocking Tier XVII (17) (being able to place 21 minions) */

	public static Random rand = new Random();
	
	/** start the simulation */
	public void start() {
		
	}
	
	public static int maxLevel(List<Minion> minions) {
		int level = 0;
		if (minions == null) return level;
		for (Minion m : minions) {
			level = Math.max(level, m.getLevel());
		}
		return level;
	}
	
	/** solve for the best next step, for a given minion type and set of initial minions.
	 * let max = the max level achieved by any minion in the list.	 *  
	 * the step that is "best" is the one that levels up a minion to (max+1) the fastest, thus contributing towards
	 * unlocking the next minion slot)
	 **/
	public static void solve(String type, int remainingSlots, List<Minion> currentMinions, int[][] targets, boolean ignoreWorse) {
		if (currentMinions == null) return;
		List<String> steps = new ArrayList<String>();
		solve(type, remainingSlots, currentMinions, targets, 0, 0, steps, new int[] {Integer.MAX_VALUE}, ignoreWorse);
	}
	public static void solve(String type, int remainingSlots, List<Minion> currentMinions, int[][] targets, int depth, int time, List<String> steps, int[] bestTime, boolean ignoreWorse) {
//		String indent = "";
//		for (int i=0; i<depth; i++) 
//			indent += "	";
//		System.out.println(indent + remainingSlots + ", " + currentMinions + ", " + steps);

		solveAdd(currentMinions, bestTime, steps, targets, 
				time, type, remainingSlots, depth, ignoreWorse);
		solveUpgrade(currentMinions, bestTime, steps, targets,
				time, type, remainingSlots, depth, ignoreWorse);
	}
	
	/** compute execution time (in seconds) needed to reach goal number of generated items, using n minions, which are always upgraded to the 
	 * given max tier whenever enough resources are produced.
	 * tiers are numbered starting at 0. 
	 * it is assumed that at time=0 we already have n instances of tier 0 minions.
	 */
	public static double computeExecutionTime(String type, int maxTier, int numMinions, long numItems) {
		// time to generate numItems at the max tier
//		System.out.println(type + ", " + Arrays.toString( Minion.periodMap.get(type)));
		double itemTime = numItems * Minion.periodMap.get(type)[maxTier];
		itemTime /= numMinions;
//		System.out.println("itemTime "+ itemTime + " " + numItems + " " + Minion.periodMap.get(type)[maxTier]);
		
		// time to upgrade to get to the max tier
		for (int i=1; i<=maxTier; i++) {
			double upgradeTime = Minion.requiredItemsMap.get(type)[i] * Minion.periodMap.get(type)[i-1];
			upgradeTime /= numMinions;
//			System.out.println(" - upgradeTime " + upgradeTime);
			itemTime += upgradeTime;
		}
		return itemTime;
	}

	public static boolean hit(List<Minion> minions, int[][] targets) {
		Map<Integer, Integer> targetsMap = new HashMap<Integer, Integer>(); 
		for (Minion m : minions) {
			Integer val = targetsMap.get(m.getLevel()); // number of minions at this level
			if (val == null) val = 0;
			val++;
			targetsMap.put(m.getLevel(), val);
		}
		
//		System.out.println(targetsMap + ", " + minions + ", " + Arrays.toString(targets));
		for (int i=0; i<targets.length; i++) {
			int level = targets[i][0];
			int count = targets[i][1];
			Integer val = targetsMap.get(level);
			if (val == null || val < count) return false;
		}
		return true;
	}
	public static void solveUpgrade(List<Minion> currentMinions, int[] bestTime, List<String> steps, int[][] targets,
			int time, String type, int remainingSlots, int depth, boolean ignoreWorse) {
		/*
		 * track which levels we've seen, because it's redundant to re-explore upgrading
		 * to the same level more than once
		 */
		Set<Integer> seenLevels = new HashSet<Integer>();
		/* shuffle the minions so we explore them randomly */
//		shuffle(currentMinions);
		for (int i = 0; i < currentMinions.size(); i++) {
			// System.out.println(indent + i);
			Minion oldMinion = currentMinions.get(i);
			Minion newMinion = oldMinion.upgrade();
			if (newMinion == null)
				continue;
			if (seenLevels.contains(newMinion.getLevel()))
				continue;
			seenLevels.add(newMinion.getLevel());

			// calculate how many items for next level, and how long it will take
			int items = oldMinion.itemsNeededForNextLevel();
			int newTime = timeToGenerateItems(currentMinions, items);
			if (ignoreWorse && (time + newTime > bestTime[0]))
				continue; // early abort if this is worst than best seen

			steps.add("Upgrade " + newMinion.getLevel() + " items "+ items + " time " + newTime + " periods " + periods(currentMinions));
			currentMinions.set(i, newMinion);

			if (hit(currentMinions, targets)) {
				System.out.println(hours(time + newTime) + "	" + "target reached.  " + steps + ", " + currentMinions);
				bestTime[0] = time + newTime;
			} else {
				solve(type, remainingSlots, currentMinions, targets, depth + 1, time + newTime, steps, bestTime, ignoreWorse);
			}
			currentMinions.set(i, oldMinion);
			steps.remove(steps.size() - 1);
		}
	}
	
	public static float hours(int time) {
		float hours = time;
		hours = hours / 60 / 60;
		return hours;
	}
	public static void solveAdd(List<Minion> currentMinions, int[] bestTime, List<String> steps, int[][] targets,
			int time, String type, int remainingSlots, int depth, boolean ignoreWorse) {
		// option: craft a new level 1 minion
		if (remainingSlots > 0) {
			Minion m = new Minion(1, type);
			
			// calculate how many items for next level, and how long it will take
			int items = m.itemsNeededForLevel(1);
			int newTime = timeToGenerateItems(currentMinions, items);
			if (ignoreWorse && (time+newTime > bestTime[0])) return; // early abort if this is worst than best seen
			
			currentMinions.add(m);
			steps.add("Add items " + items + " time " + newTime);
			solve(type, remainingSlots-1, currentMinions, targets, depth+1, time+newTime, steps, bestTime, ignoreWorse);
			currentMinions.remove(m);
			steps.remove(steps.size()-1);
		}
	}
	/** scramble array in place using fisher yates shuffle */
	public static void shuffle(List<Minion> list) {
		Random rand = new Random();
		if (list == null || list.isEmpty()) return;;
		for (int i=list.size()-1; i>=1; i--) {
			int j = rand.nextInt(i+1);
			Minion tmp = list.get(i);
			list.set(i, list.get(j));
			list.set(j, tmp);
		}
	}
	public static int timeToGenerateItems(List<Minion> minions, int items) {
		if (minions == null) throw new IllegalArgumentException("Minions cannot be null");
//		String debug = "";
		if (items == 0) return 0;
		double sum = 0;
		for (Minion m : minions) {
			double val = m.getPeriod();
			val = 1/val;
			sum += val;
//			debug += " period " + m.getPeriod() + " val " + val + " sum " + sum;
		}
		sum = items/sum;
//		debug += " result " + (int) Math.ceil(sum);
//		System.out.println(debug);
		return (int) Math.ceil(sum);
	}
	public static String periods(List<Minion> minions) {
		if (minions == null) throw new IllegalArgumentException("Minions cannot be null");
		StringBuilder sb = new StringBuilder();
		for (Minion m : minions) {
			if (sb.length() > 0) sb.append(" ");
			sb.append(m.getPeriod());
		}
		return sb.toString();
	}

	public static void testCobblestone1() {
		List<Minion> minions = new ArrayList<Minion>();
		minions.add(new Minion(6, "Cobblestone"));
		minions.add(new Minion(6, "Cobblestone"));
		minions.add(new Minion(6, "Cobblestone"));
		minions.add(new Minion(6, "Cobblestone"));
		minions.add(new Minion(6, "Cobblestone"));
		minions.add(new Minion(6, "Cobblestone"));
		minions.add(new Minion(6, "Cobblestone"));
		minions.add(new Minion(6, "Cobblestone"));
		minions.add(new Minion(9, "Cobblestone"));
		solve("Cobblestone", /*slotsremaining*/ 0, minions, /*targetlevel*/ new int[][] {{10, 1}}, /*ignoreWorse*/ true);
	}
	public static void testCobblestone2() {
		List<Minion> minions = new ArrayList<Minion>();
		minions.add(new Minion(4, "Cobblestone"));
		solve("Cobblestone", /*slotsremaining*/ 10, minions, /*targetlevel*/ new int[][] {{5, 1}}, /*ignoreWorse*/ true);
		
		// starting with a single level 3, best plan is:
		// - Add, Add, Upgrade 4 (0.8 hours): {4,1,1}
		// - Add, Add, Add, Upgrade 5 (0.56 hours): {5,1,1,1,1,1}
		// - Add, Add, Add, Add, Add, Upgrade 6 (0.63 hours) 
	}
	public static void testCoal1() {
		List<Minion> minions = new ArrayList<Minion>();
		minions.add(new Minion(6, "Coal"));
		minions.add(new Minion(6, "Coal"));
		minions.add(new Minion(6, "Coal"));
		minions.add(new Minion(7, "Coal"));
		solve("Coal", /*slotsremaining*/ 6, minions, /*targetlevel*/ new int[][] {{8, 1}}, /*ignoreWorse*/ true);
	}
	public static void testRedstone1() {
		List<Minion> minions = new ArrayList<Minion>();
		minions.add(new Minion(5, "Redstone"));
		minions.add(new Minion(1, "Redstone"));
		minions.add(new Minion(1, "Redstone"));
		solve("Redstone", /*slotsremaining*/ 5, minions, /*targetlevel*/ new int[][] {{6, 1}}, /*ignoreWorse*/ true);
	}
	public static void testOak1() {
		List<Minion> minions = new ArrayList<Minion>();
		minions.add(new Minion(4, "Oak"));
		minions.add(new Minion(5, "Oak"));
		minions.add(new Minion(6, "Oak"));
		for (int i=0; i<10; i++) {
			System.out.println("solving for " + i + " remaining slots ========================");
			solve("Oak", /*slotsremaining*/ i, minions, /*targetlevel*/ new int[][] {{7, 1}}, /*ignoreWorse*/ true);
		}
	}
	public static void testGold1() {
		List<Minion> minions = new ArrayList<Minion>();
		minions.add(new Minion(6, "Gold"));
		minions.add(new Minion(1, "Gold"));
		minions.add(new Minion(1, "Gold"));
		minions.add(new Minion(1, "Gold"));
		minions.add(new Minion(1, "Gold"));
		minions.add(new Minion(1, "Gold"));
		minions.add(new Minion(1, "Gold"));
		minions.add(new Minion(1, "Gold"));
		minions.add(new Minion(1, "Gold"));
		minions.add(new Minion(1, "Gold"));
		minions.add(new Minion(1, "Gold"));
		solve("Gold", /*slotsremaining*/ 0, minions, /*targetlevel*/ new int[][] {{2, 10}}, /*ignoreWorse*/ true);
	}
	public static void testEndStone() {
		List<Minion> minions = new ArrayList<Minion>();
		minions.add(new Minion(10, "EndStone"));
		minions.add(new Minion(10, "EndStone"));
		minions.add(new Minion(6, "EndStone"));
		minions.add(new Minion(3, "EndStone"));
		minions.add(new Minion(1, "EndStone"));
		minions.add(new Minion(1, "EndStone"));
		minions.add(new Minion(1, "EndStone"));
		minions.add(new Minion(1, "EndStone"));
		minions.add(new Minion(1, "EndStone"));
		minions.add(new Minion(1, "EndStone"));
		minions.add(new Minion(1, "EndStone"));
		minions.add(new Minion(1, "EndStone"));
		solve("EndStone", /*slotsremaining*/ 12-minions.size(), minions, /*targetlevel*/ new int[][] {{11, 1}}, /*ignoreWorse*/ true);
		// using all 12 slots:
		// 2.6 hr to level 2  (upgrade2)
		// 3.3 hr to level 3 (add add upgrade3)
		// 2.1 hr to level 4 (add add add upgrade4)
		// 2.4 hr to level 5 (add add add add add add upgrade5) (slots full)
		// 3.0 hr to level 6
		// 6.1 hr to level 7
		// 12.0 hr to level 8
		// 24.0 hr to level 9
		// 46.9 hr to level 10
		
	}
	
	/** what is the best tier to max minions to for the given number of items? */
	public static void testItemTimes() {
		// Results:
		// - Bone, 19, 30720: V (but only .6 hours faster than III)
		// - Cobblestone, 19, 1433600 (for 20 super compactors): XI (333.6 hours)
		// - Cave Spider, 19, 245760 (for leaping sword): V (162 hours) (only 10 hours faster than III)
		// - Enderman, 19, 10240 (for aote sword): III (9.25 hours)
		// - Porkchop, 19, 1,228,800 (for pigman sword): XI (656.1 hours = 27.3 days)
		// - Lapis, 19, 6 ench block (for titanic xp): III (123 hours = 5.1 days)
		
		for (int i=0; i<11; i++) {
//			double time = computeExecutionTime("Enderman", i , 19, 0);
//			double time = computeExecutionTime("Porkchop", i , 19, 1228800);
			double time = computeExecutionTime("Diamond", i , 19, 614400+1024000);
			time = time / 60 / 60;
			System.out.println("maxTier " + i + " time " + time + " hrs (" + (time/24) + " days)");
		}
	}
	
	public static void main(String[] args) {
//		testCobblestone2();
//		testEndStone();
//		testCoal1();
//		testRedstone1();
//		testGold1();
//		testOak1();
		testItemTimes();
//		System.out.println(Arrays.toString(Minion.requiredItemsMap.get("Enderman")));
	}
}
