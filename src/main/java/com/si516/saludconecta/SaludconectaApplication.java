package com.si516.saludconecta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SaludconectaApplication {

	public static void main(String[] args) {
		SpringApplication.run(SaludconectaApplication.class, args);
	}

}
