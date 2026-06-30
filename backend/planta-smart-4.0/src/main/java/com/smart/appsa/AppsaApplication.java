package com.smart.appsa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class AppsaApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppsaApplication.class, args);
	}

}
