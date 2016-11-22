package com.zodiackillerciphers.corpus;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.zodiackillerciphers.io.FileUtil;

public class Zip {
	public static final void copyInputStream(InputStream in, OutputStream out)
			throws IOException {
		byte[] buffer = new byte[1024];
		int len;
		while ((len = in.read(buffer)) >= 0)
			out.write(buffer, 0, len);
		in.close();
		out.close();
	}

	public static String loadFrom(File file, String dirTmp) {
		StringBuffer result = new StringBuffer();
		Enumeration entries;
		ZipFile zipFile;
		try {
			System.out.println("new zipfile for " + file.getAbsolutePath());
			zipFile = new ZipFile(file.getAbsolutePath());
			entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = (ZipEntry) entries.nextElement();
				System.out.println("- zip entry " + entry);
				if (entry.isDirectory()) {
					// Assume directories are stored parents first then
					// children.
					System.out
							.println("Found directory, doing nothing with it: "
									+ entry.getName());
					// This is not robust, just for demonstration
					// purposes.
					// (new File(entry.getName())).mkdir();
					continue;
				}
				if (!entry.getName().toLowerCase().endsWith(".txt")) continue; // we only want txt files
				System.out.println("Extracting file: " + entry.getName());
				String name = entry.getName();
				String[] split = name.split("/");
				if (split.length > 1) name = split[split.length-1]; // don't bother making directories
				File unzippedFile = new File(dirTmp + "/unzipped/"
						+ name);
				Zip.copyInputStream(zipFile.getInputStream(entry),
						new BufferedOutputStream(new FileOutputStream(
								unzippedFile)));
				result.append(FileUtil.loadSBFrom(unzippedFile));
				unzippedFile.delete();
			}
			zipFile.close();
		} catch (IOException ioe) {
			System.out.println("Unhandled exception:");
			ioe.printStackTrace();
			return null;
		}
		return result.toString();
	}

}
