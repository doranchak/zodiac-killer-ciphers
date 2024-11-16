package com.zodiackillerciphers.io;

import net.sf.sevenzipjbinding.*;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class Stream7Zip {
    
    public static void stream(String filePath, String password, StreamProcessor processor) throws IOException {
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(filePath, "r")) {
            IInArchive archive = SevenZip.openInArchive(null, new RandomAccessFileInStream(randomAccessFile));
            
            // Iterate through each file in the 7z archive
            for (int i = 0; i < archive.getNumberOfItems(); i++) {
                // Skip directories or non-text files (if needed)
                // if (archive.isFolder(i)) {
                //     continue;
                // }

                // Extract the current file to a stream
                // final int currentItemIndex = i;
                archive.extractSlow(i, new ISequentialOutStream() {
                    @Override
                    public int write(byte[] data) throws SevenZipException {
                        // Use a BufferedReader to process the data line-by-line
                        try (BufferedReader reader = new BufferedReader(
                                new InputStreamReader(new ByteArrayInputStream(data), StandardCharsets.UTF_8))) {
                            String line;
                            while ((line = reader.readLine()) != null) {
                                // Process each line of the file here
                                // System.out.println(line); // Replace this with your logic
                                processor.process(line);
                            }
                        } catch (IOException e) {
                            throw new SevenZipException("Error reading the file", e);
                        }
                        return data.length;
                    }
                }, password);
            }

            // Close the archive
            archive.close();
        } catch (SevenZipException e) {
            e.printStackTrace();
        }
    }
}
