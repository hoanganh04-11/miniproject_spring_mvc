package com.smarthome.iot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


// include >< exclude
//@SpringBootApplication
@SpringBootApplication(exclude = org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class)
public class IotApplication {

	public static void main(String[] args) {
		SpringApplication.run(IotApplication.class, args);
	}

}
