package com.zodiackillerciphers.tests.mapcode;

import java.util.List;

import com.zodiackillerciphers.io.FileUtil;

public class Solutions {
	/** convert solutions file to javascript objects */
	public static void convert(String path) {
		System.out.println("var solutions = new Array(");
		List<String> lines = FileUtil.loadFrom(path);
		for (String line : lines) {
			String[] split = line.split("\\t");
			System.out.println("new Solution(\"" + split[0] + "\", \""
					+ split[1] + "\", " + split[2] + ", " + split[3] + ", "
					+ split[4] + "),");
		}
		System.out.println(");");
	}
	
	/** dump all interpretations of radian values, calculate similarity to given radians/inches */
	public static void interpret(String path, float r, float i) {
		List<String> lines = FileUtil.loadFrom(path);
		for (String line : lines) {
			String[] split = line.split("\\t");
			float radians = Float.valueOf(split[2]);
			float inches = Float.valueOf(split[3]);
			double actualRadians = 0;
			double distance = 0;
			if (radians <= 2*Math.PI) {
				actualRadians = radians; 
				distance = Math.sqrt((actualRadians-r)*(actualRadians-r)+(inches-i)*(inches-i));
				System.out.println(distance + " " + actualRadians + " " + inches + " " + radians);
			}
			if (radians <= 64) {
				actualRadians = radians/64*2*Math.PI;
				distance = Math.sqrt((actualRadians-r)*(actualRadians-r)+(inches-i)*(inches-i));
				System.out.println(distance + " " + actualRadians + " " + inches + " " + radians);
			}
			if (radians <= 6400) {
				actualRadians = radians/6400*2*Math.PI;
				distance = Math.sqrt((actualRadians-r)*(actualRadians-r)+(inches-i)*(inches-i));
				System.out.println(distance + " " + actualRadians + " " + inches + " " + radians);
			}
		}
	}
	

	public static void main(String[] args) {
		//convert("/Users/doranchak/projects/zodiac/z32_cherrypicked_3");
		interpret("/Users/doranchak/projects/zodiac/z32_cherrypicked_3", 4.96f, 3.74f);
	}

}
