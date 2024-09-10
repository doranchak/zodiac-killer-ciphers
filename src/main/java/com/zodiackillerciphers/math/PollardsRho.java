package com.zodiackillerciphers.math;
/******************************************************************************
 * 
 *  From https://introcs.cs.princeton.edu/java/99crypto/PollardRho.java.html
 *  
 *  Compilation:  javac PollardRho.java
 *  Execution:    java PollardRho N
 *  
 *  Factor N using the Pollard-Rho method.
 *
 *  % java PollardRho 44343535354351600000003434353
 *  149
 *  329569479697
 *  903019357561501
 *
 ******************************************************************************/

import java.math.BigInteger;
import java.security.SecureRandom;
    

class PollardsRho {
    private final static BigInteger ZERO = new BigInteger("0");
    private final static BigInteger ONE  = new BigInteger("1");
    private final static BigInteger TWO  = new BigInteger("2");
    private final static SecureRandom random = new SecureRandom();

    public static BigInteger rho(BigInteger N) {
        BigInteger divisor;
        BigInteger c  = new BigInteger(N.bitLength(), random);
        BigInteger x  = new BigInteger(N.bitLength(), random);
        BigInteger xx = x;

        // check divisibility by 2
        if (N.mod(TWO).compareTo(ZERO) == 0) return TWO;

        do {
            x  =  x.multiply(x).mod(N).add(c).mod(N);
            xx = xx.multiply(xx).mod(N).add(c).mod(N);
            xx = xx.multiply(xx).mod(N).add(c).mod(N);
            divisor = x.subtract(xx).gcd(N);
        } while((divisor.compareTo(ONE)) == 0);

        return divisor;
    }

    public static void factor(BigInteger N) {
        if (N.compareTo(ONE) == 0) return;
        if (N.isProbablePrime(20)) { System.out.println(N); return; }
        BigInteger divisor = rho(N);
        factor(divisor);
        factor(N.divide(divisor));
    }

 
    public static void main(String[] args) {
        BigInteger N = new BigInteger(args[0]); // 18446744073709551617 = 274177 x 67280421310721
        factor(N);
    }
}