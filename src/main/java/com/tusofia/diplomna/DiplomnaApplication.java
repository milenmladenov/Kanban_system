package com.tusofia.diplomna;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;



@ComponentScan("com.tusofia.diplomna")
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class DiplomnaApplication {

	public static void main(String[] args) {
		SpringApplication.run(DiplomnaApplication.class, args);
	}

}
