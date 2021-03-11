package br.com.cafe;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration // Define como classe de configuração
public class Config implements WebMvcConfigurer { // Implementa o configurar Web Mvc
	@Override // Sobreescreve o método
	public void addViewControllers(ViewControllerRegistry registry) { // Método para adicionar views
		registry.addViewController("/").setViewName("forward:/index.html"); // Seta a raiz do site como index.html
	}
}
