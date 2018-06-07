package com.chiba;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;

@SpringBootApplication
@ComponentScan(basePackages = {"com.chiba.bean","com.chiba.config","com.chiba.controller","com.chiba.dao","com.chiba.domain","com.chiba.service"})
public class TeamsysApplication {

	public static void main(String[] args) {
		SpringApplication.run(TeamsysApplication.class, args);
	}
}
