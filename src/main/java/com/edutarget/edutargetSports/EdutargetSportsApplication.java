package com.edutarget.edutargetSports;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EntityScan(basePackages = "com.edutarget.edutargetSports.entity")
@EnableScheduling
public class EdutargetSportsApplication {

	public static void main(String[] args) {
		SpringApplication.run(EdutargetSportsApplication.class, args);
	}

}
