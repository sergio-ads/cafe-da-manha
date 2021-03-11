package br.com.cafe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication // Define como uma aplicação Spring
public class ServletInitializer extends SpringBootServletInitializer { // Classe principal do Spring

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(ServletInitializer.class);
	}

	public static void main(String[] args) { 
		SpringApplication.run(ServletInitializer.class, args);
	}
	
}
