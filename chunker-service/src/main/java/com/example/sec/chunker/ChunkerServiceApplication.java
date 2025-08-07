package com.example.sec.chunker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class ChunkerServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ChunkerServiceApplication.class, args);
    }
}
