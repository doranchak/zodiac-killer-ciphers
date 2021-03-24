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

public class DecryptoDictGUI
{
    public static final long serialVersionUID=1001;
    JMenu  menu;
							
    JFrame frame = new JFrame("Decrypto Dictionary Editor");

    ArrayList<Word> words = new ArrayList<Word>();
    WordTableModel tableModel = new WordTableModel();
    JTable wordTable = new JTable(tableModel);
    JLabel statusLabel = new JLabel("No words loaded");

    JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
    ExtensionFileFilter txtFilter = new ExtensionFileFilter("txt", "Text files");
    ExtensionFileFilter datFilter = new ExtensionFileFilter("dat", "Dictionary files");
    ExtensionFileFilter txtDatFilter = new ExtensionFileFilter(new String[] {"txt", "dat"}, "Text files and dictionary files");

    ParameterGUI pg = new ParameterGUI();
    Language lang;
    
    JTextField findField = new JTextField(15);

    public static void main(String args[])
    {
	DecryptoDictGUI d = new DecryptoDictGUI();
    }

    public DecryptoDictGUI()
    {
	pg.addString("langclass","Language Class","decrypto.EnglishLanguage");

	Font fnt = new Font("Monospaced", Font.PLAIN, statusLabel.getFont().getSize());
	wordTable.setFont(fnt);
	wordTable.addKeyListener(new MyTableKeyListener());

	JMenuBar menuBar = new JMenuBar();
	frame.setJMenuBar(menuBar);
 	JMenu fileMenu = new JMenu("File");
	menuBar.add(fileMenu);

	fileMenu.add(makeMenuItem("New", KeyEvent.VK_N, 
				  null,
				  new ActionListener() {
				      public void actionPerformed(ActionEvent e) {
					  clear();
				      }}));


	fileMenu.add(makeMenuItem("Import words...", KeyEvent.VK_I, 
				  null,
				  new ActionListener() {
				      public void actionPerformed(ActionEvent e) {
					  importWords();
				      }}));
	/*
	fileMenu.add(makeMenuItem("Add words from dictionary...", KeyEvent.VK_D, 
				  null,
				  new ActionListener() {
				      public void actionPerformed(ActionEvent e) {
					  loadFromDictionary();
				      }}));

	fileMenu.add(makeMenuItem("Add words from word list...", KeyEvent.VK_W, 
				  null,
				  new ActionListener() {
				      public void actionPerformed(ActionEvent e) {
					  loadFromWordList();
				      }}));
	*/

	fileMenu.add(makeMenuItem("Save dictionary...", KeyEvent.VK_S, 
				  null,
				  new ActionListener() {
				      public void actionPerformed(ActionEvent e) {
					  saveDictionary();
				      }}));
	fileMenu.add(makeMenuItem("Save word list...", 0,
				  null,
				  new ActionListener() {
				      public void actionPerformed(ActionEvent e) {
					  saveWordList();
				      }}));


	fileMenu.addSeparator();
	fileMenu.add(makeMenuItem("Quit", KeyEvent.VK_Q, 
				  KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK),
				  new ActionListener() {
				      public void actionPerformed(ActionEvent e) {
					  frame.dispose();
				      }}));

	JMenu sortMenu = new JMenu("Sort");
	menuBar.add(sortMenu);
	sortMenu.add(makeMenuItem("Alphabetical Order", KeyEvent.VK_A,
				  null,
				  new ActionListener() {
				      public void actionPerformed(ActionEvent e) {
					  Collections.sort(words, new Word.TextComparator());
					  update();
				      }}));
	sortMenu.add(makeMenuItem("Dictionary Order", KeyEvent.VK_D, 
				  null,
				  new ActionListener() {
				      public void actionPerformed(ActionEvent e) {
					  Collections.sort(words, new Word.PatternComparator());
					  update();
				      }}));

	JMenu toolsMenu = new JMenu("Tools");
	menuBar.add(toolsMenu);
	toolsMenu.add(makeMenuItem("Add words manually...", KeyEvent.VK_A,
				  null,
				  new ActionListener() {
				      public void actionPerformed(ActionEvent e) {
					  addWordsManually();
				      }}));

	frame.addWindowListener(new WindowAdapter() {
		public void windowClosing(WindowEvent e) {
		    frame.dispose();
		}});

	frame.setLayout(new BorderLayout());
	frame.add(new JScrollPane(wordTable), BorderLayout.CENTER);

