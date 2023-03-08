package com.zodiackillerciphers.hackerrank;

import java.io.*;
import java.math.*;
import java.security.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;

public class MergeLists {

    static class SinglyLinkedListNode {
        public int data;
        public SinglyLinkedListNode next;

        public SinglyLinkedListNode(int nodeData) {
            this.data = nodeData;
            this.next = null;
        }
    }

    static class SinglyLinkedList {
        public SinglyLinkedListNode head;
        public SinglyLinkedListNode tail;

        public SinglyLinkedList() {
            this.head = null;
            this.tail = null;
        }

        public void insertNode(int nodeData) {
            SinglyLinkedListNode node = new SinglyLinkedListNode(nodeData);

            if (this.head == null) {
                this.head = node;
            } else {
                this.tail.next = node;
            }

            this.tail = node;
        }
    }

    public static void printSinglyLinkedList(SinglyLinkedListNode node, String sep, BufferedWriter bufferedWriter) throws IOException {
        while (node != null) {
            bufferedWriter.write(String.valueOf(node.data));

            node = node.next;

            if (node != null) {
                bufferedWriter.write(sep);
            }
        }
    }

    // Complete the mergeLists function below.

    /*
     * For your reference:
     *
     * SinglyLinkedListNode {
     *     int data;
     *     SinglyLinkedListNode next;
     * }
     *
     */
    static SinglyLinkedListNode mergeLists(SinglyLinkedListNode head1, SinglyLinkedListNode head2) {
        SinglyLinkedListNode newNode = null;
        SinglyLinkedListNode newNodeHead = null;
        while (head1 != null && head2 != null) {
            int data1 = head1.data;
            int data2 = head2.data;
            
            SinglyLinkedListNode toMerge = null;
            if (data1 <= data2) {
                toMerge = head1;
                head1 = head1.next;
                //System.out.println("list1 merge " + data1);
            } else {
                toMerge = head2;
                head2 = head2.next;
                //System.out.println("list2 merge " + data2);
            }
            
            if (newNode == null) {
                newNode = toMerge;
                newNodeHead = newNode;
            }
            else {
                newNode.next = toMerge;
            }
            newNode = newNode.next;
        }
        
        while (head1 != null) {
            newNode.next = head1;
            newNode = newNode.next;
            head1 = head1.next;
        }
        while (head2 != null) {
            newNode.next = head2;
            newNode = newNode.next;
            head2 = head2.next;
        }
        return newNodeHead;
    }

    private static final Scanner scanner = new Scanner(System.in);
}