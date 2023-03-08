package com.zodiackillerciphers.hackerrank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class SimpleTextEditor {

    static StringBuffer sb = new StringBuffer();
    static List<String> undoStack = new ArrayList<String>();
    static int MAX_UNDO = 100;
    
    static void append(String str, boolean trackUndo) {
        //System.out.println("append " + str);
        sb.append(str);
        //System.out.println("append: " + sb);
        if (trackUndo) {
        	push("2 " + str.length());
        }
    }    
    static void delete(int k, boolean trackUndo) {
        //System.out.println("delete " + k);
        String sub = sb.substring(sb.length()-k);
        sb.delete(sb.length()-k, sb.length());
        //System.out.println("delete: " + sb);
        if (trackUndo) {
        	push("1 " + sub);
        }
    }
    static void push(String line) {
    	undoStack.add(line);
    	if (undoStack.size() == MAX_UNDO) {
    		undoStack.remove(0);
    	}
    		
    }
    static void print(int k) {
        //System.out.println("print " + k);
        System.out.println(sb.charAt(k-1));
    }    
    static void undo() {
        //System.out.println("Undo stack:" + undoStack);
        String line = undoStack.get(undoStack.size()-1);
        undoStack.remove(undoStack.size()-1);
        processLine(line, false);
    }
    
    static void processLine(String line, boolean trackUndo) {
        String[] split = line.split(" ");
        String op = split[0];
        if (op.equals("1")) {
            append(split[1], trackUndo);
        }
        else if (op.equals("2")) {
            delete(Integer.parseInt(split[1]), trackUndo);
        }
        else if (op.equals("3")) print(Integer.parseInt(split[1]));
        else if (op.equals("4")) undo();        
    }

    public static void main(String[] args) {
        /* Enter your code here. Read input from STDIN. Print output to STDOUT. Your class should be named Solution. */
      BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
      try {
          int Q = Integer.parseInt(br.readLine());
          for (int i=0; i<Q; i++) {
              String line = br.readLine();
           //   System.out.println("line " + i + ": " + line);
              //System.out.println("undo stack size " + undoStack.size());
              processLine(line, true);
          }
      } catch (IOException ioe) {
         System.out.println(ioe);
      }
    }
}
