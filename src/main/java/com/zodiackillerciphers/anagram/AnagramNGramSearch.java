package com.zodiackillerciphers.anagram;

/** TODO: ngram stats method for generating anagrams.
 * 
 *  Generate anagram from a given input.
 *
 *  Approach 1:
 *  
 *    - Shuffle the input
 *    - Hillclimber swaps characters inside the input
 *    - Fitness function: ngram score based on spaces (Jarl's data)
 *    - Sometimes try to place a real word from a fixed dictionary
 *    - Secondary score: Word combination score (try running with different weights)
 *    
 *  Approach 2:
 *  
 *    - Generate all words that can be made from the input
 *    - Randomly pick one
 *    - Regenerate word list from remaining letters
 *    - Randomly pick another
 *    - Place it either before or after the first word, based on which one maximizes the spaces-based ngram score.
 *    - Continue until fully anagrammed or no more words can be found.
 *    
 *  Approach 3:
 *  
 *    - Same as approach 2, but use AnagramBuilder to produce sequence of words.
 *    - Permute the word order until the ngrams score is maximized   
 * 
 */
public class AnagramNGramSearch {

}