	JPanel panel = new JPanel(new BorderLayout());
	panel.add(statusLabel, BorderLayout.WEST);
	JPanel panel2 = new JPanel(new FlowLayout());
	panel2.add(DecryptoGUI.makeLabel("Find", 'f'));
	panel2.add(findField);
	findField.setFocusAccelerator('f');
	findField.addActionListener( new ActionListener() {
		public void actionPerformed(ActionEvent e)
		{
		    doFind();
		}
	    });

	panel.add(panel2, BorderLayout.EAST);

	frame.add(panel, BorderLayout.SOUTH);
	frame.add(pg.getPanel(), BorderLayout.NORTH);

	TableColumnModel tcm = wordTable.getColumnModel();
        tcm.getColumn(0).setMaxWidth(100);

	Image icon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/decrypto.png"));
	frame.setIconImage(icon);

	fileChooser.setFileFilter(txtFilter);
	fileChooser.setFileFilter(datFilter);
	fileChooser.setFileFilter(txtDatFilter);

	frame.setSize(400,600);
	frame.setVisible(true);
	update();
    }

    class WordEditor
    {
	JFrame wframe;
	JTextArea textArea = new JTextArea();
	JButton addButton = new JButton("Add");
	JButton cancelButton = new JButton("Cancel");

	WordEditor()
	{
	    wframe = new JFrame("Add words");
	    wframe.setLayout(new BorderLayout());
	    JPanel p = new JPanel(new GridLayout(1,2));
	    p.add(cancelButton);
	    p.add(addButton);
	    wframe.add(textArea, BorderLayout.CENTER);
	    wframe.add(p, BorderLayout.SOUTH);
	    wframe.setSize(400,400);
	    wframe.setVisible(true);

	    wframe.addWindowListener(new WindowAdapter() {
		    public void windowClosing(WindowEvent e) {
			wframe.dispose();
		    }});

	    cancelButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			wframe.dispose();
		    }});

	    addButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			if (lang == null)
			    lang = new EnglishLanguage();

			String t[] = textArea.getText().split("[\n\r]");
			for (int i = 0; i < t.length; i++)
			    {
				if (t[i].charAt(0)=='#')
				    continue;

				byte[] bs = lang.stringToBytes(t[i]);
				if (bs == null)
				    continue;
				
				words.add(new Word(lang.bytesToString(bs), 0, bs));
			    }
			Dictionary.sortAndPruneWords(words);
			update();
			wframe.dispose();
		    }});
	}
    }

    void addWordsManually()
    {
	new WordEditor();
    }

    void doFind()
    {
	// fix up the search string; remove punctuation, etc.
	String s = findField.getText().toUpperCase();
	s = lang.bytesToString(lang.stringToBytes(s));

	//	System.out.println("find "+s);

	int start = wordTable.getSelectedRow();
	if (start < 0 || start >= words.size())
	    start = 0;

	for (int i = start+1; i != start; i=(i+1)%words.size())
	    {
		if (words.get(i).text.startsWith(s))
		    {
			wordTable.setRowSelectionInterval(i, i);
			Rectangle r = wordTable.getCellRect(i, 1, true);
			/*
			int pad = wordTable.getHeight() - r.height - 10;
			r.y-=pad/2;
			r.height+=pad;
			*/
			wordTable.scrollRectToVisible(r);
			//			System.out.println("found at "+i);
			return;
		    }
	    }
    }

    void clear()
    {
	words = new ArrayList<Word>();
	update();
    }

    void importWords()
    {
	fileChooser.setFileFilter(txtDatFilter);

	int res = fileChooser.showOpenDialog(frame);

	if (res != JFileChooser.APPROVE_OPTION)
	    return;

	String path = fileChooser.getSelectedFile().getAbsolutePath();
	if (path.toLowerCase().endsWith(".dat"))
	    loadFromDictionary(path);
	else
	    loadFromWordList(path);
    }

    void loadFromDictionary()
    {
	fileChooser.setFileFilter(datFilter);

	int res = fileChooser.showOpenDialog(frame);

	if (res != JFileChooser.APPROVE_OPTION)
	    return;

	String path = fileChooser.getSelectedFile().getAbsolutePath();
	loadFromDictionary(path);
    }

    void loadFromDictionary(String path)
    {
	try {
	    Dictionary d = new Dictionary(path);
	    lang = d.getLanguage();
	    
	    ArrayList<byte[]> pats = d.readAll();
	    for (int i = 0; i < pats.size(); i++)
		{
		    words.add(new Word(lang.bytesToString(pats.get(i)), 0, pats.get(i)));
		}
	} catch (IOException ex) {
	    JOptionPane.showMessageDialog(null, "Couldn't read dictionary: "+ex, "Error", JOptionPane.ERROR_MESSAGE);
	}

	Dictionary.sortAndPruneWords(words);

	update();
    }

    void loadFromWordList()
    {
	fileChooser.setFileFilter(txtFilter);

	int res = fileChooser.showOpenDialog(frame);

	if (res != JFileChooser.APPROVE_OPTION)
	    return;

	String path = fileChooser.getSelectedFile().getAbsolutePath();
	loadFromWordList(path);
    }

    void loadFromWordList(String path)
    {
	try {
	    FileReader fr = new FileReader(path);
	    BufferedReader ins = new BufferedReader(fr);
	    
	    lang = Dictionary.getLanguage(pg.gs("langclass"));
	    String line;
	    while ((line = ins.readLine())!=null) 
		{
		    line = line.trim();
		    if (line.length()==0)
			continue;

		    // skip comments
		    if (line.charAt(0)=='#')
			continue;

		    byte[] bs = lang.stringToBytes(line);
		    if (bs == null)
			continue;
		    
		    words.add(new Word(lang.bytesToString(bs), 0, bs));
		}
	} catch (IOException ex) {
	    JOptionPane.showMessageDialog(null, "Couldn't read word list: "+ex, "Error", JOptionPane.ERROR_MESSAGE);
	}

	Dictionary.sortAndPruneWords(words);
	update();
    }

    void saveWordList()
    {
	fileChooser.setFileFilter(txtFilter);

	int res = fileChooser.showSaveDialog(frame);

	if (res != JFileChooser.APPROVE_OPTION)
	    return;

	String path = fileChooser.getSelectedFile().getAbsolutePath();

	try {
	    BufferedWriter outs = new BufferedWriter(new FileWriter(path));
	    for (int i = 0; i < words.size(); i++)
		{
		    outs.write(words.get(i).text);
		    outs.newLine();
		}
	    outs.close();
	} catch (IOException ex) {
	    JOptionPane.showMessageDialog(null, "Couldn't write word list: "+ex, "Error", JOptionPane.ERROR_MESSAGE);
	}
    }
    

    void saveDictionary()
    {
	fileChooser.setFileFilter(datFilter);

	int res = fileChooser.showSaveDialog(frame);

	if (res != JFileChooser.APPROVE_OPTION)
	    return;

	String path = fileChooser.getSelectedFile().getAbsolutePath();

	Dictionary dict = new Dictionary();
	dict.properties.put("langclass", pg.gs("langclass"));
	dict.properties.put("version", "2");
	try {
	    dict.write(path, words);
	} catch (IOException ex) {
	    JOptionPane.showMessageDialog(null, "Couldn't write dictionary: "+ex, "Error", JOptionPane.ERROR_MESSAGE);
	}
    }
    
    void update()
    {
	statusLabel.setText("Size: "+words.size()+" words");
	tableModel.fireTableDataChanged();
    }

    protected JMenuItem makeMenuItem(String name, int mnemonic, KeyStroke ks, ActionListener listener)
    {
	JMenuItem jmi = new JMenuItem(name, mnemonic);
	jmi.addActionListener(listener);
	jmi.setAccelerator(ks);
	return jmi;
    }

    class WordTableModel extends AbstractTableModel
    {
	public static final long serialVersionUID = 1001;

	public int getRowCount()
	{
	    return words.size();
	}

	public int getColumnCount()
	{
	    return 2;
	}

	public Object getValueAt(int row, int col)
	{
	    Word w = words.get(row);
	    switch (col)
		{
		case 0:
		    return ""+(row+1);
		case 1:
		    return w.text;
		}
	    return "";
	}

	public String getColumnName(int col)
	{
	    switch (col)
		{
		case 0:
		    return "Row";
		case 1:
		    return "Word";
		}
	    return "?";
	}
    }

    class MyTableKeyListener extends KeyAdapter
    {
	public void keyPressed(KeyEvent e)
	{
	    if (e.getKeyCode() == KeyEvent.VK_DELETE ||
		e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
		{
		    int[] idxs = wordTable.getSelectedRows();

		    for (int i = 0; i < idxs.length; i++)
			words.set(idxs[i], null);

		    for (int i = 0; i < words.size(); i++)
			{
			    while (i < words.size() && words.get(i) == null)
				words.remove(i);
			}
		    update();
		}
	}
    }
}


