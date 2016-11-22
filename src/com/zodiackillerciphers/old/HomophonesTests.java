package com.zodiackillerciphers.old;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class HomophonesTests {
	public static void main(String[] args) {
		//testTranslateCipher(Zodiac.cipher[1]);
        //HomophonesKingBahler.testFindStrings(Zodiac.cipher[1]);
		//String cipher = Ciphers.cipher[HomophonesProblem.which];
		String cipher = null; //Quadrants.makeCipher(1,0,0,0,0,0,0,0,0,0,0,0,0,0);
		//String cipher = Quadrants.makeCipher(1, 17,10,0,7,180,180,180,270,0,0,0,1,1); // best 408 #5 (score=719)
		//String cipher = Quadrants.makeCipher(0,4,7,0,7,180,180,180,180,1,0,0,0,1); // best 340
		
		/*
			Object[] results = Homophones.scoreHomophones(cipher,0,0f);
			Homophones.dump((Map<Character, Object[]>)results[0]);
		Homophones.dump((List<Object[]>)results[1]);
		Homophones.dumpAS((Map<String, Integer[]>)results[2]);*/
		
		HomophonesKingBahler.testFindStrings(cipher);
		
		//Quadrants.testResult(1, 0,0,0,0,0,0,0,0,0,0,0,0,0); // original 408
		//HomophonesKingBahler.testFindStrings(Zodiac.cipher[0]);
		//Quadrants.testResult(1, 20, 3, 0, 1, 0, 0, 0, 90, 0, 0, 0, 0, 4); // best 408
		//Quadrants.testResult(1, 20, 17, 1, 0, 180, 0, 180, 0, 0, 0, 0, 0, 0); // best 408 #2
        //Quadrants.testResult(1,20,14,0,19,90,0,0,0,0,0,0,1,4); // best 408 #3 (score=644)
		//Quadrants.testResult(1, 16,10,0,7,180,180,180,270,0,0,0,1,1); // best 408 #4 (score=685)
		//Quadrants.testResult(1, 17,10,0,7,180,180,180,270,0,0,0,1,1); // best 408 #5 (score=719)
		 

        //Quadrants.testResult(0,17,9,1,15,0,0,180,0,0,1,0,0,4); // best 340
		//Quadrants.testResult(0,4,7,0,7,180,180,180,180,1,0,0,0,1); // best 340 (score=420)  
		

		//testFindStrings("9%P/Z/UB%kOR=pX=BWV+eGYF69HP@K!qYeMJY^UIk7qTtNQYD5)S(/9#BPORAU%fRlqEk^LMZJdr\\pFHVWe8Y@+qGD9KI)6qX85zS(RNtIYElO8qGBTQS#BLd/P#B@XqEHMU^RRkcZKqpI)Wq!85LMr9#BPDR+j=6\\N(eEUHkFZcpOVWI5+tL)l^R6HI9DR_TYr\\de/@XJQAP5M8RUt%L)NVEKH=GrI!Jk598LMlNA)Z(PzUpkA9#BVW\\+VTtOP^=SrlfUe67DzG%%IMNk)ScE/9%%ZfAP#BVBX9zXADd\\7L!=qpeXqWq_F#8c+@9A9B##6e5PORXQF%GcVZ_H%OT5RUc+_dYq_^SqWTtq_8JI+rBPQW6E@e!VZeGYKE_TYA9%#Lt_r9WI6qEHM)=UIkXJdF");
		//testExample();
		//testReduceString2();
		//testCipher();
		/*for (int i=0; i<testCiphers.length; i++) 
			testEvaluateCipher(testCiphers[i]);*/
	}

}
