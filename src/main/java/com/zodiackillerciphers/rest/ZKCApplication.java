package com.zodiackillerciphers.rest;

import java.util.concurrent.atomic.AtomicLong;

import org.apache.catalina.servlets.DefaultServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.restservice.Greeting;

@SpringBootApplication
@RestController
public class ZKCApplication {
	private static final String template = "Hello, %s!";
	private final AtomicLong counter = new AtomicLong();

	public static void main(String[] args) {
		SpringApplication.run(ZKCApplication.class, args);
	}

	@Bean
	public ServletRegistrationBean servletRegistrationBean() {
		final DefaultServlet servlet = new DefaultServlet();
		final ServletRegistrationBean bean = new ServletRegistrationBean(servlet, "/*");
		bean.addInitParameter("listings", "true");
		bean.setLoadOnStartup(1);
		return bean;
	}

	@GetMapping("/hello")
	public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
		return String.format("Hello %s!", name);
	}

	@GetMapping("/greeting")
	public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
		return new Greeting(counter.incrementAndGet(), String.format(template, name));
	}

}
