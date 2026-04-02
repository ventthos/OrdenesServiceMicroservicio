package com.ordenes.ordenservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OrdenServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrdenServiceApplication.class, args);
    }

}
