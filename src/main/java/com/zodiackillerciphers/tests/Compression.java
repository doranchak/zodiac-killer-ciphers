package com.zodiackillerciphers.tests;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipOutputStream;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.transform.CipherTransformations;

/** use compression as a way to estimate how likely a string contains a real message.
 *  seems to be able to distringuish plaintext from scrambles.
 *  but can't distinguish between scrambled cipher and original.
 *  
 *  */
public class Compression {
	
	/** return the size of the given string when compressed.
	 * which == 0: gzip
	 * which == 1: zip
	 */
	public static int compressedSize(String str, int which) {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			if (which == 0) {
				GZIPOutputStream gzip = new GZIPOutputStream(out);
				gzip.write(str.getBytes());
				gzip.close();
			} else if (which == 1) {
				ZipOutputStream zip = new ZipOutputStream(out);
				zip.write(str.getBytes());
				zip.close();
			}
			return out.size();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	public static int deflaterSize(String inputString) {
		 try {
		     // Encode a String into bytes
		     byte[] input = inputString.getBytes("UTF-8");

		     // Compress the bytes
		     byte[] output = new byte[1000];
		     Deflater compresser = new Deflater();
		     compresser.setInput(input);
		     compresser.finish();
		     int compressedDataLength = compresser.deflate(output);
		     compresser.end();
		     return compressedDataLength;
		 } catch(java.io.UnsupportedEncodingException ex) {
		     // handle
		 }
		 return 0;
	}
	public static void test() {
		String cipher = Ciphers.Z408;
		System.out.println(compressedSize(cipher, 1));
		for (int i=0; i<100; i++) {
			cipher = CipherTransformations.shuffle(cipher);
			System.out.println(compressedSize(cipher, 1));
		}
			
	}
	public static void testPt() {
		String pt = Ciphers.Z408_SOLUTION;
		System.out.println(compressedSize(pt, 0));
		for (int i=0; i<100; i++) {
			pt = CipherTransformations.shuffle(pt);
			System.out.println(compressedSize(pt, 0));
		}
			
	}
	
	public static void testDeflater() {
		String cipher = Ciphers.Z408;
		System.out.println(deflaterSize(cipher));
		for (int i=0; i<100; i++) {
			cipher = CipherTransformations.shuffle(cipher);
			System.out.println(deflaterSize(cipher));
		}
	}
	
	public static void testDeflaterPt() {
		String cipher = Ciphers.Z408_SOLUTION;
		System.out.println(deflaterSize(cipher));
		for (int i=0; i<100; i++) {
			cipher = CipherTransformations.shuffle(cipher);
			System.out.println(deflaterSize(cipher));
		}
	}
	
	public static void main(String[] args) {
		//testPt();
		testDeflaterPt();
//		testDeflater();
	}
}
