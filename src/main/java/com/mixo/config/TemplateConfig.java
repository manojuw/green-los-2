//package com.mixo.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.thymeleaf.TemplateEngine;
//import org.thymeleaf.spring5.SpringTemplateEngine;
//import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
//
//@Configuration
//public class TemplateConfig {
//
//	@Bean
//	TemplateEngine templateEngine() {
//		SpringTemplateEngine templateEngine = new SpringTemplateEngine();
//		ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
//		resolver.setPrefix("templates/");
//		resolver.setSuffix(".html");
//		resolver.setTemplateMode("HTML");
//		resolver.setCharacterEncoding("UTF-8");
//		templateEngine.setTemplateResolver(resolver);
//		return templateEngine;
//	}
//}
