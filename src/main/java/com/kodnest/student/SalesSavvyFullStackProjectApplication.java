package com.kodnest.student;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.servlet.context.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class SalesSavvyFullStackProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalesSavvyFullStackProjectApplication.class, args);
	}

}
