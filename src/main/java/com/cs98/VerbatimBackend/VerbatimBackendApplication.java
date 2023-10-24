package com.cs98.VerbatimBackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class VerbatimBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(VerbatimBackendApplication.class, args);
	}

}
