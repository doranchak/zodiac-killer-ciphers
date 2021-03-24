package com.zodiackillerciphers.annealing;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.zodiackillerciphers.annealing.craigoperation.Factory;
import com.zodiackillerciphers.annealing.craigoperation.Operation;
import com.zodiackillerciphers.annealing.craigoperation.OperationState;
import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.io.FileUtil;
import com.zodiackillerciphers.tests.cribbing.Context;
import com.zodiackillerciphers.tests.cribbing.Result;
import com.zodiackillerciphers.tests.cribbing.Results;
import com.zodiackillerciphers.tests.cribbing.Search;
import com.zodiackillerciphers.tests.cribbing.State;
import com.zodiackillerciphers.tests.cribbing.TextBean;

import ec.util.MersenneTwisterFast;

/**
 * A "Craig-like" solution, where polyphones are allowed.
 */
public class CraigishZ340Solution extends Solution {
//	public static String FILE = "/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/letters/1969-07-31-cipher.txt";
	public static String FILE = "/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/letters/all-letters-combined-including-mikado-partial.txt";

	/** the cipher text */
	StringBuffer cipher;
	/** the plain text */
	StringBuffer plain;
	/** result of fitting plain text into cipher */
	String plaintextResult;

	/** source tokens */
	List<String> tokensOriginal;
	/** current tokens, computed after applying operations */
	List<String> tokensCurrent;
	/** current start position within original tokens */
	int startingToken;
	/** starting location in the cipher */
	int cipherStartPosition;

	/** operations */
	List<Operation> operations;

	OperationState state;

	/** cache of tokens */
	public static List<String> tokensOriginalCache;

	/** cache of energy */
	public double energyCached; 
	
	public static MersenneTwisterFast rand;

	public boolean mutate() {

		// int num = rand.nextInt(3) + 1;
		// for (int k = 0; k < num; k++) {
		int which = rand.nextInt(6);
		if (which == 0) {
			/* insert random operation */
			operations.add(Factory.random(rand, state));
		} else if (which == 1) {
			/* remove a random operation */
			if (operations.size() > 0) {
				operations.remove(rand.nextInt(operations.size()));
			}
		} else if (which == 2) {
			/* swap two operations at random */
			if (operations.size() > 1) {
				int a = 0;
				int b = 0;
				while (a == b) {
					a = rand.nextInt(operations.size());
					b = rand.nextInt(operations.size());
				}
				Operation op = operations.get(a);
				operations.set(a, operations.get(b));
				operations.set(b, op);
			}
		} else if (which == 3) {
			/* shuffle operations */
			if (operations.size() > 1) {
				for (int i = operations.size() - 1; i >= 1; i--) {
					int j = rand.nextInt(i + 1);
					if (i == j)
						continue;
					Operation tmp = operations.get(i);
					operations.set(i, operations.get(j));
					operations.set(j, tmp);
				}
			}
		} else if (which == 4) {
			/* randomize parameters for a randomly selected operation */
			if (operations.size() > 1) {
				operations.get(rand.nextInt(operations.size())).generate(state);
			}

		} else if (which == 5) {
			/* random start position in the cipher */
			cipherStartPosition = rand.nextInt(140);
		} else if (which == 6) {
			/* random start positions in the tokens */
			startingToken = rand.nextInt(tokensOriginal.size());
		} else
			throw new RuntimeException("Unexpected selection: " + which);
		// }
		return true;
	}

	public double energy() {
		/**
		 * goals:
		 * 
		 * - maximize plaintext coverage - minimize polyphones
		 * 
		 * - maximize "interesting" assignments
		 * 
		 */
		double energy = 0;

		try {
			process();
		} catch (Exception e) {
			e.printStackTrace();
			return Double.MAX_VALUE;
		}
		int count = 0;
		for (int i = 0; i < plaintextResult.length(); i++) {
			char ch = plaintextResult.charAt(i);
			if (ch == '_' || ch == ' ')
				continue;
			count++;
		}
		float undecoded = cipher.length() - count;
		undecoded = undecoded / cipher.length();

		energy = undecoded;
		if (operations.size() > 10)
			energy = Double.MAX_VALUE;
		energyCached = energy;

//		 System.out.println("energy: " + energy);
		return energy;
	}

