package com.slb.apigee.xchange;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@ComponentScan("com.slb")
@SpringBootApplication
public class XchangeServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(XchangeServiceApplication.class, args);
	}

}