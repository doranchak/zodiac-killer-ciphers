package com.zodiackillerciphers.corpus;
/** dump the variations of "Little List" to an html table for easy comparison */
public class LittleList {
	public static String[] zodiac = new String[] {
		"As some day it may hapen that a victom must be found.",
		"I've got a little list.  I've got a little list,",
		"of society offenders who might well be underground",
		"who would never be missed who would never be missed.",
		"There is the pestulentual nucences who whrite for autographs,",
		"all people who have flabby hands and irritating laughs.",
		"All children who are up in dates and implore you with im platt.",
		"All people who are shakeing hands shake hands like that.",
		"And all third persons who with unspoiling take thoes who insist.",
		"They'd none of them be missed. They'd none of them be missed.",
		"There's a banjo seranader and the others of his race",
		"and the piano orginast I got him on the list.",
		"All people who eat pepermint and phomphit in your face,",
		"they would never be missed They would never be missed",
		"And the Idiout who phraises with inthusastic tone",
		"of centuries but this and every country but his own.",
		"And the lady from the provences who dress like a guy",
		"who doesn't cry",
		"and the singurly abnormily the girl who never kissed.",
		"I don't think she would be missed Im shure she wouldn't be missed.",
		"And that nice impriest that is rather rife",
		"the judicial hummerest I've got him on the list.",
		"All funny fellows, commic men and clowns of private life.",
		"They'd none of them be missed. They'd none of them be missed.",
		"And uncompromising kind",
		"such as wachmacallit, thingmebob, and like wise, well - - nevermind,",
		"and tut tut tut tut, and whashisname, and you know who,",
		"but the task of filling up the blanks I rather leave up to you.",
		"But it really doesn't matter whom you place upon the list,",
		"for none of them be missed, none of them be missed."
	};
	
	public static String[] original = new String[] {
		"As some day it may happen that a victim must be found,",
		"I've got a little list - I've got a little list",
		"Of social offenders who might well be underground,",
		"And who never would be missed - who never would be missed!",
		"There's the pestilential nuisances who write for autographs -",
		"All people who have flabby hands and irritating laughs -",
		"All children who are up in dates, and floor you with 'em flat -",
		"All persons who in shaking hands, shake hands with you like THAT -",
		"And all third persons who on spoiling <a href=\"http://en.wiktionary.org/wiki/t%C3%AAte-%C3%A0-t%C3%AAte\">TETE-E-TETES<a> insist -",
		"They'd none of 'em be missed - they'd none of 'em be missed!",
		"There's the nigger serenader, and the others of his race, (alternate version: There's the banjo serenader, and the others of his race,)",
		"And the piano organist - I've got him on the list!",
		"And the people who eat peppermint and puff it in your face,",
		"They never would be missed - they never would be missed!",
		"Then the idiot who praises, with enthusiastic tone,",
		"All centuries but this, and every country but his own;",
		"And the lady from the provinces, who dresses like a guy,",
		"And who \"doesn't think she waltzes, but would rather like to try\";",
		"And that FIN-DE-SIECLE anomaly, the scorching motorist - (alternate version: And that singular anomaly, the lady novelist--)",
		"I don't think he'd be missed - I'm SURE he'd not be missed! (alternate version: I don't think she'd be missed--I'm _sure_ she'd not be missed!)",
		"And that <a href=\"http://en.wikipedia.org/wiki/Nisi_prius\">NISI PRIUS</a> nuisance, who just now is rather rife,",
		"The Judicial humorist - I've got HIM on the list!",
		"All funny fellows, comic men, and clowns of private life -",
		"They'd none of 'em be missed - they'd none of 'em be missed!",
		"And apologetic statesmen of the compromising kind,",
		"Such as - What-d'ye-call-him - Thing'em-Bob, and likewise - Never-mind,",
		"And 'St - 'st - 'st - and What's-his-name, and also - You-know-who",
		"(The task of filling up the blanks I'd rather leave to YOU!)",
		"But it really doesn't matter whom you put upon the list,",
		"For they'd none of 'em be missed - they'd none of 'em be missed!"
	};
	
	public static String[] groucho = new String[] {
		"As some day it may happen that a victim must be found ",
		"I've got a little list I've got a little list ",
		"Of society offenders who might well be underground ",
		"and who never would be missed who never would be missed ",
		"There's the pestilential nuisances who write for autographs, ",
		"all people who have flabby hands and irritating laughs. ",
		"And all children who are up in dates and floor you with 'em flat, ",
		"and all persons who when shaking hands shake hands with you like that. ",
		"And all third persons who on spoiling tete a tetes insist, ",
		"they'd none of them be missed they'd none of them be missed. ",
		"There's the banjo serenader and the others of his race. ",
		"and the piano-organist, I've got him on the list. ",
		"And the people who eat peppermint and puff it in your face, ",
		"they never would be missed, they never would be missed. ",
		"And the idiout who praises with enthusiastic tone, ",
		"all centuries but this and every country but his own. ",
		"And the lady from the provinces who dresses like a guy, ",
		"and who doesn't think she dances but would rather like to try. ",
		"And that singular anomoly, the girl who's never kissed, ",
		"I don't think she'd be missed, I'm sure she'd not be missed. ",
		"And that nisi prius nuisance who just now is rather rife, ",
		"The judicial humorist, I've got him on the list. ",
		"All funny fellows, comic men and clowns of private life, ",
		"they'd none of them be missed they'd none of them be missed. ",
		"An apologetic statesmen of a compromising kind, ",
		"such as eh what do you call a thing-a-ma-bob and a like-wise, ah never mind. ",
		"And a tut tut tut and whats-his-name, and well, well you know who. ",
		"Ah the task of filling up the blanks I'd rather leave to you. ",
		"For it really doesn't matter whom you put upon the list, ",
		"for they'd none of them be missed, they'd none of them be missed. "		
	};
	
	public static void make() {
		System.out.println("<table class=\"z\">");
		System.out.println("<tr><td class=\"q1\">Zodiac version is shown in black</td></tr>");
		System.out.println("<tr><td class=\"q2\"><b>Original version is shown in green</b></td></tr>");
		System.out.println("<tr><td class=\"q3\"><b>Groucho version is shown in blue</b></td></tr>");

		for (int i=0; i<zodiac.length; i++) {
			System.out.println("<tr><td class=\"q1\">" + zodiac[i] + "</td></tr>");
			System.out.println("<tr><td class=\"q2\">" + original[i] + "</td></tr>");
			System.out.println("<tr><td class=\"q3\">" + groucho[i] + "</td></tr>");
		}
		
		System.out.println("</table>");
	}
	public static void make2() {
		System.out.println("<table class=\"z\">");
		System.out.println("<tr><td class=\"q1\"><u>Zodiac version</u></td>");
		System.out.println("<td class=\"q2\"><b><u>Original version</u></b></td></tr>");

		for (int i=0; i<zodiac.length; i++) {
			System.out.println("<tr valign=\"top\"><td class=\"q1\">" + zodiac[i] + "</td>");
			System.out.println("<td class=\"q2\">" + original[i] + "</td></tr>");
		}
		
		System.out.println("</table>");
	}
	public static void main(String[] args) {
		make2();
	}
}
