package com.wfc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.AbstractEnvironment;

@SpringBootApplication
public class BootLuncher {
	public static void main(String[] args) {
		// -Dspring.profiles.active=dev
		System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME,"dev");
		SpringApplication.run(BootLuncher.class, args);
	}
	

}
