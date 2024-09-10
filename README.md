# zodiac-killer-ciphers

This is a lot of code, experiments, and documentation that I developed over the many years I spent investigating the Zodiac Killer ciphers, and developing my site:  http://zodiackillerciphers.com

Much of the code is throwaway experimental code, but I'm trying to carve out all the useful reusable stuff into scripts and Web services that can be easily invoked to perform tasks related to cryptanalysis.

To build:

	mvn clean compile

To bring up Spring Boot (Java back end that provides service endpoints):

	mvn spring-boot:run

It will bring up web server at:

	http://localhost:8080  (The index page there will also link to a lot of static applications and content)

Invoke the sample endpoints:

	http://localhost:8080/hello?name=Smeg
	http://localhost:8080/greeting?name=SmeggyJSON

Bring up Angular (user interface front end for interacting with the service endpoints):

	Change to this folder: src/main/webapp/angular-src/zkc
	npm install (if needed)
	ng serve
	Then open browser to http://localhost:4200

# Scripts

The following are individual programs and utilities that can be run directly via the command line in the `scripts` folder.

## Anagrams

### Anagram Builder

Generate a number of anagrams from the given string.

`./anagram-builder.sh SPIROAGNEW 100`

The above will generate 100 anagrams of SPIROAGNEW, including incomplete anagrams (with leftover letters).
For unlimited anagrams, use `-1` as the 2nd argument.

## Z18 Corpus Scanner

These programs require a corpus archived in a specific location.  TODO: Document and make more configurable.

### "The Zodiac"

If we let Z18's plaintext end with "THEZODIAC", the rest fits this pattern: `x_H____Ax` (where `x` is any letter A-Z).  Generate samples from a corpus and output the ones that fit the pattern:

`./z18-corpus-scanner-the-zodiac.sh`

### Letter Counts

Generate corpus samples of the given length, that have the given minimum and maximum occurences of the given letter.  Example:

`./z18-corpus-scanner-letter-count.sh 18 E 5 7`:  Produces samples that are 18 letters long and have at least 5 occurrences of `E`, but no more than 7 of them.

### Cragle's Searches

Look for corpus samples of length 18 that have these letters in them: E E E E E E E R N O I

`./z18-corpus-scanner-cragle.sh`

Look for corpus samples of length 18 that have these letters in them: N S S R E E E E E E E, or N S S R E E E E E E E T T T T H H

`./z18-corpus-scanner-cragle2.sh`

### Nicodemus Search

Looks for corpus samples meeting criteria similar to Nicodemus' claims.  
Example:  Find corpus samples of length 18 that have 9 unique pairs.  5 of them must start with the same letter, and 5 of them must end with the same letter:

`./z18-corpus-scanner-nicodemus.sh 18 9 5`

### K4 Search

TODO 

`./`

### Anagrams corpus search

Searches corpus for strings that anagram exactly to the given string.
Example: Find corpus samples that anagram to the string SPIROAGNEW:

`java -cp ../target/classes com.zodiackillerciphers.corpus.AnagramsSearch SPIROAGNEW`

### Polybius keyword search

Polybius keywords have all duplicate letters removed so only unique letters remain.
For example, CABBAGE becomes CABGE.
Given a keyword, find all words that are equivalent.

`java -cp ../target/classes com.zodiackillerciphers.dictionary.KeywordSearch CABGE`

### Insert word breaks (spaces) in strings from a file

Takes the given file, one line at a time, and guesses where word breaks might appear, and prints the results.

`java -cp ../target/classes com.zodiackillerciphers.dictionary.InsertWordBreaksWrapper file_name`
