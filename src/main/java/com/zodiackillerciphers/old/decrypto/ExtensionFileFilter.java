package com.zodiackillerciphers.old.decrypto;

import java.io.*;
import java.util.*;

public class ExtensionFileFilter extends javax.swing.filechooser.FileFilter
{
    public String description="Acceptable file types";
    HashSet<String> extensions=new HashSet<String>();

    boolean allowDirectories=true;
    boolean gotWildcard=false;

    public ExtensionFileFilter(String suffix, String description)
    {
	addExtension(suffix);
	setDescription(description);
    }

    public ExtensionFileFilter(String suffixes[], String description)
    {
	setDescription(description);
	for (int i = 0; i < suffixes.length; i++)
	    addExtension(suffixes[i]);
    }

    public String getDescription()
    {
	return description;
    }

    public void addExtension(String s)
    {
	if (s.equals("*"))
	    gotWildcard=true;

	extensions.add(s.toLowerCase());
    }

    public boolean accept(File f)
    {
	if (gotWildcard)
	    return true;

	if (f.isDirectory())
	    return allowDirectories;

	String path=f.getName();

	int period=path.lastIndexOf('.');
	String type=path.substring(period+1);
	if (extensions.contains(type.toLowerCase()))
	    return true;

	return false;
    }

    public void setAllowDirectories(boolean b)
    {
	allowDirectories=b;
    }

    public void setDescription(String s)
    {
	this.description=s;
    }
}