	/**
	 * process solution, by doing these steps: - perform operations on the
	 * source tokens to produce a new list of tokens. - stream resulting tokens
	 * as a new plaintext and fit it until limits are violated.
	 * 
	 */
	public void process() {
		/* always reset current tokens */
		tokensCurrent = new ArrayList<String>();
		/* start at the indicated position */
		for (int i=startingToken; i<tokensOriginal.size(); i++) {
			tokensCurrent.add(tokensOriginal.get(i));
		}
		// System.out.println("tc size " + tokensCurrent.size());
		/* init operation state */
		state = new OperationState(tokensOriginal, tokensCurrent, cipher,
				plain, cipherStartPosition, startingToken, rand);

		/* perform operations copies of original tokens */
		performOperations();
	}

	/**
	 * perform operations. generate plaintext as we go, but quit when any limit
	 * is violated.
	 */
	public void performOperations() {
		/** perform manipulations of the tokens */
		OperationState state = new OperationState(tokensOriginal,
				tokensCurrent, cipher, plain, cipherStartPosition, startingToken, rand);
		for (Operation op : operations) {
			op.execute(state);
		}
		// System.out.println("got here " + operations.size());
		/** tokens are now configured. now we can generate the plaintext */
		plain = new StringBuffer();
		// set the starting position by using padding
		for (int i=0; i<cipherStartPosition; i++) {
			plain.append("_");
		}
		for (String token : tokensCurrent) {
			plain.append(token);
			if (plain.length() == cipher.length())
				break;
			if (plain.length() > cipher.length()) {
				plain = new StringBuffer(
						plain.substring(0, cipher.length() - 1));
				break;
			}
		}
		// see how much the plaintext fits into the ciphertext
		plaintextResult = plaintextTest(plain.toString());

//		 System.out.println("======= ");
//		 System.out.println("cipherStartPosition: " + cipherStartPosition);
//		 System.out.println("startingToken: " + startingToken);
//		 System.out.println("plain before: " + plain);
//		 System.out.println("plain after: " + plaintextResult);
//		 System.out.println("tokensO: " + tokensOriginal);
//		 System.out.println("tokensC: " + tokensCurrent);
//		 System.out.println("operations: " + operations);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.zodiackillerciphers.annealing.Solution#initialize()
	 */
	@Override
	public void initialize() {
		initialize(FILE);
	}

	/** initialize with tokens read from the given file */
	public void initialize(String path) {
		plain = new StringBuffer();
		tokensOriginal = new ArrayList<String>();
		tokensCurrent = new ArrayList<String>();
		startingToken = 0;
		cipherStartPosition = 0;
		operations = new ArrayList<Operation>();
		rand = new MersenneTwisterFast();

		if (tokensOriginalCache == null) {
			StringBuffer sb = FileUtil.loadSBFrom(new File(path));
			String[] tokens = FileUtil.tokenize(sb.toString().toUpperCase());
			List<String> list = new ArrayList<String>();
			for (String token : tokens) {
				if (token.isEmpty())
					continue;
				list.add(token);
			}
			addTokens(list);
			tokensOriginalCache = list;
		} else
			addTokens(tokensOriginalCache);

		cipherStartPosition = rand.nextInt(140);
		startingToken = rand.nextInt(tokensOriginal.size());
		state = new OperationState(tokensOriginal, tokensCurrent, cipher,
				plain, cipherStartPosition, startingToken, rand);
		

	}

	public void addTokens(List<String> tokens) {
		tokensOriginal = tokens;
		// System.out.println("to size " + tokensOriginal.size());

	}

	public Solution clone() {
		CraigishZ340Solution sol = new CraigishZ340Solution();
		sol.cipher = this.cipher;
		sol.initialize();
		sol.plain = new StringBuffer(plain);
		// for (String token : tokensCurrent)
		// sol.tokensCurrent.add(token);
		for (Operation op : operations) {
			sol.operations.add(Factory.clone(op));
		}
		sol.startingToken = startingToken;
		sol.cipherStartPosition = cipherStartPosition; 
		// System.out.println("clone to size " + tokensOriginal.size() + " " +
		// sol.tokensOriginal.size());
		// System.out.println("clone tc size " + tokensCurrent.size() + " " +
		// sol.tokensCurrent.size());

		sol.energyCached = energyCached;
		return sol;
	}

	/**
	 * @return the cipher
	 */
	public StringBuffer getCipher() {
		return cipher;
	}

	/**
	 * @param cipher
	 *            the cipher to set
	 */
	public void setCipher(StringBuffer cipher) {
		this.cipher = cipher;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.zodiackillerciphers.annealing.Solution#representation()
	 */
	@Override
	public String representation() {
		String rep = energy() + "	" + plaintextResult + "	[" + tokensCurrent
				+ "]	" + operations;
		return rep;
	}

	public static void testTokens() {
		CraigishZ340Solution sol = new CraigishZ340Solution();
		sol.initialize();
		sol.initialize("/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/letters/1969-07-31-cipher.txt");
		System.out.println(sol.tokensOriginal);
	}

	public static void testOperations() {

		boolean show = true;
		for (int i = 0; i < 100; i++) {
			CraigishZ340Solution sol = new CraigishZ340Solution();
			sol.initialize();
			sol.initialize("/Users/doranchak/projects/zodiac/zodiac-killer-ciphers/letters/1969-07-31-cipher.txt");
			sol.cipher = new StringBuffer(Ciphers.cipher[0].cipher);
			if (show) {
				System.out.println("Original tokens: " + sol.tokensOriginal);
				show = false;
			}
			Operation op = Factory.random(sol.rand, sol.state);
			sol.operations.add(op);
			sol.energy();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.zodiackillerciphers.annealing.Solution#energyCached()
	 */
	@Override
	public double energyCached() {
		// TODO Auto-generated method stub
		return 0;
	}

	public static String plaintextTest(String plaintext) {
		Context context = new Context();
		State state = new State();
		state.setCipher(Ciphers.cipher[0].cipher);
		context.setMaxAssignments(3);
		context.setMaximizePolyphones(false);
		TextBean tb = new TextBean();
		tb.setIgnoreConversion(true);
		tb.setOriginal(new StringBuilder(plaintext));
		tb.setConverted(new StringBuilder(plaintext));
		context.setTextBean(tb);
		context.setState(state);
		boolean go = true;
		Context.MAX_IOC = 1.0;
		Results.SHOW_NEW_BEST = false;
		while (go) {
			go = Search.loop(context);
		}
		Result res = context.state.getResults().getTreeSet().iterator().next();
		return res.getPlaintext();

	}

	public static void testCribContext() {
		Context context = new Context();
		State state = new State();
		state.setCipher(Ciphers.cipher[0].cipher);
		context.setMaxAssignments(3);
		context.setMaximizePolyphones(true);
		context.setTextBean(new TextBean(
				new StringBuilder(
						"_LIKEKILLINGPEOPLEBECAUSEITISSOMUCHFUNITISMOREFUNTHANKILLINGWILDGAMEINTHEFORRESTBECAUSEMANISTHEMOATDANGERTUEANAMALOFALLTOKILLSOMETHINGGIVESMETHEMOATTHRILLINGEXPERENCEITISEVENBETTERTHANGETTINGYOURROCKSOFFWITHAGIRLTHEBESTPARTOFITIATHAEWHENIDIEIWILLBEREBORNINPARADICESNDALLTHEIHAVEKILLEDWILLBECOMEMYSLAVESIWILLNOTGIVEYOUMYNAMEBECAUSEYOUWILLTRYTOSLOIDOWNORSTOPMYCOLLECTINGOFSLAVESFORMYAFTERLIFEEBEORIETEMETHHPITI")));
		context.setState(state);
		boolean go = true;
		Context.MAX_IOC = 1.0;
		Results.SHOW_NEW_BEST = false;
		while (go) {
			go = Search.loop(context);
		}
		context.state.getResults().dump();
	}

	public static void main(String[] args) {
		// testTokens();
		// testOperations();
		testCribContext();
	}

	@Override
	public void mutateReverse() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mutateReverseClear() {
		// TODO Auto-generated method stub
		
	}

}
