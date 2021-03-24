package com.zodiackillerciphers.io;

import java.util.ArrayList;
import java.util.List;

public class DupesBean {
	public long fileSize;
	public List<String> files;
	public DupesBean() {
		files = new ArrayList<String>();
	}
	@Override
	public String toString() {
		return "DupesBean [fileSize=" + fileSize + ", files=" + files + "]";
	}
}
