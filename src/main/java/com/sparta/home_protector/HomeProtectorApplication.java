package com.sparta.home_protector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class HomeProtectorApplication {

	public static void main(String[] args) {
		SpringApplication.run(HomeProtectorApplication.class, args);
	}

}
