package com.maciek.home.applicationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class ApplicationServiceApplication {

    public static void main(String[] args) {
//        SpringApplication.run(ApplicationServiceApplication.class, args);
        ApplicationContext ctx = SpringApplication.run(ApplicationServiceApplication.class, args);

    }

}
