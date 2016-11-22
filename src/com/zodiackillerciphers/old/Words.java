package com.zodiackillerciphers.old;

import java.util.HashSet;
import java.util.Set;

/** words from Zodiac's letters */
public class Words {
	// list of unique words in zodiac letters.  includes singularizations of words that appear in plural.
	public static String[] lettersWords = new String[] {
		"a", "about", "accid", "across", "activity", "adjusted", "afraid", "after", "afterlife", "afternoon", "again", "aim", "alive", "all",
		"alley", "alleys", "allways", "alone", "also", "always", "am", "an", "anamal", "and", "anilating", "another", "answer", "ant",
		"any", "anyway", "are", "area", "around", "as", "ask", "asked", "asking", "ass", "asses", "at", "attention", "aug",
		"awake", "away", "b", "baby", "babysit", "babysits", "back", "backwards", "ball", "barrel", "bates", "battered", "battery", "bay",
		"be", "beam", "beautiful", "because", "become", "beef", "been", "began", "being", "best", "better", "bewarei", "big", "billiard",
		"black", "blast", "blond", "blood", "bluber", "blue", "bomb", "booth", "bouncing", "boy", "breast", "broke", "broken", "brown",
		"brunett", "brush", "bullet", "bullshit", "burn", "burned", "burning", "bury", "bus", "buss", "but", "buton", "butons", "button",
		"buttons", "by", "caen", "cage", "cages", "call", "can", "cannot", "car", "cars", "catch", "caught", "cc", "ceiling",
		"cell", "cene", "center", "cerous", "check", "cheer", "chief", "children", "chocked", "christmass", "cid", "cipher", "circle", "circut",
		"city", "clean", "close", "closeing", "closes", "cloudy", "code", "collect", "collecting", "come", "complet", "complied", "comply", "confession",
		"conscience", "considerably", "control", "controol", "cop", "cops", "could", "couple", "coupled", "cover", "coverage", "crack", "cracked", "crackproof",
		"credit", "crooked", "cruse", "cue", "cues", "cut", "cutting", "damn", "dangeroue", "darck", "dark", "darkened", "date", "daughter",
		"daughters", "day", "dead", "dear", "death", "deep", "delicious", "deposit", "described", "detail", "details", "did", "die", "died",
		"dificult", "dig", "discon", "distributor", "do", "dont", "doo", "door", "dot", "down", "dozen", "draining", "draw", "dress",
		"dressed", "drew", "dripping", "driven", "driver", "drivers", "drove", "drownding", "dud", "dungen", "during", "each", "easy", "eat",
		"eats", "ebeorietemethhpiti", "ect", "editor", "editors", "either", "else", "elses", "end", "ended", "engine", "enjoy", "enterprise", "epasode",
		"etc", "even", "evening", "evere", "every", "everyone", "everything", "examiner", "except", "experence", "extreamly", "fact", "fall", "fart",
		"fat", "fed", "felt", "female", "few", "fiddle", "figgure", "find", "finding", "finished", "fire", "fired", "fireing", "firm",
		"first", "fk", "flash", "floor", "followed", "for", "forrest", "fought", "found", "fran", "from", "front", "fry", "fryst",
		"full", "fun", "game", "gave", "get", "getting", "girl", "girls", "give", "given", "gives", "glory", "go", "going",
		"good", "gorged", "got", "grabbed", "great", "guess", "gun", "had", "hand", "hands", "hang", "happy", "hard", "has",
		"hate", "have", "having", "he", "head", "heat", "hell", "help", "her", "herb", "here", "high", "hill", "hills",
		"him", "his", "hit", "hold", "holding", "home", "hope", "horizon", "horrible", "hose", "how", "hower", "howers", "hung",
		"i", "idenitysic", "if", "im", "implied", "in", "inch", "inches", "inflicting", "insane", "instead", "intersting", "into", "is",
		"isnt", "it", "its", "ive", "job", "july", "just", "keep", "kicked", "kiddie", "kiddies", "kill", "killed", "killing",
		"kind", "knee", "knife", "lamb", "last", "laugh", "lay", "leaped", "leave", "leg", "legs", "let", "letter", "library",
		"life", "lift", "light", "like", "lip", "lips", "list", "listen", "lit", "little", "livingunwilling", "ll", "lone", "long",
		"longer", "loose", "lot", "mailed", "make", "makes", "making", "man", "map", "maple", "massive", "material", "maybe", "me",
		"meannie", "meannies", "mech", "melvin", "melvins", "middle", "might", "mildly", "mind", "mine", "minute", "minutes", "miss", "moment",
		"money", "month", "months", "more", "morning", "most", "motorcicle", "motorcicles", "mouth", "move", "movie", "much", "murderer", "must",
		"my", "nail", "nails", "name", "nasty", "near", "neck", "need", "negro", "new", "next", "nice", "night", "nights",
		"nine", "nineth", "no", "noise", "north", "not", "notice", "now", "of", "off", "offered", "offs", "oh", "on",
		"once", "one", "ones", "only", "open", "openly", "opens", "or", "origionaly", "other", "others", "out", "over", "pace",
		"page", "pages", "pain", "paper", "paradice", "park", "parked", "part", "parts", "pay", "pencel", "people", "phone", "photoelectric",
		"pick", "piece", "pig", "pine", "placed", "play", "player", "players", "plea", "please", "pleass", "plunged", "police", "posibly",
		"power", "price", "print", "prior", "promised", "proof", "properly", "prove", "ps", "published", "punish", "punished", "put", "quietly",
		"quite", "race", "raceing", "races", "rain", "rampage", "rather", "reach", "read", "ready", "reason", "reborn", "red", "remain",
		"report", "reports", "require", "requires", "rh", "ride", "right", "ring", "riverside", "road", "rock", "rocks", "rolled", "rope",
		"rub", "run", "safe", "said", "salt", "same", "san", "sat", "save", "saw", "saying", "school", "scream", "screaming",
		"searched", "seat", "see", "seeing", "self", "selves", "sent", "set", "seven", "sf", "sfpd", "shabbly", "shall", "shapely",
		"she", "shirt", "shoe", "shoes", "shook", "shoot", "shot", "should", "shut", "sick", "sight", "sights", "silowet", "silowets",
		"sister", "sisters", "sitting", "skin", "slaughter", "slave", "slaves", "sloi", "slower", "slowly", "small", "smarter", "smudge", "so",
		"some", "someone", "something", "sorry", "speaking", "spilling", "splinter", "splinters", "spoiling", "spot", "spray", "spurting", "squealing", "squirm",
		"squirmed", "srounded", "st", "stained", "stalking", "starting", "state", "station", "stop", "store", "street", "streets", "strike", "struggle",
		"stumbling", "stupid", "summer", "sun", "supply", "swamped", "swich", "swiches", "tag", "take", "talk", "talked", "tape", "taped",
		"target", "targets", "taxi", "teen", "tell", "ten", "tenth", "teritory", "th", "than", "thank", "thashing", "that", "thats",
		"the", "their", "them", "then", "there", "these", "they", "thing", "things", "think", "thinking", "thirteenth", "this", "though",
		"thrilling", "throat", "thumb", "thumbs", "thus", "tie", "till", "time", "times", "tire", "tired", "tires", "to", "told",
		"too", "top", "torture", "toschi", "town", "tree", "trees", "triger", "truly", "try", "trying", "twich", "twiched", "twisted",
		"two", "type", "uncertain", "under", "unhappy", "until", "untill", "up", "upset", "use", "vallejo", "very", "victim", "victims",
		"victom", "violently", "wait", "waited", "waiting", "walk", "walking", "walks", "wall", "wandering", "want", "warm", "warning", "was",
		"washington", "watch", "water", "way", "we", "wear", "wearing", "weekend", "well", "went", "were", "what", "when", "where",
		"which", "while", "who", "whole", "wife", "wild", "will", "willing", "willingly", "window", "wipe", "wiped", "wire", "wish",
		"wishes", "with", "wives", "woeman", "wondering", "wont", "work", "would", "wouldnt", "writing", "year", "years", "yes", "you",
		"youll", "young", "your", "yours", "z", "zodiac"		
	};
	
	/** return a convenient hash set of the words */
	public static Set<String> lettersWords() {
		Set<String> words = new HashSet<String>();
		for (String s : lettersWords) words.add(s);
		return words;
	}
	
	
}
