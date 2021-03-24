package com.zodiackillerciphers.old.decrypto;

import java.util.*;
import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;

public class TreeList<K>
{
    static class Node<K>
    {
	K      val; // left is < value, right is >= value
	K      min; // minimum value under this node
	K      obj; // for leafs. value == value

	int    height;
	int    sz;  // number of objects beneath this node (==1 for leaf)

	Node<K> left, right;
	Node<K> parent;

	Node(K obj)
	{
	    this.obj = obj;
	    this.val = obj;
	    this.min = obj;
	    this.sz  = 1;
	}
    }

    Node<K> root;

    Comparator<K> cmp;

    public TreeList(Comparator<K> cmp)
    {
	this.root = null;
	this.cmp = cmp;

	if (false)
	    {
		JFrame frame = new JFrame("TreeList Widget");
		frame.setLayout(new BorderLayout());
		frame.add(getWidget(), BorderLayout.CENTER);
		frame.setSize(1000,400);
		frame.setVisible(true);
	    }
    }

    public void reset()
    {
	root = null;
	animateStep();
    }

    public K first()
    {
	return elementAt(0);
    }

    public K last()
    {
	return elementAt(size()-1);
    }

    static final int abs(int a)
    {
	return a > 0 ? a : -a;
    }

    static final int max(int a, int b)
    {
	return a > b ? a : b;
    }

    protected void rotateLL(Node<K> n)
    {
	Node<K> m = n.left;

	Node<K> T1 = m.left;
	Node<K> T2 = m.right;
	Node<K> T3 = n.right;
		
	assert(T1!=null && T2!=null && T3!=null);

	n.left = T1;
	n.right = m;

	m.left = T2;
	m.right = T3;

	T1.parent = n;
	T2.parent = m;
	T3.parent = m;
	m.parent = n;

	m.obj = null;
	m.val = T3.min;
	m.min = T2.min;
	n.obj = null;
	n.val = m.min;
	n.min = T1.min;
	m.sz = T1.sz + T2.sz;
	n.sz = m.sz + T3.sz;

	fixHeights(m);
	animateStep();
    }

    protected void rotateRR(Node<K> n)
    {
	Node<K> m = n.right;
		
	Node<K> T1 = n.left;
	Node<K> T2 = m.left;
	Node<K> T3 = m.right;
		
	assert(T1!=null && T2!=null && T3!=null);
		
	n.right = T3;
	n.left = m;
		
	m.left = T1;
	m.right = T2;
	m.parent = n;
		
	T1.parent = m;
	T2.parent = m;
	T3.parent = n;

	m.val = T2.min;
	m.obj = null;
	m.min = T1.min;
	n.val = T3.min;
	n.obj = null;
	n.min = m.min;
	m.sz  = T2.sz + T3.sz;
	n.sz  = T1.sz + m.sz;

	fixHeights(m);
	animateStep();
    }

    protected void balance(Node<K> n)
    {
	if (n==null)
	    return;

	// if leaf node, there's no work to be done. Balance our parent.
	if (n.obj != null)
	    {
		balance(n.parent);
		return;
	    }

	// if we're already balanced, we're done.
	if (abs(n.left.height - n.right.height) <= 1)
	    {
		balance(n.parent);
		return;
	    }

	// Rx rotation?
	if (n.right.height > n.left.height)
	    {
		if (n.right.left.height > n.right.right.height)
		    {
			rotateLL(n.right);
			rotateRR(n);
		    }
		else
		    rotateRR(n);
	    }

	// Lx rotation?
	if (n.left.height > n.right.height)
	    {
		if (n.left.left.height < n.left.right.height)
		    {
			rotateRR(n.left);
			rotateLL(n);
		    }
		else
		    rotateLL(n);
	    }

	balance(n.parent);
	return;

    }

    public boolean contains(K k)
    {
	Node<K> n = findNode(k);

	if (n==null)
	    return false;

	return (cmp.compare(k, n.obj)==0);
    }

    Node<K> findNode(K k)
    {
	Node<K> n = root;

	while (n != null && n.obj == null)
	    {
		int c = cmp.compare(k, n.val);

		if (c < 0)
		    n = n.left;
		else
		    n = n.right;
	    }

	return n;
    }

    Node<K> findNodeByIndex(int idx)
    {
	Node<K> n = root;

	while (n.obj==null) 
	    {
		if (idx >= n.left.sz)
		    {
			idx -= n.left.sz;
			n = n.right;
			continue;
		    }

		n = n.left;
		continue;
	    }

	return n;
    }

    public K elementAt(int idx)
    {
	return findNodeByIndex(idx).obj;
    }

    public int size()
    {
	if (root == null)
	    return 0;
	return root.sz;
    }

    public void remove(K k)
    {
	Node<K> n = findNode(k);
	removeNode(n);
    }

    public void remove(int idx)
    {
	Node<K> n = findNodeByIndex(idx);
	removeNode(n);
    }

    void removeNode(Node<K> n)
    {
	Node<K> p = n.parent;

	// removing the last item?
	if (p == null)
	    {
		root = null;
		return;
	    }

	// who is our sibling?
	Node<K> s = p.left == n ? p.right : p.left;

	// fetch the grandparent
	Node<K> gp = p.parent;

	if (gp.left == p)
	    gp.left = s;
	else
	    gp.right = s;

	s.parent = gp;

	fixHeights(s);
	animateStep();	

	balance(s);
    }

