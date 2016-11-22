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

public class MultiLineCellRenderer extends JTextArea implements TableCellRenderer {
    public static final long serialVersionUID = 1001;

    public MultiLineCellRenderer() {
	setLineWrap(true);
	setWrapStyleWord(true);
	setOpaque(true);
    }
 
    public Component getTableCellRendererComponent(JTable table, Object value,
						   boolean isSelected, boolean hasFocus, int row, int column) {
	if (isSelected) {
	    setForeground(table.getSelectionForeground());
	    setBackground(table.getSelectionBackground());
	} else {
	    setForeground(table.getForeground());
	    setBackground(table.getBackground());
	}
	setFont(table.getFont());
	if (hasFocus) {
	    setBorder( UIManager.getBorder("Table.focusCellHighlightBorder") );
	    if (table.isCellEditable(row, column)) {
		setForeground( UIManager.getColor("Table.focusCellForeground") );
		setBackground( UIManager.getColor("Table.focusCellBackground") );
	    }
	} else {
	    setBorder(new EmptyBorder(1, 2, 1, 2));
	}
	setText((value == null) ? "" : value.toString());

	return this;
    }

    public int getIdealHeight()
    {
	return getMinimumSize().height;
    }
}
