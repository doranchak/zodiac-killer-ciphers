# zodiac-killer-ciphers

Much of this code is throwaway experimental code, but I'm trying to carve out all the useful reusable stuff into Web services that can be easily invoked to perform tasks related to cryptanalysis.

To build:

	mvn clean compile

To bring up Spring Boot:

	mvn spring-boot:run

It will bring up web server at:

	http://localhost:8080

Invoke the sample endpoint:

	http://localhost:8080/hello?name=Smeg

