package com.jiangdaxian.base;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ComponentScan(basePackages = { "com.jiangdaxian" })
@ImportResource({ "classpath:applicationContext.xml" })
public class WebApplication {
	public static void main(String[] args) throws Exception {
		SpringApplication.run(WebApplication.class, args);
	}
}
