# zodiac-killer-ciphers

This is a lot of code, experiments, and documentation that I developed over the many years I spent investigating the Zodiac Killer ciphers, and developing my site:  http://zodiackillerciphers.com

Much of the code is throwaway experimental code, but I'm trying to carve out all the useful reusable stuff into Web services that can be easily invoked to perform tasks related to cryptanalysis.

To build:

	mvn clean compile

To bring up Spring Boot:

	mvn spring-boot:run

It will bring up web server at:

	http://localhost:8080

Invoke the sample endpoints:

	http://localhost:8080/hello?name=Smeg
	http://localhost:8080/greeting?name=SmeggyJSON
