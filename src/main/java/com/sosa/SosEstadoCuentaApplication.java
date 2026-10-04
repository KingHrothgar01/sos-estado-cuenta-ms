package com.sosa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.sosa")
public class SosEstadoCuentaApplication {

	public static void main(String[] args) {
		SpringApplication.run(SosEstadoCuentaApplication.class, args);
	}

}
