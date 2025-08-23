package com.stemlen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableMongoRepositories(basePackages = "com.stemlen.repository")
public class StemlenApplication {

    public static void main(String[] args) {
        SpringApplication.run(StemlenApplication.class, args);
    }
}
