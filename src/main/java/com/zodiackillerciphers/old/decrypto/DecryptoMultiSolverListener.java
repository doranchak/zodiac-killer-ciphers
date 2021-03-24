package com.zodiackillerciphers.old.decrypto;

public interface DecryptoMultiSolverListener extends DecryptoListener
{
    public void setMessage(String s);

    public void finished(double elapsedTime, boolean timedout);
}