    /** Returns true if element was unique. **/
    public boolean add(K obj)
    {
	Node<K> n = root;

	if (n==null)
	    {
		root = new Node<K>(obj);
		return true;
	    }

	// follow the tree down until we hit a leaf
	while (n.obj == null)
	    {
		// it's a node. pick a branch.
		if (cmp.compare(obj, n.val) < 0)
		    n = n.left;
		else
		    n = n.right;
	    }

	// compute score
	int c = cmp.compare(obj, n.obj);

	if (c == 0)
	    return false;

	// we're at a leaf node. make it a non-leaf node with two leafs.
	if (c < 0)
	    {
		n.left = new Node<K>(obj);
		n.right = new Node<K>(n.obj);
		n.obj = null; // mark as non-leaf node.
		n.val = obj;
		n.min = obj;
	    }
	else
	    {
		n.left = new Node<K>(n.obj);
		n.right = new Node<K>(obj);
		n.obj = null; // mark as non-leaf node.
		n.val = n.val;
		n.min = n.val;
	    }
	
	n.height = 1;
	n.sz = 2;

	// set parent pointers
	n.left.parent = n;
	n.right.parent = n;

	fixHeights(n);
	balance(n);
	animateStep();
	
	return true;
    }

    void fixHeights(Node<K> n)
    {
	while (n != null) 
	    {
		if (n.obj == null)
		    {
			int newheight = max(n.left.height + 1, n.right.height + 1);
			
			n.height = newheight;
			n.sz = n.left.sz + n.right.sz;

			n.val = n.right.min;
			n.min = n.left.min;
		    }
		n = n.parent;
	    }
    }

    int animateDelay = 0;

    void animateStep()
    {
	for (Widget w : widgets)
	    w.repaint();

	if (widgets.size() > 0)
	    {
		try {
		    Thread.sleep(animateDelay);
		} catch (InterruptedException ex) {}
	    }
    }

    public JPanel getWidget()
    {
	return new Widget();
    }

    ArrayList<Widget> widgets = new ArrayList<Widget>();

    class Widget extends JPanel
    {
	public int rankHeight;
	public static final long serialVersionUID = 1001;

	public Widget()
	{
	    widgets.add(this);
	}

	public void paint(Graphics g)
	{
	    g.setColor(Color.white);
	    g.fillRect(0,0,getWidth(), getHeight());
	    g.setColor(Color.black);

	    if (root == null)
		return;

	    int width = getWidth();
	    rankHeight = getHeight()/(root.height+2);

	    paintRecurse((Graphics2D) g, root, width/2, 10, width/2, 0);
	}

	void paintRecurse(Graphics2D g, Node<K> node, int x, int y, int width, int depth)
	{
	    if (node.obj != null)
		{
		    paintString(g, node.obj.toString(), x, y);
		    return;
		}

	    paintString(g, node.val.toString(), x, y);

	    g.drawLine(x, y, x - width/2, y + rankHeight); 
	    g.drawLine(x, y, x + width/2, y + rankHeight);

	    paintRecurse(g, node.left, x - width/2, y + rankHeight, width/2, depth + 1);
	    paintRecurse(g, node.right, x + width/2, y + rankHeight, width/2, depth + 1);
	}

	void paintString(Graphics2D g, String s, int x, int y)
	{
	    FontMetrics fm = g.getFontMetrics();
	    Rectangle2D r = fm.getStringBounds(s, g);
	    g.drawString(s, (int) (x - r.getWidth()/2), (int) (y + r.getHeight()));
	}
    }

    public static class ScoreString implements Scorable
    {
	public double score;
	public String s;

	public ScoreString(String s, double score)
	{
	    this.s = s;
	    this.score = score;
	}

	public String toString()
	{
	    return s;
	}

	public double getScore()
	{
	    return score;
	}
    }

    public static class ScoreStringComparator implements Comparator<ScoreString>
    {
	public int compare(ScoreString a, ScoreString b)
	{
	    return Double.compare(Double.parseDouble(a.s), Double.parseDouble(b.s));
	    //	    return a.s.compareTo(b.s);
	}
    }

    public static void main(String args[])
    {
	TreeList<ScoreString> tl = new TreeList<ScoreString>(new ScoreStringComparator());

	JFrame frame = new JFrame("TreeList Widget");
	frame.setLayout(new BorderLayout());
	frame.add(tl.getWidget(), BorderLayout.CENTER);
	frame.setSize(1000,400);
	frame.setVisible(true);

	tl.animateDelay = 1000;
	
	for (int i = 0; i < 16; i++)
	    {
		tl.add(new ScoreString(""+i, i));
	    }

	/*	for (int i = 0; i < 10; i++)
	    {
		double v = (81+i)/10.0;
		tl.add(new ScoreString(""+v, v));
	    }
	*/
	tl.animateDelay = 2500;

	sleep(2500);

	tl.add(new ScoreString("14", 5));

	/*	tl.remove(3);
	tl.remove(0);
	tl.remove(0);
	tl.remove(0);
	tl.remove(0);
	tl.remove(0);
	tl.remove(0);
	tl.remove(0);
	tl.remove(0);
	tl.remove(0);
	tl.remove(0);
	tl.remove(0);
	tl.remove(0);
	tl.remove(0);
	tl.remove(0);*/
    }

    static void sleep(int ms)
    {
	try {
	    Thread.sleep(ms);
	} catch (Exception ex) {}
    }
}
