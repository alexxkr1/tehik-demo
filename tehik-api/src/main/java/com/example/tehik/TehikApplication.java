package com.example.tehik;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class TehikApplication {

	public static void main(String[] args) {
		SpringApplication.run(TehikApplication.class, args);
	}

}
