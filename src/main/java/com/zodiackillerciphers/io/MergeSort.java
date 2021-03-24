package com.zodiackillerciphers.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.PriorityQueue;

public class MergeSort {
	/** split file into sorted chunks of the given size in bytes (approximately). */
	public static void split(String inputFile, String outputFilePrefix, String outputFileSuffix, long bytes) {
		try {
			BufferedReader input = new BufferedReader(new FileReader(new File(inputFile)), 3000000);
			PriorityQueue<String> queue = new PriorityQueue<String>(5000);
			int counter = 0;
 
 
			long bytesCurrent = 0;
			String line = null;
			while ((line = input.readLine()) != null) {
				queue.add(line);
				bytesCurrent += line.length();
				if (bytesCurrent > bytes) {
					
					String currentFile = outputFilePrefix + counter + outputFileSuffix;
					File fout = new File(currentFile);
					FileOutputStream fos = new FileOutputStream(fout);
					BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
					String head;
					while ((head = queue.poll()) != null) {
						bw.write(head);
						bw.newLine();
					}
					bw.close();
					
					queue.clear();
					bytesCurrent = 0;
					counter++;
				}
			}
			input.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	/** perform a merge sort on the given list of files whose contents are already sorted. */
	public static void sort(String[] files, String outputFile) {
		int N = files.length;
		BufferedReader[] input = new BufferedReader[N];
		long counter = 0;
		long bytesAll = 0;
		long bytesCurrent = 0;
		Date bytesStartAll = new Date();
		Date bytesStartCurrent = new Date();
		PriorityQueue<MergeSortBean> queue = new PriorityQueue<MergeSortBean>(20, new Comparator<MergeSortBean>() {

			@Override
			public int compare(MergeSortBean o1, MergeSortBean o2) {
				String s1 = "";
				String s2 = "";
				if (o1 != null && o1.line != null) s1 = o1.line;
				if (o2 != null && o2.line != null) s2 = o2.line;
				return s1.compareTo(s2);
			}
			
		}); 
		try {
			// set up the file we are writing to
			File fout = new File(outputFile);
			FileOutputStream fos = new FileOutputStream(fout);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos), 3000000);
			
			// init the K-way merge by reading one line from each of the K files
			for (int i=0; i<input.length; i++) {
				input[i] = new BufferedReader(new FileReader(new File(files[i])), 3000000);
				MergeSortBean bean = new MergeSortBean(input[i].readLine(), input[i], i);
//				System.out.println("read line [" + bean.line + "] from #[" + i + "]");
				queue.add(bean);
				bytesAll += bean.line.length();
			}
//			bytesStartThis = new Date();
			
			while (true) {
				// get the head of the queue and remove it from queue
				MergeSortBean bean = queue.poll();
				if (bean == null) break; // no more entries
				// write line to output file
				bw.write(bean.line);
				bw.newLine();
				counter++;
				if (counter % 1000000 == 0) {
					long diffAll = new Date().getTime() - bytesStartAll.getTime();
					float bps = bytesAll;
					bps /= diffAll * 1000;
					System.out.println("Wrote " + counter + " lines");
					System.out.println("Read " + bytesAll + " bytes in " + (new Date().getTime() - bytesStartAll.getTime()) + " ms, rate " + bps + " bytes per second");
				}
				//System.out.println("writing [" + bean.line + "] from file #[" + bean.index + "]");
				
				// get another line from the file the head came from.
				String line = bean.reader.readLine();
				if (line == null) {
					; // no more lines to read from this file.
				} else {
					// add the new line to the queue
					MergeSortBean newBean = new MergeSortBean(line, bean.reader, bean.index);
					queue.add(newBean);
					bytesAll += line.length();
				}
			}
			for (int i=0; i<input.length; i++) {
				input[i].close();
			}
			bw.close();
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} 
		
	}
	
	public static void main(String[] args) {
		int num = 176;
		String[] files = new String[num];
		for (int i=0; i<num; i++) {
			files[i] = "/Volumes/Biggie/projects/zodiac/vigenere-isomorphisms-index-d1-sorted-" + i + ".txt";
		}
		sort(files, "/Volumes/Biggie/projects/zodiac/vigenere-isomorphisms-index-d1-sorted-all.txt");
//		split("/Volumes/Biggie/projects/zodiac/vigenere-isomorphisms-index-d1.txt",
//				"/Volumes/Biggie/projects/zodiac/vigenere-isomorphisms-index-d1-sorted-", ".txt", 1685774663);
	}
}
