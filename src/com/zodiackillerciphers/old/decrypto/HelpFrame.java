package com.zodiackillerciphers.old.decrypto;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.io.*;
import java.util.*;
import java.util.zip.*;
import java.util.jar.*;
import java.net.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.datatransfer.*;
import javax.swing.text.*;

public class HelpFrame extends JFrame
{
    public static final long serialVersionUID=1001;

    public HelpFrame(String title, String jarPath)
    {
	this(title, jarPath, 640,400);
    }

    public HelpFrame(String title, String jarPath, int width, int height)
    {
	super(title);

	JEditorPane jed = new JEditorPane("text/html", "This is a test");
	jed.setEditable(false);
	jed.setText(getData(jarPath));
	setLayout(new BorderLayout());
	JScrollPane jsp = new JScrollPane(jed);
	add(jsp, BorderLayout.CENTER);
	setSize(width, height);
	setVisible(true);
	//	jsp.getVerticalScrollBar().setValue(jsp.getVerticalScrollBar().getMinimum());
	jed.setCaretPosition(0);
    }

    String getData(String jarPath)
    {
      //The jar should be in the classpath.  Look for it there.
        String cp = System.getProperty("java.class.path");

        String[] items = cp.split(":");

        for (int i=0; i<items.length; i++)
            {
                if (items[i].endsWith(".jar") && (new File(items[i])).exists())
                    {
                        try {
				
			    JarFile jf = new JarFile(items[i]);

                            for (Enumeration<JarEntry> e = jf.entries() ; e.hasMoreElements() ;)
                                {
                                    JarEntry je = e.nextElement();

                                    // does this entry look like it might be our library?
                                    if (je.getName().endsWith(jarPath))
                                        {
					    StringBuffer sb = new StringBuffer();

					    BufferedReader ins = new BufferedReader(new InputStreamReader(jf.getInputStream(je)));

					    String line;
					    while ((line = ins.readLine())!=null)
						sb.append(line+"\n");

					    return sb.toString();
                                        }
                                }
                        }
                        catch(IOException ioe)
                            {
                                return "Error extracting "+items[i];
                            }
                    }
            }

	return "Help file "+jarPath+" not found";
    }
}
