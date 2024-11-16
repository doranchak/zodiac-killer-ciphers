package com.zodiackillerciphers.skydrop;

public class Result {
    float score;
    String info;

     // Constructor
     public Result(float score, String info) {
        this.score = score;
        this.info = info;
    }

    public String toString() {
        return score + " " + info;
    }

}
