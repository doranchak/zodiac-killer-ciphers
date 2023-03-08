package com.zodiackillerciphers.hackerrank.for540;

import java.io.*;

import java.util.*;
import java.text.*;
import java.math.*;
import java.util.regex.*;
import java.net.*;
import com.google.gson.*;
public class Solution {
    /*
     * Complete the function below.
     */
    static int getNumberOfMovies(String substr) {
        /*
         * Endpoint: "https://jsonmock.hackerrank.com/api/moviesdata/search/?Title=substr"
         */
        try {
            // Read the query response from the endpoint
            URL url = new URL("https://jsonmock.hackerrank.com/api/moviesdata/search/?Title=" + substr);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            StringBuffer sb = new StringBuffer();
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            in.close();
        
            // Convert to JSON object
            JsonObject jsonObject = new JsonParser().parse(sb.toString()).getAsJsonObject();
            // Query for the "total" field value
            return jsonObject.get("total").getAsInt();
        } catch (Exception e) { // Possible I/O exception occurred
            e.printStackTrace();
        }
        return 0; // no results due to an I/O exception
    }
    public static void main(String[] args) throws IOException{
        Scanner in = new Scanner(System.in);
        final String fileName = System.getenv("OUTPUT_PATH");
        BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
        int res;
        String _substr;
        try {
            _substr = in.nextLine();
        } catch (Exception e) {
            _substr = null;
        }
        
        res = getNumberOfMovies(_substr);
        bw.write(String.valueOf(res));
        bw.newLine();
        
        bw.close();
    }
}