package com.zodiackillerciphers.hackerrank.for540;

import java.io.*;
import java.math.*;
import java.security.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.regex.*;
import java.util.stream.*;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
public class WebsitePagination {


    /*
     * Complete the 'fetchItemsToDisplay' function below.
     *
     * The function is expected to return a STRING_ARRAY.
     * The function accepts following parameters:
     *  1. 2D_STRING_ARRAY items
     *  2. INTEGER sortParameter
     *  3. INTEGER sortOrder
     *  4. INTEGER itemsPerPage
     *  5. INTEGER pageNumber
     */

    public static List<String> fetchItemsToDisplay(List<List<String>> items, int sortParameter, int sortOrder,
            int itemsPerPage, int pageNumber) {
        // Sort the items
        Collections.sort(items, new Comparator() { // Could have used generics here
            public int compare(Object o1, Object o2) {
                List<String> list1 = (List<String>) o1;
                List<String> list2 = (List<String>) o2;

                int v;
                if (sortParameter == 0) { // Sort by name
                    v = list1.get(0).compareTo(list2.get(0));
                } else if (sortParameter == 1) { // Sort by relevance
                    int rel1 = Integer.parseInt(list1.get(1));
                    int rel2 = Integer.parseInt(list2.get(1));
                    v = Integer.compare(rel1, rel2);
                } else { // Sort by price
                    int p1 = Integer.parseInt(list1.get(2));
                    int p2 = Integer.parseInt(list2.get(2));
                    v = Integer.compare(p1, p2);
                }
                if (sortOrder == 1) // descending, so flip the result.
                    v = 0 - v;
                return v;
            }
        });
        List<String> result = new ArrayList<String>();
        // Paginate, with bounds testing
        for (int i = pageNumber * itemsPerPage; i < pageNumber * itemsPerPage + itemsPerPage && i < items.size(); i++) {
            result.add(items.get(i).get(0));
        }
        return result;
    }

    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getenv("OUTPUT_PATH")));

        int itemsRows = Integer.parseInt(bufferedReader.readLine().trim());
        int itemsColumns = Integer.parseInt(bufferedReader.readLine().trim());

        List<List<String>> items = new ArrayList<>();

        IntStream.range(0, itemsRows).forEach(i -> {
            try {
                items.add(
                    Stream.of(bufferedReader.readLine().replaceAll("\\s+$", "").split(" "))
                        .collect(toList())
                );
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        int sortParameter = Integer.parseInt(bufferedReader.readLine().trim());

        int sortOrder = Integer.parseInt(bufferedReader.readLine().trim());

        int itemsPerPage = Integer.parseInt(bufferedReader.readLine().trim());

        int pageNumber = Integer.parseInt(bufferedReader.readLine().trim());

        List<String> result = fetchItemsToDisplay(items, sortParameter, sortOrder, itemsPerPage, pageNumber);

        bufferedWriter.write(
            result.stream()
                .collect(joining("\n"))
            + "\n"
        );

        bufferedReader.close();
        bufferedWriter.close();
    }
}
