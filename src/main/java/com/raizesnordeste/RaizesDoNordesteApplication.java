package com.raizesnordeste;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class RaizesDoNordesteApplication {

    public static void main(String[] args) {
        SpringApplication.run(RaizesDoNordesteApplication.class, args);
    }
}
