package com.edutarget.edutargetSports;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.edutarget.edutargetSports.entity")
public class EdutargetSportsApplication {

	public static void main(String[] args) {
		SpringApplication.run(EdutargetSportsApplication.class, args);
	}

}
