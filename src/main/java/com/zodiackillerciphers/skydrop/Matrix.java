package com.zodiackillerciphers.skydrop;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import com.zodiackillerciphers.annealing.skydrop.SkydropSolution;
import com.zodiackillerciphers.io.FileUtil;
import com.zodiackillerciphers.lucene.Stats;
import com.zodiackillerciphers.ngrams.AZDecrypt;

public class Matrix {
    public static List<String> dictionary;
    public static Map<String, String> plaintextMap;
    public static Map<String, Float> ngrams;
    public static int CAP = 10000;
    public static int N = 3;
    public static void init() {
		List<String> lines = FileUtil.loadFrom("/Users/doranchak/projects/zodiac/github/project-skydrop/matrix1.txt");
		dictionary = new ArrayList<String>();
		plaintextMap = new HashMap<String, String>();
		for (String line : lines) {
			String[] split = line.split(" ");
			dictionary.add(split[0]);
			plaintextMap.put(split[0], split[1]);
		}
		// lines = FileUtil.loadFrom("/Users/doranchak/projects/zodiac/github/zodiac-killer-ciphers/src/main/java/com/zodiackillerciphers/skydrop/bigrams.txt");
        lines = FileUtil.loadFrom("/Users/doranchak/projects/zodiac/github/zodiac-killer-ciphers/src/main/java/com/zodiackillerciphers/skydrop/trigrams.txt");
        ngrams = new HashMap<String, Float>();
		for (String line : lines) {
			String[] split = line.split(" ");
            // ngrams.put(split[0], Float.valueOf(split[1]));
			ngrams.put(split[0], (float) Math.log10(Float.valueOf(split[1])));
		}
        // System.out.println(ngrams);
    }
    public static StringBuffer decode(List<String> words) {
        String decoded = SkydropSolution.decode(words);
        return new StringBuffer(decoded);
    }
    public static void bruteTest() {
        init();

            // Create a max-heap with a custom comparator based on 'score'
            PriorityQueue<Result> maxHeap = new PriorityQueue<>(CAP, new Comparator<Result>() {
                @Override
                public int compare(Result r1, Result r2) {
                    // Sort in descending order of score to simulate a max-heap
                    return Float.compare(r1.score, r2.score);
                }
            });

        List<String> words = new ArrayList<String>();
        for (int i=0;i<N;i++) words.add("");
        // for (int a=0; a<dictionary.size(); a++) {
        //     words.set(0, dictionary.get(a));
        //     for (int b=0; b<dictionary.size(); b++) {
        //         if (a==b) continue;
        //         words.set(1, dictionary.get(b));
        //         StringBuffer decoded = decode(words);
        //         Result result =new Result(score(decoded, N), words + " " + decoded);
        //         insertIntoMaxHeap(maxHeap, result);
        //     }
        // }
        for (int a=0; a<dictionary.size(); a++) {
            words.set(0, dictionary.get(a));
            for (int b=0; b<dictionary.size(); b++) {
                if (b==a) continue;
                words.set(1, dictionary.get(b));
                for (int c=0; c<dictionary.size(); c++) {
                    if (b==c || a==c) continue;
                    words.set(2, dictionary.get(c));
                    StringBuffer decoded = decode(words);
                    Result result =new Result(score(decoded, N), words + " " + decoded);
                    insertIntoMaxHeap(maxHeap, result);
                }
            }
        }
        dumpHeapContents(maxHeap);
    }
        // Method to dump the heap contents in order
        public static void dumpHeapContents(PriorityQueue<Result> maxHeap) {
            // Make a copy to preserve the original heap
            PriorityQueue<Result> copy = new PriorityQueue<>(maxHeap);
    
            System.out.println("Heap contents in descending order:");
            while (!copy.isEmpty()) {
                // Polling returns the element with the highest score
                Result result = copy.poll();
                System.out.println(result);
            }
        }
    public static float score(StringBuffer str, int skips) {
        float score = 0; Float val = null;
        for (int i=0; i<str.length()-N+1; i+=skips) {
            String ngram = str.substring(i,i+N);
            val = ngrams.get(ngram);
            if (val != null) score += val;
        }
        // score *= Stats.entropy(str);
        return score;

    }

    // Method to insert into max-heap with capacity 10,000
    public static void insertIntoMaxHeap(PriorityQueue<Result> maxHeap, Result result) {
        if (maxHeap.size() < CAP) {
            maxHeap.add(result);  // Directly insert if capacity is not reached
            System.out.println("added " + result);
        } else {
            // If heap is full and the new result has a higher score than the minimum, replace it
            Result min = maxHeap.peek();
            if (min != null && result.score > min.score) {
                maxHeap.poll();  // Remove the smallest element
                maxHeap.add(result);  // Insert the new result
                System.out.println("added " + result);
            }
        }
    }

    public static void testAZ() {
        init();
		AZDecrypt.init("/Users/doranchak/projects/zodiac/github/azdecrypt/AZdecrypt/N-grams/5-grams_english_jarlve_reddit_v1912.gz", 5);
        List<String> words = new ArrayList<String>();
        words.add("century");
        words.add("throw");
        words.add("task");
        words.add("corn");
        String decoded = decode(words).toString();
		System.out.println(AZDecrypt.score(FileUtil.convert(decoded).toString(), false) + " " + decoded);

        words.add("library");
        words.add("liar");
        words.add("artefact");
        words.add("seat");
        words.add("general");
        words.add("daring");
        words.add("reduce");
        words.add("unlock");        
        decoded = decode(words).toString();
        String converted = FileUtil.convert(decoded).toString();
		System.out.println(AZDecrypt.score(converted, true, false) + " " + converted + " " + decoded);

        converted = "ALWHSRRTMEYOUPIROADCANUSEEONENDBITUBIFHETRDONOTFLITHBETWENORMTOONERUSEAMPNLUMIDDSAREYOURSCUTSCRATWURMCMONYCJARTHOFCOAMILATFILISMMOSLECANDDASSDGEMAYBEONEWEARESOMAKEEANEEUPLOVEJJSK";
        System.out.println(AZDecrypt.score(converted, true, false) + " " + converted);
        
    }    

    public static void iocDump() {
		List<String> lines = FileUtil.loadFrom("/Users/doranchak/projects/zodiac/github/project-skydrop/matrix1.txt");
        for (String line : lines) {
            String[] split = line.split(" ");
            System.out.println(Stats.ioc(split[1]) + " " + line);
        }

    }
    public static void main(String[] args) {
        // iocDump();
        testAZ();
        // bruteTest();
    }
    
}
