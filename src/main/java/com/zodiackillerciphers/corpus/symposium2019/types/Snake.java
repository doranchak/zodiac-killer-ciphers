package com.zodiackillerciphers.corpus.symposium2019.types;

import java.util.Random;

import com.zodiackillerciphers.ciphers.Ciphers;
import com.zodiackillerciphers.corpus.symposium2019.CipherBase;

public class Snake extends CipherBase {
	static Random rand = new Random();
	public int corner = 0;
	public boolean directionRow = false;
	public int width = 0;
	public Integer width_override = null; 
	@Override
	public String firstLayer(String plaintext) {
		// snake
		corner = rand.nextInt(4);
		directionRow = rand.nextBoolean();
		if (width_override == null) 
			width = 2 + rand.nextInt(20); // [2,20]
		else 
			width = width_override;
//		say("corner " + corner + " dir " + directionRow + " width " + width);
		String snake = com.zodiackillerciphers.transform.operations.Snake.transform(plaintext, corner, directionRow, width, false, false);
		snake = snake.replaceAll(" ", "");
		return snake;
	}

	/** test hom vs snake untransposed digraphic iocs */
//	public static void testHomVsSnake() {
//		Homophonic hom = new Homophonic();
//		String homCipher = hom.makeCipher(Ciphers.Z408_SOLUTION.substring(0,340));
//		Snake snake = new Snake();
//		snake.width_override = 20;
//		String snakeCipher = snake.makeCipher(Ciphers.Z408_SOLUTION.substring(0,340));
//	}
	
	public static void test() {
		String pt = Ciphers.Z408_SOLUTION.substring(0, 340);
		Snake snake = new Snake();
		String result = snake.makeCipher(pt);
		System.out.println("Result: " + result);

	}

	public static void main(String[] args) {
		 test();
//		System.out.println(com.zodiackillerciphers.transform.operations.Snake.transform("Qa9q!A0A73yqaPYX0ECRaT4J4qkqc58BQsN2qpA2bobWogBrnTdwtlciqnQ3w3V7fjE1GpHD3N1HGc05dFhLA3wZWpa50qTl6F1Uv13jHYxkB3BGLnd0xXBl5phr6snxfuz5LypD3CoVEi454sv5y7Cv!WfqSxB1k9dv0NvTmU8d3uz4XTjdzQrE5A2HSWS8CTuslTUHvya!3T2q1vWBb2BPywGyOKlTw4vRg9TQeUvLyoALjxjdNzy98vWJAQ08ut2S7fEUVXb3THzuGTGIl7kxhXtMTHGhJcsLAlQCW5xo94RR68q1FgbB2HWqxoXLmhs3rd5tdTN6T3grDxLF", 2, false, 99, false, true));
	}

}
