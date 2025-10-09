package com.kafka.producer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        //Dotenv.configure().load().entries().forEach(e -> System.setProperty(e.getKey(), e.getValue()));
        SpringApplication.run(Application.class, args);
    }

}
