package com.student_gradebook.auth_server;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class AuthServerApplication {

	@PostConstruct
	public void init() {
		TimeZone.setDefault(TimeZone.getTimeZone("Europe/Warsaw"));
	}

	public static void main(String[] args) {
		SpringApplication.run(AuthServerApplication.class, args);
	}

}
