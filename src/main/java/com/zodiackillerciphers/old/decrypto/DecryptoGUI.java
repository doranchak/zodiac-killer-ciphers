package com.zodiackillerciphers.old.decrypto;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.io.*;
import java.util.*;
import java.util.zip.*;
import java.net.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.datatransfer.*;
import javax.swing.text.*;

public class DecryptoGUI
{
    SolutionSet set = new SolutionSet(500);

    JFrame frame;
    JTextArea cipherText = new JTextArea();
    JMenu  menu;
    JTextField  clues = new JTextField();
    MyTableModel tableModel = new MyTableModel();
    JTable solutions = new JTable(tableModel);
    JButton solveButton = new JButton("Solve");
    JButton clearButton = new JButton("Clear");
    JButton scrambleButton = new JButton("Encode");
    JProgressBar progressBar = new JProgressBar(0, MAXPROGRESS);
    JLabel statusLabel = new JLabel("Starting up");
    JLabel timerLabel = new JLabel("");
    JComboBox partialSolutionsBox = new JComboBox(new String[] {"Only exact solutions", 
								"Plausible solutions", 
								"All partial solutions" });

    //    JCheckBox noPruning = new JCheckBox("Handle lots of mispellings", false);

    JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));

    GetOpt opt = new GetOpt();
    Dictionary dict;
    DecryptoPuzzle puzzle;

    static final int MAXPROGRESS = 1000;

    FocusTracker focusTracker = new FocusTracker();

    Language lang = new EnglishLanguage();

    ExtensionFileFilter txtFilter = new ExtensionFileFilter("txt", "Text files");
    ExtensionFileFilter datFilter = new ExtensionFileFilter("dat", "Dictionary files");

    DecryptoMultiSolver dms;
    boolean solving = false;

    JRadioButtonMenuItem setSizeMenuItems[];

    String plainTextBeforeEncoding;

    public static void main(String args[])
    {
	DecryptoGUI d = new DecryptoGUI();
	d.run(args);
    }

    public DecryptoGUI()
    {
	opt.addString('\0',"dictionary","english-standard.dat", "Dictionary to use");
	opt.addString('c', "clues", "", "Clues, e.g.: A=C, M=R, JXJ=DAD");
	opt.addBoolean('h', "help", false, "Show help");

	//frame = new JFrame("Decrypto 8.1 build "+DecryptoVersion.revision+", eolson@mit.edu");
	frame = new JFrame("Decrypto 8.1 build , eolson@mit.edu");

	int spacing = 5;

	Font fnt = new Font("Monospaced", Font.PLAIN, cipherText.getFont().getSize());
	clues.setFont(fnt);
	solutions.setFont(fnt);
	cipherText.setFont(fnt);
	cipherText.setLineWrap(true);
	cipherText.setWrapStyleWord(true);

	JPanel panel = new JPanel(new BorderLayout(spacing, spacing));
	panel.add(makeLabel("Clues", 'u'), BorderLayout.WEST);
	panel.add(clues, BorderLayout.CENTER);
	JPanel buttonPanel = new JPanel(new FlowLayout());
	buttonPanel.add(clearButton);
	buttonPanel.add(scrambleButton);
	buttonPanel.add(solveButton);
	panel.add(buttonPanel, BorderLayout.EAST);
	JPanel pp = new JPanel(new BorderLayout());
	pp.add(partialSolutionsBox, BorderLayout.EAST);
	//	pp.add(noPruning, BorderLayout.WEST);
	panel.add(pp, BorderLayout.SOUTH);

	JPanel puzzlePanel = new JPanel(new BorderLayout(spacing, spacing));
	puzzlePanel.setBorder(new EmptyBorder(5,5,5,5));
	puzzlePanel.add(makeLabel("Cipher Text",'T'),BorderLayout.NORTH);
	puzzlePanel.add(new JScrollPane(cipherText), BorderLayout.CENTER);
	puzzlePanel.add(panel, BorderLayout.SOUTH);

	JPanel panel2 = new JPanel(new BorderLayout(spacing, spacing));
	panel2.setBorder(new EmptyBorder(5,5,5,5));
	panel2.add(new JLabel("Solutions:"), BorderLayout.WEST);
	panel2.add(progressBar, BorderLayout.CENTER);

	JPanel bottomPanel = new JPanel(new BorderLayout(spacing, spacing));
	bottomPanel.setBorder(new EmptyBorder(5,5,5,5));
	bottomPanel.add(panel2, BorderLayout.NORTH);
	bottomPanel.add(new JScrollPane(solutions), BorderLayout.CENTER);

	JSplitPane jsp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, puzzlePanel, bottomPanel);
	jsp.setResizeWeight(0.3);
	jsp.setDividerLocation(jsp.getResizeWeight());
	jsp.setContinuousLayout(true);

	JMenuBar menuBar = new JMenuBar();
	frame.setJMenuBar(menuBar);

	JMenu fileMenu = new JMenu("File");
	menuBar.add(fileMenu);
	fileMenu.add(makeMenuItem("New Puzzle", KeyEvent.VK_N, 
				  KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK),
				  new ActionListener() {
				      public void actionPerformed(ActionEvent e) {
					  newPuzzle();
				      }}));
	fileMenu.add(makeMenuItem("Open Puzzle...", KeyEvent.VK_O, 
				  KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK),
				  new ActionListener() {
				      public void actionPerformed(ActionEvent e) {
					  openPuzzle();
				      }}));
	fileMenu.add(makeMenuItem("Save Puzzle", KeyEvent.VK_S, 
				  KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK),
				  new ActionListener() {
				      public void actionPerformed(ActionEvent e) {
					  savePuzzle();
				      }}));

	fileMenu.add(makeMenuItem("Quit", KeyEvent.VK_Q, 
				  KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK),
				  new ActionListener() {
				      public void actionPerformed(ActionEvent e) {
					  System.exit(0);
				      }}));

	JMenu editMenu = new JMenu("Edit");
	editMenu.add(makeMenuItem("Cut", KeyEvent.VK_T,
				  KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK),
				  new DefaultEditorKit.CutAction()));

	
	editMenu.add(makeMenuItem("Copy", KeyEvent.VK_C,
				  KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK),
				  new ActionListener() {
				      public void actionPerformed(ActionEvent e) {
					  // this is absurd.
					  if (focusTracker.lastFocus == solutions)
					      solutions.getActionMap().get(TransferHandler.getCopyAction().getValue(Action.NAME)).actionPerformed(new ActionEvent(solutions, ActionEvent.ACTION_PERFORMED, null));
					  else
					      new DefaultEditorKit.CopyAction().actionPerformed(e);
				      }}));

	editMenu.add(makeMenuItem("Paste", KeyEvent.VK_P,
				  KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK),
				  new DefaultEditorKit.PasteAction()));

	menuBar.add(editMenu);

	JMenu dictionaryMenu = new JMenu("Dictionary");
	menuBar.add(dictionaryMenu);
	dictionaryMenu.add(makeMenuItem("Choose Dictionary...", 0, null, 
					new ActionListener() {
					    public void actionPerformed(ActionEvent e) {
						chooseDictionary();
					    }}));
	dictionaryMenu.add(makeMenuItem("Dictionary Editor...", 0, null, 
					new ActionListener() {
					    public void actionPerformed(ActionEvent e) {
						editDictionary();
					    }}));
	
	JMenu advancedMenu = new JMenu("Advanced");
	menuBar.add(advancedMenu);
	JMenu setSizeMenu = new JMenu("Maximum solutions");
	advancedMenu.add(setSizeMenu);
	int szs[] = new int[] { 100, 500, 1000, 2000, 10000, 100000 };
	setSizeMenuItems = new JRadioButtonMenuItem[szs.length];
	for (int i = 0; i < szs.length; i++)
	    {
		setSizeMenuItems[i] = new JRadioButtonMenuItem(""+szs[i]);
		setSizeMenuItems[i].addActionListener(new SetSizeActionListener(szs[i],setSizeMenuItems[i]));
		setSizeMenuItems[i].setSelected(i==1);
		setSizeMenu.add(setSizeMenuItems[i]);
	    }
	advancedMenu.add(makeMenuItem("Testing Options...", 0, null, new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    doTestOptions();
		}
	    }));

	JMenu helpMenu = new JMenu("Help");
	menuBar.add(helpMenu);
	helpMenu.add(makeMenuItem("Help", 0, KeyStroke.getKeyStroke(KeyEvent.VK_F1,0), new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    doHelp();
		}}));
	helpMenu.add(makeMenuItem("About Decrypto", 0, null, new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    doAbout();
		}}));

	fileMenu.setMnemonic(KeyEvent.VK_F);
	editMenu.setMnemonic(KeyEvent.VK_E);
	dictionaryMenu.setMnemonic(KeyEvent.VK_D);
	helpMenu.setMnemonic(KeyEvent.VK_H);
	scrambleButton.setMnemonic(KeyEvent.VK_M);
	solveButton.setMnemonic(KeyEvent.VK_S);
	partialSolutionsBox.setSelectedIndex(1);
	//	partialSolutionsBox.setMnemonic(KeyEvent.VK_P);
	cipherText.setFocusAccelerator('T');
	clues.setFocusAccelerator('u');

	solveButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    solve();
		}});

	scrambleButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    scramble();
		}});
	clearButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    newPuzzle();
		}});

	solutions.getColumnModel().getColumn(2).setCellRenderer(new MultiLineCellRenderer());
	solutions.addComponentListener(new TableComponentListener());
	
	TableColumnModel tcm = solutions.getColumnModel();
        tcm.getColumn(0).setMaxWidth(100);
        tcm.getColumn(1).setMaxWidth(100);
	//        tcm.getColumn(1).setMaxWidth(300);

	frame.setLayout(new BorderLayout(spacing, spacing));
	frame.add(jsp, BorderLayout.CENTER);
	
	JPanel labelPanel = new JPanel(new BorderLayout());
	labelPanel.add(statusLabel, BorderLayout.WEST);
	labelPanel.add(timerLabel, BorderLayout.EAST);
	frame.add(labelPanel, BorderLayout.SOUTH);

	new TimerThread().start();

	frame.addWindowListener(new WindowAdapter() {
		public void windowClosing(WindowEvent e) {
		    System.exit(0);
		}});

	solutions.addFocusListener(focusTracker);
	cipherText.addFocusListener(focusTracker);
	clues.addFocusListener(focusTracker);

	Image icon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/decrypto.png"));
	frame.setIconImage(icon);
	
	fileChooser.setFileFilter(txtFilter);
	fileChooser.setFileFilter(datFilter);

	cipherText.getDocument().addDocumentListener(new DocumentListener() {
		public void changedUpdate(DocumentEvent e)
		{
 		}
		public void insertUpdate(DocumentEvent e)
		{
		    plainTextBeforeEncoding = null;
		    updateEncodeButtonState();
 		}
		public void removeUpdate(DocumentEvent e)
		{
		    plainTextBeforeEncoding = null;
		    updateEncodeButtonState();
 		}
	    });
    }

    class SetSizeActionListener implements ActionListener
    {
	int sz;
	JRadioButtonMenuItem jmi;

	public SetSizeActionListener(int sz,JRadioButtonMenuItem jmi)
	{
	    this.sz = sz;
	    this.jmi = jmi;
	}

	public void actionPerformed(ActionEvent e)
	{
	    set = new SolutionSet(sz);

	    for (int i = 0; i < setSizeMenuItems.length; i++)
		{
		    setSizeMenuItems[i].setSelected(false);
		}
	    
	    jmi.setSelected(true);

	    tableUpdated(); //tableModel.fireTableDataChanged();
	}
    }

    static JLabel makeLabel(String text, char accel)
    {
	JLabel jl = new JLabel(text);
	jl.setDisplayedMnemonic(accel);
	return jl;
    }

    JMenuItem makeMenuItem(Action a, String s, int ke, KeyStroke ka)
    {
	JMenuItem jmi = new JMenuItem(a);
	jmi.setText(s);
	jmi.setMnemonic(ke);
	jmi.setAccelerator(ka);
	return jmi;
    }

    JFrame parameterFrame;

    void doTestOptions()
    {
	if (parameterFrame != null)
	    {
		parameterFrame.setVisible(true);
		return;
	    }

	ParameterGUI pg = new ParameterGUI();
	pg.addCheckBoxes("prunerootnode", "Prune candidates at root node", DecryptoParameters.pruneRootNode,
			 "pruneeachnode", "Prune candidates at each node", DecryptoParameters.pruneEachNode);
	pg.addCheckBoxes("scrambleallowidentity", "Allow same-letter (R=R) when encoding", DecryptoParameters.scrambleAllowIdentityMappings,
			 "singleletters", "Enable single letters", DecryptoParameters.enableSingleLetters);
	//	pg.addInt("singleletterthresh", "Single letter threshold", DecryptoParameters.singleLetterThreshold);
	pg.addChoice("verbosity", "Verbosity", new String[] {"Silent", "Verbose", "Verboser" }, DecryptoParameters.verbosity);
	pg.addChoice("planner", "Planner", new String[] {"Static", "Greedy", "Random", "In Order", "Next Best"}, 1);
	pg.addInt("maxreduceiter", "Maximum reduce iterations", DecryptoParameters.maxReduceIterations);
	pg.addInt("maxworddisable", "Maximum random word disable", DecryptoParameters.maxRandomWordDisable);

	pg.addChoice("nbmethod", "NextBest method", new String[] {"linear", "pow", "experimental"},1);

	pg.addListener(new MyParameterListener());

	parameterFrame = new JFrame("Decrypto Parameters");
	parameterFrame.setLayout(new BorderLayout());
	parameterFrame.add(pg.getPanel(), BorderLayout.CENTER);
	parameterFrame.pack();
	parameterFrame.setVisible(true);
    }

    class MyParameterListener implements ParameterListener
    {
	public void parameterChanged(ParameterGUI pg, String name)
	{
	    DecryptoParameters.pruneRootNode = pg.gb("prunerootnode");
	    DecryptoParameters.pruneEachNode = pg.gb("pruneeachnode");
	    DecryptoParameters.scrambleAllowIdentityMappings = pg.gb("scrambleallowidentity");
	    //	    DecryptoParameters.betterWordPrioritization = pg.gb("betterwordorder");
	    DecryptoParameters.enableSingleLetters = pg.gb("singleletters");
	    //	    DecryptoParameters.singleLetterThreshold = pg.gi("singleletterthresh");
	    DecryptoParameters.verbosity = pg.gi("verbosity");
	    DecryptoParameters.maxReduceIterations = pg.gi("maxreduceiter");
	    DecryptoParameters.maxRandomWordDisable = pg.gi("maxworddisable");

	    if (pg.gs("planner").equals("Static")) 
		DecryptoParameters.planner = new SimplePlanner();
	    if (pg.gs("planner").equals("Greedy")) 
		DecryptoParameters.planner = new GreedyPlanner();
	    if (pg.gs("planner").equals("Random"))
		DecryptoParameters.planner = new RandomPlanner();
	    if (pg.gs("planner").equals("In Order"))
		DecryptoParameters.planner = new InOrderPlanner();
	    if (pg.gs("planner").equals("Next Best"))
		DecryptoParameters.planner = new NextBestPlanner();
	    NextBestPlanner.METHOD = pg.gi("nbmethod")+1;
	}
    }

    void doAbout()
    {
	HelpFrame hf = new HelpFrame("About Decrypto 8.0", "about.html",300,250);
     }

    void doHelp()
    {
	HelpFrame hf = new HelpFrame("Decrypto 8.0 Help", "helpfile.html",640,480);
    }

    void editDictionary()
    {
	DecryptoDictGUI ddg = new DecryptoDictGUI();
    }

    void newPuzzle()
    {
	cipherText.setText("");
	clues.setText("");
	set.reset();
	tableUpdated(); //tableModel.fireTableDataChanged();
	cipherText.requestFocus();

	plainTextBeforeEncoding = null;
  	updateEncodeButtonState();
  }


    void openPuzzle(String path)
    {
	StringBuffer tx = new StringBuffer();
	StringBuffer cl = new StringBuffer();

	try {
	    BufferedReader ins = new BufferedReader(new FileReader(path));
	    String line;
	    while ((line = ins.readLine())!=null)
		{
		    if (line.contains("="))
			cl.append(line+" ");
		    else
			tx.append(line+"\n");
		}
	} catch (IOException ex) {
	    JOptionPane.showMessageDialog(null, "Couldn't load puzzle: "+ex, "Error", JOptionPane.ERROR_MESSAGE);
	}
	cipherText.setText(tx.toString().trim());
	clues.setText(cl.toString().trim());
	set.reset();
	tableUpdated(); //tableModel.fireTableDataChanged();

	plainTextBeforeEncoding = null;
	updateEncodeButtonState();
    }

    void openPuzzle()
    {
	fileChooser.setFileFilter(txtFilter);

	int res = fileChooser.showOpenDialog(frame);
	if (res != JFileChooser.APPROVE_OPTION)
	    return;

 	String path = fileChooser.getSelectedFile().getAbsolutePath();
	openPuzzle(path);
   }

    void savePuzzle()
    {
	fileChooser.setFileFilter(txtFilter);

	int res = fileChooser.showSaveDialog(frame);
	if (res != JFileChooser.APPROVE_OPTION)
	    return;

 	String path = fileChooser.getSelectedFile().getAbsolutePath();
	try {
	    BufferedWriter outs = new BufferedWriter(new FileWriter(path));
	    outs.write(cipherText.getText()+"\n");
	    outs.write(clues.getText()+"\n");
	    outs.close();
	} catch (IOException ex) {
	    JOptionPane.showMessageDialog(null, "Couldn't save puzzle: "+ex, "Error", JOptionPane.ERROR_MESSAGE);
	}
   }

    void chooseDictionary()
    {
	fileChooser.setFileFilter(datFilter);

	int res = fileChooser.showOpenDialog(frame);
	if (res != JFileChooser.APPROVE_OPTION)
	    return;

	String newdictpath = fileChooser.getSelectedFile().getAbsolutePath();

	try {
	    dict = new Dictionary(newdictpath);
	    statusLabel.setText("Dictionary \""+newdictpath+"\" loaded");
	} catch (IOException ex) {
	    JOptionPane.showMessageDialog(null, "Couldn't load dictionary: "+ex, "Error", JOptionPane.ERROR_MESSAGE);
	    statusLabel.setText("Couldn't load dictionary \""+newdictpath+"\": "+ex);
	    return;
	}
    }

    protected JMenuItem makeMenuItem(String name, int mnemonic, KeyStroke ks, ActionListener listener)
    {
	JMenuItem jmi = new JMenuItem(name, mnemonic);
	jmi.addActionListener(listener);
	jmi.setAccelerator(ks);
	return jmi;
    }

    public void run(String args[])
    {
	opt.parse(args);

	if (opt.getBoolean("help")) {
	    opt.doHelp();
	    return;
	}

	try {
	    dict = new Dictionary(opt.getString("dictionary"));
	    statusLabel.setText("Dictionary \""+opt.getString("dictionary")+"\" loaded");
	} catch (IOException ex) {
	    JOptionPane.showMessageDialog(null, "Couldn't open dictionary: "+ex, "Error", JOptionPane.ERROR_MESSAGE);
	    return;
	}

	ArrayList<String> extraArgs = opt.getExtraArgs();

	if (extraArgs.size()==1 && new File(extraArgs.get(0)).exists()) {
	    openPuzzle(extraArgs.get(0));
	} else {

	    StringBuffer sb = new StringBuffer();
	    for (int i = 0; i < extraArgs.size(); i++)
		sb.append(extraArgs.get(i)+" ");

	    cipherText.setText(sb.toString());
	    clues.setText(opt.getString("clues"));
	}

	puzzle = new DecryptoPuzzle(dict, cipherText.getText().trim());
	puzzle.handleClues(clues.getText());

	frame.setSize(600,400);
	frame.setVisible(true);
    }
    
    void updateEncodeButtonState()
    {
	if (plainTextBeforeEncoding == null)
	    scrambleButton.setText("Encode");
	else
	    scrambleButton.setText("Re-encode");
    }

    void scramble()
    {
	if (plainTextBeforeEncoding == null)
	    plainTextBeforeEncoding = cipherText.getText();

	String s = plainTextBeforeEncoding;

	updateEncodeButtonState();

	puzzle = new DecryptoPuzzle(dict, s);
	puzzle.handleClues(clues.getText());

	puzzle.scramble();

	// this code will trigger a DocumentListener event, causing us to 
	// clear plainTextBeforeEncoding. Consequently, we save and restore
	// the state explicitly.
	cipherText.setText(puzzle.cipherText);
	clues.setText(puzzle.clues);

	plainTextBeforeEncoding = s;
	updateEncodeButtonState();
    }

    // handle clicks on the "solve/stop" button
    synchronized void solve()
    {
	if (solving)
	    {
		dms.stop();
		setSolveMode(false);
		return;
	    }

	// start the solver
	setSolveMode(true);

	//	    DecryptoParameters.pruneEachNode = !noPruning.isEnabled();
	//	DecryptoParameters.pruneRootNode = !noPruning.isEnabled();

	set.reset();
	tableUpdated(); //tableModel.fireTableDataChanged();

	puzzle = new DecryptoPuzzle(dict, cipherText.getText().trim());
	puzzle.handleClues(clues.getText());
	dms = new DecryptoMultiSolver(puzzle);
	dms.setPartialSolutionsThreshold(getPartialSolutionsThreshold());
	dms.addListener(new MyDMSListener());

	//	IncrementalPlanner planner = new IncrementalPlanner(puzzle);
	//	Plan p = planner.plan();

	dms.start();
    }

    int getPartialSolutionsThreshold()
    {
	int idx = partialSolutionsBox.getSelectedIndex();
	switch (idx)
	    {
	    case 0:
		return Integer.MAX_VALUE;
	    case 1:
		DecryptoSolverOptimizations.removeDuplicateWords(puzzle);
		int n = 8*puzzle.words.size()/10;
		if (n > 14)
		    n = 14;
		return n;
	    case 2:
		return 0;
	    }
	assert(false);
	return 0;
    }

    void setSolveMode(boolean solving)
    {
	this.solving=solving;

	if (solving)
	    {
		solveButton.setText("Stop");
		cipherText.setEditable(false);
		clues.setEditable(false);
		cipherText.setEnabled(false);
		scrambleButton.setEnabled(false);
		partialSolutionsBox.setEnabled(false);
	    }
	else
	    {
		progressBar.setValue(MAXPROGRESS);
		solveButton.setText("Solve");
		cipherText.setEditable(true);
		cipherText.setEnabled(true);
		partialSolutionsBox.setEnabled(true);
		scrambleButton.setEnabled(true);
		clues.setEditable(true);
	    }
    }

    class MyDMSListener implements DecryptoMultiSolverListener
    {
	public boolean gotFullSolution = false;
	long lastUpdateTime = 0;

	public void setMessage(String s)
	{
	    statusLabel.setText(s);
	}

	public void finished(double elapsedTime, boolean timedout)
	{
	    progressBar.setValue(MAXPROGRESS);
	    setSolveMode(false);

	    String msg = (String.format("Finished (%d solutions)",set.size()));

	    if (set.size()==0 && getPartialSolutionsThreshold()>0)
		msg=msg+" Try enabling more solutions.";

	    statusLabel.setText(msg);
	    tableUpdated(); //tableModel.fireTableDataChanged();
	}

	public void decryptoSolution(MapSet mapset, double score)
	{
	    gotFullSolution = true;

	    add(mapset, score);
	}

	static final double partialSolutionPenalty = -10;

	public void decryptoPartialSolution(MapSet mapset, double score)
	{
	    add(mapset, score + partialSolutionPenalty);
	}

	void add(MapSet mapset, double score)
	{
	    if (false)
		{
		    System.out.println(score);
		    //		    Solution sol = new Solution(puzzle, new Map(mapset), score);
		    //		    System.out.println(String.format("%10.20f %s",score, sol.solution));

		}

	    if (set.wouldAdd(score))
		{
		    Solution sol = new Solution(puzzle, new Map(mapset), score);

		    set.add(sol);

		    long thisTime = System.currentTimeMillis();
		    if (thisTime > lastUpdateTime + 250)
			{
			    tableUpdated();

			    lastUpdateTime = thisTime;
			}
		}
	}

	public void decryptoProgress(double progress)
	{
	    progressBar.setValue((int) (progress*MAXPROGRESS));
	}
    }

    void tableUpdated()
    {
	int row = solutions.getSelectedRow();
	tableModel.fireTableDataChanged();
	if (row>=0 && solutions.getRowCount()>0)
	    solutions.setRowSelectionInterval(row, row);
    }

    class TimerThread extends Thread
    {
	TimerThread()
	{
	    setDaemon(true);
	}

	public void run()
	{
	    double lastworst = 0;

	    while (true)
		{
		    try {
			Thread.sleep(250);
		    } catch (InterruptedException ex) {}

		    DecryptoMultiSolver tdms = dms;

		    if (tdms != null)
			{
			    double elapsedTime = tdms.getElapsedTime();
			    timerLabel.setText(String.format("%.3f seconds", elapsedTime));
			}
		    
		    double worst = set.getWorst();
		    if (worst != lastworst)
			{
			    lastworst = worst;
			    tableUpdated(); //tableModel.fireTableDataChanged();
			}
		}
	}
    }

    class MyTableModel extends AbstractTableModel
    {
	public static final long serialVersionUID = 1001;

	public int getRowCount()
	{
	    return set.size();
	}

	public int getColumnCount()
	{
	    return 3;
	}

	public Object getValueAt(int row, int col)
	{
	    Solution s = set.getElement(row);
	    if (s==null)
		return "";

	    switch (col)
		{
		case 0:
		    return ""+(row+1);
		case 1:
		    return String.format("%.4f",s.score/puzzle.words.size());
		case 2:
		    return s.solution; //.replace('\n',' ');
		}
	    return "";
	}

	public String getColumnName(int col)
	{
	    switch (col)
		{
		case 0:
		    return "Rank";
		case 1:
		    return "Score";
		case 2:
		    return "Solution";
		}
	    return "?";
	}
    }

    public class TableComponentListener extends ComponentAdapter
    {
	public void componentResized(ComponentEvent e)
	{
	    if (solutions.getModel().getRowCount() == 0)
		return;

	    TableCellRenderer renderer = solutions.getCellRenderer(0, 2);
	    
	    solutions.setRowHeight(((MultiLineCellRenderer) renderer).getIdealHeight());
	}
    }

    class FocusTracker implements FocusListener
    {
	Component lastFocus = null;

	public void focusGained(FocusEvent e)
	{
	    lastFocus = e.getComponent();
	}

	public void focusLost(FocusEvent e)
	{
	}
    }

}
