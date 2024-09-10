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

***
## Anagrams

### Anagram Builder

Generate a number of anagrams from the given string.

`./anagram-builder.sh SPIROAGNEW 100`

The above will generate 100 anagrams of SPIROAGNEW, including incomplete anagrams (with leftover letters).
For unlimited anagrams, use `-1` as the 2nd argument.

***
## Corpora related

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

### Anagrams corpus search

Searches corpus for strings that anagram exactly to the given string.
Example: Find corpus samples that anagram to the string SPIROAGNEW:

`./corpus-anagrams.sh SPIROAGNEW`

***
## Dictionaries, n-grams

Polybius keywords have all duplicate letters removed so only unique letters remain.
For example, CABBAGE becomes CABGE.
Given a keyword, find all words that are equivalent.

`java -cp ../target/classes com.zodiackillerciphers.dictionary.KeywordSearch CABGE`

## Insert word breaks (spaces) in strings from a file

Takes the given file, one line at a time, and guesses where word breaks might appear, and prints the results.  TODO: Replace with AZDecrypt-like score, which seems to work better.

`java -cp ../target/classes com.zodiackillerciphers.dictionary.InsertWordBreaksWrapper file_name`

## Telephone Keypad Solver

`./telephone-keypad-solver.sh <digits>`

Finds dictionary words that can be found for the given sequence of digits on a standard telephone keypad.

***
## Z408

### Double/Multiple Z408 Key

Allen TX shooter cipher has a line that decodes to GHOST if you apply Z408 key, treat result as cipher text, and apply Z408 key again.  

`./double-z408-key.sh <n> <cipher>`

Applies the Z408 *n* times to the given cipher.
 
***
## Kryptos

### K4 Corpus Scanner

Looks for length 97 plaintexts where we can also pop in the known K4 cribs.

`./k4-corpus-scanner.sh`

TODO 

`mvn exec:java -Dexec.mainClass="com.zodiackillerciphers.tests.samblake.Kryptos"`

***
## Other

### Pollard's Rho Integer Factorization

A Pollard's Rho implementation I found.

`./pollards-rho.sh <number>`

Attempts to factor the given number using the Pollard's Rho algorithm.


