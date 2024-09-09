package com.finance.stockhub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@SpringBootApplication
public class StockHubApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockHubApplication.class, args);
    }

}
